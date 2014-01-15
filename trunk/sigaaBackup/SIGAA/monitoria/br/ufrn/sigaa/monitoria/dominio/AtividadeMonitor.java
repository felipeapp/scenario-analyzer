/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * 
 * Created on 16/11/2006
 *
 */
package br.ufrn.sigaa.monitoria.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * <p>
 * Representa a atividade de um monitor em um período. Esta classe é utilizada
 * para o registro de frequência do monitor. Através da frequência é dada uma
 * ordem de pagamento através do SIPAC.
 * <p>
 * 
 * @author David Ricardo
 * @author Victor Hugo
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "atividade_monitor", schema = "monitoria")
public class AtividadeMonitor implements Validatable, Comparable<AtividadeMonitor> {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;

	/** Mês referente a atividade */
	private int mes;

	/** Atributo utilizado para representar o ano referente a Atividade */
	private int ano;

	/** Atributo utilizado para representar o nome das Atividades  */
	private String atividades;

	/** Atributo utilizado para representar o discente da monitoria */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente_monitoria")
	@OrderBy(value = "discente")
	private DiscenteMonitoria discenteMonitoria = new DiscenteMonitoria();

	/** Atributo utilizado para representar a Data de cadastro da Atividade do Monitor */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Atributo utilizado para representar o registro de entrada do discente na Atividade do Monitor */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada_discente")
	@CriadoPor
	private RegistroEntrada registroEntradaDiscente;

	/** Atributo utilizado para informar se a Atividade do Monitor foi ou não avaliada pelo Orientador */
	@Column(name = "validado_orientador")
	private boolean validadoOrientador;

	/** Atributo utilizado para representar a data da validação feita pelo Orientador */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_validacao_orientador")
	private Date dataValidacaoOrientador;

	/** Atriburo utilizado para representar a frenquencia da Atividade do Monitor */
	@Column(name = "frequencia")
	private int frequencia;

	/** Atributo utilizado para representar a orientação feita pelo Orientador */
	@Column(name = "observacao_orientador")
	private String observacaoOrientador;

	/** Atributo utilizado para representar o registro de entrada do Orientador na Atividade do Monitor */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada_orientador")
	private RegistroEntrada registroEntradaOrientador;

	/** Atributo utilizado para informar se a PROGRAD validou a Atividade do Monitor */
	@Column(name = "validado_prograd")
	private boolean validadoPrograd;

	/** Atributo utilizado para representar a Data de validação da PROGRAD */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_validacao_prograd")
	private Date dataValidacaoPrograd;

	/** Atributo utilizado para representar as observações da PROGRAD */
	 @Column(name = "observacao_prograd")
	 private String observacaoPrograd;

	 /** Atributo utilizado para representar o registro de entrada da PROGRAD */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada_prograd")
	private RegistroEntrada registroEntradaPrograd;
	
	/** Determina se o relatório de atividades do monitor está ativo. */
	@CampoAtivo
	private boolean ativo = true;

	/** 
	 * Construtor Padrão
	 */
	public AtividadeMonitor() {	    
	}
	
	/**
	 * Construtor utilizado na lista de atividades de monitores.
	 * 
	 * @param dm
	 * @param mes
	 * @param ano
	 * @param dataCadastro
	 * @param validadoOrientador
	 * @param validadoPrograd
	 */
	public AtividadeMonitor(int id, DiscenteMonitoria dm, int mes, int ano, Date dataCadastro, boolean validadoOrientador, boolean validadoPrograd, int frequencia) {
	    this.id = id;
	    this.discenteMonitoria = dm;
	    this.ano = ano;
	    this.mes = mes;
	    this.dataCadastro = dataCadastro;
	    this.validadoOrientador = validadoOrientador;
	    this.validadoPrograd = validadoPrograd;
	    this.frequencia = frequencia;
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAtividades() {
		return atividades;
	}
	
	public void setAtividades(String atividades) {
		this.atividades = atividades;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public int getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(int frequencia) {
		this.frequencia = frequencia;
	}

	public DiscenteMonitoria getDiscenteMonitoria() {
		return discenteMonitoria;
	}

	public void setDiscenteMonitoria(DiscenteMonitoria discenteMonitoria) {
		this.discenteMonitoria = discenteMonitoria;
	}

	/**
	 * Método utilizado para validar os atributos da Atividade do Monitor
	 */
	public ListaMensagens validate() {

		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validaInt(mes, "Mês de Referência", lista);
		ValidatorUtil.validaInt(ano, "Ano de Referência", lista);
		ValidatorUtil.validateRequired(atividades, "Atividades Desenvolvidas", lista);
		ValidatorUtil.validateRequired(discenteMonitoria, "Discente", lista);
		
		Date dataFreq = CalendarUtils.getMaximoDia((mes), ano);
		if (!CalendarUtils.isDentroPeriodo(discenteMonitoria.getDataInicio(), discenteMonitoria.getDataFim(), dataFreq)) {
		    lista.addErro("Mês e Ano de referência do Relatório de Atividades está fora do período de execução da monitoria.");
		}

		if ((this.frequencia == 0) && (this.validadoOrientador)) {
			lista.addErro("Atividades Validadas devem ter o campo de Freqüência com valor maior que 0(zero).");
		}
		if (this.frequencia > 100) {
			lista.addErro("Freqüência não pode ser maior que 100%.");
		}
		return lista;
	}

	public Date getDataValidacaoPrograd() {
		return dataValidacaoPrograd;
	}

	public void setDataValidacaoPrograd(Date dataValidacaoPrograd) {
		this.dataValidacaoPrograd = dataValidacaoPrograd;
	}

	/*
	 * public String getObservacaoPrograd() { return observacaoPrograd; }
	 * 
	 * public void setObservacaoPrograd(String observacaoPrograd) {
	 * this.observacaoPrograd = observacaoPrograd; }
	 */

	public boolean isValidadoPrograd() {
		return validadoPrograd;
	}

	public void setValidadoPrograd(boolean validadoPrograd) {
		this.validadoPrograd = validadoPrograd;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Date getDataValidacaoOrientador() {
		return dataValidacaoOrientador;
	}

	public void setDataValidacaoOrientador(Date dataValidacaoOrientador) {
		this.dataValidacaoOrientador = dataValidacaoOrientador;
	}

	public String getObservacaoOrientador() {
		return observacaoOrientador;
	}

	public void setObservacaoOrientador(String observacaoOrientador) {
		this.observacaoOrientador = observacaoOrientador;
	}

	public RegistroEntrada getRegistroEntradaDiscente() {
		return registroEntradaDiscente;
	}

	public void setRegistroEntradaDiscente(
			RegistroEntrada registroEntradaDiscente) {
		this.registroEntradaDiscente = registroEntradaDiscente;
	}

	public RegistroEntrada getRegistroEntradaOrientador() {
		return registroEntradaOrientador;
	}

	public void setRegistroEntradaOrientador(
			RegistroEntrada registroEntradaOrientador) {
		this.registroEntradaOrientador = registroEntradaOrientador;
	}

	public RegistroEntrada getRegistroEntradaPrograd() {
		return registroEntradaPrograd;
	}

	public void setRegistroEntradaPrograd(RegistroEntrada registroEntradaPrograd) {
		this.registroEntradaPrograd = registroEntradaPrograd;
	}

	public boolean isValidadoOrientador() {
		return validadoOrientador;
	}

	public void setValidadoOrientador(boolean validadoOrientador) {
		this.validadoOrientador = validadoOrientador;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AtividadeMonitor other = (AtividadeMonitor) obj;
		if (id != other.id)
			return false;
		return true;
	}

	/**
	 * Método utilizado para comparar a Atividade passada por parâmetro com a atividade atual.
	 */
	public int compareTo(AtividadeMonitor o) {
		return ((Integer) this.getId()).compareTo(o.getId());
	}

	public String getObservacaoPrograd() {
		return observacaoPrograd;
	}

	public void setObservacaoPrograd(String observacaoPrograd) {
		this.observacaoPrograd = observacaoPrograd;
	}
	
	
	/**
	 * Verifica se o relatório de atividades ser cadastrado segundo a regra de envio de frequência
	 * passada por parâmetro.
	 * 
	 * @param atv {@link AtividadeMonitor} relatório de atividades do monitor.
	 * @return <code>true</code>
	 */
	public boolean isAtividadePodeSerCadastradaValidada(EnvioFrequencia regraEnvioFrequencia) {
	    
		return (
			(regraEnvioFrequencia.isEnvioLiberado()) 
			&& (this.getAno() == regraEnvioFrequencia.getAno()) 
			&& (this.getMes() == regraEnvioFrequencia.getMes())			
			&& (this.getDiscenteMonitoria().isPermitidoCadastrarRelatorioAtividade(regraEnvioFrequencia))
			); 
	}
	
	public boolean isAtividadeValidadaOrientador() {
	    return this.isValidadoOrientador() && (this.getDataValidacaoOrientador() != null);
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	/** Determina se este Relatório de Atividades foi cadastrado diretamente pelo Gestor de Monitoria. */
	public boolean isCadastradoPorGestor(){
		return ValidatorUtil.isNotEmpty(observacaoPrograd);
	}
	
}
