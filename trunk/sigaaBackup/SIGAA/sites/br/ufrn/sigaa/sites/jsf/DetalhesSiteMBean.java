/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/08/2008
 *
 */
package br.ufrn.sigaa.sites.jsf;


import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.tecnico.dominio.CursoTecnico;
import br.ufrn.sigaa.negocio.MovimentoDetalhesSite;
import br.ufrn.sigaa.parametros.dominio.ParametrosPortalPublico;
import br.ufrn.sigaa.sites.dominio.DetalhesSite;
import br.ufrn.sigaa.sites.dominio.TemplateSite;
import br.ufrn.sigaa.sites.dominio.TipoPortalPublico;

/**
 * Classe Manage Bean respons�vel pelos dados exibidos no portal p�blico
 * de uma unidade ou curso (Centros, Departamentos, Programas e Cursos).
 * 
 * @author M�rio Rizzi
 */
@Component("detalhesSite") @Scope("session")
public class DetalhesSiteMBean extends SigaaAbstractController<DetalhesSite> {

	/** Constante que define a JSP da listagem dos cursos.	 */
	private static final String JSP_LISTA_CURSOS = "/lista_cursos.jsp";
	/** Constante que define a JSP do formul�rio de altera��o das cores do site.	 */
	private static final String JSP_FORM_TEMPLATE = "/form_template.jsf";
	/** Constante que define a JSP do formul�rio de cadastro e altera��o do site.	 */
	private static final String JSP_FORM_SITE = "/form.jsf";
	/** Constante que define a JSP de pr�-visualiza��o da p�gina principal do site.	 */
	private static final String JSP_PREV_PROG_PRINCIPAL = "/public/programa/portal.jsp";
	/** Constante que define a JSP de pr�-visualiza��o da p�gina de apresenta��o do site.	 */
	private static final String JSP_PREV_PROG_APRESENTACAO = "/public/programa/apresentacao.jsp";
		
	/** Atributo referente a imagem do texto de apresenta��o para upload */
	private UploadedFile foto;
	
	/** Atributo referente a imagem da logo para upload */
	private UploadedFile logo;
	
	/** Atributo que identifica qual aba foi selecionada no formul�rio de cadastro */
	private boolean abaTexto;
	
	/** Atributo utilizado quando o portal � do tipo unidade (Centro, Departamento ou Programa) */
	private Unidade unidade;
	
	/** Atributo utilizado quando o portal � do tipo portal do tipo curso (Gradua��o, Lato e T�cnico) */
	private Curso curso;
	
	/** Atributo utilizado quando o portal � do tipo curso (T�cnico). */
	private Collection<Curso> cursos;
	
	/** Indica se deve excluir a foto */
	private boolean excluirFoto = false;
	
	/** Indica se deve excluir a logo */
	private boolean excluirLogo = false;
	
	public DetalhesSiteMBean() throws SegurancaException {
		
		initObj();
		
	}
	
	/**
	 * M�tod que inicia os objetos envolvidos.
	 */
	private void initObj(){
		
		obj = new DetalhesSite();
		setExcluirFoto(false);
		setExcluirLogo(false);
		obj.setTemplateSite(new TemplateSite());
		abaTexto = true;
		
	}
	
	/**
	 * M�todo respons�vel em definir o diret�rio raiz do caso de uso.
	 * M�todo n�o invocado por JSP's. 
	 */
	@Override
	public String getDirBase() {
		return "/site/detalhes_site";
	}
	
	/**
	 * Verifica se o usu�rio possui as devidas permiss�es para acessar
	 * os dados dos portais p�blicos nas opera��es de cadastro, remo��o e atualiza��o.
	 * M�todo n�o invocado por JSP's.
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		
		checkRole(
				SigaaPapeis.CHEFE_DEPARTAMENTO,
				SigaaPapeis.SECRETARIA_DEPARTAMENTO,
				SigaaPapeis.DIRETOR_CENTRO,
				SigaaPapeis.SECRETARIA_POS,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO,
				SigaaPapeis.COORDENADOR_CURSO,
				SigaaPapeis.SECRETARIA_GRADUACAO,
				SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.COORDENADOR_LATO,
				SigaaPapeis.SECRETARIA_LATO,
				SigaaPapeis.GESTOR_TECNICO,
				SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR
		);
		
	}
	
	/**
	 * Criar uma inst�ncia da unidade relacionada ao curso Lato, T�cnico e Gradua��o.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 */
	public String iniciarDetalhesCurso() throws ArqException, NegocioException{
		
		getCurso();
		obj = getGenericDAO().findByExactField(DetalhesSite.class, 
				"curso.id", curso.getId(), true);
		
		
		if(ValidatorUtil.isEmpty(obj)){
			obj = new DetalhesSite();
			obj.setCurso(curso);
			obj.setTemplateSite(new TemplateSite());
			abaTexto = true;
		}else
			obj.setCurso(curso);
		
		return preCadastrar();
		
	}
	
	/**
	 * Retorna o curso de acordo com id passado por par�metro.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * <li>sigaa.war/site/detalhes_site/lista_cursos.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public Curso getCurso() throws ArqException{
		Integer idCurso =  getParameterInt("idCurso");
		if(curso == null){
			if(idCurso != null)
				curso = getGenericDAO().findByPrimaryKey(idCurso, Curso.class);
			else
				curso  = getCursoAtualCoordenacao();
		}	
		return curso;
	}
	
	/**
	 * Cria uma inst�ncia do centro
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException 
	 */
	public String iniciarDetalhesCentro() throws ArqException, NegocioException{
		initObj();
		unidade = getUsuarioLogado().getVinculoAtivo().getUnidade();
		obj = getGenericDAO().findByExactField(DetalhesSite.class, 
				"unidade.id", unidade.getId(), true);
		return preCadastrar();
		
	}
	
	/**
	 * Verifica se o usu�rio tem permiss�o para realizar as opera��es de CRUD 
	 * para as informa��es da apresenta��o, not�cia, documento e outras p�ginas
	 * do portal p�blico dos Centros/Unidades Especializadas 
	 * @return
	 */
	public final boolean isAcessoCentroUnidEspecializada(){
		
		List<Integer> tiposUnidadePermitidos = new ArrayList<Integer>();
		tiposUnidadePermitidos.add(TipoUnidadeAcademica.CENTRO);
		tiposUnidadePermitidos.add(TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA);
		tiposUnidadePermitidos.add(TipoUnidadeAcademica.ESCOLA);
		
		return getUsuarioLogado().getVinculoAtivo().getUnidade().isUnidadeAcademica() &&
			tiposUnidadePermitidos.contains( 
					getUsuarioLogado().getVinculoAtivo().getUnidade().getTipoAcademica() );
	}
	
	/**
	 * Cria uma inst�ncia do departamento
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String iniciarDetalhesDepartamento() throws ArqException, NegocioException{
		initObj();
		unidade = getServidorUsuario() == null ? (getUsuarioLogado().getVinculoAtivo().getUnidade()) : getServidorUsuario().getUnidade();
		obj = getGenericDAO().findByExactField(DetalhesSite.class, 
				"unidade.id", unidade.getId(), true);
		return preCadastrar();
		
	}
	
	/**
	 * Utilizado exclusivamente para situa��o do portal dos cursos t�cnico.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String listarCursosTecnico() throws ArqException{
		
		if(cursos == null)
			cursos = getDAO(CursoDao.class).findAll(getUsuarioLogado().getVinculoAtivo().getUnidade().getId(),
				 	getNivelEnsino(), CursoTecnico.class, null) ; 
		return forward(getDirBase()+JSP_LISTA_CURSOS);
		
	}
	
	/**
	 * Redireciona para o formul�rio de detalhes do site
	 * M�todo n�o invocado por JSP's. 
	 */
	public String preCadastrar() throws ArqException, NegocioException{
		
		if(ValidatorUtil.isEmpty(obj)){
			obj = new DetalhesSite();
			obj.setUnidade(unidade);
			obj.setCurso(curso);
			setConfirmButton("Cadastrar");
		}else
			setConfirmButton("Alterar");
		
		prepareMovimento(SigaaListaComando.ATUALIZAR_DETALHES_SITE);
		
		return forward(getFormPage());
		
	}
	
	/**
	 * Cria uma inst�ncia do programa
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 * 
	 * @throws ArqException 
	 */
	private void iniciarDadosPrograma() throws ArqException{
		
		initObj();
		unidade  = getProgramaStricto();
		obj = getGenericDAO().findByExactField(DetalhesSite.class, "unidade.id", unidade.getId(), true);
		setConfirmButton("Alterar");
		
		if(!ValidatorUtil.isEmpty(obj))
			obj.setUnidade(unidade);
		//A��o de cadastro
		if(obj == null){
			setConfirmButton("Cadastrar");
			obj = new DetalhesSite();
			obj.setUnidade(unidade);
			obj.setTemplateSite(new TemplateSite());
		}
		
		prepareMovimento(SigaaListaComando.ATUALIZAR_DETALHES_SITE);
		setOperacaoAtiva(SigaaListaComando.ATUALIZAR_DETALHES_SITE.getId());
	}
	
	/**
	 * Redireciona para p�gina dos detalhes do programa
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 * 
	 * @throws ArqException 
	 */
	public String iniciarDetalhesPrograma() throws ArqException{
		
		iniciarDadosPrograma();
		return forward(getDirBase() + JSP_FORM_SITE);
		
	}
	
	/**
	 * Visualizar os dados da principal, ainda n�o alterados, 
	 * no portal p�blico dos programas. 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/site/detalhes_site/form_template.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String previewPrincipal() throws ArqException{
		
		popularPreviewPrograma();
		return forward(JSP_PREV_PROG_PRINCIPAL);
		
	}
	
	/**
	 * Visualizar os dados da apresenta��o, ainda n�o alterados, 
	 * no portal p�blico dos programas. 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/site/detalhes_site/form_template.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String previewApresentacao() throws ArqException{
		
		popularPreviewPrograma();
		return forward(JSP_PREV_PROG_APRESENTACAO);
		
	}
	
	/**
	 * Popula as informa��es necess�rias para visualiza��o
	 * do portal p�blico dos programas.
	 */
	private void popularPreviewPrograma(){
		
		PortalPublicoProgramaMBean ppProgMBean = getMBean("portalPublicoPrograma");
		ppProgMBean.setUnidade(unidade);
		ppProgMBean.setTipoPortalPublico(TipoPortalPublico.UNIDADE);
		ppProgMBean.setLc(ParametroHelper.getInstance().getParametro(ParametrosPortalPublico.LC_DEFAULT));
		ppProgMBean.setPreVisualizar(true);
		ppProgMBean.setDetalhesSite(obj);
		
	}
	
	/**
	 * Redireciona para p�gina do template do programa
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/site/detalhes_site/menu_docente.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 * 
	 * @throws ArqException 
	 */
	public String iniciarTemplateSite() throws ArqException{
		
		if( !isEmpty(obj.getUnidade()) )
			iniciarDadosPrograma();		
		if( !isEmpty(obj.getCurso()) )
			iniciarDadosPrograma();
		if(obj.getTemplateSite()==null)
			obj.setTemplateSite(new TemplateSite());
		
		return forward(getDirBase() + JSP_FORM_TEMPLATE);
		
	}
	
	
	
	/**
	 * Retorna a p�gina p�blica
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/site/detalhes_site/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String verPortalPublicoPrograma(){
		String urlPublica = "";
		//Se � unidade (Departamento, Programa ou Centro)
		
			PortalPublicoProgramaMBean portalPublicoProg = 
				getMBean("portalPublicoPrograma");

			portalPublicoProg.setUnidade(obj.getUnidade());
			portalPublicoProg.setCurso(obj.getCurso());
			portalPublicoProg.setDetalhesSite(obj);
			portalPublicoProg.setTipoPortalPublico(TipoPortalPublico.UNIDADE);
			urlPublica = "/public/programa/portal.jsp?lc=pt_BR";
	
		
		return forward(urlPublica);
		
	}
	
	/**
	 * Remove uma se��o extra
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/site/detalhes_site/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	@Override
	public String cadastrar() throws ArqException{

		checkChangeRole();
		
		MovimentoDetalhesSite mov = new MovimentoDetalhesSite();
		mov.setDetalhesSite(obj);
		
		if(logo != null){
			if(!validaExtensao(logo.getContentType())){
				addMensagemErro("Logo n�o cadastrada. Por favor selecione um tipo v�lido!");
			}
			excluirLogo = true;
			mov.setLogo(logo);
		}
		if(foto != null){ 
			if(!validaExtensao(foto.getContentType())){
				addMensagemErro("Foto n�o cadatrada. Por favor selecione um tipo v�lido!");
			}
			excluirFoto = true;
			mov.setFoto(foto);
		}
		mov.setExcluirLogo(excluirLogo);		
		mov.setExcluirFoto(excluirFoto);
		mov.setCodMovimento(SigaaListaComando.ATUALIZAR_DETALHES_SITE);
		
		try {
			execute(mov);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO," Destalhes do Portal P�blico ");
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		}

		return cancelar();
	}
	
	/**
	 * Popula as cores padr�es dos sites.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/site/detalhes_site/form_template.jsp</li>
	 * </ul>
	 * 
	 */
	public String templatePadrao(){
		obj.getTemplateSite().resetTemplate();
		return redirectMesmaPagina();
	}
	
	/**
	 * Remove fotos relacionadas a uma unidade ou curso
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/site/detalhes_site/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String removerArquivo() throws ArqException {
		
		prepareMovimento(SigaaListaComando.REMOVER_ARQUIVO_DETALHES_SITE);
		checkChangeRole();
		MovimentoDetalhesSite mov = new MovimentoDetalhesSite();
		mov.setDetalhesSite(obj);
		
		try {
			execute(mov);
			addMensagemInformation("Arquivo do detalhe da unidade exclu�do com sucesso!");
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		}
		
		return forward(getFormPage());
		
	}
		
	/**
	 * Verifica se o tipo do arquivo enviado � v�lido
	 * N�o � chamado por jsp.
	 * 
	 * @author Mario Rizzi
	 * @param tipoArquivo
	 * @return
	 */
	protected boolean validaExtensao(String tipoArquivo){
		
		ArrayList<String> tipos = new ArrayList<String>();
		tipos.add("image/pjpeg");
		tipos.add("image/jpeg");
		tipos.add("image/gif"); 
		tipos.add("image/png");
		if(tipos.indexOf(tipoArquivo)==-1)
			return false;
		else 
			return true;
		
	}	
	
	/**
	 * Prepara o formul�rio para edi��o dos par�metros
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String carregarParametros() throws ArqException{
		
		unidade  = getProgramaStricto();
		obj = getGenericDAO().findByExactField(DetalhesSite.class, "unidade.id", unidade.getId(), true);
		if(obj == null){
			obj = new DetalhesSite();
			obj.setUnidade(unidade);
		}
		return forward("/site/detalhes_site/form_parametro.jsp");
		
	}
	
	public UploadedFile getFoto() {
		return foto;
	}

	public void setFoto(UploadedFile foto) {
		this.foto = foto;
	}

	public UploadedFile getLogo() {
		return logo;
	}

	public void setLogo(UploadedFile logo) {
		this.logo = logo;
	}
	
	public boolean isAbaTexto() {
		return abaTexto;
	}
	
	public void setAbaTexto(boolean abaTexto) {
		this.abaTexto = abaTexto;
	}

	public Collection<Curso> getCursos() {
		return cursos;
	}

	public void setCursos(Collection<Curso> cursos) {
		this.cursos = cursos;
	}
	
	/**
	 * Retorna a URL de acordo com o tipo acad�mico da unidade vinculada ao portal
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/site/detalhes_site/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public String getUrlOficial(){

		if(unidade != null && unidade.getId()>0){
			Integer tipoUnidadeAcamdemica = null;
			tipoUnidadeAcamdemica = unidade.getTipoAcademica();
			switch(tipoUnidadeAcamdemica){
				case TipoUnidadeAcademica.PROGRAMA_POS: return ParametroHelper.getInstance().getParametro(ParametrosPortalPublico.URL_PROGRAMA);
				case TipoUnidadeAcademica.DEPARTAMENTO: return ParametroHelper.getInstance().getParametro(ParametrosPortalPublico.URL_DEPTO);
			}
		}
		else if(curso !=null && curso.getId()>0)
			return ParametroHelper.getInstance().getParametro(ParametrosPortalPublico.URL_CURSO);
		
		return "";
		
	}

	/**
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/site/detalhes_site/form.jsp</li>
	 * </ul>
	 */
	public boolean isExcluirFoto() {
		return excluirFoto;
	}

	public void setExcluirFoto(boolean excluirFoto) {
		this.excluirFoto = excluirFoto;
	}

	/**
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/site/detalhes_site/form.jsp</li>
	 */
	public boolean isExcluirLogo() {
		return excluirLogo;
	}

	public void setExcluirLogo(boolean excluirLogo) {
		this.excluirLogo = excluirLogo;
	}
	
	/**
	 * Seta para exclus�o da foto quando for alterar os dados da apresenta��o.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/site/detalhes_site/form.jsp</li>
	 * </ul>
	 * @param e
	 */
	public void excluirFoto(ActionEvent e){
		setExcluirFoto(true);
	}

	/**
	 * Seta para exclus�o da logo quando for alterar os dados da apresenta��o.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/site/detalhes_site/form.jsp</li>
	 * </ul>
	 * @param e
	 */
	public void excluirLogo(ActionEvent e){
		setExcluirLogo(true);
	}
	
}
