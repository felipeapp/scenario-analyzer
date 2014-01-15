/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/03/2011
 *
 */
package br.ufrn.sigaa.ava.forum.dominio;


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

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Entidade respons�vel por armazenar participa��es de usu�rios nos f�runs.
 * 
 * @author Ilueny Santos
 *
 */
@Entity
@Table(name = "forum_curso_docente", schema = "ava")
public class ForumGeralParticipante implements PersistDB {

	/** Identificador �nico para objetos desta classe. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ 
			@Parameter(name="sequence_name", value="hibernate_sequence") })
			@Column(name = "id_forum_curso_docente", nullable = false)
			private int id;

	/** F�rum ao qual o usu�rio est� associado **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_forum")
	private ForumGeral forum;

	/** Usu�rio que est� vinculado ao f�rum */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_usuario")
	private Usuario usuario;
	
	/** Determina se o a participa��o est� ativa. Utilizado para exclus�o l�gica.*/
	@CampoAtivo
	private boolean ativo;

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ForumGeral getForum() {
		return forum;
	}

	public void setForum(ForumGeral forum) {
		this.forum = forum;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}
