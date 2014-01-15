/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 22/02/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.jsf.BuscaUsuarioBibliotecaMBean;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *
 * <p>Interface que deve ser implementada por qualquer MBean que queira realizar buscas de um usuário da biblioteca. </p>
 * <p><i> ( Quando a busca vem de outro caso de uso e após selecionar o usuário o fluxo deve retorna para o caso de uso chamador ) </i> </p>
 * 
 * @author jadson
 * @see {@link BuscaUsuarioBibliotecaMBean}
 *
 */
public interface PesquisarUsuarioBiblioteca {

	/**
	 *   Retorna ao MBean que chamou a busca a pessoa selecionada.
	 *
	 *	 <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @param espacoFisico
	 * @throws ArqException
	 */
	public void setPessoaBuscaPadrao(Pessoa p);

	/**
	 *  Retorna ao MBean que chamou a busca a pessoa selecionada a biblioteca selecionada. (Caso de busca de usuários que são bibliotecas)
	 *
	 *    <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @param espacoFisico
	 * @throws ArqException
	 */
	public void setBibliotecaBuscaPadrao(Biblioteca biblioteca);
	
	/**
	 * Array variável de parâmetros que podem ser passados dependendo do caso de uso
	 *
	 *    <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 *    
	 * @param parametos
	 */
	public void setParametrosExtra(boolean parametroDePessoa, String... parametros);
	
	
	/**
	 *   Retorna o fluxo do caso de uso ao Mbean que chamou a busca assim que o usuário seleciona o título no acervo da biblioteca.
	 *
	 *    <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String selecionouUsuarioBuscaPadrao() throws ArqException;
	
	
	
}
