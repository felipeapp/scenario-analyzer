/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.dominio;

import java.util.HashMap;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.TipoFiltroCurso;

/**
 * Entidade que representa os dados da visualização para os relatórios que possuem o curso como parâmetro.
 *
 * É usado no RelatorioPorCursoMBean que é o controlador que interage com o formulário.
 * Através dos parâmetros abaixo, por exemplo, exibeAnoPeriodo, o formulário mostra as informações configuradas.
 * Assim, permite que possamos usar várias telas de relatórios com um único MBean e em cada contexto de relatório
 * algumas informações aparecem e outras não.
 *
 *
 * @author Ricardo Wendell
 *
 */
public class RelatorioCurso extends Relatorio{

	/** Curso ao qual o relatório se restringe. */
	private Curso curso = new Curso();

	/** Ano ao qual o relatório se restringe. */
	private int ano;

	/** Ano ao qual o relatório se restringe. */
	private int periodo;

	/** Indica se o formulário deve exibir o Ano-Período. */
	private boolean exibeAnoPeriodo;

	/** Legenda do campo ano-período. Exemplo: "Ano/Período".*/
	private String legendaAnoPeriodo = "Ano/Período";

	/** Legenda do campo curso.*/
	private String legendaCurso = "Curso";

	/** Indica o tipo de filtro Curso Selecionado. */
	private int filtroCurso = TipoFiltroCurso.CURSO_SELECIONADO;
	
	/** Indica se o relatório é quantitativo. */
	private boolean quantitativo = false;
	
	/** Indica se o relatório deve exibir no filtro de cursos a opção de o usuário selecionar todos os cursos. */
	private boolean permitirTodosOsCursos = false;
	
	// Constantes que definem os relatórios gerados por este controller.
	
	// Relatórios de alunos
	public static final String ALUNOS_COM_PERCENTUAL_CH_CUMPRIDA = "PercentualCHAluno";
	public static final String ALUNOS_CONCLUINTES = "AlunosConcluintes";
	public static final String ALUNOS_FORMANDOS = "AlunosFormandos";
	public static final String ALUNOS_ATIVOS_POR_PRAZO_CONCLUSAO = "AlunosAtivosPorPrazoConclusao";
	public static final String ALUNOS_MATRICULADOS_DISCIPLINA_CH_ESTAGIO = "AlunosMatriculadosDisciplinaCHEstagio";
	public static final String ALUNOS_POR_PRAZO_MAXIMO_CONCLUSAO = "AlunosPorPrazoMaximoConclusao";
	public static final String ALUNOS_POR_CIDADE_RESIDENCIA = "AlunosPorCidadeResidencia";
	public static final String ALUNOS_COM_DETALHAMENTO_CH = "AlunosComDetalhamentosCH";
	public static final String ALUNOS_COM_PRAZO_CONCLUSAO_SEMESTRE_ATUAL = "AlunosComPrazoConclusaoSemestreAtual";
	public static final String ALUNOS_ATIVOS_CURSO = "AlunosAtivosPorCurso";
	public static final String ALUNOS_ATIVOS_NAO_MATRICULADOS_CURSO = "AlunosAtivosNaoMatriculadosPorCurso";
	public static final String ALUNOS_GRADUANDOS_COM_EMPRESTIMO_PENDENTE_BIBLIOTECA = "GraduandosComEmprestimoPendenteBiblioteca";
	// Relatórios quantitativos
	public static final String QUANTITATIVO_ALUNOS_CONCLUINTES = "QuantitativoAlunosConcluintes";
	public static final String QUANTITATIVO_ALUNOS_MATRICULADOS = "QuantitativoAlunosMatriculados";
	public static final String QUANTITATIVO_ALUNOS_VESTIBULAR_SEM_MATRICULA = "QuantitativoAlunosCadastradosVestibularSemMatricula";
	public static final String QUANTITATIVO_ALUNOS_GRADUANDOS= "QuantitativoAlunosGraduandos";
	// Habilitação
	public static final String HABILITACOES = "Habilitacoes";

	/** Mapa de relatórios que podem ser gerados por este controller. */
	private static HashMap<String, RelatorioCurso> relatorios;
	static {
		relatorios = new HashMap<String, RelatorioCurso>();
		relatorios.put(ALUNOS_COM_PERCENTUAL_CH_CUMPRIDA,
				new RelatorioCurso("Relatório de Alunos com Percentual de CH Cumprida",
						"Este relatório lista os alunos do curso selecionado e seu percentual da carga horária cumprida em relação ao total.",
						"PercentualCHAluno"));
		relatorios.put(ALUNOS_CONCLUINTES,
				new RelatorioCurso("Relatório de Alunos Graduandos",
						"Este relatório lista os alunos concluintes do curso selecionado.",
						"AlunosConcluintes"));
		relatorios.put(ALUNOS_FORMANDOS,
				new RelatorioCurso("Relatório de Alunos Formandos",
						"Este relatório lista os alunos com status formando do curso selecionado.",
						"AlunosFormandos"));
		relatorios.put(ALUNOS_ATIVOS_POR_PRAZO_CONCLUSAO,
				new RelatorioCurso("Relatório de Alunos Ativos por Prazo de Conclusão",
						null,
						"AlunosAtivosPorPrazoConclusao",
						true, "Ano/Período de Conclusão", null));
		relatorios.put(ALUNOS_MATRICULADOS_DISCIPLINA_CH_ESTAGIO,
				new RelatorioCurso("Relatório de Alunos Matriculados em Disciplinas com Carga Horária de Estágio",
						null,
						"AlunosMatriculadosDisciplinaCHEstagio"));
		relatorios.put(ALUNOS_POR_PRAZO_MAXIMO_CONCLUSAO,
				new RelatorioCurso("Relatório de Alunos por Prazo Máximo de Conclusão",
						null,
						"AlunosPorPrazoMaximoConclusao",
						true, "Ano/Período de Conclusão", null));
		relatorios.put(ALUNOS_POR_CIDADE_RESIDENCIA,
				new RelatorioCurso("Relatório de Alunos por Cidade de Residência",
						null,
				"AlunosPorCidadeResidencia"));
		relatorios.put(ALUNOS_COM_DETALHAMENTO_CH,
				new RelatorioCurso("Relatório de Alunos Ativos com suas Cargas Horárias Detalhadas",
						null,
				"AlunosComDetalhamentosCH"));
		relatorios.put(ALUNOS_COM_PRAZO_CONCLUSAO_SEMESTRE_ATUAL,
				new RelatorioCurso("Relatório de Alunos com Prazo de Conclusão no Ano Semestre Atual",
						null,
				"AlunosComPrazoConclusaoSemestreAtual"));
		relatorios.put(ALUNOS_ATIVOS_CURSO,
				new RelatorioCurso("Relatório de Alunos Ativos por Curso", null,"AlunosAtivosPorCurso",false));
		relatorios.put(ALUNOS_ATIVOS_NAO_MATRICULADOS_CURSO,
				new RelatorioCurso("Relatório de Alunos Ativos e não Matriculados por Curso", null,"AlunosAtivosNaoMatriculadosPorCurso",false));
		relatorios.put(ALUNOS_GRADUANDOS_COM_EMPRESTIMO_PENDENTE_BIBLIOTECA,
				new RelatorioCurso("Relatório de Graduandos com Empréstimos Pentendes na Biblioteca", null,"GraduandosComEmprestimoPendenteBiblioteca",false));
		// Relatórios quantitativos
		relatorios.put(QUANTITATIVO_ALUNOS_CONCLUINTES,
				new RelatorioCurso("Relatório Quantitativo de Alunos Concluintes",
						null,
						"QuantitativoAlunosConcluintes",
						true, "Ano/Período de Conclusão", null, 
						true));
		relatorios.put(QUANTITATIVO_ALUNOS_MATRICULADOS,
				new RelatorioCurso("Relatório Quantitativo de Alunos Matriculados",
						null,
						"QuantitativoAlunosMatriculados", true,
						true));
		relatorios.put(QUANTITATIVO_ALUNOS_VESTIBULAR_SEM_MATRICULA,
				new RelatorioCurso("Relatório de Alunos Ingressantes por Vestibular sem Matrícula em Componentes",
						null,
						"QuantitativoAlunosCadastradosVestibularSemMatricula", true,
						true));
		relatorios.put(QUANTITATIVO_ALUNOS_GRADUANDOS,
				new RelatorioCurso("Relatório Quantitativo de Alunos Graduandos",
						null,
					"QuantitativoAlunosGraduandos", false,
					true));
	}

	/** Construtor padrão. */
	public RelatorioCurso() {
		permitirTodosOsCursos = false;
		this.curso = new Curso();
	}

	/** Construtor parametrizado.
	 * @param titulo
	 * @param descricao
	 * @param metodoConsulta
	 */
	public RelatorioCurso(String titulo, String descricao, String metodoConsulta) {
		this();
		this.titulo = titulo;
		this.descricao = descricao;
		this.metodoConsulta = metodoConsulta;
	}

	/** Construtor parametrizado.
	 * @param titulo
	 * @param descricao
	 * @param metodoConsulta
	 * @param exibeAnoPeriodo
	 */
	public RelatorioCurso(String titulo, String descricao, String metodoConsulta, boolean exibeAnoPeriodo) {
		this(titulo, descricao, metodoConsulta);
		this.exibeAnoPeriodo = exibeAnoPeriodo;
		permitirTodosOsCursos = false;
	}
	
	/** Construtor parametrizado. 
	 * @param titulo
	 * @param descricao
	 * @param metodoConsulta
	 * @param exibeAnoPeriodo
	 * @param quantitativo
	 */
	public RelatorioCurso(String titulo, String descricao, String metodoConsulta, boolean exibeAnoPeriodo, boolean quantitativo) {
		this(titulo, descricao, metodoConsulta, exibeAnoPeriodo);
		this.quantitativo = quantitativo;
		permitirTodosOsCursos = false;
	}
	
	/** Construtor parametrizado.
	 * @param titulo
	 * @param descricao
	 * @param metodoConsulta
	 * @param exibeAnoPeriodo
	 * @param legendaAnoPeriodo
	 * @param legendaCurso
	 */
	public RelatorioCurso(String titulo, String descricao, String metodoConsulta, boolean exibeAnoPeriodo, String legendaAnoPeriodo, String legendaCurso) {
		this(titulo, descricao, metodoConsulta, exibeAnoPeriodo);
		
		if (legendaAnoPeriodo != null) {
			this.legendaAnoPeriodo = legendaAnoPeriodo;
		}
		if (legendaCurso != null) {
			this.legendaCurso = legendaCurso;
		}
		permitirTodosOsCursos = false;
	}
	
	/** Construtor parametrizado.
	 * @param titulo
	 * @param descricao
	 * @param metodoConsulta
	 * @param exibeAnoPeriodo
	 * @param legendaAnoPeriodo
	 * @param legendaCurso
	 * @param quantitativo
	 */
	public RelatorioCurso(String titulo, String descricao, String metodoConsulta, boolean exibeAnoPeriodo, String legendaAnoPeriodo, String legendaCurso, boolean quantitativo) {
		this(titulo, descricao, metodoConsulta, exibeAnoPeriodo, legendaAnoPeriodo, legendaCurso);
		this.quantitativo = quantitativo;
		permitirTodosOsCursos = false;
	}

	/** Retorna o relatório correspondente ao código informado.
	 * @param codigo
	 * @return
	 */
	public static RelatorioCurso getRelatorio(String codigo) {
		return relatorios.get(codigo);
	}
	
	/**
	 * Valida os dados do formulário
	 *
	 * @return
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();

		if ( !permitirTodosOsCursos && isFiltroCursoSelecionado() && (curso == null || curso.getId() <= 0) ) {
			erros.addErro("É necessário informar o curso desejado");
		}

		if (exibeAnoPeriodo) {
			if (ano <= 0) erros.addErro("Informe um ano válido");
			if (periodo <= 0) erros.addErro("Informe um período válido");
		}

		return erros;
	}

	/** Retorna o mapa de relatórios que podem ser gerados por este controller.
	 * @return
	 */
	public static HashMap<String, RelatorioCurso> getRelatorios() {
		return relatorios;
	}

	/** Seta o mapa de relatórios que podem ser gerados por este controller. 
	 * @param relatorios
	 */
	public static void setRelatorios(HashMap<String, RelatorioCurso> relatorios) {
		RelatorioCurso.relatorios = relatorios;
	}
	
	/** Retorna o ano ao qual o relatório se restringe.
	 * @return
	 */
	public int getAno() {
		return this.ano;
	}
	
	/** Seta o ano ao qual o relatório se restringe. 
	 * @param ano
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}
	
	/** Retorna o curso ao qual o relatório se restringe.
	 * @return
	 */
	public Curso getCurso() {
		return this.curso;
	}
	
	/** Seta o curso ao qual o relatório se restringe. 
	 * @param curso
	 */
	public void setCurso(Curso curso) {
		this.curso = curso;
	}
	
	/** Indica se o formulário deve exibir o Ano-Período. 
	 * @return
	 */
	public boolean isExibeAnoPeriodo() {
		return this.exibeAnoPeriodo;
	}
	
	/** Seta se o formulário deve exibir o Ano-Período. 
	 * @param exibeAnoPeriodo
	 */
	public void setExibeAnoPeriodo(boolean exibeAnoPeriodo) {
		this.exibeAnoPeriodo = exibeAnoPeriodo;
	}
	
	/** Retorna a legenda do campo ano-período. Exemplo: "Ano/Período".
	 * @return
	 */
	public String getLegendaAnoPeriodo() {
		return this.legendaAnoPeriodo;
	}
	
	/** Seta a legenda do campo ano-período. Exemplo: "Ano/Período".
	 * @param legendaAnoPeriodo
	 */
	public void setLegendaAnoPeriodo(String legendaAnoPeriodo) {
		this.legendaAnoPeriodo = legendaAnoPeriodo;
	}
	
	/** Retorna a legenda do campo curso.
	 * @return
	 */
	public String getLegendaCurso() {
		return this.legendaCurso;
	}
	
	/** Seta a legenda do campo curso.
	 * @param legendaCurso
	 */
	public void setLegendaCurso(String legendaCurso) {
		this.legendaCurso = legendaCurso;
	}
	
	/** Seta o ano ao qual o relatório se restringe.
	 * @return
	 */
	public int getPeriodo() {
		return this.periodo;
	}
	
	/** Retorna o ano ao qual o relatório se restringe. 
	 * @param periodo
	 */
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	/** Retorna a presentação inteira do ano/período ao qual o relatório se restringe.
	 * @return
	 */
	public int getAnoPeriodo() {
		return Integer.valueOf(ano + "" + periodo);
	}

	/** Indica se o relatório é quantitativo. 
	 * @return
	 */
	public boolean isQuantitativo() {
		return this.quantitativo;
	}

	/** Seta se o relatório é quantitativo. 
	 * @param quantitativo
	 */
	public void setQuantitativo(boolean quantitativo) {
		this.quantitativo = quantitativo;
	}

	/** Indica o tipo de filtro Curso Selecionado. 
	 * @return
	 */
	public int getFiltroCurso() {
		return this.filtroCurso;
	}

	/** Seta o tipo de filtro Curso Selecionado. 
	 * @param filtroCurso
	 */
	public void setFiltroCurso(int filtroCurso) {
		this.filtroCurso = filtroCurso;
	}
	
	/** Retorna a descrição textual do filtro de curso.
	 * @return
	 */
	public String getDescricaoFiltroCurso() {
		return TipoFiltroCurso.getDescricaoFiltro(filtroCurso);
	}
	
	/** Indica se o filtro é do tipo Curso Selecionado. 
	 * @return
	 */
	public boolean isFiltroCursoSelecionado() {
		return filtroCurso == TipoFiltroCurso.CURSO_SELECIONADO;
	}

	/** Indica se o relatório deve exibir no filtro de cursos a opção de o usuário selecionar todos os cursos. 
	 * @return
	 */
	public boolean isPermitirTodosOsCursos() {
		return permitirTodosOsCursos;
	}

	/** Seta se o relatório deve exibir no filtro de cursos a opção de o usuário selecionar todos os cursos. 
	 * @param permitirTodosOsCursos
	 */
	public void setPermitirTodosOsCursos(boolean permitirTodosOsCursos) {
		this.permitirTodosOsCursos = permitirTodosOsCursos;
	}
	
}
