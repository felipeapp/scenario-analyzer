/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p> Classe que representa um T�tulo <i>( Cataloga��o )</i> no sistema. </p>
 * 
 *    <p> <i> Os dados contidos nesta entidade est�o normalizados de acordo com o padr�o MARC21 {@link http://www.loc.gov/marc/}. <br/>
 *    Se um T�tulo for de peri�dicos ele vai possuir uma cole��o de assinaturas de peri�dicos.<br/>
 *    Se um T�tulo <strong>N�o</strong> for de peri�dicos ele vai possuir uma cole��o de exemplares.<br/>
 *    A Indica��o se um T�tulo <i>( Cataloga��o )</i>  � de peri�dico ou n�o vem do <span style="font-style: normal; font-family: courier, monospace;"> Formato do Material</span> que ele possui.<br/>
 *    Al�m das informa��es MARC, dos exemplares ou assinaturas, esta entidade mant�m algumas informa��es em cache para otimizar  <br/>
 *    as consultas dos relat�rios do sistema.  </i> </p>
 * 
 * 
 * @author Jadson
 * @since 18/07/2008
 * @version 1.0 Cria��o da classe
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
	 * Guarda um n�mero que identifica um t�tulo no sistema.
	 */
	@Column(name="numero_do_sistema")
	private int numeroDoSistema;

	
	
	/** Guarda o id da obras digitalizada em formato .pdf para aqueles t�tulos que possuem  */
	@Column(name="id_obra_digitalizada")
	private Integer idObraDigitalizada;
	
	
	/**
	 * <p>Guarda o formato do material do T�tulo (BK- Livro, SE - Peri�dico,
	 * MP - Mapa, VM - Material Visual, etc..) </p>
	 * <p>Esses formatos s�o fixos do padr�o MARC, se o formato for "SE - Peri�dico", o T�tulo conter� um cole��o
	 * de assinaturas, com seus respectivos fasc�culos. Qualquer outro formato, o T�tulo possuir� exemplares.</p>
	 * <p>O formato do material tamb�m influencia a valida��o de alguns campos do T�tulo.</p>
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_formato_material", referencedColumnName = "id_formato_material")
	private FormatoMaterial formatoMaterial;



	/**
	 * O t�tulo catalogr�fico no formato MARC � formado por v�rios registros de controle
	 * 
	 */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY,  mappedBy="tituloCatalografico")
	@IndexColumn(name="posicao", base=0, nullable=false)
	private List<CampoControle> camposControle;


	/**
	 * O t�tulo catalogr�fico no formato MARC � formado por v�rios registros de dados
	 * 
	 */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY,  mappedBy="tituloCatalografico")
	@IndexColumn(name="posicao", base=0, nullable=false)
	private List<CampoDados> camposDados;



	/**
	 * Guarda os exemplares desse t�tulo.
	 */
	@OneToMany(cascade = {}, fetch = FetchType.LAZY, mappedBy = "tituloCatalografico")
	private List<Exemplar> exemplares;


	/**
	 * Ou um T�tulo catalogr�fico possui um conjunto de exemplares ou um conjunto de assinaturas de fasc�culos.
	 * Nunca os dois ao mesmo tempo.
	 */
	@OneToMany(cascade = {}, fetch = FetchType.LAZY, mappedBy = "tituloCatalografico")
	private List<Assinatura> assinaturas;
	
		
	/**
	 * Se o T�tulo for catalogado por meio de importa��o, vai ser salvo com o valor desta vari�vel = "true".<br/>
	 * Esta informa��es serve para os relat�rios do setor de coopera��o t�cnica. <br/>
	 * <strong>Observa��o.:</strong> A data da importa��o vai ser a data da cria��o do t�tulo
	 */
	@Column(name="importado", nullable=false)
	private boolean importado = false;
	
	
	/**
	 * Guarda a biblioteca de quem importou o t�tulo. Como um T�tulo n�o est� em uma
	 * biblioteca espec�fica � preciso guardar qual a biblioteca que fez a sua importa��o.
	 * */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_biblioteca_importacao", referencedColumnName="id_biblioteca")
	private Biblioteca bibliotecaImportacao;
	

	/**
	 *   <p>Informa��o usada para saber se um T�tulo foi catalogado por completo ou n�o.</p>
	 *   <p>Se um t�tulo n�o foi catalogado por completo - o bibliotec�rio n�o informou todos os campos obrigat�rios -, o usu�rio
	 *   n�o vai poder incluir materiais.</p>
	 * 
	 *   </p>Se um arquivo de importa��o tiver v�rios T�tulos, esses tamb�m ser�o salvos no
	 *   sistema como n�o catalogados, para obrigar o bibliotec�rio a revisar as suas cataloga��es antes
	 *   de incluir materiais.</p>
	 * 
	 */
	@Column(name="catalogado", nullable=false)
	private boolean catalogado = false;
	
	
	/**
	 *   Se T�tulos n�o possu�rem materiais ativos, eles poder�o ser removidos do sistema.<br/>
	 *  <i> ( O cache � apagado para n�o aparecer mais nas pesquisas, e � atribu�do a esta vari�vel o valor "false" ) </i>
	 */
	private boolean ativo = true;
	
	
	
	////////////////////////////  DADOS EM CACHE PARA SER USADOS NOS RELAT�RIOS  ///////////////////////////////////////
	
	/** Cont�m o valor da classifica��o 1 desse T�tulo */
	@Column(name="classificacao_1")
	private String classificacao1;
	
	/** Cont�m a valor da classe principal da classifica�ao 1 dese T�tulo */
	@Column(name="classe_principal_classificacao_1" )
	private String classePrincipalClassificacao1;
	
	/** Cont�m o valor da classifica��o 2 desse T�tulo */
	@Column(name="classificacao_2")
	private String classificacao2;
	
	/** Cont�m a valor da classe principal da classifica�ao 2 dese T�tulo */
	@Column(name="classe_principal_classificacao_2" )
	private String classePrincipalClassificacao2;
	
	/** Cont�m o valor da classifica��o 3 desse T�tulo */
	@Column(name="classificacao_3")
	private String classificacao3;
	
	/** Cont�m a valor da classe principal da classifica�ao 3 dese T�tulo */
	@Column(name="classe_principal_classificacao_3" )
	private String classePrincipalClassificacao3;
	
	
	
	/**
	 * Cont�m a classe CNPQ (apenas grandes �reas) correspondente � classifica��o 1 para otimizar os relat�rios.
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_area_conhecimento_cnpq_classificacao_1", referencedColumnName="id_area_conhecimento_cnpq")
	private AreaConhecimentoCnpq areaConhecimentoCNPQClassificacao1;
	
	
	/** Cont�m a classe CNPQ (apenas grandes �reas) correspondente � classifica��o 2 para otimizar os relat�rios. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_area_conhecimento_cnpq_classificacao_2", referencedColumnName="id_area_conhecimento_cnpq")
	private AreaConhecimentoCnpq areaConhecimentoCNPQClassificacao2;
	
	
	/**  Cont�m a classe CNPQ (apenas grandes �reas) correspondente � classifica��o 3 para otimizar os relat�rios. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_area_conhecimento_cnpq_classificacao_3", referencedColumnName="id_area_conhecimento_cnpq")
	private AreaConhecimentoCnpq areaConhecimentoCNPQClassificacao3;
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	

	/**  Para t�tulos que foram migrados do sistema antigo. */
	private String codmerg;
	
	
	/**
	 * Se o t�tulo foi catalogado a partir de uma monografia ou tese, indica a
	 * defesa fonte da cataloga��o. Se for <tt>null</tt>, o t�tulo n�o foi criado a partir de
	 * uma defesa. */
	@Column(name="id_dados_defesa")
	private Integer idDadosDefesa = null;
	
	
	//////////////////////////// INFORMA��ES DE AUDITORIA  ///////////////////////////////////////


	/**
	 * informa��es de quem criou
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
	 * registro entrada  do usu�rio que realizou a �ltima atualiza��o
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroUltimaAtualizacao;

	/**
	 * data da �ltima atualiza��o
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_ultima_atualizacao")
	@AtualizadoEm
	private Date dataUltimaAtualizacao;


	//////////////////////////////////////////////////////////////////////////////////////////

	
	
	/**
	 * Guarda de forma tempor�ria as informa��es do t�tulo no formato de refer�ncia espec�fico.
	 *
	 */
	@Transient
	private String formatoReferencia;

	
	
	/**
	 * Guarda de forma tempor�ria as informa��es da ficha catalogr�fica do T�tulo
	 *
	 */
	@Transient
	private String fichaCatalografica;
	
	/**
	 * Guarda de forma tempor�ria as informa��es do n�mero de chamada
	 *
	 */
	@Transient
	private String numeroChamada;
	
	
	
	/** Atributo do ALEPH, campo utilizado na migra��o */
	@Transient
	private int sequenceNumber;
	
	/** Informa��es catalogr�ficas do t�tulo. */
	@Transient
	private CacheEntidadesMarc cache;
	
	
	/**
	 * Guarda temporariamente a defesa associada a essa cataloga��o, j� que � guardado somente o id.
	 */
	@Transient
	private DadosDefesa dadosDefesa;
	
	
	/**
	 * Construtor padr�o
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
	 * Retorna a quantidade de campos que o T�tulo possui, seja de dados ou de controle.
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
	 *  Retorna a quantidade de exemplares do t�tulo
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
	 * Retorna a quantidade de fasc�culos do t�tulo
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
	 * <p>Retorna a quantidade total de materiais que o T�tulo possui no acervo.</p>
	 * 
	 * <p>OBSERVA��O 1: Precisa estar com a sess�o do hibernate aberta para funcionar porque usa
	 * relacionamentos lazy.</p>
	 * <p>OBSERVA��O 2: Uma maneira mais r�pida de obter a quantidade de materiais de um T�tulo �
	 * consultar no seu respectivo cache.</p>
	 * 
	 * @return A quantidade de materiais ativo <i>(n�o baixados e n�o removidos)</i> do T�tulo.
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
	 *   Verifica se o t�tulo possui o campo de controle cuja tag foi passada
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
	 *   Verifica se o t�tulo possui o campo de dados cuja tag foi passada
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
	 * Dois t�tulos s�o iguais se tiverem o mesmo n�mero do sistema.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(numeroDoSistema);
	}
	
	/**
	 * Dois t�tulos s�o iguais se tiverem o mesmo n�mero do sistema.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "numeroDoSistema");
	}

	
	/**
	 * <p>Zera os dados correspondente a classifica��o 1 que s�o mantidos no T�tulo para efeito de cache das consultas dos relat�rios</p>
	 */
	public void zeraDadosClassificacao1(){
		this.setClassificacao1(null); 
		this.setClassePrincipalClassificacao1(null);
		this.setAreaConhecimentoCNPQClassificacao1(null);
	}
	
	/**
	 * <p>Zera os dados correspondente a classifica��o 1 que s�o mantidos no T�tulo para efeito de cache das consultas dos relat�rios</p>
	 */
	public void zeraDadosClassificacao2(){
		this.setClassificacao2(null); 
		this.setClassePrincipalClassificacao2(null);
		this.setAreaConhecimentoCNPQClassificacao2(null);
	}
	
	/**
	 * <p>Zera os dados correspondente a classifica��o 1 que s�o mantidos no T�tulo para efeito de cache das consultas dos relat�rios</p>
	 */
	public void zeraDadosClassificacao3(){
		this.setClassificacao3(null); 
		this.setClassePrincipalClassificacao3(null);
		this.setAreaConhecimentoCNPQClassificacao3(null);
	}
	
	
	/**
	 * <p>Anula as �reas de CNPq n�o informadas para n�o savar com o id = -1</p>
	 *
	 * <p><strong>Deve sempre ser chamado sempre antes de salvar ou atualizar o t�tulo </strong> </p>
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
	 * Imprime todos as informa��es T�tulo em Formato MARC.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder titulo = new StringBuilder();
		
		if(this.getFormatoMaterial() != null) // t�tulos importados podem n�o ter formato do material ainda.
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
	 * Retorna os campos de dados que n�o s�o reservado no sistema, os campos cujos dados PODEM ser
	 * alterados pelos bibliotec�rios.
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
	 *   Retorna os campos de dados de uso reservado no sistema, os campos cujos dados N�O podem ser
	 *   alterados pelos bibliotec�rios
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
	 * M�todo get para �rea CNPq
	 *
	 * @return
	 */
	public AreaConhecimentoCnpq getAreaConhecimentoCNPQClassificacao1() {
		if(areaConhecimentoCNPQClassificacao1 == null)
			areaConhecimentoCNPQClassificacao1 = new AreaConhecimentoCnpq(-1);
		return areaConhecimentoCNPQClassificacao1;
	}

	/**
	 * M�todo set para �rea CNPq
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
	 * M�todo get para �rea CNPq
	 *
	 * @return
	 */
	public AreaConhecimentoCnpq getAreaConhecimentoCNPQClassificacao2() {
		if(areaConhecimentoCNPQClassificacao2 == null)
			areaConhecimentoCNPQClassificacao2 = new AreaConhecimentoCnpq(-1);
		return areaConhecimentoCNPQClassificacao2;
	}

	/**
	 * M�todo set para �rea CNPq
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
	 * M�todo get para �rea CNPq
	 *
	 * @return
	 */
	public AreaConhecimentoCnpq getAreaConhecimentoCNPQClassificacao3() {
		if(areaConhecimentoCNPQClassificacao3 == null)
			areaConhecimentoCNPQClassificacao3 = new AreaConhecimentoCnpq(-1);
		return areaConhecimentoCNPQClassificacao3;
	}

	/**
	 * M�todo set para �rea CNPq
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