package br.ufrn.sigaa.ensino_rede.jsf;

public class ValoresSelecionaDocente {
	private Long valorCpf;

	private Integer valorIdCampus;
	
	private String valorNome;

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

	public void setValorNome(String valorNome) {
		this.valorNome = valorNome;
	}

	public String getValorNome() {
		return valorNome;
	}

}
