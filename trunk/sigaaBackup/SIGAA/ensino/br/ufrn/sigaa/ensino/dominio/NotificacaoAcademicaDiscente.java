package br.ufrn.sigaa.ensino.dominio;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Entidade que representa uma notificação enviada para o discente.
 *
 * @author Diego Jácome
 */
@Entity @Table(name = "notificacao_academica_discente", schema = "ensino")
@HumanName(value="Notificação Acadêmica do Discente", genero='F')
public class NotificacaoAcademicaDiscente implements PersistDB{
	
	/**Chave primaria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_notificacao_academica_discente", nullable = false)
	private int id;

	/** Notificação acadêmica que serve de template. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_notificacao_academica")
	private NotificacaoAcademica notificacaoAcademica;

	/** Discente que será notificado. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_discente")
	private Discente discente;
	
	/**
	 * Registro de entrada do usuário que cadastrou
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_registro_entrada_notificacao")
	@CriadoPor
	private RegistroEntrada registroNotificacao;

	/**
	 * Registro de entrada do usuário que confirmou
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_registro_entrada_confirmacao")
	private RegistroEntrada registroConfirmacao;
	
	/**
	 * Data de cadastro
	 */
	@Column(name="data_cadastro")
	private Date dataCadastro;
	
	/**
	 * Data de confirmação
	 */
	@Column(name="data_confirmacao")
	private Date dataConfirmacao;
		
	/**
	 * Se a notificação está pendente
	 */
	private boolean pendente;
	
	//////////////////////////// informações auditoria //////////////////////////////
	
	/**
	 * Mensagem que o discente foi notificado
	 */
	@Column(name="mensagem_notificacao")
	private String mensagemNotificacao;
	
	/**
	 * Mensagem que o discente foi notificado por e-mail
	 */
	@Column(name="mensagem_email")
	private String mensagemEmail;
	
	/**
	 * Se a notificação exige confirmação
	 */
	@Column(name="exige_confirmacao")
	private Boolean exigeConfirmacao;
	
	/**
	 * Se é para mostrar o curso discente da notificação. Utilizada na listagem de discentes notificados
	 */
	@Transient
	private boolean mostrarCurso;
	
	/**
	 *Quantidade de alunos notificados em um curso. Utilizada na listagem de discentes notificados
	 */
	@Transient
	private Integer qtdAlunos;
	
	/**
	 * Se a notificação já foi visualizada pelo discente.
	 */
	@Transient
	private Boolean visualizada;
	
	/**
	 * Data da última visualização pelo discente.
	 */
	@Transient
	private Date ultimaVisualizacao;
	
	public NotificacaoAcademicaDiscente() {}
	
	public NotificacaoAcademica getNotificacaoAcademica() {
		return notificacaoAcademica;
	}

	public void setNotificacaoAcademica(NotificacaoAcademica notificacaoAcademica) {
		this.notificacaoAcademica = notificacaoAcademica;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public RegistroEntrada getRegistroNotificacao() {
		return registroNotificacao;
	}

	public void setRegistroNotificacao(RegistroEntrada registroNotificacao) {
		this.registroNotificacao = registroNotificacao;
	}

	public RegistroEntrada getRegistroConfirmacao() {
		return registroConfirmacao;
	}

	public void setRegistroConfirmacao(RegistroEntrada registroConfirmacao) {
		this.registroConfirmacao = registroConfirmacao;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Date getDataConfirmacao() {
		return dataConfirmacao;
	}

	public void setDataConfirmacao(Date dataConfirmacao) {
		this.dataConfirmacao = dataConfirmacao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPendente(boolean pendente) {
		this.pendente = pendente;
	}

	public boolean isPendente() {
		return pendente;
	}

	public void setMensagemNotificacao(String mensagemNotificacao) {
		this.mensagemNotificacao = mensagemNotificacao;
	}

	public String getMensagemNotificacao() {
		return mensagemNotificacao;
	}

	public void setMensagemEmail(String mensagemEmail) {
		this.mensagemEmail = mensagemEmail;
	}

	public String getMensagemEmail() {
		return mensagemEmail;
	}

	public void setExigeConfirmacao(Boolean exigeConfirmacao) {
		this.exigeConfirmacao = exigeConfirmacao;
	}

	public Boolean getExigeConfirmacao() {
		return exigeConfirmacao;
	}

	public void setMostrarCurso(boolean mostrarCurso) {
		this.mostrarCurso = mostrarCurso;
	}

	public boolean isMostrarCurso() {
		return mostrarCurso;
	}

	public void setQtdAlunos(Integer qtdAlunos) {
		this.qtdAlunos = qtdAlunos;
	}

	public Integer getQtdAlunos() {
		return qtdAlunos;
	}

	public void setVisualizada(Boolean visualizada) {
		this.visualizada = visualizada;
	}

	public Boolean getVisualizada() {
		return visualizada;
	}

	public void setUltimaVisualizacao(Date ultimaVisualizacao) {
		this.ultimaVisualizacao = ultimaVisualizacao;
	}

	public String getUltimaVisualizacao() {
		String res = null;
		if (ultimaVisualizacao!=null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			res = sdf.format(ultimaVisualizacao);
		}		
		return res;
	}

}
