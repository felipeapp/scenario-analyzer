/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.processamento;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.vinculo.dao.VinculosDao;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoDiscente;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Processa os v�nculos de discente
 * 
 * @author Henrique Andr�
 *
 */
public class ProcessarDiscente extends ProcessarVinculoExecutor {

	@Override
	public void processar(HttpServletRequest req, DadosProcessamentoVinculos dados) throws ArqException {
		
		VinculosDao dao = getDAO(VinculosDao.class, req);
		
		try {
			// DISCENTES
			List<DiscenteAdapter> discentes = dao.findDiscentes(dados.getUsuario().getPessoa());
			
			if (isNotEmpty(discentes)) {
				
				for (DiscenteAdapter d : discentes) {
					dados.addVinculo(d.getUnidade(),  d.isAtivo(), new TipoVinculoDiscente(d));
				}
			}
			
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	/**
	 * Indica se o aluno possui solicita��o de matr�cula
	 * 
	 * @param solDao
	 * @param vinculoUsuario
	 * @return
	 * @throws ArqException 
	 */
	private boolean isAlunoPossuiSolicitacaoMatricula(Discente discente, HttpServletRequest req) throws ArqException {
		SolicitacaoMatriculaDao matDao = getDAO(SolicitacaoMatriculaDao.class, req);
		try {
			int count = matDao.countByDiscenteAnoPeriodo(discente, 
					discente.getAnoIngresso(), 
					discente.getPeriodoIngresso(), 
					SolicitacaoMatricula.CADASTRADA, 
					SolicitacaoMatricula.ORIENTADO, 
					SolicitacaoMatricula.SUBMETIDA,
					SolicitacaoMatricula.VISTO_EXPIRADO,
					SolicitacaoMatricula.SOLICITADA_COORDENADOR,
					SolicitacaoMatricula.VISTA);
			
			if (count == 0)
				return false;
			
			return true;			
		} finally {
			matDao.close();
		}
	}
}
