package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.util.Date;

import javax.jws.WebService;

import br.ufrn.integracao.dto.PessoaDto;
import br.ufrn.integracao.dto.ServidorDTO;

/**
 * Interface Remota para que os sistemas se comuniquem com o SIGPRH atrav�s do Spring HTTP Invoker (Spring Remotting).
 * @author Rafael Moreira
 *
 */
@WebService
public interface ServidorRemoteService extends Serializable{	

	/** 
	 * M�todo que realiza a verifica��o se uma matr�cula siape passada como par�metro pertence � algum servidor 
	 * */
	public boolean checaSiape (int siape);
	
	/** 
	 * Retorna um servidorDTO a partir de uma matr�cula siape passada como par�metro 
	 * */
	public ServidorDTO getServidorBySiape(int siape);
	
	/** 
	 * Retorna um servidorDTO a partir de um CPF passado como par�metro 
	 * */
	public ServidorDTO getServidorByCpf(String cpf);
	
	/** 
	 * Retorna uma pessoaDTO a partir de um id passado como par�metro
	 * */
	public PessoaDto getPessoa(int idpessoa);
	
	/** 
	 * Verifica se um determinado servidor (atrav�s da matr�cula siape) tem alguma necessidade especial cadastrada no sistema
	 * */
	public boolean temNecessidadeEspecial (int siape);
	
	/** 
	 * Realiza a valida��o de um Cpf passado como par�metro
	 * */
	public boolean validarCpf (String cpf);
	
	/**
	 * M�todo que verifica se um servidor possui faltas em um determinado per�odo.
	 * @param idServidor
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public boolean possuiFaltas(int idServidor, Date inicio, Date termino);
	
}
