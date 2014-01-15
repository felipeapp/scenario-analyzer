package br.ufrn.sigaa.ead.dominio;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.sigaa.dominio.Curso;

/**
 * Cidade Polo ligado com um curso de ensino a distância Um polo possui um
 * coordenador.
 *
 * @author Gleydson
 *
 */
@Entity
@Table(name = "polo_curso", schema = "ead")
public class PoloCurso implements PersistDB {

	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	@Column(name = "id_polo_curso", nullable = false)
	private int id;

	/** Curso do polo */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_curso")
	/** Curso associado a um polo */
	private Curso curso;

	/** Polo do curso */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_polo")
	/** Pólo que contém um curso */
	private Polo polo;

	/** Informação do(s) Tutores/Orientadores */
	@OneToMany(mappedBy = "poloCurso")
	/** Tutores que trabalham nesse polo-curso */
	private Collection<TutorOrientador> tutoresOrientadores;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public Polo getPolo() {
		return polo;
	}

	public void setPolo(Polo polo) {
		this.polo = polo;
	}

	public Collection<TutorOrientador> getTutoresOrientadores() {
		return tutoresOrientadores;
	}

	public void setTutoresOrientadores(Collection<TutorOrientador> tutoresOrientadores) {
		this.tutoresOrientadores = tutoresOrientadores;
	}

	public String getDescricao() {
		return getCurso().getNome() + " - " + getPolo().getDescricao();
	}
	
	/** 
	 * Compara o ID deste PoloCurso com o passado por parâmetro.
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	public static boolean compateTo(Collection<PoloCurso> polos, Integer polo) {
		for (PoloCurso poloCurso : polos) {
			if ( poloCurso.getPolo().getId() == polo ) {
				return true;
			}
		}
		return false;
	}
	
}
