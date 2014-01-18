package br.ufrn.sigaa.ensino.metropoledigital.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.metropoledigital.dao.ConsolidacaoFinalIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.MatriculaTurma;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.NotaIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.SituacaoMatriculaTurma;

/**
 * Processador responsável por cadastrar as notas de recuperação dos alunos nas disciplinas da turma. 
 * 
 * @author Rafael Silva
 *
 */
public class ProcessadorCadastroNotasRecuperacao extends AbstractProcessador{

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {				
		MovimentoConsolidacaoNotas movConsolidacao = (MovimentoConsolidacaoNotas) mov;
		ConsolidacaoFinalIMDDao dao = new ConsolidacaoFinalIMDDao();
		try {			
			for (MatriculaTurma mt : movConsolidacao.getMatriculas()) {	
				//ATUALIZA NOTA RECUPERAÇÃO
				for (MatriculaComponente mc : mt.getMatriculasComponentes().values()) {	
					if (mc.getRecuperacao()!=null) {
						dao.updateField(MatriculaComponente.class, mc.getId(), "recuperacao", mc.getRecuperacao());
					}					
				}

				// ATUALIZA A MEDIA FINAL DA MATRICULA COMPONENTE
				List<NotaIMD> notasIMD = dao.findNotasAluno(movConsolidacao.getTurmaEntrada().getId(), mt.getDiscente().getId());
				for (MatriculaComponente mc : mt.getMatriculasComponentes().values()) {						
					if (mc.getRecuperacao()!=null) {					
						for (NotaIMD nota : notasIMD) {
							if (nota.getMatriculaComponente().getId() == mc.getId()) {
								mc.setMediaFinal(nota.getMediaCalculada());
								dao.updateField(MatriculaComponente.class, mc.getId(), "mediaFinal", mc.getMediaFinal());
							}						
						}						
					}					
				}
				
				// ATUALIZA A MÉDIA FINAL DA ENTIDADE MATRÍCULA TURMA					
				dao.updateField(MatriculaTurma.class, mt.getId(), "notaFinal", mt.getMediaCalculada(notasIMD));				
			}
		} finally {
			dao.close();
		}					
		return null;
	}
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// TODO Auto-generated method stub
		
	}
}
