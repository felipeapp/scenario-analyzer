package br.ufrn.ppgsc.scenario.analyzer.miner;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
	
	public static void main(String[] args) throws IOException, ParseException {
		
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
				
				List<Map<String, String>> list_all_commits = AnalyzerReportUtil.loadCommitReport(file_all_commit_file, ",");
				
				Set<String> set_degraded_commits = new HashSet<String>();
				Set<String> set_optimized_commits = new HashSet<String>();
				
				if (!file_degraded_commits.isEmpty())
					AnalyzerReportUtil.loadCollection(set_degraded_commits, file_degraded_commits, false);
				
				if (!file_optimized_commits.isEmpty())
					AnalyzerReportUtil.loadCollection(set_optimized_commits, file_optimized_commits, false);
				
				String filename = "e" + ++evolution + "_" + system_name + "_%s_method_commits.txt";
				String degraded_path = "reports/commit_analysis/" + system_name + "/" + filename.replaceAll("%s", "degraded");
				String optimized_path = "reports/commit_analysis/" + system_name + "/" + filename.replaceAll("%s", "optimized");
				
				System.out.println("\tDegraded Path: " + degraded_path);
				System.out.println("\tOptimized Path: " + optimized_path);
				
				String header = AnalyzerReportUtil.getSelectedCommitHeaderForRAnalysis(",");
				String[] header_keys = header.split(",");
				
				// Using tree set to keep the order of the output
				Set<String> degraded_lines = new TreeSet<String>();
				Set<String> optimized_lines = new TreeSet<String>();
				
				for (Map<String, String> map : list_all_commits) {
					StringBuilder sb_degraded = new StringBuilder();
					StringBuilder sb_optimized = new StringBuilder();
					
					for (int i = 0; i < header_keys.length; i++) {
						if (header_keys[i].equals("Degradation")) {
							String revision = map.get("Revision");
							
							sb_degraded.append(set_degraded_commits.contains(revision));
							sb_optimized.append(set_optimized_commits.contains(revision));
						}
						else if (header_keys[i].equals("Hour of Day")) {
							Calendar c = new GregorianCalendar();
							c.setTime(new SimpleDateFormat("EEE dd-MMM-yyyy HH:mm:ss").parse(map.get("Date")));
							
							sb_degraded.append(c.get(Calendar.HOUR_OF_DAY));
							sb_optimized.append(c.get(Calendar.HOUR_OF_DAY));
						}
						else if (header_keys[i].equals("Day of Week")) {
							Calendar c = new GregorianCalendar();
							c.setTime(new SimpleDateFormat("EEE dd-MMM-yyyy HH:mm:ss").parse(map.get("Date")));
							
							sb_degraded.append(c.get(Calendar.DAY_OF_WEEK));
							sb_optimized.append(c.get(Calendar.DAY_OF_WEEK));
						}
						else {
							sb_degraded.append(map.get(header_keys[i]));
							sb_optimized.append(map.get(header_keys[i]));
						}
						
						if (i < header_keys.length - 1) {
							sb_degraded.append(",");
							sb_optimized.append(",");
						}
					}
					
					degraded_lines.add(sb_degraded.toString());
					optimized_lines.add(sb_optimized.toString());
				}
				
				PrintWriter pw_degraded = new PrintWriter(degraded_path);
				PrintWriter pw_optimized = new PrintWriter(optimized_path);
				
				pw_degraded.println(degraded_lines.size());
				pw_degraded.println(header);
				for (String line : degraded_lines)
					pw_degraded.println(line);
				
				pw_optimized.println(optimized_lines.size());
				pw_optimized.println(header);
				for (String line : optimized_lines)
					pw_optimized.println(line);
				
				pw_degraded.close();
				pw_optimized.close();
			}
			
			System.out.println("------------------------------------------");
			
		}
		
	}

}
