package framework;

import java.io.InputStream;
import java.util.Properties;

final class Constants {
    static final String HEADER = "HTTP/1.1 200 OK\r\n\r\n";
    private static final String CONFIG = "config.properties";


    public static Properties PROPS = new Properties();
    static {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream resourceAsStream = classLoader.getResourceAsStream(CONFIG);

        Properties prop = new Properties();
        try {
            prop.load(resourceAsStream);
        } catch (Exception ignore) {
        }
    }
}
