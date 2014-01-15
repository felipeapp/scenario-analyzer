/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 19/10/2006
 *
 */
package br.ufrn.sigaa.ensino.latosensu.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.latosensu.dominio.ParceriaLato;

/**
 * Processador para tratar dos comandos CRUD de ParceriaLato
 *
 * @author Leonardo
 *
 */
public class ProcessadorParceriaLato extends AbstractProcessador {

	/* (non-Javadoc)
	 * @see br.ufrn.arq.ejb.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		validate(mov);

		MovimentoCadastro movc = (MovimentoCadastro) mov;

		ParceriaLato parceria = (ParceriaLato) movc.getObjMovimentado();

		GenericDAO dao = getGenericDAO(mov);

		if( movc.getCodMovimento().equals( SigaaListaComando.CADASTRAR_PARCERIA_LATO) ){
			dao.create( parceria );
		} else if( movc.getCodMovimento().equals( SigaaListaComando.ALTERAR_PARCERIA_LATO) ){
			dao.update( parceria );
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.ejb.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
