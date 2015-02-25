package github.script.ghuseremail;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class GitHubUtil {

	private static final String client_id = "0053daf98ee1efe9a15d";
	private static final String client_secret = "04235bed384a591325d8735020aca5cee194605a";

	public static JsonReader getJsonReader(String url) {
		URL jurl = null;
		try {
			jurl = new URL(url + "?client_id=" + client_id + "&client_secret=" + client_secret);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return getJsonReader(jurl);
	}

	public static JsonReader getJsonReader(String url, int page) {
		URL jurl = null;
		try {
			jurl = new URL(url + "?client_id=" + client_id + "&client_secret=" + client_secret + "&page=" + page);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return getJsonReader(jurl);
	}

	public static JsonReader getJsonReader(URL url) {
		InputStream is = null;
		try {
			is = url.openStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JsonReader rdr = Json.createReader(is);

		return rdr;
	}
	
	public static String getStringProperty(JsonObject obj, String key) {
		if (!obj.containsKey(key))
			return null;
		
		if (obj.get(key) == JsonValue.NULL)
			return null;
		
		return obj.getString(key);
	}
	
	public static Integer getIntegerProperty(JsonObject obj, String key) {
		if (!obj.containsKey(key))
			return null;
		
		if (obj.get(key) == JsonValue.NULL)
			return null;
		
		return obj.getInt(key);
	}

}
