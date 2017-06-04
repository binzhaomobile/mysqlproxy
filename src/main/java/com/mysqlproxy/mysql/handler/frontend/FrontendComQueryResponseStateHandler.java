package com.mysqlproxy.mysql.handler.frontend;

import com.mysqlproxy.buffer.MyByteBuff;
import com.mysqlproxy.mysql.BackendMysqlConnection;
import com.mysqlproxy.mysql.FrontendMysqlConnection;
import com.mysqlproxy.mysql.MysqlConnection;
import com.mysqlproxy.mysql.handler.StateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class FrontendComQueryResponseStateHandler implements StateHandler {
    private Logger logger = LoggerFactory.getLogger(FrontendComQueryResponseStateHandler.class);

    public static final FrontendComQueryResponseStateHandler INSTANCE = new FrontendComQueryResponseStateHandler();

    private FrontendComQueryResponseStateHandler() {
    }

    @Override
    public void handle(MysqlConnection connection, Object o) {
        FrontendMysqlConnection frontendMysqlConnection = (FrontendMysqlConnection) connection;
        BackendMysqlConnection backendMysqlConnection = (BackendMysqlConnection) ((FrontendMysqlConnection) connection).getBackendMysqlConnection();
        try {
            if (frontendMysqlConnection.getDirectTransferPacketWriteLen() != 0 &&
                    frontendMysqlConnection.isDirectTransferComplete()) {
                frontendMysqlConnection.disableWrite();
            } else {
                if (!frontendMysqlConnection.canWrite()) {
                    frontendMysqlConnection.enableWrite();
                    return;
                }
                logger.debug("前端连接向客户端发送COM_QUERY_RESPONSE");
                MyByteBuff myByteBuff = connection.getWriteBuffer();
                frontendMysqlConnection.writeInDirectTransferMode(myByteBuff);
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
