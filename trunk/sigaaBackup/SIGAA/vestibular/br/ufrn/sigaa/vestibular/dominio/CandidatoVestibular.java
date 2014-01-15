/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/07/2008
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

import javax.persistence.CascadeType;
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
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;

/**
 * Entidade que representa uma inscri��o validada e concorrendo ao Vestibular.
 *
 * @author �dipo Elder F. de Melo
 *
 */
@Entity
@Table(name = "candidato_vestibular", schema = "vestibular", uniqueConstraints = {})
public class CandidatoVestibular implements PersistDB {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_coordenador", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Pontos de classifica��o no vestibular. */
	private double argumentoClassificacao;
	
	/** Classifica��o final do candidato. */
	private int classificacao;
	
	/**
	 * Matriz curricular para qual foi aprovado (pode ser diferente da primeira
	 * op��o do candidato).
	 */
	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_matriz_curricular", unique = false, nullable = true, insertable = true, updatable = true)
	private MatrizCurricular matrizAprovado;
	
	/** Observa��es acerca do candidato. */
	private String observacao;

	/**
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/**
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna os pontos de classifica��o no vestibular. 
	 * @return
	 */
	public double getArgumentoClassificacao() {
		return argumentoClassificacao;
	}

	/** Seta os pontos de classifica��o no vestibular.
	 * @param argumentoClassificacao
	 */
	public void setArgumentoClassificacao(double argumentoClassificacao) {
		this.argumentoClassificacao = argumentoClassificacao;
	}

	/** Retorna a classifica��o final do candidato. 
	 * @return
	 */
	public int getClassificacao() {
		return classificacao;
	}

	/** Seta a classifica��o final do candidato. 
	 * @param classificacao
	 */
	public void setClassificacao(int classificacao) {
		this.classificacao = classificacao;
	}

	/** Retorna a matriz curricular para qual foi aprovado.
	 * @return
	 */
	public MatrizCurricular getMatrizAprovado() {
		return matrizAprovado;
	}

	/** Seta a matriz curricular para qual foi aprovado.
	 * @param matrizAprovado
	 */
	public void setMatrizAprovado(MatrizCurricular matrizAprovado) {
		this.matrizAprovado = matrizAprovado;
	}

	/** Retorna observa��es acerca do candidato. 
	 * @return
	 */
	public String getObservacao() {
		return observacao;
	}

	/** Seta observa��es acerca do candidato.
	 * @param observacao
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	/** Retorna uma string representando um candidato do vestibular no formato:
	 * N�mero de inscri��o, seguido de v�rgula, seguido do nome do candidato, 
	 * seguido de v�rgula.
	 */
	@Override
	public String toString() {
		return "N�o implementado";
	}

}
