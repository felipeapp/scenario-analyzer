/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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

	/** Respons�vel por armazenar a Chave prim�ria */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_subprojeto", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Respons�vel por armazenar o t�tulo */
	private String titulo;
	
	/** Respons�vel por armazenar o coordenador */
	@ManyToOne
	@JoinColumn(name = "id_coordenador")
	private Servidor coordenador;
	
	/** Respons�vel por armazenar o projeto de Infra Estrutura e Pesquisa */
	@ManyToOne
	@JoinColumn(name = "id_projeto_infraestrutura_pesquisa")
	private ProjetoInfraEstruturaPesquisa projetoInfraEstruturaPesquisa;
	
	/** Respons�vel por armazenar a �rea de conhecimento CNPQ */
	@ManyToOne
	@JoinColumn(name = "id_area_conhecimento")
	private AreaConhecimentoCnpq grandeArea;
	
	/** Respons�vel por armazenar o custeio do Sub Projeto */
	private double custeio = 0;
	
	/** Respons�vel por armazenar o capital do Sub Projeto */
	private double capital = 0;
	
	/** Respons�vel por armazenar a taxa do Sub Projeto */
	private double taxa = 0;
	
	/** Respons�vel por armazenar o overhead do Sub Projeto */
	private double overhead = 0;
	
	/** default constructor */
	public SubProjetoInfraEstruturaPesquisa(){
		
	}

	/** Respons�vel por retornar a chave prim�ria */
	public int getId() {
		return id;
	}

	/** Respons�vel por setar a chave prim�ria */
	public void setId(int id) {
		this.id = id;
	}

	/** Respons�vel por retornar o t�tulo */
	public String getTitulo() {
		return titulo;
	}

	/** Respons�vel por setar o t�tulo */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/** Respons�vel por retornar o coordenador */
	public Servidor getCoordenador() {
		return coordenador;
	}
	
	/** Respons�vel por setar o coordenador */
	public void setCoordenador(Servidor coordenador) {
		this.coordenador = coordenador;
	}

	/** Respons�vel por retornar o Projeto de Infra Estrutura Pequisa */
	public ProjetoInfraEstruturaPesquisa getProjetoInfraEstruturaPesquisa() {
		return projetoInfraEstruturaPesquisa;
	}

	/** Respons�vel por setar o Projeto de Infra Estrutura Pequisa */
	public void setProjetoInfraEstruturaPesquisa(ProjetoInfraEstruturaPesquisa projetoInfraEstruturaPesquisa) {
		this.projetoInfraEstruturaPesquisa = projetoInfraEstruturaPesquisa;
	}

	/** Respons�vel pelas valida��es do Sub Projeto de Infra Estrutura e Pesquisa */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(titulo, "T�tulo", erros);
		if (titulo != null && titulo.trim().length() > 400 ) {
			erros.addErro("O t�tulo do sub-projeto deve conter no m�ximo 400 caracteres.");
		}
		ValidatorUtil.validateRequiredAjaxId(coordenador.getId(), "Coordenador", erros);
		ValidatorUtil.validateRequiredId(grandeArea.getId(), "�rea de Conhecimento CNPq", erros);
		ValidatorUtil.validateRequired(custeio, "Custeio", erros);
		ValidatorUtil.validateRequired(capital, "Capital", erros);
		ValidatorUtil.validateRequired(taxa, "Taxa de Administra��o", erros);
		ValidatorUtil.validateRequired(overhead, "Overhead da Institui��o", erros);
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

	/** Respons�vel por retornar a Grade �rea */
	public AreaConhecimentoCnpq getGrandeArea() {
		return grandeArea;
	}

	/** Respons�vel por setar a Grade �rea */
	public void setGrandeArea(AreaConhecimentoCnpq grandeArea) {
		this.grandeArea = grandeArea;
	}

	/** Respons�vel por retornar o custeio */
	public double getCusteio() {
		return custeio;
	}

	/** Respons�vel por setar o custeio */
	public void setCusteio(double custeio) {
		this.custeio = custeio;
	}

	/** Respons�vel por retornar o capital */
	public double getCapital() {
		return capital;
	}

	/** Respons�vel por setar o capital */
	public void setCapital(double capital) {
		this.capital = capital;
	}

	/** Respons�vel por retornar a taxa */
	public double getTaxa() {
		return taxa;
	}

	/** Respons�vel por setar a taxa */
	public void setTaxa(double taxa) {
		this.taxa = taxa;
	}

	/** Respons�vel por retornar a OverHead */
	public double getOverhead() {
		return overhead;
	}

	/** Respons�vel por setar a OverHead */
	public void setOverhead(double overhead) {
		this.overhead = overhead;
	}

	/** Respons�vel por retornar a Total */
	@Transient
	public double getTotal(){
		return custeio + capital + taxa + overhead;
	}
	
}