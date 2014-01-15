/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/12/2008
 *
 */
package br.ufrn.sigaa.projetos.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Classe responsável pelo intervalos de datas de uma atividade estabelecida em um cronograma.
 * @author ricardo
 *
 */
@Entity
@Table(name = "intervalo_cronograma", schema = "projetos")
public class IntervaloCronograma implements PersistDB{

	private int id;
	
	/** Mês inicial do intervalo */
	private int mesInicio;
	/** Ano inicial do intervalo */
	private int anoInicio;
	/** Mês final do intervalo */
	private int mesFim;
	/** Ano final do intervalo */
	private int anoFim;
	
	private CronogramaProjeto cronograma;
	
	public IntervaloCronograma() {
		
	}

	@Column(name = "ano_fim", unique = false, nullable = true, insertable = true, updatable = true)
	public int getAnoFim() {
		return anoFim;
	}

	@Column(name = "ano_inicio", unique = false, nullable = true, insertable = true, updatable = true)
	public int getAnoInicio() {
		return anoInicio;
	}
	
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cronograma_projeto", unique = false, nullable = false, insertable = true, updatable = true)
	public CronogramaProjeto getCronograma() {
		return cronograma;
	}

	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name = "id_intervalo_cronograma")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })	
	public int getId() {
		return id;
	}

	@Column(name = "mes_fim", unique = false, nullable = true, insertable = true, updatable = true)
	public int getMesFim() {
		return mesFim;
	}

	@Column(name = "mes_inicio", unique = false, nullable = true, insertable = true, updatable = true)
	public int getMesInicio() {
		return mesInicio;
	}

	public void setAnoFim(int anoFim) {
		this.anoFim = anoFim;
	}

	public void setAnoInicio(int anoInicio) {
		this.anoInicio = anoInicio;
	}

	public void setCronograma(CronogramaProjeto cronograma) {
		this.cronograma = cronograma;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setMesFim(int mesFim) {
		this.mesFim = mesFim;
	}

	public void setMesInicio(int mesInicio) {
		this.mesInicio = mesInicio;
	}

		
}
