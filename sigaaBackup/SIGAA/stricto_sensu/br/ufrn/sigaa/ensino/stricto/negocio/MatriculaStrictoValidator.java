/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.RenovacaoAtividadePosDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.stricto.dominio.ParametrosProgramaPos;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe que cont�m as valida��es necess�rias para a realiza��o de matr�culas em 
 * componentes curriculares pelos discentes de stricto
 * 
 * @author Ricardo Wendell
 *
 */
public class MatriculaStrictoValidator {

	/** Cria uma inst�ncia de um DAO
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, null, null);
	}

	/**
	 * Verifica se o discente possui orienta��o para realizar matr�cula em defesa/qualifica��o
	 * 
	 * @param tipoAtividade
	 * @throws DAOException
	 */
	public static boolean validarOrientacaoAtiva(DiscenteAdapter discente, ListaMensagens erros) throws DAOException {
		OrientacaoAcademicaDao orientacaoDao = getDAO(OrientacaoAcademicaDao.class);
		try{
			OrientacaoAcademica orientacao = orientacaoDao.findOrientadorAtivoByDiscente(discente.getId());
			
			if (discente.isRegular() && orientacao == null) {
				erros.addErro("Para realizar a matr�cula neste tipo de atividade � necess�rio ter um orientador definido. Procure a coordena��o do seu curso para que a mesma possa realizar o lan�amento no SIGAA.");
				return false;
			}
		} finally {
			orientacaoDao.close();
		}
		return true;	
	}	
	
	/**
	 * Verificar a possibilidade de solicita��o de matr�culas em atividades do tipo QUALIFICACAO
	 *
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public static boolean isPassivelSolicitacaoQualificacao(DiscenteAdapter discente, ListaMensagens mensagens) throws DAOException {
		return validarSolicitacoesQualificacaoDefesa(discente, mensagens);
	}

	/** Valida as solicita��es de qualifica��o de defesa
	 * @param discente
	 * @param mensagens
	 * @throws DAOException
	 */
	private static boolean validarSolicitacoesQualificacaoDefesa(DiscenteAdapter discente, ListaMensagens mensagens) throws DAOException {
		// Validar se o discente � regular
		if (!discente.isRegular()) {
			mensagens.addWarning("Somente discentes regulares podem solicitar matr�culas em atividades de qualifica��o ou defesa.");
			return false;
		}

		// Validar se o discente possui orientador
		return validarOrientacaoAtiva(discente, mensagens);
	}


	/**
	 * Verificar a possibilidade de solicita��o de matr�culas em atividades do tipo TESE
	 *
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public static boolean isPassivelSolicitacaoDefesa(DiscenteAdapter discente, ListaMensagens mensagens) throws DAOException {
		return validarSolicitacoesQualificacaoDefesa(discente, mensagens) && validarMatriculaDefesa(discente, mensagens);
	}

	/** Verificar a possibilidade de solicita��o de matr�culas em atividades do tipo QUALIFICACAO
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public static boolean isPassivelSolicitacaoQualificacao(DiscenteAdapter discente) throws DAOException {
		return isPassivelSolicitacaoQualificacao(discente, new ListaMensagens());
	}

	/** Verificar a possibilidade de solicita��o de matr�culas em atividades do tipo DEFESA
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public static boolean isPassivelSolicitacaoDefesa(DiscenteAdapter discente) throws DAOException {
		return isPassivelSolicitacaoDefesa(discente, new ListaMensagens());
	}

	/**
	 * Verificar se o discente pode realizar solicita��es online de matr�cula
	 *
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public static boolean isPassivelSolicitacaoMatricula(DiscenteAdapter discente) throws DAOException {
		ParametrosProgramaPos parametros = getParametrosPrograma(discente);

		boolean permissaoDiscenteEspecial = !discente.isRegular() && parametros.isPermiteMatriculaOnlineEspeciais();
		return discente.isAtivo() && (discente.isRegular() || permissaoDiscenteEspecial) ;
	}

	/**
	 * Verificar se a data atual est� compreendida no per�odo de matr�cula online
	 *
	 * @param calendarioMatricula
	 * @return
	 * @throws DAOException
	 */
	public static boolean isPeriodoSolicitacaoMatricula( CalendarioAcademico calendarioMatricula) throws DAOException {
		UnidadeDao unidadeDao = getDAO(UnidadeDao.class);
		Unidade unidade = unidadeDao.findByPrimaryKey(calendarioMatricula.getUnidade().getId(), Unidade.class);
		try {
			return unidade.isPrograma() && calendarioMatricula.isPeriodoMatriculaRegular();
		} finally {
			unidadeDao.close();
		}
	}

	/**
	 * Verificar se � poss�vel realizar uma nova solicita��o de matr�cula para
	 * um componente que j� possui uma matr�cula realizada
	 *
	 * @param matricula
	 * @return
	 */
	public static boolean isPassivelSolicitacaoNovaMatricula(MatriculaComponente matricula) {
		return (matricula.getComponente().isProficiencia() && !SituacaoMatricula.getSituacoesPagasEMatriculadas().contains(matricula.getSituacaoMatricula()))
				|| (!matricula.getComponente().isProficiencia() && !SituacaoMatricula.getSituacoesPagas().contains(matricula.getSituacaoMatricula()) );
	}

	/**
	 * Validar se � poss�vel cancelar (remover) uma solicita��o de matr�cula para a turma
	 * selecionada
	 *
	 * @param discente
	 * @param turma
	 * @param calendarioParaMatricula
	 * @return
	 * @throws DAOException
	 */
	public static boolean isPassivelCancelamentoSolicitacao(Discente discente, Turma turma, CalendarioAcademico calendario) throws DAOException {
		if (discente.isStricto()) {
			return true;
		}

		SolicitacaoMatriculaDao solicitacaoDao = getDAO(SolicitacaoMatriculaDao.class);
		try {
			// Buscar solicita��es atendidas
			Collection<SolicitacaoMatricula> solicitacoes = solicitacaoDao.findByDiscenteTurmaAnoPeriodo(discente, turma, calendario.getAno(), calendario.getPeriodo(), null,
					SolicitacaoMatricula.ATENDIDA, SolicitacaoMatricula.NEGADA);
			return discente.isStricto() && turma.getDisciplina().isMatriculavel() && ValidatorUtil.isEmpty(solicitacoes);
		} finally {
			solicitacaoDao.close();
		}
	}

	/** Retorna os par�metros do programa do discente
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	private static ParametrosProgramaPos getParametrosPrograma(DiscenteAdapter discente) throws DAOException {
		return ParametrosProgramaPosHelper.getParametros(discente);
	}

	/**
	 * Verifica se � poss�vel o cadastro de uma solicita��o de matr�cula
	 * para a qualifica��o selecionada
	 *
	 * @param discente
	 * @param atividade
	 * @param mensagens
	 * @throws DAOException
	 */
	public static boolean isPassivelSolicitacaoQualificacao(DiscenteAdapter discente, ComponenteCurricular atividade, ListaMensagens mensagens) throws DAOException {
		ParametrosProgramaPos parametros = getParametrosPrograma(discente);
		
		long maxRenovacaoQualificacao;
		
		// Validar m�ximo de renova��es
		if(discente.getDiscente().isDoutorado()){
			long totalMatriculasRenovacoes = getTotalRenovacoes(discente, atividade) + getTotalMatriculadas(discente, atividade);
			maxRenovacaoQualificacao = parametros.getMaxRenovacaoQualificacaoDoutorado() != null ? parametros.getMaxRenovacaoQualificacaoDoutorado() : 0;
			if (totalMatriculasRenovacoes > maxRenovacaoQualificacao ) {
				mensagens.addErro("N�o � poss�vel efetuar matr�cula nesta atividade pois o limite de " +
						maxRenovacaoQualificacao +
						" renova��es, estabelecido pelo Programa de P�s-Gradua��o, foi excedido.");
				return false;
			}
		} else if(discente.getDiscente().isMestrado()){
			long totalMatriculasRenovacoes = getTotalRenovacoes(discente, atividade) + getTotalMatriculadas(discente, atividade);
			maxRenovacaoQualificacao = parametros.getMaxRenovacaoQualificacaoMestrado() != null ? parametros.getMaxRenovacaoQualificacaoMestrado() : 0;
			if (totalMatriculasRenovacoes > maxRenovacaoQualificacao) {
				mensagens.addErro("N�o � poss�vel efetuar matr�cula nesta atividade pois o limite de " +
						maxRenovacaoQualificacao +
						" renova��es, estabelecido pelo Programa de P�s-Gradua��o, foi excedido.");
				return false;
			}
		}

				

		return true;
	}

	/**
	 * Verifica se � poss�vel o cadastro de uma solicita��o de matr�cula
	 * para a defesa selecionada
	 *
	 * @param discente
	 * @param atividade
	 * @param mensagens
	 * @throws DAOException
	 */
	public static boolean isPassivelSolicitacaoDefesa(DiscenteAdapter discente, ComponenteCurricular atividade, CalendarioAcademico calendario, ListaMensagens mensagens) throws DAOException {
		// Validar renova��es
		if ( !validarRenovacaoDefesa(discente, atividade, mensagens) ) {
			return false;
		}

		return true;
	}

	/**
	 * Verifica se o discente possui matr�culas que podem ser renovadas e se o programa permitiu tais renova��es
	 * 
	 * @param discente
	 * @param atividade
	 * @param mensagens
	 * @return
	 * @throws DAOException
	 */
	public static boolean validarRenovacaoDefesa(DiscenteAdapter discente,	ComponenteCurricular atividade, ListaMensagens mensagens) throws DAOException {
		ParametrosProgramaPos parametros = getParametrosPrograma(discente);
		// Validar m�ximo de renova��es
		if(discente.getDiscente().isDoutorado()){
			long totalMatriculasRenovacoes = getTotalRenovacoes(discente, atividade) + getTotalMatriculadas(discente, atividade);
			if (totalMatriculasRenovacoes > parametros.getMaxRenovacaoDefesaDoutorado() ) {
				mensagens.addErro("N�o � poss�vel efetuar matr�cula nesta atividade pois o limite de " +
						parametros.getMaxRenovacaoDefesaDoutorado() +
					" renova��es, estabelecido pelo Programa de P�s-Gradua��o, foi excedido.");
				return false;
			}
		}
		
		if(discente.getDiscente().isMestrado()){
			long totalMatriculasRenovacoes = getTotalRenovacoes(discente, atividade) + getTotalMatriculadas(discente, atividade);
			if (totalMatriculasRenovacoes > parametros.getMaxRenovacaoDefesaMestrado()) {
				mensagens.addErro("N�o � poss�vel efetuar matr�cula nesta atividade pois o limite de " +
						parametros.getMaxRenovacaoDefesaMestrado() +
					" renova��es, estabelecido pelo Programa de P�s-Gradua��o, foi excedido.");
				return false;
			}
		}
		
		
		return true;
	}

	/**
	 * Verifica o n�mero de vezes que um discente j� se matriculou em um determinado componente curricular
	 * 
	 * @param discente
	 * @param atividade
	 * @throws DAOException
	 */
	private static long getTotalMatriculadas(DiscenteAdapter discente, ComponenteCurricular atividade) throws DAOException {
		// Contabilizar o n�mero de matriculas realizadas para a atividade
		MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class);
		try {
			return matriculaDao.countMatriculadasByDiscenteComponente(discente, atividade);
		} finally {
			matriculaDao.close();
		}
	}

	/**
	 * Contar quantas renova��es existem para um determinado discente e atividade
	 *
	 * @param discente
	 * @param atividade
	 * @throws DAOException
	 */
	private static long getTotalRenovacoes(DiscenteAdapter discente, ComponenteCurricular atividade) throws DAOException {
		RenovacaoAtividadePosDao renovacaoDao = getDAO(RenovacaoAtividadePosDao.class);
		try {
			return renovacaoDao.countTotalRenovacoes(discente, atividade);
		} finally {
			renovacaoDao.close();
		}
	}

	/**
	 * Verifica se o discente especial tem permiss�o para efetuar sua matr�cula
	 * 
	 * @param discente
	 * @param msgsErros
	 * @param size
	 * @throws DAOException
	 */
	public static boolean validarMatriculaDiscenteEspecial(Discente discente, int totalAtual, Collection<MensagemAviso> msgsErros) throws DAOException {
		ParametrosProgramaPos parametros = ParametrosProgramaPosHelper.getParametros(discente);

		if (totalAtual > parametros.getMaxDisciplinasAlunoEspecial()) {
			msgsErros.add(new MensagemAviso( "Voc� ultrapassou o limite de " +
				parametros.getMaxDisciplinasAlunoEspecial() +
				" matr�culas em componentes definida pelo seu programa " +
				" para alunos especiais. ",TipoMensagemUFRN.ERROR));
			return false;
		}
		return true;

	}
	
	/**
	 * Validar a obrigatoriedade e necessidade de atualiza��o dos dados pessoais dos discentes
	 * @throws DAOException 
	 */
	public static boolean isNecessariaAtualizacaoDadosDiscente(DiscenteAdapter discente, ParametrosGestoraAcademica parametros, CalendarioAcademico calendarioMatricula) throws DAOException {
		Boolean atualizacaoSolicitada = parametros.getSolicitarAtualizacaoDadosMatricula();
		Date ultimaAtualizacao = discente.getPessoa().getUltimaAtualizacao();
		
		return atualizacaoSolicitada != null && atualizacaoSolicitada 
				&& (ultimaAtualizacao == null || ultimaAtualizacao.before(calendarioMatricula.getInicioMatriculaOnline()));
	}

	/**
	 * Verificar se o discente pode matricular-se em uma atividade de defesa
	 * 
	 * @param discente
	 * @param mensagens
	 * @return
	 * @throws DAOException
	 */
	public static boolean validarMatriculaDefesa(DiscenteAdapter discente, ListaMensagens mensagens) throws DAOException {
		
		// Verificar se deve ser validada a qualifica��o para se matricular em defesa
		ParametrosProgramaPos parametros = ParametrosProgramaPosHelper.getParametros(discente);
		if (parametros.isPermiteMatricularDefesaQualificacao()) {
			return true;
		}
		
		// Buscar as atividades de qualifica��o cumpridas
		MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class);
		try {
			Collection<MatriculaComponente> qualificacoesPagas = matriculaDao.findAtividadesByDiscente(discente, new TipoAtividade(TipoAtividade.QUALIFICACAO), SituacaoMatricula.getSituacoesPagas() );
			// Verificar se o discente cumpriu alguma atividade de qualifica��o
			if (isEmpty(qualificacoesPagas)) {
				mensagens.addErro("Somente � permitido matricular-se em defesas ap�s a conclus�o da atividade de qualifica��o.");
				return false;
			}
		}finally{
			matriculaDao.close();
		}
		
		return true;		
	}

}
