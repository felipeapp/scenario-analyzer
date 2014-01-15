/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/09/2006
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;

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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;

/**
 * Classe que registra a participação de membros em grupos de pesquisa
 * 
 * @author ricardo
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "membro_grupo_pesquisa", schema = "pesquisa", uniqueConstraints = {})
public class MembroGrupoPesquisa implements PersistDB {

	/** Classificações possíveis */
	public static final int COORDENADOR = 1;
	public static final int MEMBRO = 2;

	/** Chave primária */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_equipe_grupo_pesquisa", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_grupo_pesquisa", unique = false, nullable = true, insertable = true, updatable = true)
	private GrupoPesquisa grupoPesquisa;

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_categoria_equipe", unique = false, nullable = true, insertable = true, updatable = true)
	private CategoriaMembro categoriaMembro;

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_membro_grupo_pesquisa", unique = false, nullable = true, insertable = true, updatable = true)
	private TipoMembroGrupoPesquisa tipoMembroGrupoPesquisa;

	/** Deve ser definido para todos */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa", unique = false, nullable = true, insertable = true, updatable = true)
	private Pessoa pessoa = new Pessoa();
	
	/** Discentes participantes do grupo de pesquisa como colaboradores */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_discente", unique = false, nullable = true, insertable = true, updatable = true)
	private Discente discente = new Discente();
	
	/** Utilizado para todos os servidores, incluindo os docentes */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_servidor", unique = false, nullable = true, insertable = true, updatable = true)
	private Servidor servidor = new Servidor();
	
	/** Docentes externos */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_docente_externo", unique = false, nullable = true, insertable = true, updatable = true)
	private DocenteExterno docenteExterno = new DocenteExterno();

	@Column(name = "classificacao", unique = false, nullable = true, insertable = true, updatable = true)
	private Integer classificacao;

	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	private Date dataInicio;

	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	private Date dataFim;
	
	@Column(name = "endereco_lattes")
	private String enderecoLattes;
	
	private boolean ativo = true;
	
	@Transient
	private boolean selecionado = false;

	private Boolean assinado;
	
	@Column(name = "senha_confirmacao")
	private String senhaConfirmacao;

	@Column(name = "codigo_acesso")
	private String codigoAcesso;

	@Column(name = "data_assinatura")
	private Date dataAssinatura;

	/** Data da última atualização do componente. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;
	
	// Constructors

	/** default constructor */
	public MembroGrupoPesquisa() {
	}

	/** minimal constructor */
	public MembroGrupoPesquisa(int idEquipeGrupoPesquisa) {
		this.id = idEquipeGrupoPesquisa;
	}

	/** full constructor */
	public MembroGrupoPesquisa(int idEquipeGrupoPesquisa,
			GrupoPesquisa grupoPesquisa, CategoriaMembro categoriaMembro,
			TipoMembroGrupoPesquisa tipoMembroGrupoPesquisa, Discente discente,
			Servidor servidor, Integer classificacao,
			Date dataInicio, Date dataFim) {
		this.id = idEquipeGrupoPesquisa;
		this.grupoPesquisa = grupoPesquisa;
		this.categoriaMembro = categoriaMembro;
		this.tipoMembroGrupoPesquisa = tipoMembroGrupoPesquisa;
		this.discente = discente;
		this.servidor = servidor;
		this.classificacao = classificacao;
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int idEquipeGrupoPesquisa) {
		this.id = idEquipeGrupoPesquisa;
	}

	public GrupoPesquisa getGrupoPesquisa() {
		return this.grupoPesquisa;
	}

	public void setGrupoPesquisa(GrupoPesquisa grupoPesquisa) {
		this.grupoPesquisa = grupoPesquisa;
	}

	public CategoriaMembro getCategoriaMembro() {
		return this.categoriaMembro;
	}

	public void setCategoriaMembro(CategoriaMembro categoriaMembro) {
		this.categoriaMembro = categoriaMembro;
	}

	public TipoMembroGrupoPesquisa getTipoMembroGrupoPesquisa() {
		return this.tipoMembroGrupoPesquisa;
	}

	public void setTipoMembroGrupoPesquisa(
			TipoMembroGrupoPesquisa tipoMembroGrupoPesquisa) {
		this.tipoMembroGrupoPesquisa = tipoMembroGrupoPesquisa;
	}

	public Discente getDiscente() {
		return this.discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public Servidor getServidor() {
		return this.servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public Integer getClassificacao() {
		return this.classificacao;
	}

	public void setClassificacao(Integer classificacao) {
		this.classificacao = classificacao;
	}

	public Date getDataInicio() {
		return this.dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return this.dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "pessoa.id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, pessoa != null? pessoa.getId() : 0);
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public DocenteExterno getDocenteExterno() {
		return docenteExterno;
	}

	public void setDocenteExterno(DocenteExterno docenteExterno) {
		this.docenteExterno = docenteExterno;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getClassificacaoString(){
		if(classificacao != null && classificacao == COORDENADOR) 
			return "Líder"; 
		else if(classificacao != null && classificacao == MEMBRO)
			return "Membro";
		else
			return "Indefinido";
	}

	public String getCategoriaString(){
		if(categoriaMembro != null && categoriaMembro.getId() == CategoriaMembro.DOCENTE) 
			return "Docente"; 
		else if(classificacao != null && categoriaMembro.getId() == CategoriaMembro.DISCENTE)
			return "Discente";
		else if(classificacao != null && categoriaMembro.getId() == CategoriaMembro.EXTERNO)
			return "Docente Externo";
		else if(classificacao != null && categoriaMembro.getId() == CategoriaMembro.SERVIDOR)
			return "Servidor";
		else
			return "Indefinido";
	}

	public String getTipoMembroGrupoPesqString(){
		if(tipoMembroGrupoPesquisa != null && tipoMembroGrupoPesquisa.getId() == TipoMembroGrupoPesquisa.PERMANENTE) 
			return "Membro Permanente"; 
		else if(tipoMembroGrupoPesquisa != null && tipoMembroGrupoPesquisa.getId() == TipoMembroGrupoPesquisa.COLABORADOR)
			return "Membro Colaborador";
		else if(tipoMembroGrupoPesquisa != null && tipoMembroGrupoPesquisa.getId() == TipoMembroGrupoPesquisa.INTERINO)
			return "Membro Interino";
		else if(tipoMembroGrupoPesquisa != null && tipoMembroGrupoPesquisa.getId() == TipoMembroGrupoPesquisa.ASSOCIADO)
			return "Membro Associado";
		else
			return "Indefinido";
	}
	
	public boolean isDiscenteUFRN() {
		return categoriaMembro != null && categoriaMembro.getId() == CategoriaMembro.DISCENTE;
	}
	
	public boolean isDocenteUFRN() {
		return categoriaMembro != null && categoriaMembro.getId() == CategoriaMembro.DOCENTE;
	}
	
	public boolean isServidorUFRN() {
		return categoriaMembro != null && categoriaMembro.getId() == CategoriaMembro.SERVIDOR;
	}
	
	public boolean isDocenteExternoUFRN() {
		return categoriaMembro != null && categoriaMembro.getId() == CategoriaMembro.EXTERNO;
	}		

	public boolean isPermanente(){
		return getTipoMembroGrupoPesquisa() != null && getTipoMembroGrupoPesquisa().getId() == TipoMembroGrupoPesquisa.PERMANENTE; 
	}
	
	public boolean isAssociado(){
		return getTipoMembroGrupoPesquisa() != null && getTipoMembroGrupoPesquisa().getId() == TipoMembroGrupoPesquisa.ASSOCIADO;
	}
	
	public boolean isCoordenador(){
		return getClassificacao() != null && getClassificacao().intValue() == COORDENADOR;
	}

	public String getEnderecoLattes() {
		return enderecoLattes;
	}

	public void setEnderecoLattes(String enderecoLattes) {
		this.enderecoLattes = enderecoLattes;
	}

	public Boolean getAssinado() {
		return assinado;
	}

	public void setAssinado(Boolean assinado) {
		this.assinado = assinado;
	}

	public String getSenhaConfirmacao() {
		return senhaConfirmacao;
	}

	public void setSenhaConfirmacao(String senhaConfirmacao) {
		this.senhaConfirmacao = senhaConfirmacao;
	}

	public String getCodigoAcesso() {
		return codigoAcesso;
	}

	public void setCodigoAcesso(String codigoAcesso) {
		this.codigoAcesso = codigoAcesso;
	}

	public Date getDataAssinatura() {
		return dataAssinatura;
	}

	public void setDataAssinatura(Date dataAssinatura) {
		this.dataAssinatura = dataAssinatura;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

}