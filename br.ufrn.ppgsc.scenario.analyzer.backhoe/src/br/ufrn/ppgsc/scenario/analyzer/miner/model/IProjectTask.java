package br.ufrn.ppgsc.scenario.analyzer.miner.model;

public class IProjectTask {

	private long id;
	private long numero;
	private long idTipo;
	private String tipoDenominacao;

	public IProjectTask() {

	}

	public IProjectTask(long id, long numero, long idTipo,
			String tipoDenominacao) {
		this.id = id;
		this.numero = numero;
		this.idTipo = idTipo;
		this.tipoDenominacao = tipoDenominacao;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getNumero() {
		return numero;
	}

	public void setNumero(long numero) {
		this.numero = numero;
	}

	public long getIdTipo() {
		return idTipo;
	}

	public void setIdTipo(long idTipo) {
		this.idTipo = idTipo;
	}

	public String getTipoDenominacao() {
		return tipoDenominacao;
	}

	public void setTipoDenominacao(String tipoDenominacao) {
		this.tipoDenominacao = tipoDenominacao;
	}

}
