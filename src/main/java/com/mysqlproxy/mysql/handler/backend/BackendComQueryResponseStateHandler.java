package com.mysqlproxy.mysql.handler.backend;

import com.mysqlproxy.buffer.MyByteBuff;
import com.mysqlproxy.mysql.BackendMysqlConnection;
import com.mysqlproxy.mysql.FrontendMysqlConnection;
import com.mysqlproxy.mysql.MysqlConnection;
import com.mysqlproxy.mysql.handler.StateHandler;
import com.mysqlproxy.mysql.state.ComQueryResponseColumnDefState;
import com.mysqlproxy.mysql.state.ComQueryResponseState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class BackendComQueryResponseStateHandler implements StateHandler {
    private Logger logger = LoggerFactory.getLogger(BackendComQueryResponseStateHandler.class);

    public static final BackendComQueryResponseStateHandler INSTANCE = new BackendComQueryResponseStateHandler();

    private BackendComQueryResponseStateHandler() {
    }

    @Override
    public void handle(MysqlConnection connection, Object o) {
        try {
            logger.info("后端收到COM_QUERY_RESPONSE包");
            int resultSetPos = 0;
            BackendMysqlConnection backendMysqlConnection = (BackendMysqlConnection) connection;
            MyByteBuff myByteBuff = backendMysqlConnection.read();
            if (myByteBuff.getReadableBytes() >= 3) {
                int fieldCountPacketLen = (int) myByteBuff.getFixLenthInteger(resultSetPos, 3);
                if (myByteBuff.getReadableBytes() >= fieldCountPacketLen + 4) {
                    //第一个包完整，进入下一状态
                    connection.setPacketScanPos(fieldCountPacketLen + 4);
                    connection.setState(ComQueryResponseColumnDefState.INSTANCE);
                    connection.drive(myByteBuff);
                }
            }
        } catch (IOException e) {
            //TODO 处理异常
            e.printStackTrace();
        }
    }
}