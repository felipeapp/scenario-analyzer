package br.ufrn.ppgsc.scenario.analyzer.miner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import br.ufrn.ppgsc.scenario.analyzer.miner.db.DatabaseRelease;
import br.ufrn.ppgsc.scenario.analyzer.miner.db.GenericDB;
import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IContentIssue;
import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IPathTransformer;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedMethod;
import br.ufrn.ppgsc.scenario.analyzer.miner.svn.RepositoryManager;
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

	private static final String files[] =
		{"changed_methods", "excluded_methods", "failed_methods_both",
		"failed_methods_only_v1", "failed_methods_only_v2", "kept_methods",
		"p_degradated_methods", "p_optimized_methods", "p_unchanged_methods"};

	public AnalyzerMinerRepositoryRunnable(String strdate) throws FileNotFoundException {
		SystemMetadataUtil properties = SystemMetadataUtil.getInstance();
		
		transformer = properties.getPathTransformerObject();
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
				"miner_log/" + system_id + "_" + partial_name + "_" + strdate + ".txt", false));
		
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
					pw.println("Revis�o:" + up_line.getRevision());
					pw.println("Data:" + up_line.getDate());
					
					List<IContentIssue> tasks = up_line.getIssues();

					for (IContentIssue t : tasks) {
						if (t.getNumber() >= 0) {
							pw.println("Id:" + t.getId());
							pw.println("IdTipo:" + t.getIdType());
							pw.println("N�mero:" + t.getNumber());
							pw.println("TipoDenomica��o:" + t.getTypeName());
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
	
	private void persistFile(String message, String partial_name, List<String> members) throws FileNotFoundException {
		GenericDB database_v2 = DatabaseRelease.getDatabasev2();
		
		System.out.println("persistFile: " + message);
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(
				"miner_log/" + system_id + "_" + partial_name + "_" + strdate + ".txt", true));
		
		pw.println(message);
		pw.println(members.size());
		
		for (String sig : members) {
			System.out.println("Retrieving impacted members and scenarios from " + sig);
			
			Set<String> nodes = database_v2.getImpactedNodes(sig);
			List<String> scenarios = database_v2.getScenariosByMember(sig);
			
			pw.println(sig + ":" + nodes.size());
			pw.println(scenarios.size());
			
			for (String s : scenarios)
				pw.println(s);
		}
		
		pw.close();
	}
	
	public void run() throws FileNotFoundException {
		int i = 0;
		
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
			
			for (String s : signatures) {
				String paths[] = transformer.convert(s, rep_prefix, wc_prefix_v1, wc_prefix_v2);
				
				if (!new File(paths[1]).exists())
					throw new FileNotFoundException(paths[1]);
				
				if (!new File(paths[2]).exists())
					throw new FileNotFoundException(paths[2]);
				
				repository_paths.add(paths[0]);
				old_workcopy_paths.add(paths[1]);
				new_workcopy_paths.add(paths[2]);
			}
			
			System.out.println("Getting updated methods from repository to " + partial_names.get(i) + " ["
					+ repository_paths.size() + ", " + message + "]");
			
			Map<String, Collection<UpdatedMethod>> map_path_upmethod = null;
			
			// TODO: melhorar este código para evitar múltiplas conexões no repositório
			if (!signatures.isEmpty()) {
				map_path_upmethod = new RepositoryManager().getUpdatedMethodsFromRepository(
						url, user, password,
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
	}
	
	private Set<Long> getTaskNumbers(Map<String, Collection<UpdatedMethod>> map_path_methods) {
		Set<Long> issue_numbers = new HashSet<Long>();

		for (String path : map_path_methods.keySet())
			for (UpdatedMethod method : map_path_methods.get(path))
				for (UpdatedLine line : method.getUpdatedLines())
					for (IContentIssue issue : line.getIssues())
						issue_numbers.add(issue.getNumber());

		return issue_numbers;
	}
	
	private Map<String, Integer> counterTaskTypes(Map<String, Collection<UpdatedMethod>> map_path_methods) {
		Map<String, Integer> counter_task_types = new HashMap<String, Integer>();
		Set<Long> counted_tasks = new HashSet<Long>();

		for (String path : map_path_methods.keySet()) {

			for (UpdatedMethod method : map_path_methods.get(path)) {

				for (UpdatedLine line : method.getUpdatedLines()) {

					for (IContentIssue issue : line.getIssues()) {

						if (issue.getId() != -1 && !counted_tasks.contains(issue.getId())) {
							Integer counter = counter_task_types.get(issue.getTypeName());

							if (counter == null)
								counter_task_types.put(issue.getTypeName(), 1);
							else
								counter_task_types.put(issue.getTypeName(), counter + 1);
							
							counted_tasks.add(issue.getId());
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

					for (IContentIssue issue : line.getIssues()) {

						if (issue.getId() != -1 && !(counted_tasks.contains(issue.getId()) && counted_members.contains(method.getMethodLimit().getSignature()))) {
							Collection<UpdatedMethod> list = task_members.get(issue.getTypeName());

							if (list == null) {
								list = new ArrayList<UpdatedMethod>();
								list.add(method);
								task_members.put(issue.getTypeName(), list);
							}
							else {
								task_members.get(issue.getTypeName()).add(method);
							}
							
							counted_tasks.add(issue.getId());
							counted_members.add(method.getMethodLimit().getSignature());
						}

					}

				}

			}
		}

		return task_members;
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
