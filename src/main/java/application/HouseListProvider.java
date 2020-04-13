package application;

import application.model.House;

import java.util.HashSet;
import java.util.Set;

public class HouseListProvider {

    private static final Set<House> HOUSE_SET = new HashSet<>();

    public static void addHouse(House house) {
        HOUSE_SET.add(house);
    }

    public static Set<House> getHouseSet() {
        return HOUSE_SET;
    }
}
