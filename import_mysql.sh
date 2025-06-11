#!/bin/bash

# 设置变量
MYSQL_CONTAINER="ruoyi-mysql-dev"
MYSQL_USER="root"
MYSQL_PASSWORD="123456"
DATABASE="ruoyi-vue-pro"
SQL_DIR="sql/mysql"

# 日志文件
LOG_FILE="mysql_import.log"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 日志函数
log() {
    echo -e "${GREEN}[$(date '+%Y-%m-%d %H:%M:%S')] $1${NC}" | tee -a $LOG_FILE
}

error() {
    echo -e "${RED}[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}" | tee -a $LOG_FILE
}

warn() {
    echo -e "${YELLOW}[$(date '+%Y-%m-%d %H:%M:%S')] WARNING: $1${NC}" | tee -a $LOG_FILE
}

# 检查Docker容器是否运行
check_container() {
    if ! docker ps | grep -q $MYSQL_CONTAINER; then
        error "MySQL container ($MYSQL_CONTAINER) is not running!"
        exit 1
    fi
}

# 创建数据库
create_database() {
    log "Creating database $DATABASE if not exists..."
    docker exec -i $MYSQL_CONTAINER mysql -u$MYSQL_USER -p$MYSQL_PASSWORD -e "CREATE DATABASE IF NOT EXISTS \`$DATABASE\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    if [ $? -ne 0 ]; then
        error "Failed to create database!"
        exit 1
    fi
}

# 导入SQL文件
import_sql_file() {
    local file=$1
    log "Importing $file..."
    
    if [ ! -f "$file" ]; then
        error "SQL file not found: $file"
        return 1
    fi
    
    docker exec -i $MYSQL_CONTAINER mysql -u$MYSQL_USER -p$MYSQL_PASSWORD $DATABASE < "$file"
    if [ $? -ne 0 ]; then
        error "Failed to import $file"
        return 1
    fi
    
    log "Successfully imported $file"
    return 0
}

# 主函数
main() {
    log "Starting MySQL import process..."
    
    # 检查容器
    check_container
    
    # 创建数据库
    create_database
    
    # 定义导入顺序
    declare -a sql_files=(
        "$SQL_DIR/ruoyi-vue-pro.sql"
        "$SQL_DIR/quartz.sql"
        "$SQL_DIR/mall-2024-10-05.sql"
        "$SQL_DIR/member-2024-01-18.sql"
        "$SQL_DIR/pay-2024-08.sql"
        "$SQL_DIR/drone_cabinet.sql"
    )
    
    # 导入所有SQL文件
    for file in "${sql_files[@]}"; do
        import_sql_file "$file"
        if [ $? -ne 0 ]; then
            warn "Continuing with next file..."
        fi
    done
    
    # 验证导入
    log "Verifying database tables..."
    docker exec -i $MYSQL_CONTAINER mysql -u$MYSQL_USER -p$MYSQL_PASSWORD -e "USE \`$DATABASE\`; SHOW TABLES;" | tee -a $LOG_FILE
    
    log "Import process completed!"
}

# 执行主函数
main 