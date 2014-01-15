/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '13/09/2006'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.dominio;

// Generated 13/09/2006 08:49:22 by Hibernate Tools 3.1.0.beta5

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Entidade que registra todos os professores que compõem o corpo docente de um curso lato sensu
 */
@Entity
@Table(name = "curso_servidor", schema = "lato_sensu", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"id_curso", "id_servidor" }) })
public class CorpoDocenteCursoLato implements PersistDB {

	// Fields

	private int id;

	private CursoLato cursoLato = new CursoLato();

	private Servidor servidor = new Servidor();

	private DocenteExterno docenteExterno = new DocenteExterno();

	// Transients

	private int cursoServidorId;

	private String linkCurriculoLattes; 
	
	// Constructors

	/** default constructor */
	public CorpoDocenteCursoLato() {
	}

	/** minimal constructor */
	public CorpoDocenteCursoLato(int idCursoServidor) {
		this.id = idCursoServidor;
	}

	/** full constructor */
	public CorpoDocenteCursoLato(int idCursoServidor, CursoLato cursoLato,
			Servidor servidor) {
		this.id = idCursoServidor;
		this.cursoLato = cursoLato;
		this.servidor = servidor;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_curso_servidor", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idCursoServidor) {
		this.id = idCursoServidor;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_curso", unique = false, nullable = true, insertable = true, updatable = true)
	public CursoLato getCursoLato() {
		return this.cursoLato;
	}

	public void setCursoLato(CursoLato cursoLato) {
		this.cursoLato = cursoLato;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_servidor", unique = false, nullable = true, insertable = true, updatable = true)
	public Servidor getServidor() {
		return this.servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "servidor");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, servidor);
	}

	/**
	 * @return the docenteExterno
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_docente_externo", unique = false, nullable = true, insertable = true, updatable = true)
	public DocenteExterno getDocenteExterno() {
		return docenteExterno;
	}

	/**
	 * @param docenteExterno the docenteExterno to set
	 */
	public void setDocenteExterno(DocenteExterno docenteExterno) {
		this.docenteExterno = docenteExterno;
	}

	@Transient
	public boolean getExterno() {
		if( getDocenteExterno() != null )
			return true;
		return false;
	}

	@Transient
	public String getNome() {
		if( getExterno() )
			return getDocenteExterno().getPessoa().getNome();
		return getServidor().getNome();
	}

	@Transient
	public int getCursoServidorId() {
		return cursoServidorId;
	}

	public void setCursoServidorId(int cursoServidorId) {
		this.cursoServidorId = cursoServidorId;
	}

	@Transient
	public String getLinkCurriculoLattes() {
		return linkCurriculoLattes;
	}

	public void setLinkCurriculoLattes(String linkCurriculoLattes) {
		this.linkCurriculoLattes = linkCurriculoLattes;
	}

}
