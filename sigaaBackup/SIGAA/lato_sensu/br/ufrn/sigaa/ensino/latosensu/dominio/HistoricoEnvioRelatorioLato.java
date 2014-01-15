package br.ufrn.sigaa.ensino.latosensu.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

@Entity
@Table(name = "historico_relatorio_lato", schema = "lato_sensu", uniqueConstraints = {})
public class HistoricoEnvioRelatorioLato implements Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_historico_relatorio_lato", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	@ManyToOne @JoinColumn(name="id_relatorio")
	private RelatorioFinalLato relatorio = new RelatorioFinalLato();
	
	private String parecer;
	
	@Column(name="id_status")
	private int statusAtual;

	@Column(name="data_cadastro")
	private Date dataCadastro;
	
	@Column(name="registro_entrada")
	private int registroEntrada;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getParecer() {
		return parecer;
	}

	public void setParecer(String parecer) {
		this.parecer = parecer;
	}

	public RelatorioFinalLato getRelatorio() {
		return relatorio;
	}

	public void setRelatorio(RelatorioFinalLato relatorio) {
		this.relatorio = relatorio;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public int getStatusAtual() {
		return statusAtual;
	}

	public void setStatusAtual(int statusAtual) {
		this.statusAtual = statusAtual;
	}

	public int getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(int registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	@Override
	public ListaMensagens validate() {
		return null;
	}
	
}