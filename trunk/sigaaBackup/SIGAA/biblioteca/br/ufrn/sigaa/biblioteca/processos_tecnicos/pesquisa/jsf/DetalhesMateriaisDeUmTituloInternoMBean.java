/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 16/07/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.negocio.RegistraEstatisticasBiblioteca;

/**
 * <p>MBean Responsável por mostrar os detalhes dos materiais de um Título na busca interna do sistema para usuários não bibliotecários </p>
 * 
 * @author jadson
 *
 */
@Component("detalhesMateriaisDeUmTituloInternoMBean") 
@Scope("request")
public class DetalhesMateriaisDeUmTituloInternoMBean extends DetalhesMateriaisMBean{

	/** A página de detalhes dos materiais da busca interna */
	public static final String PAGINA_DETALHES_MATERIAIS_INTERNA = "/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/paginaDetalhesMateriaisInterna.jsp";	
	
	
	/**
	 * 
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *     <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/detalhesMateriaisInterna.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#getPodeAvancarResultadosPesquisa()
	 * 
	 */
	@Override
	public boolean getPodeAvancarResultadosPesquisa() {
		// Bean onde o resultado da pesquisa de títulos está //
		PesquisaPublicaBibliotecaMBean bean = getMBean("pesquisaInternaBibliotecaMBean");
		
		if(bean.getResultadosBuscados() == null || bean.getResultadosBuscados().size() == 0)
			return false;
		
		if(! new Integer(obj.getId()).equals( bean.getResultadosBuscados().get( bean.getResultadosBuscados().size()-1).getIdTituloCatalografico() ) ) // não é o último da lista
			return true;
		else
			return false;
	}
	
	
	/**
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *     <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/detalhesMateriaisInterna.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#getPodeVoltarResultadosPesquisa()
	 * 
	 */
	@Override
	public boolean getPodeVoltarResultadosPesquisa() {
		// Bean onde o resultado da pesquisa de títulos está //
		PesquisaPublicaBibliotecaMBean bean = getMBean("pesquisaInternaBibliotecaMBean");
		
		if(bean.getResultadosBuscados() == null || bean.getResultadosBuscados().size() == 0)
			return false;
		
		if( ! new Integer(obj.getId()).equals( bean.getResultadosBuscados().get(0).getIdTituloCatalografico()) ) // não é o primeiro da lista
			return true;
		else
			return false;
	}
	
	
	/**
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *     <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/detalhesMateriaisInterna.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#irPrimeiraPosicao()
	 * 
	 */
	@Override
	public String irPrimeiraPosicao() throws DAOException {
		// Bean onde o resultado da pesquisa de títulos está //
		PesquisaPublicaBibliotecaMBean bean = getMBean("pesquisaInternaBibliotecaMBean");
		
		if(bean.getResultadosBuscados() != null && bean.getResultadosBuscados().size() != 0)
			obj.setId(   bean.getResultadosBuscados().get(0).getIdTituloCatalografico()  ); // seta o id do primeiro título da pesquisa
		
		this.idsBibliotecasPublicas = bean.getIdsBibliotecasAcervoPublico();
		
		return telaInformacoesMateriais();	// chama a página que vai carregar as informações desse título
	}
	
	
	/**
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *     <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/detalhesMateriaisInterna.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#irProximoResultado()
	 *
	 */
	@Override
	public String irProximoResultado() throws DAOException {
		
		// Bean onde o resultado da pesquisa de títulos está //
		PesquisaPublicaBibliotecaMBean bean = getMBean("pesquisaInternaBibliotecaMBean");
		
		for (int posicao = 0; posicao < bean.getResultadosBuscados().size() ; posicao ++) {
			if(bean.getResultadosBuscados().get(posicao).getIdTituloCatalografico().equals( obj.getId() )){ // achou a polição atual
				obj.setId(   bean.getResultadosBuscados().get(  posicao < bean.getResultadosBuscados().size()-1 ? posicao+1 : bean.getResultadosBuscados().size()-1 ).getIdTituloCatalografico()  ); // seta o id do título próximo da pesquisa
				break;
			}
		}  
		
		this.idsBibliotecasPublicas = bean.getIdsBibliotecasAcervoPublico();
		
		return telaInformacoesMateriais(); // chama a página que vai carregar as informações desse título
	}
	
	/**
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *     <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/detalhesMateriaisInterna.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#irResultadoAnterior()
	 * 
	 * 
	 */
	@Override
	public String irResultadoAnterior() throws DAOException {
		// Bean onde o resultado da pesquisa de títulos está //
		PesquisaPublicaBibliotecaMBean bean = getMBean("pesquisaInternaBibliotecaMBean");
		
		for (int posicao = 0; posicao < bean.getResultadosBuscados().size() ; posicao ++) {
			if(bean.getResultadosBuscados().get(posicao).getIdTituloCatalografico().equals( obj.getId() )){ // achou a polição atual
				obj.setId(   bean.getResultadosBuscados().get(  posicao > 0 ? posicao-1 : 0 ).getIdTituloCatalografico()  ); // seta o id do título anterior da pesquisa
				break;
			}
		}  
		
		this.idsBibliotecasPublicas = bean.getIdsBibliotecasAcervoPublico();
		
		return telaInformacoesMateriais();	// chama a página que vai carregar as informações desse título
	}
	
	/**
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *     <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/detalhesMateriaisInterna.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#irUtimaPosicao()
	 * 
	 */
	@Override
	public String irUtimaPosicao() throws DAOException {
		// Bean onde o resultado da pesquisa de títulos está //
		PesquisaPublicaBibliotecaMBean bean = getMBean("pesquisaInternaBibliotecaMBean");
		
		if(bean.getResultadosBuscados() != null && bean.getResultadosBuscados().size() != 0)
			obj.setId(   bean.getResultadosBuscados().get( bean.getResultadosBuscados().size()-1).getIdTituloCatalografico()  ); // seta o id do último título da pesquisa
		
		this.idsBibliotecasPublicas = bean.getIdsBibliotecasAcervoPublico();
		
		return telaInformacoesMateriais();	// chama a página que vai carregar as informações desse título
	}

	
	/**
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *     <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisa_interna/buscaInternaAcervo.jsp</li>
	 * </ul>
	 */
	public String visualizarMateriaisTitulo(){
		if(obj == null)
			obj = new TituloCatalografico();
		obj.setId(0);
		
		return telaInformacoesMateriais();
	}
	
	
	/**
	 * 
	 *   Redireciona para a tela interna de detalhes dos materiais do título
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisa_interna/buscaPublicaAcervo.jsp
	 * @return
	 */
	@Override
	public String telaInformacoesMateriais(){
		
		// Se o usuário já selecionou a biblioteca na busca por títulos, por padrão ela já vem selecionada na visualização dos exemplares //
		
		Integer idBibliotecaPesquisaTitulosTemp  = ((PesquisaInternaBibliotecaMBean) getMBean("pesquisaInternaBibliotecaMBean")).getIdBiblioteca();
		
		if( idBibliotecaPesquisaTitulosTemp != null && idBibliotecaPesquisaTitulosTemp >= 0)
			this.idBibliotecaMateriais = idBibliotecaPesquisaTitulosTemp;
		
		try {
			carregaDadosMateriais();
		} catch (DAOException e) {
			addMensagemErro("Não foi possível obter as informações do materiais");
		}
		
		return forward(PAGINA_DETALHES_MATERIAIS_INTERNA);
	}
	
	
	/**
	 * 
	 * Sobre escreve o método da classe pai para além de carregar os dados dos materiais registrar a visualização desse material.<br/>
	 * Só vai ser registrado as consultas realizadas pelo usuários (Publica e Interna).<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#carregaDadosMateriais()
	 */
	@Override
	protected void carregaDadosMateriais() throws DAOException {
		super.carregaDadosMateriais();
		

		// Registra a visualizacao desse Títulos /// 
		RegistraEstatisticasBiblioteca.getInstance().registrarTituloVisualizado(obj.getId());
		
	}
	
	
}
