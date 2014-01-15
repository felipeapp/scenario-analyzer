/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Classe que define as formas da participa��o dos discentes e docentes nas atividades
 * acad�micas espec�ficas. As formas de participa��o podem ser de tr�s tipos:
 * 
 * <ul>
 * <li>atividade acad�mica individual;
 * <li>atividade de orienta��o individual;
 * <li>atividade especial coletiva.
 * </ul>
 * 
 * @author Henrique Andr�
 */
@Entity
@Table(name = "forma_participacao_atividade", schema = "ensino")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class FormaParticipacaoAtividade implements PersistDB {

	/**
	 * As atividades acad�micas individuais s�o as atividades acad�micas espec�ficas que
	 * o aluno desempenha sem participa��o ou orienta��o de um professor da UFRN e que, no
	 * entendimento do projeto pedag�gico do curso, contribuem para sua forma��o e devem ser
	 * registradas no hist�rico escolar.
	 */
	public static final int ACADEMICA_INDIVIDUAL	= 1;
	
	/**
	 * As atividades de orienta��o individual s�o as atividades acad�micas espec�ficas que
	 * o aluno desempenha individualmente sob a orienta��o de um professor da UFRN e que, no
	 * entendimento do projeto pedag�gico do curso, s�o obrigat�rias ou contribuem para sua forma��o e
	 * devem ser registradas no hist�rico escolar.
	 */
	public static final int ORIENTACAO_INDIVIDUAL	= 2;
	/**
	 * As atividades especiais coletivas s�o as atividades acad�micas espec�ficas previstas
	 * no projeto pedag�gico do curso em que um grupo de alunos cumpre as atividades previstas para
	 * aquele componente curricular sob a orienta��o ou supervis�o de um ou mais de um professor da
	 * UFRN.
	 */
	public static final int ESPECIAL_COLETIVA		= 3;
	
	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
   @Column(name = "id_forma_participacao", nullable = false)
	private int id;

	/** Descri��o da forma de participa��o. */
	@Column(name = "descricao", nullable = false)
	private String descricao;

	@Column(name = "permite_ch_docente", nullable = false)
	private boolean permiteCHDocente = false;
	

	@Column(name = "permite_criar_turma", nullable = false)
	private boolean permiteCriarTurma = false;
	
	/** Retorna a chave prim�ria
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave prim�ria
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna a Descri��o da forma de participa��o. 
	 * @return
	 */
	public String getDescricao() {
		return descricao;
	}

	/** Seta a Descri��o da forma de participa��o.
	 * @param descricao
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/** Indica se a forma de participa��o permite que o docente tenha carga hor�ria na turma. 
	 * @return
	 */
	public boolean isPermiteCHDocente() {
		return permiteCHDocente;
	}

	/** Seta se a forma de participa��o permite que o docente tenha carga hor�ria na turma. 
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

	/** Retorna uma descri��o textual do objeto, no formato: id, seguido por '-', seguido pela descri��o da forma de participa��o. 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id + " - " + descricao;
	}
	
	/** Indica se a forma de participa��o � ACADEMICA INDIVIDUAL
	 * @return
	 */
	public boolean isAcademicaIndividual() {
		return id == ACADEMICA_INDIVIDUAL;
	}
	
	/** Indica se a forma de participa��o � ORIENTACAO INDIVIDUAL
	 * @return
	 */
	public boolean isOrientacaoIndividual() {
		return id == ORIENTACAO_INDIVIDUAL;
	}
	
	/** Indica se a forma de participa��o � ESPECIAL COLETIVA
	 * @return
	 */
	public boolean isEspecialColetiva() {
		return id == ESPECIAL_COLETIVA;
	}
}
