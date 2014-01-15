package br.ufrn.sigaa.monitoria.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.SolicitacaoRenovacao;
import br.ufrn.sigaa.monitoria.dominio.StatusSolicitacaoRenovacao;

/**
 * Processador para cadastro de solicitações de renovação.
 *
 * @author David Ricardo
 *
 */
public class ProcessadorSolicitarRenovacao extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		MovimentoCadastro cMov = (MovimentoCadastro) mov;
		ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class, cMov);
		ProjetoEnsino pm = (ProjetoEnsino) cMov.getObjMovimentado();

		try {
			SolicitacaoRenovacao solicitacao = new SolicitacaoRenovacao();
			solicitacao.setData(new Date());
			solicitacao.setProjetoEnsino(pm);
			solicitacao.setStatus(StatusSolicitacaoRenovacao.getAguardandoAprovacao());
			dao.create(solicitacao);
			return null;
		} finally {
			dao.close();
		}
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro cMov = (MovimentoCadastro) mov;
		ProjetoEnsino pm = (ProjetoEnsino) cMov.getObjMovimentado();
		if (pm.getAno() >= CalendarUtils.getAnoAtual()) {
			throw new NegocioException("O ano contrato deve ser anterior ao ano atual.");
		}
	}

}