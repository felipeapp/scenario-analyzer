/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 05/05/2008
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade LocalAplicacaoProva Define as salas associadas a um local de
 * aplica��o de prova
 * 
 * @author �dipo
 * 
 */
@Entity
@Table(name = "sala", schema = "vestibular", uniqueConstraints = {})
public class Sala implements PersistDB, Validatable {
	
	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_sala", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** N�mero/ nome da sala. */
	private String numero;
	
	/** Capacidade m�xima de alunos na sala. */ 
	@Column(name = "capacidade_maxima")
	private int capacidadeMaxima;
	
	/** Capacidade ideal de candidatos na sala. */
	@Column(name = "capacidade_ideal")
	private int capacidadeIdeal;
	
	/** �rea �til, em metros quadrados, da sala. */
	private double area;
	
	/** Local de aplica��o de prova ao qual esta sala pertence. */
	@ManyToOne
	@JoinColumn(name = "id_local_aplicacao_prova", unique = false, nullable = true, insertable = true, updatable = true)
	private LocalAplicacaoProva localAplicacaoProva;

	/** Retorna o n�mero/ nome da sala. 
	 * @return
	 */
	public String getNumero() {
		return numero;
	}

	/** Seta o n�mero/ nome da sala.
	 * @param numero
	 */
	public void setNumero(String numero) {
		this.numero = numero;
	}

	/** Retorna a capacidade m�xima de alunos na sala. 
	 * @return
	 */
	public int getCapacidadeMaxima() {
		return capacidadeMaxima;
	}

	/** Seta a capacidade m�xima de alunos na sala.
	 * @param capacidadeMaxima
	 */
	public void setCapacidadeMaxima(int capacidadeMaxima) {
		this.capacidadeMaxima = capacidadeMaxima;
	}

	/** Retorna a capacidade ideal de candidatos na sala. 
	 * @return
	 */
	public int getCapacidadeIdeal() {
		return capacidadeIdeal;
	}

	/** Seta a capacidade ideal de candidatos na sala.
	 * @param capacidadeIdeal
	 */
	public void setCapacidadeIdeal(int capacidadeIdeal) {
		this.capacidadeIdeal = capacidadeIdeal;
	}

	/** Retorna a �rea �til, em metros quadrados, da sala. 
	 * @return
	 */
	public double getArea() {
		return area;
	}

	/** Seta a �rea �til, em metros quadrados, da sala.
	 * @param area
	 */
	public void setArea(double area) {
		this.area = area;
	}

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

	/** Retorna o local de aplica��o de prova ao qual esta sala pertence. 
	 * @return
	 */
	public LocalAplicacaoProva getLocalAplicacaoProva() {
		return localAplicacaoProva;
	}

	/** Seta o local de aplica��o de prova ao qual esta sala pertence.
	 * @param localAplicacaoProva
	 */
	public void setLocalAplicacaoProva(LocalAplicacaoProva localAplicacaoProva) {
		this.localAplicacaoProva = localAplicacaoProva;
	}

	/**
	 * Retorna uma descri��o textual da sala, informando os valores da
	 * capacidades ideal e m�xima, e a �rea em metros quadrados.
	 * 
	 * @return
	 */
	@Transient
	public String getDescricao() {
		return numero + " [ideal: " + capacidadeIdeal + ", " + "m�x: "
				+ capacidadeMaxima + ", " + "�rea: "
				+ area + "m2]";
	}

	/** Valida os dados: n�mero da sala, capacidade ideal, capacidade m�xima.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(this.numero, "N�mero da sala", lista);
		ValidatorUtil.validateRequired(this.capacidadeIdeal, "Capacidade ideal de alunos na sala", lista);
		ValidatorUtil.validateRequired(this.capacidadeMaxima, "Capacidade m�xima de alunos na sala", lista);
		return lista;
	}
	
	/** Retorna uma representa��o textual da sala no formato:
	 * n�mero da sala, seguido por colchete, seguido da palavra "ideal: ", seguido da capacidade ideal,
	 * seguido de v�rgula, seguido da palavra "m�x: ", seguido de v�rgula, seguido da capacidade m�xima,
	 * seguido de v�rgula, seguido da palavra "�rea: ", seguido da �rea, seguido da palavra "m2",
	 * seguido de colchete.
	 */
	@Override
	public String toString() {
		return getDescricao();
	}
}
