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
 * <p>TODO Insira seu comentario aqui </p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
public class MovimentoAprovarParticipantesInscritosSubAtividade extends AbstractMovimentoAdapter{
	
	/** As inscrições para serem provadas */
	private List<InscricaoAtividadeParticipante> inscricoesSelecioandas;
	
	/** Atividade selecionada */
	private SubAtividadeExtensao subAtividadeSelecionada;

	
	
	public MovimentoAprovarParticipantesInscritosSubAtividade(List<InscricaoAtividadeParticipante> inscricoesSelecioandas, SubAtividadeExtensao subAtividadeSelecionada) {
		this.inscricoesSelecioandas = inscricoesSelecioandas;
		this.subAtividadeSelecionada = subAtividadeSelecionada;
		setCodMovimento(SigaaListaComando.APROVAR_PARTICIPANTES_INSCRITOS_SUB_ATIVIDADE);
	}

	public List<InscricaoAtividadeParticipante> getInscricoesSelecioandas() {
		return inscricoesSelecioandas;
	}

	public SubAtividadeExtensao getSubAtividadeSelecionada() {
		return subAtividadeSelecionada;
	}
	
}
