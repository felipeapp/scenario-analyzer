/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * MBean responsável por carregar as informações das páginas que mostram todas as informações dos
 * materiais de um título para a busca utilizada pelos bibliotecários.
 *
 * @author Jadson
 * @since 03/11/2008
 * @version 1.0 Criação da classe
 *
 */
@Component("detalhesMateriaisDeUmTituloMBean") 
@Scope("request")
public class DetalhesMateriaisDeUmTituloMBean extends DetalhesMateriaisMBean{

	/**
	 * Página que mostra a listagem de exemplares ou fascículos do título selecionado, dependendo se é um título de periódio ou não
	 */
	public static final String PAGINA_DETALHES_MATERIAIS  = "/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp";
	
//	/**
//	 * Página que mostra a listagem dos artigos de um fascículo.
//	 */
//	public static final String PAGINA_DETALHES_ARTIGOS_DE_UM_FASCIULO = "/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesArtigosDeUmFasciculo.jsp";
//	
	
	/**
	 * Guarda o Mbean que chamou o caso de uso visualizar os materiais de um Título, caso o usuário selecione algum dos materiais
	 * e tente realizar a ação da página, o fluxo vai voltar para o MBean que chamou esse caso de uso, nele é que vai ser
	 * decidido o que fazer com os materiais selecionados
	 */
	private PesquisarAcervoMateriaisInformacionais mBeanChamador;
	
	
	
	////////////////////////// Métodos para selecionar as os materiais dos Títulos /////////////////////////////////
	
	
	
	/**
	 *   Método que verifica os exemplares que o usuário selecionou e passa ao bean que controla 
	 *   a transferência entre Títulos.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp
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
	 * Voltar para a tela de pesquisa de títulos, mantendo a operação de pesquisa 
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/pesquisa_arcevo/pesquisa_acervo/paginaDetalhesMateriais.jsp
	 *
	 * @return
	 */
	public String voltarTela(){
		
		/*
		 * Quando alguém chama a tela de selecionar vários materiais a partir da busca de vários materiais do acervo
		 * Tem que voltar iniciando essa operação novamente.
		 */
		if( mBeanChamador != null ){
			PesquisaTituloCatalograficoMBean beanPesquisa = getMBean("pesquisaTituloCatalograficoMBean");
			beanPesquisa.setMbeanChamadoPesquisaTitulo(null);
			return beanPesquisa.iniciarPesquisaSelecionaVariosMateriaisDoTitulo(mBeanChamador, false);
		}
			
		// Por padrão voltar para a tela de pesquisa mantendo a última operação //
		PesquisaTituloCatalograficoMBean bean =  getMBean("pesquisaTituloCatalograficoMBean");
		return bean.telaPesquisaTitulo();
	}

	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	/**
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp</li>
	 *  </ul>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#irProximoResultado()
	 */
	public String irProximoResultado(){
		
		// Bean onde o resultado da pesquisa de títulos está //
		PesquisaTituloCatalograficoMBean bean = getMBean("pesquisaTituloCatalograficoMBean");
		
		for (int posicao = 0; posicao < bean.getResultadosBuscados().size() ; posicao ++) {
			if(bean.getResultadosBuscados().get(posicao).getIdTituloCatalografico().equals( obj.getId() )){ // achou a polição atual
				obj.setId(   bean.getResultadosBuscados().get(  posicao < bean.getResultadosBuscados().size()-1 ? posicao+1 : bean.getResultadosBusca().size()-1 ).getIdTituloCatalografico()  ); // seta o id do título próximo da pesquisa
				break;
			}
		}  

		return telaInformacoesMateriais();	// chama a página que vai carregar as informações desse título
		
	}

	

	/**
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp</li>
	 *  </ul>
	 *   
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#irResultadoAnterior()
	 */
	public String irResultadoAnterior(){

		// Bean onde o resultado da pesquisa de títulos está //
		PesquisaTituloCatalograficoMBean bean = getMBean("pesquisaTituloCatalograficoMBean");
		
		for (int posicao = 0; posicao < bean.getResultadosBuscados().size() ; posicao ++) {
			if(bean.getResultadosBuscados().get(posicao).getIdTituloCatalografico().equals( obj.getId() )){ // achou a polição atual
				obj.setId(   bean.getResultadosBuscados().get(  posicao > 0 ? posicao-1 : 0 ).getIdTituloCatalografico()  ); // seta o id do título anterior da pesquisa
				break;
			}
		}  

		return telaInformacoesMateriais();	// chama a página que vai carregar as informações desse título
	}
	
	
	/**
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp</li>
	 *  </ul>
	 * 
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#irPrimeiraPosicao()
	 */
	public String irPrimeiraPosicao(){
		
		// Bean onde o resultado da pesquisa de títulos está //
		PesquisaTituloCatalograficoMBean bean = getMBean("pesquisaTituloCatalograficoMBean");
		
		if(bean.getResultadosBuscados() != null && bean.getResultadosBuscados().size() != 0)
			obj.setId(   bean.getResultadosBuscados().get(0).getIdTituloCatalografico()  ); // seta o id do primeiro título da pesquisa
		
		return telaInformacoesMateriais();	// chama a página que vai carregar as informações desse título
	}
	
	
	
	
	/**
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp</li>
	 *  </ul>
	 *   
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#irUtimaPosicao()
	 */
	public String irUtimaPosicao(){
		
		// Bean onde o resultado da pesquisa de títulos está //
		PesquisaTituloCatalograficoMBean bean = getMBean("pesquisaTituloCatalograficoMBean");
		
		if(bean.getResultadosBuscados() != null && bean.getResultadosBuscados().size() != 0)
			obj.setId(   bean.getResultadosBuscados().get( bean.getResultadosBuscados().size()-1).getIdTituloCatalografico()  ); // seta o id do último título da pesquisa
		
		return telaInformacoesMateriais();	// chama a página que vai carregar as informações desse título
	}
	
	
	
	/**
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#getPodeAvancarResultadosPesquisa()
	 */
	public boolean getPodeAvancarResultadosPesquisa(){
		
		// Bean onde o resultado da pesquisa de títulos está //
		PesquisaTituloCatalograficoMBean bean = getMBean("pesquisaTituloCatalograficoMBean");
		
		if(bean.getResultadosBuscados() == null || bean.getResultadosBuscados().size() == 0)
			return false;
		
		if(! new Integer(obj.getId()).equals( bean.getResultadosBuscados().get( bean.getResultadosBuscados().size()-1).getIdTituloCatalografico() ) ) // não é o último da lista
			return true;
		else
			return false;
		
	}
	
	
	/**
	 * 
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *     <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisMBean#getPodeVoltarResultadosPesquisa()
	 */
	public boolean getPodeVoltarResultadosPesquisa(){
		
		// Bean onde o resultado da pesquisa de títulos está //
		PesquisaTituloCatalograficoMBean bean = getMBean("pesquisaTituloCatalograficoMBean");
		
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
	 *     <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaTituloCatalografico.jsp</li>
	 * </ul>
	 */
	public String visualizarMateriaisTitulo(){
		if(obj == null)
			obj = new TituloCatalografico();
		obj.setId(0);
		
		return telaInformacoesMateriais();
	}
	
	
	////////////////////////////////////// Tela de Navegação /////////////////////////////////////
	
	
	/**
	 * 
	 *   <p>Redireciona para a tela de detalhes dos materiais do título</p>
	 *
	 *   <p>Chamado a partir da página: /sigaa.war/biblioteca/pesquisa_arcevo/resultadoPesquisaTitulosCatalograficos.jsp</p>
	 * @return
	 * @throws DAOException 
	 */
	public String telaInformacoesMateriais(){
		
		// Se o usuário já selecionou a biblioteca na busca por títulos, por padrão ela já vem selecionada na visualização dos exemplares //
		
		Integer idBibliotecaPesquisaTitulos = ((PesquisaTituloCatalograficoMBean) getMBean("pesquisaTituloCatalograficoMBean")).getIdBiblioteca();
		
		if( idBibliotecaPesquisaTitulos != null && idBibliotecaPesquisaTitulos >= 0)
			this.idBibliotecaMateriais = idBibliotecaPesquisaTitulos;
		else
			this.idBibliotecaMateriais = VALOR_PADRAO_COMBOBOX_BIBLIOTECA;
		
		try {
			carregaDadosMateriais();
		} catch (DAOException e) {
			addMensagemErro("Não foi possível obter as informações do materiais");
		}
		
		return forward(PAGINA_DETALHES_MATERIAIS);
	}
	
	
	
	/**
	 *   <p>Redireciona para a tela de detalhes dos materiais do título recebendo o id do Título pelo método.</p>
	 * 
	 *  <p> Método usado quando o caso de uso de visualizar exemplares não é chamado diretamente de uma jsp e
	 *   sim a partir de outro MBean </p>
	 *
	 *   Método não chamado de nenhuma jsp.
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
			addMensagemErro("Não foi possível obter as informações do materiais");
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



