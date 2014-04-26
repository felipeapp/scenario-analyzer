package br.ufrn.ppgsc.scenario.analyzer.miner.argouml;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IPathTransformer;

public class ArgoUMLPathTransformer implements IPathTransformer {

	public String[] convert(String method_signature, String repository_prefix, String workcopy_prefix_v1, String workcopy_prefix_v2) {
		String full_path = "";
		String name = method_signature.replaceAll("[(].*[)]", "");
		
		if (method_signature.startsWith("org.argouml.model.mdr"))
			full_path = "argouml-core-model-mdr/";
		else
			full_path = "argouml-app/"; 
		
		/*
		 * Verifica a pasta dependente se é uma classe de teste ou
		 * uma classe da aplicação.
		 */
		if (name.substring(name.lastIndexOf('.') + 1).startsWith("Test"))
			full_path = "test/";
		else
			full_path = "src/";
		
		/* 
		 * Se é um método (nome inicia com minúsculo)
		 * Isso precisa ser melhorado com um booleano nas próximas versões
		 */
		if (Character.isLowerCase(name.charAt(name.lastIndexOf('.') + 1)))
			name = name.replaceAll("[.][^.]+$", "");
		
		name = name.replaceAll("[.]", "/").replaceAll("[$].*", "");
		
		// Se for enum ainda ficará com uma barra no final que precisa ser removida
		if (name.endsWith("/"))
			name = name.substring(0, name.lastIndexOf('/'));
				
		full_path += name + ".java";
		
		String result[] = new String[3];
		result[0] = repository_prefix + full_path;
		result[1] = workcopy_prefix_v1 + full_path;
		result[2] = workcopy_prefix_v2 + full_path;
		
		return result;
	}
	
}
