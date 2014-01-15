/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * <p>Gerencia as inscrições de Cursos e Eventos pelo Portal Docente.</p>
 * 
 * <p>Entre as ações de gerenciamente estão  a aprovação e rejeição da inscriação e a confirmação manual do pagamento da GRU, etc...</p>
 * 
 * 
 * @author Daniel Augusto 
 * @deprecated tá ficando impossível manter esse código, separado em vários MBens 
 */
@Scope("session")
@Component("inscricaoAtividade")
@Deprecated
public class InscricaoAtividadeMBean extends SigaaAbstractController<InscricaoAtividade> {
	
	
//	/** Página para confirmar o pagamento manualmente de um curso e evento. Caso a confirmação do banco demore a chegar, poís existe a confirmaçaõ automática. */
//	public static final String PAGINA_CONFIRMAR_PAGAMENTO = "/confirmarPagamentoCursosEventosManualmente.jsp";
//	
//	
//	
//	/** Form para cadastro de inscrição */
//	private static final String FORM_INSCRICAO_ATIVIDADE = "/form.jsp";
//	/** Form para cadastro de inscrição */
//	private static final String FORM_INSCRICAO_SUB_ATIVIDADE = "/form_inscricao_sub_atividades.jsp";
//	/** Lista as inscrições criadas para uma ação de extensão */
//	private static final String LISTA = "/lista.jsp";
//	/** Lista as inscrições criadas para uma ação de extensão */
//	private static final String LISTA_SUB_ATIVIDADE = "/lista_sub_atividades.jsp";
//	/** Lista com ações e inscrições  */
//	private static final String LISTA_ACOES = "/lista_acoes.jsp";
//	/** Página gerenciar participantes */
//	private static final String GERENCIAR_PARTICIPANTES = "/gerenciar_participantes.jsp";
//	
//	/** Página lista de inscrições. é a mesma coisa da página LISTA_SUB_ATIVIDADE*/
//	@Deprecated 
//	private static final String LISTA_INSCRICOES = "/lista_inscricoes.jsp";
//	/** Página lista de inscrições. é a mesma coisa da página LISTA_SUB_ATIVIDADE  */
//	@Deprecated 
//	private static final String LISTA_INSCRICOES_SUB_ATIVIDADE = "/lista_inscricoes_sub_atividade.jsp";
//	
//	/** Página dados de inscrito  */
//	private static final String EXIBIR_DADOS_INSCRITO = "/exibir_dados_inscrito.jsp";
//	/** Página relatório de inscritos */
//	private static final String RELATORIO_INSCRITOS = "/relatorio_inscritos.jsp";
//	/** Página relatório de inscritos em subatividades*/
//	private static final String RELATORIO_INSCRITOS_SUB_ATIVIDADES = "/relatorio_inscritos_sub_atividade.jsp";
//	/** Página Inscritos */
//	private static final String INSCRITOS = "/inscritos.jsp";
//	/** Página Inscritos */
//	private static final String PENDENTES = "/pendentes.jsp";
//	/** Página para alterar os dados dos participantes */
//	private static final String ALTERAR_DADOS_INSCRITO = "/alterar_dados_inscrito.jsp";
//	
//	
//	
//	/** Inscrições de uma ação */
//	private Collection<InscricaoAtividade> inscricoesAtividades = new ArrayList<InscricaoAtividade>();
//	/** Inscrições de uma ação */
//	private Collection<InscricaoAtividade> inscricoesSubAtividades = new ArrayList<InscricaoAtividade>();
//	/** Ações de um coordenador */
//	private Collection<AtividadeExtensao> atividadesCoordenador = new ArrayList<AtividadeExtensao>();	
//	/** SubAções de um coordenador */
//	private Collection<AtividadeExtensao> subAtividadesCoordenador = new ArrayList<AtividadeExtensao>();
//	/** Lista de SelectItem questionários. */
//	private List<SelectItem> possiveisQuestionarios;
//	/** Ação de Extensão */	
//	private AtividadeExtensao atividade = new AtividadeExtensao();
//	/** Sub atividade de uma Ação de Extensão */	
//	private SubAtividadeExtensao subAtividade = new SubAtividadeExtensao();
//	/** Participante de ação de extensão. Na verdade é a inscrição do participante */
//	private InscricaoAtividadeParticipante participante = new InscricaoAtividadeParticipante();
//	/** Data início para inscrição */
//	private Date periodoAbertoInicio;
//	/** Data fim para inscrição */
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
//	/** Auxiliar para alteração de vagas de uma ação de extensão */
//	private Integer novasVagas = new Integer(0);
//	/** Auxiliar para alteração de vagas de uma mini atividade */
//	private Integer novasVagasMiniAtividade = new Integer(0);
//	/** Auxiliar idAtividade */
//	private Integer idAtividade = new Integer(0);
//	/** Auxiliar idSubAtividade */
//	private Integer idSubAtividade = new Integer(0);
//	/** Número inscrições atual */
//	private Integer numeroInscAtual;
//	/** Inscrições aceitas */
//	private Collection<InscricaoAtividadeParticipante> inscricoesAceitas = new ArrayList<InscricaoAtividadeParticipante>();
//	/** Inscriçoes recusadas */
//	private Collection<InscricaoAtividadeParticipante> inscricoesRecusadas = new ArrayList<InscricaoAtividadeParticipante>();
//	/** Inscrições canceladas */
//	private Collection<InscricaoAtividadeParticipante> inscricoesCanceladas = new ArrayList<InscricaoAtividadeParticipante>();
//	/** Inscrições não confirmadas */
//	private Collection<InscricaoAtividadeParticipante> inscricoesNaoConfirmadas = new ArrayList<InscricaoAtividadeParticipante>();
//	/** Inscrições confirmadas */
//	private Collection<InscricaoAtividadeParticipante> inscricoesConfirmadas = new ArrayList<InscricaoAtividadeParticipante>();
//
//	
//	/** Sub atividade de uma Ação de Extensão que é selecionado entre as subatividades listadas 
//	 * no caso de uso gerenciar inscrições.
//	 * 
//	 * Foi criada uma nova variável porque na tela que listas as incrições existem várias 
//	 * operações que eram realizadas sempre em cima da mesma variável "subAtividade", sobre escrevendo os dados da subatividade selecionada.
//	 * 
//	 * @see subAtividadesCoordenador
//	 * @see this#listarInscricoesMiniAtividade();
//	 */	
//	private SubAtividadeExtensao subAtividadeSelecionada = new SubAtividadeExtensao();
//	
//	
//	/** Atividade de uma Ação de Extensão que é selecionado entre as atividades listadas 
//	 * no caso de uso gerenciar inscrições.
//	 * 
//	 * Foi criada uma nova variável porque na tela que listas as incrições existem várias 
//	 * operações que eram realizadas sempre em cima da mesma variável "tividade", sobre escrevendo os dados da atividade selecionada.
//	 * 
//	 * @see atividadesCoordenador
//	 * @see this#listarInscricoes();
//	 */	
//	private AtividadeExtensao atividadeSelecionada = new AtividadeExtensao();
//	
//	
//	
//	/** Diz se o usuário está genrenciando a tela de inscrições em atividades ou sub atividades.. */
//	private boolean genrenciandoInscricoesSubAtividades = false;
//	
//	
//	/** Usado na alterado dos dados do participante */
//	private Collection<SelectItem> municipiosEndereco = new ArrayList<SelectItem>(0);
//	
//	/** Guarda a inscrição que estava com o status de pagamento EM ABERTO selecionado pelo coordenador para confirmar o seu pagamento manualmente. */
//	private InscricaoAtividadeParticipante inscricaoParaConfirmarPagamento; 
//	
//	public InscricaoAtividadeMBean() {
//		obj = new InscricaoAtividade();
//	}
//
//	/**
//	 * Busca todos os Cursos e Eventos dos quais o docente logado seja coordenador.
//	 * 
//	 * Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * Lista as inscrições criadas para uma ação de extensão.
//	 * 
//	 * Método chamado pela(s) seguinte(s) JSP(s):
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
//			addMensagemErro("Ação selecionada não é uma ação válida.");
//			return null;
//		}
//	}
//	
//	
//	/**
//	 * Lista as inscrições criadas para uma ação de extensão.
//	 * 
//	 * Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * Redireciona para a pagina que gerencia as inscrições em atividades.
//	 *  
//	 *  <br/>
//	 *  Método chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *    <li>sigaa.war/extensao/InscricaoOnline/lista.jsp</li>
//	 *   </ul>
//	 *
//	 *   <p>Método não chamado por nenhuma página jsp.</p>
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
//	 * Lista as inscrições criadas para uma ação de extensão.
//	 * 
//	 * Método chamado pela(s) seguinte(s) JSP(s):
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
//			addMensagemErro("Ação selecionada não é uma ação válida.");
//			return null;
//		}
//	}
//	
//	
//	/** Método para ser chamado de outro MBean, faz a mesma coisa do método acima. */
//	public String listarInscricoesMiniAtividade(int idSubAtividade) throws ArqException {
//		subAtividadeSelecionada = getGenericDAO().findByPrimaryKey(idSubAtividade, SubAtividadeExtensao.class);
//		inscricoesSubAtividades = getDAO(InscricaoAtividadeExtensaoDao.class).findAllBySubAtividade(subAtividadeSelecionada.getId());
//		prepareMovimento(SigaaListaComando.EXCLUIR_INSCRICAO_ACAO_EXTENSAO);
//		
//		/* A variável transiente que guarda o número de inscritos fica na sub atividade, 
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
//	 * Redireciona para a pagina que gerencia as inscrições em sub atividades.
//	 *  
//	 *  <br/>
//	 *  Método chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *    <li>sigaa.war/extensao/InscricaoOnline/lista_sub_atividades.jsp</li>
//	 *   </ul>
//	 *
//	 *   <p>Método não chamado por nenhuma página jsp.</p>
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
//	 * <p>Muitas das tela que são chamadas a partir da tela de gerenciamento de inscrições volta para o início do sistema.</p>
//	 * <p>Ver lista de JSP de onde esse método é chamado.</p>
//	 *  
//	 *  <br/>
//	 *  Método chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *    <li>/sigaa.war/extensao/InscricaoOnline/pendentes.jsp</li>
//	 *    <li>/sigaa.war/extensao/InscricaoOnline/inscritos.jsp</li>
//	 *    <li>/sigaa.war/extensao/InscricaoOnline/form_inscricao_sub_atividades.jsp</li>
//	 *    <li>/sigaa.war/extensao/InscricaoOnline/suspender_inscricao.jsp</li>
//	 *    <li>/sigaa.war/extensao/InscricaoOnline/gerenciar_participantes.jsp</li>
//	 *    
//	 *   </ul>
//	 *
//	 *   <p>Método não chamado por nenhuma página jsp.</p>
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
//	 * Inicia página para acompahamento dos inscritos.
//	 * Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * Redireciona para lista de isncritos de uma ação.
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
//	 * Listar inscrições da atividade selecionada
//	 * <br />
//	 * Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * Listar inscrições da sub-atividade selecionada
//	 * <br />
//	 * Método chamado pela(s) seguinte(s) JSP(s):
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
////	 * <p>Listar inscrições de todas as atividades do coordenador.</p>
////	 * 
////	 * <p>
////	 * <strong>Esse método é apenas um atalho para ir direto a todas as incrições cujo o usuário logado é coordenador.
////	 * O usuário pode também realizar essa ação atravez do método iniciarProcesso() ->  listarInscricoesAtividade() 
////	 * </strong>
////	 * <p>
////	 * 
////	 * 
////	 * <br />
////	 * Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * Realiza verificação e configuração antes do cadastrar.
//	 * 
//	 * Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * Realiza verificação e configuração antes do cadastrar.
//	 * 
//	 * Método chamado pela(s) seguinte(s) JSP(s):
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
//			addMensagemWarning("Essa mini ação possui a inscrição " + obj.getCodigo() + " com período aberto de " + Formatador.getInstance().formatarData(periodoAbertoInicio) + " à " + Formatador.getInstance().formatarData(periodoAbertoFim));			
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
//	 * Utilizado para mostrar os métodos de preenchimento das vagas nos mini cursos/eventos.
//	 * 
//	 * @return
//	 * @throws DAOException
//	 */
//	public Collection<SelectItem> getAllMetodosPreenchimento() throws DAOException {		
//		
//		SelectItem comConfirmacao = new SelectItem(InscricaoAtividade.COM_CONFIRMACAO, "Com Confirmação");
//		//SelectItem automatico = new SelectItem(InscricaoAtividade.PREENCHIMENTO_AUTOMATICO, "Preenchimento Automático");
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
//	 * Responsável por alterar a quantidade geral de vagas para um curso ou evento
//	 * de extensão.
//	 * Método chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/form.jsp
//	 * @throws ArqException 
//	 * 
//	 */	
//	public String alterarVagas() throws ArqException {		
//		if( novasVagas == null || novasVagas <= 0){
//			addMensagemErro("Informe o número de vagas.");
//			return iniciarProcesso();
//		}
//
//		InscricaoAtividadeExtensaoDao dao = getDAO(InscricaoAtividadeExtensaoDao.class);
//		obj.setAtividade(new AtividadeExtensao());
//		obj.setAtividade(dao.findByPrimaryKey(idAtividade, AtividadeExtensao.class));
//
//		Integer vagasReservadas = (dao.totalVagasInscricoes(obj.getAtividade().getId()));
//		if (novasVagas < vagasReservadas) {
//			addMensagemErro("O número de vagas não pode ser inferior a quantidade de inscrições reservadas.");
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
//	 * Responsável por alterar a quantidade geral de vagas para uma mini-atividade
//	 * de extensão.
//	 * Método chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/form.jsp
//	 * @throws ArqException 
//	 * 
//	 */	
//	public String alterarVagasMiniAtividade() throws ArqException {		
//		if( novasVagasMiniAtividade == null || novasVagasMiniAtividade <= 0){
//			addMensagemErro("Informe o número de vagas.");
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
//			addMensagemErro("O número de vagas não pode ser inferior a quantidade de inscrições reservadas.");
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
//	 * Cria um período de inscrição na área pública.
//	 * 
//	 * Método chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/form.jsp
//	 */
//	@Override
//	public String cadastrar() throws ArqException {
//		InscricaoAtividadeExtensaoDao dao = getDAO(InscricaoAtividadeExtensaoDao.class);
//		
//		// Realiza a associação entre o questionário e a ação de inscrição //
//		if(obj.getQuestionario()!= null && obj.getQuestionario().getId() > 0 ) {
//			obj.setQuestionario(getGenericDAO().findByPrimaryKey(obj.getQuestionario().getId(), Questionario.class));
//		}else{ // Se o usuário retirou o questionário, retira a associação
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
//				mensagens.addErro("Período da inscrição não pode estar dentro do período da inscrição aberta.");
//				mensagens.addWarning("Existe uma Inscrição com período aberto de " 
//					+ Formatador.getInstance().formatarData(periodoAbertoInicio) + " à " 
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
//			mensagens.addErro("Não é possível abrir "+obj.getQuantidadeVagas()+" vaga(s). O valor máximo permitido de vagas é "+vagasRestantes+".  ");
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
//	 * Cria um período de inscrição na área pública.
//	 * 
//	 * Método chamado pela(s) seguinte(s) JSP(s):
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
//		//Verifica se tem alguma inscrição aberta.
//		if( ! ValidatorUtil.isEmpty(obj.getSubAtividade())) {
//			InscricaoAtividade inscricao = dao.findInscricaoAbertaBySubAtividade(obj.getSubAtividade().getId());
//			if (inscricao != null) {				
//				periodoAbertoInicio = inscricao.getPeriodoInicio();
//				periodoAbertoFim = inscricao.getPeriodoFim();
//				addMensagemWarning("Essa mini ação possui a inscrição " + obj.getCodigo() + " com período aberto de " + Formatador.getInstance().formatarData(periodoAbertoInicio) + " à " + Formatador.getInstance().formatarData(periodoAbertoFim));
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
//			mensagens.addErro("Não é possível abrir "+obj.getQuantidadeVagas()+" vaga(s). O valor máximo permitido de vagas é "+vagasRestantes+".  ");
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
//	 * Realiza configuração antes do alterar.
//	 * 
//	 * Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * Realiza configuração antes do alterar.
//	 * 
//	 * Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * Altera os dados referentes a uma inscrição.
//	 * 
//	 * Método não invocado por JSP´s
//	 * 
//	 * @return
//	 * @throws ArqException
//	 */
//	private String alterar() throws ArqException {
//		InscricaoAtividadeExtensaoDao dao = getDAO(InscricaoAtividadeExtensaoDao.class);
//		erros.getMensagens().clear();
//		erros = new ListaMensagens();
//		obj.setInscricoesParticipantes(carregarParticipantes(obj, null)); // Utilizado na validação da inscrição
//		erros.addAll(obj.validate());
//		
//		obj.setAtividade(dao.findByPrimaryKey(obj.getAtividade().getId(), AtividadeExtensao.class));
//		Integer vagasRestantes = numeroInscAtual + (obj.getAtividade().getCursoEventoExtensao().getNumeroVagas() - dao.totalVagasInscricoes(obj.getAtividade().getId()));
//		if(obj.getQuantidadeVagas() > vagasRestantes ){
//			erros.addErro("Não é possível abrir "+obj.getQuantidadeVagas()+" vaga(s). O valor máximo permitido de vagas é "+vagasRestantes+".  ");
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
//			addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Período de Inscrição");
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
//	 * Altera os dados referentes a uma inscrição.
//	 * 
//	 * Método não invocado por JSP´s
//	 * 
//	 * @return
//	 * @throws ArqException
//	 */
//	private String alterarSubAtividades() throws ArqException {
//		InscricaoAtividadeExtensaoDao dao = getDAO(InscricaoAtividadeExtensaoDao.class);
//		erros.getMensagens().clear();
//		erros = new ListaMensagens();
//		obj.setInscricoesParticipantes(carregarParticipantes(obj, null)); // Utilizado na validação da inscrição
//		erros.addAll(obj.validate());
//		
//		obj.setAtividade(dao.findByPrimaryKey(obj.getAtividade().getId(), AtividadeExtensao.class));
//		Integer vagasRestantes = numeroInscAtual + (obj.getSubAtividade().getNumeroVagas() - dao.totalVagasInscricoesSubAtividade(obj.getAtividade().getId()));
//		if(obj.getQuantidadeVagas() > vagasRestantes ){
//			erros.addErro("Não é possível abrir "+obj.getQuantidadeVagas()+" vaga(s). O valor máximo permitido de vagas é "+vagasRestantes+".  ");
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
//			addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Período de Inscrição");
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
//			addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Inscrição");
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
//			addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Inscrição");
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
//	 * Realiza verificação e configuração antes do suspender.
//	 * 
//	 * Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * Suspende uma inscrição de uma ação de extensão.
//	 * 
//	 * Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * Carrega todos as inscrições confirmadas de participantes para seleção pelo coordenador.
//	 * 
//	 * Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * Carrega todos as inscrições confirmadas de participantes para seleção pelo coordenador.
//	 * 
//	 * Método não chamado por jsp.
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
//		// para o usuário validar o pagamento da GRU tem que saber se o curso é paga ou não //
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
//	 * Redireciona o usuário para a página na qual ele vai confirmar o pagamento manual da GRU.
//	 *
//	 *<br />
//	 * Método chamado pela(s) seguinte(s) JSP(s): 
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
//		if(inscricaoParaConfirmarPagamento == null){ // não deveria entra nesse if
//			addMensagemErro("Não foi possível recuperar a inscrição selecionada, por favor reinicie o processo.");
//			return null;
//		}
//		
//		if(inscricaoParaConfirmarPagamento.getIdGRUPagamento() == null){
//			addMensagemErro("Não é possível confirmar o pagamento manualmente porque a GRU não foi emitida pelo usuário.");
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
//	 * <p> Confirma o pagamento da inscrição manualmente (mediante apresentação do comprovante).</p>
//	 * 
//	 * <br/>
//	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
//			addMensagemInformation("Pagamento da inscriação confirmado com sucesso ! ");
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
//	 * Usado para visualizar dados de uma inscrição
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
//	 * Permite alterar dados da inscrição, como email e nome do usuário se tiverem errados.
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
//	 * Permite alterar dados da inscrição, como email e nome do usuário se tiverem errados.
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
//			addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Dados da Inscrição do Participante ");
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
//	 * Redireciona para a tela que lista os inscritos, na qual o coordenador pode reenviar a senha, visualizar a alterar dados da inscrição.
//	 *  
//	 *  <br/>
//	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * Redireciona para a tela na qual o coordenador pode alterar alguns dados da inscrição do 
//	 * usuário, caso ele tenha sido digitado algum dado errado.
//	 *  
//	 *  <p>Método não chamado por nenhuma pagana JSP</p>
//	 *
//	 * @return
//	 */
//	public String telaAlterarDadosInscritos(){
//		return forward(getDirBase() + ALTERAR_DADOS_INSCRITO);
//	}
//	
//	
//	/**
//	 * Carrega os municípios de uma unidade federativa.
//	 * 
//	 * <br/>
//	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * Aprova a inscrição de participantes.
//	 * 
//	 * Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * Confirma Inscrição de Participante.
//	 * 
//	 * Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * Recusa a inscrição de participantes.
//	 * 
//	 * Método chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/gerenciar_participantes.jsp
//	 * 
//	 * @return
//	 * @throws ArqException
//	 */
//	public String recusarParticipantes() throws ArqException {
//		
//		if ("".equals(obj.getMotivoCancelamento())) {
//			erroMotivo = "Informe o(s) motivo(s) da recusa da inscrição(ões).";
//			return null;
//		}
//		exibirPainel = false;
//		erroMotivo = "";
//		return alterarInscritos(SigaaListaComando.RECUSAR_INSCRICAO_PARTICIPANTE_EXTENSAO);
//	}
//	
//	/**
//	 * Exibe uma tela para informação da recusa de inscrição.
//	 * 
//	 * Método chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/gerenciar_participantes.jsp
//	 * 
//	 * @param e
//	 */
//	public void informarMotivo(ActionEvent e) {
//		exibirPainel = true;
//	}
//	
//	/**
//	 * Cancela a recusa da inscrição de participante.
//	 * 
//	 * Método chamado pela(s) seguinte(s) JSP(s):
//	 * sigaa.war/extensao/InscricaoOnline/gerenciar_participantes.jsp
//	 * 
//	 * @param e
//	 */
//	public void cancelarMotivo(ActionEvent e) {
//		exibirPainel = false;
//	}
//	
//	/**
//	 * Modifica a situação da inscrição de participantes.
//	 * 
//	 * Não chamado por JSP's
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
//	 * Método auxiliar para popular dados utilizados na view.
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
//			possiveisQuestionarios.add(new SelectItem(-1, "NÃO APLICAR QUESTIONÁRIOS ESPECÍFICOS"));
//			possiveisQuestionarios.addAll( toSelectItems(questionarios, "id", "titulo") );
//		}
//		
//	}
//	
//	
//	/**
//	 * Verifica se o período informado está aberto em relação a data atual.
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
////	 * Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * Método chamado pela(s) seguinte(s) JSP(s):
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
//	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
//	 * Reinicializa os dados para uma nova operação.
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
//	 * Recupera as inscrições de participantes pela situação informada.
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
//	 * Prepara ambiente para exibição da página contendo todos os inscritos na ação de extensão.
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
//	 * Prepara ambiente para exibição da página contendo todos os inscritos na ação de extensão.
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
//	 * Prepara ambiente para exibição da página contendo todos os participantes da inscrição on-line informada.
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
//	 * <p>Verifica entre as inscrições aceitas aquelas que tem questionários. Para habilitar a visualização</p>
//	 * 
//	 * <p>Apenas quando o usuário selecionar uma inscrição individualmente, os dados do questionário serão carregados.</p>
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
//	 * Método não invocado por JSP´s
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
//	 * Verifica se o usuário logado é um coordenador de extensão.
//	 * 
//	 * Método não invocado por JSP´s
//	 */
//	@Override
//	public void checkChangeRole() throws SegurancaException {
//		checkRole(SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO);
//	}
//
//	/**
//	 * Retorna o diretório base
//	 * 
//	 * Método não invocado por JSP´s
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
