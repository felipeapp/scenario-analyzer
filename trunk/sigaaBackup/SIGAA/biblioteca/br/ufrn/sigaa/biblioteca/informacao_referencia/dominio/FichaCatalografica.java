/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Oct 9, 2008
 *
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade que representa uma ficha catalográfica de uma obra.
 * @author Victor Hugo
 */
@Entity
@Table(name = "ficha_catalografica", schema = "biblioteca")
public class FichaCatalografica implements Validatable {

	/** Id da ficha catalográfica */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.solicitacoes_usuario_sequence") })
	@Column(name = "id_ficha_catalografica", nullable = false)
	private int id;
	
	//////////////////////////// Dados de impressão na ficha ///////////////////////////
	
	/** Título do trabalho. */
	private String titulo;
	
	/** O nome do autor que aparece na ordem direta no título da ficha. */
	private String responsabilidade;
	
	/** Autor do documento. */
	private String autor;
	
	/** Edição do documento. */
	private String edicao;
	
	/** O local da publicação. */
	@Column(name = "local_publicacao")
	private String localPublicacao;

	/** Editora que lançou o documento. */
	private String editora;

	/** Ano em que o documento foi produzido. */
	private Integer ano;

	/** Descrição física do documento. */
	@Column(name = "descricao_fisica", nullable = false)
	private String descricaoFisica;

	/** Detalhes relacionados à descrição física do documento. */
	@Column(name = "descricao_fisica_detalhes")
	private String descricaoFisicaDetalhes;

	/** Dimensão relacionada à descrição física do documento. */
	@Column(name = "descricao_fisica_dimensao")
	private String descricaoFisicaDimensao;

	/** Material relacionado à descrição física do documento. */
	@Column(name = "descricao_fisica_material_acompanha")
	private String descricaoFisicaMaterialAcompanha;

	/** Série do documento. */
	private String serie;

	/** ISBN do documento. */
	private String isbn;

	/** ISSN do documento. */
	private String issn;

	/** Identificação da biblioteca que gerou a ficha. */
	private String biblioteca;

	/** Guarda a descrição da classificação que foi utilizada na geração da ficha: CDU, CDD, BLACK, ABC, etc.....   */
	@Column(name= "descricao_classificacao")
	private String descricaoClassificacao;

	/** 
	 * O código da classificação utilizada na ficha. 
	 * Essa variával vai conter a classificação bibliográfica digitada pelo bibliotecário no momento de atender a solicitaação.
	 * 
	 * Que classificação é essa, vai depender da classificação que a biblioteca onde a solicitação foi atendida seja utilizando.
	 */
	@Column(name = "classificacao")
	private String classificacao;
	
	/** Notas gerais do documento. */
	@CollectionOfElements
	@JoinTable(name = "nota_geral_solicitacao_catalogacao", schema = "biblioteca",
	      joinColumns = @JoinColumn(name = "id_solicitacao_catalogacao"))
	@Column(name = "nota_geral")
	private List<String> notasGerais = new ArrayList<String>();

	/** Notas de teses do documento. */
	@CollectionOfElements
	@JoinTable(name = "nota_tese_solicitacao_catalogacao", schema = "biblioteca",
	      joinColumns = @JoinColumn(name = "id_solicitacao_catalogacao"))
	@Column(name = "nota_tese")
	private List<String> notasTeses = new ArrayList<String>();

	/** Notas bibliográficas do documento. */
	@CollectionOfElements
	@JoinTable(name = "nota_bibliografica_solicitacao_catalogacao", schema = "biblioteca",
	      joinColumns = @JoinColumn(name = "id_solicitacao_catalogacao"))
	@Column(name = "nota_bibliografica")
	private List<String> notasBibliograficas = new ArrayList<String>();

	/** Notas bibliográficas do documento. */
	@CollectionOfElements
	@JoinTable(name = "nota_conteudo_solicitacao_catalogacao", schema = "biblioteca",
	      joinColumns = @JoinColumn(name = "id_solicitacao_catalogacao"))
	@Column(name = "nota_conteudo")
	private List<String> notasConteudo = new ArrayList<String>();

	/** Assuntos pessoais do documento. */
	@CollectionOfElements
	@JoinTable(name = "assunto_pessoal_solicitacao_catalogacao", schema = "biblioteca",
	      joinColumns = @JoinColumn(name = "id_solicitacao_catalogacao"))
	@Column(name = "assunto_pessoal")
	private List<String> assuntosPessoais = new ArrayList<String>();

	/** Assuntos do documento. */
	@CollectionOfElements
	@JoinTable(name = "assunto_solicitacao_catalogacao", schema = "biblioteca",
	      joinColumns = @JoinColumn(name = "id_solicitacao_catalogacao"))
	@Column(name = "assunto")
	private List<String> assuntos = new ArrayList<String>();

	/** Autores secundários do documento. */
	@CollectionOfElements
	@JoinTable(name = "autor_secundario_solicitacao_catalogacao", schema = "biblioteca",
	      joinColumns = @JoinColumn(name = "id_solicitacao_catalogacao"))
	@Column(name = "autor_secundario")
	private List<String> autoresSecundarios = new ArrayList<String>();

	
	public FichaCatalografica() {
		
	}

	
	
	//////////////////////////// Dados para auditoria ///////////////////////////

	/** data que foi cadastrado */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** usuario que cadastrou */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** data que foi atualizado */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;

	/** Usuário que atualizou */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao", unique = false, nullable = true, insertable = true, updatable = true)
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;
	
	////////////////////////////////////////////////////////////////////////////
	
	/** A solicitação de catalogação que gerou essa ficha catalográfica */
	@Transient
	private SolicitacaoCatalogacao solicitacao;
	
	
	
	/** A data em que a ficha foi gerada. */
	@Column(name = "data_criacao")
	private Date dataCriacao;
	
	
	
	/**
	 * Retorna as palavras-chave separadas por vírgula em uma única String 
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		
		ValidatorUtil.validateRequired(titulo, "Título", erros);
		ValidatorUtil.validateRequired(autor, "Autor", erros);
		ValidatorUtil.validateRequired(ano, "Ano", erros);
		ValidatorUtil.validateRequired(descricaoFisica, "Descrição Física (campo 300a)", erros);
		ValidatorUtil.validateRequired(biblioteca, "Biblioteca", erros);
		
		if(StringUtils.isEmpty(classificacao)){
			erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, descricaoClassificacao);
		}
		
		Set<String>  notasGeraisNaoRepetidos  = new HashSet<String>(notasGerais);
		if(notasGeraisNaoRepetidos.size() !=  notasGerais.size())
			erros.addErro("A ficha catalográfica não pode conter duas notas gerais iguais.");
		
	
		Set<String>  notasTesesNaoRepetidos  = new HashSet<String>(notasTeses);	
		if(notasTesesNaoRepetidos.size() !=  notasTeses.size())
			erros.addErro("A ficha catalográfica não pode conter duas notas de tese iguais.");
		
		
		Set<String>  notasBibliograficasNaoRepetidos  = new HashSet<String>(notasBibliograficas);
		if(notasBibliograficasNaoRepetidos.size() !=  notasBibliograficas.size())
			erros.addErro("A ficha catalográfica não pode conter duas notas bibliográficas iguais.");

		
		Set<String>  notasConteudoNaoRepetidos  = new HashSet<String>(notasConteudo);
		if(notasConteudoNaoRepetidos.size() !=  notasConteudo.size())
			erros.addErro("A ficha catalográfica não pode conter duas notas de conteúdo iguais.");

		
		Set<String>  assuntosPessoaisNaoRepetidos  = new HashSet<String>(assuntosPessoais);
		if(assuntosPessoaisNaoRepetidos.size() !=  assuntosPessoais.size())
			erros.addErro("A ficha catalográfica não pode conter dois assuntos pessoais iguais.");
		
		
		Set<String> assuntosNaoRepetidos = new HashSet<String>(assuntos);
		if(assuntosNaoRepetidos.size() !=  assuntos.size())
			erros.addErro("A ficha catalográfica não pode conter dois assuntos iguais.");
		
		
		Set<String>  autoresSecundariosNaoRepetidos  = new HashSet<String>(autoresSecundarios);
		if(autoresSecundariosNaoRepetidos.size() !=  autoresSecundarios.size())
			erros.addErro("A ficha catalográfica não pode conter dois autores secundários iguais.");
		
		
		
		return erros;
	}
	
	
	/// sets e gets ///
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}
	
	public SolicitacaoCatalogacao getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(SolicitacaoCatalogacao solicitacao) {
		this.solicitacao = solicitacao;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public String getEdicao() {
		return edicao;
	}

	public void setEdicao(String edicao) {
		this.edicao = edicao;
	}

	public String getLocalPublicacao() {
		return localPublicacao;
	}

	public void setLocalPublicacao(String localPublicacao) {
		this.localPublicacao = localPublicacao;
	}

	public String getEditora() {
		return editora;
	}

	public void setEditora(String editora) {
		this.editora = editora;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public String getDescricaoFisica() {
		return descricaoFisica;
	}

	public void setDescricaoFisica(String descricaoFisica) {
		this.descricaoFisica = descricaoFisica;
	}

	public String getDescricaoFisicaDetalhes() {
		return descricaoFisicaDetalhes;
	}

	public void setDescricaoFisicaDetalhes(String descricaoFisicaDetalhes) {
		this.descricaoFisicaDetalhes = descricaoFisicaDetalhes;
	}

	public String getDescricaoFisicaDimensao() {
		return descricaoFisicaDimensao;
	}

	public void setDescricaoFisicaDimensao(String descricaoFisicaDimensao) {
		this.descricaoFisicaDimensao = descricaoFisicaDimensao;
	}

	public String getDescricaoFisicaMaterialAcompanha() {
		return descricaoFisicaMaterialAcompanha;
	}

	public void setDescricaoFisicaMaterialAcompanha(
			String descricaoFisicaMaterialAcompanha) {
		this.descricaoFisicaMaterialAcompanha = descricaoFisicaMaterialAcompanha;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getIssn() {
		return issn;
	}

	public void setIssn(String issn) {
		this.issn = issn;
	}

	public String getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(String biblioteca) {
		this.biblioteca = biblioteca;
	}

	public String getClassificacao() {
		return classificacao;
	}

	public void setClassificacao(String classificacao) {
		this.classificacao = classificacao;
	}

	public List<String> getNotasGerais() {
		return notasGerais;
	}

	public void setNotasGerais(List<String> notasGerais) {
		this.notasGerais = notasGerais;
	}

	public List<String> getNotasTeses() {
		return notasTeses;
	}

	public void setNotasTeses(List<String> notasTeses) {
		this.notasTeses = notasTeses;
	}

	public List<String> getNotasBibliograficas() {
		return notasBibliograficas;
	}

	public void setNotasBibliograficas(List<String> notasBibliograficas) {
		this.notasBibliograficas = notasBibliograficas;
	}

	public List<String> getNotasConteudo() {
		return notasConteudo;
	}

	public void setNotasConteudo(List<String> notasConteudo) {
		this.notasConteudo = notasConteudo;
	}

	public List<String> getAssuntosPessoais() {
		return assuntosPessoais;
	}

	public void setAssuntosPessoais(List<String> assuntosPessoais) {
		this.assuntosPessoais = assuntosPessoais;
	}

	public List<String> getAssuntos() {
		return assuntos;
	}

	public void setAssuntos(List<String> assuntos) {
		this.assuntos = assuntos;
	}

	public List<String> getAutoresSecundarios() {
		return autoresSecundarios;
	}

	public void setAutoresSecundarios(List<String> autoresSecundarios) {
		this.autoresSecundarios = autoresSecundarios;
	}

	public String getResponsabilidade() {
		return responsabilidade;
	}
	public void setResponsabilidade(String responsabilidade) {
		this.responsabilidade = responsabilidade;
	}

	public String getDescricaoClassificacao() {
		return descricaoClassificacao;
	}

	public void setDescricaoClassificacao(String descricaoClassificacao) {
		this.descricaoClassificacao = descricaoClassificacao;
	}

	
	
}
