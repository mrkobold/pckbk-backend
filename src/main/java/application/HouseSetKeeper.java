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

    private final PrintWriter pw;

    public HouseSetKeeper() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream resourceAsStream = classLoader.getResourceAsStream("config.properties");

        Properties prop = new Properties();
        prop.load(resourceAsStream);
        String savedUrlFile = prop.getProperty("saved-urls");

        BufferedWriter bw = new BufferedWriter(new FileWriter(Thread.currentThread().getContextClassLoader().getResource(savedUrlFile).getPath(), true));
        pw = new PrintWriter(bw);

        loadHousesFromFile(savedUrlFile);
    }

    private void loadHousesFromFile(String file) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(file)));

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

    public void persistHouse(House h) {
        boolean isNew = houseSet.add(h);
        if (isNew) {
            pw.append(h.getUrl());
            pw.append("\n");
            pw.flush();
        }
    }
}
