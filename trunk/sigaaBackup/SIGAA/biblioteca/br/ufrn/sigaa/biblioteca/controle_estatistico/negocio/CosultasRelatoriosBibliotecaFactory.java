/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 13/06/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.negocio;

import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioDeUsuariosComPotencialEmprestimo;

/**
 *
 * <p> F�brica que retorna algumas dados utilizados nas consultas dos relat�rios </p>
 *
 * <p> <i> Criar pontos de extern��o para suportar outras regras na consultas dos relat�rios</i> </p>
 * 
 * @author jadson
 *
 */
public class CosultasRelatoriosBibliotecaFactory {

	/**
	 * Retorna a classe das consultas de categorias de usu�rios da biblioteca
	 *
	 * @return
	 */
	public Class<RelatorioDeUsuariosComPotencialEmprestimo> getClasseGeraConsultasUsuariosComPontenciaEmprestimo(){
		return ReflectionUtils.classForName("br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioDeUsuariosComPotencialEmprestimoDao");
	}
	
}
