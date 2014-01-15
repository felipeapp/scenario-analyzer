/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 21/11/2007
 */
package br.ufrn.sigaa.ead.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ead.dominio.MetodologiaAvaliacao;
import br.ufrn.sigaa.ead.dominio.SemanaAvaliacao;

/**
 * Processador para habilitar ou desabilitar a avaliação de semanas de aula
 * no ensino a distância.
 * 
 * @author David Pereira
 *
 */
public class ProcessadorHabilitacaoAvaliacao extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		SemanaAvaliacao semana = (SemanaAvaliacao) mov;
		GenericDAO dao = getGenericDAO(mov);
		
		try { 
			MetodologiaAvaliacao metodologia = dao.findByPrimaryKey(semana.getMetodologia().getId(), MetodologiaAvaliacao.class);
			if (SigaaListaComando.HABILITAR_AVALIACAO_SEMANA.equals(semana.getCodMovimento())) {
				if (metodologia.aulaAtiva(semana.getSemana()))
					throw new NegocioException("A avaliação para a semana selecionada já está habilitada.");
				
				metodologia.habilitar(semana);
			} else {
				if (!metodologia.aulaAtiva(semana.getSemana()))
					throw new NegocioException("A avaliação para a semana selecionada já está desabilitada.");
				
				metodologia.desabilitar(semana);
			}
			
			dao.update(metodologia);

			return metodologia;
		} finally {
			dao.close();
		}
	
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
