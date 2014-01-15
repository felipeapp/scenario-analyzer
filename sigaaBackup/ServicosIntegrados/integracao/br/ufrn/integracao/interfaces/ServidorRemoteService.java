package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.util.Date;

import javax.jws.WebService;

import br.ufrn.integracao.dto.PessoaDto;
import br.ufrn.integracao.dto.ServidorDTO;

/**
 * Interface Remota para que os sistemas se comuniquem com o SIGPRH através do Spring HTTP Invoker (Spring Remotting).
 * @author Rafael Moreira
 *
 */
@WebService
public interface ServidorRemoteService extends Serializable{	

	/** 
	 * Método que realiza a verificação se uma matrícula siape passada como parâmetro pertence à algum servidor 
	 * */
	public boolean checaSiape (int siape);
	
	/** 
	 * Retorna um servidorDTO a partir de uma matrícula siape passada como parâmetro 
	 * */
	public ServidorDTO getServidorBySiape(int siape);
	
	/** 
	 * Retorna um servidorDTO a partir de um CPF passado como parâmetro 
	 * */
	public ServidorDTO getServidorByCpf(String cpf);
	
	/** 
	 * Retorna uma pessoaDTO a partir de um id passado como parâmetro
	 * */
	public PessoaDto getPessoa(int idpessoa);
	
	/** 
	 * Verifica se um determinado servidor (através da matrícula siape) tem alguma necessidade especial cadastrada no sistema
	 * */
	public boolean temNecessidadeEspecial (int siape);
	
	/** 
	 * Realiza a validação de um Cpf passado como parâmetro
	 * */
	public boolean validarCpf (String cpf);
	
	/**
	 * Método que verifica se um servidor possui faltas em um determinado período.
	 * @param idServidor
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public boolean possuiFaltas(int idServidor, Date inicio, Date termino);
	
}
