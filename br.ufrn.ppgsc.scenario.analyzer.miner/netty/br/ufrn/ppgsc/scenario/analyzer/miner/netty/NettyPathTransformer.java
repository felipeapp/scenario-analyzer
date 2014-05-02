package br.ufrn.ppgsc.scenario.analyzer.miner.netty;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IPathTransformer;

public class NettyPathTransformer implements IPathTransformer {

	public String[] convert(String method_signature, String repository_prefix, String workcopy_prefix_v1, String workcopy_prefix_v2) {

		String full_path = "";
		String name = method_signature.replaceAll("[(].*[)]", "");
		
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
		
		// Refazendo o caminho de classes internas
		if (name.equals("org/argouml/profile/internal/DummyDependencyChecker"))
			name = "org/argouml/profile/internal/TestDependencyResolver";
		// ---------------------------------------

		if (method_signature.startsWith("io.netty.testsuite"))
			full_path += "testsuite/src/test/java/";
		// Redirecionando para o projeto correto de acordo com pacote
//		if (method_signature.startsWith("io.netty.buffer"))
//			full_path = "buffer/";
//		else if (method_signature.startsWith("io.netty.handler.codec"))
//			full_path = "codec/";
//		else if (method_signature.startsWith("io.netty.testsuite.transport"))
//			full_path = "transport/";
		
		
		// ---------------------------------------------------------
		
		// Completa o nome do arquivo
		full_path += name + ".java";
		
		String result[] = new String[3];
		result[0] = repository_prefix + full_path;
		result[1] = workcopy_prefix_v1 + full_path;
		result[2] = workcopy_prefix_v2 + full_path;
		
		return result;
	}
	
	private boolean belongsToTestFolder(String class_path) {
		String name = class_path.substring(class_path.lastIndexOf('/') + 1);
		
		return name.equals("ThreadHelper") || name.equals("CheckUMLModelHelper") ||
				name.equals("AbstractUMLModelElementListModel2Test") ||
				name.equals("InitializeModel") || name.startsWith("Test") ||
				name.startsWith("AbstractTest") || name.startsWith("GenericUmlObjectTest");
	}
	
}
