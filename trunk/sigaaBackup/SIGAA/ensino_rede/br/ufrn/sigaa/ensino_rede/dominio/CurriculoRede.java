package br.ufrn.sigaa.ensino_rede.dominio;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

@Entity
@Table(name = "curriculo_rede", schema = "ensino_rede")
public class CurriculoRede implements PersistDB {
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="ensino_rede.hibernate_sequence") })
	@Column(name = "id_curriculo_rede")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "id_curso_associado")
	private CursoAssociado curso;
	
	@OneToMany(mappedBy = "curriculo")
	private List<CurriculoComponenteRede> componentes;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public CursoAssociado getCurso() {
		return curso;
	}

	public void setCurso(CursoAssociado curso) {
		this.curso = curso;
	}

	public List<CurriculoComponenteRede> getComponentes() {
		return componentes;
	}

	public void setComponentes(List<CurriculoComponenteRede> componentes) {
		this.componentes = componentes;
	}

	
}
