/* 
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 01/10/2007
 * 
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.graduacao.HabilitacaoDao;
import br.ufrn.sigaa.arq.dao.graduacao.MatrizCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.OfertaVagasCursoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.AreaSesu;
import br.ufrn.sigaa.ensino.dominio.GrauAcademico;
import br.ufrn.sigaa.ensino.dominio.OfertaVagasCurso;
import br.ufrn.sigaa.ensino.dominio.SituacaoCursoHabil;
import br.ufrn.sigaa.ensino.dominio.SituacaoDiploma;
import br.ufrn.sigaa.ensino.dominio.TipoRegimeLetivo;
import br.ufrn.sigaa.ensino.dominio.TipoSistemaCurricular;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.graduacao.dominio.Enfase;
import br.ufrn.sigaa.ensino.graduacao.dominio.Habilitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.jsf.ProcessoSeletivoMBean;

/**
 * Controller responsável por operações de matriz curricular.
 *
 * @author André
 *
 */
@Component("matrizCurricular") @Scope("session")
public class MatrizCurricularMBean extends SigaaAbstractController<MatrizCurricular> {

	/** Constante que define o link para o menu principal da graduação. */
	public static final String JSP_MENU_GRADUACAO = "menuGraduacao";

	/** Constante que define o link para o formulário de cadastro de matriz. */
	public static final String JSP_MATRIZ = "/graduacao/matriz_curricular/matriz.jsf";

	/** Constante que define o link para o formulário de definição da habilitação. */
	public static final String JSP_HABILITACAO = "/graduacao/matriz_curricular/habilitacao.jsf";

	/** Constante que define o link para a visualização do resumo das informações. */
	public static final String JSP_RESUMO = "/graduacao/matriz_curricular/resumo.jsf";
	
	/** Coleção de {@link SelectItem} de matrizes curriculares para seleção. */
	private List<SelectItem> matrizesCurriculares = new ArrayList<SelectItem>();

	/** Coleção de {@link SelectItem} de ênfases por curso para seleção. */
	private List<SelectItem> enfases = new ArrayList<SelectItem>();
	
	/** Nova habilitação a ser definida na matriz curricular. */
	private Habilitacao novaHabilitacao;

	/** Coleção de possíveis habilitações da matriz curricular. */
	private List<Habilitacao> habilitacoes = new ArrayList<Habilitacao>();

	/** Indica se deverá listar somente as matriz curriculares ativas. */
	private boolean somenteAtivas = true;
	
	/** Indica se deverá cadastrar uma nova habilitação. */
	private boolean cadastrarNovaHabilitacao = true;
	
	/** Indica o filtro a ser utilizado */
	private Boolean filtroCurso;
	
	/** Construtor padrão. */
	public MatrizCurricularMBean() {
		initObj();
	}
	
	/** Retorna a nova habilitação a ser definida na matriz curricular.
	 * @return
	 */
	public Habilitacao getNovaHabilitacao() {
		return novaHabilitacao;
	}

	/** Seta a nova habilitação a ser definida na matriz curricular.
	 * @param novaHabilitacao
	 */
	public void setNovaHabilitacao(Habilitacao novaHabilitacao) {
		this.novaHabilitacao = novaHabilitacao;
	}

	/** Retorna a coleção de possíveis habilitações da matriz curricular. 
	 * @return
	 */
	public List<Habilitacao> getHabilitacoes() {
		return habilitacoes;
	}

	/** Seta a coleção de possíveis habilitações da matriz curricular.
	 * @param habilitacoes
	 */
	public void setHabilitacoes(List<Habilitacao> habilitacoes) {
		this.habilitacoes = habilitacoes;
	}

	/** Retorna a coleção de SelectItem de possíveis habilitações da matriz curricular.
	 * @return
	 */
	public Collection<SelectItem> getHabilitacoesCombo() {
		return toSelectItems( getHabilitacoes() , "id", "nome");
	}

	/**
	 * Inicializa os métodos default, que serão utilizados no decorrer do caso de uso.
	 */
	private void initObj() {
		obj = new MatrizCurricular();
		obj.setAtivo(true);
		obj.setCurso(new Curso());
		obj.setGrauAcademico(new GrauAcademico());
		obj.setSituacao(new SituacaoCursoHabil());
		obj.setSituacaoDiploma(new SituacaoDiploma());
		obj.setCampus(new CampusIes());

		obj.setTurno(new Turno());
		obj.setHabilitacao(new Habilitacao());
		obj.setEnfase(new Enfase());
		if (getNivelEnsino() == NivelEnsino.GRADUACAO)
			obj.setRegimeLetivo(new TipoRegimeLetivo(TipoRegimeLetivo.SEMESTRAL));
		else
			obj.setRegimeLetivo(new TipoRegimeLetivo());

		if (getNivelEnsino() == NivelEnsino.GRADUACAO)
			obj.setTipoSistemaCurricular( new TipoSistemaCurricular( TipoSistemaCurricular.HORA_AULA ) );
		else
			obj.setTipoSistemaCurricular(new TipoSistemaCurricular());

		novaHabilitacao = new Habilitacao(new AreaSesu());
		setResultadosBusca(new ArrayList<MatrizCurricular>());
	}

	/**
	 * Retorna um selectItems com todas as matrizes Curriculares.
	 * @throws DAOException 
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllCombo()
	 */
	@Override
	public Collection<SelectItem> getAllCombo() throws DAOException {
		MatrizCurricularDao dao = getDAO(MatrizCurricularDao.class);
		return toSelectItems(dao.findAllOtimizado(),"id", "descricao");
	}

	/**
	 * Retorna para a tela do cadastro de um nova matriz curricular 
	 */
	@Override
	public String getFormPage() {
		return JSP_MATRIZ;
	}

	/**
	 * Retorna para a tela da listagem de uma matriz Curricular
	 */
	@Override
	public String getListPage() {
		return "/graduacao/matriz_curricular/lista.jsf";
	}

	/**
	 * Cancela a operação que estava sendo realizada.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/matriz_curricular/habilitacao.jsp</li>
	 * <li>/graduacao/matriz_curricular/matriz.jsp</li>
	 * <li>/graduacao/matriz_curricular/resumo.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		super.cancelar();
		if (obj.getId() == 0)
			return JSP_MENU_GRADUACAO;
		else
			return forward(getListPage());
	}

	/**
	 * Cadastra uma nova Matriz Curricular.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/matriz_curricular/resumo.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws ArqException {
		if (!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_MATRIZ_CURRICULAR.getId(), 
				SigaaListaComando.ALTERAR_MATRIZ_CURRICULAR.getId(),
				ArqListaComando.REMOVER.getId()))
			return null;
		if (obj == null) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		
		if ("remover".equalsIgnoreCase(getConfirmButton()))
			return remover();
		
		if (obj.getPossuiHabilitacao() != null &&
				obj.getPossuiHabilitacao().booleanValue()) {
			if (obj.getHabilitacao().getId() == 0)
				obj.setHabilitacao(novaHabilitacao);
		} else { 
			obj.setHabilitacao(null);
		}
		
		if (obj.getPossuiEnfase() != null &&
				!obj.getPossuiEnfase().booleanValue())
			obj.setEnfase(null);

		erros = new ListaMensagens();
		erros.addAll(obj.validate().getMensagens());
		if (hasErrors())
			return null;
		
		Comando comando;
		if (obj.getId() != 0)
			comando = SigaaListaComando.ALTERAR_MATRIZ_CURRICULAR;
		else
			comando = SigaaListaComando.CADASTRAR_MATRIZ_CURRICULAR;
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(comando);
		mov.setObjMovimentado(obj);
		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			obj.setEnfase(new Enfase());
			obj.setHabilitacao(new Habilitacao());
			return null;
		} 
		initObj();
		removeOperacaoAtiva();
		if (comando.equals(SigaaListaComando.CADASTRAR_MATRIZ_CURRICULAR)) {
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Matriz Curricular");
			return JSP_MENU_GRADUACAO;
		} else {
			addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Matriz Curricular");
			resultadosBusca = null;
			return forward(getListPage());
		}
	}
	
	/**
	 * Lista as matrizes curriculares cadastradas.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/menus/cdp.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#listar()
	 */
	@Override
	public String listar() throws ArqException {
		initObj();		
		return super.listar();
	}
	
	/**
	 * Inicializa os atributos que contemplam a matriz
	 * 
	 * @throws DAOException
	 */
	private void inicializarAtributosMatriz() throws DAOException {
		GenericDAO dao = getGenericDAO();
		if (obj.getRegimeLetivo().getId() > 0)
			obj.setRegimeLetivo(dao.refresh(obj.getRegimeLetivo()));
		if (obj.getTipoSistemaCurricular().getId() > 0)
			obj.setTipoSistemaCurricular(dao.refresh(obj.getTipoSistemaCurricular()));
		if (obj.getSituacao().getId() > 0)
			obj.setSituacao(dao.refresh(obj.getSituacao()));
		if (obj.getGrauAcademico().getId() > 0)
			obj.setGrauAcademico(dao.refresh(obj.getGrauAcademico()));
		if (obj.getTurno().getId() > 0)
			obj.setTurno(dao.refresh(obj.getTurno()));
		if (obj.getSituacaoDiploma().getId() > 0)
			obj.setSituacaoDiploma(dao.refresh(obj.getSituacaoDiploma()));
		if (obj.getCurso().getId() > 0)
			obj.setCurso(dao.refresh(obj.getCurso()));
		if (obj.getEnfase().getId() > 0)
			obj.setEnfase(dao.refresh(obj.getEnfase()));
		if (obj.getCampus().getId() > 0)
			obj.setCampus(dao.refresh(obj.getCampus()));		
	}

	/**
	 * Submete os dados para a matriz avançando assim para o próximo passo.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/matriz_curricular/matriz.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String submeterDadosMatriz() throws DAOException {
		checkOperacaoAtiva(SigaaListaComando.CADASTRAR_MATRIZ_CURRICULAR.getId());
		if (obj.getPossuiHabilitacao() != null && obj.getPossuiHabilitacao().booleanValue())
			obj.setPossuiEnfase(false);
		HabilitacaoDao dao = getDAO( HabilitacaoDao.class );
		if (obj.getCurso().getId() > 0)
			obj.setCurso(dao.refresh(obj.getCurso()));
		
		verificarOfertaDeVagas();
		
		ListaMensagens lista = obj.validate();
		if (lista != null)
			erros.addAll(lista.getMensagens());
		if (hasOnlyErrors())
			return null;

		inicializarAtributosMatriz();

		// carregando as habilitações do curso escolhido

		if (obj.getPossuiHabilitacao()) {
			habilitacoes = (List<Habilitacao>) dao.findByCurso( obj.getCurso().getId() );
			return forward(JSP_HABILITACAO);
		} else {
			return forward(JSP_RESUMO);
		}

	}

	/**
	 * Verifica se uma matriz possui oferta de vagas antes de inativá-la.<br>
	 * Método não invocado JSP(s):
	 * 
	 * @return
	 * @throws DAOException
	 */
	private void verificarOfertaDeVagas() throws DAOException {
		if ( !obj.getAtivo() ){
			
			OfertaVagasCursoDao oDao = getDAO( OfertaVagasCursoDao.class );
			Collection<OfertaVagasCurso> ofertasDaMatriz = oDao.findOfertasAtuaisByMatrizCurricular(obj.getId());
			
			if ( ofertasDaMatriz != null && !ofertasDaMatriz.isEmpty() ){
				
				String mensagemInativacao = "Esta matriz está sendo inativada, no entanto, exitem ofertas de vaga cadastradas para esta matriz nos anos: ";
				List<Integer> anos = new ArrayList<Integer>();
				boolean possuiVagas = false;
				
				for ( OfertaVagasCurso oferta : ofertasDaMatriz ){
					if (  oferta.getTotalVagas() > 0 || oferta.getTotalVagasOciosas() > 0 ){
						if ( !anos.contains(oferta.getAno()))
							anos.add(oferta.getAno());
						possuiVagas = true;
					}	
				}
				
				if ( possuiVagas ){
					String anosString = "";
					for ( Integer ano : anos )
						anosString += anosString == "" ? ano : ", " + ano;
					mensagemInativacao += anosString;		
					addMensagemWarning(mensagemInativacao);
				}	
			}
		}
	}

	/**
	 * Submete os dados de uma nova habilitação.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/matriz_curricular/habilitacao.jsp<</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String submeterDadosHabilitacao() throws DAOException {
		checkOperacaoAtiva(SigaaListaComando.CADASTRAR_MATRIZ_CURRICULAR.getId());
		String param = novaHabilitacao.getNome();
		String codigo = novaHabilitacao.getCodigoIes();
		
		Boolean criarNovaHabilitacao;
		if (!("").equals(param) && !("").equals(codigo) && obj.getHabilitacao().getId() != 0) {
			addMensagemErro("Está Selecionada uma Habibilitação já cadastrada e uma nova selecione apenas um tipo.");
			return null;
		}
		if (("").equals(param) && ("").equals(codigo) && obj.getHabilitacao().getId() != 0) {
			criarNovaHabilitacao = false;	
		}else{
			criarNovaHabilitacao = true;
		}
		HabilitacaoDao dao = getDAO(HabilitacaoDao.class);
		if (criarNovaHabilitacao) {
			erros = new ListaMensagens();
			ListaMensagens lista = novaHabilitacao.validate1();

			if (lista != null) {
				erros.addAll(lista.getMensagens());
			}
			if (hasErrors())
				return null;

			if (novaHabilitacao.getAreaSesu() != null && novaHabilitacao.getAreaSesu().getId() > 0)
				novaHabilitacao.setAreaSesu(dao.findByPrimaryKey(novaHabilitacao.getAreaSesu()
						.getId(), AreaSesu.class));
			obj.setHabilitacao(novaHabilitacao);
		} else {
			novaHabilitacao = new Habilitacao(new AreaSesu());
			if (obj.getHabilitacao().getId() > 0)
				obj.setHabilitacao(dao.findByPrimaryKey(obj.getHabilitacao().getId(),
						Habilitacao.class));
			else {
				addMensagemErro("Escolha uma habilitação ou cadastre uma nova");
				return null;
			}
		}

		return forward(JSP_RESUMO);
	}

	/**
	 * Chamado via AJAX para popular informações na view.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/administracao/cadastro/ProcessoSeletivo/form_cursos.jsp</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	public void carregarMatrizCurricular(ValueChangeEvent e) throws ArqException {

		String value = String.valueOf(e.getNewValue());
		matrizesCurriculares = new ArrayList<SelectItem>();	
		if(!ValidatorUtil.isEmpty(value)){
			Integer id = Integer.valueOf(value);
			matrizesCurriculares.addAll(toSelectItems(getGenericDAO().findByExactField(
			MatrizCurricular.class, "curso.id", id), "id", "descricao"));
			
			if (isLatoSensu()) {
				ProcessoSeletivoMBean mBean = getMBean("processoSeletivo");
				mBean.preencherNumeroVagas(id);
			}
		}else 	
			matrizesCurriculares.add(new SelectItem(0, "Selecione"));

	}

	/**
	 * Redireciona o usuário para a tela responsável pelo cadastro de uma matriz
	 * curricular.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/matriz_curricular/habilitacao.jsp</li>
	 * <li>/graduacao/matriz_curricular/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltarDadosMatriz() {
		return forward(JSP_MATRIZ);
	}

	/**
	 * Redireciona o usuário para a tela responsável pelo cadastro
	 * da habilitação.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/matriz_curricular/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltarDadosHabilitacao() {
		int id = obj.getHabilitacao().getId();
		if (id > 0)
			obj.setHabilitacao(new Habilitacao(id));
		return forward(JSP_HABILITACAO);
	}

	/**
	 * Seta a operação ativa, prepara o movimento , checar o usuário tem o papel
	 * necessário para prossegui com a operação e direciona-lo para a tela
	 * inicial do cadastro. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/menus/cdp.jsp</li>
	 * </ul>
	 */
	@Override
	public String preCadastrar() throws ArqException {
		checkRole(SigaaPapeis.CDP);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_MATRIZ_CURRICULAR.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_MATRIZ_CURRICULAR);
		initObj();
		setConfirmButton("Cadastrar");
		return forward(JSP_MATRIZ);
	}

	/**
	 * Atualiza os dados de uma matriz Curricular. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/matriz_curricular/lista.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException
	 */
	@Override
	public String atualizar() throws ArqException {
		checkRole(SigaaPapeis.CDP);
		populateObj(true);
		if (obj == null) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			initObj();
			return null;
		}
		
		if (obj.getEnfase() == null) obj.setEnfase(new Enfase());
		if (obj.getHabilitacao() == null) obj.setHabilitacao(new Habilitacao());
		if (obj.getSituacaoDiploma() == null) obj.setSituacaoDiploma(new SituacaoDiploma());
		if (obj.getCampus() == null) obj.setCampus(new CampusIes());
		carregarEnfasesByCurso(obj.getCurso().getId(), obj.getPossuiEnfase());
			
		prepareMovimento(SigaaListaComando.ALTERAR_MATRIZ_CURRICULAR);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_MATRIZ_CURRICULAR.getId());
		setReadOnly(false);

		setConfirmButton("Alterar");
		
		novaHabilitacao = new Habilitacao(new AreaSesu());
		return forward(JSP_MATRIZ);
	}

	/**
	 * Inicia a remoção de uma matriz.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/matriz_curricular/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String preRemover() {
		try {
			checkRole(SigaaPapeis.CDP);
			setOperacaoAtiva(ArqListaComando.REMOVER.getId());
			prepareMovimento(ArqListaComando.REMOVER);
			populateObj(true);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return forward(getListPage());
		}
		if (obj == null) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return null;
		} else {
			setConfirmButton("Remover");
			return forward(JSP_RESUMO);
		}
	}

	/**
	 * Busca por matrizes curriculares cadastradas.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/matriz_curricular/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String buscar() throws Exception {
		String param = getParameter("paramBusca");
		if (param == null) {
			addMensagemErro("Selecione um tipo de busca e digite o parâmetro de busca");
			return null;
		}
		MatrizCurricularDao dao = getDAO(MatrizCurricularDao.class);
		if ("curso".equalsIgnoreCase(param)){
			setResultadosBusca(dao.findByCurso(obj.getCurso().getId(), somenteAtivas ? true : null));
			setFiltroCurso(true);
		} else if ("todos".equalsIgnoreCase(param)) {
			setResultadosBusca( dao.findAtivos(somenteAtivas) ) ;
			setFiltroCurso(false);

		} else
			setResultadosBusca(null);
		
		if((getResultadosBusca()!=null && getResultadosBusca().isEmpty()) || getResultadosBusca()==null) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		}
		
		return null;
	}
	
	/**
	 * Método responsável por carregar as ênfases levando em consideração o curso selecionado para a matriz.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/matriz_curricular/matriz.jsp</li>
	 * </ul>
	 * @param e
	 * @throws ArqException
	 */
	public void carregarEnfasesByCurso(ValueChangeEvent e) throws ArqException {

		String value = String.valueOf(e.getNewValue());
		enfases = new ArrayList<SelectItem>();	
		
		if(!ValidatorUtil.isEmpty(value)){
			Boolean possuiEnfase;
			int idCurso = 0;
			if ( "false".equals(value) || "true".equals(value) ){
				possuiEnfase = Boolean.valueOf(value);
				idCurso = obj.getCurso() != null ? obj.getCurso().getId() : idCurso;
			} else {
				possuiEnfase = obj.getPossuiEnfase() != null && obj.getPossuiEnfase();
				idCurso = Integer.valueOf(value);
			}
			carregarEnfasesByCurso(idCurso, possuiEnfase);
		}else 	
			enfases.add(new SelectItem(0, "-- SELECIONE O CURSO --"));
		setEnfases(enfases);
	}
	
	/**
	 * Método responsável por carregar as ênfases levando em consideração o curso selecionado para a matriz,
	 * no processo de inicialização do bean.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/matriz_curricular/matriz.jsp</li>
	 * </ul>
	 * @param e
	 * @throws ArqException
	 */
	public void carregarEnfasesByCurso(int idCurso, boolean possuiEnfase) throws ArqException {
		enfases = new ArrayList<SelectItem>();	
		if ( possuiEnfase && idCurso > 0 ){
			Collection<Enfase> colEnfases = getGenericDAO().findByExactField(
					Enfase.class, "curso.id", idCurso);
			if ( colEnfases.size() > 0 ) {
				enfases.add(new SelectItem(0, "-- SELECIONE --"));
				enfases.addAll(toSelectItems(colEnfases, "id", "nome"));
			} else {
				enfases.add(new SelectItem(0, "-- NÃO HÁ ÊNFASES PARA ESTE CURSO --"));
			}
		} else {
			enfases.add(new SelectItem(0, "-- SELECIONE O CURSO --"));
		}
		setEnfases(enfases);
	}

	/** Indica se deverá listar somente as matriz curriculares ativas. 
	 * @return
	 */
	public boolean isSomenteAtivas() {
		return somenteAtivas;
	}

	/** Seta se deverá listar somente as matriz curriculares ativas.
	 * @param somenteAtivas
	 */
	public void setSomenteAtivas(boolean somenteAtivas) {
		this.somenteAtivas = somenteAtivas;
	}

	/** Retorna a coleção de SelectItem de matrizes curriculares para seleção. 
	 * @return
	 */
	public List<SelectItem> getMatrizesCurriculares() {
		return matrizesCurriculares;
	}

	/** Seta a coleção de SelectItem de matrizes curriculares para seleção.
	 * @param matrizesCurriculares
	 */
	public void setMatrizesCurriculares(List<SelectItem> matrizesCurriculares) {
		this.matrizesCurriculares = matrizesCurriculares;
	}

	public List<SelectItem> getEnfases() {
		return enfases;
	}

	public void setEnfases(List<SelectItem> enfases) {
		this.enfases = enfases;
	}

	/** Indica se deverá cadastrar uma nova habilitação. 
	 * @return
	 */
	public boolean isCadastrarNovaHabilitacao() {
		return cadastrarNovaHabilitacao;
	}

	/** Seta se deverá cadastrar uma nova habilitação. 
	 * @param cadastrarNovaHabilitacao
	 */
	public void setCadastrarNovaHabilitacao(boolean cadastrarNovaHabilitacao) {
		this.cadastrarNovaHabilitacao = cadastrarNovaHabilitacao;
	}

	public Boolean getFiltroCurso() {
		return filtroCurso;
	}

	public void setFiltroCurso(Boolean filtroCurso) {
		this.filtroCurso = filtroCurso;
	}

}