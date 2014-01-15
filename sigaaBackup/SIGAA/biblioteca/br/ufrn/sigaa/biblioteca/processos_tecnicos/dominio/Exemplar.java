/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 *    Representa um exemplar de um t�tulo catalogr�fico. Exemplares s�o os itens f�sicos de
 * um t�tulo.
 * 
 * @author Fred
 * @version 1.0 - Cria��o da Classe
 * @version 1.1 Jadson - Adi��o de novos campos depois de uma conversa com a bibliotec�ria.
 *                       E adicionando alguns coment�rios para depois saber o que significa
 * @version 1.2 Jadson - Refactor de ItemCatalografico para Exemplar. E a separa��o das informa��es
 *                 de Fasc�culos  (peri�dicos) em outra entidade.
 * 
 */
@Entity
@Table(name = "exemplar", schema = "biblioteca")
@PrimaryKeyJoinColumn (name="id_exemplar", referencedColumnName="id_material_informacional")
public class Exemplar extends MaterialInformacional{

	/** N�mero patrim�nio = n�mero do tombamento = c�digo de barras (para os novos livros)*/
	@Column(name = "numero_patrimonio")
	private Long numeroPatrimonio;
	
	/** Guarda notas de tese e disserta��o que um exemplar pode ter */
	@Column(name = "nota_tese_dissertacao", nullable=true)
	private String notaTeseDissertacao;

	/** Guarda notas de conte�do que um exemplar pode ter */
	@Column(name = "nota_conteudo", nullable=true)
	private String notaConteudo;
	

	/** Guarda o exemplar a quem esse anexo pertence   */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_exemplar_de_quem_sou_anexo", referencedColumnName = "id_exemplar")
	private Exemplar exemplarDeQuemSouAnexo;
	
	/**
	 * Isso existe para implementar uma regra da circula��o, que diz, que mais de um exemplar de um mesmo
	 * T�tulo n�o pode ser emprestado a um mesmo usu�rio ao mesmo tempo.
	 */
	@Column(name="numero_volume", nullable=true)
	private Integer numeroVolume;
	
	
	/**
	 * <p>Al�m do n�mero de volume alguns exemplares podem ter o "Tomo".  Outra informa��es que eles desejam registrar.</p>
	 * <p>Do mesmo jeito que o volume, se o tomo n�o for informado ele � considerado "�nico".  </p>
	 * 
	 * <p>Adicionado em 19/08/2011</p>
	 */
	@Column(name="tomo", nullable=true)
	private String tomo;
	
	
	
	/** Indica se o exemplar foi comprado ou doado. */
	@Column(name="tipo_tombamento")
	private Short tipoTombamento;

	/** O t�tulo desse exemplar. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_titulo_catalografico", referencedColumnName = "id_titulo_catalografico")
	private TituloCatalografico tituloCatalografico;

	/** Diz se este exemplar � anexo de algum material */
	private boolean anexo = false;



	///////////////  INFORMACOES DE REFER�NCIA NO SIPAC PARA POSTERIOR CONSULTA  ///////////////


	/**
	 * Guarda o id do bem que gerou esse exemplar a partir desse id � poss�vel obter
	 * informa��es como o pre�o da compra do exemplar.
	 * <p>
	 * <b>OBS.:</b>    Anexos n�o possuem isso porque eles n�o s�o tombados, ou seja, o SIPAC
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
	 * Cria um item j� persistido apenas com id para buscar no banco o resto das informa��es.
	 */
	public Exemplar(Integer id){
		this.id = id;
	}

	/**
	 * Testa se esse exemplar � anexo de outro ou n�o.
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
	 *   Cria o c�digo de barras de um anexo se ele ainda n�o foi criado.
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
	 * Indica se � para gerar a etiqueta de lombada ou de c�digo de barras para esse exemplar.
	 */
	public boolean isGerarEtiqueta() {
		return gerarEtiqueta;
	}

	/**
	 * Usado pelo JSF para configurar essa vari�vel
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
