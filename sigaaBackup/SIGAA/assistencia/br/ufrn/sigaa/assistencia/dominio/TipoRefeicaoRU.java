package br.ufrn.sigaa.assistencia.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrn.arq.dominio.PersistDB;

@Entity
@Table(name="tipo_refeicao_ru", schema="sae")
public class TipoRefeicaoRU implements PersistDB {

	public static final String SEGUNDA = "1"; 
	public static final String TERCA = "2"; 
	public static final String QUARTA = "3"; 
	public static final String QUINTA = "4"; 
	public static final String SEXTA = "5"; 
	public static final String SABADO = "6"; 
	public static final String DOMINGO = "7";
	
	/**
	 * Dias da Semana possuem um horário maior
	 */
	public static final int CAFE = 1;
	public static final int ALMOCO = 2; 
	public static final int JANTA = 3;
	
	/**
	 * Finais de Semana possuem um horário mais restrito
	 */
	public static final int CAFE_FIM_SEMANA = 4;
	public static final int ALMOCO_FIM_SEMANA = 5; 
	public static final int JANTA_FIM_SEMANA = 6;
	
	@Transient
	private boolean horarioCafeValido;
	@Transient
	private boolean horarioAlmocoValido;
	@Transient
	private boolean horarioJantaValido;
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name="id_tipo_refeicao_ru")
	private int id;
	
	private String descricao;
	
	/**
	 * Hora inicial/final de cada refeição
	 */
	@Column(name="hora_inicio")
	private int horaInicio;
	
	@Column(name="hora_fim")
	private int horaFim;
	
	/**
	 * Minuto inicial/final de cada refeição
	 */
	@Column(name="minuto_inicio")
	private int minutoInicio;
	
	@Column(name="minuto_fim")
	private int minutoFim;
	
	/**
	 * Indica se esse tipo de refeição é servido no fim de semana, 
	 * caso seja, pode possuir um horário diferenciado do mesmo tipo
	 * de refeição que é servido durante a semana.
	 */
	@Column(name="fim_semana")
	private boolean fimSemana;
	
	public boolean isCafe() {
		if (id == CAFE || id == CAFE_FIM_SEMANA)
			return true;
		else
			return false;
	}
	
	public boolean isAlmoco() {
		if (id == ALMOCO || id == ALMOCO_FIM_SEMANA)
			return true;
		else
			return false;
	}

	public boolean isJanta() {
		if (id == JANTA || id == JANTA_FIM_SEMANA)
			return true;
		else
			return false;
	}
	
	public void setTipoRefeicao(int idTipoRefeicao) {
		id = idTipoRefeicao;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public int getHoraInicio() {
		return horaInicio;
	}
	public void setHoraInicio(int horaInicio) {
		this.horaInicio = horaInicio;
	}
	public int getHoraFim() {
		return horaFim;
	}
	public void setHoraFim(int horaFim) {
		this.horaFim = horaFim;
	}
	public int getMinutoInicio() {
		return minutoInicio;
	}
	public void setMinutoInicio(int minutoInicio) {
		this.minutoInicio = minutoInicio;
	}
	public int getMinutoFim() {
		return minutoFim;
	}
	public void setMinutoFim(int minutoFim) {
		this.minutoFim = minutoFim;
	}
	public boolean isFimSemana() {
		return fimSemana;
	}
	public void setFimSemana(boolean fimSemana) {
		this.fimSemana = fimSemana;
	}

	public boolean isHorarioCafeValido() {
		return horarioCafeValido;
	}

	public void setHorarioCafeValido(boolean horarioCafeValido) {
		this.horarioCafeValido = horarioCafeValido;
	}

	public boolean isHorarioAlmocoValido() {
		return horarioAlmocoValido;
	}

	public void setHorarioAlmocoValido(boolean horarioAlmocoValido) {
		this.horarioAlmocoValido = horarioAlmocoValido;
	}

	public boolean isHorarioJantaValido() {
		return horarioJantaValido;
	}

	public void setHorarioJantaValido(boolean horarioJantaValido) {
		this.horarioJantaValido = horarioJantaValido;
	}
	
}