/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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

import br.ufrn.integracao.interfaces.CursoFundacaoRemoteService;
import br.ufrn.sigaa.ensino.dao.CursoFundacaoRemoteServiceDao;
import fundacao.integracao.academico.CursoDTO;

/**
 * Implementação do serviço remoto para operações com a importação de cursos.
 * 
 * @author Rafael Gomes
 *
 */

@WebService
@Component("cursoFundacaoRemoteServiceImpl")
public class CursoFundacaoRemoteServiceImpl implements CursoFundacaoRemoteService{

	/**
	 * Retorna a lista de todos os cursos para importação com o sistema da Fundação. 
	 * @return
	 */
	public List<CursoDTO> findAllCursoFundacao() {
		List<CursoDTO> cursosFundacao = new ArrayList<CursoDTO>();
		
		CursoFundacaoRemoteServiceDao dao = new CursoFundacaoRemoteServiceDao();
		try{
			cursosFundacao = dao.findAllCursoFundacao();
		}finally{
			dao.close();
		}
		
		return cursosFundacao;
	}

}
