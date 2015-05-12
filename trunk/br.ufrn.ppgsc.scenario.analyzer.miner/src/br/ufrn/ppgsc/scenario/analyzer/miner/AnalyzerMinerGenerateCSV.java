package br.ufrn.ppgsc.scenario.analyzer.miner;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import br.ufrn.ppgsc.scenario.analyzer.miner.util.AnalyzerReportUtil;

public class AnalyzerMinerGenerateCSV {
	
	private static final String[][][] TARGET_FILES = {
		{
			{
			"argouml/argouml_tests_2015-02-18_23h03min/repository_mining/argouml_all_commits_changed_methods_2015-02-18_23h03min.txt",
			"argouml/argouml_tests_2015-02-18_23h03min/repository_mining/argouml_pu_blamed_methods_of_degraded_scenarios_significance_only_commits_2015-02-18_23h03min.txt",
			"argouml/argouml_tests_2015-02-18_23h03min/repository_mining/argouml_pu_blamed_methods_of_optimized_scenarios_significance_only_commits_2015-02-18_23h03min.txt"
			}
		},
		{
			{
			"netty/3_netty-4.0.[0-6].Final/repository_mining/netty-4.0.[0-6].Final_all_commits_changed_methods_2015-02-12_07h55min.txt",
			"netty/3_netty-4.0.[0-6].Final/repository_mining/netty-4.0.[0-6].Final_pu_blamed_methods_of_degraded_scenarios_significance_only_commits_2015-02-12_07h55min.txt",
			"netty/3_netty-4.0.[0-6].Final/repository_mining/netty-4.0.[0-6].Final_pu_blamed_methods_of_optimized_scenarios_significance_only_commits_2015-02-12_07h55min.txt",
			},
			{
			"netty/3_netty-4.0.[6-10].Final/repository_mining/netty-4.0.[6-10].Final_all_commits_changed_methods_2015-02-12_08h39min.txt",
			"netty/3_netty-4.0.[6-10].Final/repository_mining/netty-4.0.[6-10].Final_pu_blamed_methods_of_degraded_scenarios_significance_only_commits_2015-02-12_08h39min.txt",
			"netty/3_netty-4.0.[6-10].Final/repository_mining/netty-4.0.[6-10].Final_pu_blamed_methods_of_optimized_scenarios_significance_only_commits_2015-02-12_08h39min.txt"
			},
			{
			"netty/3_netty-4.0.[10-15].Final/repository_mining/netty-4.0.[10-15].Final_all_commits_changed_methods_2015-02-12_08h59min.txt",
			"netty/3_netty-4.0.[10-15].Final/repository_mining/netty-4.0.[10-15].Final_pu_blamed_methods_of_degraded_scenarios_significance_only_commits_2015-02-12_08h59min.txt",
			"netty/3_netty-4.0.[10-15].Final/repository_mining/netty-4.0.[10-15].Final_pu_blamed_methods_of_optimized_scenarios_significance_only_commits_2015-02-12_08h59min.txt"
			},
			{
			"netty/3_netty-4.0.[15-17].Final/repository_mining/netty-4.0.[15-17].Final_all_commits_changed_methods_2015-02-12_10h15min.txt",
			"netty/3_netty-4.0.[15-17].Final/repository_mining/netty-4.0.[15-17].Final_pu_blamed_methods_of_degraded_scenarios_significance_only_commits_2015-02-12_10h15min.txt",
			"netty/3_netty-4.0.[15-17].Final/repository_mining/netty-4.0.[15-17].Final_pu_blamed_methods_of_optimized_scenarios_significance_only_commits_2015-02-12_10h15min.txt"
			},
			{
			"netty/3_netty-4.0.[17-18].Final/repository_mining/netty-4.0.[17-18].Final_all_commits_changed_methods_2015-02-12_13h14min.txt",
			"netty/3_netty-4.0.[17-18].Final/repository_mining/netty-4.0.[17-18].Final_pu_blamed_methods_of_degraded_scenarios_significance_only_commits_2015-02-12_13h14min.txt",
			"netty/3_netty-4.0.[17-18].Final/repository_mining/netty-4.0.[17-18].Final_pu_blamed_methods_of_optimized_scenarios_significance_only_commits_2015-02-12_13h14min.txt"
			},
			{
			"netty/3_netty-4.0.[18-21].Final/repository_mining/netty-4.0.[18-21].Final_all_commits_changed_methods_2015-02-12_15h40min.txt",
			"netty/3_netty-4.0.[18-21].Final/repository_mining/netty-4.0.[18-21].Final_pu_blamed_methods_of_degraded_scenarios_significance_only_commits_2015-02-12_15h40min.txt",
			"netty/3_netty-4.0.[18-21].Final/repository_mining/netty-4.0.[18-21].Final_pu_blamed_methods_of_optimized_scenarios_significance_only_commits_2015-02-12_15h40min.txt"
			}
		},
		{
			{
			"jetty/jetty_9.2.6_x_9.2.7/repository_mining/jetty_9.2.6_x_9.2.7_all_commits_changed_methods_2015-03-17_16h34min.txt",
			"",
			""
			},
			{
			"jetty/jetty_9.2.7_x_9.2.8/repository_mining/jetty_9.2.7_x_9.2.8_all_commits_changed_methods_2015-03-17_16h51min.txt",
			"",
			""
			},
			{
			"jetty/jetty_9.2.8_x_9.2.9/repository_mining/jetty_9.2.8_x_9.2.9_all_commits_changed_methods_2015-03-17_16h54min.txt",
			"",
			""
			},
			{
			"jetty/jetty_9.2.9_x_9.2.10/repository_mining/jetty_9.2.9_x_9.2.10_all_commits_changed_methods_2015-03-17_16h59min.txt",
			"jetty/jetty_9.2.9_x_9.2.10/repository_mining/jetty_9.2.9_x_9.2.10_pu_blamed_methods_of_degraded_scenarios_significance_only_commits_2015-03-17_16h59min.txt",
			""
			},
			{
			"jetty/jetty_9.2.10_x_9.3.0.M0/repository_mining/jetty_9.2.10_x_9.3.0.M0_all_commits_changed_methods_2015-03-17_17h04min.txt",
			"jetty/jetty_9.2.10_x_9.3.0.M0/repository_mining/jetty_9.2.10_x_9.3.0.M0_pu_blamed_methods_of_degraded_scenarios_significance_only_commits_2015-03-17_17h04min.txt",
			"jetty/jetty_9.2.10_x_9.3.0.M0/repository_mining/jetty_9.2.10_x_9.3.0.M0_pu_blamed_methods_of_optimized_scenarios_significance_only_commits_2015-03-17_17h04min.txt"
			},
			{
			"jetty/jetty_9.3.0.M0_x_9.3.0.M1/repository_mining/jetty_9.3.0.M0_x_9.3.0.M1_all_commits_changed_methods_2015-03-17_17h16min.txt",
			"jetty/jetty_9.3.0.M0_x_9.3.0.M1/repository_mining/jetty_9.3.0.M0_x_9.3.0.M1_pu_blamed_methods_of_degraded_scenarios_significance_only_commits_2015-03-17_17h16min.txt",
			"jetty/jetty_9.3.0.M0_x_9.3.0.M1/repository_mining/jetty_9.3.0.M0_x_9.3.0.M1_pu_blamed_methods_of_optimized_scenarios_significance_only_commits_2015-03-17_17h16min.txt"
			}
		},
		{
			{
			"wicket/3_wicket-6.[15x16].0/repository_mining/wicket-6.[15x16].0_all_commits_changed_methods_2015-02-13_11h33min.txt",
			"wicket/3_wicket-6.[15x16].0/repository_mining/wicket-6.[15x16].0_pu_blamed_methods_of_degraded_scenarios_significance_only_commits_2015-02-13_11h33min.txt",
			"wicket/3_wicket-6.[15x16].0/repository_mining/wicket-6.[15x16].0_pu_blamed_methods_of_optimized_scenarios_significance_only_commits_2015-02-13_11h33min.txt"
			},
			{
			"wicket/3_wicket-6.[16x17].0/repository_mining/wicket-6.[16x17].0_all_commits_changed_methods_2015-02-13_14h46min.txt",
			"wicket/3_wicket-6.[16x17].0/repository_mining/wicket-6.[16x17].0_pu_blamed_methods_of_degraded_scenarios_significance_only_commits_2015-02-13_14h46min.txt",
			""
			},
			{
			"wicket/3_wicket-6.[17x18].0/repository_mining/wicket-6.[17x18].0_all_commits_changed_methods_2015-02-13_16h35min.txt",
			"wicket/3_wicket-6.[17x18].0/repository_mining/wicket-6.[17x18].0_pu_blamed_methods_of_degraded_scenarios_significance_only_commits_2015-02-13_16h35min.txt",
			"wicket/3_wicket-6.[17x18].0/repository_mining/wicket-6.[17x18].0_pu_blamed_methods_of_optimized_scenarios_significance_only_commits_2015-02-13_16h35min.txt"
			},
			{
			"wicket/3_wicket-6.18.0x7.0.0-M1/repository_mining/wicket-6.18.0x7.0.0-M1_all_commits_changed_methods_2015-02-13_21h21min.txt",
			"wicket/3_wicket-6.18.0x7.0.0-M1/repository_mining/wicket-6.18.0x7.0.0-M1_pu_blamed_methods_of_degraded_scenarios_significance_only_commits_2015-02-13_21h21min.txt",
			"wicket/3_wicket-6.18.0x7.0.0-M1/repository_mining/wicket-6.18.0x7.0.0-M1_pu_blamed_methods_of_optimized_scenarios_significance_only_commits_2015-02-13_21h21min.txt"
			},
			{
			"wicket/3_wicket-7.0.0-[M1xM2]/repository_mining/wicket-7.0.0-[M1xM2]_all_commits_changed_methods_2015-02-15_11h55min.txt",
			"wicket/3_wicket-7.0.0-[M1xM2]/repository_mining/wicket-7.0.0-[M1xM2]_pu_blamed_methods_of_degraded_scenarios_significance_only_commits_2015-02-15_11h55min.txt",
			"wicket/3_wicket-7.0.0-[M1xM2]/repository_mining/wicket-7.0.0-[M1xM2]_pu_blamed_methods_of_optimized_scenarios_significance_only_commits_2015-02-15_11h55min.txt"
			},
			{
			"wicket/3_wicket-7.0.0-[M2xM4]/repository_mining/wicket-7.0.0-[M2xM4]_all_commits_changed_methods_2015-02-15_12h46min.txt",
			"wicket/3_wicket-7.0.0-[M2xM4]/repository_mining/wicket-7.0.0-[M2xM4]_pu_blamed_methods_of_degraded_scenarios_significance_only_commits_2015-02-15_12h46min.txt",
			"wicket/3_wicket-7.0.0-[M2xM4]/repository_mining/wicket-7.0.0-[M2xM4]_pu_blamed_methods_of_optimized_scenarios_significance_only_commits_2015-02-15_12h46min.txt"
			}
		}
	};
	
	/*
	private static final String[][] PROPERTY_TARGETS = {
		{
			"argouml/argouml-tests.properties"
		},
		{
			"3",
			"netty/netty-4.0.[0-6].Final.properties",
			"netty/netty-4.0.[6-10].Final.properties",
			"netty/netty-4.0.[10-15].Final.properties",
			"netty/netty-4.0.[15-17].Final.properties",
			"netty/netty-4.0.[17-18].Final.properties",
			"netty/netty-4.0.[18-21].Final.properties"
		},
		{
			"",
			"jetty/jetty_9.2.6_x_9.2.7.properties",
			"jetty/jetty_9.2.7_x_9.2.8.properties",
			"jetty/jetty_9.2.8_x_9.2.9.properties",
			"jetty/jetty_9.2.9_x_9.2.10.properties",
			"jetty/jetty_9.2.10_x_9.3.0.M0.properties",
			"jetty/jetty_9.3.0.M0_x_9.3.0.M1.properties"
		},
		{
			"wicket/wicket-6.[15x16].0.properties",
			"wicket/wicket-6.[16x17].0.properties",
			"wicket/wicket-6.[17x18].0.properties",
			"wicket/wicket-[6.18.0x7.0.0-M1].properties",
			"wicket/wicket-7.0.0-[M1xM2].properties",
			"wicket/wicket-7.0.0-[M2xM4].properties"
		}
	};
	*/
	
//netty/3_netty-4.0.[18-21].Final/repository_mining/netty-4.0.[18-21].Final_all_commits_changed_methods_2015-02-12_15h40min.txt
	
	public static void main(String[] args) throws IOException {
		
		String[] line = "felipe".split(";");
		
		System.out.println(line[0]);
		
		for (String[][] target_systems : TARGET_FILES) {
			
			String system_name = null;
			int evolution = 0;
			
			for (String[] target_files : target_systems) {
				
				String file_all_commit_file = target_files[0];
				String file_degraded_commits = target_files[1];
				String file_optimized_commits = target_files[2];
				
				if (system_name == null) {
					system_name = file_all_commit_file.substring(0, file_all_commit_file.indexOf('/'));
					System.out.println("System: " + system_name);
				}
				
				System.out.println("\tAll Commits: " + file_all_commit_file);
				System.out.println("\tDegraded Commits: " + file_degraded_commits);
				System.out.println("\tOptimized Commits: " + file_optimized_commits);
				
				Set<String> set_all_commits = new HashSet<String>();
				Set<String> set_degraded_commits = new HashSet<String>();
				Set<String> set_optimized_commits = new HashSet<String>();
				
				AnalyzerReportUtil.loadCollection(set_all_commits, file_all_commit_file, true);
				
				if (!file_degraded_commits.isEmpty())
					AnalyzerReportUtil.loadCollection(set_degraded_commits, file_degraded_commits, false);
				
				if (!file_optimized_commits.isEmpty())
					AnalyzerReportUtil.loadCollection(set_optimized_commits, file_optimized_commits, false);
				
				String filename = "e" + ++evolution + "_" + system_name + "_%s_method_commits.txt";
				String degraded_path = "reports/commit_analysis/" + system_name + "/" + filename.replaceAll("%s", "degraded");
				String optimized_path = "reports/commit_analysis/" + system_name + "/" + filename.replaceAll("%s", "optimized");
				
				System.out.println("\tDegraded Path: " + degraded_path);
				System.out.println("\tOptimized Path: " + optimized_path);
				
				
			}
			
			System.out.println("------------------------------------------");
			
		}
		
//		for (String[] system_properties : PROPERTY_TARGETS) {
//			
//			String system_name = null;
//			
//			for (String property_file : system_properties) {
//			
//				Properties p = new Properties();
//				p.load(new FileInputStream("resources/" + property_file));
//				
//				if (system_name == null) {
//					system_name = property_file.substring(0, property_file.indexOf('/'));
//					System.out.println("System: " + system_name);
//				}
//				
//				System.out.println("\tProperty File: resources/" + property_file);
//				
//				system_name + "/" + 
//				
//				System.out.println("------------------------------------------");
//				
//			}
//			
//		}
		
	}

}
