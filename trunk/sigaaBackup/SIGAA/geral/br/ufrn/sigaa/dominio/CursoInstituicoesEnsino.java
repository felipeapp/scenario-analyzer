/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 08/04/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.dominio;

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

/**
 * Classe responsável pela associação de cursos a instituições de ensino, 
 * quando este curso está vinculado a rede de ensino entre diversas Instituições de ensino.
 * 
 * @author Rafael Gomes
 *
 */

@Entity
@Table(name = "curso_instituicoes_ensino", schema="comum")
public class CursoInstituicoesEnsino implements PersistDB{

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	@Column(name = "id_curso_instituicoes_ensino", nullable = false)
	private int id;

	@ManyToOne
	@JoinColumn(name = "id_curso", nullable = false)
	private Curso curso;

	@ManyToOne
	@JoinColumn(name = "id_instituicoes_ensino", nullable = false)
	private InstituicoesEnsino instituicaoEnsino;
	
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
		
	}

	/**
	 * @return the curso
	 */
	public Curso getCurso() {
		return curso;
	}

	/**
	 * @param curso the curso to set
	 */
	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	/**
	 * @return the instituicaoEnsino
	 */
	public InstituicoesEnsino getInstituicaoEnsino() {
		return instituicaoEnsino;
	}

	/**
	 * @param instituicaoEnsino the instituicaoEnsino to set
	 */
	public void setInstituicaoEnsino(InstituicoesEnsino instituicaoEnsino) {
		this.instituicaoEnsino = instituicaoEnsino;
	}

}
