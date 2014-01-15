/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.graduacao.RelatorioDiscenteSqlDao;
import br.ufrn.sigaa.arq.jsf.AbstractControllerRelatorio;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.TipoFiltroCurso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.graduacao.relatorios.dominio.RelatorioCurso;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;

/**
 * Controlador Geral para os relat�rios que possuem o curso como par�metro.
 *
 * @author Ricardo Wendell
 *
 */
public class RelatorioPorCursoMBean extends AbstractControllerRelatorio {

	/** Relat�rio a ser gerado. */
	private RelatorioCurso relatorio;

	/**
	 * Inicia consulta do relat�rio.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  /graduacao/menus/coordenacao.jsp
	 *  /graduacao/menus/relatorios_dae.jsp
	 *  /portais/rh_plan/abas/graduacao.jsp
	 * 
	 * @return
	 */
	public String iniciar() {
		
		relatorio = RelatorioCurso.getRelatorio(getParameter("relatorio"));
		
		Boolean permiteTodosCursos = getParameterBoolean("permitirTodosOsCursos");
		if( permiteTodosCursos )
			relatorio.setPermitirTodosOsCursos( true );
		else
			relatorio.setPermitirTodosOsCursos( false );

		if ( relatorio == null ) {
			addMensagemErroPadrao();
			return null;
		}

		// Popular dados iniciais
		if (relatorio.getAno() == 0) {
			relatorio.setAno( getCalendarioVigente().getAno() );
		}
		if (relatorio.getPeriodo() == 0) {
			relatorio.setPeriodo( getCalendarioVigente().getPeriodo() );
		}
		
		if( relatorio.getCurso() == null )
			relatorio.setCurso(new Curso());

		return forward("/graduacao/relatorios/form_curso.jsp");
	}

	/** Gera o relat�rio por curso.
	 * M�todo chamado pela(s) seguinte(s) JSP(s): /graduacao/relatorios/form_curso.jsp
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorio() throws DAOException {
		
		// Validar dados informados
		ListaMensagens erros = relatorio.validate();
		if ( !erros.isEmpty() ) {
			addMensagens(erros);
			return null;
		}
		
		// Popular dados a serem apresentados no cabe�alho do relat�rio
		popularDadosCabecalho();

		// Realizar a consulta do relat�rio selecionado
		try {
			Method metodo = getClass().getMethod("gerarRelatorio" + relatorio.getMetodoConsulta());
			return (String) metodo.invoke(this);
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Popular dados a serem utilizados para a exibi��o do cabe�alho do relat�rio.
	 *
	 * @throws DAOException
	 *
	 */
	private void popularDadosCabecalho() throws DAOException {
		GenericDAO dao = getGenericDAO();

		// Popular curso
		if ( getRelatorio().isFiltroCursoSelecionado() ) {
			Curso curso = dao.findByPrimaryKey(relatorio.getCurso().getId(), Curso.class);
			relatorio.setCurso(curso);
		}
	}

	/**
	 * Define o resultado para ser utilizado na view
	 *
	 * @param resultado
	 */
	private void prepararResultado(List<Map<String, Object>> resultado) {
		getCurrentRequest().setAttribute("resultado", resultado);
	}

	/**
	 * Popula a cole��o de cursos que o usu�rio tem acesso.
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getCursosCombo() throws DAOException {
		CursoDao dao = getDAO(CursoDao.class);
		
		//  Definir restri��es dos cursos
		Unidade centro = null;
		if (isUserInRole(SigaaPapeis.SECRETARIA_CENTRO)) {
			centro = getAcessoMenu().getSecretariaCentro(); 
		}
		
		Collection<Curso> cursos = dao.findByNivel( NivelEnsino.GRADUACAO, centro );
		return toSelectItems(cursos, "id", "descricao");
	}

	//M�TODOS DE CONSULTAS DOS RELAT�RIOS

	/** Gera o relat�rio de discentes com o Percentual de CH Cumprida.
	 * M�todo n�o invocado por JSP.
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioPercentualCHAluno() throws DAOException{
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		List<Map<String,Object>> resultado = dao.findListaPercentualCHAluno(relatorio.getCurso());

		prepararResultado(resultado);
		return forward("/graduacao/relatorios/discente/lista_percentual_ch.jsp");
	}

	/** Gera o relat�rio de discentes concluintes
	 * M�todo n�o invocado por JSP.
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioAlunosConcluintes() throws DAOException, HibernateException{
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(relatorio.getCurso());
		List<Map<String, Object>> todosGraduandos = dao.findGraduandosByAnoPeriodo(relatorio.getCurso(), cal.getAno(), cal.getPeriodo(), false);
		List<Map<String, Object>> resultado = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> outrosGraduandos = new ArrayList<Map<String,Object>>();
		for (Map<String, Object> linha : todosGraduandos) {
			Boolean graduandoNoAnoPeriodoAtual = (Boolean) linha.get("graduando_no_ano_periodo");
			if (graduandoNoAnoPeriodoAtual != null && !graduandoNoAnoPeriodoAtual) {
				outrosGraduandos.add(linha);
			} else {
				resultado.add(linha);
			}
		}
		prepararResultado(resultado);
		
		if( isEmpty(resultado) && isEmpty(outrosGraduandos) ){
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		getCurrentRequest().setAttribute("outrosGraduandos", outrosGraduandos);
		return forward("/graduacao/relatorios/discente/lista_concluintes.jsp");
	}
	
	/** Gera o relat�rio de discentes formandos.
	 * M�todo n�o invocado por JSP.
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioAlunosFormandos() throws DAOException, HibernateException{
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		List<Map<String, Object>> resultado = dao.findListaAlunosFormandos(relatorio.getCurso());
		prepararResultado(resultado);
		return forward("/graduacao/relatorios/discente/lista_concluintes.jsp");
	}

	/** Gera o relat�rio de discentes ativos com prazo de conclus�o.
	 * M�todo n�o invocado por JSP.
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioAlunosAtivosPorPrazoConclusao() throws DAOException{
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		List<Map<String, Object>> resultado = dao.findListaAlunoPrazoConclusao(relatorio.getCurso(), relatorio.getAno(), relatorio.getPeriodo());
		prepararResultado(resultado);
		
		if (resultado.isEmpty()){
		addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		}else{
		return forward("/graduacao/relatorios/discente/lista_prazo_conclusao.jsp");
		}
	return "";	
	}
	
	/** Gera o relat�rio de discentes ativos por curso.
	 * M�todo n�o invocado por JSP.
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioAlunosAtivosPorCurso() throws DAOException{
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		List<Map<String, Object>> resultado = dao.findListaAlunoAtivo(relatorio.getCurso());

		prepararResultado(resultado);
		return forward("/graduacao/relatorios/discente/lista_ativos_curso.jsp");
	}
	
	 /** Gera o relat�rio de discentes matriculados e n�o matriculados por curso.
		 * M�todo n�o invocado por JSP.
		 * @return
		 * @throws DAOException
		 */
	 public String gerarRelatorioAlunosAtivosNaoMatriculadosPorCurso() throws DAOException{
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		List<Map<String, Object>> resultado = dao.findListaAlunoAtivoNaoMatriculado(relatorio.getCurso());

		prepararResultado(resultado);
		return forward("/graduacao/relatorios/discente/lista_ativos_nao_matriculados_curso.jsp");
	}

   /** Gera o relat�rio de discentes matriculados em disciplinas com carga hor�ria de est�gio.
	 * M�todo n�o invocado por JSP.
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioAlunosMatriculadosDisciplinaCHEstagio() throws DAOException{
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		List<Map<String, Object>> resultado = dao.findListaAlunoMatriculadosEstagio(null, relatorio.getCurso());

		prepararResultado(resultado);
		return forward("/graduacao/relatorios/discente/lista_matriculado_estagio.jsp");
	}

	/** Gera o relat�rio de discentes com prazo m�ximo de conclus�o, carga hor�ria total e carga hor�ria integralizada.
	 * M�todo n�o invocado por JSP.
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioAlunosPorPrazoMaximoConclusao() throws DAOException{
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		List<Map<String, Object>> resultado = dao.findListaAlunoPrazoConclusaoCh(null, relatorio.getCurso(), relatorio.getAnoPeriodo());

		prepararResultado(resultado);
		return forward("/graduacao/relatorios/discente/lista_prazo_conclusao_ch.jsp");
	}
	
	/** Gera o relat�rio de discentes por cidade de resid�ncia.
	 * M�todo n�o invocado por JSP.
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioAlunosPorCidadeResidencia() throws DAOException{
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		List<Map<String, Object>> resultado = dao.findListaAlunoCidadeResidencia(null, relatorio.getCurso());

		prepararResultado(resultado);
		return forward("/graduacao/relatorios/discente/lista_cidade_residencia.jsp");
	}

	/** Gera o relat�rio de discentes com carga hor�ria detalhada.
	 * M�todo n�o invocado por JSP.
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioAlunosComDetalhamentosCH() throws DAOException{
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		List<Map<String, Object>> resultado = dao.findListaAlunoChDetalhada(null, relatorio.getCurso());

		prepararResultado(resultado);
		return forward("/graduacao/relatorios/discente/lista_ch_detalhada.jsp");
	}

	/** Gera o relat�rio de discentes com prazo de conclus�o para o semestre atual.
	 * M�todo n�o invocado por JSP.
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioAlunosComPrazoConclusaoSemestreAtual() throws DAOException{
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);

		relatorio.setAno(getCalendarioVigente().getAno() );
		relatorio.setPeriodo(getCalendarioVigente().getPeriodo());
		List<Map<String, Object>> resultado = dao.findListaAlunoPrazoSemestreAtual(relatorio.getAnoPeriodo(), null, relatorio.getCurso());

		prepararResultado(resultado);
		return forward("/graduacao/relatorios/discente/lista_prazo_semestre_atual.jsp");
	}
	
	/** Gera o relat�rio de discentes com empr�stimos pendentes na biblioteca.
	 * M�todo n�o invocado por JSP.
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioGraduandosComEmprestimoPendenteBiblioteca() throws DAOException {
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);

		List<Map<String, Object>> resultado = dao.findGraduandosComEmprestimoPendenteBiblioteca(relatorio.getCurso().getId());

		if (ValidatorUtil.isEmpty(resultado)) {
			addMensagemWarning("N�o h� discentes com empr�stimo pendente na biblioteca para este curso.");
			return null;
		}
		prepararResultado(resultado);
		return forward("/graduacao/relatorios/discente/lista_graduandos_pendente_biblioteca.jsp");
	}

	//Quantitativos
	
	/** Gera o relat�rio de quantitativo de discentes concluintes.
	 * M�todo n�o invocado por JSP.
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioQuantitativoAlunosConcluintes() throws DAOException{
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		List<Map<String, Object>> resultado = dao.findQuantitativoAlunosConcluintes(relatorio.getCurso(),
				relatorio.getFiltroCurso() ,relatorio.getAno(),relatorio.getPeriodo());

		prepararResultado(resultado);
		return forward("/graduacao/relatorios/discente/quantitativo_concluintes.jsp");
	}

	/** Gera o relat�rio de quantitativo de discentes ativos e matriculados.
	 * M�todo n�o invocado por JSP.
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioQuantitativoAlunosMatriculados() throws DAOException{
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		List<Map<String, Object>> resultado = dao.findQuantitativoAlunoMatriculados(relatorio.getCurso(),
				relatorio.getFiltroCurso(), relatorio.getAno(),relatorio.getPeriodo());

		prepararResultado(resultado);
		return forward("/graduacao/relatorios/discente/quantitativo_matriculados.jsp");
	}

	/** Gera o relat�rio de quantitativo de discentes cadastrados (do vestibular) sem matr�cula em componente curricular.
	 * M�todo n�o invocado por JSP.
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioQuantitativoAlunosCadastradosVestibularSemMatricula() throws DAOException{
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		List<Map<String, Object>> resultado = dao.findQuantitativoAlunoVestibularSemMatricula(relatorio.getCurso(),
				relatorio.getFiltroCurso(), relatorio.getAno(),relatorio.getPeriodo());

		prepararResultado(resultado);
		return forward("/graduacao/relatorios/discente/quantitativo_vest_sem_mat.jsp");
	}

	/** Gera o relat�rio de quantitativo de discentes graduandos por curso.
	 * M�todo n�o invocado por JSP.
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioQuantitativoAlunosGraduandos() throws DAOException{
		RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
		List<Map<String, Object>> resultado = dao.findQuantitativoAlunoGraduando(null, relatorio.getCurso(), relatorio.getFiltroCurso());

		prepararResultado(resultado);
		return forward("/graduacao/relatorios/discente/quantitativo_graduando.jsp");
	}

	// GETTERS E SETTERS

	/** Retorna o relat�rio a ser gerado.
	 * @return
	 */
	public RelatorioCurso getRelatorio() {
		return this.relatorio;
	}

	/** Seta o relat�rio a ser gerado. 
	 * @param relatorio
	 */
	public void setRelatorio(RelatorioCurso relatorio) {
		this.relatorio = relatorio;
	}

	/** Retorna uma cole��o de selectItem de filtros por curso.
	 * @return
	 */
	public Collection<SelectItem> getAllFiltrosCursoCombo() {
		return TipoFiltroCurso.getAllCombo();
	}

}
