/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 04/12/2012
 * Autor: Rafael Gomes
 */
package br.ufrn.sigaa.ensino.servicos;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.springframework.stereotype.Component;

import br.ufrn.integracao.interfaces.DiscenteFundacaoRemoteService;
import br.ufrn.sigaa.ensino.dao.DiscenteFundacaoRemoteServiceDao;
import fundacao.integracao.academico.DiscenteDTO;

/**
 * Implementa��o do servi�o remoto para opera��es com a importa��o de discentes.
 * 
 * @author Rafael Gomes
 *
 */

@WebService
@Component("discenteFundacaoRemoteServiceImpl")
public class DiscenteFundacaoRemoteServiceImpl implements DiscenteFundacaoRemoteService{

	/**
	 * Retorna a lista de todos os discentes para importa��o com o sistema da Funda��o. 
	 * @return
	 */
	public List<DiscenteDTO> findAllDiscenteFundacao() {
		List<DiscenteDTO> discentesFundacao = new ArrayList<DiscenteDTO>();
		
		DiscenteFundacaoRemoteServiceDao dao = new DiscenteFundacaoRemoteServiceDao();
		try{
			discentesFundacao = dao.findAllDiscenteFundacao();
		}finally{
			dao.close();
		}
		
		return discentesFundacao;
	}

}
