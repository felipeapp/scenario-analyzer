/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '26/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.dominio;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.lattes.LeituraXML;

/**
 * Classe que implementa os dados gerais de todas as produções intelectuais de docentes da instituição
 *
 * @author Gleydson
 *
 */
@Entity
@Table(name = "Producao", schema = "prodocente")
@Inheritance(strategy = InheritanceType.JOINED)
public class Producao implements Validatable {

	/** Chave primária. */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_producao", nullable = false)
	private int id;

	/** Título da produção */
	@Column(name = "titulo", columnDefinition=HibernateUtils.TEXT_COLUMN_DEFINITION)
	private String titulo;

	/**
	 * Ao remover as produções as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo;

	/** Tipo da produção cadastrada */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_producao")
	private TipoProducao tipoProducao = new TipoProducao();

	/** Tipo da participação do docente na produção */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_participacao")
	private TipoParticipacao tipoParticipacao = new TipoParticipacao(TipoParticipacao.AUTOR_GENERICO);

	/** Área de conhecimento da Produção */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_area")
	private AreaConhecimentoCnpq area = new AreaConhecimentoCnpq();
	
	/** Sub-área de conhecimento da produção */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_sub_area")
	private AreaConhecimentoCnpq subArea = new AreaConhecimentoCnpq();

	/** Servidor que realizou o cadastro da produção */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_servidor")
	private Servidor servidor ;

	/** Validações */
	private Boolean validado;

	/** Usuário que validou a produção cadastrada */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_validador")
	private Usuario usuarioValidador;

	/** Data que foi realizada a validação da produção */
	@Column(name = "data_validacao")
	private Date dataValidacao;

	/** Consolidação das validações */
	private Boolean consolidado;

	/** Usuário que realizou a consolidação da produção */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_consolidador")
	private Usuario usuarioConsolidador;
	
	/** Data da consolidação da produção */
	@Column(name = "data_consolidacao")
	private Date dataConsolidacao;

	/** Informação referente a produção */
	private String informacao;

	/** Id do arquivo importado */
	@Column(name = "id_arquivo")
	private Integer idArquivo;

	/** Data da produção */
	@Column(name="data_producao")
	private Date dataProducao;

	/** Data da criação da produção */
	@CriadoEm
	@Column(name="data_cadastro")
	private Date dataCadastro;

	/** Registro de entrada do usuário que cadastro a produção */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro")
	private RegistroEntrada registro;

	/** Número de seqüência da publicação no currículo lattes */
	@Column(name = "sequencia_producao", updatable=false)
	private Integer sequenciaProducao;

	/** Armazena o id da leitura do xml, caso a produção seja oriunda da importação do lattes */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_leitura_xml")
	private LeituraXML leituraXml;

	/** Representa a validação da produção */
	@OneToMany(cascade = {}, fetch = FetchType.LAZY, mappedBy = "producao")
	private Set<ValidacaoProducao> validacoesproducao = new HashSet<ValidacaoProducao>(
			0);
	
	/** Representa o ano de Referência da Produção */
	@Column(name = "ano_referencia")
	private Integer anoReferencia;

	/** Apresenta a informação se o anexo deve ser visualizado na parte pública */
	@Column(name = "exibir")
	private boolean exibir;

	public Producao(int id, String titulo, String tipoDescricao, int tipoId,
			String servidorNome, int servidorId, Boolean validado,
			Date dataValidacao) {
		this.id = id;
		this.titulo = titulo;
		//this.tipoProducao = new TipoProducao(tipoId, tipoDescricao);
		servidor = new Servidor(servidorId, servidorNome);
		this.validado = validado;
		this.dataValidacao = dataValidacao;
	}

	public Producao(int id, String titulo, int idServidor, String nomeServidor, int idTipo, String tipoProducao, Integer ano, String sigla, Integer idArquivo ) {
		this.id = id;
		this.titulo = titulo;
		servidor = new Servidor(idServidor, nomeServidor);
		Unidade unidade = new Unidade();
		unidade.setSigla(sigla);
		servidor.setUnidade(unidade);
		this.tipoProducao = new TipoProducao(idTipo, tipoProducao);
		this.anoReferencia = ano;
		this.idArquivo = idArquivo;
	}

	public Producao(int id, String titulo, int idServidor, String nomeServidor, int idTipo, String tipoProducao, Integer ano, String sigla, Integer idArquivo, String login ) {
		this.id = id;
		this.titulo = titulo;
		servidor = new Servidor(idServidor, nomeServidor);
		Unidade unidade = new Unidade();
		unidade.setSigla(sigla);
		servidor.setUnidade(unidade);
		this.tipoProducao = new TipoProducao(idTipo, tipoProducao);
		this.anoReferencia = ano;
		this.idArquivo = idArquivo;
		servidor.setPrimeiroUsuario(new Usuario());
		servidor.getPrimeiroUsuario().setLogin(login);
	}	

	/** Retorna a descrição da situação da produção */
	public String getSituacaoDesc() {
		if (validado == null) {
			return "Pendente Validação";
		} else {
			if (validado) {
				return "Validado";
			} else {
				return "Validação Negada";
			}
		}
	}

	public Producao() {

	}

	public Producao(Integer idProducao) {
		this.id = idProducao;
	}

	public AreaConhecimentoCnpq getArea() {
		return area;
	}

	public void setArea(AreaConhecimentoCnpq area) {
		this.area = area;
	}

	public Date getDataValidacao() {
		return dataValidacao;
	}

	public void setDataValidacao(Date dataValidacao) {
		this.dataValidacao = dataValidacao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Boolean getValidado() {
		return validado;
	}

	public void setValidado(Boolean validado) {
		this.validado = validado;
	}

	/**
	 * Ao remover as produções as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public Boolean getAtivo()
	{
		return this.ativo;
	}

	/**
	 * Ao remover as produções as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public void setAtivo(Boolean ativo)
	{
		this.ativo = ativo;
	}

	public String getInformacao() {
		return informacao;
	}

	public void setInformacao(String informacao) {
		this.informacao = informacao;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public AreaConhecimentoCnpq getSubArea() {
		return subArea;
	}

	public void setSubArea(AreaConhecimentoCnpq subArea) {
		this.subArea = subArea;
	}

	public TipoParticipacao getTipoParticipacao() {
		return tipoParticipacao;
	}

	public void setTipoParticipacao(TipoParticipacao tipoParticipacao) {
		this.tipoParticipacao = tipoParticipacao;
	}

	public TipoProducao getTipoProducao() {
		return tipoProducao;
	}

	public void setTipoProducao(TipoProducao tipoProducao) {
		this.tipoProducao = tipoProducao;
	}


	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Usuario getUsuarioValidador() {
		return usuarioValidador;
	}

	public void setUsuarioValidador(Usuario usuarioValidador) {
		this.usuarioValidador = usuarioValidador;
	}

	public Set<ValidacaoProducao> getValidacoesproducao() {
		return validacoesproducao;
	}

	public void setValidacoesproducao(Set<ValidacaoProducao> validacoesproducao) {
		this.validacoesproducao = validacoesproducao;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getTitulo(), "Título da Produção", lista);
		ValidatorUtil.validateRequired(getTipoProducao(), "Tipo da Produção", lista);
		ValidatorUtil.validateRequired(getTipoParticipacao(), "Tipo da Participação", lista);
		ValidatorUtil.validateRequired(getArea(), "Área de Conhecimento CNPQ", lista);
		ValidatorUtil.validateRequired(getSubArea(), "Sub-área de Conhecimento CNPQ", lista);
		ValidatorUtil.validateRequired(getDataProducao(), "Data de Produção", lista);
		return lista;
	}

	public Integer getAnoReferencia() {
		return anoReferencia;
	}

	public void setAnoReferencia(Integer anoReferencia) {
		this.anoReferencia = anoReferencia;
	}

	public Integer getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	public Date getDataProducao() {
		return dataProducao;
	}

	public void setDataProducao(Date dataProducao) {
		this.dataProducao = dataProducao;
	}

	public RegistroEntrada getRegistro() {
		return registro;
	}

	public void setRegistro(RegistroEntrada registro) {
		this.registro = registro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Integer getSequenciaProducao() {
		return sequenciaProducao;
	}

	public void setSequenciaProducao(Integer sequenciaProducao) {
		this.sequenciaProducao = sequenciaProducao;
	}

	public LeituraXML getLeituraXml() {
		return leituraXml;
	}

	public void setLeituraXml(LeituraXML leituraXml) {
		this.leituraXml = leituraXml;
	}

	public Boolean getConsolidado() {
		return this.consolidado;
	}

	public void setConsolidado(Boolean consolidado) {
		this.consolidado = consolidado;
	}

	public Usuario getUsuarioConsolidador() {
		return this.usuarioConsolidador;
	}

	public void setUsuarioConsolidador(Usuario usuarioConsolidador) {
		this.usuarioConsolidador = usuarioConsolidador;
	}

	public Date getDataConsolidacao() {
		return this.dataConsolidacao;
	}

	public void setDataConsolidacao(Date dataConsolidacao) {
		this.dataConsolidacao = dataConsolidacao;
	}

	public boolean isExibir() {
		return exibir;
	}

	public void setExibir(boolean exibir) {
		this.exibir = exibir;
	}

	/** Retorna a classificação da Validação */
	public String getClassValidacao() {
		if ( validado == null ) {
			return "pendente";
		} else {
			if ( validado ) {
				return "validado";
			} else {
				return "invalidado";
			}
		}
	}

	/** Retorna a classificação da consolidação */
	public String getClassConsolidacao() {
		if ( consolidado == null ) {
			return "pendente";
		} else {
			if ( consolidado ) {
				return "validado";
			} else {
				return "invalidado";
			}
		}
	}

	public String getDescricaoCompleta() {
		return getTitulo();
	}

	/**
	 * Retorna uma string com o semestre da produção (1º ou 2º semestre)
	 * de acordo com a data da produção
	 * @return
	 */
	public String getSemestre(){
		String ano = " ";
		String periodo = " ";
		if(dataProducao != null){
			Calendar c = Calendar.getInstance();
			c.setTime(dataProducao);
			ano = new Integer(c.get(Calendar.YEAR)).toString();
			periodo = c.get(Calendar.MONTH) <= Calendar.JUNE ? "1" : "2";
		}
		if(anoReferencia != null)
			return anoReferencia.toString() + "." + periodo;
		else
			return ano + "." + periodo;
	}
}