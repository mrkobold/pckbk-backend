package application.servlet;

import application.HouseSetKeeper;
import application.model.House;
import application.utils.HouseScraper;
import server.Request;
import server.Servlet;
import server.annotations.KoboldPath;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@KoboldPath("/NewHouse")
public class NewHouseServlet extends Servlet {

    private static final String CONFIG = "config.properties";

    @Override
    public String respond(Request request) throws Exception {
        String houseUrl = request.getParameters().get("houseUrl");
        House h = HouseScraper.scrapeHouse(houseUrl);

        saveHouseUrl(houseUrl);

        HouseSetKeeper.addHouse(h);
        return "Successfully added new house";
    }

    private void saveHouseUrl(String houseUrl) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream resourceAsStream = classLoader.getResourceAsStream(CONFIG);

        Properties prop = new Properties();
        prop.load(resourceAsStream);

        FileWriter fw = new FileWriter(prop.getProperty("saved-urls"), true);
        fw.append(houseUrl);
        fw.append("\n");
        fw.close();
    }
}
