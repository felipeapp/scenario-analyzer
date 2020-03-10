package query;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Complement {

	private Connection connection;
	private String database;

	public Complement(String database) {
		this.database = database;

		try {
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + database,
					"scenario_analyzer_user", "123456");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getDatabase() {
		return database;
	}

	public Map<String, Double> getTime(Set<String> signatures) {
		if (connection == null || signatures == null || signatures.isEmpty())
			return null;

		StringBuilder sb = new StringBuilder();
		for (String s : signatures)
			sb.append("'" + s + "',");
		sb.deleteCharAt(sb.length() - 1);

		Map<String, Double> map = new HashMap<String, Double>();

		try {
			Statement stmt = connection.createStatement();

			ResultSet rs = stmt.executeQuery(
					"select node.member signature, avg(node.real_time) average from node where node.time <> -1 and node.real_time > 0"
							+ " and node.member in (" + sb.toString() + ") group by node.member order by average desc");

			while (rs.next())
				map.put(rs.getString("signature"), rs.getDouble("average"));

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return map;
	}

	public static void main(String[] args) throws IOException {
		String databases[] = { "wicket_6.18.0", "wicket_7.0.0-M1" };

		Map<String, Integer> map_samples = new HashMap<String, Integer>();

		Complement query1 = new Complement(databases[0]);
		Complement query2 = new Complement(databases[1]);

		BufferedReader br = new BufferedReader(new FileReader("src/query/data.txt"));

		String line;
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split(";");
			map_samples.put(tokens[0], Integer.parseInt(tokens[5]));
		}

		System.out.println(map_samples.size());
		br.close();

		Map<String, Double> mapavg1 = query1.getTime(map_samples.keySet());
		System.out.println(mapavg1.size());
		Map<String, Double> mapavg2 = query2.getTime(map_samples.keySet());
		System.out.println(mapavg2.size());

		for (String s : map_samples.keySet()) {
			double avg1 = mapavg1.get(s) == null ? 0 : mapavg1.get(s);
			double avg2 = mapavg2.get(s) == null ? 0 : mapavg2.get(s);
			int samples = map_samples.get(s);

			System.out.println(s + ";" + avg1 + ";" + avg2 + ";" + (avg2 - avg1) + ";" + samples / 10.0 + ";"
					+ (avg2 - avg1) * samples / 10.0);
		}

	}

}
