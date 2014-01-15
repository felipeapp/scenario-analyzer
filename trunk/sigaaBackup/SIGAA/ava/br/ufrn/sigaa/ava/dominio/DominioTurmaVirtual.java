/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 22/01/2008
 */
package br.ufrn.sigaa.ava.dominio;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Interface que serve como base para todas as classes de domínio para a turma virtual.
 * 
 * @author David Pereira
 *
 */
public interface DominioTurmaVirtual extends PersistDB {

	public void setTurma(Turma turma);
	
	public Turma getTurma();

	public String getMensagemAtividade();
	
}
