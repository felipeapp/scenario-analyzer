/*
 * TransfereExemplaresEntreTitulosMBean.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
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
 *    <p>MBean que gerencia a parte de transfer�ncia de exemplares entre t�tulos.</p>
 *
 *    <p> <i> ( A transfer�ncia de fasc�culos ocorre em outro caso de uso, porque envolve a tranfer�ncia n�o entre t�tulos, mas 
 *    entre assinaturas de t�tulos difer�ntes. )</i></p>
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
	 * P�gina que mostras as informa��o dos exemplares selecionados e do t�tulo destino para o usu�rio confirmar a opera��o.
	 */
	public static final String PAGINA_CONFIRMA_TRANSFERENCIA_EXEMPLARES 
			= "/biblioteca/processos_tecnicos/catalogacao/paginaConfirmaTransferenciaExemplares.jsp";
	
	
	      
	
	/** quando for transferir todos os exemplar de um t�tulo essa vari�vel vai estar != null */
	private Integer idTituloOriginalExemplares;   
	
	/** os id do T�tulo para onde os exemplares v�o. */
	private Integer idTituloDestinoExemplares;   
	
	/** As informa��es do T�tulo para o usu�rio visualizar  */
	private CacheEntidadesMarc tituloOriginal;
	
	/** As informa��es do T�tulo para o usu�rio visualizar */
	private CacheEntidadesMarc tituloDestinatario;
	
	/** Os Exemplares que n�o ser transferidos */
	private List<Exemplar> exemplaresParaTransferincia = new ArrayList<Exemplar>();
	
	/** 
	 * Guarda os materiais selecionados no caso de uso de pesquisa materiais no acervo temporariamente
	 */
	private List<MaterialInformacional> materiaisTemp = new ArrayList<MaterialInformacional>();
	
	
	/**
	 * Inicia o caso de uso com uma pesquisa no acervo dos exemplares
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 *     Executa a transfer�ncia dos exemplares para os T�tulos efetivamente.
	 *
	 *     Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaConfirmaTransferenciaExemplares.jsp
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
			
			addMensagemInformation("Transfer�ncia de exemplares executada com sucesso");
			
			return cancelar();
			
		}catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());
			
			
			// Se a transfer�ncia n�o for finalizada, volta para a p�gina de origem dos exemplares 
			DetalhesMateriaisDeUmTituloMBean bean = getMBean("detalhesMateriaisDeUmTituloMBean");
			return bean.telaInformacoesMateriaisPassandoIdTitulo(idTituloOriginalExemplares);
		}
		
		
		
	}
	
	
	
	
	
	
	///////////////////////////////////  telas de navega��o /////////////////////////////////
	
	
	
	/**
	 * M�todo n�o chamado por p�gina jsp.
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





	////////////////////////// M�todos da interface de busca de Materiais no Acervo ///////////////////////////////

	/**
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp</li>
	 *   </ul>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoMateriaisInformacionais#getDescricaoOperacaoUtilizandoBuscaAcervoMateriais()
	 */
	@Override
	public String getDescricaoOperacaoUtilizandoBuscaAcervoMateriais() {
		return "Transferir Exemplares para outro T�tulo";
	}



	/**
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
		return false;                            // N�o, entra direto na p�gina de pesquisa do acervo
	}


	/**
	 *   <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * <p>M�todo n�o chamado por p�gina jsp.</p>
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
				addMensagemErro("O material "+materialSelecionadoTemp.getCodigoBarras()+" n�o � um exemplar ");
				return null;
			}		
			
		}
		
		
		ExemplarDao dao = getDAO(ExemplarDao.class);
		MaterialInformacionalDao daoMaterial = getDAO(MaterialInformacionalDao.class);
		
		// Obt�m o T�tulo dos exemplares
		idTituloOriginalExemplares = dao.obtemIdDoTituloDoExemplar(exemplaresParaTransferincia.get(0).getId());
		tituloOriginal = BibliotecaUtil.obtemDadosTituloCache(idTituloOriginalExemplares);
		
		
		// Verifica se o usu�rio tem permiss�o de realizar a tranfer�ncia
		if(! isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
			
			for (Exemplar e : exemplaresParaTransferincia) {
			
				Integer idUnidadeMaterial =  daoMaterial.findIdUnidadeDoMaterialDaBibliotecaInternaAtiva(e.getId());
				
				try{
					checkRole(new Unidade( idUnidadeMaterial ), SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);
				}catch (SegurancaException se) {
					addMensagemErro(" O usu�rio n�o tem permiss�o para alterar o exemplar da biblioteca: "+dao.findDescricaoBibliotecaDoExemplar(e.getId()));
					// Se a transfer�ncia n�o for finalizada, volta para a p�gina de origem dos exemplares 
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
	 * <p>M�todo n�o chamado por p�gina jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoMateriaisInformacionais#setMateriaisRetornadosDaPesquisaAcervo(java.util.List)
	 */
	@Override
	public void setMateriaisRetornadosDaPesquisaAcervo(List<MaterialInformacional> materiaisSelecionados) throws ArqException {
		materiaisTemp = materiaisSelecionados;
	}

	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	

	
	
	
	
	
	
	
	
	
	
	
	//////////////////////////////  M�todos da interface de busca de T�tulos no acervo //////////////////////////
	
	/**
	 *  <p>M�todo n�o chamado por p�gina jsp.</p>
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
	 *  <p>M�todo n�o chamado por p�gina jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#setTitulo(br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc)
	 */
	@Override
	public void setTitulo(CacheEntidadesMarc titulo) throws ArqException {
		this.idTituloDestinoExemplares = titulo.getIdTituloCatalografico();
	}



	/**
	 * Volta para a tela onde seleciona os materiais do T�tulo de Origem
	 * 
	 *   <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
