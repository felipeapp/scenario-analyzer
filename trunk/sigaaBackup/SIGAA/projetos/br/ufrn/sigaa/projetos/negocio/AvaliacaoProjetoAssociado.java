/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Implementa��o da estrat�gia de avalia��o de projetos de a��es acad�micas
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
	 * Realiza a avalia��o atualizando dados do projeto:
	 * 
	 * 1. verifica situa��o das avalia��es
	 * 2. verifica data fim dos membros do projeto
	 * 3. altera situa��o do projeto
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
	 * Realiza o c�lculo da m�dia do projeto com base nas avalia��es dos
	 * consultores ad hoc e membros do comit� interno.
	 * 
	 * @param projeto
	 * @return
	 */
	private void mediaComiteInterno(Projeto projeto) throws NegocioException {
		Avaliacao avaliacaoCI = projeto.getAvaliacaoComiteInterno();
		if (avaliacaoCI == null) {
			throw new NegocioException("Avalia��o do Projeto n�o pode ser realizada. " +
			"O projeto est� pendente de an�lise por um membro do comit� interno.");
		}

		//Recuperando nota Ad hoc mais pr�xima da nota dada pelo membro do comit� interno
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
	 * Realiza o c�lculo da m�dia do projeto com base somente nas avalia��es dos
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
