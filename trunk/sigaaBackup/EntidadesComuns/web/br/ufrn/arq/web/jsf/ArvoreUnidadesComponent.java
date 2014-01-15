/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.arq.web.jsf;

import javax.faces.component.UIInput;

/**
 * Componente para cria��o de �rvore de unidades. 
 * Seta no managed bean a unidade selecionada (a unidade
 * toda, e n�o apenas o id).
 * 
 * @author David Pereira
 *
 */
public class ArvoreUnidadesComponent extends UIInput {

	@Override
	public String getFamily() {
		return "ARVORE_UNIDADES_FAMILY";
	}
	
}
