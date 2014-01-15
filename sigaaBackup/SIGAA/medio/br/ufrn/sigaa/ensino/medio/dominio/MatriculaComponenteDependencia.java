/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 01/09/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;

/**
 * Classe responsável pelo relacionamento entre a matricula de Dependência e sua correspondente do tipo regular.
 * 
 * @author Rafael Gomes
 *
 */
@Entity
@Table(name = "matricula_componente_dependencia", schema = "medio", uniqueConstraints = {})
public class MatriculaComponenteDependencia implements PersistDB{

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_matricula_componente_dependencia", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Referência da matricula do tipo regular em disciplina reprovada.*/
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_matricula_regular")
	private MatriculaComponente matriculaRegular;
	
	/** Referência da matricula em disciplina de dependência.*/
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_matricula_dependencia")
	private MatriculaComponente matriculaDependencia;
	
	/** MatriculaDiscenteSerie da matrícula regular, ou matrícula em disciplina reprovada pelo discente.*/ 
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_matricula_serie_regular")
	private MatriculaDiscenteSerie matriculaSerieRegular;
	
	/** Registro de Entrada do usuário de cadastrou a matrícula de dependência do aluno. */	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/** Data de cadastro da matrícula de dependência do aluno. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	// Constructors
	
	/** default constructor */
	public MatriculaComponenteDependencia() {
		super();
	}

	// Getters and Setters

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public MatriculaComponente getMatriculaRegular() {
		return matriculaRegular;
	}
	public void setMatriculaRegular(MatriculaComponente matriculaRegular) {
		this.matriculaRegular = matriculaRegular;
	}
	public MatriculaComponente getMatriculaDependencia() {
		return matriculaDependencia;
	}
	public void setMatriculaDependencia(MatriculaComponente matriculaDependencia) {
		this.matriculaDependencia = matriculaDependencia;
	}
	public MatriculaDiscenteSerie getMatriculaSerieRegular() {
		return matriculaSerieRegular;
	}
	public void setMatriculaSerieRegular(
			MatriculaDiscenteSerie matriculaSerieRegular) {
		this.matriculaSerieRegular = matriculaSerieRegular;
	}
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}
	public Date getDataCadastro() {
		return dataCadastro;
	}
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
}
