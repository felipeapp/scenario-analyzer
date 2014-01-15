package br.ufrn.sigaa.ensino.dominio;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Entidade para notificar os discentes
 *
 * @author Diego J�come
 */
@Entity @Table(name = "notificacao_academica", schema = "ensino")
@HumanName(value="Notifica��o Acad�mica", genero='F')
public class NotificacaoAcademica implements PersistDB{

	/**Chave primaria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_notificacao_academica", nullable = false)
	private int id;

	/** Descri��o da notifica��o. */
	private String descricao;

	/** Mensagem no e-mail da notifica��o. */
	@Column(name = "mensagem_email")
	private String mensagemEmail;

	/** Mensagem de confirma��o enviada para os discentes. */
	@Column(name = "mensagem_notificacao")
	private String mensagemNotificacao;
	
	/** Se exige confirma��o para acessar o SIGAA. */
	@Column(name = "exige_confirmacao")
	private boolean exigeConfirmacao;
	
	/** SQL com o filtro que ir� retornar os discentes que ser�o notificados. */
	@Column(name = "sql_filtros_discentes")
	private String sqlFiltrosDiscentes;
	
	/** Indica se a notifica��o d� suporte a ano e per�odo de refer�ncia. */
	@Column(name = "ano_periodo_referencia")
	private boolean anoPeriodoReferencia;
	
	/** Discentes que ser�o notificados. */
	@Transient
	private List<Discente> discentes;
	
	/** Se � pra exibir os discentes na tela de listagem. */
	@Transient
	private boolean exibirDiscentes;
	
	/** N�meros de discentes que ser�o notificados. */
	@Transient
	private Integer numeroDiscentes;
	
	/** Exibir o n�meros de discentes que ser�o notificados. */
	@Transient
	private boolean exibirNumeroDiscentes;
	
	/** Data em que a notifica��o foi enviada. */
	@Transient
	private Date dataEnvio;
	
	/** Quantidade de notifica��es enviadas. */
	@Transient
	private Integer qtdEnviadas;
	
	/** Quantidade de notifica��es confirmadas pelos discentes. */
	@Transient
	private Integer qtdConfirmadas;
	
	/** Registro de notifica��o dos Discentes */
	@Transient
	private Integer idRegistroNotificacao;
	
	/** Ano de refer�ncia do sql da notifica��o */
	@Transient
	private Integer anoReferencia;
	
	/** Per�odo de refer�ncia do sql da notifica��o */
	@Transient
	private Integer periodoReferencia;
	
	
	/** Se a notifica��o est� ativa ou foi deletada. */
	private Boolean ativo;
	
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

	public String getMensagemEmail() {
		return mensagemEmail;
	}

	public void setMensagemEmail(String mensagemEmail) {
		this.mensagemEmail = mensagemEmail;
	}

	public String getMensagemNotificacao() {
		return mensagemNotificacao;
	}

	public void setMensagemNotificacao(String mensagemNotificacao) {
		this.mensagemNotificacao = mensagemNotificacao;
	}

	public boolean isExigeConfirmacao() {
		return exigeConfirmacao;
	}

	public void setExigeConfirmacao(boolean exigeConfirmacao) {
		this.exigeConfirmacao = exigeConfirmacao;
	}

	public String getSqlFiltrosDiscentes() {
		return sqlFiltrosDiscentes;
	}

	public void setSqlFiltrosDiscentes(String sqlFiltrosDiscentes) {
		this.sqlFiltrosDiscentes = sqlFiltrosDiscentes;
	}

	public void setDiscentes(List<Discente> discentes) {
		this.discentes = discentes;
	}

	public List<Discente> getDiscentes() {
		return discentes;
	}

	public void setExibirDiscentes(boolean exibirDiscentes) {
		this.exibirDiscentes = exibirDiscentes;
	}

	public boolean isExibirDiscentes() {
		return exibirDiscentes;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NotificacaoAcademica other = (NotificacaoAcademica) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	public void setNumeroDiscentes(Integer numeroDiscentes) {
		this.numeroDiscentes = numeroDiscentes;
	}

	public Integer getNumeroDiscentes() {
		return numeroDiscentes;
	}

	public void setExibirNumeroDiscentes(boolean exibirNumeroDiscentes) {
		this.exibirNumeroDiscentes = exibirNumeroDiscentes;
	}

	public boolean isExibirNumeroDiscentes() {
		return exibirNumeroDiscentes;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public Date getDataEnvio() {
		return dataEnvio;
	}

	public void setQtdEnviadas(Integer qtdEnviadas) {
		this.qtdEnviadas = qtdEnviadas;
	}

	public Integer getQtdEnviadas() {
		return qtdEnviadas;
	}
	
	public Integer getQtdConfirmadas() {
		return qtdConfirmadas;
	}

	public void setQtdConfirmadas(Integer qtdConfirmadas) {
		this.qtdConfirmadas = qtdConfirmadas;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Boolean isAtivo() {
		return ativo;
	}

	public void setIdRegistroNotificacao(Integer idRegistroNotificacao) {
		this.idRegistroNotificacao = idRegistroNotificacao;
	}

	public Integer getIdRegistroNotificacao() {
		return idRegistroNotificacao;
	}

	public void setAnoPeriodoReferencia(boolean anoPeriodoReferencia) {
		this.anoPeriodoReferencia = anoPeriodoReferencia;
	}

	public boolean isAnoPeriodoReferencia() {
		return anoPeriodoReferencia;
	}
	
	public void setAnoReferencia(Integer anoReferencia) {
			this.anoReferencia = anoReferencia;
	}

	public Integer getAnoReferencia() {
		return anoReferencia;
	}

	public void setPeriodoReferencia(Integer periodoReferencia) {
			this.periodoReferencia = periodoReferencia;
	}

	public Integer getPeriodoReferencia() {
		return periodoReferencia;
	}

}
