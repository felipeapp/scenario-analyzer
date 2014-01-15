package br.ufrn.sigaa.ead.jsf;

import java.io.IOException;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ead.RelatorioAlunosEadDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;

/**
 * Managed Bean que gerencia gera o relatório com as notas dos alunos de ead.
 * 
 * @author Fred_Castro
 */

@SuppressWarnings("unchecked")
@Component("relatorioAlunosEad")
@Scope("request")
public class RelatorioAlunosEadMBean extends SigaaAbstractController {
	
	/** Link para a página do formulário. */
	public static final String PAGINA_ESCOLHER_CURSOS = "/ead/relatorios/formEscolherCursos.jsp";
	/** Link para a página do relatório. */
	public static final String PAGINA_RELATORIO = "/ead/relatorios/relatorioNotas.jsp";
	
	/** Lista de cursos */
	private List <Curso> cursos;
	
	/** Lista contendo os dados das disciplinas dos cursos buscados */
	private List <String []> linhas;
	
	/** Identificador do curso que tera ás disciplinas buscadas*/
	private int idCurso;
	
	/** Nome do Curso*/
	private String nomeCurso;
	
	/** Ano das disciplinas buscadas*/
	private int ano;
	
	/** Semestre das disciplinas buscadas*/
	private int semestre;
	
	/** Se deve buscar apenas turmas abertas */
	private boolean turmaAberta;
	
	/** Total de turmas abertas */
	private int totalTurmasAbertas;
	/** Total de turmas à definir docente */
	private int totalTurmasADefinirDocente;
	/** Total de turmas consolidadas */
	private int totalTurmasConsolidadas;
	/** Total de turmas excluídas */
	private int totalTurmasExcluidas;
	/** Total de alunos matriculados */
	private int totalAlunosMatriculados;
	/** Total de alunos aprovados */
	private int totalAlunosAprovados;
	/** Total de alunos reprovados */
	private int totalAlunosReprovados;
	/** Total de alunos trancados */
	private int totalAlunosTrancados;
	
	public RelatorioAlunosEadMBean(){}

	
	public static String getPaginaEscolherCursos() {
		return PAGINA_ESCOLHER_CURSOS;
	}

	public static String getPaginaRelatorio() {
		return PAGINA_RELATORIO;
	}

	/**
	 * Gera o arquivo contendo os inserts com os dados das tabelas selecionadas.
	 * chamado em /ead/relatorios/formEscolherTabelasSql.jsp
	 * 
	 * @return
	 * @throws ArqException
	 * @throws IOException 
	 */
	public String gerar () throws ArqException{
		checkRole(SigaaPapeis.SEDIS);
		
		RelatorioAlunosEadDao dao = null;
		
		try {
			dao = getDAO(RelatorioAlunosEadDao.class);
			linhas = dao.geraRelatorio(idCurso, ano, semestre, turmaAberta);
			setTotais();
			return forward(PAGINA_RELATORIO);
			
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Retorna para o formulário de escolher cursos.
	 * chamado em /ead/menu.jsp
	 * @return
	 */
	public String selecionarCursos () throws SegurancaException{
		checkRole(SigaaPapeis.SEDIS);
		return forward(PAGINA_ESCOLHER_CURSOS);
	}

	/**
	 * Busca e retorna os cursos de ead;
	 * chamado em /ead/relatorios/formEscolherCursos.jsp
	 * @return
	 * @throws ArqException
	 */
	public List<Curso> getCursos() throws ArqException {
		
		if (cursos == null){
			RelatorioAlunosEadDao dao = null;
			try {
				dao = getDAO(RelatorioAlunosEadDao.class);
				cursos = dao.findAllCursosEad();
			} finally {
				if (dao != null)
					dao.close();
			}
		}
			
		return cursos;
	}
	
	/**
	 * Retorna o combo com os cursos;
	 * 
	 * @return
	 * @throws ArqException
	 */
	public List <SelectItem> getCursosCombo () throws ArqException{
		return toSelectItems(getCursos(), "id", "nome");
	}

	public void setCursos(List<Curso> cursos) {
		this.cursos = cursos;
	}

	public int getIdCurso() {
		return idCurso;
	}

	/**
	 * Seta a id do curso, atribuindo o nome correto do mesmo.
	 * 
	 * @param idCurso
	 * @throws ArqException
	 */
	public void setIdCurso(int idCurso) throws ArqException {
		this.idCurso = idCurso;
		
		if (idCurso <= 0)
			nomeCurso = "Todos";
		else {
			
			List <Curso> cs = getCursos();
			
			try {
			for (Curso c : cs)
				if (c.getId() == idCurso)
					nomeCurso = c.getNome(); 
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * Calcula as estatísticas do curso.
	 * 
	 * @throws ArqException
	 */
	public void setTotais () {
		totalTurmasAbertas = 0;
		totalTurmasADefinirDocente = 0;
		totalTurmasConsolidadas = 0;
		totalTurmasExcluidas = 0;
		totalAlunosMatriculados = 0;
		totalAlunosAprovados = 0;
		totalAlunosReprovados = 0;
		totalAlunosTrancados = 0;
		
		for (Object[] linha : linhas){
				if ( (Integer) linha[3] == SituacaoTurma.ABERTA)
					totalTurmasAbertas++;
				if ( (Integer) linha[3] == SituacaoTurma.A_DEFINIR_DOCENTE)
					totalTurmasADefinirDocente++;
				if ( (Integer) linha[3] == SituacaoTurma.CONSOLIDADA)
					totalTurmasConsolidadas++;
				if ( (Integer) linha[3] == SituacaoTurma.EXCLUIDA)
					totalTurmasExcluidas++;
				
				totalAlunosMatriculados+= ((Number) linha[6]).intValue();
				totalAlunosAprovados+= ((Number) linha[7]).intValue();
				totalAlunosReprovados+= ((Number) linha[8]).intValue();
				totalAlunosTrancados+= ((Number) linha[9]).intValue();
		}
				
	}
	
	public List<String[]> getLinhas() {
		return linhas;
	}

	public void setLinhas(List<String[]> linhas) {
		this.linhas = linhas;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getSemestre() {
		return semestre;
	}

	public void setSemestre(int semestre) {
		this.semestre = semestre;
	}

	public String getNomeCurso() {
		return nomeCurso;
	}

	public void setNomeCurso(String nomeCurso) {
		this.nomeCurso = nomeCurso;
	}

	public void setTurmaAberta(boolean turmaAberta) {
		this.turmaAberta = turmaAberta;
	}

	public boolean isTurmaAberta() {
		return turmaAberta;
	}
	
	public int getTotalTurmasAbertas() {
		return totalTurmasAbertas;
	}

	public void setTotalTurmasAbertas(int totalTurmasAbertas) {
		this.totalTurmasAbertas = totalTurmasAbertas;
	}

	public int getTotalTurmasADefinirDocente() {
		return totalTurmasADefinirDocente;
	}

	public void setTotalTurmasADefinirDocente(int totalTurmasADefinirDocente) {
		this.totalTurmasADefinirDocente = totalTurmasADefinirDocente;
	}

	public int getTotalTurmasConsolidadas() {
		return totalTurmasConsolidadas;
	}

	public void setTotalTurmasConsolidadas(int totalTurmasConsolidadas) {
		this.totalTurmasConsolidadas = totalTurmasConsolidadas;
	}

	public int getTotalAlunosMatriculados() {
		return totalAlunosMatriculados;
	}

	public void setTotalAlunosMatriculados(int totalAlunosMatriculados) {
		this.totalAlunosMatriculados = totalAlunosMatriculados;
	}

	public int getTotalAlunosAprovados() {
		return totalAlunosAprovados;
	}

	public void setTotalAlunosAprovados(int totalAlunosAprovados) {
		this.totalAlunosAprovados = totalAlunosAprovados;
	}

	public int getTotalAlunosReprovados() {
		return totalAlunosReprovados;
	}

	public void setTotalAlunosReprovados(int totalAlunosReprovados) {
		this.totalAlunosReprovados = totalAlunosReprovados;
	}

	public int getTotalAlunosTrancados() {
		return totalAlunosTrancados;
	}

	public void setTotalAlunosTrancados(int totalAlunosTrancados) {
		this.totalAlunosTrancados = totalAlunosTrancados;
	}


	public void setTotalTurmasExcluidas(int totalTurmasExcluidas) {
		this.totalTurmasExcluidas = totalTurmasExcluidas;
	}


	public int getTotalTurmasExcluidas() {
		return totalTurmasExcluidas;
	}
}