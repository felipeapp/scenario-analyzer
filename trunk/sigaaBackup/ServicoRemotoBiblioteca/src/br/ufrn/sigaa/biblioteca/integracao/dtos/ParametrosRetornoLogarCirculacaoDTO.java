/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 23/05/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.integracao.dtos;

import java.util.List;

/**
 *
 * <p>Representa um par�metro retornado pelo m�todo logar no m�dulo de circula��o da biblioteca.</p>
 *
 * <p> <i> Criado porque webservices ainda n�o suporta trabalhar com mapas, que era a solu��o utilizada antes com spring remoting.</i> </p>
 * 
 * @author jadson
 *
 */
public class ParametrosRetornoLogarCirculacaoDTO {

	/** Para registrar o operador que realizou os empr�stimos, renova��es e devolu��es */
	public int idOperador;
	
	/** Mostrado ao usu�rio dentro da aplica��o de circula��o  */
	public String  nomeOperador;
	
	/** usuado para chamar o sistema web a partir do desktop */
	public String loginOperador;
	/** usuado para chamar o sistema web a partir do desktop */
	public int sistemaOrigem;
	/** usuado para chamar o sistema web a partir do desktop */
	public int sistemaDestino;
	
	/** Usado para manter o registro de entrada criado no login do sistema desktop */
	public int idRegistroEntrada;
	
	/** Usado para parametrizar as informa��es institucionais */ 
	public String instituicao;
	/** Usado para parametrizar as informa��es institucionais */ 
	public String  siglaSistema;
	/** Usado para parametrizar as informa��es institucionais */
	public String nomeSistema;
	/** Usado para parametrizar as informa��es institucionais */
	public String mensagemSobre;
	/** Usado para parametrizar as informa��es institucionais */
	public String linkManualEmprestimos;
	/** Usado para parametrizar as informa��es institucionais */
	public String linkManualCheckOut;
	
	/**
	 * <p>Envia os pap�is do usu�rio e o id da biblioteca onde ele tem o papel, para habilitar as abas corretas do m�duto de circulacao</p>
	 * 
	 * <p>Enviado no formato IdPapel_idBiblioteca para evitar ter que criar outro DTO para enviar esses dados</p>
	 */
	public List<String> papeis;
	
	/**
	 *  O conjunto de bibliotecas onde o usu�rio tem permiss�o de circula��o, no login do desktop ele vai ter que escolher uma para trabalhar
	 */
	public List<BibliotecaDTO> bibliotecasPermissaoRemotas;
	
	
	public List <TipoEmprestimoDto> allTiposEmprestimo;
	
}
