/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;

import java.io.Serializable;

/**
 * DTO que contém os dados específicos do item orçado.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class OrcamentoItemDTO implements Serializable {
	
	/** Descrição da planilha orçamentária. */
	private String descricao;

	/** Membro da planilha orçamentária. */
	private String membro;

	/** Membro da planilha orçamentária. */
	private Long membroCPF;

	/** Representa a observação.*/
	private String observacao;

	/** Valor destinado à rubrica */
	private Double valor;

	/** Identificador do grupo da planilha orçamentária. */
	private Integer idGrupo;

	/** Identificador do grupo da planilha orçamentária. */
	private Long codigoGrupo;
	
	/** Grupo da despesa */
	private String grupo;
	
	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public String getMembro() {
		return membro;
	}

	public void setMembro(String membro) {
		this.membro = membro;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setIdGrupo(Integer idGrupo) {
		this.idGrupo = idGrupo;
	}

	public Integer getIdGrupo() {
		return idGrupo;
	}

	public void setCodigoGrupo(Long codigoGrupo) {
		this.codigoGrupo = codigoGrupo;
	}

	public Long getCodigoGrupo() {
		return codigoGrupo;
	}

	public void setMembroCPF(Long membroCPF) {
		this.membroCPF = membroCPF;
	}

	public Long getMembroCPF() {
		return membroCPF;
	}
}
