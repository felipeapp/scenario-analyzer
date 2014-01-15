/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 01/07/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.dominio;

import java.util.List;

import br.ufrn.sigaa.biblioteca.dominio.InformacoesUsuarioBiblioteca;

/**
 * <p> 
 *     Guarda temporariamente as informa��es dos empr�stimos de um v�nculo de um usu�rio na Biblioteca. <br/> 
 *     Utilizado apenas na emiss�o do comprovante de quita��o.
 * </p>
 * 
 * @author jadson
 *
 */
public class InformacaoEmprestimosPorVinculoUsuarioBiblioteca {

	
	/** A conta o usu�rio da biblioteca, podem existir v�rias para a mesma pessoa, mas apenas uma n�o quitada por vez */
	private UsuarioBiblioteca usuarioBiblioteca;
	
	/**
	 * Informa��es impressas no comprovante de quita��o.
	 */
	private InformacoesUsuarioBiblioteca infoUsuarioBiblioteca;
	
	/**
	 * Descreve o curso ou lota��o dependendo do v�nculo
	 */
	private String descricaoDetalhadaVinculo;
	
	/** Guarda as Informa��es dos empr�stimos ativos (data devolu��o, prazo, etc...) do usu�rio para o respectivo v�nculo. Caso essa lista esteja vazia, o usu�rio 
	 * pode emitir  o documento de quita��o */
	private List <Emprestimo> emprestimosAtivos;

	/** Guarda a quantidade total de empr�stimos feitos(Ativos, devolvidos e estornados) com o v�nculo que esse objeto representa. 
	 * Utilizado para inativar o documento caso o usu�rio realize um novo empr�stimo */
	private int qtdEmprestimosTotaisDoVinculo = 0;
	
	/**
	 * Se esse v�niculo nunca foi utilizado pelo usu�rio
	 */
	private boolean vinculoNuncaUsado = false;
	
	/**
	 * Se esse v�niculo � o que o usu�rio est� utilizando no momento.
	 */
	private boolean vinculoAtualmenteUsado = false;
	
	/**
	 * Construtor padr�o
	 */
	public InformacaoEmprestimosPorVinculoUsuarioBiblioteca(){
		
	}
	
	
	/**
	 * Construtor para informa��es do v�nculo usu�rio e seu empr�stimos para a emiss�o da quita��o.
	 * 
	 * @param usuarioBiblioteca
	 * @param emprestimosAtivos
	 * @param qtdEmprestimosTotaisDoVinculo
	 * @param descricaoDetalhadaVinculo
	 */
	public InformacaoEmprestimosPorVinculoUsuarioBiblioteca(UsuarioBiblioteca usuarioBiblioteca, List<Emprestimo> emprestimosAtivos
			, int qtdEmprestimosTotaisDoVinculo, String descricaoDetalhadaVinculo, boolean vinculoNuncaUsado, boolean vinculoAtualmenteUsado) {
		this.usuarioBiblioteca = usuarioBiblioteca;
		this.emprestimosAtivos = emprestimosAtivos;
		this.qtdEmprestimosTotaisDoVinculo = qtdEmprestimosTotaisDoVinculo;
		this.descricaoDetalhadaVinculo = descricaoDetalhadaVinculo;
		this.vinculoNuncaUsado = vinculoNuncaUsado;
		this.vinculoAtualmenteUsado = vinculoAtualmenteUsado;
	}
	

	public UsuarioBiblioteca getUsuarioBiblioteca() {
		return usuarioBiblioteca;
	}

	public List<Emprestimo> getEmprestimosAtivos() {
		return emprestimosAtivos;
	}
	
	
	/**
	 *   <p>Retorna a quantidade de empr�stimos que o usu�rio possui para este vinculo. </p>
	 *   <p>Se a quantidade for 0, pode-se emitir o comprovante de quita��o. </p>
	 *    
	 * @return
	 */
	public int getQuantidadeEmprestimosAtivos(){
		if(emprestimosAtivos != null)
			return emprestimosAtivos.size();
		else
			return 0;
	}


	public int getQtdEmprestimosTotaisDoVinculo() {
		return qtdEmprestimosTotaisDoVinculo;
	}


	public void setQtdEmprestimosTotaisDoVinculo(int qtdEmprestimosTotaisDoVinculo) {
		this.qtdEmprestimosTotaisDoVinculo = qtdEmprestimosTotaisDoVinculo;
	}


	public String getDescricaoDetalhadaVinculo() {
		return descricaoDetalhadaVinculo;
	}


	public InformacoesUsuarioBiblioteca getInfoUsuarioBiblioteca() {
		return infoUsuarioBiblioteca;
	}


	public void setInfoUsuarioBiblioteca(InformacoesUsuarioBiblioteca infoUsuarioBiblioteca) {
		this.infoUsuarioBiblioteca = infoUsuarioBiblioteca;
	}

	public boolean isVinculoAtualmenteUsado() {
		return vinculoAtualmenteUsado;
	}

	public void setVinculoAtualmenteUsado(boolean vinculoAtualmenteUsado) {
		this.vinculoAtualmenteUsado = vinculoAtualmenteUsado;
	}


	public boolean isVinculoNuncaUsado() {
		return vinculoNuncaUsado;
	}
	
	
}
