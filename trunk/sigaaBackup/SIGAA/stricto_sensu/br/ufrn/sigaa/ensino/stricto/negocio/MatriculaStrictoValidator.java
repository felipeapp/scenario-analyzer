/* 
 * Superintendência de Informática - Diretoria de Sistemas
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
 * Classe que contém as validações necessárias para a realização de matrículas em 
 * componentes curriculares pelos discentes de stricto
 * 
 * @author Ricardo Wendell
 *
 */
public class MatriculaStrictoValidator {

	/** Cria uma instância de um DAO
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, null, null);
	}

	/**
	 * Verifica se o discente possui orientação para realizar matrícula em defesa/qualificação
	 * 
	 * @param tipoAtividade
	 * @throws DAOException
	 */
	public static boolean validarOrientacaoAtiva(DiscenteAdapter discente, ListaMensagens erros) throws DAOException {
		OrientacaoAcademicaDao orientacaoDao = getDAO(OrientacaoAcademicaDao.class);
		try{
			OrientacaoAcademica orientacao = orientacaoDao.findOrientadorAtivoByDiscente(discente.getId());
			
			if (discente.isRegular() && orientacao == null) {
				erros.addErro("Para realizar a matrícula neste tipo de atividade é necessário ter um orientador definido. Procure a coordenação do seu curso para que a mesma possa realizar o lançamento no SIGAA.");
				return false;
			}
		} finally {
			orientacaoDao.close();
		}
		return true;	
	}	
	
	/**
	 * Verificar a possibilidade de solicitação de matrículas em atividades do tipo QUALIFICACAO
	 *
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public static boolean isPassivelSolicitacaoQualificacao(DiscenteAdapter discente, ListaMensagens mensagens) throws DAOException {
		return validarSolicitacoesQualificacaoDefesa(discente, mensagens);
	}

	/** Valida as solicitações de qualificação de defesa
	 * @param discente
	 * @param mensagens
	 * @throws DAOException
	 */
	private static boolean validarSolicitacoesQualificacaoDefesa(DiscenteAdapter discente, ListaMensagens mensagens) throws DAOException {
		// Validar se o discente é regular
		if (!discente.isRegular()) {
			mensagens.addWarning("Somente discentes regulares podem solicitar matrículas em atividades de qualificação ou defesa.");
			return false;
		}

		// Validar se o discente possui orientador
		return validarOrientacaoAtiva(discente, mensagens);
	}


	/**
	 * Verificar a possibilidade de solicitação de matrículas em atividades do tipo TESE
	 *
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public static boolean isPassivelSolicitacaoDefesa(DiscenteAdapter discente, ListaMensagens mensagens) throws DAOException {
		return validarSolicitacoesQualificacaoDefesa(discente, mensagens) && validarMatriculaDefesa(discente, mensagens);
	}

	/** Verificar a possibilidade de solicitação de matrículas em atividades do tipo QUALIFICACAO
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public static boolean isPassivelSolicitacaoQualificacao(DiscenteAdapter discente) throws DAOException {
		return isPassivelSolicitacaoQualificacao(discente, new ListaMensagens());
	}

	/** Verificar a possibilidade de solicitação de matrículas em atividades do tipo DEFESA
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public static boolean isPassivelSolicitacaoDefesa(DiscenteAdapter discente) throws DAOException {
		return isPassivelSolicitacaoDefesa(discente, new ListaMensagens());
	}

	/**
	 * Verificar se o discente pode realizar solicitações online de matrícula
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
	 * Verificar se a data atual está compreendida no período de matrícula online
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
	 * Verificar se é possível realizar uma nova solicitação de matrícula para
	 * um componente que já possui uma matrícula realizada
	 *
	 * @param matricula
	 * @return
	 */
	public static boolean isPassivelSolicitacaoNovaMatricula(MatriculaComponente matricula) {
		return (matricula.getComponente().isProficiencia() && !SituacaoMatricula.getSituacoesPagasEMatriculadas().contains(matricula.getSituacaoMatricula()))
				|| (!matricula.getComponente().isProficiencia() && !SituacaoMatricula.getSituacoesPagas().contains(matricula.getSituacaoMatricula()) );
	}

	/**
	 * Validar se é possível cancelar (remover) uma solicitação de matrícula para a turma
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
			// Buscar solicitações atendidas
			Collection<SolicitacaoMatricula> solicitacoes = solicitacaoDao.findByDiscenteTurmaAnoPeriodo(discente, turma, calendario.getAno(), calendario.getPeriodo(), null,
					SolicitacaoMatricula.ATENDIDA, SolicitacaoMatricula.NEGADA);
			return discente.isStricto() && turma.getDisciplina().isMatriculavel() && ValidatorUtil.isEmpty(solicitacoes);
		} finally {
			solicitacaoDao.close();
		}
	}

	/** Retorna os parâmetros do programa do discente
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	private static ParametrosProgramaPos getParametrosPrograma(DiscenteAdapter discente) throws DAOException {
		return ParametrosProgramaPosHelper.getParametros(discente);
	}

	/**
	 * Verifica se é possível o cadastro de uma solicitação de matrícula
	 * para a qualificação selecionada
	 *
	 * @param discente
	 * @param atividade
	 * @param mensagens
	 * @throws DAOException
	 */
	public static boolean isPassivelSolicitacaoQualificacao(DiscenteAdapter discente, ComponenteCurricular atividade, ListaMensagens mensagens) throws DAOException {
		ParametrosProgramaPos parametros = getParametrosPrograma(discente);
		
		long maxRenovacaoQualificacao;
		
		// Validar máximo de renovações
		if(discente.getDiscente().isDoutorado()){
			long totalMatriculasRenovacoes = getTotalRenovacoes(discente, atividade) + getTotalMatriculadas(discente, atividade);
			maxRenovacaoQualificacao = parametros.getMaxRenovacaoQualificacaoDoutorado() != null ? parametros.getMaxRenovacaoQualificacaoDoutorado() : 0;
			if (totalMatriculasRenovacoes > maxRenovacaoQualificacao ) {
				mensagens.addErro("Não é possível efetuar matrícula nesta atividade pois o limite de " +
						maxRenovacaoQualificacao +
						" renovações, estabelecido pelo Programa de Pós-Graduação, foi excedido.");
				return false;
			}
		} else if(discente.getDiscente().isMestrado()){
			long totalMatriculasRenovacoes = getTotalRenovacoes(discente, atividade) + getTotalMatriculadas(discente, atividade);
			maxRenovacaoQualificacao = parametros.getMaxRenovacaoQualificacaoMestrado() != null ? parametros.getMaxRenovacaoQualificacaoMestrado() : 0;
			if (totalMatriculasRenovacoes > maxRenovacaoQualificacao) {
				mensagens.addErro("Não é possível efetuar matrícula nesta atividade pois o limite de " +
						maxRenovacaoQualificacao +
						" renovações, estabelecido pelo Programa de Pós-Graduação, foi excedido.");
				return false;
			}
		}

				

		return true;
	}

	/**
	 * Verifica se é possível o cadastro de uma solicitação de matrícula
	 * para a defesa selecionada
	 *
	 * @param discente
	 * @param atividade
	 * @param mensagens
	 * @throws DAOException
	 */
	public static boolean isPassivelSolicitacaoDefesa(DiscenteAdapter discente, ComponenteCurricular atividade, CalendarioAcademico calendario, ListaMensagens mensagens) throws DAOException {
		// Validar renovações
		if ( !validarRenovacaoDefesa(discente, atividade, mensagens) ) {
			return false;
		}

		return true;
	}

	/**
	 * Verifica se o discente possui matrículas que podem ser renovadas e se o programa permitiu tais renovações
	 * 
	 * @param discente
	 * @param atividade
	 * @param mensagens
	 * @return
	 * @throws DAOException
	 */
	public static boolean validarRenovacaoDefesa(DiscenteAdapter discente,	ComponenteCurricular atividade, ListaMensagens mensagens) throws DAOException {
		ParametrosProgramaPos parametros = getParametrosPrograma(discente);
		// Validar máximo de renovações
		if(discente.getDiscente().isDoutorado()){
			long totalMatriculasRenovacoes = getTotalRenovacoes(discente, atividade) + getTotalMatriculadas(discente, atividade);
			if (totalMatriculasRenovacoes > parametros.getMaxRenovacaoDefesaDoutorado() ) {
				mensagens.addErro("Não é possível efetuar matrícula nesta atividade pois o limite de " +
						parametros.getMaxRenovacaoDefesaDoutorado() +
					" renovações, estabelecido pelo Programa de Pós-Graduação, foi excedido.");
				return false;
			}
		}
		
		if(discente.getDiscente().isMestrado()){
			long totalMatriculasRenovacoes = getTotalRenovacoes(discente, atividade) + getTotalMatriculadas(discente, atividade);
			if (totalMatriculasRenovacoes > parametros.getMaxRenovacaoDefesaMestrado()) {
				mensagens.addErro("Não é possível efetuar matrícula nesta atividade pois o limite de " +
						parametros.getMaxRenovacaoDefesaMestrado() +
					" renovações, estabelecido pelo Programa de Pós-Graduação, foi excedido.");
				return false;
			}
		}
		
		
		return true;
	}

	/**
	 * Verifica o número de vezes que um discente já se matriculou em um determinado componente curricular
	 * 
	 * @param discente
	 * @param atividade
	 * @throws DAOException
	 */
	private static long getTotalMatriculadas(DiscenteAdapter discente, ComponenteCurricular atividade) throws DAOException {
		// Contabilizar o número de matriculas realizadas para a atividade
		MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class);
		try {
			return matriculaDao.countMatriculadasByDiscenteComponente(discente, atividade);
		} finally {
			matriculaDao.close();
		}
	}

	/**
	 * Contar quantas renovações existem para um determinado discente e atividade
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
	 * Verifica se o discente especial tem permissão para efetuar sua matrícula
	 * 
	 * @param discente
	 * @param msgsErros
	 * @param size
	 * @throws DAOException
	 */
	public static boolean validarMatriculaDiscenteEspecial(Discente discente, int totalAtual, Collection<MensagemAviso> msgsErros) throws DAOException {
		ParametrosProgramaPos parametros = ParametrosProgramaPosHelper.getParametros(discente);

		if (totalAtual > parametros.getMaxDisciplinasAlunoEspecial()) {
			msgsErros.add(new MensagemAviso( "Você ultrapassou o limite de " +
				parametros.getMaxDisciplinasAlunoEspecial() +
				" matrículas em componentes definida pelo seu programa " +
				" para alunos especiais. ",TipoMensagemUFRN.ERROR));
			return false;
		}
		return true;

	}
	
	/**
	 * Validar a obrigatoriedade e necessidade de atualização dos dados pessoais dos discentes
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
		
		// Verificar se deve ser validada a qualificação para se matricular em defesa
		ParametrosProgramaPos parametros = ParametrosProgramaPosHelper.getParametros(discente);
		if (parametros.isPermiteMatricularDefesaQualificacao()) {
			return true;
		}
		
		// Buscar as atividades de qualificação cumpridas
		MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class);
		try {
			Collection<MatriculaComponente> qualificacoesPagas = matriculaDao.findAtividadesByDiscente(discente, new TipoAtividade(TipoAtividade.QUALIFICACAO), SituacaoMatricula.getSituacoesPagas() );
			// Verificar se o discente cumpriu alguma atividade de qualificação
			if (isEmpty(qualificacoesPagas)) {
				mensagens.addErro("Somente é permitido matricular-se em defesas após a conclusão da atividade de qualificação.");
				return false;
			}
		}finally{
			matriculaDao.close();
		}
		
		return true;		
	}

}
