/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 05/12/2006
 *
 */
package br.ufrn.sigaa.ensino.tecnico.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.tecnico.dao.TurmaEntradaTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;

/**
 * Processador responsável pelo cadastro/aletaração de Turmas de Entrada.
 * @author Andre M Dantas
 *
 */
public class ProcessadorTurmaEntrada extends ProcessadorCadastro {


	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {

		validate(movimento);

		if (SigaaListaComando.CADASTRAR_TURMA_ENTRADA_TEC.equals(movimento.getCodMovimento())) {
			criar((MovimentoCadastro) movimento);
		} else if (SigaaListaComando.ALTERAR_TURMA_ENTRADA_TEC.equals(movimento.getCodMovimento())) {
			alterar((MovimentoCadastro) movimento);
		}

		return null;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ListaMensagens erros = new ListaMensagens();
		TurmaEntradaTecnico turmaMov = (TurmaEntradaTecnico) ((MovimentoCadastro) mov).getObjMovimentado();
		//teste de nulos
		if (turmaMov.getEspecializacao() != null && turmaMov.getEspecializacao().getId() == 0)
			turmaMov.setEspecializacao(null);

		TurmaEntradaTecnicoDao dao = getDAO(TurmaEntradaTecnicoDao.class, mov);
		// verifica regra de unicidade
		boolean objUnico = dao.validateUniqueRules(turmaMov);
		dao.close();
		if (!objUnico) {
			erros.addErro("Não foi possível realizar a operação.<br>" +
					"Já existe turma de entrada nesse ano e período para esse curso");
		}
		checkValidation(erros);
	}
}
