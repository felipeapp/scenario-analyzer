/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/06/2010
 *
 */
package br.ufrn.sigaa.vestibular.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.vestibular.dominio.InscricaoVestibular;

/** Processador respons�vel pela valida��o em lote de inscri��es para o vestibular.
 * @author �dipo Elder F. Melo
 *
 */
public class ProcessadorValidarInscricaoVestibular extends AbstractProcessador {

	/** Valida as inscri��es dos candidatos.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,	RemoteException {
		validate(mov);
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		GenericDAO dao = getGenericDAO(mov);
		@SuppressWarnings("unchecked")
		Collection<InscricaoVestibular> inscricoes = (Collection<InscricaoVestibular>) movimento.getColObjMovimentado();
		for (InscricaoVestibular inscricao : inscricoes) {
			dao.updateFields(InscricaoVestibular.class, inscricao.getId(),
				new String[] {"validada", "observacao"}, 
				new Object[] {true, inscricao.getObservacao()});
		}
		dao.close();
		return null;
	}

	/** Valida os dados a persistir.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		if (ValidatorUtil.isEmpty(((MovimentoCadastro) mov).getColObjMovimentado()))
				throw new NegocioException("N�o h� inscri��es para validar.");
	}

}
