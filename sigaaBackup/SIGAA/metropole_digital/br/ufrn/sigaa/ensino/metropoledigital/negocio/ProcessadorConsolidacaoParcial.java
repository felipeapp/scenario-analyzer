package br.ufrn.sigaa.ensino.metropoledigital.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ensino.metropoledigital.dao.ConsolidacaoParcialIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.ParametrosAcademicosIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.DadosTurmaIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.MatriculaTurma;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.NotaIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.ParametrosAcademicosIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.SituacaoMatriculaTurma;


/**
 * Processador respons�vel por realizar a consolida��o parcial das notas.
 * 
 * @author Rafael Silva
 *
 */
public class ProcessadorConsolidacaoParcial extends AbstractProcessador{

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		MovimentoConsolidacaoNotas movConsolidacao = (MovimentoConsolidacaoNotas) mov;
		ConsolidacaoParcialIMDDao dao = new ConsolidacaoParcialIMDDao();
		ParametrosAcademicosIMDDao parametrosDao = new ParametrosAcademicosIMDDao();
			
		try {
			//Recupera os par�metros ativos
			ParametrosAcademicosIMD parametros = parametrosDao.getParametrosAtivo();						

			SituacaoMatriculaTurma sit = new SituacaoMatriculaTurma();
						
			
			for (MatriculaTurma matriculaTurma : movConsolidacao.getMatriculas()) {				
				sit = new SituacaoMatriculaTurma();				
				
				//verifica se o aluno foi aprovado no m�dulo.
				if (matriculaTurma.getNotaParcial() >= parametros.getMediaAprovacao()) {
					sit.setId(SituacaoMatriculaTurma.APROVADO);
					matriculaTurma.setNotaFinal(matriculaTurma.getNotaParcial());
				}else{
					sit.setId(SituacaoMatriculaTurma.EM_RECUPERACAO);
				}				
				
				//Verifica se o discente foi reprovado por freq�ncia;				
				if (matriculaTurma.getChnf() > movConsolidacao.getModulo().getCargaHoraria()*parametros.getFrequenciaMinimaAprovacao()/100) {
					sit.setId(SituacaoMatriculaTurma.REPROVADO_FREQUENCIA);
				}
				
				//Verifica se o discente foi reprovado por nota componente; 
				List<NotaIMD> listaNotas =  dao.findNotasAluno(movConsolidacao.getTurmaEntrada().getId(), matriculaTurma.getDiscente().getDiscente().getId());				
				
				for (NotaIMD notaIMD : listaNotas) {					
					if (notaIMD.getMediaCalculada()<parametros.getNotaReprovacaoComponente()) {
						//sit.setId(SituacaoMatriculaTurma.REPROVADO_NOTA);//
						sit.setId(SituacaoMatriculaTurma.EM_RECUPERACAO);
						break;
					}												
				}
				
				//Atualiza a m�dia final do aluno caso o mesmo tenha sido reprovado
				if (sit.getId() == SituacaoMatriculaTurma.REPROVADO_NOTA || sit.getId()==SituacaoMatriculaTurma.REPROVADO_FREQUENCIA) {
					matriculaTurma.setNotaFinal(matriculaTurma.getNotaParcial());					
				}
				
				matriculaTurma.setSituacaoMatriculaTurma(sit);
				matriculaTurma.setTurma(movConsolidacao.getTurmaEntrada());				
				dao.createOrUpdate(matriculaTurma);
																			
			}
			dao.updateField(DadosTurmaIMD.class, movConsolidacao.getTurmaEntrada().getDadosTurmaIMD().getId(), "consolidadoParcialmente", true);
									
		} finally  {
			dao.close();			
		}		
		return null;
	}
	
	


	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}
}
