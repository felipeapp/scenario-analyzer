/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '22/01/2010'
 *
 */

package br.ufrn.sigaa.ava.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;

/**
 * Processador responsável por salvar as notas importadas pela planilha fornecida pelo professor.
 * 
 * @author Fred_Castro
 *
 */

public class ProcessadorImportarNotasPlanilha extends AbstractProcessador{

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoImportacaoNotasPlanilha personalMov = (MovimentoImportacaoNotasPlanilha) mov;
		
		List <MatriculaComponente> ms = personalMov.getMatriculasAAtualizar();
		List <Avaliacao> as = personalMov.getAvaliacoesAAtualizar();
		List <NotaUnidade> ns = personalMov.getNotasAAtualizar();
		
		GenericDAO dao = null;
		
		try {
			dao = getGenericDAO(personalMov);
			
			for (MatriculaComponente m : ms)
				dao.updateFields(MatriculaComponente.class, m.getId(), new String [] {"recuperacao", "numeroFaltas", "mediaFinal", "apto"}, new Object [] {m.getRecuperacao(), m.getNumeroFaltas(), m.getMediaFinal(), m.getApto()});
			
			for (Avaliacao a : as)
				dao.updateField(Avaliacao.class, a.getId(), "nota", a.getNota());
			
			for (NotaUnidade n : ns)
				dao.updateField(NotaUnidade.class, n.getId(), "nota", n.getNota());
		
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}
