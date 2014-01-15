/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Entidade que representa um artigo t�cnico-cient�fico publicado por um servidor da institui��o
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

	/** T�tulo do peri�dico. */
	@Column(name = "titulo_periodico")
	private String tituloPeriodico;

	/** Volume do peri�dico. */
	@Column(name = "volume")
	private Integer volume;

	/** P�gina inicial da publica��o. */
	@Column(name = "pagina_inicial")
	private String paginaInicial;

	/** P�gina final da publica��o. */
	@Column(name = "pagina_final")
	private String paginaFinal;

	/** N�mero da publica��o. */
	@Column(name = "numero")
	private Integer numero;

	/** Indica se a publica��o � de destaque. */
	@Column(name = "destaque")
	private Boolean destaque;

	/** International Standard Serial Number da publica��o. */
	@Column(name = "issn")
	private String issn;

	/** Tipo do peri�dico. */
	@JoinColumn(name = "id_tipo_periodico", referencedColumnName = "id_tipo_periodico")
	@ManyToOne(fetch = FetchType.EAGER)
	private TipoPeriodico tipoPeriodico = new TipoPeriodico();

	/** Regi�o de abrang�ncia do peri�dico. */
	@JoinColumn(name = "id_tipo_regiao", referencedColumnName = "id_tipo_regiao")
	@ManyToOne(fetch = FetchType.EAGER)
	private TipoRegiao tipoRegiao = new TipoRegiao();


	/** Construtor padr�o. */
	public Artigo() {
	}

	/** Construtor com par�metros.
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

	/** Construtor m�nimo.
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

	/** Indica se a publica��o � de destaque. 
	 * @return
	 */
	public Boolean getDestaque() {
		return destaque;
	}

	/** Seta se a publica��o � de destaque. 
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

	/** Retorna o International Standard Serial Number da publica��o. 
	 * @return
	 */
	public String getIssn() {
		return issn;
	}

	/** Seta o International Standard Serial Number da publica��o. 
	 * @param issn
	 */
	public void setIssn(String issn) {
		this.issn = issn;
	}

	/** Retorna o n�mero da publica��o. 
	 * @return
	 */
	public Integer getNumero() {
		return numero;
	}

	/** Seta o n�mero da publica��o. 
	 * @param numero
	 */
	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	/** Retorna a p�gina final da publica��o. 
	 * @return
	 */
	public String getPaginaFinal() {
		return paginaFinal;
	}

	/** Seta a p�gina final da publica��o.
	 * @param paginaFinal
	 */
	public void setPaginaFinal(String paginaFinal) {
		this.paginaFinal = paginaFinal;
	}

	/** Retorna a p�gina inicial da publica��o.
	 * @return
	 */
	public String getPaginaInicial() {
		return paginaInicial;
	}

	/** Retorna a p�gina incial da publica��o.
	 * @param paginaInicial
	 */
	public void setPaginaInicial(String paginaInicial) {
		this.paginaInicial = paginaInicial;
	}

	/** Retorna o tipo do peri�dico. 
	 * @return
	 */
	public TipoPeriodico getTipoPeriodico() {
		return tipoPeriodico;
	}

	/** Seta o tipo do peri�dico.
	 * @param tipoPeriodico
	 */
	public void setTipoPeriodico(TipoPeriodico tipoPeriodico) {
		this.tipoPeriodico = tipoPeriodico;
	}

	/** Retorna a regi�o de abrang�ncia do peri�dico. 
	 * @return
	 */
	public TipoRegiao getTipoRegiao() {
		return tipoRegiao;
	}

	/** Seta a regi�o de abrang�ncia do peri�dico.
	 * @param tipoRegiao
	 */
	public void setTipoRegiao(TipoRegiao tipoRegiao) {
		this.tipoRegiao = tipoRegiao;
	}

	/** Retorna o t�tulo do per�odico. 
	 * @return
	 */
	public String getTituloPeriodico() {
		return tituloPeriodico;
	}

	/** Seta o t�tulo do per�odico.
	 * @param tituloPeriodico
	 */
	public void setTituloPeriodico(String tituloPeriodico) {
		this.tituloPeriodico = tituloPeriodico;
	}

	/** Retorna o volume do per�odico. 
	 * @return
	 */
	public Integer getVolume() {
		return volume;
	}

	/** Seta o volume do per�odico.
	 * @param volume
	 */
	public void setVolume(Integer volume) {
		this.volume = volume;
	}

	/** Valida os dados do artigo. Os campos Obrigat�rios s�o: T�tulo, Tipo Regi�o, Participa��o, 
	 * Tipo Peri�dico, Data, T�tulo do Peri�dico
	 * @see br.ufrn.sigaa.prodocente.producao.dominio.Publicacao#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getDataProducao(),"Data de Produ��o", lista);
		ValidatorUtil.validateRequired(getAnoReferencia(),"Ano de Refer�ncia", lista);
		ValidatorUtil.validateRequired(getTitulo(),"T�tulo", lista);
		ValidatorUtil.validateRequired(getAutores(),"Autores", lista);
		ValidatorUtil.validateRequired(getLocalPublicacao(),"Local de Publica��o", lista);
		ValidatorUtil.validateRequired(getTituloPeriodico(),"T�tulo do Peri�dico", lista);
		ValidatorUtil.validateRequired(getTipoPeriodico(),"Tipo do Peri�dico", lista);
		ValidatorUtil.validateRequired(getTipoRegiao(),"�mbito", lista);
		ValidatorUtil.validateRequired(getTipoParticipacao(),"Tipo de Participa��o", lista);
		ValidatorUtil.validateRequired(getArea(), "�rea", lista);
		ValidatorUtil.validateRequired(getSubArea(), "Sub-�rea", lista);
//		lista.addAll(super.validate().getMensagens());

		return lista;
	}

	/** Retorna a descri��o completa do artigo. */
	@Override
	public String getDescricaoCompleta() {
		return getTitulo() +
		(getTituloPeriodico() != null ? ", <em>" + getTituloPeriodico() + "</em>" : "") +
		(!(getIssn() == null || "".equals(getIssn())) ?  ", ISSN: " + getIssn() : "") + (getVolume() != null ? ", Vol: " + getVolume() : "") +
		(!(getPaginaInicial() == null || "".equals(getPaginaInicial())) && !(getPaginaFinal() == null || "".equals(getPaginaFinal())) ? ", Pags: " + getPaginaInicial() + " - " + getPaginaFinal() : (!(getPaginaInicial() == null || "".equals(getPaginaInicial())) ? ", Pag: " + getPaginaInicial() : ""));
	}

}
