/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 09/06/2009
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ExemplarDao;
import br.ufrn.sigaa.arq.dao.biblioteca.FasciculoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.NotaCirculacaoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.SituacaoMaterialInformacionalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.NotaCirculacao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.StatusMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;
import br.ufrn.sigaa.biblioteca.circulacao.jsf.NotasCirculacaoMBean;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoAtualizaExemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoAtualizaFasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoDarBaixaExemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoDarBaixaFasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoDesfazerBaixaExemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoDesfazerBaixaFasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoRemoverMateriaisAcervo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisDeUmTituloMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarExemplarMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarFasciculoMBean;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 *     MBean para gerenciar tanto a página de edição de fascículos quanto a de edição de exemplares.
 *
 * @author jadson
 * @since 22/04/2009
 * @version 1.0 criação da classe
 *
 */
@Component(value="editaMaterialInformacionalMBean")
@Scope(value="request")
public class EditaMaterialInformacionalMBean extends SigaaAbstractController<Object>{
	
	/** Diretório base para as páginas. */
	private static final String BASE = "/biblioteca/processos_tecnicos/catalogacao/";
	
	/** Página de edição de um fascículo. */
	public static final String PAGINA_EDICAO_FASCICULO = BASE + "paginaEdicaoFasciculo.jsp";
	/** Página de edição de um exemplar. */
	public static final String PAGINA_EDICAO_EXEMPLAR = BASE + "paginaEdicaoExemplar.jsp";
	
	/** Página para o processo de baixa de um exemplar. */
	public static final String PAGINA_CONFIRMA_BAIXA_EXEMPLAR = BASE + "paginaDarBaixaExemplar.jsp";
	/** Página para o processo de baixa de um fascículo. */
	public static final String PAGINA_CONFIRMA_BAIXA_FASCICULO = BASE + "paginaDarBaixaFasciculo.jsp";

	/** Página para cancelamento da baixa de um exemplar. */
	public static final String PAGINA_CONFIRMA_DESFAZER_BAIXA_EXEMPLAR = BASE + "paginaDesfazerBaixaExemplar.jsp";
	/** Página para cancelamento da baixa de um fascículo. */
	public static final String PAGINA_CONFIRMA_DESFAZER_BAIXA_FASCICULO = BASE + "paginaDesfazerBaixaFasciculo.jsp";

	/** Página para a confirmação da remoção de um exemplar. */
	public static final String PAGINA_CONFIRMA_REMOCAO_EXEMPLAR = BASE + "paginaRemocaoExemplar.jsp";
	/** Página para a confirmação da remoção de um fascículo. */
	public static final String PAGINA_CONFIRMA_REMOCAO_FASCICULO = BASE + "paginaRemocaoFasciculo.jsp";
	
	/* Os objetos que vão ser editados */
	/** Exemplar sendo editado. */
	private Exemplar exemplarEdicao;
	/** Fascículo sendo editado. */
	private Fasciculo fasciculoEdicao;
	
	/** A nota de circulação do material sendo editado, se houver */
	private NotaCirculacao notaCirculacao;
	
	/** 
	 *  Guarda os ids Escolhidos pelo usuário na página com o selectManyList, quanto
	 *  as formas de documento que um material possui. 
	 */
	private Collection<String> idsFormasDocumentoEscolhidos;
	
	// Indica quais dos dois materiais está sendo editado agora, importante para a operação de baixa:
	/** Indica se o que está sendo editado é um exemplar. */
	private boolean editandoExemplar = false;
	/** Indica se o que está sendo editado */
	private boolean editandoFasciculo = false;
	
	/** Indica se, ao voltar, volta para a página de detalhes do exemplar. */
	private boolean retornaPaginaDetalhesExemplar = false;
	
	/** Indica se, ao voltar, volta para a página de detalhes do fascículo. */
	private boolean retornaPaginaDetalhesFasciculo = false;
	
	/**
	 * Indica quando usuário chegou na página de alterar as informações dos exemplares a partir
	 * da página de inclusão de exemplares. Nesse caso deve retorna para a página de inclusão.
	 */
	private boolean retornaPaginaIncluirExemplar = false;
	
	/**
	 * Indica que o botão voltar na página deve voltar para a tela de catalogação.
	 */
	private boolean retornaCatalogacao = false;
	
	/** As informações sobre o título, vindas do cache. */
	private CacheEntidadesMarc titulo;
	
	/**   Se true, volta para a página que chamou a atualização, senão fica na mesma página. */
	private String finalizarAtualizacao = "false";
	
	
	/** Informa a nova situação que o material deve ter após ter a baixa cancelada. */
	private int idNovaSituacao;
	
	/** Guarda a página de onde a página de edição de exemplares foi chamada, deve retornar para essa página depois da inclusão da nota de circulação.*/
	private String paginaChamouEdicaoMaterial;
	
	
	/**
	 * <p>Recupera a URL que chamou essa Caso de USO e separa a parte a partir de /biblioteca/, essa será a página para onde o 
	 * caso de uso de nota de circulação deve voltar depois de incluir a nota.</p>
	 *
	 */
	private void configuraPaginaChamouEdicaoMaterial(){
		paginaChamouEdicaoMaterial = StringUtils.isNotEmpty(getCurrentURL()) ? getCurrentURL().substring(getCurrentURL().indexOf("/biblioteca/"), getCurrentURL().length()) : "";
	}
	
	
	
	/**
	 * 
	 * Inicia o caso de uso para edição de um exemplar
	 *
	 * <br/><br/>
	 * Chamado a partir das páginas: <br>
	 * 		/sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaInclusaoExemplares.jsp <br>
	 * 		/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp <br>
	 * 		/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/resultadoPesquisaExemplar.jsp <br>
	 */
	public String iniciarParaEdicaoExemplares() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		setOperacaoEditarExemplar();
		
		prepareMovimento(SigaaListaComando.ATUALIZA_EXEMPLAR);
		
		configuraPaginaChamouEdicaoMaterial();
		
		Integer idExemplar = getParameterInt("idExemplarParaEdicao");
		
		if(idExemplar == null)	
			idExemplar = (Integer) getCurrentRequest().getAttribute("idExemplarParaEdicao");
		
		zeraOpcoesRetorno();
		
		if(getParameterBoolean("retornaPaginaDetalhesExemplar") != null)
			retornaPaginaDetalhesExemplar = getParameterBoolean("retornaPaginaDetalhesExemplar");
		
		if(getParameterBoolean("retornaPaginaInclusaoExemplar") != null   )
			retornaPaginaIncluirExemplar = getParameterBoolean("retornaPaginaInclusaoExemplar");
		
		exemplarEdicao = getDAO(ExemplarDao.class).findExemplarAtivosByIDInicializandoRelacionamentos(idExemplar);
		configurarNotaCirculacao(exemplarEdicao);
		setIdsFormasDocumentoEscolhidos(exemplarEdicao.extrairFormasDocumento());
		
		titulo = BibliotecaUtil.obtemDadosTituloCache(exemplarEdicao.getTituloCatalografico().getId());
		
		return telaEdicaoExemplar();
		
	}



	/**
	 * 
	 * <p>Inicia o caso de uso para edição de um exemplar a partir da página de catalogação.</p>
	 *
	 * <p>Chamado da arvore de materiais que fica no menu lateral da tela de catalogação.</p>
	 * 
	 *  <p>Método não chamado por nenhuma página jsp.</p>
	 * 
	 * @throws IOException 
	 */
	public String iniciarParaEdicaoExemplaresVindoPaginaCatalogacao(int idExemplar) throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		setOperacaoEditarExemplar();
		
		prepareMovimento(SigaaListaComando.ATUALIZA_EXEMPLAR);
		
		zeraOpcoesRetorno();
		
		retornaCatalogacao = true;
		
		exemplarEdicao = getDAO(ExemplarDao.class).findExemplarAtivosByIDInicializandoRelacionamentos(idExemplar);
		
		if(exemplarEdicao != null){
		
			configurarNotaCirculacao(exemplarEdicao);
			setIdsFormasDocumentoEscolhidos(exemplarEdicao.extrairFormasDocumento());
			
			titulo = BibliotecaUtil.obtemDadosTituloCache(exemplarEdicao.getTituloCatalografico().getId());
			return forward(PAGINA_EDICAO_EXEMPLAR);
			
		}else{
			addMensagemErro("Exemplar não foi selecionado corretamente.");
			return null;
		}
		
	}
	
	
	

	
	/**
	 * 
	 * <p>Inicia o caso de uso para edição de um fascículo vindo da tela de catalogação.</p>
	 *
	 * <p>Chamado da arvore de materiais que fica no menu lateral da tela de catalogação.</p>
	 *
	 * <br/>
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 * 
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarParaEdicaoFasciculosVindoPaginaCatalogaca(int idFasciculo) throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		setOperacaoEditarFasciculo();
		
		prepareMovimento(SigaaListaComando.ATUALIZA_FASCICULO);
		
		zeraOpcoesRetorno();
		
		retornaCatalogacao = true;
		
		fasciculoEdicao = getDAO(FasciculoDao.class).findFasciculoAtivosByIDInicializandoRelacionamentos(idFasciculo);
		
		if(fasciculoEdicao != null){
			configurarNotaCirculacao(fasciculoEdicao);
			setIdsFormasDocumentoEscolhidos(fasciculoEdicao.extrairFormasDocumento());
			
			titulo = BibliotecaUtil.obtemDadosTituloCache(fasciculoEdicao.getAssinatura().getTituloCatalografico().getId());
			
			return telaEdicaoFasciculo();
		}else{
			addMensagemErro("Fascículo não foi selecionado corretamente.");
			return null;
		}
		
	}
	
	
	/**
	 * 
	 * Inicia o caso de uso para edição de um fascículo
	 *
	 * <br/><br/>
	 * Chamado a partir das páginas: <ul>
	 * 								 <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp</li>
	 * 							   	 <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/resultadoPesquisaFasciculo.jsp</li>
	 * 								 </ul>
	 * <br/>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarParaEdicaoFasciculos() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		setOperacaoEditarFasciculo();
		
		configuraPaginaChamouEdicaoMaterial();
		
		prepareMovimento(SigaaListaComando.ATUALIZA_FASCICULO);
		
		int idFasciculo = getParameterInt("idFasciculoParaEdicao");
		
		if(getParameterBoolean("retornaPaginaDetalhesFasciculo") != null)
			retornaPaginaDetalhesFasciculo = getParameterBoolean("retornaPaginaDetalhesFasciculo");
		
		fasciculoEdicao = getDAO(FasciculoDao.class).findFasciculoAtivosByIDInicializandoRelacionamentos(idFasciculo);
		configurarNotaCirculacao(fasciculoEdicao);
		setIdsFormasDocumentoEscolhidos(fasciculoEdicao.extrairFormasDocumento());
		
		titulo = BibliotecaUtil.obtemDadosTituloCache(fasciculoEdicao.getAssinatura().getTituloCatalografico().getId());
		
		
		return telaEdicaoFasciculo();
		
	}
	
	/**
	 * 
	 *   Inicia o caso de uso de baixar exemplares do acervo.
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/resultadoPesquisaExemplar.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciaParaBaixaExemplar() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.DAR_BAIXA_EXEMPLAR);
		
		int idExemplar = getParameterInt("idExemplarBaixa");
		
		zeraOpcoesRetorno();
		
		setOperacaoEditarExemplar();
		
		exemplarEdicao = getGenericDAO().findByPrimaryKey(idExemplar, Exemplar.class);
		
		titulo = BibliotecaUtil.obtemDadosTituloCache(exemplarEdicao.getTituloCatalografico().getId());
		
		return telaConfirmaBaixaExemplar();
	}
	
	/**
	 * <p>Inicia o caso de uso de desfazer baixa de exemplares.</p>
	 * 
	 * <p> Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	 <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/resultadoPesquisaExemplar.jsp</li>
	 *   </ul>
	 * </p>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciaParaDesfazerBaixaExemplar() throws ArqException {
		
		checkRole(
				SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.DESFAZER_BAIXA_EXEMPLAR);
		
		int idExemplar = getParameterInt("idExemplarBaixa");
		
		zeraOpcoesRetorno();
		
		setOperacaoEditarExemplar();
		
		exemplarEdicao = getGenericDAO().findByPrimaryKey(idExemplar, Exemplar.class);
		
		titulo = BibliotecaUtil.obtemDadosTituloCache(exemplarEdicao.getTituloCatalografico().getId());
		
		idNovaSituacao = 0;
		
		return telaConfirmaDesfazerBaixaExemplar();
	}
	
	/**
	 * 
	 *   Inicia o caso de uso de baixar fascículos do acervo.
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/resultadoPesquisaFasciculo.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciaParaBaixaFasciculo() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.DAR_BAIXA_FASCICULO);
		
		int idFasciculo = getParameterInt("idFasciculoBaixa");
		
		zeraOpcoesRetorno();
		
		setOperacaoEditarFasciculo();
		
		fasciculoEdicao = getGenericDAO().findByPrimaryKey(idFasciculo, Fasciculo.class);
		
		titulo = BibliotecaUtil.obtemDadosTituloCache(fasciculoEdicao.getAssinatura().getTituloCatalografico().getId());
		
		return telaConfirmaBaixaFasciculo();
	}
	
	/**
	 * <p>Inicia o caso de uso de desfazer baixa de um fascículo.</p>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/resultadoPesquisaFasciculo.jsp</li>
	 * </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciaParaDesfazerBaixaFasciculo() throws ArqException{
		
		checkRole(
				SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, 
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.DESFAZER_BAIXA_FASCICULO);
		
		int idFasciculo = getParameterInt("idFasciculoBaixa");
		
		zeraOpcoesRetorno();
		
		setOperacaoEditarFasciculo();
		
		fasciculoEdicao = getGenericDAO().findByPrimaryKey(idFasciculo, Fasciculo.class);
		
		titulo = BibliotecaUtil.obtemDadosTituloCache(fasciculoEdicao.getAssinatura().getTituloCatalografico().getId());
		
		idNovaSituacao = 0;
		
		return telaConfirmaDesfazerBaixaFasciculo();
	}
	
	/**
	 * 
	 *   Inicia o caso de uso de remover exemplares do acervo.
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/resultadoPesquisaExemplar</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciaParaRemocaoExemplar() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.REMOVER_MATERIAIS_ACERVO);
		
		int idExemplar = getParameterInt("idExemplarRemocao");
		
		setOperacaoEditarExemplar();
		
		exemplarEdicao = getGenericDAO().findByPrimaryKey(idExemplar, Exemplar.class);
		
		titulo = BibliotecaUtil.obtemDadosTituloCache(exemplarEdicao.getTituloCatalografico().getId());
		
		return telaConfirmaRemocaoExemplar();
	}
	
	
	
	
	/**
	 * 
	 *   Inicia o caso de uso de remover fascículos  do acervo.
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/resultadoPesquisaFasciculo.jsp</li>
	 *   </ul>
	 */
	public String iniciaParaRemocaoFasciculo() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.REMOVER_MATERIAIS_ACERVO);
		
		int idFasciculo = getParameterInt("idFasciculoRemocao");
		
		setOperacaoEditarFasciculo();
		
		fasciculoEdicao = getGenericDAO().findByPrimaryKey(idFasciculo, Fasciculo.class);
		
		titulo = BibliotecaUtil.obtemDadosTituloCache(fasciculoEdicao.getAssinatura().getTituloCatalografico().getId());
		
		return telaConfirmaRemocaoFasciculo();
	}
	
	
	
	
	/**
	 * Método que zera todas as opções de retorno da página.
	 * <p>
	 * Geralmente para ser chamado antes de setar uma nova opção de retorno
	 */
	private void zeraOpcoesRetorno(){
		retornaPaginaDetalhesExemplar = false;
		retornaPaginaDetalhesFasciculo = false;
		retornaPaginaIncluirExemplar = false;
		retornaCatalogacao = false;
	}
	
	
	/**
	 * Configura a operação de edição correta.
	 */
	public void setOperacaoEditarExemplar(){
		editandoExemplar = true;
		editandoFasciculo = false;
	}
	
	/**
	 * Configura a operação de edição correta.
	 */
	public void setOperacaoEditarFasciculo(){
		editandoExemplar = false;
		editandoFasciculo = true;
	}
	
	/**
	 *  Método responsável por atualizar os dados de um exemplar.
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaEdicaoFasciculo.jsp
	 */
	public String atualizarExemplar() throws ArqException{
		
		if(usuarioPreencheuDadosExemplarCorretamente()){
			
			try{
				exemplarEdicao.popularFormasDocumento(getIdsFormasDocumentoEscolhidos());
				MovimentoAtualizaExemplar mov = new MovimentoAtualizaExemplar(exemplarEdicao, null);
				mov.setCodMovimento(SigaaListaComando.ATUALIZA_EXEMPLAR);
				
				execute(mov);
			
			}catch(NegocioException ne){
				addMensagens(ne.getListaMensagens());
				return null;
			}
			
			
			addMensagemInformation("Exemplar atualizado com sucesso");
			
			if("true".equals(finalizarAtualizacao)){
				return voltarPaginaExemplar(); // volta para a página que chamou
			}else{
				prepareMovimento(SigaaListaComando.ATUALIZA_EXEMPLAR); // usuário apenas salvou, então fica na mesma página e libera para salvar novamente.
				return null;
			}
			
		}else{
			return null;  // fica na mesma tela mostrando as mensagens de erro no preenchimento do formulário
		}
		
	}
	
	
	/**
	 *     Método responsável por atualizar os dados de um fascículo.
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaEdicaoFasciculo.jsp
	 */
	public String atualizarFasciculo() throws ArqException{
		
		if(usuarioPreencheuDadosFasciculoCorretamente()){
			
			try{
				fasciculoEdicao.popularFormasDocumento(getIdsFormasDocumentoEscolhidos());
				MovimentoAtualizaFasciculo mov = new MovimentoAtualizaFasciculo(fasciculoEdicao, null);
				mov.setCodMovimento(SigaaListaComando.ATUALIZA_FASCICULO);
				
				execute(mov);
			
			}catch(NegocioException ne){
				addMensagens(ne.getListaMensagens());
				return null;
			}
			
			
			addMensagemInformation("Fascículo atualizado com sucesso.");
			
			if("true".equals(finalizarAtualizacao)){
				return voltarPaginaFasciculo(); // volta para a página que o chamou
			}else{
				prepareMovimento(SigaaListaComando.ATUALIZA_FASCICULO); // usuário apenas salva, então fica na mesma página e libera para salvar novamente.
				return null;
			}
			
			
		}else{
			return null;  // fica na mesma tela mostrando as mensagens de erro no preenchimento do formulário
		}
		
	}
	
	
	/**
	 * <p>Método que realiza a operação de dar baixa em um material </p>
	 * 
	 * <br/><br/>
	 * Chamado a partir da página:
	 * <ul>
	 * <li> /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaDarBaixaExemplar.jsp  </li>
	 * <li> /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaDarBaixaFasciculo.jsp </li>
	 * </ul>
	 */
	public String darBaixaMaterial() throws ArqException{
		
		try{
			if(editandoExemplar){
				
				if(usuarioPreencheuDadosExemplarCorretamente()){
					MovimentoDarBaixaExemplar movimento = new MovimentoDarBaixaExemplar(exemplarEdicao);
					movimento.setCodMovimento(SigaaListaComando.DAR_BAIXA_EXEMPLAR);
					
					execute(movimento);
			
					addMensagemInformation("Baixa do exemplar realizada com sucesso.");
					
					return voltarPaginaExemplar(); // volta para a página que o chamou
					
				}else{
					return null;  // fica na mesma tela mostrando as mensagens de erro no preenchimento do formulário
				}
				
				
			}
			
			if(editandoFasciculo){
				
				if(usuarioPreencheuDadosFasciculoCorretamente()){
				
					MovimentoDarBaixaFasciculo movimento = new MovimentoDarBaixaFasciculo(fasciculoEdicao);
					movimento.setCodMovimento(SigaaListaComando.DAR_BAIXA_FASCICULO);
					
					execute(movimento);
					
					addMensagemInformation("Baixa do fascículo realizada com sucesso.");
					
					return voltarPaginaFasciculo(); // volta para a página que o chamou
					
				}else{
					return null;  // fica na mesma tela mostrando as mensagens de erro no preenchimento do formulário
				}
			}
			
		}catch(NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		return null; // nunca era para chegar aqui.
		
	}
	
	/**
	 * <p>Método que realiza a operação de desfazer baixa de um material.</p>
	 * 
	 * Chamado a partir da página:
	 * <ul>
	 *   <li> /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaDesfazerBaixaExemplar.jsp  </li>
	 *   <li> /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaDesfazerBaixaFasciculo.jsp </li>
	 * </ul>
	 */
	public String desfazerBaixaMaterial() throws ArqException {
		
		try {
			
			if (editandoExemplar) {
				
				if ( idNovaSituacao <= 0 ) {
					addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nova Situação");
					return null;
				}
				
				SituacaoMaterialInformacional sit = getGenericDAO().
						findByPrimaryKey(idNovaSituacao, SituacaoMaterialInformacional.class);
				
				MovimentoDesfazerBaixaExemplar movimento = new MovimentoDesfazerBaixaExemplar(
						exemplarEdicao, sit );
				movimento.setCodMovimento(SigaaListaComando.DESFAZER_BAIXA_EXEMPLAR);
				
				execute(movimento);
		
				addMensagemInformation("Baixa do exemplar desfeita com sucesso.");
				
				return voltarPaginaExemplar(); // volta para a página que o chamou
				
			}
			
			if (editandoFasciculo) {
				
				if ( idNovaSituacao <= 0 ) {
					addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nova Situação");
					return null;
				}
				
				SituacaoMaterialInformacional sit = getGenericDAO().
						findByPrimaryKey(idNovaSituacao, SituacaoMaterialInformacional.class);
		
				MovimentoDesfazerBaixaFasciculo movimento = new MovimentoDesfazerBaixaFasciculo(
						fasciculoEdicao, sit );
				movimento.setCodMovimento(SigaaListaComando.DESFAZER_BAIXA_FASCICULO);
				
				execute(movimento);
				
				addMensagemInformation("Baixa do fascículo desfeita com sucesso.");
				
				return voltarPaginaFasciculo(); // volta para a página que o chamou
				
			}
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		return null; // nunca era para chegar aqui.
		
	}
	
	/**
	 * <p>Método que realizar a operação de apagar (desativar) um material do acervo</p>
	 * 
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaEdicaoExemplar.jsp/</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaEdicaoFasciculo.jsp/</li>
	 *   </ul>
	 */
	public String apagarMaterial() throws ArqException{
		
		try{
			if(editandoExemplar){
				
				if(usuarioPreencheuDadosExemplarCorretamente()){
					MovimentoRemoverMateriaisAcervo movimento = new MovimentoRemoverMateriaisAcervo(exemplarEdicao);
					movimento.setCodMovimento(SigaaListaComando.REMOVER_MATERIAIS_ACERVO);
					
					execute(movimento);
			
					addMensagemInformation("Remoção do Exemplar realizada com sucesso.");
					
					return voltarPaginaExemplar(); // volta para a página que o chamou
					
				}else{
					return null;  // fica na mesma tela mostrando as mensagens de erro no preenchimento do formulário
				}
				
				
			}
			
			if(editandoFasciculo){
				
				if(usuarioPreencheuDadosFasciculoCorretamente()){
				
					MovimentoRemoverMateriaisAcervo movimento = new MovimentoRemoverMateriaisAcervo(fasciculoEdicao);
					movimento.setCodMovimento(SigaaListaComando.REMOVER_MATERIAIS_ACERVO );
					
					execute(movimento);
					
					addMensagemInformation("Remoção do Fascículo realizada com sucesso.");
					
					return voltarPaginaFasciculo(); // volta para a página que o chamou
					
				}else{
					return null;  // fica na mesma tela mostrando as mensagens de erro no preenchimento do formulário
				}
			}
			
		}catch(NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		return null; // nunca era para chegar aqui.
		
	}
	
	/**
	 * Inicializa a nota de circulação a partir do material sendo editado.
	 * 
	 * @param material
	 * @throws ArqException
	 */
	private void configurarNotaCirculacao(MaterialInformacional material) throws ArqException {
		List<NotaCirculacao> notas = getDAO(NotaCirculacaoDao.class).getNotasAtivasDoMaterial(material.getId());
		
		if (notas.size() > 0) {
			notaCirculacao = notas.get(0);

			notaCirculacao.setMaterial(material);
		}
		else {
			notaCirculacao = null;
		}
	}
	
	/**
	 *   <p>Método que encaminha para o caso de uso de incluir uma nota de circulação no exemplar que
	 *  está sendo editado nesse momento. </p>
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaEdicaoExemplar.jsp
	 */
	public String incluirNotaCirculacaoExemplar() throws ArqException{
		
		NotasCirculacaoMBean mbean = getMBean("notasCirculacaoMBean");
		List<MaterialInformacional> materiais = new ArrayList<MaterialInformacional>();
		materiais.add( exemplarEdicao );
		return mbean.iniciarConfigurandoDiretamenteMaterialIncluirNota(materiais, paginaChamouEdicaoMaterial);
		
	}
	
	/**
	 *     <p>Método que encaminha para o caso de uso de incluir uma nota de circulação no fascículo que
	 *  está sendo editado nesse momento. </p>
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaEdicaoFasciculo.jsp
	 */
	public String incluirNotaCirculacaoFasciculo() throws ArqException{
		
		NotasCirculacaoMBean mbean = getMBean("notasCirculacaoMBean");
		List<MaterialInformacional> materiais = new ArrayList<MaterialInformacional>();
		materiais.add( fasciculoEdicao );
		return mbean.iniciarConfigurandoDiretamenteMaterialIncluirNota(materiais, paginaChamouEdicaoMaterial);
		
	}
	
	/**
	 * Remove a nota de circulação do exemplar sendo editado.
	 * Utilizado na página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaEdicaoExemplar.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String removerNotaCirculacaoExemplar() throws ArqException {

		NotasCirculacaoMBean mbean = getMBean("notasCirculacaoMBean");
		mbean.desbloquearRemoverNota(notaCirculacao);		
		configurarNotaCirculacao(exemplarEdicao);
		return telaEdicaoExemplar();

	}
	
	/**
	 * Remove a nota de circulação do fascículo sendo editado.
	 * Utilizado na página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaEdicaoFasciculo.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String removerNotaCirculacaoFasciculo() throws ArqException {

		NotasCirculacaoMBean mbean = getMBean("notasCirculacaoMBean");
		mbean.desbloquearRemoverNota(notaCirculacao);		
		configurarNotaCirculacao(fasciculoEdicao);
		return telaEdicaoFasciculo();

	}
	
	
	/**
	 *   Método que verifica de qual a página a alteração foi chamada.
	 *   É o lugar para o qual deve-se voltar depois que a atualização for feita.
	 * 
	 *  <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaEdicaoFasciculo.jsp
	 */
	public String voltarPaginaFasciculo() throws DAOException{
		
		if(retornaPaginaDetalhesFasciculo){
			DetalhesMateriaisDeUmTituloMBean bean = getMBean("detalhesMateriaisDeUmTituloMBean");
			return bean.telaInformacoesMateriais();
		}
		
		if(retornaCatalogacao){
			CatalogacaoMBean bean = getMBean("catalogacaoMBean");
			return bean.telaDadosTituloCatalografico();
		}
		
		// se não tiver setado nenhuma opção diferente, volta para a tela de pesquisa de fascículos
		PesquisarFasciculoMBean bean = getMBean("pesquisarFasciculoMBean");
		return bean.pesquisar();
		
	}
	

	/**
	 *   Método que verifica de qual página a alteração foi chamada.
	 *   É o lugar para o qual deve-se voltar depois que a atualização for feita.
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaEdicaoExemplar.jsp
	 */
	public String voltarPaginaExemplar() throws ArqException{
		
		if(retornaPaginaIncluirExemplar){
			
			MaterialInformacionalMBean bean = getMBean("materialInformacionalMBean");
			
			return bean.voltaTelaIncluirNovoMaterial(); //atualiza os dado da página para o exemplar que foi baixado não aparecer novamente.
			
			//return bean.inativar();
			
			///return forward(MaterialInformacionalMBean.PAGINA_INCLUSAO_MATERIAIS);
		}
		
		if(retornaPaginaDetalhesExemplar){
			/* A página de detalhes faz uma nova busca para não aparecer mais o exemplar que
			 * acabou de ser dado baixa */
			DetalhesMateriaisDeUmTituloMBean bean = getMBean("detalhesMateriaisDeUmTituloMBean");
			return bean.telaInformacoesMateriais();
		}
		
		
		if(retornaCatalogacao){
			CatalogacaoMBean bean = getMBean("catalogacaoMBean");
			return bean.telaDadosTituloCatalografico();
		}
		
		// se não tiver setado nenhuma opção diferente, volta para a tela de pesquisa de exemplares
		PesquisarExemplarMBean bean = getMBean("pesquisarExemplarMBean");
		return bean.pesquisar();
	}
	
	
	
	
	/**
	 * Verifica se os dados do fascículo foram informados corretamente
	 */
	private boolean usuarioPreencheuDadosFasciculoCorretamente(){

		boolean contenErro = false;
		
		if ( StringUtils.isEmpty( fasciculoEdicao.getCodigoBarras() ) ){
			addMensagemErro("É preciso informar o código de barras do fascículo ");
			contenErro = true;
		}
		
		
		if ( StringUtils.isEmpty( fasciculoEdicao.getNumeroChamada() ) ){
			addMensagemErro("É preciso informar o número de chamada do fascículo ");
			contenErro = true;
		}
		
		if ( fasciculoEdicao.getColecao().getId() == -1){
			addMensagemErro("É preciso informar a coleção do fascículo ");
			contenErro = true;
		}
		
		if ( fasciculoEdicao.getStatus().getId() == -1){
			addMensagemErro("É preciso informar o status do fascículo ");
			contenErro = true;
		}
		
		if ( fasciculoEdicao.getSituacao().getId() == -1){
			addMensagemErro("É preciso informar a situação do fascículo");
			contenErro = true;
		}
		
		if ( fasciculoEdicao.getBiblioteca().getId() == -1){
			addMensagemErro("É preciso informar a  biblioteca onde o fascículo está localizado");
			contenErro = true;
		}
		
		if( fasciculoEdicao.getTipoMaterial().getId() == -1){
			addMensagemErro("É preciso informar o tipo de material do fascículo");
			contenErro = true;
		}
		
		if( fasciculoEdicao.getNotaGeral() != null && fasciculoEdicao.getNotaGeral().length() > 300){
			addMensagemErro("O tamanho máximo do campo Nota Geral é de 300 caracteres.");
			contenErro = true;
		}
		
		if( fasciculoEdicao.getNotaUsuario() != null && fasciculoEdicao.getNotaUsuario().length() > 300 ){
			addMensagemErro("O tamanho máximo do campo Nota ao Usuário é de 300 caracteres.");
			contenErro = true;
		}
		
		return ! contenErro ;
	}
	
	
	
	
	/**
	 * Verifica se os dados do exemplar foram informados corretamente
	 */
	private boolean usuarioPreencheuDadosExemplarCorretamente(){

		boolean contenErro = false;
		
		if ( StringUtils.isEmpty( exemplarEdicao.getCodigoBarras() ) ){
			addMensagemErro("É preciso informar o código de barras do exemplar ");
			contenErro = true;
		}
		
		if ( StringUtils.isEmpty( exemplarEdicao.getNumeroChamada() ) ){
			addMensagemErro("É preciso informar o número de chamada do exemplar ");
			contenErro = true;
		}
		
		if ( exemplarEdicao.getColecao().getId() == -1){
			addMensagemErro("É preciso informar a coleção do exemplar ");
			contenErro = true;
		}
		
		if ( exemplarEdicao.getStatus().getId() == -1){
			addMensagemErro("É preciso informar o status do exemplar ");
			contenErro = true;
		}
		
		if ( exemplarEdicao.getSituacao().getId() == -1){
			addMensagemErro("É preciso informar a situação do exemplar");
			contenErro = true;
		}
		
		if ( exemplarEdicao.getBiblioteca().getId() == -1){
			addMensagemErro("É preciso informar a  biblioteca onde o exemplar está localizado");
			contenErro = true;
		}
		
		if( exemplarEdicao.getTipoMaterial().getId() == -1){
			addMensagemErro("É preciso informar o tipo de material do exemplar");
			contenErro = true;
		}
		
		if( exemplarEdicao.getNotaGeral() != null && exemplarEdicao.getNotaGeral().length() > 300){
			addMensagemErro("O tamanho máximo do campo Nota Geral é de 300 caracteres.");
			contenErro = true;
		}
		
		if( exemplarEdicao.getNotaUsuario() != null && exemplarEdicao.getNotaUsuario().length() > 300 ){
			addMensagemErro("O tamanho máximo do campo Nota ao Usuário é de 300 caracteres.");
			contenErro = true;
		}
		
		return ! contenErro ;
	}
	
	
	
	// telas de navegação
	
	/** Redireciona para a tela de edição de exemplar. */
	private String telaEdicaoExemplar() {
		return forward(PAGINA_EDICAO_EXEMPLAR);
	}
	
	/** Redireciona para a tela de edição de fascículo. */
	private String telaEdicaoFasciculo() {
		return forward(PAGINA_EDICAO_FASCICULO);
	}

	/** Redireciona para a tela de confirmação de baixa de um exemplar. */
	private String telaConfirmaBaixaExemplar() {
		return forward(PAGINA_CONFIRMA_BAIXA_EXEMPLAR);
	}
	
	/** Redireciona para a tela de confirmação de baixa de um fascículo. */
	private String telaConfirmaBaixaFasciculo() {
		return forward(PAGINA_CONFIRMA_BAIXA_FASCICULO);
	}
	
	/** Redireciona para a tela de confirmação de cancelamento de baixa de um exemplar. */
	private String telaConfirmaDesfazerBaixaExemplar() {
		return forward(PAGINA_CONFIRMA_DESFAZER_BAIXA_EXEMPLAR);
	}
	
	/** Redireciona para a tela de confirmação de cancelamento de baixa de um fascículo. */
	private String telaConfirmaDesfazerBaixaFasciculo() {
		return forward(PAGINA_CONFIRMA_DESFAZER_BAIXA_FASCICULO);
	}
	
	/** Redireciona para a tela de confirmação de remoção de um exemplar. */
	private String telaConfirmaRemocaoExemplar() {
		return forward(PAGINA_CONFIRMA_REMOCAO_EXEMPLAR);
	}
	
	/** Redireciona para a tela de confirmação de remoção de um fascículo. */
	private String telaConfirmaRemocaoFasciculo() {
		return forward(PAGINA_CONFIRMA_REMOCAO_FASCICULO);
	}
	

	/////////////////   os método dos combobox da tela   //////////////////////////
	
	
	
	/**
	 * 
	 *    Retorna todas as bibliotecas internas.
	 *    No caso da alteração, aparece todas as bibliotecas porque o usuário pode alterar a biblioteca
	 *    do material.
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getBibliotecasInternas() throws DAOException{
		Collection <Biblioteca> b = getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivas();
		return toSelectItems(b, "id", "descricaoCompleta");
	}
	

	/**
	 * 
	 * Retorna todos os status do material dependendo da coleção escolhida
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getStatusAtivos() throws DAOException{
		
		Collection <StatusMaterialInformacional> si
			= getGenericDAO().findByExactField(StatusMaterialInformacional.class, "ativo", true);
		
		return toSelectItems(si, "id", "descricao");
	}

	/**
	 * 
	 * Retorna todos as situações que o usuário pode atribuir a um material
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getSituacoes() throws DAOException{
		Collection <SituacaoMaterialInformacional> si = getDAO(SituacaoMaterialInformacionalDao.class)
									.findSituacoesUsuarioPodeAtribuirMaterial();
		return toSelectItems(si, "id", "descricao");
	}
	
	/**
	 * Retorna as situações que podem ser escolhidas quando um material está sendo tirado
	 * da situação de baixado.
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getSituacosPosDesfazerBaixa() throws DAOException {
		Collection <SituacaoMaterialInformacional> si =
				getDAO(SituacaoMaterialInformacionalDao.class).
				findSituacoesUsuarioPodeAtribuirMaterial();

		return toSelectItems(si, "id", "descricao");
	}
	
	
	/**
	 *    Retorna o id da situação emprestado para gerar um combobox na página apenas com esta situação.
	 *
	 * <br/><br/>
	 * Chamado a partir da página:
	 * 
	 * <ul>
	 * <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaEdicaoFasciculo.jsp</li>
	 * <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaEdicaoExemplar.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Integer getIdSituacaoEmprestado() throws DAOException{
		SituacaoMaterialInformacional situacaoEmprestado = getGenericDAO().findByExactField(
				SituacaoMaterialInformacional.class, "situacaoEmprestado", true, true);
		return situacaoEmprestado.getId();
	}
	
	
	/**
	 * 
	 * Retorna todas as coleções cadastradas.
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getColecoes() throws DAOException{
		Collection <Colecao> c = getGenericDAO().findByExactField(Colecao.class, "ativo", true);
		return toSelectItems(c, "id", "descricaoCompleta");
	}
	
	/**
	 * 
	 * Retorna os tipos de materiais.
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getTiposMaterial() throws DAOException{
		Collection <TipoMaterial> t = getGenericDAO().findByExactField(TipoMaterial.class, "ativo", true);
		return toSelectItems(t, "id", "descricao");
	}
	
	////////////////////////////////////////////////////////////////
	
	
	// sets e gets
	
	public Exemplar getExemplarEdicao() {
		return exemplarEdicao;
	}


	public void setExemplarEdicao(Exemplar exemplarEdicao) {
		this.exemplarEdicao = exemplarEdicao;
	}


	public Fasciculo getFasciculoEdicao() {
		return fasciculoEdicao;
	}


	public void setFasciculoEdicao(Fasciculo fasciculoEdicao) {
		this.fasciculoEdicao = fasciculoEdicao;
	}


	public boolean isRetornaPaginaDetalhesExemplar() {
		return retornaPaginaDetalhesExemplar;
	}


	public void setRetornaPaginaDetalhesExemplar(boolean retornaPaginaDetalhesExemplar) {
		this.retornaPaginaDetalhesExemplar = retornaPaginaDetalhesExemplar;
	}


	public boolean isRetornaPaginaDetalhesFasciculo() {
		return retornaPaginaDetalhesFasciculo;
	}


	public void setRetornaPaginaDetalhesFasciculo(boolean retornaPaginaDetalhesFasciculo) {
		this.retornaPaginaDetalhesFasciculo = retornaPaginaDetalhesFasciculo;
	}


	public CacheEntidadesMarc getTitulo() {
		return titulo;
	}


	public void setTitulo(CacheEntidadesMarc titulo) {
		this.titulo = titulo;
	}


	public String getFinalizarAtualizacao() {
		return finalizarAtualizacao;
	}


	public void setFinalizarAtualizacao(String finalizarAtualizacao) {
		this.finalizarAtualizacao = finalizarAtualizacao;
	}

	public int getIdNovaSituacao() {
		return idNovaSituacao;
	}

	public void setIdNovaSituacao(int idNovaSituacao) {
		this.idNovaSituacao = idNovaSituacao;
	}

	public Collection<String> getIdsFormasDocumentoEscolhidos() {
		return idsFormasDocumentoEscolhidos;
	}
	
	/** Seta as formas de documento escolhidas pelo usuário eliminando os ids negativos que não são formas de documento válidas. */
	public void setIdsFormasDocumentoEscolhidos(Collection<String> idsFormasDocumentoEscolhidos) {
		this.idsFormasDocumentoEscolhidos = new ArrayList<String>();
		
		for (String string : idsFormasDocumentoEscolhidos) {
			if( Integer.parseInt(string) > 0 )
				this.idsFormasDocumentoEscolhidos.add(string);
		}
	}


	public boolean isRetornaCatalogacao() {
		return retornaCatalogacao;
	}


	public void setRetornaCatalogacao(boolean retornaCatalogacao) {
		this.retornaCatalogacao = retornaCatalogacao;
	}



	public NotaCirculacao getNotaCirculacao() {
		return notaCirculacao;
	}
	
}
