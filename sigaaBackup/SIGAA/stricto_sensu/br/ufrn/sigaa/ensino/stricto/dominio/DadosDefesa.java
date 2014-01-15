/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 12/03/2008
 *
 */
package br.ufrn.sigaa.ensino.stricto.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

/**
 * Entidade que representa os dados de uma defesa (sustentação de uma tese ou
 * dissertação) de pós-graduação (seja qualificação ou defesa) de um discente de stricto.
 * {@link BancaPos banca de defesa de pós-graduação}
 * 
 * @author Andre Dantas
 */
@Entity
@Table(name = "dados_defesa", schema = "stricto_sensu")
public class DadosDefesa implements PersistDB, Validatable {

	/** Chave primária. */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_dados_defesa", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/**
	 * Título do trabalho
	 */
	@Column(columnDefinition= HibernateUtils.TEXT_COLUMN_DEFINITION)
	private String titulo;

	/**
	 * Total de páginas do trabalho
	 */
	private Integer paginas;

	/**
	 * Resumo do trabalho
	 */
	@Column(columnDefinition= HibernateUtils.TEXT_COLUMN_DEFINITION)
	private String resumo;
	
	/**
	 * Palavras chaves do trabalho
	 */
	@Column(name="palavras_chave", columnDefinition= HibernateUtils.TEXT_COLUMN_DEFINITION)
	private String palavrasChave;

	/**
	 * Discente autor do trabalho
	 */
	@ManyToOne
	@JoinColumn(name = "id_discente")
	private DiscenteStricto discente;

	/**
	 * Área do CNPQ a qual pertence o trabalho. Pode ser área, subárea ou especialidade 
	 */
	@ManyToOne
	@JoinColumn(name = "id_area")
	private AreaConhecimentoCnpq area;

	/**
	 * Data que foi cadastrado
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;
	
	/** Registro de entrada do discente */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroEntrada criadoPor;	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao", unique = false, nullable = true, insertable = true, updatable = true)
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atualizacao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	@AtualizadoEm
	private Date dataAtualizacao;	

	/**
	 * ID do arquivo do trabalho submetido
	 */
	@Column(name="id_arquivo")
	private Integer idArquivo;
	
	/**
	 * Link do arquivo no BDTD(UFRN) do trabalho submetido
	 */
	@Column(name="link_arquivo")
	private String linkArquivo;
	
	/**
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/**
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna o título do trabalho
	 * @return
	 */
	public String getTitulo() {
		return titulo;
	}

	/** Seta o título do trabalho
	 * @param titulo
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/** Retorna o total de páginas do trabalho
	 * @return
	 */
	public Integer getPaginas() {
		return paginas;
	}

	/** Seta o total de páginas do trabalho
	 * @param paginas
	 */
	public void setPaginas(Integer paginas) {
		this.paginas = paginas;
	}

	/** Retorna o resumo do trabalho
	 * @return
	 */
	public String getResumo() {
		return resumo;
	}

	/** Seta o resumo do trabalho
	 * @param resumo
	 */
	public void setResumo(String resumo) {
		this.resumo = resumo;
	}

	/** Retorna o discente autor do trabalho
	 * @return
	 */
	public DiscenteStricto getDiscente() {
		return discente;
	}

	/** Seta o discente autor do trabalho
	 * @param discente
	 */
	public void setDiscente(DiscenteStricto discente) {
		this.discente = discente;
	}

	/** Valida os dados: discente, título, páginas, resumo, palavras-chave, e área.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(discente, "Discente", erros);
		ValidatorUtil.validateRequired(titulo, "Título", erros);
		ValidatorUtil.validateRequired(paginas, "Páginas", erros);
		ValidatorUtil.validateRequired(resumo, "Resumo", erros);
		ValidatorUtil.validateRequired(palavrasChave, "Palavras chave", erros);
		ValidatorUtil.validateRequired(area, "Área", erros);
		return erros;
	}

	/** Retorna a área do CNPQ a qual pertence o trabalho. Pode ser área, subárea ou especialidade 
	 * @return
	 */
	public AreaConhecimentoCnpq getArea() {
		return area;
	}

	/** Seta a área do CNPQ a qual pertence o trabalho. Pode ser área, subárea ou especialidade
	 * @param area
	 */
	public void setArea(AreaConhecimentoCnpq area) {
		this.area = area;
	}

	/** Retorna o título do trabalho
	 * @return
	 */
	public String getDescricao() {
		return titulo ;
	}

	/** Retorna a data que foi cadastrado
	 * @return
	 */
	public Date getDataCadastro() {
		return dataCadastro;
	}

	/** Seta a data que foi cadastrado
	 * @param dataCadastro
	 */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/**
	 * Indica se há dado entre os dois objetos com valor diferente. Os dados
	 * comparados são: título, resumo, páginas, área e palavra-chave.
	 * 
	 * @param d
	 * @return
	 */
	public boolean isDadosDiferentes(DadosDefesa d) {
		if (!d.getTitulo().trim().equalsIgnoreCase(titulo))
			return true;
		if (!d.getResumo().trim().equalsIgnoreCase(resumo))
			return true;
		if (!d.getPaginas().equals(paginas))
			return true;
		if (!d.getArea().equals(area))
			return true;
		if (d.getPalavrasChave() == null && palavrasChave != null)
			return true;
		if (!d.getPalavrasChave().equals(palavrasChave))
			return true;
		return false;
	}

	/** Retorna o ID do arquivo do trabalho submetido
	 * @return
	 */
	public Integer getIdArquivo() {
		return idArquivo;
	}

	/** Seta o ID do arquivo do trabalho submetido
	 * @param idArquivo
	 */
	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	/** Retorna as palavras chaves do trabalho
	 * @return
	 */
	public String getPalavrasChave() {
		return palavrasChave;
	}

	/** Seta as palavras chaves do trabalho
	 * @param palavrasChave
	 */
	public void setPalavrasChave(String palavrasChave) {
		this.palavrasChave = palavrasChave;
	}

	public String getLinkArquivo() {
		if(linkArquivo != null && linkArquivo.length()>0 && linkArquivo.indexOf("http://")<0 && linkArquivo.indexOf("https://")<0)
			linkArquivo = "http://" + linkArquivo.trim();
		return linkArquivo;
	}

	public void setLinkArquivo(String linkArquivo) {
		this.linkArquivo = linkArquivo;
	}

	public RegistroEntrada getCriadoPor() {
		return criadoPor;
	}

	public void setCriadoPor(RegistroEntrada criadoPor) {
		this.criadoPor = criadoPor;
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

	/**
	 * Retorna o resumo sem tag html
	 * @return
	 */
	public String getResumoStripHtml() {
		resumo = StringUtils.toAsciiHtml(resumo);
		return StringUtils.stripHtmlTags(resumo);
	}
	
	/**
	 * Retorna o título sem tag html
	 * @return
	 */
	public String getTituloStripHtml() {
		titulo = StringUtils.toAsciiHtml(titulo);
		return StringUtils.stripHtmlTags(titulo);
	}
	
	/**
	 * Retorna palavra chave sem html
	 * @return
	 */
	public String getPalavrasChaveStripHtml() {
		palavrasChave = StringUtils.toAsciiHtml(palavrasChave);
		return StringUtils.stripHtmlTags(palavrasChave);
	}
	
}

