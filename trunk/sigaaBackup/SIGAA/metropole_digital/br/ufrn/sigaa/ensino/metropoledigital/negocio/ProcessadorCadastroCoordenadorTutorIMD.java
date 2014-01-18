package br.ufrn.sigaa.ensino.metropoledigital.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ensino.metropoledigital.dao.CoordenadorTutorIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.CoordenadorTutorIMD;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Processador utilizado no cadastro de coordenador de tutor do IMD
 * 
 * @author Rafael Barros
 *
 */
public class ProcessadorCadastroCoordenadorTutorIMD extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		CoordenadorTutorIMDDao coordenadorDao = new CoordenadorTutorIMDDao();
		MovimentoCadastroCoordenadorTutorIMD movCoordenador = (MovimentoCadastroCoordenadorTutorIMD) mov;
		
		try {
			
			if(movCoordenador.getCoordenadorTutorIMD().getId() > 0) {
				
				
				CoordenadorTutorIMD newCoordenador = new CoordenadorTutorIMD();
				newCoordenador.setId(movCoordenador.getCoordenadorTutorIMD().getId());
				
				
				Pessoa pessoa = new Pessoa();
				pessoa = coordenadorDao.findByPrimaryKey(movCoordenador.getCoordenadorTutorIMD().getPessoa().getId(), Pessoa.class);
				
				newCoordenador.setPessoa(pessoa);
				
				coordenadorDao.update(newCoordenador);
				
			} else {
				coordenadorDao.create(movCoordenador.getCoordenadorTutorIMD());
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
