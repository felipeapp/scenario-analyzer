/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 19/09/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.negocio;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.medio.dao.MatriculaDiscenteSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.SituacaoMatriculaSerie;
import br.ufrn.sigaa.ensino.negocio.ProcessadorDiscente;

/**
 * Processador que consome as matrículas em série a serem consolidadas na base de dados do sistema.
 * 
 * @author Rafael Gomes
 *
 */
public class ProcessadorConsolidacaoDiscenteSerie extends AbstractProcessador{

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,	RemoteException {
		
		validate(mov);
		MovimentoConsolidarSerie movimento = (MovimentoConsolidarSerie) mov;
		List<MatriculaDiscenteSerie> listMatriculaSerieUpdate = movimento.getListMatriculaSerie();
		GenericDAO dao = getGenericDAO(mov);
		Set<DiscenteMedio> discentesConsolidacao = new HashSet<DiscenteMedio>();
		try{
			for (MatriculaDiscenteSerie mds : listMatriculaSerieUpdate) {
				discentesConsolidacao.add(mds.getDiscenteMedio());
				if (mds.getId() != 0)
					dao.updateField(MatriculaDiscenteSerie.class, mds.getId(), "situacaoMatriculaSerie", mds.getSituacaoMatriculaSerie());
				else{
					dao.create(mds);
				}
			}
			for (DiscenteMedio discenteMedio : discentesConsolidacao) {
				alterarStatusDiscenteAfterConsolidarSerie(discenteMedio, movimento.getAno(), mov);
			}
		} finally {
			dao.close();
		}
		return null;
	}

	/** 
	 * Método responsável pela alteração do status do discente, quando este for realizada a alteração da situação da matricula na série.
	 * @param matriculaDiscenteSerie
	 * @param novaSituacao
	 * @param mov
	 * @throws ArqException
	 */
	private void alterarStatusDiscenteAfterConsolidarSerie(DiscenteMedio discenteMedio, Integer ano, Movimento mov ) throws ArqException{
		MatriculaDiscenteSerieDao dao = getDAO(MatriculaDiscenteSerieDao.class, mov);
		DiscenteDao discenteDao = getDAO(DiscenteDao.class, mov);
		try{
			ProcessadorDiscente processadorDiscente = new ProcessadorDiscente();
			List<MatriculaDiscenteSerie> matriculasSerieByAno = dao.findAllMatriculasByDiscente(discenteMedio, ano);
			int statusDiscente = discenteMedio.getStatus();
			for (MatriculaDiscenteSerie mds : matriculasSerieByAno) {
				if( SituacaoMatriculaSerie.APROVADO_DEPENDENCIA.getId() == mds.getSituacaoMatriculaSerie().getId() ){
					statusDiscente = StatusDiscente.ATIVO_DEPENDENCIA;
					break;
				} else if (mds.isDependencia() && !SituacaoMatriculaSerie.getSituacoesAprovadas().contains(mds.getSituacaoMatriculaSerie()) ){
					statusDiscente = StatusDiscente.ATIVO_DEPENDENCIA;
					break;
				} else {
					statusDiscente = StatusDiscente.ATIVO;
				}
				
				if (statusDiscente == SituacaoMatriculaSerie.APROVADO_DEPENDENCIA.getId())
					break;
			}
			
			if (discenteMedio.getStatus() != statusDiscente) {
				DiscenteAdapter discente = discenteDao.findByPK(discenteMedio.getId());
				processadorDiscente.persistirAlteracaoStatus(discente, statusDiscente, mov);
			}
		}finally{
			dao.close();
			discenteDao.close();
		}
	}
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}	
}
