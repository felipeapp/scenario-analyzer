/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;

/**
 * Responsável por analisar quais registros de solicitação de matrículas não
 * foram analisadas pelos coordenadores e registrá-las como matrículas em aberta
 *
 * @author Andre M Dantas
 *
 */
public class ProcessadorSolicitacoesNaoAnalisadas extends ProcessadorCadastro {

	/*
	 * (non-Javadoc)
	 *
	 * @see br.ufrn.arq.ejb.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		SolicitacaoMatriculaDao dao = getDAO(SolicitacaoMatriculaDao.class, mov);

		try {
			Collection<SolicitacaoMatricula> cadastradas = dao.findCadastradasByAnoPeriodo(1, 2);
			for (SolicitacaoMatricula sol : cadastradas) {
				sol.setStatus(SolicitacaoMatricula.VISTO_EXPIRADO);
				sol.setPrazoExpirado(true);
				MatriculaComponente mat = new MatriculaComponente();
				mat.setDataCadastro(new Date());
				mat.setDiscente(sol.getDiscente());
				mat.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());

				mat.setSituacaoMatricula(SituacaoMatricula.EM_ESPERA);
				mat.setTurma(sol.getTurma());
				mat.setNotas(null);
				mat.setRecuperacao(null);
				dao.create(mat);
				sol.setMatriculaGerada(mat);
				dao.update(sol);
			}
		} finally {
			dao.close();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see br.ufrn.arq.ejb.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// TODO Auto-generated method stub

	}

}
