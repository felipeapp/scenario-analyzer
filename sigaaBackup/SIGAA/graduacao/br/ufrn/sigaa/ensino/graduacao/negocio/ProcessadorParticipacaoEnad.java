/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoENADE;

/**
 * Processador responsável por atualizar a participação do discente de graduação
 * no ENADE.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
public class ProcessadorParticipacaoEnad extends AbstractProcessador {

	/** Atualiza a participação do discente no ENADE
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		checkRole(SigaaPapeis.DAE, mov);
		validate(mov);

		MovimentoCadastro movCadastro = (MovimentoCadastro) mov;
		GenericDAO dao = getGenericDAO(mov);

		try {
			if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_PARTICIPACAO_ENADE_LOTE)) {
				@SuppressWarnings("unchecked")
				Collection<DiscenteGraduacao> lista = (Collection<DiscenteGraduacao>) movCadastro.getColObjMovimentado();
				TipoENADE tipo = (TipoENADE) movCadastro.getObjAuxiliar();
				for (DiscenteGraduacao discente : lista) {
					if (discente.isMatricular()) {
						if (tipo == TipoENADE.INGRESSANTE) {
							dao.updateField(DiscenteGraduacao.class, discente.getId(), "participacaoEnadeIngressante", discente.getParticipacaoEnadeIngressante());
							dao.updateField(DiscenteGraduacao.class, discente.getId(), "dataProvaEnadeIngressante", discente.getDataProvaEnadeIngressante());
						} else {
							dao.updateField(DiscenteGraduacao.class, discente.getId(), "participacaoEnadeConcluinte", discente.getParticipacaoEnadeConcluinte());
							dao.updateField(DiscenteGraduacao.class, discente.getId(), "dataProvaEnadeConcluinte", discente.getDataProvaEnadeConcluinte());
						}
					}
				}
				return lista;
			} else {
				DiscenteGraduacao discente = movCadastro.getObjMovimentado();
				dao.updateField(DiscenteGraduacao.class, discente.getId(), "participacaoEnadeIngressante", discente.getParticipacaoEnadeIngressante());
				dao.updateField(DiscenteGraduacao.class, discente.getId(), "dataProvaEnadeIngressante", discente.getDataProvaEnadeIngressante());
				dao.updateField(DiscenteGraduacao.class, discente.getId(), "participacaoEnadeConcluinte", discente.getParticipacaoEnadeConcluinte());
				dao.updateField(DiscenteGraduacao.class, discente.getId(), "dataProvaEnadeConcluinte", discente.getDataProvaEnadeConcluinte());
				return discente;
			}
		} catch (Exception e) {
			throw new ArqException(e);
		} finally {
			dao.close();
		}

	}

	/** Valida os dados obrigatórios para a operação.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro movDiscente = (MovimentoCadastro) mov;
		DiscenteGraduacao discente = movDiscente.getObjMovimentado();
		// Realizar validações
		ListaMensagens lista = new ListaMensagens();
		if (discente != null) {
			validateRequiredId(discente.getId(), "Discente", lista);
		}
		checkValidation(lista);
	}

}
