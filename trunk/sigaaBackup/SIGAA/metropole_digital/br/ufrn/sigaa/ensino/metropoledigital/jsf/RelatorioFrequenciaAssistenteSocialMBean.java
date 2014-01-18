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
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Entidade responsável pelo gerenciamento da funcionalidade do relatório de frequencia do assistente social do IMD.
 * 
 * 
 * @author Rafael Barros
 *
 */

@Scope("request")
@Component("relatorioFrequenciaAssitenteSocial")
public class RelatorioFrequenciaAssistenteSocialMBean extends SigaaAbstractController {
	
	/**Coleção de itens do combobox de polo**/
	private Collection<SelectItem> opcaoPolosCombo = new ArrayList<SelectItem>();
	
	/**Coleção de itens do combobox de modulo**/
	private Collection<SelectItem> modulosCombo = new ArrayList<SelectItem>();
	
	/**Variável que corresponde ao ID do polo selecionado**/
	private int idOpcaoPoloSelecionado = 0;
	
	/**Variável que corresponde ao ID do polo selecionado Antigo**/
	private int idOpcaoPoloSelecionadoAntigo = 0;
	
	/**Variável que corresponde ao ID do módulo selecionado**/
	private int idModuloSelecionado = 0;
	
	/**Coleção de turmas que estão associadas ao polo selecionado**/
	private Collection<TurmaEntradaTecnico> listaTurmas = new ArrayList<TurmaEntradaTecnico> ();
	
	/**Variável que corresponde ao ID da turma selecionada**/
	private int idTurmaEntradaSelecionada = 0;
	
	/**Entidade que corresponde a turma de entrada selecionada na qual os alunos serão matriculados**/
	private TurmaEntradaTecnico turmaEntradaSelecionada = new TurmaEntradaTecnico();
	
	/**Entidade que corresponde a turma de entrada que armazenará os dados informados para filtrar as turmas**/
	private TurmaEntradaTecnico turmaFiltros = new TurmaEntradaTecnico();
	
	/**Entidade que corresponde a opção polo grupo selecionada na qual as turmas serão listadas**/
	private OpcaoPoloGrupo opcaoPoloSelecionada = new OpcaoPoloGrupo();
	
	/**Entidade que corresponde ao modulo selecionado na qual as turmas serão listadas**/
	private Modulo moduloSelecionado = new Modulo();
	
	/**Coleção de discentes não enturmados que estão associadas ao polo selecionado**/
	private Collection<DiscenteTecnico> listaDiscentes = new ArrayList<DiscenteTecnico> ();
	
	/**Coleção de turmas de entrada selecionadas para o relatorio de frequencia do assistente social do IMD**/
	private Collection<TurmaEntradaTecnico> turmasSelecionadas = new ArrayList<TurmaEntradaTecnico>();
	
	/**Coleção de tutorias buscadas para o relatorio de frequencia do assistente social do IMD**/
	private ArrayList<TutoriaIMD> listaTutorias = new ArrayList<TutoriaIMD>();
	
	/**Coleção de tutorias SELECIONADAS para o relatorio de frequencia do assistente social do IMD**/
	private ArrayList<TutoriaIMD> tutoriasSelecionadas = new ArrayList<TutoriaIMD>();

	
	//VARIÁVEIS QUE SERÃO UTILIZADAS PARA O RELATÓRIO DE FREQUÊNCIA POR TURMA CONFIGURÁVEL
	/**Corresponde a data inicio informada pelo usuário para geração do relatório de frequência configurável*/
	private boolean relatorioFiltrado = false;
	
	/**Corresponde a data inicio informada pelo usuário para geração do relatório de frequência configurável*/
	private Date dataInicioInformada = null;
	
	/**Corresponde a data fim informada pelo usuário para geração do relatório de frequência configurável*/
	private Date dataFimInformada = null;
	
	/**Corresponde a quantidade mínima de faltas informada pelo usuário para geração do relatório de frequência configurável*/
	private Integer qtdFaltasMinimaInformada = null;
	
	/**Corresponde a quantidade máxima de faltas informada pelo usuário para geração do relatório de frequência configurável*/
	private Integer qtdFaltasMaximaInformada = null;
	
	/**Corresponde ao percentual mínimo de faltas informado pelo usuário para geração do relatório de frequência configurável*/
	private Integer percFaltasMinimaInformada = null;
	
	/**Corresponde ao percentual máximo de faltas informado pelo usuário para geração do relatório de frequência configurável*/
	private Integer percFaltasMaximaInformada = null;
	
	/**
	 * Entidade que irá armazenar a listagem dos discentes do curso técnico que
	 * compõem a turma de entrada selecionada para se efetuar a frequência
	 **/
	private Collection<AcompanhamentoSemanalDiscente> listaGeralAcompanhamentos;

	/**
	 * Entidade que irá armazenar a listagem dos objetos correspondentes ao
	 * relatório de frequencia da turma
	 **/
	private Collection<RelatorioFrequenciaTurmaIMD> listaRelatorioFrequencia;
	
	/**
	 * Entidade que irá armazenar uma tabela com as listagens dos objetos correspondentes ao
	 * relatório de frequencia da turma
	 **/
	private ArrayList<ArrayList<RelatorioFrequenciaTurmaIMD>> tabelaRelatorioFrequencia;
	

	
	public RelatorioFrequenciaAssistenteSocialMBean(){
		opcaoPolosCombo = Collections.emptyList();
		turmaEntradaSelecionada = new TurmaEntradaTecnico();
		opcaoPoloSelecionada = new OpcaoPoloGrupo();
		listaTurmas = new ArrayList<TurmaEntradaTecnico>();
		listaDiscentes = new ArrayList<DiscenteTecnico>(); 
	}
	
	
	/** Método responsável por retornar o atributo polosCombo, caso o atributo esteja vazio, o método efetua o preenchimento da lista.
	 * 
	 * Método é chamado nas seguintes JSP's:
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
	
	
	/** Método responsável por retornar o atributo listaTurmas, caso o atributo esteja vazio, o método efetua o preenchimento da lista.
	 * 
	 * Método é chamado nas seguintes JSP's:
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

	/** Método responsável por retornar o atributo opcaoPoloSelecionada, caso o atributo esteja vazio, o método efetua o preenchimento da lista.
	 * 
	 * Método é chamado nas seguintes JSP's:
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

	/** Método responsável por retornar o atributo moduloSelecionado, caso o atributo esteja vazio, o método efetua o preenchimento da lista.
	 * 
	 * Método é chamado nas seguintes JSP's:
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
	
	/** Método responsável por retornar o atributo modulosCombo, caso o atributo esteja vazio, o método efetua o preenchimento da lista.
	 * 
	 * Método é chamado nas seguintes JSP's:
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


	/** Método responsável por preencher a lista de turmas quando a opção polo grupo for alterada.
	 * 
	 * Método é chamado nas seguintes JSP's:
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
						addMensagemErro("Período: O valor deve ser maior ou igual a 1 e menor ou igual a 2.");
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
						addMensagemErro("Nenhum registro encontrado de acordo com os critérios de busca informados.");
					}
				}
				
			} else if(idOpcaoPoloSelecionado <= 0){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Opção - Pólo - Grupo");
			} 
		} finally {
			tutoDao.close();
			tDao.close();
		}
	}
	

	
	/** Redireciona a página para o formulário de seleção de opção polo grupo e turmas.
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 *  <li>/sigaa.war/metropole_digital/assistente_social/selecao_opcao_turmas.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual a página será redirecionada
	 * @throws 
	 */
	public String selecionarPoloTurmas(){
		return forward("/metropole_digital/assistente_social/selecao_polo_turmas.jsp");
	}
	
	/** Voltar a página no formulário dos filtros do relatório
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 *  <li>/sigaa.war/metropole_digital/assistente_social/selecao_filtros.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual a página será redirecionada
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
	 * Responsável por realizar as validações nos dados informados e direcionar o usuário a 
	 * tela dos filtros do relatorio a ser gerado.
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/assistente_social/selecao_opcao_turmas.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual a página será redirecionada
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
			addMensagemErro("É necessário selecionar pelo menos uma turma.");
			return null;
		} 
		
		
		
		return forward("/metropole_digital/assistente_social/selecao_filtros.jsp");
	}
	
	
	/**
	 * Responsável por realizar as validações nos dados informados e direcionar o usuário a 
	 * tela do relatorio sem filtros.
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/assistente_social/selecao_opcao_turmas.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual a página será redirecionada
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
			addMensagemErro("É necessário informar pelo menos uma turma.");
			return null;
		} 
		
		
		return gerarTabelaRelatorioFrequenciaTurmaSemFiltros();
		//return forward("/metropole_digital/assistente_social/selecao_filtros.jsp");
	}
	
	
	/**
	 * Função que carrega os dados e redireciona para o relatorio de frequencia
	 * da turma com filtros
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/assistente_social/selecao_opcao_turmas.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return Página referente à operação.
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
	 * Função que efetua a geração dos valores do relatorio de frequencia da
	 * turma com os filtros
	 * 
	 * Método é chamado nas seguintes JSP's:
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
			
			
			//VERIFICAÇÃO SE O RELATÓRIO É CONFIGURÁVEL E SE UMA DAS DATAS FOI INFORMADA
			if(dataInicioInformada != null || dataFimInformada != null) {
				
				if(dataInicioInformada != null && dataFimInformada != null){
					if(! dataFimInformada.before(dataInicioInformada) && ! dataFimInformada.after(dataInicioInformada)){
						addMessage("A data início é igual que a data fim.",TipoMensagemUFRN.ERROR);
						return forward("/metropole_digital/assistente_social/selecao_filtros.jsp");
					}
					else if(dataFimInformada.before(dataInicioInformada)){
						addMessage("A data início é maior que a data fim.",TipoMensagemUFRN.ERROR);
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
				
				
				
				//VERIFICAÇÃO SE O RELATÓRIO É CONFIGURÁVEL E SE O FILTRO SOBRE A QUANTIDADE DE FALTAS FOI INFORMADO
				if(qtdFaltasMinimaInformada != null || qtdFaltasMaximaInformada != null) {
					
					if(qtdFaltasMinimaInformada != null && qtdFaltasMaximaInformada != null) {
						if(qtdFaltasMinimaInformada > qtdFaltasMaximaInformada) {
							addMessage("A quantidade mínima de faltas é maior que a quantidade máxima de faltas.",TipoMensagemUFRN.ERROR);
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
				
				
				
				
				
				//VERIFICAÇÃO SE O RELATÓRIO É CONFIGURÁVEL E SE O FILTRO SOBRE A PERCENTUAL DE FALTAS FOI INFORMADO
				if(percFaltasMinimaInformada != null || percFaltasMaximaInformada != null) {
					
					if(percFaltasMinimaInformada != null && percFaltasMaximaInformada != null) {
						if(percFaltasMinimaInformada > percFaltasMaximaInformada) {
							addMessage("O percentual mínimo de faltas é maior que o percentual máximo de faltas.",TipoMensagemUFRN.ERROR);
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
				addMessage("Para gerar o relatório com os filtros, a data início ou a data fim deve ser informada.",TipoMensagemUFRN.ERROR);
				return forward("/metropole_digital/assistente_social/selecao_filtros.jsp");
			}

			

		} finally {
			acompDao.close();
		}
	}
	
	
	/**
	 * Função que efetua a geração dos valores do relatorio de frequencia da
	 * turma com os filtros
	 * 
	 * Método é chamado nas seguintes JSP's:
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
			
			
			//VERIFICAÇÃO SE O RELATÓRIO É CONFIGURÁVEL E SE UMA DAS DATAS FOI INFORMADA
			if(dataInicioInformada != null || dataFimInformada != null) {
				
				if(dataInicioInformada != null && dataFimInformada != null){
					/*if(! dataFimInformada.before(dataInicioInformada) && ! dataFimInformada.after(dataInicioInformada)){
						addMessage("A data início é igual que a data fim.",TipoMensagemUFRN.ERROR);
						return forward("/metropole_digital/assistente_social/selecao_filtros.jsp");
					}
					else */ 
						
					if(dataFimInformada.before(dataInicioInformada)){
						addMessage("A data início é maior que a data fim.",TipoMensagemUFRN.ERROR);
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
				
				
				
				//VERIFICAÇÃO SE O RELATÓRIO É CONFIGURÁVEL E SE O FILTRO SOBRE A QUANTIDADE DE FALTAS FOI INFORMADO
				if(qtdFaltasMinimaInformada != null || qtdFaltasMaximaInformada != null) {
					
					if(qtdFaltasMinimaInformada != null && qtdFaltasMaximaInformada != null) {
						if(qtdFaltasMinimaInformada > qtdFaltasMaximaInformada) {
							addMessage("A quantidade mínima de faltas é maior que a quantidade máxima de faltas.",TipoMensagemUFRN.ERROR);
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
				
				
				
				
				
				//VERIFICAÇÃO SE O RELATÓRIO É CONFIGURÁVEL E SE O FILTRO SOBRE A PERCENTUAL DE FALTAS FOI INFORMADO
				if(percFaltasMinimaInformada != null || percFaltasMaximaInformada != null) {
					
					if(percFaltasMinimaInformada != null && percFaltasMaximaInformada != null) {
						if(percFaltasMinimaInformada > percFaltasMaximaInformada) {
							addMessage("O percentual mínimo de faltas é maior que o percentual máximo de faltas.",TipoMensagemUFRN.ERROR);
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
				addMessage("Para gerar o relatório com os filtros, a data início ou a data fim deve ser informada.",TipoMensagemUFRN.ERROR);
				return forward("/metropole_digital/assistente_social/selecao_filtros.jsp");
			}

			

		} finally {
			acompDao.close();
		}
	}	
	
	
	
	/**
	 * Função que efetua a geração dos valores do relatorio de frequencia da
	 * turma SEM filtros
	 * 
	 * Método é chamado nas seguintes JSP's:
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
	 * Ação de cancelamento da página de seleção de filtros 
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/assistente_social/selecao_opcao_turmas.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual a página será redirecionada
	 * @throws 
	 */
	public String cancelarFiltros() {
		
		return forward("/metropole_digital/menus/menu_imd.jsp");
		
	}
	
}
