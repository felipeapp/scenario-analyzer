/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 11/12/2012
 * Autor:     Tiago Hiroshi 
 *
 */
package br.ufrn.arq.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;

/**
 * <p>
 * Registro de uma conexão para sincronização de informações dos sistemas integrados com os sistemas do governo (SIAPE e SIAFI, por exemplo)
 * </p>
 * <p>
 * Usado para armazenar as informações da conexão.
 * </p>
 * 
 * @author Tiago Hiroshi
 */
@Entity
@Table(name = "registro_serpro_conexao", schema = "public")
public class RegistroSERPROConexao implements PersistDB {

	/** Identificador */
	@Id
    @GeneratedValue(generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
    				  parameters = {@Parameter(name = "sequence_name", value = "registro_serpro_conexao_seq")})
    @Column(name = "id_registro_serpro_conexao", nullable = false)
    private int id;

	/** Data de acesso da conexão. **/
	@CriadoEm
    @Column(name = "data_acesso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAcesso;
	
	/** Usuário utilizado para o acesso aos sistemas do governo (SIAPE e SIAFI, por exemplo). **/
    @Column(name = "usuario", nullable=false)
    private String usuario;
	
	/** Armazena o registro de entrada da operação de sincronização. */
	@CriadoPor
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;

	/**
	 * Construtor padrão.
	 */
	public RegistroSERPROConexao() {}

	/**
	 * Construtor passando o id como parâmetro.
	 * @param id
	 */
	public RegistroSERPROConexao(int id) {
		this.id = id;
	}

	/**
	 * Construtor passando o usuário como parâmetro.
	 * @param usuario
	 */
	public RegistroSERPROConexao(String usuario) {
		this.usuario = usuario;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDataAcesso() {
		return dataAcesso;
	}

	public void setDataAcesso(Date dataAcesso) {
		this.dataAcesso = dataAcesso;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}
	
}
