/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/08/09 - 21:21:00
 */
package br.ufrn.sigaa.ead.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.ead.TutoriaAlunoDao;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;
import br.ufrn.sigaa.ead.dominio.TutoriaAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;

/**
 * Processador para associação entre alunos e tutor orientador
 * 
 * @author David Pereira
 *
 */
public class ProcessadorTutoriaAluno extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		TutoriaAlunoMov tMov = (TutoriaAlunoMov) mov;
		TutoriaAlunoDao dao = getDAO(TutoriaAlunoDao.class, mov);
		
		Collection<DiscenteGraduacao> discentes = tMov.getDiscentes();
		TutorOrientador tutor = tMov.getTutor();
		
		for (DiscenteGraduacao d : discentes) {
			
			// Se tiver uma tutoria anteriormente, substitui pela nova
			TutoriaAluno ultimaTutoria = dao.findUltimoByAluno(d.getId());
			if (ultimaTutoria != null) {
				ultimaTutoria.setFim(new Date());
				ultimaTutoria.setAtivo(false);
				
				dao.update(ultimaTutoria);
			}
			
			TutoriaAluno tutoria = new TutoriaAluno();
			tutoria.setAluno(d);
			tutoria.setTutor(tutor);
			tutoria.setInicio(new Date());
			tutoria.setAtivo(true);
			
			dao.create(tutoria);
		}
		
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}
