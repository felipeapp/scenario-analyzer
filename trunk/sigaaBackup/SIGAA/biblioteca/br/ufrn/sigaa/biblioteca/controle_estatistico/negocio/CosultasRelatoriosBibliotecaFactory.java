/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p> Fábrica que retorna algumas dados utilizados nas consultas dos relatórios </p>
 *
 * <p> <i> Criar pontos de externção para suportar outras regras na consultas dos relatórios</i> </p>
 * 
 * @author jadson
 *
 */
public class CosultasRelatoriosBibliotecaFactory {

	/**
	 * Retorna a classe das consultas de categorias de usuários da biblioteca
	 *
	 * @return
	 */
	public Class<RelatorioDeUsuariosComPotencialEmprestimo> getClasseGeraConsultasUsuariosComPontenciaEmprestimo(){
		return ReflectionUtils.classForName("br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioDeUsuariosComPotencialEmprestimoDao");
	}
	
}
