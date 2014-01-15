/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.comum.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.seguranca.log.CriadoEm;

/**
 * Entidade utilizada para registrar alterações feitas nos usuários dos sistemas.
 * 
 * @author David Pereira
 *
 */
@Entity @Table(name="usuario_alteracoes", schema="comum")
public class AlteracaoUsuario implements PersistDB {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="comum.usuario_alteracao_seq")})
	private int id;
	
	/** Usuário que teve seus dados alterados. */
	@Column(name="id_usuario")
	private Integer idUsuario;
	
	/** Usuário teve a unidade alterada. Essa coluna armazena a unidade que o usuário tinha antes da alteração. */
	@Column(name="id_unidade_antiga")
	private Integer idUnidadeAntiga;
	
	/** Usuário teve a unidade alterada. Essa coluna armazena a unidade que o usuário ficou depois da alteração. */
	@Column(name="id_unidade_nova")
	private Integer idUnidadeNova;

	/** Usuário que realizou as alterações. */
	@Column(name="id_usuario_alteracao")
	private Integer idUsuarioAlteracao;
	
	/** Data e hora em que as alterações foram realizadas. */
	@CriadoEm @Column(name="data_alteracao")
	private Date dataAlteracao;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Integer getIdUnidadeAntiga() {
		return idUnidadeAntiga;
	}

	public void setIdUnidadeAntiga(Integer idUnidadeAntiga) {
		this.idUnidadeAntiga = idUnidadeAntiga;
	}

	public Integer getIdUnidadeNova() {
		return idUnidadeNova;
	}

	public void setIdUnidadeNova(Integer idUnidadeNova) {
		this.idUnidadeNova = idUnidadeNova;
	}

	public Integer getIdUsuarioAlteracao() {
		return idUsuarioAlteracao;
	}

	public void setIdUsuarioAlteracao(Integer idUsuarioAlteracao) {
		this.idUsuarioAlteracao = idUsuarioAlteracao;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

}
