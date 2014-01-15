/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.List;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ConfiguracaoAmbienteException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.ensino.CalendarioAcademicoDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.PeriodoAcademico;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ConvenioAcademico;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.negocio.PeriodoAcademicoHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;

/**
 * Clase que possui os m�todos de valida��o padr�o referentes
 * � classe de dom�nio CalendarioAcademico
 * 
 * @author Andre M Dantas
 */
public class CalendarioAcademicoHelper {

	/**
	 * Unidade global
	 */
	private static final Unidade UNIDADE_DIREITO_GLOBAL = new Unidade(UnidadeGeral.UNIDADE_DIREITO_GLOBAL);

	/**
	 * 
	 * Retorna um calend�rio acad�mico de acordo com os par�metros da gestora acad�mica.
	 * 
	 * @param param
	 * @return
	 * @throws DAOException
	 */
	public static CalendarioAcademico getCalendario(ParametrosGestoraAcademica param) throws DAOException {
		return getCalendario(null, null, param.getUnidade(),
				param.getNivel(), param.getModalidade(), param.getConvenio(), param.getCurso());
	}

	
	/**
	 * 
	 * Retorna um calend�rio acad�mico de acordo com o discente.
	 * 
	 * @param d
	 * @return
	 * @throws DAOException
	 */
	public static CalendarioAcademico getCalendario(DiscenteAdapter d) throws DAOException {
		return getCalendario(null, null, d.getGestoraAcademica(), d.getNivel(), null, null, d.getCurso());
	}

	
	/**
	 * 
	 * Retorna um calend�rio acad�mico de acordo com um curso.
	 * 
	 * @param c
	 * @return
	 * @throws DAOException
	 */
	public static CalendarioAcademico getCalendario(Curso c) throws DAOException {
		return getCalendario(null, null, c.getUnidade().getGestoraAcademica(), null, null, null, c);
	}

	/**
	 * 
	 * Retorna um calend�rio acad�mico de acordo com uma unidade acad�mica.
	 * 
	 * @param u
	 * @return
	 * @throws DAOException
	 */
	public static CalendarioAcademico getCalendario(Unidade u) throws DAOException {
		return getCalendario(null, null, u, null, null, null, null);
	}

	
	/**
	 * 
	 * Retorna um calend�rio acad�mico de acordo com um componente curricular.
	 * 
	 * @param c
	 * @return
	 * @throws DAOException
	 */
	public static CalendarioAcademico getCalendario(ComponenteCurricular c) throws DAOException {
		return getCalendario(null, null, c.getUnidade().getGestoraAcademica(),
				c.getNivel(), null, null, null);
	}

	
	/**
	 * 
	 * Retorna um calend�rio acad�mico de acordo com um usu�rio. 
	 * Leva em considera��o a unidade gestora academica da unidade do usuario e tamb�m o n�vel de ensino do usu�rio.
	 * 
	 * @param u
	 * @return
	 * @throws DAOException
	 */
	public static CalendarioAcademico getCalendario(Usuario u) throws DAOException {
		Unidade unidade = u.getVinculoAtivo().getUnidade();
		if (unidade.getGestoraAcademica() != null)
			unidade = unidade.getGestoraAcademica();

		return getCalendario(null, null, unidade, u.getNivelEnsino(), null, null, null);
	}

	
	/**
	 * 
	 * Retorna um calend�rio acad�mico de acordo com uma turma.
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public static CalendarioAcademico getCalendario(Turma turma) throws DAOException {
		return getCalendario(turma, null);
	}

	
	/**
	 * 
	 * Retorna um calend�rio acad�mico de acordo com uma turma e unidade gestora acad�mica.
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public static CalendarioAcademico getCalendario(Turma turma, Unidade gestoraAcademica) throws DAOException {
		char nivel = turma.getDisciplina().getNivel();
		ModalidadeEducacao mod = null;
		
		if (turma.getPolo() != null)
			mod = new ModalidadeEducacao( ModalidadeEducacao.A_DISTANCIA);
		Unidade un = gestoraAcademica;
		
		if (un == null) {
			if (isNotEmpty(nivel) && (nivel == NivelEnsino.FORMACAO_COMPLEMENTAR || nivel == NivelEnsino.TECNICO))
				un = turma.getDisciplina().getUnidade();
			else
				un = turma.getDisciplina().getUnidade().getGestoraAcademica();
		}
		ConvenioAcademico conv = null;
		if (turma.getCurso() != null)
			conv = turma.getCurso().getConvenio();

		int periodo = turma.getPeriodo();
		
		if (PeriodoAcademicoHelper.getInstance().isPeriodoIntervalar(periodo)) {
			List<PeriodoAcademico> intervalos = PeriodoAcademicoHelper.getInstance().getPeriodosIntervalares();
			List<PeriodoAcademico> regulares = PeriodoAcademicoHelper.getInstance().getPeriodosRegulares();
			
			PeriodoAcademico primeiroPeriodoIntervalar = intervalos.get(0);
			PeriodoAcademico primeiroPeriodoRegular = regulares.get(0);
			PeriodoAcademico ultimoPeriodoRegular = regulares.get(regulares.size() - 1);
		
			if (periodo == primeiroPeriodoIntervalar.getPeriodo())
				periodo = primeiroPeriodoRegular.getPeriodo();
			else
				periodo = ultimoPeriodoRegular.getPeriodo();
		}

		return getCalendario(turma.getAno(), periodo, un, nivel, mod, conv, null);
	}

	
	/**
	 * Retorna o calend�rio global de gradua��o.
	 * @return
	 * @throws DAOException
	 */
	public static CalendarioAcademico getCalendarioUnidadeGlobalGrad() throws DAOException {
		return getCalendario(ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalGraduacao());
	}
	
	/**
	 * Retorna o calend�rio global de Stricto
	 * @return
	 * @throws DAOException
	 */
	public static CalendarioAcademico getCalendarioUnidadeGlobalStricto() throws DAOException {
		return getCalendario(ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalStricto());
	}
	
	/**
	 * Retorna o calend�rio global de ensino m�dio
	 * @return
	 * @throws DAOException
	 */
	public static CalendarioAcademico getCalendarioUnidadeGlobalMedio() throws DAOException {
		return getCalendario(ParametrosGestoraAcademicaHelper.getParametrosMedio());
	}	

	
	/**
	 * Retorna o calend�rio global de gradua��o para a modalidade EAD.
	 * @return
	 * @throws DAOException
	 */
	public static CalendarioAcademico getCalendarioEAD() throws DAOException {
		return getCalendario(null, null, UNIDADE_DIREITO_GLOBAL, NivelEnsino.GRADUACAO,
				new ModalidadeEducacao(ModalidadeEducacao.A_DISTANCIA), null, null);
	}

	
	/**
	 * 
	 * Retorna um calend�rio de acordo com os par�metros especificados. Caso n�o encontre nenhum, ser� lancada uma Exce��o: ConfiguracaoAmbienteException
	 * 
	 * @param ano
	 * @param periodo
	 * @param unidade
	 * @param nivel
	 * @param modalidade
	 * @param convenio
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public static CalendarioAcademico getCalendario(Integer ano, Integer periodo,
			Unidade unidade, Character nivel, ModalidadeEducacao modalidade,
			ConvenioAcademico convenio, Curso curso) throws DAOException {

		if (nivel == null && !isEmpty(curso))
			nivel = curso.getNivel();
		if( NivelEnsino.isAlgumNivelStricto(nivel) || (nivel == null && unidade.getTipoAcademica() == TipoUnidadeAcademica.PROGRAMA_POS) ) {
			nivel = NivelEnsino.STRICTO;
			if (ParametroHelper.getInstance().getParametroBoolean(ParametrosStrictoSensu.CALENDARIO_POR_CURSO) && curso != null)
				nivel = curso.getNivel();
		}
		if (modalidade == null && !isEmpty(curso) && curso.isADistancia())
			modalidade = curso.getModalidadeEducacao();
		if (convenio == null && !isEmpty(curso))
			convenio = curso.getConvenio();
		if (NivelEnsino.isAlgumNivelStricto(nivel) && !ParametroHelper.getInstance().getParametroBoolean(ParametrosStrictoSensu.CALENDARIO_POR_CURSO))
			curso = null;
		CalendarioAcademicoDao cdao = getDAO(CalendarioAcademicoDao.class);
		try {
			CalendarioAcademico cal = null;
			if (!isEmpty(curso))
				cal = cdao.findByCurso(ano, periodo, curso, nivel);
			if (cal == null) {
				if (nivel != null && nivel.charValue() == NivelEnsino.GRADUACAO) {
					if ( modalidade != null && modalidade.isADistancia()) {
						// carrega um geral do EAD
						cal = cdao.findByModalidade(ano, periodo, UNIDADE_DIREITO_GLOBAL, nivel, modalidade);
					} else if ( convenio != null ) {
						cal = cdao.findByConvenio(ano, periodo, UNIDADE_DIREITO_GLOBAL, nivel, convenio);
					}
					if (cal == null)
						cal = cdao.findByAnoPeriodo(ano, periodo, UNIDADE_DIREITO_GLOBAL.getId(), NivelEnsino.GRADUACAO);
				} else {
					// tenta da gestora, se n�o der carrega um geral do n�vel de ensino.
					if (unidade != null)
						cal = cdao.findByAnoPeriodo(ano, periodo, unidade.getId(), nivel);
					if (cal == null) {
						if (NivelEnsino.isAlgumNivelStricto(nivel))
							nivel = NivelEnsino.STRICTO;						
						if(unidade != null)
							cal = cdao.findByAnoPeriodo(ano, periodo, unidade.getId(), nivel);						
						if(cal == null){
							cal = cdao.findByAnoPeriodo(ano, periodo, UNIDADE_DIREITO_GLOBAL.getId(), nivel);
						}
					}
				}

				// se ainda assim estiver nulo, carrega os par�metros da gradua��o
				// (acontece enquanto existirem alunos sem gestora acad�mica)
				if (cal == null)
					cal = cdao.findByAnoPeriodo(ano, periodo, UNIDADE_DIREITO_GLOBAL.getId(), NivelEnsino.GRADUACAO);
				
				// se ainda assim estiver nulo, busca um calend�rio de qualquer n�vel de ensino que esteja ativo. Isso acontece quando n�o houver calend�rio de gradua��o ativo.
				if (cal == null)
					cal = cdao.findByParametros(ano, periodo, new Unidade(UNIDADE_DIREITO_GLOBAL.getId()), null, null, null, null, null);
				
			}

			if(cal != null) {
				return cal;
			}
			else {
				throw new ConfiguracaoAmbienteException("N�o h� nenhum Calend�rio Acad�mico ativo");
			}
			
		} finally {
			cdao.close();
		}
	}

	/**
	 * Este m�todo busca o calend�rio de acordo com os par�metros passado e,
	 * caso n�o encontra, NAO IRA REALIZAR A BUSCA POR OUTROS CALENDARIOS
	 * MAIS GENERICOS REMOVENDO OS PARAMETROS!
	 * @param ano
	 * @param periodo
	 * @param gestoraAcademica
	 * @param nivel
	 * @param modalidade
	 * @param convenio
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public static CalendarioAcademico getCalendarioExato(Integer ano, Integer periodo,
			Unidade gestoraAcademica, Character nivel, ModalidadeEducacao modalidade,
			ConvenioAcademico convenio, Curso curso, Integer periodoFerias) throws DAOException {

		CalendarioAcademicoDao cdao = getDAO(CalendarioAcademicoDao.class);
		try {
			return cdao.findByParametros( ano, periodo, gestoraAcademica, nivel, modalidade, convenio, curso, periodoFerias );
		} finally {
			cdao.close();
		}
	}

	/**
	 * Retorna o DAO da classe informada
	 * @param <T>
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, null, null);
	}

	
	/**
	 * 
	 * Retorna o calend�rio acad�mico seguinte ao vigente.
	 * 
	 * @param unidadeId
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public static CalendarioAcademico getProximoCalendario(int unidadeId, char nivel, ConvenioAcademico conv) throws DAOException {

		CalendarioAcademicoDao dao = getDAO(CalendarioAcademicoDao.class);
		try {
			CalendarioAcademico vigente = dao.findByParametros(null, null, new Unidade(unidadeId), nivel, null, conv, null, null);
			CalendarioAcademico prox = dao.findProximo(vigente.getAno(),vigente.getPeriodo(), unidadeId, nivel, conv);
			return prox;
		} finally {
			dao.close();
		}
	}

	/*	public static CalendarioAcademico getCalendarioAtual(int unidadeId, char nivel) throws DAOException {
	ParametrosGestoraAcademicaDao paramDao = getDAO(ParametrosGestoraAcademicaDao.class);
	CalendarioAcademicoDao dao = getDAO(CalendarioAcademicoDao.class);
	try {
		ParametrosGestoraAcademica p = paramDao.findByUnidade(unidadeId, nivel);
		return dao.findByAnoPeriodo(p.getAnoAtual(), p.getPeriodoAtual(), unidadeId, nivel);
	} catch (Exception e) {
		e.printStackTrace();
		return null;
	} finally {
		paramDao.close();
		dao.close();
	}

}
*/

	/**
	 * retorna o calend�rio acad�mico atual do usu�rio, de acordo com sua
	 * gestora acad�mica e seu n�vel de ensino.
	 * @param usuario
	 * @return
	 * @throws DAOException
	 */
/*	public static CalendarioAcademico getCalendarioAtual(Usuario usuario) throws DAOException {
		Unidade unidade = usuario.getUnidade();
		if (unidade.getGestoraAcademica() != null)
			unidade = unidade.getGestoraAcademica();
		return getCalendarioAtual(unidade.getId(), usuario.getNivelEnsino());
	}
*/

}
