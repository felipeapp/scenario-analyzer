/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 21/12/2012
 * 
 */
package br.ufrn.sigaa.extensao.negocio.inscricoes_atividades;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;

/**
 *
 * <p>Passa os dados para o processador </p>
 * 
 * @author jadson
 *
 */
public class MovimentoEstornaPagamentoGRUCursosEventosExtensao  extends AbstractMovimentoAdapter{

	/** As inscrições  selecionadas pelo coordenador */
	private List<InscricaoAtividadeParticipante> inscricoesConfirmarEstornoPagamento;

	public MovimentoEstornaPagamentoGRUCursosEventosExtensao(List<InscricaoAtividadeParticipante> inscricoesConfirmarEstornoPagamento) {
		this.inscricoesConfirmarEstornoPagamento = inscricoesConfirmarEstornoPagamento;
		setCodMovimento(SigaaListaComando.ESTORNA_PAGAMENTO_GRU_CURSOS_EVENTOS_EXTENSAO);
	}

	public List<InscricaoAtividadeParticipante> getInscricoesConfirmarEstornoPagamento() {
		return inscricoesConfirmarEstornoPagamento;
	}
	
}
