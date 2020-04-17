package application.servlet;

import application.HouseListProvider;
import application.model.House;
import framework.Request;
import framework.Servlet;
import framework.annotations.KoboldPath;

import java.util.Collection;

@KoboldPath("/HouseList")
public class HouseListServlet extends Servlet {

    @Override
    public Collection<House> respond(Request request) {
        return HouseListProvider.getHouseSet();
    }
}
