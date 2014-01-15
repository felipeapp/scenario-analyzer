package br.ufrn.academico.dominio;

public class MembroProjeto {

	private Integer idServidor;
	
	private Integer idDiscente;
	
	private String tipoParticipacao;
	
	private Integer chHorariaSemanal;
	
	public Integer getIdServidor() {
		return idServidor;
	}

	public void setIdServidor(Integer idServidor) {
		this.idServidor = idServidor;
	}

	public Integer getIdDiscente() {
		return idDiscente;
	}

	public void setIdDiscente(Integer idDiscente) {
		this.idDiscente = idDiscente;
	}

	public String getTipoParticipacao() {
		return tipoParticipacao;
	}

	public void setTipoParticipacao(String tipoParticipacao) {
		this.tipoParticipacao = tipoParticipacao;
	}

	public Integer getChHorariaSemanal() {
		return chHorariaSemanal;
	}

	public void setChHorariaSemanal(Integer chHorariaSemanal) {
		this.chHorariaSemanal = chHorariaSemanal;
	}
	
}
