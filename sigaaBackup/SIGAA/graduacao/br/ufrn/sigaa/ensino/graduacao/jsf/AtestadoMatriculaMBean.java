/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/02/2008
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

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
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Managed Bean para geração do atestado de matrícula de alunos de Graduação
 * 
 * @author David Pereira
 * 
 */
@Component("atestadoMatricula") @Scope("request")
public class AtestadoMatriculaMBean extends SigaaAbstractController<Discente> implements OperadorDiscente, AutValidator {

	/** Discente do atestado. */
	private DiscenteAdapter discente;

	/** Listagem de matrículas do discente. */
	private List<MatriculaComponente> matriculas;

	/** Horarios da unidade gestora. */
	private List<Horario> horarios;

	/** Lista de hor[arios de turma de acordo com as matrículas do discente. */
	private List<HorarioTurma> horariosTurma;

	/** Armazena o ano e semestre no formato? {ano}.{periodo} . */
	private String anoSemestre;

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

	/** Nível de ensino do discente. */
	private Character nivelEnsino;

	/** Indica se o caso de uso é de EAD. */
	private boolean ead;
	
	/** Armazena o ano de referência para a emissão do atestado de matrícula. */
	private Integer ano;
	
	/** Armazena o período de referência para a emissão do atestado de matrícula. */
	private Integer periodo;
	
	/** Setado true se for solicitado o atestado de matrícula para disciplinas de férias. */
	private boolean atestadoMatriculaFerias = false;
	public AtestadoMatriculaMBean() {
		

	}

	/**
	 * Verifica as permissões do usuário.
	 * <br/>
	 * Não chamado por JSPs.
	 * @throws SegurancaException
	 */
	public void verificaAcesso() throws SegurancaException {
		if (getUsuarioLogado().getDiscenteAtivo() == null) {
			if (SigaaSubsistemas.GRADUACAO.equals(getSubSistema())) {
				checkRole(new int[] { SigaaPapeis.COORDENADOR_CURSO,
						SigaaPapeis.SECRETARIA_COORDENACAO,
						SigaaPapeis.SECRETARIA_CENTRO, SigaaPapeis.DAE,
						SigaaPapeis.CDP,
						SigaaPapeis.CADASTRA_DISCENTE_GRADUACAO});
			} else if (SigaaSubsistemas.STRICTO_SENSU.equals(getSubSistema())) {
				checkRole(new int[] { SigaaPapeis.COORDENADOR_CURSO_STRICTO,
						SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_POS });
			} else if (SigaaSubsistemas.SEDIS.equals(getSubSistema())) {
				checkRole(SigaaPapeis.COORDENADOR_GERAL_EAD, SigaaPapeis.SEDIS);
			}
		}
	}

	/**
	 * Redirecionar para o Managed Bean para a busca de discentes de graduação
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ead/menu.jsp</li>
	 *		<li>sigaa.war/graduacao/menus/aluno.jsp</li>
	 *		<li>sigaa.war/graduacao/menus/cdp.jsp</li>
	 *		<li>sigaa.war/graduacao/menus/coordenacao.jsp</li>
	 *		<li>sigaa.war/graduacao/menu_coordenador.jsp</li>
	 *		<li>sigaa.war/lato/menu_coordenador.jsp</li>
	 *		<li>sigaa.war/stricto/menus/discente.jsp</li>
	 *		<li>sigaa.war/stricto/coordenador.jsp</li>
	 *		<li>sigaa.war/stricto/menu_coordenador.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/coordenacao.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/discente.jsp</li>
	 *	</ul>
	 * @return
	 */
	public String buscarDiscente() throws SegurancaException {
		verificaAcesso();
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.EMISSAO_ATESTADO_MATRICULA);
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
	 *		<li>sigaa.war/graduacao/orientacao_academica/lista.jsp</li>
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

		DiscenteDao ddao = getDAO(DiscenteDao.class);
		if (discente.isStricto()) {
			discente = ddao.findByPK(discente.getId());
			DiscenteStricto ds = (DiscenteStricto) discente;
			ds.setOrientacao(DiscenteHelper.getOrientadorAtivo(getDiscente().getDiscente()));
			if (ds.getArea() != null) {
				ds.getArea().getDenominacao();
			}
			if (ds.getLinha() != null) {
				ds.getLinha().getDenominacao();
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
				&& getDiscente().getStatus() != StatusDiscente.CADASTRADO
				&& getDiscente().getStatus() != StatusDiscente.FORMANDO) {
			addMensagemErro("Somente discentes com um vínculo ativo com a instituição podem tirar o atestado de matrícula.");
			return redirectMesmaPagina();
		}

		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class);
		HorarioDao horarioDao = getDAO(HorarioDao.class);
		char nivelEnsino = getDiscente().getNivel();

		try {

			// Definir calendário		
			CalendarioAcademico cal = null;
			
			if (ano == null && periodo == null) {
				if (getUsuarioLogado() != null)
					cal = getCalendarioVigente();
				
				if (cal == null) {
					cal = CalendarioAcademicoHelper.getCalendario(getDiscente());
				}
				if (isAtestadoMatriculaFerias()) {
					ano = cal.getAnoFeriasVigente();
					periodo = cal.getPeriodoFeriasVigente();
				} else {
					ano = cal.getAno();
					periodo = cal.getPeriodo();
				}
			}
			
			
			matriculas = buscarMatriculas(dao);
			
			matriculas.addAll(buscarRenovadas(dao));

			qtdTurmasMatriculadas = contarMatriculasTipoComponente(TipoComponenteCurricular.getNaoAtividades());
			
			qtdAtividadesMatriculadas = contarMatriculasTipoComponente(TipoComponenteCurricular.getAtividades());

			horariosTurma = horarioDao.findByMatriculas(matriculas);

			if (matriculas == null || matriculas.isEmpty()) {

				String mensagem = "O discente selecionado não possui matrículas em componentes do período "
					+(isAtestadoMatriculaFerias() ? "de férias vigente." : "atual.");
				if (isMobile(getCurrentRequest())) {
					getCurrentRequest().setAttribute("avisoMobile", mensagem);
					return forward("/mobile/menu.jsp");
				} else {
					addMensagemErro(mensagem);
					return redirectMesmaPagina();
				}
			}

			int unidadeGestora = 0;

			if (comprovante == null && !verificando) {
				comprovante = geraEmissao(TipoDocumentoAutenticado.ATESTADO, discente.getMatricula().toString(), 
						geraDigestSemente(matriculas), ano + "/" + periodo + "/" + getDiscente().getUnidade().getId() + "/" + getNivelEnsino(), null ,false);
				unidadeGestora = getDiscente().getUnidade().getId();
			
				// recupera o ano semestre atual
				anoSemestre = ano + "." + periodo;
			} else {

				StringTokenizer st = new StringTokenizer(comprovante.getDadosAuxiliares(), "/");
				String ano = st.nextToken();
				String semestre = st.nextToken();
				unidadeGestora = new Integer(st.nextToken());
				nivelEnsino = st.nextToken().charAt(0);
				anoSemestre = ano + "." + semestre;
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

		// redireciona assim caso esteja validando o comprovante
		if (comprovante != null && verificando) {
			try {
				getCurrentSession().setAttribute("atestadoMatricula", this);
				getCurrentSession().setAttribute(AutenticacaoUtil.TOKEN_REDIRECT, true);
				redirect("/graduacao/matricula/atestado.jsf");
			} catch (Exception e) {
				notifyError(e);
				e.printStackTrace();
			}
			return null;
		} else {
			if (!isMobile(getCurrentRequest())) {
				return forward("/graduacao/matricula/atestado.jsp");
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
	private List<MatriculaComponente> buscarMatriculas(MatriculaComponenteDao dao) throws DAOException {

		if(!discente.isLato()){
			return (List<MatriculaComponente>) dao.findMatriculadasByDiscenteAnoPeriodo(getDiscente().getDiscente(), ano, periodo, null);
		}
		else
			return (List<MatriculaComponente>) dao.findMatriculadasByDiscenteAnoPeriodo(getDiscente().getDiscente(), null, null, null);
	}
	
	/**
	 * Busca as matrículas de atividades renovadas no semestre atual.
	 * 
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private List<MatriculaComponente> buscarRenovadas(MatriculaComponenteDao dao) throws DAOException {
		if(!discente.isLato()){
			return (List<MatriculaComponente>) dao.findRenovadasByDiscenteAnoPeriodo(getDiscente().getDiscente(), ano, periodo);
		}
		else
			return (List<MatriculaComponente>) dao.findRenovadasByDiscenteAnoPeriodo(getDiscente().getDiscente(), null, null);
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

	/**
	 * Insere o discente para o qual será emitido o atestado de matrícula
	 * 
	 * @return
	 */
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

			if (discente.isGraduacao()) {
				discente = (DiscenteGraduacao) dao.findDiscenteAdapterById(discente.getId());
				if (((DiscenteGraduacao) discente).isEAD()) {
					((DiscenteGraduacao) discente).getPolo().getDescricao();
				}
			}

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

		// Definir calendário		
		CalendarioAcademico cal = null;
		
		try {
			discente = dao.findByMatricula(new Long(matricula));
			
			
			if (cal == null) {
				cal = CalendarioAcademicoHelper.getCalendario(getDiscente());
			}

			ano = cal.getAno();
			periodo = cal.getPeriodo();
			
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
		}

		MatriculaComponenteDao matDao = getDAO(MatriculaComponenteDao.class);
		try {

			Collection<MatriculaComponente> matriculas = buscarMatriculas(matDao);
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

	public boolean isAtestadoMatriculaFerias() {
		return atestadoMatriculaFerias;
	}

	public void setAtestadoMatriculaFerias(boolean atestadoMatriculaFerias) {
		this.atestadoMatriculaFerias = atestadoMatriculaFerias;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}
}
