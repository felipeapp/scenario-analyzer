/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 18/01/2008
 */
package br.ufrn.sigaa.ava.validacao;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe que guarda a lista de mensagens a serem exibidas na próxima resposta ao usuário.
 * 
 * @author David Pereira
 *
 */
public class Notification {

	/** Lista contendo as mensagens  */
	private ListaMensagens lista = new ListaMensagens();
	
	/** Lista de turmas com sucesso, que não precisarão ser notificadas  */
	private List<Turma> turmasSucesso = new ArrayList<Turma>();
	
	/** Verifica se a lista possui mensagens */
	public boolean hasMessages() {
		return !lista.isEmpty();
	}

	/** Retorna as mensagens da lista */
	public ListaMensagens getMessages() {
		return lista;
	}

	/** Adiciona erros na lista */
	public void addError(String msg) {
		lista.addErro(msg);
	}
	
	/** Adiciona mensagens na lista */
	public void addMensagem(String msg, Object ... params){
		lista.addMensagem(msg, params);
	}
	
	/** Adiciona warnings na lista */
	public void addWarning(String msg){
		lista.addWarning(msg);
	}
	
	/** Adiciona uma string contendo os erros da lista */
	public String getErrorString(){
		return lista.toString();
	}
	
	public void setTurmasSucesso(List<Turma> turmasSucesso) {
		this.turmasSucesso = turmasSucesso;
	}

	public List<Turma> getTurmasSucesso() {
		return turmasSucesso;
	}
	
	public void adicionarTurmaSucesso ( Turma t ){
		turmasSucesso.add(t);
	}
	
	/** Verifica se existe erros na lista. */
	public boolean hasError ( ){
		boolean error = false;
		
		if ( getMessages() != null )
			if ( getMessages().isErrorPresent() )
				error = true;
		
		return error;
	}
}