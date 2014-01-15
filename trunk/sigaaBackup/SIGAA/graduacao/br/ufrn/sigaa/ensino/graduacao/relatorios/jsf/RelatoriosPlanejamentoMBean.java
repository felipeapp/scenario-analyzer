/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
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
 * MBean  para gerar os relat�rios de planejamento
 * @author leonardo
 *
 */

public class RelatoriosPlanejamentoMBean extends SigaaAbstractController<Unidade> {

	/** Link do formul�rio de sele��o de ano e periodo, utilizado por outros tipos de relat�rio. */
	private final String JSP_ANO_PERIODO = "/portais/rh_plan/relatorios/form_ano_periodo.jsp";
	/** Link do formul�rio de sele��o de ano de inicio e ano de fim, utilizado por outros tipos de relat�rio. */
	private final String JSP_ANO_INICIO_FIM = "/portais/rh_plan/relatorios/form_ano_inicio_fim.jsp";
	/** Link para o relat�rio de Alunos Matriculados utilizado pelo RelatorioQuantitativoAlunosMatriculados. */
	private final String JSP_REL_ALUNOS_MATRICULADOS = "/portais/rh_plan/relatorios/alunos_matriculados.jsp";
	/** Link para o relat�rio de Alunos ativos utilizado pelo RelatorioQuantitativoAlunosMatriculados. */
	private final String JSP_REL_ALUNOS_ATIVOS = "/portais/rh_plan/relatorios/alunos_ativos.jsp";
	/** Link do formul�rio de sele��o de par�metros compartilhado por varios tipos de relat�rio. */
	private final String JSP_FORM_ESTATISTICAS = "/portais/rh_plan/relatorios/form_estatisticas.jsp";
	/** Link do formul�rio de sele��o de unidade e ano compartilhado por varios tipos de relat�rio. */
	private final String JSP_SELECIONA_DADOS = "/portais/rh_plan/relatorios/seleciona_dados.jsp";
	/** Link do formul�rio de sele��o de m�s e ano, compartilhado por varios tipos de relat�rio. */
	private final String JSP_FORM_MES_ANO = "/portais/rh_plan/relatorios/form_mes_ano.jsp";
	/** Link do formul�rio de sele��o de parametros ,utilizado pelo relat�rio das disciplinas com o maior �ndice de reprova��es por centro e departamento. */
	private final String JSP_FORM_REPROVACOES = "/portais/rh_plan/relatorios/form_reprovacoes.jsp";
	/** Link para o relat�rio das disciplinas com o maior �ndice de reprova��es por centro e departamento. */
	private final String JSP_REL_REPROVACOES = "/portais/rh_plan/relatorios/reprovacoes.jsp";
	/** Link para o relat�rio das disciplinas com o maior �ndice de reprova��es por centro e departamento analitico. */
	private final String JSP_REL_REPROVACOES_ANALITICO = "/portais/rh_plan/relatorios/reprovacoes_analitico.jsp";
	/** Link para o Relat�rio de utiliza��o da turma virtual. */
	private final String JSP_REL_TURMA_VIRTUAL = "/portais/rh_plan/relatorios/rel_turma_virtual.jsp";
	/** Link do formul�rio de sele��o de tipo de relatorio, utilizado pelo relatorio de turma virtual.*/
	private final String JSP_FORM_TURMA_VIRTUAL = "/portais/rh_plan/relatorios/form_turma_virtual.jsp";
	/** Link para o Relat�rio de Professores que n�o utilizam as turmas virtuais. */
	private final String JSP_REL_NAO_TURMA_VIRTUAL = "/portais/rh_plan/relatorios/rel_doc_nao_turma_virtual.jsp"; 
	/** Link para o relat�rio de Alunos concluintes nos cursos de gradua��o, por semestre, turno e g�nero. */
	private final String JSP_REL_371 = "/portais/rh_plan/relatorios/rel_371.jsp";
	/** Link para o relat�rio de Alunos concluintes nos cursos de gradua��o por unidade e ano. */
	private final String JSP_REL_02 = "/portais/rh_plan/relatorios/rel_concluintes_02.jsp";
	
	/** Indicador para o relat�rio Alunos concluintes nos cursos de gradua��o, por semestre, turno e g�nero*/
	private final int IND_RELATORIO_371 = 1;
	/** Indicador para o relat�rio Alunos concluintes nos cursos de gradua��o, por semestre, turno e g�nero (Anal�tico)*/
	private final int IND_RELATORIO_2 = 2;
	/**Constante que recebe o indicador do relatorio que ser� gerado*/
	private int indicadorRelatorioAtual;
	
	/** Lista de dados do relat�rio. */
	private List<Map<String,Object>> lista = new ArrayList<Map<String,Object>>();	
	
	/** Constante que define o tipo de relatorio com alunos matriculados */
	private final int MATRICULADOS = 1;
	/** Constante que define o tipo de relatorio com alunos ativos*/
	private final int ATIVOS = 2;

	/** Unidade / centro do relat�rio. */
	private Unidade unidade, centro;
	/** Par�metros de gera��o do relat�rio. */
	private Integer ano, anoInicio, anoFim, periodo, tipo, mes;
	/** Formato do relat�rio. */
	private String formato;
	/** T�tulo do relat�rio. */
	private String tituloRelatorio;
	/** Nome do relat�rio. */
	private String nomeRelatorio;
	/** Indica se o relat�rio � anal�tico. */
	private boolean analitico;
	
	/** Valida o ano inicial e final. */
	private boolean validaPeriodoAno;
	/** Valida o ano. */
	private boolean validaAno;
	/** Valida o m�s. */
	private boolean validaMes;
	/** Valida a unidade. */
	private boolean validaUnidade;
	/** Valida o ano/per�odo. */
	private boolean validaAnoPeriodo;
	/** Valida o tipo do Relat�rio. */
	private boolean validaTipoRelatorio;
	/** Atributo para auxiliar na gera��o do relat�rio de detalhes de concluintes */
	private String campoLink;
	
	/** Sistema de origem dos dados de cada relat�rio */
    private int origemDados = Sistema.SIGAA;

    /** Dados do relat�rio. */
	private Map<String, Map<String, Integer>> relatorio = new TreeMap<String, Map<String, Integer>>();
	
	/** Lista de dados do relat�rio chamado pelo relat�rio 3.7.1. */
	private List<Map<String,Object>> listagem = new ArrayList<Map<String,Object>>();		
	
	/** Lista de dados do relat�rio de reprova��es */
	private Collection<RelatorioReprovacoesDisciplinas> listaReprovacoes = new ArrayList<RelatorioReprovacoesDisciplinas>();

	/** Construtor padr�o. */
	public RelatoriosPlanejamentoMBean(){
		clear();
	}

	/**
	 * Inicializar todos os objeto que ser�o usados no decorrer da classe.
	 */
	private void clear() {
		formato = "pdf";
        ano = CalendarUtils.getAnoAtual()-1;
        unidade = new Unidade();
        centro = new Unidade();
        mes = 1;
	}	
	/**
	 * Faz o redirecionamento para a tela de preenchimento dos dados para a gera��o do relat�rio
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Gera um relat�rio tendo com entrada o tipo (Matriculado ou Ativo) e o ano e per�odo que se deseja fazer a busca. 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemErro("Informe o tipo do relat�rio.");
			validaAnoPeriodo = true;
			return forward(JSP_ANO_PERIODO);
		}
	}
	
	/**
	 * Gera um relat�rio das disciplinas com o maior �ndice de reprova��es por centro e departamento.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Gera um relat�rio das disciplinas que mais reprovam os alunos em cada departamento.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>form_reprovacoes.jsp</li>
	 * </ul> 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioReprovacoesPlanejamento() throws DAOException{
		UnidadeDao dao = getDAO(UnidadeDao.class);
		
		// Valida��o dos campos do formul�rio
		erros = new ListaMensagens();
		
		ValidatorUtil.validaInt(anoInicio, "Ano In�cio", erros);
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
	 * Gera um relat�rio das disciplinas com o maior �ndice de reprova��es por centro e departamento (Anal�tico).
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Gera um relat�rio das disciplinas que mais reprovam os alunos em cada departamento(Anal�tico).
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> form_reprovacoes.jsp</li>
	 * </ul>  
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioReprovacoesPlanejamentoAnalitico() throws DAOException {
		UnidadeDao dao = getDAO(UnidadeDao.class);
		
		// Valida��o dos campos do formul�rio
		erros = new ListaMensagens();
		
		ValidatorUtil.validaInt(anoInicio, "Ano In�cio", erros);
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
	 * Gera um relat�rio com informa��es Acad�micas, dos Alunos cadastrados (vinculados) nos cursos de gradua��o, 
	 * por modalidade, habilita��o, turno, semestre e g�nero.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul>  
	 * @return
	 */
	public String iniciar311(){
		tituloRelatorio = "3.1.1 - Alunos cadastrados (vinculados) nos cursos de gradua��o, por modalidade, habilita��o, turno, semestre e g�nero";
		nomeRelatorio = "r_311_alunos_cadastrados_no_curso_mod_hab_tur";
		return iniciar();
	}

	/**
	 * Gera um relat�rio com informa��es Acad�micas, dos Alunos cadastrados nos cursos de gradua��o, 
	 * por semestre, turno e g�nero.
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul>  
	 * @return
	 */
	public String iniciar312(){
		tituloRelatorio = "3.1.2 - Alunos cadastrados nos cursos de gradua��o, por semestre, turno e g�nero";
		nomeRelatorio = "r_312_alunos_cadastrados_no_curso_sem_tur_gen";
		return iniciar();
	}

	/**
	 *  Gera um relat�rio com informa��es Acad�micas, nos cursos de gradua��o, por semestre, 
	 *  turno e g�nero (vestibular e outras formas)
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar331(){
		tituloRelatorio = "Alunos ingressantes (vestibular e outras formas) nos cursos de gradua��o por semestre, turno e g�nero";
		nomeRelatorio = "r_331_alunos_ingressantes_no_curso_sem_tur_gen";
		return iniciar();
	}

	/**
	 * Gera um relat�rio com informa��es Acad�micas, nos cursos de gradua��o, por faixa et�ria e 
	 * g�nero (vestibular e outras formas).
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar332(){
		tituloRelatorio = "Alunos ingressantes (vestibular e outras formas) nos cursos de gradua��o por faixa et�ria e g�nero";
		nomeRelatorio = "r_332_alunos_ingressantes_vestiv_e_out_faixa_etaria_genero";
		return iniciar();
	}
	
	/**
	 * Gera um relat�rio com informa��es Acad�micas, nas modalidades e habilita��es dos cursos de gradua��o, por turno, 
	 * semestre e g�nero (vestibular e outras formas).
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar333(){
		tituloRelatorio = "Alunos ingressantes (vestibular e outras formas) nas modalidades e habilita��es dos cursos de gradua��o, por turno, semestre e g�nero";
		nomeRelatorio = "r_333_alunos_ingressantes_no_curso_mod_hab_tur";
		return iniciar();
	}

	/**
	 * Gera um relat�rio com informa��es Acad�micas, nas modalidades dos cursos de gradua��o, por turno, 
	 * forma de entrada e g�nero (vestibular e outras formas). 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar334(){
		tituloRelatorio = "Alunos ingressantes nas modalidades dos cursos de gradua��o, por turno, forma de entrada e g�nero";
		nomeRelatorio = "r_334_alunos_ingressantes_no_curso_entrada_sem_tur_gen";
		return iniciar();
	}

	/**
	 * Gera um relat�rio com informa��es Acad�micas, pelo Vestibular nos cursos de gradua��o, por semestre, turno e g�nero.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar335(){
		tituloRelatorio = "Alunos ingressantes pelo vestibular nos cursos de gradua��o por semestre, turno e g�nero";
		nomeRelatorio = "r_335_alunos_ingressantes_no_curso_sem_tur_gen";
		return iniciar();
	}

	/**
	 * Gera um relat�rio com informa��es Acad�micas, por outras formas de ingresso nos cursos de 
	 * gradua��o, por semestre, turno e g�nero.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar336(){
		tituloRelatorio = "Alunos ingressantes por outras formas de ingressos nos cursos de gradua��o por semestre, turno e g�nero";
		nomeRelatorio = "r_336_alunos_ingressantes_no_curso_sem_tur_gen";
		return iniciar();
	}

	/**
	 * Gera um relat�rio com informa��es Acad�micas sobre os alunos que trancaram o curso de gradua��o,
	 *  por modalidade, habilita��o, turno, semestre e g�nero.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar351(){
		tituloRelatorio = "Alunos que trancaram o curso de gradua��o, por modalidade, habilita��o, turno, semestre e g�nero";
		nomeRelatorio = "r_351_alunos_trancaram_curso_sem_tur_gen";
		return iniciar();
	}

	/**
	 * Gera um relat�rio com informa��es Acad�micas, nos cursos de gradua��o, por semestre, turno e g�nero.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar361(){
		tituloRelatorio = "Alunos matriculados nos cursos de gradua��o, por semestre, turno e g�nero";
		nomeRelatorio = "r_361_alunos_matriculados_no_curso_sem_tur_gen";
		return iniciar();
	}

	/**
	 * Gera um relat�rio com informa��es Acad�micas, nas habilita��es e modalidades dos cursos de gradua��o, 
	 * por semestre, turno e g�nero.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar362(){
		tituloRelatorio = "Alunos matriculados nas habilita��es e modalidades dos cursos de gradua��o, por semestre, turno e g�nero";
		nomeRelatorio = "r_362_alunos_matriculados_no_curso_mod_hab_tur";
		return iniciar();
	}

	/**
	 * Gera um relat�rio com informa��es Acad�micas, nas habilita��es e modalidades dos cursos de gradua��o, por turno (Anal�tico).
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar3621(){
		tituloRelatorio = "Alunos matriculados nas habilita��es e modalidades dos cursos de gradua��o, por turno no ano (Anal�tico)";
		nomeRelatorio = "r_3621_alunos_matriculados_no_curso_mod_hab_tur_analitico";
		return iniciar();
	}

	/**
	 * Gera um relat�rio com informa��es Acad�micas, nas habilita��es e modalidades dos cursos de gradua��o, 
	 * por turno e cidade (Anal�tico).
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar3622(){
		tituloRelatorio = "Alunos matriculados nas habilita��es e modalidades dos cursos de gradua��o, por turno e cidade no ano (Anal�tico)";
		nomeRelatorio = "r_3622_alunos_matriculados_no_curso_mod_hab_tur_cidade_analitico";
		return iniciar();
	}

	/**
	 * Gera um relat�rio com informa��es Acad�micas, nos cursos de gradua��o, por faixa et�ria e g�nero
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar363(){
		tituloRelatorio = "Alunos matriculados nos cursos de gradua��o, por faixa et�ria e g�nero";
		nomeRelatorio = "r_363_alunos_matriculados_faixa_etaria_genero";
		return iniciar();
	}

	/**
	 * Gera um relat�rio com informa��es Acad�micas, alunos concluintes nos cursos de gradua��o, por semestre, turno e g�nero.
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp
	 *   portais/relatorios/abas/ensino.jsp
	 *   </li>
	 * </ul> 
	 * @return
	 */
	public String iniciar371(){
		tituloRelatorio = "3.7.1 Alunos concluintes nos cursos de gradua��o, por semestre, turno e g�nero";
		nomeRelatorio = "r_371_alunos_concluintes_no_curso_sem_tur_gen";
		indicadorRelatorioAtual = IND_RELATORIO_371;
		return iniciarRelatoriosJSP();
	}	

	/**
	 * Gera um relat�rio com informa��es Acad�micas, alunos concluintes nos cursos de gradua��o, 
	 * por semestre, turno e g�nero (Anal�tico).
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar3711(){
		tituloRelatorio = "Alunos concluintes nos cursos de gradua��o, por semestre, turno e g�nero (Anal�tico)";
		indicadorRelatorioAtual = IND_RELATORIO_2;
		return iniciarRelatoriosJSP();
	}

	/**
	 * Gera um relat�rio com informa��es Acad�micas, alunos concluintes nas modalidades e habilita��es dos cursos
	 * de gradua��o, por turno, semestre e g�nero.
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp
	 *   portais/relatorios/abas/ensino.jsp
	 *   </li>
	 * </ul> 
	 * @return
	 */
	public String iniciar372(){
		tituloRelatorio = "Alunos concluintes nas modalidades e habilita��es dos cursos de gradua��o, por turno, semestre e g�nero";
		nomeRelatorio = "r_372_alunos_concluintes_no_curso_mod_hab_tur";
		return iniciar();
	}

	/**
	 * Gera um relat�rio com informa��es Acad�micas, alunos evadidos nas modalidades e habilita��es dos cursos de gradua��o, 
	 * por turno, semestre e g�nero.
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar381(){
		tituloRelatorio = "Alunos evadidos nas modalidades e habilita��es dos cursos de gradua��o, por turno, semestre e g�nero";
		nomeRelatorio = "r_381_alunos_evadidos_no_curso_mod_hab_tur";
		return iniciar();
	}

	/**
	 * era um relat�rio com informa��es Acad�micas, alunos evadidos nos cursos de gradua��o, 
	 * segundo a forma de sa�da, turno e g�nero.  
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar382(){
		tituloRelatorio = "Alunos evadidos nos cursos de gradua��o, segundo a forma de sa�da, turno e g�nero";
		nomeRelatorio = "r_382_alunos_evadidos_no_curso_segundo_forma_saida_turno_genero";
		return iniciar();
	}

	/**
	 * Emiti um Relat�rio do tipo RENEX. 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Gera um relat�rio com do corpo Docente, com o n�mero total de docentes por 
	 * grau de forma��o, por regime de trabalho e sexo.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar41(){
		tituloRelatorio = "N�mero total de docentes por grau de forma��o, por regime de trabalho e sexo";
		nomeRelatorio = "r_41_numero_total_docentes_formacao_regime_sexo";
		origemDados = Sistema.SIPAC;
		clear();
		validaAno = true;
		validaMes = true;
		return forward(JSP_FORM_MES_ANO);
	}
	
	/**
	 * Gera um relat�rio com do corpo Docente, com o n�mero total de docentes por grau de forma��o.
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar411(){
		tituloRelatorio = "N�mero total de docentes por grau de forma��o";
		nomeRelatorio = "r_411_docentes_formacao";
		origemDados = Sistema.SIPAC;
		clear();
		validaAno = true;
		validaMes = true;
		return forward(JSP_FORM_MES_ANO);
	}
	
	/**
	 * Gera um relat�rio com do corpo Docente, com o n�mero de docentes por sexo e faixa et�ria.
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar43(){
		tituloRelatorio = "N�mero de docentes por sexo e faixa et�ria";
		nomeRelatorio = "r_43_numero_docentes_sexo_faixa_etaria";
		origemDados = Sistema.SIPAC;
		clear();
		validaAno = true;
		validaMes = true;
		return forward(JSP_FORM_MES_ANO);
	}
	
	/**
	 * Gera um relat�rio com do corpo Docente, com o n�mero de docentes permanentes por sexo e faixa et�ria.
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar431(){
		tituloRelatorio = "N�mero de docentes permanentes por sexo e faixa et�ria";
		nomeRelatorio = "r_431_numero_docentes_sexo_faixa_etaria_permanentes";
		origemDados = Sistema.SIPAC;
		clear();
		validaAno = true;
		validaMes = true;
		return forward(JSP_FORM_MES_ANO);
	}
	
	/**
	 * Gera um relat�rio com do corpo Docente, com o n�mero total de docentes por categoria funcional e sexo.
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar47(){
		tituloRelatorio = "N�mero total de docentes por categoria funcional e sexo";
		nomeRelatorio = "r_47_numero_total_docentes_categoria_sexo";
		origemDados = Sistema.SIPAC;
		clear();
		validaAno = true;
		validaMes = true;
		return forward(JSP_FORM_MES_ANO);
	}
	
	/**
	 * Gera um relat�rio com do corpo Docente, com o n�mero de docentes por centro, departamento e forma��o.
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar48(){
		tituloRelatorio = "N�mero de docentes por centro, departamento e forma��o";
		nomeRelatorio = "r_48_numero_docentes_centro_formacao";
		origemDados = Sistema.SIPAC;
		clear();
		validaAno = true;
		validaMes = true;
		return forward(JSP_FORM_MES_ANO);
	}
	
	/**
	 * Gera um relat�rio com do corpo Docente, com o n�mero de T�cnicos Administrativos por sexo e faixa et�ria
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar54(){
		tituloRelatorio = "N�mero de T�cnicos Administrativos por sexo e faixa et�ria";
		nomeRelatorio = "r_54_numero_tecnicosadministrativos_sexo_faixa_etaria";
		origemDados = Sistema.SIPAC;
		clear();
		validaAno = true;
		validaMes = true;
		return forward(JSP_FORM_MES_ANO);
	}

	/**
	 * Gera um relat�rio com do corpo Docente, com o n�mero de T�cnicos Administrativos por n�vel de classifica��o.
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li> /portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String iniciar55(){
		tituloRelatorio = "N�mero de T�cnicos Administrativos por n�vel de classifica��o";
		nomeRelatorio = "r_55_numero_tecnicosadministrativos_nivel_classificacao";
		origemDados = Sistema.SIPAC;
		clear();
		validaAno = true;
		validaMes = true;
		return forward(JSP_FORM_MES_ANO);
	}
	
	/**
	 * Gera um relat�rio com do corpo Docente, com o n�mero de T�cnicos Administrativos e Docentes por Centro e Departamento.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/portais/rh_plan/abas/planejamento.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciar56(){
		tituloRelatorio = "N�mero de T�cnicos Administrativos e Docentes por Centro e Departamento";
		nomeRelatorio = "r_56_numero_tecnicosadministrativos_docentes_centro";
		origemDados = Sistema.SIPAC;
		clear();
		validaAno = true;
		validaMes = true;
		return forward(JSP_FORM_MES_ANO);
	}
	/**
	 * Gera um relat�rio Anal�ticos dos Alunos Ingressantes.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Gera um relat�rio Anal�ticos dos Alunos Concluintes.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Gera um relat�rioAnal�ticos dos Alunos Concluintes(formato antigo).
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Gera um relat�rio Anal�ticos dos Alunos Matriculados.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Gera um relat�rio Anal�ticos dos Alunos Evadidos.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/portais/rh_plan/abas/graduacao.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciar04(){
		tituloRelatorio = "Evas�es";
		nomeRelatorio = "relatorio_4";
		return iniciar();
	}
	/**
	 * Gera um relat�rio Anal�ticos dos Alunos Trancados.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Gera um relat�rio Anal�ticos dos Alunos N�o Matriculados.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/portais/rh_plan/abas/graduacao.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciar06(){
		tituloRelatorio = "N�o Matriculados";
		nomeRelatorio = "relatorio_6";
		return iniciar();
	}
	/**
	 * Gera um relat�rio Anal�ticos dos Alunos Ingresantes por outras formas.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Gera um relat�rio Anal�ticos dos Alunos .
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
     * M�todo para iniciar a gera��o do relat�rio, compartilhado por todos os tipos de relat�rio.
     * Redireciona para a pagina do formul�rio
     *
     * @return
     */
    private String iniciar() {
        clear();
        validaAno = true;
        return forward(JSP_FORM_ESTATISTICAS);
    }
    
	/**
     * M�todo para iniciar a gera��o do relat�rio, compartilhado por todos os tipos de relat�rio.
     * Redireciona para a pagina do formul�rio (Relat�rios em JSP)
     * @return
     */
    private String iniciarRelatoriosJSP() {
        clear();
        validaAno = true;
        return forward(JSP_SELECIONA_DADOS);
    }    
    
    /**
     * Gera o relat�rio conforme o indicador setado.<br/>
     * M�todo chamado pela seguinte JSP:
     * <ul>
     *    <li>/portais/rh_plan/relatorios/seleciona_dados.jsp</li>
     * </ul>
     * @return
     * @throws SegurancaException 
     */
    public String gerarRelatorioJSP() throws SegurancaException{
    	checkRole(SigaaPapeis.DAE);
       	// Validar campos do formul�rio
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
     * Gera Hist�rico ao clicar sobre um discente no relat�rio de concluintes.<br/>
     * M�todo chamado pela seguinte JSP:
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
     * Gera o relat�rio de concluintes.
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
     * Exibe os alunos referente ao total clicado no relat�rio 3.7.1<br/>
     * M�todo chamado pela seguinte JSP:
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
     * Auxilia na exibi��o da lista de alunos para utiliza��o nos relat�rios de taxa de conclus�o.
     *  <br />
	 * M�todo n�o invocado por JSPs.
	 * @return
     */
    public String exibirListaAlunos(){
    	if (!lista.isEmpty())
    		return forward(JSP_REL_02);
    	else
    		return null;
    }
    
    /**
     * Gera o relat�rio com informa��es Acad�micas, alunos concluintes nos cursos de gradua��o, por semestre, turno e g�nero.
     * <br />
	 * M�todo n�o invocado por JSPs.
	 *  @return
     */
    public String gerarRelatorio371JSP(){
 		tituloRelatorio = "3.7.1 - Alunos concluintes nos cursos de gradua��o, por semestre, turno e g�nero";

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
    			// Soma os valores nas sua respectivas vari�veis
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
     * Adiciona os valores totais para a lista que ser� exibida no relat�rio 371
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
		// 1� SEMESTRE
		// Diurno 
		// Masculino
		linha.put("DM1", DM1);
		// Feminino
		linha.put("DF1", DF1);
		// Total Diurno 1� Semestre
		linha.put("TD1", DM1 + DF1);
		
		// Noturno
		// Masculino
		linha.put("NM1", NM1);
		// Feminino
		linha.put("NF1", NF1);
		// Total Noturno 1� Semestre    				
		linha.put("TN1", NM1 + NF1);
		
		// Total Masculino 1� Semestre
		linha.put("TM1", DM1 + NM1);
		// Total Feminino 1� Semestre    				
		linha.put("TF1", DF1 + NF1);
		
		// Total Geral do 1� Semestre
		linha.put("TG1", DM1 + NM1 + DF1 + NF1);
		
		// 2� SEMESTRE
		// Noturno
		// Masculino    				
		linha.put("DM2", DM2);
		// Feminino
		linha.put("DF2", DF2);
		// Total Diurno 2� Semestre
		linha.put("TD2", DM2 + DF2);
		
		// Noturno
		// Masculino
		linha.put("NM2", NM2);
		// Feminino
		linha.put("NF2", NF2);
		// Total Noturno 2� Semestre
		linha.put("TN2", NM2 + NF2);
		
		// Total Masculino 2� Semestre
		linha.put("TM2", DM2 + NM2);
		// Total Feminino 2� Semestre   
		linha.put("TF2", DF2 + NF2);
		
		// Total Geral do 2� Semestre
		linha.put("TG2", DM2 + NM2 + DF2 + NF2);
		
		// Total Geral Masculino
		linha.put("TM", DM1 + NM1 + DM2 + NM2);
		// Total Geral Feminino    				
		linha.put("TF", DF1 + NF1 + DF2 + NF2);
		// Total Geral     				
		linha.put("TG", DM1 + NM1 + DF1 + NF1 + DM2 + NM2 + DF2 + NF2);
	}

    /**
     * Realizar a gera��o do relat�rio, de acordo com os crit�rios selecionados
     *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
    	// Validar campos do formul�rio
        if ( !validate() ) {
            return null;
        }

        // Gerar relat�rio
        Connection con = null; 
        try {
        	
            // Popular par�metros do relat�rio
            HashMap<String, Object> parametros = new HashMap<String, Object>();
//            parametros.put("SUBREPORT_DIR", JasperReportsUtil.getReportSIGAA(nomeRelatorio + ".jasper") );
            parametros.put("unidade", unidade.getId() );
            parametros.put("mes", mes );
            parametros.put("ano", ano );
            parametros.put("anoInicio", anoInicio );
            parametros.put("anoFim", anoFim );
            parametros.put("subSistema", getSubSistema().getNome());
	        parametros.put("subSistemaLink", getSubSistema().getLink());

            // Preencher relat�rio
            con = getConexao();
            JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA(nomeRelatorio + ".jasper") , parametros, con );
            if (prt.getPages().size() == 0) {
				addMensagemWarning("O relat�rio gerado n�o possui p�ginas.");
				return null;
			}
            // Exportar relat�rio de acordo com o formato escolhido
            String nomeArquivo = nomeRelatorio+"."+formato;
            JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), formato);
            FacesContext.getCurrentInstance().responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
            notifyError(e);
            addMensagemErro("Ocorreu um erro durante a gera��o deste relat�rio. Por favor,contacte o suporte atrav�s do \"Abrir Chamado\"");
            return null;
        } finally {
            Database.getInstance().close(con);
        }

        return null;
    }
    
    /**
     * Retorna a conex�o de acordo com a origem dos dados especifica
     * de cada relat�rio
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
     * Direciona para a p�gina para se verificar o uso da turma virtual, buscando pelo ano 
     * e pelo per�odo.
     * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Serve para direcionar o usu�rio para a busca espec�fica de acordo com o tipo de relat�rio que o mesmo selecionou na
	 * <br />
	 * tela de busca. E de acordo com os par�metros passados, para a busca. 
	 *   
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
				addMensagemErro("Tipo de Relat�rio: Campo obrigat�rio n�o informado");
			}
			if (ano == null || periodo == null ) {
				addMensagemErro("Ano-Per�odo: Campo obrigat�rio n�o informado");
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
		addMensagemErro("N�o foi encontrado nenhum registro com os dados passados.");
		validaAnoPeriodo = true;
		validaTipoRelatorio = true;
		return forward(JSP_FORM_TURMA_VIRTUAL);
    }

    /**
     * Validar dados do formul�rio
     *
     * @return
     */
    private boolean validate() {
    	ListaMensagens erros = new ListaMensagens();
    	if(validaAnoPeriodo) {
    		if (ano == null || periodo == null) {
    			addMensagemErro("Ano e Periodo s�o campos obrigat�rios");
    		}
    	}
    	
    	if (validaPeriodoAno) {
	        if (anoInicio == null)
	        	addMensagemErro("O campo Ano In�cio � obrigat�rio");
	        if (anoFim == null)
	        	addMensagemErro("O campo Ano Fim � obrigat�rio");
	        if (hasErrors())
	        	return false;
	      	ValidatorUtil.validaInt(anoInicio, "Ano In�cio", erros);
	      	ValidatorUtil.validaInt(anoFim, "Ano Fim", erros);
	      	if (anoInicio > CalendarUtils.getAnoAtual() || anoFim > CalendarUtils.getAnoAtual())
	      		erros.addErro("O ano informado n�o pode ser maior ao ano atual");
	      	if (anoInicio > anoFim )
	      		erros.addErro("O ano Inicial deve ser menor que o ano Final");
    	}
    	
    	if (validaAno) {
	        if (ano == null)
	        	addMensagemErro("O campo Ano � obrigat�rio");
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