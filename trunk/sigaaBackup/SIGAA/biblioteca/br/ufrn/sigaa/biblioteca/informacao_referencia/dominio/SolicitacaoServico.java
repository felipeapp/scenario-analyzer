/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Criado em 08/04/2011
 *
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 *	<p>Representa a abstração de uma solicitação de serviço prestado pela biblioteca ao usuário.</p>
 *
 *	@author Felipe Rivas
 */
@MappedSuperclass
public abstract class SolicitacaoServico implements PersistDB, Validatable {
	
	/**
	 * As situações (estados) em que uma solicitação de serviço pode estar
	 */
	public static enum TipoSituacao {
		
		/** Usuário fez a solicitação de serviço */
		SOLICITADO(0, "Solicitado"),
		/** Usuário efetuou os procedimentos iniciais necessários (entrega de material, etc), 
		 * 	o bibliotecário validou e a solicitação está pronta para ser atendida */
		VALIDADO(1, "Validado"),
		/** A solicitação foi atendida pelo bibliotecário (fez o serviço) */
		ATENDIDO(2, "Atendido"),
		/** O usuário ou bibliotecário cancelou a solicitação */
		CANCELADO(3, "Cancelado"),
		/** O usuário confirmou a solicitação */
		CONFIRMADO(4, "Confirmado"),
		/** O usuário não confirmou a solicitação */
		NAO_CONFIRMADO(5, "Não Confirmado"),
		/** O usuário reenviou a solicitação */
		REENVIADO(6, "Reenviado"),
		/** A solicitação foi atendida pela última vez pelo bibliotecário (corrigiu os erros) */
		ATENDIDO_FINALIZADO(7, "Atendido Finalizado");
		
		/**
		 * Valor numérico do item da enumeração
		 */
		private int valor;
		/**
		 * Descrição do item da enumeração
		 */
		private String descricao;
		
		private TipoSituacao(int valor, String descricao) {
			this.valor = valor;
			this.descricao = descricao;
		}
		
		/** Retorna as situações que a solicitação está inativa, mesmo a data do atendimento ainda for uma data futura.*/
		public static TipoSituacao[] getTipoSituacoesInativas(){
			return new TipoSituacao[]{CANCELADO, NAO_CONFIRMADO};
		}
		
		public String getDescricao() {
			return descricao;
		}

		@Override
		public String toString() {
			return String.valueOf(this.valor);
		}
		
	}
	
	/** Identifica uma solicitação no sistema.  Gerado a partir de uma sequência no banco. */
	@Column(name = "numero_solicitacao", nullable = false)
	private int numeroSolicitacao;
	
	
	/**
	 * Identifica a pessoa que realizou a solicitação, não pode ser discente nem servidor,
	 * porque geralmente os usuários possuem vários vínculos ao mesmo tempo
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa")
	private Pessoa pessoa;
	
	/**
	 * Se o usuário realizou a solicitação pelo modulo do servidor ou pelo portal do docente,
	 * vai ficar como solicitação de servidor
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_servidor")
	private Servidor servidor;
	
	/** Se o usuário realizou a solicitação pelo portal do discente vai ficar como solicitação de discente */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_discente")
	private Discente discente;
	
	/** A biblioteca a qual esta solicitação está vinculada. */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_biblioteca")
	private Biblioteca biblioteca;

	/** A situação da solicitação de serviço */
	@Enumerated(EnumType.ORDINAL)
	@Column(name="situacao")
	private TipoSituacao situacao;
		
	/** Observações do solicitante (discente ou servidor) ao bibliotecário.  */
	@Column(name="observacoes")
	private String observacoes;
	
	/** Se o usuário apagar uma solicitação feita, ela vai ser desativada por questões de auditoria */
	@Column(name="ativa")
	private boolean ativa = true;
	
	////////////////////////////////  Dados da Auditoria //////////////////////////////

	/** Data que foi cadastrado. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Usuário que cadastrou. */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** Data em que a última alteração ocorreu. Pode ser igual a data de atendimento, ou cancelamento. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_ultima_alteracao")
	@AtualizadoEm
	private Date dataUltimaAlteracao;

	/** Usuário que alterou por último a solicitação de serviço. Pode ser igual ao registro de atendimento, ou cancelamento. */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_alteracao", unique = false, nullable = true, insertable = true, updatable = true)
	@AtualizadoPor
	private RegistroEntrada registroUltimaAlteracao;
	
	/** Data que foi atendida. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atendimento")
	private Date dataAtendimento;

	/** Usuário que atendeu. */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atendimento", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroEntrada registroAtendimento;

	/** Data que foi cancelado. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cancelamento")
	private Date dataCancelamento;

	/** Usuário que cancelou. */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cancelamento", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroEntrada registroCancelamento;
	
	/////////////////////////// sets e gets ////////////////////////////
	
	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public Discente getDiscente() {
		return discente;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataAtendimento() {
		return dataAtendimento;
	}

	public void setDataAtendimento(Date dataAtendimento) {
		this.dataAtendimento = dataAtendimento;
	}

	public RegistroEntrada getRegistroAtendimento() {
		return registroAtendimento;
	}

	public void setRegistroAtendimento(RegistroEntrada registroAtendimento) {
		this.registroAtendimento = registroAtendimento;
	}

	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

	public RegistroEntrada getRegistroCancelamento() {
		return registroCancelamento;
	}

	public void setRegistroCancelamento(RegistroEntrada registroCancelamento) {
		this.registroCancelamento = registroCancelamento;
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}

	public TipoSituacao getSituacao() {
		return situacao;
	}
	
	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public String getObservacoes() {
		return observacoes;
	}
	
	public boolean isAtiva() {
		return ativa;
	}
	
	public void setAtiva(boolean ativa) {
		this.ativa = ativa;
	}

	public String getSolicitante(){
		return pessoa.getNome();
	}
	
	/**
	 * Retorna a pessoa que atendeu
	 * @return
	 */
	public String getAtendente(){
		return registroAtendimento.getUsuario().getPessoa().getNome();
	}
	
	/**
	 * Retorna a descrição do tipo de serviço que o objeto representa
	 * @return
	 */
	public abstract String getTipoServico();
	
	/**
	 * Retorna a pessoa que cancelou
	 * @return
	 */
	public String getCancelador(){
		return registroCancelamento.getUsuario().getPessoa().getNome();
	}

	public void setSituacao(TipoSituacao situacao) {
		this.situacao = situacao;
	}

	public boolean isSolicitado(){
		return situacao == TipoSituacao.SOLICITADO;
	}
	
	public boolean isValidado(){
		return situacao == TipoSituacao.VALIDADO;
	}
	
	public boolean isAtendido(){
		return situacao == TipoSituacao.ATENDIDO;
	}
	
	public boolean isCancelado(){
		return situacao == TipoSituacao.CANCELADO;
	}
	
	public boolean isReenviado(){
		return situacao == TipoSituacao.REENVIADO;
	}
	
	public boolean isAtendidoFinalizado(){
		return situacao == TipoSituacao.ATENDIDO_FINALIZADO;
	}
	
	public int getNumeroSolicitacao() {
		return numeroSolicitacao;
	}

	public void setNumeroSolicitacao(int numeroSolicitacao) {
		this.numeroSolicitacao = numeroSolicitacao;
	}
	
	public Date getDataUltimaAlteracao() {
		return dataUltimaAlteracao;
	}

	public void setDataUltimaAlteracao(Date dataUltimaAlteracao) {
		this.dataUltimaAlteracao = dataUltimaAlteracao;
	}

	public RegistroEntrada getRegistroUltimaAlteracao() {
		return registroUltimaAlteracao;
	}

	public void setRegistroUltimaAlteracao(RegistroEntrada registroUltimaAlteracao) {
		this.registroUltimaAlteracao = registroUltimaAlteracao;
	}

	/**
	 * Valida o preenchimento dos campos do objeto.
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		
		ListaMensagens erros = new ListaMensagens();
		
		ValidatorUtil.validateRequired(biblioteca, "Biblioteca", erros);

		if(discente == null && servidor == null) {
			erros.addErro("Dados do usuário que está fazendo a solicitação não estão configurados corretamente.");
		}
		
		return erros;
	}

}