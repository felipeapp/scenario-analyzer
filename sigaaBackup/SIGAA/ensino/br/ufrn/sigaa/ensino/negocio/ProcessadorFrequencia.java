/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.FrequenciaAluno;
import br.ufrn.sigaa.ensino.dominio.TipoCaptcaoFrequencia;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.dominio.FrequenciaMov;

/**
 * Classe que implementa o processador para lançar as frequências do
 * aluno de uma turma.
 *
 * @author Gleydson
 *
 */
public class ProcessadorFrequencia extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		FrequenciaMov freqMov = (FrequenciaMov) mov;
		GenericDAO dao = getGenericDAO(mov);

		validate(mov);
		
		try {
			if (SigaaListaComando.LANCAR_FREQUENCIA.equals(mov.getCodMovimento())) {
				if (freqMov.getFrequencias() != null) {
					for (FrequenciaAluno f : freqMov.getFrequencias()) {
						if (f.getId() == 0)
							dao.create(f);
						else {
							if (f.getTipoCaptcaoFrequencia() == null)
								f.setTipoCaptcaoFrequencia(TipoCaptcaoFrequencia.TURMA_VIRTUAL);
						
							dao.updateFields(FrequenciaAluno.class, f.getId(), new String [] {"faltas", "tipoCaptcaoFrequencia"}, new Object [] {f.getFaltas(), f.getTipoCaptcaoFrequencia()});
						}
					}
				}
			} else {
				for (FrequenciaAluno f : freqMov.getFrequencias()) {
					if (f.getId() != 0) {
						f = dao.findByPrimaryKey(f.getId(), FrequenciaAluno.class);
						if (f != null) {
							f.setAtivo(false);
							dao.update(f);	
						}
					}
				}
			}
		} finally {
			dao.close();
		}

		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

		if (SigaaListaComando.LANCAR_FREQUENCIA.equals(mov.getCodMovimento())) {
			
			TurmaDao turmaDao = null;
			FrequenciaMov freqMov = (FrequenciaMov) mov;
			
			try {
				turmaDao = getDAO(TurmaDao.class, mov);
				ArrayList<FrequenciaAluno> frequencias = null; 
		
				Turma t = freqMov.getFrequencias().iterator().next().getTurma();
				Date dataSelecionada = freqMov.getFrequencias().iterator().next().getData();
				frequencias = (ArrayList<FrequenciaAluno>) turmaDao.findFrequenciasByTurma(t, dataSelecionada);
				ArrayList<FrequenciaAluno> frequenciasMov = (ArrayList<FrequenciaAluno>) freqMov.getFrequencias();
				
				if (frequencias != null && !frequencias.isEmpty() && frequenciasMov != null && !frequenciasMov.isEmpty()){			
					
					for (FrequenciaAluno f : frequencias){
						for (FrequenciaAluno fMov : frequenciasMov){
							// Verifica se as frequencias já foram cadastradas.
							if (	f.getDiscente() != null && fMov.getDiscente() != null 
									&& f.getDiscente().getId() == fMov.getDiscente().getId()
									&& f.getId() > 0 && fMov.getId() == 0
								)
								throw new NegocioException ("Frequências já cadastrada para esta data.");
						}						
					}
				}								
					
			} finally {
				if (turmaDao != null)
					turmaDao.close();
			}
		}
	}


}
