/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 30/05/2011
 */
package br.ufrn.integracao.interfaces;

import java.io.Serializable;

import javax.jws.WebService;

import br.ufrn.integracao.dto.DiasAlimentacaoDTO;
import br.ufrn.integracao.dto.DivisaoEstudantilDTO;
import br.ufrn.integracao.dto.ParametroRetornoLogarAcessoRU;
import br.ufrn.integracao.dto.PessoaDto;
import br.ufrn.integracao.exceptions.NegocioRemotoException;

/**
 * Servi�o de acesso pago ao RU utilizando cart�es magn�ticos. 
 *
 * @author Br�ulio
 */
@WebService
public interface AcessoRUSipacRemoteService extends Serializable {

	/**
	 * Retorna a quantidade de cr�ditos em refei��es, ou seja, a quantidade de refei��es.
	 */
	public int retornaCreditosRefeicao( String trilha2 ) throws NegocioRemotoException;

	/**
	 * M�todo que deve ser chamado ap�s o giro da catraca.
	 * Registra a utiliza��o do cart�o e debita uma refei��o.
	 * 
	 * @param cartaoAcesso conte�do das trilhas do cart�o
	 * @param restaurante id do restaurante
	 */
	public void registraUsoRefeicao( String cartaoAcesso, int restaurante ) throws NegocioRemotoException;

	/**
	 * Retorna os dias nos quais o usu�rio pode utilizar o cart�o.
	 */
	public DiasAlimentacaoDTO autorizarRefeicaoCartao( String cartaoAcesso ) throws NegocioRemotoException;

	/**
	 * Faz o login no aplicativo desktop do controle de catracas do RU.
	 */
	public ParametroRetornoLogarAcessoRU logarSistemaRU( String login, String senha, String hostName,
			String hostAddress );

	/**
	 * Retorna nome e CPF da pessoa que possui o cart�o informado.
	 */
	public PessoaDto identificarPessoaRU( String cartaoAcesso, Long cpf ) throws NegocioRemotoException;

	/** Retorna nome, foto da pessoa e chave de acesso ao arquivo. */
	public Object[] getFotoPessoaRU( String trilha2 ) throws NegocioRemotoException;
	
	/**
     * Retorna a divisao estudantil que possui o cartao informado.
     */
	public DivisaoEstudantilDTO identificarDivisaoEstudantilRU( String cartaoAcesso ) throws NegocioRemotoException;
}