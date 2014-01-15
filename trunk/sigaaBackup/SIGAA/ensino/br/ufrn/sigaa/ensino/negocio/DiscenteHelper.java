/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Data de Cria��o: 23/10/2007
 */
package br.ufrn.sigaa.ensino.negocio;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import org.hibernate.HibernateException;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.AlteracaoStatusAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.ExtrapolarCreditoDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.diploma.dominio.ParametrosDiplomas;
import br.ufrn.sigaa.dominio.AlteracaoStatusAluno;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.TipoIntegralizacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.ExtrapolarCredito;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.graduacao.dominio.RestricoesMatricula;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * M�todos utilit�rios para opera��es sobre os discentes;
 *
 * @author Andre M Dantas
 *
 */
public class DiscenteHelper {

	/**
	 * Retorna o �ltimo status registrado para este discente em alteracao_Status_discente;
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public static Integer getUltimoStatus(Discente discente) throws DAOException {
		AlteracaoStatusAlunoDao dao = new AlteracaoStatusAlunoDao();
		try {
			AlteracaoStatusAluno alteracao = dao.findUltimaAlteracaoByDiscente(discente.getId());
			if (alteracao == null)
				return null;
			return alteracao.getStatus();
		} finally {
			dao.close();
		}
	}

	/**
	 * Realiza a altera��o de status com observa��o.
	 * @param d
	 * @param status
	 * @param observacao
	 * @param mov
	 * @param dao
	 * @throws ArqException
	 */
	public static void alterarStatusDiscente(DiscenteAdapter d, int status, String observacao, Movimento mov, GenericDAO dao) throws ArqException {
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(d);

		AlteracaoStatusAluno alteracao = new AlteracaoStatusAluno();
		alteracao.setAno(cal.getAno());
		alteracao.setPeriodo(cal.getPeriodo());
		alteracao.setData(new Date());
		alteracao.setMovimento(mov.getCodMovimento().getId());
		alteracao.setUsuario((Usuario) mov.getUsuarioLogado());
		alteracao.setStatus(d.getStatus());
		alteracao.setDiscente(d.getDiscente());
		alteracao.setObservacao(observacao);
		// registrando altera��o
		dao.create(alteracao);

		// persistindo altera��o do status do discente
		dao.updateField(Discente.class,  d.getId(), "status", status);
		
		// caso definido por par�metro, ser� enviado um e-mail para que o discente
		// verifique seus dados pessoais quando muda para status FORMANDO.
		if (status == StatusDiscente.FORMANDO &&
				ParametroHelper.getInstance().getParametroBoolean(ParametrosDiplomas.SOLICITAR_ATUALIZACAO_DADOS_FORMANDO)) {
			dao.findByPrimaryKey(d.getPessoa().getId(), Pessoa.class);
			String email = d.getPessoa().getEmail();
			String assunto = "[SIGAA] Solicita��o de verifica��o de dados pessoais.";
			String mensagem = ParametroHelper.getInstance().getParametro(ParametrosDiplomas.EMAIL_SOLICITACAO_ATUALIZACAO_DADOS_PESSOAIS).trim(); 
			MailBody mail = new MailBody();
			mail.setEmail(email);
			mail.setAssunto(assunto);
			mail.setMensagem(mensagem);				
			Mail.send(mail);
		}
	}

	/**
	 * Realiza a altera��o do status do discente sem precisar informar a observa��o.
	 * @param d
	 * @param status
	 * @param mov
	 * @param dao
	 * @throws ArqException
	 */
	public static void alterarStatusDiscente(DiscenteAdapter d, int status, Movimento mov, GenericDAO dao) throws ArqException {

		alterarStatusDiscente( d, status, null, mov, dao );

	}

	/**
	 * Soma uma quantidade de semestres baseado num ano e semestre passados.
	 *
	 * @param ano
	 * @param semestre
	 * @param qtdSemestres
	 * @return
	 */
	public static Integer somaSemestres(Integer ano, Integer semestre, Integer qtdSemestres) {
		try {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, ano);
			cal.set(Calendar.MONTH, (semestre == 1) ? Calendar.JANUARY : Calendar.JULY);
			cal.add(Calendar.MONTH, qtdSemestres * 6);
			int anoFinal = cal.get(Calendar.YEAR);
			int semFinal = (cal.get(Calendar.MONTH) >= Calendar.JULY) ? 2 : 1;

			return new Integer(anoFinal + "" + semFinal);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Subtrai uma quantidade de semestres baseado num ano e semestre passados.
	 *
	 * @param ano
	 * @param semestre
	 * @param qtdSemestres
	 * @return
	 */
	public static Integer subtrairSemestres(Integer ano, Integer semestre, Integer qtdSemestres) {
		try {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, ano);
			cal.set(Calendar.MONTH, (semestre == 1) ? Calendar.JANUARY : Calendar.JULY);
			cal.add(Calendar.MONTH, qtdSemestres * 6 * (-1) );
			int anoFinal = cal.get(Calendar.YEAR);
			int semFinal = (cal.get(Calendar.MONTH) >= Calendar.JULY) ? 2 : 1;

			return new Integer(anoFinal + "" + semFinal);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Soma qtdMeses somados ao ano-semestre passado por par�metro.
	 * @return o ano-semestre como resultado no formato AAAAS
	 */
	public static Integer somaMeses(Integer ano, Integer semestre, Integer qtdMeses) {
		try {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, ano);
			cal.set(Calendar.MONTH, (semestre == 1) ? Calendar.JANUARY : Calendar.JULY);
			cal.add(Calendar.MONTH, qtdMeses);
			int anoFinal = cal.get(Calendar.YEAR);
			int semFinal = (cal.get(Calendar.MONTH) >= Calendar.JULY) ? 2 : 1;

			return new Integer(anoFinal + "" + semFinal);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Verifica se o ano-per�odo passado faz parte dos per�odos v�lidos cursados pelo discente.
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @return
	 */
	public static String validarPeriodoDiscente(DiscenteAdapter discente, int ano, int periodo) {
		int anoPeriodoDiscente = new Integer(discente.getAnoIngresso() +  "" + discente.getPeriodoIngresso());
		int anoPeriodo = new Integer(ano + "" + periodo);
		if (anoPeriodo < anoPeriodoDiscente)
			return "Ano e per�odo inv�lidos: O discente ingressou em " + discente.getAnoPeriodoIngresso();

		return null;
	}

	/**
	 * Retorna o orientador ativo relacionado ao programa corrente do discente.
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public static OrientacaoAcademica getOrientadorAtivo( Discente discente) throws DAOException {
		OrientacaoAcademicaDao dao = getDAO(OrientacaoAcademicaDao.class);
		try {
			OrientacaoAcademica orientacao = dao.findOrientadorAtivoByDiscente(discente.getId());
			if (orientacao != null) {
				orientacao.getDescricaoOrientador();
				return orientacao;
			}
			return null;
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Retorna o �ltimo orientador (n�o necessariamente ativo)
	 * do programa mais recente do discente informado.
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public static OrientacaoAcademica getUltimoOrientador( Discente discente) throws DAOException {
		OrientacaoAcademicaDao dao = getDAO(OrientacaoAcademicaDao.class);
		try {
			OrientacaoAcademica orientacao = dao.findUltimoOrientadorByDiscente(discente.getId());
			if (orientacao != null) {
				orientacao.getDescricaoOrientador();
				return orientacao;
			}
			return null;
		} finally {
			dao.close();
		}
	}

	/**
	 * M�todo respons�vel por retornar o ano e per�odo de f�rias anterior ao ano e per�odo atual.
	 * @param ano
	 * @param periodo
	 * @return
	 */
	public static int getAnoPeriodoFeriasAnterior(int ano, int periodo) {
		int anoFerias, periodoFerias;
		if (periodo == 1) {
			anoFerias = ano - 1;
			periodoFerias = 4;
		} else {
			anoFerias = ano;
			periodoFerias = 3;
		}

		return new Integer(anoFerias+""+periodoFerias);
	}

	/** Retorna uma inst�ncia de uma classe de DAO expec�fica.
	 * @param <T>
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, null, null);
	}

	/**
	 *  Retorna o co-orientador atual de um discente.
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public static OrientacaoAcademica getCoOrientadorAtivo(DiscenteStricto discente) throws DAOException {
		OrientacaoAcademicaDao dao = getDAO(OrientacaoAcademicaDao.class);
		try {
			OrientacaoAcademica orientacao = dao.findCoOrientadorAtivoByDiscente(discente.getId());
			if (orientacao != null) {
				orientacao.getDescricaoOrientador();
				return orientacao;
			}
			return null;
		} finally {
			dao.close();
		}
	}

	/**
	 * Retorna o �ltimo co-orientador de um discente.
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public static OrientacaoAcademica getUltimoCoOrientador(DiscenteStricto discente) throws DAOException {
		OrientacaoAcademicaDao dao = getDAO(OrientacaoAcademicaDao.class);
		try {
			OrientacaoAcademica orientacao = dao.findUltimoCoOrientadorByDiscente(discente.getId());
			if (orientacao != null) {
				orientacao.getDescricaoOrientador();
				return orientacao;
			}
			return null;
		} finally {
			dao.close();
		}
	}	
	
	/**
	 * Calcula o total de cr�ditos extra-curriculares do aluno
	 * @param matriculas
	 * @param mapa 
	 * @return
	 * @throws DAOException
	 */
	public static int getTotalCreditosExtra(Collection<MatriculaComponente> matriculas, HashMap<Integer, Integer> mapa ) throws DAOException {

		int total = 0;
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);

		try {
			for ( MatriculaComponente m : matriculas ) {
				if ( m.getTipoIntegralizacao() == null || m.getTipoIntegralizacao().equals(TipoIntegralizacao.EXTRA_CURRICULAR) ) {
					
					Integer crTotal = dao.findCrTotalByIdComponente( m.getComponente().getId() );
					if(crTotal != null){
						total += crTotal;
						
						mapa.put(m.getId(), crTotal);
					}
				}
			}
			return total;
		}finally {
			dao.close();
		}

	}

	/**
	 * Retorna a quantidade m�nima de trabalhos de profici�ncia exigida de acordo com o n�vel do discente
	 * (mestrado ou doutorado).
	 * 
	 * @param discente - Discente Stricto Sensu
	 * 
	 * @return
	 */
	public static int getQuantidadeMinimaTrabalhosProficienciaExigida(DiscenteStricto discente) {
		
		int quantidadeProficienciasExigidas = 0;
		
		if (discente.isMestrado()) {
			quantidadeProficienciasExigidas = ParametroHelper.getInstance().getParametroInt(ParametrosStrictoSensu.QUANTIDADE_MINIMA_PROFICIENCIA_MESTRADO);
		} else if (discente.isDoutorado()) {
			quantidadeProficienciasExigidas = ParametroHelper.getInstance().getParametroInt(ParametrosStrictoSensu.QUANTIDADE_MINIMA_PROFICIENCIA_DOUTORADO);
		}
		
		return quantidadeProficienciasExigidas;
	}
	
	/**
	 * 
	 * verifica se o discente possui permiss�o de extrapolar cr�ditos e seta os valores permitidos na RestricoesMatricula. 
	 * 
	 * */
	public static void setarPermissaoExtrapolarCredito(DiscenteAdapter discente, RestricoesMatricula restricoes, CalendarioAcademico calendarioParaMatricula) throws HibernateException, DAOException {
		
		if(restricoes == null || ValidatorUtil.isEmpty(calendarioParaMatricula) || ValidatorUtil.isEmpty(discente)) {
			return;
		}
		
		ExtrapolarCreditoDao ecDao = getDAO(ExtrapolarCreditoDao.class);
		ExtrapolarCredito extrapolarCredito = ecDao.findPermissaoAtivo(discente.getDiscente(), calendarioParaMatricula.getAno(), calendarioParaMatricula.getPeriodo() );

		if (extrapolarCredito != null) {
			// caso a permiss�o seja no modelo antigo, sem valor m�ximo:
			if (extrapolarCredito.getCrMaximoExtrapolado() == null) {
				if (extrapolarCredito.isExtrapolarMaximo())
					restricoes.setLimiteMaxCreditosSemestre(false);
				else
					restricoes.setLimiteMinCreditosSemestre(false);
			} else {
				// modelo novo, com valor m�ximo e m�nimo
				restricoes.setValorMaximoCreditos(extrapolarCredito.getCrMaximoExtrapolado());
				restricoes.setValorMinimoCreditos(extrapolarCredito.getCrMinimoExtrapolado());
			}
		}
	}

	
	
}
