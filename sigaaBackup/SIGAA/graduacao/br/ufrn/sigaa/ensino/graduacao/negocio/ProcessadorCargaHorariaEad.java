/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 15/06/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ead.dao.CargaHorariaEadDAO;
import br.ufrn.sigaa.ead.dominio.CargaHorariaEad;

/** Processador responsável por persistir a Carga horária dedicada pelo docente no ensino à distância.
 * @author Édipo Elder F. de Melo
 *
 */
public class ProcessadorCargaHorariaEad extends AbstractProcessador {

	/** Persiste a Carga horária dedicada pelo docente no ensino à distância.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		@SuppressWarnings("unchecked")
		Collection<CargaHorariaEad> col = (Collection<CargaHorariaEad>) ((MovimentoCadastro) mov).getColObjMovimentado();
		@SuppressWarnings("unchecked")
		Collection<CargaHorariaEad> colRemover = (Collection<CargaHorariaEad>) ((MovimentoCadastro) mov).getObjAuxiliar();
		CargaHorariaEadDAO dao = getDAO(CargaHorariaEadDAO.class, mov);
		try {
			// persiste as cargas horárias atualizadas
			for (CargaHorariaEad chEad : col) {
				// limpa os atributos transientes
				if (isEmpty(chEad.getDocenteExterno()))
					chEad.setDocenteExterno(null);
				else
					chEad.setServidor(null);
				dao.createOrUpdate(chEad);
			}
			// remove as cargas horárias não mais referenciadas
			if (!isEmpty(colRemover)) {
				for (CargaHorariaEad chEad : colRemover) {
					dao.remove(chEad);
				}
			}
		} finally {
			dao.close();
		}
		return null;
	}

	/** Valida os dados antes de processar.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		if (isEmpty(((MovimentoCadastro) mov).getColObjMovimentado()))
				throw new NegocioException("Não há Carga Horária EAD para cadastrar.");
	}

}
