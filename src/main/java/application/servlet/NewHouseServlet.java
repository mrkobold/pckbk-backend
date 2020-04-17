package application.servlet;

import application.HouseSetKeeper;
import application.model.House;
import application.utils.HouseScraper;
import server.Request;
import server.Servlet;
import server.annotations.KbkPath;
import server.annotations.KbkWired;

@KbkPath("/NewHouse")
public class NewHouseServlet extends Servlet {

    @KbkWired
    private HouseSetKeeper houseSetKeeper;

    @Override
    public String respond(Request request) throws Exception {
        String houseUrl = request.getParameters().get("houseUrl");
        House h = HouseScraper.scrapeHouse(houseUrl);

        houseSetKeeper.persistHouse(h);
        return "Successfully added new house";
    }
}
