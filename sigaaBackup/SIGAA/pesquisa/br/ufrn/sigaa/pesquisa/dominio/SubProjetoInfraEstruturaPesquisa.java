/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/02/2008
 *
 */

package br.ufrn.sigaa.pesquisa.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Classe que representa os sub-projetos dos projetos de infra-estrutura em pesquisa.
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name = "subprojeto_infraestrutura_pesquisa", schema = "pesquisa", uniqueConstraints = {})
public class SubProjetoInfraEstruturaPesquisa implements Validatable {

	/** Responsável por armazenar a Chave primária */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_subprojeto", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Responsável por armazenar o título */
	private String titulo;
	
	/** Responsável por armazenar o coordenador */
	@ManyToOne
	@JoinColumn(name = "id_coordenador")
	private Servidor coordenador;
	
	/** Responsável por armazenar o projeto de Infra Estrutura e Pesquisa */
	@ManyToOne
	@JoinColumn(name = "id_projeto_infraestrutura_pesquisa")
	private ProjetoInfraEstruturaPesquisa projetoInfraEstruturaPesquisa;
	
	/** Responsável por armazenar a Área de conhecimento CNPQ */
	@ManyToOne
	@JoinColumn(name = "id_area_conhecimento")
	private AreaConhecimentoCnpq grandeArea;
	
	/** Responsável por armazenar o custeio do Sub Projeto */
	private double custeio = 0;
	
	/** Responsável por armazenar o capital do Sub Projeto */
	private double capital = 0;
	
	/** Responsável por armazenar a taxa do Sub Projeto */
	private double taxa = 0;
	
	/** Responsável por armazenar o overhead do Sub Projeto */
	private double overhead = 0;
	
	/** default constructor */
	public SubProjetoInfraEstruturaPesquisa(){
		
	}

	/** Responsável por retornar a chave primária */
	public int getId() {
		return id;
	}

	/** Responsável por setar a chave primária */
	public void setId(int id) {
		this.id = id;
	}

	/** Responsável por retornar o título */
	public String getTitulo() {
		return titulo;
	}

	/** Responsável por setar o título */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/** Responsável por retornar o coordenador */
	public Servidor getCoordenador() {
		return coordenador;
	}
	
	/** Responsável por setar o coordenador */
	public void setCoordenador(Servidor coordenador) {
		this.coordenador = coordenador;
	}

	/** Responsável por retornar o Projeto de Infra Estrutura Pequisa */
	public ProjetoInfraEstruturaPesquisa getProjetoInfraEstruturaPesquisa() {
		return projetoInfraEstruturaPesquisa;
	}

	/** Responsável por setar o Projeto de Infra Estrutura Pequisa */
	public void setProjetoInfraEstruturaPesquisa(ProjetoInfraEstruturaPesquisa projetoInfraEstruturaPesquisa) {
		this.projetoInfraEstruturaPesquisa = projetoInfraEstruturaPesquisa;
	}

	/** Responsável pelas validações do Sub Projeto de Infra Estrutura e Pesquisa */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(titulo, "Título", erros);
		if (titulo != null && titulo.trim().length() > 400 ) {
			erros.addErro("O título do sub-projeto deve conter no máximo 400 caracteres.");
		}
		ValidatorUtil.validateRequiredAjaxId(coordenador.getId(), "Coordenador", erros);
		ValidatorUtil.validateRequiredId(grandeArea.getId(), "Área de Conhecimento CNPq", erros);
		ValidatorUtil.validateRequired(custeio, "Custeio", erros);
		ValidatorUtil.validateRequired(capital, "Capital", erros);
		ValidatorUtil.validateRequired(taxa, "Taxa de Administração", erros);
		ValidatorUtil.validateRequired(overhead, "Overhead da Instituição", erros);
		return erros;
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "coordenador.id"); 
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, coordenador.getId());
	}

	/** Responsável por retornar a Grade Área */
	public AreaConhecimentoCnpq getGrandeArea() {
		return grandeArea;
	}

	/** Responsável por setar a Grade Área */
	public void setGrandeArea(AreaConhecimentoCnpq grandeArea) {
		this.grandeArea = grandeArea;
	}

	/** Responsável por retornar o custeio */
	public double getCusteio() {
		return custeio;
	}

	/** Responsável por setar o custeio */
	public void setCusteio(double custeio) {
		this.custeio = custeio;
	}

	/** Responsável por retornar o capital */
	public double getCapital() {
		return capital;
	}

	/** Responsável por setar o capital */
	public void setCapital(double capital) {
		this.capital = capital;
	}

	/** Responsável por retornar a taxa */
	public double getTaxa() {
		return taxa;
	}

	/** Responsável por setar a taxa */
	public void setTaxa(double taxa) {
		this.taxa = taxa;
	}

	/** Responsável por retornar a OverHead */
	public double getOverhead() {
		return overhead;
	}

	/** Responsável por setar a OverHead */
	public void setOverhead(double overhead) {
		this.overhead = overhead;
	}

	/** Responsável por retornar a Total */
	@Transient
	public double getTotal(){
		return custeio + capital + taxa + overhead;
	}
	
}