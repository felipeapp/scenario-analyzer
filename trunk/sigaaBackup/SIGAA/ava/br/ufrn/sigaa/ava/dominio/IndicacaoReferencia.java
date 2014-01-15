 /*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 31/01/2008
 *
 */
package br.ufrn.sigaa.ava.dominio;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.validator.UrlValidator;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Entidade que representa uma indica��o para uma refer�ncia a ser utilizada na turma. Pode ser um livro, artigo, revista, etc. Tamb�m h�
 * a possibilidade de associar a indica��o de refer�ncia a um livro das bibliotecas da institui��o. 
 *
 *
 * @author Gleydson
 */
@Entity @HumanName(value="Refer�ncia", genero='F')
@Table(name = "indicacao_referencia", schema = "ava")
public class IndicacaoReferencia extends AbstractMaterialTurma implements DominioTurmaVirtual {

	/** Tipos de indica��o da refer�ncia */
	/** Indica��o a um artigo */
	public static final char TIPO_ARTIGO = 'A';
	/** Indica��o a um livro */
	public static final char TIPO_LIVRO = 'L';
	/** Indica��o a uma revista */
	public static final char TIPO_REVISTA = 'R';
	/** Indica��o a um site */
	public static final char TIPO_SITE = 'S';
	/** Outro tipo de indica��o */
	public static final char TIPO_OUTROS = 'O';
	
	/** A indica��o da refer�ncia � b�sica */
	public static final int TIPO_INDICACAO_BASICA = 1;
	/** A indica��o da refer�ncia � complementar */
	public static final int TIPO_INDICACAO_COMPLEMENTAR = 2;
	
	/** Chave pirm�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_indicacao_referencia", nullable = false)
	private int id;

	/** Turma da refer�ncia */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_turma")
	private Turma turma;

	/** URL da indica��o */
	@Column(name = "url")
	private String url;

	/** Descri��o ou t�tulo da indica��o */
	@Column(name = "descricao")
	private String descricao;

	/** Datalhes da indica��o */
	private String detalhes;

	/** Tipo de indica��o */
	private char tipo = TIPO_LIVRO; // Artigo - A; Livro - L; Revista - R; Site - S; Outros - O
	
	/** Tipo de Indica��o - B�sica ou Complementar */
	@Column(name="tipo_indicacao")
	private Integer tipoIndicacao; // B�sica - 1; Complementar - 2

	/** T�pico de Aula que a refer�ncia est� associada. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_topico_aula")
	private TopicoAula aula = new TopicoAula();

	/** Data de cadastro da refer�ncia. */
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;
	
	/** Id do usu�rio que cadastrou a refer�ncia. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_usuario")
	@CriadoPor
	private Usuario usuarioCadastro;
	
	/** Atributos usados quando a Indica��o de Refer�ncia � do tipo LIVRO */	
	/**
	 * O T�tulo Catalogr�fico referenciado. Null se n�o existir nas bibliotecas da institui��o.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_titulo_catalografico")
	private TituloCatalografico tituloCatalografico;
	
	/** T�tulo do livro indicado. */
	private String titulo;
	/** Autor do livro. */
	private String autor;
	/** Editora do livro. */
	private String editora;
	/** Ano do livro. */
	private String ano;
	/** Edi��o do livro */
	private String edicao;
	/** Lista de exemplares do livro presente nas bibliotecas da institui��o. */
	@Transient
	private List<Exemplar> exemplares = null;
	
	/** Se a refer�ncia foi selecionada ou n�o. Usada na importa��o de dados de turma anteriores. */
	@Transient
	private boolean selecionada;
	
	/** Material Turma. */
	@ManyToOne(fetch=FetchType.LAZY, cascade= CascadeType.ALL)
	@JoinColumn(name = "id_material_turma")
	private MaterialTurma material = new MaterialTurma(TipoMaterialTurma.REFERENCIA);
	
	/** Identifica se o registro est� ativo no sistema. */
	@CampoAtivo
	private boolean ativo = true;
	
	
	/**
	 * Retorna um mapa contendo os tipos de indica��o de refer�ncia associados �s suas descri��es.
	 * 
	 * @return
	 */
	public static Map <Character, String> getAllTipos (){
		Map <Character, String> allTipos = new HashMap <Character, String> ();
		allTipos.put(TIPO_LIVRO, "Livro");
		allTipos.put(TIPO_ARTIGO, "Artigo");
		allTipos.put(TIPO_REVISTA, "Revista");
		allTipos.put(TIPO_SITE, "Site");
		allTipos.put(TIPO_OUTROS, "Outros");
		
		return allTipos;
	}
	
	@Transient @Override
	public boolean isSite() {
		return tipo == TIPO_SITE;
	}
	
	public boolean isLivro() {
		return tipo == TIPO_LIVRO;
	}
	
	public boolean isRevista() {
		return tipo == TIPO_REVISTA;
	}
	
	public boolean isArtigo() {
		return tipo == TIPO_ARTIGO;
	}
	
	public boolean isOutro() {
		return tipo == TIPO_OUTROS;
	}
	
	public String getDetalhes() {
		return detalhes;
	}

	public void setDetalhes(String detalhes) {
		this.detalhes = detalhes;
	}

	/** Creates a new instance of PlanoCursoFavoritos */
	public IndicacaoReferencia() {
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	/**
	 * Pega a Url da refer�ncia. 
	 * @return Url Associada a uma referencia
	 */
	public String getUrl() {
		
		if ( url != null && !url.startsWith("http://") && !url.startsWith("https://")) {
			return "http://" + url;
		} else {
			return url;
		}
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public TopicoAula getAula() {
		return aula;
	}

	public void setAula(TopicoAula aula) {
		this.aula = aula;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		if (descricao == null || "".equals(descricao.trim()))
			lista.addErro("Digite o nome/t�tulo da refer�ncia.");

		if (tipo == '-')
			lista.addErro("Selecione um tipo de refer�ncia.");
		
		if (tipo == 'S') {
			UrlValidator val = new UrlValidator();
			if (!val.isValid(url)) {
				lista.addErro("A url digitada n�o � v�lida!");
			}
		}

		return lista;
	}

	public char getTipo() {
		return tipo;
	}

	public void setTipo(char tipo) {
		this.tipo = tipo;
	}

	/**
	 * Pega a data em que uma referencia foi cadastrada.
	 * @return the dataCadastro
	 */
	public Date getDataCadastro() {
		return dataCadastro;
	}

	/**Atribui o valor para a data de cadatro da referencia. 
	 * @param dataCadastro the dataCadastro to set
	 */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/**Pega o usuario que cadastrou uma referencia.
	 * @return the usuarioCadastro
	 */
	public Usuario getUsuarioCadastro() {
		return usuarioCadastro;
	}

	public TituloCatalografico getTituloCatalografico() {
		return tituloCatalografico;
	}

	public void setTituloCatalografico(TituloCatalografico tituloCatalografico) {
		this.tituloCatalografico = tituloCatalografico;
	}

	/**Atribui o valor para o usuario que  cadatrou a referencia.
	 * @param usuarioCadastro the usuarioCadastro to set
	 */
	public void setUsuarioCadastro(Usuario usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	@Override
	public String getNome() {
		return descricao;
	}
	/**
	 * Pega o tipo da indicacao (Artigo,Livro,Revista,Site ou Outros).
	 * @return tipo da indica��o
	 */
	public String getTipoDesc() {
		switch(tipo) {
			case 'A': return "Artigo";
			case 'L': return "Livro";
			case 'R': return "Revista";
			case 'S': return "Site";
			default: return "Outros";
		}
	}

	public String getMensagemAtividade() {
		return "Indica��o de " + getTipoDesc() + ": " + descricao;
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

	public String getEditora() {
		return editora;
	}

	public void setEditora(String editora) {
		this.editora = editora;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public String getEdicao() {
		return edicao;
	}

	public void setEdicao(String edicao) {
		this.edicao = edicao;
	}
	
	public boolean equals(Object o){
		return EqualsUtil.testEquals(this, o, "id");
	}
	
	public int hashCode (){
		return HashCodeUtil.hashAll(getId());
	}

	public Integer getTipoIndicacao() {
		return tipoIndicacao;
	}
	
	public String getTipoIndicacaoDesc (){
		return tipoIndicacao != null && tipoIndicacao == 1 ? "B�sica" : "Complementar";
	}

	/**
	 * retorna true caso uma referencia seja basica e false caso contrario.
	 * @return
	 */
	public boolean isIndicacaoBasica (){
		if ( tipoIndicacao == 1 )
			return true;
		else return false;
	}
	
	public void setTipoIndicacao(Integer tipoIndicacao) {
		this.tipoIndicacao = tipoIndicacao;
	}

	public boolean isSelecionada() {
		return selecionada;
	}

	public void setSelecionada(boolean selecionada) {
		this.selecionada = selecionada;
	}

	public void setExemplares(List<Exemplar> exemplares) {
		this.exemplares = exemplares;
	}

	public List<Exemplar> getExemplares() {
		return exemplares;
	}

	public MaterialTurma getMaterial() {
		return material;
	}

	public void setMaterial(MaterialTurma material) {
		this.material = material;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	@Override
	public String getDescricaoGeral() {		
		return getTipoDesc() + ", Tipo de indica��o: " + getTipoIndicacaoDesc() ;
	}
	
}