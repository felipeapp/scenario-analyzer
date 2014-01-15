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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Define o conceito de regi�o preferencial de prova. Permite ao candidato
 * escolher entre regi�es (atualmente: Caic�, Currais Novos, Natal - regi�o Sul,
 * Natal - regi�o Norte, Natal - regi�o central, Mossor�, Santa Cruz)
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
@Entity
@Table(name = "regiao_preferencial_prova", schema = "vestibular", uniqueConstraints = {})
public class RegiaoPreferencialProva implements PersistDB, Validatable {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_regiao_preferencial_prova", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Denomina��o da regi�o. */
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

	/** Retorna a denomina��o da regi�o. 
	 * @return
	 */
	public String getDenominacao() {
		return denominacao;
	}

	/** Seta a denomina��o da regi�o.
	 * @param denominacao
	 */
	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}
	
	/** Retorna uma representa��o textual da regi�o preferencial de prova no formato:
	 * ID, seguido de v�rgula, seguido da denomina��o.
	 */
	@Override
	public String toString() {
		return getId() + ", " + getDenominacao();
	}

	/** Valida se foi entrado um nome n�o vazio.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(denominacao, "Denomina��o", lista);
		return lista;
	}

}
