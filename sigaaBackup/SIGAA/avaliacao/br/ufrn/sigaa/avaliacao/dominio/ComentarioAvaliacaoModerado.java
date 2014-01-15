/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 11/05/2010
 * 
 */
package br.ufrn.sigaa.avaliacao.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe que indica quais {@link ObservacoesDocenteTurma} e
 * {@link ObservacoesTrancamento} foram moderadas pela Comissão Própria de
 * Avaliacao (CPA) e liberados para consulta no SIGAA.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
@Entity @Table(name="comentario_avaliacao_moderado", schema="avaliacao")
public class ComentarioAvaliacaoModerado implements PersistDB {

	/** Chave primária. */
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="avaliacao_seq")
	@SequenceGenerator(name = "avaliacao_seq", sequenceName = "avaliacao.avaliacao_seq", allocationSize=1)
	@Column(name="id_moderacao_comentario")
	private int id;
	
	/** DocenteTurma que teve os comentários moderados finalizados. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_docente_turma")
	private DocenteTurma docenteTurma;
	
	/** Turma que teve as observações moderadas sobre trancamento finalizado. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_turma")
	private Turma turma;
	
	/** Registro de Entrada do usuário que finalizou a moderação das observações. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_entrada")
	private RegistroEntrada registro;
	
	/** Data da última atualização. */
	@Column(name="data_atualizacao")
	private Date dataAtualizacao;
	
	/** Caso true, indica que a observação moderada é foi realizada pelo discentes aos docentes da turma. Caso false, indica que a observação moderada foi realizada pelo docente para a turma. */
	@Column(name="observacao_discente")
	private boolean observacaoDiscente;

	/** Retorna a chave primária. 
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave primária. 
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna o docenteTurma que teve os comentários moderados finalizados.
	 * @return
	 */
	public DocenteTurma getDocenteTurma() {
		return docenteTurma;
	}

	/** Seta o docenteTurma que teve os comentários moderados finalizados.
	 * @param docenteTurma
	 */
	public void setDocenteTurma(DocenteTurma docenteTurma) {
		this.docenteTurma = docenteTurma;
	}

	/** Retorna a turma que teve as observações moderadas sobre trancamento finalizado.
	 * @return
	 */
	public Turma getTurma() {
		return turma;
	}

	/** Seta a turma que teve as observações moderadas sobre trancamento finalizado.
	 * @param turma
	 */
	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	/** Retorna o Registro de Entrada do usuário que finalizou a moderação das observações
	 * @return
	 */
	public RegistroEntrada getRegistro() {
		return registro;
	}

	/** Seta o Registro de Entrada do usuário que finalizou a moderação das observações
	 * @param registro
	 */
	public void setRegistro(RegistroEntrada registro) {
		this.registro = registro;
	}

	/** Retorna a data da última atualização.
	 * @return
	 */
	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	/** Seta a data da última atualização.
	 * @param dataAtualizacao
	 */
	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	/** Caso true, indica que a observação moderada é foi realizada pelo discentes aos docentes da turma. Caso false, indica que a observação moderada foi realizada pelo docente para a turma. 
	 * @return
	 */
	public boolean isObservacaoDiscente() {
		return observacaoDiscente;
	}
	
	/** Caso true, indica que a observação moderada é foi realizada pelo discentes aos docentes da turma. Caso false, indica que a observação moderada foi realizada pelo docente para a turma. 
	 * @param observacaoDocenteTurma
	 */
	public void setObservacaoDiscente(boolean observacaoDiscente) {
		this.observacaoDiscente = observacaoDiscente;
	}
	
	/** Retorna uma descrição textual da observação moderada.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String retorno = "";
		if (turma != null)
			retorno = "Observações sobre trancamento da turma: " + turma.toString();
		else if (docenteTurma != null)
			retorno = "Observações sobre o docente: " + docenteTurma.getDocenteNome();
		else retorno = "Sem informações";
		return retorno;
	}
}
