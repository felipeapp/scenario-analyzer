package br.ufrn.sigaa.ensino_rede.jsf;

public class ParametrosSelecionaDiscente {

	private boolean checkCpf;
	private boolean checkNome;
	private boolean checkCampus;
	private boolean checkProgramaRede;
	private boolean checkStatusDiscente;

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

	public boolean isCheckNome() {
		return checkNome;
	}

	public void setCheckNome(boolean checkNome) {
		this.checkNome = checkNome;
	}

	public boolean isCheckProgramaRede() {
		return checkProgramaRede;
	}

	public void setCheckProgramaRede(boolean checkProgramaRede) {
		this.checkProgramaRede = checkProgramaRede;
	}

	public boolean isCheckStatusDiscente() {
		return checkStatusDiscente;
	}

	public void setCheckStatusDiscente(boolean checkStatusDiscente) {
		this.checkStatusDiscente = checkStatusDiscente;
	}

}
