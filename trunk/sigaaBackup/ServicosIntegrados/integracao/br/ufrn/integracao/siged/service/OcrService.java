/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 17/06/2010
 */
package br.ufrn.integracao.siged.service;

import javax.jws.WebService;

import br.ufrn.integracao.siged.dto.ArquivoDocumento;

/**
 * Interface para acesso ao serviço remoto de reconhecimento
 * ótico de caracteres. Utilizado para identificar o conteúdo
 * textual de imagens.
 * 
 * @author David Pereira
 *
 */
@WebService
public interface OcrService {

	/**
	 * Dado um arquivo, retorna a String identificada como sendo
	 * o conteúdo da imagem passada.
	 * @param arquivo
	 * @return
	 */
	public String identificarConteudo(ArquivoDocumento arquivo);
	
}
