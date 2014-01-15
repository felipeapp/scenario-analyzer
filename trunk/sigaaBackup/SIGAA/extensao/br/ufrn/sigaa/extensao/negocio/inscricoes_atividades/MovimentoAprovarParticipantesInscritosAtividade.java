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
 * <p>Passa dos dados para o processador que irá aprovar.</p>
 *
 * @author jadson
 *
 */
public class MovimentoAprovarParticipantesInscritosAtividade extends AbstractMovimentoAdapter{

	/** As inscrições para serem provadas */
	private List<InscricaoAtividadeParticipante> inscricoesSelecioandas;
	
	/** Atividade selecionada */
	private AtividadeExtensao atividadeSelecionada;

	
	
	public MovimentoAprovarParticipantesInscritosAtividade(List<InscricaoAtividadeParticipante> inscricoesSelecioandas,AtividadeExtensao atividadeSelecionada) {
		this.inscricoesSelecioandas = inscricoesSelecioandas;
		this.atividadeSelecionada = atividadeSelecionada;
		setCodMovimento(SigaaListaComando.APROVAR_PARTICIPANTES_INSCRITOS_ATIVIDADE);
	}

	public List<InscricaoAtividadeParticipante> getInscricoesSelecioandas() {
		return inscricoesSelecioandas;
	}

	public AtividadeExtensao getAtividadeSelecionada() {
		return atividadeSelecionada;
	}
	
}
