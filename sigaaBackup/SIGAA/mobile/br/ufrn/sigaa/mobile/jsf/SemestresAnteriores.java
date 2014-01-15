package br.ufrn.sigaa.mobile.jsf;

public class SemestresAnteriores {
	
	private int ano;
	
	private int periodo;

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ano;
		result = prime * result + periodo;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SemestresAnteriores other = (SemestresAnteriores) obj;
		if (ano != other.ano)
			return false;
		if (periodo != other.periodo)
			return false;
		return true;
	}

}
