/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;

/**
 * Relatório para buscar todos os alunos matriculados em uma disciplina
 * 
 * @author Henrique André
 *
 */
@Component("relatorioAlunosEmDisciplinas")
@Scope("request")
public class RelatorioAlunosEmDisciplinasMBean extends SigaaAbstractController {

	private Integer ano;
	private Integer periodo;
	private ComponenteCurricular disciplina = new ComponenteCurricular();
	private String tituloRelatorio;
	/** Resultado **/
	private List<MatriculaComponente> matriculas;

	/**
	 * Direciona o usuário para o formulário
	 * @return
	 */
	public String iniciarRelatorio() {
		clear();
		return forward("/graduacao/relatorios/discente/seleciona_alunos_por_disciplina.jsp");
	}

	/**
	 * Limpa o formulário
	 */
	private void clear() {
		ano = getAnoAtual();
		periodo = getPeriodoAtual();
		disciplina = new ComponenteCurricular();
	}


	/**
	 * Gera o relatório com as informações passadas no formulário
	 * @return
	 */
	public String gerarRelatorio() {
		ListaMensagens lista = new ListaMensagens();
		validar(lista);
		
		if (!lista.isEmpty()) {
			addMensagens(lista);
			return null;
		}
		
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class);
		matriculas = dao.findByMatriculadoEmDisciplina(disciplina.getId(), ano, periodo);

		if (matriculas == null || matriculas.isEmpty()) {
			addMensagemWarning("Nenhum registro encontrado.");
			return null;
		}
		
		tituloRelatorio = "Alunos matriculados em " + disciplina.getNome();
		clear();
		return forward("/graduacao/relatorios/discente/lista_alunos_por_disciplina.jsp");
	}

	/**
	 * Verifica se o usuário preencheu corretamente os dados
	 * @param lista
	 */
	private void validar(ListaMensagens lista) {
		ValidatorUtil.validateRequired(ano, "Ano", lista);
		ValidatorUtil.validateRequired(periodo, "Periodo", lista);
		ValidatorUtil.validateRequiredId(disciplina.getId(), "Disciplina", lista);
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

	public List<MatriculaComponente> getMatriculas() {
		return matriculas;
	}

	public void setMatriculas(List<MatriculaComponente> matriculas) {
		this.matriculas = matriculas;
	}

	public ComponenteCurricular getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(ComponenteCurricular disciplina) {
		this.disciplina = disciplina;
	}

	public String getTituloRelatorio() {
		return tituloRelatorio;
	}

	public void setTituloRelatorio(String tituloRelatorio) {
		this.tituloRelatorio = tituloRelatorio;
	}

}
