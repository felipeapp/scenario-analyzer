/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p> <i> O gerador de pesquisa textual possui mecanimos que otimizam a pesquisa textual no acervo. Porém cada 
 *  banco possui a sua estratégia de pesquia textual, por isso essa fábrica estancia o que estiver configurado no parâmetro do sistema</i> 
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
