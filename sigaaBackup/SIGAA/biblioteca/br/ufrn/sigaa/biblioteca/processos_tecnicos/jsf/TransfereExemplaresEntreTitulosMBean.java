/*
 * TransfereExemplaresEntreTitulosMBean.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.comum.dominio.Unidade;
import br.ufrn.sigaa.arq.dao.biblioteca.ExemplarDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoTransfereExemplaresEntreTitulos;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisDeUmTituloMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisaTituloCatalograficoMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoMateriaisInformacionais;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 *    <p>MBean que gerencia a parte de transferência de exemplares entre títulos.</p>
 *
 *    <p> <i> ( A transferência de fascículos ocorre em outro caso de uso, porque envolve a tranferência não entre títulos, mas 
 *    entre assinaturas de títulos diferêntes. )</i></p>
 *
 * @author jadson
 * @since 29/09/2009
 * @version 1.0 criacao da classe
 *
 */
@Component("transfereExemplaresEntreTitulosMBean")
@Scope("request")
public class TransfereExemplaresEntreTitulosMBean extends SigaaAbstractController<Object> implements PesquisarAcervoMateriaisInformacionais, PesquisarAcervoBiblioteca{

	/**
	 * Página que mostras as informação dos exemplares selecionados e do título destino para o usuário confirmar a operação.
	 */
	public static final String PAGINA_CONFIRMA_TRANSFERENCIA_EXEMPLARES 
			= "/biblioteca/processos_tecnicos/catalogacao/paginaConfirmaTransferenciaExemplares.jsp";
	
	
	      
	
	/** quando for transferir todos os exemplar de um título essa variável vai estar != null */
	private Integer idTituloOriginalExemplares;   
	
	/** os id do Título para onde os exemplares vão. */
	private Integer idTituloDestinoExemplares;   
	
	/** As informações do Título para o usuário visualizar  */
	private CacheEntidadesMarc tituloOriginal;
	
	/** As informações do Título para o usuário visualizar */
	private CacheEntidadesMarc tituloDestinatario;
	
	/** Os Exemplares que não ser transferidos */
	private List<Exemplar> exemplaresParaTransferincia = new ArrayList<Exemplar>();
	
	/** 
	 * Guarda os materiais selecionados no caso de uso de pesquisa materiais no acervo temporariamente
	 */
	private List<MaterialInformacional> materiaisTemp = new ArrayList<MaterialInformacional>();
	
	
	/**
	 * Inicia o caso de uso com uma pesquisa no acervo dos exemplares
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/processos_tecnicos</li>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String iniciarPrePesquisaTransferenciaTituloDoExemplar(){
		PesquisaTituloCatalograficoMBean beanPesquisa = getMBean("pesquisaTituloCatalograficoMBean");
		return beanPesquisa.iniciarPesquisaSelecionaVariosMateriaisDoTitulo(this, true);
	}
	
	
	
	
	/**
	 *     Executa a transferência dos exemplares para os Títulos efetivamente.
	 *
	 *     Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaConfirmaTransferenciaExemplares.jsp
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String transferirMateriais() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		try{
			
			MovimentoTransfereExemplaresEntreTitulos mov 
				= new MovimentoTransfereExemplaresEntreTitulos( exemplaresParaTransferincia, idTituloOriginalExemplares, idTituloDestinoExemplares);
			mov.setCodMovimento(SigaaListaComando.TRANSFERE_EXEMPLARES_ENTRE_TITULOS);
			
			execute(mov);
			
			addMensagemInformation("Transferência de exemplares executada com sucesso");
			
			return cancelar();
			
		}catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());
			
			
			// Se a transferência não for finalizada, volta para a página de origem dos exemplares 
			DetalhesMateriaisDeUmTituloMBean bean = getMBean("detalhesMateriaisDeUmTituloMBean");
			return bean.telaInformacoesMateriaisPassandoIdTitulo(idTituloOriginalExemplares);
		}
		
		
		
	}
	
	
	
	
	
	
	///////////////////////////////////  telas de navegação /////////////////////////////////
	
	
	
	/**
	 * Método não chamado por página jsp.
	 */
	public String telaConfirmaTransferenciaMaterial(){
		return forward(PAGINA_CONFIRMA_TRANSFERENCIA_EXEMPLARES);
	}

	
	/////////////////////////////////////////////////////////////////////////////////////////

	
	
	
	// sets e gets

	public CacheEntidadesMarc getTituloOriginal() {
		return tituloOriginal;
	}



	public void setTituloOriginal(CacheEntidadesMarc tituloOriginal) {
		this.tituloOriginal = tituloOriginal;
	}



	public CacheEntidadesMarc getTituloDestinatario() {
		return tituloDestinatario;
	}



	public void setTituloDestinatario(CacheEntidadesMarc tituloDestinatario) {
		this.tituloDestinatario = tituloDestinatario;
	}



	public List<Exemplar> getExemplaresParaTransferincia() {
		return exemplaresParaTransferincia;
	}


	public void setExemplaresParaTransferincia(List<Exemplar> exemplaresParaTransferincia) {
		this.exemplaresParaTransferincia = exemplaresParaTransferincia;
	}





	////////////////////////// Métodos da interface de busca de Materiais no Acervo ///////////////////////////////

	/**
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp</li>
	 *   </ul>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoMateriaisInformacionais#getDescricaoOperacaoUtilizandoBuscaAcervoMateriais()
	 */
	@Override
	public String getDescricaoOperacaoUtilizandoBuscaAcervoMateriais() {
		return "Transferir Exemplares para outro Título";
	}



	/**
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *   </ul>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoMateriaisInformacionais#isUtilizaBotaoVoltarBuscaPesquisarAcervoMateriais()
	 */
	@Override
	public boolean isUtilizaBotaoVoltarBuscaPesquisarAcervoMateriais() {
		return false;                            // Não, entra direto na página de pesquisa do acervo
	}


	/**
	 *   <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *   </ul>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoMateriaisInformacionais#voltarBusca()
	 */
	@Override
	public String voltarBuscaPesquisarAcervoMateriais() throws ArqException {
		return null;
	}
	



	/**
	 * <p>Método não chamado por página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoMateriaisInformacionais#selecionouMateriaisRetornadosDaPesquisaAcervo()
	 */
	@Override
	public String selecionouMateriaisRetornadosDaPesquisaAcervo() throws ArqException {

		exemplaresParaTransferincia = new ArrayList<Exemplar>();
		
		for (MaterialInformacional materialSelecionadoTemp : materiaisTemp) {
			
			if( materialSelecionadoTemp instanceof Exemplar){
				if(! exemplaresParaTransferincia.contains( materialSelecionadoTemp))
					exemplaresParaTransferincia.add((Exemplar)  materialSelecionadoTemp);
			}else{
				addMensagemErro("O material "+materialSelecionadoTemp.getCodigoBarras()+" não é um exemplar ");
				return null;
			}		
			
		}
		
		
		ExemplarDao dao = getDAO(ExemplarDao.class);
		MaterialInformacionalDao daoMaterial = getDAO(MaterialInformacionalDao.class);
		
		// Obtém o Título dos exemplares
		idTituloOriginalExemplares = dao.obtemIdDoTituloDoExemplar(exemplaresParaTransferincia.get(0).getId());
		tituloOriginal = BibliotecaUtil.obtemDadosTituloCache(idTituloOriginalExemplares);
		
		
		// Verifica se o usuário tem permissão de realizar a tranferência
		if(! isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
			
			for (Exemplar e : exemplaresParaTransferincia) {
			
				Integer idUnidadeMaterial =  daoMaterial.findIdUnidadeDoMaterialDaBibliotecaInternaAtiva(e.getId());
				
				try{
					checkRole(new Unidade( idUnidadeMaterial ), SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);
				}catch (SegurancaException se) {
					addMensagemErro(" O usuário não tem permissão para alterar o exemplar da biblioteca: "+dao.findDescricaoBibliotecaDoExemplar(e.getId()));
					// Se a transferência não for finalizada, volta para a página de origem dos exemplares 
					//DetalhesMateriaisDeUmTituloMBean bean = getMBean("detalhesMateriaisDeUmTituloMBean");
					return null;
				}
				
			}
			
		}
		
		PesquisaTituloCatalograficoMBean bean = getMBean("pesquisaTituloCatalograficoMBean");
		bean.limpaResultadoPesquisa();
		return bean.iniciarPesquisaSelecionarTitulo(this);
		
	}



	/**
	 * <p>Método não chamado por página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoMateriaisInformacionais#setMateriaisRetornadosDaPesquisaAcervo(java.util.List)
	 */
	@Override
	public void setMateriaisRetornadosDaPesquisaAcervo(List<MaterialInformacional> materiaisSelecionados) throws ArqException {
		materiaisTemp = materiaisSelecionados;
	}

	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	

	
	
	
	
	
	
	
	
	
	
	
	//////////////////////////////  Métodos da interface de busca de Títulos no acervo //////////////////////////
	
	/**
	 *  <p>Método não chamado por página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#selecionaTitulo()
	 */
	@Override
	public String selecionaTitulo() throws ArqException {
		
		prepareMovimento(SigaaListaComando.TRANSFERE_EXEMPLARES_ENTRE_TITULOS);
		
		tituloDestinatario = BibliotecaUtil.obtemDadosTituloCache(idTituloDestinoExemplares);
		
		return telaConfirmaTransferenciaMaterial();
	}



	/**
	 *  <p>Método não chamado por página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#setTitulo(br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc)
	 */
	@Override
	public void setTitulo(CacheEntidadesMarc titulo) throws ArqException {
		this.idTituloDestinoExemplares = titulo.getIdTituloCatalografico();
	}



	/**
	 * Volta para a tela onde seleciona os materiais do Título de Origem
	 * 
	 *   <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *   </ul>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#voltarBuscaAcervo()
	 */
	@Override
	public String voltarBuscaAcervo() throws ArqException {
		DetalhesMateriaisDeUmTituloMBean bean = getMBean("detalhesMateriaisDeUmTituloMBean");	
		bean.setMbeanChamador(this);
		return bean.telaInformacoesMateriaisPassandoIdTitulo(idTituloOriginalExemplares);
	}

	/**
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *   </ul>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#isUtilizaVoltarBuscaAcervo()
	 */
	@Override
	public boolean isUtilizaVoltarBuscaAcervo() {
		return true;
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
}
