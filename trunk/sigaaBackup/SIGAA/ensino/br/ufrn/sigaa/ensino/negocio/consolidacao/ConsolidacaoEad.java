/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 10/03/2010
 */
package br.ufrn.sigaa.ensino.negocio.consolidacao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.ead.dominio.MetodologiaAvaliacao;
import br.ufrn.sigaa.ead.negocio.MetodologiaAvaliacaoHelper;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Regras para consolidação de turmas de EAD bacharelado.
 * 
 * @author David Pereira
 *
 */
public class ConsolidacaoEad extends AbstractEstrategiaConsolidacao implements EstrategiaConsolidacao {

	/** metodologia utilizada */
	private MetodologiaAvaliacao metodologia = null;

	/**
	 * Retorna a metodologia de avaliação utilizada, se não
	 * houver uma é buscada com base na matrícula no componente
	 * @param mc
	 * @return
	 */
	public MetodologiaAvaliacao getMetodologia(MatriculaComponente mc) {
		
		if (metodologia == null) {
			try {
				metodologia = MetodologiaAvaliacaoHelper.getMetodologia(mc.getDiscente().getCurso(), mc.getTurma().getAno(), mc.getTurma().getPeriodo());
			} catch (DAOException e) {
				// Silenciada
			}
		}
		
		return metodologia;
	}
	
	@Override
	public Double calculaMediaFinal(MatriculaComponente matricula) {
		return calculaMediaSemRecuperacao(matricula);
	}

	@Override
	public Double calculaMediaSemRecuperacao(MatriculaComponente matricula) {
		
		if (isEmpty(matricula.getNotas()))
			return null;

		if (getMetodologia(matricula).isUmaProva())
			return calcularMediaProva(matricula, 1);
		//TODO Adaptar calcularMediaProva para chamada recursiva passando somente calcularMediaProva(matricula, 2). Calcularia 2 e 1 unidade recursivamente. 
		else if (getMetodologia(matricula).isDuasProvas())
			return Math.round( ((calcularMediaProva(matricula, 1) * 10D + calcularMediaProva(matricula, 2) * 10D ) /2D) ) /10D;
		
		return null;
	}

	/**
	 * Calcula a média da prova de acordo com a matrícula e a unidade
	 * @param matricula
	 * @param unidade
	 * @return
	 */
	private Double calcularMediaProva(MatriculaComponente matricula, int unidade) {
		
		NotaUnidade nu = matricula.getNotaByUnidade(unidade); 
		Double nota = nu.getNota();

		if (nota != null) {
			
			if (isMenorNota(matricula, nu) && matricula.getRecuperacao() != null && matricula.getRecuperacao() > nota)
				nota = matricula.getRecuperacao();
			
			Double notaTutor = (matricula.getNotaTutorByUnidade(unidade) == null ? 0.0 : matricula.getNotaTutorByUnidade(unidade));
			nota = nota * getMetodologia(matricula).getPorcentagemProfessor() / 100.0;
			notaTutor = notaTutor * getMetodologia(matricula).getPorcentagemTutor() / 100.0;
			
			// Para manter uma casa decimal dividi-se por 10 depois do arredondamento
			return Math.round( (nota * 10D + notaTutor * 10D) ) / 10D;
		}
		return 0d;
	}

	/**
	 * Indica se a unidade atual representa a menor nota do aluno, para ser substituída pela nota da 
	 * recuperação, caso esta seja maior.
	 * 
	 * @param matricula
	 * @param nu
	 * @return
	 */
	private boolean isMenorNota(MatriculaComponente matricula, NotaUnidade nu) {
		Double menorNota = 10D;
		boolean estaEAMenorNota = false; // Indica se esta unidade representa a menor nota do discente
		
		for (NotaUnidade n : matricula.getNotas())
			if (n.getNota() != null && n.getNota() <= menorNota){
				menorNota = n.getNota();
				estaEAMenorNota = n.getId() == nu.getId();
			}
		return estaEAMenorNota;
	}

	@Override
	public void consolidar(MatriculaComponente matricula) {
		SituacaoMatricula situacao = SituacaoMatricula.APROVADO;		

		if (matricula.getMediaFinal() < mediaMinimaAprovacao) {
			situacao = SituacaoMatricula.REPROVADO;		
		}
		matricula.setSituacaoMatricula(situacao);
	}

	@Override
	public boolean isEmRecuperacao(MatriculaComponente matricula) {
		return matricula.getMediaParcial() != null && matricula.getMediaParcial() >= mediaMinimaPossibilitaRecuperacao && matricula.getMediaParcial() < mediaMinimaAprovacao;
	}

	@Override
	public boolean consolidarAlunosAprovadorPorMedia() {
		return false;
	}
	
	@Override
	public String getDescricaoSituacao(MatriculaComponente matricula) {
		if (matricula.isConsolidada())
			return getSiglaSituacao(matricula);

		if (matricula.isTrancado())
			return "TRAN";

		boolean reprovadoFaltas = false;
		boolean reprovadoMedia = false;
		
		if (matricula.isReprovadoFalta(freqMinima,minutosAulaRegular)) {
			reprovadoFaltas = true;
		}
		
		if (!reprovadoFaltas && matricula.getMediaParcial() == null) {
			return "--";
		} else if (!reprovadoFaltas && matricula.getMediaParcial() >= mediaMinimaAprovacao) {
			return "APR";
		} else if (!reprovadoFaltas && matricula.isEmRecuperacao() && matricula.getRecuperacao() == null) {
			return "REC";
		} else if (matricula.getMediaParcial() != null && matricula.getMediaParcial() < mediaMinimaPossibilitaRecuperacao) {
			reprovadoMedia = true;
		} else if (!reprovadoFaltas && ((matricula.isEmRecuperacao() && matricula.getRecuperacao() != null) || !matricula.isEmRecuperacao()) && (matricula.getMediaFinal() != null && matricula.getMediaFinal() >= mediaMinimaAprovacao)) {
			return "APR";
		} else if (((matricula.isEmRecuperacao() && matricula.getRecuperacao() != null) || !matricula.isEmRecuperacao()) && (matricula.getMediaFinal() != null && matricula.getMediaFinal() < mediaMinimaAprovacao)) {
			reprovadoMedia = true;
		}
		
		if (reprovadoMedia && !reprovadoFaltas)
			return "REP";
		else if (reprovadoFaltas && !reprovadoMedia)
			return "REPF";
		else if (reprovadoMedia && reprovadoFaltas)
			return "REMF";
		else
			return "--";
	}

	@Override
	public void validaNotas(Turma turma) throws NegocioException {
		// não há nenhuma implementação para a UFRN
	}
	
}
