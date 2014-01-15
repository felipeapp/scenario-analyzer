/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '26/02/2007'
 *
 */
package br.ufrn.sigaa.pessoa.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;

/**
 * Observações cadastradas para um aluno, a serem exibidas em seu histórico escolar
 *
 * @author Gleydson
 *
 */
@Entity
@Table(name = "observacao_discente", schema = "ensino")
public class ObservacaoDiscente extends AbstractMovimento {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_observacao_discente", nullable = false)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity=Discente.class)
	@JoinColumn(name = "id_discente")
	private DiscenteAdapter discente;

	private String observacao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro")
	private RegistroEntrada registro;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_movimentacao")
	private MovimentacaoAluno movimentacao;

	private Date data;

	private boolean ativo = true;

	@Transient
	private ObservacaoDiscente observacaoAnterior;

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public RegistroEntrada getRegistro() {
		return registro;
	}

	public void setRegistro(RegistroEntrada registro) {
		this.registro = registro;
	}

	public MovimentacaoAluno getMovimentacao() {
		return movimentacao;
	}

	public void setMovimentacao(MovimentacaoAluno movimentacao) {
		this.movimentacao = movimentacao;
	}

	public boolean isAtivo() {
		return this.ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public ObservacaoDiscente getObservacaoAnterior() {
		return this.observacaoAnterior;
	}

	public void setObservacaoAnterior(ObservacaoDiscente observacaoAnterior) {
		this.observacaoAnterior = observacaoAnterior;
	}

}