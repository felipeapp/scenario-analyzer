/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 12/03/2007
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;
import static br.ufrn.arq.util.ValidatorUtil.validaInicioFim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

/**
 * Classe com os tipos de bolsa de pesquisa
 *
 * @author Ricardo Wendell
 *
 */
@Entity
@Table(name="tipo_bolsa_pesquisa", schema = "pesquisa", uniqueConstraints = {})
public class TipoBolsaPesquisa implements Validatable {

	/** Tipo tempor�rio enquanto o docente n�o recebe a cota */
	public static final int A_DEFINIR = 100;

	/** Constante do tipo de bolsa PROPESQ */
	public static final int PROPESQ = 1;
	/** Constante do tipo de bolsa PIBIC */
	public static final int PIBIC = 2;
	/** Constante do tipo de bolsa BALCAO */
	public static final int BALCAO = 3;
	/** Constante do tipo de bolsa VOLUNTARIO */
	public static final int VOLUNTARIO = 4;
	/** Constante do tipo de bolsa OUTROS */
	public static final int OUTROS = 5;
	/** Constante do tipo de bolsa PROPESQ_INDUCAO */
	public static final int PROPESQ_INDUCAO = 6;
	/** Constante do tipo de bolsa PROPESQ_IT */
	public static final int PROPESQ_IT = 7;
	/** Constante do tipo de bolsa PIBIT */
	public static final int PIBIT = 8;
	/** Constante do tipo de bolsa VOLUNTARIO_IT */
	public static final int VOLUNTARIO_IT = 9;
	/** Constante do tipo de bolsa PIBIC_EM */
	public static final int PIBIC_EM = 4779164;
	/** Constante do tipo de bolsa Reuni IT */
	public static final int REUNI_IT = 11511581;
	/** Constante do tipo de bolsa Reuni IC */
	public static final int REUNI_IC = 11511580;
	
	/** Mapa com os tipos de bolsas */
	private static Map<Integer, String> tipos = new TreeMap<Integer, String>();
	/** Mapa com os tipos de bolsas de volunt�rios */
	private static Map<Integer, String> tiposVoluntarios = new TreeMap<Integer, String>();

	static {
		tipos.put(A_DEFINIR, "A DEFINIR");
		tipos.put(PROPESQ, "PROPESQ");
		tipos.put(PROPESQ_INDUCAO, "PROPESQ - INDU��O");
		tipos.put(PIBIC, "PIBIC");
		tipos.put(BALCAO, "BALC�O");
		tipos.put(VOLUNTARIO, "VOLUNT�RIO (IC)");
		tipos.put(VOLUNTARIO_IT, "VOLUNT�RIO (IT)");
		tipos.put(OUTROS, "OUTROS");
		tipos.put(PROPESQ_IT, "PROPESQ (IT)");
		tipos.put(PIBIT, "PIBIT");
		tipos.put(PIBIC_EM, "PIBIC (EM)");
		
		tiposVoluntarios.put(VOLUNTARIO, "VOLUNT�RIO (IC)");
		tiposVoluntarios.put(VOLUNTARIO_IT, "VOLUNT�RIO (IT)");		
	}

	/** Respons�vel por retornar a Descri��o do tipo de Bolsa de Pesquisa */
	public static String getDescricao(int tipo) {
		return tipos.get(tipo);
	}

	/** Respons�vel por retornar os tipos de Bolsa de Pesquisa */
	public static Map<Integer, String> getTipos() {
		return tipos;
	}
	
	/** Respons�vel por retornar os tipos de Volunt�rios */
	public static Map<Integer, String> getTiposVoluntarios() {
		return tiposVoluntarios;
	}

	/** Respons�vel por retornar se o Tipo de Bolsa e Externa ou n�o */
	public static boolean isExterna(int tipo) {
		return tipo == BALCAO || tipo == OUTROS;
	}

	/** Respons�vel por retornar se o Tipo de Bolsa e Volunt�rio ou n�o */
	public static boolean isVoluntario(int tipo) {
		return tipo == VOLUNTARIO || tipo == VOLUNTARIO_IT;
	}
	
	/** Inicio da defini��o dos atributos e seus mapeamentos */

	/** Chave prim�ria */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_tipo_bolsa", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Categoria que o Tipo de Bolsa de Pesquisa se enquadra */
	private int categoria;

	/** Indica se o tipo de bolsa requer o envio de relat�rios parciais e finais ou n�o */
	@Column(name = "necessita_relatorio")
	private boolean necessitaRelatorio;

	/** Indica se o tipo de bolsa est� vinculado a um per�odo de cota ou n�o */
	@Column(name = "vinculado_cota")
	private boolean vinculadoCota;

	/** Descri��o do Tipo de Bolsa Pesquisa */
	private String descricao;

	/** Indica se o Tipo de Bolsa Pesquisa est� em uso */
	private boolean ativo = true;
	
	/** Armazena o tipo de bolsa que est� associado no SIPAC */
	@Column(name = "id_tipo_bolsa_sipac")
	private Integer tipoBolsaSipac;
	
	/** Indica quais n�veis este tipo de bolsa � indicado. */
	@Column (name = "niveis_permitidos")
	private String niveisPermitidos;
	
	/** In�cio do per�odo de envio de relat�rios parciais para este tipo de bolsa */
	@Temporal(TemporalType.DATE)
	@Column(name = "inicio_envio_relatorio_parcial")
	private Date inicioEnvioRelatorioParcial;
	
	/** Fim do per�odo de envio de relat�rios parciais para este tipo de bolsa */
	@Temporal(TemporalType.DATE)
	@Column(name = "fim_envio_relatorio_parcial")
	private Date fimEnvioRelatorioParcial;
	
	/** In�cio do per�odo de envio de relat�rios finais para este tipo de bolsa */
	@Temporal(TemporalType.DATE)
	@Column(name = "inicio_envio_relatorio_final")
	private Date inicioEnvioRelatorioFinal;
	
	/** Fim do per�odo de envio de relat�rios finais para este tipo de bolsa */
	@Temporal(TemporalType.DATE)
	@Column(name = "fim_envio_relatorio_final")
	private Date fimEnvioRelatorioFinal;
	
	/** In�cio do per�odo de envio de resumos do Congresso de Inicia��o Cient�fica para este tipo de bolsa */
	@Temporal(TemporalType.DATE)
	@Column(name = "inicio_envio_resumo_congresso")
	private Date inicioEnvioResumoCongresso;
	
	/** Fim do per�odo de envio de resumos do Congresso de Inicia��o Cient�fica para este tipo de bolsa */
	@Temporal(TemporalType.DATE)
	@Column(name = "fim_envio_resumo_congresso")
	private Date fimEnvioResumoCongresso;
	
	/** �rg�o financiador do tipo de bolsa */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_entidade_financiadora")
	private EntidadeFinanciadora entidadeFinanciadora = new EntidadeFinanciadora();
	
	/** Dia Limite para que o discente entre na folha de pagamento do m�s */
	@Column(name = "dia_limite_indicacao")
	private Integer diaLimiteIndicacao = 1;

	/** Dia Limite para que o discente n�o entre na folha de pagamento do m�s */
	@Column(name = "dia_limite_finalizacao")
	private Integer diaLimiteFinalizacao = 1;

	/** Construtor padr�o */
	public TipoBolsaPesquisa(){

	}

	public TipoBolsaPesquisa(int id){
		this.id = id;
	}

	/** Respons�vel por retornar a chave prim�ria do Tipo de Bolsa Pesquisa */
	public int getId() {
		return id;
	}

	/** Respons�vel por setar a chave prim�ria do Tipo de Bolsa Pesquisa */
	public void setId(int id) {
		this.id = id;
	}

	/** Respons�vel por retornar a categoria do Tipo de Bolsa Pesquisa */
	public int getCategoria() {
		return categoria;
	}

	/** Respons�vel por setar a categoria do Tipo de Bolsa Pesquisa */
	public void setCategoria(int categoria) {
		this.categoria = categoria;
	}

	/** Respons�vel por retornar se o Tipo de Bolsa Pesquisa necessita de Relat�rio */
	public boolean isNecessitaRelatorio() {
		return necessitaRelatorio;
	}

	/** Respons�vel por setar a necessidade ou n�o de Relat�rio */
	public void setNecessitaRelatorio(boolean necessitaRelatorio) {
		this.necessitaRelatorio = necessitaRelatorio;
	}

	/** Respons�vel por retornar se o Tipo de Bolsa Pesquisa est� vinculado a Cota */
	public boolean isVinculadoCota() {
		return vinculadoCota;
	}

	/** Respons�vel por setar se o Tipo de Bolsa Pesquisa est� vinculado a cota */
	public void setVinculadoCota(boolean vinculadoCota) {
		this.vinculadoCota = vinculadoCota;
	}

	/** Respons�vel por retornar a descri��o do Tipo de Bolsa Pesquisa */
	public String getDescricao() {
		return descricao;
	}

	/** Respons�vel por setar a descri��o do Tipo de Bolsa Pesquisa */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/** Respons�vel por validar os campos da entidade Tipo de Bolsa Pesquisa */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		validateRequired(descricao, "Descri��o", erros);
		validateRequiredId(categoria, "Categoria", erros);
		validateRequired(entidadeFinanciadora, "�rg�o Financiador", erros);
		validaInicioFim(inicioEnvioRelatorioParcial, fimEnvioRelatorioParcial, "Per�odo de Envio de Relat�rios Parciais", erros);
		validaInicioFim(inicioEnvioRelatorioFinal, fimEnvioRelatorioFinal, "Per�odo de Envio de Relat�rios Finais", erros);
		validaInicioFim(inicioEnvioResumoCongresso, fimEnvioResumoCongresso, "Per�odo de Envio de Resumos para Congresso de Inicia��o Cient�fica", erros);
		return erros;
	}

	/** Respons�vel por retornar a descri��o Completa do Tipo de Bolsa Pesquisa */
	@Transient
	public String getDescricaoCompleta(){
		return getDescricao() + " - " + getDescricaoCategoria();
	}

	/** Respons�vel por retornar a descri��o Resumida do Tipo de Bolsa Pesquisa */
	@Transient
	public String getDescricaoResumida(){
		return getDescricao() + (categoria == 0 || getId() == A_DEFINIR ? "" : categoria == 1? " (IC)": " (IT)");
	}

	/** Respons�vel por retornar a descri��o da Categoria do Tipo de Bolsa Pesquisa */
	@Transient
	public String getDescricaoCategoria(){
		return CategoriaBolsaPesquisa.getDescricao(categoria);
	}

	/** Mapa com todos poss�veis status de uma inscri��o. */
	public static final Map<Integer, String> BOLSAS_RELATORIO_CNPQ = new HashMap<Integer, String>(){{
		put(PIBIC, "PIBIC");
		put(PIBIT, "PIBIT");
	}};

	/** Respons�vel por retornar a descri��o da Categoria do Tipo de Bolsa Pesquisa */
	public static String getDescricaoCategoria(int categoria){
		return CategoriaBolsaPesquisa.getDescricao(categoria);
	}

	/** Respons�vel por retornar um boleando que indica se a entidade est� em uso */
	public boolean isAtivo() {
		return ativo;
	}

	/** Respons�vel por setar um boleando que indica se a entidade est� em uso */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Respons�vel por retornar o tipo de Bolsa do SIPAC */
	public Integer getTipoBolsaSipac() {
		return tipoBolsaSipac;
	}

	/** Respons�vel por setar o tipo de Bolsa do SIPAC */
	public void setTipoBolsaSipac(Integer tipoBolsaSipac) {
		this.tipoBolsaSipac = tipoBolsaSipac;
	}
	
	/** Retorna os n�veis de ensino dos alunos que podem receber bolsa desse tipo. */
	public String getNiveisPermitidos() {
		return niveisPermitidos;
	}

	public void setNiveisPermitidos(String niveisPermitidos) {
		this.niveisPermitidos = niveisPermitidos;
	}

	public Date getInicioEnvioRelatorioParcial() {
		return inicioEnvioRelatorioParcial;
	}

	public void setInicioEnvioRelatorioParcial(Date inicioEnvioRelatorioParcial) {
		this.inicioEnvioRelatorioParcial = inicioEnvioRelatorioParcial;
	}

	public Date getFimEnvioRelatorioParcial() {
		return fimEnvioRelatorioParcial;
	}

	public void setFimEnvioRelatorioParcial(Date fimEnvioRelatorioParcial) {
		this.fimEnvioRelatorioParcial = fimEnvioRelatorioParcial;
	}

	public Date getInicioEnvioRelatorioFinal() {
		return inicioEnvioRelatorioFinal;
	}

	public void setInicioEnvioRelatorioFinal(Date inicioEnvioRelatorioFinal) {
		this.inicioEnvioRelatorioFinal = inicioEnvioRelatorioFinal;
	}

	public Date getFimEnvioRelatorioFinal() {
		return fimEnvioRelatorioFinal;
	}

	public void setFimEnvioRelatorioFinal(Date fimEnvioRelatorioFinal) {
		this.fimEnvioRelatorioFinal = fimEnvioRelatorioFinal;
	}

	public Date getInicioEnvioResumoCongresso() {
		return inicioEnvioResumoCongresso;
	}

	public void setInicioEnvioResumoCongresso(Date inicioEnvioResumoCongresso) {
		this.inicioEnvioResumoCongresso = inicioEnvioResumoCongresso;
	}

	public Date getFimEnvioResumoCongresso() {
		return fimEnvioResumoCongresso;
	}

	public void setFimEnvioResumoCongresso(Date fimEnvioResumoCongresso) {
		this.fimEnvioResumoCongresso = fimEnvioResumoCongresso;
	}

	public EntidadeFinanciadora getEntidadeFinanciadora() {
		return entidadeFinanciadora;
	}

	public void setEntidadeFinanciadora(EntidadeFinanciadora entidadeFinanciadora) {
		this.entidadeFinanciadora = entidadeFinanciadora;
	}

	public Integer getDiaLimiteIndicacao() {
		return diaLimiteIndicacao;
	}

	public void setDiaLimiteIndicacao(Integer diaLimiteIndicacao) {
		this.diaLimiteIndicacao = diaLimiteIndicacao;
	}

	public Integer getDiaLimiteFinalizacao() {
		return diaLimiteFinalizacao;
	}

	public void setDiaLimiteFinalizacao(Integer diaLimiteFinalizacao) {
		this.diaLimiteFinalizacao = diaLimiteFinalizacao;
	}
	
	public static int[] getBolsasNaoPermiteAcumular() {
		int [] bolsas = {PROPESQ, PROPESQ_INDUCAO, PROPESQ_IT, PIBIC, PIBIC_EM, REUNI_IC, REUNI_IT};
		return bolsas;
	}
}