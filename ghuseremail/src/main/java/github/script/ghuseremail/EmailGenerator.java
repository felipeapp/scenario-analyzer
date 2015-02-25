package github.script.ghuseremail;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class EmailGenerator {

	public static String load(String system) {
		StringBuilder sbuilder = new StringBuilder();

		try {
			BufferedReader br = new BufferedReader(new FileReader("resources/" + system + "_message.template"));

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

		if (args.length != 1) {
			System.out.println("Usage: java <program> <system>");

			for (String str : args)
				System.out.println("\t" + str);

			System.exit(0);
		}

		String system = args[0];
		String message_template = load(system);
		BufferedReader br = new BufferedReader(new FileReader("resources/" + system + "_survey.data"));

		String str;
		while ((str = br.readLine()) != null) {
			String[] tokens = str.split(";");

			String form = tokens[0];
			String name = tokens[1];
			String login = tokens[2];
			String email = tokens[3];
			String url = tokens[4];

			PrintWriter pw = new PrintWriter("custom_messages/" + system + "/" + form + ".txt");

			String custom_msg = message_template
					.replaceAll("<email>", email)
					.replaceAll("<name>", name)
					.replaceAll("<login>", login)
					.replaceAll("<url>", url);

			pw.write(custom_msg);

			pw.close();
		}

		br.close();

	}

}
