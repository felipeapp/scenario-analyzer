/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
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
 * Tabela que define o vinculo dos participantes nos programas de pós-graduação stricto sensu.
 * Ex: professor, pesquisador 
 * @author Gleydson
 */
@Entity
@Table(name = "vinculo_equipe", schema = "stricto_sensu")
public class VinculoEquipe implements PersistDB {

	public static VinculoEquipe PROFESSOR = new VinculoEquipe(1);
	public static VinculoEquipe PESQUISADOR = new VinculoEquipe(2);

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_vinculo_equipe", nullable = false)
	private int id;

	/** denominação do vinculo */
	private String denominacao;

	public VinculoEquipe() {

	}

	public VinculoEquipe(int i) {
		id = i;
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

	@Override
	public String toString() {
		return denominacao;
	}

}
