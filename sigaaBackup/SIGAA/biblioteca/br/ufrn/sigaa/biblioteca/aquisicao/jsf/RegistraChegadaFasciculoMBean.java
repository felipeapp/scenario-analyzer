/*
 * Universidade Federal do Rio Grande no Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 23/01/2009
 * 
 */
package br.ufrn.sigaa.biblioteca.aquisicao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.FasciculoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.aquisicao.negocio.MovimentoAlteraFasciculoRegistrado;
import br.ufrn.sigaa.biblioteca.aquisicao.negocio.MovimentoRegistraChegadaFasciculo;
import br.ufrn.sigaa.biblioteca.aquisicao.negocio.MovimentoRegistraChegadaSuplemento;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.util.FasciculoByBibliotecaAnoCronologicoNumeroComparator;

/**
 *
 *      <p>  MBean que controla a p�gina onde o usu�rio registra a chegada de fasc�culos das assinaturas 
 * e tamb�m controla a altera��o e remo��o desses fasc�culos antes deles serem inclu�dos no acervo das bibliotecas.</p> 
 * 
 *      <p>   O registro de chegada vai marcar que um fasc�culo chegou na biblioteca (para fazer o
 * acompanhamento se os materiais da assinatura comprada est�o realmente sendo entregues). </p>
 * 
 * @author Jadson
 * @since 23/01/2009
 * @version 1.0 Cria��o da classe
 *
 */
@Component("registraChegadaFasciculoMBean")
@Scope("request")
public class RegistraChegadaFasciculoMBean extends SigaaAbstractController<Object> implements PesquisarAcervoAssinaturas{
	
	/** P�gina para registrar a chegada de um fasc�culo. */
	public static final String PAGINA_REGISTRA_CHEGADA_FASCICULO = "/biblioteca/aquisicao/registraChegadaFasciculo.jsp";
	
	/** P�gina para o registro de chegada de um suplemento de um fasc�culo. */
	public static final String PAGINA_REGISTRA_CHEGADA_SUPLEMENTO = "/biblioteca/aquisicao/registraChegadaSuplemento.jsp";
	
	
	/** P�gina com lista para altera��o ou remo��o dos fasc�culos j� registrados, mas ainda n�o catalogados. */
	public static final String PAGINA_LISTA_FASCICULOS_REGISTRADOS = "/biblioteca/aquisicao/listaFasciculosRegistrados.jsp";
	
	/** P�gina para altera��o de um fasc�culo registrado. */
	public static final String PAGINA_ALTERA_DADOS_FASCICULOS_REGISTRADOS = "/biblioteca/aquisicao/alteraDadosFasciculosRegistrados.jsp";
	
	
	/** A assinatura selecionada na tela da busca padr�o, vai ser a assinatura cujos fasc�culos v�o ser registrados*/
	private Assinatura assinaturaSelecionada;
	
	/** Os fasc�culos registrados para a assinatura. */
	private List<Fasciculo> fasciculosRegistradosDaAssinatura = new ArrayList<Fasciculo>();
	
	/** Os fasc�culos da assinatura que j� se encontram cadastrados no acervo. */
	private List<Fasciculo> fasciculosNoAcervoDaAssinatura = new ArrayList<Fasciculo>();
	
	/** Indica se os fasc�culos j� inclu�dos no acervo para a assinatura selecionada devem ser mostrados ou n�o, por padr�o essa informa��o n�o � mostrada porque geralmente a quantidade � grande */
	private boolean mostrarFasciculosNoAcervo = false;
	
	
	// Os n�meros que o usu�rio vai preencher para o fasc�culo que vai ser registrado
	
	/** Ano cronol�gico de publica��o do fasc�culo. */
	private String anoCronologico;
	/** Ano de publica��o do fasc�culo. */
	private String ano;
	/** Dia/m�s  de publica��o do fasc�culo. */
	private String diaMes;
	/** N�mero do fasc�culo. */
	private String numeroFasciculo;
	/** N�mero do volume do fasc�culo. */
	private String numeroVolume;
	/** Edi��o do fasc�culo. */
	private String edicao;
	

	/** Indica que um suplemento deve ser registrado tamb�m. */
	private boolean possuiSuplemento;
	
	
	// Os n�meros que o usu�rio vai preencher para o suplemento do fasc�culo que vai ser registrado
	
	/** Ano cronol�gico de publica��o do suplemento de fasc�culo. */
	private String anoCronologicoSuplemento;
	/** Ano de publica��o do suplemento de fasc�culo. */
	private String anoSuplemento;
	/** Dia/m�s  de publica��o do suplemento de fasc�culo. */
	private String diaMesSuplemento;
	/** N�mero do suplemento de fasc�culo. */
	private String numeroFasciculoSuplemento;
	/** N�mero do volume do suplemento de fasc�culo. */
	private String numeroVolumeSuplemento;
	/** Edi��o do suplemento de fasc�culo. */
	private String edicaoSuplemento;
	
	/** O fasc�culo onde vai ser inclu�do o suplemento */
	private Fasciculo fasciculoPrincipal;
	
	
	/** Mostra para o usu�rio os suplementos que o fasc�culo j� tem.*/
	private List<Fasciculo> suplementosDoFasciculoPrincipal;
	
	
	// Indicam quais opera��es est�o sendo realizados
	/** Indica se a opera��o que est� sendo realizada � o registro de fasc�culos. */
	private boolean registrandoFasciculos = false;
	/** Indica se a opera��o que est� sendo realizada � a altera��o de fasc�culos registrados. */
	private boolean alterandoFasciculosRegistrados = false;
	
	
	/** Guarda as informa��es do fasc�culo que vai ser alterado. */
	private Fasciculo fasciculoAlteracao;
	
	
	
	/**
	 *    Inicia o caso de uso de registrar a chegada de fasc�culos � biblioteca.
	 *
	 *    Busca todas as assinaturas "ativas" para o usu�rio escolher uma
	 * 
	 *   Chamado a partir da p�gina: /sigaa.war/biblioteca/menu/aquisicoes.jsp
	 * @throws SegurancaException 
	 */
	public String iniciarRegistroChegadaFasciculo(){
		
		limpaOperacoes();
		registrandoFasciculos = true;
		
		AssinaturaPeriodicoMBean bean = getMBean("assinaturaPeriodicoMBean");
		return bean.iniciarPesquisaSelecionarAssinatura(this);
		
	}
	
	
	
	
	/**
	 *    Inicia o caso de uso em que o usu�rio pode alterar os dados ou apagar os fasc�culos
	 *    registrados no sistema mas n�o inclu�dos no acervo ainda.
	 * 
	 *   Chamado a partir da p�gina: /sigaa.war/biblioteca/menu/aquisicoes.jsp
	 */
	public String iniciarBuscaAlteracaoFasciculosRegistrados(){
		
		limpaOperacoes();
		alterandoFasciculosRegistrados  = true;
		
		AssinaturaPeriodicoMBean bean = getMBean("assinaturaPeriodicoMBean");
		return bean.iniciarPesquisaSelecionarAssinatura(this);
	}
	
	
	
	
	//////////////   M�todos da interface de busca de assinaturas  //////////////
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * <br><br>
	 * M�todo n�o chamado por nenhuma p�gina jsp.
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#setAssinatura(br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura)
	 */
	@Override
	public void setAssinatura(Assinatura assinatura) throws ArqException {
		this.assinaturaSelecionada = assinatura;
	}


	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * <br><br>
	 * M�todo n�o chamado por nenhuma p�gina jsp.
	 * 
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#selecionaAssinatura()
	 */
	@Override
	public String selecionaAssinatura() throws ArqException {
		
		prepareMovimento(SigaaListaComando.REGISTRA_CHEGADA_FASCICULO); // Est� apto a registrar um novo
		
		FasciculoDao dao = null;
		
		mostrarFasciculosNoAcervo = false;
		fasciculosNoAcervoDaAssinatura = new ArrayList<Fasciculo>();
		
		try{
			dao = getDAO(FasciculoDao.class);
			
			assinaturaSelecionada = dao.refresh(new Assinatura( assinaturaSelecionada.getId()  ));
			
			if(assinaturaSelecionada != null){
				fasciculosRegistradosDaAssinatura = dao.findFasciculosRegistradosDaAssinatura(assinaturaSelecionada.getId());
				Collections.sort(fasciculosRegistradosDaAssinatura, new FasciculoByBibliotecaAnoCronologicoNumeroComparator());
				
			}else{
				addMensagemErro("Selecione uma assinatura");
				return null;
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
		if(registrandoFasciculos){
			
			limpaDadosFormulario(); // Chamar antes de calcular o pr�ximo n�mero dos fasc�culos.
			calculaProximosNumerosFasciculoEVolume();
			
			return telaRegistraChegadaFasciculo();
		}
			
		
		if(alterandoFasciculosRegistrados)
			return telaListaFasciculosRegistrados();
		
		return null; // n�o era para chegar aqui, sempre deve cair em 1 dos 2 if anteriores
		
		
	}


	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#voltarBuscaAssinatura()
	 */
	
	@Override
	public String voltarBuscaAssinatura() throws ArqException {
		return null;
	}


	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#isUtilizaVoltarBuscaAssinatura()
	 */
	
	@Override
	public boolean isUtilizaVoltarBuscaAssinatura() {
		return false;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 *  Limpa as opera��es que podem ser realizadas na p�gina.
	 */
	private void limpaOperacoes(){
		registrandoFasciculos = false;
		alterandoFasciculosRegistrados = false;
	}
	
	
	
	/**
	 *   Apaga os dados do fasc�culo que o usu�rio digitou na p�gina
	 */
	private void limpaDadosFormulario(){
		anoCronologico = null;
		ano = null;
		numeroFasciculo = null;
		numeroVolume = null;
		edicao = null;
		diaMes = null;
		
		anoCronologicoSuplemento = null;
		anoSuplemento = null;
		numeroFasciculoSuplemento = null;
		numeroVolumeSuplemento = null;
		edicaoSuplemento = null;
		diaMesSuplemento = null;
	}
	
		
	
	/**
	 * Abre os dados do fasc�culo selecionado em outra tela para o usu�rio alterar.
	 * <p>
	 * Chamado a partir da JSP: /sigaa.war/biblioteca/aquisicao/listaFasciculosRegistrados.jsp
	 */
	public String preparaAlteracaoDadosFasciculos() throws ArqException{
	
		prepareMovimento(SigaaListaComando.ALTERA_FASCICULOS_REGISTRADOS);
		
		int idFasciculoAlteracao = getParameterInt("idFasciculoAlteracao");
		
		fasciculoAlteracao = getGenericDAO().refresh(new Fasciculo(idFasciculoAlteracao));
		
		fasciculoAlteracao.setAssinatura(getGenericDAO().refresh(fasciculoAlteracao.getAssinatura()));
		
		return telaAlteraDadosFasciculosRegistrados();
	}
	
	
	
	/**
	 * Realiza a opera��o de alterar os dados dos fasc�culos que foram registrados.
	 * 
	 * M�todo n�o chamado por nenhuma p�gina jsp.
	 */
	public String alterarFasciculoRegistrado() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL 
				, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS
				, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO);
		
		try{
			
			MovimentoAlteraFasciculoRegistrado movimento = new MovimentoAlteraFasciculoRegistrado(fasciculoAlteracao, fasciculoAlteracao.getAssinatura(), false);
			movimento.setCodMovimento(SigaaListaComando.ALTERA_FASCICULOS_REGISTRADOS);
			
			execute(movimento);
			
			addMensagemInformation(" Dados do fasc�culo registrado alterados com sucesso.");
			
			fasciculosRegistradosDaAssinatura = getDAO(FasciculoDao.class).findFasciculosRegistradosDaAssinatura(assinaturaSelecionada.getId());
			Collections.sort(fasciculosRegistradosDaAssinatura, new FasciculoByBibliotecaAnoCronologicoNumeroComparator());
			
		}catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		return telaListaFasciculosRegistrados();
		
	}
	
	
	
	
	
	/**
	 *    Remove o fasc�culo registrado errado. Como n�o estava no acervo nem nada, remove
	 * literalmente do banco. N�o precisa fazer auditoria sobre quem removeu.
	 * <p>
	 * Chamado a partir da JSP: /sigaa.war/biblioteca/aquisicao/listaFasciculosRegistrados.jsp
	 */
	public String removeFasciculoRegistrado() throws ArqException{
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL
				, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS
				, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO);
		
		int idFasciculoRemocao = getParameterInt("idFasciculoRemocao");
		
		Fasciculo f = getGenericDAO().refresh(new Fasciculo(idFasciculoRemocao));
		
		if(f == null){
			addMensagemErro("O Fasc�culo selecionado j� foi removido do sistema.");
			return cancelar();
		}
		
		f.setAssinatura(getGenericDAO().refresh(f.getAssinatura()));
		
		prepareMovimento(SigaaListaComando.ALTERA_FASCICULOS_REGISTRADOS);
		
		try{
		
			MovimentoAlteraFasciculoRegistrado movimento = new MovimentoAlteraFasciculoRegistrado(f, f.getAssinatura(), true);
			movimento.setCodMovimento(SigaaListaComando.ALTERA_FASCICULOS_REGISTRADOS);
			
			execute(movimento);
			
			addMensagemInformation("Fasc�culo removido com sucesso.");
			
			fasciculosRegistradosDaAssinatura = getDAO(FasciculoDao.class).findFasciculosRegistradosDaAssinatura(assinaturaSelecionada.getId());
			Collections.sort(fasciculosRegistradosDaAssinatura, new FasciculoByBibliotecaAnoCronologicoNumeroComparator());
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		return telaListaFasciculosRegistrados();
	}
	
	
	
	
	/**
	 *    Calcula o pr�ximo n�mero do fasc�culo e volume com base nas informa��es que o usu�rio
	 *  colocou na hora que criou a assinatura. Esses n�meros s�o apenas sugest�es.
	 */
	private void calculaProximosNumerosFasciculoEVolume(){

		Integer numeroFasciculoTemp = assinaturaSelecionada.getNumeroFasciculoAtual();
		
		if(numeroFasciculoTemp == null){ // O primeiro criado ainda
			numeroFasciculoTemp = assinaturaSelecionada.getNumeroPrimeiroFasciculo();
		}else{
			
			numeroFasciculoTemp++;  // Pega o pr�ximo n�mero
		}
			
		numeroFasciculo = String.valueOf( numeroFasciculoTemp );
		
		// O n�mero do volume permanece o mesmo, o usu�rio que o aumenta manualmente
		if(assinaturaSelecionada.getNumeroVolumeAtual() != null)
			numeroVolume = String.valueOf( assinaturaSelecionada.getNumeroVolumeAtual());
		
	}
	
	
	
	
	/**
	 *    M�todo que verifica se o usu�rio escolheu a op��o "incluir o suplemento do fasc�culo"
	 *    na p�gina.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/aquisicao/registraChegadaFasciculo.jsp
	 */
	public void verificaSuplemento(ValueChangeEvent evt){
		possuiSuplemento = (Boolean) evt.getNewValue();
		
		anoCronologicoSuplemento = anoCronologico;
		anoSuplemento = ano;
		numeroVolumeSuplemento = numeroVolume;
		edicaoSuplemento = edicao;
		numeroFasciculoSuplemento = numeroFasciculo;
		
	}
	
	
	
	
	/**
	 *    Registra a chegada do fasc�culo. A data de cria��o vai ser a data da chegada.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/aquisicao/registraChegadaFasciculo.jsp
	 */
	public String registraChegadoProximoFasciculo() throws  ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO
				, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS
				, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		Fasciculo f = new Fasciculo();
		
		if( StringUtils.isEmpty( numeroFasciculo ) && StringUtils.isEmpty( numeroVolume ) && anoCronologico == null && StringUtils.isEmpty( ano ) && StringUtils.isEmpty( edicao ) ){
			addMensagemErro("Para registrar a chegada � preciso informar pelo menos um dos campos do fasc�culo.");
			return null;
		}
		
		if( possuiSuplemento && StringUtils.isEmpty( numeroFasciculoSuplemento ) && StringUtils.isEmpty( numeroVolumeSuplemento )
				&& anoCronologicoSuplemento == null && StringUtils.isEmpty( anoSuplemento ) && StringUtils.isEmpty( edicaoSuplemento ) ){
			addMensagemErro("Para registrar a chegada do suplemento � preciso informar pelo menos um dos campos do suplemento.");
			return null;
		}
		
		f.setNumero(numeroFasciculo);
		f.setVolume(numeroVolume);
		f.setEdicao(edicao);
		f.setAnoCronologico(anoCronologico);
		f.setAno(ano);
		f.setDiaMes(diaMes);
		
		Fasciculo suplemento = new Fasciculo();
		suplemento.setNumero(numeroFasciculoSuplemento);
		suplemento.setVolume(numeroVolumeSuplemento);
		suplemento.setEdicao(edicaoSuplemento);
		suplemento.setAnoCronologico(anoCronologicoSuplemento);
		suplemento.setAno(anoSuplemento);
		suplemento.setDiaMes(diaMesSuplemento);
		
		MovimentoRegistraChegadaFasciculo mov = null;
		
		if(! possuiSuplemento)
			mov = new MovimentoRegistraChegadaFasciculo(assinaturaSelecionada, f);
		else
			mov = new MovimentoRegistraChegadaFasciculo(assinaturaSelecionada, f, suplemento);
		
		mov.setCodMovimento(SigaaListaComando.REGISTRA_CHEGADA_FASCICULO);
		
		try {
			
			execute(mov);
			
			addMensagemInformation("Chegada do fasc�culo registrada com sucesso.");
			
			possuiSuplemento = false;
			
			assinaturaSelecionada = getGenericDAO().refresh(assinaturaSelecionada);
			
			
			prepareMovimento(SigaaListaComando.REGISTRA_CHEGADA_FASCICULO);  // para poder registrar o pr�ximo
			
			calculaProximosNumerosFasciculoEVolume();
			
			
			fasciculosRegistradosDaAssinatura = getDAO(FasciculoDao.class).findFasciculosRegistradosDaAssinatura(assinaturaSelecionada.getId());
			Collections.sort(fasciculosRegistradosDaAssinatura, new FasciculoByBibliotecaAnoCronologicoNumeroComparator());
			
			
			return telaRegistraChegadaFasciculo();
			
		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens(e.getListaMensagens());
			return null;
		}
		
	
		
	}
	
	
	
	
	/**
	 *    Redireciona para a p�gina onde o usu�rio vai digitar os dados do suplemento e registrar sua chegada.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/aquisicao/registraChegadaFasciculo.jsp
	 */
	public String preparaRegistraSuplemento() throws ArqException{
		
		int idFasciculoPrincipal = getParameterInt("idFasciculoRegistrado");
		
		FasciculoDao dao = getDAO(FasciculoDao.class);
		
		fasciculoPrincipal = dao.refresh(new Fasciculo(idFasciculoPrincipal));
		
		suplementosDoFasciculoPrincipal = dao.findSuplementosDoFasciculo(fasciculoPrincipal.getId());
		
		prepareMovimento(SigaaListaComando.REGISTRA_CHEGADA_SUPLEMENTO);
		
		anoCronologicoSuplemento = fasciculoPrincipal.getAnoCronologico();
		anoSuplemento = fasciculoPrincipal.getAno();
		numeroVolumeSuplemento = fasciculoPrincipal.getVolume();
		edicaoSuplemento = fasciculoPrincipal.getEdicao();
		numeroFasciculoSuplemento = fasciculoPrincipal.getNumero();
		diaMesSuplemento = fasciculoPrincipal.getDiaMes();
		
		return telaRegistraChegadaSuplemento();
	}
	
	
	
	
	/**
	 *    Registra a chegada de algum suplemento a um fasc�culo que j� foi registrado.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/aquisicao/registraChegadaSuplemento.jsp
	 */
	public String registrarSuplementoNoFasciculo() throws  ArqException{
		
		Fasciculo suplemento = new Fasciculo();
		suplemento.setNumero(numeroFasciculoSuplemento);
		suplemento.setVolume(numeroVolumeSuplemento);
		suplemento.setEdicao(edicaoSuplemento);
		suplemento.setAnoCronologico(anoCronologicoSuplemento);
		suplemento.setAno(anoSuplemento);
		suplemento.setDiaMes(diaMesSuplemento);
		
		if( StringUtils.isEmpty( numeroFasciculoSuplemento ) && StringUtils.isEmpty( numeroVolumeSuplemento )
				&& anoCronologicoSuplemento == null && StringUtils.isEmpty( anoSuplemento ) && StringUtils.isEmpty( edicaoSuplemento ) ){
			addMensagemErro("Para registrar a chegada do suplemento � preciso informar pelo menos um dos campos do suplemento.");
			return null;
		}
		
		MovimentoRegistraChegadaSuplemento mov
		= new MovimentoRegistraChegadaSuplemento(fasciculoPrincipal, suplemento, assinaturaSelecionada);
		
		mov.setCodMovimento(SigaaListaComando.REGISTRA_CHEGADA_SUPLEMENTO);
		
		try {
			
			execute(mov);
			
			addMensagemInformation("Chegada do suplemento registrada com sucesso.");
			
			assinaturaSelecionada = getGenericDAO().refresh(assinaturaSelecionada);
			
			fasciculosRegistradosDaAssinatura = getDAO(FasciculoDao.class).findFasciculosRegistradosDaAssinatura(assinaturaSelecionada.getId());
			Collections.sort(fasciculosRegistradosDaAssinatura, new FasciculoByBibliotecaAnoCronologicoNumeroComparator());
		
			limpaDadosFormulario();
			
			calculaProximosNumerosFasciculoEVolume();
			
			return telaRegistraChegadaFasciculo();
			
		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens(e.getListaMensagens());
			return null;
		}
		
		
	}
	
	
	
	
	/**
	 *    Retorna todas as bibliotecas internas ativas do sistema. S�o as �nicas para as quais
	 *  pode-se criar assinaturas.
	 */
	public Collection <SelectItem> getBibliotecasInternas() throws DAOException{
		
		Collection <Biblioteca> b = getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivas();
		return toSelectItems(b, "id", "descricaoCompleta");
	}
	
	
	/**
	 *  Retorna a quantidade de fasc�culos registrados que ainda n�o est�o no acervo para asssinatura.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/aquisicao/registraChegadaFasciculo.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public int getQtdFasciculosRegistradosDaAssinatura(){
		if(fasciculosRegistradosDaAssinatura == null)
			return 0;
		else
			return fasciculosRegistradosDaAssinatura.size();
	}
	
	/**
	 *  Retorna a quantidade de fasc�culos j� inclu�dos no acervo para asssinatura.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/aquisicao/registraChegadaFasciculo.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public int getQtdFasciculosNoAcervoDaAssinatura(){
		if(fasciculosNoAcervoDaAssinatura == null)
			return 0;
		else
			return fasciculosNoAcervoDaAssinatura.size();
	}
	
	
	
	/**
	 *   Implementa a a��o de habilitar ou desabilitar a visualia��o dos exemplares do T�tulo
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/aquisicao/registraChegadaFasciculo.jsp</li>
	 *   </ul>
	 *
	 * @param evt
	 * @throws DAOException 
	 */
	public void habilitaVisualizacaoFasciculosNoAcervo(ActionEvent evt) throws DAOException{
		
		mostrarFasciculosNoAcervo = ! mostrarFasciculosNoAcervo;
		
		if(mostrarFasciculosNoAcervo && ( fasciculosNoAcervoDaAssinatura == null || fasciculosNoAcervoDaAssinatura.size() == 0 ) ){
			
			
			FasciculoDao dao = null;
			
			try{
				dao = getDAO(FasciculoDao.class);
				
				fasciculosNoAcervoDaAssinatura = dao.findFasciculosAtivosNoAcervoDaAssinatura(assinaturaSelecionada.getId());
				Collections.sort(fasciculosNoAcervoDaAssinatura, new FasciculoByBibliotecaAnoCronologicoNumeroComparator());
			
			}finally{
				if(dao != null) dao.close();
			}
		}
		
	}
	
	
	/**
	 *    M�todo que retorna o texto que � mostrado no link que habilita ou desabilita a visualiza��o dos exemplares.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/aquisicao/registraChegadaFasciculo.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String getTextoLinkHabilitarVisualizacaoFasciculos(){
		if(! mostrarFasciculosNoAcervo)
			return "Visualizar os Fasc�culos j� inclu�dos no acervo.";
		else
			return "Ocultar os Fasc�culos j� inclu�dos no acervo.";
	}
	
	
	
	//////////  M�todos de navega��o ////////////////
	
	/**
	 * Vai para a tela de registro de chegada de fasc�culos.
	 * <p>
	 * N�o � chamado por nenhuma JSP.
	 */
	public String telaRegistraChegadaFasciculo(){
		return forward(PAGINA_REGISTRA_CHEGADA_FASCICULO);
	}

	/**
	 * Vai para a tela de registro de chegada de suplemento.
	 * <p>
	 * N�o � chamado por nenhuma JSP.
	 */
	public String telaRegistraChegadaSuplemento(){
		return forward(PAGINA_REGISTRA_CHEGADA_SUPLEMENTO);
	}
	
	/**
	 * Vai para a tela que lista os fasc�culos registrados.
	 * <p>
	 * Chamado pela JSP /sigaa.war/biblioteca/aquisicao/alteraDadosFasciculosRegistrados.jsp.
	 */
	public String telaListaFasciculosRegistrados(){
		return forward(PAGINA_LISTA_FASCICULOS_REGISTRADOS);
	}
	
	/**
	 * Vai para a tela de altera��o de um fasc�culo registrado.
	 * <p>
	 * N�o � chamado por nenhuma JSP.
	 */
	public String telaAlteraDadosFasciculosRegistrados(){
		return forward(PAGINA_ALTERA_DADOS_FASCICULOS_REGISTRADOS);
	}
	
	////////////////// Sets e gets ///////////
	

	public Assinatura getAssinaturaSelecionada() {
		return assinaturaSelecionada;
	}

	public void setAssinaturaSelecionada(Assinatura assinaturaSelecionada) {
		this.assinaturaSelecionada = assinaturaSelecionada;
	}
	
	public String getAnoCronologico() {
		return anoCronologico;
	}

	public void setAnoCronologico(String anoCronologico) {
		this.anoCronologico = anoCronologico;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public String getNumeroFasciculo() {
		return numeroFasciculo;
	}

	public void setNumeroFasciculo(String numeroFasciculo) {
		this.numeroFasciculo = numeroFasciculo;
	}

	public String getNumeroVolume() {
		return numeroVolume;
	}

	public void setNumeroVolume(String numeroVolume) {
		this.numeroVolume = numeroVolume;
	}

	public String getEdicao() {
		return edicao;
	}

	public void setEdicao(String edicao) {
		this.edicao = edicao;
	}

	public boolean isPossuiSuplemento() {
		return possuiSuplemento;
	}

	public void setPossuiSuplemento(boolean possuiSuplemento) {
		this.possuiSuplemento = possuiSuplemento;
	}

	public String getAnoCronologicoSuplemento() {
		return anoCronologicoSuplemento;
	}

	public void setAnoCronologicoSuplemento(String anoCronologicoSuplemento) {
		this.anoCronologicoSuplemento = anoCronologicoSuplemento;
	}

	public String getAnoSuplemento() {
		return anoSuplemento;
	}

	public void setAnoSuplemento(String anoSuplemento) {
		this.anoSuplemento = anoSuplemento;
	}

	public String getNumeroFasciculoSuplemento() {
		return numeroFasciculoSuplemento;
	}

	public void setNumeroFasciculoSuplemento(String numeroFasciculoSuplemento) {
		this.numeroFasciculoSuplemento = numeroFasciculoSuplemento;
	}

	public String getNumeroVolumeSuplemento() {
		return numeroVolumeSuplemento;
	}

	public void setNumeroVolumeSuplemento(String numeroVolumeSuplemento) {
		this.numeroVolumeSuplemento = numeroVolumeSuplemento;
	}

	public String getEdicaoSuplemento() {
		return edicaoSuplemento;
	}

	public void setEdicaoSuplemento(String edicaoSuplemento) {
		this.edicaoSuplemento = edicaoSuplemento;
	}

	public Fasciculo getFasciculoPrincipal() {
		return fasciculoPrincipal;
	}

	public void setFasciculoPrincipal(Fasciculo fasciculoPrincipal) {
		this.fasciculoPrincipal = fasciculoPrincipal;
	}

	public List<Fasciculo> getSuplementosDoFasciculoPrincipal() {
		return suplementosDoFasciculoPrincipal;
	}

	public void setSuplementosDoFasciculoPrincipal(List<Fasciculo> suplementosDoFasciculoPrincipal) {
		this.suplementosDoFasciculoPrincipal = suplementosDoFasciculoPrincipal;
	}

	public boolean isRegistrandoFasciculos() {
		return registrandoFasciculos;
	}

	public void setRegistrandoFasciculos(boolean registrandoFasciculos) {
		this.registrandoFasciculos = registrandoFasciculos;
	}

	public boolean isAlterandoFasciculosRegistrados() {
		return alterandoFasciculosRegistrados;
	}

	public void setAlterandoFasciculosRegistrados(boolean alterandoFasciculosRegistrados) {
		this.alterandoFasciculosRegistrados = alterandoFasciculosRegistrados;
	}
	
	public List<Fasciculo> getFasciculosRegistradosDaAssinatura() {
		return fasciculosRegistradosDaAssinatura;
	}

	public void setFasciculosRegistradosDaAssinatura(List<Fasciculo> fasciculosRegistradosDaAssinatura) {
		this.fasciculosRegistradosDaAssinatura = fasciculosRegistradosDaAssinatura;
	}

	public List<Fasciculo> getFasciculosNoAcervoDaAssinatura() {
		return fasciculosNoAcervoDaAssinatura;
	}

	public void setFasciculosNoAcervoDaAssinatura(	List<Fasciculo> fasciculosNoAcervoDaAssinatura) {
		this.fasciculosNoAcervoDaAssinatura = fasciculosNoAcervoDaAssinatura;
	}

	public Fasciculo getFasciculoAlteracao() {
		return fasciculoAlteracao;
	}

	public void setFasciculoAlteracao(Fasciculo fasciculoAlteracao) {
		this.fasciculoAlteracao = fasciculoAlteracao;
	}

	public String getDiaMes() {
		return diaMes;
	}

	public void setDiaMes(String mes) {
		this.diaMes = mes;
	}

	public String getDiaMesSuplemento() {
		return diaMesSuplemento;
	}

	public void setDiaMesSuplemento(String mesSuplemento) {
		this.diaMesSuplemento = mesSuplemento;
	}

	public boolean isMostrarFasciculosNoAcervo() {
		return mostrarFasciculosNoAcervo;
	}

	public void setMostrarFasciculosNoAcervo(boolean mostrarFasciculosNoAcervo) {
		this.mostrarFasciculosNoAcervo = mostrarFasciculosNoAcervo;
	}

}
