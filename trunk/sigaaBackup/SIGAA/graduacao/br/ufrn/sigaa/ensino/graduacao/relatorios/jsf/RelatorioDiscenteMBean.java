/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 31/05/07
 *
 */

package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMaxValue;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;
import br.ufrn.sigaa.arq.dao.graduacao.IndiceAcademicoDao;
import br.ufrn.sigaa.arq.dao.graduacao.MatrizCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.RelatorioDiscenteSqlDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.TipoFiltroCurso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademico;
import br.ufrn.sigaa.ensino.graduacao.dao.RelatorioDiscenteGraduacaoSqlDao;
import br.ufrn.sigaa.ensino.graduacao.jsf.EstruturaCurricularMBean;
import br.ufrn.sigaa.ensino.graduacao.relatorios.dao.RelatorioDiscenteGraduacaoDao;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.jsf.UnidadeMBean;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivo;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/** 
 * Controller responsável pela geração de diversos relatórios de discentes.
 * @author Eric Moura
 */
@Component("relatorioDiscente") @Scope("request")
public class RelatorioDiscenteMBean extends AbstractRelatorioGraduacaoMBean {

	// constantes
	/** Ano mínimo a ser informado pelo usuário em parâmetros do relatório. */
	private static final int ANO_MINIMO = 1960;

	// Dados do relatório.
	
	/** Representa os Laureados para o UC Lista de Laureados. */
	private List<Map<String,Object>> listaDiscente = new ArrayList<Map<String,Object>>();
	
	/** Representa os Discentes com IEAN menor que 600, mas que tiveram maior IEAN do curso. */
	private List<Map<String,Object>> listaDiscenteNaoAtingiuIndiceLaureadoMinimo = new ArrayList<Map<String,Object>>();
	
	/** Dados do relatório de Discentes Bolsas. */
	private List<DiscentesBolsas> listaDiscenteBolsa = new ArrayList<DiscentesBolsas>();

	/** Lista de discentes encontrados na geração de alguns relatórios. */
	private Collection<Discente> discentes;
	
	/** Dados do relatório de trancamentos por disciplina. */
	private Map<String, List<LinhaRelatorioQuantitativoTrancamentos>> relatorioTrancamento;

	/** Listagem com o resultado da consulta que será exibida no relatório */
	private List<Map<String,Object>> lista = new ArrayList<Map<String,Object>>();
	
	//Constantes de navegação - paginas dos relatórios
	/** Indica se o filtro de busca por status foi selecionado. */
	private boolean filtroStatus;
	/** Indica se o filtro de busca por unidade foi selecionado. */
	private boolean filtroUnidade;
	/** Indica se o usuário deseja realizar a busca para todos os centros. */
	private boolean todosCentros;
	/** Indica se os discentes mostrados sáo somente os matriculados atualmente. */
	private boolean matriculados;
	/** Indica se a ordem do relatório será ordenado pelo nome dos discentes ou pelo nome dos orientadores.*/
	private boolean ordenarPorOrientador;
	/** Ano a que se refere o relatório. */
	private Integer ano = getCalendarioVigente().getAno();
	/** Ano final a que se refere o relatório. */
	private Integer anoFinal = null;
	/** Período a que se refere o relatório. */
	private Integer periodo = getCalendarioVigente().getPeriodo();
	/** Data de início do período escolhido. */
	private Date dataInicio = new Date();
	/** Data final do período escolhido. */
	private Date dataFinal = new Date();
	/** Chamada que compõe o código de migração da COMPERVE de um discente. */
	private Integer chamada;
	/** Convocação a qual o discente recebeu para realizar seu cadastramento. */
	private Integer convocacao;
	/** Processo Seletivo da migração da COMPERVE de um discente. */
	private Integer processoSeletivo;
	/** Descrição da chamada que compõe o código de migração da COMPERVE de um discente. */
	private String descricaoChamada;
	/** Descrição do processo seletivo que compõe o código de migração da COMPERVE de um discente. */
	private String descricaoProcessoSeletivo;
	
	/** índice acadêmico selecionado para gerar a lista de alunos laureados */
	private IndiceAcademico indiceSelecionado;
	
	/** valor mínimo do índice acadêmico selecionado para gerar a lista de alunos laureados */
	private double valorMinimo;
	
	/** Índices Acadêmicos utilizados na geração do relatório. */
	private Collection<IndiceAcademico> indices;
	
	/** Indica que serão totalizado somente cursos que possuem convênios. */
	private boolean somenteCursosConvenio;
	
	/** Restringe os dados do relatório à somente cursos desta modalidade de educação. */
	private ModalidadeEducacao modalidadeEnsino;
	
	/** Restringe os dados do relatório à somente cursos desta unidade. */
	private Unidade unidade;

	/** Sumário de índices acadêmicos */
	private List<Map<String,Object>>  sumarioIndices;

	/** Sumário de trancamentos acadêmicos */
	private List<Map<String, Object>> sumarioTrancamento;

	/** Lista de municípios do relatório. */
	private Set<String> municipios;
	
	//Listas

	/** Link do relatório de alunos ingressantes. */
	private static final String JSP_RELATORIO_INGRESSANTES = "lista_ingressantes.jsp";
	/** Link do relatório de percentual de carga horária pelo aluno. */
	private static final String JSP_RELATORIO_PERCENTUAL_CH= "lista_percentual_ch.jsp";
	/** Link do relatório de listagem de alunos para ser usado em eleições. */
	private static final String JSP_RELATORIO_ELEICAO = "lista_eleicao.jsp";
	/** Link do relatório de lista de contatos de alunos. */
	private static final String JSP_RELATORIO_CONTATOS_ALUNOS = "lista_contatos_alunos.jsp";
	/** Link do relatório de concluintes. */
	private static final String JSP_RELATORIO_CONCLUINTES = "lista_concluintes.jsp";
	/** Link do relatório de alunos ativos e matriculados. */
	private static final String JSP_RELATORIO_MATRICULADOS = "lista_matriculados.jsp";
	/** Link do relatório de alunos reprovados e trancados. */
	private static final String JSP_RELATORIO_REPROVADOS_TRANCADOS = "lista_reprovados_trancados.jsp";
	/** Link do relatório de alunos ativos com seus respectivos prazos de conclusão. */
	private static final String JSP_RELATORIO_PRAZO_CONCLUSAO = "lista_prazo_conclusao.jsp";
	/** Link do relatório de alunos de acordo com seu tipo de saída. */
	private static final String JSP_RELATORIO_TIPO_SAIDA = "lista_tipo_saida.jsp";
	/** Link do relatório de alunos com registro em uma disciplina. */
	private static final String JSP_RELATORIO_REGISTRO_DISCIPLINA = "lista_registro_disciplina.jsp";
	/** Link do relatório de alunos com trancamenteo em um determinado componente. */
	private static final String JSP_RELATORIO_TRANCAMENTOS_COMPONENTE = "lista_trancamentos_componente.jsp";
	/** Link do relatório de alunos matriculados em disciplina com carga horária de estágio. */
	private static final String JSP_RELATORIO_MATRICULADO_ESTAGIO = "lista_matriculado_estagio.jsp";
	/** Link do relatório de alunos por Curso/Centro com prazo máximo de conclusão em um ano semestre por carga horária total de curso por carga horária integralizada. */
	private static final String JSP_RELATORIO_PRAZO_CONCLUSAO_CH = "lista_prazo_conclusao_ch.jsp";
	/** Link do relatório de alunos por cidade de residência. */
	private static final String JSP_RELATORIO_CIDADE_RESIDENCIA = "lista_cidade_residencia.jsp";
	/** Link do relatório de alunos por carga horária detalhada. */
	private static final String JSP_RELATORIO_CH_DETALHADA = "lista_ch_detalhada.jsp";
	/** Link do relatório de alunos matriculados em uma determinada atividade. */
	private static final String JSP_RELATORIO_MATRICULADO_ATIVIDADE = "lista_matriculado_atividade.jsp";
	/** Link do relatório de alunos com prazo de conclusão no semestre atual. */
	private static final String JSP_RELATORIO_PRAZO_SEMESTRE_ATUAL = "lista_prazo_semestre_atual.jsp";
	/** Link do relatório de alunos laureados. */
	private static final String JSP_RELATORIO_LAUREADOS = "lista_laureados.jsp";
	/** Link do relatório de alunos concluídos com créditos pendentes. */
	private static final String JSP_RELATORIO_CONCLUIDO_CREDITO_PENDENTE = "lista_concluido_credito_pendente.jsp";
	/** Link do relatório de alunos de acordo com o motivo de trancamento. */
	private static final String JSP_RELATORIO_MOTIVO_TRANCAMENTO = "lista_motivo_trancamento.jsp";
	/** Link do relatório de alunos vinculados a uma estrutura curricular. */
	private static final String JSP_RELATORIO_VINVCULADOS_ESTRUTURA = "lista_vinculados_estrutura.jsp";
	/** Link do relatório de insucessos de alunos em uma disciplina. */
	private static final String JSP_RELATORIO_INSUCESSOS = "lista_insucessos.jsp";
	/** Link do formulário de seleção de parâmetros para o relatório de alunos reprovados. */
	private static final String JSP_SELECIONA_RELATORIO_ALUNOS_REPROVADOS = "seleciona_alunos_reprovados.jsp";
	/** Link do relatório de alunos reprovados. */
	private static final String JSP_RELATORIO_ALUNOS_REPROVADOS = "lista_alunos_reprovados.jsp";
	/** Link do formulário de seleção de parâmetros para o relatório de alunos não matriculados. */
	private static final String JSP_SELECIONA_RELATORIO_ALUNOS_NAO_MATRICULADOS = "seleciona_alunos_nao_matriculados.jsp";
	/** Link do relatório de alunos ativos não matriculados. */
	private static final String JSP_RELATORIO_ALUNOS_NAO_MATRICULADOS = "lista_alunos_nao_matriculados.jsp";
	/** Link do formulário de seleção de parâmetros para o relatório de alunos matriculados em atividades, mas não renovados. */
	private static final String JSP_SELECIONA_RELATORIO_ALUNOS_MATRICULADOS_EM_ATIVIDADES_NAO_RENOVADAS = "seleciona_alunos_matriculados_em_atividades_nao_renovadas.jsp";
	/** Link do relatório de alunos matriculados em atividades não renovadas. */
	private static final String JSP_RELATORIO_ALUNOS_MATRICULADOS_EM_ATIVIDADES_NAO_RENOVADAS = "lista_alunos_matriculados_em_atividades_nao_renovadas.jsp";
	/** Link do formulário de seleção de parâmetros para o relatório de prazo máximo de bolsas do aluno. */
	private static final String JSP_SELECIONA_RELATORIO_PRAZO_MAXIMO_BOLSA = "seleciona_prazo_maximo_bolsa.jsp";
	/** Link do relatório de prazo máximos de bolsas do aluno. */
	private static final String JSP_RELATORIO_PRAZO_MAXIMO_BOLSA = "lista_prazo_maximo_bolsa.jsp";
	/** Link para geração de um relatório de alunos já na UFRN, mas ingressantes em outros cursos. */
	private static final String JSP_SELECIONA_ALUNOS_INGRESSANTES_OUTRO_CURSO = "seleciona_aluno_ingressante_outro_curso.jsp";
	/** Link do relatório de alunos da UFRN ingressantes em outro curso. */
	private static final String JSP_RELATORIO_ALUNOS_INGRESSANTES_OUTRO_CURSO = "lista_alunos_ingressantes_outro_curso.jsp";
	/** Link do relatório de discentes com mobilidade estudantil. */
	private static final String JSP_RELATORIO_MOBILIDADE_ESTUDANTIL = "lista_mobilidade_estudantil.jsp";
	/** Link do formulário para geração do relatório de discentes especiais e seus componentes por ano e período de matrícula. */
	private static final String JSP_SELECIONA_RELATORIO_DISCENTES_ESPECIAIS = "seleciona_discentes_especiais.jsp";
	/** Link do relatório de discentes especiais e seus componentes por ano e período de matrícula.. */
	public static final String JSP_RELATORIO_DISCENTES_ESPECIAIS = "lista_discentes_especiais.jsp";
	/** Link do formulário que gerar o relatório de discentes com mais de um vinculo ativo. */
	public static final String JSP_SELECIONA_ALUNOS_MAIS_VINCULOS_ATIVOS = "seleciona_discentes_mais_vinculos_ativos.jsp";
	/** Link do relatório de discentes com mais de um vinculo ativo. */
	public static final String JSP_RELATORIO_ALUNOS_MAIS_VINCULOS_ATIVOS = "lista_discentes_mais_vinculos_ativos.jsp";
	/** Link do fomulário de discentes e seus respectivos orientadores. */
	public static final String JSP_SELECIONA_ALUNOS_RESPECTIVOS_ORIENTADORES = "seleciona_discentes_orientadores.jsp";
	/** Link do relatório de discentes e seus respectivos orientadores. */
	public static final String JSP_RELATORIO_ALUNOS_RESPECTIVOS_ORIENTADORES = "relatorio_discentes_orientadores.jsp";
	
	/** String para armazenar ao texto da chamada que será concatenado a ordem da chamada.*/
	private static final String TEXTO_CHAMADA = "ª Chamada";
	
	
	//Quantitativos
	/** Link do relatório quantitativo de alunos concluintes. */
	private static final String JSP_RELATORIO_QUANT_CONCLUINTES = "quantitativo_concluintes.jsp";
	/** Link do relatório quantitativo de alunos matriculados. */
	private static final String JSP_RELATORIO_QUANT_MATRICULADOS = "quantitativo_matriculados.jsp";
	/** Link do relatório quantitativo de alunos egressos por sexo. */
	private static final String JSP_RELATORIO_QUANT_SEXO_EGRESSO = "quantitativo_sexo_egresso.jsp";
	/** Link do relatório quantitativo de alunos ingressos por sexo. */
	private static final String JSP_RELATORIO_QUANT_SEXO_INGRESSO = "quantitativo_sexo_ingresso.jsp";
	/** Link do relatório quantitativo de alunos cadastrados no vestibular sem matrícula. */
	private static final String JSP_RELATORIO_QUANT_VEST_SEM_MAT = "quantitativo_vest_sem_mat.jsp";
	/** Link do relatório quantitativo de alunos de pró-básica concluídos. */
	private static final String JSP_RELATORIO_QUANT_PROBASICA_CONCLUIDO = "quantitativo_probasica_concluido.jsp";
	/** Link do relatório quantitativo de alunos de pró-básica matriculados. */
	private static final String JSP_RELATORIO_QUANT_PROBASICA_MATRICULADO = "quantitativo_probasica_matriculado.jsp";
	/** Link do relatório quantitativo de alunos de pró-básica sem matrícula. */
	private static final String JSP_RELATORIO_QUANT_PROBASICA_SEM_MAT = "quantitativo_probasica_sem_matricula.jsp";
	/** Link do relatório quantitativo de alunos graduando por curso. */
	private static final String JSP_RELATORIO_QUANT_GRADUANDO = "quantitativo_graduando.jsp";
	/** Link do relatório quantitativo de solicitações de trancamento de matrícula. */
	private static final String JSP_RELATORIO_QUANT_MOTIVO_TRANCAMENTO = "quantitativo_motivo_trancamento.jsp";
	/** Link do relatório quantitativo de alunos reprovados e desnivelados. */
	private static final String JSP_RELATORIO_QUANT_REPROVADOS_DESNIVELADOS = "reprovados_desnivelados.jsp";
	/** Link do relatório quantitativo de trancamentos de disciplina. */
	private static final String JSP_RELATORIO_QUANT_TRANCAMENTO_REUNI = "discente/quantitativo_trancamento_disciplina.jsp";
	/** Link do relatório quantitativo de turmas não consolidadas por departamento. */
	private static final String JSP_RELATORIO_QUANT_TURMA_DEPARTAMENTO = "discente/quantitativo_turma_departamento.jsp";
	/** Link do relatório quantitativo de alunos regulares. */
	private static final String JSP_RELATORIO_QUANT_TOTAL_ALUNOS_REGULARES = "discente/quantitativo_aluno_regular.jsp";
	/** Link do relatório quantitativo de alunos regulares por curso. */
	private static final String JSP_RELATORIO_QUANT_TOTAL_ALUNOS_REGULARES_CURSO = "discente/quantitativo_aluno_regular_curso.jsp";
	
	/** Construtor padrão. */
	public RelatorioDiscenteMBean(){
		initObj();
		setAmbito("discente/");
		indiceSelecionado = new IndiceAcademico();
	}

	/**
	 * Carrega os objetos com os títulos dos relatórios.
	 * <br />
	 * Método não invocado por JSPs.
	 *
	 * @throws DAOException
	 */
	@Override
	public void carregarTituloRelatorio() throws DAOException{
		super.carregarTituloRelatorio();
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			if (!isFiltroCentro() && centro != null) centro.setId(0);
			if(centro != null)
				if (centro.getId()!=0)
					centro = dao.findByPrimaryKey(centro.getId(), Unidade.class);
				else
					centro.setNome("TODOS");
			
			if (!isFiltroDepartamento() && departamento != null) departamento.setId(0);
			if(departamento != null)
				if (departamento.getId()!=0)
					departamento = dao.findByPrimaryKey(departamento.getId(), Unidade.class);
				else
					departamento.setNome("TODOS");
	
			if (!isFiltroCurso() && curso != null) curso.setId(0);
			if(curso != null)
				if (curso.getId()!=0)
					curso = dao.findByPrimaryKey(curso.getId(), Curso.class);
				else
					curso.setNome("TODOS");
	
			if(disciplina != null)
				if(disciplina.getId()!=0 && !disciplina.getDetalhes().getNome().equals(""))
					disciplina = dao.findByPrimaryKey(disciplina.getId(), ComponenteCurricular.class);
				else {
					disciplina.setId(0);
					disciplina.setNome("TODOS");
				}
			
			if(discente != null)
				if(discente.getId()!=0)
					discente = dao.findByPrimaryKey(discente.getId(), Discente.class);
				else
					discente.getPessoa().setNome("TODOS");
	
			if (!isFiltroIngresso() && formaIngresso != null) formaIngresso.setId(0);
			if(formaIngresso != null)
				if(formaIngresso.getId()!=0)
					formaIngresso = dao.findByPrimaryKey(formaIngresso.getId(), FormaIngresso.class);
				else
					formaIngresso.setDescricao("TODOS");
		} finally {
			dao.close();
		}

	}

	/**
	 * Repassa para a view, o resultado da consulta da lista de discentes ingressantes.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_ingressantes.jsp</li></ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaIngressante() throws DAOException{
		if(idMatrizCurricular!=null)
			matrizCurricular.setId(idMatrizCurricular);
		if(idCurso!=null) {
			matrizCurricular.getCurso().setId(idCurso);
			curso = new Curso(idCurso);
		}
		if(centro.getId() != 0)
			matrizCurricular.getCurso().getUnidade().setId(centro.getId());
		
		carregarTituloRelatorio();
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findListaDiscenteIngressante(ano, periodo, status, formaIngresso.getId(), matrizCurricular, filtros);
			carregarFormaIngresso();
		} finally {
			dao.close();
		}
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_INGRESSANTES);

	}
	
	/**
	 * Repassa para a view, o resultado da consulta da lista de discentes com mais de um vinculo ativo
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_discentes_mais_vinculos_ativos.jsp</li></ul>
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public String gerarRelatorioDiscentesMaisVinculosAtivos() throws DAOException, SegurancaException{
	
		checkRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE);
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		if( ano == null )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano Ingresso");
		else
			listaDiscente = dao.findListaAlunoMaisVinculoAtivo(ano);
		
		if( isEmpty(listaDiscente) )
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS, "Discentes com mais de um vinculo ativo");
		
		if( hasErrors() )
			return null;

		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_ALUNOS_MAIS_VINCULOS_ATIVOS);

	}
	
	/**
	 * Repassa para a view, o resultado da consulta de Percentual de Carga Horária Cumprida pelo Aluno.
	 *<br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_percentual_ch.jsp </li></ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaPercentualCHAluno() throws DAOException{
		carregarTituloRelatorio();
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findListaPercentualCHAluno(curso);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_PERCENTUAL_CH);

	}
	
	/**
	 * Repassa para a view o resultado da consulta da lista de alunos para ser usada
	 * em alguma eleição.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_eleicao.jsp </li></ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaEleicao() throws DAOException{

		carregarTituloRelatorio();

		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();

		//teste para o coordenador buscar apenas aluno do seu curso
		if( ( getAcessoMenu().isCoordenadorCursoGrad() || getAcessoMenu().isSecretarioGraduacao() ) && isPortalCoordenadorGraduacao()){
			filtros.put(AbstractRelatorioSqlDao.CURSO, true);
			matrizCurricular.setId(idMatrizCurricular);
			curso.setId( getCursoAtualCoordenacao().getId());
			idCurso = getCursoAtualCoordenacao().getId();
			filtros.put(AbstractRelatorioSqlDao.ATIVO, isFiltroAtivo());
		} if( ( getAcessoMenu().isAlgumUsuarioStricto() || getAcessoMenu().isSecretariaPosGraduacao() ) && isPortalCoordenadorStricto() ){
			
			if( isEmpty( idCurso ) ){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Curso");
				return null;
			}
			curso.setId(idCurso);
			filtros.put(AbstractRelatorioSqlDao.CURSO, true);
			
		}else {
			
			boolean erro = false;
			
			filtros.put(AbstractRelatorioSqlDao.ATIVO, isFiltroAtivo());
			if (filtros.get(AbstractRelatorioSqlDao.MATRIZ_CURRICULAR)){
				if( matrizCurricular == null || matrizCurricular.getId() == 0){
					addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Matriz curricular");
					erro = true;
				} else
					matrizCurricular.setId(idMatrizCurricular);
			}
			
			if (filtros.get(AbstractRelatorioSqlDao.CURSO)) {
				if( isEmpty( idCurso ) ){
					addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Curso");
					erro = true;
				}
				curso.setId(idCurso);
			}
			
			if (erro)
				return null;
		}
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findListaEleicao(getNivelEnsino(), curso, matrizCurricular, ano, periodo, filtros);
			if(filtros.get(9)==true){
				matriculados = true;
			}
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_ELEICAO);
	}
	
	/**
	 * Carrega as unidades caso for PPG, senão atribui a do coordenador. 
	 */
	private void carregarUnidade(){
		//teste para verificar se é ppg. e se foi informada a unidade (programa)
		if ( isPortalPpg() ){		
			if (filtroUnidade || matrizCurricular.getCurso().getUnidade().getId() != 0){
				if( matrizCurricular == null || matrizCurricular.getCurso().getUnidade().getId() == 0)					
					addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Programa");
				 else {
					matrizCurricular.getCurso().getUnidade().setId(matrizCurricular.getCurso().getUnidade().getId());
					
					UnidadeMBean unidadeBean = getMBean("unidade");
					
					List<SelectItem> unidades = (List<SelectItem>) unidadeBean.getAllProgramaPosCombo();
					for (SelectItem u : unidades){
						if (u.getValue().toString().equals(""+matrizCurricular.getCurso().getUnidade().getId()))
						   matrizCurricular.getCurso().getUnidade().setNome(u.getLabel());
					}					
					matrizCurricular.getCurso().getUnidade().setNome( matrizCurricular.getCurso().getUnidade().getNome());				
				}
			} else {
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Programa");
			}
		} else {
			matrizCurricular.getCurso().getUnidade().setId(getProgramaStricto().getId());
			matrizCurricular.getCurso().getUnidade().setNome(getProgramaStricto().getNome());
		}			
	}
	
	/**
	 * Seta data incio e fim de acordo com o calendario acadêmico
	 * @throws DAOException
	 */
	private void carregarDataInicioFim() throws DAOException {
		CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendario(ano, periodo, getGenericDAO().refresh( matrizCurricular.getCurso().getUnidade()), null, null, null, null);

		if (calendario != null) {
			dataInicio = calendario.getInicioPeriodoLetivo();
			dataFinal = calendario.getFimPeriodoLetivo();
		} else {
			if (periodo == 1) {
				dataInicio = CalendarUtils.createDate(1, 1, CalendarUtils.getAnoAtual());
				dataFinal = CalendarUtils.createDate(30, 6, CalendarUtils.getAnoAtual());
			} else {
				dataInicio = CalendarUtils.createDate(1, 7, CalendarUtils.getAnoAtual());
				dataFinal = CalendarUtils.createDate(31, 12, CalendarUtils.getAnoAtual());
			}
		}
	}
	
	
	/**
	 * Repassa para a view, o resultado da consulta de Lista de contatos de Alunos.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_contato.jsp </li></ul>
	 * @return
	 */
	public String gerarRelatorioContatosAlunos() throws DAOException{
		carregarUnidade();
		
		if (! hasErrors() ){
			RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
			
			try {
				listaDiscente = dao.findListaContatoDiscente(status, matrizCurricular.getCurso().getUnidade().getId());
				for ( Map<String,Object>vlistaDiscente : listaDiscente){
					vlistaDiscente.put("nivel", NivelEnsino.getDescricao(vlistaDiscente.get("nivel").toString().toCharArray()[0]));
				}
			} finally {
				dao.close();
			}
			
			return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_CONTATOS_ALUNOS);
		} 
		return null;			
	}
	
	/**
	 * Redireciona para o form de Seleção de parâmetro do relatório de alunos reprovados.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> /sigaa.war/stricto/menu_coordenador.jsp (Portal Stricto) </li>
	 *  <li> /sigaa.war/stricto/menus/relatorios.jsp (PPG) </li>
	 * </ul>   
	 * @return
	 */
    public String iniciaRelatorioAlunosReprovados(){
		CalendarioAcademico cal = getCalendarioVigente();
		ano = cal.getAno();
		periodo = cal.getPeriodo();    	
    	return carregarSelecaoRelatorio(JSP_SELECIONA_RELATORIO_ALUNOS_REPROVADOS);
    }	
    
	/**
	 * Repassa para a view, o resultado da consulta de Lista Alunos Reprovados.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_alunos_reprovados.jsp </li></ul>
	 * @return
	 */    
    public String gerarRelatorioAlunosReprovados() throws ArqException{  	    	
    	ValidatorUtil.validateRequired(ano, "Ano", erros);
		ValidatorUtil.validateRequired(periodo, "Período", erros);

		carregarUnidade();
		
		if (ano <= 0 || periodo <= 0) {
			addMensagemErro("Informe Ano e Período válidos.");
			return null;
		}
		
		if (! hasErrors() ){
			RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
			try {				
				listaDiscente = dao.findAlunosReprovados(ano, periodo, matrizCurricular.getCurso().getUnidade().getId());
				if (listaDiscente.size() == 0)
					addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);					
				else {
					for ( Map<String,Object>vlistaDiscente : listaDiscente){
						vlistaDiscente.put("nivel", NivelEnsino.getDescricao(vlistaDiscente.get("nivel").toString().toCharArray()[0]));
					}							
					return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_ALUNOS_REPROVADOS);
				}
					
			} finally {
				if (dao != null)
					dao.close();						
			}
		}
		return null;         
    }
    
    /**
     * Utilizado para decidir pode ou não mostrar ma coluna de table na view(JSP)
     * 
     * Chamado por:
     * sigaa.war/graduacao/relatorios/discente/lista_alunos_reprovados.jsp
     * 
     * @return
     */
    public boolean isMostrarTipoBolsaEmRelatorioAlunosReprovados() {    	
    	if( Sistema.isSipacAtivo() ){
    		return true;
    	} else {
    		return false;    	
    	}
    }
    
    
	/**
	 * Redireciona para o form de Seleção de parâmetro do relatório de alunos não matriculados.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> /sigaa.war/stricto/menu_coordenador.jsp (Portal Stricto) </li>
	 *  <li> /sigaa.war/stricto/menus/relatorios.jsp (PPG) </li>
	 * </ul>   
	 * @return
	 */
    public String iniciaRelatorioAlunosAtivosNaoMatriculadosBolsa(){
		CalendarioAcademico cal = getCalendarioVigente();
		ano = cal.getAno();
		periodo = cal.getPeriodo();    	
    	return carregarSelecaoRelatorio(JSP_SELECIONA_RELATORIO_ALUNOS_NAO_MATRICULADOS);
    }
    
    /**
     * Direciona o usuário para a tela de definição dos parêmetros utilizados na geração do relatório de discentes especiais.
     * <br />
	 *  Método chamado pela seguinte JSP:
	 * <ul>
	 * <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * <li>/sigaa.war/stricto/menus/relatorios.jsp</li>
	 * </ul> 
     * 
     * @return
     */
    public String iniciarRelatorioDiscentesEspeciais() {
    	CalendarioAcademico cal = getCalendarioVigente();
		ano = cal.getAno();
		periodo = cal.getPeriodo();    	
    	return carregarSelecaoRelatorio(JSP_SELECIONA_RELATORIO_DISCENTES_ESPECIAIS);
	}
 
	/**
	 * Repassa para a view, o resultado da consulta de Lista Alunos Ativos não matriculados.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_alunos_reprovados.jsp </li></ul>
	 * @return
	 */    
    public String gerarRelatorioAlunosAtivosNaoMatriculadosBolsa() throws ArqException{  	    	

    	ValidatorUtil.validateRequired(ano, "Ano", erros);
		ValidatorUtil.validateRequired(periodo, "Período", erros);    	
    	
		carregarUnidade();			
		
		if (! hasErrors() ){
			RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
			try {				
				listaDiscente = dao.findAlunosNaoMatriculadosBolsa(ano, periodo, matrizCurricular.getCurso().getUnidade().getId());
				if (listaDiscente.size() == 0)
					addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);					
				else {
					for ( Map<String,Object>vlistaDiscente : listaDiscente){
						vlistaDiscente.put("nivel", NivelEnsino.getDescricao(vlistaDiscente.get("nivel").toString().toCharArray()[0]));
					}					
					return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_ALUNOS_NAO_MATRICULADOS);
				}
					
			} finally {
				if (dao != null)
					dao.close();						
			}
		}
		return null;         
    }	 
    
    
    /**
	 * Repassa para a view, o resultado da consulta dos Alunos Matriculados
	 * em Atividades Não Renovadas.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_alunos_matriculados_em_atividades_nao_renovadas.jsp </li></ul>
	 * @return
	 */    
    public String gerarRelatorioAlunosMatriculadosEmAtividadesNaoRenovadas() throws ArqException{  	    	

    	ValidatorUtil.validateRequired(ano, "Ano", erros);
		ValidatorUtil.validateRequired(periodo, "Período", erros);    	
    	
		carregarTituloRelatorio();			
		
		if (! hasErrors() ){
			
			Unidade unidade = null;
			Collection<Character> niveis = new ArrayList<Character>();
			RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
			
			if (isPortalPpg()){
				niveis = NivelEnsino.getNiveisStricto();
			}else{
				if( isUserInRole( SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS ) && isEmpty(disciplina) )
					unidade = getProgramaStricto();
				if( isUserInRole( SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS )){
					niveis = NivelEnsino.getNiveisStricto();
				}
				listaDiscente = dao.findAlunosMatriculadosEmAtividadesNaoRenovadas(disciplina, ano, periodo, unidade , niveis );
			}
		}	
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_ALUNOS_MATRICULADOS_EM_ATIVIDADES_NAO_RENOVADAS);
    }
    
	/**
	 * Redireciona para o form de Seleção de parâmetro do relatório de prazo máximo de bolsas dos alunos.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> /sigaa.war/stricto/menu_coordenador.jsp (Portal Stricto) </li>
	 *  <li> /sigaa.war/stricto/menus/relatorios.jsp (PPG) </li>
	 * </ul>   
	 * @return
	 */
    public String iniciaRelatorioPrazoMaximoBolsaAlunos(){
		CalendarioAcademico cal = getCalendarioVigente();
		ano = cal.getAno();
		periodo = cal.getPeriodo();    	
    	return carregarSelecaoRelatorio(JSP_SELECIONA_RELATORIO_PRAZO_MAXIMO_BOLSA);
    }	   
    
    
	/**
	 * Repassa para a view, o resultado da consulta do Relatório de Prazo máximo de bolsas dos alunos.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_prazo_maximo_bolsa.jsp </li></ul>
	 * @return
	 */    
    public String gerarRelatorioPrazoMaximoBolsasAlunos() throws ArqException{   	  

    	if(ano == 0){
    		addMensagemErro("O campo ano está com valor inválido.");
    	}
    	
    	if(periodo == 0){
    		addMensagemErro("O campo período está com valor inválido.");
    		return null;
    	}
    	
    	ValidatorUtil.validateRequired(ano, "Ano", erros);
		ValidatorUtil.validateRequired(periodo, "Período", erros);   
		carregarUnidade();
		if (hasErrors()||matrizCurricular == null || matrizCurricular.getCurso().getUnidade().getId() == 0)
			return null;
		
		carregarDataInicioFim();
		
		if (! hasErrors()){
			RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
			try {
				
				listaDiscenteBolsa = dao.findDiscentesBolsas(matrizCurricular.getCurso().getUnidade().getId(), dataInicio, dataFinal);
				if (listaDiscenteBolsa.size() == 0)
					addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);					
				else {
					return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_PRAZO_MAXIMO_BOLSA);
				}
					
			} finally {
				if (dao != null)
					dao.close();
			}
		}
		return null;
    }

	/**
	 * Repassa para a view, o resultado da consulta de Lista de Insucessos de Alunos de um curso nas disciplinas.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_insucessos.jsp </li></ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaInsucessosAlunos() throws DAOException{

		//teste para o coordenador buscar apenas aluno do seu curso
		if(getAcessoMenu().isCoordenadorCursoGrad() || getAcessoMenu().isSecretarioGraduacao()){
			idCurso = getCursoAtualCoordenacao().getId();
		}
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			curso = dao.findByPrimaryKey(idCurso, Curso.class, "nome");
			listaDiscente = dao.findListaInsucessosAlunos(ano, periodo, idCurso);
		} finally {
			dao.close();
		}
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_INSUCESSOS);
	}

	/**
	 * Repassa para a view, o resultado da consulta de Alunos Concluintes.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_concluintes.jsp </li></ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaAlunosConcluintes() throws DAOException{
		carregarTituloRelatorio();
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findListaAlunosConcluintes(curso);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_CONCLUINTES);
	}


	/**
	 * Repassa para a view, o resultado da consulta de Quantitativos de Alunos Concluintes.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/selecionaq_concluintes.jsp </li></ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioQuantitativoAlunosConcluintes() throws DAOException{
		carregarTituloRelatorio();
		
		if(isTodos()){
			ano=0;
			periodo=0;
		}
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findQuantitativoAlunosConcluintes(curso, TipoFiltroCurso.CURSO_SELECIONADO, ano,periodo);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_QUANT_CONCLUINTES);
	}

	/**
	 * Repassa para a view, o resultado da consulta de Alunos Ativos e Matriculados.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_matriculados.jsp </li></ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaAlunoMatriculados() throws DAOException{
		carregarTituloRelatorio();
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findListaAlunoMatriculados(matrizCurricular, ano, periodo, filtros);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_MATRICULADOS);
	}

	/**
	 * Repassa para a view, o resultado da consulta de Alunos Vinculados a uma Estrutura Curricular.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_vinculados_estrutura.jsp </li></ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaVinculadosEstrutura() throws DAOException{
		carregarTituloRelatorio();
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findListaVinculadosEstrutura(matrizCurricular, codigo, filtros);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_VINVCULADOS_ESTRUTURA);
	}

	/**
	 * Repassa para a view, o resultado da consulta de Alunos Reprovados e Trancados.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_reprovados_trancados.jsp </li></ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaAlunoReprovadosTrancados() throws DAOException{
		carregarTituloRelatorio();
		
		if(idMatrizCurricular!=null)
			matrizCurricular.setId(idMatrizCurricular);
		if(idCurso!=null)
			matrizCurricular.getCurso().setId(idCurso);
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findListaAlunoReprovadosTrancados(matrizCurricular, ano, periodo, situacaoMatricula, filtros);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_REPROVADOS_TRANCADOS);
	}

	/**
	 * Repassa para a view, o resultado da consulta de Quantitativos de Alunos Matriculados.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/selecionaq_matriculados.jsp </li></ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioQuantitativoAlunoMatriculados() throws DAOException{
		carregarTituloRelatorio();
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findQuantitativoAlunoMatriculados(curso, TipoFiltroCurso.CURSO_SELECIONADO , ano, periodo);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_QUANT_MATRICULADOS);
	}


	/**
	 * Repassa para a view, o resultado da consulta de Alunos Ativos e seus Prazos de Conclusão.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_prazo_conclusao.jsp </li></ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaAlunoPrazoConclusao() throws DAOException{
		carregarTituloRelatorio();
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findListaAlunoPrazoConclusao(curso, ano, periodo);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_PRAZO_CONCLUSAO);
	}


	/**
	 * Repassa para a view, o resultado da consulta de Quantitativos de Alunos Egressos por Sexo.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/selecionaq_sexo_egresso.jsp </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioQuantitativoAlunoSexoEgresso() throws DAOException{
		if(isTodosCentros()) {
			centro.setId(0);
		}
		else {
			setFiltroCentro(true);
			if(isEmpty(centro)) {
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Centro");
				return null;
			}
		}
		carregarTituloRelatorio();
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findQuantitativosAlunoSexoEgresso(centro, ano, periodo);
			
			if(isEmpty(listaDiscente)) {
				addMensagemErro("Nenhum discente encontrado com os parâmetros informados.");
				return null;
			}
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_QUANT_SEXO_EGRESSO);
	}

	/**
	 * Repassa para a view, o resultado da consulta de Quantitativos de Alunos Ingressos por Sexo.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/selecionaq_sexo_ingresso.jsp </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioQuantitativoAlunoSexoIngresso() throws DAOException{
		if(isTodosCentros()) {
			centro.setId(0);
		}
		else {
			setFiltroCentro(true);
			if(isEmpty(centro)) {
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Centro");
				return null;
			}
		}
		carregarTituloRelatorio();
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findQuantitativosAlunosSexo(centro, ano, periodo);
			
			if(isEmpty(listaDiscente)) {
				addMensagemErro("Nenhum discente encontrado com os parâmetros informados.");
				return null;
			}
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_QUANT_SEXO_INGRESSO);
	}

	/**
	 * Repassa para a view, o resultado da consulta de Alunos Cadastrados no Vestibular sem Matrícula.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/selecionaq_vest_sem_mat.jsp </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioQuantitativoVestibularMatricula() throws DAOException{
		carregarTituloRelatorio();
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findQuantitativoAlunoVestibularSemMatricula(curso, TipoFiltroCurso.CURSO_SELECIONADO, ano, periodo);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_QUANT_VEST_SEM_MAT);
	}
	
	/**
	 * Repassa para a view, o resultado da consulta de Alunos Reprovados e Desnivelados.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_centro.jsp </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioQuantitativoReprovadosDesnivelados() throws DAOException{
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);

		try {
			if(centro != null)
				if (centro.getId()!=0)
					centro = dao.findByPrimaryKey(centro.getId(), Unidade.class);
				else
					centro.setNome("TODOS");
			
			if(departamento != null)
				if (departamento.getId()!=0)
					departamento = dao.findByPrimaryKey(departamento.getId(), Unidade.class);
				else
					departamento.setNome("TODOS");
			
			listaDiscente = dao.findQuantitativoAlunosReprovadosDesnivelados(centro, departamento);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_QUANT_REPROVADOS_DESNIVELADOS);
	}

	/**
	 * Repassa para a view, o resultado da consulta de Alunos pelo seu Tipo de
	 * Saída, seja ela temporária ou não. 
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_tipo_saida.jsp </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	
	public String gerarRelatorioListaAlunoTipoSaida() throws DAOException, SegurancaException{
		checkRole(SigaaPapeis.DAE,SigaaPapeis.ADMINISTRADOR_DAE,SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO);
		if(idMatrizCurricular!=null)
			matrizCurricular.setId(idMatrizCurricular);
		if(idCurso!=null)
			matrizCurricular.getCurso().setId(idCurso);

		carregarTituloRelatorio();
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		validaRelatorioListaAlunoTipoSaida();
		
		if( hasErrors() )
			return null;
		
		try {
			listaDiscente = dao.findListaAlunoTipoSaida(matrizCurricular, ano, periodo, status, formaIngresso, tipoMovimentacaoAluno, filtros);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_TIPO_SAIDA);
	}
	
	/**
	 * Valida os filtros do para geração do relatório dos Alunos pelo seu Tipo de
	 * Saída
	 */
	private void validaRelatorioListaAlunoTipoSaida(){
		validateRequired(ano, "Ano", erros);
		validateRequired(periodo, "Período", erros);
		
		if( isFiltroUnidade() ){
			if( isFiltroMatriz() && isEmpty(matrizCurricular) )
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Matriz Curricular");
			if( isFiltroCurso() && isEmpty(idCurso) )
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Curso");
			if( matrizCurricular.getCurso().getId() == 0 && isFiltroUnidade() && isEmpty(matrizCurricular.getCurso().getUnidade()) )
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Unidade");
		}
		if( isFiltroEgresso() && isEmpty(tipoMovimentacaoAluno) )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Tipo de Saída");
		if( isFiltroIngresso() && isEmpty(formaIngresso) )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Tipo de Entrada");
		if( isFiltroStatus() && isEmpty(status) )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Status do Discente");
	}

	/**
	 * Repassa para a view, o resultado da consulta do Quantitativos de Alunos de pro-básica concluídos.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/selecionaq_probasica_concluido.jsp </li></ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioQuantitativoAlunoProbasicaConcluido() throws DAOException{
		carregarTituloRelatorio();

		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findQuantitativoAlunoProbasicaConcluido(centro);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_QUANT_PROBASICA_CONCLUIDO);
	}

	/**
	 * Repassa para a view, o resultado da consulta do Quantitativos de Alunos de pro-básica matriculados.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/selecionaq_probasica_matriculado.jsp </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioQuantitativoAlunoProbasicaMatriculados() throws DAOException{
		carregarTituloRelatorio();

		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findQuantitativoAlunoProbasicaMatriculados(centro);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_QUANT_PROBASICA_MATRICULADO);
	}

	/**
	 * Repassa para a view, o resultado da consulta do Quantitativos de Alunos de pro-básica sem matrícula.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/selecionaq_probasica_sem_matricula.jsp </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioQuantitativoAlunoProbasicaSemMatricula() throws DAOException{
		carregarTituloRelatorio();
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findQuantitativoAlunoProbasicaSemMatricula(centro);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_QUANT_PROBASICA_SEM_MAT);
	}

	/**
	 * Repassa para a view, o resultado da consulta de Alunos com registro em uma determinada disciplina.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_registro_disciplina.jsp </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaAlunoRegistroDisciplina() throws DAOException{
		carregarTituloRelatorio();
		
		if ( isEmpty(disciplina) && isEmpty(discente) ) {
			disciplina = new ComponenteCurricular();
			discente = new Discente();
			addMensagem( MensagensArquitetura.SELECIONE_OPCAO_BUSCA );
			return null;
		}
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findListaAlunoRegistroDisciplina(disciplina, discente);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_REGISTRO_DISCIPLINA);
	}

	/**
	 * Repassa para a view, o resultado da consulta de Alunos com trancamento em um determinado componente curricular.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_trancamentos_componente.jsp </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaTrancamentosComponente() throws DAOException{
		carregarTituloRelatorio();

		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findListaTrancamentosComponente(disciplina, ano, periodo, getProgramaStricto());
		} finally {
			dao.close();
		}
		
		if ( listaDiscente.isEmpty() ) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_TRANCAMENTOS_COMPONENTE);
	}
	
	/**
	 * Repassa para a view, o resultado da consulta de Alunos matriculados em disciplina com carga
	 * horária de estágio por disciplina/curso/centro/cpf/data de nascimento.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_matriculado_estagio.jsp </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaAlunoMatriculadosEstagio() throws DAOException{
		carregarTituloRelatorio();

		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findListaAlunoMatriculadosEstagio(centro, curso);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_MATRICULADO_ESTAGIO);
	}

	/**
	 * Repassa para a view, o resultado da consulta de alunos por Curso/Centro com prazo máximo de
	 * conclusão em um ano semestre por carga horária total de curso por carga horária integralizada.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_prazo_conclusao_ch.jsp </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaAlunoPrazoConclusaoCh() throws DAOException{
		carregarTituloRelatorio();
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findListaAlunoPrazoConclusaoCh(centro, curso, prazoConclusao);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_PRAZO_CONCLUSAO_CH);
	}

	/**
	 * Repassa para a view, o resultado da consulta do Quantitativos de Alunos graduandos por curso.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/selecionaq_graduando.jsp </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioQuantitativoAlunoGraduando() throws DAOException{
		carregarTituloRelatorio();
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findQuantitativoAlunoGraduando(centro, curso, TipoFiltroCurso.CURSO_SELECIONADO);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_QUANT_GRADUANDO);
	}

	/**
	 * Repassa para a view, o resultado da consulta de alunos por cidade de residência.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_cidade_residencia.jsp </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaAlunoCidadeResidencia() throws DAOException{
		carregarTituloRelatorio();
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findListaAlunoCidadeResidencia(centro, curso);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_CIDADE_RESIDENCIA);
	}

	/**
	 * Repassa para a view, o resultado da consulta de aluno/matrícula por curso- turno -cidade por carga horária total
	 * exigida por carga horária disciplina-atividade obrigatória exigida por carga horária disciplina-atividade
	 * obrigatória integralizada por ch disciplina-atividade complementar exigida por ch disciplina-atividade
	 * complementar integralizado por % total de integralização pelo produto da ch disciplina-atividade exigida - (subtraído)
	 * carga horária disciplina atividade complementar cumprida.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_ch_detalhada.jsp </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaAlunoChDetalhada() throws DAOException{
		carregarTituloRelatorio();
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findListaAlunoChDetalhada(centro, curso);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_CH_DETALHADA);
	}

	/**
	 * Repassa para a view, o resultado da consulta de alunos matriculados em uma determinada atividade.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_matriculado_atividade.jsp </li></ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaAlunoMatriculadoAtividade() throws DAOException{
		carregarTituloRelatorio();
		
		if( isEmpty(ano) )
			ano = getCalendarioVigente().getAno();
		if( isEmpty(periodo) )
			periodo = getCalendarioVigente().getPeriodo();
		
		Collection<Character> niveis = new ArrayList<Character>();
		Unidade unidade = null;
		
		if (isPortalPpg()){
			niveis = NivelEnsino.getNiveisStricto();
		}else{
			if( isUserInRole( SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS ) && isEmpty(disciplina) )
				unidade = getProgramaStricto();
			if( isUserInRole( SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS )){
				niveis = NivelEnsino.getNiveisStricto();
			}else{
				if ( !isUserInRole( SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE, SigaaPapeis.CDP ))
					curso = getCursoAtualCoordenacao();
				niveis.add(NivelEnsino.GRADUACAO);
				setExibirOrientador(true);
			}
		}
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			if(!niveis.contains(NivelEnsino.GRADUACAO))
				listaDiscente = dao.findListaAlunoMatriculadoAtividadeStricto(disciplina, ano, periodo, unidade, niveis);
			if(niveis.contains(NivelEnsino.GRADUACAO))
				listaDiscente = dao.findListaAlunoMatriculadoAtividadeGraduacao(disciplina, ano, periodo, curso, niveis);
		} finally {
			dao.close();
		}
		
		if(listaDiscente.isEmpty()){
			addMensagemErro("Não foram encontrados discentes matriculados em atividades com os critérios informados.");
			return null;
		}
			
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_MATRICULADO_ATIVIDADE);
	}

	/**
	 * Repassa para a view, o resultado da consulta de alunos com prazo de conclusão no ano semestre atual.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_prazo_semestre_atual.jsp </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaAlunoPrazoSemestreAtual() throws DAOException{
		carregarTituloRelatorio();
		
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		anoSemestreAtual = (ano * 10)+ periodo;
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findListaAlunoPrazoSemestreAtual(anoSemestreAtual, centro, curso);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_PRAZO_SEMESTRE_ATUAL);
	}


	/**
	 * Repassa para a view, o resultado da consulta de alunos laureados.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_laureados.jsp </ul>
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public String gerarRelatorioListaAlunoLaureados() throws DAOException, SegurancaException{
		checkRole(SigaaPapeis.DAE,SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.PORTAL_PLANEJAMENTO );		
		carregarTituloRelatorio();
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);

		int antigoIndiceLaureados = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.INDICE_ACADEMICO_LAUREADOS_ANTIGO_IRA);
		valorMinimo = ParametroHelper.getInstance().getParametroDouble(ParametrosGraduacao.VALOR_MINIMO_INDICE_ACADEMICO_LAUREADOS);
		
		if( indiceSelecionado.getId() == antigoIndiceLaureados ){
			valorMinimo = ParametroHelper.getInstance().getParametroDouble(ParametrosGraduacao.VALOR_MINIMO_INDICE_ACADEMICO_LAUREADOS_ANTIGO_IRA);
		}
		
		try {
			//Representa, neste UC, os Discentes laureados de um dado curso.
			listaDiscente = dao.findListaAlunoLaureados(ano, periodo, indiceSelecionado.getId(), valorMinimo);
			
			//Representa os Discentes com IEAN menor que 600, mas que tiveram maior IEAN do curso.
			listaDiscenteNaoAtingiuIndiceLaureadoMinimo = dao.findAlunoNaoAtingiuIndiceLaureadoMinimo(ano, periodo, indiceSelecionado.getId(), valorMinimo);
		} finally {
			dao.close();
		}
		
		if (isEmpty(listaDiscente) && isEmpty(listaDiscenteNaoAtingiuIndiceLaureadoMinimo)) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_LAUREADOS);
	}


	/**
	 * Repassa para a view, o resultado da consulta do quantitativos de solicitação de trancamento de matrícula.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/selecionaq_motivo_trancamento.jsp </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioQuantitativosAlunoMotivoTrancamento() throws DAOException{
		if(idMatrizCurricular!=null)
			matrizCurricular.setId(idMatrizCurricular);
		if(idCurso!=null)
			matrizCurricular.getCurso().setId(idCurso);
		
		carregarTituloRelatorio();
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findQuantitativosAlunoMotivoTrancamento(ano,periodo,matrizCurricular,motivoTrancamento, filtros);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_QUANT_MOTIVO_TRANCAMENTO);
	}


	/**
	 * Repassa para a view, o resultado da consulta de alunos de acordo com o motivo de trancamento.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_motivo_trancamento.jsp </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaAlunoMotivoTrancamento() throws DAOException{
		centro = matrizCurricular.getCurso().getUnidade();
		if(idMatrizCurricular!=null)
			matrizCurricular.setId(idMatrizCurricular);
		if(idCurso!=null)
			matrizCurricular.getCurso().setId(idCurso);
		
		carregarTituloRelatorio();
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findListaAlunoMotivoTrancamento(ano,periodo,matrizCurricular,motivoTrancamento,filtros);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_MOTIVO_TRANCAMENTO);
	}

	/**
	 * Repassa para a view, o resultado da consulta de alunos concluídos com créditos pendentes.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/menus/administracao.jsp </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaAlunoConcluidoCreditoPendente() throws DAOException{
		carregarTituloRelatorio();
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findListaAlunoConcluidoCreditoPendente();
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_CONCLUIDO_CREDITO_PENDENTE);
	}

	/**
	 * Relatório dos discente com mobilidade estudantil.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/menus/relatorios_dae.jsp </ul>
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String gerarRelatorioMobilidade() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE);

		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			List<Map<String, Object>> todosAlunosMobilidade = dao.findAllAlunosMobilidadeEstudantil(new String(ano+""+periodo), new String(anoFim+""+periodoFim));
			List<Map<String, Object>> resultado = new ArrayList<Map<String,Object>>();
			
			for (Map<String, Object> linha : todosAlunosMobilidade) {
				resultado.add(linha);
			}
			
			getCurrentRequest().setAttribute("resultado", resultado);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+getAmbito()+JSP_RELATORIO_MOBILIDADE_ESTUDANTIL);
	}
	
	/**
	 * Retorna a descrição do status do discente.
	 * 
	 * @return
	 */
	public String getStatusDescricao(){
		if(status == 0)
			return "TODOS";
		else
			return StatusDiscente.getDescricao(status);
	}

	public List<Map<String, Object>> getListaDiscente() {
		return listaDiscente;
	}

	public void setListaDiscente(List<Map<String, Object>> listaDiscente) {
		this.listaDiscente = listaDiscente;
	}

	/**
	 * Retorna o número de discentes encontrados na {@link #listaDiscente}.
	 * 
	 * @return
	 */
	public int getNumeroRegistosEncontrados() {
		if(listaDiscente!=null)
			return listaDiscente.size();
		else
			return 0;
	}
	
	/**
	 * Retorna o número de discentes encontrados na {@link #listaDiscenteNaoAtingiuIndiceLaureadoMinimo}.
	 * 
	 * @return
	 */
	public int getNumeroRegistosEncontradosDiscenteNaoAtingiuIndicelaureadoMinimo() {
		if(listaDiscenteNaoAtingiuIndiceLaureadoMinimo !=null)
			return listaDiscenteNaoAtingiuIndiceLaureadoMinimo.size();
		else
			return 0;
	}
	
	/**
	 * Retorna a lista das possíveis matrizes de um coordenador de curso
	 *
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getPossiveisMatrizesCoord() throws DAOException{
		List<SelectItem> listaMatrizes = new ArrayList<SelectItem>();// = toSelectItems(dao.findByCurso(getCursoAtualCoordenacao().getId(), null), "id", "descricao");
		
		MatrizCurricularDao dao = getDAO(MatrizCurricularDao.class);

		try {
			if( getCursoAtualCoordenacao() != null )
				listaMatrizes = toSelectItems(dao.findByCurso(getCursoAtualCoordenacao().getId(), null), "id", "descricao");
			else if( !isEmpty( getAllCursosCoordenacaoNivel() ) ){
				for(Curso c : getAllCursosCoordenacaoNivel()){
					listaMatrizes.addAll( toSelectItems(dao.findByCurso(c.getId(), null), "id", "descricao") );
				}
			}
		} finally {
			dao.close();
		}
		
		return listaMatrizes;
	}

	/**
	 * Retorna a lista das possíveis cursos de uma coordenação de programa
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getPossiveisCursosCoordStricto() throws DAOException{
		List<SelectItem> listaCursos = new ArrayList<SelectItem>();
		
		CursoDao dao = getDAO(CursoDao.class);
		
		try {
			listaCursos = toSelectItems(dao.findByPrograma(getProgramaStricto().getId()), "id", "nomeCursoStricto");
		} finally {
			dao.close();
		}
		
		return listaCursos;
	}
	
	/**
	 * Utilizado na Lista de Discentes que ingressaram em um determinado 
	 * ano semestre(Relatório no Módulo Graduação) 
	 * 
	 * Chamado por:
	 * sigaa.war/graduacao/relatorios/discente/seleciona_ingressantes.jsp
	 * 
	 * retorna os 'status'de um discente
	 * @return
	 */
	public Collection<SelectItem> getStatusDiscenteListaIngressantes() {

		Collection<StatusDiscente> statusPermitidos = new ArrayList<StatusDiscente>();
		StatusDiscente.getStatusTodos().remove(StatusDiscente.EXCLUIDO);
		for( StatusDiscente status : StatusDiscente.getStatusTodos() ) {
			if(status.getId() != StatusDiscente.EXCLUIDO) {
				statusPermitidos.add(status);
			}
		}
			
		return toSelectItems(statusPermitidos, "id", "descricao");

	}
	

	/** Carrega o relatório de discentes stricto sensu matriculados em atividades.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 */
	public String carregarRelatorioMatriculadosAtividadeStricto() {
		return carregarSelecaoRelatorio("seleciona_matriculado_atividade.jsf");
	}
	
    /**
	 * Redireciona para o form de Seleção de parâmetro do relatório de alunos matriculados
	 * em atividades mas não renovados.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> /sigaa.war/stricto/menu_coordenador.jsp (Portal Stricto) </li>
	 * 	<li> /sigaa.war/stricto/menus/relatorios.jsp (PPG) </li>
	 *  </ul>   
	 * @return
	 */
    public String carregarRelatorioAlunosMatriculadosEmAtividadesNaoRenovadas(){
    	return carregarSelecaoRelatorio(JSP_SELECIONA_RELATORIO_ALUNOS_MATRICULADOS_EM_ATIVIDADES_NAO_RENOVADAS);
    }
	
	/** Carrega a lista de alunos para eleição.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 */
	public String carregarListaAlunosEleicao(){
		return carregarSelecaoRelatorio("seleciona_eleicao.jsf");
	}
	
	/**
	 * Carrega a listagem de contatos dos alunos.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/menu_coordenador.jsp (Portal Stricto)</li>
	 * <li>/sigaa.war/stricto/menus/relatorios.jsp (PPG)</li>
	 * </ul>
	 * @return
	 */
	public String carregarListaContatoAlunos(){
		return carregarSelecaoRelatorio("seleciona_contato.jsf");
	}

	/** Carrega a lista de matrizes currilares quando o valor do curso é alterado.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/relatorios/discente/seleciona_ingressantes.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void carregarMatrizes(ValueChangeEvent e) throws DAOException {
		getCurriculoBean().carregarMatrizes(e);
		setFiltroMatriz(false);
	}
	
	/** Retorna o MBean de Estrutura Curricular de Graduação.
	 * @return
	 */
	private EstruturaCurricularMBean getCurriculoBean() {
		return getMBean("curriculo");
	}

	/** 
	 * Inicia o relatório dos Trancamentos de Disciplinas.
     * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/menus/relatorios_dae.jsp</li>
	 * </ul>
     *
	 * @return
	 * @throws DAOException
	 */
	public String iniciarTrancamentoReuni() throws DAOException {
		ano = CalendarUtils.getAnoAtual();
		periodo = getPeriodoAtual();
		return forward(CONTEXTO + "discente/seleciona_trancamento_disciplina.jsp");
	}
	
	/**
	 * Repassa para a view, o resultado da consulta da lista Trancamento de Disciplinas.
	 * 
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_trancamento_disciplina.jsp </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioTrancamentodeDisciplina() throws DAOException{
		validarCampos();
		
		if (periodo == 3 || periodo == 4) {
			addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Período");
		}

		if (hasErrors())
			return forward(CONTEXTO + "/discente/seleciona_trancamento_disciplina.jsp");
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			relatorioTrancamento = dao.findQuantitativoTrancamentoDisciplina(ano, periodo, departamento.getId());
			if (relatorioTrancamento.size() == 0) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return forward(CONTEXTO + "discente/seleciona_trancamento_disciplina.jsp");
			}
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO + JSP_RELATORIO_QUANT_TRANCAMENTO_REUNI);
	}
	
	/**
	 * Popula os dados e direciona o usuário para a tela do relatório.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/relatorios/discente/seleciona_discentes_especiais.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioDiscentesEspeciais() throws DAOException {
		validarCampos();
		
		if(hasErrors())
			return null;
		
		carregarUnidade();
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			discentes = dao.findDiscentesEspeciaisDisciplinasByAnoPeriodo(ano, periodo, matrizCurricular.getCurso().getUnidade().getId());
			
			if(discentes == null || discentes.isEmpty()) {
				addMensagemErro("Nenhum resultado encontrado com os parâmetros informados.");
				return null;
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO + getAmbito() + JSP_RELATORIO_DISCENTES_ESPECIAIS);
	}

	/**
	 * Método responsável por validar os dados de inserção de ano e período.
	 * @return
	 */
	private String validarCampos() {
		if (ano == null || ano <= 0 || ano > CalendarUtils.getAnoAtual()) {
			addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Ano");
		}
		if (periodo == null || periodo <= 0 || periodo > 4 ) {
			addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Período");
		}

		return null;
	}
	
	/**
	 * Serve para setas as informações o ano e período atual e para redirecionar para a tela da geração do relatório.
	 * 
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/relatorio_dae.jsp </ul>
	 * @return
	 */
	public String iniciarRelatorioQuantitativoTurmaDepartamento() {
		ano = CalendarUtils.getAnoAtual();
		periodo = getPeriodoAtual();
		return forward("/graduacao/relatorios/discente/seleciona_quant_turma_departamento.jsf");
	}
	
	/**
	 * Inicia o relatório: Sumário de Indices Acadêmicos por Curso de Graduação
	 * 
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/relatorio_dae.jsp </ul>
	 * @return
	 */
	public String iniciarSumarioIndicesAcademicos() {
		ano = CalendarUtils.getAnoAtual() - 1;
		anoFinal = CalendarUtils.getAnoAtual();
		periodo = getPeriodoAtual();
		this.unidade = new Unidade();
		this.modalidadeEnsino = new ModalidadeEducacao();
		this.somenteCursosConvenio = false;
		return forward("/graduacao/relatorios/discente/seleciona_ano_inicial_final.jsf");
	}
	
	/**
	 * Repassa para a view, o resultado da consulta de turmas não consolidadas por departamento.
	 * 
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_trancamento_disciplina.jsp </li></ul>
	 * @return
	 * @throws DAOException
	 */
	public String relatorioQuantitativoTurmasDepartamento() throws DAOException{
		validarCampos();
		if (hasErrors())
			return forward(CONTEXTO + "/discente/seleciona_quant_turma_departamento.jsp");
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		try {
			listaDiscente = dao.findQuantitativoTurmaDepartamento(ano, periodo, isTodos());
			if (listaDiscente.size() == 0) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				forward(CONTEXTO + "/discente/seleciona_quant_turma_departamento.jsp");
			}
		} finally {
			dao.close();
		}
		return forward(CONTEXTO + JSP_RELATORIO_QUANT_TURMA_DEPARTAMENTO);
	}
	
	/**
	 * Gera o relatório: Sumário de Indices Acadêmicos por Curso de Graduação
	 * 
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_ano_inicial_final.jsp </ul>
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String relatorioSumarioIndicesAcademicos() throws HibernateException, DAOException {
		validateMinValue(ano, ANO_MINIMO, "Ano Inicial", erros);
		validateMaxValue(ano, CalendarUtils.getAnoAtual(), "Ano Inicial", erros);
		
		validateMinValue(anoFinal, ano, "Ano Final", erros);
		validateMaxValue(anoFinal, CalendarUtils.getAnoAtual(), "Ano Final", erros);
		
		if (hasErrors()) return null;
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		modalidadeEnsino = dao.refresh(modalidadeEnsino);
		unidade = dao.refresh(unidade);
		
		lista = dao.relatorioSumarioFormaIngresso(ano, anoFinal, modalidadeEnsino.getId(), unidade.getId(), somenteCursosConvenio);
		sumarioIndices = dao.relatorioIndiceAcademicos(modalidadeEnsino.getId(), unidade.getId(), somenteCursosConvenio);
		sumarioTrancamento = dao.relatorioSumarioTrancamentoDisciplinas(ano, anoFinal, modalidadeEnsino.getId(), unidade.getId(), somenteCursosConvenio);
		indices = dao.findAtivosByExactField(IndiceAcademico.class, "nivel", NivelEnsino.GRADUACAO, "ordem", "asc");
		municipios = new TreeSet<String>();
		for (Map<String,Object> linha : lista) {
			municipios.add((String) linha.get("curso_cidade"));
		}
		
		if (isEmpty(lista)) {
			addMensagem(MensagensGerais.RELATORIO_VAZIO, "Relatório Sumário de Indices Acadêmicos por Curso de Graduação");
			return null;
		}
		
		return forward("/graduacao/relatorios/discente/sumario_indices_academicos.jsp");
	}

	/**
	 * Pega o curso do usuário e traz todos os alunos deste que fazem este curso
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/menu_coordenador.jsp </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String relatorioEmailDosAlunos() throws DAOException {
		curso = getCursoAtualCoordenacao();

		DiscenteDao dao = getDAO(DiscenteDao.class);
		
		try {
			discentes = dao.findDiscentesComVinculoByCurso( curso.getId() );
		} finally {
			dao.close();
		}
		
		return forward("/graduacao/relatorios/discente/lista_email_alunos.jsp");
	}
	
	/**
	 * 
	 * Retorna os alunos que tenham concluído um dado curso. 
	 * 
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/menu_coordenador.jsp </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */	
	public String iniciarRelatorioEmailAlunoConcluidos(){
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		return forward("/graduacao/relatorios/discente/seleciona_email_alunos_concluintes.jsp");		
	}

	/**
	 * 
	 * Retorna os alunos que tenham concluído um dado curso. 
	 * 
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/graduacao/relatorios/discente/seleciona_email_alunos_concluintes.jsp</ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String relatorioEmailDosAlunosConcluidos() throws DAOException {
		
		erros = new ListaMensagens();
		ValidatorUtil.validaInt(ano, "Ano", erros);
		ValidatorUtil.validaInt(periodo, "Período", erros);
		
		if (ano < 1900 )
			addMensagemErro("Ano inválido");
		
		if (hasErrors())
			return null;
		
		curso = getCursoAtualCoordenacao();
		
		RelatorioDiscenteGraduacaoDao dao = getDAO(RelatorioDiscenteGraduacaoDao.class);
		
		try {
			discentes = dao.findDiscentesConcluidosByCurso( curso.getId(), ano, periodo );
			
			if (isEmpty(discentes)){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
		} finally {
			dao.close();
		}
		
		return forward("/graduacao/relatorios/discente/lista_email_alunos_concluidos.jsp");
	}
	
	/**
	 * Responsável pela geração de um relatório com os Discente já na UFRN, porém ingressante 
	 * em um novo curso.
	 * 
	 *<br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> sigaa.war/graduacao/relatorios/discente/seleciona_aluno_ingressante_outro_curso.jsp
	 * </li></ul>
	 * @return
	 * @throws DAOException
	 */
	public String alunosIngressantesOutroCurso() throws DAOException{
		validarCampos();
		
		if (hasErrors()) {
			return forward(CONTEXTO + "discente/" + JSP_SELECIONA_ALUNOS_INGRESSANTES_OUTRO_CURSO);
		}
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		int anoInicioConvocacao = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.ANO_INICIO_UTILIZACAO_CONVOCACAO_DISCENTE);		
		if (ano < anoInicioConvocacao) {
			processoSeletivo = null;
		} else {
			if( isEmpty( processoSeletivo ) ){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Processo Seletivo");
				return null;
			} else if ( convocacao == 0 ){
				ProcessoSeletivoVestibular ps = dao.findByPrimaryKey(processoSeletivo, ProcessoSeletivoVestibular.class);
				descricaoProcessoSeletivo = ps.getNome();
			}
			setChamada(convocacao);
		}
		try {
			listaDiscente = dao.findAlunoIngressanteOutroCurso(ano, periodo, chamada, processoSeletivo);
			if ( processoSeletivo != null && convocacao > 0 ){
				ConvocacaoProcessoSeletivo convocacaoDiscemte = dao.findByPrimaryKey(convocacao, ConvocacaoProcessoSeletivo.class);
				descricaoChamada = convocacaoDiscemte.getDescricao();
				descricaoProcessoSeletivo = convocacaoDiscemte.getProcessoSeletivo().getNome();
			} else {
				descricaoChamada = chamada + TEXTO_CHAMADA;
			}
			
			if (listaDiscente.size() == 0) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return forward(CONTEXTO + "discente/" + JSP_SELECIONA_ALUNOS_INGRESSANTES_OUTRO_CURSO);
			}
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO + "discente/" + JSP_RELATORIO_ALUNOS_INGRESSANTES_OUTRO_CURSO);
	}
	
	/**
	 * Esse método abaixo é responsável pela geração do relatório quantitativo dos discente regulares.
	 * 
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/relatorios/discente/seleciona_total_alunos_regulares.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String totalAlunosRegulares() throws DAOException {
		GenericDAO daoGeneric = getDAO(GenericDAOImpl.class);
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			if (campus.getId() != 0) 
				campus = daoGeneric.findByPrimaryKey(campus.getId(), CampusIes.class);
			else
				campus.setNome("Todos");
			
			listaDiscente = dao.findTotalAlunoRegular(campus.getId());
			
			if (listaDiscente.size() == 0) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
		} finally {
			dao.close();
			daoGeneric.close();
		}
		
		return forward(CONTEXTO + JSP_RELATORIO_QUANT_TOTAL_ALUNOS_REGULARES);
	}

	/**
	 * Reponsável pela geração de um relatório com a quantidade de homens e mulheres agruapado por 
	 * curso dos discentes regulares.
	 * 
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/relatorios/discente/seleciona_total_alunos_regulares_curso.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String totalAlunosRegularesPorCurso() throws DAOException {
		GenericDAO daoGeneric = getDAO(GenericDAOImpl.class);
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			if (campus.getId() != 0) 
				campus = daoGeneric.findByPrimaryKey(campus.getId(), CampusIes.class);
			else
				campus.setNome("Todos");
			
			listaDiscente = dao.findTotalAlunoRegularCurso(campus.getId());
			
			if (listaDiscente.size() == 0) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
		} finally {
			dao.close();
			daoGeneric.close();
		}
		
		return forward(CONTEXTO + JSP_RELATORIO_QUANT_TOTAL_ALUNOS_REGULARES_CURSO);
	}

	/**
	 * Método responsável pela geração de um relatório com todos os discentes que não possuem registro de solicitação de 
	 * matrícula ou matrículas em espera para o período.
	 * 
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	 <li>sigaa.war/graduacao/relatorios/discente/seleciona_total_alunos_regulares_curso.jsp</li>
	 *  </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String alunosIngressantesSemMatricula() throws DAOException {
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		
		try {
			listaDiscente = dao.findAlunosIngressantesSemMatricula(ano, periodo);
		} finally {
			dao.close();
		}
		
		if (listaDiscente.size() == 0) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		return forward(CONTEXTO + "discente/quantitativo_aluno_sem_matricula.jsp");
	}
	
	/**
	 * Método responsável pela geração de do relatório Quantitativo de Alunos que Entraram por Segunda Opção no Vestibular.
	 * 
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	 <li>/sigaa.war/graduacao/relatorios/discente/seleciona_vestibular.jsp
	 *  </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String ingressantesSegundaOpcao() throws DAOException {
		RelatorioDiscenteGraduacaoSqlDao dao = getDAO(RelatorioDiscenteGraduacaoSqlDao.class);
		validateRequiredId(processoSeletivo, "Processo Seletivo", erros);
		if (hasErrors())
			return null;
		try {
			listaDiscente = dao.qtdIngressantesSegundaOpcao(processoSeletivo);
			ProcessoSeletivoVestibular ps = dao.findByPrimaryKey(processoSeletivo, ProcessoSeletivoVestibular.class);
			descricaoProcessoSeletivo = ps.getNome();
		} finally {
			dao.close();
		}
		
		if (listaDiscente.size() == 0) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		return forward(CONTEXTO + "discente/quantitativo_ativos_segunda_opcao.jsp");
	}
		
	/**
	 * Retorna a lista da formas de ingresso referente a lista de discente, que o relatório será composto. <br/>
	 * @return
	 * @throws DAOException
	 */
	private void carregarFormaIngresso() throws DAOException{
		
		if (listaDiscente.isEmpty()){
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		}
		
		lista = new ArrayList<Map<String,Object>>();
		for (Map<String, Object> linha : listaDiscente){
			
			int id = Integer.parseInt(""+ linha.get("id_forma_ingresso") );
			String descricao = (String) linha.get("forma_ingresso_descricao");
			
			boolean encontrou = false;
			for (Map<String, Object> l : lista){
				if (((Integer) l.get("id_forma_ingresso")) == id){
					l.put("total", ((Integer) l.get("total")) + 1);	
					encontrou = true;
					break;
				}
			}
			
			if (!encontrou){
				HashMap<String, Object> campos = new HashMap<String, Object>();    				
				campos.put("id_forma_ingresso", id);
				campos.put("descricao", descricao);
				campos.put("total", 1);	
				lista.add(campos);
			}
			
		}
	}
	
	/**
	 * Método que redireciona o Usuário para o fomulário usado na geração do relatório de Alunos e Orientadores
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> /sigaa.war/stricto/menu_coordenador.jsp (Portal Stricto) </li>
	 * </ul> 
	 * @return
	 */
	public String iniciarRelatorioAlunosRespecOrientadores() {		
		return carregarSelecaoRelatorio(JSP_SELECIONA_ALUNOS_RESPECTIVOS_ORIENTADORES);
	}
	
	/**
	 * Método responsável por gerar o relatório de alunos e Orientadores.
	 * <br>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li> sigaa.war/graduacao/relatorios/discente/seleciona_discentes_orientadores.jsp </li>
	 * </ul>
	 * @return
	 */
	public String gerarRelatorioAlunosRespecOrientadores() throws DAOException {
		
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		try {
			lista = dao.findAlunosOrientadoresByCurso(idCurso, ordenarPorOrientador);
			curso = dao.findByPrimaryKey(idCurso, Curso.class);
		} finally {
			dao.close();
		}
		
		if (lista.isEmpty()) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
	
		return carregarSelecaoRelatorio(JSP_RELATORIO_ALUNOS_RESPECTIVOS_ORIENTADORES);
	}
	
	
	public Map<String, List<LinhaRelatorioQuantitativoTrancamentos>> getRelatorioTrancamento() {
		return relatorioTrancamento;
	}

	public void setRelatorioTrancamento(
			Map<String, List<LinhaRelatorioQuantitativoTrancamentos>> relatorioTrancamento) {
		this.relatorioTrancamento = relatorioTrancamento;
	}

	public boolean isFiltroStatus() {
		return filtroStatus;
	}

	public void setFiltroStatus(boolean filtroStatus) {
		this.filtroStatus = filtroStatus;
	}

	public boolean isFiltroUnidade() {
		return filtroUnidade;
	}

	public void setFiltroUnidade(boolean filtroUnidade) {
		this.filtroUnidade = filtroUnidade;
	}

	public boolean isTodosCentros() {
		return todosCentros;
	}

	public void setTodosCentros(boolean todosCentros) {
		this.todosCentros = todosCentros;
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

	public List<DiscentesBolsas> getListaDiscenteBolsa() {
		return listaDiscenteBolsa;
	}

	public void setListaDiscenteBolsa(List<DiscentesBolsas> listaDiscenteBolsa) {
		this.listaDiscenteBolsa = listaDiscenteBolsa;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public List<Map<String, Object>> getListaDiscenteNaoAtingiuIndiceLaureadoMinimo() {
		return listaDiscenteNaoAtingiuIndiceLaureadoMinimo;
	}

	public void setListaDiscenteNaoAtingiuIndiceLaureadoMinimo(
			List<Map<String, Object>> listaDiscenteNaoAtingiuIndiceLaureadoMinimo) {
		this.listaDiscenteNaoAtingiuIndiceLaureadoMinimo = listaDiscenteNaoAtingiuIndiceLaureadoMinimo;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public Collection<Discente> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(Collection<Discente> discentes) {
		this.discentes = discentes;
	}

	public Integer getChamada() {
		return chamada;
	}

	public void setChamada(Integer chamada) {
		this.chamada = chamada;
	}

	public Integer getConvocacao() {
		return convocacao;
	}

	public void setConvocacao(Integer convocacao) {
		this.convocacao = convocacao;
	}

	public Integer getProcessoSeletivo() {
		return processoSeletivo;
	}

	public void setProcessoSeletivo(Integer processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}

	public String getDescricaoChamada() {
		return descricaoChamada;
	}

	public void setDescricaoChamada(String descricaoChamada) {
		this.descricaoChamada = descricaoChamada;
	}

	public String getDescricaoProcessoSeletivo() {
		return descricaoProcessoSeletivo;
	}

	public void setDescricaoProcessoSeletivo(String descricaoProcessoSeletivo) {
		this.descricaoProcessoSeletivo = descricaoProcessoSeletivo;
	}

	public void setMatriculados(boolean matriculados) {
		this.matriculados = matriculados;
	}

	public boolean isMatriculados() {
		return matriculados;
	}

	/**
	 * Este método retorna os possíveis índices que podem ser selecionados no relatório de laureados.
	 * Este relatório permite a seleção de outros índices para contemplar casos antigos onde o índice utilizado era outro, no caso o IRA
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getIndicesLaureados() throws DAOException{
		
		IndiceAcademicoDao dao = getDAO(IndiceAcademicoDao.class);
		IndiceAcademico indiceAtual = dao.findByPrimaryKey( ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.INDICE_ACADEMICO_LAUREADOS) ,IndiceAcademico.class);
		IndiceAcademico indiceAnterior = dao.findByPrimaryKey( ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.INDICE_ACADEMICO_LAUREADOS_ANTIGO_IRA) ,IndiceAcademico.class);
		
		List<SelectItem> indices = new ArrayList<SelectItem>();
		indices.add( new SelectItem( indiceAtual.getId(), indiceAtual.getSigla()) );
		indices.add( new SelectItem( indiceAnterior.getId(), indiceAnterior.getSigla()) );
		
		return indices;
	}
	
	/**
	 * Repassa para a view, a sigla do Índice utilizado 
	 * na listagem dos laureados.
	 */
	public String getSiglaIndiceSelecionado() throws DAOException{
		IndiceAcademicoDao dao = getDAO(IndiceAcademicoDao.class);
		return dao.findByPrimaryKey( indiceSelecionado.getId(), IndiceAcademico.class).getSigla();		
	}

	public IndiceAcademico getIndiceSelecionado() {
		return indiceSelecionado;
	}

	public void setIndiceSelecionado(IndiceAcademico indiceSelecionado) {
		this.indiceSelecionado = indiceSelecionado;
	}

	public double getValorMinimo() {
		return valorMinimo;
	}

	public void setValorMinimo(double valorMinimo) {
		this.valorMinimo = valorMinimo;
	}

	public Integer getAnoFinal() {
		return anoFinal;
	}

	public void setAnoFinal(Integer anoFinal) {
		this.anoFinal = anoFinal;
	}

	public Collection<IndiceAcademico> getIndices() {
		return indices;
	}

	public void setIndices(Collection<IndiceAcademico> indices) {
		this.indices = indices;
	}

	public List<Map<String, Object>> getLista() {
		return lista;
	}

	public void setLista(List<Map<String, Object>> lista) {
		this.lista = lista;
	}
	
	/** Array de anos utilizado no relatório Sumário de Índices Acadêmicos.
	 * @return
	 */
	public int[] getAnosSumario() {
		if (anoFinal >= ano) {
			int[] anos = new int[anoFinal - ano + 1];
			for (int k = 0, i = ano; i <= anoFinal; i++)
				anos[k++] = i;
			return anos;
		} else {
			int[] anos = new int[0];
			return anos;
		}
	}

	public boolean isSomenteCursosConvenio() {
		return somenteCursosConvenio;
	}

	public void setSomenteCursosConvenio(boolean somenteCursosConvenio) {
		this.somenteCursosConvenio = somenteCursosConvenio;
	}

	public ModalidadeEducacao getModalidadeEnsino() {
		return modalidadeEnsino;
	}

	public void setModalidadeEnsino(ModalidadeEducacao modalidadeEnsino) {
		this.modalidadeEnsino = modalidadeEnsino;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public List<Map<String, Object>> getSumarioIndices() {
		return sumarioIndices;
	}

	public List<Map<String, Object>> getSumarioTrancamento() {
		return sumarioTrancamento;
	}

	public Set<String> getMunicipios() {
		return municipios;
	}

	public boolean isOrdenarPorOrientador() {
		return ordenarPorOrientador;
	}

	public void setOrdenarPorOrientador(boolean ordenarPorOrientador) {
		this.ordenarPorOrientador = ordenarPorOrientador;
	}
	
}