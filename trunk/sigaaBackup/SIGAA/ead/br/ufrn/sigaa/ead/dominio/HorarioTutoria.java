/**
 *
 */
package br.ufrn.sigaa.ead.dominio;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 *
 * Horário de disponibilidade do tutor para com seus alunos em um determinado
 * semestre
 *
 * @author Andre M Dantas
 *
 */
@Entity
@Table(name = "horario_tutoria", schema = "ead", uniqueConstraints = {})
public class HorarioTutoria implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_horario", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Ano em que esse horário é válido */
	private Integer ano;

	/** Período em que esse horário é válido */
	private Integer periodo;

	/** Dia da semana em que haverá reunião */
	private Integer diaSemana;

	/** Se a reunião poderá ser no turno matutino */
	private Boolean matutino;

	/** Se a reunião poderá ser no turno vespertino */
	private Boolean vespertino;

	/** Se a reunião poderá ser no turno noturno */
	private Boolean noturno;

	@ManyToOne()
	@JoinColumn(name = "id_tutoria")
	/** Tutoria que seguirá essas horas de reunião */
	private TutoriaAluno tutoria;

	/**
	 * Para ser utilizado na jsp pra formatação do formulário.
	 */
	@Transient
	private boolean selecionado = false;
	
	public HorarioTutoria() {
	}

	public HorarioTutoria(int id) {
		this.id = id;
	}

	public HorarioTutoria(TutoriaAluno tutoria, int diaSemana) {
		this.tutoria = tutoria;
		this.diaSemana = diaSemana;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	/**
	 * Retorna o dia da semana selecionado no formato stricto
	 * @return
	 */
	public String getDiaSemanaString(){
		switch (diaSemana) {
		case Calendar.MONDAY:
			return "Segunda-feira";
		case Calendar.TUESDAY:
			return "Terça-feira";
		case Calendar.WEDNESDAY:
			return "Quarta-feira";
		case Calendar.THURSDAY:
			return "Quinta-feira";
		case Calendar.FRIDAY:
			return "Sexta-feira";
		case Calendar.SATURDAY:
			return "Sábado";
		case Calendar.SUNDAY:
			return "Domingo";
		default:
			return "indefinido";
		}
		
	}
	
	public Integer getDiaSemana() {
		return diaSemana;
	}

	public void setDiaSemana(Integer diaSemana) {
		this.diaSemana = diaSemana;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Boolean isMatutino() {
		return matutino;
	}

	public void setMatutino(Boolean matutino) {
		this.matutino = matutino;
	}

	public Boolean isNoturno() {
		return noturno;
	}

	public void setNoturno(Boolean noturno) {
		this.noturno = noturno;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public TutoriaAluno getTutoria() {
		return tutoria;
	}

	public void setTutoria(TutoriaAluno tutoria) {
		this.tutoria = tutoria;
	}

	public Boolean isVespertino() {
		return vespertino;
	}

	public void setVespertino(Boolean vespertino) {
		this.vespertino = vespertino;
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();

		ValidatorUtil.validateRequired(diaSemana, "Dia de Semana", erros);
		if (!matutino && !vespertino && !noturno )
			erros.addErro("Ao menos um turno deve ser selecionado para cada dia de semana selecionado.");

		return erros;

	}

	public Boolean getMatutino() {
		return matutino;
	}

	public Boolean getNoturno() {
		return noturno;
	}

	public Boolean getVespertino() {
		return vespertino;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

}