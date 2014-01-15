/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 14/10/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.integracao.dtos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p> Classe que representa os dados que são retornado pelos processadores que realizam as principais operações 
 * de circulação (Emprestimo, Renovação e Devolução). </p>
 *
 *  <p> Todos os processadores da parte de circulação vão retornar o mesmo objeto porque assim fica mais fácil acrescentar 
 *  informação sem ter um impacto grande no sistema.  O objeto é um DTO porque deve ser envido também ao sistema remoto desktop que realiza empréstimo.</p>
 *
 * 
 * @author jadson
 * 
 */
public class RetornoOperacoesCirculacaoDTO implements Serializable{

	
	private static final long serialVersionUID = 1L;
	
	
	/** 
	 * <p>Guarda um lista de operações de foram feitas nos processadores de empréstimo e renovação.  </p>
	 * 
	 * <p> Essa lista é usado para obter informações para imprimir comprovantes ou extornar os empréstimo e renovação</p>
	 * 
	 */
	public List <OperacaoBibliotecaDto> operacoesRealizadas;
	
	/**
	 * <p> Utilizando na devolução porque é necessário guardar as informações do empréstimo que foi devolvido por exemplo para 
	 * imprimir o comprovante de devolução. </p>
	 * 
	 * <p>Utilizando também da operação de check out para caso o material esteja empréstado, retorna os dados do empréstimo para o usuário. </p>
	 * 
	 */
	public EmprestimoDto emprestimoRetornado;
	
	/**
	 * <p>Utilizando na operação de checkout, retorna os dados do material em que foi realizado o checkout. </p>
	 * 
	 */
	public MaterialInformacionalDto materialRetornado;
	
	
	/**
	 * <p>Mensagem que devem ser mostradas ao usuários ou operadores do sistema no momento que um empréstimo é realizado, renovado ou devolvido. 
	 * O fluxo do caso de uso segue normalmente, com o usuário consegindo realizar as operações.</p>
	 * 
	 * <p> Se tiver qualquer mensagem na lista, é mostrada ao usuário. Geralmente mensagens de informação, mensagem de erros são lançadas execeções 
	 * e o fluxo do empréstimo pára. </p>
	 */
	public List<String> mensagemAosUsuarios = new ArrayList<String>();
	
	/**
	 * <p>Mensagem utilizadas na parte de impressão dos comprovantes de devolução do material.</p>
	 * 
	 * <p> Essas mensagens devem ser bem resumidas e não podem conter caracteres acentuados. Essas mensagem podem variar 
	 * dependendo da stratégia de punição que se utilize, por isso são geradas e passadas pelo processaor de devolução</p>
	 */
	public List<String> mensagensComprovanteDevolucao = new ArrayList<String>();
	
	
	
	/**
     * Construtor default, obrigatórios nos DTOs.
     */
	public RetornoOperacoesCirculacaoDTO() {
		super();
	}

	/**
	 * Adiciona operações feitas nos processadores aos  dados de  retorno
	 * 
	 * @param mensagens
	 */
	public void addOperacoesRealizadas(List <OperacaoBibliotecaDto> operacoesRealizadas){
		
		if(this.operacoesRealizadas == null)
			this.operacoesRealizadas = new ArrayList<OperacaoBibliotecaDto>();
		
		this.operacoesRealizadas.addAll(operacoesRealizadas);
	}
	
	/**
	 * Adiciona uma operação feita nos processadores de circulação aos  dados de  retorno
	 * 
	 * @param mensagens
	 */
	public void addOperacaoRealizada(OperacaoBibliotecaDto operacaoRealizada){
		
		if(this.operacoesRealizadas == null)
			this.operacoesRealizadas = new ArrayList<OperacaoBibliotecaDto>();
		
		this.operacoesRealizadas.add(operacaoRealizada);
	}
	
	
	
	public List<OperacaoBibliotecaDto> getOperacoesRealizadas() {
		return operacoesRealizadas;
	}


	/**
	 * Adiciona mensagens que serão mostradas ao usuário/operador
	 * 
	 * @param mensagens
	 */
	public void addMensagensAosUsuarios(List<String> mensagens){
		
		if(this.mensagemAosUsuarios == null)
			this.mensagemAosUsuarios = new ArrayList<String>();
		
		this.mensagemAosUsuarios.addAll(mensagens);
	}
	
	/**
	 * Adiciona uma mensagem que será mostrada ao usuário/operador
	 * 
	 * @param mensagem
	 */
	public void addMensagemAosUsuarios(String mensagem){
		
		if(this.mensagemAosUsuarios == null)
			this.mensagemAosUsuarios = new ArrayList<String>();
		
		this.mensagemAosUsuarios.add(mensagem);
	}
	
	
	
}
