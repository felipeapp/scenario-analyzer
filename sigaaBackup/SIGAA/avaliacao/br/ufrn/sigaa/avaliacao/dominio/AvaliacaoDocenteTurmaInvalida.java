/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/03/2010
 * 
 */
package br.ufrn.sigaa.avaliacao.dominio;

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

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade que representa uma Avalia��o Institucional de DocentesTurmas que �
 * ignorada no c�lculo das m�dias do docente, nem na exporta��o de dados para a
 * Comiss�o Pr�pria de Avalia��o - CPA. O motivo de ignorar esta Avalia��o pode
 * depender de regras comums, como se o discente responder zero ou N/A � uma
 * pergunta, ou pela simples determina��o da CPA para que seja ignorada por
 * motivo qualquer.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
@Entity
@Table(name = "avaliacao_docente_turma_invalida", schema = "avaliacao")
public class AvaliacaoDocenteTurmaInvalida implements Comparable<AvaliacaoDocenteTurmaInvalida>, PersistDB {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy = "br.ufrn.arq.dao.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "avaliacao.avaliacao_seq") })
	@Column(name = "id_avaliacao_invalida", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** ID da Avalia��o Institucional que ser� ignorada. */
	@Column(name="id_avaliacao")
	private int idAvaliacao;
	
	/** ID do DocenteTurma que ser� ignorado. */
	@Column(name="id_docente_turma")
	private int idDocenteTurma;
	
	/** Par�metro de Processamento ao qual esta Avalia��o Inv�lida est� associada. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_parametro_processamento")
	private ParametroProcessamentoAvaliacaoInstitucional parametro;
	
	/** Indica se invalidade desta Avalia��o foi determinada por regra geral. Caso false, a invalidade foi terminada por outro motivo, determinado manualmente pela CPA. */
	@Column(name="regra_geral")
	private boolean regraGeral;

	public AvaliacaoDocenteTurmaInvalida() {
		regraGeral = false;
	}
	
	public AvaliacaoDocenteTurmaInvalida(int id) {
		this();
		this.id = id;
	}
	
	public AvaliacaoDocenteTurmaInvalida(int idAvaliacao, int idDocenteTurma) {
		this();
		this.idAvaliacao = idAvaliacao;
		this.idDocenteTurma = idDocenteTurma;
	}
	
	/** Calcula o c�digo Hash teste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idAvaliacao;
		result = prime * result + idDocenteTurma;
		return result;
	}

	/** Compara se este objeto e o informado possuem mesmo idAvaliacao e idDocenteTurma.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AvaliacaoDocenteTurmaInvalida)) {
			return false;
		}
		AvaliacaoDocenteTurmaInvalida other = (AvaliacaoDocenteTurmaInvalida) obj;
		if (idAvaliacao != other.idAvaliacao) {
			return false;
		}
		if (idDocenteTurma != other.idDocenteTurma) {
			return false;
		}
		return true;
	}

	/** Retorna uma representa��o textual deste objeto no formato: (idAvaliacao, idDocenteTurma).
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("(%d, %d)", idAvaliacao, idDocenteTurma);
	}

	/** Compara este objeto com o informado, terminando sua ordem baseado no idAvaliacao e idDocenteTurma. 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(AvaliacaoDocenteTurmaInvalida other) {
		if (this.idAvaliacao == other.idAvaliacao) {
			return this.idDocenteTurma - other.idDocenteTurma;
		} else {
			return this.idAvaliacao - other.idAvaliacao;
		}
	}

	/** Retorna a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Retorna a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna o ID da Avalia��o Institucional que ser� ignorada. 
	 * @return
	 */
	public int getIdAvaliacao() {
		return idAvaliacao;
	}

	/** Seta o ID da Avalia��o Institucional que ser� ignorada. 
	 * @param idAvaliacao
	 */
	public void setIdAvaliacao(int idAvaliacao) {
		this.idAvaliacao = idAvaliacao;
	}

	/** Retorna o ID do DocenteTurma que ser� ignorado. 
	 * @return
	 */
	public int getIdDocenteTurma() {
		return idDocenteTurma;
	}

	/** Seta o ID do DocenteTurma que ser� ignorado.
	 * @param idDocenteTurma
	 */
	public void setIdDocenteTurma(int idDocenteTurma) {
		this.idDocenteTurma = idDocenteTurma;
	}

	/** Retorna o Par�metro de Processamento ao qual esta Avalia��o Inv�lida est� associada. 
	 * @return
	 */
	public ParametroProcessamentoAvaliacaoInstitucional getParametro() {
		return parametro;
	}

	/** Seta o Par�metro de Processamento ao qual esta Avalia��o Inv�lida est� associada.
	 * @param parametro
	 */
	public void setParametro(ParametroProcessamentoAvaliacaoInstitucional parametro) {
		this.parametro = parametro;
	}

	/** Indica se invalidade desta Avalia��o foi determinada por regra geral. 
	 * @return Caso false, a invalidade foi terminada por outro motivo, determinado manualmente pela CPA. 
	 */
	public boolean isRegraGeral() {
		return regraGeral;
	}

	/** Seta se invalidade desta Avalia��o foi determinada por regra geral. 
	 * @param regraGeral Caso false, a invalidade foi terminada por outro motivo, determinado manualmente pela CPA. 
	 */
	public void setRegraGeral(boolean regraGeral) {
		this.regraGeral = regraGeral;
	}

}
