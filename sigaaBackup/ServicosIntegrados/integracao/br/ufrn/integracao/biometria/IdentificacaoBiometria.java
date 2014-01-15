/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 16/03/2011
 * 
 */
package br.ufrn.integracao.biometria;

import java.util.Date;

/**
 *
 * <p>Guarda os dados das digitais em cache</p>
 * 
 * @author jadson
 *
 */
public class IdentificacaoBiometria {

	/**
	 * O cpf do usuário
	 */
	private long cpf;
	
	/**
	 * indica o dedo coletado D = direito , E = esquerdo. Junto com o cpf, identifica a digital
	 */
	private String dedoColetado;
	
	
	/**
	 * O array de bytes que representa a digitail do usuário
	 */
	private byte[] digital;
	
	/**
	 * A data do cadastros
	 */
	private Date dataCadastro;

	/**
	 * Construtor de digitais já existentes no sistema
	 * 
	 * @param l
	 * @param bs
	 */
	public IdentificacaoBiometria(long cpf, String dedoColetado) {
		this.cpf = cpf;
		this.dedoColetado = dedoColetado;
	}
	
	/**
	 * Construtor utilizado para novas digitais
	 * 
	 * @param l
	 * @param bs
	 */
	public IdentificacaoBiometria(long cpf, String dedoColetado, byte[] bs, Date data) {
		this(cpf, dedoColetado);
		this.digital = bs;
		this.dataCadastro = data;
	}

	
	
	public long getCpf() {
		return cpf;
	}

	public void setCpf(long cpf) {
		this.cpf = cpf;
	}

	public byte[] getDigital() {
		return digital;
	}

	public void setDigital(byte[] digital) {
		this.digital = digital;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public String getDedoColetado() {
		return dedoColetado;
	}

	public void setDedoColetado(String dedoColetado) {
		this.dedoColetado = dedoColetado;
	}

	/**
	 * O cpf e o dedo coletado identificam uma digital 
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (cpf ^ (cpf >>> 32));
		result = prime * result
				+ ((dedoColetado == null) ? 0 : dedoColetado.hashCode());
		return result;
	}

	/**
	 * O cpf e o dedo coletado identificam uma digital 
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IdentificacaoBiometria other = (IdentificacaoBiometria) obj;
		if (cpf != other.cpf)
			return false;
		if (dedoColetado == null) {
			if (other.dedoColetado != null)
				return false;
		} else if (!dedoColetado.equals(other.dedoColetado))
			return false;
		return true;
	}
	
}
