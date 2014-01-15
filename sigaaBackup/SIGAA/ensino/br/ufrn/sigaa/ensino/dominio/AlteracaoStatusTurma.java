/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 04/06/2007
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Histórico de mudanças na situação de uma turma
 *
 * @author David Pereira
 *
 */
@Entity
@Table(name="alteracao_status_turma", schema="ensino")
public class AlteracaoStatusTurma implements PersistDB {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int id;

	@ManyToOne
	@JoinColumn(name="id_turma")
	private Turma turma;

	@ManyToOne
	@JoinColumn(name="id_situacao_nova")
	private SituacaoTurma situacaoNova;
	
	@ManyToOne
	@JoinColumn(name="id_situacao_antiga")
	private SituacaoTurma situacaoAntiga;

	@ManyToOne
	@JoinColumn(name="id_usuario")
	private Usuario usuario;

	private Date data;

	@Column(name="cod_movimento")
	private int codMovimento;
	
	/**
	 * @return the data
	 */
	public Date getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Date data) {
		this.data = data;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	public SituacaoTurma getSituacaoNova() {
		return situacaoNova;
	}

	public void setSituacaoNova(SituacaoTurma situacaoNova) {
		this.situacaoNova = situacaoNova;
	}

	public SituacaoTurma getSituacaoAntiga() {
		return situacaoAntiga;
	}

	public void setSituacaoAntiga(SituacaoTurma situacaoAntiga) {
		this.situacaoAntiga = situacaoAntiga;
	}

	public int getCodMovimento() {
		return codMovimento;
	}

	public void setCodMovimento(int codMovimento) {
		this.codMovimento = codMovimento;
	}

	/**
	 * @return the turma
	 */
	public Turma getTurma() {
		return turma;
	}

	/**
	 * @param turma the turma to set
	 */
	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	/**
	 * @return the usuario
	 */
	public Usuario getUsuario() {
		return usuario;
	}

	/**
	 * @param usuario the usuario to set
	 */
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	/**
	 * ATENÇÃO!!! Este método não deve ser invocado diretamente, se quiser criar esta entidade use TurmaHelper.criarAlteracaoStatusTurma(Turma, Movimento)
	 * @param turma
	 * @param usuario
	 * @return
	 */
	public static AlteracaoStatusTurma create(Turma turma, Usuario usuario) {
		AlteracaoStatusTurma alteracao = new AlteracaoStatusTurma();
		alteracao.setData(new Date());
		alteracao.setSituacaoNova(turma.getSituacaoTurma());
		alteracao.setTurma(turma);
		alteracao.setUsuario(usuario);
		return alteracao;
	}

}
