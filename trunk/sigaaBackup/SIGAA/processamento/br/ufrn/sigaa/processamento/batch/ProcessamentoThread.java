/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 16/01/2009 
 *
 */

package br.ufrn.sigaa.processamento.batch;

import java.rmi.RemoteException;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.Processador;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.dominio.TurmaMov;
import br.ufrn.sigaa.processamento.dominio.ModoProcessamentoMatricula;

/**
 * Thread que consome as turmas.
 *
 * @author Gleydson
 *
 */
public class ProcessamentoThread extends ProcessamentoBatchThread<Turma> {

	private CalendarioAcademico calendario;

	private ModoProcessamentoMatricula modo;

	private boolean rematricula;
	
	public ProcessamentoThread(ModoProcessamentoMatricula modo, boolean rematricula, ListaProcessamentoBatch<Turma> lista) throws DAOException {
		this.modo = modo;
		this.lista = lista;
		this.rematricula = rematricula;
		calendario = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
	}
	
	@Override
	public void processar(Processador processador, Turma turma) throws NegocioException, ArqException, RemoteException {
		TurmaMov mov = new TurmaMov();
		mov.setCalendario(calendario);
		mov.setTurma(turma);
		if (modo.equals(ModoProcessamentoMatricula.GRADUACAO))
			mov.setCodMovimento(SigaaListaComando.PROCESSAR_MATRICULA_GRADUACAO);
		else if (modo.equals(ModoProcessamentoMatricula.GRADUACAO_FERIAS))
			mov.setCodMovimento(SigaaListaComando.PROCESSAR_MATRICULA_GRADUACAO_FERIAS);
		else
			mov.setCodMovimento(SigaaListaComando.PROCESSAR_MATRICULA_MUSICA);
		mov.setUsuarioLogado(new Usuario(Usuario.USUARIO_SISTEMA));
		mov.setSistema(Sistema.SIGAA);
		mov.setRematricula(rematricula);
		
		processador.execute(mov);
		
	}
	
}
