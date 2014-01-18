/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 25/06/2013
 * Autor: Diego J�come
 */
package br.ufrn.sigaa.ensino.servicos;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.springframework.stereotype.Component;

import br.ufrn.integracao.dto.dados_discentes.PessoaDTO;
import br.ufrn.integracao.interfaces.DadosDiscentesRemoteService;
import br.ufrn.sigaa.ensino.dao.DadosDiscentesRemoteServiceDao;

/**
 * Implementa��o do servi�o remoto para opera��es de busca de discentes.
 * 
 * @author Diego J�come
 *
 */
@WebService
@Component("dadosDiscentesRemoteServiceImpl")
public class DadosDiscentesRemoteServiceImpl implements DadosDiscentesRemoteService{

	@Override
	public List<PessoaDTO> findDiscentesByPessoas(List<Integer> ids) {
		
		ArrayList<PessoaDTO> pessoas = new ArrayList<PessoaDTO>();
		DadosDiscentesRemoteServiceDao dao = new DadosDiscentesRemoteServiceDao();
		try {
			if (ids != null && !ids.isEmpty())
				pessoas = (ArrayList<PessoaDTO>) dao.findDadosDiscentesByIdsPessoa(ids);
		}finally{
			dao.close();
		}

		return pessoas;
	}

}
