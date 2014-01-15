/*
 * CategoriaMaterial.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import static br.ufrn.arq.util.StringUtils.unescapeHTML;

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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 *
 * Define os valores para o campo 007 
 * Categoria do Material so vi sendo usada ate agora para o campo 007.
 * 
 * Essa classe eh usado principalmente na hora de montar a tela onde os dados do campo
 * 007 sao inseridos e na hora da validacao do campo 007, porque os dados dependem da
 * categoria.
 * 
 * Categoria eh na verdade a primeira posicao do campo 007, mas como as outras posicoes
 * dependem da primeira criei uma classe separada para fazer as associacoes.
 * O primerio carater do campo 007 eh copiado para a variavel <code>codigo</code> dessa classe
 *
 *
 * @author jadson
 * @since 09/12/2008
 * @version 1.0 criacao da classe
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "categoria_material", schema = "biblioteca")
public class CategoriaMaterial implements PersistDB{

	/** Constantes que guardam os valores das categoria de materias para nao precisar ficar 
	 * buscando em banco ja que esses valores fazem parte de um padrao e se o que eles chamam
	 * de padrao eh o mesmo que eu conheco nao eram para mudar */
	public static final CategoriaMaterial MAPA = new CategoriaMaterial('a', "Mapa");
	public static final CategoriaMaterial ARQUIVO_DE_COMPUTADOR = new CategoriaMaterial('c', "Arquivo de computador");
	public static final CategoriaMaterial GLOBO = new CategoriaMaterial('d', "Globo");
	public static final CategoriaMaterial MATERIAL_PROJETAVEL = new CategoriaMaterial('g', "Material projetável");
	public static final CategoriaMaterial MICROFORMA = new CategoriaMaterial('h', "Microforma");
	public static final CategoriaMaterial MATERIAL_NAO_PROJETAVEL = new CategoriaMaterial('k', "Material não projetável");
	public static final CategoriaMaterial FILME = new CategoriaMaterial('m', "Filme");
	public static final CategoriaMaterial SENSORIAMENTO_REMOTO= new CategoriaMaterial('r', "Sensoriamento remoto");
	public static final CategoriaMaterial GRAVACAO_SONORA = new CategoriaMaterial('s', "Gravação sonora");
	public static final CategoriaMaterial TEXTO = new CategoriaMaterial('t', "Texto");
	public static final CategoriaMaterial GRAVACAO_EM_VIDEO = new CategoriaMaterial('v', "Gravação em vídeo");
	public static final CategoriaMaterial NAO_ESPECIFICADO = new CategoriaMaterial('z', "Não especificado");


	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })	
	@Column(name="id_categoria_material")
	private int id;

	// o codigo da catagoria a , b , c ...
	// esse eh na verdade a primeiro carater do campo 007 que influencia todos os demais valores
	@Column(name = "codigo", nullable=false)
	private Character codigo; 

	@Column(name = "descricao", nullable=false)
	private String descricao; // o que esse codigo significa

	
	/* A Etiqueta a que se refere */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_etiqueta", referencedColumnName="id_etiqueta")
	private Etiqueta etiqueta;

	/*
	 * Possui varios descritores que informam onde comeca e onde termina e o que significa os 
	 * dados que estão todos juntos no campo de dados do registro de controle.
	 */
	@OneToMany(mappedBy="categoriaMaterial", cascade={CascadeType.REMOVE})
	private List<DescritorCampoControle> descritoresCampoControle;
	
	
	/** informação sobre a categoria para ser apresentada ao usuário */
	private String info;
	
	
	/* 
	 * Categoria Material vai existir um conjunto de valores padrao para o campo 007 
	 * */
	@SuppressWarnings("unused")  // É usado assim, sendo que é buscado direto no banco, não usa o get para pegar.
	@OneToMany(mappedBy="categoriaMaterial", cascade={CascadeType.REMOVE})
	private List<ValorPadraoCampoControle> valoresPadraoControle;
	
	
	/* Desativadas estão as categorias que vieram do aleph mas não existem no padrão MARC*/
	@SuppressWarnings("unused")
	@Column(name="ativo", nullable=false)
	private boolean ativo;
	
	
	/**
	 * Construtor padrao para hibernate e jsf
	 */
	public CategoriaMaterial(){
	}

	/**
	 *      Construtor geralmente usado para cria um objeto CategoriaMaterial na memoria 
	 * (usado pelas contantes acima)
	 * 
	 * @param id
	 * @param codigo
	 * @param descricao
	 */
	public CategoriaMaterial(char codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;		
	}

	public Etiqueta getEtiqueta() {
		return etiqueta;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = unescapeHTML(info);
	}

	public Character getCodigo() {
		return codigo;
	}

	public void setCodigo(Character codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = unescapeHTML(descricao);
	}

	public void setEtiqueta(Etiqueta etiqueta) {
		this.etiqueta = etiqueta;
	}

	public List<DescritorCampoControle> getDescritoresCampoControle() {
		return descritoresCampoControle;
	}
	
	public void setDescritoresCampoControle(List<DescritorCampoControle> descritoresCampoControle) {
		this.descritoresCampoControle = descritoresCampoControle;
	}

}
