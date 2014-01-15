/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 17/03/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.negocio.RegistraEstatisticasBiblioteca;

/**
 *
 * MBean respons�vel por carregar as informa��es das p�ginas que mostram todas as informa��es dos
 * materiais de um t�tulo na parte p�blica do sistema.
 * 
 * @author jadson
 *
 */
@Component("detalhesMateriaisDeUmTituloPublicoMBean") 
@Scope("request")
public class DetalhesMateriaisDeUmTituloPublicoMBean extends DetalhesMateriaisMBean{

	/** visualiza os materiais de um T�tulo na parte p�blica do sistema */
	public static final String PAGINA_DETALHES_MATERIAIS_PUBLICA = "/public/biblioteca/paginaDetalhesMateriaisPublica.jsp";	
		
	
	/**
	 * 
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *     <li>/public/biblioteca/paginaDetalhesMateriaisPublica.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#getPodeAvancarResultadosPesquisa()
	 * 
	 */
	@Override
	public boolean getPodeAvancarResultadosPesquisa() {
		// Bean onde o resultado da pesquisa de t�tulos est� //
		PesquisaPublicaBibliotecaMBean bean = getMBean("pesquisaPublicaBibliotecaMBean");
		
		if(bean.getResultadosBuscados() == null || bean.getResultadosBuscados().size() == 0)
			return false;
		
		if(! new Integer(obj.getId()).equals( bean.getResultadosBuscados().get( bean.getResultadosBuscados().size()-1).getIdTituloCatalografico() ) ) // n�o � o �ltimo da lista
			return true;
		else
			return false;
	}
	
	
	/**
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *     <li>/public/biblioteca/paginaDetalhesMateriaisPublica.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#getPodeVoltarResultadosPesquisa()
	 * 
	 */
	@Override
	public boolean getPodeVoltarResultadosPesquisa() {
		// Bean onde o resultado da pesquisa de t�tulos est� //
		PesquisaPublicaBibliotecaMBean bean = getMBean("pesquisaPublicaBibliotecaMBean");
		
		if(bean.getResultadosBuscados() == null || bean.getResultadosBuscados().size() == 0)
			return false;
		
		if( ! new Integer(obj.getId()).equals( bean.getResultadosBuscados().get(0).getIdTituloCatalografico()) ) // n�o � o primeiro da lista
			return true;
		else
			return false;
	}
	
	
	/**
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *     <li>/public/biblioteca/paginaDetalhesMateriaisPublica.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#irPrimeiraPosicao()
	 * 
	 */
	@Override
	public String irPrimeiraPosicao() throws DAOException {
		// Bean onde o resultado da pesquisa de t�tulos est� //
		PesquisaPublicaBibliotecaMBean bean = getMBean("pesquisaPublicaBibliotecaMBean");
		
		if(bean.getResultadosBuscados() != null && bean.getResultadosBuscados().size() != 0)
			obj.setId(   bean.getResultadosBuscados().get(0).getIdTituloCatalografico()  ); // seta o id do primeiro t�tulo da pesquisa
		
		this.idsBibliotecasPublicas = bean.getIdsBibliotecasAcervoPublico();
		
		
		return telaInformacoesMateriais();	// chama a p�gina que vai carregar as informa��es desse t�tulo
	}
	
	
	/**
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *     <li>/public/biblioteca/paginaDetalhesMateriaisPublica.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#irProximoResultado()
	 *
	 */
	@Override
	public String irProximoResultado() throws DAOException {
		
		// Bean onde o resultado da pesquisa de t�tulos est� //
		PesquisaPublicaBibliotecaMBean bean = getMBean("pesquisaPublicaBibliotecaMBean");
		
		for (int posicao = 0; posicao < bean.getResultadosBuscados().size() ; posicao ++) {
			if(bean.getResultadosBuscados().get(posicao).getIdTituloCatalografico().equals( obj.getId() )){ // achou a poli��o atual
				obj.setId(   bean.getResultadosBuscados().get(  posicao < bean.getResultadosBuscados().size()-1 ? posicao+1 : bean.getResultadosBuscados().size()-1 ).getIdTituloCatalografico()  ); // seta o id do t�tulo pr�ximo da pesquisa
				break;
			}
		}  
		
		this.idsBibliotecasPublicas = bean.getIdsBibliotecasAcervoPublico();
		
		return telaInformacoesMateriais(); // chama a p�gina que vai carregar as informa��es desse t�tulo
	}
	
	/**
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *     <li>/public/biblioteca/paginaDetalhesMateriaisPublica.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#irResultadoAnterior()
	 * 
	 * 
	 */
	@Override
	public String irResultadoAnterior() throws DAOException {
		// Bean onde o resultado da pesquisa de t�tulos est� //
		PesquisaPublicaBibliotecaMBean bean = getMBean("pesquisaPublicaBibliotecaMBean");
		
		for (int posicao = 0; posicao < bean.getResultadosBuscados().size() ; posicao ++) {
			if(bean.getResultadosBuscados().get(posicao).getIdTituloCatalografico().equals( obj.getId() )){ // achou a poli��o atual
				obj.setId(   bean.getResultadosBuscados().get(  posicao > 0 ? posicao-1 : 0 ).getIdTituloCatalografico()  ); // seta o id do t�tulo anterior da pesquisa
				break;
			}
		}  
		
		this.idsBibliotecasPublicas = bean.getIdsBibliotecasAcervoPublico();
		
		return telaInformacoesMateriais();	// chama a p�gina que vai carregar as informa��es desse t�tulo
	}
	
	/**
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *     <li>/public/biblioteca/paginaDetalhesMateriaisPublica.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#irUtimaPosicao()
	 * 
	 */
	@Override
	public String irUtimaPosicao() throws DAOException {
		// Bean onde o resultado da pesquisa de t�tulos est� //
		PesquisaPublicaBibliotecaMBean bean = getMBean("pesquisaPublicaBibliotecaMBean");
		
		if(bean.getResultadosBuscados() != null && bean.getResultadosBuscados().size() != 0)
			obj.setId(   bean.getResultadosBuscados().get( bean.getResultadosBuscados().size()-1).getIdTituloCatalografico()  ); // seta o id do �ltimo t�tulo da pesquisa
		
		this.idsBibliotecasPublicas = bean.getIdsBibliotecasAcervoPublico();
		
		return telaInformacoesMateriais();	// chama a p�gina que vai carregar as informa��es desse t�tulo
	}

	
	/**
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *     <li>/sigaa.war/public/biblioteca/buscaPublicaAcervo.jsp</li>
	 * </ul>
	 */
	public String visualizarMateriaisTitulo(){
		if(obj == null)
			obj = new TituloCatalografico();
		obj.setId(0);
		
		return telaInformacoesMateriais();
	}

	/**
	 * M�todo respons�vel por realizar a pesquisa atrav�s de solicita��es externas via par�metro idTitulo.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *     <li>/sigaa.war/public/biblioteca/paginaDetalhesMateriaisPublica.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String getPesquisaExterna() throws DAOException{
		if(obj == null)
			obj = new TituloCatalografico();
		obj.setId(0);
		carregaDadosMateriais();
		return null;
	}
	
	
	
	/**
	 *
	 * Chamado a partir da p�gina: /sigaa.war/public/biblioteca/buscaPublicaAcervo.jsp
	 *
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#telaInformacoesMateriais()
	 */
	@Override
	public String telaInformacoesMateriais(){
		
		// Se o usu�rio j� selecionou a biblioteca na busca por t�tulos, por padr�o ela j� vem selecionada na visualiza��o dos exemplares //
		
		Integer idBibliotecaPesquisaTitulosTemp = ((PesquisaPublicaBibliotecaMBean) getMBean("pesquisaPublicaBibliotecaMBean")).getIdBiblioteca();
		
		if( idBibliotecaPesquisaTitulosTemp != null && idBibliotecaPesquisaTitulosTemp >= 0)
			this.idBibliotecaMateriais = idBibliotecaPesquisaTitulosTemp;
		else
			this.idBibliotecaMateriais = -2;
		
		try {
			carregaDadosMateriais();
		} catch (DAOException e) {
			addMensagemErro("N�o foi poss�vel obter as informa��es do materiais");
		}
		
		return forward(PAGINA_DETALHES_MATERIAIS_PUBLICA);
	}

	
	/**
	 * 
	 * Sobre escreve o m�todo da classe pai para al�m de carregar os dados dos materiais registrar a visualiza��o desse material.<br/>
	 * S� vai ser registrado as consultas realizadas pelo usu�rios (Publica e Interna).<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#carregaDadosMateriais()
	 */
	@Override
	protected void carregaDadosMateriais() throws DAOException {
		super.carregaDadosMateriais();
		

		// Registra a visualizacao desse T�tulo pelo usu�rio /// 
		RegistraEstatisticasBiblioteca.getInstance().registrarTituloVisualizado(obj.getId());
		
	}
	
	
	
	
//	/**
//	 *
//	 * Chamado a partir da p�gina: public/biblioteca/paginaDetalhesMateriaisPublica.jsp
//	 * 
//	 *
//	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#telaInformacoesArtigo()
//	 */
//	@Override
//	public String telaInformacoesArtigo() throws DAOException{
//		return forward(PAGINA_DETALHES_ARTIGOS_DE_UM_FASCICULO_PUBLICA);
//	}
	

	

}
