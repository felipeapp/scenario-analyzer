package br.ufrn.sigaa.monitoria.dominio;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;

/*******************************************************************************
 * <p>
 * Classe que representa a avaliação do relatório final ou parcial do projeto de
 * monitoria.
 * </p>
 * <p>
 * Através da avaliação do relatório do projeto por membros da comissão de
 * monitoria ele pode ser renovado, cancelado ou ter algumas de suas bolsas
 * cortadas.
 * </p>
 * <p>
 * Uma avaliação de relatório de projeto é criada quando um relatório de projeto
 * é distribuído para um avaliador.
 * </p>
 * 
 * @author Victor Hugo
 * @author Ilueny Santos
 * 
 * 
 ******************************************************************************/
@Entity
@Table(name = "avaliacao_relatorio_projeto", schema = "monitoria")
public class AvaliacaoRelatorioProjeto implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_avaliacao_relatorio_projeto")
	private int id;

	@Column(name = "data_distribuicao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataDistribuicao;

	@Column(name = "data_avaliacao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataAvaliacao;

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_membro_comissao")
	private MembroComissao avaliador;

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_status_avaliacao", unique = false, nullable = false, insertable = true, updatable = true)
	private StatusAvaliacao statusAvaliacao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada_retirada_distribuicao")
	private RegistroEntrada registroEntradaRetiradaDistribuicao;

	/** quanto ao relatório do monitor */
	@Column(name = "monitor_conhece_projeto")
	private Character monitorConheceProjeto;

	@Column(name = "coerencia_objetivos_atividades")
	private Character coerenciaObjetivosAtividades;

	@Column(name = "monitor_envolvido_sid")
	private Character monitorEnvolvidoSid = 'S';

	@Column(name = "projeto_contribuiu_formacao_academica_monitor")
	private Character projetoContribuiuFormacaoAcademicaMonitor;

	/** relatório do projeto (coordenador) */
	@Column(name = "estrategias_possibilitam_alcance_objetivos")
	private Character estrategiasPossibilitamAlcanceObjetivos;

	@Column(name = "atividades_monitores_programa_monitoria")
	private Character atividadesMonitoresProgramaMonitoria;

	@Column(name = "participacao_membros_sid_satisfatoria")
	private Character participacaoMembrosSidSatisfatoria;

	@Column(name = "propostas_justificam_renovacao")
	private Character propostasJustificamRenovacao;

	private String parecer;

	@Column(name = "recomenda_renovacao")
	private boolean recomendaRenovacao = true;

	@Column(name = "mantem_quantidade_bolsas")
	private boolean mantemQuantidadeBolsas = true;

	/** se for reduzir bolsas tem que dizer quais bolsas quer retirar do projeto */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "avaliacaoRelatorioProjeto")
	private Set<RecomendacaoCorteBolsa> recomendacoesCorteBolsa = new HashSet<RecomendacaoCorteBolsa>();

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_relatorio_projeto", unique = false, nullable = false, insertable = true, updatable = true)
	private RelatorioProjetoMonitoria relatorioProjetoMonitoria;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;

	// construtor padrão
	public AvaliacaoRelatorioProjeto() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getParecer() {
		return parecer;
	}

	public void setParecer(String parecer) {
		this.parecer = parecer;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public ListaMensagens validate() {
		return null;
	}

	/**
	 * As atividades dos monitores estão de acordo com o que propõe o programa
	 * de monitoria?
	 * 
	 * @return S=SIM, N=NAO, P=PARCIALMENTE
	 */
	public Character getAtividadesMonitoresProgramaMonitoria() {
		return atividadesMonitoresProgramaMonitoria;
	}

	public void setAtividadesMonitoresProgramaMonitoria(
			Character atividadesMonitoresProgramaMonitoria) {
		this.atividadesMonitoresProgramaMonitoria = atividadesMonitoresProgramaMonitoria;
	}

	/**
	 * Há coerência entre os objetivos propostas no projeto e as atividades
	 * desenvolvidas pelo (s) monitor (es) ?
	 * 
	 * @return S=SIM, N=NAO, P=PARCIALMENTE
	 */
	public Character getCoerenciaObjetivosAtividades() {
		return coerenciaObjetivosAtividades;
	}

	public void setCoerenciaObjetivosAtividades(
			Character coerenciaObjetivosAtividades) {
		this.coerenciaObjetivosAtividades = coerenciaObjetivosAtividades;
	}

	/**
	 * As estratégias traçadas pelo projeto possibilitam o alcance dos
	 * objetivos?
	 * 
	 * @return S=SIM, N=NAO, P=PARCIALMENTE
	 */
	public Character getEstrategiasPossibilitamAlcanceObjetivos() {
		return estrategiasPossibilitamAlcanceObjetivos;
	}

	public void setEstrategiasPossibilitamAlcanceObjetivos(
			Character estrategiasPossibilitamAlcanceObjetivos) {
		this.estrategiasPossibilitamAlcanceObjetivos = estrategiasPossibilitamAlcanceObjetivos;
	}

	/**
	 * Faz parte do parecer do avaliador
	 * 
	 * @return
	 */
	public boolean isMantemQuantidadeBolsas() {
		return mantemQuantidadeBolsas;
	}

	public void setMantemQuantidadeBolsas(boolean mantemQuantidadeBolsas) {
		this.mantemQuantidadeBolsas = mantemQuantidadeBolsas;
	}

	/**
	 * 
	 * O monitor demonstra conhecer o projeto ao qual está vinculado?
	 * 
	 * @return S=SIM, N=NAO, P=PARCIALMENTE
	 */
	public Character getMonitorConheceProjeto() {
		return monitorConheceProjeto;
	}

	public void setMonitorConheceProjeto(Character monitorConheceProjeto) {
		this.monitorConheceProjeto = monitorConheceProjeto;
	}

	/**
	 * Houve envolvimento do(s) monitor(es) no Seminário de Iniciação à Docência (
	 * SID ), ou ausência ?
	 * 
	 * @return S=SIM, N=NAO, P=PARCIALMENTE
	 */
	public Character getMonitorEnvolvidoSid() {
		return monitorEnvolvidoSid;
	}

	public void setMonitorEnvolvidoSid(Character monitorEnvolvidoSid) {
		this.monitorEnvolvidoSid = monitorEnvolvidoSid;
	}

	/**
	 * A participação dos membros do projeto no SID foi satisfatória?
	 * 
	 * @return S=SIM, N=NAO, P=PARCIALMENTE
	 */
	public Character getParticipacaoMembrosSidSatisfatoria() {
		return participacaoMembrosSidSatisfatoria;
	}

	public void setParticipacaoMembrosSidSatisfatoria(
			Character participacaoMembrosSidSatisfatoria) {
		this.participacaoMembrosSidSatisfatoria = participacaoMembrosSidSatisfatoria;
	}

	/**
	 * O projeto de monitoria propiciou a participação efetiva do monitor,
	 * contribuindo para a sua formação acadêmica?
	 * 
	 * @return S=SIM, N=NAO, P=PARCIALMENTE
	 */
	public Character getProjetoContribuiuFormacaoAcademicaMonitor() {
		return projetoContribuiuFormacaoAcademicaMonitor;
	}

	public void setProjetoContribuiuFormacaoAcademicaMonitor(
			Character projetoContribuiuFormacaoAcademicaMonitor) {
		this.projetoContribuiuFormacaoAcademicaMonitor = projetoContribuiuFormacaoAcademicaMonitor;
	}

	/**
	 * As perspectivas propostas para o aprimoramento do projeto justificam a
	 * sua renovação?
	 * 
	 * @return S=SIM, N=NAO, P=PARCIALMENTE
	 */
	public Character getPropostasJustificamRenovacao() {
		return propostasJustificamRenovacao;
	}

	public void setPropostasJustificamRenovacao(
			Character propostasJustificamRenovacao) {
		this.propostasJustificamRenovacao = propostasJustificamRenovacao;
	}

	/**
	 * Lista de recomendações de cortes de cortes de bolsas feitas pelo
	 * avaliador.
	 * 
	 * @return
	 */
	public Set<RecomendacaoCorteBolsa> getRecomendacoesCorteBolsa() {
		return recomendacoesCorteBolsa;
	}

	public void setRecomendacoesCorteBolsa(
			Set<RecomendacaoCorteBolsa> recomendacoesCorteBolsa) {
		this.recomendacoesCorteBolsa = recomendacoesCorteBolsa;
	}

	/**
	 * Informa se o projeto, na visão do avaliador, deve ser renovado ou não.
	 * 
	 * @return
	 */
	public boolean isRecomendaRenovacao() {
		return recomendaRenovacao;
	}

	public void setRecomendaRenovacao(boolean recomendaRenovacao) {
		this.recomendaRenovacao = recomendaRenovacao;
	}

	public MembroComissao getAvaliador() {
		return avaliador;
	}

	public void setAvaliador(MembroComissao avaliador) {
		this.avaliador = avaliador;
	}

	public Date getDataAvaliacao() {
		return dataAvaliacao;
	}

	public void setDataAvaliacao(Date dataAvaliacao) {
		this.dataAvaliacao = dataAvaliacao;
	}

	public Date getDataDistribuicao() {
		return dataDistribuicao;
	}

	public void setDataDistribuicao(Date dataDistribuicao) {
		this.dataDistribuicao = dataDistribuicao;
	}

	public RegistroEntrada getRegistroEntradaRetiradaDistribuicao() {
		return registroEntradaRetiradaDistribuicao;
	}

	public void setRegistroEntradaRetiradaDistribuicao(
			RegistroEntrada registroEntradaRetiradaDistribuicao) {
		this.registroEntradaRetiradaDistribuicao = registroEntradaRetiradaDistribuicao;
	}

	public StatusAvaliacao getStatusAvaliacao() {
		return statusAvaliacao;
	}

	public void setStatusAvaliacao(StatusAvaliacao statusAvaliacao) {
		this.statusAvaliacao = statusAvaliacao;
	}

	/**
	 * O relatório do projeto no qual o avaliador usou como base da avaliação
	 * 
	 * @return
	 */
	public RelatorioProjetoMonitoria getRelatorioProjetoMonitoria() {
		return relatorioProjetoMonitoria;
	}

	public void setRelatorioProjetoMonitoria(
			RelatorioProjetoMonitoria relatorioProjetoMonitoria) {
		this.relatorioProjetoMonitoria = relatorioProjetoMonitoria;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "avaliador",
				"relatorioProjetoMonitoria");
	}

	/**
	 * Retorna true se a avaliação ainda não foi realizada
	 * 
	 * dataAvaliacao != null && statusAvaliacao != Cancelada
	 * 
	 * @return
	 */
	public boolean isAvaliacaoEmAberto() {
		if (this.statusAvaliacao != null) {
			return (((this.statusAvaliacao.getId() == StatusAvaliacao.AGUARDANDO_AVALIACAO) 
				|| (this.statusAvaliacao.getId() == StatusAvaliacao.AVALIACAO_EM_ANDAMENTO)) 
				&& (this.dataAvaliacao == null));
		} else {
			return false;
		}
	}

}
