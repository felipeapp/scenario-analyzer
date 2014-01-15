/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p> Tabela que configura para cada serviço existente na biblioteca para qual emails as notificações devem ir.</p>
 *
 * <p>Por exemplo: Quanto o usuário solicita um serviço de catalogação na fonte, para quais emails a notificação 
 * que existe uma solicitação pedente deve ir ?   Isso é configurado per essa classe.</p>
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
	 * O tipo de serviço para o qual os e-mails de notificação são válidos.
	 */
	@Enumerated(EnumType.ORDINAL) // O valor salvo no banco vai ser a ordem em que as variáveis são declaradas
	@Column(name="tipo_servico_informacao_referencia", nullable=false)
	private TipoServicoInformacaoReferencia tipoServico;
	
	
	/** Informa a biblioteca para a qual os e-mails de notificação são válidos. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_biblioteca", referencedColumnName = "id_biblioteca", nullable=false)
	private Biblioteca biblioteca; 
	
	
	/** 
	 * <p>Lista de emails para serem notificados para a referidada biblioteca e serviço.</p>
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
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	
	@Override
	public ListaMensagens validate() {

		ListaMensagens mensagens = new ListaMensagens();

		if(tipoServico == null)
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Tipo de Serviço");
		
		if(biblioteca == null || biblioteca.getId() <= 0)
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Biblioteca");
		
		if(StringUtils.isEmpty(emailsNotificacao))
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "E-mails de Notificação");
		else{
			if(emailsNotificacao.length() > 200)
				mensagens.addMensagem(MensagensArquitetura.MAXIMO_CARACTERES, "E-mails de Notificação", "200");
			else{
				String[] emails = emailsNotificacao.split(";"); 
				for (int i = 0; i < emails.length; i++) {
					ValidatorUtil.validateEmail(emails[i], "E-mails", mensagens);
				}
			}
		}
		return mensagens;
	}

	/** A identificação de um emails de notificação é pela biblioteca e tipo de serviço. */
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

	/** A identificação de um emails de notificação é pela biblioteca e tipo de serviço. */
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
