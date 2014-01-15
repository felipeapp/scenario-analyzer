/*
 * MovimentoRealizaEmprestimo.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendencia de Informatica
 * Diretoria de Sistemas
 * Campos Universitario Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software eh confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * Nao se deve utilizar este produto em desacordo com as normas
 * da referida instituicao.
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.util.Map;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;

/**
 * Movimento que guarda os dados passados ao processador que realiza o empréstimo de itens. 
 *
 * @author jadson
 * @since 26/09/2008
 * @version 1.0 criação da classe
 *
 */
public class MovimentoRealizaEmprestimo extends MovimentoCadastro{

	/** < IdMaterialCatalografico , IdTipoEmprestimo > */
	private Map <Integer, Integer> idsTiposEmprestimosMateriais;
	
	/** O usuário que está emprestando os materiais. */
	private UsuarioBiblioteca usuarioBiblioteca;
	
	/** A senha do usuário que vai levar os materiais. */
	private String senhaDigitada;
	
	/** Informa a quantidade de dias a emprestar para os empréstimos sem política. */
	private Integer diasAEmprestar;
	
	
	/** Guarda o id do material que estava sendo processado, para poder dizer onde ocorreu o erro. */
	private int idMaterialProcessando;
	
	/** A biblioteca onde o usuário está opearando no momento. Utilizado para os empréstimos do módulo 
	 * de circulação desktop. Onde o usuário opera uma uma biblioteca por vez. */
	private Integer idBibliotecaOperacao;
	
	/**
	 * Construtor com os dados necessários para realizar um emprétimos no sistema.
	 * 
	 * 
	 * @param idsTiposEmprestimos     um mapa contendo <idMaterial, idTipoEmprestimo>
	 * @param diasAEmprestar          passa a quantidade de dias do empréstimo (utilizado apenas nos casos de emprétimo personalizavel)
	 * @param usuarioBiblioteca       o usuário para quem o empréstimo vai pertencer
	 * @param tipoUsuario             o vínculo que o usuário está usando para realizar o empréstimos
	 * @param senhaDigitada           a senha do usuário digitada para autorizar o empréstimo
	 * @param identificacaoUsuario    a identificação do vínculo do usuário do empréstimos, depende do vínculo que ele está usando para realizar o empréstimo
	 */
	public MovimentoRealizaEmprestimo(Map<Integer, Integer> idsTiposEmprestimosMateriais, UsuarioBiblioteca usuarioBiblioteca, String senhaDigitada
			,Integer diasAEmprestar, Integer idBibliotecaOperacao){
		
		this.idsTiposEmprestimosMateriais = idsTiposEmprestimosMateriais;
		this.diasAEmprestar = diasAEmprestar;
		this.usuarioBiblioteca = usuarioBiblioteca;
		this.senhaDigitada = senhaDigitada;
		
		setCodMovimento(SigaaListaComando.REALIZA_EMPRESTIMO);
		
		
		this.idBibliotecaOperacao = idBibliotecaOperacao;
		
	}
	
	public Map<Integer, Integer> getIdsTiposEmprestimosMateriais() {
		return idsTiposEmprestimosMateriais;
	}

	public void setIdsTiposEmprestimosMateriais(Map<Integer, Integer> idsTiposEmprestimosMateriais) {
		this.idsTiposEmprestimosMateriais = idsTiposEmprestimosMateriais;
	}

	public int getIdMaterialProcessando() {
		return idMaterialProcessando;
	}

	public void setIdMaterialProcessando(int idMaterialProcessando) {
		this.idMaterialProcessando = idMaterialProcessando;
	}

	public UsuarioBiblioteca getUsuarioBiblioteca() {
		return usuarioBiblioteca;
	}

	public String getSenhaDigitada() {
		return senhaDigitada;
	}


	public int getDiasAEmprestar() {
		return diasAEmprestar;
	}
	
	public Integer getIdBibliotecaOperacao() {
		return idBibliotecaOperacao;
	}

	/**
	 * <p>Método que determina se o usuário está operando a parte de circulação em uma biblioteca específica</p>
	 * <p>Nesse caso o processador deve validar se a biblioteca do operador é igual a biblioteca do material para deixar a devolução ser feita</p>
	 * 
	 * @return
	 */
	public boolean isOperandoBibliotecaEspecifica(){
		if(idBibliotecaOperacao != null)
			return true;
		else
			return false;
	}
	
}