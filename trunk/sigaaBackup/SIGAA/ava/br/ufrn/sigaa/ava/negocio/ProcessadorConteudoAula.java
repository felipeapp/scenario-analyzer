/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 * 
 * Created on 12/12/2006
 *
 */
package br.ufrn.sigaa.ava.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.TopicoAula;

/**
 * Processador para realizar o cadastro da agenda de aulas
 * no portal da turma.
 *
 * @author David Ricardo
 *
 */
public class ProcessadorConteudoAula extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		ConteudoAulaMov cMov = (ConteudoAulaMov) mov;
		Collection<TopicoAula> aulas = cMov.getAulas();
		
		GenericDAO dao = getDAO(GenericSigaaDAO.class, mov);
		
		try {
			for (TopicoAula aula : aulas) 
			{
				if (!aula.getDescricao().equals(""))
				{	
					if (aula.getId() == 0)
						dao.create(aula);
					else
						dao.update(aula);
				}
			}
		} finally {
			dao.close();
		}
		
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException 
	{
	}

}
