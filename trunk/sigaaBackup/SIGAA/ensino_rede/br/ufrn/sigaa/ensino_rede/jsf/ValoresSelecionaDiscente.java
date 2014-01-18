package br.ufrn.sigaa.ensino_rede.jsf;

import java.util.List;

import br.ufrn.sigaa.ensino_rede.dominio.StatusDiscenteAssociado;

public class ValoresSelecionaDiscente {

	private Long valorCpf;

	private Integer valorIdCampus;
	
	private String valorNome;
	
	private Integer valorIdProgramaRede;
	
	private List<StatusDiscenteAssociado> status;

	public Long getValorCpf() {
		return valorCpf;
	}

	public void setValorCpf(Long valorCpf) {
		this.valorCpf = valorCpf;
	}

	public Integer getValorIdCampus() {
		return valorIdCampus;
	}

	public void setValorIdCampus(Integer valorIdCampus) {
		this.valorIdCampus = valorIdCampus;
	}

	public String getValorNome() {
		return valorNome;
	}

	public void setValorNome(String valorNome) {
		this.valorNome = valorNome;
	}

	public Integer getValorIdProgramaRede() {
		return valorIdProgramaRede;
	}

	public void setValorIdProgramaRede(Integer valorIdProgramaRede) {
		this.valorIdProgramaRede = valorIdProgramaRede;
	}

	public List<StatusDiscenteAssociado> getStatus() {
		return status;
	}

	public void setStatus(List<StatusDiscenteAssociado> status) {
		this.status = status;
	}
	
}
