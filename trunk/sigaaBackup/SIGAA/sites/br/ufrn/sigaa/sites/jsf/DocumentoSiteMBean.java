/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 23/10/2008
 *
 */
package br.ufrn.sigaa.sites.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.site.DocumentoSiteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.negocio.MovimentoDocumentoSite;
import br.ufrn.sigaa.sites.dominio.DocumentoSite;
import br.ufrn.sigaa.sites.dominio.TipoDocumentoSite;
import br.ufrn.sigaa.sites.dominio.TipoPortalPublico;

/**
 * Classe Manage Bean respons�vel pela inser��o, altera��o e remo��o de documentos relacionados
 * aos portais p�blicos (unidade e curso).
 * 
 * @author M�rio Rizzi
 */
@Component("documentoSite") @Scope("session")
public class DocumentoSiteMBean extends SigaaAbstractController<DocumentoSite> {

	/** Atributo referente ao arquivo para upload */
	private UploadedFile documento;
	
	/** Atributo utilizado quando o documento pertence a um portal do tipo unidade (Centro, Departamento ou Programa) */
	private Unidade unidade;
	
	/** Atributo utilizado quando o documento pertence a um portal do tipo curso (Gradua��o, Lato e T�cnico) */
	private Curso curso;

	/** Atributo que identifica o tipo de portal p�blico (unidade ou curso). */
	private TipoPortalPublico tipoPortalPublico;
	
	public DocumentoSiteMBean() {
		obj = new DocumentoSite();
		obj.setTipoDocumentoSite(new TipoDocumentoSite());
		obj.setRegistroCadastro(new RegistroEntrada());
		setTipoPortalPublico();
	}
	
	/**
	 * M�todo respons�vel em definir o diret�rio raiz do caso de uso.
	 * M�todo n�o invocado por JSP's.
	 */
	@Override
	public String getDirBase() {
		return "/site/documento_site";
	}
	
	/**
	 * Verifica se o usu�rio possui algum dos pap�is abaixo.
	 * <br/><br/><br/><br/>N�o � chamado por nenhuma jsp.
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
	 * Prepara o formul�rio de cadastro do documento de um portal p�blico do curso.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/site/documento_site/form.jsp</li>
	 * </ul>
	 * 
	 * @throws NegocioException 
	 */
	public String preCadastrarCurso() throws ArqException, NegocioException{
		this.tipoPortalPublico = TipoPortalPublico.CURSO;
		curso = getCurso();
		return preCadastrar();
		
	}
	
	/**
	 * Prepara o formul�rio de cadastro do documento de um portal p�blico do centro.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/site/documento_site/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String preCadastrarCentro() throws ArqException, NegocioException {
		this.tipoPortalPublico = TipoPortalPublico.UNIDADE;
		unidade = getUsuarioLogado().getVinculoAtivo().getUnidade();
		return preCadastrar();
		
	}
	

	/**
	 * Retorna o curso de acordo com id passado por par�metro.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * <li>sigaa.war/site/documento_site/lista.jsp</li>
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
	 * Prepara o formul�rio de cadastro do documento de um portal p�blico do departamento.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/site/documento_site/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String preCadastrarDepartamento() throws ArqException, NegocioException {
		this.tipoPortalPublico = TipoPortalPublico.UNIDADE;
		unidade = getServidorUsuario() == null ? (getUsuarioLogado().getVinculoAtivo().getUnidade()) : getServidorUsuario().getUnidade();
		return preCadastrar();
		
	}
	
	/**
	 * Prepara o formul�rio de cadastro do documento de um portal p�blico do programa.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/site/documento_site/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String preCadastrarPrograma() throws ArqException, NegocioException {
		this.tipoPortalPublico = TipoPortalPublico.UNIDADE;
		unidade = getProgramaStricto();
		return preCadastrar();
		
	}
	
	/**
	 * Prepara o formul�rio de cadastro de documentos em rela��o
	 * ao programa, curso ou departamento.
	 * <br/><br/>N�o � chamado por nenhuma jsp.
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
	
		obj = new DocumentoSite();
		obj.setTipoDocumentoSite(new TipoDocumentoSite());
		obj.setRegistroCadastro(new RegistroEntrada());
		setTipoPortalPublico();
		prepareMovimento(SigaaListaComando.CADASTRAR_DOCUMENTO_SITE);
		return super.preCadastrar();
		
	}
		
	/**
	 * Atualiza os dados de um documento.
	 * <br/><br/>N�o � chamado por nenhuma jsp.
	 */
	@Override
	public String atualizar() throws ArqException {
		
		prepareMovimento(SigaaListaComando.CADASTRAR_DOCUMENTO_SITE);
		return super.atualizar();
		
	}
	

	/**
	 * Cadastra um novo documento
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/site/documento_site/form.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String cadastrar() throws ArqException{

		checkChangeRole();
		
		if( obj.getIdArquivo() == 0 && documento == null )
			addMensagemErro("Selecione um documento");
		
		ListaMensagens listaMensagens = obj.validate();
		if( !isEmpty(listaMensagens.getMensagens()) ){
			addMensagens(listaMensagens);
		}
		
		if( hasErrors() )
			return null;
		
		MovimentoDocumentoSite mov = new MovimentoDocumentoSite();
		
		obj.setUnidade(unidade);
		obj.setCurso(curso);
		
		mov.setDocumentoSite(obj);
		mov.setArquivo( documento );
				
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_DOCUMENTO_SITE);

		try {
			execute(mov);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO," Documento ");
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		}
		
		return listar();
	}

	/**
	 * Remove o documento selecionado
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/site/documento_site/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String remover() throws ArqException {

		Integer id = getParameterInt("id",0);
		obj = getGenericDAO().findByPrimaryKey( id , DocumentoSite.class);
		verificaObjRemovido();
			
		if(isEmpty(obj) && id>0){
			addMensagemErro("Solicita��o j� processada!");
		}else{
			checkChangeRole();
			prepareMovimento(SigaaListaComando.REMOVER_DOCUMENTO_SITE);
					
			MovimentoDocumentoSite mov = new MovimentoDocumentoSite();
			mov.setDocumentoSite(obj);
			mov.setCodMovimento(SigaaListaComando.REMOVER_DOCUMENTO_SITE);
			
			try {
				execute(mov);
				addMensagemInformation("Documento exclu�do com sucesso!");
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				e.printStackTrace();
				return null;
			}
		}
		return listar();
		
	}
	
	 /**  
	 * M�todo respons�vel em listar todos os documento do portal p�blico dos dos cursos.
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/site/documento_site/lista.jsp</li>
	 * </ul>
	 * 
	 * @throws NegocioException 
	 */
	public String listarCurso() throws ArqException, NegocioException{
		this.tipoPortalPublico = TipoPortalPublico.CURSO;
		getCurso();
		return listar();
		
	}
	
	/**
	 * M�todo respons�vel em listar todos os documento do portal p�blico dos centros de ensino.
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/site/documento_site/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String listarCentro() throws ArqException, NegocioException {
		this.tipoPortalPublico = TipoPortalPublico.UNIDADE;
		unidade = getUsuarioLogado().getVinculoAtivo().getUnidade();
		return listar();
		
	}

	/**
	 * M�todo respons�vel em listar todos os documento do portal p�blico dos departamentos
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/site/documento_site/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String listarDepartamento() throws ArqException, NegocioException {
		this.tipoPortalPublico = TipoPortalPublico.UNIDADE;
		unidade = getServidorUsuario() == null ? (getUsuarioLogado().getVinculoAtivo().getUnidade()) : getServidorUsuario().getUnidade();
		return listar();
		
	}
	
	/**
	 * M�todo respons�vel em listar todos os documento do portal p�blico dos programas
	 * <br>
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 	<li>sigaa.war/site/documento_site/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String listarPrograma() throws ArqException, NegocioException {
		
		this.tipoPortalPublico = TipoPortalPublico.UNIDADE;
		unidade = getProgramaStricto();
		return listar();		
		
	}
	
	/**
	 * Retorna todos os documentos de uma unidade ou curso
	 * <br/><br/>N�o � chamado por nenhuma jsp.
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String listar()  throws ArqException {

		checkChangeRole();
		Collection<DocumentoSite> documentos = new  ArrayList<DocumentoSite>();
		documentos = getDAO(DocumentoSiteDao.class).findByUnidadeCurso
		(isEmpty(unidade)?curso.getId():unidade.getId(), this.tipoPortalPublico, null);
		setResultadosBusca(documentos);
		return super.listar();
		
	}

	/** 
	 * Verifica se o objeto manipulado j� foi removido.
	 * @throws DAOException
	 * @throws ArqException
	 */
	private void verificaObjRemovido() throws DAOException, ArqException {
		DocumentoSite objRefresh = getGenericDAO().refresh(obj);
		if (isEmpty(objRefresh))
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA,"");
	}
	
	public UploadedFile getDocumento() {
		return documento;
	}

	public void setDocumento(UploadedFile documento) {
		this.documento = documento;
	}
	
	/**
	 * Identifica o tipo de portal que o usu�rio est� logado.
	 * <br/><br/>N�o � chamado por nenhuma jsp;
	 */
	public void setTipoPortalPublico() {
		if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO,
				SigaaPapeis.SECRETARIA_POS,
				SigaaPapeis.SECRETARIA_DEPARTAMENTO,
				SigaaPapeis.CHEFE_DEPARTAMENTO,
				SigaaPapeis.DIRETOR_CENTRO)) 
			this.tipoPortalPublico = TipoPortalPublico.UNIDADE;
		else if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO,
				SigaaPapeis.SECRETARIA_GRADUACAO,
				SigaaPapeis.SECRETARIA_LATO,
				SigaaPapeis.COORDENADOR_LATO,
				SigaaPapeis.GESTOR_TECNICO)) 
			this.tipoPortalPublico = TipoPortalPublico.CURSO;
	}
	
}
