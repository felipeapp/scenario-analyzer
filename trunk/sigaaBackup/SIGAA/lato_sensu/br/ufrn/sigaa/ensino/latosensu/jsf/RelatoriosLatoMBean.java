/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '03/12/2007'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CursoLatoDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.DiscenteLatoDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.RelatoriosLatoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.latosensu.dao.MensalidadeCursoLatoDao;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta;
import br.ufrn.sigaa.ensino.latosensu.negocio.MensalidadeCursoLatoHelper;
import br.ufrn.sigaa.ensino.latosensu.relatorios.LinhaRelatorioSinteticoAlunosCurso;
import br.ufrn.sigaa.ensino.latosensu.relatorios.LinhaRelatorioSinteticoCursosCentro;
import br.ufrn.sigaa.ensino.latosensu.relatorios.LinhaRelatorioSinteticoEntradas;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean para gerar relatórios do lato sensu.
 * @author leonardo
 *
 */
public class RelatoriosLatoMBean extends SigaaAbstractController<Object> {

	// Constantes das views 
	/** JSP do relatório da lista de concluintes. */ 
	public final String JSP_LISTA_CONCLUINTES = "/lato/relatorios/lista_concluintes.jsp";
	/** JSP do relatório da lista de ranking por curso. */ 
	public final String JSP_LISTA_RANKING_CURSO = "/lato/relatorios/lista_ranking_curso.jsp";
	/** JSP do relatório da lista de matriculados concluintes. */ 
	public final String JSP_LISTA_MATRICULADOS_CONCLUINTES = "/lato/relatorios/lista_matriculados_concluintes.jsp";
	/** JSP do relatório da lista de orientadores de TCC. */ 
	public final String JSP_LISTA_ORIENTADORES_TCC = "/lato/relatorios/lista_orientadores_tcc.jsp";
	/** JSP do formulário do relatório de entrada por ano. */ 
	public final String FORM_RELATORIO_ENTRADA_ANO = "/lato/relatorios/form_entradas_ano.jsp";
	/** JSP do relatório de entradas por ano. */ 
	public final String REL_RELATORIO_ENTRADA_ANO = "/lato/relatorios/entradas_ano_sintetico.jsp";
	/** JSP do relatório de alunos por curso. */ 
	public final String REL_RELATORIO_ALUNO_CURSO = "/lato/relatorios/alunos_curso_sintetico.jsp";
	/** JSP do relatório de alunos por curso detalhado. */ 
	public final String REL_RELATORIO_ALUNO_CURSO_DET = "/lato/relatorios/alunos_curso_sintetico_detalhado.jsp";
	/** JSP do relatório de cursos por centro. */
	public final String REL_RELATORIO_CURSO_CENTRO = "/lato/relatorios/cursos_centro_sintetico.jsp";
	/** JSP do formulário do relatório de alunos por curso. */
	public final String FORM_RELATORIO_ALUNO_CURSO = "/lato/relatorios/form_alunos_curso_sintetico.jsp";
	/** JSP do formulário relatório de cursos por centro. */
	public final String FORM_RELATORIO_CURSO_CENTRO = "/lato/relatorios/form_curso_centro.jsp";
	/** JSP do relatório de situação das propostas de cursos. */
	public final String REL_RELATORIO_SITUACAO_PROPOSTA = "/lato/relatorios/rel_situacao_proposta.jsp";
	/** JSP do formulário do relatório de andamento dos cursos. */
	public final String FORM_ANDAMENTO_CURSOS = "/lato/relatorios/form_andamento_cursos.jsp";
	/** JSP do relatório de andamento de cursos. */
	public final String REL_ANDAMENTO_CURSOS = "/lato/relatorios/rel_andamento_cursos.jsp";
	/** JSP do relatório de orientação de cursos. */
	public final String REL_ORIENTACAO_CURSOS = "/lato/relatorios/rel_orientacao_cursos.jsp";
	/** JSP do relatório de orientação de cursos por departamento. */
	public final String REL_ORIENTACAO_CURSOS_DEPARTAMENTO = "/lato/relatorios/rel_orientacao_cursos_depart.jsp";
	/** JSP do relatório de discentes sem orientação. */
	public final String REL_DISCENTES_SEM_ORIENTACAO = "/lato/relatorios/rel_discentes_sem_orientacao.jsp";
	/** JSP do relatório de mensalidades pagas. */
	public final String REL_RELATORIO_MENSALIDADES_PAGAS = "/lato/mensalidade/rel_mensalidades_pagas.jsp";
	
	/** Lista de dados do relatório. */
	private List<HashMap<String,Object>> lista = new ArrayList<HashMap<String,Object>>();
	
	/** Lista auxiliar de dados do relatório. */
	private List<HashMap<String,Object>> listaAuxiliar = new ArrayList<HashMap<String,Object>>();
	
	/** Indica o ano corrente. */
	private Integer ano = CalendarUtils.getAnoAtual();
	
	/** Indica um ano informado pelo usuário para geração do relátorio. */
	private Integer anoInicial = CalendarUtils.getAnoAtual() - 1;
	
	/** Indica o período corrente. */
	private Integer periodo = getPeriodoAtual();
	
	/** Indica o id Curso. */
	private Integer idCurso;
	
	/** Indica o nome do Curso. */
	private String curso;
	
	/** Coleção de itens dos Cursos. */
	private Collection<SelectItem> cursosCombo;

	/** Cursos Latos a serem utilizados na geração do relatório. */
	private Collection<Curso> cursosLato;
	
	/** Discentes a serem utilizados na geração do relatório. */
	private Collection<Discente> discentes;
	
	/** Map para a geração dos relatórios de Lato. */
	private Map<Integer, LinhaRelatorioSinteticoEntradas> relatorioAnoSintetico;
	/** Map para a geração dos relatórios de Lato. */
	private Map<CursoLato, LinhaRelatorioSinteticoAlunosCurso> relatorioCurso;
	/** Map para a geração dos relatórios de Lato. */
	private Map<Unidade, LinhaRelatorioSinteticoCursosCentro> relatorioCursoCentro;
	/** Lista com as datas de vencimentos das GRUs da mensalidade do curso. */
	private List<String> dataVencimentos;
	/** Dados do relatório sintético de alunos do curso. */
	private LinhaRelatorioSinteticoAlunosCurso detalhesCursoLato;
	
	/**
	 * Popula os dados e gera o relatório de alunos concluintes.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/relatorios.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String gerarRelatorioConcluintes() throws DAOException, ArqException {
		RelatoriosLatoDao dao = getDAO(RelatoriosLatoDao.class);
		lista = dao.findListaAlunosConcluintes();
		return forward(JSP_LISTA_CONCLUINTES);
	}
	
	/**
	 * Popula os dados e gera o relatório com o ranking de alunos por curso.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/lato/relatorios/seleciona_ranking_curso.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String gerarRankingCurso() throws DAOException, ArqException {
		RelatoriosLatoDao dao = getDAO(RelatoriosLatoDao.class);
		lista = dao.findRankingCurso(idCurso);
		return forward(JSP_LISTA_RANKING_CURSO);
	}
	
	/**
	 * Popula os dados e gera o relatório com o ranking de alunos por curso.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/lato/menu_coordenador.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String gerarRankingCursoCoordenador() throws DAOException, ArqException {
		RelatoriosLatoDao dao = getDAO(RelatoriosLatoDao.class);
		idCurso = getCursoAtualCoordenacao().getId();
		lista = dao.findRankingCurso(idCurso);
		return forward(JSP_LISTA_RANKING_CURSO);
	}

	/**
	 * Popula os dados e gera o relatório quantitativo de alunos matriculados e
	 * concluintes por centro.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/lato/relatorios/seleciona_matriculados_concluintes.jsp</li>
	 *	</ul> 
	 *
	 * @return
	 * @throws ArqException
	 */
	public String gerarRelatorioMatriculadosEConcluintes() throws ArqException {
		RelatoriosLatoDao dao = getDAO(RelatoriosLatoDao.class);
		if(ano == null || periodo == null){
			addMensagemErro("Informe um ano-período válido.");
			return null;
		}
		lista = dao.findMatriculadosEConcluintes(ano, periodo);
		return forward(JSP_LISTA_MATRICULADOS_CONCLUINTES);
	}
	
	/**
	 * Popula os dados e gera o relatório dos orientadores dos trabalhos finais de curso.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/lato/menu_coordenador.jsp</li>
	 *	</ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioOrientadoresTcc() throws DAOException{
		RelatoriosLatoDao dao = getDAO(RelatoriosLatoDao.class);
		cursosLato = new ArrayList<Curso>();
		cursosLato.add(getCursoAtualCoordenacao());
		lista = dao.findTrabalhoFimCursoByCurso(cursosLato);
		if(lista != null && lista.size() > 0)
			curso = (String)lista.get(0).get("curso");
		return forward(JSP_LISTA_ORIENTADORES_TCC);
	}
	
	/**
	 * Popula os dados e gera o relatório dos orientadores dos trabalhos finais de curso.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/lato/seleciona_curso_orientadores.jsp</li>
	 *	</ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioOrientadoresTccCoordenador() throws DAOException{
		RelatoriosLatoDao dao = getDAO(RelatoriosLatoDao.class);
		cursosLato = new ArrayList<Curso>();
		cursosLato.add(getCursoAtualCoordenacao());
		lista = dao.findTrabalhoFimCursoByCurso(cursosLato);
		if(lista != null && lista.size() > 0)
			curso = (String)lista.get(0).get("curso");
		if (lista.isEmpty()) {
			addMensagemErro("Não foi encontrado nenhum Trabalho Fim de Curso.");
			return null;
		}
		return forward(JSP_LISTA_ORIENTADORES_TCC);
	}
	
	/**
	 * Redireciona para a página do relatório sintético de alunos ingressantes por ano.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/portais/rh_plan/abas/pos-graduacao.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/relatorios.jsp</li>
	 *	</ul>
	 * @return
	 */
	public String popularEntradasAnoSintetico() {
		return forward(FORM_RELATORIO_ENTRADA_ANO);
	}
	
	/**
	 * Popula os dados e gera o relatório sintético de alunos ingressantes por ano.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/lato/relatorios/form_entradas_ano.jsp</li>
	 *	</ul>
	 *
	 * @return
	 * @throws Exception
	 */
	public String entradasAnoSintetico() throws Exception {
		
		RelatoriosLatoDao dao = getDAO(RelatoriosLatoDao.class);
		
		// Validar formulário
		ListaMensagens lista = new ListaMensagens();
		
		ValidatorUtil.validaInt(anoInicial, "Ano Inicial", lista);
		ValidatorUtil.validaInt(ano, "Ano Final", lista);
		
		if ( !lista.isEmpty() ) {
			addMensagens(lista);
			return forward(FORM_RELATORIO_ENTRADA_ANO);
		}
		if (anoInicial > ano){
			addMensagemErro("O Ano Inicial deve ser menor que o Ano Final");
			return forward(FORM_RELATORIO_ENTRADA_ANO);
		}
		
		// Realizar a consulta
		relatorioAnoSintetico = dao.findEntradasAno(anoInicial, ano);
		
		if ( relatorioAnoSintetico == null || relatorioAnoSintetico.isEmpty() ) {
			addMensagemErro("Não foram encontradas entradas para os critérios informados");
			return forward(FORM_RELATORIO_ENTRADA_ANO);
		}
		return forward(REL_RELATORIO_ENTRADA_ANO);
	}
	
	/**
	 * Inicia a busca pelo ano dos alunso pro curso.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/relatorios.jsp</li>
	 * </ul>
	 */
	public String iniciarBuscaAlunosCursoSintetico(){
		return forward(FORM_RELATORIO_ALUNO_CURSO);
	}

	/**
	 * Inicia a busca pelo ano dos alunso pro curso.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/relatorios.jsp</li>
	 * </ul>
	 */
	public String iniciarBuscaCursoCentro(){
		return forward(FORM_RELATORIO_CURSO_CENTRO);
	}

	/**
	 * Responsável pela geração de um relatório com os curso que apresentam as situações informadas.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/relatorios.jsp</li>
	 * </ul>
	 * 	
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioStatusProposta() throws DAOException{
		CursoLatoDao dao = getDAO(CursoLatoDao.class);
		try {
			cursosLato = dao.findAllBySituacoes( SituacaoProposta.ACEITA, SituacaoProposta.NAO_ACEITA );
		} finally {
			dao.close();
		}
		return forward(REL_RELATORIO_SITUACAO_PROPOSTA);
	}

	/**
	 * Direciona o usuário para a tela de busca. 
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/relatorios.jsp</li>
	 * </ul>
	 */
	public String inicioAndamentoDosCursos(){
		return forward(FORM_ANDAMENTO_CURSOS);
	}
	
	/** Gera o relatório de andamento de cursos.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/lato/relatorios/rel_andamento_cursos.jsp</li>
	 * </ul>
	 * @return
	 * @throws LimiteResultadosException
	 */
	public String gerarRelatorioAndamentoCursos() throws LimiteResultadosException {
		CursoLatoDao dao = getDAO(CursoLatoDao.class);
		validateMinValue(anoInicial, 1900, "Ano Inicial", erros);
		validateMinValue(ano, 1900, "Ano Final", erros);
		if (anoInicial > 0 && ano > 0 && anoInicial > ano) 
			addMensagemErro("O Ano Inicial não pode ser posterior ao Ano Final.");
		if (hasErrors()) return null;
		try {
			lista = dao.findAndamentoCursoLato(anoInicial, ano);
		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		
		if ( isEmpty(lista) ) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		return forward(REL_ANDAMENTO_CURSOS);
	}

	/**
	 * Geração dos relatórios de Orientações de todos os cursos ativos.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/lato/relatorios/form_alunos_curso_sintetico.jsp</li>
	 * </ul>
	 * 
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private void relatoriosOrientacoes() throws HibernateException, DAOException {
		RelatoriosLatoDao dao = getDAO(RelatoriosLatoDao.class);
		CursoLatoDao cursodao = getDAO(CursoLatoDao.class);
		try {
			cursosLato = cursodao.findAllBySituacoes( SituacaoProposta.ACEITA );
			lista = dao.findTrabalhoFimCursoByCurso(cursosLato);
		} finally {
			dao.close();
			cursodao.close();
		}
	}

	/**
	 * Gera o relatório de todas as orientações por curso.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/lato/relatorios/form_alunos_curso_sintetico.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String gerarRelatorioOrientacoes() throws HibernateException, DAOException{
		relatoriosOrientacoes();
		return forward(REL_ORIENTACAO_CURSOS); 
	}
	
	/**
	 * Gera o relatório de todas as orientações organizado por departamento.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/lato/relatorios/form_alunos_curso_sintetico.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String gerarRelatorioOrientacoesDepartamento() throws HibernateException, DAOException {
		relatoriosOrientacoes();
		return forward(REL_ORIENTACAO_CURSOS_DEPARTAMENTO); 
	}
	
	/** Gera o relatório de discentes sem orientação.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/lato/relatorios/form_alunos_curso_sintetico.jsp</li>
	 * </ul>
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String gerarRelatorioDiscenteSemOrientacao() throws HibernateException, DAOException{
		relatoriosOrientacoes();
		
		Collection<Integer> ids = new ArrayList<Integer>();
		for (HashMap<String, Object> linha : lista) {
			ids.add((Integer) linha.get("id_discente"));
		}
		
		DiscenteLatoDao dao = getDAO(DiscenteLatoDao.class);
		try {
			discentes = dao.findByIds(ids);
		} finally {
			dao.close();
		}
		
		return forward(REL_DISCENTES_SEM_ORIENTACAO);
	}
	
	/**
	 * Popula os dados e gera o relatório sintético de alunos por curso.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/portais/rh_plan/abas/pos-graduacao.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/relatorios.jsp</li>
	 *	</ul>
	 *
	 * @return
	 * @throws Exception
	 */
	public String alunosCursoSintetico() throws Exception {
		
		RelatoriosLatoDao dao = getDAO(RelatoriosLatoDao.class);
		
		// Realizar a consulta
		 relatorioCurso = dao.findAlunosCurso(anoInicial, ano, null);
		
		if ( relatorioCurso == null || relatorioCurso.isEmpty() ) {
			addMensagemErro("Não foi possível gerar o relatório");
			return null;
		}

		return forward(REL_RELATORIO_ALUNO_CURSO);
	}
	
	/**
	 * Exibe os detalhes do curso
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/lato/relatorios/alunos_curso_sintetico.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public String detalharCursoSintetico() throws DAOException {
		
		Integer id = getParameterInt("id");
		
		
		RelatoriosLatoDao dao = getDAO(RelatoriosLatoDao.class);
		CursoLato cursoLato = dao.findByPrimaryKey(id, CursoLato.class);
		
		detalhesCursoLato = dao.detalharAlunosCursoSintetico(cursoLato);
		
		
		return forward(REL_RELATORIO_ALUNO_CURSO_DET);
	}
	
	/** Exibe os detalhes do curso por centro.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/lato/relatorios/cursos_centro_sintetico.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public String detalharCentroCursoSintetico() throws DAOException {
		
		Integer id = getParameterInt("id");
		
		
		RelatoriosLatoDao dao = getDAO(RelatoriosLatoDao.class);
		relatorioCurso = dao.findAlunosCurso(anoInicial, ano, new Unidade(id));

		return forward(REL_RELATORIO_ALUNO_CURSO);		
		
	}
	
	/**
	 * Popula os dados e gera o relatório sintético de cursos por centro.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/portais/rh_plan/abas/pos-graduacao.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/relatorios.jsp</li>
	 *	</ul>
	 *
	 * @return
	 * @throws Exception
	 */
	public String cursosCentroSintetico() throws Exception {
		
		RelatoriosLatoDao dao = getDAO(RelatoriosLatoDao.class);
		
		// Realizar a consulta
		relatorioCursoCentro = dao.findCursosCentro(anoInicial, ano);
		
		if ( relatorioCursoCentro == null || relatorioCursoCentro.isEmpty() ) {
			addMensagemErro("Não foi possível gerar o relatório");
			return null;
		}
		
		return forward(REL_RELATORIO_CURSO_CENTRO);
	}
	
	/**
	 * Retorna a quantidade de registros obtidos na consulta.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/lato/relatorios/lista_concluintes.jsp</li>
	 *		<li>sigaa.war/lato/relatorios/lista_orientadores_tcc.jsp</li>
	 *		<li>sigaa.war/lato/relatorios/lista_ranking_curso.jsp</li>
	 *	</ul>
	 *
	 * @return
	 */
	public int getNumeroRegistrosEncontrados() {
		if(lista!=null)
			return lista.size();
		else
			return 0;
	}
	
	/** Inicia o relatório de mensalidades pagas.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/lato/relatorios/form_alunos_curso_sintetico.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciarRelatorioMensalidadesPagas() {
		idCurso = 0;
		return forward("/lato/mensalidade/seleciona_curso.jsp");
	}
	
	/** Gera o relatório de mensalidades pagas.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/lato/relatorios/seleciona_curso.jsp</li>
	 * </ul>
	 * @return
	 * @throws HibernateException
	 * @throws ArqException 
	 */
	public String gerarRelatorioMensalidadesPagas() throws HibernateException, ArqException {
		MensalidadeCursoLatoDao dao = getDAO(MensalidadeCursoLatoDao.class);
		int id = 0;
		if (isPortalCoordenadorLato())
			id = getCursoAtualCoordenacao().getId();
		else
			id = idCurso;
		validateRequiredId(id, "Curso", erros);
		CursoLato cursoLato = dao.findByPrimaryKey(id, CursoLato.class);
		if (cursoLato.getDataInicio() == null)
			addMensagemErro("Não foi definida a data de início do curso");
		if (isEmpty(cursoLato.getQtdMensalidades()))
			addMensagemErro("Na proposta do curso não foi definido a quantidade de mensalidades do curso");
		if (hasErrors()) return null;
		lista = dao.findDadosRelatorioMensalidadesPagas(id);
		if (isEmpty(lista)) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		} else {
			quitarMensalidadesPagas(cursoLato);
			lista = dao.findDadosRelatorioMensalidadesPagas(id);
		}
		listaAuxiliar = dao.findDadosRelatorioMensalidadesValoresPagos(id);
		CursoLato curso = dao.findByPrimaryKey(id, CursoLato.class);
		this.curso = curso.getDescricaoCompleta();
		dataVencimentos = new LinkedList<String>();
		for (Date vencimento : MensalidadeCursoLatoHelper.getDatasVencimento(curso)) {
			dataVencimentos.add(String.format("%1$td/%1$tm/%1$tY", vencimento));
		}
		return forward(REL_RELATORIO_MENSALIDADES_PAGAS);
	}

	/** Marca as mensalidades que tiveram a GRU paga como quitadas.
	 * @param cursoLato 
	 * @throws ArqException
	 */
	private void quitarMensalidadesPagas(CursoLato cursoLato) throws ArqException {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(cursoLato);
		mov.setCodMovimento(SigaaListaComando.QUITAR_MENSALIDADES_CURSO_LATO);
		prepareMovimento(SigaaListaComando.QUITAR_MENSALIDADES_CURSO_LATO);
		try {
			execute(mov);
		} catch (Exception e) {
			if (e instanceof NegocioException)
				addMensagemErro(e.getMessage());
			else
				addMensagemErroPadrao();
			notifyError(e);
		}
	}
	
	public List<HashMap<String, Object>> getLista() {
		return lista;
	}

	public void setLista(List<HashMap<String, Object>> lista) {
		this.lista = lista;
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

	public Integer getIdCurso() {
		return idCurso;
	}

	public void setIdCurso(Integer idCurso) {
		this.idCurso = idCurso;
	}

	public Collection<SelectItem> getCursos() {
		return cursosCombo;
	}

	public void setCursos(Collection<SelectItem> cursos) {
		this.cursosCombo = cursos;
	}

	/** Retorna uma lista de cursos de lato sensu.
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public Collection<SelectItem> getCursosCombo() throws DAOException, ArqException {
		if (cursosCombo == null)
			cursosCombo = toSelectItems(getGenericDAO().findByExactField(Curso.class, "nivel", NivelEnsino.LATO), "id", "descricao");
		return cursosCombo;
	}

	public void setCursosCombo(Collection<SelectItem> cursosCombo) {
		this.cursosCombo = cursosCombo;
	}

	public Integer getAnoInicial() {
		return anoInicial;
	}

	public void setAnoInicial(Integer anoInicial) {
		this.anoInicial = anoInicial;
	}

	public Map<Integer, LinhaRelatorioSinteticoEntradas> getRelatorioAnoSintetico() {
		return relatorioAnoSintetico;
	}

	public void setRelatorioAnoSintetico(
			Map<Integer, LinhaRelatorioSinteticoEntradas> relatorioAnoSintetico) {
		this.relatorioAnoSintetico = relatorioAnoSintetico;
	}

	public Map<CursoLato, LinhaRelatorioSinteticoAlunosCurso> getRelatorioCurso() {
		return relatorioCurso;
	}

	public void setRelatorioCurso(
			Map<CursoLato, LinhaRelatorioSinteticoAlunosCurso> relatorioCurso) {
		this.relatorioCurso = relatorioCurso;
	}

	public Map<Unidade, LinhaRelatorioSinteticoCursosCentro> getRelatorioCursoCentro() {
		return relatorioCursoCentro;
	}

	public void setRelatorioCursoCentro(
			Map<Unidade, LinhaRelatorioSinteticoCursosCentro> relatorioCursoCentro) {
		this.relatorioCursoCentro = relatorioCursoCentro;
	}

	public String getCurso() {
		return curso;
	}

	public void setCurso(String curso) {
		this.curso = curso;
	}

	public Collection<Curso> getCursosLato() {
		return cursosLato;
	}

	public void setCursosLato(Collection<Curso> cursosLato) {
		this.cursosLato = cursosLato;
	}

	public Collection<Discente> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(Collection<Discente> discentes) {
		this.discentes = discentes;
	}

	public List<String> getDataVencimentos() {
		return dataVencimentos;
	}

	public List<HashMap<String, Object>> getListaAuxiliar() {
		return listaAuxiliar;
	}

	public void setListaAuxiliar(List<HashMap<String, Object>> listaAuxiliar) {
		this.listaAuxiliar = listaAuxiliar;
	}

	public LinhaRelatorioSinteticoAlunosCurso getDetalhesCursoLato() {
		return detalhesCursoLato;
	}

	public void setDetalhesCursoLato(
			LinhaRelatorioSinteticoAlunosCurso detalhesCursoLato) {
		this.detalhesCursoLato = detalhesCursoLato;
	}

}