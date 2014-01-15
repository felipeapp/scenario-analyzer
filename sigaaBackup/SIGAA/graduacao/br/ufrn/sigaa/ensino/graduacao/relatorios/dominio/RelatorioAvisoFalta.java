/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.dominio;

import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.dominio.CalendarioAcademico;

/**
 * Objeto que encapsula o resultado do relatório
 * 
 * @author Henrique André
 *
 */
public class RelatorioAvisoFalta {
	/**
	 * Quantidade de faltas de um professor
	 */
	private Long qtdFalta;
	
	/**
	 * Nome do professor
	 */
	private String docenteNome;
	
	/**
	 * id do servidor
	 */
	private Integer docenteId;
	
	/**
	 * Nome da turma
	 */
	private String turmaNome;
	
	/**
	 * Código da turma 
	 */
	private String codigo;
	
	/**
	 * Id da turma
	 */
	private Integer turmaId;
	
	/**
	 * Numero do mês
	 */
	private Integer mes;
	
	/**
	 * Data da falta
	 */
	private Date dataAula;
	
	/**
	 * Observações dos discentes
	 */
	private Collection<String> observacoes;
	
	/**
	 * Sse é docente externo
	 */
	private boolean docenteExterno;
	
	/**
	 * Calendário acadêmico
	 */
	private CalendarioAcademico calAcademico;
	
	private Long possuiFaltaHomologada;
	
	/**
	 * Indica se a falta pertence ao período e ano atual
	 * @return
	 */
	public boolean isPeriodoAtual() {
		return CalendarUtils.isDentroPeriodo(calAcademico.getInicioPeriodoLetivo(), calAcademico.getFimPeriodoLetivo(), dataAula);
	}
	
	public Long getQtdFalta() {
		return qtdFalta;
	}

	public void setQtdFalta(Long qtdFalta) {
		this.qtdFalta = qtdFalta;
	}

	public String getDocenteNome() {
		return docenteNome;
	}

	public void setDocenteNome(String docenteNome) {
		this.docenteNome = docenteNome;
	}

	public String getTurmaNome() {
		return turmaNome;
	}

	public void setTurmaNome(String turmaNome) {
		this.turmaNome = turmaNome;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Date getDataAula() {
		return dataAula;
	}

	public void setDataAula(Date dataAula) {
		this.dataAula = dataAula;
	}

	public Collection<String> getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(Collection<String> observacoes) {
		this.observacoes = observacoes;
	}

	public Integer getDocenteId() {
		return docenteId;
	}

	public void setDocenteId(Integer docenteId) {
		this.docenteId = docenteId;
	}

	public Integer getTurmaId() {
		return turmaId;
	}

	public void setTurmaId(Integer turmaId) {
		this.turmaId = turmaId;
	}

	public boolean isDocenteExterno() {
		return docenteExterno;
	}

	public void setDocenteExterno(boolean docenteExterno) {
		this.docenteExterno = docenteExterno;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public CalendarioAcademico getCalAcademico() {
		return calAcademico;
	}

	public void setCalAcademico(CalendarioAcademico calAcademico) {
		this.calAcademico = calAcademico;
	}

	public Long getPossuiFaltaHomologada() {
		return possuiFaltaHomologada;
	}

	public void setPossuiFaltaHomologada(Long possuiFaltaHomologada) {
		this.possuiFaltaHomologada = possuiFaltaHomologada;
	}

}