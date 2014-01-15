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

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Classe que registra os trabalhos finais dos alunos de cursos de pós-graduação lato sensu
 */
@Entity
@Table(name = "trabalho_final_lato", schema = "lato_sensu", uniqueConstraints = {})
public class TrabalhoFinalLato implements PersistDB {

	// Fields

	private int id;

	private DiscenteLato discenteLato = new DiscenteLato();

	private Servidor servidor = new Servidor();

	private String titulo;

	private double conceito;
	
	private Integer idArquivo;

	// Constructors

	/** default constructor */
	public TrabalhoFinalLato() {
	}

	/** minimal constructor */
	public TrabalhoFinalLato(int idTrabalhoFinal) {
		this.id = idTrabalhoFinal;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_trabalho_final", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idTrabalhoFinal) {
		this.id = idTrabalhoFinal;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente", unique = false, nullable = true, insertable = true, updatable = true)
	public DiscenteLato getDiscenteLato() {
		return this.discenteLato;
	}

	public void setDiscenteLato(DiscenteLato discenteLato) {
		this.discenteLato = discenteLato;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_servidor", unique = false, nullable = true, insertable = true, updatable = true)
	public Servidor getServidor() {
		return this.servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	@Column(name = "titulo", unique = false, nullable = true, insertable = true, updatable = true, length = 150)
	public String getTitulo() {
		return this.titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	@Column(name = "conceito", unique = false, nullable = true, insertable = true, updatable = true, precision = 2, scale = 1)
	public double getConceito() {
		return this.conceito;
	}

	public void setConceito(double conceito) {
		this.conceito = conceito;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	@Column(name = "id_arquivo")
	public Integer getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

}
