/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 13/05/2009
 * 
 */
package br.ufrn.sigaa.jsf;

import static br.ufrn.arq.util.JasperReportsUtil.getReportSIGAA;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMaxLength;
import static br.ufrn.arq.util.ValidatorUtil.validateMaxValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.gru.dominio.ConfiguracaoGRU;
import br.ufrn.comum.gru.dominio.TipoArrecadacao;
import br.ufrn.comum.gru.negocio.ConfiguracaoGRUFactory;
import br.ufrn.comum.gru.negocio.GuiaRecolhimentoUniaoHelper;
import br.ufrn.sigaa.arq.acesso.DadosAcesso;
import br.ufrn.sigaa.arq.dao.ensino.InscricaoSelecaoDao;
import br.ufrn.sigaa.arq.dao.ensino.ProcessoSeletivoDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CursoLatoDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.EquipeProgramaDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.LinhaPesquisaStrictoDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.ParametrosProgramaPosDao;
import br.ufrn.sigaa.arq.dao.questionario.QuestionarioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.AgendaProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.EditalProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.InscricaoSelecao;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.RestricaoInscricaoSelecao;
import br.ufrn.sigaa.ensino.dominio.StatusInscricaoSelecao;
import br.ufrn.sigaa.ensino.dominio.StatusProcessoSeletivo;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.jsf.MatrizCurricularMBean;
import br.ufrn.sigaa.ensino.jsf.InscricaoSelecaoMBean;
import br.ufrn.sigaa.ensino.jsf.NotificarInscritosMBean;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.negocio.dominio.MovimentoProcessoSeletivo;
import br.ufrn.sigaa.ensino.stricto.dominio.AreaConcentracao;
import br.ufrn.sigaa.ensino.stricto.dominio.EquipePrograma;
import br.ufrn.sigaa.ensino.stricto.dominio.ParametrosProgramaPos;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.questionario.dominio.Questionario;
import br.ufrn.sigaa.questionario.jsf.QuestionarioRespostasMBean;

/**
 * Managed bean para cadastro de processos seletivos
 * 
 * @author Leonardo
 * @author Ricardo Wendell
 * 
 */
@Component("processoSeletivo") @Scope("session") 
public class ProcessoSeletivoMBean extends SigaaAbstractController<ProcessoSeletivo> {

	/**
	 * N�mero de dias ap�s o fim da inscri��o que o processo ainda deve ser
	 * exibido.
	 */
	public static final int NUMERO_DIAS_PASSADOS_PUBLICACAO = ParametroHelper.getInstance().getParametroInt(ParametrosGerais.NUMERO_DIAS_PASSADOS_PUBLICACAO_PROCESSO_SELETIVO);
	
	/** Constante que define a mensagem quando o resultado n�o retornar inscritos. */
	private static final String MSG_INSCRITOS_NAO_ENCONTRADOS = "N�o foram encontradas inscri��es para o Status informado";
	
	/** Formul�rio de dados do Processo Seletivo. */
	public static final String JSP_DADOS_PROCESSO_SELETIVO = "/administracao/cadastro/ProcessoSeletivo/form.jsp";
	/** Formul�rio de cursos do Processo Seletivo. */
	public static final String JSP_CURSOS_PROCESSO_SELETIVO = "/administracao/cadastro/ProcessoSeletivo/form_cursos.jsp";
	/** Formul�rio do Per�odo de Agendamento. */
	public static final String JSP_AGENDA_PROCESSO_SELETIVO = "/administracao/cadastro/ProcessoSeletivo/form_agenda.jsp";
	/** Seleciona os dados para gera��o do Relat�rio de Demanda por Vaga. */
	public static final String JSP_SELECIONA_PROGRAMA = "/stricto/relatorios/processoSeletivo/seleciona_programa.jsp";	
	/** Relat�rio de Demanda por Vaga. */
	public static final String JSP_RELATORIO_DEMANDA_VAGA = "/stricto/relatorios/processoSeletivo/relatorio_demanda_por_vaga.jsp";
	/** N�mero de caracteres permitido nas instru��es espec�ficas da GRU. */
	private static final int TAM_MAXIMO_INSTRUCOES_GRU = 240;
	/** N�mero de linhas permitido nas instru��es espec�ficas da GRU. */
	private static final int LIMITE_LINHAS_INSTRUCOES_GRU = 3;
	
	/** Edital do Processo Seletivo. */
	private UploadedFile edital;
	
	/** Equipe do Programa, para os processos seletivos de p�s-gradua��o. */
	private List<SelectItem> equipeDoPrograma;

	/** Inscri��o de candidato no processo seletivo. */
	private StatusInscricaoSelecao inscrito;

	/** Manual do Candidato. */
	private UploadedFile manualCandidato;
	
	/** N�vel de Ensino do Processo Seletivo. */
	private char nivel;
	
	/** Descri��o do N�vel de Ensino do Processo Seletivo. */
	private String descricaoNivel;	
	
	/**
	 *  Lista de Processos Seletivos. Utilizado somente para processos seletivos de TRANSFER�NCIA VOLUNT�RIA,
	 *  para manipula��o da lista de cursos no formul�rio.
	 */
	private ArrayList<ProcessoSeletivo> processosSeletivos;
		
	/**
	 *  ID da matriz curricular a ser removida da lista. Utilizado somente para processos seletivos de TRANSFER�NCIA VOLUNT�RIA,
	 *  para manipula��o da lista de cursos no formul�rio
	 */
	private Integer matrizCurricularSel;
	
	/**
	 *  ID do Curso. Utilizado somente para processos seletivos de TRANSFER�NCIA VOLUNT�RIA,
	 *  para manipula��o da lista de cursos no formul�rio
	 */
	private Integer cursoSel;
	
	/**
	 *  ID do Curso. Utilizado somente para processos seletivos de TRANSFER�NCIA VOLUNT�RIA,
	 *  para manipula��o da lista de cursos no formul�rio
	 */
	private Integer questionarioSel;
	
	/**
	 *  Vagas ofertadas no curso. Utilizado somente para processos seletivos de TRANSFER�NCIA VOLUNT�RIA,
	 *  para manipula��o da lista de cursos no formul�rio
	 */
	private Integer vagasInput;
	
	/**
	 *  Utilizado somente para processos seletivos de TRANSFER�NCIA VOLUNT�RIA,
	 */
	private Collection<ProcessoSeletivo> processos;
		
	/** Par�metros do Programa de p�s-gradua��o. */
	private ParametrosProgramaPos parametrosProgramaPos;

	/** Lista de SelectItem de �reas de conhecimento. */
	private List<SelectItem> possiveisAreas;
	
	/** Lista de SelectItem de linhas de pesquisa. */
	private List<SelectItem> possiveisLinhas;
	
	/** Lista de SelectItem question�rios s�cio-econ�mico. */
	private List<SelectItem> possiveisQuestionarios;
	
	/** Lista de Processos Seletivos ativos. */
	private Collection<ProcessoSeletivo> allVisiveis;
	
	/** Dados do relat�rio. */
	private List<Map<String,Object>> listaResultado = new ArrayList<Map<String,Object>>();
	
	/** Auxilia na gera��o de Relat�rios */
	private Unidade unidade = new Unidade();
	
	/** Define o ano que deve ser listado os processo seletivos **/
	private Integer ano;
	/** Define o status que deve ser listado os processo seletivos **/
	private Integer status;
	/** Define a unidade deve ser listado os processo seletivos **/
	private Integer idUnidade;	
	/** Define quantidade de dis passados dos processos seletivos **/
	private int diasPassados;
	/** Define se deve buscar pelo os processos seletivos do programa selecionado. **/
	private boolean chkUnidade;
	/** Define se deve buscar pelo os processos seletivos no ano selecionado. **/
	private boolean chkAno;
	/** Define se deve buscar pelo os processos seletivos no status selecionado. **/
	private boolean chkStatus;
	/** Define se deve buscar pelo os processos seletivos no status selecionado. **/
	private boolean possivelAlterarNumVagas;
	
	/** data de in�cio para gera��o do relat�rio de demanda */
	private Date dataInicio;
	/** data de fim para gera��o do relat�rio de demanda */
	private Date dataFim;
	
	/** Define o status selecionado para filtrar os inscritos na listagem. */
	private Integer statusInscricao;

	/** Configura��es da GRU para recolhimento da taxa de inscri��o. */
	private Collection<SelectItem> configuracoesGRU;
	
	/** Lista de restri��es dispon�vel de acordo com o {@link NivelEnsino} */
	private Collection<SelectItem> comboRestricaoInscricao;

	/** Define a url de retorno ap�s realizar alguma opera��o. */
	private String urlRedirect;
	
	/** Indica que o processo seletivo ter� taxa de inscri��o. **/
	private boolean possuiTaxaInscricao;

	/** COnfigura��o da GRU utilizada no PS. */
	private ConfiguracaoGRU configuracao;

	/** Construtor padr�o. */
	public ProcessoSeletivoMBean() {
		clear();
	}

	/**
	 * Chamado logo ap�s a opera��o de atualiza��o <br />
	 * M�todo n�o invocado por JSP's'.
	 */
	@Override
	public void afterAtualizar() throws ArqException {
		
		//Se o processo seletivo tiver ativo = true
		if(obj.getAtivo()){
	
			//No caso de curso de gradua��o
			if(obj.getMatrizCurricular() != null){
				obj.setCurso(null);
				
				List<SelectItem> matrizesCurriculares = new ArrayList<SelectItem>();	
				MatrizCurricularMBean mcMBean =  getMBean("matrizCurricular");

				matrizesCurriculares.addAll(toSelectItems(getGenericDAO().findByExactField(
				MatrizCurricular.class, "curso.id", obj.getMatrizCurricular().getCurso().getId()),
				"id", "descricao"));
				mcMBean.setMatrizesCurriculares(matrizesCurriculares);
			}	
			
			processosSeletivos = (ArrayList<ProcessoSeletivo>) getDAO(ProcessoSeletivoDao.class).
					findAllByEdital(obj.getEditalProcessoSeletivo());

			
			prepareMovimento(SigaaListaComando.CADASTRAR_ALTERAR_PROCESSO_SELETIVO);
			carregarQuestionarios();
			
		}else{
			addMensagemErro("Processo seletivo j� foi removido!");
			cancelar();
		}
	}

	/**
	 * Valida os dados do formul�rio e chama a camada de neg�cio para persistir as altera��es.
	 * <br /> 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {

		checkChangeRole();
		if(!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_ALTERAR_PROCESSO_SELETIVO.getId()))
			return null;

		erros.addAll( obj.validate() );

		boolean algumAtivo = false;
		for (ProcessoSeletivo ps : processosSeletivos){
			if (ps.isAtivo()) {
				algumAtivo = true;
				break;
			}
		}
		if(!algumAtivo){
			addMensagemErro("Deve ser adicionado pelo menos uma Matriz Curricular/Curso a lista.");
		}	
		validaProcessosSeletivos();
		
		if(hasErrors())
			return null;
		
		// Popular dados do curso selecionado
		obj.setCurso(getGenericDAO().refresh(obj.getCurso()));
		obj.setMatrizCurricular(getGenericDAO().refresh(obj.getMatrizCurricular()));
		obj.populaHoraPeriodoInscricao();
		
		// Persistir processo seletivo
		MovimentoProcessoSeletivo mov = new MovimentoProcessoSeletivo();
		mov.setEdital(edital);
		mov.setManualCandidato(manualCandidato);
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_ALTERAR_PROCESSO_SELETIVO);
		mov.setProcessoSeletivo(obj);
		mov.setUsuarioLogado(getUsuarioLogado());
		 
		
		if(algumAtivo){
			obj.getEditalProcessoSeletivo().setProcessosSeletivos(getProcessosSeletivos());
		}else{	
			obj.getEditalProcessoSeletivo().setProcessosSeletivos(null);
		}
		
		execute(mov);
		addMessage("Cadastro do Processo Seletivo realizado com sucesso", TipoMensagemUFRN.INFORMATION);	
		
		populateObj();
		getCurrentRequest().setAttribute("comprovante", true);

		processos.clear();
		return listaProcessos();
	}
	
	/**
	 * Valida se o n�mero de vagas de um dos processos seletivos
	 * da lista est� igual a zero.
	 */
	private void validaProcessosSeletivos(){
		
		if( !isEmpty(processosSeletivos) )
			for (ProcessoSeletivo ps : processosSeletivos){
				if( ps.getQuestionario() != null && ps.getQuestionario().getId() == 0 )
					ps.setQuestionario(null);
				
				if (ps.isAtivo() && isEmpty(ps.getVaga())){ 
					addMensagemErro("N�mero de vagas deve ser maior que zero");
					break;
				}
			}	
				
	}
	
	/**
	 * Chama o m�todo cadastrar, por�m indicando o status como CADASTRADO, 
	 * podendo ser alterado depois (Ser� chamado apenas para P�s-Gradua��o).
	 * <br /> 
	 * M�todo chamado pela seguinte JSP: 
	 * <ul>
	 *   <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form_curso.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String salvar() throws SegurancaException, ArqException, NegocioException{
		obj.getEditalProcessoSeletivo().setStatus(StatusProcessoSeletivo.CADASTRADO);
		for (ProcessoSeletivo ps : processosSeletivos){
			ps.getEditalProcessoSeletivo().setStatus(obj.getEditalProcessoSeletivo().getStatus());
		}
		
		return cadastrar();
	}
	
	/**
	 * Chama o m�todo cadastrar, por�m indicando o status como PENDENTE DE APROVA��O, 
	 * n�o podendo ser alterado depois. (Ser� chamado apenas para P�s-Gradua��o).
	 * <br /> 
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form_curso.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String submeter() throws SegurancaException, ArqException, NegocioException{
		obj.getEditalProcessoSeletivo().setStatus(StatusProcessoSeletivo.PENDENTE_APROVACAO);		
		for (ProcessoSeletivo ps : processosSeletivos){
			ps.getEditalProcessoSeletivo().setStatus(obj.getEditalProcessoSeletivo().getStatus());
		}
		
		return cadastrar();
	}
	
	/**
	 * Chamado via AJAX para popular na view
	 * informa��es relacionados �s �reas.
	 * TRANSFER�NCIA VOLUNT�RIA
	 * 
	 * @throws DAOException
	 */
	private void carregarAreas() throws DAOException {
		possiveisLinhas = toSelectItems(getDAO(LinhaPesquisaStrictoDao.class).
				findByCurso(obj.getCurso()), "id", "denominacao");
		possiveisAreas = toSelectItems(getGenericDAO().findByExactField(
				AreaConcentracao.class, "programa.id",
				obj.getCurso().getUnidade().getId()), "id", "denominacao");
	}

	/**
	 * Chamado via AJAX para popular na view
	 * informa��es relacionadas �s Equipes.
	 *  
	 * @throws DAOException
	 */
	private void carregarEquipes() throws DAOException {
		EquipeProgramaDao dao = getDAO(EquipeProgramaDao.class);
		List<EquipePrograma> equipes = dao.findByPrograma(obj.getCurso()
				.getUnidade().getId());
		Collections.sort(equipes);
		equipeDoPrograma = toSelectItems(equipes, "id", "nome");
	}

	/**
	 * Verifica os pap�is que podem efetuar opera��es de altera��o no caso de uso. <br />
	 * JSP: M�todo n�o invocado por JSP's'.
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA, SigaaPapeis.SECRETARIA_POS,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO,
				SigaaPapeis.GESTOR_TECNICO,
				SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR,
				SigaaPapeis.ADMINISTRADOR_DAE,
				SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO,
				SigaaPapeis.GESTOR_LATO,
				SigaaPapeis.DAE, SigaaPapeis.PPG);
	}

	/**
	 * Limpa os dados do MBean
	 */
	private void clear() {
		obj = new ProcessoSeletivo();
		obj.setCurso(new Curso());
		obj.setMatrizCurricular(new MatrizCurricular());
		obj.getMatrizCurricular().setCurso(new Curso());
		obj.setEditalProcessoSeletivo(new EditalProcessoSeletivo());
		obj.getEditalProcessoSeletivo().setAgendas(new ArrayList<AgendaProcessoSeletivo>());
		
		this.processos = new HashSet<ProcessoSeletivo>();
		this.equipeDoPrograma = new ArrayList<SelectItem>();
		this.parametrosProgramaPos = new ParametrosProgramaPos();
		this.possiveisAreas = new ArrayList<SelectItem>();
		this.possiveisQuestionarios = new ArrayList<SelectItem>();
		this.processosSeletivos = new ArrayList<ProcessoSeletivo>();
		this.configuracoesGRU = null;
		this.cursoSel = 0;
		this.matrizCurricularSel = 0;
		this.vagasInput = 0;		

		this.chkStatus = false;
		this.chkAno = false;
		this.chkUnidade = false;
		this.ano = null;
		this.status = null;
		this.idUnidade = null;
		this.unidade = new Unidade();
		this.diasPassados = 0;
		
		this.possivelAlterarNumVagas = false;
		this.possuiTaxaInscricao = false;
		this.statusInscricao = null;
	}
	
	/**
	 * Realiza a busca dos processos seletivos de acordo com os filtros selecionados.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/public/processo_seletivo/lista.jsp</li>
	 *  </ul>
	 * @return
	 * @throws ArqException
	 */
	public String buscar() throws ArqException{
		//Limpa a lista dos processos seletivos
		this.processos = null;
		this.diasPassados = -1;
		
		//Verifica se pelo menos um filtro foi selecionado
		if(isUserInRole(SigaaPapeis.PPG) && isChkUnidade() && isEmpty(getIdUnidade()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Programa");
		if(isChkAno() && isEmpty(getAno()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		if(( isUserInRole(SigaaPapeis.PPG) || isAcessoProcessoSeletivoStricto() ) && isChkStatus() && isEmpty(getStatus()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Status");
		
		if(hasErrors())
			return null;
		
		getAllAtivos();
		
		return listaProcessos();
	}

	/**
	 * Retorna todos os processos seletivos de acordo com as permiss�es de acesso do usu�rio.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/public/processo_seletivo/lista.jsp</li>
	 *   <li>/sigaa.war/public/processo_seletivo/lista_transferencia_voluntaria.jsp</li>
	 *   <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 *  </ul>
	 */
	@Override
	public Collection<ProcessoSeletivo> getAll() throws ArqException {
		
		if ( !isEmpty(processos) )
			return processos;
		
		ProcessoSeletivoDao processoDao = getDAO(ProcessoSeletivoDao.class);
		this.diasPassados  = -1;

		DadosAcesso acesso = getAcessoMenu();

		if ( isAcessoCompleto() && (!isChkUnidade() && !isChkStatus() && !isChkAno())) 
			processos = processoDao.findAll(ProcessoSeletivo.class);
		else if ( isAcessoProcessoSeletivoStricto() ) {
			
			setChkUnidade(true);
			setIdUnidade(getProgramaStricto().getId());
			Integer[] lstStatus = new Integer[]{};
			
			//Se n�o foi realizado a busca, retorna os processos onde a data final n�o tenha superado o valor definido na constante
			if(!isChkAno() && !isChkStatus())
				this.diasPassados  = NUMERO_DIAS_PASSADOS_PUBLICACAO;
			else if( isChkStatus() )
				lstStatus = new Integer[]{getStatus()};
			
			processos = processoDao.findGeral(getIdUnidade(), NivelEnsino.STRICTO, getAno(), diasPassados, lstStatus);
			
			Collections.sort( (List<ProcessoSeletivo>) processos, new Comparator<ProcessoSeletivo>(){
				public int compare(ProcessoSeletivo p1,	ProcessoSeletivo p2) {						
					return p2.getEditalProcessoSeletivo().getInicioInscricoes().
						compareTo(p1.getEditalProcessoSeletivo().getInicioInscricoes()); 					
				}
			});		
	
		} else if ( acesso.isPpg() && isPortalPpg() ) {
			
			Integer[] lstStatus = new Integer[4];
			lstStatus[0] = StatusProcessoSeletivo.PENDENTE_APROVACAO;
			lstStatus[1] = StatusProcessoSeletivo.SOLICITADO_ALTERACAO;

			//Se n�o foi realizado a busca, retorna somente os processos n�o encerrados
			if(isChkUnidade() || isChkAno() || isChkStatus()){
				lstStatus[2] = StatusProcessoSeletivo.PUBLICADO;
				lstStatus[3] = StatusProcessoSeletivo.CADASTRADO;
				if( isChkStatus() )
					lstStatus = new Integer[]{getStatus()};
			}	
			
			processos = processoDao.findGeral(getIdUnidade(), NivelEnsino.STRICTO, getAno(), diasPassados, lstStatus);
			if( isEmpty(processos) ){
				processos = processoDao.findGeral(getIdUnidade(), NivelEnsino.STRICTO, 
						getAno(), NUMERO_DIAS_PASSADOS_PUBLICACAO,  StatusProcessoSeletivo.getAtivosPPG());
			}
			

			Collections.sort((List<ProcessoSeletivo>) processos, new Comparator<ProcessoSeletivo>(){
				public int compare(ProcessoSeletivo p1, ProcessoSeletivo p2) {
					if (p1.isPendentePublicacao() && !p2.isPendentePublicacao())
						return -1;
					else if (!p1.isPendentePublicacao() && p2.isPendentePublicacao())
						return 1;
					else if (p1.isSolicitadoAlteracao() && !p2.isSolicitadoAlteracao())
						return -1;
					else if (!p1.isSolicitadoAlteracao() && p2.isSolicitadoAlteracao())
						return 1;
					else 
						return p2.getEditalProcessoSeletivo().getInicioInscricoes().
							compareTo(p1.getEditalProcessoSeletivo().getInicioInscricoes());
				}
			});
			
			
		} else if ( isUserInRole(SigaaPapeis.GESTOR_LATO) && isPortalLatoSensu() ) {
				
				Integer[] lstStatus = new Integer[4];
				lstStatus[0] = StatusProcessoSeletivo.PENDENTE_APROVACAO;
				lstStatus[1] = StatusProcessoSeletivo.SOLICITADO_ALTERACAO;

				//Se n�o foi realizado a busca, retorna somente os processos n�o encerrados
				if(isChkUnidade() || isChkAno() || isChkStatus()){
					lstStatus[2] = StatusProcessoSeletivo.PUBLICADO;
					lstStatus[3] = StatusProcessoSeletivo.CADASTRADO;
					if( isChkStatus() )
						lstStatus = new Integer[]{getStatus()};
				}	
				
				processos = processoDao.findGeral(getIdUnidade(), NivelEnsino.LATO, getAno(), diasPassados, lstStatus);
				if( isEmpty(processos) ){
					processos = processoDao.findGeral(getIdUnidade(), NivelEnsino.LATO, 
							getAno(), NUMERO_DIAS_PASSADOS_PUBLICACAO,  StatusProcessoSeletivo.getAtivosPPG());
				}
				

				Collections.sort((List<ProcessoSeletivo>) processos, new Comparator<ProcessoSeletivo>(){
					public int compare(ProcessoSeletivo p1, ProcessoSeletivo p2) {
						if (p1.isPendentePublicacao() && !p2.isPendentePublicacao())
							return -1;
						else if (!p1.isPendentePublicacao() && p2.isPendentePublicacao())
							return 1;
						else if (p1.isSolicitadoAlteracao() && !p2.isSolicitadoAlteracao())
							return -1;
						else if (!p1.isSolicitadoAlteracao() && p2.isSolicitadoAlteracao())
							return 1;
						else 
							return p2.getEditalProcessoSeletivo().getInicioInscricoes().
								compareTo(p1.getEditalProcessoSeletivo().getInicioInscricoes());
					}
				});
				
				
		} else {
			
			if ( acesso.isTecnico() && getSubSistema().equals(SigaaSubsistemas.TECNICO) ) 
				processos = processoDao.findGeral(getUnidadeGestora(), NivelEnsino.TECNICO, getAno(), diasPassados,
						StatusProcessoSeletivo.CADASTRADO, StatusProcessoSeletivo.PUBLICADO);
			else if ( acesso.isFormacaoComplementar() && getSubSistema().equals(SigaaSubsistemas.FORMACAO_COMPLEMENTAR) ) {
				if(!isChkAno())
					this.diasPassados  = NUMERO_DIAS_PASSADOS_PUBLICACAO;
				processos = processoDao.findGeral(getUnidadeGestora(), NivelEnsino.FORMACAO_COMPLEMENTAR, getAno(), diasPassados,
							StatusProcessoSeletivo.CADASTRADO, StatusProcessoSeletivo.PUBLICADO);
			} else if ( ( acesso.isCoordenadorCursoLato() || acesso.isSecretarioLato() ) && getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO) ) 
				processos = processoDao.findGeral(getCursoAtualCoordenacao(), getAno(), diasPassados, 
						StatusProcessoSeletivo.getAll() );
			else if ( ( acesso.isAdministradorDAE() || acesso.isDae() ) && getSubSistema().equals(SigaaSubsistemas.GRADUACAO) )
				processos = processoDao.findGeral(null, NivelEnsino.GRADUACAO, getAno(), diasPassados,
						StatusProcessoSeletivo.CADASTRADO, StatusProcessoSeletivo.PUBLICADO);
		}
	
		return processos;
	}
	
	/**
	 * Retorna uma cole��o de SelectItem com os tipos de arrecada��o utilizados
	 * na GRU para a cobran�a da taxa de inscri��o.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form.jsp</li>
	 *   <li>/sigaa.war/vestibular/ProcessoSeletivoVestibular/form.jsp</li>
	 *  </ul> 
	 * @return
	 */
	public Collection<SelectItem> getTiposArrecadacaoCombo() {
		Collection<SelectItem> tipos = new ArrayList<SelectItem>();
		switch (getNivelEnsino()) {
		case NivelEnsino.GRADUACAO:
			tipos.add(new SelectItem(TipoArrecadacao.VESTIBULAR, "Vestibular"));
			tipos.add(new SelectItem(TipoArrecadacao.REINGRESSO, "Reingresso"));
			tipos.add(new SelectItem(TipoArrecadacao.REOPCAO, "Reop��o"));
			tipos.add(new SelectItem(TipoArrecadacao.TRANSFERENCIA_VOLUNTARIA, "Transfer�ncia Volunt�ria"));
			break;
		case NivelEnsino.LATO :
			tipos.add(new SelectItem(TipoArrecadacao.PROCESSO_SELETIVO_LATO_SENSU, "Processo Seletivo para Lato Sensu"));
			break;
		case NivelEnsino.STRICTO:
		case NivelEnsino.DOUTORADO :
		case NivelEnsino.MESTRADO :
			tipos.add(new SelectItem(TipoArrecadacao.PROCESSO_SELETIVO_STRICTO_SENSU, "Processo Seletivo para Stricto Sensu"));
			break;
		case NivelEnsino.TECNICO :
			tipos.add(new SelectItem(TipoArrecadacao.PROCESSO_SELETIVO_TECNICO, "Processo Seletivo para Curso T�cnico"));
			break;
		case NivelEnsino.FORMACAO_COMPLEMENTAR :
			tipos.add(new SelectItem(TipoArrecadacao.PROCESSO_SELETIVO_ESCOLAS_ESPECIALIZADAS, "Processo Seletivo para Escolas Especializadas"));
			break;
		default:
			break;
		}
		return tipos;
	}
	
	/**
	 * Retorna uma cole��o de SelectItem com os tipos de arrecada��o utilizados
	 * na GRU para a cobran�a da taxa de inscri��o.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form.jsp</li>
	 *  </ul> 
	 * @return
	 * @throws DAOException 
	 */
	public Collection<SelectItem> getConfiguracoesGRU() throws DAOException {
		if (configuracoesGRU == null) {
			configuracoesGRU = toSelectItems(ConfiguracaoGRUFactory.getInstance()
				.getConfiguracoesGRUByTipoArrecadacao(TipoArrecadacao.PROCESSO_SELETIVO_STRICTO_SENSU), "id", "descricao");
		}
		return configuracoesGRU;
	}
	
	/** Define a taxa de inscri��o e data de vencimento baseado na configura��o da GRU escolhida.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form.jsp</li>
	 * </ul> 
	 * 
	 * @throws DAOException
	 */
	public void atualizaConfiguracaoGRU(ValueChangeEvent evt) throws DAOException {
		boolean possuiTaxa = evt != null ? (Boolean) evt.getNewValue() : possuiTaxaInscricao;
		
		isPossuiConfiguracaoGRU(possuiTaxa);
	}

	/**
	 * Valida se existe uma ConfiguracaoGRU definida
	 * 
	 * @param possuiTaxa
	 * @throws DAOException
	 */
	private void isPossuiConfiguracaoGRU(boolean possuiTaxa) throws DAOException {
		 
		if (!possuiTaxa) {
			configuracao = new ConfiguracaoGRU();
		} else {
			Unidade u;
			if (NivelEnsino.isLato(getNivelEnsino())) {
				CursoLato c = null;
				if (isPortalLatoSensu())
					c = (CursoLato) obj.getCurso();
				else
					c = (CursoLato) getCursoAtualCoordenacao();
				c = getGenericDAO().refresh(c);
				u = c.getUnidadeOrcamentaria();
				if (c.getIdConfiguracaoGRUInscricao() == null) {
					addMensagemWarning("N�o h� configura��o de GRU definida para cobran�a de taxa de inscri��o. A taxa dever� ser paga por outro meio.");
					configuracao = new ConfiguracaoGRU();
				} else {
					GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
					try {
						configuracao = dao.findByPrimaryKey(c.getIdConfiguracaoGRUInscricao(), ConfiguracaoGRU.class);
					} finally {
						dao.close();
					}
				}
			} else if (NivelEnsino.isAlgumNivelStricto(getNivelEnsino())) {
				if (obj.getId() == 0) {
					if (isPortalCoordenadorStricto() && getAcessoMenu().isCoordenadorCursoStricto())
						u = getProgramaStricto();
					else
						u = getUsuarioLogado().getVinculoAtivo().getUnidade(); // No caso de stricto, a unidade atual do usu�rio � sempre a unidade do processo seletivo
				} else
					u = obj.getCurso().getUnidade(); // Para o caso de lato, a unidade do processo � a unidade do curso.
				
				configuracao = GuiaRecolhimentoUniaoHelper.getConfiguracaoGRUByTipoArrecadacao(TipoArrecadacao.PROCESSO_SELETIVO_STRICTO_SENSU, u.getId());
			} else if (NivelEnsino.FORMACAO_COMPLEMENTAR == getNivelEnsino()) {
				u = getUsuarioLogado().getVinculoAtivo().getUnidade();
				configuracao = GuiaRecolhimentoUniaoHelper.getConfiguracaoGRUByTipoArrecadacao(TipoArrecadacao.PROCESSO_SELETIVO_FORMACAO_COMPLEMENTAR, u.getId());
			}
			if (isEmpty(configuracao))
				addMensagemErro("N�o h� configura��o de GRU dispon�vel para uso em sua Unidade. Por favor, entre em contato com o departamento financeiro e solicite o cadastro de uma configura��o de GRU.");
		}
	}
	
	/**
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>\sigaa.war\public\processo_seletivo\lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public Collection<SelectItem> getStatusCombo(){
		return StatusProcessoSeletivo.getAllCombo();
	}

	/** Cancela a opera��o atual.
	 * <br>
	 * <b> M�todo chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * <li>sigaa.war/administracao/cadastro/ProcessoSeletivo/form_agenda.jsp</li>
	 * <li>sigaa.war/administracao/cadastro/ProcessoSeletivo/form_cursos.jsp</li>
	 * <li>sigaa.war/administracao/cadastro/ProcessoSeletivo/form_qtd_inscritos.jsp</li>
	 * <li>sigaa.war/administracao/cadastro/ProcessoSeletivo/form.jsp</li>
	 * <li>sigaa.war/administracao/cadastro/ProcessoSeletivo/informa_motivo_alteracao.jsp</li>
	 * <li>sigaa.war/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		clear();
		return super.cancelar();
	}

	/**
	 * Retorna todos os processos seletivos ativos.
	 * <br>
	 * <b> M�todo chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * <li> /sigaa.war/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllAtivos()
	 */	
	@Override
	public Collection<ProcessoSeletivo> getAllAtivos() throws ArqException {
		Set<ProcessoSeletivo> processosAtivos = new LinkedHashSet<ProcessoSeletivo>();
		Collection<ProcessoSeletivo> todosProcessos = getAll();
		if (todosProcessos != null)
			for (ProcessoSeletivo ps : todosProcessos) {
				if (ps.isAtivo()) processosAtivos.add(ps);
			}
		processos = processosAtivos;
		
		return processos;
	}
 
	/**
	 * Retorna todos os processos seletivos abertos. <br />
	 * M�todo n�o invocado por JSP's'.
	 * @return
	 */
	public Collection<ProcessoSeletivo> getAllAbertos() {
		ProcessoSeletivoDao processoDao = getDAO(ProcessoSeletivoDao.class);
		try {
			return processoDao.findAllAbertos(nivel);
		} catch (DAOException e) {
			notifyError(e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Buscar todos os processos seletivos que podem aparecer na consulta
	 * p�blica.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/public/processo_seletivo/lista.jsp</li>
	 * 		<li>/sigaa.war/public/processo_seletivo/lista_transferencia_voluntaria.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<ProcessoSeletivo> getAllVisiveis() throws DAOException {
		if ( isEmpty(allVisiveis) ) {
			ProcessoSeletivoDao processoDao = getDAO(ProcessoSeletivoDao.class);
			if (nivel == NivelEnsino.STRICTO || nivel == NivelEnsino.LATO)
				allVisiveis = processoDao.findAllVisiveis(nivel,NUMERO_DIAS_PASSADOS_PUBLICACAO,StatusProcessoSeletivo.PUBLICADO);
			else
				allVisiveis = processoDao.findAllVisiveis(nivel,NUMERO_DIAS_PASSADOS_PUBLICACAO, null);
			
			//Ordena os processos seletivos na ordem dos abertos com a data de inscri��o mais pr�xima 
			if( !isEmpty(allVisiveis) ){
				Collections.sort((List<ProcessoSeletivo>) allVisiveis, new Comparator<ProcessoSeletivo>(){
					public int compare(ProcessoSeletivo p1, ProcessoSeletivo p2) {
						if ( p1.isInscricoesAbertas() && !p2.isInscricoesAbertas() )
							return -1;
						else if (  !p1.isInscricoesAbertas() && p2.isInscricoesAbertas() )
							return 1;
						else 
							return p2.getEditalProcessoSeletivo().getInicioInscricoes().
									compareTo(p1.getEditalProcessoSeletivo().getInicioInscricoes());
					}
				});
			}
			
			
		}
		
		
		return allVisiveis;
	}

	/**
	 * Retorna o diret�rio base.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getDirBase()
	 * JSP: M�todo n�o invocado por JSP's'.
	 */
	@Override
	public String getDirBase() {
		return "/administracao/cadastro/ProcessoSeletivo";
	}

	/** Retorna o arquivo do Edital do PRocesso Seletivo.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public UploadedFile getEdital() {
		return this.edital;
	}

	/** Retorna a equipe do Programa, para os processos seletivos de p�s-gradua��o. 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/public/processo_seletivo/form_inscricao.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getEquipeDoPrograma() {
		return equipeDoPrograma;
	}

	/** Retorna a inscri��o de candidato no processo seletivo. 
	 * M�todo  n�o invocado por JSP's.
	 * @return
	 */
	public StatusInscricaoSelecao getInscrito() {
		return inscrito;
	}

	/** Retorna o arquivo do Manual do Candidato. 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public UploadedFile getManualCandidato() {
		return this.manualCandidato;
	}

	/** Retorna o N�vel de Ensino do Processo Seletivo. 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/public/processo_seletivo/lista.jsp</li>
	 * 		<li>/sigaa.war/public/processo_seletivo/lista_inscricoes.jsp</li>
	 * 		<li> /sigaa.war/public/processo_seletivo/lista_transferencia_voluntaria.jsp</li>
	 * </ul>
	 * @return
	 */
	public char getNivel() {
		return this.nivel;
	}

	/** Retorna uma descri��o textual do N�vel de Ensino do Processo Seletivo.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/public/processo_seletivo/lista.jsp</li>
	 * 		<li>/sigaa.war/public/processo_seletivo/lista_transferencia_voluntaria.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String getNivelDescricao() {
		if( !isEmpty(getParameterChar("nivel")) && getParameterChar("nivel") != ' ' )
			setNivel(getParameterChar("nivel"));
		allVisiveis = null;
		return NivelEnsino.getDescricao(this.nivel);
	}

	/** Retorna os par�metros do programa de p�s-gradua��o.  
	 * M�todo n�o invocado por JSP's.
	 * @return
	 */
	public ParametrosProgramaPos getParametrosProgramaPos() {
		return parametrosProgramaPos;
	}

	/** Retorna uma lista de SelectItem de �reas de conhecimento. 
	 * M�todo n�o invocado por JSP's.
	 * @return
	 */
	public List<SelectItem> getPossiveisAreas() {
		return possiveisAreas;
	}

	/**
	 * Verifica se usu�rio tem acesso ao cadastro de processo seletivos de
	 * qualquer curso
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li> /sigaa.war/administracao/cadastro/ProcessoSeletivo/form_cursos.jsp</li>
	 * 		<li> /sigaa.war/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isAcessoCompleto() {
		return getUsuarioLogado() != null
				&& isUserInRole(SigaaPapeis.ADMINISTRADOR_SIGAA);
	}

	/**
	 * Verifica se usu�rio tem acesso ao cadastro de processo seletivos de
	 * curso de gradua��o
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li> /sigaa.war/administracao/cadastro/ProcessoSeletivo/form.jsp</li>
	 * 		<li> /sigaa.war/administracao/cadastro/ProcessoSeletivo/form_cursos.jsp</li>
	 * 		<li> /sigaa.war/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isAcessoProcessoSeletivoGraduacao() {
		return getUsuarioLogado() != null
				&& isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE,SigaaPapeis.DAE) && isPortalGraduacao();
	}
	
	/**
	 * Verifica se usu�rio tem acesso ao cadastro de processo seletivos de
	 * curso de P�s-Gradua��o
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li> /sigaa.war/administracao/cadastro/ProcessoSeletivo/form.jsp</li>
	 * 		<li> /sigaa.war/administracao/cadastro/ProcessoSeletivo/form_cursos.jsp</li>
	 * 		<li> /sigaa.war/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isAcessoProcessoSeletivoStricto() {
		return getUsuarioLogado() != null
				&& isUserInRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS) && isPortalCoordenadorStricto();
	}
	
	/**
	 * Verifica se usu�rio tem acesso ao cadastro de processo seletivos de
	 * curso de Lato Sensu
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li> /sigaa.war/administracao/cadastro/ProcessoSeletivo/form.jsp</li>
	 * 		<li> /sigaa.war/administracao/cadastro/ProcessoSeletivo/form_cursos.jsp</li>
	 * 		<li> /sigaa.war/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isAcessoProcessoSeletivoLato() {
		return getUsuarioLogado() != null
				&& isUserInRole(SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO) && isPortalCoordenadorLato();
	}
	
	/**
	 * Verifica se usu�rio tem acesso para gerenciamento de processo seletivos de Stricto Sensu OU lato Sensu.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li> /sigaa.war/administracao/cadastro/ProcessoSeletivo/form.jsp</li>
	 * 		<li> /sigaa.war/administracao/cadastro/ProcessoSeletivo/form_cursos.jsp</li>
	 * 		<li> /sigaa.war/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isAcessoGerenciaStrictoOrLato() {
		return (isPortalPpg() || isAcessoProcessoSeletivoStricto()) ||
				(isPortalLatoSensu() || isAcessoProcessoSeletivoLato());
	}
	
	/**
	 * Indica se o processo seletivo � de stricto-sensu.
	 * Permite visualizar determinadas funcionalidades somente para os 
	 * processos seletivos de n�vel de ensino indicado acima.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>/sigaa.war/public/processo_seletivo/lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isProcessoStricto() {
		return !isEmpty( getNivel() ) && getNivel() == NivelEnsino.STRICTO;
	}
	
	/**
	 * Recupera o processo seletivo e lista os inscritos de um processo seletivo.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String buscarInscritos() throws ArqException {
		
		if( !isEmpty(getParameterInt("id")) ){
			clear();
			setId();
			populateObj(true);
		}
		InscricaoSelecaoMBean inscricaoMBean = getMBean("inscricaoSelecao");
		inscricaoMBean.setAtualizacaoStatus(false);
		
		return buscarInscritos(obj) ;
	}
	
	/**
	 * Popula a lista de inscritos e redireciona para JSP.
	 * 
	 * <br />
	 * N�o � chamado por JSP's.
	 * 
	 * @param processo
	 * @return
	 * @throws DAOException
	 */
	public String buscarInscritos( ProcessoSeletivo processo ) throws DAOException {
		// Buscar inscri��es
		InscricaoSelecaoDao inscricaoDAO = getDAO(InscricaoSelecaoDao.class);
		
		List<Integer> listaStatusInscricao = new ArrayList<Integer>();
		if( !isEmpty(statusInscricao) ){
			listaStatusInscricao.add(statusInscricao);
			obj.setInscritos( inscricaoDAO.findInscritosByProcessoStatus(processo, listaStatusInscricao, true) );
		}else{
			obj.setInscritos( inscricaoDAO.findInscritos(processo, true) );
		}
		
		if ( isEmpty(obj.getInscritos()) ) {
			addMensagemErro(MSG_INSCRITOS_NAO_ENCONTRADOS);
			return null;
		} else {
			// verifica o pagamento das GRUs
			Collection<Integer> idsGruQuitadas = new ArrayList<Integer>();
			for (InscricaoSelecao inscricao : obj.getInscritos())
				if (inscricao.getIdGRU() != null)
					idsGruQuitadas.add(inscricao.getIdGRU());
			idsGruQuitadas = GuiaRecolhimentoUniaoHelper.isGRUQuitada(idsGruQuitadas);
			if (!isEmpty(idsGruQuitadas))
				for (InscricaoSelecao inscricao : obj.getInscritos())
					inscricao.setGruQuitada(idsGruQuitadas.contains(inscricao.getIdGRU()));
		}
		
		//Seta os destinat�rios necess�rios para que a coordena��o possa notific�-los.
		NotificarInscritosMBean notificaInscMBean  = getMBean("notificarInscritos");
		notificaInscMBean.setInscritos(obj.getInscritos());
		
		return forward(getDirBase() + "/lista_inscritos.jsf");
	}
	
	/**
	 * M�todo principal para popular dados utilizados na view.<br>
	 * M�todo n�o chamado por JSP.
	 * @throws ArqException 
	 */
	private void popularView() throws ArqException {
		clear();
		setId();
		verificaObjRemovido();
		if( !isEmpty(obj) ){
			InscricaoSelecaoDao inscricaoDao = getDAO(InscricaoSelecaoDao.class);
			this.obj = inscricaoDao.findByPrimaryKey(obj.getId(), ProcessoSeletivo.class);
			obj.setVagaRestante( obj.getVaga() - inscricaoDao.getQtdInscritos(obj) );
			popularDadosAuxiliares();
		}else{
			addMensagem( MensagensArquitetura.OBJETO_NAO_FOI_SELECIONADO, "Processo Seletivo" );
		}
	}

	/**
	 * M�todo auxiliar para popular dados complementares como as �reas de Concentra��o, 
	 * equipes do programa utilizados na view.<br>
	 * M�todo n�o chamado por JSP's.
	 * @throws DAOException
	 */
	public void popularDadosAuxiliares() throws DAOException {
		if (!isEmpty(obj.getCurso()) && isEmpty(obj.getMatrizCurricular())) {
			popularParametrosPrograma();
			carregarAreas();
			carregarEquipes();
			
		}
	}
	
	/**
	 * M�todo auxiliar para popular dados utilizados na view.
	 * @throws DAOException
	 */
	private void popularParametrosPrograma() throws DAOException {
		ParametrosProgramaPosDao dao = getDAO(ParametrosProgramaPosDao.class);
		if (!isEmpty(obj.getCurso()) && obj.getCurso().isStricto())
			this.parametrosProgramaPos = dao.findByPrograma(obj.getCurso().getUnidade());
		else if(getUsuarioLogado()!=null)
			this.parametrosProgramaPos = dao.findByPrograma(getUsuarioLogado().getVinculoAtivo().getUnidade());
		atualizaConfiguracaoGRU(null);
	}

	/**
	 * M�todo auxiliar para popular dados utilizados na view.
	 * @throws DAOException
	 */
	private void carregarQuestionarios() throws DAOException {
		
		Collection<Questionario> questionarios = new ArrayList<Questionario>();
		QuestionarioDao questionarioDao = getDAO(QuestionarioDao.class);
		DadosAcesso acesso = getAcessoMenu();

		if (isAcessoCompleto()) 
			questionarios = questionarioDao.findAllAtivos(Questionario.class, "titulo");
		else if (acesso.isPpg()) 
			questionarios = questionarioDao.findByTipoUnidadeAcademica(TipoUnidadeAcademica.PROGRAMA_POS);
		else{
			Unidade unidade = NivelEnsino.isAlgumNivelStricto( getNivelEnsino() ) ? getProgramaStricto() : getUsuarioLogado().getVinculoAtivo().getUnidade();
			questionarios = questionarioDao.findQuestionariosProcessosSeletivos(unidade);
		}
		
		if ( !isEmpty(questionarios) ) {
			if (obj.getQuestionario() == null ) {
				obj.setQuestionario( new Questionario() );
			}
			
			possiveisQuestionarios = new ArrayList<SelectItem>();
			possiveisQuestionarios.add(new SelectItem(0, "N�O APLICAR QUESTION�RIOS ESPEC�FICOS"));
			possiveisQuestionarios.addAll( toSelectItems(questionarios, "id", "titulo") );
		}
		
	}

		
	/**
	 * Inicia a opera��o de cadastro.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		
		checkChangeRole();
		clear();
		setConfirmButton("Cadastrar");
		carregarQuestionarios();
		popularParametrosPrograma();
		
		if (!isAcessoProcessoSeletivoStricto())
			obj.getEditalProcessoSeletivo().setStatus(StatusProcessoSeletivo.PUBLICADO);
		else
			obj.getEditalProcessoSeletivo().setStatus(StatusProcessoSeletivo.CADASTRADO);
		
		obj.getEditalProcessoSeletivo().setPossuiAgendamento(false);
		
		prepareMovimento(SigaaListaComando.CADASTRAR_ALTERAR_PROCESSO_SELETIVO);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_ALTERAR_PROCESSO_SELETIVO.getId());
		return formDadosProcessoSeletivo();
	}

	/** 
	 * Retorna o link para o formul�rio dos dados do Processo Seletivo.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Chamado por /sigaa.war/administracao/cadastro/ProcessoSeletivo/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String formDadosProcessoSeletivo() throws ArqException{
		if(!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_ALTERAR_PROCESSO_SELETIVO.getId()))
			return listaProcessos();
		else{
			validaProcessosSeletivos();
			if(hasErrors())
				return null;
			return forward(JSP_DADOS_PROCESSO_SELETIVO);
		}	
	}
	
	
	/** 
	 * Retorna o link para o formul�rio dos per�odos de agendamento
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form_agenda.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String formPeriodoAgenda() throws ArqException{
		
		FacesContext context = FacesContext.getCurrentInstance();
		Iterator<FacesMessage> messages = context.getMessages();
	
		if (messages.hasNext()) {
			FacesMessage facesMessage = messages.next();
			addMensagemErro(facesMessage.getSummary());
		}
		
		validaProcessosSeletivos();
		
		if (possuiTaxaInscricao)
			validateRequiredId(obj.getEditalProcessoSeletivo().getIdConfiguracaoGRU(), "Configura��o da GRU", erros);
		else
			obj.getEditalProcessoSeletivo().setIdConfiguracaoGRU(null);
		
		erros.addAll(this.obj.validate());
		
		if (hasErrors())
			return null;
		
		if(!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_ALTERAR_PROCESSO_SELETIVO.getId()))
			return listaProcessos();

				
		return forward(JSP_AGENDA_PROCESSO_SELETIVO);
	}
	
	/** 
	 * Retorna o link para o formul�rio dos cursos do Processo Seletivo.
	 * <br /> 
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form_curso.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String formCursosProcessoSeletivo() throws ArqException{
		// Para valida��o da data Informada		
		FacesContext context = FacesContext.getCurrentInstance();
		Iterator<FacesMessage> messages = context.getMessages();
		
		if (messages.hasNext()) {
			FacesMessage facesMessage = messages.next();
			addMensagemErro(facesMessage.getSummary());
		}
		
		isPossuiConfiguracaoGRU(isPossuiTaxaInscricao());
		
		if (hasErrors())
			return null;
		
		if(!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_ALTERAR_PROCESSO_SELETIVO.getId()))
			return listaProcessos();
		
		validarPeriodoAgenda();
		if( configuracao != null)
			obj.getEditalProcessoSeletivo().setIdConfiguracaoGRU(configuracao.getId());
		
		if (possuiTaxaInscricao) {
			validateRequiredId(obj.getEditalProcessoSeletivo().getIdConfiguracaoGRU(), "Configura��o da GRU", erros);
			String instrucoes = obj.getEditalProcessoSeletivo().getInstrucoesEspecificasGRU();
			instrucoes = instrucoes.replace("\r\n", "\n");
			validateMaxLength(instrucoes, TAM_MAXIMO_INSTRUCOES_GRU, "Instru��es espec�ficas para a GRU", erros);
			// verifica o limite de tr�s linhas
			if (!isEmpty(instrucoes)) {
				StringTokenizer tokenizer = new StringTokenizer(instrucoes, "\n");
				validateMaxValue(tokenizer.countTokens(), LIMITE_LINHAS_INSTRUCOES_GRU, "N� de linhas nas instru��es espec�ficas para a GRU", erros);
			}
		} else {
			obj.getEditalProcessoSeletivo().setIdConfiguracaoGRU(null);
		}
		
		getGenericDAO().refresh(this.obj.getQuestionario());
		
		erros.addAll(this.obj.validate());
		if (hasErrors())
			return null;
		
		cursoSel = 0;
		matrizCurricularSel = 0;
		questionarioSel = 0;
		vagasInput = 0;		

		return forward(JSP_CURSOS_PROCESSO_SELETIVO);
	}
	
	
	/**
	 * Remove um processo seletivo.
	 * <br /> 
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * </ul>
	 */
	public String remover() throws ArqException {
		
		
		int id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, ProcessoSeletivo.class);
		verificaObjRemovido();
		
		prepareMovimento(SigaaListaComando.REMOVER_PROCESSO_SELETIVO);
		// Remover processo seletivo
		try {
			MovimentoProcessoSeletivo mov = new MovimentoProcessoSeletivo();
			mov.setProcessoSeletivo(obj);
			mov.setCodMovimento(SigaaListaComando.REMOVER_PROCESSO_SELETIVO);
			
			executeWithoutClosingSession(mov, getCurrentRequest());
			addMessage("Edital e Processo(s) Seletivo(s) removido(s) com sucesso",
					TipoMensagemUFRN.INFORMATION);
			processos = null;
			all = null;
		} catch (Exception e) {
			addMensagemErroPadrao();
			e.printStackTrace();
			return listaProcessos();
		}
		return listaProcessos();
	}
	
	/**
	 * Redireciona o usu�rio para a p�gina de listagem dos processos seletivos.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form_agenda.jsp</li>
	 * <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form_qtd_inscritos.jsp</li>
	 * <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form.jsp</li>
	 * <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/lista_inscritos.jsp</li>
	 * <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * <li>/sigaa.war/administracao/menus/cadastro.jsp</li>
	 * <li>/sigaa.war/administracao/menu_administracao.jsp</li>
	 * <li>/sigaa.war/graduacao/menus/programa.jsp</li>
	 * <li>/sigaa.war/lato/menu_coordenador.jsp</li>
	 * <li>/sigaa.war/stricto/menus/cadastro.jsp</li>
	 * <li>/sigaa.war/stricto/relatorios/processoSeletivo/relatorio_demanda_por_vaga.jsp</li>
	 * <li>/sigaa.war/stricto/coordenador.jsp</li>
	 * <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * <li>/sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/curso.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#listar()
	 */
	@Override
	public String listar() throws ArqException {
		if (processos != null){
			processos.clear(); // Corrige problema de n�o recarregar os dados
		}
		
		//Inicia o filtro ano de acordo com o calend�rio vigente, quando 
		if( getAcessoMenu().isPpg() ){
			setChkAno(true);
			setAno(getCalendarioVigente().getAno());
		}
		
		return super.listar();
	}
	
	/**
	 * Redireciona para a lista de processos seletivos.
	 * N�o � chamado por JSP.
	 * @return
	 */
	protected String listaProcessos(){
		if (processos != null)
			processos.clear(); // Corrige problema de n�o recarregar os dados
		return forward(getListPage()); 
	}

	public void setEdital(UploadedFile edital) {
		this.edital = edital;
	}

	/** Seta a equipe do Programa, para os processos seletivos de p�s-gradua��o.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li> /sigaa.war/public/processo_seletivo/form_inscricao.jsp</li>
	 * </ul>
	 * @param equipeDoPrograma
	 */
	public void setEquipeDoPrograma(List<SelectItem> equipeDoPrograma) {
		this.equipeDoPrograma = equipeDoPrograma;
	}

	/** Seta a inscri��o de candidato no processo seletivo. 
	 * M�todo n�o invocado por JSP's.
	 * @param inscrito
	 */
	public void setInscrito(StatusInscricaoSelecao inscrito) {
		this.inscrito = inscrito;
	}

	/** Seta o arquivo do Manual do Candidato. 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form.jsp</li>
	 * </ul>
	 * @param manualCandidato
	 */
	public void setManualCandidato(UploadedFile manualCandidato) {
		this.manualCandidato = manualCandidato;
	}

	/** Seta o N�vel de Ensino do Processo Seletivo. 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/public/processo_seletivo/lista.jsp</li>
	 * 		<li>/sigaa.war/public/processo_seletivo/lista_inscricoes.jsp</li>
	 * 		<li> /sigaa.war/public/processo_seletivo/lista_transferencia_voluntaria.jsp</li>
	 * </ul>
	 * @param nivel
	 */
	public void setNivel(char nivel) {
		this.nivel = nivel;
	}

	/** Seta os par�metros do programa de p�s-gradua��o.
	 * M�todo n�o invocado por JSP's. 
	 * @param parametrosProgramaPos
	 */
	public void setParametrosProgramaPos(
			ParametrosProgramaPos parametrosProgramaPos) {
		this.parametrosProgramaPos = parametrosProgramaPos;
	}

	/** Seta a lista de SelectItem de �reas de conhecimento.
	 * M�todo n�o invocado por JSP's. 
	 * @param possiveisAreas
	 */
	public void setPossiveisAreas(List<SelectItem> possiveisAreas) {
		this.possiveisAreas = possiveisAreas;
	}

	/**
	 * Visualiza dados de um processo seletivo cadastrado.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/lista_inscritos.jsp</li>
	 * 	<li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String view() throws SegurancaException, ArqException,
			NegocioException {

		setUrlRedirect( getDirBase() + "/lista.jsf" );
		popularView();

		if( hasErrors() )
			return null;
		
		return forward(getViewPage());
	}

	/**
	 * Visualiza dados da inscri��o de um candidato.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/lista_inscritos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String viewInscrito() throws SegurancaException, ArqException,
			NegocioException {
		verificaObjRemovido();
		InscricaoSelecaoMBean mBean = (InscricaoSelecaoMBean) getMBean("inscricaoSelecao");
		mBean.setId();
		
		InscricaoSelecao inscricao = getGenericDAO().findByPrimaryKey(mBean.getObj().getId(), InscricaoSelecao.class);
		mBean.setObj(inscricao);
		mBean.getObj().getPessoaInscricao();
		mBean.getObj().getProcessoSeletivo();
		
		// Popular respostas do question�rio, quando aplic�vel
		if ( inscricao.getProcessoSeletivo().getQuestionario() != null ) {
			getQuestionarioRespostasMBean().popularVizualicacaoRespostas(inscricao);
		}
		
		return forward("/administracao/cadastro/ProcessoSeletivo/view_inscrito.jsf");
	}
	
	/** Retorna o MBean respons�vel pelo question�rio.
	 * @return
	 */
	private QuestionarioRespostasMBean getQuestionarioRespostasMBean() {
		return (QuestionarioRespostasMBean) getMBean("questionarioRespostasBean");
	}
	
	/**
	 * Popula os dados e redireciona para a p�gina de resumo da �rea p�blica do sistema.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/public/processo_seletivo/lista.jsp</li>
	 * 	<li> /sigaa.war/public/programa/portal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String viewPublico() throws SegurancaException, ArqException,
			NegocioException {
		
		setUrlRedirect( "public/processo_seletivo/lista.jsf?aba=p-processo&nivel=" + getNivel() );
		popularView();
		
		if( hasErrors() )
			return null;
		
		return forward("/public/processo_seletivo/view.jsp");
	}
	
	/** Retorna a lista de SelectItem de linhas de pesquisa. 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> /sigaa.war/public/processo_seletivo/form_inscricao.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getPossiveisLinhas() {
		return possiveisLinhas;
	}

	/** Seta a lista de SelectItem de linhas de pesquisa. 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> /sigaa.war/public/processo_seletivo/form_inscricao.jsp</li>
	 * </ul>
	 * @param possiveisLinhas
	 */
	public void setPossiveisLinhas(List<SelectItem> possiveisLinhas) {
		this.possiveisLinhas = possiveisLinhas;
	}

	/** Retorna a lista de SelectItem question�rios s�cio-econ�mico. 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>  /sigaa.war/administracao/cadastro/ProcessoSeletivo/form_cursos.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getPossiveisQuestionarios() {
		return possiveisQuestionarios;
	}

	/** Seta a lista de SelectItem question�rios s�cio-econ�mico.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>  /sigaa.war/administracao/cadastro/ProcessoSeletivo/form_cursos.jsp</li>
	 * </ul>
	 * @param possiveisQuestionarios
	 */
	public void setPossiveisQuestionarios(List<SelectItem> possiveisQuestionarios) {
		this.possiveisQuestionarios = possiveisQuestionarios;
	}
	
	/** Retorna as vagas ofertadas no curso.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>  /sigaa.war/administracao/cadastro/ProcessoSeletivo/form_cursos.jsp</li>
	 * </ul> 
	 * @return
	 */
	public Integer getVagasInput() {
		return vagasInput;
	}

	/** Seta as vagas ofertadas no curso. 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>  /sigaa.war/administracao/cadastro/ProcessoSeletivo/form_cursos.jsp</li>
	 * </ul>
	 * @param vagasInput
	 */
	public void setVagasInput(Integer vagasInput) {
		this.vagasInput = vagasInput;
	}

	/**
	 * Gera PDF com a lista de presen�a dos inscritos.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * </ul>
	 *  
	 * @return
	 * @throws JRException
	 * @throws IOException
	 * @throws ArqException 
	 */
	public String listaPresencaInscritos() throws ArqException {
		setId();
		verificaObjRemovido();
		InscricaoSelecaoDao inscricaoDao = getDAO(InscricaoSelecaoDao.class);
		obj = inscricaoDao.findByPrimaryKey(obj.getId(), ProcessoSeletivo.class);
		
		List<Integer> ativos = new ArrayList<Integer>();
		ativos.add(StatusInscricaoSelecao.APROVADO_SELECAO); 
		ativos.add(StatusInscricaoSelecao.DEFERIDA);
		ativos.add(StatusInscricaoSelecao.INDEFERIDA);
		ativos.add(StatusInscricaoSelecao.SUBMETIDA);
		ativos.add(StatusInscricaoSelecao.SUPLENTE);
		
		Collection<InscricaoSelecao> inscritos = inscricaoDao.findInscritosByProcessoStatus(obj, ativos, true);

		if (isEmpty(inscritos)) {
			addMensagemErro("N�o foram encontradas inscri��es para este processo seletivo");
			return null;
		}

		Map<String, String> props = new HashMap<String, String>();
		props.put("nivel", NivelEnsino.getDescricao(obj.getNivelEnsino()));
		props.put("nomeCurso", !isEmpty(obj.getCurso())?obj.getNome():obj.getMatrizCurricular().getDescricao());
		props.put("nomeInstituicao", RepositorioDadosInstitucionais.getNomeInstituicao());
		props.put("nomeSistema", RepositorioDadosInstitucionais.get("nomeSigaa"));
		props.put("logoInstituicao", RepositorioDadosInstitucionais.get("logoInstituicao"));
		props.put("logoDepartamento", RepositorioDadosInstitucionais.get("logoInformatica"));
		try {
			JRDataSource ds = new JRBeanCollectionDataSource(inscritos);
			JasperPrint prt = JasperFillManager.fillReport(getReportSIGAA("InscritosProcessoSeletivo.jasper"), props, ds);
			
			JasperReportsUtil.exportarPdf(prt, "inscritos.pdf", getCurrentResponse());
			FacesContext.getCurrentInstance().responseComplete();
		} catch (JRException e) {
			notifyError(e);
			addMensagemErroPadrao();
		} catch (IOException e) {
			notifyError(e);
			addMensagemErroPadrao();
		}
		return null;
	}
	
	/**
	 * Redireciona para o Relat�rio de Demandas e Vagas 
	 * <br/>
	 * M�todo chamado pelas seguintes JSP:
	 * <ul> 
	 * 	  <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 *    <li>/sigaa.war/stricto/menus/relatorios.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String iniciaRelatorioDemandaVagas() throws DAOException{
		if (!isPortalPpg()){
			unidade = getProgramaStricto();
			return listaDemandaVagas();			
		}
		
		return forward(JSP_SELECIONA_PROGRAMA);
	}
	
	/**
	 * Gera Relat�rio de Todos os Processos Seletivos com a quantidade de vagas dispon�vel e o total de inscritos.<br />
	 * M�todo chamado pela seguinte JSP:
	 * <ul> 
	 * 	  <li>/sigaa.war/stricto/relatorios/processoSeletivo/relatorio_demanda_por_vaga.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String listaDemandaVagas() throws DAOException{
		
		if (dataInicio != null && dataFim != null)
			ValidatorUtil.validaInicioFim(dataInicio, dataFim, "Data de In�cio", erros);
		
		if (hasErrors())
			return null;
		
		ProcessoSeletivoDao dao = getDAO(ProcessoSeletivoDao.class);
		
		try{		
			listaResultado = dao.findListaDemandaVagas(unidade.getId(), nivel , dataInicio, dataFim);
			
			if(unidade != null)
				if (unidade.getId()!=0)
					unidade = dao.findByPrimaryKey(unidade.getId(), Unidade.class);
				else
					unidade.setNome("TODOS");			
			
			if (listaResultado.isEmpty()){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;				
			}

			descricaoNivel = "TODOS";
			if (nivel > 0 && NivelEnsino.isValido(nivel))
				descricaoNivel = NivelEnsino.getDescricao(nivel);				
			
		} finally {
			if (dao != null)
				dao.close();
		}		
		return forward(JSP_RELATORIO_DEMANDA_VAGA);
	}

	/**
	 * Adiciona um item na lista de processos seletivos, utilizado somente para GRADUA��O
	 * <br / >
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form_cursos.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws ArqException
	 */
	public void adicionarProcessos() throws ArqException{
		
		if(!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_ALTERAR_PROCESSO_SELETIVO.getId()))
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA,"");
		if ( getVagasInput() <= 0 ) {
			addMensagemErro("N�mero de vagas deve ser maior que zero");
		}
		if (getMatrizCurricularSel() <= 0 && getCursoSel() <= 0 ) {
			addMensagemErro("Escolha uma Matriz Curricular ou Curso v�lido");
		}
		
		if (hasErrors())
			return;
		
		MatrizCurricular objMatriz = null;
		Curso objCurso = null;
		Questionario objQuestionario = null;
		ProcessoSeletivo p = new ProcessoSeletivo();
		
		//Campos envolvidos no formul�rio que adiciona processos a uma lista.
		if(getQuestionarioSel()>0)
			objQuestionario = getGenericDAO().findByPrimaryKey(getQuestionarioSel(),Questionario.class);
		if(getMatrizCurricularSel()>0)
			objMatriz = getGenericDAO().findByPrimaryKey(getMatrizCurricularSel(),MatrizCurricular.class);
		else if(getCursoSel()>0)
			objCurso = getGenericDAO().findByPrimaryKey(getCursoSel(),Curso.class);
			
		//Verifica se existe um processo com curso ou matriz selecionado
		boolean novo = true;
		for (ProcessoSeletivo ps : processosSeletivos) {
			if((objMatriz !=null && objMatriz.equals( ps.getMatrizCurricular())) || 
					(objCurso!=null && objCurso.equals(ps.getCurso()))){
				if(!ps.getAtivo()) {
					processosSeletivos.get(processosSeletivos.indexOf(ps)).setAtivo(true);
					novo = false;
					p = ps;
				}
				else p = null;
				break;
			}
		}
		
		if(p!=null){
			
			if(objMatriz != null){
				p.setMatrizCurricular(objMatriz);
				p.setCurso(null);
			}
			if(objCurso != null){
				p.setCurso(objCurso);
				p.setMatrizCurricular(null);
			}
			if(!isEmpty(objQuestionario))		
				p.setQuestionario(objQuestionario);
			
			p.setVaga(getVagasInput());
			p.setEditalProcessoSeletivo(obj.getEditalProcessoSeletivo());
			p.getEditalProcessoSeletivo().setStatus(obj.getEditalProcessoSeletivo().getStatus());
			p.setAtivo(true);
			p.setCriadoPor(p.getEditalProcessoSeletivo().getCriadoPor());
			p.setCriadoEm(p.getEditalProcessoSeletivo().getCriadoEm());
			
			if (novo)
				processosSeletivos.add(p);
			
			setVagasInput(0);
			setCursoSel(0);
			possivelAlterarNumVagas = false;
			
		}else{
			addMensagemErro("Matriz Curricular ou Curso j� adicionado � lista");
		}
		
	}
	
	/**
	 * Remove um item da lista de processos seletivos, utilizado somente para GRADUA��O
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException
	 */
	public void removerProcessos(ActionEvent e) throws ArqException{
		
		ProcessoSeletivo itemPS = (ProcessoSeletivo) e.getComponent().getAttributes().get("itemProcessoSeletivo");
		
		for (ProcessoSeletivo p : processosSeletivos){
			if (p.equals(itemPS)) {
				if( isEmpty(p) ){
					processosSeletivos.remove(p);
				}else{
					p.setAtivo(false);
				}	
				break;
			}
		}
	
	}
	
	/**
	 * Adiciona um item na lista de datas de agendamento, 
	 * utilizado somente para GRADUA��O
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form_agenda.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws ArqException
	 */
	public void adicionarAgendas() throws ArqException{
		
		
		if(!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_ALTERAR_PROCESSO_SELETIVO.getId()))
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA,"");
		
		if( obj.getId()==0 ){
			if( !ValidatorUtil.isEmpty(obj.getEditalProcessoSeletivo().getInicioPeriodoAgenda()) )
				ValidatorUtil.validateFuture(CalendarUtils.adicionaDias(obj.getEditalProcessoSeletivo().getInicioPeriodoAgenda(),1), "Data In�cio  do Per�odo de Agendamento", erros);
			else
				ValidatorUtil.validateRequired(obj.getEditalProcessoSeletivo().getInicioPeriodoAgenda(), "Data In�cio do Per�odo de Agendamento", erros);
			if( !ValidatorUtil.isEmpty(obj.getEditalProcessoSeletivo().getFimPeriodoAgenda()) )
				ValidatorUtil.validaInicioFim(obj.getEditalProcessoSeletivo().getInicioPeriodoAgenda(), obj.getEditalProcessoSeletivo().getFimPeriodoAgenda(), "Per�odo de Agendamento", erros);
			else
				ValidatorUtil.validateRequired(obj.getEditalProcessoSeletivo().getFimPeriodoAgenda(), "Data fim do Per�odo de Agendamento", erros);
		}
		
		if (hasErrors())
			return;

		int dias = CalendarUtils.calculoDias(obj.getEditalProcessoSeletivo().getInicioPeriodoAgenda(), obj.getEditalProcessoSeletivo().getFimPeriodoAgenda());
		Date proxDataAgenda = obj.getEditalProcessoSeletivo().getInicioPeriodoAgenda();
		
		//Adiciona data de agendamento caso n�o exista na lista
		for(int i = 0; i<=dias ; i++){
			AgendaProcessoSeletivo agenda = new AgendaProcessoSeletivo();
			agenda.setDataAgenda(proxDataAgenda);
			if(obj.getEditalProcessoSeletivo().getAgendas()==null || !obj.getEditalProcessoSeletivo().getAgendas().contains(agenda))
				obj.getEditalProcessoSeletivo().addAgenda(agenda);
			
			proxDataAgenda = CalendarUtils.adicionaUmDia(proxDataAgenda);
		}
		
		//Remove as datas caso esteja fora do per�odo de agendamento
		List<AgendaProcessoSeletivo> agendasChk = new ArrayList<AgendaProcessoSeletivo>();
		agendasChk.addAll(obj.getEditalProcessoSeletivo().getAgendas());
		Date dataAtual = new Date();
		for (AgendaProcessoSeletivo a : agendasChk) {
			
			if((a.getDataAgenda().getTime() < obj.getEditalProcessoSeletivo().getInicioPeriodoAgenda().getTime() ||
					a.getDataAgenda().getTime() > obj.getEditalProcessoSeletivo().getFimPeriodoAgenda().getTime())){
				if(obj.getId() > 0 &&  dataAtual.getTime() >= obj.getEditalProcessoSeletivo().getInicioInscricoes().getTime()){
					addMensagemErro("N�o � poss�vel reduzir o per�odo de agendamento em um processo seletivo em andamento.");
					break;
				}	
				obj.getEditalProcessoSeletivo().getAgendas().remove(a);
			}	
		}
		

	}
	
	/**
	 * M�todo respons�vel em validar o per�odo de agendamento em rela��o ao per�odo de inscri��o
	 */
	private void validarPeriodoAgenda(){
		
		if(isAcessoProcessoSeletivoGraduacao() && obj.isPossuiAgendamento()){
		
			if ( obj.getEditalProcessoSeletivo().getInicioPeriodoAgenda() == null 
					|| obj.getEditalProcessoSeletivo().getFimPeriodoAgenda() == null ||
					obj.getEditalProcessoSeletivo().getAgendas() == null || 
					obj.getEditalProcessoSeletivo().getAgendas().size() == 0) 
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Per�odo de Agendamento");
			
			else if ( obj.getEditalProcessoSeletivo().getInicioPeriodoAgenda().getTime() 
						>= obj.getEditalProcessoSeletivo().getFimPeriodoAgenda().getTime()) 
				addMensagemErro("Data final n�o pode ser igual ou inferior a inicial");
			
			else if(obj.getEditalProcessoSeletivo().getInicioPeriodoAgenda().getTime() < 
						obj.getEditalProcessoSeletivo().getFimInscricoes().getTime() 
						|| obj.getEditalProcessoSeletivo().getFimPeriodoAgenda().getTime() < 
							obj.getEditalProcessoSeletivo().getFimInscricoes().getTime())
				addMensagemErro("Per�odo de agendamento n�o dever ser anterior ao per�odo de inscri��o.");
			
		}
	}
	
	
	/**
	 * Remove um item da lista de datas de agendamento, utilizado somente para GRADUA��O
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form_agenda.jsp</li>
	 * </ul>
	 * @throws ArqException
	 * @throws ParseException 
	 */
	public void removerAgendas(ActionEvent e){
		int posicao = getParameterInt("dataSel");
		List<AgendaProcessoSeletivo> agendas = obj.getEditalProcessoSeletivo().getAgendas();
		if(agendas != null)
				agendas.remove(posicao);
	}

	/** Retorna a lista de Processos Seletivos. 
	 * <br / >
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form_cursos.jsp</li>
	 * </ul>
	 * @return
	 */
	public ArrayList<ProcessoSeletivo> getProcessosSeletivos() {
		return processosSeletivos;
	}

	/** Seta a lista de Processos Seletivos. 
	 * <br / >
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form_cursos.jsp</li>
	 * </ul>
	 * @param processosSeletivos
	 */
	public void setProcessosSeletivos(ArrayList<ProcessoSeletivo> processosSeletivos) {
		this.processosSeletivos = processosSeletivos;
	}
	
	/** Retorna o ID da matriz curricular a se removida da lista. 
	 * <br / >
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form_cursos.jsp</li>
	 * </ul>
	 * @return
	 */
	public Integer getMatrizCurricularSel() {
		return matrizCurricularSel;
	}

	/** Seta o ID da matriz curricular a se removida da lista. 
	 * <br / >
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form_cursos.jsp</li>
	 * </ul>
	 * @param matrizCurricularSel
	 */
	public void setMatrizCurricularSel(Integer matrizCurricularSel) {
		this.matrizCurricularSel = matrizCurricularSel;
	}

	/** Retorna o ID do Curso.
	 * <br / >
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form_cursos.jsp</li>
	 * </ul>
	 * @return
	 */
	public Integer getCursoSel() {
		return cursoSel;
	}

	/** Seta o ID do Curso. 
	 * <br / >
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form_cursos.jsp</li>
	 * </ul>
	 * @param cursoSel
	 */
	public void setCursoSel(Integer cursoSel) {
		this.cursoSel = cursoSel;
	}
	
	/**
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>//sigaa.war/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	@Override
	public String atualizar() throws ArqException {
		checkChangeRole();
		obj = new ProcessoSeletivo();
		setId();
		obj = getGenericDAO().findAndFetch(obj.getId(), ProcessoSeletivo.class, "questionario");
		verificaObjRemovido();
		popularParametrosPrograma();
		
		if(obj.getEditalProcessoSeletivo().getAgendas()!=null && obj.getEditalProcessoSeletivo().getAgendas().size()>0)
			obj.getEditalProcessoSeletivo().getAgendas().iterator().next();
		
		String retorno = super.atualizar();
		
		if( obj.getEditalProcessoSeletivo().getRestricaoInscrito() == null )
			obj.getEditalProcessoSeletivo().setRestricaoInscrito( new RestricaoInscricaoSelecao() );
		
		if (obj.getEditalProcessoSeletivo().getIdConfiguracaoGRU() != null) {
			possuiTaxaInscricao = true;
			GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
			try {
				configuracao = dao.findByPrimaryKey(obj.getEditalProcessoSeletivo().getIdConfiguracaoGRU(), ConfiguracaoGRU.class);
			} finally {
				dao.close();
			}
		} else {
			possuiTaxaInscricao = false;
		}
		
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_ALTERAR_PROCESSO_SELETIVO.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_ALTERAR_PROCESSO_SELETIVO);
		
		return retorno;
	}


	/** 
	 * Verifica se o objeto manipulado j� foi removido.
	 * @throws DAOException
	 * @throws ArqException
	 */
	private void verificaObjRemovido() throws DAOException, ArqException {
		ProcessoSeletivo objRefresh = getGenericDAO().refresh(obj);
		if (isEmpty(objRefresh) || !objRefresh.isAtivo())
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA,"");
	}

	/** 
	 * Redireciona o usu�rio para o formul�rio de cadastro de Processo Seletivo.<br />
	 * M�todo n�o invocado por JSP.
	 * @return
	 */
	public String direcionar() {
		return forward("administracao/cadastro/ProcessoSeletivo/form.jsf");
	}
	
	/**
	 * Preenche o numero de vagas de acordo com o informando no Cadastro da Proposta de Curso Lato Sensu.
	 * M�todo n�o invocado por JSP's.
	 * @param cursoLato
	 * @throws DAOException
	 */
	public void preencherNumeroVagas(int cursoLato) throws DAOException{
		CursoLatoDao dao = getDAO(CursoLatoDao.class);
		CursoLato curso;
		try {
			curso = dao.findById(cursoLato);
			if (isEmpty(vagasInput) && !isEmpty(curso)){
				setVagasInput(curso.getNumeroVagas());
				setCursoSel(curso.getId());
				possivelAlterarNumVagas = true;
			}else{
				setVagasInput(0);
				setCursoSel(0);
				possivelAlterarNumVagas = false;
			}
		} finally  {
			dao.close();
		}
		redirectMesmaPagina();
	}
	
	/**
	 * Retorna todas as restri��es dispon�veis de acordo com o n�vel processo seletivo a ser cadastrado.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getComboRestricaoInscricao() throws DAOException{
		if( comboRestricaoInscricao == null ){
			comboRestricaoInscricao = 
				toSelectItems( getGenericDAO().findByExactField(
							RestricaoInscricaoSelecao.class, "nivel", 
							getNivelEnsino()), "id", "descricao");
		}	
		return comboRestricaoInscricao;
	}
		 
	/**
	 * Redireciona a tela para a {@link this#urlRedirect} setada no m�todo que iniciou a opera��o.
	 *	<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> /sigaa.war/administracao/cadastro/ProcessoSeletivo/resumo.jsp</li>
	 * </ul>
	 * @return
	 */
	public String redirectUrl(){
		if( urlRedirect != null)
			return redirectJSF( urlRedirect );
		return null;
	}
	
	public List<Map<String, Object>> getListaResultado() {
		return listaResultado;
	}

	public void setListaResultado(List<Map<String, Object>> listaResultado) {
		this.listaResultado = listaResultado;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public Integer getQuestionarioSel() {
		return questionarioSel;
	}

	public void setQuestionarioSel(Integer questionarioSel) {
		this.questionarioSel = questionarioSel;
	}

	/**
	 * Retorna o ano selecionado no filtro da busca dos processos seletivos.
	 * <br / >
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * </ul> 
	 * @return
	 */
	public Integer getAno() {
		if(!isChkAno())
			ano = null;
		return ano;
	}

	/**
	 * Define o ano no filtro da busca dos processos seletivos.
	 * <br / >
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * </ul> 
	 * @param ano
	 */
	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public boolean isChkUnidade() {
		return chkUnidade;
	}

	public void setChkUnidade(boolean chkUnidade) {
		this.chkUnidade = chkUnidade;
	}

	public boolean isChkAno() {
		return chkAno;
	}

	public void setChkAno(boolean chkAno) {
		this.chkAno = chkAno;
	}

	/**
	 * Retorna o id da unidade selecionado no filtro da busca dos processos seletivos.
	 * Utilizado somente pela PPG.
	 * <br / >
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * </ul> 
	 * @return
	 */
	public Integer getIdUnidade() {
		if(!isChkUnidade())
			idUnidade = null;
		return idUnidade;
	}

	/**
	 * Define o id da unidade no filtro da busca dos processos seletivos.
	 * Utilizado somente pela PPG.
	 * <br / >
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * </ul> 
	 * @param idUnidade
	 */
	public void setIdUnidade(Integer idUnidade) {
		this.idUnidade = idUnidade;
	}

	/**
	 * Retorna o status selecionado no filtro da busca dos processos seletivos.
	 * Utilizado somente pela PPG.
	 * <br / >
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * </ul> 
	 * @param status
	 */
	public Integer getStatus() {
		if(!isChkStatus())
			status = null;
		return status;
	}

	/**
	 * Define o status no filtro da busca dos processos seletivos.
	 * Utilizado somente pela PPG.
	 * <br / >
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * </ul> 
	 * @param status
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	public boolean isChkStatus() {
		return chkStatus;
	}

	public void setChkStatus(boolean chkStatus) {
		this.chkStatus = chkStatus;
	}
	
	public int getNumeroDiasPassados(){
		return NUMERO_DIAS_PASSADOS_PUBLICACAO;
	}

	public Integer getStatusInscricao() {
		return statusInscricao;
	}

	public void setStatusInscricao(Integer statusInscricao) {
		this.statusInscricao = statusInscricao;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public String getDescricaoNivel() {
		return descricaoNivel;
	}

	public void setDescricaoNivel(String descricaoNivel) {
		this.descricaoNivel = descricaoNivel;
	}

	public boolean isPossivelAlterarNumVagas() {
		return possivelAlterarNumVagas;
	}

	public void setPossivelAlterarNumVagas(boolean possivelAlterarNumVagas) {
		this.possivelAlterarNumVagas = possivelAlterarNumVagas;
	}
	
	public void setUrlRedirect(String urlRedirect){
		this.urlRedirect = urlRedirect;
	}

	public boolean isPossuiTaxaInscricao() {
		return possuiTaxaInscricao;
	}

	public void setPossuiTaxaInscricao(boolean possuiTaxaInscricao) {
		this.possuiTaxaInscricao = possuiTaxaInscricao;
	}

	public ConfiguracaoGRU getConfiguracao() {
		return configuracao;
	}
	
}
