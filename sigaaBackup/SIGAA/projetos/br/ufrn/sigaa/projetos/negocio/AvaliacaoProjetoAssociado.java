/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 * 
 * Created on 18/11/2010
 * 
 */
package br.ufrn.sigaa.projetos.negocio;

import java.util.List;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.projetos.dominio.Avaliacao;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoAvaliador;

/**
 * Implementação da estratégia de avaliação de projetos de ações acadêmicas
 * integradas.
 * 
 * @author Ilueny Santos
 * 
 */
public class AvaliacaoProjetoAssociado implements EstrategiaAvaliacaoProjetos {

	@Override
	public void classificar(List<Projeto> projetos) throws NegocioException {
	}

	/**
	 * Realiza a avaliação atualizando dados do projeto:
	 * 
	 * 1. verifica situação das avaliações
	 * 2. verifica data fim dos membros do projeto
	 * 3. altera situação do projeto
	 * 
	 */
	@Override
	public void avaliar(Avaliacao avaliacao) throws NegocioException {
		Projeto pj = avaliacao.getProjeto();
		if (avaliacao.getDistribuicao().getTipoAvaliador().getId() == TipoAvaliador.COMITE_INTEGRADO_ENSINO_PESQUISA_EXTENSAO) {
			mediaComiteInterno(pj);
		}else {
			mediaConsultoresAdHoc(pj);
		}
	}

	/**
	 * Realiza o cálculo da média do projeto com base nas avaliações dos
	 * consultores ad hoc e membros do comitê interno.
	 * 
	 * @param projeto
	 * @return
	 */
	private void mediaComiteInterno(Projeto projeto) throws NegocioException {
		Avaliacao avaliacaoCI = projeto.getAvaliacaoComiteInterno();
		if (avaliacaoCI == null) {
			throw new NegocioException("Avaliação do Projeto não pode ser realizada. " +
			"O projeto está pendente de análise por um membro do comitê interno.");
		}

		//Recuperando nota Ad hoc mais próxima da nota dada pelo membro do comitê interno
		double notaMaisProximaCI = 0;
		for (Avaliacao av : projeto.getAvaliacoesAtivas()) {
			if (!av.isAvaliacaoComiteInterno() && av.getDataAvaliacao() != null) {
				if (av.getNota() == avaliacaoCI.getNota()) {
					notaMaisProximaCI = avaliacaoCI.getNota();
					break;
				} else if (Math.abs(avaliacaoCI.getNota() - av.getNota()) <  Math.abs(avaliacaoCI.getNota() - notaMaisProximaCI)) {
					notaMaisProximaCI = av.getNota();
				}
			}
		}
		if (notaMaisProximaCI == 0) {
			projeto.setMedia(avaliacaoCI.getNota());
		}else {
			projeto.setMedia((notaMaisProximaCI + avaliacaoCI.getNota()) / 2);
		}
	}
	
	/**
	 * Realiza o cálculo da média do projeto com base somente nas avaliações dos
	 * consultores ad hoc.
	 * 
	 * @param projeto
	 * @return
	 */
	private void mediaConsultoresAdHoc(Projeto projeto) {
		double soma = 0.0;
		int totAvaliacoes = 0;		
		for (Avaliacao av : projeto.getAvaliacoesAtivas()) {
			if (!av.isAvaliacaoComiteInterno()) {
				totAvaliacoes++;
				soma += av.getNota(); 				
			}
		}
		if (totAvaliacoes > 0) {
			projeto.setMedia(soma / totAvaliacoes);
		} else {
			projeto.setMedia(0.0);
		}
	}

}
