/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 11/12/2007
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.persistence.CascadeType;
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
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.mensagens.MensagensExtensao;
import br.ufrn.sigaa.projetos.dominio.CronogramaProjeto;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente;

/*******************************************************************************
 * <p>
 * Representa um plano de trabalho de um Discente de extensão. <br/>
 * 
 * Todos os tipos de discentes de extensão (BOLSISTA, VOLUNTARIO, EM ATIVIDADE
 * CURRICULAR, BOLSISTA EXTERNO) devem ter um plano de trabalho descrevendo as
 * atividades que desenvolve na ação de extensão. <br/>
 * 
 * O plano de trabalho deve ser cadastrado pelo Coordenador da ação durante o
 * cadastro do discente. <br/>
 * 
 * Discentes voluntários de uma ação de extensão tem planos de trabalho em
 * comum. Vários discentes compartilham o mesmo plano.
 * </p>
 * 
 * @author Ricardo Wendell
 * @author ilueny santos
 * 
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "plano_trabalho_extensao")
public class PlanoTrabalhoExtensao implements Validatable {

	/** Identificador único do objeto. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_plano_trabalho_extensao", unique = true)
	private int id;

	/** Justificativa do plano de trabalho. */
	@Column(name = "justificativa")
	private String justificativa;

	/** Objetivo do plano de trabalho. */
	@Column(name = "objetivo")
	private String objetivo;

	/** Descrição das atividades do plano de trabalho. */
	@Column(name = "descricao_atividades")
	private String descricaoAtividades;

	/** Data de cadastro do plano de trabalho. */
	@CriadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	private Date dataCadastro;

	/** Data de envio do plano de trabalho. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_envio")
	private Date dataEnvio;
	
	/** Data início do plano de trabalho. */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio")
	private Date dataInicio;

	/** Data Fim do plano de trabalho. */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim")
	private Date dataFim;

	/** Informa se o plano de trabalho esta ativo no sistema. */
	@Column(name = "ativo")
	private boolean ativo;

	/** Local onde será realizado o plano de trabalho. */
	@Column(name = "local_trabalho")
	private String localTrabalho;

	/** Responsável pelo cadastro do plano de trabalho. */
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;

	/** Atividade de Extensão ao qual o plano de trabalho se refere. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_atividade")
	private AtividadeExtensao atividade = new AtividadeExtensao();

	/** Discente que esta executando o plano de trabalho atualmente. */
	@ManyToOne(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name = "id_discente_extensao")
	private DiscenteExtensao discenteExtensao;
	
	/** Informa o tipo de vinculo do discente do plano de trabalho. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_vinculo_discente")
	private TipoVinculoDiscente tipoVinculo;

	/** Cronograma do projeto. */
	@IndexColumn(name = "ordem", base = 1)
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "planoTrabalhoExtensao")
	private List<CronogramaProjeto> cronogramas = new ArrayList<CronogramaProjeto>(0);

	/** Lista de discentes voluntários. */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "planoTrabalhoExtensao")
	private Collection<DiscenteExtensao> discentesVoluntarios = new HashSet<DiscenteExtensao>(0);

	/** Histórico dos Discentes que trabalharam no plano de trabalho. */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "planoTrabalhoExtensao")
	private Collection<DiscenteExtensao> historicoDiscentesPlano = new HashSet<DiscenteExtensao>(0);
	
	/** Orientador deste plano de trabalho. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_orientador")
	private MembroProjeto orientador = new MembroProjeto();

	/** Utilizado na substituição de discentes do plano de trabalho. */
	@Transient
	private DiscenteExtensao discenteExtensaoNovo;

	/** Utilizado no cadastro do plano, caso o discente tenha sido escolhido da lista do processo seletivo. */
	@Transient
	private InscricaoSelecaoExtensao inscricaoSelecaoExtensao;


	public PlanoTrabalhoExtensao() {
	}

	public PlanoTrabalhoExtensao(int id) {
		this.id = id;
	}

	public AtividadeExtensao getAtividade() {
		return atividade;
	}

	public void setAtividade(AtividadeExtensao atividade) {
		this.atividade = atividade;
	}

	public String getDescricaoAtividades() {
		return descricaoAtividades;
	}

	public void setDescricaoAtividades(String descricaoAtividades) {
		this.descricaoAtividades = descricaoAtividades;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
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

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public String getLocalTrabalho() {
		return localTrabalho;
	}

	public void setLocalTrabalho(String localTrabalho) {
		this.localTrabalho = localTrabalho;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		if (discenteExtensao != null) {
			ValidatorUtil.validateRequired(discenteExtensao.getDiscente(), "Discente", lista);
		} else if(isNovoPlano()){
			lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Discente");
		}
		ValidatorUtil.validateRequired(atividade, "Atividade", lista);
		ValidatorUtil.validateRequired(justificativa, "Justificativa", lista);
		ValidatorUtil.validateRequired(descricaoAtividades, "Descrição", lista);
		ValidatorUtil.validateRequired(localTrabalho, "Local de Trabalho", lista);
		
		if (atividade != null && atividade.isProjetoAssociado() && ValidatorUtil.isEmpty(atividade.getDataEnvio())) {
		    lista.addMensagem(MensagensExtensao.REGRA_CADASTRO_PLANO_TRABALHO_ACAO_EXTENSAO);
		}
		
		return lista;
	}

	public DiscenteExtensao getDiscenteExtensao() {
		return discenteExtensao;
	}

	public void setDiscenteExtensao(DiscenteExtensao discenteExtensao) {
		this.discenteExtensao = discenteExtensao;
	}

	public String getObjetivo() {
		return objetivo;
	}

	public void setObjetivo(String objetivo) {
		this.objetivo = objetivo;
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

	public MembroProjeto getOrientador() {
		return orientador;
	}
	
	public void setOrientador(MembroProjeto orientador) {
		this.orientador = orientador;
	}

	public List<CronogramaProjeto> getCronogramas() {
		return cronogramas;
	}

	public void setCronogramas(List<CronogramaProjeto> cronogramas) {
		this.cronogramas = cronogramas;
	}

	/**
	 * Discentes voluntários de uma ação de extensão tem planos de trabalho em
	 * comum. Vários discentes compartilham o mesmo plano.
	 * 
	 * @return
	 */
	public Collection<DiscenteExtensao> getDiscentesVoluntarios() {
		return discentesVoluntarios;
	}

	public void setDiscentesVoluntarios(
			Collection<DiscenteExtensao> discentesVoluntarios) {
		this.discentesVoluntarios = discentesVoluntarios;
	}

	public DiscenteExtensao getDiscenteExtensaoNovo() {
		return discenteExtensaoNovo;
	}

	public void setDiscenteExtensaoNovo(DiscenteExtensao discenteExtensaoNovo) {
		this.discenteExtensaoNovo = discenteExtensaoNovo;
	}

	public Collection<DiscenteExtensao> getHistoricoDiscentesPlano() {
		return historicoDiscentesPlano;
	}

	public void setHistoricoDiscentesPlano(Collection<DiscenteExtensao> historicoDiscentesPlano) {
		this.historicoDiscentesPlano = historicoDiscentesPlano;
	}

	public InscricaoSelecaoExtensao getInscricaoSelecaoExtensao() {
		return inscricaoSelecaoExtensao;
	}

	public void setInscricaoSelecaoExtensao(InscricaoSelecaoExtensao inscricaoSelecaoExtensao) {
		this.inscricaoSelecaoExtensao = inscricaoSelecaoExtensao;
	}

	public boolean isNovoPlano() {
		return id == 0;
	}
	
	/**
	 * Indica, com base na data início e data fim, se o plano de trabalho já iniciou no projeto.
	 * @return
	 */
	public boolean isValido() {
	    return (getDataInicio() != null) && (getDataFim() != null);
	}

	/**
	 * Verifica, com base na data início e data fim, se o plano de trabalho está atuante no projeto.
	 * @return
	 */
	public boolean isVigente() {
	    return atividade.getProjeto().isVigente() && isAtivo() && isValido() && isEnviado() && CalendarUtils.isDentroPeriodo(getDataInicio(), getDataFim());
	}

	
	/**
	 * Informa se o plano de trabalho está finalizado.
	 * 
	 * @return
	 */
	public boolean isFinalizado() {
	    return isAtivo() && isValido() && isEnviado() && getDataFim().before(CalendarUtils.descartarHoras(new Date()));
	}

	
	/** Indica que o plano foi enviado para registro. O cadastro foi concluído. */
	public boolean isEnviado() {
		return (getDataEnvio() != null);
	}

	
	/**
	 * Indica se o plano de trabalho está dentro do período do projeto.
	 * 	
	 * @return
	 */
	public boolean isPlanoNoPeriodoProjeto() {
		if (this.getAtividade() != null && this.getAtividade().getProjeto() != null) {
			
			Date inicioProjeto = this.getAtividade().getProjeto().getDataInicio();
			Date fimProjeto = this.getAtividade().getProjeto().getDataFim();
			
			boolean inicioPlanoDentroProjeto = CalendarUtils.isDentroPeriodo(inicioProjeto, fimProjeto, getDataInicio());
			boolean fimPlanoDentroProjeto = CalendarUtils.isDentroPeriodo(inicioProjeto, fimProjeto, getDataFim());
			
			return inicioPlanoDentroProjeto && fimPlanoDentroProjeto;
		}
		return false;
	}

	/** Informa a data em que o plano de trabalho foi enviado para registro na Pró-Reitoria. */
	public Date getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public TipoVinculoDiscente getTipoVinculo() {
		return tipoVinculo;
	}

	public void setTipoVinculo(TipoVinculoDiscente tipoVinculo) {
		this.tipoVinculo = tipoVinculo;
	}

}
