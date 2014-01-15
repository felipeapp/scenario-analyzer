/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 24/03/2009
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.ArtigoDePeriodicoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.FasciculoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.SituacaoMaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.StatusMaterialInformacionalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.StatusMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormaDocumento;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf.SubstituirFasciculoMBean;
import br.ufrn.sigaa.biblioteca.util.FasciculoByBibliotecaAnoCronologicoNumeroComparator;

/**
 *
 *    Gerencia a p�gina de pesquisa de fasc�culos.
 *
 * @author Jadson
 * @since 24/03/2009
 * @version 1.0 cria��o da classe
 *
 */
@Component(value="pesquisarFasciculoMBean")
@Scope(value="request")
public class PesquisarFasciculoMBean extends SigaaAbstractController<Fasciculo>{

	/** P�gina com os filtros e com os resultados da pesquisa por um fasc�culo. */
	public static final String PAGINA_PESQUISA_FASCICULO = "/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaFasciculo.jsp";
	
	/**  Resultados da consulta que vai ser iterado na p�gina   */
	private List<Fasciculo> fasciculos = new ArrayList<Fasciculo>();
	
	/** A quantidade de fasc�culos encontrados. */
	private int qtdFasciculos = -1;
	
	/** Filtro de limite inferior de data de cria��o do fasc�culo. */
	private Date dataCriacaoInicio;
	
	/** Filtro de limite superior de data de cria��o do fasc�culo. */
	private Date dataCriacaoFinal;
	
	/** 
	 *  Guarda os ids Escolhidos pelo usu�rio na p�gina com o selectManyList, quando for 
	 *  efetaur a pesquisa de exemplares.
	 */
	private List<String> idsFormasDocumentoEscolhidos;
	
	/** A opera��o que ser� feita. */
	private int operacao = -1;
	
	/* Opera��es que s�o feitas em cima de um fasc�culo a partir da p�gina de pesquisa: */
	
	/** Indica que o fasc�culo ser� escolhido na tela de pesquisa de fasc�culos. */
	public static final int OPERACAO_PESQUISAR = 1;
	
	/** Indica que o fasc�culo ser� escolhido para cataloga��o de artigos. */
	public static final int OPERACAO_CATALOGACAO_ARTIGOS = 2;
	
	/** Indica que o fasc�culo ser� escolhido para a pesquisa de artigos. */
	public static final int OPERACAO_PESQUISAR_ARTIGOS = 3;
	
	/** Indica que o fasc�culo ser� escolhido para substitui��o de fasc�culos. */
	public static final int OPERACAO_SUBSTITUICAO = 4;
	
	/** Indica que o fasc�culo ser� escolhido para ser o fasc�culo que substituir�
	 * um anterior que j� foi escolhido.*/
	public static final int OPERACAO_PROCURA_FASCICULO_PARA_SUBSTITUICAO = 5;
	
	/** Indica que o fasc�culo ser� escolhido para liga��o de artigos a fasc�culos. */
	//public static final int OPERACAO_ATRIBUI_FASCICULO_AO_ARTIGO = 6;
	
	/** Indica que o fasc�culo ser� escolhido para dar baixa. */
	public static final int OPERACAO_BAIXAR_FASCICULO = 7;
	
	/** Indica que o fasc�culo ser� escolhido para ser removido. */
	public static final int OPERACAO_REMOVER_FASCICULO= 8;
	
	/** Indica que o fasc�culo ser� escolhido para que sua baixa seja desfeita.*/
	public static final int OPERACAO_DESFAZER_BAIXA_FASCICULO = 9;
	
	/** Indica que o fasc�culo ser� escolhido para ser bloqueado. */
	public static final int OPERACAO_BLOQUEAR_MATERIAL = 10;
	
	
	
	
	/* Para visualizar as informa��es dos artigos do fasc�culo selecionado: */
	
	/** Fasc�culo que cont�m os artigos. */
	private Fasciculo fasciculoDosArtigos;
	/** Lista com informa��es dos artigos do fasc�culo. */
	private List<CacheEntidadesMarc> listaArtigosDoFasciculo;
	
	/** Campos a serem utilizados nos checks da pesquisa por fasc�culos. Indicam se o filtro correspondente est� ativo. */
	private boolean buscarTituloAssinatura, buscarCodigoAssinatura, buscarCodigoBarras, buscarFormaDocumento
				, buscarColecao, buscarTipoMaterial, buscarStatus, buscarSituacao, buscarAnoCronologico,
				buscarAno , buscarDiaMes,  buscarVolume, buscarNumero, buscarEdicao, buscarBiblioteca;
	
	
	/**
	 *  Indica se � para buscas os fasc�culos que possuem artigos, os que n�o possui ou tanto faz.
	 * Tanto faz = -1
	 * SIM = 0
	 * N�O = 1
	 */
	private short possuiArtigos = -1;
	

	
	
	/**
	 * Construtor default.
	 * Inicia um Item Catalogr�fico num estado inv�lido.
	 */
	public PesquisarFasciculoMBean(){
		obj = new Fasciculo(new Assinatura(), new Biblioteca());
		obj.setColecao(new Colecao());
		obj.setStatus(new StatusMaterialInformacional());
		obj.setSituacao(new SituacaoMaterialInformacional());
		obj.setTipoMaterial(new TipoMaterial());
	}
	
	
	/**
	 * Inicia a pesquisa geralmente chamada do menu principal da biblioteca
	 * <br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/menus/processos_tecnicos.jsp
	 */
	public String iniciarPesquisa(){
		
		operacao = OPERACAO_PESQUISAR;
		
		limparDadosPesquisa();
		
		return telaPesquisaFasciculo();
	}
	
	
	/**
	 *   Inicia o caso de uso de catalogar o artigo de um peri�dico (fasc�culo).
	 *   Para catalogar � preciso primeiro informar de qual fasc�culo vai ser o artigo.
	 *   Por isso come�a pela p�gina de pesquisa.
	 * <br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/menus/processos_tecnicos.jsp
	 */
	public String iniciarCatalogacaoArtigo(){
		
		operacao = OPERACAO_CATALOGACAO_ARTIGOS;
		
		limparDadosPesquisa();
		
		return telaPesquisaFasciculo();
	}
	
	
	
	
	
	/**
	 *   Chamado quando se deseja voltar de outro lugar para a p�gina de pesquisa com a op��o
	 *  de cataloga��o de artigos ativada.
	 * <br/>
	 * Chamado a partir do MBean: CatalogaArtigoMBean
	 * 
	 * M�todo n�o chamado de nenhuma jsp.
	 */
	public String voltaCatalogacaoArtigo() throws DAOException{
		
		operacao = OPERACAO_CATALOGACAO_ARTIGOS;
		
		return pesquisar();
	}
	
	
	/**
	 *   M�todo que inicia a pesquisa para escolher o fasc�culo que vai ser substitu�do.
	 *   O fasc�culo escolhido pelo usu�rio nessa pesquisa vai ser baixado do acervo.
	 * <br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisafasciculo.jsp
	 * 
	 * @Deprecated passou para ser feita no SIPAC, como n�o foi feita l� ainda, est� implementada nesta classe.
	 */
	public String iniciarSubstituicaoFasciculo() throws SegurancaException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		limparDadosPesquisa();
		
		operacao = OPERACAO_SUBSTITUICAO;
		
		return telaPesquisaFasciculo();
	}
	
	
	
	
	/**
	 *  M�todo que inicia a pesquisa para escolher o fasc�culo que vai substituir o fasc�culo que o
	 *  usu�rio escolheu no passo anterior desse caso de uso.
	 * <br/>
	 * M�todo n�o chamado por p�ginas jsp.
	 * Chamado a partir da p�gina: A partir do MBean  {@link SubstituirFasciculoMBean#iniciar()}
	 * 
	 * @Deprecated passou para ser feita no SIPAC, como n�o foi feita l� ainda, est� implementada nesta classe.
	 */
	public String iniciarProcuraFasciculoParaSubstituir() throws SegurancaException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		limparDadosPesquisa();
		
		operacao = OPERACAO_PROCURA_FASCICULO_PARA_SUBSTITUICAO;
		
		return telaPesquisaFasciculo();
	}
	
	
	
	/**
	 * Inicia a pesquisa de fasc�culos para baixar o fasc�culo do acervo
	 * <br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/menus/processos_tecnicos.jsp
	 */
	public String iniciarPesquisaBaixa(){
		
		operacao = OPERACAO_BAIXAR_FASCICULO;
		
		limparDadosPesquisa();
		
		return telaPesquisaFasciculo();
	}
	
	/**
	 * Inicia a pesquisa de fasc�culos para desfaze baixa de fasc�culo do acervo.
	 * <br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/menus/processos_tecnicos.jsp
	 */
	public String iniciarPesquisaDesfazerBaixa() {
		
		operacao = OPERACAO_DESFAZER_BAIXA_FASCICULO;
		
		limparDadosPesquisa();
		
		return telaPesquisaFasciculo();
	}
	
	
	/**
	 * Inicia a pesquisa de fasc�culos para remover o fasc�culos do acervo.
	 * <br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/menus/processos_tecnicos.jsp
	 */
	public String iniciarPesquisaRemocao(){
		
		operacao = OPERACAO_REMOVER_FASCICULO;
		
		limparDadosPesquisa();
		
		return telaPesquisaFasciculo();
	}
	
	
	/**
	 * 
	 * Inicia a pesquisa para bloquear um fasc�culo na parte de circula��o.
	 *
	 * <br/><br/>
	 * M�todo n�o chamado por nenhuma p�gina jsp.
	 */
	public String iniciarBloqueioMaterial () throws SegurancaException{
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO);
		
		limparDadosPesquisa();
		
		operacao = OPERACAO_BLOQUEAR_MATERIAL;
		return telaPesquisaFasciculo();
	}
	
	
	
	/**
	 * 
	 *     M�todo que realiza a pesquisa do item catalogr�fico de acordo com o que foi
	 * preenchido pelo usu�rio no formul�rio.
	 * <br/>
	 * Chamado pela JSP:  /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisafasciculo.jsp
	 */
	public String pesquisar() throws DAOException {
		
		FasciculoDao  fasciculoDao = null;
		ArtigoDePeriodicoDao artigoDao = null;
		
		fasciculoDao = getDAO(FasciculoDao.class);
		artigoDao = getDAO(ArtigoDePeriodicoDao.class);
		
		
		if(obj.getBiblioteca().getId() == -1) buscarBiblioteca = false;
		if(obj.getColecao().getId() == -1) buscarColecao = false;
		if(obj.getTipoMaterial().getId() == -1) buscarTipoMaterial = false;
		if(obj.getStatus().getId() == -1) buscarStatus = false;
		if(obj.getSituacao().getId() == -1) buscarSituacao = false;
		if(StringUtils.isEmpty(obj.getCodigoBarras())) buscarCodigoBarras = false;
		if(StringUtils.isEmpty(obj.getEdicao())) buscarEdicao = false;
		if(StringUtils.isEmpty(obj.getNumero())) buscarNumero = false;
		if(StringUtils.isEmpty(obj.getVolume())) buscarVolume = false;
		if(StringUtils.isEmpty(obj.getAno())) buscarAno = false;
		if(StringUtils.isEmpty(obj.getDiaMes())) buscarDiaMes = false;
		if(StringUtils.isEmpty(obj.getAnoCronologico())) buscarAnoCronologico= false;
		if(StringUtils.isEmpty(obj.getAssinatura().getCodigo())) buscarCodigoAssinatura = false;
		if(StringUtils.isEmpty(obj.getAssinatura().getTitulo())) buscarTituloAssinatura= false;
		if(isEmpty(getIdsFormasDocumentoEscolhidos())) buscarFormaDocumento= false;
		
		
		if (! buscarTituloAssinatura) obj.getAssinatura().setTitulo( null );
		if (! buscarCodigoAssinatura) obj.getAssinatura().setCodigo( null );
		if (! buscarCodigoBarras) obj.setCodigoBarras(null);
		else if (!StringUtils.isEmpty(obj.getCodigoBarras()))
			obj.setCodigoBarras(obj.getCodigoBarras().toUpperCase());
		
		if (! buscarAnoCronologico) obj.setAnoCronologico(null);
		if (! buscarAno) obj.setAno(null);
		if (! buscarDiaMes) obj.setDiaMes(null);
		if (! buscarVolume) obj.setVolume(null);
		if (! buscarNumero) obj.setNumero(null);
		if (! buscarEdicao) obj.setEdicao(null);
		if (! buscarBiblioteca) obj.getBiblioteca().setId(-1);
		if (! buscarColecao) obj.getColecao().setId(-1);
		if (! buscarTipoMaterial) obj.getTipoMaterial().setId(-1);
		if (! buscarStatus) obj.getStatus().setId(-1);
		if (! buscarSituacao) obj.getSituacao().setId(-1);
		
		
		if (! buscarFormaDocumento) obj.setFormasDocumento(new ArrayList<FormaDocumento>());
		else
			obj.popularFormasDocumento(getIdsFormasDocumentoEscolhidos());
		
		
		if(validaDadosFormulario()){
		
			if(validaIntervaloEntreDados(dataCriacaoInicio , dataCriacaoFinal)) {
			
				fasciculos = fasciculoDao.findAllFasciculosAtivosByExemplo(obj, dataCriacaoInicio, dataCriacaoFinal, possuiArtigos);
				
				// CONTA OS ARTIGOS ATIVOS DO FASC�CULO
				for (Fasciculo fasciculo : fasciculos) {
					fasciculo.setQuantidadeArtigos( new Long( artigoDao.countArtigosDoFasciculoNaoRemovidos(fasciculo.getId())).intValue());
				}
				
				Collections.sort(fasciculos, new FasciculoByBibliotecaAnoCronologicoNumeroComparator());
				
				qtdFasciculos = fasciculos.size();
				
				if(qtdFasciculos == 0){
					addMensagemWarning("N�o Foram Encontrados Fasc�culos com as Caracter�sticas Buscadas");
				}
			
				if(qtdFasciculos >= FasciculoDao.LIMITE_RESULTADOS){
					addMensagemWarning("Sua busca resultou em um n�mero muito grande de resultados e "
						+ "somente os "+FasciculoDao.LIMITE_RESULTADOS+" primeiros est�o sendo mostrados. Por favor refine mais a sua busca");
				}
				
			}else{
				addMensagemErro("Informe uma Data Final Maior que a Inicial");
			}
		}else{
			addMensagemWarning("Informe um crit�rio para busca");
		}
		
		return telaPesquisaFasciculo();
		
	}
	
	/**
	 * 
	 *   M�todo para limpar os resultados da busca.
	 * 
	 * <br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaFasciculo.jsp
	 */
	public String limparDadosPesquisa(){
		obj = new Fasciculo(new Assinatura(), new Biblioteca());
		obj.setColecao(new Colecao());
		obj.setStatus(new StatusMaterialInformacional());
		obj.setSituacao(new SituacaoMaterialInformacional());
		obj.setTipoMaterial(new TipoMaterial());
		
		fasciculos = new ArrayList<Fasciculo>();
		
		qtdFasciculos = 0;
		
		dataCriacaoInicio = null;
		dataCriacaoFinal = null;
		
		buscarTituloAssinatura = false; buscarCodigoAssinatura = false;
		buscarCodigoBarras = false; buscarAnoCronologico = false; buscarAno = false; buscarDiaMes = false; buscarVolume = false;
		buscarNumero = false; buscarEdicao = false; buscarBiblioteca = false;
		buscarColecao = false; buscarTipoMaterial = false; buscarStatus = false;
		
		// Na opera��o de desfazer baixa, o check box de situa��o est� sempre setado.
		buscarSituacao = isOperacaoDesfazerBaixa();
		
		return null;
	}
	
	
	
//	/**
//	 *   Monta as informa��es dos arquivos que est�o no formato MARC do fasc�culo selecionado e
//	 *   mostra para o usu�rio.
//	 * <br/>
//	 * Chamado a partir da p�gina: <ul><li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/resultadoPesquisaFasciculo.jsp</li></ul>
//	 */
//	public String mostarInformacoesArtigos() throws DAOException{
//		
//		DetalhesMateriaisDeUmTituloMBean bean = getMBean("detalhesMateriaisDeUmTituloMBean");
//		return bean.carregaInformacoesArtigo();
//	}
	
	
	
	
//	/**
//	 *   M�todo chamado pelo MBean {@link RemoverEntidadeDoAcervoMBean#voltarTelaArtigos()}
//	 * depois que o usu�rio removeu o artigo vindo dessa mesma p�gina.
//	 * 
//	 *   O sistema volta para a mesma p�gina de visualiza��o dos artigos do fasc�culo, mostrando os
//	 * artigos ainda ativos do fasc�culo.
//	 * <br/>
//	 * M�todo n�o chamado por nenhuma p�gina jsp.
//	 */
//	public String mostarInformacoesArtigosVindoDaRemocao() throws DAOException{
//		
//		DetalhesMateriaisDeUmTituloMBean bean = getMBean("detalhesMateriaisDeUmTituloMBean");
//		return bean.telaInformacoesArtigo();
//		
//	}
	
	
	
	
	
	
	
	/**
	 * M�todo que valida os dados do formul�rio de pesquisa
	 */
	private boolean validaDadosFormulario(){
		if(obj.getAnoCronologico() == null && obj.getAno() == null && obj.getDiaMes() == null && obj.getVolume()  == null  && obj.getNumero()  == null
				&& obj.getEdicao() == null && obj.getBiblioteca().getId() <= 0 && obj.getColecao().getId() <= 0 &&
				obj.getTipoMaterial().getId() <= 0 && obj.getStatus().getId() <= 0 && obj.getSituacao().getId() <= 0 &&
				StringUtils.isEmpty(obj.getCodigoBarras())
				&& StringUtils.isEmpty(obj.getAssinatura().getTitulo()) && StringUtils.isEmpty(obj.getAssinatura().getCodigo())
				&& dataCriacaoFinal == null && dataCriacaoInicio == null && isEmpty(getIdsFormasDocumentoEscolhidos())){
			return false;
		}
		
		return true;
	}
	
	
	 /**
	  * Valida se data fim � maior ou igual a data in�cio.
	  */
	private boolean validaIntervaloEntreDados(Date inicio, Date fim) {
		
		if(inicio == null || fim == null){
			return true;
		}
		
		if (inicio.getTime() > fim.getTime()){
			return false;
		}

		return true;
		
	}
	
	

	// M�todo dos select item
	
	/**
	 * Retorna todas as Biblioteca internas do sistema para mostrar no formul�rio da busca
	 */
	public Collection <SelectItem> getBibliotecas() throws DAOException{
		Collection <Biblioteca> sb = getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivas();
		return toSelectItems(sb, "id", "descricaoCompleta");
	}

	
	/**
	 * Retorna todas as cole��es para mostrar no formul�rio da busca
	 */
	public Collection <SelectItem> getColecoes() throws DAOException{
		Collection <Colecao> c = getGenericDAO().findByExactField(Colecao.class, "ativo", true);
		return toSelectItems(c, "id", "descricaoCompleta");
	}
	
	/**
	 * Retorna todos os status do item  para mostrar no formul�rio da busca
	 */
	public Collection <SelectItem> getStatusMateriais() throws DAOException{
		Collection <StatusMaterialInformacional> si = getDAO(StatusMaterialInformacionalDao.class).findAllStatusMaterialAtivos();
		return toSelectItems(si, "id", "descricao");
	}

	
	/**
	 * Retorna todos os status do item  para mostrar no formul�rio da busca
	 */
	public Collection <SelectItem> getSituacaoMateriais() throws DAOException{
		Collection <SituacaoMaterialInformacional> si;
		
		if ( ! isOperacaoDesfazerBaixa() ) {
			si = getDAO(SituacaoMaterialInformacionalDao.class).findSituacoesUsuarioPodeVerNaHoraDaPesquisa();
		} else {
			si = getDAO(SituacaoMaterialInformacionalDao.class).findSituacoesDeBaixa();
		}
		
		return toSelectItems(si, "id", "descricao");
	}
	
	
	/**
	 * Retorna todas os tipos do item catalogr�fico para mostrar no formul�rio da busca
	 */
	public Collection <SelectItem> getTiposMaterial() throws DAOException{
		Collection <TipoMaterial> r = getGenericDAO().findByExactField(TipoMaterial.class, "ativo", true);
		return toSelectItems(r, "id", "descricao");
	}
	
	
	
	///////////// telas de navega��o ///////////////
	
	/**
	 * Redireciona para a tela de pesquisa de fasc�culos.<br/>
	 * N�o � chamado por nenhuma JSP.
	 */
	public String telaPesquisaFasciculo(){
		return forward(PAGINA_PESQUISA_FASCICULO);
	}
	
	
	///////////// opera��es /////////
	
	public boolean isOperacaoPesquisar(){
		return operacao == OPERACAO_PESQUISAR;
	}
	
	public boolean isOperacaoCatalogacaoArtigos(){
		return operacao == OPERACAO_CATALOGACAO_ARTIGOS;
	}
	
	public boolean isOperacaoPesquisarArtigo(){
		return operacao == OPERACAO_PESQUISAR_ARTIGOS;
	}
	
	
	public boolean isOperacaoSubstituicao() {
		return operacao == OPERACAO_SUBSTITUICAO;
	}

	public boolean isOperacaoProcuraFasciculoParaSubstituicao() {
		return operacao ==  OPERACAO_PROCURA_FASCICULO_PARA_SUBSTITUICAO;
	}
	
	
	// sets e gets
	
	public List<Fasciculo> getFasciculos() {
		return fasciculos;
	}

	public void setFasciculos(List<Fasciculo> fasciculos) {
		this.fasciculos = fasciculos;
	}

	public int getQtdFasciculos() {
		return qtdFasciculos;
	}

	public void setQtdFasciculos(int qtdFasciculos) {
		this.qtdFasciculos = qtdFasciculos;
	}

	public Date getDataCriacaoInicio() {
		return dataCriacaoInicio;
	}

	public void setDataCriacaoInicio(Date dataCriacaoInicio) {
		this.dataCriacaoInicio = dataCriacaoInicio;
	}

	public Date getDataCriacaoFinal() {
		return dataCriacaoFinal;
	}

	public void setDataCriacaoFinal(Date dataCriacaoFinal) {
		this.dataCriacaoFinal = dataCriacaoFinal;
	}

	public int getOperacao() {
		return operacao;
	}

	public void setOperacao(int operacao) {
		this.operacao = operacao;
	}

	public List<CacheEntidadesMarc> getListaArtigosDoFasciculo() {
		return listaArtigosDoFasciculo;
	}

	public void setListaArtigosDoFasciculo(List<CacheEntidadesMarc> listaArtigosDoFasciculo) {
		this.listaArtigosDoFasciculo = listaArtigosDoFasciculo;
	}

	public boolean isBuscarTituloAssinatura() {
		return buscarTituloAssinatura;
	}

	public void setBuscarTituloAssinatura(boolean buscarTituloAssinatura) {
		this.buscarTituloAssinatura = buscarTituloAssinatura;
	}

	public boolean isCodigoAssinantura() {
		return buscarCodigoAssinatura;
	}

	public boolean isBuscarCodigoAssinatura() {
		return buscarCodigoAssinatura;
	}

	public void setBuscarCodigoAssinatura(boolean buscarCodigoAssinatura) {
		this.buscarCodigoAssinatura = buscarCodigoAssinatura;
	}

	public void setBuscarCodigoBarras(boolean buscarCodigoBarras) {
		this.buscarCodigoBarras = buscarCodigoBarras;
	}

	public boolean isBuscarAnoCronologico() {
		return buscarAnoCronologico;
	}

	public void setBuscarAnoCronologico(boolean buscarAnoCronologico) {
		this.buscarAnoCronologico = buscarAnoCronologico;
	}

	public boolean isBuscarAno() {
		return buscarAno;
	}

	public void setBuscarAno(boolean buscarAno) {
		this.buscarAno = buscarAno;
	}

	public boolean isBuscarVolume() {
		return buscarVolume;
	}

	public void setBuscarVolume(boolean buscarVolume) {
		this.buscarVolume = buscarVolume;
	}

	public boolean isBuscarNumero() {
		return buscarNumero;
	}

	public void setBuscarNumero(boolean buscarNumero) {
		this.buscarNumero = buscarNumero;
	}

	public boolean isBuscarEdicao() {
		return buscarEdicao;
	}

	public void setBuscarEdicao(boolean buscarEdicao) {
		this.buscarEdicao = buscarEdicao;
	}

	public boolean isBuscarBiblioteca() {
		return buscarBiblioteca;
	}

	public void setBuscarBiblioteca(boolean buscarBiblioteca) {
		this.buscarBiblioteca = buscarBiblioteca;
	}

	public Fasciculo getFasciculoDosArtigos() {
		return fasciculoDosArtigos;
	}

	public void setFasciculoDosArtigos(Fasciculo fasciculoDosArtigos) {
		this.fasciculoDosArtigos = fasciculoDosArtigos;
	}

	public short getPossuiArtigos() {
		return possuiArtigos;
	}

	public void setPossuiArtigos(short possuiArtigos) {
		this.possuiArtigos = possuiArtigos;
	}

	public boolean isBuscarColecao() {
		return buscarColecao;
	}

	public void setBuscarColecao(boolean buscarColecao) {
		this.buscarColecao = buscarColecao;
	}

	public boolean isBuscarTipoMaterial() {
		return buscarTipoMaterial;
	}

	public void setBuscarTipoMaterial(boolean buscarTipoMaterial) {
		this.buscarTipoMaterial = buscarTipoMaterial;
	}

	public boolean isBuscarStatus() {
		return buscarStatus;
	}

	public void setBuscarStatus(boolean buscarStatus) {
		this.buscarStatus = buscarStatus;
	}

	public boolean isBuscarSituacao() {
		return buscarSituacao;
	}

	public void setBuscarSituacao(boolean buscarSituacao) {
		this.buscarSituacao = buscarSituacao;
	}

	public boolean isBuscarCodigoBarras() {
		return buscarCodigoBarras;
	}

	public boolean isOperacaoBaixar(){
		return operacao == OPERACAO_BAIXAR_FASCICULO;
	}
	
	public boolean isOperacaoDesfazerBaixa(){
		return operacao == OPERACAO_DESFAZER_BAIXA_FASCICULO;
	}
	
	public boolean isOperacaoRemover(){
		return operacao == OPERACAO_REMOVER_FASCICULO;
	}
	
	public boolean isOperacaoBloquearMaterial(){
		return operacao == OPERACAO_BLOQUEAR_MATERIAL;
	}
	
	public boolean isBuscarDiaMes() {
		return buscarDiaMes;
	}

	public void setBuscarDiaMes(boolean buscarMes) {
		this.buscarDiaMes = buscarMes;
	}

	public List<String> getIdsFormasDocumentoEscolhidos() {
		return idsFormasDocumentoEscolhidos;
	}
	
	
	/** Seta as formas de documento escolhidas pelo usu�rio eliminando os ids negativos que n�o s�o formas de documento v�lidas. */
	public void setIdsFormasDocumentoEscolhidos(List<String> idsFormasDocumentoEscolhidos) {
		this.idsFormasDocumentoEscolhidos = new ArrayList<String>();
		
		for (String string : idsFormasDocumentoEscolhidos) {
			if( Integer.parseInt(string) > 0 )
				this.idsFormasDocumentoEscolhidos.add(string);
		}
	}

	public boolean isBuscarFormaDocumento() {
		return buscarFormaDocumento;
	}

	public void setBuscarFormaDocumento(boolean buscarFormaDocumento) {
		this.buscarFormaDocumento = buscarFormaDocumento;
	}
	
}
