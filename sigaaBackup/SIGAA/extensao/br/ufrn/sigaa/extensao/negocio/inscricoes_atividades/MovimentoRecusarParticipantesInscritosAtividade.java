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
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
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
public class MovimentoRecusarParticipantesInscritosAtividade extends AbstractMovimentoAdapter {
	
	/** As inscrições para serem provadas */
	private List<InscricaoAtividadeParticipante> inscricoesSelecionadas;
	
	/** Atividade selecionada */
	private AtividadeExtensao atividadeSelecionada;

	
	
	public MovimentoRecusarParticipantesInscritosAtividade(List<InscricaoAtividadeParticipante> inscricoesSelecionadas, AtividadeExtensao atividadeSelecionada) {
		this.inscricoesSelecionadas = inscricoesSelecionadas;
		this.atividadeSelecionada = atividadeSelecionada;
		setCodMovimento(SigaaListaComando.RECUSAR_PARTICIPANTES_INSCRITOS_ATIVIDADE);
	}

	public List<InscricaoAtividadeParticipante> getInscricoesSelecionadas() {
		return inscricoesSelecionadas;
	}

	public AtividadeExtensao getAtividadeSelecionada() {
		return atividadeSelecionada;
	}
}
