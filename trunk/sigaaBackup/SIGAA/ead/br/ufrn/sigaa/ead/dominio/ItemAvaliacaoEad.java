
package br.ufrn.sigaa.ead.dominio;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Item para a avaliação do discente de Ensino a distância
 * 
 * @author David Pereira
 *
 */
@Entity
@Table(schema="ead", name="item_avaliacao_ead")
public class ItemAvaliacaoEad implements Validatable {

	/** Chave primária */
	@Id
	@Column(name = "id", unique = true, nullable = false, insertable = true, updatable = true)
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	private int id;
	
	/** Nome do item */
	private String nome;
	
	/** Se o item está ativo ou não */
	private boolean ativo;
	
	/** Cada avaliação segue sua metodologia de avaliação. Identifica qual a metodologia que possui esse item de avaliação. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_metodologia")
	private MetodologiaAvaliacao metodologia;

	/** Usuário que efetuou o cadastro do item */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_usuario")
	private Usuario usuario;
	
	/** Data de cadastro do Item de Avaliação */
	@Column(name="data_cadastro")
	private Date data;

	/** Notas do item de Avaliação */
	@Transient
	private List<NotaItemAvaliacao> notas;

	/**
	 * Informa de o item está ativo ou não
	 */
	public boolean isAtivo() {
		return ativo;
	}

	/**
	 * Seta a informação ativo do item de Avaliação
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * Retorna a data de cadastro
	 */
	public Date getData() {
		return data;
	}

	/**
	 * Seta a data de cadastro
	 */
	public void setData(Date data) {
		this.data = data;
	}

	/**
	 * Retorna a chave primária
	 */
	public int getId() {
		return id;
	}

	/**
	 * Seta a chave primária
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Retorna o nome do item
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * Seta o nome do item
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * Retorna o usuário
	 */
	public Usuario getUsuario() {
		return usuario;
	}

	/**
	 * Seta o usuário
	 */
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	/**
	 * Retornar as notas do Item
	 */
	public List<NotaItemAvaliacao> getNotas() {
		return notas;
	}

	/**
	 * Seta as notas do Item da Avaliação
	 */
	public void setNotas(List<NotaItemAvaliacao> notas) {
		this.notas = notas;
	}

	/** 
	 * Valida o nome do item de Avaliação.
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		if (this.getNome() == null || "".equals(this.getNome().trim()))
			lista.addErro("O campo nome é obrigatório!");
		
		return lista;
	}

	/** Retorna o código hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, nome);
	}

	/** 
	 * Compara este objeto ao passado por parâmetro, comparando o atributo "id" e "nome".
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "nome");
	}

	/**	
	 * Retorna a metodologia do Item de Avaliação
	 */
	public MetodologiaAvaliacao getMetodologia() {
		return metodologia;
	}

	/**
	 * Seta a Metodologia do Item de Avaliação
	 */
	public void setMetodologia(MetodologiaAvaliacao metogologia) {
		this.metodologia = metogologia;
	}
	
}
