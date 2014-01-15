/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 *     MBean para gerenciar tanto a p�gina de edi��o de fasc�culos quanto a de edi��o de exemplares.
 *
 * @author jadson
 * @since 22/04/2009
 * @version 1.0 cria��o da classe
 *
 */
@Component(value="editaMaterialInformacionalMBean")
@Scope(value="request")
public class EditaMaterialInformacionalMBean extends SigaaAbstractController<Object>{
	
	/** Diret�rio base para as p�ginas. */
	private static final String BASE = "/biblioteca/processos_tecnicos/catalogacao/";
	
	/** P�gina de edi��o de um fasc�culo. */
	public static final String PAGINA_EDICAO_FASCICULO = BASE + "paginaEdicaoFasciculo.jsp";
	/** P�gina de edi��o de um exemplar. */
	public static final String PAGINA_EDICAO_EXEMPLAR = BASE + "paginaEdicaoExemplar.jsp";
	
	/** P�gina para o processo de baixa de um exemplar. */
	public static final String PAGINA_CONFIRMA_BAIXA_EXEMPLAR = BASE + "paginaDarBaixaExemplar.jsp";
	/** P�gina para o processo de baixa de um fasc�culo. */
	public static final String PAGINA_CONFIRMA_BAIXA_FASCICULO = BASE + "paginaDarBaixaFasciculo.jsp";

	/** P�gina para cancelamento da baixa de um exemplar. */
	public static final String PAGINA_CONFIRMA_DESFAZER_BAIXA_EXEMPLAR = BASE + "paginaDesfazerBaixaExemplar.jsp";
	/** P�gina para cancelamento da baixa de um fasc�culo. */
	public static final String PAGINA_CONFIRMA_DESFAZER_BAIXA_FASCICULO = BASE + "paginaDesfazerBaixaFasciculo.jsp";

	/** P�gina para a confirma��o da remo��o de um exemplar. */
	public static final String PAGINA_CONFIRMA_REMOCAO_EXEMPLAR = BASE + "paginaRemocaoExemplar.jsp";
	/** P�gina para a confirma��o da remo��o de um fasc�culo. */
	public static final String PAGINA_CONFIRMA_REMOCAO_FASCICULO = BASE + "paginaRemocaoFasciculo.jsp";
	
	/* Os objetos que v�o ser editados */
	/** Exemplar sendo editado. */
	private Exemplar exemplarEdicao;
	/** Fasc�culo sendo editado. */
	private Fasciculo fasciculoEdicao;
	
	/** A nota de circula��o do material sendo editado, se houver */
	private NotaCirculacao notaCirculacao;
	
	/** 
	 *  Guarda os ids Escolhidos pelo usu�rio na p�gina com o selectManyList, quanto
	 *  as formas de documento que um material possui. 
	 */
	private Collection<String> idsFormasDocumentoEscolhidos;
	
	// Indica quais dos dois materiais est� sendo editado agora, importante para a opera��o de baixa:
	/** Indica se o que est� sendo editado � um exemplar. */
	private boolean editandoExemplar = false;
	/** Indica se o que est� sendo editado */
	private boolean editandoFasciculo = false;
	
	/** Indica se, ao voltar, volta para a p�gina de detalhes do exemplar. */
	private boolean retornaPaginaDetalhesExemplar = false;
	
	/** Indica se, ao voltar, volta para a p�gina de detalhes do fasc�culo. */
	private boolean retornaPaginaDetalhesFasciculo = false;
	
	/**
	 * Indica quando usu�rio chegou na p�gina de alterar as informa��es dos exemplares a partir
	 * da p�gina de inclus�o de exemplares. Nesse caso deve retorna para a p�gina de inclus�o.
	 */
	private boolean retornaPaginaIncluirExemplar = false;
	
	/**
	 * Indica que o bot�o voltar na p�gina deve voltar para a tela de cataloga��o.
	 */
	private boolean retornaCatalogacao = false;
	
	/** As informa��es sobre o t�tulo, vindas do cache. */
	private CacheEntidadesMarc titulo;
	
	/**   Se true, volta para a p�gina que chamou a atualiza��o, sen�o fica na mesma p�gina. */
	private String finalizarAtualizacao = "false";
	
	
	/** Informa a nova situa��o que o material deve ter ap�s ter a baixa cancelada. */
	private int idNovaSituacao;
	
	/** Guarda a p�gina de onde a p�gina de edi��o de exemplares foi chamada, deve retornar para essa p�gina depois da inclus�o da nota de circula��o.*/
	private String paginaChamouEdicaoMaterial;
	
	
	/**
	 * <p>Recupera a URL que chamou essa Caso de USO e separa a parte a partir de /biblioteca/, essa ser� a p�gina para onde o 
	 * caso de uso de nota de circula��o deve voltar depois de incluir a nota.</p>
	 *
	 */
	private void configuraPaginaChamouEdicaoMaterial(){
		paginaChamouEdicaoMaterial = StringUtils.isNotEmpty(getCurrentURL()) ? getCurrentURL().substring(getCurrentURL().indexOf("/biblioteca/"), getCurrentURL().length()) : "";
	}
	
	
	
	/**
	 * 
	 * Inicia o caso de uso para edi��o de um exemplar
	 *
	 * <br/><br/>
	 * Chamado a partir das p�ginas: <br>
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
	 * <p>Inicia o caso de uso para edi��o de um exemplar a partir da p�gina de cataloga��o.</p>
	 *
	 * <p>Chamado da arvore de materiais que fica no menu lateral da tela de cataloga��o.</p>
	 * 
	 *  <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
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
			addMensagemErro("Exemplar n�o foi selecionado corretamente.");
			return null;
		}
		
	}
	
	
	

	
	/**
	 * 
	 * <p>Inicia o caso de uso para edi��o de um fasc�culo vindo da tela de cataloga��o.</p>
	 *
	 * <p>Chamado da arvore de materiais que fica no menu lateral da tela de cataloga��o.</p>
	 *
	 * <br/>
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
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
			addMensagemErro("Fasc�culo n�o foi selecionado corretamente.");
			return null;
		}
		
	}
	
	
	/**
	 * 
	 * Inicia o caso de uso para edi��o de um fasc�culo
	 *
	 * <br/><br/>
	 * Chamado a partir das p�ginas: <ul>
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
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * <p> M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 *   Inicia o caso de uso de baixar fasc�culos do acervo.
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * <p>Inicia o caso de uso de desfazer baixa de um fasc�culo.</p>
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 *   Inicia o caso de uso de remover fasc�culos  do acervo.
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * M�todo que zera todas as op��es de retorno da p�gina.
	 * <p>
	 * Geralmente para ser chamado antes de setar uma nova op��o de retorno
	 */
	private void zeraOpcoesRetorno(){
		retornaPaginaDetalhesExemplar = false;
		retornaPaginaDetalhesFasciculo = false;
		retornaPaginaIncluirExemplar = false;
		retornaCatalogacao = false;
	}
	
	
	/**
	 * Configura a opera��o de edi��o correta.
	 */
	public void setOperacaoEditarExemplar(){
		editandoExemplar = true;
		editandoFasciculo = false;
	}
	
	/**
	 * Configura a opera��o de edi��o correta.
	 */
	public void setOperacaoEditarFasciculo(){
		editandoExemplar = false;
		editandoFasciculo = true;
	}
	
	/**
	 *  M�todo respons�vel por atualizar os dados de um exemplar.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaEdicaoFasciculo.jsp
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
				return voltarPaginaExemplar(); // volta para a p�gina que chamou
			}else{
				prepareMovimento(SigaaListaComando.ATUALIZA_EXEMPLAR); // usu�rio apenas salvou, ent�o fica na mesma p�gina e libera para salvar novamente.
				return null;
			}
			
		}else{
			return null;  // fica na mesma tela mostrando as mensagens de erro no preenchimento do formul�rio
		}
		
	}
	
	
	/**
	 *     M�todo respons�vel por atualizar os dados de um fasc�culo.
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaEdicaoFasciculo.jsp
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
			
			
			addMensagemInformation("Fasc�culo atualizado com sucesso.");
			
			if("true".equals(finalizarAtualizacao)){
				return voltarPaginaFasciculo(); // volta para a p�gina que o chamou
			}else{
				prepareMovimento(SigaaListaComando.ATUALIZA_FASCICULO); // usu�rio apenas salva, ent�o fica na mesma p�gina e libera para salvar novamente.
				return null;
			}
			
			
		}else{
			return null;  // fica na mesma tela mostrando as mensagens de erro no preenchimento do formul�rio
		}
		
	}
	
	
	/**
	 * <p>M�todo que realiza a opera��o de dar baixa em um material </p>
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina:
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
					
					return voltarPaginaExemplar(); // volta para a p�gina que o chamou
					
				}else{
					return null;  // fica na mesma tela mostrando as mensagens de erro no preenchimento do formul�rio
				}
				
				
			}
			
			if(editandoFasciculo){
				
				if(usuarioPreencheuDadosFasciculoCorretamente()){
				
					MovimentoDarBaixaFasciculo movimento = new MovimentoDarBaixaFasciculo(fasciculoEdicao);
					movimento.setCodMovimento(SigaaListaComando.DAR_BAIXA_FASCICULO);
					
					execute(movimento);
					
					addMensagemInformation("Baixa do fasc�culo realizada com sucesso.");
					
					return voltarPaginaFasciculo(); // volta para a p�gina que o chamou
					
				}else{
					return null;  // fica na mesma tela mostrando as mensagens de erro no preenchimento do formul�rio
				}
			}
			
		}catch(NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		return null; // nunca era para chegar aqui.
		
	}
	
	/**
	 * <p>M�todo que realiza a opera��o de desfazer baixa de um material.</p>
	 * 
	 * Chamado a partir da p�gina:
	 * <ul>
	 *   <li> /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaDesfazerBaixaExemplar.jsp  </li>
	 *   <li> /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaDesfazerBaixaFasciculo.jsp </li>
	 * </ul>
	 */
	public String desfazerBaixaMaterial() throws ArqException {
		
		try {
			
			if (editandoExemplar) {
				
				if ( idNovaSituacao <= 0 ) {
					addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nova Situa��o");
					return null;
				}
				
				SituacaoMaterialInformacional sit = getGenericDAO().
						findByPrimaryKey(idNovaSituacao, SituacaoMaterialInformacional.class);
				
				MovimentoDesfazerBaixaExemplar movimento = new MovimentoDesfazerBaixaExemplar(
						exemplarEdicao, sit );
				movimento.setCodMovimento(SigaaListaComando.DESFAZER_BAIXA_EXEMPLAR);
				
				execute(movimento);
		
				addMensagemInformation("Baixa do exemplar desfeita com sucesso.");
				
				return voltarPaginaExemplar(); // volta para a p�gina que o chamou
				
			}
			
			if (editandoFasciculo) {
				
				if ( idNovaSituacao <= 0 ) {
					addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nova Situa��o");
					return null;
				}
				
				SituacaoMaterialInformacional sit = getGenericDAO().
						findByPrimaryKey(idNovaSituacao, SituacaoMaterialInformacional.class);
		
				MovimentoDesfazerBaixaFasciculo movimento = new MovimentoDesfazerBaixaFasciculo(
						fasciculoEdicao, sit );
				movimento.setCodMovimento(SigaaListaComando.DESFAZER_BAIXA_FASCICULO);
				
				execute(movimento);
				
				addMensagemInformation("Baixa do fasc�culo desfeita com sucesso.");
				
				return voltarPaginaFasciculo(); // volta para a p�gina que o chamou
				
			}
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		return null; // nunca era para chegar aqui.
		
	}
	
	/**
	 * <p>M�todo que realizar a opera��o de apagar (desativar) um material do acervo</p>
	 * 
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
			
					addMensagemInformation("Remo��o do Exemplar realizada com sucesso.");
					
					return voltarPaginaExemplar(); // volta para a p�gina que o chamou
					
				}else{
					return null;  // fica na mesma tela mostrando as mensagens de erro no preenchimento do formul�rio
				}
				
				
			}
			
			if(editandoFasciculo){
				
				if(usuarioPreencheuDadosFasciculoCorretamente()){
				
					MovimentoRemoverMateriaisAcervo movimento = new MovimentoRemoverMateriaisAcervo(fasciculoEdicao);
					movimento.setCodMovimento(SigaaListaComando.REMOVER_MATERIAIS_ACERVO );
					
					execute(movimento);
					
					addMensagemInformation("Remo��o do Fasc�culo realizada com sucesso.");
					
					return voltarPaginaFasciculo(); // volta para a p�gina que o chamou
					
				}else{
					return null;  // fica na mesma tela mostrando as mensagens de erro no preenchimento do formul�rio
				}
			}
			
		}catch(NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		return null; // nunca era para chegar aqui.
		
	}
	
	/**
	 * Inicializa a nota de circula��o a partir do material sendo editado.
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
	 *   <p>M�todo que encaminha para o caso de uso de incluir uma nota de circula��o no exemplar que
	 *  est� sendo editado nesse momento. </p>
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaEdicaoExemplar.jsp
	 */
	public String incluirNotaCirculacaoExemplar() throws ArqException{
		
		NotasCirculacaoMBean mbean = getMBean("notasCirculacaoMBean");
		List<MaterialInformacional> materiais = new ArrayList<MaterialInformacional>();
		materiais.add( exemplarEdicao );
		return mbean.iniciarConfigurandoDiretamenteMaterialIncluirNota(materiais, paginaChamouEdicaoMaterial);
		
	}
	
	/**
	 *     <p>M�todo que encaminha para o caso de uso de incluir uma nota de circula��o no fasc�culo que
	 *  est� sendo editado nesse momento. </p>
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaEdicaoFasciculo.jsp
	 */
	public String incluirNotaCirculacaoFasciculo() throws ArqException{
		
		NotasCirculacaoMBean mbean = getMBean("notasCirculacaoMBean");
		List<MaterialInformacional> materiais = new ArrayList<MaterialInformacional>();
		materiais.add( fasciculoEdicao );
		return mbean.iniciarConfigurandoDiretamenteMaterialIncluirNota(materiais, paginaChamouEdicaoMaterial);
		
	}
	
	/**
	 * Remove a nota de circula��o do exemplar sendo editado.
	 * Utilizado na p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaEdicaoExemplar.jsp
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
	 * Remove a nota de circula��o do fasc�culo sendo editado.
	 * Utilizado na p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaEdicaoFasciculo.jsp
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
	 *   M�todo que verifica de qual a p�gina a altera��o foi chamada.
	 *   � o lugar para o qual deve-se voltar depois que a atualiza��o for feita.
	 * 
	 *  <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaEdicaoFasciculo.jsp
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
		
		// se n�o tiver setado nenhuma op��o diferente, volta para a tela de pesquisa de fasc�culos
		PesquisarFasciculoMBean bean = getMBean("pesquisarFasciculoMBean");
		return bean.pesquisar();
		
	}
	

	/**
	 *   M�todo que verifica de qual p�gina a altera��o foi chamada.
	 *   � o lugar para o qual deve-se voltar depois que a atualiza��o for feita.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaEdicaoExemplar.jsp
	 */
	public String voltarPaginaExemplar() throws ArqException{
		
		if(retornaPaginaIncluirExemplar){
			
			MaterialInformacionalMBean bean = getMBean("materialInformacionalMBean");
			
			return bean.voltaTelaIncluirNovoMaterial(); //atualiza os dado da p�gina para o exemplar que foi baixado n�o aparecer novamente.
			
			//return bean.inativar();
			
			///return forward(MaterialInformacionalMBean.PAGINA_INCLUSAO_MATERIAIS);
		}
		
		if(retornaPaginaDetalhesExemplar){
			/* A p�gina de detalhes faz uma nova busca para n�o aparecer mais o exemplar que
			 * acabou de ser dado baixa */
			DetalhesMateriaisDeUmTituloMBean bean = getMBean("detalhesMateriaisDeUmTituloMBean");
			return bean.telaInformacoesMateriais();
		}
		
		
		if(retornaCatalogacao){
			CatalogacaoMBean bean = getMBean("catalogacaoMBean");
			return bean.telaDadosTituloCatalografico();
		}
		
		// se n�o tiver setado nenhuma op��o diferente, volta para a tela de pesquisa de exemplares
		PesquisarExemplarMBean bean = getMBean("pesquisarExemplarMBean");
		return bean.pesquisar();
	}
	
	
	
	
	/**
	 * Verifica se os dados do fasc�culo foram informados corretamente
	 */
	private boolean usuarioPreencheuDadosFasciculoCorretamente(){

		boolean contenErro = false;
		
		if ( StringUtils.isEmpty( fasciculoEdicao.getCodigoBarras() ) ){
			addMensagemErro("� preciso informar o c�digo de barras do fasc�culo ");
			contenErro = true;
		}
		
		
		if ( StringUtils.isEmpty( fasciculoEdicao.getNumeroChamada() ) ){
			addMensagemErro("� preciso informar o n�mero de chamada do fasc�culo ");
			contenErro = true;
		}
		
		if ( fasciculoEdicao.getColecao().getId() == -1){
			addMensagemErro("� preciso informar a cole��o do fasc�culo ");
			contenErro = true;
		}
		
		if ( fasciculoEdicao.getStatus().getId() == -1){
			addMensagemErro("� preciso informar o status do fasc�culo ");
			contenErro = true;
		}
		
		if ( fasciculoEdicao.getSituacao().getId() == -1){
			addMensagemErro("� preciso informar a situa��o do fasc�culo");
			contenErro = true;
		}
		
		if ( fasciculoEdicao.getBiblioteca().getId() == -1){
			addMensagemErro("� preciso informar a  biblioteca onde o fasc�culo est� localizado");
			contenErro = true;
		}
		
		if( fasciculoEdicao.getTipoMaterial().getId() == -1){
			addMensagemErro("� preciso informar o tipo de material do fasc�culo");
			contenErro = true;
		}
		
		if( fasciculoEdicao.getNotaGeral() != null && fasciculoEdicao.getNotaGeral().length() > 300){
			addMensagemErro("O tamanho m�ximo do campo Nota Geral � de 300 caracteres.");
			contenErro = true;
		}
		
		if( fasciculoEdicao.getNotaUsuario() != null && fasciculoEdicao.getNotaUsuario().length() > 300 ){
			addMensagemErro("O tamanho m�ximo do campo Nota ao Usu�rio � de 300 caracteres.");
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
			addMensagemErro("� preciso informar o c�digo de barras do exemplar ");
			contenErro = true;
		}
		
		if ( StringUtils.isEmpty( exemplarEdicao.getNumeroChamada() ) ){
			addMensagemErro("� preciso informar o n�mero de chamada do exemplar ");
			contenErro = true;
		}
		
		if ( exemplarEdicao.getColecao().getId() == -1){
			addMensagemErro("� preciso informar a cole��o do exemplar ");
			contenErro = true;
		}
		
		if ( exemplarEdicao.getStatus().getId() == -1){
			addMensagemErro("� preciso informar o status do exemplar ");
			contenErro = true;
		}
		
		if ( exemplarEdicao.getSituacao().getId() == -1){
			addMensagemErro("� preciso informar a situa��o do exemplar");
			contenErro = true;
		}
		
		if ( exemplarEdicao.getBiblioteca().getId() == -1){
			addMensagemErro("� preciso informar a  biblioteca onde o exemplar est� localizado");
			contenErro = true;
		}
		
		if( exemplarEdicao.getTipoMaterial().getId() == -1){
			addMensagemErro("� preciso informar o tipo de material do exemplar");
			contenErro = true;
		}
		
		if( exemplarEdicao.getNotaGeral() != null && exemplarEdicao.getNotaGeral().length() > 300){
			addMensagemErro("O tamanho m�ximo do campo Nota Geral � de 300 caracteres.");
			contenErro = true;
		}
		
		if( exemplarEdicao.getNotaUsuario() != null && exemplarEdicao.getNotaUsuario().length() > 300 ){
			addMensagemErro("O tamanho m�ximo do campo Nota ao Usu�rio � de 300 caracteres.");
			contenErro = true;
		}
		
		return ! contenErro ;
	}
	
	
	
	// telas de navega��o
	
	/** Redireciona para a tela de edi��o de exemplar. */
	private String telaEdicaoExemplar() {
		return forward(PAGINA_EDICAO_EXEMPLAR);
	}
	
	/** Redireciona para a tela de edi��o de fasc�culo. */
	private String telaEdicaoFasciculo() {
		return forward(PAGINA_EDICAO_FASCICULO);
	}

	/** Redireciona para a tela de confirma��o de baixa de um exemplar. */
	private String telaConfirmaBaixaExemplar() {
		return forward(PAGINA_CONFIRMA_BAIXA_EXEMPLAR);
	}
	
	/** Redireciona para a tela de confirma��o de baixa de um fasc�culo. */
	private String telaConfirmaBaixaFasciculo() {
		return forward(PAGINA_CONFIRMA_BAIXA_FASCICULO);
	}
	
	/** Redireciona para a tela de confirma��o de cancelamento de baixa de um exemplar. */
	private String telaConfirmaDesfazerBaixaExemplar() {
		return forward(PAGINA_CONFIRMA_DESFAZER_BAIXA_EXEMPLAR);
	}
	
	/** Redireciona para a tela de confirma��o de cancelamento de baixa de um fasc�culo. */
	private String telaConfirmaDesfazerBaixaFasciculo() {
		return forward(PAGINA_CONFIRMA_DESFAZER_BAIXA_FASCICULO);
	}
	
	/** Redireciona para a tela de confirma��o de remo��o de um exemplar. */
	private String telaConfirmaRemocaoExemplar() {
		return forward(PAGINA_CONFIRMA_REMOCAO_EXEMPLAR);
	}
	
	/** Redireciona para a tela de confirma��o de remo��o de um fasc�culo. */
	private String telaConfirmaRemocaoFasciculo() {
		return forward(PAGINA_CONFIRMA_REMOCAO_FASCICULO);
	}
	

	/////////////////   os m�todo dos combobox da tela   //////////////////////////
	
	
	
	/**
	 * 
	 *    Retorna todas as bibliotecas internas.
	 *    No caso da altera��o, aparece todas as bibliotecas porque o usu�rio pode alterar a biblioteca
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
	 * Retorna todos os status do material dependendo da cole��o escolhida
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
	 * Retorna todos as situa��es que o usu�rio pode atribuir a um material
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
	 * Retorna as situa��es que podem ser escolhidas quando um material est� sendo tirado
	 * da situa��o de baixado.
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
	 *    Retorna o id da situa��o emprestado para gerar um combobox na p�gina apenas com esta situa��o.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina:
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
	 * Retorna todas as cole��es cadastradas.
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
	
	/** Seta as formas de documento escolhidas pelo usu�rio eliminando os ids negativos que n�o s�o formas de documento v�lidas. */
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
