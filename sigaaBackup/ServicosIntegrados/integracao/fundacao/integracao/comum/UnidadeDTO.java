package fundacao.integracao.comum;

import java.io.Serializable;

/**
 * DTO de integração com as Unidades Organizacionais do SIPAC
 * 
 * @author Gleydson
 * 
 */
public class UnidadeDTO implements Serializable{

	private int id;

	private String codigo;

	private String nome;

	private String municipio;

	private int responsavel;

	private int gestora;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public int getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(int responsavel) {
		this.responsavel = responsavel;
	}

	public int getGestora() {
		return gestora;
	}

	public void setGestora(int gestora) {
		this.gestora = gestora;
	}

}
