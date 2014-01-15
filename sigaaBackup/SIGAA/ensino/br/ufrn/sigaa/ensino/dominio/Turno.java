/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.latosensu.dominio.TurmaEntradaLato;
import br.ufrn.sigaa.ensino.tecnico.dominio.EstruturaCurricularTecnica;

/**
 * Entidade que armazena os turnos de aulas que um curso pode dispor suas grade de turmas 
 * 
 */
@Entity
@Table(name = "turno", schema = "ensino", uniqueConstraints = {})
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
public class Turno implements Validatable {

	// Fields

	private int id;

	private String descricao;

	private String sigla;

	private Boolean ativo;

	// Constructors

	public Boolean getAtivo() {
		return ativo;
	}
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	/** default constructor */
	public Turno() {
	}

	/** default minimal constructor */
	public Turno(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public Turno(int idTurno, String descricao) {
		this.id = idTurno;
		this.descricao = descricao;
	}

	/** full constructor */
	public Turno(int idTurno, String descricao,
			Set<EstruturaCurricularTecnica> estruturaCurricularTecnicas,
			Set<TurmaEntradaLato> turmaEntradas, Set<Turma> turmas) {
		this.id = idTurno;
		this.descricao = descricao;
	}

	public Turno(String turnoSigla){
		setSigla(turnoSigla);
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_turno", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idTurno) {
		this.id = idTurno;
	}

	@Column(name = "descricao", unique = false, nullable = false, insertable = true, updatable = true, length = 80)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "descricao");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, descricao);
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descricao", lista);
		ValidatorUtil.validateRequired(sigla, "Sigla", lista);
		ValidatorUtil.validateRequired(ativo, "Ativo", lista);
		return lista;
	}
	
	@Transient
	public boolean isManha() {
		return ( sigla != null && sigla.contains("M") );
	}

	@Transient
	public boolean isTarde() {
		return ( sigla != null && sigla.contains("T") );
	}
	
	@Transient
	public boolean isNoite() {
		return ( sigla != null && sigla.contains("N") );
	}
	
}
