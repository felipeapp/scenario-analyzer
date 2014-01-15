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

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

 /**
 * Define a utilidade do espaço. Se é uma sala, auditório, laboratório...
 */

@Entity
@Table(name = "tipo_espaco_fisico", schema = "espaco_fisico")
public class TipoEspacoFisico implements Validatable {

	private int id;
	private String denominacao;

	private boolean reservavel;
	private boolean espacoAulas;
	
	private boolean ativo;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_tipo_espaco_fisico")
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

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isReservavel() {
		return reservavel;
	}

	public void setReservavel(boolean reservavel) {
		this.reservavel = reservavel;
	}

	@Column(name = "espaco_aulas")
	public boolean isEspacoAulas() {
		return espacoAulas;
	}

	public void setEspacoAulas(boolean espacoAulas) {
		this.espacoAulas = espacoAulas;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getDenominacao(), "Espaço Físico", lista);
		return lista;
	}
}
