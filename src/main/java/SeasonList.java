import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.ReadProperty;

import java.io.IOException;
import java.util.*;

public class SeasonList {
    public Map<String, String> fetchSeasonList(String movieLink){

        Document document = null;
        try {
            document = Jsoup.connect(movieLink).userAgent(ReadProperty.getValue("useragent")).get();
        } catch (IOException e){
            e.printStackTrace();
        }

        Elements listviewElements = document.select("[data-role=listview]");

        List<String> seasonLinksList = new ArrayList<>();

        Elements seasonLinksElements = listviewElements.first().select("a[href]");

        for (Element seasonLinks : seasonLinksElements){
            String linksOfSeason = ReadProperty.getValue("site") + seasonLinks.attr("href");
            if (!linksOfSeason.contains("appsraid.com")){
                seasonLinksList.add(linksOfSeason);
            }
        }

        List<String> seasonTitle = listviewElements.first().select("h3").eachText();

        //Season season = new Season(seasonTitle, seasonLinksList);

        Map<String, String> seasonResponse = new HashMap<>();

        Map<List<String>,List<String>> map = new HashMap<>();
    //    Collections.reverse(seasonTitle);
      //  Collections.reverse(seasonLinksList);

        map.put(seasonTitle,seasonLinksList);

        for (Map.Entry<List<String>,List<String>> seasons : map.entrySet()){

            for (int i =0;i<seasonTitle.size();i++){
                seasonResponse.put(seasons.getKey().get(i),seasons.getValue().get(i));
            }
        }

        return seasonResponse;
    }
}
