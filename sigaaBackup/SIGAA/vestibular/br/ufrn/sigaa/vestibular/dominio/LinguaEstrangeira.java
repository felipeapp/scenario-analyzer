/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Línguas estrangeiras do vestibular (Inglês, Francês, Espanhol)
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
@Entity
@Table(name = "lingua_estrangeira", schema = "vestibular", uniqueConstraints = {})
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
public class LinguaEstrangeira implements PersistDB {

	/** Constante para a língua Inglês. */ 
	public static final int INGLES = 1;
	
	/** Constante para a língua Francês. */
	public static final int FRANCES = 2;
	
	/** Constante para a língua Espanhol. */
	public static final int ESPANHOL = 3;

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_lingua_estrangeira", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Descrição textual da língua estrangeira. */
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

	/** Retorna a descrição textual da língua estrangeira.
	 * @return
	 */
	public String getDenominacao() {
		return denominacao;
	}

	/** Seta a descrição textual da língua estrangeira.
	 * @param denominacao
	 */
	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

	/** Retorna uma representação textual da língua estrangeira no formato:
	 * ID, seguido de vírgula, seguido da denominação.
	 */
	@Override
	public String toString() {
		return getId() + ", " + getDenominacao();
	}
}
