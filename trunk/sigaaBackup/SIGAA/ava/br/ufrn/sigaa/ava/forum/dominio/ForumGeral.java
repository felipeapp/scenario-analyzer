/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '14/02/2011'
 *
 */
package br.ufrn.sigaa.ava.forum.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Representa um Forum de discussão em uma determinada turma.
 *
 * @author Gleydson Lima
 * @author Ilueny Santos
 *
 */
@Entity
@Table(name = "forum", schema = "ava")
@HumanName(value="Fórum", genero='M')
public class ForumGeral implements Validatable {
	
	/** Chave Primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name="id_forum")           
	private int id;
	
	/** Descrição do fórum. */
	private String descricao;
	
	/** Título do fórum. */
	private String titulo;
	
	/** 
	 * Permite ao criador do forum monitorar leitura. 
	 * 
	 * Monitorar a leitura deste fórum?
	 * Se a opção 'monitorar leitura' dos fóruns estiver ativada, os usuários podem monitorar as 
	 * mensagens lidas e não-lidas em fóruns e discussões. O moderador pode escolher se forçar 
	 * um tipo de monitoramento no fórum.
	 * 
	 * Existem três escolhas para essa configuração:
	 * Opcional [padrão]: O estudante pode escolher se monitorar ou não o fórum a seu critério.
	 * Ativar: Monitoramento sempre ativo.
	 * Desativar: Monitoramento sempre desativado.
	 */
	@Column(name = "monitorar_leitura")
	private Boolean monitorarLeitura; 

	/** Representa o tipo de fórum. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_forum")
	private TipoForum tipo = new TipoForum();

	/** Indica se é um tipo de fórum válido. Utilizado para exclusão lógica. */
	@CampoAtivo
	private boolean ativo;

	/** Data de cadastro do fórum. */
	@CriadoEm
	@Column(name = "data_criacao")
	private Date dataCadastro;
	
	/** Usuário autor do fórum. */
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	/** Todas as Mensagens relacionadas ao fórum. */
	@OneToMany(mappedBy = "forum", fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	private List<ForumGeralMensagem> mensagens = new ArrayList<ForumGeralMensagem>(0);
	
	/** Número de tópicos que a mensagem recebeu. */
	@Column(name = "total_topicos")
	private Integer totalTopicos = 0;

	/** Última mensagem postada para o tópico. **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_ultima_mensagem")
	private ForumGeralMensagem ultimaMensagem;
	
	/** Representa a ordenação padrão utilizada nas mensagens do fórum. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_ordem_mensagens_forum")
	private OrdemMensagensForum ordenacaoPadrao = new OrdemMensagensForum();
	
	/** Arquivo anexado a uma mensagem **/
	@Column(name = "id_arquivo")
	private Integer idArquivo;
	
    /** Arquivo Anexo. */
	@Transient
    private UploadedFile arquivo;

	
	/** Construtor padrão. */
	public ForumGeral() {
	}
	
	public ForumGeral(int id) {
		this.id = id;
	}
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id; 
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * Retorna a descriao do fórum sem as tags HTML.
	 * @return descricao
	 */
	public String getDescricaoSemFormatacao() {
		if( descricao != null )
			return  StringUtils.stripHtmlTags(StringUtils.removerComentarios(StringUtils.removeLineBreak(descricao)));
		return descricao;
	}
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public TipoForum getTipo() {
		return tipo;
	}

	public void setTipo(TipoForum tipo) {
		this.tipo = tipo;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public List<ForumGeralMensagem> getMensagens() {
		return mensagens;
	}

	public void setMensagens(List<ForumGeralMensagem> mensagens) {
		this.mensagens = mensagens;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	/** Validação de principais campos do fórum. */
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(titulo, "Título", lista);		
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		ValidatorUtil.validateRequired(tipo, "Tipo", lista);
		return lista;
	}

	public Boolean getMonitorarLeitura() {
		return monitorarLeitura;
	}

	public void setMonitorarLeitura(Boolean monitorarLeitura) {
		this.monitorarLeitura = monitorarLeitura;
	}
	
	@Override
	public String toString() {
		return titulo;
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(obj, this, "id", "titulo");
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((titulo == null) ? 0 : titulo.hashCode());
		return result;
	}
	
	public int getTotalTopicos() {
		return totalTopicos;
	}

	public void setTotalTopicos(int totalTopicos) {
		this.totalTopicos = totalTopicos;
	}

	public ForumGeralMensagem getUltimaMensagem() {
		return ultimaMensagem;
	}

	public void setUltimaMensagem(ForumGeralMensagem ultimaMensagem) {
		this.ultimaMensagem = ultimaMensagem;
	}

	public void setTotalTopicos(Integer totalTopicos) {
		this.totalTopicos = totalTopicos;
	}

	public Integer getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	public OrdemMensagensForum getOrdenacaoPadrao() {
		return ordenacaoPadrao;
	}

	public void setOrdenacaoPadrao(OrdemMensagensForum ordenacaoPadrao) {
		this.ordenacaoPadrao = ordenacaoPadrao;
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	
	/** Retorna lista de tópicos ativos do fórum. */
	public List<ForumGeralMensagem> getTopicos(){
		List<ForumGeralMensagem> result = new ArrayList<ForumGeralMensagem>();
		for (ForumGeralMensagem m : mensagens) {
			if (m.isAtivo() && m.isTipoTopico()) {
				result.add(m);
			}			
		}
		return result;
	}

	/** Retorna lista de mensagens ativas do tópico informado. */
	public List<ForumGeralMensagem> getMensagensTopico(ForumGeralMensagem topico){
		List<ForumGeralMensagem> result = new ArrayList<ForumGeralMensagem>();
		if (topico != null && topico.isTipoTopico()) {
			for (ForumGeralMensagem m : mensagens) {
				if (m.isAtivo() && !m.isTipoTopico() && m.getTopico().equals(topico)) {
					result.add(m);
				}			
			}
		}
		return result;
	}

	
	/** Verifica se o fórum possui arquivo anexo. */
	public boolean isPossuiArquivoAnexo() {
		return ValidatorUtil.isNotEmpty(idArquivo);
	}

	/** Informa que cada usuário pode cadastrar apenas um tópico para fóruns deste tipo. */
	public boolean isCadaUsuarioApenasUmTopico() {
		//TODO: remover dependência de tipos fixos.
		return this.getTipo().getId() == TipoForum.TIPO_NOVO_TOPICO;
	}

	/** Retorna instância de fórum geral com configurações default. */
	public static ForumGeral getNewInstanceForumDefault() {
		ForumGeral forum = new ForumGeral();
		forum.setTipo(new TipoForum(TipoForum.TIPO_GERAL));
		forum.setOrdenacaoPadrao(new OrdemMensagensForum(OrdemMensagensForum.RESPOSTAS_ANINHADAS));
		forum.setMonitorarLeitura(true);
		return forum;
	}
	
}
