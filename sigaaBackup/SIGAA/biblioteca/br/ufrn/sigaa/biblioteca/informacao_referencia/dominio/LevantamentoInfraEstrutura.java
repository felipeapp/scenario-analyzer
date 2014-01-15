/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 12/11/2010
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.dominio;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Um levantamento de infra-estrutura pode ser solicitado por chefes de departamento e
 * alguns outros funcion�rios. Um dos usos do levantamento � na cria��o de novos cursos.
 *
 * @author Br�ulio
 * 
 * @see ArquivoLevantamentoInfra
 */
@Entity
@Table(name="levantamento_infra",schema="biblioteca")
public class LevantamentoInfraEstrutura implements Validatable {

	/** Indica a situa��o atual de um levantamento de infra-estrutura. Use o campo <code>v</code>
	 * para pegar o valor da constante. */
	public enum Situacao {
		
		/** Indica que o levantamento de infra-estrutura foi solicitado por algum usu�rio e est� esperando atendimento. */
		SOLICITADO            (1, "Solicitado"),
		
		/** Indica que o levantamento foi cancelado por algum motivo. */
		CANCELADO             (2, "Cancelado"),
		
		/** Indica que o levantamento j� foi atendido. */
		CONCLUIDO             (3, "Conclu�do");
		
		/** Valor da constante */
		public final int v;
		/** Descri��o humana da constante. */
		public final String descricao;
		Situacao(int v, String descricao) { this.v = v; this.descricao = descricao; }
		@Override public String toString() { return descricao; }
		/** Converte do valor para a constante correspondente. */
		public static Situacao fromInt(int v) {
			for ( Situacao s : Situacao.values() )
				if ( v == s.v ) return s;
			return null;
		}
		public int getV() { return v; }
		public String getDescricao() { return descricao; }
	}
	
	/** O id do levantamento de infra-estrutura. */
	@Id
	@Column(name = "id_levantamento_infra")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="biblioteca.solicitacoes_usuario_sequence") })
	private int id;
	
	
	/**
	 * N�mero sequencial gerado pelo sistema para identificar mais facilmente uma solicita��o de levantamente de infra-estrutura.  
	 * Se o usu�rio tiver uma quantidade grande de solicita��es, fica um pouco dif�cil de diferenciar uma da outra.
	 */
	@Column(name = "numero_levantamento_infra")
	private int numeroLevantamentoInfra;
	
	
	
	/**
	 * A situa��o do levantamento.
	 * @see Situacao
	 */
	@Column(nullable=false)
	private int situacao;
	
	/** A pessoa que solicitou o levantamento. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_solicitante", nullable=false)
	private Pessoa solicitante;

	/** O texto de solicita��o que o usu�rio preenche. */
	@Column(name="texto_solicitacao", nullable=false)
	private String textoSolicitacao;

	/** O texto preenchido pelo bibliotec�rio quando ele atende a solicita��o. */
	@Column(name="texto_bibliotecario")
	private String textoBibliotecario;
	
	/** Os arquivos que comp�em o resultado do levantamento de infra-estrutura. */
	@OneToMany( mappedBy="levantamentoInfra", fetch=FetchType.LAZY)
	private List<ArquivoLevantamentoInfra> arquivos;
	
	/** A biblioteca que recebeu a solicita��o e que far� o levantamento de infra-estrutura. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_biblioteca", nullable=false)
	private Biblioteca biblioteca;
	
	/** O motivo pelo qual o levantamento foi cancelado. Quando a solicita��o � cancelada pelo bibliotec�rio. */
	@Column(name="motivo_cancelamento", length=300)
	private String motivoCancelamento;
	
	
	/** A data na qual a solicita��o foi feita. */
	@CriadoEm
	@Column(name="data_solicitacao")
	private Date dataSolicitacao;
	
	/** Quem atualizou por �ltimo o levantamento. */
	@AtualizadoEm
	@Column(name="data_atualizacao")
	private Date dataAtualizacao;
	
	/** A data na qual o levantamento foi conclu�do. */
	@Column(name="data_conclusao")
	private Date dataConclusao;
	
	/** A data na qual o levantamento foi cancelado. */
	@Column(name="data_cancelamento")
	private Date dataCancelamento;
	
	/** Registro de Entrada da solicita��o do levantamento. */
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada_solicitacao")
	private RegistroEntrada registroEntradaSolicitacao;

	/** Registro de Entrada da �ltima atualiza��o. */
	@AtualizadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada_atualizacao")
	private RegistroEntrada registroEntradaAtualizacao;
	
	/** Registro de Entrada da valida��o da solicita��o. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada_conclusao")
	private RegistroEntrada registroEntradaConclusao;
	
	/** Registro de Entrada do cancelamento  */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada_cancelamento")
	private RegistroEntrada registroEntradaCancelamento;
	
	@Override
	public ListaMensagens validate() {
		ListaMensagens msgs = new ListaMensagens();
		
		if ( solicitante == null )
			msgs.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Solicitante");
		if ( StringUtils.isEmpty(textoSolicitacao) )
			msgs.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Texto da Solicita��o");
		if ( biblioteca == null || biblioteca.getId() <= 0 )
			msgs.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Biblioteca");
		
		if ( this.situacao == Situacao.SOLICITADO.v ) {
			// nada mais a ser validado
		}
		else if ( this.situacao == Situacao.CONCLUIDO.v ) {
			if ( dataConclusao == null )
				msgs.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data de Conclus�o");
			if ( registroEntradaConclusao == null || registroEntradaConclusao.getId() <= 0 )
				msgs.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Registro de Entrada de Conclus�o");
			if ( StringUtils.isEmpty( textoBibliotecario ) )
				msgs.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Texto do Bibliotec�rio");
		}
		else if ( this.situacao == Situacao.CANCELADO.v ) {
			if ( StringUtils.isEmpty(this.getMotivoCancelamento()) )
				msgs.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Motivo do Cancelamento");
			if ( registroEntradaCancelamento == null || registroEntradaCancelamento.getId() <= 0 )
				msgs.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Registro de Entrada de Cancelamento");
			if ( dataCancelamento == null )
				msgs.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data de Cancelamento");
		}
		else {
			msgs.addErro("A situa��o do levantamento deve ser Solicitado, Conclu�do ou Cancelado.");
		}
		
		return msgs;
	}
	
	/**
	 *  <p>M�todo que verifica se a solicita��o de levamtamento de infra estrutura j� foi salva por algum bibliotec�rio. 
	 *  Caso positivo, o usu�rio n�o pode mais cancelar essa solicita��o. Pois o bibliotec�rio j� gastou tempo trabalhando nela. 
	 *  Caso o bibliotec�rio ainda n�o tenha come�ado a trabalhar nesse levantamento o usu�rio solicitante vai pode cancel�-la.</p>
	 *
	 * @return
	 */
	public boolean usuarioPodeCancelar(){
		if(dataAtualizacao == null)
			return true;
		else
			return false;
	}
	
	/**
	 * 
	 * Usado nas p�ginas JSF
	 * 
	 * @see {@link LevantamentoInfraEstrutura#usuarioPodeCancelar() }
	 *
	 * @return
	 */
	public boolean isUsuarioPodeCancelar(){
		return usuarioPodeCancelar();
	}
	
	
	//// GETs e SETs

	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public void setId(int id) {
		this.id = id;
	}

	public int getSituacao() {
		return situacao;
	}

	public void setSituacao(int situacao) {
		this.situacao = situacao;
	}

	public Pessoa getSolicitante() {
		return solicitante;
	}

	public void setSolicitante(Pessoa solicitante) {
		this.solicitante = solicitante;
	}

	public List<ArquivoLevantamentoInfra> getArquivos() {
		return arquivos;
	}

	public void setArquivos(List<ArquivoLevantamentoInfra> arquivos) {
		this.arquivos = arquivos;
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}

	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}

	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public Date getDataConclusao() {
		return dataConclusao;
	}

	public void setDataConclusao(Date dataConclusao) {
		this.dataConclusao = dataConclusao;
	}

	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

	public RegistroEntrada getRegistroEntradaSolicitacao() {
		return registroEntradaSolicitacao;
	}

	public void setRegistroEntradaSolicitacao(
			RegistroEntrada registroEntradaSolicitacao) {
		this.registroEntradaSolicitacao = registroEntradaSolicitacao;
	}

	public RegistroEntrada getRegistroEntradaConclusao() {
		return registroEntradaConclusao;
	}

	public void setRegistroEntradaConclusao(RegistroEntrada registroEntradaValidacao) {
		this.registroEntradaConclusao = registroEntradaValidacao;
	}

	public RegistroEntrada getRegistroEntradaCancelamento() {
		return registroEntradaCancelamento;
	}

	public void setRegistroEntradaCancelamento(
			RegistroEntrada registroEntradaCancelamento) {
		this.registroEntradaCancelamento = registroEntradaCancelamento;
	}

	/** Retorna a forma amig�vel ao usu�rio da situa��o. */
	public String getDescricaoSituacao() {
		Situacao s = Situacao.fromInt( this.situacao );
		if ( s != null ) return s.toString();
		else return "";
	}

	public String getTextoSolicitacao() {
		return textoSolicitacao;
	}

	public void setTextoSolicitacao(String textoSolicitacao) {
		this.textoSolicitacao = textoSolicitacao;
	}

	public String getTextoBibliotecario() {
		return textoBibliotecario;
	}

	public void setTextoBibliotecario(String textoBibliotecario) {
		this.textoBibliotecario = textoBibliotecario;
	}

	public String getMotivoCancelamento() {
		return motivoCancelamento;
	}

	public void setMotivoCancelamento(String motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public RegistroEntrada getRegistroEntradaAtualizacao() {
		return registroEntradaAtualizacao;
	}

	public void setRegistroEntradaAtualizacao(
			RegistroEntrada registroEntradaAtualizacao) {
		this.registroEntradaAtualizacao = registroEntradaAtualizacao;
	}
	
	public int getSolicitado() {
		return Situacao.SOLICITADO.v;
	}
	
	public int getCancelado() {
		return Situacao.CANCELADO.v;
	}

	public int getConcluido() {
		return Situacao.CONCLUIDO.v;
	}

	public int getNumeroLevantamentoInfra() {
		return numeroLevantamentoInfra;
	}

	public void setNumeroLevantamentoInfra(int numeroLevantamentoInfra) {
		this.numeroLevantamentoInfra = numeroLevantamentoInfra;
	}
	
}
