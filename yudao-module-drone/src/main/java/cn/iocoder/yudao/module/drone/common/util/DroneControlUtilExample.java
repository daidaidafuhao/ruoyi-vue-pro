package com.template.utils;

import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;

/**
 * 无人机柜控制工具类使用示例
 * 展示如何使用参数化API控制不同的无人机和货柜
 * 
 * @author 系统管理员
 * @since 2025-01-20
 */
@Slf4j
public class DroneControlUtilExample {
    
    public static void main(String[] args) {
        DroneControlUtil droneControl = new DroneControlUtil();
        
        try {
            // 示例1：使用默认参数的基本连接测试
            basicConnectionExample(droneControl);
            
            // 示例2：控制多个不同的无人机和货柜
            // multiDeviceExample(droneControl);
            
            // 示例3：自定义参数的完整配送流程
            // customParametersExample(droneControl);
            
            // 示例4：工具方法使用示例
            utilityMethodsExample();
            
        } catch (Exception e) {
            log.error("示例执行失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 基本连接测试示例（使用默认参数）
     */
    public static void basicConnectionExample(DroneControlUtil droneControl) throws Exception {
        log.info("=== 基本连接测试示例（默认参数） ===");
        
        // 使用默认参数连接
        Socket droneSocket = droneControl.connectDrone();
        log.info("无人机连接成功: {}", droneSocket.getRemoteSocketAddress());
        
        ModbusMaster containerMaster = droneControl.connectContainer();
        log.info("无人机柜连接成功");
        
        // 使用默认参数测试基本命令
        droneControl.setContainerAutoMode(containerMaster);
        droneControl.checkStorageStatus(containerMaster);
        
        // 关闭连接
        droneControl.closeConnection(droneSocket);
        droneControl.closeConnection(containerMaster);
        
        log.info("基本连接测试完成");
    }
    
    /**
     * 多设备控制示例
     */
    public static void multiDeviceExample(DroneControlUtil droneControl) throws Exception {
        log.info("=== 多设备控制示例 ===");
        
        // 连接第一套设备（默认参数）
        Socket drone1 = droneControl.connectDrone("sk.yunenjoy.cn", 61473);
        ModbusMaster container1 = droneControl.connectContainer("172.22.33.253", 502);
        
        // 连接第二套设备（不同参数）
        Socket drone2 = droneControl.connectDrone("192.168.1.100", 61474);
        ModbusMaster container2 = droneControl.connectContainer("192.168.1.101", 502);
        
        // 连接第三套设备（不同参数）
        Socket drone3 = droneControl.connectDrone("192.168.1.102", 61475);
        ModbusMaster container3 = droneControl.connectContainer("192.168.1.103", 502);
        
        // 设置不同的设备参数
        // 设备1：系统ID=1，从站ID=1
        droneControl.setMissionCount(drone1, 5, 1, 1, 1, 1);
        droneControl.setContainerAutoMode(container1, 1, 0xBCC, 0xBCD);
        
        // 设备2：系统ID=2，从站ID=2
        droneControl.setMissionCount(drone2, 5, 2, 1, 2, 1);
        droneControl.setContainerAutoMode(container2, 2, 0xBCC, 0xBCD);
        
        // 设备3：系统ID=3，从站ID=3
        droneControl.setMissionCount(drone3, 5, 3, 1, 3, 1);
        droneControl.setContainerAutoMode(container3, 3, 0xBCC, 0xBCD);
        
        log.info("多设备控制示例完成");
        
        // 清理连接
        droneControl.closeConnection(drone1);
        droneControl.closeConnection(drone2);
        droneControl.closeConnection(drone3);
        droneControl.closeConnection(container1);
        droneControl.closeConnection(container2);
        droneControl.closeConnection(container3);
    }
    
    /**
     * 自定义参数的完整配送流程示例
     */
    public static void customParametersExample(DroneControlUtil droneControl) throws Exception {
        log.info("=== 自定义参数配送流程示例 ===");
        
        // 自定义设备参数
        String droneHost = "192.168.100.50";
        int dronePort = 61473;
        String containerHost = "192.168.100.51";
        int containerPort = 502;
        
        // MAVLink参数
        int droneSystemId = 5;
        int droneCompId = 1;
        int targetSysId = 5;
        int targetCompId = 1;
        
        // Modbus参数
        int containerSlaveId = 3;
        int doorAddr = 0xBB8;
        int parkingAddr = 0xBB9;
        int storageAddr = 0xBBA;
        int servoAddr = 0xBBB;
        
        // 连接设备
        Socket droneSocket = droneControl.connectDrone(droneHost, dronePort);
        ModbusMaster containerMaster = droneControl.connectContainer(containerHost, containerPort);
        
        // 设置航线任务（使用自定义参数）
        droneControl.setMissionCount(droneSocket, 6, droneSystemId, droneCompId, targetSysId, targetCompId);
        droneControl.setSpeed(droneSocket, 1, 15, droneSystemId, droneCompId, targetSysId, targetCompId);
        
        // 设置起飞点
        int takeoffLat = DroneControlUtil.degreesToMavlinkInt(28.7904567);
        int takeoffLon = DroneControlUtil.degreesToMavlinkInt(115.3875000);
        droneControl.setTakeoff(droneSocket, takeoffLat, takeoffLon, 50, 0, 
                               droneSystemId, droneCompId, targetSysId, targetCompId);
        
        // 设置目的地
        int destLat = DroneControlUtil.degreesToMavlinkInt(28.7911779);
        int destLon = DroneControlUtil.degreesToMavlinkInt(115.3857110);
        droneControl.setLanding(droneSocket, destLat, destLon, 0, 5, true,
                               droneSystemId, droneCompId, targetSysId, targetCompId);
        
        // 下载航线
        droneControl.downloadMission(droneSocket, droneSystemId, droneCompId, targetSysId, targetCompId);
        droneControl.acknowledgeMission(droneSocket, droneSystemId, droneCompId, targetSysId, targetCompId);
        
        // 无人机柜操作（使用自定义参数）
        droneControl.setContainerAutoMode(containerMaster, containerSlaveId, 0xBCC, 0xBCD);
        droneControl.openDoor(containerMaster, containerSlaveId, doorAddr);
        
        // 解锁并起飞
        droneControl.armDrone(droneSocket, droneSystemId, droneCompId, targetSysId, targetCompId);
        DroneControlUtil.waitFor(5000);
        droneControl.takeoffNow(droneSocket, 50, droneSystemId, droneCompId, targetSysId, targetCompId);
        
        log.info("自定义参数配送流程示例启动完成");
        
        // 清理连接
        droneControl.closeConnection(droneSocket);
        droneControl.closeConnection(containerMaster);
    }
    
    /**
     * 并行控制多个设备示例
     */
    public static void parallelControlExample(DroneControlUtil droneControl) throws Exception {
        log.info("=== 并行控制多个设备示例 ===");
        
        // 准备多个设备的参数
        String[] droneHosts = {"192.168.1.10", "192.168.1.11", "192.168.1.12"};
        String[] containerHosts = {"192.168.1.20", "192.168.1.21", "192.168.1.22"};
        int[] systemIds = {1, 2, 3};
        int[] slaveIds = {1, 2, 3};
        
        // 并行连接多个设备
        for (int i = 0; i < droneHosts.length; i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    Socket drone = droneControl.connectDrone(droneHosts[index], 61473);
                    ModbusMaster container = droneControl.connectContainer(containerHosts[index], 502);
                    
                    // 同时控制多个设备
                    droneControl.setMissionCount(drone, 5, systemIds[index], 1, systemIds[index], 1);
                    droneControl.setContainerAutoMode(container, slaveIds[index], 0xBCC, 0xBCD);
                    
                    log.info("设备 {} 控制完成", index + 1);
                    
                    droneControl.closeConnection(drone);
                    droneControl.closeConnection(container);
                } catch (Exception e) {
                    log.error("设备 {} 控制失败: {}", index + 1, e.getMessage());
                }
            }).start();
        }
        
        // 等待所有线程完成
        Thread.sleep(10000);
        log.info("并行控制示例完成");
    }
    
    /**
     * 分步骤操作示例（使用自定义参数）
     */
    public static void stepByStepCustomExample(DroneControlUtil droneControl) throws Exception {
        log.info("=== 分步骤操作示例（自定义参数） ===");
        
        // 设备参数配置
        String droneHost = "sk.yunenjoy.cn";
        int dronePort = 61473;
        String containerHost = "172.22.33.253";
        int containerPort = 502;
        
        // 自定义MAVLink参数（针对特定无人机）
        int sysId = 2;  // 无人机系统ID
        int compId = 1; // 组件ID
        int targetSys = 2; // 目标系统ID
        int targetComp = 1; // 目标组件ID
        
        // 自定义Modbus参数（针对特定货柜）
        int slaveId = 1; // 从站ID
        int doorAddr = 0xBB8; // 舱门地址
        int parkingAddr = 0xBB9; // 停机坪地址
        int storageAddr = 0xBBA; // 存件地址
        int servoAddr = 0xBBB; // 舵机地址
        
        Socket droneSocket = droneControl.connectDrone(droneHost, dronePort);
        ModbusMaster containerMaster = droneControl.connectContainer(containerHost, containerPort);
        
        // 1. 无人机柜准备（使用自定义参数）
        log.info("Step 1: 准备无人机柜（设备ID: {}）", slaveId);
        droneControl.setContainerAutoMode(containerMaster, slaveId, 0xBCC, 0xBCD);
        droneControl.checkStorageStatus(containerMaster, slaveId, 0xBBE);
        droneControl.openDoor(containerMaster, slaveId, doorAddr);
        
        // 2. 无人机航线设置（使用自定义参数）
        log.info("Step 2: 设置无人机航线（系统ID: {}）", sysId);
        droneControl.setMissionCount(droneSocket, 6, sysId, compId, targetSys, targetComp);
        droneControl.setSpeed(droneSocket, 1, 15, sysId, compId, targetSys, targetComp);
        
        int lat = DroneControlUtil.degreesToMavlinkInt(28.7904567);
        int lon = DroneControlUtil.degreesToMavlinkInt(115.3875000);
        
        droneControl.setTakeoff(droneSocket, lat, lon, 50, 0, sysId, compId, targetSys, targetComp);
        droneControl.setWaypoint(droneSocket, lat, lon, 100, 2, 0.0f, sysId, compId, targetSys, targetComp);
        droneControl.setLanding(droneSocket, lat, lon, 0, 4, true, sysId, compId, targetSys, targetComp);
        
        // 3. 下载航线
        log.info("Step 3: 下载并确认航线");
        droneControl.downloadMission(droneSocket, sysId, compId, targetSys, targetComp);
        for (int i = 0; i < 6; i++) {
            droneControl.requestMissionItem(droneSocket, i, sysId, compId, targetSys, targetComp);
        }
        droneControl.acknowledgeMission(droneSocket, sysId, compId, targetSys, targetComp);
        
        // 4. 无人机起飞
        log.info("Step 4: 无人机起飞");
        droneControl.armDrone(droneSocket, sysId, compId, targetSys, targetComp);
        DroneControlUtil.waitFor(5000);
        droneControl.takeoffNow(droneSocket, 50, sysId, compId, targetSys, targetComp);
        droneControl.setFlightMode(droneSocket, 4, 4, sysId, compId, targetSys, targetComp);
        
        // 5. 模拟等待降落
        log.info("Step 5: 等待无人机降落");
        // 实际使用中需要监控无人机状态
        
        // 6. 存件操作（使用自定义参数）
        log.info("Step 6: 执行存件操作");
        droneControl.confirmDroneOnPad(containerMaster, slaveId, parkingAddr);
        int[] code = DroneControlUtil.generateDefaultPickupCode();
        droneControl.startStorageOperation(containerMaster, code[0], code[1], slaveId, storageAddr, 0xBC0, 0xBC1);
        
        // 7. 机械臂操作
        log.info("Step 7: 机械臂操作");
        droneControl.waitForServoStatus(containerMaster, 1, 30000, slaveId, servoAddr);
        droneControl.controlServo(droneSocket, DroneControlUtil.SERVO_OPEN, sysId, compId, targetSys, targetComp);
        droneControl.setServoStatus(containerMaster, 10, 11, "开舵机", slaveId, servoAddr);
        
        // 8. 完成操作
        log.info("Step 8: 完成存件并关闭");
        int result = droneControl.waitForStorageComplete(containerMaster, slaveId, storageAddr);
        if (result == 111) {
            droneControl.controlServo(droneSocket, DroneControlUtil.SERVO_CLOSE, sysId, compId, targetSys, targetComp);
        }
        droneControl.setServoStatus(containerMaster, 20, 21, "关舵机", slaveId, servoAddr);
        droneControl.closeDoor(containerMaster, slaveId, doorAddr);
        
        // 清理连接
        droneControl.closeConnection(droneSocket);
        droneControl.closeConnection(containerMaster);
        
        log.info("分步骤操作示例完成");
    }
    
    /**
     * 工具方法使用示例
     */
    public static void utilityMethodsExample() {
        log.info("=== 工具方法使用示例 ===");
        
        // 坐标转换
        double lat = 28.7904567;
        double lon = 115.3875000;
        int mavlinkLat = DroneControlUtil.degreesToMavlinkInt(lat);
        int mavlinkLon = DroneControlUtil.degreesToMavlinkInt(lon);
        
        log.info("原始坐标: ({}, {})", lat, lon);
        log.info("MAVLink坐标: ({}, {})", mavlinkLat, mavlinkLon);
        log.info("转换回来: ({}, {})", 
                DroneControlUtil.mavlinkIntToDegrees(mavlinkLat),
                DroneControlUtil.mavlinkIntToDegrees(mavlinkLon));
        
        // 坐标验证
        boolean valid = DroneControlUtil.isValidCoordinate(lat, lon);
        log.info("坐标有效性: {}", valid);
        
        // 状态检查
        boolean ready = DroneControlUtil.isDroneReadyForLanding(1, 81);
        log.info("无人机准备降落: {}", ready);
        
        // GPS状态
        boolean rtkFixed = DroneControlUtil.isGpsRtkFixed(6);
        log.info("GPS RTK固定: {}", rtkFixed);
        
        // 状态描述
        String statusDesc = DroneControlUtil.getSystemStatusDescription(1, 81, 6);
        log.info("系统状态: {}", statusDesc);
        
        // 生成取件码
        int[] pickupCode = DroneControlUtil.generateDefaultPickupCode();
        log.info("默认取件码: {}{}", pickupCode[0], pickupCode[1]);
        
        log.info("工具方法示例完成");
    }
    
    /**
     * 参数配置示例 - 展示如何为不同设备配置参数
     */
    public static void configurationExample() {
        log.info("=== 参数配置示例 ===");
        
        // 场景1: 校园配送系统
        log.info("--- 校园配送系统配置 ---");
        String campusDroneHost = "192.168.100.10";
        int campusDronePort = 61473;
        String campusContainerHost = "192.168.100.20";
        int campusSystemId = 1;
        int campusSlaveId = 1;
        log.info("校园系统 - 无人机: {}:{}, 货柜: {}, 系统ID: {}, 从站ID: {}", 
                campusDroneHost, campusDronePort, campusContainerHost, campusSystemId, campusSlaveId);
        
        // 场景2: 工业园区配送系统
        log.info("--- 工业园区配送系统配置 ---");
        String industrialDroneHost = "10.10.1.100";
        int industrialDronePort = 61474;
        String industrialContainerHost = "10.10.1.200";
        int industrialSystemId = 2;
        int industrialSlaveId = 2;
        log.info("工业园区 - 无人机: {}:{}, 货柜: {}, 系统ID: {}, 从站ID: {}", 
                industrialDroneHost, industrialDronePort, industrialContainerHost, industrialSystemId, industrialSlaveId);
        
        // 场景3: 测试环境
        log.info("--- 测试环境配置 ---");
        String testDroneHost = DroneControlUtil.DEFAULT_DRONE_HOST;
        int testDronePort = DroneControlUtil.DEFAULT_DRONE_PORT;
        String testContainerHost = DroneControlUtil.DEFAULT_CONTAINER_HOST;
        int testSystemId = DroneControlUtil.DEFAULT_MAVLINK_SYSTEM_ID;
        int testSlaveId = DroneControlUtil.DEFAULT_MODBUS_SLAVE_ID;
        log.info("测试环境 - 无人机: {}:{}, 货柜: {}, 系统ID: {}, 从站ID: {}", 
                testDroneHost, testDronePort, testContainerHost, testSystemId, testSlaveId);
        
        log.info("参数配置示例完成");
    }
} 