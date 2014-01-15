/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/12/2007
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.comum.dominio.notificacoes.Destinatario;
import br.ufrn.comum.dominio.notificacoes.Notificacao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.DiscenteExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.InscricaoSelecaoExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.PlanoTrabalhoExtensaoDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoSelecaoExtensao;
import br.ufrn.sigaa.extensao.dominio.PlanoTrabalhoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoSituacaoDiscenteExtensao;
import br.ufrn.sigaa.extensao.jsf.helper.DesignacaoFuncaoProjetoHelper;
import br.ufrn.sigaa.extensao.negocio.CadastroExtensaoMov;
import br.ufrn.sigaa.extensao.negocio.PlanoTrabalhoValidator;
import br.ufrn.sigaa.parametros.dominio.ParametrosExtensao;
import br.ufrn.sigaa.pesquisa.form.TelaCronograma;
import br.ufrn.sigaa.pessoa.dominio.Banco;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.projetos.dominio.CronogramaProjeto;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente;
import br.ufrn.sigaa.projetos.negocio.CronogramaProjetoHelper;

/*******************************************************************************
 * MBean respons�vel pelo controle de planos de trabalho de extens�o.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Scope("session")
@Component("planoTrabalhoExtensao")
public class PlanoTrabalhoExtensaoMBean extends SigaaAbstractController<PlanoTrabalhoExtensao> {

	public static final int LIMITE_CARACTER_ATIVIDADE = 1000;
	
	/** Atributo utilizado para mostrar o cronograma */
	private TelaCronograma telaCronograma = new TelaCronograma();

	/** Atributo utilizado para armazenar informa��es de consulta ao banco. */
	private Collection<PlanoTrabalhoExtensao> planosTrabalho;

	/** Atributo utilizado para armazenar informa��es de consulta ao banco. */
	private Collection<AtividadeExtensao> atividades = new ArrayList<AtividadeExtensao>();

	/** Atributo utilizado para armazenar informa��es de consulta ao banco. */
	private Collection<PlanoTrabalhoExtensao> planosCoordenadorLogado = new ArrayList<PlanoTrabalhoExtensao>();

	/** Atributo utilizado para armazenar informa��es de consulta ao banco. */
	private Collection<DiscenteExtensao> discentesExtensao = new ArrayList<DiscenteExtensao>();
	
	/** Atributo utilizado para lista de poss�veis orientadores. */
	private Collection<MembroProjeto> possiveisOrientadores = new ArrayList<MembroProjeto>();

	/** Atributo utilizado para armazenar o n�mero id da atividade */
	private Integer idAtividade = new Integer(0);	

	/** Informa��o do tipo de V�nculo do discente */
	private TipoVinculoDiscente tipoVinculo = new TipoVinculoDiscente();
	
	/** Respons�vel por armazenar a Inscri��o do discente */
	private InscricaoSelecaoExtensao inscricao = new InscricaoSelecaoExtensao();
	
	/** Respons�vel por resgatar a origem do acesso ao caso de uso */
	private String origemAcesso = null;
	
	/**
	 * Construtor padr�o. 
	 * 
	 */
	public PlanoTrabalhoExtensaoMBean() {
		this.obj = new PlanoTrabalhoExtensao();
	}

	/**
	 * Verifica se usu�rio atual � coordenador de a��o de extens�o.	 
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamado por JSPs.</li>
	 * </ul>
	 * 
	 * @throws SegurancaException Somente coordenadores de a��es realizam esta opera��o.
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {		
		if ( !DesignacaoFuncaoProjetoHelper.isCoordenadorOrDesignacaoCoordenador(getUsuarioLogado().getServidor().getPessoa().getId()) 
				&& !isUserInRole(SigaaPapeis.GESTOR_EXTENSAO)) {
			throw new SegurancaException("Usu�rio n�o autorizado a realizar esta opera��o.");
		}
		super.checkChangeRole();
	}

	/**
	 * Inicia o caso de uso de cadastro de planos de trabalho de bolsistas da a��o.
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul>
	 * 	<li> /sigaa.war/extensao/menu_ta.jsp </li>
	 * 	<li> /sigaa.war/portais/docente/menu_docente.jsp </li>
	 * </ul>
	 * 
	 * @return retorna p�gina com todas as atividades coordenadas pelo usu�rio atual que podem receber cadastros de planos de trabalho
	 * @throws SegurancaException Somente coordenadores de a��es tem acesso a esta opera��o.
	 * @throws DAOException Gerado na busca das a��es poss�veis de cadastro de plano.
	 */
	public String iniciarCadastroPlanoBolsista() throws SegurancaException, DAOException {
		checkChangeRole();
		filtrarAcoesCoordenadasUsuarioLogado();
		obj = new PlanoTrabalhoExtensao();
		tipoVinculo = getGenericDAO().findByPrimaryKey(TipoVinculoDiscente.EXTENSAO_BOLSISTA_INTERNO, TipoVinculoDiscente.class);
		return forward(ConstantesNavegacao.PLANOTRABALHOBOLSISTA_ATIVIDADE_LISTA);
	}


	/**
	 * Inicia o caso de uso de cadastro de planos de trabalho de volunt�rio da a��o.
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul>
	 * 	<li> /sigaa.war/extensao/menu_ta.jsp </li>
	 * 	<li> /sigaa.war/portais/docente/menu_docente.jsp </li>
	 * </ul>
	 * 
	 * @return retorna p�gina com todas as atividades coordenadas pelo usu�rio atual que podem receber cadastros de planos de trabalho
	 * @throws SegurancaException Somente coordenadores de a��es tem acesso a esta opera��o.
	 * @throws DAOException Gerado na busca das a��es poss�veis de cadastro de plano.
	 */
	public String iniciarCadastroPlanoVoluntario() throws SegurancaException, DAOException {
		checkChangeRole();
		filtrarAcoesCoordenadasUsuarioLogado();
		obj = new PlanoTrabalhoExtensao();
		tipoVinculo = getGenericDAO().findByPrimaryKey(TipoVinculoDiscente.EXTENSAO_VOLUNTARIO, TipoVinculoDiscente.class);
		return forward(ConstantesNavegacao.PLANOTRABALHOBOLSISTA_ATIVIDADE_LISTA);
	}
	
	/**
	 * Retorna p�gina com todas as atividades coordenadas pelo usu�rio atual que podem receber cadastros de planos de trabalho.
	 *  
	 * @throws DAOException
	 */
	private void filtrarAcoesCoordenadasUsuarioLogado() throws DAOException {
		AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class);
		atividades = dao.findByCoordenadorAtivo(getUsuarioLogado().getServidor(), new Integer[] {TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO});
	}
	
	/**
	 * Verifica as permiss�es e lista todos os planos de trabalho do coordenador atual.
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul>
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/alterar_plano_lista.jsp (2 matches) </li>
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/planos_coordenador.jsp </li>
	 * 	<li> /sigaa.war/extensao/menu_ta.jsp </li>
	 * 	<li> /sigaa.war/portais/docente/menu_docente.jsp </li> 
	 * </ul>
	 * 
	 * @return Lista todos os planos de trabalho onde o usu�rio � coordenador ativo de a��es em execu��o
	 * @throws SegurancaException Somente coordenadores de a��es tem acesso a esta opera��o.
	 * @throws DAOException Gerado na busca das a��es poss�veis de cadastro de plano.
	 */
	public String listarPlanosCoordenador() throws SegurancaException, DAOException {
		checkChangeRole();
		PlanoTrabalhoExtensaoDao dao = getDAO(PlanoTrabalhoExtensaoDao.class);
		planosCoordenadorLogado = dao.findByServidorCoordenadorAtivo(getUsuarioLogado().getServidor(),
				new Integer[] {TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO, TipoSituacaoProjeto.EXTENSAO_CONCLUIDO});

		return forward(ConstantesNavegacao.PLANOTRABALHOBOLSISTA_PLANOS_COORDENADOR);
	}

	/**
	 * M�todo utilizado para remover planos de trabalho de extens�o.
	 * Carrega o plano de trabalho selecionado e exibe tela de confirma��o da remo��o.
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul><li> /sigaa.war/extensao/PlanoTrabalho/planos_coordenador.jsp </li></ul> 
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String preRemoverPlanoTrabalho() throws ArqException {
		checkChangeRole();
		prepareMovimento(SigaaListaComando.REMOVER_PLANO_TRABALHO_EXTENSAO);
		
		setId();
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), PlanoTrabalhoExtensao.class);
		
		// evitar erro de lazy
		obj.getAtividade().setMembrosEquipe(getGenericDAO().findByExactField(MembroProjeto.class, "projeto.id", obj.getAtividade().getProjeto().getId()));
		obj.getCronogramas().iterator();
		obj.getHistoricoDiscentesPlano().iterator();
		
		// Inicializar tela do cronograma
		TelaCronograma cronograma = new TelaCronograma(obj.getDataInicio(), obj.getDataFim(), obj.getCronogramas());
		setTelaCronograma(cronograma);
		setConfirmButton("Remover");
		setReadOnly(true);
		
		return forward(ConstantesNavegacao.PLANOTRABALHOBOLSISTA_RESUMO);
	}

	/**
	 * Inicia o cadastro de novo plano de trabalho de extens�o.
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul><li> /sigaa.war/extensao/PlanoTrabalho/atividades_lista.jsp </li></ul> 
	 * 
	 * @return P�gina com formul�rio para cadastro de dados gerais do plano de trabalho.
	 * @throws ArqException Somente coordenadores de a��es pode realizar esta a��o.
	 */
	public String novoPlanoTrabalho() throws ArqException {
		checkChangeRole();
		AtividadeExtensao atividade = getGenericDAO().findByPrimaryKey(idAtividade, AtividadeExtensao.class);
		ListaMensagens mensagens = new ListaMensagens();
		PlanoTrabalhoValidator.validaAcao(atividade, mensagens);

		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
		}
		// carrega lista de discentes interessados para o coordenador selecionar
		obj = new PlanoTrabalhoExtensao();
		obj.setTipoVinculo(this.tipoVinculo);
		obj.setAtividade(atividade);
		obj.setAtivo(true);
		obj.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
		obj.setInscricaoSelecaoExtensao(new InscricaoSelecaoExtensao());
		obj.setDiscenteExtensao(novoDiscenteExtensaoPlanoTrabalho());
		if ( tipoVinculo.isExtensaoInterno() )
			obj.getDiscenteExtensao().setDataInicio( carregarInicioBolsista() );
		filtrarDiscentesDemonstraramInteresse();
		filtrarPossiveisOrientadores();

		setReadOnly(false);
		prepareMovimento(SigaaListaComando.ENVIAR_PLANO_TRABALHO_EXTENSAO);
		return forward(ConstantesNavegacao.PLANOTRABALHOBOLSISTA_FORM);
	}

	/**
	 * Cria um novo discente de extens�o devidamente configurado para ser
	 * inclu�do no plano de trabalho.
	 * 
	 * @return Discente pronto para ser adicionado ao plano de trabalho;
	 * @throws DAOException Carrega TipoSituacaoDiscente para legenda.
	 */
	private DiscenteExtensao novoDiscenteExtensaoPlanoTrabalho() throws DAOException {
		DiscenteExtensao de = new DiscenteExtensao();
		de.setAtivo(true);
		de.setPlanoTrabalhoExtensao(null);
		de.setSituacaoDiscenteExtensao(new TipoSituacaoDiscenteExtensao(TipoSituacaoDiscenteExtensao.CADASTRO_EM_ANDAMENTO));
		getGenericDAO().initialize(de.getSituacaoDiscenteExtensao());
		de.setBanco(new Banco());
		de.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
		de.setAtividade(obj.getAtividade());
		de.setDataFim(obj.getDataFim());
		de.setDataCadastro(new Date());
		de.setTipoVinculo(obj.getTipoVinculo());
		return de;
	}

	/**
	 * Diret�rio base do caso de uso.
	 * 
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *  	<li>N�o � chamado por JSP(s)</li>
	 *  </ul>
	 * 
	 * @return Retorna diret�rio base do caso de uso.
	 */
	@Override
	public String getDirBase() {
		return "/extensao/PlanoTrabalho";
	}

	/**
	 * Direciona o usu�rio para p�gina de cadastro de dados gerais do plano de trabalho.
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul><li> /sigaa.war/extensao/PlanoTrabalho/cronograma.jsp </li></ul> 
	 * 
	 * @return P�gina de dados gerais do plano.
	 * @throws DAOException 
	 */
	public String irDadosGerais() throws DAOException {
		//Evitar erro de Lazy.
		obj.setAtividade(getGenericDAO().findByPrimaryKey(obj.getAtividade().getId(), AtividadeExtensao.class));
		filtrarDiscentesDemonstraramInteresse();
		return forward(ConstantesNavegacao.PLANOTRABALHOBOLSISTA_FORM);
	}

	/**
	 * Direciona o usu�rio para p�gina do cronograma do plano de trabalho.
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul><li> /sigaa.war/extensao/PlanoTrabalho/resumo.jsp </li></ul> 
	 * 
	 * @return P�gina de cronograma do plano.
	 */
	public String irCronograma() {
		return forward(ConstantesNavegacao.PLANOTRABALHOBOLSISTA_CRONOGRAMA);
	}

	
	/**
	 * Inicializa o discente no novo plano de trabalho.
	 * 
	 * @throws DAOException
	 */
	private void inicializarDiscenteNoPlano() throws DAOException {
		obj.setInscricaoSelecaoExtensao(null);

		if (obj.getDiscenteExtensao() == null) {
			obj.setDiscenteExtensao(novoDiscenteExtensaoPlanoTrabalho());

		}else if (ValidatorUtil.isEmpty(obj.getDiscenteExtensao().getDiscente())) {
			obj.getDiscenteExtensao().setDiscente(new Discente());

		}else {

			//Preparando Plano para Valida��o dos dados gerais
			GenericDAO dao = getGenericDAO();
			obj.getDiscenteExtensao().setDiscente(dao.refresh(obj.getDiscenteExtensao().getDiscente()));
			obj.setAtividade(dao.findByPrimaryKey(obj.getAtividade().getId(), AtividadeExtensao.class));

			// Verificando se discente selecionado faz parte da lista de interessados.
			for (InscricaoSelecaoExtensao inscricao : obj.getAtividade().getInscricoesSelecao()) {
				if (inscricao.getDiscente().equals(obj.getDiscenteExtensao().getDiscente())) {
					dao.initialize(inscricao);
					obj.setInscricaoSelecaoExtensao(inscricao); 
					break;
				}
			}

			//Sincroniza dados do discente com o plano.
			dao.initialize(obj.getDiscenteExtensao().getTipoVinculo());
			obj.setTipoVinculo(obj.getDiscenteExtensao().getTipoVinculo());
			obj.getDiscenteExtensao().setDataFim(obj.getDataFim());
		}		
	}
	
	/**
	 * Submete a primeira p�gina de cadastro dos planos de trabalho para valida��o
	 * dos dados e prepara a tela do cronograma. (Pr�ximo passo do cadastro).
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul><li> /sigaa.war/extensao/PlanoTrabalho/form.jsp </li></ul> 
	 * 
	 * @return Em caso de erro retorna para mesma tela, em caso de sucesso na valida��o vai para tela de cronograma.
	 * @throws ArqException Somente coordenadores de a��es podem realizar.                                                               
	 */
	public String submeterDadosGerais() throws ArqException {
		checkChangeRole();		

		inicializarDiscenteNoPlano();
		ListaMensagens mensagens = new ListaMensagens();
		PlanoTrabalhoValidator.validaDadosGerais(obj, mensagens);
		
		if (!mensagens.isEmpty()) {			
			inicializarDiscenteNoPlano();
			filtrarDiscentesDemonstraramInteresse();
			addMensagens(mensagens);
			return null;
		}
		
		// Inicializar tela do cronograma
		TelaCronograma cronograma = new TelaCronograma(obj.getDataInicio(), obj.getDataFim(), obj.getCronogramas());
		setTelaCronograma(cronograma);
		
		return forward(ConstantesNavegacao.PLANOTRABALHOBOLSISTA_CRONOGRAMA);
	}

	

	/**
	 * Atualiza a lista de inscritos na sele��o
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul><li> /sigaa.war/extensao/PlanoTrabalho/form.jsp </li></ul> 
	 * 
	 * @return Lista de inscritos na sele��o para a��o de extens�o do plano de trabalho.
	 * @throws DAOException Gerado pela busca dos dados para valida��o.
	 */
	public Collection<InscricaoSelecaoExtensao> getInscricoesSelecao() throws DAOException{
		filtrarDiscentesDemonstraramInteresse();
		return obj.getAtividade().getInscricoesSelecao();	
	}


	/**
	 * Submete o cronograma a valida��o dos dados digitados e em seguida 
	 * redireciona o usu�rio para tela de resumo.
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul><li> /sigaa.war/extensao/PlanoTrabalho/cronograma.jsp </li></ul> 
	 * 
	 * @return P�gina de resumo do plano de trabalho. 
	 * @throws SegurancaException Somente coordenadores de a��es de extens�o podem realizar esta a��o.
	 * @throws DAOException Gerado pela busca dos dados para valida��o.
	 */
	public String submeterCronograma() throws SegurancaException, DAOException, Exception {
		validaCronograma();

		if ( hasErrors() ) {
			addMensagens(erros);
			return null;
		}
		
		setConfirmButton(null);
		obj.setAtividade(getGenericDAO().findByPrimaryKey(obj.getAtividade().getId(), AtividadeExtensao.class));
		obj.setOrientador( getGenericDAO().findByPrimaryKey(obj.getOrientador().getId(), MembroProjeto.class) );
		obj.setDiscentesVoluntarios(getGenericDAO().findByExactField(DiscenteExtensao.class, "planoTrabalhoExtensao.id", obj.getId()));

		return forward(ConstantesNavegacao.PLANOTRABALHOBOLSISTA_RESUMO);
	}

	/**
	 * Serve para Validar as informa��es do cronograma do plano de trabalho.
	 */
	private void validaCronograma() throws SegurancaException, Exception {
		checkChangeRole();

		// Obter objetos cronogramas a partir dos dados do formul�rio
		String[] calendario = getCurrentRequest().getParameterValues("telaCronograma.calendario");
		getTelaCronograma().setCalendario(calendario);

		String[] atividadesDesenvolvidas = getCurrentRequest().getParameterValues("telaCronograma.atividade");
		getTelaCronograma().setAtividade(atividadesDesenvolvidas);


		// Obtendo atividades desenvolvidas do cronograma a partir da view.
		getTelaCronograma().definirCronograma(getCurrentRequest());
		List<CronogramaProjeto> cronogramas = getTelaCronograma().getCronogramas();
		for (CronogramaProjeto cronograma : cronogramas) {
			cronograma.setPlanoTrabalhoExtensao(getObj());
		}

		getObj().setCronogramas(cronogramas);
		erros.addAll( CronogramaProjetoHelper.validate( getTelaCronograma() ) );
		erros.addAll( CronogramaProjetoHelper.validateTamanhoAtividade( getTelaCronograma(), LIMITE_CARACTER_ATIVIDADE ) );

	}

	/**
	 * Salva um plano de trabalho permitindo a continua��o do cadastro depois.
	 * 
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul>
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/resumo.jsp </li>
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/cronograma.jsp </li>
	 * </ul> 
	 * 
	 * @return Mesma p�gina informando o sucesso ao salvar os dados.
	 * @throws Exception 
	 * @throws NumberFormatException 
	 * 
	 */
	public String salvar() throws NumberFormatException, Exception {
		checkChangeRole();
		
		ListaMensagens mensagens = new ListaMensagens();
		PlanoTrabalhoValidator.validaDadosGerais(obj, mensagens);
		if (!mensagens.isEmpty()) {			
			inicializarDiscenteNoPlano();
			filtrarDiscentesDemonstraramInteresse();
			addMensagens(mensagens);
			return null;
		}
		
		prepareMovimento(SigaaListaComando.SALVAR_PLANO_TRABALHO_EXTENSAO);
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.SALVAR_PLANO_TRABALHO_EXTENSAO);
		
		mov.setObjMovimentado(obj);
		mov.setUsuarioLogado(getUsuarioLogado());
		execute(mov, getCurrentRequest());
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		
		inicializarDiscenteNoPlano();
		filtrarDiscentesDemonstraramInteresse();
		return null;
	}
	
	/**
	 * Realiza o cadastro quando se est� na tela do cronograma dos planos de Extens�o.
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * 
	 * <ul>
	 * 		<li> /SIGAA/app/sigaa.ear/sigaa.war/extensao/PlanoTrabalho/cronograma.jsp </li>
	 * </ul> 
	 */
	public String salvarCronograma() throws NumberFormatException, Exception {
		validaCronograma();
		
		if ( hasErrors() ) {
			addMensagens(erros);
			return null;
		}
		
		return salvar();	
	}
	
	/**
	 * Envia um plano de trabalho.
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul><li> /sigaa.war/extensao/PlanoTrabalho/resumo.jsp </li></ul> 
	 * 
	 * @return P�gina de sucesso do cadastro ou remo��o do plano.
	 * @throws ArqException Gerado pela chamada ao processador
	 * @throws NegocioException Somente coordenadores de a��es de extens�o podem cadastrar planos.
	 * 
	 */
	@Override
	public String cadastrar() throws NegocioException, ArqException {
		checkChangeRole();	
		try {
			MovimentoCadastro mov = new MovimentoCadastro();	
			ListaMensagens mensagens = new ListaMensagens();
			mensagens = obj.validate();
			if (!mensagens.isEmpty()) {
				addMensagens(mensagens);
				return null;
			}
			mov.setCodMovimento(SigaaListaComando.ENVIAR_PLANO_TRABALHO_EXTENSAO);
			mov.setObjMovimentado(obj);
			mov.setUsuarioLogado(getUsuarioLogado());
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			obj = new PlanoTrabalhoExtensao();
			return listarPlanosCoordenador();
		}catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}catch (Exception e) {
			notifyError(e);
		}
		return null;
	}

	/**
	 * Remove um plano de trabalho.
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul><li> /sigaa.war/extensao/PlanoTrabalho/resumo.jsp </li></ul> 
	 * 
	 * @return P�gina de sucesso da remo��o do plano.
	 * @throws ArqException Gerado pela chamada ao processador
	 * 
	 */
	public String remover() throws ArqException {
		checkChangeRole();
		try {
			MovimentoCadastro mov = new MovimentoCadastro();	
			mov.setCodMovimento(SigaaListaComando.REMOVER_PLANO_TRABALHO_EXTENSAO);
			mov.setObjMovimentado(obj);
			mov.setUsuarioLogado(getUsuarioLogado());
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			obj = new PlanoTrabalhoExtensao();
			setConfirmButton(null);
			return listarPlanosCoordenador();
		}catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}catch (Exception e) {
			if(e.getMessage().contains("Solicita��o j� processada")){
				addMensagemWarning("Solicita��o j� processada");
				return listarPlanosCoordenador();
			}
			notifyError(e);
		}
		return null;
	}

	
	
	/**
	 * Inicia a atualiza��o do plano de trabalho do discente.
	 * Esta opera��o permite que somente o plano de trabalho seja alterado
	 * evitando valida��es redundantes sobre o discente do plano.
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul><li> /sigaa.war/extensao/PlanoTrabalho/alterar_plano_lista.jsp </li></ul>
	 * 
	 * @return P�gina para altera��o dos dados do plano.
	 * @throws ArqException Somente coordenadores de a��es podem atualizar os planos.  
	 */
	public String atualizar() throws ArqException {
		checkChangeRole();
		prepareMovimento(SigaaListaComando.SALVAR_PLANO_TRABALHO_EXTENSAO);
		setId();
		PlanoTrabalhoExtensaoDao dao = getDAO(PlanoTrabalhoExtensaoDao.class);
		obj = dao.findByPrimaryKey(obj.getId(), PlanoTrabalhoExtensao.class);

		//Evitar erro de lazy
		obj.setOrientador(dao.findByPrimaryKey(obj.getOrientador().getId(), MembroProjeto.class, "id", "pessoa.nome"));
		obj.getCronogramas().iterator();
		filtrarDiscentesDemonstraramInteresse();

		if (ValidatorUtil.isEmpty(obj.getOrientador())) {
			obj.setOrientador(new MembroProjeto());
		}
		
		if (ValidatorUtil.isEmpty(obj.getDiscenteExtensao())) {
			obj.setDiscenteExtensao(novoDiscenteExtensaoPlanoTrabalho());
		} else {	
			obj.setDiscenteExtensao(getGenericDAO().findByPrimaryKey(obj.getDiscenteExtensao().getId(), DiscenteExtensao.class));
			obj.getDiscenteExtensao().setDiscente(getGenericDAO().findByPrimaryKey(obj.getDiscenteExtensao().getDiscente().getId(), Discente.class));
		}
		
		if (ValidatorUtil.isEmpty(obj.getDiscenteExtensao().getBanco())) {
			obj.getDiscenteExtensao().setBanco(new Banco());
		}
		
		inicializarDiscenteNoPlano();
		filtrarPossiveisOrientadores();
		setReadOnly(false);
		return forward(getFormPage());
	}


	/**
	 * Carrega todos os planos de trabalho de extens�o onde o usu�rio logado � o
	 * discente das respectivas atividades.
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul>
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/planos_discente.jsp </li>
	 * </ul>
	 * 
	 * @throws DAOException Gerado pela busca dos planos
	 */
	public String carregarPlanosTrabalhoDiscenteLogado() throws DAOException {
		discentesExtensao = getDAO(DiscenteExtensaoDao.class).findByDiscenteComPlanoTrabalho(getDiscenteUsuario().getId(), null);	
		return forward(ConstantesNavegacao.PLANOTRABALHOBOLSISTA_DISCENTE);
	}

	/**
	 * Carrega plano e prepara MBeans para visualiza��o.
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul>
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/busca_discente.jsp </li>
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/homologacao_bolsa_discente.jsp </li> 
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/alterar_plano_lista.jsp </li>
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/lista.jsp </li>
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/planos_coordenador.jsp </li>
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/planos_discente.jsp </li>
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/dados_bancarios_discentes_form.jsp </li>     
	 * </ul>
	 * 
	 * @return P�gina de visualiza��o do plano.
	 */
	public String view() {
		try {
			// Inicia o plano selecionado
			Integer id = getParameterInt("id", 0);
			
			//Se id n�o for zero ele est� vindo de um f:setPropertyActionListener
			if(id == 0) id = obj.getId();
			
			obj = getGenericDAO().findByPrimaryKey(id, PlanoTrabalhoExtensao.class);
			
			// Inicializar tela do cronograma
			obj.getCronogramas().iterator();
			TelaCronograma cronograma = new TelaCronograma(obj.getDataInicio(), obj.getDataFim(), obj.getCronogramas());
			setTelaCronograma(cronograma);
			
			return forward(ConstantesNavegacao.PLANOTRABALHOBOLSISTA_VIEW);
		} catch (Exception e) {
			notifyError(e);
			return null;
		}
	}

	/**
	 * Lista todos os planos do coordenador com op��es para indicar, finalizar
	 * ou substituir bolsista.
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul>
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/menu_ta.jsp </li>
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/menu_docente.jsp </li> 
	 * </ul>
	 * 
	 * @return Lista com planos de trabalho de a��es antigas do coordenador.
	 * @throws SegurancaException Somente coordenador de a��o pode realizar esta a��o.
	 * @throws DAOException Gerado na busca dos planos de trabalho.
	 */
	public String iniciarAlterarPlano() throws SegurancaException, DAOException {
		checkChangeRole();
		PlanoTrabalhoExtensaoDao dao = getDAO(PlanoTrabalhoExtensaoDao.class);
		planosCoordenadorLogado = dao.findByServidorCoordenadorAtivo(getUsuarioLogado().getServidor(), 
				new Integer[] {TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO, TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO,
			TipoSituacaoProjeto.EXTENSAO_CONCLUIDO, TipoSituacaoProjeto.PROJETO_BASE_CONCLUIDO});
		return forward(ConstantesNavegacao.PLANOTRABALHOBOLSISTA_ALTERAR_PLANO_LISTA);
	}

	/**
	 * Carrega do banco o plano de trabalho selecionado e 
	 * redireciona o usu�rio para tela de indica��o de novo
	 * discente para o plano.
	 *	<br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul>
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/menu_ta.jsp </li>
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/menu_docente.jsp </li> 
	 * </ul>
	 * 
	 * @return P�gina com formul�rio para inidica��o de novo discente no plano.
	 * @throws ArqException Somente coordenadores podem realizar esta opera��o.
	 */
	public String iniciarIndicarDiscente() throws ArqException {
		checkChangeRole();
		setId();
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), PlanoTrabalhoExtensao.class);
		obj.setDiscenteExtensaoNovo(new DiscenteExtensao());
		obj.getDiscenteExtensaoNovo().setAtividade(new AtividadeExtensao(obj.getAtividade().getId()));
		obj.getDiscenteExtensaoNovo().setBanco(new Banco());
		obj.getDiscenteExtensaoNovo().setDataInicio( carregarInicioBolsista() );
		if ( obj.getDiscenteExtensao() != null )
			obj.getDiscenteExtensao().setDataFim( 
					CalendarUtils.adicionaDias(obj.getDiscenteExtensaoNovo().getDataInicio(), -1) );

		prepareMovimento(SigaaListaComando.INDICAR_DISCENTE_EXTENSAO);
		filtrarDiscentesDemonstraramInteresse();
		filtrarPossiveisOrientadores();
		return forward(ConstantesNavegacao.PLANOTRABALHOBOLSISTA_INDICAR_DISCENTE);
	}

	private Date carregarInicioBolsista() throws DAOException {
		int diaInicial = ParametroHelper.getInstance().getParametroInt(ParametrosExtensao.DIA_INICIAL_INDICACAO_BOLSISTA);
		int diaFinal = ParametroHelper.getInstance().getParametroInt(ParametrosExtensao.DIA_FINAL_INDICACAO_BOLSISTA);
		int diaAtual = CalendarUtils.getDiaByData(new Date());
		Date dataInicial = null;
		Date dataFim = null;
		if ( diaAtual > diaInicial ) {
			dataInicial = CalendarUtils.createDate( diaInicial, CalendarUtils.getMesAtual(), CalendarUtils.getAnoAtual());
			dataFim = CalendarUtils.createDate( diaFinal, CalendarUtils.getMesAtual()+1, CalendarUtils.getAnoAtual());
		} else {
			dataInicial = CalendarUtils.createDate( diaInicial,CalendarUtils.getMesAtual()-1, CalendarUtils.getAnoAtual());
			dataFim = CalendarUtils.createDate( diaFinal, CalendarUtils.getMesAtual(), CalendarUtils.getAnoAtual());
		}
		if ( CalendarUtils.isDentroPeriodo(dataInicial, dataFim) ) {
			return CalendarUtils.createDate(1, CalendarUtils.getMesByData(dataFim), CalendarUtils.getAnoAtual());
		} else {
			return CalendarUtils.createDate(1, CalendarUtils.getMesByData(dataFim)+1, CalendarUtils.getAnoAtual());
		}
	}

	public void carregarDataInicialBolsista(ValueChangeEvent e) throws ArqException {
		TipoVinculoDiscente tipoVinculo = new TipoVinculoDiscente((Integer) e.getNewValue());
		if (tipoVinculo.isExtensaoInterno())
			obj.getDiscenteExtensao().setDataInicio( carregarInicioBolsista() ); 
	}
	
	/**
	 * Indica novo discente para o plano de trabalho selecionado.
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul>
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/alterar_plano_lista.jsp </li>
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/indicar_discente.jsp </li> 
	 * </ul>
	 * 
	 * @return P�gina com lista de planos de trabalho permitindo indica��o de novo discente.
	 * @throws ArqException Somente coordenadores de a��es podem realizar esta opera��o.
	 */
	public String indicarDiscente() throws ArqException {
		checkChangeRole();
		try {	    
			int idDiscente = obj.getDiscenteExtensaoNovo().getDiscente().getId();
			Discente discente = getGenericDAO().findByPrimaryKey(idDiscente, Discente.class);
			if (discente == null) {
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Novo Discente");
				return null;
			}
			//Valida��o...
			obj.getDiscenteExtensaoNovo().setDiscente(discente);
			ListaMensagens mensagens = new ListaMensagens();
			PlanoTrabalhoValidator.validaIndicarDiscente(obj, mensagens);
			if (!mensagens.isEmpty()) {
				addMensagens(mensagens);
				return null;
			}
			CadastroExtensaoMov mov = new CadastroExtensaoMov();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(SigaaListaComando.INDICAR_DISCENTE_EXTENSAO);
			if (!obj.getDiscenteExtensaoNovo().isBolsista()) {
				obj.getDiscenteExtensaoNovo().setBanco(null);
			}
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
		}
		return iniciarAlterarPlano();	
	}

	/**
	 * Carrega plano de trabalho e redireciona para tela de finalizar discente.
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul><li> /sigaa.war/extensao/PlanoTrabalho/alterar_plano_lista.jsp </li></ul>
	 * 
	 * @return Retorna p�gina formul�rio para finaliza��o do discente no plano de trabalho.
	 * @throws ArqException Somente coordenadores podem realizar esta opera��o.
	 */
	public String iniciarFinalizarDiscente() throws ArqException {
		checkChangeRole();
		int idDiscente = getParameterInt("idDiscenteExtensao", 0);
		if (idDiscente == 0) {
			addMensagemErro("Discente selecionado n�o foi localizado. Por favor realize a busca novamente.");
			return null;
		}
		
		DiscenteExtensao de = getGenericDAO().findByPrimaryKey(idDiscente, DiscenteExtensao.class);
		obj = getGenericDAO().findByPrimaryKey(de.getPlanoTrabalhoExtensao().getId(), PlanoTrabalhoExtensao.class);
		getGenericDAO().initialize(obj.getAtividade());
		obj.setDiscenteExtensao(de);
		prepareMovimento(SigaaListaComando.FINALIZAR_DISCENTE_EXTENSAO);
		setOrigemAcesso(getParameter("origemAcesso"));
		return forward(ConstantesNavegacao.PLANOTRABALHOBOLSISTA_FINALIZAR_DISCENTE);
	}

	/**
	 * Realiza opera��o de finaliza��o do discente no plano de trabalho.
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul>
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/alterar_plano_lista.jsp </li>
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/finalizar_discente.jsp </li> 
	 * </ul>

	 * 
	 * @return Retorna para tela com lista de planos de trabalho do coordenador permitindo finaliza��o de novo discente.
	 * @throws ArqException Somente coordenadores de a��es podem realizar esta opera��o. 
	 */
	public String finalizarDiscente() throws ArqException {

		checkChangeRole();
		ListaMensagens mensagens = new ListaMensagens();
		PlanoTrabalhoValidator.validaFinalizarDiscente(obj, mensagens);
		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
		}
		try {
			CadastroExtensaoMov mov = new CadastroExtensaoMov();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(SigaaListaComando.FINALIZAR_DISCENTE_EXTENSAO);
			execute(mov, getCurrentRequest());
			obj = new PlanoTrabalhoExtensao();
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			notifyError(e);
		}
		
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		if (getOrigemAcesso() != null) {
			((DiscenteExtensaoMBean)getMBean("discenteExtensao")).setDiscentesExtensao(null);
			return forward(getOrigemAcesso());
		} else {
			return iniciarAlterarPlano();
		}

	}

	/**
	 * Lista todos os discentes que demonstraram interesse em participar do
	 * processo seletivo da a��o de extens�o.
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul><li> /sigaa.war/extensao/PlanoTrabalho/form.jsp </li></ul>
	 * <ul><li> /sigaa.war/extensao/PlanoTrabalho/indicar_discente.jsp </li></ul>
	 * 
	 * @throws DAOException se ocorrer erro relacionado com acesso aos dados
	 */
	public void filtrarDiscentesDemonstraramInteresse() throws DAOException {

		InscricaoSelecaoExtensaoDao dao = getDAO(InscricaoSelecaoExtensaoDao.class);
		Collection<InscricaoSelecaoExtensao> inscritos = dao.findInscritosProcessoSeletivoByAtividade(obj.getAtividade().getId());
		for (InscricaoSelecaoExtensao inscricao : inscritos) {
			inscricao.setPrioritario(isPrioritario(inscricao.getDiscente()));
		}

		obj.getAtividade().setInscricoesSelecao(inscritos);
	}

	/**
	 * Lista todos os poss�veis orientadores entre todos os membros da equipe do projeto.
	 * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>N�o � chamado por JSPs.</li>
	 * </ul>
	 * @throws DAOException se ocorrer erro relacionado com acesso aos dados
	 */
	public void filtrarPossiveisOrientadores() throws DAOException {
		MembroProjetoDao dao = getDAO(MembroProjetoDao.class);
		possiveisOrientadores = dao.findOrientadoresByProjeto(obj.getAtividade().getProjeto().getId());
	}

	/**
	 * Converte lista de orientadores em itens selecion�veis para caixa de combina��o.
	 * 
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul><li> /sigaa.war/extensao/PlanoTrabalho/form.jsp </li></ul>
	 * <ul><li> /sigaa.war/extensao/PlanoTrabalho/indicar_discente.jsp </li></ul>
	 * @return
	 */
    public Collection<SelectItem> getAllOrientadoresCombo() {
    	return toSelectItems(possiveisOrientadores, "id", "pessoa.nome");
    }

	/**
	 * Carrega as informa��es para que seja enviado o email e direciona para a tela 
	 * de preenchimento do t�tulo e mensagem.
	 * 
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul>
	 * 	<li> /SIGAA/app/sigaa.ear/sigaa.war/extensao/PlanoTrabalho/form.jsp </li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String preEnviarEmail() throws DAOException {
		int id = getParameterInt("id", 0);
		
		if(id == 0) id = obj.getId();

		if ( inscricao != null ) {
			inscricao = getGenericDAO().findByPrimaryKey(id, InscricaoSelecaoExtensao.class);
		} else {
			obj = new PlanoTrabalhoExtensao();
			if (obj.getDiscenteExtensao() == null)
				obj.setDiscenteExtensao(new DiscenteExtensao());
			
			obj.setDiscenteExtensao( getGenericDAO().findAndFetch(id, DiscenteExtensao.class, "discente", "atividade.projeto.coordenador") );
			inscricao = new InscricaoSelecaoExtensao();
			inscricao.setDiscente( new Discente() );
			inscricao.setDiscente( obj.getDiscenteExtensao().getDiscente() );
			inscricao.setAtividade( obj.getDiscenteExtensao().getAtividade() );
		}
	
		inscricao.setNotificacao(new Notificacao());
		carregarEmail();
		return forward(ConstantesNavegacao.PLANOTRABALHOBOLSISTA_CONTATO);
	}

	/**
	 * Inicializa o envio quando originado da tela de indicar e remover bolsita
	 * 
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul>
	 * 	<li> /SIGAA/app/sigaa.ear/sigaa.war/extensao/PlanoTrabalho/alterar_plano_lista.jsp </li>
	 * </ul>
	 */
	public String preEnviarEmailIndicacao() throws DAOException{
		inscricao = null;
		return preEnviarEmail();
	}
	
	/**
	 * M�todo respons�vel por carregar as informa��es necess�rios para o envio do email e/ou mensagem ao discente 
	 * 
	 * @throws DAOException
	 */
	private void carregarEmail() throws DAOException {
		UsuarioDao dao = getDAO(UsuarioDao.class);
		try {
			Destinatario destinatario = new Destinatario();
			destinatario.setUsuario(new UsuarioGeral());
			
			destinatario.setNome(inscricao.getDiscente().getPessoa().getNome());
			
			if (inscricao.getDados() != null)
				destinatario.setEmail(inscricao.getDados().getEmail());
			else
				destinatario.setEmail(inscricao.getDiscente().getPessoa().getEmail());
			
			Collection<Destinatario> destinatarios = new ArrayList<Destinatario>();
			destinatarios.add(destinatario);
			destinatario.getUsuario().setEmail(destinatario.getEmail());
			
			List<UsuarioGeral> usuarios = (List<UsuarioGeral>) dao.findByCpf(inscricao.getDiscente().getPessoa().getCpf_cnpj());
			
			UsuarioGeral usuarioGeral = usuarios.get(0);
			destinatario.setUsuario(usuarioGeral);
			inscricao.getNotificacao().setDestinatariosEmail(destinatarios);
			inscricao.getNotificacao().setDestinatariosMensagem(destinatarios);
			
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Respons�vel pelo envio do email e mensagem para o candidato a bolsa.
	 *
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul>
	 * 	<li> /SIGAA/app/sigaa.ear/sigaa.war/extensao/PlanoTrabalho/form_contato.jsp </li>
	 * </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String enviarEmail() throws ArqException {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(inscricao.getNotificacao().getTitulo(), "T�tulo", lista);
		ValidatorUtil.validateRequired(inscricao.getNotificacao().getMensagem(), "Mensagem", lista);
		
		if ( !inscricao.getNotificacao().isEnviarEmail() && !inscricao.getNotificacao().isEnviarMensagem() )
			lista.addErro("� necess�rio informar pelo menos uma forma de envio.");
		
		if ( lista.isErrorPresent() ) {
			addMensagens(lista);
			return null;
		}
		
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			prepareMovimento(SigaaListaComando.NOTIFICACAO_DISCENTE_MENSAGEM_EMAIL);
			mov.setCodMovimento(SigaaListaComando.NOTIFICACAO_DISCENTE_MENSAGEM_EMAIL);
			mov.setObjMovimentado(obj);
			mov.setObjAuxiliar(inscricao);
			mov.setUsuarioLogado(getUsuarioLogado());
			execute(mov, getCurrentRequest());

			if ( inscricao.getNotificacao().isEnviarEmail() )
				addMensagemInformation("Foi enviado um email para " + inscricao.getDiscente().getPessoa().getNome() );

			if ( inscricao.getNotificacao().isEnviarMensagem() ) 
				addMensagemInformation("Foi enviado uma mensagem para " + inscricao.getDiscente().getPessoa().getNome() );

			inscricao = new InscricaoSelecaoExtensao();
			return redirectJSF(getSubSistema().getLink());
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		return null;
	}
	
	/**
	 * Utilizado para sele��o de a��es de extens�o coordenadas pelo usu�rio atual
	 * para listar planos de trabalho.
	 * <br/>
	 * M�todo chamado pela(s) seguintes JSPs:
	 * <ul>
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/atividades_lista.jsp (3 matches) </li>
	 * 	<li> /sigaa.war/extensao/PlanoTrabalho/planos_discente.jsp </li> 
	 * </ul>
	 * 
	 * @return Retorna id da a��o selecionada.
	 */
	public Integer getIdAtividade() {
		return idAtividade;
	}

	public void setIdAtividade(Integer idAtividade) {
		this.idAtividade = idAtividade;
	}

	public TelaCronograma getTelaCronograma() {
		return telaCronograma;
	}

	public void setTelaCronograma(TelaCronograma telaCronograma) {
		this.telaCronograma = telaCronograma;
	}

	/**
	 * M�todo utilizado para recuperar todos os planos de trabalho
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamado por JSP(s)</li>
	 * </ul>
	 * @return
	 */
	public Collection<PlanoTrabalhoExtensao> getPlanosTrabalho() {
		if (planosTrabalho == null) {
			planosTrabalho = obj.getAtividade().getPlanosTrabalho();
		}
		return planosTrabalho;
	}

	public void setPlanosTrabalho(Collection<PlanoTrabalhoExtensao> planosTrabalho) {
		this.planosTrabalho = planosTrabalho;
	}

	public Collection<AtividadeExtensao> getAtividades() {
		return atividades;
	}

	public void setAtividades(Collection<AtividadeExtensao> atividades) {
		this.atividades = atividades;
	}

	public Collection<PlanoTrabalhoExtensao> getPlanosCoordenadorLogado() {
		return planosCoordenadorLogado;
	}

	public void setPlanosCoordenadorLogado(
			Collection<PlanoTrabalhoExtensao> planosCoordenadorLogado) {
		this.planosCoordenadorLogado = planosCoordenadorLogado;
	}

	public Collection<DiscenteExtensao> getDiscentesExtensao() {
		return discentesExtensao;
	}


	public void setDiscentesExtensao(Collection<DiscenteExtensao> discentesExtensao) {
		this.discentesExtensao = discentesExtensao;
	}

	public TipoVinculoDiscente getTipoVinculo() {
		return tipoVinculo;
	}

	public void setTipoVinculo(TipoVinculoDiscente tipoVinculo) {
		this.tipoVinculo = tipoVinculo;
	}

	public InscricaoSelecaoExtensao getInscricao() {
		return inscricao;
	}

	public void setInscricao(InscricaoSelecaoExtensao inscricao) {
		this.inscricao = inscricao;
	}

	public String getOrigemAcesso() {
		return origemAcesso;
	}

	public void setOrigemAcesso(String origemAcesso) {
		this.origemAcesso = origemAcesso;
	}

	public Collection<MembroProjeto> getPossiveisOrientadores() {
		return possiveisOrientadores;
	}

	public void setPossiveisOrientadores(
			Collection<MembroProjeto> possiveisOrientadores) {
		this.possiveisOrientadores = possiveisOrientadores;
	}

}