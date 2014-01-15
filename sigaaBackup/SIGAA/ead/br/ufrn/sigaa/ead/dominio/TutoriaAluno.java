package br.ufrn.sigaa.ead.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;

/**
 * relação entre o tutor orientador e o aluno
 *
 * @author Gleydson
 *
 */
@Entity
@Table(name = "tutoria_aluno", schema = "ead")
public class TutoriaAluno implements Validatable{

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_tutoria_aluno", nullable = false)
	private int id;

	@ManyToOne
	@JoinColumn(name = "id_discente")
	/** Discente da tutoria */
	private DiscenteGraduacao aluno;

	@ManyToOne
	@JoinColumn(name = "id_tutor")
	/** Tutor da tutoria */
	private TutorOrientador tutor;

	@Temporal(TemporalType.DATE)
	/** Início da atividade de tutoria */
	private Date inicio;

	@Temporal(TemporalType.DATE)
	/** Fim da atividade de tutoria */
	private Date fim;

	/** Se a tutoria está ativa, ou seja, se o tutor continua orientando o aluno */
	private boolean ativo;

	public TutoriaAluno() {
	}
	public TutoriaAluno(int tutoria) {
		id = tutoria;
	}

	public DiscenteGraduacao getAluno() {
		return aluno;
	}

	public void setAluno(DiscenteGraduacao aluno) {
		this.aluno = aluno;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public TutorOrientador getTutor() {
		return tutor;
	}

	public void setTutor(TutorOrientador tutor) {
		this.tutor = tutor;
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(aluno, "Aluno", erros);
		ValidatorUtil.validateRequired(tutor, "Tutor", erros);
		ValidatorUtil.validateRequired(inicio, "Data Inicial", erros);
		if (fim != null)
			ValidatorUtil.validateMinValue(fim, inicio, "Data Final", erros);
		return erros;
	}
}
