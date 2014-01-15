/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/09/2011
 *
 */
package br.ufrn.sigaa.ensino.medio.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Entidade responsável por armazenar os forum que os docentes possuem acesso.
 * 
 * @author Rafael Gomes
 *
 */
@Entity
@Table(name = "forum_medio_turma_docente", schema = "medio")
public class ForumMedioTurmaDocente implements PersistDB {
	
	/** Chave primaria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ 
			@Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_forum_medio_turma_docente", nullable = false)
	private int id;

	/** Fórum ao qual o docente está associado **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_forum_medio")
	private ForumMedio forum;
	
	/** Servidor que está vinculado ao forum */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_servidor")
	private Servidor servidor;	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ForumMedio getForum() {
		return forum;
	}

	public void setForum(ForumMedio forum) {
		this.forum = forum;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}
}
