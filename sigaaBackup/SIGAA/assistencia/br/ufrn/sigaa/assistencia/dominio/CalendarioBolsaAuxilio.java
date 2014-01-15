/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 24/07/2008
 *
 */	
package br.ufrn.sigaa.assistencia.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * Entidade que armazena o calendário definido pelo SAE.
 * 
 * @author Gleydson
 * @author Agostinho
 *
 */
@Entity
@Table(name = "calendario_bolsa_auxilio", schema = "sae")
public class CalendarioBolsaAuxilio implements PersistDB, Validatable, Comparable<CalendarioBolsaAuxilio> {

	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name="id")
	private int id;

	/**
	 * Início do período de solicitações de bolsa auxílio
	 */
	private Date inicio;
	
	/**
	 * Até que horas o cadastramento vai estar liberado para os alunos
	 */
	@Column(name="hora_termino")
	private Integer horaTermino;
	
	/**
	 * Até que minutos o cadastramento vai estar liberado para os alunos
	 */
	@Column(name="minuto_termino")
	private Integer minutoTermino; 
	
	/**
	 * Fim do período de solicitações de bolsa auxílio
	 */
	private Date fim;
	
	/**
	 * O município que o SAE liberou para o cadastramento
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_municipio")
	private Municipio municipio;
	
	/**
	 * Tipo da bolsa liberada
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_tipo_bolsa_auxilio")
	private TipoBolsaAuxilio tipoBolsaAuxilio;
	
	/**
	 * Utilizado pelo SAE quando o mesmo deseja disponibilizar os
	 * resultados para os alunos
	 */
	@Column(name="inicio_divulgacao_resultado")
	private Date inicioDivulgacaoResultado;

	/** Data Final para a divulgação do resultado da bolsa */
	@Column(name="fim_divulgacao_resultado")
	private Date fimDivulgacaoResultado;

	/** Data Inicial para a execução da bolsa */
	@Column(name="inicio_execucao_bolsa")
	private Date inicioExecucaoBolsa;
	
	/** Data Fim para a execução da bolsa */
	@Column(name="fim_execucao_bolsa")
	private Date fimExecucaoBolsa;
	
	/**
	 * Usado para indicar se o período de inscrição para determinada bolsa 
	 * permite alunos veteranos solicitarem bolsa 
	 */
	@Column(name="aluno_veterano", nullable=false)
	private boolean alunoVeterano;
	
	/**
	 * Usado para indicar se o período de inscrição para determinada bolsa 
	 * permite alunos novatos solicitarem bolsa 
	 */
	@Column(name="aluno_novato", nullable=false)
	private boolean alunoNovato;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_ano_periodo_referencia", referencedColumnName = "id")
	private AnoPeriodoReferenciaSAE anoReferencia;
	
	@CampoAtivo
	private boolean ativo;
	
	public int getId() {
		return id;
	}

	public Municipio getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	public TipoBolsaAuxilio getTipoBolsaAuxilio() {
		return tipoBolsaAuxilio;
	}

	public void setTipoBolsaAuxilio(TipoBolsaAuxilio tipoBolsaAuxilio) {
		this.tipoBolsaAuxilio = tipoBolsaAuxilio;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public Date getInicioDivulgacaoResultado() {
		return inicioDivulgacaoResultado;
	}

	public void setInicioDivulgacaoResultado(Date inicioDivulgacaoResultado) {
		this.inicioDivulgacaoResultado = inicioDivulgacaoResultado;
	}

	public Date getFimDivulgacaoResultado() {
		return fimDivulgacaoResultado;
	}

	public void setFimDivulgacaoResultado(Date fimDivulgacaoResultado) {
		this.fimDivulgacaoResultado = fimDivulgacaoResultado;
	}

	public AnoPeriodoReferenciaSAE getAnoReferencia() {
		return anoReferencia;
	}

	public void setAnoReferencia(AnoPeriodoReferenciaSAE anoReferencia) {
		this.anoReferencia = anoReferencia;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequiredId(tipoBolsaAuxilio.getId(), "Tipo da Bolsa", lista );
		ValidatorUtil.validateRequired(municipio, "Município", lista);
		ValidatorUtil.validateRequired(inicio, "Data Inicial para Inscrição de Bolsa Auxílio", lista );
		ValidatorUtil.validateRequired(fim, "Data Final para Inscrição de Bolsa Auxílio", lista );
		ValidatorUtil.validateRequired(inicioExecucaoBolsa, "Data Inicial para Execução de Bolsa Auxílio", lista);
		ValidatorUtil.validateRequired(fimExecucaoBolsa, "Data Final para Execução de Bolsa Auxílio", lista);

		ValidatorUtil.validaOrdemTemporalDatas(inicio, fim, true, "Inscrição", lista);
		ValidatorUtil.validaOrdemTemporalDatas(inicioDivulgacaoResultado, fimDivulgacaoResultado, true, "Resultados", lista);
		ValidatorUtil.validaOrdemTemporalDatas(inicioExecucaoBolsa, fimExecucaoBolsa, true, "Execução", lista);
		
		
		if (inicioDivulgacaoResultado == null && fimDivulgacaoResultado != null)
			lista.addErro("Informe o início da Data de Divulgação de Resultados");
		else if (inicioDivulgacaoResultado != null && fimDivulgacaoResultado == null)
			lista.addErro("Informe o fim da Data de Divulgação de Resultados");
		// Valida as datas informadas em ordem crescente: inscrição, divulgação de resultados, execução
		if (fim != null && inicioDivulgacaoResultado != null){
			if (inicioDivulgacaoResultado.before(CalendarUtils.adicionaUmDia(fim)))
				lista.addErro("Início da Data de Divulgação dos Resultados: esta data deve ser posterior à Data de Fim De Inscrição");
			ValidatorUtil.validaDataAnteriorIgual(fim, inicioDivulgacaoResultado, "Fim da Data de Inscrição", lista);
		}
		if (fimDivulgacaoResultado != null && inicioExecucaoBolsa != null)
			ValidatorUtil.validaDataAnteriorIgual(fimDivulgacaoResultado, inicioExecucaoBolsa, "Fim da Data de Divulgação de Resultados", lista);
		if (fim != null && inicioExecucaoBolsa != null)
			ValidatorUtil.validaDataAnteriorIgual(fim, inicioExecucaoBolsa, "Fim da Data de Inscrição", lista);

		// Valida "Hora término" (caso esse campo seja informado)
		if (horaTermino != null && minutoTermino != null){
			ValidatorUtil.validaHora(String.valueOf(horaTermino) + ":" + String.valueOf(minutoTermino), "Hora término", lista);
		}

		return lista;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fim == null) ? 0 : fim.hashCode());
        result = prime
                * result
                + ((fimDivulgacaoResultado == null) ? 0
                        : fimDivulgacaoResultado.hashCode());
        result = prime * result + id;
        result = prime * result + ((inicio == null) ? 0 : inicio.hashCode());
        result = prime
                * result
                + ((inicioDivulgacaoResultado == null) ? 0
                        : inicioDivulgacaoResultado.hashCode());
        result = prime * result
                + ((municipio == null) ? 0 : municipio.hashCode());
        result = prime
                * result
                + ((tipoBolsaAuxilio == null) ? 0 : tipoBolsaAuxilio.hashCode());
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
        CalendarioBolsaAuxilio other = (CalendarioBolsaAuxilio) obj;
        if (fim == null) {
            if (other.fim != null)
                return false;
        } else if (!fim.equals(other.fim))
            return false;
        if (fimDivulgacaoResultado == null) {
            if (other.fimDivulgacaoResultado != null)
                return false;
        } else if (!fimDivulgacaoResultado.equals(other.fimDivulgacaoResultado))
            return false;
        if (id != other.id)
            return false;
        if (inicio == null) {
            if (other.inicio != null)
                return false;
        } else if (!inicio.equals(other.inicio))
            return false;
        if (inicioDivulgacaoResultado == null) {
            if (other.inicioDivulgacaoResultado != null)
                return false;
        } else if (!inicioDivulgacaoResultado
                .equals(other.inicioDivulgacaoResultado))
            return false;
        if (municipio == null) {
            if (other.municipio != null)
                return false;
        } else if (!municipio.equals(other.municipio))
            return false;
        if (tipoBolsaAuxilio == null) {
            if (other.tipoBolsaAuxilio != null)
                return false;
        } else if (!tipoBolsaAuxilio.equals(other.tipoBolsaAuxilio))
            return false;
        return true;
    }

	public boolean isAlunoVeterano() {
		return alunoVeterano;
	}

	public void setAlunoVeterano(boolean alunoVeterano) {
		this.alunoVeterano = alunoVeterano;
	}

	public boolean isAlunoNovato() {
		return alunoNovato;
	}

	public void setAlunoNovato(boolean alunoNovato) {
		this.alunoNovato = alunoNovato;
	}

	public Integer getHoraTermino() {
		return horaTermino;
	}

	public void setHoraTermino(Integer horaTermino) {
		this.horaTermino = horaTermino;
	}

	public Integer getMinutoTermino() {
		return minutoTermino;
	}

	public void setMinutoTermino(Integer minutoTermino) {
		this.minutoTermino = minutoTermino;
	}

	/** Compara este Calendario com o passado, ordenando-os por nome do município. */
	public int compareTo(CalendarioBolsaAuxilio o) {
		return new CompareToBuilder().append(this.getMunicipio().getNome(), o.getMunicipio().getNome()).toComparison();
	}

	public Date getInicioExecucaoBolsa() {
		return inicioExecucaoBolsa;
	}

	public void setInicioExecucaoBolsa(Date inicioExecucaoBolsa) {
		this.inicioExecucaoBolsa = inicioExecucaoBolsa;
	}

	public Date getFimExecucaoBolsa() {
		return fimExecucaoBolsa;
	}

	public void setFimExecucaoBolsa(Date fimExecucaoBolsa) {
		this.fimExecucaoBolsa = fimExecucaoBolsa;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
}