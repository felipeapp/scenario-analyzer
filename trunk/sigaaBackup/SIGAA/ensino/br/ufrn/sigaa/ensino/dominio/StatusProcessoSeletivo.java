/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 05/02/2010
*
*/
package br.ufrn.sigaa.ensino.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

/**
 * Define os possíveis status do Processo Seletivo.
 * @author Arlindo Rodrigues
 * 
 */
public class StatusProcessoSeletivo {

	/** Indica que o processo seletivo está cadastrado */
	public static final int CADASTRADO = 1;
	/** Indica que o processo seletivo está com pendência de aprovação */
	public static final int PENDENTE_APROVACAO = 2;
	/** Indica que o processo seletivo foi publicado pela PPG */
	public static final int PUBLICADO = 3;
	/** Indica que o processo seletivo foi solicitado alteração pela PPG */
	public static final int SOLICITADO_ALTERACAO = 4;
	/** Indica que o processo seletivo foi finalizado */
	public static final int FINALIZADO = 5;
		
	/** 
	 * Retorna uma descrição textual do Status informado.
	 * @param status
	 * @return
	 */
	public static String getDescricao(int status){
		switch (status) {
		case CADASTRADO:     	  return "CADASTRADO";
		case PENDENTE_APROVACAO:  return "PENDENTE DE APROVAÇÃO";
		case PUBLICADO:           return "PUBLICADO";
		case SOLICITADO_ALTERACAO:return "SOLICITADO ALTERAÇÃO";
		case FINALIZADO: return "FINALIZADO";
		default:                  return "DESCONHECIDO";
		}
	}	
	
	/**
	 * Retorna todos os status do processo seletivos em uma coleção de selectitens
	 * @return
	 */
	public static Collection<SelectItem> getAllCombo(){
		
		Collection<SelectItem> itens = new ArrayList<SelectItem>();
		
		itens.add(new SelectItem(CADASTRADO, getDescricao(CADASTRADO)));
		itens.add(new SelectItem(PENDENTE_APROVACAO, getDescricao(PENDENTE_APROVACAO)));
		itens.add(new SelectItem(PUBLICADO, getDescricao(PUBLICADO)));
		itens.add(new SelectItem(SOLICITADO_ALTERACAO, getDescricao(SOLICITADO_ALTERACAO)));
		itens.add(new SelectItem(FINALIZADO, getDescricao(FINALIZADO)));
		
		return itens;
	}
		
	/** 
	 * Retorna os status ativos, que permitem alteração do processo seletivo.
	 * @return
	 */
	public static List<Integer> getAtivos() {
		List<Integer> status = new ArrayList<Integer>(3);
		status.add(CADASTRADO);
		status.add(SOLICITADO_ALTERACAO);
		return status;
	}
	
	/** 
	 * Retorna os status ativos, que permitem alteração do processo seletivo.
	 * @return
	 */
	public static Integer[] getAtivosPPG() {
		Integer[] status = new Integer[3];
		status[0] = PUBLICADO;
		status[1] = SOLICITADO_ALTERACAO;
		status[2] = PENDENTE_APROVACAO;
		return status;
	}

	/** 
	 * Retorna os status ativos, que permitem alteração do processo seletivo.
	 * @return
	 */
	public static Integer[] getAll() {
		Integer[] status = new Integer[5];
		status[0] = PUBLICADO;
		status[1] = SOLICITADO_ALTERACAO;
		status[2] = PENDENTE_APROVACAO;
		status[3] = CADASTRADO;
		status[4] = FINALIZADO;
		return status;
	}
	
}
