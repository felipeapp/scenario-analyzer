/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * 
 * Created on 24/10/2006
 *
 */
package br.ufrn.sigaa.monitoria.dominio;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;

/*******************************************************************************
 * <p>
 * Representa os status que um resumo (SID), um relatório de projeto ou um
 * relatório de monitor pode assumir.
 * </p>
 * 
 * 
 * @author David Ricardo
 * 
 ******************************************************************************/
@Entity
@Table(name = "status_relatorio", schema = "monitoria")
public class StatusRelatorio implements PersistDB {

	public static final int AGUARDANDO_DISTRIBUICAO = 1;

	public static final int AGUARDANDO_AVALIACAO = 2;

	/** para resumo (sid) = avaliado sem ressalvas */
	public static final int AVALIADO = 3; 

	public static final int AVALIADO_COM_RESSALVAS = 4;

	public static final int CADASTRO_EM_ANDAMENTO = 5;

	public static final int REMOVIDO = 6;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;

	private String descricao;

	public StatusRelatorio() {

	}

	public StatusRelatorio(int id) {
		this.id = id;
	}

	/**
	 * @return the descrição
	 */
	public String getDescricao() {
		return descricao;
	}

	/**
	 * @param descricao
	 *            the descricao to set
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

}
