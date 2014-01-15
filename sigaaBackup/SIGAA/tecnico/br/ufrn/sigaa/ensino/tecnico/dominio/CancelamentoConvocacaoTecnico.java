/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.MotivoCancelamentoConvocacao;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivoDiscente;

/**
 * Registra o cancelamento de uma convocação do vestibular.
 * 
 * @author Leonardo Campos
 * @author Fred_Castro
 *
 */
@Entity
@Table(name = "cancelamento_convocacao_tecnico", schema = "tecnico", uniqueConstraints = {})
public class CancelamentoConvocacaoTecnico implements Validatable {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_cancelamento_convocacao", nullable = false)
	private int id;

	/** {@link ConvocacaoProcessoSeletivoDiscente Convocação} que foi cancelada. */
	@ManyToOne
	@JoinColumn(name="id_convocacao_processo_seletivo_discente")
	private ConvocacaoProcessoSeletivoDiscenteTecnico convocacao;
	
	/** {@link MotivoCancelamentoConvocacao Motivo} do cancelamento. */
	@ManyToOne
	@JoinColumn(name="id_motivo_cancelamento_convocacao")
	private MotivoCancelamentoConvocacaoTecnico motivo;
	
	/** {@link MovimentacaoAluno Movimentação de cancelamento} do aluno. */
	@ManyToOne
	@JoinColumn(name="id_movimentacao_aluno")
	private MovimentacaoAluno movimentacaoCancelamento;
	
	/** {@link RegistroEntrada Registro de Entrada} do usuário que cadastrou o cancelamento. */
	@CriadoPor
	@ManyToOne
	@JoinColumn(name = "id_registro_cadastro")
	private RegistroEntrada registroCadastro;
	
	/** Data em que foi realizado o cancelamento. */
	@CriadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cancelamento")
	private Date dataCancelamento;
	
	private String observacoes;
	
	public CancelamentoConvocacaoTecnico() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public MotivoCancelamentoConvocacaoTecnico getMotivo() {
		return motivo;
	}

	public void setMotivo(MotivoCancelamentoConvocacaoTecnico motivo) {
		this.motivo = motivo;
	}

	public MovimentacaoAluno getMovimentacaoCancelamento() {
		return movimentacaoCancelamento;
	}

	public void setMovimentacaoCancelamento(
			MovimentacaoAluno movimentacaoCancelamento) {
		this.movimentacaoCancelamento = movimentacaoCancelamento;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

	@Override
	public ListaMensagens validate() {
		return null;
	}

	public ConvocacaoProcessoSeletivoDiscenteTecnico getConvocacao() {
		return convocacao;
	}

	public void setConvocacao(ConvocacaoProcessoSeletivoDiscenteTecnico convocacao) {
		this.convocacao = convocacao;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}
}