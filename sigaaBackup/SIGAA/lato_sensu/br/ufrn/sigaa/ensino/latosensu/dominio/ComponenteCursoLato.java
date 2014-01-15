/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '26/01/2007'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.dominio;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;

/**
 * Classe que representa o relacionamento entre ComponenteCurricular e CursoLato
 *
 * @author Leonardo
 *
 */
@Entity
@Table(name = "componente_curso_lato", schema = "lato_sensu", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"id_componente_curricular", "id_curso_lato" }) })
public class ComponenteCursoLato implements PersistDB {

	// Fields
	private int id;

	private ComponenteCurricular disciplina;

	private CursoLato cursoLato;

	private List<String> docentesNome = new ArrayList<String>();
	
	// Constructors
	public ComponenteCursoLato(){
	}

	public ComponenteCursoLato(int id, CursoLato curso){
		this.id = id;
		this.cursoLato = curso;
	}

	public ComponenteCursoLato(int id){
		this.id = id;
	}

	public ComponenteCursoLato(int id, ComponenteCurricular componente, CursoLato curso){
		this.id = id;
		this.disciplina = componente;
		this.cursoLato = curso;
	}

	// Property accessors
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_componente_curso_lato", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_curso_lato", unique = false, nullable = true, insertable = true, updatable = true)
	public CursoLato getCursoLato() {
		return cursoLato;
	}

	public void setCursoLato(CursoLato cursoLato) {
		this.cursoLato = cursoLato;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_componente_curricular", unique = false, nullable = true, insertable = true, updatable = true)
	public ComponenteCurricular getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(ComponenteCurricular disciplina) {
		this.disciplina = disciplina;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	public void addDocentesNome(String nome) {
		this.docentesNome.add(nome);
	}

	@Transient
	public List<String> getDocentesNome() {
		return docentesNome;
	}

}
