package application.utils;

import application.model.House;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class HouseScraper {

    public static House scrapeHouse(String houseUrl) throws Exception {
        Document doc = Jsoup.connect(houseUrl).get();

        Integer price = getPrice(doc);
        String agency = getAgency(doc);
        Set<String> photos = getPhotos(doc);
        return new House(houseUrl, price, agency, photos);
    }

    private static Set<String> getPhotos(Document doc) {
        return doc.getElementsByClass("noselect")
                .stream()
                .flatMap(e -> e.childNodes()
                        .stream()
                        .filter(child -> !child.getClass().getName().contains("Text"))
                )
                .flatMap(e -> e.childNodes()
                        .stream()
                        .filter(child -> child.toString().contains("href")))
                .flatMap(e -> e.childNodes()
                        .stream()
                        .filter(child -> !child.getClass().getName().contains("Text"))
                )
                .map(e -> !e.attributes().get("src").isEmpty() ? e.attributes().get("src") : e.attributes().get("data-original"))
                .collect(Collectors.toSet());
    }

    private static String getAgency(Document doc) {
        try {
            return doc.getElementsByClass("agentie grey-medium").get(0).childNodes().get(1).childNodes().get(0).attributes().get("text");
        } catch (Exception ex) {
            return "FAILED TO GET AGENCY";
        }
    }

    private static Integer getPrice(Document doc) {
        String priceText = doc.getElementsByClass("pret first blue").get(0).childNodes().get(1).attributes().get("text");
        priceText = priceText.trim().replaceAll("\\.", "");
        try {
            return Integer.parseInt(priceText);
        } catch (Exception ex) {
            log.warn("Failed to parse price: {}" + priceText);
            return null;
        }
    }
}
