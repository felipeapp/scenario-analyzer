/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/10/2009
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;

/**
 * <p>Responsável pelo cadastro das inscrições de participantes de cursos e eventos na área pública, impressão de GRU, certificados, etc...</p>
 * 
 
 * @author Daniel Augusto 
 * 
 * @deprecated Impossível de se manter.  substituído por GerenciarInscricoesCursosEventosExtensaoMBean
 */
@Scope("request")
@Component("inscricaoParticipantes")
@Deprecated
public class InscricaoParticipantesMBean extends SigaaAbstractController<InscricaoAtividadeParticipante> {
	
	
//	/** Usado para armazenar informação obtida através de uma consulta ao banco */
//	private Collection<InscricaoAtividade> inscricoesAtividades = new ArrayList<InscricaoAtividade>();
//	/** Usado para armazenar informação obtida através de uma consulta ao banco */
//	private Collection<InscricaoAtividade> inscricoesSubAtividades = new ArrayList<InscricaoAtividade>();
//	/** Atributo utilizado para armazenar informaçoes obtidas através de uma consulta ao banco */
//	private Collection<SelectItem> municipiosEndereco = new ArrayList<SelectItem>(0);
//	/** Atributo utilizado para representar a inscrição do participante */
//	private InscricaoAtividade inscricao = new InscricaoAtividade();
//	
//	/** Usado como auxiliar de tela de busca */
//	private boolean checkBuscaTitulo;
//	/** Usado como auxiliar de tela de busca */
//	private boolean checkBuscaTipoAtividade;
//	/** Usado como auxiliar de tela de busca */
//	private boolean checkBuscaArea;
//	/** Usado como auxiliar de tela de busca */
//	private boolean checkBuscaServidor;
//	/** Usado como auxiliar de tela de busca */
//	private boolean checkBuscaPeriodo;
//	/** Usado como auxiliar de tela de busca */
//	private boolean checkBuscaAbertas;
//	/** Usado para armazenar informação inserida em tela de busca */
//	private Date buscaInicio;
//	/** Usado para armazenar informação inserida em tela de busca */
//	private Date buscaFim;
//	/** Usado para armazenar informação inserida em tela de busca */
//	private String buscaNomeAtividade;
//	/** Usado para armazenar informação inserida em tela de busca */
//	private Integer buscaTipoAtividade;
//	/** Usado para armazenar informação inserida em tela de busca */
//	private Integer buscaArea;
//	/** Usado para armazenar informação inserida em tela de busca */
//	private Servidor docente = new Servidor();
//	
//	/** Atributo utilizado para informar se a mensagem é mostrada */
//	private boolean mostrarMensagem;
//	/** Atributo utilizado para informar se a Área é mostrada */
//	private boolean exibirArea;
//	/** Atributo utilizado para informar se é um novo cadastro, habilita ou deshabilita os campos do form de cadastro. */
//	private boolean novoCadastro = false;
//	/** Atributo utilizado para informar se é um participante */
//	private boolean participante;
//	/** Atributo utilizado para informar se há código de acesso */
//	private String codigoAcesso;
//	/** Atributo utilizado para informar o ID da atividade */
//	private int idAtividade;
//	/** Atributo utilizado para informar se uma inscrição já existe. */
//	private boolean jaExisteInscricaoNestaAtividade = false;
//	/** Atributo utilizado para armazenar a inscrição anterior e cancela-la caso uma nova inscrição seja feita. */
//	public InscricaoAtividadeParticipante inscricaoAnterior;
//	/** Atributo utilizado para informar se é uma subAtividade */
//	private Boolean subAtv;
//	
//	/** Atributo utilizado para informar se a taxa de inscrição foi paga ou não caso a atividad exiga. */
//	private boolean atividadePossuiCobancaTaxaInscricao;
//	
//	/** Utilizando para visualização das respostas dos questionários das inscrições. */
//	private QuestionarioRespostas questionarioRespondido;
	
//	/**
//	 * Construtor padrão
//	 */
//	public InscricaoParticipantesMBean() {
//		obj = new InscricaoAtividadeParticipante();
//		docente = new Servidor();
//		docente.setPessoa(new Pessoa());
//	}
//	
//	/**
//	 * Busca todos os períodos de inscrições criados na área pública.
//	 * <br />
//	 * Método chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>/sigaa.war/public/menus/extensao.jsp</li>
//	 * </ul>
//	 */
//	public void buscarInscricoesAreaPublica() {
//		
//		try {
//			InscricaoAtividadeExtensaoDao dao = getDAO(InscricaoAtividadeExtensaoDao.class);			
//			inscricoesAtividades = dao.findInscricoesAtividadesByFilter(true);
//			inscricoesSubAtividades = dao.findInscricoesSubAtividadesAbertas(null, null, null, null, null, true, null, null);
//			checkBuscaAbertas = true;			
//			//contabilizar quantidade de participantes aceitos
//			verificarQuantidadeAceitos();
//			
//		} catch (DAOException e) {
//			notifyError(e);
//			addMensagemErroPadrao();
//			e.printStackTrace();
//		}
//	}
//	
//	/**
//	 * Busca os períodos de inscrições mediante os filtros definidos pelo usuário.
//	 * <br />
//	 * Método chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>/sigaa.war/public/extensao/consulta_inscricoes.jsp</li>
//	 * </ul>
//	 */
//	public void buscarAcoesComInscricoes()  {
//		
//		erros.getMensagens().clear();
//
//		String titulo = null;
//		Integer idTipoAtividade = null;
//		Integer idAreaTematica = null;
//		Integer idDocente = null;
//		Date inicio = null;
//		Date fim = null;
//
//		// Definição dos filtros e validações
//		if (checkBuscaTitulo) { 
//			if(!"".equals(buscaNomeAtividade))
//				titulo = buscaNomeAtividade;
//			else {
//				addMensagem(MensagensExtensao.OPCAO_SELECIONADA_NAO_INFORMADO);
//				return;
//			}
//		}
//		if (checkBuscaTipoAtividade)
//			idTipoAtividade = buscaTipoAtividade;
//		
//		if (checkBuscaArea)
//			if(buscaArea > 0)
//				idAreaTematica = buscaArea;
//			else {
//				addMensagem(MensagensExtensao.OPCAO_SELECIONADA_NAO_INFORMADO);
//				return;
//			}
//		if (checkBuscaServidor) {
//			if (docente.getId() > 0)
//				idDocente = docente.getId();
//			else {
//				addMensagem(MensagensExtensao.OPCAO_SELECIONADA_NAO_INFORMADO);
//				return;
//			}
//		}
//		if (checkBuscaPeriodo) {
//			if (buscaInicio != null && buscaFim != null) {
//				if (buscaInicio.getTime() > buscaFim.getTime()) {
//					addMensagem(MensagensArquitetura.DATA_INICIO_MENOR_FIM, "Período");
//					return;
//				}
//				inicio = buscaInicio;
//				fim = buscaFim;
//			} else {
//				addMensagem(MensagensExtensao.OPCAO_SELECIONADA_NAO_INFORMADO);
//				return;
//			}
//		}
//		try {
//			inscricoesAtividades = getDAO(InscricaoAtividadeExtensaoDao.class).findInscricoesAtividadesByFilter(titulo, idTipoAtividade, 
//						idAreaTematica, idDocente, FuncaoMembro.COORDENADOR, checkBuscaAbertas, inicio, fim);
//			
//			inscricoesSubAtividades = getDAO(InscricaoAtividadeExtensaoDao.class).findInscricoesSubAtividadesAbertas(
//					titulo, idTipoAtividade, idAreaTematica, idDocente, FuncaoMembro.COORDENADOR, checkBuscaAbertas, inicio, fim);
//			
//			if (inscricoesAtividades.isEmpty() && inscricoesSubAtividades.isEmpty()) {
//				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
//				return;
//			}
//			verificarQuantidadeAceitos();
//		} catch (DAOException e) {
//			notifyError(e);
//			e.printStackTrace();
//		}
//	}
//	
//	/**
//	 * Realiza configurações para criar uma inscrição de participante.
//	 * <br />
//	 * Método chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>/sigaa.war/public/extensao/consulta_inscricoes.jsp</li>
//	 * </ul>
//	 * 
//	 * @throws ArqException
//	 */
//	public void iniciarInscricaoParticipante() throws ArqException {
//
//		if (!operacaoValida()) {
//			addMensagem(MensagensExtensao.OPERACAO_INVALIDA);
//			return;
//		}
//		
//		if ( obj.getInscricaoAtividade().getQuestionario() !=  null ) {
//			((QuestionarioRespostasMBean) getMBean("questionarioRespostasBean")).inicializarQuestionarioAtividade(obj);
//		}
//		
//		
//		prepareMovimento(SigaaListaComando.CADASTRAR_INSCRICAO_PARTICIPANTE);
//		setOperacaoAtiva(SigaaListaComando.CADASTRAR_INSCRICAO_PARTICIPANTE.getId());
//		if (!getParameterBoolean("flag"))
//			addMensagem(MensagensExtensao.INSCRICAO_NUMERO_VAGAS_ESGOTADO);
//		novoCadastro = false;
//		inscricaoAnterior = null;
//		carregarMunicipios();
//	}
//	
//	
//	
//	/**
//	 * Realiza configurações para criar uma inscrição de participante.
//	 * <br />
//	 * Método chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>/sigaa.war/public/extensao/consulta_inscricoes.jsp</li>
//	 * </ul>
//	 * 
//	 * @throws ArqException
//	 */
//	public void iniciarInscricaoParticipanteSubAtividade() throws ArqException {
//		
//		
//		final Integer id = getParameterInt("id");		
//		obj.setInscricaoAtividade(getGenericDAO().findByPrimaryKey(id,InscricaoAtividade.class));	
//		prepareMovimento(SigaaListaComando.CADASTRAR_INSCRICAO_PARTICIPANTE_SUB_ATIVIDADE);
//		setOperacaoAtiva(SigaaListaComando.CADASTRAR_INSCRICAO_PARTICIPANTE_SUB_ATIVIDADE.getId());
//		if (!getParameterBoolean("flag"))
//			addMensagem(MensagensExtensao.INSCRICAO_NUMERO_VAGAS_ESGOTADO);
//		novoCadastro = false;
//		inscricaoAnterior = null;
//		carregarMunicipios();
//	}
//	
//	
//	
//
//	/**
//	 * Realiza configurações para reenvio de nova senha de acesso ao curso/evento de extensão.
//	 * <br />
//	 * Método chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 * 		<li>/sigaa.war/public/extensao/consulta_inscricoes.jsp</li>
//	 * </ul>
//	 * 
//	 * @throws ArqException
//	 */
//	public void iniciarReenviarSenha() throws ArqException {
//
//		subAtv = getParameterBoolean("subAtv");
//		
//		if (!operacaoValida()) {
//			addMensagem(MensagensExtensao.OPERACAO_INVALIDA);
//			return;
//		}
//		prepareMovimento(SigaaListaComando.REENVIAR_SENHA_PARTICIPANTE_EXTENSAO);
//		setOperacaoAtiva(SigaaListaComando.REENVIAR_SENHA_PARTICIPANTE_EXTENSAO.getId());		
//	}
//	
//	/**
//	 * Permite o reenvio do código e senha para o participante que perdeu.
//	 * <br />
//	 * Método chamado pela(s) seguinte(s) JSP(s): 
//	 * <ul>
//	 * 		<li>/sigaa.war/extensao/InscricaoOnline/gerenciar_participantes.jsp</li>
//	 * </ul>
//	 *   
//	 * @return
//	 * @throws NegocioException
//	 * @throws ArqException
//	 */
//	public String reenviarSenhaAcesso() throws NegocioException, ArqException {
//		if (obj.getInscricaoAtividade().getId() == 0) {
//			addMensagem(MensagensExtensao.OPERACAO_INVALIDA);
//			return null;
//		}
//		if ("".equals(obj.getEmail())) {
//			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "E-mail");
//			return null;
//		}
//	    
//		if ("".equals(obj.getDataNascimento())) {
//			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data de Nascimento");
//			return null;
//		}
//	    
//	    	InscricaoAtividadeParticipante iap = getDAO(InscricaoAtividadeParticipanteDao.class)
//	    			.findByEmailAcao(obj.getEmail(), obj.getDataNascimento(), obj.getInscricaoAtividade().getId());
//		return reenviarSenhaAcesso(iap);
//	}
//
//	
//	/**
//	 * Permite o reenvio do código e senha para o participante que perdeu.
//	 * <br />
//	 * Método chamado pela(s) seguinte(s) JSP(s): 
//	 * <ul>
//	 * 		<li>/sigaa.war/extensao/InscricaoOnline/inscritos.jsp</li>
//	 * </ul>
//	 *   
//	 * @return
//	 * @throws NegocioException
//	 * @throws ArqException
//	 */
//	public String reenviarSenhaParticipanteOnline() throws NegocioException, ArqException {
//	    	int id = getParameterInt("id", 0);
//	    	InscricaoAtividadeParticipante iap = getGenericDAO().findByPrimaryKey(id, InscricaoAtividadeParticipante.class);
//		return reenviarSenhaAcesso(iap);
//	}
//
//	/**
//	 * Reenvia a senha de acesso do participante informado.
//	 * 
//	 * @param iap
//	 * @return
//	 * @throws NegocioException
//	 * @throws ArqException
//	 */
//	private String reenviarSenhaAcesso(InscricaoAtividadeParticipante iap) throws NegocioException, ArqException {
//	    if (iap != null) {
//			MovimentoCadastro mov = new MovimentoCadastro();
//			prepareMovimento(SigaaListaComando.REENVIAR_SENHA_PARTICIPANTE_EXTENSAO);
//			mov.setCodMovimento(SigaaListaComando.REENVIAR_SENHA_PARTICIPANTE_EXTENSAO);
//			mov.setObjMovimentado(iap);
//			execute(mov);
//			addMensagemInformation("ATENÇÃO: Esse email se refere ao email informado no momento da inscrição. Para alterá-lo utilize a opção \"Alterar inscriações <i>on-line</i>\" ");
//			addMensagemInformation("Senha de acesso a área pública de Cursos e Eventos de Extensão reenviada para: " + iap.getEmail());
//	    }else {
//	    	addMensagemErro("Usuário não encontrado entre os participantes que realizaram inscrição para a ação selecionada.");
//	    }
//	    return null;
//	}
//
//
//	
//	
//	/**
//	 * Carrega os municípios de uma unidade federativa.
//	 * <br />
//	 * Método chamado pela(s) seguinte(s) JSP(s): 
//	 * <ul>
//	 * 		<li>/sigaa.war/public/extensao/form_inscricao.jsp</li>
//	 * </ul>
//	 * 
//	 * @throws DAOException
//	 */
//	public void carregarMunicipios() throws DAOException {		
//		MunicipioDao dao = getDAO(MunicipioDao.class);
//		UnidadeFederativa uf = dao.findByPrimaryKey(obj.getUnidadeFederativa().getId(), UnidadeFederativa.class);
//		Collection<Municipio> municipios = dao.findByUF(uf.getId());
//		municipiosEndereco = new ArrayList<SelectItem>();
//		if (uf.getCapital() != null) {
//			municipiosEndereco.add(new SelectItem(uf.getCapital().getId(), uf.getCapital().getNome()));		
//		}
//		municipiosEndereco.addAll(toSelectItems(municipios, "id", "nome"));
//	}
//	
//	/**
//	 * Inicia novo cadastro de participante.
//	 * <br>
//	 *  Método chamado pela(s) seguinte(s) JSP(s):
//	 * <ul>
//	 *  <li>sigaa.war/public/extensao/form_inscricao.jsp</li>
//	 * <ul>
//	 * @param event
//	 */
//	public void novoCadastroParticipante(ActionEvent event)  {
//	    novoCadastro = true;
//	}
//	
//	/**
//	 * Verifica através do número do CPF informado se o usuário já possui seus dados cadastrados.
//	 * <br />
//	 * Método chamado pela(s) seguinte(s) JSP(s): 
//	 * <ul>
//	 * 		<li>/sigaa.war/public/extensao/form_inscricao.jsp</li>
//	 * </ul>
//	 * 
//	 * @return
//	 * @throws NegocioException
//	 * @throws ArqException
//	 */
//	public void verificarInscricaoExistente(ActionEvent event) throws NegocioException, ArqException {
//	    	
//		if (!operacaoValida()) {
//			addMensagem(MensagensExtensao.OPERACAO_INVALIDA);
//			return;
//		}
//		
//		InscricaoAtividadeParticipanteDao dao = getDAO(InscricaoAtividadeParticipanteDao.class);
//		ListaMensagens lista = new ListaMensagens();
//		inscricaoAnterior = null;
//		
//		// Caso seja um participante Nacional
//		if (!obj.isInternacional()) {
//			// VALIDA CPF
//			if(!ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(obj.getCpf()))
//				addMensagem(MensagensArquitetura.FORMATO_INVALIDO, "CPF");
//			
//			// Procurar dados existentes
//			InscricaoAtividadeParticipante dadosRecentesParticipante = dao.findDadosRecentesParticipanteByCpf(obj.getCpf());
//			
//			//Verifica se é um novo cadastro
//			if (dadosRecentesParticipante == null) { 
//				novoCadastro = true; // libera os campos do form para inclusão de novo participante.
//				addMensagemWarning("Nenhum inscrição foi encontrada.");
//			} else {
//				novoCadastro = false;
//				InscricaoAtividadeParticipanteValidator.validaInscricaoSelecaoNacional(obj, lista);				
//				if(!lista.isEmpty()){
//					addMensagens(lista);
//					return;
//				}
//				
//				// Preenche os campos do form com os dados do usuário encontrado no sistema.
//				carregarInformacoesAnterioresInscricao(dadosRecentesParticipante); 
//				
//				// Caso não tenha erro de validação será verificado se o participante já submeteu alguma inscrição nessa ação de extensão
//				// Recupera a inscrição submetida por esse usuário nessa ação para ser removida
//				inscricaoAnterior = dao.findInscricaoParticipanteByStatus(obj.getInscricaoAtividade().getId(), obj.getCpf(), StatusInscricaoParticipante.INSCRITO);
//			}
//			
//		// Verifica quando for Internacional
//		} else if (obj.isInternacional()) {
//			
//			// Procurar dados existentes
//			InscricaoAtividadeParticipante dadosRecentesParticipante  = dao.findDadosRecentesParticipanteByPassaporte(obj.getPassaporte());
//			
//			// Verifica se é um novo cadastro
//			if (dadosRecentesParticipante == null) {
//				novoCadastro = true;
//				
//			} else {
//				novoCadastro = false;
//				InscricaoAtividadeParticipanteValidator.validaInscricaoSelecaoInternacional(obj, lista);
//				if (!lista.isEmpty()){
//					addMensagens(lista);
//					return;
//				}
//				
//				// Preenche os campos do form com os dados do usuário encontrado no sistema.
//				carregarInformacoesAnterioresInscricao(dadosRecentesParticipante);
//				
//				// Caso não tenha erro de validação será verificado se o participante já submeteu alguma inscrição nessa ação de extensão
//				inscricaoAnterior = dao.findInscricaoParticipanteByStatusEstrangeiro(obj.getInscricaoAtividade().getId(), obj.getPassaporte(), StatusInscricaoParticipante.INSCRITO);				
//			}
//		}
//		
//		
//		if (inscricaoAnterior != null) {
//			jaExisteInscricaoNestaAtividade = true; // Informa que já existe uma submissão de inscrição e avisa ao usuário que a inscrição anterior será cancelada.
//		} else {
//			jaExisteInscricaoNestaAtividade = false;
//		}
//
//	}
//	
//	/**
//	 * Verifica se os dados informado no formulários estão corretos antes de criar uma inscrição para participante.
//	 * <br />
//	 * Método chamado pela(s) seguinte(s) JSP(s): 
//	 * <ul>
//	 * 		<li>/sigaa.war/public/extensao/consulta_inscricoes.jsp</li>
//	 * </ul>
//	 * 
//	 * @return
//	 * @throws NegocioException
//	 * @throws ArqException
//	 */
//	public String criarInscricaoParticipante() throws NegocioException, ArqException {
//		
//		erros.getMensagens().clear();
//		ListaMensagens erros = new ListaMensagens();
//		
//		if(obj.isInternacional())	
//			obj.setCpf(null);
//		else { 
//			obj.setPassaporte(null);
//		}
//		
//		if (!operacaoValida()) {
//			addMensagem(MensagensExtensao.OPERACAO_INVALIDA);
//			redirect("/public/home.jsf");
//		}		
//		
//		erros.addAll(obj.validate());
//		
//		if( ! ValidatorUtil.isEmpty(obj.getInscricaoAtividade().getQuestionario())) {
//			erros.addAll(((QuestionarioRespostasMBean) getMBean("questionarioRespostasBean")).getObj().validate());
//		}
//		
//		if (!erros.isEmpty()) {
//			addMensagens(erros);
//			return null;
//		}
//		
//		// Reverifica se realmente não está cadastrado caso o usuário não tenha clicado no botão lupaCpf
//		// Caso já tenha achada uma inscrição de mesmo cpf não faz nada, caso não, procura se tem inscrição igual e armazena para ser desativada no executarCadastro() 
//		this.verificarInscricaoDuplicada();
//		
//		
//		if (obj.getInscricaoAtividade().getQuestionario() != null) {
//			obj.setQuestionarioRespostas(((QuestionarioRespostasMBean) getMBean("questionarioRespostasBean")).getObj());
//		}
//		
//		
//		return executarCadastro();
//	}
//	
//	
//	/**
//	 * Verifica através do número do CPF informado se o usuário já possui seus dados cadastrados.
//	 * <br />
//	 * Método auxiliar chamado pelo método criarInscricaoParticipante() 
//	 * 
//	 * <p>Método não chamado por JSP.</p>
//	 * 
//	 * @return
//	 * @throws NegocioException
//	 * @throws ArqException
//	 */
//	public void verificarInscricaoDuplicada() throws NegocioException, ArqException {
//		// Se ja descobriu que já foi inscrito não precisa repetir o processo
//		if(inscricaoAnterior != null) return;
//		
//		InscricaoAtividadeParticipanteDao dao = getDAO(InscricaoAtividadeParticipanteDao.class);
//		
//		// Caso seja um participante Nacional
//		if (!obj.isInternacional()) {		
//				// Caso não tenha erro de validação será verificado se o participante já submeteu alguma inscrição nessa ação de extensão
//				// Recupera a inscrição submetida por esse usuário nessa ação para ser removida
//				inscricaoAnterior = dao.findInscricaoParticipanteByStatus(obj.getInscricaoAtividade().getId(), obj.getCpf(), StatusInscricaoParticipante.INSCRITO);
//		
//			
//		// Verifica quando for Internacional
//		} else if (obj.isInternacional()) {	
//			
//				// Caso não tenha erro de validação será verificado se o participante já submeteu alguma inscrição nessa ação de extensão
//				inscricaoAnterior = dao.findInscricaoParticipanteByStatusEstrangeiro(obj.getInscricaoAtividade().getId(), obj.getPassaporte(), StatusInscricaoParticipante.INSCRITO);				
//			
//		}
//				
//		if (inscricaoAnterior != null) {
//			// Informa que já existe uma submissão de inscrição e avisa ao usuário que a inscrição anterior será cancelada.
//			jaExisteInscricaoNestaAtividade = true; 
//		} else {
//			jaExisteInscricaoNestaAtividade = false;
//		}
//
//	}
//	
//	
//	/**
//	 * Verifica se os dados informado no formulários estão corretos antes de criar uma inscrição para participante.
//	 * <br />
//	 * Método chamado pela(s) seguinte(s) JSP(s): 
//	 * <ul>
//	 * 		<li>/sigaa.war/public/extensao/consulta_inscricoes.jsp</li>
//	 * </ul>
//	 * 
//	 * @return
//	 * @throws NegocioException
//	 * @throws ArqException
//	 */
//	public String criarInscricaoParticipanteSubAtividade() throws NegocioException, ArqException {
//		
//		erros.getMensagens().clear();
//		ListaMensagens erros = new ListaMensagens();
//		
//		if(obj.isInternacional())	
//			obj.setCpf(null);
//		else { 
//			obj.setPassaporte(null);
//		}
//		
//		// Verifica se o usuário tem CPF para pode se inscrever em Cursos e Evento que exigem pagamento //
//		if(obj.getInscricaoAtividade().getSubAtividade().getAtividade().getCursoEventoExtensao().isCobrancaTaxaMatricula() && ValidatorUtil.isEmpty(obj.getCpf()) ) {
//			addMensagemErro("Caro usuário, essa atividade de extensão exige a cobrança de um taxa de matrícula, é necessário " +
//					" informar o CPF para pode emitir a GRU e realizar o seu pagamento.");
//			return null;
//		}
//				
//		
//		erros.addAll(obj.validate());
//		
//		if (!erros.isEmpty()) {
//			addMensagens(erros);
//			return null;
//		}
//		
//		// Reverifica se realmente não está cadastrado caso o usuário não tenha clicado no botão lupaCpf
//		// Caso já tenha achada uma inscrição de mesmo cpf não faz nada, caso não, procura se tem inscrição igual e armazena para ser desativada no executarCadastro() 
//		this.verificarInscricaoDuplicada();
//		
//		MovimentoCadastro mov = new MovimentoCadastro();
//		mov.setCodMovimento(SigaaListaComando.CADASTRAR_INSCRICAO_PARTICIPANTE_SUB_ATIVIDADE);
//		mov.setObjMovimentado(obj);
//		mov.setObjAuxiliar(inscricaoAnterior); //inscrição que deverá ser cancelada, se existir.
//		execute(mov);
//		addMensagem(MensagensExtensao.INSCRICAO_EFETUADA_COM_SUCESSO);
//		removeOperacaoAtiva();
//		resetBean();
//		clear();
//		return "pretty:buscaInscricoesOnline";
//	}
//	
//	/**
//	 * Verificar autenticidade do id passado como parâmetro.
//	 * 
//	 * @return
//	 * @throws DAOException
//	 */
//	private boolean operacaoValida() throws DAOException {
//		
//		final Integer id = getParameterInt("id");
//		if (id == null) return false;
//		obj.setInscricaoAtividade(getGenericDAO().findAndFetch(id,InscricaoAtividade.class,"questionario"));
//		return (obj.getInscricaoAtividade() == null) ? false : true;
//	}
//	
//	/**
//	 * Efetua o cadastro de uma inscrição para participante.
//	 * 
//	 * @return
//	 * @throws NegocioException
//	 * @throws ArqException
//	 */
//	private String executarCadastro() throws NegocioException, ArqException {
//		
//		MovimentoCadastro mov = new MovimentoCadastro();
//		mov.setCodMovimento(SigaaListaComando.CADASTRAR_INSCRICAO_PARTICIPANTE);
//		mov.setObjMovimentado(obj);
//		mov.setObjAuxiliar(inscricaoAnterior); //inscrição que deverá ser cancelada, se existir.
//		execute(mov);
//		addMensagem(MensagensExtensao.INSCRICAO_EFETUADA_COM_SUCESSO);
//		removeOperacaoAtiva();
//		resetBean();
//		clear();
//		return "pretty:buscaInscricoesOnline";
//	}
//
//	
//	/**
//	 * Valida o código de acesso informado.
//	 * <br />
//	 * Método chamado pela(s) seguinte(s) JSP(s): 
//	 * <ul>
//	 * 		<li>/sigaa/link/public/confirmarInscricao/</li>
//	 * </ul>
//	 * 
//	 * @return
//	 * @throws ArqException
//	 */
//	public String validarCodigoAcesso() throws ArqException {
//		
//		if (codigoAcesso.length() == 40) {
//			obj = getDAO(InscricaoAtividadeParticipanteDao.class)
//					.findInscricaoParticipanteByCodigoAcesso(codigoAcesso, idAtividade);
//			if (obj != null) {
//				prepareMovimento(SigaaListaComando.CONFIRMAR_INSCRICAO_PARTICIPANTE);
//				return "pretty:submeterSenha";
//			}
//		}
//		addMensagem(MensagensExtensao.URL_CONFIRMACAO_INSCRICAO_INVALIDA);
//		return redirect("/public/home.jsf");
//	}
//	
//	/**
//	 * Confirma a inscrição caso a senha informada esteja correta.
//	 * <br />
//	 * Método chamado pela(s) seguinte(s) JSP(s): 
//	 * <ul>
//	 * 		<li>/sigaa.war/public/extensao/form_senha.jsp</li>
//	 * </ul>
//	 * 
//	 * @throws ArqException
//	 */
//	public void confirmarInscricao() throws ArqException {
//		
//		if (obj.getId() == 0) {
//			addMensagem(MensagensExtensao.OPERACAO_INVALIDA);
//			return;
//		}
//		if ("".equals(obj.getSenha())) {
//			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Senha");
//			return;
//		}
//		
//		if(!ValidatorUtil.isEmpty(obj.getPassaporte())){
//		    if (getDAO(InscricaoAtividadeParticipanteDao.class).findInscricaoParticipanteByStatusEstrangeiro(
//			    obj.getInscricaoAtividade().getId(), obj.getPassaporte(), StatusInscricaoParticipante.APROVADO) != null) {
//			addMensagem(MensagensExtensao.INSCRITO_EH_PARTICIPANTE_ACAO);
//			//enviarEmail(obj, "participação");
//			return;
//		    }
//		}else{
//
//		    if (getDAO(InscricaoAtividadeParticipanteDao.class).findInscricaoParticipanteByStatus(
//			    obj.getInscricaoAtividade().getId(), obj.getCpf(), StatusInscricaoParticipante.APROVADO) != null) {
//			addMensagem(MensagensExtensao.INSCRITO_EH_PARTICIPANTE_ACAO);
//			//enviarEmail(obj, "participação");
//			return;
//		    }
//
//		}
//		if (getDAO(InscricaoAtividadeParticipanteDao.class).confirmarInscricaoParticipanteBySenha(obj)) {
//			MovimentoCadastro mov;
//			if(ValidatorUtil.isEmpty(obj.getPassaporte())){
//			    if (getDAO(InscricaoAtividadeParticipanteDao.class).findInscricaoParticipanteByStatus(
//					obj.getInscricaoAtividade().getId(), obj.getCpf(), 
//					StatusInscricaoParticipante.CONFIRMADO) != null)
//			    {
//				addMensagem(MensagensExtensao.INSCRICAO_ATIVA_RECENTE_EXISTENTE);
//				return;
//			    }
//			}else{
//			    if (getDAO(InscricaoAtividadeParticipanteDao.class).findInscricaoParticipanteByStatusEstrangeiro(
//					obj.getInscricaoAtividade().getId(), obj.getPassaporte(), 
//					StatusInscricaoParticipante.CONFIRMADO) != null)
//			    {
//				addMensagem(MensagensExtensao.INSCRICAO_ATIVA_RECENTE_EXISTENTE);
//				return;
//			    }
//			}
//		    
//			try {
//				mov = new MovimentoCadastro();
//				mov.setCodMovimento(SigaaListaComando.CONFIRMAR_INSCRICAO_PARTICIPANTE);
//				mov.setObjMovimentado(obj);
//				execute(mov);
//				mostrarMensagem = true;
//				addMensagem(MensagensExtensao.INSCRICAO_CONFIRMADA_SUCESSO);
//			} catch (Exception e) {
//				notifyError(e);
//				addMensagemErroPadrao();
//				e.printStackTrace();
//			}
//		} else {
//			addMensagem(MensagensExtensao.ERRO_CONFIRMACAO_INSCRICAO);
//		}
//	}
//
//	/**
//	 * <p>Valida o login do inscrito ou participante pelo email e senha informados.</p>
//	 * 
//	 * <p>Esse método realiza o login na áreas dos usuários inscritos em extensão. É o método chamado quando
//	 *  o usuário clica no botão "logar"</p>
//	 * 
//	 * <br />
//	 * Método chamado pela(s) seguinte(s) JSP(s): 
//	 * <ul>
//	 * 		<li>/sigaa.war/public/extensao/login_inscricao.jsp</li>
//	 * </ul>
//	 * 
//	 * @throws DAOException
//	 */
//	public void acessarAreaParticipante() throws DAOException {
//
//		if (!"".equals(obj.getSenha()) && !"".equals(obj.getEmail())) {
//			
//			obj = getDAO(InscricaoAtividadeParticipanteDao.class).findInscricaoParticipanteByLogin(
//					obj.getEmail(), obj.getSenha());
//			if (obj != null) {
//				
//				if (obj.getStatusInscricao().getId() == StatusInscricaoParticipante.CANCELADO) 
//					addMensagem(MensagensExtensao.INSCRICAO_CANCELADA_ACESSO_SUSPENSO);
//
//				else if (obj.getStatusInscricao().getId() == StatusInscricaoParticipante.INSCRITO) 
//					addMensagem(MensagensExtensao.INSCRICAO_NAO_CONFIRMADA_ACESSO_SUSPENSO);
//				
//				else if (obj.getStatusInscricao().getId() == StatusInscricaoParticipante.RECUSADO) 
//					addMensagem(MensagensExtensao.INSCRICAO_RECUSADA_ACESSO_SUSPENSO);
//				
//				else if (obj.getStatusInscricao().getId() == StatusInscricaoParticipante.CONFIRMADO) {
//					participante = false;
//					exibirArea = true;
//					addMensagem(MensagensExtensao.INSCRICAO_PENDENTE_APROVACAO_COORDENDADOR);
//				}
//				else if (obj.getStatusInscricao().getId() == StatusInscricaoParticipante.APROVADO
//						&& obj.getParticipanteExtensao() != null) {
//					participante = true;
//					exibirArea = true;
//					addMensagem(MensagensExtensao.ACEITA_INSCRICAO_PARA_PARTICIPANTE);
//				}
//				try {
//					prepareMovimento(SigaaListaComando.CANCELAR_INSCRICAO_PARTICIPANTE);
//				} catch (ArqException e) {
//					notifyError(e);
//					addMensagemErroPadrao();
//					e.printStackTrace();
//				}
//				
//				
//				CursoEventoExtensao cursoEvento = null;
//				InscricaoAtividadeParticipante iap = getGenericDAO().findByPrimaryKey(obj.getId(), InscricaoAtividadeParticipante.class);
//				if(ValidatorUtil.isEmpty( iap.getInscricaoAtividade().getAtividade())) {
//					cursoEvento = iap.getInscricaoAtividade().getSubAtividade().getAtividade().getCursoEventoExtensao();
//				} else {
//					cursoEvento = iap.getInscricaoAtividade().getAtividade().getCursoEventoExtensao();
//				}
//				
//				
//				// para visualizar se a GRU foi pagae //
//				if( cursoEvento.isCobrancaTaxaMatricula()){
//					atividadePossuiCobancaTaxaInscricao = true;
//				} else {
//					atividadePossuiCobancaTaxaInscricao = false;
//				}
//				
//				return;
//			} else {
//				obj = new InscricaoAtividadeParticipante();
//			}
//		}
//		addMensagem(MensagensExtensao.EMAIL_SENHA_INVALIDOS);
//	}
//	
//	/**
//	 * Cancela a inscrição para participante pelo usuário.
//	 * <br />
//	 * Método chamado pela(s) seguinte(s) JSP(s): 
//	 * <ul>
//	 * 		<li>/sigaa.war/public/extensao/login_inscricao.jsp</li>
//	 * </ul>
//	 * 
//	 */
//	public void cancelarInscricao() {
//		
//		if (isParticipante()) {		
//			addMensagem(MensagensExtensao.NAO_EH_PERMITIDO_CANCELAR_INSCRICAO_ACEITA);
//			return;
//		}
//		try {
//			MovimentoCadastro mov = new MovimentoCadastro();
//			mov.setCodMovimento(SigaaListaComando.CANCELAR_INSCRICAO_PARTICIPANTE);
//			mov.setObjMovimentado(obj);
//			execute(mov);
//			addMensagem(MensagensExtensao.INSCRICAO_CANCELADA_SUCESSO);
//			redirect("/public/home.jsf");
//			//mostrarMensagem = true;
//		} catch (Exception e) {
//			notifyError(e);
//			addMensagemErroPadrao();
//		}
//	}
//	
//	
//	/**
//	 *  <p>Emite a GRU para o usuário pagar a multa</p>
//	 *  
//	 *  <br/>
//	 *  Método chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *    <li>/sigaa.war/public/extensao/login_inscricao.jsp</li>
//	 *   </ul>
//	 *
//	 * @throws ArqException 
//	 * @throws NegocioException 
//	 *
//	 */
//	public String emitirGRU() throws ArqException, NegocioException{
//		
//		if(obj == null){
//			addMensagemErro("Erro ao selecionar a inscrição para ser paga. Por favor, reinicie o processo! "); // Mesagem que não era para ser mostrada
//			return null;
//		}
//		
//		if ( !isEmpty( obj.getPassaporte() ) ) {
//			addMensagemInformation("A emissão da GRU só pode ser feita para as inscrições realizadas utilizando CPF. Entre em contato com o coordenador da ação, " +
//					"para efetuar o pagamento.");
//			return null;
//		}
//		
//		// Não precisa, não é mais impresso na GRU
//		//obj.setInfoIdentificacaoMultaGerada( MultaUsuarioBibliotecaUtil.montaInformacoesMultaComprovante(obj, false));
//		
//		if(obj.getIdGRUPagamento() == null){ // A GRU não foi gerada ainda
//			
//			try {
//			
//				MovimentoCadastro movGRU = new MovimentoCadastro(obj);
//				movGRU.setCodMovimento(SigaaListaComando.CRIA_GRU_CURSOS_EVENTOS_EXTENSAO);
//			
//				obj = (InscricaoAtividadeParticipante) execute(movGRU);    // A multa com o id da GRU gerada
//				
//			} catch (NegocioException ne) {
//				addMensagens(ne.getListaMensagens());
//				return null;
//			} finally{
//				prepareMovimento(SigaaListaComando.CRIA_GRU_CURSOS_EVENTOS_EXTENSAO);  // prepara o próximo, caso o usuário tente reimprimir
//			}  
//			
//			
//		}
//		
//		try{
//			getCurrentResponse().setContentType("application/pdf");
//			getCurrentResponse().addHeader("Content-Disposition", "attachment;filename=GRU.pdf");
//			GuiaRecolhimentoUniaoHelper.gerarPDF(getCurrentResponse().getOutputStream(), obj.getIdGRUPagamento());
//			FacesContext.getCurrentInstance().responseComplete();
//			
//		} catch (IOException e) {
//			addMensagemErro("Erro ao tentar gerar a GRU, por favor tente novamente, caso o problema persista, contacte o suporte.");
//		}
//		
//		return null; // fica na mesma página
//		
//	}
//	
//	
//	/**
//	 * Imprime o certificado de participação na ação de extensão caso o participante esteja apto a recebê-lo.
//	 * <br />
//	 * Método chamado pela(s) seguinte(s) JSP(s): 
//	 * <ul>
//	 * 		<li>/sigaa.war/public/extensao/login_inscricao.jsp</li>
//	 * </ul>
//	 * 
//	 * @throws DAOException
//	 * @throws SegurancaException
//	 */
//	public void imprimirCertificado() throws DAOException, SegurancaException {
//		
//	    if (obj.getParticipanteExtensao() == null) {
//		addMensagem(MensagensExtensao.NAO_EH_POSSIVEL_IMPRIMIR_CERTIFICADO_PENDENTE_ACAO);
//		return;
//	    }
//	    
//	    obj.setParticipanteExtensao( getGenericDAO().findByPrimaryKey(obj.getParticipanteExtensao().getId(), ParticipanteAcaoExtensao.class) );
//	    if (!isParticipante()) {
//		addMensagem(MensagensExtensao.NAO_EH_POSSIVEL_IMPRIMIR_CERTIFICADO_POR_NAO_PARTICIPANTE);
//		return;
//	    }
//	    if (obj.getParticipanteExtensao().isPassivelEmissaoCertificado()) {
//		CertificadoParticipanteExtensaoMBean certificadoBean = new CertificadoParticipanteExtensaoMBean();
//		certificadoBean.setParticipante(obj.getParticipanteExtensao());
//		certificadoBean.emitirCertificado();
//	    } else {
//		addMensagem(MensagensExtensao.NAO_EH_POSSIVEL_IMPRIMIR_CERTIFICADO_PENDENTE_ACAO);
//		return;
//	    }
//	}
//	
//	/**
//	 * Sai da área de inscritos.
//	 * <br />
//	 * Método chamado pela(s) seguinte(s) JSP(s): 
//	 * <ul>
//	 * 		<li>/sigaa.war/public/extensao/login_inscricao.jsp</li>
//	 * </ul>
//	 * 
//	 * @return
//	 */
//	public String sair() {
//		return redirect("/public/home.jsf");
//	}
//	
//	/**
//	 * Limpa os dados do mbean.
//	 */
//	private void clear() {
//		obj = new InscricaoAtividadeParticipante();
//		checkBuscaArea = false;
//		checkBuscaPeriodo = false;
//		checkBuscaServidor = false;
//		checkBuscaTipoAtividade = false;
//		checkBuscaTitulo = false;
//	}
//	
//	/**
//	 * Cancela o cadastro da inscrição para participante.
//	 * <br />
//	 * Método chamado pela(s) seguinte(s) JSP(s): 
//	 * <ul>
//	 * 		<li>/sigaa.war/public/extensao/form_inscricao.jsp</li>
//	 * </ul>
//	 * 
//	 */
//	public String cancelar() {
//		resetBean();
//		return forward("/public/index.jsp");
//	}
//	
//	/**
//	 * Verifica o número de inscritos que fora aceitos como participantes.
//	 * 
//	 * @throws DAOException
//	 */
//	@Deprecated
//	private void verificarQuantidadeAceitos() throws DAOException {
//		//InscricaoAtividadeParticipanteDao dao = getDAO(InscricaoAtividadeParticipanteDao.class);
//		//for (InscricaoAtividade inscricao : inscricoesAtividades) 
//		//	inscricao.calcularNumeroVagas(dao.countNumeroParticipantes(inscricao.getId()));		
//		//for (InscricaoAtividade inscricao : inscricoesSubAtividades) 
//		//	inscricao.calcularNumeroVagas(dao.countNumeroParticipantes(inscricao.getId()));
//	}
//	
//	/**
//	 * Envia email para os inscritos na ação.
//	 * 
//	 * @param p
//	 * @param status
//	 */
//	/*private void enviarEmail(InscricaoAtividadeParticipante p, String status) {
//		MailBody mail = new MailBody();
//		mail.setContentType(MailBody.TEXT_PLAN);
//		mail.setAssunto("Informação de Senha - MENSAGEM AUTOMÁTICA");
//		mail.setMensagem("Prezado " + p.getNome() + ",\n\n \tAcesse a área de inscritos pelo portal público do " +
//				"SIGAA para acompanhamento de sua " + status + " com a senha informada abaixo.\n\n Senha: " + 
//				p.getSenha());
//		mail.setEmail(p.getEmail());
//		mail.setNome(p.getNome());
//		Mail.send(mail);
//	}*/
//	
//	/**
//	 * Carrega as informações pessoais apartir de uma inscrição anterior.
//	 * 
//	 * @param inscricaoAnterior
//	 */
//	private void carregarInformacoesAnterioresInscricao(InscricaoAtividadeParticipante inscricaoAnterior) {
//		
//		obj.setNome(inscricaoAnterior.getNome());
//		obj.setCpf(inscricaoAnterior.getCpf());
//		obj.setPassaporte(inscricaoAnterior.getPassaporte());
//		obj.setDataNascimento(inscricaoAnterior.getDataNascimento());
//		obj.setInstituicao(inscricaoAnterior.getInstituicao());
//		obj.setLogradouro(inscricaoAnterior.getLogradouro());
//		obj.setNumero(inscricaoAnterior.getNumero());
//		obj.setBairro(inscricaoAnterior.getBairro());
//		obj.setMunicipio(inscricaoAnterior.getMunicipio());
//		obj.setUnidadeFederativa(inscricaoAnterior.getUnidadeFederativa());
//		obj.setCep(inscricaoAnterior.getCep());
//		obj.setEmail(inscricaoAnterior.getEmail());
//		obj.setTelefone(inscricaoAnterior.getTelefone());
//		obj.setCelular(inscricaoAnterior.getCelular());
//		obj.setDiscente(inscricaoAnterior.getDiscente());
//		obj.setServidor(inscricaoAnterior.getServidor());
//	}
//	
//	
//	
//    /**
//     * Adiciona um arquivo a inscrição.
//     * <br>
//     * Método chamado pela(s) seguinte(s) JSP(s): 
//     * <ul>
//	 * 		<li>/sigaa.war/public/extensao/form_inscricao.jsp</li>
//     * </ul>
//     * 
//     * @return Retorma para mesma página permitindo a inclusão de novo arquivo.
//     */
//    public String anexarArquivo() {
//    	try {
//
//    		if ((obj.getDescricaoArquivo() == null) || ("".equals(obj.getDescricaoArquivo().trim()))) {
//    			addMensagemErro("Descrição do arquivo é obrigatória.");
//    			return null;
//    		}
//
//    		if ((obj.getFile() == null) || (obj.getFile().getBytes() == null)) {
//    			addMensagemErro("Informe o nome completo do arquivo.");
//    			return null;
//    		}
//
//    		int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
//    		EnvioArquivoHelper.inserirArquivo(idArquivo, obj.getFile().getBytes(), obj.getFile().getContentType(), obj.getFile().getName());
//    		obj.setIdArquivo(idArquivo);
//    		addMensagemInformation("Arquivo Anexado com Sucesso.");
//
//    	} catch (Exception e) {
//    		notifyError(e);
//    		addMensagemErro(e.getMessage());
//    	}
//
//    	return null;
//    }
//
//    /**
//     * Remove o arquivo da lista de anexos do projeto.
//     * 
//     * <br>
//     * Método chamado pela(s) seguinte(s) JSP(s):
//     * <ul>
//     * 	<li> sigaa.war/projetos/ProjetoBase/arquivos.jsp</li>
//     * </ul>
//     * 
//     * @return Retorna para mesma página permitindo a exclusão de novo arquivo.
//     */
//    public String removeArquivo() {
//    	try {
//    		Integer idArquivo =  getParameterInt("idArquivoProjeto", 0);
//
//    		// Remove do banco de arquivos (cluster)
//    		if (idArquivo > 0) {
//    			EnvioArquivoHelper.removeArquivo(idArquivo);
//    			obj.setIdArquivo(null);
//    		}else {
//    			addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
//    		}
//    		
//    	} catch (Exception e) {
//    		tratamentoErroPadrao(e);
//    		return null;
//    	}
//
//    	return null;
//    }
//
//    /**
//     * Visualizar o arquivo anexo a inscrição.
//     * 
//     * <br>
//     * Método chamado pela(s) seguinte(s) JSP(s):
//     * <ul>
//     * 	<li> sigaa.war/projetos/ProjetoBase/arquivos.jsp</li>
//     * </ul>
//     * 
//     */
//    public void viewArquivo() {
//    	try {
//    		int idArquivo = getParameterInt("idArquivo",0);
//    		
//    		if (idArquivo == 0) {
//    			addMensagemErro("Arquivo não encontrado.");
//    			return;
//    		}
//    		
//    		EnvioArquivoHelper.recuperaArquivo(getCurrentResponse(), idArquivo, false);
//    		
//    	} catch (Exception e) {
//    		notifyError(e);
//    		addMensagemErro("Arquivo não encontrado.");
//    		return;
//    	}
//    	FacesContext.getCurrentInstance().responseComplete();
//    }
//	
//    
//    
//    
//    /**
//     * Visualizar as respotas ao questionário dos participantes da ação de extensão.
//     * 
//     * <br>
//     * Método chamado pela(s) seguinte(s) JSP(s):
//     * <ul>
//     * 		<li> /sigaa.war/extensao/InscricaoOnline/inscritos.jsp</li>
//     * </ul>
//     * @throws DAOException 
//     * 
//     */
//    public String viewRespostaParticipanteQuestionario() throws DAOException{
//    	
//    	int idQuestionarioRespostas = getParameterInt("idQuestionario",0);
//    	
//    	if(idQuestionarioRespostas <= 0 ){
//    		addMensagemErro("Questionário não selecionado corretamente.");
//    		return null;
//    	}
//    	
//    	QuestionarioRespostasDao dao = null;
//    	
//    	try{
//    		dao = getDAO(QuestionarioRespostasDao.class);
//    		
//    		questionarioRespondido =  dao.findInformacaoRespostasQuestionario(idQuestionarioRespostas, TipoQuestionario.QUESTIONARIO_INSCRICAO_ATIVIDADE);
//    		
//    		if(questionarioRespondido == null || questionarioRespondido.getRespostas() == null){
//    			addMensagemErro("Não existem respostas para o questionário selecionado.");
//        		return null;
//    		} 
//    		
//    		/* Redireciona para a página padrão de visualização das resposta do questionário */
//    		getCurrentRequest().setAttribute("_visualizaRespostasQuestionarioMBean", this);
//    		return forward("/geral/questionario/view_respostas_questionario.jsp");
//    		
//    	}finally{
//    		if(dao != null) dao.close();
//    	}
//    	
//    }
//    
//    
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
//	public void setInscricao(InscricaoAtividade inscricao) {
//		this.inscricao = inscricao;
//	}
//
//	public InscricaoAtividade getInscricao() {
//		return inscricao;
//	}
//
//	public boolean isCheckBuscaTitulo() {
//		return checkBuscaTitulo;
//	}
//
//	public void setCheckBuscaTitulo(boolean checkBuscaTitulo) {
//		this.checkBuscaTitulo = checkBuscaTitulo;
//	}
//
//	public boolean isCheckBuscaTipoAtividade() {
//		return checkBuscaTipoAtividade;
//	}
//
//	public void setCheckBuscaTipoAtividade(boolean checkBuscaTipoAtividade) {
//		this.checkBuscaTipoAtividade = checkBuscaTipoAtividade;
//	}
//
//	public boolean isCheckBuscaArea() {
//		return checkBuscaArea;
//	}
//
//	public void setCheckBuscaArea(boolean checkBuscaArea) {
//		this.checkBuscaArea = checkBuscaArea;
//	}
//
//	public boolean isCheckBuscaServidor() {
//		return checkBuscaServidor;
//	}
//
//	public void setCheckBuscaServidor(boolean checkBuscaServidor) {
//		this.checkBuscaServidor = checkBuscaServidor;
//	}
//
//	public boolean isCheckBuscaPeriodo() {
//		return checkBuscaPeriodo;
//	}
//
//	public void setCheckBuscaPeriodo(boolean checkBuscaPeriodo) {
//		this.checkBuscaPeriodo = checkBuscaPeriodo;
//	}
//
//	public void setCheckBuscaAbertas(boolean checkBuscaAbertas) {
//		this.checkBuscaAbertas = checkBuscaAbertas;
//	}
//
//	public boolean isCheckBuscaAbertas() {
//		return checkBuscaAbertas;
//	}
//
//	public void setNovoCadastro(boolean novoCadastro) {
//		this.novoCadastro = novoCadastro;
//	}
//
//	public boolean isNovoCadastro() {
//		return novoCadastro;
//	}
//
//	public String getBuscaNomeAtividade() {
//		return buscaNomeAtividade;
//	}
//
//	public void setBuscaNomeAtividade(String buscaNomeAtividade) {
//		this.buscaNomeAtividade = buscaNomeAtividade;
//	}
//
//	public Integer getBuscaTipoAtividade() {
//		return buscaTipoAtividade;
//	}
//
//	public void setBuscaTipoAtividade(Integer buscaTipoAtividade) {
//		this.buscaTipoAtividade = buscaTipoAtividade;
//	}
//
//	public Integer getBuscaArea() {
//		return buscaArea;
//	}
//
//	public void setBuscaArea(Integer buscaArea) {
//		this.buscaArea = buscaArea;
//	}
//
//	public Servidor getDocente() {
//		return docente;
//	}
//
//	public void setDocente(Servidor docente) {
//		this.docente = docente;
//	}
//
//	public int getIdAtividade() {
//		return idAtividade;
//	}
//
//	public void setIdAtividade(int idAtividade) {
//		this.idAtividade = idAtividade;
//	}
//
//	public void setCodigoAcesso(String codigoAcesso) {
//		this.codigoAcesso = codigoAcesso;
//	}
//
//	public String getCodigoAcesso() {
//		return codigoAcesso;
//	}
//
//	public void setMostrarMensagem(boolean mostrarMensagem) {
//		this.mostrarMensagem = mostrarMensagem;
//	}
//
//	public boolean isMostrarMensagem() {
//		return mostrarMensagem;
//	}
//
//	public void setExibirArea(boolean exibirArea) {
//		this.exibirArea = exibirArea;
//	}
//
//	public boolean isExibirArea() {
//		return exibirArea;
//	}
//
//	public void setBuscaFim(Date buscaFim) {
//		this.buscaFim = buscaFim;
//	}
//
//	public Date getBuscaFim() {
//		return buscaFim;
//	}
//
//	public void setBuscaInicio(Date buscaInicio) {
//		this.buscaInicio = buscaInicio;
//	}
//
//	public Date getBuscaInicio() {
//		return buscaInicio;
//	}
//
//	public void setParticipante(boolean participante) {
//		this.participante = participante;
//	}
//
//	public boolean isParticipante() {
//		return participante;
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
//	public boolean isJaExisteInscricaoNestaAtividade() {
//		return jaExisteInscricaoNestaAtividade;
//	}
//
//	public void setJaExisteInscricaoNestaAtividade(boolean jaExisteInscricaoNestaAtividade) {
//		this.jaExisteInscricaoNestaAtividade = jaExisteInscricaoNestaAtividade;
//	}
//
//	public InscricaoAtividadeParticipante getInscricaoAnterior() {
//		return inscricaoAnterior;
//	}
//
//	public void setInscricaoAnterior(InscricaoAtividadeParticipante inscricaoAnterior) {
//		this.inscricaoAnterior = inscricaoAnterior;
//	}
//
//	public boolean isAtividadePossuiCobancaTaxaInscricao() {
//		return atividadePossuiCobancaTaxaInscricao;
//	}
//
//	public void setAtividadePossuiCobancaTaxaInscricao(boolean atividadePossuiCobancaTaxaInscricao) {
//		this.atividadePossuiCobancaTaxaInscricao = atividadePossuiCobancaTaxaInscricao;
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
//	public QuestionarioRespostas getQuestionarioRespondido() {
//		return questionarioRespondido;
//	}
//
//	public void setQuestionarioRespondido(QuestionarioRespostas questionarioRespondido) {
//		this.questionarioRespondido = questionarioRespondido;
//	}
//
//	public void setSubAtv(Boolean subAtv) {
//		this.subAtv = subAtv;
//	}
//
//	public Boolean getSubAtv() {
//		return subAtv;
//	}
//	
	
}
