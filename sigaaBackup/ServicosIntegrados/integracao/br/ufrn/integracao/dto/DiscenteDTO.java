/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 31/08/2009
 *
 */	
package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 	Cria objeto sem comportamento para ser transferido entre as camadas (tiers) remotas.
* 	Pattern Data Transfer Object.
* 	Objeto utilizado pelo cliente desktop de Chamada Biométrica.
*
* @author agostinho campos
*/

public class DiscenteDTO implements Serializable {
    
	private int idDiscente;
	private Long cpfcnpj;
	private Long matricula;
    private String nome;
    private List<byte[]> digitais = new ArrayList<byte[]>();
    private Date horaPresencaDigital;
    /**
     * Faltas ou presenças do aluno.
     *  
     * 0 = presença completa
     * 1 = 1 falta
     * 2 = 2 faltas
     */
    private short frequencia;
    
    /**
     * Como pode ter várias subturmas abertas para uma mesma aula,
     * é necessário informar qual a subturma do aluno para que seja
     * gerado o registro corretamente no banco
     */
    private int codigoTurmaOuSubturma;

    public int getCodigoTurmaOuSubturma() {
		return codigoTurmaOuSubturma;
	}
	public void setCodigoTurmaOuSubturma(int codigoTurmaOuSubturma) {
		this.codigoTurmaOuSubturma = codigoTurmaOuSubturma;
	}
	public Long getMatricula() {
        return matricula;
    }
    public void setMatricula(Long matricula) {
        this.matricula = matricula;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getHoraPresencaDigital() {
		return horaPresencaDigital;
	}
	public void setHoraPresencaDigital(Date horaPresencaDigital) {
		this.horaPresencaDigital = horaPresencaDigital;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((matricula == null) ? 0 : matricula.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
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
		DiscenteDTO other = (DiscenteDTO) obj;
		if (matricula == null) {
			if (other.matricula != null)
				return false;
		} else if (!matricula.equals(other.matricula))
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}
	public Long getCpf_cnpj() {
		return cpfcnpj;
	}
	public void setCpf_cnpj(Long cpfcnpj) {
		this.cpfcnpj = cpfcnpj;
	}
	public int getIdDiscente() {
		return idDiscente;
	}
	public void setIdDiscente(int idDiscente) {
		this.idDiscente = idDiscente;
	}
	public Long getCpfcnpj() {
		return cpfcnpj;
	}
	public void setCpfcnpj(Long cpfcnpj) {
		this.cpfcnpj = cpfcnpj;
	}
	public void setDigitais(List<byte[]> digitais) {
		this.digitais = digitais;
	}
	public List<byte[]> getDigitais() {
		return digitais;
	}
	public short getFrequencia() {
		return frequencia;
	}
	public void setFrequencia(short frequencia) {
		this.frequencia = frequencia;
	}
}
