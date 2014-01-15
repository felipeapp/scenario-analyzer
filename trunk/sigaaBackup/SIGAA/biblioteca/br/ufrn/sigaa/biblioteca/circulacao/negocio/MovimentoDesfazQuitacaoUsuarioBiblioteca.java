/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
	 * Todas as contas que o usuário tem no sistema, quitadas ou não, utilizado para verificar se pode ativar a selecionada
	 */
	private List<UsuarioBiblioteca> contasUsuario;
	
	
	/**
	 * A conta cuja quitação será desfeita
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
