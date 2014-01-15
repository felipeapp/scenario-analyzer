/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 09/05/2012
 */
package fundacao.integracao.projetos;

import java.io.Serializable;


/**
 * Data Transfer Object para informações do unidade interesssada no projeto.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class UnidadeInteressadaDTO implements Serializable {
	
	/** Representa o identificador da uniade interssada.*/
	private Integer idUnidade;

	/** Representa o nome da uniade interssada.*/
	private String nome;

	/** Representa o código da uniade interssada.*/
	private Long codigo;

	/** Representa o identificador do tipo da unidade. */
	private Integer idTipoUnidadeAcademica;

	/** Representa o tipo da unidade. */
	private String tipoUnidadeAcademica;

	public void setIdUnidade(Integer idUnidade) {
		this.idUnidade = idUnidade;
	}

	public Integer getIdUnidade() {
		return idUnidade;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setIdTipoUnidadeAcademica(Integer idTipoUnidadeAcademica) {
		this.idTipoUnidadeAcademica = idTipoUnidadeAcademica;
	}

	public Integer getIdTipoUnidadeAcademica() {
		return idTipoUnidadeAcademica;
	}

	public void setTipoUnidadeAcademica(String tipoUnidadeAcademica) {
		this.tipoUnidadeAcademica = tipoUnidadeAcademica;
	}

	public String getTipoUnidadeAcademica() {
		return tipoUnidadeAcademica;
	}

}
