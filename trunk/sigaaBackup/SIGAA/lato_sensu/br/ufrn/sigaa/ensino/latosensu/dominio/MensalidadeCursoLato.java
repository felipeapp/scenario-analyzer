/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 02/05/2012
 *
 */
package br.ufrn.sigaa.ensino.latosensu.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.comum.gru.dominio.GuiaRecolhimentoUniao;

/**
 * Classe que associa um discente à uma mensalidade do curso de lato sensu.
 * 
 * @author Édipo Elder F. de Melo
 *
 */
@Entity
@Table(name = "mensalidade_discente", schema="lato_sensu")
public class MensalidadeCursoLato implements PersistDB {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	@Column(name = "id_mensalidade_discente", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Discente a quem pertence a mensaldiade. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente")
	private DiscenteLato discente;
	
	/** ID da GRU associada à mensalidade. */
	@Column(name = "id_gru")
	private int idGRU;
	
	/** Indica se a mensalidade foi quitada ou não. */
	private boolean quitada = false;
	
	/** Ordem da mensalidade. */
	private int ordem;
	
	/** Data de vencimento da GRU */
	private Date vencimento;
	
	/** Objeto GRU recuperado de dados comuns. */
	@Transient
	private GuiaRecolhimentoUniao gru;
	
	public MensalidadeCursoLato() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DiscenteLato getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteLato discente) {
		this.discente = discente;
	}

	public int getIdGRU() {
		return idGRU;
	}

	public void setIdGRU(int idGRU) {
		this.idGRU = idGRU;
	}

	public boolean isQuitada() {
		return quitada;
	}

	public void setQuitada(boolean quitada) {
		this.quitada = quitada;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(ordem).append(" - ");
		if (quitada)
			str.append("quitada");
		else
			str.append("em aberto");
		return str.toString();
	}

	public Date getVencimento() {
		return vencimento;
	}

	public void setVencimento(Date vencimento) {
		this.vencimento = vencimento;
	}

	public GuiaRecolhimentoUniao getGru() {
		return gru;
	}

	public void setGru(GuiaRecolhimentoUniao gru) {
		this.gru = gru;
	}
}
