/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 17/08/2012
 * Autor:     Rômulo Augusto 
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

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.seguranca.log.CriadoEm;

/**
 * <p>
 * Registro de uma sincronização de informações dos sistemas integrados com os sistemas do governo (SIAPE e SIAFI, por exemplo)
 * </p>
 * <p>
 * Usado para armazenar sincronizações com falha e com sucesso.
 * </p>
 * 
 * @author Rômulo Augusto
 * @author Tiago Hiroshi
 */
@Entity
@Table(name = "registro_serpro_sincronizacao", schema = "public")
public class RegistroSERPROSincronizacao implements PersistDB {
	
	/** Identificador */
	@Id
    @GeneratedValue(generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
    				  parameters = {@Parameter(name = "sequence_name", value = "registro_serpro_sincronizacao_seq")})
    @Column(name = "id_registro_serpro_sincronizacao", nullable = false)
    private int id;
	
	/** "Qualified name" da classe da entidade que está sendo sincronizada. */
	@Column(name = "classe_entidade_sincronizada")
	private String classeEntidadeSincronizada;
	
	/** Identificador do registro da entidade que está sendo sincronizada. */
	@Column(name = "id_entidade_sincronizada")
	private int idEntidadeSincronizada;
	
	/** 
	 * Armazena o erro gerado nos casos de falha de sincronização. De preferências, deve-se guardar o stacktrace
	 * @see ExceptionUtils#getStackTrace(Throwable) 
	 */
	@Column(name = "excecao_falha_sincronizacao")
	private String excecaoFalhaSincronizacao;
	
	/**
	 * Armazena a mensagem de erro gerada no processo de sincronização. A mensagem pode ser de negócio ou não.
	 * Ela se diferencia da exceção porque é uma informação de fácil entendimento.
	 */
	@Column(name = "mensagem_erro")
	private String mensagemErro;
	
	/** Data de cadastro do registro. **/
	@CriadoEm
    @Column(name = "data_cadastro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro;
	
	/** Campo usado para armazenar alguma observação que se julgue necessária sobre a sincronização do registro. */
	private String observacao;
	
	/** Indica se a sincronização ocorreu com sucesso. */
	private boolean sucesso;
	
	/** Tipo do registro de sincronização. Define o que se está sincronizando. Ex.: FÉRIAS, EMPENHO */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_tipo_registro_sincronizacao")
	private TipoRegistroSERPROSincronizacao tipoRegistroSERPROSincronizacao;
	
	/** Armazena o registro de conexão com os sistemas do governo. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_registro_conexao")
	private RegistroSERPROConexao registroSERPROConexao;

	/**
	 * Construtor.
	 * @param id
	 */
	public RegistroSERPROSincronizacao() {}

	/**
	 * Construtor passando o id.
	 * @param id
	 */
	public RegistroSERPROSincronizacao(int id) {
		this.id = id;
	}

	/**
	 * Cria um novo registro de sincronização com dados básicos, por exemplo: ID da entidade, classe da entidade.
	 * @param entidadeSincronizada
	 * @return
	 */
	public static RegistroSERPROSincronizacao criarRegistroSERPROSincronizacaoBase(PersistDB entidadeSincronizada) {
		RegistroSERPROSincronizacao registroSERPROSincronizacao = new RegistroSERPROSincronizacao();
		registroSERPROSincronizacao.setClasseEntidadeSincronizada(entidadeSincronizada.getClass().getName());
		registroSERPROSincronizacao.setIdEntidadeSincronizada(entidadeSincronizada.getId());
		
		return registroSERPROSincronizacao;
	}
	
	/**
	 * Define a informação de sincronizado com <strong>sucesso</strong> no registro. 
	 */
	public void sincronizadoComSucesso() {
		this.sucesso = true;
	}
	
	/**
	 * Define a informação de sincronizado com <strong>falha</strong> no registro.
	 * @param e
	 */
	public void sincronizadoComFalha(Throwable e) {
		this.sucesso = false;
		this.excecaoFalhaSincronizacao = ExceptionUtils.getStackTrace(e);
		this.mensagemErro = e.getMessage();
	}
	
	public String getClasseEntidadeSincronizada() {
		return classeEntidadeSincronizada;
	}
	
	public void setClasseEntidadeSincronizada(String classeEntidadeSincronizada) {
		this.classeEntidadeSincronizada = classeEntidadeSincronizada;
	}
	
	public int getIdEntidadeSincronizada() {
		return idEntidadeSincronizada;
	}
	
	public void setIdEntidadeSincronizada(int idEntidadeSincronizada) {
		this.idEntidadeSincronizada = idEntidadeSincronizada;
	}
	
	public String getExcecaoFalhaSincronizacao() {
		return excecaoFalhaSincronizacao;
	}
	
	public void setExcecaoFalhaSincronizacao(String erro) {
		this.excecaoFalhaSincronizacao = erro;
	}
	
	public String getMensagemErro() {
		return mensagemErro;
	}
	
	public void setMensagemErro(String mensagemErroNegocio) {
		this.mensagemErro = mensagemErroNegocio;
	}
	
	public String getObservacao() {
		return observacao;
	}
	
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	public boolean isSucesso() {
		return sucesso;
	}
	
	public void setSucesso(boolean sucesso) {
		this.sucesso = sucesso;
	}
	
	public TipoRegistroSERPROSincronizacao getTipoRegistroSERPROSincronizacao() {
		return tipoRegistroSERPROSincronizacao;
	}
	
	public void setTipoRegistroSERPROSincronizacao(TipoRegistroSERPROSincronizacao tipoRegistroSERPROSincronizacao) {
		this.tipoRegistroSERPROSincronizacao = tipoRegistroSERPROSincronizacao;
	}
	
	public Date getDataCadastro() {
		return dataCadastro;
	}
	
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroSERPROConexao getRegistroSERPROConexao() {
		return registroSERPROConexao;
	}

	public void setRegistroSERPROConexao(RegistroSERPROConexao registroSERPROConexao) {
		this.registroSERPROConexao = registroSERPROConexao;
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
