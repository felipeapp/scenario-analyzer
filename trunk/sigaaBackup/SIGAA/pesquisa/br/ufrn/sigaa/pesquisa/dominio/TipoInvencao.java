/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/05/2008
 *
 */

package br.ufrn.sigaa.pesquisa.dominio;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Poss�veis tipos assumidos pelos projetos de inova��o
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name="tipo_invencao", schema="pesquisa", uniqueConstraints = {})
public class TipoInvencao implements Validatable {
	
	/** Constantes dos tipos de notifica��o de inven��o */
	public static final int PATENTE = 1;
	public static final int MODELO_UTILIDADE = 2;
	public static final int DESENHO_INDUSTRIAL = 3;
	public static final int MARCA = 4;
	
	/** Constantes das categorias de patentes */
	public static final int PRODUTO = 1;
	public static final int PROCESSO = 2;

	private static Map<Integer, String> categorias = new HashMap<Integer, String>();

	static {
		categorias.put(PRODUTO, "Produto");
		categorias.put(PROCESSO, "Processo");
	}

	/** Chave prim�ria */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name="id_tipo_invencao")
	private int id;
	
	/** Descri��o referente ao Tipo de Inven��o */
	private String descricao;
	
	/** Indicar se a o Tipo de Inven��o est� em uso */
	private boolean ativo;
	

	/** default constructor */
	public TipoInvencao() {
		
	}

	/** Respons�vel por retornar a chave prim�ria */
	public int getId() {
		return id;
	}

	/** Respons�vel por setar a chave prim�ria */
	public void setId(int id) {
		this.id = id;
	}

	/** Respons�vel por retornar a Descri��o */
	public String getDescricao() {
		return descricao;
	}

	/** Respons�vel por setar a Descri��o */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	/** Respons�vel por retornar um boleano indicando se o tipo de inven��o est� em uso */
	public boolean isAtivo() {
		return ativo;
	}
	
	/** Respons�vel por setar um boleano indicando se o tipo de inven��o est� em uso */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Respons�vel por retornar a descri��o da Categoria */
	public static String getDescricaoCategoria(int categoria) {
		return categorias.get(categoria);
	}

	/** Respons�vel por retornar a Categoria */
	public static Map<Integer, String> getCategorias() {
		return categorias;
	}

	/** Respons�vel pela realiza��o da valida��es do Tipo de Inven��o */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descri��o", erros);
		return erros;
	}
}