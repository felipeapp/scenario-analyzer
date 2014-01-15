/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Possíveis tipos assumidos pelos projetos de inovação
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name="tipo_invencao", schema="pesquisa", uniqueConstraints = {})
public class TipoInvencao implements Validatable {
	
	/** Constantes dos tipos de notificação de invenção */
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

	/** Chave primária */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name="id_tipo_invencao")
	private int id;
	
	/** Descrição referente ao Tipo de Invenção */
	private String descricao;
	
	/** Indicar se a o Tipo de Invenção está em uso */
	private boolean ativo;
	

	/** default constructor */
	public TipoInvencao() {
		
	}

	/** Responsável por retornar a chave primária */
	public int getId() {
		return id;
	}

	/** Responsável por setar a chave primária */
	public void setId(int id) {
		this.id = id;
	}

	/** Responsável por retornar a Descrição */
	public String getDescricao() {
		return descricao;
	}

	/** Responsável por setar a Descrição */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	/** Responsável por retornar um boleano indicando se o tipo de invenção está em uso */
	public boolean isAtivo() {
		return ativo;
	}
	
	/** Responsável por setar um boleano indicando se o tipo de invenção está em uso */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Responsável por retornar a descrição da Categoria */
	public static String getDescricaoCategoria(int categoria) {
		return categorias.get(categoria);
	}

	/** Responsável por retornar a Categoria */
	public static Map<Integer, String> getCategorias() {
		return categorias;
	}

	/** Responsável pela realização da validações do Tipo de Invenção */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", erros);
		return erros;
	}
}