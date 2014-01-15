/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 01/09/2008
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 *    <p>Entidade que guarda as informações específicas dos materiais informacionais que possuem
 * período de publicação regular (1 vez por semana, 1 vez por mês).<p/>
 *    <p>Exemplo de fascículos são: uma revista, um jornal, entre outros.<p/>
 *
 * @author Jadson
 * @since 23/03/2009
 * @version 1.0 Criação da classe
 *
 */
@Entity
@Table(name = "fasciculo", schema = "biblioteca")
@PrimaryKeyJoinColumn(name="id_fasciculo", referencedColumnName="id_material_informacional" )
public class Fasciculo extends MaterialInformacional {

	/**    Ano "real" do periódico ex.: 1979, 2009, 2010, 3050, ...   */
	@Column(name = "ano_cronologico")
	private String anoCronologico;
	
	/**    Número que faz referência ao ano ex: 1870 => ano = 1, 1871 => ano = 2, 2009 => ano 40   */
	@Column(name = "ano")
	private String ano;
	
	/**    O volume dentro daquele ano. Na prática funciona como um "lote"     */
	@Column(name = "volume")
	private String volume;

	/** O número do fascículo dentro do volume. ex.: numero = 30. É o trigésimo fascículo do volume
	 * É String pois um fascículo pode ter mais de um número ex: 1-2 */
	@Column(name = "numero")
	private String numero;

	/** Número do fascículo contando desde do primeiro ano, primeiro volume. */
	@Column(name = "edicao")
	private String edicao;

	/** Dia / mês que o periódico foi publicado. <i> (informação opcional ) </i> */
	@Column(name = "dia_mes")
	private String diaMes;
	
	/**  Guarda a descrição do que acompanha o fascículo  */
	@Column(name = "descricao_suplemento")
	private String descricaoSuplemento;
	

	/** Se o fascículo é suplemento de alguém ele deve guardar de quem ele é suplemento */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_fasciculo_de_quem_sou_suplemento", referencedColumnName = "id_fasciculo")
	private Fasciculo fasciculoDeQuemSouSuplemento;
		
	/**     Fascículo vai ser criado pelo setor de compras mas ele so pode aparecer nas pesquisas
	 *   depois que foi incluído no acervo, essa variável indica isso.
	 */
	@Column(name = "incluido_acervo", nullable=false)
	private boolean incluidoAcervo;
	
	
	/** A assinatura que esse fascículo pertence  */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_assinatura", referencedColumnName = "id_assinatura")
	private Assinatura assinatura;


	/** Os artigos que o fascículo possui e foram catalogados no sistema  */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fasciculo")
	private List<ArtigoDePeriodico> artigos;
	
	
	/** Diz se este fascículo é suplemento de algum outro fascículo */
	private boolean suplemento = false;
	
	
	
	/** Guarda a quantidade de artigos que o fascículo possui na memória porque a relacionamento está lazy*/
	@Transient
	private int quantidadeArtigos = 0;

	
	/** Guarda temporariamente alguma quantidade ou id de algum objeto referente ao fascículo */
	@Transient
	private int identificador = 0;
	
	/**
	 * Construtor padrão para o Hibernate.
	 */
	public Fasciculo() {
		// vazio
	}
	
	/**
	 * Construtor padrão de um objeto persistente.
	 */
	public Fasciculo(int id) {
		this.id = id;
	}

	/**
	 * Construtor que cria um fascículo com uma assinatura e um biblioteca.
	 */
	public Fasciculo(Assinatura assinatura, Biblioteca biblioteca) {
		this.assinatura = assinatura;
		this.biblioteca = biblioteca;
	}
	
	/**
	 * Construtor usado para consultas do Hibernate.
	 */
	public Fasciculo(String codigoBarras, String anoCronologico, String ano, String numero, String volume, String edicao) {
		this.codigoBarras = codigoBarras;
		this.anoCronologico = anoCronologico;
		this.ano = ano;
		this.numero = numero;
		this.volume = volume;
		this.edicao = edicao;
	}
	
	
		
	/**
	 * Testa se esse exemplar é anexo de outro ou não.
	 */
	public boolean isSuplemento(){
		if( fasciculoDeQuemSouSuplemento != null || suplemento)
			return true;
		else
			return false;
	}
	
	/**
	 * Gera o código de barras dos fascículos segundo a lógica do sistema:  "Código Asssinatura" + "-" + "Número Sequencial"
	 *
	 * @void
	 */
	public void geraCodigoBarrasFasciculo(String codigoAssinatura, int numeroFasciculoNaAssinatura){
		this.setCodigoBarras(codigoAssinatura+"-"+numeroFasciculoNaAssinatura);
	}
	

	/**
	 *   Cria o código de barras de um suplemento se ele ainda não foi criado
	 */
	public int  criaCodigoBarrasSuplemento(Fasciculo fasciculoPrincipal) throws NegocioException{
		
		int numeroGerador = fasciculoPrincipal.getNumeroGeradorCodigoBarrasAnexos();
		
		codigoBarras = fasciculoPrincipal.getCodigoBarras()+ BibliotecaUtil.geraCaraterCorespondente(numeroGerador);
		
		numeroGerador++;
		
		return numeroGerador;
	}
	
	
	// sets e gets

	public int getQuantidadeArtigos() {
		return quantidadeArtigos;
	}

	public void setQuantidadeArtigos(int quantidadeArtigos) {
		this.quantidadeArtigos = quantidadeArtigos;
	}

	public Assinatura getAssinatura() {
		return assinatura;
	}

	@Override
	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	@Override
	public int getId() {
		return id;
	}

	public String getAno() {
		return ano;
	}

	public String getVolume() {
		return volume;
	}

	public String getEdicao() {
		return edicao;
	}

	@Override
	public RegistroEntrada getRegistroCriacao() {
		return registroCriacao;
	}

	@Override
	public Date getDataCriacao() {
		return dataCriacao;
	}

	@Override
	public RegistroEntrada getRegistroUltimaAtualizacao() {
		return registroUltimaAtualizacao;
	}

	@Override
	public Date getDataUltimaAtualizacao() {
		return dataUltimaAtualizacao;
	}

	@Override
	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public void setAssinatura(Assinatura assinatura) {
		this.assinatura = assinatura;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public void setEdicao(String edicao) {
		this.edicao = edicao;
	}


	public String getDescricaoSuplemento() {
		return descricaoSuplemento;
	}

	public void setDescricaoSuplemento(String descricaoSuplemento) {
		this.descricaoSuplemento = descricaoSuplemento;
	}

	public boolean isIncluidoAcervo() {
		return incluidoAcervo;
	}

	public void setIncluidoAcervo(boolean incluidoAcervo) {
		this.incluidoAcervo = incluidoAcervo;
	}

	public String getAnoCronologico() {
		return anoCronologico;
	}

	public void setAnoCronologico(String anoCronologico) {
		this.anoCronologico = anoCronologico;
	}


	public List<ArtigoDePeriodico> getArtigos() {
		return artigos;
	}

	public void setArtigos(List<ArtigoDePeriodico> artigos) {
		this.artigos = artigos;
	}

	
	/**
	 * Adiciona um artigo ao fascículo
	 */
	public void addArtigo(ArtigoDePeriodico artigo){
		
		if(artigos == null)
			artigos = new ArrayList<ArtigoDePeriodico>();
		
		artigos.add(artigo);
		
	}
	
	/**
	 * Data formatada para a página
	 */
	public String getDataCriacaoFormatada(){
		
		if(dataCriacao != null){
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			return df.format(dataCriacao);
		}else
			return "";
	}


	public Fasciculo getFasciculoDeQuemSouSuplemento() {
		return fasciculoDeQuemSouSuplemento;
	}


	public void setFasciculoDeQuemSouSuplemento(Fasciculo fasciculoDeQuemSouSuplemento) {
		this.fasciculoDeQuemSouSuplemento = fasciculoDeQuemSouSuplemento;
	}


	public void setSuplemento(boolean suplemento) {
		this.suplemento = suplemento;
	}


	public int getIdentificador() {
		return identificador;
	}


	public void setIdentificador(int identificador) {
		this.identificador = identificador;
	}

	public String getDiaMes() {
		return diaMes;
	}

	public void setDiaMes(String diaMes) {
		this.diaMes = diaMes;
	}
	
}
