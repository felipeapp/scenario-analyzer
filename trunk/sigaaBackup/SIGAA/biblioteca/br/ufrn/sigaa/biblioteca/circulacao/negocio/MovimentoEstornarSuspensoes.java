/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on 10/06/2009
 *
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.util.List;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SuspensaoUsuarioBiblioteca;

/**
 * Movimento respons�vel por armazenar as informa��es de Estorno de Suspens�es
 * @author freddcs
 *
 */
public class MovimentoEstornarSuspensoes extends MovimentoCadastro {
    
	/** Lista de suspens�es a serem estornadas */
	private List <SuspensaoUsuarioBiblioteca> suspensoesAEstornar;
	
	/** Motivo do estorno */
	private String motivo;
	
	/** Construtor da classe, passando as suspens�es e o motivo do estorno */
	public MovimentoEstornarSuspensoes (List <SuspensaoUsuarioBiblioteca> suspensoes, String motivo){
		this.suspensoesAEstornar = suspensoes;
		this.motivo = motivo;
		setCodMovimento(SigaaListaComando.ESTORNAR_SUSPENSOES_BIBLIOTECA);
	}

	public List<SuspensaoUsuarioBiblioteca> getSuspensoesAEstornar() {
		return suspensoesAEstornar;
	}


	public String getMotivo() {
		return motivo;
	}

}