/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/11/2008
 *
 */
package br.ufrn.sigaa.sites.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.Iterator;

import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.site.NoticiaSiteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.negocio.MovimentoNoticiaSite;
import br.ufrn.sigaa.sites.dominio.NoticiaSite;
import br.ufrn.sigaa.sites.dominio.TipoPortalPublico;

/**
 * Classe Manage Bean respons�vel pelas not�cias do portal p�blico dos 
 * programas de p�s-gradua��o
 * 
 * @author M�rio Rizzi
 */
@Component("noticiaSite") @Scope("session")
public class NoticiaSiteMBean extends SigaaAbstractController<NoticiaSite> {

	/** Atributo referente ao arquivo de anexo da noticia para upload */
	private UploadedFile anexo;
	
	/** Atributo referente a imagem da noticia para upload */
	private UploadedFile foto;
	
	/** Atributo que identifica o tipo de portal p�blico (unidade ou curso). */
	private TipoPortalPublico tipoPortalPublico;
	
	/** Atributo utilizado quando a not�cia pertence a um portal do tipo unidade (Centro, Departamento ou Programa) */
	private Unidade unidade;
	
	/** Atributo utilizado quando a not�cia pertence a um portal do tipo curso (Gradua��o, Lato e T�cnico) */
	private Curso curso;
	
	private String urlPublica;
	
	private SelectItem[] linguagens = new SelectItem[] { 
			new SelectItem("pt_BR", "Portugu�s (Padr�o)"),
			new SelectItem("en_US", "Ingl�s")/*,
			new SelectItem("es_ES", "Espanhol"),
			new SelectItem("fr_FR", "Franc�s")*/
	};

	public NoticiaSiteMBean() {
		initObj();
	}

	private void initObj() {
		obj = new NoticiaSite();
		curso = null;
		unidade = null;
		setTipoPortalPublico();
	}
	
	@Override
	public String getDirBase() {
		return "/site/noticias_site";
	}
	
	/**
	 * Verifica se o usu�rio possui as devidas permiss�es
	 * N�o � chamado por nenhuma jsp.
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
				SigaaPapeis.COORDENADOR_LATO,
				SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.SECRETARIA_LATO,
				SigaaPapeis.GESTOR_TECNICO, 
				SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR
		);
	}
	
	/**
	 * Prepara o formul�rio de cadastro, setando o curso ao que o usu�rio pertence.
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/noticia_site/form.jsp</li>
	 * </ul>
	 * 
	 * @throws NegocioException 
	 */
	public String preCadastrarCurso() throws ArqException, NegocioException{
		this.tipoPortalPublico = TipoPortalPublico.CURSO;
		setUrlPublica("/sigaa/public/curso/noticias_desc.jsf?lc=pt&id="+getCurso().getId());
		return preCadastrar();
		
	}
	
	/**
	 * Prepara o formul�rio de cadastro, setando a unidade(centro) a que o usu�rio pertence.
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/noticia_site/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String preCadastrarCentro() throws ArqException, NegocioException {
		this.tipoPortalPublico = TipoPortalPublico.UNIDADE;
		unidade = getUsuarioLogado().getVinculoAtivo().getUnidade();
		setUrlPublica("/sigaa/public/centro/noticias_desc.jsf?lc=pt&id="+unidade.getId());
		return preCadastrar();
		
	}

	/**
	 * Prepara o formul�rio de cadastro, setando a unidade(departamento) a que o usu�rio pertence.
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/noticia_site/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String preCadastrarDepartamento() throws ArqException, NegocioException {
		this.tipoPortalPublico = TipoPortalPublico.UNIDADE;
		unidade = getServidorUsuario() == null ? (getUsuarioLogado().getVinculoAtivo().getUnidade()) : getServidorUsuario().getUnidade();
		setUrlPublica("/sigaa/public/departamento/noticias_desc.jsf?lc=pt&id="+unidade.getId());
		return preCadastrar();
		
	}
	
	/**
	 * Prepara o formul�rio de cadastro, setando a unidade(programa) a que o usu�rio pertence.
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/noticia_site/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String preCadastrarPrograma() throws ArqException, NegocioException {
		this.tipoPortalPublico = TipoPortalPublico.UNIDADE;
		unidade = getProgramaStricto();
		setUrlPublica("/sigaa/public/programa/noticias_desc.jsf?lc=pt&id="+unidade.getId());
		return preCadastrar();
		
	}
	
	/**
	 * N�o � chamada por JSP. Utilizado somente nos m�todos internos.
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		obj = new NoticiaSite();
		prepareMovimento(SigaaListaComando.ATUALIZAR_NOTICIA_SITE);
		return super.preCadastrar();
	}
	
	/**
	 * Prepara o formul�rio para exibi��o dos dados.
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/noticia_site/form.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String atualizar() throws ArqException {
		
		prepareMovimento(SigaaListaComando.ATUALIZAR_NOTICIA_SITE);
		return super.atualizar();
		
	}
	
	/**
	 * Verifica antes de cadastrar o papel do usu�rio
	 * N�o � chamado por nenhuma jsp.
	 */
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException,
			SegurancaException, DAOException {
		if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO,
				SigaaPapeis.SECRETARIA_POS)) {
			obj.setUnidade(getProgramaStricto());
		} else if (isUserInRole(SigaaPapeis.SECRETARIA_DEPARTAMENTO,
				SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.DIRETOR_CENTRO)) {
			obj.setUnidade(getUsuarioLogado().getVinculoAtivo().getUnidade());
		}
		super.beforeCadastrarAndValidate();
	}
	
	/**
	 * Prepara formul�rio para remo��o da not�cia
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/noticias_site/lista.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String preRemover() {
		try {
			prepareMovimento(SigaaListaComando.ATUALIZAR_NOTICIA_SITE);
		} catch (ArqException e) {
			e.printStackTrace();
		}
		return super.preRemover();
	}


	/**
	 * Cadastra os dados de uma not�cia como n�o publicada para pr�-visualiza��o
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/noticia_site/form.jsp</li>
	 * </ul>
	 * 
	 */
	public String verNoticia() throws ArqException{
		
		Integer id = getParameterInt("id");
		obj = getGenericDAO().findByPrimaryKey(id,NoticiaSite.class);
		return redirectJSF(getViewPage()+"&noticia="+obj.getId());
	}

	/**
	 * Retorna a URL para pr�-visualiza��o da not�cia.
	 * JSP's chamadas: nenhuma.
	 */
	@Override
	public String getViewPage() {
		return urlPublica;
	}
	
	/**
	 * Popula os dados necess�rios antes de inserir ou alterar a not�cia.
	 * JSP's chamadas: nenhuma. 
	 * @return
	 * @throws ArqException
	 */
	public String cadastrar() throws ArqException{

		//Popula curso ou unidade dependendo do portal
		obj.setCurso(curso);
		obj.setUnidade(unidade);

		//Seta o movimento 
		MovimentoNoticiaSite mov = new MovimentoNoticiaSite();
		mov.setNoticiaSite(obj);
		mov.setCodMovimento(SigaaListaComando.ATUALIZAR_NOTICIA_SITE);		

		//Verifica se a extens�o da foto se � v�lida caso exista.
		if(foto != null && !obj.validaExtensao(foto.getContentType())){
			addMensagem(MensagensArquitetura.FORMATO_INVALIDO, "Foto");
		}else mov.setFoto(foto);

		//Verifica se existem erros
		erros = new ListaMensagens();
		erros.addAll(obj.validate().getMensagens());
		if (hasErrors())
			return null;
	
		//Seta o arquivo se existir
		mov.setArquivo(this.anexo);

		try {
			execute(mov);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO," Not�cia ");
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		}
		return listar();
	}
	
	
	/**
	 * Remove arquivo relacionado a not�cia
	 * N�o � chamado por nenhuma JSP.
	 */
	public String remover() throws ArqException {

		Integer id = getParameterInt("id",0);
		
		obj = getGenericDAO().findByPrimaryKey( id , NoticiaSite.class);
			
		if(isEmpty(obj) && id>0){
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
		}else{
			checkChangeRole();
			prepareMovimento(SigaaListaComando.REMOVER_ARQUIVO_NOTICIA_SITE);
			
			MovimentoNoticiaSite mov = new MovimentoNoticiaSite();
			mov.setNoticiaSite(obj);
			mov.setCodMovimento(SigaaListaComando.REMOVER_ARQUIVO_NOTICIA_SITE);
	
			try {
				execute(mov);
				addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO,"Not�cia");
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				e.printStackTrace();
				return null;
			}
		}		
		return listar();

	}
	
	 /**
	 *  Prepara a listagem somente das not�cias a que o usu�rio logado pertence.
	 *  
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/noticias_site/lista.jsp</li>
	 * </ul>
	 *  
	 * @throws NegocioException 
	 */
	public String listarCurso() throws ArqException, NegocioException{
		this.tipoPortalPublico = TipoPortalPublico.CURSO;
		setUrlPublica("/sigaa/public/curso/noticias_desc.jsf?lc=pt&id="+getCurso().getId());
		return listar();
		
	}
	
	/**
	 * Prepara a listagem somente das not�cias a que o usu�rio logado pertence.
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/noticias_site/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String listarCentro() throws ArqException, NegocioException {
		this.tipoPortalPublico = TipoPortalPublico.UNIDADE;
		unidade = getUsuarioLogado().getVinculoAtivo().getUnidade();
		setUrlPublica("/sigaa/public/centro/noticias_desc.jsf?lc=pt&id="+unidade.getId());
		return listar();
		
	}

	/**
	 * Prepara a listagem somente das not�cias a que o usu�rio logado pertence.
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/noticias_site/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String listarDepartamento() throws ArqException, NegocioException {
		this.tipoPortalPublico = TipoPortalPublico.UNIDADE;
		unidade = getServidorUsuario() == null ? (getUsuarioLogado().getVinculoAtivo().getUnidade()) : getServidorUsuario().getUnidade();
		setUrlPublica("/sigaa/public/departamento/noticias_desc.jsf?lc=pt&id="+unidade.getId());
		return listar();
		
	}
	
	/**
	 * Prepara a listagem somente das not�cias a que o usu�rio logado pertence.
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/noticias_site/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String listarPrograma() throws ArqException, NegocioException {
		this.tipoPortalPublico = TipoPortalPublico.UNIDADE;
		unidade = getProgramaStricto();
		setUrlPublica("/sigaa/public/programa/noticias_desc.jsf?lc=pt&id="+unidade.getId());
		return listar();		
		
	}
	
	/**
	 * Lista not�cias
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/noticias_site/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	@Override
	public String listar() throws ArqException{

		checkChangeRole();
		Collection<NoticiaSite> noticiasUnidade = getDAO(NoticiaSiteDao.class).
					findByIdioma(isEmpty(unidade)?curso.getId():unidade.getId(),
							this.tipoPortalPublico,null, null, null);
		for (Iterator<NoticiaSite> iterator = noticiasUnidade.iterator(); iterator
				.hasNext();) {
			NoticiaSite noticiaSite = iterator.next();
			noticiaSite.setTituloResumido(StringUtils.limitTxt(
					noticiaSite.getTitulo(), 80));
		}
		setResultadosBusca(noticiasUnidade);
		return super.listar();
	}
	
	public UploadedFile getAnexo() {
		return anexo;
	}

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
	
	public void setAnexo(UploadedFile anexo) {
		this.anexo = anexo;
	}
	
	public UploadedFile getFoto() {
		return foto;
	}

	public void setFoto(UploadedFile foto) {
		this.foto = foto;
	}
	
	public SelectItem[] getLinguagens() {
		return linguagens;
	}
	
	public void setTipoPortalPublico() {
		if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO,
				SigaaPapeis.SECRETARIA_POS,
				SigaaPapeis.SECRETARIA_DEPARTAMENTO,
				SigaaPapeis.CHEFE_DEPARTAMENTO,
				SigaaPapeis.DIRETOR_CENTRO)) 
			this.tipoPortalPublico = TipoPortalPublico.UNIDADE;
		else if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO,
				SigaaPapeis.SECRETARIA_GRADUACAO,
				SigaaPapeis.COORDENADOR_LATO,
				SigaaPapeis.GESTOR_TECNICO)) 
			this.tipoPortalPublico = TipoPortalPublico.CURSO;
	}

	public String getUrlPublica() {
		return urlPublica;
	}

	public void setUrlPublica(String urlPublica) {
		this.urlPublica = urlPublica;
	}

}
