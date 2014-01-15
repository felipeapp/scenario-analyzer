/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 26/08/2008
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf;

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
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ExemplarDao;
import br.ufrn.sigaa.arq.dao.biblioteca.SituacaoMaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.StatusMaterialInformacionalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.StatusMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormaDocumento;
import br.ufrn.sigaa.biblioteca.util.ExemplarByCodigoBarrasComparator;

/**
 *    MBean que gerencia a página de buscas de exemplares no acervo da biblioteca.
 *
 * @author jadson
 * @since 26/08/2008
 * @version 1.0 criação da classe
 *
 */
@Component(value="pesquisarExemplarMBean")
@Scope(value="request")
public class PesquisarExemplarMBean  extends SigaaAbstractController<Exemplar>{

	/** Página para pesquisa de exemplares. */
	public static final String PAGINA_PESQUISA_EXEMPLAR = "/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaExemplar.jsp";
	
	
	/**  Resultados da consulta que vai ser inteirado na página   */
	private List<Exemplar> exemplares = new ArrayList<Exemplar>();
	
	/** Guarda a quantidade de exemplares encontrados*/
	private int qtdExemplares = -1;
	
	
	/* Guarda o intervalo de busca */
	
	/** Intervalo inferior da data de criação.  */
	private Date dataCriacaoInicio;
	/** Intervalo superior da data de criação. */
	private Date dataCriacaoFinal;
	
	/** 
	 *  Guarda os ids Escolhidos pelo usuário na página com o selectManyList, quando for 
	 *  efetaur a pesquisa de exemplares.
	 */
	private List<String> idsFormasDocumentoEscolhidos;
	
	/** Operações que são feitas em cima de um exemplar a partir da página de pesquisa */
	public static final int
			OPERACAO_PESQUISAR = 0,
			OPERACAO_SUBSTITUICAO = 1,
			OPERACAO_PROCURA_EXEMPLAR_PARA_SUBSTITUICAO = 2,
			OPERACAO_BLOQUEAR_MATERIAL = 4,
			OPERACAO_BAIXAR_EXEMPLAR = 5,
			OPERACAO_REMOVER_EXEMPLAR = 6,
			OPERACAO_DESFAZER_BAIXA_EXEMPLAR = 7;
	
	/** Operação que será habilitada a ser feita em cima dos exemplares. */
	private int operacao = -1;
	
		
	/** Flags que indicam quais filtros estão ativos. */
	private boolean buscarCodigoBarras, buscarBiblioteca, buscarColecao, buscarTipoMaterial
			, buscarStatus, buscarSituacao, buscarFormaDocumento;
	
	
	/**
	 * Construtor default.
	 * Inicia um Item Catalográfico num estado inválido.
	 * 
	 */
	public PesquisarExemplarMBean(){
		this.obj = new Exemplar();
		obj.setStatus(new StatusMaterialInformacional("")); // cria objeto vazio
		obj.setSituacao(new SituacaoMaterialInformacional()); // cria objeto vazio
		obj.setColecao(new Colecao());
		obj.setBiblioteca(new Biblioteca());
		obj.setTipoMaterial(new TipoMaterial(""));
	}
	
	
	
	/**
	 * Inicia a pesquisa geralmente chamada do menu principal da biblioteca
	 *
	 * <br/><br/
	 * Chamado a partir da página: /sigaa.war/biblioteca/menus/processos_tecnicos.jsp
	 */
	public String iniciarPesquisa(){
		
		operacao = OPERACAO_PESQUISAR;
		
		limparDadosPesquisa();
		
		return telaPesquisaExemplar();
	}
	
	
	/**
	 * 
	 * Inicia a pesquisa para bloquear um exemplar na parte de circulação.
	 *
	 * <br/><br/>
	 * Método não chamado por nenhuma página jsp.
	 */
	public String iniciarBloqueioMaterial () throws SegurancaException{
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO);
		
		limparDadosPesquisa();
		
		operacao = OPERACAO_BLOQUEAR_MATERIAL;
		return telaPesquisaExemplar();
	}
	
	
	
	
	/**
	 * Inicia a pesquisa para a operação de substituir um exemplar.<br/>
	 * Nesse passo, vai escolher o exemplar que será substituído.
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/menus/processos_tecnicos.jsp
	 */
	public String iniciarSubstituicaoExemplar() throws SegurancaException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		limparDadosPesquisa();
		
		operacao = OPERACAO_SUBSTITUICAO;
		return telaPesquisaExemplar();
	}
	
	
	
	/**
	 * Inicia a segunda parte do caso de uso de substituição de exemplares, nesta opção vai escolher
	 * o exemplar que vai substituir o exemplar escolhido no passo anterior.
	 * 
	 * <br/><br/>
	 * Método não chamado por nenhuma jsp.
	 */
	public String iniciarProcuraExemplarParaSubstituir() throws SegurancaException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		limparDadosPesquisa();
		
		operacao = OPERACAO_PROCURA_EXEMPLAR_PARA_SUBSTITUICAO;
		
		return telaPesquisaExemplar();
	}
	
	
	/**
	 * Inicia a pesquisa de exemplares para baixar o exemplar do acervo
	 * <br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/menus/processos_tecnicos.jsp
	 */
	public String iniciarPesquisaBaixa(){
		
		operacao = OPERACAO_BAIXAR_EXEMPLAR;
		
		limparDadosPesquisa();
		
		return telaPesquisaExemplar();
	}
	
	/**
	 * Inicia a pesquisa de exemplares para desfazer baixa de exemplar do acervo.
	 * <br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/menus/processos_tecnicos.jsp
	 * @return
	 */
	public String iniciarPesquisaDesfazerBaixa(){
		
		operacao = OPERACAO_DESFAZER_BAIXA_EXEMPLAR;
		
		limparDadosPesquisa();
		
		buscarSituacao = true;
		
		return telaPesquisaExemplar();
	}
	
	/**
	 * Inicia a pesquisa de exemplares para remover o exemplar do acervo.
	 * <br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/menus/processos_tecnicos.jsp
	 */
	public String iniciarPesquisaRemocao(){
		
		operacao = OPERACAO_REMOVER_EXEMPLAR ;
		
		limparDadosPesquisa();
		
		return telaPesquisaExemplar();
	}
	
	
	
	/**
	 * 
	 *     Método que realiza a pesquisa do item catalográfico de acordo com o que foi
	 * preenchido pelo usuário no formulário.
	 *
	 * <br/><br/>
	 * Chamado a partir da página:: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_no_acervo/pesquisaExemplar.jsp
	 */
	public String pesquisar() throws DAOException {
		
		exemplares = new ArrayList<Exemplar>();
		qtdExemplares = -1;
		
		ExemplarDao  exemplarDao = null;
		
		try{
			
			exemplarDao = getDAO(ExemplarDao.class);
			
			if (! buscarCodigoBarras) obj.setCodigoBarras( null );
			else if (!StringUtils.isEmpty(obj.getCodigoBarras()))
				obj.setCodigoBarras(obj.getCodigoBarras().toUpperCase());
			
			
			if(obj.getBiblioteca().getId() == -1) buscarBiblioteca = false;
			if(obj.getColecao().getId() == -1) buscarColecao = false;
			if(obj.getTipoMaterial().getId() == -1) buscarTipoMaterial = false;
			if(obj.getStatus().getId() == -1) buscarStatus = false;
			if(obj.getSituacao().getId() == -1) buscarSituacao = false;
			if( getIdsFormasDocumentoEscolhidos() == null) buscarFormaDocumento = false;
			if(StringUtils.isEmpty(obj.getCodigoBarras())) buscarCodigoBarras = false;
			
			if (! buscarBiblioteca) obj.getBiblioteca().setId(-1);
			if (! buscarColecao) obj.getColecao().setId(-1);
			if (! buscarTipoMaterial) obj.getTipoMaterial().setId(-1);
			if (! buscarStatus) obj.getStatus().setId(-1);
			if (! buscarSituacao) obj.getSituacao().setId(-1);
			
			if (! buscarFormaDocumento) obj.setFormasDocumento(new ArrayList<FormaDocumento>());
			else
				obj.popularFormasDocumento(getIdsFormasDocumentoEscolhidos());
			
			
			//if (! buscarDataCriacao) {dataCriacaoInicio = null; dataCriacaoFinal = null;}
			
			if(validaDadosFormulario()){
			
				if(validaIntervaloEntreDados(dataCriacaoInicio , dataCriacaoFinal)) {
				
					exemplares = exemplarDao.findAllExemplaresAtivosByExemplo(obj, dataCriacaoInicio, dataCriacaoFinal);
					
					Collections.sort(exemplares, new ExemplarByCodigoBarrasComparator());
					
					qtdExemplares = exemplares.size();
					
					if(qtdExemplares == 0){
						addMensagemWarning("Não Foram Encontrados Exemplares com as Características Buscadas");
					}
					
					if(qtdExemplares >= ExemplarDao.LIMITE_RESULTADOS){
						addMensagemWarning("Sua busca resultou em um número muito grande de resultados e "
							+ "somente os "+ExemplarDao.LIMITE_RESULTADOS+" primeiros estão sendo mostrados. Por favor refine mais a sua busca.");
					}
					
				}else{
					addMensagemErro( "Informe uma Data Final Maior que a Inicial");
				}
			}else{
				addMensagemWarning("Informe um critério para busca");
			}
		
		}finally{
			if(exemplarDao != null) exemplarDao.close();
		}
		
		return telaPesquisaExemplar();
		
	}
	
	/**
	 * Método para limpar os resultados da busca.
	 * 
	 * <p>Método chamado pela(s) seguintes JSPs:
	 * 	<ul><li>sigaa.war/biblioteca/processos_tecnicos/pesquisas_no_acervo/pesquisaExemplar.jsp</li></ul>
	 */
	public String limparDadosPesquisa(){
		this.obj = new Exemplar();
		obj.setStatus(new StatusMaterialInformacional("")); // cria objeto vazio
		obj.setSituacao(new SituacaoMaterialInformacional());
		obj.setColecao(new Colecao());
		obj.setBiblioteca(new Biblioteca());
		obj.setTipoMaterial(new TipoMaterial(""));
		
		exemplares = new ArrayList<Exemplar>();
		
		qtdExemplares = -1;
		
		dataCriacaoInicio = null;
		dataCriacaoFinal = null;
		
		buscarCodigoBarras = false; buscarBiblioteca= false; buscarColecao= false;
		buscarTipoMaterial = false; buscarStatus = false;
		buscarFormaDocumento = false;
		
		// Na operação de desfazer baixa, o check box de situação está sempre setado.
		buscarSituacao = isOperacaoDesfazerBaixa();
		
		return null;
	}
	
	
	
	/**
	 * Método que valida os dados do formulário de pesquisa
	 */
	private boolean validaDadosFormulario(){
		if(obj.getNumeroPatrimonio() == null && obj.getStatus().getId() <= 0
				&& obj.getSituacao().getId() <=0 && obj.getColecao().getId() <=0 && obj.getBiblioteca().getId() <= 0
				&& obj.getTipoMaterial().getId() <= 0
				&& StringUtils.isEmpty(obj.getCodigoBarras())
				&& (dataCriacaoInicio == null || dataCriacaoFinal == null)
				&& (idsFormasDocumentoEscolhidos == null || idsFormasDocumentoEscolhidos.size() == 0) ) return false;
		return true;
	}
	
	
	 /**
	  * Valida se data fim é maior ou igual a data início.
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
	

	// métodos dos selectItem
	
	/**
	 * 
	 * Retorna todas as Biblioteca internas do sistema para mostrar no formulário da busca
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getBibliotecas() throws DAOException{	
		Collection <Biblioteca> sb = getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivas();
		return toSelectItems(sb, "id", "descricaoCompleta");
	}
	

	/**
	 * 
	 * Retorna todos os status dos materiais  para mostrar no formulário da busca
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getStatusMateriais() throws DAOException{
		Collection <StatusMaterialInformacional> si = getDAO(StatusMaterialInformacionalDao.class).findAllStatusMaterialAtivos();
		return toSelectItems(si, "id", "descricao");
	}

	/**
	 * 
	 * Retorna todos as situações dos materiais para mostrar no formulário da busca.
	 *
	 * @return
	 * @throws DAOException
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
	 * 
	 * Retorna todas as coleções para mostrar no formulário da busca.
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
	 * Retorna todas os tipos do item catalográfico para mostrar no formulário da busca.
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getTiposMaterial() throws DAOException{
		Collection <TipoMaterial> r = getGenericDAO().findByExactField(TipoMaterial.class, "ativo", true);
		return toSelectItems(r, "id", "descricao");
	}
	
	
	///////////// Telas de navegação ///////////////
	
	/**
	 * <p>Redireciona para a página de pesquisa de exemplares.
	 * <p> Método não chamado por nenhuma JSP.
	 */
	public String telaPesquisaExemplar(){
		return forward(PAGINA_PESQUISA_EXEMPLAR);
	}

	
	
	/////////////// sets e gets//////////////////////

	public Date getDataCriacaoInicio() {
		return dataCriacaoInicio;
	}


	public List<Exemplar> getExemplares() {
		return exemplares;
	}

	public void setExemplares(List<Exemplar> exemplares) {
		this.exemplares = exemplares;
	}

	public void setDataCriacaoInicio(Date dataCriacaoInicio) {
		this.dataCriacaoInicio = dataCriacaoInicio;
	}

	public Date getDataCriacaoFinal() {
		return dataCriacaoFinal;
	}

	/**
	 * Retorna todos as formas de documento selecionadas na pesquisa de exemplares.
	 * 
	 * <p>Método chamado pela(s) seguintes JSPs:
	 * 	<ul><li>sigaa.war/biblioteca/processos_tecnicos/pesquisas_no_acervo/pesquisaExemplar.jsp</li></ul>
	 */
	public List<String> getIdsFormasDocumentoEscolhidos() {
		return idsFormasDocumentoEscolhidos;
	}
	
	
	/** Seta as formas de documento escolhidas pelo usuário eliminando os ids negativos que não são formas de documento válidas. */
	public void setIdsFormasDocumentoEscolhidos(List<String> idsFormasDocumentoEscolhidos) {
		
		this.idsFormasDocumentoEscolhidos = new ArrayList<String>();
		
		for (String string : idsFormasDocumentoEscolhidos) {
			if( Integer.parseInt(string) > 0 )
				this.idsFormasDocumentoEscolhidos.add(string);
		}
	}
	
	public void setDataCriacaoFinal(Date dataCriacaoFinal) {
		this.dataCriacaoFinal = dataCriacaoFinal;
	}

	public void setQtdExemplares(int qtdExemplares) {
		this.qtdExemplares = qtdExemplares;
	}
	
	public int getQtdExemplares() {
		return qtdExemplares;
	}

	public int getOperacaoPesquisar() {
		return OPERACAO_PESQUISAR;
	}
	
	public int getOperacaoBloquearMaterial (){
		return OPERACAO_BLOQUEAR_MATERIAL;
	}
	
	public int getOperacao() {
		return operacao;
	}

	public void setOperacao(int operacao) {
		this.operacao = operacao;
	}

	public boolean isBuscarCodigoBarras() {
		return buscarCodigoBarras;
	}

	public void setBuscarCodigoBarras(boolean buscarCodigoBarras) {
		this.buscarCodigoBarras = buscarCodigoBarras;
	}

	public boolean isBuscarBiblioteca() {
		return buscarBiblioteca;
	}

	public void setBuscarBiblioteca(boolean buscarBiblioteca) {
		this.buscarBiblioteca = buscarBiblioteca;
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
	
	public boolean isBuscarFormaDocumento() {
		return buscarFormaDocumento;
	}

	public void setBuscarFormaDocumento(boolean buscarFormaDocumento) {
		this.buscarFormaDocumento = buscarFormaDocumento;
	}

	public boolean isOperacaoSubstituicao(){
		return operacao == OPERACAO_SUBSTITUICAO;
	}
	
	public boolean isOperacaoBaixar(){
		return operacao == OPERACAO_BAIXAR_EXEMPLAR;
	}
	
	public boolean isOperacaoDesfazerBaixa(){
		return operacao == OPERACAO_DESFAZER_BAIXA_EXEMPLAR;
	}
	
	public boolean isOperacaoRemover(){
		return operacao == OPERACAO_REMOVER_EXEMPLAR;
	}
	
	public boolean isOperacaoProcuraExemplarParaSubstituicao() {
		return operacao == OPERACAO_PROCURA_EXEMPLAR_PARA_SUBSTITUICAO;
	}

	
}