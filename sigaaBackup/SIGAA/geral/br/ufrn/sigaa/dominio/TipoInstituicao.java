/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '25/04/2007'
 *
 */
package br.ufrn.sigaa.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Mapeia os tipos de institui��es que abrigam os docentes na UFRN. Essa
 * entidade foi criada pois v�rios docentes eram oriundos de �rg�os estaduais,
 * federais,etc que n�o eram institui��es de ensino.
 *
 * @author Gleydson
 *
 */
@Entity
@Table(schema="comum", name = "tipo_instituicao")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class TipoInstituicao implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_tipo_instituicao", nullable = false)
	private int id;

	private String denominacao;

	public static final int TIPO_ENSINO = 1;

	public TipoInstituicao() {
	}

	public TipoInstituicao(int i) {
		this.id = i;
	}

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
