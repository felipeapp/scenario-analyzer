/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p> Classe que representa os dados que s�o retornado pelos processadores que realizam as principais opera��es 
 * de circula��o (Emprestimo, Renova��o e Devolu��o). </p>
 *
 *  <p> Todos os processadores da parte de circula��o v�o retornar o mesmo objeto porque assim fica mais f�cil acrescentar 
 *  informa��o sem ter um impacto grande no sistema.  O objeto � um DTO porque deve ser envido tamb�m ao sistema remoto desktop que realiza empr�stimo.</p>
 *
 * 
 * @author jadson
 * 
 */
public class RetornoOperacoesCirculacaoDTO implements Serializable{

	
	private static final long serialVersionUID = 1L;
	
	
	/** 
	 * <p>Guarda um lista de opera��es de foram feitas nos processadores de empr�stimo e renova��o.  </p>
	 * 
	 * <p> Essa lista � usado para obter informa��es para imprimir comprovantes ou extornar os empr�stimo e renova��o</p>
	 * 
	 */
	public List <OperacaoBibliotecaDto> operacoesRealizadas;
	
	/**
	 * <p> Utilizando na devolu��o porque � necess�rio guardar as informa��es do empr�stimo que foi devolvido por exemplo para 
	 * imprimir o comprovante de devolu��o. </p>
	 * 
	 * <p>Utilizando tamb�m da opera��o de check out para caso o material esteja empr�stado, retorna os dados do empr�stimo para o usu�rio. </p>
	 * 
	 */
	public EmprestimoDto emprestimoRetornado;
	
	/**
	 * <p>Utilizando na opera��o de checkout, retorna os dados do material em que foi realizado o checkout. </p>
	 * 
	 */
	public MaterialInformacionalDto materialRetornado;
	
	
	/**
	 * <p>Mensagem que devem ser mostradas ao usu�rios ou operadores do sistema no momento que um empr�stimo � realizado, renovado ou devolvido. 
	 * O fluxo do caso de uso segue normalmente, com o usu�rio consegindo realizar as opera��es.</p>
	 * 
	 * <p> Se tiver qualquer mensagem na lista, � mostrada ao usu�rio. Geralmente mensagens de informa��o, mensagem de erros s�o lan�adas exece��es 
	 * e o fluxo do empr�stimo p�ra. </p>
	 */
	public List<String> mensagemAosUsuarios = new ArrayList<String>();
	
	/**
	 * <p>Mensagem utilizadas na parte de impress�o dos comprovantes de devolu��o do material.</p>
	 * 
	 * <p> Essas mensagens devem ser bem resumidas e n�o podem conter caracteres acentuados. Essas mensagem podem variar 
	 * dependendo da strat�gia de puni��o que se utilize, por isso s�o geradas e passadas pelo processaor de devolu��o</p>
	 */
	public List<String> mensagensComprovanteDevolucao = new ArrayList<String>();
	
	
	
	/**
     * Construtor default, obrigat�rios nos DTOs.
     */
	public RetornoOperacoesCirculacaoDTO() {
		super();
	}

	/**
	 * Adiciona opera��es feitas nos processadores aos  dados de  retorno
	 * 
	 * @param mensagens
	 */
	public void addOperacoesRealizadas(List <OperacaoBibliotecaDto> operacoesRealizadas){
		
		if(this.operacoesRealizadas == null)
			this.operacoesRealizadas = new ArrayList<OperacaoBibliotecaDto>();
		
		this.operacoesRealizadas.addAll(operacoesRealizadas);
	}
	
	/**
	 * Adiciona uma opera��o feita nos processadores de circula��o aos  dados de  retorno
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
	 * Adiciona mensagens que ser�o mostradas ao usu�rio/operador
	 * 
	 * @param mensagens
	 */
	public void addMensagensAosUsuarios(List<String> mensagens){
		
		if(this.mensagemAosUsuarios == null)
			this.mensagemAosUsuarios = new ArrayList<String>();
		
		this.mensagemAosUsuarios.addAll(mensagens);
	}
	
	/**
	 * Adiciona uma mensagem que ser� mostrada ao usu�rio/operador
	 * 
	 * @param mensagem
	 */
	public void addMensagemAosUsuarios(String mensagem){
		
		if(this.mensagemAosUsuarios == null)
			this.mensagemAosUsuarios = new ArrayList<String>();
		
		this.mensagemAosUsuarios.add(mensagem);
	}
	
	
	
}
