/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Serviço de acesso pago ao RU utilizando cartões magnéticos. 
 *
 * @author Bráulio
 */
@WebService
public interface AcessoRUSipacRemoteService extends Serializable {

	/**
	 * Retorna a quantidade de créditos em refeições, ou seja, a quantidade de refeições.
	 */
	public int retornaCreditosRefeicao( String trilha2 ) throws NegocioRemotoException;

	/**
	 * Método que deve ser chamado após o giro da catraca.
	 * Registra a utilização do cartão e debita uma refeição.
	 * 
	 * @param cartaoAcesso conteúdo das trilhas do cartão
	 * @param restaurante id do restaurante
	 */
	public void registraUsoRefeicao( String cartaoAcesso, int restaurante ) throws NegocioRemotoException;

	/**
	 * Retorna os dias nos quais o usuário pode utilizar o cartão.
	 */
	public DiasAlimentacaoDTO autorizarRefeicaoCartao( String cartaoAcesso ) throws NegocioRemotoException;

	/**
	 * Faz o login no aplicativo desktop do controle de catracas do RU.
	 */
	public ParametroRetornoLogarAcessoRU logarSistemaRU( String login, String senha, String hostName,
			String hostAddress );

	/**
	 * Retorna nome e CPF da pessoa que possui o cartão informado.
	 */
	public PessoaDto identificarPessoaRU( String cartaoAcesso, Long cpf ) throws NegocioRemotoException;

	/** Retorna nome, foto da pessoa e chave de acesso ao arquivo. */
	public Object[] getFotoPessoaRU( String trilha2 ) throws NegocioRemotoException;
	
	/**
     * Retorna a divisao estudantil que possui o cartao informado.
     */
	public DivisaoEstudantilDTO identificarDivisaoEstudantilRU( String cartaoAcesso ) throws NegocioRemotoException;
}