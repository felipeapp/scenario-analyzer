/*
 * Universidade Federal do Rio Grande no Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 10/06/2009
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import static br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil.retiraPontuacaoCamposBuscas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.CampoPesquisaPorListas;
import br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil;


/**
 *      Classe que vai funcionar como um cache das informações MARC para títulos, artigos ou autoridades
 *  para otimizar as pesquisas do sistema.
 * 
 *     <p><strong>*** Essas informações devem ser sincronizadas sempre que as entidades forem criadas
 * ou alteradas no sistema. *** </strong></p>
 * 
 *      <p><strong>Observação:</strong> Alguns campos de classe são resultados da junção de vários campos.
 *      Nesse caso tudo fica dentro de apenas 1 tabela no banco para melhorar o desempenho do sistema.
 *      Essas informações vão ser separadas pela sequência de caracteres '#$&', que deve ser
 *      tratada na hora de ser mostrada para o usuário.</p>
 *      <p> Todos os campos marcados com ( pode conter vários campos ) ou ( pode ser repetido )
 *      contém dados separados por '#$&'. </p>
 * 
 *
 * @author Jadson
 * @since 10/06/2009
 * @version 1.0 Criação da classe
 *
 */
@Entity
@Table(name = "cache_entidades_marc", schema = "biblioteca")
public class CacheEntidadesMarc implements PersistDB{
	
	/** Id do registro de cache MARC. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column (name="id_cache_entidades_marc")
	private int id;
	
	/**
	 * Ficou melhor nesse caso só guardar o id e depois se precisar dá um refresh no banco   *
	 * porque geralmente nas páginas do sistema precisão do id para passar como parâmetro    *
	 * e para trazer o id o hibernate ficava gerando consultas extras.                       */
	
	/** Se as informações aqui são de um título */
	@Column (name="id_titulo_catalografico")
	private Integer idTituloCatalografico;
	
	/** Se as informações aqui são de uma autoridade */
	@Column(name="id_autoridade")
	private Integer idAutoridade;
	
	/** Se as informações aqui são de um artigo de periódico */
	@Column (name="id_artigo_de_periodico")
	private Integer idArtigoDePeriodico;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////// Campos mostrados ao usuário //////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Guarda um número que identifica um título no sistema, autoridade ou artigo
	 */
	@Column(name="numero_do_sistema")
	private int numeroDoSistema;
	
	/** 245$a bibliográfico. (não se repete)
	 *  Utilizado também para guardar os títulos dos artigos
	 * 
	 */
	@Column(name="titulo")
	private String titulo;
	
	
	/** campo 245$b (não se repete) */
	@Column(name="sub_titulo")
	private String subTitulo;
	
	/** O meio de publicação do material, deve ser mostrando junto ao título para o usuário.
	 * campo 245$h (não se repete) 
	 */
	@Column(name="meio_publicacao")
	private String meioPublicacao;
	
	
	/**  246$a (repete) */
	@Column(name="formas_varientes_titulo")
	private String formasVarientesTitulo;
	
	/** Campos 100$a, 110$a, 111$a bibliográfico. ( só um campo por vez )
	 * 
	 *  Utilizado também para guardar o autor de um artigo de periódico
	 */
	@Column(name="autor")
	private String autor;
	
	/** Campos 700$a, 700$a, 700$b, 711$a, 711$e bibliográfico. ( pode ser repetido ) */
	@Column(name="autores_secundarios")
	private String autoresSecundarios;
	
	/**  600$a, 610$a, 6111$a, 611$e, 630$a, 650$a, 650$x,  650$y, 650$z, 651$a, 651$x,  651$y,  651$z, 653$a bibliográfico. ( pode ser repetido )
     *
     *  Utilizado também para guardar as palavras chaves de artigos de periódicos
     */
	@Column(name="assunto")
	private String assunto;
	
	/**  260$a bibliográfico ( pode ser repetido )
	 * 
	 * Utilizado também para guardar o local de publicação de artigos de periódicos
	 */
	@Column(name="local_publicacao")
	private String localPublicacao;

	/**  260$b bibliográfico ( pode ser repetido )
	 * 
	 * Utilizado também para guardar a editora de artigos de periódicos
	 */
	@Column(name="editora")
	private String editora;
	
	/**
	 * Campo 260$c, 260$g bibliográfico ( pode ser repetido )
	 */
	@Column(name="ano")
	private String ano;
	
	
	/** Posição 7 a 10 do campo 008 bibliográfico (não se repete), dado numérico para poder realizar a busca simples utilizando um período de datas  */
	@Column(name="ano_publicacao")
	private Integer anoPublicacao;
	
	
	/** Campo 300$a série (repete) */
	@Column(name="descricao_fisica")
	private String descricaoFisica;


	/** 490$a série (pode repetir)  -> 440$a usado atualmente está em desuso desde de 2008. */
	@Column(name="serie")
	private String serie;
	
	
	/**
	 * Campo 250$a, 250$b bibliográfico. (não se repete)
	 */
	@Column(name="edicao")
	private String edicao;
	
	
	/**  Campo 020$a bibliográfico. (repete) */
	@Column(name="isbn")
	private String isbn;
	
	/** Campo 022$a bibliográfico. (repete) */
	@Column(name="issn")
	private String issn;
	
	
	/**
	 * Campo 090$a + 090$b + 090$c + 090$d bibliográfico. (não se repete)
	 */
	@Column(name="numero_chamada")
	private String numeroChamada;

	
	/** 505$a (repete) */
	@Column(name="nota_de_conteudo")
	private String notaDeConteudo;
	
	/** 500$a (repete) */
	@Column(name="notas_gerais")
	private String notasGerais;
	
	/** 590$a (repete) */
	@Column(name="notas_locais")
	private String notasLocais;
	
	
	/** Guarda o id da obras digitalizada em formato .pdf para aqueles títulos que possuem  */
	@Column(name="id_obra_digitalizada")
	private Integer idObraDigitalizada;
	
	/** Guarda os vário endereços eletrônicos que uma obra pode ter. campo 856$a e 856$u */
	@Column(name="localizacao_endereco_eletronico")
	private String localizacaoEnderecoEletronico;   // ( pode ser repetido )
	
	
	/** Classificação bibliográfica 1, utilizada para visualização dos usuário */
	@Column(name="classificacao_1")
	private String classificacao1;
	
	/** Classificação bibliográfica 2, utilizada para visualização dos usuário */
	@Column(name="classificacao_2")
	private String classificacao2;
	
	/** Classificação bibliográfica 3, utilizada para visualização dos usuário */
	@Column(name="classificacao_3")
	private String classificacao3;
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// campos de busca    ////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Utilizado na pesquisa de artigos . (não se repete)
	 */
	@Column(name="titulo_ascii")
	private String tituloAscii;
	
	
	/**
	 * Utilizado para realizar a consulta no acervo das informações do Título de Título . (possui vários campos)
	 * 
	 * Campo 245 (Título principal) - Sub campos a, b
	 * Campo 780 (Título anterior) - Sub campos t
	 * Campo 785 (Título posterior) - Sub campos t
	 * Campo 130 (Título uniforme) - Sub campos a
	 * Campo 240 (Título uniforme) - Sub campos a
	 * Campo 730 (Título uniforme) - Sub campos a
	 * Campo 210 (Título abreviado) - Sub campos a
	 * Campo 246 (Título variável) - Sub campos a, b, n , p
	 * Campo 243 (Título da coletânea) - Sub campos a
	 * 
	 */
	@Column(name="outras_informacoes_titulo_ascii")
	private String outrasInformacoesTituloAscii;

	/** 240$a (não se repete) Campo utilizado para pesquisas na  ** busca avançada **    */
	@Column(name="titulo_uniforme_ascii")
	private String tituloUniformeAscii;
	
	
	/** campos 100$a, 110$a, 110$b, 111$a, 111$e bibliográfico. ( só um campo por vez  ) */
	@Column(name="autor_ascii")
	private String autorAscii;
	
	
	/** campos 700$a, 710$a, 710$b, 711$a, 711$e bibliográfico. ( pode ser repetido ) */
	@Column(name="autores_secundarios_ascii")
	private String autoresSecundariosAscii;
	
	
	/**
	 * 600$a, 610$a, 6111$a, 611$e, 630$a, 650$a, 650$x,  650$y, 650$z, 651$a, 651$x,  651$y,  651$z, 653$a bibliográfico. ( pode ser repetido )
	 */
	@Column(name="assunto_ascii")
	private String assuntoAscii;
	
	/**  260$a bibliográfico ( pode ser repetido )  */
	@Column(name="local_publicacao_ascii")
	private String localPublicacaoAscii;
	
	/**  260$b bibliográfico ( pode ser repetido ) */
	@Column(name="editora_ascii")
	private String editoraAscii;
	

	/** 490 série (pode repetir)  -> 440 usado atualmente está em desuso desde de 2008. */
	@Column(name="serie_ascii")
	private String serieAscii;
	
	/** Guardas todas as notas que vão ser pesquisadas em uma coluna só: 500$a 505$a 590$a 534$p, $f, $b, $c   530$a, 502$a, 501$a (repete) */
	@Column(name="notas_ascii")
	private String notasAscii;
	

//	/** Classificação CDU do título. ( pode repetir ) , utilizado para as buscas */
//	@Column(name="cdu_ascii")
//	private String cduAscii;
//	
//	/** Classificação Black (odontologia) do título. ( pode repetir ) utilizado para as buscas */
//	@Column(name="black_ascii")
//	private String blackAscii;
	
	
	/** Classificação bibliográfica 1, utilizada para as pesquisas no sistema */
	@Column(name="classificacao_1_ascii")
	private String classificacao1Ascii;
	
	/** Classificação bibliográfica 2, utilizada para as pesquisas no sistema */
	@Column(name="classificacao_2_ascii")
	private String classificacao2Ascii;
	
	/** Classificação bibliográfica 3, utilizada para as pesquisas no sistema */
	@Column(name="classificacao_3_ascii")
	private String classificacao3Ascii;
	

	/** O filtro por idioma utilizado na busca avançada, essa informação poderá ser abstraída do campo 008, Idioma (35,37). 
	 * 
	 * Usa apenas siglas:  por = português
	 *                     eng = english
	 *                     xxx = .......
	 * */
	@Column(name="idioma_ascii")
	private String idiomaAscii;
	
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////     Campos do cache de autoridades        ///////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** 100 110 ou 111 (todos os subcampo) de autoridades( só um por vez  mais
	 * para entrada autorizada autor guarda todos os subcampos ) */
	@Column(name="entrada_autorizada_autor")
	private String entradaAutorizadaAutor;
	
    /** 400#a, 410$a e 411$a de autoridades ( pode conter vários campos ) */
	@Column(name="nomes_remissivos_autor")
	private String nomesRemissivosAutor;
	
	/** Campos 150 151  180 (todos os subcampo) de autoridades ( só tem um
	 * campo mais para assunto guarda todos os subcampos.) */
	@Column(name="entrada_autorizada_assunto")
	private String entradaAutorizadaAssunto;
	
	/** Campos 450$a 451$a 480$a de autoridades ( pode conter vários campos ) */
	@Column(name="nomes_remissivos_assunto")
	private String nomesRemissivosAssunto;
	
	/** String que guarda qual o campo que gerou a entrada autorizada para o
	 * usuário saber se foi o campo 100$a ou 110$a ou etc.. <p>
	 * exemplos 100$a 450$a, 451$a */
	@Column(name="campo_entrada_autorizada")
	private String campoEntradaAutorizada;
	
	
	///////////////////////////////// Campos de busca de autoridade //////////////////////////////////
	
	/** 100$a 110$a ou 111$a de autoridades( só um por vez ) */
	@Column(name="entrada_autorizada_autor_ascii")
	private String entradaAutorizadaAutorAscii;
	
	 /** 400#a, 410$a e 411$a de autoridades ( pode conter vários campos ) */
	@Column(name="nomes_remissivos_autor_ascii")
	private String nomesRemissivosAutorAscii;
	
	/** Campos 150$a 151$a 480$a de autoridades ( só um por vez ) */
	@Column(name="entrada_autorizada_assunto_ascii")
	private String entradaAutorizadaAssuntoAscii;
	
	/** Campos 450$a 451$a 480$a de autoridades ( pode conter vários campos ) */
	@Column(name="nomes_remissivos_assunto_ascii")
	private String nomesRemissivosAssuntoAscii;
	
	/////////////////////////////////////////////////////////////////////////////////////////

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////// Campos do cache de artigos de periódicos////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////
	
	
	/* *************************************************************************************************
	 * Além das variáveis abaixo os artigos utilizam: "título", "autor" e "assunto" (palavras chaves)   *
	 *, "localPublicacao", "editora" e "ano" que são comuns ao Título e Artigo de Periódico            *
	 * *************************************************************************************************/
	
	/** Intervalo de páginas no qual o artigo se encontra dentro de um periódico. */
	@Column(name="intervalo_paginas")
	private String intervaloPaginas;
	
	/** Resumo do artigo ou do título */
	@Column(name="resumo")
	private String resumo;
	
	///////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	
	
	/**
	 *   Foi necessário colocar a quantidade de materiais em cache também porque estava atrasando
	 * muito a busca ter que contar isso para cada Título achado.
	 * 
	 *   Quando um exemplar ou fascículo for cadastro ou dado baixa para um título essa variável
	 * precisa ser sincronizada.
	 */
	@Column(name="quantidade_materiais_ativos_titulo")
	private int quantidadeMateriaisAtivosTitulo = 0;
	
	
	
	/**
	 * <p>Guarda a quantidade de vezes que esse título foi recuperado nas consultas.</p> 
	 * <p>Utilizado para ordenar os resultados da consulta do acervo e facilitar o usuário encontrar o que está procurando.</p>
	 */
	@Column(name="quantidade_vezes_consultado", nullable=false)
	private long quantidadeVezesConsultado = 0;
	
	/**
	 * <p>Guarda a quantidade de vezes que esse título visualizado, o usuário selecionou ele para visulizar os materiais.</p> 
	 * <p>Utilizado para ordenar os resultados da consulta do acervo e facilitar o usuário encontrar o que está procurando.</p>
	 */
	@Column(name="quantidade_vezes_visualizado", nullable=false)
	private long quantidadeVezesVisualizado = 0;
	
	
	/**
	 * <p>Guarda a quantidade de vezes que esse os materiais desse título foram emprestados.</p> 
	 * <p>Utilizado para ordenar os resultados da consulta do acervo e facilitar o usuário encontrar o que está procurando.</p>
	 */
	@Column(name="quantidade_vezes_emprestado", nullable=false)
	private long quantidadeVezesEmprestado = 0;
	
	
	/**
	 *   Informação usada para saber se um título ou autoridade foi catalogado por completo ou não.
	 * 
	 *   Entidades não catalogadas serão mostradas de uma forma diferente para o usuário e não podem
	 *   ser exportadas nem adicionados materiais se forme títulos.
	 */
	@Column(name="catalogado", nullable=false)
	private boolean catalogado = false;
	
	
	
	
	
	/** Guarda se esse objeto foi selecionado ou não nas página do sistema*/
	@Transient
	private boolean selecionada;

	/** Guarda se esse objeto foi importado ou não */
	@Transient
	private boolean importado;
	
	/**
	 *      Guarda temporariamente o nome do usuário que criou o objeto que o cache representa
	 * (título, autoridade ou artigo), para mostrar mas páginas do sistema.
	 */
	@Transient
	private String nomeUsuario;
	
	
	/**
	 *     Guarda o campo que deve ser mostrado ao usuário na tela na pesquisa por listas. <br/>
	 *     Porque na pesquisa por lista, o que é mostrado para o usuário varia de acordo com o que
	 * o usuário buscou.
	 */
	@Transient
	private int tipoCampoBuscaPorListas;
	

	
	
	public CacheEntidadesMarc(){
		// Construtor padrão
	}
	
	
	/** Cria um objeto vazio com o id passado. */
	public CacheEntidadesMarc(int id){
		this.id = id;
	}
	
	
	
	
	//////////////////////// Dados formatados para exibição  /////////////////////////////
	
	/**
	 *  Retorna os dados formatados para visualização.
	 *  Já que o objeto cache guarda todos em uma única variável separados pela sequência '#$&'
	 */
	public List<String> getDescricaoFisicaFormatada() {
		
		
		List<String> nomesList = new ArrayList<String>();
		
		if(descricaoFisica != null){
			
			String[] tokens = descricaoFisica.split("\\#\\$\\&");
		
			for (int indiceArray = 0; indiceArray < tokens.length; indiceArray++) {
				String descTemp = tokens[indiceArray];
				if(StringUtils.notEmpty(descTemp))
					nomesList.add(descTemp);
			}
		}
		
		return nomesList;
	}
	
	/**
	 *  Retorna os dados formatados para visualização.
	 *  Já que o objeto cache guardas todos em uma única variável separados pela sequência '#$&'
	 */
	public List<String> getSerieFormatados() {
		
		
		List<String> nomesList = new ArrayList<String>();
		
		if(serie != null){
			
			String[] tokens = serie.split("\\#\\$\\&");
		
			for (int indiceArray = 0; indiceArray < tokens.length; indiceArray++) {
				String serieTemp = tokens[indiceArray];
				if(StringUtils.notEmpty(serieTemp))
					nomesList.add(serieTemp);
			}
		}
		
		return nomesList;
	}
	
	
	/** Retorna os dados cdu formatados para visualização, já que pode ter vários */
	public List<String>  getClassificacao1Formatada() {
		List<String> nomesList = new ArrayList<String>();
		
		if(classificacao1 != null){
			
			String[] tokens = classificacao1.split("\\#\\$\\&");
		
			for (int indiceArray = 0; indiceArray < tokens.length; indiceArray++) {
				String classificacao1Temp = tokens[indiceArray];
				if(StringUtils.notEmpty(classificacao1Temp))
					nomesList.add(classificacao1Temp);
			}
		}
		
		return nomesList;
	}
	
	/** Retorna os dados black formatados para visualização, já que pode ter vários */
	public List<String>  getClassificacao2Formatada() {
		List<String> nomesList = new ArrayList<String>();
		
		if(classificacao2 != null){
			
			String[] tokens = classificacao2.split("\\#\\$\\&");
		
			for (int indiceArray = 0; indiceArray < tokens.length; indiceArray++) {
				String classificacao2Temp = tokens[indiceArray];
				if(StringUtils.notEmpty(classificacao2Temp))
					nomesList.add(classificacao2Temp);
			}
		}
		
		return nomesList;
	}
	
	
	/** Retorna os dados black formatados para visualização, já que pode ter vários */
	public List<String>  getClassificacao3Formatada() {
		List<String> nomesList = new ArrayList<String>();
		
		if(classificacao3 != null){
			
			String[] tokens = classificacao3.split("\\#\\$\\&");
		
			for (int indiceArray = 0; indiceArray < tokens.length; indiceArray++) {
				String classificacao3Temp = tokens[indiceArray];
				if(StringUtils.notEmpty(classificacao3Temp))
					nomesList.add(classificacao3Temp);
			}
		}
		
		return nomesList;
	}
	
	
	/**
	 *  Retorna os dados formatados para visualização.
	 *  Já que o objeto cache guardas todos em uma única variável separados pela sequência '#$&'
	 */
	public List<String> getISBNFormatados() {
		
		
		List<String> nomesList = new ArrayList<String>();
		
		if(isbn != null){
			String[] tokens = isbn.split("\\#\\$\\&");
			
			
			for (int indiceArray = 0; indiceArray < tokens.length; indiceArray++) {
				
				String isbnTemp = tokens[indiceArray];
				if(StringUtils.notEmpty(isbnTemp))
					nomesList.add(isbnTemp);
			}
		}
		
		return nomesList;
	}
	
	
	/**
	 *  Retorna os dados formatados para visualização.
	 *  Já que o objeto cache guardas todos em uma única variável separados pela sequência '#$&'
	 */
	public List<String> getISSNFormatados() {
		
		
		List<String> nomesList = new ArrayList<String>();
		
		if(issn != null){
			String[] tokens = issn.split("\\#\\$\\&");
			
			
			for (int indiceArray = 0; indiceArray < tokens.length; indiceArray++) {
				
				String issnTemp = tokens[indiceArray];
				if(StringUtils.notEmpty(issnTemp))
					nomesList.add(issnTemp);
			}
		}
		
		return nomesList;
	}
	
	
	/**
	 *   Retorna os nomes remissivos de autor formatados para visualização.
	 *   Já que o objeto cache guarda todos em uma única variável separados pela sequência '#$&'
	 */
	public List<String> getNomesRemissivosAutorFormatados() {
		
		List<String> nomesList = new ArrayList<String>();
		
		if(nomesRemissivosAutor != null){
		
			String[] tokens = nomesRemissivosAutor.split("\\#\\$\\&");

			for (int indiceArray = 0; indiceArray < tokens.length; indiceArray++) {
				String nomes = tokens[indiceArray];
				if(StringUtils.notEmpty(nomes))
					nomesList.add(nomes);
			}
		}
		
		return nomesList;
	}
	
	/**
	 *   Retorna os nomes remissivos de assuntos formatados para visualização.
	 *   Já que o objeto cache guardas todos em uma única variável separados pela sequência '#$&'
	 */
	public List<String> getNomesRemissivosAssuntoFormatados() {
		
		List<String> nomesList = new ArrayList<String>();
		
		if(nomesRemissivosAssunto != null){
		
			String[] tokens = nomesRemissivosAssunto.split("\\#\\$\\&");
			
			for (int indiceArray = 0; indiceArray < tokens.length; indiceArray++) {
				
				String nomes = tokens[indiceArray];
				if(StringUtils.notEmpty(nomes))
					nomesList.add(nomes);
			}
		}
	
		return nomesList;
	}
	
	/**
	 *  Retorna os dados formatados para visualização.
	 *  Já que o objeto cache guardas todos em uma única variável separados pela sequência '#$&'
	 */
	public List<String> getLocaisPublicacaoFormatados() {
		
		List<String> nomesList = new ArrayList<String>();
		
		if(localPublicacao != null){
			String[] tokens = localPublicacao.split("\\#\\$\\&");
			for (int indiceArray = 0; indiceArray < tokens.length; indiceArray++) {
				
				String local = tokens[indiceArray];
				if(StringUtils.notEmpty(local))
					nomesList.add(local);
			}
		}
	
		return nomesList;
	}
	
	/**
	 *  Retorna os dados formatados para visualização.
	 *  Já que o objeto cache guardas todos em uma única variável separados pela sequência '#$&'
	 */
	public List<String> getAnosFormatados() {
		
		
		List<String> nomesList = new ArrayList<String>();
		
		if(ano != null){
			String[] tokens = ano.split("\\#\\$\\&");
			
			for (int indiceArray = 0; indiceArray < tokens.length; indiceArray++) {
				
				String ano1 = tokens[indiceArray];
				if(StringUtils.notEmpty(ano1))
					nomesList.add(ano1);
			}
		}
		
		return nomesList;
	}
	
	/**
	 *  Retorna os dados formatados para visualização.
	 *  Já que o objeto cache guardas todos em uma única variável separados pela sequência '#$&'
	 */
	public List<String> getEditorasFormatadas() {
		
		List<String> nomesList = new ArrayList<String>();
		
		if(editora != null){
			String[] tokens = editora.split(CatalogacaoUtil.SEPARADOR_VALORES_CACHE_REGEX);
			
			for (int indiceArray = 0; indiceArray < tokens.length; indiceArray++) {
				
				String editora1 = tokens[indiceArray];
				if(StringUtils.notEmpty(editora1))
					nomesList.add(editora1);
				
			}
		}
		
		return nomesList;
	}
	
	
	/**
	 *  Retorna os dados dos autores secundários formatados para visualização.
	 *  Já que o objeto cache guardas todos em uma única variável separados pela sequência '#$&'
	 */
	public List<String> getAutoresSecundariosFormatados() {
		
		List<String> nomesList = new ArrayList<String>();
		
		if(autoresSecundarios != null){
		
			String[] tokens = autoresSecundarios.split(CatalogacaoUtil.SEPARADOR_VALORES_CACHE_REGEX);
			
			for (int indiceArray = 0; indiceArray < tokens.length; indiceArray++) {
				
				String autorSecundario = tokens[indiceArray];
				if(StringUtils.notEmpty(autorSecundario))
					nomesList.add(autorSecundario);
				
			}
		}
	
		return nomesList;
	}
	
	
	
	
	/**
	 *  Retorna os dados formatados para visualização.
	 *  Já que o objeto cache guardas todos em uma única variável separados pela sequência '#$&'
	 */
	public List<String> getAssuntosFormatados() {
		
		List<String> nomesList = new ArrayList<String>();
		
		if(assunto != null){
		
			String[] tokens = assunto.split(CatalogacaoUtil.SEPARADOR_VALORES_CACHE_REGEX);
			
			for (int indiceArray = 0; indiceArray < tokens.length; indiceArray++) {
				
				String asnt = tokens[indiceArray];
				if(StringUtils.notEmpty(asnt) && ! nomesList.contains(asnt.trim())) // retira os duplicados
					nomesList.add(asnt);
				
			}
		}
		
		return nomesList;
	}
	
	/**
	 *  Retorna os dados formatados para visualização.
	 *  Já que o objeto cache guardas todos em uma única variável separados pela sequência '#$&'
	 */
	public List<String> getNotasGeraisFormatadas() {
		
		
		List<String> nomesList = new ArrayList<String>();
		
		if(notasGerais != null){
			String[] tokens = notasGerais.split(CatalogacaoUtil.SEPARADOR_VALORES_CACHE_REGEX);
			
			for (int indiceArray = 0; indiceArray < tokens.length; indiceArray++) {
				
				String notaTemp = tokens[indiceArray];
				if(StringUtils.notEmpty(notaTemp))
					nomesList.add(notaTemp);
			}
		}
		
		return nomesList;
	}
	
	/**
	 *  Retorna os dados formatados para visualização.
	 *  Já que o objeto cache guardas todos em uma única variável separados pela sequência '#$&'
	 */
	public List<String> getNotasConteudoFormatadas() {
		
		
		List<String> nomesList = new ArrayList<String>();
		
		if(notaDeConteudo != null){
			String[] tokens = notaDeConteudo.split(CatalogacaoUtil.SEPARADOR_VALORES_CACHE_REGEX);
			
			
			for (int indiceArray = 0; indiceArray < tokens.length; indiceArray++) {
				
				String notaDeConteudoTemp = tokens[indiceArray];
				if(StringUtils.notEmpty(notaDeConteudoTemp))
					nomesList.add(notaDeConteudoTemp);
			}
		}
		
		return nomesList;
	}
	
	/**
	 *  Retorna os dados formatados para visualização.
	 *  Já que o objeto cache guardas todos em uma única variável separados pela sequência '#$&'
	 */
	public List<String> getNotasLocaisFormatadas() {
		
		List<String> nomesList = new ArrayList<String>();
		
		if(notasLocais != null){
			String[] tokens = notasLocais.split(CatalogacaoUtil.SEPARADOR_VALORES_CACHE_REGEX);
			
			
			for (int indiceArray = 0; indiceArray < tokens.length; indiceArray++) {
				String notaLocaisTemp = tokens[indiceArray];
				if(StringUtils.notEmpty(notaLocaisTemp))
					nomesList.add(notaLocaisTemp);
			}
		}
		
		return nomesList;
	}
	
	
	
	/**
	 *  Retorna os dados formatados para visualização.
	 *  Já que o objeto cache guardas todos em uma única variável separados pela sequência '#$&'
	 */
	public List<String> getFormasVariantesTituloFormatadas() {
		
		
		List<String> nomesList = new ArrayList<String>();
		
		if(formasVarientesTitulo != null){
			String[] tokens = formasVarientesTitulo.split(CatalogacaoUtil.SEPARADOR_VALORES_CACHE_REGEX);
			
			
			for (int indiceArray = 0; indiceArray < tokens.length; indiceArray++) {
				
				String formasVarientesTituloTemp = tokens[indiceArray];
				if(StringUtils.notEmpty(formasVarientesTituloTemp))
					nomesList.add(formasVarientesTituloTemp);
			}
		}
		
		return nomesList;
	}
	
	/**
	 * Retorna os dados sobre as edições separadamente, para visualização.
	 */
	public  List<String> getEdicoesFormatadas() {
		
		List<String> nomesList = new ArrayList<String>();
		
		if(edicao != null){
			String[] tokens = edicao.split(CatalogacaoUtil.SEPARADOR_VALORES_CACHE_REGEX);
			
			for (int indiceArray = 0; indiceArray < tokens.length; indiceArray++) {
				
				String edicaoTemp = tokens[indiceArray];
				if(StringUtils.notEmpty(edicaoTemp))
					nomesList.add(edicaoTemp);
				
			}
		}
		
		return nomesList;
	}
	
	/**
	 *  Retorna os dados da localização eletrônica formatados para visualização.
	 *  Já que o objeto cache guardas todos em uma única variável separados pela sequência '#$&'
	 */
	public List<Map<String,String>> getLocalizacaoEnderecoEletronicoFormatados() {
		
		List<Map<String,String>> nomesList = new ArrayList<Map<String, String>>();
		
		
		Map<Integer, String[]> temp = new HashMap<Integer, String[]>();
		
		if(localizacaoEnderecoEletronico != null){
			
			String [] auxiliar = localizacaoEnderecoEletronico.split(CatalogacaoUtil.SEPARADOR_VALORES_CACHE_REGEX);
			
			for (String string : auxiliar) {
				
				if(string.contains(CatalogacaoUtil.SEPARADOR_LABEL_ENDERECO_ELETRONICO)){ // é um label
				
					Pattern padrao = Pattern.compile(CatalogacaoUtil.SEPARADOR_LABEL_ENDERECO_ELETRONICO+"[\\d]+"+CatalogacaoUtil.SEPARADOR_LABEL_ENDERECO_ELETRONICO); 
					Matcher pesquisa = padrao.matcher(string);
				
					if((pesquisa.find())){
						int inicio = pesquisa.start();
						int fim = pesquisa.end();
	
						Integer idCampoLabel = Integer.parseInt(string.substring(inicio+3, fim-3));
						
						if(temp.containsKey(idCampoLabel)){
							String[] dadosLabel = temp.get(idCampoLabel);
							dadosLabel[0] = string.substring(fim);
						}else{
							temp.put(idCampoLabel, new String[]{string.substring(fim), null});
						}
					}
				}
				
				if(string.contains(CatalogacaoUtil.SEPARADOR_TEXTO_ENDERECO_ELETRONICO)){ // é um endereço
					
					Pattern padrao = Pattern.compile(CatalogacaoUtil.SEPARADOR_TEXTO_ENDERECO_ELETRONICO_REGEX+"[\\d]+"+CatalogacaoUtil.SEPARADOR_TEXTO_ENDERECO_ELETRONICO_REGEX); 
					Matcher pesquisa = padrao.matcher(string);
				
					if((pesquisa.find())){
						int inicio = pesquisa.start();
						int fim = pesquisa.end();
	
						Integer idCampoLabel = Integer.parseInt(string.substring(inicio+3, fim-3));
						
						if(temp.containsKey(idCampoLabel)){
							String[] dadosEndereco = temp.get(idCampoLabel);
							dadosEndereco[1] = string.substring(fim);
						}else{
							temp.put(idCampoLabel, new String[]{null, string.substring(fim)});
						}
					}
				}
				
			}
			
			for (Integer idCampo : temp.keySet()) {
				Map<String, String> mapa = new HashMap<String, String>();
				String[] dadosEnderecoEletronico = temp.get( idCampo);
				if(StringUtils.notEmpty(dadosEnderecoEletronico[0]))
					mapa.put("descricao" , dadosEnderecoEletronico[0]); // O LABEL
				else
					mapa.put("descricao" , dadosEnderecoEletronico[1]); // SE não tiver label coloca no próprio endereço.
				
				mapa.put("url" , dadosEnderecoEletronico[1]);       // O ENDEREÇO
				nomesList.add(mapa);
			}
		}
	
		return nomesList;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}


	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	
	/**
	 *   <strong>IMPORTANTE.: Sempre que criar uma nova variável no cache, apague o valor dela nesse
	 *   método, senão vai gerar falta de sincronismo. Quando o usuário apagar um dado do Título ele
	 *   vai continuar no cache.</strong>
	 * 
	 *   Método que apaga os dados do cache. tem que apagar todos os dados antes de atualizar o cache.
	 */
	public void zeraDadosMARCCache(){
		titulo = null;
		subTitulo = null;
		meioPublicacao = null;
		formasVarientesTitulo =null;
		autor = null;
		autoresSecundarios = null;
		assunto = null;
		localPublicacao  = null;
		editora  = null;
		ano  = null;
		edicao  = null;
		isbn  = null;
		numeroChamada  = null;
		localizacaoEnderecoEletronico  = null;
		tituloAscii = null;
		tituloUniformeAscii = null;
		outrasInformacoesTituloAscii= null;
		autorAscii= null;
		autoresSecundariosAscii = null;
		assuntoAscii= null;
		localPublicacaoAscii= null;
		editoraAscii= null;
		anoPublicacao= null;
		notaDeConteudo = null;
		notasGerais = null;
		notasLocais = null;
		notasAscii= null;
		issn = null;
		descricaoFisica = null;
		serie = null;
		serieAscii = null;
		classificacao1 = null;
		classificacao2 = null;
		classificacao3 = null;
		classificacao1Ascii = null;
		classificacao2Ascii = null;
		classificacao3Ascii = null;
		entradaAutorizadaAutor = null;
		nomesRemissivosAutor = null;
		entradaAutorizadaAssunto = null;
		nomesRemissivosAssunto = null;
		campoEntradaAutorizada = null;
		entradaAutorizadaAutorAscii = null;
		nomesRemissivosAutorAscii = null;
		entradaAutorizadaAssuntoAscii = null;
		nomesRemissivosAssuntoAscii = null;
		intervaloPaginas = null;
		resumo = null;
		idiomaAscii = null;
	}
	
	
	/**
	 *   <strong>IMPORTANTE.: Sempre que criar uma nova variável *ASCII para pesquisa, altere esse método
	 *   para formatar os dados da pesquisa, senão o sistema não trará os dados precisos</strong><br/><br/>
	 * 
	 *   Método que retira o sinais de pontuação dos dados da pesquisa do cache. e atribui um espaço
	 *   no início e final do campo, para a busca com like '% campo %', funcionar
	 */
	public void formataDadosPesquisa(){
		if (tituloAscii != null)                   tituloAscii = " "+retiraPontuacaoCamposBuscas( tituloAscii ) +" ";
		if (tituloUniformeAscii != null)           tituloUniformeAscii = " "+retiraPontuacaoCamposBuscas( tituloUniformeAscii ) +" ";
		if (outrasInformacoesTituloAscii != null)  outrasInformacoesTituloAscii = " "+retiraPontuacaoCamposBuscas( outrasInformacoesTituloAscii )+" ";
		if (autorAscii != null)                    autorAscii= " "+retiraPontuacaoCamposBuscas( autorAscii )+" ";
		if (autoresSecundariosAscii != null)       autoresSecundariosAscii = " "+retiraPontuacaoCamposBuscas( autoresSecundariosAscii )+" ";
		if (assuntoAscii != null)                  assuntoAscii= " "+retiraPontuacaoCamposBuscas( assuntoAscii )+" ";
		if (localPublicacaoAscii != null)          localPublicacaoAscii= " "+retiraPontuacaoCamposBuscas( localPublicacaoAscii )+" ";
		if (editoraAscii != null)                  editoraAscii= " "+retiraPontuacaoCamposBuscas( editoraAscii )+" ";
		if (notasAscii != null)              	   notasAscii= " "+retiraPontuacaoCamposBuscas( notasAscii )+" ";
		if (serieAscii != null)                    serieAscii = " "+retiraPontuacaoCamposBuscas( serieAscii )+" ";
		if (entradaAutorizadaAutorAscii != null)   entradaAutorizadaAutorAscii = " "+retiraPontuacaoCamposBuscas( entradaAutorizadaAutorAscii )+" ";
		if (nomesRemissivosAutorAscii != null)     nomesRemissivosAutorAscii = " "+retiraPontuacaoCamposBuscas( nomesRemissivosAutorAscii )+" ";
		if (entradaAutorizadaAssuntoAscii != null) entradaAutorizadaAssuntoAscii = " "+retiraPontuacaoCamposBuscas( entradaAutorizadaAssuntoAscii )+" ";
		if (nomesRemissivosAssuntoAscii != null)   nomesRemissivosAssuntoAscii = " "+retiraPontuacaoCamposBuscas( nomesRemissivosAssuntoAscii )+" ";
		if (classificacao1Ascii != null)           classificacao1Ascii = " "+classificacao1Ascii+" "; // nas classificações deixa a pontuação
		if (classificacao2Ascii != null)           classificacao2Ascii = " "+classificacao2Ascii+" "; // nas classificações deixa a pontuação
		if (classificacao3Ascii != null)           classificacao3Ascii = " "+classificacao3Ascii+" "; // nas classificações deixa a pontuação
		if (idiomaAscii != null)                   idiomaAscii = " "+retiraPontuacaoCamposBuscas( idiomaAscii )+" ";
		
	}
	
	
	/**
	 *    Recupera os dados do campo que deve ser mostrado ao usuário. <br/>
	 *    <i>( Usado na busca por listas, onde o campo a ser mostrado para o usuário muda conforme o filtro )</i> 
	 *
	 * @param tipoCampo
	 * @return
	 */
	public List<String> getCampoMostrarUsuarioPesquisaLista(){
		
		switch(tipoCampoBuscaPorListas){
			case CampoPesquisaPorListas.TITULO:
				List<String> temp = new ArrayList<String>();
				temp.add(titulo +" "+ ( StringUtils.notEmpty( subTitulo ) ? subTitulo : " ") );
				return temp;
			case CampoPesquisaPorListas.AUTOR: 
				List<String> temp1 = new ArrayList<String>();
				temp1.add(autor);
				temp1.addAll(getAutoresSecundariosFormatados());
				return temp1;
			case CampoPesquisaPorListas.ASSUNTO:  
				return getAssuntosFormatados();	
			case CampoPesquisaPorListas.SERIE:           
				List<String> temp2 = new ArrayList<String>();
				temp2.add(serie);
				return temp2;
			case CampoPesquisaPorListas.LOCAL:
				return getLocaisPublicacaoFormatados();
			case CampoPesquisaPorListas.EDITORA:
				return getEditorasFormatadas();
			case CampoPesquisaPorListas.CLASSIFICACAO_1:
				return getClassificacao1Formatada();
			case CampoPesquisaPorListas.CLASSIFICACAO_2:
				return getClassificacao2Formatada();
			case CampoPesquisaPorListas.CLASSIFICACAO_3:
				return getClassificacao3Formatada();
			case CampoPesquisaPorListas.NUMERO_CHAMADA:
				List<String> temp4 = new ArrayList<String>();
				temp4.add(numeroChamada);
				return temp4;
			case CampoPesquisaPorListas.ISBN:
				List<String> temp5 = new ArrayList<String>();
				temp5.add(isbn);
				return temp5;
			case CampoPesquisaPorListas.ISSN:
				List<String> temp6 = new ArrayList<String>();
				temp6.add(issn);
				return temp6;
			default:
					return new ArrayList<String>();
			}
	}
	
	
	//////////////// Set's e get's //////////////

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
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

	public void setAno(String ano) {
		this.ano = ano;
	}

	public void setEdicao(String edicao) {
		this.edicao = edicao;
	}

	public String getNumeroChamada() {
		return numeroChamada;
	}

	public void setNumeroChamada(String numeroChamada) {
		this.numeroChamada = numeroChamada;
	}

	public String getTituloAscii() {
		return tituloAscii;
	}

	public void setTituloAscii(String tituloAscii) {
		this.tituloAscii = tituloAscii;
	}

	public String getAutorAscii() {
		return autorAscii;
	}

	public void setAutorAscii(String autorAscii) {
		this.autorAscii = autorAscii;
	}

	public String getAssuntoAscii() {
		return assuntoAscii;
	}

	public void setAssuntoAscii(String assuntoAscii) {
		this.assuntoAscii = assuntoAscii;
	}

	public String getLocalPublicacaoAscii() {
		return localPublicacaoAscii;
	}

	public void setLocalPublicacaoAscii(String localPublicacaoAscii) {
		this.localPublicacaoAscii = localPublicacaoAscii;
	}

	public void setEditoraAscii(String editoraAscii) {
		this.editoraAscii = editoraAscii;
	}

	public Integer getIdObraDigitalizada() {
		return idObraDigitalizada;
	}

	public void setIdObraDigitalizada(Integer idObraDigitalizada) {
		this.idObraDigitalizada = idObraDigitalizada;
	}

	public int getNumeroDoSistema() {
		return numeroDoSistema;
	}

	public void setNumeroDoSistema(int numeroDoSistema) {
		this.numeroDoSistema = numeroDoSistema;
	}
	
	public String getIntervaloPaginas() {
		return intervaloPaginas;
	}

	public void setIntervaloPaginas(String intervaloPaginas) {
		this.intervaloPaginas = intervaloPaginas;
	}

	public String getResumo() {
		return resumo;
	}

	public void setResumo(String resumo) {
		this.resumo = resumo;
	}
	
	/**
	 * Retorna uma lista com o(s) resumo(s), se houver.
	 */
	public List<String> getResumosFormatados() {
		if ( resumo != null )
			return Arrays.asList( this.resumo.split(CatalogacaoUtil.SEPARADOR_VALORES_CACHE_REGEX) );
		else
			return Collections.emptyList();
	}
	
	/**
	 *   Na entrada autorizada do autor estou colocando a indicação do campo ex.: 100$a para o
	 * usuário saber qual campo gerou essa entrada autorizada. Esse método retorna a entrada autorizada
	 * <strong>sem</strong> essa indicação do campo.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/
	 */
	public String getEntradaAutorizadaAutor() {
		return entradaAutorizadaAutor;
	}
	
	/**
	 *   Na entrada autorizada do autor estou colocando a indicação do campo ex.: 100$a para o
	 * usuário saber qual campo gerou essa entrada autorizada. Esse método retorna a entrada autorizada
	 * <strong>com</strong> essa indicação do campo.
	 */
	public String getEntradaAutorizadaAutorComIndicacaoCampo() {
		
		String retorno = campoEntradaAutorizada!= null ? campoEntradaAutorizada :" ";
		
		if(StringUtils.notEmpty(entradaAutorizadaAutor))
			return retorno+" "+entradaAutorizadaAutor;
		
		return  "";
	}
	
	public void setEntradaAutorizadaAutor(String entradaAutorizadaAutor) {
		this.entradaAutorizadaAutor = entradaAutorizadaAutor;
	}

	public String getNomesRemissivosAutor() {
		return nomesRemissivosAutor;
	}

	public void setNomesRemissivosAutor(String nomesRemissivosAutor) {
		this.nomesRemissivosAutor = nomesRemissivosAutor;
	}

	/**
	 * Na entrada autorizada do assunto estou colocando a indicação do campo ex.: 100$a para o
	 * usuário saber qual campo gerou essa entrada autorizada. Esse método retorna a entrada autorizada
	 * <strong>sem</strong> essa indicação do campo.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/
	 */
	public String getEntradaAutorizadaAssunto() {
		return entradaAutorizadaAssunto;
	}
	
	/**
	 * Na entrada autorizada do assunto estou colocando a indicação do campo ex.: 100$a para o
	 * usuário saber qual campo gerou essa entrada autorizada. Esse método retorna a entrada autorizada
	 * <strong>com</strong> essa indicação do campo.
	 */
	public String getEntradaAutorizadaAssuntoComIndicacaoCampo() {
		String retorno = campoEntradaAutorizada!= null ? campoEntradaAutorizada :" ";
		
		if(StringUtils.notEmpty(entradaAutorizadaAssunto))
			return retorno+" "+entradaAutorizadaAssunto;
		
		return  "";
	}
	
	public void setEntradaAutorizadaAssunto(String entradaAutorizadaAssunto) {
		this.entradaAutorizadaAssunto = entradaAutorizadaAssunto;
	}

	public String getNomesRemissivosAssunto() {
		return nomesRemissivosAssunto;
	}

	public void setNomesRemissivosAssunto(String nomesRemissivosAssunto) {
		this.nomesRemissivosAssunto = nomesRemissivosAssunto;
	}

	public String getEntradaAutorizadaAutorAscii() {
		return entradaAutorizadaAutorAscii;
	}

	public void setEntradaAutorizadaAutorAscii(String entradaAutorizadaAutorAscii) {
		this.entradaAutorizadaAutorAscii = entradaAutorizadaAutorAscii;
	}

	public String getNomesRemissivosAutorAscii() {
		return nomesRemissivosAutorAscii;
	}

	public void setNomesRemissivosAutorAscii(String nomesRemissivosAutorAscii) {
		this.nomesRemissivosAutorAscii = nomesRemissivosAutorAscii;
	}

	public String getEntradaAutorizadaAssuntoAscii() {
		return entradaAutorizadaAssuntoAscii;
	}

	public void setEntradaAutorizadaAssuntoAscii(
			String entradaAutorizadaAssuntoAscii) {
		this.entradaAutorizadaAssuntoAscii = entradaAutorizadaAssuntoAscii;
	}

	public String getNomesRemissivosAssuntoAscii() {
		return nomesRemissivosAssuntoAscii;
	}

	public void setNomesRemissivosAssuntoAscii(String nomesRemissivosAssuntoAscii) {
		this.nomesRemissivosAssuntoAscii = nomesRemissivosAssuntoAscii;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public void setLocalPublicacao(String localPublicacao) {
		this.localPublicacao = localPublicacao;
	}


	public void setEditora(String editora) {
		this.editora = editora;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getAssunto() {
		return assunto;
	}

	public String getLocalPublicacao() {
		return localPublicacao;
	}

	public String getAno() {
		return ano;
	}

	public int getQuantidadeMateriaisAtivosTitulo() {
		return quantidadeMateriaisAtivosTitulo;
	}

	/** Altera a quantidade calculada de materiais ativos do título. */
	public void setQuantidadeMateriaisAtivosTitulo(int quantidadeMateriaisAtivosTitulo) {
		if(quantidadeMateriaisAtivosTitulo > 0)
			this.quantidadeMateriaisAtivosTitulo = quantidadeMateriaisAtivosTitulo;
		else
			this.quantidadeMateriaisAtivosTitulo = 0;
	}

	public Integer getIdTituloCatalografico() {
		return idTituloCatalografico;
	}

	public void setIdTituloCatalografico(Integer idTituloCatalografico) {
		this.idTituloCatalografico = idTituloCatalografico;
	}

	public Integer getIdAutoridade() {
		return idAutoridade;
	}

	public void setIdAutoridade(Integer idAutoridade) {
		this.idAutoridade = idAutoridade;
	}

	public Integer getIdArtigoDePeriodico() {
		return idArtigoDePeriodico;
	}

	public void setIdArtigoDePeriodico(Integer idArtigoDePeriodico) {
		this.idArtigoDePeriodico = idArtigoDePeriodico;
	}

	public void setLocalizacaoEnderecoEletronico(String localizacaoEnderecoEletronico) {
		this.localizacaoEnderecoEletronico = localizacaoEnderecoEletronico;
	}

	public String getLocalizacaoEnderecoEletronico() {
		return localizacaoEnderecoEletronico;
	}
	
	public String getOutrasInformacoesTituloAscii() {
		return outrasInformacoesTituloAscii;
	}


	public void setOutrasInformacoesTituloAscii(String outrasInformacoesTituloAscii) {
		this.outrasInformacoesTituloAscii = outrasInformacoesTituloAscii;
	}


	public String getNotasAscii() {
		return notasAscii;
	}

	public void setNotasAscii(String notasAscii) {
		this.notasAscii = notasAscii;
	}

	public String getIssn() {
		return issn;
	}

	public void setIssn(String issn) {
		this.issn = issn;
	}
	
	public String getDescricaoFisica() {
		return descricaoFisica;
	}


	public void setDescricaoFisica(String descricaoFisica) {
		this.descricaoFisica = descricaoFisica;
	}

	public String getSerieAscii() {
		return serieAscii;
	}

	public void setSerieAscii(String serieAscii) {
		this.serieAscii = serieAscii;
	}
	
	/**
	 * Testa se o título possui algum endereço eletrônico.
	 */
	public boolean isPossuiEnderecoEletronico(){
		
		if(StringUtils.notEmpty(localizacaoEnderecoEletronico))
			return true;
		else
			return false;
	}

	public boolean isSelecionada() {
		return selecionada;
	}
	
	public void setSelecionada(boolean selecionada) {
		this.selecionada = selecionada;
	}

	public void setCampoEntradaAutorizada(String campoEntradaAutorizada) {
		this.campoEntradaAutorizada = campoEntradaAutorizada;
	}

	public boolean isCatalogado() {
		return catalogado;
	}

	public void setCatalogado(boolean catalogado) {
		this.catalogado = catalogado;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public String getSubTitulo() {
		return subTitulo;
	}

	public void setSubTitulo(String subTitulo) {
		this.subTitulo = subTitulo;
	}

	public void setAutoresSecundarios(String autoresSecundarios) {
		this.autoresSecundarios = autoresSecundarios;
	}

	public void setAutoresSecundariosAscii(String autoresSecundariosAscii) {
		this.autoresSecundariosAscii = autoresSecundariosAscii;
	}

	public void setFormasVarientesTitulo(String formasVarientesTitulo) {
		this.formasVarientesTitulo = formasVarientesTitulo;
	}

	public void setNotaDeConteudo(String notaDeConteudo) {
		this.notaDeConteudo = notaDeConteudo;
	}

	public void setNotasGerais(String notasGerais) {
		this.notasGerais = notasGerais;
	}

	public void setTipoCampoBuscaPorListas(int tipoCampoBuscaPorListas) {
		this.tipoCampoBuscaPorListas = tipoCampoBuscaPorListas;
	}
	
	public int getTipoCampoBuscaPorListas() {
		return tipoCampoBuscaPorListas;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public String getNotasLocais() {
		return notasLocais;
	}
	
	public void setNotasLocais(String notasLocais) {
		this.notasLocais = notasLocais;
	}

	public Integer getAnoPublicacao() {
		return anoPublicacao;
	}

	public void setAnoPublicacao(Integer anoPublicacao) {
		this.anoPublicacao = anoPublicacao;
	}

	public String getFormasVarientesTitulo() {
		return formasVarientesTitulo;
	}

	public String getAutoresSecundarios() {
		return autoresSecundarios;
	}

	public String getEditora() {
		return editora;
	}

	public String getSerie() {
		return serie;
	}

	public String getEdicao() {
		return edicao;
	}

	public String getNotaDeConteudo() {
		return notaDeConteudo;
	}

	public String getNotasGerais() {
		return notasGerais;
	}

	public String getAutoresSecundariosAscii() {
		return autoresSecundariosAscii;
	}

	public String getEditoraAscii() {
		return editoraAscii;
	}

	public String getTituloUniformeAscii() {
		return tituloUniformeAscii;
	}

	public void setTituloUniformeAscii(String tituloUniformeAscii) {
		this.tituloUniformeAscii = tituloUniformeAscii;
	}

	public boolean isImportado() {
		return importado;
	}

	public void setImportado(boolean importado) {
		this.importado = importado;
	}

	public long getQuantidadeVezesConsultado() {
		return quantidadeVezesConsultado;
	}

	public long getQuantidadeVezesVisualizado() {
		return quantidadeVezesVisualizado;
	}

	public long getQuantidadeVezesEmprestado() {
		return quantidadeVezesEmprestado;
	}

	public String getIdiomaAscii() {
		return idiomaAscii;
	}

	public void setIdiomaAscii(String idiomaAscii) {
		this.idiomaAscii = idiomaAscii;
	}

	public String getMeioPublicacao() {
		return meioPublicacao;
	}

	public void setMeioPublicacao(String meioPublicacao) {
		this.meioPublicacao = meioPublicacao;
	}


	public String getClassificacao1() {
		return classificacao1;
	}


	public void setClassificacao1(String classificacao1) {
		this.classificacao1 = classificacao1;
	}


	public String getClassificacao2() {
		return classificacao2;
	}


	public void setClassificacao2(String classificacao2) {
		this.classificacao2 = classificacao2;
	}


	public String getClassificacao3() {
		return classificacao3;
	}


	public void setClassificacao3(String classificacao3) {
		this.classificacao3 = classificacao3;
	}


	public String getClassificacao1Ascii() {
		return classificacao1Ascii;
	}


	public void setClassificacao1Ascii(String classificacao1Ascii) {
		this.classificacao1Ascii = classificacao1Ascii;
	}


	public String getClassificacao2Ascii() {
		return classificacao2Ascii;
	}


	public void setClassificacao2Ascii(String classificacao2Ascii) {
		this.classificacao2Ascii = classificacao2Ascii;
	}


	public String getClassificacao3Ascii() {
		return classificacao3Ascii;
	}


	public void setClassificacao3Ascii(String classificacao3Ascii) {
		this.classificacao3Ascii = classificacao3Ascii;
	}

}
