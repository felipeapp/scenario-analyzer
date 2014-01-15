/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 *     Guarda temporariamente as informações dos empréstimos de um vínculo de um usuário na Biblioteca. <br/> 
 *     Utilizado apenas na emissão do comprovante de quitação.
 * </p>
 * 
 * @author jadson
 *
 */
public class InformacaoEmprestimosPorVinculoUsuarioBiblioteca {

	
	/** A conta o usuário da biblioteca, podem existir várias para a mesma pessoa, mas apenas uma não quitada por vez */
	private UsuarioBiblioteca usuarioBiblioteca;
	
	/**
	 * Informações impressas no comprovante de quitação.
	 */
	private InformacoesUsuarioBiblioteca infoUsuarioBiblioteca;
	
	/**
	 * Descreve o curso ou lotação dependendo do vínculo
	 */
	private String descricaoDetalhadaVinculo;
	
	/** Guarda as Informações dos empréstimos ativos (data devolução, prazo, etc...) do usuário para o respectivo vínculo. Caso essa lista esteja vazia, o usuário 
	 * pode emitir  o documento de quitação */
	private List <Emprestimo> emprestimosAtivos;

	/** Guarda a quantidade total de empréstimos feitos(Ativos, devolvidos e estornados) com o vínculo que esse objeto representa. 
	 * Utilizado para inativar o documento caso o usuário realize um novo empréstimo */
	private int qtdEmprestimosTotaisDoVinculo = 0;
	
	/**
	 * Se esse víniculo nunca foi utilizado pelo usuário
	 */
	private boolean vinculoNuncaUsado = false;
	
	/**
	 * Se esse víniculo é o que o usuário está utilizando no momento.
	 */
	private boolean vinculoAtualmenteUsado = false;
	
	/**
	 * Construtor padrão
	 */
	public InformacaoEmprestimosPorVinculoUsuarioBiblioteca(){
		
	}
	
	
	/**
	 * Construtor para informações do vínculo usuário e seu empréstimos para a emissão da quitação.
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
	 *   <p>Retorna a quantidade de empréstimos que o usuário possui para este vinculo. </p>
	 *   <p>Se a quantidade for 0, pode-se emitir o comprovante de quitação. </p>
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
