/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 05/12/2007
 * 
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.diploma.dao.RegistroDiplomaDao;
import br.ufrn.sigaa.diploma.dominio.RegistroDiploma;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Processador para realizar a altera��o da data da cola��o dos discentes 
 * 
 * @author leonardo
 * 
 */
public class ProcessadorAlterarDataColacao extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		validate(mov);

		GenericDAO dao = getGenericDAO(mov);
		MovimentoCadastro movc = (MovimentoCadastro) mov;
		MovimentacaoAluno movConclusao = (MovimentacaoAluno) movc.getObjMovimentado();
		DiscenteAdapter discente = movConclusao.getDiscente();

		// atualiza apenas o ano, per�odo e a data de cola��o da movimenta��o
		dao.updateFields(MovimentacaoAluno.class, movConclusao.getId(),
				new String[] { "anoReferencia", "periodoReferencia", "anoOcorrencia", "periodoOcorrencia" },
				new Object[] { movConclusao.getAnoReferencia(),
						movConclusao.getPeriodoReferencia(),
						movConclusao.getAnoOcorrencia(),
						movConclusao.getPeriodoOcorrencia()});
		
		// atualiza apenas o campo data de cola��o do discente
		dao.updateField(Discente.class, discente.getId(), "dataColacaoGrau",
				discente.getDataColacaoGrau());
		// verifca e atualiza o registro de diplomas do discente
		atualizaRegistroDiploma(mov);
		dao.close();
		return discente;
	}

	/** Atualiza o registro de diplomas para a nova data de cola��o de grau.
	 * @param mov
	 * @throws DAOException
	 */
	private void atualizaRegistroDiploma(Movimento mov) throws DAOException {
		RegistroDiplomaDao dao = getDAO(RegistroDiplomaDao.class, mov);
		MovimentacaoAluno movConclusao = (MovimentacaoAluno) ((MovimentoCadastro) mov).getObjMovimentado();
		DiscenteAdapter discente = movConclusao.getDiscente();
		RegistroDiploma registro = dao.findByDiscente(discente.getId());
		if (registro != null) {
			dao.updateField(RegistroDiploma.class, registro.getId(), "dataColacao", discente.getDataColacaoGrau());
		}
		dao.close();
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		// a se definir valida��es relevantes
		// pode-se colar grau antes do final do per�odo letivo de sa�da?
	}

}
