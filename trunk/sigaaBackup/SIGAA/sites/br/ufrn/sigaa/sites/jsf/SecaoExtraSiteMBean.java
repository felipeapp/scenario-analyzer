/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * MBean responsável por carregar as sessões de páginas cadastradas para as
 * diferentes unidades(departamentos, programas e centro) e cursos(graduação, lato e técnico)
 * 
 * @author Mário Rizzi
 */
@Component("secaoExtraSite") @Scope("session")
public class SecaoExtraSiteMBean extends SigaaAbstractController<SecaoExtraSite> {

	private TipoPortalPublico tipoPortalPublico;
	
	private Unidade unidade;
	
	private Curso curso;
	
	private String urlPublica;
	
	private SelectItem[] linguagens = new SelectItem[] { 
			new SelectItem("pt_BR", "Português (Padrão)"),
			new SelectItem("en_US", "Inglês")/*,
			new SelectItem("es_ES", "Espanhol"),
			new SelectItem("fr_FR", "Francês")*/
	};
	
	public SecaoExtraSiteMBean() {
		obj = new SecaoExtraSite();
		setTipoPortalPublico();
	}

	/**
	 * Verifica se o usuário possui algum dos papéis abaixo.
	 * Não é chamado por nenhuma JSP.
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
	 * Cadastra os dados de uma notícia como não publicada para pré-visualização
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
	 * Retorna a URL para pré-visualização da notícia.
	 * JSP's chamadas: nenhuma.
	 */
	@Override
	public String getViewPage() {
		return urlPublica;
	}
	
	/**
	 * Cadastrar os dados de uma seção extra (conteúdo extra)
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
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO," Seção Extra ");
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		}

		return listar();
		
	}
	
	/**
	 * Cadastrar os dados de uma seção extra (conteúdo extra)
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
	 * Prepara o formulário de cadastro de seção do portal público dos cursos
	 * para visualização e inserção dos dados.
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
	 * Prepara o formulário de cadastro de seção do portal público dos centros
	 * para visualização e inserção dos dados.
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
	 * Prepara o formulário de cadastro de seção do portal público dos departamentos
	 * para visualização e inserção dos dados.
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
	 * Prepara o formulário de cadastro de seção do portal público dos programas
	 * para visualização e inserção dos dados.
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
	 * Prepara o formulário de cadastro.
	 *  Não é chamado por nenhuma JSP.
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
	 *  Atualiza os dados da seção extra dos portais públicos.
	 *  Não é chamado por nenhuma JSP.
	 */
	@Override
	public String atualizar() throws ArqException {
		
		prepareMovimento(SigaaListaComando.ATUALIZAR_SECAO_EXTRA_SITE);
		return super.atualizar();
		
	}
		
	/**
	 *  Seta os atributos descrição e linkExterno antes de cadastrar os dados 
	 *  da seção dos portais públicos. 
	 *  Não é chamado por nenhuma JSP.
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
	 *  Reinicializa o estado dos atributos publicada, linkExterno e descrição
	 *  após a atualização dos dados.
	 *  Não é chamado por nenhuma JSP.
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
	 * Remove uma seção extra de um portal público.
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
			addMensagemErro("Solicitação já processada!");
		}else{
			checkChangeRole();
			prepareMovimento(SigaaListaComando.REMOVER_SECAO_EXTRA_SITE);
			
			MovimentoSecaoExtraSite mov = new MovimentoSecaoExtraSite();
			mov.setSecaoExtraSite(obj);
			mov.setCodMovimento(SigaaListaComando.REMOVER_SECAO_EXTRA_SITE);
	
			try {
				execute(mov);
				addMensagemInformation("Seção Extra excluída com sucesso!");
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
	  * Lista todas as seções do portal público dos cursos(graduação, lato e técnico). 
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
	 *  Lista todas as seções do portal público dos centros.
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
	 * Lista todas as seções do portal público dos departamentos.
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
	 * Lista todas as seções do portal público dos programas de pós-graduação
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
	 * Retorna uma coleção de todos os conteúdo de uma unidade
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
