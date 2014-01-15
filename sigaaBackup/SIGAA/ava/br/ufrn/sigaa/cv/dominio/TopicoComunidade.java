/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 */
package br.ufrn.sigaa.cv.dominio;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * O t�pico da comunidade � onde as entidades se ligam, tais como arquivos, etc..
 * 
 * @author Gleydson Lima
 */

@SuppressWarnings("serial")
@Entity
@Table(name = "topico_comunidade", schema = "cv")
public class TopicoComunidade implements DominioComunidadeVirtual {

	/** Chave Prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_topico_comunidade", nullable = false)
	private int id;

	/** T�tulo do t�pico */
	private String descricao;

	/** Conte�do do t�pico */
	private String conteudo;
	
	/** A cor RGB de fundo do t�pico da comunidade. */
	private String cor;
	
	/** Indica se o t�pico est� no modo cadastro ou no modo edi��o */
	@Transient
	private boolean modoCadastro;

	/** Comunidade do t�pico. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_comunidade")
	private ComunidadeVirtual comunidade;

	/**
	 * T�pico pai. Os t�picos s�o organizados em estrutura de �rvore, podendo um t�pico ser um sub-conte�do do conte�do do pai.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_topico_pai")
	private TopicoComunidade topicoPai;

 	/**  Data em que o t�pico foi criado. */
	@CriadoEm
	@Column(name = "data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCadastro;
	
	/** Usu�rio que criou o t�pico. */
	@CriadoPor
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	/** As atividades cadastradas para este t�pico. */
	@OneToMany(mappedBy = "topico")
	private Collection<ConteudoComunidade> conteudoTurma;

	/** As indica��es de refer�ncia cadastradas para este t�pico. */
	@OneToMany(mappedBy = "topico")
	private Collection<IndicacaoReferenciaComunidade> indicacaoReferencia;

	/** Os arquivos cadastrados para este t�pico. */
	@OneToMany(mappedBy = "topico")
	private Collection<ArquivoComunidade> arquivoComunidade;

	/** Ordens dos t�picos. */
	private Integer ordem;
	
	/**
	 * Indica se o T�pico est� vis�vel ou n�o.
	 */
	private boolean visivel = true;
	
	/** N�vel do t�pico na �rvore. */
	@Transient
	private int nivel;

	/** Lista de materiais. */
	@Transient
	private List<MaterialComunidade> materiais = new ArrayList<MaterialComunidade>();

	/** Lista de t�picos filhos. */
	@Transient
	// constru�do no PortalTurmaMBean
	private List<TopicoComunidade> topicosFilhos = new ArrayList<TopicoComunidade>();

	/**
	 * Indica se os membros da comunidade ser�o notificados da inclus�o deste recurso
	 */
	@Transient
	private boolean notificarMembros;
	
	public List<TopicoComunidade> getTopicosFilhos() {
		return topicosFilhos;
	}

	public TopicoComunidade() {
	}

	public TopicoComunidade(int id, String descricao, String conteudo, Date dataCadastro, ComunidadeVirtual comunidade, TopicoComunidade topicoPai, Usuario usuario) {
		this.id = id;
		this.descricao = descricao;
		this.dataCadastro = dataCadastro;
		this.conteudo = conteudo;
		this.comunidade = comunidade;
		this.topicoPai = topicoPai;
		this.usuario = usuario;
	}

	public TopicoComunidade(int id, String descricao, String conteudo, Date dataCadastro, Integer topicoPai, int idComunidade, Usuario usuario) {
		this(id, descricao, conteudo, dataCadastro, new ComunidadeVirtual(idComunidade), (topicoPai != null ? new TopicoComunidade(topicoPai) : null), usuario);
	}
	
	public TopicoComunidade(int id) {
		this.id = id;
	}
	
	/**
	 * Mensagem exibida ao se cadastrar um novo t�pico 
	 */
	public String getMensagemAtividade() {
		return "Novo t�pico da comunidade: " + org.apache.commons.lang.StringUtils.abbreviate(descricao, 50);
	}
	
	/**
	 * Valida os campos do pr�prio objeto
	 * @return
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		if (StringUtils.isEmpty(descricao))
			lista.addErro("O campo descri��o � obrigat�rio.");

		return lista;
	}

	public Collection<ConteudoComunidade> getConteudoTurma() {
		return conteudoTurma;
	}

	public void setConteudoTurma(Collection<ConteudoComunidade> conteudoTurma) {
		this.conteudoTurma = conteudoTurma;
	}

	public Collection<IndicacaoReferenciaComunidade> getIndicacaoReferencia() {
		return indicacaoReferencia;
	}

	public void setIndicacaoReferencia(
			Collection<IndicacaoReferenciaComunidade> indicacaoReferencia) {
		this.indicacaoReferencia = indicacaoReferencia;
	}

	public Collection<ArquivoComunidade> getArquivoComunidade() {
		return arquivoComunidade;
	}

	public void setArquivoComunidade(Collection<ArquivoComunidade> arquivoComunidade) {
		this.arquivoComunidade = arquivoComunidade;
	}

	public List<MaterialComunidade> getMateriais() {
		return materiais;
	}

	public void setMateriais(List<MaterialComunidade> materiais) {
		this.materiais = materiais;
	}

	public void setTopicosFilhos(List<TopicoComunidade> topicosFilhos) {
		this.topicosFilhos = topicosFilhos;
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
		final TopicoComunidade other = (TopicoComunidade) obj;
		if (id != other.id)
			return false;
		return true;
	}

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

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public ComunidadeVirtual getComunidade() {
		return comunidade;
	}

	public void setComunidade(ComunidadeVirtual comunidade) {
		this.comunidade = comunidade;
	}

	public TopicoComunidade getTopicoPai() {
		return topicoPai;
	}

	public void setTopicoPai(TopicoComunidade topicoPai) {
		this.topicoPai = topicoPai;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}

	public boolean isModoCadastro() {
		return modoCadastro;
	}

	public void setModoCadastro(boolean modoCadastro) {
		this.modoCadastro = modoCadastro;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public Integer getOrdem() {
		return ordem;
	}
	
	/** Exibe data no formato dd/MM/yyyy */
	public String getDataFormatada(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(dataCadastro);
	}

	public String getCor() {
		return cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}

	public void setVisivel(boolean visivel) {
		this.visivel = visivel;
	}

	public boolean isVisivel() {
		return visivel;
	}

	public void setNotificarMembros(boolean notificarMembros) {
		this.notificarMembros = notificarMembros;
	}

	public boolean isNotificarMembros() {
		return notificarMembros;
	}
}
