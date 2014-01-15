
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
 * Item para a avalia��o do discente de Ensino a dist�ncia
 * 
 * @author David Pereira
 *
 */
@Entity
@Table(schema="ead", name="item_avaliacao_ead")
public class ItemAvaliacaoEad implements Validatable {

	/** Chave prim�ria */
	@Id
	@Column(name = "id", unique = true, nullable = false, insertable = true, updatable = true)
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	private int id;
	
	/** Nome do item */
	private String nome;
	
	/** Se o item est� ativo ou n�o */
	private boolean ativo;
	
	/** Cada avalia��o segue sua metodologia de avalia��o. Identifica qual a metodologia que possui esse item de avalia��o. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_metodologia")
	private MetodologiaAvaliacao metodologia;

	/** Usu�rio que efetuou o cadastro do item */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_usuario")
	private Usuario usuario;
	
	/** Data de cadastro do Item de Avalia��o */
	@Column(name="data_cadastro")
	private Date data;

	/** Notas do item de Avalia��o */
	@Transient
	private List<NotaItemAvaliacao> notas;

	/**
	 * Informa de o item est� ativo ou n�o
	 */
	public boolean isAtivo() {
		return ativo;
	}

	/**
	 * Seta a informa��o ativo do item de Avalia��o
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
	 * Retorna a chave prim�ria
	 */
	public int getId() {
		return id;
	}

	/**
	 * Seta a chave prim�ria
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
	 * Retorna o usu�rio
	 */
	public Usuario getUsuario() {
		return usuario;
	}

	/**
	 * Seta o usu�rio
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
	 * Seta as notas do Item da Avalia��o
	 */
	public void setNotas(List<NotaItemAvaliacao> notas) {
		this.notas = notas;
	}

	/** 
	 * Valida o nome do item de Avalia��o.
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		if (this.getNome() == null || "".equals(this.getNome().trim()))
			lista.addErro("O campo nome � obrigat�rio!");
		
		return lista;
	}

	/** Retorna o c�digo hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, nome);
	}

	/** 
	 * Compara este objeto ao passado por par�metro, comparando o atributo "id" e "nome".
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "nome");
	}

	/**	
	 * Retorna a metodologia do Item de Avalia��o
	 */
	public MetodologiaAvaliacao getMetodologia() {
		return metodologia;
	}

	/**
	 * Seta a Metodologia do Item de Avalia��o
	 */
	public void setMetodologia(MetodologiaAvaliacao metogologia) {
		this.metodologia = metogologia;
	}
	
}
