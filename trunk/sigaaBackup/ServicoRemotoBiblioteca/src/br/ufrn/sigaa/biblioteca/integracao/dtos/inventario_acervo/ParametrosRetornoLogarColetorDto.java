/*
*
* Universidade Federal do Rio Grande do Norte
* Superintendencia de Informatica
* Diretoria de Sistemas
* 
* Criado em: 04/04/2012
* 
*/
package br.ufrn.sigaa.biblioteca.integracao.dtos.inventario_acervo;

import java.io.Serializable;
import java.util.List;

/**
*
* <p>Representa os parametros retornados pelo metodo logar no modulo de coletor da biblioteca.</p>
*
* <p><i>Criado porque webservices ainda nao suportam trabalhar com mapas, que era a solucao utilizada antes com spring remoting.</i></p>
* 
* <strong>OBSERVACAO 1 .:</strong>   A VM do coletor trabalha na versao 1.3 do Java.
* <strong>OBSERVACAO 2 .:</strong>   Nao usar acentos nos comentarios porque essas classes sao copiadas para o 
* netbens e dar erro de codificacao se usar.
* 
* @author felipe
*
*/
public class ParametrosRetornoLogarColetorDto implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** Para registrar o operador que realizou os emprestimos, renovacoes e devolucoes */
	public int idOperador;	
	/** Mostrado ao usuario dentro da aplicacao do coletor  */
	public String nomeOperador;	
	/** usuado para chamar o sistema web a partir do coletor */
	public String loginOperador;
	/** usuado para chamar o sistema web a partir do coletor */
	public int sistemaOrigem;
	/** usuado para chamar o sistema web a partir do coletor */
	public int sistemaDestino;
	
	/**
	 *  O conjunto de inventarios correspondentes as bibliotecas onde o usuario tem permissao de circulacao, no login do desktop ele vai ter que escolher uma para trabalhar
	 *  
	 *  OBSERVACAO: Usar generics, se não dá erro. ele não reconhece a classe: InventarioDto
	 */
	public List<InventarioDto> inventariosPermissaoRemota;
	
}
