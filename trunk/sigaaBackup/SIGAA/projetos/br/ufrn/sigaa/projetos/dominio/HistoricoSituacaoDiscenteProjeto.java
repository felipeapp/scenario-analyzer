/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 03/01/2011
 *
 */
package br.ufrn.sigaa.projetos.dominio;

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

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;

/**
 * <p>
 * Histórico de todas as situações do discente de projetos. 
 * Um registro no histórico é criado após o discente ser vinculado a um projeto.
 * </p>
 * 
 * @author geyson
 * 
 */
@Entity
@Table(name = "historico_situacao_discente_projeto", schema = "projetos", uniqueConstraints = {})
public class HistoricoSituacaoDiscenteProjeto implements PersistDB {
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name = "id_historico_situacao_discente_projeto", unique = true, nullable = false)
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;

	/** data */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data")
	private Date data;

	/** Situação do histórico */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_situacao_discente_projeto")
	private TipoSituacaoDiscenteProjeto situacaoDiscenteProjeto;

	/** Tipo vínculo do discente. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_vinculo_discente")
	private TipoVinculoDiscente tipoVinculo = new TipoVinculoDiscente();

	/** Discente na qual se refere o registro no histórico */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente_projeto")
	private DiscenteProjeto discenteProjeto;

	/** Responsável pela auteração no histórico */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public TipoSituacaoDiscenteProjeto getSituacaoDiscenteProjeto() {
		return situacaoDiscenteProjeto;
	}

	public void setSituacaoDiscenteProjeto(
			TipoSituacaoDiscenteProjeto situacaoDiscenteProjeto) {
		this.situacaoDiscenteProjeto = situacaoDiscenteProjeto;
	}

	public TipoVinculoDiscente getTipoVinculo() {
		return tipoVinculo;
	}

	public void setTipoVinculo(TipoVinculoDiscente tipoVinculo) {
		this.tipoVinculo = tipoVinculo;
	}

	public DiscenteProjeto getDiscenteProjeto() {
		return discenteProjeto;
	}

	public void setDiscenteProjeto(DiscenteProjeto discenteProjeto) {
		this.discenteProjeto = discenteProjeto;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

}
