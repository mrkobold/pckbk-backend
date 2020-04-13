package framework;

import framework.serialization.Serializator;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import static framework.Constants.HEADER;

@Slf4j
public class ServletRunnable implements Runnable {

    private final Socket socket;

    ServletRunnable(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            String requestString = readRequest(socket.getInputStream());
            Request request = new Request(requestString);
            String response = respond(request);
            writeResponse(response);

        } catch (Exception ex) {
            log.warn("Problem during ServletRunnable's life: {}", ex);
        }
    }

    private void writeResponse(String response) throws IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(response);
        writer.close();
    }

    private static String readRequest(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[4_000];
        int length = inputStream.read(buffer);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append((char) buffer[i]);
        }
        return builder.toString();
    }

    private static String respond(Request request) {
        Servlet servlet = Server.servlets.get(request.getUri().split("\\?")[0]);
        Object response = null;
        if (servlet == null) {
            response = HEADER;
        } else {
            try {
                response = servlet.respond(request);
            } catch (Exception ignore) {

            }
        }
        String serializedResponse = Serializator.serialize(response);
        return HEADER + serializedResponse;
    }
}
