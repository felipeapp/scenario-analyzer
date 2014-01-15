/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 17/12/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.dominio;

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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;

/**
 *
 * <p> Tabela que configura para cada servi�o existente na biblioteca para qual emails as notifica��es devem ir.</p>
 *
 * <p>Por exemplo: Quanto o usu�rio solicita um servi�o de cataloga��o na fonte, para quais emails a notifica��o 
 * que existe uma solicita��o pedente deve ir ?   Isso � configurado per essa classe.</p>
 *
 * <p> <i>  O gestor geral da biblioteca pode configurar para cada um. e quantos e-mails quiser separados por ";" </i> </p>
 * 
 * @author jadson
 *
 */
@Entity
@Table(name = "emails_notificacao_servicos_biblioteca", schema = "biblioteca")
public class EmailsNotificacaoServicosBiblioteca implements Validatable{

	/** O identificador do registro. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.resgistro_extras_sequence") })
	@Column(name = "id_emails_notificacao_servicos_biblioteca")
	private int id;
	
	
	/**
	 * O tipo de servi�o para o qual os e-mails de notifica��o s�o v�lidos.
	 */
	@Enumerated(EnumType.ORDINAL) // O valor salvo no banco vai ser a ordem em que as vari�veis s�o declaradas
	@Column(name="tipo_servico_informacao_referencia", nullable=false)
	private TipoServicoInformacaoReferencia tipoServico;
	
	
	/** Informa a biblioteca para a qual os e-mails de notifica��o s�o v�lidos. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_biblioteca", referencedColumnName = "id_biblioteca", nullable=false)
	private Biblioteca biblioteca; 
	
	
	/** 
	 * <p>Lista de emails para serem notificados para a referidada biblioteca e servi�o.</p>
	 * <p>Guarda a lista de e-mails separados por ",". </p>
	 */
	@Column(name = "emails_notificacao", nullable=false)
	private String emailsNotificacao;

	
	/**
	 * O construtor
	 */
	public EmailsNotificacaoServicosBiblioteca() {
		
	}
	
	/**
	 * 
	 * @param tipoServico
	 * @param biblioteca
	 */
	public EmailsNotificacaoServicosBiblioteca(TipoServicoInformacaoReferencia tipoServico, Biblioteca biblioteca) {
		this.tipoServico = tipoServico;
		this.biblioteca = biblioteca;
	}
	
	/** Se a quantidade de emails for muito grande, mostra apenas os 20 primeiros caracteres. */
	public String getEmailsNotificacaoResumido(){
		if(emailsNotificacao != null && emailsNotificacao.length() > 40)
			return emailsNotificacao.substring(0, 40)+" . . . ";
		else
			return emailsNotificacao;
	}
	
	
	
	// sets e gets //
	public TipoServicoInformacaoReferencia getTipoServico() {return tipoServico;}
	public void setTipoServico(TipoServicoInformacaoReferencia tipoServico) {	this.tipoServico = tipoServico;}
	public Biblioteca getBiblioteca() {	return biblioteca;}
	public void setBiblioteca(Biblioteca biblioteca) {	this.biblioteca = biblioteca;}
	public String getEmailsNotificacao() {	return emailsNotificacao;}
	public void setEmailsNotificacao(String emailsNotificacao) {this.emailsNotificacao = emailsNotificacao;}
	public int getId() {return id;}
	public void setId(int id) {this.id = id;	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	
	@Override
	public ListaMensagens validate() {

		ListaMensagens mensagens = new ListaMensagens();

		if(tipoServico == null)
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Tipo de Servi�o");
		
		if(biblioteca == null || biblioteca.getId() <= 0)
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Biblioteca");
		
		if(StringUtils.isEmpty(emailsNotificacao))
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "E-mails de Notifica��o");
		else{
			if(emailsNotificacao.length() > 200)
				mensagens.addMensagem(MensagensArquitetura.MAXIMO_CARACTERES, "E-mails de Notifica��o", "200");
			else{
				String[] emails = emailsNotificacao.split(";"); 
				for (int i = 0; i < emails.length; i++) {
					ValidatorUtil.validateEmail(emails[i], "E-mails", mensagens);
				}
			}
		}
		return mensagens;
	}

	/** A identifica��o de um emails de notifica��o � pela biblioteca e tipo de servi�o. */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((biblioteca == null) ? 0 : biblioteca.hashCode());
		result = prime * result
				+ ((tipoServico == null) ? 0 : tipoServico.hashCode());
		return result;
	}

	/** A identifica��o de um emails de notifica��o � pela biblioteca e tipo de servi�o. */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmailsNotificacaoServicosBiblioteca other = (EmailsNotificacaoServicosBiblioteca) obj;
		if (biblioteca == null) {
			if (other.biblioteca != null)
				return false;
		} else if (!biblioteca.equals(other.biblioteca))
			return false;
		if (tipoServico != other.tipoServico)
			return false;
		return true;
	}
	
}
