package br.ufrn.sigaa.ensino.metropoledigital.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.metropoledigital.dao.CoordenadorPoloIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.CoordenadorPoloIMD;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;


/**
 * Processador utilizado para o cadastro de coordenador de pólo do IMD
 * 
 * @author Rafael Barros
 *
 */
public class ProcessadorCadastroCoordenadorPoloIMD extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		CoordenadorPoloIMDDao coordenadorDao = new CoordenadorPoloIMDDao();
		MovimentoCadastroCoordenadorPoloIMD movCoordenador = (MovimentoCadastroCoordenadorPoloIMD) mov;
		
		try {
			
			if(movCoordenador.getCoordenadorPoloIMD().getId() > 0) {
				
				
				CoordenadorPoloIMD newCoordenador = new CoordenadorPoloIMD();
				newCoordenador.setId(movCoordenador.getCoordenadorPoloIMD().getId());
				
				Polo polo = new Polo();
				polo = coordenadorDao.findByPrimaryKey(movCoordenador.getCoordenadorPoloIMD().getPolo().getId(), Polo.class);
				
				Pessoa pessoa = new Pessoa();
				pessoa = coordenadorDao.findByPrimaryKey(movCoordenador.getCoordenadorPoloIMD().getPessoa().getId(), Pessoa.class);
				
				newCoordenador.setPessoa(pessoa);
				newCoordenador.setPolo(polo);
				
				coordenadorDao.update(newCoordenador);
				
			} else {
				coordenadorDao.create(movCoordenador.getCoordenadorPoloIMD());
			}
			
		} finally {
			coordenadorDao.close();
		}
		
		return movCoordenador;
	}
	
	

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// TODO Auto-generated method stub
		
	}

}
