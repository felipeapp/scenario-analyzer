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
import br.ufrn.sigaa.projetos.dominio.TipoPublicoAlvo;

/**
 * Associa os públicos-alvo aos cursos lato sensu
 */
@Entity
@Table(name = "publico_alvo_curso", schema = "lato_sensu", uniqueConstraints = {})
public class PublicoAlvoCurso implements PersistDB {

	// Fields    

	private int id;

	private CursoLato cursoLato;

	private TipoPublicoAlvo tipoPublicoAlvo;

	private String descricao;

	// Constructors

	/** default constructor */
	public PublicoAlvoCurso() {
	}

	/** minimal constructor */
	public PublicoAlvoCurso(int idPublicoAlvo) {
		this.id = idPublicoAlvo;
	}

	/** full constructor */
	public PublicoAlvoCurso(int idPublicoAlvo, CursoLato cursoLato,
			TipoPublicoAlvo tipoPublicoAlvo, String descricao) {
		this.id = idPublicoAlvo;
		this.cursoLato = cursoLato;
		this.tipoPublicoAlvo = tipoPublicoAlvo;
		this.descricao = descricao;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_publico_alvo", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idPublicoAlvo) {
		this.id = idPublicoAlvo;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_curso", unique = false, nullable = true, insertable = true, updatable = true)
	public CursoLato getCursoLato() {
		return this.cursoLato;
	}

	public void setCursoLato(CursoLato cursoLato) {
		this.cursoLato = cursoLato;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_publico_alvo", unique = false, nullable = true, insertable = true, updatable = true)
	public TipoPublicoAlvo getTipoPublicoAlvo() {
		return this.tipoPublicoAlvo;
	}

	public void setTipoPublicoAlvo(TipoPublicoAlvo tipoPublicoAlvo) {
		this.tipoPublicoAlvo = tipoPublicoAlvo;
	}

	@Column(name = "descricao", unique = false, nullable = true, insertable = true, updatable = true, length = 120)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testTransientEquals(this, obj, "id", "tipoPublicoAlvo");
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, tipoPublicoAlvo);
	}

}
