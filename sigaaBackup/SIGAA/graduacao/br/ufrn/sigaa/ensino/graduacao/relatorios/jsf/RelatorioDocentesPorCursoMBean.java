package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import static br.ufrn.arq.util.ValidatorUtil.validaInt;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.ensino.DocenteTurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;

/**
 * Relatório com listagem dos professores que estão vinculados com um determinado curso, em turmas 
 * de um determinado ano.
 * 
 * @author wendell
 *
 */
@Component("relatorioDocentesPorCursoBean") @Scope("request")
@SuppressWarnings("unchecked")
public class RelatorioDocentesPorCursoMBean extends SigaaAbstractController {

	private Curso curso;
	private Integer ano;
	
	public RelatorioDocentesPorCursoMBean() {
		clear();
	}
	
	/**
	 * Prepara formulário do relatório
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String iniciar() throws DAOException {
		clear();
		return forward("/graduacao/relatorios/docente/form_docentes_curso.jsf");
	}

	private void clear() {
		curso = new Curso();
		ano = getAnoAtual();
	}
	
	/**
	 * Gera o relatório de acordo com os parâmetros definidos
	 * pelo usuário
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String gerar() throws DAOException {
		if (!validate()) {
			return null;
		}
		
		DocenteTurmaDao dao = getDAO(DocenteTurmaDao.class);
		List<Map<String, Object>> resultado = dao.findRelatorioDocentesByCurso(curso, ano);
		
		if (resultado.isEmpty()) {
			addMensagem( MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		} else {
			curso = dao.refresh(curso);
			getCurrentRequest().setAttribute("resultado", resultado);
			
			return forward("/graduacao/relatorios/docente/relatorio_docentes_curso.jsf");
		}
	}

	/**
	 * Valida campos do formulário
	 * 
	 * @return
	 */
	private boolean validate() {
		ListaMensagens erros = new ListaMensagens();
		validateRequired(curso, "Curso", erros);
		validaInt(ano, "Ano", erros);
		addMensagens(erros);
		return erros.isEmpty();
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}
	
}

