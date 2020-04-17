package application;

import application.model.House;
import lombok.Getter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class HouseSetKeeper {

    @Getter
    private final Set<House> houseSet = new HashSet<>();

    private final FileWriter fw;

    public HouseSetKeeper() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream resourceAsStream = classLoader.getResourceAsStream("config.properties");

        Properties prop = new Properties();
        prop.load(resourceAsStream);
        fw = new FileWriter(prop.getProperty("saved-urls"), true);
    }

    public void persistHouse(House h) throws IOException {
        boolean isNew = houseSet.add(h);
        if (isNew) {
            fw.append(h.getUrl());
            fw.append("\n");
            fw.close();
        }
    }
}
