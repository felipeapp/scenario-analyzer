/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 16/07/2008
 */
package br.ufrn.arq.caixa_postal;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
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
import javax.persistence.Transient;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dao.UsuarioDAO;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Classe utilizada para representar mensagens no sistema
 * 
 * @author Gleydson Lima
 * 
 */
@Entity @Table(name = "Mensagem", schema = "comum")
public class Mensagem extends AbstractMovimento implements Movimento {


	/** Identificador */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={@Parameter(name="sequence_name", value="comum.mensagem_seq") })
	@Column(name = "id_mensagem")
	protected int id;

	/** Título da mensagem */
	@Column(name = "descricao")
	protected String titulo;

	/** Descrição da mensagem, pode ser em texto ou HTML */
	protected String mensagem;

	/*  Usuário que se destina a mensagem */
	@ManyToOne
	@JoinColumn(name = "id_usuario")
	private UsuarioGeral usuarioDestino;

	/**
	 * Mensagem para um papel, se for setado é enviado para todos os usuários
	 * que tem o papel.
	 */
	@Column(name = "id_papel")
	private Integer idPapel;

	/** Data do Cadastro da Mensagem* */
	@CriadoEm
	@Column(name = "data_cadastro")
	protected Date dataCadastro;

	/** Remetente da mensagem */
	@ManyToOne
	@JoinColumn(name = "id_remetente")
	protected UsuarioGeral remetente;

	/** Se a mensagem foi lida * */
	protected boolean lida;

	/** Data da leitura da mensagem pelo usuário * */
	@Column(name = "data_leitura")
	protected Date dataLeitura;

	/**
	 * Se a mensagem foi removida na caixa de mensagens do remetente, não
	 * aparecerá mais na caixa de entrada/saída do remetente
	 */
	@Column(name = "removida_remetente")
	protected Boolean removidaRemetente;

	/**
	 * Se a mensagem foi removida na caixa de mensagens do destinatario, não
	 * aparecerá mais na caixa de entrada/saída do remetente
	 */
	@Column(name = "removida_destinatario")
	protected boolean removidaDestinatario;

	/** Se a mensagem foi gerada automaticamente pelo sistema */
	protected boolean automatica;

	/** Se a mensagem possui confirmação de leitura */
	@Column(name = "confirmacao_leitura")
	protected boolean confLeitura;

	/** Tipo da mensagem */
	private int tipo = MENSAGEM;

	/** Número do chamado, se a mensagem for do tipo chamado. */
	@Column(name = "num_chamado")
	protected Integer numChamado;

	/** Número do chamado, se a mensagem for do tipo chamado patrimonial. */
	@Column(name = "num_chamado_patrimonial")
	protected Integer numChamadoPatrimonial;

	/** Caso a mensagem seja um chamado, especifica o tipo */
	@Column(name = "tipo_chamado")
	private Integer tipoChamado;
	
	@Transient
	private String tipoChamadoDesc;
	
	/**
	 * Atributo não mapeado, utilizado apenas para auxiliar na seleção individual ou em lote das mensangens
	 */
	@Transient
	private boolean selecionada;

	/** Id de mensagens selecionadas para operações em lote */
	@Transient
	protected int[] idMensagem;

	/** Sistema vinculado à mensagem */
	@Transient
	protected int sistema;
	
	/** Status do chamado, caso a mensagem seja um chamado  */
	@Column(name="status_chamado")
	private Integer statusChamado;
	
	/** Telefone fixo da pessoa que abriu o chamado para que a equipe de suporte possa entrar em contato. */
	@Column(name="telefone_fixo_contato")
	private String telefoneFixoContato;
	
	/** Telefone celular da pessoa que abriu o chamado para que a equipe de suporte possa entrar em contato. */
	@Column(name="telefone_movel_contato")
	private String telefoneMovelContato;
	
	
	/** Indica mensagem padrão */
	public static final int MENSAGEM = 1;

	/** Indica que a mensagem se trata de um chamado do SIPAC */
	public static final int CHAMADO_SIPAC = 2;

	/** Indica que a mensagem se trata de um chamado do SIGAA */
	public static final int CHAMADO_SIGAA = 3;

	/** Indica que a mensagem se trata de um chamado patrimonial do SIPAC */
	public static final int CHAMADO_SIPAC_PATRIMONIO = 4;

	/** Indica que a mensagem se trata de um chamado do SIGPRH */
	public static final int CHAMADO_SIGRH = 5;
	
	/** Indica que a mensagem se trata de um chamado do SIGAdmin */
	public static final int CHAMADO_SIGADMIN = 6;

	/** Indica que a mensagem se trata de um chamado do IProject */
	public static final int CHAMADO_IPROJECT = 7;

	/** Indica mensagem padrão */
	public static final int MAX_TITLE_LENGTH = 50;

	/** Indica mensagem padrão */
	public static final int TAM_PAGINA = 20;

	/** Indica mensagem padrão */
	public static final int TAM_PAGINA_ADM = 50;

	/** Lista de mensagens de resposta para a mensagem */
	@OneToMany(mappedBy = "replyFrom")
	private Collection<Mensagem> respostasArq;

	/** Lista de destinatários */
	@Transient
	private Collection<UsuarioGeral> destinatarios;

	/** Faz com que o usuário seja obrigado a ler a mensagem */
	@Column(name = "leitura_obrigatoria")
	private boolean leituraObrigatoria;

	/** Mensagem relacionada com uma outra */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_mensagem_anterior")
	private Mensagem replyFrom;

	/** Id do arquivo anexado */
	@Column(name="id_arquivo")
	private Integer idArquivo;
	
	/** Indica se a mensagem deve ser enviada por e-mail ou não */
	@Transient
	private boolean enviarEmail = false;
	
	/** Define se o arquivo anexado será enviado na resposta */
	@Transient
	private boolean anexarArqResposta = true;
	
	/** Arquivo anexado */
	@Transient
	private UploadedFile arquivo;
	
	/** Nome do arquivo anexado */
	@Column(name = "nome_arquivo")
	private String nomeArquivo;
	
	/** Indica se a mensagem foi removida da lixeira do remetente */
	@Column(name = "rem_lix_remetente")
	protected Boolean removidaLixeiraRemetente;
	
	/** Indica se a mensagem foi removida da lixeira do destinatário */
	@Column(name = "rem_lix_destinatario")
	protected Boolean removidaLixeiraDestinatario; 

	/**
	 * Mensagem para um papel, se for setado é enviado para todos os usuários
	 * que tem o papel *
	 */
	@Transient
	private Papel papel = new Papel();
	
	@Transient
	private List<String> respostasCriadorChamado = new ArrayList<String>();

	public Mensagem getReplyFrom() {
		return replyFrom;
	}

	public void setReplyFrom(Mensagem replyFrom) {
		this.replyFrom = replyFrom;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public Boolean isRemovidaRemetente() {
		return removidaRemetente;
	}

	public void setRemovidaRemetente(Boolean removidaRemetente) {
		this.removidaRemetente = removidaRemetente;
	}

	public boolean isRemovidaDestinatario() {
		return removidaDestinatario;
	}

	public void setRemovidaDestinatario(boolean removidaDestinatario) {
		this.removidaDestinatario = removidaDestinatario;
	}

	public Date getDataLeitura() {
		return dataLeitura;
	}

	public void setDataLeitura(Date dataLeitura) {
		this.dataLeitura = dataLeitura;
	}

	public Mensagem() {

	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Transient
	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String descricao) {
		this.titulo = descricao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public boolean isLida() {
		return lida;
	}

	public void setLida(boolean lida) {
		this.lida = lida;
	}

	public boolean isConfLeitura() {
		return confLeitura;
	}

	public void setConfLeitura(boolean confLeitura) {
		this.confLeitura = confLeitura;
	}

	/**
	 * @return Returns the numChamado.
	 */
	public Integer getNumChamado() {
		return numChamado;
	}

	/**
	 * @param numChamado
	 *            The numChamado to set.
	 */
	public void setNumChamado(Integer numChamado) {
		this.numChamado = numChamado;
	}

	public int[] getIdMensagem() {
		return idMensagem;
	}

	public void setIdMensagem(int[] idMensagem) {
		this.idMensagem = idMensagem;
	}

	/**
	 * Transforma a mensagem em um objeto do tipo MailBody para um destinatário específico.
	 * para ser enviado por e-mail.
	 * 
	 * @param replyTo
	 * @return
	 */
	public MailBody toMailBody(String replyTo, UsuarioGeral usuarioDestinatario) {

		MailBody mail = new MailBody();
		String msg = getMensagem();
		String titulo = getTitulo();
		
		if (getSistema() == Sistema.SIPAC) {
			titulo = "[SIPAC]" + titulo;
			msg += "\n\n"
					+ "Mensagem Gerada pelo SIPAC - Sistema Integrado de Patrimonio, Administração e Contratos";
		} else if (getSistema() == Sistema.SIGAA) {
			titulo = "[SIGAA]" + titulo;
			msg = "\n\n"
					+ "Mensagem Gerada pelo SIGAA - Sistema Integrado de Gestão de Atividades Acadêmicas";
		} else if (getSistema() == Sistema.IPROJECT) {
			if (!isEmpty(getDestinatarios())) {
				for (UsuarioGeral usr : getDestinatarios()) {
					UsuarioDAO daoArq = new UsuarioDAO();
					UsuarioGeral destinatario;
					try {
						destinatario = daoArq.findByPrimaryKey(usr.getId(), UsuarioGeral.class);
						mail.setEmail( destinatario.getEmail());
						msg += "\n\n"
						+ "Mensagem Gerada pelos Sistemas da " + RepositorioDadosInstitucionais.get("siglaInstituicao");
					} catch (DAOException e) {
						e.printStackTrace();
					} finally {
						daoArq.close();
					}
				}
			}
		}else if (getSistema() == Sistema.SIGADMIN) {
			UsuarioDAO daoArq = new UsuarioDAO();
			try {
				if(usuarioDestinatario != null) {
						mail.setEmail( daoArq.findByPrimaryKey(usuarioDestinatario.getId(), UsuarioGeral.class).getEmail());
						msg += "Mensagem Gerada pelo SIGAdmin - Sistema de Administração dos Sistemas (Técnica e Gestão)";
				}else if (!isEmpty(getDestinatarios())) {
					for (UsuarioGeral usr : getDestinatarios()) {
						UsuarioGeral destinatario;
							destinatario = daoArq.findByPrimaryKey(usr.getId(), UsuarioGeral.class);
							mail.setEmail( destinatario.getEmail());
							msg += "Mensagem Gerada pelo SIGAdmin - Sistema de Administração dos Sistemas (Técnica e Gestão)";
					}
				}
			} catch (DAOException e) {
				e.printStackTrace();
			} finally {
				daoArq.close();
			}
		}

		mail.setAssunto(titulo);
		mail.setMensagem(msg);
		mail.setReplyTo(replyTo);
		mail.setContentType(MailBody.TEXT_PLAN);

		return mail;
	}

	/**
	 * Transforma a mensagem em um objeto do tipo MailBody
	 * para ser enviado por e-mail.
	 * 
	 * @param replyTo
	 * @return
	 */
	public MailBody toMailBody(String replyTo) {
		return toMailBody(replyTo, null);
	}

	
	public void setDescricao(String descricao) {
		setTitulo(descricao);
	}

	public String getDescricao() {
		return getTitulo();
	}

	public Collection<Mensagem> getRespostasArq() {
		return respostasArq;
	}

	public void setRespostasArq(Collection<Mensagem> respostasArq) {
		this.respostasArq = respostasArq;
	}

	/**
	 * Retorna a data formatada 
	 * @return Date
	 */
	public String getDataCadastroStr() {
		if(ValidatorUtil.isEmpty(dataCadastro))
			this.setDataCadastro(new Date());
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		return sdf.format(dataCadastro);
	}

	/**
	 * Verifica se o título é maior que um determinado tamanho máximo. Se for,
	 * reduz o tamanho ao tamanho máximo e acrescenta reticências. Se não for, 
	 * retorna o título normal.
	 * 
	 * @return
	 */
	public String getTituloResumido() {
		StringBuffer tituloResumido = new StringBuffer();

		if (isChamado())
			tituloResumido.append(getNumChamado() + " - ");

		if (titulo.length() > MAX_TITLE_LENGTH)
			tituloResumido
					.append(titulo.substring(0, MAX_TITLE_LENGTH) + "...");
		else
			tituloResumido.append(titulo);

		return tituloResumido.toString();
	}

	public boolean isChamado() {
		return (this.tipo == Mensagem.CHAMADO_SIPAC
				|| this.tipo == Mensagem.CHAMADO_SIGAA || this.tipo == Mensagem.CHAMADO_SIGRH);
	}

	/**
	 * @return Returns the destinatarios.
	 */
	public Collection<UsuarioGeral> getDestinatarios() {
		return destinatarios;
	}

	/**
	 * @param destinatarios
	 *            The destinatarios to set.
	 */
	public void setDestinatarios(Collection<UsuarioGeral> destinatarios) {
		this.destinatarios = destinatarios;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Mensagem other = (Mensagem) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public boolean isLeituraObrigatoria() {
		return leituraObrigatoria;
	}

	public void setLeituraObrigatoria(boolean leituraObrigatoria) {
		this.leituraObrigatoria = leituraObrigatoria;
	}

	@Override
	public int getSistema() {
		return sistema;
	}

	@Override
	public void setSistema(int sistema) {
		this.sistema = sistema;
	}

	public boolean isEnviarEmail() {
		return enviarEmail;
	}

	public void setEnviarEmail(boolean enviarEmail) {
		this.enviarEmail = enviarEmail;
	}

	public boolean isAutomatica() {
		return automatica;
	}

	public void setAutomatica(boolean automatica) {
		this.automatica = automatica;
	}

	public Integer getIdPapel() {
		return idPapel != null ? idPapel : (papel != null ? papel.getId()
				: null);
	}

	public void setIdPapel(Integer idPapel) {
		this.idPapel = idPapel;
	}

	public Papel getPapel() {
		return papel;
	}

	public void setPapel(Papel papel) {
		this.papel = papel;
	}

	public Integer getTipoChamado() {
		return tipoChamado;
	}

	public void setTipoChamado(Integer tipoChamado) {
		this.tipoChamado = tipoChamado;
	}

	public Integer getNumChamadoPatrimonial() {
		return numChamadoPatrimonial;
	}

	public void setNumChamadoPatrimonial(Integer numChamadoPatrimonial) {
		this.numChamadoPatrimonial = numChamadoPatrimonial;
	}
	
	public boolean isSelecionada() {
		return selecionada;
	}

	public void setSelecionada(boolean selecionada) {
		this.selecionada = selecionada;
	}

	public UsuarioGeral getUsuarioDestino() {
		return usuarioDestino;
	}

	public void setUsuarioDestino(UsuarioGeral usuarioDestino) {
		this.usuarioDestino = usuarioDestino;
	}

	public UsuarioGeral getRemetente() {
		return remetente;
	}

	public void setRemetente(UsuarioGeral remetente) {
		this.remetente = remetente;
	}

	public Integer getStatusChamado() {
		return statusChamado;
	}

	public void setStatusChamadoDesc(Integer statusChamado) {
		this.statusChamado = statusChamado;
	}

	public String getStatusChamadoDesc() {
		return getStatusChamadoDesc(statusChamado);
	}
	
	/**
	 * Retorna a descrição do chamado atraves de um parametro
	 * @param statusChamado
	 * @return
	 */
	public static String getStatusChamadoDesc(Integer statusChamado) {
		if (statusChamado == null)
			return "";

		switch (statusChamado) {
		case (-1):
			return "PENDENTE";
		case 1:
			return "ABERTO LIDO";
		case 2:
			return "ABERTO DESIGNADO";
		case 3:
			return "AGUARD. USUÁRIO";
		case 4:
			return "SETOR DESENVOLVIMENTO.";
		case 5:
			return "ATRIBUÍDO AO SUPORTE";
		case 6:
			return "RESOLVIDO";
		case 7:
			return "RESP. USUÁRIO";
		default:
			return "";
		}
	}
	
	/**
	 * Retorna o identificador do tipo de chamado por sistema
	 * @param sistema
	 * @return
	 */
	public static int getTipoChamado(int sistema) {

		switch (sistema) {

		case Sistema.SIPAC:
			return Mensagem.CHAMADO_SIPAC;

		case Sistema.SIGRH:
			return Mensagem.CHAMADO_SIGRH;

		case Sistema.SIGAA:
			return Mensagem.CHAMADO_SIGAA;

		case Sistema.SIGADMIN:
			return Mensagem.CHAMADO_SIGADMIN;

		case Sistema.IPROJECT:
			return Mensagem.CHAMADO_IPROJECT;

		default:
			return Mensagem.CHAMADO_SIGADMIN;
		}
	}

	public String getTipoChamadoDesc() {
		return tipoChamadoDesc;
	}

	public void setTipoChamadoDesc(String tipoChamadoDesc) {
		this.tipoChamadoDesc = tipoChamadoDesc;
	}

	public Integer getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setAnexarArqResposta(boolean anexarArqResposta) {
		this.anexarArqResposta = anexarArqResposta;
	}

	public boolean isAnexarArqResposta() {
		return anexarArqResposta;
	}

	public String getTelefoneFixoContato() {
		return telefoneFixoContato;
	}

	public void setTelefoneFixoContato(String telefoneFixoContato) {
		this.telefoneFixoContato = telefoneFixoContato;
	}

	public String getTelefoneMovelContato() {
		return telefoneMovelContato;
	}

	public void setTelefoneMovelContato(String telefoneMovelContato) {
		this.telefoneMovelContato = telefoneMovelContato;
	}

	public Boolean getRemovidaLixeiraRemetente() {
		return removidaLixeiraRemetente;
	}

	public void setRemovidaLixeiraRemetente(Boolean removidaLixeiraRemetente) {
		this.removidaLixeiraRemetente = removidaLixeiraRemetente;
	}

	public Boolean getRemovidaLixeiraDestinatario() {
		return removidaLixeiraDestinatario;
	}

	public void setRemovidaLixeiraDestinatario(Boolean removidaLixeiraDestinatario) {
		this.removidaLixeiraDestinatario = removidaLixeiraDestinatario;
	}

	public List<String> getRespostasCriadorChamado() {
		return respostasCriadorChamado;
	}

	public void setRespostasCriadorChamado(List<String> respostasCriadorChamado) {
		this.respostasCriadorChamado = respostasCriadorChamado;
	}

}