/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 12/05/2011
 *
 */

package br.ufrn.sigaa.biblioteca.informacao_referencia.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.EnvioEmailBiblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dao.ServicoInformacaoReferenciaBibliotecaDAO;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dao.SolicitacaoOrientacaoDAO;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dao.SolicitacaoServicoDocumentoDAO;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.SolicitacaoOrientacao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.SolicitacaoOrientacao.TurnoDisponibilidadeUsuario;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.SolicitacaoServico.TipoSituacao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoServicoInformacaoReferencia;
import br.ufrn.sigaa.biblioteca.informacao_referencia.negocio.MovimentoSolicitacaoOrientacao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.negocio.ProcessadorSolicitacaoOrientacao;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.UsuarioBibliotecaUtil;

/**
 * <p>MBean responsável pelos atendimento e solicitação de normalização de obra realizada por discentes ou servidores à biblioteca.</p>
 * 
 * @author Felipe Rivas
 */
@Component("solicitacaoOrientacaoMBean")
@Scope("request")
public class SolicitacaoOrientacaoMBean extends SigaaAbstractController<SolicitacaoOrientacao> {

	/** Página onde o usuário faz uma nova solicitação de normalização */
	public static final String PAGINA_NOVA_SOLICITACAO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/novaSolicitacaoOrientacao.jsp";
	
	/** Página onde o usuário pode visualizar os dados da sua solicitação */
	public static final String PAGINA_VISUALIZA_DADOS_SOLICITACAO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoOrientacao.jsp";
	
	/** O comprovante de solicitação de normalização. */
	public static final String PAGINA_VISUALIZA_COMPROVANTE_SOLICITACAO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/comprovanteSolicitacaoOrientacao.jsp";
	
	/** Página onde o bibliotecário pode visualizar os dados da sua solicitação */
	public static final String PAGINA_VISUALIZA_DADOS_SOLICITACAO_ATENDIMENTO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/visualizarDadosSolicitacaoOrientacaoAtendimento.jsp";
	
	/** Usuário visualiza as solicitações feitas por ele */
	public static final String PAGINA_VISUALIZA_MINHAS_SOLICITACOES =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/minhasSolicitacoesOrientacao.jsf";

	/** Bibliotecário visualiza as solicitações para atender*/
	public static final String PAGINA_VISUALIZA_SOLICITACOES =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/listarSolicitacoesOrientacao.jsf";
	
	/** O bibliotecário transfere uma solicitação da sua biblioteca para a central */
	public static final String PAGINA_TRANSFERENCIA_SOLICITACAO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/formTransferenciaSolicitacaoOrientacao.jsp";
	
	/** O bibliotecário transfere uma solicitação da sua biblioteca para a central */
	public static final String PAGINA_FINALIZAR_SOLICITACAO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/finalizarSolicitacaoOrientacao.jsp";
	
	/** Página na qual o bibliotecário vai notificar alguém sobre a solicitação, geralmente o setor de processos técnicos para realizar o atendimento. */
	public static final String PAGINA_NOTIFICAR_SOLICITACAO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/formNotificarSolicitacaoOrientacao.jsp";
	
	
	
	/** usado no botão no qual o usuário faz a solicitação ou a altera */
	private static final String TEXTO_ALTERAR = "Alterar";
	/** Valor padrão para indicar a seleção de todas as bibliotecas no filtro de busca */
	private static final int TODAS_BIBLIOTECAS = -1;
	
	/** Armazena a operação que está sendo executada no momento. */
	private int operacao;

	/** Operação de atender. */
	public static final int ATENDER = 1;
	/** Operação de confirmar. */
	public static final int CONFIRMAR = 2;
	/** Operação de cancelar. */
	public static final int CANCELAR = 3;
	
	/** Se o bibliotecário cancelar uma solicitação tem que informar ao usuário que a solicitou */
	private String motivoCancelamento;
	
	/** Usado na transferência de solicitações */
	private Collection<Biblioteca> bibliotecasServico = new ArrayList<Biblioteca>();
	
	/** Usado na transferência de solicitações */
	private Biblioteca bibliotecaDestino = new Biblioteca(-1);
	
	/**
	 * Guarda as solicitações feitas pelo usuário quando o caso de uso é de realizar uma
	 * solicitação de serviço.
	 * Guarda as solicitações que o bibliotecário buscou na atendimento das solicitações
	 */
	private SortedSet<SolicitacaoOrientacao> solicitacoes;
	
	/**
	 * Biblioteca para as quais o usuário pode realizar as solicitações.
	 */
	private Collection<Biblioteca> bibliotecas = new ArrayList<Biblioteca>();
	/** Biblioteca selecionada no filtro de busca */
	private Biblioteca biblioteca = new Biblioteca(-1);
	
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
	
	/** Limite inicial da data de cadastro selecionado no filtro de busca */
	private Date dataInicial;
	/** Limite final da data de cadastro selecionado no filtro de busca */
	private Date dataFinal;
	/** Indica se as solicitações atendidas devem ser incluídas no resultado da busca */
	private boolean buscarAtendidas; 
	/** Indica se as solicitações canceladas devem ser incluídas no resultado da busca */
	private boolean buscarCanceladas;
	/** Indica se as solicitações confirmadas devem ser incluídas no resultado da busca */
	private boolean buscarConfirmadas;
	/** Retorna as solicitações removidas pelo usuário, antes mesmo do atendimento ser realizado. */
	private boolean buscarRemovidasPeloUsuario;
	
	/** Data de atendimento informada pelo bibliotecário no atendimento da solicitação */
	private Date dataAtendimento;
	/** Horário início de atendimento informado pelo bibliotecário no atendimento da solicitação */
	private String horarioInicioAtendimento;
	/** Horário término de atendimento informado pelo bibliotecário no atendimento da solicitação */
	private String horarioFimAtendimento;
	/** Indica se o usuário tem disponibilidade de atendimento no turno matutino */
	private boolean turnoManha;
	/** Indica se o usuário tem disponibilidade de atendimento no turno vespertino */
	private boolean turnoTarde;
	/** Indica se o usuário tem disponibilidade de atendimento no turno noturno */
	private boolean turnoNoite;
	
	
	/** Email para onde será enviado a notificação sobre a solicitação de catalogação. */
	private String emailNotificacao;
	
	/** O Texto que será enviado no email de notificação sobre a solicitação de catalogação.  */
	private String textoNotificacao;
	
	public SolicitacaoOrientacaoMBean() throws DAOException {
		solicitacoes = new TreeSet<SolicitacaoOrientacao>(
				new Comparator<SolicitacaoOrientacao>() {
					@Override
					public int compare(SolicitacaoOrientacao o1, SolicitacaoOrientacao o2) {
						return - o1.getDataCadastro().compareTo( o2.getDataCadastro() );
					}
				}
			);
		
		initObj();
	}

	public TipoServicoInformacaoReferencia getTipoServico() {
		return TipoServicoInformacaoReferencia.ORIENTACAO_NORMALIZACAO;
	}

	protected Comando getMovimentoCadastrar() {
		return SigaaListaComando.CADASTRAR_SOLICITACAO_ORIENTACAO;
	}

	protected Comando getMovimentoAlterar() {
		return SigaaListaComando.ALTERAR_SOLICITACAO_ORIENTACAO;
	}

	protected Comando getMovimentoAtender() {
		return SigaaListaComando.ATENDER_SOLICITACAO_ORIENTACAO;
	}

	protected Comando getMovimentoConfirmar() {
		return SigaaListaComando.CONFIRMAR_SOLICITACAO_ORIENTACAO;
	}

	protected Comando getMovimentoNaoConfirmar() {
		return SigaaListaComando.NAO_CONFIRMAR_HORARIO_AGENDADO_ORIENTACAO;
	}
	
	protected Comando getMovimentoCancelar() {
		return SigaaListaComando.CANCELAR_ATENDIMENTO_SOLICITACAO_ORIENTACAO;
	}
	
	/**
	 * Retorna a instância de um movimento específico através de polimorfismo.
	 * 
	 * @return
	 */
	protected MovimentoSolicitacaoOrientacao instanciarMovimento() {
		return new MovimentoSolicitacaoOrientacao();
	}

	/**
	 * Retorna a propriedade da classe ServicosBiblioteca que representa o tipo de serviço solicitado
	 * 
	 * @return
	 */
	public String getPropriedadeServico() {
		return "realizaOrientacaoNormalizacao";
	}

	/**
	 * Redireciona o fluxo de navegação para a página de criação de solicitação.
	 * 
	 * @return
	 */
	protected String telaNovaSolicitacaoServico() {
		return forward(PAGINA_NOVA_SOLICITACAO);
	}

	/**
	 * Redireciona o fluxo de navegação para a página de visualização dos dados de uma solicitação do usuário.
	 * 
	 * @return
	 */
	protected String telaVisualizarSolicitacao() {
		return forward(PAGINA_VISUALIZA_DADOS_SOLICITACAO);
	}

	/**
	 * Redireciona o fluxo de navegação para a página de visualização dos dados de uma solicitação para o bibliotecário.
	 * 
	 * @return
	 */
	protected String telaVisualizarDadosSolicitacaoAtendimento() {
		return forward(PAGINA_VISUALIZA_DADOS_SOLICITACAO_ATENDIMENTO);
	}

	/**
	 * Redireciona o fluxo de navegação para a página de confirmação ou cancelamento de um agendamento por parte do usuário.
	 * 
	 * @return
	 */
	protected String telaFinalizarSolicitacao() {
		return forward(PAGINA_FINALIZAR_SOLICITACAO);
	}

	/**
	 * Redireciona o fluxo de navegação para a página de exibição do comprovante de solicitação.
	 * 
	 * @return
	 */
	protected String telaVisualizaComprovante() {
		return forward(PAGINA_VISUALIZA_COMPROVANTE_SOLICITACAO);
	}

	public Date getDataAtendimento() {
		return dataAtendimento;
	}

	public void setDataAtendimento(Date dataAtendimento) {
		this.dataAtendimento = dataAtendimento;
	}

	public String getHorarioInicioAtendimento() {
		return horarioInicioAtendimento;
	}

	public void setHorarioInicioAtendimento(String horarioInicioAtendimento) {
		this.horarioInicioAtendimento = horarioInicioAtendimento;
	}

	public String getHorarioFimAtendimento() {
		return horarioFimAtendimento;
	}

	public void setHorarioFimAtendimento(String horarioFimAtendimento) {
		this.horarioFimAtendimento = horarioFimAtendimento;
	}
	
	/**
	 *    Inicializa a solicitação, se tiver algum idSolicitacao como parâmetro da
	 * solicitação, inicializa o <tt>obj</tt> com os dados da solicitação salva no banco(
	 * alteração e remoção), senão inicializa uma nova solicitação
	 * @throws DAOException 
	 *  
	 * 
	 */
	protected void initObj() throws DAOException {
		obj = new SolicitacaoOrientacao();
		
		int id = getParameterInt("idSolicitacao", 0);
		
		if (id != 0){
			obj.setId(id);
			
			populateObj();
		} 
		else {
			obj.setBiblioteca(new Biblioteca(-1));
		}
	}

	/**
	 * Inicia o caso de uso onde o usuário visualiza as suas solicitações feitas.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/portais/discente/menu_discente.jsp</li>
	 * 	<li>/portais/docente/menu_docente.jsp</li>
	 * 	<li>/biblioteca/menus/modulo_biblioteca_servidor.jsp</li>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoOrientacao.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException 
	 */
	public String verMinhasSolicitacoes() throws DAOException {
		
		solicitacoes.clear();
		
		SolicitacaoOrientacaoDAO dao = null;
		
		
		
		
		try {
			
		    // Verifica se o usuário possuia alguma conta ativida na biblioteca para poder solicitar os serviços da seção de Info. e Ref. 
			// Caso não tenha será lançada uma NegocioException
			UsuarioBibliotecaUtil.retornaUsuarioBibliotecaUnicoDaPessoa(getUsuarioLogado().getPessoa().getId(), null);
			
			dao = getDAO(SolicitacaoOrientacaoDAO.class);
			
			//// ENCONTRA AS SOLICITAÇÕES FEITAS PELA PESSOA LOGADA ////////
			
			// Usuário acessou pelo portal discente
			if (SigaaSubsistemas.PORTAL_DISCENTE.equals(getSubSistema())) {
				if (getDiscenteUsuario() != null ){
					solicitacoes.addAll( dao.findSolicitacoesAtivas(getDiscenteUsuario().getPessoa(), null, null, null, null, 
						null, null, null, true,(TipoSituacao[]) null) );
					return telaMinhasSolicitacoes();
				}
			// Usuário acessou pelo portal docente (professor)
			} else if (SigaaSubsistemas.PORTAL_DOCENTE.equals(getSubSistema())) {
				if (getUsuarioLogado().getServidor() != null ){
					solicitacoes.addAll( dao.findSolicitacoesAtivas(getServidorUsuario().getPessoa(), null, null,  null, null,
						null, null, null, true,(TipoSituacao[]) null) );
					return telaMinhasSolicitacoes();
				}
			// Usuário acessou pelo módulo do servidor na biblioteca (servidor ou professor)
			} else if (getUsuarioLogado().getServidor() != null ) {
				solicitacoes.addAll( dao.findSolicitacoesAtivas(getServidorUsuario().getPessoa(), null, null,  null, null,
						null, null, null, true,(TipoSituacao[]) null) );
				return telaMinhasSolicitacoes();
			}
			
			addMensagemErro("Usuário não pode realizar solicitações de serviços da biblioteca, usuário não é discente nem servidor da instituição.");
			
			return null;			
		}catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		} finally {
			if (dao != null) dao.close();
		}
	}

	/**
	 * Redireciona o fluxo de navegação para a página de exibição das solicitações do usuário.
	 * 
	 * @return
	 */
	protected String telaMinhasSolicitacoes(){
		return forward(PAGINA_VISUALIZA_MINHAS_SOLICITACOES);
	}
	
	public SortedSet<SolicitacaoOrientacao> getSolicitacoes() {
		return solicitacoes;
	}

	/**
	 * Verifica se a lista de bibliotecas passa contem o objeto biblioteca pelo id.
	 * Não dava para usar o <code>contains</code> de <code>Collections</code> porque o
	 * Hibernate traz objetos <code>proxy</code> e o <code>equals</code> e
	 * <code>hashcode</code> não funcionam
	 */
	private boolean contains(Collection<Biblioteca> bib, Biblioteca b) {
		for (Biblioteca bibl : bib) {
			if(bibl.getId() == b.getId())
				return true;
		}
		
		return false;
	}
	
	/**
	 *   Método que obtém a biblioteca que o usuário pode solicitar o serviço.
	 *   Se for aluno a setorial dele, se for servidor ou aluno sem setorial a biblioteca Central.
	 * @throws ArqException
	 *
	 */
	private void obtemBibliotecaSolicitante() throws ArqException {
		
		bibliotecas = new ArrayList<Biblioteca>();
		
		// Acessou pelo portal discente, então vai fazer a solicitação como discente
		if (getSubSistema().equals( SigaaSubsistemas.PORTAL_DISCENTE)){
			if (!isEmpty(obj.getDiscente().getUnidade())){
				bibliotecas = BibliotecaUtil.getBibliotecasDoDiscenteByServico(obj.getDiscente(), getTipoServico());
			}
		} else { // Solicitação como servidor
			bibliotecas.add( BibliotecaUtil.getBibliotecaCentral() );
		}
		
		// Se o usuário fez uma solicitação para a setorial do centro dele como aluno e depois
		// se loga como servidor, só vai poder fazer novas solicitações para central, mas para as
		// solicitações já feitas tem que aparecer a biblioteca onde ela estava
		
		if(obj.getId() > 0 && obj.getBiblioteca() != null && ! contains(bibliotecas, obj.getBiblioteca())){
			bibliotecas.add(obj.getBiblioteca());
		}
		
		// Não deixa a biblioteca central se houver somente ela e ela não estiver oferecendo
		// os serviço desejado
		if ( bibliotecas.size() == 1 ) {
			Iterator<Biblioteca> it = bibliotecas.iterator();
			if ( ! BibliotecaUtil.bibliotecaRealizaServico(getPropriedadeServico(), it.next().getId()))
				it.remove();
		}
		
		if (bibliotecas.size() == 0) {
			addMensagemErro("No momento não há bibliotecas disponibilizando este serviço.");
		}
	}
	
	/**
	 * Inicia o caso de uso onde o usuário faz uma solicitação de serviço.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/minhasSolicitacoesOrientacao.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException
	 */
	public String realizarNovaSolicitacao() throws ArqException {
		
		setConfirmButton("Cadastrar Solicitação");
		
		initObj();
		
		BibliotecaDao dao = null;

		turnoManha = false;
		turnoTarde = false;
		turnoNoite = false;
		
		try {
			dao = getDAO(BibliotecaDao.class);
		
			if( getSubSistema().equals( SigaaSubsistemas.PORTAL_DISCENTE ) ){ // se acessou pelo portal discente é aluno
				obj.setDiscente( getDiscenteUsuario().getDiscente() );
				obj.setPessoa(getDiscenteUsuario().getPessoa());
				obj.setServidor( null );
			} else {                                     // qualquer outra forma de acesso é servidor
				obj.setServidor( getServidorUsuario() );
				obj.setPessoa(getServidorUsuario().getPessoa());
				obj.setDiscente( null );
			}
			
			obtemBibliotecaSolicitante();
	
			prepareMovimento(getMovimentoCadastrar());
			
			return telaNovaSolicitacaoServico();
		} finally {
			if (dao != null)  dao.close();
		}
	}

	/**
	 * Exibe as informações completas da solicitação para o usuário.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/minhasSolicitacoesOrientacao.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException 
	 */
	public String visualizarDadosSolicitacao() throws DAOException {
		operacao = -1;
		initObj();          // inicializa o objeto salvo no banco com o id passado a requisição
		
		if (obj == null || obj.getId() == 0 || !obj.isAtiva()) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			
			return verMinhasSolicitacoes();
		}
		
		return telaVisualizarSolicitacao();
	}

	/**
	 * Exibe a tela com o comprovante da solicitação.
	 * 
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoOrientacao.jsp
	 */
	public String telaComprovante() throws DAOException{
		GenericDAO dao = null;
		
		try {
			dao = getGenericDAO();
			obj = dao.refresh(obj);
		
			return telaVisualizaComprovante();
		} finally {
			if (dao != null) dao.close();
		}
	}

	public Collection<SelectItem> getBibliotecasCombo(){
		return toSelectItems(bibliotecas, "id", "descricao");
	}

	/**
	 * Cria a solicitação de serviço do usuário.
	 * 
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/novaSolicitacaoOrientacao.jsp
	 */
	public String cadastrarSolicitacao() throws ArqException{
		
		GenericDAO dao = null;
		try {
			
			dao = getGenericDAO();
			
			obj.setBiblioteca(dao.refresh(new Biblioteca(obj.getBiblioteca().getId())));
			
			obj.setTurnoDisponivel(getTurnoDisponivel());
			
			MovimentoCadastro mov = instanciarMovimento();
			
			mov.setObjMovimentado(obj);
	
			if (!getConfirmButton().equals(TEXTO_ALTERAR)){
				mov.setCodMovimento(getMovimentoCadastrar());
				execute(mov);
				addMensagemInformation("Solicitação de orientação cadastrada com sucesso, aguarde " +
						"a definição do agendamento por parte de um bibliotecario da " +
						obj.getBiblioteca().getDescricao() + ".");
			} else {
 				mov.setCodMovimento(getMovimentoAlterar());
				execute(mov);
				addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Solicitação");
			}
			
			return verMinhasSolicitacoes();
		
		} catch (NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		} finally {
			if (dao != null) dao.close();
		}
		
	}

	/**
	 * Altera os dados da solicitação feita.
	 * O usuário pode alterar solicitações que estão com status "Solicitado".
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/minhasSolicitacoesOrientacao.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException 
	 */
	public String alterarSolicitacao() throws ArqException {
		
		setConfirmButton(TEXTO_ALTERAR);

		BibliotecaDao dao = null;
		initObj(); // inicializa o objeto salvo no banco com o id passado a requisição
		
		if ( obj == null || obj.getId() == 0 || ! obj.isAtiva() ){
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return verMinhasSolicitacoes();
		}

		turnoManha = obj.isDisponivelManha();
		turnoTarde = obj.isDisponivelTarde();
		turnoNoite = obj.isDisponivelNoite();
		
		try {
			dao = getDAO(BibliotecaDao.class);
			
			prepareMovimento(getMovimentoAlterar());
			
			obj.setBiblioteca(dao.refresh(obj.getBiblioteca()));

			setConfirmButton("Alterar");

			obtemBibliotecaSolicitante();
			
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		} finally {
			if (dao != null) dao.close();
		}

		prepareMovimento(getMovimentoAlterar());
		
		// tela que cria a solicitação sendo que agora com o botão alterar habilitado
		return telaNovaSolicitacaoServico();
	}

	/**
	 * Remove a solicitação selecionada.
	 * O usuário pode remover solicitações que estão com status "Solicitado".
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/minhasSolicitacoesOrientacao.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	public String removerSolicitacao() throws ArqException {
		
		initObj();   // inicializa o objeto salvo no banco com o id passado a requisição
		
		GenericDAO dao = null;
		
		if (obj == null || obj.getId() == 0 || ! obj.isAtiva())
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
		else{
			try {
				
				MovimentoCadastro movimentoRemover = new MovimentoCadastro();
				movimentoRemover.setCodMovimento(ArqListaComando.ALTERAR);
				obj.setAtiva(false);
				movimentoRemover.setObjMovimentado(obj);
				
				execute(movimentoRemover);
				
				addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Solicitação");
				
			} catch (NegocioException ne) {
				addMensagens(ne.getListaMensagens());
				return null;
			} finally {
				if (dao != null) dao.close();
			}
		
		}
		
		return verMinhasSolicitacoes();
	}

	/**
	 * Redireciona o fluxo de navegação para a página de exibição das solicitações em aberto para o bibliotecário.
	 * 
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/formTransferenciaSolicitacaoOrientacao.jsp
	 * 
	 * @return
	 */
	public String telaListaSolicitacoes(){
		return forward( PAGINA_VISUALIZA_SOLICITACOES);
	}

	/**
	 * Exibe a tela com as solicitações a serem atendidas para o bibliotecário.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/portais/discente/menu_discente.jsp</li>
	 * 	<li>/portais/docente/menu_docente.jsp</li>
	 * 	<li>/biblioteca/menus/modulo_biblioteca_servidor.jsp</li>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoOrientacaoAtendimento.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException 
	 */
	public String verSolicitacoes() throws ArqException {
		//initObj();

		obtemBibliotecasAtendimento();
		
		if(bibliotecas == null || bibliotecas.size() == 0 ){
			addMensagemWarning("O senhor(a) não possui permissão em nenhuma biblioteca para gerenciar as solicitações de orientação de normalização.");
		}
		
		return telaListaSolicitacoes();
	}
	
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
	 * Busca todas as solicitações de serviço não canceladas
	 * cadastradas no sistema de acordo com os filtros selecionados na página.
	 * 
	 * <p>Usado quando o bibliotecário vai pesquisar as solicitações para atendê-las.</p>
	 *     
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/listarSolicitacoesOrientacao.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public String buscarSolicitacoesSolicitadas() throws DAOException {
		
		SolicitacaoOrientacaoDAO dao = getDAO(SolicitacaoOrientacaoDAO.class);
		
		Biblioteca bibliotecaBusca;
		
		if( biblioteca.getId() == TODAS_BIBLIOTECAS )
			bibliotecaBusca = null;
		else
			bibliotecaBusca = new Biblioteca(biblioteca.getId());
		
		List<TipoSituacao> situacoes = new ArrayList<TipoSituacao>();
		
		situacoes.add(TipoSituacao.SOLICITADO);
		//situacoes.add(TipoSituacao.VALIDADO);

		if(buscarAtendidas)
			situacoes.add(TipoSituacao.ATENDIDO);

		if(buscarConfirmadas)
			situacoes.add(TipoSituacao.CONFIRMADO);
		
		if(buscarCanceladas)
			situacoes.add(TipoSituacao.CANCELADO);
		
		Integer numeroSolicitacaoBusca = null;
		String nomeUsuarioSolicitanteBusca = null;
		
		if( buscarNumeroSolicitacao) numeroSolicitacaoBusca = numeroSolicitacao;
		if( buscarNomeUsuarioSolicitante) nomeUsuarioSolicitanteBusca = nomeUsuarioSolicitante;
		
		Date dataInicialBusca = null;
		Date dataFinalBusca = null;
		
		if( buscarData) {  dataInicialBusca = dataInicial; dataFinalBusca = dataFinal; };
		
		solicitacoes.clear();
		
		if(dataInicialBusca != null && dataFinalBusca != null){
			if( CalendarUtils.calculoDias(dataInicialBusca, dataFinalBusca) < 0){
				addMensagem(MensagensArquitetura.DATA_INICIO_MENOR_FIM, "Data da Solicitação");
				return null;
			}
		}
		
		solicitacoes.addAll( dao.findSolicitacoesAtivas(null, null, null, numeroSolicitacaoBusca, nomeUsuarioSolicitanteBusca, bibliotecaBusca,
				dataInicialBusca, dataFinalBusca, ! buscarRemovidasPeloUsuario, situacoes.toArray(new TipoSituacao[situacoes.size()])));
		
		if(solicitacoes.size() == 0)
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		
		if(solicitacoes.size() > 300)
			addMensagem(MensagensArquitetura.BUSCA_MAXIMO_RESULTADOS, 100);
		
		return telaListaSolicitacoes();
	}
	
	/**
	 * Exibe as informações completa da solicitação para o bibliotecário.
	 * 
	 * Possui algumas informações a mais como por quem foi validada, por quem foi atendida
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/listarSolicitacoesOrientacao.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException
	 */
	public String visualizarDadosSolicitacaoAtendimento() throws ArqException {
		
		//setConfirmButton("Visualizar Dados Solicitação");
		
		operacao = -1;
		
		initObj();          // inicializa o objeto salvo no banco com o id passado a requisição
		
		if (obj == null){
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			
			return verSolicitacoes();
		}
		
		return telaVisualizarDadosSolicitacaoAtendimento();
	}

	/**
	 * Atende uma solicitação de serviço.
	 * 
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/listarSolicitacoesOrientacao.jsp
	 */
	public String atenderSolicitacao() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);
		
		operacao = ATENDER;
		setConfirmButton("Atender");
		prepareMovimento(getMovimentoAtender());
		
		dataAtendimento = null;
		horarioInicioAtendimento = null;
		horarioFimAtendimento = null;
		
		return verificarOperacoes();
	}
	
	/**
	 * Cancelar uma solicitação de serviço (retorna ao usuário).
	 * 
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/listarSolicitacoesOrientacao.jsp
	 */
	public String cancelarSolicitacaoAtendimento() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);
		
		operacao = CANCELAR;
		motivoCancelamento = "";
		setConfirmButton("Cancelar Solicitação");
		prepareMovimento(getMovimentoCancelar());
		
		return verificarOperacoes();
	}
	
	/**
	 * O usuário que fez a solicitação confirma que vai comparecer no horário agendado pelo bibliotecário..
	 * 
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/minhasSolicitacoesOrientacao.jsp
	 */
	public String confirmarSolicitacao() throws ArqException{

		operacao = CONFIRMAR;
		setConfirmButton("Confirmar");
		prepareMovimento(getMovimentoConfirmar());

		initObj();
		
		if( !obj.isAtendido() ){
			addMensagemErro("Apenas solicitações de orientação atendidas podem ser confirmadas.");
			return null;
		}
		
		return telaVisualizarSolicitacao();
	}
	
	
	
	/**
	 * O usuário que fez a solicitação informa que NÃO pode comparecer no horário agendado pelo bibliotecário..
	 * 
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/minhasSolicitacoesOrientacao.jsp
	 */
	public String cancelarSolicitacao() throws ArqException{
		
		operacao = CANCELAR;
		motivoCancelamento = "Não estou disponível no horário definido.";
		setConfirmButton("Cancelar Agendamento");
		prepareMovimento(getMovimentoNaoConfirmar());

		initObj();
		
		if( !obj.isAtendido() ){
			addMensagemErro("Apenas solicitações de orientação atendidas podem ser canceladas.");
			return null;
		}
		
		return telaVisualizarSolicitacao();
	}
	
	
	/**
	 * <p> Método chamdo quando o ***usuário***  vai confirmar ou cancelar o seu comparecimento no horário agendado para orientação da normalização. </p>
	 * 
	 * Realiza a operação do botão confirmar da tela que visualiza os dados da solicitação. A operação realizada
	 * vai depender da operação escolhida no passo anterior pelo usuário.
	 * 
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/visualizaDadosSolicitacaoOrientacao.jsp
	 */
	public String finalizar() throws ArqException {
		
		MovimentoSolicitacaoOrientacao mov = instanciarMovimento();
		String msgConfirmacao = null;
		
		mov.setObjMovimentado(obj);

		try {
			if (isConfirmar()){
				mov.setCodMovimento(getMovimentoConfirmar());
				execute(mov);
				msgConfirmacao = "Confirmação realizada com sucesso.";
				
			} else if (isCancelar()) {
				
				mov.setMotivoCancelamento(motivoCancelamento);
				mov.setCodMovimento(getMovimentoNaoConfirmar());
				execute(mov);
				
				msgConfirmacao = "Cancelamento realizado com sucesso. Um email informando " +
						"o cancelamento do agendamento foi enviado para o  bibliotecário que atendeu a sua solicitação.";
			}
			else {
				throw new IllegalArgumentException("Tipo de operação inválida.");
			}
		
			addMensagemInformation(msgConfirmacao);
			
			return verMinhasSolicitacoes();

		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	
	
	/**
	 * Realiza a operação do botão confirmar da tela que visualiza os dados da solicitação. A operação realizada
	 * vai depender da operação escolhida no passo anterior pelo usuário.
	 * 
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/visualizarDadosSolicitacaoOrientacaoAtencimento.jsp
	 * @throws ArqException 
	 */
	public String confirmarAtendimento() throws ArqException {
		
		checkRole (SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);
		
		MovimentoSolicitacaoOrientacao mov = instanciarMovimento();
		mov.setObjMovimentado(obj);
		String msgConfirmacao = null;

		try {
			if (isAtender()){
				validarConfirmarAtendimento();
				
				Date horarioInicio = CalendarUtils.parseDate(horarioInicioAtendimento, "HH:mm"); // formatoHora.parse(horarioInicioAtendimento);
				Date horarioFim = CalendarUtils.parseDate(horarioFimAtendimento, "HH:mm"); // formatoHora.parse(horarioFimAtendimento);

				Date dataInicio = CalendarUtils.definirHorario(dataAtendimento, horarioInicio);
				Date dataFim = CalendarUtils.definirHorario(dataAtendimento, horarioFim);
				
				mov.setDataInicio(dataInicio);
				mov.setDataFim(dataFim);
				
				mov.setComentariosBibliotecario(obj.getComentariosBibliotecario()) ;
				
				mov.setCodMovimento(getMovimentoAtender());
				execute(mov);
				
				msgConfirmacao = "Agendamento do horário para atendimento realizado com sucesso. Aguarde a confirmação do usuário se ele vai poder comparecer no horário agendado ou não.";
				
			} else if (isCancelar()){
				
				mov.setCodMovimento(getMovimentoCancelar());
				mov.setMotivoCancelamento(motivoCancelamento);
				execute(mov);
				
				msgConfirmacao = "Cancelamento realizado com sucesso. Um email foi enviado ao usuário da solicitação informando o seu cancelamento.";
			}
		
			addMensagemInformation(msgConfirmacao);
			
			return buscarSolicitacoesSolicitadas();

		} catch (IllegalArgumentException e) {
			addMensagemErro(e.getMessage());
			return null;
		} catch (ParseException e) {
			addMensagemErro("Horário de atendimento inválido.");
			return null;
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		}
		
	}

	/**
	 * Valida se os campos obrigatórios foram preenchidos corretamente.
	 * 
	 * @throws NegocioException
	 */
	private void validarConfirmarAtendimento() throws NegocioException {
		ListaMensagens erros = new ListaMensagens();
		
		if (dataAtendimento == null) {
			erros.addErro("O campo 'Data de Atendimento' é obrigatório.");
		}
		if (StringUtils.isEmpty(horarioInicioAtendimento)) {
			erros.addErro("O campo 'Horário de Início' é obrigatório.");
		}
		if (StringUtils.isEmpty(horarioFimAtendimento)) {
			erros.addErro("O campo 'Horário de Fim' é obrigatório.");
		}
		if (StringUtils.isNotEmpty(motivoCancelamento) && motivoCancelamento.length() > 100) {
			erros.addErro("O campo 'Motivo' não pode ultrapassar 100 caracteres.");
		}
		
		if (erros.size() > 0) {
			throw new NegocioException(erros);
		}
	}
		
	/**
	 *   Verifica se a operação (Atender, Confirmar ou Cancelar) escolhida pelo usuário pode
	 *   ser realizada.
	 */
	protected String verificarOperacoes() throws SegurancaException, DAOException {
		
		checkRole(
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL,
				SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO );
		
		initObj();
		
		if( isAtender() ){
			if( !obj.isSolicitado() ){
				addMensagemErro("Apenas solicitações de orientação solicitadas podem ser atendidas.");
				return telaListaSolicitacoes();
			}
		} else if( isCancelar() ){
			if( obj.isConfirmado() ){
				addMensagemErro("Esta solicitação de orientação já foi confirmada. Portanto, não pode ser cancelada.");
				return telaListaSolicitacoes();
			}
			
			if( obj.isCancelado() ){
				addMensagemErro("Esta solicitação de orientação já foi cancelada. Portanto, não pode ser cancelada novamente.");
				return telaListaSolicitacoes();
			}
		}
		
		///////////////////////////////////////////////////////////////////////////////////////////
		// A tela que visualiza as informações da solicitação é comum em todos os casos          //
		// Apenas mudando os botões e alguma informação extra que depende da operação escolhida  //
		///////////////////////////////////////////////////////////////////////////////////////////
		return telaVisualizarDadosSolicitacaoAtendimento();
	
	}
	


	/**
	 * Exibe o formulário para modificar a biblioteca da solicitação selecionada.
	 * 
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/listarSolicitacoesOrientacao.jsp
	 */
	public String preTransferirSolicitacao() throws ArqException {
		checkRole (SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);

		prepareMovimento(SigaaListaComando.TRANSFERIR_SOLICITACAO_ORIENTACAO);
		
		obj = buscarNaListaByID(getParameterInt("idSolicitacao", 0));
		
		if(obj == null){
			addMensagemErro("Solicitação já Processada!");
			return null;
		}
		
		bibliotecasServico = new ArrayList<Biblioteca>();
		
		ServicoInformacaoReferenciaBibliotecaDAO dao = null;
		
		try {
			dao = getDAO(ServicoInformacaoReferenciaBibliotecaDAO.class);
			
			if(obj.getBiblioteca().isBibliotecaCentral()) // A central pode transferir para as setoriais
				bibliotecasServico = dao.findBibliotecasInternasByServico(getTipoServico(), false, true);
			else // As setoriais só podem transferir para a central
				bibliotecasServico = dao.findBibliotecasInternasByServico(getTipoServico(), true, false);
			
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
	 * Transfere uma solicitação, alterando sua biblioteca responsável.
	 * 
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/formTransferenciaSolicitacaoOrientacao.jsp
	 */
	public String transferirSolicitacao () throws ArqException{
		
		checkRole (SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);
		
		BibliotecaDao dao = null;
		
		if (bibliotecaDestino.getId() <= 0) {
			addMensagemErro("Selecione a biblioteca para onde a solicitação vai ser transferida.");
			return null;
		} else {
			try {
				dao = getDAO(BibliotecaDao.class);
				populateObj();
				
				MovimentoSolicitacaoOrientacao mov = new MovimentoSolicitacaoOrientacao();
				mov.setObjMovimentado(obj);
				mov.setBibliotecaOrigem(dao.findByPrimaryKey(obj.getBiblioteca().getId(), Biblioteca.class, new String[]{"id", "descricao", "unidade.id"}) );
				mov.setBibliotecaDestino(dao.findByPrimaryKey(bibliotecaDestino.getId(), Biblioteca.class, new String[]{"id", "descricao", "unidade.id"}) );
				mov.setCodMovimento(SigaaListaComando.TRANSFERIR_SOLICITACAO_ORIENTACAO);
				
				execute(mov);
				
				addMensagemInformation("Solicitação transferida com Sucesso. " +
						"Um email foi enviado ao solicitante e os bibliotecários da biblioteca destino informando a transferência.");
				
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
	 * Redireciona para a página utilizada para notificar alguém sobre o atendimento da solicitação.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/listarSolicitacoesOrientacao.jsp</li>
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
	 *    <li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/formNotificarSolicitacaoOrientacao.jsp</li>
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
	 * Retorna umaa solicitação de serviço da lista de solicitações de acordo com seu id.
	 * 
	 * @param id
	 * @return
	 */
	protected SolicitacaoOrientacao buscarNaListaByID(Integer id) {
		if (id != null) {
			for (SolicitacaoOrientacao solicitacao : solicitacoes) {
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

	/**
	 * Método que retorna o turno de disponibilidade do usuário baseado nas opções marcadas por ele
	 * 
	 * @return
	 * @throws ArqException
	 */
	private TurnoDisponibilidadeUsuario getTurnoDisponivel() throws ArqException {
		if (turnoManha) {
			if (turnoTarde) {
				if (turnoNoite) {
					return TurnoDisponibilidadeUsuario.TODOS;
				}
				
				return TurnoDisponibilidadeUsuario.MANHA_TARDE;
			}
			else if (turnoNoite) {
				return TurnoDisponibilidadeUsuario.MANHA_NOITE;
			}

			return TurnoDisponibilidadeUsuario.MANHA;
		}
		else if (turnoTarde) {
			if (turnoNoite) {
				return TurnoDisponibilidadeUsuario.TARDE_NOITE;
			}

			return TurnoDisponibilidadeUsuario.TARDE;
		}
		else if (turnoNoite) {
			return TurnoDisponibilidadeUsuario.NOITE;
		}
		
		return null;
	}
	
	/**
	 * Apaga os resultados da busca.
	 *
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/listarSolicitacoesOrientacao.jsp
	 */
	public String limparResultadosBusca() {
		solicitacoes.clear();
		buscarConfirmadas = false;
		buscarAtendidas = false;
		buscarCanceladas = false;
		biblioteca = new Biblioteca(TODAS_BIBLIOTECAS);
		dataFinal = null;
		dataInicial = null;
		
		return telaListaSolicitacoes();
	}
	
	/**
	 * Retorna a quantidade de bibliotecas que estão utilizando o servido de Orientação de normalização no sistema.
	 * 
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:</p>
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/minhasSolicitacoesOrientacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public int getQuantidadeBibliotecasRealizandoOrientacaoNormalizacao() throws DAOException{
		Integer quantidadeBibliotecasRealizandoOrientacaoNormalizacao = (Integer) getCurrentSession().getAttribute("_quantidadeBibliotecasRealizandoOrientacaoNormalizacao");
		
		if(quantidadeBibliotecasRealizandoOrientacaoNormalizacao == null){
			
			SolicitacaoServicoDocumentoDAO dao = null;
			
			try{
				dao = getDAO(SolicitacaoServicoDocumentoDAO.class);
				quantidadeBibliotecasRealizandoOrientacaoNormalizacao = dao.contaBibliotecasComServicoAtivo(TipoServicoInformacaoReferencia.ORIENTACAO_NORMALIZACAO);
				getCurrentSession().setAttribute("_quantidadeBibliotecasRealizandoOrientacaoNormalizacao", quantidadeBibliotecasRealizandoOrientacaoNormalizacao);
			}finally{
				if(dao != null) dao.close();
			}
		}
		
		return quantidadeBibliotecasRealizandoOrientacaoNormalizacao;
	}
	
	
	/**
	 * Método que retorna o número de autenticação gerado para ser exibido no comprovante.
	 * 
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/comprovanteSolicitacaoOrientacao.jsp
	 */
	public String getNumeroAutenticacao() {
		return BibliotecaUtil.geraNumeroAutenticacaoComprovantes(obj.getId(), obj.getDataCadastro());
	}

	public boolean isAtender(){
		return operacao == ATENDER;
	}
	
	public boolean isCancelar(){
		return operacao == CANCELAR;
	}
	
	public boolean isConfirmar(){
		return operacao == CONFIRMAR;
	}
	
	public int getTodasBibliotecas() {
		return TODAS_BIBLIOTECAS;
	}
	
	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}

	/*public Integer getSituacao() {
		return situacao;
	}

	public void setSituacao(Integer situacao) {
		this.situacao = situacao;
	}*/

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

	public Collection<SelectItem> getBibliotecasServicoCombo(){
		return toSelectItems(bibliotecasServico, "id", "descricao");
	}

	public Biblioteca getBibliotecaDestino() {
		return bibliotecaDestino;
	}

	public void setBibliotecaDestino(Biblioteca bibliotecaDestino) {
		this.bibliotecaDestino = bibliotecaDestino;
	}

	public boolean isBuscarConfirmadas() {
		return buscarConfirmadas;
	}

	public void setBuscarConfirmadas(boolean buscarConfirmadas) {
		this.buscarConfirmadas = buscarConfirmadas;
	}

	public Collection<Biblioteca> getBibliotecas() {
		return bibliotecas;
	}

	public void setBibliotecas(Collection<Biblioteca> bibliotecas) {
		this.bibliotecas = bibliotecas;
	}

	public boolean isTurnoManha() {
		return turnoManha;
	}

	public void setTurnoManha(boolean turnoManha) {
		this.turnoManha = turnoManha;
	}

	public boolean isTurnoTarde() {
		return turnoTarde;
	}

	public void setTurnoTarde(boolean turnoTarde) {
		this.turnoTarde = turnoTarde;
	}

	public boolean isTurnoNoite() {
		return turnoNoite;
	}

	public void setTurnoNoite(boolean turnoNoite) {
		this.turnoNoite = turnoNoite;
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