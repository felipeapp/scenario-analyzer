/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on 06/06/2007
 *
 */
package br.ufrn.sigaa.batch;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dominio.OperacaoLote;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ClassificacaoRelatorio;

/**
 * Classe que encapsula o processamento assíncrono de ma tarefa.
 * O andamento da operação é armazenado em um objeto ProcessadorLote
 *
 * @author Ricardo Wendell
 *
 */
public class ProcessadorLote implements Runnable{

	private Callable<Void> tarefa;

	private OperacaoLote operacaoLote;

	public ProcessadorLote(Callable<Void> tarefa, int codigoOperacao, int idLote, int tamanhoLote) throws NegocioException {
		this.tarefa = tarefa;

		//TODO: Verificar se já existe uma operacao com o codigo informado em andamento
		if ( false ) {
			throw new NegocioException();
		}

		operacaoLote = new OperacaoLote();
		operacaoLote.setCodigoOperacao( codigoOperacao );
		operacaoLote.setIdLote(idLote);
		operacaoLote.setTamanhoLote(tamanhoLote);

	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		FutureTask<Void> task = new FutureTask<Void> ( tarefa );
		ExecutorService es = Executors.newSingleThreadExecutor();

		GenericDAO dao = new GenericDAOImpl(Sistema.SIGAA);

		try {
			// Criar registro da operação em lote
			operacaoLote.setInicio( new Date() );
			operacaoLote.setStatus( OperacaoLote.EM_ANDAMENTO );
			dao.updateField(ClassificacaoRelatorio.class, operacaoLote.getIdLote(), "statusProcessamento", ClassificacaoRelatorio.EM_PROCESSAMENTO);
			dao.create( operacaoLote );

			// Iniciar tarefa
			es.submit(task);
			task.get();

			// Atualizar registro com o status final do processamento
			operacaoLote.setStatus( OperacaoLote.FINALIZADA );
			dao.updateField(ClassificacaoRelatorio.class, operacaoLote.getIdLote(), "statusProcessamento", ClassificacaoRelatorio.PROCESSADO_COM_SUCESSO);
			operacaoLote.setFim( new Date() );
			try {
				dao.update( operacaoLote );
			} catch (DAOException e) {
				e.printStackTrace();
			}
		} catch (CancellationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			// Gravar erro
			operacaoLote.setFim( new Date() );
			operacaoLote.setStatus( OperacaoLote.ERRO );
			operacaoLote.setLog( Arrays.toString(e.getCause().getCause().getStackTrace()).replace(',', '\n') );

			System.out.println( e.getStackTrace() );
			System.out.println( e.getCause().getStackTrace());
			System.out.println( e.getCause().getCause().getStackTrace() );

			try {
				dao.update( operacaoLote );
				dao.updateField(ClassificacaoRelatorio.class, operacaoLote.getIdLote(), "statusProcessamento", ClassificacaoRelatorio.PROCESSADO_COM_FALHA);
			} catch (DAOException e1) {
				e1.printStackTrace();
			}
		} finally {
			dao.close();
			es.shutdown();
		}
	}

}
