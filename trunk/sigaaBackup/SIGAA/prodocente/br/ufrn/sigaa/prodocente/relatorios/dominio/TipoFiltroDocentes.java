/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '04/06/2007'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.dominio;

import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;

/**
 * 
 * Tipos de filtros dos docentes que podem ser utilizados no momento de gerar o relatório de produtividade. 
 * Por exemplo, caso a distribuição das bolsas seja feita por área, seria necessário criar um tipo de filtro para cada área e gerar a distribuição sucessivas vezes, uma para cada área. 
 * Os tipos de filtro devem ser cadastrados aqui e é necessário informar a classe do projeto que implementa o filtro. 
 * Os filtros serão carregados automaticamente para a seleção do usuário.
 *
 * @author Ricardo Wendell
 */
@Entity
@Table(name = "tipo_filtro_docentes", schema = "prodocente")
public class TipoFiltroDocentes implements PersistDB{

	/**
	 * chave primária do tipo de filtro
	 */
	@Id @GeneratedValue
	@Column(name="id_tipo_filtro_docentes")
	private int id;
	
	/**
	 * descrição do tipo de filtro. Este é o conteúdo que aparecerá para o usuário no momento de selecionar o filtro de docentes.
	 */
	private String descricao;
	
	/**
	 * Caminho completo da classe que implementa o filtro
	 */
	private String classe;
	
	private boolean ativo = true;
	
	public static final int TODOS = 1;

	public static final int CONCORRENDO_COTA_PESQUISA = 2;

	public TipoFiltroDocentes() {
	}

	public static Map<Integer, String> getTipos() {
		Map<Integer, String> tipos = new TreeMap<Integer, String>();
		tipos.put(TODOS, "TODOS OS DOCENTES");
		tipos.put(CONCORRENDO_COTA_PESQUISA, "DOCENTES CONCORRENDO A COTAS DE BOLSAS DE PESQUISA");
		return tipos;
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

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}
	
	

}
