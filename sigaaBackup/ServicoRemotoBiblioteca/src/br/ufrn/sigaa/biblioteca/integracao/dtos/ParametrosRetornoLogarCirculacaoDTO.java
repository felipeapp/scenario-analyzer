/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 23/05/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.integracao.dtos;

import java.util.List;

/**
 *
 * <p>Representa um parâmetro retornado pelo método logar no módulo de circulação da biblioteca.</p>
 *
 * <p> <i> Criado porque webservices ainda não suporta trabalhar com mapas, que era a solução utilizada antes com spring remoting.</i> </p>
 * 
 * @author jadson
 *
 */
public class ParametrosRetornoLogarCirculacaoDTO {

	/** Para registrar o operador que realizou os empréstimos, renovações e devoluções */
	public int idOperador;
	
	/** Mostrado ao usuário dentro da aplicação de circulação  */
	public String  nomeOperador;
	
	/** usuado para chamar o sistema web a partir do desktop */
	public String loginOperador;
	/** usuado para chamar o sistema web a partir do desktop */
	public int sistemaOrigem;
	/** usuado para chamar o sistema web a partir do desktop */
	public int sistemaDestino;
	
	/** Usado para manter o registro de entrada criado no login do sistema desktop */
	public int idRegistroEntrada;
	
	/** Usado para parametrizar as informações institucionais */ 
	public String instituicao;
	/** Usado para parametrizar as informações institucionais */ 
	public String  siglaSistema;
	/** Usado para parametrizar as informações institucionais */
	public String nomeSistema;
	/** Usado para parametrizar as informações institucionais */
	public String mensagemSobre;
	/** Usado para parametrizar as informações institucionais */
	public String linkManualEmprestimos;
	/** Usado para parametrizar as informações institucionais */
	public String linkManualCheckOut;
	
	/**
	 * <p>Envia os papéis do usuário e o id da biblioteca onde ele tem o papel, para habilitar as abas corretas do móduto de circulacao</p>
	 * 
	 * <p>Enviado no formato IdPapel_idBiblioteca para evitar ter que criar outro DTO para enviar esses dados</p>
	 */
	public List<String> papeis;
	
	/**
	 *  O conjunto de bibliotecas onde o usuário tem permissão de circulação, no login do desktop ele vai ter que escolher uma para trabalhar
	 */
	public List<BibliotecaDTO> bibliotecasPermissaoRemotas;
	
	
	public List <TipoEmprestimoDto> allTiposEmprestimo;
	
}
