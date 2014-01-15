/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 19/05/2010
 */
package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.util.Date;

import javax.jws.WebService;

import br.ufrn.integracao.dto.DiasAlimentacaoDTO;
import br.ufrn.integracao.dto.ParametroRetornoLogarAcessoRU;
import br.ufrn.integracao.dto.PessoaDto;
import br.ufrn.integracao.exceptions.NegocioRemotoException;
import br.ufrn.integracao.exceptions.PessoaDigitalExistenteSistemaExcpetion;

/**
 * Interface responsável pela comunicação remota através de Spring Remote 
 * para gerenciar o acesso Restaurante Universitário.
 * 
 * @author agostinho campos
 *
 */
@WebService
public interface AcessoRURemoteService extends Serializable {
	
	public DiasAlimentacaoDTO autorizarRefeicao(byte[] digital) throws NegocioRemotoException;
	public DiasAlimentacaoDTO autorizarRefeicaoCartao(String cartaoAcesso) throws NegocioRemotoException;
	public boolean registrarAcessoRU(int tipoLiberacao, Date dataHora, int idUsuario, Long matricula, int tipoBolsa, String outraJustificativa);
	
    public ParametroRetornoLogarAcessoRU logarSistemaRU(String login, String senha, String hostName, String hostAddress);
    public PessoaDto identificarPessoa(byte[] digital, String tipoDedo, Long cpf) throws PessoaDigitalExistenteSistemaExcpetion, NegocioRemotoException;
    public PessoaDto identificarPessoaRU(String cartaoAcesso, Long cpf) throws NegocioRemotoException;
}
