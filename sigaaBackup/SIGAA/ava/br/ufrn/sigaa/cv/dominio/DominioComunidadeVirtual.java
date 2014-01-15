/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 22/01/2008
 */
package br.ufrn.sigaa.cv.dominio;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Interface utilizada pelas classes de domínio da Comunidade Virtual
 * 
 * @author David Pereira
 */
public interface DominioComunidadeVirtual extends PersistDB {

	public void setComunidade(ComunidadeVirtual comunidade);
	
	public ComunidadeVirtual getComunidade();

	public String getMensagemAtividade();
	
}
