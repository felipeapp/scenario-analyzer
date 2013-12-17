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

public class PathTransformerSIGAA implements IPathTransformer {

	public static final String arquitetura1 = "01_Arquitetura 2.6.25";
	public static final String comum1       = "02_EntidadesComuns 1.2.56";
	public static final String integrado1   = "03_ServicosIntegrados 1.3.25";
	public static final String remoto_bib1  = "ServicoRemotoBiblioteca";
	public static final String sigaa1       = "SIGAA 3.11.24";
	
	public static final String arquitetura2 = "01_Arquitetura 2.6.29";
	public static final String comum2       = "02_EntidadesComuns 1.2.57";
	public static final String integrado2   = "03_ServicosIntegrados 1.3.28";
	public static final String remoto_bib2  = "ServicoRemotoBiblioteca";
	public static final String sigaa2       = "SIGAA 3.12.18";
	
	@Override
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
		else if (method_signature.startsWith("br.ufrn.sigaa.biblioteca"))
			full_path += "SIGAA/biblioteca/";
		else if (method_signature.startsWith("br.ufrn.sigaa.arq.dao"))
			full_path += "SIGAA/arq_dao/";
		else if (method_signature.startsWith("br.ufrn.sigaa.arq"))
			full_path += "SIGAA/arq/";
		else if (method_signature.startsWith("br.ufrn.sigaa.dominio"))
			full_path += "SIGAA/geral/";
		
		if (isConstructor(method_signature)) {
			method_signature = method_signature.substring(
					method_signature.indexOf("br", 2), method_signature.indexOf("("));
		}
		
		full_path += method_signature
				.replaceAll("[.][^.]+[(].*[)]", "")
				.replaceAll("[.]", "/")
				.replaceAll("[$][0-9]+", "");
		
		full_path += ".java";
		
		String result[] = new String[3];
		
		if (full_path.startsWith("ServicoRemotoBiblioteca"))
			repository_prefix = "/trunk/";
		
		result[0] = repository_prefix + full_path;
		result[1] = workcopy_prefix_v1 + full_path;
		result[2] = workcopy_prefix_v2 + full_path;
		
		return result;
	}

	private boolean isConstructor(String sig) {
		return sig.indexOf("br.", 3) != -1 && sig.indexOf("br.", 3) <= sig.indexOf("(");
	}
	
}
