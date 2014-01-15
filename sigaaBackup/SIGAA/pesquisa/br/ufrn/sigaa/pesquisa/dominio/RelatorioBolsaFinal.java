/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/01/2007
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;


/**
 * Classe representativa dos relatórios finais de bolsas de iniciação científica
 * 
 * @author ricardo
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "relatorio_bolsa_final", schema = "pesquisa", uniqueConstraints = {})
public class RelatorioBolsaFinal  extends AbstractMovimento implements PersistDB, Validatable {

	// Fields

	/** Chave primária */
	private int id;

	/** Plano de trabalho do relatório do bolsa final */
	private PlanoTrabalho planoTrabalho;

	/** Membros que compõem o relatório Bolsa final */
	private MembroProjetoDiscente membroDiscente;

	/** Introdução do relatório Bolsa final */
	private String introducao;

	/** Objetivos do relatório Bolsa final*/
	private String objetivos;

	/** Metodologia do relatório Bolsa final */
	private String metodologia;

	/** Resultados do relatório Bolsa final*/
	private String resultados;

	/** Conclusões do relatório Bolsa final */
	private String conclusoes;

	/** Perspectivas do relatório Bolsa final */
	private String perspectivas;

	/** Parecer do Orientador do relatório Bolsa final*/
	private String parecerOrientador;

	/** Data do envio do relatório Bolsa final */
	private Date dataEnvio;

	/** Discussão do relatório Bolsa final*/
	private String discussao;

	/** Bibliografia do relatório Bolsa final */
	private String bibliografia;

	/** Outras Atividades do relatório Bolsa final */
	private String outrasAtividades;

	/** Resumo do relatório Bolsa final */
	private String resumo;

	/** Palavras chave do relatório Bolsa final*/
	private String palavrasChave;

	/** Data do parecer do relatório Bolsa final */
	private Date dataParecer;
	
	/** Indicação do relatório Bolsa final se foi enviado ou não */
	private boolean enviado;

	@CampoAtivo
	private boolean ativo;
	
	// Constructors

	/** default constructor */
	public RelatorioBolsaFinal() {
	}

	/** minimal constructor */
	public RelatorioBolsaFinal(int idRelatorioDiscenteFinal) {
		this.id = idRelatorioDiscenteFinal;
	}

	/** full constructor */
	public RelatorioBolsaFinal(int idRelatorioDiscenteFinal,
			PlanoTrabalho planoTrabalho, String introducao,
			String objetivos, String metodologia, String resultados,
			String conclusoes, String perspectivas, String parecerOrientador,
			Date dataEnvio, String discussao, String bibliografia,
			String outrasAtividades, String resumo, String palavrasChave,
			Date dataParecer) {

		this.id = idRelatorioDiscenteFinal;
		this.planoTrabalho = planoTrabalho;
		this.introducao = introducao;
		this.objetivos = objetivos;
		this.metodologia = metodologia;
		this.resultados = resultados;
		this.conclusoes = conclusoes;
		this.perspectivas = perspectivas;
		this.parecerOrientador = parecerOrientador;
		this.dataEnvio = dataEnvio;
		this.discussao = discussao;
		this.bibliografia = bibliografia;
		this.outrasAtividades = outrasAtividades;
		this.resumo = resumo;
		this.palavrasChave = palavrasChave;
		this.dataParecer = dataParecer;
	}

	// Property accessors
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_relatorio_discente_final", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idRelatorioDiscenteFinal) {
		this.id = idRelatorioDiscenteFinal;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_plano_trabalho", unique = false, nullable = true, insertable = true, updatable = true)
	public PlanoTrabalho getPlanoTrabalho() {
		return this.planoTrabalho;
	}

	public void setPlanoTrabalho(
			PlanoTrabalho planoTrabalho) {
		this.planoTrabalho = planoTrabalho;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_membro_discente", unique = false, nullable = true, insertable = true, updatable = true)
	public MembroProjetoDiscente getMembroDiscente() {
		return membroDiscente;
	}

	public void setMembroDiscente(MembroProjetoDiscente membroDiscente) {
		this.membroDiscente = membroDiscente;
	}

	@Column(name = "introducao", unique = false, nullable = true, insertable = true, updatable = true)
	public String getIntroducao() {
		return this.introducao;
	}

	public void setIntroducao(String introducao) {
		this.introducao = introducao;
	}

	@Column(name = "objetivos", unique = false, nullable = true, insertable = true, updatable = true)
	public String getObjetivos() {
		return this.objetivos;
	}

	public void setObjetivos(String objetivos) {
		this.objetivos = objetivos;
	}

	@Column(name = "metodologia", unique = false, nullable = true, insertable = true, updatable = true)
	public String getMetodologia() {
		return this.metodologia;
	}

	public void setMetodologia(String metodologia) {
		this.metodologia = metodologia;
	}

	@Column(name = "resultados", unique = false, nullable = true, insertable = true, updatable = true)
	public String getResultados() {
		return this.resultados;
	}

	public void setResultados(String resultado) {
		this.resultados = resultado;
	}

	@Column(name = "conclusoes", unique = false, nullable = true, insertable = true, updatable = true)
	public String getConclusoes() {
		return this.conclusoes;
	}

	public void setConclusoes(String conclusoes) {
		this.conclusoes = conclusoes;
	}

	@Column(name = "perspectivas", unique = false, nullable = true, insertable = true, updatable = true)
	public String getPerspectivas() {
		return this.perspectivas;
	}

	public void setPerspectivas(String perspectiva) {
		this.perspectivas = perspectiva;
	}

	@Column(name = "parecer_orientador", unique = false, nullable = true, insertable = true, updatable = true)
	public String getParecerOrientador() {
		return this.parecerOrientador;
	}

	public void setParecerOrientador(String parecerOrientador) {
		this.parecerOrientador = parecerOrientador;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_envio", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataEnvio() {
		return this.dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	@Column(name = "discussao", unique = false, nullable = true, insertable = true, updatable = true)
	public String getDiscussao() {
		return this.discussao;
	}

	public void setDiscussao(String discussao) {
		this.discussao = discussao;
	}

	@Column(name = "bibliografia", unique = false, nullable = true, insertable = true, updatable = true)
	public String getBibliografia() {
		return this.bibliografia;
	}

	public void setBibliografia(String bibliografia) {
		this.bibliografia = bibliografia;
	}

	@Column(name = "outras_atividades", unique = false, nullable = true, insertable = true, updatable = true)
	public String getOutrasAtividades() {
		return this.outrasAtividades;
	}

	public void setOutrasAtividades(String outrasAtividades) {
		this.outrasAtividades = outrasAtividades;
	}

	@Column(name = "resumo", unique = false, nullable = true, insertable = true, updatable = true)
	public String getResumo() {
		return this.resumo;
	}

	public void setResumo(String resumo) {
		this.resumo = resumo;
	}

	@Column(name = "palavras_chave", unique = false, nullable = true, insertable = true, updatable = true, length = 200)
	public String getPalavrasChave() {
		return this.palavrasChave;
	}

	public void setPalavrasChave(String palavrasChave) {
		this.palavrasChave = palavrasChave;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_parecer", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataParecer() {
		return this.dataParecer;
	}

	public void setDataParecer(Date dataParecer) {
		this.dataParecer = dataParecer;
	}

	@Column(name = "relatorio_enviado", unique = false, nullable = true, insertable = true, updatable = true)
	public boolean isEnviado() {
		return enviado;
	}

	public void setEnviado(boolean enviado) {
		this.enviado = enviado;
	}
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	public ListaMensagens validate() {

		ListaMensagens erros = new ListaMensagens();

		if (getResumo() == null || "".equals(getResumo().trim()))
			ValidatorUtil.validateRequired(null, "Resumo", erros);
		else if (getResumo().trim().replace("\r\n", "\n").length() > 1500 )
			erros.addMensagem(MensagensArquitetura.MAXIMO_CARACTERES, "Resumo", 1500);
		if (getPalavrasChave() == null || "".equals(getPalavrasChave().trim()))
			ValidatorUtil.validateRequired(null, "Palavras-Chave", erros);
		else if (getPalavrasChave().replace("\r\n", "\n").trim().length() > 70 )
			erros.addMensagem(MensagensArquitetura.MAXIMO_CARACTERES, "Palavra-Chave", 70);
		if (getIntroducao() == null || "".equals(getIntroducao().trim()))
			ValidatorUtil.validateRequired(null, "Introdução", erros);
		else if( getIntroducao().trim().replace("\r\n", "\n").length() > 10000 )
			erros.addMensagem(MensagensArquitetura.MAXIMO_CARACTERES, "Introdução", 10000);
		if (getObjetivos() == null || "".equals(getObjetivos().trim()))
			ValidatorUtil.validateRequired(null, "Objetivos", erros);
		else if (getObjetivos().replace("\r\n", "\n").trim().length() > 2000 )
			erros.addMensagem(MensagensArquitetura.MAXIMO_CARACTERES, "Objetivos", 2000);
		if (getMetodologia() == null || "".equals(getMetodologia().trim()))
			ValidatorUtil.validateRequired(null, "Metodologia", erros);
		else if (getMetodologia().replace("\r\n", "\n").trim().length() > 10000 )
			erros.addMensagem(MensagensArquitetura.MAXIMO_CARACTERES, "Metodologia", 10000);
		if (getResultados() == null || "".equals(getResultados().trim()))
			ValidatorUtil.validateRequired(null, "Resultados", erros);
		else if (getResultados().replace("\r\n", "\n").trim().length() > 10000 )
			erros.addMensagem(MensagensArquitetura.MAXIMO_CARACTERES, "Resultados", 10000);
		if (getDiscussao() == null || "".equals(getDiscussao().trim()))
			ValidatorUtil.validateRequired(null, "Discursões", erros);
		else if (getDiscussao().replace("\r\n", "\n").trim().length() > 10000 )
			erros.addMensagem(MensagensArquitetura.MAXIMO_CARACTERES, "Discursões", 10000);
		if (getConclusoes() == null || "".equals(getConclusoes().trim()))
			ValidatorUtil.validateRequired(null, "Conclusões", erros);
		else if (getConclusoes().replace("\r\n", "\n").trim().length() > 2000 )
			erros.addMensagem(MensagensArquitetura.MAXIMO_CARACTERES, "Conclusões", 2000);
		if (getPerspectivas() == null || "".equals(getPerspectivas().trim()))
			ValidatorUtil.validateRequired(null, "Perspectivas", erros);
		else if (getPerspectivas().replace("\r\n", "\n").trim().length() > 2000 )
			erros.addMensagem(MensagensArquitetura.MAXIMO_CARACTERES, "Perspectivas", 2000);

		if (getBibliografia() == null || "".equals(getBibliografia().trim()))
			ValidatorUtil.validateRequired(null, "Bibliografia", erros);

		if (getOutrasAtividades() == null || "".equals(getOutrasAtividades().trim()))
			ValidatorUtil.validateRequired(null, "Outras Atividades", erros);
		else if (getOutrasAtividades().replace("\r\n", "\n").trim().length() > 2000 )
			erros.addMensagem(MensagensArquitetura.MAXIMO_CARACTERES, "Outras Atividades", 2000);
			
		return erros;
	}

	
	@Transient
	public String getEnviadoString(){
		return isEnviado() ? "Sim" : "Não";
	}
	
	@Transient
	public String getParecerEmitidoString(){
		return getParecerOrientador() != null ? "Sim" : "Não";
	}
}
