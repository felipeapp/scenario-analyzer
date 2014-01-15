/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 15/02/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.dominio;

import java.math.BigDecimal;
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

import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.PunicaoAtrasoEmprestimoStrategyFactory;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.dominio.Usuario;

/**
 *
 * <p>Entidade que guarda as informa��es das multas geras na devolu��o atrasada dos empr�stimos. </p>
 *
 * <p>Multas podem ser de dois tipo:
 * 		<ul>
 * 			<li>Autom�ricas:  Criadas pelo sistema, est�o ligadas ao empr�stimo que a gerou.</li>
 * 			<li>Manuais: Criadas pelo usu�rio: est�o ligas diretamente ao usu�rio biblioteca.</li>
 * 		</ul>
 * </p>
 * 
 * @author jadson
 *
 */
@Entity
@Table(name = "multa_usuario_biblioteca", schema = "biblioteca")
public class MultaUsuarioBiblioteca extends PunicaoAtrasoEmprestimoBiblioteca implements Validatable{

	/**
	 * <p>Representa os poss�veis estados que uma multa no sistema </p>
	 * 
	 * @author jadson
	 *
	 */
	public enum StatusMulta{
		
		/** 
		 * <p>EM_ABERTO = A multa foi criada e ainda n�o foi paga</p>
		 * <p>QUITADA_MANUALMENTE = Algum usu�rio quitou a multa do usu�rio no sistema mediante a apresenta��o do comprovante.</p>
		 * <p>QUITADA_AUTOMATICAMENTE = A multa foi quitada automaticamente por meio do recebimento do arquivo de retorno do banco, � poss�vel que multas 
		 * j� estornadas sejam quitadas automaticamente, desde que o estorno ocorra depois que o usu�rio j� tenha emitido a GRU para pagamento.</p>
		 */ 
		EM_ABERTO(0, "EM ABERTO"), QUITADA_MANUALMENTE(1, "QUITADA MANUALMENTE"), QUITADA_AUTOMATICAMENTE(2, "QUITADA AUTOMATICAMENTE");
		
		/**  O valor do enum, deve ser igual a posi��o que foi declarado. J� que n�o tem como - de uma maneira f�cil de simples, sem 
		 * herdar de outras classes e sobrescrever um monte de m�todo  - fazer o hibernate 
		 * pegar o valor desejado, o hibernate simplemente pega o valor da posi��o onde a vari�vel do 
		 * enum foi declarada para realizar a persist�ncia. <br/>
		 * Diferentemente da pesquisa, onde o valor utilizado  pelo hibernate � o valor do m�todo toString() <br/>.
		 * 
		 * {@link http://www.hibernate.org/265.html}
		 * {@link http://www.guj.com.br/java/144147-persistencia-de-enum-enumtypeordinal}
		 */
		private int valor;
		
		private String descricao;
		
		private StatusMulta(int valor, String descricao){
			this.valor = valor;
			this.descricao = descricao;
		}
		
		/**
		 * Retorna o status da multa a partir da descri��o.
		 * @param status
		 * @return
		 */
		public static StatusMulta getStatusMulta(Integer status) {
			if(status == EM_ABERTO.valor)
				return  EM_ABERTO;
			if(status == QUITADA_MANUALMENTE.valor)
				return  QUITADA_MANUALMENTE;
			if(status == QUITADA_AUTOMATICAMENTE.valor)
				return  QUITADA_AUTOMATICAMENTE;
			return null;
		}
		
		
		public String getDescricao() {
			return descricao;
		}

		/**  M�todo chamado pelo hibernate para pegar o valor do enum nas consultas HQLs  */
		@Override
		public String toString() {
			return String.valueOf(valor);
		}
	}
	

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.penalidades_usuario_sequence") })
	@Column(name = "id_multa_usuario_biblioteca", nullable = false)
	protected int id;
	
	
	/** O valor da multa : ( valor padr�o * quantidade de dias em atraso ) */
	@Column(name = "valor", nullable=false)
	private BigDecimal valor;
	
	/**
	 * O status da multa
	 */
	@Enumerated(EnumType.ORDINAL) // O valor salvo no banco vai ser a ordem em que as vari�veis s�o declaradas
	@Column(name="status", nullable=false)
	private StatusMulta status = StatusMulta.EM_ABERTO;
	
	
	/** 
	 *  <p> Guarda uma refer�ncia a GRU que foi gerada para pagamento dessa multa.</p>
	 *  <p> <strong>N�o tem um mapeamento com GuiaRecolhimentoUniao porque deve ficar no bando comum. <strong> </p>
	 *  <br/>
	 *  <p> <strong> A GRU S� � CRIADA E SEU ID SALVO, QUANDO O USU�RIO TENTA IMPRIMIR A GRU. <strong> </p>
	 */
	@Column(name = "id_gru_quitacao", nullable=true)
	private Integer idGRUQuitacao;
	
	/** Guarda o n�mero de refer�ncia da GRU, informa��o duplicada porque � necess�ria em alguns lugares no sistema 
	 * e para n�o precisar recuperar do banco comum. */
	@Column(name="numero_referencia", nullable=true)
	private long numeroReferencia;
	
	
	/** 
	 * <p>Guarda uma refer�ncia � biblioteca de recolhimento da GRU. 
	 * 
	 * <p>
	 *    (i) - Para os casos de multas manuais o usu�rio informa no momento do cadastro a unidade.
	 *    (ii) - Para as multas autom�ticas � a biblioteca do material.
	 * </p>
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn (name="id_biblioteca_recolhimento", nullable=false)
	private Biblioteca bibliotecaRecolhimento;
	
	
	/** Usu�rio que quitou a multa, para o caso de multas quitadas manualmente. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_quitacao", nullable=true)
	private Usuario usuarioQuitacao;
	
	
	/** Data em que a multa foi quitada, manualmente ou automaticamente. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_quitacao", nullable=true)
	private Date dataQuitacao;
	
	/** Alguma informa��o que o usu�rio queira informa no momento do pagamento. */
	@Column(name="observacao_pagamento", nullable=true)
	private String observacaoPagamento;
	
	
	/** Indica que a GRU foi paga. */
	@Transient
	private boolean gruQuitada;
	
	
	/**
	 * Informa��es que identificam uma malta, para no momento do pagamento o usu�rio saber qual a multa ele est� pagando.
	 */
	@Transient
	private String infoIdentificacaoMultaGerada;
	
	
	
	/**
	 * Retorna os status que s�o de multas pagas.
	 *
	 * @return
	 */
	public static StatusMulta[] getStatusPagos(){
		return new StatusMulta[]{StatusMulta.QUITADA_MANUALMENTE, StatusMulta.QUITADA_AUTOMATICAMENTE};
	}
	
	/**
	 * Retorna os status que s�o de multas pagas.
	 *
	 * @return
	 */
	public static StatusMulta[] getStatusNaoPagos(){
		return new StatusMulta[]{StatusMulta.EM_ABERTO};
	}
	
	/**
	 * 
	 * Verifica que o usu�rio j� gerou a GRU Para pagar essa multa.
	 *
	 * @return
	 */
	public boolean gruJaFoiGerada(){
		if(idGRUQuitacao != null && idGRUQuitacao.longValue() > 0){
			return true;
		}else
			return false;
	}
	
	/**
	 * 
	 * Utilizado nas p�gina JSF
	 *
	 * @return
	 */
	public boolean isgruJaFoiGerada(){
		return gruJaFoiGerada();
	}
	
	
	/**
	 * Valida os dados quando uma multa � criada.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		
		ListaMensagens mensagens = new ListaMensagens();
		
		if (valor == null || valor.equals(new BigDecimal(0)))
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Valor da multa");
		
		if(StringUtils.isEmpty(motivoCadastro))
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Motivo do cria��o da multa");
		
		if (bibliotecaRecolhimento == null || bibliotecaRecolhimento.getId() <= 0)
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Biblioteca do Recolhimento da Multa");
		
		return mensagens;
	}
	
	
	
	@Override
	public boolean equals (Object obj){
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	@Override
	public int hashCode (){
		return HashCodeUtil.hashAll(id);
	}
	
	
	// Sets e gets //
	
	
	public BigDecimal getValor() {
		return valor;
	}
	
	public String getValorFormatado() {
		return new PunicaoAtrasoEmprestimoStrategyFactory().getEstrategiaMulta().getValorFormatado(valor);
	}
	
	public void setValor(BigDecimal valor) {		
		this.valor = valor;
	}


	/**
	 * Usado para valores da multa digitados pelo usu�rio, porque o JSF dava erro ao tentar converte para BigDecimal
	 *
	 * @param valor
	 */
	public void setValorAsDouble(Double valor) {
		if(valor != null)
			this.valor = new BigDecimal(valor);
	}

	/**
	 * Usado para valores da multa digitados pelo usu�rio, porque o JSF dava erro ao tentar converte para BigDecimal
	 */
	public Double getValorAsDouble() {
		if(valor == null)
			return 0d;
		return valor.doubleValue();
	}
	
	public StatusMulta getStatus() {
		return status;
	}



	public void setStatus(StatusMulta status) {
		this.status = status;
	}


	public Usuario getUsuarioQuitacao() {
		return usuarioQuitacao;
	}

	public void setUsuarioQuitacao(Usuario usuarioQuitacao) {
		this.usuarioQuitacao = usuarioQuitacao;
	}

	public Date getDataQuitacao() {
		return dataQuitacao;
	}

	public void setDataQuitacao(Date dataQuitacao) {
		this.dataQuitacao = dataQuitacao;
	}

	public Integer getIdGRUQuitacao() {
		return idGRUQuitacao;
	}

	public void setIdGRUQuitacao(Integer idGRUQuitacao) {
		this.idGRUQuitacao = idGRUQuitacao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getInfoIdentificacaoMultaGerada() {
		return infoIdentificacaoMultaGerada;
	}

	public void setInfoIdentificacaoMultaGerada(String infoIdentificacaoMultaGerada) {
		this.infoIdentificacaoMultaGerada = infoIdentificacaoMultaGerada;
	}

	public String getObservacaoPagamento() {
		return observacaoPagamento;
	}

	public void setObservacaoPagamento(String observacaoPagamento) {
		this.observacaoPagamento = observacaoPagamento;
	}

	public boolean isGruQuitada() {
		return gruQuitada;
	}

	public void setGruQuitada(boolean gruQuitada) {
		this.gruQuitada = gruQuitada;
	}

	public Biblioteca getBibliotecaRecolhimento() {
		return bibliotecaRecolhimento;
	}

	public void setBibliotecaRecolhimento(Biblioteca bibliotecaRecolhimento) {
		this.bibliotecaRecolhimento = bibliotecaRecolhimento;
	}

	public long getNumeroReferencia() {
		return numeroReferencia;
	}

	public void setNumeroReferencia(long numeroReferencia) {
		this.numeroReferencia = numeroReferencia;
	}
	
	
	
}
