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
import br.ufrn.sigaa.extensao.dominio.SubAtividadeExtensao;

/**
 *
 * <p>passa dos dados para processador </p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
public class MovimentoRecusarParticipantesInscritosSubAtividade extends AbstractMovimentoAdapter{

	/** As inscrições para serem provadas */
	private List<InscricaoAtividadeParticipante> inscricoesSelecionadas;
	
	/** Atividade selecionada */
	private SubAtividadeExtensao subAtividadeSelecionada;

	
	
	public MovimentoRecusarParticipantesInscritosSubAtividade(List<InscricaoAtividadeParticipante> inscricoesSelecionadas, SubAtividadeExtensao subAtividadeSelecionada) {
		this.inscricoesSelecionadas = inscricoesSelecionadas;
		this.subAtividadeSelecionada = subAtividadeSelecionada;
		setCodMovimento(SigaaListaComando.RECUSAR_PARTICIPANTES_INSCRITOS_SUB_ATIVIDADE);
	}

	public List<InscricaoAtividadeParticipante> getInscricoesSelecionadas() {
		return inscricoesSelecionadas;
	}

	public SubAtividadeExtensao getSubAtividadeSelecionada() {
		return subAtividadeSelecionada;
	}
}
