package br.ufrn.sigaa.ensino.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.ensino.util.HorarioTurmaUtil;

/** Classe que define os horários do docente em uma turma.
 * @author Édipo Elder F. Melo
 *
 */
@Entity
@Table(name = "horario_docente", schema = "ensino")
public class HorarioDocente implements PersistDB, Comparable<HorarioDocente> {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="ensino.hibernate_sequence") }) 	
	@Column(name = "id_horario_docente", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/**
	 * Dia da semana: Ex, 2 (segunda),3 (terca),4 (quarta), etc.
	 */
	private char dia;

	/**
	 * Data de quando começa o horário.
	 */
	@Column(name = "data_inicio")
	private Date dataInicio;

	/**
	 * Data de quando termina o horário.
	 */
	@Column(name = "data_fim")
	private Date dataFim;

	/**
	 * Se as aulas serão semanais, quinzenal, mensal ou outro tipo.
	 */
	private Integer tipo = TipoPeriodicidadeHorario.SEMANAL;

	/**
	 * Horário da turma, se é no 1º horario, 2º horario, etc.
	 */
	@ManyToOne()
	@JoinColumn(name = "id_horario")
	private Horario horario;

	/** Indica se o horário chocou com outro */
	@Transient
	private boolean conflitoHorario = false;

	/** Construtor padrão. */
	public HorarioDocente() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public char getDia() {
		return dia;
	}

	public void setDia(char dia) {
		this.dia = dia;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Integer getTipo() {
		return tipo;
	}

	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}

	public Horario getHorario() {
		return horario;
	}

	public void setHorario(Horario horario) {
		this.horario = horario;
	}

	public boolean isConflitoHorario() {
		return conflitoHorario;
	}

	public void setConflitoHorario(boolean conflitoHorario) {
		this.conflitoHorario = conflitoHorario;
	}

	/**
	 * Retorna a carga horária, em minutos, do respectivo horário. 
	 * @return
	 */
	public int getCargaHorariaEmMinutos() {
		int total = 0;
		int dias = HorarioTurmaUtil.getTotalDias(new Integer(getDia()+""), dataInicio, dataFim);
		long minutosPorDia = horario.getFim().getTime() - horario.getInicio().getTime();
		minutosPorDia = ((minutosPorDia / 1000) / 60) ;
		total = (int) (total + ( minutosPorDia * dias));
		return total;
	}

	@Override
	public boolean equals(Object obj) { 
 		return EqualsUtil.testEquals(this, obj,"id", "dataFim","dataInicio", "dia","horario","tipo");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, dataFim, dataInicio, dia, horario, tipo);
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(dia);
		str.append(horario.getTurnoChar());
		str.append(horario.getOrdem());
		return str.toString();
	}
	
	/**
	 * Retorna a hora final referente ao horário ou <code>null</code> caso o horário não esteja definido.
	 * 
	 * @return
	 */
	@Transient
	public Date getHoraFim() {
		if (horario != null)
			return horario.getFim();
		else
			return null;
	}
	
	/**
	 * Retorna a hora inicial referente ao horário ou <code>null</code> caso o horário não esteja definido.
	 * 
	 * @return
	 */
	@Transient
	public Date getHoraInicio() {
		if (horario != null)
			return horario.getInicio();
		else
			return null;
	}
	
	@Override
	public int compareTo(HorarioDocente other) {
		// compara...
		// data de início
		long dia = this.getDataInicio().getTime() - other.getDataInicio().getTime();
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
