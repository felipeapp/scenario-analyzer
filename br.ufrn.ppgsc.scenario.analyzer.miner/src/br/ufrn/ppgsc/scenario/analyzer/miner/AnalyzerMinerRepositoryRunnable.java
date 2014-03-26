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

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IContentIssue;
import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IPathTransformer;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedMethod;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.SystemPropertiesUtil;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.UpdatedMethodsMinerUtil;

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
		SystemPropertiesUtil properties = SystemPropertiesUtil.getInstance();
		
		transformer = properties.getPathTransformerProperty();
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
				String filename = "miner.log/" + system_id + "_" + name + "_" + strdate + ".log";
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
				"miner.log/" + system_id + "_" + partial_name + "_" + strdate + ".log", false));
		
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
					
					List<IContentIssue> tasks = up_line.getIssues();

					for (IContentIssue t : tasks) {
						if (t.getNumber() >= 0) {
							pw.println("Id:" + t.getId());
							pw.println("IdTipo:" + t.getIdType());
							pw.println("Número:" + t.getNumber());
							pw.println("TipoDenomicação:" + t.getTypeName());
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
	
	public void run() throws FileNotFoundException {
		int i = 0;
		
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
			
			if (!signatures.isEmpty()) {
				map_path_upmethod = UpdatedMethodsMinerUtil.getUpdatedMethodsFromRepository(
						url, user, password,
						repository_paths, old_workcopy_paths, new_workcopy_paths);
			}
			
			if (map_path_upmethod == null)
				map_path_upmethod = new HashMap<String, Collection<UpdatedMethod>>();
			
			// Os que foram modificados e estão dentro do critério (degradados, por exemplo)
			Map<String, Collection<UpdatedMethod>> filtrated_path_upmethod = new HashMap<String, Collection<UpdatedMethod>>();
			
			// Percorre a estrutura para filtrar os métodos de interesse para cada path
			for (String path : map_path_upmethod.keySet()) {
				for (UpdatedMethod upm : map_path_upmethod.get(path)) {
					/* 
					 * Testa se o nome do método casa com a assinatura do método
					 * Limitação quando o método tem formas diferentes com o mesmo nome
					 * A limitação é causada devido o parser do JDT que está sendo usado
					 */
					if (matchesName(upm.getMethodLimit().getSignature(), signatures)) {
						Collection<UpdatedMethod> upm_list = filtrated_path_upmethod.get(path);
						
						if (upm_list == null) {
							upm_list = new ArrayList<UpdatedMethod>();
							filtrated_path_upmethod.put(path, upm_list);
						}
						
						upm_list.add(upm);
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
			 * por exemplo, quantas vezes o tipo de tarefa aparece para métodos com desempenho
			 * degradado. Agora, são apenas os métodos modificados e afetados pelo problema analisado.
			 */
			Map<String, Collection<UpdatedMethod>> filtrated_task_members = getTaskMembers(filtrated_path_upmethod);
			Map<String, Integer> filtrated_counter_task_types = counterTaskTypes(filtrated_path_upmethod);
			
			// Persistir em arquivo os dados coletados
			persistFile(message, "svn_" + partial_names.get(i++), getTaskNumbers(filtrated_path_upmethod), filtrated_path_upmethod,
					counter_task_types, filtrated_counter_task_types, task_members, filtrated_task_members);
		}
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
	
	private boolean matchesName(String method_name, List<String> method_signatures) {
		for (String sig : method_signatures)
			if (sig.matches(".*[.]" + method_name + "[(].*"))
				return true;
		
		return false;
	}

}
