/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 28/11/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

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

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.GrauAcademico;

/** O calendário ENADE é definido para um Curso e Grau Acadêmico (Licenciatura, Bacharelado, Formação, etc.).
 * Esta classe tem como objetivo permitir a associação de um calendário ENADE à um curso/grau acadêmico.
 * @author Édipo Elder F. de Melo
 *
 */
@Entity
@Table(schema = "graduacao", name = "curso_grau_academico_enade")
public class CursoGrauAcademicoEnade implements Validatable, Comparable<CursoGrauAcademicoEnade> {
	
	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_curso_grau_academico_enade", nullable = false)
	private int id;
	
	/** Curso associado ao Calendário ENADE. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_curso",nullable=false)
	private Curso curso;
	
	/** Grau Acadêmico do curso associado ao Calendário ENADE. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_grau_academico",nullable=false)
	private GrauAcademico grauAcademico;
	
	/** Calendário ENADE ao qual este curs/grau acadêmico pertence. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_calendario_enade",nullable=false)
	private CalendarioEnade calendarioEnade;

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		validateRequired(curso, "Curso", lista);
		validateRequired(grauAcademico, "Grau Acadêmico", lista);
		validateRequired(calendarioEnade, "Calendário Enade", lista);
		return lista;
	}

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

	public GrauAcademico getGrauAcademico() {
		return grauAcademico;
	}

	public void setGrauAcademico(GrauAcademico grauAcademico) {
		this.grauAcademico = grauAcademico;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		if (curso != null)
			result.append(curso.getNome());
		if (grauAcademico != null) {
			result.append(" - ");
			result.append(grauAcademico.getDescricao());
		}
		return result.toString();
	}

	public String getDescricao() {
		return toString();
	}
	
	@Override
	public int compareTo(CursoGrauAcademicoEnade other) {
		int comp = 0;
		if (other != null) {
			comp = this.getCurso().getNome().compareTo(other.getCurso().getNome());
			if (comp == 0)
				comp = this.getGrauAcademico().getDescricao().compareTo(other.getGrauAcademico().getDescricao());
		}
		return comp;
	}
	
	@Override
	public boolean equals(Object other) {
		return EqualsUtil.testEquals(this, other, "curso", "grauAcademico");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, curso, grauAcademico);
	}

	public CalendarioEnade getCalendarioEnade() {
		return calendarioEnade;
	}

	public void setCalendarioEnade(CalendarioEnade calendarioEnade) {
		this.calendarioEnade = calendarioEnade;
	}
}
