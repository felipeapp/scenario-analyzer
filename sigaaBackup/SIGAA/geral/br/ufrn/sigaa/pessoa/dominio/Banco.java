/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on 26/09/2004
 *
 */
package br.ufrn.sigaa.pessoa.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.UFRNUtils;

/**
 * Classe de domínio de um Banco
 * @author Ricardo Wendell
 */
@Entity
@Table(name = "banco", schema="comum")
public class Banco implements PersistDB {

	/** Constante com id do Banco do Brasil */
	public static final int BANCO_DO_BRASIL = 1;
	
	private int id;

	/** Denominação do banco */
	private String denominacao;

	/** Código do banco */
	private int codigo;

	/**
	 * Diz se o banco deve ser exibido para seleção nas páginas
	 */
	private Boolean publicado;
	
	private Boolean ativo;

	@Column(name = "codigo", unique = false, nullable = false, insertable = true, updatable = true)
	public int getCodigo() {
		return codigo;
	}

	@Transient
	public String getCodigoNome() {
		return UFRNUtils.completaZeros(codigo, 3) + " - " + denominacao;
	}

	/**
	 * @return Retorna denominacao.
	 */
	@Column(name = "denominacao", unique = false, nullable = false, insertable = true, updatable = true)
	public String getDenominacao() {
		return denominacao;
	}

	/**
	 * @return Retorna id.
	 */
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_banco", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return id;
	}

	@Column(name = "publicado", unique = false, nullable = true, insertable = true, updatable = true)
	public Boolean getPublicado() {
		return publicado;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	/**
	 * @param denominacao
	 *            The denominacao to set.
	 */
	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

	/**
	 * @param id
	 *            The id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}

	public void setPublicado(Boolean publicado) {
		this.publicado = publicado;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
}
