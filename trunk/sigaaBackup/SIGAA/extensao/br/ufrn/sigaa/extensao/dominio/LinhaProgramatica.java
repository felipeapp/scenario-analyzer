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
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * 
 * <p>
 * Todas as ações de extensão deverão sempre ser classificadas também segundo
 * linhas programáticas. Propõe-se que as ações sejam classificadas em uma única
 * linha programática. Segundo o documento de Sistemas de Dados e Informações da
 * Extensão - SIDE, "uma linha, representada por um ou mais programas, pode
 * atender a várias áreas" (SDIE, p.33). Por exemplo, a linha "Capacitação de
 * gestores de políticas públicas" pode estar relacionada às áreas: Saúde,
 * Educação ou Trabalho. <br/>
 * 
 * O Documento de sistemas de dados e informações da extensão lista 50 tipos de
 * linhas programáticas diferentes <br/>
 * 
 * Atualmente, por opção da PROEx, o SIGAA não utiliza linha programática no
 * módulo de extensão.
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
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
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
