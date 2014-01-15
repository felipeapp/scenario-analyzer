/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 23/05/2008

 */
package br.ufrn.sigaa.pesquisa.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.ValidadorCPFCNPJ;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.GrupoPesquisaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.PessoaMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.negocio.PessoaValidator;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.MembroGrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.StatusGrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoMembroGrupoPesquisa;
import br.ufrn.sigaa.pesquisa.negocio.ProjetoPesquisaValidator;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;

/**
 * MBean responsável pelas operações de gerenciamento dos Grupos de Pesquisa da Instituição.
 * 
 * @author Leonardo Campos
 */
@Component("grupoPesquisa") @Scope("session")
public class GrupoPesquisaMBean extends SigaaAbstractController<GrupoPesquisa> {

	/** Página do formulário de criação do grupo de pesquisa */
	public static final String JSP_FORM = "/pesquisa/GrupoPesquisa/form.jsp";
	/** Página da listagem dos grupos de pesquisa */
	public static final String JSP_LISTA = "/pesquisa/GrupoPesquisa/lista.jsp";
	/** Página da visualização do grupo de pesquisa */
	public static final String JSP_VIEW = "/pesquisa/GrupoPesquisa/view.jsp";
	
	/** Docente Membro do Grupo de Pesquisa */
	private Servidor docente = new Servidor();
	/** Servidor Membro do Grupo de Pesquisa */
	private Servidor servidor = new Servidor();
	/** Discente Membro do Grupo de Pesquisa */
	private Discente discente = new Discente();
	/** Docente Externo Membro do Grupo de Pesquisa */
	private DocenteExterno docenteExterno = new DocenteExterno();

	/** Avisos Ajax. */
	private List<MensagemAviso> avisosAjax = new ArrayList<MensagemAviso>();
	
	/** CPF utilizado no cadastro de membros externos. */
	private String cpf = new String("");
	
	/** Membro do grupo utilizado no formulário de cadastro. */
	private MembroGrupoPesquisa membroEquipe = new MembroGrupoPesquisa();
	
	/** Utilizado para armazenar os membros ativos e adicionados ao Grupo de Pesquisa. */
	private Collection<MembroGrupoPesquisa> membrosGrupoPesquisa = new ArrayList<MembroGrupoPesquisa>();
	
	/** Armazena o centro informado na pesquisa. */
	private Character centro;
	
	/** Filtro do nome utilizado na busca */
	private boolean filtroNome;
	/** Filtro do coordenador utilizado na busca */
	private boolean filtroCoordenador;
	/** Filtro da área utilizado na busca */
	private boolean filtroArea;
	/** Filtro do status utilizado na busca */
	private boolean filtroStatus;
	/** Filtro do centro utilizado na busca */
	private boolean filtroCentro;
	/** Filtragem pelo grupos ainda ativos utilizado na busca */
	private boolean filtroAtivo;
	/** Filtro do código utilizado na busca */
	private boolean filtroCodigo;
	/** Indica se o usuário pode alterar ou não. */
	private boolean alterar = false;
	
	/** Construtor */
	public GrupoPesquisaMBean() {
		clear();
	}
	
	/**
	 * Método responsável por popular uma combo contendo todos os Grupos de Pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/GrupoPesquisa/form.jsp</li>
	 *  	<li>sigaa.war/pesquisa/invencao/vinculos.jsp</li>
	 *  	<li>sigaa.war/prodocente/producao/relatorios/avaliacao_grupo_pesquisa/form.jsp</li>
	 *	</ul>
	 */
	public Collection<SelectItem> getAllCombo() throws DAOException{
		return toSelectItems(getGenericDAO().findAll(GrupoPesquisa.class), "id", "nome");
	}
	
	/**
	 * Método responsável por popular uma combo contendo os tipos de pesquisa dos Grupos de Pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/GrupoPesquisa/form.jsp</li>
	 *  	<li>sigaa.war/pesquisa/GrupoPesquisa/lista.jsp</li>
	 *	</ul> 
	 */
	public Collection<SelectItem> getTiposStatusCombo() {
		return toSelectItems(StatusGrupoPesquisa.getTiposStatus());
	}

	/**
	 * Método não invocado por JSP´s.
	 */
	@Override
	public String getFormPage() {
		return JSP_FORM;
	}
	
	/**
	 * Método não invocado por JSP´s.
	 */
	@Override
	public String getListPage() {
		return JSP_LISTA;
	}
	
	/**
	 * Método não invocado por JSP´s.
	 */
	@Override
	public String getViewPage() {
		return JSP_VIEW;
	}
	

	/**
	 * Limpa o objeto que o MBean gerência.
	 */
	private void clear() {
		obj = new GrupoPesquisa();
		obj.setCoordenador(new Servidor());
		obj.setAreaConhecimentoCnpq(new AreaConhecimentoCnpq());
		alterar = false;
		membroEquipe = new MembroGrupoPesquisa();
		membroEquipe.setTipoMembroGrupoPesquisa(new TipoMembroGrupoPesquisa());
		membrosGrupoPesquisa = new ArrayList<MembroGrupoPesquisa>();
		limparFiltrosBusca();
	}
	
	/**
	 * Limpa os checkbox's do formulário de busca de grupos de pesquisa.
	 */
	private void limparFiltrosBusca() {
		filtroArea = false;
		filtroCoordenador = false;
		filtroNome = false;
		filtroStatus = false;
	}

	/**
	 * Ação realizada depois de atualizar, consiste na comparação e 
	 * instanciação dos atributos desde que ele seja nulo
	 * para área de conhecimentoCNPQ e de um coordenador. 
	 * <br>
	 * Método não invocado por JSP´s
	 */
	@Override
	public void afterAtualizar() throws ArqException {
		if(obj.getAreaConhecimentoCnpq() == null)
			obj.setAreaConhecimentoCnpq(new AreaConhecimentoCnpq());
		if(obj.getCoordenador() == null)
			obj.setCoordenador(new Servidor());
		
		membroEquipe = new MembroGrupoPesquisa();
		membroEquipe.setTipoMembroGrupoPesquisa(new TipoMembroGrupoPesquisa());
	}

	/**
	 * Popula os dados necessários para o cadastro e encaminha para a página de cadastro.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *  	<li>sigaa.war/pesquisa/GrupoPesquisa/lista.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		setConfirmButton("Cadastrar");
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_GRUPO_PESQUISA.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_GRUPO_PESQUISA);
		clear();
		return forward(JSP_FORM);
	}
	/**
	 * Esse método é invocado antes do remover com o intuito de verificar a área cnpq é o coordenador
	 * para que possa ver realizada a remoção. 
	 * <br>
	 * Método não invocado por JSP´s 
	 */
	@Override
	public void beforeRemover() throws DAOException {
		
		if (obj.getAreaConhecimentoCnpq().getId() == 0) {
			obj.setAreaConhecimentoCnpq(null);
		}
		if (obj.getCoordenador().getId() == 0) {
			obj.setCoordenador(null);
		}
		super.beforeRemover();
	}
	/**
	 * Esse caso de uso é invocado quando se deseja alterar o campo ativo do Grupo de pesquisa de true para false.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *  	<li>sigaa.war/pesquisa/GrupoPesquisa/lista.jsp</li>
	 *	</ul>
	 */
	@Override
	public String inativar() throws ArqException, NegocioException {
		prepareMovimento(ArqListaComando.DESATIVAR);
		return super.inativar();
	}
	
	/**
	 * Checa os papéis e encaminha para a listagem dos grupos de pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/menu/auxiliares.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	@Override
	public String listar() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		clear();
		resultadosBusca = null;
		return forward(JSP_LISTA);
	}

	/**
	 * Checa os papéis e encaminha para a listagem dos grupos de pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/GrupoPesquisa/declaracao.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String lista() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		return forward(JSP_LISTA);
	}
	
	/**
	 * Popula o objeto e encaminha para a página de visualização.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *  	<li>sigaa.war/pesquisa/GrupoPesquisa/lista.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String view() throws ArqException {
		setId();
		PersistDB obj = this.obj;

		this.obj = getGenericDAO().findByPrimaryKey(obj.getId(), GrupoPesquisa.class);
		
		return forward(getViewPage());
	}
	
	/**
	 * Executa a busca por grupos de pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *  	<li>sigaa.war/pesquisa/GrupoPesquisa/lista.jsp</li>
	 *	</ul>
	 */
	public String buscar() throws DAOException, ArqException {
		GrupoPesquisaDao dao = getDAO(GrupoPesquisaDao.class);

		setResultadosBusca(null);
		
		String nome = null;
		String nomeCoordenador = null;
		Integer idArea = null;
		Integer status = null;
		Boolean ativo = true;
		String codigo = null;
		Character centro = null;
		
		ListaMensagens erros = new ListaMensagens();

		if (filtroNome){
			nome = obj.getNome();
			ValidatorUtil.validateRequired(nome, "Nome da Base", erros);
		}
		if (filtroCoordenador){
			nomeCoordenador = obj.getCoordenador().getPessoa().getNome();
			ValidatorUtil.validateRequired(nomeCoordenador, "Coordenador", erros);
		}
		if (filtroArea){
			idArea = obj.getAreaConhecimentoCnpq().getId();
			ValidatorUtil.validateRequiredId(idArea, "Grande área", erros);
		}
		if (filtroStatus){
			status = obj.getStatus();
			ValidatorUtil.validateRequiredId(status, "Status", erros);
		}
		if (filtroAtivo) {
			ativo = obj.isAtivo();
			ValidatorUtil.validateRequired(ativo, "Situação", erros);
		}
		if (filtroCodigo) {
			codigo = obj.getCodigo();
			ValidatorUtil.validateRequired(codigo, "Código", erros);
		}
		if (filtroCentro) {
			centro = getCentro();
			if (centro.equals('0'))
				erros.addErro("Escolha um Centro para consulta");
		}
		
		if( erros.isEmpty() ) {

			Collection<GrupoPesquisa> lista = dao.findOtimizado(nome, nomeCoordenador, idArea, ativo, codigo, centro, status);

			if (!lista.isEmpty()) {
				setResultadosBusca(lista);
			} else {
				addMessage("Nenhuma base de pesquisa foi encontrada de acordo com os critérios de busca utilizados.", TipoMensagemUFRN.WARNING);
			}
		} else
			addMensagens(erros);


		return forward(getListPage());
	}
	
	/**
	 * Faz uma consulta para verificar se já existe uma pessoa com o cpf digitado no banco.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *  	<li>sigaa.war/pesquisa/GrupoPesquisa/form.jsp</li>
	 *	</ul>
	 * 
	 * @throws DAOException
	 */
	public void buscarDocenteExternoByCPF() throws DAOException {
		PessoaDao pessoaDao = getDAO(PessoaDao.class);
		try {

			// limpa os dados
			docente = new Servidor();
			servidor = new Servidor();

			membroEquipe = new MembroGrupoPesquisa();
			membroEquipe.setGrupoPesquisa(this.obj);
			membroEquipe.setClassificacao(MembroGrupoPesquisa.MEMBRO);
			membroEquipe.setDocenteExterno(new DocenteExterno());

			if (docenteExterno == null)
				docenteExterno = new DocenteExterno();

			// pessoa internacional
			if (docenteExterno.getPessoa().isInternacional()) {
				docenteExterno.setPessoa(new Pessoa());
				membroEquipe.setDocenteExterno(docenteExterno);
				// permite a edição do nome da pessoa pelo usuário
				membroEquipe.setSelecionado(true);
				cpf = "";
			} else {
				// não permite inclusão do nome da pessoa
				membroEquipe.setSelecionado(false);
			}

			if ((cpf != null) && (!cpf.trim().equals(""))) {
				avisosAjax.clear();

				if (!ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(cpf)) {
					getAvisosAjax().add(
							new MensagemAviso("CPF inválido!",
									TipoMensagemUFRN.ERROR));

				} else {

					Pessoa p = pessoaDao.findByCpf(Long.parseLong(cpf));
					docenteExterno.setPessoa(p); // seta a pessoa encontrada

					if (docenteExterno.getPessoa() == null) {
						p = new Pessoa();
						p.setCpf_cnpjString(cpf);
						docenteExterno.setPessoa(p);
						membroEquipe.setDocenteExterno(docenteExterno);
						// permite a edição do nome da pessoa pelo usuário
						membroEquipe.setSelecionado(true);
					} else {
						// não permite inclusão do nome da pessoa
						membroEquipe.setSelecionado(false);
					}
				}
			}
		} catch (Exception e) {
			notifyError(e);
		} finally {
			pessoaDao.close();
		}
		membroEquipe.setTipoMembroGrupoPesquisa(new TipoMembroGrupoPesquisa());
		getCurrentSession().setAttribute("aba", "membro-externo");
		getCurrentSession().setAttribute("categoriaAtual", CategoriaMembro.EXTERNO);
	}
	
	/**
	 * Adiciona um discente a lista de membros do grupo de pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *  	<li>sigaa.war/pesquisa/GrupoPesquisa/form.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 */
	public String adicionaDiscente() {
		ListaMensagens mensagens = new ListaMensagens();

		try {
			getCurrentSession().setAttribute("aba", "membro-discente");
			ValidatorUtil.validateRequired(discente, "Discente", mensagens);
			ValidatorUtil.validateRequiredId(membroEquipe.getClassificacao(), "Classificação", mensagens);
			ValidatorUtil.validateRequiredId(membroEquipe.getTipoMembroGrupoPesquisa().getId(), "Tipo", mensagens);
			ValidatorUtil.validateRequired(membroEquipe.getDataInicio(), "Data Início", mensagens);			
			ValidatorUtil.validaInicioFim(membroEquipe.getDataInicio(), membroEquipe.getDataFim(), "Data Fim", mensagens);
			
			if (!mensagens.isEmpty()) {
				addMensagens(mensagens);
				return null;
			}
			
			discente = getGenericDAO().findByPrimaryKey(discente.getId(),
					Discente.class);

			membroEquipe.setDiscente(discente);
			membroEquipe.setPessoa(discente.getPessoa());
			membroEquipe.setCategoriaMembro(getGenericDAO().findByPrimaryKey(CategoriaMembro.DISCENTE, CategoriaMembro.class));
			
			// validação
			if (!mensagens.isEmpty()) {
				membroEquipe = new MembroGrupoPesquisa();
				addMensagens(mensagens);
				return null;
			}

			if (!membrosGrupoPesquisa.contains(membroEquipe)) {

				// limpando outros atributos para não dar
				// TransientObjectException
				membroEquipe.setServidor(null);
				membroEquipe.setDocenteExterno(null);

				membrosGrupoPesquisa.add(membroEquipe);

			} else {
				if (alterar) {
					membrosGrupoPesquisa.add(membroEquipe);
				} else {
					addMensagemErro("Discente já inserido na atividade");
				}
			}

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}

		popularDadosMembroProjeto();

		return null;
	}
	
	/**
	 * Adiciona um servidor a lista de membros do grupo de pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *  	<li>sigaa.war/pesquisa/GrupoPesquisa/form.jsp</li>
	 *	</ul>
	 * 
	 * @return null
	 * @throws RemoteException 
	 */
	public String adicionarMembroEquipe() throws ArqException, RemoteException {

		GenericDAO dao = getGenericDAO();
		ListaMensagens mensagens = new ListaMensagens();

		// qual o tipo de membro será adicionado (default = docente)
		int categoriaMembro = getParameterInt("categoriaMembro",CategoriaMembro.DOCENTE);

		membroEquipe.setCategoriaMembro(dao.findByPrimaryKey(categoriaMembro, CategoriaMembro.class));

		// método para adicionar discente
		if (CategoriaMembro.DISCENTE == categoriaMembro) {
			return adicionaDiscente();
		}
		// Validação
		if(categoriaMembro == CategoriaMembro.DOCENTE)
			ValidatorUtil.validateRequired(docente, "Docente", mensagens);
		else if(categoriaMembro == CategoriaMembro.SERVIDOR)
			ValidatorUtil.validateRequired(servidor, "Servidor", mensagens);
		ValidatorUtil.validateRequiredId(membroEquipe.getClassificacao(), "Classificação", mensagens);
		ValidatorUtil.validateRequiredId(membroEquipe.getTipoMembroGrupoPesquisa().getId(), "Tipo", mensagens);
		ValidatorUtil.validateRequired(membroEquipe.getDataInicio(), "Data Início", mensagens);
		ValidatorUtil.validaInicioFim(membroEquipe.getDataInicio(), membroEquipe.getDataFim(), "Data Fim", mensagens);
		
		
		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
		}
		membroEquipe.setTipoMembroGrupoPesquisa(dao.findByPrimaryKey(membroEquipe.getTipoMembroGrupoPesquisa().getId(), TipoMembroGrupoPesquisa.class));
		membroEquipe.setDiscente(null);

		if (CategoriaMembro.EXTERNO == categoriaMembro) {

			// evitar np
			if ((docenteExterno == null)
					|| (docenteExterno.getPessoa() == null)) {
				docenteExterno = new DocenteExterno();
				cpf = "";
				return null;
			}

			if ((docenteExterno.getPessoa().isInternacional())
					|| (ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(cpf))) {

				if ((membroEquipe != null) && (docenteExterno != null)) {
					if (!mensagens.isEmpty()) {
						addMensagens(mensagens);
						return null;
					}
				} else {
					addMensagemErro("Dados obrigatórios para o cadastro do membro da equipe não foram informados");
					return null;
				}

			} else {
				addMensagemErro("CPF digitado para o participante externo é ínválido!");
				return null;
			}

			if (docenteExterno.getPessoa() != null) {
				docenteExterno.getPessoa().setNome(docenteExterno.getPessoa().getNome().trim().toUpperCase());
				membroEquipe.setDocenteExterno(docenteExterno);
				membroEquipe.setPessoa(docenteExterno.getPessoa());
			}

			getCurrentSession().setAttribute("aba", "membro-externo");

			// não é pessoa externa...
		} else {

			if (CategoriaMembro.SERVIDOR == categoriaMembro) {
				membroEquipe.setServidor(getGenericDAO().findByPrimaryKey(
						servidor.getId(), Servidor.class));
				// evitando erro de lazy na validação
				membroEquipe.getServidor().getEscolaridade().getId();
				getCurrentSession().setAttribute("aba", "membro-servidor");
			}

			if (CategoriaMembro.DOCENTE == categoriaMembro) {
				membroEquipe.setServidor(getGenericDAO().findByPrimaryKey(
						docente.getId(), Servidor.class));
				// evitando erro de lazy na validação
				membroEquipe.getServidor().getEscolaridade().getId();
				getCurrentSession().setAttribute("aba", "membro-docente");
			}

			if ((CategoriaMembro.SERVIDOR == categoriaMembro)
					|| (CategoriaMembro.DOCENTE == categoriaMembro)) {
				membroEquipe.setPessoa(membroEquipe.getServidor().getPessoa());
			}

		}

		// informa qual o tipo de membro de equipe será cadastrado
		getCurrentSession().setAttribute("categoriaAtual", categoriaMembro);

		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
		}

		// /FIM DAS VALIDAÇÕES, AGORA VAI GRAVAR NO BANCO MESMO....
		if (CategoriaMembro.EXTERNO == categoriaMembro) {

			// verifica se a pessoa externa já está na base de dados (pelo
			// cpf_cnpj) se não estiver, inclui...
			// se tiver, retorna a pessoa encontrada no banco
			// se for extrangeiro inclui sempre
			Pessoa pessoa = adicionaPessoaExterna(membroEquipe.getDocenteExterno().getPessoa());

			membroEquipe.setPessoa(pessoa);

			// seta a pessoa encontrada ou que acabou de ser incluída...
			membroEquipe.getDocenteExterno().setPessoa(pessoa);

		}

		if (!membrosGrupoPesquisa.contains(membroEquipe)) {

			if ((CategoriaMembro.SERVIDOR == categoriaMembro)
					|| (CategoriaMembro.DOCENTE == categoriaMembro))
				membroEquipe.setDocenteExterno(null);

			if (CategoriaMembro.EXTERNO == categoriaMembro)
				membroEquipe.setServidor(null);

			membrosGrupoPesquisa.add(membroEquipe);

		} else {
			membrosGrupoPesquisa.remove(membroEquipe);
			membrosGrupoPesquisa.add(membroEquipe);
		}

		// limpa os dados
		popularDadosMembroProjeto();
		addMensagemWarning("Quando estiver adicionando ou alterando os membros do grupo, e tiver terminado de fazer todas as mudanças e desejar sair é necessário antes clicar em " + getConfirmButton() + " para confirmar a operação. O botão esta localizado no fim da página. ");
		return null;
	}
	
	/**
	 * Faz com o que o formulário volte para o modo de inserção
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *  	<li>sigaa.war/pesquisa/GrupoPesquisa/form.jsp</li>
	 *	</ul>
	 * 
	 * @param event
	 */
	public void cancelarAlterarMembroEquipe(ActionEvent event) {
		popularDadosMembroProjeto();
		alterar = false;
	}
	
	/**
	 * Prepara objeto para adicionar um membro
	 */
	private void popularDadosMembroProjeto() {
		cpf = "";
		docente = new Servidor();
		discente = new Discente();
		servidor = new Servidor();
		docenteExterno = new DocenteExterno();

		membroEquipe = new MembroGrupoPesquisa();
		membroEquipe.setGrupoPesquisa(this.obj);
		membroEquipe.setDocenteExterno(new DocenteExterno());
		membroEquipe.setTipoMembroGrupoPesquisa(new TipoMembroGrupoPesquisa());
	}
	
	/**
	 * Persiste pessoa externa
	 * 
	 * @param pessoa
	 *            Objeto do tipo pessoa
	 * @return pessoa Objeto do tipo pessoa se cadastrou com sucesso, Retorna
	 *         Null se falhar.
	 * @throws RemoteExceptgetCurrentSession().setAttribute("aba", "membro-docente");ion
	 * @throws ArqException
	 * @throws RemoteException
	 * @throws DAOException
	 */
	private Pessoa adicionaPessoaExterna(Pessoa pessoa) throws ArqException,
			RemoteException {

		PessoaDao pessoaDao = getDAO(PessoaDao.class);
		// busca pessoa por cpf, se não achar, entra para cadastrar...
		// estrangeiros não tem cpf, não tem como verificar se já está no banco.
		// se for estrangeiro, cadastra um novo
		if ((pessoa.isInternacional())
				|| ((pessoaDao.findByCpf(pessoa.getCpf_cnpj()) == null))) {

			pessoa.setTipo('F');

			try {

				ProjetoPesquisaValidator.limparDadosPessoa(pessoa);

				// Cadastrar a pessoa
				PessoaMov pessoaMov = new PessoaMov();
				pessoaMov.setPessoa(pessoa);
				pessoaMov.setTipoValidacao(PessoaValidator.DOCENTE_EXTERNO);
				pessoaMov.setCodMovimento(SigaaListaComando.CADASTRAR_PESSOA);

				prepareMovimento(SigaaListaComando.CADASTRAR_PESSOA);
				pessoa = (Pessoa) execute(pessoaMov,
						getCurrentRequest());

			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return null;

			} finally {

				// Re-preparar o comando anterior
				if(obj.getId() > 0)
					prepareMovimento(ArqListaComando.ALTERAR);
				else
					prepareMovimento(ArqListaComando.CADASTRAR);
					
				pessoaDao.close();

			}

		} else { // a pessoa já está no banco...

			// Buscar pessoa pelo CPF
			pessoa = pessoaDao.findByCpfCnpj(pessoa.getCpf_cnpj()).iterator().next();
			pessoa = pessoaDao.findByPrimaryKey(pessoa.getId(), Pessoa.class);

		}
		return pessoa;

	}
	
	/**
	 * Remove o docente/discente/docente externo da lista de membros do grupo de pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *  	<li>sigaa.war/pesquisa/GrupoPesquisa/form.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 */
	public void removeMembroEquipe(ActionEvent evt) {
		int id = getParameterInt("idMembro", 0);
		MembroGrupoPesquisa membro = new MembroGrupoPesquisa(id);

		if (id > 0) {
			try {
				membro = getGenericDAO().findByPrimaryKey(membro.getId(), MembroGrupoPesquisa.class);
				membro.getPessoa();
				membrosGrupoPesquisa.remove(membro);
				for(MembroGrupoPesquisa m : obj.getEquipesGrupoPesquisa())
					if(m.getId() == membro.getId())
						m.setAtivo(false);
			} catch (Exception e) {
				addMensagemErro("Erro ao Remover Membro do Grupo de Pesquisa.");
				notifyError(e);
			}
		} else {
			int posicao = getParameterInt("posicao", -1);
			CollectionUtils.removePorPosicao(membrosGrupoPesquisa, posicao);
		}

		popularDadosMembroProjeto();
	}
	
	/**
	 * Popula o formulário com os dados do membro.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *  	<li>sigaa.war/pesquisa/GrupoPesquisa/form.jsp</li>
	 *	</ul>
	 * 
	 * @param event
	 * @throws DAOException
	 */
	public void popularMembroEquipe(ActionEvent event) throws DAOException {
		int id = getParameterInt("id");
		
		for (MembroGrupoPesquisa membro : obj.getEquipesGrupoPesquisa()) {
			if (membro.getId() == id) {
				membroEquipe = membro;
				break;
			}
		}
		
		if (membroEquipe == null)
			return;
		
		membroEquipe = getGenericDAO().findByPrimaryKey(membroEquipe.getId(), MembroGrupoPesquisa.class);
		
		if (membroEquipe.isDocenteUFRN()) {
			docente = membroEquipe.getServidor();
			getCurrentSession().setAttribute("aba", "membro-docente");
			getCurrentSession().setAttribute("categoriaAtual", CategoriaMembro.DOCENTE);
		} else if (membroEquipe.isServidorUFRN()) {
			servidor = membroEquipe.getServidor();
			getCurrentSession().setAttribute("aba", "membro-servidor");
			getCurrentSession().setAttribute("categoriaAtual", CategoriaMembro.SERVIDOR);
		} else if (membroEquipe.isDiscenteUFRN()) {
			discente = membroEquipe.getDiscente();
			getCurrentSession().setAttribute("aba", "membro-discente");
			getCurrentSession().setAttribute("categoriaAtual", CategoriaMembro.DISCENTE);
		} else if (membroEquipe.isDocenteExternoUFRN()) {
			docenteExterno = membroEquipe.getDocenteExterno();
			getCurrentSession().setAttribute("aba", "membro-externo");
			getCurrentSession().setAttribute("categoriaAtual", CategoriaMembro.EXTERNO);
		}

		alterar = true;
	}
	
	
	/**
	 * Responsável pelo cadastro de um Grupo de Pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *  	<li>sigaa.war/pesquisa/GrupoPesquisa/form.jsp</li>
	 *	</ul>
	 */
	@Override
	public String cadastrar() throws ArqException, NegocioException{
		try {
			if (isOperacaoAtiva(SigaaListaComando.CADASTRAR_GRUPO_PESQUISA.getId()) ){
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setCodMovimento(SigaaListaComando.CADASTRAR_GRUPO_PESQUISA);
				mov.setObjMovimentado(obj);
				mov.setColObjMovimentado(membrosGrupoPesquisa);
				execute(mov);
			} else {
				addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
				return cancelar();
			}
			addMensagem(OPERACAO_SUCESSO);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		return forward(getListPage());
	}
	
	
	/**
	 * Método utilitário, altera objetos diretamente do banco de dados.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *  	<li>sigaa.war/pesquisa/GrupoPesquisa/form.jsp</li>
	 *	</ul>
	 * 
	 * @param persistDB
	 * @return
	 * @throws ArqException
	 */
	public String alterar(PersistDB persistDB) {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(persistDB);

		if (persistDB.getId() == 0) {
			addMensagemErro("Não há objeto informado para Alterar");
			return null;
		}

		Comando ultimoComando = getUltimoComando();

		try {
			prepareMovimento(ArqListaComando.ALTERAR);
			mov.setCodMovimento(ArqListaComando.ALTERAR);

			execute(mov, getCurrentRequest());
			addMessage("Alteração realizada com sucesso!", TipoMensagemUFRN.INFORMATION);

			prepareMovimento(ultimoComando);

		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
		}
		return null;
	}

	/**
	 * Método invocado quando deseja alterar um grupo de pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *  	<li>sigaa.war/pesquisa/GrupoPesquisa/lista.jsp</li>
	 *	</ul>
	 * 
	 */
	@Override
	public String atualizar() throws ArqException {
		try {
			clear();
			setOperacaoAtiva(SigaaListaComando.CADASTRAR_GRUPO_PESQUISA.getId());
			prepareMovimento(SigaaListaComando.CADASTRAR_GRUPO_PESQUISA);
			setId();
			setReadOnly(false);
			obj = getGenericDAO().findByPrimaryKey(obj.getId(), GrupoPesquisa.class);
			for(MembroGrupoPesquisa m : obj.getEquipesGrupoPesquisa())
				if(m.isAtivo())
					membrosGrupoPesquisa.add(m);
			setConfirmButton("Alterar");
			popularDadosMembroProjeto();
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
		}
		return forward(getFormPage());
	}
	
	public Servidor getDocente() {
		return docente;
	}

	/**
	 * AutoComplete realizado sobre o Grupo de Pesquisa
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/ProjetoApoioGrupoPesquisa/form.jsp</li>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/ProjetoApoioNovosPesquisadores/form.jsp</li>
	 * </ul>
	 * @param event
	 * @return
	 * @throws DAOException   
	 */
	public Collection<GrupoPesquisa> autocompleteGrupoPesquisa(Object event) throws DAOException {
		Collection<GrupoPesquisa> grupos = new ArrayList<GrupoPesquisa>();
		
		String nome = event.toString();
		GrupoPesquisaDao dao = getDAO(GrupoPesquisaDao.class);
		try {
			grupos = dao.findAutoComplete( nome, getUsuarioLogado().getPessoa().getId() );
		} finally {
			dao.close();
		}
		return grupos;
	}
	
	public void setDocente(Servidor docente) {
		this.docente = docente;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public DocenteExterno getDocenteExterno() {
		return docenteExterno;
	}

	public void setDocenteExterno(DocenteExterno docenteExterno) {
		this.docenteExterno = docenteExterno;
	}

	public List<MensagemAviso> getAvisosAjax() {
		return avisosAjax;
	}

	public void setAvisosAjax(List<MensagemAviso> avisosAjax) {
		this.avisosAjax = avisosAjax;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public MembroGrupoPesquisa getMembroEquipe() {
		return membroEquipe;
	}

	public void setMembroEquipe(MembroGrupoPesquisa membroEquipe) {
		this.membroEquipe = membroEquipe;
	}

	public boolean isFiltroNome() {
		return filtroNome;
	}

	public void setFiltroNome(boolean filtroNome) {
		this.filtroNome = filtroNome;
	}

	public boolean isFiltroCoordenador() {
		return filtroCoordenador;
	}

	public void setFiltroCoordenador(boolean filtroCoordenador) {
		this.filtroCoordenador = filtroCoordenador;
	}

	public boolean isFiltroArea() {
		return filtroArea;
	}

	public void setFiltroArea(boolean filtroArea) {
		this.filtroArea = filtroArea;
	}

	public boolean isFiltroStatus() {
		return filtroStatus;
	}

	public void setFiltroStatus(boolean filtroStatus) {
		this.filtroStatus = filtroStatus;
	}

	public boolean isAlterar() {
		return alterar;
	}

	public void setAlterar(boolean alterar) {
		this.alterar = alterar;
	}

	public void setFiltroCentro(boolean filtroCentro) {
		this.filtroCentro = filtroCentro;
	}

	public boolean isFiltroCentro() {
		return filtroCentro;
	}

	public void setFiltroAtivo(boolean filtroAtivo) {
		this.filtroAtivo = filtroAtivo;
	}

	public boolean isFiltroAtivo() {
		return filtroAtivo;
	}

	public void setFiltroCodigo(boolean filtroCodigo) {
		this.filtroCodigo = filtroCodigo;
	}

	public boolean isFiltroCodigo() {
		return filtroCodigo;
	}

	public void setCentro(Character centro) {
		this.centro = centro;
	}

	public Character getCentro() {
		return centro;
	}

	public Collection<MembroGrupoPesquisa> getMembrosGrupoPesquisa() {
		return membrosGrupoPesquisa;
	}

	public void setMembrosGrupoPesquisa(
			Collection<MembroGrupoPesquisa> membrosGrupoPesquisa) {
		this.membrosGrupoPesquisa = membrosGrupoPesquisa;
	}
	
}
