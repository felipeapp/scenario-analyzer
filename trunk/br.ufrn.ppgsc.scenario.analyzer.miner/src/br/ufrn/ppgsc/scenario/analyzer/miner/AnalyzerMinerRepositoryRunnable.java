package br.ufrn.ppgsc.scenario.analyzer.miner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import br.ufrn.ppgsc.scenario.analyzer.miner.db.DatabaseRelease;
import br.ufrn.ppgsc.scenario.analyzer.miner.db.GenericDB;
import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IPathTransformer;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedMethod;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.RepositoryManager;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.SystemMetadataUtil;

public final class AnalyzerMinerRepositoryRunnable {

	private List<Scanner> readers;
	private List<String> partial_names;
	private IPathTransformer transformer;
	
	private String url;
	private String rep_prefix;
	private String user;
	private String password;
	private String system_id;
	private String strdate;
	private String wc_prefix_v1;
	private String wc_prefix_v2;
	private String exclude_word;
	private int package_deep;

	private static final String files[] =
		{"changed_methods", "excluded_methods", "failed_methods_both",
		"failed_methods_only_v1", "failed_methods_only_v2", "kept_methods",
		"p_degraded_methods", "p_optimized_methods", "p_unchanged_methods"};

	public AnalyzerMinerRepositoryRunnable(String strdate) throws FileNotFoundException {
		SystemMetadataUtil properties = SystemMetadataUtil.getInstance();
		
		transformer = properties.newObjectFromProperties(IPathTransformer.class);
		this.strdate = strdate;
		readers = new ArrayList<Scanner>();
		partial_names = new ArrayList<String>();
		
		url = properties.getStringProperty("repository_url");
		rep_prefix = properties.getStringProperty("repository_prefix");
		user = properties.getStringProperty("repository_user");
		password = properties.getStringProperty("repository_password");
		system_id = properties.getStringProperty("system_id");
		
		wc_prefix_v1 = properties.getStringProperty("workcopy_prefix_v1");
		wc_prefix_v2 = properties.getStringProperty("workcopy_prefix_v2");
		
		exclude_word = properties.getStringProperty("exclude_word");
		package_deep = properties.getIntProperty("package_deep");
		
		for (String name : files) {
			boolean active = properties.getBooleanProperty(name);
			
			if (active) {
				String filename = "miner_log/" + system_id + "_" + name + "_" + strdate + ".txt";
				partial_names.add(name);
				readers.add(new Scanner(new FileInputStream(filename)));
			}
		}
		
	}

	private String loadFile(Scanner in, List<String> list) {
		String message = in.nextLine();
		int count = in.nextInt();
		
		for (int i = 0; i < count; i++)
			list.add(in.next());
		
		return message;
	}
	
	private void persistFile(String message, String partial_name, Set<Long> task_numbers,
			Map<String, Collection<UpdatedMethod>> filtrated_path_upmethod,
			Map<String, Integer> counter_task_types,
			Map<String, Integer> filtrated_counter_task_types,
			Map<String, Collection<UpdatedMethod>> task_members,
			Map<String, Collection<UpdatedMethod>> filtrated_task_members) throws FileNotFoundException {
		System.out.println("persistFile: " + message);
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(
				"miner_log/" + system_id + "_" + partial_name + "_" + strdate + ".txt"), true);
		
		pw.println(message);
		
		for (Long tnumber : task_numbers)
			pw.print(tnumber + " ");
		pw.println();
		
		pw.println(filtrated_path_upmethod.size());
		
		for (String path: filtrated_path_upmethod.keySet()) {
			pw.println(path);
			
			Collection<UpdatedMethod> upmethod_list = filtrated_path_upmethod.get(path);
			
			pw.println("Members " + upmethod_list.size());
			
			for (UpdatedMethod method : upmethod_list) {
				List<UpdatedLine> upl_list = method.getUpdatedLines();
				Set<String> counte_revisions = new HashSet<String>();
				
				pw.print(method.getMethodLimit().getSignature() + " ");
				pw.print(method.getMethodLimit().getStartLine() + " ");
				pw.println(method.getMethodLimit().getEndLine());
				pw.println("Lines " + upl_list.size());
				
				for (UpdatedLine up_line : upl_list) {
					if (counte_revisions.contains(up_line.getRevision()))
						continue;
					
					List<Issue> issues = up_line.getIssues();
				
					pw.println("Revision " + up_line.getRevision());
					pw.println("Issues " + issues.size());
					
					counte_revisions.add(up_line.getRevision());
					
					for (Issue issue : issues) {
						if (issue.getNumber() >= 0) {
							pw.print("IssueID " + issue.getIssueId() + " | ");
							pw.print("IssueNumber " + issue.getNumber() + " | ");
							pw.println("IssueType " + issue.getIssueType());
						}
					}
				}
			}
		}
		
		pw.println(counter_task_types.size());
		
		for (String type : counter_task_types.keySet())
			pw.println(type + ":" + counter_task_types.get(type));
		
		pw.println(filtrated_counter_task_types.size());
		
		for (String type : filtrated_counter_task_types.keySet())
			pw.println(type + ":" + filtrated_counter_task_types.get(type));
		
		pw.println(task_members.size());

		for (String type : task_members.keySet()) {
			Collection<UpdatedMethod> list = task_members.get(type);

			pw.println(type);
			pw.println(list.size());
			for (UpdatedMethod up : list)
				pw.println(up.getMethodLimit().getSignature());
		}

		pw.println(filtrated_task_members.size());

		for (String type : filtrated_task_members.keySet()) {
			Collection<UpdatedMethod> list = filtrated_task_members.get(type);

			pw.println(type);
			pw.println(list.size());
			for (UpdatedMethod up : list)
				pw.println(up.getMethodLimit().getSignature());
		}
		
		pw.close();
	}
	
	// TODO: Ver se posso tirar
//	private void persistFile2(String message, String partial_name, Set<Long> task_numbers,
//			Map<String, Collection<UpdatedMethod>> filtrated_path_upmethod,
//			Map<String, Integer> counter_task_types,
//			Map<String, Integer> filtrated_counter_task_types,
//			Map<String, Collection<UpdatedMethod>> task_members,
//			Map<String, Collection<UpdatedMethod>> filtrated_task_members) throws FileNotFoundException {
//		System.out.println("persistFile: " + message);
//		
//		PrintWriter pw = new PrintWriter(new FileOutputStream(
//				"miner_log/" + system_id + "_" + partial_name + "_" + strdate + ".txt"), true);
//		
//		pw.println(message);
//		
//		for (Long tnumber : task_numbers)
//			pw.print(tnumber + " ");
//		pw.println();
//		
//		pw.println(filtrated_path_upmethod.size());
//		
//		for (String path: filtrated_path_upmethod.keySet()) {
//			pw.println(path);
//			
//			Collection<UpdatedMethod> upmethod_list = filtrated_path_upmethod.get(path);
//			
//			pw.println(upmethod_list.size());
//			
//			for (UpdatedMethod method : upmethod_list) {
//				List<UpdatedLine> upl_list = method.getUpdatedLines();
//				
//				pw.print(method.getMethodLimit().getSignature() + " ");
//				pw.print(method.getMethodLimit().getStartLine() + " ");
//				pw.println(method.getMethodLimit().getEndLine());
//				pw.println(upl_list.size());
//				
//				for (UpdatedLine up_line : upl_list) {
//					pw.println("Autor:" + up_line.getAuthor());
//					pw.println("Linha:" + up_line.getLineNumber());
//					pw.println("RevisÃ£o:" + up_line.getRevision());
//					pw.println("Data:" + up_line.getDate());
//					
//					List<Issue> tasks = up_line.getIssues();
//
//					pw.println("####" + task_members.size());
//					pw.println("@@@@" + tasks.size());
//					for (Issue t : tasks) {
//						if (t.getNumber() >= 0) {
//							pw.println("Id:" + t.getIssueId());
//							pw.println("IdTipo:" + t.getIssueTypeId());
//							pw.println("NÃºmero:" + t.getNumber());
//							pw.println("TipoDenomicaÃ§Ã£o:" + t.getIssueType());
//						}
//					}
//				}
//			}
//		}
//		
//		pw.println(counter_task_types.size());
//		
//		for (String type : counter_task_types.keySet())
//			pw.println(type + ":" + counter_task_types.get(type));
//		
//		pw.println(filtrated_counter_task_types.size());
//		
//		for (String type : filtrated_counter_task_types.keySet())
//			pw.println(type + ":" + filtrated_counter_task_types.get(type));
//		
//		pw.println(task_members.size());
//
//		for (String type : task_members.keySet()) {
//			Collection<UpdatedMethod> list = task_members.get(type);
//
//			pw.println(type);
//			pw.println(list.size());
//			for (UpdatedMethod up : list)
//				pw.println(up.getMethodLimit().getSignature());
//		}
//
//		pw.println(filtrated_task_members.size());
//
//		for (String type : filtrated_task_members.keySet()) {
//			Collection<UpdatedMethod> list = filtrated_task_members.get(type);
//
//			pw.println(type);
//			pw.println(list.size());
//			for (UpdatedMethod up : list)
//				pw.println(up.getMethodLimit().getSignature());
//		}
//		
//		pw.close();
//	}
	
	private Map<String, List<String>> getScenariosWithBlames(String scenarios_file, String methods_file) throws FileNotFoundException {
		Map<String, List<String>> scenarios_blames = new HashMap<String, List<String>>();
		
		Scanner in = new Scanner(new FileInputStream("miner_log/" + system_id + "_" + methods_file + "_" + strdate + ".txt"));
		
		loadScenarios(scenarios_blames, scenarios_file);
		
		// Ler a mensagem
		System.out.println("Processing... " + in.nextLine());
		
		int total_members = in.nextInt(); in.nextLine();
		
		for (int i = 0; i < total_members; i++) {
			
			String signature = in.nextLine();
			int total_scenarios = in.nextInt(); in.nextLine();
			
			for (int j = 0; j < total_scenarios; j++) {
				
				String scenario = in.nextLine();
				
				List<String> members_sig = scenarios_blames.get(scenario);
				if (members_sig != null)
					members_sig.add(signature);
				
			}
			
		}

		in.close();
		
		return scenarios_blames;
	}
	
	private String loadScenarios(Map<String, List<String>> scenarios_blames, String name) throws FileNotFoundException {
		String message = null;
		String filename = "miner_log/" + system_id + "_" + name + "_" + strdate + ".txt";
		
		Scanner in = new Scanner(new FileInputStream(filename));

		message = in.nextLine();
		
		int total = in.nextInt();
		in.nextLine();

		for (int i = 0; i < total; i++) {
			String scenario_name = in.nextLine();
			in.nextLine(); // Tempos
			scenarios_blames.put(scenario_name, new ArrayList<String>());
		}

		in.close();

		return message;
	}
	
	private void persistFile(String message, String partial_name, Map<String, Collection<UpdatedMethod>> members) throws FileNotFoundException {
		System.out.println("persistFile: " + message);
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(
				"miner_log/" + system_id + "_" + partial_name + "_" + strdate + ".txt"), true);
		
		pw.println(message);
		pw.println(members.size());
		
		for (String signature : members.keySet()) {
			pw.println(signature);
			
			Collection<UpdatedMethod> upmethod_list = members.get(signature);
			
			pw.println("Members " + upmethod_list.size());
			
			for (UpdatedMethod method : upmethod_list) {
				List<UpdatedLine> upl_list = method.getUpdatedLines();
				Set<String> counte_revisions = new HashSet<String>();
				
				pw.print(method.getMethodLimit().getSignature() + " ");
				pw.print(method.getMethodLimit().getStartLine() + " ");
				pw.println(method.getMethodLimit().getEndLine());
				pw.println("Lines " + upl_list.size());
				
				for (UpdatedLine up_line : upl_list) {
					if (counte_revisions.contains(up_line.getRevision()))
						continue;
					
					List<Issue> issues = up_line.getIssues();
				
					pw.println("Revision " + up_line.getRevision());
					
					if (issues.size() == 1 && issues.get(0).getNumber() < 0)
						pw.println("Issues " + 0);
					else
						pw.println("Issues " + issues.size());
					
					counte_revisions.add(up_line.getRevision());
					
					for (Issue issue : issues) {
						if (issue.getNumber() >= 0) {
							pw.print("IssueID " + issue.getIssueId() + " | ");
							pw.print("IssueNumber " + issue.getNumber() + " | ");
							pw.print("IssueType " + issue.getIssueType() + " | ");
							pw.println("ShortDescription " + issue.getShortDescription());
						}
					}
				}
			}
		}
		
		Map<String, Integer> counter_task_types = counterTaskTypes(members);
		Map<String, Collection<UpdatedMethod>> task_members = getTaskMembers(members);
		
		pw.println("TotalOfTypes " + counter_task_types.size());
		
		for (String type : counter_task_types.keySet())
			pw.println(type + " " + counter_task_types.get(type));
		
		pw.println("TotalOfTypes " + task_members.size());

		for (String type : task_members.keySet()) {
			Collection<UpdatedMethod> list = task_members.get(type);

			pw.println(type);
			pw.println("TotalOfMembers " + list.size());
			for (UpdatedMethod up : list)
				pw.println(up.getMethodLimit().getSignature());
		}
		
		pw.close();
	}
	
	private void persistFile(String message, String partial_name, Set<String> members) throws FileNotFoundException {
		GenericDB database_v2 = DatabaseRelease.getDatabasev2();
		
		System.out.println("persistFile: " + message);
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(
				"miner_log/" + system_id + "_" + partial_name + "_" + strdate + ".txt"), true);
		
		pw.println(message);
		pw.println(members.size());
		
		int i = 0;
		for (String sig : members) {
			System.out.println("[" + ++i + "/" + members.size() + "] Retrieving impacted members and scenarios by " + sig);
			
//			Set<String> nodes = database_v2.getImpactedNodes(sig);
			List<String> scenarios = database_v2.getScenariosByMember(sig);
			
//			pw.println(sig + ":" + nodes.size());
			pw.println(sig);
			pw.println(scenarios.size());
			
			for (String s : scenarios)
				pw.println(s);
		}
		
		pw.close();
	}
	
	public void persistFile(String message, String partial_name,
			Map<String, Integer> total_classes, Map<String, Integer> total_packages) throws FileNotFoundException {
		System.out.println("persistFile: " + message);
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(
				"miner_log/" + system_id + "_" + partial_name + "_" + strdate + ".txt"), true);
		
		pw.println(total_classes.size());
		
		Set<String> class_keys = new TreeSet<String>(total_classes.keySet());
		Set<String> package_keys = new TreeSet<String>(total_packages.keySet());
		
		for (String class_name : class_keys)
			pw.println(class_name + " " + total_classes.get(class_name));
		
		pw.println(total_packages.size());
		
		for (String package_prefix : package_keys)
			pw.println(package_prefix + " " + total_packages.get(package_prefix));
		
		pw.close();
	}
	
	public void countTotalOfModules(Map<String, List<String>> scenariosWithBlames,
			Map<String, Integer> total_classes, Map<String, Integer> total_packages, int package_deep,
			Map<String, Double> avg_time_members_v1, Map<String, Double> avg_time_members_v2) {
		
		Set<String> counted = new HashSet<String>();
		
		for (List<String> signatures : scenariosWithBlames.values()) {
			if (signatures.isEmpty())
				continue;
			
			for (String sig : signatures) {
				Double t1 = avg_time_members_v1.get(sig);
				Double t2 = avg_time_members_v2.get(sig);
				
				double delta = t1 == null ? t2 : t2 - t1;
				
				if (counted.contains(sig) || matchesExcludeWord(sig) || delta < 0.001)
					continue;
				
				String class_name = getClassNameFromSignature(sig);
				String package_prefix = getPackagePrefixFromSignature(sig, package_deep);
				
				Integer value = total_classes.get(class_name);
				total_classes.put(class_name, value == null ? 1 : value + 1);
				
				value = total_packages.get(package_prefix);
				total_packages.put(package_prefix, value == null ? 1 : value + 1);
				
				counted.add(sig);
			}
		}
		
	}
	
	private void persistScenariosWithBlames(String message, String partial_name, Map<String, List<String>> scenariosWithBlames,
			Map<String, Double> avg_time_members_v1, Map<String, Double> avg_time_members_v2,
			Map<String, Collection<UpdatedMethod>> p_degraded_changed_methods) throws FileNotFoundException {
		int total_members = 0;
		int total_scenarios_with_blames = 0;
		int total_members_without_word = 0;
		
		Set<String> members_with_time = new TreeSet<String>();
		Set<String> counted = new HashSet<String>();
		
		List<Issue> list_issues = new ArrayList<Issue>();
		Map<String, List<Issue>> map_type_issues = new HashMap<String, List<Issue>>();
		
		System.out.println("persistFile: " + message);
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(
				"miner_log/" + system_id + "_" + partial_name + "_" + strdate + ".txt"), true);
		
		PrintWriter pwl = new PrintWriter(new FileOutputStream(
				"miner_log/" + system_id + "_" + partial_name + "_list_" + strdate + ".txt"), true);
		
		for (List<String> list : scenariosWithBlames.values())
			if (!list.isEmpty())
				++total_scenarios_with_blames;
		
		pw.println(message);
		pwl.println(message + " [list]");
		
		// Cenários que possuem blames
		pw.println(total_scenarios_with_blames);
		pwl.println(total_scenarios_with_blames);
		
		/* 
		 * Todos os cenários, alguns podem não ter blames.
		 * Isso acontece, por exemplo, devido somatórios de membros
		 * degradados, pequenos aumentos individuais (menor que a taxa)
		 * que quando somados impactam o cenário (com variação maior que a taxa)
		 */
		pw.println(scenariosWithBlames.size());
		
		for (String scenario : new TreeSet<String>(scenariosWithBlames.keySet())) {
			List<String> signatures = scenariosWithBlames.get(scenario);
			
			Collections.sort(signatures);
			
			pw.println(scenario);
			
			int i = 0;
			for (String s : signatures)
				if (!matchesExcludeWord(s))
					++i;
			
			pw.println(signatures.size() + " " + i);
			
			if (i > 0)
				pwl.println(scenario);
			
			for (String s : signatures) {
				Double t1 = avg_time_members_v1.get(s);
				Double t2 = avg_time_members_v2.get(s);
				
				double delta = t1 == null ? t2 : t2 - t1;
				
				String text = s + " " + t1 + " " + t2 + " " + delta;
				
				for (UpdatedMethod um : p_degraded_changed_methods.get(s)) {
					for (UpdatedLine ul : um.getUpdatedLines()) {
						for (Issue issue  : ul.getIssues()) {
							text += " " + issue.getNumber();
							
							list_issues.add(issue);
							
							List<Issue> type_list = map_type_issues.get(issue.getIssueType());
							
							if (type_list == null) {
								type_list = new ArrayList<Issue>();
								map_type_issues.put(issue.getIssueType(), type_list);
							}
							
							type_list.add(issue);
						}
					}
				}
				
				pw.println(text);
				// Aqui vai contar com os testes
				//members_with_time.add(text);
				
				if (!counted.contains(s)) {
					counted.add(s);
					++total_members;
					
					if (!matchesExcludeWord(s) && delta >= 0.001) {
						++total_members_without_word;
						members_with_time.add(text);
					}
				}
			}
		}
		
		pw.println(total_members);
		pw.println(total_members_without_word);
		
		pwl.println(members_with_time.size());
		for (String member : members_with_time)
			pwl.println(member);
		
		pwl.println(list_issues.size());
		for (Issue issue : list_issues)
			pwl.println(issue.getNumber() + " " + issue.getIssueType());
		
		pwl.println(map_type_issues.size());
		for (String issue_type : map_type_issues.keySet())
			pwl.println(issue_type + ":" + map_type_issues.get(issue_type).size());
		
		
		pw.close();
		pwl.close();
	}
	
	public void run() throws FileNotFoundException {
		int i = 0;
		
		/*
		 * O gerente de repositório conecta ao repositÃ³rio e permite acesso
		 * para a mineraÃ§Ã£o dos dados atravÃ©s do mÃ©todo disponibilizado.
		 * Conecta no momento que Ã© criado.
		 */
		RepositoryManager repository = new RepositoryManager(url, user, password);
		
		/* 
		 * Guarda as assinaturas de mÃ©todos que contribuiram especificamente
		 * para a degradaÃ§Ã£o do desempenho. Esta lista serÃ¡ usada na mineraÃ§Ã£o final.
		 * A chave do Map é a assinatura do método. O conteúdo é a lista de métodos que
		 * casam com a assinatura, lembrando que devido a limitação de assinaturas do parser
		 * mais de um nome de método pode casar com a assinatura.
		 */
		Map<String, Collection<UpdatedMethod>> p_degraded_changed_methods = new HashMap<String, Collection<UpdatedMethod>>();
		
		for (Scanner in : readers) {
			List<String> repository_paths = new ArrayList<String>();
			List<String> old_workcopy_paths = new ArrayList<String>();
			List<String> new_workcopy_paths = new ArrayList<String>();
			
			List<String> signatures = new ArrayList<String>();
			String message;
			
			message = loadFile(in, signatures);
			
			System.out.println(files[i]);
			
			for (String s : signatures) {
				String paths[] = transformer.convert(s, rep_prefix, wc_prefix_v1, wc_prefix_v2);
				
				if (paths == null) {
					System.out.println("Ignoring " + s + " because paths is null (Is it right?).");
					continue;
				}
				
				boolean ignore_file = false;
				boolean file_exists_v1 = new File(paths[1]).exists();
				boolean file_exists_v2 = new File(paths[2]).exists();
				
				if (!file_exists_v1 && !file_exists_v2)
					throw new FileNotFoundException("\n" + paths[1] + "\n" + paths[2]);
				
				if (!file_exists_v1) {
					System.out.println("Not found (Is it right?): " + paths[1]);
					ignore_file = true;
				}
				
				if (!file_exists_v2) {
					System.out.println("Not found (Is it right?): " + paths[2]);
					ignore_file = true;
				}
				
				if (ignore_file) {
					System.out.println("Ignoring files...");
				}
				else {
					repository_paths.add(paths[0]);
					old_workcopy_paths.add(paths[1]);
					new_workcopy_paths.add(paths[2]);
				}
			}
			
			System.out.println("Getting updated methods from repository to " + partial_names.get(i) + " ["
					+ repository_paths.size() + ", " + message + "]");
			
			Map<String, Collection<UpdatedMethod>> map_path_upmethod = null;
			
			if (!signatures.isEmpty()) {
				map_path_upmethod = repository.getUpdatedMethodsFromRepository(
						repository_paths, old_workcopy_paths, new_workcopy_paths);
			}
			
			if (map_path_upmethod == null)
				map_path_upmethod = new HashMap<String, Collection<UpdatedMethod>>();
			
			// Os que foram modificados e estÃ£o dentro do critÃ©rio analisado (degradados, por exemplo)
			Map<String, Collection<UpdatedMethod>> filtrated_path_upmethod = new HashMap<String, Collection<UpdatedMethod>>();
			
			// Percorre a estrutura para filtrar os mÃ©todos de interesse para cada path
			for (String path : map_path_upmethod.keySet()) {
				for (UpdatedMethod upm : map_path_upmethod.get(path)) {
					/* 
					 * Testa se o nome do mÃ©todo casa com a assinatura do mÃ©todo
					 * LimitaÃ§Ã£o quando o mÃ©todo tem formas diferentes com o mesmo nome
					 * A limitaÃ§Ã£o Ã© causada devido o parser do JDT que estÃ¡ sendo usado
					 */
					String sig_matched = matchesName(path, upm.getMethodLimit().getSignature(), signatures);
					
					if (sig_matched != null) {
						Collection<UpdatedMethod> upm_list = filtrated_path_upmethod.get(path);
						
						if (upm_list == null) {
							upm_list = new ArrayList<UpdatedMethod>();
							filtrated_path_upmethod.put(path, upm_list);
						}
						
						upm_list.add(upm);
						
						/*
						 * OBS: Em caso de dÃºvida porque o equals do sigaa nÃ£o entrou na soluÃ§Ã£o dos
						 * nove mÃ©todos degradados, a resposta Ã© que apesar dele ser novo na execuÃ§Ã£o
						 * evoluÃ§Ã£o, o mÃ©todo jÃ¡ existia antes e nÃ£o era usado. Ele nÃ£o foi modificado,
						 * logo nenhuma mudanÃ§a feita para ele poderia ser culpada de degradaÃ§Ã£o, pois
						 * elas nÃ£o existem. No caso dos outros quatro mÃ©todos, eles sÃ£o novos e foram
						 * adicionados ao cÃ³digo durante a evoluÃ§Ã£o.
						 */
						if (partial_names.get(i).equals("changed_methods") || partial_names.get(i).equals("p_degraded_methods"))
							p_degraded_changed_methods.put(sig_matched, upm_list);
					}
				}
			}
			
			/*
			 * Conta quantas vezes o tipo de tarefa ocorreu em toda a evoluÃ§Ã£o, considerando
			 * as classes dos mÃ©todos para o problema sendo analisado. Note que algumas classes
			 * podem ter mudanÃ§as em mÃ©todos, mas estes nÃ£o terem sido degradados neste caso.
			 */
			Map<String, Collection<UpdatedMethod>> task_members = getTaskMembers(map_path_upmethod);
			Map<String, Integer> counter_task_types = counterTaskTypes(map_path_upmethod);
			
			/*
			 * Conta quantas vezes o tipo de tarefa ocorreu para o problema sendo analisado,
			 * por exemplo, quantas vezes o tipo de tarefa aparece para mï¿½todos com desempenho
			 * degradado. Agora, sÃ£o apenas os mÃ©todos modificados e afetados pelo problema analisado.
			 */
			Map<String, Collection<UpdatedMethod>> filtrated_task_members = getTaskMembers(filtrated_path_upmethod);
			Map<String, Integer> filtrated_counter_task_types = counterTaskTypes(filtrated_path_upmethod);
			
			// Persistir em arquivo os dados coletados
			persistFile(message, "svn_" + partial_names.get(i++), getTaskNumbers(filtrated_path_upmethod), filtrated_path_upmethod,
					counter_task_types, filtrated_counter_task_types, task_members, filtrated_task_members);
		}
		
		// Mostrando os issues
		persistFile("# Issues responsáveis pela degradação de performance", "issues_performance_degradation_all", p_degraded_changed_methods);
		
		// Mostrando o impacto dos responsáveis pela degradação de performance
		persistFile("# Métodos responsáveis pela degradação de performance", "methods_performance_degradation", p_degraded_changed_methods.keySet());
		
		// Recuperando os cenários degradados e os membros culpados
		Map<String, List<String>> scenariosWithBlames = getScenariosWithBlames(
				"p_degraded_scenarios", "methods_performance_degradation");
		
		/*
		 *  Necessário para o Netty porque estes testes mudaram.
		 *  TODO: Ver como não usar isso depois.
		 */
		scenariosWithBlames.remove("Entry point for DatagramUnicastTest.testSimpleSend");
		scenariosWithBlames.remove("Entry point for SocketConnectionAttemptTest.testConnectTimeout");
		scenariosWithBlames.remove("Entry point for DatagramMulticastTest.testMulticast");
		
		/*
		 *  Necessário para o ArgoUML porque este teste tem um sleep(2000).
		 *  TODO: Ver como não usar isso depois.
		 */		
		scenariosWithBlames.remove("Entry point for TestNotationProvider.testListener");
		
		/*
		 *  Temporário
		 *  TODO: Fazer o teste durante a análise de degradação na fase anterior
		 *  da abordagem e depois retirar isso
		 */
		
		GenericDB database_v1 = DatabaseRelease.getDatabasev1();
		GenericDB database_v2 = DatabaseRelease.getDatabasev2();
		Map<String, Double> avg_time_members_v1 = database_v1.getExecutionTimeAverageOfMembers();
		Map<String, Double> avg_time_members_v2 = database_v2.getExecutionTimeAverageOfMembers();
		
		// Mostrando os cenários degradados e os membros culpados
		persistScenariosWithBlames("# Membros responsáveis pela degradação de performance em cada cenário degradado",
				"scenarios_blames_performance_degradation", scenariosWithBlames,
				avg_time_members_v1, avg_time_members_v2, p_degraded_changed_methods);
		
		// Mapas para contar quantas vezes as classes e pacotes aparecem no resultado dos culpados
		Map<String, Integer> total_classes = new HashMap<String, Integer>();
		Map<String, Integer> total_packages = new HashMap<String, Integer>();
		
		// Conta e armazena os resultados nos mapas.
		countTotalOfModules(scenariosWithBlames, total_classes, total_packages, package_deep,
				avg_time_members_v1, avg_time_members_v2);
		
		// Mostrando os cenÃ¡rios degradados e os membros culpados
		persistFile("# Contagem de classes e pacotes", "total_of_classes_and_packages", total_classes, total_packages);
	}
	
	private Set<Long> getTaskNumbers(Map<String, Collection<UpdatedMethod>> map_path_methods) {
		Set<Long> issue_numbers = new HashSet<Long>();

		for (String path : map_path_methods.keySet())
			for (UpdatedMethod method : map_path_methods.get(path))
				for (UpdatedLine line : method.getUpdatedLines())
					for (Issue issue : line.getIssues())
						issue_numbers.add(issue.getNumber());

		return issue_numbers;
	}
	
	private Map<String, Integer> counterTaskTypes(Map<String, Collection<UpdatedMethod>> map_path_methods) {
		Map<String, Integer> counter_task_types = new HashMap<String, Integer>();
		Set<Long> counted_tasks = new HashSet<Long>();

		for (String path : map_path_methods.keySet()) {

			for (UpdatedMethod method : map_path_methods.get(path)) {

				for (UpdatedLine line : method.getUpdatedLines()) {

					for (Issue issue : line.getIssues()) {

						if (issue.getIssueId() > 0 && !counted_tasks.contains(issue.getIssueId())) {
							Integer counter = counter_task_types.get(issue.getIssueType());

							if (counter == null)
								counter_task_types.put(issue.getIssueType(), 1);
							else
								counter_task_types.put(issue.getIssueType(), counter + 1);
							
							counted_tasks.add(issue.getIssueId());
						}

					}

				}

			}
		}

		return counter_task_types;
	}
	
	private Map<String, Collection<UpdatedMethod>> getTaskMembers(Map<String, Collection<UpdatedMethod>> map_path_methods) {
		Map<String, Collection<UpdatedMethod>> task_members = new HashMap<String, Collection<UpdatedMethod>>();
		
		Set<Long> counted_tasks = new HashSet<Long>();
		Set<String> counted_members = new HashSet<String>();

		for (String path : map_path_methods.keySet()) {

			for (UpdatedMethod method : map_path_methods.get(path)) {

				for (UpdatedLine line : method.getUpdatedLines()) {

					for (Issue issue : line.getIssues()) {

						if (issue.getIssueId() > 0 && !(counted_tasks.contains(issue.getIssueId()) && counted_members.contains(method.getMethodLimit().getSignature()))) {
							Collection<UpdatedMethod> list = task_members.get(issue.getIssueType());

							if (list == null) {
								list = new ArrayList<UpdatedMethod>();
								list.add(method);
								task_members.put(issue.getIssueType(), list);
							}
							else {
								task_members.get(issue.getIssueType()).add(method);
							}
							
							counted_tasks.add(issue.getIssueId());
							counted_members.add(method.getMethodLimit().getSignature());
						}

					}

				}

			}
		}

		return task_members;
	}
	
	private String getClassNameFromSignature(String signature) {
		String method_name = signature.substring(0, signature.indexOf("("));
		
		if (Character.isLowerCase(method_name.charAt(method_name.lastIndexOf('.') + 1)))
			return method_name.substring(0, method_name.lastIndexOf("."));
		
		return method_name;
	}
	
	private String getPackagePrefixFromSignature(String signature, int deep) {
		int i = 0, j = 0;
		
		while (i < signature.length()) {
			if (signature.charAt(i) == '.')
				++j;
			
			if (j == deep)
				break;
			
			++i;
		}
		
		return signature.substring(0, i);
	}
	
	private boolean matchesExcludeWord(String text) {
		return (exclude_word != null && !exclude_word.isEmpty() && text.contains(exclude_word));
	}
	
	private String matchesName(String path, String method_name, List<String> method_signatures) {
		String class_name = path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'));
		String class_dot_method = class_name + "." + method_name;
		
		for (String sig : method_signatures)
			if (sig.matches(".*[.]" + class_dot_method + "[(].*"))
				return sig;
		
		return null;
	}

}
