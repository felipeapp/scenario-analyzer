/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/01/2007
 *
 */	
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.InstituicoesEnsinoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ensino.dominio.AreaSesu;
import br.ufrn.sigaa.ensino.dominio.ConvenioAcademico;
import br.ufrn.sigaa.ensino.dominio.NaturezaCurso;
import br.ufrn.sigaa.ensino.dominio.TipoCicloFormacao;
import br.ufrn.sigaa.ensino.dominio.TipoOfertaCurso;
import br.ufrn.sigaa.ensino.dominio.TipoOfertaDisciplina;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoCursoGraduacao;
import br.ufrn.sigaa.ensino.stricto.dominio.OrganizacaoAdministrativa;
import br.ufrn.sigaa.ensino.stricto.dominio.TipoCursoStricto;
import br.ufrn.sigaa.jsf.MunicipioMBean;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.vestibular.dominio.AreaConhecimentoVestibular;

/**
 * MBean responsável pelas determinadas operações de registro sobre os casos de uso dos cursos de graduação.
 *
 * @author André
 *
 */
@Component("cursoGrad")
@Scope("request")
public class CursoGraduacaoMBean extends SigaaAbstractController<Curso> {

	/** Link para o menu graduação. */
	public static final String JSP_MENU_GRADUACAO = "menuGraduacao";

	/** Link para o formulário de dados do curso. */
	public static final String JSP_DADOS_GERAIS = "/graduacao/curso/form.jsf";

	/** Arquivo do Regimento do Curso (caso curso de stricto sensu) ou Projeto Político-Pedagógico (caso curso de graduação). */
	private UploadedFile arquivo;

	/** Coleção de tipos de tipos de cursos de stricto sensu. */
	private Collection<SelectItem> tiposStricto;

	/** Parâmetro utilizado na busca de curso. */
	private String param;
	
	/** Coleção de Instituições de Ensino vinculadas aos cursos de stricto sensu situadas em redes de ensino. */
	private InstituicoesEnsino instituicaoEnsino = new InstituicoesEnsino();
	
	/** Lista de Instituições de ensino associadas ao curso. */
	private List<InstituicoesEnsino> instituicoesEnsino = new ArrayList<InstituicoesEnsino>();

	/** Coleção de selecitens de cursos de graduação. */
	private Collection<SelectItem> allCombo;
	
	/** Retorna a coleção de tipos de tipos de cursos de stricto sensu. 
	 * @return
	 */
	public Collection<SelectItem> getTiposStricto() {
		return tiposStricto;
	}

	/** Seta a coleção de tipos de tipos de cursos de stricto sensu. 
	 * @param tiposStricto
	 */
	public void setTiposStricto(Collection<SelectItem> tiposStricto) {
		this.tiposStricto = tiposStricto;
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	/** Construtor padrão. */
	public CursoGraduacaoMBean() {
		initObj();
	}

	/**
	 * Inicializa o objeto gerenciado pelo MBean
	 */
	protected void initObj() {
		obj = new Curso();
		obj.setUnidade(new Unidade());
		obj.setUnidade2(new Unidade());
		obj.setMunicipio(new Municipio());
		obj.setNaturezaCurso(new NaturezaCurso());
		obj.setModalidadeEducacao(new ModalidadeEducacao());
		obj.setConvenio(new ConvenioAcademico());
		obj.setAreaCurso(new AreaConhecimentoCnpq());
		obj.setAreaSesu(new AreaSesu());
		obj.setNivel(NivelEnsino.GRADUACAO);
		obj.setTipoOfertaCurso(new TipoOfertaCurso());
		obj.setTipoOfertaDisciplina(new TipoOfertaDisciplina());
		obj.setTipoCicloFormacao(new TipoCicloFormacao());
		obj.setOrganizacaoAdministrativa(new OrganizacaoAdministrativa());
		obj.setUnidadeCoordenacao(new Unidade());
		obj.setAreaVestibular(new AreaConhecimentoVestibular());
	}

	/**
	 * Monta combo de cursos de graduação. <br/><br/>
	 * JSP: 
	 * <ul>
	 * 	<li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form_cursos.jsp
	 * 	<li>/sigaa.war/ensino/coordenacao_curso/form.jsp
	 * 	<li>/sigaa.war/ensino/coordenacao_curso/lista.jsp
	 * 	<li>/sigaa.war/geral/estrutura_curricular/busca_geral.jsp
	 * 	<li>/sigaa.war/ensino/turma/busca_turma.jsp
	 * 	<li>/sigaa.war/graduacao/curriculo/lista.jsp
	 * 	<li>/graduacao/curriculo_optativos/selecao_curriculo.jsp
	 * 	<li>/sigaa.war/graduacao/discente/form.jsp
	 * 	<li>/sigaa.war/graduacao/matriz_curricular/lista.jsp
	 * 	<li>/sigaa.war/graduacao/matriz_curricular/matriz.jsp
	 * 	<li>/sigaa.war/graduacao/mudanca_curricular/mudanca_coletiva.jsp
	 * 	<li>/sigaa.war/graduacao/reconhecimento/form.jsp
	 * 	<li>/sigaa.war/graduacao/relatorios/discente/seleciona_matriculados.jsp
	 * 	<li>/sigaa.war/graduacao/relatorios/discente/seleciona_vinculados_estrutura.jsp
	 * 	<li>/sigaa.war/graduacao/turma/form_reservas.jsp
	 * 	<li>/sigaa.war/vestibular/relatorios/seleciona_ps_perfil.jsp
	 * </ul>
	 * @throws DAOException 
	 */
	@Override
	public Collection<SelectItem> getAllCombo() throws DAOException {
		if (allCombo == null) {
			CursoDao dao = getDAO(CursoDao.class);
			allCombo = toSelectItems(dao.findByNivelWithProjection(NivelEnsino.GRADUACAO),	"id", "descricao");
		}
		return allCombo;
	}

	/**
	 * Retorna uma coleção de SelectItem com os cursos de graduação pro-básica.
	 * <br/><br/>
	 * JSP: /sigaa.war/graduacao/turma/dados_gerais.jsp
	 * @return
	 */
	public Collection<SelectItem> getAllProbasicaCombo() {
		CursoDao dao = null;
		try {
			dao = getDAO(CursoDao.class);
			return toSelectItems(dao.findByConvenioAcademico(
					ConvenioAcademico.PROBASICA, NivelEnsino.GRADUACAO), "id",
					"descricao");
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			return new ArrayList<SelectItem>();
		} finally {
			if (dao != null) dao.close();
		}
	}

	/**
	 * Carrega o tipo de curso stricto: Mestrado Acadêmico, Mestrado Profissional, Doutorado.
	 * <br/><br/>
	 * JSP: /sigaa.war/graduacao/curso/form.jsp
	 * @param e
	 * @throws DAOException
	 */
	public void carregarTiposStricto(ValueChangeEvent e) throws DAOException {
		if (e != null && e.getNewValue() != null
				&& !"".equals(e.getNewValue().toString().trim())) {
			Collection<TipoCursoStricto> tipos = getGenericDAO()
					.findByExactField(TipoCursoStricto.class, "nivel",
							e.getNewValue().toString());
			tiposStricto = toSelectItems(tipos, "id", "descricao");
		} else {
			Collection<TipoCursoStricto> tipos = getGenericDAO()
					.findByExactField(TipoCursoStricto.class, "nivel",
							obj.getNivel() + "");
			tiposStricto = toSelectItems(tipos, "id", "descricao");
		}
	}

	/**
	 * Ação executada depois do cadastro.
	 * <br/><br/>
	 * JSP: Não invocado por JSP
	 */
	@Override
	protected void afterCadastrar() throws ArqException {
		super.afterCadastrar();
		initObj();
	}

	/** Cancela a operação corrente.
	 * <br/><br/>
	 * JSP:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/curso/form.jsp
	 * 	<li>/sigaa.war/graduacao/curso/lista.jsp
	 * 	<li>/sigaa.war/graduacao/curso/view.jsp
	 * 	<li>/sigaa.war/graduacao/relatorios/ocupacao_turma/busca_componente.jsp
	 * </ul>
	 */
	@Override
	public String cancelar() {
		return super.cancelar();
	}
	
	/** Cancela a operação corrente do formulário e retorna para a listagem de cursos. 
	 * <br/><br/>
	 * JSP:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/curso/form.jsp
	 * 	<li>/sigaa.war/graduacao/curso/lista.jsp
	 * 	<li>/sigaa.war/graduacao/curso/view.jsp
	 * 	<li>/sigaa.war/graduacao/relatorios/ocupacao_turma/busca_componente.jsp
	 * </ul>
	 */
	public String cancelarForm(){
		return forward(getListPage());
	}

	/**
	 * Busca por cursos de acordo com o parâmetro. 
	 * <br/><br/>
	 * JSP:
	 * /sigaa.war/graduacao/curso/lista.jsp
	 */
	@Override
	public String buscar() throws DAOException {
		param = getParameter("paramBusca");
		if (param == null) {
			addMensagemErro("Selecione um tipo de busca e digite o parâmetro de busca");
			return null;
		}

		CursoDao dao = getDAO(CursoDao.class);
		if ("nome".equalsIgnoreCase(param)) {
			ValidatorUtil.validateRequired(obj.getNome(), "Nome", erros);
			if (hasErrors()) return null;
			setResultadosBusca(dao.findByNome(obj.getNome(), 0, null, getNivelEnsino(), null, null));
		}
		else if ("centro".equalsIgnoreCase(param)) {
			String campo;
			switch (getNivelEnsino()) {
				case 'G': campo = "Centro"; break;
				case 'S': campo = "Programa"; break;
				default: campo = "Unidade"; break;
			}
			ValidatorUtil.validateRequired(obj.getUnidade(), campo, erros);
			if (hasErrors()) return null;
			setResultadosBusca(dao.findByUnidade(obj.getUnidade().getId(), getNivelEnsino(), null));
		}
		else if ("todos".equalsIgnoreCase(param)) {
			setResultadosBusca(dao.findByNivel(getNivelEnsino(), null, true, null));
		}
		else
			setResultadosBusca(null);
		
		if((getResultadosBusca()!=null && getResultadosBusca().isEmpty()) || getResultadosBusca()==null) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		}		
		
		return null;
	}

	/**
	 * Não invocado por JSP
	 */
	@Override
	public String getFormPage() {
		return JSP_DADOS_GERAIS;
	}

	/**
	 * Não invocado por JSP
	 */
	@Override
	public String getListPage() {
		return "/graduacao/curso/lista.jsf";
	}

	/** Redireciona para a lista de cursos
	 * <br>@see br.ufrn.arq.web.jsf.AbstractControllerCadastro#listar()
	 * <br>Método chamado pela JSP:
	 * <ul><li>/sigaa.war/stricto/menus/relatorios.jsp</li></ul>
	 */
	@Override
	public String listar() throws ArqException {
		initObj();
		this.resultadosBusca = null;
		this.param = "";
		return super.listar();
	}
	
	/** Redireciona para a lista de cursos de graduação
	 * <br>@see br.ufrn.arq.web.jsf.AbstractControllerCadastro#listar()
	 * <br>Método chamado pela JSP:
	 * <ul><li>/sigaa.war/portais/abas/rh_plan/graduacao.jsp</li></ul>
	 */
	public String listarCursosGrad() throws ArqException {
		initObj();
		this.resultadosBusca = null;
		this.param = "";
		getCurrentSession().setAttribute("nivel", NivelEnsino.GRADUACAO);
		return super.listar();
	}
	/**
	 * Não invocado por JSP
	 */
	@Override
	public String getViewPage() {
		return "/graduacao/curso/d_view.jsf";
	}

	public boolean isStricto() {
		return getNivelEnsino() == NivelEnsino.STRICTO;
	}

	/**
	 * Realiza o cadastro de um curso.
	 * <br/><br/>
	 * JSP: /sigaa.war/graduacao/curso/form.jsp
	 */
	@Override
	public String cadastrar() throws ArqException {
		if (!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_CURSO_GRADUACAO.getId(),
				SigaaListaComando.ALTERAR_CURSO_GRADUACAO.getId(),ArqListaComando.REMOVER.getId()))
			return cancelar();
		
		if (!confirmaSenha()) {
			return null;
		}

		if (!"remover".equalsIgnoreCase(getConfirmButton())) {
			
			erros = new ListaMensagens();
			erros.addAll(obj.validate().getMensagens());
			
			if (hasErrors()) {
				return null;
			}
		}
		
		if(obj.isRede() && instituicoesEnsino.isEmpty()){
			addMensagemErro("Para Curso em rede é necessário informar a(s) Instituição(ões) de Ensino!");
			return null;
		}
		
		preencherNulos();

		// Cursos de stricto são previamente definido com o tipo de ciclo de formação único.
		if(obj.isStricto())
			obj.setTipoCicloFormacao(TipoCicloFormacao.CICLO_UNICO);
		
		
		Comando comando = SigaaListaComando.CADASTRAR_CURSO_GRADUACAO;
		
		if ("alterar".equalsIgnoreCase(getConfirmButton())) {
			comando = SigaaListaComando.ALTERAR_CURSO_GRADUACAO;
			
		} else if ("remover".equalsIgnoreCase(getConfirmButton())) {
			comando = ArqListaComando.REMOVER;
		}
		
		StringUtils.toLatin9(obj.getNome());
		
		MovimentoCursoGraduacao mov = new MovimentoCursoGraduacao();
		mov.setCodMovimento(comando);
		mov.setObjMovimentado(obj);
		mov.setArquivo(arquivo);
		mov.setInstituicoesEnsino(instituicoesEnsino);
		// se o curso estiver sendo cadastrado a partir do módulo de registro de diplomas, ele deve ser inativo.
		if (isModuloDiplomas())
			mov.setCadastrarCursoInativo(true);
		else
			mov.setCadastrarCursoInativo(false);
		try {
		
			if("remover".equalsIgnoreCase(getConfirmButton()) && !NivelEnsino.isAlgumNivelStricto( obj.getNivel() )){
				obj.setTipoCursoStricto(null);
			}
			
			execute(mov, getCurrentRequest());
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			verificaNulos();
			return null;
		} catch (ArqException e) {
			addMensagemErro(e.getMessage());
			e.printStackTrace();
			verificaNulos();
			return null;
		} catch (Exception e) {
			verificaNulos();
			return tratamentoErroPadrao(e);
		}
		
		super.afterCadastrar();
		
		initObj();
		
		if (comando.equals(SigaaListaComando.CADASTRAR_CURSO_GRADUACAO)) {
			addMessage("Curso cadastrado com sucesso!", TipoMensagemUFRN.INFORMATION);
			
		} else if (comando.equals(SigaaListaComando.ALTERAR_CURSO_GRADUACAO)) {
			addMessage("Curso atualizado com sucesso!", TipoMensagemUFRN.INFORMATION);
			
		} else if (comando.equals(ArqListaComando.REMOVER)) {
			addMessage("Curso removido com sucesso!", TipoMensagemUFRN.INFORMATION);
		}
		removeOperacaoAtiva();
		return cancelar();
	}

	/**
	 * Cadastra um curso.
	 * <br/><br/>
	 * JSP: 
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/menus/cdp.jsp
	 *  <li>/sigaa.war/stricto/menus/cadastro.jsp
	 * </ul> 
	 */
	@Override
	public String preCadastrar() throws ArqException {
		
		initObj();
		
		setReadOnly(false);
		setConfirmButton("Cadastrar");
		
		// gestor de diplomas poderá cadastrar cursos de graduação antigos.
		if (isModuloDiplomas()) {
			obj.setNaturezaCurso(new NaturezaCurso(NaturezaCurso.GRADUACAO));
			obj.setNivel(NivelEnsino.GRADUACAO);
			obj.setAtivo(false);
			checkRole(SigaaPapeis.GESTOR_DIPLOMAS_GRADUACAO);
		} else if (getNivelEnsino() == NivelEnsino.STRICTO) {
			
			checkRole(SigaaPapeis.PPG);
			
			
			
			obj.setModalidadeEducacao(new ModalidadeEducacao(ModalidadeEducacao.PRESENCIAL));
			obj.setNaturezaCurso(new NaturezaCurso(NaturezaCurso.OUTRAS));
			obj.setTipoOfertaCurso(new TipoOfertaCurso(TipoOfertaCurso.REGULAR));
			obj.setTipoCursoStricto(new TipoCursoStricto());
			obj.setUnidadeCoordenacao(null);
			
			tiposStricto = new ArrayList<SelectItem>(0);
			
			obj.setNivel(NivelEnsino.MESTRADO);
			
			carregarTiposStricto(null);
			
		} else {
			
			obj.setNaturezaCurso(new NaturezaCurso(NaturezaCurso.GRADUACAO));
			obj.setNivel(NivelEnsino.GRADUACAO);
			
			checkRole(SigaaPapeis.CDP);
		}
		
		prepareMovimento(SigaaListaComando.CADASTRAR_CURSO_GRADUACAO);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_CURSO_GRADUACAO.getId());
		return forward(getFormPage());
	}

	/**
	 * Atualiza um curso
	 * <br/><br/>
	 * JSP: /sigaa.war/graduacao/curso/lista.jsp
	 */
	@Override
	public String atualizar() {
		
		try {
			
			checkRole(SigaaPapeis.CDP, SigaaPapeis.PPG, SigaaPapeis.GESTOR_DIPLOMAS_GRADUACAO);
			setConfirmButton("Alterar");
			
			GenericDAO dao = getGenericDAO();
			
			setId();
			
			setReadOnly(false);
			
			this.obj = dao.findAndFetch(obj.getId(), Curso.class, "unidade", "unidade2", "unidadeCoordenacao", "areaCurso");
			
			atualizaUnidadeFederativa();
			
			verificaNulos();
			
			if (NivelEnsino.isAlgumNivelStricto(obj.getNivel())) {
				carregarTiposStricto(null);
			}
			
			prepareMovimento(SigaaListaComando.ALTERAR_CURSO_GRADUACAO);
			setOperacaoAtiva(SigaaListaComando.ALTERAR_CURSO_GRADUACAO.getId());
			return forward(getFormPage());
			
		} catch (SegurancaException e) {
			
			notifyError(e);
			addMensagemErro(e.getMessage());
			
			return null;
			
		} catch (Exception e) {
			
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			
			return null;
		}
	}

	/**
	 * Atualiza o campo unidade federativa configurando o objeto do
	 * MunicipioMBean. 
	 */
	private void atualizaUnidadeFederativa() {
		MunicipioMBean municipioMBean = getMBean("municipio");
		municipioMBean.setObj(new Municipio());
		municipioMBean.getObj().setId(obj.getMunicipio().getId());
		municipioMBean.getObj().setUnidadeFederativa(new UnidadeFederativa());
		municipioMBean.getObj().getUnidadeFederativa().setId(obj.getMunicipio().getUnidadeFederativa().getId());
	}

	/**
	 * Visualiza o resumo de um curso
	 * <br/><br/>
	 * JSP:
	 * /sigaa.war/graduacao/curso/lista.jsp
	 * @return
	 */
	public String visualizar() {
		
		try {
			
			GenericDAO dao = getGenericDAO();
			setId();
			
			this.obj = dao.findByPrimaryKey(obj.getId(), Curso.class);
			this.obj.setUnidade(dao.findByPrimaryKey(this.obj.getUnidade().getId(), Unidade.class));
			
			verificaNulos();
			
			if (NivelEnsino.isAlgumNivelStricto(obj.getNivel())) {
				carregarTiposStricto(null);
			}
			
			return forward(getViewPage());
			
		} catch (Exception e) {
			
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			
			return null;
		}
	}

	/**
	 * Ação invocada quando deseja remover um curso
	 * <br/><br/>
	 * JSP: /sigaa.war/graduacao/curso/lista.jsp
	 */
	@Override
	public String preRemover() {
		
		try {
			
			checkRole(SigaaPapeis.CDP, SigaaPapeis.PPG);
			
			String ret = super.preRemover();
			if (isEmpty(obj)) {
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
				return null;
			}
			verificaNulos();
			
			//municipio.obj.unidadeFederativa.id
			MunicipioMBean municipioMBean = getMBean("municipio");
			municipioMBean.getObj().setUnidadeFederativa(obj.getMunicipio().getUnidadeFederativa());
			
			
			if (NivelEnsino.isAlgumNivelStricto(obj.getNivel())) {
				carregarTiposStricto(null);
			}
			
			prepareMovimento(ArqListaComando.REMOVER);
			setOperacaoAtiva(ArqListaComando.REMOVER.getId());
			return ret;
			
		} catch (SegurancaException e) {
			
			notifyError(e);
			addMensagemErro(e.getMessage());
			
			return null;
			
		} catch (Exception e) {
			
			e.printStackTrace();
			addMensagemErroPadrao();
			
			return null;
		}
	}

	/**
	 * Instância os objetos nulos
	 */
	protected void verificaNulos() {
		
		if (obj.getMunicipio() == null) {
			obj.setMunicipio(new Municipio());
		}
		
		if (obj.getNaturezaCurso() == null) {
			obj.setNaturezaCurso(new NaturezaCurso());
		}
		
		if (obj.getModalidadeEducacao() == null) {
			obj.setModalidadeEducacao(new ModalidadeEducacao());
		}
		
		if (obj.getConvenio() == null) {
			obj.setConvenio(new ConvenioAcademico());
		}
			
		if (obj.getAreaCurso() == null) {
			obj.setAreaCurso(new AreaConhecimentoCnpq());
		}
		
		if (obj.getUnidade() == null) {
			obj.setUnidade(new Unidade());
		}
		
		if (obj.getUnidade2() == null) {
			obj.setUnidade2(new Unidade());
		}
		
		if (obj.getTipoOfertaCurso() == null) {
			obj.setTipoOfertaCurso(new TipoOfertaCurso());
		}
		
		if (obj.getUnidadeCoordenacao() == null) {
			obj.setUnidadeCoordenacao(new Unidade());
		}
		
		if (obj.getTipoCursoStricto() == null) {
			obj.setTipoCursoStricto(new TipoCursoStricto());
		}
		
		if (obj.getTipoOfertaDisciplina() == null) {
			obj.setTipoOfertaDisciplina(new TipoOfertaDisciplina());
		}
		
		if (obj.getTipoCicloFormacao() == null) {
			obj.setTipoCicloFormacao(new TipoCicloFormacao());
		}
		
		if (obj.getOrganizacaoAdministrativa() == null) {
			obj.setOrganizacaoAdministrativa(new OrganizacaoAdministrativa());
		}
		
		if (obj.getAreaVestibular() == null) {
			obj.setAreaVestibular(new AreaConhecimentoVestibular());
		}
		
		if (obj.getAreaSesu() == null) {
			obj.setAreaSesu(new AreaSesu());
		}
	}

	/**
	 * Método responsável anular os campos com relacionamento a outros objetos, quando estes possuírem id = 0.
	 */
	protected void preencherNulos() {
		
		if (obj.getMunicipio() != null && obj.getMunicipio().getId() == 0) {
			obj.setMunicipio(null);
		}
		
		if (obj.getNaturezaCurso() != null && obj.getNaturezaCurso().getId() == 0) {
			obj.setNaturezaCurso(null);
		}
		
		if (obj.getModalidadeEducacao() != null && obj.getModalidadeEducacao().getId() == 0) {
			obj.setModalidadeEducacao(null);
		}
		
		if (obj.getConvenio() != null && obj.getConvenio().getId() == 0) {
			obj.setConvenio(null);
		}
		
		if (obj.getAreaCurso() != null && obj.getAreaCurso().getId() == 0) {
			obj.setAreaCurso(null);
		}
		
		if (obj.getUnidade() != null && obj.getUnidade().getId() == 0) {
			obj.setUnidade(null);
		}
		
		if (obj.getUnidade2() != null && obj.getUnidade2().getId() == 0) {
			obj.setUnidade2(null);
		}
		
		if (obj.getUnidadeCoordenacao() != null && obj.getUnidadeCoordenacao().getId() == 0) {
			obj.setUnidadeCoordenacao(null);
		}
		
		if (obj.getAreaVestibular() != null && obj.getAreaVestibular().getId() == 0) {
			obj.setAreaVestibular(null);
		}
		
		if (obj.getTipoOfertaCurso() != null && obj.getTipoOfertaCurso().getId() == 0) {
			obj.setTipoOfertaCurso(null);
		}
		
		if (obj.getTipoCicloFormacao() != null && obj.getTipoCicloFormacao().getId() == 0) {
			obj.setTipoCicloFormacao(null);
		}
		
		if ( isEmpty( obj.getTipoOfertaDisciplina() ) ) {
			obj.setTipoOfertaDisciplina(null);
		}
		
		if ( isEmpty( obj.getOrganizacaoAdministrativa() ) ) {
			obj.setOrganizacaoAdministrativa(null);
		}
		
		if ( isEmpty( obj.getAreaSesu() ) ) {
			obj.setAreaSesu(null);
		}
		
		if (obj.getTipoCursoStricto() != null && obj.getTipoCursoStricto().getId() == 0) {
			obj.setTipoCursoStricto(null);
		}
	}
	
	/**
	 * Método responsável por inserir Instituição de ensino a lista de instituições em rede do curso.
	 * <br>
	 * Método Chamado pela(s) JSP(s):
	 * <ul><li>/sigaa.war/graduacao/curso/form.jsp</li></ul>
	 * @return
	 * @throws DAOException
	 */
	public void adicionarInstituicao(ActionEvent arg) throws DAOException{
		if( instituicaoEnsino.getId() > 0 ){
			InstituicoesEnsinoDao dao = getDAO(InstituicoesEnsinoDao.class);
			instituicaoEnsino = dao.findInstituicaoEnsinoByPK(instituicaoEnsino.getId());
			
			if(instituicoesEnsino.contains(instituicaoEnsino)){
				addMensagemErroAjax("Instituição de Ensino já vinculada ao curso.");
			}else{
				instituicoesEnsino.add(instituicaoEnsino);
				instituicaoEnsino = new InstituicoesEnsino();
			}	
		}
	}
	
	
	/** Remove uma instituição de ensino da lista do curso.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
     *   <li>/sigaa.war/graduacao/curso/form.jsp</li>
     * </ul>  
	 * @param e
	 * @throws ArqException
	 */
	public String removerInstituicao()  throws ArqException {
		int linha = getParameterInt("indice");
		List<InstituicoesEnsino> lista = instituicoesEnsino;
		if (lista.remove(linha) == null) {
			addMensagemErro("Erro ao remover Instituição de Ensino");
			return null;
		}
		
		return null;
	}

	/** Retorna o parâmetro utilizado na busca de curso. 
	 * @return
	 */
	public String getParam() {
		return param;
	}

	/** Seta o parâmetro utilizado na busca de curso.
	 * @param param
	 */
	public void setParam(String param) {
		this.param = param;
	}

	public InstituicoesEnsino getInstituicaoEnsino() {
		return instituicaoEnsino;
	}

	public void setInstituicaoEnsino(InstituicoesEnsino instituicaoEnsino) {
		this.instituicaoEnsino = instituicaoEnsino;
	}

	public List<InstituicoesEnsino> getInstituicoesEnsino() {
		return instituicoesEnsino;
	}

	public void setInstituicoesEnsino(List<InstituicoesEnsino> instituicoesEnsino) {
		this.instituicoesEnsino = instituicoesEnsino;
	}
	
	/**
	 * Retorna o id do arquivo (projeto político-pedagógico), verificando se o
	 * id do arquivo referenciado existe no banco.
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/graduacao/curso/d_view.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Integer getIdArquivo() throws DAOException {
		if (obj.getIdArquivo() == null || isEmpty(EnvioArquivoHelper.recuperaNomeArquivo(obj.getIdArquivo()))) {
			return new Integer(0);
		}
		return obj.getIdArquivo();
	}

	/** Indica se o usuário está no módulo de registro de diplomas.
	 * @return
	 */
	public boolean isModuloDiplomas() {
		return SigaaSubsistemas.REGISTRO_DIPLOMAS.getId() == getSubSistema().getId();
	}
}
