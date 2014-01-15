/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;


import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Classe que representa os tipos de membros de um grupo de pesquisa
 * 
 * @author Ricardo
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "tipo_membro_grupo_pesquisa", schema = "pesquisa", uniqueConstraints = {})
public class TipoMembroGrupoPesquisa implements PersistDB {

	/** Tipo de Membro Grupo Pesquisa */
	public static final int PERMANENTE = 1;
	public static final int COLABORADOR = 2;
	public static final int ASSOCIADO = 3;
	public static final int INTERINO = 4;
	
	// Fields

	/** Chave prim�ria */
	private int id;

	/** Descri��o do tipo Membro Grupo Pesquisa */
	private String descricao;

	// Constructors

	/** default constructor */
	public TipoMembroGrupoPesquisa() {
	}

	/** minimal constructor */
	public TipoMembroGrupoPesquisa(int idTipoMembroGrupoPesquisa) {
		this.id = idTipoMembroGrupoPesquisa;
	}

	/** full constructor */
	public TipoMembroGrupoPesquisa(int idTipoMembroGrupoPesquisa,
			String descricao, Set<MembroGrupoPesquisa> equipeGrupoPesquisas) {
		this.id = idTipoMembroGrupoPesquisa;
		this.descricao = descricao;
	}

	/** Respons�vel por retornar a chave prim�ria */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_tipo_membro_grupo_pesquisa", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	/** Respons�vel por setar a chave prim�ria */
	public void setId(int idTipoMembroGrupoPesquisa) {
		this.id = idTipoMembroGrupoPesquisa;
	}

	/** Respons�vel por retornar a Descri��o */
	@Column(name = "descricao", unique = false, nullable = true, insertable = true, updatable = true)
	public String getDescricao() {
		return this.descricao;
	}

	/** Respons�vel por setar a Descri��o */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}
}