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
			Map<String, Collection<UpdatedMethod>> map_path_upmethod,
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
		
		pw.println(map_path_upmethod.size());
		
		for (String path: map_path_upmethod.keySet()) {
			pw.println(path);
			
			Collection<UpdatedMethod> upmethod_list = map_path_upmethod.get(path);
			
			pw.println(upmethod_list.size());
			
			for (UpdatedMethod method : upmethod_list) {
				List<UpdatedLine> upl_list = method.getUpdatedLines();
				
				pw.print(method.getMethodLimit().getSignature() + " ");
				pw.print(method.getMethodLimit().getStartLine() + " ");
				pw.println(method.getMethodLimit().getEndLine());
				pw.println(upl_list.size());
				
				for (UpdatedLine up_line : upl_list) {
					pw.println("Autor:" + up_line.getAuthor());
					pw.println("Linha:" + up_line.getLineNumber());
					pw.println("Revisão:" + up_line.getRevision());
					pw.println("Data:" + up_line.getDate());
					
					List<Issue> tasks = up_line.getIssues();

					for (Issue t : tasks) {
						if (t.getNumber() >= 0) {
							pw.println("Id:" + t.getIssueId());
							pw.println("IdTipo:" + t.getIssueTypeId());
							pw.println("Número:" + t.getNumber());
							pw.println("TipoDenomicação:" + t.getIssueType());
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
	
	private void persistFile(String message, String partial_name, List<String> members) throws FileNotFoundException {
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
	
	private void persistScenariosWithBlames(String message, String partial_name, Map<String, List<String>> scenariosWithBlames) throws FileNotFoundException {
		int total_members = 0;
		int total_scenarios_with_blames = 0;
		int total_members_without_word = 0;
		
		Set<String> counted = new HashSet<String>();
		
		System.out.println("persistFile: " + message);
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(
				"miner_log/" + system_id + "_" + partial_name + "_" + strdate + ".txt"), true);
		
		for (List<String> list : scenariosWithBlames.values())
			if (!list.isEmpty())
				++total_scenarios_with_blames;
		
		pw.println(message);
		
		// Cenários que possuem blames
		pw.println(total_scenarios_with_blames);
		
		/* 
		 * Todos os cenários, alguns podem não ter blames.
		 * Isso acontece, por exemplo, devido somatórios de membros
		 * degradados, pequenos aumentos individuais (menor que a taxa)
		 * que quando somados impactam o cenário (com variação maior que a taxa)
		 */
		pw.println(scenariosWithBlames.size());
		
		for (String scenario : scenariosWithBlames.keySet()) {
			List<String> signatures = scenariosWithBlames.get(scenario);
			
			Collections.sort(signatures);
			
			pw.println(scenario);
			
			int i = 0;
			for (String s : signatures)
				if (!matchesExcludeWord(s))
					++i;
			
			pw.println(signatures.size() + " " + i);
			
			for (String s : signatures) {
				pw.println(s);
				
				if (!counted.contains(s)) {
					counted.add(s);
					++total_members;
					
					if (!matchesExcludeWord(s))
						++total_members_without_word;
				}
			}
		}
		
		pw.println(total_members);
		pw.println(total_members_without_word);
		pw.close();
	}
	
	public void run() throws FileNotFoundException {
		int i = 0;
		
		/*
		 * O gerente de repositório conecta ao repositório e permite acesso
		 * para a mineração dos dados através do método disponibilizado.
		 * Conecta no momento que é criado.
		 */
		RepositoryManager repository = new RepositoryManager(url, user, password);
		
		/* 
		 * Guarda as assinaturas de métodos que contribuiram especificamente
		 * para a degradação do desempenho. Esta lista será usada na mineração final.
		 */
		List<String> p_degradated_changed_methods = new ArrayList<String>();
		
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
			
			// Os que foram modificados e estão dentro do critério analisado (degradados, por exemplo)
			Map<String, Collection<UpdatedMethod>> filtrated_path_upmethod = new HashMap<String, Collection<UpdatedMethod>>();
			
			// Percorre a estrutura para filtrar os métodos de interesse para cada path
			for (String path : map_path_upmethod.keySet()) {
				for (UpdatedMethod upm : map_path_upmethod.get(path)) {
					/* 
					 * Testa se o nome do método casa com a assinatura do método
					 * Limitação quando o método tem formas diferentes com o mesmo nome
					 * A limitação é causada devido o parser do JDT que está sendo usado
					 */
					String sig_matched = matchesName(path, upm.getMethodLimit().getSignature(), signatures);
					
					if (sig_matched != null) {
						Collection<UpdatedMethod> upm_list = filtrated_path_upmethod.get(path);
						
						if (upm_list == null) {
							upm_list = new ArrayList<UpdatedMethod>();
							filtrated_path_upmethod.put(path, upm_list);
						}
						
						upm_list.add(upm);
						
						if (partial_names.get(i).equals("changed_methods") || partial_names.get(i).equals("p_degradated_methods"))
							p_degradated_changed_methods.add(sig_matched);
					}
				}
			}
			
			/*
			 * Conta quantas vezes o tipo de tarefa ocorreu em toda a evolução, considerando
			 * as classes dos métodos para o problema sendo analisado. Note que algumas classes
			 * podem ter mudanças em métodos, mas estes não terem sido degradados neste caso.
			 */
			Map<String, Collection<UpdatedMethod>> task_members = getTaskMembers(map_path_upmethod);
			Map<String, Integer> counter_task_types = counterTaskTypes(map_path_upmethod);
			
			/*
			 * Conta quantas vezes o tipo de tarefa ocorreu para o problema sendo analisado,
			 * por exemplo, quantas vezes o tipo de tarefa aparece para m�todos com desempenho
			 * degradado. Agora, são apenas os métodos modificados e afetados pelo problema analisado.
			 */
			Map<String, Collection<UpdatedMethod>> filtrated_task_members = getTaskMembers(filtrated_path_upmethod);
			Map<String, Integer> filtrated_counter_task_types = counterTaskTypes(filtrated_path_upmethod);
			
			// Persistir em arquivo os dados coletados
			persistFile(message, "svn_" + partial_names.get(i++), getTaskNumbers(filtrated_path_upmethod), filtrated_path_upmethod,
					counter_task_types, filtrated_counter_task_types, task_members, filtrated_task_members);
		}
		
		// Mostrando o impacto dos responsáveis pela degradação de performance
		persistFile("# Métodos responsáveis pela degradação de performance", "methods_performance_degradation", p_degradated_changed_methods);
		
		// Recuperando os cenários degradados e os membros culpados
		Map<String, List<String>> scenariosWithBlames = getScenariosWithBlames(
				"p_degraded_scenarios", "methods_performance_degradation");
		
		// Mostrando os cenários degradados e os membros culpados
		persistScenariosWithBlames("# Membros responsáveis pela degradação de performance em cada cenários",
				"scenarios_blames_performance_degradation", scenariosWithBlames);
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

						if (issue.getIssueId() != -1 && !counted_tasks.contains(issue.getIssueId())) {
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

						if (issue.getIssueId() != -1 && !(counted_tasks.contains(issue.getIssueId()) && counted_members.contains(method.getMethodLimit().getSignature()))) {
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
