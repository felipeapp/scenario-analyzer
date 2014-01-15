/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 20/10/2010
 */
package br.ufrn.sigaa.estagio.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ArrayList;
import java.util.Calendar;
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
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Entidade que representa os Est�gios dos Discentes.
 * 
 * @author arlindo
 *
 */
@Entity
@Table(name = "estagiario", schema = "estagio")
public class Estagiario implements Validatable, Comparable<Estagiario> {
	
	/** Chave prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="estagio.hibernate_sequence") })	
	@Column(name = "id_estagiario")
	private int id;	
	
	/** Discente estagi�rio */
	@ManyToOne(targetEntity=Discente.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_discente")
	private DiscenteAdapter discente;
	
	/** Interesse Oferta de Est�gio ao qual o discente se interessou */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_interesse_oferta")
	private InteresseOferta interesseOferta;
	
	/** Concedente de Est�gio da oferta */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_concedente_estagio")
	private ConcedenteEstagio concedente;	
	
	/** Supervisor do Est�gio */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa_supervisor")	
	private Pessoa supervisor;
	
	/** Tipo do Est�gio */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_estagio")
	private TipoEstagio tipoEstagio;
	
	/** Orientador do Est�gio do Discente */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_servidor")
	private Servidor orientador;	
	
	/** Indica se o est�gio alterna entre teoria e pr�tica */
	@Column(name = "alterna_teoria_pratica")
	private boolean alternaTeoriaPratica;
	
	/** Data de in�cio do est�gio */
	@Column(name = "data_inicio")
	private Date dataInicio;
	
	/** Data de Fim do est�gio */
	@Column(name = "data_fim")
	private Date dataFim;
	
	/** Hora (Di�ria) de in�cio do est�gio */
	@Column(name = "hora_inicio")
	private Date horaInicio;
	
	/** Hora (Di�ria) de Fim do est�gio */
	@Column(name = "hora_fim")
	private Date horaFim;	
	
	/** Carga Hor�ria semanal do est�gio */
	@Column(name = "carga_horaria_semanal")
	private int cargaHorariaSemanal;
	
	/** Valor da bolsa auxilio do est�gio */
	@Column(name="valor_bolsa")
	private Double valorBolsa;	
	
	/** Valor do auxilio transporte */
	@Column(name="valor_aux_transporte")
	private Double valorAuxTransporte;
	
	/** Nome da seguradora contra acidentes pessoais */
	@Column(name = "seguradora")
	private String seguradora;
	
	/** Cnpj da Seguradora */
	@Column(name = "cnpj_seguradora")
	private Long cnpjSeguradora;	
	
	/** Dados da Ap�lice de seguro */
	@Column(name = "apolice_seguro")
	private String apoliceSeguro;
	
	/** Valor do Seguro */
	@Column(name="valor_seguro")
	private Double valorSeguro;
	
	/** Status atual do est�gio */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="status")
	private StatusEstagio status;
	
	/** Observa��es referente ao parecer do Coordenador */
	@Column(name = "obs_parecer_coordenador")
	private String obsParecerCoordenador;		
	
	/** Descri��o das Atividades que o discente selecionado ir� exercer no est�gio */
	@Column(name = "descricao_atividades")
	private String descricaoAtividades;		
	
	/** Data do cadastro. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;
	
	/** Registro entrada de quem cadastrou. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroCadastro;	
	
	/** Data da Atualiza��o. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_alteracao")
	@AtualizadoEm
	private Date dataAlteracao;
	
	/** Registro entrada de quem Atualizou. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_alteracao")
	@AtualizadoPor
	private RegistroEntrada registroAlteracao;
	
	/** Data de Cancelamento. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cancelamento")
	private Date dataCancelamento;
	
	/** Data de Aprova��o do est�gio. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_aprovacao")
	private Date dataAprovacao;	
	
	/** Registro entrada de quem Cancelou o Est�gio. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_cancelamento")
	private RegistroEntrada registroCancelamento;
	
	/** Registro entrada de quem Solicitou o Cancelamento do Est�gio. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_solicitacao_cancelamento")
	private RegistroEntrada registroSolicitacaoCancelamento;	
	
	/** Motivo pelo qual o est�gio foi cancelado */
	@Column(name = "motivo_cancelamento")
	private String motivoCancelamento;			
	
	/** Hor�rios que o discente trabalhar� no est�gio. */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_estagiario")
	@OrderBy("dia, horaInicio")
	private List<HorarioEstagio> horariosEstagio;
	
	/** Empresa que ser� conveniada. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa")
	private Pessoa empresaEstagio = new Pessoa();
	
	/**
	 * Renova��es do Est�gio
	 */
	@OneToMany(mappedBy = "estagio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<RenovacaoEstagio> renovacoes = new ArrayList<RenovacaoEstagio>();	

	public Estagiario() {
		horariosEstagio = new ArrayList<HorarioEstagio>();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}	

	public ConcedenteEstagio getConcedente() {
		return concedente;
	}

	public void setConcedente(ConcedenteEstagio concedente) {
		this.concedente = concedente;
	}

	public InteresseOferta getInteresseOferta() {
		return interesseOferta;
	}

	public void setInteresseOferta(InteresseOferta interesseOferta) {
		this.interesseOferta = interesseOferta;
	}

	public TipoEstagio getTipoEstagio() {
		return tipoEstagio;
	}

	public void setTipoEstagio(TipoEstagio tipoEstagio) {
		this.tipoEstagio = tipoEstagio;
	}

	public Servidor getOrientador() {
		return orientador;
	}

	public void setOrientador(Servidor orientador) {
		this.orientador = orientador;
	}

	public boolean isAlternaTeoriaPratica() {
		return alternaTeoriaPratica;
	}

	public void setAlternaTeoriaPratica(boolean alternaTeoriaPratica) {
		this.alternaTeoriaPratica = alternaTeoriaPratica;
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

	public int getCargaHorariaSemanal() {
		return cargaHorariaSemanal;
	}

	public void setCargaHorariaSemanal(int cargaHorariaSemanal) {
		this.cargaHorariaSemanal = cargaHorariaSemanal;
	}

	public Double getValorBolsa() {
		return valorBolsa;
	}

	public void setValorBolsa(Double valorBolsa) {
		this.valorBolsa = valorBolsa;
	}

	public Double getValorAuxTransporte() {
		return valorAuxTransporte;
	}

	public void setValorAuxTransporte(Double valorAuxTransporte) {
		this.valorAuxTransporte = valorAuxTransporte;
	}

	public String getSeguradora() {
		return seguradora;
	}

	public void setSeguradora(String seguradora) {
		this.seguradora = seguradora;
	}

	public String getApoliceSeguro() {
		return apoliceSeguro;
	}

	public void setApoliceSeguro(String apoliceSeguro) {
		this.apoliceSeguro = apoliceSeguro;
	}

	public Double getValorSeguro() {
		return valorSeguro;
	}

	public void setValorSeguro(Double valorSeguro) {
		this.valorSeguro = valorSeguro;
	}

	public StatusEstagio getStatus() {
		return status;
	}

	public void setStatus(StatusEstagio status) {
		this.status = status;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public RegistroEntrada getRegistroAlteracao() {
		return registroAlteracao;
	}

	public void setRegistroAlteracao(RegistroEntrada registroAlteracao) {
		this.registroAlteracao = registroAlteracao;
	}		
	
	public Pessoa getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(Pessoa supervisor) {
		this.supervisor = supervisor;
	}	

	public String getDescricaoAtividades() {
		return descricaoAtividades;
	}

	public void setDescricaoAtividades(String descricaoAtividades) {
		this.descricaoAtividades = descricaoAtividades;
	}
	
	public Long getCnpjSeguradora() {
		return cnpjSeguradora;
	}

	public void setCnpjSeguradora(Long cnpjSeguradora) {
		this.cnpjSeguradora = cnpjSeguradora;
	}
	
	/**
	 * Retorna o Cnpj da Seguradora formatado 
	 * @return
	 */
	public String getCnpjSeguradoraFormatado(){
		if (cnpjSeguradora == null)
			return null;
		return Formatador.getInstance().formatarCNPJ(cnpjSeguradora);
	}

	public Date getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(Date horaInicio) {
		this.horaInicio = horaInicio;
	}

	public Date getHoraFim() {
		return horaFim;
	}

	public void setHoraFim(Date horaFim) {
		this.horaFim = horaFim;
	}	

	public String getObsParecerCoordenador() {
		return obsParecerCoordenador;
	}

	public void setObsParecerCoordenador(String obsParecerCoordenador) {
		this.obsParecerCoordenador = obsParecerCoordenador;
	}

	public List<RenovacaoEstagio> getRenovacoes() {
		return renovacoes;
	}

	public void setRenovacoes(List<RenovacaoEstagio> renovacoes) {
		this.renovacoes = renovacoes;
	}

	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

	public RegistroEntrada getRegistroCancelamento() {
		return registroCancelamento;
	}

	public void setRegistroCancelamento(RegistroEntrada registroCancelamento) {
		this.registroCancelamento = registroCancelamento;
	}

	public String getMotivoCancelamento() {
		return motivoCancelamento;
	}

	public void setMotivoCancelamento(String motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}

	public Date getDataAprovacao() {
		return dataAprovacao;
	}

	public void setDataAprovacao(Date dataAprovacao) {
		this.dataAprovacao = dataAprovacao;
	}

	/** Verifica se o est�gio est� pendente de aprova��o */
	@Transient
	public boolean isPendente(){
		if (status == null)
			return false;
		return status.getId() == StatusEstagio.EM_ANALISE;
	}

	/** Verifica se o est�gio est� cancelado */
	@Transient
	public boolean isCancelado(){
		if (status == null)
			return false;		
		return status.getId() == StatusEstagio.CANCELADO;
	}
	
	/** Verifica se o est�gio est� conclu�do */
	@Transient
	public boolean isConcluido(){
		if (status == null)
			return false;		
		return status.getId() == StatusEstagio.CONCLUIDO;
	}
	
	/** Verifica se o est�gio n�o � compat�vel */
	@Transient
	public boolean isNaoCompativel(){
		if (status == null)
			return false;		
		return status.getId() == StatusEstagio.NAO_COMPATIVEL;
	}
	
	/** Verifica se o est�gio foi Solicitado cancelamento */
	@Transient
	public boolean isSolicitadoCancelamento(){
		if (status == null)
			return false;		
		return status.getId() == StatusEstagio.SOLICITADO_CANCELAMENTO;
	}
	
	/** Verifica se o est�gio est� Aprovado */
	@Transient
	public boolean isAprovado(){
		if (status == null)
			return false;		
		return status.getId() == StatusEstagio.APROVADO;
	}
	
	/** Verifica se o est�gio passado est� Aprovado */
	@Transient
	public boolean isAprovado(StatusEstagio s){
		if (ValidatorUtil.isEmpty(s))
			return false;
		return s.getId() == StatusEstagio.APROVADO;
	}
	
	public RegistroEntrada getRegistroSolicitacaoCancelamento() {
		return registroSolicitacaoCancelamento;
	}

	public void setRegistroSolicitacaoCancelamento(
			RegistroEntrada registroSolicitacaoCancelamento) {
		this.registroSolicitacaoCancelamento = registroSolicitacaoCancelamento;
	}

	/**
	 * Verifica se o est�gio est� vigente
	 * @return
	 */
	@Transient
	public boolean isVigente(){
		
		if (dataFim != null){
			Integer periodoMax = ParametroHelper.getInstance().getParametroInt(ParametrosEstagio.PERIODO_MAX_ESTAGIO);
			
			Calendar c = Calendar.getInstance();
			c.setTime(dataInicio);
			c.add(Calendar.YEAR, periodoMax);
			c.add(Calendar.DAY_OF_WEEK, -1);
					
			if (CalendarUtils.descartarHoras(new Date()).getTime() > CalendarUtils.descartarHoras(dataFim).getTime())
				return false;			
			
			if (CalendarUtils.descartarHoras(dataFim).getTime() > CalendarUtils.descartarHoras(c.getTime()).getTime())
				return false;		
			
			return true;
		}
		
		return false;		
	}	
	
	/**
	 * Verifica se est� no per�odo de preencher o Relat�rio Final
	 * S� pode preencher o Relat�rio final a partir do pen�ltimo m�s de fim do est�gio
	 * Ou se for cancelado o est�gio.
	 * @return
	 */
	@Transient
	public boolean isPermitePreencherRelatorioFinal(){	
		if (dataFim != null)
			return (CalendarUtils.calculaQuantidadeMesesEntreDatasIntervaloFechado(new Date(), dataFim) <= 1 && (isAprovado() || isSolicitadoCancelamento())) || isCancelado();
		return false;
	}
	
	/**
	 * Verifica se est� no per�odo de preencher o Relat�rio Peri�dico,
	 * este s� pode ser preenchido a partir do 5 (quinto) m�s ap�s a data de In�cio
	 * Se for cancelado o est�gio preencher o Relat�rio Final.
	 * @return
	 */
	@Transient
	public boolean isPermitePreencherRelatorioPeriodico(){
		if (dataInicio != null)
			return (CalendarUtils.calculaQuantidadeMesesEntreDatasIntervaloFechado(dataInicio, new Date()) >= 5 && (isAprovado() || isSolicitadoCancelamento())) || isCancelado();
		return false;
	}	
	
	/**
	 * Retorna a quantidade de Relat�rios que Devem ser preenchidos durante o Est�gio
	 * @return
	 */
	@Transient
	public int getQuantRelatoriosNecessarios(){
		Date fim = new Date();
		if (fim.after(dataFim))
			fim = dataFim;
		int quantMesesTotal = CalendarUtils.calculaQuantidadeMesesEntreDatasIntervaloFechado(dataInicio, fim);
		int quantMesesRelatorio = ParametroHelper.getInstance().getParametroInt(ParametrosEstagio.MESES_PARA_PREENCHIMENTO_RELATORIO_ESTAGIO);
		
		int quantRelatoriosNecessarios = 0;					
		if (quantMesesTotal > 0)
			quantRelatoriosNecessarios = Math.round(quantMesesTotal / quantMesesRelatorio);

		return quantRelatoriosNecessarios;
	}
	
	/**
	 * Retorna quem cancelou o est�gio.
	 * @return
	 */
	@Transient
	public String getInteressadoQueCancelou(){
		if (registroSolicitacaoCancelamento != null){
			if (registroSolicitacaoCancelamento.getUsuario().getPessoa().getId() == discente.getPessoa().getId())
				return "Discente";
			else if (registroSolicitacaoCancelamento.getUsuario().getPessoa().getId() == concedente.getPessoa().getId()
					||registroSolicitacaoCancelamento.getUsuario().getPessoa().getId() == concedente.getResponsavel().getPessoa().getId())
				return "Concedente";
			else if (registroSolicitacaoCancelamento.getUsuario().getPessoa().getId() == registroCancelamento.getUsuario().getPessoa().getId())
				return "Coodenador";
		}
		return "";
	}
	
	/**
	 * Compara o ID e do est�gio com o passado por par�metro.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}


	/** 
	 * Calcula e retorna o c�digo hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}	

	/**
	 * Valida os atributos do est�gio. 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate() 
	 */	
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		ValidatorUtil.validateRequired(discente, "Discente", lista);
		ValidatorUtil.validateRequired(tipoEstagio, "Tipo do Est�gio", lista);
		
		ValidatorUtil.validateRequired(orientador, "Orientador do Est�gio", lista);
		
		ValidatorUtil.validateRequired(dataInicio, "In�cio do Est�gio", lista);
		ValidatorUtil.validateRequired(dataFim, "Fim do Est�gio", lista);		
		ValidatorUtil.validaInicioFim(dataInicio, dataFim, "Per�odo do Est�gio", lista);

		if (dataFim != null)
			ValidatorUtil.validaDataAnteriorIgual(new Date(), dataFim, "Data Fim", lista);
		
		ValidatorUtil.validateRequired(cargaHorariaSemanal, "Carga Hor�ria", lista);
		
		if (isBolsaObrigatorio()) {
			double minBolsa = ParametroHelper.getInstance().getParametroDouble(ParametrosEstagio.VALOR_MINIMO_BOLSA_ESTAGIO_NAO_OBRIGATORIO);
			if (valorBolsa < minBolsa)
				lista.addErro("Valor da Bolsa: O valor deve ser maior ou igual a R$ " + Formatador.getInstance().formatarMoeda(minBolsa));
			double minTransporte = ParametroHelper.getInstance().getParametroDouble(ParametrosEstagio.VALOR_MINIMO_TRANSPORTE_ESTAGIO_NAO_OBRIGATORIO);
			if (valorAuxTransporte < minTransporte)
				lista.addErro("Valor do Aux�lio Transporte: O valor deve ser maior ou igual a R$ " + Formatador.getInstance().formatarMoeda(minTransporte));
		}
		
		if (cnpjSeguradora == null)
			validateRequired(cnpjSeguradora, "CNPJ da Seguradora", lista);
		else
			ValidatorUtil.validateCPF_CNPJ(cnpjSeguradora, "CNPJ da Seguradora", lista);
		ValidatorUtil.validateRequired(seguradora, "Seguradora", lista);
		ValidatorUtil.validateRequired(apoliceSeguro, "Ap�lice do Seguro", lista);
		ValidatorUtil.validaDoublePositivo(valorSeguro, "Valor do Seguro", lista);

		return lista;
	}

	/** Indica se a bolsa � obrigat�ria ou n�o.
	 * @return
	 */
	public boolean isBolsaObrigatorio() {
		return concedente.getConvenioEstagio().isOrgaoFederal() && tipoEstagio.getId() == TipoEstagio.ESTAGIO_CURRICULAR_NAO_OBRIGATORIO;
	}

	public List<HorarioEstagio> getHorariosEstagio() {
		return horariosEstagio;
	}

	public void setHorariosEstagio(List<HorarioEstagio> horariosEstagio) {
		this.horariosEstagio = horariosEstagio;
	}

	/** Retorna uma descri��o textual do hor�rio de est�gio.
	 * @return
	 */
	public String getDescricaoHorarioEstagio() {
		if (isEmpty(horariosEstagio)) return null;
		StringBuilder str = new StringBuilder();
		Formatador formatador = Formatador.getInstance();
		char dia = '0';
		for (HorarioEstagio horario : horariosEstagio) {
			if (dia != horario.getDia()) {
				if (str.length() > 1) {
					str.delete(str.lastIndexOf(", "), str.length());
					str.append("; ");
				}
				str.append(horario.getDescricaoDia()).append(": ");
				dia = horario.getDia();
			}
			str.append("das ").append(formatador.formatarHora(horario.getHoraInicio())).append(" �s ").append(formatador.formatarHora(horario.getHoraFim())).append(", ");
		}
		str.delete(str.lastIndexOf(", "), str.length());
		return str.toString();
	}

	/** Adiciona um hor�rio de est�gio � lista de hor�rios do estagi�rio.
	 * @param horario
	 */
	public void addHorarioEstagio(HorarioEstagio horario) {
		if (horariosEstagio == null) horariosEstagio = new ArrayList<HorarioEstagio>();
		horario.setEstagio(this);
		horariosEstagio.add(horario);
	}

	public Pessoa getEmpresaEstagio() {
		return empresaEstagio;
	}

	public void setEmpresaEstagio(Pessoa empresaEstagio) {
		this.empresaEstagio = empresaEstagio;
	}

	/** Compara este estagi�rio com outro
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Estagiario other) {
		int cmp = 0;
		if (this.concedente != null && other.concedente != null && this.concedente.getPessoa() != null)
			cmp = this.concedente.getPessoa().compareTo(other.concedente.getPessoa());
		if (cmp == 0 && this.status != null && other.status != null)
			cmp = this.status.getDescricao().compareTo(other.status.getDescricao());
		return cmp;
	}

}
