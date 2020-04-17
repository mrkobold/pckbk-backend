package application.servlet;

import application.HouseSetKeeper;
import application.model.House;
import server.Request;
import server.Servlet;
import server.annotations.KbkPath;
import server.annotations.KbkWired;

import java.util.Collection;

@KbkPath("/HouseList")
public class HouseListServlet extends Servlet {

    @KbkWired
    private HouseSetKeeper houseSetKeeper;

    @Override
    public Collection<House> respond(Request request) {
        return houseSetKeeper.getHouseSet();
    }
}
