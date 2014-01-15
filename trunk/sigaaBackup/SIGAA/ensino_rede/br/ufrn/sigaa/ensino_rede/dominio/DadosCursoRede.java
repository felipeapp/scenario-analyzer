/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Classe responsável pela associação de cursos a instituições de ensino, 
 * quando este curso está vinculado a rede de ensino entre diversas Instituições de ensino.
 * 
 * @author Henrique André
 * 
 */
@Entity
@Table(schema="ensino_rede", name = "dados_curso_rede")
public class DadosCursoRede implements PersistDB  {

	/** Chave primária */
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

	/** Campus em que o curso será ministrado. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_campus", nullable = false)
	private CampusIes campus;
	
	/** Programa da rede em que os cursos são oferecidos. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_programa_rede", nullable = false)	
	private ProgramaRede programaRede;
	
	/**
	 * Construtor padrão. 
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
