/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/07/2012
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade que registra a associação de um discente de formação complementar com o tipo de bolsa
 * que ele possui.
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name = "bolsa_discente_formacao_complementar", schema = "tecnico", uniqueConstraints={})
public class BolsaDiscenteFormacaoComplementar implements PersistDB {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") }) 
	@Column(name = "id", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "id_discente")
	private DiscenteTecnico discente;
	
	@ManyToOne
	@JoinColumn(name = "id_tipo_bolsa")
	private TipoBolsaFormacaoComplementar tipoBolsa;
	
	@CampoAtivo
	private boolean ativo;
	
	public BolsaDiscenteFormacaoComplementar() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DiscenteTecnico getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteTecnico discente) {
		this.discente = discente;
	}

	public TipoBolsaFormacaoComplementar getTipoBolsa() {
		return tipoBolsa;
	}

	public void setTipoBolsa(TipoBolsaFormacaoComplementar tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
}
