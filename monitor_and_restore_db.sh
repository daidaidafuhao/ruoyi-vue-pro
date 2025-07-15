#!/bin/bash

# =============================================================================
# 数据库监控和自动恢复脚本
# 功能：监控 ruoyi-vue-pro 数据库是否存在，不存在时自动恢复最新备份
# 作者：System Auto-Generated
# =============================================================================

# 配置变量
MYSQL_CONTAINER="ruoyi-mysql-dev"
MYSQL_USER="root"
MYSQL_PASSWORD="123456"
DATABASE="ruoyi-vue-pro"
BACKUP_DIR="sql/mysql/data"
LOG_FILE="db_monitor.log"
CHECK_INTERVAL=30  # 检查间隔（秒）

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log() {
    echo -e "${GREEN}[$(date '+%Y-%m-%d %H:%M:%S')] $1${NC}" | tee -a "$LOG_FILE"
}

error() {
    echo -e "${RED}[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}" | tee -a "$LOG_FILE"
}

warn() {
    echo -e "${YELLOW}[$(date '+%Y-%m-%d %H:%M:%S')] WARNING: $1${NC}" | tee -a "$LOG_FILE"
}

info() {
    echo -e "${BLUE}[$(date '+%Y-%m-%d %H:%M:%S')] INFO: $1${NC}" | tee -a "$LOG_FILE"
}

# 检查Docker容器是否运行
check_container() {
    if ! sudo docker ps | grep -q "$MYSQL_CONTAINER"; then
        error "MySQL container ($MYSQL_CONTAINER) is not running!"
        return 1
    fi
    return 0
}

# 检查数据库是否存在
check_database_exists() {
    local result
    result=$(sudo docker exec "$MYSQL_CONTAINER" mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "SHOW DATABASES LIKE '$DATABASE';" 2>/dev/null | grep -v "Database" | wc -l)
    
    if [ "$result" -eq 0 ]; then
        return 1  # 数据库不存在
    else
        return 0  # 数据库存在
    fi
}

# 检查数据库是否有数据
check_database_has_data() {
    local table_count
    table_count=$(sudo docker exec "$MYSQL_CONTAINER" mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -D "$DATABASE" -e "SHOW TABLES;" 2>/dev/null | wc -l)
    
    if [ "$table_count" -le 1 ]; then
        return 1  # 没有数据（只有表头）
    else
        return 0  # 有数据
    fi
}

# 获取最新的备份文件
get_latest_backup() {
    if [ ! -d "$BACKUP_DIR" ]; then
        error "Backup directory ($BACKUP_DIR) does not exist!"
        return 1
    fi
    
    local latest_backup
    latest_backup=$(ls -t "$BACKUP_DIR"/ruoyi-vue-pro-backup-*.sql 2>/dev/null | head -1)
    
    if [ -z "$latest_backup" ]; then
        error "No backup files found in $BACKUP_DIR"
        return 1
    fi
    
    echo "$latest_backup"
    return 0
}

# 创建数据库
create_database() {
    info "Creating database $DATABASE..."
    sudo docker exec "$MYSQL_CONTAINER" mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "CREATE DATABASE IF NOT EXISTS \`$DATABASE\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null
    
    if [ $? -eq 0 ]; then
        log "Database $DATABASE created successfully"
        return 0
    else
        error "Failed to create database $DATABASE"
        return 1
    fi
}

# 导入备份数据
import_backup() {
    local backup_file="$1"
    
    if [ ! -f "$backup_file" ]; then
        error "Backup file not found: $backup_file"
        return 1
    fi
    
    info "Importing backup from: $backup_file"
    info "File size: $(ls -lh "$backup_file" | awk '{print $5}')"
    
    # 导入数据
    sudo docker exec -i "$MYSQL_CONTAINER" mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$DATABASE" < "$backup_file" 2>/dev/null
    
    if [ $? -eq 0 ]; then
        log "Successfully imported backup: $(basename "$backup_file")"
        return 0
    else
        error "Failed to import backup: $backup_file"
        return 1
    fi
}

# 数据库恢复流程
restore_database() {
    log "Starting database restoration process..."
    
    # 获取最新备份文件
    local backup_file
    backup_file=$(get_latest_backup)
    if [ $? -ne 0 ]; then
        return 1
    fi
    
    # 创建数据库
    if ! create_database; then
        return 1
    fi
    
    # 导入备份
    if import_backup "$backup_file"; then
        log "Database restoration completed successfully!"
        return 0
    else
        error "Database restoration failed!"
        return 1
    fi
}

# 监控函数
monitor_database() {
    log "Database monitoring started. Check interval: ${CHECK_INTERVAL}s"
    log "Monitoring database: $DATABASE in container: $MYSQL_CONTAINER"
    
    while true; do
        # 检查容器是否运行
        if ! check_container; then
            warn "Container not running, waiting..."
            sleep "$CHECK_INTERVAL"
            continue
        fi
        
        # 检查数据库是否存在
        if ! check_database_exists; then
            warn "Database $DATABASE does not exist!"
            info "Attempting to restore database..."
            
            if restore_database; then
                log "Database $DATABASE restored successfully!"
            else
                error "Failed to restore database $DATABASE"
            fi
        else
            # 数据库存在，检查是否有数据
            if ! check_database_has_data; then
                warn "Database $DATABASE exists but appears empty!"
                info "Attempting to restore data..."
                
                if restore_database; then
                    log "Database $DATABASE data restored successfully!"
                else
                    error "Failed to restore data to database $DATABASE"
                fi
            else
                info "Database $DATABASE is healthy ($(sudo docker exec "$MYSQL_CONTAINER" mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -D "$DATABASE" -e "SHOW TABLES;" 2>/dev/null | wc -l) tables)"
            fi
        fi
        
        sleep "$CHECK_INTERVAL"
    done
}

# 显示帮助信息
show_help() {
    echo "数据库监控和自动恢复脚本"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -h, --help              显示帮助信息"
    echo "  -c, --check             检查数据库状态（一次性）"
    echo "  -r, --restore           立即恢复数据库"
    echo "  -m, --monitor           启动监控模式（默认）"
    echo "  -i, --interval SECONDS  设置检查间隔（默认30秒）"
    echo ""
    echo "示例:"
    echo "  $0                      # 启动监控模式"
    echo "  $0 -c                   # 检查数据库状态"
    echo "  $0 -r                   # 立即恢复数据库"
    echo "  $0 -m -i 60             # 启动监控，60秒检查一次"
}

# 一次性检查
check_once() {
    log "Performing one-time database check..."
    
    if ! check_container; then
        exit 1
    fi
    
    if check_database_exists; then
        if check_database_has_data; then
            log "Database $DATABASE exists and has data"
            info "Table count: $(sudo docker exec "$MYSQL_CONTAINER" mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -D "$DATABASE" -e "SHOW TABLES;" 2>/dev/null | wc -l)"
        else
            warn "Database $DATABASE exists but appears empty"
        fi
    else
        warn "Database $DATABASE does not exist"
    fi
}

# 主函数
main() {
    # 解析命令行参数
    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                show_help
                exit 0
                ;;
            -c|--check)
                check_once
                exit 0
                ;;
            -r|--restore)
                restore_database
                exit $?
                ;;
            -m|--monitor)
                monitor_database
                exit 0
                ;;
            -i|--interval)
                CHECK_INTERVAL="$2"
                shift
                ;;
            *)
                echo "未知选项: $1"
                show_help
                exit 1
                ;;
        esac
        shift
    done
    
    # 默认启动监控模式
    monitor_database
}

# 信号处理
trap 'log "Monitoring stopped by user"; exit 0' SIGINT SIGTERM

# 启动脚本
log "=== Database Monitor Script Started ==="
log "Target database: $DATABASE"
log "MySQL container: $MYSQL_CONTAINER"
log "Backup directory: $BACKUP_DIR"

main "$@" 