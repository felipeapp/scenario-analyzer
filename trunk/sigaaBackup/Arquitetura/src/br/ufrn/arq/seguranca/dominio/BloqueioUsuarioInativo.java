/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 19/07/2010
 */
package br.ufrn.arq.seguranca.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Classe que representa o bloqueio de um usuário devido à inatividade
 * nos sistemas, ou seja, por ele não ter entrado nos sistemas por um
 * período de três meses, por exemplo. 
 * 
 * Após bloqueado, um usuário deverá passar por um desbloqueio para
 * poder entrar novamente, no qual ele irá confirmar os seus dados
 * e o bloqueio será inativado.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@Entity @Table(name="bloqueio_usuario_inativo", schema="comum")
public class BloqueioUsuarioInativo implements PersistDB {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="comum.bloqueio_usuario_inativo_seq") })
	private int id;
	

	/**
	 * Usuário que está bloqueado devido à inatividade
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_usuario")
	private UsuarioGeral usuario;
	
	/**
	 * Data em que o bloqueio foi efetuado.
	 */
	@Column(name = "data_bloqueio")
	private Date dataBloqueio;
	
	/**
	 * Data em que o usuário desbloqueou a sua conta
	 */
	@Column(name = "data_desbloqueio")
	private Date dataDesbloqueio;
	
	/**
	 * Se o bloqueio está ativo ou não. Quando o bloqueio não está ativo
	 * significa que o usuário realizou o desbloqueio de sua conta.
	 */
	private boolean ativo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UsuarioGeral getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioGeral usuario) {
		this.usuario = usuario;
	}

	public Date getDataBloqueio() {
		return dataBloqueio;
	}

	public void setDataBloqueio(Date dataBloqueio) {
		this.dataBloqueio = dataBloqueio;
	}

	public Date getDataDesbloqueio() {
		return dataDesbloqueio;
	}

	public void setDataDesbloqueio(Date dataDesbloqueio) {
		this.dataDesbloqueio = dataDesbloqueio;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
}
