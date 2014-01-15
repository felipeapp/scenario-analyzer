/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/01/2011
 *
 */
package br.ufrn.sigaa.ava.dominio;

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
 * Entidade respons�vel por armazenar os forum que os docentes possuem acesso.
 * 
 * @author arlindo
 *
 */
@Entity
@Table(name = "forum_curso_docente", schema = "ava")
public class ForumCursoDocente implements PersistDB {
	
	/** Chave primaria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ 
			@Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_forum_curso_docente", nullable = false)
	private int id;

	/** F�rum ao qual o docente est� associado **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_forum")
	private Forum forum;
	
	/** Servidor que est� vinculado ao forum */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_servidor")
	private Servidor servidor;	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Forum getForum() {
		return forum;
	}

	public void setForum(Forum forum) {
		this.forum = forum;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}
}
