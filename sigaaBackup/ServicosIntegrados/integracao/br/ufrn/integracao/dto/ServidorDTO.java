package br.ufrn.integracao.dto;

import java.io.Serializable;
/**
 * Classe DTO que � respons�vel por transferir informa��es sobre os <br>
 * servidores para a aplica��o de auto atendimento
 * 
 * @author Rafael Moreira
 *
 */
public class ServidorDTO implements Serializable{
	
	private static final long serialVersionUID = -1L;
	
	/** Identificador*/
	private int id;
	
	/** Pessoa a qual o servidor tem refer�ncia*/
	private PessoaDto pessoa;
	
	/** Matr�cula siape do servidor*/
	private int siape;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PessoaDto getPessoa() {
		return pessoa;
	}

	public void setPessoa(PessoaDto pessoa) {
		this.pessoa = pessoa;
	}

	public int getSiape() {
		return siape;
	}

	public void setSiape(int siape) {
		this.siape = siape;
	}
	
	
}
