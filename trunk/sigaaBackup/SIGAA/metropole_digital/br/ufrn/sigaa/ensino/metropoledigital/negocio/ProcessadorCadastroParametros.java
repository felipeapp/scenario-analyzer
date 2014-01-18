package br.ufrn.sigaa.ensino.metropoledigital.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ensino.metropoledigital.dao.ParametrosAcademicosIMDDao;

/**
 * Processador responsável por realizar o cadastro dos parâmetros acadêmicos do IMD.
 * 
 * @author Rafael Silva
 *
 */
public class ProcessadorCadastroParametros extends AbstractProcessador{
	

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
			ParametrosAcademicosIMDDao dao =  new ParametrosAcademicosIMDDao();
			MovimentoCadastroParametros movimentoParametros = (MovimentoCadastroParametros) mov;
			try {
				movimentoParametros.getParametrosAntigos().setDataInativacao(new Date());
				movimentoParametros.getParametrosAntigos().setVigente(false);
				
				movimentoParametros.getParametrosNovos().setVigente(true);
				movimentoParametros.getParametrosNovos().setDataCadastro(new Date());
				
				dao.update(movimentoParametros.getParametrosAntigos());				
				dao.create(movimentoParametros.getParametrosNovos());
				
			} finally{
				dao.close();
			}
		return movimentoParametros;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// TODO Auto-generated method stub
		
	}

}
