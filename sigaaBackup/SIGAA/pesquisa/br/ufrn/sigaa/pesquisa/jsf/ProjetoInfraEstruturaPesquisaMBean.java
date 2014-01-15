/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 25/02/2008
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.util.Date;
import java.util.HashSet;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoInfraEstruturaPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.SubProjetoInfraEstruturaPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Controlador responsável pelo cadastro de projetos de infra-estrutura em pesquisa.
 * 
 * @author leonardo
 *
 */
public class ProjetoInfraEstruturaPesquisaMBean extends
		SigaaAbstractController<ProjetoInfraEstruturaPesquisa> {

	private SubProjetoInfraEstruturaPesquisa subProjeto;
	
	/**
	 * Utilizado para armazenar o arquivo do projeto
	 */
	private UploadedFile file;
	
	/**
	 * Utilizado na alteração de sub-projetos
	 */
	private boolean alterar = false;
	
	 /**
     * Construtor padrão. 
     * Inicializa o MBean removendo objetos antigos de sessão e
     * instânciando outros objetos importantes.
     * 
     */
	public ProjetoInfraEstruturaPesquisaMBean(){
		clear();
	}

	 /**
     * Inicializa objetos importantes no cadastro de um novo projeto de infra-estrutura.
     * 
     */
	private void clear() {
		this.obj = new ProjetoInfraEstruturaPesquisa();	
		obj.setCoordenadorGeral(new Servidor() );
		obj.getProjeto().setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.CADASTRADO));
		obj.setInterveniente(new Unidade());
		obj.setExecutora(new Unidade());
		obj.setConcedente(new EntidadeFinanciadora());
		obj.setConvenente(new EntidadeFinanciadora());
		obj.setSubProjetos(new HashSet<SubProjetoInfraEstruturaPesquisa>(0));
		subProjeto = new SubProjetoInfraEstruturaPesquisa();
		subProjeto.setCoordenador( new Servidor() );
		subProjeto.setGrandeArea(new AreaConhecimentoCnpq());
	}
	
	/** 
     * Inicia cadastro de um projeto de infra-estrutura.
     * Inicializa o controle de fluxo do cadastro.
     * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /pesquisa/menu/projetos.jsp
	 */
	public String iniciar() throws ArqException{
		clear();
		setConfirmButton("Cadastrar");
		setReadOnly(false);
		prepareMovimento(SigaaListaComando.CADASTRAR_PROJETO_INFRA_ESTRUTURA);
		return telaDadosGerais();
	}
	
	/** 
	 *
     * Método usado para avançar o primeiro passo (dados gerais). Vai para a
     * tela de sub-projetos.
     * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /pesquisa/projeto_infraestrutura/dados_gerais.jsp
	 */
	public String submeterDadosGerais() throws DAOException {
		erros = obj.validate();
		if(hasErrors()){
			addMensagens(erros);
			return telaDadosGerais();
		}
		GenericDAO dao = getGenericDAO();
		obj.setConcedente(dao.findByPrimaryKey(obj.getConcedente().getId(), EntidadeFinanciadora.class, "id", "nome"));
		obj.setConvenente(dao.findByPrimaryKey(obj.getConvenente().getId(), EntidadeFinanciadora.class, "id", "nome"));
		obj.setInterveniente( dao.findByPrimaryKey(obj.getInterveniente().getId(), Unidade.class, "id", "nome"));
		obj.setExecutora(dao.findByPrimaryKey(obj.getExecutora().getId(), Unidade.class, "id", "nome"));
		obj.getProjeto().setSituacaoProjeto( dao.findByPrimaryKey(obj.getProjeto().getSituacaoProjeto() .getId(), TipoSituacaoProjeto.class, "id", "descricao") );
		obj.setCoordenadorGeral( dao.findByPrimaryKey(obj.getCoordenadorGeral().getId(), Servidor.class));
		obj.getCoordenadorGeral().getFormacao().getDenominacao();
		return telaSubProjetos();
	}
	
	/** 
	 * Método utilizado para preparar o tipo da operação. 
	 * Direciona para a tela de resumo do projeto.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /pesquisa/projeto_infraestrutura/subprojetos.jsp
	 * 
	 * @throws ArqException 
	 */
	public String submeterSubProjetos() throws ArqException {
		prepareMovimento(SigaaListaComando.CADASTRAR_PROJETO_INFRA_ESTRUTURA);
		obj.getProjeto().setUnidade(null);
		return telaResumo();
	}
	
	/** 
	 * Adiciona um Sub-Projeto ao Projeto de Infra-Estrutura.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /pesquisa/projeto_infraestrutura/subprojetos.jsp
	 */
	public String adicionarSubProjeto() throws DAOException {
		if(obj.getSubProjetos() != null && subProjeto != null){
			erros = subProjeto.validate();
			if(hasErrors()){
				addMensagens(erros);
				return telaSubProjetos();
			}
			subProjeto.setGrandeArea(getGenericDAO().findByPrimaryKey(subProjeto.getGrandeArea().getId(), 
					AreaConhecimentoCnpq.class, "id", "nome"));
			final int quantidade = obj.getSubProjetos().size();
			obj.addSubProjeto(subProjeto);
			if (quantidade == obj.getSubProjetos().size())
				addMensagemErro("Esse docente já é coordenador em um Sub-Projeto desse projeto");
			else {
				addMensagemInformation("Sub-Projeto adicionado com sucesso!");
				subProjeto = new SubProjetoInfraEstruturaPesquisa();
				subProjeto.setGrandeArea(new AreaConhecimentoCnpq());
			}
			subProjeto.setCoordenador(new Servidor());
		}
		return telaSubProjetos();
	}
	
	/** 
	 * Remove um Sub-Projeto do Projeto de Infra-Estrutura.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /pesquisa/projeto_infraestrutura/subprojetos.jsp
	 */
	public String removeSubProjeto(){
		int idSubProjeto = getParameterInt("idSubProjeto", 0);
		int idCoordenador = getParameterInt("idCoordenador", 0);
		try {
			SubProjetoInfraEstruturaPesquisa subProjeto = new SubProjetoInfraEstruturaPesquisa();
			subProjeto.setId(idSubProjeto);
			subProjeto.setCoordenador(new Servidor(idCoordenador));
			obj.getSubProjetos().remove(subProjeto);
		} catch (Exception e) {
			addMensagemErro("Erro ao Remover Sub-Projeto.");
			e.printStackTrace();
			notifyError(e);
		}
		addMensagemInformation("Sub-Projeto removido com sucesso!");
		return null;
	}
	
	/** 
	 * Realiza o carregamento do subprojeto a partir do seu id.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /pesquisa/projeto_infraestrutura/subprojetos.jsp
	 */
	public void carregarSubProjeto(ActionEvent evt){
		int idSubProjeto = getParameterInt("id");
		for(SubProjetoInfraEstruturaPesquisa p: obj.getSubProjetos()){
			if(p.getId() == idSubProjeto)
				subProjeto = p; 
		}
		alterar = true;
	}
	
	/**
	 * Altera um Sub-Projeto do Projeto de Infra-Estrutura.
	 * <br/><br/> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /pesquisa/projeto_infraestrutura/subprojetos.jsp
	 */
	public String alterarSubProjeto() throws DAOException{
		if(subProjeto.getId() == 0){
			addMensagemErro("Não é possível alterar um projeto que não foi adicionado à lista");
		} else {
			SubProjetoInfraEstruturaPesquisa novo = subProjeto;
			obj.getSubProjetos().remove(subProjeto);
			novo.setGrandeArea(getGenericDAO().findByPrimaryKey(novo.getGrandeArea().getId(), 
					AreaConhecimentoCnpq.class, "id", "nome"));
			obj.getSubProjetos().add(novo);
			subProjeto = new SubProjetoInfraEstruturaPesquisa();
			subProjeto.setCoordenador( new Servidor() );
			subProjeto.setGrandeArea(new AreaConhecimentoCnpq());
			addMensagemWarning("Sub-Projeto alterado com sucesso!");
		}
		alterar = false;
		return telaSubProjetos();
	}
	
	/** 
	 * Exibe a lista de projetos cadastrados.
     * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /pesquisa/menu/projetos.jsp
	 * 
	 */
	public String listar() throws DAOException {
		setResultadosBusca( 
				getGenericDAO().findAll(ProjetoInfraEstruturaPesquisa.class, "projeto.titulo", "asc") ); 
		return forward(getListPage());
	}
	
	/** 
	 * Realiza o cadastro do Projeto de Infra-Estrutura e dos Sub-Projetos relacionados.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /pesquisa/projeto_infraestrutura/resumo.jsp
	 */
	@Override	
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {

		checkChangeRole();

		if (getConfirmButton().equalsIgnoreCase("remover")) {
			return remover();
		} else {

			// transforma para uma visão de objeto validável e chama a validação
			Validatable objValidavel = obj;
			ProjetoInfraEstruturaPesquisa obj = this.obj;
			beforeCadastrarAndValidate();

			erros = new ListaMensagens();
			ListaMensagens lista = objValidavel.validate();

			if (lista != null && !lista.isEmpty()) {
				erros.addAll(lista.getMensagens());
			}

			String descDominio = null;
			try {
				descDominio = ReflectionUtils.evalProperty(obj, "descricaoDominio");
			} catch (Exception e) {
			}

			if (!hasErrors()) {
				beforeCadastrarAfterValidate();

				// Verificar se existe um arquivo a salvar
				if (file != null && file.getName() != null) {
					try {
						if (obj.getProjeto().getIdArquivo() == null	|| obj.getProjeto().getIdArquivo() == 0) {
							// Buscar um id para o arquivo
							obj.getProjeto().setIdArquivo(EnvioArquivoHelper.getNextIdArquivo());
						}

						// Gravar arquivo do projeto
						EnvioArquivoHelper.inserirArquivo(obj.getProjeto().getIdArquivo(),
								file.getBytes(), file.getContentType(), file.getName());
					} catch (Exception e) {
						throw new ArqException(e);
					}
				}
				
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setObjMovimentado(obj);

				if (getConfirmButton().equalsIgnoreCase("cadastrar")) {
					mov.setCodMovimento(SigaaListaComando.CADASTRAR_PROJETO_INFRA_ESTRUTURA);
					try {
						execute(mov, getCurrentRequest());
						if (descDominio != null && !descDominio.equals("")) {
							addMessage(descDominio + "  cadastrado com sucesso!",
									TipoMensagemUFRN.INFORMATION);
						} else {
							addMessage("Operação Realizada com sucesso", TipoMensagemUFRN.INFORMATION);
						}
						//prepareMovimento(SigaaListaComando.CADASTRAR_PROJETO_INFRA_ESTRUTURA);
					
					} catch (NegocioException e) {
						addMensagemErro(e.getMessage());
						return forward(getFormPage());
					}
					afterCadastrar();

					String forward = forwardCadastrar();
					if (forward == null) {
						return redirectJSF(getCurrentURL());
					} else {
						return redirectJSF(forward);
					}

				} else if (getConfirmButton().equalsIgnoreCase("alterar")){
					mov.setCodMovimento(SigaaListaComando.CADASTRAR_PROJETO_INFRA_ESTRUTURA);
					try {
						execute(mov, getCurrentRequest());
						if (descDominio != null && !descDominio.equals("")) {
							addMessage(descDominio + " alterado com sucesso!",	TipoMensagemUFRN.INFORMATION);
						} else {
							addMessage("Operação Realizada com sucesso", TipoMensagemUFRN.INFORMATION);
						}
					} catch (NegocioException e) {
						addMensagemErro(e.getMessage());
						return forward(getFormPage());
					}
					afterCadastrar();

					String forward = forwardCadastrar();
					if (forward == null) {
						return redirectJSF(getListPage());
					} else {
						return redirectJSF(forward);
					}
				}
			} else {
				return null;
			}
		}
		return forward(forwardCadastrar());
	}
	
	/** 
	 * Método responsável por realizar as configurações necessárias para realizar a alteração de um projeto.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /pesquisa/projeto_infraestrutura/lista.jsp
	 */
	@Override
	public String atualizar() throws ArqException {

		try {
			clear();
			UnidadeDao dao = getDAO(UnidadeDao.class);
			
			int id = getParameterInt("id");
			obj.setId(id);
			obj = dao.findByPrimaryKey(obj.getId(), obj.getClass());

			prepareMovimento(ArqListaComando.ALTERAR);
			
			instanciarAtributos(getObj());
			
		} catch (Exception e) {
			addMensagemErroPadrao();
			e.printStackTrace();
		}
		setConfirmButton("Alterar");
		return telaDadosGerais();
	}
	
	/**
	 * Método responsável por inicializar as propriedades do projeto.
	 * 
	 * @param projeto
	 */
	private void instanciarAtributos(ProjetoInfraEstruturaPesquisa projeto){
		
		if (projeto.getExecutora() == null)
			projeto.setExecutora(new Unidade());
		if(projeto.getInterveniente() == null)
			projeto.setInterveniente(new Unidade());
		if (projeto.getConcedente() == null)
			projeto.setConcedente(new EntidadeFinanciadora());
		if (projeto.getConvenente() == null)
			projeto.setConvenente(new EntidadeFinanciadora());
		if(projeto.getCoordenadorGeral() == null)
			projeto.setCoordenadorGeral(new Servidor());
		if(projeto.getSubProjetos() != null)
			projeto.getSubProjetos().size();
		if(projeto.getProjeto().getHistoricoSituacao() != null)
			projeto.getProjeto().getHistoricoSituacao().size();
		// Inicializar atributos lazy
		projeto.getSubProjetos().iterator();
		projeto.getProjeto().getHistoricoSituacao().iterator();
	}
	
	/** 
	 * Método responsável por realizar as configurações necessárias para realizar a exclusão de um projeto.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /pesquisa/projeto_infraestrutura/lista.jsp
	 */
	@Override
	public String preRemover() {

		try {
			prepareMovimento(ArqListaComando.DESATIVAR);
			setId();
			PersistDB obj = this.obj;

			this.obj = (ProjetoInfraEstruturaPesquisa) getGenericDAO().findByPrimaryKey(obj.getId(), obj.getClass());
			instanciarAtributos(getObj());
		} catch (Exception e) {
			addMensagemErroPadrao();
			e.printStackTrace();
		}
		setReadOnly(true);
		setConfirmButton("Remover");
		return telaResumo();
	}
	
	/**
	 * Exclui o projeto selecionado.
	 * <br/><br/>
	 * Método não invocado por JSP´s
	 */
	@Override
	public String remover() throws ArqException {

		MovimentoCadastro mov = new MovimentoCadastro();
		PersistDB obj = this.obj;
		mov.setObjMovimentado(obj);

		if (obj.getId() == 0) {
			addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
			return null;
		} else {
			mov.setCodMovimento(ArqListaComando.DESATIVAR);
			try {
				execute(mov, (HttpServletRequest) FacesContext.getCurrentInstance()
								.getExternalContext().getRequest());
				addMessage("Operação realizada com sucesso!", TipoMensagemUFRN.INFORMATION);
			} catch (NegocioException e) {
				addMensagemErro(e.getMessage());
				return forward(getFormPage());
			} catch (ArqException e) {
				addMensagemErro(e.getMessage());
				return forward(forwardCadastrar());
			} catch (Exception e) {
				addMensagemErroPadrao();
				e.printStackTrace();
				return forward(forwardCadastrar());
			}
			setResultadosBusca(getGenericDAO().findAll(ProjetoInfraEstruturaPesquisa.class, 
					"projeto.titulo", "asc"));
			redirectJSF(getListPage());
			return null;
		}
	}
	
	/** 
	 * Visualiza dados detalhados do projeto.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /pesquisa/projeto_infraestrutura/lista.jsp
	 */
	public String view() {
		
		int id = getParameterInt("id");
		obj.setId(id);
		try {
			obj = getDAO(UnidadeDao.class).findByPrimaryKey(obj.getId(), obj.getClass());
		} catch (DAOException e) {
			addMensagemErroPadrao();
			e.printStackTrace();
		}
		return forward("/pesquisa/projeto_infraestrutura/visualizar_projeto.jsf");
	}
	
	/**
	 * Método não invocado por JSP´s
	 */
	@Override
	public String getListPage() {
		return "/pesquisa/projeto_infraestrutura/lista.jsf";
	}
	
	/**
	 * Direciona o usuário para a tela inicial do cadastro de um projeto de Infra Estrutura Pesquisa
	 * <br/><br/>
	 * Método não invocado por JSP´s
	 */
	public String telaDadosGerais() {
		return forward("/pesquisa/projeto_infraestrutura/dados_gerais.jsf");
	}
	
	/**
	 * Direciona o usuário para a tela de cadastro de um novo sub-projeto de infra estrutura.  
	 * <br/><br/>
	 * Método não invocado por JSP´s
	 */
	public String telaSubProjetos() {
		return forward("/pesquisa/projeto_infraestrutura/subprojetos.jsf");
	}
	
	/**
	 * Exibe ao usuário uma tela com o resumo do cadastro do projeto de infra estrutura e Pesquisa
	 * <br/><br/>
	 * Método não invocado por JSP´s
	 */
	public String telaResumo() {
		return forward("/pesquisa/projeto_infraestrutura/resumo.jsf");
	}
	
	/**
	 * Para quando ao cadastrar um novo projeto de InfraEstrutura e Pesquisa o usuário é direcionado 
	 * para a tela principal do módulo de pesquisa.
	 * <br/><br/>
	 * Método não invocado por JSP´s
	 */
	@Override
	public String forwardCadastrar() {
		return "/pesquisa/menu.jsf";
	}
	
	/**
	 * Serve para armazenar a data de cadastro do projeto de infra-estrutura
	 * e também tem como função adicionar o registro de entrada do usuário logado. 
	 * <br/><br/>
	 * Método não invocado por JSP´s
	 */
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException,
			SegurancaException, DAOException {
		obj.getProjeto().setDataCadastro(new Date());
		obj.getProjeto().setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
	}
	
	/**
	 * Método não invocado por JSP´s
	 */
	@Override
	public String getDirBase() {
		return "/pesquisa/projeto_infraestrutura";
	}
	
	/**
	 * Método não invocado por JSP´s
	 */
	@Override
	public String getFormPage() {
		return getDirBase() + "/form.jsf";
	}

	public SubProjetoInfraEstruturaPesquisa getSubProjeto() {
		return subProjeto;
	}

	public void setSubProjeto(SubProjetoInfraEstruturaPesquisa subProjeto) {
		this.subProjeto = subProjeto;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public boolean isAlterar() {
		return alterar;
	}

	public void setAlterar(boolean alterar) {
		this.alterar = alterar;
	}
	
}