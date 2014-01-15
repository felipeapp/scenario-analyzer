/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '25/10/2007'
 *
 */
package br.ufrn.sigaa.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;

/**
 * 
 * @author Victor Hugo
 * 
 */
@Entity
@Table(schema = "projetos", name = "elemento_despesa")
public class ElementoDespesa implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_elemento_despesa", nullable = false)
	private int id;

	@Column(name = "descricao", nullable = false)
	private String descricao;

	/**
	 * Indica se o orçamento que possuir esse elemento de despesa vai poder ter valores fracionados.
	 * 
	 * Por padrão apenas valores inteiros são permitidos. A princípio apenas diárias permitem valores fracionados.
	 */
	@Column(name = "permite_valor_fracionado", nullable = false)
	private boolean permiteValorFracionado = false;
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public boolean isPermiteValorFracionado() {
		return permiteValorFracionado;
	}

	public void setPermiteValorFracionado(boolean permiteValorFracionado) {
		this.permiteValorFracionado = permiteValorFracionado;
	}

	public ListaMensagens validate() {
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	@Override
	public int hashCode() {

		int result = 17;
    	result = 37*result + new Integer(this.getId()).hashCode();
        return  result;

	}

}