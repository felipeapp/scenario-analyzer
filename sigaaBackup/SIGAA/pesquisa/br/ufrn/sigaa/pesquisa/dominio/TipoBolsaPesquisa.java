/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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

	/** Tipo temporário enquanto o docente não recebe a cota */
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
	/** Mapa com os tipos de bolsas de voluntários */
	private static Map<Integer, String> tiposVoluntarios = new TreeMap<Integer, String>();

	static {
		tipos.put(A_DEFINIR, "A DEFINIR");
		tipos.put(PROPESQ, "PROPESQ");
		tipos.put(PROPESQ_INDUCAO, "PROPESQ - INDUÇÃO");
		tipos.put(PIBIC, "PIBIC");
		tipos.put(BALCAO, "BALCÃO");
		tipos.put(VOLUNTARIO, "VOLUNTÁRIO (IC)");
		tipos.put(VOLUNTARIO_IT, "VOLUNTÁRIO (IT)");
		tipos.put(OUTROS, "OUTROS");
		tipos.put(PROPESQ_IT, "PROPESQ (IT)");
		tipos.put(PIBIT, "PIBIT");
		tipos.put(PIBIC_EM, "PIBIC (EM)");
		
		tiposVoluntarios.put(VOLUNTARIO, "VOLUNTÁRIO (IC)");
		tiposVoluntarios.put(VOLUNTARIO_IT, "VOLUNTÁRIO (IT)");		
	}

	/** Responsável por retornar a Descrição do tipo de Bolsa de Pesquisa */
	public static String getDescricao(int tipo) {
		return tipos.get(tipo);
	}

	/** Responsável por retornar os tipos de Bolsa de Pesquisa */
	public static Map<Integer, String> getTipos() {
		return tipos;
	}
	
	/** Responsável por retornar os tipos de Voluntários */
	public static Map<Integer, String> getTiposVoluntarios() {
		return tiposVoluntarios;
	}

	/** Responsável por retornar se o Tipo de Bolsa e Externa ou não */
	public static boolean isExterna(int tipo) {
		return tipo == BALCAO || tipo == OUTROS;
	}

	/** Responsável por retornar se o Tipo de Bolsa e Voluntário ou não */
	public static boolean isVoluntario(int tipo) {
		return tipo == VOLUNTARIO || tipo == VOLUNTARIO_IT;
	}
	
	/** Inicio da definição dos atributos e seus mapeamentos */

	/** Chave primária */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_tipo_bolsa", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Categoria que o Tipo de Bolsa de Pesquisa se enquadra */
	private int categoria;

	/** Indica se o tipo de bolsa requer o envio de relatórios parciais e finais ou não */
	@Column(name = "necessita_relatorio")
	private boolean necessitaRelatorio;

	/** Indica se o tipo de bolsa está vinculado a um período de cota ou não */
	@Column(name = "vinculado_cota")
	private boolean vinculadoCota;

	/** Descrição do Tipo de Bolsa Pesquisa */
	private String descricao;

	/** Indica se o Tipo de Bolsa Pesquisa está em uso */
	private boolean ativo = true;
	
	/** Armazena o tipo de bolsa que está associado no SIPAC */
	@Column(name = "id_tipo_bolsa_sipac")
	private Integer tipoBolsaSipac;
	
	/** Indica quais níveis este tipo de bolsa é indicado. */
	@Column (name = "niveis_permitidos")
	private String niveisPermitidos;
	
	/** Início do período de envio de relatórios parciais para este tipo de bolsa */
	@Temporal(TemporalType.DATE)
	@Column(name = "inicio_envio_relatorio_parcial")
	private Date inicioEnvioRelatorioParcial;
	
	/** Fim do período de envio de relatórios parciais para este tipo de bolsa */
	@Temporal(TemporalType.DATE)
	@Column(name = "fim_envio_relatorio_parcial")
	private Date fimEnvioRelatorioParcial;
	
	/** Início do período de envio de relatórios finais para este tipo de bolsa */
	@Temporal(TemporalType.DATE)
	@Column(name = "inicio_envio_relatorio_final")
	private Date inicioEnvioRelatorioFinal;
	
	/** Fim do período de envio de relatórios finais para este tipo de bolsa */
	@Temporal(TemporalType.DATE)
	@Column(name = "fim_envio_relatorio_final")
	private Date fimEnvioRelatorioFinal;
	
	/** Início do período de envio de resumos do Congresso de Iniciação Científica para este tipo de bolsa */
	@Temporal(TemporalType.DATE)
	@Column(name = "inicio_envio_resumo_congresso")
	private Date inicioEnvioResumoCongresso;
	
	/** Fim do período de envio de resumos do Congresso de Iniciação Científica para este tipo de bolsa */
	@Temporal(TemporalType.DATE)
	@Column(name = "fim_envio_resumo_congresso")
	private Date fimEnvioResumoCongresso;
	
	/** Órgão financiador do tipo de bolsa */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_entidade_financiadora")
	private EntidadeFinanciadora entidadeFinanciadora = new EntidadeFinanciadora();
	
	/** Dia Limite para que o discente entre na folha de pagamento do mês */
	@Column(name = "dia_limite_indicacao")
	private Integer diaLimiteIndicacao = 1;

	/** Dia Limite para que o discente não entre na folha de pagamento do mês */
	@Column(name = "dia_limite_finalizacao")
	private Integer diaLimiteFinalizacao = 1;

	/** Construtor padrão */
	public TipoBolsaPesquisa(){

	}

	public TipoBolsaPesquisa(int id){
		this.id = id;
	}

	/** Responsável por retornar a chave primária do Tipo de Bolsa Pesquisa */
	public int getId() {
		return id;
	}

	/** Responsável por setar a chave primária do Tipo de Bolsa Pesquisa */
	public void setId(int id) {
		this.id = id;
	}

	/** Responsável por retornar a categoria do Tipo de Bolsa Pesquisa */
	public int getCategoria() {
		return categoria;
	}

	/** Responsável por setar a categoria do Tipo de Bolsa Pesquisa */
	public void setCategoria(int categoria) {
		this.categoria = categoria;
	}

	/** Responsável por retornar se o Tipo de Bolsa Pesquisa necessita de Relatório */
	public boolean isNecessitaRelatorio() {
		return necessitaRelatorio;
	}

	/** Responsável por setar a necessidade ou não de Relatório */
	public void setNecessitaRelatorio(boolean necessitaRelatorio) {
		this.necessitaRelatorio = necessitaRelatorio;
	}

	/** Responsável por retornar se o Tipo de Bolsa Pesquisa está vinculado a Cota */
	public boolean isVinculadoCota() {
		return vinculadoCota;
	}

	/** Responsável por setar se o Tipo de Bolsa Pesquisa está vinculado a cota */
	public void setVinculadoCota(boolean vinculadoCota) {
		this.vinculadoCota = vinculadoCota;
	}

	/** Responsável por retornar a descrição do Tipo de Bolsa Pesquisa */
	public String getDescricao() {
		return descricao;
	}

	/** Responsável por setar a descrição do Tipo de Bolsa Pesquisa */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/** Responsável por validar os campos da entidade Tipo de Bolsa Pesquisa */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		validateRequired(descricao, "Descrição", erros);
		validateRequiredId(categoria, "Categoria", erros);
		validateRequired(entidadeFinanciadora, "Órgão Financiador", erros);
		validaInicioFim(inicioEnvioRelatorioParcial, fimEnvioRelatorioParcial, "Período de Envio de Relatórios Parciais", erros);
		validaInicioFim(inicioEnvioRelatorioFinal, fimEnvioRelatorioFinal, "Período de Envio de Relatórios Finais", erros);
		validaInicioFim(inicioEnvioResumoCongresso, fimEnvioResumoCongresso, "Período de Envio de Resumos para Congresso de Iniciação Científica", erros);
		return erros;
	}

	/** Responsável por retornar a descrição Completa do Tipo de Bolsa Pesquisa */
	@Transient
	public String getDescricaoCompleta(){
		return getDescricao() + " - " + getDescricaoCategoria();
	}

	/** Responsável por retornar a descrição Resumida do Tipo de Bolsa Pesquisa */
	@Transient
	public String getDescricaoResumida(){
		return getDescricao() + (categoria == 0 || getId() == A_DEFINIR ? "" : categoria == 1? " (IC)": " (IT)");
	}

	/** Responsável por retornar a descrição da Categoria do Tipo de Bolsa Pesquisa */
	@Transient
	public String getDescricaoCategoria(){
		return CategoriaBolsaPesquisa.getDescricao(categoria);
	}

	/** Mapa com todos possíveis status de uma inscrição. */
	public static final Map<Integer, String> BOLSAS_RELATORIO_CNPQ = new HashMap<Integer, String>(){{
		put(PIBIC, "PIBIC");
		put(PIBIT, "PIBIT");
	}};

	/** Responsável por retornar a descrição da Categoria do Tipo de Bolsa Pesquisa */
	public static String getDescricaoCategoria(int categoria){
		return CategoriaBolsaPesquisa.getDescricao(categoria);
	}

	/** Responsável por retornar um boleando que indica se a entidade está em uso */
	public boolean isAtivo() {
		return ativo;
	}

	/** Responsável por setar um boleando que indica se a entidade está em uso */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Responsável por retornar o tipo de Bolsa do SIPAC */
	public Integer getTipoBolsaSipac() {
		return tipoBolsaSipac;
	}

	/** Responsável por setar o tipo de Bolsa do SIPAC */
	public void setTipoBolsaSipac(Integer tipoBolsaSipac) {
		this.tipoBolsaSipac = tipoBolsaSipac;
	}
	
	/** Retorna os níveis de ensino dos alunos que podem receber bolsa desse tipo. */
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