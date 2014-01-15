/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 09/09/2011
 *
 */
package br.ufrn.sigaa.ensino.medio.dominio;

import java.util.Date;

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
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.RegraNota;

/**
 * Entidade conter� as datas do per�odo letivo referentes a cada regra de nota (bimestre ou trimestre) do n�vel m�dio.
 * 
 * @author Arlindo Rodrigues
 */
@Entity
@Table(name = "calendario_regra", schema = "medio")
public class CalendarioRegra implements PersistDB {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_calendario_regra", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;	
	
	/** Calend�rio acad�mico vinculado */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_calendario")
	private CalendarioAcademico calendario;
	
	/** Regra que ser� definida o calend�rio */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_regra_nota")
	private RegraNota regra;
	
	/** Data de in�cio do per�odo letivo para regra */
	@Column(name = "data_inicio")
	private Date dataInicio;
	
	/** Data de fim do per�odo letivo para regra */
	@Column(name = "data_fim")
	private Date dataFim;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public CalendarioAcademico getCalendario() {
		return calendario;
	}

	public void setCalendario(CalendarioAcademico calendario) {
		this.calendario = calendario;
	}

	public RegraNota getRegra() {
		return regra;
	}

	public void setRegra(RegraNota regra) {
		this.regra = regra;
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
}
