/*
 * ArtigoDePeriodico.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.biblioteca.util.CampoControleByEtiquetaComparator;
import br.ufrn.sigaa.biblioteca.util.CampoDadosByEtiquetaPosicaoComparator;

/**
 *     Classe que representa os artigos de periódicos (fascículos) que também pode ser catalogados no sistema.
 *     No ALEPH isto é chamado de Catalogação Analítica. Analítica porque deriva da catalogação do fascículo,
 * apenas é dado um nível de informação a mais.
 *     Esses artigos gerarão um título com as informações de autor(100$a), título (245$a)
 * , intervalo de páginas (773$g), palavras chaves (650$a) e resumo (520$a). O campo de controle
 * são os mesmos do título fascículo.
 * 
 * @author jadson
 * @since 05/05/2009
 * @version 1.0 criação da classe
 *
 */
@Entity
@Table(name = "artigo_de_periodico", schema = "biblioteca")
public class ArtigoDePeriodico implements PersistDB{

	
	/* INDICADORES QUE O SISTEMA DEVE GERAR NO CADASTRO DE ARTIGOS DE PERIÓDICOS */
	
	public static final char CDU_INDICADOR_1 = '#';
	public static final char CDU_INDICADOR_2 = '#';
	
	public static final char NUMERO_CHAMADA_INDICADOR_1 = ' ';
	public static final char NUMERO_CHAMADA_INDICADOR_2 = ' ';
	
	public static final char TITULO_INDICADOR_1 = '1';
	public static final char TITULO_INDICADOR_2 = '0';
	
	public static final char AUTOR_INDICADOR_1 = '1';
	public static final char AUTOR_INDICADOR_2 = ' ';
	
	public static final char AUTOR_SECUNDARIO_INDICADOR_1 = '1';
	public static final char AUTOR_SECUNDARIO_INDICADOR_2 = ' ';
	
	public static final char LIGACAO_INDICADOR_1 = ' ';
	public static final char LIGACAO_INDICADOR_2 = ' ';
	
	public static final char PALAVRAS_CHAVE_INDICADOR_1 = ' ';
	public static final char PALAVRAS_CHAVE_INDICADOR_2 = '4';
	
	public static final char PUBLICACAO_INDICADOR_1 = ' ';
	public static final char PUBLICACAO_INDICADOR_2 = ' ';
	
	public static final char RESUMO_INDICADOR_1 = ' ';
	public static final char RESUMO_INDICADOR_2 = ' ';
	
	
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column(name = "id_artigo_de_periodico")
	private int id;
	
	
	/**
	 * Guarda o que é chamado de formato do título (BK- Livro, SE - Periódico,
	 * MP - Mapa, VM - Material Visual, etc..)
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_formato_material", referencedColumnName = "id_formato_material")
	private FormatoMaterial formatoMaterial;
	
	
	/**
	 *   Guarda um número que identifica um artigo no sistema.
	 */
	@Column(name="numero_do_sistema")
	private int numeroDoSistema;
	
	
	/**
	 * O título catalográfico no formato MARC é formado por vários registros de controle e de dados
	 * OBS.: Tudo que acontecer com o título acontece com seus registros. persistir, remover, etc..
	 */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY,  mappedBy="artigo")
	@IndexColumn(name="posicao", base=0, nullable=false)
	private List<CampoControle> camposControle;


	/**
	 * O título catalográfico no formato MARC é formado por vários registros de dados
	 * OBS.: Tudo que acontecer com o título acontece com seus registros. persistir, remover, etc..
	 */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY,  mappedBy="artigo")
	@IndexColumn(name="posicao", base=0, nullable=false)
	private List<CampoDados> camposDados;
	
	
	/** A qual fascículo pertence esse artigo */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_fasciculo", referencedColumnName = "id_fasciculo")
	private Fasciculo fasciculo;
	
	
	/**
	 *   Artigos podem ser apagadas do sistema, nesse caso eles são desativadas e seus
	 *   caches removidos para não aparecerem mais nas pesquisas.
	 */
	private boolean ativo = true;
	
	
	/** Usado para referenciar o registro no banco do sistema legado. */
	private String codmerg;
	
	
	/** Atributo do aleph, campo utilizado na migração */
	@Transient
	private int sequenceNumber;
	
	////////////////////////////INFORMACOES AUDITORIA  ///////////////////////////////////////


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_criacao")
	@CriadoPor
	private RegistroEntrada registroCriacao;

	/**
	 * data de cadastro
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	@Column(name="data_criacao")
	private Date dataCriacao;

	/**
	 * registro entrada  do usuário que realizou a última atualização
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroUltimaAtualizacao;

	/**
	 * data da última atualização
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_ultima_atualizacao")
	@AtualizadoEm
	private Date dataUltimaAtualizacao;


	//////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	/**
	 *   Usado pelo hibernate
	 */
	public ArtigoDePeriodico() {
		// construtor padrão
	}

	/**
	 *   Criando um já persistido.
	 */
	public ArtigoDePeriodico(int id) {
		this.id = id;
	}
	
	/**
	 * Construtor que cria um novo artigo
	 */
	public ArtigoDePeriodico(Fasciculo fasciculo) {
		this.fasciculo = fasciculo;
		this.fasciculo.addArtigo(this);
	}

	
	/**
	 * Adiciona um novo campo controle
	 */
	public void addCampoControle(CampoControle c){
		if(camposControle == null){
			camposControle = new ArrayList<CampoControle>();
		}
		camposControle.add(c);
	}

	/**
	 * Adiciona um novo campo de dados
	 */
	public void addCampoDados(CampoDados d){
		if(camposDados == null){
			camposDados = new ArrayList<CampoDados>();
		}
		camposDados.add(d);
	}
	
	
	
	/**
	 * 
	 * Retorna a lista de campos de controle ordenados pela etiqueta LDR -> 001 -> 003 ...
	 */
	public Collection<CampoControle> getCamposControleOrdenadosByEtiqueta() {
		
		if(camposControle == null) return new ArrayList<CampoControle>();
		
		Collections.sort(camposControle, new CampoControleByEtiquetaComparator());
		return camposControle;
	}
	
	
	/**
	 * Retorna a lista de campos de dados ordenados pela etiqueta 010 -> 022 -> 940 ...
	 */
	public Collection<CampoDados> getCamposDadosOrdenadosByEtiqueta() {
		
		if(camposDados == null) return new ArrayList<CampoDados>();
		
		Collections.sort(camposDados, new CampoDadosByEtiquetaPosicaoComparator());
		return camposDados;
	}
	



	// sets e gets
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public List<CampoControle> getCamposControle() {
		return camposControle;
	}

	public void setCamposControle(List<CampoControle> camposControle) {
		this.camposControle = camposControle;
	}

	public List<CampoDados> getCamposDados() {
		return camposDados;
	}

	public void setCamposDados(List<CampoDados> camposDados) {
		this.camposDados = camposDados;
	}

	public Fasciculo getFasciculo() {
		return fasciculo;
	}

	public void setFasciculo(Fasciculo fasciculo) {
		this.fasciculo = fasciculo;
	}

	public FormatoMaterial getFormatoMaterial() {
		return formatoMaterial;
	}

	public void setFormatoMaterial(FormatoMaterial formatoMaterial) {
		this.formatoMaterial = formatoMaterial;
	}

	public int getNumeroDoSistema() {
		return numeroDoSistema;
	}

	public void setNumeroDoSistema(int numeroDoSistema) {
		this.numeroDoSistema = numeroDoSistema;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		StringBuilder artigo = new StringBuilder();
		
		if(this.getCamposControleOrdenadosByEtiqueta() != null)
			for (CampoControle cc : this.getCamposControleOrdenadosByEtiqueta()) {
				artigo.append(cc.getEtiqueta().getTag()+" "+cc.getDado()+" \n");
			}
		
		if(this.getCamposDadosOrdenadosByEtiqueta() != null)
			for (CampoDados cd : this.getCamposDadosOrdenadosByEtiqueta()) {
				
				artigo.append(cd.getEtiqueta().getTag()+" "+(new Character(' ').equals(cd.getIndicador1()) ? '_' : cd.getIndicador1())
						+" "+(new Character(' ').equals(cd.getIndicador2()) ? '_' : cd.getIndicador2()));
				
				for (SubCampo  sc : cd.getSubCampos()) {
					artigo.append(" $"+sc.getCodigo()+" "+sc.getDado());
				}
				
				artigo.append(" \n");
			}
		
		return artigo.toString();
	}
	
	public RegistroEntrada getRegistroCriacao() {
		return registroCriacao;
	}

	public void setRegistroCriacao(RegistroEntrada registroCriacao) {
		this.registroCriacao = registroCriacao;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public RegistroEntrada getRegistroUltimaAtualizacao() {
		return registroUltimaAtualizacao;
	}

	public void setRegistroUltimaAtualizacao(
			RegistroEntrada registroUltimaAtualizacao) {
		this.registroUltimaAtualizacao = registroUltimaAtualizacao;
	}

	public Date getDataUltimaAtualizacao() {
		return dataUltimaAtualizacao;
	}

	public void setDataUltimaAtualizacao(Date dataUltimaAtualizacao) {
		this.dataUltimaAtualizacao = dataUltimaAtualizacao;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getCodmerg() {
		return codmerg;
	}

	public void setCodmerg(String codmerg) {
		this.codmerg = codmerg;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	

}
