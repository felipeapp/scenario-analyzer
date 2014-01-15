/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 *
 * Created on 15/09/2004
 *
 */
package br.ufrn.comum.dominio;

import java.util.Date;

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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.rh.dominio.Cargo;
import br.ufrn.rh.dominio.NivelResponsabilidade;
import br.ufrn.rh.dominio.Servidor;

/**
 * Responsável por uma Unidade. Um responsável é um funcionário que tem um tempo
 * de início e fim sobre a responsabilidade da Unidade.
 *
 * Classe que representa os responsáveis de determinada unidade.
 * Responsáveis no sentido de chefia, vice-chefia, gerencia e secretaria.
 *
 * @author Gleydson Lima
 *
 */
@Entity
@Table(name = "RESPONSAVEL_UNIDADE",schema="comum")
public class Responsavel implements Validatable {

	public static final Character SISTEMA_SIAPE = 'F';
	public static final Character SISTEMA_SIGPRH = 'S';
	public static final Character SISTEMA_SIGADMIN = 'A';

	/** Identificador */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="comum.resp_seq") })
	@Column(name = "id", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Unidade para o qual o servidor é responsável */
	@ManyToOne
	@JoinColumn(name="id_unidade")
	private UnidadeGeral unidade;

	/** Servidor que possui nível de responsabilidade pela unidade */
	@ManyToOne
	@JoinColumn(name="id_servidor")
	private Servidor servidor;

	/** Cargo do responsável pela unidade */
	@ManyToOne
	@JoinColumn(name="id_cargo")
	private Cargo cargo;

	/** Data início que a responsabilidade começa a valer */
	@Column(name="data_inicio") @Temporal(TemporalType.DATE)
	private Date inicio;

	/** Data final que a responsabilidade deixa de valer */
	@Column(name="data_fim") @Temporal(TemporalType.DATE)
	private Date fim;

	/** Usuário que atribuiu a responsabilidade */
	@ManyToOne @JoinColumn(name="id_usuario")
	private UsuarioGeral usuario;

	/** Data de cadastro da responsabilidade */
	@Column(name="data_cadastro")
	private Date dataCadastro;

	/** Nível da responsabilidade: (C) Chefia/Diretoria, (V) Vice-chefia/Vice-diretoria, (S) Secretaria, (G) Gerência */
	@Column(name="nivel_responsabilidade")
	private Character nivelResponsabilidade;

	/** Identifica a origem da atribuição da responsabilidade da unidade, se designação na Fita Siape, ou sistema. */
	@Column(name="origem")
	private Character origem;

	@CriadoEm
	@AtualizadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="ultima_atualizacao", nullable = false)
	private Date ultimaAtualizacao;
	
	@Column(name="observacao")
	private String observacao;
	
	@Column(name = "id_designacao", nullable = true)
	private Integer idDesignacao;
	
	@ManyToOne
	@JoinColumn(name = "id_registro_entrada_exclusao", nullable = true)
	private RegistroEntrada registroEntradaExclusao;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "data_exclusao", nullable = true)
	private Date dataExclusao;

	@Transient
	private String nomeServidor;

	@Transient
	private Integer matricula;
	
	/** Indica se a unidade sob responsabilidade do usuário deve ser adicionada como unidade de operação no SIPAC/SIGRH. Não persistido. */
	@Transient
	private boolean adicionarUnidade = true;


	
	/** Construtores. */
	
	public Responsavel() { }

	public Responsavel(int id) {
		this.id = id;
	}

	public Responsavel(int id, Unidade unidade) {
		this.id = id;
		this.unidade = unidade;
	}

	
	
	/** Métodos acessores. */
	
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public UnidadeGeral getUnidade() {
		return unidade;
	}

	public void setUnidade(UnidadeGeral unidade) {
		this.unidade = unidade;
	}

	public UsuarioGeral getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioGeral usuario) {
		this.usuario = usuario;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public Cargo getCargo() {
		return cargo;
	}

	public void setCargo(Cargo cargo) {
		this.cargo = cargo;
	}

	public Character getNivelResponsabilidade() {
		return nivelResponsabilidade;
	}

	public void setNivelResponsabilidade(Character nivelResponsabilidade) {
		this.nivelResponsabilidade = nivelResponsabilidade;
	}

	public Character getOrigem() {
		return origem;
	}

	public void setOrigem(Character origem) {
		this.origem = origem;
	}

	public String getDescricaoNivelResponsabilidade(){
		return NivelResponsabilidade.getNivelDescricao(nivelResponsabilidade);
	}

	public boolean isSupervisorDiretorAcademico() {
		return getNivelResponsabilidade() == NivelResponsabilidade.SUPERVISOR_DIRETOR_ACADEMICO;
	}

	public boolean isChefeUnidade(){
		return getNivelResponsabilidade() == NivelResponsabilidade.CHEFE;
	}

	public boolean isViceChefeUnidade(){
		return getNivelResponsabilidade() == NivelResponsabilidade.VICE;
	}

	public ListaMensagens validate() {

		ListaMensagens mensagens = new ListaMensagens();

		ValidatorUtil.validateRequired(getUnidade(), "Unidade", mensagens);
		if (getServidor().getId() == 0){
			ValidatorUtil.validateRequired(getServidor(), "Servidor", mensagens);
		}
		ValidatorUtil.validateRequired(getInicio(), "Período Inicial do Mandato", mensagens);
		ValidatorUtil.validateRequired(getNivelResponsabilidade(), "Nível de Responsabilidade", mensagens);

		return mensagens;
	}

	public Date getUltimaAtualizacao() {
		return ultimaAtualizacao;
	}

	public void setUltimaAtualizacao(Date ultimaAtualizacao) {
		this.ultimaAtualizacao = ultimaAtualizacao;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Integer getIdDesignacao() {
		return idDesignacao;
	}

	public void setIdDesignacao(Integer idDesignacao) {
		this.idDesignacao = idDesignacao;
	}

	public RegistroEntrada getRegistroEntradaExclusao() {
		return registroEntradaExclusao;
	}

	public void setRegistroEntradaExclusao(RegistroEntrada registroEntradaExclusao) {
		this.registroEntradaExclusao = registroEntradaExclusao;
	}

	public Date getDataExclusao() {
		return dataExclusao;
	}

	public void setDataExclusao(Date dataExclusao) {
		this.dataExclusao = dataExclusao;
	}
	
	public String getNomeServidor() {
		return nomeServidor;
	}

	public void setNomeServidor(String nomeServidor) {
		this.nomeServidor = nomeServidor;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}
	
	public boolean isAdicionarUnidade() {
		return adicionarUnidade;
	}
	
	public void setAdicionarUnidade(boolean adicionarUnidade) {
		this.adicionarUnidade = adicionarUnidade;
	}

}