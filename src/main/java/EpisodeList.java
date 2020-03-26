import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.ReadProperty;

import java.io.IOException;
import java.util.*;

public class EpisodeList {

    public Map<String, String> fetchEpisodes(String url){

        Document document = null;
        try {
            document = Jsoup.connect(url).userAgent(ReadProperty.getValue("useragent")).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String paginated = url+ "?page=";

        int lastPage = 1;
        List<String> paginationList = null;

        Elements getPagination = document.getElementsByClass("pagination").select("a");
       if (!getPagination.isEmpty()){
           paginationList = getPagination.eachText();
           lastPage = Integer.parseInt(paginationList.get(paginationList.size()-1));
       }

        List<String> episodeTitleList = new ArrayList<>();
        List<String> episodeLinkList = new ArrayList<>();

        for (int i =1;i<=lastPage;i++){
            String page = paginated+i;

            Document doc = null;
            try {
                doc = Jsoup.connect(page).userAgent(ReadProperty.getValue("useragent")).get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Elements listviewElements = doc.select("[data-role=listview]");

            List<String> list = listviewElements.first().select("a[href]").eachText();

            for (String titles : list){
                episodeTitleList.add(titles);
            }

           Elements linksElement = listviewElements.first().select("a[href]");

            for (Element links : linksElement){
                String epLinks = links.attr("href");
                if (epLinks.contains("/download/")) {
                    episodeLinkList.add(ReadProperty.getValue("site") + epLinks);
                }
            }
        }

        Map<List<String>,List<String>> map = new HashMap<>();

        map.put(episodeTitleList,episodeLinkList);

        Map<String, String> episodeResponse = new HashMap<>();

        for (Map.Entry<List<String>,List<String>> episodeList : map.entrySet()){
            for (int i=0;i<episodeTitleList.size();i++){
                episodeResponse.put(episodeList.getKey().get(i), episodeList.getValue().get(i));
            }
        }
        return episodeResponse;
    }
}