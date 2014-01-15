/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 06/12/2012
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

/**
 * Entidade que armazena as telas de conexão entre os sistemas
 * integrados e os sistemas do governo.
 *
 * @author Tiago Hiroshi
 *
 */
@Entity
@Table(name = "registro_serpro_tela_conexao", schema="public")
public class RegistroSERPROTelaConexao implements PersistDB {

	@Id
	@Column(name = "id_tela_conexao", nullable = false)
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="registro_serpro_tela_conexao_seq") })
	private int id;

	/** Data de acesso da conexão. **/
	@CriadoEm
    @Column(name = "data_acesso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAcesso;

	/** Armazena o registro de conexão. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_registro_serpro_conexao")
	private RegistroSERPROConexao registroSERPROConexao;
	
	/** Armazena o registro de sincronização. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_registro_serpro_sincronizacao")
	private RegistroSERPROSincronizacao registroSERPROSincronizacao;
	
	/** Tela capturada dos sistemas do governo (SIAPE e SIAFI, por exemplo). **/
    @Column(name = "tela", nullable=false)
    private String tela;

    @Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public Date getDataAcesso() {
		return dataAcesso;
	}

	public void setDataAcesso(Date dataAcesso) {
		this.dataAcesso = dataAcesso;
	}

	public RegistroSERPROConexao getRegistroSERPROConexao() {
		return registroSERPROConexao;
	}

	public void setRegistroSERPROConexao(RegistroSERPROConexao registroSERPROConexao) {
		this.registroSERPROConexao = registroSERPROConexao;
	}

	public RegistroSERPROSincronizacao getRegistroSERPROSincronizacao() {
		return registroSERPROSincronizacao;
	}

	public void setRegistroSERPROSincronizacao(
			RegistroSERPROSincronizacao registroSERPROSincronizacao) {
		this.registroSERPROSincronizacao = registroSERPROSincronizacao;
	}

	public String getTela() {
		return tela;
	}

	public void setTela(String tela) {
		this.tela = tela;
	}

}
