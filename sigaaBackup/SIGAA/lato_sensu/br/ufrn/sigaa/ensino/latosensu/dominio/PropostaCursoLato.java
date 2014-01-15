/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Classe que representa uma proposta de cria��o de curso de especializa��o
 * 
 * @author leonardo
 */
@Entity
@Table(name = "proposta_curso_lato", schema = "lato_sensu", uniqueConstraints = {})
public class PropostaCursoLato implements PersistDB {

	/** Chave prim�ria. */
	private int id;

	/** Indica a situa��o da Prosposta do Curso. */
	private SituacaoProposta situacaoProposta = new SituacaoProposta();

	/** Justificativa para a cria��o do Curso. */
	private String justificativa;

	/** Import�ncia da cria��o do Curso. */
	private String importancia;

	/** Descri��o dos requisitos necess�rios para inscri��o no Curso. */
	private String requisitos;

	/** Descri��o das formas de contato com os respons�veis pelo processo seletivo do Curso. */
	private String contatos;

	/** In�cio do per�odo de inscri��o para o Processo Seletivo. */
	private Date inicioInscSelecao;

	/** Fim do per�odo de inscri��o para o Processo Seletivo. */
	private Date fimInscSelecao;

	/** In�cio do per�odo do Processo Seletivo. */
	private Date inicioSelecao;

	/** Fim do per�odo do Processo Seletivo. */
	private Date fimSelecao;

	/** Frequ�ncia m�nima obrigat�ria no Curso. */
	private Float freqObrigatoria;

	/** Descri��o dos recursos de instala��es necess�rios para o funcionamento do Curso. */
	private String recursoInstalacao;

	/** Descri��o dos recursos de biblioteca necess�rios para o funcionamento do Curso. */
	private String recursoBiblioteca;

	/** Descri��o dos recursos de inform�tica necess�rios para o funcinamento do Curso. */
	private String recursoInformatica;

	/** Descri��o dos recursos de reprografia necess�rios ao Curso. */
	private String recursoReprografia;

	/** Local onde o Curso ocorrer� */
	private String localCurso;
	
	/** Indica se os dados da sele��o devem ser publicados na �rea p�blica do Sigaa. */
	private boolean publicar;

	/** Indica o percentual dos recursos destinados � institui��o. */
	private Integer percentualInstituicao;

	/** Indica o percentual dos recursos destinados ao coordenador. */
	private Integer percentualCoordenador;
	
	/** Indica o usu�rio respons�vel pela realiza��o do cadastro da proposta do Curso. */
	private Usuario usuario;
	
	/** Data em que a Proposta do Curso foi submetida. */
	private Date dataCadastro;
	
	/** Ano do processo de cria��o do Curso no sistema de protocolos, caso seja Curso antigo. */
	private Integer anoProcesso;
	
	/** N�mero do processo de cria��o do Curso no sistema de protocolos, caso seja Curso antigo. */
	private Integer numeroProcesso;

	/** Data da publica��o da portaria */
	private Date dataPublicacaoPortaria;
	
	/** Indica a associa��o entre a Proposta do Curso e uma Forma de Sele��o. */
	private Set<FormaSelecaoProposta> formasSelecaoProposta = new HashSet<FormaSelecaoProposta>(0);

	/** Indica a associa��o entre a Proposta do Curso e uma Forma de Avalia��o. */
	private Set<FormaAvaliacaoProposta> formasAvaliacaoProposta = new HashSet<FormaAvaliacaoProposta>(0);

	/** Indica a associa��o entre o Corpo Docente da Proposta e as disciplinas ministradas por estes. */
	private Set<CorpoDocenteDisciplinaLato> equipesLato = new HashSet<CorpoDocenteDisciplinaLato>(0);

	/** Representa o coordenador do Curso. */
	private Servidor coordenador;

	/** Indica se a avalia��o � por NOTA ou CONCEITO. */
	private int metodoAvaliacao = 1;
		
	/** N�mero da portaria em que o Curso foi aprovado para entrar em andamento. */
	private Integer numeroPortaria;
	
	/** Ano da portaria em que o Curso foi aprovado para entrar em andamento. */
	private Integer anoPortaria;
	
	/** Descri��o do p�blico-alvo associado ao Curso. */
	private String publicoAlvo;

	/** M�dia m�nima que deve ser obtida pelos alunos para conseguir aprova��o no Curso. */
	private Float mediaMinimaAprovacao;
	
	/** Refer�ncia ao arquivo da proposta */
	private Integer idArquivo;
	
	/** Hist�rico da Proposta de Curso Lato Sensu. */
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
	 * Retorna as Formas de Avalia��o da Proposta
	 */
	@OneToMany(cascade = { }, fetch = FetchType.LAZY, mappedBy = "proposta")
	public Set<FormaAvaliacaoProposta> getFormasAvaliacaoProposta() {
		return formasAvaliacaoProposta;
	}

	/**
	 * Seta as Formas de Avalia��o da Proposta
	 */
	public void setFormasAvaliacaoProposta(
			Set<FormaAvaliacaoProposta> formasAvaliacaoProposta) {
		this.formasAvaliacaoProposta = formasAvaliacaoProposta;
	}

	/**
	 * Retorna as Formas de Sele��o da Proposta
	 */
	@OneToMany(cascade = { }, fetch = FetchType.LAZY, mappedBy = "proposta")
	public Set<FormaSelecaoProposta> getFormasSelecaoProposta() {
		return formasSelecaoProposta;
	}

	/**
	 * Seta as Formas de Sele��o da Proposta
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
	 * Implementa��o do m�todo equals comparando o id, o justificativa, o recurso Instala��o, o recurso da Biblioteca,
	 * os recursos de Inform�tica, e os recursos de Reprografia da PropostaCursoLato atual com a passada como par�metro.
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "justificativa", "importancia", 
				  "recursoInstalacao", "recursoBiblioteca", "recursoInformatica", "recursoReprografia");
	}

	/**
	 * Implementa��o do m�todo hashCode comparando o id, o justificativa, o recurso Instala��o, o recurso da Biblioteca,
	 * os recursos de Inform�tica, e os recursos de Reprografia da PropostaCursoLato atual com a passada como par�metro.
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

	/** Realiza uma busca para ver se uma das situa��es passa � a da proposta. */
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

	/** Retorna verdadeiro se o m�todo de Avalia��o for do tipo Nota */
	@Transient
	public boolean getNota(){
		if (MetodoAvaliacao.NOTA == metodoAvaliacao )
			return true;
		return false;
	}
	
	/** Retorna a m�dia minima para aprova��o */
	@Transient
	public String getMediaMinimaAprovacaoDesc(){
		if(metodoAvaliacao == MetodoAvaliacao.NOTA)
			return new Float(getMediaMinimaAprovacao()).toString();
		
		return ConceitoNota.getDescricao(getMediaMinimaAprovacao().doubleValue());
	}
	
}