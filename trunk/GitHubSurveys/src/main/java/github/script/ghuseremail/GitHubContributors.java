package github.script.ghuseremail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class GitHubContributors {

	public static void main(String[] args) throws IOException {

		String github_url_contributors = "https://api.github.com/repos/apache/wicket/contributors";

		List<JsonObject> users_with_email = new ArrayList<JsonObject>();
		
		int page = 1;
		int order = 1;

		while (true) {

			JsonReader r_contributors = GitHubUtil.getJsonReader(github_url_contributors, page);
			JsonArray results = r_contributors.readArray();

			if (results.size() == 0) {
				System.out.println("Breaking on page " + page);
				break;
			}

			for (JsonObject result : results.getValuesAs(JsonObject.class)) {
				
				JsonReader r_user = GitHubUtil.getJsonReader(result.getString("url"));
				JsonObject user = r_user.readObject();
				
				int commits = GitHubUtil.getIntegerProperty(result, "contributions");
				
				try {
					String name = GitHubUtil.getStringProperty(user, "name");
					String login = GitHubUtil.getStringProperty(user, "login");
					String email = GitHubUtil.getStringProperty(user, "email");
				
					if (email != null && !email.isEmpty())
						users_with_email.add(user);
					
					System.out.printf("%d;%d;%s;%s;%s\n", order++, commits, name, login, email);
				} catch (Exception e) {
					System.err.println(result.getString("url"));
					e.printStackTrace();
				}
				
				r_user.close();
			}

			r_contributors.close();
			++page;

		}
		
		System.out.println("--------------------------------------------");
		
		int i = 0;
		for (JsonObject u : users_with_email) {
			String name = GitHubUtil.getStringProperty(u, "name");
			String login = GitHubUtil.getStringProperty(u, "login");
			String email = GitHubUtil.getStringProperty(u, "email");
			
			System.out.printf("%d_WicketSurvey_%s;%s;%s;%s\n", ++i, login, name, login, email);
		}

	}

}
