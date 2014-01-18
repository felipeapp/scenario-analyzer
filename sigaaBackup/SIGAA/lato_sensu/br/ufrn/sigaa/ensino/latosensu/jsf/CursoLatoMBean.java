package br.ufrn.sigaa.ensino.latosensu.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.CargoAcademico;
import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.gru.dominio.ConfiguracaoGRU;
import br.ufrn.comum.gru.dominio.GrupoEmissaoGRU;
import br.ufrn.comum.gru.dominio.TipoArrecadacao;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.ead.PoloCursoDao;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.dao.ensino.DocenteExternoDao;
import br.ufrn.sigaa.arq.dao.ensino.SecretariaUnidadeDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.ComponenteCursoLatoDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CorpoDocenteCursoLatoDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CorpoDocenteDisciplinaLatoDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CursoLatoDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.HistoricoSituacaoDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.UnidadeCursoLatoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ead.dominio.PoloCurso;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.SecretariaUnidade;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.TipoTrabalhoConclusao;
import br.ufrn.sigaa.ensino.latosensu.dominio.ComponenteCursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.CorpoDocenteCursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.CorpoDocenteDisciplinaLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.FormaAvaliacao;
import br.ufrn.sigaa.ensino.latosensu.dominio.FormaAvaliacaoProposta;
import br.ufrn.sigaa.ensino.latosensu.dominio.FormaSelecao;
import br.ufrn.sigaa.ensino.latosensu.dominio.FormaSelecaoProposta;
import br.ufrn.sigaa.ensino.latosensu.dominio.HistoricoSituacao;
import br.ufrn.sigaa.ensino.latosensu.dominio.ParametrosPropostaCursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.PropostaCursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta;
import br.ufrn.sigaa.ensino.latosensu.dominio.TipoCursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.TipoPassoPropostaLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.UnidadeCursoLato;
import br.ufrn.sigaa.ensino.latosensu.negocio.CursoLatoValidator;
import br.ufrn.sigaa.ensino.latosensu.relatorios.LinhaComponenteCursoLato;
import br.ufrn.sigaa.ensino.latosensu.relatorios.LinhaConsultaCursoGeral;
import br.ufrn.sigaa.ensino.negocio.dominio.ComponenteCurricularMov;
import br.ufrn.sigaa.ensino_rede.dominio.DadosCursoRede;
import br.ufrn.sigaa.jsf.UnidadeMBean;
import br.ufrn.sigaa.parametros.dominio.ParametrosLatoSensu;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.pessoa.dominio.TipoDocenteExterno;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * Managed Bean de Curso Lato-Sensu, que realiza as seguintes operações cadastro, atualização, 
 * remoção de um Curso.
 * 
 * @author Jean Guerethes
 */
@Component @Scope("session")
public class CursoLatoMBean extends AbstractControllerAtividades<CursoLato> {

	/** Constante de visualização da proposta submetidas */
	private final static String JSP_PROPOSTAS_SUBMETIDAS = "/lato/proposta_curso/propostas_submetidas.jsp";
	/** Constante de visualização dos dados gerais */
	private final static String JSP_DADOS_GERAIS = "/lato/proposta_curso/dados_gerais.jsp";
	/** Constante de visualização dos objetivos do curso */
	private final static String JSP_OBJETIVO_CURSO = "/lato/proposta_curso/objetivo_curso.jsp";
	/** Constante de visualização da coordenação do curso */
	private final static String JSP_COORDENACAO_CURSO = "/lato/proposta_curso/coordenacao_curso.jsp";
	/** Constante de visualização da seleção da avliação */
	private final static String JSP_SELECAO_AVALIACAO = "/lato/proposta_curso/selecao.jsp";
	/** Constante de visualização dos docentes do curso */
	private final static String JSP_DOCENTES_CURSO = "/lato/proposta_curso/docentes.jsp";
	/** Constante de visualização das disciplinas do curso */
	private final static String JSP_DISCIPLINAS_CURSO = "/lato/proposta_curso/disciplinas.jsp";
	/** Constante de visualização das propostas do curso */
	private final static String JSP_PROPOSTAS_CURSO = "/lato/proposta_curso/propostas_submetidas.jsp";
	/** Constante de visualização da visualização da proposta */
	private final static String JSP_VIEW = "/lato/proposta_curso/view.jsp";
	/** Constante de visualização da visualização da proposta */
	private final static String JSP_VIEW_DESPACHO = "/lato/proposta_curso/despacho.jsp";

	/** Lista com os cursos Lato Sensu encontrados */
	private Collection<CursoLato> cursosLato = new ArrayList<CursoLato>();
	/** Serve para carregar o combo das especialidades da proposta */
	protected Collection<SelectItem> propostaEspecialidade = new ArrayList<SelectItem>();
	/** Serve para carregar as especialidades */
	private Collection<SelectItem> especialidades = new ArrayList<SelectItem>();
	/** Informação referente o andamento do Curso Lato Sensu */
	private Boolean andamento = false;
	/** Informação referente o nível do Curso Lato Sensu */
	private Boolean tecnico = false;
	/** Informação referente a origem do Curso Lato Sensu */
	private Boolean estrangeiro = false;
	/** Armazena o público Alvo do Curso Lato Sensu */
	private String[] tiposPublicoAlvo;
	/** Tipo de avaliação se e nota ou conceito */
	private boolean nota = false;
	/** Armazena o corpo docente do Curso Lato */
	private CorpoDocenteCursoLato corpoDocenteCursoLato = new CorpoDocenteCursoLato(); 
	/** Arquivo para complementar a proposta */	
	private UploadedFile arquivoProposta;
	/** Carga horária Aula da disciplina */
	private Integer chAula;
	/** Carga horária Aula da Laboratório */
	private Integer chLaboratorio;
	/** Carga horária Aula da Estágio */
	private Integer chEstagio;
	/** Descrição da Disciplina Antiga */
	private String descricaoDisciplina;
	/** Id Componente Curricular Disciplina Antiga*/
	private int idComponente;
	/** Indica se a proposta é nova ou se a mesma é báseada em outra. */
	boolean nova = true;
	/** Grupos de emissão de GRUs. */
	private Collection<SelectItem> gruposEmissaoGRU;
	/** Indica se o curso terá pagamento de taxa de inscrição via GRU. */
	private boolean possuiGRUInscricao;
	/** Indica se o curso terá pagamento de mensalidades via GRU. */
	private boolean possuiGRUMensalidade;
	
	/**
	 * Constructor default
	 */
	public CursoLatoMBean() throws DAOException {
		obj = new CursoLato();
		clear();
	}

	/**
	 * Popula os atributos de sessão para serem usados em todo o caso de uso.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 */
	public String popularDadosGerais() throws Exception {

		if (getParameter("id") == null && getParameter("renovar") == null)
			clear();

		UnidadeDao dao = getDAO(UnidadeDao.class);

		AreaConhecimentoCnpqDao daoAreaConhecimento = getDAO(AreaConhecimentoCnpqDao.class);
		try {
			obj.getAreaConhecimentoCnpq().setGrandeArea(
					(AreaConhecimentoCnpq) daoAreaConhecimento
							.findGrandeAreasConhecimentoCnpq());
			obj.setTipoCursoLato((TipoCursoLato) dao
					.findAll(TipoCursoLato.class));
			obj.setModalidadeEducacao((ModalidadeEducacao) dao
					.findAll(ModalidadeEducacao.class));
			obj.setTipoTrabalhoConclusao((TipoTrabalhoConclusao) dao
					.findAll(TipoTrabalhoConclusao.class));
			obj.setAreaConhecimentoCnpq((AreaConhecimentoCnpq) dao.findAll(
					AreaConhecimentoCnpq.class, "nome", "asc"));
			obj.setMunicipio((Municipio) dao.findByExactField(Municipio.class,
					"unidadeFederativa.sigla", "RN"));

		} finally {
			dao.close();
		}
		prepareMovimento(SigaaListaComando.PERSISTIR_CURSO_LATO);

		return JSP_PROPOSTAS_SUBMETIDAS;
	}

	/**
	 * Realiza uma busca do último Despacho encontrado para a proposta de Curso Lato Sensu.
	 *
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>JSP: /SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/propostas_submetidas.jsp</li>
	 * </ul>
	 */
	public String viewDespacho(){
		HistoricoSituacaoDao histDao = getDAO(HistoricoSituacaoDao.class);
		try {
			setId();
			setObj(histDao.findByPrimaryKey(obj.getId(), CursoLato.class));
			obj.getPropostaCurso().setHistorico(histDao.findLastHistorico(obj.getPropostaCurso().getId()));			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			histDao.close();
		}
		return forward(JSP_VIEW_DESPACHO);
	}
	
	/**
	 * Carrega todos os objetos necessários para alteração ou remoção. <br>
	 * <br>
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/propostas_submetidas.jsp
	 * 
	 */
	public String carregaObject() throws ArqException {
		setId();
		setObj(getGenericDAO().findByPrimaryKey(obj.getId() == 0 ? getCursoAtualCoordenacao().getId() : obj.getId(), CursoLato.class));
	
		if (obj.getPropostaCurso().getSituacaoProposta().getId() == SituacaoProposta.EXCLUIDA){ 
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return null;
		}

		PoloCursoDao poloCursoDao = getDAO(PoloCursoDao.class);
		try {
			obj.getPolosCursos().addAll(poloCursoDao.findAllCursoPolo(obj));	
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			poloCursoDao.close();	
		}

		UnidadeCursoLatoDao undCursoLatoDao = getDAO(UnidadeCursoLatoDao.class);
		try {
			obj.getUnidadesCursoLato().addAll(undCursoLatoDao.findAllUnidadeCursoLato(obj));	
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			undCursoLatoDao.close();	
		}
		
		Collection<CoordenacaoCurso> corpoDocente = getGenericDAO().findByExactField(CoordenacaoCurso.class, 
				new String[] { "curso", "ativo" }, new Object[] { obj.getId(), true });
		for (CoordenacaoCurso coordenacaoCurso : corpoDocente) {
			if (coordenacaoCurso.getCargoAcademico().getId() == CargoAcademico.COORDENACAO)
				obj.setCoordenador(coordenacaoCurso);
			if (coordenacaoCurso.getCargoAcademico().getId() == CargoAcademico.VICE_COORDENACAO)
				obj.setViceCoordenador(coordenacaoCurso);
		}
		carregarSecretarioAtual();
		obj.setTipoPassoPropostaLato(TipoPassoPropostaLato.TELA_DADOS_GERAIS);
		
		carregarCombos();
		if (obj != null) {
			prepareMovimento(SigaaListaComando.ALTERAR_CURSO_LATO);
			clear();
				
			Double vagasSerInter = ((ParametrosPropostaCursoLato) getGenericDAO().findLast(ParametrosPropostaCursoLato.class))
			.getPorcentagemVagasServidores(); 
			if (  vagasSerInter > 0 ) {
				obj.setNumeroVagasServidores((int) ( obj.getNumeroVagas() / 
						vagasSerInter));
			}
			setOperacaoAtiva(SigaaListaComando.ALTERAR_CURSO_LATO.getId());
			return forward(JSP_DADOS_GERAIS);	
		}
		setOperacaoAtiva(SigaaListaComando.ALTERAR_CURSO_LATO.getId());
		return getCurrentURL();
	}

	/**
	 * Carrega os combos das áreas de conhecimento CNPQ
	 * 
	 * @throws DAOException
	 */
	private void carregarCombos() throws DAOException {
		if (obj.getGrandeAreaConhecimentoCnpq() != null) {
			AreaConhecimentoCnpq grandeArea = obj.getGrandeAreaConhecimentoCnpq();
			AreaConhecimentoCnpq area = obj.getAreaConhecimentoCnpq();
			AreaConhecimentoCnpq subArea = obj.getSubAreaConhecimentoCnpq();
			AreaConhecimentoCnpq especialidade = obj.getEspecialidadeAreaConhecimentoCnpq();
			
			if ( ValidatorUtil.isNotEmpty(grandeArea) ) 
				changeArea( grandeArea.getId() );
			else
				grandeArea = new AreaConhecimentoCnpq();
			
			if ( ValidatorUtil.isNotEmpty( area ) ) {
				obj.setAreaConhecimentoCnpq(area);
				changeSubArea( area.getId() );
			} else 
				area = new AreaConhecimentoCnpq();
			
			if ( ValidatorUtil.isNotEmpty( subArea ) ) {
				obj.setSubAreaConhecimentoCnpq(subArea);
				changeEspecialidade( subArea.getId() );
			} else
				subArea = new AreaConhecimentoCnpq();
			
			if ( ValidatorUtil.isEmpty(especialidade) )
				especialidade = new AreaConhecimentoCnpq();
			
			obj.setGrandeAreaConhecimentoCnpq(grandeArea);
			obj.setAreaConhecimentoCnpq(area);
			obj.setSubAreaConhecimentoCnpq(subArea);
			obj.setEspecialidadeAreaConhecimentoCnpq(especialidade);
		}
	}

	/** Responsável por carregar a Área de conhecimento do Curso Lato Sensu 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 */
	public void changeArea(int id) throws DAOException {
		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		if (id != 0) {
			AreaConhecimentoCnpq grandeArea = new AreaConhecimentoCnpq(id) ;
			subArea = toSelectItems(dao.findAreas(grandeArea), "id", "nome");
		}
	}
	
	/**
	 * Caso a prosposta já possua um secretário esse método é responsável por carregar todas as suas informações
	 * 
	 * @return
	 * @throws DAOException
	 */
	private void carregarSecretarioAtual() throws DAOException{
		SecretariaUnidadeDao secretariaDao = getDAO(SecretariaUnidadeDao.class);
		Collection<SecretariaUnidade> secretariosCurso = secretariaDao.findByCurso(obj.getId());
		for (SecretariaUnidade secretariaUnidade : secretariosCurso) {
			if (secretariaUnidade.isAtivo()) {
				obj.setSecretario( new SecretariaUnidade() );
				obj.setSecretario( secretariaUnidade );
				obj.getSecretario().getUsuario();
				obj.getSecretario().setUsuario(getGenericDAO().findAndFetch(secretariaUnidade.getUsuario().getId(), Usuario.class,"pessoaSigaa"));
				obj.getSecretario().getUsuario().setPessoa(getGenericDAO().findByPrimaryKey(obj.getSecretario().getUsuario().getPessoa().getId(), Pessoa.class));
			}
		}
	}

	/**
	 * Caso o Curso seja do tipo A distância é permitido ao usuário cadastrar o polo no qual o mesmo faz parte.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/dados_gerais.jsp
	 * 
	 * @throws DAOException
	 */
	public void cadastrarPolos() throws DAOException {
		if (PoloCurso.compateTo(obj.getPolosCursos(), obj.getPoloCurso().getPolo().getId())) {
			addMensagemErro("Pólo: Pólo já Cadastrado.");
		} else if (obj.getPoloCurso().getPolo().getId() == 0) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Pólo");
		} else {
			getGenericDAO().clearSession();
			obj.getPoloCurso().setPolo(getGenericDAO().findAndFetch(obj.getPoloCurso().getPolo().getId(), Polo.class, "cidade"));
			obj.getPolosCursos().add(obj.getPoloCurso());
			obj.setPoloCurso(new PoloCurso());
			obj.getPoloCurso().setCurso(new Curso());
			obj.getPoloCurso().setPolo(new Polo());
		}
	}

	/**
	 * Remove um polo cadastrado para uma Proposta.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/dados_gerais.jsp
	 * @throws ArqException 
	 */
	public void removerPolo() throws ArqException{
		int idPolo = getParameterInt("id");
		for (PoloCurso poloCurso : obj.getPolosCursos()) {
			if (idPolo == poloCurso.getPolo().getId()) {
				obj.getPolosCursos().remove(poloCurso);
				break;
			}
		}
		if ( obj.getPolosCursos().size() == 0 ) 
			obj.setPolosCursos( new ArrayList<PoloCurso>());
	}

	/**
	 * Cadas unidade envolvida.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/dados_gerais.jsp
	 * 
	 * @throws DAOException
	 */
	public void cadastrarUnidadeEnvolvida() throws DAOException {
		if (obj.getUnidadeCursoLato().getUnidade().getId() != 0) {
			getGenericDAO().clearSession();
			obj.getUnidadeCursoLato().setUnidade(getGenericDAO().findByPrimaryKey(obj.getUnidadeCursoLato().getUnidade().getId(), Unidade.class));
			obj.getUnidadeCursoLato().setCurso(obj);
			obj.getUnidadesCursoLato().add(obj.getUnidadeCursoLato());
			obj.setUnidadeCursoLato(new UnidadeCursoLato());
		}else
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Outras Unidades Envolvidas");
	}

	/**
	 * Remove uma unidade envolvida.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/dados_gerais.jsp
	 * @throws ArqException 
	 */
	public void removerUnidadeEnvolvida() throws ArqException{
		int idUnidade = getParameterInt("id");
			for (UnidadeCursoLato undCursoLato : obj.getUnidadesCursoLato()) {
				if (idUnidade == undCursoLato.getUnidade().getId()) {
					obj.getUnidadesCursoLato().remove(undCursoLato);
					break;
				}
			}
		if (obj.getUnidadesCursoLato().size() == 0 && obj.getId() > 0) 
			recarregarProposta();
	}
	
	/**
	 * Recarregar Proposta Curso Lato
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String recarregarProposta() throws ArqException{
		int id = (obj.getId() != 0 ? obj.getId() : getParameterInt("id"));
		setObj(getGenericDAO().findAndFetch(id, CursoLato.class, "cursosServidores"));
		Collection<CoordenacaoCurso> corpoDocente = getGenericDAO().findByExactField(CoordenacaoCurso.class, "curso", obj.getId());
		for (CoordenacaoCurso coordenacaoCurso : corpoDocente) {
			if (coordenacaoCurso.getCargoAcademico().getId() == CargoAcademico.COORDENACAO 
					&& coordenacaoCurso.getDataFimMandato().after(new Date()))
				obj.setCoordenador(coordenacaoCurso);
			if (coordenacaoCurso.getCargoAcademico().getId() == CargoAcademico.VICE_COORDENACAO 
					&& coordenacaoCurso.getDataFimMandato().after(new Date()))
				obj.setViceCoordenador(coordenacaoCurso);
		}
		carregarSecretarioAtual();
		if (obj != null) {
			prepareMovimento(SigaaListaComando.ALTERAR_CURSO_LATO);
			clear();
			setOperacaoAtiva(SigaaListaComando.ALTERAR_CURSO_LATO.getId());
			return forward(JSP_DADOS_GERAIS);	
		}
		setOperacaoAtiva(SigaaListaComando.ALTERAR_CURSO_LATO.getId());
		return getCurrentURL();
	}

	/**
	 * Responsável por seta as informações do Servidor no MBean para que seja exibido na view. 
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/coordenacao_curso.jsp</li>
	 * </ul>
	 * 
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public String carregaInformacao(ActionEvent event) throws DAOException {
		Boolean funcao = Boolean.parseBoolean((String) event.getComponent().getAttributes().get("funcaoCoordenador"));
		if (funcao) {
			br.ufrn.rh.dominio.Servidor coord = (br.ufrn.rh.dominio.Servidor) event.getComponent().getAttributes().get("servCoord");
			obj.getCoordenador().setServidor(
					getGenericDAO().findByPrimaryKey(coord.getId(), Servidor.class));
			
			obj.getCoordenador().setEmailContato(obj.getCoordenador().getServidor().getPessoa().getEmail());
			obj.getCoordenador().setTelefoneContato1(obj.getCoordenador().getServidor().getPessoa().getTelefone());
		} else {
			br.ufrn.rh.dominio.Servidor viceCoord = (br.ufrn.rh.dominio.Servidor) event.getComponent().getAttributes().get("servViceCoord");
			obj.getViceCoordenador().setServidor(
					getGenericDAO().findByPrimaryKey(viceCoord.getId(), Servidor.class));
			
			obj.getViceCoordenador().setEmailContato(obj.getViceCoordenador().getServidor().getPessoa().getEmail());
			obj.getViceCoordenador().setTelefoneContato1(obj.getViceCoordenador().getServidor().getPessoa().getTelefone());
		}
		return redirectMesmaPagina();
	}
	
	/** 
	 * Responsável por anexar um arquivo a proposta
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não invocado por JSP</li>
	 * </ul>
	 */
	public String enviarArquivo() throws IOException {
		int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
		EnvioArquivoHelper.inserirArquivo(idArquivo, arquivoProposta.getBytes(), arquivoProposta.getContentType(), arquivoProposta.getName());
		obj.getPropostaCurso().setIdArquivo(idArquivo);
		return null;
	}

	/**
	 * Serve para realizar a validação das informações contida na tela atual.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * @throws DAOException
	 */
	private void validaTela() throws DAOException {
		if (obj.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_DADOS_GERAIS)) { 
			CursoLatoValidator.validaDadosBasicos(obj, erros, getGenericDAO());
			if ( hasOnlyErrors() &&  isEmpty( obj.getAreaConhecimentoCnpq().getEspecialidade() ))
				obj.getAreaConhecimentoCnpq().setEspecialidade( new AreaConhecimentoCnpq() );
		} else if (obj.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_CONFIGURACAO_GRU)) {
			if ((possuiGRUInscricao || possuiGRUMensalidade) && isEmpty(obj.getUnidadeOrcamentaria())) {
				erros.addErro("Para poder gerar uma GRU é necessário definir uma unidade orçamentária no passo anterior.");
			} else {
				getGenericDAO().initialize(obj.getUnidadeOrcamentaria());
				obj.getConfiguracaoGRUInscricao().setUnidade(obj.getUnidadeOrcamentaria());
				obj.getConfiguracaoGRUMensalidade().setUnidade(obj.getUnidadeOrcamentaria());
				if (possuiGRUInscricao) 
					erros.addAll(obj.getConfiguracaoGRUInscricao().validate());
				if (possuiGRUMensalidade) 
					erros.addAll(obj.getConfiguracaoGRUMensalidade().validate());
			}
		} else if (obj.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_COORDENACAO_CURSO)) { 
			CursoLatoValidator.validaCoordenacaoCurso(obj, erros, getGenericDAO());
		} else if (obj.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_OBJETIVO_IMPORTACIA)) {
			CursoLatoValidator.validateObjetivoImportancia(obj, erros);
		} else if (obj.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_CORPO_DOCENTE)) {
			CursoLatoValidator.validacaoCorpoDocente(getDAO(CorpoDocenteCursoLatoDao.class), erros, obj);
		} else if (obj.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_PROCESSO_SELETIVO)) {
			erros = CursoLatoValidator.validateSelecao(obj);
		}
	}
	
	/**
	 * Método responsável pelo cadastro ou atualização de uma proposta.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * @return 
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws SegurancaException 
	 */
	public String submeterDadosGerais() throws SegurancaException, ArqException, NegocioException {
		validaTela();
		if (hasErrors()) 
			return null;
		if (isEmpty(obj.getUnidadeOrcamentaria().getNome()))
			obj.getUnidadeOrcamentaria().setId(0);
		return cadastrar();
	}
	
	/**
	 * Método responsável pelo cadastro ou atualização de uma proposta.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * @return 
	 */
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		validaTela();
		
		if (hasErrors()) 
			return null;
		
		if (!possuiGRUInscricao) 
			obj.setConfiguracaoGRUInscricao(null);
		if (!possuiGRUMensalidade) 
			obj.setConfiguracaoGRUMensalidade(null);
		
		ListaMensagens lista = new ListaMensagens();
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setUsuarioLogado(getUsuarioLogado());
		
		if (obj.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_DADOS_GERAIS) && arquivoProposta != null) {
			try {
				enviarArquivo();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.getId() == 0 && nova) {
			setOperacaoAtiva(SigaaListaComando.PERSISTIR_CURSO_LATO.getId());
			prepareMovimento(SigaaListaComando.PERSISTIR_CURSO_LATO);
			mov.setCodMovimento(SigaaListaComando.PERSISTIR_CURSO_LATO);
		} else if (obj.getId() == 0 && !nova) {
			setOperacaoAtiva(SigaaListaComando.NOVA_PROPOSTA_EXISTENTE.getId());
			prepareMovimento(SigaaListaComando.NOVA_PROPOSTA_EXISTENTE);
			mov.setCodMovimento(SigaaListaComando.NOVA_PROPOSTA_EXISTENTE);
		} else {
			setOperacaoAtiva(SigaaListaComando.ALTERAR_PROPOSTA_CURSO_LATO.getId());
			prepareMovimento(SigaaListaComando.ALTERAR_PROPOSTA_CURSO_LATO);
			mov.setCodMovimento(SigaaListaComando.ALTERAR_PROPOSTA_CURSO_LATO);
		}
			try {
				UFRNUtils.anularAtributosVazios(obj, "unidadeOrcamentaria", "subAreaConhecimentoCnpq", "especialidadeAreaConhecimentoCnpq");
				lista = execute(mov, getCurrentRequest());
			       if(lista != null){
			    	   addMensagens(lista);
			    	   return null;
			       }
			} catch (Exception e) {
				notifyError(e);
				addMensagemErroPadrao();
				e.printStackTrace();
			}
			chAula=0; 
			chEstagio=0;
			chLaboratorio=0;
		return proximaTela(obj);
	}

	/**
	 * Método que direciona o usuário para a próxima tela.  
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	private String proximaTela(CursoLato curso) throws DAOException {
		setOperacaoAtiva(SigaaListaComando.ALTERAR_PROPOSTA_CURSO_LATO.getId());
		if (curso.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_DADOS_GERAIS) && !hasErrors()) {
			if (obj.getCoordenador().getDataInicioMandato() == null)
				obj.getCoordenador().setDataInicioMandato(obj.getDataInicio());
			if (obj.getCoordenador().getDataFimMandato() == null)
				obj.getCoordenador().setDataFimMandato(obj.getDataFim());
			if (obj.getViceCoordenador().getDataInicioMandato() == null)
				obj.getViceCoordenador().setDataInicioMandato(obj.getDataInicio());
			if (obj.getViceCoordenador().getDataFimMandato() == null)
				obj.getViceCoordenador().setDataFimMandato(obj.getDataFim());
			if (!isLatoSensu()) { 
				obj.setTipoPassoPropostaLato(TipoPassoPropostaLato.TELA_COORDENACAO_CURSO);
				return forward(JSP_COORDENACAO_CURSO);
			} else {
				preparaFormConfigGRU();
				return forward("/lato/proposta_curso/configuracao_gru.jsp");
			}
		}	
		if (curso.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_CONFIGURACAO_GRU) && !hasErrors()) {
			obj.setTipoPassoPropostaLato(TipoPassoPropostaLato.TELA_COORDENACAO_CURSO);
			return forward(JSP_COORDENACAO_CURSO);
		}
		if (curso.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_COORDENACAO_CURSO) && !hasErrors()) {
			carregarSecretarioAtual();
			obj.setTipoPassoPropostaLato(TipoPassoPropostaLato.TELA_OBJETIVO_IMPORTACIA);
			return forward(JSP_OBJETIVO_CURSO);
		}
		if (curso.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_OBJETIVO_IMPORTACIA) && !hasErrors()) {
			obj.setTipoPassoPropostaLato(TipoPassoPropostaLato.TELA_PROCESSO_SELETIVO);
			return forward(JSP_SELECAO_AVALIACAO);
		}
		if (curso.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_PROCESSO_SELETIVO) && !hasErrors()) {
			corpoDocenteCursoLato = new CorpoDocenteCursoLato();
			corpoDocenteCursoLato.getDocenteExterno().getPessoa().setSexo(PessoaGeral.SEXO_MASCULINO);
			obj.getCorpoDocenteProposta().clear();
			obj.setTipoPassoPropostaLato(TipoPassoPropostaLato.TELA_CORPO_DOCENTE);
			return forward(JSP_DOCENTES_CURSO);
		}
		if (obj.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_CORPO_DOCENTE)) {
			obj.setDocentes( new ArrayList<SelectItem>() );
			for (CorpoDocenteCursoLato corpoDocente : getallCorpoDocenteProposta()) {
				if (corpoDocente.getServidor() != null) 
					obj.getDocentes().add(new SelectItem(corpoDocente.getId(), corpoDocente.getServidor().getPessoa().getNome()));
				else
					obj.getDocentes().add(new SelectItem(corpoDocente.getId(), corpoDocente.getDocenteExterno().getPessoa().getNome()));
			}
			obj.setTipoPassoPropostaLato(TipoPassoPropostaLato.TELA_DISCIPLINA_CURSO_LATO);
			corpoDocenteCursoLato = new CorpoDocenteCursoLato();
			if (obj.getCursosServidores() != null)
				obj.getCursosServidores().clear();
			else
				obj.setCursosServidores( new HashSet<CorpoDocenteCursoLato>(0) );
			
			obj.getEquipesLato().clear();
			obj.setEquipeLato( new CorpoDocenteDisciplinaLato() );
			obj.setDisciplina(new ComponenteCurricular());
			obj.getDisciplina().setDetalhes(new ComponenteDetalhes());
			idComponente = 0;
			descricaoDisciplina = "";
			return forward(JSP_DISCIPLINAS_CURSO);
		}
		
		return null;
	}
	
	/**
	 * Direcionar o usuário para tela inicial do cadastro de uma nova proposta de Curso Lato Sensu.
	 *
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/view.jsp</li>
	 * </ul>
	 */
	public String dadosGerais(){
		obj.setTipoPassoPropostaLato(TipoPassoPropostaLato.TELA_DADOS_GERAIS);
		if (ValidatorUtil.isEmpty(obj.getUnidadeOrcamentaria()))
			obj.setUnidadeOrcamentaria(new Unidade());
		if (obj.getAreaConhecimentoCnpq() == null)
			obj.setAreaConhecimentoCnpq(new AreaConhecimentoCnpq());
		if( obj.getAreaConhecimentoCnpq().getEspecialidade() == null)
			obj.getAreaConhecimentoCnpq().setEspecialidade(new AreaConhecimentoCnpq());
		if (obj.getSubAreaConhecimentoCnpq() == null ) 
			obj.setSubAreaConhecimentoCnpq(new AreaConhecimentoCnpq());
		if (obj.getEspecialidadeAreaConhecimentoCnpq() == null)
			obj.setEspecialidadeAreaConhecimentoCnpq(new AreaConhecimentoCnpq());
		
		return forward(JSP_DADOS_GERAIS);
	}
	
	/**
	 * Volta o usuário para a tela anterior.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
 	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/coordenacao_curso.jsp</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/disciplinas.jsp</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/docentes.jsp</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/objetivo_curso.jsp</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/selecao.jsp</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/view.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String telaAnterior() throws DAOException {
		setOperacaoAtiva(SigaaListaComando.ALTERAR_PROPOSTA_CURSO_LATO.getId());
		if (obj.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_CONFIGURACAO_GRU) && !hasErrors()) {
			obj.setTipoPassoPropostaLato(TipoPassoPropostaLato.TELA_DADOS_GERAIS);
			if (ValidatorUtil.isEmpty(obj.getUnidadeOrcamentaria()))
				obj.setUnidadeOrcamentaria(new Unidade());
			if (ValidatorUtil.isEmpty(obj.getEspecialidadeAreaConhecimentoCnpq()))
				obj.setEspecialidadeAreaConhecimentoCnpq(new AreaConhecimentoCnpq());
			if (ValidatorUtil.isEmpty(obj.getSubAreaConhecimentoCnpq()))
				obj.setSubAreaConhecimentoCnpq(new AreaConhecimentoCnpq());
			if (ValidatorUtil.isEmpty(obj.getAreaConhecimentoCnpq().getEspecialidade()))
				obj.getAreaConhecimentoCnpq().setEspecialidade(new AreaConhecimentoCnpq());

			return forward(JSP_DADOS_GERAIS);
		}
		if (obj.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_COORDENACAO_CURSO) && !hasErrors()) {
			if (!isLatoSensu()) {
				obj.setTipoPassoPropostaLato(TipoPassoPropostaLato.TELA_DADOS_GERAIS);
				if (ValidatorUtil.isEmpty(obj.getUnidadeOrcamentaria()))
					obj.setUnidadeOrcamentaria(new Unidade());
				if (ValidatorUtil.isEmpty(obj.getEspecialidadeAreaConhecimentoCnpq()))
					obj.setEspecialidadeAreaConhecimentoCnpq(new AreaConhecimentoCnpq());
				if (ValidatorUtil.isEmpty(obj.getSubAreaConhecimentoCnpq()))
					obj.setSubAreaConhecimentoCnpq(new AreaConhecimentoCnpq());
				if (ValidatorUtil.isEmpty(obj.getAreaConhecimentoCnpq().getEspecialidade()))
					obj.getAreaConhecimentoCnpq().setEspecialidade(new AreaConhecimentoCnpq());
	
				return forward(JSP_DADOS_GERAIS);
			} else {
				preparaFormConfigGRU();
				return forward("/lato/proposta_curso/configuracao_gru.jsp");
			}
		}
		if (obj.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_CONFIGURACAO_GRU) && !hasErrors()) {
			obj.setTipoPassoPropostaLato(TipoPassoPropostaLato.TELA_DADOS_GERAIS);
			if (ValidatorUtil.isEmpty(obj.getUnidadeOrcamentaria()))
				obj.setUnidadeOrcamentaria(new Unidade());
			if (ValidatorUtil.isEmpty(obj.getEspecialidadeAreaConhecimentoCnpq()))
				obj.setEspecialidadeAreaConhecimentoCnpq(new AreaConhecimentoCnpq());
			if (ValidatorUtil.isEmpty(obj.getSubAreaConhecimentoCnpq()))
				obj.setSubAreaConhecimentoCnpq(new AreaConhecimentoCnpq());
			if (ValidatorUtil.isEmpty(obj.getAreaConhecimentoCnpq().getEspecialidade()))
				obj.getAreaConhecimentoCnpq().setEspecialidade(new AreaConhecimentoCnpq());

			return forward(JSP_DADOS_GERAIS);
		}
		if (obj.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_OBJETIVO_IMPORTACIA) && !hasErrors()) {
			obj.setTipoPassoPropostaLato(TipoPassoPropostaLato.TELA_COORDENACAO_CURSO);
			return forward(JSP_COORDENACAO_CURSO);
		}
		if (obj.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_PROCESSO_SELETIVO) && !hasErrors()) {
			obj.setTipoPassoPropostaLato(TipoPassoPropostaLato.TELA_OBJETIVO_IMPORTACIA);
			return forward(JSP_OBJETIVO_CURSO);
		}
		if (obj.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_CORPO_DOCENTE) && !hasErrors()) {
			obj.setTipoPassoPropostaLato(TipoPassoPropostaLato.TELA_PROCESSO_SELETIVO);
			return forward(JSP_SELECAO_AVALIACAO);
		}
		if (obj.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_DISCIPLINA_CURSO_LATO) && !hasErrors()) {
			obj.setTipoPassoPropostaLato(TipoPassoPropostaLato.TELA_CORPO_DOCENTE);
			return forward(JSP_DOCENTES_CURSO);
		}
		if (obj.getTipoPassoPropostaLato().equals(TipoPassoPropostaLato.TELA_RESUMO_PROPOSTA) && !hasErrors()) {
			obj.setTipoPassoPropostaLato(TipoPassoPropostaLato.TELA_DISCIPLINA_CURSO_LATO);
			return forward(JSP_DISCIPLINAS_CURSO);
		}
		return null;
	}

	/** Prepara uma configuração de GRU
	 * @throws DAOException
	 */
	private void preparaFormConfigGRU() throws DAOException {
		// a configuração da GRU é disponível apenas para o usuário do portal lato sensu
		GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
		try {
			if (obj.getConfiguracaoGRUInscricao() == null) {
				if (obj.getIdConfiguracaoGRUInscricao() != null) {
					ConfiguracaoGRU config = dao.findByPrimaryKey(obj.getIdConfiguracaoGRUInscricao(), ConfiguracaoGRU.class);
					obj.setConfiguracaoGRUInscricao(config);
					this.possuiGRUInscricao = true;
				} else {
					obj.setConfiguracaoGRUInscricao(new ConfiguracaoGRU());
					TipoArrecadacao tipoArrecadacao = dao.findByPrimaryKey(TipoArrecadacao.PROCESSO_SELETIVO_LATO_SENSU, TipoArrecadacao.class);
					obj.getConfiguracaoGRUInscricao().setTipoArrecadacao(tipoArrecadacao);
				}
			}
			if (obj.getConfiguracaoGRUMensalidade() == null) {
				if (obj.getIdConfiguracaoGRUMensalidade() != null) {
					ConfiguracaoGRU config = dao.findByPrimaryKey(obj.getIdConfiguracaoGRUMensalidade(), ConfiguracaoGRU.class);
					obj.setConfiguracaoGRUMensalidade(config);
					this.possuiGRUMensalidade = true;
				} else {
					obj.setConfiguracaoGRUMensalidade(new ConfiguracaoGRU());
					TipoArrecadacao tipoArrecadacao = dao.findByPrimaryKey(TipoArrecadacao.MENSALIDADE_CURSO_LATO, TipoArrecadacao.class);
					obj.getConfiguracaoGRUMensalidade().setTipoArrecadacao(tipoArrecadacao);
				}
			}
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			dao.close();
		}
		obj.setTipoPassoPropostaLato(TipoPassoPropostaLato.TELA_CONFIGURACAO_GRU);
		gruposEmissaoGRU = null;
	}
	
	/**
	 * Serve para cancelar a operação que está realizando
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/alterar_situacao.jsp</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/busca.jsp</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/coordenacao_curso.jsp</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/dados_gerais.jsp</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/disciplinas.jsp</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/docentes.jsp</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/objetivo_curso.jsp</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/selecao.jsp</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/view.jsp</li>   
	 * </ul>
	 * 
	 */
	@Override
	public String cancelar() {
		if (isPortalDocente()){ 
			cursosLato = new ArrayList<CursoLato>();
			try {
				getAllCursoLatoDocente();
			} catch (DAOException e) {
				e.printStackTrace();
			}
			return forward(JSP_PROPOSTAS_SUBMETIDAS);
		}
		else if (isPortalLatoSensu())
			return forward(BuscaCursoLatoMBean.getJspBuscaPropostas());
		else
			return super.cancelar();
	}
	
	/**
	 * Adiciona uma Coordenador, sendo esse um servidor.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 */
	public void adicionarServidorCoordenacao(){
		if (corpoDocenteCursoLato.getServidor().getId() != 0) {
			CorpoDocenteCursoLatoDao dao = getDAO(CorpoDocenteCursoLatoDao.class);
			CorpoDocenteDisciplinaLatoDao corpoDocenteDisciplinaLatoDao = getDAO(CorpoDocenteDisciplinaLatoDao.class); 
			CorpoDocenteCursoLato corpoDocenteOld = null;
			try {
				boolean temDisciplina = corpoDocenteDisciplinaLatoDao.validarDocenteMinistraDisciplina(obj.getEquipeLato().getDocente(), obj.getPropostaCurso());
				boolean jaCadastrado = dao.validarDocenteDisciplina(corpoDocenteCursoLato.getServidor(), obj);
				if (!temDisciplina) {
					corpoDocenteOld = dao.findByDocenteCurso(obj, obj.getEquipeLato().getDocente().getId());
					if (corpoDocenteOld != null	
							&& corpoDocenteOld.getServidor().getId() != corpoDocenteCursoLato.getServidor().getId() && !temDisciplina) {
						getGenericDAO().detach(corpoDocenteOld);
						getGenericDAO().updateField(CorpoDocenteCursoLato.class, corpoDocenteOld.getId(), "servidor", corpoDocenteCursoLato.getServidor());
						obj.setEquipeLato( new CorpoDocenteDisciplinaLato() );
					} else if (!jaCadastrado) {
						getGenericDAO().create(corpoDocenteCursoLato);
					}	
				}else if (!jaCadastrado) 
					getGenericDAO().create(corpoDocenteCursoLato);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
		}
	}
	
	/**
	 * Método responsável pela adição de um docente interno a proposta.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/docentes.jsp
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws SegurancaException 
	 */ 
	public void adicionarServidor() throws SegurancaException, ArqException, NegocioException{
		obj.setAba("interno");
		if (corpoDocenteCursoLato.getServidor().getId() != 0) {
			CorpoDocenteCursoLatoDao dao = getDAO(CorpoDocenteCursoLatoDao.class);
			Collection<CorpoDocenteCursoLato> corpoDocenteOld = null;
			try {
				corpoDocenteOld = dao.findByAllDocenteCurso(obj);
				corpoDocenteCursoLato.setDocenteExterno(null);
				corpoDocenteCursoLato.setServidor(
						dao.findByPrimaryKey(corpoDocenteCursoLato.getServidor().getId(), Servidor.class) );
				erros.addAll(CursoLatoValidator.validateAddServidor(corpoDocenteOld, corpoDocenteCursoLato, obj, dao, estrangeiro));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
				obj.getCorpoDocenteProposta().clear();
			}
		}else
			addMensagem(MensagensArquitetura.AUTOCOMPLETE_OBRIGATORIO, "Docente");
		
		corpoDocenteCursoLato = new CorpoDocenteCursoLato();
		corpoDocenteCursoLato.getDocenteExterno().getPessoa().setSexo(PessoaGeral.SEXO_MASCULINO);
		
		if (hasOnlyErrors()) 
			forward(JSP_DOCENTES_CURSO);
		else
			redirectMesmaPagina();
		
	}
	
	/**
	 * Carrega os dados da pessoa baseado no CPF informando.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/docentes.jsp</li>
	 * </ul>
	 */
	public void carregaDocenteExterno() throws DAOException {
		obj.setAba("externo");
		PessoaDao pessoaDao = getDAO(PessoaDao.class);
		try {
			if ( ( corpoDocenteCursoLato.getDocenteExterno().getPessoa().getCpf_cnpj() != null && !isEstrangeiro() )  
					|| !corpoDocenteCursoLato.getDocenteExterno().getPessoa().getPassaporte().isEmpty() ) {
				
				Pessoa p = null;
				Long cpf = null;
				String passaporte = null;
				if ( corpoDocenteCursoLato.getDocenteExterno().getPessoa().getCpf_cnpj() != null && !isEstrangeiro() )
					p = pessoaDao.findByCpf(corpoDocenteCursoLato.getDocenteExterno().getPessoa().getCpf_cnpj());
				else 
					p = pessoaDao.findByPassaporte(corpoDocenteCursoLato.getDocenteExterno().getPessoa().getPassaporte());
			
				if (p != null){ 
					corpoDocenteCursoLato.getDocenteExterno().setPessoa(p);
					obj.setCpfEncontrado(true);
				} else{
					if ( corpoDocenteCursoLato.getDocenteExterno().getPessoa().getCpf_cnpj() != null && !isEstrangeiro() )
						cpf = corpoDocenteCursoLato.getDocenteExterno().getPessoa().getCpf_cnpj();
					else
						passaporte = corpoDocenteCursoLato.getDocenteExterno().getPessoa().getPassaporte();
					
					corpoDocenteCursoLato = new CorpoDocenteCursoLato();
					corpoDocenteCursoLato.getDocenteExterno().getPessoa().setSexo(PessoaGeral.SEXO_MASCULINO);
					if ( corpoDocenteCursoLato.getDocenteExterno().getPessoa() != null && !isEstrangeiro() )
						corpoDocenteCursoLato.getDocenteExterno().getPessoa().setCpf_cnpj(cpf);
					else
						corpoDocenteCursoLato.getDocenteExterno().getPessoa().setPassaporte(passaporte);
					obj.setCpfEncontrado(false);
					redirectMesmaPagina();
				}
			
			} else {
				corpoDocenteCursoLato.getDocenteExterno().setPessoa(new Pessoa());
				corpoDocenteCursoLato.getDocenteExterno().getPessoa().setSexo(PessoaGeral.SEXO_MASCULINO);
				obj.setCpfEncontrado(false);
				redirectMesmaPagina();
			}
		} finally {
			pessoaDao.close();
			obj.setAba("externo");
		}
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
		erros.addAll(CursoLatoValidator.validateDocenteExterno(corpoDocenteCursoLato, estrangeiro));
		obj.setAba("externo");		
		if (!hasOnlyErrors()) {
			PessoaDao pessoaDao = getDAO(PessoaDao.class);
			DocenteExternoDao docExtDao = getDAO(DocenteExternoDao.class); 

			if (!hasOnlyErrors()) {
				MovimentoCadastro movDocExt = new MovimentoCadastro();
				DocenteExterno docExt = null;				
				if ( corpoDocenteCursoLato.getDocenteExterno().getPessoa().getId() > 0 ) {
					corpoDocenteCursoLato.getDocenteExterno().setPessoa( 
							pessoaDao.findAndFetch( corpoDocenteCursoLato.getDocenteExterno().getPessoa().getId(), Pessoa.class, "enderecoContato", "contaBancaria") );
					
					docExt = docExtDao.findByPessoa(corpoDocenteCursoLato.getDocenteExterno().getPessoa(), TipoDocenteExterno.DOCENTE_EXTERNO_LATO_SENSU, obj.getUnidade());
				}
				
				if ( isEmpty(docExt) ) {
					corpoDocenteCursoLato.getDocenteExterno().setPrazoValidade(obj.getDataFim());
					prepareMovimento(SigaaListaComando.CADASTRAR_DOCENTE_EXTERNO);
					movDocExt.setCodMovimento(SigaaListaComando.CADASTRAR_DOCENTE_EXTERNO);
				} else if(docExt != null && docExt.getId() > 0){
					corpoDocenteCursoLato.getDocenteExterno().setId(docExt.getId());
					if(docExt.getPrazoValidade() != null && obj.getDataFim().after(docExt.getPrazoValidade()))
						corpoDocenteCursoLato.getDocenteExterno().setPrazoValidade(obj.getDataFim());
					
					prepareMovimento(SigaaListaComando.ALTERAR_DOCENTE_EXTERNO);
					movDocExt.setCodMovimento(SigaaListaComando.ALTERAR_DOCENTE_EXTERNO);
				}	
				movDocExt.setObjMovimentado(corpoDocenteCursoLato.getDocenteExterno());
				corpoDocenteCursoLato.getDocenteExterno().getTipoDocenteExterno().setId(TipoDocenteExterno.DOCENTE_EXTERNO_LATO_SENSU);
				corpoDocenteCursoLato.setServidor(null);
				corpoDocenteCursoLato.getDocenteExterno().setServidor(null);
				corpoDocenteCursoLato.setCursoLato(obj);
				corpoDocenteCursoLato.getDocenteExterno().setPessoa(corpoDocenteCursoLato.getDocenteExterno().getPessoa());
				try {
					corpoDocenteCursoLato.getDocenteExterno().setUnidade(obj.getUnidade());
					movDocExt.setObjMovimentado(corpoDocenteCursoLato.getDocenteExterno());
					movDocExt.setSistema(getSistema());
					movDocExt.setUsuarioLogado(getUsuarioLogado());
					execute(movDocExt);
					getGenericDAO().create(corpoDocenteCursoLato);
					addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Docente");
					corpoDocenteCursoLato =  new CorpoDocenteCursoLato();
					corpoDocenteCursoLato.getDocenteExterno().getPessoa().setSexo(PessoaGeral.SEXO_MASCULINO);
				} catch (NegocioException e) {
					addMensagens(e.getListaMensagens());
					corpoDocenteCursoLato.getDocenteExterno().getPessoa().setSexo(PessoaGeral.SEXO_MASCULINO);
					return;
				}finally {
					pessoaDao.close();
					docExtDao.close();
					obj.getCorpoDocenteProposta().clear();
				}
			}
		}
		if (!hasOnlyErrors()) {
			corpoDocenteCursoLato = new CorpoDocenteCursoLato();
			corpoDocenteCursoLato.getDocenteExterno().getPessoa().setSexo(PessoaGeral.SEXO_MASCULINO);
			obj.setCpfEncontrado(false);
		}
		
		redirectMesmaPagina();
	}
	
	/**
	 * Serve para mudar o informação do campo cpf para passaporte (caso o usuário não possua cpf).
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/docentes.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 */
	public void changeEstrangeiro(ValueChangeEvent e) {
		estrangeiro = (Boolean) e.getNewValue();
		corpoDocenteCursoLato = new CorpoDocenteCursoLato();
		corpoDocenteCursoLato.getDocenteExterno().getPessoa().setSexo(PessoaGeral.SEXO_MASCULINO);
		obj.setCpfEncontrado(false);
		redirectMesmaPagina();
	}
	
	/**
	 * Valida as informações referente ao dados da nova disciplina
	 *
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return
	 */
	public ListaMensagens validateAddDisciplina(){
		
		ValidatorUtil.validateRequired(obj.getDisciplina().getDetalhes().getNome(), "Nome", erros);
		ValidatorUtil.validateRequired(obj.getDisciplina().getDetalhes().getChAula(), "Carga Horária	Aula", erros);
		ValidatorUtil.validateRequired(obj.getDisciplina().getDetalhes().getChLaboratorio(), "Carga Horária	Laboratório", erros);
		ValidatorUtil.validateRequired(obj.getDisciplina().getDetalhes().getChEstagio(), "Carga Horária	Estágio", erros);
		ValidatorUtil.validateRequired(obj.getDisciplina().getDetalhes().getEmenta(), "Ementa", erros);
		ValidatorUtil.validateRequired(obj.getDisciplina().getBibliografia(), "Bibliografia", erros);
		if (obj.getEquipeLato().getDocente() != null) 
			ValidatorUtil.validateRequiredId(obj.getEquipeLato().getDocente().getId(), "Docente", erros);	
		else
			ValidatorUtil.validateRequiredId(obj.getEquipeLato().getDocenteExterno().getId(), "Docente", erros);
		
		ValidatorUtil.validateRequired(obj.getEquipeLato().getCargaHoraria() == null ? "" : 
				obj.getEquipeLato().getCargaHoraria(), "Carga Horária Dedicada", erros);
		
		return erros;
	}
	
	/**
	 * Serve para associar um docente a uma disciplina.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/disciplinas.jsp 
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String adicionarDocenteDisciplina() throws SegurancaException, ArqException, NegocioException {
		if (corpoDocenteCursoLato.getId() == 0){ 
			addMensagemErro("É necessário informar um docente para ministrar a disciplina.");
			return null;
		}
		
		corpoDocenteCursoLato = getGenericDAO().findByPrimaryKey(corpoDocenteCursoLato.getId(), CorpoDocenteCursoLato.class);
		if (corpoDocenteCursoLato.getServidor() != null){ 
			obj.getEquipeLato().setDocente(corpoDocenteCursoLato.getServidor());
			obj.getEquipeLato().setDocenteExterno(null);
		}else {
			obj.getEquipeLato().setDocenteExterno(corpoDocenteCursoLato.getDocenteExterno());
			obj.getEquipeLato().setDocente(null);
		}
		
		if (obj.getDisciplina().getId() == 0 && idComponente == 0){
			obj.getDisciplina().getDetalhes().setChAula(ValidatorUtil.isEmpty(chAula) ? 0 : chAula);
			obj.getDisciplina().getDetalhes().setChLaboratorio(ValidatorUtil.isEmpty(chLaboratorio) ? 0 : chLaboratorio);
			obj.getDisciplina().getDetalhes().setChEstagio(ValidatorUtil.isEmpty(chEstagio) ? 0 : chEstagio);
			validateAddDisciplina();
		} else if ( obj.getDisciplina().getId() == 0 ) {
			
			if (LinhaComponenteCursoLato.compareTo(getAllDisciplinasCursoLato(), idComponente)){
				idComponente = 0;
				addMensagemErro("Disciplina já presente no corpo do Curso");
				return null;
			}
			
			obj.setDisciplina( getGenericDAO().findByPrimaryKey(idComponente, ComponenteCurricular.class) );
			chAula = obj.getDisciplina().getDetalhes().getChAula();
			chEstagio = obj.getDisciplina().getDetalhes().getChEstagio();
			chLaboratorio = obj.getDisciplina().getDetalhes().getChLaboratorio();
			idComponente = 0;
			descricaoDisciplina = "";
			
		}
		
		for (CorpoDocenteDisciplinaLato corpoDocente : obj.getEquipesLato()) {
			if ((corpoDocente.getDocente() !=null && obj.getEquipeLato().getDocente() != null) && (corpoDocente.getDocente().getId() == obj.getEquipeLato().getDocente().getId()))
				addMensagemErro("Docente já presente no Corpo Docente da Disciplina.");	
			if ((corpoDocente.getDocenteExterno() !=null &&  obj.getEquipeLato().getDocenteExterno() != null) && (corpoDocente.getDocenteExterno().getId() == obj.getEquipeLato().getDocenteExterno().getId())) 
				addMensagemErro("Docente já presente no Corpo Docente da Disciplina.");

			obj.setDisciplina( corpoDocente.getDisciplina() );
		}
		
		if (obj.getDisciplina().getChTotal() == 0) 
			addMensagemErro("Carga Horária Total Inválida. A disciplina deve ter mais do que " + obj.getDisciplina().getChTotal() +"h");
		
		if (obj.getEquipeLato().getCargaHoraria() == null || obj.getEquipeLato().getCargaHoraria() == 0){ 
			obj.getEquipeLato().setCargaHoraria(0);
			addMensagemErro("A Carga Horária do Docente deve ser maior do que 0h");
		}
		
		if (hasOnlyErrors())
			return null;
		
		obj.getDisciplina().setCurso(obj);
		obj.getEquipeLato().setProposta(obj.getPropostaCurso());
		obj.getEquipeLato().setDisciplina(obj.getDisciplina());
		obj.getEquipeLato().getDisciplina().getDetalhes().setNome(obj.getDisciplina().getDetalhes().getNome());
		corpoDocenteCursoLato = getGenericDAO().findByPrimaryKey(corpoDocenteCursoLato.getId(), CorpoDocenteCursoLato.class);
		if (corpoDocenteCursoLato.getServidor() != null ){ 
			obj.getEquipeLato().setDocente(getGenericDAO().findByPrimaryKey(corpoDocenteCursoLato.getServidor().getId(), Servidor.class));	
			obj.getEquipeLato().setNome(corpoDocenteCursoLato.getServidor().getPessoa().getNome());
		}else{
			obj.getEquipeLato().setDocenteExterno(getGenericDAO().findByPrimaryKey(corpoDocenteCursoLato.getDocenteExterno().getId(), DocenteExterno.class));
			obj.getEquipeLato().setNome(corpoDocenteCursoLato.getDocenteExterno().getPessoa().getNome());
		}
		
		obj.getEquipesLato().add(obj.getEquipeLato());
		obj.setEquipeLato( new CorpoDocenteDisciplinaLato() );
		corpoDocenteCursoLato = new CorpoDocenteCursoLato();
		return null;
	}
	
	/**
	 * Remoção da Docente adicionando na disciplina.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/disciplinas.jsp 
	 * @throws ArqException 
	 */
	public void removerDocenteDisciplina(ActionEvent evt) throws ArqException{
		CorpoDocenteDisciplinaLato corpoDocenteDiscLato = (CorpoDocenteDisciplinaLato) evt.getComponent().getAttributes().get("linha");
		for (CorpoDocenteDisciplinaLato corpoDocente : obj.getEquipesLato()) {
			 if (corpoDocente.getDocente() != null &&  corpoDocenteDiscLato.getDocente() != null && 
					 corpoDocente.getDocente().getId() ==  corpoDocenteDiscLato.getDocente().getId()) {
				 obj.getEquipesLato().remove(corpoDocente);
				 break;
			}
			 if (corpoDocente.getDocenteExterno() != null && corpoDocenteDiscLato.getDocenteExterno() != null && 
					 corpoDocente.getDocenteExterno().getId() ==  corpoDocenteDiscLato.getDocenteExterno().getId()) {
				 obj.getEquipesLato().remove(corpoDocente);
				 break;
			} 
		}
		if (obj.getEquipesLato().size()==0 && ValidatorUtil.isNotEmpty( obj.getDisciplina() )) { 
			obj.setDisciplina(new ComponenteCurricular());
		}
		forward(JSP_DISCIPLINAS_CURSO);
	}
	
	
	/**
	 * Serve para cadastrar um docente a uma disciplina.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/disciplinas.jsp
	 * 
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String adicionarDisciplina() throws SegurancaException, ArqException, NegocioException {
		//Testando pra ver se o docente adicionou primeiro o docente a disciplina.
		if (obj.getEquipesLato().size() == 0) {
			addMensagemWarning("É necessário antes informar a disciplina");
			addMensagemWarning("É necessário antes informar um docente");
			return null;
		}
		
		// Testando a Carga horária da Disciplina
		Integer cargaHorariaTotal=0;
		for (CorpoDocenteDisciplinaLato cdd : obj.getEquipesLato()) 
			cargaHorariaTotal += cdd.getCargaHoraria();
		
		if (ValidatorUtil.isEmpty( obj.getDisciplina()) ) {
			obj.getDisciplina().getDetalhes().setChAula(ValidatorUtil.isEmpty(chAula) ? 0 : chAula );
			obj.getDisciplina().getDetalhes().setChEstagio(ValidatorUtil.isEmpty(chEstagio) ? 0 : chEstagio );
			obj.getDisciplina().getDetalhes().setChLaboratorio(ValidatorUtil.isEmpty(chLaboratorio) ? 0 : chLaboratorio );
		}
		
		if ( cargaHorariaTotal != obj.getDisciplina().getChTotal() ){ 
			addMensagemErro("Carga horária total dos Docentes (" + cargaHorariaTotal + "h) é diferente da Carga Horária da disciplina("+obj.getDisciplina().getChTotal()+"h).");
			return null;
		}
		
		ComponenteCurricularMov mov = new ComponenteCurricularMov();
		obj.setComponenteCursoLato( new ComponenteCursoLato() );
		for (CorpoDocenteDisciplinaLato cdd : obj.getEquipesLato()) {
			obj.getEquipeLato().setCargaHoraria(cdd.getCargaHoraria());
			obj.getEquipeLato().setDisciplina(cdd.getDisciplina());
			obj.getEquipeLato().getDisciplina().getDetalhes().setNome(cdd.getDisciplina().getDetalhes().getNome());
			obj.getEquipeLato().setProposta(obj.getPropostaCurso());
			if (cdd.getDocente() != null && cdd.getDocente().getId() != 0) {
				obj.getEquipeLato().getDocente().setId(cdd.getDocente().getId());	
				obj.getEquipeLato().setDocenteExterno(null);
			}else{
				obj.getEquipeLato().getDocenteExterno().setId(cdd.getDocenteExterno().getId());
				obj.getEquipeLato().setDocente(null);
			}

			if (cdd.getDisciplina().getId() == 0) {
				obj.getComponenteCursoLato().setDisciplina(cdd.getDisciplina());
				obj.getComponenteCursoLato().setCursoLato((CursoLato) cdd.getDisciplina().getCurso());
				cdd.getDisciplina().setNivel(NivelEnsino.LATO);
				cdd.getDisciplina().setUnidade(obj.getUnidade());
				cdd.getDisciplina().getTipoComponente().setId(TipoComponenteCurricular.MODULO);
				cdd.getDisciplina().setModalidadeEducacao(obj.getModalidadeEducacao());
				
				mov.setObjMovimentado(cdd.getDisciplina());
				if (cdd.getDisciplina().getId() == 0) {
					prepareMovimento(SigaaListaComando.CADASTRAR_COMPONENTE_CURRICULAR);
					mov.setCodMovimento(SigaaListaComando.CADASTRAR_COMPONENTE_CURRICULAR);
				}else{
					prepareMovimento(SigaaListaComando.ALTERAR_COMPONENTE_CURRICULAR);
					mov.setCodMovimento(SigaaListaComando.ALTERAR_COMPONENTE_CURRICULAR);
				}
				execute(mov);
				getGenericDAO().create(obj.getComponenteCursoLato());
				getGenericDAO().create(obj.getEquipeLato());
				obj.setComponenteCursoLato( new ComponenteCursoLato() );
				obj.setEquipeLato( new CorpoDocenteDisciplinaLato() );
			}else{
				obj.getComponenteCursoLato().setDisciplina(cdd.getDisciplina());
				obj.getComponenteCursoLato().setCursoLato((CursoLato) cdd.getDisciplina().getCurso());
				getGenericDAO().create(obj.getComponenteCursoLato());
				getGenericDAO().create(obj.getEquipeLato());
				obj.setComponenteCursoLato( new ComponenteCursoLato() );
				obj.setEquipeLato( new CorpoDocenteDisciplinaLato() );
			}
		}
		cadastrar();
		obj.getEquipesLato().clear();
		obj.setEquipeLato( new CorpoDocenteDisciplinaLato() );
		obj.setDisciplina(new ComponenteCurricular());
		obj.getDisciplina().setDetalhes(new ComponenteDetalhes());
		return forward(JSP_DISCIPLINAS_CURSO);
	}
	
	/**
	 * Retorna todas as disciplinas cadastradas para o curso Lato Sensu.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/include/view_disciplinas.jsp</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/disciplinas.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<LinhaComponenteCursoLato> getAllDisciplinasCursoLato() throws DAOException {
		ComponenteCursoLatoDao componenteCursoLatoDao = getDAO(ComponenteCursoLatoDao.class);
		Collection<LinhaComponenteCursoLato> componente = new ArrayList<LinhaComponenteCursoLato>(); 
		try {
			componente.clear();
			componente = componenteCursoLatoDao.findComponenteCursoLatoByCurso(obj.getId());
		} finally {
			componenteCursoLatoDao.close();
		}
		return componente;
	}
	
	/**
	 * Remove a disciplina anteriormente cadastrada
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/disciplinas.jsp
	 * 
	 * @param componenteCurricular
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String removerDisciplina() throws ArqException, NegocioException{
		ComponenteCursoLatoDao componenteCursoLatoDao = getDAO(ComponenteCursoLatoDao.class);
		CorpoDocenteDisciplinaLatoDao corpoDocenteDisciplinaLatoDao = getDAO(CorpoDocenteDisciplinaLatoDao.class); 
		try {
			obj.setDisciplina( getGenericDAO().findByPrimaryKey(getParameterInt("id"), ComponenteCurricular.class) );
			
			obj.setEquipesLato( new ArrayList<CorpoDocenteDisciplinaLato>() );
			obj.setEquipesLato( corpoDocenteDisciplinaLatoDao.findByPropostaDisciplina(obj.getPropostaCurso().getId(), obj.getDisciplina().getId()) );

			if (obj.getEquipesLato() != null && obj.getEquipesLato().size() > 0) {
				for (CorpoDocenteDisciplinaLato corpoDocenteDisciplinaLato : obj.getEquipesLato()) {
					getGenericDAO().remove(corpoDocenteDisciplinaLato);
				}
			}
			
			Collection<ComponenteCursoLato> componenteCursoLato = null;
			componenteCursoLato = componenteCursoLatoDao.findComponenteCursoLatoByDisciplina(obj.getDisciplina().getId(), obj.getId());
			
			if (componenteCursoLato == null) {
				addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
				return getListPage();
			}
			
			for (ComponenteCursoLato componenteCursoLato3 : componenteCursoLato) {
				getGenericDAO().remove(componenteCursoLato3);	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			componenteCursoLatoDao.close();
			corpoDocenteDisciplinaLatoDao.close();
			obj.setEquipesLato( new ArrayList<CorpoDocenteDisciplinaLato>() );
			obj.setDisciplina( new ComponenteCurricular() );
		}
		return getCurrentURL();
	}

	/**
	 * 
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/disciplinas.jsp
	 * 
	 * @param idCursoLato
	 * @throws ArqException
	 */
	public void removerCursoLato(int idCursoLato) throws ArqException{
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		prepareMovimento(SigaaListaComando.REMOVER_CURSO_LATO);
		mov.setCodMovimento(SigaaListaComando.REMOVER_CURSO_LATO);
		try {
			execute(mov);
			addMensagem(OPERACAO_SUCESSO);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		}
	}
	
	/**
	 * Carrega a proposta e direciona para a tela de alteração da situação da proposta.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/busca.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String preAlteracaoSituacaoProposta() throws DAOException{
		int id = getParameterInt("id", 0);
		setObj(getGenericDAO().findByPrimaryKey(id, CursoLato.class));
		return forward("/lato/proposta_curso/alterar_situacao.jsp");
	}
	
	/**
	 * Serve para alterar a situação da proposta.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/alterar_situacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String alterarSituacao() throws ArqException {
		PropostaCursoLato propostaAntiga = new PropostaCursoLato();
		propostaAntiga = getGenericDAO().findAndFetch(obj.getPropostaCurso().getId(), PropostaCursoLato.class, "situacaoProposta");

		ValidatorUtil.validateRequiredId(obj.getPropostaCurso().getSituacaoProposta().getId(), "Nova Situação", erros);
		ValidatorUtil.validateRequired(obj.getHistoricoSituacao().getObservacoes(), "Despacho", erros);

		if (propostaAntiga.getSituacaoProposta().getId() == obj.getPropostaCurso().getSituacaoProposta().getId())
			addMensagemErro("A situação escolhida é igual a situação atual da proposta.");
		
		if (hasOnlyErrors()) 
			return null;
		
		if (propostaAntiga.getSituacaoProposta().getId() != obj.getPropostaCurso().getSituacaoProposta().getId()) {
			obj.getHistoricoSituacao().setProposta(obj.getPropostaCurso());
			obj.getHistoricoSituacao().setDataCadastro(new Date());
			obj.getHistoricoSituacao().setSituacao(obj.getPropostaCurso().getSituacaoProposta());
			obj.getHistoricoSituacao().setUsuario(getUsuarioLogado());
			getGenericDAO().create(obj.getHistoricoSituacao());
			getGenericDAO().detach(propostaAntiga);
			getGenericDAO().update(obj.getPropostaCurso());
			CursoLato curso = getDAO(CursoLatoDao.class).findByPropostaCursoLato(obj.getPropostaCurso().getId());
			CoordenacaoCurso coordenador = getDAO(CoordenacaoCursoDao.class).findUltimaByCurso(curso);
			obj.getPropostaCurso().setSituacaoProposta(getGenericDAO().findByPrimaryKey(obj.getPropostaCurso().getSituacaoProposta().getId(), SituacaoProposta.class));
			if (!ValidatorUtil.isEmpty(coordenador) && !ValidatorUtil.isEmpty(propostaAntiga) && !ValidatorUtil.isEmpty(curso))
				notificarSituacaoPropostaCoordenador(coordenador, propostaAntiga, obj.getPropostaCurso().getSituacaoProposta().getDescricao(), curso);
			else
				addMensagemWarning("Não foi enviado o email para o coordenador, pois não há coordenador ativo para o curso em questão.");
			
			BuscaCursoLatoMBean mBean = getMBean("buscaCursoLatoMBean");
			mBean.setListaCursoLato(new ArrayList<LinhaConsultaCursoGeral>());
			
			addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Situação Proposta");
		}
		return forward("/lato/proposta_curso/busca.jsp");
	}
	
	/**
	 * Realiza o envio da nova situação da proposta para o coordenador da Proposta de Curso Lato Sensu.
	 *  
	 * @param coordenador
	 * @param proposta
	 * @param situacaoAtual
	 * @param curso
	 */
	private static void notificarSituacaoPropostaCoordenador(CoordenacaoCurso coordenador, PropostaCursoLato proposta, String situacaoAtual, CursoLato curso){
		  String mensagem = "Caro(a) Coordenador(a) "+ coordenador.getServidor().getPessoa().getNome() + ", <br> " +
		  "a proposta "+ curso.getNomeCompleto() +" passou da situação "+ proposta.getSituacaoProposta().getDescricao() +" para a seguinte situação: <b>"+
		  situacaoAtual+"</b> ";
		  
		  // enviando e-mail.
		  MailBody email = new MailBody();
		  email.setAssunto("[SIGAA] Notificação de Mudança da Situação da Proposta");
		  email.setContentType(MailBody.HTML);
		  email.setNome(coordenador.getServidor().getPessoa().getNome());
		  email.setEmail(coordenador.getServidor().getPessoa().getEmail());
		  email.setMensagem(mensagem);
		  Mail.send(email);
	}
	
	/**
	 * Preenche os dados e joga para tela do histórico
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/busca.jsp</li>
	 * </ul>
	 * 
	 */
	public String verHistorico() throws Exception {
		HistoricoSituacaoDao daoH = getDAO(HistoricoSituacaoDao.class);
		int idCurso = getParameterInt("id");  
		setObj(getGenericDAO().findByPrimaryKey(idCurso, CursoLato.class));
		obj.setHistoricoSituacoes( daoH.findByProposta( obj.getPropostaCurso().getId() ) );
		return forward("/lato/proposta_curso/historio_alteracoes.jsp");
	}
	
	/**
	 * Serve para carregar a proposta e direcionar para a tela de exibição das informações 
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/busca.jsp</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/propostas_submetidas.jsp</li>
	 * </ul>
	 * 
	 * cursoLatoMBean.visualizar
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String visualizar() throws ArqException {
		obj = new CursoLato();
		carregaObject();
		obj.setSelecionado(true);
		return forward(JSP_VIEW);
	}
	
	/**
	 * Gera um resumo da curso.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/disciplinas.jsp
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String resumo() throws DAOException{
//		CursoLatoValidator.validacaoCargaHorarioCorpoDocente(getDAO(ComponenteCursoLatoDao.class), obj, erros);
		if (hasOnlyErrors()) {
			addMensagens(erros);
			return null;
		}
		if (getAllDisciplinasCursoLato().size() >= 1){ 
			obj.setUnidade(getGenericDAO().findByPrimaryKey(obj.getUnidade().getId(), Unidade.class));
			obj.setTipoTrabalhoConclusao( getGenericDAO().findByPrimaryKey(obj.getTipoTrabalhoConclusao().getId(), TipoTrabalhoConclusao.class));
			obj.setTipoPassoPropostaLato(TipoPassoPropostaLato.TELA_RESUMO_PROPOSTA);
			setOperacaoAtiva(SigaaListaComando.ALTERAR_CURSO_LATO.getId());
			obj.setPropostaCurso(getGenericDAO().refresh(obj.getPropostaCurso()));
			if ( obj.getPropostaCurso().getSituacaoProposta().getId() == SituacaoProposta.SUBMETIDA 
					|| obj.getPropostaCurso().getSituacaoProposta().getId() == SituacaoProposta.ACEITA ) 
				setConfirmButton("Alterar Proposta");
			 else 
				setConfirmButton("Submeter Proposta");
			
			getGenericDAO().initialize(obj.getAreaConhecimentoCnpq());
			getGenericDAO().initialize(obj.getAreaConhecimentoCnpq().getGrandeArea());
			getGenericDAO().initialize(obj.getAreaConhecimentoCnpq().getEspecialidade());
			getGenericDAO().initialize(obj.getSubAreaConhecimentoCnpq());
			getGenericDAO().initialize(obj.getEspecialidadeAreaConhecimentoCnpq());
			return forward(JSP_VIEW);	
		}
		else{
			addMensagemErro("É necessário adicionar uma disciplina");
			return null;
		}
	}
	
	/**
	 * Serve para mudar o status da proposta e cadastra o histório de quando foi submetida.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/view.jsp</li>
	 * </ul>
	 * 
	 */
	public String concluir() throws DAOException{
		if (checkOperacaoAtiva(SigaaListaComando.ALTERAR_CURSO_LATO.getId())) {
			mudarStatusProposta();
			addMensagemInformation("Proposta Submetida com Sucesso");
			cadastrarHistorico(obj);
			removeOperacaoAtiva();
			obj = new CursoLato();
			cursosLato = new ArrayList<CursoLato>();
		}
		if (getAcessoMenu().isLato())
			return forward("/lato/proposta_curso/busca.jsf");
		else
			return forward(JSP_PROPOSTAS_SUBMETIDAS);
	}
	
	/**
	 * Realiza da mundança de situação da proposta desde que a mesma não esteja submetida ou Aceita.  
	 */
	private void mudarStatusProposta() throws DAOException{
		if ( obj.getPropostaCurso().getSituacaoProposta().getId() != SituacaoProposta.SUBMETIDA 
				&& obj.getPropostaCurso().getSituacaoProposta().getId() != SituacaoProposta.ACEITA )

			getGenericDAO().updateField(PropostaCursoLato.class,obj.getPropostaCurso().getId(), "situacaoProposta", SituacaoProposta.SUBMETIDA);
	}
	

	/**
	 * Serve para cadastrar o histórico do Curso.  
	 */
	private void cadastrarHistorico(CursoLato curso) throws DAOException{
		if (ValidatorUtil.isNotEmpty(curso.getPropostaCurso().getId()) && ValidatorUtil.isNotEmpty(curso.getPropostaCurso().getSituacaoProposta().getId())) {
			HistoricoSituacao historicoSituacaoProposta = new HistoricoSituacao();
			historicoSituacaoProposta.setProposta(curso.getPropostaCurso());
			historicoSituacaoProposta.setUsuario(curso.getPropostaCurso().getUsuario());
			historicoSituacaoProposta.setSituacao(curso.getPropostaCurso().getSituacaoProposta());
			historicoSituacaoProposta.setDataCadastro(new Date());
			getGenericDAO().create(historicoSituacaoProposta);
		}
	}

	/**
	 * Método utilizado pra carregar a SubArea.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/dados_gerais.jsp
	 */
	public void changeEspecialidade(ValueChangeEvent evt) throws DAOException {
		changeEspecialidade((Integer) evt.getNewValue());
	}

	/** reponsável por carregar a Especializade do Curso Lato Sensu.  */
	private void changeEspecialidade(int id) throws DAOException {
		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		if ( id != 0 ) {
			Integer codigo = id;
			AreaConhecimentoCnpq area =  getGenericDAO().findByPrimaryKey(codigo, AreaConhecimentoCnpq.class);
			propostaEspecialidade = toSelectItems(dao.findEspecialidade(area), "id", "nome");
		}
	}
	
	/**
	 * Serve pra ligstar todas os cursos com a proposta ainda incompleta;
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/portais/docente/menu_docente.jsp
	 */
	@Override
	public String listar() throws ArqException {
		resetBean();
		cursosLato = new ArrayList<CursoLato>();
		prepareMovimento(SigaaListaComando.REMOVER_CURSO_LATO);
		return getListPage();
	}
	
	/**
	 * Cadastra um curso Baseado em um curso anterior
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/busca.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String cadastrarCursoBaseadoEmCursoAntigo() throws ArqException{
		carregaObject();
		obj.setDataInicio(null);
		obj.setDataFim(null);
		
		CursoLatoDao dao = getDAO(CursoLatoDao.class);
		try {
			obj.setCursoInstituicoesEnsino((List<DadosCursoRede>) 
					getGenericDAO().findByExactField(DadosCursoRede.class, "curso.id", obj.getId()));
			obj.getComponentesCursoLato().addAll(
					getGenericDAO().findByExactField(ComponenteCursoLato.class, "cursoLato.id", obj.getId()));
			obj.getComponentesCursoLato().addAll(
					getGenericDAO().findByExactField(ComponenteCursoLato.class, "cursoLato.id", obj.getId()));
			
		} finally{
			dao.close();
		}
		
		CorpoDocenteDisciplinaLatoDao corpoDocenteDiscLatodao = getDAO(CorpoDocenteDisciplinaLatoDao.class);
		Collection<CorpoDocenteDisciplinaLato> corpoDocente = corpoDocenteDiscLatodao.findByPropostaDocente(obj.getPropostaCurso().getId(), null, null);
		for (CorpoDocenteDisciplinaLato corpoDocenteDisciplinaLato : corpoDocente) {
			corpoDocenteDisciplinaLato.setProposta(obj.getPropostaCurso());
			obj.getPropostaCurso().getEquipesLato().add(corpoDocenteDisciplinaLato);
		}

		obj.setNumeroVagas(0);
		obj.setNumeroVagasServidores(0);
		obj.setId(0);
		obj.getPropostaCurso().setId(0);
		obj.getPropostaCurso().setHistorico(null);
		obj.setComponentesCursoLato(null);
		obj.setCursosServidores(null);
		obj.setOutrosDocumentos(null);
		obj.setParcerias(null);
		obj.setPublicosAlvoCurso(null);
		obj.setTurmasEntrada(null);
		obj.getCoordenador().setCurso(obj);
		obj.getCoordenador().setId(0);
		obj.getCoordenacao().setCurso(obj);
		obj.getCoordenacao().setId(0);
		obj.getViceCoordenador().setCurso(obj);
		obj.getViceCoordenador().setId(0);
		if (obj.getViceCoordenacao() != null) {
			obj.getViceCoordenacao().setCurso(obj);
			obj.getViceCoordenacao().setId(0);
		}
		
		for (CorpoDocenteDisciplinaLato equipeLato : obj.getPropostaCurso().getEquipesLato()){
			equipeLato.setId(0);
			equipeLato.setProposta(obj.getPropostaCurso());
		}
		for (FormaAvaliacaoProposta formaAvaliacao : obj.getPropostaCurso().getFormasAvaliacaoProposta()){
			formaAvaliacao.setId(0);
			formaAvaliacao.setProposta(obj.getPropostaCurso());
		}
		for (FormaSelecaoProposta formaSelecao : obj.getPropostaCurso().getFormasSelecaoProposta()){
			formaSelecao.setId(0);
			formaSelecao.setProposta(obj.getPropostaCurso());
		}
		
		nova = false;
		return forward(JSP_DADOS_GERAIS);	
	}
	
	/**
	 * Redireciona para a tela da listagem de todos os cursos com proposta ainda incompleta.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/portais/docente/menu_docente.jsp
	 */
	@Override
	public String getListPage() {
		return isLatoSensu() ? forward("/lato/proposta_curso/busca.jsp") : forward(JSP_PROPOSTAS_CURSO);
	}
	
	/**
	 * Direciona para a tela inicial do cadastro.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/propostas_submetidas.jsp
	 */
	@Override
	public String getFormPage() {
		return forward(JSP_DADOS_GERAIS);
	}
	
	/**
	 * Limpa as informações a serem utilizadas e direciona para a tela de cadastro.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String preCadastrar() {
		try {
			obj = new CursoLato();
			clear();
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return getFormPage();
	}
	
	/**
	 * Retornar todo o corpo Docente da proposta.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<CorpoDocenteCursoLato> getallCorpoDocenteProposta() throws DAOException{
		CorpoDocenteCursoLatoDao dao = getDAO(CorpoDocenteCursoLatoDao.class);
		
		Collection<CorpoDocenteCursoLato> corpoDocentesCursoLato = null;
		try {
			corpoDocentesCursoLato = dao.findByAllDocenteCurso(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dao.close();
		}
		return corpoDocentesCursoLato;
	}
	
	/**
	 * Carrega as propostas do curso
	 *  
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/propostas_submetidas.jsp
	 * @return
	 * @throws DAOException
	 */
	public Collection<CursoLato> getAllCursoLatoDocente() throws DAOException {
		CursoLatoDao dao = getDAO(CursoLatoDao.class);
		try {
			if (cursosLato.isEmpty() && !ValidatorUtil.isEmpty(getUsuarioLogado().getServidor()))
				cursosLato = dao.findAllByPropostaServidor(getUsuarioLogado().getServidor());
		} finally {
			dao.close();
		} 
		return cursosLato;
	}
	
	/**
	 * Remove o do corpo do curso Lato.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/disciplinas.jsp</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/docentes.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public void removerDocente() throws DAOException{
		int id = getParameterInt("idDocente") == 0 ? getParameterInt("idDocenteExterno") : getParameterInt("idDocente");
		corpoDocenteCursoLato = getGenericDAO().findByPrimaryKey(id, CorpoDocenteCursoLato.class);
		
		if (corpoDocenteCursoLato != null) {
			if (corpoDocenteCursoLato.getServidor() != null && (obj.getCoordenador().getServidor().getId() == corpoDocenteCursoLato.getServidor().getId() 
					|| obj.getViceCoordenador().getServidor().getId() == corpoDocenteCursoLato.getServidor().getId())) 
				addMensagemErro("O Docente "+ corpoDocenteCursoLato.getNome() +" não pode ser removido, pois o mesmo " +
						"possui o cargo de COORDENADOR/VICE-COORDENADOR do curso.");
	
			if (!hasErrors()) {
				CorpoDocenteDisciplinaLatoDao dao = getDAO(CorpoDocenteDisciplinaLatoDao.class);
				Collection<CorpoDocenteDisciplinaLato> corpoDocenteDisciplinaLato = dao.findByPropostaDocente(obj.getPropostaCurso().getId(), 
						corpoDocenteCursoLato.getServidor() == null ? null : corpoDocenteCursoLato.getServidor().getId(), 
								corpoDocenteCursoLato.getDocenteExterno() == null ? null : corpoDocenteCursoLato.getDocenteExterno().getId());
				for (CorpoDocenteDisciplinaLato cddl: corpoDocenteDisciplinaLato) {
					if ((cddl.getDocente() != null && cddl.getDocente().getId() == corpoDocenteCursoLato.getServidor().getId()) 
							|| 
						(cddl.getDocenteExterno() != null && cddl.getDocenteExterno().getId() == corpoDocenteCursoLato.getDocenteExterno().getId())) 
						addMensagemErro("Não é possível remover esse docente pois o mesmo ministra uma disciplina");
				}
			}
		if (!hasErrors()){
			if ( corpoDocenteCursoLato.getDocenteExterno() != null ) {
				getGenericDAO().updateFields(DocenteExterno.class, corpoDocenteCursoLato.getDocenteExterno().getId(), 
						new String[] {"ativo","prazoValidade"}, 
						new Object[] {Boolean.FALSE, new Date()});
			}
			getGenericDAO().remove(corpoDocenteCursoLato);
			obj.getCorpoDocenteProposta().clear();
		}
		} else{
			addMensagem(MensagensArquitetura.ACAO_JA_EXECUTADA, "Docente", "removido");
			getListPage();
		}
		
		corpoDocenteCursoLato = new CorpoDocenteCursoLato();
		docentes();
	}
	
	
	/**
	 * Remover um Proposta
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/busca.jsp</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/propostas_submetidas.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public String remocao() throws NegocioException, ArqException{
		setId();
		setObj(getGenericDAO().findByPrimaryKey(obj.getId(), CursoLato.class));
		if (obj == null){ 
			addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
			return null;
		}
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			prepareMovimento(SigaaListaComando.REMOVER_CURSO_LATO);
			mov.setCodMovimento(SigaaListaComando.REMOVER_CURSO_LATO);
			mov.setObjMovimentado(obj);
			execute(mov);
			obj = new CursoLato();
			addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Proposta");
			
		} catch (DAOException e) {
			e.printStackTrace();
		}
		
		cursosLato = new ArrayList<CursoLato>();
		
		if (ValidatorUtil.isNotEmpty(getUsuarioLogado().getServidor()))
			getAllCursoLatoDocente();
		
		return getListPage();
	}
	
	/**
	 * Carrega as Áreas.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @param area
	 * @throws DAOException
	 */
	public void carregarGrandeArea(Integer area) throws DAOException {
		if (obj.getAreaConhecimentoCnpq().getGrandeArea().getId() != 0) {
			area = obj.getAreaConhecimentoCnpq().getGrandeArea().getId();
			AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
			try {
				AreaConhecimentoCnpq grandeArea = dao.findByPrimaryKey(area, AreaConhecimentoCnpq.class);
				Collection<AreaConhecimentoCnpq> areas = dao.findAreas(grandeArea);
				subArea.addAll(toSelectItems(areas, "id","nome"));
			} finally {
				dao.close();
			}
		}
	}

	/**
	 * Carrega as Sub-Área.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @param area
	 * @throws DAOException
	 */
	public void carregarSubArea(Integer subArea) throws DAOException {
		if (obj.getAreaConhecimentoCnpq().getSubarea().getId() != 0) {
			subArea = obj.getAreaConhecimentoCnpq().getSubarea().getId();
			AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
			try {
				AreaConhecimentoCnpq grandeArea = dao.findByPrimaryKey(subArea, AreaConhecimentoCnpq.class);
				Collection<AreaConhecimentoCnpq> subAreas = dao.findEspecialidade(grandeArea);
				especialidades.addAll(toSelectItems(subAreas, "id", "nome"));
			} finally {
				dao.close();
			}
		}
	}

	/**
	 * Para tratar mudança da subárea
	 *
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/dados_gerais.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void changeSubArea(ValueChangeEvent evt) throws DAOException {
		changeSubArea((Integer) evt.getNewValue());
	}

	/** Responsável por carregar a Sub-área do Curso Lato Sensu. */
	private void changeSubArea(int id) throws DAOException {
		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		if ( id != 0 ) {
			Integer codigo = id;
			obj.setSubAreaConhecimentoCnpq(dao.findAndFetch(codigo, AreaConhecimentoCnpq.class, "subarea"));
			especialidades = toSelectItems(dao.findSubAreas(codigo), "id", "nome");
		}
	}

	/**
	 * Carrega as Sub-Área.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @param area
	 * @throws DAOException
	 */
	public void carregarSubEspecialidade(Integer especialidade) throws DAOException {
		if (obj.getAreaConhecimentoCnpq().getEspecialidade().getId() != 0) {
			especialidade = obj.getAreaConhecimentoCnpq().getEspecialidade().getId();
			AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
			try {
				AreaConhecimentoCnpq grandeArea = dao.findByPrimaryKey(especialidade, AreaConhecimentoCnpq.class);
				Collection<AreaConhecimentoCnpq> subAreas = dao.findEspecialidade(grandeArea);
				propostaEspecialidade.addAll(toSelectItems(subAreas, "id", "nome"));
			} finally {
				dao.close();
			}
		}
	}

	/**
	 * Inicializa os atributos para evitar futuros erro. 
	 * 
	 * @throws DAOException
	 */
	private void clear() throws DAOException {
		if (obj.getId() == 0){ 
			subArea.clear();
		}
		obj.setAreaConhecimentoCnpq(getGenericDAO().refresh(obj.getAreaConhecimentoCnpq()));
		if (obj.getGrandeAreaConhecimentoCnpq() == null)
			obj.setGrandeAreaConhecimentoCnpq(new AreaConhecimentoCnpq());
		if (obj.getSubAreaConhecimentoCnpq() == null)
			obj.setSubAreaConhecimentoCnpq(new AreaConhecimentoCnpq());
		if (obj.getAreaConhecimentoCnpq() == null)
			obj.setAreaConhecimentoCnpq(new AreaConhecimentoCnpq());
		if (obj.getAreaConhecimentoCnpq().getGrandeArea() == null) 
			obj.getAreaConhecimentoCnpq().setGrandeArea(new AreaConhecimentoCnpq());
		if (obj.getEspecialidadeAreaConhecimentoCnpq() == null) 
			obj.setEspecialidadeAreaConhecimentoCnpq(new AreaConhecimentoCnpq());
		if (obj.getAreaConhecimentoCnpq().getGrandeArea() != null)
			carregarGrandeArea(obj.getAreaConhecimentoCnpq().getGrandeArea().getId());
		if (obj.getAreaConhecimentoCnpq().getSubarea() == null) 
			obj.getAreaConhecimentoCnpq().setSubarea(new AreaConhecimentoCnpq());
		if (obj.getAreaConhecimentoCnpq().getSubarea() != null) 
			carregarSubArea(obj.getAreaConhecimentoCnpq().getSubarea().getId());
		if (obj.getAreaConhecimentoCnpq().getEspecialidade() == null) 
			obj.getAreaConhecimentoCnpq().setEspecialidade(new AreaConhecimentoCnpq());
		if (obj.getAreaConhecimentoCnpq().getEspecialidade() != null) 
			carregarSubEspecialidade(obj.getAreaConhecimentoCnpq().getEspecialidade().getId());		
		if (obj.getModalidadeEducacao() == null) 
			obj.setModalidadeEducacao(new ModalidadeEducacao());
		if (obj.getTipoTrabalhoConclusao() == null) 
			obj.setTipoTrabalhoConclusao(new TipoTrabalhoConclusao());
		if (obj.getPoloCurso().getPolo() == null) 
			obj.getPoloCurso().setPolo(new Polo());
		if (obj.getPoloCurso().getCurso() == null) 
			obj.getPoloCurso().setCurso(new Curso());
		if (obj.getPoloCurso().getPolo().getCidade() == null) 
			obj.getPoloCurso().getPolo().setCidade(new Municipio());
		if (obj.getCoordenador() == null)
			obj.setCoordenador(new CoordenacaoCurso());
		if (obj.getCoordenadorAntigo() == null) 
			obj.setCoordenadorAntigo( new CoordenacaoCurso() );
		if (obj.getViceCoordenador() == null) 
			obj.setViceCoordenador(new CoordenacaoCurso());
		if (obj.getViceCoordenadorAntigo() == null) 
			obj.setViceCoordenadorAntigo( new CoordenacaoCurso() );
		if (obj.getSecretario() == null) 
			obj.setSecretario( new SecretariaUnidade() );
		if (obj.getSecretario().getUnidade() == null ) 
			obj.getSecretario().setUnidade(new Unidade());	
		if (obj.getSecretario().getCurso() == null) 
			obj.getSecretario().setCurso(new Curso());
		if (obj.getSecretario().getUsuario() == null) 
			obj.getSecretario().setUsuario(new Usuario());
		if (obj.getSecretario().getUsuario().getServidor() == null) 
			obj.getSecretario().getUsuario().setServidor(new Servidor());
		if (obj.getSecretarioAntigo() == null) 
			obj.setSecretarioAntigo( new SecretariaUnidade() );
		if (obj.getSecretarioAntigo().getUnidade() == null) 
			obj.getSecretarioAntigo().setUnidade(new Unidade());
		if (obj.getSecretarioAntigo().getCurso() == null) 
			obj.getSecretarioAntigo().setCurso(new Curso());
		if (obj.getSecretarioAntigo().getUsuario() == null) 
			obj.getSecretarioAntigo().setUsuario(new Usuario());
		if (obj.getEquipeLato().getDocente() == null)
			obj.getEquipeLato().setDocente(new Servidor());
		if (obj.getEquipeLato().getDocenteExterno() == null) 
			obj.getEquipeLato().setDocenteExterno(new DocenteExterno());
		if (obj.getDisciplina() == null) 
			obj.setDisciplina( new ComponenteCurricular() );
		if (obj.getDisciplina().getDetalhes() == null) 
			obj.getDisciplina().setDetalhes(new ComponenteDetalhes());
		if (obj.getDisciplina().getCurso() == null) 
			obj.getDisciplina().setCurso(new Curso());
		if (obj.getUnidadeOrcamentaria() == null)
			obj.setUnidadeOrcamentaria(new Unidade());
		
		corpoDocenteCursoLato = new CorpoDocenteCursoLato();		
		corpoDocenteCursoLato.getDocenteExterno().getPessoa().setSexo(PessoaGeral.SEXO_MASCULINO);
		getCurrentSession().setAttribute("nivel", NivelEnsino.LATO);
		
		if (obj.getFormaSelecao().size() == 0) {
			for (FormaSelecaoProposta forma : obj.getPropostaCurso().getFormasSelecaoProposta()) 
				obj.getFormaSelecao().add( ((Integer) forma.getFormaSelecao().getId()).toString() );
		}
		
		if (obj.getFormasAvaliacao().size() == 0) {
			for (FormaAvaliacaoProposta formaAva : obj.getPropostaCurso().getFormasAvaliacaoProposta()) 
				obj.getFormasAvaliacao().add( ((Integer) formaAva.getFormaAvaliacao().getId()).toString() );
		}
		
		obj.setTipoPassoPropostaLato(TipoPassoPropostaLato.TELA_DADOS_GERAIS);
	}
	
	public boolean getUnidadesEnvolvidas(){
		return ( !ValidatorUtil.isEmpty(obj.getUnidadesCursoLato()) );
	}
	
	public Collection<SelectItem> getPropostaEspecialidade() {
		return propostaEspecialidade;
	}

	public void setPropostaEspecialidade(
			Collection<SelectItem> propostaEspecialidade) {
		this.propostaEspecialidade = propostaEspecialidade;
	}

	public boolean isAndamento() {
		return andamento;
	}

	public void setAndamento(boolean andamento) {
		this.andamento = andamento;
	}

	public String[] getTiposPublicoAlvo() {
		return tiposPublicoAlvo;
	}

	public void setTiposPublicoAlvo(String[] tiposPublicoAlvo) {
		this.tiposPublicoAlvo = tiposPublicoAlvo;
	}

	public boolean isTecnico() {
		return tecnico;
	}

	public void setTecnico(boolean tecnico) {
		this.tecnico = tecnico;
	}

	public boolean isEstrangeiro() {
		return estrangeiro;
	}

	public void setEstrangeiro(boolean estrangeiro) {
		this.estrangeiro = estrangeiro;
	}

	/**
	 * Retorna todas as forma de seleção da proposta
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getFormasSelecaoProposta() throws DAOException {
		return toSelectItems(getGenericDAO().findAll(FormaSelecao.class), "id", "descricao");
	}

	/**
	 * Retorna todas as forma de Avaliação da proposta
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getFormasAvaliacaoProposta() throws DAOException {
		return toSelectItems(getGenericDAO().findAll(FormaAvaliacao.class), "id", "descricao");
	}

	/** 
	 * Direcionar para tela dos docentes do curso 
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/disciplinas.jsp</li>
	 * </ul>
	 */
	public String docentes() {
		corpoDocenteCursoLato = new CorpoDocenteCursoLato();
		corpoDocenteCursoLato.getDocenteExterno().getPessoa().setSexo(PessoaGeral.SEXO_MASCULINO);
		return forward(JSP_DOCENTES_CURSO);
	}
	
	public Collection<SelectItem> getEspecialidades() {
		return especialidades;
	}

	public void setEspecialidades(Collection<SelectItem> especialidades) {
		this.especialidades = especialidades;
	}

	public boolean isNota() {
		return nota;
	}

	public void setNota(boolean nota) {
		this.nota = nota;
	}

	public CorpoDocenteCursoLato getCorpoDocenteCursoLato() {
		return corpoDocenteCursoLato;
	}

	public void setCorpoDocenteCursoLato(CorpoDocenteCursoLato corpoDocenteCursoLato) {
		this.corpoDocenteCursoLato = corpoDocenteCursoLato;
	}

	public boolean getGestorPodeCadastrarProposta(){
		return ParametroHelper.getInstance().getParametroBoolean(ParametrosLatoSensu.PERMITE_GESTOR_CADASTRAR_PROPOSTA_CURSO_LATO);
	}
	
	/**
	 * Retorna o Corpo Docente da Proposta.
	 *  
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/docentes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<CorpoDocenteCursoLato> getCorpoDocenteProposta() throws DAOException {
		if (ValidatorUtil.isEmpty(obj.getCorpoDocenteProposta())) 
			obj.setCorpoDocenteProposta( getallCorpoDocenteProposta() );
		return obj.getCorpoDocenteProposta();
	}

	/**
	 * Carregar o Tipo do Curso Lato Sensu. 
	 *
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/dados_gerais.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 * 
	 * cursoLatoMBean.changeTipoCurso
	 */
	public void changeTipoCurso(ValueChangeEvent e) throws DAOException {
		obj.setTipoCursoLato(getGenericDAO().findByPrimaryKey((Integer)e.getNewValue(), TipoCursoLato.class));
		if (obj.getTipoCursoLato() == null)
			obj.setTipoCursoLato(new TipoCursoLato());
	}

	/**
	 * Carrega a partir da quantidade de vagas oferidas para o Curso a quantidade destinada ao Servidores Internos da Instituição.
	 *
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/dados_gerais.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void changeVagasServidores(ValueChangeEvent e) throws DAOException {
		obj.setNumeroVagas((Integer) e.getNewValue());
		obj.setNumeroVagasServidores((int) ( obj.getNumeroVagas() * 
			((ParametrosPropostaCursoLato) getGenericDAO().findLast(ParametrosPropostaCursoLato.class))
				.getPorcentagemVagasServidores()) / 100 );
	}
	
	/** Retorna uma coleção de unidades de arrecadação.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getUnidadesArrecadacao() throws DAOException {
		UnidadeMBean unidadeMBean = getMBean("unidade");
		return toSelectItems(unidadeMBean.getAllUnidadesOrcamentarias(), "id", "nome");
	}
	
	/** Retorna uma coleção de grupos de emissão de GRU
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getGruposEmissaoGRU() throws DAOException {
		if (gruposEmissaoGRU == null) {
			gruposEmissaoGRU = new ArrayList<SelectItem>();
			GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
			Collection<GrupoEmissaoGRU> lista;
			try {
				 lista = dao.findAllAtivos(GrupoEmissaoGRU.class, "agencia", "codigoCedente", "convenio");
			} catch (HibernateException e) {
				throw new DAOException(e);
			} finally {
				dao.close();
			}
			if (lista != null) {
				for (GrupoEmissaoGRU grupo : lista) {
					gruposEmissaoGRU.add(new SelectItem(grupo.getId(), grupo.toString()));
				}
			}
		}
		return gruposEmissaoGRU;
	}
	
	/** Atualiza na view o grupo de emissão de GRU selecionado.<br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/lato/proposta_curso/configuracao_gru.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws ArqException
	 */
	public void grupoEmissaoListener(ValueChangeEvent evt) throws ArqException {
		String componente = evt.getComponent().getId();
		int id = (Integer) evt.getNewValue();
		if (componente.equalsIgnoreCase("grupoEmissaoGruInscricao"))
			obj.getConfiguracaoGRUInscricao().setGrupoEmissaoGRU(new GrupoEmissaoGRU());
		else if (componente.equalsIgnoreCase("grupoEmissaoGruMensalidade"))
			obj.getConfiguracaoGRUMensalidade().setGrupoEmissaoGRU(new GrupoEmissaoGRU());
		else throw new ArqException("Não foi possível identificar o tipo de configuração.");
		if (id == 0)
			return;
		GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
		GrupoEmissaoGRU grupo = null;
		try {
			grupo = dao.findByPrimaryKey(id, GrupoEmissaoGRU.class);
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			dao.close();
		}
		if (grupo != null) {
			if (componente.equalsIgnoreCase("grupoEmissaoGruInscricao"))
				obj.getConfiguracaoGRUInscricao().setGrupoEmissaoGRU(grupo);
			else if (componente.equalsIgnoreCase("grupoEmissaoGruMensalidade"))
				obj.getConfiguracaoGRUMensalidade().setGrupoEmissaoGRU(grupo);
			else throw new ArqException("Não foi possível identificar o tipo de configuração.");
		}
	}
	
	/** Atualiza na view o tipo de arrecadação selecionado<br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/lato/proposta_curso/configuracao_gru.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws DAOException
	 */
	public void tipoArrecadacaoListener(ValueChangeEvent evt) throws DAOException {
		int id = (Integer) evt.getNewValue();
		GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
		TipoArrecadacao tipoArrecadacao = null;
		try {
			tipoArrecadacao = dao.findByPrimaryKey(id, TipoArrecadacao.class);
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			dao.close();
		}
		if (tipoArrecadacao != null)
			obj.getConfiguracaoGRUInscricao().setTipoArrecadacao(tipoArrecadacao);
		else
			obj.getConfiguracaoGRUInscricao().setTipoArrecadacao(new TipoArrecadacao());
	}
	
	public UploadedFile getArquivoProposta() {
		return arquivoProposta;
	}

	public void setArquivoProposta(UploadedFile arquivoProposta) {
		this.arquivoProposta = arquivoProposta;
	}

	public Collection<CursoLato> getCursosLato() {
		return cursosLato;
	}

	public void setCursosLato(Collection<CursoLato> cursosLato) {
		this.cursosLato = cursosLato;
	}

	public Integer getChAula() {
		return chAula;
	}

	public void setChAula(Integer chAula) {
		this.chAula = chAula;
	}

	public Integer getChLaboratorio() {
		return chLaboratorio;
	}

	public void setChLaboratorio(Integer chLaboratorio) {
		this.chLaboratorio = chLaboratorio;
	}

	public Integer getChEstagio() {
		return chEstagio;
	}

	public void setChEstagio(Integer chEstagio) {
		this.chEstagio = chEstagio;
	}
	
	public String getDescricaoDisciplina() {
		return descricaoDisciplina;
	}

	public void setDescricaoDisciplina(String descricaoDisciplina) {
		this.descricaoDisciplina = descricaoDisciplina;
	}

	public int getIdComponente() {
		return idComponente;
	}

	public void setIdComponente(int idComponente) {
		this.idComponente = idComponente;
	}

	public boolean isNova() {
		return nova;
	}

	public void setNova(boolean nova) {
		this.nova = nova;
	}

	public boolean isVisualizar() {
		return visualizar;
	}

	public void setVisualizar(boolean visualizar) {
		this.visualizar = visualizar;
	}

	public boolean isPossuiGRUInscricao() {
		return possuiGRUInscricao;
	}

	public void setPossuiGRUInscricao(boolean possuiGRUInscricao) {
		this.possuiGRUInscricao = possuiGRUInscricao;
	}

	public boolean isPossuiGRUMensalidade() {
		return possuiGRUMensalidade;
	}

	public void setPossuiGRUMensalidade(boolean possuiGRUMensalidade) {
		this.possuiGRUMensalidade = possuiGRUMensalidade;
	}
	
}