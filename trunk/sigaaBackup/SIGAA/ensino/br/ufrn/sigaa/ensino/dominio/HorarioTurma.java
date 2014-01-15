/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '22/09/2006'
 *
 */
package br.ufrn.sigaa.ensino.dominio;

// Generated 13/09/2006 08:49:22 by Hibernate Tools 3.1.0.beta5

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Definição da estrutura de horários de uma turma
 *
 */
@Entity
@Table(name = "horario_turma", schema = "ensino", uniqueConstraints = {})
public class HorarioTurma implements PersistDB, Comparable<HorarioTurma> {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="ensino.horario_turma_seq") }) 	
	@Column(name = "id_horario_turma", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/**
	 * Dia da semana: Ex, 2 (segunda),3 (terca),4 (quarta)
	 */
	private char dia;

	/**
	 * Hora que aula inicia
	 */
	@Column(name = "hora_inicio")
	private Date horaInicio;

	/**
	 * Hora que aula termina
	 */
	@Column(name = "hora_fim")
	private Date horaFim;

	/**
	 * Data de quando comeca o horário
	 */
	@Column(name = "data_inicio")
	@Temporal(TemporalType.DATE)
	private Date dataInicio;

	/**
	 * Data de quando termina o horário
	 */
	@Column(name = "data_fim")
	@Temporal(TemporalType.DATE)
	private Date dataFim;

	/**
	 * Se as aulas serão semanais, quinzenal, mensal ou outro tipo
	 */
	private Integer tipo = TipoPeriodicidadeHorario.SEMANAL;

	/**
	 * Turma que possui o horário
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_turma", nullable=true, updatable=true)
	private Turma turma;

	/**
	 * Horario da turma, se é no 1º horario, 2º horario.. etc
	 */
	@ManyToOne()
	@JoinColumn(name = "id_horario")
	private Horario horario;

	/** se o horário chocou com outro */
	@Transient
	private boolean conflitoHorario = false;
	
	/** Indica se o horário foi selecionado. */
	@Transient
	private boolean selecionado = false;

	public Integer getTipo() {
		return tipo;
	}

	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_turma")
	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public HorarioTurma(Horario h, Turma t, Date periodoInicial, Date periodoFinal) {
		turma = t;
		dataInicio = periodoInicial;
		dataFim = periodoFinal;
		horario = h;
		horaInicio = h.getInicio();
		horaFim = h.getFim();
	}	
	
	public HorarioTurma(Horario h, Turma t) {
		turma = t;
		dataInicio = t.getDataInicio();
		dataFim = t.getDataFim();
		horario = h;
		horaInicio = h.getInicio();
		horaFim = h.getFim();
	}

	public HorarioTurma(Horario h, char dia) {
		horario = h;
		horaInicio = h.getInicio();
		horaFim = h.getFim();
		this.dia = dia;
	}

	public HorarioTurma(Horario h, char dia, Date periodoInicial, Date periodoFinal) {
		horario = h;
		horaInicio = h.getInicio();
		horaFim = h.getFim();
		this.dia = dia;
		dataInicio = periodoInicial;
		dataFim = periodoFinal;
	}
	
	/** default constructor */
	public HorarioTurma() {
	}

	/** minimal constructor */
	public HorarioTurma(int idHorario) {
		this.id = idHorario;
	}

	// Property accessors

	public int getId() {
		return this.id;
	}

	public void setId(int idHorario) {
		this.id = idHorario;
	}

	@Transient
	public String getDias() {

		return "";

	}

	@Transient
	public String getDiasResumidos() {

		return "";

	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio =  dataInicio;
	}

	public char getDia() {
		return dia;
	}

	public void setDia(char dia) {
		this.dia = dia;
	}

	public Date getHoraFim() {
		return horaFim;
	}

	public void setHoraFim(Date horaFim) {
		this.horaFim = horaFim;
	}

	public Date getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(Date horaInicio) {
		this.horaInicio = horaInicio;
	}

	public Horario getHorario() {
		return horario;
	}

	public void setHorario(Horario horario) {
		this.horario = horario;
	}

	/**
	 * Retorna uma descrição textual do horário da turma no seguinte formato:
	 * <br />
	 * <i>De dd/MM/yyyy até dd/MM/yy, nos horários de HH:mm às HH:mm</i>
	 * 
	 * @return
	 */
	@Transient
	public String getDescricao() {
		Formatador fmt = Formatador.getInstance();
		String desc = "De " + fmt.formatarData(getDataInicio()) + " até " + fmt.formatarData(getDataFim())
				+ ", nos horários de ";
		desc += fmt.formatarHora(getHoraInicio()) + " às " + fmt.formatarHora(getHoraFim());
		return desc;
	}

	/**
	 * Retorna uma string indicando o dia e o horário da turma seguindo-se o seguinte formato:
	 * <br />
	 * <i>{{@link #dia}} HH:mm às HH:mm</i>
	 * 
	 * 
	 * @return
	 */
	@Transient
	public String getDescricaoHorario() {
		Formatador fmt = Formatador.getInstance();
		return dia + " " + fmt.formatarHora(getHoraInicio()) + " às " + fmt.formatarHora(getHoraFim());
	}

	/**
	 * Retorna um inteiro representando o período de repetição do horário.
	 * Sendo 1 para horários semanais, 2 para quinzenais e 4 para mensais.
	 * 
	 * @return
	 */
	@Transient
	public int getPeriodoRepeticao() {
		if (tipo == TipoPeriodicidadeHorario.SEMANAL)
			return 1;
		else if (tipo == TipoPeriodicidadeHorario.QUINZENAL)
			return 2;
		else if (tipo == TipoPeriodicidadeHorario.MENSAL)
			return 4;
		else
			return 1;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "dia", "horario", "dataInicio", "dataFim");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(dia, horario);
	}

	public boolean isConflitoHorario() {
		return conflitoHorario;
	}

	public void setConflitoHorario(boolean chocado) {
		this.conflitoHorario = chocado;
	}

	/**
	 * Verifica se o horárioTurma passado como argumento entra em conflito de data
	 * 
	 * @param ht
	 * @return
	 */
	public boolean checarConflitoPerido(HorarioTurma ht) {
		return CalendarUtils.isIntervalosDeDatasConflitantes(dataInicio, dataFim, ht.getDataInicio(), ht.getDataFim());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		Formatador fmt = Formatador.getInstance();
		return fmt.formatarData(dataInicio) + " - " + fmt.formatarData(dataFim) + ": " + getDescricaoHorario();
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	@Override
	public int compareTo(HorarioTurma other) {
		// compara...
		// data de início
		long dia = 0;
		if (this.getDataInicio() != null && other.getDataInicio() != null)
			dia = this.getDataInicio().getTime() - other.getDataInicio().getTime();
		// dia da semana
		if (dia == 0)
			dia = this.getDia() - other.getDia();
		// hora inicial
		if (dia == 0 && this.getHorario() != null && other.getHorario() != null) {
			dia = this.getHorario().getInicio().getTime() - other.getHorario().getInicio().getTime();
		}
		if (dia == 0)
			return 0;
		else 
			return dia > 1 ? 1 : -1;
	}
}
