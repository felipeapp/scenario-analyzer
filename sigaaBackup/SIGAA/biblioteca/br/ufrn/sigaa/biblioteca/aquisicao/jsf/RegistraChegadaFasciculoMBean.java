/*
 * Universidade Federal do Rio Grande no Norte
 * Superintendência de Informática
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
 *      <p>  MBean que controla a página onde o usuário registra a chegada de fascículos das assinaturas 
 * e também controla a alteração e remoção desses fascículos antes deles serem incluídos no acervo das bibliotecas.</p> 
 * 
 *      <p>   O registro de chegada vai marcar que um fascículo chegou na biblioteca (para fazer o
 * acompanhamento se os materiais da assinatura comprada estão realmente sendo entregues). </p>
 * 
 * @author Jadson
 * @since 23/01/2009
 * @version 1.0 Criação da classe
 *
 */
@Component("registraChegadaFasciculoMBean")
@Scope("request")
public class RegistraChegadaFasciculoMBean extends SigaaAbstractController<Object> implements PesquisarAcervoAssinaturas{
	
	/** Página para registrar a chegada de um fascículo. */
	public static final String PAGINA_REGISTRA_CHEGADA_FASCICULO = "/biblioteca/aquisicao/registraChegadaFasciculo.jsp";
	
	/** Página para o registro de chegada de um suplemento de um fascículo. */
	public static final String PAGINA_REGISTRA_CHEGADA_SUPLEMENTO = "/biblioteca/aquisicao/registraChegadaSuplemento.jsp";
	
	
	/** Página com lista para alteração ou remoção dos fascículos já registrados, mas ainda não catalogados. */
	public static final String PAGINA_LISTA_FASCICULOS_REGISTRADOS = "/biblioteca/aquisicao/listaFasciculosRegistrados.jsp";
	
	/** Página para alteração de um fascículo registrado. */
	public static final String PAGINA_ALTERA_DADOS_FASCICULOS_REGISTRADOS = "/biblioteca/aquisicao/alteraDadosFasciculosRegistrados.jsp";
	
	
	/** A assinatura selecionada na tela da busca padrão, vai ser a assinatura cujos fascículos vão ser registrados*/
	private Assinatura assinaturaSelecionada;
	
	/** Os fascículos registrados para a assinatura. */
	private List<Fasciculo> fasciculosRegistradosDaAssinatura = new ArrayList<Fasciculo>();
	
	/** Os fascículos da assinatura que já se encontram cadastrados no acervo. */
	private List<Fasciculo> fasciculosNoAcervoDaAssinatura = new ArrayList<Fasciculo>();
	
	/** Indica se os fascículos já incluídos no acervo para a assinatura selecionada devem ser mostrados ou não, por padrão essa informação não é mostrada porque geralmente a quantidade é grande */
	private boolean mostrarFasciculosNoAcervo = false;
	
	
	// Os números que o usuário vai preencher para o fascículo que vai ser registrado
	
	/** Ano cronológico de publicação do fascículo. */
	private String anoCronologico;
	/** Ano de publicação do fascículo. */
	private String ano;
	/** Dia/mês  de publicação do fascículo. */
	private String diaMes;
	/** Número do fascículo. */
	private String numeroFasciculo;
	/** Número do volume do fascículo. */
	private String numeroVolume;
	/** Edição do fascículo. */
	private String edicao;
	

	/** Indica que um suplemento deve ser registrado também. */
	private boolean possuiSuplemento;
	
	
	// Os números que o usuário vai preencher para o suplemento do fascículo que vai ser registrado
	
	/** Ano cronológico de publicação do suplemento de fascículo. */
	private String anoCronologicoSuplemento;
	/** Ano de publicação do suplemento de fascículo. */
	private String anoSuplemento;
	/** Dia/mês  de publicação do suplemento de fascículo. */
	private String diaMesSuplemento;
	/** Número do suplemento de fascículo. */
	private String numeroFasciculoSuplemento;
	/** Número do volume do suplemento de fascículo. */
	private String numeroVolumeSuplemento;
	/** Edição do suplemento de fascículo. */
	private String edicaoSuplemento;
	
	/** O fascículo onde vai ser incluído o suplemento */
	private Fasciculo fasciculoPrincipal;
	
	
	/** Mostra para o usuário os suplementos que o fascículo já tem.*/
	private List<Fasciculo> suplementosDoFasciculoPrincipal;
	
	
	// Indicam quais operações estão sendo realizados
	/** Indica se a operação que está sendo realizada é o registro de fascículos. */
	private boolean registrandoFasciculos = false;
	/** Indica se a operação que está sendo realizada é a alteração de fascículos registrados. */
	private boolean alterandoFasciculosRegistrados = false;
	
	
	/** Guarda as informações do fascículo que vai ser alterado. */
	private Fasciculo fasciculoAlteracao;
	
	
	
	/**
	 *    Inicia o caso de uso de registrar a chegada de fascículos à biblioteca.
	 *
	 *    Busca todas as assinaturas "ativas" para o usuário escolher uma
	 * 
	 *   Chamado a partir da página: /sigaa.war/biblioteca/menu/aquisicoes.jsp
	 * @throws SegurancaException 
	 */
	public String iniciarRegistroChegadaFasciculo(){
		
		limpaOperacoes();
		registrandoFasciculos = true;
		
		AssinaturaPeriodicoMBean bean = getMBean("assinaturaPeriodicoMBean");
		return bean.iniciarPesquisaSelecionarAssinatura(this);
		
	}
	
	
	
	
	/**
	 *    Inicia o caso de uso em que o usuário pode alterar os dados ou apagar os fascículos
	 *    registrados no sistema mas não incluídos no acervo ainda.
	 * 
	 *   Chamado a partir da página: /sigaa.war/biblioteca/menu/aquisicoes.jsp
	 */
	public String iniciarBuscaAlteracaoFasciculosRegistrados(){
		
		limpaOperacoes();
		alterandoFasciculosRegistrados  = true;
		
		AssinaturaPeriodicoMBean bean = getMBean("assinaturaPeriodicoMBean");
		return bean.iniciarPesquisaSelecionarAssinatura(this);
	}
	
	
	
	
	//////////////   Métodos da interface de busca de assinaturas  //////////////
	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * <br><br>
	 * Método não chamado por nenhuma página jsp.
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#setAssinatura(br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura)
	 */
	@Override
	public void setAssinatura(Assinatura assinatura) throws ArqException {
		this.assinaturaSelecionada = assinatura;
	}


	/**
	 * Ver comentários da classe pai.<br/>
	 * 
	 * <br><br>
	 * Método não chamado por nenhuma página jsp.
	 * 
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#selecionaAssinatura()
	 */
	@Override
	public String selecionaAssinatura() throws ArqException {
		
		prepareMovimento(SigaaListaComando.REGISTRA_CHEGADA_FASCICULO); // Está apto a registrar um novo
		
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
			
			limpaDadosFormulario(); // Chamar antes de calcular o próximo número dos fascículos.
			calculaProximosNumerosFasciculoEVolume();
			
			return telaRegistraChegadaFasciculo();
		}
			
		
		if(alterandoFasciculosRegistrados)
			return telaListaFasciculosRegistrados();
		
		return null; // não era para chegar aqui, sempre deve cair em 1 dos 2 if anteriores
		
		
	}


	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#voltarBuscaAssinatura()
	 */
	
	@Override
	public String voltarBuscaAssinatura() throws ArqException {
		return null;
	}


	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#isUtilizaVoltarBuscaAssinatura()
	 */
	
	@Override
	public boolean isUtilizaVoltarBuscaAssinatura() {
		return false;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 *  Limpa as operações que podem ser realizadas na página.
	 */
	private void limpaOperacoes(){
		registrandoFasciculos = false;
		alterandoFasciculosRegistrados = false;
	}
	
	
	
	/**
	 *   Apaga os dados do fascículo que o usuário digitou na página
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
	 * Abre os dados do fascículo selecionado em outra tela para o usuário alterar.
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
	 * Realiza a operação de alterar os dados dos fascículos que foram registrados.
	 * 
	 * Método não chamado por nenhuma página jsp.
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
			
			addMensagemInformation(" Dados do fascículo registrado alterados com sucesso.");
			
			fasciculosRegistradosDaAssinatura = getDAO(FasciculoDao.class).findFasciculosRegistradosDaAssinatura(assinaturaSelecionada.getId());
			Collections.sort(fasciculosRegistradosDaAssinatura, new FasciculoByBibliotecaAnoCronologicoNumeroComparator());
			
		}catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		return telaListaFasciculosRegistrados();
		
	}
	
	
	
	
	
	/**
	 *    Remove o fascículo registrado errado. Como não estava no acervo nem nada, remove
	 * literalmente do banco. Não precisa fazer auditoria sobre quem removeu.
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
			addMensagemErro("O Fascículo selecionado já foi removido do sistema.");
			return cancelar();
		}
		
		f.setAssinatura(getGenericDAO().refresh(f.getAssinatura()));
		
		prepareMovimento(SigaaListaComando.ALTERA_FASCICULOS_REGISTRADOS);
		
		try{
		
			MovimentoAlteraFasciculoRegistrado movimento = new MovimentoAlteraFasciculoRegistrado(f, f.getAssinatura(), true);
			movimento.setCodMovimento(SigaaListaComando.ALTERA_FASCICULOS_REGISTRADOS);
			
			execute(movimento);
			
			addMensagemInformation("Fascículo removido com sucesso.");
			
			fasciculosRegistradosDaAssinatura = getDAO(FasciculoDao.class).findFasciculosRegistradosDaAssinatura(assinaturaSelecionada.getId());
			Collections.sort(fasciculosRegistradosDaAssinatura, new FasciculoByBibliotecaAnoCronologicoNumeroComparator());
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		return telaListaFasciculosRegistrados();
	}
	
	
	
	
	/**
	 *    Calcula o próximo número do fascículo e volume com base nas informações que o usuário
	 *  colocou na hora que criou a assinatura. Esses números são apenas sugestões.
	 */
	private void calculaProximosNumerosFasciculoEVolume(){

		Integer numeroFasciculoTemp = assinaturaSelecionada.getNumeroFasciculoAtual();
		
		if(numeroFasciculoTemp == null){ // O primeiro criado ainda
			numeroFasciculoTemp = assinaturaSelecionada.getNumeroPrimeiroFasciculo();
		}else{
			
			numeroFasciculoTemp++;  // Pega o próximo número
		}
			
		numeroFasciculo = String.valueOf( numeroFasciculoTemp );
		
		// O número do volume permanece o mesmo, o usuário que o aumenta manualmente
		if(assinaturaSelecionada.getNumeroVolumeAtual() != null)
			numeroVolume = String.valueOf( assinaturaSelecionada.getNumeroVolumeAtual());
		
	}
	
	
	
	
	/**
	 *    Método que verifica se o usuário escolheu a opção "incluir o suplemento do fascículo"
	 *    na página.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/aquisicao/registraChegadaFasciculo.jsp
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
	 *    Registra a chegada do fascículo. A data de criação vai ser a data da chegada.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/aquisicao/registraChegadaFasciculo.jsp
	 */
	public String registraChegadoProximoFasciculo() throws  ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO
				, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS
				, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		Fasciculo f = new Fasciculo();
		
		if( StringUtils.isEmpty( numeroFasciculo ) && StringUtils.isEmpty( numeroVolume ) && anoCronologico == null && StringUtils.isEmpty( ano ) && StringUtils.isEmpty( edicao ) ){
			addMensagemErro("Para registrar a chegada é preciso informar pelo menos um dos campos do fascículo.");
			return null;
		}
		
		if( possuiSuplemento && StringUtils.isEmpty( numeroFasciculoSuplemento ) && StringUtils.isEmpty( numeroVolumeSuplemento )
				&& anoCronologicoSuplemento == null && StringUtils.isEmpty( anoSuplemento ) && StringUtils.isEmpty( edicaoSuplemento ) ){
			addMensagemErro("Para registrar a chegada do suplemento é preciso informar pelo menos um dos campos do suplemento.");
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
			
			addMensagemInformation("Chegada do fascículo registrada com sucesso.");
			
			possuiSuplemento = false;
			
			assinaturaSelecionada = getGenericDAO().refresh(assinaturaSelecionada);
			
			
			prepareMovimento(SigaaListaComando.REGISTRA_CHEGADA_FASCICULO);  // para poder registrar o próximo
			
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
	 *    Redireciona para a página onde o usuário vai digitar os dados do suplemento e registrar sua chegada.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/aquisicao/registraChegadaFasciculo.jsp
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
	 *    Registra a chegada de algum suplemento a um fascículo que já foi registrado.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/aquisicao/registraChegadaSuplemento.jsp
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
			addMensagemErro("Para registrar a chegada do suplemento é preciso informar pelo menos um dos campos do suplemento.");
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
	 *    Retorna todas as bibliotecas internas ativas do sistema. São as únicas para as quais
	 *  pode-se criar assinaturas.
	 */
	public Collection <SelectItem> getBibliotecasInternas() throws DAOException{
		
		Collection <Biblioteca> b = getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivas();
		return toSelectItems(b, "id", "descricaoCompleta");
	}
	
	
	/**
	 *  Retorna a quantidade de fascículos registrados que ainda não estão no acervo para asssinatura.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 *  Retorna a quantidade de fascículos já incluídos no acervo para asssinatura.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 *   Implementa a ação de habilitar ou desabilitar a visualiação dos exemplares do Título
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 *    Método que retorna o texto que é mostrado no link que habilita ou desabilita a visualização dos exemplares.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/aquisicao/registraChegadaFasciculo.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String getTextoLinkHabilitarVisualizacaoFasciculos(){
		if(! mostrarFasciculosNoAcervo)
			return "Visualizar os Fascículos já incluídos no acervo.";
		else
			return "Ocultar os Fascículos já incluídos no acervo.";
	}
	
	
	
	//////////  Métodos de navegação ////////////////
	
	/**
	 * Vai para a tela de registro de chegada de fascículos.
	 * <p>
	 * Não é chamado por nenhuma JSP.
	 */
	public String telaRegistraChegadaFasciculo(){
		return forward(PAGINA_REGISTRA_CHEGADA_FASCICULO);
	}

	/**
	 * Vai para a tela de registro de chegada de suplemento.
	 * <p>
	 * Não é chamado por nenhuma JSP.
	 */
	public String telaRegistraChegadaSuplemento(){
		return forward(PAGINA_REGISTRA_CHEGADA_SUPLEMENTO);
	}
	
	/**
	 * Vai para a tela que lista os fascículos registrados.
	 * <p>
	 * Chamado pela JSP /sigaa.war/biblioteca/aquisicao/alteraDadosFasciculosRegistrados.jsp.
	 */
	public String telaListaFasciculosRegistrados(){
		return forward(PAGINA_LISTA_FASCICULOS_REGISTRADOS);
	}
	
	/**
	 * Vai para a tela de alteração de um fascículo registrado.
	 * <p>
	 * Não é chamado por nenhuma JSP.
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
