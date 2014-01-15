package br.ufrn.sigaa.ensino_rede.dominio;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

@Entity
@Table(schema="ensino_rede", name = "equipe_docente_rede")
public class EquipeDocenteRede implements PersistDB {
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="ensino_rede.hibernate_sequence") })
	@Column(name = "id_equipe_docente_rede", nullable = false)
	private int id;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name="docentes_equipe_rede", schema="ensino_rede",
			joinColumns=@JoinColumn(name="id_equipe_docente_rede"),  
			inverseJoinColumns=@JoinColumn(name="id_docente_rede"))
	private Set<DocenteRede> docentes;
	
	@ManyToOne
	@JoinColumn(name = "id_dados_curso_rede")
	private DadosCursoRede dadosCurso;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Set<DocenteRede> getDocentes() {
		return docentes;
	}

	public void setDocentes(Set<DocenteRede> docentes) {
		this.docentes = docentes;
	}

	public DadosCursoRede getDadosCurso() {
		return dadosCurso;
	}

	public void setDadosCurso(DadosCursoRede dadosCurso) {
		this.dadosCurso = dadosCurso;
	}

}
