/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 08/04/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino_rede.dominio;

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
import br.ufrn.sigaa.dominio.CampusIes;

/**
 * Classe respons�vel pela associa��o de cursos a institui��es de ensino, 
 * quando este curso est� vinculado a rede de ensino entre diversas Institui��es de ensino.
 * 
 * @author Henrique Andr�
 * 
 */
@Entity
@Table(schema="ensino_rede", name = "dados_curso_rede")
public class DadosCursoRede implements PersistDB  {

	/** Chave prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="ensino_rede.hibernate_sequence") })
	@Column(name = "id_dados_curso_rede", nullable = false)
	private int id;

	/** Curso associado ao campus. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_curso_associado", nullable = false)
	private CursoAssociado curso;

	/** Campus em que o curso ser� ministrado. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_campus", nullable = false)
	private CampusIes campus;
	
	/** Programa da rede em que os cursos s�o oferecidos. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_programa_rede", nullable = false)	
	private ProgramaRede programaRede;
	
	/**
	 * Construtor padr�o. 
	 */
	public DadosCursoRede() {
	}
	
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
		
	}
	
	public CursoAssociado getCurso() {
		return curso;
	}

	public void setCurso(CursoAssociado curso) {
		this.curso = curso;
	}

	public ProgramaRede getProgramaRede() {
		return programaRede;
	}

	public void setProgramaRede(ProgramaRede programaRede) {
		this.programaRede = programaRede;
	}

	public CampusIes getCampus() {
		return campus;
	}

	public void setCampus(CampusIes campus) {
		this.campus = campus;
	}
	
}
