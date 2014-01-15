/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/07/2009
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;

/**
 *
 * MBean respons�vel por carregar as informa��es das p�ginas que mostram todas as informa��es dos
 * materiais de um t�tulo para a busca utilizada pelos bibliotec�rios.
 *
 * @author Jadson
 * @since 03/11/2008
 * @version 1.0 Cria��o da classe
 *
 */
@Component("detalhesMateriaisDeUmTituloMBean") 
@Scope("request")
public class DetalhesMateriaisDeUmTituloMBean extends DetalhesMateriaisMBean{

	/**
	 * P�gina que mostra a listagem de exemplares ou fasc�culos do t�tulo selecionado, dependendo se � um t�tulo de peri�dio ou n�o
	 */
	public static final String PAGINA_DETALHES_MATERIAIS  = "/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp";
	
//	/**
//	 * P�gina que mostra a listagem dos artigos de um fasc�culo.
//	 */
//	public static final String PAGINA_DETALHES_ARTIGOS_DE_UM_FASCIULO = "/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesArtigosDeUmFasciculo.jsp";
//	
	
	/**
	 * Guarda o Mbean que chamou o caso de uso visualizar os materiais de um T�tulo, caso o usu�rio selecione algum dos materiais
	 * e tente realizar a a��o da p�gina, o fluxo vai voltar para o MBean que chamou esse caso de uso, nele � que vai ser
	 * decidido o que fazer com os materiais selecionados
	 */
	private PesquisarAcervoMateriaisInformacionais mBeanChamador;
	
	
	
	////////////////////////// M�todos para selecionar as os materiais dos T�tulos /////////////////////////////////
	
	
	
	/**
	 *   M�todo que verifica os exemplares que o usu�rio selecionou e passa ao bean que controla 
	 *   a transfer�ncia entre T�tulos.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String realizarAcaoMateriasSelecionado() throws ArqException{
		
		List<MaterialInformacional> materiaisSelecionados = new ArrayList<MaterialInformacional>();
		
		if(! isPeriodico()){
			for (Exemplar e : exemplares) {
				if(e.isSelecionado())
					materiaisSelecionados.add(e);
			}
		}else{
			for (Fasciculo f : fasciculos) {
				if(f.isSelecionado())
					materiaisSelecionados.add(f);
			}
		}
		
		if(materiaisSelecionados.size() == 0){
			addMensagemErro("Selecione pelo menos um material para "+mBeanChamador.getDescricaoOperacaoUtilizandoBuscaAcervoMateriais());
			return null;
		}
		
		mBeanChamador.setMateriaisRetornadosDaPesquisaAcervo(materiaisSelecionados);
		
		return mBeanChamador.selecionouMateriaisRetornadosDaPesquisaAcervo();
		
	}

	
	
	
	/**
	 * Voltar para a tela de pesquisa de t�tulos, mantendo a opera��o de pesquisa 
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/pesquisa_arcevo/pesquisa_acervo/paginaDetalhesMateriais.jsp
	 *
	 * @return
	 */
	public String voltarTela(){
		
		/*
		 * Quando algu�m chama a tela de selecionar v�rios materiais a partir da busca de v�rios materiais do acervo
		 * Tem que voltar iniciando essa opera��o novamente.
		 */
		if( mBeanChamador != null ){
			PesquisaTituloCatalograficoMBean beanPesquisa = getMBean("pesquisaTituloCatalograficoMBean");
			beanPesquisa.setMbeanChamadoPesquisaTitulo(null);
			return beanPesquisa.iniciarPesquisaSelecionaVariosMateriaisDoTitulo(mBeanChamador, false);
		}
			
		// Por padr�o voltar para a tela de pesquisa mantendo a �ltima opera��o //
		PesquisaTituloCatalograficoMBean bean =  getMBean("pesquisaTituloCatalograficoMBean");
		return bean.telaPesquisaTitulo();
	}

	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	/**
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp</li>
	 *  </ul>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#irProximoResultado()
	 */
	public String irProximoResultado(){
		
		// Bean onde o resultado da pesquisa de t�tulos est� //
		PesquisaTituloCatalograficoMBean bean = getMBean("pesquisaTituloCatalograficoMBean");
		
		for (int posicao = 0; posicao < bean.getResultadosBuscados().size() ; posicao ++) {
			if(bean.getResultadosBuscados().get(posicao).getIdTituloCatalografico().equals( obj.getId() )){ // achou a poli��o atual
				obj.setId(   bean.getResultadosBuscados().get(  posicao < bean.getResultadosBuscados().size()-1 ? posicao+1 : bean.getResultadosBusca().size()-1 ).getIdTituloCatalografico()  ); // seta o id do t�tulo pr�ximo da pesquisa
				break;
			}
		}  

		return telaInformacoesMateriais();	// chama a p�gina que vai carregar as informa��es desse t�tulo
		
	}

	

	/**
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp</li>
	 *  </ul>
	 *   
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#irResultadoAnterior()
	 */
	public String irResultadoAnterior(){

		// Bean onde o resultado da pesquisa de t�tulos est� //
		PesquisaTituloCatalograficoMBean bean = getMBean("pesquisaTituloCatalograficoMBean");
		
		for (int posicao = 0; posicao < bean.getResultadosBuscados().size() ; posicao ++) {
			if(bean.getResultadosBuscados().get(posicao).getIdTituloCatalografico().equals( obj.getId() )){ // achou a poli��o atual
				obj.setId(   bean.getResultadosBuscados().get(  posicao > 0 ? posicao-1 : 0 ).getIdTituloCatalografico()  ); // seta o id do t�tulo anterior da pesquisa
				break;
			}
		}  

		return telaInformacoesMateriais();	// chama a p�gina que vai carregar as informa��es desse t�tulo
	}
	
	
	/**
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp</li>
	 *  </ul>
	 * 
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#irPrimeiraPosicao()
	 */
	public String irPrimeiraPosicao(){
		
		// Bean onde o resultado da pesquisa de t�tulos est� //
		PesquisaTituloCatalograficoMBean bean = getMBean("pesquisaTituloCatalograficoMBean");
		
		if(bean.getResultadosBuscados() != null && bean.getResultadosBuscados().size() != 0)
			obj.setId(   bean.getResultadosBuscados().get(0).getIdTituloCatalografico()  ); // seta o id do primeiro t�tulo da pesquisa
		
		return telaInformacoesMateriais();	// chama a p�gina que vai carregar as informa��es desse t�tulo
	}
	
	
	
	
	/**
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp</li>
	 *  </ul>
	 *   
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#irUtimaPosicao()
	 */
	public String irUtimaPosicao(){
		
		// Bean onde o resultado da pesquisa de t�tulos est� //
		PesquisaTituloCatalograficoMBean bean = getMBean("pesquisaTituloCatalograficoMBean");
		
		if(bean.getResultadosBuscados() != null && bean.getResultadosBuscados().size() != 0)
			obj.setId(   bean.getResultadosBuscados().get( bean.getResultadosBuscados().size()-1).getIdTituloCatalografico()  ); // seta o id do �ltimo t�tulo da pesquisa
		
		return telaInformacoesMateriais();	// chama a p�gina que vai carregar as informa��es desse t�tulo
	}
	
	
	
	/**
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#getPodeAvancarResultadosPesquisa()
	 */
	public boolean getPodeAvancarResultadosPesquisa(){
		
		// Bean onde o resultado da pesquisa de t�tulos est� //
		PesquisaTituloCatalograficoMBean bean = getMBean("pesquisaTituloCatalograficoMBean");
		
		if(bean.getResultadosBuscados() == null || bean.getResultadosBuscados().size() == 0)
			return false;
		
		if(! new Integer(obj.getId()).equals( bean.getResultadosBuscados().get( bean.getResultadosBuscados().size()-1).getIdTituloCatalografico() ) ) // n�o � o �ltimo da lista
			return true;
		else
			return false;
		
	}
	
	
	/**
	 * 
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *     <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#getPodeVoltarResultadosPesquisa()
	 */
	public boolean getPodeVoltarResultadosPesquisa(){
		
		// Bean onde o resultado da pesquisa de t�tulos est� //
		PesquisaTituloCatalograficoMBean bean = getMBean("pesquisaTituloCatalograficoMBean");
		
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
	 *     <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaTituloCatalografico.jsp</li>
	 * </ul>
	 */
	public String visualizarMateriaisTitulo(){
		if(obj == null)
			obj = new TituloCatalografico();
		obj.setId(0);
		
		return telaInformacoesMateriais();
	}
	
	
	////////////////////////////////////// Tela de Navega��o /////////////////////////////////////
	
	
	/**
	 * 
	 *   <p>Redireciona para a tela de detalhes dos materiais do t�tulo</p>
	 *
	 *   <p>Chamado a partir da p�gina: /sigaa.war/biblioteca/pesquisa_arcevo/resultadoPesquisaTitulosCatalograficos.jsp</p>
	 * @return
	 * @throws DAOException 
	 */
	public String telaInformacoesMateriais(){
		
		// Se o usu�rio j� selecionou a biblioteca na busca por t�tulos, por padr�o ela j� vem selecionada na visualiza��o dos exemplares //
		
		Integer idBibliotecaPesquisaTitulos = ((PesquisaTituloCatalograficoMBean) getMBean("pesquisaTituloCatalograficoMBean")).getIdBiblioteca();
		
		if( idBibliotecaPesquisaTitulos != null && idBibliotecaPesquisaTitulos >= 0)
			this.idBibliotecaMateriais = idBibliotecaPesquisaTitulos;
		else
			this.idBibliotecaMateriais = VALOR_PADRAO_COMBOBOX_BIBLIOTECA;
		
		try {
			carregaDadosMateriais();
		} catch (DAOException e) {
			addMensagemErro("N�o foi poss�vel obter as informa��es do materiais");
		}
		
		return forward(PAGINA_DETALHES_MATERIAIS);
	}
	
	
	
	/**
	 *   <p>Redireciona para a tela de detalhes dos materiais do t�tulo recebendo o id do T�tulo pelo m�todo.</p>
	 * 
	 *  <p> M�todo usado quando o caso de uso de visualizar exemplares n�o � chamado diretamente de uma jsp e
	 *   sim a partir de outro MBean </p>
	 *
	 *   M�todo n�o chamado de nenhuma jsp.
	 *   
	 * @return
	 */
	public String telaInformacoesMateriaisPassandoIdTitulo(int idTitulo){
		
		if(obj == null)
			obj = new TituloCatalografico();
		obj.setId(idTitulo);
		
		try {
			carregaDadosMateriais();
		} catch (DAOException e) {
			addMensagemErro("N�o foi poss�vel obter as informa��es do materiais");
		}
		
		return forward(PAGINA_DETALHES_MATERIAIS);
	}

	
	
	
	///////////////////////////////////////////////////////////////////////////
	
	

	// sets e gets
	

	public void setMbeanChamador(PesquisarAcervoMateriaisInformacionais beanChamador) {
		this.mBeanChamador = beanChamador;
	}
	

	public PesquisarAcervoMateriaisInformacionais getMbeanChamador() {
		return mBeanChamador;
	}

	
}



