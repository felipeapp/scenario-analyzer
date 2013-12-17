package br.ufrn.ppgsc.scenario.analyzer.miner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import br.ufrn.ppgsc.scenario.analyzer.miner.model.IProjectTask;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedMethod;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.UpdatedMethodsMinerUtil;

public final class AnalyzerMinerRepositoryRunnable {

	private List<Scanner> readers;
	private List<String> partial_names;
	private IPathTransformer transformer;
	
	private String url;
	private String prefix;
	private String user;
	private String password;
	private String system_id;
	private String strdate;

	private static final String files[] =
		{"changed_methods", "excluded_methods", "failed_methods_both",
		"failed_methods_only_v1", "failed_methods_only_v2", "kept_methods",
		"p_degradated_methods", "p_optimized_methods", "p_unchanged_methods"};

	public AnalyzerMinerRepositoryRunnable(Properties properties,
			String strdate, IPathTransformer o) throws FileNotFoundException {
		transformer = o;
		this.strdate = strdate;
		readers = new ArrayList<Scanner>();
		partial_names = new ArrayList<String>();
		
		url = properties.getProperty("repository_url");
		prefix = properties.getProperty("repository_prefix");
		user = properties.getProperty("repository_user");
		password = properties.getProperty("repository_password");
		system_id = properties.getProperty("system_id");
		
		for (String name : files) {
			boolean active = Boolean.parseBoolean(properties.getProperty(name));
			
			if (active) {
				String filename = "log/" + system_id + "_" + name + "_" + strdate + ".log";
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
	
	private void persistFile(String message, String partial_name,
			Map<String, Collection<UpdatedMethod>> map_path_upmethod) throws FileNotFoundException {
		System.out.println("persistFile: " + message);
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(
				"log/" + system_id + "_" + partial_name + "_" + strdate + ".log", true));
		
		pw.println(message);
		pw.println(map_path_upmethod.size());
		
		for (String path: map_path_upmethod.keySet()) {
			pw.println(path);
			
			Collection<UpdatedMethod> upmethod_list = map_path_upmethod.get(path);
			
			pw.println(upmethod_list.size());
			
			for (UpdatedMethod method : upmethod_list) {
				System.out.println(method.getMethodLimit().getSignature());
				
				List<UpdatedLine> upl_list = method.getUpdatedLines();
				
				pw.println(upl_list.size());
				
				for (UpdatedLine up_line : upl_list) {
					pw.println("Autor:" + up_line.getAuthor());
					pw.println("Linha:" + up_line.getLineNumber());
					pw.println("Revisão:" + up_line.getRevision());
					pw.println("Data:" + up_line.getDate());
					
					List<IProjectTask> tasks = up_line.getTasks();
					
					pw.println(tasks.size());
					
					for (IProjectTask t : tasks) {
						pw.println("Id:" + t.getId());
						pw.println("IdTipo:" + t.getIdTipo());
						pw.println("Número:" + t.getNumero());
						pw.println("TipoDenomicação:" + t.getTipoDenominacao());
					}
				}
			}
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
			
			System.out.println(message);
			System.out.println(signatures.size());
			
			for (String s : signatures) {
				String paths[] = transformer.convert(s);
				
				if (!new File(paths[1]).exists())
					throw new FileNotFoundException(paths[1]);
				
				if (!new File(paths[2]).exists())
					throw new FileNotFoundException(paths[2]);
				
				repository_paths.add(prefix + paths[0]);
				old_workcopy_paths.add(paths[1]);
				new_workcopy_paths.add(paths[2]);
			}
			
			Map<String, Collection<UpdatedMethod>> map_path_upmethod = UpdatedMethodsMinerUtil.getUpdatedMethodsFromRepository(
					url, user, password,
					repository_paths, old_workcopy_paths, new_workcopy_paths);
			
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
			
			// Persistir em arquivo os dados coletados
			persistFile(message, partial_names.get(i++), filtrated_path_upmethod);
		}
	}

	private boolean matchesName(String method_name, List<String> method_signatures) {
		for (String sig : method_signatures)
			if (sig.matches(".*[.]" + method_name + "[(].*"))
				return true;
		
		return false;
	}

}
