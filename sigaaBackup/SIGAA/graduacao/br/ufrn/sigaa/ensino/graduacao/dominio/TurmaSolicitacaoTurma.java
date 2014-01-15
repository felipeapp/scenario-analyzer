/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Jul 4, 2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe de relacionamento NxN entre Turma e SolicitacaoTurma
 * para ter referencia de quais solicitações originaram quais turmas
 * @author Victor Hugo
 *
 */
@Entity
@Table(name = "turma_solicitacao_turma", schema="graduacao")
public class TurmaSolicitacaoTurma implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_turma_solicitacao_turma", nullable = false)
	private int id;

	@ManyToOne
	@JoinColumn(name = "id_solicitacao", nullable = false)
	private SolicitacaoTurma solicitacao;

	@ManyToOne
	@JoinColumn(name = "id_turma", nullable = false)
	private Turma turma;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public SolicitacaoTurma getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(SolicitacaoTurma solicitacao) {
		this.solicitacao = solicitacao;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

}
