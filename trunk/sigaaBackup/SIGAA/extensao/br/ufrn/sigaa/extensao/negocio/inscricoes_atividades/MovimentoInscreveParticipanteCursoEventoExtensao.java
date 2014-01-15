/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 12/11/2012
 * 
 */
package br.ufrn.sigaa.extensao.negocio.inscricoes_atividades;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;

/**
 *
 * <p>Passa dos dados para o processador </p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
public class MovimentoInscreveParticipanteCursoEventoExtensao extends AbstractMovimentoAdapter{
	
	/** A inscrição que será realizada*/
	private InscricaoAtividadeParticipante inscricaoParticipante;

	/** A inscrição anterior do usuário, caso exista será cancelada quando ele fizar uma nova. */
	private List<InscricaoAtividadeParticipante> inscricoesParticipanteAnteriores;
	
	

	/** A inscrição anterior do usuário, caso exista será cancelada quando ele fizar uma nova. */
	private List<InscricaoAtividadeParticipante> inscricoesParticipanteAnterioresSubAtividade;
	
	public MovimentoInscreveParticipanteCursoEventoExtensao(InscricaoAtividadeParticipante inscricaoParticipante
				, List<InscricaoAtividadeParticipante> inscricoesParticipanteAnteriores
				, List<InscricaoAtividadeParticipante> inscricoesParticipanteAnterioresSubAtividade) {
		this.inscricaoParticipante = inscricaoParticipante;
		this.inscricoesParticipanteAnteriores = inscricoesParticipanteAnteriores;
		this.inscricoesParticipanteAnterioresSubAtividade = inscricoesParticipanteAnterioresSubAtividade;
		setCodMovimento(SigaaListaComando.INSCREVE_PARTICIPANTE_ATIVIDADE_EXTENSAO);
	}

	public InscricaoAtividadeParticipante getInscricaoParticipante() {
		return inscricaoParticipante;
	}

	public List<InscricaoAtividadeParticipante> getInscricoesParticipanteAnteriores() {
		return inscricoesParticipanteAnteriores;
	}

	public List<InscricaoAtividadeParticipante> getInscricoesParticipanteAnterioresSubAtividade() {
		return inscricoesParticipanteAnterioresSubAtividade;
	}
	
}
