/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
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
 * Altera parâmetros no banco comum a partir de operações no SIGAA.
 * 
 * @author Édipo Elder F. Melo
 * @author Leonardo Campos
 *
 */
public class ProcessadorAlterarParametros extends AbstractProcessador {

	/** Altera os valores dos parâmetros no banco comum. 
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
