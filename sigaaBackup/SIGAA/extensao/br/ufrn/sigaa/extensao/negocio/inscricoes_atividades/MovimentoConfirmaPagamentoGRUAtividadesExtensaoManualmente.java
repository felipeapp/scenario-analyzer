/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 28/11/2012
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
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
public class MovimentoConfirmaPagamentoGRUAtividadesExtensaoManualmente extends AbstractMovimentoAdapter{

	/** As inscrições  selecionadas pelo coordenador */
	private List<InscricaoAtividadeParticipante> inscricoesConfirmarPagamento;

	public MovimentoConfirmaPagamentoGRUAtividadesExtensaoManualmente(List<InscricaoAtividadeParticipante> inscricoesConfirmarPagamento) {
		this.inscricoesConfirmarPagamento = inscricoesConfirmarPagamento;
		setCodMovimento(SigaaListaComando.CONFIRMA_PAGAMENTO_MANUAL_GRU_CURSOS_EVENTOS_EXTENSAO);
	}

	public List<InscricaoAtividadeParticipante> getInscricoesConfirmarPagamento() {
		return inscricoesConfirmarPagamento;
	}
	
}
