/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/10/2006'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.dominio;

// Generated 13/09/2006 08:49:22 by Hibernate Tools 3.1.0.beta5

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.stricto.dominio.ConceitoNota;
import br.ufrn.sigaa.pessoa.dominio.Servidor;


/**
 * Classe que representa uma proposta de criação de curso de especialização
 * 
 * @author leonardo
 */
@Entity
@Table(name = "proposta_curso_lato", schema = "lato_sensu", uniqueConstraints = {})
public class PropostaCursoLato implements PersistDB {

	/** Chave primária. */
	private int id;

	/** Indica a situação da Prosposta do Curso. */
	private SituacaoProposta situacaoProposta = new SituacaoProposta();

	/** Justificativa para a criação do Curso. */
	private String justificativa;

	/** Importância da criação do Curso. */
	private String importancia;

	/** Descrição dos requisitos necessários para inscrição no Curso. */
	private String requisitos;

	/** Descrição das formas de contato com os responsáveis pelo processo seletivo do Curso. */
	private String contatos;

	/** Início do período de inscrição para o Processo Seletivo. */
	private Date inicioInscSelecao;

	/** Fim do período de inscrição para o Processo Seletivo. */
	private Date fimInscSelecao;

	/** Início do período do Processo Seletivo. */
	private Date inicioSelecao;

	/** Fim do período do Processo Seletivo. */
	private Date fimSelecao;

	/** Frequência mínima obrigatória no Curso. */
	private Float freqObrigatoria;

	/** Descrição dos recursos de instalações necessários para o funcionamento do Curso. */
	private String recursoInstalacao;

	/** Descrição dos recursos de biblioteca necessários para o funcionamento do Curso. */
	private String recursoBiblioteca;

	/** Descrição dos recursos de informática necessários para o funcinamento do Curso. */
	private String recursoInformatica;

	/** Descrição dos recursos de reprografia necessários ao Curso. */
	private String recursoReprografia;

	/** Local onde o Curso ocorrerá */
	private String localCurso;
	
	/** Indica se os dados da seleção devem ser publicados na área pública do Sigaa. */
	private boolean publicar;

	/** Indica o percentual dos recursos destinados à instituição. */
	private Integer percentualInstituicao;

	/** Indica o percentual dos recursos destinados ao coordenador. */
	private Integer percentualCoordenador;
	
	/** Indica o usuário responsável pela realização do cadastro da proposta do Curso. */
	private Usuario usuario;
	
	/** Data em que a Proposta do Curso foi submetida. */
	private Date dataCadastro;
	
	/** Ano do processo de criação do Curso no sistema de protocolos, caso seja Curso antigo. */
	private Integer anoProcesso;
	
	/** Número do processo de criação do Curso no sistema de protocolos, caso seja Curso antigo. */
	private Integer numeroProcesso;

	/** Data da publicação da portaria */
	private Date dataPublicacaoPortaria;
	
	/** Indica a associação entre a Proposta do Curso e uma Forma de Seleção. */
	private Set<FormaSelecaoProposta> formasSelecaoProposta = new HashSet<FormaSelecaoProposta>(0);

	/** Indica a associação entre a Proposta do Curso e uma Forma de Avaliação. */
	private Set<FormaAvaliacaoProposta> formasAvaliacaoProposta = new HashSet<FormaAvaliacaoProposta>(0);

	/** Indica a associação entre o Corpo Docente da Proposta e as disciplinas ministradas por estes. */
	private Set<CorpoDocenteDisciplinaLato> equipesLato = new HashSet<CorpoDocenteDisciplinaLato>(0);

	/** Representa o coordenador do Curso. */
	private Servidor coordenador;

	/** Indica se a avaliação é por NOTA ou CONCEITO. */
	private int metodoAvaliacao = 1;
		
	/** Número da portaria em que o Curso foi aprovado para entrar em andamento. */
	private Integer numeroPortaria;
	
	/** Ano da portaria em que o Curso foi aprovado para entrar em andamento. */
	private Integer anoPortaria;
	
	/** Descrição do público-alvo associado ao Curso. */
	private String publicoAlvo;

	/** Média mínima que deve ser obtida pelos alunos para conseguir aprovação no Curso. */
	private Float mediaMinimaAprovacao;
	
	/** Referência ao arquivo da proposta */
	private Integer idArquivo;
	
	/** Histórico da Proposta de Curso Lato Sensu. */
	private HistoricoSituacao historico = new HistoricoSituacao();
	
	// Constructors

	/** default constructor */
	public PropostaCursoLato() {
	}

	/** minimal constructor */
	public PropostaCursoLato(int idProposta) {
		this.id = idProposta;
	}
	
	public PropostaCursoLato(int idSituacaoProposta, String descricaoSituacaoProposta, int idCoordenador, String nomeCoordenador){
		this.situacaoProposta = new SituacaoProposta(idSituacaoProposta, descricaoSituacaoProposta);
		this.coordenador = new Servidor(idCoordenador, nomeCoordenador);
	}

		// Property accessors
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	@Column(name = "id_proposta", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idProposta) {
		this.id = idProposta;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_situacao_proposta", unique = false, nullable = true, insertable = true, updatable = true)
	public SituacaoProposta getSituacaoProposta() {
		return this.situacaoProposta;
	}

	public void setSituacaoProposta(SituacaoProposta situacaoProposta) {
		this.situacaoProposta = situacaoProposta;
	}

	@Column(name = "justificativa", unique = false, nullable = true, insertable = true, updatable = true)
	public String getJustificativa() {
		return this.justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	@Deprecated
	@Column(name = "importancia", unique = false, nullable = true, insertable = true, updatable = true)
	public String getImportancia() {
		return this.importancia;
	}

	public void setImportancia(String importancia) {
		this.importancia = importancia;
	}

	@Column(name = "requisitos", unique = false, nullable = true, insertable = true, updatable = true)
	public String getRequisitos() {
		return requisitos;
	}

	public void setRequisitos(String requisitos) {
		this.requisitos = requisitos;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "inicio_insc_selecao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getInicioInscSelecao() {
		return this.inicioInscSelecao;
	}

	public void setInicioInscSelecao(Date inicioInscSelecao) {
		this.inicioInscSelecao = inicioInscSelecao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fim_insc_selecao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getFimInscSelecao() {
		return this.fimInscSelecao;
	}

	public void setFimInscSelecao(Date fimInscSelecao) {
		this.fimInscSelecao = fimInscSelecao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "inicio_selecao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getInicioSelecao() {
		return this.inicioSelecao;
	}

	public void setInicioSelecao(Date inicioSelecao) {
		this.inicioSelecao = inicioSelecao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fim_selecao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getFimSelecao() {
		return this.fimSelecao;
	}

	public void setFimSelecao(Date fimSelecao) {
		this.fimSelecao = fimSelecao;
	}

	@Column(name = "freq_obrigatoria", unique = false, nullable = true, insertable = true, updatable = true)
	public Float getFreqObrigatoria() {
		return this.freqObrigatoria;
	}

	public void setFreqObrigatoria(Float freqObrigatoria) {
		this.freqObrigatoria = freqObrigatoria;
	}

	@Deprecated
	@Column(name = "recurso_instalacao", unique = false, nullable = true, insertable = true, updatable = true)
	public String getRecursoInstalacao() {
		return this.recursoInstalacao;
	}

	public void setRecursoInstalacao(String recursoInstalacao) {
		this.recursoInstalacao = recursoInstalacao;
	}

	@Deprecated
	@Column(name = "recurso_biblioteca", unique = false, nullable = true, insertable = true, updatable = true)
	public String getRecursoBiblioteca() {
		return this.recursoBiblioteca;
	}

	public void setRecursoBiblioteca(String recursoBiblioteca) {
		this.recursoBiblioteca = recursoBiblioteca;
	}

	@Deprecated
	@Column(name = "recurso_informatica", unique = false, nullable = true, insertable = true, updatable = true)
	public String getRecursoInformatica() {
		return this.recursoInformatica;
	}

	public void setRecursoInformatica(String recursoInformatica) {
		this.recursoInformatica = recursoInformatica;
	}

	@Deprecated
	@Column(name = "recurso_reprografia", unique = false, nullable = true, insertable = true, updatable = true)
	public String getRecursoReprografia() {
		return this.recursoReprografia;
	}

	public void setRecursoReprografia(String recursoReprografia) {
		this.recursoReprografia = recursoReprografia;
	}


	/**
	 * Retorna as Formas de Avaliação da Proposta
	 */
	@OneToMany(cascade = { }, fetch = FetchType.LAZY, mappedBy = "proposta")
	public Set<FormaAvaliacaoProposta> getFormasAvaliacaoProposta() {
		return formasAvaliacaoProposta;
	}

	/**
	 * Seta as Formas de Avaliação da Proposta
	 */
	public void setFormasAvaliacaoProposta(
			Set<FormaAvaliacaoProposta> formasAvaliacaoProposta) {
		this.formasAvaliacaoProposta = formasAvaliacaoProposta;
	}

	/**
	 * Retorna as Formas de Seleção da Proposta
	 */
	@OneToMany(cascade = { }, fetch = FetchType.LAZY, mappedBy = "proposta")
	public Set<FormaSelecaoProposta> getFormasSelecaoProposta() {
		return formasSelecaoProposta;
	}

	/**
	 * Seta as Formas de Seleção da Proposta
	 */
	public void setFormasSelecaoProposta(
			Set<FormaSelecaoProposta> formasSelecaoProposta) {
		this.formasSelecaoProposta = formasSelecaoProposta;
	}

	@OneToMany(cascade = { }, fetch = FetchType.LAZY, mappedBy = "proposta")
	public Set<CorpoDocenteDisciplinaLato> getEquipesLato() {
		return equipesLato;
	}

	public void setEquipesLato(Set<CorpoDocenteDisciplinaLato> equipesLato) {
		this.equipesLato = equipesLato;
	}

	/**
	 * Implementação do método equals comparando o id, o justificativa, o recurso Instalação, o recurso da Biblioteca,
	 * os recursos de Informática, e os recursos de Reprografia da PropostaCursoLato atual com a passada como parâmetro.
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "justificativa", "importancia", 
				  "recursoInstalacao", "recursoBiblioteca", "recursoInformatica", "recursoReprografia");
	}

	/**
	 * Implementação do método hashCode comparando o id, o justificativa, o recurso Instalação, o recurso da Biblioteca,
	 * os recursos de Informática, e os recursos de Reprografia da PropostaCursoLato atual com a passada como parâmetro.
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, justificativa, importancia, recursoBiblioteca, 
						recursoInformatica, recursoInstalacao, recursoReprografia);
	}

	@Column(name = "publicar", unique = false, nullable = true, insertable = true, updatable = true)
	public boolean isPublicar() {
		return publicar;
	}

	public void setPublicar(boolean publicar) {
		this.publicar = publicar;
	}

	@Column(name = "percentual_coordenador", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getPercentualCoordenador() {
		return percentualCoordenador;
	}

	public void setPercentualCoordenador(Integer percentualCoordenador) {
		this.percentualCoordenador = percentualCoordenador;
	}

	@Column(name = "percentual_instituicao", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getPercentualInstituicao() {
		return percentualInstituicao;
	}

	public void setPercentualInstituicao(Integer percentualInstituicao) {
		this.percentualInstituicao = percentualInstituicao;
	}

	@Column(name = "contatos", unique = false, nullable = true, insertable = true, updatable = true)
	public String getContatos() {
		return contatos;
	}

	public void setContatos(String contatos) {
		this.contatos = contatos;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_usuario", unique = false, nullable = true, insertable = true, updatable = true)
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Column(name = "numero_processo", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getNumeroProcesso() {
		return numeroProcesso;
	}

	public void setNumeroProcesso(Integer numeroProcesso) {
		this.numeroProcesso = numeroProcesso;
	}

	@Column(name = "ano_processo", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getAnoProcesso() {
		return anoProcesso;
	}

	public void setAnoProcesso(Integer anoProcesso) {
		this.anoProcesso = anoProcesso;
	}

	/** Realiza uma busca para ver se uma das situações passa é a da proposta. */
	@Transient
	public boolean isSituacao(int[] situacoes){
		for( int i = 0; i < situacoes.length; i++){
			if(situacaoProposta.getId() == situacoes[i])
				return true;
		}
		return false;
	}

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_coordenador")
	public Servidor getCoordenador() {
		return coordenador;
	}

	public void setCoordenador(Servidor coordenador) {
		this.coordenador = coordenador;
	}

	@Column(name = "metodo_avaliacao", unique = false, nullable = true, insertable = true, updatable = true)
	public int getMetodoAvaliacao() {
		return metodoAvaliacao;
	}
	
	public void setMetodoAvaliacao(int metodoAvaliacao) {
		this.metodoAvaliacao = metodoAvaliacao;
	}

	@Column(name = "media_minima_aprovacao", unique = false, nullable = true, insertable = true, updatable = true)
	public Float getMediaMinimaAprovacao() {
		return mediaMinimaAprovacao;
	}

	public void setMediaMinimaAprovacao(Float mediaMinimaAprovacao) {
		this.mediaMinimaAprovacao = mediaMinimaAprovacao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_publicacao_portaria", unique = false, nullable = true, insertable = true, updatable = true)
	public Date getDataPublicacaoPortaria() {
		return dataPublicacaoPortaria;
	}

	public void setDataPublicacaoPortaria(Date dataPublicacaoPortaria) {
		this.dataPublicacaoPortaria = dataPublicacaoPortaria;
	}

	@Column(name = "numero_portaria")
	public Integer getNumeroPortaria() {
		return numeroPortaria;
	}
	
	public void setNumeroPortaria(Integer numeroPortaria) {
		this.numeroPortaria = numeroPortaria;
	}
	
	@Column(name = "ano_portaria")
	public Integer getAnoPortaria() {
		return anoPortaria;
	}
	
	public void setAnoPortaria(Integer anoPortaria) {
		this.anoPortaria = anoPortaria;
	}
	
	@Column(name = "publico_alvo", unique = false, nullable = true, insertable = true, updatable = true)	
	public String getPublicoAlvo() {
		return publicoAlvo;
	}

	public void setPublicoAlvo(String publicoAlvo) {
		this.publicoAlvo = publicoAlvo;
	}

	@Column(name = "local_curso", unique = false, nullable = true, insertable = true, updatable = true)
	public String getLocalCurso() {
		return localCurso;
	}

	public void setLocalCurso(String localCurso) {
		this.localCurso = localCurso;
	}

	@Column(name = "id_arquivo")
	public Integer getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	@Transient
	public HistoricoSituacao getHistorico() {
		return historico;
	}

	public void setHistorico(HistoricoSituacao historico) {
		this.historico = historico;
	}

	/** Retorna verdadeiro se o método de Avaliação for do tipo Nota */
	@Transient
	public boolean getNota(){
		if (MetodoAvaliacao.NOTA == metodoAvaliacao )
			return true;
		return false;
	}
	
	/** Retorna a média minima para aprovação */
	@Transient
	public String getMediaMinimaAprovacaoDesc(){
		if(metodoAvaliacao == MetodoAvaliacao.NOTA)
			return new Float(getMediaMinimaAprovacao()).toString();
		
		return ConceitoNota.getDescricao(getMediaMinimaAprovacao().doubleValue());
	}
	
}