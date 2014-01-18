/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 05/01/2010
 *
 */
package br.ufrn.sigaa.avaliacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.avaliacao.AvaliacaoInstitucionalDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.avaliacao.dominio.FormularioAvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.ListaAvaliacaoInstitucionalProcessar;
import br.ufrn.sigaa.avaliacao.dominio.MediaNotas;
import br.ufrn.sigaa.avaliacao.dominio.ParametroProcessamentoAvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.PercentualSimNao;
import br.ufrn.sigaa.avaliacao.dominio.ResultadoAvaliacaoDocente;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.TurmaDocenciaAssistida;

/**
 * Processa as notas da avaliação institucional para os docentes de uma turma.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public class ProcessadorResultadoAvaliacaoInstitucional extends
		AbstractProcessador {

	/** Executa o processamento das notas da Avaliação Institucional
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		MovimentoCalculoAvaliacaoInstitucional movCal = (MovimentoCalculoAvaliacaoInstitucional) mov;
		if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_RESULTADO_AVALIACAO_INSTITUCIONAL)) {
			// remove resultados anteriores
			removeResultadosAnteriores(movCal);
			return null;
		} if (mov.getCodMovimento().equals(SigaaListaComando.DETERMINA_AVALIACOES_INVALIDAS)) {
			determinaAvaliacoesInvalidas(movCal);
			return null;
		} else {
			// se for para salvar os resultados
			if (movCal.isSalvarResultado()) {
				persisteResultado(movCal);
				return null;
			} else {
				return calculaResultado(movCal);
			}
		}
	}
	
	/** Remove os resultados calculados de um ano-período.
	 * @param movCal
	 * @throws DAOException
	 */
	private void removeResultadosAnteriores(MovimentoCalculoAvaliacaoInstitucional movCal) throws DAOException {
		AvaliacaoInstitucionalDao avaliacaoDao = getDAO(AvaliacaoInstitucionalDao.class, movCal);
		try {
			int ano = movCal.getParametroProcessamento().getAno();
			int periodo = movCal.getParametroProcessamento().getPeriodo();
			int idFormulario = movCal.getParametroProcessamento().getFormulario().getId();
			avaliacaoDao.removeResultadosAvaliacao(ano, periodo, idFormulario);
		} finally {
			avaliacaoDao.close();
		}
	}

	/** Calcula as médias da Avaliação Institucional de um DocenteTurma
	 * @param movCal
	 * @return
	 * @throws DAOException
	 */
	private ResultadoAvaliacaoDocente calculaResultado(MovimentoCalculoAvaliacaoInstitucional movCal) throws DAOException {
		AvaliacaoInstitucionalDao avaliacaoDao = getDAO(AvaliacaoInstitucionalDao.class, movCal);
		MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class, movCal);
		
		ResultadoAvaliacaoDocente resultado = new ResultadoAvaliacaoDocente();
		try {
			DocenteTurma docenteTurma = movCal.getDocenteTurma();
			TurmaDocenciaAssistida turmaDocenciaAssistida = movCal.getTurmaDocenciaAssistida();
			
			int idDocenteTurma = docenteTurma == null ? 0 : docenteTurma.getId();
			int idTurmaDocenciaAssistida = turmaDocenciaAssistida == null ? 0 : turmaDocenciaAssistida.getId();
			
			ParametroProcessamentoAvaliacaoInstitucional parametroProcessamento = movCal.getParametroProcessamento();
			int idFormulario = parametroProcessamento.getFormulario().getId();
			
			// verifica se o docente possui avaliação a processar
			if (parametroProcessamento.getFormulario().isAvaliacaoDocenciaAssistida() && parametroProcessamento.isTurmaDocenciaAssistidaInvalida(idTurmaDocenciaAssistida) 
				||	!parametroProcessamento.getFormulario().isAvaliacaoDocenciaAssistida() && parametroProcessamento.isDocenteInvalido(idDocenteTurma)) 
				return null;
			
			// lista de avaliações do docente que não serão computadas
			Collection<Integer> idAvaliacoesInvalidas = movCal.getParametroProcessamento().getListaIdAvaliacaoInvalida(docenteTurma);
			 
			// calcula as médias referentes ao docente na turma
			Collection<MediaNotas> mediasNotas = avaliacaoDao.calculaMediaNotas(idDocenteTurma, idTurmaDocenciaAssistida, idAvaliacoesInvalidas, idFormulario);
			Collection<PercentualSimNao> percentualRespostasSimNao = avaliacaoDao.calculaPercentualSimNao(idDocenteTurma, idTurmaDocenciaAssistida, idAvaliacoesInvalidas, idFormulario);
			
			// Caso não possua médias, retorna.
			if (isEmpty(mediasNotas) && isEmpty(percentualRespostasSimNao)) 
				return null;
			
			// cria o resultado para o docenteTurma
			resultado.setAno(movCal.getParametroProcessamento().getAno());
			resultado.setPeriodo(movCal.getParametroProcessamento().getPeriodo());
			resultado.setDocenteTurma(docenteTurma);
			resultado.setTurmaDocenciaAssistida(turmaDocenciaAssistida);
			resultado.setMediaNotas(mediasNotas);
			resultado.setPercentualRespostasSimNao(percentualRespostasSimNao);
			resultado.setFormularioAvaliacaoInstitucional(new FormularioAvaliacaoInstitucional(idFormulario));
			
			// calcula desvio padrão geral
			resultado.setDesvioPadraoGeral(avaliacaoDao.findDesvioPadraoGeral(idDocenteTurma, idTurmaDocenciaAssistida, idAvaliacoesInvalidas, idFormulario));
			
			// calcula a média geral
			int count = 0;
			double media = 0;
			int idGrupo = parametroProcessamento.getFormulario().getGrupoMediaGeral().getId();
			for (MediaNotas mediaNotas : mediasNotas) {
				if (mediaNotas.getPergunta().getGrupo().getId() == idGrupo) {
					media += mediaNotas.getMedia();
					count++;
				}
			}
			if (count == 0) {
				// não há notas registradas
				resultado.setMediaGeral(null);
				resultado.setDesvioPadraoGeral(null);
			} else {
				resultado.setMediaGeral(media / count);
			}
			
			// calcula o número de discentes e trancamentos
			Turma turma = docenteTurma != null ? docenteTurma.getTurma() : turmaDocenciaAssistida != null ? turmaDocenciaAssistida.getTurma() : null;
			resultado.setNumTrancamentos(matriculaDao.countMatriculasByTurma(turma, SituacaoMatricula.TRANCADO));
			resultado.setNumDiscentes(matriculaDao.countMatriculasByTurma(turma, 
					SituacaoMatricula.getSituacoesMatriculadoOuConcluido()
					.toArray(new SituacaoMatricula[SituacaoMatricula.getSituacoesMatriculadoOuConcluido().size()])));
			if (!isEmpty(resultado.getMediaNotas()))
				for (MediaNotas m : resultado.getMediaNotas()) 
					m.setResultadoAvaliacaoDocente(resultado);
			if (!isEmpty(resultado.getPercentualRespostasSimNao()))
				for (PercentualSimNao sn : resultado.getPercentualRespostasSimNao()) 
					sn.setResultadoAvaliacaoDocente(resultado);
		} finally {
			// processamento concluído
			avaliacaoDao.close();
			matriculaDao.close();
		}
		return resultado;		
	}

	/** Persiste os resultados calculados.
	 * @param movCal
	 * @throws DAOException
	 */
	private void persisteResultado(MovimentoCalculoAvaliacaoInstitucional movCal) throws DAOException {
		AvaliacaoInstitucionalDao avaliacaoDao = getDAO(AvaliacaoInstitucionalDao.class, movCal);
		try {
			while (ListaAvaliacaoInstitucionalProcessar.possuiResultados()) {
				ResultadoAvaliacaoDocente resultado = ListaAvaliacaoInstitucionalProcessar.getProximoResultado();
				// persiste o novo resultado
				avaliacaoDao.create(resultado);
			}
			// registra o processamento
			ParametroProcessamentoAvaliacaoInstitucional param = movCal.getParametroProcessamento();
			param.setFimProcessamento(new Date());
			avaliacaoDao.create(param);
		} finally {
			avaliacaoDao.close();
		}
	}

	/** Determina quais Avaliações Institucionais não serão computadas.
	 * @param movCal
	 * @throws DAOException 
	 */
	private void determinaAvaliacoesInvalidas(MovimentoCalculoAvaliacaoInstitucional movCal) throws DAOException {
		AvaliacaoInstitucionalDao avaliacaoDao = getDAO(AvaliacaoInstitucionalDao.class, movCal);
		try {
			ParametroProcessamentoAvaliacaoInstitucional processamento = movCal.getParametroProcessamento();
			avaliacaoDao.determinaAvaliacoesNaoComputadas(processamento);
		} finally {
			avaliacaoDao.close();
		}
	}


	/** Valida os dados.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

}
