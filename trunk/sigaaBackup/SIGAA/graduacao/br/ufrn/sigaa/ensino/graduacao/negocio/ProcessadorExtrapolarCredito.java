/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.graduacao.ExtrapolarCreditoDao;
import br.ufrn.sigaa.ensino.graduacao.dominio.ExtrapolarCredito;

public class ProcessadorExtrapolarCredito extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		MovimentoExtrapolarCredito mec = (MovimentoExtrapolarCredito) mov;

		if (mec.getAcao() == MovimentoExtrapolarCredito.CADASTRAR)
			cadastrar(mec);
		else if (mec.getAcao() == MovimentoExtrapolarCredito.EXCLUIR)
			excluir(mec);

		return null;
	}

	private void excluir(MovimentoExtrapolarCredito mec) throws DAOException {
		ExtrapolarCredito extrapolarCredito = mec.getExtrapolarCredito();
		extrapolarCredito.setAtivo(false);
		
		ExtrapolarCreditoDao dao = getDAO(ExtrapolarCreditoDao.class, mec);
		dao.update(extrapolarCredito);
	}

	private void cadastrar(MovimentoExtrapolarCredito mec) throws DAOException, NegocioException {
		ExtrapolarCredito extrapolarCredito = mec.getExtrapolarCredito();
		extrapolarCredito.setAtivo(true);
		ExtrapolarCreditoDao dao = getDAO(ExtrapolarCreditoDao.class, mec);

		ExtrapolarCredito permissao = dao.findPermissaoAtivo(mec.getExtrapolarCredito().getDiscente().getDiscente(), 
				mec.getExtrapolarCredito().getAno(), 
				mec.getExtrapolarCredito().getPeriodo());

		if (permissao != null)
			throw new NegocioException(
					"Este aluno já possui a permissão de extrapolar creditos");
		else
			dao.create(extrapolarCredito);

	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
