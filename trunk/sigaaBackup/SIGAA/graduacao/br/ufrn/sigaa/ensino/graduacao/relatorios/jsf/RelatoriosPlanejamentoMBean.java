/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * 18/10/2007
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.context.FacesContext;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.relatorios.TaxaConclusaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.jsf.HistoricoMBean;

/**
 * MBean  para gerar os relatórios de planejamento
 * @author leonardo
 *
 */

public class RelatoriosPlanejamentoMBean extends SigaaAbstractController<Unidade> {

	/** Link do formulário de seleção de ano e periodo, utilizado por outros tipos de relatório. */
	private final String JSP_ANO_PERIODO = "/portais/rh_plan/relatorios/form_ano_periodo.jsp";
	/** Link do formulário de seleção de ano de inicio e ano de fim, utilizado por outros tipos de relatório. */
	private final String JSP_ANO_INICIO_FIM = "/portais/rh_plan/relatorios/form_ano_inicio_fim.jsp";
	/** Link para o relatório de Alunos Matriculados utilizado pelo RelatorioQuantitativoAlunosMatriculados. */
	private final String JSP_REL_ALUNOS_MATRICULADOS = "/portais/rh_plan/relatorios/alunos_matriculados.jsp";
	/** Link para o relatório de Alunos ativos utilizado pelo RelatorioQuantitativoAlunosMatriculados. */
	private final String JSP_REL_ALUNOS_ATIVOS = "/portais/rh_plan/relatorios/alunos_ativos.jsp";
	/** Link do formulário de seleção de parâmetros compartilhado por varios tipos de relatório. */
	private final String JSP_FORM_ESTATISTICAS = "/portais/rh_plan/relatorios/form_estatisticas.jsp";
	/** Link do formulário de seleção de unidade e ano compartilhado por varios tipos de relatório. */
	private final String JSP_SELECIONA_DADOS = "/portais/rh_plan/relatorios/seleciona_dados.jsp";
	/** Link do formulário de seleção de mês e ano, compartilhado por varios tipos de relatório. */
	private final String JSP_FORM_MES_ANO = "/portais/rh_plan/relatorios/form_mes_ano.jsp";
	/** Link do formulário de seleção de parametros ,utilizado pelo relatório das disciplinas com o maior índice de reprovações por centro e departamento. */
	private final String JSP_FORM_REPROVACOES = "/portais/rh_plan/relatorios/form_reprovacoes.jsp";
	/** Link para o relatório das disciplinas com o maior índice de reprovações por centro e departamento. */
	private final String JSP_REL_REPROVACOES = "/portais/rh_plan/relatorios/reprovacoes.jsp";
	/** Link para o relatório das disciplinas com o maior índice de reprovações por centro e departamento analitico. */
	private final String JSP_REL_REPROVACOES_ANALITICO = "/portais/rh_plan/relatorios/reprovacoes_analitico.jsp";
	/** Link para o Relatório de utilização da turma virtual. */
	private final String JSP_REL_TURMA_VIRTUAL = "/portais/rh_plan/relatorios/rel_turma_virtual.jsp";
	/** Link do formulário de seleção de tipo de relatorio, utilizado pelo relatorio de turma virtual.*/
	private final String JSP_FORM_TURMA_VIRTUAL = "/portais/rh_plan/relatorios/form_turma_virtual.jsp";
	/** Link para o Relatório de Professores que não utilizam as turmas virtuais. */
	private final String JSP_REL_NAO_TURMA_VIRTUAL = "/portais/rh_plan/relatorios/rel_doc_nao_turma_virtual.jsp"; 
	/** Link para o relatório de Alunos concluintes nos cursos de graduação, por semestre, turno e gênero. */
	private final String JSP_REL_371 = "/portais/rh_plan/relatorios/rel_371.jsp";
	/** Link para o relatório de Alunos concluintes nos cursos de graduação por unidade e ano. */
	private final String JSP_REL_02 = "/portais/rh_plan/relatorios/rel_concluintes_02.jsp";
	
	/** Indicador para o relatório Alunos concluintes nos cursos de graduação, por semestre, turno e gênero*/
	private final int IND_RELATORIO_371 = 1;
	/** Indicador para o relatório Alunos concluintes nos cursos de graduação, por semestre, turno e gênero (Analítico)*/
	private final int IND_RELATORIO_2 = 2;
	/**Constante que recebe o indicador do relatorio que será gerado*/
	private int indicadorRelatorioAtual;
	
	/** Lista de dados do relatório. */
	private List<Map<String,Object>> lista = new ArrayList<Map<String,Object>>();	
	
	/** Constante que define o tipo de relatorio com alunos matriculados */
	private final int MATRICULADOS = 1;
	/** Constante que define o tipo de relatorio com alunos ativos*/
	private final int ATIVOS = 2;

	/** Unidade / centro do relatório. */
	private Unidade unidade, centro;
	/** Parâmetros de geração do relatório. */
	private Integer ano, anoInicio, anoFim, periodo, tipo, mes;
	/** Formato do relatório. */
	private String formato;
	/** Título do relatório. */
	private String tituloRelatorio;
	/** Nome do relatório. */
	private String nomeRelatorio;
	/** Indica se o relatório é analítico. */
	private boolean analitico;
	
	/** Valida o ano inicial e final. */
	private boolean validaPeriodoAno;
	/** Valida o ano. */
	private boolean validaAno;
	/** Valida o mês. */
	private boolean validaMes;
	/** Valida a unidade. */
	private boolean validaUnidade;
	/** Valida o ano/período. */
	private boolean validaAnoPeriodo;
	/** Valida o tipo do Relatório. */
	private boolean validaTipoRelatorio;
	/** Atributo para auxiliar na geração do relatório de detalhes de concluintes */
	private String campoLink;
	
	/** Sistema de origem dos dados de cada relatório */
    private int origemDados = Sistema.SIGAA;

    /** Dados do relatório. */
	private Map<String, Map<String, Integer>> relatorio = new TreeMap<String, Map<String, Integer>>();
	
	/** Lista de dados do relatório chamado pelo relatório 3.7.1. */
	private List<Map<String,Object>> listagem = new ArrayList<Map<String,Object>>();		
	
	/** Lista de dados do relatório de reprovações */
	private Collection<RelatorioReprovacoesDisciplinas> listaReprovacoes = new ArrayList<RelatorioReprovacoesDisciplinas>();

	/** Construtor padrão. */
	public RelatoriosPlanejamentoMBean(){
		clear();
	}

	/**
	 * Inicializar todos os objeto que serão usados no decorrer da classe.
	 */
	private void clear() {
		formato = "pdf";
        ano = CalendarUtils.getAnoAtual()-1;
        unidade = new Unidade();
        centro = new Unidade();
        mes = 1;
	}	
	/**
	 * Faz o redirecionamento para a tela de preenchimento dos dados para a geração do relatório
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>relatorios_dae.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String iniciarRelatorioQuantitativoAlunosMatriculados() throws DAOException{
		CalendarioAcademico cal = getCalendarioVigente();
		ano = cal.getAno();
		periodo = cal.getPeriodo();
		tipo = MATRICULADOS;
		validaAnoPeriodo = true;
		return forward(JSP_ANO_PERIODO);
	}

	/**
	 * Gera um relatório tendo com entrada o tipo (Matriculado ou Ativo) e o ano e período que se deseja fazer a busca. 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>form_ano_periodo.jsp</li>
	 * </ul> 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioQuantitativoAlunosMatriculados() throws DAOException{
		if (!validate()) return null;
		DiscenteDao dao = getDAO(DiscenteDao.class);
		if(tipo == MATRICULADOS){
			relatorio = dao.findQuantitativoAlunosMatriculadosPlanejamento(ano, periodo);
			return forward(JSP_REL_ALUNOS_MATRICULADOS);
		} else if(tipo == ATIVOS){
			relatorio = dao.findQuantitativoAlunosAtivosPlanejamento();
			return forward(JSP_REL_ALUNOS_ATIVOS);
		} else {
			addMensagemErro("Informe o tipo do relatório.");
			validaAnoPeriodo = true;
			return forward(JSP_ANO_PERIODO);
		}
	}
	
	/**
	 * Gera um relatório das disciplinas com o maior índice de reprovações por centro e departamento.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul>   
	 * @return
	 * @throws DAOException
	 */
	public String iniciarRelatorioReprovacoesPlanejamento() throws DAOException{
		anoInicio = CalendarUtils.getAnoAtual();
		anoFim = CalendarUtils.getAnoAtual();
		tipo = 3;
		analitico = false;
		validaPeriodoAno = true;
		return forward(JSP_FORM_REPROVACOES);
	}
	
	/**
	 * Gera um relatório das disciplinas que mais reprovam os alunos em cada departamento.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>form_reprovacoes.jsp</li>
	 * </ul> 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioReprovacoesPlanejamento() throws DAOException{
		UnidadeDao dao = getDAO(UnidadeDao.class);
		
		// Validação dos campos do formulário
		erros = new ListaMensagens();
		
		ValidatorUtil.validaInt(anoInicio, "Ano Início", erros);
		ValidatorUtil.validaInt(anoFim, "Ano Fim", erros);
		if(hasErrors()){
			addMensagens(erros);
			return null;
		}		
		
		if(centro.getId() > 0)
			centro = dao.findByPrimaryKey(centro.getId(), Unidade.class, "id", "nome");
		
		listaReprovacoes = dao.findReprovacoesByDepartamento(centro, anoInicio, anoFim);
		
		
		return forward(JSP_REL_REPROVACOES);
	}

	/**
	 * Gera um relatório das disciplinas com o maior índice de reprovações por centro e departamento (Analítico).
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul>  
	 * @return
	 * @throws DAOException
	 */
	public String iniciarRelatorioReprovacoesPlanejamentoAnalitico() throws DAOException{
		anoInicio = CalendarUtils.getAnoAtual();
		anoFim = CalendarUtils.getAnoAtual();
		tipo = 3;
		analitico = true;
		validaPeriodoAno = true;
		return forward(JSP_FORM_REPROVACOES);
	}
	
	/**
	 * Gera um relatório das disciplinas que mais reprovam os alunos em cada departamento(Analítico).
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> form_reprovacoes.jsp</li>
	 * </ul>  
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioReprovacoesPlanejamentoAnalitico() throws DAOException {
		UnidadeDao dao = getDAO(UnidadeDao.class);
		
		// Validação dos campos do formulário
		erros = new ListaMensagens();
		
		ValidatorUtil.validaInt(anoInicio, "Ano Início", erros);
		ValidatorUtil.validaInt(anoFim, "Ano Fim", erros);
		if(hasErrors()){
			addMensagens(erros);
			return null;
		}		
		
		if(centro.getId() > 0)
			centro = dao.findByPrimaryKey(centro.getId(), Unidade.class, "id", "nome");
		
		listaReprovacoes = dao.findReprovacoesByDepartamento(centro, anoInicio, anoFim);
		
		return forward(JSP_REL_REPROVACOES_ANALITICO);
	}
	
	/**
	 * Gera um relatório com informações Acadêmicas, dos Alunos cadastrados (vinculados) nos cursos de graduação, 
	 * por modalidade, habilitação, turno, semestre e gênero.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul>  
	 * @return
	 */
	public String iniciar311(){
		tituloRelatorio = "3.1.1 - Alunos cadastrados (vinculados) nos cursos de graduação, por modalidade, habilitação, turno, semestre e gênero";
		nomeRelatorio = "r_311_alunos_cadastrados_no_curso_mod_hab_tur";
		return iniciar();
	}

	/**
	 * Gera um relatório com informações Acadêmicas, dos Alunos cadastrados nos cursos de graduação, 
	 * por semestre, turno e gênero.
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul>  
	 * @return
	 */
	public String iniciar312(){
		tituloRelatorio = "3.1.2 - Alunos cadastrados nos cursos de graduação, por semestre, turno e gênero";
		nomeRelatorio = "r_312_alunos_cadastrados_no_curso_sem_tur_gen";
		return iniciar();
	}

	/**
	 *  Gera um relatório com informações Acadêmicas, nos cursos de graduação, por semestre, 
	 *  turno e gênero (vestibular e outras formas)
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar331(){
		tituloRelatorio = "Alunos ingressantes (vestibular e outras formas) nos cursos de graduação por semestre, turno e gênero";
		nomeRelatorio = "r_331_alunos_ingressantes_no_curso_sem_tur_gen";
		return iniciar();
	}

	/**
	 * Gera um relatório com informações Acadêmicas, nos cursos de graduação, por faixa etária e 
	 * gênero (vestibular e outras formas).
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar332(){
		tituloRelatorio = "Alunos ingressantes (vestibular e outras formas) nos cursos de graduação por faixa etária e gênero";
		nomeRelatorio = "r_332_alunos_ingressantes_vestiv_e_out_faixa_etaria_genero";
		return iniciar();
	}
	
	/**
	 * Gera um relatório com informações Acadêmicas, nas modalidades e habilitações dos cursos de graduação, por turno, 
	 * semestre e gênero (vestibular e outras formas).
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar333(){
		tituloRelatorio = "Alunos ingressantes (vestibular e outras formas) nas modalidades e habilitações dos cursos de graduação, por turno, semestre e gênero";
		nomeRelatorio = "r_333_alunos_ingressantes_no_curso_mod_hab_tur";
		return iniciar();
	}

	/**
	 * Gera um relatório com informações Acadêmicas, nas modalidades dos cursos de graduação, por turno, 
	 * forma de entrada e gênero (vestibular e outras formas). 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar334(){
		tituloRelatorio = "Alunos ingressantes nas modalidades dos cursos de graduação, por turno, forma de entrada e gênero";
		nomeRelatorio = "r_334_alunos_ingressantes_no_curso_entrada_sem_tur_gen";
		return iniciar();
	}

	/**
	 * Gera um relatório com informações Acadêmicas, pelo Vestibular nos cursos de graduação, por semestre, turno e gênero.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar335(){
		tituloRelatorio = "Alunos ingressantes pelo vestibular nos cursos de graduação por semestre, turno e gênero";
		nomeRelatorio = "r_335_alunos_ingressantes_no_curso_sem_tur_gen";
		return iniciar();
	}

	/**
	 * Gera um relatório com informações Acadêmicas, por outras formas de ingresso nos cursos de 
	 * graduação, por semestre, turno e gênero.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar336(){
		tituloRelatorio = "Alunos ingressantes por outras formas de ingressos nos cursos de graduação por semestre, turno e gênero";
		nomeRelatorio = "r_336_alunos_ingressantes_no_curso_sem_tur_gen";
		return iniciar();
	}

	/**
	 * Gera um relatório com informações Acadêmicas sobre os alunos que trancaram o curso de graduação,
	 *  por modalidade, habilitação, turno, semestre e gênero.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar351(){
		tituloRelatorio = "Alunos que trancaram o curso de graduação, por modalidade, habilitação, turno, semestre e gênero";
		nomeRelatorio = "r_351_alunos_trancaram_curso_sem_tur_gen";
		return iniciar();
	}

	/**
	 * Gera um relatório com informações Acadêmicas, nos cursos de graduação, por semestre, turno e gênero.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar361(){
		tituloRelatorio = "Alunos matriculados nos cursos de graduação, por semestre, turno e gênero";
		nomeRelatorio = "r_361_alunos_matriculados_no_curso_sem_tur_gen";
		return iniciar();
	}

	/**
	 * Gera um relatório com informações Acadêmicas, nas habilitações e modalidades dos cursos de graduação, 
	 * por semestre, turno e gênero.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar362(){
		tituloRelatorio = "Alunos matriculados nas habilitações e modalidades dos cursos de graduação, por semestre, turno e gênero";
		nomeRelatorio = "r_362_alunos_matriculados_no_curso_mod_hab_tur";
		return iniciar();
	}

	/**
	 * Gera um relatório com informações Acadêmicas, nas habilitações e modalidades dos cursos de graduação, por turno (Analítico).
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar3621(){
		tituloRelatorio = "Alunos matriculados nas habilitações e modalidades dos cursos de graduação, por turno no ano (Analítico)";
		nomeRelatorio = "r_3621_alunos_matriculados_no_curso_mod_hab_tur_analitico";
		return iniciar();
	}

	/**
	 * Gera um relatório com informações Acadêmicas, nas habilitações e modalidades dos cursos de graduação, 
	 * por turno e cidade (Analítico).
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar3622(){
		tituloRelatorio = "Alunos matriculados nas habilitações e modalidades dos cursos de graduação, por turno e cidade no ano (Analítico)";
		nomeRelatorio = "r_3622_alunos_matriculados_no_curso_mod_hab_tur_cidade_analitico";
		return iniciar();
	}

	/**
	 * Gera um relatório com informações Acadêmicas, nos cursos de graduação, por faixa etária e gênero
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar363(){
		tituloRelatorio = "Alunos matriculados nos cursos de graduação, por faixa etária e gênero";
		nomeRelatorio = "r_363_alunos_matriculados_faixa_etaria_genero";
		return iniciar();
	}

	/**
	 * Gera um relatório com informações Acadêmicas, alunos concluintes nos cursos de graduação, por semestre, turno e gênero.
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp
	 *   portais/relatorios/abas/ensino.jsp
	 *   </li>
	 * </ul> 
	 * @return
	 */
	public String iniciar371(){
		tituloRelatorio = "3.7.1 Alunos concluintes nos cursos de graduação, por semestre, turno e gênero";
		nomeRelatorio = "r_371_alunos_concluintes_no_curso_sem_tur_gen";
		indicadorRelatorioAtual = IND_RELATORIO_371;
		return iniciarRelatoriosJSP();
	}	

	/**
	 * Gera um relatório com informações Acadêmicas, alunos concluintes nos cursos de graduação, 
	 * por semestre, turno e gênero (Analítico).
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar3711(){
		tituloRelatorio = "Alunos concluintes nos cursos de graduação, por semestre, turno e gênero (Analítico)";
		indicadorRelatorioAtual = IND_RELATORIO_2;
		return iniciarRelatoriosJSP();
	}

	/**
	 * Gera um relatório com informações Acadêmicas, alunos concluintes nas modalidades e habilitações dos cursos
	 * de graduação, por turno, semestre e gênero.
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp
	 *   portais/relatorios/abas/ensino.jsp
	 *   </li>
	 * </ul> 
	 * @return
	 */
	public String iniciar372(){
		tituloRelatorio = "Alunos concluintes nas modalidades e habilitações dos cursos de graduação, por turno, semestre e gênero";
		nomeRelatorio = "r_372_alunos_concluintes_no_curso_mod_hab_tur";
		return iniciar();
	}

	/**
	 * Gera um relatório com informações Acadêmicas, alunos evadidos nas modalidades e habilitações dos cursos de graduação, 
	 * por turno, semestre e gênero.
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar381(){
		tituloRelatorio = "Alunos evadidos nas modalidades e habilitações dos cursos de graduação, por turno, semestre e gênero";
		nomeRelatorio = "r_381_alunos_evadidos_no_curso_mod_hab_tur";
		return iniciar();
	}

	/**
	 * era um relatório com informações Acadêmicas, alunos evadidos nos cursos de graduação, 
	 * segundo a forma de saída, turno e gênero.  
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar382(){
		tituloRelatorio = "Alunos evadidos nos cursos de graduação, segundo a forma de saída, turno e gênero";
		nomeRelatorio = "r_382_alunos_evadidos_no_curso_segundo_forma_saida_turno_genero";
		return iniciar();
	}

	/**
	 * Emiti um Relatório do tipo RENEX. 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> menu.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciarRENEX(){
		tituloRelatorio = "RENEX";
		nomeRelatorio = "renex_243";
		clear();
		return forward(JSP_ANO_INICIO_FIM);
	}
	
	/**
	 * Gera um relatório com do corpo Docente, com o número total de docentes por 
	 * grau de formação, por regime de trabalho e sexo.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar41(){
		tituloRelatorio = "Número total de docentes por grau de formação, por regime de trabalho e sexo";
		nomeRelatorio = "r_41_numero_total_docentes_formacao_regime_sexo";
		origemDados = Sistema.SIPAC;
		clear();
		validaAno = true;
		validaMes = true;
		return forward(JSP_FORM_MES_ANO);
	}
	
	/**
	 * Gera um relatório com do corpo Docente, com o número total de docentes por grau de formação.
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar411(){
		tituloRelatorio = "Número total de docentes por grau de formação";
		nomeRelatorio = "r_411_docentes_formacao";
		origemDados = Sistema.SIPAC;
		clear();
		validaAno = true;
		validaMes = true;
		return forward(JSP_FORM_MES_ANO);
	}
	
	/**
	 * Gera um relatório com do corpo Docente, com o número de docentes por sexo e faixa etária.
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar43(){
		tituloRelatorio = "Número de docentes por sexo e faixa etária";
		nomeRelatorio = "r_43_numero_docentes_sexo_faixa_etaria";
		origemDados = Sistema.SIPAC;
		clear();
		validaAno = true;
		validaMes = true;
		return forward(JSP_FORM_MES_ANO);
	}
	
	/**
	 * Gera um relatório com do corpo Docente, com o número de docentes permanentes por sexo e faixa etária.
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar431(){
		tituloRelatorio = "Número de docentes permanentes por sexo e faixa etária";
		nomeRelatorio = "r_431_numero_docentes_sexo_faixa_etaria_permanentes";
		origemDados = Sistema.SIPAC;
		clear();
		validaAno = true;
		validaMes = true;
		return forward(JSP_FORM_MES_ANO);
	}
	
	/**
	 * Gera um relatório com do corpo Docente, com o número total de docentes por categoria funcional e sexo.
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar47(){
		tituloRelatorio = "Número total de docentes por categoria funcional e sexo";
		nomeRelatorio = "r_47_numero_total_docentes_categoria_sexo";
		origemDados = Sistema.SIPAC;
		clear();
		validaAno = true;
		validaMes = true;
		return forward(JSP_FORM_MES_ANO);
	}
	
	/**
	 * Gera um relatório com do corpo Docente, com o número de docentes por centro, departamento e formação.
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar48(){
		tituloRelatorio = "Número de docentes por centro, departamento e formação";
		nomeRelatorio = "r_48_numero_docentes_centro_formacao";
		origemDados = Sistema.SIPAC;
		clear();
		validaAno = true;
		validaMes = true;
		return forward(JSP_FORM_MES_ANO);
	}
	
	/**
	 * Gera um relatório com do corpo Docente, com o número de Técnicos Administrativos por sexo e faixa etária
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar54(){
		tituloRelatorio = "Número de Técnicos Administrativos por sexo e faixa etária";
		nomeRelatorio = "r_54_numero_tecnicosadministrativos_sexo_faixa_etaria";
		origemDados = Sistema.SIPAC;
		clear();
		validaAno = true;
		validaMes = true;
		return forward(JSP_FORM_MES_ANO);
	}

	/**
	 * Gera um relatório com do corpo Docente, com o número de Técnicos Administrativos por nível de classificação.
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar55(){
		tituloRelatorio = "Número de Técnicos Administrativos por nível de classificação";
		nomeRelatorio = "r_55_numero_tecnicosadministrativos_nivel_classificacao";
		origemDados = Sistema.SIPAC;
		clear();
		validaAno = true;
		validaMes = true;
		return forward(JSP_FORM_MES_ANO);
	}
	
	/**
	 * Gera um relatório com do corpo Docente, com o número de Técnicos Administrativos e Docentes por Centro e Departamento.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciar56(){
		tituloRelatorio = "Número de Técnicos Administrativos e Docentes por Centro e Departamento";
		nomeRelatorio = "r_56_numero_tecnicosadministrativos_docentes_centro";
		origemDados = Sistema.SIPAC;
		clear();
		validaAno = true;
		validaMes = true;
		return forward(JSP_FORM_MES_ANO);
	}
	/**
	 * Gera um relatório Analíticos dos Alunos Ingressantes.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/portais/rh_plan/abas/graduacao.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciar01(){
		tituloRelatorio = "Ingressantes";
		nomeRelatorio = "relatorio_1";
		return iniciar();
	}
	/**
	 * Gera um relatório Analíticos dos Alunos Concluintes.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/portais/rh_plan/abas/graduacao.jsp
	 *   portais/relatorios/abas/ensino.jsp
	 *   </li>
	 * </ul>
	 * @return
	 */
	public String iniciar02(){
		tituloRelatorio = "Concluintes";
		indicadorRelatorioAtual = IND_RELATORIO_2;
		return iniciarRelatoriosJSP();
	}
	/**
	 * Gera um relatórioAnalíticos dos Alunos Concluintes(formato antigo).
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/portais/rh_plan/abas/graduacao.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciar02Jasper(){
		tituloRelatorio = "Concluintes";
		nomeRelatorio = "relatorio_2";
		return iniciar();
	}
	/**
	 * Gera um relatório Analíticos dos Alunos Matriculados.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/portais/rh_plan/abas/graduacao.jsp
	 *   portais/relatorios/abas/ensino.jsp
	 *   </li>
	 * </ul>
	 * @return
	 */
	public String iniciar03(){
		tituloRelatorio = "Matriculados";
		nomeRelatorio = "relatorio_3";
		return iniciar();
	}
	/**
	 * Gera um relatório Analíticos dos Alunos Evadidos.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/portais/rh_plan/abas/graduacao.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciar04(){
		tituloRelatorio = "Evasões";
		nomeRelatorio = "relatorio_4";
		return iniciar();
	}
	/**
	 * Gera um relatório Analíticos dos Alunos Trancados.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/portais/rh_plan/abas/graduacao.jsp
	 *   portais/relatorios/abas/ensino.jsp
	 *   </li>
	 * </ul>
	 * @return
	 */
	public String iniciar05(){
		tituloRelatorio = "Trancados";
		nomeRelatorio = "relatorio_5";
		return iniciar();
	}
	/**
	 * Gera um relatório Analíticos dos Alunos Não Matriculados.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/portais/rh_plan/abas/graduacao.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciar06(){
		tituloRelatorio = "Não Matriculados";
		nomeRelatorio = "relatorio_6";
		return iniciar();
	}
	/**
	 * Gera um relatório Analíticos dos Alunos Ingresantes por outras formas.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/portais/rh_plan/abas/graduacao.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciar07(){
		tituloRelatorio = "Ingresantes por outras formas";
		nomeRelatorio = "relatorio_7";
		return iniciar();
	}
	/**
	 * Gera um relatório Analíticos dos Alunos .
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/portais/rh_plan/abas/graduacao.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciar08(){
		tituloRelatorio = "Retornos de trancamento";
		nomeRelatorio = "relatorio_8";
		return iniciar();
	}
	
	/**
     * Método para iniciar a geração do relatório, compartilhado por todos os tipos de relatório.
     * Redireciona para a pagina do formulário
     *
     * @return
     */
    private String iniciar() {
        clear();
        validaAno = true;
        return forward(JSP_FORM_ESTATISTICAS);
    }
    
	/**
     * Método para iniciar a geração do relatório, compartilhado por todos os tipos de relatório.
     * Redireciona para a pagina do formulário (Relatórios em JSP)
     * @return
     */
    private String iniciarRelatoriosJSP() {
        clear();
        validaAno = true;
        return forward(JSP_SELECIONA_DADOS);
    }    
    
    /**
     * Gera o relatório conforme o indicador setado.<br/>
     * Método chamado pela seguinte JSP:
     * <ul>
     *    <li>/portais/rh_plan/relatorios/seleciona_dados.jsp</li>
     * </ul>
     * @return
     * @throws SegurancaException 
     */
    public String gerarRelatorioJSP() throws SegurancaException{
    	checkRole(SigaaPapeis.DAE);
       	// Validar campos do formulário
        if ( !validate() ) {
            return null;
        } 
    	switch (indicadorRelatorioAtual) {
		case IND_RELATORIO_371:
			return gerarRelatorio371JSP();
		case IND_RELATORIO_2:
			return gerarRelatorio02JSP();
		default:
			return null;
		}
    }
    
    /**
     * Gera Histórico ao clicar sobre um discente no relatório de concluintes.<br/>
     * Método chamado pela seguinte JSP:
     * <ul>
     *    <li>/portais/rh_plan/relatorios/rel_concluintes_02.jsp</li>
     * </ul>
     * 
     * @return
     * @throws ArqException
     */
    public String gerarHistorico() throws ArqException{
		HistoricoMBean bean = getMBean("historico");				
		return bean.selecionaDiscenteForm();
    }
    
    /**
     * Gera o relatório de concluintes.
     * @return
     */
    private String gerarRelatorio02JSP(){
    	TaxaConclusaoDao dao = getDAO(TaxaConclusaoDao.class);
    	try{                           
    		lista = dao.findConcluintes(ano, unidade.getId(),0,null,null,0,0);
    		
    		if (lista.isEmpty()){
    			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
    			return null;
    		}
    	} finally{
    		if (dao!=null)
    			dao.close();
    	}
    	return forward(JSP_REL_02);
    }
    
    /**
     * Exibe os alunos referente ao total clicado no relatório 3.7.1<br/>
     * Método chamado pela seguinte JSP:
     *  <ul>
     *  	<li>/portais/rh_plan/relatorios/rel_371.jsp</li>
     *  </ul>
     * @return
     */
    public String exibirRelatorioConcluintesDetalhes(){
    	TaxaConclusaoDao dao = getDAO(TaxaConclusaoDao.class);
    	try{                         
    		final String MASCULINO = "M";
    		final String FEMININO = "F";
    		final String DIURNO = "D";
    		final String NOTURNO = "N";
    		final String SEMESTRE_1 = "1";
    		final String SEMESTRE_2 = "2";
    		
    		int idUnidade = 0;
    		int idCurso = 0;
    		String sexo = null;
    		String turno = null;
    		int semestre = 0;
    		
    		int idModalidade = 0;
    		
    		String valor = campoLink;    		   		 
    		
    		String[] campos = valor.split("&");
    		
    		for (int i = 0; i < campos.length; i++){
    			if (i == 0){
    				String [] subCampo = campos[i].split("_");    			    			    				
    				for (int j = 0; j < subCampo.length; j++){
	    					if (subCampo[j].toString().equals(MASCULINO) || subCampo[j].toString().equals(FEMININO))
	    						sexo = subCampo[j];
	    					
	    					if (subCampo[j].toString().equals(DIURNO) || subCampo[j].toString().equals(NOTURNO))
	    						turno = subCampo[j];    
	    					
	    					if (subCampo[j].toString().equals(SEMESTRE_1) || subCampo[j].toString().equals(SEMESTRE_2))
	    						semestre = Integer.parseInt(subCampo[j]);
    				} 
    			} else {
    				String [] subCampo = campos[i].split(" ");    			    			    				
    				for (int j = 0; j < subCampo.length; j++){    				    				
	    				if (subCampo[j].toString().equals("curso")){
	    					idCurso = Integer.parseInt(subCampo[j + 1]);	    					
	    				}
	    				   
	    				if (subCampo[j].toString().equals("siglas")){
	    					idUnidade = Integer.parseInt(subCampo[j + 1]);
	    				} 
	    				
	    				if (subCampo[j].toString().equals("modalidade")){
	    					idModalidade = Integer.parseInt(subCampo[j + 1]);
	    				}	
    				}
				}    				     				    			
    		}
    		lista = dao.findConcluintes(ano, idUnidade, idCurso, sexo, turno, semestre, idModalidade);
    		
    		if (lista.isEmpty()){
    			return forward(JSP_REL_371);
    		}
    	} finally{
    		if (dao!=null)
    			dao.close();
    	}
    	return exibirListaAlunos();    	
    }    
    
    /**
     * Auxilia na exibição da lista de alunos para utilização nos relatórios de taxa de conclusão.
     *  <br />
	 * Método não invocado por JSPs.
	 * @return
     */
    public String exibirListaAlunos(){
    	if (!lista.isEmpty())
    		return forward(JSP_REL_02);
    	else
    		return null;
    }
    
    /**
     * Gera o relatório com informações Acadêmicas, alunos concluintes nos cursos de graduação, por semestre, turno e gênero.
     * <br />
	 * Método não invocado por JSPs.
	 *  @return
     */
    public String gerarRelatorio371JSP(){
 		tituloRelatorio = "3.7.1 - Alunos concluintes nos cursos de graduação, por semestre, turno e gênero";

    	TaxaConclusaoDao dao = getDAO(TaxaConclusaoDao.class);
    	try{
    		List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();
    		resultado = dao.findQuantitativoConcluintes(ano, unidade.getId());

    		if (resultado.isEmpty()){
    			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
    			return null;
    		}
    		
    		int idCurso = 0;
    		int DM1, DF1, NM1, NF1, DM2, DF2, NM2, NF2; 
    		DM1 = DF1 = NM1 = NF1 = DM2 = DF2 = NM2 = NF2 = 0;
    		int idModalidade = 0; 
    		String modalidade = ""; 
    		String sigla = "";
    		String nome = "";    		
    		int idUnidade = 0;
    		for (Map<String, Object> l : resultado){
    			if (idCurso == 0)
    				idCurso = Integer.parseInt(""+l.get("id_curso"));    		    			
    			
    			if (idCurso != Integer.parseInt(""+l.get("id_curso"))){   
    				HashMap<String, Object> linha = new HashMap<String, Object>();
    				
   					linha.put("modalidade", modalidade);
   					linha.put("sigla", sigla);
   					linha.put("nome", nome);
   					linha.put("idCurso", idCurso);
   					linha.put("idUnidade", idUnidade);
   					linha.put("idModalidade", idModalidade);

   					// Adiciona a lista os totais somados
    				addListaTotaisConcluintes(DM1, DF1, NM1, NF1, DM2, DF2,	NM2, NF2, linha);
    				
    				listagem.add(linha);
    				DM1 = DF1 = NM1 = NF1 = DM2 = DF2 = NM2 = NF2 = 0;
    				
    				idCurso = Integer.parseInt(""+l.get("id_curso"));
    			}
    			// Soma os valores nas sua respectivas variáveis
    			if (String.valueOf(l.get("periodo_turno")).equals("D") &&  String.valueOf(l.get("sexo")).equals("M") && Integer.parseInt(""+l.get("semestre")) == 1)
    				DM1 += Integer.parseInt(""+l.get("total"));
    			
    			if (String.valueOf(l.get("periodo_turno")).equals("D") &&  String.valueOf(l.get("sexo")).equals("F") && Integer.parseInt(""+l.get("semestre")) == 1)
    				DF1 += Integer.parseInt(""+l.get("total"));    		
    			
    			if (String.valueOf(l.get("periodo_turno")).equals("N") &&  String.valueOf(l.get("sexo")).equals("M") && Integer.parseInt(""+l.get("semestre")) == 1)
    				NM1 += Integer.parseInt(""+l.get("total"));
    			
    			if (String.valueOf(l.get("periodo_turno")).equals("N") &&  String.valueOf(l.get("sexo")).equals("F") && Integer.parseInt(""+l.get("semestre")) == 1)
    				NF1 += Integer.parseInt(""+l.get("total"));       			
    			
    			if (String.valueOf(l.get("periodo_turno")).equals("D") &&  String.valueOf(l.get("sexo")).equals("M") && Integer.parseInt(""+l.get("semestre")) == 2)
    				DM2 += Integer.parseInt(""+l.get("total"));
    			
    			if (String.valueOf(l.get("periodo_turno")).equals("D") &&  String.valueOf(l.get("sexo")).equals("F") && Integer.parseInt(""+l.get("semestre")) == 2)
    				DF2 += Integer.parseInt(""+l.get("total"));    		
    			
    			if (String.valueOf(l.get("periodo_turno")).equals("N") &&  String.valueOf(l.get("sexo")).equals("M") && Integer.parseInt(""+l.get("semestre")) == 2)
    				NM2 += Integer.parseInt(""+l.get("total"));
    			
    			if (String.valueOf(l.get("periodo_turno")).equals("N") &&  String.valueOf(l.get("sexo")).equals("F") && Integer.parseInt(""+l.get("semestre")) == 2)
    				NF2 += Integer.parseInt(""+l.get("total"));      	
    			
				modalidade = String.valueOf(l.get("ME_MODALIDADE"));
				sigla = String.valueOf(l.get("sigla"));
				nome = String.valueOf(l.get("nome"));
				idUnidade = Integer.valueOf(""+l.get("id_unidade"));
				idModalidade = Integer.valueOf(""+l.get("IDMODALIDADE"));   
    		}
    		
			HashMap<String, Object> linha = new HashMap<String, Object>();
			
			linha.put("modalidade", modalidade);
			linha.put("sigla", sigla);
			linha.put("nome", nome);
			linha.put("idCurso", idCurso);
			linha.put("idUnidade", idUnidade);
			linha.put("idModalidade", idModalidade);
			
			addListaTotaisConcluintes(DM1, DF1, NM1, NF1, DM2, DF2, NM2, NF2, linha);	
			
			listagem.add(linha);    		
    		
    	} finally {
    		if (dao != null)
    			dao.close();
    	}
    	
    	return forward(JSP_REL_371); 
    }  

    /**
     * Adiciona os valores totais para a lista que será exibida no relatório 371
     * @param DM1
     * @param DF1
     * @param NM1
     * @param NF1
     * @param DM2
     * @param DF2
     * @param NM2
     * @param NF2
     * @param linha
     */
	private void addListaTotaisConcluintes(int DM1, int DF1, int NM1, int NF1,
			int DM2, int DF2, int NM2, int NF2, HashMap<String, Object> linha) {
		// 1º SEMESTRE
		// Diurno 
		// Masculino
		linha.put("DM1", DM1);
		// Feminino
		linha.put("DF1", DF1);
		// Total Diurno 1º Semestre
		linha.put("TD1", DM1 + DF1);
		
		// Noturno
		// Masculino
		linha.put("NM1", NM1);
		// Feminino
		linha.put("NF1", NF1);
		// Total Noturno 1º Semestre    				
		linha.put("TN1", NM1 + NF1);
		
		// Total Masculino 1º Semestre
		linha.put("TM1", DM1 + NM1);
		// Total Feminino 1º Semestre    				
		linha.put("TF1", DF1 + NF1);
		
		// Total Geral do 1º Semestre
		linha.put("TG1", DM1 + NM1 + DF1 + NF1);
		
		// 2º SEMESTRE
		// Noturno
		// Masculino    				
		linha.put("DM2", DM2);
		// Feminino
		linha.put("DF2", DF2);
		// Total Diurno 2º Semestre
		linha.put("TD2", DM2 + DF2);
		
		// Noturno
		// Masculino
		linha.put("NM2", NM2);
		// Feminino
		linha.put("NF2", NF2);
		// Total Noturno 2º Semestre
		linha.put("TN2", NM2 + NF2);
		
		// Total Masculino 2º Semestre
		linha.put("TM2", DM2 + NM2);
		// Total Feminino 2º Semestre   
		linha.put("TF2", DF2 + NF2);
		
		// Total Geral do 2º Semestre
		linha.put("TG2", DM2 + NM2 + DF2 + NF2);
		
		// Total Geral Masculino
		linha.put("TM", DM1 + NM1 + DM2 + NM2);
		// Total Geral Feminino    				
		linha.put("TF", DF1 + NF1 + DF2 + NF2);
		// Total Geral     				
		linha.put("TG", DM1 + NM1 + DF1 + NF1 + DM2 + NM2 + DF2 + NF2);
	}

    /**
     * Realizar a geração do relatório, de acordo com os critérios selecionados
     *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/form_ano_inicio_fim.jsp
	 *   /portais/rh_plan/abas/form_ano_periodo.jsp
	 *   /portais/rh_plan/abas/form_estatisticas.jsp
	 *   /portais/rh_plan/abas/form_mes_ano.jsp
	 *   /portais/rh_plan/abas/ form_reprovacoes.jsp (2 matches)
	 *   /portais/rh_plan/abas/seleciona_dados.jsp
	 *   </li>
	 * </ul> 
     * @return
     * @throws DAOException
     */
    public String gerarRelatorio() throws DAOException {
    	// Validar campos do formulário
        if ( !validate() ) {
            return null;
        }

        // Gerar relatório
        Connection con = null; 
        try {
        	
            // Popular parâmetros do relatório
            HashMap<String, Object> parametros = new HashMap<String, Object>();
//            parametros.put("SUBREPORT_DIR", JasperReportsUtil.getReportSIGAA(nomeRelatorio + ".jasper") );
            parametros.put("unidade", unidade.getId() );
            parametros.put("mes", mes );
            parametros.put("ano", ano );
            parametros.put("anoInicio", anoInicio );
            parametros.put("anoFim", anoFim );
            parametros.put("subSistema", getSubSistema().getNome());
	        parametros.put("subSistemaLink", getSubSistema().getLink());

            // Preencher relatório
            con = getConexao();
            JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA(nomeRelatorio + ".jasper") , parametros, con );
            if (prt.getPages().size() == 0) {
				addMensagemWarning("O relatório gerado não possui páginas.");
				return null;
			}
            // Exportar relatório de acordo com o formato escolhido
            String nomeArquivo = nomeRelatorio+"."+formato;
            JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), formato);
            FacesContext.getCurrentInstance().responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
            notifyError(e);
            addMensagemErro("Ocorreu um erro durante a geração deste relatório. Por favor,contacte o suporte através do \"Abrir Chamado\"");
            return null;
        } finally {
            Database.getInstance().close(con);
        }

        return null;
    }
    
    /**
     * Retorna a conexão de acordo com a origem dos dados especifica
     * de cada relatório
     *
     * @return
     * @throws SQLException
     */
    private Connection getConexao() throws SQLException {
        Connection con = null;
        switch (origemDados) {
            case Sistema.SIGAA: con = Database.getInstance().getSigaaConnection(); break;
            case Sistema.SIPAC: con = Database.getInstance().getSipacConnection(); break;
        }
        return con;
    }

    
    /**
     * Direciona para a página para se verificar o uso da turma virtual, buscando pelo ano 
     * e pelo período.
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul>
     * @return
     * @throws DAOException
     */
	public String iniciarRelatorioTurmaVirtual() throws DAOException{
		ano = getAno();
		periodo = getPeriodoAtual();
		validaAnoPeriodo = true;
		validaTipoRelatorio = true;
		return forward(JSP_FORM_TURMA_VIRTUAL);
	}
	
	/**
	 * Serve para direcionar o usuário para a busca específica de acordo com o tipo de relatório que o mesmo selecionou na
	 * <br />
	 * tela de busca. E de acordo com os parâmetros passados, para a busca. 
	 *   
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/portais/rh_plan/relatorios/form_turma_virtual.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String relatorioTurmaVirtual() throws DAOException, ArqException {
		if (tipo == 0 ||ano == null || periodo == null) {
			if (tipo == 0) {
				addMensagemErro("Tipo de Relatório: Campo obrigatório não informado");
			}
			if (ano == null || periodo == null ) {
				addMensagemErro("Ano-Período: Campo obrigatório não informado");
			}
			validaAnoPeriodo = true;
			validaTipoRelatorio = true;
			return forward(JSP_FORM_TURMA_VIRTUAL);
		}
		TurmaVirtualDao dao = getDAO(TurmaVirtualDao.class);
		if (tipo == 1) {
			lista = dao.relatorioUsoTurmaVirtual(ano, periodo);	
			if (lista.size() != 0) {
				return forward(JSP_REL_TURMA_VIRTUAL);
			}
		}else {
			lista = dao.relatorioDocenteNaoUsaTurmaVirtual(ano, periodo);
			if (lista.size() != 0) {
				return forward(JSP_REL_NAO_TURMA_VIRTUAL);	
		    }
	    }
		addMensagemErro("Não foi encontrado nenhum registro com os dados passados.");
		validaAnoPeriodo = true;
		validaTipoRelatorio = true;
		return forward(JSP_FORM_TURMA_VIRTUAL);
    }

    /**
     * Validar dados do formulário
     *
     * @return
     */
    private boolean validate() {
    	ListaMensagens erros = new ListaMensagens();
    	if(validaAnoPeriodo) {
    		if (ano == null || periodo == null) {
    			addMensagemErro("Ano e Periodo são campos obrigatórios");
    		}
    	}
    	
    	if (validaPeriodoAno) {
	        if (anoInicio == null)
	        	addMensagemErro("O campo Ano Início é obrigatório");
	        if (anoFim == null)
	        	addMensagemErro("O campo Ano Fim é obrigatório");
	        if (hasErrors())
	        	return false;
	      	ValidatorUtil.validaInt(anoInicio, "Ano Início", erros);
	      	ValidatorUtil.validaInt(anoFim, "Ano Fim", erros);
	      	if (anoInicio > CalendarUtils.getAnoAtual() || anoFim > CalendarUtils.getAnoAtual())
	      		erros.addErro("O ano informado não pode ser maior ao ano atual");
	      	if (anoInicio > anoFim )
	      		erros.addErro("O ano Inicial deve ser menor que o ano Final");
    	}
    	
    	if (validaAno) {
	        if (ano == null)
	        	addMensagemErro("O campo Ano é obrigatório");
	        if (hasErrors())
	        	return false;
	      	ValidatorUtil.validaInt(ano, "Ano", erros);
    	}
    	
    	if (validaUnidade) {
	      	ValidatorUtil.validateRequiredId(unidade.getId(), "Ano", erros);
    	}
      	
        addMensagens(erros);
        return !hasErrors();
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
	public Map<String, Map<String, Integer>> getRelatorio() {
		return relatorio;
	}
	public void setRelatorio(Map<String, Map<String, Integer>> relatorio) {
		this.relatorio = relatorio;
	}

	public Integer getTipo() {
		return tipo;
	}

	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}

	public String getFormato() {
		return formato;
	}

	public void setFormato(String formato) {
		this.formato = formato;
	}

	public String getTituloRelatorio() {
		return tituloRelatorio;
	}

	public void setTituloRelatorio(String tituloRelatorio) {
		this.tituloRelatorio = tituloRelatorio;
	}

	public String getNomeRelatorio() {
		return nomeRelatorio;
	}

	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio = nomeRelatorio;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public Integer getAnoFim() {
		return anoFim;
	}

	public void setAnoFim(Integer anoFim) {
		this.anoFim = anoFim;
	}

	public Integer getAnoInicio() {
		return anoInicio;
	}

	public void setAnoInicio(Integer anoInicio) {
		this.anoInicio = anoInicio;
	}

	public Unidade getCentro() {
		return centro;
	}

	public void setCentro(Unidade centro) {
		this.centro = centro;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public int getOrigemDados() {
		return origemDados;
	}

	public void setOrigemDados(int origemDados) {
		this.origemDados = origemDados;
	}

	public boolean isAnalitico() {
		return analitico;
	}

	public void setAnalitico(boolean analitico) {
		this.analitico = analitico;
	}

	public List<Map<String, Object>> getLista() {
		return lista;
	}

	public void setLista(List<Map<String, Object>> lista) {
		this.lista = lista;
	}

	public boolean isValidaPeriodoAno() {
		return validaPeriodoAno;
	}

	public void setValidaPeriodoAno(boolean validaPeriodoAno) {
		this.validaPeriodoAno = validaPeriodoAno;
	}

	public boolean isValidaAno() {
		return validaAno;
	}

	public void setValidaAno(boolean validaAno) {
		this.validaAno = validaAno;
	}

	public boolean isValidaMes() {
		return validaMes;
	}

	public void setValidaMes(boolean validaMes) {
		this.validaMes = validaMes;
	}

	public boolean isValidaUnidade() {
		return validaUnidade;
	}

	public void setValidaUnidade(boolean validaUnidade) {
		this.validaUnidade = validaUnidade;
	}

	public boolean isValidaAnoPeriodo() {
		return validaAnoPeriodo;
	}

	public void setValidaAnoPeriodo(boolean validaAnoPeriodo) {
		this.validaAnoPeriodo = validaAnoPeriodo;
	}

	public boolean isValidaTipoRelatorio() {
		return validaTipoRelatorio;
	}

	public void setValidaTipoRelatorio(boolean validaTipoRelatorio) {
		this.validaTipoRelatorio = validaTipoRelatorio;
	}

	public int getIndicadorRelatorioAtual() {
		return indicadorRelatorioAtual;
	}

	public void setIndicadorRelatorioAtual(int indicadorRelatorioAtual) {
		this.indicadorRelatorioAtual = indicadorRelatorioAtual;
	}

	public List<Map<String, Object>> getListagem() {
		return listagem;
	}

	public void setListagem(List<Map<String, Object>> listagem) {
		this.listagem = listagem;
	}

	public String getCampoLink() {
		return campoLink;
	}

	public void setCampoLink(String campoLink) {
		this.campoLink = campoLink;
	}

	public Collection<RelatorioReprovacoesDisciplinas> getListaReprovacoes() {
		return listaReprovacoes;
	}

	public void setListaReprovacoes(
			Collection<RelatorioReprovacoesDisciplinas> listaReprovacoes) {
		this.listaReprovacoes = listaReprovacoes;
	}
}