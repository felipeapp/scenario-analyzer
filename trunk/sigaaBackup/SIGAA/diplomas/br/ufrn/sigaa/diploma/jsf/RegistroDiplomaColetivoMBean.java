/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/05/2009
 *
 */
package br.ufrn.sigaa.diploma.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.DiscenteLatoDao;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.diploma.dao.LivroRegistroDiplomaDao;
import br.ufrn.sigaa.diploma.dao.RegistroDiplomaDao;
import br.ufrn.sigaa.diploma.dominio.LivroRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.ObservacaoRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.RegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.RegistroDiplomaColetivo;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.mensagens.MensagensGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/** Controller respons�vel pelo registro de diplomas coletivo (de uma turma de cola��o de grau).
 * @author �dipo Elder F. Melo
 *
 */
@Component("registroDiplomaColetivo")
@Scope("session")
public class RegistroDiplomaColetivoMBean extends SigaaAbstractController<RegistroDiplomaColetivo> {

	/** Constante utilizada na opera��o de registro de diploma. */
	private final int REGISTRAR_DIPLOMAS = 1;
	
	/** Constante utilizada na opera��o de registro de diploma. */
	private final int VISUALIZAR_REGISTRO_COLETIVO = 2;
	
	/** Vari�vel utilizada na opera��o de registro de diploma. */
	private int operacao = 0;
	
	/** Cole��o de graduandos da turma. */
	private Collection<Discente> discentes;
	
	/** Cole��o de graduandos que ter�o o registro de diploma efetuado. */
	private Collection<Discente> registrar;
	
	/** Cole��o de discentes da turma que j� tiveram o diploma registrado. */
	private Collection<RegistroDiploma> discentesRegistrados;
	
	/** Ano a realizar a busca por cola��o de graus coletivas. */
	private int ano;
	/** Semestre a realizar a busca por cola��o de graus coletivas. */
	private int semestre;

	/** Lista de turmas de cola��o de grau encontradas na busca para o registro coletivo.*/
	private List<Map<String, Object>> turmasEncontradas;

	/** Indica se deve exibir um bot�o "Voltar" na view. */
	private boolean exibirVoltar;

	/**
	 * Inicia a opera��o de registro de diploma coletivo.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/menus/registro.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		initObj();
		checkChangeRole();
		prepareMovimento(SigaaListaComando.REGISTRO_DIPLOMA_COLETIVO);
		setOperacaoAtiva(SigaaListaComando.REGISTRO_DIPLOMA_COLETIVO.getId());
		setConfirmButton("Cadastrar");
		operacao = REGISTRAR_DIPLOMAS;
		return formBuscaCurso();
	}

	/**
	 * Busca por registros de diploma coletivo.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/menus/registro.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarBusca() throws SegurancaException {
		initObj();
		checkChangeRole();
		operacao = VISUALIZAR_REGISTRO_COLETIVO;
		return formBuscaCurso();
	}

	/**
	 * Cadastra os registros de diplomas.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/registro_diplomas/dados_registro.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		validacaoDados(erros);
		if (hasErrors())
			return null;
		// setando os valores de registro
		for (Discente discente: this.discentes) {
			if (discente.isMatricular()) {
				// cria um registro de  diploma
				RegistroDiploma registro = this.obj.getLivroRegistroDiploma().getRegistroLivre();
				registro.setDiscente(discente);
				registro.setDataColacao(obj.getDataColacao());
				registro.setDataExpedicao(obj.getDataExpedicao());
				registro.setDataRegistro(obj.getDataRegistro());
				registro.setProcesso(obj.getProcesso());
				registro.setLivre(false);
				registro.setRegistroDiplomaColetivo(obj);
				ObservacaoRegistroDiploma obs = null;
				if (obj.getLivroRegistroDiploma().isRegistroCertificado())
					obs = new ObservacaoRegistroDiploma("Certificado registrado em Turma de Conclus�o Coletiva.");
				else
					obs = new ObservacaoRegistroDiploma("Diploma registrado em Turma de Conclus�o Coletiva.");
				obs.setRegistroDiploma(registro);
				registro.setObservacoes(new ArrayList<ObservacaoRegistroDiploma>());
				registro.addObservacao(obs);
				obj.getRegistrosDiplomas().add(registro);
			} 
		}
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.REGISTRO_DIPLOMA_COLETIVO);
		mov.setObjMovimentado(obj);
		execute(mov, getCurrentRequest());
		if (obj.getLivroRegistroDiploma().isRegistroCertificado())
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Registro de Certificado");
		else
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Registro de Diploma");
		buscarTurma();
		ImpressaoDiplomaMBean mbean = getMBean("impressaoDiploma");
		mbean.setRegistroDiplomaColetivo(obj);
		return forward(formResumoColetivo());
	}
	
	/** Inicializa os atributos do controller.
	 * 
	 */
	private void initObj() {
		this.obj = new RegistroDiplomaColetivo();
		this.obj.setCurso(new Curso());
		this.obj.setRegistrosDiplomas(new ArrayList<RegistroDiploma>());
		this.obj.setProcesso("23077.");
		this.obj.setLivroRegistroDiploma(new LivroRegistroDiploma());
		this.ano = CalendarUtils.getAnoAtual();
		this.semestre = getPeriodoAtual();
		this.discentesRegistrados = new ArrayList<RegistroDiploma>();
		this.turmasEncontradas = new ArrayList<Map<String,Object>>();
		this.exibirVoltar = false;
	}

	/**
	 * Registra os diplomas de uma turma de cola��o de grau.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/registro_diplomas/busca_curso.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 * @throws ParseException
	 */
	public String registrarTurma() throws DAOException, SegurancaException, ParseException {
		checkChangeRole();
		Integer idCurso = getParameterInt("idCurso");
		Integer idPolo = getParameterInt("idPolo");
		Date dataColacao = CalendarUtils.parseDate(getParameter("dataColacao"));
		if (idCurso != null)
			obj.getCurso().setId(idCurso);
		if (dataColacao != null)
			obj.setDataColacao(dataColacao);
		if (idPolo != null)
			obj.setPolo(new Polo(idPolo));
		// valida os dados antes da busca
		ValidatorUtil.validateRequired(obj.getDataColacao(), "Data de Conclus�o", erros);
		ValidatorUtil.validateRequired(obj.getCurso(), "Curso", erros);
		if (hasErrors())
			return null;
		if (obj.getLivroRegistroDiploma().isGraduacao()) {
			// busca por graduandos que colaram grau na data informada
			DiscenteGraduacaoDao dao = getDAO(DiscenteGraduacaoDao.class);
			discentes = new ArrayList<Discente>();
			for (DiscenteGraduacao dg : dao.findConcluidosByCursoDataColacao(this.obj.getCurso().getId(), this.obj.getDataColacao(), this.obj.getPolo()))
					discentes.add(dg.getDiscente());
		} else if (obj.getLivroRegistroDiploma().isLatoSensu()) {
			// busca por discentes que possuem a mesma data da movimenta��o de conclus�o
			DiscenteLatoDao dao = getDAO(DiscenteLatoDao.class);
			discentes = dao.findConcluidosByCursoDataColacao(this.obj.getCurso().getId(), this.obj.getDataColacao());
		}
		if (discentes == null || discentes.isEmpty()) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		switch (operacao) {
			case REGISTRAR_DIPLOMAS: return prepareRegistroDiploma(discentes);
			case VISUALIZAR_REGISTRO_COLETIVO: return viewRegistroDiplomaColetivo();
			default: return null;
		}
	}

	/**
	 * Busca as turmas de cola��o coletivas.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/registro_diplomas/busca_curso.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String buscarTurma() throws DAOException, SegurancaException {
		checkChangeRole();
		// valida os dados antes da busca
		if (!NivelEnsino.isValido(obj.getLivroRegistroDiploma().getNivel())) 
			erros.addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, "N�vel de Ensino");
		ValidatorUtil.validateRange(semestre, 1, 2, "Semestre", erros);
		ValidatorUtil.validateRequired(ano, "Ano", erros);
		if (hasErrors()) return null;
		int anoInicioRegistro = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.ANO_INICIO_REGISTRO_DIPLOMA);
		int semestreInicioRegistro = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.SEMESTRE_INICIO_REGISTRO_DIPLOMA);
		if (ano * 10 + semestre < anoInicioRegistro * 10 + semestreInicioRegistro) {
			addMensagem(MensagensGraduacao.REGISTRO_DIPLOMA_ANTERIOR_AO_PERIODO_INICIO, anoInicioRegistro, semestreInicioRegistro);
			return null;
		}
		obj.setAno(ano);
		obj.setPeriodo(semestre);
		if (obj.getLivroRegistroDiploma().isGraduacao()) {
			DiscenteGraduacaoDao dao = getDAO(DiscenteGraduacaoDao.class);
			turmasEncontradas = dao.findTurmasColacaoByCursoAnoPeriodoConclusao(this.obj.getCurso().getId(), this.ano, this.semestre);
		} else if (obj.getLivroRegistroDiploma().isLatoSensu()) {
			DiscenteLatoDao dao = getDAO(DiscenteLatoDao.class);
			turmasEncontradas = dao.findTurmasConclusaoByCursoAnoPeriodoConclusao(this.obj.getCurso().getId(), this.ano, this.semestre);
		}
		// busca discentes que colaram grau na data informada
		if (isEmpty(turmasEncontradas)) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		}
		return null;
	}

	/** Prepara o registro de diplomas coletivo, utilizando a lista de graduandos.<br/>M�todo n�o invocado por JSP�s.
	 * @param discentes
	 * @return
	 * @throws DAOException
	 */
	private String prepareRegistroDiploma(Collection<Discente> discentes) throws DAOException {
		RegistroDiplomaDao registroDiplomaDao = getDAO(RegistroDiplomaDao.class);
		LivroRegistroDiplomaDao livroDao = getDAO(LivroRegistroDiplomaDao.class);

		// busca por registro de diploma coletivo do curso no ano semestre
		RegistroDiplomaColetivo registroDiplomaAntigo = registroDiplomaDao.findByCursoDataColacao(obj.getCurso().getId(), obj.getDataColacao(), obj.getPolo());
		if (registroDiplomaAntigo == null) {
			// n�o existe registro de diploma coletivo para o curso/ano-per�odo
			// Verifica se h� graduandos n�o registrados na turma.
			boolean possuiDiscenteNaoRegistrado = false;
			Collection<Discente> registrados = registroDiplomaDao.verificaDiscentesRegistrados(discentes);
			if (registrados != null) {
				for (Discente discente : discentes) {
					if (!registrados.contains(discente)) {
						possuiDiscenteNaoRegistrado = true;
						break;
					} 
				}
			}
			// se todos da turma foram registrados, retorne
			if (!possuiDiscenteNaoRegistrado) {
				addMensagem(MensagensGraduacao.TODOS_DISCENTES_DA_TURMA_FORAM_REGISTRADOS);
				return null;
			}
			// existe livro de registro para o curso?
			LivroRegistroDiploma livro = null;
			if (obj.getLivroRegistroDiploma().isGraduacao()) {
				livro = livroDao.findByCurso(obj.getCurso().getId(), true, false);
			} else if (obj.getLivroRegistroDiploma().isLatoSensu()) {
				livro = livroDao.findLivroAtivoPosByNivelEnsino(false, NivelEnsino.LATO);
			}
			if (livro == null) {
				// n�o existe um livro aberto
				Curso curso = livroDao.refresh(obj.getCurso());
				addMensagem(MensagensGraduacao.NAO_HA_LIVRO_ABERTO_PARA_REGISTRO_CURSO, curso);
				return null;
			} else {
				obj.setLivroRegistroDiploma(livro);
				criaRegistroAutomatico();
				return formDadosRegistro();
			}
		} else {
			addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Turma de Cola��o de Grau");
			return null;
		}
	}
	
	/** Retorna o link para escolha do livro a ser utilizado no registro de diplomas.
	 * <br/>M�todo n�o invocado por JSP�s.
	 * @return
	 */
	public String formLivroRegistro() {
		return "/diplomas/registro_diplomas/escolhe_livro.jsp";
	}
	
	/** Retorna o link para o resumo do registro de diploma coletivo.
	 * <br/>M�todo n�o invocado por JSP�s.
	 * @return
	 */
	public String formResumoColetivo() {
		return "/diplomas/registro_diplomas/resumo_coletivo.jsp";
	}
	
	/** Retorna o link para o comprovante do registro de diploma coletivo.
	 * <br/>M�todo n�o invocado por JSP�s.
	 * @return
	 */
	public String formComprovanteColetivo() {
		return forward("/diplomas/registro_diplomas/view_coletivo.jsp");
	}

	/**
	 * Cria uma pr�via do registro autom�tico de diplomas.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/registro_diplomas/escolhe_livro.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public String criaRegistroAutomatico() throws DAOException {
		ValidatorUtil.validateRequiredId(obj.getLivroRegistroDiploma().getId(), "Livro", erros);
		if (hasErrors()) return null;
		RegistroDiplomaDao registroDiplomaDao = getDAO(RegistroDiplomaDao.class);
		this.obj.setCurso(registroDiplomaDao.findByPrimaryKey(this.obj.getCurso().getId(), Curso.class));
		discentesRegistrados = new ArrayList<RegistroDiploma>();
		
		// se o livro foi escolhido, recupera os atributos
		if (obj.getLivroRegistroDiploma().getTitulo() == null) {
			obj.setLivroRegistroDiploma(registroDiplomaDao.refresh(obj.getLivroRegistroDiploma()));
		}
		
		// exclui os discente j� registrados
		registrar = new ArrayList<Discente>();
		Collection<Discente> registrados = registroDiplomaDao.verificaDiscentesRegistrados(discentes);
		for (Discente discente : discentes) {
			if (!registrados.contains(discente)) {
				discente.setMatricular(true);
				registrar.add(discente);
			} else {
				discentesRegistrados.add(registroDiplomaDao.findByDiscente(discente.getId()));
			}
		}
		// se todos da turma foram registrados e a lista a registrar � vazia, retorne
		if (registrar.size() == 0) {
			addMensagem(MensagensGraduacao.TODOS_DISCENTES_DA_TURMA_FORAM_REGISTRADOS);
			return null;
		}
		else { 
			return formDadosRegistro();
		}
	}

	/**
	 * Cria uma lista de registro de diplomas coletivo para visualiza��o.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/registro_diplomas/busca_curso.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public String viewRegistroDiplomaColetivo() throws DAOException {
		RegistroDiplomaDao registroDiplomaDao = getDAO(RegistroDiplomaDao.class);
		RegistroDiplomaColetivo registroColetivo = registroDiplomaDao.findByCursoDataColacao(obj.getCurso().getId(), obj.getDataColacao(), obj.getPolo());
		if (registroColetivo == null) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		obj = registroColetivo;
		obj.setCurso(registroDiplomaDao.findByPrimaryKey(this.obj.getCurso().getId(), Curso.class));
		this.exibirVoltar = true;
		return forward(formResumoColetivo());
	}
	
	
	/** Valida os dados para o cadastro de diploma: Ano/Per�odo, datas de cole��o, expedi��o e registro, e n�mero do processo.
	 * <br/>M�todo n�o invocado por JSP�s.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(java.util.Collection)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens lista) {
		ValidatorUtil.validateRequired(obj.getAno(), "Ano", lista);
		ValidatorUtil.validateRange(obj.getPeriodo(), 1, 2, "Per�odo", lista);
		// datas
		ValidatorUtil.validateRequired(obj.getDataColacao(), "Data de Cola��o", lista);
		ValidatorUtil.validateRequired(obj.getDataExpedicao(), "Data de Expedi��o", lista);
		ValidatorUtil.validateRequired(obj.getDataRegistro(), "Data de Registro", lista);
		ValidatorUtil.validateRequired(obj.getProcesso(), "N� do Processo", erros);
		if(!confirmaProcesso()) {
			lista.addMensagem(MensagensGerais.NUMERO_PROCESSO_NAO_ENCONTRADO);
		}
		// verifica se h� aluno selecionado
		boolean selecionado = false;
		for (Discente discente: this.discentes) {
			if (discente.isMatricular()) {
				selecionado = true;
				break;
			}
		}
		if (!selecionado)
			lista.addMensagem(MensagensGraduacao.SELECIONE_DISCENTE_PARA_REGISTAR_DIPLOMA);
		return hasErrors();
	}

	/**
	 * Retorna o link para o formul�rio de busca de cursos.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/registro_diplomas/escolhe_livro.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String formBuscaCurso() {
		return forward("/diplomas/registro_diplomas/busca_curso.jsp");
	}

	/** Retorna o link para o formul�rio de dados dos registros de diplomas.
	 * <br/>M�todo n�o invocado por JSP�s.
	 * @return
	 */
	public String formDadosRegistro() {
		return forward("/diplomas/registro_diplomas/dados_registro.jsp");
	}
	
	/** Retorna o link para o formul�rio de cancelamento de registro de diplomas.
	 * <br/>M�todo n�o invocado por JSP�s.
	 * @return
	 */
	public String formCancelarRegistro() {
		return forward("/diplomas/registro_diplomas/cancelar_registro.jsp");
	}
	
	/** Verifica se o processo existe no banco.
	 * @return
	 */
	private boolean confirmaProcesso() {
		ImpressaoDiplomaMBean mBean = getMBean("impressaoDiploma");
		return mBean.confirmaProcesso(obj.getProcesso());
	}

	/** Retorna a cole��o de discentes da turma que j� tiveram o diploma registrado. 
	 * @return
	 */
	public Collection<RegistroDiploma> getDiscentesRegistrados() {
		return discentesRegistrados;
	}

	/** Seta a cole��o de discentes da turma que j� tiveram o diploma registrado.
	 * @param discentesRegistrados
	 */
	public void setDiscentesRegistrados(Collection<RegistroDiploma> discentesRegistrados) {
		this.discentesRegistrados = discentesRegistrados;
	}

	/** Retorna a cole��o de graduandos que ter�o o registro de diploma efetuado. 
	 * @return
	 */
	public Collection<Discente> getRegistrar() {
		return registrar;
	}

	/** Seta a cole��o de graduandos que ter�o o registro de diploma efetuado.
	 * @param discentes
	 */
	public void setRegistrar(Collection<Discente> registrar) {
		this.registrar = registrar;
	}

	/** Retorna o ano a realizar a busca por cola��o de graus coletivas. 
	 * @return
	 */
	public int getAno() {
		return ano;
	}

	/** Seta o ano a realizar a busca por cola��o de graus coletivas.
	 * @param ano
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Retorna o semestre a realizar a busca por cola��o de graus coletivas. 
	 * @return
	 */
	public int getSemestre() {
		return semestre;
	}

	/** Seta o semestre a realizar a busca por cola��o de graus coletivas.
	 * @param semestre
	 */
	public void setSemestre(int semestre) {
		this.semestre = semestre;
	}

	/** Retorna a lista de turmas de cola��o de grau encontradas na busca para o registro coletivo.
	 * @return
	 */
	public List<Map<String, Object>> getTurmasEncontradas() {
		return turmasEncontradas;
	}

	/** Seta a lista de turmas de cola��o de grau encontradas na busca para o registro coletivo.
	 * @param turmasEncontradas
	 */
	public void setTurmasEncontradas(List<Map<String, Object>> turmasEncontradas) {
		this.turmasEncontradas = turmasEncontradas;
	}

	/** Verifica as permiss�es do usu�rio.
	 * <br/>M�todo n�o invocado por JSP�s.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_DIPLOMAS_GRADUACAO, SigaaPapeis.GESTOR_DIPLOMAS_LATO, SigaaPapeis.GESTOR_DIPLOMAS_STRICTO);
	}
	
	/**
	 * Retorna uma cole��o de {@link SelectItem} de n�veis de ensino que o
	 * usu�rio pode registrar diploma de forma coletiva. No caso de registro de
	 * diplomas coletivos ser� restrito apenas aos cursos de gradua��o e lato sensu.
	 * 
	 * @see br.ufrn.sigaa.arq.jsf.SigaaAbstractController#getNiveisHabilitadosCombo()
	 */
	@Override
	public Collection<SelectItem> getNiveisHabilitadosCombo() {
		Collection<SelectItem> combo = new ArrayList<SelectItem>();
			if (getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_DIPLOMAS_GRADUACAO))
				combo.add(new SelectItem(new Character(NivelEnsino.GRADUACAO), NivelEnsino.getDescricao(NivelEnsino.GRADUACAO)));
			if (getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_DIPLOMAS_LATO))
				combo.add(new SelectItem(new Character(NivelEnsino.LATO), NivelEnsino.getDescricao(NivelEnsino.LATO)));
		return combo;
	}

	public boolean isExibirVoltar() {
		return exibirVoltar;
	}
}
