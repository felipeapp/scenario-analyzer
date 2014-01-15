/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/11/2008
 *
 */
package br.ufrn.sigaa.sites.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.dao.site.DetalhesSiteDao;
import br.ufrn.sigaa.arq.dao.site.DocumentoSiteDao;
import br.ufrn.sigaa.arq.dao.site.NoticiaSiteDao;
import br.ufrn.sigaa.arq.dao.site.SecaoExtraSiteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.parametros.dominio.ParametrosPortalPublico;
import br.ufrn.sigaa.sites.dominio.DetalhesSite;
import br.ufrn.sigaa.sites.dominio.DocumentoSite;
import br.ufrn.sigaa.sites.dominio.NoticiaSite;
import br.ufrn.sigaa.sites.dominio.SecaoExtraSite;
import br.ufrn.sigaa.sites.dominio.TemplateSite;
import br.ufrn.sigaa.sites.dominio.TipoDocumentoSite;
import br.ufrn.sigaa.sites.dominio.TipoPortalPublico;

/**
 * Classe gen�rica e comum aos Programas, Departamentos,
 * Cursos e Centros na p�gina p�blica do SIGAA.
 * 
 * @author M�rio Rizzi
 *
 */
public abstract class AbstractControllerPortalPublico extends SigaaAbstractController<Object>{

	
	/** Tipo de portal p�blico conforme URL */
	private TipoPortalPublico tipoPortalPublico;
	
	/** Diferente de nulo quando Programa, Departamento ou Centros	*/
	private Unidade unidade;
	/** Diferente de nulo quando Curso(Gradua��o, T�cnico ou Lato-Sensu) */
	private Curso curso;

	/** Atributo que possui os dados gerais do portal */
	private DetalhesSite detalhesSite;

	/** Atributo que possui os dados para visualiza��o da not�cia selecionada */
	private Collection<NoticiaSite> noticiaSiteDestaques;
	/** Atributo que possui todas as se��es extras em destaque do portal */
	private Collection<SecaoExtraSite> secaoExtraDestaques;

	/** Atributo que possui todas as not�cias associadas ao portal. */
	private Collection<NoticiaSite> noticiaSite;
	/** Atributo que define todos os documentos associados ao portal. */
	private Collection<DocumentoSite> documentosSite;
	/** Atributo que define todos os tipos de documento aossciados ao portal. */
	private Collection<TipoDocumentoSite> tipoDocumentosSite;

	/** Atribtuo que define os detalhes da not�cia selecionada no portal. */
	private NoticiaSite noticiaSiteDetalhes;
	
	/** Atributo que possui os dados para visualiza��o da se��o extra selecionada */
	private SecaoExtraSite secaoExtraSiteDetalhes;

	/** Atribtuo que possui o idioma definido no portal. */
	private String lc;
	
	/** Utilizado para quando o usu�rio que mant�m o portal possa visualizar as altera��es.  */
	private boolean preVisualizar;
	
	public AbstractControllerPortalPublico(){
		
		lc = getParameter("lc");
		if(lc == null)
			lc = ParametroHelper.getInstance().getParametro(ParametrosPortalPublico.LC_DEFAULT);
		
	}


	/**
	 * Retorna o id de uma unidade ou curso de acordo com
	 * o tipo de portal p�blico indicado pela URL.
	 * 
	 * @return
	 */
	public Integer getId() {
		
		if(!isEmpty(unidade) || !isEmpty(curso)) {
			switch (tipoPortalPublico) {
				case UNIDADE: return getUnidade().getId();
				case CURSO: return getCurso().getId();
				default: return 0;
			}
		}else
			return 0;
		
	}
	
	/**
	 * Retorna o endere�o do site alternativo, caso o acesso ao site seja obrigatoriamente externo.
	 * 
	 * <br />
	 * <br/><b>M�todo chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>/public/departamento/include/cabecalho.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public void getRedirecionarSiteExterno() throws ArqException{
		
		if(!isEmpty(getDetalhesSite()))
			if(getDetalhesSite().getSiteProprioObrigatorio()==true 
					&& getDetalhesSite().getSiteExtra() != null)
				 redirectSemContexto(getDetalhesSite().getSiteExtra());
				
	}

	/**
	 * Redireciona para p�gina principal do portal.
	 * 
	 * <br />
	 * <br/><b>M�todo chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>/public/departamento/include/menu.jsp</li>
	 * 	<li>/public/departamento/busca_departamento.jsp</li>
	 * 	<li>/public/programa/include/menu.jsp</li>
	 * 	<li>/public/programa/busca_programa.jsp</li>
	 * 	<li>/public/centro/include/menu.jsp</li>
	 * 	<li>/public/centro/busca_centro.jsp</li>
	 * </ul>
	 * 
	 *@return
	 */
	@Override
	public String cancelar() {
		
		this.unidade = new Unidade();
		this.curso = new Curso();
		return redirect("/sigaa/public/home.jsf");
		
	}

	/**
	 * Retorna uma cole��o de not�cias referente a um portal p�blico
	 * 
	 * <br />
	 * <br/><b>M�todo chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>/public/departamento/noticias_desc.jsp</li>
	 * 	<li>/public/programa/noticias_desc.jsp</li>
	 * 	<li>/public/centro/noticias_desc.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public NoticiaSite getNoticiaSiteDetalhes() throws ArqException {
		
		int id = getParameterInt("noticia", 0);
		if(isEmpty(noticiaSiteDetalhes) && !isEmpty(id))
			noticiaSiteDetalhes = getGenericDAO().findByExactField(
				NoticiaSite.class, "id", id, true);
		return noticiaSiteDetalhes;
		
	}
		
	public void setNoticiaSiteDetalhes(NoticiaSite noticiaSiteDetalhes) {
		this.noticiaSiteDetalhes = noticiaSiteDetalhes;
	}

	/**
	 * Retorna uma cole��o de se��es extras de um portal p�blico.
	 * 
	 * <br />
	 * <br/><b>M�todo chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>/public/programa/include/menu.jsp</li>
	 * 	<li>/public/departamento/include/menu.jsp</li>
	 * 	<li>/public/centro/include/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<SecaoExtraSite> getSecaoExtra() throws ArqException {
		
		if (isEmpty(secaoExtraDestaques) && !isEmpty(getId())) {
			SecaoExtraSiteDao daoSecao = getDAO(SecaoExtraSiteDao.class);
			secaoExtraDestaques =daoSecao.findByIdioma(getId(), this.tipoPortalPublico, 
					true,this.getLc());
			if (secaoExtraDestaques.size() == 0)
				secaoExtraDestaques = daoSecao.findByIdioma(getId(), this.tipoPortalPublico,
						 true,	ParametroHelper.getInstance().getParametro(ParametrosPortalPublico.LC_DEFAULT));
		}
		return secaoExtraDestaques;
		
	}
	
	/**
	 * Retorna uma cole��o de not�cias referente a um portal p�blico
	 * 
	 * <br />
	 * <br/><b>M�todo chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>/public/departamento/noticias_desc.jsp</li>
	 * 	<li>/public/programa/noticias_desc.jsp</li>
	 * 	<li>/public/centro/noticias_desc.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public SecaoExtraSite getSecaoExtraSiteDetalhes() throws ArqException {
		
		int id = getParameterInt("extra", 0);
		if(isEmpty(secaoExtraSiteDetalhes) && !isEmpty(id))
			secaoExtraSiteDetalhes = getGenericDAO().findByExactField(
				SecaoExtraSite.class, "id", id, true);
		return secaoExtraSiteDetalhes;
		
	}

	/**
	 * Retorna uma cole��o de noticias, com limite
	 * 
	 * <br />
	 * <br/><b>M�todo chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>/public/programa/include/menu.jsp</li>
	 * 	<li>/public/departamento/include/menu.jsp</li>
	 * 	<li>/public/centro/include/menu.jsp</li>
	 * 	<li>/public/programa/portal.jsp</li>
	 * 	<li>/public/departamento/portal.jsp</li>
	 * 	<li>/public/centro/portal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<NoticiaSite> getNoticiaSiteDestaques()
			throws ArqException {
		
		if (isEmpty(noticiaSiteDestaques) && !isEmpty(getId())) {
			
			
			Integer qtdMaxNoticias = ParametroHelper.getInstance().getParametroInt(ParametrosPortalPublico.QTD_MAX_NOTICIAS);
			String location = ParametroHelper.getInstance().getParametro(ParametrosPortalPublico.LC_DEFAULT);
			
			//Popula as noticias mais recentes do idioma seleciopnado
			NoticiaSiteDao noticiaSiteDao = getDAO(NoticiaSiteDao.class);
			noticiaSiteDestaques = noticiaSiteDao.findByIdioma(getId(),
					this.tipoPortalPublico, this.getLc(), true, qtdMaxNoticias);
			
			//Se n�o houver nenhuma not�cia,  pega as mais recentes do idioma padr�o
			if (isEmpty(noticiaSiteDestaques))
				noticiaSiteDestaques = noticiaSiteDao.findByIdioma(getId(),
						this.tipoPortalPublico,location, true, qtdMaxNoticias);
	
			if(!isEmpty(noticiaSiteDestaques)){
				//Popula os detalhes da noticia destaque na p�gina principal
				if(TipoPortalPublico.UNIDADE.equals(tipoPortalPublico) &&
						getUnidade().isPrograma()){
					noticiaSiteDetalhes = noticiaSiteDestaques.iterator().next(); 
					//Remove a not��ia de destaque da listagem de destaques
					noticiaSiteDestaques.remove(noticiaSiteDetalhes);
				}
			}

		}
		return noticiaSiteDestaques;
		
	}

	/**
	 * Retorna uma cole��o de not�cias de um portal p�blico
	 * 
	 * <br />
	 * <br/><b>M�todo chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>/public/programa/noticias.jsp</li>
	 * 	<li>/public/departamento/noticias.jsp</li>
	 * 	<li>/public/centro/noticias.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<NoticiaSite> getAllNoticiaSite() throws ArqException {
		
		if (noticiaSite == null && !isEmpty(getId())) {
			NoticiaSiteDao noticiaSiteDao = getDAO(NoticiaSiteDao.class);
			noticiaSite = noticiaSiteDao.findByIdioma(
					getId(), this.tipoPortalPublico, getLc(), true, null);
			if (noticiaSite.size() == 0)
				noticiaSite = noticiaSiteDao.findByIdioma(
						getId(), this.tipoPortalPublico, 
						ParametroHelper.getInstance().getParametro(ParametrosPortalPublico.LC_DEFAULT), true, null);
		}
		return noticiaSite;
		
	}
	
	/**
	 * Popula detalhes de um portal p�blico
	 * 
	 * <br />
	 * <br/><b>M�todo chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>/public/programa/include/menu.jsp</li>
	 * 	<li>/public/departamento/include/menu.jsp</li>
	 * 	<li>/public/centro/include/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public DetalhesSite getDetalhesSite() throws ArqException {
		
		if ( detalhesSite == null && !isEmpty(getId()) ){
			detalhesSite = getDAO(DetalhesSiteDao.class).findByIdSite(getId(), tipoPortalPublico);
			if( isEmpty(detalhesSite) ){
				detalhesSite = new DetalhesSite();
			}
			if( isEmpty(detalhesSite.getTemplateSite()) ){
				detalhesSite.setTemplateSite(new TemplateSite());
				detalhesSite.getTemplateSite().resetTemplate();
			}	
		}
		return detalhesSite;
		
	}

	/**
	 * Retorna o texto de introdu��o da Unidade(Departamento, Programa ou
	 * Centro) de acordo com o idioma setado
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String getIntroducaoLocale() throws ArqException {
		
		if (getDetalhesSite() != null) {
			if(lc != null){
				if ((lc.toLowerCase().indexOf("en")!=-1 || lc.indexOf("us")!=-1)
						&& !isEmpty(getDetalhesSite().getIntroducaoEN()))
					return getDetalhesSite().getIntroducaoEN();
				if (lc.toLowerCase().indexOf("fr")!=-1 
						&& !isEmpty(getDetalhesSite().getIntroducaoFR()))
					return getDetalhesSite().getIntroducaoFR();
				if (lc.toLowerCase().indexOf("es")!=-1 
						&& !isEmpty(getDetalhesSite().getIntroducaoES()))
					return getDetalhesSite().getIntroducaoES();
			}
			return getDetalhesSite().getIntroducao();

		} else
			return "";
	}
	
	/**
	 * Retorna o endere�o oficial do portal p�blico
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String getUrlOficial() throws ArqException{
		
		String urlOficial = "";
		if(tipoPortalPublico == TipoPortalPublico.UNIDADE && !isEmpty(getDetalhesSite())){
			if(getUnidade().getTipoAcademica().equals(TipoUnidadeAcademica.PROGRAMA_POS))
				urlOficial = ParametroHelper.getInstance().getParametro(ParametrosPortalPublico.URL_PROGRAMA) + "/" + getDetalhesSite().getUrl();
			else if(getUnidade().getTipoAcademica().equals(TipoUnidadeAcademica.DEPARTAMENTO))
				urlOficial = ParametroHelper.getInstance().getParametro(ParametrosPortalPublico.URL_DEPTO) + "/" + getDetalhesSite().getUrl();
			else if(getUnidade().getTipoAcademica().equals(TipoUnidadeAcademica.CENTRO))
				urlOficial = ParametroHelper.getInstance().getParametro(ParametrosPortalPublico.URL_CENTRO) + "/" + getDetalhesSite().getUrl();
			
		}else if(tipoPortalPublico == TipoPortalPublico.CURSO && !isEmpty(getDetalhesSite()) && !isEmpty(getDetalhesSite().getUrl()) ){
			urlOficial = ParametroHelper.getInstance().getParametro(ParametrosPortalPublico.URL_CURSO) + "/" + getDetalhesSite().getUrl();
		}
		return urlOficial;
		
	}
	
	/**
	 * Retorna todos os documentos do portal p�blico
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<DocumentoSite> getDocumentos() throws ArqException {
		Integer idTipoDoc = getParameterInt("idTipo");
		if (isEmpty(documentosSite) && (!isEmpty(getId()) || !isEmpty(idTipoDoc)) )
			documentosSite = getDAO(DocumentoSiteDao.class).findGeral(getId(), idTipoDoc, this.tipoPortalPublico,null);
		return documentosSite;
	}
	

	/**
	 * Retorna todos os documentos do portal p�blico
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<TipoDocumentoSite> getTiposDocumentos() throws ArqException {
		if (isEmpty(tipoDocumentosSite) && !isEmpty(getId()))
			tipoDocumentosSite = getDAO(DocumentoSiteDao.class).findTiposByUnidadeCurso(getId(),this.tipoPortalPublico,null);
		return tipoDocumentosSite;
	}

	/**
	 * Retorna os documentos associados ao site
	 * @return
	 */
	public Collection<DocumentoSite> getDocumentosSite() {
		return documentosSite;
	}

	/**
	 * Define os documentos associados ao site
	 * @param documentosSite
	 */
	public void setDocumentosSite(Collection<DocumentoSite> documentosSite) {
		this.documentosSite = documentosSite;
	}

	/**
	 * Retorna a unidade assiciada ao portal
	 * quando for do tipo CENTRO, DEPARTAMENTO ou PROGRAMA
	 * @return
	 */
	public Unidade getUnidade() {
		return unidade;
	}

	/**
	 * Define a unidade associada ao portal
	 * quando for do tipo CENTRO, DEPARTAMENTO ou PROGRAMA
	 * @param unidade
	 */
	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}
	
	/**
	 * Retorna o curso associado ao portal
	 * quando for do tipo CURSO
	 * @return
	 */
	public Curso getCurso() {
		return curso;
	}

	/**
	 * Define o curso associado ao portal
	 * quando for do tipo CURSO
	 * @param curso
	 */
	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	/**
	 * Define o idioma utilizado no portal
	 * @param lc
	 */
	public void setLc(String lc) {
		this.lc = lc;
	}

	/**
	 * Retorna o idioma selecionado no portal
	 * @return
	 */
	public String getLc() {
		return this.lc;
	}

	/**
	 * Retorna o tipo de portal
	 * @return
	 */
	public TipoPortalPublico getTipoPortalPublico() {
		return tipoPortalPublico;
	}

	/**
	 * Define o tipo de portal se � CENTRO, DEPARTAMENTO, PROGRAMA ou CURSO 
	 * @param tipoPortalPublico
	 */
	public void setTipoPortalPublico(TipoPortalPublico tipoPortalPublico) {
		this.tipoPortalPublico = tipoPortalPublico;
	}

	/**
	 * Define os detalhes da se��o extra selecionada no portal.
	 * @param secaoExtraSiteDetalhes
	 */
	public void setSecaoExtraSiteDetalhes(SecaoExtraSite secaoExtraSiteDetalhes) {
		this.secaoExtraSiteDetalhes = secaoExtraSiteDetalhes;
	}
	
	/**
	 * Retorna a URL da institui��o de ensino.
	 * @return
	 */
	public String getUrlInstituicao(){
		return ParametroHelper.getInstance().getParametro(ParametrosPortalPublico.URL_INSTITUICAO);
	}

	/**
	 * Define os dados gerais do portal acessado.
	 * @param detalhesSite
	 */
	public void setDetalhesSite(DetalhesSite detalhesSite) {
		this.detalhesSite = detalhesSite;
	}

	/**
	 * Define se o idioma utilizado no portal � portugu�s.
	 * @return
	 */
	public boolean isPt(){
		return lc == null || ParametroHelper.getInstance().getParametro(ParametrosPortalPublico.LC_DEFAULT).contains(lc);
	}

	/**
	 * Verifica se a a��o de um formul�rio no portal � de pr�-visualizar.
	 * @return
	 */
	public boolean isPreVisualizar() {
		return preVisualizar;
	}


	/**
	 * Define que a a��o de um formul�rio no portal � de pr�-visualizar.
	 * @param preVisualizar
	 */
	public void setPreVisualizar(boolean preVisualizar) {
		this.preVisualizar = preVisualizar;
	}
	
	/**
	 * Retorna o nome do curso concatenado coma a string 'CURSO DE '
	 * para visualiza��o no cabe�alho dos cursos na p�gina p�blica.
	 * <br />
	 * <b>M�todo chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>/public/curso/include/curso.jsp</li>
	 * </ul>
	 * @return
	 */
	public String getNomeCurso(){
		
		if( isEmpty(curso) )
			return null;
		
		String nomeCurso = curso.getNome().toUpperCase();
		if( nomeCurso.indexOf("CURSO") == -1 )
			nomeCurso = "CURSO DE " + nomeCurso;

		return nomeCurso;		
			
	}

}
