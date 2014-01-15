/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Jan 30, 2008
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.ensino.negocio.dominio.AlteracaoStatusDiscenteMov;

/**
 * Processador respons�vel por realizar altera��o de status de discente.
 * 
 * @author Victor Hugo
 */
public class ProcessadorAlteracaoStatusDiscente extends AbstractProcessador {

	/** Executa a altera��o de status do discente.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento aMov) throws NegocioException, ArqException,
			RemoteException {

		validate(aMov);

		AlteracaoStatusDiscenteMov mov = (AlteracaoStatusDiscenteMov) aMov;
		DiscenteHelper.alterarStatusDiscente(mov.getDiscente(), mov.getStatus().getId(), mov.getObservacao(), mov, getGenericDAO(mov));

		return null;
	}

	/** valida se o usu�rio tem os pap�is: SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.PPG
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkRole(new int [] {SigaaPapeis.GESTOR_LATO ,SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.PPG, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR, SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA, SigaaPapeis.SECRETARIA_RESIDENCIA}, mov);
	}

}
