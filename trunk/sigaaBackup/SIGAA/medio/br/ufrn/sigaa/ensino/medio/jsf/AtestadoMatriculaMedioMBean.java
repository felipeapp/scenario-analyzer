/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 02/09/2011
 *
 */
package br.ufrn.sigaa.ensino.medio.jsf;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.seguranca.autenticacao.AutValidator;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.HorarioDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.Horario;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.medio.dao.MatriculaComponenteMedioDao;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Managed Bean para geração do atestado de matrícula de alunos de Graduação
 * 
 * @author Rafael Gomes
 * 
 */
@Component("atestadoMatriculaMedio") @Scope("request")
public class AtestadoMatriculaMedioMBean extends SigaaAbstractController<Discente> implements OperadorDiscente, AutValidator {

	/** Discente do atestado. */
	private DiscenteAdapter discente;

	/** Listagem de matrículas do discente. */
	private List<MatriculaComponente> matriculas;

	/** Horários da unidade gestora. */
	private List<Horario> horarios;

	/** Lista de horários de turma de acordo com as matrículas do discente. */
	private List<HorarioTurma> horariosTurma;

	/** Armazena o ano e semestre no formato? {ano}.{periodo} . */
	private String anoSemestre;
	
	/** Armazena o ano letivo. */
	private int anoLetivo;

	/** Armazena a quantidade de turmas de componentes que não sejam atividades. */
	private int qtdTurmasMatriculadas;

	/** Armazena a quantidade de turmas de atividades. */
	private int qtdAtividadesMatriculadas;

	/**
	 * Seta falso caso seja verificação
	 */
	private EmissaoDocumentoAutenticado comprovante;
	
	/** Código de segurança para validação do relatório */
	private String codigoSeguranca;

	/** Setado true se estiver verificando a validade. */
	private boolean verificando = false;

	/** Nivel de ensino do discente. */
	private Character nivelEnsino;

	/** Indica se o caso de uso é de EAD. */
	private boolean ead;

	public AtestadoMatriculaMedioMBean() {

	}

	/**
	 * Verifica as permissões do usuário.
	 * <br />
	 * Não chamado por JSPs.
	 * @throws SegurancaException
	 */
	public void verificaAcesso() throws SegurancaException {
		if (getUsuarioLogado().getDiscenteAtivo() == null) {
			if (SigaaSubsistemas.MEDIO.equals(getSubSistema())) {
				checkRole(new int[] { 
						SigaaPapeis.COORDENADOR_MEDIO,
						SigaaPapeis.GESTOR_MEDIO,
						SigaaPapeis.SECRETARIA_MEDIO,
						SigaaPapeis.PEDAGOGICO_MEDIO});
			}
		}
	}

	/**
	 * Redirecionar para o Managed Bean para a busca de discentes de graduação
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/medio/menus/discente.jsp</li>
	 *	</ul>
	 * @return
	 */
	public String buscarDiscente() throws SegurancaException {
		verificaAcesso();
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.EMISSAO_ATESTADO_MATRICULA_MEDIO);
		buscaDiscenteMBean.setEad(ead);

		// atributo que verifica se o atestado está liberado para emissão. Este
		// atributo vai controlar o uso do F5.
		getCurrentSession().setAttribute("atestadoLiberado", 0);

		return buscaDiscenteMBean.popular();
	}

	/**
	 * Retorna o ano-semestre atual
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getAnoSemestre() throws DAOException {
		return anoSemestre;

	}
	/**
	 * Retorna o ano atual
	 * 
	 * @return
	 * @throws DAOException
	 */
	public int getAnoLetivo() {
		return anoLetivo;
	}

	/**
	 * Responsável por recuperar informações do discente que foi selecionado.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/graduacao/orientacao_academica/lista.jsp</li>
	 *	</ul>
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String selecionaDiscenteForm() throws SegurancaException,
			DAOException {
		discente = getDAO(DiscenteDao.class).findDiscenteAdapterById(getParameterInt("id"));
		
		if (discente.isStricto()) {
			DiscenteStricto ds = (DiscenteStricto) discente;
			if (ds.getArea() != null) ds.getArea().getDenominacao().toString();
			if (ds.getLinha() != null) ds.getLinha().getDenominacao().toString();
		}
		
		getCurrentSession().setAttribute("atestadoLiberado", discente.getId() );
		nivelEnsino = discente.getNivel();
		return selecionaDiscente();
	}

	/**
	 * Seleciona o discente e busca as disciplinas nas quais ele foi matriculado
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/medio/matricula_medio/atestado.jsp</li>
	 *	</ul>
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String selecionaDiscente() throws SegurancaException, DAOException {

		// Verificar bloqueio de emissão do documento
		if (!AutenticacaoUtil.isDocumentoLiberado(
				TipoDocumentoAutenticado.ATESTADO, discente.getNivel())) {
			String mensagem = "A emissão de atestados de matrícula encontra-se temporariamente indisponível";

			if (isMobile(getCurrentRequest())) {
				getCurrentRequest().setAttribute("avisoMobile", mensagem);
				return forward("/mobile/menu.jsp");
			} else {
				addMensagemErro(mensagem);
				return redirectMesmaPagina();
			}
		}

		if ( getCurrentSession().getAttribute("atestadoLiberado") == null
				&& comprovante == null) {
			// comprovante == null por causa da verificação que tem que liberar
			// o atestado
			return buscarDiscente();
		}

		// Validar se o discente está ativo
		if (getDiscente().getStatus() != StatusDiscente.ATIVO
				&& getDiscente().getStatus() != StatusDiscente.ATIVO_DEPENDENCIA
				&& getDiscente().getStatus() != StatusDiscente.CADASTRADO
				&& getDiscente().getStatus() != StatusDiscente.FORMANDO) {
			addMensagemErro("Somente discentes com um vínculo ativo com a instituição podem tirar o atestado de matrícula.");
			return redirectMesmaPagina();
		}

		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class);
		MatriculaComponenteMedioDao mDao = getDAO(MatriculaComponenteMedioDao.class);
		HorarioDao horarioDao = getDAO(HorarioDao.class);
		char nivelEnsino = getDiscente().getNivel();

		try {

			matriculas = buscarMatriculas(mDao);
			
			matriculas.addAll(buscarRenovadas(dao));

			qtdTurmasMatriculadas = contarMatriculasTipoComponente(TipoComponenteCurricular.getNaoAtividades());
			
			qtdAtividadesMatriculadas = contarMatriculasTipoComponente(TipoComponenteCurricular.getAtividades());

			horariosTurma = horarioDao.findByMatriculas(matriculas);

			if (matriculas == null || matriculas.isEmpty()) {

				String mensagem = "O discente selecionado não possui matrículas ativas em disciplinas no ano atual.";
				if (isMobile(getCurrentRequest())) {
					getCurrentRequest().setAttribute("avisoMobile", mensagem);
					return forward("/mobile/menu.jsp");
				} else {
					addMensagemErro(mensagem);
					return redirectMesmaPagina();
				}
			}

			for (MatriculaComponente mc : matriculas) {
				if ( ValidatorUtil.isEmpty(mc.getTurma().getDescricaoHorario() ) )
						mc.getTurma().setDescricaoHorario("---");
			}
			
			int unidadeGestora = 0;

			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(discente);
			if (comprovante == null && !verificando) {
				comprovante = geraEmissao(TipoDocumentoAutenticado.ATESTADO, discente.getMatricula().toString(), 
						geraDigestSemente(matriculas), cal.getAno() + "/" + cal.getPeriodo() + "/" + getUnidadeGestora() + "/" + getNivelEnsino(), null ,false);
				unidadeGestora = getUnidadeGestora();
				//nivelEnsino = getNivelEnsino();
				// recupera o ano semestre atual
				anoSemestre = cal.getAnoPeriodo();
				anoLetivo = cal.getAno();
			} else {

				StringTokenizer st = new StringTokenizer(comprovante.getDadosAuxiliares(), "/");
				String ano = st.nextToken();
				String semestre = st.nextToken();
				unidadeGestora = new Integer(st.nextToken());
				nivelEnsino = st.nextToken().charAt(0);
				anoSemestre = ano + "." + semestre;
				anoLetivo = Integer.parseInt(ano);
			}

			horarios = (List<Horario>) horarioDao.findByUnidadeOtimizado(unidadeGestora, nivelEnsino);
			
			// inclui os horários inativos, caso exista
			boolean reordena = false;
			
			if (horariosTurma != null) {
				for (HorarioTurma ht : horariosTurma) {
					if (horarios.indexOf(ht.getHorario()) < 0) {
						horarios.add(ht.getHorario());
						reordena = true;
					}
				}
				// se houve inserção de horário, reordena a lista.
				if (reordena) Collections.sort(horarios, new Comparator<Horario>() {
					public int compare(Horario o1, Horario o2) {
						return o1.getInicio().compareTo(o2.getInicio());
					}
				});
			}
			
			codigoSeguranca = comprovante.getCodigoSeguranca();
			
			Integer atestadoLiberado = (Integer) getCurrentSession().getAttribute("atestadoLiberado");
			if ( atestadoLiberado != null && atestadoLiberado != 0 ) {
				getCurrentSession().removeAttribute("atestadoLiberado");
			}
			
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErroPadrao();
			return null;
		}

		String jsfAtestado = "/medio/matriculaMedio/atestado.jsf"; 
		
		
		// redireciona assim caso esteja validando o comprovante
		if (comprovante != null && verificando) {
			try {
				getCurrentSession().setAttribute("atestadoMatricula", this);
				getCurrentSession().setAttribute(AutenticacaoUtil.TOKEN_REDIRECT, true);
				redirect(jsfAtestado);
			} catch (Exception e) {
				notifyError(e);
				e.printStackTrace();
			}
			return null;
		} else {
			if (!isMobile(getCurrentRequest())) {
				return forward(jsfAtestado);
			} else {
				return forward("/mobile/atestado_mobile.jsp");
			}
		}

	}

	/**
	 * Conta as matrículas do {@link TipoComponenteCurricular} desejado
	 * 
	 * @param tipos
	 * @return
	 */
	private int contarMatriculasTipoComponente(final Collection<TipoComponenteCurricular> tipos) {
		return CollectionUtils.select(matriculas, new Predicate() {
			
			@Override
			public boolean evaluate(Object obj) {
				MatriculaComponente mat = (MatriculaComponente) obj;
				
				if (tipos.contains(mat.getComponente().getTipoComponente()))
					return true;
				
				return false;
			}
		}).size();
	}

	/**
	 * Busca as matrículas de NÃO ATIVIDADES
	 * 
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private List<MatriculaComponente> buscarMatriculas(MatriculaComponenteMedioDao dao) throws DAOException {

		// Definir calendário		
		CalendarioAcademico cal = null;
		
		if (getUsuarioLogado() != null)
			cal = getCalendarioVigente();
		
		if (cal == null) {
			cal = CalendarioAcademicoHelper.getCalendario(getDiscente());
		}
		return (List<MatriculaComponente>) dao.findMatriculadasByDiscenteAnoPeriodo(getDiscente().getDiscente(), cal.getAno(), null, null);
	}
	
	/**
	 * Busca as matrículas de atividades renovadas no semestre atual.
	 * 
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private List<MatriculaComponente> buscarRenovadas(MatriculaComponenteDao dao) throws DAOException {
		// Definir calendário		
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(getDiscente());
		if( getUsuarioLogado() != null ){
			 cal = getCalendarioVigente();
		}	 
		return (List<MatriculaComponente>) dao.findRenovadasByDiscenteAnoPeriodo(getDiscente().getDiscente(), cal.getAno(), cal.getPeriodo());
	}

	/**
	 * Retorna o discente para o qual será emitido o atestado de matrícula
	 * 
	 * @return
	 */
	public DiscenteAdapter getDiscente() {
		if (discente == null)
			if (getUsuarioLogado().getDiscenteAtivo() != null) {
				return getUsuarioLogado().getDiscenteAtivo();
			} else {
				return discente;
			}
		else
			return discente;
	}

	/** Método utilizado para carregar o discente após a sua seleção.*/
	public void setDiscente(DiscenteAdapter discente) throws DAOException {
		this.discente = discente;
	}

	public List<MatriculaComponente> getDisciplinasMatriculadas() {
		return matriculas;
	}

	public Collection<Horario> getHorarios() {
		return horarios;
	}

	public List<HorarioTurma> getHorariosTurma() {
		return horariosTurma;
	}

	/**
	 * Responsável por recuperar informações do discente através de sua matrícula.
	 * <br />
	 * Não chamado por JSPs.
	 */
	public void exibir(EmissaoDocumentoAutenticado comprovante,
			HttpServletRequest req, HttpServletResponse res) {

		String matricula = comprovante.getIdentificador();

		DiscenteDao dao = getDAO(DiscenteDao.class);
		this.comprovante = comprovante;
		verificando = true;

		try {
			discente = dao.findByMatricula(new Long(matricula));

			selecionaDiscente();
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
		}

	}

	/**
	 * Valida mudanças no histórico.
	 * <br />
	 * Não chamado por JSPs.
	 */
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovante) {

		String matricula = comprovante.getIdentificador();
		DiscenteDao dao = getDAO(DiscenteDao.class);

		try {
			discente = dao.findByMatricula(new Long(matricula));
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
		}

		MatriculaComponenteMedioDao mDao = getDAO(MatriculaComponenteMedioDao.class);
		try {

			Collection<MatriculaComponente> matriculas = buscarMatriculas(mDao);
			String semente = geraDigestSemente(matriculas);

			String codigoVerificacao = AutenticacaoUtil.geraCodigoValidacao(
					comprovante, semente);

			if (codigoVerificacao.equals(comprovante.getCodigoSeguranca())) {
				return true;
			}
		} catch (Exception e) {
			notifyError(e);
		}
		return false;
	}

	/**
	 * Digest de verificação - Coloca todas as matriculas e seus estados.
	 * <br />
	 * Não chamado por JSPs.
	 * 
	 * @param matriculas
	 * @return
	 */
	public String geraDigestSemente(Collection<MatriculaComponente> matriculas) {

		StringBuffer bufferDigest = new StringBuffer();
		for (MatriculaComponente comp : matriculas) {
			bufferDigest.append(comp.getId() + "_"
					+ comp.getSituacaoMatricula().getId());
		}
		return bufferDigest.toString();

	}

	public int getQtdTurmasMatriculadas() {
		return qtdTurmasMatriculadas;
	}

	public void setQtdTurmasMatriculadas(int qtdTurmasMatriculadas) {
		this.qtdTurmasMatriculadas = qtdTurmasMatriculadas;
	}

	public int getQtdAtividadesMatriculadas() {
		return qtdAtividadesMatriculadas;
	}

	public void setQtdAtividadesMatriculadas(int qtdAtividadesMatriculadas) {
		this.qtdAtividadesMatriculadas = qtdAtividadesMatriculadas;
	}

	@Override
	public char getNivelEnsino() {
		if (nivelEnsino == null)
			return super.getNivelEnsino();
		return nivelEnsino;
	}

	/**
	 * Inicia a busca de discentes para EAD.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/ead/menu.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarEad() throws SegurancaException {
		ead = true;
		return buscarDiscente();
	}

	public String getCodigoSeguranca() {
		return codigoSeguranca;
	}

	public void setCodigoSeguranca(String codigoSeguranca) {
		this.codigoSeguranca = codigoSeguranca;
	}
}
