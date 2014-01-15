/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '22/02/2007'
 *
 */
package br.ufrn.sigaa.prodocente.producao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * @author wendell
 *
 */
@Deprecated
@Entity
@Table(name = "tipo_apresentacao", schema = "prodocente")
public class TipoApresentacao implements Validatable{
	
	public final static TipoApresentacao EXPOSICAO = new TipoApresentacao(1);
	
	public final static TipoApresentacao MONTAGEM = new TipoApresentacao(2);  
	
	public final static TipoApresentacao PROGRAMACAO = new TipoApresentacao(3);
	
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_tipo_apresentacao", nullable = false)
	private int id;
	@Column(name="descricao")
	private String descricao;

	public TipoApresentacao(int id) {
		this.id=id;
	}
	public TipoApresentacao() {
		
	}
	/**
	 * @return the descricao
	 */
	public String getDescricao() {
		return descricao;
	}

	/**
	 * @param descricao the descricao to set
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getDescricao(),"Descrição", lista.getMensagens());

		return lista;
	}
	
	
}
