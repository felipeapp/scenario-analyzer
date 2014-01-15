/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 *    <p>Entidade que guarda as informa��es espec�ficas dos materiais informacionais que possuem
 * per�odo de publica��o regular (1 vez por semana, 1 vez por m�s).<p/>
 *    <p>Exemplo de fasc�culos s�o: uma revista, um jornal, entre outros.<p/>
 *
 * @author Jadson
 * @since 23/03/2009
 * @version 1.0 Cria��o da classe
 *
 */
@Entity
@Table(name = "fasciculo", schema = "biblioteca")
@PrimaryKeyJoinColumn(name="id_fasciculo", referencedColumnName="id_material_informacional" )
public class Fasciculo extends MaterialInformacional {

	/**    Ano "real" do peri�dico ex.: 1979, 2009, 2010, 3050, ...   */
	@Column(name = "ano_cronologico")
	private String anoCronologico;
	
	/**    N�mero que faz refer�ncia ao ano ex: 1870 => ano = 1, 1871 => ano = 2, 2009 => ano 40   */
	@Column(name = "ano")
	private String ano;
	
	/**    O volume dentro daquele ano. Na pr�tica funciona como um "lote"     */
	@Column(name = "volume")
	private String volume;

	/** O n�mero do fasc�culo dentro do volume. ex.: numero = 30. � o trig�simo fasc�culo do volume
	 * � String pois um fasc�culo pode ter mais de um n�mero ex: 1-2 */
	@Column(name = "numero")
	private String numero;

	/** N�mero do fasc�culo contando desde do primeiro ano, primeiro volume. */
	@Column(name = "edicao")
	private String edicao;

	/** Dia / m�s que o peri�dico foi publicado. <i> (informa��o opcional ) </i> */
	@Column(name = "dia_mes")
	private String diaMes;
	
	/**  Guarda a descri��o do que acompanha o fasc�culo  */
	@Column(name = "descricao_suplemento")
	private String descricaoSuplemento;
	

	/** Se o fasc�culo � suplemento de algu�m ele deve guardar de quem ele � suplemento */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_fasciculo_de_quem_sou_suplemento", referencedColumnName = "id_fasciculo")
	private Fasciculo fasciculoDeQuemSouSuplemento;
		
	/**     Fasc�culo vai ser criado pelo setor de compras mas ele so pode aparecer nas pesquisas
	 *   depois que foi inclu�do no acervo, essa vari�vel indica isso.
	 */
	@Column(name = "incluido_acervo", nullable=false)
	private boolean incluidoAcervo;
	
	
	/** A assinatura que esse fasc�culo pertence  */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_assinatura", referencedColumnName = "id_assinatura")
	private Assinatura assinatura;


	/** Os artigos que o fasc�culo possui e foram catalogados no sistema  */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fasciculo")
	private List<ArtigoDePeriodico> artigos;
	
	
	/** Diz se este fasc�culo � suplemento de algum outro fasc�culo */
	private boolean suplemento = false;
	
	
	
	/** Guarda a quantidade de artigos que o fasc�culo possui na mem�ria porque a relacionamento est� lazy*/
	@Transient
	private int quantidadeArtigos = 0;

	
	/** Guarda temporariamente alguma quantidade ou id de algum objeto referente ao fasc�culo */
	@Transient
	private int identificador = 0;
	
	/**
	 * Construtor padr�o para o Hibernate.
	 */
	public Fasciculo() {
		// vazio
	}
	
	/**
	 * Construtor padr�o de um objeto persistente.
	 */
	public Fasciculo(int id) {
		this.id = id;
	}

	/**
	 * Construtor que cria um fasc�culo com uma assinatura e um biblioteca.
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
	 * Testa se esse exemplar � anexo de outro ou n�o.
	 */
	public boolean isSuplemento(){
		if( fasciculoDeQuemSouSuplemento != null || suplemento)
			return true;
		else
			return false;
	}
	
	/**
	 * Gera o c�digo de barras dos fasc�culos segundo a l�gica do sistema:  "C�digo Asssinatura" + "-" + "N�mero Sequencial"
	 *
	 * @void
	 */
	public void geraCodigoBarrasFasciculo(String codigoAssinatura, int numeroFasciculoNaAssinatura){
		this.setCodigoBarras(codigoAssinatura+"-"+numeroFasciculoNaAssinatura);
	}
	

	/**
	 *   Cria o c�digo de barras de um suplemento se ele ainda n�o foi criado
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
	 * Adiciona um artigo ao fasc�culo
	 */
	public void addArtigo(ArtigoDePeriodico artigo){
		
		if(artigos == null)
			artigos = new ArrayList<ArtigoDePeriodico>();
		
		artigos.add(artigo);
		
	}
	
	/**
	 * Data formatada para a p�gina
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
