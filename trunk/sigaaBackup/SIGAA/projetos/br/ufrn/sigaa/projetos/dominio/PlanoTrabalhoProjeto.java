package br.ufrn.sigaa.projetos.dominio;

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
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;


/*******************************************************************************
 * <p>
 * Representa um plano de trabalho de um Discente de projeto. Todos os tipos de discentes de projeto 
 * (BOLSISTA, VOLUNTARIO, BOLSISTA EXTERNO) devem ter um plano de trabalho descrevendo as atividades que 
 * desenvolve no projeto. 
 * O plano de trabalho deve ser cadastrado pelo Coordenador do projeto durante o cadastro do discente. 
 * </p>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(schema = "projetos", name = "plano_trabalho_projeto")
public class PlanoTrabalhoProjeto implements Validatable{
	
	/** Identificador de plano de tranalho projeto */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_plano_trabalho_projeto", unique = true, nullable = false)
	private int id;

	/** Projeto ao qual plano de trabalho faz referência. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_projeto")
	private Projeto projeto;

	/** Local onde será realizado o plano de trabalho. */
	@Column(name = "local_trabalho")
	private String localTrabalho;
 	
	/** Introdução e justificativa do plano de trabalho. */
	private String justificativa;

	/** Objetivos do plano de trabalho. */
	@Column(name = "objetivo")
	private String objetivos;

	/** Metodologia do plano de trabalho. */
	private String metodologia;

	/** Data início do plano de trabalho. */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio")
	private Date dataInicio;

	/** Data Fim do plano de trabalho. */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim")
	private Date dataFim;

	/** Responsável pelo cadastro do plano de trabalho. */
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;

	/** Cronograma do plano de trabalho. */
	@IndexColumn(name = "ordem", base = 1)
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "planoTrabalhoProjeto")
	private List<CronogramaProjeto> cronogramas = new ArrayList<CronogramaProjeto>(0);
	
	/** Discente atualmente associado ao plano de trabalho do projeto. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_discente_projeto")    	
	private DiscenteProjeto discenteProjeto = new DiscenteProjeto();
	
	/** Histórico dos Discentes que trabalharam no plano de trabalho. */ 
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "planoTrabalhoProjeto")
	private Collection<DiscenteProjeto> historicoDiscentesPlano = new HashSet<DiscenteProjeto>(0);
	
	/** Informa o tipo de vinculo do discente do plano de trabalho. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_vinculo_discente")
	private TipoVinculoDiscente tipoVinculo;
	
	/** Data de envio do plano de trabalho.	 */	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_envio")
	private Date dataEnvio;
	
	/** Indica ativo ou falso para plano de trabalho  */
	private boolean ativo;
	
	/** Utilizado para evitar validações de discentes quando somente o plano está sendo alterado*/
	@Transient
	private boolean alterando = false;
	
	/** Utilizado na substituição de discentes do plano de trabalho */
	@Transient
	private DiscenteProjeto discenteProjetoNovo;
	
	/** Utilizado no cadastro do plano, caso o discente tenha sido escolhido da lista do processo seletivo*/
	@Transient
	private InscricaoSelecaoProjeto inscricaoSelecaoProjeto;
	
	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void setId(int id) {
		this.id = id; 
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		if (discenteProjeto != null) {
			ValidatorUtil.validateRequired(discenteProjeto.getDiscente(), "Discente", lista);
		} else {
			lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Discente");
		}
		ValidatorUtil.validateRequired(projeto, "Projeto", lista);
		ValidatorUtil.validateRequired(justificativa, "Justificativa", lista);
		ValidatorUtil.validateRequired(metodologia, "Metodologia/Atividades", lista);
		ValidatorUtil.validateRequired(localTrabalho, "Local de Trabalho", lista);

		return lista;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public String getObjetivos() {
		return objetivos;
	}

	public void setObjetivos(String objetivos) {
		this.objetivos = objetivos;
	}

	public String getMetodologia() {
		return metodologia;
	}

	public void setMetodologia(String metodologia) {
		this.metodologia = metodologia;
	}

	public List<CronogramaProjeto> getCronogramas() {
		return cronogramas;
	}

	public void setCronogramas(List<CronogramaProjeto> cronogramas) {
		this.cronogramas = cronogramas;
	}

	public DiscenteProjeto getDiscenteProjeto() {
		return discenteProjeto;
	}

	public void setDiscenteProjeto(DiscenteProjeto discenteProjeto) {
		this.discenteProjeto = discenteProjeto;
	}

	public String getLocalTrabalho() {
		return localTrabalho;
	}

	public void setLocalTrabalho(String localTrabalho) {
		this.localTrabalho = localTrabalho;
	}

	public TipoVinculoDiscente getTipoVinculo() {
		return tipoVinculo;
	}

	public void setTipoVinculo(TipoVinculoDiscente tipoVinculo) {
		this.tipoVinculo = tipoVinculo;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	public boolean isAlterando() {
		return alterando;
	}

	public void setAlterando(boolean alterando) {
		this.alterando = alterando;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Collection<DiscenteProjeto> getHistoricoDiscentesPlano() {
		return historicoDiscentesPlano;
	}

	public void setHistoricoDiscentesPlano(
			Collection<DiscenteProjeto> historicoDiscentesPlano) {
		this.historicoDiscentesPlano = historicoDiscentesPlano;
	}

	public DiscenteProjeto getDiscenteProjetoNovo() {
		return discenteProjetoNovo;
	}

	public void setDiscenteProjetoNovo(DiscenteProjeto discenteProjetoNovo) {
		this.discenteProjetoNovo = discenteProjetoNovo;
	}

	public InscricaoSelecaoProjeto getInscricaoSelecaoProjeto() {
		return inscricaoSelecaoProjeto;
	}

	public void setInscricaoSelecaoProjeto(
			InscricaoSelecaoProjeto inscricaoSelecaoProjeto) {
		this.inscricaoSelecaoProjeto = inscricaoSelecaoProjeto;
	}

	/** Informa a data em que o plano de trabalho foi enviado para registro. */
	public Date getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

}
