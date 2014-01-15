/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/08/14 - 13:46:54
 */
package br.ufrn.sigaa.ead.dominio;

/**
 * M�todo de avalia��o de um curso
 * de ensino a dist�ncia.
 * 
 * @author David Pereira
 *
 */
public class MetodoAvaliacao {

	/** Duas Unidades + Avalia��o substituindo menor nota */
	public static final int DUAS_PROVAS_RECUPERACAO = 1;
	
	/** Uma Unidade + Avalia��o substituindo menor nota */
	public static final int UMA_PROVA = 2;
	
	/**
	 * Retorna a descri��o do M�todo de Avalia��o
	 * @param tipo
	 * @return
	 */
	public static String getDescricao(int tipo) {
		if (tipo == DUAS_PROVAS_RECUPERACAO) {
			return "Duas Unidades + Avalia��o substituindo menor nota";
		} else if (tipo == UMA_PROVA) {
			return "Uma Unidade + Avalia��o substituindo menor nota";
		}
		
		return "";
	}
	
}
