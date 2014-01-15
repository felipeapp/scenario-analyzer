/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/09/2011
 *
 */
package br.ufrn.sigaa.pesquisa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.faces.component.UIData;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.richfaces.component.html.HtmlDataTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.autenticacao.TokenGenerator;
import br.ufrn.arq.seguranca.dominio.TokenAutenticacao;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.GrupoPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.LinhaPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.MembroGrupoPesquisaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.ensino.negocio.MovimentoCadastroDocenteExterno;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.pesquisa.dao.PropostaGrupoPesquisaDao;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.HistoricoGrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.LinhaPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.MembroGrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.StatusGrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoMembroGrupoPesquisa;
import br.ufrn.sigaa.pesquisa.negocio.GrupoPesquisaValidator;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.pessoa.dominio.TipoDocenteExterno;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;

/**	
 * Controlador responsável pela submissão de propostas de criação de grupos de
 * pesquisa.
 * 
 * @author Leonardo Campos
 * @author Jean Guerethes
 */
@Component("propostaGrupoPesquisaMBean") @Scope("session")
public class PropostaGrupoPesquisaMBean extends SigaaAbstractController<GrupoPesquisa> {

	/** Responsável por injetar o TokenGenerator 
	 * responsável pela comunicação com sipac, para a geração da capa do processo. */
	@Autowired
	private TokenGenerator generator;
	
	/** UIData utilizado para a manipulação da listagem dos membros permanentes */
	private UIData membrosPermanentes = new HtmlDataTable();
	/** UIData utilizado para a manipulação da listagem dos membros associados */
	private UIData membrosAssociados = new HtmlDataTable();
	/** UIData utilizado para a manipulação da listagem dos membros do termo de concordância */
	private UIData membrosTermoConcordancia = new HtmlDataTable();
	/** Área de conhecimento do grupo de pesquisa */
	private AreaConhecimentoCnpq area = new AreaConhecimentoCnpq();
	/** Sub-área do grupo de pesquisa */
	private Collection<SelectItem> subareas = new ArrayList<SelectItem>();
	/** Docente permanente do grupo de pesquisa */
	private Servidor docentePermanente = new Servidor();
	/** Docente associado do grupo de pesquisa */
	private Servidor docenteAssociado = new Servidor();
	/** discente associado do grupo de pesquisa */
	private Discente discenteAssociado = new Discente();
	/** Pesquisador externo associado do grupo de pesquisa */
	private DocenteExterno docenteExterno = new DocenteExterno();
	/** Membro do grupo de pesquisa */
	private MembroGrupoPesquisa membroPainel = new MembroGrupoPesquisa();
	/** Linha de pesquisa presente no grupo de pesquisa */
	private LinhaPesquisa linha = new LinhaPesquisa();
	/** DataModel utilizado para a exibição do grupo de pesquisa */
	private DataModel gruposPesquisa;
	/** Código utilizado para carregar o membro do grupo de pesquisa */
	private String codigo;
	/** Utilizado para exibir ou não o grupo de pesquisa */
	private boolean exibeBotoes;
	/** Total máximo de caracteres que o docente pode informar nos dados gerais do grupo de pesquisa */
	private int totalCaracteres = 0; 
	/** Indica se o participante externo do grupo de pesquisa é estrangeiro ou não. */
	private Boolean estrangeiro = false;
	/** Indica se o CPF do participante externo foi encontrado no banco. */
	private boolean cpfEncontrado = false;
	/** Enumeração contendo as abas dos membros associados. */
	private enum AbasMembrosAssociados { DOCENTE, SERVIDOR, DISCENTE, EXTERNO }
	/** Indica qual aba dos membros associados está ativa. */
	private int abaAtivaMembrosAssociados = AbasMembrosAssociados.DOCENTE.ordinal(); 
	
	public PropostaGrupoPesquisaMBean() {
		clear();
	}

	/**
	 * Inicializa todos os atributos utilizados na proposta do grupo de pesquisa
	 */
	private void clear() {
		obj = new GrupoPesquisa();
		docentePermanente = new Servidor();
		docenteAssociado = new Servidor();
		docenteExterno = new DocenteExterno();
		docenteExterno.getPessoa().setSexo(PessoaGeral.SEXO_MASCULINO);
		abaAtivaMembrosAssociados = AbasMembrosAssociados.DOCENTE.ordinal();
		area = new AreaConhecimentoCnpq();
		exibeBotoes = false;
		totalCaracteres = ParametroHelper.getInstance().getParametroInt(ParametrosPesquisa.NUMERO_CARACTERES_GRUPO_PESQUISA);
	}
	
	/**
	 * Método utilizado na inicialização da proposta do grupo de pesquisa
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 */
	public String iniciar() throws DAOException{
		clear();
		
		// TODO: Verificar permissões
		if ( isPesquisa() ) {
			obj.setCoordenador( new Servidor() );
			obj.setViceCoordenador( new Servidor() );
		} else {
			obj.setCoordenador(getServidorUsuario());
			obj.setViceCoordenador(new Servidor());
			createAddMembro(getServidorUsuario(), null, null, MembroGrupoPesquisa.COORDENADOR, CategoriaMembro.DOCENTE, TipoMembroGrupoPesquisa.PERMANENTE);
		}
		
		obj.setStatus( StatusGrupoPesquisa.CADASTRO_EM_ANDAMENTO );

		return telaProposta();
	}

	/**
	 * Realiza da submissão dos dados gerais do grupo de pesquisa
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/dados_gerais.jsp</li>
	 * </ul>
	 */
	public String submeterDadosGerais() throws ArqException{
		GrupoPesquisaValidator.validaDadosGerais(obj, erros, isPortalDocente(), true);
		if (hasErrors()) return null;
		if ( isEmpty( obj.getViceCoordenador().getPessoa().getNome() ) )
			obj.setViceCoordenador(null);
		prepareMovimento(SigaaListaComando.GRAVAR_PROPOSTA_GRUPO_PESQUISA);
		gravarTemporariamente();
		carregarCamposLazy();
		setConfirmButton("Submeter Proposta");
		return telaMembros();
	}
	
	/**
	 * Realiza da submissão dos membros do grupo de pesquisa
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/membros.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String submeterMembros() throws ArqException{
		GrupoPesquisaValidator.validaDadosGerais(obj, erros, isPortalDocente(), true);
		if (hasErrors()) return telaDadosGerais();
		GrupoPesquisaValidator.validaMembros(obj, erros);
		if (hasErrors()) return telaMembros();
		prepareMovimento(SigaaListaComando.GRAVAR_PROPOSTA_GRUPO_PESQUISA);
		gravarTemporariamente();
		carregarCamposLazy();
		exibeBotoes = true;
		return telaTermoConcordancia();
	}

	/**
	 * Realiza da submissão do termo de concordância do grupo de pesquisa
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/membros.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String submeterTermo() throws ArqException{
		carregarCamposLazy();
		return telaProjetosVinculados();
	}

	/**
	 * Realiza da submissão dos projetos vínculados do grupo de pesquisa
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/projetos_vinculados.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String submeterProjetosVinculados() throws ArqException{
		return telaDescricao();
	}

	/**
	 * Realiza da submissão dos dados complementares do grupo de pesquisa
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/descricao.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String submeterDescricao() throws ArqException{
		GrupoPesquisaValidator.validaDescricaoDetalhada(obj, erros);
		obj.setParecer(null);
		if (hasErrors()) return null;
		prepareMovimento(SigaaListaComando.GRAVAR_PROPOSTA_GRUPO_PESQUISA);
		gravarTemporariamente();
		carregarCamposLazy();
		setConfirmButton("Enviar");
		exibeBotoes = true;
		prepareMovimento(SigaaListaComando.ENVIAR_PROPOSTA_GRUPO_PESQUISA);
		return telaVisualizar();
	}

	/**
	 * Carregar a proposta do grupo de pesquisa e exibe o resumo do mesmo.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/minhas_propostas.jsp</li>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String visualizar() throws DAOException {
		if ( isPesquisa() ) {
			setId();
			setObj( getGenericDAO().findByPrimaryKey(obj.getId(), GrupoPesquisa.class) );
		} else 
			obj = (GrupoPesquisa) gruposPesquisa.getRowData();
		carregarCamposLazy();
		exibeBotoes = false;
		return telaVisualizar();
	}
	
	/**
	 * Serve para enviar o grupo de pesquisa para a PROPESQ analisar.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/view.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String submeterGrupoPesquisa() throws ArqException {
		
		for(MembroGrupoPesquisa mp: obj.getEquipesGrupoPesquisa()){
			if(mp.getAssinado() == null || !mp.getAssinado()){
				addMensagemErro("A proposta só pode ser submetida quando todos os membros assinarem-na. " +
						"Repita o procedimento de enviar email para coletar as assinaturas restantes, " +
						"ou remova da proposta os membros que não assinaram.");
				return null;
			}
		}
		
		MovimentoCadastro mov = new MovimentoCadastro();
		obj.setStatus(StatusGrupoPesquisa.APROVACAO_DEPARTAMENTO);
		mov.setObjMovimentado(obj);
		mov.setUsuarioLogado(getUsuarioLogado());
		mov.setCodMovimento(SigaaListaComando.ENVIAR_PROPOSTA_GRUPO_PESQUISA);
		try {
			execute(mov);
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Proposta de Criação de Grupo de Pesquisa");
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		if (isPortalDocente())
			return telaComprovante();
		else {
			GrupoPesquisaMBean mBean = getMBean("grupoPesquisa");
			return mBean.listar();
		}
	}

	/**
	 * Serve para enviar um parecer sobre um grupo de pesquisa.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String enviarParecer() throws ArqException{
		setId();
		setObj( getGenericDAO().findByPrimaryKey(obj.getId(), GrupoPesquisa.class) );
		prepareMovimento(SigaaListaComando.GRAVAR_PROPOSTA_GRUPO_PESQUISA);
		return telaParecer();
	}
	
	/**
	 * Serve para submeter um parecer sobre um grupo de pesquisa.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/parecer.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String submeterParecer() throws ArqException {
		ValidatorUtil.validateRequiredId(obj.getStatus(), "Status", erros);
		if ( isNecessitaCorrecao() )
			ValidatorUtil.validateRequired(obj.getParecer(), "Parecer", erros);
		else
			prepareMovimento(SigaaListaComando.ENVIAR_PROPOSTA_GRUPO_PESQUISA);
			
		addMensagens(erros);
		if (hasErrors()) return null;
		
		enviarTemporariamente();
		addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Parecer");
		GrupoPesquisaMBean mBean = getMBean("grupoPesquisa");
		return mBean.listar();
	}
	
	public boolean isNecessitaCorrecao(){
		return obj.getStatus() == StatusGrupoPesquisa.NECESSITA_CORRECAO;
	}
	
	/**
	 * Serve para direcionar o usuário para a tela dos dados gerais.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/membros.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String telaDadosGerais() {
		if ( isEmpty(obj.getViceCoordenador()) )
			obj.setViceCoordenador(new Servidor());
		return forward(ConstantesNavegacaoPesquisa.PROPOSTAGRUPO_DADOS_GERAIS);
	}

	/**
	 * Serve para direcionar o usuário para a tela dos projetos vínculados.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/projetos_vinculados.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String telaTermoConcordancia() {
		return forward(ConstantesNavegacaoPesquisa.PROPOSTAGRUPO_TERMO_CONCORDANCIA);
	}
	
	/**
	 * Serve para direcionar o usuário para a tela dos dados complementares(as Descrições).
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/view.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String telaDescricao() {
		return forward(ConstantesNavegacaoPesquisa.PROPOSTAGRUPO_DESCRICAO);
	}
	
	/**
	 * Serve para direcionar o usuário para a tela do termo de concordância.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/termo.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String telaMembros() {
		return forward(ConstantesNavegacaoPesquisa.PROPOSTAGRUPO_MEMBROS);
	}
	
	/**
	 * Serve para direcionar o usuário para a tela das propostas cadastradas.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>Não invocado por JSP'S</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String telaMinhasPropostas() {
		return forward(ConstantesNavegacaoPesquisa.PROPOSTAGRUPO_MINHAS_PROPOSTAS);
	}
	
	/**
	 * Serve para direcionar o usuário para a tela das projetos vínculados.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/descricao.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String telaProjetosVinculados() throws DAOException {
		return forward(ConstantesNavegacaoPesquisa.PROPOSTAGRUPO_PROJETOS_VINCULADOS);
	}
	
	/**
	 * Serve para direcionar o usuário para a tela da visualização dos dados informandos anteriormente.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>Não invocado por JSP's</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String telaVisualizar() {
		return forward(ConstantesNavegacaoPesquisa.PROPOSTAGRUPO_VISUALIZAR);
	}

	/**
	 * Serve para direcionar o usuário para a tela do parecer.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>Não invocado por JSP's</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String telaParecer() {
		return forward(ConstantesNavegacaoPesquisa.PROPOSTAGRUPO_EMITIR_PARECER);
	}
	
	/**
	 * Serve para carregar sub-área da proposta de grupo de pesquisa.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/dados_gerais.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public void changeArea(ValueChangeEvent evt) throws DAOException {
		subareas = new ArrayList<SelectItem>();
		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		if (((Integer) evt.getNewValue()) != null && ((Integer) evt.getNewValue()) != 0) {
			AreaConhecimentoCnpq grandeArea = new AreaConhecimentoCnpq( (Integer) evt.getNewValue()) ;
			subareas = toSelectItems(dao.findAreas(grandeArea), "id", "nome");
		}
	}

	/**
	 * Serve para carregar sub-área da proposta de grupo de pesquisa.
	 */
	private void changeArea() throws DAOException {
		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		AreaConhecimentoCnpq grandeArea = new AreaConhecimentoCnpq( obj.getAreaConhecimentoCnpq().getGrandeArea().getId() ) ;
		subareas = toSelectItems(dao.findAreas(grandeArea), "id", "nome");
	}
	
	/**
	 * Serve para adicionar um membro ao grupo de pesquisa
	 * @throws DAOException 
	 */
	private void createAddMembro(Servidor servidor, Discente discente, DocenteExterno docenteExterno, int classificacao, int idCategoriaMembro, int idTipoMembro) throws DAOException {
		int idPessoa = 0;
		if ( servidor != null )
			idPessoa = servidor.getPessoa().getId();
		else if ( discente != null )
			idPessoa = discente.getPessoa().getId();
		else if ( docenteExterno != null)
			idPessoa = docenteExterno.getPessoa().getId();
		
		if (obj.getId() > 0 && idPessoa > 0) {
			Collection<MembroGrupoPesquisa> membros = getGenericDAO().findByExactField(MembroGrupoPesquisa.class, 
					new String[]{"pessoa.id", "grupoPesquisa.id"}, new Object[]{idPessoa, obj.getId()});
			if ( !membros.isEmpty() ) {
				addMensagemErro("Membro já presente do grupo de pesquisa.");
				redirectMesmaPagina();
			}
		}
		
		MembroGrupoPesquisa m = new MembroGrupoPesquisa();
		m.setDiscente(discente);
		m.setDocenteExterno(docenteExterno);
		m.setServidor(servidor);
		if ( servidor != null )
			m.setPessoa( getGenericDAO().refresh(servidor.getPessoa()) );
		else if ( discente != null )
			m.setPessoa( getGenericDAO().refresh(discente.getPessoa()) );
		else if ( docenteExterno != null)
			m.setPessoa( getGenericDAO().refresh(docenteExterno.getPessoa()) );
		m.setClassificacao(classificacao);
		
		if ( classificacao == MembroGrupoPesquisa.COORDENADOR ) {
			m.setAssinado(true);
			m.setDataAssinatura(new Date());
		}
		
		m.setCategoriaMembro(new CategoriaMembro(idCategoriaMembro));
		m.setTipoMembroGrupoPesquisa(new TipoMembroGrupoPesquisa(idTipoMembro));
		if (servidor != null)
			m.setEnderecoLattes(getDAO(PropostaGrupoPesquisaDao.class).getEnderecoLattes(servidor));
		obj.addEquipeGrupoPesquisa(m);
	}

	/**
	 * Serve para setar o membro selecionado no Membro do Painel.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>Não invocado por JSP's</li>
	 * </ul>
	 */
	public void carregarPainelMembroPermanente(ActionEvent event) {
		membroPainel = (MembroGrupoPesquisa) membrosPermanentes.getRowData();
	}

	/**
	 * Serve para buscar o nome do docente permanente informado.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/dados_gerais.jsp</li>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/membros.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public List<Servidor> autoCompleteDocentePermanente(Object query) throws DAOException {
		return (List<Servidor>) getDAO(PropostaGrupoPesquisaDao.class).findByMembroPermanente((String) query);
	}

	/**
	 * Serve para buscar o nome do docente associado informado.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/membros.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public List<Servidor> autoCompleteDocenteAssociado(Object query) throws DAOException {
		return (List<Servidor>) getDAO(PropostaGrupoPesquisaDao.class).findByMembroDocenteAssociado((String) query );
	}

	/**
	 * Serve para buscar o nome do técnico administrativo associado informado.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/membros.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public List<Servidor> autoCompleteTecnicoAssociado(Object query) throws DAOException {
		return (List<Servidor>) getDAO(PropostaGrupoPesquisaDao.class).findByMembroTecnicoAssociado((String) query );
	}

	/**
	 * Serve para buscar o nome do discente associado informado.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/membros.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public List<Discente> autoCompleteDiscenteAssociado(Object query) throws DAOException {
		return (List<Discente>) getDAO(PropostaGrupoPesquisaDao.class).findByMembroDiscenteAssociado((String) query );
	}

	/**
	 * Serve para buscar pelo título do projeto de pesquisa.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/projetos_vinculados.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public List<ProjetoPesquisa> autoCompleteProjetoPesquisa(Object query) throws DAOException {
		return (List<ProjetoPesquisa>) getDAO(PropostaGrupoPesquisaDao.class).findByProjetoPesquisa((String) query, obj.getEquipesGrupoPesquisaCol() );
	}

	/**
	 * Serve para listar os grupos de pesquisa com cadastro em andamento do docente logado..
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/termo.jsp</li>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/view.jsp</li>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	@Override
	public String listar() throws ArqException {
		
		GrupoPesquisaDao dao = getDAO(GrupoPesquisaDao.class);
		try {
			gruposPesquisa = new ListDataModel( 
					(List<GrupoPesquisa>) dao.findOtimizado(null, getServidorUsuario().getId(), null, null, Boolean.TRUE, new Integer[]{}) );
		} finally {
			dao.close();
		}
		return telaMinhasPropostas();
	}
	
	/**
	 * Serve para dar continuidade ao cadastro do grupo de pesquisa.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/minhas_propostas.jsp</li>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String continuar() throws DAOException{
		clear();
		if ( isPesquisa() ) {
			setId();
			setObj( getGenericDAO().findByPrimaryKey(obj.getId(), GrupoPesquisa.class) );
		} else 
			obj = (GrupoPesquisa) gruposPesquisa.getRowData();
		carregarCamposLazy();
		
		if (isEmpty(obj.getViceCoordenador())) {
			obj.setViceCoordenador(new Servidor());
			obj.getViceCoordenador().setPessoa(new Pessoa());
		}
		
		return telaDadosGerais();
	}

	/**
	 * Serve para inativar uma proposta de criação de um grupo de pesquisa.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/minhas_propostas.jsp</li>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	@Override
	public String inativar() throws ArqException, NegocioException {
		if ( isPesquisa() ) {
			setId();
			setObj( getGenericDAO().findByPrimaryKey(obj.getId(), GrupoPesquisa.class) );
		} else 
			obj = (GrupoPesquisa) gruposPesquisa.getRowData();
		
		prepareMovimento(ArqListaComando.DESATIVAR);
		MovimentoCadastro mov = new MovimentoCadastro(obj, ArqListaComando.DESATIVAR);
		try {
			execute(mov);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return forward(getFormPage());
		}
		return listar();
	}
	
	/**
	 * Serve para carregar os campos Lazy do cadastro das proposta de criação de um grupo de pesquisa.
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>Não invocado por JSP's</li>
	 * </ul>
	 */
	public void carregarCamposLazy() throws DAOException {
		GrupoPesquisaDao dao = getDAO(GrupoPesquisaDao.class);
		LinhaPesquisaDao linhaDao = getDAO(LinhaPesquisaDao.class);
		try {
			setObj( dao.findAndFetch(obj.getId(), GrupoPesquisa.class, "viceCoordenador", "areaConhecimentoCnpq", "coordenador", "usuarioCriacao", "equipesGrupoPesquisa", "equipesGrupoPesquisa.pessoa") );
			obj.setLinhasPesquisa( new HashSet<LinhaPesquisa>(0) );
			obj.getLinhasPesquisa().addAll( linhaDao.findByTrecho(null, obj.getId()) );
			changeArea();
			setArea( obj.getAreaConhecimentoCnpq().getGrandeArea() );
		} finally {
			dao.close();
			linhaDao.close();
		}
	}
	
	/**
	 * Serve para visualizar as assinaturas do grupo de pesquisa.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/minhas_propostas.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String verAssinaturas() throws DAOException{
		if ( isPesquisa() ) {
			setId();
			setObj( getGenericDAO().findByPrimaryKey(obj.getId(), GrupoPesquisa.class) );
		} else 
			obj = (GrupoPesquisa) gruposPesquisa.getRowData();
		carregarCamposLazy();
		exibeBotoes = false;
		return telaTermoConcordancia();
	}
	
	/**
	 * Serve para cadastrar um docente permanente ao grupo de pesquisa.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/membros.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public void carregaDocentePermanente(ActionEvent e) throws ArqException{
		Servidor servidor = (Servidor) e.getComponent().getAttributes().get("docenteAutoComplete");
		createAddMembro(servidor, null, null, MembroGrupoPesquisa.MEMBRO, CategoriaMembro.DOCENTE, TipoMembroGrupoPesquisa.PERMANENTE);
		docentePermanente = new Servidor();
		gravarTemporariamente();
		redirectMesmaPagina();
	}
	
	/**
	 * Serve para cadastrar o coordenador ao grupo de pesquisa.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/dados_gerais.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public void carregarCoordenador(ActionEvent e) throws ArqException{
		Servidor servidor = (Servidor) e.getComponent().getAttributes().get("docenteAutoComplete");
		createAddMembro(servidor, null, null, MembroGrupoPesquisa.COORDENADOR, CategoriaMembro.DOCENTE, TipoMembroGrupoPesquisa.PERMANENTE);
		obj.setCoordenador(servidor);
	}

	/**
	 * Serve para cadastrar o vice coordenador ao grupo de pesquisa.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/dados_gerais.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public void carregarViceCoordenador(ActionEvent e) throws ArqException{
		Servidor servidor = (Servidor) e.getComponent().getAttributes().get("docenteAutoComplete");
		createAddMembro(servidor, null, null, MembroGrupoPesquisa.MEMBRO, CategoriaMembro.DOCENTE, TipoMembroGrupoPesquisa.PERMANENTE);
		obj.setViceCoordenador(servidor);
	}

	/**
	 * Serve para cadastrar um docente associado ao grupo de pesquisa.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/membros.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public void carregaDocenteAssociado(ActionEvent e) throws ArqException{
		setAbaAtivaMembrosAssociados(AbasMembrosAssociados.DOCENTE.ordinal());
		Servidor servidor = (Servidor) e.getComponent().getAttributes().get("docenteAssoAutoComplete");
		createAddMembro(servidor, null, null, MembroGrupoPesquisa.MEMBRO, CategoriaMembro.DOCENTE, TipoMembroGrupoPesquisa.ASSOCIADO);
		docenteAssociado = new Servidor();
		gravarTemporariamente();
		redirectMesmaPagina();
	}

	/**
	 * Serve para cadastrar um docente técnico adminstrativo ao grupo de pesquisa.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/membros.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public void carregaDocenteTecnAssociado(ActionEvent e) throws ArqException{
		setAbaAtivaMembrosAssociados(AbasMembrosAssociados.SERVIDOR.ordinal());
		Servidor servidor = (Servidor) e.getComponent().getAttributes().get("docenteAssoTecnAutoComplete");
		createAddMembro(servidor, null, null, MembroGrupoPesquisa.MEMBRO, CategoriaMembro.SERVIDOR, TipoMembroGrupoPesquisa.ASSOCIADO);
		docenteAssociado = new Servidor();
		gravarTemporariamente();
		redirectMesmaPagina();
	}

	/**
	 * Serve para cadastrar um discente associado ao grupo de pesquisa.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/membros.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public void carregaDiscenteAssociado(ActionEvent e) throws ArqException{
		setAbaAtivaMembrosAssociados(AbasMembrosAssociados.DISCENTE.ordinal());
		Discente discente = (Discente) e.getComponent().getAttributes().get("discenteAssoAutoComplete");
		createAddMembro(null, discente, null, MembroGrupoPesquisa.MEMBRO, CategoriaMembro.DISCENTE, TipoMembroGrupoPesquisa.ASSOCIADO);
		discenteAssociado = new Discente();
		gravarTemporariamente();
		redirectMesmaPagina();
	}
	
	/**
	 * Serve para adicionar uma projeto ao grupo de pesquisa.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/projetos_vinculados.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public void adicionarProjeto() throws DAOException{
		ValidatorUtil.validateRequired(obj.getProjPesquisa().getProjeto().getTitulo(), "Projeto de Pesquisa", erros);
		ValidatorUtil.validateRequiredId(linha.getId(), "Linha de Pesquisa", erros);

		if (hasErrors()){
			addMensagens(erros);
			return;
		}
		
		for (LinhaPesquisa linhaAtual : obj.getLinhasPesquisaCol()) {
			 if ( linhaAtual.getId() == linha.getId() ) {
				 boolean achou = false;
				 for(ProjetoPesquisa p: linhaAtual.getProjetosPesquisa()){
					 if(p.getId() == obj.getProjPesquisa().getId())
						 achou = true;
				 }
				 if ( !achou  ) {
					 getGenericDAO().updateField(ProjetoPesquisa.class, obj.getProjPesquisa().getId(), "linhaPesquisa", linhaAtual.getId());
				 } else {
					 addMensagemErroAjax("Projeto de Pesquisa já presente na Linha de Pesquisa.");
				     addMensagemErro("Projeto de Pesquisa já presente na Linha de Pesquisa.");
				 }
			 }
		}
		
		carregarCamposLazy();
	}
	
	/**
	 * Serve para remover uma projeto ao grupo de pesquisa.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/projetos_vinculados.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public void removerProjeto() throws DAOException{
		int idLinhaPesquisa = getParameterInt("id", 0);
		int idProjPesquisa = getParameterInt("idProj", 0);
		for (LinhaPesquisa linhaAtual : obj.getLinhasPesquisaCol()) {
			if ( linhaAtual.getId() == idLinhaPesquisa ) {
				for (ProjetoPesquisa projPesq : linhaAtual.getProjetosPesquisaCol()) {
					if (projPesq.getId() == idProjPesquisa) {
						getGenericDAO().updateField(ProjetoPesquisa.class, idProjPesquisa, "linhaPesquisa", null);
						carregarCamposLazy();
						return;
					}
				}
			}
		}	
	}

	/**
	 * Serve para remover um docente permanente ao grupo de pesquisa.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/membros.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public void removeMembroEquipePermanente() throws ArqException {
		membroPainel = (MembroGrupoPesquisa) membrosPermanentes.getRowData();
		getGenericDAO().remove(membroPainel);
		carregarCamposLazy();
		obj.removeEquipeGrupoPesquisa(membroPainel);
		redirectMesmaPagina();
	}

	/**
	 * Serve para cadastrar um docente associado ao grupo de pesquisa.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/membros.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public void removeMembroEquipeAssociado() throws ArqException {
		membroPainel = (MembroGrupoPesquisa) membrosAssociados.getRowData();
		getGenericDAO().remove(membroPainel);
		carregarCamposLazy();
		obj.removeEquipeGrupoPesquisa(membroPainel);
		redirectMesmaPagina();
	}

	/**
	 * Serve para remover o vice coordenador do grupo de pesquisa.
	 * Método chamado pelas seguintes JSPs:
	 * propostaGrupoPesquisaMBean.
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/membros.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public void removeViceCoordenador() throws ArqException {
		membroPainel = (MembroGrupoPesquisa) membrosPermanentes.getRowData();
		obj.removeEquipeGrupoPesquisa(membroPainel);
		redirectMesmaPagina();
	}
	
	/**
	 * Serve para adicionar uma linha de pesquisa ao grupo de pesquisa.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/dados_gerais.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public void adicionarLinhaPesquisa() throws ArqException {
		Collection<LinhaPesquisa> linhas;
		if ( !linha.getNome().isEmpty() ) {
			linhas = getGenericDAO().findByExactField(LinhaPesquisa.class, 
					new String[]{"nome", "inativa"}, new Object[]{linha.getNome(), Boolean.FALSE});
		} else {
			addMensagemErro("É necessário informar o nome da linha de pesquisa.");
			return;
		}

		if ( obj.getId() == 0 ) { 
			GrupoPesquisaValidator.validaDadosGerais(obj, erros, isPortalDocente(), false);
			if (hasErrors()) return;
			gravarTemporariamente();
			carregarCamposLazy();
			if ( linha.getId() == 0 )
				criacaoLinhaPesquisa();	
			else
				obj.getLinhasPesquisa().add(linha);
			if ( isEmpty( obj.getViceCoordenador() ) )
				obj.setViceCoordenador(new Servidor() );
			

		} else {
			
			if ( !linhas.isEmpty() ) {
				addMensagemErro("Já existe uma linha de Pesquisa com esse mesmo nome.");
				return;
			} else {
				gravarTemporariamente();
				carregarCamposLazy();
				if ( linha.getId() == 0 )
					criacaoLinhaPesquisa();	
				else
					obj.getLinhasPesquisa().add(linha);
				if ( isEmpty( obj.getViceCoordenador() ) )
					obj.setViceCoordenador(new Servidor() );
			}
		}
		
	}

	/**
	 * Serve para remover uma linha de pesquisa ao grupo de pesquisa.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/dados_gerais.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public void removerLinha() throws ArqException{
		int id = getParameterInt("id", 0);
		for (LinhaPesquisa linha : obj.getLinhasPesquisaCol()) {
			if (linha.getId() == id) {
				obj.getLinhasPesquisa().remove(linha);
				gravarTemporariamente();
				break;
			}
		}
	}
	
	/**
	 * Serve para cadastrar um o lattes para um docente do grupo de pesquisa.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/membros.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public void adicionarLattes( ActionEvent event ) throws ArqException{
		if ( membroPainel.getId() == 0 ) {
			prepareMovimento(SigaaListaComando.GRAVAR_PROPOSTA_GRUPO_PESQUISA);
			gravarTemporariamente();
		}
		getGenericDAO().updateField(MembroGrupoPesquisa.class, membroPainel.getId(), "enderecoLattes", membroPainel.getEnderecoLattes());
		carregarCamposLazy();
		redirectMesmaPagina();
	}
	
	/**
	 * Serve para carregar o lattes do docente para alterção.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/membros.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public void carregarPainel( ActionEvent event ) {
		membroPainel = (MembroGrupoPesquisa) membrosAssociados.getRowData();
	}
	
	/** Realiza a criação de uma linha de pesquisa. */
	private void criacaoLinhaPesquisa() throws DAOException {
		linha.setDataInicio(new Date());
		linha.setGrupoPesquisa(obj);
		linha.setInativa(Boolean.FALSE);
		getGenericDAO().createOrUpdate(linha);
		obj.getLinhasPesquisa().add(linha);
		linha = new LinhaPesquisa();
	}
	
	/**
	 * Grava a proposta do grupo de pesquisa.
	 * @throws ArqException
	 */
	private void gravarTemporariamente() throws ArqException{
		try {
			prepareMovimento(SigaaListaComando.GRAVAR_PROPOSTA_GRUPO_PESQUISA);
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setUsuarioLogado(getUsuarioLogado());
			mov.setSubsistema(getSubSistema().getId());
			mov.setCodMovimento(SigaaListaComando.GRAVAR_PROPOSTA_GRUPO_PESQUISA);
			execute(mov);
			if (isReprepare())
				prepareMovimento(SigaaListaComando.GRAVAR_PROPOSTA_GRUPO_PESQUISA);
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
	}

	/**
	 * Grava a proposta do grupo de pesquisa.
	 * @throws ArqException
	 */
	private void enviarTemporariamente() throws ArqException{
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setUsuarioLogado(getUsuarioLogado());
			mov.setSubsistema(getSubSistema().getId());
			mov.setCodMovimento(SigaaListaComando.ENVIAR_PROPOSTA_GRUPO_PESQUISA);
			execute(mov);
			if (isReprepare())
				prepareMovimento(SigaaListaComando.ENVIAR_PROPOSTA_GRUPO_PESQUISA);
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
	}

	/**
	 * Serve para o envio de email para os membros.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/termo.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public void enviarEmail() throws ArqException {
	
		if ( !isPesquisadorSelecionado() ) {
			addMensagemErro("Selecione ao menos um membro para o envio do e-mail.");
			return;
		} 
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setUsuarioLogado(getUsuarioLogado());
		mov.setCodMovimento(SigaaListaComando.ENVIAR_EMAIL_MEMBROS_PROPOSTA_GRUPO_PESQUISA);
		prepareMovimento(SigaaListaComando.ENVIAR_EMAIL_MEMBROS_PROPOSTA_GRUPO_PESQUISA);
		
		try {
			execute(mov);
			if (isReprepare())
				prepareMovimento(SigaaListaComando.ENVIAR_EMAIL_MEMBROS_PROPOSTA_GRUPO_PESQUISA);
			addMensagemInformation("Email(s) enviado(s) com sucesso");
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
	}

	/**
	 * Verifica se o pesquisador está selecionado.
	 * @return
	 */
	private boolean isPesquisadorSelecionado() {
		for (MembroGrupoPesquisa membro : obj.getEquipesGrupoPesquisaCol() ) {
			if ( membro.isSelecionado() ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Serve para carregar os dados do grupo de pesquisa na parte pública.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>Não invocado por JSP's</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public void visualizarGrupoPartePublica() throws ArqException{
		carregarGrupoPartePublica();
		exibeBotoes = true;
		redirect( telaAssinaturaDigital() );
	}

	/**
	 * Verifica se deve exibir o formulário para o usuário.
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/public/pesquisa/assinatura_digital_grupo_pesquisa.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isExibirForm() {
		if ( isEmpty( membroPainel ) || isEmpty( membroPainel.getAssinado() ) )
			return true;
		return false;
	}
	
	/**
	 * Serve para cadastrar os informações do grupo de pesquisa.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>Não invocado por JSP's</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public void carregarGrupoPartePublica() throws ArqException{
		MembroGrupoPesquisaDao dao = getDAO(MembroGrupoPesquisaDao.class);
		try {
			if (codigo.contains(".jsp") || codigo.contains(".jsf"))
				codigo = codigo.substring(0, codigo.length()-4);
			
			membroPainel = dao.findbyCodigo(codigo);
			if (membroPainel != null) {
				setObj( membroPainel.getGrupoPesquisa() );
				carregarCamposLazy();
				membroPainel.getPessoa().setCpf_cnpj(null);
				membroPainel.setSenhaConfirmacao("");
			}
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Serve para o membro assinar a sua participação no grupo de pesquisa.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/public/pesquisa/assinatura_digital_grupo_pesquisa.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String assinar() throws ArqException {
		if ( isEmpty(obj) )
			carregarGrupoPartePublica();
		else 
			prepareMovimento(SigaaListaComando.ASSINAR_PROPOSTA_GRUPO_PESQUISA);
		
		MembroGrupoPesquisa memb = getGenericDAO().findByPrimaryKey(membroPainel.getId(), MembroGrupoPesquisa.class);
		
		if ( isEmpty(memb) || !memb.isAtivo() ) {
			membroPainel = null;
			return redirect( telaAssinaturaDigital() );	
		}
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setObjAuxiliar(membroPainel);
		mov.setUsuarioLogado(getUsuarioLogado());
		mov.setCodMovimento(SigaaListaComando.ASSINAR_PROPOSTA_GRUPO_PESQUISA);
		try {
			execute(mov);

			if(membroPainel.isSelecionado())
				addMensagemInformation("Assinatura Digital realizada com sucesso!");
			else
				addMensagemWarning("Você não assinou digitalmente sua participação no grupo de pesquisa.");
		
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		
		carregarGrupoPartePublica();
		
		exibeBotoes = false;
		return redirect( telaAssinaturaDigital() );
	}

	/**
	 * Exibir certificado de participação em um Grupo de Pesquisa.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String certificado() throws DAOException {
		setId();
		setObj( getGenericDAO().findByPrimaryKey(obj.getId(), GrupoPesquisa.class) );
		carregarCamposLazy();
		exibeBotoes = false;
		return forward(ConstantesNavegacaoPesquisa.PROPOSTAGRUPO_EMITIR_CERTIFICADO);
	}
	
	/**
	 * Reponsável pela visualização do comprovante
	 * 
  	 * <br/>
   	 * Método chamado pelas seguintes JSPs:
   	 * 		<ul>
	 * 		  <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/minhas_propostas.jsp</li>
	 *      </ul>
	 *      
	 * @return
	 * @throws DAOException
	 */
	public String verComprovante() throws DAOException {
		obj = (GrupoPesquisa) gruposPesquisa.getRowData();
		carregarCamposLazy();
		return telaComprovante();
	}
	
	/**
	 * Esse método tem com finalidade a realização de uma consulta em uma capa de processo.
  	 * <br/>
   	 * Método chamado pelas seguintes JSPs:
   	 * 		<ul>
	 * 		  <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/comprovante.jsp</li>
	 *      </ul>
	 * @return
	 */
	public String consultaCapaProcesso() {
		if(obj.getNumeroProtocolo() != null && obj.getAnoProtocolo() != null){
			TokenAutenticacao token = generator.generateToken(getUsuarioLogado(), getSistema(), String.valueOf(obj.getNumeroProtocolo()), String.valueOf(obj.getAnoProtocolo()));
			return redirectSemContexto(RepositorioDadosInstitucionais.getLinkSipac() + "/sipac/visualizaCapaProcesso?numero=" + obj.getNumeroProtocolo() + "&ano=" + 
					obj.getAnoProtocolo() + "&key=" + token.getKey() + "&id=" + token.getId());
		} else {
			addMensagemErro("Não foi localizado o processo no sistema de protocolos correspondente a esta proposta de criação de grupo de pesquisa.");
			return null;
		}
	}
	
	/**
	 * Serve para direcionar o usuário para a tela dos dados gerais.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>Não invocado por JSP's</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String telaProposta(){
		return forward(ConstantesNavegacaoPesquisa.PROPOSTAGRUPO_DADOS_GERAIS);
	}

	/**
	 * Serve para direcionar o usuário para a tela da assinatura digital.
	 * Método chamado pelas seguintes JSPs:
	 *  <ul>
	 * 		<li>Não invocado por JSP's</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String telaAssinaturaDigital(){
		return ConstantesNavegacaoPesquisa.PROPOSTAGRUPO_ASSINATURA_DIGITAL;
	}
	
	/**
	 * propostaGrupoPesquisaMBean.telaComprovante
	 * Método chamado pelas seguintes JSPs:
	 *  <ul>
	 * 		<li>Não invocado por JSP's</li>
	 * </ul>
	 */
	public String telaComprovante() {
		return forward(ConstantesNavegacaoPesquisa.PROPOSTAGRUPO_COMPROVANTE);
	}
	
	public UIData getMembrosPermanentes() {
		return membrosPermanentes;
	}

	public void setMembrosPermanentes(UIData membrosPermanentes) {
		this.membrosPermanentes = membrosPermanentes;
	}

	public UIData getMembrosAssociados() {
		return membrosAssociados;
	}

	public void setMembrosAssociados(UIData membrosAssociados) {
		this.membrosAssociados = membrosAssociados;
	}

	public UIData getMembrosTermoConcordancia() {
		return membrosTermoConcordancia;
	}

	public void setMembrosTermoConcordancia(UIData membrosTermoConcordancia) {
		this.membrosTermoConcordancia = membrosTermoConcordancia;
	}

	public AreaConhecimentoCnpq getArea() {
		return area;
	}

	public void setArea(AreaConhecimentoCnpq area) {
		this.area = area;
	}

	public Collection<SelectItem> getSubareas() {
		return subareas;
	}

	public void setSubareas(Collection<SelectItem> subareas) {
		this.subareas = subareas;
	}

	public Servidor getDocentePermanente() {
		return docentePermanente;
	}

	public void setDocentePermanente(Servidor docentePermanente) {
		this.docentePermanente = docentePermanente;
	}

	public Servidor getDocenteAssociado() {
		return docenteAssociado;
	}

	public void setDocenteAssociado(Servidor docenteAssociado) {
		this.docenteAssociado = docenteAssociado;
	}

	public MembroGrupoPesquisa getMembroPainel() {
		return membroPainel;
	}

	public void setMembroPainel(MembroGrupoPesquisa membroPainel) {
		this.membroPainel = membroPainel;
	}

	public LinhaPesquisa getLinha() {
		return linha;
	}

	public void setLinha(LinhaPesquisa linha) {
		this.linha = linha;
	}

	public DataModel getGruposPesquisa() {
		return gruposPesquisa;
	}

	public void setGruposPesquisa(DataModel gruposPesquisa) {
		this.gruposPesquisa = gruposPesquisa;
	}

	public Discente getDiscenteAssociado() {
		return discenteAssociado;
	}

	public void setDiscenteAssociado(Discente discenteAssociado) {
		this.discenteAssociado = discenteAssociado;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public boolean isExibeBotoes() {
		return exibeBotoes;
	}

	public void setExibeBotoes(boolean exibeBotoes) {
		this.exibeBotoes = exibeBotoes;
	}

	public int getTotalCaracteres() {
		return totalCaracteres;
	}

	public void setTotalCaracteres(int totalCaracteres) {
		this.totalCaracteres = totalCaracteres;
	}

	/**
	 * Retorna o histórico de modificações de status realizadas no grupo de pesquisa.
	 *  
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/view.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<HistoricoGrupoPesquisa> getHistoricoGrupoPesquisa() throws DAOException{
		return getGenericDAO().findByExactField(HistoricoGrupoPesquisa.class, "grupoPesquisa.id", obj.getId(), "asc", "data");
	}

	public Boolean getEstrangeiro() {
		return estrangeiro;
	}

	public void setEstrangeiro(Boolean estrangeiro) {
		this.estrangeiro = estrangeiro;
	}
	
	/**
	 * Serve para mudar o informação do campo cpf para passaporte (caso o usuário não possua cpf).
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/membros.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 */
	public void changeEstrangeiro(ValueChangeEvent e) {
		setAbaAtivaMembrosAssociados(AbasMembrosAssociados.EXTERNO.ordinal());
		estrangeiro = (Boolean) e.getNewValue();
		redirectMesmaPagina();
	}

	public DocenteExterno getDocenteExterno() {
		return docenteExterno;
	}

	public void setDocenteExterno(DocenteExterno docenteExterno) {
		this.docenteExterno = docenteExterno;
	}
	
	/**
	 * Carrega os dados da pessoa baseado no CPF informando.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/proposta/membros.jsp</li>
	 * </ul>
	 */
	public void carregaDocenteExterno() throws DAOException {
		setAbaAtivaMembrosAssociados(AbasMembrosAssociados.EXTERNO.ordinal());
		PessoaDao pessoaDao = getDAO(PessoaDao.class);
		try {
			if ( ( docenteExterno.getPessoa().getCpf_cnpj() != null && !getEstrangeiro() )  
					|| !docenteExterno.getPessoa().getPassaporte().isEmpty() ) {
				
				Pessoa p = null;
				Long cpf = null;
				String passaporte = null;
				if ( docenteExterno.getPessoa().getCpf_cnpj() != null && !getEstrangeiro() )
					p = pessoaDao.findByCpf(docenteExterno.getPessoa().getCpf_cnpj());
				else 
					p = pessoaDao.findByPassaporte(docenteExterno.getPessoa().getPassaporte());
			
				if (p != null){ 
					docenteExterno.setPessoa(p);
					setCpfEncontrado(true);
				} else{
					if ( docenteExterno.getPessoa().getCpf_cnpj() != null && !getEstrangeiro() )
						cpf = docenteExterno.getPessoa().getCpf_cnpj();
					else
						passaporte = docenteExterno.getPessoa().getPassaporte();
					
					docenteExterno = new DocenteExterno();
					docenteExterno.getPessoa().setSexo(PessoaGeral.SEXO_MASCULINO);
					if ( docenteExterno.getPessoa() != null && !getEstrangeiro() )
						docenteExterno.getPessoa().setCpf_cnpj(cpf);
					else
						docenteExterno.getPessoa().setPassaporte(passaporte);
					setCpfEncontrado(false);
					redirectMesmaPagina();
				}
			
			} else {
				docenteExterno.setPessoa(new Pessoa());
				docenteExterno.getPessoa().setSexo(PessoaGeral.SEXO_MASCULINO);
				setCpfEncontrado(false);
				redirectMesmaPagina();
			}
		} finally {
			pessoaDao.close();
		}
	}

	public boolean isCpfEncontrado() {
		return cpfEncontrado;
	}

	public void setCpfEncontrado(boolean cpfEncontrado) {
		this.cpfEncontrado = cpfEncontrado;
	}
	
	/**
	 * Método responsável pela adição de uma novo docente externo a proposta.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/docentes.jsp
	 * 	
	 * @throws ArqException
	 * @throws RemoteException 
	 */
	public void adicionarDocenteExterno() throws ArqException, RemoteException {
		setAbaAtivaMembrosAssociados(AbasMembrosAssociados.EXTERNO.ordinal());
		erros = new ListaMensagens();
		if (estrangeiro) 
			ValidatorUtil.validateRequired(docenteExterno.getPessoa().getPassaporte(), "Passaporte", erros);	
		else { 
			if (docenteExterno.getPessoa().getCpf_cnpj() != null ) 
				ValidatorUtil.validateCPF_CNPJ(docenteExterno.getPessoa().getCpf_cnpj(), "CPF", erros);
			else
				ValidatorUtil.validateRequired(null, "CPF", erros);
		}
		
		ValidatorUtil.validateRequired(docenteExterno.getPessoa().getNome(), "Nome", erros);
		ValidatorUtil.validateRequired(docenteExterno.getPessoa().getEmail(), "Email", erros);
		ValidatorUtil.validateEmail(docenteExterno.getPessoa().getEmail(), "Email", erros);
		ValidatorUtil.validateRequired(docenteExterno.getFormacao(), "Formação", erros);
		ValidatorUtil.validateRequired(docenteExterno.getInstituicao(), "Instituição", erros);
		
				
		if (!hasOnlyErrors()) {
			Pessoa p = new Pessoa();
			PessoaDao pessoaDao = getDAO(PessoaDao.class);
			if (estrangeiro) {
				p = pessoaDao.findByPassaporte(docenteExterno.getPessoa().getPassaporte());
				int id = docenteExterno.getPessoa().getId();
				docenteExterno.getPessoa().setId( p == null ? 0 : id );
				docenteExterno.getPessoa().setInternacional(true);
			}

			MovimentoCadastroDocenteExterno movDocExt = new MovimentoCadastroDocenteExterno();
			if ( docenteExterno.getPessoa().getId() > 0 ) {
				Pessoa pessoa = pessoaDao.findAndFetch( docenteExterno.getPessoa().getId(), Pessoa.class, "enderecoContato", "contaBancaria");
				docenteExterno.getPessoa().setEnderecoContato( pessoa.getEnderecoContato() );
				docenteExterno.getPessoa().setContaBancaria( pessoa.getContaBancaria() );
			}
			
			if ( isEmpty(p) ) {
				prepareMovimento(SigaaListaComando.CADASTRAR_DOCENTE_EXTERNO);
				movDocExt.setCodMovimento(SigaaListaComando.CADASTRAR_DOCENTE_EXTERNO);
			} else {
				prepareMovimento(SigaaListaComando.ALTERAR_DOCENTE_EXTERNO);
				movDocExt.setCodMovimento(SigaaListaComando.ALTERAR_DOCENTE_EXTERNO);
			}
			docenteExterno.getTipoDocenteExterno().setId(TipoDocenteExterno.PROFESSOR_EXTERNO);
			docenteExterno.setPrazoValidade(new Date());
			docenteExterno.setServidor(null);
			try {
				movDocExt.setObjMovimentado(docenteExterno);
				movDocExt.setSistema(getSistema());
				movDocExt.setUsuarioLogado(getUsuarioLogado());
				movDocExt.setAcao(MovimentoCadastroDocenteExterno.ACAO_NAO_GERAR_MATRICULA);
				execute(movDocExt);
				addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Pesquisador Externo");
			} catch (NegocioException e) {
				addMensagensAjax(e.getListaMensagens());
				return;
			}finally {
				pessoaDao.close();
			}
			createAddMembro(null, null, docenteExterno, MembroGrupoPesquisa.MEMBRO, CategoriaMembro.EXTERNO, TipoMembroGrupoPesquisa.ASSOCIADO);
			gravarTemporariamente();
			docenteExterno = new DocenteExterno();
			docenteExterno.getPessoa().setSexo(PessoaGeral.SEXO_MASCULINO);
			setCpfEncontrado(false);
		}
		redirectMesmaPagina();
	}

	public int getAbaAtivaMembrosAssociados() {
		return abaAtivaMembrosAssociados;
	}

	public void setAbaAtivaMembrosAssociados(int abaAtivaMembrosAssociados) {
		this.abaAtivaMembrosAssociados = abaAtivaMembrosAssociados;
	}

	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		return getAllAtivo(GrupoPesquisa.class, "id", "nome");
	}
	
}