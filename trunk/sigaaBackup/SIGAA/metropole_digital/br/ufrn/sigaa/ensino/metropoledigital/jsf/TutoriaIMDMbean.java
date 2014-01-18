package br.ufrn.sigaa.ensino.metropoledigital.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dao.ensino.HorarioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ead.dominio.VinculoTutor;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.metropoledigital.dao.ComponenteCurricularDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.CronogramaExecucaoAulasDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.HorarioTurmaDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.LancamentoFreqEncontroDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TurmaDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TurmaEntradaTecnicoDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TutorIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TutoriaIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.CronogramaExecucaoAulas;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.DadosTurmaIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.TutorIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.TutoriaIMD;
import br.ufrn.sigaa.ensino.tecnico.dao.ModuloDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.CursoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.EspecializacaoTurmaEntrada;
import br.ufrn.sigaa.ensino.tecnico.dominio.Modulo;
import br.ufrn.sigaa.ensino.tecnico.dominio.OpcaoPoloGrupo;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;
import br.ufrn.sigaa.ensino.util.HorarioTurmaUtil;
import br.ufrn.sigaa.mobile.dto.VinculoDiscenteDTO;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;

/**
 * Bean responsável por gerenciar as turmas e tutorias do Instituto Metrópole
 * Digital - IMD
 * 
 * @author Rafael Silva
 * @author Rafael Barros
 * 
 */

@Scope("session")
@Component("tutoriaIMD")
public class TutoriaIMDMbean extends SigaaAbstractController<TutoriaIMD> {

	/** Coleção de selectItem de cursos do Instituto Metrópole Digital. */
	private List<SelectItem> cursosCombo = new ArrayList<SelectItem>(0);

	/** Coleção de selectItem de módulos */
	private List<SelectItem> modulosCombo = new ArrayList<SelectItem>(0);

	/** Coleção de selectItem de Cronogramas do IMD */
	private List<SelectItem> cronogramasCombo = new ArrayList<SelectItem>(0);

	/** Coleção de selectItem de Tutores do IMD */
	private List<SelectItem> tutoresCombo = new ArrayList<SelectItem>(0);

	/** Coleção de selectItem contendo a opção/grupo das turmas do IMD */
	private List<SelectItem> opcaoPoloGrupoCombo = new ArrayList<SelectItem>();

	/** ID de uma turma selecionada para o edicao e exclusao */
	private TurmaEntradaTecnico turmaSelecionada = new TurmaEntradaTecnico();

	/** Coleção de Tutorias do IMD */
	private List<TutoriaIMD> listaTutorias = new ArrayList<TutoriaIMD>();

	/** Coleção de turmas de entrada do IMD */
	private List<TurmaEntradaTecnico> listaTurmas = new ArrayList<TurmaEntradaTecnico>();

	/** Coleção de disciplinas (turma) que compõem a turma (turma de entrada) **/
	private List<Turma> listaDisciplinas = new ArrayList<Turma>();

	/**
	 * Entidade que corresponde ao curso que será utilizado para pesquisar as
	 * turmas
	 **/
	private CursoTecnico curso;

	/**
	 * Entidade que corresponde ao modulo que será utilizado para pesquisar as
	 * turmas
	 **/
	private Modulo modulo;

	/**
	 * Entidade que corresponde ao cronograma que está vinculado a uma
	 * determinada turma
	 **/
	private CronogramaExecucaoAulas cronograma;

	/**
	 * Entidade que corresponde a opção polo grupo que está vinculada a uma
	 * determinada turma
	 **/
	private OpcaoPoloGrupo opcaoPoloGrupo;

	/**
	 * Tutor antigo de uma turma. Essa entidade será utilizada quando um tutor
	 * for alterado de turma. Servirá para armazenar o histórico das tutorias de
	 * uma turma
	 **/
	private TutorIMD tutorOld = new TutorIMD();

	/**
	 * Tutor novo de uma turma. Essa entidade será utilizada quando um tutor for
	 * alterado de turma. Servirá para armazenar o histórico das tutorias de uma
	 * turma
	 **/
	private TutorIMD tutorNew = new TutorIMD();

	/**
	 * ID do tutor antigo de uma turma. Essa entidade será utilizada quando um
	 * tutor for alterado de turma. Servirá para armazenar o histórico das
	 * tutorias de uma turma
	 **/
	private int idTutorOld;

	/**
	 * Informa se o formulário está sendo utilizando para cadastro ou
	 * não(Alteração)
	 */
	private Boolean cadastro = true;

	/**
	 * Informa se o formulário está sendo direcionado para vincular um tutor a
	 * turma
	 **/
	private boolean vincularTutor = false;

	/** ID de uma turma selecionada para o relatorios */
	private int idTurmaEntradaSelecionada;

	/** Entidade turma selecionada para o relatorios */
	private TurmaEntradaTecnico turmaEntradaSelecionada = new TurmaEntradaTecnico();

	/**
	 * Representa os discentes que pertencem a turma de entrada selecionada para
	 * o relatorio de lista de discentes
	 */
	private Collection<DiscenteTecnico> discentesTurma = new ArrayList<DiscenteTecnico>();

	/**
	 * Representa a quantidade de registros exibidos no relatorio de lista de
	 * discentes
	 **/
	private int qtdDiscentes;
	/** ID do cronograma de execução */
	private int idCronograma;

	// CONSTRUTOR
	public TutoriaIMDMbean() throws DAOException, ArqException {
		vincularTutor = false;
		
		idTutorOld = 0;
		idTurmaEntradaSelecionada = 0;
		qtdDiscentes = 0;
		
		obj = new TutoriaIMD();
		obj.setTurmaEntrada(new TurmaEntradaTecnico());
		obj.getTurmaEntrada().setDadosTurmaIMD(new DadosTurmaIMD());

		obj.setTutor(new TutorIMD());

		curso = new CursoTecnico();
		modulo = new Modulo();
		cronograma = new CronogramaExecucaoAulas();
		opcaoPoloGrupo = new OpcaoPoloGrupo();

		tutoresCombo = toSelectItems(
				getDAO(TutorIMDDao.class).findAll(TutorIMD.class), "id",
				"pessoa.nome");
		
		
		Unidade imd = getGenericDAO().findByPrimaryKey(ParametroHelper.getInstance().getParametroInt(
				ParametrosTecnico.ID_UNIDADE_INSTITUTO_METROPOLE_DIGITAL), Unidade.class);
		
		//cursosCombo = toSelectItems(cursoDao.findByNivel(getNivelEnsino(), true, null, new Unidade(getUnidadeGestora())), "id", "descricao");
		cursosCombo = toSelectItems(getDAO(CursoDao.class).findByNivel(getNivelEnsino(), true, null, imd), "id", "descricao");
		
		
		opcaoPoloGrupoCombo = toSelectItems(getDAO(GenericSigaaDAO.class)
				.findAll(OpcaoPoloGrupo.class), "id", "descricao");
	}

	/**
	 * Limpa os dados dos objetos salvos em sessão.
	 */
	public void clear() {
		obj = new TutoriaIMD();
		obj.setTurmaEntrada(new TurmaEntradaTecnico());
		obj.getTurmaEntrada().setDadosTurmaIMD(new DadosTurmaIMD());
		// obj.getTurmaEntrada().setEspecializacao(new
		// EspecializacaoTurmaEntrada());
		obj.setTutor(new TutorIMD());

		curso = new CursoTecnico();
		modulo = new Modulo();
		cronograma = new CronogramaExecucaoAulas();
		opcaoPoloGrupo = new OpcaoPoloGrupo();
		idCronograma = 0;

		listaDisciplinas = Collections.emptyList();
		listaTurmas = Collections.emptyList();
		modulosCombo = Collections.emptyList();
		cronogramasCombo = Collections.emptyList();
		cadastro = true;
	}

	// ROTINAS DE NAVEGAÇÃO;
	/**
	 * Efetua um pré-carregamento das informações necessárias para se realizar a
	 * alteração de uma turma. Preenchimento dos campos da turma.
	 * 
	 * @param
	 * @return JSP que o sistema será redirecionado
	 * @throws DAOException
	 */
	public String preAlterar() throws DAOException {
		try {
			cadastro = false;
			vincularTutor = false;

			turmaSelecionada = getDAO(TurmaEntradaTecnicoDao.class)
					.findByPrimaryKey((Integer) getParameterInt("id"),
							TurmaEntradaTecnico.class);

			if (turmaSelecionada == null || getParameterInt("id") == 0) {
				addMessage(
						"A Turma selecionada não pode ser alterada, pois, a mesma não encontra-se mais disponível no banco de dados. ",
						TipoMensagemUFRN.ERROR);
			}

			if (!hasErrors()) {
				obj.setTurmaEntrada(turmaSelecionada);
				cronograma = turmaSelecionada.getDadosTurmaIMD()
						.getCronograma();
				modulo = cronograma.getModulo();
				opcaoPoloGrupo = turmaSelecionada.getOpcaoPoloGrupo();
				carregarCronogramas(modulo.getId());

				// carrega as turmas cadastradas para a tutoria
				// listaDisciplinas =
				// getDAO(TurmaDao.class).findByEspecializacao(
				// turmaSelecionada.getEspecializacao().getId());

				List<Turma> lista = new ArrayList<Turma>();
				List<ComponenteCurricular> listaComponentes = getDAO(
						ComponenteCurricularDao.class).getByModulo(
						modulo.getId());

				if (obj.getId() == 0) {
					for (ComponenteCurricular cc : listaComponentes) {
						Turma t = new Turma();
						t.setAno(cronograma.getAno());// ano
						t.setPeriodo(cronograma.getPeriodo());// Período
						t.setCapacidadeAluno(obj.getTurmaEntrada()
								.getCapacidade());// Capacidade
						t.setLocal(obj.getTurmaEntrada().getDadosTurmaIMD()
								.getLocal());// Local
						t.setDataInicio(cronograma.getDataInicio());// Data
																	// Inicio
						t.setDataFim(cronograma.getDataFim());// Data Fim
						t.setDisciplina(cc);// Disciplina
						t.setCodigo(obj.getTurmaEntrada().getEspecializacao()
								.getDescricao());// Código
						t.setTipo(1);// Tipo de turma Regular
						t.setEspecializacao(obj.getTurmaEntrada()
								.getEspecializacao());// Especialização da Turma
														// de
														// Entrada
						lista.add(t);
					}
				}

				listaDisciplinas = lista;

				return forward("/metropole_digital/tutoria/form.jsf");
			}
			return redirect("/metropole_digital/tutoria/lista.jsf");

		} finally {
			getDAO(TurmaEntradaTecnicoDao.class).close();
		}

	}

	/**
	 * Efetua um pré-carregamento das informações necessárias para se realizar o
	 * vínculo de um tutor a turma. Preenchimento dos campos da turma.
	 * 
	 * @param
	 * @return JSP que o sistema será redirecionado
	 * @throws DAOException
	 */
	public String preVincularTutor() throws DAOException {
		TutoriaIMDDao tutoriaDao = new TutoriaIMDDao();
		try {
			cadastro = false;

			TutoriaIMD tutoria = tutoriaDao
					.findUltimaByTurmaEntrada(getParameterInt("id"));

			turmaSelecionada = getDAO(TurmaEntradaTecnicoDao.class)
					.findByPrimaryKey((Integer) getParameterInt("id"),
							TurmaEntradaTecnico.class);

			if (turmaSelecionada == null || getParameterInt("id") == 0) {
				addMessage(
						"O tutor não pode ser vinculado, pois, a turma selecionada não encontra-se mais disponível no banco de dados. ",
						TipoMensagemUFRN.ERROR);
			}
				
				ArrayList<TutorIMD> listaTutores = new ArrayList<TutorIMD>();
				listaTutores = (ArrayList<TutorIMD>) getDAO(TutorIMDDao.class).findAll(TutorIMD.class);
				
				ArrayList<TutorIMD> listaTutoresAux = new ArrayList<TutorIMD>();
				
				Date dataAtual = new Date();
				
				for(TutorIMD tut: listaTutores){
					if((tut.getDataFim().after(dataAtual)) || (! tut.getDataFim().after(dataAtual) && !(tut.getDataFim().before(dataAtual)))){
						listaTutoresAux.add(tut);
					}
				}
				tutoresCombo = toSelectItems(listaTutoresAux, "id", "pessoa.nome");
				

			if (!hasErrors()) {
				if (tutoria != null) {

					obj = tutoria;
					cronograma = obj.getTurmaEntrada().getDadosTurmaIMD()
							.getCronograma();
					modulo = cronograma.getModulo();
					opcaoPoloGrupo = obj.getTurmaEntrada().getOpcaoPoloGrupo();
					carregarCronogramas(modulo.getId());
					// carrega as turmas cadastradas para a tutoria
					listaDisciplinas = getDAO(TurmaDao.class)
							.findByEspecializacao(
									obj.getTurmaEntrada().getEspecializacao()
											.getId());
					tutorOld = obj.getTutor();
					idTutorOld = obj.getTutor().getId();

				} else {
					obj.setTurmaEntrada(turmaSelecionada);
					cronograma = turmaSelecionada.getDadosTurmaIMD()
							.getCronograma();
					modulo = cronograma.getModulo();
					opcaoPoloGrupo = turmaSelecionada.getOpcaoPoloGrupo();
					carregarCronogramas(modulo.getId());
					// carrega as turmas cadastradas para a tutoria
					listaDisciplinas = getDAO(TurmaDao.class)
							.findByEspecializacao(
									turmaSelecionada.getEspecializacao()
											.getId());
				}
				vincularTutor = true;
				return forward("/metropole_digital/tutoria/form_tutor.jsf");
			}
			return redirect("/metropole_digital/tutoria/lista.jsf");

		} finally {
			getDAO(TurmaEntradaTecnicoDao.class).close();
			tutoriaDao.close();
		}
	}

	/**
	 * Redireciona o usuário para a pagina de cadastro de tutorias do IMD
	 * 
	 * @param
	 * @return JSP que o sistema será redirecionado
	 * @throws
	 */
	public String preCadastrar() {
		clear();
		return forward("/metropole_digital/tutoria/form.jsf");
	}

	/**
	 * Redireciona o usuário para a pagina listagem das turmas IMD
	 * 
	 * @param
	 * @return JSP que o sistema será redirecionado
	 * @throws
	 */
	public String selecionarTurma() {
		return forward("/metropole_digital/relatorios/selecao_turma.jsf");
	}

	/**
	 * Redireciona o usuário para a pagina de listagem/alteração de tutorias do
	 * IMD
	 * 
	 * @param
	 * @return JSP que o sistema será redirecionado
	 * @throws
	 */
	public String preListar() {
		clear();
		return forward("/metropole_digital/tutoria/lista.jsf");
	}

	/**
	 * Efetua o processamento das informações para se alterar uma turma
	 * 
	 * @param
	 * @return JSP que o sistema será redirecionado
	 * @throws ArqException
	 */
	public String alterar() throws ArqException {
		if (!vincularTutor) {
			validaFomulario();
		} else {
			validaFomularioTutor();
		}
		if (!hasErrors()) {
			if (!vincularTutor) {
				salvar();
			} else {
				return salvarTutor();
			}
			addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Turma");
			listaTutorias = getDAO(TutoriaIMDDao.class).findByModulo(
					modulo.getId());
			listarTurmas();
			return redirect("/metropole_digital/tutoria/lista.jsf");
		}
		return null;
	}

	/**
	 * Efetua o processamento das informações para se cadastrar uma turma
	 * 
	 * @param
	 * @return JSP que o sistema será redirecionado
	 * @throws ArqException
	 */
	public String cadastrar() throws ArqException {
		validaFomulario();
		if (!hasErrors()) {
			salvar();
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Turma");
			listaTutorias = getDAO(TutoriaIMDDao.class).findByModulo(
					modulo.getId());
			// forward("/metropole_digital/menus/menu_imd.jsf");
			clear();
		}
		return null;
	}

	/**
	 * Efetua o processamento das informações para se visualizar uma turma
	 * 
	 * @param
	 * @return JSP que o sistema será redirecionado
	 * @throws DAOException
	 */
	public String visualizar() throws DAOException {
		cadastro = false;
		obj = getDAO(TutoriaIMDDao.class).findById(
				(Integer) getParameterInt("id"));
		cronograma = obj.getTurmaEntrada().getDadosTurmaIMD().getCronograma();
		modulo = cronograma.getModulo();
		opcaoPoloGrupo = obj.getTurmaEntrada().getOpcaoPoloGrupo();
		carregarCronogramas(modulo.getId());
		// carrega as turmas cadastradas para a tutoria
		listaDisciplinas = getDAO(TurmaDao.class).findByEspecializacao(
				obj.getTurmaEntrada().getEspecializacao().getId());

		return forward("/metropole_digital/tutoria/view.jsf");
	}

	/**
	 * Cancela a operação e retorna para a página principal do módulo.
	 * 
	 * @param
	 * @return JSP que o sistema será redirecionado
	 * @throws
	 */
	public String cancelar() {
		clear();
		return super.cancelar();
	}

	/**
	 * Cancela a operação e redireciona para a página de listagem de tutorias;
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String cancelarAlteracao() {
		return forward("/metropole_digital/tutoria/lista.jsf");
	}

	/**
	 * Redireciona a página ao cancelar o vinculo de tutor
	 * 
	 * @param
	 * @return JSP que o sistema será redirecionado
	 * @throws
	 */
	public String cancelarVinculo() {
		obj.setTutor(new TutorIMD());
		return forward("/metropole_digital/tutoria/lista.jsf");
	}

	/**
	 * Lista as turmas cadastradas de acordo com os critérios de pesquisa
	 * 
	 * @param
	 * @return JSP que o sistema será redirecionado
	 * @throws DAOException
	 */
	public String listarTurmas() throws DAOException {
		if (modulo.getId() != 0) {
			listaTurmas = getDAO(TutoriaIMDDao.class).findTurmasByModulo(
					modulo.getId());
			if (listaTurmas.isEmpty()) {
				addMensagemErro("Nenhum registro encontrado de acordo com os critérios de busca informados.");
			}
		} else {
			if (curso.getId() > 0) {
				listaTurmas = getDAO(TutoriaIMDDao.class).findTurmasByCurso(
						curso.getId());
				if (listaTurmas.isEmpty()) {
					addMensagemErro("Nenhum registro encontrado de acordo com os critérios de busca informados.");
				}
			} else {
				addMensagem(
						MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,
						"Curso");
			}
		}
		//Collections.sort(listaTurmas);
		return null;
	}

	/**
	 * Lista as tutorias cadastradas de acordo com os critérios de pesquisa
	 * 
	 * @param
	 * @return JSP que o sistema será redirecionado
	 * @throws DAOException
	 */
	public String listar() throws DAOException {
		if (modulo.getId() != 0) {
			listaTutorias = getDAO(TutoriaIMDDao.class).findByModulo(
					modulo.getId());

		} else {
			if (curso.getId() > 0) {
				listaTutorias = getDAO(TutoriaIMDDao.class).findByCurso(
						curso.getId());
			} else {
				addMensagem(
						MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,
						"Curso");
			}
		}
		Collections.sort(listaTutorias);
		return null;
	}

	/**
	 * Efetua o processamento das informações para se remover uma turma
	 * 
	 * @param
	 * @return JSP que o sistema será redirecionado
	 * @throws DAOException
	 */
	public String remover() throws DAOException {
		TurmaEntradaTecnicoDao turmaDao = getDAO(TurmaEntradaTecnicoDao.class);
		try {
			TurmaEntradaTecnico turma = turmaDao.findByPrimaryKey(
					getParameterInt("id"), TurmaEntradaTecnico.class);
			if (turma == null) {
				addMessage(
						"A Turma Selecionada não pode ser removida, pois, a mesma não encontra-se mais disponível no banco de dados. ",
						TipoMensagemUFRN.ERROR);

			}

			if (!hasErrors()) {
				turmaDao.remove(turma);
				turmaDao.remove(turma.getDadosTurmaIMD());
				addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO,
						"Turma");
				listarTurmas();
			}

		} finally {
			turmaDao.close();
		}

		return redirect("/metropole_digital/tutoria/lista.jsf");
	}

	// MÉTODOS AUXILIARES
	/**
	 * Persiste a entidade TurmaEntrada
	 * 
	 * @param
	 * @return
	 * @throws ArqException
	 */
	public void salvar() throws ArqException {

		opcaoPoloGrupo = getDAO(GenericSigaaDAO.class).findByPrimaryKey(
				opcaoPoloGrupo.getId(), OpcaoPoloGrupo.class);

		getDAO(TutoriaIMDDao.class).createOrUpdate(
				obj.getTurmaEntrada().getEspecializacao());

		/* Criação do codigo de integração entre o SIGAA e o moodle */
		DadosTurmaIMD dadosTurma = new DadosTurmaIMD();
		dadosTurma = obj.getTurmaEntrada().getDadosTurmaIMD();

		/* Dados Turma IMD */
		getDAO(TutoriaIMDDao.class).createOrUpdate(dadosTurma);

		/* Turma Entrada */
		getDAO(TutoriaIMDDao.class).createOrUpdate(obj.getTurmaEntrada());

		String codigoIntegracao = "T" + obj.getTurmaEntrada().getId();
		dadosTurma.setCodigoIntegracao(codigoIntegracao);

		/* Dados Turma IMD */
		getDAO(TutoriaIMDDao.class).createOrUpdate(dadosTurma);

		List<Turma> lista = new ArrayList<Turma>();
		
		for (Turma t : listaDisciplinas) {
			t.setCapacidadeAluno(obj.getTurmaEntrada().getCapacidade());// Capacidade
			t.setLocal(obj.getTurmaEntrada().getDadosTurmaIMD().getLocal());// Local
			t.setCodigo(obj.getTurmaEntrada().getEspecializacao()
					.getDescricao());// Código
			t.setTipo(1);// Tipo de turma Regular
			t.setEspecializacao(obj.getTurmaEntrada().getEspecializacao());// Especialização
																			// da
																			// Turma
																			// de
																			// Entrada

			lista.add(t);
		}
		listaDisciplinas = lista;

		/* Tutoria IMD */
		// getDAO(TutoriaIMDDao.class).createOrUpdate(obj);

		/* Cria as turmas do Metrópole digital */
		if (cadastro) {
			salvarTurmasComponente();
		}
		
		// salvarHorariosTurmasComponentes();

	}

	/**
	 * Persiste a entidade Tutoria efetuando o vínculo com a TurmaEntrada ou
	 * efetuando uma alteração
	 * 
	 * @param
	 * @return
	 * @throws ArqException
	 */
	public String salvarTutor() throws ArqException {

		TutorIMDDao tDao = new TutorIMDDao();
		TutoriaIMDDao tutoriaDao = new TutoriaIMDDao();
		try {

			TutoriaIMD tutoriaAtiva = new TutoriaIMD();
			tutoriaAtiva = tutoriaDao.existeTutoriaAtiva(obj.getTurmaEntrada().getId());
			
			if(tutoriaAtiva != null) {
				idTutorOld = tutoriaAtiva.getTutor().getId();
			}			
			
			if (idTutorOld > 0 && idTutorOld == obj.getTutor().getId()) {
				addMessage("É necessário selecionar um tutor diferente do tutor atual.", TipoMensagemUFRN.ERROR);
				return forward("/metropole_digital/tutoria/form_tutor.jsf");
			} else {

				if (!tutoriaDao.existeTutoria(obj.getTurmaEntrada().getId(), obj.getTutor().getId())) {
					opcaoPoloGrupo = getDAO(GenericSigaaDAO.class).findByPrimaryKey(opcaoPoloGrupo.getId(),	OpcaoPoloGrupo.class);

					getDAO(TutoriaIMDDao.class).createOrUpdate(obj.getTurmaEntrada().getEspecializacao());

					/* Criação do codigo de integração entre o SIGAA e o moodle */
					DadosTurmaIMD dadosTurma = new DadosTurmaIMD();
					dadosTurma = obj.getTurmaEntrada().getDadosTurmaIMD();

					/* Dados Turma IMD */
					getDAO(TutoriaIMDDao.class).createOrUpdate(dadosTurma);

					/* Turma Entrada */
					getDAO(TutoriaIMDDao.class).createOrUpdate(obj.getTurmaEntrada());

					String codigoIntegracao = "T"
							+ obj.getTurmaEntrada().getId();
					dadosTurma.setCodigoIntegracao(codigoIntegracao);

					/* Dados Turma IMD */
					getDAO(TutoriaIMDDao.class).createOrUpdate(dadosTurma);

					List<Turma> lista = new ArrayList<Turma>();
					for (Turma t : listaDisciplinas) {
						t.setCapacidadeAluno(obj.getTurmaEntrada().getCapacidade());// Capacidade
						t.setLocal(obj.getTurmaEntrada().getDadosTurmaIMD().getLocal());// Local
						t.setCodigo(obj.getTurmaEntrada().getEspecializacao().getDescricao());// Código
						t.setTipo(1);// Tipo de turma Regular
						t.setEspecializacao(obj.getTurmaEntrada().getEspecializacao());// Especialização da Turma de Entrada

						lista.add(t);
					}
					listaDisciplinas = lista;

					if (idTutorOld > 0) {

						TutoriaIMD novaTutoria = new TutoriaIMD();
						novaTutoria.setTurmaEntrada(obj.getTurmaEntrada());
						novaTutoria.setTutor(obj.getTutor());

						getDAO(TutoriaIMDDao.class).createOrUpdate(novaTutoria);

						TutorIMD tutorAntigo = new TutorIMD();
						tutorAntigo = tDao.findByPrimaryKey(idTutorOld,
								TutorIMD.class);

						obj.setTutor(tutorAntigo);
						obj.setDataFimTutoria(new Date());

						addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO,
								"Tutor");
					} else {
						addMensagem(
								MensagensArquitetura.CADASTRADO_COM_SUCESSO,
								"Tutor");
					}

					
					/* Tutoria IMD */
					if(tutoriaAtiva != null) {
						tutoriaAtiva.setDataFimTutoria(new Date());
						getDAO(TutoriaIMDDao.class).create(tutoriaAtiva);
					} else {
						getDAO(TutoriaIMDDao.class).create(obj);
					}
					

					/* Cria as turmas do Metrópole digital */
					// salvarTurmasComponente();
					// salvarHorariosTurmasComponentes();

					listaTutorias = getDAO(TutoriaIMDDao.class).findByModulo(
							modulo.getId());
					return forward("/metropole_digital/tutoria/lista.jsf");
				} else {
					addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO,
							"Tutor");

					listaTutorias = getDAO(TutoriaIMDDao.class).findByModulo(
							modulo.getId());
					return forward("/metropole_digital/tutoria/lista.jsf");
				}

			}
		} finally {
			tDao.close();
			tutoriaDao.close();
		}

	}

	/**
	 * Cria uma Turma Componente para cada disciplina vinculada ao módulo do
	 * cronograma cadastrado para a Turma de Entrada.
	 * 
	 * @param
	 * @return
	 * @throws ArqException
	 */
	public void salvarTurmasComponente() throws ArqException {
		TurmaDao turmaDao = getDAO(TurmaDao.class);
		if (vincularTutor) {
			
		}
		for (Turma t : listaDisciplinas) {
			SituacaoTurma situacao = new SituacaoTurma(1);
			t.setSituacaoTurma(situacao);
			turmaDao.createOrUpdate(t);
		}
	}

	/**
	 * Valida o formulário de cadastro de turmas
	 * 
	 * @param
	 * @return
	 * @throws DAOException
	 *             , ArqException
	 */
	public void validaFomulario() throws DAOException, ArqException {
		/* Validade Horário */
		HorarioDao dao = getDAO(HorarioDao.class);
		List<HorarioTurma> horarios = null;
		try {
			horarios = HorarioTurmaUtil.parseCodigoHorarios(obj
					.getTurmaEntrada().getDadosTurmaIMD().getHorario(),
					getUnidadeGestora(), getNivelEnsino(), dao);
		} finally {
			// verifica se os horário foi totalmente convertido
			/* Valida Curso */
			if (curso == null || curso.getId() == 0)
				erros.addErro("Curso:  Campo obrigatório não informado.");

			/* Valida Módulo */
			if (modulo == null || modulo.getId() == 0)
				erros.addErro("Módulo:  Campo obrigatório não informado.");

			/* Validad Cronograma */
			if (cronograma == null || cronograma.getId() == 0)
				erros.addErro("Cronograma:  Campo obrigatório não informado.");

			/* Valida Polo Grupo */
			if (opcaoPoloGrupo == null || opcaoPoloGrupo.getId() == 0)
				erros.addErro("Polo/Grupo:  Campo obrigatório não informado.");

			/* Valida Turma */
			if (obj.getTurmaEntrada().getEspecializacao().getDescricao() == null
					|| obj.getTurmaEntrada().getEspecializacao().getDescricao()
							.equals(""))
				erros.addErro("Nome da Turma:  Campo obrigatório não informado.");

			/* Valida Disciplinas */
			if (listaDisciplinas == null || listaDisciplinas.size() == 0)
				erros.addErro("Disciplinas não vinculadas.");

			if (obj.getTurmaEntrada().getDadosTurmaIMD().getHorario() == null
					|| obj.getTurmaEntrada().getDadosTurmaIMD().getHorario()
							.equals("")) {
				erros.addErro("Horário: Campo obrigatório não informado.");
			} else {
				if (isEmpty(horarios)) {
					erros.addErro("Expressão de horário inválida.");
					return;
				}
			}

		}

	}

	/**
	 * Valida o formulário de cadastro de turmas e os dados da tutoria
	 * 
	 * @param
	 * @return
	 * @throws DAOException
	 *             , ArqException
	 */
	public void validaFomularioTutor() throws DAOException, ArqException {
		/* Validade Horário */
		HorarioDao dao = getDAO(HorarioDao.class);
		List<HorarioTurma> horarios = null;
		try {
			horarios = HorarioTurmaUtil.parseCodigoHorarios(obj
					.getTurmaEntrada().getDadosTurmaIMD().getHorario(),
					getUnidadeGestora(), getNivelEnsino(), dao);
		} finally {
			// verifica se os horário foi totalmente convertido
			if (isEmpty(horarios)) {
				addMensagemErro("Expressão de horário inválida.");
				return;
			}
		}

		/* Valida Curso */
		if (curso == null || curso.getId() == 0)
			erros.addErro("Curso:  Campo obrigatório não informado.");

		/* Valida Módulo */
		if (modulo == null || modulo.getId() == 0)
			erros.addErro("Modulo:  Campo obrigatório não informado.");

		/* Validad Cronograma */
		if (cronograma == null || cronograma.getId() == 0)
			erros.addErro("Cronograma:  Campo obrigatório não informado.");

		/* Valida Polo Grupo */
		if (opcaoPoloGrupo == null || opcaoPoloGrupo.getId() == 0)
			erros.addErro("Polo/Grupo:  Campo obrigatório não informado.");

		/* Valida Turma */
		if (obj.getTurmaEntrada().getDescricao() == null
				|| obj.getTurmaEntrada().getDescricao().equals(""))
			erros.addErro("Nome da Turma:  Campo obrigatório não informado.");

		/* Valida Tutor */
		if (obj.getTutor() == null || obj.getTutor().getId() == 0)
			erros.addErro("Tutor:  Campo obrigatório não informado.");

		/* Valida Disciplinas */
		if (listaDisciplinas == null || listaDisciplinas.size() == 0)
			erros.addErro("Disciplinas não vinculadas.");

	}

	/**
	 * Carrega os Cronogramas do módulo informado
	 * 
	 * @param idModulo
	 * @return
	 * @throws DAOException
	 */
	private void carregarCronogramas(int idModulo) throws DAOException {
		CronogramaExecucaoAulasDao cronogramaDao = getDAO(CronogramaExecucaoAulasDao.class);
		cronogramasCombo = toSelectItems(
				cronogramaDao.findCronograma(idModulo, null, null), "id",
				"descricao");
	}

	/**
	 * Vincula as disciplinas (Turma) no momento de um cadastro ou alteração de
	 * uma Turma (TurmaEntrada)
	 * 
	 * @param e
	 * @return
	 * @throws DAOException
	 */
	public void vincularDisciplinas(ValueChangeEvent e) throws DAOException {
		List<Turma> lista = new ArrayList<Turma>();
		if ((Integer) e.getNewValue() == 0) {
			lista = Collections.emptyList();
		} else {
			List<ComponenteCurricular> listaComponentes = getDAO(
					ComponenteCurricularDao.class).getByModulo(modulo.getId());

			curso = getDAO(GenericSigaaDAO.class).findByPrimaryKey(
					curso.getId(), CursoTecnico.class);
			cronograma = getDAO(CronogramaExecucaoAulasDao.class)
					.findByPrimaryKey((Integer) e.getNewValue(),
							CronogramaExecucaoAulas.class);
			modulo = getDAO(GenericSigaaDAO.class).findByPrimaryKey(
					modulo.getId(), Modulo.class);

			/* Especialização Turma de Entrada! */
			obj.getTurmaEntrada()
					.getEspecializacao()
					.setUnidade(
							getUsuarioLogado().getVinculoAtivo().getUnidade());

			/* Dados Turma IMD */
			obj.getTurmaEntrada().getDadosTurmaIMD().setCronograma(cronograma);

			/* Turma Entrada */
			obj.getTurmaEntrada().setAnoReferencia(cronograma.getAno());
			obj.getTurmaEntrada().setPeriodoReferencia(cronograma.getPeriodo());
			obj.getTurmaEntrada().setCursoTecnico(curso);
			obj.getTurmaEntrada().setUnidade(
					getUsuarioLogado().getVinculoAtivo().getUnidade());
			obj.getTurmaEntrada().setOpcaoPoloGrupo(opcaoPoloGrupo);
			obj.getTurmaEntrada().setDataEntrada(cronograma.getDataInicio());

			if (obj.getId() == 0) {
				for (ComponenteCurricular cc : listaComponentes) {
					Turma t = new Turma();
					t.setAno(cronograma.getAno());// ano
					t.setPeriodo(cronograma.getPeriodo());// Período
					t.setCapacidadeAluno(obj.getTurmaEntrada().getCapacidade());// Capacidade
					t.setLocal(obj.getTurmaEntrada().getDadosTurmaIMD()
							.getLocal());// Local
					t.setDataInicio(cronograma.getDataInicio());// Data Inicio
					t.setDataFim(cronograma.getDataFim());// Data Fim
					t.setDisciplina(cc);// Disciplina
					t.setCodigo(obj.getTurmaEntrada().getEspecializacao()
							.getDescricao());// Código
					t.setTipo(1);// Tipo de turma Regular
					t.setEspecializacao(obj.getTurmaEntrada()
							.getEspecializacao());// Especialização da Turma de
													// Entrada
					lista.add(t);
				}
			}
		}
		listaDisciplinas = lista;
	}

	// AJAX:
	/**
	 * Carrega os módulos do curso escolhido
	 * 
	 * @param e
	 * @return
	 * @throws DAOException
	 */
	public void carregarModulos(ValueChangeEvent e) throws DAOException {
		if ((Integer) e.getNewValue() == 0) {
			modulosCombo = Collections.emptyList();
		} else {
			curso.setId((Integer) e.getNewValue());
			modulosCombo = toSelectItems(
					getDAO(CronogramaExecucaoAulasDao.class)
							.findByCursoTecnico((Integer) e.getNewValue()),
					"id", "descricao");
		}
		listaDisciplinas = Collections.emptyList();
		cronogramasCombo = Collections.emptyList();
	}

	/**
	 * Carrega os Cronogramas do módulo informado
	 * 
	 * @param e
	 * @return
	 * @throws DAOException
	 */
	public void carregarCronogramas(ValueChangeEvent e) throws DAOException {
		if ((Integer) e.getNewValue() == 0) {
			cronogramasCombo = Collections.emptyList();
		} else {
			modulo.setId((Integer) e.getNewValue());
			carregarCronogramas(modulo.getId());
		}
		listaDisciplinas = Collections.emptyList();
	}

	/**
	 * Efetua o preenchimento dos dados do relatorio e faz o redirecionamento da
	 * pagina
	 * 
	 * @param
	 * @return página na qual a JSP sera redirecionada
	 * @throws DAOException
	 */
	public String gerarRelatorioListaDiscentes() throws DAOException {
		int id = getParameterInt("id");

		if (id > 0) {
			idTurmaEntradaSelecionada = id;
		}

		turmaEntradaSelecionada = getGenericDAO().findByPrimaryKey(
				idTurmaEntradaSelecionada, TurmaEntradaTecnico.class);
		findDiscentesTurma();
		return forward("/metropole_digital/relatorios/relatorio_discentes_turma.jsp");
	}

	/**
	 * Efetua o preenchimento dos dados dos discentes da turma selecionada
	 * 
	 * @param
	 * @return
	 * @throws DAOException
	 */
	public void findDiscentesTurma() throws DAOException {

		LancamentoFreqEncontroDao lancamentoFreqDao = new LancamentoFreqEncontroDao();

		try {
			discentesTurma = lancamentoFreqDao
					.findDiscentesByTurmaEntrada(idTurmaEntradaSelecionada);
			qtdDiscentes = discentesTurma.size();
		} finally {
			lancamentoFreqDao.close();
		}
	}

	// GETTERS AND SETTERS
	public List<SelectItem> getCursosCombo() {
		return cursosCombo;
	}

	public void setCursosCombo(List<SelectItem> cursosCombo) {
		this.cursosCombo = cursosCombo;
	}

	public List<SelectItem> getModulosCombo() {
		return modulosCombo;
	}

	public void setModulosCombo(List<SelectItem> modulosCombo) {
		this.modulosCombo = modulosCombo;
	}

	public List<SelectItem> getCronogramasCombo() {
		return cronogramasCombo;
	}

	public void setCronogramasCombo(List<SelectItem> cronogramasCombo) {
		this.cronogramasCombo = cronogramasCombo;
	}

	public List<SelectItem> getTutoresCombo() {
		return tutoresCombo;
	}

	public void setTutoresCombo(List<SelectItem> tutoresCombo) {
		this.tutoresCombo = tutoresCombo;
	}

	public List<SelectItem> getOpcaoPoloGrupoCombo() {
		return opcaoPoloGrupoCombo;
	}

	public void setOpcaoPoloGrupoCombo(List<SelectItem> opcaoPoloGrupoCombo) {
		this.opcaoPoloGrupoCombo = opcaoPoloGrupoCombo;
	}

	public List<TutoriaIMD> getListaTutorias() {
		return listaTutorias;
	}

	public void setListaTutorias(List<TutoriaIMD> listaTutorias) {
		this.listaTutorias = listaTutorias;
	}

	public List<Turma> getListaDisciplinas() {
		return listaDisciplinas;
	}

	public void setListaDisciplinas(List<Turma> listaDisciplinas) {
		this.listaDisciplinas = listaDisciplinas;
	}

	public CursoTecnico getCurso() {
		return curso;
	}

	public void setCurso(CursoTecnico curso) {
		this.curso = curso;
	}

	public Modulo getModulo() {
		return modulo;
	}

	public void setModulo(Modulo modulo) {
		this.modulo = modulo;
	}

	public CronogramaExecucaoAulas getCronograma() {
		return cronograma;
	}

	public void setCronograma(CronogramaExecucaoAulas cronograma) {
		this.cronograma = cronograma;
	}

	public OpcaoPoloGrupo getOpcaoPoloGrupo() {
		return opcaoPoloGrupo;
	}

	public void setOpcaoPoloGrupo(OpcaoPoloGrupo opcaoPoloGrupo) {
		this.opcaoPoloGrupo = opcaoPoloGrupo;
	}

	public Boolean getCadastro() {
		return cadastro;
	}

	public void setCadastro(Boolean cadastro) {
		this.cadastro = cadastro;
	}

	public List<TurmaEntradaTecnico> getListaTurmas() {
		return listaTurmas;
	}

	public void setListaTurmas(List<TurmaEntradaTecnico> listaTurmas) {
		this.listaTurmas = listaTurmas;
	}

	public TurmaEntradaTecnico getTurmaSelecionada() {
		return turmaSelecionada;
	}

	public void setTurmaSelecionada(TurmaEntradaTecnico turmaSelecionada) {
		this.turmaSelecionada = turmaSelecionada;
	}

	public TutorIMD getTutorOld() {
		return tutorOld;
	}

	public void setTutorOld(TutorIMD tutorOld) {
		this.tutorOld = tutorOld;
	}

	public TutorIMD getTutorNew() {
		return tutorNew;
	}

	public void setTutorNew(TutorIMD tutorNew) {
		this.tutorNew = tutorNew;
	}

	public boolean isVincularTutor() {
		return vincularTutor;
	}

	public void setVincularTutor(boolean vincularTutor) {
		this.vincularTutor = vincularTutor;
	}

	public int getIdTutorOld() {
		return idTutorOld;
	}

	public void setIdTutorOld(int idTutorOld) {
		this.idTutorOld = idTutorOld;
	}

	public int getIdTurmaEntradaSelecionada() {
		return idTurmaEntradaSelecionada;
	}

	public void setIdTurmaEntradaSelecionada(int idTurmaEntradaSelecionada) {
		this.idTurmaEntradaSelecionada = idTurmaEntradaSelecionada;
	}

	public TurmaEntradaTecnico getTurmaEntradaSelecionada() {
		return turmaEntradaSelecionada;
	}

	public void setTurmaEntradaSelecionada(
			TurmaEntradaTecnico turmaEntradaSelecionada) {
		this.turmaEntradaSelecionada = turmaEntradaSelecionada;
	}

	/**
	 * Método get do atributo discentesTurma que também efetua o preenchimento
	 * do campo caso o mesmo esteja vazio
	 * 
	 * @param
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteTecnico> getDiscentesTurma() throws DAOException {
		if (discentesTurma.isEmpty()) {
			findDiscentesTurma();
		}
		return discentesTurma;
	}

	public void setDiscentesTurma(Collection<DiscenteTecnico> discentesTurma) {
		this.discentesTurma = discentesTurma;
	}

	public int getQtdDiscentes() {
		return qtdDiscentes;
	}

	public void setQtdDiscentes(int qtdDiscentes) {
		this.qtdDiscentes = qtdDiscentes;
	}

	public int getIdCronograma() {
		return idCronograma;
	}

	public void setIdCronograma(int idCronograma) {
		this.idCronograma = idCronograma;
	}

}
