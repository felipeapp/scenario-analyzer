package br.ufrn.sigaa.ensino.metropoledigital.jsf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.notificacoes.Destinatario;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.jsf.NotificacoesMBean;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.metropoledigital.dao.AcompanhamentoSemanalDiscenteDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.CoordenadorTutorIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.CronogramaExecucaoAulasDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.RelatoriosCoordenadorTutoresIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TurmaEntradaTecnicoDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TutorIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TutoriaIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.AcompanhamentoSemanalDiscente;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.CronogramaExecucaoAulas;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.DadosTurmaIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.PeriodoAvaliacao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.RelatorioAcompanhamentoExecucaoFreqNotas;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.RetornoNotaPVIntegracaoMoodle;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.TutorIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.TutoriaIMD;
import br.ufrn.sigaa.ensino.metropoledigital.negocio.MovimentoCriacaoRegistrosAcompanhamento;
import br.ufrn.sigaa.ensino.tecnico.dominio.CursoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.Modulo;
import br.ufrn.sigaa.ensino.tecnico.dominio.OpcaoPoloGrupo;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;
import br.ufrn.sigaa.projetos.dominio.DiscenteProjeto;

/**
 * 
 * Managed Bean responsável pelos relatórios do coordenador de tutores do IMD
 * 
 * @author Rafael Barros
 *
 */

@Scope("request")
@Component("relatoriosCoordenadorTutoresIMD")
public class RelatoriosCoordenadorTutoresIMDMBean extends SigaaAbstractController{

	
	//RELATÓRIO DE ACOMPANHAMENTO DA EXECUÇÃO DA FREQUÊNCIA E NOTAS SEMANAIS
	
	/** Entidade que irá armazenar a turma de entrada selecionada para se efetuar a frequência semanal do IMD **/
	TurmaEntradaTecnico turmaEntradaSelecionada;
	
	/** Entidade que irá armazenar a listagem dos discentes do curso técnico que compõem a turma de entrada selecionada para se efetuar a frequência **/
	Collection<DiscenteTecnico> listaDiscentesTurma;
	
	/** Entidade que irá armazenar a listagem dos períodos que compõem a turma de entrada selecionada para se efetuar a frequência **/
	Collection<PeriodoAvaliacao> listaPeriodosTurma;
	
	/** Tabela responsável por armazenar os registros de acompanhamentos dos discentes em função dos períodos de avaliação **/
	List<List<AcompanhamentoSemanalDiscente>> tabelaAcompanhamento;
	
	/** Entidade que possui a coleção geral de acompanhamentos para a turma**/
	List<AcompanhamentoSemanalDiscente> listaAcompanhamentosGeral;
	
	/** Coleção de selectItem de cursos do Instituto Metrópole Digital. */
	private List<SelectItem> cursosCombo = new ArrayList<SelectItem>(0);

	/** Coleção de selectItem de módulos */
	private List<SelectItem> modulosCombo = new ArrayList<SelectItem>(0);
	
	/** Coleção de selectItem contendo a opção/grupo das turmas do IMD */
	private List<SelectItem> opcaoPoloGrupoCombo = new ArrayList<SelectItem>();

	/** ID de uma turma selecionada para o edicao e exclusao */
	private TurmaEntradaTecnico turmaSelecionada = new TurmaEntradaTecnico();

	/** Coleção de Tutorias do IMD */
	private List<TutoriaIMD> listaTutorias = new ArrayList<TutoriaIMD>();

	/** Coleção de turmas de entrada do IMD */
	private List<TurmaEntradaTecnico> listaTurmas = new ArrayList<TurmaEntradaTecnico>();
	
	/** Entidade que corresponde ao curso que será utilizado para pesquisar as turmas **/
	private CursoTecnico curso;

	/** Entidade que corresponde ao modulo que será utilizado para pesquisar as turmas **/
	private Modulo modulo;

	/** Entidade que corresponde a opção polo grupo que está vinculada a uma determinada turma **/
	private OpcaoPoloGrupo opcaoPoloGrupo;
	
	/**Data Atual*/
	private Date dataAtual = new Date();   
	
	/** Lista de objetos correspondentes ao relatório de acompanhamento de execução da frequência e notas semanais **/
	private Collection<RelatorioAcompanhamentoExecucaoFreqNotas> listaRegistros = new ArrayList<RelatorioAcompanhamentoExecucaoFreqNotas>();
	
	/**Variável que armazena a quantidade de periodos das turmas a serem exibidas no relatório de acompanhamento de execução da frequência e notas semanais **/
	private int qtdPeriodosTurmas = 0;
	
	
	/**
	 * Construtor da Classe
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/menus/menu_imd.jsp</li>
	 * </ul>
	 *  
	 * @param  
	 * @throws DAOException, ArqException
	 * @return 
	 */
	public RelatoriosCoordenadorTutoresIMDMBean() throws DAOException, ArqException {
		this.listaDiscentesTurma = new ArrayList<DiscenteTecnico>();
		this.listaPeriodosTurma = Collections.emptyList();
		this.listaAcompanhamentosGeral = new ArrayList<AcompanhamentoSemanalDiscente>();
		
		turmaEntradaSelecionada = new TurmaEntradaTecnico();
		turmaEntradaSelecionada.setDadosTurmaIMD(new DadosTurmaIMD());

		curso = new CursoTecnico();
		modulo = new Modulo();
		opcaoPoloGrupo = new OpcaoPoloGrupo();

		cursosCombo = toSelectItems(getDAO(CursoDao.class).findByNivel(getNivelEnsino(), true,	null, new Unidade(getUnidadeGestora())), "id", "descricao");
		opcaoPoloGrupoCombo = toSelectItems(getDAO(GenericSigaaDAO.class).findAll(OpcaoPoloGrupo.class), "id", "descricao");
	}
	
	// AJAX:
	/**
	 * Carrega os módulos do curso escolhido
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/menus/menu_imd.jsp</li>
	 * </ul>
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
			modulosCombo = toSelectItems(getDAO(CronogramaExecucaoAulasDao.class).findByCursoTecnico((Integer) e.getNewValue()), "id", "descricao");
		}
	}
	
	/**
	 * Redireciona a página para a listagem das turmas na qual o acompanhamento deve ser gerado
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/menus/menu_imd.jsp</li>
	 * </ul>
	 *  
	 * @param
	 * @return JSP que o sistema será redirecionado
	 * @throws DAOException, HibernateException
	 */
	public String listagemExecucaoFrequenciaNotas() throws HibernateException, DAOException{
		
		RelatoriosCoordenadorTutoresIMDDao coordDao = new RelatoriosCoordenadorTutoresIMDDao();
		
		try {
			listaRegistros = new ArrayList<RelatorioAcompanhamentoExecucaoFreqNotas>();
			listaRegistros = coordDao.findAcompanhamentoExecFreqNotas();
			
			preencherTurmasAndPeriodos();
			
			return forward("/metropole_digital/relatorios/coordenador_tutor/acomp_exec_freq_notas.jsp");
		} finally {
			coordDao.close();
			
		}
		
		
	}
	
	
	/**
	 * Redireciona a página de impressão para a listagem das turmas na qual o acompanhamento deve ser gerado
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/menus/menu_imd.jsp</li>
	 * </ul>
	 *  
	 * @param
	 * @return JSP que o sistema será redirecionado
	 * @throws DAOException, HibernateException
	 */
	public String listagemExecucaoFrequenciaNotasImprimir() throws HibernateException, DAOException{
		
		RelatoriosCoordenadorTutoresIMDDao coordDao = new RelatoriosCoordenadorTutoresIMDDao();
		
		try {
			listaRegistros = new ArrayList<RelatorioAcompanhamentoExecucaoFreqNotas>();
			listaRegistros = coordDao.findAcompanhamentoExecFreqNotas();
			
			preencherTurmasAndPeriodos();
			
			return forward("/metropole_digital/relatorios/coordenador_tutor/view_acomp_exec_freq_notas.jsp");
		} finally {
			coordDao.close();
			
		}
		
		
	}

	
	/**
	 * Efetua o preenchimento das turmas e periodos relacionados ao relatorio
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/menus/menu_imd.jsp</li>
	 * </ul>
	 *  
	 * @param
	 * @return 
	 * @throws DAOException
	 */
	public void preencherTurmasAndPeriodos() throws DAOException{
		TutoriaIMDDao tDao = new TutoriaIMDDao();
		CronogramaExecucaoAulasDao cronoDao = new CronogramaExecucaoAulasDao();
		listaTutorias = new ArrayList<TutoriaIMD>();
		try {
			
			TurmaEntradaTecnico turma = new TurmaEntradaTecnico();
			listaTurmas = new ArrayList<TurmaEntradaTecnico>();
			
			for(RelatorioAcompanhamentoExecucaoFreqNotas registro: listaRegistros) {
				if(turma.getId() != registro.getTurmaEntrada().getId()){
					//turma = tDao.findByPrimaryKey(registro.getTurmaEntrada().getId(), TurmaEntradaTecnico.class);
					
					turma = registro.getTurmaEntrada();
					listaTurmas.add(turma);
					TutoriaIMD tutoria = new TutoriaIMD();
					tutoria = tDao.findUltimaByTurmaEntradaProjetado(turma.getId());					
							
					tutoria.setTurmaEntrada(turma);
					listaTutorias.add(tutoria);
				}
			}
			int contadorMaior = 0;
			for(TurmaEntradaTecnico t: listaTurmas){
				CronogramaExecucaoAulas crono = cronoDao.findByPrimaryKey(t.getDadosTurmaIMD().getCronograma().getId());
				
				if(contadorMaior < crono.getPeriodosAvaliacao().size()){
					contadorMaior = crono.getPeriodosAvaliacao().size();
				}
			}
			
			qtdPeriodosTurmas = contadorMaior;
			
		} finally {
			tDao.close();
			cronoDao.close();
		}
		
		
	}
	
	
	/**
	 * Lista as turmas cadastradas de acordo com os critérios de pesquisa
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/menus/menu_imd.jsp</li>
	 * </ul>
	 *  
	 * @param
	 * @return JSP que o sistema será redirecionado
	 * @throws DAOException
	 */
	public String listarTurmas() throws DAOException {
		if (modulo.getId() != 0) {
			listaTurmas = getDAO(TutoriaIMDDao.class).findTurmasByModulo(modulo.getId());
			if (listaTurmas.isEmpty()) {
				addMensagemErro("Nenhum registro encontrado de acordo com os critérios de busca informados.");
			} else {
				preencherDadosAcompanhamentoExecFreqNotas();
			}
		} else {
			if (curso.getId() > 0) {
				listaTurmas = getDAO(TutoriaIMDDao.class).findTurmasByCurso(curso.getId());
				if (listaTurmas.isEmpty()) {
					addMensagemErro("Nenhum registro encontrado de acordo com os critérios de busca informados.");
				} else {
					preencherDadosAcompanhamentoExecFreqNotas();
				}
			} else {
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Curso");
			}
		}
		//Collections.sort(listaTurmas);
		return null;
	}

	
	/** 
	 * Função que efetua o preenchimento das informações referentes ao relatório de acompanhamento da execução da frequência e notas semanais
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/menus/menu_imd.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return 
	 * @throws DAOException 
	 */
	public void preencherDadosAcompanhamentoExecFreqNotas() throws DAOException{
		
		for(TurmaEntradaTecnico turma: listaTurmas){
			turmaEntradaSelecionada = turma;
			preencheTabelaAcompanhamento();
			
		}
	
	}
	
	
	/** 
	 * Função que efetua o preenchimento dos registros da tabela de acompanhamento da execução da frequência e notas semanais
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/menus/menu_imd.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return 
	 * @throws DAOException
	 * 
	 */
	public void preencheTabelaAcompanhamento() throws DAOException{
		AcompanhamentoSemanalDiscenteDao acompanhamentoDao = getDAO(AcompanhamentoSemanalDiscenteDao.class);	
		try {
			tabelaAcompanhamento = new ArrayList<List<AcompanhamentoSemanalDiscente>>();
			
			turmaEntradaSelecionada.setId(getParameterInt("id"));
			turmaEntradaSelecionada = getGenericDAO().findByPrimaryKey(turmaEntradaSelecionada.getId(), TurmaEntradaTecnico.class);
			
			listaDiscentesTurma = getDAO(TurmaEntradaTecnicoDao.class).findDiscentesByTurmaEntrada(turmaEntradaSelecionada.getId());
			listaPeriodosTurma = getDAO(TurmaEntradaTecnicoDao.class).findPeriodosByTurmaEntrada(turmaEntradaSelecionada.getId());

			turmaEntradaSelecionada.getDadosTurmaIMD().getCronograma().getUnidadeTempo().getDescricao();
			//Carrega a lista de acompanhamento. Caso não existe ele cria a lista;

			
		} finally {
			acompanhamentoDao.close();
		}
	}
	
	
	/** 
	 * Função que efetua o preenchimento dos registros da tabela de acompanhamentos
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/menus/menu_imd.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return 
	 * @throws DAOException
	 * 
	 
	public void preencheTabelaAcompanhamento() throws DAOException{
		AcompanhamentoSemanalDiscenteDao acompanhamentoDao = getDAO(AcompanhamentoSemanalDiscenteDao.class);	
		try {
			tabelaAcompanhamento = new ArrayList<List<AcompanhamentoSemanalDiscente>>();
			
			turmaEntradaSelecionada.setId(getParameterInt("id"));
			turmaEntradaSelecionada = getGenericDAO().findByPrimaryKey(turmaEntradaSelecionada.getId(), TurmaEntradaTecnico.class);
			
			listaDiscentesTurma = getDAO(TurmaEntradaTecnicoDao.class).findDiscentesByTurmaEntrada(turmaEntradaSelecionada.getId());
			listaPeriodosTurma = getDAO(TurmaEntradaTecnicoDao.class).findPeriodosByTurmaEntrada(turmaEntradaSelecionada.getId());

			turmaEntradaSelecionada.getDadosTurmaIMD().getCronograma().getUnidadeTempo().getDescricao();
			//Carrega a lista de acompanhamento. Caso não existe ele cria a lista;

			this.listaAcompanhamentosGeral = (ArrayList) acompanhamentoDao.findAcompanhamentosByTurmaEntradaProjetado(turmaEntradaSelecionada.getId());
			int contadorLista = 0;
			List<AcompanhamentoSemanalDiscente> listaAuxiliarAcompanhamentos = new ArrayList<AcompanhamentoSemanalDiscente>();
			
			//Tabela de Acomponhamento
			for(AcompanhamentoSemanalDiscente acomp : this.listaAcompanhamentosGeral){
				if(contadorLista == listaPeriodosTurma.size()){
					tabelaAcompanhamento.add(listaAuxiliarAcompanhamentos);
					listaAuxiliarAcompanhamentos = new ArrayList<AcompanhamentoSemanalDiscente>();
					contadorLista = 0;
				}
				listaAuxiliarAcompanhamentos.add(acomp);
				contadorLista ++;
			}
			tabelaAcompanhamento.add(listaAuxiliarAcompanhamentos);
		} finally {
			acompanhamentoDao.close();
		}
	}*/
	
	
	
	/** Função que preenche a listagem dos discentes, períodos de avaliação e os registros das notas semanais da Turma de Entrada selecionada.
	 *  A função também verifica se os registros da frequencia da turma já foram criados, caso não tenham sido criados, a função chama uma nova
	 *  função que irá efetuar o procedimento de criar os registros da frequencia da turma
	 *  
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/menus/menu_imd.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return 
	 * @throws Exception 
	 */
	public void findDiscentesPeriodosTurma() throws Exception{	
		listaDiscentesTurma = getDAO(TurmaEntradaTecnicoDao.class).findDiscentesByTurmaEntrada(turmaEntradaSelecionada.getId());
		listaPeriodosTurma = getDAO(TurmaEntradaTecnicoDao.class).findPeriodosByTurmaEntrada(turmaEntradaSelecionada.getId());
		//preencheTabelaAcompanhamento(); 
	}
	
	public TurmaEntradaTecnico getTurmaEntradaSelecionada() {
		return turmaEntradaSelecionada;
	}

	public void setTurmaEntradaSelecionada(
			TurmaEntradaTecnico turmaEntradaSelecionada) {
		this.turmaEntradaSelecionada = turmaEntradaSelecionada;
	}

	
	/** Função GET do atributo listaDiscentesTurma que também efetua o preenchimento dessa entidate caso esteja vazia
	 *  
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/menus/menu_imd.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return 
	 * @throws Exception 
	 */
	public Collection<DiscenteTecnico> getListaDiscentesTurma() throws Exception {
		if(this.listaDiscentesTurma.isEmpty()){
			findDiscentesPeriodosTurma();
		}
		
		return listaDiscentesTurma;
	}

	public void setListaDiscentesTurma(Collection<DiscenteTecnico> listaDiscentesTurma) {
		this.listaDiscentesTurma = listaDiscentesTurma;
	}
	
	
	/** Função GET do atributo listaPeriodosTurma que também efetua o preenchimento dessa entidate caso esteja vazia
	 *  
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/menus/menu_imd.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return 
	 * @throws Exception 
	 */
	public Collection<PeriodoAvaliacao> getListaPeriodosTurma() throws Exception {
		if(this.listaPeriodosTurma.isEmpty()){
			findDiscentesPeriodosTurma();
		}
		return listaPeriodosTurma;
	}
 
	public void setListaPeriodosTurma(
			Collection<PeriodoAvaliacao> listaPeriodosTurma) {
		this.listaPeriodosTurma = listaPeriodosTurma;
	}
	
	public List<List<AcompanhamentoSemanalDiscente>> getTabelaAcompanhamento() {
		return tabelaAcompanhamento;
	}

	public void setTabelaAcompanhamento(
			List<List<AcompanhamentoSemanalDiscente>> tabelaAcompanhamento) {
		this.tabelaAcompanhamento = tabelaAcompanhamento;
	}

	public Date getDataAtual() {
		return dataAtual;
	}

	public void setDataAtual(Date dataAtual) {
		this.dataAtual = dataAtual;
	}

	public List<AcompanhamentoSemanalDiscente> getListaAcompanhamentosGeral() {
		return listaAcompanhamentosGeral;
	}

	public void setListaAcompanhamentosGeral(List<AcompanhamentoSemanalDiscente> listaAcompanhamentosGeral) {
		this.listaAcompanhamentosGeral = listaAcompanhamentosGeral;
	}

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

	public List<SelectItem> getOpcaoPoloGrupoCombo() {
		return opcaoPoloGrupoCombo;
	}

	public void setOpcaoPoloGrupoCombo(List<SelectItem> opcaoPoloGrupoCombo) {
		this.opcaoPoloGrupoCombo = opcaoPoloGrupoCombo;
	}

	public TurmaEntradaTecnico getTurmaSelecionada() {
		return turmaSelecionada;
	}

	public void setTurmaSelecionada(TurmaEntradaTecnico turmaSelecionada) {
		this.turmaSelecionada = turmaSelecionada;
	}

	public List<TutoriaIMD> getListaTutorias() {
		return listaTutorias;
	}

	public void setListaTutorias(List<TutoriaIMD> listaTutorias) {
		this.listaTutorias = listaTutorias;
	}

	public List<TurmaEntradaTecnico> getListaTurmas() {
		return listaTurmas;
	}

	public void setListaTurmas(List<TurmaEntradaTecnico> listaTurmas) {
		this.listaTurmas = listaTurmas;
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
	
	public OpcaoPoloGrupo getOpcaoPoloGrupo() {
		return opcaoPoloGrupo;
	}

	public void setOpcaoPoloGrupo(OpcaoPoloGrupo opcaoPoloGrupo) {
		this.opcaoPoloGrupo = opcaoPoloGrupo;
	}

	public Collection<RelatorioAcompanhamentoExecucaoFreqNotas> getListaRegistros() {
		return listaRegistros;
	}

	public void setListaRegistros(Collection<RelatorioAcompanhamentoExecucaoFreqNotas> listaRegistros) {
		this.listaRegistros = listaRegistros;
	}

	public int getQtdPeriodosTurmas() {
		return qtdPeriodosTurmas;
	}

	public void setQtdPeriodosTurmas(int qtdPeriodosTurmas) {
		this.qtdPeriodosTurmas = qtdPeriodosTurmas;
	}
	
	
	/**
	 * 
	 * Função que efetua o redirecionamento para o formulario de envio de e-mail e mensagens aos tutores do IMD
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/relatorios/coordenador_tutor/acomp_exec_freq_notas.jsp</li>
	 * </ul>
	 * 
	 * @param
	 * @return
	 * @throws NumberFormatException, ArqException
	 */
	public String enviarMensagem() throws NumberFormatException, ArqException{
		
		String nome = null;
		String email = null;
		int idUsuario = 0;
		TutorIMDDao dao = getDAO(TutorIMDDao.class);
		UsuarioDao usuDao = getDAO(UsuarioDao.class);
		try {

			if(getParameterInt("id") > 0) {
				int idTutor = getParameterInt("id");
				
				TutorIMD tutor = new TutorIMD();
				tutor = dao.findByPrimaryKey(idTutor, TutorIMD.class);
				Collection<Usuario> listaUsuarios = usuDao.findByPessoa(tutor.getPessoa());
				
				Usuario usuario = new Usuario();
				for(Usuario u: listaUsuarios){
					usuario = u;
				}
				
				nome = tutor.getPessoa().getNome();
				email = tutor.getPessoa().getEmail();
				idUsuario = usuario.getId();

				String remetente =  getUsuarioLogado().getNome();
				NotificacoesIMDMBean notificacao = getMBean("notificacoesIMD");
				ArrayList<Destinatario> destinatarios = new ArrayList<Destinatario>();	
				Destinatario destinatario = new Destinatario(nome, email);
				destinatario.setIdusuario(idUsuario);			
				destinatarios.add(destinatario);
				notificacao.setDestinatarios(destinatarios);
				notificacao.setRemetente( remetente );
				notificacao.setTitulo("Enviar mensagem ao Tutor do IMD");
				notificacao.clear();
				notificacao.prepararFormulario();
				notificacao.getObj().setContentType(MailBody.HTML);
				return forward(notificacao.getCaminhoFormulario());

			} else {
				addMensagemErro("Tutor não informado.");
				return null;
			}
			
						
		} finally {
			dao.close();
			usuDao.close();
		}
	}
	
	
}
