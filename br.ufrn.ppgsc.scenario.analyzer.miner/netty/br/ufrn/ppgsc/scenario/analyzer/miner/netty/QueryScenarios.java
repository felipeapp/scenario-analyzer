package br.ufrn.ppgsc.scenario.analyzer.miner.netty;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class QueryScenarios {

	private Connection connection;
	private String database;

	public QueryScenarios(String database) {
		this.database = database;
		
		try {
			connection = DriverManager.getConnection(
					"jdbc:postgresql://localhost:5432/" + database,
					"scenario_analyzer_user", "123456");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getDatabase() {
		return database;
	}

	public double getTime(String name) {
		if (connection == null || name == null || name.isEmpty())
			return -2;

		double average = -1;

		try {
			PreparedStatement stmt = connection
					.prepareStatement("select node.member member, avg(node.time) average from scenario inner join node"
							+ " on scenario.root_id = node.id"
							+ " where scenario.name = ?"
							+ " group by node.member");

			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();

			if (rs.next())
				average = rs.getDouble("average");

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return average;
	}

	public static void main(String[] args) {
		String databases[] = {
				"netty_4.0.0.Final_testsuite",
				"netty_4.0.6.Final_testsuite",
				"netty_4.0.10.Final_testsuite",
				"netty_4.0.15.Final_testsuite",
				"netty_4.0.17.Final_testsuite",
				"netty_4.0.18.Final_testsuite",
				"netty_4.0.21.Final_testsuite"
		};
		
		String entry_points[] = {
			"Entry point for DatagramUnicastTest.testSimpleSendWithoutBind",
			"Entry point for SocketBufReleaseTest.testBufRelease",
			"Entry point for SocketEchoTest.testSimpleEcho",
			"Entry point for SocketEchoTest.testSimpleEchoWithAdditionalExecutor",
			"Entry point for SocketEchoTest.testSimpleEchoWithAdditionalExecutorAndVoidPromise",
			"Entry point for SocketEchoTest.testSimpleEchoWithVoidPromise",
			"Entry point for SocketFileRegionTest.testFileRegion",
			"Entry point for SocketFileRegionTest.testFileRegionVoidPromise",
			"Entry point for SocketFileRegionTest.testFileRegionNotAutoRead",
			"Entry point for SocketFixedLengthEchoTest.testFixedLengthEcho",
			"Entry point for SocketObjectEchoTest.testObjectEcho",
			"Entry point for SocketShutdownOutputByPeerTest.testShutdownOutput",
			"Entry point for SocketShutdownOutputByPeerTest.testShutdownOutputWithoutOption",
			"Entry point for SocketShutdownOutputBySelfTest.testShutdownOutput",
			"Entry point for SocketSpdyEchoTest.testSpdyEcho",
			"Entry point for SocketSslEchoTest.testSslEcho",
			"Entry point for SocketSslEchoTest.testSslEchoWithChunkHandler",
			"Entry point for SocketStartTlsTest.testStartTls",
			"Entry point for SocketStringEchoTest.testStringEcho",
			"Entry point for WriteBeforeRegisteredTest.testWriteBeforeConnect"
		};
		
		Map<String, List<Double>> table = new HashMap<String, List<Double>>();
		
		for (String db : databases) {
			QueryScenarios query = new QueryScenarios(db);
			
			for (String name : entry_points) {
				double average = query.getTime(name);
				
				List<Double> list = table.get(name);
				
				if (list == null) {
					list = new ArrayList<Double>();
					table.put(name, list);
				}
				
				list.add(average);
			}
		}
		
		StringBuilder sb = new StringBuilder();
		
		
		
		for (String name : new TreeSet<String>(table.keySet())) {
			sb.append(name);
			sb.append(";");
			
			for (double average : table.get(name)) {
				sb.append(average);
				sb.append(";");
			}
			
			sb.append("\n");
		}
		
		System.out.println(sb);
	}

}
