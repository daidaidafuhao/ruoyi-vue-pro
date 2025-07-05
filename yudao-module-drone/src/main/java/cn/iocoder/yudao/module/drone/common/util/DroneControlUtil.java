package cn.iocoder.yudao.module.drone.common.util;

import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.common.*;
import io.dronefleet.mavlink.util.EnumValue;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.math3.util.Decimal64.NAN;

/**
 * 无人机柜控制工具类
 * 整合了无人机MAVLink控制和无人机柜Modbus控制的所有核心功能
 * 支持多设备控制，所有关键参数都可以自定义传入
 * 
 * @author 系统管理员
 * @since 2025-01-20
 */
@Slf4j
public class DroneControlUtil {
    
    // ============================== 默认配置常量 ==============================
    
    /** 默认无人机服务器地址 */
    public static final String DEFAULT_DRONE_HOST = "sk.yunenjoy.cn";
    
    /** 默认无人机通信端口 */
    public static final int DEFAULT_DRONE_PORT = 61473;
    
    /** 默认无人机柜IP地址 */
    public static final String DEFAULT_CONTAINER_HOST = "172.22.33.253";
    
    /** 默认无人机柜Modbus端口 */
    public static final int DEFAULT_CONTAINER_PORT = 502;
    
    /** 默认MAVLink系统ID */
    public static final int DEFAULT_MAVLINK_SYSTEM_ID = 1;
    
    /** 默认MAVLink组件ID */
    public static final int DEFAULT_MAVLINK_COMPONENT_ID = 1;
    
    /** 默认目标系统ID */
    public static final int DEFAULT_TARGET_SYSTEM = 1;
    
    /** 默认目标组件ID */
    public static final int DEFAULT_TARGET_COMPONENT = 1;
    
    /** 默认Modbus从站ID */
    public static final int DEFAULT_MODBUS_SLAVE_ID = 1;
    
    // ============================== 状态常量 ==============================
    
    /** 无人机在地面状态 */
    public static final int DRONE_STATUS_ON_GROUND = 1;
    
    /** 无人机飞行中状态 */
    public static final int DRONE_STATUS_IN_FLIGHT = 2;
    
    /** 无人机未解锁状态 */
    public static final int DRONE_UNLOCK_STATUS_LOCKED = 81;
    
    /** 无人机已解锁状态 */
    public static final int DRONE_UNLOCK_STATUS_UNLOCKED = 209;
    
    /** GPS固定模式（RTK） */
    public static final int GPS_FIX_TYPE_RTK = 6;
    
    // ============================== 机械臂控制常量 ==============================
    
    /** 夹子关闭PWM值 */
    public static final int SERVO_CLOSE = 800;
    
    /** 夹子打开PWM值 */
    public static final int SERVO_OPEN = 2300;
    
    // ============================== 默认Modbus地址常量 ==============================
    
    /** 舱门控制地址 */
    public static final int DEFAULT_MODBUS_ADDR_DOOR = 0xBB8;
    
    /** 停机坪状态地址 */
    public static final int DEFAULT_MODBUS_ADDR_PARKING = 0xBB9;
    
    /** 存件操作状态地址 */
    public static final int DEFAULT_MODBUS_ADDR_STORAGE = 0xBBA;
    
    /** 舵机控制状态地址 */
    public static final int DEFAULT_MODBUS_ADDR_SERVO = 0xBBB;
    
    /** 模式设置地址 */
    public static final int DEFAULT_MODBUS_ADDR_MODE_SET = 0xBCC;
    
    /** 模式状态地址 */
    public static final int DEFAULT_MODBUS_ADDR_MODE_STATUS = 0xBCD;
    
    /** 存件格口状态地址 */
    public static final int DEFAULT_MODBUS_ADDR_STORAGE_STATUS = 0xBBE;
    
    /** 取件码地址1 */
    public static final int DEFAULT_MODBUS_ADDR_CODE_1 = 0xBC0;
    
    /** 取件码地址2 */
    public static final int DEFAULT_MODBUS_ADDR_CODE_2 = 0xBC1;
    
    // ============================== 连接管理 ==============================
    
    /**
     * 连接无人机（使用默认参数）
     * @return Socket连接对象
     * @throws IOException 连接失败异常
     */
    public Socket connectDrone() throws IOException {
        return connectDrone(DEFAULT_DRONE_HOST, DEFAULT_DRONE_PORT);
    }
    
    /**
     * 连接无人机（指定地址和端口）
     * @param host 无人机地址
     * @param port 无人机端口
     * @return Socket连接对象
     * @throws IOException 连接失败异常
     */
    public Socket connectDrone(String host, int port) throws IOException {
        boolean connected = false;
        int retryCount = 0;
        final int maxRetries = 5;
        
        while (!connected && retryCount < maxRetries) {
            try {
                Socket droneSocket = new Socket(host, port);
                connected = true;
                log.info("无人机连接成功: {}:{}", host, port);
                return droneSocket;
            } catch (IOException e) {
                retryCount++;
                log.error("无人机连接失败，重试第{}次: {}", retryCount, e.getMessage());
                if (retryCount >= maxRetries) {
                    throw new IOException("无人机连接失败，已达到最大重试次数", e);
                }
                try {
                    Thread.sleep(5000); // 等待5秒后重试
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("连接过程被中断", ie);
                }
            }
        }
        throw new IOException("无法连接到无人机");
    }
    
    /**
     * 连接无人机柜（使用默认参数）
     * @return ModbusMaster连接对象
     * @throws Exception 连接失败异常
     */
    public ModbusMaster connectContainer() throws Exception {
        return connectContainer(DEFAULT_CONTAINER_HOST, DEFAULT_CONTAINER_PORT);
    }
    
    /**
     * 连接无人机柜（指定地址和端口）
     * @param host 无人机柜地址
     * @param port 无人机柜端口
     * @return ModbusMaster连接对象
     * @throws Exception 连接失败异常
     */
    public ModbusMaster connectContainer(String host, int port) throws Exception {
        boolean connected = false;
        int retryCount = 0;
        final int maxRetries = 5;
        
        while (!connected && retryCount < maxRetries) {
            try {
                TcpParameters tcpParameters = new TcpParameters();
                InetAddress address = InetAddress.getByName(host);
                tcpParameters.setHost(address);
                tcpParameters.setKeepAlive(true);
                tcpParameters.setPort(port);
                
                ModbusMaster containerMaster = ModbusMasterFactory.createModbusMasterTCP(tcpParameters);
                Modbus.setAutoIncrementTransactionId(true);
                
                if (!containerMaster.isConnected()) {
                    containerMaster.connect();
                }
                
                connected = true;
                log.info("无人机柜连接成功: {}:{}", host, port);
                return containerMaster;
            } catch (Exception e) {
                retryCount++;
                log.error("无人机柜连接失败，重试第{}次: {}", retryCount, e.getMessage());
                if (retryCount >= maxRetries) {
                    throw new Exception("无人机柜连接失败，已达到最大重试次数", e);
                }
                try {
                    Thread.sleep(5000); // 等待5秒后重试
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new Exception("连接过程被中断", ie);
                }
            }
        }
        throw new Exception("无法连接到无人机柜");
    }
    
    /**
     * 关闭连接
     * @param socket 要关闭的Socket
     */
    public void closeConnection(Socket socket) {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
                log.info("无人机连接已关闭");
            } catch (IOException e) {
                log.error("关闭无人机连接失败: {}", e.getMessage());
            }
        }
    }
    
    /**
     * 关闭连接
     * @param master 要关闭的ModbusMaster
     */
    public void closeConnection(ModbusMaster master) {
        if (master != null && master.isConnected()) {
            try {
                master.disconnect();
                log.info("无人机柜连接已关闭");
            } catch (Exception e) {
                log.error("关闭无人机柜连接失败: {}", e.getMessage());
            }
        }
    }
    
    // ============================== MAVLink 无人机控制方法 ==============================
    
    /**
     * 设置任务总数（使用默认参数）
     * @param socket 无人机连接
     * @param count 任务总数
     * @throws IOException 通信异常
     */
    public void setMissionCount(Socket socket, int count) throws IOException {
        setMissionCount(socket, count, DEFAULT_MAVLINK_SYSTEM_ID, DEFAULT_MAVLINK_COMPONENT_ID, 
                       DEFAULT_TARGET_SYSTEM, DEFAULT_TARGET_COMPONENT);
    }
    
    /**
     * 设置任务总数（完整参数）
     * @param socket 无人机连接
     * @param count 任务总数
     * @param sysId 系统ID
     * @param compId 组件ID
     * @param targetSys 目标系统ID
     * @param targetComp 目标组件ID
     * @throws IOException 通信异常
     */
    public void setMissionCount(Socket socket, int count, int sysId, int compId, 
                               int targetSys, int targetComp) throws IOException {
        MavlinkConnection connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
        MissionCount missionCount = MissionCount.builder()
                .targetSystem(targetSys)
                .targetComponent(targetComp)
                .count(count)
                .missionType(MavMissionType.MAV_MISSION_TYPE_MISSION)
                .build();
        connection.send2(sysId, compId, missionCount);
        log.info("设置任务总数: {}, 系统ID: {}, 组件ID: {}", count, sysId, compId);
    }
    
    /**
     * 设置飞行速度（使用默认参数）
     * @param socket 无人机连接
     * @param seq 序列号
     * @param speed 速度值
     * @throws IOException 通信异常
     */
    public void setSpeed(Socket socket, int seq, int speed) throws IOException {
        setSpeed(socket, seq, speed, DEFAULT_MAVLINK_SYSTEM_ID, DEFAULT_MAVLINK_COMPONENT_ID, 
                DEFAULT_TARGET_SYSTEM, DEFAULT_TARGET_COMPONENT);
    }
    
    /**
     * 设置飞行速度（完整参数）
     * @param socket 无人机连接
     * @param seq 序列号
     * @param speed 速度值
     * @param sysId 系统ID
     * @param compId 组件ID
     * @param targetSys 目标系统ID
     * @param targetComp 目标组件ID
     * @throws IOException 通信异常
     */
    public void setSpeed(Socket socket, int seq, int speed, int sysId, int compId, 
                        int targetSys, int targetComp) throws IOException {
        MavlinkConnection connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
        MissionItemInt missionItem = MissionItemInt.builder()
                .param1(0)
                .param2(speed)
                .param3(-1)
                .param4(0)
                .targetSystem(targetSys)
                .targetComponent(targetComp)
                .seq(seq)
                .frame(MavFrame.MAV_FRAME_GLOBAL)
                .command(MavCmd.MAV_CMD_DO_CHANGE_SPEED)
                .current(0)
                .autocontinue(1)
                .missionType(MavMissionType.MAV_MISSION_TYPE_MISSION)
                .build();
        connection.send2(sysId, compId, missionItem);
        log.info("设置飞行速度: {} m/s, 序列: {}, 系统ID: {}", speed, seq, sysId);
    }
    
    /**
     * 设置起飞命令（使用默认参数）
     * @param socket 无人机连接
     * @param latitude 纬度 (需要乘以10^7)
     * @param longitude 经度 (需要乘以10^7)
     * @param altitude 高度 (米)
     * @param seq 序列号
     * @throws IOException 通信异常
     */
    public void setTakeoff(Socket socket, int latitude, int longitude, int altitude, int seq) throws IOException {
        setTakeoff(socket, latitude, longitude, altitude, seq, DEFAULT_MAVLINK_SYSTEM_ID, 
                  DEFAULT_MAVLINK_COMPONENT_ID, DEFAULT_TARGET_SYSTEM, DEFAULT_TARGET_COMPONENT);
    }
    
    /**
     * 设置起飞命令（完整参数）
     * @param socket 无人机连接
     * @param latitude 纬度 (需要乘以10^7)
     * @param longitude 经度 (需要乘以10^7)
     * @param altitude 高度 (米)
     * @param seq 序列号
     * @param sysId 系统ID
     * @param compId 组件ID
     * @param targetSys 目标系统ID
     * @param targetComp 目标组件ID
     * @throws IOException 通信异常
     */
    public void setTakeoff(Socket socket, int latitude, int longitude, int altitude, int seq,
                          int sysId, int compId, int targetSys, int targetComp) throws IOException {
        MavlinkConnection connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
        MissionItemInt missionItem = MissionItemInt.builder()
                .param1(2)
                .param2(0)
                .param3(0)
                .param4(NAN.floatValue())
                .x(latitude)
                .y(longitude)
                .z(altitude)
                .targetSystem(targetSys)
                .targetComponent(targetComp)
                .seq(seq)
                .frame(MavFrame.MAV_FRAME_GLOBAL)
                .command(MavCmd.MAV_CMD_NAV_TAKEOFF)
                .current(0)
                .autocontinue(1)
                .missionType(MavMissionType.MAV_MISSION_TYPE_MISSION)
                .build();
        connection.send2(sysId, compId, missionItem);
        log.info("设置起飞点: 纬度={}, 经度={}, 高度={}m, 序列={}, 系统ID={}", latitude, longitude, altitude, seq, sysId);
    }
    
    /**
     * 设置航点（使用默认参数）
     * @param socket 无人机连接
     * @param latitude 纬度 (需要乘以10^7)
     * @param longitude 经度 (需要乘以10^7)
     * @param altitude 高度 (米)
     * @param seq 序列号
     * @param headingAngle 航向角 (度)
     * @throws IOException 通信异常
     */
    public void setWaypoint(Socket socket, int latitude, int longitude, int altitude, int seq, float headingAngle) throws IOException {
        setWaypoint(socket, latitude, longitude, altitude, seq, headingAngle, DEFAULT_MAVLINK_SYSTEM_ID, 
                   DEFAULT_MAVLINK_COMPONENT_ID, DEFAULT_TARGET_SYSTEM, DEFAULT_TARGET_COMPONENT);
    }
    
    /**
     * 设置航点（完整参数）
     * @param socket 无人机连接
     * @param latitude 纬度 (需要乘以10^7)
     * @param longitude 经度 (需要乘以10^7)
     * @param altitude 高度 (米)
     * @param seq 序列号
     * @param headingAngle 航向角 (度)
     * @param sysId 系统ID
     * @param compId 组件ID
     * @param targetSys 目标系统ID
     * @param targetComp 目标组件ID
     * @throws IOException 通信异常
     */
    public void setWaypoint(Socket socket, int latitude, int longitude, int altitude, int seq, float headingAngle,
                           int sysId, int compId, int targetSys, int targetComp) throws IOException {
        MavlinkConnection connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
        MissionItemInt missionItem = MissionItemInt.builder()
                .param1(0)
                .param2(0)
                .param3(0)
                .param4(headingAngle)
                .x(latitude)
                .y(longitude)
                .z(altitude)
                .targetSystem(targetSys)
                .targetComponent(targetComp)
                .seq(seq)
                .frame(MavFrame.MAV_FRAME_GLOBAL)
                .command(MavCmd.MAV_CMD_NAV_WAYPOINT)
                .current(0)
                .autocontinue(1)
                .missionType(MavMissionType.MAV_MISSION_TYPE_MISSION)
                .build();
        connection.send2(sysId, compId, missionItem);
        log.info("设置航点: 纬度={}, 经度={}, 高度={}m, 航向={}°, 序列={}, 系统ID={}", latitude, longitude, altitude, headingAngle, seq, sysId);
    }
    
    /**
     * 设置降落命令（使用默认参数）
     * @param socket 无人机连接
     * @param latitude 纬度 (需要乘以10^7)
     * @param longitude 经度 (需要乘以10^7)
     * @param altitude 高度 (米)
     * @param seq 序列号
     * @param isFinalLanding 是否最终降落（影响autocontinue参数）
     * @throws IOException 通信异常
     */
    public void setLanding(Socket socket, int latitude, int longitude, int altitude, int seq, boolean isFinalLanding) throws IOException {
        setLanding(socket, latitude, longitude, altitude, seq, isFinalLanding, DEFAULT_MAVLINK_SYSTEM_ID, 
                  DEFAULT_MAVLINK_COMPONENT_ID, DEFAULT_TARGET_SYSTEM, DEFAULT_TARGET_COMPONENT);
    }
    
    /**
     * 设置降落命令（完整参数）
     * @param socket 无人机连接
     * @param latitude 纬度 (需要乘以10^7)
     * @param longitude 经度 (需要乘以10^7)
     * @param altitude 高度 (米)
     * @param seq 序列号
     * @param isFinalLanding 是否最终降落（影响autocontinue参数）
     * @param sysId 系统ID
     * @param compId 组件ID
     * @param targetSys 目标系统ID
     * @param targetComp 目标组件ID
     * @throws IOException 通信异常
     */
    public void setLanding(Socket socket, int latitude, int longitude, int altitude, int seq, boolean isFinalLanding,
                          int sysId, int compId, int targetSys, int targetComp) throws IOException {
        MavlinkConnection connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
        MissionItemInt missionItem = MissionItemInt.builder()
                .param1(0)
                .param2(0)
                .param3(0)
                .param4(NAN.floatValue())
                .x(latitude)
                .y(longitude)
                .z(altitude)
                .targetSystem(targetSys)
                .targetComponent(targetComp)
                .seq(seq)
                .frame(MavFrame.MAV_FRAME_GLOBAL)
                .command(MavCmd.MAV_CMD_NAV_LAND)
                .current(0)
                .autocontinue(isFinalLanding ? 0 : 1)
                .missionType(MavMissionType.MAV_MISSION_TYPE_MISSION)
                .build();
        connection.send2(sysId, compId, missionItem);
        log.info("设置降落点: 纬度={}, 经度={}, 高度={}m, 序列={}, 最终降落={}, 系统ID={}", latitude, longitude, altitude, seq, isFinalLanding, sysId);
    }
    
    /**
     * 解锁无人机（使用默认参数）
     * @param socket 无人机连接
     * @throws IOException 通信异常
     */
    public void armDrone(Socket socket) throws IOException {
        armDrone(socket, DEFAULT_MAVLINK_SYSTEM_ID, DEFAULT_MAVLINK_COMPONENT_ID, 
                DEFAULT_TARGET_SYSTEM, DEFAULT_TARGET_COMPONENT);
    }
    
    /**
     * 解锁无人机（完整参数）
     * @param socket 无人机连接
     * @param sysId 系统ID
     * @param compId 组件ID
     * @param targetSys 目标系统ID
     * @param targetComp 目标组件ID
     * @throws IOException 通信异常
     */
    public void armDrone(Socket socket, int sysId, int compId, int targetSys, int targetComp) throws IOException {
        MavlinkConnection connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
        CommandLong commandLong = CommandLong.builder()
                .param1(1) // 1=解锁，0=加锁
                .param2(0).param3(0).param4(0).param5(0).param6(0).param7(0)
                .command(MavCmd.MAV_CMD_COMPONENT_ARM_DISARM)
                .targetSystem(targetSys)
                .targetComponent(targetComp)
                .confirmation(0)
                .build();
        connection.send2(sysId, compId, commandLong);
        log.info("无人机解锁命令已发送，系统ID: {}", sysId);
    }
    
    /**
     * 加锁无人机（使用默认参数）
     * @param socket 无人机连接
     * @throws IOException 通信异常
     */
    public void disarmDrone(Socket socket) throws IOException {
        disarmDrone(socket, DEFAULT_MAVLINK_SYSTEM_ID, DEFAULT_MAVLINK_COMPONENT_ID, 
                   DEFAULT_TARGET_SYSTEM, DEFAULT_TARGET_COMPONENT);
    }
    
    /**
     * 加锁无人机（完整参数）
     * @param socket 无人机连接
     * @param sysId 系统ID
     * @param compId 组件ID
     * @param targetSys 目标系统ID
     * @param targetComp 目标组件ID
     * @throws IOException 通信异常
     */
    public void disarmDrone(Socket socket, int sysId, int compId, int targetSys, int targetComp) throws IOException {
        MavlinkConnection connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
        CommandLong commandLong = CommandLong.builder()
                .param1(0) // 1=解锁，0=加锁
                .param2(0).param3(0).param4(0).param5(0).param6(0).param7(0)
                .command(MavCmd.MAV_CMD_COMPONENT_ARM_DISARM)
                .targetSystem(targetSys)
                .targetComponent(targetComp)
                .confirmation(0)
                .build();
        connection.send2(sysId, compId, commandLong);
        log.info("无人机加锁命令已发送，系统ID: {}", sysId);
    }
    
    /**
     * 一键起飞（使用默认参数）
     * @param socket 无人机连接
     * @param altitude 起飞高度 (米)
     * @throws IOException 通信异常
     */
    public void takeoffNow(Socket socket, int altitude) throws IOException {
        takeoffNow(socket, altitude, DEFAULT_MAVLINK_SYSTEM_ID, DEFAULT_MAVLINK_COMPONENT_ID, 
                  DEFAULT_TARGET_SYSTEM, DEFAULT_TARGET_COMPONENT);
    }
    
    /**
     * 一键起飞（完整参数）
     * @param socket 无人机连接
     * @param altitude 起飞高度 (米)
     * @param sysId 系统ID
     * @param compId 组件ID
     * @param targetSys 目标系统ID
     * @param targetComp 目标组件ID
     * @throws IOException 通信异常
     */
    public void takeoffNow(Socket socket, int altitude, int sysId, int compId, int targetSys, int targetComp) throws IOException {
        MavlinkConnection connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
        CommandLong commandLong = CommandLong.builder()
                .param1(0).param2(0).param3(0).param4(0).param5(0).param6(0)
                .param7(altitude)
                .command(MavCmd.MAV_CMD_NAV_TAKEOFF_LOCAL)
                .targetSystem(targetSys)
                .targetComponent(targetComp)
                .confirmation(0)
                .build();
        connection.send2(sysId, compId, commandLong);
        log.info("一键起飞命令已发送，高度: {}米, 系统ID: {}", altitude, sysId);
    }
    
    /**
     * 设置飞行模式（使用默认参数）
     * @param socket 无人机连接
     * @param mode 飞行模式 (2=定高, 3=定点, 4=任务, 5=返航)
     * @param subMode 子模式 (仅任务模式使用: 2=自动起飞, 3=自动跟踪, 4=自动任务, 5=自动返航, 6=自动降落)
     * @throws IOException 通信异常
     */
    public void setFlightMode(Socket socket, int mode, int subMode) throws IOException {
        setFlightMode(socket, mode, subMode, DEFAULT_MAVLINK_SYSTEM_ID, DEFAULT_MAVLINK_COMPONENT_ID, 
                     DEFAULT_TARGET_SYSTEM, DEFAULT_TARGET_COMPONENT);
    }
    
    /**
     * 设置飞行模式（完整参数）
     * @param socket 无人机连接
     * @param mode 飞行模式 (2=定高, 3=定点, 4=任务, 5=返航)
     * @param subMode 子模式 (仅任务模式使用: 2=自动起飞, 3=自动跟踪, 4=自动任务, 5=自动返航, 6=自动降落)
     * @param sysId 系统ID
     * @param compId 组件ID
     * @param targetSys 目标系统ID
     * @param targetComp 目标组件ID
     * @throws IOException 通信异常
     */
    public void setFlightMode(Socket socket, int mode, int subMode, int sysId, int compId, int targetSys, int targetComp) throws IOException {
        MavlinkConnection connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
        CommandLong commandLong = CommandLong.builder()
                .param1(1)
                .param2(mode)
                .param3(subMode)
                .param4(0).param5(0).param6(0).param7(0)
                .command(MavCmd.MAV_CMD_DO_SET_MODE)
                .targetSystem(targetSys)
                .targetComponent(targetComp)
                .confirmation(0)
                .build();
        connection.send2(sysId, compId, commandLong);
        log.info("设置飞行模式: mode={}, subMode={}, 系统ID={}", mode, subMode, sysId);
    }
    
    /**
     * 控制机械臂/夹子（使用默认参数）
     * @param socket 无人机连接
     * @param servoValue PWM值 (800=关闭夹子, 2300=打开夹子)
     * @throws IOException 通信异常
     */
    public void controlServo(Socket socket, int servoValue) throws IOException {
        controlServo(socket, servoValue, DEFAULT_MAVLINK_SYSTEM_ID, DEFAULT_MAVLINK_COMPONENT_ID, 
                    DEFAULT_TARGET_SYSTEM, DEFAULT_TARGET_COMPONENT);
    }
    
    /**
     * 控制机械臂/夹子（完整参数）
     * @param socket 无人机连接
     * @param servoValue PWM值 (800=关闭夹子, 2300=打开夹子)
     * @param sysId 系统ID
     * @param compId 组件ID
     * @param targetSys 目标系统ID
     * @param targetComp 目标组件ID
     * @throws IOException 通信异常
     */
    public void controlServo(Socket socket, int servoValue, int sysId, int compId, int targetSys, int targetComp) throws IOException {
        MavlinkConnection connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
        CommandLong commandLong = CommandLong.builder()
                .param1(8) // 舵机通道
                .param2(servoValue)
                .param3(0).param4(0).param5(0).param6(0).param7(0)
                .command(MavCmd.MAV_CMD_DO_SET_SERVO)
                .targetSystem(targetSys)
                .targetComponent(targetComp)
                .confirmation(0)
                .build();
        connection.send2(sysId, compId, commandLong);
        String action = (servoValue == SERVO_OPEN) ? "打开" : "关闭";
        log.info("机械臂控制: {} (PWM: {}), 系统ID: {}", action, servoValue, sysId);
    }
    
    /**
     * 设置返航点（使用默认参数）
     * @param socket 无人机连接
     * @param latitude 纬度 (需要乘以10^7)
     * @param longitude 经度 (需要乘以10^7)
     * @param altitude 高度 (毫米)
     * @throws IOException 通信异常
     */
    public void setHomePosition(Socket socket, int latitude, int longitude, int altitude) throws IOException {
        setHomePosition(socket, latitude, longitude, altitude, DEFAULT_MAVLINK_SYSTEM_ID, DEFAULT_MAVLINK_COMPONENT_ID, 
                       DEFAULT_TARGET_SYSTEM);
    }
    
    /**
     * 设置返航点（完整参数）
     * @param socket 无人机连接
     * @param latitude 纬度 (需要乘以10^7)
     * @param longitude 经度 (需要乘以10^7)
     * @param altitude 高度 (毫米)
     * @param sysId 系统ID
     * @param compId 组件ID
     * @param targetSys 目标系统ID
     * @throws IOException 通信异常
     */
    public void setHomePosition(Socket socket, int latitude, int longitude, int altitude, int sysId, int compId, int targetSys) throws IOException {
        MavlinkConnection connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
        List<Float> quaternion = new ArrayList<>();
        quaternion.add(0F);
        
        SetHomePosition setHome = SetHomePosition.builder()
                .latitude(latitude)
                .longitude(longitude)
                .altitude(altitude)
                .x(0).y(0).z(0)
                .q(quaternion)
                .approachX(0).approachY(0).approachZ(0)
                .targetSystem(targetSys)
                .timeUsec(BigInteger.ZERO)
                .build();
        connection.send2(sysId, compId, setHome);
        log.info("设置返航点: 纬度={}, 经度={}, 高度={}mm, 系统ID={}", latitude, longitude, altitude, sysId);
    }
    
    /**
     * 一键返航（使用默认参数）
     * @param socket 无人机连接
     * @throws IOException 通信异常
     */
    public void returnToLaunch(Socket socket) throws IOException {
        returnToLaunch(socket, DEFAULT_MAVLINK_SYSTEM_ID, DEFAULT_MAVLINK_COMPONENT_ID, 
                      DEFAULT_TARGET_SYSTEM, DEFAULT_TARGET_COMPONENT);
    }
    
    /**
     * 一键返航（完整参数）
     * @param socket 无人机连接
     * @param sysId 系统ID
     * @param compId 组件ID
     * @param targetSys 目标系统ID
     * @param targetComp 目标组件ID
     * @throws IOException 通信异常
     */
    public void returnToLaunch(Socket socket, int sysId, int compId, int targetSys, int targetComp) throws IOException {
        MavlinkConnection connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
        CommandLong commandLong = CommandLong.builder()
                .param1(0).param2(0).param3(0).param4(0).param5(0).param6(0).param7(0)
                .command(MavCmd.MAV_CMD_NAV_RETURN_TO_LAUNCH)
                .targetSystem(targetSys)
                .targetComponent(targetComp)
                .confirmation(0)
                .build();
        connection.send2(sysId, compId, commandLong);
        log.info("一键返航命令已发送，系统ID: {}", sysId);
    }
    
    /**
     * 下载航线任务（使用默认参数）
     * @param socket 无人机连接
     * @throws IOException 通信异常
     */
    public void downloadMission(Socket socket) throws IOException {
        downloadMission(socket, DEFAULT_MAVLINK_SYSTEM_ID, DEFAULT_MAVLINK_COMPONENT_ID, 
                       DEFAULT_TARGET_SYSTEM, DEFAULT_TARGET_COMPONENT);
    }
    
    /**
     * 下载航线任务（完整参数）
     * @param socket 无人机连接
     * @param sysId 系统ID
     * @param compId 组件ID
     * @param targetSys 目标系统ID
     * @param targetComp 目标组件ID
     * @throws IOException 通信异常
     */
    public void downloadMission(Socket socket, int sysId, int compId, int targetSys, int targetComp) throws IOException {
        MavlinkConnection connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
        MissionRequestList requestList = MissionRequestList.builder()
                .targetSystem(targetSys)
                .targetComponent(targetComp)
                .missionType(MavMissionType.MAV_MISSION_TYPE_MISSION)
                .build();
        connection.send2(sysId, compId, requestList);
        log.info("下载航线任务命令已发送，系统ID: {}", sysId);
    }
    
    /**
     * 请求特定任务项（使用默认参数）
     * @param socket 无人机连接
     * @param seq 任务序列号
     * @throws IOException 通信异常
     */
    public void requestMissionItem(Socket socket, int seq) throws IOException {
        requestMissionItem(socket, seq, DEFAULT_MAVLINK_SYSTEM_ID, DEFAULT_MAVLINK_COMPONENT_ID, 
                          DEFAULT_TARGET_SYSTEM, DEFAULT_TARGET_COMPONENT);
    }
    
    /**
     * 请求特定任务项（完整参数）
     * @param socket 无人机连接
     * @param seq 任务序列号
     * @param sysId 系统ID
     * @param compId 组件ID
     * @param targetSys 目标系统ID
     * @param targetComp 目标组件ID
     * @throws IOException 通信异常
     */
    public void requestMissionItem(Socket socket, int seq, int sysId, int compId, int targetSys, int targetComp) throws IOException {
        MavlinkConnection connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
        MissionRequestInt requestInt = MissionRequestInt.builder()
                .targetSystem(targetSys)
                .targetComponent(targetComp)
                .seq(seq)
                .missionType(MavMissionType.MAV_MISSION_TYPE_MISSION)
                .build();
        connection.send2(sysId, compId, requestInt);
        log.info("请求任务项: 序列={}, 系统ID={}", seq, sysId);
    }
    
    /**
     * 确认任务接收（使用默认参数）
     * @param socket 无人机连接
     * @throws IOException 通信异常
     */
    public void acknowledgeMission(Socket socket) throws IOException {
        acknowledgeMission(socket, DEFAULT_MAVLINK_SYSTEM_ID, DEFAULT_MAVLINK_COMPONENT_ID, 
                          DEFAULT_TARGET_SYSTEM, DEFAULT_TARGET_COMPONENT);
    }
    
    /**
     * 确认任务接收（完整参数）
     * @param socket 无人机连接
     * @param sysId 系统ID
     * @param compId 组件ID
     * @param targetSys 目标系统ID
     * @param targetComp 目标组件ID
     * @throws IOException 通信异常
     */
    public void acknowledgeMission(Socket socket, int sysId, int compId, int targetSys, int targetComp) throws IOException {
        MavlinkConnection connection = MavlinkConnection.create(socket.getInputStream(), socket.getOutputStream());
        MissionAck ack = MissionAck.builder()
                .targetSystem(targetSys)
                .targetComponent(targetComp)
                .type(MavMissionResult.MAV_MISSION_ACCEPTED)
                .missionType(MavMissionType.MAV_MISSION_TYPE_MISSION)
                .build();
        connection.send2(sysId, compId, ack);
        log.info("任务确认已发送，系统ID: {}", sysId);
    }
    
    // ============================== Modbus 无人机柜控制方法 ==============================
    
    /**
     * 设置无人机柜为自动模式（使用默认参数）
     * @param master Modbus连接
     * @throws Exception 操作异常
     */
    public void setContainerAutoMode(ModbusMaster master) throws Exception {
        setContainerAutoMode(master, DEFAULT_MODBUS_SLAVE_ID, DEFAULT_MODBUS_ADDR_MODE_SET, DEFAULT_MODBUS_ADDR_MODE_STATUS);
    }
    
    /**
     * 设置无人机柜为自动模式（完整参数）
     * @param master Modbus连接
     * @param slaveId 从站ID
     * @param modeSetAddr 模式设置地址
     * @param modeStatusAddr 模式状态地址
     * @throws Exception 操作异常
     */
    public void setContainerAutoMode(ModbusMaster master, int slaveId, int modeSetAddr, int modeStatusAddr) throws Exception {
        if (!master.isConnected()) {
            master.connect();
        }
        
        // 先检查当前模式
        int[] currentMode = master.readInputRegisters(slaveId, modeStatusAddr, 1);
        for (int mode : currentMode) {
            if (mode != 12) { // 12表示自动模式
                // 设置为自动模式
                master.writeSingleRegister(slaveId, modeSetAddr, 0x0A);
                
                // 等待模式切换完成
                while (true) {
                    int[] newMode = master.readInputRegisters(slaveId, modeStatusAddr, 1);
                    for (int modeStatus : newMode) {
                        if (modeStatus == 12) {
                            log.info("无人机柜已切换到自动模式，从站ID: {}", slaveId);
                            return;
                        } else if (modeStatus != 10) {
                            throw new Exception("设置自动模式异常");
                        }
                    }
                    Thread.sleep(1000);
                }
            }
        }
        log.info("无人机柜已在自动模式，从站ID: {}", slaveId);
    }
    
    /**
     * 检查存件格口状态（使用默认参数）
     * @param master Modbus连接
     * @throws Exception 操作异常
     */
    public void checkStorageStatus(ModbusMaster master) throws Exception {
        checkStorageStatus(master, DEFAULT_MODBUS_SLAVE_ID, DEFAULT_MODBUS_ADDR_STORAGE_STATUS);
    }
    
    /**
     * 检查存件格口状态（完整参数）
     * @param master Modbus连接
     * @param slaveId 从站ID
     * @param storageStatusAddr 存件状态地址
     * @throws Exception 操作异常
     */
    public void checkStorageStatus(ModbusMaster master, int slaveId, int storageStatusAddr) throws Exception {
        if (!master.isConnected()) {
            master.connect();
        }
        
        int[] status = master.readInputRegisters(slaveId, storageStatusAddr, 1);
        for (int statusValue : status) {
            if (statusValue == 10) { // 10表示不可以存件
                log.error("当前不可以存件，从站ID: {}", slaveId);
                throw new Exception("当前无法存件");
            } else if (statusValue == 11) { // 11表示可以存件
                log.info("当前可以存件，从站ID: {}", slaveId);
            }
        }
    }
    
    /**
     * 打开舱门（使用默认参数）
     * @param master Modbus连接
     * @throws Exception 操作异常
     */
    public void openDoor(ModbusMaster master) throws Exception {
        openDoor(master, DEFAULT_MODBUS_SLAVE_ID, DEFAULT_MODBUS_ADDR_DOOR);
    }
    
    /**
     * 打开舱门（完整参数）
     * @param master Modbus连接
     * @param slaveId 从站ID
     * @param doorAddr 舱门控制地址
     * @throws Exception 操作异常
     */
    public void openDoor(ModbusMaster master, int slaveId, int doorAddr) throws Exception {
        if (!master.isConnected()) {
            master.connect();
        }
        
        master.writeSingleRegister(slaveId, doorAddr, 0x0A);
        
        while (true) {
            int[] doorStatus = master.readInputRegisters(slaveId, doorAddr, 1);
            for (int status : doorStatus) {
                if (status == 11) { // 11表示舱门开到位
                    log.info("舱门已打开，从站ID: {}", slaveId);
                    return;
                } else if (status != 10) {
                    throw new Exception("开舱门异常");
                }
            }
            Thread.sleep(1000);
        }
    }
    
    /**
     * 关闭舱门（使用默认参数）
     * @param master Modbus连接
     * @throws Exception 操作异常
     */
    public void closeDoor(ModbusMaster master) throws Exception {
        closeDoor(master, DEFAULT_MODBUS_SLAVE_ID, DEFAULT_MODBUS_ADDR_DOOR);
    }
    
    /**
     * 关闭舱门（完整参数）
     * @param master Modbus连接
     * @param slaveId 从站ID
     * @param doorAddr 舱门控制地址
     * @throws Exception 操作异常
     */
    public void closeDoor(ModbusMaster master, int slaveId, int doorAddr) throws Exception {
        writeAndWaitForResponse(master, slaveId, doorAddr, 20, 21, "关舱门");
    }
    
    /**
     * 确认停机坪有飞机（使用默认参数）
     * @param master Modbus连接
     * @throws Exception 操作异常
     */
    public void confirmDroneOnPad(ModbusMaster master) throws Exception {
        confirmDroneOnPad(master, DEFAULT_MODBUS_SLAVE_ID, DEFAULT_MODBUS_ADDR_PARKING);
    }
    
    /**
     * 确认停机坪有飞机（完整参数）
     * @param master Modbus连接
     * @param slaveId 从站ID
     * @param parkingAddr 停机坪地址
     * @throws Exception 操作异常
     */
    public void confirmDroneOnPad(ModbusMaster master, int slaveId, int parkingAddr) throws Exception {
        if (!master.isConnected()) {
            master.connect();
        }
        
        // 发送停机坪飞机确认信号
        master.writeSingleRegister(slaveId, parkingAddr, 0x0A);
        
        while (true) {
            int[] parkingStatus = master.readInputRegisters(slaveId, parkingAddr, 1);
            for (int status : parkingStatus) {
                if (status == 11) { // 11表示停机坪有飞机
                    log.info("停机坪有飞机确认，从站ID: {}", slaveId);
                    return;
                } else if (status != 10) {
                    throw new Exception("停机坪飞机确认异常");
                }
            }
            Thread.sleep(1000);
        }
    }
    
    /**
     * 设置停机坪无飞机（使用默认参数）
     * @param master Modbus连接
     * @throws Exception 操作异常
     */
    public void setParkingEmpty(ModbusMaster master) throws Exception {
        setParkingEmpty(master, DEFAULT_MODBUS_SLAVE_ID, DEFAULT_MODBUS_ADDR_PARKING);
    }
    
    /**
     * 设置停机坪无飞机（完整参数）
     * @param master Modbus连接
     * @param slaveId 从站ID
     * @param parkingAddr 停机坪地址
     * @throws Exception 操作异常
     */
    public void setParkingEmpty(ModbusMaster master, int slaveId, int parkingAddr) throws Exception {
        writeAndWaitForResponse(master, slaveId, parkingAddr, 20, 21, "停机坪无飞机");
    }
    
    /**
     * 开始存件操作（使用默认参数）
     * @param master Modbus连接
     * @param pickupCode1 取件码前三位
     * @param pickupCode2 取件码后三位
     * @throws Exception 操作异常
     */
    public void startStorageOperation(ModbusMaster master, int pickupCode1, int pickupCode2) throws Exception {
        startStorageOperation(master, pickupCode1, pickupCode2, DEFAULT_MODBUS_SLAVE_ID, 
                             DEFAULT_MODBUS_ADDR_STORAGE, DEFAULT_MODBUS_ADDR_CODE_1, DEFAULT_MODBUS_ADDR_CODE_2);
    }
    
    /**
     * 开始存件操作（完整参数）
     * @param master Modbus连接
     * @param pickupCode1 取件码前三位
     * @param pickupCode2 取件码后三位
     * @param slaveId 从站ID
     * @param storageAddr 存件操作地址
     * @param code1Addr 取件码1地址
     * @param code2Addr 取件码2地址
     * @throws Exception 操作异常
     */
    public void startStorageOperation(ModbusMaster master, int pickupCode1, int pickupCode2, 
                                     int slaveId, int storageAddr, int code1Addr, int code2Addr) throws Exception {
        if (!master.isConnected()) {
            master.connect();
        }
        
        // 开始执行存件动作
        master.writeSingleRegister(slaveId, storageAddr, 110);
        
        // 设置取件码
        master.writeSingleRegister(slaveId, code1Addr, pickupCode1);
        master.writeSingleRegister(slaveId, code2Addr, pickupCode2);
        
        log.info("存件操作已开始，取件码: {}{}, 从站ID: {}", pickupCode1, pickupCode2, slaveId);
    }
    
    /**
     * 等待舵机状态就绪（使用默认参数）
     * @param master Modbus连接
     * @param expectedStatus 期望的状态值
     * @param timeoutMs 超时时间(毫秒)
     * @throws Exception 操作异常
     */
    public void waitForServoStatus(ModbusMaster master, int expectedStatus, long timeoutMs) throws Exception {
        waitForServoStatus(master, expectedStatus, timeoutMs, DEFAULT_MODBUS_SLAVE_ID, DEFAULT_MODBUS_ADDR_SERVO);
    }
    
    /**
     * 等待舵机状态就绪（完整参数）
     * @param master Modbus连接
     * @param expectedStatus 期望的状态值
     * @param timeoutMs 超时时间(毫秒)
     * @param slaveId 从站ID
     * @param servoAddr 舵机地址
     * @throws Exception 操作异常
     */
    public void waitForServoStatus(ModbusMaster master, int expectedStatus, long timeoutMs, int slaveId, int servoAddr) throws Exception {
        if (!master.isConnected()) {
            master.connect();
        }
        
        long startTime = System.currentTimeMillis();
        
        while (true) {
            if (System.currentTimeMillis() - startTime > timeoutMs) {
                throw new Exception("等待舵机状态超时");
            }
            
            int[] servoStatus = master.readInputRegisters(slaveId, servoAddr, 1);
            for (int status : servoStatus) {
                if (status == expectedStatus) {
                    log.info("舵机状态就绪: {}, 从站ID: {}", status, slaveId);
                    return;
                }
            }
            Thread.sleep(1000);
        }
    }
    
    /**
     * 设置舵机状态（使用默认参数）
     * @param master Modbus连接
     * @param command 舵机命令 (10=打开舵机, 20=关闭舵机)
     * @param expectedResponse 期望的响应值
     * @param description 操作描述
     * @throws Exception 操作异常
     */
    public void setServoStatus(ModbusMaster master, int command, int expectedResponse, String description) throws Exception {
        setServoStatus(master, command, expectedResponse, description, DEFAULT_MODBUS_SLAVE_ID, DEFAULT_MODBUS_ADDR_SERVO);
    }
    
    /**
     * 设置舵机状态（完整参数）
     * @param master Modbus连接
     * @param command 舵机命令 (10=打开舵机, 20=关闭舵机)
     * @param expectedResponse 期望的响应值
     * @param description 操作描述
     * @param slaveId 从站ID
     * @param servoAddr 舵机地址
     * @throws Exception 操作异常
     */
    public void setServoStatus(ModbusMaster master, int command, int expectedResponse, String description, 
                              int slaveId, int servoAddr) throws Exception {
        writeAndWaitForResponse(master, slaveId, servoAddr, command, expectedResponse, description);
    }
    
    /**
     * 等待存件完成（使用默认参数）
     * @param master Modbus连接
     * @return 存件结果 (111=存件完成需要取包裹, 122=存件完成不取包裹)
     * @throws Exception 操作异常
     */
    public int waitForStorageComplete(ModbusMaster master) throws Exception {
        return waitForStorageComplete(master, DEFAULT_MODBUS_SLAVE_ID, DEFAULT_MODBUS_ADDR_STORAGE);
    }
    
    /**
     * 等待存件完成（完整参数）
     * @param master Modbus连接
     * @param slaveId 从站ID
     * @param storageAddr 存件地址
     * @return 存件结果 (111=存件完成需要取包裹, 122=存件完成不取包裹)
     * @throws Exception 操作异常
     */
    public int waitForStorageComplete(ModbusMaster master, int slaveId, int storageAddr) throws Exception {
        if (!master.isConnected()) {
            master.connect();
        }
        
        while (true) {
            int[] storageStatus = master.readInputRegisters(slaveId, storageAddr, 1);
            for (int status : storageStatus) {
                if (status == 111) {
                    log.info("存件完成，需要取包裹，从站ID: {}", slaveId);
                    return 111;
                } else if (status == 122) {
                    log.info("存件完成，不取包裹，从站ID: {}", slaveId);
                    return 122;
                } else if (status != 110) {
                    throw new Exception("存件操作异常，状态码: " + status);
                }
            }
            Thread.sleep(1000);
        }
    }
    
    /**
     * 等待特定状态值（使用默认参数）
     * @param master Modbus连接
     * @param address Modbus地址
     * @param expectedValue 期望值
     * @param description 操作描述
     * @throws Exception 操作异常
     */
    public void waitForStatus(ModbusMaster master, int address, int expectedValue, String description) throws Exception {
        waitForStatus(master, address, expectedValue, description, DEFAULT_MODBUS_SLAVE_ID);
    }
    
    /**
     * 等待特定状态值（完整参数）
     * @param master Modbus连接
     * @param address Modbus地址
     * @param expectedValue 期望值
     * @param description 操作描述
     * @param slaveId 从站ID
     * @throws Exception 操作异常
     */
    public void waitForStatus(ModbusMaster master, int address, int expectedValue, String description, int slaveId) throws Exception {
        if (!master.isConnected()) {
            master.connect();
        }
        
        while (true) {
            int[] status = master.readInputRegisters(slaveId, address, 1);
            for (int statusValue : status) {
                if (statusValue == expectedValue) {
                    log.info("{}: 状态值={}, 从站ID={}", description, statusValue, slaveId);
                    return;
                }
            }
            Thread.sleep(1000);
        }
    }
    
    /**
     * 获取取件码（使用默认参数）
     * @param master Modbus连接
     * @return 完整的取件码字符串
     * @throws Exception 操作异常
     */
    public String getPickupCode(ModbusMaster master) throws Exception {
        return getPickupCode(master, DEFAULT_MODBUS_SLAVE_ID, DEFAULT_MODBUS_ADDR_CODE_1);
    }
    
    /**
     * 获取取件码（完整参数）
     * @param master Modbus连接
     * @param slaveId 从站ID
     * @param codeStartAddr 取件码起始地址
     * @return 完整的取件码字符串
     * @throws Exception 操作异常
     */
    public String getPickupCode(ModbusMaster master, int slaveId, int codeStartAddr) throws Exception {
        if (!master.isConnected()) {
            master.connect();
        }
        
        int[] codes = master.readInputRegisters(slaveId, codeStartAddr, 2);
        StringBuilder codeBuilder = new StringBuilder();
        
        for (int code : codes) {
            codeBuilder.append(code);
        }
        
        String pickupCode = codeBuilder.toString();
        log.info("获取到取件码: {}, 从站ID: {}", pickupCode, slaveId);
        return pickupCode;
    }
    
    /**
     * 通用Modbus写入并等待响应方法
     * @param master Modbus连接
     * @param slaveId 从站ID
     * @param address Modbus地址
     * @param writeValue 写入值
     * @param expectedResponse 期望响应值
     * @param description 操作描述
     * @throws Exception 操作异常
     */
    private void writeAndWaitForResponse(ModbusMaster master, int slaveId, int address, int writeValue, 
                                       int expectedResponse, String description) throws Exception {
        if (!master.isConnected()) {
            master.connect();
        }
        
        master.writeSingleRegister(slaveId, address, writeValue);
        
        while (true) {
            int[] status = master.readInputRegisters(slaveId, address, 1);
            for (int statusValue : status) {
                if (statusValue == expectedResponse) {
                    log.info("{} 完成，从站ID: {}", description, slaveId);
                    return;
                } else if (statusValue != writeValue) {
                    throw new Exception(description + " 异常，期望=" + expectedResponse + ", 实际=" + statusValue);
                }
            }
            Thread.sleep(1000);
        }
    }
    
    // ============================== 高级组合操作方法 ==============================
    
    /**
     * 完整的配送流程 - 带货物配送
     * @param droneSocket 无人机连接
     * @param containerMaster 无人机柜连接
     * @param takeoffLat 起飞点纬度 (需要乘以10^7)
     * @param takeoffLon 起飞点经度 (需要乘以10^7)
     * @param takeoffAlt 起飞高度 (米)
     * @param destinationLat 目的地纬度 (需要乘以10^7)
     * @param destinationLon 目的地经度 (需要乘以10^7)
     * @param destinationAlt 目的地高度 (米)
     * @param pickupCode1 取件码前三位
     * @param pickupCode2 取件码后三位
     * @throws Exception 操作异常
     */
    public void executeDeliveryWithContainer(Socket droneSocket, ModbusMaster containerMaster,
                                           int takeoffLat, int takeoffLon, int takeoffAlt,
                                           int destinationLat, int destinationLon, int destinationAlt,
                                           int pickupCode1, int pickupCode2) throws Exception {
        log.info("开始执行带箱子的配送流程");
        
        // 1. 设置无人机柜为自动模式
        setContainerAutoMode(containerMaster);
        
        // 2. 检查存件格口状态
        checkStorageStatus(containerMaster);
        
        // 3. 打开舱门
        openDoor(containerMaster);
        
        // 4. 设置航线任务
        setMissionCount(droneSocket, 6); // 总共6个任务
        setSpeed(droneSocket, 1, 15); // 设置速度15m/s
        setTakeoff(droneSocket, takeoffLat, takeoffLon, takeoffAlt, 0);
        setWaypoint(droneSocket, takeoffLat, takeoffLon, 100, 2, NAN.floatValue());
        setWaypoint(droneSocket, takeoffLat, takeoffLon, 100, 3, NAN.floatValue());
        setLanding(droneSocket, destinationLat, destinationLon, 0, 4, false);
        setLanding(droneSocket, destinationLat, destinationLon, 0, 5, true);
        
        // 5. 下载并确认航线
        downloadMission(droneSocket);
        for (int i = 0; i < 6; i++) {
            requestMissionItem(droneSocket, i);
        }
        acknowledgeMission(droneSocket);
        
        // 6. 解锁并起飞
        armDrone(droneSocket);
        Thread.sleep(5000);
        takeoffNow(droneSocket, 50);
        setFlightMode(droneSocket, 4, 4); // 设置自动任务模式
        
        log.info("无人机已起飞，等待到达目的地...");
        // 注意：这里需要外部监控无人机状态，等待降落完成
        
        log.info("配送流程第一阶段完成");
    }
    
    /**
     * 配送流程第二阶段 - 存件和返程
     * @param droneSocket 无人机连接
     * @param containerMaster 无人机柜连接
     * @param returnLat 返程点纬度 (需要乘以10^7)
     * @param returnLon 返程点经度 (需要乘以10^7)
     * @param returnAlt 返程高度 (米)
     * @param pickupCode1 取件码前三位
     * @param pickupCode2 取件码后三位
     * @param needPickup 是否需要取件
     * @throws Exception 操作异常
     */
    public void executeStorageAndReturn(Socket droneSocket, ModbusMaster containerMaster,
                                      int returnLat, int returnLon, int returnAlt,
                                      int pickupCode1, int pickupCode2, boolean needPickup) throws Exception {
        log.info("开始执行存件和返程流程");
        
        // 1. 确认无人机已降落
        confirmDroneOnPad(containerMaster);
        
        // 2. 开始存件操作
        startStorageOperation(containerMaster, pickupCode1, pickupCode2);
        
        // 3. 等待舵机状态就绪
        waitForServoStatus(containerMaster, 1, 30000); // 等待30秒
        
        // 4. 打开机械臂
        controlServo(droneSocket, SERVO_OPEN);
        setServoStatus(containerMaster, 10, 11, "开舵机");
        
        // 5. 等待存件完成
        int storageResult = waitForStorageComplete(containerMaster);
        
        if (storageResult == 111 && needPickup) {
            // 需要取包裹
            waitForServoStatus(containerMaster, 2, 30000);
            controlServo(droneSocket, SERVO_CLOSE); // 关闭夹子
            Thread.sleep(3000);
            setServoStatus(containerMaster, 20, 21, "关舵机");
            waitForStatus(containerMaster, DEFAULT_MODBUS_ADDR_STORAGE, 121, "无人机取件");
            
            // 设置返程航线
            setMissionCount(droneSocket, 3);
            setTakeoff(droneSocket, returnLat, returnLon, 50, 0);
            setLanding(droneSocket, returnLat, returnLon, 0, 1, false);
            setLanding(droneSocket, returnLat, returnLon, 0, 2, true);
            
            // 下载并确认返程航线
            downloadMission(droneSocket);
            for (int i = 0; i < 3; i++) {
                requestMissionItem(droneSocket, i);
            }
            acknowledgeMission(droneSocket);
            
            // 起飞返程
            armDrone(droneSocket);
            Thread.sleep(5000);
            takeoffNow(droneSocket, 50);
            setFlightMode(droneSocket, 4, 4);
            Thread.sleep(5000);
            
            setParkingEmpty(containerMaster);
            
        } else {
            // 不需要取包裹，直接返程
            setServoStatus(containerMaster, 20, 21, "关舵机");
            setParkingEmpty(containerMaster);
        }
        
        // 最后关闭舱门
        closeDoor(containerMaster);
        
        log.info("存件和返程流程完成");
    }
    
    /**
     * 简单配送流程 - 不带货柜操作
     * @param droneSocket 无人机连接
     * @param takeoffLat 起飞点纬度 (需要乘以10^7)
     * @param takeoffLon 起飞点经度 (需要乘以10^7)
     * @param takeoffAlt 起飞高度 (米)
     * @param destinationLat 目的地纬度 (需要乘以10^7)
     * @param destinationLon 目的地经度 (需要乘以10^7)
     * @param destinationAlt 目的地高度 (米)
     * @throws Exception 操作异常
     */
    public void executeSimpleDelivery(Socket droneSocket, 
                                    int takeoffLat, int takeoffLon, int takeoffAlt,
                                    int destinationLat, int destinationLon, int destinationAlt) throws Exception {
        log.info("开始执行简单配送流程");
        
        // 设置航线任务
        setMissionCount(droneSocket, 3);
        setTakeoff(droneSocket, takeoffLat, takeoffLon, takeoffAlt, 0);
        setLanding(droneSocket, destinationLat, destinationLon, destinationAlt, 1, false);
        setLanding(droneSocket, destinationLat, destinationLon, destinationAlt, 2, true);
        
        // 下载并确认航线
        downloadMission(droneSocket);
        for (int i = 0; i < 3; i++) {
            requestMissionItem(droneSocket, i);
        }
        acknowledgeMission(droneSocket);
        
        // 解锁并起飞
        armDrone(droneSocket);
        Thread.sleep(5000);
        takeoffNow(droneSocket, 50);
        setFlightMode(droneSocket, 4, 4);
        
        log.info("简单配送流程已启动");
    }
    
    // ============================== 工具方法 ==============================
    
    /**
     * 将度数转换为MAVLink协议的整数格式
     * @param degrees 度数 (小数形式)
     * @return MAVLink整数 (度数 * 10^7)
     */
    public static int degreesToMavlinkInt(double degrees) {
        return (int) (degrees * 10_000_000);
    }
    
    /**
     * 将MAVLink整数格式转换为度数
     * @param mavlinkInt MAVLink整数
     * @return 度数 (小数形式)
     */
    public static double mavlinkIntToDegrees(int mavlinkInt) {
        return mavlinkInt / 10_000_000.0;
    }
    
    /**
     * 验证坐标是否有效
     * @param latitude 纬度
     * @param longitude 经度
     * @return 是否有效
     */
    public static boolean isValidCoordinate(double latitude, double longitude) {
        return latitude >= -90.0 && latitude <= 90.0 && longitude >= -180.0 && longitude <= 180.0;
    }
    
    /**
     * 检查无人机是否准备好降落
     * @param landingStatus 降落状态
     * @param unlockStatus 解锁状态
     * @return 是否准备好降落
     */
    public static boolean isDroneReadyForLanding(int landingStatus, int unlockStatus) {
        return landingStatus == DRONE_STATUS_ON_GROUND && unlockStatus == DRONE_UNLOCK_STATUS_UNLOCKED;
    }
    
    /**
     * 检查GPS是否RTK固定
     * @param gpsFixType GPS修正类型
     * @return 是否RTK固定
     */
    public static boolean isGpsRtkFixed(int gpsFixType) {
        return gpsFixType == GPS_FIX_TYPE_RTK;
    }
    
    /**
     * 获取系统状态描述
     * @param landingStatus 降落状态
     * @param unlockStatus 解锁状态
     * @param gpsFixType GPS修正类型
     * @return 状态描述
     */
    public static String getSystemStatusDescription(int landingStatus, int unlockStatus, int gpsFixType) {
        StringBuilder sb = new StringBuilder();
        
        // 降落状态
        sb.append("降落状态: ");
        if (landingStatus == DRONE_STATUS_ON_GROUND) {
            sb.append("在地面");
        } else if (landingStatus == DRONE_STATUS_IN_FLIGHT) {
            sb.append("飞行中");
        } else {
            sb.append("未知(").append(landingStatus).append(")");
        }
        
        // 解锁状态
        sb.append(", 解锁状态: ");
        if (unlockStatus == DRONE_UNLOCK_STATUS_LOCKED) {
            sb.append("已加锁");
        } else if (unlockStatus == DRONE_UNLOCK_STATUS_UNLOCKED) {
            sb.append("已解锁");
        } else {
            sb.append("未知(").append(unlockStatus).append(")");
        }
        
        // GPS状态
        sb.append(", GPS: ");
        if (gpsFixType == GPS_FIX_TYPE_RTK) {
            sb.append("RTK固定");
        } else {
            sb.append("非RTK(").append(gpsFixType).append(")");
        }
        
        return sb.toString();
    }
    
    /**
     * 生成默认取件码
     * @return 取件码数组 [前三位, 后三位]
     */
    public static int[] generateDefaultPickupCode() {
        // 生成6位随机数字
        int code = (int) (Math.random() * 1000000);
        String codeStr = String.format("%06d", code);
        
        int code1 = Integer.parseInt(codeStr.substring(0, 3));
        int code2 = Integer.parseInt(codeStr.substring(3, 6));
        
        return new int[]{code1, code2};
    }
    
    /**
     * 等待指定时间
     * @param milliseconds 等待时间（毫秒）
     */
    public static void waitFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("等待过程被中断");
        }
    }
    
    /**
     * 计算两点之间的距离（近似）
     * @param lat1 纬度1
     * @param lon1 经度1
     * @param lat2 纬度2
     * @param lon2 经度2
     * @return 距离（米）
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371000; // 地球半径（米）
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    /**
     * 验证取件码格式
     * @param code1 取件码前三位
     * @param code2 取件码后三位
     * @return 是否有效
     */
    public static boolean isValidPickupCode(int code1, int code2) {
        return code1 >= 0 && code1 <= 999 && code2 >= 0 && code2 <= 999;
    }
    
    /**
     * 格式化取件码
     * @param code1 取件码前三位
     * @param code2 取件码后三位
     * @return 格式化的取件码字符串
     */
    public static String formatPickupCode(int code1, int code2) {
        return String.format("%03d%03d", code1, code2);
    }
    
    /**
     * 检查Modbus地址是否有效
     * @param address Modbus地址
     * @return 是否有效
     */
    public static boolean isValidModbusAddress(int address) {
        return address >= 0 && address <= 0xFFFF;
    }
    
    /**
     * 检查MAVLink系统ID是否有效
     * @param systemId 系统ID
     * @return 是否有效
     */
    public static boolean isValidSystemId(int systemId) {
        return systemId >= 1 && systemId <= 255;
    }
    
    /**
     * 检查从站ID是否有效
     * @param slaveId 从站ID
     * @return 是否有效
     */
    public static boolean isValidSlaveId(int slaveId) {
        return slaveId >= 1 && slaveId <= 247;
    }
    
    /**
     * 获取飞行模式描述
     * @param mode 飞行模式
     * @param subMode 子模式
     * @return 模式描述
     */
    public static String getFlightModeDescription(int mode, int subMode) {
        String modeDesc;
        switch (mode) {
            case 2:
                modeDesc = "定高模式";
                break;
            case 3:
                modeDesc = "定点模式";
                break;
            case 4:
                modeDesc = "任务模式";
                break;
            case 5:
                modeDesc = "返航模式";
                break;
            default:
                modeDesc = "未知模式(" + mode + ")";
        }
        
        if (mode == 4) { // 任务模式有子模式
            String subModeDesc;
            switch (subMode) {
                case 2:
                    subModeDesc = "自动起飞";
                    break;
                case 3:
                    subModeDesc = "自动跟踪";
                    break;
                case 4:
                    subModeDesc = "自动任务";
                    break;
                case 5:
                    subModeDesc = "自动返航";
                    break;
                case 6:
                    subModeDesc = "自动降落";
                    break;
                default:
                    subModeDesc = "未知子模式(" + subMode + ")";
            }
            modeDesc += " - " + subModeDesc;
        }
        
        return modeDesc;
    }
    
    /**
     * 检查网络连接是否可用
     * @param host 主机地址
     * @param port 端口
     * @param timeoutMs 超时时间（毫秒）
     * @return 是否可连接
     */
    public static boolean isNetworkReachable(String host, int port, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new java.net.InetSocketAddress(host, port), timeoutMs);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 生成设备配置摘要
     * @param droneHost 无人机地址
     * @param dronePort 无人机端口
     * @param containerHost 货柜地址
     * @param containerPort 货柜端口
     * @param systemId 系统ID
     * @param slaveId 从站ID
     * @return 配置摘要
     */
    public static String generateDeviceConfigSummary(String droneHost, int dronePort, 
                                                   String containerHost, int containerPort,
                                                   int systemId, int slaveId) {
        return String.format(
            "设备配置 | 无人机: %s:%d (系统ID: %d) | 货柜: %s:%d (从站ID: %d)",
            droneHost, dronePort, systemId, containerHost, containerPort, slaveId
        );
    }
} 