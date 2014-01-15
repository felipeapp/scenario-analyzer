/*
 * RemoverEntidadeDoAcervoMBean.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
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
 * MBea que gerencia a remoção de títulos. Apenas título que não possuem materias não baixados (ativos)
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

	/** Constante que define a página de remoção de título. */
	public static final String PAGINA_CONFIRMA_REMOCAO_TITULO = "/biblioteca/processos_tecnicos/catalogacao/confirmaRemocaoTitulo.jsp";
	/** Constante que define a página de remoção de autoridade. */
	public static final String PAGINA_CONFIRMA_REMOCAO_AUTORIDADE = "/biblioteca/processos_tecnicos/catalogacao/confirmaRemocaoAutoridade.jsp";
	/** Constante que define a página de remoção de artigo de periódico. */
	public static final String PAGINA_CONFIRMA_REMOCAO_ARTIGO_DE_PERIODICO = "/biblioteca/processos_tecnicos/catalogacao/confirmaRemocaoArtigoDePeriodico.jsp";

	/** Indica se a requisição ao bean veio da página de pesquisa ou não. */
	private boolean vindoPaginaPesquisa = false;
	/** Indica se a requisição ao bean veio da página de catalogações incompletas ou não. */
	private boolean vindoPaginaCatalogacoesIncomplates = false;
	
	// Variáveis utilizadas no controle das remoções de Títulos, Autoridade e Artigos.
	/** Indica se a remoção é uma remoção de títulos. */
	private boolean remocaoDeTitulos; 
	/** Indica se a remoção é uma remoção de autoridades. */
	private boolean remocaoDeAutoridades;
	/** Indica se a remoção é uma remoção de artigos. */
	private boolean remocaoDeArtigos;

	/** Se true, a operação do botão 'Voltar' redireciona o fluxo de navegação para a página que requisitou a action de edição de artigo. */
	private boolean voltarParaRequest;
	
	/** Armazena a página que requisitou a action de edição de artigo. */
	private String requestURL;
	
	/**
	 *    Obtém as informações do título passado e redireciona para a página onde o usuário vai poder 
	 * confirma a remoção do título.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/resultadoPesquisaTituloCatalografico.jsp
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
			addMensagemErro("O Título já foi removido.");
			return null;
		}
		
		
		return forward(PAGINA_CONFIRMA_REMOCAO_TITULO);
	}
	
	
	
	
	/**
	 *    Obtém as informações do título passado e redireciona para a página onde o usuário vai poder 
	 * confirma a remoção do título.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaTitulosCatalogacaoIncompleta.jsp
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
			addMensagemErro("O Título já foi removido.");
			return null;
		}
		
		return forward(PAGINA_CONFIRMA_REMOCAO_TITULO);
	}
	
	
	/**
	 *    Obtém as informações da autoridade passada e redireciona para a página onde o usuário vai poder 
	 * confirma a remoção da autoridade.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/paginaPesquisaAutoridade.jsp
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
			addMensagemErro("Autoridade já foi removida.");
			return null;
		}
		
		return forward(PAGINA_CONFIRMA_REMOCAO_AUTORIDADE);
	}
	
	/**
	 *    Obtém as informações da autoridade passada e redireciona para a página onde o usuário poderá 
	 * confirmar a remoção da autoridade.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaAutoridadesCatalogacaoIncompleta.jsp
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
			addMensagemErro("Autoridade já foi removida.");
			return null;
		}
		
		return forward(PAGINA_CONFIRMA_REMOCAO_AUTORIDADE);
	}
	
	
	
	/**
	 *    Obtém as informações da autoridade passada e redireciona para a página onde o usuário poderá 
	 * confirmar a remoção do Artigo.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/paginaPesquisaAutoridade.jsp
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
			addMensagemErro("O artigo já foi removido.");
			return null;
		}
		
		return forward(PAGINA_CONFIRMA_REMOCAO_ARTIGO_DE_PERIODICO);
	}
	
	
	
	/**
	 *    Obtém as informações da autoridade passada e redireciona para a página onde o usuário poderá 
	 * confirmar a remoção do fascículo.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesArtigosDeUmFasciculo.jsp
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String telaConfirmaRemocaoVindoPaginaVisualizacaoArtigosDoFasciculo() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.REMOVE_ENTIDADES_DO_ACERVO);
		
		zeraOperacoes();
		
		// aqui no caso é usado para quando vem da página de visualizar as informações de artigos de um fascículo
		vindoPaginaCatalogacoesIncomplates = true;
		
		remocaoDeArtigos = true;
		
		obj = BibliotecaUtil.obtemDadosArtigoCache(getParameterInt("idArtigoRemocao"));
		
		if(obj == null){
			addMensagemErro("O artigo já foi removido.");
			return null;
		}
		
		return forward(PAGINA_CONFIRMA_REMOCAO_ARTIGO_DE_PERIODICO);
	}
	
	
	
	
	/**
	 *    Paga o título do acervo para sempre.
	 *
	 *    Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/confirmaRemocaoTitulo.jsp";
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
				addMensagemInformation("Título com o número do sistema: "+obj.getNumeroDoSistema()+" removido com sucesso.");
			
			if(remocaoDeAutoridades)
				addMensagemInformation("Autoridade com o número do sistema: "+obj.getNumeroDoSistema()+" removido com sucesso.");
			
			if(remocaoDeArtigos)
				addMensagemInformation("Artigo de Periódico com o número do sistema: "+obj.getNumeroDoSistema()+" removido com sucesso.");
			
			
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
	 *    Retorna a tela de títulos de onde foi chamado
	 *
	 *    Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/confirmaRemocaoTitulo.jsp
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
	 *    Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/confirmaRemocaoAutoridade.jsp
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
	 *    Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/confirmaRemocaoArtigo.jsp
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
			// Opção não mais existente
//			PesquisarFasciculoMBean bean =  getMBean("pesquisarFasciculoMBean");
//		
//			return bean.mostarInformacoesArtigosVindoDaRemocao();
			return null;
			
		}
		
	}
	
	
	
	
	/**
	 *   Apaga as operações antes de seta uma nova
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
