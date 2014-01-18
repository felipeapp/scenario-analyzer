package br.ufrn.sigaa.ensino.metropoledigital.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Entidade que armazena a situa��o da matr�cula em uma turma do IMD.
 * 
 * @author Gleydson
 * @author Rafael Silva
 *
 */
@Entity
@Table(name="situacao_matricula_turma", schema="metropole_digital")
public class SituacaoMatriculaTurma {
	/**Vari�vel est�tica que indica que o aluno est� matriculado*/
	public static final int MATRICULADO = 1;
	/**Vari�vel est�tica que indica que o aluno est� em recupera��o*/
	public static final int EM_RECUPERACAO = 2;
	/**Vari�vel est�tica que indica que o aluno est� em aprovado*/
	public static final int APROVADO = 3;
	/**Vari�vel est�tica que indica que o aluno foi reprovado por nota*/	
	public static final int REPROVADO_NOTA = 4;
	/**Vari�vel est�tica que indica que o aluno foi reprovado por faltas*/
	public static final int REPROVADO_FREQUENCIA = 5;
	
	
	/**ID da situa��o matricula*/
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
    parameters={ @Parameter(name="sequence_name", value="metropole_digital.situacao_matricula_turma_id_situacao_matricula_turma_seq") })
	@Column(name="id_situacao_matricula_turma")
	private int id;
	/**Nome da situ��o*/
	@Column(name="descricao")
	private String descricao;

	//GETTERS AND SETTERS
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDenominacao() {
		return descricao;
	}

	public void setDenominacao(String denominacao) {
		this.descricao = denominacao;
	}

}
