package br.ufrn.sigaa.ead.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ead.dominio.LoteMatriculasDiscente;
import br.ufrn.sigaa.ead.jsf.MovimentoLoteMatricula;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoMatriculaGraduacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.ProcessadorMatriculaGraduacao;

/**
 * Chama o processador de matricula graduação e faz varias matriculas de uma vez.
 * 
 * @author Henrique André
 *
 */
public class ProcessadorLoteMatricula extends AbstractProcessador {
	int cont =0;
	public Object execute(Movimento mov) throws NegocioException, ArqException,	RemoteException {
		long init = System.currentTimeMillis();

		MovimentoLoteMatricula movLote = (MovimentoLoteMatricula) mov;
		List<LoteMatriculasDiscente> loteMatriculas = movLote.getLoteMatriculas();
		
		ProcessadorMatriculaGraduacao pmg = new ProcessadorMatriculaGraduacao();
		
		for (LoteMatriculasDiscente lote : loteMatriculas) {
			
			// movimentos pra matricula
			MovimentoMatriculaGraduacao movMG = new MovimentoMatriculaGraduacao();
			
			movMG.setUsuarioLogado(mov.getUsuarioLogado());
			movMG.setSistema(mov.getSistema());
			
			movMG.setDiscente(lote.getDiscente());
			movMG.setTurmas(lote.getTurmas());
			movMG.setMatriculaEAD(true);
			movMG.setMatriculaConvenio(false);
			movMG.setAtualizarStatusDiscenteETiposIntegralizacao(false);
			movMG.setCodMovimento(SigaaListaComando.MATRICULAR_GRADUACAO);
			movMG.setCalendarioAcademicoAtual(lote.getCalendario());
			movMG.setLoteMatriculasDiscente(lote);
			
			
			pmg.execute(movMG);
			System.out.println(++cont);
		}

		long fim = System.currentTimeMillis();
		
		System.out.println(cont);
		System.out.println("TEMPO: " + (fim - init));

		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
