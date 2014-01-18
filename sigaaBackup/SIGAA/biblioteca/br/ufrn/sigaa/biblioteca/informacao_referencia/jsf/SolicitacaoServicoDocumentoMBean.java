/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 12/04/2011
 *
 */

package br.ufrn.sigaa.biblioteca.informacao_referencia.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.WordUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.EnvioEmailBiblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dao.ServicoInformacaoReferenciaBibliotecaDAO;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dao.SolicitacaoServicoDocumentoDAO;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.SolicitacaoCatalogacao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.SolicitacaoNormalizacao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.SolicitacaoServico.TipoSituacao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.SolicitacaoServicoDocumento;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoDocumentoNormalizacaoCatalogacao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoServicoInformacaoReferencia;
import br.ufrn.sigaa.biblioteca.informacao_referencia.negocio.ProcessadorSolicitacaoOrientacao;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 * <p>MBean responsável pelos atendimento e solicitação de serviços realizada por discentes ou servidores à biblioteca.</p>
 * 
 * <p> MBean que contém a parte comum das duas solicitações, Catalogação e Normalização.  Os MBeans com os dados específicos são 
 * SolicitacaoCatalogacaoMBean e  SolicitacaoNormalizacaoMBean
 * </p>
 * 
 * @author Felipe Rivas
 */
@Component("solicitacaoServicoDocumentoMBean")
@Scope("request")
public class SolicitacaoServicoDocumentoMBean extends SigaaAbstractController<SolicitacaoServicoDocumento> {
	
	/** Bibliotecário visualiza as solicitações para atender*/
	public static final String PAGINA_VISUALIZA_SOLICITACOES =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/listarSolicitacoes.jsf";
	
	/** Usuário visualiza as solicitações feitas por ele */
	public static final String PAGINA_VISUALIZA_MINHAS_SOLICITACOES =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/minhasSolicitacoes.jsf";
	
	/** O bibliotecário transfere uma solicitação da sua biblioteca para a central */
	public static final String PAGINA_TRANSFERENCIA_SOLICITACAO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/formTransferenciaSolicitacao.jsp";
	
	/** Página na qual o bibliotecário vai notificar alguém sobre a solicitação, geralmente o setor de processos técnicos para realizar o atendimento. */
	public static final String PAGINA_NOTIFICAR_SOLICITACAO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/formNotificarSolicitacao.jsp";
	
	
	/** O comprovante de solicitação de normalização. */
	public static final String PAGINA_VISUALIZA_COMPROVANTE_SOLICITACAO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/comprovanteSolicitacao.jsp";
	
	
	/**
	 * Guarda as solicitações feitas pelo usuário quando o caso de uso é de realizar uma
	 * solicitação de serviço.
	 * Guarda as solicitações que o bibliotecário buscou na atendimento das solicitações
	 */
	private final SortedSet<SolicitacaoServicoDocumento> solicitacoes;
	
	/** usado no botão no qual o usuário faz a solicitação ou a altera */
	public static final String TEXTO_ALTERAR = "Alterar";
	
	/**
	 * Biblioteca para as quais o usuário pode realizar as solicitações.
	 */
	private Collection<Biblioteca> bibliotecas = new ArrayList<Biblioteca>();
	
	/** Usado na transferência de solicitações */
	private Collection<Biblioteca> bibliotecasServico = new ArrayList<Biblioteca>();
	
	/** Armazena a operação que está sendo executada no momento. */
	private int operacao;
	
	/** Campo para busca do usuário pelo número da solicitação */
	private Integer numeroSolicitacao;
	
	/** Campo para busca do usuário pelo nome de quem realizou a solicitação  */
	private String nomeUsuarioSolicitante;
	
	/** Indica se o filtro de numero da solicitação está ativado */
	private boolean buscarNumeroSolicitacao;
	
	/** Indica se o filtro de data está ativado */
	private boolean buscarData;
	
	/** Indica se o filtro de nome do solicitante está ativado */
	private boolean buscarNomeUsuarioSolicitante;
	
	/** Tipo de serviço selecionado no filtro de busca */
	private Integer tipoServico;
	/** Tipo de documento selecionado no filtro de busca */
	private Integer tipoDocumento;
	
	/** Limite inicial da data de cadastro selecionado no filtro de busca */
	private Date dataInicial; 
	/** Limite final da data de cadastro selecionado no filtro de busca */
	private Date dataFinal;
	/** Indica se as solicitações canceladas devem ser incluídas no resultado da busca */
	private boolean buscarCanceladas;
	/** Indica se as solicitações atendidas devem ser incluídas no resultado da busca */
	private boolean buscarAtendidas;
	/** Retorna as solicitações removidas pelo usuário, antes mesmo do atendimento ser realizado. */
	private boolean buscarRemovidasPeloUsuario;
	/** Indica se o filtro de tipo de serviço está ativado */
	private boolean buscarTipoServico;
	/** Indica se o filtro de tipo de documento está ativado */
	private boolean buscarTipoDocumento;
	
	/** Valor padrão para indicar a seleção de todas as bibliotecas no filtro de busca */
	public static final int TODAS_BIBLIOTECAS = -1;
	/** Biblioteca selecionada no filtro de busca */
	private Biblioteca biblioteca = new Biblioteca(-1);
	
	/** Usado na transferência de solicitações */
	private Biblioteca bibliotecaDestino = new Biblioteca(-1);
	
	/** Operação de atender. */
	public static final int ATENDER = 2;
	/** Operação de cancelar. */
	public static final int CANCELAR = 3;
	
	/** Se o bibliotecário cancelar uma solicitação tem que informar ao usuário que a solicitou */
	private String motivoCancelamento;
	
	/** Email para onde será enviado a notificação sobre a solicitação de catalogação. */
	private String emailNotificacao;
	
	/** O Texto que será enviado no email de notificação sobre a solicitação de catalogação.  */
	private String textoNotificacao;
	
	
	public SolicitacaoServicoDocumentoMBean() throws DAOException {
		solicitacoes = new TreeSet<SolicitacaoServicoDocumento>(
				new Comparator<SolicitacaoServicoDocumento>() {
					@Override
					public int compare(SolicitacaoServicoDocumento o1,
							SolicitacaoServicoDocumento o2) {
						return - o1.getDataCadastro().compareTo( o2.getDataCadastro() );
					}
				}
				);
		//initObj();
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////  Realização da solicitação //////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Inicia o caso de uso onde o usuário visualiza as suas solicitações feitas.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:</p>
	 * <ul>
	 * 	<li>/portais/discente/menu_discente.jsp</li>
	 * 	<li>/portais/docente/menu_docente.jsp</li>
	 * 	<li>/biblioteca/menus/modulo_biblioteca_servidor.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException 
	 */
	public String verMinhasSolicitacoes() throws DAOException {
		
		//initObj();
		solicitacoes.clear();
		
		SolicitacaoServicoDocumentoDAO dao = null;
		
		try {
			dao = getDAO(SolicitacaoServicoDocumentoDAO.class);
			
			//// ENCONTRA AS SOLICITAÇÕES FEITAS PELA PESSOA LOGADA ////////
			
			// Usuário acessou pelo portal discente
			if (SigaaSubsistemas.PORTAL_DISCENTE.equals(getSubSistema())){
				if (getDiscenteUsuario() != null ){
					solicitacoes.addAll( dao.findSolicitacoesAtivas(getDiscenteUsuario().getPessoa(), null, null, null, null, null, null,
						null, null, null, true, (TipoSituacao[]) null) );
					return telaMinhasSolicitacoes();
				}
			// Usuário acessou pelo portal docente (professor)
			} else if (SigaaSubsistemas.PORTAL_DOCENTE.equals(getSubSistema())){
				if (getUsuarioLogado().getServidor() != null ){
					solicitacoes.addAll( dao.findSolicitacoesAtivas(getUsuarioLogado().getServidor().getPessoa(),  null, null, null, null, null, null,
						null, null, null, true, (TipoSituacao[]) null) );
					return telaMinhasSolicitacoes();
				}
			// Usuário acessou pelo módulo do servidor na biblioteca (servidor ou professor)
			} else if (getUsuarioLogado().getServidor() != null ){
				solicitacoes.addAll( dao.findSolicitacoesAtivas(getUsuarioLogado().getServidor().getPessoa(),  null, null, null, null, null, null,
						null, null, null, true,  (TipoSituacao[]) null) );
				return telaMinhasSolicitacoes();
			}
			
			addMensagemErro("Usuário não pode realizar solicitações de serviços da biblioteca, usuário não é discente nem servidor da instituição.");
			
			return null;			
		} finally {
			if (dao != null) dao.close();
		}
	}
	
	/////////////////  fim da parte de realizar um solicitação  ////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////   Parte onde o usuário pode alterar e visualizar os dados das suas solicitações ////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Altera os dados da solicitação feita.
	 * O usuário pode alterar solicitações que estão com status "Solicitado".
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:</p>
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/minhasSolicitacoes.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException 
	 */
	public String alterarSolicitacao() throws ArqException {
		obj = buscarNaListaByID(getParameterInt("idSolicitacao", 0));
		
		if(obj == null){
			addMensagemErro("Solicitação já processada!");
			return null;
		}
		
		ISolicitacaoServicoDocumentoMBean<SolicitacaoServicoDocumento> solicitacaoServicoMbean = getSolicitacaoServicoMBean(obj);
		
		return solicitacaoServicoMbean.alterarSolicitacao();
	}
	
	/**
	 * Remove a solicitação selecionada.
	 * O usuário pode remover solicitações que estão com status "Solicitado".
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:</p>
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/minhasSolicitacoes.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException 
	 */
	public String removerSolicitacao() throws DAOException {
		obj = buscarNaListaByID(getParameterInt("idSolicitacao", 0));
		
		if(obj == null){
			addMensagemErro("Solicitação já processada!");
			return null;
		}
		
		ISolicitacaoServicoDocumentoMBean<SolicitacaoServicoDocumento> solicitacaoServicoMbean = getSolicitacaoServicoMBean(obj);
		
		return solicitacaoServicoMbean.removerSolicitacao();
	}
	
	/**
	 * Exibe as informações completas da solicitação para o usuário.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:</p>
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/minhasSolicitacoes.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException 
	 */
	public String visualizarDadosSolicitacao() throws ArqException {
		obj = buscarNaListaByID(getParameterInt("idSolicitacao", 0));
		
		if(obj == null){
			addMensagemErro("Solicitação já processada!");
			return null;
		}
		
		ISolicitacaoServicoDocumentoMBean<SolicitacaoServicoDocumento> solicitacaoServicoMbean = getSolicitacaoServicoMBean(obj);
		
		return solicitacaoServicoMbean.visualizarDadosSolicitacao();
	}
	
	/**
	 * Exibe as informações completsa da solicitação para o bibliotecário.
	 * Possui algumas informações a mais como por quem foi validada, por quem foi atendida.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:</p>
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/listarSolicitacoes.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException 
	 */
	public String visualizarDadosSolicitacaoAtendimento() throws ArqException {
		setConfirmButton("Visualizar Dados Solicitação");
		
		operacao = -1;

		obj = buscarNaListaByID(getParameterInt("idSolicitacao", 0));
		
		if(obj == null){
			addMensagemErro("Solicitação já processada!");
			return null;
		}
		
		ISolicitacaoServicoDocumentoMBean<SolicitacaoServicoDocumento> solicitacaoServicoMbean = getSolicitacaoServicoMBean(obj);
		
		return solicitacaoServicoMbean.visualizarDadosSolicitacaoAtendimento();
	}

	/**
	 * Retorna a instância do managed bean referente ao tipo de solicitação tratada.
	 * 
	 * @param obj
	 * @return
	 */
	private ISolicitacaoServicoDocumentoMBean<SolicitacaoServicoDocumento> getSolicitacaoServicoMBean(SolicitacaoServicoDocumento obj) {
		ISolicitacaoServicoDocumentoMBean<SolicitacaoServicoDocumento> solicitacaoServicoMbean = null;

		if (obj instanceof SolicitacaoCatalogacao) {
			solicitacaoServicoMbean = getMBean("solicitacaoCatalogacaoMBean");
		}
		else if (obj instanceof SolicitacaoNormalizacao) {
			solicitacaoServicoMbean = getMBean("solicitacaoNormalizacaoMBean");
		}
		/*else if (obj instanceof SolicitacaoOrientacao) {
			solicitacaoServicoMbean = getMBean("solicitacaoOrientacaoMBean");
		}*/
		else {
			throw new IllegalArgumentException("Tipo de solicitação inválida.");
		}
		
		return solicitacaoServicoMbean;
	}
	
	/**
	 * Retorna umaa solicitação de serviço da lista de solicitações de acordo com seu id.
	 * 
	 * @param id
	 * @return
	 */
	protected SolicitacaoServicoDocumento buscarNaListaByID(Integer id) {
		if (id != null) {
			for (SolicitacaoServicoDocumento solicitacao : solicitacoes) {
				if (solicitacao.getId() == id) {
					return solicitacao;
				}
			}
		}
		else {
			throw new IllegalArgumentException("Valor do parâmetro ID é inválido.");
		}
		
		return null;
	}
	
	/////////////////////  Fim da parte de alteração da solicitação pelo usuário /////////////////////////
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////  Parte do fluxo onde o bibliotecário atende as solicitações de serviço       //////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Exibe a tela com as solicitações a serem atendidas para o bibliotecário.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:</p>
	 * <ul>
	 * 	<li>/portais/discente/menu_discente.jsp</li>
	 * 	<li>/portais/docente/menu_docente.jsp</li>
	 * 	<li>/biblioteca/menus/modulo_biblioteca_servidor.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException 
	 */
	public String verSolicitacoes() throws ArqException {
		//initObj();

		obtemBibliotecasAtendimento();
		
		
		if(bibliotecas == null || bibliotecas.size() == 0 ){
			addMensagemWarning("O senhor(a) não possui permissão em nenhuma biblioteca para gerenciar as solicitações de catalogação ou normalização.");
		}
		
		
		return telaListaSolicitacoes();
	}
	
	/**
	 * Busca todas as solicitações de serviço não canceladas
	 * cadastradas no sistema de acordo com os filtros selecionados na página.<br/><br/>
	 * 
	 * <p>Usado quando o bibliotecário vai pesquisar as solicitações para atendê-las.</p>
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:</p>
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/listarSolicitacoes.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException 
	 */
	public String buscarSolicitacoesSolicitadas() throws DAOException {
		
		try {
			SolicitacaoServicoDocumentoDAO dao = getDAO(SolicitacaoServicoDocumentoDAO.class);
			
			Biblioteca bibliotecaBusca;
			
			if( biblioteca.getId() == TODAS_BIBLIOTECAS )
				bibliotecaBusca = null;
			else
				bibliotecaBusca = new Biblioteca(biblioteca.getId());
	
			List<TipoSituacao> situacoes = new ArrayList<TipoSituacao>();
	
			TipoServicoInformacaoReferencia objTipoServico = getObjTipoServico();
			TipoDocumentoNormalizacaoCatalogacao objTipoDocumento = getObjTipoDocumento();
			
			situacoes.add(TipoSituacao.SOLICITADO);
			//situacoes.add(TipoSituacao.VALIDADO);
			situacoes.add(TipoSituacao.REENVIADO);
			
			if(buscarCanceladas)
				situacoes.add(TipoSituacao.CANCELADO);
				
			if(buscarAtendidas) {
				situacoes.add(TipoSituacao.ATENDIDO);
				situacoes.add(TipoSituacao.ATENDIDO_FINALIZADO);
			}
			
			Integer numeroSolicitacaoBusca = null;
			String nomeUsuarioSolicitanteBusca = null;
			
			if( buscarNumeroSolicitacao) numeroSolicitacaoBusca = numeroSolicitacao;
			if( buscarNomeUsuarioSolicitante) nomeUsuarioSolicitanteBusca = nomeUsuarioSolicitante;
			
			Date dataInicialBusca = null;
			Date dataFinalBusca = null;
			
			if( buscarData) {  dataInicialBusca = dataInicial; dataFinalBusca = dataFinal; };
			
			if(dataInicialBusca != null && dataFinalBusca != null){
				if( CalendarUtils.calculoDias(dataInicialBusca, dataFinalBusca) < 0){
					addMensagem(MensagensArquitetura.DATA_INICIO_MENOR_FIM, "Data da Solicitação");
					return null;
				}
			}
			
			solicitacoes.clear();
			
			solicitacoes.addAll( dao.findSolicitacoesAtivas(null, null, null, numeroSolicitacaoBusca, nomeUsuarioSolicitanteBusca, objTipoServico, objTipoDocumento, bibliotecaBusca,
					dataInicialBusca, dataFinalBusca, ! buscarRemovidasPeloUsuario, situacoes.toArray(new TipoSituacao[situacoes.size()])) );
			
			if(solicitacoes.size() == 0)
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			
			if(solicitacoes.size() >= SolicitacaoServicoDocumentoDAO.QTD_MAXIMA_RESULTADOS)
				addMensagem(MensagensArquitetura.BUSCA_MAXIMO_RESULTADOS, SolicitacaoServicoDocumentoDAO.QTD_MAXIMA_RESULTADOS);
			
		} catch (IllegalArgumentException iaex) {
			addMensagemErro(iaex.getMessage());
		}
		
		return telaListaSolicitacoes();
	}

	/**
	 * Retorna o tipo de documento de acordo com o valor selecionado no filtro de busca.
	 * 
	 * @return
	 */
	private TipoDocumentoNormalizacaoCatalogacao getObjTipoDocumento() {
		if (buscarTipoDocumento) {
			if (tipoDocumento != -1) {
				return new TipoDocumentoNormalizacaoCatalogacao(tipoDocumento);
			}
			
			throw new IllegalArgumentException("Selecione o tipo de documento desejado ou desmarque este filtro.");
		}
		
		return null;
	}

	/**
	 * Retorna o tipo de serviço de acordo com o valor selecionado no filtro de busca.
	 * 
	 * @return
	 */
	private TipoServicoInformacaoReferencia getObjTipoServico() {
		if (buscarTipoServico) {
			for (TipoServicoInformacaoReferencia servico : TipoServicoInformacaoReferencia.values()) {
				if (servico.getValor() == tipoServico) {
					return servico;
				}
			}
			
			throw new IllegalArgumentException("Selecione o tipo de serviço desejado ou desmarque este filtro.");
		}

		//throw new IllegalArgumentException("Tipo de serviço inválido.");
		return null;
	}
	
	/**
	 * Apaga os resultados da busca.
	 *
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/listarSolicitacoes.jsp
	 */
	public String limparResultadosBusca() {
		solicitacoes.clear();
		buscarTipoServico = false;
		buscarAtendidas = false;
		buscarCanceladas = false;
		buscarTipoDocumento = false;
		buscarNumeroSolicitacao =false;
		buscarNomeUsuarioSolicitante =false;
		biblioteca = new Biblioteca(TODAS_BIBLIOTECAS);
		tipoDocumento = null;
		tipoServico = null;
		dataFinal = null;
		dataInicial = null;
		
		return telaListaSolicitacoes();
	}
	
	/*/**
	 * Valida uma solicitação de serviço.
	 * 
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/listarSolicitacoes.jsp
	 *
	public String validarSolicitacao() throws ArqException{
		obj = buscarNaListaByID(getParameterInt("idSolicitacao", 0));
		ISolicitacaoServicoDocumentoMBean<SolicitacaoServicoDocumento> solicitacaoServicoMbean = getSolicitacaoServicoMBean(obj);
		
		return solicitacaoServicoMbean.validarSolicitacao();
	}*/
	
	/**
	 * Atende uma solicitação de serviço.
	 * 
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/listarSolicitacoes.jsp
	 */
	public String atenderSolicitacao() throws ArqException{
		obj = buscarNaListaByID(getParameterInt("idSolicitacao", 0));
		if(obj == null){
			addMensagemErro("Solicitação já processada!");
			return null;
		}
		ISolicitacaoServicoDocumentoMBean<SolicitacaoServicoDocumento> solicitacaoServicoMbean = getSolicitacaoServicoMBean(obj);
		
		return solicitacaoServicoMbean.atenderSolicitacao();
	}
	
	/**
	 * Cancela uma solicitação de serviço.
	 * 
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/listarSolicitacoes.jsp
	 */
	public String cancelarSolicitacao() throws ArqException {
		obj = buscarNaListaByID(getParameterInt("idSolicitacao", 0));
		
		if(obj == null){
			addMensagemErro("Solicitação já processada!");
			return null;
		}
		
		ISolicitacaoServicoDocumentoMBean<SolicitacaoServicoDocumento> solicitacaoServicoMbean = getSolicitacaoServicoMBean(obj);
		
		return solicitacaoServicoMbean.cancelarSolicitacao();
	}	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////    Parte de transferência da solicitação     ////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Exibe o formulário para modificar a biblioteca da solicitação selecionada.
	 * 
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/listarSolicitacoes.jsp
	 */
	public String preTransferirSolicitacao() throws ArqException {
		checkRole (SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);
		//initObj();

		obj = buscarNaListaByID(getParameterInt("idSolicitacao", 0));
		
		if(obj == null){
			addMensagemErro("Solicitação já processada!");
			return null;
		}
		
		ISolicitacaoServicoDocumentoMBean<SolicitacaoServicoDocumento> solicitacaoServicoMbean = getSolicitacaoServicoMBean(obj);
		
		bibliotecasServico = new ArrayList<Biblioteca>();
		
		ServicoInformacaoReferenciaBibliotecaDAO dao = null;
		
		try {
			dao = getDAO(ServicoInformacaoReferenciaBibliotecaDAO.class);
			
			if(obj.getBiblioteca().isBibliotecaCentral()) // A central pode transferir para as setoriais
				bibliotecasServico = dao.findBibliotecasInternasByServico(
						solicitacaoServicoMbean.getTipoServico(), false, true);
			else // As setoriais só podem transferir para a central
				bibliotecasServico = dao.findBibliotecasInternasByServico(
						solicitacaoServicoMbean.getTipoServico(), true, false);
			
			if (bibliotecasServico.size() == 0) {
				addMensagemErro("No momento não há outras bibliotecas disponibilizando este serviço.");
			}
			
			bibliotecaDestino = new Biblioteca(-1);
			prepareMovimento(ArqListaComando.ALTERAR);
			
			return forward(PAGINA_TRANSFERENCIA_SOLICITACAO);
		} catch(Exception ex) {
			throw new ArqException(ex);
		} finally {
			if (dao != null) dao.close();
		}
	}
	
	
	/**
	 * Redireciona para a página utilizada para notificar alguém sobre o atendimento da solicitação.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/listarSolicitacoes.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String preNotificarSolicitacao() {
		
		
		obj = buscarNaListaByID(getParameterInt("idSolicitacao", 0));
		
		if(obj == null){
			addMensagemErro("Solicitação já processada!");
			return null;
		}
		
		emailNotificacao = "";
		textoNotificacao ="Prezado setor de                  , <br/><br/>"
			+"  Informamos que a solicitação de "+obj.getTipoServico()+" número "+obj.getNumeroSolicitacao()+" do usuário: "+obj.getPessoa().getNome()
			+" para a biblioteca: "+obj.getBiblioteca().getDescricao()
			+" está aguardando atendimento pelo seu setor. "+"<br/>"
			+ProcessadorSolicitacaoOrientacao.ASSINATURA_SETOR_INFORMACAO_E_REFERENCIA;
		
		return forward(PAGINA_NOTIFICAR_SOLICITACAO);
	}
	
	
	/**
	 *  Email um email para notificar o usuário sobre a solicitação pendente de atendimento.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/formNotificarSolicitacao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String notificarSolicitacao() {
		
		if(StringUtils.isEmpty(emailNotificacao))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Email");
			
		if(StringUtils.isEmpty(textoNotificacao))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Mensagem");
				
		if(hasErrors())
			return null;
		
		new EnvioEmailBiblioteca().enviaEmailSimples(null, emailNotificacao, "Notificação de Solicitação Pendente de Atendimento"
				, "Solicitação Pendente", EnvioEmailBiblioteca.AVISO_INFORMACAO_REFERENCIA, textoNotificacao);
		
		addMensagemInformation("A notificação enviada para o email "+emailNotificacao);
		
		return telaListaSolicitacoes();
	}
	
	
	
	/**
	 * Transfere uma solicitação, alterando sua biblioteca responsável.
	 * 
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/formTransferenciaSolicitacao.jsp
	 */
	public String transferirSolicitacao () throws ArqException{
		checkRole (SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);
		
		BibliotecaDao dao = null;
		
		if (bibliotecaDestino.getId() <= 0) {
			addMensagemErro("Selecione a biblioteca para onde a solicitação vai ser transferida.");
			return null;
		} else {
			try {
				dao = getDAO(BibliotecaDao.class);
				populateObj();
				
				getGenericDAO().refresh( obj.getPessoa() );
				
				String descricaoBibliotecaOrigem = obj.getBiblioteca().getDescricao();
				
				bibliotecaDestino = dao.refresh(bibliotecaDestino);
				
				String descricaoBibliotecaDestino = bibliotecaDestino.getDescricao();
				
				obj.setBiblioteca(bibliotecaDestino);
	
				MovimentoCadastro mov = new MovimentoCadastro(obj);
				mov.setCodMovimento(ArqListaComando.ALTERAR);
				
				execute(mov);
				
				enviarEmailsTransferenciaUsuario(descricaoBibliotecaOrigem, descricaoBibliotecaDestino);
				enviarEmailsTransferenciaBibliotecaDestino(descricaoBibliotecaOrigem, bibliotecaDestino.getDescricao(), bibliotecaDestino.getEmail());
				
				addMensagemInformation("Solicitação transferida com Sucesso. " +
						"Um email foi enviado ao solicitante e a biblioteca destino informando a transferência.");
				
				return buscarSolicitacoesSolicitadas();
			} catch (NegocioException ne){
				ne.printStackTrace();
				addMensagens(ne.getListaMensagens());
				return null;
			} finally {
				if (dao != null) dao.close();
			}
		
		}
	}

	/**
	 * Envia os e-mails referentes à operação de transferência de solicitação.
	 * 
	 * @param descricaoBibliotecaOrigem
	 * @param descricaoBibliotecaDestino
	 * @throws DAOException
	 */
	private void enviarEmailsTransferenciaUsuario(String descricaoBibliotecaOrigem, String descricaoBibliotecaDestino) throws DAOException {
		EnvioEmailBiblioteca envioEmail = new EnvioEmailBiblioteca();
		
		String assuntoEmail = null;
		String conteudo = null;
		String destinatario = null;
		
		assuntoEmail = "Solicitação transferida";
		
		conteudo = "<p>A sua solicitação de " + obj.getTipoServico().toLowerCase() + " nº "+obj.getNumeroSolicitacao()+" sua foi transferida da " +
			"   <strong>" + descricaoBibliotecaOrigem + "</strong> para a <strong>" +
			descricaoBibliotecaDestino + "</strong>." +
			"<br><br>Atenciosamente,<br>Setor de Informação e Referência</p>";
		
		destinatario = obj.getPessoa().getEmail();
		
		envioEmail.enviaEmailSimples(obj.getPessoa().getNome(), 
				destinatario, 
				assuntoEmail, 
				assuntoEmail, 
				EnvioEmailBiblioteca.AVISO_INFORMACAO_REFERENCIA, 
				conteudo);
	}
	
	
	
	/**
	 * Envia os e-mails referentes à operação de transferência de solicitação.
	 * 
	 * @param descricaoBibliotecaOrigem
	 * @param descricaoBibliotecaDestino
	 * @throws DAOException
	 */
	private void enviarEmailsTransferenciaBibliotecaDestino(String descricaoBibliotecaOrigem, String descricaoBibliotecaDestino, String emailBibliotecaDestino) throws DAOException {
		
		String assunto = " Aviso de Solicitação de "+WordUtils.capitalize(obj.getTipoServico().toLowerCase())+" tranferida. ";
		String titulo = " Nova Solicitação de "+WordUtils.capitalize(obj.getTipoServico().toLowerCase());
		
		String mensagemNivel1Email =  " Uma solicitação de " + obj.getTipoServico().toLowerCase()+ " foi transferida da "
									+descricaoBibliotecaOrigem+" para essa biblioteca. ";
		
		String mensagemNivel3Email =  " Essa solicitação está pedente de atendimento.";
		
		new EnvioEmailBiblioteca().enviaEmail( descricaoBibliotecaDestino, emailBibliotecaDestino, assunto, titulo
				, EnvioEmailBiblioteca.AVISO_INFORMACAO_REFERENCIA, null, mensagemNivel1Email, null, mensagemNivel3Email, null
				, null, null,  null, null);
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	////// Ultima parte onde o usuário pode ver o resultado do atendimento da solicitação ///////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
		
	/**
	 *   Método que obtém a biblioteca para o biblioteca atender as requisições.<br/><br/>
	 *   Se for administrador geral, retorna todas, senão retorna as biblioteca onde o bibliotecário tem permissão
	 *   de catalogação e informação e referência.<br/><br/>
	 * 
	 * 
	 * @throws ArqException
	 *
	 */
	private void obtemBibliotecasAtendimento() throws ArqException{
		
		bibliotecas = new ArrayList<Biblioteca>();
	
		// evita bibliotecas repetidas
		Set<Biblioteca> b = new TreeSet<Biblioteca>(
				new Comparator<Biblioteca>() {
					@Override
					public int compare(Biblioteca a, Biblioteca e) { return a.getId() - e.getId(); }
				});
		
		if(! isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
			
			/*
			 * Setor de processos técnicos podem atender as solicitações de catalogação na fonte,
			 * por isso esse papel tá aqui. !!!
			*/
			if(isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO)){
			
				List<Integer> idUnidades = BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(
							getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);
				
				b.addAll( getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivasPorUnidade(idUnidades) );
			}
			
			if(isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO)){
			
				List<Integer> idUnidades = BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(
					getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);
		
				b.addAll( getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivasPorUnidade(idUnidades) );
			}
			
			if(isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF)){
				
				List<Integer> idUnidades = BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(
					getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF);
		
				b.addAll( getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivasPorUnidade(idUnidades) );
			}
			
		
		} else {
			b.addAll( getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivas() );
		}
		
		bibliotecas.addAll(b);
	}
	
	/**
	 * Retorna uma coleção de SelectItem com as situações que a solicitação pode ter.
	 * Auxilia a listagem na visão do operador.
	 */
	/*public Collection<SelectItem> getSituacoesSolicitacaoCombo(){
		SelectItem solicitado = new SelectItem( SolicitacaoNormalizacaoCatalogacao.SOLICITADO, "Solicitado" );
		SelectItem validado = new SelectItem( SolicitacaoNormalizacaoCatalogacao.VALIDADO, "Validado" );
		SelectItem atendido = new SelectItem( SolicitacaoNormalizacaoCatalogacao.ATENDIDO, "Atendido" );
		SelectItem cancelado = new SelectItem( SolicitacaoNormalizacaoCatalogacao.CANCELADO, "Cancelado" );
		
		Collection<SelectItem> situacoes = new ArrayList<SelectItem>();
		situacoes.add(solicitado);
		situacoes.add(validado);
		situacoes.add(atendido);
		situacoes.add(cancelado);
		return situacoes;
	}*/
	
	//////////////////////////   telas de navegação  //////////////////////////////////////
	
	/**
	 * Redireciona o fluxo de navegação para a página de exibição das solicitações do usuário.
	 * 
	 * @return
	 */
	protected String telaMinhasSolicitacoes(){
		return forward( PAGINA_VISUALIZA_MINHAS_SOLICITACOES);
	}
	
	/**
	 * Redireciona o fluxo de navegação para a página de exibição das solicitações em aberto para o bibliotecário.
	 * 
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/formTransferenciaSolicitacao.jsp
	 * 
	 * @return
	 */
	public String telaListaSolicitacoes(){
		return forward( PAGINA_VISUALIZA_SOLICITACOES);
	}
	
	/**
	 * Exibe a tela com o comprovante da solicitação.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:</p>
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/minhasSolicitacoes.jsp</li>
	 * </ul>
	 */
	public String telaComprovante() throws DAOException {
		obj = buscarNaListaByID(getParameterInt("idSolicitacao", 0));
		
		if(obj == null){
			addMensagemErro("Solicitação já processada!");
			return null;
		}
		
		ISolicitacaoServicoDocumentoMBean<SolicitacaoServicoDocumento> solicitacaoServicoMbean = getSolicitacaoServicoMBean(obj);
		
		return solicitacaoServicoMbean.telaComprovante();
	}
	

	/**
	 * Retorna a quantidade de bibliotecas que estão utilizando o servido de normalização no sistema.
	 * 
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:</p>
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/minhasSolicitacoes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public int getQuantidadeBibliotecasRealizandoNormalizacao() throws DAOException{
		Integer quantidadeBibliotecasRealizandoNormalizacao = (Integer) getCurrentSession().getAttribute("_quantidadeBibliotecasRealizandoNormalizacao");
		
		if(quantidadeBibliotecasRealizandoNormalizacao == null){
			
			SolicitacaoServicoDocumentoDAO dao = null;
			
			try{
				dao = getDAO(SolicitacaoServicoDocumentoDAO.class);
				quantidadeBibliotecasRealizandoNormalizacao = dao.contaBibliotecasComServicoAtivo(TipoServicoInformacaoReferencia.NORMALIZACAO);
				getCurrentSession().setAttribute("_quantidadeBibliotecasRealizandoNormalizacao", quantidadeBibliotecasRealizandoNormalizacao);
			}finally{
				if(dao != null) dao.close();
			}
		}
		
		return quantidadeBibliotecasRealizandoNormalizacao;
	}
	
	
	
	/**
	 * Retorna a quantidade de bibliotecas que estão utilizando o servido de catalogação na fonte no sistema.
	 * 
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:</p>
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/minhasSolicitacoes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public int getQuantidadeBibliotecasRealizandoCatalogacaoNaFonte() throws DAOException{
		Integer quantidadeBibliotecasRealizandoCatalogacaoNaFonte = (Integer) getCurrentSession().getAttribute("_quantidadeBibliotecasRealizandoCatalogacaoNaFonte");
		
		if(quantidadeBibliotecasRealizandoCatalogacaoNaFonte == null){
			
			SolicitacaoServicoDocumentoDAO dao = null;
			
			try{
				dao = getDAO(SolicitacaoServicoDocumentoDAO.class);
				quantidadeBibliotecasRealizandoCatalogacaoNaFonte = dao.contaBibliotecasComServicoAtivo(TipoServicoInformacaoReferencia.CATALOGACAO_NA_FONTE);
				getCurrentSession().setAttribute("_quantidadeBibliotecasRealizandoCatalogacaoNaFonte", quantidadeBibliotecasRealizandoCatalogacaoNaFonte);
			}finally{
				if(dao != null) dao.close();
			}
		}
		
		return quantidadeBibliotecasRealizandoCatalogacaoNaFonte;
	}
	
	
	
	
		
	///////////////////////////////////////////////////////////////////////////////////////////
	
	//////    sets e gets   ////////
	
	public int getQuantidadeDiasDiscarteMaterial(){
		 return ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.TEXTO_TITULO_FICHA_CATALOGRAFICA);
	}
	
	public Collection<Biblioteca> getBibliotecas() {
		return bibliotecas;
	}

	public void setBibliotecas(Collection<Biblioteca> bibliotecas) {
		this.bibliotecas = bibliotecas;
	}

	public Collection<SelectItem> getBibliotecasCombo(){
		return toSelectItems(bibliotecas, "id", "descricao");
	}

	public Collection<SelectItem> getBibliotecasServicoCombo(){
		return toSelectItems(bibliotecasServico, "id", "descricao");
	}
	
	public SortedSet<SolicitacaoServicoDocumento> getSolicitacoes() {
		return solicitacoes;
	}

	public Integer getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(Integer tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	/*public Integer getSituacao() {
		return situacao;
	}

	public void setSituacao(Integer situacao) {
		this.situacao = situacao;
	}*/

	public Integer getTipoServico() {
		return tipoServico;
	}

	public void setTipoServico(Integer tipoServico) {
		this.tipoServico = tipoServico;
	}

	public int getOperacao() {
		return operacao;
	}

	public void setOperacao(int operacao) {
		this.operacao = operacao;
	}

	/*public boolean isValidar(){
		return operacao == VALIDAR;
	}*/
	
	public boolean isCancelar(){
		return operacao == CANCELAR;
	}
	
	public boolean isAtender(){
		return operacao == ATENDER;
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public String getMotivoCancelamento() {
		return motivoCancelamento;
	}

	public void setMotivoCancelamento(String motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}

	public Collection<Biblioteca> getBibliotecasServico() {
		return bibliotecasServico;
	}

	public void setBibliotecasServico(Collection<Biblioteca> bibliotecasServico) {
		this.bibliotecasServico = bibliotecasServico;
	}

	public Biblioteca getBibliotecaDestino() {
		return bibliotecaDestino;
	}

	public void setBibliotecaDestino(Biblioteca bibliotecaDestino) {
		this.bibliotecaDestino = bibliotecaDestino;
	}

	public boolean isBuscarCanceladas() {
		return buscarCanceladas;
	}

	public void setBuscarCanceladas(boolean buscarCanceladas) {
		this.buscarCanceladas = buscarCanceladas;
	}

	public boolean isBuscarAtendidas() {
		return buscarAtendidas;
	}

	public void setBuscarAtendidas(boolean buscarAtendidas) {
		this.buscarAtendidas = buscarAtendidas;
	}

	public boolean isBuscarTipoDocumento() {
		return buscarTipoDocumento;
	}

	public void setBuscarTipoDocumento(boolean buscarTipoDocumento) {
		this.buscarTipoDocumento = buscarTipoDocumento;
	}
	
	public int getTodasBibliotecas() {
		return TODAS_BIBLIOTECAS;
	}
	
	/**
	 * Retorna a lista de tipos de serviços suportados pelo managed bean para ser utilizada no filtro de busca.
	 * 
	 * @return
	 */
	public Collection<SelectItem> getTiposServico() {
		ArrayList<TipoServicoInformacaoReferencia> tiposServico = new ArrayList<TipoServicoInformacaoReferencia>();
		
		tiposServico.add(TipoServicoInformacaoReferencia.CATALOGACAO_NA_FONTE);
		//tiposServico.add(Servico.ORIENTACAO_NORMALIZACAO);
		tiposServico.add(TipoServicoInformacaoReferencia.NORMALIZACAO);
		//tiposServico.add(Servico.LEVANTAMENTO_BIBLIOGRAFICO);
		//tiposServico.add(Servico.LEVANTAMENTO_INFRA_ESTRUTURA);
		
		return toSelectItems(tiposServico, "valor", "descricao");
	}

	public boolean isBuscarTipoServico() {
		return buscarTipoServico;
	}

	public void setBuscarTipoServico(boolean buscarTipoServico) {
		this.buscarTipoServico = buscarTipoServico;
	}

	public String getEmailNotificacao() {
		return emailNotificacao;
	}

	public void setEmailNotificacao(String emailNotificacao) {
		this.emailNotificacao = emailNotificacao;
	}

	public String getTextoNotificacao() {
		return textoNotificacao;
	}

	public void setTextoNotificacao(String textoNotificacao) {
		this.textoNotificacao = textoNotificacao;
	}

	public boolean isBuscarRemovidasPeloUsuario() {
		return buscarRemovidasPeloUsuario;
	}

	public void setBuscarRemovidasPeloUsuario(boolean buscarRemovidasPeloUsuario) {
		this.buscarRemovidasPeloUsuario = buscarRemovidasPeloUsuario;
	}

	public Integer getNumeroSolicitacao() {
		return numeroSolicitacao;
	}

	public void setNumeroSolicitacao(Integer numeroSolicitacao) {
		this.numeroSolicitacao = numeroSolicitacao;
	}

	public String getNomeUsuarioSolicitante() {
		return nomeUsuarioSolicitante;
	}

	public void setNomeUsuarioSolicitante(String nomeUsuarioSolicitante) {
		this.nomeUsuarioSolicitante = nomeUsuarioSolicitante;
	}

	public boolean isBuscarNumeroSolicitacao() {
		return buscarNumeroSolicitacao;
	}

	public void setBuscarNumeroSolicitacao(boolean buscarNumeroSolicitacao) {
		this.buscarNumeroSolicitacao = buscarNumeroSolicitacao;
	}

	public boolean isBuscarNomeUsuarioSolicitante() {
		return buscarNomeUsuarioSolicitante;
	}

	public void setBuscarNomeUsuarioSolicitante(boolean buscarNomeUsuarioSolicitante) {
		this.buscarNomeUsuarioSolicitante = buscarNomeUsuarioSolicitante;
	}

	public boolean isBuscarData() {
		return buscarData;
	}

	public void setBuscarData(boolean buscarData) {
		this.buscarData = buscarData;
	}
	
}