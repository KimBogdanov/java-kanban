package manager;

import Server.KVServer;
import models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.time.Instant;


import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private KVServer server;
    public void HttpTaskManager() {
        try {
            server = new KVServer();
            server.start();
        } catch (IOException e) {
            System.out.println("Ошибка при создании KVServer");
        }
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }
}