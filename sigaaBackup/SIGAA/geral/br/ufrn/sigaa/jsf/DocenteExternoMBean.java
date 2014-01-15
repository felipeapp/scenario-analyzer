/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Criado em: 24/04/2008
 */
package br.ufrn.sigaa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.GenericTipo;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.rh.dominio.Situacao;
import br.ufrn.sigaa.arq.dao.InstituicoesEnsinoDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.ensino.DocenteExternoDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.parametros.dominio.MensagensStrictoSensu;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.EstadoCivil;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.pessoa.dominio.TipoDocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.TipoLogradouro;
import br.ufrn.sigaa.pessoa.dominio.TipoRaca;

/**
 * Managed Bean responsável pelas operações básicas de Docente Externo
 *
 * @author Andre
 *
 */
public class DocenteExternoMBean extends SigaaAbstractController<DocenteExterno> implements OperadorDadosPessoais{

	/** Formulário de cadastro. */
	private static final String VIEW_FORM = "/administracao/docente_externo/form.jsf";

	/** Lista de docentes externos. */
	private static final String VIEW_LIST = "/administracao/docente_externo/lista.jsf";

	/** Detalhes do docente externo. */
	private static final String VIEW_DETAILS = "/administracao/docente_externo/detalhes.jsf";

	/** Sexo do docente. */
	private String sexo;

	/** RG do docente */
	private String rg;

	/** Indica se o docente é do nível graduação. */
	private boolean graduacao = true;

	/** Indica se o docente é do nível stricto senso. */
	private boolean stricto = false;

	/** Indica se o docente é do nível técnico. */
	private boolean tecnico = false;
	
	/** Indica se o docente é do nível lato sensu. */
	private boolean lato = false;
	
	/** Indica se o docente é do nível Infantil. */
	private boolean infantil = false;
	
	/** Indica se o docente é do nível Médio. */
	private boolean medio = false;

	/** Indica se a operação atual é remover docente externo */
	private boolean remover = false;

	/** Docentes encontrados na tela de busca. */
	private Collection<DocenteExterno> docentesEncontrados;

	/** MBean responsável pela digitação dos dados pessoais do docente. */
	private DadosPessoaisMBean dadosPessoaisMBean;
	
	/** Permite escolher o tipo do docente externo no formulário */
	private boolean escolheTipo = true;
	
	/** Unidade Acadêmica */
	private Unidade unidade;
	
	/** Permite escolher a instituição de ensino no formulário */
	private boolean escolheInstituicao = true;
	
	/** Permite escolher o tipo do docente externo no formulário */
	private boolean escolheTipoDocenteExterno = true;

	/** Permite escolher o departameto no formulário */
	private boolean escolheDepartamento = true;
	
	/** Permite escolher o nível de Ensino no formulário */
	private boolean escolheNivelEnsino = false;
	
	/** Construtor padrão
	 * 
	 */
	public DocenteExternoMBean() {
		init();
	}

	/**
	 * Inicia o procedimento pra alterar docente externo
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/stricto/menu_coordenador.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 */
	public String iniciarAlterar() {
		init();
		dadosPessoaisMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
		dadosPessoaisMBean.setCodigoOperacao( OperacaoDadosPessoais.DOCENTE_EXTERNO );
		dadosPessoaisMBean.initObj();
		dadosPessoaisMBean.setOperacao(OperacaoDadosPessoais.getOperacao(dadosPessoaisMBean.getCodigoOperacao()));
		return forward("/administracao/docente_externo/lista.jsp");
	}
	
	/** Inicializa os atributos.
	 * 
	 */
	private void init() {
		remover = false;
		obj = new DocenteExterno();
		obj.getPessoa().setTipoRedeEnsino(null);
		// setando valores default
		obj.getPessoa().setTipoRaca(new TipoRaca(GenericTipo.NAO_INFORMADO));
		obj.getPessoa().setEstadoCivil(new EstadoCivil(GenericTipo.NAO_INFORMADO));
		obj.getPessoa().getIdentidade().setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
		obj.getPessoa().setPais(new Pais(Pais.BRASIL));
		obj.getPessoa().setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
		obj.getPessoa().getEnderecoContato().setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
		obj.getPessoa().getEnderecoContato().setTipoLogradouro(new TipoLogradouro(GenericTipo.NAO_INFORMADO));
		obj.setUnidade( new Unidade() );
		dadosPessoaisMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
		dadosPessoaisMBean.setCodigoOperacao( OperacaoDadosPessoais.DOCENTE_EXTERNO );
		dadosPessoaisMBean.initObj();
		docentesEncontrados = new ArrayList<DocenteExterno>();
	}
	
	/**
	 * Método que responde às requisições de autocomplete com o componente
	 * rich:suggestionBox do RichFaces retornando uma lista de Docentes Externos por nome.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/ensino/turma/docentes.jsp</li>
	 * </ul>
	 * 
	 * @param event
	 * @return
	 * @throws DAOException 
	 */
	public Collection<DocenteExterno> autoCompleteNomeDocenteExterno(Object event) throws DAOException{
		DocenteExternoDao dao = getDAO(DocenteExternoDao.class);
		String nome = event.toString();
 		Collection<DocenteExterno> lista = dao.findByNomeUnidade(nome, null);
 		return lista;
	}
	
	
	/**
	 * Método que responde às requisições de autocomplete com o componente
	 * rich:suggestionBox do RichFaces retornando uma lista de pessoas por nome.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/stricto/banca_pos/membros.jsp</li>
	 * </ul>
	 * 
	 * @param event
	 * @return
	 * @throws DAOException 
	 */
	public Collection<Pessoa> autoCompletePessoaNaoServidorNaoDocenteExterno(Object event) throws DAOException{
		PessoaDao dao = getDAO(PessoaDao.class);
		String nome = event.toString();
 		Collection<Pessoa> lista = dao.findByNomeTipoNaoServidorNaoDocenteExterno(nome, Pessoa.PESSOA_FISICA);
 		return lista;
	}

	/** Retorna a lista de docentes encontrados.
	 * 
	 * @return Lista de DocenteExterno encontrado.
	 */
	public Collection<DocenteExterno> getDocentesEncontrados() {
		return docentesEncontrados;
	}

	/** Seta a lista de Docentes.
	 * @param docentesEncontrados
	 */	
	public void setDocentesEncontrados(Collection<DocenteExterno> docentesEncontrados) {
		this.docentesEncontrados = docentesEncontrados;
	}

	/** Retorna o RG do docente.
	 * @return RG do docente.
	 */
	public String getRg() {
		rg = obj.getPessoa().getIdentidade().getNumero();
		return rg;
	}

	/** Seta o RG do docente.
	 * @param rg
	 */
	public void setRg(String rg) {
		this.rg = rg;
		obj.getPessoa().getIdentidade().setNumero(rg);
	}

	/** Retorna o sexo do docente.
	 * @return the sexo
	 */
	public String getSexo() {
		sexo = obj.getPessoa().getSexo() + "";
		return sexo;
	}

	/** Seta o sexo do docente.
	 * @param sexo
	 */
	public void setSexo(String sexo) {
		this.sexo = sexo;
		if (sexo != null)
			obj.getPessoa().setSexo(sexo.charAt(0));
	}

	/** Indica se é graduação.
	 * @return
	 */
	public boolean isGraduacao() {
		return graduacao;
	}

	/** Seta o valor de graduação.
	 * @param graduacao
	 */
	public void setGraduacao(boolean graduacao) {
		this.graduacao = graduacao;
	}

	/** Indica se é Stricto Senso.
	 * @return
	 */
	public boolean isStricto() {
		return stricto;
	}

	/** Seta o valor de Stricto Senso.
	 * @param stricto
	 */
	public void setStricto(boolean stricto) {
		this.stricto = stricto;
	}

	/**
	 * Operação realizada depois da realização do cadastro.
	 * 
 	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 */
	@Override
	protected void afterCadastrar() {
		removeOperacaoAtiva();
		init();
	}
	
	/**
	 * Inicia a busca de docentes externos.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/infantil/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarBusca(){
		obj.getPessoa().setNome("");
		docentesEncontrados = new ArrayList<DocenteExterno>();
		return forward(VIEW_LIST);
	}

	/** Busca por docentes externos.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/administracao/docente_externo/lista.jsp</li>
	 *	</ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#buscar()
	 */
	@Override
	public String buscar() throws ArqException {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.SECRETARIA_DEPARTAMENTO, SigaaPapeis.GESTOR_LATO, SigaaPapeis.GESTOR_INFANTIL, SigaaPapeis.PPG,
				SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO,
				SigaaPapeis.COORDENADOR_GERAL_EAD, SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO, 
				SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR);

		String nome = obj.getNome();
		DocenteExternoDao dao = getDAO(DocenteExternoDao.class);
		if( getSubSistema().getId() == SigaaSubsistemas.INFANTIL.getId() && isUserInRole(SigaaPapeis.GESTOR_INFANTIL) )
			docentesEncontrados = dao.findByNomeUnidade(nome, getParametrosAcademicos().getUnidade().getId() , true, false);
		if( getSubSistema().getId() == SigaaSubsistemas.MEDIO.getId() && isUserInRole(SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO) )
			docentesEncontrados = dao.findByNomeUnidade(nome, getParametrosAcademicos().getUnidade().getId() , true, false);
		else if( getSubSistema().getId() == SigaaSubsistemas.ADMINISTRACAO.getId() && isUserInRole( SigaaPapeis.ADMINISTRADOR_SIGAA ) )
			docentesEncontrados = dao.findByNomeUnidade(nome, null, true, false);
		else if( getSubSistema().getId() == SigaaSubsistemas.GRADUACAO.getId() && isUserInRole( SigaaPapeis.DAE ) )
			docentesEncontrados = dao.findByNomeUnidade(nome, null,true, false,
					TipoUnidadeAcademica.COORDENACAO_CURSO,	TipoUnidadeAcademica.DEPARTAMENTO, TipoUnidadeAcademica.ESCOLA, TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA);
		else if( getSubSistema().getId() == SigaaSubsistemas.SEDIS.getId() && isUserInRole( SigaaPapeis.COORDENADOR_GERAL_EAD ) )
			docentesEncontrados = dao.findByNomeUnidade(nome, null,true, false,
					TipoUnidadeAcademica.COORDENACAO_CURSO,	TipoUnidadeAcademica.DEPARTAMENTO, TipoUnidadeAcademica.ESCOLA, TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA);		
		else if( getSubSistema().getId() == SigaaSubsistemas.GRADUACAO.getId() && isUserInRole( SigaaPapeis.SECRETARIA_DEPARTAMENTO ) )
			docentesEncontrados = dao.findByNomeUnidade(nome, getUsuarioLogado().getVinculoAtivo().getUnidade().getId(), true, false, TipoUnidadeAcademica.DEPARTAMENTO,
					TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA, TipoUnidadeAcademica.COORDENACAO_CURSO );
		else if( getSubSistema().getId() == SigaaSubsistemas.LATO_SENSU.getId() && isUserInRole( SigaaPapeis.GESTOR_LATO ) ){
			docentesEncontrados = dao.findByPessoaDocenteExternoAtivo(nome);
			if (docentesEncontrados.size() >= 200) {
				docentesEncontrados = new ArrayList<DocenteExterno>();
				addMensagem(MensagensArquitetura.BUSCA_MAXIMO_RESULTADOS, 200);
			}
		}	
		else if( getSubSistema().getId() == SigaaSubsistemas.STRICTO_SENSU.getId() && isUserInRole( SigaaPapeis.PPG ) )
			docentesEncontrados = dao.findByNomeUnidade(nome, null, true, false, TipoUnidadeAcademica.PROGRAMA_POS);
		else if( getSubSistema().getId() == SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO.getId() && isUserInRole( SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS ) )
			docentesEncontrados = dao.findByNomeUnidade(nome, getProgramaStricto().getId(), true, false, TipoUnidadeAcademica.PROGRAMA_POS);
		else if( getSubSistema().getId() == SigaaSubsistemas.TECNICO.getId() && isUserInRole(SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO) )
			docentesEncontrados = dao.findByNomeUnidade(nome, getUnidadeGestora(), true, false);
		else if( getSubSistema().getId() == SigaaSubsistemas.PORTAL_DOCENTE.getId() && isUserInRole( SigaaPapeis.CHEFE_DEPARTAMENTO) )
			docentesEncontrados = dao.findByNomeUnidade(nome, getUsuarioLogado().getVinculoAtivo().getUnidade().getId(), true, false, TipoUnidadeAcademica.DEPARTAMENTO );
		else if( getSubSistema().getId() == SigaaSubsistemas.FORMACAO_COMPLEMENTAR.getId() && isUserInRole(SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR) )
			docentesEncontrados = dao.findByNomeUnidade(nome, getUnidadeGestora(), true, false);
			
		if(isEmpty(docentesEncontrados))
			addMensagemErro("Nenhum docente encontrado com os parâmetros indicados.");
		
		return null;
	}

	/**
	 * Método que direciona para a tela do formulário
	 * 
 	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 */
	@Override
	public String getFormPage() {
		return VIEW_FORM;
	}

	/**
	 * Método que direciona para a tela da listagem
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 */
	@Override
	public String getListPage() {
		init();
		return VIEW_LIST;
	}

	/**
	 * Direciona para a tela da listagem ou do módulo, removendo a operação ativa.   
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}

	/** Inicia a operação de remover.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/administracao/docente_externo/lista.jsp</li>
	 *	</ul>
	 * @return @see {@link #VIEW_DETAILS}
	 * @throws ArqException
	 */
	public String iniciarRemover() throws ArqException {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.PPG, SigaaPapeis.ADMINISTRADOR_DAE,SigaaPapeis.GESTOR_LATO,
				SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO,
				SigaaPapeis.GESTOR_INFANTIL, SigaaPapeis.GESTOR_TECNICO);
		setId();
		this.obj = getGenericDAO().refresh(obj);

		try {
			if( SigaaSubsistemas.GRADUACAO.equals(getSubSistema()) && isUserInRole( SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE )
					&& !isEmpty( obj.getUnidade() ) && !obj.getUnidade().isDepartamento() && !obj.getUnidade().isUnidadeAcademicaEspecializada()){
					throw new NegocioException( "Você não tem permissão para remover este docente externo pois ele não está associado a um departamento ou a uma unidade" +
							" acadêmica especializada" );
			}
			if( SigaaSubsistemas.STRICTO_SENSU.equals(getSubSistema()) &&  isUserInRole( SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO )
					&& !isEmpty( obj.getUnidade() ) && !obj.getUnidade().isPrograma() ){
				throw new NegocioException( "Você não tem permissão para remover este docente externo pois ele não está associado a um programa de pós-graduação" );
			}
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		}

		setReadOnly(true);
		setConfirmButton("Remover");
		remover = true;
		prepareMovimento(SigaaListaComando.REMOVER_DOCENTE_EXTERNO);
		return forward(VIEW_DETAILS);
	}

	/** 
	 * Visualiza os dados do docente externo
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return @see {@link #VIEW_DETAILS}
	 */
	public String visualizar() {
		GenericDAO dao = getGenericDAO();
		
		dao.setSistema(getSistema());
		dao.setSession(getSessionRequest());
		
		try {
			setId();

			this.obj = dao.findByPrimaryKey(obj.getId(), obj.getClass());
		} catch (Exception e) {
			tratamentoErroPadrao(e);
		}

		setConfirmButton("Voltar");

		return forward(VIEW_DETAILS);

	}

	/** Volta para a tela da listagem.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/administracao/docente_externo/detalhes.jsp</li>
	 *		<li>sigaa.war/administracao/docente_externo/form.jsp</li>
	 *	</ul>
	 * @see br.ufrn.arq.web.jsf.AbstractController#cancelar()
	 */
	public String voltar() {
		if (getConfirmButton().equalsIgnoreCase("remover") || getConfirmButton().equalsIgnoreCase("alterar"))
			return forward(VIEW_LIST);
		removeOperacaoAtiva();
		return super.cancelar();
	}
	
	/** Atualiza os docente externo.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/administracao/docente_externo/lista.jsp</li>
	 *	</ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	@Override
	public String atualizar() throws ArqException {
		PessoaDao dao = getDAO(PessoaDao.class);
		setOperacaoAtiva(SigaaListaComando.ALTERAR_DOCENTE_EXTERNO.getId());
		prepareMovimento(SigaaListaComando.ALTERAR_DOCENTE_EXTERNO);
		populateObj(true);
		setConfirmButton("Alterar");
		remover = false;
		obj.setPessoa(dao.refresh(obj.getPessoa()));
		try {
			if ( obj.getUnidade() != null )
				obj.setUnidade( dao.findByPrimaryKey(obj.getUnidade().getId(), Unidade.class) );
		} catch (DAOException e) {
			return tratamentoErroPadrao(e);
		}

		if( obj.getPessoa().getMunicipio() != null )
			getCurrentRequest().setAttribute("naturMunicipio", obj.getPessoa().getMunicipio().getId());
		if (obj.getPessoa().getEnderecoContato() != null && obj.getPessoa().getEnderecoContato().getMunicipio() != null )
			getCurrentRequest().setAttribute("endMunicipio", obj.getPessoa().getEnderecoContato().getMunicipio().getId());

		if( obj.getFormacao() == null )
			obj.setFormacao(new Formacao());
		if( obj.getTipoDocenteExterno() == null )
			obj.setTipoDocenteExterno(new TipoDocenteExterno());	
		if( obj.getInstituicao() == null )
			obj.setInstituicao(new InstituicoesEnsino());				
		if( obj.getPrazoValidade() == null )
			obj.setPrazoValidade(new Date());
		if( obj.getUnidade() == null )
			obj.setUnidade(new Unidade());
		else
			obj.getUnidade().getId();

		graduacao = getNivelEnsino() == NivelEnsino.GRADUACAO;
		stricto = NivelEnsino.isAlgumNivelStricto(getNivelEnsino());
		tecnico = NivelEnsino.isEnsinoBasico(getNivelEnsino());
		lato = getNivelEnsino() == NivelEnsino.LATO;
		infantil = getNivelEnsino() == NivelEnsino.INFANTIL;
		medio = getNivelEnsino() == NivelEnsino.MEDIO;
		
		if (medio) setEscolheNivelEnsino(true);

		return forward( getFormPage() );
	}
	
	/** 
	 * Inicia o cadastro de docente externo.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/graduacao/menus/administracao.jsp</li>
	 *	</ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		init();
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_DOCENTE_EXTERNO.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_DOCENTE_EXTERNO);
		return popular();
	}

	/**
	 * Chama o operador de dados pessoais para realizar a verificação do CPF e redireciona para o cadastro do docente externo.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/administracao/menu_administracao.jsp</li>
	 *		<li>sigaa.war/stricto/menus/permissao.jsp</li>
	 *		<li>sigaa.war/stricto/menu_coordenador.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/administracao.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/turma.jsp</li>
	 *		<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String popular() throws ArqException{
		checkRole(new int[] { SigaaPapeis.PPG, SigaaPapeis.ADMINISTRADOR_SIGAA, SigaaPapeis.DAE , SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO, SigaaPapeis.GESTOR_INFANTIL,
				SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_GERAL_EAD, SigaaPapeis.CHEFE_DEPARTAMENTO,
				SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR});

		init();
		setConfirmButton("Cadastrar");
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_DOCENTE_EXTERNO.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_DOCENTE_EXTERNO);
		graduacao = getNivelEnsino() == NivelEnsino.GRADUACAO;
		stricto = NivelEnsino.isAlgumNivelStricto(getNivelEnsino());
		tecnico = NivelEnsino.isEnsinoBasico(getNivelEnsino());
		lato = getNivelEnsino() == NivelEnsino.LATO;
		infantil = getNivelEnsino() == NivelEnsino.INFANTIL;
		medio = getNivelEnsino() == NivelEnsino.MEDIO;
		return dadosPessoaisMBean.popular();
	}
	/**
	 * Retorna a tela incial dos dados pessoais
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/administracao/docente_externo/form.jsp</li>
	 *	</ul>
	 */
	public String voltaTelaDadosPessoais() throws SegurancaException, DAOException{
		dadosPessoaisMBean.setObj(obj.getPessoa());
		return dadosPessoaisMBean.popular();
	}
	
	/** Prepara o DadosPessoaisMBean e chama o form.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/administracao/docente_externo/form.jsp</li>
	 *	</ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String telaDadosPessoais() throws SegurancaException, DAOException {
		dadosPessoaisMBean.setObj(obj.getPessoa());
		dadosPessoaisMBean.setPassivelAlterarCpf(false);
		dadosPessoaisMBean.setExibirPainel(false);
		return dadosPessoaisMBean.voltarDadosPessoais();
	}

	/**
	 * Seta os dados pessoais
	 */
	public void setDadosPessoais(Pessoa pessoa) {
		obj.setPessoa(pessoa);
	}

	/** Valida os dados pessoais do docente.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @throws  
	 * @see br.ufrn.sigaa.jsf.OperadorDadosPessoais#submeterDadosPessoais()
	 * Chamado por {@link #dadosPessoaisMBean#VIEW_FORM}
	 */
	public String submeterDadosPessoais()  {
		
		ServidorDao servidorDao = null;
		DocenteExternoDao docenteExternoDao = null;
		
		obj.getPessoa().anularAtributosVazios();
		// verifica se a pessoa tem vinculo como servidor aposentado, 
		// pois neste caso o docente deve ser cadastrado como colaborador voluntário.
		try{
			servidorDao = getDAO(ServidorDao.class);
			if( (!isUserInRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.PPG)) && ( !isEmpty(obj.getPessoa().getCpf_cnpj()))) {
				
					
					Servidor servidor = servidorDao.findByCpf(obj.getPessoa().getCpf_cnpj());
					if (servidor != null && 
							servidor.getPessoa().getCpf_cnpj().equals(obj.getPessoa().getCpf_cnpj())
							&& servidor.getSituacaoServidor().getId() == Situacao.APOSENTADO) {
						GenericDAO gDao = getDAO(GenericDAOImpl.class);
						TipoDocenteExterno tipo = gDao.findByPrimaryKey(TipoDocenteExterno.COLABORADOR_VOLUNTARIO, TipoDocenteExterno.class);
						obj.setTipoDocenteExterno(tipo);
						String msg = "Não é possível cadastrar o docente: o CPF informado tem vínculo como servidor aposentado e deve ser cadastrado como Colaborador Voluntário.";
						if( isUserInRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS) )
							msg += " "+ UFRNUtils.getMensagem(MensagensStrictoSensu.CADASTRO_DOCENTE_EXTERNO_APENAS_GESTOR).getMensagem();					
						addMensagemErro(msg);
						return null;
					} else {
						if (ValidatorUtil.isEmpty(obj.getTipoDocenteExterno())) {
							obj.setTipoDocenteExterno(new TipoDocenteExterno(0));
							escolheTipo = true;
						}
					}
			}
			
			if (!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_DOCENTE_EXTERNO.getId())){
				setConfirmButton("Alterar");
				remover = false;
			}
			if (obj.getId() == 0 && !checkOperacaoAtiva(SigaaListaComando.CADASTRAR_DOCENTE_EXTERNO.getId())){
				docenteExternoDao = getDAO(DocenteExternoDao.class);					
				setObj( docenteExternoDao.findByCPF(obj.getPessoa().getCpf_cnpj()) );
			}
			
			if( isPortalCoordenadorStricto() && ( getAcessoMenu().isSecretariaPosGraduacao() || getAcessoMenu().isCoordenadorCursoStricto() ))
				obj.setUnidade( getProgramaStricto() );
			else if( NivelEnsino.isEnsinoBasico(getNivelEnsino()) ){
				try {
					obj.setUnidade( getGenericDAO().findAndFetch(getUnidadeGestora(), Unidade.class, "nome") );
				} catch (ArqException e) {
					return tratamentoErroPadrao(e);
				}
			}
			
			if (getNivelEnsino() == NivelEnsino.INFANTIL){
				TipoDocenteExterno tipo = new TipoDocenteExterno();
				tipo.setId(TipoDocenteExterno.PROFESSOR_EXTERNO);
				obj.setTipoDocenteExterno(tipo);		
				setEscolheTipoDocenteExterno(false);
				try{				
					obj.setInstituicao(getDAO(InstituicoesEnsinoDao.class).findByPrimaryKey(InstituicoesEnsino.UFRN, InstituicoesEnsino.class));			
				} catch (DAOException e) {
					return tratamentoErroPadrao(e);
				}
				setEscolheInstituicao(false);			
			}
			
			if (getNivelEnsino() == NivelEnsino.GRADUACAO && isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO) && isPortalDocente()){
				escolheDepartamento = false;
				escolheTipo = false;				
				
				Integer idUnidade = null;
				if ( isPortalCoordenadorGraduacao() && isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO) )
					idUnidade = getCursoAtualCoordenacao().getUnidadeCoordenacao().getId();
				if ( ( isPortalGraduacao() || isPortalDocente() || isTurmaVirtual() ) && isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO) ){
					idUnidade = getUsuarioLogado().getVinculoAtivo().getUnidade().getId();	
				}	
				
				Unidade unidade = servidorDao.findByPrimaryKey(idUnidade, Unidade.class);
				obj.setUnidade(unidade);
				
				TipoDocenteExterno tipoDocenteExterno = servidorDao.findByPrimaryKey(TipoDocenteExterno.PROFESSOR_EXTERNO, TipoDocenteExterno.class);
				obj.setTipoDocenteExterno(tipoDocenteExterno);
			}
			
			if ( lato && getNivelEnsino() == NivelEnsino.LATO ){
				if ( isUserInRole(SigaaPapeis.GESTOR_LATO) ){ 
					escolheDepartamento = true;
				}else{
					escolheDepartamento = false;
					obj.setUnidade(getUsuarioLogado().getVinculoAtivo().getUnidade());
				}
			}	
			
			if (getNivelEnsino() == NivelEnsino.MEDIO){
				TipoDocenteExterno tipo = new TipoDocenteExterno();
				tipo.setId(TipoDocenteExterno.PROFESSOR_EXTERNO);
				obj.setTipoDocenteExterno(tipo);		
				setEscolheTipoDocenteExterno(false);
				try{				
					obj.setInstituicao(getDAO(InstituicoesEnsinoDao.class).findByPrimaryKey(InstituicoesEnsino.UFRN, InstituicoesEnsino.class));			
				} catch (DAOException e) {
					return tratamentoErroPadrao(e);
				}
				setEscolheInstituicao(true);			
			}
			
		} catch (DAOException e) {
			return tratamentoErroPadrao(e);
		} finally {
			if (servidorDao != null)
				servidorDao.close();
			if (docenteExternoDao != null)
				docenteExternoDao.close();
		}

		try {
			return forward( VIEW_FORM );
		} catch (Exception e) {
			return tratamentoErroPadrao(e);
		}
	}
	
	
	/** Cadastra o docente externo.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/administracao/docente_externo/detalhes.jsp</li>
	 *		<li>sigaa.war/administracao/docente_externo/form.jsp</li>
	 *	</ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 * 
	 */
	@Override
	public String cadastrar() throws ArqException {

		if( remover )
			return remover();

		if (!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_DOCENTE_EXTERNO.getId(), SigaaListaComando.ALTERAR_DOCENTE_EXTERNO.getId())){
			return cancelar();
		}
		erros = new ListaMensagens();
		ListaMensagens lista = obj.validate();
		
		if ( infantil ){
			obj.setUnidade(getUnidade());
		}
		
		if ( medio ){
			obj.setUnidade(getUnidade());
			obj.setNivel(NivelEnsino.MEDIO);
		}

		if( graduacao ){
			if( obj.getUnidade() == null || obj.getUnidade().getId() == 0 )
				lista.addErro("Informe o departamento.");
		}

		if( stricto ){
			if( obj.getUnidade() == null || obj.getUnidade().getId() == 0 )
				lista.addErro("Informe o programa.");
		}

		if( tecnico ){
			if( obj.getUnidade() == null || obj.getUnidade().getId() == 0 )
				lista.addErro("Informe a escola.");
		}

		if (lato) {
			if( obj.getUnidade() == null || obj.getUnidade().getId() == 0 )
				lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Departamento ou Programa:");
		}
		
		if (lista != null) {
			erros.addAll(lista.getMensagens());
		}


		if (!hasErrors()) {
			obj.setTipoDocenteExterno( getGenericDAO().findByPrimaryKey(obj.getTipoDocenteExterno().getId(), TipoDocenteExterno.class) );
			MovimentoCadastro mov = new MovimentoCadastro();
			obj.anularAtributosVazios();
			mov.setObjMovimentado(obj);

			if (obj.getId() == 0) {
				mov.setCodMovimento(SigaaListaComando.CADASTRAR_DOCENTE_EXTERNO);
				prepareMovimento(SigaaListaComando.CADASTRAR_DOCENTE_EXTERNO);
				try {
					execute(mov);
					addMessage("Operação realizada com sucesso!",	TipoMensagemUFRN.INFORMATION);
				}catch (NegocioException e) {
					addMensagens(e.getListaMensagens());
					return null;
				}

				afterCadastrar();

				return cancelar();

			} else {
				mov.setCodMovimento(SigaaListaComando.ALTERAR_DOCENTE_EXTERNO);
				prepareMovimento(SigaaListaComando.ALTERAR_DOCENTE_EXTERNO);
				try {
					execute(mov);
					addMessage(
							"Operação realizada com sucesso!",
							TipoMensagemUFRN.INFORMATION);
				} catch (NegocioException e) {
					addMensagens(e.getListaMensagens());
					return forward(getFormPage());
				}

				afterCadastrar();

				String forward = forwardCadastrar();
				if (forward == null)
					return forward(getListPage());
				else
					return forward(forward);
			}

		}

		return null;
	}

	/** Remove o docente externo.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 */
	@Override
	public String remover() throws ArqException {

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.REMOVER_DOCENTE_EXTERNO);
		mov.setObjMovimentado(obj);

		try {
			execute(mov);
			docentesEncontrados.remove(obj);
			addMensagemInformation("Docente externo removido com sucesso!");
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		}
		removeOperacaoAtiva();
		return cancelar();
	}

	/** Indica se é técnico.
	 * @return
	 */
	public boolean isTecnico() {
		return tecnico;
	}

	/** Seta o valor de técnico.
	 * @param tecnico
	 */
	public void setTecnico(boolean tecnico) {
		this.tecnico = tecnico;
	}

	/** Indica se pode-se escolher o tipo.
	 * @return
	 */
	public boolean isEscolheTipo() {
		return escolheTipo;
	}

	/** Seta o valor para escolha de tipo.
	 * @param escolheTipo
	 */
	public void setEscolheTipo(boolean escolheTipo) {
		this.escolheTipo = escolheTipo;
	}

	/** Indica se o docente é do nível lato sensu. 
	 * @return
	 */
	public boolean isLato() {
		return lato;
	}

	/** Seta se o docente é do nível lato sensu. 
	 * @param lato
	 */
	public void setLato(boolean lato) {
		this.lato = lato;
	}

	/**
	 * Indica se o docente é do nível Infantil
	 */
	public boolean isInfantil() {
		return infantil;
	}

	/**
	 * Seta se o docente é do nível Infantil 
	 */
	public void setInfantil(boolean infantil) {
		this.infantil = infantil;
	}

	public boolean isMedio() {
		return medio;
	}

	public void setMedio(boolean medio) {
		this.medio = medio;
	}

	/**
	 * Retorna a unidade acadêmica.
	 * @return
	 * @throws DAOException 
	 */
	public Unidade getUnidade() throws DAOException {
		unidade = getParametrosAcademicos().getUnidade();
		return unidade;
	}

	public boolean isEscolheInstituicao() {
		return escolheInstituicao;
	}

	public void setEscolheInstituicao(boolean escolheInstituicao) {
		this.escolheInstituicao = escolheInstituicao;
	}

	public boolean isEscolheTipoDocenteExterno() {
		return escolheTipoDocenteExterno;
	}

	public void setEscolheTipoDocenteExterno(boolean escolheTipoDocenteExterno) {
		this.escolheTipoDocenteExterno = escolheTipoDocenteExterno;
	}

	public boolean isEscolheDepartamento() {
		return escolheDepartamento;
	}

	public void setEscolheDepartamento(boolean escolheDepartamento) {
		this.escolheDepartamento = escolheDepartamento;
	}

	public boolean isEscolheNivelEnsino() {
		return escolheNivelEnsino;
	}

	public void setEscolheNivelEnsino(boolean escolheNivelEnsino) {
		this.escolheNivelEnsino = escolheNivelEnsino;
	}

}
