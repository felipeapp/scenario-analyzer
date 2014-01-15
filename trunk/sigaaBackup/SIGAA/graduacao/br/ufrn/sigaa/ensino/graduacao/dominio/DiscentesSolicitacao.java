/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/06/2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade de relacionamento entre SolicitacaoTurma e DiscenteGraduacao.
 * É utilizado no caso de uma solicitação de curso especial de férias no qual deve conter uma
 * lista dos alunos interessados na turma em questão
 * Ou no caso de solicitações de turmas de ensino individual que também possui uma lista de discentes interessados.
 * @author Victor Hugo
 *
 */
@Entity
@Table(name = "discentes_solicitacao", schema="graduacao")
public class DiscentesSolicitacao implements PersistDB, Comparable<DiscentesSolicitacao> {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_discentes_solicitacao", nullable = false)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_solicitacao_turma", unique = false, nullable = true, insertable = true, updatable = true)
	private SolicitacaoTurma solicitacaoTurma;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente_graduacao", unique = false, nullable = true, insertable = true, updatable = true)
	private DiscenteGraduacao discenteGraduacao;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DiscenteGraduacao getDiscenteGraduacao() {
		return discenteGraduacao;
	}

	public void setDiscenteGraduacao(DiscenteGraduacao discenteGraduacao) {
		this.discenteGraduacao = discenteGraduacao;
	}

	public SolicitacaoTurma getSolicitacaoTurma() {
		return solicitacaoTurma;
	}

	public void setSolicitacaoTurma(SolicitacaoTurma solicitacaoTurma) {
		this.solicitacaoTurma = solicitacaoTurma;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((discenteGraduacao == null) ? 0 : discenteGraduacao
						.hashCode());
		result = prime * result + id;
		result = prime
				* result
				+ ((solicitacaoTurma == null) ? 0 : solicitacaoTurma.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj != null) {
			DiscentesSolicitacao other = (DiscentesSolicitacao) obj;
			if (other.id == this.id)
				return true;
			if (other.getSolicitacaoTurma().equals(this.solicitacaoTurma) && other.getDiscenteGraduacao().equals(this.getDiscenteGraduacao()))
				return true;
		}

		return false;

	}

	public int compareTo(DiscentesSolicitacao obj) {
		int result = 0;
		if( this.discenteGraduacao != null && obj.getDiscenteGraduacao() != null ){
			
			if( this.discenteGraduacao.getCurso() != null && obj.discenteGraduacao.getCurso() != null )
				result = this.discenteGraduacao.getCurso().getDescricao().compareTo( obj.getDiscenteGraduacao().getCurso().getDescricao() );
			
			if( result == 0 )
				result = this.discenteGraduacao.getNome().compareTo( obj.getDiscenteGraduacao().getNome() );
		}
		return result;
	}

}
