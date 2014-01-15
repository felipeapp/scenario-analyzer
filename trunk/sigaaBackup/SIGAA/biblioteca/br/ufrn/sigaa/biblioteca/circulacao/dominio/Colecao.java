/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 01/08/2008
 */
package br.ufrn.sigaa.biblioteca.circulacao.dominio;

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

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;

/**
 * 
 * Cole��o a qual pertencer�o os materiais informacionais das bibliotecas do sistema (Acervo Circulante, Cole��o em Braille, 
 * Disserta��es da UFRN, Folhetos, entre outras...)
 * 
 * @author Fred
 * 
 */
@Entity
@Table(name = "colecao", schema = "biblioteca")
public class Colecao implements Validatable{
	
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column(name = "id_colecao")
	private int id;

	/**O c�digo da cole��o*/
	String codigo;
	
	/**A descri��o da cole��o */
	@Column(nullable=false)
	private String descricao;
	
	
	/** Indica se a cole��o est� ativa*/
	private boolean ativo = true;
	
	/** Indica se a cole��o � utilizada para o registro de movimenta��o de materiais. */
	@Column(name="contagem_movimentacao")
	private boolean contagemMovimentacao = false;
	
	
	//////////////////////  dados para auditoria  ////////////////////
	
	/**
	 * Registro entrada do usu�rio que cadastrou
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;
	
	/**
	 * Data de cadastro
	 */
	@CriadoEm
	@Column(name="data_cadastro")
	private Date dataCadastro;

	/**
	 * Registro de entrada do usu�rio que realizou a �ltima atualiza��o
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/**
	 * Data da �ltima atualiza��o
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;

	//////////////////////////////////////////////////////////////////
	
	
	/** Id da cole��o a qual esta pertence, para as contagens de movimenta��o di�ria. */
	@Column(name="id_colecao_pai")
	private int idColecaoPai;
	
	
	public Colecao() { }
	
	
	public Colecao(int id) {
		this.id = id;
	}

	
	public Colecao(String descricao) {
		this.descricao = descricao;
	}
	
	public Colecao(int id, String descricao) {
		this(id);
		this.descricao = descricao;
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(new Integer(id));
	}
	

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	
	/**
	 * Validar se as vari�veis est�o preenchidas corretamente
	 */
	public ListaMensagens validate() {
		
		ListaMensagens mensagens = new ListaMensagens();

		if(StringUtils.isEmpty(codigo))
			mensagens.addErro("� preciso informar o c�digo da cole��o");
		
		if(StringUtils.isEmpty(descricao))
			mensagens.addErro("� preciso informar a descri��o da cole��o");
		
		return mensagens;
	}
	
	
	/**
	 * Mostra o c�digo mais a descri��o para o usu�rio
	 */
	public String getDescricaoCompleta(){
		return (codigo != null ? codigo +" - ": "") + descricao;
	}
	
	
	
	// Gets e Sets
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}	
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}


	public String getCodigo() {
		return codigo;
	}


	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	public boolean isContagemMovimentacao() {
		return contagemMovimentacao;
	}

	public void setContagemMovimentacao(boolean contagemMovimentacao) {
		this.contagemMovimentacao = contagemMovimentacao;
	}

	public int getIdColecaoPai() {
		return idColecaoPai;
	}

	public void setIdColecaoPai(int idColecaoPai) {
		this.idColecaoPai = idColecaoPai;
	}
	
	
}
