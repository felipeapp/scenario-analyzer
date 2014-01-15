/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 30/10/2009
 */
package br.ufrn.sigaa.ava.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dominio.TopicoAula;

/**
 * Movimento que transporta as informa��es para se cadastrar ou alterar
 * v�rios t�picos de aula em uma �nica transa��o.
 * 
 * @author Fred de Castro
 *
 */

public class MovimentoGerenciaTopicosAula extends AbstractMovimentoAdapter {
	List <TopicoAula> topicosAula;
	
	public MovimentoGerenciaTopicosAula (List <TopicoAula> topicosAula){
		this.topicosAula = topicosAula;
		
		setCodMovimento(SigaaListaComando.GERENCIAR_TOPICOS_AULA_EM_LOTE);
	}

	public List<TopicoAula> getTopicosAula() {
		return topicosAula;
	}
}