/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 26/08/2009
 */
package br.ufrn.sigaa.parametros.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.Parametro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.comum.dominio.Sistema;

/** 
 * Altera par�metros no banco comum a partir de opera��es no SIGAA.
 * 
 * @author �dipo Elder F. Melo
 * @author Leonardo Campos
 *
 */
public class ProcessadorAlterarParametros extends AbstractProcessador {

	/** Altera os valores dos par�metros no banco comum. 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,	RemoteException {
		validate(mov);
		Parametro parametro = (Parametro) ((MovimentoCadastro) mov).getObjAuxiliar();
		ParametroHelper.getInstance().atualizaParametro(mov.getUsuarioLogado(), Sistema.SIGAA, parametro.getCodigo(), parametro.getValor());
		return parametro;
	}

	/** Valida os dados.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}
