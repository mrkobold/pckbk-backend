package application.model;

import lombok.Data;

import java.util.Set;

@Data
public class House {
    private final String url;
    private final Integer price;
    private final String agency;
    private final Set<String> photos;
}
