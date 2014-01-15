/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Feb 13, 2008
 *
 */
package br.ufrn.sigaa.ensino.stricto.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Tabela que define o n�vel dos participantes nos programas de p�s-gradua��o stricto sensu.
 * Pode ser: Permanente e colaborador
 * @author Victor Hugo
 */
@Entity
@Table(name = "nivel_equipe", schema = "stricto_sensu")
public class NivelEquipe implements PersistDB {

	public static NivelEquipe PLENO = new NivelEquipe(1);
	public static NivelEquipe COLABORADOR = new NivelEquipe(2);

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_nivel_equipe", nullable = false)
	private int id;

	/** a denomina��o do n�vel */
	private String denominacao;

	public NivelEquipe() {
	}

	public NivelEquipe(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

	@Override
	public String toString() {
		return denominacao;
	}

}
