/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/07/2008
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

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
 * L�nguas estrangeiras do vestibular (Ingl�s, Franc�s, Espanhol)
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
@Entity
@Table(name = "lingua_estrangeira", schema = "vestibular", uniqueConstraints = {})
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
public class LinguaEstrangeira implements PersistDB {

	/** Constante para a l�ngua Ingl�s. */ 
	public static final int INGLES = 1;
	
	/** Constante para a l�ngua Franc�s. */
	public static final int FRANCES = 2;
	
	/** Constante para a l�ngua Espanhol. */
	public static final int ESPANHOL = 3;

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_lingua_estrangeira", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Descri��o textual da l�ngua estrangeira. */
	private String denominacao;

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

	/** Retorna a descri��o textual da l�ngua estrangeira.
	 * @return
	 */
	public String getDenominacao() {
		return denominacao;
	}

	/** Seta a descri��o textual da l�ngua estrangeira.
	 * @param denominacao
	 */
	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

	/** Retorna uma representa��o textual da l�ngua estrangeira no formato:
	 * ID, seguido de v�rgula, seguido da denomina��o.
	 */
	@Override
	public String toString() {
		return getId() + ", " + getDenominacao();
	}
}
