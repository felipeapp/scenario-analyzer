package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;

/** Processa a inativação de componentes curriculares de um departamento.
 * @author Édipo Elder F. de Melo
 *
 */
public class ProcessadorInativarComponentesDepartamento extends ProcessadorComponenteCurricular {

	/** Processa a inativação de componentes curriculares de um departamento.
	 * @see br.ufrn.sigaa.ensino.negocio.ProcessadorComponenteCurricular#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		@SuppressWarnings("unchecked")
		Collection<ComponenteCurricular> componentes = (Collection<ComponenteCurricular>) movimento.getColObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);
		try {
			for (ComponenteCurricular componente : componentes) {
				componente = dao.findByPrimaryKey(componente.getId(), ComponenteCurricular.class);
				dao.detach(componente);
				movimento.setObjMovimentado(componente);
				desativar(movimento);
			}
		} finally {
			dao.close();
		}
		return null;
	}

	/** Valida a inativação de componentes curriculares de um departamento.
	 * @see br.ufrn.sigaa.ensino.negocio.ProcessadorComponenteCurricular#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ListaMensagens erros = new ListaMensagens();
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		validateRequired(movimento.getColObjMovimentado(), "List de Componentes Curriculares", erros);
		checkValidation(erros);
	}

}
