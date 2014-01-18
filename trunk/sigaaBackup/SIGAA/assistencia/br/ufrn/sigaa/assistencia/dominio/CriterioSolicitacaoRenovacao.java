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

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

@Entity
@Table(name = "criterio_solicitacao_renovacao", schema = "sae")
public class CriterioSolicitacaoRenovacao implements Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
   	@Column(name = "id_criterio")
	private int id;
	
	/** Estados que uma Bolsa Auxílio pode se encontrar. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_situacao_bolsa")
	private SituacaoBolsaAuxilio situacaoBolsa;

	/** Representa os tipos de bolsa auxílio disponíveis */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_bolsa_auxilio")
	private TipoBolsaAuxilio tipoBolsaAuxilio;

	/** Indica se o componente é ativo ou não */
	@CampoAtivo(true)
	private boolean ativo = true;

	/** Bolsa do discente no SIPAC. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "tipo_renovacao")
	private TipoRenovacaoBolsa tipoRenovacao;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public SituacaoBolsaAuxilio getSituacaoBolsa() {
		return situacaoBolsa;
	}

	public void setSituacaoBolsa(SituacaoBolsaAuxilio situacaoBolsa) {
		this.situacaoBolsa = situacaoBolsa;
	}

	public TipoBolsaAuxilio getTipoBolsaAuxilio() {
		return tipoBolsaAuxilio;
	}

	public void setTipoBolsaAuxilio(TipoBolsaAuxilio tipoBolsaAuxilio) {
		this.tipoBolsaAuxilio = tipoBolsaAuxilio;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public TipoRenovacaoBolsa getTipoRenovacao() {
		return tipoRenovacao;
	}

	public void setTipoRenovacao(TipoRenovacaoBolsa tipoRenovacao) {
		this.tipoRenovacao = tipoRenovacao;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequiredId(tipoBolsaAuxilio.getId(), "Bolsa Auxílio", lista);
		ValidatorUtil.validateRequiredId(situacaoBolsa.getId(), "Situação Auxílio", lista);
		ValidatorUtil.validateRequiredId(tipoRenovacao.getId(), "Tipo Renovação", lista);
		return lista;
	}

}