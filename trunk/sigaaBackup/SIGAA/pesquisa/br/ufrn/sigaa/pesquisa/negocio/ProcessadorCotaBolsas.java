/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 12/03/2007
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;

/**
 * Processador responsável pelo cadastro das Cotas de Bolsas
 * 
 * @author Ricardo Wendell
 */
public class ProcessadorCotaBolsas extends AbstractProcessador {

	/**
	 * Método responsável pela execução do procesamento das cotas de Bolsas
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		checkRole(SigaaPapeis.GESTOR_PESQUISA, mov);
		validate(mov);

		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		CotaBolsas cota = (CotaBolsas) movimento.getObjMovimentado();

		GenericDAO dao = getGenericDAO(mov);

		if(cota.getId() == 0)
			dao.create(cota);
		else
			dao.update(cota);

		return movimento.getObjMovimentado();
	}

	/**
	 * Método responsável pela validação do processador de cotas Bolsas
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

}
