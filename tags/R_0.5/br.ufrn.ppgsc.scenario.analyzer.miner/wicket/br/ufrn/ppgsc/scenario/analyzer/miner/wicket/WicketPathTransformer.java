package br.ufrn.ppgsc.scenario.analyzer.miner.wicket;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IPathTransformer;

/* TODO:
 * Os dados para essa classe deveriam estar vindo de um arquivo que mapeia os pacotes
 * para seu diretório correto. Acredito que um arquivo de proproedades resolveria,
 * e ai a implemantação poderia ficar genérica, igual para todos os sistemas.
 */
public class WicketPathTransformer implements IPathTransformer {

	public String[] convert(String method_signature, String repository_prefix, String workcopy_prefix_v1, String workcopy_prefix_v2) {

		String full_path = "";
		String name = method_signature.replaceAll("[(].*[)]", "");
		
		/* TODO: Fazer isso com uma consulta no banco depois 
		 * Testa se é um método (inicia com caractere não maiúsculo)
		 * Isso precisa ser melhorado com um booleano nas próximas versões
		 */
		if (!Character.isUpperCase(name.charAt(name.lastIndexOf('.') + 1)))
			name = name.replaceAll("[.][^.]+$", "");
		
		name = name.replaceAll("[.]", "/").replaceAll("[$].*", "");
		
		// If it is Enum, it will have a / at the end. We need to remove.
		if (name.endsWith("/"))
			name = name.substring(0, name.lastIndexOf('/'));

		// Switch the directory
		if (method_signature.startsWith("org.apache.wicket.util.tester")
				|| method_signature.startsWith("org.apache.wicket.util.cookies")
				|| method_signature.startsWith("org.apache.wicket.request.handler.render")
				|| method_signature.startsWith("org.apache.wicket.request.cycle")
				|| method_signature.startsWith("org.apache.wicket.request.resource")
				|| method_signature.startsWith("org.apache.wicket.request.handler.resource"))
			full_path += "wicket-core/src/main/java/";
		
		else if (method_signature.startsWith("org.apache.wicket.authroles"))
			full_path += "wicket-auth-roles/src/main/java/";
				
		else if (method_signature.startsWith("org.apache.wicket.examples")
				|| method_signature.startsWith("org.apache.wicket.filtertest")
				|| method_signature.startsWith("org.apache.wicket.util.markup.xhtml") && name.endsWith("Test"))
			if (name.endsWith("Test") || name.endsWith("TestCase"))
				full_path += "wicket-examples/src/test/java/";
			else
				full_path += "wicket-examples/src/main/java/";
		
		else if (method_signature.startsWith("org.apache.wicket.atmosphere"))
			full_path += "wicket-experimental/wicket-atmosphere/src/main/java/";
		
		else if (method_signature.startsWith("org.apache.wicket.request"))
			full_path += "wicket-request/src/main/java/";
		
		else if (method_signature.startsWith("org.apache.wicket.devutils"))
			full_path += "wicket-devutils/src/main/java/";
		
		else if (method_signature.startsWith("org.apache.wicket.jmx"))
			full_path += "wicket-jmx/src/main/java/";
		
		else if (method_signature.startsWith("org.apache.wicket.velocity"))
			full_path += "wicket-velocity/src/main/java/";
		
		else if (method_signature.startsWith("org.apache.wicket.extensions"))
			full_path += "wicket-extensions/src/main/java/";
		
		else if (method_signature.startsWith("org.apache.wicket.util"))
			full_path += "wicket-util/src/main/java/";
		
		else if (method_signature.startsWith("org.apache.wicket"))
			full_path += "wicket-core/src/main/java/";
		
		// Add .java to the file
		full_path += name + ".java";
		
		String result[] = new String[3];
		result[0] = repository_prefix + full_path;
		result[1] = workcopy_prefix_v1 + full_path;
		result[2] = workcopy_prefix_v2 + full_path;
		
		return result;
	}
	
}
