package cn.iocoder.yudao.module.drone.common.util;

import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusProtocolException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;

import java.net.InetAddress;

public class JLibModbusUtils {
    private String ip;
    private int port;
    private int slaveId;
    private ModbusMaster master;

    public enum ResultCode {
        SUCCESS, TIMEOUT, ERROR, WRONG_STATE, NOT_ALLOWED, ALREADY_DONE
    }

    public JLibModbusUtils(String ip, int port, int slaveId) throws Exception {
        this.ip = ip;
        this.port = port;
        this.slaveId = slaveId;
        TcpParameters tcpParameters = new TcpParameters();
        tcpParameters.setHost(InetAddress.getByName(ip));
        tcpParameters.setPort(port);
        tcpParameters.setKeepAlive(true);
        master = ModbusMasterFactory.createModbusMasterTCP(tcpParameters);
        Modbus.setAutoIncrementTransactionId(true);
    }

    private void connect() throws Exception {
        if (!master.isConnected()) {
            master.connect();
        }
    }

    private void disconnect() {
        try {
            if (master.isConnected()) {
                master.disconnect();
            }
        } catch (Exception ignored) {}
    }

    // 检查是否自动模式
    public ResultCode ensureAutoMode() throws Exception {
        connect();
        int[] mode = master.readInputRegisters(slaveId, 0xBCD, 1);
        if (mode[0] == 12) return ResultCode.SUCCESS;
        master.writeSingleRegister(slaveId, 0xBCC, 0x0A);
        for (int i = 0; i < 10; i++) {
            Thread.sleep(500);
            int[] check = master.readInputRegisters(slaveId, 0xBCD, 1);
            if (check[0] == 12) return ResultCode.SUCCESS;
        }
        return ResultCode.TIMEOUT;
    }

    // 开舱门
    public ResultCode openDoor() throws Exception {
        connect();
        master.writeSingleRegister(slaveId, 0xBB8, 0x0A);
        for (int i = 0; i < 10; i++) {
            Thread.sleep(500);
            int[] state = master.readInputRegisters(slaveId, 0xBB8, 1);
            if (state[0] == 11) return ResultCode.SUCCESS;
        }
        return ResultCode.TIMEOUT;
    }

    // 关舱门
    public ResultCode closeDoor() throws Exception {
        connect();
        master.writeSingleRegister(slaveId, 0xBB8, 0x14);
        for (int i = 0; i < 10; i++) {
            Thread.sleep(500);
            int[] state = master.readInputRegisters(slaveId, 0xBB8, 1);
            if (state[0] == 21) return ResultCode.SUCCESS;
        }
        return ResultCode.TIMEOUT;
    }

    // 停机坪有飞机确认
    public ResultCode confirmDroneArrived() throws Exception {
        connect();
        master.writeSingleRegister(slaveId, 0xBB9, 0x0A);
        for (int i = 0; i < 10; i++) {
            Thread.sleep(500);
            int[] state = master.readInputRegisters(slaveId, 0xBB9, 1);
            if (state[0] == 11) return ResultCode.SUCCESS;
        }
        return ResultCode.TIMEOUT;
    }

    // 停机坪无飞机确认
    public ResultCode confirmDroneLeft() throws Exception {
        connect();
        master.writeSingleRegister(slaveId, 0xBB9, 0x14);
        for (int i = 0; i < 10; i++) {
            Thread.sleep(500);
            int[] state = master.readInputRegisters(slaveId, 0xBB9, 1);
            if (state[0] == 21) return ResultCode.SUCCESS;
        }
        return ResultCode.TIMEOUT;
    }

    // 检查无人机存件格口状态
    public boolean canStorePackage() throws Exception {
        connect();
        int[] state = master.readInputRegisters(slaveId, 0xBBE, 1);
        return state[0] == 11;
    }

    // 无人机包裹暂存
    public ResultCode droneStorePackage() throws Exception {
        connect();
        master.writeSingleRegister(slaveId, 0xBBA, 110);
        for (int i = 0; i < 10; i++) {
            Thread.sleep(500);
            int[] state = master.readInputRegisters(slaveId, 0xBBA, 1);
            if (state[0] == 111) return ResultCode.SUCCESS;
        }
        return ResultCode.TIMEOUT;
    }

    // 舵机开
    public ResultCode openServo() throws Exception {
        connect();
        int[] ready = master.readInputRegisters(slaveId, 0xBBB, 1);
        if (ready[0] != 1) return ResultCode.NOT_ALLOWED;
        master.writeSingleRegister(slaveId, 0xBBB, 10);
        for (int i = 0; i < 10; i++) {
            Thread.sleep(500);
            int[] state = master.readInputRegisters(slaveId, 0xBBB, 1);
            if (state[0] == 11) return ResultCode.SUCCESS;
        }
        return ResultCode.TIMEOUT;
    }

    // 舵机关
    public ResultCode closeServo() throws Exception {
        connect();
        int[] ready = master.readInputRegisters(slaveId, 0xBBB, 1);
        if (ready[0] != 2) return ResultCode.NOT_ALLOWED;
        master.writeSingleRegister(slaveId, 0xBBB, 20);
        for (int i = 0; i < 10; i++) {
            Thread.sleep(500);
            int[] state = master.readInputRegisters(slaveId, 0xBBB, 1);
            if (state[0] == 21) return ResultCode.SUCCESS;
        }
        return ResultCode.TIMEOUT;
    }

    // 无人机取件
    public ResultCode dronePickupPackage() throws Exception {
        connect();
        for (int i = 0; i < 10; i++) {
            Thread.sleep(500);
            int[] state = master.readInputRegisters(slaveId, 0xBBA, 1);
            if (state[0] == 121) return ResultCode.SUCCESS;
            if (state[0] == 120) continue;
        }
        return ResultCode.TIMEOUT;
    }

    // 用户取件（格口号方式）
    public ResultCode userPickupByBox(int boxNo) throws Exception {
        connect();
        master.writeSingleRegister(slaveId, 0xBC3, 210);
        master.writeSingleRegister(slaveId, 0xBC2, boxNo);
        for (int i = 0; i < 10; i++) {
            Thread.sleep(500);
            int[] state = master.readInputRegisters(slaveId, 0xBC3, 1);
            if (state[0] == 211) return ResultCode.SUCCESS;
        }
        return ResultCode.TIMEOUT;
    }

    // 用户取件（取件码方式）
    public ResultCode userPickupByCode(int code) throws Exception {
        connect();
        master.writeSingleRegister(slaveId, 0xBC3, code);
        for (int i = 0; i < 10; i++) {
            Thread.sleep(500);
            int[] state = master.readInputRegisters(slaveId, 0xBC3, 1);
            if (state[0] == 211) return ResultCode.SUCCESS;
        }
        return ResultCode.TIMEOUT;
    }

    // 空包裹回收（远程）
    public ResultCode remoteRecycleStep1() throws Exception {
        connect();
        master.writeSingleRegister(slaveId, 0xBC4, 210);
        for (int i = 0; i < 10; i++) {
            Thread.sleep(500);
            int[] state = master.readInputRegisters(slaveId, 0xBC4, 1);
            if (state[0] == 211) return ResultCode.SUCCESS;
        }
        return ResultCode.TIMEOUT;
    }
    public ResultCode remoteRecycleStep2() throws Exception {
        connect();
        master.writeSingleRegister(slaveId, 0xBC5, 210);
        for (int i = 0; i < 10; i++) {
            Thread.sleep(500);
            int[] state = master.readInputRegisters(slaveId, 0xBC5, 1);
            if (state[0] == 211) return ResultCode.SUCCESS;
        }
        return ResultCode.TIMEOUT;
    }

    // 用户寄件（取空包裹）
    public ResultCode userPickupEmptyBox() throws Exception {
        connect();
        master.writeSingleRegister(slaveId, 0xBC5, 110);
        for (int i = 0; i < 10; i++) {
            Thread.sleep(500);
            int[] state = master.readInputRegisters(slaveId, 0xBC5, 1);
            if (state[0] == 111) return ResultCode.SUCCESS;
        }
        return ResultCode.TIMEOUT;
    }

    // 用户寄件（远程暂存）
    public ResultCode remoteDeposit() throws Exception {
        connect();
        master.writeSingleRegister(slaveId, 0xBC7, 210);
        for (int i = 0; i < 10; i++) {
            Thread.sleep(500);
            int[] state = master.readInputRegisters(slaveId, 0xBC7, 1);
            if (state[0] == 211) return ResultCode.SUCCESS;
        }
        return ResultCode.TIMEOUT;
    }

    // 检查寄件格口状态
    public boolean canDeposit() throws Exception {
        connect();
        int[] state = master.readInputRegisters(slaveId, 0xBD2, 1);
        return state[0] == 10;
    }

    // 断开连接（可选）
    public void close() {
        disconnect();
    }
} 