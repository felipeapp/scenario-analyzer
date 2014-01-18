package br.ufrn.sigaa.ensino.metropoledigital.jsf;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.metropoledigital.dao.AcompanhamentoSemanalDiscenteDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.CronogramaExecucaoAulasDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TurmaEntradaTecnicoDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TutoriaIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.AcompanhamentoSemanalDiscente;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.RelatorioFrequenciaTurmaIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.TutoriaIMD;
import br.ufrn.sigaa.ensino.tecnico.dao.ModuloDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.CursoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.Modulo;
import br.ufrn.sigaa.ensino.tecnico.dominio.OpcaoPoloGrupo;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;




/**
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Entidade respons�vel pelo gerenciamento da funcionalidade do relat�rio de frequencia do assistente social do IMD.
 * 
 * 
 * @author Rafael Barros
 *
 */

@Scope("request")
@Component("relatorioFrequenciaAssitenteSocial")
public class RelatorioFrequenciaAssistenteSocialMBean extends SigaaAbstractController {
	
	/**Cole��o de itens do combobox de polo**/
	private Collection<SelectItem> opcaoPolosCombo = new ArrayList<SelectItem>();
	
	/**Cole��o de itens do combobox de modulo**/
	private Collection<SelectItem> modulosCombo = new ArrayList<SelectItem>();
	
	/**Vari�vel que corresponde ao ID do polo selecionado**/
	private int idOpcaoPoloSelecionado = 0;
	
	/**Vari�vel que corresponde ao ID do polo selecionado Antigo**/
	private int idOpcaoPoloSelecionadoAntigo = 0;
	
	/**Vari�vel que corresponde ao ID do m�dulo selecionado**/
	private int idModuloSelecionado = 0;
	
	/**Cole��o de turmas que est�o associadas ao polo selecionado**/
	private Collection<TurmaEntradaTecnico> listaTurmas = new ArrayList<TurmaEntradaTecnico> ();
	
	/**Vari�vel que corresponde ao ID da turma selecionada**/
	private int idTurmaEntradaSelecionada = 0;
	
	/**Entidade que corresponde a turma de entrada selecionada na qual os alunos ser�o matriculados**/
	private TurmaEntradaTecnico turmaEntradaSelecionada = new TurmaEntradaTecnico();
	
	/**Entidade que corresponde a turma de entrada que armazenar� os dados informados para filtrar as turmas**/
	private TurmaEntradaTecnico turmaFiltros = new TurmaEntradaTecnico();
	
	/**Entidade que corresponde a op��o polo grupo selecionada na qual as turmas ser�o listadas**/
	private OpcaoPoloGrupo opcaoPoloSelecionada = new OpcaoPoloGrupo();
	
	/**Entidade que corresponde ao modulo selecionado na qual as turmas ser�o listadas**/
	private Modulo moduloSelecionado = new Modulo();
	
	/**Cole��o de discentes n�o enturmados que est�o associadas ao polo selecionado**/
	private Collection<DiscenteTecnico> listaDiscentes = new ArrayList<DiscenteTecnico> ();
	
	/**Cole��o de turmas de entrada selecionadas para o relatorio de frequencia do assistente social do IMD**/
	private Collection<TurmaEntradaTecnico> turmasSelecionadas = new ArrayList<TurmaEntradaTecnico>();
	
	/**Cole��o de tutorias buscadas para o relatorio de frequencia do assistente social do IMD**/
	private ArrayList<TutoriaIMD> listaTutorias = new ArrayList<TutoriaIMD>();
	
	/**Cole��o de tutorias SELECIONADAS para o relatorio de frequencia do assistente social do IMD**/
	private ArrayList<TutoriaIMD> tutoriasSelecionadas = new ArrayList<TutoriaIMD>();

	
	//VARI�VEIS QUE SER�O UTILIZADAS PARA O RELAT�RIO DE FREQU�NCIA POR TURMA CONFIGUR�VEL
	/**Corresponde a data inicio informada pelo usu�rio para gera��o do relat�rio de frequ�ncia configur�vel*/
	private boolean relatorioFiltrado = false;
	
	/**Corresponde a data inicio informada pelo usu�rio para gera��o do relat�rio de frequ�ncia configur�vel*/
	private Date dataInicioInformada = null;
	
	/**Corresponde a data fim informada pelo usu�rio para gera��o do relat�rio de frequ�ncia configur�vel*/
	private Date dataFimInformada = null;
	
	/**Corresponde a quantidade m�nima de faltas informada pelo usu�rio para gera��o do relat�rio de frequ�ncia configur�vel*/
	private Integer qtdFaltasMinimaInformada = null;
	
	/**Corresponde a quantidade m�xima de faltas informada pelo usu�rio para gera��o do relat�rio de frequ�ncia configur�vel*/
	private Integer qtdFaltasMaximaInformada = null;
	
	/**Corresponde ao percentual m�nimo de faltas informado pelo usu�rio para gera��o do relat�rio de frequ�ncia configur�vel*/
	private Integer percFaltasMinimaInformada = null;
	
	/**Corresponde ao percentual m�ximo de faltas informado pelo usu�rio para gera��o do relat�rio de frequ�ncia configur�vel*/
	private Integer percFaltasMaximaInformada = null;
	
	/**
	 * Entidade que ir� armazenar a listagem dos discentes do curso t�cnico que
	 * comp�em a turma de entrada selecionada para se efetuar a frequ�ncia
	 **/
	private Collection<AcompanhamentoSemanalDiscente> listaGeralAcompanhamentos;

	/**
	 * Entidade que ir� armazenar a listagem dos objetos correspondentes ao
	 * relat�rio de frequencia da turma
	 **/
	private Collection<RelatorioFrequenciaTurmaIMD> listaRelatorioFrequencia;
	
	/**
	 * Entidade que ir� armazenar uma tabela com as listagens dos objetos correspondentes ao
	 * relat�rio de frequencia da turma
	 **/
	private ArrayList<ArrayList<RelatorioFrequenciaTurmaIMD>> tabelaRelatorioFrequencia;
	

	
	public RelatorioFrequenciaAssistenteSocialMBean(){
		opcaoPolosCombo = Collections.emptyList();
		turmaEntradaSelecionada = new TurmaEntradaTecnico();
		opcaoPoloSelecionada = new OpcaoPoloGrupo();
		listaTurmas = new ArrayList<TurmaEntradaTecnico>();
		listaDiscentes = new ArrayList<DiscenteTecnico>(); 
	}
	
	
	/** M�todo respons�vel por retornar o atributo polosCombo, caso o atributo esteja vazio, o m�todo efetua o preenchimento da lista.
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/assistente_social/selecao_opcao_turmas.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return polosCombo
	 * @throws DAOException
	 */
	public Collection<SelectItem> getOpcaoPolosCombo() throws DAOException {
		if(opcaoPolosCombo.isEmpty()){
			opcaoPolosCombo = toSelectItems(getGenericDAO().findAll(OpcaoPoloGrupo.class), "id", "descricao");
		}
		return opcaoPolosCombo;
	}

	
	public void setOpcaoPolosCombo(Collection<SelectItem> opcaoPolosCombo) {
		this.opcaoPolosCombo = opcaoPolosCombo;
	}
	
	
	/** M�todo respons�vel por retornar o atributo listaTurmas, caso o atributo esteja vazio, o m�todo efetua o preenchimento da lista.
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/assistente_social/selecao_opcao_turmas.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return listaTurmas
	 * @throws DAOException
	 */
	public Collection<TurmaEntradaTecnico> getListaTurmas() throws DAOException {
		TurmaEntradaTecnicoDao tDao = new TurmaEntradaTecnicoDao();
		try {
			
			if(idOpcaoPoloSelecionado > 0 && idOpcaoPoloSelecionado != idOpcaoPoloSelecionadoAntigo){
				listaTurmas = (Collection<TurmaEntradaTecnico>) tDao.findTurmaByOpcaoPoloGrupo(idOpcaoPoloSelecionado);
				idOpcaoPoloSelecionadoAntigo = idOpcaoPoloSelecionado;
			}
			return listaTurmas;
			
		} finally {
			tDao.close();
		}
	}

	
	public void setListaTurmas(Collection<TurmaEntradaTecnico> listaTurmas) {
		this.listaTurmas = listaTurmas;
	}


	public int getIdOpcaoPoloSelecionado() {
		return idOpcaoPoloSelecionado;
	}


	public void setIdOpcaoPoloSelecionado(int idOpcaoPoloSelecionado) {
		this.idOpcaoPoloSelecionado = idOpcaoPoloSelecionado;
	}	
	
	
	public ArrayList<TutoriaIMD> getListaTutorias() {
		return listaTutorias;
	}
	

	public ArrayList<TutoriaIMD> getTutoriasSelecionadas() {
		return tutoriasSelecionadas;
	}


	public void setTutoriasSelecionadas(ArrayList<TutoriaIMD> tutoriasSelecionadas) {
		this.tutoriasSelecionadas = tutoriasSelecionadas;
	}


	public void setListaTutorias(ArrayList<TutoriaIMD> listaTutorias) {
		this.listaTutorias = listaTutorias;
	}


	public int getIdOpcaoPoloSelecionadoAntigo() {
		return idOpcaoPoloSelecionadoAntigo;
	}


	public void setIdOpcaoPoloSelecionadoAntigo(int idOpcaoPoloSelecionadoAntigo) {
		this.idOpcaoPoloSelecionadoAntigo = idOpcaoPoloSelecionadoAntigo;
	}
	

	public TurmaEntradaTecnico getTurmaEntradaSelecionada() {
		return turmaEntradaSelecionada;
	}


	public void setTurmaEntradaSelecionada(
			TurmaEntradaTecnico turmaEntradaSelecionada) {
		this.turmaEntradaSelecionada = turmaEntradaSelecionada;
	}


	public int getIdTurmaEntradaSelecionada() {
		return idTurmaEntradaSelecionada;
	}


	public void setIdTurmaEntradaSelecionada(int idTurmaEntradaSelecionada) {
		this.idTurmaEntradaSelecionada = idTurmaEntradaSelecionada;
	}

	/** M�todo respons�vel por retornar o atributo opcaoPoloSelecionada, caso o atributo esteja vazio, o m�todo efetua o preenchimento da lista.
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/assistente_social/selecao_opcao_turmas.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return opcaoPoloSelecionada
	 * @throws DAOException
	 */
	public OpcaoPoloGrupo getOpcaoPoloSelecionada() throws DAOException {
		if(idOpcaoPoloSelecionado > 0){
			opcaoPoloSelecionada = getGenericDAO().findByPrimaryKey(idOpcaoPoloSelecionado, OpcaoPoloGrupo.class);
		} else {
			opcaoPoloSelecionada = null;
		}
		
		return opcaoPoloSelecionada;
	}


	public void setOpcaoPoloSelecionada(OpcaoPoloGrupo opcaoPoloSelecionada) {
		this.opcaoPoloSelecionada = opcaoPoloSelecionada;
	}


	public Collection<DiscenteTecnico> getListaDiscentes() {
		return listaDiscentes;
	}


	public void setListaDiscentes(Collection<DiscenteTecnico> listaDiscentes) {
		this.listaDiscentes = listaDiscentes;
	}
	

	public Collection<TurmaEntradaTecnico> getTurmasSelecionadas() {
		return turmasSelecionadas;
	}


	public void setTurmasSelecionadas(
			Collection<TurmaEntradaTecnico> turmasSelecionadas) {
		this.turmasSelecionadas = turmasSelecionadas;
	}
	

	public boolean isRelatorioFiltrado() {
		return relatorioFiltrado;
	}


	public void setRelatorioFiltrado(boolean relatorioFiltrado) {
		this.relatorioFiltrado = relatorioFiltrado;
	}


	public Date getDataInicioInformada() {
		return dataInicioInformada;
	}


	public void setDataInicioInformada(Date dataInicioInformada) {
		this.dataInicioInformada = dataInicioInformada;
	}


	public Date getDataFimInformada() {
		return dataFimInformada;
	}


	public void setDataFimInformada(Date dataFimInformada) {
		this.dataFimInformada = dataFimInformada;
	}


	public Integer getQtdFaltasMinimaInformada() {
		return qtdFaltasMinimaInformada;
	}


	public void setQtdFaltasMinimaInformada(Integer qtdFaltasMinimaInformada) {
		this.qtdFaltasMinimaInformada = qtdFaltasMinimaInformada;
	}


	public Integer getQtdFaltasMaximaInformada() {
		return qtdFaltasMaximaInformada;
	}


	public void setQtdFaltasMaximaInformada(Integer qtdFaltasMaximaInformada) {
		this.qtdFaltasMaximaInformada = qtdFaltasMaximaInformada;
	}


	public Integer getPercFaltasMinimaInformada() {
		return percFaltasMinimaInformada;
	}


	public void setPercFaltasMinimaInformada(Integer percFaltasMinimaInformada) {
		this.percFaltasMinimaInformada = percFaltasMinimaInformada;
	}


	public Integer getPercFaltasMaximaInformada() {
		return percFaltasMaximaInformada;
	}


	public void setPercFaltasMaximaInformada(Integer percFaltasMaximaInformada) {
		this.percFaltasMaximaInformada = percFaltasMaximaInformada;
	}
	

	public Collection<AcompanhamentoSemanalDiscente> getListaGeralAcompanhamentos() {
		return listaGeralAcompanhamentos;
	}


	public void setListaGeralAcompanhamentos(
			Collection<AcompanhamentoSemanalDiscente> listaGeralAcompanhamentos) {
		this.listaGeralAcompanhamentos = listaGeralAcompanhamentos;
	}


	public Collection<RelatorioFrequenciaTurmaIMD> getListaRelatorioFrequencia() {
		return listaRelatorioFrequencia;
	}


	public void setListaRelatorioFrequencia(
			Collection<RelatorioFrequenciaTurmaIMD> listaRelatorioFrequencia) {
		this.listaRelatorioFrequencia = listaRelatorioFrequencia;
	}
	

	public ArrayList<ArrayList<RelatorioFrequenciaTurmaIMD>> getTabelaRelatorioFrequencia() {
		return tabelaRelatorioFrequencia;
	}


	public void setTabelaRelatorioFrequencia(
			ArrayList<ArrayList<RelatorioFrequenciaTurmaIMD>> tabelaRelatorioFrequencia) {
		this.tabelaRelatorioFrequencia = tabelaRelatorioFrequencia;
	}

	

	
	public void setModulosCombo(Collection<SelectItem> modulosCombo)  {
		this.modulosCombo = modulosCombo;
	}


	public TurmaEntradaTecnico getTurmaFiltros() {
		return turmaFiltros;
	}


	public void setTurmaFiltros(TurmaEntradaTecnico turmaFiltros) {
		this.turmaFiltros = turmaFiltros;
	}


	public int getIdModuloSelecionado() {
		return idModuloSelecionado;
	}


	public void setIdModuloSelecionado(int idModuloSelecionado) {
		this.idModuloSelecionado = idModuloSelecionado;
	}

	/** M�todo respons�vel por retornar o atributo moduloSelecionado, caso o atributo esteja vazio, o m�todo efetua o preenchimento da lista.
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/assistente_social/selecao_opcao_turmas.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return moduloSelecionado
	 * @throws DAOException
	 */
	public Modulo getModuloSelecionado() throws DAOException {
		if(idModuloSelecionado > 0) {
			moduloSelecionado = getDAO(ModuloDao.class).findByPrimaryKey(idModuloSelecionado, Modulo.class);
		} else {
			moduloSelecionado = null;
		}
		return moduloSelecionado;
	}
	
	/** M�todo respons�vel por retornar o atributo modulosCombo, caso o atributo esteja vazio, o m�todo efetua o preenchimento da lista.
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/assistente_social/selecao_opcao_turmas.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return modulosCombo
	 * @throws ArqException 
	 */	
	public Collection<SelectItem> getModulosCombo() throws ArqException{
		if(modulosCombo.isEmpty()){			
			
			Unidade imd = getGenericDAO().findByPrimaryKey(ParametroHelper.getInstance().getParametroInt(
					ParametrosTecnico.ID_UNIDADE_INSTITUTO_METROPOLE_DIGITAL), Unidade.class);
			
			//cursosCombo = toSelectItems(cursoDao.findByNivel(getNivelEnsino(), true, null, new Unidade(getUnidadeGestora())), "id", "descricao");
			Collection<Curso> listaCursos = getDAO(CursoDao.class).findByNivel(getNivelEnsino(), true, null, imd);
			int idCurso = 0;
			
			for(Curso c: listaCursos){
				idCurso = c.getId();
			}
			
			modulosCombo = toSelectItems(getDAO(CronogramaExecucaoAulasDao.class).findByCursoTecnico(idCurso), "id", "descricao");
			//modulosCombo = toSelectItems(getDAO(CronogramaExecucaoAulasDao.class).findByCursoTecnico(96054058), "id", "descricao");
		}
		return modulosCombo;
	}


	public void setModuloSelecionado(Modulo moduloSelecionado) {
		this.moduloSelecionado = moduloSelecionado;
	}


	/** M�todo respons�vel por preencher a lista de turmas quando a op��o polo grupo for alterada.
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/assistente_social/selecao_opcao_turmas.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return 
	 * @throws DAOException
	 */
	public void preencherTurmas() throws DAOException{
		TurmaEntradaTecnicoDao tDao = new TurmaEntradaTecnicoDao();
		TutoriaIMDDao tutoDao = new TutoriaIMDDao();
		try {
			this.listaTurmas = Collections.emptyList();
			
			if(idOpcaoPoloSelecionado > 0 && listaTurmas.isEmpty()){
				boolean validado = true;
				
				if(turmaFiltros.getAnoReferencia() != null) {
					if(turmaFiltros.getAnoReferencia() < 1900){
						addMensagemErro("Ano: O valor deve ser maior ou igual a 1900.");
						validado = false;
					}
				} 
				
				if(turmaFiltros.getPeriodoReferencia() != null) {
					if(turmaFiltros.getPeriodoReferencia() != 1 && turmaFiltros.getPeriodoReferencia() != 2) {
						addMensagemErro("Per�odo: O valor deve ser maior ou igual a 1 e menor ou igual a 2.");
						validado = false;
					} 
				}
				
				if(validado){
					listaTurmas = (Collection<TurmaEntradaTecnico>) tDao.findTurmaByOpcaoPoloGrupoAndModuloAndAnoPeriodo(idOpcaoPoloSelecionado, idModuloSelecionado, turmaFiltros.getAnoReferencia(), turmaFiltros.getPeriodoReferencia());
					
					listaTutorias = new ArrayList<TutoriaIMD>();
					
					for(TurmaEntradaTecnico tur: listaTurmas){
						TutoriaIMD tutoria = new TutoriaIMD();
						tutoria = tutoDao.findUltimaByTurmaEntrada(tur.getId());
						if(tutoria != null) {
							listaTutorias.add(tutoria);
						}
					}
					
					if(listaTurmas.isEmpty() || listaTutorias.isEmpty()){
						addMensagemErro("Nenhum registro encontrado de acordo com os crit�rios de busca informados.");
					}
				}
				
			} else if(idOpcaoPoloSelecionado <= 0){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Op��o - P�lo - Grupo");
			} 
		} finally {
			tutoDao.close();
			tDao.close();
		}
	}
	

	
	/** Redireciona a p�gina para o formul�rio de sele��o de op��o polo grupo e turmas.
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 *  <li>/sigaa.war/metropole_digital/assistente_social/selecao_opcao_turmas.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual a p�gina ser� redirecionada
	 * @throws 
	 */
	public String selecionarPoloTurmas(){
		return forward("/metropole_digital/assistente_social/selecao_polo_turmas.jsp");
	}
	
	/** Voltar a p�gina no formul�rio dos filtros do relat�rio
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 *  <li>/sigaa.war/metropole_digital/assistente_social/selecao_filtros.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual a p�gina ser� redirecionada
	 * @throws 
	 */
	public String voltarForm(){
		dataInicioInformada = null;
		dataFimInformada = null;
		qtdFaltasMaximaInformada = null;
		qtdFaltasMinimaInformada = null;
		percFaltasMaximaInformada = null;
		percFaltasMinimaInformada = null;
		
		return forward("/metropole_digital/assistente_social/selecao_polo_turmas.jsf");
	}
	
	/**
	 * Respons�vel por realizar as valida��es nos dados informados e direcionar o usu�rio a 
	 * tela dos filtros do relatorio a ser gerado.
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/assistente_social/selecao_opcao_turmas.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual a p�gina ser� redirecionada
	 * @throws 
	 */
	public String submeterPoloTurmas() throws DAOException {
		
		if (hasOnlyErrors()){
			return null;
		}
		tutoriasSelecionadas = new ArrayList<TutoriaIMD>();
		turmasSelecionadas = new ArrayList<TurmaEntradaTecnico>();
		for (TutoriaIMD tutoria : listaTutorias) {
			if ( tutoria.getTurmaEntrada().getDadosTurmaIMD().isSelecionada()  ){
				turmasSelecionadas.add(tutoria.getTurmaEntrada());
				tutoriasSelecionadas.add(tutoria);
			}
		}
		
		if ( turmasSelecionadas.isEmpty() ) {
			addMensagemErro("� necess�rio selecionar pelo menos uma turma.");
			return null;
		} 
		
		
		
		return forward("/metropole_digital/assistente_social/selecao_filtros.jsp");
	}
	
	
	/**
	 * Respons�vel por realizar as valida��es nos dados informados e direcionar o usu�rio a 
	 * tela do relatorio sem filtros.
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/assistente_social/selecao_opcao_turmas.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual a p�gina ser� redirecionada
	 * @throws 
	 */
	public String submeterPoloTurmasSemFiltros() throws DAOException {
		
		if (hasOnlyErrors()){
			return null;
		}
		
		turmasSelecionadas = new ArrayList<TurmaEntradaTecnico>();
		tutoriasSelecionadas = new ArrayList<TutoriaIMD>();
		for (TutoriaIMD tutoria : listaTutorias) {
			if ( tutoria.getTurmaEntrada().getDadosTurmaIMD().isSelecionada()  ){
				turmasSelecionadas.add(tutoria.getTurmaEntrada());
				tutoriasSelecionadas.add(tutoria);
			}
			
		}
		
		if ( turmasSelecionadas.isEmpty() ) {
			addMensagemErro("� necess�rio informar pelo menos uma turma.");
			return null;
		} 
		
		
		return gerarTabelaRelatorioFrequenciaTurmaSemFiltros();
		//return forward("/metropole_digital/assistente_social/selecao_filtros.jsp");
	}
	
	
	/**
	 * Fun��o que carrega os dados e redireciona para o relatorio de frequencia
	 * da turma com filtros
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/assistente_social/selecao_opcao_turmas.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return P�gina referente � opera��o.
	 */
	public String redirecionarRelatorioFrequenciaFiltrado() throws DAOException {
		int id = 0;
		
		if(getParameter("id") != null) {
			id = getParameterInt("id");
		} 

		if (id > 0) {
			idTurmaEntradaSelecionada = id;
		}
		
		relatorioFiltrado = true;

		turmaEntradaSelecionada = getGenericDAO().findByPrimaryKey(
				idTurmaEntradaSelecionada, TurmaEntradaTecnico.class);

		return gerarTabelaRelatorioFrequenciaTurmaFiltrado();
	
	}
	
	
	
	/**
	 * Fun��o que efetua a gera��o dos valores do relatorio de frequencia da
	 * turma com os filtros
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_frequencia/form.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @throws DAOException
	 * @return
	 **/
	public String gerarRelatorioFrequenciaTurmaFiltrado() throws DAOException {
		AcompanhamentoSemanalDiscenteDao acompDao = new AcompanhamentoSemanalDiscenteDao();
		try {
			
			boolean retornouResultadosFiltros = false;
			listaRelatorioFrequencia = new ArrayList<RelatorioFrequenciaTurmaIMD>();
			
			listaGeralAcompanhamentos = new ArrayList<AcompanhamentoSemanalDiscente>();
			listaGeralAcompanhamentos = acompDao.findAcompanhamentosByListaTurmasProjetadoOrdenado(listaTurmas);
			
			
			//VERIFICA��O SE O RELAT�RIO � CONFIGUR�VEL E SE UMA DAS DATAS FOI INFORMADA
			if(dataInicioInformada != null || dataFimInformada != null) {
				
				if(dataInicioInformada != null && dataFimInformada != null){
					if(! dataFimInformada.before(dataInicioInformada) && ! dataFimInformada.after(dataInicioInformada)){
						addMessage("A data in�cio � igual que a data fim.",TipoMensagemUFRN.ERROR);
						return forward("/metropole_digital/assistente_social/selecao_filtros.jsp");
					}
					else if(dataFimInformada.before(dataInicioInformada)){
						addMessage("A data in�cio � maior que a data fim.",TipoMensagemUFRN.ERROR);
						return forward("/metropole_digital/assistente_social/selecao_filtros.jsp");
					}
				}
				
				List<AcompanhamentoSemanalDiscente> listaAcompFiltrada = new ArrayList<AcompanhamentoSemanalDiscente>();
				for(AcompanhamentoSemanalDiscente ac: listaGeralAcompanhamentos) {
					
					if(dataInicioInformada != null && dataFimInformada != null){
						if(((! ac.getPeriodoAvaliacao().getDataInicio().before(dataInicioInformada) && ! ac.getPeriodoAvaliacao().getDataInicio().after(dataInicioInformada)) || ac.getPeriodoAvaliacao().getDataInicio().after(dataInicioInformada)) && (((!ac.getPeriodoAvaliacao().getDatafim().before(dataFimInformada) && !ac.getPeriodoAvaliacao().getDatafim().after(dataFimInformada)) || ac.getPeriodoAvaliacao().getDatafim().before(dataFimInformada)))){
							listaAcompFiltrada.add(ac);
							if(qtdFaltasMinimaInformada == null && qtdFaltasMaximaInformada == null && percFaltasMaximaInformada == null && percFaltasMinimaInformada == null) {
								retornouResultadosFiltros = true;
							}
						}
					} else if(dataInicioInformada != null){
						if(((! ac.getPeriodoAvaliacao().getDataInicio().before(dataInicioInformada) && ! ac.getPeriodoAvaliacao().getDataInicio().after(dataInicioInformada)) || ac.getPeriodoAvaliacao().getDataInicio().after(dataInicioInformada))){
							listaAcompFiltrada.add(ac);
							if(qtdFaltasMinimaInformada == null && qtdFaltasMaximaInformada == null && percFaltasMaximaInformada == null && percFaltasMinimaInformada == null) {
								retornouResultadosFiltros = true;
							}
						}
					} else if (dataFimInformada != null) {
						if (((!ac.getPeriodoAvaliacao().getDatafim().before(dataFimInformada) && !ac.getPeriodoAvaliacao().getDatafim().after(dataFimInformada)) || ac.getPeriodoAvaliacao().getDatafim().before(dataFimInformada))){
							listaAcompFiltrada.add(ac);
							if(qtdFaltasMinimaInformada == null && qtdFaltasMaximaInformada == null && percFaltasMaximaInformada == null && percFaltasMinimaInformada == null) {
								retornouResultadosFiltros = true;
							}
						}
					}
				}
				listaGeralAcompanhamentos = listaAcompFiltrada;
				
				
				DiscenteTecnico discente = new DiscenteTecnico();
				int contador = 0;
				int idDiscenteAuxiliar = 0;
				Double qtdFaltas = (double) 0; 
				Double chExecutada = (double) 0;
				Double chPresenca = (double) 0;
				Double chFaltas = (double) 0;
				Double chTotal = (double) 0;
				Double percentualFaltas = (double) 0;
				DecimalFormat df = new DecimalFormat("0.00");  
				String percentualFaltasTexto = "0";

				for (AcompanhamentoSemanalDiscente acomp : listaGeralAcompanhamentos) {

					if (contador == 0) {
						discente = acomp.getDiscente();
					}

					if (idDiscenteAuxiliar != acomp.getDiscente().getId()) {
						if (contador > 0) {
							chFaltas = chExecutada - chPresenca;
							if (chExecutada > 0) {
								percentualFaltas = (double) ((chFaltas * 100) / chExecutada);
								
								percentualFaltasTexto = df.format((double) ((chFaltas * 100) / chExecutada));
							}

							RelatorioFrequenciaTurmaIMD relatorio = new RelatorioFrequenciaTurmaIMD();
							relatorio.setDiscente(discente);
							relatorio.setChExecutada(chExecutada);
							relatorio.setChFaltas(chFaltas);
							relatorio.setChTotal(chTotal);
							relatorio.setPercentualFaltas(percentualFaltas);
							relatorio.setPercentualFaltasTexto(percentualFaltasTexto);
							relatorio.setQtdFaltas(qtdFaltas);

							listaRelatorioFrequencia.add(relatorio);

							chExecutada = (double) 0;
							chPresenca = (double) 0;
							chFaltas = (double) 0;
							percentualFaltas = (double) 0;
							chTotal = (double) 0;
							percentualFaltasTexto = "0";
							qtdFaltas = (double) 0;
									
						}

						idDiscenteAuxiliar = acomp.getDiscente().getId();
						discente = acomp.getDiscente();
					}

					if (acomp.getFrequencia() != null) {
						chExecutada += (double) (acomp.getPeriodoAvaliacao().getChTotalPeriodo());
						chPresenca += (double) ((acomp.getPeriodoAvaliacao().getChTotalPeriodo() * acomp.getFrequencia()));
						if(acomp.getFrequencia() == 0){
							qtdFaltas ++;
						} else if(acomp.getFrequencia() == 0.5){
							qtdFaltas += 0.5;
						}
					}

					chTotal += (double) (acomp.getPeriodoAvaliacao().getChTotalPeriodo());
					contador++;
					
				}
				
				if (contador > 0) {
					chFaltas = chExecutada - chPresenca;
					if (chExecutada > 0) {
						percentualFaltas = (double) ((chFaltas * 100) / chExecutada);
						
						percentualFaltasTexto = df.format((double) ((chFaltas * 100) / chExecutada));
					}

					RelatorioFrequenciaTurmaIMD relatorio = new RelatorioFrequenciaTurmaIMD();
					relatorio.setDiscente(discente);
					relatorio.setChExecutada(chExecutada);
					relatorio.setChFaltas(chFaltas);
					relatorio.setChTotal(chTotal);
					relatorio.setPercentualFaltas(percentualFaltas);
					relatorio.setPercentualFaltasTexto(percentualFaltasTexto);
					relatorio.setQtdFaltas(qtdFaltas);
					
					listaRelatorioFrequencia.add(relatorio);

					chExecutada = (double) 0;
					chPresenca = (double) 0;
					chFaltas = (double) 0;
					percentualFaltas = (double) 0;
					chTotal = (double) 0;
					percentualFaltasTexto = "0";
					qtdFaltas = (double) 0;
				}
				
				
				
				//VERIFICA��O SE O RELAT�RIO � CONFIGUR�VEL E SE O FILTRO SOBRE A QUANTIDADE DE FALTAS FOI INFORMADO
				if(qtdFaltasMinimaInformada != null || qtdFaltasMaximaInformada != null) {
					
					if(qtdFaltasMinimaInformada != null && qtdFaltasMaximaInformada != null) {
						if(qtdFaltasMinimaInformada > qtdFaltasMaximaInformada) {
							addMessage("A quantidade m�nima de faltas � maior que a quantidade m�xima de faltas.",TipoMensagemUFRN.ERROR);
							return forward("/metropole_digital/assistente_social/selecao_filtros.jsp");
						}
					}
					
					List<RelatorioFrequenciaTurmaIMD> listaRelatorioFiltrada = new ArrayList<RelatorioFrequenciaTurmaIMD>();
					for(RelatorioFrequenciaTurmaIMD reg: listaRelatorioFrequencia) {
						if(qtdFaltasMinimaInformada != null && qtdFaltasMaximaInformada != null) {
							if(qtdFaltasMinimaInformada <= reg.getQtdFaltas() && qtdFaltasMaximaInformada >= reg.getQtdFaltas()){
								listaRelatorioFiltrada.add(reg);
								retornouResultadosFiltros = true;
							}
						} else if(qtdFaltasMinimaInformada != null) {
							if(qtdFaltasMinimaInformada <= reg.getQtdFaltas()){
								listaRelatorioFiltrada.add(reg);
								retornouResultadosFiltros = true;
							}
						} else if(qtdFaltasMaximaInformada != null) {
							if(qtdFaltasMaximaInformada >= reg.getQtdFaltas()){
								listaRelatorioFiltrada.add(reg);
								retornouResultadosFiltros = true;
							}
						}
					}
					listaRelatorioFrequencia = listaRelatorioFiltrada;
				} 
				
				
				
				
				
				//VERIFICA��O SE O RELAT�RIO � CONFIGUR�VEL E SE O FILTRO SOBRE A PERCENTUAL DE FALTAS FOI INFORMADO
				if(percFaltasMinimaInformada != null || percFaltasMaximaInformada != null) {
					
					if(percFaltasMinimaInformada != null && percFaltasMaximaInformada != null) {
						if(percFaltasMinimaInformada > percFaltasMaximaInformada) {
							addMessage("O percentual m�nimo de faltas � maior que o percentual m�ximo de faltas.",TipoMensagemUFRN.ERROR);
							return forward("/metropole_digital/assistente_social/selecao_filtros.jsp");
						}
					}
					
					List<RelatorioFrequenciaTurmaIMD> listaRelatorioFiltrada = new ArrayList<RelatorioFrequenciaTurmaIMD>();
					for(RelatorioFrequenciaTurmaIMD reg: listaRelatorioFrequencia) {
						if(percFaltasMinimaInformada != null && percFaltasMaximaInformada != null) {
							if(percFaltasMinimaInformada <= reg.getPercentualFaltas() && percFaltasMaximaInformada >= reg.getPercentualFaltas()){
								listaRelatorioFiltrada.add(reg);
								retornouResultadosFiltros = true;
							}
						} else if(percFaltasMinimaInformada != null) {
							if(percFaltasMinimaInformada <= reg.getPercentualFaltas()){
								listaRelatorioFiltrada.add(reg);
								retornouResultadosFiltros = true;
							}
						} else if(percFaltasMaximaInformada != null) {
							if(percFaltasMaximaInformada >= reg.getPercentualFaltas()){
								listaRelatorioFiltrada.add(reg);
								retornouResultadosFiltros = true;
							}
						}
					}
					listaRelatorioFrequencia = listaRelatorioFiltrada;
				} 
				
				relatorioFiltrado = false;
				if(retornouResultadosFiltros){
					return forward("/metropole_digital/assistente_social/relatorio_frequencia.jsp");
				} else {
					addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
					return null;
				}
				
			} else {
				addMessage("Para gerar o relat�rio com os filtros, a data in�cio ou a data fim deve ser informada.",TipoMensagemUFRN.ERROR);
				return forward("/metropole_digital/assistente_social/selecao_filtros.jsp");
			}

			

		} finally {
			acompDao.close();
		}
	}
	
	
	/**
	 * Fun��o que efetua a gera��o dos valores do relatorio de frequencia da
	 * turma com os filtros
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_frequencia/form.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @throws DAOException
	 * @return
	 **/
	public String gerarTabelaRelatorioFrequenciaTurmaFiltrado() throws DAOException {
		AcompanhamentoSemanalDiscenteDao acompDao = new AcompanhamentoSemanalDiscenteDao();
		try {
			
			boolean retornouResultadosFiltros = false;
			listaRelatorioFrequencia = new ArrayList<RelatorioFrequenciaTurmaIMD>();
			
			listaGeralAcompanhamentos = new ArrayList<AcompanhamentoSemanalDiscente>();
			listaGeralAcompanhamentos = acompDao.findAcompanhamentosByListaTurmasProjetadoOrdenado(listaTurmas);
			
			
			//VERIFICA��O SE O RELAT�RIO � CONFIGUR�VEL E SE UMA DAS DATAS FOI INFORMADA
			if(dataInicioInformada != null || dataFimInformada != null) {
				
				if(dataInicioInformada != null && dataFimInformada != null){
					/*if(! dataFimInformada.before(dataInicioInformada) && ! dataFimInformada.after(dataInicioInformada)){
						addMessage("A data in�cio � igual que a data fim.",TipoMensagemUFRN.ERROR);
						return forward("/metropole_digital/assistente_social/selecao_filtros.jsp");
					}
					else */ 
						
					if(dataFimInformada.before(dataInicioInformada)){
						addMessage("A data in�cio � maior que a data fim.",TipoMensagemUFRN.ERROR);
						return forward("/metropole_digital/assistente_social/selecao_filtros.jsp");
					}
				}
				
				List<AcompanhamentoSemanalDiscente> listaAcompFiltrada = new ArrayList<AcompanhamentoSemanalDiscente>();
				for(AcompanhamentoSemanalDiscente ac: listaGeralAcompanhamentos) {
					
					if(dataInicioInformada != null && dataFimInformada != null){
						if(((! ac.getPeriodoAvaliacao().getDataInicio().before(dataInicioInformada) && ! ac.getPeriodoAvaliacao().getDataInicio().after(dataInicioInformada)) || ac.getPeriodoAvaliacao().getDataInicio().after(dataInicioInformada)) && (((!ac.getPeriodoAvaliacao().getDatafim().before(dataFimInformada) && !ac.getPeriodoAvaliacao().getDatafim().after(dataFimInformada)) || ac.getPeriodoAvaliacao().getDatafim().before(dataFimInformada)))){
							listaAcompFiltrada.add(ac);
							if(qtdFaltasMinimaInformada == null && qtdFaltasMaximaInformada == null && percFaltasMaximaInformada == null && percFaltasMinimaInformada == null) {
								retornouResultadosFiltros = true;
							}
						}
					} else if(dataInicioInformada != null){
						if(((! ac.getPeriodoAvaliacao().getDataInicio().before(dataInicioInformada) && ! ac.getPeriodoAvaliacao().getDataInicio().after(dataInicioInformada)) || ac.getPeriodoAvaliacao().getDataInicio().after(dataInicioInformada))){
							listaAcompFiltrada.add(ac);
							if(qtdFaltasMinimaInformada == null && qtdFaltasMaximaInformada == null && percFaltasMaximaInformada == null && percFaltasMinimaInformada == null) {
								retornouResultadosFiltros = true;
							}
						}
					} else if (dataFimInformada != null) {
						if (((!ac.getPeriodoAvaliacao().getDatafim().before(dataFimInformada) && !ac.getPeriodoAvaliacao().getDatafim().after(dataFimInformada)) || ac.getPeriodoAvaliacao().getDatafim().before(dataFimInformada))){
							listaAcompFiltrada.add(ac);
							if(qtdFaltasMinimaInformada == null && qtdFaltasMaximaInformada == null && percFaltasMaximaInformada == null && percFaltasMinimaInformada == null) {
								retornouResultadosFiltros = true;
							}
						}
					}
				}
				listaGeralAcompanhamentos = listaAcompFiltrada;
				
				
				DiscenteTecnico discente = new DiscenteTecnico();
				int contador = 0;
				int idDiscenteAuxiliar = 0;
				Double qtdFaltas = (double) 0; 
				Double chExecutada = (double) 0;
				Double chPresenca = (double) 0;
				Double chFaltas = (double) 0;
				Double chTotal = (double) 0;
				Double percentualFaltas = (double) 0;
				DecimalFormat df = new DecimalFormat("0.00");  
				String percentualFaltasTexto = "0";

				for (AcompanhamentoSemanalDiscente acomp : listaGeralAcompanhamentos) {

					if (contador == 0) {
						discente = acomp.getDiscente();
					}

					if (idDiscenteAuxiliar != acomp.getDiscente().getId()) {
						if (contador > 0) {
							chFaltas = chExecutada - chPresenca;
							if (chExecutada > 0) {
								percentualFaltas = (double) ((chFaltas * 100) / chExecutada);
								
								percentualFaltasTexto = df.format((double) ((chFaltas * 100) / chExecutada));
							}

							RelatorioFrequenciaTurmaIMD relatorio = new RelatorioFrequenciaTurmaIMD();
							relatorio.setDiscente(discente);
							relatorio.setChExecutada(chExecutada);
							relatorio.setChFaltas(chFaltas);
							relatorio.setChTotal(chTotal);
							relatorio.setPercentualFaltas(percentualFaltas);
							relatorio.setPercentualFaltasTexto(percentualFaltasTexto);
							relatorio.setQtdFaltas(qtdFaltas);

							listaRelatorioFrequencia.add(relatorio);

							chExecutada = (double) 0;
							chPresenca = (double) 0;
							chFaltas = (double) 0;
							percentualFaltas = (double) 0;
							chTotal = (double) 0;
							percentualFaltasTexto = "0";
							qtdFaltas = (double) 0;
									
						}

						idDiscenteAuxiliar = acomp.getDiscente().getId();
						discente = acomp.getDiscente();
					}

					if (acomp.getFrequencia() != null) {
						chExecutada += (double) (acomp.getPeriodoAvaliacao().getChTotalPeriodo());
						chPresenca += (double) ((acomp.getPeriodoAvaliacao().getChTotalPeriodo() * acomp.getFrequencia()));
						if(acomp.getFrequencia() == 0){
							qtdFaltas ++;
						} else if(acomp.getFrequencia() == 0.5){
							qtdFaltas += 0.5;
						}
					}

					chTotal += (double) (acomp.getPeriodoAvaliacao().getChTotalPeriodo());
					contador++;
					
				}
				
				if (contador > 0) {
					chFaltas = chExecutada - chPresenca;
					if (chExecutada > 0) {
						percentualFaltas = (double) ((chFaltas * 100) / chExecutada);
						
						percentualFaltasTexto = df.format((double) ((chFaltas * 100) / chExecutada));
					}

					RelatorioFrequenciaTurmaIMD relatorio = new RelatorioFrequenciaTurmaIMD();
					relatorio.setDiscente(discente);
					relatorio.setChExecutada(chExecutada);
					relatorio.setChFaltas(chFaltas);
					relatorio.setChTotal(chTotal);
					relatorio.setPercentualFaltas(percentualFaltas);
					relatorio.setPercentualFaltasTexto(percentualFaltasTexto);
					relatorio.setQtdFaltas(qtdFaltas);
					
					listaRelatorioFrequencia.add(relatorio);

					chExecutada = (double) 0;
					chPresenca = (double) 0;
					chFaltas = (double) 0;
					percentualFaltas = (double) 0;
					chTotal = (double) 0;
					percentualFaltasTexto = "0";
					qtdFaltas = (double) 0;
				}
				
				
				
				//VERIFICA��O SE O RELAT�RIO � CONFIGUR�VEL E SE O FILTRO SOBRE A QUANTIDADE DE FALTAS FOI INFORMADO
				if(qtdFaltasMinimaInformada != null || qtdFaltasMaximaInformada != null) {
					
					if(qtdFaltasMinimaInformada != null && qtdFaltasMaximaInformada != null) {
						if(qtdFaltasMinimaInformada > qtdFaltasMaximaInformada) {
							addMessage("A quantidade m�nima de faltas � maior que a quantidade m�xima de faltas.",TipoMensagemUFRN.ERROR);
							return forward("/metropole_digital/assistente_social/selecao_filtros.jsp");
						}
					}
					
					List<RelatorioFrequenciaTurmaIMD> listaRelatorioFiltrada = new ArrayList<RelatorioFrequenciaTurmaIMD>();
					for(RelatorioFrequenciaTurmaIMD reg: listaRelatorioFrequencia) {
						if(qtdFaltasMinimaInformada != null && qtdFaltasMaximaInformada != null) {
							if(qtdFaltasMinimaInformada <= reg.getQtdFaltas() && qtdFaltasMaximaInformada >= reg.getQtdFaltas()){
								listaRelatorioFiltrada.add(reg);
								retornouResultadosFiltros = true;
							}
						} else if(qtdFaltasMinimaInformada != null) {
							if(qtdFaltasMinimaInformada <= reg.getQtdFaltas()){
								listaRelatorioFiltrada.add(reg);
								retornouResultadosFiltros = true;
							}
						} else if(qtdFaltasMaximaInformada != null) {
							if(qtdFaltasMaximaInformada >= reg.getQtdFaltas()){
								listaRelatorioFiltrada.add(reg);
								retornouResultadosFiltros = true;
							}
						}
					}
					listaRelatorioFrequencia = listaRelatorioFiltrada;
				} 
				
				
				
				
				
				//VERIFICA��O SE O RELAT�RIO � CONFIGUR�VEL E SE O FILTRO SOBRE A PERCENTUAL DE FALTAS FOI INFORMADO
				if(percFaltasMinimaInformada != null || percFaltasMaximaInformada != null) {
					
					if(percFaltasMinimaInformada != null && percFaltasMaximaInformada != null) {
						if(percFaltasMinimaInformada > percFaltasMaximaInformada) {
							addMessage("O percentual m�nimo de faltas � maior que o percentual m�ximo de faltas.",TipoMensagemUFRN.ERROR);
							return forward("/metropole_digital/assistente_social/selecao_filtros.jsp");
						}
					}
					
					List<RelatorioFrequenciaTurmaIMD> listaRelatorioFiltrada = new ArrayList<RelatorioFrequenciaTurmaIMD>();
					for(RelatorioFrequenciaTurmaIMD reg: listaRelatorioFrequencia) {
						if(percFaltasMinimaInformada != null && percFaltasMaximaInformada != null) {
							if(percFaltasMinimaInformada <= reg.getPercentualFaltas() && percFaltasMaximaInformada >= reg.getPercentualFaltas()){
								listaRelatorioFiltrada.add(reg);
								retornouResultadosFiltros = true;
							}
						} else if(percFaltasMinimaInformada != null) {
							if(percFaltasMinimaInformada <= reg.getPercentualFaltas()){
								listaRelatorioFiltrada.add(reg);
								retornouResultadosFiltros = true;
							}
						} else if(percFaltasMaximaInformada != null) {
							if(percFaltasMaximaInformada >= reg.getPercentualFaltas()){
								listaRelatorioFiltrada.add(reg);
								retornouResultadosFiltros = true;
							}
						}
					}
					listaRelatorioFrequencia = listaRelatorioFiltrada;
				} 
				
				tabelaRelatorioFrequencia = new ArrayList<ArrayList<RelatorioFrequenciaTurmaIMD>>();
				ArrayList<RelatorioFrequenciaTurmaIMD> itemLista = new ArrayList<RelatorioFrequenciaTurmaIMD>();
				int contadorTurmas = 0;
				String especializacao = "";
				
				for(RelatorioFrequenciaTurmaIMD item: listaRelatorioFrequencia){
					if(especializacao.equalsIgnoreCase("") && contadorTurmas == 0){
						itemLista = new ArrayList<RelatorioFrequenciaTurmaIMD>();
						especializacao = item.getDiscente().getTurmaEntradaTecnico().getEspecializacao().getDescricao();
					}
					if(especializacao.equalsIgnoreCase(item.getDiscente().getTurmaEntradaTecnico().getEspecializacao().getDescricao())){
						itemLista.add(item);
						contadorTurmas++;
					} else {
						tabelaRelatorioFrequencia.add(itemLista);
						itemLista = new ArrayList<RelatorioFrequenciaTurmaIMD>();
						contadorTurmas = 0;
						especializacao = item.getDiscente().getTurmaEntradaTecnico().getEspecializacao().getDescricao();
						itemLista.add(item);
					}
				}

				if(contadorTurmas > 0){
					tabelaRelatorioFrequencia.add(itemLista);
				}
				
				relatorioFiltrado = false;
				if(retornouResultadosFiltros){
					return forward("/metropole_digital/assistente_social/relatorio_frequencia.jsp");
				} else {
					addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
					return null;
				}
				
			} else {
				addMessage("Para gerar o relat�rio com os filtros, a data in�cio ou a data fim deve ser informada.",TipoMensagemUFRN.ERROR);
				return forward("/metropole_digital/assistente_social/selecao_filtros.jsp");
			}

			

		} finally {
			acompDao.close();
		}
	}	
	
	
	
	/**
	 * Fun��o que efetua a gera��o dos valores do relatorio de frequencia da
	 * turma SEM filtros
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/lancar_frequencia/form.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @throws DAOException
	 * @return
	 **/
	public String gerarTabelaRelatorioFrequenciaTurmaSemFiltros() throws DAOException {
		AcompanhamentoSemanalDiscenteDao acompDao = new AcompanhamentoSemanalDiscenteDao();
		try {
			
			listaRelatorioFrequencia = new ArrayList<RelatorioFrequenciaTurmaIMD>();
			
			listaGeralAcompanhamentos = new ArrayList<AcompanhamentoSemanalDiscente>();
			listaGeralAcompanhamentos = acompDao.findAcompanhamentosByListaTurmasProjetadoOrdenado(listaTurmas);
							
			DiscenteTecnico discente = new DiscenteTecnico();
			int contador = 0;
			int idDiscenteAuxiliar = 0;
			Double qtdFaltas = (double) 0; 
			Double chExecutada = (double) 0;
			Double chPresenca = (double) 0;
			Double chFaltas = (double) 0;
			Double chTotal = (double) 0;
			Double percentualFaltas = (double) 0;
			DecimalFormat df = new DecimalFormat("0.00");  
			String percentualFaltasTexto = "0";

			for (AcompanhamentoSemanalDiscente acomp : listaGeralAcompanhamentos) {

				if (contador == 0) {
					discente = acomp.getDiscente();
				}

				if (idDiscenteAuxiliar != acomp.getDiscente().getId()) {
					if (contador > 0) {
						chFaltas = chExecutada - chPresenca;
						if (chExecutada > 0) {
							percentualFaltas = (double) ((chFaltas * 100) / chExecutada);
							
							percentualFaltasTexto = df.format((double) ((chFaltas * 100) / chExecutada));
						}

						RelatorioFrequenciaTurmaIMD relatorio = new RelatorioFrequenciaTurmaIMD();
						relatorio.setDiscente(discente);
						relatorio.setChExecutada(chExecutada);
						relatorio.setChFaltas(chFaltas);
						relatorio.setChTotal(chTotal);
						relatorio.setPercentualFaltas(percentualFaltas);
						relatorio.setPercentualFaltasTexto(percentualFaltasTexto);
						relatorio.setQtdFaltas(qtdFaltas);

						listaRelatorioFrequencia.add(relatorio);

						chExecutada = (double) 0;
						chPresenca = (double) 0;
						chFaltas = (double) 0;
						percentualFaltas = (double) 0;
						chTotal = (double) 0;
						percentualFaltasTexto = "0";
						qtdFaltas = (double) 0;
								
					}

					idDiscenteAuxiliar = acomp.getDiscente().getId();
					discente = acomp.getDiscente();
				}

				if (acomp.getFrequencia() != null) {
					chExecutada += (double) (acomp.getPeriodoAvaliacao().getChTotalPeriodo());
					chPresenca += (double) ((acomp.getPeriodoAvaliacao().getChTotalPeriodo() * acomp.getFrequencia()));
					if(acomp.getFrequencia() == 0){
						qtdFaltas ++;
					} else if(acomp.getFrequencia() == 0.5){
						qtdFaltas += 0.5;
					}
				}

				chTotal += (double) (acomp.getPeriodoAvaliacao().getChTotalPeriodo());
				contador++;
				
			}
			
			if (contador > 0) {
				chFaltas = chExecutada - chPresenca;
				if (chExecutada > 0) {
					percentualFaltas = (double) ((chFaltas * 100) / chExecutada);
					
					percentualFaltasTexto = df.format((double) ((chFaltas * 100) / chExecutada));
				}

				RelatorioFrequenciaTurmaIMD relatorio = new RelatorioFrequenciaTurmaIMD();
				relatorio.setDiscente(discente);
				relatorio.setChExecutada(chExecutada);
				relatorio.setChFaltas(chFaltas);
				relatorio.setChTotal(chTotal);
				relatorio.setPercentualFaltas(percentualFaltas);
				relatorio.setPercentualFaltasTexto(percentualFaltasTexto);
				relatorio.setQtdFaltas(qtdFaltas);
				
				listaRelatorioFrequencia.add(relatorio);

				chExecutada = (double) 0;
				chPresenca = (double) 0;
				chFaltas = (double) 0;
				percentualFaltas = (double) 0;
				chTotal = (double) 0;
				percentualFaltasTexto = "0";
				qtdFaltas = (double) 0;
			}
			
			
			tabelaRelatorioFrequencia = new ArrayList<ArrayList<RelatorioFrequenciaTurmaIMD>>();
			ArrayList<RelatorioFrequenciaTurmaIMD> itemLista = new ArrayList<RelatorioFrequenciaTurmaIMD>();
			int contadorTurmas = 0;
			String especializacao = "";
			
			for(RelatorioFrequenciaTurmaIMD item: listaRelatorioFrequencia){
				if(especializacao.equalsIgnoreCase("") && contadorTurmas == 0){
					itemLista = new ArrayList<RelatorioFrequenciaTurmaIMD>();
					especializacao = item.getDiscente().getTurmaEntradaTecnico().getEspecializacao().getDescricao();
				}
				if(especializacao.equalsIgnoreCase(item.getDiscente().getTurmaEntradaTecnico().getEspecializacao().getDescricao())){
					itemLista.add(item);
					contadorTurmas++;
				} else {
					tabelaRelatorioFrequencia.add(itemLista);
					itemLista = new ArrayList<RelatorioFrequenciaTurmaIMD>();
					contadorTurmas = 0;
					especializacao = item.getDiscente().getTurmaEntradaTecnico().getEspecializacao().getDescricao();
					itemLista.add(item);
				}
			}

			if(contadorTurmas > 0){
				tabelaRelatorioFrequencia.add(itemLista);
			}
			
			if(! listaRelatorioFrequencia.isEmpty()){
				return forward("/metropole_digital/assistente_social/relatorio_frequencia.jsp");
			} else {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
				

		} finally {
			acompDao.close();
		}
	}	
	

	/**
	 * A��o de cancelamento da p�gina de sele��o de filtros 
	 * 
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/assistente_social/selecao_opcao_turmas.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual a p�gina ser� redirecionada
	 * @throws 
	 */
	public String cancelarFiltros() {
		
		return forward("/metropole_digital/menus/menu_imd.jsp");
		
	}
	
}
