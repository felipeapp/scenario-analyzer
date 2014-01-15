/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 18/05/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ServicosEmprestimosBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.ServicosInformacaoReferenciaBiblioteca;

/**
 *
 * <p>Passa os dados para o processador que atualiza a biblioteca</p>
 *
 * 
 * @author jadson
 *
 */
public class MovimentoAtualizaBibliotecaInterna extends AbstractMovimentoAdapter{

	/**
	 * A biblioteca que vai ser atualizada
	 */
	private Biblioteca biblioteca;
	
	/**
	 * Os serviços da biblioteca que devem ser atualizados
	 */
	private java.util.List<ServicosEmprestimosBiblioteca> servicosDeEmprestimos;
	
	/**
	 * Os serviços aos usuários da biblioteca que devem ser atualizados
	 */
	private ServicosInformacaoReferenciaBiblioteca servicosAosUsuarios;

	/**
	 * Construtor parão.
	 * 
	 * @param biblioteca
	 * @param servicosDeEmprestimos
	 * @param servicosAosUsuarios
	 */
	public MovimentoAtualizaBibliotecaInterna(
			Biblioteca biblioteca,
			java.util.List<ServicosEmprestimosBiblioteca> servicosDeEmprestimos,
			ServicosInformacaoReferenciaBiblioteca servicosAosUsuarios) {
		this.biblioteca = biblioteca;
		this.servicosDeEmprestimos = servicosDeEmprestimos;
		this.servicosAosUsuarios = servicosAosUsuarios;
		this.setCodMovimento(SigaaListaComando.ALTERAR_BIBLIOTECA_INTERNA);
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public java.util.List<ServicosEmprestimosBiblioteca> getServicosDeEmprestimos() {
		return servicosDeEmprestimos;
	}

	public ServicosInformacaoReferenciaBiblioteca getServicosAosUsuarios() {
		return servicosAosUsuarios;
	}
	
	
	
}
