/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 29/01/2013
 *
 */
package br.ufrn.sigaa.nee.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;

/**
 * Classe que define os possíveis recursos nee.
 * @author Diego Jácome
 */
@Entity
@Table(name="tipo_recurso_nee", schema="nee")
public class TipoRecursoNee implements PersistDB{

	//Constantes
	/**
	 * POSSÍVEIS RECURSOS NEE
	 */
	/** Constante que define se o recurso é "Impressão do material em Braille". */
	public static final int MATERIAL_BRAILLE = 1;
	/** Constante que define se o recurso é "Fonte Ampliada". */
	public static final int FONTE_AMPLIADA = 2;
	/** Constante que define se o recurso é "Tradutor/Intérprete de LIBRAS". */
	public static final int TRADUTOR_LIBRAS = 3;
	/** Constante que define se o recurso é "Outros". */
	public static final int OUTROS = 4;

	/** Chave Primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_tipo_recurso_nee", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Descrição do Recurso. */
	private String descricao;
	
	/** Se a descrição precisa de complemento */
	@Column(name = "permite_complemento")
	private boolean permiteComplemento;

	/** Campo utilizado para verificar se este tipo de recurso já foi selecionado*/
	@Transient
	private boolean selecionado;
	
	/** Campo utilizado para guardar o valor do complemento */
	@Transient
	private String complemento;
	
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setPermiteComplemento(boolean permiteComplemento) {
		this.permiteComplemento = permiteComplemento;
	}

	public boolean isPermiteComplemento() {
		return permiteComplemento;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getComplemento() {
		return complemento;
	}
	
	public boolean isOutros () {
		return this.getId()==TipoRecursoNee.OUTROS ? true : false;
	}
	
	/**
	 * Implementação do método equals comparando-se os ids dos {@link TipoRecursoNee}.
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}
}
