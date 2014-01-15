/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 18/07/2008
 *
 */

package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * 
 * 
 *     <p>A etiqueta é quem diz o que o campo significa. exemplo: valor=020 descricao="ISBN".</p>
 *     <p>'repetível' informa se o registro pode aparecer repetido ou não dentro de um mesmo título
 * catalográfico.</p>
 *     <p>O que identifica uma etiqueta para o negócio é o valor dela e a descrição. Para exemplo,
 * verifique a definição da etiqueta 006. Podem existir duas ou mais etiquetas com o mesmo valor,
 * mas para materiais diferentes e com significados totalmente diferentes para o que vem do campo
 * dado de um registro Catalográfico.</p>
 * 
 * <p>Exemplo de etiqueta para o campo líder: <br/>
 * 
 * 		codigo = LDR, descricao LÍDER, tipoCampo = CampoControle.INDETIFICADOR_CAMPO
 * 
 * </p>
 * 
 * @author Jadson
 * @since 18/07/2008
 * @version 1.0 Criação da classe
 * 
 */
@Entity
@Table(name = "etiqueta", schema = "biblioteca")
public class Etiqueta implements Validatable {

	
	
	/* *************************************************************************************************
	 *  CONSTANTES QUE IDENTIFICAM AS ETIQUETAS DE CONTROLE PARA A BASE BIBLIOGRÁFICA E DE AUTORIDADES  *
	 ***************************************************************************************************/
	
	/** Etiqueta de identificação do campo líder bibliográfica*/
	public static final Etiqueta CAMPO_LIDER_BIBLIOGRAFICO = new Etiqueta("LDR", false, TipoCatalogacao.BIBLIOGRAFICA);
	/** Etiqueta de identificação do campo 001 bibliográfica */
	public static final Etiqueta CAMPO_001_BIBLIOGRAFICO = new Etiqueta("001", false, TipoCatalogacao.BIBLIOGRAFICA);
	/** Etiqueta de identificação do campo 003 bibliográfica */
	public static final Etiqueta CAMPO_003_BIBLIOGRAFICO = new Etiqueta("003", false, TipoCatalogacao.BIBLIOGRAFICA);
	/** Etiqueta de identificação do campo 005 bibliográfica */
	public static final Etiqueta CAMPO_005_BIBLIOGRAFICO = new Etiqueta("005", false, TipoCatalogacao.BIBLIOGRAFICA);
	/** Etiqueta de identificação do campo 006 bibliográfica */
	public static final Etiqueta CAMPO_006_BIBLIOGRAFICO = new Etiqueta("006", true, TipoCatalogacao.BIBLIOGRAFICA);
	/** Etiqueta de identificação do campo 007 bibliográfica */
	public static final Etiqueta CAMPO_007_BIBLIOGRAFICO = new Etiqueta("007", true, TipoCatalogacao.BIBLIOGRAFICA);
	/** Etiqueta de identificação do campo 008 bibliográfica */
	public static final Etiqueta CAMPO_008_BIBLIOGRAFICO = new Etiqueta("008", false, TipoCatalogacao.BIBLIOGRAFICA);
	/** Etiqueta de identificação do campo líder autoridades */
	public static final Etiqueta CAMPO_LIDER_AUTORIDADE = new Etiqueta("LDR", false, TipoCatalogacao.AUTORIDADE);
	/** Etiqueta de identificação do campo 001 autoridades */
	public static final Etiqueta CAMPO_001_AUTORIDADE = new Etiqueta("001", false, TipoCatalogacao.AUTORIDADE);
	/** Etiqueta de identificação do campo 003 autoridades */
	public static final Etiqueta CAMPO_003_AUTORIDADE = new Etiqueta("003", false, TipoCatalogacao.AUTORIDADE);
	/** Etiqueta de identificação do campo 005 autoridades */
	public static final Etiqueta CAMPO_005_AUTORIDADE = new Etiqueta("005", false, TipoCatalogacao.AUTORIDADE);
	/** Etiqueta de identificação do campo 008 autoridades */
	public static final Etiqueta CAMPO_008_AUTORIDADE = new Etiqueta("008", false, TipoCatalogacao.AUTORIDADE);
	
	
	/* ********************************************************************************
	 *  CONSTANTES QUE IDENTIFICAM AS ETIQUETAS DE DADOS PARA A BASE BIBLIOGRÁFICA    *
	 **********************************************************************************/
	
	/** ETIQUETA */
	public static final Etiqueta CDU = new Etiqueta("080", TipoCatalogacao.BIBLIOGRAFICA);

	/** ETIQUETA */
	public static final Etiqueta BLACK = new Etiqueta("084", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta CDD = new Etiqueta("082", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta ISBN = new Etiqueta("020", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta ISSN = new Etiqueta("022", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta FONTE_CATALOGACAO_BIBLIOGRAFICA = new Etiqueta("040", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta NUMERO_CHAMADA = new Etiqueta("090", TipoCatalogacao.BIBLIOGRAFICA); // campo local fixo no sistema ( não pode ser alterado pole usuário ).
	
	/** ETIQUETA */
	public static final Etiqueta AUTOR = new Etiqueta("100", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta AUTOR_SECUNDARIO = new Etiqueta("700", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta AUTOR_COOPORATIVO = new Etiqueta("110", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta AUTOR_COOPORATIVO_SECUNDARIO = new Etiqueta("710", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta AUTOR_EVENTO = new Etiqueta("111", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta AUTOR_EVENTO_SECUNDARIO = new Etiqueta("711", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta TITULO_UNIFORME_ENTRADA_PRINCIPAL = new Etiqueta("130", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta TITULO_UNIFORME = new Etiqueta("240", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta TITULO = new Etiqueta("245", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta TITULO_ABREVIADO = new Etiqueta("210", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta TITULO_COLETANEA = new Etiqueta("243", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta TITULO_VARIAVEL = new Etiqueta("246", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta TITULO_UNIFORME_ENTRADA_ADICIONAL = new Etiqueta("730", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta TITULO_RELACIONADO_ANALITICO_NAO_CONTROLADO = new Etiqueta("740", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta TITULO_ANTERIOR = new Etiqueta("780", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta TITULO_POSTERIOR = new Etiqueta("785", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta PERIODICIDADE = new Etiqueta("310", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta ASSUNTO_PESSOAL = new Etiqueta("600", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta ASSUNTO_ENTIDADE = new Etiqueta("610", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta ASSUNTO_EVENTOS = new Etiqueta("611", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta ASSUNTO_TITULO_UNIFORME = new Etiqueta("630", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta ASSUNTO_CRONOLOGICO = new Etiqueta("648", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta ASSUNTO = new Etiqueta("650", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta ASSUNTO_GEOGRAFICO = new Etiqueta("651", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta ASSUNTO_SEM_CONTROLE = new Etiqueta("653", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta ASSUNTO_FACETADO = new Etiqueta("654", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta ASSUNTO_GENERO_FORMA = new Etiqueta("655", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta ASSUNTO_OCUPACAO = new Etiqueta("656", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta ASSUNTO_FUNCAO = new Etiqueta("657", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta ASSUNTO_CURRICULO_OBJETIVO = new Etiqueta("658", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta ASSUNTO_LUGAR_HIERARQUICO= new Etiqueta("662", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta EDICAO = new Etiqueta("250", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta LOCAL_EDITORA_ANO_PUBLICACAO = new Etiqueta("260", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta SERIE_OBSOLETA = new Etiqueta("440", TipoCatalogacao.BIBLIOGRAFICA); // DESATIVAR pois não é mais usado desde 2008, apesar deles continuarem usando, a série agora é o campo 490.
	
	/** ETIQUETA */
	public static final Etiqueta SERIE = new Etiqueta("490", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta RESUMO = new Etiqueta("520", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta NOTA_GERAL = new Etiqueta("500", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta NOTA_COM = new Etiqueta("501", TipoCatalogacao.BIBLIOGRAFICA); // "With Note" -> Notas que iniciam com "com"
	
	/** ETIQUETA */
	public static final Etiqueta NOTA_DISSETACAO_TESE = new Etiqueta("502", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta NOTA_BIBLIOGRAFICA = new Etiqueta("504", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta NOTA_DE_CONTEUDO = new Etiqueta("505", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta NOTA_FORMA_FISICA = new Etiqueta("530", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta NOTA_FAC_SIMILE = new Etiqueta("534", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta NOTA_LOCAL = new Etiqueta("590", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta DESCRICAO_FISICA = new Etiqueta("300", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta AREA = new Etiqueta("940", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta TITULACAO = new Etiqueta("941", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta MEMBRO_BANCA = new Etiqueta("942", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta DATA_APRESENTACAO = new Etiqueta("943", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta LIGACAO = new Etiqueta("773", TipoCatalogacao.BIBLIOGRAFICA);
	
	/** ETIQUETA */
	public static final Etiqueta LOCALIZACAO_E_ACESSO_ELETRONICO = new Etiqueta("856", TipoCatalogacao.BIBLIOGRAFICA);
	
	
	
	
	/** Etiquetas usadas na cooperação com a FGV  */
	public static final Etiqueta CODIGO_DA_BIBLIOTECA = new Etiqueta("997", TipoCatalogacao.BIBLIOGRAFICA);
	/** Etiquetas usadas na cooperação com a FGV  */
	public static final Etiqueta CODIGO_DAS_BIBLIOTECAS_COOPERANTES = new Etiqueta("998", TipoCatalogacao.BIBLIOGRAFICA);
	/** Etiquetas usadas na cooperação com a FGV  */
	public static final Etiqueta INFORMACOES_DE_MOVIMENTO = new Etiqueta("999", TipoCatalogacao.BIBLIOGRAFICA);
	/** Etiquetas usadas na cooperação com a FGV  */
	public static final Etiqueta CODIGO_DA_BIBLIOTECA_AUTORIDADES = new Etiqueta("997", TipoCatalogacao.AUTORIDADE);
	/** Etiquetas usadas na cooperação com a FGV  */
	public static final Etiqueta CODIGO_DAS_BIBLIOTECAS_COOPERANTES_AUTORIDADES = new Etiqueta("998", TipoCatalogacao.AUTORIDADE);
	/** Etiquetas usadas na cooperação com a FGV  */
	public static final Etiqueta INFORMACOES_DE_MOVIMENTO_AUTORIDADES = new Etiqueta("999", TipoCatalogacao.AUTORIDADE);
	
	
	/* ********************************************************************************
	 *  CONSTANTES QUE IDENTIFICAM AS ETIQUETAS DE DADOS PARA A BASE AUTORIDADES      *
	 **********************************************************************************/
	
	/** ETIQUETA DE AUTORIDADE */
	public static final Etiqueta NOME_PESSOAL = new Etiqueta("100", TipoCatalogacao.AUTORIDADE);
	
	/** ETIQUETA DE AUTORIDADE */
	public static final Etiqueta NOME_CORPORATIVO = new Etiqueta("110", TipoCatalogacao.AUTORIDADE);
	
	/** ETIQUETA DE AUTORIDADE */
	public static final Etiqueta NOME_EVENTO = new Etiqueta("111", TipoCatalogacao.AUTORIDADE);
	
	/** ETIQUETA DE AUTORIDADE */
	public static final Etiqueta NOME_PESSOAL_REMISSIVO = new Etiqueta("400", TipoCatalogacao.AUTORIDADE);
	
	/** ETIQUETA DE AUTORIDADE */
	public static final Etiqueta NOME_CORPORATIVO_REMISSIVO = new Etiqueta("410", TipoCatalogacao.AUTORIDADE);
	
	/** ETIQUETA DE AUTORIDADE */
	public static final Etiqueta NOME_EVENTO_REMISSIVO = new Etiqueta("411", TipoCatalogacao.AUTORIDADE);
	
	/** ETIQUETA DE AUTORIDADE */
	public static final Etiqueta CABECALHO_TOPICOS = new Etiqueta("150", TipoCatalogacao.AUTORIDADE);
	
	/** ETIQUETA DE AUTORIDADE */
	public static final Etiqueta CABECALHO_NOME_GEOGRAFICO = new Etiqueta("151", TipoCatalogacao.AUTORIDADE);
	
	/** ETIQUETA DE AUTORIDADE */
	public static final Etiqueta CABECALHO_GERAL_SUBDIVISAO = new Etiqueta("180", TipoCatalogacao.AUTORIDADE);
	
	/** ETIQUETA DE AUTORIDADE */
	public static final Etiqueta CABECALHO_TOPICOS_REMISSIVO = new Etiqueta("450", TipoCatalogacao.AUTORIDADE);
	
	/** ETIQUETA DE AUTORIDADE */
	public static final Etiqueta CABECALHO_NOME_GEOGRAFICO_REMISSIVO = new Etiqueta("451", TipoCatalogacao.AUTORIDADE);
	
	/** ETIQUETA DE AUTORIDADE */
	public static final Etiqueta CABECALHO_GERAL_SUBDIVISAO_REMISSIVO = new Etiqueta("480", TipoCatalogacao.AUTORIDADE);
	
	/** ETIQUETA DE AUTORIDADE */
	public static final Etiqueta FONTE_CATALOGACAO_AUTORIDADES = new Etiqueta("040", TipoCatalogacao.AUTORIDADE);
	


	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column(name="id_etiqueta")
	private int id;

	/** O valor da etiqueta = 001, 006, LDR, 800, 450 , etc..*/
	@Column(name = "tag", nullable=false)
	private String tag;

	
	/**
	 * Guarda o tipo da etiqueta. Etiquetas significam diferentes coisa dependendo do tipo.
	 * 
	 * Etiqueta 100 do tipo BIBLIOGRAFICO é totalmente diferente da etiqueta 100 para AUTORIDADES.
	 */
	@Column(name = "tipo", nullable=false)
	private short tipo = TipoCatalogacao.BIBLIOGRAFICA;
	
	
	
	/**
	 * A descrição da etiqueta. exemplo: valor= 001 , descricao = Numero Controle */
	@Column(name = "descricao", nullable=false)
	private String descricao;

	/**
	 * Indica se os registros catalográfico desse etiqueta podem ser repetidos ou não no título
	 */
	@Column(name = "repetivel", nullable=false)
	private boolean repetivel;

	/**
	 * Diz se essa etiqueta é para um registro de dados ou de controle.
	 */
	@Column(name = "tipo_campo", nullable=false)
	private char tipoCampo = CampoDados.IDENTIFICADOR_CAMPO;
	
	
	/**Campo para verificar se o usuário pode alterar os dados da etiqueta
	 * Existem algumas etiquetas locais que são usadas para a cooperação com a FGV então o usuário
	 * não vai poder alterar seus dados ou removê-la do sistema.*/
	@Column(name = "alteravel", nullable=false)
	private boolean alteravel = true;
	
	
	/**
	 * Usado no cadastro de etiqueta(campos) locais. O usuário vai poder apagar uma etiqueta local.
	 * Nesse caso na catalogação ela não será mais mostrada para o usuário.
	 * E na edição dos títulos ou autoridades o sistema deve informar que aquela etiqueta foi removida
	 * por algum usuário e não permitirá incluir alguma entidade com etiquetas desativas.
	 */
	@Column(name = "ativa", nullable=false)
	private boolean ativa = true;
	
	/** Informação da etiqueta para auxílio ao usuário  */
	private String info;
	
	/** A denominação do primeiro indicador da etiqueta */
	@Column(name="descricao_indicador_1")
	private String descricaoIndicador1;
	
	/** Informação do primeiro indicador da etiqueta */
	@Column(name="info_indicador_1")
	private String infoIndicador1;

	/** A denominação do segundo indicador da etiqueta */
	@Column(name="descricao_indicador_2")
	private String descricaoIndicador2;
	
	/** Informação do segundo indicador da etiqueta */
	@Column(name="info_indicador_2")
	private String infoIndicador2;
	
	
	////////////////////Se for uma etiqueta de um campo de controle /////////////////////

	// Se for != 007   os descritores dependem do tipo de material
	@OneToMany(mappedBy="etiqueta", fetch= FetchType.LAZY)
	private List<FormatoMaterialEtiqueta> formatosMaterialEtiqueta;


	// Se for = 007   os descritores dependem do primeiro caráter dos dados
	@OneToMany(mappedBy="etiqueta", fetch= FetchType.LAZY)
	private List<CategoriaMaterial> categoriasMaterial;

	/////////////////// Se for uma etiqueta de um campo de dados //////////////////////

	@OneToMany(mappedBy="etiqueta", cascade = { CascadeType.ALL }, fetch= FetchType.LAZY)
	private Set<ValorIndicador> valoresIndicador;


	@OneToMany(mappedBy="etiqueta", cascade = { CascadeType.ALL }, fetch= FetchType.LAZY)
	private Set<DescritorSubCampo> descritorSubCampo;
	
	
	// Atributo para usar nos casos de uso, ordenar etc. Na hora de salvar cópia para o set
	@Transient
	private List<ValorIndicador> valoresIndicadorList;
	
	// Atributo para usar nos casos de uso, ordenar etc. Na hora de salvar cópia para o set
	@Transient
	private List<DescritorSubCampo> descritorSubCampoList;
	
	
	/////////////////////////// Auditoria ////////////////////////////////
	
	/**
	 * Registro entrada do usuário que cadastrou
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/**
	 * Data de cadastro
	 */
	@CriadoEm
	@Column(name="data_cadastro")
	private Date dataCadastro;

	/**
	 * Registro entrada do usuário que realizou a última atualização
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/**
	 * Data da última atualização
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;

	/////////////////////////////////////////////////////////////////
	
	
	/** O grupo MARC no qual essa etiqueta está localizada. Utilizado para organizar os dados na tela para o usuário */
	@Transient
	private GrupoEtiqueta grupo;
	

	/**
	 *   Construtor <i>default</i>
	 */
	public Etiqueta(){

	}

	/**
	 * Cria uma etiqueta persistente
	 * 
	 * @param id
	 */
	public Etiqueta(int id){
		this.id =  id;
	}

	
	/**
	 * Construtor usando quando se cria uma Etiqueta com o mínimo de informações possíveis para identificá-la.
	 * @param tag
	 */
	public Etiqueta(String tag, short tipo){
		this.tag = tag;
		this.tipo = tipo;
	}
	
	public Etiqueta(String tag, short tipo, String descricao){
		this(tag, tipo);
		this.descricao = descricao;
	}
	
	public Etiqueta(int id, String tag, short tipo){
		this(tag, tipo);
		this.id = id;
	}

	public Etiqueta(String tag, boolean repetivel, short tipo){
		this(tag, tipo);
		this.repetivel = repetivel;
	}
	
	/**
	 * Criando um etiqueta com todas as suas informações, inclusive para validação.
	 * @param tag
	 * @param repetivel
	 * @param tipo
	 */
	public Etiqueta(int id, String tag, short tipo, char tipoCampo, String descricao, boolean repetivel, boolean ativa, String info, String descricaoIndicador1
			, String infoIndicador1, String  descricaoIndicador2, String infoIndicador2, List<FormatoMaterialEtiqueta> formatosMaterialEtiqueta
			, List<CategoriaMaterial> categoriasMaterial, Set<ValorIndicador> valoresIndicador, Set<DescritorSubCampo> descritorSubCampo){
		this(id, tag, tipo);
		
		this.tipoCampo = tipoCampo;
		this.repetivel = repetivel;
		this.descricao = descricao;
		this.ativa = ativa;
		this.info = info;
		this.descricaoIndicador1 =descricaoIndicador1 ;
		this.infoIndicador1 = infoIndicador1;
		this.descricaoIndicador2 = descricaoIndicador2;
		this.infoIndicador2 = infoIndicador2;
		
		this.formatosMaterialEtiqueta = new ArrayList<FormatoMaterialEtiqueta>();
		this.formatosMaterialEtiqueta.addAll( formatosMaterialEtiqueta);
		
		this.categoriasMaterial = new ArrayList<CategoriaMaterial>();
		this.categoriasMaterial.addAll(categoriasMaterial);
		
		this.valoresIndicador = new HashSet<ValorIndicador>();
		this.valoresIndicador.addAll(valoresIndicador);
		
		this.descritorSubCampo = new HashSet<DescritorSubCampo>();
		this.descritorSubCampo.addAll(descritorSubCampo);
		
		
	}
	
	
	
	
	/**
	 * Verifica se a etiqueta é de controle ou de dados
	 *
	 * @return
	 * @throws NegocioException
	 */
	public boolean isEtiquetaControle(){
		if(tipoCampo == CampoControle.IDENTIFICADOR_CAMPO){
			return true;
		}else{
			return false;
		}
	}
	
	
	/**
	 *     Verifica se a etiqueta é de controle pela tag da etiqueta. Para casos no qual se possui apenas
	 * a tag da etiqueta.
	 *
	 * @return
	 * @throws NegocioException
	 */
	public boolean isEtiquetaControleComparandoPelaTag(){
		if(tag != null && (tag.equalsIgnoreCase("LDR") || tag.startsWith("00")))
			return true;

		return false;
		
	}
	
	
	
	
	/**
	 * Verifica se a etiqueta é de controle ou de dados
	 *
	 * @return
	 * @throws NegocioException
	 */
	public boolean isEtiquetaDados(){
		if(tipoCampo == CampoDados.IDENTIFICADOR_CAMPO){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 
	 * Retorna a descrição do tipo da etiqueta
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/
	 * @return
	 */
	public String getDescricaoTipoEtiqueta(){
		switch ( tipo ) {
			case TipoCatalogacao.BIBLIOGRAFICA:
				return "BIOGRÁFICA";
			case TipoCatalogacao.AUTORIDADE:
				return "AUTORIDADE";
			/*case Etiqueta.EXPLORACAO:
				return "EXPLORAÇÃO";
			case Etiqueta.COMUNIDADE:
				return "COMUNIDADE";
			case Etiqueta.CLASSIFICACAO:
				return "CLASSIFICAÇÃO";	*/
			default:
				return "NÃO ESPECIFICADA";
		}
	}
	
	/**
	 * Adiciona um novo descritor de subcampo à etiqueta.
	 *
	 * @param d
	 */
	public void addDescritorSubCampoList(DescritorSubCampo d){
		if(descritorSubCampoList == null)
			descritorSubCampoList = new ArrayList<DescritorSubCampo>();
		descritorSubCampoList.add(d);
	}
	
	
	/**
	 * Adiciona um novo valor ao indicador do campo local.
	 *
	 * @param d
	 */
	public void addValorIndicadorList(ValorIndicador v){
		if(valoresIndicadorList == null)
			valoresIndicadorList = new ArrayList<ValorIndicador>();
		valoresIndicadorList.add(v);
	}

	/**
	 * Duas etiquetas são iguais se tiverem a mesma TAG e for do mesmo TIPO
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(this.tag, this.tipo);
	}
	
	/**
	 * Duas etiquetas são iguais se tiverem a mesma TAG e for do mesmo TIPO
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "tag", "tipo");
	}


	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Id: "+this.id +" tag: "+this.tag+" tipo: "+this.tipo;
	}

	
	/**
	 * <p>Teste se uma etiqueta é uma etiqueta local. As etiquetas locais são:</p>
	 *
	 * <p>Definidas no padrão MARC:<br/>
	 *    09X -> números de chamada Locais<br/>
	 *    59X -> Notas locais<br/>
	 *    69X -> Campos de Assunto Locais<br/>
	 * </p>
	 *
	 * <p>Obs. as etiquetas 9XX são consideradas locais também no sistema, pois o padrão MARC não menciona
	 * nada sobre elas e a FGV usa as 997, 998 e 999 para intercâmbio de informações.</p>
	 *
	 *
	 * @return
	 */
	public boolean isEquetaLocal(){
		if ( ( tag.startsWith("09") ||tag.startsWith("59") || tag.startsWith("69") || tag.startsWith("9") ) && ! isEtiquetaControle() )
			return true;
		else
			return false;
	}
	
	
	/**
	 * <p>Testa se uma etiqueta é uma etiqueta de uso reservado no sistema, por exemplo as etiquetas usadas na
	 * cooperação com a FGV, os bibliotecários não podem alterar o conteúdo dessas etiquetas. </p>
	 *
	 * @return
	 */
	public boolean isEquetaDeUsoReservado(){
		if (this.equals(Etiqueta.CODIGO_DA_BIBLIOTECA)
				||  this.equals(Etiqueta.CODIGO_DAS_BIBLIOTECAS_COOPERANTES)
				||  this.equals(Etiqueta.INFORMACOES_DE_MOVIMENTO)
				||  this.equals(Etiqueta.CODIGO_DA_BIBLIOTECA_AUTORIDADES)
				||  this.equals(Etiqueta.CODIGO_DAS_BIBLIOTECAS_COOPERANTES_AUTORIDADES)
				||  this.equals(Etiqueta.INFORMACOES_DE_MOVIMENTO_AUTORIDADES) )
			return true;
		else
			return false;
	}
	
	
	/**
	 * Método que contém as validação de uma etiqueta. Usado no cadastro de etiquetas(campos) locais.
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {

		ListaMensagens mensagens = new ListaMensagens();

		if (tipoCampo != CampoControle.IDENTIFICADOR_CAMPO && tipoCampo != CampoDados.IDENTIFICADOR_CAMPO){
			mensagens.addErro("Valor do Tipo de Registro Catalográfico inválido");
		}

//		// Validações para etiquetas locais, pois somente etiqueta locais podem ser cadastradas.
//		if (! isEquetaLocal())
//			mensagens.addErro("Só é possível criar ou alterar campos locais. Campos locais são: 09X -> números de chamada Locais, 59X -> Notas locais, "
//						+" 69X -> Camps de Assunto Locais<br/> e os campos 9XX.");
		
		if (tag.length() != 3)
			mensagens.addErro("A tag do campo deve possuir exatamente três caracteres.");
		
		if (tag.startsWith("00"))
			mensagens.addErro("Não podem ser criados campos de controle. Campos de controle são os campos que começam com '00' e o campo Líder. Exemplo: 001, 008, 009, entre outros.");
		
		if (tag.matches("(\\d*\\D+\\d*)+"))
			mensagens.addErro("A tag de um campo MARC não pode conter letras nem espaços em branco.");
		
		if ( br.ufrn.arq.util.StringUtils.isEmpty(descricao))
			mensagens.addErro("A descrição de um campo MARC deve ser informada.");
		
		if ( br.ufrn.arq.util.StringUtils.isEmpty(descricaoIndicador1))
			mensagens.addErro("A descrição do primeiro indicador deve ser informada.");
		
		if ( br.ufrn.arq.util.StringUtils.isEmpty(descricaoIndicador2))
			mensagens.addErro("A descrição do segundo indicador deve ser informada.");
		
		return mensagens;
	}

	 
	
	/**
	 * @return retorna uma lista de valores dos indicadores com os dados que estavam no SET.
	 */
	public List<ValorIndicador> getValoresIndicadorList() {
		
		if(valoresIndicadorList == null)
			valoresIndicadorList = new ArrayList<ValorIndicador>();
		
		return valoresIndicadorList;
	}

	/**
	 *
	 * @return  retorna uma lista de valores dos descritores com os dados que estavam no SET.
	 */
	public List<DescritorSubCampo> getDescritorSubCampoList() {
		
		if(descritorSubCampoList == null)
			descritorSubCampoList = new ArrayList<DescritorSubCampo>();
		
		return descritorSubCampoList;
	}
	
	/**
	 *   Inicia a lista transiente com os valores permanentes do SET
	 */
	public void iniciaListaValoresIndicador(){
		if(valoresIndicadorList == null && getValoresIndicador() != null)
			valoresIndicadorList = new ArrayList<ValorIndicador>(getValoresIndicador());
	}
	
	/**
	 *    <p>Método que atribui ao SET que valores dos indicadores que vão ser persistidos, os valores dos
	 * indicadores que estavam na Lista transiente que foi usado para interar na página. </p>
	 * 
	 *    <p><strong>Esse método deve ser chamado antes de persistir a etiqueta local, senão os valores não vão ser atualizados no banco.</strong></p>
	 * 
	 */
	public void iniciaValoresIndicadorPersistidos(){
		if(valoresIndicador == null)
			valoresIndicador = new HashSet<ValorIndicador>();
		
		for (ValorIndicador valorIndicadorTransiente : getValoresIndicadorList()) {
			valoresIndicador.add(valorIndicadorTransiente); // se já tiver salvo o set vai eliminar, só vai ficar os novos adicionados
		}
	}
	
	
	/**
	 *  Inicia a lista transiente com os descritores permanentes do SET
	 *
	 */
	public void iniciaListaDescritoresSubCampo(){
		if(descritorSubCampoList == null && getDescritorSubCampo() != null )
			descritorSubCampoList = new ArrayList<DescritorSubCampo>(getDescritorSubCampo());
	}
	
	
	/**
	 *     <p>Método que atribui ao SET de descritores que vão ser persistidos, os descritores
	 *   que estavam na Lista transiente que foi usado para interar na página.</p>
	 * 
	 *   <p><strong>Esse método deve ser chamado antes de persistir a etiqueta local, senão os valores não vão ser atualizados no banco.</strong></p>
	 */
	public void  iniciaDescritoresSubCampoPersistidos(){
		if(descritorSubCampo == null)
			descritorSubCampo = new HashSet<DescritorSubCampo>();
		
		for (DescritorSubCampo descritorTransiente : getDescritorSubCampoList()) {
			descritorSubCampo.add(descritorTransiente); // se já tiver salvo o set vai eliminar, só vai ficar os novos adicionados
		}
		
	}
	
	/**
	 *  Verifica se a etiqueta é bibliográfica
	 *
	 * @return
	 */
	public boolean isEtiquetaBibliografica(){
		if(this.tipo == TipoCatalogacao.BIBLIOGRAFICA)
			return true;
		else
			return false;
	}
	
	/**
	 *  Verifica se a etiqueta é de autoridades
	 *
	 * @return
	 */
	public boolean isEtiquetaAutoridades(){
		if(this.tipo == TipoCatalogacao.AUTORIDADE)
			return true;
		else
			return false;
	}
	
	
	
	// Sets e gets

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;

	}

	public void setRepetivel(boolean repetivel) {
		this.repetivel = repetivel;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public char getTipoCampo() {
		return tipoCampo;
	}

	public void setTipoCampo(char tipoCampo) {
		this.tipoCampo = tipoCampo;
	}

	public String getTag() {
		return tag;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getDescricaoCompleta() {
		return tag + " " + descricao;
	}

	public boolean isRepetivel() {
		return repetivel;
	}

	public Set<ValorIndicador> getValoresIndicador() {
		return valoresIndicador;
	}

	public void setValoresIndicador(Set<ValorIndicador> valoresIndicador) {
		this.valoresIndicador = valoresIndicador;
	}


	public Set<DescritorSubCampo> getDescritorSubCampo() {
		return descritorSubCampo;
	}

	public void setDescritorSubCampo(Set<DescritorSubCampo> descritorSubCampo) {
		this.descritorSubCampo = descritorSubCampo;
	}

	public List<CategoriaMaterial> getCategoriasMaterial() {
		return categoriasMaterial;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public List<FormatoMaterialEtiqueta> getFormatosMaterialEtiqueta() {
		return formatosMaterialEtiqueta;
	}

	public short getTipo() {
		return tipo;
	}
	
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getDescricaoIndicador1() {
		return descricaoIndicador1;
	}

	public void setDescricaoIndicador1(String descricaoIndicador1) {
		this.descricaoIndicador1 = descricaoIndicador1;
	}

	public String getInfoIndicador1() {
		return infoIndicador1;
	}

	public void setInfoIndicador1(String infoIndicador1) {
		this.infoIndicador1 = infoIndicador1;
	}

	public String getDescricaoIndicador2() {
		return descricaoIndicador2;
	}

	public void setDescricaoIndicador2(String descricaoIndicador2) {
		this.descricaoIndicador2 = descricaoIndicador2;
	}

	public String getInfoIndicador2() {
		return infoIndicador2;
	}

	public void setInfoIndicador2(String infoIndicador2) {
		this.infoIndicador2 = infoIndicador2;
	}

	public void setFormatosMaterialEtiqueta(List<FormatoMaterialEtiqueta> formatosMaterialEtiqueta) {
		this.formatosMaterialEtiqueta = formatosMaterialEtiqueta;
	}

	public void setCategoriasMaterial(List<CategoriaMaterial> categoriasMaterial) {
		this.categoriasMaterial = categoriasMaterial;
	}

	public void setTipo(short tipo) {
		this.tipo = tipo;
	}

	public boolean isAlteravel() {
		return alteravel;
	}

	public void setAlteravel(boolean alteravel) {
		this.alteravel = alteravel;
	}

	public short getTipoBibliografica() {
		return TipoCatalogacao.BIBLIOGRAFICA;
	}

	public short getTipoAutoridade() {
		return TipoCatalogacao.AUTORIDADE;
	}

	public boolean isAtiva() {
		return ativa;
	}

	public void setAtiva(boolean ativa) {
		this.ativa = ativa;
	}
	
	
	public void setValoresIndicadorList(List<ValorIndicador> valoresIndicadorList) {
		this.valoresIndicadorList = valoresIndicadorList;
	}

	public void setDescritorSubCampoList(List<DescritorSubCampo> descritorSubCampoList) {
		this.descritorSubCampoList = descritorSubCampoList;
	}

	public GrupoEtiqueta getGrupo() {
		return grupo;
	}

	public void setGrupo(GrupoEtiqueta grupo) {
		this.grupo = grupo;
	}
	
}
