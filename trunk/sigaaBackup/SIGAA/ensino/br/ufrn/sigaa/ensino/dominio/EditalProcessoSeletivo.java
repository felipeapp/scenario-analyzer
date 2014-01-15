/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Classe de domínio responsável em agrupar os processos seletivos
 * 
 * @author Mário Rizzi
 */
@Entity
@Table(name="edital_processo_seletivo", schema="ensino")
public class EditalProcessoSeletivo implements Validatable {

	/**
	 * Atributo que indica a chave-primária que identifica um edital processo seletivo.
	 */
	@Id
	@Column(name="id_edital_processo_seletivo")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;

	/**
	 * Atributo utilizado para definir o nome do edital do processo seletivo.
	 */
	private String nome;
	
	/**
	 * Atributo utilizado para definir a descrição que abrange os demais processo seletivos associados ao edital.
	 */
	@Column(name="descricao")
	private String descricao;
	
	/**
	 * Atributo utilizado para indicar o status do processo seletivo (Pós-Graduação).
	 */
	@Column(name="status")
	private Integer status;	
	
	/**
	 * Atributo para descrever alterações a serem realizadas no Processo Seletivo (Apenas Para Pós-Graduação).
	 */
	@Column(name="motivo_alteracao")
	private String motivoAlteracao;

	/**
	 * Atributo utilizado para popular todos os processos seletivos associados ao edital.
	 */
	@OneToMany(mappedBy = "editalProcessoSeletivo", cascade = CascadeType.ALL)
	@Cascade({org.hibernate.annotations.CascadeType.DELETE})
	private Collection<ProcessoSeletivo> processosSeletivos;
	
	/**
	 * Quando o processo seletivo for stricto, o orientador escolhido pelo aluno será notificado.
	 */
	@Column(name = "notificar_orientador")
	private Boolean notificarOrientador = Boolean.FALSE;
	
	/**
	 * Define na inscrição se existe vagas disponíveis.
	 */
	@Column(name = "verifica_existe_vaga")
	private boolean verificaExisteVaga = Boolean.FALSE;
	
	/**
	 * Atributo utilizado para definir o id do arquivo do manual do candidato.
	 */
	@Column(name="id_manual_candidato")
	private Integer idManualCandidato;

	/**
	 * Atributo utilizado para definir maiores informações ao candidato de como proceder na inscrição.
	 */
	@Column(name="orientacoes_inscritos")
	private String orientacoesInscritos;
	
	/**
	 * Atributo utilizado para definir o id do arquivo do edital que engloba todos processos seletivos.
	 */
	@Column(name="id_edital")
	private Integer idEdital;
	
	/**
	 * Atributo utilizado para definir a data inicial das inscrições.
	 */
	@Column(name="inicio_inscricoes")
	@Temporal(TemporalType.TIMESTAMP)
	private Date inicioInscricoes;
	
	/**
	 * Atributo utilizado para definir a hora do início das inscriçõe que deve
	 * ser adicionado ao atributo {@link #inicioInscricoes} quando persistido.
	 */
	@Transient
	private Date horaInicioInscricoes;

	/**
	 * Atributo utilizado para definir a data final das inscrições
	 */
	@Column(name="fim_inscricoes")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fimInscricoes;
	
	/**
	 * Atributo utilizado para definir a hora do final das inscriçõe que deve
	 * ser adicionado ao atributo {@link #fimInscricoes} quando persistido.
	 */
	@Transient
	private Date horaFimInscricoes;
	
	/**
	 * Atributo utilizado para definir a data inicial do período de agendamento.
	 */
	@Column(name = "inicio_periodo_agenda", nullable = true)
	private Date inicioPeriodoAgenda;
	
	/**
	 * Atributo utilizado para definir a data final do período de agendamento.
	 */
	@Column(name = "fim_periodo_agenda", nullable = true)
	private Date fimPeriodoAgenda;
	
	/**
	 * Atributo utilizado para definir as datas de agendamento associadas ao edital.
	 */
	@OneToMany(mappedBy="editalProcessoSeletivo",cascade=CascadeType.ALL)
	private List<AgendaProcessoSeletivo> agendas;
	
	/**
	 * Atributo que indica qual usuário que cadastrou o edital.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada")
	@CriadoPor
	private RegistroEntrada criadoPor;

	/**
	 * Atributo que indica a data que foi criado o processo seletivo
	 */
	@Column(name="data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	private Date criadoEm;
	
	/** Valor da taxa de inscrição. */
	@Column(name = "taxa_inscricao")
	private double taxaInscricao;
	
	/** Data de vencimento do boleto bancário/GRU. */
	@Column(name = "data_vencimento_boleto")
	private Date dataVencimentoBoleto;
	
	/** Configuração de GRU a ser utilizada para recolhimento da taxa de inscrição. */
	@Column(name = "id_configuracao_gru")
	private Integer idConfiguracaoGRU;
	
	/** Habilita o cadastro de datas de agendamento para entregas de documento */
	@Column(name = "possui_agenda")
	private boolean possuiAgendamento;
	
	/** Quantidade de inscrições realizadas para este Edital. */
	@Transient
	private int qtdInscritos;
	
	/** Restringe o processo seletivo a um grupo */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_restricao_inscricao_selecao", nullable = true)
	private  RestricaoInscricaoSelecao restricaoInscrito;
	
	/** Instruções a serem incluídas na GRU, até 3 linhas. */
	@Column(name="instrucoes_especificas_gru")
	private String instrucoesEspecificasGRU;
	
	/** Construtor padrão. */
	public EditalProcessoSeletivo() {
		this.taxaInscricao = 0.0d;
		Date dataAtual = new Date();
		this.inicioInscricoes = CalendarUtils.configuraTempoDaData(dataAtual, 0, 0, 0, 0);
		this.horaInicioInscricoes = CalendarUtils.configuraTempoDaData(dataAtual, 0, 0, 0, 0);
		this.fimInscricoes = CalendarUtils.configuraTempoDaData(dataAtual, 23, 59, 59, 999);
		this.horaFimInscricoes = CalendarUtils.configuraTempoDaData(dataAtual, 23, 59, 59, 999);
		this.restricaoInscrito = new RestricaoInscricaoSelecao();
		this.verificaExisteVaga = false;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	/**
	 * Retorna o nome do link na parte pública.
	 * @return
	 */
	public String getLinkPartePublica() {
		if (CalendarUtils.isDentroPeriodo(inicioInscricoes, fimInscricoes)) 
			return "Visualizar dados do processo seletivo - " + getNome() + "*";
		else
			return "Visualizar dados do processo seletivo - " + getNome();
			
	}

	/**
	 * Retorna a descrição do link na parte pública.
	 * @return
	 */
	public String getDescricaoPartePublica() {
		if (CalendarUtils.isDentroPeriodo(inicioInscricoes, fimInscricoes)) 
			return getNome() + "*";
		else
			return getNome();
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public Date getInicioPeriodoAgenda() {
		return inicioPeriodoAgenda;
	}

	public void setInicioPeriodoAgenda(Date inicioPeriodoAgenda) {
		this.inicioPeriodoAgenda = inicioPeriodoAgenda;
	}

	public Date getFimPeriodoAgenda() {
		return fimPeriodoAgenda;
	}

	public void setFimPeriodoAgenda(Date fimPeriodoAgenda) {
		this.fimPeriodoAgenda = fimPeriodoAgenda;
	}

	/**
	 * Método que valida alguns atributos do edital de processo seletivo.
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		
		ValidatorUtil.validateRequired(inicioInscricoes, "Início do Perído de Inscrições", erros);
		ValidatorUtil.validateRequired(fimInscricoes, "Fim do Perído de Inscrições", erros);
		ValidatorUtil.validaInicioFim(inicioInscricoes, fimInscricoes, "Data das Inscrições", erros);
		
		return erros;
	}

	/**
	 * Método que retorna todas as datas de agendamento associada ao edital.
	 * @return
	 */
	public List<AgendaProcessoSeletivo> getAgendas() {
		
		if(agendas!=null){
			Collections.sort( agendas, new Comparator<AgendaProcessoSeletivo>(){
				public int compare(AgendaProcessoSeletivo a1, AgendaProcessoSeletivo a2) {
					return a1.getDataAgenda().compareTo(a2.getDataAgenda());
				}
			});
		}
	
		return agendas;
	}

	public void setAgendas(List<AgendaProcessoSeletivo> agendas) {
		this.agendas = agendas;
	}

	public RegistroEntrada getCriadoPor() {
		return criadoPor;
	}

	public void setCriadoPor(RegistroEntrada criadoPor) {
		this.criadoPor = criadoPor;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	/**
	 * Adicionar uma data de agendamento a listagem associada ao edital. 
	 * @param agenda
	 */
	public void addAgenda(AgendaProcessoSeletivo agenda ) {
		if (this.agendas == null) {
			agendas = new ArrayList<AgendaProcessoSeletivo>();
		}
		agenda.setEditalProcessoSeletivo(this);
		agendas.add(agenda);
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getIdManualCandidato() {
		return idManualCandidato;
	}

	public void setIdManualCandidato(Integer idManualCandidato) {
		this.idManualCandidato = idManualCandidato;
	}

	public Integer getIdEdital() {
		return idEdital;
	}

	public void setIdEdital(Integer idEdital) {
		this.idEdital = idEdital;
	}

	public Date getInicioInscricoes() {
		return inicioInscricoes;
	}

	public void setInicioInscricoes(Date inicioInscricoes) {
		this.inicioInscricoes = inicioInscricoes;
	}

	public Date getFimInscricoes() {
		return fimInscricoes;
	}

	public void setFimInscricoes(Date fimInscricoes) {
		this.fimInscricoes = fimInscricoes;
	}

	public Boolean getNotificarOrientador() {
		return notificarOrientador;
	}

	public void setNotificarOrientador(Boolean notificarOrientador) {
		this.notificarOrientador = notificarOrientador;
	}

	public String getOrientacoesInscritos() {
		return orientacoesInscritos;
	}

	public void setOrientacoesInscritos(String orientacoesInscritos) {
		this.orientacoesInscritos = orientacoesInscritos;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMotivoAlteracao() {
		return motivoAlteracao;
	}

	public void setMotivoAlteracao(String motivoAlteracao) {
		this.motivoAlteracao = motivoAlteracao;
	}

	public Collection<ProcessoSeletivo> getProcessosSeletivos() {
		return processosSeletivos;
	}

	public void setProcessosSeletivos(Collection<ProcessoSeletivo> processosSeletivos) {
		this.processosSeletivos = processosSeletivos;
	}

	public double getTaxaInscricao() {
		return taxaInscricao;
	}

	public void setTaxaInscricao(double taxaInscricao) {
		this.taxaInscricao = taxaInscricao;
	}

	public Date getDataVencimentoBoleto() {
		return dataVencimentoBoleto;
	}

	public void setDataVencimentoBoleto(Date dataVencimentoBoleto) {
		this.dataVencimentoBoleto = dataVencimentoBoleto;
	}

	public int getQtdInscritos() {
		return qtdInscritos;
	}

	public void setQtdInscritos(int qtdInscritos) {
		this.qtdInscritos = qtdInscritos;
	}

	public boolean isPossuiAgendamento() {
		return possuiAgendamento;
	}

	public void setPossuiAgendamento(boolean possuiAgendamento) {
		this.possuiAgendamento = possuiAgendamento;
	}

	public Integer getIdConfiguracaoGRU() {
		return idConfiguracaoGRU;
	}

	public void setIdConfiguracaoGRU(Integer idConfiguracaoGRU) {
		this.idConfiguracaoGRU = idConfiguracaoGRU;
	}

	/**
	 * Retorna a hora que deve iniciar as inscrições
	 * @return
	 */
	public Date getHoraInicioInscricoes() {
		if( !isEmpty( this.inicioInscricoes ) 
				&& CalendarUtils.calculaQuantidadeHorasEntreDatas( 
						CalendarUtils.descartarHoras(this.inicioInscricoes), this.inicioInscricoes ) > 0  )
			this.horaInicioInscricoes =  this.inicioInscricoes;
		return horaInicioInscricoes;
	}

	public void setHoraInicioInscricoes(Date horaInicioInscricoes) {
		this.horaInicioInscricoes = horaInicioInscricoes;
	}

	/**
	 * Retorna a hora que deve finalizar as inscrições
	 * @return
	 */
	public Date getHoraFimInscricoes() {
		if( !isEmpty( this.fimInscricoes ) 
				&& CalendarUtils.calculaQuantidadeHorasEntreDatas( 
						CalendarUtils.descartarHoras(this.fimInscricoes), this.fimInscricoes ) > 0  )
			this.horaFimInscricoes =  this.fimInscricoes;
		return horaFimInscricoes;
	}

	public void setHoraFimInscricoes(Date horaFimInscricoes) {
		this.horaFimInscricoes = horaFimInscricoes;
	}
	
	/**
	 * Retorna a data e horário concatenados.
	 * @param data
	 * @param hora
	 * @return
	 */
	@Transient
	private String getDescricaoDataHora(Date data, Date hora){
		String strDataHora = CalendarUtils.format(data, "dd/MM/yyyy");
		if( !isEmpty( hora ) )
			strDataHora += CalendarUtils.format(hora, " ' às ' HH:mm");
		return strDataHora;
	}
	
	/**
	 * Retorna a data e hora do início das inscrições
	 * @return
	 */
	@Transient
	public String getDescricaoInicioInscricoes(){
		return getDescricaoDataHora(getInicioInscricoes(), getHoraInicioInscricoes());
	}

	/**
	 * Retorna a data e hora do final das inscrições
	 * @return
	 */
	@Transient
	public String getDescricaoFimInscricoes(){
		return getDescricaoDataHora(getFimInscricoes(), getHoraFimInscricoes());
	}

	public RestricaoInscricaoSelecao getRestricaoInscrito() {
		return restricaoInscrito;
	}

	public void setRestricaoInscrito(RestricaoInscricaoSelecao restricaoInscrito) {
		this.restricaoInscrito = restricaoInscrito;
	}

	public boolean isVerificaExisteVaga() {
		return verificaExisteVaga;
	}

	public void setVerificaExisteVaga(boolean verificaExisteVaga) {
		this.verificaExisteVaga = verificaExisteVaga;
	}

	public String getInstrucoesEspecificasGRU() {
		return instrucoesEspecificasGRU;
	}

	public void setInstrucoesEspecificasGRU(String instrucoesEspecificasGRU) {
		this.instrucoesEspecificasGRU = instrucoesEspecificasGRU;
	}

}
