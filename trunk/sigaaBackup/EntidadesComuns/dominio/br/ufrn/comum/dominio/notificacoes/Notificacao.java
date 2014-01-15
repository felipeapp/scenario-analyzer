/**
 *
 */
package br.ufrn.comum.dominio.notificacoes;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Classe de dom�nio que modela uma notifica��o de lista de destinat�rios,
 * seja de usu�rios ou de pessoas pertencentes a um p�blico-alvo.
 *
 * As notifica��es podem ser atrav�s de mensagens no sistema, e-mail ou ambos.
 *
 * @author Ricardo Wendell
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "notificacao",schema="comum")
public class Notificacao extends AbstractMovimentoAdapter{

	/** Constante que define o remetente padr�o. */
	public static final String REMETENTE_DEFAULT = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.REMETENTE_PADRAO_NOTIFICACAO);
	
	/** Chave prim�ria da notifica��o */
	@Id
	@Column(name="id_notificacao")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	private int id;
	
	/** T�tulo da mensagem */
	private String titulo;

	/** Corpo da mensagem */
	private String mensagem;

	/** Nome que ser� apresentado aos destinat�rios como remetente da mensagem (somente para e-mails) */
	@Column(name="nome_remetente")
	private String nomeRemetente;
	
	/** Email que ser� definido no replyTo para que respostas � notifica��o possam ser enviadas (somente para e-mails) */
	@Column(name="email_respostas")
	private String emailRespostas;
	
	/** Grupo de destinat�rios da notifica��o. */
	@Transient
	private Collection<GrupoDestinatarios> gruposDestinatarios;

	/** Grupo de emails de destinat�rios da notifica��o. */
	@Transient
	private Collection<Destinatario> destinatariosEmail = new TreeSet<Destinatario>(Destinatario.EMAIL_COMPARATOR);
	
	/** Grupo de usu�rios de destinat�rios da notifica��o. */
	@Transient
	private Collection<Destinatario> destinatariosMensagem = new TreeSet<Destinatario>(Destinatario.USUARIO_COMPARATOR);

	/** Indica��o se a mensagem foi enviada por e-mail */
	@Column(name="enviar_email")
	private boolean enviarEmail = true;

	/** Indica��o se a notifica��o foi enviada por mensagem no sistema */
	@Column(name="enviar_mensagem")
	private boolean enviarMensagem;

	/** Descri��o contendo uma lista com os nomes de todos os grupos de destinat�rios que foram notificados */
	@Column(name="descricao_destinatarios")
	private String descricaoDestinatarios;
	
	/** Usu�rio remetente da notifica��o */
	@ManyToOne
	@JoinColumn(name="id_usuario")
	@CriadoPor
	private UsuarioGeral usuario;

	/** Data em que a notifica��o foi enviada */
	@Column(name="data_envio")
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	private Date dataEnvio;
	
	/** Aponta para o id do arquivo que foi anexado na notifica��o, caso exista */
	@Column(name="id_anexo")
	private Integer idAnexo;
	
	/** Indica se o envio da notifica��o est� autorizado. */
	@Transient
	private boolean autorizado;

	/** Tipo do conte�do da notifica��o (texto ou HTML, por exemplo). */
	@Transient
	private String contentType = MailBody.TEXT_PLAN;
	
	/** Arquivo anexo a ser enviado com a notifica��o. */
	@Transient
	private UploadedFile anexo;
	
	/** Construtor padr�o */
	public Notificacao() {
	}

	/**
	 * Adiciona um destinat�rio � lista de destinat�rios.
	 * @param destinatario
	 */
	public void adicionarDestinatario(Destinatario destinatario) {
		destinatariosEmail.add(destinatario);
		destinatariosMensagem.add(destinatario);
	}

	/**
	 * Realiza uma uni�o entre o conjunto de destinat�rios passado como par�metro
	 * e o conjunto de destinat�rios da notifica��o.
	 * @param destinatarios
	 */
	public void adicionarDestinatarios(Collection<Destinatario> destinatarios) {
		destinatariosEmail.addAll(destinatarios);
		destinatariosMensagem.addAll(destinatarios);
	}

	/**
	 * Adiciona um grupo de destinat�rios � lista de grupos de destinat�rios
	 * da notifica��o.
	 * @param grupo
	 * @return
	 */
	public GrupoDestinatarios adicionarGrupoDestinatarios(GrupoDestinatarios grupo) {
		if (gruposDestinatarios == null) {
			gruposDestinatarios = new ArrayList<GrupoDestinatarios>();
		}

		if (gruposDestinatarios.contains(grupo)) {
			if (grupo.getParametros() != null && !grupo.getParametros().isEmpty())
				gruposDestinatarios.add(grupo);
		} else {
			gruposDestinatarios.add(grupo);
		}
		return grupo;
	}

	/**
	 * Remove um grupo de destinat�rios da lista de grupos de destinat�rios
	 * da notifica��o.
	 * @param grupo
	 */
	public void removerGrupoDestinatarios(GrupoDestinatarios grupo) {
		if (gruposDestinatarios != null) {
			gruposDestinatarios.remove(grupo);
		}
	}

	/** Indica se o tipo de notifica��o est� definido (mensagem ou e-mail).
	 * @return
	 */
	public boolean isTipoNotificacaoDefinida() {
		return enviarMensagem || enviarEmail;
	}

	/** Valida os dados da notifica��o.
	 * @return
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(titulo, "Assunto", lista);
		ValidatorUtil.validateRequired(mensagem, "Mensagem", lista);

		if ( !isDestinatariosDefinidos() ) {
			lista.addErro("� necess�rio definir os destinat�rios da mensagem");
		}

		if ( !isTipoNotificacaoDefinida() ) {
			lista.addErro("� necess�rio selecionar pelo menos um dos tipos de notifica��o dispon�veis: e-mail ou mensagem do sistema");
		}

		if ( !isEmpty(emailRespostas) ) {
			ValidatorUtil.validateEmail(emailRespostas, "E-mail para respostas", lista);
		}
		
		if(getTitulo().length() > 500)
			lista.addErro("Assunto: Deve possuir no m�ximo 500 caracteres.");

		if (!isEmpty(getGruposDestinatarios())) {
			for (GrupoDestinatarios gd : getGruposDestinatarios()) {
				if (!isEmpty(gd.getParametros())) {
					for (ParametroGrupo pg : gd.getParametros()) {
						if (isEmpty(pg.getValor()) && pg.getObrigatorio() == true) {
							lista.addErro("O grupo " + gd.getDescricao() + " n�o teve o valor do par�metro " + pg.getNome() + " definido.");
						}
					}
				}
			}
		}
		
		return lista;
	}

	/** Indica se os detinat�rios da notifica��o est�o definidos.
	 * @return
	 */
	private boolean isDestinatariosDefinidos() {
		return !(ValidatorUtil.isEmpty(destinatariosEmail)
				&& ValidatorUtil.isEmpty(destinatariosMensagem)
				&& ValidatorUtil.isEmpty(gruposDestinatarios) );
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public boolean isEnviarMensagem() {
		return enviarMensagem;
	}

	public void setEnviarMensagem(boolean enviarMensagem) {
		this.enviarMensagem = enviarMensagem;
	}

	public boolean isEnviarEmail() {
		return enviarEmail;
	}

	public void setEnviarEmail(boolean enviarEmail) {
		this.enviarEmail = enviarEmail;
	}

	public Collection<GrupoDestinatarios> getGruposDestinatarios() {
		return gruposDestinatarios;
	}

	public void setGruposDestinatarios(Collection<GrupoDestinatarios> gruposDestinatarios) {
		this.gruposDestinatarios = gruposDestinatarios;
	}

	public Collection<Destinatario> getDestinatariosEmail() {
		return destinatariosEmail;
	}

	public void setDestinatariosEmail(Collection<Destinatario> destinatariosEmail) {
		this.destinatariosEmail = destinatariosEmail;
	}

	public Collection<Destinatario> getDestinatariosMensagem() {
		return destinatariosMensagem;
	}

	public void setDestinatariosMensagem(
			Collection<Destinatario> destinatariosMensagem) {
		this.destinatariosMensagem = destinatariosMensagem;
	}

	public void setAutorizado(boolean autorizado) {
		this.autorizado = autorizado;
	}

	public boolean isAutorizado() {
		return autorizado;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public UsuarioGeral getUsuario() {
		return usuario;
	}

	@Override
	public void setUsuario(UsuarioGeral usuario) {
		this.usuario = usuario;
	}

	public Date getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public String getDescricaoDestinatarios() {
		return descricaoDestinatarios;
	}

	public void setDescricaoDestinatarios(String descricaoDestinatarios) {
		this.descricaoDestinatarios = descricaoDestinatarios;
	}

	public String getNomeRemetente() {
		return nomeRemetente;
	}

	public void setNomeRemetente(String nomeRemetente) {
		this.nomeRemetente = nomeRemetente;
	}

	public String getEmailRespostas() {
		return emailRespostas;
	}

	public void setEmailRespostas(String emailRespostas) {
		this.emailRespostas = emailRespostas;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Integer getIdAnexo() {
		return idAnexo;
	}

	public void setIdAnexo(Integer idAnexo) {
		this.idAnexo = idAnexo;
	}

	/**
	 * Popular a descricao dos destinatarios a partir dos grupos da notifica��o
	 */
	@SuppressWarnings("unchecked")
	public void popularDescricaoDestinatarios() {
		if ( !isEmpty(gruposDestinatarios) ) {
			Collection<String> grupos = CollectionUtils.collect(gruposDestinatarios, new Transformer() {
				public Object transform(Object obj) {
					return ((GrupoDestinatarios) obj).getDescricao();
				}
			});
			
			descricaoDestinatarios = StringUtils.transformaEmLista( br.ufrn.arq.util.CollectionUtils.toList(grupos) );
		} 		
	}

	public UploadedFile getAnexo() {
		return anexo;
	}

	public void setAnexo(UploadedFile anexo) {
		this.anexo = anexo;
	}
}
