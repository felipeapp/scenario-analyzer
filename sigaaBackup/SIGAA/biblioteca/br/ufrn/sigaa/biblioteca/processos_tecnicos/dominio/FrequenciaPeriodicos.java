/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 04/01/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.StringUtils;

/**
 *
 * <p> Classe que guarda e configura os tempo de expiração dos periódicos do sistema. </p>
 *
 * <p> <i> Esse dado vai estar associado a uma assinatura e vai ser utilizado  para saber <br/> 
 * se o periódico está <strong>corrente</strong> ou <strong>não corrente</strong>.  <br/>
 * <strong>Corrente</strong> são aqueles periódicos que continua chegando na biblioteca e <strong>não corrente</strong> são aqueles periódico <br/>
 * estão no acervo mas não estão chegando mais na biblioteca. <br/> </i> 
 * </p>
 * 
 * <p> <i> Como os periódicos têm prerioticidade iregulares, semanal, anual, etc...  O tempo para que eles sejam  <br/>
 * considerados não correntes também é iregular e é configurado a partir dessa classe denominado de <code>tempoExpiracao</code>. </i> </p>
 * @author jadson
 *
 */
@Entity
@Table(name = "frequencia_periodicos", schema = "biblioteca")
public class FrequenciaPeriodicos implements Validatable{

	/** Unidade de expiração informada pelo usuário. O usuário vai informar em meses ou anos, mas o sistema sempre vai salvar 
	 * o periódo em meses para facilitar o calculo na consulta do relatório */
	public enum UnidadeTempoExpiracao{ MESES, ANOS}
	
	/** O id */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.resgistro_extras_sequence") })	
	@Column(name="id_frequencia_periodicos")
	private int id;
	
	/** A descrição da frequência, normalmente contém os valores possíveis na possição 18 do campo de controle 008. Exemplo:
	 *  <ul>
	 *  	<li> <strong> Diária</strong> </li>
	 *  	<li> <strong> Semanal</strong> </li>
	 *  	<li> <strong> Semestral</strong> </li>
	 *  	<li> <strong> Anual</strong> </li>
	 *  </ul>
	 */
	@Column(name = "descricao", nullable=false)
	private String descricao;
	
	
	/** O tempo EM MESES que a assinatura que tiver essa fequência será considerada não corrente. Exemplo: 
	 * <ul>
	 *  	<li> <strong> Diária:</strong> Expira <strong>4</strong> meses após a data do último cadastro no acervo </li>
	 *  	<li> <strong> Semestral:</strong> Expira <strong>24</strong> meses após a data do último cadastro no acervo </li>
	 *  	<li> <strong> Anual:</strong> Expira <strong>48</strong> meses após a data do último cadastro no acervo </li>
	 *  	<li> <strong> Bienal:</strong> Expira <strong>96</strong> meses após a data do último cadastro no acervo </li>
	 *  </ul>
	 */
	@Column(name = "tempo_expiracao", nullable=false)
	private short tempoExpiracao;
	
	
	/** Guarda a unidade que o usuário informato para o tempo de expiração para poder converter, já que o sistema 
	 * sempre guarda o tempo de expiração em meses
	 */
	@Enumerated(EnumType.ORDINAL) // O valor salvo no banco vai ser a ordem em que as variáveis são declaradas
	@Column(name="unidade_tempo_espiracao", nullable=false)
	private UnidadeTempoExpiracao unidadeTempoExpiracao = UnidadeTempoExpiracao.MESES;
	
	/** Um frequência desativa foi removida do sistema, não podendo ter nenhuma assinatura associada a ela */
	@Column(name = "ativo", nullable=false)
	private boolean ativo = true;
	
	
	////////////////////////////INFORMAÇÕES DE AUDITORIA  ///////////////////////////////////////


	/**
	 * informações da autoria da criação dessa entidade.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_criacao")
	@CriadoPor
	private RegistroEntrada registroCriacao;

	/**
	 * data de cadastro
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	@Column(name="data_criacao")
	private Date dataCriacao;

	/**
	 * registro entrada  do usuário que realizou a última atualização
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroUltimaAtualizacao;

	/**
	 * data da última atualização
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_ultima_atualizacao")
	@AtualizadoEm
	private Date dataUltimaAtualizacao;


	//////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	/** O tempo de expiração informado pelo usuário.  Para facilitar o preenchimento o usuário pode preencher 
	 * em meses ou anos, mas o sistema vai converter e salvar sempre em meses*/
	@Transient
	private short tempoExpiracaoInformadoUsuario;

	/** construtor objeto vazio */
	public FrequenciaPeriodicos() {}
	
	/** construtor objeto persistido */
	public FrequenciaPeriodicos(int id) {
		this.id = id;
	}
	
	/** construtor objeto persistido para mostrar ao usuário */
	public FrequenciaPeriodicos(int id, String descricao) {
		this(id);
		this.descricao = descricao;
	}

	/** construtor de uma novo objeto a ser persistido */
	public FrequenciaPeriodicos(int id, String descricao, short tempoExpiracao, UnidadeTempoExpiracao unidadeTempoExpiracao) {
		this(id, descricao);
		this.tempoExpiracao = tempoExpiracao;
		this.unidadeTempoExpiracao = unidadeTempoExpiracao;
	}

	/**
	 * Configura o valor da uniadade de acordo com o valor selecionado pelo usuário.
	 * 
	 * @param valorUnidadeTempoExpiracao
	 */
	public void configuraUnidadeTempoExpriracao(int valorUnidadeTempoExpiracao) {
		if(valorUnidadeTempoExpiracao == UnidadeTempoExpiracao.MESES.ordinal())
			this.setUnidadeTempoExpiracao( UnidadeTempoExpiracao.MESES);
		
		if(valorUnidadeTempoExpiracao == UnidadeTempoExpiracao.ANOS.ordinal())
			this.setUnidadeTempoExpiracao( UnidadeTempoExpiracao.ANOS);
		
	}
	
	/**
	 * Retorna o valor da uniade de tempo de expiracao
	 * 
	 * @param valorUnidadeTempoExpiracao
	 */
	public int getValorUnidadeTempoExpiracao() {
		if(this.unidadeTempoExpiracao == UnidadeTempoExpiracao.MESES)
			return UnidadeTempoExpiracao.MESES.ordinal();
		
		if(this.unidadeTempoExpiracao == UnidadeTempoExpiracao.ANOS)
			return UnidadeTempoExpiracao.ANOS.ordinal();
		
		return UnidadeTempoExpiracao.MESES.ordinal();
	}
	
	
	/** Converte o tempo de expiração informato pelo usuário que pode ter sido em ANOS para MESES */
	public void converteTempoExpiracaoInformatoUsuario(){
		if ( this.unidadeTempoExpiracao == UnidadeTempoExpiracao.ANOS)
			this.tempoExpiracao =  new Integer(tempoExpiracaoInformadoUsuario * 12).shortValue();
		else
			this.tempoExpiracao = tempoExpiracaoInformadoUsuario;
	}
	
	
	/** Converte o tempo de expiração salvo no banco para o tempo informato pelo usuário que pode ter sido em ANOS. */
	public void converteTempoExpiracaoSalvo(){
		if ( this.unidadeTempoExpiracao == UnidadeTempoExpiracao.ANOS)
			this.tempoExpiracaoInformadoUsuario =  new Integer(tempoExpiracao / 12).shortValue();
		else
			this.tempoExpiracaoInformadoUsuario = tempoExpiracao;
	}
	
	
	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	
	@Override
	public ListaMensagens validate() {
		ListaMensagens mensagens = new ListaMensagens();

		if(tempoExpiracaoInformadoUsuario <= 0)
			mensagens.addMensagem(MensagensArquitetura.VALOR_MAIOR_ZERO, "Tempo de Expiração");
		
		if(tempoExpiracaoInformadoUsuario > 100)
			mensagens.addMensagem(MensagensArquitetura.VALOR_MENOR_IGUAL_A, "Tempo de Expiração", 100);
		
		if(StringUtils.isEmpty(descricao))
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Descrição");
		
		return mensagens;
	}


	/////// Sets e Gets /////////
	

	public int getId() {return id;}
	public void setId(int id) {this.id = id;}
	
	public String getDescricao() {return descricao;}
	public String getDescricaoCompleta() {return descricao+"       [    "+tempoExpiracaoInformadoUsuario+" "+unidadeTempoExpiracao+"    ]";}
	public void setDescricao(String descricao) {	this.descricao = descricao;}
	
	public short getTempoExpiracao() {return tempoExpiracao;}
	public void setTempoExpiracao(short tempoExpiracao) {this.tempoExpiracao = tempoExpiracao;}
	
	public UnidadeTempoExpiracao getUnidadeTempoExpiracao() {return unidadeTempoExpiracao;}
	public void setUnidadeTempoExpiracao(UnidadeTempoExpiracao unidadeTempoExpiracao) {	this.unidadeTempoExpiracao = unidadeTempoExpiracao;}
	
	public Short getTempoExpiracaoInformadoUsuario() {return tempoExpiracaoInformadoUsuario;}
	public void setTempoExpiracaoInformadoUsuario(Short tempoExpiracaoInformadoUsuario) {
		if(tempoExpiracaoInformadoUsuario != null){
			this.tempoExpiracaoInformadoUsuario = tempoExpiracaoInformadoUsuario;
		}
	}
	
	public boolean isAtivo() {return ativo;}
	public void setAtivo(boolean ativo) {this.ativo = ativo;}

	public RegistroEntrada getRegistroCriacao() {return registroCriacao;}
	public void setRegistroCriacao(RegistroEntrada registroCriacao) {this.registroCriacao = registroCriacao;}
	public Date getDataCriacao() {return dataCriacao;}
	public void setDataCriacao(Date dataCriacao) {this.dataCriacao = dataCriacao;}
	public RegistroEntrada getRegistroUltimaAtualizacao() {return registroUltimaAtualizacao;}
	public void setRegistroUltimaAtualizacao(	RegistroEntrada registroUltimaAtualizacao) {this.registroUltimaAtualizacao = registroUltimaAtualizacao;}
	public Date getDataUltimaAtualizacao() {return dataUltimaAtualizacao;}
	public void setDataUltimaAtualizacao(Date dataUltimaAtualizacao) {this.dataUltimaAtualizacao = dataUltimaAtualizacao;}

	

	
	
}
