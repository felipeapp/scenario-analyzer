/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 15/04/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.InformacaoEmprestimosPorVinculoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.InformacoesUsuarioBiblioteca;

/**
 *
 * <p>Passa os dados para o processador que vai realizar a emiss�o e quita��o do usu�rio da biblioteca</p>
 * 
 * @author jadson
 *
 */
public class MovimentoEmiteQuitacaoBiblioteca extends AbstractMovimentoAdapter{

	/**
	 * Informa��es para serem utilizadas no comprovante como nome, matr�cula, etc..
	 * 
	 * Quando o usu�rio possui um v�nculo no sistema.
	 */
	private InformacaoEmprestimosPorVinculoUsuarioBiblioteca infomacaoEmprestimosPorVinculoUsuarioBiblioteca;

	/**
	 * Se o usu�rio n�o tem v�nculos, nesse caso tem que emitir o documento apenas com as informa��es da pessoa
	 */
	private boolean emitindoDocumentoSemVinculo = false;
	
	/**
	 * Utilizando quando o usu�rio n�o possui, nem nunca possuio um v�nculo com a biblioteca, neste caso
	 * as informa��es pessoais dele v�m nesse objeto.
	 * 
	 */
	private InformacoesUsuarioBiblioteca informacoesUsuarioBiblioteca;
	
	
	/**
	 * Dados usando quando o usu�rio n�o tem v�nculos (Bibliotecas sempre tem v�nculo no sistema, mesmo que quitado)
	 */
	private Integer idPessoaEmissaoComprovante;
	
	public MovimentoEmiteQuitacaoBiblioteca(InformacaoEmprestimosPorVinculoUsuarioBiblioteca infomacaoEmprestimosPorVinculoUsuarioBiblioteca) {
		this.infomacaoEmprestimosPorVinculoUsuarioBiblioteca = infomacaoEmprestimosPorVinculoUsuarioBiblioteca;
		
		this.emitindoDocumentoSemVinculo = false;
	}

	public MovimentoEmiteQuitacaoBiblioteca( InformacoesUsuarioBiblioteca informacoesUsuarioBiblioteca,  Integer idPessoaEmissaoComprovante, Integer idBibliotecaEmissaoComprovante) {
		this.idPessoaEmissaoComprovante = idPessoaEmissaoComprovante;
		this.informacoesUsuarioBiblioteca = informacoesUsuarioBiblioteca;
		this.emitindoDocumentoSemVinculo = true;
	}
	
	
	public InformacaoEmprestimosPorVinculoUsuarioBiblioteca getInformacaoEmprestimosPorVinculoUsuarioBiblioteca() {
		return infomacaoEmprestimosPorVinculoUsuarioBiblioteca;
	}

	public boolean isEmitindoDocumentoSemVinculo() {
		return emitindoDocumentoSemVinculo;
	}

	public Integer getIdPessoaEmissaoComprovante() {
		return idPessoaEmissaoComprovante;
	}

	
	public InformacoesUsuarioBiblioteca getInformacoesUsuarioBiblioteca() {
		return informacoesUsuarioBiblioteca;
	}
	
	
}
