
package br.ufrn.sigaa.ead.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ead.dominio.AvaliacaoDiscenteEad;
import br.ufrn.sigaa.ead.dominio.FichaAvaliacaoEad;
import br.ufrn.sigaa.ead.dominio.ItemAvaliacaoEad;
import br.ufrn.sigaa.ead.dominio.NotaItemAvaliacao;

/**
 * Processador para cadastro e atualização das avaliações dos discentes
 * de ensino a distância.
 * 
 * @author David Pereira
 *
 */
public class ProcessadorAvaliacaoSemanalEad extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		AvaliacaoEadMov aMov = (AvaliacaoEadMov) mov;
		
		FichaAvaliacaoEad ficha = aMov.getFicha();
		AvaliacaoDiscenteEad avaliacao = aMov.getAvaliacao();
		
		GenericDAO dao = getGenericDAO(mov);
		
		try {
			// Cadastra a ficha de avaliação se não tiver sido cadastrada
			if (ficha.getId() == 0)
				dao.create(ficha);	
			
			// Cadastra a avaliação, se ainda não tiver sido cadastrada
			if (avaliacao.getId() == 0) 
				dao.create(avaliacao);
			else
				dao.update(avaliacao);
				
			// Cadastra ou atualiza as notas
			if (!isEmpty(avaliacao.getItens())) {
				for (ItemAvaliacaoEad item : avaliacao.getItens()) {
					for (NotaItemAvaliacao nota : item.getNotas()) {
						if (nota.getId() == 0) {
							dao.create(nota);
						} else {
							dao.update(nota);
						}
					}						
				}
			}
							
		} finally {
			dao.close();
		}
		
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}
