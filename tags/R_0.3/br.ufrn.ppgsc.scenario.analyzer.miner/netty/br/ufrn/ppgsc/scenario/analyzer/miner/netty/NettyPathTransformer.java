package br.ufrn.ppgsc.scenario.analyzer.miner.netty;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IPathTransformer;

public class NettyPathTransformer implements IPathTransformer {

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
		
		// Se for enum ainda ficará com uma barra no final que precisa ser removida
		if (name.endsWith("/"))
			name = name.substring(0, name.lastIndexOf('/'));

		// Direciona para dentro do projeto correto
		if (method_signature.startsWith("io.netty.testsuite"))
			full_path += "testsuite/src/test/java/";
		else if (method_signature.startsWith("io.netty.channel") ||
				method_signature.startsWith("io.netty.bootstrap"))
			full_path += "transport/src/main/java/";
		else if (method_signature.startsWith("io.netty.buffer"))
			full_path += "buffer/src/main/java/";
		else if (method_signature.startsWith("io.netty.handler.codec.http") ||
				method_signature.startsWith("io.netty.handler.codec.rtsp") ||
				method_signature.startsWith("io.netty.handler.codec.spdy"))
			full_path += "codec-http/src/main/java/";
		else if (method_signature.startsWith("io.netty.handler.codec"))
			full_path += "codec/src/main/java/";
		else if (method_signature.startsWith("io.netty.handler"))
			full_path += "handler/src/main/java/";
		
		// Completa o nome do arquivo
		full_path += name + ".java";
		
		String result[] = new String[3];
		result[0] = repository_prefix + full_path;
		result[1] = workcopy_prefix_v1 + full_path;
		result[2] = workcopy_prefix_v2 + full_path;
		
		return result;
	}
	
}
