/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 01/08/2008
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 *    Representa um exemplar de um título catalográfico. Exemplares são os itens físicos de
 * um título.
 * 
 * @author Fred
 * @version 1.0 - Criação da Classe
 * @version 1.1 Jadson - Adição de novos campos depois de uma conversa com a bibliotecária.
 *                       E adicionando alguns comentários para depois saber o que significa
 * @version 1.2 Jadson - Refactor de ItemCatalografico para Exemplar. E a separação das informações
 *                 de Fascículos  (periódicos) em outra entidade.
 * 
 */
@Entity
@Table(name = "exemplar", schema = "biblioteca")
@PrimaryKeyJoinColumn (name="id_exemplar", referencedColumnName="id_material_informacional")
public class Exemplar extends MaterialInformacional{

	/** Número patrimônio = número do tombamento = código de barras (para os novos livros)*/
	@Column(name = "numero_patrimonio")
	private Long numeroPatrimonio;
	
	/** Guarda notas de tese e dissertação que um exemplar pode ter */
	@Column(name = "nota_tese_dissertacao", nullable=true)
	private String notaTeseDissertacao;

	/** Guarda notas de conteúdo que um exemplar pode ter */
	@Column(name = "nota_conteudo", nullable=true)
	private String notaConteudo;
	

	/** Guarda o exemplar a quem esse anexo pertence   */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_exemplar_de_quem_sou_anexo", referencedColumnName = "id_exemplar")
	private Exemplar exemplarDeQuemSouAnexo;
	
	/**
	 * Isso existe para implementar uma regra da circulação, que diz, que mais de um exemplar de um mesmo
	 * Título não pode ser emprestado a um mesmo usuário ao mesmo tempo.
	 */
	@Column(name="numero_volume", nullable=true)
	private Integer numeroVolume;
	
	
	/**
	 * <p>Além do número de volume alguns exemplares podem ter o "Tomo".  Outra informações que eles desejam registrar.</p>
	 * <p>Do mesmo jeito que o volume, se o tomo não for informado ele é considerado "Único".  </p>
	 * 
	 * <p>Adicionado em 19/08/2011</p>
	 */
	@Column(name="tomo", nullable=true)
	private String tomo;
	
	
	
	/** Indica se o exemplar foi comprado ou doado. */
	@Column(name="tipo_tombamento")
	private Short tipoTombamento;

	/** O título desse exemplar. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_titulo_catalografico", referencedColumnName = "id_titulo_catalografico")
	private TituloCatalografico tituloCatalografico;

	/** Diz se este exemplar é anexo de algum material */
	private boolean anexo = false;



	///////////////  INFORMACOES DE REFERÊNCIA NO SIPAC PARA POSTERIOR CONSULTA  ///////////////


	/**
	 * Guarda o id do bem que gerou esse exemplar a partir desse id é possível obter
	 * informações como o preço da compra do exemplar.
	 * <p>
	 * <b>OBS.:</b>    Anexos não possuem isso porque eles não são tombados, ou seja, o SIPAC
	 * nem toma conhecimento de que eles existem.
	 */
	@Column(name="id_bem", nullable = true )
	private int idBem;


	///////////////////////////////////////////////////////////////////////////////////////////


	/**  Campo transiente utilizado para auxiliar no caso de uso de gerar etiquetas  */
	@Transient
	private boolean gerarEtiqueta;

	/**
	 * Construtor default, evitar usar.
	 */
	public Exemplar(){
		// vazio
	}

	/**
	 * Cria um item já persistido apenas com id para buscar no banco o resto das informações.
	 */
	public Exemplar(Integer id){
		this.id = id;
	}

	/**
	 * Testa se esse exemplar é anexo de outro ou não.
	 */
	public boolean isAnexo(){
		if( exemplarDeQuemSouAnexo != null || anexo)
			return true;
		else
			return false;
	}
	
	public void setAnexo(boolean anexo) {
		this.anexo = anexo;
	}

	@Override
	public int getId() {
		return id;
	}
	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getCodigoBarras() {
		return codigoBarras;
	}

	@Override
	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}

	public String getNotaTeseDissertacao() {
		return notaTeseDissertacao;
	}

	public void setNotaTeseDissertacao(String notaTeseDissertacao) {
		this.notaTeseDissertacao = notaTeseDissertacao;
	}

	public String getNotaConteudo() {
		return notaConteudo;
	}

	public void setNotaConteudo(String notaConteudo) {
		this.notaConteudo = notaConteudo;
	}

	public Long getNumeroPatrimonio() {
		return numeroPatrimonio;
	}

	public void setNumeroPatrimonio(Long numeroPatrimonio) {
		this.numeroPatrimonio = numeroPatrimonio;
	}

	public Integer getNumeroVolume() {
		return numeroVolume;
	}
	
	public void setNumeroVolume(Integer numeroVolume) {
		this.numeroVolume = numeroVolume;
	}

	/**
	 *   Cria o código de barras de um anexo se ele ainda não foi criado.
	 */
	public int criaCodigoBarrasAnexo(Exemplar exemplarPrincipal) throws NegocioException{
		
		int numeroGerador = exemplarPrincipal.getNumeroGeradorCodigoBarrasAnexos();
		
		codigoBarras = exemplarPrincipal.getCodigoBarras()
			+ BibliotecaUtil.geraCaraterCorespondente(numeroGerador);
		
		numeroGerador++;
		
		return numeroGerador;
	}
	
	public int getIdBem() {
		return idBem;
	}
	
	public void setIdBem(int idBem) {
		this.idBem = idBem;
	}

	/**
	 * Indica se é para gerar a etiqueta de lombada ou de código de barras para esse exemplar.
	 */
	public boolean isGerarEtiqueta() {
		return gerarEtiqueta;
	}

	/**
	 * Usado pelo JSF para configurar essa variável
	 */
	public void setGerarEtiqueta(boolean gerarEtiqueta) {
		this.gerarEtiqueta = gerarEtiqueta;
	}

	public Exemplar getExemplarDeQuemSouAnexo() {
		return exemplarDeQuemSouAnexo;
	}

	public void setExemplarDeQuemSouAnexo(Exemplar exemplarDeQuemSouAnexo) {
		this.exemplarDeQuemSouAnexo = exemplarDeQuemSouAnexo;
	}


	@Override
	public TituloCatalografico getTituloCatalografico() {
		return tituloCatalografico;
	}

	@Override
	public void setTituloCatalografico(TituloCatalografico tituloCatalografico) {
		this.tituloCatalografico = tituloCatalografico;
	}

	public Short getTipoTombamento() {
		return tipoTombamento;
	}

	public void setTipoTombamento(Short tipoTombamento) {
		this.tipoTombamento = tipoTombamento;
	}

	public String getTomo() {
		return tomo;
	}

	public void setTomo(String tomo) {
		this.tomo = tomo;
	}
	
}
