/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 02/09/2010
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.dominio.InscricaoSelecao;

/**
 * Processador responsável em validar as inscrições.
 * @author Mário Rizzi
 */
public class ProcessadorValidaInscricaoSelecao extends AbstractProcessador {

	/** Valida o pagamento das inscrições.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		GenericDAO dao = getGenericDAO(mov);

		try{
			@SuppressWarnings("unchecked")
			Collection<InscricaoSelecao> inscricoes = (Collection<InscricaoSelecao>) movimento.getColObjMovimentado();
			for (InscricaoSelecao inscricao : inscricoes) {
				dao.updateFields(InscricaoSelecao.class, inscricao.getId(),
					new String[] {"gruQuitada"}, 
					new Object[] {true});
			}
		}finally{
			dao.close();
		}
		return null;
	}

	/** Valida se há dados para processar.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		if (ValidatorUtil.isEmpty(((MovimentoCadastro) mov).getColObjMovimentado()))
			throw new NegocioException("Não há inscrições para validar.");
	}

}
