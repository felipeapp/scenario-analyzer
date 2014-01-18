package br.ufrn.sigaa.ensino_rede.jsf;

public class ParametrosSelecionaDocente {

	private boolean checkCpf;
	private boolean checkCampus;
	private boolean checkNome;

	public boolean isCheckCpf() {
		return checkCpf;
	}

	public void setCheckCpf(boolean checkCpf) {
		this.checkCpf = checkCpf;
	}

	public boolean isCheckCampus() {
		return checkCampus;
	}

	public void setCheckCampus(boolean checkCampus) {
		this.checkCampus = checkCampus;
	}

	public void setCheckNome(boolean checkNome) {
		this.checkNome = checkNome;
	}

	public boolean isCheckNome() {
		return checkNome;
	}

}