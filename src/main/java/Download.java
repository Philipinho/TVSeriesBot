import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import util.ReadProperty;

import java.io.IOException;

public class Download {
    public String fetchDownloadLink(String url){

        Document document = null;
        try {
            document = Jsoup.connect(url).userAgent(ReadProperty.getValue("useragent")).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements listviewElements = document.select("[data-role=listview]");

        StringBuilder sb = new StringBuilder();
        sb.append(listviewElements.select("a[href]").select("a[id=listD2]").attr("href"));

        return !sb.toString().isEmpty() ? sb.toString() : "Sorry, no download option available for this movie.";
    }
}
