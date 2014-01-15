/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.pessoa.dominio;

// Generated 13/09/2006 08:49:22 by Hibernate Tools 3.1.0.beta5

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.rh.dominio.AtividadeServidor;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.rh.dominio.Cargo;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.rh.dominio.ClasseFuncional;
import br.ufrn.rh.dominio.Escolaridade;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.rh.dominio.Situacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Servidor com os dados específicos do SIGAA
 */
@Entity
@Table(name = "servidor", schema = "rh", uniqueConstraints = { @UniqueConstraint(columnNames = { "siape" }) })
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Servidor implements PersistDB, Docente, Comparable<Servidor> {

	// Fields

	private int id;

	/**
	 * <p>Situação do servidor que é obtida no SIAPE. </p>
	 * 
	 * <p>Na maioria dos casos vai está com valor igual ou equivalente a variável Ativo, mas pode assumir valores diferentes.</p>
	 */
	private Situacao situacaoServidor;

	private AtividadeServidor atividade;

	private Cargo cargo;

	private Escolaridade escolaridade;

	private Categoria categoria;

	private int siape;

	private Pessoa pessoa = new Pessoa();

	private Character digitoSiape;

	/**
	 * Representa a situação interna do servidor na instituição.
	 */
	private Ativo ativo;

	/** Código da unidade de lotação do servidor no SIAPE */
	private String uorg;

	private String lotacao;

	private Date ultimaAtualizacao;

	private double auxilioTransporte;

	private Unidade unidade;

	private Formacao formacao;

	private Integer regimeTrabalho;

	private boolean dedicacaoExclusiva;

	private String tipoVinculo;

	private Integer idFoto;

	private Integer idPerfil;

	private PerfilPessoa perfil;

	private Usuario primeiroUsuario;

	private Date dataDesligamento;

	private boolean colaboradorVoluntario;

	/** Data em que o servidor foi admitido na instituição */
	private Date dataAdmissao;
	
	/** Classe funcional do servidor: A, B, C, D, E, Adjunto, Assistente, etc */
	protected ClasseFuncional classeFuncional;
	
	// Constructors

	/** default constructor */
	public Servidor() {
		pessoa = new Pessoa();
	}

	/** default minimal constructor */
	public Servidor(int id) {
		this.id = id;
	}

	public Servidor(int id, String nome) {
		this.id = id;
		if (pessoa == null) {
			pessoa = new Pessoa();
		}
		pessoa.setNome(nome);
	}

	public Servidor(int id, String nome, int siape) {
		this.id = id;
		this.siape = siape;
		if (pessoa == null) {
			pessoa = new Pessoa();
		}
		pessoa.setNome(nome);
	}

	public Servidor(int id, String nome, int siape, String ativo) {
		this.id = id;
		this.siape = siape;
		if (pessoa == null) {
			pessoa = new Pessoa();
		}
		pessoa.setNome(nome);
		this.ativo = new Ativo();
		this.ativo.setDescricao(ativo);
	}

	/** minimal constructor */
	public Servidor(int idServidor, Cargo cargo, int siape, Pessoa pessoa,
			double auxilioTransporte) {
		this.id = idServidor;
		this.cargo = cargo;
		this.siape = siape;
		this.pessoa = pessoa;
		this.auxilioTransporte = auxilioTransporte;
	}

	// Property accessors
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
				parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_servidor", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idServidor) {
		this.id = idServidor;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_situacao", unique = false, nullable = true, insertable = true, updatable = true)
	public Situacao getSituacaoServidor() {
		return this.situacaoServidor;
	}

	public void setSituacaoServidor(Situacao situacaoServidor) {
		this.situacaoServidor = situacaoServidor;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_atividade", unique = false, nullable = true, insertable = true, updatable = true)
	public AtividadeServidor getAtividade() {
		return this.atividade;
	}

	public void setAtividade(AtividadeServidor atividade) {
		this.atividade = atividade;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cargo", unique = false, nullable = false, insertable = true, updatable = true)
	public Cargo getCargo() {
		return this.cargo;
	}

	public void setCargo(Cargo cargo) {
		this.cargo = cargo;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_escolaridade", unique = false, nullable = true, insertable = true, updatable = true)
	public Escolaridade getEscolaridade() {
		return this.escolaridade;
	}

	public void setEscolaridade(Escolaridade escolaridade) {
		this.escolaridade = escolaridade;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_categoria", unique = false, nullable = true, insertable = true, updatable = true)
	public Categoria getCategoria() {
		return this.categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	@Column(name = "siape", unique = true, nullable = false, insertable = true, updatable = true)
	public int getSiape() {
		return this.siape;
	}

	public void setSiape(int siape) {
		this.siape = siape;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pessoa")
	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	@Column(name = "digito_siape", length = 1)
	public Character getDigitoSiape() {
		return this.digitoSiape;
	}

	public void setDigitoSiape(Character digitoSiape) {
		this.digitoSiape = digitoSiape;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_ativo", unique = false, nullable = true, insertable = true, updatable = true)
	public Ativo getAtivo() {
		return this.ativo;
	}

	public void setAtivo(Ativo ativo) {
		this.ativo = ativo;
	}

	@Column(name = "uorg", unique = false, nullable = true, insertable = true, updatable = true, length = 0)
	public String getUorg() {
		return this.uorg;
	}

	public void setUorg(String uorg) {
		this.uorg = uorg;
	}

	@Column(name = "lotacao", unique = false, nullable = true, insertable = true, updatable = true, length = 0)
	public String getLotacao() {
		return this.lotacao;
	}

	public void setLotacao(String lotacao) {
		this.lotacao = lotacao;
	}

	@Column(name = "ultima_atualizacao", unique = false, nullable = true, insertable = true, updatable = true, length = 8)
	public Date getUltimaAtualizacao() {
		return this.ultimaAtualizacao;
	}

	public void setUltimaAtualizacao(Date ultimaAtualizacao) {
		this.ultimaAtualizacao = ultimaAtualizacao;
	}

	@Column(name = "auxilio_transporte", unique = false, nullable = false, insertable = true, updatable = true, precision = 16)
	public double getAuxilioTransporte() {
		return this.auxilioTransporte;
	}

	public void setAuxilioTransporte(double auxilioTransporte) {
		this.auxilioTransporte = auxilioTransporte;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_unidade", unique = false, nullable = true, insertable = true, updatable = true)
	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_formacao", unique = false, nullable = true, insertable = true, updatable = true)
	public Formacao getFormacao() {
		return formacao;
	}

	public void setFormacao(Formacao formacao) {
		this.formacao = formacao;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	@Transient
	public String getNome() {
		return ((this.pessoa != null && this.pessoa.getNome() == null) ? "" : this.pessoa.getNome());
	}
	
	@Transient
	public String getNomeOficial() {
		return ((this.pessoa != null && this.pessoa.getNomeOficial() == null) ? "" : this.pessoa.getNomeOficial());
	}

	@Transient
	public String getSiapeNome() {
		return getSiape() + " - " + getNome();
	}

	@Transient
	public String getCpfNome() {
		return getPessoa().getCpfCnpjFormatado() + " - " + getNome();
	}

	@Column(name = "dedicacao_exclusiva")
	public boolean isDedicacaoExclusiva() {
		return dedicacaoExclusiva;
	}

	public void setDedicacaoExclusiva(boolean dedicacaoExclusiva) {
		this.dedicacaoExclusiva = dedicacaoExclusiva;
	}

	@Column(name = "regime_trabalho")
	public Integer getRegimeTrabalho() {
		return regimeTrabalho;
	}
	
	@Transient
	public String getDescricaoRegimeTrabalho() {
		return (regimeTrabalho == 99 ? "DE" : getRegimeTrabalho() + "h");
	}

	public void setRegimeTrabalho(Integer regimeTrabalho) {
		this.regimeTrabalho = regimeTrabalho;
	}

	@Column(name = "tipo_vinculo")
	public String getTipoVinculo() {
		return tipoVinculo;
	}

	public void setTipoVinculo(String tipoVinculo) {
		this.tipoVinculo = tipoVinculo;
	}

	@Transient
	public String getIdentificacao() {
		return (new Integer(siape)).toString();
	}

	@Column(name = "id_foto")
	public Integer getIdFoto() {
		return idFoto;
	}

	public void setIdFoto(Integer idFoto) {
		this.idFoto = idFoto;
	}

	@Transient
	public PerfilPessoa getPerfil() {
		return perfil;
	}

	public void setPerfil(PerfilPessoa perfil) {
		this.perfil = perfil;
	}

	@Column(name = "id_perfil")
	public Integer getIdPerfil() {
		return idPerfil;
	}

	public void setIdPerfil(Integer idPerfil) {
		this.idPerfil = idPerfil;
	}

	@Override
	public String toString() {
		return getSiape() + " - " + getNome();
	}

	@Transient
	public String getDescricaoCompleta() {
		String situacao = this.ativo.getDescricao().trim().toUpperCase();
		if( "EXCLUSÃO".equalsIgnoreCase( situacao.trim() ) )
			situacao = "INATIVO";
		return this.getSiapeNome() + "  (" + situacao + ")";
	}

	@Transient
	public String getDescricaoResumida() {
		String situacao = this.ativo.getDescricao().trim().toUpperCase();
		if( "EXCLUSÃO".equalsIgnoreCase( situacao.trim() ) )
			situacao = "INATIVO";
		return this.getNome() + "  (" + situacao + ")";
	}

	@Transient
	public Usuario getPrimeiroUsuario() {
		return primeiroUsuario;
	}

	public void setPrimeiroUsuario(Usuario primeiroUsuario) {
		this.primeiroUsuario = primeiroUsuario;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_desligamento", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataDesligamento() {
		return dataDesligamento;
	}

	public void setDataDesligamento(Date dataDesligamento) {
		this.dataDesligamento = dataDesligamento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "admissao")
	public Date getDataAdmissao() {
		return dataAdmissao;
	}

	public void setDataAdmissao(Date dataAdmissao) {
		this.dataAdmissao = dataAdmissao;
	}

	/** Retorna siape da pessoa concatenado com nome */
	@Transient
	public String getSiapeNomeFormatado() {
		return "<div style='border-bottom: dashed 1px; padding: 2px'><div style='width:20px; float: left; text-align: right'>" + siape + "&nbsp;&nbsp;</div><div style='margin-left: 60px'>" + pessoa.getNome() + "</div></div>";
	}
	
	/** Retorna a matricula siape da pessoa, concatenado com o nome */
	@Transient
	public String getDescricaoCompletaFormatado() {
		
		String ativo = "ATIVO";
		String aposentado = "APOSENTADO";
		
		String color = "#292";
		if( this.ativo.getDescricao().toUpperCase().trim().equalsIgnoreCase(aposentado) )
			color="#F93";
		else if( !this.ativo.getDescricao().toUpperCase().trim().equalsIgnoreCase(ativo) )
			color = "#922";

		String situacao = this.ativo.getDescricao().trim().toUpperCase();
		if( "EXCLUSÃO".equalsIgnoreCase( situacao.trim() ) )
			situacao = "INATIVO";
		
		String retorno = "<div style='border-bottom: 1px solid #CCC;line-height: 1.5em;' > " 
			+ " <div style='width: 20px; float: left; text-align: right'>" + siape + "&nbsp;&nbsp;</div> " 
			+ " <div style='margin-left: 60px;'>" + pessoa.getNome()
			+ " <span style='font-size:0.9em; padding-left: 15px; color: "  + color + ";'> (" + situacao + ")</span> </div> </div>";
		
		return retorno;
		
	}

	@Transient
	public boolean isDocente() {
		return categoria != null && categoria.isDocente();
	}
	
	@Transient
	public boolean isPermanente(){
		return situacaoServidor.getId() == Situacao.ATIVO_PERMANENTE;
	}
	
	@Transient
	public boolean isTemporario(){
		return situacaoServidor.getId() == Situacao.CONTRATO_TEMPORARIO;
	}

	@Transient
	public boolean isSubstituto() {
		return isTemporario() && ( Cargo.DOCENTE_SUBSTITUTO.contains(cargo.getId()) );
	}
	
	@Transient
	public boolean isVisitante(){
		return isTemporario()	&& ( Cargo.DOCENTE_SUPERIOR_VISITANTE  == cargo.getId() );
	}
	
	/**
	 * Identifica se um servidor é colaborador voluntário. Não é persistido,
	 * é populado na hora do processamento de vínculos de um usuário.
	 * @return
	 */
	@Transient
	public boolean isColaboradorVoluntario() {
		return colaboradorVoluntario;
	}

	public void setColaboradorVoluntario(boolean colaboradorVoluntario) {
		this.colaboradorVoluntario = colaboradorVoluntario;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_classe_funcional")
	public ClasseFuncional getClasseFuncional() {
		return classeFuncional;
	}

	public void setClasseFuncional(ClasseFuncional classeFuncional) {
		this.classeFuncional = classeFuncional;
	}
	
	public int compareTo(Servidor s) {
		return new CompareToBuilder()
			.append(this.getNome(), s.getNome())
			.toComparison();
	}
	
}