package br.ufrn.sigaa.mobile.jsf;

import java.util.ArrayList;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.pessoa.dominio.Discente;

public class ConsultaNotasMobile {
	
	private int anoAtual;
	private int periodoAtual;
	private DiscenteAdapter discente;
	private Double mediaGeral;
	private ArrayList<MatriculaComponente> disciplinasAnteriores = new ArrayList<MatriculaComponente>();
	
	private int anoSemestreAnterior;
	private int periodoSemestreAnterior;

	public ConsultaNotasMobile(Discente discente) {
		this.discente = discente;	
	}
	
	public ConsultaNotasMobile() {
	}

	public int getAnoSemestreAnterior() {
		return anoSemestreAnterior;
	}
	public void setAnoSemestreAnterior(int anoSemestreAnterior) {
		this.anoSemestreAnterior = anoSemestreAnterior;
	}
	public int getPeriodoSemestreAnterior() {
		return periodoSemestreAnterior;
	}
	public void setPeriodoSemestreAnterior(int periodoSemestreAnterior) {
		this.periodoSemestreAnterior = periodoSemestreAnterior;
	}
	
	public void adicionarDisciplinasAnteriores(MatriculaComponente matriculaComponente) {
		disciplinasAnteriores.add(matriculaComponente);
	}

	public void definirAnoPeriodoAtual(DiscenteAdapter discente) throws DAOException {
		this.discente = discente;
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(discente);
		anoAtual = cal.getAno();
		periodoAtual = cal.getPeriodo();
	}
	
	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	public int getPeriodoAtual() {
		return periodoAtual;
	}
	
	public void setPeriodoAtual(int periodoAtual) {
		this.periodoAtual = periodoAtual;
	}

	public int getAnoAtual() {
		return anoAtual;
	}

	public void setAnoAtual(int anoAtual) {
		this.anoAtual = anoAtual;
	}

	public ArrayList<MatriculaComponente> getDisciplinasAnteriores() {
		return disciplinasAnteriores;
	}

	public void setDisciplinasAnteriores(
			ArrayList<MatriculaComponente> disciplinasAnteriores) {
		this.disciplinasAnteriores = disciplinasAnteriores;
	}

	public Double getMediaGeral() {
		return mediaGeral;
	}

	public void setMediaGeral(Double mediaGeral) {
		this.mediaGeral = mediaGeral;
	}

}
