package br.ufrn.ppgsc.scenario.analyzer.miner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AnalyzerMinerJoinCSV {
	
	private static final String[][] DEGRADED_TARGETS = {
		{
			"argouml/argouml_tests_2015-02-18_23h03min/repository_mining/argouml_pu_r_degraded_method_commits_2015-02-18_23h03min.txt"
		},
		{
			"netty/3_netty-4.0.[0-6].Final/repository_mining/netty-4.0.[0-6].Final_pu_r_degraded_method_commits_2015-02-12_07h55min.txt",
			"netty/3_netty-4.0.[6-10].Final/repository_mining/netty-4.0.[6-10].Final_pu_r_degraded_method_commits_2015-02-12_08h39min.txt",
			"netty/3_netty-4.0.[10-15].Final/repository_mining/netty-4.0.[10-15].Final_pu_r_degraded_method_commits_2015-02-12_08h59min.txt",
			"netty/3_netty-4.0.[15-17].Final/repository_mining/netty-4.0.[15-17].Final_pu_r_degraded_method_commits_2015-02-12_10h15min.txt",
			"netty/3_netty-4.0.[17-18].Final/repository_mining/netty-4.0.[17-18].Final_pu_r_degraded_method_commits_2015-02-12_13h14min.txt",
			"netty/3_netty-4.0.[18-21].Final/repository_mining/netty-4.0.[18-21].Final_pu_r_degraded_method_commits_2015-02-12_15h40min.txt"
		},
		{
			"jetty/jetty_9.2.6_x_9.2.7/repository_mining/jetty_9.2.6_x_9.2.7_pu_r_degraded_method_commits_2015-03-17_16h34min.txt",
			"jetty/jetty_9.2.7_x_9.2.8/repository_mining/jetty_9.2.7_x_9.2.8_pu_r_degraded_method_commits_2015-03-17_16h51min.txt",
			"jetty/jetty_9.2.8_x_9.2.9/repository_mining/jetty_9.2.8_x_9.2.9_pu_r_degraded_method_commits_2015-03-17_16h54min.txt",
			"jetty/jetty_9.2.9_x_9.2.10/repository_mining/jetty_9.2.9_x_9.2.10_pu_r_degraded_method_commits_2015-03-17_16h59min.txt",
			"jetty/jetty_9.2.10_x_9.3.0.M0/repository_mining/jetty_9.2.10_x_9.3.0.M0_pu_r_degraded_method_commits_2015-03-17_17h04min.txt",
			"jetty/jetty_9.3.0.M0_x_9.3.0.M1/repository_mining/jetty_9.3.0.M0_x_9.3.0.M1_pu_r_degraded_method_commits_2015-03-17_17h16min.txt"
		},
		{
			"wicket/3_wicket-6.[15x16].0/repository_mining/wicket-6.[15x16].0_pu_r_degraded_method_commits_2015-02-13_11h33min.txt",
			"wicket/3_wicket-6.[16x17].0/repository_mining/wicket-6.[16x17].0_pu_r_degraded_method_commits_2015-02-13_14h46min.txt",
			"wicket/3_wicket-6.[17x18].0/repository_mining/wicket-6.[17x18].0_pu_r_degraded_method_commits_2015-02-13_16h35min.txt",
			"wicket/3_wicket-6.18.0x7.0.0-M1/repository_mining/wicket-6.18.0x7.0.0-M1_pu_r_degraded_method_commits_2015-02-13_21h21min.txt",
			"wicket/3_wicket-7.0.0-[M1xM2]/repository_mining/wicket-7.0.0-[M1xM2]_pu_r_degraded_method_commits_2015-02-15_11h55min.txt",
			"wicket/3_wicket-7.0.0-[M2xM4]/repository_mining/wicket-7.0.0-[M2xM4]_pu_r_degraded_method_commits_2015-02-15_12h46min.txt"
		}
	};
	
	private static final String[][] OPTIMIZED_TARGETS = {
		{
			"argouml/argouml_tests_2015-02-18_23h03min/repository_mining/argouml_pu_r_optimized_method_commits_2015-02-18_23h03min.txt"
		},
		{
			"netty/3_netty-4.0.[0-6].Final/repository_mining/netty-4.0.[0-6].Final_pu_r_optimized_method_commits_2015-02-12_07h55min.txt",
			"netty/3_netty-4.0.[6-10].Final/repository_mining/netty-4.0.[6-10].Final_pu_r_optimized_method_commits_2015-02-12_08h39min.txt",
			"netty/3_netty-4.0.[10-15].Final/repository_mining/netty-4.0.[10-15].Final_pu_r_optimized_method_commits_2015-02-12_08h59min.txt",
			"netty/3_netty-4.0.[15-17].Final/repository_mining/netty-4.0.[15-17].Final_pu_r_optimized_method_commits_2015-02-12_10h15min.txt",
			"netty/3_netty-4.0.[17-18].Final/repository_mining/netty-4.0.[17-18].Final_pu_r_optimized_method_commits_2015-02-12_13h14min.txt",
			"netty/3_netty-4.0.[18-21].Final/repository_mining/netty-4.0.[18-21].Final_pu_r_optimized_method_commits_2015-02-12_15h40min.txt"
		},
		{
			"jetty/jetty_9.2.6_x_9.2.7/repository_mining/jetty_9.2.6_x_9.2.7_pu_r_optimized_method_commits_2015-03-17_16h34min.txt",
			"jetty/jetty_9.2.7_x_9.2.8/repository_mining/jetty_9.2.7_x_9.2.8_pu_r_optimized_method_commits_2015-03-17_16h51min.txt",
			"jetty/jetty_9.2.8_x_9.2.9/repository_mining/jetty_9.2.8_x_9.2.9_pu_r_optimized_method_commits_2015-03-17_16h54min.txt",
			"jetty/jetty_9.2.9_x_9.2.10/repository_mining/jetty_9.2.9_x_9.2.10_pu_r_optimized_method_commits_2015-03-17_16h59min.txt",
			"jetty/jetty_9.2.10_x_9.3.0.M0/repository_mining/jetty_9.2.10_x_9.3.0.M0_pu_r_optimized_method_commits_2015-03-17_17h04min.txt",
			"jetty/jetty_9.3.0.M0_x_9.3.0.M1/repository_mining/jetty_9.3.0.M0_x_9.3.0.M1_pu_r_optimized_method_commits_2015-03-17_17h16min.txt"
		},
		{
			"wicket/3_wicket-6.[15x16].0/repository_mining/wicket-6.[15x16].0_pu_r_optimized_method_commits_2015-02-13_11h33min.txt",
			"wicket/3_wicket-6.[16x17].0/repository_mining/wicket-6.[16x17].0_pu_r_optimized_method_commits_2015-02-13_14h46min.txt",
			"wicket/3_wicket-6.[17x18].0/repository_mining/wicket-6.[17x18].0_pu_r_optimized_method_commits_2015-02-13_16h35min.txt",
			"wicket/3_wicket-6.18.0x7.0.0-M1/repository_mining/wicket-6.18.0x7.0.0-M1_pu_r_optimized_method_commits_2015-02-13_21h21min.txt",
			"wicket/3_wicket-7.0.0-[M1xM2]/repository_mining/wicket-7.0.0-[M1xM2]_pu_r_optimized_method_commits_2015-02-15_11h55min.txt",
			"wicket/3_wicket-7.0.0-[M2xM4]/repository_mining/wicket-7.0.0-[M2xM4]_pu_r_optimized_method_commits_2015-02-15_12h46min.txt"
		}
	};
	
	/*
	private static final String[][] DEGRADED_TARGETS = {
		{
			"argouml/e1_argouml_degraded_method_commits.txt"
		},
		{
			"netty/e1_netty_degraded_method_commits.txt",
			"netty/e2_netty_degraded_method_commits.txt",
			"netty/e3_netty_degraded_method_commits.txt",
			"netty/e4_netty_degraded_method_commits.txt",
			"netty/e5_netty_degraded_method_commits.txt",
			"netty/e6_netty_degraded_method_commits.txt",
		},
		{
			"jetty/e1_jetty_degraded_method_commits.txt",
			"jetty/e2_jetty_degraded_method_commits.txt",
			"jetty/e3_jetty_degraded_method_commits.txt",
			"jetty/e4_jetty_degraded_method_commits.txt",
			"jetty/e5_jetty_degraded_method_commits.txt",
			"jetty/e6_jetty_degraded_method_commits.txt"
		},
		{
			"wicket/e1_wicket_degraded_method_commits.txt",
			"wicket/e2_wicket_degraded_method_commits.txt",
			"wicket/e3_wicket_degraded_method_commits.txt",
			"wicket/e4_wicket_degraded_method_commits.txt",
			"wicket/e5_wicket_degraded_method_commits.txt",
			"wicket/e6_wicket_degraded_method_commits.txt"
		}
	};
	
	private static final String[][] OPTIMIZED_TARGETS = {
		{
			"argouml/e1_argouml_optimized_method_commits.txt"
		},
		{
			"netty/e1_netty_optimized_method_commits.txt",
			"netty/e2_netty_optimized_method_commits.txt",
			"netty/e3_netty_optimized_method_commits.txt",
			"netty/e4_netty_optimized_method_commits.txt",
			"netty/e5_netty_optimized_method_commits.txt",
			"netty/e6_netty_optimized_method_commits.txt",
		},
		{
			"jetty/e1_jetty_optimized_method_commits.txt",
			"jetty/e2_jetty_optimized_method_commits.txt",
			"jetty/e3_jetty_optimized_method_commits.txt",
			"jetty/e4_jetty_optimized_method_commits.txt",
			"jetty/e5_jetty_optimized_method_commits.txt",
			"jetty/e6_jetty_optimized_method_commits.txt"
		},
		{
			"wicket/e1_wicket_optimized_method_commits.txt",
			"wicket/e2_wicket_optimized_method_commits.txt",
			"wicket/e3_wicket_optimized_method_commits.txt",
			"wicket/e4_wicket_optimized_method_commits.txt",
			"wicket/e5_wicket_optimized_method_commits.txt",
			"wicket/e6_wicket_optimized_method_commits.txt"
		}
	};
	*/

	private static void saveJoinedCSV(String[][] input_systems, String deviation_type,
			String input_folter, String output_folter, boolean save_local) throws IOException {
		
		// For all systems
		List<String> set_of_all_commits = new ArrayList<String>();
		
		// The header of the output csv file
		String header = null;
		
		for (String[] system_files : input_systems) {

			String system_name = null;
			int total_of_entries = 0;
			int evolution = 0;
			String output_path = null;
			Set<String> set_of_entries = new TreeSet<String>();
			
			for (String input_path : system_files) {
				
				if (evolution == 0) {
					system_name = input_path.substring(0, input_path.indexOf('/'));
					output_path = output_folter + "/" + system_name + "/all_" + system_name + "_" + deviation_type + "_method_commits.txt";
					
					System.out.println("System: " + system_name);
					System.out.println("\tOutput: " + output_path);
				}
				
				if (!input_folter.isEmpty() && !input_folter.endsWith("/"))
					input_folter += "/";
				
				BufferedReader local_in = new BufferedReader(new FileReader(input_folter + input_path));
				PrintWriter local_out = null;
				
				if (save_local)
					local_out = new PrintWriter(output_folter + "/" + system_name + "/e" + ++evolution + "_" + input_path.substring(input_path.lastIndexOf('/') + 1));
				
				System.out.println("\tPath: " + input_path);
				
				int number_of_entries = Integer.parseInt(local_in.readLine()); // Number of entries
				total_of_entries += number_of_entries;
				
				System.out.println("\t\tEntries: " + number_of_entries);
				
				header = local_in.readLine(); // The header
				if (save_local) {
					local_out.println(number_of_entries);
					local_out.println(header);
				}
				
				String line = null;
				while ((line = local_in.readLine()) != null) {
					set_of_entries.add(line);
					
					if (save_local)
						local_out.println(line);
				}

				if (save_local)
					local_out.close();
				local_in.close();
				
			}
			
			System.out.println("\tTotal of Entries: " + total_of_entries + " and " + set_of_entries.size());
			
			if (total_of_entries != set_of_entries.size())
				System.err.println("\tNumber of entries are different. Is it ok?");
		
			saveCSV(set_of_entries, output_path, header);
			
			set_of_all_commits.addAll(set_of_entries);
			
			System.out.println("-----------------------------------------------");

		}
		
		saveCSV(set_of_all_commits, output_folter + "/csv_of_all_commits_" + deviation_type + ".txt", header);
	}
	
	public static void saveCSV(Collection<String> collection, String filepath, String header) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(filepath);
		
		pw.println(header);
		for (String entry : collection)
			pw.println(entry);
		
		pw.close();
	}
	
	public static void main(String[] args) throws IOException {
		
		saveJoinedCSV(DEGRADED_TARGETS, "degraded", "", "reports/commit_analysis", true);
		saveJoinedCSV(OPTIMIZED_TARGETS, "optimized", "", "reports/commit_analysis", true);
		
		//saveJoinedCSV(DEGRADED_TARGETS, "degraded", "reports/commit_analysis", "reports/commit_analysis", false);
		//saveJoinedCSV(OPTIMIZED_TARGETS, "optimized", "reports/commit_analysis", "reports/commit_analysis", false);
		
	}

}
