package application.servlet;

import application.HouseSetKeeper;
import application.model.House;
import server.Request;
import server.Servlet;
import server.annotations.KoboldPath;

import java.util.Collection;

@KoboldPath("/HouseList")
public class HouseListServlet extends Servlet {

    @Override
    public Collection<House> respond(Request request) {
        return HouseSetKeeper.getHouseSet();
    }
}
