/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 09/05/2008
 *
 */

package br.ufrn.sigaa.pesquisa.dominio;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Classe com as categorias de projetos de pesquisa
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name="categoria_projeto_pesquisa", schema = "pesquisa", uniqueConstraints = {})
public class CategoriaProjetoPesquisa implements Validatable {
	
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int id;
	
	private String denominacao;
	
	private boolean ativo = true;
	
	private Integer ordem;
	
	public CategoriaProjetoPesquisa() {
		
	}
	
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(denominacao, "Denominação", lista);
		ValidatorUtil.validaInt(ordem, "Ordem", lista);
		return lista;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

}