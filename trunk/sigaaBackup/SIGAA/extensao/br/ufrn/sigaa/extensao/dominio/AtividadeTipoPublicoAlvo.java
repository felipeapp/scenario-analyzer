/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/10/2006
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.projetos.dominio.TipoPublicoAlvo;

/*******************************************************************************
 * Representa os tipos de publico alvo existente. Uma ação de extensão pode ser
 * direcionada para um tipo de publico especifico.
 * <br>
 * Exemplo: CRIANCAS, ADULTOS, IDOSOS.
 * 
 * 
 * @author Victor Hugo
 * @author Gleydson
 * 
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "atividade_tipo_publico_alvo")
public class AtividadeTipoPublicoAlvo implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_atividade_tipo_publico_alvo", nullable = false)
	private int id;

	//Representa o tipo de público alvo atingido pela atividade.
	@JoinColumn(name = "id_tipo_publico_alvo", referencedColumnName = "id_tipo_publico_alvo")
	@ManyToOne
	private TipoPublicoAlvo tipoPublicoAlvo;

	//Atividade Associada ao tipo de publico alvo.
	@JoinColumn(name = "id_atividade", referencedColumnName = "id_atividade")
	@ManyToOne
	private AtividadeExtensao atividade;

	/** Creates a new instance of AtividadeTipoPublicoAlvo */
	public AtividadeTipoPublicoAlvo() {
	}

	public AtividadeTipoPublicoAlvo(int id) {
		this.id = id;
	}

	public AtividadeTipoPublicoAlvo(int id, TipoPublicoAlvo tipoPublicoAlvo) {
		this.id = id;
		this.tipoPublicoAlvo = tipoPublicoAlvo;
	}

	/**
	 * @return Returns the tipoPublicoAlvo.
	 */
	public TipoPublicoAlvo getTipoPublicoAlvo() {
		return tipoPublicoAlvo;
	}

	/**
	 * @param tipoPublicoAlvo
	 *            The tipoPublicoAlvo to set.
	 */
	public void setTipoPublicoAlvo(TipoPublicoAlvo tipoPublicoAlvo) {
		this.tipoPublicoAlvo = tipoPublicoAlvo;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public AtividadeExtensao getAtividade() {
		return this.atividade;
	}

	public void setAtividade(AtividadeExtensao atividade) {
		this.atividade = atividade;
	}

	public String toString() {
		return "br.ufrn.sigaa.extensao.dominio.AtividadeTipoPublicoAlvo[id="
		+ id + "]";
	}

	/**
	 * @see br.ufrn.arq.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		return null;
	}

}
