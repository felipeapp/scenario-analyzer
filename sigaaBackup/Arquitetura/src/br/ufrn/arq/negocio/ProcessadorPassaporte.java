/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 04/10/2007
 */
package br.ufrn.arq.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.dominio.PassaporteLogon;

/**
 * Processador para cadastro do passaporte.
 * 
 * @author Gleydson Lima
 *
 */
public class ProcessadorPassaporte extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		int tempo = ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.TEMPO_EXPIRACAO_PASSAPORTE);
		
		PassaporteLogon p = (PassaporteLogon) mov;
		p.setHora(new Date());
		p.setValidade(System.currentTimeMillis() + tempo);
		GenericSharedDBDao dao = getDAO(GenericSharedDBDao.class, mov);
		dao.create(p);
		dao.close();

		return p;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}
}
