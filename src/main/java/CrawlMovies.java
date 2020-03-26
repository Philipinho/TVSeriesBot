import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.ReadProperty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrawlMovies {


    public Map<String,String> SearchMovies(String search) {

        String searchUrl = ReadProperty.getValue("site")+ "/search/?q=";
        Document document = null;

        try {
            document = Jsoup.connect(searchUrl + search).userAgent(ReadProperty.getValue("useragent")).get();
        } catch (IOException e){
            e.printStackTrace();
        }

        Elements listviewElements = document.select("[data-role=listview]");

        List<String> movieLinkList = new ArrayList<>();
        List<String> movieTitleList = new ArrayList<>();


        for (Element listview : listviewElements){

            Elements movieLinks = listview.select("a[href]");

            for (Element links : movieLinks){
                String movieLink = links.attr("href");

                if (movieLink.startsWith("/id")){
                    movieLinkList.add(ReadProperty.getValue("site") + movieLink);
                }
            }

            List<String> title = listview.select("h3").eachText();

            for (String mTitle : title){
                movieTitleList.add(mTitle);
            }

            if (movieTitleList.contains("A-Z tv series List")){
                movieTitleList.remove("A-Z tv series List");
            }

            if (movieTitleList.contains("A-Z Movies List")){
                movieTitleList.remove("A-Z Movies List");
            }

            if (movieTitleList.contains("A-Z TV Series List")){
                movieTitleList.remove("A-Z TV Series List");
            }
        }

        Map<List<String>,List<String>> map = new HashMap<>();

        map.put(movieTitleList,movieLinkList);

        Map<String, String> movieResponse = new HashMap<>();


        for (Map.Entry<List<String>,List<String>> movieList : map.entrySet()){
            for (int i=0;i<movieTitleList.size();i++){
                movieResponse.put(movieList.getKey().get(i), movieList.getValue().get(i));
            }
        }

        return movieResponse;
    }

    public String fetchThumbnail(String url){
        Document document = null;
        StringBuilder sb = new StringBuilder();
        try {
            document = Jsoup.connect(url).userAgent(ReadProperty.getValue("useragent")).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements elements = document.select("img");

        for (Element element : elements){
            String images = element.attr("src");
            if (images.contains("https://src.foxscore.live/thumbnail/")){
                sb.append(images);
            }
        }
        return sb.toString();
    }


}
