package br.ufrn.sigaa.ensino.graduacao.dominio;

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
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;

/**
 * Entidade que registra as matrículas extraordinárias realizadas com sua respectiva ordem.
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name = "matricula_componente_extraordinaria", schema = "graduacao", uniqueConstraints = {})
public class MatriculaComponenteExtraordinaria implements Validatable {
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy = "br.ufrn.arq.dao.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "hibernate_sequence") })
	@Column(name = "id_matricula_extraordinaria", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Ordem de realização da matrícula extraordinária */
	private int ordem;
	
	/** Matrícula em componente curricular gerada através da matrícula extraordinária */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_matricula_componente")
	private MatriculaComponente matriculaComponente;

	@Override
	public ListaMensagens validate() {
		return null;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	public MatriculaComponente getMatriculaComponente() {
		return matriculaComponente;
	}

	public void setMatriculaComponente(MatriculaComponente matriculaComponente) {
		this.matriculaComponente = matriculaComponente;
	}
}
