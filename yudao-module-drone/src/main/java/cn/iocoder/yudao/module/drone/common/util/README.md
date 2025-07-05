# 无人机柜控制工具类 (DroneControlUtil)

## 概述

`DroneControlUtil` 是一个完整的无人机柜控制工具类，整合了无人机MAVLink控制和无人机柜Modbus控制的所有核心功能。该工具类支持多设备控制，所有关键参数都可以自定义传入。

## 主要特性

- ✅ **多设备支持** - 可以同时控制多个不同的无人机和货柜
- ✅ **参数化设计** - 所有关键参数都可以自定义传入
- ✅ **默认值支持** - 提供默认参数的重载方法，便于快速使用
- ✅ **异常处理** - 完整的异常处理和错误日志
- ✅ **连接管理** - 自动连接、重连和连接清理
- ✅ **工具方法** - 丰富的工具方法支持各种操作

## 核心功能

### 1. 连接管理
- 无人机连接 (MAVLink over TCP)
- 无人机柜连接 (Modbus TCP)
- 自动重连机制
- 连接状态监控

### 2. MAVLink 无人机控制
- 任务规划和航线设置
- 飞行控制 (起飞、降落、航点)
- 模式切换 (定点、定高、任务、返航)
- 安全控制 (解锁/加锁)
- 机械臂控制 (夹子开关)

### 3. Modbus 无人机柜控制
- 模式设置 (自动模式)
- 舱门控制 (开门/关门)
- 停机坪管理
- 存件操作
- 舵机控制

## 使用方法

### 1. 基本用法 (使用默认参数)

```java
DroneControlUtil droneControl = new DroneControlUtil();

// 连接设备
Socket drone = droneControl.connectDrone(); // 使用默认地址和端口
ModbusMaster container = droneControl.connectContainer();

// 基本操作
droneControl.setMissionCount(drone, 5); // 使用默认MAVLink参数
droneControl.setContainerAutoMode(container); // 使用默认Modbus参数

// 清理连接
droneControl.closeConnection(drone);
droneControl.closeConnection(container);
```

### 2. 多设备控制

```java
// 连接多个设备
Socket drone1 = droneControl.connectDrone("192.168.1.10", 61473);
Socket drone2 = droneControl.connectDrone("192.168.1.11", 61474);
ModbusMaster container1 = droneControl.connectContainer("192.168.1.20", 502);
ModbusMaster container2 = droneControl.connectContainer("192.168.1.21", 502);

// 使用不同的参数控制不同设备
droneControl.setMissionCount(drone1, 5, 1, 1, 1, 1); // 系统ID=1
droneControl.setMissionCount(drone2, 5, 2, 1, 2, 1); // 系统ID=2
droneControl.setContainerAutoMode(container1, 1, 0xBCC, 0xBCD); // 从站ID=1
droneControl.setContainerAutoMode(container2, 2, 0xBCC, 0xBCD); // 从站ID=2
```

### 3. 完整配送流程

```java
// 设备配置
String droneHost = "192.168.100.50";
int dronePort = 61473;
String containerHost = "192.168.100.51";
int containerPort = 502;
int systemId = 5;
int slaveId = 3;

// 连接设备
Socket drone = droneControl.connectDrone(droneHost, dronePort);
ModbusMaster container = droneControl.connectContainer(containerHost, containerPort);

// 1. 设置航线
droneControl.setMissionCount(drone, 6, systemId, 1, systemId, 1);
droneControl.setSpeed(drone, 1, 15, systemId, 1, systemId, 1);

int lat = DroneControlUtil.degreesToMavlinkInt(28.7904567);
int lon = DroneControlUtil.degreesToMavlinkInt(115.3875000);
droneControl.setTakeoff(drone, lat, lon, 50, 0, systemId, 1, systemId, 1);
droneControl.setLanding(drone, lat, lon, 0, 5, true, systemId, 1, systemId, 1);

// 2. 准备货柜
droneControl.setContainerAutoMode(container, slaveId, 0xBCC, 0xBCD);
droneControl.openDoor(container, slaveId, 0xBB8);

// 3. 执行任务
droneControl.armDrone(drone, systemId, 1, systemId, 1);
droneControl.takeoffNow(drone, 50, systemId, 1, systemId, 1);
droneControl.setFlightMode(drone, 4, 4, systemId, 1, systemId, 1);
```

## 默认参数配置

### 连接参数
- 无人机默认地址: `sk.yunenjoy.cn:61473`
- 货柜默认地址: `172.22.33.253:502`

### MAVLink参数
- 系统ID: `1`
- 组件ID: `1`
- 目标系统ID: `1`
- 目标组件ID: `1`

### Modbus参数
- 从站ID: `1`
- 舱门地址: `0xBB8`
- 停机坪地址: `0xBB9`
- 存件地址: `0xBBA`
- 舵机地址: `0xBBB`
- 模式设置地址: `0xBCC`
- 模式状态地址: `0xBCD`

## 工具方法

### 坐标转换
```java
double lat = 28.7904567;
int mavlinkLat = DroneControlUtil.degreesToMavlinkInt(lat);
double convertedLat = DroneControlUtil.mavlinkIntToDegrees(mavlinkLat);
```

### 参数验证
```java
boolean validCoord = DroneControlUtil.isValidCoordinate(28.7904567, 115.3875000);
boolean validSystemId = DroneControlUtil.isValidSystemId(5);
boolean validSlaveId = DroneControlUtil.isValidSlaveId(3);
```

### 状态检查
```java
boolean ready = DroneControlUtil.isDroneReadyForLanding(1, 209);
boolean rtkFixed = DroneControlUtil.isGpsRtkFixed(6);
String status = DroneControlUtil.getSystemStatusDescription(1, 209, 6);
```

### 取件码生成
```java
int[] code = DroneControlUtil.generateDefaultPickupCode();
String formattedCode = DroneControlUtil.formatPickupCode(code[0], code[1]);
```

## 错误处理

所有方法都包含完整的异常处理：

```java
try {
    Socket drone = droneControl.connectDrone("192.168.1.100", 61473);
    droneControl.setMissionCount(drone, 5, 2, 1, 2, 1);
} catch (IOException e) {
    log.error("无人机连接失败: {}", e.getMessage());
} catch (Exception e) {
    log.error("操作失败: {}", e.getMessage());
}
```

## 最佳实践

1. **总是关闭连接**
   ```java
   try {
       Socket drone = droneControl.connectDrone();
       // 使用连接...
   } finally {
       droneControl.closeConnection(drone);
   }
   ```

2. **使用参数验证**
   ```java
   if (DroneControlUtil.isValidSystemId(systemId) && 
       DroneControlUtil.isValidSlaveId(slaveId)) {
       // 执行操作
   }
   ```

3. **网络连接检查**
   ```java
   if (DroneControlUtil.isNetworkReachable("192.168.1.100", 61473, 5000)) {
       Socket drone = droneControl.connectDrone("192.168.1.100", 61473);
   }
   ```

4. **配置摘要记录**
   ```java
   String summary = DroneControlUtil.generateDeviceConfigSummary(
       droneHost, dronePort, containerHost, containerPort, systemId, slaveId);
   log.info("设备配置: {}", summary);
   ```

## 常见问题

1. **Q: 如何同时控制多个设备？**
   A: 使用不同的系统ID和从站ID参数，参考"多设备控制"示例。

2. **Q: 如何自定义Modbus地址？**
   A: 使用完整参数版本的方法，传入自定义地址参数。

3. **Q: 如何处理连接失败？**
   A: 工具类内置了5次重试机制，也可以使用`isNetworkReachable`预检查。

## 示例代码

完整的示例代码请参考 `DroneControlUtilExample.java` 文件。

## 版本信息

- 版本: 1.0.0
- 作者: 系统管理员
- 创建日期: 2025-07-25
- 支持的协议: MAVLink, Modbus TCP 