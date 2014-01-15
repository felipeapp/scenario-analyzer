/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p>MBean respons�vel pelos atendimento e solicita��o de normaliza��o de obra realizada por discentes ou servidores � biblioteca.</p>
 * 
 * @author Felipe Rivas
 */
@Component("solicitacaoOrientacaoMBean")
@Scope("request")
public class SolicitacaoOrientacaoMBean extends SigaaAbstractController<SolicitacaoOrientacao> {

	/** P�gina onde o usu�rio faz uma nova solicita��o de normaliza��o */
	public static final String PAGINA_NOVA_SOLICITACAO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/novaSolicitacaoOrientacao.jsp";
	
	/** P�gina onde o usu�rio pode visualizar os dados da sua solicita��o */
	public static final String PAGINA_VISUALIZA_DADOS_SOLICITACAO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoOrientacao.jsp";
	
	/** O comprovante de solicita��o de normaliza��o. */
	public static final String PAGINA_VISUALIZA_COMPROVANTE_SOLICITACAO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/comprovanteSolicitacaoOrientacao.jsp";
	
	/** P�gina onde o bibliotec�rio pode visualizar os dados da sua solicita��o */
	public static final String PAGINA_VISUALIZA_DADOS_SOLICITACAO_ATENDIMENTO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/visualizarDadosSolicitacaoOrientacaoAtendimento.jsp";
	
	/** Usu�rio visualiza as solicita��es feitas por ele */
	public static final String PAGINA_VISUALIZA_MINHAS_SOLICITACOES =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/minhasSolicitacoesOrientacao.jsf";

	/** Bibliotec�rio visualiza as solicita��es para atender*/
	public static final String PAGINA_VISUALIZA_SOLICITACOES =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/listarSolicitacoesOrientacao.jsf";
	
	/** O bibliotec�rio transfere uma solicita��o da sua biblioteca para a central */
	public static final String PAGINA_TRANSFERENCIA_SOLICITACAO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/formTransferenciaSolicitacaoOrientacao.jsp";
	
	/** O bibliotec�rio transfere uma solicita��o da sua biblioteca para a central */
	public static final String PAGINA_FINALIZAR_SOLICITACAO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/finalizarSolicitacaoOrientacao.jsp";
	
	/** P�gina na qual o bibliotec�rio vai notificar algu�m sobre a solicita��o, geralmente o setor de processos t�cnicos para realizar o atendimento. */
	public static final String PAGINA_NOTIFICAR_SOLICITACAO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/formNotificarSolicitacaoOrientacao.jsp";
	
	
	
	/** usado no bot�o no qual o usu�rio faz a solicita��o ou a altera */
	private static final String TEXTO_ALTERAR = "Alterar";
	/** Valor padr�o para indicar a sele��o de todas as bibliotecas no filtro de busca */
	private static final int TODAS_BIBLIOTECAS = -1;
	
	/** Armazena a opera��o que est� sendo executada no momento. */
	private int operacao;

	/** Opera��o de atender. */
	public static final int ATENDER = 1;
	/** Opera��o de confirmar. */
	public static final int CONFIRMAR = 2;
	/** Opera��o de cancelar. */
	public static final int CANCELAR = 3;
	
	/** Se o bibliotec�rio cancelar uma solicita��o tem que informar ao usu�rio que a solicitou */
	private String motivoCancelamento;
	
	/** Usado na transfer�ncia de solicita��es */
	private Collection<Biblioteca> bibliotecasServico = new ArrayList<Biblioteca>();
	
	/** Usado na transfer�ncia de solicita��es */
	private Biblioteca bibliotecaDestino = new Biblioteca(-1);
	
	/**
	 * Guarda as solicita��es feitas pelo usu�rio quando o caso de uso � de realizar uma
	 * solicita��o de servi�o.
	 * Guarda as solicita��es que o bibliotec�rio buscou na atendimento das solicita��es
	 */
	private SortedSet<SolicitacaoOrientacao> solicitacoes;
	
	/**
	 * Biblioteca para as quais o usu�rio pode realizar as solicita��es.
	 */
	private Collection<Biblioteca> bibliotecas = new ArrayList<Biblioteca>();
	/** Biblioteca selecionada no filtro de busca */
	private Biblioteca biblioteca = new Biblioteca(-1);
	
	/** Campo para busca do usu�rio pelo n�mero da solicita��o */
	private Integer numeroSolicitacao;
	
	/** Campo para busca do usu�rio pelo nome de quem realizou a solicita��o  */
	private String nomeUsuarioSolicitante;
	
	/** Indica se o filtro de numero da solicita��o est� ativado */
	private boolean buscarNumeroSolicitacao;
	
	/** Indica se o filtro de data est� ativado */
	private boolean buscarData;
	
	
	/** Indica se o filtro de nome do solicitante est� ativado */
	private boolean buscarNomeUsuarioSolicitante;
	
	/** Limite inicial da data de cadastro selecionado no filtro de busca */
	private Date dataInicial;
	/** Limite final da data de cadastro selecionado no filtro de busca */
	private Date dataFinal;
	/** Indica se as solicita��es atendidas devem ser inclu�das no resultado da busca */
	private boolean buscarAtendidas; 
	/** Indica se as solicita��es canceladas devem ser inclu�das no resultado da busca */
	private boolean buscarCanceladas;
	/** Indica se as solicita��es confirmadas devem ser inclu�das no resultado da busca */
	private boolean buscarConfirmadas;
	/** Retorna as solicita��es removidas pelo usu�rio, antes mesmo do atendimento ser realizado. */
	private boolean buscarRemovidasPeloUsuario;
	
	/** Data de atendimento informada pelo bibliotec�rio no atendimento da solicita��o */
	private Date dataAtendimento;
	/** Hor�rio in�cio de atendimento informado pelo bibliotec�rio no atendimento da solicita��o */
	private String horarioInicioAtendimento;
	/** Hor�rio t�rmino de atendimento informado pelo bibliotec�rio no atendimento da solicita��o */
	private String horarioFimAtendimento;
	/** Indica se o usu�rio tem disponibilidade de atendimento no turno matutino */
	private boolean turnoManha;
	/** Indica se o usu�rio tem disponibilidade de atendimento no turno vespertino */
	private boolean turnoTarde;
	/** Indica se o usu�rio tem disponibilidade de atendimento no turno noturno */
	private boolean turnoNoite;
	
	
	/** Email para onde ser� enviado a notifica��o sobre a solicita��o de cataloga��o. */
	private String emailNotificacao;
	
	/** O Texto que ser� enviado no email de notifica��o sobre a solicita��o de cataloga��o.  */
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
	 * Retorna a inst�ncia de um movimento espec�fico atrav�s de polimorfismo.
	 * 
	 * @return
	 */
	protected MovimentoSolicitacaoOrientacao instanciarMovimento() {
		return new MovimentoSolicitacaoOrientacao();
	}

	/**
	 * Retorna a propriedade da classe ServicosBiblioteca que representa o tipo de servi�o solicitado
	 * 
	 * @return
	 */
	public String getPropriedadeServico() {
		return "realizaOrientacaoNormalizacao";
	}

	/**
	 * Redireciona o fluxo de navega��o para a p�gina de cria��o de solicita��o.
	 * 
	 * @return
	 */
	protected String telaNovaSolicitacaoServico() {
		return forward(PAGINA_NOVA_SOLICITACAO);
	}

	/**
	 * Redireciona o fluxo de navega��o para a p�gina de visualiza��o dos dados de uma solicita��o do usu�rio.
	 * 
	 * @return
	 */
	protected String telaVisualizarSolicitacao() {
		return forward(PAGINA_VISUALIZA_DADOS_SOLICITACAO);
	}

	/**
	 * Redireciona o fluxo de navega��o para a p�gina de visualiza��o dos dados de uma solicita��o para o bibliotec�rio.
	 * 
	 * @return
	 */
	protected String telaVisualizarDadosSolicitacaoAtendimento() {
		return forward(PAGINA_VISUALIZA_DADOS_SOLICITACAO_ATENDIMENTO);
	}

	/**
	 * Redireciona o fluxo de navega��o para a p�gina de confirma��o ou cancelamento de um agendamento por parte do usu�rio.
	 * 
	 * @return
	 */
	protected String telaFinalizarSolicitacao() {
		return forward(PAGINA_FINALIZAR_SOLICITACAO);
	}

	/**
	 * Redireciona o fluxo de navega��o para a p�gina de exibi��o do comprovante de solicita��o.
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
	 *    Inicializa a solicita��o, se tiver algum idSolicitacao como par�metro da
	 * solicita��o, inicializa o <tt>obj</tt> com os dados da solicita��o salva no banco(
	 * altera��o e remo��o), sen�o inicializa uma nova solicita��o
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
	 * Inicia o caso de uso onde o usu�rio visualiza as suas solicita��es feitas.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
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
			
		    // Verifica se o usu�rio possuia alguma conta ativida na biblioteca para poder solicitar os servi�os da se��o de Info. e Ref. 
			// Caso n�o tenha ser� lan�ada uma NegocioException
			UsuarioBibliotecaUtil.retornaUsuarioBibliotecaUnicoDaPessoa(getUsuarioLogado().getPessoa().getId(), null);
			
			dao = getDAO(SolicitacaoOrientacaoDAO.class);
			
			//// ENCONTRA AS SOLICITA��ES FEITAS PELA PESSOA LOGADA ////////
			
			// Usu�rio acessou pelo portal discente
			if (SigaaSubsistemas.PORTAL_DISCENTE.equals(getSubSistema())) {
				if (getDiscenteUsuario() != null ){
					solicitacoes.addAll( dao.findSolicitacoesAtivas(getDiscenteUsuario().getPessoa(), null, null, null, null, 
						null, null, null, true,(TipoSituacao[]) null) );
					return telaMinhasSolicitacoes();
				}
			// Usu�rio acessou pelo portal docente (professor)
			} else if (SigaaSubsistemas.PORTAL_DOCENTE.equals(getSubSistema())) {
				if (getUsuarioLogado().getServidor() != null ){
					solicitacoes.addAll( dao.findSolicitacoesAtivas(getServidorUsuario().getPessoa(), null, null,  null, null,
						null, null, null, true,(TipoSituacao[]) null) );
					return telaMinhasSolicitacoes();
				}
			// Usu�rio acessou pelo m�dulo do servidor na biblioteca (servidor ou professor)
			} else if (getUsuarioLogado().getServidor() != null ) {
				solicitacoes.addAll( dao.findSolicitacoesAtivas(getServidorUsuario().getPessoa(), null, null,  null, null,
						null, null, null, true,(TipoSituacao[]) null) );
				return telaMinhasSolicitacoes();
			}
			
			addMensagemErro("Usu�rio n�o pode realizar solicita��es de servi�os da biblioteca, usu�rio n�o � discente nem servidor da institui��o.");
			
			return null;			
		}catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		} finally {
			if (dao != null) dao.close();
		}
	}

	/**
	 * Redireciona o fluxo de navega��o para a p�gina de exibi��o das solicita��es do usu�rio.
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
	 * N�o dava para usar o <code>contains</code> de <code>Collections</code> porque o
	 * Hibernate traz objetos <code>proxy</code> e o <code>equals</code> e
	 * <code>hashcode</code> n�o funcionam
	 */
	private boolean contains(Collection<Biblioteca> bib, Biblioteca b) {
		for (Biblioteca bibl : bib) {
			if(bibl.getId() == b.getId())
				return true;
		}
		
		return false;
	}
	
	/**
	 *   M�todo que obt�m a biblioteca que o usu�rio pode solicitar o servi�o.
	 *   Se for aluno a setorial dele, se for servidor ou aluno sem setorial a biblioteca Central.
	 * @throws ArqException
	 *
	 */
	private void obtemBibliotecaSolicitante() throws ArqException {
		
		bibliotecas = new ArrayList<Biblioteca>();
		
		// Acessou pelo portal discente, ent�o vai fazer a solicita��o como discente
		if (getSubSistema().equals( SigaaSubsistemas.PORTAL_DISCENTE)){
			if (!isEmpty(obj.getDiscente().getUnidade())){
				bibliotecas = BibliotecaUtil.getBibliotecasDoDiscenteByServico(obj.getDiscente(), getTipoServico());
			}
		} else { // Solicita��o como servidor
			bibliotecas.add( BibliotecaUtil.getBibliotecaCentral() );
		}
		
		// Se o usu�rio fez uma solicita��o para a setorial do centro dele como aluno e depois
		// se loga como servidor, s� vai poder fazer novas solicita��es para central, mas para as
		// solicita��es j� feitas tem que aparecer a biblioteca onde ela estava
		
		if(obj.getId() > 0 && obj.getBiblioteca() != null && ! contains(bibliotecas, obj.getBiblioteca())){
			bibliotecas.add(obj.getBiblioteca());
		}
		
		// N�o deixa a biblioteca central se houver somente ela e ela n�o estiver oferecendo
		// os servi�o desejado
		if ( bibliotecas.size() == 1 ) {
			Iterator<Biblioteca> it = bibliotecas.iterator();
			if ( ! BibliotecaUtil.bibliotecaRealizaServico(getPropriedadeServico(), it.next().getId()))
				it.remove();
		}
		
		if (bibliotecas.size() == 0) {
			addMensagemErro("No momento n�o h� bibliotecas disponibilizando este servi�o.");
		}
	}
	
	/**
	 * Inicia o caso de uso onde o usu�rio faz uma solicita��o de servi�o.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/minhasSolicitacoesOrientacao.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException
	 */
	public String realizarNovaSolicitacao() throws ArqException {
		
		setConfirmButton("Cadastrar Solicita��o");
		
		initObj();
		
		BibliotecaDao dao = null;

		turnoManha = false;
		turnoTarde = false;
		turnoNoite = false;
		
		try {
			dao = getDAO(BibliotecaDao.class);
		
			if( getSubSistema().equals( SigaaSubsistemas.PORTAL_DISCENTE ) ){ // se acessou pelo portal discente � aluno
				obj.setDiscente( getDiscenteUsuario().getDiscente() );
				obj.setPessoa(getDiscenteUsuario().getPessoa());
				obj.setServidor( null );
			} else {                                     // qualquer outra forma de acesso � servidor
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
	 * Exibe as informa��es completas da solicita��o para o usu�rio.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/minhasSolicitacoesOrientacao.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException 
	 */
	public String visualizarDadosSolicitacao() throws DAOException {
		operacao = -1;
		initObj();          // inicializa o objeto salvo no banco com o id passado a requisi��o
		
		if (obj == null || obj.getId() == 0 || !obj.isAtiva()) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			
			return verMinhasSolicitacoes();
		}
		
		return telaVisualizarSolicitacao();
	}

	/**
	 * Exibe a tela com o comprovante da solicita��o.
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
	 * Cria a solicita��o de servi�o do usu�rio.
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
				addMensagemInformation("Solicita��o de orienta��o cadastrada com sucesso, aguarde " +
						"a defini��o do agendamento por parte de um bibliotecario da " +
						obj.getBiblioteca().getDescricao() + ".");
			} else {
 				mov.setCodMovimento(getMovimentoAlterar());
				execute(mov);
				addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Solicita��o");
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
	 * Altera os dados da solicita��o feita.
	 * O usu�rio pode alterar solicita��es que est�o com status "Solicitado".
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/minhasSolicitacoesOrientacao.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException 
	 */
	public String alterarSolicitacao() throws ArqException {
		
		setConfirmButton(TEXTO_ALTERAR);

		BibliotecaDao dao = null;
		initObj(); // inicializa o objeto salvo no banco com o id passado a requisi��o
		
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
		
		// tela que cria a solicita��o sendo que agora com o bot�o alterar habilitado
		return telaNovaSolicitacaoServico();
	}

	/**
	 * Remove a solicita��o selecionada.
	 * O usu�rio pode remover solicita��es que est�o com status "Solicitado".
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/minhasSolicitacoesOrientacao.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	public String removerSolicitacao() throws ArqException {
		
		initObj();   // inicializa o objeto salvo no banco com o id passado a requisi��o
		
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
				
				addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Solicita��o");
				
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
	 * Redireciona o fluxo de navega��o para a p�gina de exibi��o das solicita��es em aberto para o bibliotec�rio.
	 * 
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/formTransferenciaSolicitacaoOrientacao.jsp
	 * 
	 * @return
	 */
	public String telaListaSolicitacoes(){
		return forward( PAGINA_VISUALIZA_SOLICITACOES);
	}

	/**
	 * Exibe a tela com as solicita��es a serem atendidas para o bibliotec�rio.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
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
			addMensagemWarning("O senhor(a) n�o possui permiss�o em nenhuma biblioteca para gerenciar as solicita��es de orienta��o de normaliza��o.");
		}
		
		return telaListaSolicitacoes();
	}
	
	/**
	 *   M�todo que obt�m a biblioteca para o biblioteca atender as requisi��es.<br/><br/>
	 *   Se for administrador geral, retorna todas, sen�o retorna as biblioteca onde o bibliotec�rio tem permiss�o
	 *   de cataloga��o e informa��o e refer�ncia.<br/><br/>
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
	 * Busca todas as solicita��es de servi�o n�o canceladas
	 * cadastradas no sistema de acordo com os filtros selecionados na p�gina.
	 * 
	 * <p>Usado quando o bibliotec�rio vai pesquisar as solicita��es para atend�-las.</p>
	 *     
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
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
				addMensagem(MensagensArquitetura.DATA_INICIO_MENOR_FIM, "Data da Solicita��o");
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
	 * Exibe as informa��es completa da solicita��o para o bibliotec�rio.
	 * 
	 * Possui algumas informa��es a mais como por quem foi validada, por quem foi atendida
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/listarSolicitacoesOrientacao.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException
	 */
	public String visualizarDadosSolicitacaoAtendimento() throws ArqException {
		
		//setConfirmButton("Visualizar Dados Solicita��o");
		
		operacao = -1;
		
		initObj();          // inicializa o objeto salvo no banco com o id passado a requisi��o
		
		if (obj == null){
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			
			return verSolicitacoes();
		}
		
		return telaVisualizarDadosSolicitacaoAtendimento();
	}

	/**
	 * Atende uma solicita��o de servi�o.
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
	 * Cancelar uma solicita��o de servi�o (retorna ao usu�rio).
	 * 
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/listarSolicitacoesOrientacao.jsp
	 */
	public String cancelarSolicitacaoAtendimento() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);
		
		operacao = CANCELAR;
		motivoCancelamento = "";
		setConfirmButton("Cancelar Solicita��o");
		prepareMovimento(getMovimentoCancelar());
		
		return verificarOperacoes();
	}
	
	/**
	 * O usu�rio que fez a solicita��o confirma que vai comparecer no hor�rio agendado pelo bibliotec�rio..
	 * 
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/minhasSolicitacoesOrientacao.jsp
	 */
	public String confirmarSolicitacao() throws ArqException{

		operacao = CONFIRMAR;
		setConfirmButton("Confirmar");
		prepareMovimento(getMovimentoConfirmar());

		initObj();
		
		if( !obj.isAtendido() ){
			addMensagemErro("Apenas solicita��es de orienta��o atendidas podem ser confirmadas.");
			return null;
		}
		
		return telaVisualizarSolicitacao();
	}
	
	
	
	/**
	 * O usu�rio que fez a solicita��o informa que N�O pode comparecer no hor�rio agendado pelo bibliotec�rio..
	 * 
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/minhasSolicitacoesOrientacao.jsp
	 */
	public String cancelarSolicitacao() throws ArqException{
		
		operacao = CANCELAR;
		motivoCancelamento = "N�o estou dispon�vel no hor�rio definido.";
		setConfirmButton("Cancelar Agendamento");
		prepareMovimento(getMovimentoNaoConfirmar());

		initObj();
		
		if( !obj.isAtendido() ){
			addMensagemErro("Apenas solicita��es de orienta��o atendidas podem ser canceladas.");
			return null;
		}
		
		return telaVisualizarSolicitacao();
	}
	
	
	/**
	 * <p> M�todo chamdo quando o ***usu�rio***  vai confirmar ou cancelar o seu comparecimento no hor�rio agendado para orienta��o da normaliza��o. </p>
	 * 
	 * Realiza a opera��o do bot�o confirmar da tela que visualiza os dados da solicita��o. A opera��o realizada
	 * vai depender da opera��o escolhida no passo anterior pelo usu�rio.
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
				msgConfirmacao = "Confirma��o realizada com sucesso.";
				
			} else if (isCancelar()) {
				
				mov.setMotivoCancelamento(motivoCancelamento);
				mov.setCodMovimento(getMovimentoNaoConfirmar());
				execute(mov);
				
				msgConfirmacao = "Cancelamento realizado com sucesso. Um email informando " +
						"o cancelamento do agendamento foi enviado para o  bibliotec�rio que atendeu a sua solicita��o.";
			}
			else {
				throw new IllegalArgumentException("Tipo de opera��o inv�lida.");
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
	 * Realiza a opera��o do bot�o confirmar da tela que visualiza os dados da solicita��o. A opera��o realizada
	 * vai depender da opera��o escolhida no passo anterior pelo usu�rio.
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
				
				msgConfirmacao = "Agendamento do hor�rio para atendimento realizado com sucesso. Aguarde a confirma��o do usu�rio se ele vai poder comparecer no hor�rio agendado ou n�o.";
				
			} else if (isCancelar()){
				
				mov.setCodMovimento(getMovimentoCancelar());
				mov.setMotivoCancelamento(motivoCancelamento);
				execute(mov);
				
				msgConfirmacao = "Cancelamento realizado com sucesso. Um email foi enviado ao usu�rio da solicita��o informando o seu cancelamento.";
			}
		
			addMensagemInformation(msgConfirmacao);
			
			return buscarSolicitacoesSolicitadas();

		} catch (IllegalArgumentException e) {
			addMensagemErro(e.getMessage());
			return null;
		} catch (ParseException e) {
			addMensagemErro("Hor�rio de atendimento inv�lido.");
			return null;
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		}
		
	}

	/**
	 * Valida se os campos obrigat�rios foram preenchidos corretamente.
	 * 
	 * @throws NegocioException
	 */
	private void validarConfirmarAtendimento() throws NegocioException {
		ListaMensagens erros = new ListaMensagens();
		
		if (dataAtendimento == null) {
			erros.addErro("O campo 'Data de Atendimento' � obrigat�rio.");
		}
		if (StringUtils.isEmpty(horarioInicioAtendimento)) {
			erros.addErro("O campo 'Hor�rio de In�cio' � obrigat�rio.");
		}
		if (StringUtils.isEmpty(horarioFimAtendimento)) {
			erros.addErro("O campo 'Hor�rio de Fim' � obrigat�rio.");
		}
		if (StringUtils.isNotEmpty(motivoCancelamento) && motivoCancelamento.length() > 100) {
			erros.addErro("O campo 'Motivo' n�o pode ultrapassar 100 caracteres.");
		}
		
		if (erros.size() > 0) {
			throw new NegocioException(erros);
		}
	}
		
	/**
	 *   Verifica se a opera��o (Atender, Confirmar ou Cancelar) escolhida pelo usu�rio pode
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
				addMensagemErro("Apenas solicita��es de orienta��o solicitadas podem ser atendidas.");
				return telaListaSolicitacoes();
			}
		} else if( isCancelar() ){
			if( obj.isConfirmado() ){
				addMensagemErro("Esta solicita��o de orienta��o j� foi confirmada. Portanto, n�o pode ser cancelada.");
				return telaListaSolicitacoes();
			}
			
			if( obj.isCancelado() ){
				addMensagemErro("Esta solicita��o de orienta��o j� foi cancelada. Portanto, n�o pode ser cancelada novamente.");
				return telaListaSolicitacoes();
			}
		}
		
		///////////////////////////////////////////////////////////////////////////////////////////
		// A tela que visualiza as informa��es da solicita��o � comum em todos os casos          //
		// Apenas mudando os bot�es e alguma informa��o extra que depende da opera��o escolhida  //
		///////////////////////////////////////////////////////////////////////////////////////////
		return telaVisualizarDadosSolicitacaoAtendimento();
	
	}
	


	/**
	 * Exibe o formul�rio para modificar a biblioteca da solicita��o selecionada.
	 * 
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/listarSolicitacoesOrientacao.jsp
	 */
	public String preTransferirSolicitacao() throws ArqException {
		checkRole (SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);

		prepareMovimento(SigaaListaComando.TRANSFERIR_SOLICITACAO_ORIENTACAO);
		
		obj = buscarNaListaByID(getParameterInt("idSolicitacao", 0));
		
		if(obj == null){
			addMensagemErro("Solicita��o j� Processada!");
			return null;
		}
		
		bibliotecasServico = new ArrayList<Biblioteca>();
		
		ServicoInformacaoReferenciaBibliotecaDAO dao = null;
		
		try {
			dao = getDAO(ServicoInformacaoReferenciaBibliotecaDAO.class);
			
			if(obj.getBiblioteca().isBibliotecaCentral()) // A central pode transferir para as setoriais
				bibliotecasServico = dao.findBibliotecasInternasByServico(getTipoServico(), false, true);
			else // As setoriais s� podem transferir para a central
				bibliotecasServico = dao.findBibliotecasInternasByServico(getTipoServico(), true, false);
			
			if (bibliotecasServico.size() == 0) {
				addMensagemErro("No momento n�o h� outras bibliotecas disponibilizando este servi�o.");
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
	 * Transfere uma solicita��o, alterando sua biblioteca respons�vel.
	 * 
	 * Utilizado na JSP sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/formTransferenciaSolicitacaoOrientacao.jsp
	 */
	public String transferirSolicitacao () throws ArqException{
		
		checkRole (SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);
		
		BibliotecaDao dao = null;
		
		if (bibliotecaDestino.getId() <= 0) {
			addMensagemErro("Selecione a biblioteca para onde a solicita��o vai ser transferida.");
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
				
				addMensagemInformation("Solicita��o transferida com Sucesso. " +
						"Um email foi enviado ao solicitante e os bibliotec�rios da biblioteca destino informando a transfer�ncia.");
				
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
	 * Redireciona para a p�gina utilizada para notificar algu�m sobre o atendimento da solicita��o.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/listarSolicitacoesOrientacao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String preNotificarSolicitacao() {
		
		
		obj = buscarNaListaByID(getParameterInt("idSolicitacao", 0));
		
		if(obj == null){
			addMensagemErro("Solicita��o j� processada!");
			return null;
		}
		
		emailNotificacao = "";
		textoNotificacao ="Prezado setor de                  , <br/><br/>"
			+"  Informamos que a solicita��o de "+obj.getTipoServico()+" n�mero "+obj.getNumeroSolicitacao()+" do usu�rio: "+obj.getPessoa().getNome()
			+" para a biblioteca: "+obj.getBiblioteca().getDescricao()
			+" est� aguardando atendimento pelo seu setor. "+"<br/>"
			+ProcessadorSolicitacaoOrientacao.ASSINATURA_SETOR_INFORMACAO_E_REFERENCIA;
		
		return forward(PAGINA_NOTIFICAR_SOLICITACAO);
	}
	
	
	/**
	 *  Email um email para notificar o usu�rio sobre a solicita��o pendente de atendimento.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
		
		new EnvioEmailBiblioteca().enviaEmailSimples(null, emailNotificacao, "Notifica��o de Solicita��o Pendente de Atendimento"
				, "Solicita��o Pendente", EnvioEmailBiblioteca.AVISO_INFORMACAO_REFERENCIA, textoNotificacao);
		
		addMensagemInformation("A notifica��o enviada para o email "+emailNotificacao);
		
		return telaListaSolicitacoes();
	}
	
	
	/**
	 * Retorna umaa solicita��o de servi�o da lista de solicita��es de acordo com seu id.
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
			throw new IllegalArgumentException("Valor do par�metro ID � inv�lido.");
		}
		
		return null;
	}

	/**
	 * M�todo que retorna o turno de disponibilidade do usu�rio baseado nas op��es marcadas por ele
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
	 * Retorna a quantidade de bibliotecas que est�o utilizando o servido de Orienta��o de normaliza��o no sistema.
	 * 
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:</p>
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
	 * M�todo que retorna o n�mero de autentica��o gerado para ser exibido no comprovante.
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