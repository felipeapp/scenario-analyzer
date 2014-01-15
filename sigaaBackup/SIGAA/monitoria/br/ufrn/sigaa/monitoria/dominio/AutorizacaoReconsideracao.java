package br.ufrn.sigaa.monitoria.dominio;

// Generated 09/04/2007 10:03:38 

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * Classe que representa a autorização dada pelos membros da PROGRAD para que a
 * proposta de projeto seja re-analisada no que se refere aos requisitos
 * formais.
 * 
 * Assim, se o projeto não estiver sido enviado a prograd por algum motivo
 * formal (erro na elaboração da proposta) ele poderá ser reconsiderado e
 * reenviado depois.
 * 
 * classe referente a reconsideração por requisitos formais.
 * 
 ******************************************************************************/
@Entity
@Table(name = "autorizacao_reconsideracao", schema = "monitoria")
public class AutorizacaoReconsideracao implements Validatable, Cloneable {

	// Fields

	private int id;

	private ProjetoEnsino projetoEnsino;

	private RegistroEntrada registroEntradaAutorizador;

	private RegistroEntrada registroEntradaSolicitante;

	private Date dataAutorizacao;

	private Date dataSolicitacao;

	private boolean autorizado;

	private String justificativaSolicitacao;

	private String parecerPrograd;
	
	private boolean ativo;

	// Constructors

	/** default constructor */
	public AutorizacaoReconsideracao() {
	}

	// Property accessors

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_autorizacao_reconsideracao", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_projeto_monitoria", unique = false, nullable = false, insertable = true, updatable = true)
	public ProjetoEnsino getProjetoEnsino() {
		return this.projetoEnsino;
	}

	public void setProjetoEnsino(ProjetoEnsino projetoEnsino) {
		this.projetoEnsino = projetoEnsino;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada_autorizador", unique = false, nullable = true, insertable = true, updatable = true)
	public RegistroEntrada getRegistroEntradaAutorizador() {
		return this.registroEntradaAutorizador;
	}

	public void setRegistroEntradaAutorizador(
			RegistroEntrada registroEntradaAutorizador) {
		this.registroEntradaAutorizador = registroEntradaAutorizador;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada_solicitante", unique = false, nullable = true, insertable = true, updatable = true)
	public RegistroEntrada getRegistroEntradaSolicitante() {
		return this.registroEntradaSolicitante;
	}

	public void setRegistroEntradaSolicitante(
			RegistroEntrada registroEntradaSolicitante) {
		this.registroEntradaSolicitante = registroEntradaSolicitante;
	}

	/**
	 * Informa a data da autorização/desautorização dada pelo membro da prograd
	 * 
	 * @return
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_autorizacao", unique = false, nullable = true, insertable = true, updatable = true, length = 8)
	public Date getDataAutorizacao() {
		return dataAutorizacao;
	}

	public void setDataAutorizacao(Date dataAutorizacao) {
		this.dataAutorizacao = dataAutorizacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_solicitacao", unique = false, nullable = false, insertable = true, updatable = true, length = 8)
	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}

	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	@Column(name = "autorizado", unique = false, nullable = false, insertable = true, updatable = true)
	public boolean isAutorizado() {
		return autorizado;
	}

	public void setAutorizado(boolean autorizado) {
		this.autorizado = autorizado;
	}

	@Column(name = "justificativa_solicitacao", unique = false, nullable = true, insertable = true, updatable = true)
	public String getJustificativaSolicitacao() {
		return justificativaSolicitacao;
	}

	public void setJustificativaSolicitacao(String justificativaSolicitacao) {
		this.justificativaSolicitacao = justificativaSolicitacao;
	}

	@Column(name = "parecer_prograd", unique = false, nullable = true, insertable = true, updatable = true)
	public String getParecerPrograd() {
		return parecerPrograd;
	}

	public void setParecerPrograd(String parecerPrograd) {
		this.parecerPrograd = parecerPrograd;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(projetoEnsino, "Projeto de Monitoria", lista);
		ValidatorUtil.validateRequired(dataSolicitacao, "Data da Solicitação", lista);
		return lista;
	}

	public boolean isAtivo() {
	    return ativo;
	}

	public void setAtivo(boolean ativo) {
	    this.ativo = ativo;
	}

}