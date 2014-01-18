/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 08/03/2010
 */
package br.ufrn.sigaa.ensino.stricto.docenciaassistida.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.TamanhoArquivo;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dao.PlanoDocenciaAssistidaDao;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.AtividadeDocenciaAssistida;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.FormaAtuacaoDocenciaAssistida;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.FrequenciaAtividadeDocenciaAssistida;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.HistoricoPlanoDocenciaAssistida;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.PlanoDocenciaAssistida;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.TurmaDocenciaAssistida;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.reuni.dao.IndicacaoBolsistaReuniDao;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.IndicacaoBolsistaReuni;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.PeriodoIndicacaoReuni;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.PlanoTrabalhoReuni;
import br.ufrn.sigaa.parametros.dominio.ParametrosTurmaVirtual;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.ModalidadeBolsaExterna;

/**
 * Este MBean tem como finalidade de auxiliar nas operações relacionadas ao plano de docência assistida.
 * 
 * @author Arlindo Rodrigues
 */
@Component("planoDocenciaAssistidaMBean")
@Scope("request")
public class PlanoDocenciaAssistidaMBean extends SigaaAbstractController<PlanoDocenciaAssistida> {
	
	/** Lista de turmas para o combo. */
	private List<SelectItem> comboTurmas = new ArrayList<SelectItem>();
	
	/** Coleção de Formas de Atuação (Atividades), para ser incluídas no plano	 */
	private Collection<FormaAtuacaoDocenciaAssistida> formasAtuacao;
	
	/** Atividade Selecionada. */
	private AtividadeDocenciaAssistida atividade;
	
	/** Turma Selecionada */
	private TurmaDocenciaAssistida turmaDocencia;

	/** Coleção de turmas para preencher o combo */
	private Collection<Turma> turmas;
	
	/** Turma Selecionada */
	private Turma turmaSelecionada;
	
	/** Orientador do discente */
	private OrientacaoAcademica orientacao;
	
	/** Indica se irá cadastrar ou alterar */
	private boolean cadastrar;
	
	/** Atributo para o auxilio na exibição dos dados */
	private PlanoTrabalhoReuni planoTrabalhoReuni;
	
	/** Atributo para identificar qual período de indicação do plano de docência assistida foi selecionado */
	private PeriodoIndicacaoReuni periodoIndicacao;
	
	/** Lista de indicações do discente logado */
	private Collection<IndicacaoBolsistaReuni> listaIndicacoes;
	
	/** Lista de Planos do discente */
	private Collection<PlanoDocenciaAssistida> planosSemIndicacao;
	
	/** Comando que será executado pelo processador */
	private Comando comando;
	
	/** Indica se a impressão do plano é para o relatório semestral ou não */
	private boolean relatorioSemestral = false;
	
	/**indicar se o discente é bolsista ou não*/
	private boolean bolsista = true;
	
	/** Filtros utilizados na busca de plano de docência assistida */
	private boolean filtroUnidade;
	/** Filtro de Ano e Período utilizado na busca de plano de docência assistida */
	private boolean filtroAnoPeriodo;	
	/** Filtro de status utilizado na busca de plano de docência assistida */
	private boolean filtroStatus;
	/** Filtro de nível utilizado na busca de plano de docência assistida */
	private boolean filtroNivel;
	/** Filtro de modalidade utilizado na busca de plano de docência assistida */
	private boolean filtroModalidade;
	/** Filtro de tipo da bolsa utilizado na busca de plano de docência assistida */
	private boolean filtroTipoBolsa;
	/** Filtro de discente utilizado na busca de plano de docência assistida */
	private boolean filtroDiscente;
	/** Filtro de componente curricular utilizado na busca de plano de docência assistida */
	private boolean filtroComponente;
	/** Filtro de atividade utilizado na busca de plano de docência assistida */
	private boolean filtroAtividade;
	/** Filtro de carga horária da atividade utilizado na busca de plano de docência assistida */
	private boolean filtroCargaHoraria;
	
	/** Unidade selecionada na busca */
	private Unidade unidade = new Unidade();
	/** Ano informado na busca */
	private Integer ano;
	/** Período informado na busca */
	private Integer periodo;
	/** Status informado na busca */
	private Integer status;
	/** Nível informado na busca */
	private Character nivel;
	/** Modalidade de Bolsa informado na busca */
	private ModalidadeBolsaExterna modalidadeBolsa;
	/** Tipo de Bolsa informado na busca */
	private Integer tipoBolsa;
	/** Discente informado na busca */
	private String discente;
	/** CH Inicial informada na busca */
	private Integer chInicial;
	/** CH Final informada na busca */
	private Integer chFinal;
	/** Novo Status Definido pelo Coordenador */
	private int novoStatus;
	/** Arquivo enviado */
	private UploadedFile arquivo;	
	/** Título do Formulário de Busca */
	private String tituloFormBusca;
	/** Indica se é a busca sem Indicação ou não */
	private boolean buscaSemIndicacao;
	/** Constante que indica o tipo da busca "Com Indicação" */
	private static final int COM_INDICACAO = 1;
	/** Constante que indica o tipo da busca "Sem Indicação" */
	private static final int SEM_INDICACAO = 2;
	/** Constante que indica o tipo de análise do plano */
	private static final int ANALISE_PLANO = 1;
	/** Constante que indica o tipo de análise do relatório */
	private static final int ANALISE_RELATORIO = 2;
	
	/** Componente Curricular selecionado na busca */
	private ComponenteCurricular componente = new ComponenteCurricular();
	
	/** Lista de Atividades não previstas */
	private List<AtividadeDocenciaAssistida> atividadesNaoPrevistas = new ArrayList<AtividadeDocenciaAssistida>();
	
	/** Lista de status para analise do plano */
	private Map<Integer, String> statusAnalise;
	
	/** Tamanho máximo do arquivo */
	private int tamanhoMaxArquivo;
	
	/** Construtor da Classe */
	public PlanoDocenciaAssistidaMBean() {
		initObj();
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		buscaSemIndicacao = false;
				
		filtroUnidade = false;	
		filtroAnoPeriodo = false;	
		filtroStatus = false;
		filtroNivel = false;
		filtroModalidade = false;
		filtroTipoBolsa = false;
		
		status = 0;
		nivel = '0';
		modalidadeBolsa = new ModalidadeBolsaExterna();
		tipoBolsa = 0;		
	}
	
	/** Inicializa os objetos */
	private void initObj(){
		if (obj == null)
			obj = new PlanoDocenciaAssistida();
		
		if (isEmpty(obj.getComponenteCurricular())) {
			obj.setComponenteCurricular(new ComponenteCurricular());
		}
		
		if (isEmpty(obj.getServidor()))
			obj.setServidor(new Servidor());
		
		if (isEmpty(obj.getCurso())) {
			obj.setCurso(new Curso());
		}
		
		if (isEmpty(obj.getDiscente())) {
			obj.setDiscente(new DiscenteStricto());			
		}
			
		if (isEmpty(obj.getTurmaDocenciaAssistida())){
			obj.setTurmaDocenciaAssistida(new ArrayList<TurmaDocenciaAssistida>());
		}		
		
		if (isEmpty(obj.getAtividadeDocenciaAssistida())) {
			obj.setAtividadeDocenciaAssistida(new ArrayList<AtividadeDocenciaAssistida>());
		} 
		
		if (isEmpty(turmaDocencia)){
			turmaDocencia = new TurmaDocenciaAssistida();
			turmaDocencia.setTurma(new Turma());
		}
		atividade = new AtividadeDocenciaAssistida();
		atividade.setPlanoDocenciaAssistida(new PlanoDocenciaAssistida());
		atividade.setFormaAtuacao(new FormaAtuacaoDocenciaAssistida());
		atividade.setFrequenciaAtividade(new FrequenciaAtividadeDocenciaAssistida());
		atividade.getFormaAtuacao().setId(-1);
	
		bolsista = obj.isBolsista();
		if (isEmpty(obj.getModalidadeBolsa()) && isEmpty(obj.getOutraModalidade()))
			obj.setModalidadeBolsa(new ModalidadeBolsaExterna(-1));
		
		obj.setBolsista(true);		
		if (isEmpty(obj.getPeriodoIndicacaoBolsa()) && isEmpty(periodoIndicacao)){
			if (isEmpty(obj.getModalidadeBolsa()) && isEmpty(obj.getOutraModalidade())){
				obj.setModalidadeBolsa(new ModalidadeBolsaExterna(-1));
				obj.setBolsista(false);			
			} else if (!isEmpty(obj.getOutraModalidade())){
				obj.setModalidadeBolsa(new ModalidadeBolsaExterna(0));
			} 			
			obj.setPeriodoIndicacaoBolsa(null);
		} else {
			obj.setModalidadeBolsa(new ModalidadeBolsaExterna(ModalidadeBolsaExterna.REUNI));	
			if (!isEmpty(periodoIndicacao))
				obj.setPeriodoIndicacaoBolsa(periodoIndicacao);				
		}	
	}	
	
	/**
	 * Inicia o caso de uso exibindo a lista de indicações ativas.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/portais/discente/discente.jsp</li>
	 *  <li>/sigaa.war/stricto/solicitacao_bosas_reuni/view_solicitacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String iniciar() throws DAOException{	
		relatorioSemestral = false;
		listaIndicacoes = new ArrayList<IndicacaoBolsistaReuni>();
		planosSemIndicacao = new ArrayList<PlanoDocenciaAssistida>();
		return forward(getListPage());
	}

	/**
	 * Retorna todas as indicações ativas do discente logado.
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *	<li>/sigaa.war/stricto/plano_docencia_assistida/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<IndicacaoBolsistaReuni> getAllIndicacoes() throws DAOException{
		if (isEmpty(listaIndicacoes)){
			IndicacaoBolsistaReuniDao dao = getDAO(IndicacaoBolsistaReuniDao.class);
			try{
				if (isPortalDiscente()){
					listaIndicacoes = dao.findIndicacoesByDiscentePeriodo(getDiscenteUsuario().getDiscente().getId(), 0);					
				} else if (isPortalCoordenadorStricto() || isPortalPpg()){
					listaIndicacoes = dao.findIndicacoesByDiscentePeriodo(0, planoTrabalhoReuni.getId());
				}							
				// Ordena as indicações pelo período (ano.periodo)
				for (IndicacaoBolsistaReuni indicacao : listaIndicacoes){
					if (indicacao.getPeriodosIndicacao() != null){
						Collections.sort( indicacao.getPeriodosIndicacao() , new Comparator<PeriodoIndicacaoReuni>(){
							public int compare(PeriodoIndicacaoReuni arg0,	PeriodoIndicacaoReuni arg1) {
								return arg0.getAnoPeriodo() < arg1.getAnoPeriodo() ? 1 : -1;  
							}
						});																
					}
				}
			} finally {
				if (dao != null)
					dao.close();
			}			
		} 
		
		if (listaIndicacoes == null)
			listaIndicacoes = new ArrayList<IndicacaoBolsistaReuni>();
		
		return listaIndicacoes;
	}
	
	/**
	 * Exibe A Lista de Planos de Docência Assistida Sem Indicação.
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 *  <li>/sigaa.war/stricto/menus/bolsas_reuni.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String listarSemIndicao() throws SegurancaException{
		checkRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS,
				SigaaPapeis.PPG, SigaaPapeis.MEMBRO_APOIO_DOCENCIA_ASSISTIDA);
		tituloFormBusca = "Planos de Docência Assistida sem Indicação";
		buscaSemIndicacao = true;
		return forward(getListaSemIndicacao());
	}
	
	/**
	 * Exibe A Consulta de Planos de Docência Assistida.
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/menus/bolsas_reuni.jsp</li>
	 *  <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */	
	public String iniciarBuscaGeral() throws SegurancaException{
		checkRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS, 
				SigaaPapeis.PPG, SigaaPapeis.MEMBRO_APOIO_DOCENCIA_ASSISTIDA);
		tituloFormBusca = "Gerenciar Planos de Docência Assistida";
		buscaSemIndicacao = false;
		return forward(getListaSemIndicacao());		
	}
	
	/**
	 * Inicia o cadastro de Plano de Docência Assistida.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/plano_docencia_assistida/lista.jsp</li>
	 *  <li>/sigaa.war/stricto/plano_docencia_assistida/include_acoes.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarCadastro() throws ArqException{
		if (isEmpty( orientacao )){
			addMensagemErro("Não é possivel criar um Plano de Docência Assistida sem ter um Orientador");
			return null;
		}
		/* Verifica se já possui Plano cadastrado para o período corrente */
		if (existePlanoCadastrado())
			return null;
		
		relatorioSemestral = false;
		
		obj = new PlanoDocenciaAssistida();
		
		initObj();
		/* Seta o discente logado */ 
		obj.setDiscente((DiscenteStricto) getDiscenteUsuario());
		obj.getDiscente().setOrientacao(getOrientacao());
		/* Seta o Ano e Período para facilitar nas consultas */
		if (isEmpty(periodoIndicacao)){
			obj.setAno(getCalendarioVigente().getAno());
			obj.setPeriodo(getCalendarioVigente().getPeriodo());			
		} else {
			obj.setAno(periodoIndicacao.getAno());
			obj.setPeriodo(periodoIndicacao.getPeriodo());			
		}
				
		comando = SigaaListaComando.CADASTRAR_PLANO_DOCENCIA_ASSISTIDA;
		prepareMovimento(comando);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_PLANO_DOCENCIA_ASSISTIDA.getId());
		
		return redirectForm();
	}	
	
	/**
	 * Verifica se existe Plano de docência cadastrado para o período
	 * @return
	 * @throws DAOException 
	 */
	private boolean existePlanoCadastrado() throws DAOException{
		PlanoDocenciaAssistidaDao dao = getDAO(PlanoDocenciaAssistidaDao.class);
		try {
			List<PlanoDocenciaAssistida> planos = null;
			if (isEmpty(periodoIndicacao))
				planos = dao.findByPeriodoSituacao(getDiscenteUsuario(), null, getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo(), false);
			else
				planos = dao.findByPeriodoSituacao(getDiscenteUsuario(), null, periodoIndicacao.getAno(), periodoIndicacao.getPeriodo(), true);
			if (!isEmpty(planos)){
				addMensagemErro("Não é possível criar um plano de docência assistida, pois já existe um aprovado ou submetido para o mesmo período.");				
				return true;		
			}
		} finally {
			if (dao != null)
				dao.close();
		}		
		return false;
	}
	
	/**
	 * Inicia a Alteração de Plano de Docência Assistida.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/plano_docencia_assistida/include_acoes.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String preAlterar() throws ArqException{
		populateObj();
		initObj();		
		comando = SigaaListaComando.ALTERAR_PLANO_DOCENCIA_ASSISTIDA;		
		prepareMovimento(comando);
		setOperacaoAtiva(SigaaListaComando.ALTERAR_PLANO_DOCENCIA_ASSISTIDA.getId());
		
		if (ValidatorUtil.isNotEmpty(obj.getComponenteCurricular()) && !obj.getComponenteCurricular().isAtividade())
			carregaTurmas();
		
		return redirectForm();		
	}
	
	/**
	 * Busca por Plano de Docência Assistida Sem Indicação
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/plano_docencia_assistida/lista_sem_indicacoes.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String buscar() throws DAOException{

		if (isPortalCoordenadorStricto()){
			filtroUnidade = true;
			unidade = getProgramaStricto();
		}
		
		if (!filtroUnidade && !filtroAnoPeriodo && !filtroStatus && 
				!filtroNivel && !filtroModalidade && !filtroTipoBolsa && 
				!filtroDiscente && !filtroComponente && !filtroAtividade && !filtroCargaHoraria){
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			return null;
		}
		
		if (filtroAnoPeriodo && (ano < 1950 || periodo < 1 || periodo > 2)){
			addMensagemErro("Informe Corretamente o Ano e Período.");
			return null;
		}		
		
		if (filtroCargaHoraria && (chInicial < 0 || chFinal < chInicial || chFinal < 0)){
			addMensagemErro("Informe Corretamente a Carga Horária.");
			return null;
		}		
		
		/* Se a busca for sem indicação atualiza os valores para trazer somente os sem indicação */
		if (buscaSemIndicacao){
			filtroTipoBolsa = true;
			tipoBolsa = SEM_INDICACAO;
		}		
				
		PlanoDocenciaAssistidaDao dao = getDAO(PlanoDocenciaAssistidaDao.class);
		try {
			DiscenteAdapter d = null;
			if (filtroDiscente && !ValidatorUtil.isEmpty(discente)){
				d = new DiscenteStricto();
				d.setPessoa(new Pessoa(0, discente));
			}
			
			planosSemIndicacao = dao.findGeral(d, 
					(filtroUnidade && unidade.getId() > 0 ? unidade.getId() : null),
					(filtroAnoPeriodo && ano > 0 ? ano : null), 
					(filtroAnoPeriodo && periodo > 0 ? periodo : null), 
					(filtroNivel && nivel != '0' ? nivel : null), 
					(filtroModalidade && modalidadeBolsa.getId() > 0 ? modalidadeBolsa.getId() : null),
					(filtroTipoBolsa && tipoBolsa > 0 ? tipoBolsa != COM_INDICACAO : null), 
					(filtroComponente && !ValidatorUtil.isEmpty(componente) ? componente : null),
					(filtroAtividade && atividade.getFormaAtuacao().getId() > -1 ? atividade.getFormaAtuacao() : null),
					(filtroCargaHoraria && chInicial != null && chInicial > 0 ? chInicial : null),
					(filtroCargaHoraria && chFinal != null && chFinal > 0 ? chFinal : null),
					(filtroStatus && status > 0 ? status : 0));
		} finally {
			if (dao != null)
				dao.close();
		}		
		
		return forward(getListaSemIndicacao());
	}
	
	/**
	 * Imprime o resultado da busca
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/plano_docencia_assistida/lista_sem_indicacoes.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */	
	public String imprimirBusca() throws DAOException{
		if (isEmpty(planosSemIndicacao)){
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		if (filtroUnidade && unidade != null && unidade.getId() > 0)
			unidade = getGenericDAO().refresh(unidade);
		
		if (filtroModalidade && modalidadeBolsa != null && modalidadeBolsa.getId() > 0)
			modalidadeBolsa = getGenericDAO().refresh(modalidadeBolsa);
		
		return forward(getPrintBusca());		
	}
	
	/**
	 * Chama Visualização dos dados cadastrados.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/plano_docencia_assistida/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String viewImpressao() throws DAOException{
		relatorioSemestral = getParameterBoolean("relatorioSemestral");
		
		initObj();		
		populateObj();

		
		if (isEmpty(obj)){
			addMensagemWarning("Não existe nenhum Plano de Docência Assistida Selecionado");
			return null;
		}
		
		if (relatorioSemestral){
			TurmaDao dao = getDAO(TurmaDao.class);
			try{
				for (TurmaDocenciaAssistida turma : obj.getTurmaDocenciaAssistida()){
					Map<String, Object> quant = dao.findQuantitativoSituacaoTurma(turma.getTurma().getId());
					if (!quant.isEmpty()){
						turma.getTurma().setQtdAprovados(((Number) quant.get("aprovados")).longValue());
						turma.getTurma().setQtdMatriculados(((Number) quant.get("matriculados")).longValue());
						turma.getTurma().setQtdReprovados(((Number) quant.get("reprovado_nota")).longValue());
						turma.getTurma().setQtdReprovadosFalta(((Number) quant.get("reprovado_falta")).longValue());
						turma.getTurma().setQtdTrancados(((Number) quant.get("trancados")).longValue());
					}
				}				
			} finally {
				if (dao != null)
					dao.close();
			}
		}
		
		/** setando orientador no mbean */
		orientacao = DiscenteHelper.getUltimoOrientador(obj.getDiscente().getDiscente());			
		return forward(getViewPrintPage());
	}
	
	/**
	 * Chama Visualização do Relatório Semestral.<br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *  <li>/sigaa.war/stricto/plano_docencia_assistida/view.jsp</li>
	 * </ul>
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws SegurancaException 
	 */
	public String emitirRelatorioSemestral() throws SegurancaException, ArqException, NegocioException{
		// verifica se o tamanha do arquivo é maior que o permitido.
		if (arquivo != null && arquivo.getSize() > tamanhoMaxArquivo * TamanhoArquivo.MEGA_BYTE){
			addMensagemErro("O arquivo deve ter tamanho menor ou igual a " + tamanhoMaxArquivo + " MB.");
			return null;				
		}	
		
		boolean turmasConsolidadas = true;
		for (TurmaDocenciaAssistida turma : obj.getTurmaDocenciaAssistida()){
			if (!turma.getTurma().isConsolidada()){					
				turmasConsolidadas = false;
				break;
			}
		}				
		
		if (!turmasConsolidadas){
			addMensagemWarning("Todas as Turmas devem estar consolidadas para Emitir o Relatório Semestral.");
			return null;
		}		
		
		obj.setStatus(PlanoDocenciaAssistida.ANALISE_RELATORIO);
		salvarRelatorioSemestral();
		return viewImpressao();
	}	
	
	/**
	 * Salva o Relatório Semestral.<br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *  <li>/sigaa.war/stricto/plano_docencia_assistida/view.jsp</li>
	 * </ul>
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws SegurancaException 
	 */	
	public String salvarRelatorioSemestral() throws SegurancaException, ArqException, NegocioException{		
		// verifica se o tamanha do arquivo é maior que o permitido.
		if (arquivo != null && arquivo.getSize() > tamanhoMaxArquivo * TamanhoArquivo.MEGA_BYTE){
			addMensagemErro("O arquivo deve ter tamanho menor ou igual a " + tamanhoMaxArquivo + " MB.");
			return null;				
		}	
		
		relatorioSemestral = true;
		return cadastrar();
	}
	
	/**
	 * Redireciona para o formulário de preenchimento do relatório Semestral.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *  <li>/sigaa.war/stricto/plano_docencia_assistida/include_lista_plano_docencia.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String preencherRelatorioSemestral() throws ArqException{
	
		comando = SigaaListaComando.ALTERAR_PLANO_DOCENCIA_ASSISTIDA;
		prepareMovimento(comando);
		setOperacaoAtiva(SigaaListaComando.ALTERAR_PLANO_DOCENCIA_ASSISTIDA.getId());
		
		// Atribui o tamanho máximo do arquivo a ser anexado pelo discente. 
		tamanhoMaxArquivo = ParametroHelper.getInstance().getParametroInt(ParametrosTurmaVirtual.TAMANHO_MAXIMO_PADRAO_ARQUIVO_ALUNO);
		// se não existir valor definido atribui 5MB
		if (tamanhoMaxArquivo <= 0)
			tamanhoMaxArquivo = 5; 		
		
		relatorioSemestral = true;
		initObj();
		populateObj();
		
		atividadesNaoPrevistas = new ArrayList<AtividadeDocenciaAssistida>();
		for (AtividadeDocenciaAssistida atividade : obj.getAtividadeDocenciaAssistida()){
			
			if (!atividade.isPrevista())
				atividadesNaoPrevistas.add(atividade);
			
		}
		
		return forward(getViewPage());
	}
	
	/** 
	 * Redireciona para o formulário de cadastro.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/plano_docencia_assistida/view.jsp</li>
	 * </ul>	
	 */
	public String redirectForm(){
		return forward(getFormPage());
	}
	
	/**
	 * Redireciona para a confirmação dos dados do cadastro.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/plano_docencia_assistida/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String confirmar() throws DAOException{
		
		if (!checkOperacaoAtiva(comando.getId()))
			return super.cancelar();
		
		if (obj.getModalidadeBolsa() != null && obj.getModalidadeBolsa().getId() > 0) 
			obj.setModalidadeBolsa(getGenericDAO().refresh(obj.getModalidadeBolsa()));
		else 			
			obj.setModalidadeBolsa(new ModalidadeBolsaExterna());
		
		if (obj.getComponenteCurricular().getNome().isEmpty()){
			obj.setComponenteCurricular(new ComponenteCurricular());
		}
		
		if (obj.getCurso().getId() <= 0){
			obj.setCurso(new Curso());
		}		
		
		if (!validarCargaHoraria())
			return redirectForm();
		
		obj.setBolsista(bolsista);
		
		return forward(getViewPage());
	}

	/**
	 * Valida a carga horária total do plano
	 * @throws DAOException
	 */
	private boolean validarCargaHoraria() throws DAOException {
		if (obj.getModalidadeBolsa() != null && obj.getModalidadeBolsa().getId() > 0) {
			
			obj.setModalidadeBolsa(getGenericDAO().refresh(obj.getModalidadeBolsa()));
			
			int chMax = obj.getModalidadeBolsa().getChMax();
			int chMin = obj.getModalidadeBolsa().getChMin();

			if (ValidatorUtil.isNotEmpty(obj.getAtividadeDocenciaAssistida())){
				
				float chTotal = 0;
				for (AtividadeDocenciaAssistida atividade : obj.getAtividadeDocenciaAssistida()){
					if (atividade.getCh() != null && atividade.getCh() > 0){
						if (atividade.getFrequenciaAtividade().getQuantSemanas() > 0)
							chTotal += ((float) atividade.getCh() / (float) atividade.getFrequenciaAtividade().getQuantSemanas());
						else
							chTotal += atividade.getCh();
					}
				}
				
				if (chMin > 0 && chTotal < chMin){
					addMensagemErro("A carga horária total das atividades não pode ser inferior à "+chMin+" horas semanais.");
					return false;
				}
				
				if (chMax > 0 && chTotal > chMax){
					addMensagemWarning("A carga horária total das atividades é superior à carga horária máxima, que é de "+chMax+" horas semanais. " +
					"Caso confirme assim mesmo, estará ciente que sua carga horária total é superior que a máxima permitida.");
				}
			}
			
		}
		return true;
		
	}
	
	/**
	 * Redireciona para a tela de Análise do Plano.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/plano_docencia_assistida/include_acoes.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String analisarPlano() throws ArqException{
		checkRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG);
		
		int tipo = getParameterInt("tipo", 0);
		int id = getParameterInt("idPlano", 0);
		
		obj = getGenericDAO().findByPrimaryKey(id, PlanoDocenciaAssistida.class);
		
		if (isEmpty(obj) || tipo == 0){
			addMensagemWarning("Não existe nenhum Plano de Docência Assistida Selecionado");
			return null;
		}
		
		/* Carrega as atividades */
		if (ValidatorUtil.isNotEmpty(obj.getAtividadeDocenciaAssistida()))
			obj.getAtividadeDocenciaAssistida().iterator();		
		
		/* Recarrega as turmas e os seus docentes, para evitar lazy.. */
		obj.getTurmaDocenciaAssistida().iterator();
		for (TurmaDocenciaAssistida turma : obj.getTurmaDocenciaAssistida()){
			turma.getTurma();
			turma.getTurma().getDocentesTurmas();
			turma.getTurma().getDocentesTurmas().iterator();
		}
		
		comando = SigaaListaComando.ANALISAR_PLANO_DOCENCIA_ASSISTIDA;
		prepareMovimento(comando);
		setOperacaoAtiva(SigaaListaComando.ANALISAR_PLANO_DOCENCIA_ASSISTIDA.getId());
		
		obj.setObservacao(new String());
		novoStatus = 0;
		
		if (tipo == ANALISE_PLANO)
			statusAnalise = getStatusAnalisePlano();
		else if (tipo == ANALISE_RELATORIO)
			statusAnalise = getStatusAnaliseRelatorio();				
		
		orientacao = DiscenteHelper.getUltimoOrientador(obj.getDiscente().getDiscente());	
		
		return forward(getFormObs());		
	}	
	
	/**
	 * Visualiza o Histórico de Movimentação do Plano.
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/plano_docencia_assistida/include_acoes.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 * @throws DAOException 
	 */
	public String visualizarHistorico() throws SegurancaException, DAOException{
		checkRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG , SigaaPapeis.SECRETARIA_POS);
		populateObj();
		
		if (isEmpty(obj)){
			addMensagemWarning("Não existe nenhum Plano de Docência Assistida Selecionado");
			return null;
		}
		
		orientacao = DiscenteHelper.getUltimoOrientador(obj.getDiscente().getDiscente());		
		
		Collections.sort( obj.getHistoricoMovimentacoes() , new Comparator<HistoricoPlanoDocenciaAssistida>(){
			public int compare(HistoricoPlanoDocenciaAssistida arg0,	HistoricoPlanoDocenciaAssistida arg1) {
				return arg0.getData().getTime() > arg1.getData().getTime() ? 1 : -1;  
			}
		});																
		
				
		return forward(getViewHistorico());				
	}
	
	/**
	 * Cadastra o Plano de Docência Assistida
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/plano_docencia_assistida/form_observacao.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		
		if (!checkOperacaoAtiva(comando.getId()))
			return super.cancelar();		
		
		if (comando.getId() == SigaaListaComando.ANALISAR_PLANO_DOCENCIA_ASSISTIDA.getId()){
			if (novoStatus <= 0){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Situação");
			}			
			if (obj.getObservacao().trim().isEmpty()){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Observação");
			}
			
			if (novoStatus == PlanoDocenciaAssistida.APROVADO){
				PlanoDocenciaAssistidaDao dao = getDAO(PlanoDocenciaAssistidaDao.class);
				try{
					// verifica se o discente já possui algum plano de docência assistida submetido no ano e período selecionado.			
					List<PlanoDocenciaAssistida> planos = dao.findByPeriodoSituacao(obj.getDiscente(), null, 
							obj.getAno(), obj.getPeriodo(), true, PlanoDocenciaAssistida.APROVADO);
					if (!isEmpty(planos)){
						addMensagemErro("Não é possível aprovar este plano de docência assistida, pois já existe um aprovado para o mesmo discente no período vigente.");
					}										
				} finally {
					if (dao != null)
						dao.close();
				}					
			}
			
			if (hasErrors())
				return null;
			
			obj.setStatus(novoStatus);
		}
		
		try {		
			/* Carrega as atividades */
			if (ValidatorUtil.isNotEmpty(obj.getAtividadeDocenciaAssistida()))
				obj.getAtividadeDocenciaAssistida().iterator();			
			
			/* Só Valida se submeter ou for a PPG alterando */
			if (obj.getStatus() == PlanoDocenciaAssistida.SUBMETIDO || ( isPortalPpg() && comando.equals(SigaaListaComando.ALTERAR_PLANO_DOCENCIA_ASSISTIDA))){
				erros = new ListaMensagens();
				erros.addAll(obj.validate().getMensagens());
				
				if (!isEmpty(obj.getComponenteCurricular()) && !isEmpty(turmas) && isEmpty(obj.getTurmaDocenciaAssistida()))
					addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Turma");
				
				if (hasErrors())
					return redirectForm();						
			}
			
			if (isRelatorioSemestral())
				obj.getAtividadeDocenciaAssistida().addAll(atividadesNaoPrevistas);
			
			// Prepara o movimento, setando o objeto
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(comando);
			if (relatorioSemestral)
				mov.setObjAuxiliar(arquivo);
			execute(mov);
							
			//limpa a lista de indicações para atualizar com os dados alterados
			if (!isEmpty(listaIndicacoes))
				listaIndicacoes = new ArrayList<IndicacaoBolsistaReuni>();
			if (!isEmpty(planosSemIndicacao))
				planosSemIndicacao = new ArrayList<PlanoDocenciaAssistida>();		
			// Se chegou aqui, não houve erros. 
			if (!relatorioSemestral || obj.getStatus() != PlanoDocenciaAssistida.CONCLUIDO)
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO, TipoMensagemUFRN.INFORMATION);			
			
			 if ( isPortalPpg() || isPortalCoordenadorStricto())
				 return buscar();
			 
			return cancelar();									
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}				
	}
	
	/**
	 * Submete o Plano de Docência, não permitindo alterar posteriormente.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/plano_docencia_assistida/form.jsp</li>
	 * </ul>	
	*/
	public String submeter() throws SegurancaException, ArqException, NegocioException{
		if (isEmpty(orientacao)){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Orientador");		
			return redirectForm();
		}
		
		if (!validarCargaHoraria())
			return redirectForm(); 
		
		if (hasOnlyErrors())
			return null;
		
		obj.setStatus(PlanoDocenciaAssistida.SUBMETIDO);
		return cadastrar();				
	}
	
	/**
	 * Salva o Plano de Docência, permitindo alterar posteriormente.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/plano_docencia_assistida/form.jsp</li>
	 * </ul>	
	*/
	public String salvar() throws SegurancaException, ArqException, NegocioException{
		if (isPortalDiscente() && obj.getStatus() != PlanoDocenciaAssistida.SOLICITADO_ALTERACAO)
			obj.setStatus(PlanoDocenciaAssistida.CADASTRADO);
		
		return cadastrar();
	}
	
	/**
	 * Seleciona o Departamento escolhido pelo o usuário.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/plano_docencia_assistida/form.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void selecionarComponente(ActionEvent e) throws DAOException {
		ComponenteCurricular cc = (ComponenteCurricular) e.getComponent().getAttributes().get("componente");
		selecionarComponente(cc);
	}
	
	/**
	 * Seleciona o Componente Curricular escolhido pelo o usuário.<br/>
	 * @param e
	 * @throws DAOException
	 */
	private void selecionarComponente(ComponenteCurricular componente) throws DAOException {
		if (!isEmpty(componente)) {
			
			this.componente =  getGenericDAO().refresh(componente);
			obj.setComponenteCurricular( this.componente );
			
			if (ValidatorUtil.isNotEmpty(obj.getTurmaDocenciaAssistida()) && obj.getTurmaDocenciaAssistida().size() > 0){			
				for (TurmaDocenciaAssistida turma : obj.getTurmaDocenciaAssistida()){
					if (!isEmpty(turma))
						getGenericDAO().remove(turma);					
				}
				obj.setTurmaDocenciaAssistida(new ArrayList<TurmaDocenciaAssistida>());
			} 
			
			if (!obj.getComponenteCurricular().isAtividade() && getUltimoComando() != null && 
					( getUltimoComando().equals(SigaaListaComando.CADASTRAR_PLANO_DOCENCIA_ASSISTIDA) || 
					getUltimoComando().equals(SigaaListaComando.ALTERAR_PLANO_DOCENCIA_ASSISTIDA)))
				carregaTurmas();
			
		} 
	}	
	
	/**
	 * Retorna o curso escolhido pelo usuário e retorna as turmas.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/plano_docencia_assistida/form.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void selecionarCurso(ActionEvent e) throws DAOException {
		if (!isEmpty(obj.getCurso())) {
			obj.setCurso(getGenericDAO().refresh(obj.getCurso()));
			carregaTurmas();
		}
	}	
	
	/**
	 * Atribui as datas das turmas selecionadas.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/plano_docencia_assistida/form.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void selecionarTurma(ActionEvent e) throws DAOException {
		if (!isEmpty(turmaDocencia.getTurma())) {
			turmaSelecionada = getGenericDAO().refresh(turmaDocencia.getTurma());			
			turmaDocencia.setDataInicio( turmaSelecionada.getDataInicio() );
			turmaDocencia.setDataFim( turmaSelecionada.getDataFim() );
		}
	}	
	
	/**
	 * Carrega as turmas para o combo.
	 * @throws DAOException 
	 */
	private void carregaTurmas() throws DAOException{
		TurmaDao dao = getDAO(TurmaDao.class);
		try {
			// Limpa o combo das turmas
			comboTurmas = new ArrayList<SelectItem>();
			// Traz todas as turmas do componente curricular selecionado e do ano e período da indicação.
			int ano = 0;
			int periodo = 0;
			if (!isEmpty(obj.getPeriodoIndicacaoBolsa())){
				ano = obj.getPeriodoIndicacaoBolsa().getAno();
				periodo = obj.getPeriodoIndicacaoBolsa().getPeriodo();
			} else {
				ano = obj.getAno();
				periodo = obj.getPeriodo();
			}
			turmas = dao.findByDisciplinaAnoPeriodoCurso(obj.getComponenteCurricular(), ano, periodo, 0, NivelEnsino.GRADUACAO, obj.getCurso().getId());
			// popula o selectItem com as turmas encontradas.
			for(  Turma turma : turmas ){
				if (turma.isAberta()){ //Não exibir as turmas consolidadas
					SelectItem si = new SelectItem();
					si.setLabel( turma.getDescricao() );
					si.setValue( turma.getId() );
					comboTurmas.add(si);
				}
			}				
		} finally {
			if (dao != null)
				dao.close();
		}		
	}	
	

	/**
	 * Adiciona uma turma selecionada na lista de turmas.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/plano_docencia_assistida/form.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException 
	 */	
	public void addTurma(ActionEvent e){
		ListaMensagens erros = new ListaMensagens(); 
		 if (turmaDocencia.getTurma().getId() == 0){
			 erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Turma");
 		 }
		 
		 if (turmaDocencia.getDataInicio() != null && turmaDocencia.getDataFim() != null){
			 if (CalendarUtils.estorouPrazo(turmaDocencia.getDataFim(), turmaDocencia.getDataInicio())){
				 erros.addMensagem(MensagensArquitetura.DATA_INICIO_MENOR_FIM, "Data da Turma");
			 }
		 } else {
			 if (turmaDocencia.getDataInicio() == null){
				 erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data de Início");
			 }
			 
			 if (turmaDocencia.getDataFim() == null){
				 erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data de Fim");
			 }
		 }	
		 CalendarioAcademico cal = getCalendarioVigente();
		 if (cal.getInicioPeriodoLetivo() == null || cal.getFimPeriodoLetivo() == null){
			 erros.addWarning("O Calendário Acadêmico no Período Letivo atual, não foi definido para no seu Programa.");
		 } else {
			 DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			 if (!CalendarUtils.isDentroPeriodo(cal.getInicioPeriodoLetivo(), cal.getFimPeriodoLetivo(), turmaDocencia.getDataInicio())){			 
				 erros.addWarning("A data de Início deve está dentro do período vigente ("+
						 df.format(cal.getInicioPeriodoLetivo())+" à "+df.format(cal.getFimPeriodoLetivo())+")");
			 }
			 
			 if (!CalendarUtils.isDentroPeriodo(cal.getInicioPeriodoLetivo(), cal.getFimPeriodoLetivo(), turmaDocencia.getDataFim())){
				 erros.addWarning("A data de Fim deve está dentro do período da vigente ("+
						 df.format(cal.getInicioPeriodoLetivo())+" à "+df.format(cal.getFimPeriodoLetivo())+")");
			 }		 			 
		 }
 							 
		 boolean turmaEncontrada = false;
		 boolean componenteDiferente = false;
		 for (TurmaDocenciaAssistida turma : obj.getTurmaDocenciaAssistida()){
			 if (turma.getTurma().getId() == turmaDocencia.getTurma().getId()){
				 turmaEncontrada = true;
				 break;
			 }
			 
			 if (turma.getComponenteCurricular().getId() != obj.getComponenteCurricular().getId()){
				 componenteDiferente = true;
				 break;
			 }			 
		 }
		 
		 if (turmaEncontrada){
			 erros.addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Turma");
		 }		
		
		 if (componenteDiferente){
			 erros.addWarning("Não é possível incluir turmas de Componentes Curriculares diferentes.");
		 }		
		 
		 if (!erros.isEmpty()){
			 addMensagensAjax(erros);
			 return;
		 }
		 
		 for (Turma turma : turmas){
			 if (turmaDocencia.getTurma().getId() == turma.getId()){
				 turmaDocencia.setTurma(turma);
				 break;
			 }
			 
		 }		 
		 		
		turmaDocencia.setPlanoDocenciaAssistida(obj);		
		turmaDocencia.setCurso(obj.getCurso());
		turmaDocencia.setComponenteCurricular(obj.getComponenteCurricular());
		
		obj.getTurmaDocenciaAssistida().add(turmaDocencia);
		
		turmaDocencia = new TurmaDocenciaAssistida();	
		turmaDocencia.setTurma(new Turma());
	}
	
	/**
	 * Remove uma turma <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/plano_docencia_assistida/form.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void removerTurma(ActionEvent e) throws DAOException {
		int index = getParameterInt("indice",-1);	
		if ( index >= 0 ) {
			TurmaDocenciaAssistida turmaDocencia = obj.getTurmaDocenciaAssistida().remove(index);									
			if (turmaDocencia.getId() > 0)
				getGenericDAO().remove(turmaDocencia);
		}
	}		
	
	/**
	 * Adiciona um atividade selecionada na lista de atividades (Formas de Atuação).<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/plano_docencia_assistida/form.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException 
	 */
	public void addAtividade(ActionEvent e) throws DAOException{
		ListaMensagens erros = new ListaMensagens(); 
		
		 if (atividade.getFormaAtuacao().getId() < 0 || (atividade.getFormaAtuacao().getId() == 0 && atividade.getOutraAtividade().trim().isEmpty())){
			 erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Atividade");
		 }			 
		 
		 if (atividade.getDataInicio() == null){
			 erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data de Início da Atividade");
		 }
		 
		 if (atividade.getDataFim() == null){
			 erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data de Fim da Atividade");
		 }
		 	 
		 if (atividade.getDataInicio() != null && atividade.getDataFim() != null){
			 if (CalendarUtils.estorouPrazo(atividade.getDataFim(), atividade.getDataInicio())){
				 erros.addMensagem(MensagensArquitetura.DATA_INICIO_MENOR_FIM, "Data");
			 }			 
		 }
		 
		 CalendarioAcademico cal = getCalendarioVigente();
		 if (cal.getInicioPeriodoLetivo() == null || cal.getFimPeriodoLetivo() == null){
			 erros.addErro("O Calendário Acadêmico no Período Letivo atual, não foi definido para no seu Programa.");
		 } else {		 
			 DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			 if (!CalendarUtils.isDentroPeriodo(cal.getInicioPeriodoLetivo(), cal.getFimPeriodoLetivo(), atividade.getDataInicio())){			 
				 erros.addErro("A data de Início deve está dentro do período vigente ("+
						 df.format(cal.getInicioPeriodoLetivo())+" à "+df.format(cal.getFimPeriodoLetivo())+")");
			 }
			 
			 if (!CalendarUtils.isDentroPeriodo(cal.getInicioPeriodoLetivo(), cal.getFimPeriodoLetivo(), atividade.getDataFim())){
				 erros.addErro("A data de Fim deve está dentro do período da vigente ("+
						 df.format(cal.getInicioPeriodoLetivo())+" à "+df.format(cal.getFimPeriodoLetivo())+")");
			 }		
		 }
		 
		 if (ValidatorUtil.isEmpty(atividade.getFrequenciaAtividade())){
			 erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Frequência da Atividade");					
		 }	
		 
		 if (atividade.getCh() == null)
			 erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Carga Horária");					
		 
		 if (atividade.getCh() != null && atividade.getCh() <= 0)
			 erros.addMensagem(MensagensArquitetura.VALOR_MAIOR_ZERO, "Carga Horária");					
		 
		 atividade.setPrevista(true);
		 
		 if (isRelatorioSemestral()){		
			 if (atividade.getPercentualRealizado() == 0)
				 erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Realização");
			 
			 if (atividade.getResultadosObtidos().isEmpty())
				 erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Resultados Obtidos");
			 
			 if (atividade.getDificuldades().isEmpty())
				 erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Dificuldades Encontradas");			 
			 	
			 atividade.setPrevista(false);		 
		 }
		 
		 if (atividade.getComoOrganizar().isEmpty()){
			 erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Metodologias da Atividade");
		 }
		 
		 if (atividade.getProcedimentos().isEmpty()){
			 erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Como Avaliar a Atividade e Metodologias Empregadas");
		 }			 
		 		 
		 if (!erros.isEmpty()){
			 addMensagensAjax(erros);
			 return;
		 }
		 
		 if (atividade.getFormaAtuacao().getId() > 0){
		 	 atividade.setFormaAtuacao(getGenericDAO().refresh(atividade.getFormaAtuacao()));
		 	 atividade.setOutraAtividade(null);
		 } else {
			 atividade.setFormaAtuacao(null);
		 }
		 
		 if (atividade.getFrequenciaAtividade().getId() > 0)
		 	 atividade.setFrequenciaAtividade(getGenericDAO().refresh(atividade.getFrequenciaAtividade()));
		 else
			 atividade.setFrequenciaAtividade(null);

		atividade.setPlanoDocenciaAssistida(obj);
		
		if (isRelatorioSemestral())
			atividadesNaoPrevistas.add(atividade);
		else if (!atividade.isAlteracao())		
			obj.getAtividadeDocenciaAssistida().add(atividade);			

		if (atividade.isAlteracao()){
			for (Iterator<AtividadeDocenciaAssistida> iterator = obj.getAtividadeDocenciaAssistida().iterator(); iterator.hasNext();) {
				AtividadeDocenciaAssistida ada = iterator.next();
				if (atividade.getId() == ada.getId())
					iterator.remove();
			}
			obj.getAtividadeDocenciaAssistida().add(atividade);	
		}
		
		atividade.setAlteracao(false);
				
		this.atividade = new AtividadeDocenciaAssistida();
		this.atividade.setPlanoDocenciaAssistida(new PlanoDocenciaAssistida());
		this.atividade.setFormaAtuacao(new FormaAtuacaoDocenciaAssistida());
		this.atividade.setFrequenciaAtividade(new FrequenciaAtividadeDocenciaAssistida());
		this.atividade.getFormaAtuacao().setId(-1);
		this.atividade.setAlteracao(false);
	}
	
	/**
	 * Remove uma atividade <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/plano_docencia_assistida/form.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void removerAtividade(ActionEvent e) throws DAOException {
		int index = getParameterInt("indice",-1);	
		if ( index >= 0 ) {
			AtividadeDocenciaAssistida atividade;
			if (isRelatorioSemestral())
				atividade = atividadesNaoPrevistas.remove(index);
			else
				atividade = obj.getAtividadeDocenciaAssistida().remove(index);
			if (atividade.getId() > 0)
				getGenericDAO().remove(atividade);
		}
	}	
	
	/**
	 * Alterar uma atividade <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/plano_docencia_assistida/form.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void alterarAtividade(ActionEvent e) throws DAOException {
		int index = getParameterInt("indice",-1);
		int id = getParameterInt("id",0);
		if ( index >= 0 && id > 0 ){
			
			if (isRelatorioSemestral())
				atividade = atividadesNaoPrevistas.get(index);
			else{ 
				atividade = getGenericDAO().findByPrimaryKey(id, AtividadeDocenciaAssistida.class);
			}	
			atividade.setAlteracao(true);
						
			if (isEmpty(atividade.getFormaAtuacao())){
				atividade.setFormaAtuacao(new FormaAtuacaoDocenciaAssistida());
				atividade.getFormaAtuacao().setId(0);
			}			
			
			if (isEmpty(atividade.getFrequenciaAtividade()))
				atividade.setFrequenciaAtividade(new FrequenciaAtividadeDocenciaAssistida());
		}
	}		
	
	/**
	 * Retorna as formas de atuação cadastradas.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/plano_docencia_assistida/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getFormasAtuacaoCombo() throws DAOException {
		if (formasAtuacao == null) {
			formasAtuacao = getGenericDAO().findAllAtivos(FormaAtuacaoDocenciaAssistida.class, "descricao");
		}
		return toSelectItems(formasAtuacao, "id", "descricao");
	}	
	
	@Override
	public String getFormPage() {
		return "/stricto/plano_docencia_assistida/form.jsf";
	}

	public String getViewPage(){
		return "/stricto/plano_docencia_assistida/view.jsf";
	}
	
	public String getListPage(){
		return "/stricto/plano_docencia_assistida/lista.jsf";
	}	
	
	public String getListaSemIndicacao(){
		return "/stricto/plano_docencia_assistida/lista_sem_indicacoes.jsf";
	}	
	
	public String getViewPrintPage(){
		return "/stricto/plano_docencia_assistida/view_impressao.jsf";
	}
	
	public String getFormObs(){
		return "/stricto/plano_docencia_assistida/form_observacao.jsf";
	}
	
	public String getViewHistorico(){
		return "/stricto/plano_docencia_assistida/view_historico.jsf";
	}
	
	public String getPrintBusca(){
		return "/stricto/plano_docencia_assistida/print_busca.jsf";
	}		
	
	/**
	 * Cancela a operação.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/plano_docencia_assistida/form.jsp</li>
	 *  <li>/sigaa.war/stricto/plano_docencia_assistida/form_observacao.jsp</li>
	 *  <li>/sigaa.war/stricto/plano_docencia_assistida/view.jsp</li>
	 * </ul>
	 */
	public String cancelarGeral() {
		try {
			
			resetBean();
			
			obj = new PlanoDocenciaAssistida();
			initObj();		
			
			if (!isPortalDiscente()){				
				return buscar();
			} else 
				return iniciar();
			
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Retorna todas as modalidades de bolsa para o combo.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/plano_docencia_assistida/form.jsp</li>
	 * </ul>
	 * @return 
	 */
	public Collection<SelectItem> getAllModalidadeBolsaCombo() throws DAOException{
		return toSelectItems(getGenericDAO().findAll(ModalidadeBolsaExterna.class), "id", "descricao");
	}
	
	/**
	 * Retorna todas as frequências de atividades para o combo.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/plano_docencia_assistida/form.jsp</li>
	 * </ul>
	 * @return 
	 */
	public Collection<SelectItem> getAllFrequenciaAtividadeCombo() throws DAOException{
		return toSelectItems(getGenericDAO().findAll(FrequenciaAtividadeDocenciaAssistida.class), "id", "descricao");
	}	
	
	/**
	 * Retorna um combo com os Status do Plano;
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/stricto/plano_docencia_assistida/lista_sem_indicacao.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getAllStatus(){
		return toSelectItems(PlanoDocenciaAssistida.DESCRICOES_STATUS);
	}		
	
	/**
	 * Retorna um combo com os Status que o pode Analisar;
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/stricto/plano_docencia_assistida/form_observacao.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getStatusAnalisePlanoCombo(){
		return toSelectItems(statusAnalise);
	}

	/**
	 * Status de análise do plano
	 * @return
	 */
	private Map<Integer, String> getStatusAnalisePlano() {
		Map<Integer, String> status = new HashMap<Integer, String>();
	
		if (isPortalPpg()){
			status.put(PlanoDocenciaAssistida.SUBMETIDO, PlanoDocenciaAssistida.getDescricaoStatus(PlanoDocenciaAssistida.SUBMETIDO));
			status.put(PlanoDocenciaAssistida.CANCELADO, PlanoDocenciaAssistida.getDescricaoStatus(PlanoDocenciaAssistida.CANCELADO));
		}
		
		status.put(PlanoDocenciaAssistida.APROVADO, PlanoDocenciaAssistida.getDescricaoStatus(PlanoDocenciaAssistida.APROVADO));
		status.put(PlanoDocenciaAssistida.SOLICITADO_ALTERACAO, PlanoDocenciaAssistida.getDescricaoStatus(PlanoDocenciaAssistida.SOLICITADO_ALTERACAO));
		status.put(PlanoDocenciaAssistida.REPROVADO, PlanoDocenciaAssistida.getDescricaoStatus(PlanoDocenciaAssistida.REPROVADO));						

		return status;
	}		
	
	/**
	 * Status de análise do relatório semestral
	 * @return
	 */
	private Map<Integer, String> getStatusAnaliseRelatorio() {
		Map<Integer, String> status = new HashMap<Integer, String>();
	
		if (!obj.isConcluido())
			status.put(PlanoDocenciaAssistida.CONCLUIDO, PlanoDocenciaAssistida.getDescricaoStatus(PlanoDocenciaAssistida.CONCLUIDO));
		
		status.put(PlanoDocenciaAssistida.ANALISE_RELATORIO, PlanoDocenciaAssistida.getDescricaoStatus(PlanoDocenciaAssistida.ANALISE_RELATORIO));
		status.put(PlanoDocenciaAssistida.SOLICITADO_ALTERACAO_RELATORIO, PlanoDocenciaAssistida.getDescricaoStatus(PlanoDocenciaAssistida.SOLICITADO_ALTERACAO_RELATORIO));
			
	
		return status;
	}		
	
	/**
	 * Retorna um combo com percentuais de 0 a 100;
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/stricto/plano_docencia_assistida/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getPercentuaisCombo(){
		List<SelectItem> percentuais = new ArrayList<SelectItem>();
		for (int i = 0; i <= 100; i+=10) {
			percentuais.add(new SelectItem(String.valueOf(i), String.valueOf(i)));
		}		
		return percentuais;
	}	

	public List<SelectItem> getComboTurmas() {
		return comboTurmas;
	}

	public void setComboTurmas(List<SelectItem> comboTurmas) {
		this.comboTurmas = comboTurmas;
	}

	public AtividadeDocenciaAssistida getAtividade() {
		return atividade;
	}

	public void setAtividade(AtividadeDocenciaAssistida atividade) {
		this.atividade = atividade;
	}

	public TurmaDocenciaAssistida getTurmaDocencia() {
		return turmaDocencia;
	}

	public void setTurmaDocencia(TurmaDocenciaAssistida turmaDocencia) {
		this.turmaDocencia = turmaDocencia;
	}

	public Collection<Turma> getTurmas() {
		return turmas;
	}

	public void setTurmas(Collection<Turma> turmas) {
		this.turmas = turmas;
	}

	/**
	 * Retorna a orientação acadêmica do discente do plano atual
	 * <br><br>
	 * Não invocado por JSP
	 * @return
	 * @throws DAOException
	 */
	public OrientacaoAcademica getOrientacao() throws DAOException {
		
		if (ValidatorUtil.isEmpty(orientacao)){
		
			if (isPortalDiscente())			
				orientacao = DiscenteHelper.getUltimoOrientador(getDiscenteUsuario().getDiscente());
			else if (!isEmpty(obj.getDiscente()))
				orientacao = DiscenteHelper.getUltimoOrientador(obj.getDiscente().getDiscente());
			
			obj.getDiscente().setOrientacao(orientacao);
		
		}
		
		return orientacao;
	}

	public void setOrientacao(OrientacaoAcademica orientacao) {
		this.orientacao = orientacao;
	}

	public boolean isCadastrar() {
		return cadastrar;
	}

	public void setCadastrar(boolean cadastrar) {
		this.cadastrar = cadastrar;
	}

	public PlanoTrabalhoReuni getPlanoTrabalhoReuni() {
		return planoTrabalhoReuni;
	}

	public void setPlanoTrabalhoReuni(PlanoTrabalhoReuni planoTrabalhoReuni) {
		this.planoTrabalhoReuni = planoTrabalhoReuni;
	}

	public PeriodoIndicacaoReuni getPeriodoIndicacao() {
		return periodoIndicacao;
	}

	public void setPeriodoIndicacao(PeriodoIndicacaoReuni periodoIndicacao) {
		this.periodoIndicacao = periodoIndicacao;
	}

	public Collection<IndicacaoBolsistaReuni> getListaIndicacoes() {
		return listaIndicacoes;
	}

	public void setListaIndicacoes(
			Collection<IndicacaoBolsistaReuni> listaIndicacoes) {
		this.listaIndicacoes = listaIndicacoes;
	}

	public Turma getTurmaSelecionada() {
		return turmaSelecionada;
	}

	public void setTurmaSelecionada(Turma turmaSelecionada) {
		this.turmaSelecionada = turmaSelecionada;
	}

	public boolean isRelatorioSemestral() {
		return relatorioSemestral;
	}

	public void setRelatorioSemestral(boolean relatorioSemestral) {
		this.relatorioSemestral = relatorioSemestral;
	}

	/**
	 * Retorna a lista de Plano de Docência Assistida dos Alunos que não possui indicações.
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/stricto/plano_docencia_assistida/include_lista_plano_docencia.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public Collection<PlanoDocenciaAssistida> getPlanosSemIndicacao() throws DAOException {
		if (isEmpty(planosSemIndicacao)){
			PlanoDocenciaAssistidaDao dao = getDAO(PlanoDocenciaAssistidaDao.class);
			try {
				if (isPortalDiscente())
					planosSemIndicacao = dao.findByPeriodoSituacao(getDiscenteUsuario(), null, null, null, false);	
				
				if (!isEmpty(planosSemIndicacao)){
					Collections.sort( (List<PlanoDocenciaAssistida>) planosSemIndicacao, new Comparator<PlanoDocenciaAssistida>(){
						public int compare(PlanoDocenciaAssistida p1,	PlanoDocenciaAssistida p2) {						
							int status = p1.getDescricaoStatus().compareToIgnoreCase(p2.getDescricaoStatus());						
							return (status == 0 ? p1.getDiscente().getNome().compareToIgnoreCase(p2.getDiscente().getNome()) : status); 					
						}
					});								
				}				
			} finally {
				if (dao != null)
					dao.close();
			}
		}					
		
		return planosSemIndicacao;
	}
	
	public void setPlanosSemIndicacao(
			Collection<PlanoDocenciaAssistida> planosSemIndicacao) {
		this.planosSemIndicacao = planosSemIndicacao;
	}

	public boolean isFiltroUnidade() {
		return filtroUnidade;
	}

	public void setFiltroUnidade(boolean filtroUnidade) {
		this.filtroUnidade = filtroUnidade;
	}

	public boolean isFiltroAnoPeriodo() {
		return filtroAnoPeriodo;
	}

	public void setFiltroAnoPeriodo(boolean filtroAnoPeriodo) {
		this.filtroAnoPeriodo = filtroAnoPeriodo;
	}

	public boolean isFiltroStatus() {
		return filtroStatus;
	}

	public void setFiltroStatus(boolean filtroStatus) {
		this.filtroStatus = filtroStatus;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public int getTamanhoMaxArquivo() {
		return tamanhoMaxArquivo;
	}

	public void setTamanhoMaxArquivo(int tamanhoMaxArquivo) {
		this.tamanhoMaxArquivo = tamanhoMaxArquivo;
	}

	public int getNovoStatus() {
		return novoStatus;
	}

	public void setNovoStatus(int novoStatus) {
		this.novoStatus = novoStatus;
	}

	public boolean isFiltroNivel() {
		return filtroNivel;
	}

	public void setFiltroNivel(boolean filtroNivel) {
		this.filtroNivel = filtroNivel;
	}

	public boolean isFiltroModalidade() {
		return filtroModalidade;
	}

	public void setFiltroModalidade(boolean filtroModalidade) {
		this.filtroModalidade = filtroModalidade;
	}

	public boolean isFiltroTipoBolsa() {
		return filtroTipoBolsa;
	}

	public void setFiltroTipoBolsa(boolean filtroTipoBolsa) {
		this.filtroTipoBolsa = filtroTipoBolsa;
	}

	public Character getNivel() {
		return nivel;
	}

	public void setNivel(Character nivel) {
		this.nivel = nivel;
	}

	public ModalidadeBolsaExterna getModalidadeBolsa() {
		return modalidadeBolsa;
	}

	public void setModalidadeBolsa(ModalidadeBolsaExterna modalidadeBolsa) {
		this.modalidadeBolsa = modalidadeBolsa;
	}

	public Integer getTipoBolsa() {
		return tipoBolsa;
	}

	public void setTipoBolsa(Integer tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}

	public String getTituloFormBusca() {
		return tituloFormBusca;
	}

	public void setTituloFormBusca(String tituloFormBusca) {
		this.tituloFormBusca = tituloFormBusca;
	}

	public boolean isBuscaSemIndicacao() {
		return buscaSemIndicacao;
	}

	public void setBuscaSemIndicacao(boolean buscaSemIndicacao) {
		this.buscaSemIndicacao = buscaSemIndicacao;
	}
	
	/**
	 * Retorna a descrição do nivel
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/stricto/plano_docencia_assistida/print_busca.jsp</li>
	 * </ul>
	 * @return
	 */
	public String getDescricaoNivel(){
		return NivelEnsino.getDescricao(nivel);
	}
	
	/**
	 * Retorna a descrição do status
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/stricto/plano_docencia_assistida/print_busca.jsp</li>
	 * </ul>
	 * @return
	 */
	public String getDescricaoStatus(){
		return PlanoDocenciaAssistida.getDescricaoStatus(status);
	}
	
	/**
	 * Retorna a descrição do tipo da bolsa  
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/stricto/plano_docencia_assistida/print_busca.jsp</li>
	 * </ul>
	 * @return
	 */
	public String getDescricaoTipoBolsa(){
		if (tipoBolsa <= 0)
			return "Todos";
		return (tipoBolsa == COM_INDICACAO ? "Com Indicação" : "Sem Indicação");
	}

	/**
	 * Retorna a lista de atividades não previstas ordenadas por data
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/stricto/plano_docencia_assistida/view.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<AtividadeDocenciaAssistida> getAtividadesNaoPrevistas() {
		Collections.sort( atividadesNaoPrevistas, new Comparator<AtividadeDocenciaAssistida>(){
			public int compare(AtividadeDocenciaAssistida arg0,	AtividadeDocenciaAssistida arg1) {
				return new CompareToBuilder()
				.append(arg0.getDataInicio(), arg1.getDataInicio())
				.append(arg0.getDataFim(), arg1.getDataFim())
				.toComparison();  
			}
		});		
		return atividadesNaoPrevistas;
	}

	public void setAtividadesNaoPrevistas(
			List<AtividadeDocenciaAssistida> atividadesNaoPrevistas) {
		this.atividadesNaoPrevistas = atividadesNaoPrevistas;
	}

	public boolean isFiltroDiscente() {
		return filtroDiscente;
	}

	public void setFiltroDiscente(boolean filtroDiscente) {
		this.filtroDiscente = filtroDiscente;
	}

	public boolean isFiltroComponente() {
		return filtroComponente;
	}

	public void setFiltroComponente(boolean filtroComponente) {
		this.filtroComponente = filtroComponente;
	}

	public boolean isFiltroAtividade() {
		return filtroAtividade;
	}

	public void setFiltroAtividade(boolean filtroAtividade) {
		this.filtroAtividade = filtroAtividade;
	}

	public boolean isFiltroCargaHoraria() {
		return filtroCargaHoraria;
	}

	public void setFiltroCargaHoraria(boolean filtroCargaHoraria) {
		this.filtroCargaHoraria = filtroCargaHoraria;
	}

	public String getDiscente() {
		return discente;
	}

	public void setDiscente(String discente) {
		this.discente = discente;
	}

	public Integer getChInicial() {
		return chInicial;
	}

	public void setChInicial(Integer chInicial) {
		this.chInicial = chInicial;
	}

	public Integer getChFinal() {
		return chFinal;
	}

	public void setChFinal(Integer chFinal) {
		this.chFinal = chFinal;
	}

	public Map<Integer, String> getStatusAnalise() {
		return statusAnalise;
	}

	public void setStatusAnalise(Map<Integer, String> statusAnalise) {
		this.statusAnalise = statusAnalise;
	}

	public ComponenteCurricular getComponente() {
		return componente;
	}

	public void setComponente(ComponenteCurricular componente) {
		this.componente = componente;
	}

	public boolean isBolsista() {
		return bolsista;
	}

	public void setBolsista(boolean bolsista) {
		this.bolsista = bolsista;
	}
}
