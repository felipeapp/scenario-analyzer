/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 29/10/2007
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * <p>
 * Entidade que representa os tipos de curso de extensão existentes ex:
 * Divulgação, Atualização, Capacitação.
 * </p>
 * 
 * @author Victor Hugo
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "tipo_curso_evento")
public class TipoCursoEventoExtensao implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_tipo_curso_evento", nullable = false)
	private int id;

	@Column(name = "descricao", nullable = false)
	private String descricao;

	@Column(name = "escopo", nullable = false)
	private Character escopo;

	@Column(name = "ch_minima")
	private Integer chMinima;

	private boolean ativo = true;

	@Transient
	private boolean selecionado; // utilizado para selecionar os checkboxes

	/** Creates a new instance of TipoEventoExtensao */
	public TipoCursoEventoExtensao() {
	}

	public TipoCursoEventoExtensao(int id) {
		this.id = id;
	}

	public TipoCursoEventoExtensao(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Character getEscopo() {
		return this.escopo;
	}

	public void setEscopo(Character escopo) {
		this.escopo = escopo;
	}

	public boolean isSelecionado() {
		return this.selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public Integer getChMinima() {
		return this.chMinima;
	}

	public void setChMinima(Integer chMinima) {
		this.chMinima = chMinima;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		ValidatorUtil.validateRequired(chMinima, "Carga Horária Mínima", lista);
		ValidatorUtil.validateRequired(escopo, "Escopo", lista);
		return lista;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(getId());
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

}
