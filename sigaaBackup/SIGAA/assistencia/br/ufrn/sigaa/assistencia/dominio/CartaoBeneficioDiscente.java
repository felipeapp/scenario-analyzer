/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 25/02/2011
 * 
 */
package br.ufrn.sigaa.assistencia.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * 
 * Classe que representa cartão de benefício do discente.
 * 
 * @author geyson
 *
 */
@Entity
@Table(name = "cartao_beneficio_discente", schema = "sae")
public class CartaoBeneficioDiscente implements PersistDB {

	/** Identificador */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name = "id_cartao_beneficio_discente", unique = true, nullable = false)
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/** Cartão bolsa alimentação */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_cartao_bolsa_alimentacao")
	private CartaoBolsaAlimentacao cartaoBolsaAlimentacao;
	
	/** Discente com cartão benefício */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente")
	private Discente discente;

	/** Registro de atribuição */
	@Column( name ="id_registro_atribuicao")
	private Integer registroAtribuicao;
	
	/** Data do benefício */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data")
	private Date data;

	/** Informa se cartão benefício está ativo */
	@Column(name = "ativo")
	@CampoAtivo
	private boolean ativo;
	
	/** Status Cartão Benefício */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_status_cartao_beneficio")
	private StatusCartaoBeneficio statusCartaoBeneficio = new StatusCartaoBeneficio();
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public CartaoBolsaAlimentacao getCartaoBolsaAlimentacao() {
		return cartaoBolsaAlimentacao;
	}

	public void setCartaoBolsaAlimentacao(
			CartaoBolsaAlimentacao cartaoBolsaAlimentacao) {
		this.cartaoBolsaAlimentacao = cartaoBolsaAlimentacao;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public Integer getRegistroAtribuicao() {
		return registroAtribuicao;
	}

	public void setRegistroAtribuicao(Integer registroAtribuicao) {
		this.registroAtribuicao = registroAtribuicao;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public StatusCartaoBeneficio getStatusCartaoBeneficio() {
		return statusCartaoBeneficio;
	}

	public void setStatusCartaoBeneficio(StatusCartaoBeneficio statusCartaoBeneficio) {
		this.statusCartaoBeneficio = statusCartaoBeneficio;
	}
	
}
