/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 26/04/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;

/**
 *
 * <p>Passa os dados para o processador </p>
 * 
 * @author jadson
 *
 */
public class MovimentoDesfazQuitacaoUsuarioBiblioteca extends AbstractMovimentoAdapter{

	/**
	 * Todas as contas que o usu�rio tem no sistema, quitadas ou n�o, utilizado para verificar se pode ativar a selecionada
	 */
	private List<UsuarioBiblioteca> contasUsuario;
	
	
	/**
	 * A conta cuja quita��o ser� desfeita
	 */
	private UsuarioBiblioteca contaSelecionada;

	
	
	public MovimentoDesfazQuitacaoUsuarioBiblioteca(List<UsuarioBiblioteca> contasUsuario, UsuarioBiblioteca contaSelecionada) {
		this.contasUsuario = contasUsuario;
		this.contaSelecionada = contaSelecionada;
		this.setCodMovimento(SigaaListaComando.DESFAZ_QUITACAO_BIBLIOTECA);
	}

	public List<UsuarioBiblioteca> getContasUsuario() {
		return contasUsuario;
	}

	public UsuarioBiblioteca getContaSelecionada() {
		return contaSelecionada;
	}
	
	
	
}
