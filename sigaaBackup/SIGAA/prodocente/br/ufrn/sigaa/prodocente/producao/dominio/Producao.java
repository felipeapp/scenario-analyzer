/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Classe que implementa os dados gerais de todas as produ��es intelectuais de docentes da institui��o
 *
 * @author Gleydson
 *
 */
@Entity
@Table(name = "Producao", schema = "prodocente")
@Inheritance(strategy = InheritanceType.JOINED)
public class Producao implements Validatable {

	/** Chave prim�ria. */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_producao", nullable = false)
	private int id;

	/** T�tulo da produ��o */
	@Column(name = "titulo", columnDefinition=HibernateUtils.TEXT_COLUMN_DEFINITION)
	private String titulo;

	/**
	 * Ao remover as produ��es as mesmas n�o ser�o removidas da base de dados,
	 * apenas o campo ativo ser� marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo;

	/** Tipo da produ��o cadastrada */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_producao")
	private TipoProducao tipoProducao = new TipoProducao();

	/** Tipo da participa��o do docente na produ��o */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_participacao")
	private TipoParticipacao tipoParticipacao = new TipoParticipacao(TipoParticipacao.AUTOR_GENERICO);

	/** �rea de conhecimento da Produ��o */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_area")
	private AreaConhecimentoCnpq area = new AreaConhecimentoCnpq();
	
	/** Sub-�rea de conhecimento da produ��o */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_sub_area")
	private AreaConhecimentoCnpq subArea = new AreaConhecimentoCnpq();

	/** Servidor que realizou o cadastro da produ��o */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_servidor")
	private Servidor servidor ;

	/** Valida��es */
	private Boolean validado;

	/** Usu�rio que validou a produ��o cadastrada */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_validador")
	private Usuario usuarioValidador;

	/** Data que foi realizada a valida��o da produ��o */
	@Column(name = "data_validacao")
	private Date dataValidacao;

	/** Consolida��o das valida��es */
	private Boolean consolidado;

	/** Usu�rio que realizou a consolida��o da produ��o */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_consolidador")
	private Usuario usuarioConsolidador;
	
	/** Data da consolida��o da produ��o */
	@Column(name = "data_consolidacao")
	private Date dataConsolidacao;

	/** Informa��o referente a produ��o */
	private String informacao;

	/** Id do arquivo importado */
	@Column(name = "id_arquivo")
	private Integer idArquivo;

	/** Data da produ��o */
	@Column(name="data_producao")
	private Date dataProducao;

	/** Data da cria��o da produ��o */
	@CriadoEm
	@Column(name="data_cadastro")
	private Date dataCadastro;

	/** Registro de entrada do usu�rio que cadastro a produ��o */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro")
	private RegistroEntrada registro;

	/** N�mero de seq��ncia da publica��o no curr�culo lattes */
	@Column(name = "sequencia_producao", updatable=false)
	private Integer sequenciaProducao;

	/** Armazena o id da leitura do xml, caso a produ��o seja oriunda da importa��o do lattes */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_leitura_xml")
	private LeituraXML leituraXml;

	/** Representa a valida��o da produ��o */
	@OneToMany(cascade = {}, fetch = FetchType.LAZY, mappedBy = "producao")
	private Set<ValidacaoProducao> validacoesproducao = new HashSet<ValidacaoProducao>(
			0);
	
	/** Representa o ano de Refer�ncia da Produ��o */
	@Column(name = "ano_referencia")
	private Integer anoReferencia;

	/** Apresenta a informa��o se o anexo deve ser visualizado na parte p�blica */
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

	/** Retorna a descri��o da situa��o da produ��o */
	public String getSituacaoDesc() {
		if (validado == null) {
			return "Pendente Valida��o";
		} else {
			if (validado) {
				return "Validado";
			} else {
				return "Valida��o Negada";
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
	 * Ao remover as produ��es as mesmas n�o ser�o removidas da base de dados,
	 * apenas o campo ativo ser� marcado como FALSE
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public Boolean getAtivo()
	{
		return this.ativo;
	}

	/**
	 * Ao remover as produ��es as mesmas n�o ser�o removidas da base de dados,
	 * apenas o campo ativo ser� marcado como FALSE
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
		ValidatorUtil.validateRequired(getTitulo(), "T�tulo da Produ��o", lista);
		ValidatorUtil.validateRequired(getTipoProducao(), "Tipo da Produ��o", lista);
		ValidatorUtil.validateRequired(getTipoParticipacao(), "Tipo da Participa��o", lista);
		ValidatorUtil.validateRequired(getArea(), "�rea de Conhecimento CNPQ", lista);
		ValidatorUtil.validateRequired(getSubArea(), "Sub-�rea de Conhecimento CNPQ", lista);
		ValidatorUtil.validateRequired(getDataProducao(), "Data de Produ��o", lista);
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

	/** Retorna a classifica��o da Valida��o */
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

	/** Retorna a classifica��o da consolida��o */
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
	 * Retorna uma string com o semestre da produ��o (1� ou 2� semestre)
	 * de acordo com a data da produ��o
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