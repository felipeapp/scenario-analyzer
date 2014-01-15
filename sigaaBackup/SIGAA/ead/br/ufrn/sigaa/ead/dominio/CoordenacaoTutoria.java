/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/06/25 - 13:53:46
 */
package br.ufrn.sigaa.ead.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Representa um coordenador de tutoria. 
 * 
 * @author David Pereira
 *
 */
@Entity
@Table(name="coordenacao_tutoria", schema="ead")
public class CoordenacaoTutoria implements PersistDB {

	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int id;
	
	@ManyToOne
	@JoinColumn(name="id_pessoa")
	/** Pessoa que é coordenadora de tutoria */
	private Pessoa pessoa;

	/** Se o coordenador está ativo ou não */
	private boolean ativo;

	@Column(name="data_inicio")
	/** Início da atividade de coordenação */
	private Date dataInicio;
	
	@Column(name="data_fim")
	/** Fim da atividade de coordenação */
	private Date dataFim;
	
	@ManyToOne
	@JoinColumn(name="id_usuario_cadastro")
	private Usuario usuarioCadastro;
	
	@ManyToOne
	@JoinColumn(name="id_usuario_inativacao")
	/** Usuário que inativou o coordenador */
	private Usuario usuarioInativacao;

	/**
	 * @return the ativo
	 */
	public boolean isAtivo() {
		return ativo;
	}

	/**
	 * @param ativo the ativo to set
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * @return the dataFim
	 */
	public Date getDataFim() {
		return dataFim;
	}

	/**
	 * @param dataFim the dataFim to set
	 */
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	/**
	 * @return the dataInicio
	 */
	public Date getDataInicio() {
		return dataInicio;
	}

	/**
	 * @param dataInicio the dataInicio to set
	 */
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
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

	/**
	 * @return the pessoa
	 */
	public Pessoa getPessoa() {
		return pessoa;
	}

	/**
	 * @param pessoa the pessoa to set
	 */
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	/**
	 * @return the usuarioCadastro
	 */
	public Usuario getUsuarioCadastro() {
		return usuarioCadastro;
	}

	/**
	 * @param usuarioCadastro the usuarioCadastro to set
	 */
	public void setUsuarioCadastro(Usuario usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	/**
	 * @return the usuarioInativacao
	 */
	public Usuario getUsuarioInativacao() {
		return usuarioInativacao;
	}

	/**
	 * @param usuarioInativacao the usuarioInativacao to set
	 */
	public void setUsuarioInativacao(Usuario usuarioInativacao) {
		this.usuarioInativacao = usuarioInativacao;
	}
	
	
	
}
