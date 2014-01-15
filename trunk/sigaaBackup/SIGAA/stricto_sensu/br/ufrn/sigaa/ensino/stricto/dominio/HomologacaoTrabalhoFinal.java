/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 22/02/2008
 */
package br.ufrn.sigaa.ensino.stricto.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Entidade para registrar as informações de uma homologação
 * de dissertação ou tese. 
 * @author David Pereira
 */
@Entity @Table(name="homologacao_trabalho_final", schema="stricto_sensu")
public class HomologacaoTrabalhoFinal implements PersistDB {

	@Id @GeneratedValue
	private int id;
	
	/** 
	 * Banca de defesa associada ao trabalho final. Os dados 
	 * do trabalho final estão nessa entidade.
	 */
	@ManyToOne @JoinColumn(name="id_banca")
	private BancaPos banca;
	
	/** Número do processo de homologação no protocolo */
	@Column(name="num_processo")
	private int numProcesso;
	
	/** Ano do processo de homologação no protocolo */
	@Column(name="ano_processo")
	private int anoProcesso;
	
	/** usuário que cadastrou */
	@ManyToOne @JoinColumn(name="id_usuario")
	@CriadoPor
	private Usuario criadoPor;
	
	/** data de cadastro */
	@Column(name="criado_em")
	@CriadoEm
	private Date criadoEm;

	/**
	 * Registro de Entrada do usuário que modificou a requisicao de diploma
	 */
	@ManyToOne
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/**
	 * Data de alteração do currículo.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_alteracao")
	@AtualizadoEm
	private Date dataAlteracao;	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BancaPos getBanca() {
		return banca;
	}

	public void setBanca(BancaPos banca) {
		this.banca = banca;
	}

	public int getNumProcesso() {
		return numProcesso;
	}

	public void setNumProcesso(int numProcesso) {
		this.numProcesso = numProcesso;
	}

	public int getAnoProcesso() {
		return anoProcesso;
	}

	public void setAnoProcesso(int anoProcesso) {
		this.anoProcesso = anoProcesso;
	}

	public Usuario getCriadoPor() {
		return criadoPor;
	}

	public void setCriadoPor(Usuario criadoPor) {
		this.criadoPor = criadoPor;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

}
