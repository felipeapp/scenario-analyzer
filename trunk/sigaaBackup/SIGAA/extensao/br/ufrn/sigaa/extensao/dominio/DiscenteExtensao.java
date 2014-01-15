/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/12/2007
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioBolsistaExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.TipoRelatorioExtensao;
import br.ufrn.sigaa.pessoa.dominio.Banco;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente;

/*******************************************************************************
 * <p>
 * Representa um discente que faz parte da equipe executora da ação de extensão.
 * Todos os discentes de extensão devem possuir um plano de trabalho que será
 * informado pelo coordenação da ação de extensão no ato do cadastramento do
 * discente na ação. <br>
 * Uma atenção especial deve ser dada a classe MembroProjeto. Um objeto desta
 * classe pode ser da categoria DISCENTE. Vale lembrar que os discentes
 * informados durante o cadastro da ação são cadastrados em MembroProjeto na
 * categoria DISCENTE e não representam discentes ativos da ação até que sejam
 * cadastrados em DiscenteExtensao com o tipo de vinculo correspondente.
 * </p>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/

@Entity
@Table(schema = "extensao", name = "discente_extensao")
public class DiscenteExtensao implements Validatable {

	/** Identificador único entre objetos desta classe. */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_discente_extensao", unique = true, nullable = false)
	private int id;

	/** Data onde o discente foi cadastrado. */
	@CriadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	private Date dataCadastro;

	/** Data onde o discente foi cadastrado para iniciar no plano de trabalho. */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio")
	private Date dataInicio;

	/** Data onde o discente saiu do plano de trabalho. */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim")
	private Date dataFim;

	/** Indica se o discente esta ativo no sistema. */
	@Column(name = "ativo")
	private boolean ativo = true;

	/** Descrição textual informando o motivo da substituição do discente no plano de trabalho. */
	@Column(name = "motivo_substituicao")
	private String motivoSubstituicao;

	/** Justificativa para a escolha na seleção para o plano de trabalho. */ 
	@Column(name = "justificativa_selecao")
	private String justificativa;	

	/** Discente de extensão. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_discente")
    private Discente discente = new Discente();

    /** Tipo de vínculo do discente na ação ao qual o mesmo pertence. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_vinculo_discente")
	private TipoVinculoDiscente tipoVinculo = new TipoVinculoDiscente();

	/** Discente Extensão que foi substituido no plano de trabalho. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente_extensao_anterior")
	private DiscenteExtensao discenteExtensaoAnterior;

	/** Grava quem inseriu o discente no plano de trabalho. */
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada = new RegistroEntrada();

	/** Plano de trabalho no qual o discente faz parte. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_plano_trabalho_extensao")
	private PlanoTrabalhoExtensao planoTrabalhoExtensao = new PlanoTrabalhoExtensao();

	/** Situação do discente de extensão. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_situacao_discente_extensao")
	private TipoSituacaoDiscenteExtensao situacaoDiscenteExtensao = new TipoSituacaoDiscenteExtensao();

	/** Atividade de Extensão no qual esta inserido o discente de extensão. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_atividade")
	private AtividadeExtensao atividade;

	/** Histórico das situações do discentes perante a ação de extensão. */ 
	@OneToMany(mappedBy = "discenteExtensao")
	private Collection<HistoricoSituacaoDiscenteExtensao> historicoSituacao;

	/** Relatórios cadastrados pelo discente de extensão. */
	@OneToMany(mappedBy = "discenteExtensao")
	private Collection<RelatorioBolsistaExtensao> relatorios;


	/**  Dados bancários dos alunos. São informados depois da validação da  seleção pela prograd. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_banco")
	private Banco banco = null;

	/** Número da agência bancária do discente de extensão. */
	@Column(name = "num_agencia")
	private String agencia;

	/** Número da conta bancária do discente de extensão. */
	@Column(name = "num_conta")
	private String conta;
	
	/** Número da operação da conta bancária do discente de extensão. */
	@Column(name = "num_operacao")
	private String operacao;


	/** Utilizado na view. */
	@Transient
	private boolean selecionado;

	/** Creates a new instance of Objetivo. */
	public DiscenteExtensao() {
	}

	public DiscenteExtensao(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(discente, "Discente", lista);
		ValidatorUtil.validateRequired(situacaoDiscenteExtensao, "Situação do discente", lista);
		ValidatorUtil.validateRequired(tipoVinculo, "Tipo de Vínculo", lista);
		ValidatorUtil.validateRequired(atividade, "Ação de Extensão", lista);
		return lista;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "discente.id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, discente.getId());
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public PlanoTrabalhoExtensao getPlanoTrabalhoExtensao() {
		return planoTrabalhoExtensao;
	}

	public void setPlanoTrabalhoExtensao(PlanoTrabalhoExtensao plano) {
		this.planoTrabalhoExtensao = plano;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public TipoSituacaoDiscenteExtensao getSituacaoDiscenteExtensao() {
		return situacaoDiscenteExtensao;
	}

	public void setSituacaoDiscenteExtensao(
			TipoSituacaoDiscenteExtensao situacaoDiscenteExtensao) {
		this.situacaoDiscenteExtensao = situacaoDiscenteExtensao;
	}

	public AtividadeExtensao getAtividade() {
		return atividade;
	}

	public void setAtividade(AtividadeExtensao atividade) {
		this.atividade = atividade;
	}

	public Banco getBanco() {
		return banco;
	}

	public void setBanco(Banco banco) {
		this.banco = banco;
	}

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public String getConta() {
		return conta;
	}

	public void setConta(String conta) {
		this.conta = conta;
	}

	public String getOperacao() {
		return operacao;
	}

	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}

	public String getMotivoSubstituicao() {
		return motivoSubstituicao;
	}

	public void setMotivoSubstituicao(String motivoSubstituicao) {
		this.motivoSubstituicao = motivoSubstituicao;
	}

	public DiscenteExtensao getDiscenteExtensaoAnterior() {
		return discenteExtensaoAnterior;
	}

	public void setDiscenteExtensaoAnterior(DiscenteExtensao discente) {
		this.discenteExtensaoAnterior = discente;
	}

	public Collection<HistoricoSituacaoDiscenteExtensao> getHistoricoSituacao() {
		return historicoSituacao;
	}

	public void setHistoricoSituacao(
			Collection<HistoricoSituacaoDiscenteExtensao> historicoSituacao) {
		this.historicoSituacao = historicoSituacao;
	}

	public void setTipoVinculo(TipoVinculoDiscente tipoVinculo) {
		this.tipoVinculo = tipoVinculo;
	}

	/**
	 * Representa o tipo de vínculo do aluno com a ação de extensão. valores
	 * possíveis: -1 = SEM VÍNCULO, 1 = VOLUNTÁRIO, 2 = BOLSISTA, 3 = EM
	 * ATIVIDADE CURRICULAR
	 */
	public TipoVinculoDiscente getTipoVinculo() {
		return tipoVinculo;
	}

	/** Retorna lista de relatórios ativos do discente. */
	public Collection<RelatorioBolsistaExtensao> getRelatorios() {
		if(relatorios != null)
			for (java.util.Iterator<RelatorioBolsistaExtensao> it = relatorios.iterator(); it.hasNext();) {
				if(!it.next().isAtivo())
					it.remove();				
			}
		return relatorios;
	}

	public void setRelatorios(Collection<RelatorioBolsistaExtensao> relatorios) {
		this.relatorios = relatorios;
	}

	/**
	 * Verifica se o discente enviou o relatório final
	 * utilizado em: planoTrabalhoValidator, validação na inclusão do discente na ação
	 * 
	 * @return
	 */
	public boolean isEnviouRelatorioFinal() {
		for (RelatorioBolsistaExtensao rel : this.getRelatorios()) {
			if(rel.getTipoRelatorio().getId() == TipoRelatorioExtensao.RELATORIO_FINAL)
				return true;
		}
		return false;	
	}

	/**
	 * Verifica se o discente enviou o relatório parcial
	 * utilizado em: planoTrabalhoValidator, validação na inclusão do discente na ação
	 * 
	 * @return
	 */
	public boolean isEnviouRelatorioParcial() {
		for (RelatorioBolsistaExtensao rel : this.getRelatorios()) {
			if(rel.getTipoRelatorio().getId() == TipoRelatorioExtensao.RELATORIO_PARCIAL)
				return true;
		}
		return false;	
	}

	public boolean isBolsista() {
		return isBolsistaExterno() || isBolsistaInterno();
	}

	public boolean isBolsistaInterno() {
		return (this.getTipoVinculo().getId() == TipoVinculoDiscente.EXTENSAO_BOLSISTA_INTERNO);
	}

	public boolean isBolsistaExterno() {
		return (this.getTipoVinculo().getId() == TipoVinculoDiscente.EXTENSAO_BOLSISTA_EXTENO);
	}



	/**
	 * Justificativa do coordenador na seleção do discente.
	 * 
	 * @return
	 */
	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	/**
	 * Indica, com base na data início e data fim, se o discente já iniciou sua participação no projeto.
	 * @return
	 */
	public boolean isValido() {
	    return (getDataInicio() != null) && (getDataFim() != null);
	}

	/**
	 * Verifica, com base na data início e data fim, se o discente está atuante no projeto.
	 * @return
	 */
	public boolean isVigente() {
	    return atividade.getProjeto().isVigente() && isAtivo() && isValido() && CalendarUtils.isDentroPeriodo(getDataInicio(), getDataFim());
	}

	
	/**
	 * Informa se o discente do projeto está finalizado.
	 * 
	 * @return
	 */
	public boolean isFinalizado() {
	    return isAtivo() && isValido() && getDataFim().before(new Date());
	}

	/**
	  * Informa se discente pode receber certificado. 
	  * @return
	  */
	 public boolean isPassivelEmissaoCertificado() {
		 if(atividade == null )
			 return false;
		 return atividade.isFinalizada() && atividade.getProjeto().isConcluido() && isFinalizado();		 
	 }	
	 
}
