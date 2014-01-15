/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 23/04/2012
 */
package fundacao.integracao.projetos;

import java.io.Serializable;
import java.util.List;


/**
 * Data Transfer Object para informações da meta do projeto.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class MetaDTO implements Serializable {
	
	/** Meta.*/
	private String meta;

	/** Descrição da meta.*/
	private String descricao;

	/** Etapas associadas a meta.*/
	private List<EtapaDTO> etapas;

	public void setMeta(String meta) {
		this.meta = meta;
	}

	public String getMeta() {
		return meta;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setEtapas(List<EtapaDTO> etapas) {
		this.etapas = etapas;
	}

	public List<EtapaDTO> getEtapas() {
		return etapas;
	}

}
