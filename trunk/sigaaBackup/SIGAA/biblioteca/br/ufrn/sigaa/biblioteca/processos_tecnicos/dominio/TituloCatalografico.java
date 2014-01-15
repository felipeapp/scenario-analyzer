/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 18/07/2008
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
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.util.CampoControleByEtiquetaComparator;
import br.ufrn.sigaa.biblioteca.util.CampoDadosByEtiquetaPosicaoComparator;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.ensino.stricto.dominio.DadosDefesa;

/**
 * 
 * <p> Classe que representa um Título <i>( Catalogação )</i> no sistema. </p>
 * 
 *    <p> <i> Os dados contidos nesta entidade estão normalizados de acordo com o padrão MARC21 {@link http://www.loc.gov/marc/}. <br/>
 *    Se um Título for de periódicos ele vai possuir uma coleção de assinaturas de periódicos.<br/>
 *    Se um Título <strong>Não</strong> for de periódicos ele vai possuir uma coleção de exemplares.<br/>
 *    A Indicação se um Título <i>( Catalogação )</i>  é de periódico ou não vem do <span style="font-style: normal; font-family: courier, monospace;"> Formato do Material</span> que ele possui.<br/>
 *    Além das informações MARC, dos exemplares ou assinaturas, esta entidade mantém algumas informações em cache para otimizar  <br/>
 *    as consultas dos relatórios do sistema.  </i> </p>
 * 
 * 
 * @author Jadson
 * @since 18/07/2008
 * @version 1.0 Criação da classe
 * 
 */
@Entity
@Table(name = "titulo_catalografico", schema = "biblioteca")
public class TituloCatalografico implements PersistDB {

	/** O id */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column (name="id_titulo_catalografico")
	private int id;

	
	/**
	 * Guarda um número que identifica um título no sistema.
	 */
	@Column(name="numero_do_sistema")
	private int numeroDoSistema;

	
	
	/** Guarda o id da obras digitalizada em formato .pdf para aqueles títulos que possuem  */
	@Column(name="id_obra_digitalizada")
	private Integer idObraDigitalizada;
	
	
	/**
	 * <p>Guarda o formato do material do Título (BK- Livro, SE - Periódico,
	 * MP - Mapa, VM - Material Visual, etc..) </p>
	 * <p>Esses formatos são fixos do padrão MARC, se o formato for "SE - Periódico", o Título conterá um coleção
	 * de assinaturas, com seus respectivos fascículos. Qualquer outro formato, o Título possuirá exemplares.</p>
	 * <p>O formato do material também influencia a validação de alguns campos do Título.</p>
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_formato_material", referencedColumnName = "id_formato_material")
	private FormatoMaterial formatoMaterial;



	/**
	 * O título catalográfico no formato MARC é formado por vários registros de controle
	 * 
	 */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY,  mappedBy="tituloCatalografico")
	@IndexColumn(name="posicao", base=0, nullable=false)
	private List<CampoControle> camposControle;


	/**
	 * O título catalográfico no formato MARC é formado por vários registros de dados
	 * 
	 */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY,  mappedBy="tituloCatalografico")
	@IndexColumn(name="posicao", base=0, nullable=false)
	private List<CampoDados> camposDados;



	/**
	 * Guarda os exemplares desse título.
	 */
	@OneToMany(cascade = {}, fetch = FetchType.LAZY, mappedBy = "tituloCatalografico")
	private List<Exemplar> exemplares;


	/**
	 * Ou um Título catalográfico possui um conjunto de exemplares ou um conjunto de assinaturas de fascículos.
	 * Nunca os dois ao mesmo tempo.
	 */
	@OneToMany(cascade = {}, fetch = FetchType.LAZY, mappedBy = "tituloCatalografico")
	private List<Assinatura> assinaturas;
	
		
	/**
	 * Se o Título for catalogado por meio de importação, vai ser salvo com o valor desta variável = "true".<br/>
	 * Esta informações serve para os relatórios do setor de cooperação técnica. <br/>
	 * <strong>Observação.:</strong> A data da importação vai ser a data da criação do título
	 */
	@Column(name="importado", nullable=false)
	private boolean importado = false;
	
	
	/**
	 * Guarda a biblioteca de quem importou o título. Como um Título não está em uma
	 * biblioteca específica é preciso guardar qual a biblioteca que fez a sua importação.
	 * */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_biblioteca_importacao", referencedColumnName="id_biblioteca")
	private Biblioteca bibliotecaImportacao;
	

	/**
	 *   <p>Informação usada para saber se um Título foi catalogado por completo ou não.</p>
	 *   <p>Se um título não foi catalogado por completo - o bibliotecário não informou todos os campos obrigatórios -, o usuário
	 *   não vai poder incluir materiais.</p>
	 * 
	 *   </p>Se um arquivo de importação tiver vários Títulos, esses também serão salvos no
	 *   sistema como não catalogados, para obrigar o bibliotecário a revisar as suas catalogações antes
	 *   de incluir materiais.</p>
	 * 
	 */
	@Column(name="catalogado", nullable=false)
	private boolean catalogado = false;
	
	
	/**
	 *   Se Títulos não possuírem materiais ativos, eles poderão ser removidos do sistema.<br/>
	 *  <i> ( O cache é apagado para não aparecer mais nas pesquisas, e é atribuído a esta variável o valor "false" ) </i>
	 */
	private boolean ativo = true;
	
	
	
	////////////////////////////  DADOS EM CACHE PARA SER USADOS NOS RELATÓRIOS  ///////////////////////////////////////
	
	/** Contém o valor da classificação 1 desse Título */
	@Column(name="classificacao_1")
	private String classificacao1;
	
	/** Contém a valor da classe principal da classificaçao 1 dese Título */
	@Column(name="classe_principal_classificacao_1" )
	private String classePrincipalClassificacao1;
	
	/** Contém o valor da classificação 2 desse Título */
	@Column(name="classificacao_2")
	private String classificacao2;
	
	/** Contém a valor da classe principal da classificaçao 2 dese Título */
	@Column(name="classe_principal_classificacao_2" )
	private String classePrincipalClassificacao2;
	
	/** Contém o valor da classificação 3 desse Título */
	@Column(name="classificacao_3")
	private String classificacao3;
	
	/** Contém a valor da classe principal da classificaçao 3 dese Título */
	@Column(name="classe_principal_classificacao_3" )
	private String classePrincipalClassificacao3;
	
	
	
	/**
	 * Contém a classe CNPQ (apenas grandes áreas) correspondente à classificação 1 para otimizar os relatórios.
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_area_conhecimento_cnpq_classificacao_1", referencedColumnName="id_area_conhecimento_cnpq")
	private AreaConhecimentoCnpq areaConhecimentoCNPQClassificacao1;
	
	
	/** Contém a classe CNPQ (apenas grandes áreas) correspondente à classificação 2 para otimizar os relatórios. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_area_conhecimento_cnpq_classificacao_2", referencedColumnName="id_area_conhecimento_cnpq")
	private AreaConhecimentoCnpq areaConhecimentoCNPQClassificacao2;
	
	
	/**  Contém a classe CNPQ (apenas grandes áreas) correspondente à classificação 3 para otimizar os relatórios. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_area_conhecimento_cnpq_classificacao_3", referencedColumnName="id_area_conhecimento_cnpq")
	private AreaConhecimentoCnpq areaConhecimentoCNPQClassificacao3;
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	

	/**  Para títulos que foram migrados do sistema antigo. */
	private String codmerg;
	
	
	/**
	 * Se o título foi catalogado a partir de uma monografia ou tese, indica a
	 * defesa fonte da catalogação. Se for <tt>null</tt>, o título não foi criado a partir de
	 * uma defesa. */
	@Column(name="id_dados_defesa")
	private Integer idDadosDefesa = null;
	
	
	//////////////////////////// INFORMAÇÕES DE AUDITORIA  ///////////////////////////////////////


	/**
	 * informações de quem criou
	 */
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
	 * Guarda de forma temporária as informações do título no formato de referência específico.
	 *
	 */
	@Transient
	private String formatoReferencia;

	
	
	/**
	 * Guarda de forma temporária as informações da ficha catalográfica do Título
	 *
	 */
	@Transient
	private String fichaCatalografica;
	
	/**
	 * Guarda de forma temporária as informações do número de chamada
	 *
	 */
	@Transient
	private String numeroChamada;
	
	
	
	/** Atributo do ALEPH, campo utilizado na migração */
	@Transient
	private int sequenceNumber;
	
	/** Informações catalográficas do título. */
	@Transient
	private CacheEntidadesMarc cache;
	
	
	/**
	 * Guarda temporariamente a defesa associada a essa catalogação, já que é guardado somente o id.
	 */
	@Transient
	private DadosDefesa dadosDefesa;
	
	
	/**
	 * Construtor padrão
	 */
	public TituloCatalografico(){

	}

	/**
	 * Construtor recebendo id
	 * @param id
	 */
	public TituloCatalografico(int id){
		this.id = id;
	}
	
	/**
	 * Construtor recebendo o formato do material
	 */
	public TituloCatalografico(FormatoMaterial formato){
		this.formatoMaterial = formato;
	}


	/**
	 * Adiciona um novo campo controle
	 * @param c
	 */
	public void addCampoControle(CampoControle c){
		if(camposControle == null){
			camposControle = new ArrayList<CampoControle>();
		}
		camposControle.add(c);
	}

	/**
	 * Adiciona um novo campo de dados
	 * @param c
	 */
	public void addCampoDados(CampoDados d){
		if(camposDados == null){
			camposDados = new ArrayList<CampoDados>();
		}
		camposDados.add(d);
	}
	
	
	
	/**
	 * Adiciona todos os campos de dados passados.
	 * @param c
	 */
	public void addAllCampoDados(List<CampoDados> listaDados){
		if(camposDados == null){
			camposDados = new ArrayList<CampoDados>();
		}
		camposDados.addAll(listaDados);
	}

	
	
	/**
	 * 
	 * Retorna a quantidade de campos que o Título possui, seja de dados ou de controle.
	 *
	 * @return
	 */
	public int getQuantidadeCampos(){

		int qtd = 0;

		if(camposControle != null){
			qtd += camposControle.size();
		}

		if(camposDados != null ){
			qtd += camposDados.size();
		}

		return qtd;
	}


	/**
	 *  Retorna a quantidade de exemplares do título
	 * 
	 * @return
	 */
	public int getQuantidadeExemplaresAtivos(){
		int quantidade = 0;
		
		if(exemplares != null){
			for (Exemplar e : exemplares) {
				if(! e.isDadoBaixa() && e.isAtivo() )  quantidade++;
			}
		}
		
		return quantidade;
	}

	
	/**
	 * Retorna a quantidade de fascículos do título
	 * 
	 * @return
	 */
	public int getQuantidadeFasciculosAtivos(){
		int quantidade = 0;
		
		if(assinaturas != null ){
			
			for (Assinatura assinatura : assinaturas) {
				quantidade += assinatura.getQuantidadeFasciculosAtivos();
			}
			
			return quantidade;
		}else{
			return 0;
		}
	}

	
	/**
	 * <p>Retorna a quantidade total de materiais que o Título possui no acervo.</p>
	 * 
	 * <p>OBSERVAÇÃO 1: Precisa estar com a sessão do hibernate aberta para funcionar porque usa
	 * relacionamentos lazy.</p>
	 * <p>OBSERVAÇÃO 2: Uma maneira mais rápida de obter a quantidade de materiais de um Título é
	 * consultar no seu respectivo cache.</p>
	 * 
	 * @return A quantidade de materiais ativo <i>(não baixados e não removidos)</i> do Título.
	 */
	public int getQuantidadeMateriaisAtivos(){
		
		if( formatoMaterial != null && formatoMaterial.isFormatoPeriodico()){
			return getQuantidadeFasciculosAtivos();
		}else{
			return getQuantidadeExemplaresAtivos();
		}
		
	}
	


	/**
	 * Retorna a lista de campos de controle ordenados por suas etiquetas, exemplos: LDR -> 001 -> 003 -> ... -> 999.
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


	/**
	 *   Verifica se o título possui o campo de controle cuja tag foi passada
	 */
	public boolean contemCampoControle(String tag){

		if ( camposControle == null || camposControle.size() == 0){
			return false;
		}

		for (CampoControle campo : camposControle) {
			if(campo.getEtiqueta() != null && campo.getEtiqueta().getTag().equals(tag)){
				return true;
			}
		}

		return false;

	}

	
	/**
	 *   Verifica se o título possui o campo de dados cuja tag foi passada
	 */
	public boolean contemCampoDados(String tag){

		if ( camposDados == null || camposDados.size() == 0){
			return false;
		}

		for (CampoDados campo : camposDados) {
			if(campo.getEtiqueta() != null && campo.getEtiqueta().getTag().equals(tag)){
				return true;
			}
		}

		return false;

	}
	
	
	
	/**
	 * Dois títulos são iguais se tiverem o mesmo número do sistema.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(numeroDoSistema);
	}
	
	/**
	 * Dois títulos são iguais se tiverem o mesmo número do sistema.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "numeroDoSistema");
	}

	
	/**
	 * <p>Zera os dados correspondente a classificação 1 que são mantidos no Título para efeito de cache das consultas dos relatórios</p>
	 */
	public void zeraDadosClassificacao1(){
		this.setClassificacao1(null); 
		this.setClassePrincipalClassificacao1(null);
		this.setAreaConhecimentoCNPQClassificacao1(null);
	}
	
	/**
	 * <p>Zera os dados correspondente a classificação 1 que são mantidos no Título para efeito de cache das consultas dos relatórios</p>
	 */
	public void zeraDadosClassificacao2(){
		this.setClassificacao2(null); 
		this.setClassePrincipalClassificacao2(null);
		this.setAreaConhecimentoCNPQClassificacao2(null);
	}
	
	/**
	 * <p>Zera os dados correspondente a classificação 1 que são mantidos no Título para efeito de cache das consultas dos relatórios</p>
	 */
	public void zeraDadosClassificacao3(){
		this.setClassificacao3(null); 
		this.setClassePrincipalClassificacao3(null);
		this.setAreaConhecimentoCNPQClassificacao3(null);
	}
	
	
	/**
	 * <p>Anula as áreas de CNPq não informadas para não savar com o id = -1</p>
	 *
	 * <p><strong>Deve sempre ser chamado sempre antes de salvar ou atualizar o título </strong> </p>
	 */
	public void anularAreasCNPqNaoInformadas(){
		if(this.areaConhecimentoCNPQClassificacao1 != null && this.areaConhecimentoCNPQClassificacao1.getId() <= 0 )
			this.areaConhecimentoCNPQClassificacao1 = null;
		
		if(areaConhecimentoCNPQClassificacao2 != null && this.areaConhecimentoCNPQClassificacao2.getId() <= 0 )
			this.areaConhecimentoCNPQClassificacao2 = null;
		
		if(areaConhecimentoCNPQClassificacao3 != null && this.areaConhecimentoCNPQClassificacao3.getId() <= 0 )
			this.areaConhecimentoCNPQClassificacao3 = null;
	}
	
	
	/**
	 * Imprime todos as informações Título em Formato MARC.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder titulo = new StringBuilder();
		
		if(this.getFormatoMaterial() != null) // títulos importados podem não ter formato do material ainda.
			titulo.append(this.getFormatoMaterial().getDescricaoCompleta()+" \n");
		
		if(this.getCamposControleOrdenadosByEtiqueta() != null)
			for (CampoControle cc : this.getCamposControleOrdenadosByEtiqueta()) {
				titulo.append(cc.getEtiqueta().getTag()+" "+cc.getDado()+" \n");
			}
		
		if(this.getCamposDadosOrdenadosByEtiqueta() != null)
			for (CampoDados cd : this.getCamposDadosOrdenadosByEtiqueta()) {
				
				titulo.append(cd.getEtiqueta().getTag()+" "+  (new Character(' ').equals(cd.getIndicador1()) ? '_' : cd.getIndicador1())
						+" "+(new Character(' ').equals(cd.getIndicador2()) ? '_' : cd.getIndicador2()));
				
				for (SubCampo  sc : cd.getSubCampos()) {
					titulo.append(" $"+sc.getCodigo()+" "+sc.getDado());
				}
				
				titulo.append(" \n");
			}
		
		return titulo.toString();
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

	public int getNumeroDoSistema() {
		return numeroDoSistema;
	}

	public void setNumeroDoSistema(int numeroDoSistema) {
		this.numeroDoSistema = numeroDoSistema;
	}

	public FormatoMaterial getFormatoMaterial() {
		return formatoMaterial;
	}

	public void setFormatoMaterial(FormatoMaterial formatoMaterial) {
		this.formatoMaterial = formatoMaterial;
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
	
	
	/**
	 * Retorna os campos de dados que não são reservado no sistema, os campos cujos dados PODEM ser
	 * alterados pelos bibliotecários.
	 */
	public List<CampoDados> getCamposDadosNaoReservados() {
		
		List<CampoDados> temp = new ArrayList<CampoDados>();
		
		for (CampoDados campoDados : camposDados) {
			
			if(campoDados == null)
				continue;
			
			if(! campoDados.isCampoDeUsoReservador())
				temp.add(campoDados);
		}
		
		return temp;
	}
	
	
	/**
	 *   Retorna os campos de dados de uso reservado no sistema, os campos cujos dados NÃO podem ser
	 *   alterados pelos bibliotecários
	 *.
	 * @return
	 */
	public List<CampoDados> getCamposDadosReservados() {
		
		List<CampoDados> temp = new ArrayList<CampoDados>();
		
		for (CampoDados campoDados : camposDados) {
			
			if(campoDados == null)
				continue;
			
			if(campoDados.isCampoDeUsoReservador())
				temp.add(campoDados);
		}
		
		return temp;
	}
	

	public void setCamposDados(List<CampoDados> camposDados) {
		this.camposDados = camposDados;
	}


	public List<Exemplar> getExemplares() {
		return exemplares;
	}

	public void setExemplares(List<Exemplar> exemplares) {
		this.exemplares = exemplares;
	}

	

	public List<Assinatura> getAssinaturas() {
		return assinaturas;
	}

	public void setAssinaturas(List<Assinatura> assinaturas) {
		this.assinaturas = assinaturas;
	}

	public RegistroEntrada getRegistroCriacao() {
		return registroCriacao;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public RegistroEntrada getRegistroUltimaAtualizacao() {
		return registroUltimaAtualizacao;
	}

	public Date getDataUltimaAtualizacao() {
		return dataUltimaAtualizacao;
	}
	
	public String getFormatoReferencia() {
		return formatoReferencia;
	}

	public void setFormatoReferencia(String formatoReferencia) {
		this.formatoReferencia = formatoReferencia;
	}

	public Integer getIdObraDigitalizada() {
		return idObraDigitalizada;
	}

	public void setIdObraDigitalizada(Integer idObraDigitalizada) {
		this.idObraDigitalizada = idObraDigitalizada;
	}
	
	public void setRegistroCriacao(RegistroEntrada registroCriacao) {
		this.registroCriacao = registroCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public void setRegistroUltimaAtualizacao(RegistroEntrada registroUltimaAtualizacao) {
		this.registroUltimaAtualizacao = registroUltimaAtualizacao;
	}

	public void setDataUltimaAtualizacao(Date dataUltimaAtualizacao) {
		this.dataUltimaAtualizacao = dataUltimaAtualizacao;
	}
		
	public boolean isImportado() {
		return importado;
	}

	public void setImportado(boolean importado) {
		this.importado = importado;
	}

	public Biblioteca getBibliotecaImportacao() {
		return bibliotecaImportacao;
	}

	public void setBibliotecaImportacao(Biblioteca bibliotecaImportacao) {
		this.bibliotecaImportacao = bibliotecaImportacao;
	}

	public String getCodmerg() {
		return codmerg;
	}

	public void setCodmerg(String codmerg) {
		this.codmerg = codmerg;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public boolean isCatalogado() {
		return catalogado;
	}

	public void setCatalogado(boolean catalogado) {
		this.catalogado = catalogado;
	}

	/**
	 * Método get para área CNPq
	 *
	 * @return
	 */
	public AreaConhecimentoCnpq getAreaConhecimentoCNPQClassificacao1() {
		if(areaConhecimentoCNPQClassificacao1 == null)
			areaConhecimentoCNPQClassificacao1 = new AreaConhecimentoCnpq(-1);
		return areaConhecimentoCNPQClassificacao1;
	}

	/**
	 * Método set para área CNPq
	 *
	 * @return
	 */
	public void setAreaConhecimentoCNPQClassificacao1(AreaConhecimentoCnpq areaConhecimentoCNPQ) {
		if(areaConhecimentoCNPQ == null)
			this.areaConhecimentoCNPQClassificacao1 = new AreaConhecimentoCnpq(-1);
		else
			this.areaConhecimentoCNPQClassificacao1 = areaConhecimentoCNPQ;
	}


	/**
	 * Método get para área CNPq
	 *
	 * @return
	 */
	public AreaConhecimentoCnpq getAreaConhecimentoCNPQClassificacao2() {
		if(areaConhecimentoCNPQClassificacao2 == null)
			areaConhecimentoCNPQClassificacao2 = new AreaConhecimentoCnpq(-1);
		return areaConhecimentoCNPQClassificacao2;
	}

	/**
	 * Método set para área CNPq
	 *
	 * @return
	 */
	public void setAreaConhecimentoCNPQClassificacao2(AreaConhecimentoCnpq areaConhecimentoCNPQ) {
		if(areaConhecimentoCNPQ == null)
			this.areaConhecimentoCNPQClassificacao2 = new AreaConhecimentoCnpq(-1);
		else
			this.areaConhecimentoCNPQClassificacao2 = areaConhecimentoCNPQ;
	}
	
	
	/**
	 * Método get para área CNPq
	 *
	 * @return
	 */
	public AreaConhecimentoCnpq getAreaConhecimentoCNPQClassificacao3() {
		if(areaConhecimentoCNPQClassificacao3 == null)
			areaConhecimentoCNPQClassificacao3 = new AreaConhecimentoCnpq(-1);
		return areaConhecimentoCNPQClassificacao3;
	}

	/**
	 * Método set para área CNPq
	 *
	 * @return
	 */
	public void setAreaConhecimentoCNPQClassificacao3(AreaConhecimentoCnpq areaConhecimentoCNPQ) {
		if(areaConhecimentoCNPQ == null)
			this.areaConhecimentoCNPQClassificacao3 = new AreaConhecimentoCnpq(-1);
		else
			this.areaConhecimentoCNPQClassificacao3 = areaConhecimentoCNPQ;
	}

	

	public String getClassePrincipalClassificacao1() {
		return classePrincipalClassificacao1;
	}

	public void setClassePrincipalClassificacao1(
			String classePrincipalClassificacao1) {
		this.classePrincipalClassificacao1 = classePrincipalClassificacao1;
	}

	public String getClassificacao1() {
		return classificacao1;
	}

	public void setClassificacao1(String classificacao1) {
		this.classificacao1 = classificacao1;
	}

	public String getClassePrincipalClassificacao2() {
		return classePrincipalClassificacao2;
	}

	public void setClassePrincipalClassificacao2(
			String classePrincipalClassificacao2) {
		this.classePrincipalClassificacao2 = classePrincipalClassificacao2;
	}

	public String getClassificacao2() {
		return classificacao2;
	}

	public void setClassificacao2(String classificacao2) {
		this.classificacao2 = classificacao2;
	}

	public String getClassePrincipalClassificacao3() {
		return classePrincipalClassificacao3;
	}

	public void setClassePrincipalClassificacao3(
			String classePrincipalClassificacao3) {
		this.classePrincipalClassificacao3 = classePrincipalClassificacao3;
	}

	public String getClassificacao3() {
		return classificacao3;
	}

	public void setClassificacao3(String classificacao3) {
		this.classificacao3 = classificacao3;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public CacheEntidadesMarc getCache() {
		return cache;
	}

	public void setCache(CacheEntidadesMarc cache) {
		this.cache = cache;
	}
	
	
	public String getFichaCatalografica() {
		return fichaCatalografica;
	}

	public void setFichaCatalografica(String fichaCatalografica) {
		this.fichaCatalografica = fichaCatalografica;
	}

	public String getNumeroChamada() {
		return numeroChamada;
	}

	public void setNumeroChamada(String numeroChamada) {
		this.numeroChamada = numeroChamada;
	}
	
	public Integer getIdDadosDefesa() {
		return idDadosDefesa;
	}

	public void setIdDadosDefesa(Integer idDadosDefesa) {
		this.idDadosDefesa = idDadosDefesa;
	}

	public DadosDefesa getDadosDefesa() {
		return dadosDefesa;
	}

	public void setDadosDefesa(DadosDefesa dadosDefesa) {
		this.dadosDefesa = dadosDefesa;
	}
	
	
	
}