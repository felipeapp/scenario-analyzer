/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 02/03/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.negocio;

import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.GeraPesquisaTextual;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 *
 * <p> Retorna o gerador da pesquisa textual para o banco que se estiver utilizando. </p>
 *
 * <p> <i> O gerador de pesquisa textual possui mecanimos que otimizam a pesquisa textual no acervo. Por�m cada 
 *  banco possui a sua estrat�gia de pesquia textual, por isso essa f�brica estancia o que estiver configurado no par�metro do sistema</i> 
 *  </p>
 * 
 * @author jadson
 *
 */
public class GeraPesquisaTextualFactory {

	
	public GeraPesquisaTextual getGeradorPesquisaTextual(){
		return ReflectionUtils.newInstance( ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.NOME_CLASSE_IMPLEMENTA_GERADOR_PESQUISA_TEXTUAL) );
	}
	
}
