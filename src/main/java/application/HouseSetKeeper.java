package application;

import application.model.House;
import application.utils.HouseScraper;
import lombok.Getter;

import java.io.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class HouseSetKeeper {

    @Getter
    private final Set<House> houseSet = new HashSet<>();

    private final FileWriter fw;

    public HouseSetKeeper() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream resourceAsStream = classLoader.getResourceAsStream("config.properties");

        Properties prop = new Properties();
        prop.load(resourceAsStream);
        String savedUrlFile = prop.getProperty("saved-urls");

        this.fw = new FileWriter(savedUrlFile, true);
        loadHousesFromFile(savedUrlFile);
    }

    private void loadHousesFromFile(String file) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        new Thread(() -> {
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    House h = HouseScraper.scrapeHouse(line);
                    synchronized (houseSet) {
                        houseSet.add(h);
                    }
                }
            } catch (Exception ignore) {
            }
        }).start();
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
