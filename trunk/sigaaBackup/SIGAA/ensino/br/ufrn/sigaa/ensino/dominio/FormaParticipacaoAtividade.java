/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 *  Created on 05/04/2010
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Classe que define as formas da participação dos discentes e docentes nas atividades
 * acadêmicas específicas. As formas de participação podem ser de três tipos:
 * 
 * <ul>
 * <li>atividade acadêmica individual;
 * <li>atividade de orientação individual;
 * <li>atividade especial coletiva.
 * </ul>
 * 
 * @author Henrique André
 */
@Entity
@Table(name = "forma_participacao_atividade", schema = "ensino")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class FormaParticipacaoAtividade implements PersistDB {

	/**
	 * As atividades acadêmicas individuais são as atividades acadêmicas específicas que
	 * o aluno desempenha sem participação ou orientação de um professor da UFRN e que, no
	 * entendimento do projeto pedagógico do curso, contribuem para sua formação e devem ser
	 * registradas no histórico escolar.
	 */
	public static final int ACADEMICA_INDIVIDUAL	= 1;
	
	/**
	 * As atividades de orientação individual são as atividades acadêmicas específicas que
	 * o aluno desempenha individualmente sob a orientação de um professor da UFRN e que, no
	 * entendimento do projeto pedagógico do curso, são obrigatórias ou contribuem para sua formação e
	 * devem ser registradas no histórico escolar.
	 */
	public static final int ORIENTACAO_INDIVIDUAL	= 2;
	/**
	 * As atividades especiais coletivas são as atividades acadêmicas específicas previstas
	 * no projeto pedagógico do curso em que um grupo de alunos cumpre as atividades previstas para
	 * aquele componente curricular sob a orientação ou supervisão de um ou mais de um professor da
	 * UFRN.
	 */
	public static final int ESPECIAL_COLETIVA		= 3;
	
	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
   @Column(name = "id_forma_participacao", nullable = false)
	private int id;

	/** Descrição da forma de participação. */
	@Column(name = "descricao", nullable = false)
	private String descricao;

	@Column(name = "permite_ch_docente", nullable = false)
	private boolean permiteCHDocente = false;
	

	@Column(name = "permite_criar_turma", nullable = false)
	private boolean permiteCriarTurma = false;
	
	/** Retorna a chave primária
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave primária
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna a Descrição da forma de participação. 
	 * @return
	 */
	public String getDescricao() {
		return descricao;
	}

	/** Seta a Descrição da forma de participação.
	 * @param descricao
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/** Indica se a forma de participação permite que o docente tenha carga horária na turma. 
	 * @return
	 */
	public boolean isPermiteCHDocente() {
		return permiteCHDocente;
	}

	/** Seta se a forma de participação permite que o docente tenha carga horária na turma. 
	 * @param permiteCHDocente
	 */
	public void setPermiteCHDocente(boolean permiteCHDocente) {
		this.permiteCHDocente = permiteCHDocente;
	}

	public boolean isPermiteCriarTurma() {
		return permiteCriarTurma;
	}

	public void setPermiteCriarTurma(boolean permiteCriarTurma) {
		this.permiteCriarTurma = permiteCriarTurma;
	}

	/** Retorna uma descrição textual do objeto, no formato: id, seguido por '-', seguido pela descrição da forma de participação. 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id + " - " + descricao;
	}
	
	/** Indica se a forma de participação é ACADEMICA INDIVIDUAL
	 * @return
	 */
	public boolean isAcademicaIndividual() {
		return id == ACADEMICA_INDIVIDUAL;
	}
	
	/** Indica se a forma de participação é ORIENTACAO INDIVIDUAL
	 * @return
	 */
	public boolean isOrientacaoIndividual() {
		return id == ORIENTACAO_INDIVIDUAL;
	}
	
	/** Indica se a forma de participação é ESPECIAL COLETIVA
	 * @return
	 */
	public boolean isEspecialColetiva() {
		return id == ESPECIAL_COLETIVA;
	}
}
