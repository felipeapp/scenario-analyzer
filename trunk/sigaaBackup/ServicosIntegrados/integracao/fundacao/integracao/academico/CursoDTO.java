package fundacao.integracao.academico;

import java.io.Serializable;

import fundacao.integracao.comum.UnidadeDTO;

/**
 * DTO de Integração com o sistema da fundação na área acadêmica.
 * 
 * @author Gleydson Lima
 *
 */
public class CursoDTO implements Serializable{

	private int id;

	private String nome;

	private String nivel;

	private String municipio;

	private UnidadeDTO unidade;

	private String modalidade;

	private String grauAcademico;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	public UnidadeDTO getUnidade() {
		return unidade;
	}

	public void setUnidade(UnidadeDTO unidade) {
		this.unidade = unidade;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public String getModalidade() {
		return modalidade;
	}

	public void setModalidade(String modalidade) {
		this.modalidade = modalidade;
	}

	public String getGrauAcademico() {
		return grauAcademico;
	}

	public void setGrauAcademico(String grauAcademico) {
		this.grauAcademico = grauAcademico;
	}

}