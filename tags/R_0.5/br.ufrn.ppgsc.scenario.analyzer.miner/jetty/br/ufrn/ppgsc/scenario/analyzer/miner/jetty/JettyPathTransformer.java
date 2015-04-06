package br.ufrn.ppgsc.scenario.analyzer.miner.jetty;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IPathTransformer;

/* TODO:
 * Os dados para essa classe deveriam estar vindo de um arquivo que mapeia os pacotes
 * para seu diretório correto. Acredito que um arquivo de proproedades resolveria,
 * e ai a implemantação poderia ficar genérica, igual para todos os sistemas.
 */
public class JettyPathTransformer implements IPathTransformer {

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
		if (name.endsWith("Test"))
			full_path += "jetty-servlet/src/test/java/";
		else if (method_signature.startsWith("org.eclipse.jetty.servlet"))
			full_path += "jetty-servlet/src/main/java/";
		else if (method_signature.startsWith("org.eclipse.jetty.util"))
			full_path += "jetty-util/src/main/java/";
		else if (method_signature.startsWith("org.eclipse.jetty.server"))
			full_path += "jetty-server/src/main/java/";
		else if (method_signature.startsWith("org.eclipse.jetty.io"))
			full_path += "jetty-io/src/main/java/";
		else if (method_signature.startsWith("org.eclipse.jetty.http"))
			full_path += "jetty-http/src/main/java/";
		else if (method_signature.startsWith("org.eclipse.jetty.security"))
			full_path += "jetty-security/src/main/java/";
		
		// Add .java to the file
		full_path += name + ".java";
		
		String result[] = new String[3];
		result[0] = repository_prefix + full_path;
		result[1] = workcopy_prefix_v1 + full_path;
		result[2] = workcopy_prefix_v2 + full_path;
		
		return result;
	}
	
}
