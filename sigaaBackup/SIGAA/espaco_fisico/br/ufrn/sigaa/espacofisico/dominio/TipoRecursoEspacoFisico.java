/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/03/2009
 *
 */
package br.ufrn.sigaa.espacofisico.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.dominio.GenericTipo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Recurso que pode conter no espaço físico. Ar-condicionado, projetor, etc...
 * 
 * @author Gleydson
 * 
 */

@Entity
@Table(name = "tipo_recurso_espaco_fisico", schema = "espaco_fisico")
public class TipoRecursoEspacoFisico extends GenericTipo implements Validatable {

	/**
	 * Indica se foi removido ou não
	 */
	private boolean ativo = true;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_tipo_recurso_espaco_fisico")
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "denominacao")
	public String getDenominacao() {
		return super.getDenominacao();
	}
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(denominacao);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "denominacao", "ativo");
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getDenominacao(), "Recurso", lista);
		return lista;
	}
}
