/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 19/04/2010
 */
package br.ufrn.integracao.interfaces;

import java.io.File;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * Serviço remoto para conversão de arquivos para
 * o formato pdf.
 * 
 * @author David Pereira
 *
 */
@WebService
public interface ConversaoPdfRemoteService {

	/**
	 * Converte um arquivo do banco de dados cujo id
	 * foi passado como parâmetro em pdf. 
	 * @param idArquivo
	 * @return
	 */
	@WebMethod(operationName="converterIdArquivo")
	public File converter(int idArquivo);
	
	/**
	 * Converte o arquivo passado como parâmetro para o formato pdf.
	 * @param origem
	 * @return
	 */
	@WebMethod(operationName="converterFile")
	public File converter(File origem);
	
}
