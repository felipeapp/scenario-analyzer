/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.comum.dominio.NoticiaPortal;

public class MovimentoNoticiaPortalDiscente extends AbstractMovimentoAdapter {
	private NoticiaPortal noticia;

	public NoticiaPortal getNoticia() {
		return noticia;
	}

	public void setNoticia(NoticiaPortal noticia) {
		this.noticia = noticia;
	}

}
