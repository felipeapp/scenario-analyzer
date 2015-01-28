package github.script.ghuseremail;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class EmailGenerator {

	public static final String MESSAGE_TEMPLATE = load();

	public static String load() {
		StringBuilder sbuilder = new StringBuilder();

		try {
			BufferedReader br = new BufferedReader(new FileReader("resources/message.template"));

			String str;
			while ((str = br.readLine()) != null) {
				sbuilder.append(str);
				sbuilder.append("\r\n");
			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sbuilder.toString();
	}
	
	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader("resources/survey.data"));
		
		String str;
		while ((str = br.readLine()) != null) {
			String[] tokens = str.split(";");
		
			String seq = tokens[0].length() == 1 ? "0" + tokens[0] : tokens[0];
			String name = tokens[1];
			String login = tokens[2];
			String email = tokens[3];
			String url = tokens[4];
			
			PrintWriter pw = new PrintWriter("custom_messages/" + seq + "_NettySurvey_" + login + ".txt");
			
			String custom_msg = MESSAGE_TEMPLATE.replaceAll("<email>", email)
				.replaceAll("<name>", name)
				.replaceAll("<login>", login)
				.replaceAll("<url>", url);
			
			pw.write(custom_msg);
			
			pw.close();
		}

		br.close();

	}

}
