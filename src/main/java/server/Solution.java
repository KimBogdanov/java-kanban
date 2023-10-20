package server;

import java.io.IOException;

public class Solution {
        public static void main(String[] args) throws  IOException {
            KVServer kvServer = new KVServer();
            kvServer.start();

            HttpTaskServer httpTaskServer = new HttpTaskServer();
            httpTaskServer.start();
    }
}
