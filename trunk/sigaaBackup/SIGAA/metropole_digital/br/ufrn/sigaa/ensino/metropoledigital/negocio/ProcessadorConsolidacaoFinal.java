package br.ufrn.sigaa.ensino.metropoledigital.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.metropoledigital.dao.ConsolidacaoFinalIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.ConsolidacaoParcialIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.ParametrosAcademicosIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.DadosTurmaIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.MatriculaTurma;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.NotaIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.ParametrosAcademicosIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.SituacaoMatriculaTurma;

/**
 * 
 * @author Rafael Silva
 * 
 */
public class ProcessadorConsolidacaoFinal extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		MovimentoConsolidacaoNotas movConsolidacao = (MovimentoConsolidacaoNotas) mov;
		ConsolidacaoFinalIMDDao dao = new ConsolidacaoFinalIMDDao();
			
		try {			
			for (MatriculaTurma mt : movConsolidacao.getMatriculas()) {
				//Atualiza a situação do aluno no banco de dados.								
				dao.updateField(MatriculaTurma.class, mt.getId(), "situacaoMatriculaTurma", mt.getSituacaoMatriculaTurma().getId());										
																
				//Atualiza a situação da matrícula componente
				for (MatriculaComponente mc : mt.getMatriculasComponentes().values()) {
					switch (mt.getSituacaoMatriculaTurma().getId()) {
						case SituacaoMatriculaTurma.APROVADO:
							dao.updateField(MatriculaComponente.class, mc.getId(), "situacaoMatricula", SituacaoMatricula.APROVADO);
							break;
							
						case SituacaoMatriculaTurma.REPROVADO_FREQUENCIA:
							dao.updateField(MatriculaComponente.class, mc.getId(), "situacaoMatricula", SituacaoMatricula.REPROVADO_FALTA);
							break;
							
						case SituacaoMatriculaTurma.REPROVADO_NOTA:
							dao.updateField(MatriculaComponente.class, mc.getId(), "situacaoMatricula", SituacaoMatricula.REPROVADO);
							break;
		
						default:
							break;
					}
				}
				
				//Altera o status das turmas compoententes vinculadas a turma de entrada de "ABERTA" para "CONSOLIDADA".
				List<Turma> turmasComponentes = dao.findTurmasComponente(movConsolidacao.getTurmaEntrada().getId());
				for (Turma turma : turmasComponentes) {
					dao.updateField(Turma.class, turma.getId(), "situacaoTurma", SituacaoTurma.CONSOLIDADA);
				}							
			}
													
		} finally  {
			dao.close();			
		}		
		return null;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// TODO Auto-generated method stub

	}

}
