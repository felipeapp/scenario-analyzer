/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '26/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade que representa um artigo técnico-científico publicado por um servidor da instituição
 *
 * @author Gleydson
 */
@Entity
@Table(name = "artigo", schema = "prodocente")
@PrimaryKeyJoinColumn(name = "id_artigo")
public class Artigo extends Publicacao {

	/** Autores do artigo. */
	@Column(name = "autores")
	private String autores;

	/** Editora do artigo. */
	@Column(name = "editora")
	private String editora;

	/** Título do periódico. */
	@Column(name = "titulo_periodico")
	private String tituloPeriodico;

	/** Volume do periódico. */
	@Column(name = "volume")
	private Integer volume;

	/** Página inicial da publicação. */
	@Column(name = "pagina_inicial")
	private String paginaInicial;

	/** Página final da publicação. */
	@Column(name = "pagina_final")
	private String paginaFinal;

	/** Número da publicação. */
	@Column(name = "numero")
	private Integer numero;

	/** Indica se a publicação é de destaque. */
	@Column(name = "destaque")
	private Boolean destaque;

	/** International Standard Serial Number da publicação. */
	@Column(name = "issn")
	private String issn;

	/** Tipo do periódico. */
	@JoinColumn(name = "id_tipo_periodico", referencedColumnName = "id_tipo_periodico")
	@ManyToOne(fetch = FetchType.EAGER)
	private TipoPeriodico tipoPeriodico = new TipoPeriodico();

	/** Região de abrangência do periódico. */
	@JoinColumn(name = "id_tipo_regiao", referencedColumnName = "id_tipo_regiao")
	@ManyToOne(fetch = FetchType.EAGER)
	private TipoRegiao tipoRegiao = new TipoRegiao();


	/** Construtor padrão. */
	public Artigo() {
	}

	/** Construtor com parâmetros.
	 * @param id
	 * @param titulo
	 * @param tipoParticipacao
	 * @param ano
	 * @param validado
	 * @param idArquivo
	 * @param sequenciaProducao
	 */
	public Artigo(int id, String titulo, String tipoParticipacao, Integer ano, Boolean validado, Integer idArquivo, Integer sequenciaProducao, String issn, boolean exibir ) {

		setId(id);
		setTitulo(titulo);
		TipoParticipacao tipo = new TipoParticipacao();
		tipo.setDescricao(tipoParticipacao);
		setTipoParticipacao(tipo);
		setAnoReferencia(ano);
		setValidado(validado);
		setIdArquivo(idArquivo);
		setSequenciaProducao(sequenciaProducao);
		setIssn(issn);
		setExibir(exibir);
	}

	/** Construtor mínimo.
	 * @param id ID do artigo.
	 */
	public Artigo(Integer id) {
		setId(id);
	}

	/** Retorna os autores do artigo.  
	 * @return
	 */
	public String getAutores() {
		return autores;
	}

	/** Seta os autores do artigo. 
	 * @param autores
	 */
	public void setAutores(String autores) {
		this.autores = autores;
	}

	/** Indica se a publicação é de destaque. 
	 * @return
	 */
	public Boolean getDestaque() {
		return destaque;
	}

	/** Seta se a publicação é de destaque. 
	 * @param destaque
	 */
	public void setDestaque(Boolean destaque) {
		this.destaque = destaque;
	}

	/** Retorna a editora do artigo. 
	 * @return
	 */
	public String getEditora() {
		return editora;
	}

	/** Seta a editora do artigo. 
	 * @param editora
	 */
	public void setEditora(String editora) {
		this.editora = editora;
	}

	/** Retorna o International Standard Serial Number da publicação. 
	 * @return
	 */
	public String getIssn() {
		return issn;
	}

	/** Seta o International Standard Serial Number da publicação. 
	 * @param issn
	 */
	public void setIssn(String issn) {
		this.issn = issn;
	}

	/** Retorna o número da publicação. 
	 * @return
	 */
	public Integer getNumero() {
		return numero;
	}

	/** Seta o número da publicação. 
	 * @param numero
	 */
	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	/** Retorna a página final da publicação. 
	 * @return
	 */
	public String getPaginaFinal() {
		return paginaFinal;
	}

	/** Seta a página final da publicação.
	 * @param paginaFinal
	 */
	public void setPaginaFinal(String paginaFinal) {
		this.paginaFinal = paginaFinal;
	}

	/** Retorna a página inicial da publicação.
	 * @return
	 */
	public String getPaginaInicial() {
		return paginaInicial;
	}

	/** Retorna a página incial da publicação.
	 * @param paginaInicial
	 */
	public void setPaginaInicial(String paginaInicial) {
		this.paginaInicial = paginaInicial;
	}

	/** Retorna o tipo do periódico. 
	 * @return
	 */
	public TipoPeriodico getTipoPeriodico() {
		return tipoPeriodico;
	}

	/** Seta o tipo do periódico.
	 * @param tipoPeriodico
	 */
	public void setTipoPeriodico(TipoPeriodico tipoPeriodico) {
		this.tipoPeriodico = tipoPeriodico;
	}

	/** Retorna a região de abrangência do periódico. 
	 * @return
	 */
	public TipoRegiao getTipoRegiao() {
		return tipoRegiao;
	}

	/** Seta a região de abrangência do periódico.
	 * @param tipoRegiao
	 */
	public void setTipoRegiao(TipoRegiao tipoRegiao) {
		this.tipoRegiao = tipoRegiao;
	}

	/** Retorna o título do períodico. 
	 * @return
	 */
	public String getTituloPeriodico() {
		return tituloPeriodico;
	}

	/** Seta o título do períodico.
	 * @param tituloPeriodico
	 */
	public void setTituloPeriodico(String tituloPeriodico) {
		this.tituloPeriodico = tituloPeriodico;
	}

	/** Retorna o volume do períodico. 
	 * @return
	 */
	public Integer getVolume() {
		return volume;
	}

	/** Seta o volume do períodico.
	 * @param volume
	 */
	public void setVolume(Integer volume) {
		this.volume = volume;
	}

	/** Valida os dados do artigo. Os campos Obrigatórios são: Título, Tipo Região, Participação, 
	 * Tipo Periódico, Data, Título do Periódico
	 * @see br.ufrn.sigaa.prodocente.producao.dominio.Publicacao#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getDataProducao(),"Data de Produção", lista);
		ValidatorUtil.validateRequired(getAnoReferencia(),"Ano de Referência", lista);
		ValidatorUtil.validateRequired(getTitulo(),"Título", lista);
		ValidatorUtil.validateRequired(getAutores(),"Autores", lista);
		ValidatorUtil.validateRequired(getLocalPublicacao(),"Local de Publicação", lista);
		ValidatorUtil.validateRequired(getTituloPeriodico(),"Título do Periódico", lista);
		ValidatorUtil.validateRequired(getTipoPeriodico(),"Tipo do Periódico", lista);
		ValidatorUtil.validateRequired(getTipoRegiao(),"Âmbito", lista);
		ValidatorUtil.validateRequired(getTipoParticipacao(),"Tipo de Participação", lista);
		ValidatorUtil.validateRequired(getArea(), "Área", lista);
		ValidatorUtil.validateRequired(getSubArea(), "Sub-Área", lista);
//		lista.addAll(super.validate().getMensagens());

		return lista;
	}

	/** Retorna a descrição completa do artigo. */
	@Override
	public String getDescricaoCompleta() {
		return getTitulo() +
		(getTituloPeriodico() != null ? ", <em>" + getTituloPeriodico() + "</em>" : "") +
		(!(getIssn() == null || "".equals(getIssn())) ?  ", ISSN: " + getIssn() : "") + (getVolume() != null ? ", Vol: " + getVolume() : "") +
		(!(getPaginaInicial() == null || "".equals(getPaginaInicial())) && !(getPaginaFinal() == null || "".equals(getPaginaFinal())) ? ", Pags: " + getPaginaInicial() + " - " + getPaginaFinal() : (!(getPaginaInicial() == null || "".equals(getPaginaInicial())) ? ", Pag: " + getPaginaInicial() : ""));
	}

}
