/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 21/09/2010
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

/** Processador responsável por definir o local de aplicação de provas de um conjunto de candidatos do vestibular.
 * @author Édipo Elder F. Melo
 *
 */
public class ProcessadorLocalProvaCandidato extends AbstractProcessador {

	/** Define os locais de provas dos candidatos.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		GenericDAO dao = getGenericDAO(mov);
		@SuppressWarnings("unchecked")
		Collection<InscricaoVestibular> inscricoes = (Collection<InscricaoVestibular>) movimento.getColObjMovimentado();
		for (InscricaoVestibular inscricao : inscricoes) {
			dao.updateFields(InscricaoVestibular.class, inscricao.getId(),
				new String[] {"localProva", "turma"}, 
				new Object[] {inscricao.getLocalProva(), inscricao.getTurma()});
		}
		dao.close();
		return null;
	}

	/** Verifica se a lista de candidatos não é vazia.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		if (ValidatorUtil.isEmpty(((MovimentoCadastro) mov).getColObjMovimentado()))
			throw new NegocioException("Não há dados para importar.");
	}

}
