 /*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 31/01/2008
 *
 */
package br.ufrn.sigaa.ava.dominio;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.ava.forum.relacionamentos.dominio.ForumTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.QuestionarioTurma;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * O t�pico de aula � onde guarda informa��es sobre o que aconteceu em determinada aula ou conjunto de aulas,
 * onde h� o relacionamento entre entidades, tais como arquivos, materiais, etc..
 * 
 *
 * @author Gleydson Lima
 * @author David Pereira
 */

@Entity @HumanName(value="T�pico de Aula", genero='M')
@Table(name = "topico_aula", schema = "ava")
public class TopicoAula implements DominioTurmaVirtual {
    
	/**Chave primaria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_topico_aula", nullable = false)
	private int id;

	/** T�tulo do t�pico de aula */
	private String descricao;
	/** Conte�do do t�pico de aula. N�o � obrigat�rio. */
	private String conteudo;
	
	/** A cor RGB de fundo do t�pico da comunidade. */
	private String cor;
	
	/**
	 * Indica se o T�pico est� vis�vel ou n�o para o aluno.
	 * Apenas os t�picos definidos como vis�vel=true ser�o 
	 * exibidos para os alunos 
	 *   
	 */
	@Column(name="visivel")
	private boolean visivel = true;
	
	/** Indica se o t�pico de aula est� no modo cadastro ou no modo edi��o */
	@Transient
	private boolean modoCadastro;
	
	/** Indica se o t�pico de aula est� ativo ou n�o. */
	private Boolean ativo = Boolean.TRUE;

	/** In�cio do t�pico de aula */
	@Temporal(TemporalType.DATE)
	private Date data;
	
	/** Fim do t�pico de aula */
	@Temporal(TemporalType.DATE)
	private Date fim;
	
	/** Turma do t�pico de aula */
	@ManyToOne (fetch=FetchType.LAZY) @JoinColumn(name = "id_turma")
	private Turma turma;
	
	/**
	 * T�pico pai. Os t�picos de aula s�o organizados em estrutura de �rvore, podendo um t�pico ser um sub-conte�do do conte�do do pai.
	 */
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "id_topico_pai")
	private TopicoAula topicoPai;
	
	/** Docentes que ministrar�o a aula representada por este t�pico. */
	@ManyToMany(fetch = FetchType.LAZY) 
	@JoinTable(schema="ava", name="topico_aula_docente_turma",
			joinColumns=@JoinColumn(name="id_topico_aula"),
			inverseJoinColumns=@JoinColumn(name="id_docente_turma"))
	private List<DocenteTurma> docentesTurma;
	
	/** Usu�rios que ministrar�o a aula. */
	@Transient
	private Collection<Usuario> docentes;

 	/**  Data em que o t�pico foi criado. */
	@CriadoEm @Column(name="data_cadastro")
	private Date dataCadastro;
	
	/** Usu�rio que criou o t�pico. */
	@CriadoPor @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="id_usuario")
	private Usuario usuario;
	
	/** As atividades cadastradas para este t�pico de aula. */
	@OneToMany(mappedBy="aula", cascade=CascadeType.REMOVE)	
	private Collection<ConteudoTurma> conteudoTurma;
	
	/** As indica��es de refer�ncia cadastradas para este t�pico de aula. */
	@OneToMany(mappedBy="aula", cascade=CascadeType.REMOVE)	
	private Collection<IndicacaoReferencia> indicacaoReferencia;

	/** Os arquivos cadastrados para este t�pico de aula. */
	@OneToMany(mappedBy="aula", cascade=CascadeType.REMOVE)
	private Collection<ArquivoTurma> arquivoTurma;	
	
	/** Representa uma aula extra. */
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_aula_extra")
	private AulaExtra aulaExtra;
	
	/** N�vel da �rvore dos t�picos de aula */
	@Transient
	private int nivel;

	/** Lista de materiais associados ao t�pico de aula */
	@Transient
	private List<AbstractMaterialTurma> materiais = new ArrayList<AbstractMaterialTurma>();

	/** Lista dos t�picos filhos do t�pico de aula. */
	@Transient
	// constru�do no PortalTurmaMBean
	private List<TopicoAula> topicosFilhos = new ArrayList<TopicoAula>();
	
	/** Os f�runs cadastrados para este t�pico de aula. */
	@Transient
	private List <ForumTurma> forums = new ArrayList <ForumTurma> ();

	/** As enquetes cadastradas para este t�pico de aula. */
	@Transient
	private List <Enquete> enquetes = new ArrayList <Enquete> ();
	
	/** Os questionarios cadastrados para este t�pico de aula. */
	@Transient
	private List <QuestionarioTurma> questionarios = new ArrayList <QuestionarioTurma> ();
	
	/** Os v�deos cadastrados para este t�pico de aula. */
	@Transient
	private List <VideoTurma> videos = new ArrayList <VideoTurma> ();
	
	/** Os r�tulos cadastrados para este t�pico de aula. */
	@Transient
	private List <RotuloTurma> rotulos = new ArrayList <RotuloTurma> ();
	
	// Atributos usados na importa��o de dados
	/** Indica se o t�pico de aula foi selecionado para importa��o. */
	@Transient
	private boolean selecionado;
	/** Data inicial em Long do novo t�pico importado. */
	@Transient 
	private Long dataInicio;
	/** Data final em long do novo t�pico importado. */
	@Transient 
	private Long dataFim;
	
	/** Se ocorrer� aula na data determinada. */
	 @Column(name="aula_cancelada")
	private boolean aulaCancelada;
	
	/** Lista de t�picos filhos. */
	public List<TopicoAula> getTopicosFilhos() {
		return topicosFilhos;
	}
	
	public TopicoAula() { }
	
	public TopicoAula(int id, String descricao, String conteudo, Date data, Date fim, Turma turma, TopicoAula topicoPai, boolean visivel) {
		this.id = id;
		this.descricao = descricao;
		this.conteudo = conteudo;
		this.data = data;
		this.fim = fim;
		this.topicoPai = topicoPai;
		this.turma = turma;
		this.visivel = visivel;
	}
	
	public TopicoAula(int id, String descricao, String conteudo, Date data, Date fim, Integer topicoPai, int idTurma, boolean visivel) {
		this(id, descricao, conteudo, data, fim, new Turma(idTurma), (topicoPai != null ? new TopicoAula(topicoPai) : null), visivel );
	}
	
	public TopicoAula(int id, String descricao, String conteudo, Date data, Date fim, Turma turma, TopicoAula topicoPai, boolean visivel, boolean aulaCancelada) {
		this(id, descricao, conteudo, data, fim, turma, topicoPai, visivel);
		this.aulaCancelada = aulaCancelada;
	}
	
	public TopicoAula(int id, String descricao, String conteudo, Date data, Date fim, Turma turma, TopicoAula topicoPai, boolean visivel, boolean aulaCancelada,String cor) {
		this(id, descricao, conteudo, data, fim, turma, topicoPai, visivel);
		this.aulaCancelada = aulaCancelada;
		this.cor = cor;
	}
	
	public TopicoAula(int id, String descricao, String conteudo, Date data, Date fim, Integer topicoPai, int idTurma, boolean visivel, Integer ...idsDocenteTurma) {
		this.id = id;
		this.descricao = descricao;
		this.conteudo = conteudo;
		this.data = data;
		this.fim = fim;
		this.topicoPai = topicoPai != null ? new TopicoAula(topicoPai) : null;
		this.turma = new Turma (idTurma);
		this.visivel = visivel;
		if(idsDocenteTurma != null) {
			for (Integer idDocente : idsDocenteTurma) {
				adicionarDocenteTurma(new DocenteTurma(idDocente));
			}
		}
	}

	public TopicoAula(int id) {
		this.id = id;
	}
	
	public boolean isVisivel() {
		return visivel;
	}

	public void setVisivel(boolean visivel) {
		this.visivel = visivel;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		if (StringUtils.isEmpty(descricao))
			lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Descri��o");

		return lista;
	}

	@Override
	public boolean equals (Object o){
		return EqualsUtil.testEquals(this, o, "id");
	}
	
	@Override
	public int hashCode (){
		return HashCodeUtil.hashAll(id);
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

	public Date getData() {
		return data;
	}

	/** Exibe data no formato dd/MM/yyyy */
	public String getDataFormatada(){
		if ( data != null ){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			return sdf.format(data);
		} else return null;
	}
	
	/** Exibe data fim no formato dd/MM/yyyy */
	public String getDataFimFormatada(){
		if ( fim != null ){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			return sdf.format(fim);
		} else return null;
	}
	
	public void setData(Date data) {
		this.data = data;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public TopicoAula getTopicoPai() {
		return topicoPai;
	}

	public void setTopicoPai(TopicoAula topicoPai) {
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

	public List<AbstractMaterialTurma> getMateriais() {
		return materiais;
	}

	public void setMateriais(List<AbstractMaterialTurma> materiais) {
		this.materiais = materiais;
	}
	
	public void setTopicosFilhos(List<TopicoAula> topicosFilhos) {
		this.topicosFilhos = topicosFilhos;
	}

	public Collection<ConteudoTurma> getConteudoTurma() {
		return conteudoTurma;
	}

	public void setConteudoTurma(Collection<ConteudoTurma> conteudoTurma) {
		this.conteudoTurma = conteudoTurma;
	}

	public Collection<IndicacaoReferencia> getIndicacaoReferencia() {
		return indicacaoReferencia;
	}

	public void setIndicacaoReferencia(
			Collection<IndicacaoReferencia> indicacaoReferencia) {
		this.indicacaoReferencia = indicacaoReferencia;
	}

	public Collection<ArquivoTurma> getArquivoTurma() {
		return arquivoTurma;
	}

	public void setArquivoTurma(Collection<ArquivoTurma> arquivoTurma) {
		this.arquivoTurma = arquivoTurma;
	}

	public boolean isModoCadastro() {
		return modoCadastro;
	}

	public void setModoCadastro(boolean modoCadastro) {
		this.modoCadastro = modoCadastro;
	}

	public String getMensagemAtividade() {
		return "Novo T�pico de Aula: " + descricao;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public List<ForumTurma> getForums() {
		return forums;
	}

	public void setForums(List<ForumTurma> forums) {
		this.forums = forums;
	}

	public Collection<DocenteTurma> getDocentesTurma() {
		return docentesTurma;
	}

	public void setDocentesTurma(List<DocenteTurma> docentesTurma) {
		this.docentesTurma = docentesTurma;
	}

	public Collection<Usuario> getDocentes() {
		return docentes;
	}

	public void setDocentes(Collection<Usuario> docentes) {
		this.docentes = docentes;
	}

	public List<Enquete> getEnquetes() {
		return enquetes;
	}

	public void setEnquetes(List<Enquete> enquetes) {
		this.enquetes = enquetes;
	}

	public void setDataInicio(Long dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Long getDataInicio() {
		return dataInicio;
	}

	public void setDataFim(Long dataFim) {
		this.dataFim = dataFim;
	}

	public Long getDataFim() {
		return dataFim;
	}

	public List<QuestionarioTurma> getQuestionarios() {
		return questionarios;
	}

	public void setQuestionarios(List<QuestionarioTurma> questionarios) {
		this.questionarios = questionarios;
	}
	
	public List<VideoTurma> getVideos() {
		return videos;
	}

	public void setVideos(List<VideoTurma> videos) {
		this.videos = videos;
	}

	/**
	 * Adiciona o {@link DocenteTurma} passado na listagem de DocentesTurma do {@link TopicoAula}.
	 * 
	 * @param dt
	 */
	public void adicionarDocenteTurma(DocenteTurma dt) {
		if(docentesTurma == null)
			docentesTurma = new ArrayList<DocenteTurma>();
		
		if(!containsDocenteTurma(dt))
			docentesTurma.add(dt);
	}
	
	/**
	 * Verifica, pelo id do {@link DocenteTurma} se ele j� foi adicionado na listagem de {@link #docentesTurma}.
	 * 
	 * @param dt
	 * @return
	 */
	public boolean containsDocenteTurma(DocenteTurma dt) {
		if(docentesTurma == null)
			return false;
		
		for (DocenteTurma doc : docentesTurma) {
			if(dt.getId() == doc.getId())
				return true;
		}
		
		return false;
	}
	
	/**
	 * Adiciona o {@link Usuario} passado na listagem de docentes do {@link TopicoAula}.
	 * 
	 * @param u
	 */
	public void adicionarDocente(Usuario u) {
		if(docentes == null)
			docentes = new ArrayList<Usuario>();
		
		if(!docentes.contains(u))
			docentes.add(u);
	}

	public void setAulaCancelada(boolean aulaCancelada) {
		this.aulaCancelada = aulaCancelada;
	}

	public boolean isAulaCancelada() {
		return aulaCancelada;
	}

	public void setAulaExtra(AulaExtra aulaExtra) {
		this.aulaExtra = aulaExtra;
	}

	public AulaExtra getAulaExtra() {
		return aulaExtra;
	}

	public List<RotuloTurma> getRotulos() {
		return rotulos;
	}

	public void setRotulos(List<RotuloTurma> rotulos) {
		this.rotulos = rotulos;
	}
	
	public String getCor() {
		return cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}


}
