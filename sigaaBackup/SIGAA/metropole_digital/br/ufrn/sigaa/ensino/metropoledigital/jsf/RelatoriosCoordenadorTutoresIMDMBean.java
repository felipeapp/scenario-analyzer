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
 * Managed Bean respons�vel pelos relat�rios do coordenador de tutores do IMD
 * 
 * @author Rafael Barros
 *
 */

@Scope("request")
@Component("relatoriosCoordenadorTutoresIMD")
public class RelatoriosCoordenadorTutoresIMDMBean extends SigaaAbstractController{

	
	//RELAT�RIO DE ACOMPANHAMENTO DA EXECU��O DA FREQU�NCIA E NOTAS SEMANAIS
	
	/** Entidade que ir� armazenar a turma de entrada selecionada para se efetuar a frequ�ncia semanal do IMD **/
	TurmaEntradaTecnico turmaEntradaSelecionada;
	
	/** Entidade que ir� armazenar a listagem dos discentes do curso t�cnico que comp�em a turma de entrada selecionada para se efetuar a frequ�ncia **/
	Collection<DiscenteTecnico> listaDiscentesTurma;
	
	/** Entidade que ir� armazenar a listagem dos per�odos que comp�em a turma de entrada selecionada para se efetuar a frequ�ncia **/
	Collection<PeriodoAvaliacao> listaPeriodosTurma;
	
	/** Tabela respons�vel por armazenar os registros de acompanhamentos dos discentes em fun��o dos per�odos de avalia��o **/
	List<List<AcompanhamentoSemanalDiscente>> tabelaAcompanhamento;
	
	/** Entidade que possui a cole��o geral de acompanhamentos para a turma**/
	List<AcompanhamentoSemanalDiscente> listaAcompanhamentosGeral;
	
	/** Cole��o de selectItem de cursos do Instituto Metr�pole Digital. */
	private List<SelectItem> cursosCombo = new ArrayList<SelectItem>(0);

	/** Cole��o de selectItem de m�dulos */
	private List<SelectItem> modulosCombo = new ArrayList<SelectItem>(0);
	
	/** Cole��o de selectItem contendo a op��o/grupo das turmas do IMD */
	private List<SelectItem> opcaoPoloGrupoCombo = new ArrayList<SelectItem>();

	/** ID de uma turma selecionada para o edicao e exclusao */
	private TurmaEntradaTecnico turmaSelecionada = new TurmaEntradaTecnico();

	/** Cole��o de Tutorias do IMD */
	private List<TutoriaIMD> listaTutorias = new ArrayList<TutoriaIMD>();

	/** Cole��o de turmas de entrada do IMD */
	private List<TurmaEntradaTecnico> listaTurmas = new ArrayList<TurmaEntradaTecnico>();
	
	/** Entidade que corresponde ao curso que ser� utilizado para pesquisar as turmas **/
	private CursoTecnico curso;

	/** Entidade que corresponde ao modulo que ser� utilizado para pesquisar as turmas **/
	private Modulo modulo;

	/** Entidade que corresponde a op��o polo grupo que est� vinculada a uma determinada turma **/
	private OpcaoPoloGrupo opcaoPoloGrupo;
	
	/**Data Atual*/
	private Date dataAtual = new Date();   
	
	/** Lista de objetos correspondentes ao relat�rio de acompanhamento de execu��o da frequ�ncia e notas semanais **/
	private Collection<RelatorioAcompanhamentoExecucaoFreqNotas> listaRegistros = new ArrayList<RelatorioAcompanhamentoExecucaoFreqNotas>();
	
	/**Vari�vel que armazena a quantidade de periodos das turmas a serem exibidas no relat�rio de acompanhamento de execu��o da frequ�ncia e notas semanais **/
	private int qtdPeriodosTurmas = 0;
	
	
	/**
	 * Construtor da Classe
	 * 
	 * M�todo � chamado nas seguintes JSP's:
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
	 * Carrega os m�dulos do curso escolhido
	 * 
	 * M�todo � chamado nas seguintes JSP's:
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
	 * Redireciona a p�gina para a listagem das turmas na qual o acompanhamento deve ser gerado
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/menus/menu_imd.jsp</li>
	 * </ul>
	 *  
	 * @param
	 * @return JSP que o sistema ser� redirecionado
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
	 * Redireciona a p�gina de impress�o para a listagem das turmas na qual o acompanhamento deve ser gerado
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/menus/menu_imd.jsp</li>
	 * </ul>
	 *  
	 * @param
	 * @return JSP que o sistema ser� redirecionado
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
	 * M�todo � chamado nas seguintes JSP's:
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
	 * Lista as turmas cadastradas de acordo com os crit�rios de pesquisa
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/menus/menu_imd.jsp</li>
	 * </ul>
	 *  
	 * @param
	 * @return JSP que o sistema ser� redirecionado
	 * @throws DAOException
	 */
	public String listarTurmas() throws DAOException {
		if (modulo.getId() != 0) {
			listaTurmas = getDAO(TutoriaIMDDao.class).findTurmasByModulo(modulo.getId());
			if (listaTurmas.isEmpty()) {
				addMensagemErro("Nenhum registro encontrado de acordo com os crit�rios de busca informados.");
			} else {
				preencherDadosAcompanhamentoExecFreqNotas();
			}
		} else {
			if (curso.getId() > 0) {
				listaTurmas = getDAO(TutoriaIMDDao.class).findTurmasByCurso(curso.getId());
				if (listaTurmas.isEmpty()) {
					addMensagemErro("Nenhum registro encontrado de acordo com os crit�rios de busca informados.");
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
	 * Fun��o que efetua o preenchimento das informa��es referentes ao relat�rio de acompanhamento da execu��o da frequ�ncia e notas semanais
	 * 
	 * M�todo � chamado nas seguintes JSP's:
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
	 * Fun��o que efetua o preenchimento dos registros da tabela de acompanhamento da execu��o da frequ�ncia e notas semanais
	 * 
	 * M�todo � chamado nas seguintes JSP's:
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
			//Carrega a lista de acompanhamento. Caso n�o existe ele cria a lista;

			
		} finally {
			acompanhamentoDao.close();
		}
	}
	
	
	/** 
	 * Fun��o que efetua o preenchimento dos registros da tabela de acompanhamentos
	 * 
	 * M�todo � chamado nas seguintes JSP's:
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
			//Carrega a lista de acompanhamento. Caso n�o existe ele cria a lista;

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
	
	
	
	/** Fun��o que preenche a listagem dos discentes, per�odos de avalia��o e os registros das notas semanais da Turma de Entrada selecionada.
	 *  A fun��o tamb�m verifica se os registros da frequencia da turma j� foram criados, caso n�o tenham sido criados, a fun��o chama uma nova
	 *  fun��o que ir� efetuar o procedimento de criar os registros da frequencia da turma
	 *  
	 * M�todo � chamado nas seguintes JSP's:
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

	
	/** Fun��o GET do atributo listaDiscentesTurma que tamb�m efetua o preenchimento dessa entidate caso esteja vazia
	 *  
	 * M�todo � chamado nas seguintes JSP's:
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
	
	
	/** Fun��o GET do atributo listaPeriodosTurma que tamb�m efetua o preenchimento dessa entidate caso esteja vazia
	 *  
	 * M�todo � chamado nas seguintes JSP's:
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
	 * Fun��o que efetua o redirecionamento para o formulario de envio de e-mail e mensagens aos tutores do IMD
	 * 
	 * M�todo � chamado nas seguintes JSP's:
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
				addMensagemErro("Tutor n�o informado.");
				return null;
			}
			
						
		} finally {
			dao.close();
			usuDao.close();
		}
	}
	
	
}
