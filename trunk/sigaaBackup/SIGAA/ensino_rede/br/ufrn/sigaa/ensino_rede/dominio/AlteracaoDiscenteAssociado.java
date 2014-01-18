/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 30/08/2013
 *
 */
package br.ufrn.sigaa.ensino_rede.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Date;

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

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;

/**
 * Registra as alteraçoes de discente associado a um programa de ensino em rede.
 * @author Édipo Elder F. de Melo
 *
 */
@Entity
@Table(name = "alteracao_discente_associado", schema = "ensino_rede")
public class AlteracaoDiscenteAssociado implements PersistDB {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="ensino_rede.alteracao_status_discente_associado_seq") })
	@Column(name = "id_alteracao_discente_associado", nullable=false)
	private int id;

	/** Registro de Entrada do usuário que alterou */
	@ManyToOne
	@JoinColumn(name = "id_registro_entrada", nullable=false)
	private RegistroEntrada registroEntrada;
	
	/** Discente que teve o status alterado */
	@ManyToOne
	@JoinColumn(name = "id_discente_associado", nullable=false)
	private DiscenteAssociado discenteAssociado;

	/** Data da alteração */
	@Temporal(TemporalType.TIMESTAMP)
	private Date data;

	/** Código do movimento que está alterando o status do aluno */
	@Column(name = "codigo_movimento", nullable=false)
	private int codigoMovimento;
	
	/** Ano de ingresso anterior do discente */
	@Column(name = "ano_ingresso_anterior")
	private Integer anoIngressoAnterior;
	
	/** Período de ingresso do discente */
	@Column(name = "periodo_ingresso_anterior")
	private Integer periodoIngressoAnterior;
	
	/** Dados do Curso anterior do discente. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_dados_curso_rede_anterior")
	private DadosCursoRede dadosCursoAnterior;

	/** Nível de ensino anterior. */
	@Column(name = "nivel_anterior")
	private Character nivelAnterior;
	
	/** Indica se houve alteração nos dados do discente. */
	@Transient
	private boolean houveAlteracao;

	public AlteracaoDiscenteAssociado() {
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public DiscenteAssociado getDiscenteAssociado() {
		return discenteAssociado;
	}

	public void setDiscenteAssociado(DiscenteAssociado discenteAssociado) {
		this.discenteAssociado = discenteAssociado;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getCodigoMovimento() {
		return codigoMovimento;
	}

	public void setCodigoMovimento(int codigoMovimento) {
		this.codigoMovimento = codigoMovimento;
	}

	public Integer getAnoIngressoAnterior() {
		return anoIngressoAnterior;
	}

	public void setAnoIngressoAnterior(Integer anoIngressoAnterior) {
		this.anoIngressoAnterior = anoIngressoAnterior;
	}

	public Integer getPeriodoIngressoAnterior() {
		return periodoIngressoAnterior;
	}

	public void setPeriodoIngressoAnterior(Integer periodoIngressoAnterior) {
		this.periodoIngressoAnterior = periodoIngressoAnterior;
	}

	public DadosCursoRede getDadosCursoAnterior() {
		return dadosCursoAnterior;
	}

	public void setDadosCursoAnterior(DadosCursoRede dadosCursoAnterior) {
		this.dadosCursoAnterior = dadosCursoAnterior;
	}
	
	public void populaDiferencas(DiscenteAssociado discenteAtual, DiscenteAssociado discenteAlterado) {
		houveAlteracao = false;
		if (isEmpty(discenteAlterado) || isEmpty(discenteAtual)) return;
		this.discenteAssociado = discenteAtual;
		if (discenteAtual.getAnoIngresso() != discenteAlterado.getAnoIngresso()) {
			this.anoIngressoAnterior = discenteAtual.getAnoIngresso();
			houveAlteracao = true;
		} else this.anoIngressoAnterior = null;
		if (discenteAtual.getPeriodoIngresso() != discenteAlterado.getPeriodoIngresso()) {
			this.periodoIngressoAnterior = discenteAtual.getPeriodoIngresso();
			houveAlteracao = true;
		} else this.periodoIngressoAnterior = null;
		if (discenteAtual.getDadosCurso().getId() != discenteAlterado.getDadosCurso().getId()) {
			this.dadosCursoAnterior = discenteAtual.getDadosCurso();
			houveAlteracao = true;
		} else this.dadosCursoAnterior = null;
		if (discenteAtual.getNivel() != discenteAlterado.getNivel()) {
			this.nivelAnterior = discenteAtual.getNivel();
			houveAlteracao = true;
		} else this.nivelAnterior = null;
	}

	public boolean isHouveAlteracao() {
		return houveAlteracao;
	}

	public Character getNivelAnterior() {
		return nivelAnterior;
	}

	public void setNivelAnterior(Character nivelAnterior) {
		this.nivelAnterior = nivelAnterior;
	}
}
