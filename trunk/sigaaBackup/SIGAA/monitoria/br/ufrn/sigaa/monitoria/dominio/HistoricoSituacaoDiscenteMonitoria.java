/*
 * SIGAA - Sistema Integrado de Gestao Academica
 * Superintendencia de Informatica - UFRN
 *
 * Criado em 14/05/2007
 *
 */
package br.ufrn.sigaa.monitoria.dominio;

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
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;

/*******************************************************************************
 *<p> Histórico de todas as situações do discente de monitoria.</p>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "historico_situacao_discente_monitoria", schema = "monitoria")
public class HistoricoSituacaoDiscenteMonitoria implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_historico_situacao_discente_monitoria")
	private int id;

	@CriadoEm
	@Column(name = "data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date data;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_situacao_discente_monitoria")
	private SituacaoDiscenteMonitoria situacaoDiscenteMonitoria;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente_monitoria")
	private DiscenteMonitoria discenteMonitoria;

	@CriadoPor
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;

	/*
	@Column(name = "tipo_monitoria")
	private int tipoMonitoria;
	*/
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_vinculo_discente")
	private TipoVinculoDiscenteMonitoria tipoVinculo = new TipoVinculoDiscenteMonitoria();

	
	public HistoricoSituacaoDiscenteMonitoria() {
	}

	public HistoricoSituacaoDiscenteMonitoria(int idHistoricoProjeto) {
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

	public DiscenteMonitoria getDiscenteMonitoria() {
		return discenteMonitoria;
	}

	public void setDiscenteMonitoria(DiscenteMonitoria discenteMonitoria) {
		this.discenteMonitoria = discenteMonitoria;
	}

	public SituacaoDiscenteMonitoria getSituacaoDiscenteMonitoria() {
		return situacaoDiscenteMonitoria;
	}

	public void setSituacaoDiscenteMonitoria(
			SituacaoDiscenteMonitoria situacaoDiscenteMonitoria) {
		this.situacaoDiscenteMonitoria = situacaoDiscenteMonitoria;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public TipoVinculoDiscenteMonitoria getTipoVinculo() {
		return tipoVinculo;
	}

	public void setTipoVinculo(TipoVinculoDiscenteMonitoria tipoVinculo) {
		this.tipoVinculo = tipoVinculo;
	}
	

}
