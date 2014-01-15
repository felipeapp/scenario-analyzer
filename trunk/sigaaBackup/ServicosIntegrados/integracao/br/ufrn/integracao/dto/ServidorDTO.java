package br.ufrn.integracao.dto;

import java.io.Serializable;
/**
 * Classe DTO que é responsável por transferir informações sobre os <br>
 * servidores para a aplicação de auto atendimento
 * 
 * @author Rafael Moreira
 *
 */
public class ServidorDTO implements Serializable{
	
	private static final long serialVersionUID = -1L;
	
	/** Identificador*/
	private int id;
	
	/** Pessoa a qual o servidor tem referência*/
	private PessoaDto pessoa;
	
	/** Matrícula siape do servidor*/
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
