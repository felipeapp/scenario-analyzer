package br.ufrn.ppgsc.scenario.analyzer.miner.sigaa;

import br.ufrn.ppgsc.scenario.analyzer.miner.IPathTransformer;

/*
Versão 01
01_Arquitetura 2.6.25
02_EntidadesComuns 1.2.56
03_ServicosIntegrados 1.3.25
ServicoRemotoBiblioteca
SIGAA 3.11.24
*/

/*
Versão 02
01_Arquitetura 2.6.29
02_EntidadesComuns 1.2.57
03_ServicosIntegrados 1.3.28
ServicoRemotoBiblioteca
SIGAA 3.12.18
*/

public class SIGAAPathTransformer implements IPathTransformer {

	public String[] convert(String method_signature, String repository_prefix, String workcopy_prefix_v1, String workcopy_prefix_v2) {
		String full_path = "";
		
		if (method_signature.startsWith("br.ufrn.arq"))
			full_path += "Arquitetura/src/";
		else if (method_signature.startsWith("br.ufrn.sigaa.arq.dao.DiscenteAdapterListener"))
			full_path += "SIGAA/arq/";
		else if (method_signature.startsWith("br.ufrn.comum.dao.PermissaoDAO"))
			full_path += "EntidadesComuns/daos/";
		else if (method_signature.startsWith("br.ufrn.rh.dominio.Categoria"))
			full_path += "EntidadesComuns/dominio/";
		else if (method_signature.startsWith("br.ufrn.rh.dominio.Cargo"))
			full_path += "EntidadesComuns/dominio/";
		else if (method_signature.startsWith("br.ufrn.comum"))
			full_path += "Arquitetura/comum/";
		else if (method_signature.startsWith("br.ufrn.rh.dominio"))
			full_path += "Arquitetura/comum/";
		else if (method_signature.startsWith("br.ufrn.ambientes"))
			full_path += "Arquitetura/ambientes/";
		else if (method_signature.startsWith("br.ufrn.services"))
			full_path += "Arquitetura/services/";
		else if (method_signature.startsWith("br.ufrn.integracao"))
			full_path += "ServicosIntegrados/integracao/";
		else if (method_signature.startsWith("br.ufrn.sigaa.biblioteca.integracao"))
			full_path += "ServicoRemotoBiblioteca/src/";
		else if (method_signature.startsWith("br.ufrn.academico.dominio"))
			full_path += "EntidadesComuns/dominio/";
		else if (method_signature.startsWith("br.ufrn.sigaa.pessoa.dominio"))
			full_path += "SIGAA/geral/";
		else if (method_signature.startsWith("br.ufrn.sigaa.ensino"))
			full_path += "SIGAA/ensino/";
		else if (method_signature.startsWith("br.ufrn.sigaa.biblioteca"))
			full_path += "SIGAA/biblioteca/";
		else if (method_signature.startsWith("br.ufrn.sigaa.arq.dao"))
			full_path += "SIGAA/arq_dao/";
		else if (method_signature.startsWith("br.ufrn.sigaa.arq"))
			full_path += "SIGAA/arq/";
		else if (method_signature.startsWith("br.ufrn.sigaa.dominio"))
			full_path += "SIGAA/geral/";
		
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
				
		full_path += name + ".java";
		
		String result[] = new String[3];
		
		if (full_path.startsWith("ServicoRemotoBiblioteca"))
			repository_prefix = "/trunk/";
		
		result[0] = repository_prefix + full_path;
		result[1] = workcopy_prefix_v1 + full_path;
		result[2] = workcopy_prefix_v2 + full_path;
		
		return result;
	}
	
}
