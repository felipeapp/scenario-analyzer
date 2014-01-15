/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendencia de Informatica - UFRN
 * Diretoria de Sistemas
 * 
 * Created on 30/03/2012
 */
package br.ufrn.sigaa.biblioteca.integracao.interfaces;

import java.util.List;

import br.ufrn.sigaa.biblioteca.integracao.dtos.inventario_acervo.MaterialDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.inventario_acervo.ParametrosRetornoExecutarColetorDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.inventario_acervo.ParametrosRetornoLogarColetorDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.inventario_acervo.ParametrosRetornoRegistrarMateriaisDto;
import br.ufrn.sigaa.biblioteca.integracao.exceptions.NegocioRemotoBibliotecaColetorException;

/**
 * <p>Interface de webservice para uso de coletor de inventario do acervo no modulo de biblioteca.</p>
 * A classe que representa a implementacao dessa interface se situa: 
 * <br/>
 * 
 * <strong>OBSERVACAO 1 .:</strong>   A VM do coletor trabalha na versao 1.3 do Java.
 * <strong>OBSERVACAO 2 .:</strong>   Nao usar acentos nos comentarios porque essas classes sao copiadas para o 
 * netbens e dar erro de codificacao se usar.
 * 
 * <code> br.ufrn.sigaa.biblioteca.processos_tecnicos.remoto.BibliotecaInventarioAcervoRemoteServiceImpl </code>
 * 
 * @author Felipe
 *
 */
public interface BibliotecaInventarioAcervoRemoteService {
	
	/**
	 * Metodo responsavel por realizar um teste de conexao que e solicitado na tela de configuracao.
	 * 
	 * @return
	 */
	public String testarConexao();

	/**
	 * Realiza o registro de uma lista de materiais no inventario do acervo indicado.
	 * 
	 * @param idInventario
	 * @param codigosBarras
	 * @param idOperador
	 * @return
	 * @throws NegocioRemotoBibliotecaColetorException
	 */
	public ParametrosRetornoRegistrarMateriaisDto registrarMateriais(int idInventario, List<String> codigosBarras, int idOperador) throws NegocioRemotoBibliotecaColetorException;
	
	/**
	 * Retorna os parametros associados a instituicao.
	 * 
	 * @return
	 */
	public ParametrosRetornoExecutarColetorDto obterParametrosInstitucionais();
	
	/**
	 * Realiza o logon do usuario do coletor no sistema. E importante para impedir
	 * que qualquer usuario utilize o sistema e registrar todas as operacoes realizadas pelo usuario
	 * autorizado caso algum dia necessite de auditoria.
	 * 
	 * @param login o login do usuario
	 * @param senha a senha do usuario
	 * @param inetAdd o endereco da estacao remota de onde o usuario esta se logando.
	 * @return um mapa do usuario com seus registros de entrada.
	 * 
	 * @throws NegocioRemotoBibliotecaColetorException
	 */
	public ParametrosRetornoLogarColetorDto logar(String login, String senha, String hostAddress, String hostName, String operacionalSystem) throws NegocioRemotoBibliotecaColetorException;

	/**
	 * Retorna as informacoes referentes a um material.
	 * 
	 * @param codigoBarras
	 * @return
	 * @throws NegocioRemotoBibliotecaColetorException
	 */
	public MaterialDto obterInformacoesMaterial(String codigoBarras) throws NegocioRemotoBibliotecaColetorException;

}
