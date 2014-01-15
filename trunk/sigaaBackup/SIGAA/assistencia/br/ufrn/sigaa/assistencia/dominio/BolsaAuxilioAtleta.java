package br.ufrn.sigaa.assistencia.dominio;

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

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Classe que representa uma SOLICITAÇÃO de Bolsa Auxílio por parte do atleta.
 *
 */

@Entity
@Table(name = "bolsa_auxilio_atleta", schema = "sae")
public class BolsaAuxilioAtleta implements Validatable {

	/** Chave primária da bolsa de auxilio */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
   	@Column(name = "id_bolsa_auxilio_atleta")
	private int id;

	/** Estados que uma Bolsa Auxílio pode se encontrar. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_bolsa_auxilio")
	private BolsaAuxilio bolsaAuxilio;

	/** Estados que uma Bolsa Auxílio pode se encontrar. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_modalidade_esportiva")
	private ModalidadeEsportiva modalidadeEsportiva;

	/** Federação a qual o atleta é associado */
	private String federacao;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BolsaAuxilio getBolsaAuxilio() {
		return bolsaAuxilio;
	}

	public void setBolsaAuxilio(BolsaAuxilio bolsaAuxilio) {
		this.bolsaAuxilio = bolsaAuxilio;
	}

	public ModalidadeEsportiva getModalidadeEsportiva() {
		return modalidadeEsportiva;
	}

	public void setModalidadeEsportiva(ModalidadeEsportiva modalidadeEsportiva) {
		this.modalidadeEsportiva = modalidadeEsportiva;
	}

	public String getFederacao() {
		return federacao;
	}

	public void setFederacao(String federacao) {
		this.federacao = federacao;
	}

	@Override
	public ListaMensagens validate() {
		return null;
	}
	
}