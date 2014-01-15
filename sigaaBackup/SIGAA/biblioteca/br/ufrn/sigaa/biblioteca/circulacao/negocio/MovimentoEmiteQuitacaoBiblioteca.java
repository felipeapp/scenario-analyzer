/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>Passa os dados para o processador que vai realizar a emissão e quitação do usuário da biblioteca</p>
 * 
 * @author jadson
 *
 */
public class MovimentoEmiteQuitacaoBiblioteca extends AbstractMovimentoAdapter{

	/**
	 * Informações para serem utilizadas no comprovante como nome, matrícula, etc..
	 * 
	 * Quando o usuário possui um vínculo no sistema.
	 */
	private InformacaoEmprestimosPorVinculoUsuarioBiblioteca infomacaoEmprestimosPorVinculoUsuarioBiblioteca;

	/**
	 * Se o usuário não tem vínculos, nesse caso tem que emitir o documento apenas com as informações da pessoa
	 */
	private boolean emitindoDocumentoSemVinculo = false;
	
	/**
	 * Utilizando quando o usuário não possui, nem nunca possuio um vínculo com a biblioteca, neste caso
	 * as informações pessoais dele vêm nesse objeto.
	 * 
	 */
	private InformacoesUsuarioBiblioteca informacoesUsuarioBiblioteca;
	
	
	/**
	 * Dados usando quando o usuário não tem vínculos (Bibliotecas sempre tem vínculo no sistema, mesmo que quitado)
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
