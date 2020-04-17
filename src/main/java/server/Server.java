package server;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import server.annotations.KbkPath;
import server.annotations.KbkWired;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Server {

    private static Map<Class<?>, Object> dependencies = new HashMap<>();
    static Map<String, Servlet> servlets = new HashMap<>();

    private static final String URL_BASE = "/kbk";
    private static final String SERVLET_PACKAGE = "application.servlet";

    public static void main(String[] args) throws Exception {
        log.info("Server initialization started");
        registerServlets();
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

    private static void registerServlets() throws Exception {
        log.info("Registering servlets from package: {}", SERVLET_PACKAGE);

        Reflections reflections = new Reflections(SERVLET_PACKAGE);
        for (Class<? extends Servlet> servletClass : reflections.getSubTypesOf(Servlet.class)) {
            Servlet servlet = createServletObject(servletClass);
            inject(servlet);
        }
        log.info("Servlets registered");
    }

    private static <T extends Servlet> T createServletObject(Class<? extends Servlet> servletClass) throws Exception {
        if (!servletClass.isAnnotationPresent(KbkPath.class)) {
            log.warn("No path annotation in servlet {}", servletClass.getName());
        }
        KbkPath annotation = servletClass.getAnnotation(KbkPath.class);
        String path = annotation.value();
        Servlet servletObject = servletClass.getConstructor().newInstance();
        servlets.put(URL_BASE + path, servletObject);
        log.info("Registered: {} for URI: {} ", servletObject.getClass().getName(), URL_BASE + path);
        return (T) servletObject;
    }

    private static void inject(Servlet servlet) throws Exception {
        Field[] fields = servlet.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(KbkWired.class) != null) {
                Class<?> dependencyClass = field.getType();
                Object dependency = dependencies.get(dependencyClass);
                if (dependency == null) {
                    dependency = dependencyClass.getConstructor().newInstance();
                    dependencies.put(dependencyClass, dependency);
                }
                field.setAccessible(true);
                field.set(servlet, dependency);
            }
        }
    }
}
