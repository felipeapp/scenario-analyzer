package br.ufrn.sigaa.ouvidoria.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ouvidoria.dao.CategoriaSolicitanteDao;
import br.ufrn.sigaa.ouvidoria.dominio.AssuntoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaAssuntoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaSolicitante;
import br.ufrn.sigaa.ouvidoria.dominio.InteressadoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.InteressadoNaoAutenticado;
import br.ufrn.sigaa.ouvidoria.dominio.Manifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.OrigemManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.StatusManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.TipoManifestacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.pessoa.dominio.TipoLogradouro;

/**
 * MBean responsável por realizar as operações básicas de {@link Manifestacao} do módulo de Ouvidoria.
 * 
 * @author bernardo
 *
 */
@Component(value="manifestacaoOuvidoria") @Scope(value="request")
public class ManifestacaoOuvidoriaMBean extends	ManifestacaoAbstractController {
    
	/** Link para a página de seleção de tipo do solicitante. */
    public static final String JSP_TIPO_SOLICITANTE = "/tipo_solicitante.jsp";
    
    /** Link para a página de dados de um interessado não autenticado. */
    public static final String JSP_DADOS_INTERESSADO_NAO_AUTENTICADO = "/dados_interessado.jsp";
    
    /** Link para a página de busca de discentes. */
    public static final String JSP_BUSCA_DISCENTE = "/busca_discente.jsp";
    
    /** Link para a página de busca de docentes. */
    public static final String JSP_BUSCA_DOCENTE = "/busca_docente.jsp";
    
    /** Link para a página de busca de servidores técnico administrativos. */
    public static final String JSP_BUSCA_TECNICO_ADMINISTRATIVO = "/busca_tecnico_administrativo.jsp";
    
    /** Link para a página de dados da manifestação. */
    public static final String JSP_DADOS_MANIFESTACAO = "/dados_manifestacao.jsp";
    
    /** Link para a página de resumo de uma manifestação. */
    public static final String JSP_RESUMO = "/resumo.jsp";
    
    /** Constante que define um manifestante da comunidade interna. */
    public static final int COMUNIDADE_INTERNA = 1;
    
    /** Constante que define um manifestante da comunidade externa. */
    public static final int COMUNIDADE_EXTERNA = 2;
    
    /** Armazena os dados do interessado não autenticado. */
    private InteressadoNaoAutenticado interessadoNaoAutenticado;
    
    /** Armazena o discente selecionado. */
    private Discente discente;
    
    /** Armazena o docente selecionado. */
    private Servidor servidor;
    
    /** Armazena o código de área informado para o telefone do solicitante. */
    private String codArea;
    
    /** Origem do manifestante escolhida. */
    private int origemManifestante;
    
    /** Combo de municípios carregado de acordo com a UF selecionada. */
    private Collection<SelectItem> municipiosCombo;
    
    /** Armazena a lista de discentes buscados. */
    private Collection<? extends DiscenteAdapter> discentes;
    
    /** Armazena a lista de servidores buscados. */
    private Collection<Servidor> servidores;
    
    /** Indica s o filtro de matrícula/SIAPE foi selecionado. */
    private boolean buscaMatricula;
    
    /** Indica s o filtro de nome foi selecionado. */
    private boolean buscaNome;
    
    /** Indica s o filtro de curso foi selecionado. */
    private boolean buscaCurso;
    
    /** Matrícula/SIAPE informado para busca. */
    private String matricula;
    
    /** Nome do discente/servidor informado para busca. */
    private String nome;
    
    /** Nome do curso informado para busca. */
    private String curso;

    public ManifestacaoOuvidoriaMBean() {
    	init();
    }

    /**
     * Inicia os dados necessários do MBean
     */
    protected void init() {
		municipiosCombo = new ArrayList<SelectItem>();
		obj = new Manifestacao();
		obj.setInteressadoManifestacao(new InteressadoManifestacao());
		obj.getInteressadoManifestacao().setCategoriaSolicitante(new CategoriaSolicitante());
    }
    
    /**
     * Inicia o processo de cadastro de uma manifestação.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/menu.jsp</li>
     * </ul>
     * 
     * @return
     */
    public String iniciarCadastro() {
		init();
		obj.setTipoManifestacao(TipoManifestacao.getTipoManifestacao(TipoManifestacao.CRITICA));
		
		return forward(getDirBase() + "/tipo_solicitante.jsp");
    }
    
    /**
     * Direciona o fluxo para a página de escolha do tipo de solicitante.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/busca_discente.jsp</li>
     * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/busca_docente.jsp</li>
     * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/busca_tecnico_administrativo.jsp</li>
     * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/dados_interessado.jsp</li>
     * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/resumo.jsp</li>
     * </ul>
     * 
     * @return
     */
    public String paginaTipoSolicitante() {
    	return forward(getDirBase() + JSP_TIPO_SOLICITANTE);
    }
    
    /**
     * Submete os dados do tipo de solicitante escolhidos.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/tipo_solicitante.jsp</li>
     * </ul>
     * 
     * @return
     * @throws ArqException
     */
    public String submeterTipoSolicitante() throws ArqException {
		if(origemManifestante == 0)
		    addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Origem do Manifestante");
		if(origemManifestante == COMUNIDADE_INTERNA && isEmpty(obj.getInteressadoManifestacao().getCategoriaSolicitante()))
		    addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Categoria");
		
		if(hasErrors())
		    return null;
		
		if(interessadoNaoAutenticado == null || interessadoNaoAutenticado.getNome() == null)
			prepararCadastro();
		
		//Se o manifestante for da comunidade externa, direciona para o formulário correspondente
		if(origemManifestante == COMUNIDADE_EXTERNA) {
		    return paginaDadosInteressado();
		}
		
		//Se o manifestante for da comunidade interna, direciona para a busca correspondente ao tipo escolhido
		if(verificarCategoria(CategoriaSolicitante.DISCENTE)) {
			if(buscaMatricula || buscaNome || buscaCurso) {
				buscarDiscente();
			}
		    return paginaBuscaDiscente();
		}
		if(verificarCategoria(CategoriaSolicitante.DOCENTE)) {
			if(buscaMatricula || buscaNome) {
				buscarDocente();
			}
		    return paginaBuscaDocente();
		}
		if(verificarCategoria(CategoriaSolicitante.TECNICO_ADMINISTRATIVO)) {
			if(buscaMatricula || buscaNome) {
				buscarTecnicoAdministrativo();
			}
		    return paginaBuscaTecnicoAdministrativo();
		}
		
		return null;
    }
    
    /**
     * Direciona o fluxo para a tela de busca de discentes da instituição.<br />
     * Método não invocado por JSPs.
     * 
     * @return
     */
    public String paginaBuscaDiscente() {
    	return forward(getDirBase() + JSP_BUSCA_DISCENTE);
    }
    
    /**
     * Realiza a busca de discentes de acordo com os critérios informados.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/busca_discente.jsp</li>
     * </ul>
     * 
     * @return
     */
    public String buscarDiscente() {
		validarBusca();
		
		if(hasErrors())
			return null;
		
		try {
			popularBuscaDiscente();
		}  catch (LimiteResultadosException e) {
			addMensagemErro(e.getMessage());
		    return null;
		} catch (DAOException e) {
		    return tratamentoErroPadrao(e);
		}
		
		return null;
    }

    /**
     * Popula os dados da busca de discente.
     * 
     * @throws LimiteResultadosException
     * @throws DAOException
     */
	private void popularBuscaDiscente() throws LimiteResultadosException, DAOException {
		DiscenteDao dao = getDAO(DiscenteDao.class);
		Long mat = null;
		
		if(isNotEmpty(matricula)) {
		    mat = Long.valueOf(matricula);
		}
		
		try {
		    discentes = dao.findOtimizado(null, mat, nome, curso, null, null, StatusDiscente.getValidos(), null, 0, '0', true );
		    
		    if(isEmpty(discentes))
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		} finally {
		    dao.close();
		}
	}
    
	/**
	 * Direciona o fluxo para a página de busca de docentes.<br />
	 * Método não invocado por JSPs.
	 * 
	 * @return
	 */
    public String paginaBuscaDocente() {
    	return forward(getDirBase() + JSP_BUSCA_DOCENTE);
    }
    
    /**
     * Efetua a busca de docentes de acordo com os critérios informados.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/busca_docente.jsp</li>
     * </ul>
     * 
     * @return
     * @throws DAOException
     */
    public String buscarDocente() throws DAOException {
		validarBusca();
		
		if(hasErrors())
			return null;
		
		popularBuscaDocente();
		
		return null;
    }

    /**
     * Popula os dados da busca de docente.
     * 
     * @throws DAOException
     */
	private void popularBuscaDocente() throws DAOException {
		Integer mat = null;
		
		if(isNotEmpty(matricula)) {
		    mat = Integer.valueOf(matricula);
		}
		
		ServidorDao dao = getDAO(ServidorDao.class);
		
		try {
		    servidores = dao.findServidorUnidadeByNomeSiape(nome, mat, true, Categoria.DOCENTE);
		    
		    if(isEmpty(servidores))
		    	addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		} finally {
		    dao.close();
		}
	}
    
	/**
	 * Direciona o fluxo para a página de busca de servidores técnicos administrativos.<br />
	 * Método não invocado por JSPs.
	 * 
	 * @return
	 */
    public String paginaBuscaTecnicoAdministrativo() {
    	return forward(getDirBase() + JSP_BUSCA_TECNICO_ADMINISTRATIVO);
    }
    
    /**
     * Realiza a busca de técnicos administrativos.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/busca_tecnico_administrativo.jsp</li>
     * </ul>
     * 
     * @return
     * @throws DAOException
     */
    public String buscarTecnicoAdministrativo() throws DAOException {
		validarBusca();
		
		if(hasErrors())
		    return null;
		
		popularBuscaTecnicoAdministrativo();
		
		return null;
    }

    /**
     * Popula os dados da busca de técnicos administrativos.
     * 
     * @throws DAOException
     */
	private void popularBuscaTecnicoAdministrativo() throws DAOException {
		ServidorDao dao = getDAO(ServidorDao.class);
		int mat = 0;
		
		if(isNotEmpty(matricula)) {
		    mat = Integer.parseInt(matricula);
		}
		
		try {
		    servidores = dao.findServidorUnidadeByNomeSiape(nome, mat, true, Categoria.TECNICO_ADMINISTRATIVO);
		    
		    if(isEmpty(servidores))
		    	addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		} finally {
		    dao.close();
		}
	}

	/**
	 * Valida os dados informados para busca.
	 */
    private void validarBusca() {
		if(!buscaMatricula && !buscaNome && !buscaCurso) {
		    addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
		}
		
		if(buscaMatricula && isEmpty(matricula)) {
		    if(verificarCategoria(CategoriaSolicitante.DISCENTE))
		    	addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Matrícula");
		    else
		    	addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,"SIAPE");
		}
		if(buscaNome && isEmpty(nome)) {
		    addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome");
		}
		if(buscaCurso && isEmpty(curso)) {
		    addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Curso");
		}
		
		if(hasErrors())
			return;
		
		if(!buscaMatricula) {
		    matricula = null;
		}
		if(!buscaNome) {
		    nome = null;
		}
		if(!buscaCurso) {
		    curso = null;
		}
    }
    
    /**
     * Recupera o id da pessoa selecionada para carregar seus dados.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/busca_discente.jsp</li>
     * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/busca_docente.jsp</li>
     * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/busca_tecnico_administrativo.jsp</li>
     * </ul>
     * 
     * @param evt
     * @throws HibernateException
     * @throws DAOException
     */
    public void selecionarPessoa(ActionEvent evt) throws HibernateException, DAOException {
		Integer id = (Integer) evt.getComponent().getAttributes().get("idSelecionado");
		
		selecionarPessoa(id);
    }

    /**
     * Realiza a seleção da pessoa desejada e direciona o usuário para a página de dados da manifestação.
     * 
     * @param id
     * @throws HibernateException
     * @throws DAOException
     */
    private void selecionarPessoa(Integer id) throws HibernateException, DAOException {
		PessoaDao dao = getDAO(PessoaDao.class);
		
		try {
			obj.getInteressadoManifestacao().setCategoriaSolicitante(dao.findByPrimaryKey(obj.getInteressadoManifestacao().getCategoriaSolicitante().getId(), CategoriaSolicitante.class));
			
		    if(verificarCategoria(CategoriaSolicitante.DISCENTE)) {
				discente = getDiscenteBuscado(id);
				discente.setPessoa(dao.findLeveByDiscente(id));
				
				obj.getInteressadoManifestacao().getDadosInteressadoManifestacao().setPessoa(discente.getPessoa());
		    }
		    else {
				servidor = getServidorBuscado(id);
				servidor.setPessoa(dao.findLeveByServidor(id));
				
				obj.getInteressadoManifestacao().getDadosInteressadoManifestacao().setPessoa(servidor.getPessoa());
		    }
		} finally {
		    dao.close();
		}
		
		paginaDadosManifestacao();
    }

    /**
     * Retorna o servidor previamente buscado de acordo com seu id.
     * 
     * @param id
     * @return
     */
    private Servidor getServidorBuscado(Integer id) {
		for (Servidor s : servidores) {
			if(s.getId() == id) {
				return s;
			}
		}
		
		return new Servidor(id);
	}

    /**
     * Retorna um discente buscado de acordo com seu id.
     * 
     * @param id
     * @return
     */
	private Discente getDiscenteBuscado(Integer id) {
		for (DiscenteAdapter d : discentes) {
			if(d.getId() == id) {
				return d.getDiscente();
			}
		}
		
		return new Discente(id);
	}

	/**
	 * Direciona o fluxo para a tela de dados do interessado não autenticado.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/resumo.jsp</li>
     * </ul>
	 * 
	 * @return
	 */
	public String paginaDadosInteressado() {
		if(interessadoNaoAutenticado.getEndereco() == null)
			interessadoNaoAutenticado.setEndereco(new Endereco());
		if(interessadoNaoAutenticado.getEndereco().getMunicipio() == null)
			interessadoNaoAutenticado.getEndereco().setMunicipio(new Municipio());
		if(interessadoNaoAutenticado.getEndereco().getUnidadeFederativa() == null)
			interessadoNaoAutenticado.getEndereco().setUnidadeFederativa(new UnidadeFederativa());
		
    	return forward(getDirBase() + JSP_DADOS_INTERESSADO_NAO_AUTENTICADO);
    }
    
	/**
	 * Volta para a página de dados do usuário ou busca de usuários de acordo com a categoria do solicitante definida.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/dados_manifestacao.jsp</li>
     * </ul>
	 * 
	 * @return
	 */
    public String voltarDadosUsuario() {
		if(isComunidadeInterna()) {
		    if(verificarCategoria(CategoriaSolicitante.DISCENTE))
		    	return paginaBuscaDiscente();
		    if(verificarCategoria(CategoriaSolicitante.DOCENTE))
		    	return paginaBuscaDocente();
		    if(verificarCategoria(CategoriaSolicitante.TECNICO_ADMINISTRATIVO))
		    	return paginaBuscaTecnicoAdministrativo();
		}
		
		return paginaDadosInteressado();
    }

    /**
     * Verifica se a categoria passada é a mesma categoria do manifestante.
     * 
     * @param categoria
     * @return
     */
    private boolean verificarCategoria(int categoria) {
    	return obj.getInteressadoManifestacao().getCategoriaSolicitante().getId() == categoria;
    }
    
    /**
     * Verifica se o manifestante é um discente
     * 
     * @return
     */
    public boolean isCategoriaDiscente() {
    	return isComunidadeInterna() && verificarCategoria(CategoriaSolicitante.DISCENTE);
    }
    
    /**
     * Submete os dados do interessado não autenticado.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/dados_interessado.jsp</li>
     * </ul>
     * 
     * @return
     * @throws DAOException 
     */
    public String submeterDadosSolicitante() throws DAOException {
		ListaMensagens erros = new ListaMensagens();
		
		if(isNotEmpty(codArea))
		    interessadoNaoAutenticado.setCodigoAreaTelefone(Short.valueOf(codArea));
		if(isNotEmpty(interessadoNaoAutenticado.getEndereco().getTipoLogradouro())) {
			GenericDAO dao = getGenericDAO();
			
			interessadoNaoAutenticado.getEndereco().setTipoLogradouro(dao.findByPrimaryKey(interessadoNaoAutenticado.getEndereco().getTipoLogradouro().getId(), TipoLogradouro.class));
		}
		
		obj.getInteressadoManifestacao().getDadosInteressadoManifestacao().setInteressadoNaoAutenticado(interessadoNaoAutenticado);
		
		obj.validateDadosInteressadoNaoAutenticado(erros);
		
		addMensagens(erros);
		
		if(hasErrors())
		    return null;
		
		return paginaDadosManifestacao();
    }

    /**
     * Direciona o fluxo para a tela de dados da manifestação.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/resumo.jsp</li>
     * </ul>
     * 
     * @return
     */
    public String paginaDadosManifestacao() {
    	return forward(getDirBase() + JSP_DADOS_MANIFESTACAO);
    }
    
    /**
     * Submete os dados informados para a manifestação.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/dados_manifestacao.jsp</li>
     * </ul>
     * 
     * @return
     * @throws DAOException
     */
    public String submeterDadosManifestacao() throws DAOException {
    	try {
			validaFormatoArquivo(arquivo);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
    	
    	ListaMensagens erros = new ListaMensagens();
		
		obj.validateDadosManifestacao(erros);
		
		addMensagens(erros);
		
		if(hasErrors())
		    return null;
		
		carregarDadosManifestacao();
		
		return paginaResumo();
    }
    
    /**
     * Carrega os dados necessários para a correta visualização da manifestação.
     * 
     * @throws DAOException
     */
    private void carregarDadosManifestacao() throws DAOException {
		GenericDAO dao = getGenericDAO();
		
		try {
		    obj.setAssuntoManifestacao(dao.findByPrimaryKey(obj.getAssuntoManifestacao().getId(), AssuntoManifestacao.class));
		    obj.getAssuntoManifestacao().setCategoriaAssuntoManifestacao(dao.findByPrimaryKey(obj.getAssuntoManifestacao().getCategoriaAssuntoManifestacao().getId(), CategoriaAssuntoManifestacao.class));
		    obj.setTipoManifestacao(dao.findByPrimaryKey(obj.getTipoManifestacao().getId(), TipoManifestacao.class));
		} finally {
		    dao.close();
		}
    }

    /**
     * Direciona o fluxo para a página de resumo da manifestação.<br />
     * Método não invocado por JSPs.
     * 
     * @return
     */
    public String paginaResumo() {
    	return forward(getDirBase() + JSP_RESUMO);
    }

    /**
     * Prepara o cadastro de acordo com os dados escolhidos.
     * 
     * @throws ArqException 
     */
    private void prepararCadastro() throws ArqException {
		if(origemManifestante == COMUNIDADE_EXTERNA) {
		    obj = new Manifestacao(CategoriaSolicitante.COMUNIDADE_EXTERNA, OrigemManifestacao.PRESENCIAL, StatusManifestacao.SOLICITADA, null);
		    interessadoNaoAutenticado = new InteressadoNaoAutenticado();
		    interessadoNaoAutenticado.setEndereco(new Endereco());
		    
		    prepareMovimento(SigaaListaComando.CADASTRAR_MANIFESTACAO_COMUNIDADE_EXTERNA);
		}
		else {
		    if(verificarCategoria(CategoriaSolicitante.DISCENTE)) {
				obj = new Manifestacao(CategoriaSolicitante.DISCENTE, OrigemManifestacao.PRESENCIAL, StatusManifestacao.SOLICITADA, null);
				
				prepareMovimento(SigaaListaComando.CADASTRAR_MANIFESTACAO_DISCENTE);
		    }
		    if(verificarCategoria(CategoriaSolicitante.DOCENTE)) {
				obj = new Manifestacao(CategoriaSolicitante.DOCENTE, OrigemManifestacao.PRESENCIAL, StatusManifestacao.SOLICITADA, null);
				
				prepareMovimento(SigaaListaComando.CADASTRAR_MANIFESTACAO_DOCENTE);
		    }
		    if(verificarCategoria(CategoriaSolicitante.TECNICO_ADMINISTRATIVO)) {
				obj = new Manifestacao(CategoriaSolicitante.TECNICO_ADMINISTRATIVO, OrigemManifestacao.PRESENCIAL, StatusManifestacao.SOLICITADA, null);
				
				prepareMovimento(SigaaListaComando.CADASTRAR_MANIFESTACAO_TECNICO_ADMINISTRATIVO);
		    }
		    
		    obj.getInteressadoManifestacao().getDadosInteressadoManifestacao().setInteressadoNaoAutenticado(null);
		    interessadoNaoAutenticado = null;
		}
		obj.setTipoManifestacao(TipoManifestacao.getTipoManifestacao(TipoManifestacao.CRITICA));
    }
    
    @Override
    public String cadastrar() throws SegurancaException, ArqException, NegocioException {
    	if(origemManifestante == COMUNIDADE_EXTERNA) {
    		String nomeAscii = StringUtils.toAsciiAndUpperCase(obj.getInteressadoManifestacao().getDadosInteressadoManifestacao().getInteressadoNaoAutenticado().getNome());
    		obj.getInteressadoManifestacao().getDadosInteressadoManifestacao().getInteressadoNaoAutenticado().setNomeAscii(nomeAscii);
    	}
    	
		MovimentoCadastro mov = prepararMovimento();
		
		try {
		    execute(mov);
		    
		    obj = getGenericDAO().findAndFetch(obj.getId(), Manifestacao.class, "tipoManifestacao", "assuntoManifestacao");
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());
		    return null;
		}
		
		return paginaComprovante();
    }
      
    /**
     * Prepara o movimento de cadastro de acordo com a categoria do solicitante.
     * 
     * @return
     */
    private MovimentoCadastro prepararMovimento() {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setObjAuxiliar(arquivo);
		if(isComunidadeInterna()) {
		    if(verificarCategoria(CategoriaSolicitante.DISCENTE))
		    	mov.setCodMovimento(SigaaListaComando.CADASTRAR_MANIFESTACAO_DISCENTE);
		    if(verificarCategoria(CategoriaSolicitante.DOCENTE))
		    	mov.setCodMovimento(SigaaListaComando.CADASTRAR_MANIFESTACAO_DOCENTE);
		    if(verificarCategoria(CategoriaSolicitante.TECNICO_ADMINISTRATIVO))
		    	mov.setCodMovimento(SigaaListaComando.CADASTRAR_MANIFESTACAO_TECNICO_ADMINISTRATIVO);
		}
		else {
		    mov.setCodMovimento(SigaaListaComando.CADASTRAR_MANIFESTACAO_COMUNIDADE_EXTERNA);
		}
		
		return mov;
    }
    
    /**
     * Listener para carregar os municípios de acordo com a UF escolhida.<br />
     * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/dados_interessado.jsp</li>
     * </ul>
     * 
     * @param e
     * @throws DAOException
     */
    public void carregarMunicipios(ValueChangeEvent e) throws DAOException {
		String selectId = e.getComponent().getId();
		if (selectId != null && e.getNewValue() != null) {
			Integer idUf = (Integer) e.getNewValue();
			carregarMunicipios(idUf);
		}
    }
    
    /**
     * Carrega o combo de municípios de acordo com o id da UF passado.<br />
     * Método não invocado por JSPs.
     * 
     * @param idUf
     * @throws DAOException
     */
    public void carregarMunicipios(Integer idUf) throws DAOException {
		if ( idUf == null || idUf == 0) 
			idUf = interessadoNaoAutenticado.getEndereco().getUnidadeFederativa().getId() == 0
			? UnidadeFederativa.ID_UF_PADRAO : interessadoNaoAutenticado.getEndereco().getUnidadeFederativa().getId();
	
		MunicipioDao dao = getDAO(MunicipioDao.class);
		UnidadeFederativa uf = dao.findByPrimaryKey(idUf, UnidadeFederativa.class);
		Collection<Municipio> municipios = dao.findByUF(idUf);
		if(interessadoNaoAutenticado.getEndereco() != null) {
		    interessadoNaoAutenticado.getEndereco().setUnidadeFederativa(uf);
		    interessadoNaoAutenticado.getEndereco().setMunicipio(uf.getCapital());
		}
		municipiosCombo = new ArrayList<SelectItem>();
		municipiosCombo.add(new SelectItem(uf.getCapital().getId(), uf.getCapital().getNome()));
		municipiosCombo.addAll(toSelectItems(municipios, "id", "nome"));
    }
    
    /**
     * Seta o município escolhido no combo nos dados do {@link #interessadoNaoAutenticado}.
     * 
     * @param e
     * @throws DAOException
     */
    public void setarMunicipio(ValueChangeEvent e) throws DAOException {
		if(e.getNewValue() != null) {
		    GenericDAO dao = getGenericDAO();
		    try {
				int idMunicipio = (Integer) e.getNewValue();
				Municipio municipio = dao.findByPrimaryKey(idMunicipio, Municipio.class, "id", "nome");
				
				interessadoNaoAutenticado.getEndereco().setMunicipio(municipio);
		    } finally {
		    	dao.close();
		    }
		}
    }

    /**
     * Verifica se a {@link OrigemManifestante} selecionada é comunidade interna.
     * 
     * @return
     */
    public boolean isComunidadeInterna() {
    	return origemManifestante == COMUNIDADE_INTERNA;
    }
    
    @Override
    public String getDirBase() {
    	return super.getDirBase() + "/ouvidor";
    }
    
    /**
     * Retorna as origens de manifestante disponíveis em forma de combo.
     * 
     * @return
     */
    public Collection<SelectItem> getAllOrigensManifesanteCombo() {
		Collection<SelectItem> origens = new ArrayList<SelectItem>();
		    
		origens.add(new SelectItem(COMUNIDADE_INTERNA, "Comunidade Interna"));
		origens.add(new SelectItem(COMUNIDADE_EXTERNA, "Comunidade Externa"));
		    
		return origens;
    }
    
    /**
     * Retorna todas as categorias de um solicitante interno à instituição em forma de combo.
     * 
     * @return
     * @throws HibernateException
     * @throws DAOException
     */
    public Collection<SelectItem> getAllCategoriasSolicitanteInternoCombo() throws HibernateException, DAOException {
    	return toSelectItems(getAllCategoriasSolicitanteInterno(), "id", "descricao");
    }

    /**
     * Retorna todas as categorias de um solicitante interno à instituição.
     * 
     * @return
     * @throws HibernateException
     * @throws DAOException
     */
    private Collection<CategoriaSolicitante> getAllCategoriasSolicitanteInterno() throws HibernateException, DAOException {
		CategoriaSolicitanteDao dao = getDAO(CategoriaSolicitanteDao.class);
		Collection<CategoriaSolicitante> categorias = new ArrayList<CategoriaSolicitante>();
		
		try {
		    categorias = dao.getAllCategoriasSolicitanteInterno();
		} finally {
		    dao.close();
		}
		
		return categorias;
    }

    public InteressadoNaoAutenticado getInteressadoNaoAutenticado() {
        return interessadoNaoAutenticado;
    }

    public void setInteressadoNaoAutenticado(InteressadoNaoAutenticado interessadoNaoAutenticado) {
        this.interessadoNaoAutenticado = interessadoNaoAutenticado;
    }

    public Discente getDiscente() {
        return discente;
    }

    public void setDiscente(Discente discente) {
        this.discente = discente;
    }

    public Servidor getServidor() {
        return servidor;
    }

    public void setServidor(Servidor servidor) {
        this.servidor = servidor;
    }

    public String getCodArea() {
        return codArea;
    }

    public void setCodArea(String codArea) {
        this.codArea = codArea;
    }

    public int getOrigemManifestante() {
        return origemManifestante;
    }

    public void setOrigemManifestante(int origemManifestante) {
        this.origemManifestante = origemManifestante;
    }

    public Collection<SelectItem> getMunicipiosCombo() {
        return municipiosCombo;
    }

    public void setMunicipiosCombo(Collection<SelectItem> municipiosCombo) {
        this.municipiosCombo = municipiosCombo;
    }

    public Collection<? extends DiscenteAdapter> getDiscentes() {
        return discentes;
    }

    public void setDiscentes(Collection<? extends DiscenteAdapter> discentes) {
        this.discentes = discentes;
    }

    public Collection<Servidor> getServidores() {
        return servidores;
    }

    public void setServidores(Collection<Servidor> servidores) {
        this.servidores = servidores;
    }

    public boolean isBuscaMatricula() {
        return buscaMatricula;
    }

    public void setBuscaMatricula(boolean buscaMatricula) {
        this.buscaMatricula = buscaMatricula;
    }

    public boolean isBuscaNome() {
        return buscaNome;
    }

    public void setBuscaNome(boolean buscaNome) {
        this.buscaNome = buscaNome;
    }

    public boolean isBuscaCurso() {
        return buscaCurso;
    }

    public void setBuscaCurso(boolean buscaCurso) {
        this.buscaCurso = buscaCurso;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }
    
}
