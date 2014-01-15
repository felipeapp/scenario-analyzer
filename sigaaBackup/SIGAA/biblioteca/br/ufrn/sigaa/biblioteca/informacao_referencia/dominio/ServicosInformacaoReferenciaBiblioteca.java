/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 07/12/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.dominio;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * <p>Classe que guarda as informações de quais serviços do setor informações e referência uma biblioteca presta aos usuários</p>
 * 
 * @author Jadson
 * @author Bráulio
 */
@Entity
@Table(name = "servicos_informacao_referencia_biblioteca", schema = "biblioteca")
public class ServicosInformacaoReferenciaBiblioteca implements PersistDB {

	/** O identificador do registro. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.resgistro_extras_sequence") })
	@Column(name = "id_servicos_biblioteca")
	private int id;

	/** A biblioteca que presta os serviços descritos por essa entidade. */
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_biblioteca", nullable = false, unique = true)
	private Biblioteca biblioteca;
	
	/** Diz se a biblioteca realiza o serviço de Normalização */
	@Column(name = "normalizacao")
	private boolean realizaNormalizacao;
	
	/** Diz se a biblioteca realiza o serviço de Orientação para Normalização. */
	@Column(name = "orientacao_normalizacao")
	private boolean realizaOrientacaoNormalizacao;

	/** Diz se a biblioteca realiza o serviço de Catalogação (Geração de Ficha Catalográfica) */
	@Column(name = "catalogacao_na_fonte")
	private boolean realizaCatalogacaoNaFonte;
	
	/** Diz se a biblioteca realiza o serviço de Levantamento Bibliográfico */
	@Column(name = "levantamento_bibliografico")
	private boolean realizaLevantamentoBibliografico;

	/** Diz se a biblioteca realiza o serviço de Levantamento de Infra Estrutura */
	@Column(name = "levantamento_infra")
	private boolean realizaLevantamentoInfraEstrutura;
	
	/** Os cursos que podem requisitar os serviços com restrição de uma biblioteca. */
	@ManyToMany(
			cascade = {},
			fetch = FetchType.LAZY,
			targetEntity = Curso.class )
	@JoinTable(
			schema = "biblioteca",
			name = "biblioteca_curso",
			joinColumns = { @JoinColumn( name="id_biblioteca", referencedColumnName="id_biblioteca" ) },
			inverseJoinColumns = { @JoinColumn(name="id_curso") } )
	private List<Curso> cursosAssociados;
	
	/** As unidades que podem requisitar os serviços com restrição de uma biblioteca. */
	@ManyToMany(
			cascade = {},
			fetch = FetchType.LAZY,
			targetEntity = Unidade.class )
	@JoinTable(
			schema = "biblioteca",
			name = "biblioteca_unidade",
			joinColumns = { @JoinColumn( name="id_biblioteca", referencedColumnName="id_biblioteca" ) },
			inverseJoinColumns = { @JoinColumn( name="id_unidade" ) } )
	private List<Unidade> unidadesAssociadas;
	
	////////////////////
	
	/** Informa se a biblioteca realiza o serviço passado. */
	@Transient
	public boolean realizaServico( TipoServicoInformacaoReferencia servico ) {
		switch ( servico ) {
			case CATALOGACAO_NA_FONTE : return realizaCatalogacaoNaFonte;
			case ORIENTACAO_NORMALIZACAO : return realizaOrientacaoNormalizacao;
			case LEVANTAMENTO_BIBLIOGRAFICO : return realizaLevantamentoBibliografico;
			case LEVANTAMENTO_INFRA_ESTRUTURA : return realizaLevantamentoInfraEstrutura;
			case NORMALIZACAO : return realizaNormalizacao;
		}
		return false;
	}
	
	/** Informa se a biblioteca realiza o serviço passado. */
	@Transient
	public boolean isRealizaServico( TipoServicoInformacaoReferencia servico ) {
		return realizaServico( servico );
	}
	
	///// GETs e SETs /////

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}

	public boolean isRealizaNormalizacao() {
		return realizaNormalizacao;
	}

	public void setRealizaNormalizacao(boolean realizaNormalizacao) {
		this.realizaNormalizacao = realizaNormalizacao;
	}

	public boolean isRealizaCatalogacaoNaFonte() {
		return realizaCatalogacaoNaFonte;
	}

	public void setRealizaCatalogacaoNaFonte(boolean realizaCatalogacaoNaFonte) {
		this.realizaCatalogacaoNaFonte = realizaCatalogacaoNaFonte;
	}

	public boolean isRealizaOrientacaoNormalizacao() {
		return realizaOrientacaoNormalizacao;
	}

	public void setRealizaOrientacaoNormalizacao(boolean realizaOrientacaoNormalizacao) {
		this.realizaOrientacaoNormalizacao = realizaOrientacaoNormalizacao;
	}

	public List<Curso> getCursosAssociados() {
		return cursosAssociados;
	}

	public void setCursosAssociados(List<Curso> cursosAssociados) {
		this.cursosAssociados = cursosAssociados;
	}

	public boolean isRealizaLevantamentoBibliografico() {
		return realizaLevantamentoBibliografico;
	}

	public void setRealizaLevantamentoBibliografico(
			boolean realizaLevantamentoBibliografico) {
		this.realizaLevantamentoBibliografico = realizaLevantamentoBibliografico;
	}

	public boolean isRealizaLevantamentoInfraEstrutura() {
		return realizaLevantamentoInfraEstrutura;
	}

	public void setRealizaLevantamentoInfraEstrutura(
			boolean realizaLevantamentoInfraEstrutura) {
		this.realizaLevantamentoInfraEstrutura = realizaLevantamentoInfraEstrutura;
	}
	
	public List<Unidade> getUnidadesAssociadas() {
		return unidadesAssociadas;
	}

	public void setUnidadesAssociadas(List<Unidade> unidadesAssociadas) {
		this.unidadesAssociadas = unidadesAssociadas;
	}
		
}
