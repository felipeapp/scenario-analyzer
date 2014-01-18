package br.ufrn.sigaa.ensino.metropoledigital.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * 
 * @author Rafael A Silva
 *
 */

@Entity
@Table(name="carga_horaria_semanal_disciplina", schema="metropole_digital")
public class CargaHorariaSemanalDisciplina implements PersistDB, Validatable, Comparable<CargaHorariaSemanalDisciplina> {
	/**ID da Classe*/
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="metropole_digital.carga_horaria_semanal_discipl_id_carga_horaria_semanal_disc_seq") })
	@Column(name="id_carga_horaria_semanal_disciplina", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/**Carga Horária Semanal da disciplina*/
	@Column(name="carga_horaria")
	private Integer cargaHoraria;
	
	/**Componente Curricular vinculado a Carga Horária Semanal*/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_disciplina")
	private ComponenteCurricular disciplina;
	
	/**Perído de avaliação correspondente a carga horária*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_periodo_avaliacao")
	private PeriodoAvaliacao periodoAvaliacao;
	
	/**Carga Horária total da semana*/
	@Transient
	private Integer chSemana;
	
	/**Carga horária total da disciplina*/
	@Transient
	private Integer chDisciplina;
	
	
	//GETTERS AND SETTERS
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Integer getCargaHoraria() {
		return cargaHoraria;
	}
	public void setCargaHoraria(Integer cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}
	public ComponenteCurricular getDisciplina() {
		return disciplina;
	}
	public void setDisciplina(ComponenteCurricular disciplina) {
		this.disciplina = disciplina;
	}
	public PeriodoAvaliacao getPeriodoAvaliacao() {
		return periodoAvaliacao;
	}
	public void setPeriodoAvaliacao(PeriodoAvaliacao periodoAvaliacao) {
		this.periodoAvaliacao = periodoAvaliacao;
	}
	public Integer getChSemana() {
		return chSemana;
	}
	public void setChSemana(Integer chSemana) {
		this.chSemana = chSemana;
	}
	public Integer getChDisciplina() {
		return chDisciplina;
	}
	public void setChDisciplina(Integer chDisciplina) {
		this.chDisciplina = chDisciplina;
	}
	
	@Override
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int compareTo(CargaHorariaSemanalDisciplina o) {		
		return this.getId()-o.getId();
	}

}
