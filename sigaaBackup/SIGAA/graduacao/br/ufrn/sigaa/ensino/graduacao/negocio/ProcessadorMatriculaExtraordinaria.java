/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 03/08/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ParametrosGestoraAcademicaDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dao.MatriculaExtraordinariaDao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatriculaComponenteExtraordinaria;
import br.ufrn.sigaa.ensino.graduacao.dominio.SequenciaMatriculaExtraordinaria;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;

/**
 * Processador responsável pela realização de matrículas extraordinárias de
 * alunos em turmas com vagas remanescentes.
 * 
 * @author Leonardo Campos
 * 
 */
public class ProcessadorMatriculaExtraordinaria extends AbstractProcessador {

	/* (non-Javadoc)
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		MatriculaExtraordinariaDao dao = getDAO(MatriculaExtraordinariaDao.class, mov);
		
		try {
			MovimentoMatriculaGraduacao matriculaMov = (MovimentoMatriculaGraduacao) mov;
			Turma t = matriculaMov.getTurmas().iterator().next();
			
			SequenciaMatriculaExtraordinaria sequencia = dao.getSequenciaMatriculaExtraordinaria(t);

			ProcessadorMatriculaGraduacao processador = new ProcessadorMatriculaGraduacao();
			matriculaMov = (MovimentoMatriculaGraduacao) processador.execute(mov);
			
			sequencia.incrementarSequencia();
			
			MatriculaComponenteExtraordinaria mce = new MatriculaComponenteExtraordinaria();
			mce.setMatriculaComponente(matriculaMov.getMatriculaGerada());
			mce.setOrdem(sequencia.getSequencia());
			dao.create(mce);
			
			// Cria uma nova ou atualiza a sequência existente
			dao.createOrUpdate(sequencia);
		} finally {
			dao.close();
		}
		
		
		return null;
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
	}
	
}
