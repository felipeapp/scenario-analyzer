/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 06/10/2009
 *
 */

package br.ufrn.sigaa.extensao.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividade;

/**
 * <p>Gerencia as inscri��es de Cursos e Eventos pelo Portal Docente.</p>
 * 
 * <p>Entre as a��es de gerenciamente est�o  a aprova��o e rejei��o da inscria��o e a confirma��o manual do pagamento da GRU, etc...</p>
 * 
 * 
 * @author Daniel Augusto 
 * @deprecated t� ficando imposs�vel manter esse c�digo, separado em v�rios MBens 
 */
@Scope("session")
@Component("inscricaoAtividade")
@Deprecated
public class InscricaoAtividadeMBean extends SigaaAbstractController<InscricaoAtividade> {
	
	
//	/** P�gina para confirmar o pagamento manualmente de um curso e evento. Caso a confirma��o do banco demore a chegar, po�s existe a confirma�a� autom�tica. */
//	public static final String PAGINA_CONFIRMAR_PAGAMENTO = "/confirmarPagamentoCursosEventosManualmente.jsp";
//	
//	
//	
//	/** Form para cadastro de inscri��o */
//	private static final String FORM_INSCRICAO_ATIVIDADE = "/form.jsp";
//	/** Form para cadastro de inscri��o */
//	private static final String FORM_INSCRICAO_SUB_ATIVIDADE = "/form_inscricao_sub_atividades.jsp";
//	/** Lista as inscri��es criadas para uma a��o de extens�o */
//	private static final String LISTA = "/lista.jsp";
//	/** Lista as inscri��es criadas para uma a��o de extens�o */
//	private static final String LISTA_SUB_ATIVIDADE = "/lista_sub_atividades.jsp";
//	/** Lista com a��es e inscri��es  */
//	private static final String LISTA_ACOES = "/lista_acoes.jsp";
//	/** P�gina gerenciar participantes */
//	private static final String GERENCIAR_PARTICIPANTES = "/gerenciar_participantes.jsp";
//	
//	/** P�gina lista de inscri��es. � a mesma coisa da p�gina LISTA_SUB_ATIVIDADE*/
//	@Deprecated 
//	private static final String LISTA_INSCRICOES = "/lista_inscricoes.jsp";
//	/** P�gina lista de inscri��es. � a mesma coisa da p�gina LISTA_SUB_ATIVIDADE  */
//	@Deprecated 
//	private static final String LISTA_INSCRICOES_SUB_ATIVIDADE = "/lista_inscricoes_sub_atividade.jsp";
//	
//	/** P�gina dados de inscrito  */
//	private static final String EXIBIR_DADOS_INSCRITO = "/exibir_dados_inscrito.jsp";
//	/** P�gina relat�rio de inscritos */
//	private static final String RELATORIO_INSCRITOS = "/relatorio_inscritos.jsp";
//	/** P�gina relat�rio de inscritos em subatividades*/
//	private static final String RELATORIO_INSCRITOS_SUB_ATIVIDADES = "/relatorio_inscritos_sub_atividade.jsp";
//	/** P�gina Inscritos */
//	private static final String INSCRITOS = "/inscritos.jsp";
//	/** P�gina Inscritos */
//	private static final String PENDENTES = "/pendentes.jsp";
//	/** P�gina para alterar os dados dos participantes */
//	private static final String ALTERAR_DADOS_INSCRITO = "/alterar_dados_inscrito.jsp";
//	
//	
//	
//	/** Inscri��es de uma a��o */
//	private Collection<InscricaoAtividade> inscricoesAtividades = new ArrayList<InscricaoAtividade>();
//	/** Inscri��es de uma a��o */
//	private Collection<InscricaoAtividade> inscricoesSubAtividades = new ArrayList<InscricaoAtividade>();
//	/** A��es de um coordenador */
//	private Collection<AtividadeExtensao> atividadesCoordenador = new ArrayList<AtividadeExtensao>();	
//	/** SubA��es de um coordenador */
//	private Collection<AtividadeExtensao> subAtividadesCoordenador = new ArrayList<AtividadeExtensao>();
//	/** Lista de SelectItem question�rios. */
//	private List<SelectItem> possiveisQuestionarios;
//	/** A��o de Extens�o */	
//	private AtividadeExtensao atividade = new AtividadeExtensao();
//	/** Sub atividade de uma A��o de Extens�o */	
//	private SubAtividadeExtensao subAtividade = new SubAtividadeExtensao();
//	/** Participante de a��o de extens�o. Na verdade � a inscri��o do participante */
//	private InscricaoAtividadeParticipante participante = new InscricaoAtividadeParticipante();
//	/** Data in�cio para inscri��o */
//	private Date periodoAbertoInicio;
//	/** Data fim para inscri��o */
//	private Date periodoAbertoFim;
//	/** ConfirmButton */
//	private String confirmButton = "Cadastrar";
//	/** Auxiliar texto */
//	private String texto;
//	/** Auxiliar motivo erro */
//	private String erroMotivo;
//	/** Controle para exibir painel  */
//	private boolean exibirPainel;
//	/** Controle motivo */
//	private boolean disableMotivo;
//	/** Auxiliar para altera��o de vagas de uma a��o de extens�o */
//	private Integer novasVagas = new Integer(0);
//	/** Auxiliar para altera��o de vagas de uma mini atividade */
//	private Integer novasVagasMiniAtividade = new Integer(0);
//	/** Auxiliar idAtividade */
//	private Integer idAtividade = new Integer(0);
//	/** Auxiliar idSubAtividade */
//	private Integer idSubAtividade = new Integer(0);
//	/** N�mero inscri��es atual */
//	private Integer numeroInscAtual;
//	/** Inscri��es aceitas */
//	private Collection<InscricaoAtividadeParticipante> inscricoesAceitas = new ArrayList<InscricaoAtividadeParticipante>();
//	/** Inscri�oes recusadas */
//	private Collection<InscricaoAtividadeParticipante> inscricoesRecusadas = new ArrayList<InscricaoAtividadeParticipante>();
//	/** Inscri��es canceladas */
//	private Collection<InscricaoAtividadeParticipante> inscricoesCanceladas = new ArrayList<InscricaoAtividadeParticipante>();
//	/** Inscri��es n�o confirmadas */
//	private Collection<InscricaoAtividadeParticipante> inscricoesNaoConfirmadas = new ArrayList<InscricaoAtividadeParticipante>();
//	/** Inscri��es confirmadas */
//	private Collection<InscricaoAtividadeParticipante> inscricoesConfirmadas = new ArrayList<InscricaoAtividadeParticipante>();
//
//	
//	/** Sub atividade de uma A��o de Extens�o que � selecionado entre as subatividades listadas 
//	 * no caso de uso gerenciar inscri��es.
//	 * 
//	 * Foi criada uma nova vari�vel porque na tela que listas as incri��es existem v�rias 
//	 * opera��es que eram realizadas sempre em cima da mesma vari�vel "subAtividade", sobre escrevendo os dados da subatividade selecionada.
//	 * 
//	 * @see subAtividadesCoordenador
//	 * @see this#listarInscricoesMiniAtividade();
//	 */	
//	private SubAtividadeExtensao subAtividadeSelecionada = new SubAtividadeExtensao();
//	
//	
//	/** Atividade de uma A��o de Extens�o que � selecionado entre as atividades listadas 
//	 * no caso de uso gerenciar inscri��es.
//	 * 
//	 * Foi criada uma nova vari�vel porque na tela que listas as incri��es existem v�rias 
//	 * opera��es que eram realizadas sempre em cima da mesma vari�vel "tividade", sobre escrevendo os dados da atividade selecionada.
//	 * 
//	 * @see atividadesCoordenador
//	 * @see this#listarInscricoes();
//	 */	
//	private AtividadeExtensao atividadeSelecionada = new AtividadeExtensao();
//	
//	
//	
//	/** Diz se o usu�rio est� genrenciando a tela de inscri��es em atividades ou sub atividades.. */
//	private boolean genrenciandoInscricoesSubAtividades = false;
//	
//	
//	/** Usado na alterado dos dados do participante */
//	private Collection<SelectItem> municipiosEndereco = new ArrayList<SelectItem>(0);
//	
//	/** Guarda a inscri��o que estava com o status de pagamento EM ABERTO selecionado pelo coordenador para confirmar o seu pagamento manualmente. */
//	private InscricaoAtividadeParticipante inscricaoParaConfirmarPagamento; 
//	
//	public InscricaoAtividadeMBean() {
//		obj = new InscricaoAtividade();
//	}
//
//	/**
//	 * Busca todos os Cursos e Eventos dos quais o docente logado seja coordenador.
//	 * 
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/portais/docente/menu_docente.jsp
//	 * 
//	 * @return
//	 * @throws DAOException 
//	 */
//	public String iniciarProcesso() throws DAOException {		
//		
//		AtividadeExtensaoDao dao = null;
//		
//		try{
//			dao =getDAO(AtividadeExtensaoDao.class);
//			setAtividadesCoordenador(dao.findCursoEventoAtivosEmExecucaoByCoordenador(getUsuarioLogado().getServidor()));
//			
//		}finally{
//			if(dao != null) dao.close();
//		}
//		
//		//setSubAtividadesCoordenador(getDAO(AtividadeExtensaoDao.class).findSubAtividadeCursoEventoByCoordenador(getUsuarioLogado().getServidor()));
//		
//		return forward(getDirBase() + LISTA_ACOES);
//	}
//
//	
//	///////////////////////////////// Atividades /////////////////////////////////////
//	
//	/**
//	 * Lista as inscri��es criadas para uma a��o de extens�o.
//	 * 
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/lista_acoes.jsp
//	 * 
//	 * @return
//	 * @throws ArqException 
//	 */
//	public String listarInscricoes() throws ArqException {
//		Integer id = getParameterInt("idAtividade", 0);
//		if (id > 0) {
//			return listarInscricoes(id);
//		}else {
//			addMensagemErro("A��o selecionada n�o � uma a��o v�lida.");
//			return null;
//		}
//	}
//	
//	
//	/**
//	 * Lista as inscri��es criadas para uma a��o de extens�o.
//	 * 
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/lista_acoes.jsp
//	 * 
//	 * @return
//	 * @throws ArqException 
//	 */
//	public String listarInscricoes(int idAtividade) throws ArqException {
//		
//		atividadeSelecionada = getGenericDAO().findByPrimaryKey(idAtividade, AtividadeExtensao.class, "id", "sequencia", "tipoAtividadeExtensao.id", 
//				"projeto.ano", "projeto.id", "projeto.titulo", "projeto.dataInicio", "projeto.dataFim", "cursoEventoExtensao.numeroVagas");
//		
//		atividadeSelecionada.getCursoEventoExtensao().setNumeroInscritos(getParameterInt("numeroInscritos", 0));
//		carregarInscricoes();
//		prepareMovimento(SigaaListaComando.EXCLUIR_INSCRICAO_ACAO_EXTENSAO);
//		
//		genrenciandoInscricoesSubAtividades = false;
//		
//		return  telaGerenciaInscricoesAtividade();
//		
//	}
//	
//	/**
//	 * Redireciona para a pagina que gerencia as inscri��es em atividades.
//	 *  
//	 *  <br/>
//	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *    <li>sigaa.war/extensao/InscricaoOnline/lista.jsp</li>
//	 *   </ul>
//	 *
//	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
//	 *
//	 * @return
//	 */
//	public String telaGerenciaInscricoesAtividade(){
//		return forward(getDirBase() + LISTA);
//	}
//	
//	///////////////////////////////////////////////////////////////////////////////////////
//	
//	
//	//////////////////////////////    Mini Atividades    //////////////////////////////////
//	
//	/**
//	 * Lista as inscri��es criadas para uma a��o de extens�o.
//	 * 
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/lista_acoes.jsp
//	 * 
//	 * @return
//	 * @throws ArqException 
//	 */
//	public String listarInscricoesMiniAtividade() throws ArqException {
//		int idSubAtividade = getParameterInt("idSubAtividade");
//		if (idSubAtividade > 0) {
//			return listarInscricoesMiniAtividade(idSubAtividade);
//		}else {
//			addMensagemErro("A��o selecionada n�o � uma a��o v�lida.");
//			return null;
//		}
//	}
//	
//	
//	/** M�todo para ser chamado de outro MBean, faz a mesma coisa do m�todo acima. */
//	public String listarInscricoesMiniAtividade(int idSubAtividade) throws ArqException {
//		subAtividadeSelecionada = getGenericDAO().findByPrimaryKey(idSubAtividade, SubAtividadeExtensao.class);
//		inscricoesSubAtividades = getDAO(InscricaoAtividadeExtensaoDao.class).findAllBySubAtividade(subAtividadeSelecionada.getId());
//		prepareMovimento(SigaaListaComando.EXCLUIR_INSCRICAO_ACAO_EXTENSAO);
//		
//		/* A vari�vel transiente que guarda o n�mero de inscritos fica na sub atividade, 
//		 * diferentemente da ativida que fica na entidade CursoEvento                    */
//		subAtividadeSelecionada.setNumeroInscritos(getParameterInt("numeroInscritos", 0));
//		
//		genrenciandoInscricoesSubAtividades = true;
//		
//		return telaGerenciaInscricoesMiniAtividade();
//	}
//	
//	
//	/**
//	 * Redireciona para a pagina que gerencia as inscri��es em sub atividades.
//	 *  
//	 *  <br/>
//	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *    <li>sigaa.war/extensao/InscricaoOnline/lista_sub_atividades.jsp</li>
//	 *   </ul>
//	 *
//	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
//	 *
//	 * @return
//	 */
//	public String telaGerenciaInscricoesMiniAtividade(){
//		return forward(getDirBase() + LISTA_SUB_ATIVIDADE);
//	}
//	
//	
//	
//	//////////////////////////////////////////////////////////////////////
//	
//	
//	
//	/**
//	 * 
//	 * <p>Diz para qual tela o sistema deve redirecionar.</p> 
//	 * 
//	 * <p>Muitas das tela que s�o chamadas a partir da tela de gerenciamento de inscri��es volta para o in�cio do sistema.</p>
//	 * <p>Ver lista de JSP de onde esse m�todo � chamado.</p>
//	 *  
//	 *  <br/>
//	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *    <li>/sigaa.war/extensao/InscricaoOnline/pendentes.jsp</li>
//	 *    <li>/sigaa.war/extensao/InscricaoOnline/inscritos.jsp</li>
//	 *    <li>/sigaa.war/extensao/InscricaoOnline/form_inscricao_sub_atividades.jsp</li>
//	 *    <li>/sigaa.war/extensao/InscricaoOnline/suspender_inscricao.jsp</li>
//	 *    <li>/sigaa.war/extensao/InscricaoOnline/gerenciar_participantes.jsp</li>
//	 *    
//	 *   </ul>
//	 *
//	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
//	 *
//	 * @return
//	 */
//	public String voltaTelaGerenciaInscricoes(){
//		if(genrenciandoInscricoesSubAtividades)
//			return telaGerenciaInscricoesMiniAtividade();
//		else
//			return telaGerenciaInscricoesAtividade();
//	}
//	
//	
//	
//	
//	/**
//	 * Inicia p�gina para acompahamento dos inscritos.
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/portais/docente/menu_docente.jsp
//	 * @return
//	 * @throws DAOException
//	 */
//	public String iniciarAcompanhamentoInscritos() throws DAOException {		
//		clear();
//		return carregarInscricoesParticipantesInscritos();
//	}
//	
//	/**
//	 * Redireciona para lista de isncritos de uma a��o.
//	 * @return
//	 * @throws DAOException
//	 */
//	private String carregarInscricoesParticipantesInscritos() throws DAOException {		
//		inscricoesAtividades = getDAO(InscricaoAtividadeExtensaoDao.class).findInscricoesSelecaoParticipantes(getServidorUsuario().getId());
//		return forward(getDirBase() + LISTA_INSCRICOES);
//	}
//	
//	/**
//	 * Carrega inscritos.
//	 * @throws DAOException
//	 */
//	private void carregarInscricoes() throws DAOException {
//		inscricoesAtividades = getDAO(InscricaoAtividadeExtensaoDao.class).findAllByAtividade(atividadeSelecionada.getId());
//	}
//	
//	/**
//	 * 
//	 * Chamado por:
//	 * sigaa.war/extensao/InscricaoOnline/lista_acoes.jsp
//	 * 
//	 * @return
//	 * @throws DAOException
//	 */
//	public String exibirRelatorioInscritos() throws DAOException {		
//		atividade = getGenericDAO().findByPrimaryKey(getParameterInt("idAtividade"), AtividadeExtensao.class);
//		iniciarVisualizarParticipantesDaAcao(atividade);
//		return forward(getDirBase() + RELATORIO_INSCRITOS);
//	}
//
//	/**
//	 * 
//	 * Chamado por:
//	 * sigaa.war/extensao/InscricaoOnline/lista_acoes.jsp
//	 * 
//	 * @return
//	 * @throws DAOException
//	 */
//	public String exibirRelatorioInscritosSubAtividade() throws DAOException {		
//		subAtividade = getGenericDAO().findByPrimaryKey(getParameterInt("idSubAtividade"), SubAtividadeExtensao.class);
//		iniciarVisualizarParticipantesDaSubAtividade(subAtividade);
//		return forward(getDirBase() + RELATORIO_INSCRITOS_SUB_ATIVIDADES);
//	}
//	
//	/**
//	 * Exibe lista de inscritos possibilidando reenvio de senha.
//	 * 
//	 * Chamado por:
//	 * sigaa.war/extensao/InscricaoOnline/consulta_inscricoes.jsp
//	 * 
//	 * @return
//	 * @throws DAOException
//	 */
//	public String listarInscritos() throws DAOException {		
//		obj = getGenericDAO().findByPrimaryKey(getParameterInt("idInscricao"), InscricaoAtividade.class);
//		iniciarVisualizarParticipantesDaInscricao(obj);
//		return forward(getDirBase() + INSCRITOS);
//	}
//	
//
//	
//	/**
//	 * Exibe lista de inscritos possibilidando reenvio de senha.
//	 * 
//	 * Chamado por:
//	 * sigaa.war/extensao/InscricaoOnline/consulta_inscricoes.jsp
//	 * 
//	 * @return
//	 * @throws DAOException
//	 */
//	public String listarPendentes() throws DAOException {		
//		obj = getGenericDAO().findByPrimaryKey(getParameterInt("idInscricao"), InscricaoAtividade.class);
//		inscricoesNaoConfirmadas = getDAO(InscricaoAtividadeParticipanteDao.class).findParticipantesByInscricaoStatus(obj.getId(), StatusInscricaoParticipante.INSCRITO);
//		return forward(getDirBase() + PENDENTES);
//	}
//
//	/**
//	 * Listar inscri��es da atividade selecionada
//	 * <br />
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>/sigaa/extensao/InscricaoOnline/lista_acoes.jsp</li>
//	 * </ul>
//	 * 
//	 * @return
//	 * @throws DAOException
//	 */
//	public String listarInscricoesAtividade() throws DAOException {
//		inscricoesSubAtividades = new ArrayList<InscricaoAtividade>();
//		inscricoesAtividades = getDAO(InscricaoAtividadeExtensaoDao.class).inscricaoAtividadeByAtividade(getParameterInt("idAtividade", 0));
//		return forward(getDirBase() + LISTA_INSCRICOES);
//	}
//
//
//	/**
//	 * Listar inscri��es da sub-atividade selecionada
//	 * <br />
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>/sigaa/extensao/InscricaoOnline/lista_acoes.jsp</li>
//	 * </ul>
//	 * 
//	 * @Deprecated usar listarInscricaoMiniAtividades(); 
//	 * @return
//	 * @throws DAOException
//	 */
//	@Deprecated
//	public String listarInscricoesSubAtividade() throws DAOException {
//		inscricoesAtividades = new ArrayList<InscricaoAtividade>();
//		//SubAtividadeExtensao subAtividade = getGenericDAO().findByPrimaryKey(getParameterInt("idSubAtividade", 0),SubAtividadeExtensao.class, "atividade");
//		//inscricoesAtividades = getDAO(InscricaoAtividadeExtensaoDao.class).inscricaoAtividadeByAtividade(subAtividade.getAtividade().getId());
//		inscricoesSubAtividades = getDAO(InscricaoAtividadeExtensaoDao.class).inscricaoAtividadeBySubAtividade(getParameterInt("idSubAtividade", 0));
//		return forward(getDirBase() + LISTA_INSCRICOES_SUB_ATIVIDADE);
//	}
//	
////	/**
////	 * <p>Listar inscri��es de todas as atividades do coordenador.</p>
////	 * 
////	 * <p>
////	 * <strong>Esse m�todo � apenas um atalho para ir direto a todas as incri��es cujo o usu�rio logado � coordenador.
////	 * O usu�rio pode tamb�m realizar essa a��o atravez do m�todo iniciarProcesso() ->  listarInscricoesAtividade() 
////	 * </strong>
////	 * <p>
////	 * 
////	 * 
////	 * <br />
////	 * M�todo chamado pela(s) seguinte(s) JSP(s):
////	 * <ul>
////	 * 		<li>/sigaa/portais/docente/menu_docente.jsp</li>
////	 * </ul>
////	 * 
////	 * @return
////	 * @see {@link this#iniciarProcesso()}
////	 * @see {@link this#listarInscricoesAtividade()}
////	 * @throws DAOException
////	 */
////	@Deprecated
////	public String listarInscricoesAtividadeByCoordenador() throws DAOException {
////		InscricaoAtividadeExtensaoDao dao = null;
////		
////		try{
////			dao = getDAO(InscricaoAtividadeExtensaoDao.class);
////			inscricoesAtividades = dao.findAllInscricaoAtividadeByCoordenador(getUsuarioLogado().getServidor() );
////			inscricoesSubAtividades = dao.findAllInscricaoSubAtividadeByCoordenador(getUsuarioLogado().getServidor() );
////		}finally{
////			if(dao != null) dao.close();
////		}
////		
////		
////		return forward(getDirBase() + LISTA_INSCRICOES);
////	}
//	
//	
//	
//	
//	/**
//	 * Realiza verifica��o e configura��o antes do cadastrar.
//	 * 
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/lista_acoes.jsp
//	 */
//	@Override
//	public String preCadastrar() throws ArqException {		
//		int idAtividade = getParameterInt("idAtividade");
//		obj = getDAO(InscricaoAtividadeExtensaoDao.class).findInscricaoAbertaByAtividade(idAtividade);
//		periodoAbertoInicio= null;
//		periodoAbertoFim = null;
//		if (obj != null) {
//			periodoAbertoInicio = obj.getPeriodoInicio();
//			periodoAbertoFim = obj.getPeriodoFim();
//			addMensagem(MensagensExtensao.PERIODO_INSCRICAO_ATIVO, obj.getCodigo(), 
//					Formatador.getInstance().formatarData(periodoAbertoInicio), 
//					Formatador.getInstance().formatarData(periodoAbertoFim));
//			return null;
//		}
//		
//		atividade = getGenericDAO().findByPrimaryKey(idAtividade, AtividadeExtensao.class, "id", "sequencia", "tipoAtividadeExtensao.id", 
//				"projeto.ano", "projeto.id", "projeto.titulo", "projeto.dataInicio", "projeto.dataFim", "cursoEventoExtensao.numeroVagas");
//		obj = new InscricaoAtividade(atividade);
//		carregarQuestionarios();
//		prepareMovimento(SigaaListaComando.CADASTRAR_INSCRICAO_ACAO_EXTENSAO);
//		setOperacaoAtiva(SigaaListaComando.CADASTRAR_INSCRICAO_ACAO_EXTENSAO.getId());
//		texto = "Criar";
//		confirmButton = "Cadastrar";
//		return forward(getDirBase() + FORM_INSCRICAO_ATIVIDADE);
//	}
//	
//	/**
//	 * Realiza verifica��o e configura��o antes do cadastrar.
//	 * 
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/lista_acoes.jsp
//	 */	
//	public String preCadastrarInscricaoMiniAtividade() throws ArqException {		
//		
//		int idSubAtividade = getParameterInt("idSubAtividade");
//		
//		obj = getDAO(InscricaoAtividadeExtensaoDao.class).findInscricaoAbertaBySubAtividade(idSubAtividade);
//		if (obj != null) {
//			periodoAbertoInicio = obj.getPeriodoInicio();
//			periodoAbertoFim = obj.getPeriodoFim();			
//			addMensagemWarning("Essa mini a��o possui a inscri��o " + obj.getCodigo() + " com per�odo aberto de " + Formatador.getInstance().formatarData(periodoAbertoInicio) + " � " + Formatador.getInstance().formatarData(periodoAbertoFim));			
//			return null;
//		}
//		
//		SubAtividadeExtensao subAtividade = getGenericDAO().findByPrimaryKey(idSubAtividade, SubAtividadeExtensao.class);
//		atividade = subAtividade.getAtividade();
//		obj = new InscricaoAtividade(atividade);
//		obj.setSubAtividade(subAtividade);
//		prepareMovimento(SigaaListaComando.CADASTRAR_INSCRICAO_SUB_ATIVIDADE);
//		setOperacaoAtiva(SigaaListaComando.CADASTRAR_INSCRICAO_SUB_ATIVIDADE.getId());
//		texto = "Criar";
//		confirmButton = "Cadastrar";
//		return forward(getDirBase() + FORM_INSCRICAO_SUB_ATIVIDADE);
//	}
//	
//	
//	/**
//	 * 
//	 * Utilizado para mostrar os tipos de curso ativos
//	 * 
//	 * @return
//	 * @throws DAOException
//	 */
//	public Collection<SelectItem> getAllSubAtividades() throws DAOException {
//		Collection<SubAtividadeExtensao> subAtividades = getGenericDAO().findByExactField(SubAtividadeExtensao.class, "atividade.id", atividade.getId());
//		return toSelectItems(subAtividades, "id", "titulo");
//	}
//	
//	
//	/**
//	 * 
//	 * Utilizado para mostrar os m�todos de preenchimento das vagas nos mini cursos/eventos.
//	 * 
//	 * @return
//	 * @throws DAOException
//	 */
//	public Collection<SelectItem> getAllMetodosPreenchimento() throws DAOException {		
//		
//		SelectItem comConfirmacao = new SelectItem(InscricaoAtividade.COM_CONFIRMACAO, "Com Confirma��o");
//		//SelectItem automatico = new SelectItem(InscricaoAtividade.PREENCHIMENTO_AUTOMATICO, "Preenchimento Autom�tico");
//		
//		Collection<SelectItem> metodos = new ArrayList<SelectItem>();
//		
//		metodos.add(comConfirmacao);
//		//metodos.add(automatico);	
//		
//		return metodos;
//	}
//	
//	/**
//	 * Respons�vel por alterar a quantidade geral de vagas para um curso ou evento
//	 * de extens�o.
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/form.jsp
//	 * @throws ArqException 
//	 * 
//	 */	
//	public String alterarVagas() throws ArqException {		
//		if( novasVagas == null || novasVagas <= 0){
//			addMensagemErro("Informe o n�mero de vagas.");
//			return iniciarProcesso();
//		}
//
//		InscricaoAtividadeExtensaoDao dao = getDAO(InscricaoAtividadeExtensaoDao.class);
//		obj.setAtividade(new AtividadeExtensao());
//		obj.setAtividade(dao.findByPrimaryKey(idAtividade, AtividadeExtensao.class));
//
//		Integer vagasReservadas = (dao.totalVagasInscricoes(obj.getAtividade().getId()));
//		if (novasVagas < vagasReservadas) {
//			addMensagemErro("O n�mero de vagas n�o pode ser inferior a quantidade de inscri��es reservadas.");
//			return iniciarProcesso();
//		}
//		obj.getAtividade().getCursoEventoExtensao().setNumeroVagas(novasVagas);
//		prepareMovimento(SigaaListaComando.ALTERAR_VAGAS_CURSO_EVENTO_EXTENSAO);
//		MovimentoCadastro mov = new MovimentoCadastro();
//		mov.setObjMovimentado(obj);
//		mov.setCodMovimento(SigaaListaComando.ALTERAR_VAGAS_CURSO_EVENTO_EXTENSAO);
//
//		try {
//			execute(mov);
//			addMensagem(OPERACAO_SUCESSO);
//			atividade = new AtividadeExtensao();
//		} catch (NegocioException e) {
//			addMensagens(e.getListaMensagens());
//		} catch (Exception e) {
//			notifyError(e);
//		}
//
//		return iniciarProcesso();		
//	}
//
//	/**
//	 * Respons�vel por alterar a quantidade geral de vagas para uma mini-atividade
//	 * de extens�o.
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/form.jsp
//	 * @throws ArqException 
//	 * 
//	 */	
//	public String alterarVagasMiniAtividade() throws ArqException {		
//		if( novasVagasMiniAtividade == null || novasVagasMiniAtividade <= 0){
//			addMensagemErro("Informe o n�mero de vagas.");
//			return iniciarProcesso();
//		}
//
//		SubAtividadeExtensao subAtividade = getGenericDAO().findByPrimaryKey(idSubAtividade, SubAtividadeExtensao.class);
//		atividade = subAtividade.getAtividade();
//		obj = new InscricaoAtividade(atividade);
//		obj.setSubAtividade(subAtividade);
//		
//		InscricaoAtividadeExtensaoDao dao = getDAO(InscricaoAtividadeExtensaoDao.class);
//		Integer vagasReservadas = (dao.somaVagasInscricoesSubAtividade(obj.getSubAtividade().getId()));
//		
//		if (novasVagasMiniAtividade < vagasReservadas) {
//			addMensagemErro("O n�mero de vagas n�o pode ser inferior a quantidade de inscri��es reservadas.");
//			return iniciarProcesso();
//		}
//		obj.getSubAtividade().setNumeroVagas(novasVagasMiniAtividade);
//		prepareMovimento(SigaaListaComando.ALTERAR_VAGAS_MINI_ATIVIDADE);
//		MovimentoCadastro mov = new MovimentoCadastro();
//		mov.setObjMovimentado(obj);
//		mov.setCodMovimento(SigaaListaComando.ALTERAR_VAGAS_MINI_ATIVIDADE);
//
//		try {
//			execute(mov);
//			addMensagem(OPERACAO_SUCESSO);
//			atividade = new AtividadeExtensao();
//		} catch (NegocioException e) {
//			addMensagens(e.getListaMensagens());
//		} catch (Exception e) {
//			notifyError(e);
//		}
//
//		return iniciarProcesso();		
//	}
//	
//	/**
//	 * Cria um per�odo de inscri��o na �rea p�blica.
//	 * 
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/form.jsp
//	 */
//	@Override
//	public String cadastrar() throws ArqException {
//		InscricaoAtividadeExtensaoDao dao = getDAO(InscricaoAtividadeExtensaoDao.class);
//		
//		// Realiza a associa��o entre o question�rio e a a��o de inscri��o //
//		if(obj.getQuestionario()!= null && obj.getQuestionario().getId() > 0 ) {
//			obj.setQuestionario(getGenericDAO().findByPrimaryKey(obj.getQuestionario().getId(), Questionario.class));
//		}else{ // Se o usu�rio retirou o question�rio, retira a associa��o
//			obj.setQuestionario(null);
//		}
//		
//		if (confirmButton.equalsIgnoreCase("alterar")) {
//			return alterar();
//		}		
//		
//		beforeCadastrarAndValidate();
//		ListaMensagens mensagens = new ListaMensagens();
//		mensagens.addAll(obj.validate());
//		
//		if (periodoAbertoInicio != null && obj.getPeriodoInicio() != null && obj.getPeriodoFim() != null) {
//			if (estaDentroPeriodoAberto(obj.getPeriodoInicio()) || estaDentroPeriodoAberto(obj.getPeriodoFim())) {
//				mensagens.addErro("Per�odo da inscri��o n�o pode estar dentro do per�odo da inscri��o aberta.");
//				mensagens.addWarning("Existe uma Inscri��o com per�odo aberto de " 
//					+ Formatador.getInstance().formatarData(periodoAbertoInicio) + " � " 
//					+ Formatador.getInstance().formatarData(periodoAbertoFim) + ".");
//			}
//		}
//		
//		if (!mensagens.isEmpty()) {
//			addMensagens(mensagens);
//			return null;
//		}
//		
//		Integer vagasRestantes = (obj.getAtividade().getCursoEventoExtensao().getNumeroVagas() - dao.totalVagasInscricoes(obj.getAtividade().getId()));
//		if(obj.getQuantidadeVagas() > vagasRestantes ) {
//			mensagens.addErro("N�o � poss�vel abrir "+obj.getQuantidadeVagas()+" vaga(s). O valor m�ximo permitido de vagas � "+vagasRestantes+".  ");
//		}
//		
//		if (!mensagens.isEmpty()) {
//			addMensagens(mensagens);
//			return null;
//		}
//		
//		try {
//			MovimentoCadastro mov = new MovimentoCadastro();
//			mov.setObjMovimentado(obj);
//			mov.setCodMovimento(SigaaListaComando.CADASTRAR_INSCRICAO_ACAO_EXTENSAO);
//			execute(mov);
//			addMensagem(OPERACAO_SUCESSO);
//			clear();			
//		} catch (NegocioException e) {
//			addMensagens(e.getListaMensagens());
//			return null;
//		}finally{
//			dao.close();
//		}
//		
//		return iniciarProcesso();
//	}
//	
//	
//	
//	
//	/**
//	 * Cria um per�odo de inscri��o na �rea p�blica.
//	 * 
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/form.jsp
//	 */	
//	public String cadastrarInscricaoSubAtividade() throws ArqException {
//		
//		if (confirmButton.equalsIgnoreCase("alterar")) {
//			alterarSubAtividades();
//			return forward(getDirBase() + LISTA_SUB_ATIVIDADE);
//		}	
//		InscricaoAtividadeExtensaoDao dao = getDAO(InscricaoAtividadeExtensaoDao.class);
//		obj.setSubAtividade(dao.findByPrimaryKey(obj.getSubAtividade().getId(),SubAtividadeExtensao.class));	
//
//
//		//Verifica se tem alguma inscri��o aberta.
//		if( ! ValidatorUtil.isEmpty(obj.getSubAtividade())) {
//			InscricaoAtividade inscricao = dao.findInscricaoAbertaBySubAtividade(obj.getSubAtividade().getId());
//			if (inscricao != null) {				
//				periodoAbertoInicio = inscricao.getPeriodoInicio();
//				periodoAbertoFim = inscricao.getPeriodoFim();
//				addMensagemWarning("Essa mini a��o possui a inscri��o " + obj.getCodigo() + " com per�odo aberto de " + Formatador.getInstance().formatarData(periodoAbertoInicio) + " � " + Formatador.getInstance().formatarData(periodoAbertoFim));
//				return null;
//			}
//		} else {
//			obj.setSubAtividade(new SubAtividadeExtensao());
//		}
//		
//		
//		
//		
//		
//		beforeCadastrarAndValidate();
//		ListaMensagens mensagens = new ListaMensagens();
//		mensagens.addAll(obj.validate());
//		
//		
//		
//		if (!mensagens.isEmpty()) {
//			addMensagens(mensagens);
//			return null;
//		}
//		
//		Integer vagasRestantes = (obj.getSubAtividade().getNumeroVagas() - dao.totalVagasInscricoesSubAtividade(obj.getAtividade().getId()));
//		if(obj.getQuantidadeVagas() > vagasRestantes ) {
//			mensagens.addErro("N�o � poss�vel abrir "+obj.getQuantidadeVagas()+" vaga(s). O valor m�ximo permitido de vagas � "+vagasRestantes+".  ");
//		}
//		
//		if (!mensagens.isEmpty()) {
//			addMensagens(mensagens);
//			return null;
//		}
//		
//		try {
//			MovimentoCadastro mov = new MovimentoCadastro();
//			mov.setObjMovimentado(obj);
//			mov.setCodMovimento(SigaaListaComando.CADASTRAR_INSCRICAO_SUB_ATIVIDADE);
//			execute(mov);
//			addMensagem(OPERACAO_SUCESSO);
//			clear();			
//		} catch (NegocioException e) {
//			addMensagens(e.getListaMensagens());
//			return null;
//		}finally{
//			dao.close();
//		}
//		
//		return iniciarProcesso();
//	}
//	
//	
//	/**
//	 * Realiza configura��o antes do alterar.
//	 * 
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/lista.jsp
//	 */
//	public String preAlterar() throws ArqException {
//		obj = getGenericDAO().findByPrimaryKey(getParameterInt("id", 0), InscricaoAtividade.class);
//		if (obj == null) {
//			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
//			return forward(getDirBase() + LISTA_SUB_ATIVIDADE);
//		}
//		prepareMovimento(ArqListaComando.ALTERAR);
//		removeOperacaoAtiva();
//		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
//		texto = "Alterar";
//		confirmButton = "Alterar";
//		numeroInscAtual = obj.getQuantidadeVagas();
//		carregarQuestionarios();
//		
//		return forward(getDirBase() + FORM_INSCRICAO_ATIVIDADE);
//	}
//	
//	/**
//	 * Realiza configura��o antes do alterar.
//	 * 
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/lista.jsp
//	 */
//	public String preAlterarSubAtividades() throws ArqException {
//		
//		GenericDAO dao = null;  
//		try {
//			dao = getGenericDAO();
//			obj = dao.findByPrimaryKey(getParameterInt("id", 0), InscricaoAtividade.class);
//			if (obj == null) {
//				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
//				return null;
//			}
//			obj.setSubAtividade(dao.findByPrimaryKey(obj.getSubAtividade().getId(),SubAtividadeExtensao.class));		
//			obj.setAtividade(obj.getSubAtividade().getAtividade());
//			
//			prepareMovimento(ArqListaComando.ALTERAR);
//			removeOperacaoAtiva();
//			setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
//			texto = "Alterar";
//			confirmButton = "Alterar";
//			numeroInscAtual = obj.getQuantidadeVagas();
//			
//			return forward(getDirBase() + FORM_INSCRICAO_SUB_ATIVIDADE);
//		} finally {
//			if (dao != null)
//				dao.close();
//		}
//	}
//	
//	/**
//	 * Altera os dados referentes a uma inscri��o.
//	 * 
//	 * M�todo n�o invocado por JSP�s
//	 * 
//	 * @return
//	 * @throws ArqException
//	 */
//	private String alterar() throws ArqException {
//		InscricaoAtividadeExtensaoDao dao = getDAO(InscricaoAtividadeExtensaoDao.class);
//		erros.getMensagens().clear();
//		erros = new ListaMensagens();
//		obj.setInscricoesParticipantes(carregarParticipantes(obj, null)); // Utilizado na valida��o da inscri��o
//		erros.addAll(obj.validate());
//		
//		obj.setAtividade(dao.findByPrimaryKey(obj.getAtividade().getId(), AtividadeExtensao.class));
//		Integer vagasRestantes = numeroInscAtual + (obj.getAtividade().getCursoEventoExtensao().getNumeroVagas() - dao.totalVagasInscricoes(obj.getAtividade().getId()));
//		if(obj.getQuantidadeVagas() > vagasRestantes ){
//			erros.addErro("N�o � poss�vel abrir "+obj.getQuantidadeVagas()+" vaga(s). O valor m�ximo permitido de vagas � "+vagasRestantes+".  ");
//		}
//
//		if (!erros.isEmpty()) {
//			addMensagens(erros);
//			return null;
//		}
//		
//		try {
//			MovimentoCadastro mov = new MovimentoCadastro();
//			mov.setObjMovimentado(obj);
//			mov.setCodMovimento(ArqListaComando.ALTERAR);
//			execute(mov);
//			addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Per�odo de Inscri��o");
//						
//			carregarInscricoes();
//			prepareMovimento(SigaaListaComando.EXCLUIR_INSCRICAO_ACAO_EXTENSAO);
//			return forward(getDirBase() + LISTA);
//
//		} catch (NegocioException e) {
//			addMensagens(e.getListaMensagens());
//		}
//		return null;
//	}
//	
//	/**
//	 * Altera os dados referentes a uma inscri��o.
//	 * 
//	 * M�todo n�o invocado por JSP�s
//	 * 
//	 * @return
//	 * @throws ArqException
//	 */
//	private String alterarSubAtividades() throws ArqException {
//		InscricaoAtividadeExtensaoDao dao = getDAO(InscricaoAtividadeExtensaoDao.class);
//		erros.getMensagens().clear();
//		erros = new ListaMensagens();
//		obj.setInscricoesParticipantes(carregarParticipantes(obj, null)); // Utilizado na valida��o da inscri��o
//		erros.addAll(obj.validate());
//		
//		obj.setAtividade(dao.findByPrimaryKey(obj.getAtividade().getId(), AtividadeExtensao.class));
//		Integer vagasRestantes = numeroInscAtual + (obj.getSubAtividade().getNumeroVagas() - dao.totalVagasInscricoesSubAtividade(obj.getAtividade().getId()));
//		if(obj.getQuantidadeVagas() > vagasRestantes ){
//			erros.addErro("N�o � poss�vel abrir "+obj.getQuantidadeVagas()+" vaga(s). O valor m�ximo permitido de vagas � "+vagasRestantes+".  ");
//		}
//
//		if (!erros.isEmpty()) {
//			addMensagens(erros);
//			return null;
//		}
//		
//		try {
//			MovimentoCadastro mov = new MovimentoCadastro();
//			mov.setObjMovimentado(obj);
//			mov.setCodMovimento(ArqListaComando.ALTERAR);
//			execute(mov);
//			addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Per�odo de Inscri��o");
//						
//			inscricoesSubAtividades = getDAO(InscricaoAtividadeExtensaoDao.class).findAllBySubAtividade(obj.getSubAtividade().getId());
//			prepareMovimento(SigaaListaComando.EXCLUIR_INSCRICAO_ACAO_EXTENSAO);
//			return forward(getDirBase() + LISTA_SUB_ATIVIDADE);
//
//		} catch (NegocioException e) {
//			addMensagens(e.getListaMensagens());
//		}
//		return null;
//	}
//	
//	/**
//	 * 
//	 * Chamado por:
//	 * sigaa.war/extensao/InscricaoOnline/lista.jsp
//	 * 
//	 * @return
//	 * @throws ArqException
//	 */
//	public String excluir() throws ArqException {
//		
//		obj = getDAO(InscricaoAtividadeExtensaoDao.class).findByPrimaryKey(getParameterInt("id"));
//		if (obj == null) {
//			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
//			return null;
//		}
//		MovimentoCadastro mov = new MovimentoCadastro();
//		mov.setObjMovimentado(obj);
//		mov.setCodMovimento(SigaaListaComando.EXCLUIR_INSCRICAO_ACAO_EXTENSAO);
//		
//		try {
//			execute(mov);
//			addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Inscri��o");
//			
//		} catch (NegocioException e) {
//			addMensagens(e.getListaMensagens());
//			return null;
//		}
//		
//		carregarInscricoes();
//		prepareMovimento(SigaaListaComando.EXCLUIR_INSCRICAO_ACAO_EXTENSAO);
//		return forward(getDirBase() + LISTA);
//
//	}
//	
//	/**
//	 * 
//	 * Chamado por:
//	 * sigaa.war/extensao/InscricaoOnline/lista.jsp
//	 * 
//	 * @return
//	 * @throws ArqException
//	 */
//	public String excluirSubAtividades() throws ArqException {
//		
//		obj = getDAO(InscricaoAtividadeExtensaoDao.class).findByPrimaryKey(getParameterInt("id"),InscricaoAtividade.class);
//		if (obj == null) {
//			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
//			return null;
//		}
//		MovimentoCadastro mov = new MovimentoCadastro();
//		mov.setObjMovimentado(obj);
//		mov.setCodMovimento(SigaaListaComando.EXCLUIR_INSCRICAO_ACAO_EXTENSAO);
//		
//		try {
//			execute(mov);
//			addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Inscri��o");
//			
//		} catch (NegocioException e) {
//			addMensagens(e.getListaMensagens());
//			return null;
//		}
//		
//		inscricoesSubAtividades = getDAO(InscricaoAtividadeExtensaoDao.class).findAllBySubAtividade(obj.getSubAtividade().getId());
//		prepareMovimento(SigaaListaComando.EXCLUIR_INSCRICAO_ACAO_EXTENSAO);
//		return forward(getDirBase() + LISTA_SUB_ATIVIDADE);
//
//	}
//	
//	/**
//	 * Realiza verifica��o e configura��o antes do suspender.
//	 * 
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/lista.jsp
//	 */
//	public String preSuspender() {
//		
//		try {
//			obj = getDAO(InscricaoAtividadeExtensaoDao.class).findByPrimaryKey(getParameterInt("id"),InscricaoAtividade.class);
//			if (obj == null) {
//				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
//				return null;
//			}
//			obj.setParticipantesInscritos(carregarParticipantes(obj, StatusInscricaoParticipante.CONFIRMADO));
//			prepareMovimento(SigaaListaComando.SUSPENDER_INSCRICAO_ACAO_EXTENSAO);
//		} catch (ArqException e) {
//			notifyError(e);
//			e.printStackTrace();
//			addMensagemErroPadrao();
//		}
//		if (!obj.getParticipantesInscritos().isEmpty())
//			addMensagem(MensagensExtensao.REMOCAO_INSCRICAO_MEMBROS_INSCRITOS);
//		disableMotivo = obj.getParticipantesInscritos().isEmpty() ? true : false;
//		removeOperacaoAtiva();
//		setOperacaoAtiva(SigaaListaComando.SUSPENDER_INSCRICAO_ACAO_EXTENSAO.getId());
//		return forward(getDirBase() + "/suspender_inscricao.jsp");
//	}
//	
//	/**
//	 * Suspende uma inscri��o de uma a��o de extens�o.
//	 * 
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/suspender_inscricao.jsp
//	 */
//	public String suspender() throws ArqException {
//		
//		if (!disableMotivo && "".equals(obj.getMotivoCancelamento())) {
//			addMensagem(MensagensExtensao.INFORMAR_MOTIVOS_SUSPENSAO_PERIODO);
//			return null;
//		}
//		MovimentoCadastro mov = new MovimentoCadastro();
//		mov.setObjMovimentado(obj);
//		mov.setCodMovimento(SigaaListaComando.SUSPENDER_INSCRICAO_ACAO_EXTENSAO);
//		
//		try {
//			execute(mov);
//			addMensagem(OPERACAO_SUCESSO);
//			
//		} catch (NegocioException e) {
//			addMensagens(e.getListaMensagens());
//			return null;
//		}
//		inscricoesAtividades.remove(obj);
//		
//		if (inscricoesAtividades.isEmpty()) {
//			return iniciarProcesso();
//		}else {
//			carregarInscricoes();
//			prepareMovimento(SigaaListaComando.EXCLUIR_INSCRICAO_ACAO_EXTENSAO);
//			return forward(getDirBase() + LISTA);
//		}
//	}
//
//	/**
//	 * Carrega todos as inscri��es confirmadas de participantes para sele��o pelo coordenador.
//	 * 
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/lista.jsp
//	 * 
//	 * @return
//	 * @throws ArqException
//	 */
//	public String listarParticipantesInscritos() throws ArqException  {
//		
//		return listarParticipantesInscritos(getParameterInt("id"));
//	}
//	
//	
//	/**
//	 * Carrega todos as inscri��es confirmadas de participantes para sele��o pelo coordenador.
//	 * 
//	 * M�todo n�o chamado por jsp.
//	 * 
//	 * @return
//	 * @throws ArqException
//	 */
//	public String listarParticipantesInscritos(int idAtividade) throws ArqException  {
//		
//		obj = getGenericDAO().findByPrimaryKey(idAtividade, InscricaoAtividade.class);
//		CursoEventoExtensao cursoEvento = null;
//		if(!ValidatorUtil.isEmpty(obj.getAtividade())) {
//			cursoEvento = obj.getAtividade().getCursoEventoExtensao();
//		} else {
//			cursoEvento = obj.getSubAtividade().getAtividade().getCursoEventoExtensao();
//		}		
//		
//		// para o usu�rio validar o pagamento da GRU tem que saber se o curso � paga ou n�o //
//		obj.setExigeCombrancaTaxa(cursoEvento.isCobrancaTaxaMatricula()); 
//		
//		obj.setParticipantesInscritos(carregarParticipantes(obj, StatusInscricaoParticipante.CONFIRMADO));
//		obj.setInscricoesParticipantes(carregarParticipantes(obj, StatusInscricaoParticipante.APROVADO));
//		prepareMovimento(SigaaListaComando.APROVAR_INSCRICAO_PARTICIPANTE_EXTENSAO);
//		prepareMovimento(SigaaListaComando.RECUSAR_INSCRICAO_PARTICIPANTE_EXTENSAO);
//		removeOperacaoAtiva();
//		setOperacaoAtiva(1);
//		return telaGerenciarInscricaoparticipantes();
//	}
//	
//	
//	
//	
//	/**
//	 * Redireciona o usu�rio para a p�gina na qual ele vai confirmar o pagamento manual da GRU.
//	 *
//	 *<br />
//	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
//	 * <ul>
//	 * 		<li>/sigaa.war/extensao/InscricaoOnlike/gerenciarParticipantes.jsp</li>
//	 * </ul>
//	 * @throws ArqException 
//	 * @String
//	 */
//	public String preConfirmarPagamentoManualmente() throws ArqException{
//		
//		prepareMovimento(SigaaListaComando.CONFIRMA_PAGAMENTO_MANUAL_GRU_CURSOS_EVENTOS_EXTENSAO);
//		
//		int idPaticipante = getParameterInt("idParticipante");
//		
//		for (InscricaoAtividadeParticipante inscricaoParticipante : obj.getParticipantesInscritos()) {
//			if( inscricaoParticipante.getId() == idPaticipante){
//				inscricaoParaConfirmarPagamento = inscricaoParticipante;
//			}
//		}
//		
//		if(inscricaoParaConfirmarPagamento == null){ // n�o deveria entra nesse if
//			addMensagemErro("N�o foi poss�vel recuperar a inscri��o selecionada, por favor reinicie o processo.");
//			return null;
//		}
//		
//		if(inscricaoParaConfirmarPagamento.getIdGRUPagamento() == null){
//			addMensagemErro("N�o � poss�vel confirmar o pagamento manualmente porque a GRU n�o foi emitida pelo usu�rio.");
//			return null;
//		}
//		
//		inscricaoParaConfirmarPagamento.setGru(GuiaRecolhimentoUniaoHelper.getGRUByID(inscricaoParaConfirmarPagamento.getIdGRUPagamento()));
//		
//		return forward(getDirBase() + PAGINA_CONFIRMAR_PAGAMENTO);
//	}
//	
//	
//	
//	
//	/**
//	 * <p> Confirma o pagamento da inscri��o manualmente (mediante apresenta��o do comprovante).</p>
//	 * 
//	 * <br/>
//	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *   	<li>/sigaa.war/extensao/InscricaoOnlike/confirmarPagamentoCursosEventosManualmente.jsp</li>
//	 *   </ul> 
//	 */
//	public String confirmarPagamentoManualmente() throws ArqException{
//		
//		try {
//	
//			MovimentoCadastro movimento = new MovimentoCadastro();
//			movimento.setCodMovimento(SigaaListaComando.CONFIRMA_PAGAMENTO_MANUAL_GRU_CURSOS_EVENTOS_EXTENSAO);
//			movimento.setObjAuxiliar(inscricaoParaConfirmarPagamento.getGru().getId());
//			
//			execute(movimento);
//			
//			addMensagemInformation("Pagamento da inscria��o confirmado com sucesso ! ");
//			
//			return listarParticipantesInscritos(obj.getId());
//			
//		} catch (NegocioException ne) {
//			addMensagens(ne.getListaMensagens());
//			return null;
//		}
//		
//	}
//	
//	
//	
//	
//	/**
//	 * 
//	 * Usado para visualizar dados de uma inscri��o
//	 * 
//	 * Chamado por:
//	 * sigaa.war/extensao/InscricaoOnline/gerenciar_participantes.jsp
//	 * 
//	 * 
//	 * @return
//	 * @throws DAOException
//	 */
//	public String visualizarDadosInscrito() throws DAOException {
//		participante = getDAO(InscricaoAtividadeParticipanteDao.class).findByPrimaryKey(getParameterInt("id",0));
//		return forward(getDirBase() + EXIBIR_DADOS_INSCRITO);
//	}
//	
//	
//	/**
//	 * 
//	 * Permite alterar dados da inscri��o, como email e nome do usu�rio se tiverem errados.
//	 * 
//	 * Chamado por:
//	 * sigaa.war/extensao/InscricaoOnline/inscritos.jsp
//	 * 
//	 * 
//	 * @return
//	 * @throws ArqException 
//	 */
//	public String preAlterarDadosInscrito() throws ArqException {
//		
//		prepareMovimento(SigaaListaComando.ALTERAR_INSCRICAO_PARTICIPANTE);
//		
//		InscricaoAtividadeParticipanteDao dao = null;
//		try{
//			dao = getDAO(InscricaoAtividadeParticipanteDao.class);
//			participante = getDAO(InscricaoAtividadeParticipanteDao.class).findByPrimaryKey(getParameterInt("id",0));
//			carregarMunicipios();
//		}finally{
//			if(dao != null) dao.close();
//		}
//		
//		return telaAlterarDadosInscritos();
//	}
//	
//	
//	/**
//	 * 
//	 * Permite alterar dados da inscri��o, como email e nome do usu�rio se tiverem errados.
//	 * 
//	 * Chamado por:
//	 * sigaa.war/extensao/InscricaoOnline/alterar_dados_inscrito.jsp
//	 * 
//	 * 
//	 * @return
//	 * @throws ArqException 
//	 */
//	public String alterarDadosInscrito() throws ArqException {
//		
//		try {
//			MovimentoCadastro mov = new MovimentoCadastro();
//			mov.setObjMovimentado(participante);
//			mov.setCodMovimento(SigaaListaComando.ALTERAR_INSCRICAO_PARTICIPANTE);
//			execute(mov);
//			addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Dados da Inscri��o do Participante ");
//			
//			iniciarVisualizarParticipantesDaInscricao(obj); // recarerga os dados			
//			return telaInscritos();
//
//		} catch (NegocioException ne) {
//			addMensagens(ne.getListaMensagens());
//		}
//		return null;
//	}
//	
//	/**
//	 * Redireciona para a tela que lista os inscritos, na qual o coordenador pode reenviar a senha, visualizar a alterar dados da inscri��o.
//	 *  
//	 *  <br/>
//	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *    <li>/sigaa.war/extensao/InscricaoOnline/alterar_dados_inscrito.jsp</li>
//	 *   </ul>
//	 *
//	 * @return
//	 */
//	public String telaInscritos(){
//		return forward(getDirBase() + INSCRITOS);
//	}
//	
//	
//	
//	/**
//	 * Redireciona para a tela na qual o coordenador pode alterar alguns dados da inscri��o do 
//	 * usu�rio, caso ele tenha sido digitado algum dado errado.
//	 *  
//	 *  <p>M�todo n�o chamado por nenhuma pagana JSP</p>
//	 *
//	 * @return
//	 */
//	public String telaAlterarDadosInscritos(){
//		return forward(getDirBase() + ALTERAR_DADOS_INSCRITO);
//	}
//	
//	
//	/**
//	 * Carrega os munic�pios de uma unidade federativa.
//	 * 
//	 * <br/>
//	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *    <li>/sigaa.war/extensao/InscricaoOnline/alterar_dados_inscrito.jsp</li>
//	 *   </ul>
//	 * 
//	 * @throws DAOException
//	 */
//	public void carregarMunicipios() throws DAOException {		
//		MunicipioDao dao = getDAO(MunicipioDao.class);
//		UnidadeFederativa uf = dao.findByPrimaryKey(participante.getUnidadeFederativa().getId(), UnidadeFederativa.class);
//		Collection<Municipio> municipios = dao.findByUF(uf.getId());
//		
//		municipiosEndereco = new ArrayList<SelectItem>();
//		if (uf.getCapital() != null) {
//			municipiosEndereco.add(new SelectItem(uf.getCapital().getId(), uf.getCapital().getNome()));		
//		}
//		municipiosEndereco.addAll(toSelectItems(municipios, "id", "nome"));
//	}
//	
//	
//	
//	/**
//	 * Aprova a inscri��o de participantes.
//	 * 
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/gerenciar_participantes.jsp
//	 * 
//	 * @return
//	 * @throws ArqException
//	 */
//	public String aprovarParticipantes() throws ArqException {
//		return alterarInscritos(SigaaListaComando.APROVAR_INSCRICAO_PARTICIPANTE_EXTENSAO);
//	}
//
//	/**
//	 * Confirma Inscri��o de Participante.
//	 * 
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/gerenciar_participantes.jsp
//	 * 
//	 * @return
//	 * @throws ArqException
//	 */
//	public String confirmaInscricaoParticipantes() throws ArqException {
//		return alterarInscritos(SigaaListaComando.CONFIRMAR_INSCRICAO_PARTICIPANTE);
//	}
//	
//	/**
//	 * Recusa a inscri��o de participantes.
//	 * 
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/gerenciar_participantes.jsp
//	 * 
//	 * @return
//	 * @throws ArqException
//	 */
//	public String recusarParticipantes() throws ArqException {
//		
//		if ("".equals(obj.getMotivoCancelamento())) {
//			erroMotivo = "Informe o(s) motivo(s) da recusa da inscri��o(�es).";
//			return null;
//		}
//		exibirPainel = false;
//		erroMotivo = "";
//		return alterarInscritos(SigaaListaComando.RECUSAR_INSCRICAO_PARTICIPANTE_EXTENSAO);
//	}
//	
//	/**
//	 * Exibe uma tela para informa��o da recusa de inscri��o.
//	 * 
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/gerenciar_participantes.jsp
//	 * 
//	 * @param e
//	 */
//	public void informarMotivo(ActionEvent e) {
//		exibirPainel = true;
//	}
//	
//	/**
//	 * Cancela a recusa da inscri��o de participante.
//	 * 
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/gerenciar_participantes.jsp
//	 * 
//	 * @param e
//	 */
//	public void cancelarMotivo(ActionEvent e) {
//		exibirPainel = false;
//	}
//	
//	/**
//	 * Modifica a situa��o da inscri��o de participantes.
//	 * 
//	 * N�o chamado por JSP's
//	 * 
//	 * @param codMovimento
//	 * @return
//	 * @throws ArqException
//	 */
//	private String alterarInscritos(Comando codMovimento) throws ArqException {
//		
//		MovimentoCadastro mov = new MovimentoCadastro();
//		mov.setObjMovimentado(obj);
//		mov.setCodMovimento(codMovimento);
//		mov.setObjAuxiliar(inscricoesNaoConfirmadas);
//		
//		try {
//			execute(mov);
//			addMensagem(OPERACAO_SUCESSO);
//		} catch (NegocioException e) {
//			addMensagens(e.getListaMensagens());
//			return null;
//		}
//		
//		if(genrenciandoInscricoesSubAtividades){
//			return listarInscricoesMiniAtividade(subAtividadeSelecionada.getId());
//		}else{
//			return listarInscricoes(atividadeSelecionada.getId());
//		}
//		
//	}
//	
//	
//	/**
//	 * M�todo auxiliar para popular dados utilizados na view.
//	 * @throws DAOException
//	 */
//	private void carregarQuestionarios() throws DAOException {
//		
//		Collection<Questionario> questionarios = new ArrayList<Questionario>();
//		QuestionarioDao questionarioDao = getDAO(QuestionarioDao.class);
//		
//		Unidade unidade = getUsuarioLogado().getVinculoAtivo().getUnidade();
//		questionarios = questionarioDao.findQuestionariosinscricaoAtividade(unidade);
//		
//		
//		if ( !isEmpty(questionarios) ) {
//			if (obj.getQuestionario() == null ) {
//				obj.setQuestionario( new Questionario() );
//			}
//			
//			possiveisQuestionarios = new ArrayList<SelectItem>();
//			possiveisQuestionarios.add(new SelectItem(-1, "N�O APLICAR QUESTION�RIOS ESPEC�FICOS"));
//			possiveisQuestionarios.addAll( toSelectItems(questionarios, "id", "titulo") );
//		}
//		
//	}
//	
//	
//	/**
//	 * Verifica se o per�odo informado est� aberto em rela��o a data atual.
//	 * 
//	 * @param data
//	 * @return
//	 */
//	private boolean estaDentroPeriodoAberto(Date data) {
//		return data.getTime() >= periodoAbertoInicio.getTime() && data.getTime() <= periodoAbertoFim.getTime();
//	}
//
////	/**
////	 * Retorna para a tela anterior ao passo atual.
////	 * 
////	 * M�todo chamado pela(s) seguinte(s) JSP(s):
////	 * sigaa.war/extensao/InscricaoOnline/form.jsp
////	 * 
////	 * @return
////	 * @throws ArqException 
////	 */
////	public String voltarForm() throws ArqException {
////		if (confirmButton.equalsIgnoreCase("alterar")) {
////			carregarInscricoes();
////			prepareMovimento(SigaaListaComando.EXCLUIR_INSCRICAO_ACAO_EXTENSAO);
////			return forward(getDirBase() + LISTA);
////		}else {
////			return iniciarProcesso();
////		}
////	}
//	
//	
//	/**
//	 * Volta para lista de inscritos
//	 * M�todo chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/form.jsp
//	 * @return
//	 */
//	public String voltaListaInscritos() {
//		return forward(getDirBase() + LISTA_INSCRICOES);
//	}
//	
//	/**
//	 * Redireciona para a tela de gerenciar participantes.
//	 *
//	 * <br/>
//	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *   	<li>/sigaa.war/extensao/InscricaoOnlike/confirmarPagamentoCursosEventosManualmente.jsp</li>
//	 *   </ul> 
//	 *
//	 * @String
//	 */
//	public String telaGerenciarInscricaoparticipantes(){
//		return forward(getDirBase() + GERENCIAR_PARTICIPANTES);
//	}
//		
//	/**
//	 * Reinicializa os dados para uma nova opera��o.
//	 */
//	private void clear() {		
//		obj = new InscricaoAtividade();
//		atividade = new AtividadeExtensao();
//		inscricoesAtividades = new ArrayList<InscricaoAtividade>();
//		atividadesCoordenador = new ArrayList<AtividadeExtensao>();
//		inscricoesAtividades = new ArrayList<InscricaoAtividade>();
//		periodoAbertoInicio = null;
//		periodoAbertoFim = null;
//	}
//	
//	/**
//	 * Recupera as inscri��es de participantes pela situa��o informada.
//	 * 
//	 * @param insc
//	 * @param status
//	 * @return
//	 * @throws DAOException
//	 */
//	private Collection<InscricaoAtividadeParticipante> carregarParticipantes(InscricaoAtividade insc, Integer status) throws DAOException{
//		return getDAO(InscricaoAtividadeParticipanteDao.class).findParticipantesByInscricaoStatus(insc.getId(), status);
//	}
//
//	/**
//	 * Prepara ambiente para exibi��o da p�gina contendo todos os inscritos na a��o de extens�o.
//	 * 
//	 * @throws DAOException
//	 */
//	private void iniciarVisualizarParticipantesDaAcao(AtividadeExtensao acao) throws DAOException {
//	    inscricoesAceitas = getDAO(InscricaoAtividadeParticipanteDao.class).findInscricoesByAtividadeStatus(acao.getId(), StatusInscricaoParticipante.APROVADO);
//	    inscricoesRecusadas = getDAO(InscricaoAtividadeParticipanteDao.class).findInscricoesByAtividadeStatus(acao.getId(), StatusInscricaoParticipante.RECUSADO);
//	    inscricoesCanceladas = getDAO(InscricaoAtividadeParticipanteDao.class).findInscricoesByAtividadeStatus(acao.getId(), StatusInscricaoParticipante.CANCELADO);
//	    inscricoesNaoConfirmadas = getDAO(InscricaoAtividadeParticipanteDao.class).findInscricoesByAtividadeStatus(acao.getId(), StatusInscricaoParticipante.INSCRITO);
//	    inscricoesConfirmadas = getDAO(InscricaoAtividadeParticipanteDao.class).findInscricoesByAtividadeStatus(acao.getId(), StatusInscricaoParticipante.CONFIRMADO);
//	}
//	
//	/**
//	 * Prepara ambiente para exibi��o da p�gina contendo todos os inscritos na a��o de extens�o.
//	 * 
//	 * @throws DAOException
//	 */
//	private void iniciarVisualizarParticipantesDaSubAtividade(SubAtividadeExtensao sub) throws DAOException {
//	    inscricoesAceitas = getDAO(InscricaoAtividadeParticipanteDao.class).findInscricoesBySubAtividadeStatus(sub.getId(), StatusInscricaoParticipante.APROVADO);
//	    inscricoesRecusadas = getDAO(InscricaoAtividadeParticipanteDao.class).findInscricoesBySubAtividadeStatus(sub.getId(), StatusInscricaoParticipante.RECUSADO);
//	    inscricoesCanceladas = getDAO(InscricaoAtividadeParticipanteDao.class).findInscricoesBySubAtividadeStatus(sub.getId(), StatusInscricaoParticipante.CANCELADO);
//	    inscricoesNaoConfirmadas = getDAO(InscricaoAtividadeParticipanteDao.class).findInscricoesBySubAtividadeStatus(sub.getId(), StatusInscricaoParticipante.INSCRITO);
//	    inscricoesConfirmadas = getDAO(InscricaoAtividadeParticipanteDao.class).findInscricoesBySubAtividadeStatus(sub.getId(), StatusInscricaoParticipante.CONFIRMADO);
//	}
//	
//	/**
//	 * Prepara ambiente para exibi��o da p�gina contendo todos os participantes da inscri��o on-line informada.
//	 * 
//	 * @throws DAOException
//	 */
//	private void iniciarVisualizarParticipantesDaInscricao(InscricaoAtividade insc) throws DAOException {
//	    inscricoesAceitas = getDAO(InscricaoAtividadeParticipanteDao.class).findParticipantesByInscricaoStatus(insc.getId(), StatusInscricaoParticipante.APROVADO);
//	    inscricoesRecusadas = getDAO(InscricaoAtividadeParticipanteDao.class).findParticipantesByInscricaoStatus(insc.getId(), StatusInscricaoParticipante.RECUSADO);
//	    inscricoesCanceladas = getDAO(InscricaoAtividadeParticipanteDao.class).findParticipantesByInscricaoStatus(insc.getId(), StatusInscricaoParticipante.CANCELADO);
//	    inscricoesNaoConfirmadas = getDAO(InscricaoAtividadeParticipanteDao.class).findParticipantesByInscricaoStatus(insc.getId(), StatusInscricaoParticipante.INSCRITO);
//	    inscricoesConfirmadas = getDAO(InscricaoAtividadeParticipanteDao.class).findParticipantesByInscricaoStatus(insc.getId(), StatusInscricaoParticipante.CONFIRMADO);
//	
//	    if( insc.getQuestionario()  != null){
//	    	recuperaIdsQuestionariosRespondidos(insc.getQuestionario().getId());
//	    }
//	    
//	}
//
//	
//	/**
//	 * <p>Verifica entre as inscri��es aceitas aquelas que tem question�rios. Para habilitar a visualiza��o</p>
//	 * 
//	 * <p>Apenas quando o usu�rio selecionar uma inscri��o individualmente, os dados do question�rio ser�o carregados.</p>
//	 * @throws DAOException 
//	 * @throws  
//	 */
//	private void recuperaIdsQuestionariosRespondidos(int idQuestionario) throws DAOException{
//		
//		QuestionarioDao dao = null;
//		
//		try{
//			dao = getDAO(QuestionarioDao.class);
//			
//			List<QuestionarioRespostas> lista = dao.findIdsRespostasInscricaoAtividadeExtensaoByQuestionario(idQuestionario);
//			
//			for (QuestionarioRespostas questionarioRespostas : lista) {
//				
//				for (InscricaoAtividadeParticipante participante : inscricoesAceitas) {
//					if(participante.getId() == questionarioRespostas.getInscricaoAtividadeParticipante().getId()){
//						participante.setQuestionarioRespostas(questionarioRespostas);
//					}
//				}
//				
//				for (InscricaoAtividadeParticipante participante : inscricoesRecusadas) {
//					if(participante.getId() == questionarioRespostas.getInscricaoAtividadeParticipante().getId()){
//						participante.setQuestionarioRespostas(questionarioRespostas);
//					}
//				}
//				
//				for (InscricaoAtividadeParticipante participante : inscricoesCanceladas) {
//					if(participante.getId() == questionarioRespostas.getInscricaoAtividadeParticipante().getId()){
//						participante.setQuestionarioRespostas(questionarioRespostas);
//					}
//				}
//				
//				for (InscricaoAtividadeParticipante participante : inscricoesNaoConfirmadas) {
//					if(participante.getId() == questionarioRespostas.getInscricaoAtividadeParticipante().getId()){
//						participante.setQuestionarioRespostas(questionarioRespostas);
//					}
//				}
//				
//				for (InscricaoAtividadeParticipante participante : inscricoesConfirmadas) {
//					if(participante.getId() == questionarioRespostas.getInscricaoAtividadeParticipante().getId()){
//						participante.setQuestionarioRespostas(questionarioRespostas);
//					}
//				}
//			}
//			
//		}finally{
//			if(dao != null) dao.close();
//		}
//		
//	}
//	
//	
//	
//	
//	/**
//	 * Chamado antes de realizar o cadastro.
//	 * 
//	 * M�todo n�o invocado por JSP�s
//	 */
//	@Override
//	public void beforeCadastrarAndValidate() throws SegurancaException, DAOException {
//		obj.setDataCadastro(new Date());
//		obj.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
//		obj.setInstrucoesInscricao(StringUtils.removerComentarios(obj.getInstrucoesInscricao()));
//		obj.setObservacoes(StringUtils.removerComentarios(obj.getObservacoes()));
//	}
//
//	/**
//	 * Verifica se o usu�rio logado � um coordenador de extens�o.
//	 * 
//	 * M�todo n�o invocado por JSP�s
//	 */
//	@Override
//	public void checkChangeRole() throws SegurancaException {
//		checkRole(SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO);
//	}
//
//	/**
//	 * Retorna o diret�rio base
//	 * 
//	 * M�todo n�o invocado por JSP�s
//	 */
//	@Override
//	public String getDirBase() {
//		return "/extensao/InscricaoOnline";
//	}
//
//	public Collection<InscricaoAtividade> getInscricoesAtividades() {
//		return inscricoesAtividades;
//	}
//
//	public void setInscricoesAtividades(
//			Collection<InscricaoAtividade> inscricoesAtividades) {
//		this.inscricoesAtividades = inscricoesAtividades;
//	}
//
//	public void setAtividade(AtividadeExtensao atividade) {
//		this.atividade = atividade;
//	}
//
//	public AtividadeExtensao getAtividade() {
//		return atividade;
//	}
//
//	public void setAtividadesCoordenador(Collection<AtividadeExtensao> atividadesCoordenador) {
//		this.atividadesCoordenador = atividadesCoordenador;
//	}
//
//	public Collection<AtividadeExtensao> getAtividadesCoordenador() {
//		return atividadesCoordenador;
//	}
//
//	public void setTexto(String texto) {
//		this.texto = texto;
//	}
//
//	public String getTexto() {
//		return texto;
//	}
//
//	public String getConfirmButton() {
//		return confirmButton;
//	}
//
//	public void setConfirmButton(String confirmButton) {
//		this.confirmButton = confirmButton;
//	}
//
//	public void setExibirPainel(boolean exibirPainel) {
//		this.exibirPainel = exibirPainel;
//	}
//
//	public boolean isExibirPainel() {
//		return exibirPainel;
//	}
//
//	public void setErroMotivo(String erroMotivo) {
//		this.erroMotivo = erroMotivo;
//	}
//
//	public String getErroMotivo() {
//		return erroMotivo;
//	}
//
//	public void setDisableMotivo(boolean disableMotivo) {
//		this.disableMotivo = disableMotivo;
//	}
//
//	public boolean isDisableMotivo() {
//		return disableMotivo;
//	}
//
//	public InscricaoAtividadeParticipante getParticipante() {
//		return participante;
//	}
//
//	public void setParticipante(InscricaoAtividadeParticipante participante) {
//		this.participante = participante;
//	}
//
//	public Collection<InscricaoAtividadeParticipante> getInscricoesNaoConfirmadas() {
//	    return inscricoesNaoConfirmadas;
//	}
//
//	public void setInscricoesNaoConfirmadas(
//		Collection<InscricaoAtividadeParticipante> inscricoesNaoConfirmadas) {
//	    this.inscricoesNaoConfirmadas = inscricoesNaoConfirmadas;
//	}
//
//	public void setInscricoesAceitas(
//		Collection<InscricaoAtividadeParticipante> inscricoesAceitas) {
//	    this.inscricoesAceitas = inscricoesAceitas;
//	}
//
//	public void setInscricoesRecusadas(
//		Collection<InscricaoAtividadeParticipante> inscricoesRecusadas) {
//	    this.inscricoesRecusadas = inscricoesRecusadas;
//	}
//
//	public void setInscricoesCanceladas(
//		Collection<InscricaoAtividadeParticipante> inscricoesCanceladas) {
//	    this.inscricoesCanceladas = inscricoesCanceladas;
//	}
//
//	public void setInscricoesConfirmadas(
//		Collection<InscricaoAtividadeParticipante> inscricoesConfirmadas) {
//	    this.inscricoesConfirmadas = inscricoesConfirmadas;
//	}
//
//	public Collection<InscricaoAtividadeParticipante> getInscricoesAceitas() {
//	    return inscricoesAceitas;
//	}
//
//	public Collection<InscricaoAtividadeParticipante> getInscricoesRecusadas() {
//	    return inscricoesRecusadas;
//	}
//
//	public Collection<InscricaoAtividadeParticipante> getInscricoesCanceladas() {
//	    return inscricoesCanceladas;
//	}
//
//	public Collection<InscricaoAtividadeParticipante> getInscricoesConfirmadas() {
//	    return inscricoesConfirmadas;
//	}
//
//	public Integer getNovasVagas() {
//		return novasVagas;
//	}
//
//	public void setNovasVagas(Integer novasVagas) {
//		this.novasVagas = novasVagas;
//	}
//
//	public Integer getNumeroInscAtual() {
//		return numeroInscAtual;
//	}
//
//	public void setNumeroInscAtual(Integer numeroInscAtual) {
//		this.numeroInscAtual = numeroInscAtual;
//	}
//
//	public Integer getIdAtividade() {
//		return idAtividade;
//	}
//
//	public void setIdAtividade(Integer idAtividade) {
//		this.idAtividade = idAtividade;
//	}
//
//	public InscricaoAtividadeParticipante getInscricaoParaConfirmarPagamento() {
//		return inscricaoParaConfirmarPagamento;
//	}
//
//	public void setInscricaoParaConfirmarPagamento(InscricaoAtividadeParticipante inscricaoParaConfirmarPagamento) {
//		this.inscricaoParaConfirmarPagamento = inscricaoParaConfirmarPagamento;
//	}
//
//	public Collection<AtividadeExtensao> getSubAtividadesCoordenador() {
//		return subAtividadesCoordenador;
//	}
//
//	public void setSubAtividadesCoordenador(
//			Collection<AtividadeExtensao> subAtividadesCoordenador) {
//		this.subAtividadesCoordenador = subAtividadesCoordenador;
//	}
//
//	public Collection<InscricaoAtividade> getInscricoesSubAtividades() {
//		return inscricoesSubAtividades;
//	}
//
//	public void setInscricoesSubAtividades(
//			Collection<InscricaoAtividade> inscricoesSubAtividades) {
//		this.inscricoesSubAtividades = inscricoesSubAtividades;
//	}
//
//	public List<SelectItem> getPossiveisQuestionarios() {
//		return possiveisQuestionarios;
//	}
//
//	public void setPossiveisQuestionarios(List<SelectItem> possiveisQuestionarios) {
//		this.possiveisQuestionarios = possiveisQuestionarios;
//	}
//
//	public Collection<SelectItem> getMunicipiosEndereco() {
//		return municipiosEndereco;
//	}
//
//	public void setMunicipiosEndereco(Collection<SelectItem> municipiosEndereco) {
//		this.municipiosEndereco = municipiosEndereco;
//	}
//
//	public void setNovasVagasMiniAtividade(Integer novasVagasMiniAtividade) {
//		this.novasVagasMiniAtividade = novasVagasMiniAtividade;
//	}
//
//	public Integer getNovasVagasMiniAtividade() {
//		return novasVagasMiniAtividade;
//	}
//
//	public void setIdSubAtividade(Integer idSubAtividade) {
//		this.idSubAtividade = idSubAtividade;
//	}
//
//	public Integer getIdSubAtividade() {
//		return idSubAtividade;
//	}
//
//	public void setSubAtividade(SubAtividadeExtensao subAtividade) {
//		this.subAtividade = subAtividade;
//	}
//
//	public SubAtividadeExtensao getSubAtividade() {
//		return subAtividade;
//	}
//
//	public SubAtividadeExtensao getSubAtividadeSelecionada() {
//		return subAtividadeSelecionada;
//	}
//
//	public void setSubAtividadeSelecionada(SubAtividadeExtensao subAtividadeSelecionada) {
//		this.subAtividadeSelecionada = subAtividadeSelecionada;
//	}
//
//	public AtividadeExtensao getAtividadeSelecionada() {
//		return atividadeSelecionada;
//	}
//
//	public void setAtividadeSelecionada(AtividadeExtensao atividadeSelecionada) {
//		this.atividadeSelecionada = atividadeSelecionada;
//	}
//	
	
}
