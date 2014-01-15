package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import br.ufrn.integracao.dto.ParametrosRetornoLogarCadastroPessoaDTO;
import br.ufrn.integracao.dto.PessoaDto;
import br.ufrn.integracao.exceptions.NegocioRemotoException;
import br.ufrn.integracao.exceptions.PessoaDigitalExistenteSistemaExcpetion;

/**
 * Interface responsável pela comunicação remota através de Spring Remote 
 * para gerenciar o cadastro de digital das pessoas.
 * 
 * @author agostinho campos
 *
 */
@WebService
public interface IdentificacaoPessoaRemoteService extends Serializable {
	
    public List<PessoaDto> findPessoa(String nome, int tipoPessoa);
    public boolean gravarOuAtualizarIdentificacao(Long cpf, byte[] imagem, byte[] digital, String tipoDedo, int idUsuario,  byte[] imagemDigital);
    
    public ParametrosRetornoLogarCadastroPessoaDTO logarSistemaCadastroPessoa(String login, String senha, String hostName, String hostAddress);
    public PessoaDto identificarPessoa(byte[] digital, String tipoDedo, Long cpf) throws PessoaDigitalExistenteSistemaExcpetion, NegocioRemotoException;
    
}