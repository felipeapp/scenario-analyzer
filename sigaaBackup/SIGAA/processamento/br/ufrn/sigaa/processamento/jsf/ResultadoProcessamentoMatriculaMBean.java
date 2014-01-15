/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 01/08/2008 
 *
 */
package br.ufrn.sigaa.processamento.jsf;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.graduacao.ProcessamentoMatriculaDAO;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.processamento.batch.ListaTurmasProcessadas;
import br.ufrn.sigaa.processamento.batch.ResultadoThread;
import br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaGraduacaoDao;
import br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaGraduacaoFeriasDao;
import br.ufrn.sigaa.processamento.dominio.MatriculaEmProcessamento;
import br.ufrn.sigaa.processamento.dominio.TurmaProcessada;

/**
 * Managed bean para realizar a geração dos relatórios
 * do resultado de processamento da matrícula.
 * @author David Pereira
 *
 */
@Component("resultadoProcessamentoMatriculaBean") @Scope("request")
public class ResultadoProcessamentoMatriculaMBean extends SigaaAbstractController<Object> {

	private int ano;
	
	private int periodo;
	
	private boolean rematricula;
	
	private boolean resultadoTurmasRegulares = true;
	
	/**
	 * Direciona o usuário para o formulário de início da geração do
	 * resultado do processamento de matrículas.
	 * JSP: /administracao/menu_administracao.jsp
	 * @return
	 * @throws Exception
	 */
	public String iniciar() throws Exception {
		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA);
		
		CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
		ano = calendario.getAno();
		periodo = calendario.getPeriodo();
		
		return forward("/processamento/processamento/resultado.jsf");
	}
	
	/**
	 * Gera os relatórios de processamento de matrícula para
	 * cada turma de graduação presencial.
	 * 
	 * JSP: /processamento/processamento/resultado.jsp
	 * @return
	 * @throws Exception
	 */
	public String gerar() {
		
		ProcessamentoMatriculaDAO dao = getDAO(ProcessamentoMatriculaDAO.class);
		
		List<Turma> turmas = null;
		
		if (resultadoTurmasRegulares) {
			turmas = getDAO(ProcessamentoMatriculaGraduacaoDao.class).findTurmasProcessadas(ano, periodo, rematricula);
		} else {
			turmas = getDAO(ProcessamentoMatriculaGraduacaoFeriasDao.class).findTurmasProcessadas(ano, periodo, rematricula);
		}
		
		int j = 0;
		for (Turma t : turmas) {
			Map<MatrizCurricular, ReservaCurso> reservas = dao.findInformacoesVagasTurma(t.getId(), rematricula, true);
			List<MatriculaEmProcessamento> matriculas = dao.findResultadoProcessamento(t.getId(), rematricula);
			List<MatriculaEmProcessamento> desistencias = dao.findDesistenciasTurma(t.getId());
			
			if (!resultadoTurmasRegulares) {
				t.setTipo(Turma.FERIAS);
			}
			
			ListaTurmasProcessadas.addTurma(new TurmaProcessada(t, matriculas, reservas, desistencias));
			System.out.println(++j);
		}
		
		//for (int i = 0; i < 1; i++) {
		String servletContextPath = getCurrentSession().getServletContext().getRealPath("");
		ResultadoThread t = new ResultadoThread(rematricula, servletContextPath);
		t.start();
		//}
		
		addMensagemInformation("Os resultados do processamento estão sendo gerados em segundo plano, dentro de 1 minuto estará disponível para acesso.");
		return null;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public boolean isRematricula() {
		return rematricula;
	}

	public void setRematricula(boolean rematricula) {
		this.rematricula = rematricula;
	}

	public boolean isTipo() {
		return resultadoTurmasRegulares;
	}

	public void setTipo(boolean tipo) {
		this.resultadoTurmasRegulares = tipo;
	}
	
}
