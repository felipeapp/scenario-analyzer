/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 01/08/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;


/**
 * Enumeração de tipos de ENADE: INGRESSANTE, CONCLUINTE.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
public enum TipoENADE {

	// NÃO ALTERAR A ORDEM DOS ENUM!!!
	/** Define o tipo de ENADE Ingressante*/
	INGRESSANTE,
	/** Define o tipo de ENADE Concluinte*/
	CONCLUINTE;

	/** Retorna uma descrição textual do tipo de ENADE.
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		switch (this) {
			case INGRESSANTE: return "ENADE INGRESSANTE";
			case CONCLUINTE: return "ENADE CONCLUINTE";
		}
		return super.toString();
	}

	/** Permite recuperar o valor ordinal do enum para ser exibido em jsp.
	 * @see {@link #ordinal()}
	 * @return
	 */
	public int getOrdinal() {
		return super.ordinal();
	}
	
	public boolean isIngressante() {
		return this == INGRESSANTE;
	}
}
