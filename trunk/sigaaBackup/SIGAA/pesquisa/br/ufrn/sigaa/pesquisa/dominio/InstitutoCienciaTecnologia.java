/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/08/2009
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Classe que representa os Institutos Nacionais de Ciência e Tecnologia
 * 
 * @author Geyson Karlos
 */

@Entity
@Table(name = "instituto_ciencia_tecnologia", schema = "pesquisa", uniqueConstraints = {})
public class InstitutoCienciaTecnologia implements Validatable{
	
	//Fields
	
	private int id;
	/** Nome do Instituto Ciência e Tecnologia */
	private String nome;
	/** Informa o volume recurso do  Instituto Ciência e Tecnologia */
	private BigDecimal volumeRecursos;
	/** Período Inicial do Instituto Ciência e Tecnologia */
	private Date periodoInicio;
	/** Período Final do Instituto Ciência e Tecnologia */
	private Date periodoFim;
	/** Informa o coordenador do Instituto Ciência e Tecnologia */
	private Servidor coordenador;
	
	private Set<MembroInstitutoCienciaTecnologia> equipesInstitutoCienciaTecnologia = new HashSet<MembroInstitutoCienciaTecnologia>(0);
	/** Unidade Federativa do Instituto Ciência e Tecnologia */
	private UnidadeFederativa unidadeFederativa;
	/** Informa se o Instituto Ciência e Tecnologia está em uso */
	private boolean ativo = true;
	
	
	/** default constructor */
	public InstitutoCienciaTecnologia(){
		
	}
	
	/** minimal constructor */
	public InstitutoCienciaTecnologia(int idInstitutoCienciaTecnologia){
		this.id = idInstitutoCienciaTecnologia;
	}
	
	public InstitutoCienciaTecnologia(int idInstitutoCienciaTecnologia, String nome, BigDecimal volumeRecursos, Date periodoInicio, Date periodoFim, Servidor coordenador, UnidadeFederativa unidadefederativa, boolean ativo){
		this.id = idInstitutoCienciaTecnologia;
		this.nome = nome;
		this.volumeRecursos = volumeRecursos;
		this.periodoInicio = periodoInicio;
		this.periodoFim = periodoFim;
		this.coordenador = coordenador;
		this.unidadeFederativa = unidadefederativa;
		this.ativo = ativo;	
	}

	/** Serve para gerar a chave primária e para retornar a chave primária */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_instituto_ciencia_tecnologia", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return id;
	}

	/** Serve para setar o id do Instituto Ciência e Tecnologia */
	public void setId(int id) {
		this.id = id;
	}

	/** Serve para Retornar o Nome de um Instituto Ciencia e Tecnologia */
	@Column(name = "nome", unique = false, nullable = true, insertable = true, updatable = true, length = 150)
	public String getNome() {
		return nome;
	}

	/** Serve para setar o Nome do Instituto Ciencia Tecnologia */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/** Serve para retornar o volume de Recursos */
	@Column(name = "volume_recurso", unique = true, nullable = false, insertable = true, updatable = true)
	public BigDecimal getVolumeRecursos() {
		return volumeRecursos;
	}

	/** Serve para setar o Volume de Recursos */
	public void setVolumeRecursos(BigDecimal volumeRecursos) {
		this.volumeRecursos = volumeRecursos;
	}

	/** Serve para retornar o periodo Inicial */
	@Temporal(TemporalType.DATE)
	@Column(name = "periodo_inicio", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getPeriodoInicio() {
		return periodoInicio;
	}

	/** Serve para setar o perído Inicial */
	public void setPeriodoInicio(Date periodoInicio) {
		this.periodoInicio = periodoInicio;
	}

	/** Serve para retornar o periodo Final */
	@Temporal(TemporalType.DATE)
	@Column(name = "periodo_fim", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getPeriodoFim() {
		return periodoFim;
	}

	/** Serve para seta o período Final */
	public void setPeriodoFim(Date periodoFim) {
		this.periodoFim = periodoFim;
	}

	/** Serve para retornar o coordenador do Insituto Ciência e Tecnologia */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_coordenador", unique = false, nullable = true, insertable = true, updatable = true)
	public Servidor getCoordenador() {
		return coordenador;
	}

	/** Serve para setar o Coordenador */
	public void setCoordenador(Servidor coordenador) {
		this.coordenador = coordenador;
	}

	/** Serve para retorna uma Unidade Federativa do Instituto Ciencia Tecnologia */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_unidade_federativa", unique = false, nullable = true, insertable = true, updatable = true)
	public UnidadeFederativa getUnidadeFederativa() {
		return unidadeFederativa;
	}

	/** Seta uma unidade federativa */
	public void setUnidadeFederativa(UnidadeFederativa unidadeFederativa) {
		this.unidadeFederativa = unidadeFederativa;
	}

	/** Retorna um boleano informando se o campo está ou não em uso. */
	@Column(name = "ativo", unique = false, nullable = true, insertable = true, updatable = true)
	public boolean isAtivo() {
		return ativo;
	}

	/** Seta um valor para o campo ativo  */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Serve para validar o cadastro de um novo Instituto Ciência Tecnologia */
	public ListaMensagens validate() {
		
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getNome(), "Nome", lista);
		ValidatorUtil.validateRequired(getVolumeRecursos(), "Volume de Recursos", lista);
		ValidatorUtil.validateRequiredId(getCoordenador().getId(), "Coordenador", lista);
		ValidatorUtil.validateRequired(getPeriodoInicio(), "Periodo Inicial", lista);
		ValidatorUtil.validateRequired(getPeriodoFim(), "Periodo Final", lista);
		ValidatorUtil.validateRequiredId(getUnidadeFederativa().getId(), "Unidade Federativa", lista);	
	
		return lista;
		
	}

	/** Seta uma Equipe Instituto Ciência Tecnologia */
	public void setEquipesInstitutoCienciaTecnologia(
			Set<MembroInstitutoCienciaTecnologia> equipesInstitutoCienciaTecnologia) {
		this.equipesInstitutoCienciaTecnologia = equipesInstitutoCienciaTecnologia;
	}

	/** Retorna uma Equipe Instituto Ciência Tecnologia */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "institutoCienciaTecnologia")
	public Set<MembroInstitutoCienciaTecnologia> getEquipesInstitutoCienciaTecnologia() {
		return equipesInstitutoCienciaTecnologia;
	}

	/**	Adiciona uma equipe Instituto Ciência Tecnologia */
	public boolean addEquipeInstitutoCienciaTecnologia(MembroInstitutoCienciaTecnologia obj) {
		obj.setInstitutoCienciaTecnologia(this);
		return equipesInstitutoCienciaTecnologia.add(obj);
	}

}