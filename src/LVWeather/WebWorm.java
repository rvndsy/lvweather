package LVWeather;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class WebWorm {
	// static String baseUrl;
	
	static Document getPage(int speed, String url) {
		System.out.println("GET: " +url);
		Document doc = null;
		try {
			pause(speed);
			String randomUA = "Internet Explorer 13.0 Crawling snail";
			String contentType = "application/json";
			return Jsoup
					.connect(url)
					.timeout(20000)
					.header("content-type", contentType)
					.header("cookie", "thing")
					.userAgent(randomUA)
					.ignoreContentType(true)
					.execute()
					.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	static Document getPage(String url) {
		return getPage(250, url);
	}
	
	static String postPage(String url, String payload) {
		System.out.println("POST: " +url);
		try {
			pause(1000);
			return Jsoup.connect(url)
				.timeout(10000)
				.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
				.header("Accept", "application/json, text/javascript, */*; q=0.01")
				.userAgent("Whatever we get")
				.ignoreContentType(true)
				.requestBody(payload)
				.post()
				.body()
				.text();
		} catch (Exception e) {
			System.out.println("! postPage() Error on " +url);
			e.printStackTrace();
		}
		return null;
	}
	static void pause(int millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {}
	}
	
	static JSONArray getPageJSONArray(int speed, String url) {
		Document data = getPage(speed, url);
		JSONArray jsonArr = new JSONArray(data.text());
		return jsonArr;
	}
	
	static JSONArray getPageJSONArray(String url) {
		Document data = getPage(250, url);
		JSONArray jsonArr = new JSONArray(data.text());
		return jsonArr;
	}
}
