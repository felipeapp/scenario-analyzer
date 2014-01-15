/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 12/12/2008
 */
package br.ufrn.arq.erros.gerencia;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Retrata a ocorrência de um erro. Um erro (NullPointer em uma linha, por exemplo) pode 
 * acontecer com diversos usuários. Isso significa que é um único erro com várias ocorências.
 * 
 * @author Gleydson Lima
 * @author David Pereira
 *
 */
@Entity
@Table(name="erro_ocorrencia", schema="infra")
public class ErroOcorrencia implements PersistDB{

	@Id
	@Column(name="id_erro_ocorrencia")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;

	private Date data;

	@Column(name="id_usuario")
	private Integer idUsuario;
	//usar na jsp a classe usuarioDAO de comum - o método findUsuarioLeve (usuário já fica na sessão)
	
	@Column(name="id_registro_entrada")
	private Integer idRegistroEntrada;

	@ManyToOne
	@JoinColumn(name="id_erro")
	private Erro erro;

	@Transient
	private UsuarioGeral usuario = new UsuarioGeral();
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Integer getIdRegistroEntrada() {
		return idRegistroEntrada;
	}

	public void setIdRegistroEntrada(Integer idRegistroEntrada) {
		this.idRegistroEntrada = idRegistroEntrada;
	}

	public Erro getErro() {
		return erro;
	}

	public void setErro(Erro erro) {
		this.erro = erro;
	}

	public UsuarioGeral getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioGeral usuario) {
		this.usuario = usuario;
	}

}
