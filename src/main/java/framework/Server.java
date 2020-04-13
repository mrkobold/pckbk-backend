package framework;

import framework.annotations.KoboldPath;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class Server {

    static Map<String, Servlet> servlets = new HashMap<>();

    private static final String URL_BASE = "/kbk";

    public static void main(String[] args) throws Exception {
        log.info("Server initialization started");
        registerServlets("target/classes/application/servlet/");
        runServer();
    }

    private static void runServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        log.info("Server started, listening on port 8080...");

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(new ServletRunnable(socket)).start();
        }
    }

    private static void registerServlets(String servletPath) throws Exception {
        log.info("Registering servlets from folder: {}", servletPath);

        String[] servletFileNames = new File(servletPath).list();

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Set<String> servletNames = Arrays.stream(servletFileNames).map(s -> s.split("\\.")[0]).collect(Collectors.toSet());
        String servletPackage = "application.servlet.";

        for (String servletName : servletNames) {
            Class<?> clazz = loader.loadClass(servletPackage + servletName);
            if (!clazz.isAnnotationPresent(KoboldPath.class)) {
                log.warn("No path annotation in servlet {}", servletName);
            }
            KoboldPath annotation = clazz.getAnnotation(KoboldPath.class);
            String path = annotation.value();
            Object servletObject = clazz.getConstructor().newInstance();

            servlets.put(URL_BASE + path, (Servlet) servletObject);
            log.info("Registered: {} for URI: {} ", servletObject.getClass().getName(), URL_BASE + path);
        }

        log.info("Servlets registered");
    }
}
