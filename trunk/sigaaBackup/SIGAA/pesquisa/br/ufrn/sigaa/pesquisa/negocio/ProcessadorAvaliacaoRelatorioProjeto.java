/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 10/10/2006
 *
 */

package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.pesquisa.dominio.RelatorioProjeto;

/**
 * Processador para cadastro em Avaliações dos Relatórios de Projetos de Pesquisa
 * @author ricardo
 *
 */
public class ProcessadorAvaliacaoRelatorioProjeto extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		RelatorioProjeto relatorio = (RelatorioProjeto) mov;

		GenericDAO dao = getDAO(mov);
		RelatorioProjeto relatorioBanco = relatorio;
		try {
			relatorioBanco = dao.findByPrimaryKey(relatorio.getId(), RelatorioProjeto.class);

			relatorioBanco.setParecerConsultor(relatorio.getParecerConsultor());
			relatorioBanco.setAvaliacao(relatorio.getAvaliacao());
			relatorioBanco.setDataAvaliacao(new Date());

			dao.update(relatorioBanco);
		} finally {
			dao.close();
		}

		return relatorioBanco;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

}
