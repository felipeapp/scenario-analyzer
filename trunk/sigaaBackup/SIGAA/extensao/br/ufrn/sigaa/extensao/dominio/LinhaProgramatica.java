/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * 
 * <p>
 * Todas as a��es de extens�o dever�o sempre ser classificadas tamb�m segundo
 * linhas program�ticas. Prop�e-se que as a��es sejam classificadas em uma �nica
 * linha program�tica. Segundo o documento de Sistemas de Dados e Informa��es da
 * Extens�o - SIDE, "uma linha, representada por um ou mais programas, pode
 * atender a v�rias �reas" (SDIE, p.33). Por exemplo, a linha "Capacita��o de
 * gestores de pol�ticas p�blicas" pode estar relacionada �s �reas: Sa�de,
 * Educa��o ou Trabalho. <br/>
 * 
 * O Documento de sistemas de dados e informa��es da extens�o lista 50 tipos de
 * linhas program�ticas diferentes <br/>
 * 
 * Atualmente, por op��o da PROEx, o SIGAA n�o utiliza linha program�tica no
 * m�dulo de extens�o.
 * </p>
 * 
 * @author Ilueny Santos
 * @author Gleydson
 * @author Victor Hugo
 * 
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "linha_programatica")
public class LinhaProgramatica implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_linha_programatica", nullable = false)
	private int id;

	@Column(name = "descricao")
	private String descricao;

	@Transient
	private boolean selecionado; // para controle do jsf

	/** Creates a new instance of LinhaProgramatica */
	public LinhaProgramatica() {
	}

	public LinhaProgramatica(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String toString() {
		return descricao;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descri��o", lista);
		return lista;
	}

	/**
	 * @return Returns the selecionado.
	 */
	public boolean isSelecionado() {
		return selecionado;
	}

	/**
	 * @param selecionado
	 *            The selecionado to set.
	 */
	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

}
