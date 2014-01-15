/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/02/2008
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente;

/*******************************************************************************
 * <p>
 * Histórico de todas as situações do discente de extensão. Um registro no
 * histórico é criado sempre que um discentes eh vinculado a uma ação de
 * extensão ou quando algum tipo de alteração nesse vínculo é realizada.
 * </p>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "historico_situacao_discente_extensao", schema = "extensao", uniqueConstraints = {})
public class HistoricoSituacaoDiscenteExtensao implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_historico_situacao_discente_extensao", unique = true, nullable = false)
	private int id;

	//Data
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data")
	private Date data;

	//Situação do histórico
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_situacao_discente_extensao")
	private TipoSituacaoDiscenteExtensao situacaoDiscenteExtensao;

	//Tipo vínculo do discente.
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_vinculo_discente")
	private TipoVinculoDiscente tipoVinculo = new TipoVinculoDiscente();

	//Discente na qual se refere o registro no histórico
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente_extensao")
	private DiscenteExtensao discenteExtensao;

	//Responsável pela auteração no histórico
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;

	public HistoricoSituacaoDiscenteExtensao() {
	}

	public HistoricoSituacaoDiscenteExtensao(int idHistoricoProjeto) {
		this.id = idHistoricoProjeto;
	}

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

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public TipoSituacaoDiscenteExtensao getSituacaoDiscenteExtensao() {
		return situacaoDiscenteExtensao;
	}

	public void setSituacaoDiscenteExtensao(
			TipoSituacaoDiscenteExtensao situacaoDiscenteExtensao) {
		this.situacaoDiscenteExtensao = situacaoDiscenteExtensao;
	}

	public DiscenteExtensao getDiscenteExtensao() {
		return discenteExtensao;
	}

	public void setDiscenteExtensao(DiscenteExtensao discenteExtensao) {
		this.discenteExtensao = discenteExtensao;
	}

	public TipoVinculoDiscente getTipoVinculo() {
		return tipoVinculo;
	}

	public void setTipoVinculo(TipoVinculoDiscente tipoVinculo) {
		this.tipoVinculo = tipoVinculo;
	}

}
