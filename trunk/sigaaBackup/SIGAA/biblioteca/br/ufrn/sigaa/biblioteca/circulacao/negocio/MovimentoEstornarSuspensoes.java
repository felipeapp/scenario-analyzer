/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
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
 * Movimento responsável por armazenar as informações de Estorno de Suspensões
 * @author freddcs
 *
 */
public class MovimentoEstornarSuspensoes extends MovimentoCadastro {
    
	/** Lista de suspensões a serem estornadas */
	private List <SuspensaoUsuarioBiblioteca> suspensoesAEstornar;
	
	/** Motivo do estorno */
	private String motivo;
	
	/** Construtor da classe, passando as suspensões e o motivo do estorno */
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