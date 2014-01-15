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
import br.ufrn.sigaa.arq.dao.site.SecaoExtraSiteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.negocio.MovimentoSecaoExtraSite;
import br.ufrn.sigaa.sites.dominio.SecaoExtraSite;
import br.ufrn.sigaa.sites.dominio.TipoPortalPublico;

/**
 * MBean respons�vel por carregar as sess�es de p�ginas cadastradas para as
 * diferentes unidades(departamentos, programas e centro) e cursos(gradua��o, lato e t�cnico)
 * 
 * @author M�rio Rizzi
 */
@Component("secaoExtraSite") @Scope("session")
public class SecaoExtraSiteMBean extends SigaaAbstractController<SecaoExtraSite> {

	private TipoPortalPublico tipoPortalPublico;
	
	private Unidade unidade;
	
	private Curso curso;
	
	private String urlPublica;
	
	private SelectItem[] linguagens = new SelectItem[] { 
			new SelectItem("pt_BR", "Portugu�s (Padr�o)"),
			new SelectItem("en_US", "Ingl�s")/*,
			new SelectItem("es_ES", "Espanhol"),
			new SelectItem("fr_FR", "Franc�s")*/
	};
	
	public SecaoExtraSiteMBean() {
		obj = new SecaoExtraSite();
		setTipoPortalPublico();
	}

	/**
	 * Verifica se o usu�rio possui algum dos pap�is abaixo.
	 * N�o � chamado por nenhuma JSP.
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
				SigaaPapeis.SECRETARIA_LATO,
				SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.GESTOR_TECNICO,
				SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR
		);
	}

	@Override
	public String getDirBase() {
		return "/site/secao_extra";
	}

	@Override
	public String cancelar() {
		return super.cancelar();
	}

	/**
	 * Cadastra os dados de uma not�cia como n�o publicada para pr�-visualiza��o
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/noticias_site/form.jsp</li>
	 * </ul>
	 * 
	 */
	public String verSecao() throws ArqException{
		
		Integer id = getParameterInt("id");
		obj = getGenericDAO().findByPrimaryKey(id,SecaoExtraSite.class);
		return redirectJSF(getViewPage()+"&extra="+obj.getId());
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
	 * Cadastrar os dados de uma se��o extra (conte�do extra)
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/secao_extra_site/form.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String cadastrar() throws ArqException {
		checkChangeRole();
		MovimentoSecaoExtraSite mov = new MovimentoSecaoExtraSite();
		
		obj.setCurso(curso);
		obj.setUnidade(unidade);
		
		mov.setSecaoExtraSite(obj);
		mov.setCodMovimento(SigaaListaComando.ATUALIZAR_SECAO_EXTRA_SITE);		


		ListaMensagens listaMensagens = obj.validate();
		if (!isEmpty(listaMensagens.getMensagens())) {
			addMensagens(listaMensagens);
		}
		if (hasErrors())
			return null;



		try {
			execute(mov);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO," Se��o Extra ");
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		}

		return listar();
		
	}
	
	/**
	 * Cadastrar os dados de uma se��o extra (conte�do extra)
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/secao_extra_site/form.jsp</li>
	 * </ul>
	 * 
	 */
	public String preVisualizar() throws ArqException {
		checkChangeRole();
		String msgConfirmacao = "";
		MovimentoSecaoExtraSite mov = new MovimentoSecaoExtraSite();
		
		obj.setCurso(curso);
		obj.setUnidade(unidade);
		
		mov.setSecaoExtraSite(obj);
		mov.setCodMovimento(SigaaListaComando.ATUALIZAR_SECAO_EXTRA_SITE);		
		
	
		obj.setPublicada(false);
			
		ListaMensagens listaMensagens = obj.validate();
		if (!isEmpty(listaMensagens.getMensagens())) {
			addMensagens(listaMensagens);
		}
		
		if (hasErrors())
			return null;

		try {
			execute(mov);
			addMensagemInformation(msgConfirmacao);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		}
		
		removeOperacaoAtiva();
		prepareMovimento(SigaaListaComando.ATUALIZAR_SECAO_EXTRA_SITE);
		return redirectJSF("/sigaa/public/curso/secao_extra.jsf?lc=pt&id="+obj.getCurso().getId()+"&extra="+obj.getId());
		
	}
	
	/**
	 * Prepara o formul�rio de cadastro de se��o do portal p�blico dos cursos
	 * para visualiza��o e inser��o dos dados.
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/secao_extra/form.jsp</li>
	 * </ul>
	 * 
	 * @throws NegocioException 
	 */
	public String preCadastrarCurso() throws ArqException, NegocioException{
		this.tipoPortalPublico = TipoPortalPublico.CURSO;
		setUrlPublica("/sigaa/public/curso/secao_extra.jsf?lc=pt&id="+getCurso().getId());
		return preCadastrar();
		
	}
	
	/**
	 * Prepara o formul�rio de cadastro de se��o do portal p�blico dos centros
	 * para visualiza��o e inser��o dos dados.
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/secao_extra/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String preCadastrarCentro() throws ArqException, NegocioException {
		this.tipoPortalPublico = TipoPortalPublico.UNIDADE;
		unidade =  getUsuarioLogado().getVinculoAtivo().getUnidade();
		setUrlPublica("/sigaa/public/centro/secao_extra.jsf?lc=pt&id="+	unidade.getId());
		return preCadastrar();
		
	}

	/**
	 * Prepara o formul�rio de cadastro de se��o do portal p�blico dos departamentos
	 * para visualiza��o e inser��o dos dados.
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/secao_extra/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String preCadastrarDepartamento() throws ArqException, NegocioException {
		this.tipoPortalPublico = TipoPortalPublico.UNIDADE;
		unidade = getServidorUsuario() == null ? (getUsuarioLogado().getVinculoAtivo().getUnidade()) : getServidorUsuario().getUnidade();
		setUrlPublica("/sigaa/public/departamento/secao_extra.jsf?lc=pt&id="+unidade.getId());
		return preCadastrar();
		
	}
	
	/**
	 * Prepara o formul�rio de cadastro de se��o do portal p�blico dos programas
	 * para visualiza��o e inser��o dos dados.
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/secao_extra/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String preCadastrarPrograma() throws ArqException, NegocioException {
		this.tipoPortalPublico = TipoPortalPublico.UNIDADE;
		unidade =  getProgramaStricto();
		setUrlPublica("/sigaa/public/programa/secao_extra.jsf?lc=pt&id="+unidade.getId());
		return preCadastrar();		
		
	}
	
	/**
	 * Prepara o formul�rio de cadastro.
	 *  N�o � chamado por nenhuma JSP.
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		obj = new SecaoExtraSite();
		obj.setPublicada(true);
		obj.setIsLinkExterno(false);
		prepareMovimento(SigaaListaComando.ATUALIZAR_SECAO_EXTRA_SITE);
		return super.preCadastrar();
		
	}
		
	/**
	 *  Atualiza os dados da se��o extra dos portais p�blicos.
	 *  N�o � chamado por nenhuma JSP.
	 */
	@Override
	public String atualizar() throws ArqException {
		
		prepareMovimento(SigaaListaComando.ATUALIZAR_SECAO_EXTRA_SITE);
		return super.atualizar();
		
	}
		
	/**
	 *  Seta os atributos descri��o e linkExterno antes de cadastrar os dados 
	 *  da se��o dos portais p�blicos. 
	 *  N�o � chamado por nenhuma JSP.
	 */
	@Override
	public void beforeCadastrarAfterValidate() throws NegocioException,
			SegurancaException, DAOException {
		if(obj.getIsLinkExterno()){
			obj.setDescricao(null);
		}else
			obj.setLinkExterno(null);
		
		if(!isEmpty(obj.getUnidade()) && obj.getUnidade().getId()>0)
			obj.setCurso(null);
		else
			obj.setUnidade(null);
		
		super.beforeCadastrarAfterValidate();

	}
	
	/**
	 *  Reinicializa o estado dos atributos publicada, linkExterno e descri��o
	 *  ap�s a atualiza��o dos dados.
	 *  N�o � chamado por nenhuma JSP.
	 */
	@Override
	public void afterAtualizar() throws ArqException {
		obj.setPublicada(true);
		if(isEmpty(obj.getLinkExterno())){
			obj.setIsLinkExterno(false);	
			obj.setLinkExterno(null);
		}else{
			obj.setIsLinkExterno(true);
			obj.setDescricao(null);
		}
	}

	/**
	 * Remove uma se��o extra de um portal p�blico.
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/secao_extra/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	@Override
	public String remover() throws ArqException {
		Integer id = getParameterInt("id",0);
		
		obj = getGenericDAO().findByPrimaryKey( id , SecaoExtraSite.class);
			
		if(isEmpty(obj) && id>0){
			addMensagemErro("Solicita��o j� processada!");
		}else{
			checkChangeRole();
			prepareMovimento(SigaaListaComando.REMOVER_SECAO_EXTRA_SITE);
			
			MovimentoSecaoExtraSite mov = new MovimentoSecaoExtraSite();
			mov.setSecaoExtraSite(obj);
			mov.setCodMovimento(SigaaListaComando.REMOVER_SECAO_EXTRA_SITE);
	
			try {
				execute(mov);
				addMensagemInformation("Se��o Extra exclu�da com sucesso!");
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				e.printStackTrace();
				return null;
			}
		}		
		return listar();
	}
	
	@Override
	public void afterRemover() {
		try {
			setResultadosBusca(getAllSecaoExtra());
		} catch (ArqException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String forwardCadastrar() {
		
		return getListPage();
		
	}
	
	@Override
	protected void afterCadastrar() throws ArqException {
		
	}

	 /**  
	  * Lista todas as se��es do portal p�blico dos cursos(gradua��o, lato e t�cnico). 
	  * 
	  * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/secao_extra/lista.jsp</li>
	 * </ul>
	  * 
	  * @throws NegocioException 
	 */
	public String listarCurso() throws ArqException, NegocioException{
		this.tipoPortalPublico = TipoPortalPublico.CURSO;
		setUrlPublica("/sigaa/public/curso/secao_extra.jsf?lc=pt&id="+getCurso().getId());
		return listar();
		
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
	
	/**
	 *  Lista todas as se��es do portal p�blico dos centros.
	 *  
	 *  <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/secao_extra/lista.jsp</li>
	 * </ul>
	 *  
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String listarCentro() throws ArqException, NegocioException {
		this.tipoPortalPublico = TipoPortalPublico.UNIDADE;
		unidade =  getUsuarioLogado().getVinculoAtivo().getUnidade();
		setUrlPublica("/sigaa/public/centro/secao_extra.jsf?lc=pt&id="+unidade.getId());
		return listar();
		
	}

	/**
	 * Lista todas as se��es do portal p�blico dos departamentos.
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/secao_extra/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String listarDepartamento() throws ArqException, NegocioException {
		this.tipoPortalPublico = TipoPortalPublico.UNIDADE;
		unidade = getServidorUsuario() == null ? (getUsuarioLogado().getVinculoAtivo().getUnidade()) : getServidorUsuario().getUnidade();
		setUrlPublica("/sigaa/public/departamento/secao_extra.jsf?lc=pt&id="+unidade.getId());
		return listar();
		
	}
	
	/**
	 * Lista todas as se��es do portal p�blico dos programas de p�s-gradua��o
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/secao_extra/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String listarPrograma() throws ArqException, NegocioException {
		this.tipoPortalPublico = TipoPortalPublico.UNIDADE;
		unidade = getProgramaStricto();
		setUrlPublica("/sigaa/public/programa/secao_extra.jsf?lc=pt&id="+unidade.getId());
		return listar();		
		
	}
	
	/**
	 * Retorna uma cole��o de todos os conte�do de uma unidade
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/site/secao_extra/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	@Override
	public String listar() throws ArqException {
		
		setResultadosBusca(getAllSecaoExtra());
		return super.listar();
	
	}
	
	public Collection<SecaoExtraSite> getAllSecaoExtra() throws ArqException {
		
		checkChangeRole();
		Collection<SecaoExtraSite> conteudosExtraUnidade = getDAO(SecaoExtraSiteDao.class).
		findByIdioma(isEmpty(unidade)?curso.getId():unidade.getId(),
				this.tipoPortalPublico, null,null);
		for (Iterator<SecaoExtraSite> iterator = conteudosExtraUnidade.iterator(); iterator
				.hasNext();) {
			SecaoExtraSite conteudoExtraUnidade =  iterator.next();
			conteudoExtraUnidade.setTituloResumido(StringUtils.limitTxt(
					conteudoExtraUnidade.getTitulo(), 80));
		}
		return conteudosExtraUnidade;
		
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
