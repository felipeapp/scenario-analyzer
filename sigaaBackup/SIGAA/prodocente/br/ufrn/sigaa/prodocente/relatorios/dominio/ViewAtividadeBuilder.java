/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '09/04/2007'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.dominio;

import java.util.HashMap;

/**
 * Interface para que as atividades implementem e possam
 * ser exibidas de maneira diferente das outras nos relat�rios
 * avalia��o docente.
 *
 * @author Gleydson
 *
 */
public interface ViewAtividadeBuilder {

	public String getItemView();

	public String getTituloView();

	public HashMap<String, String> getItens();

	public float getQtdBase();

}
