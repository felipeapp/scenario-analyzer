/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 23/07/2009
 *
 */
package br.ufrn.sigaa.espacofisico.dominio;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade que modela uma repeti��o de hor�rio de uma Reserva. Uma repeti��o
 * pode ser di�ria (todos dias), semanal, quinzenal ou mensal. N�o ser� definido
 * a repeti��o anual uma vez que esta repeti��o se destina a reservas de espa�os
 * f�sicos para um semestre (geralmente).
 * 
 * @author �dipo Elder F. Melo
 * 
 */
@Entity
@Table(name = "repeticao_horario", schema = "espaco_fisico")
public class RepeticaoHorario implements PersistDB, TipoRepeticaoHorario {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_repeticao_horario")
	private int id;
	
	@Column(name = "tipo_repeticao")
	private char tipoRepeticao;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio")
	private Date dataInicio;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim")
	private Date dataFim;

	/** Indica a frequ�ncia da repeti��o. Por exemplo: a cada 5 dias, a cada duas semana, a cada m�s, etc. */
	private int frequencia;

	/** Dias da semana a repetir (usado somente no caso de repeti��o semanal). Por exemplo: repetir as segundas, ter�as e sextas. */
	@Column(name = "dias_semana")
	private String diasSemana;
	
	/** Indica se repete no dia da semana ao inv�s da data (usado somente no caso de repeti��o mensal). Por exemplo: repete na �ltima sexta-feira de cada m�s. */
	@Column(name = "repete_dia_semana")
	private boolean repeteDiaSemana;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_reserva_horario")
	private RepeticaoHorario repeticaoHorario;
	
	public RepeticaoHorario() {
		
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public boolean isDiario() {
		return this.tipoRepeticao == TipoRepeticaoHorario.DIARIO;
	}
	
	public boolean isSemanal() {
		return this.tipoRepeticao == TipoRepeticaoHorario.SEMANAL;
	}
	
	public boolean isQuinzenal(){
		return this.tipoRepeticao == TipoRepeticaoHorario.QUINZENAL;
	}
	
	public boolean isMensal(){
		return this.tipoRepeticao == TipoRepeticaoHorario.MENSAL;
	}
	
	/** Indica se a reserva se repete no dia da semana especificado.
	 * @param diaSemana
	 * @return
	 */
	public boolean isRepetidoDiaSemana(char diaSemana){
		if (diaSemana < 1 || diaSemana > 7)
			return false;
		return this.diasSemana.indexOf(diaSemana) > 0;
	}	

	public char getTipoRepeticao() {
		return tipoRepeticao;
	}

	public void setTipoRepeticao(char tipoRepeticao) {
		this.tipoRepeticao = tipoRepeticao;
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

	public int getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(int frequencia) {
		this.frequencia = frequencia;
	}

	public String getDiasSemana() {
		return diasSemana;
	}

	public void setDiasSemana(String diasSemana) {
		this.diasSemana = diasSemana;
	}

	public boolean isRepeteDiaSemana() {
		return repeteDiaSemana;
	}

	public void setRepeteDiaSemana(boolean repeteDiaSemana) {
		this.repeteDiaSemana = repeteDiaSemana;
	}

	public RepeticaoHorario getRepeticaoHorario() {
		return repeticaoHorario;
	}

	public void setRepeticaoHorario(RepeticaoHorario repeticaoHorario) {
		this.repeticaoHorario = repeticaoHorario;
	}
	
}
