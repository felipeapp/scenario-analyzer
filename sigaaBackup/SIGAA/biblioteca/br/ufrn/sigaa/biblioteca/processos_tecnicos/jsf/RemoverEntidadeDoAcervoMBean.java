/*
 * RemoverEntidadeDoAcervoMBean.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoRemoveEntidadesDoAcervo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisaTituloCatalograficoMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarArtigoMBean;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 *
 * MBea que gerencia a remo��o de t�tulos. Apenas t�tulo que n�o possuem materias n�o baixados (ativos)
 * podem ser removidos.
 *
 * @author jadson
 * @since 25/09/2009
 * @version 1.0 criacao da classe
 *
 */
@Component("removerEntidadeDoAcervoMBean")
@Scope("request")
public class RemoverEntidadeDoAcervoMBean extends SigaaAbstractController<CacheEntidadesMarc>{

	/** Constante que define a p�gina de remo��o de t�tulo. */
	public static final String PAGINA_CONFIRMA_REMOCAO_TITULO = "/biblioteca/processos_tecnicos/catalogacao/confirmaRemocaoTitulo.jsp";
	/** Constante que define a p�gina de remo��o de autoridade. */
	public static final String PAGINA_CONFIRMA_REMOCAO_AUTORIDADE = "/biblioteca/processos_tecnicos/catalogacao/confirmaRemocaoAutoridade.jsp";
	/** Constante que define a p�gina de remo��o de artigo de peri�dico. */
	public static final String PAGINA_CONFIRMA_REMOCAO_ARTIGO_DE_PERIODICO = "/biblioteca/processos_tecnicos/catalogacao/confirmaRemocaoArtigoDePeriodico.jsp";

	/** Indica se a requisi��o ao bean veio da p�gina de pesquisa ou n�o. */
	private boolean vindoPaginaPesquisa = false;
	/** Indica se a requisi��o ao bean veio da p�gina de cataloga��es incompletas ou n�o. */
	private boolean vindoPaginaCatalogacoesIncomplates = false;
	
	// Vari�veis utilizadas no controle das remo��es de T�tulos, Autoridade e Artigos.
	/** Indica se a remo��o � uma remo��o de t�tulos. */
	private boolean remocaoDeTitulos; 
	/** Indica se a remo��o � uma remo��o de autoridades. */
	private boolean remocaoDeAutoridades;
	/** Indica se a remo��o � uma remo��o de artigos. */
	private boolean remocaoDeArtigos;

	/** Se true, a opera��o do bot�o 'Voltar' redireciona o fluxo de navega��o para a p�gina que requisitou a action de edi��o de artigo. */
	private boolean voltarParaRequest;
	
	/** Armazena a p�gina que requisitou a action de edi��o de artigo. */
	private String requestURL;
	
	/**
	 *    Obt�m as informa��es do t�tulo passado e redireciona para a p�gina onde o usu�rio vai poder 
	 * confirma a remo��o do t�tulo.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/resultadoPesquisaTituloCatalografico.jsp
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String telaConfirmaRemocaoVindoPaginaPesquisaTitulo() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.REMOVE_ENTIDADES_DO_ACERVO);
		
		zeraOperacoes();
		vindoPaginaPesquisa = true;
		
		remocaoDeTitulos = true;
		
		obj = BibliotecaUtil.obtemDadosTituloCache(getParameterInt("idTituloRemocao"));
	
		if(obj == null){
			addMensagemErro("O T�tulo j� foi removido.");
			return null;
		}
		
		
		return forward(PAGINA_CONFIRMA_REMOCAO_TITULO);
	}
	
	
	
	
	/**
	 *    Obt�m as informa��es do t�tulo passado e redireciona para a p�gina onde o usu�rio vai poder 
	 * confirma a remo��o do t�tulo.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaTitulosCatalogacaoIncompleta.jsp
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String telaConfirmaRemocaoVindoPaginaCatalogacoesIncompletas() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.REMOVE_ENTIDADES_DO_ACERVO);
		
		zeraOperacoes();
		vindoPaginaCatalogacoesIncomplates = true;
		
		remocaoDeTitulos = true;
		
		obj = BibliotecaUtil.obtemDadosTituloCache(getParameterInt("idTituloRemocao"));
		
		if(obj == null){
			addMensagemErro("O T�tulo j� foi removido.");
			return null;
		}
		
		return forward(PAGINA_CONFIRMA_REMOCAO_TITULO);
	}
	
	
	/**
	 *    Obt�m as informa��es da autoridade passada e redireciona para a p�gina onde o usu�rio vai poder 
	 * confirma a remo��o da autoridade.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/paginaPesquisaAutoridade.jsp
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String telaConfirmaRemocaoVindoPaginaPesquisaAutoridades() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.REMOVE_ENTIDADES_DO_ACERVO);
		
		zeraOperacoes();
		vindoPaginaPesquisa = true;
		
		remocaoDeAutoridades = true;
		
		obj = BibliotecaUtil.obtemDadosAutoridadeEmCache(getParameterInt("idAutoridadeRemocao"));
		
		if(obj == null){
			addMensagemErro("Autoridade j� foi removida.");
			return null;
		}
		
		return forward(PAGINA_CONFIRMA_REMOCAO_AUTORIDADE);
	}
	
	/**
	 *    Obt�m as informa��es da autoridade passada e redireciona para a p�gina onde o usu�rio poder� 
	 * confirmar a remo��o da autoridade.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaAutoridadesCatalogacaoIncompleta.jsp
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String telaConfirmaRemocaoVindoPaginaCatalogacoesIncompletasAutoridades() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.REMOVE_ENTIDADES_DO_ACERVO);
		
		zeraOperacoes();
		vindoPaginaCatalogacoesIncomplates = true;
		
		remocaoDeAutoridades = true;
		
		obj = BibliotecaUtil.obtemDadosAutoridadeEmCache(getParameterInt("idAutoridadeRemocao"));
		
		if(obj == null){
			addMensagemErro("Autoridade j� foi removida.");
			return null;
		}
		
		return forward(PAGINA_CONFIRMA_REMOCAO_AUTORIDADE);
	}
	
	
	
	/**
	 *    Obt�m as informa��es da autoridade passada e redireciona para a p�gina onde o usu�rio poder� 
	 * confirmar a remo��o do Artigo.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/paginaPesquisaAutoridade.jsp
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String telaConfirmaRemocaoVindoPaginaPesquisaArtigos() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);

		if (getParameterBoolean("voltarParaRequest")) {
			voltarParaRequest = true;
			requestURL = getCurrentRequest().getRequestURI().replaceFirst(getContextPath(), "");
		}
		else {
			voltarParaRequest = false;
		}
		
		prepareMovimento(SigaaListaComando.REMOVE_ENTIDADES_DO_ACERVO);
		
		zeraOperacoes();
		vindoPaginaPesquisa = true;
		
		remocaoDeArtigos = true;
		
		obj = BibliotecaUtil.obtemDadosArtigoCache(getParameterInt("idArtigoRemocao"));
		
		if(obj == null){
			addMensagemErro("O artigo j� foi removido.");
			return null;
		}
		
		return forward(PAGINA_CONFIRMA_REMOCAO_ARTIGO_DE_PERIODICO);
	}
	
	
	
	/**
	 *    Obt�m as informa��es da autoridade passada e redireciona para a p�gina onde o usu�rio poder� 
	 * confirmar a remo��o do fasc�culo.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesArtigosDeUmFasciculo.jsp
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String telaConfirmaRemocaoVindoPaginaVisualizacaoArtigosDoFasciculo() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.REMOVE_ENTIDADES_DO_ACERVO);
		
		zeraOperacoes();
		
		// aqui no caso � usado para quando vem da p�gina de visualizar as informa��es de artigos de um fasc�culo
		vindoPaginaCatalogacoesIncomplates = true;
		
		remocaoDeArtigos = true;
		
		obj = BibliotecaUtil.obtemDadosArtigoCache(getParameterInt("idArtigoRemocao"));
		
		if(obj == null){
			addMensagemErro("O artigo j� foi removido.");
			return null;
		}
		
		return forward(PAGINA_CONFIRMA_REMOCAO_ARTIGO_DE_PERIODICO);
	}
	
	
	
	
	/**
	 *    Paga o t�tulo do acervo para sempre.
	 *
	 *    Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/confirmaRemocaoTitulo.jsp";
	 *
	 * @return
	 * @throws ArqException
	 * @throws  
	 */
	public String removerEntidade() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		MovimentoRemoveEntidadesDoAcervo movimento = new MovimentoRemoveEntidadesDoAcervo(obj);
		movimento.setCodMovimento(SigaaListaComando.REMOVE_ENTIDADES_DO_ACERVO);
		
		try {
			
			if(remocaoDeTitulos)
				movimento.setRemocaoDeTitulo();
			
			if(remocaoDeAutoridades)
				movimento.setRemocaoDeAutoridade();
			
			if(remocaoDeTitulos)
				movimento.setRemocaoDeTitulo();
			
			if(remocaoDeArtigos)
				movimento.setRemocaoDeArtigo();
			
			execute(movimento);
			
			
			if(remocaoDeTitulos)
				addMensagemInformation("T�tulo com o n�mero do sistema: "+obj.getNumeroDoSistema()+" removido com sucesso.");
			
			if(remocaoDeAutoridades)
				addMensagemInformation("Autoridade com o n�mero do sistema: "+obj.getNumeroDoSistema()+" removido com sucesso.");
			
			if(remocaoDeArtigos)
				addMensagemInformation("Artigo de Peri�dico com o n�mero do sistema: "+obj.getNumeroDoSistema()+" removido com sucesso.");
			
			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		if(remocaoDeTitulos)
			return voltarTelaTitulos();
		
		if(remocaoDeAutoridades)
			return voltarTelaAutoridades();
		
		if(remocaoDeArtigos)
			return voltarTelaArtigos();
		
		return null;
		
	}

	
	
	
	
	/**
	 *    Retorna a tela de t�tulos de onde foi chamado
	 *
	 *    Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/confirmaRemocaoTitulo.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String voltarTelaTitulos() throws ArqException{

		if(vindoPaginaPesquisa){
			PesquisaTituloCatalograficoMBean bean =  getMBean("pesquisaTituloCatalograficoMBean");
		    
			if(bean.isRealizouPesquisaMultiCampo())
				return bean.pesquisaMultiCampo();
			else
				return bean.pesquisaAvancada();
		}else{
			BuscaCatalogacoesIncompletasMBean bean =  getMBean("buscaCatalogacoesIncompletasMBean");
		
			if(bean.isPesquisaImportacao()){
				bean.setTitulosIncompletos(null); // para buscar novamente
				return bean.iniciarBuscaTitulosIncompletosImportacao();
			}else{
				bean.setTitulosIncompletos(null); // para buscar novamente
				return bean.iniciarBuscaTitulosIncompletos();
			}
		}
		
	}
	
	
	/**
	 *    Retorna a tela de Autoridades de onde foi chamado.
	 *
	 *    Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/confirmaRemocaoAutoridade.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String voltarTelaAutoridades() throws ArqException{
		
		if(vindoPaginaPesquisa){
			CatalogaAutoridadesMBean bean = getMBean("catalogaAutoridadesMBean");
		    
			if("buscaSimples".equals(bean.getValorAbaPesquisa()))
				return bean.pesquisarAutoridadeSimples();
			else
				return bean.pesquisarAutoridadeMultiCampo();
			
		}else{
			BuscaCatalogacoesIncompletasMBean bean =  getMBean("buscaCatalogacoesIncompletasMBean");
		
			if(bean.isPesquisaImportacao()){
				bean.setAutoridadesIncompletas(null); // para buscar novamente
				return bean.iniciarBuscaAutoridadesIncompletasImportacao();
			}else{
				bean.setAutoridadesIncompletas(null); // para buscar novamente
				return bean.iniciarBuscaAutoridadesIncompletas();
			}
		}
		
	}
	
	
	/**
	 * 
	 *    Retorna a tela de artigos de onde foi chamado
	 *
	 *    Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/confirmaRemocaoArtigo.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String voltarTelaArtigos() throws ArqException{

		if (voltarParaRequest) {
			return forward(requestURL);
		}
		
		if(vindoPaginaPesquisa){
			PesquisarArtigoMBean bean =  getMBean("pesquisarArtigoMBean");
		    
			return bean.pesquisar();
			
		}else{
			// Op��o n�o mais existente
//			PesquisarFasciculoMBean bean =  getMBean("pesquisarFasciculoMBean");
//		
//			return bean.mostarInformacoesArtigosVindoDaRemocao();
			return null;
			
		}
		
	}
	
	
	
	
	/**
	 *   Apaga as opera��es antes de seta uma nova
	 */
	private void zeraOperacoes(){
		 vindoPaginaPesquisa = false;
		 vindoPaginaCatalogacoesIncomplates = false;
	}

	
	public boolean isVindoPaginaPesquisa() {
		return vindoPaginaPesquisa;
	}

	public void setVindoPaginaPesquisaTitulo(boolean vindoPaginaPesquisa) {
		this.vindoPaginaPesquisa = vindoPaginaPesquisa;
	}

	public boolean isVindoPaginaCatalogacoesIncomplates() {
		return vindoPaginaCatalogacoesIncomplates;
	}

	public void setVindoPaginaCatalogacoesIncomplates(boolean vindoPaginaCatalogacoesIncomplates) {
		this.vindoPaginaCatalogacoesIncomplates = vindoPaginaCatalogacoesIncomplates;
	}

	public boolean isRemocaoDeTitulos() {
		return remocaoDeTitulos;
	}

	public boolean isRemocaoDeAutoridades() {
		return remocaoDeAutoridades;
	}

	public boolean isRemocaoDeArtigos() {
		return remocaoDeArtigos;
	}
	

	
	
}
