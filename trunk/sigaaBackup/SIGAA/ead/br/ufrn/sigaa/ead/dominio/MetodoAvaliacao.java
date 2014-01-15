/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/08/14 - 13:46:54
 */
package br.ufrn.sigaa.ead.dominio;

/**
 * Método de avaliação de um curso
 * de ensino a distância.
 * 
 * @author David Pereira
 *
 */
public class MetodoAvaliacao {

	/** Duas Unidades + Avaliação substituindo menor nota */
	public static final int DUAS_PROVAS_RECUPERACAO = 1;
	
	/** Uma Unidade + Avaliação substituindo menor nota */
	public static final int UMA_PROVA = 2;
	
	/**
	 * Retorna a descrição do Método de Avaliação
	 * @param tipo
	 * @return
	 */
	public static String getDescricao(int tipo) {
		if (tipo == DUAS_PROVAS_RECUPERACAO) {
			return "Duas Unidades + Avaliação substituindo menor nota";
		} else if (tipo == UMA_PROVA) {
			return "Uma Unidade + Avaliação substituindo menor nota";
		}
		
		return "";
	}
	
}
