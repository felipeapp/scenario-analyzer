/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Criado em: 17/08/2012
 * Autor:     R�mulo Augusto 
 *
 */
package br.ufrn.arq.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * <p>
 * Define qual o tipo de sincroniza��o est� sendo feita, por exemplo: f�rias, empenho, aus�ncias.
 * </p>
 * <p>
 * Cada tipo est� associado a um sistema.
 * </p>
 * 
 * @author R�mulo Augusto
 * @author Tiago Hiroshi
 */
@Entity
@Table(name = "tipo_registro_sincronizacao", schema = "public")
public class TipoRegistroSERPROSincronizacao implements PersistDB {
	
	/** Identificador */
	@Id
    @GeneratedValue(generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
    				  parameters = {@Parameter(name = "sequence_name", value = "tipo_registro_serpro_sincronizacao_seq")})
    @Column(name = "id_tipo_registro_serpro_sincronizacao", nullable = false)
    private int id;
	
	/** Nome do tipo de registro. Ex.: F�RIAS, EMPENHO */
	private String nome;
	
	/** Sistema que o tipo est� relacionado */
	@Column(name = "id_sistema")
	private int idSistema;
	
	public TipoRegistroSERPROSincronizacao() {
	}
	
	public TipoRegistroSERPROSincronizacao(int id) {
		this.id = id;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public int getIdSistema() {
		return idSistema;
	}
	
	public void setIdSistema(int idSistema) {
		this.idSistema = idSistema;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}
}
