package br.ufrn.sigaa.monitoria.dominio;

// Generated 09/10/2006 10:44:38 by Hibernate Tools 3.1.0.beta5

import java.util.Collection;
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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/*******************************************************************************
 * <p>
 * Representa o relatório do projeto de monitoria. Pode ser Parcial ou Final.
 * </p>
 * 
 * 
 * @author Ilueny Santos
 * @autor Victor Hugo
 ******************************************************************************/
@Entity
@Table(name = "relatorio_projeto", schema = "monitoria")
public class RelatorioProjetoMonitoria implements Validatable {

	// Fields

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_relatorio_projeto", unique = true, nullable = false)
	private int id;

	@Column(name = "objetivos_alcancados")
	private String objetivosAlcancados;

	@Column(name = "atribuicoes_executadas")
	private String atribuicoesExecutadas;

	/**
	 * valores: 1- Sim 2- Não 3- Parcialmente
	 */
	@Column(name = "monitores_cumpriram_exigencias")
	private int monitoresCumpriramExigencias = 1;

	/**
	 * justificativa caso o item acima sejam 2 (Não) ou 3 (Parcialmente)
	 */
	@Column(name = "monitores_cumpriram_exigencias_justificativa")
	private String monitoresCumpriramExigenciasJustificativa;

	/**
	 * valores: 1- Sim 2- Não 3- Ruim ou seria?! Bom, Regular, Ruim?
	 */
	@Column(name = "participacao_membros_sid")
	private int participacaoMembrosSid = 1;

	/**
	 * justificativa caso o item acima sejam 2 (Não) ou 3 (Ruim)
	 */
	@Column(name = "participacao_membros_sid_justificativa")
	private String participacaoMembrosSidJustificativa;

	@Column(name = "articulacao_politico_pedagogico")
	private String articulacaoPoliticoPedagogico;

	@Column(name = "estimulo_iniciacao_docencia")
	private String estimuloIniciacaoDocencia;

	@Column(name = "funcao_monitor")
	private String funcaoMonitor;

	@Column(name = "integracao_entre_areas")
	private String integracaoEntreAreas;

	@Column(name = "carater_inovador")
	private String caraterInovador;

	@Column(name = "sugestoes")
	private String sugestoes;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_projeto_monitoria")
	private ProjetoEnsino projetoEnsino = new ProjetoEnsino();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_relatorio_monitoria")
	private TipoRelatorioMonitoria tipoRelatorio = new TipoRelatorioMonitoria();

	@OneToMany(cascade = {}, mappedBy = "relatorioProjetoMonitoria")
	@OrderBy(value = "avaliador")
	private Set<AvaliacaoRelatorioProjeto> avaliacoes = new HashSet<AvaliacaoRelatorioProjeto>();

	@Column(name = "deseja_renovar_projeto")
	private boolean desejaRenovarProjeto;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_status_relatorio")
	private StatusRelatorio status = new StatusRelatorio();

	@Column(name = "ativo")
	private boolean ativo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_envio")
	private Date dataEnvio;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada_devolucao")
	private RegistroEntrada registroEntradaDevolucaoReedicao;

	@Transient
	private boolean selecionado;
	
	// Constructors

	/** default constructor */
	public RelatorioProjetoMonitoria() {
	}

	/** minimal constructor */
	public RelatorioProjetoMonitoria(int id) {
		this.id = id;
	}

	// Property accessors
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getArticulacaoPoliticoPedagogico() {
		return articulacaoPoliticoPedagogico;
	}

	public void setArticulacaoPoliticoPedagogico(
			String articulacaoPoliticoPedagogico) {
		this.articulacaoPoliticoPedagogico = articulacaoPoliticoPedagogico;
	}

	public String getAtribuicoesExecutadas() {
		return atribuicoesExecutadas;
	}

	public void setAtribuicoesExecutadas(String atribuicoesExecutadas) {
		this.atribuicoesExecutadas = atribuicoesExecutadas;
	}

	public String getCaraterInovador() {
		return caraterInovador;
	}

	public void setCaraterInovador(String caraterInovador) {
		this.caraterInovador = caraterInovador;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public String getEstimuloIniciacaoDocencia() {
		return estimuloIniciacaoDocencia;
	}

	public void setEstimuloIniciacaoDocencia(String estimuloIniciacaoDocencia) {
		this.estimuloIniciacaoDocencia = estimuloIniciacaoDocencia;
	}

	public String getFuncaoMonitor() {
		return funcaoMonitor;
	}

	public void setFuncaoMonitor(String funcaoMonitor) {
		this.funcaoMonitor = funcaoMonitor;
	}

	public String getIntegracaoEntreAreas() {
		return integracaoEntreAreas;
	}

	public void setIntegracaoEntreAreas(String integracaoEntreAreas) {
		this.integracaoEntreAreas = integracaoEntreAreas;
	}

	public int getMonitoresCumpriramExigencias() {
		return monitoresCumpriramExigencias;
	}

	public void setMonitoresCumpriramExigencias(int monitoresCumpriramExigencias) {
		this.monitoresCumpriramExigencias = monitoresCumpriramExigencias;
	}

	public String getMonitoresCumpriramExigenciasJustificativa() {
		return monitoresCumpriramExigenciasJustificativa;
	}

	public void setMonitoresCumpriramExigenciasJustificativa(
			String monitoresCumpriramExigenciasJustificativa) {
		this.monitoresCumpriramExigenciasJustificativa = monitoresCumpriramExigenciasJustificativa;
	}

	public String getObjetivosAlcancados() {
		return objetivosAlcancados;
	}

	public void setObjetivosAlcancados(String objetivosAlcancados) {
		this.objetivosAlcancados = objetivosAlcancados;
	}

	public int getParticipacaoMembrosSid() {
		return participacaoMembrosSid;
	}

	public void setParticipacaoMembrosSid(int participacaoMembrosSid) {
		this.participacaoMembrosSid = participacaoMembrosSid;
	}

	public String getParticipacaoMembrosSidJustificativa() {
		return participacaoMembrosSidJustificativa;
	}

	public void setParticipacaoMembrosSidJustificativa(
			String participacaoMembrosSidJustificativa) {
		this.participacaoMembrosSidJustificativa = participacaoMembrosSidJustificativa;
	}

	public ProjetoEnsino getProjetoEnsino() {
		return projetoEnsino;
	}

	public void setProjetoEnsino(ProjetoEnsino projetoEnsino) {
		this.projetoEnsino = projetoEnsino;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public String getSugestoes() {
		return sugestoes;
	}

	public void setSugestoes(String sugestoes) {
		this.sugestoes = sugestoes;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		// 1 = Sim, 2 = Não, 3 = Parcialmente
		if (monitoresCumpriramExigencias > 1) { 
			ValidatorUtil.validateRequired(monitoresCumpriramExigenciasJustificativa, "Monitores Cumpriram Exigências: Justificativa", lista);			
		}
		// 1 = Sim, 2 = Regular, 3 = Ruim
		if (participacaoMembrosSid > 1) {
			ValidatorUtil.validateRequired(participacaoMembrosSidJustificativa, "Participação Membros Sid: Justificativa", lista);
		}

		ValidatorUtil.validateRequired(objetivosAlcancados, "Objetivos Alcancados", lista);
		ValidatorUtil.validateRequired(atribuicoesExecutadas, "Atribuições Executadas Pelos Monitores", lista);
		ValidatorUtil.validateRequired(monitoresCumpriramExigencias, "Monitores Cumpriram Exigências", lista);
		ValidatorUtil.validateRequired(participacaoMembrosSid,"Participacao Membros Sid", lista);
		ValidatorUtil.validateRequired(articulacaoPoliticoPedagogico, "Articulação Político Pedagógica", lista);
		ValidatorUtil.validateRequired(estimuloIniciacaoDocencia, "Estímulo Iniciação Docência", lista);
		ValidatorUtil.validateRequired(funcaoMonitor, "Função do Monitor", lista);
		ValidatorUtil.validateRequired(integracaoEntreAreas, "Integração Entre Áreas", lista);
		ValidatorUtil.validateRequired(caraterInovador, "Carater Inovador", lista);
		ValidatorUtil.validateRequired(sugestoes, "Sugestões", lista);
		ValidatorUtil.validateRequiredId(projetoEnsino.getId(),	"Projeto de monitoria", lista);
		ValidatorUtil.validateRequiredId(tipoRelatorio.getId(), "Tipo de Relatório", lista);
		ValidatorUtil.validateRequired(registroEntrada, "Registro de Entrada", lista);
		return lista;
	}

	public TipoRelatorioMonitoria getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(TipoRelatorioMonitoria tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public Set<AvaliacaoRelatorioProjeto> getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(Set<AvaliacaoRelatorioProjeto> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}

	public StatusRelatorio getStatus() {
		return status;
	}

	public void setStatus(StatusRelatorio status) {
		this.status = status;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Date getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	/**
	 * Adiciona uma avaliação ao relatório.
	 * 
	 * @param obj
	 * @return true se adicionado com sucesso.
	 */
	public boolean addAvaliacaoRelatorioProjeto(AvaliacaoRelatorioProjeto obj) {
		obj.setRelatorioProjetoMonitoria(this);
		return avaliacoes.add(obj);
	}

	/**
	 * Remove uma avaliação do relatório
	 * 
	 * @param obj
	 * @return true se removido com sucesso.
	 */
	public boolean removeAvaliacaoRelatorioProjeto(AvaliacaoRelatorioProjeto obj) {
		obj.setRelatorioProjetoMonitoria(null);
		return avaliacoes.remove(obj);
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	public boolean isDesejaRenovarProjeto() {
		return desejaRenovarProjeto;
	}

	public void setDesejaRenovarProjeto(boolean desejaRenovarProjeto) {
		this.desejaRenovarProjeto = desejaRenovarProjeto;
	}

	/**
	 * Retorna true se o relatório puder ser distribuído para avaliação da
	 * comissão de monitoria
	 * 
	 */
	public boolean isPermitidoDistribuirComissaoMonitoria() {
		return ((getProjetoEnsino().getSituacaoProjeto().getId() == TipoSituacaoProjeto.MON_AGUARDANDO_DISTRIBUICAO_DO_RELATORIO) 
				|| (getProjetoEnsino().getSituacaoProjeto().getId() == TipoSituacaoProjeto.MON_AGUARDANDO_AVALIACAO));
	}

	/**
	 * Só pode alterar o relatório enquanto ele não for enviado para prograd
	 * 
	 * @return
	 */
	public boolean isPermitidoAlterar() {
		return getDataEnvio() == null;
	}

	/**
	 * Só pode devolver o relatório enquanto ele não for distribuído para
	 * avaliação pela comissão de monitoria
	 * 
	 * @return
	 */
	public boolean isPermitidoDevolverCoordenador() {

		if ((getStatus() != null) && (getStatus().getId() != 0)) {
			return ((getStatus().getId() == StatusRelatorio.AGUARDANDO_DISTRIBUICAO) && (getDataEnvio() != null));
		}
		return false;

	}

	/**
	 * retorna total de membros da comissão avaliando este relatório (com
	 * avaliações ativas)
	 * 
	 * @return
	 */
	public int getTotalAvaliadoresRelatorioAtivos() {
		int result = 0;
		for (AvaliacaoRelatorioProjeto a : avaliacoes) {
			if (a.getStatusAvaliacao().getId() != StatusAvaliacao.AVALIACAO_CANCELADA) {
				result++;
			}
		}
		return result;
	}

	/**
	 * Informa o registro de entrada do operador do sistema que realizou a
	 * devolução do relatório para coordenador reeditá-lo
	 * 
	 * @return
	 */
	public RegistroEntrada getRegistroEntradaDevolucaoReedicao() {
		return registroEntradaDevolucaoReedicao;
	}

	public void setRegistroEntradaDevolucaoReedicao(
			RegistroEntrada registroEntradaDevolucaoReedicao) {
		this.registroEntradaDevolucaoReedicao = registroEntradaDevolucaoReedicao;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

}