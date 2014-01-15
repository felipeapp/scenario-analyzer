/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 03/09/2004
 */
package br.ufrn.arq.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.CreateException;
import javax.ejb.EJBException;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import br.ufrn.arq.dao.ClosableResource;
import br.ufrn.arq.dao.ThreadScopedResourceCache;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.management.MovimentoPerformance;
import br.ufrn.arq.seguranca.log.LogProcessorDelegate;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Implementação do Bean de Fachada. Implementa o Pattern Session Façade.
 *
 * @author Gleydson Lima
 */
@SuppressWarnings("serial")
public class ArqFacadeBean extends SessionAdapter {

	/** Lista que armazena os movimentos habilitados para acesso externo */
	private static final List<Comando> movimentosExternos = new ArrayList<Comando>();

	static {
		movimentosExternos.add(ArqListaComando.MENSAGEM_ENVIAR);
		movimentosExternos.add(ArqListaComando.MENSAGEM_EXCLUIR);
		movimentosExternos.add(ArqListaComando.MENSAGEM_LIDA);
	}

	public void ejbCreate() throws CreateException {

	}

	public Object execute(final Movimento mov) throws ArqException, NegocioException, RemoteException {
		TransactionTemplate template = getTransactionTemplate(mov);
		List<Comando> blockedCommands = initBlockedCommands(mov);
		
		try {
			
			if (mov.getCodMovimento().equals(ArqListaComando.PREPARE_MOVIMENTO)) {
				prepare(mov.getUsuarioLogado(), mov.getComandosBloqueados(), mov.getId());
				return null;
			} else {
				Object retorno = null;

				if (!blockedCommands.contains(mov.getCodMovimento())) {
					if (mov.getCodMovimento().getId() != ArqListaComando.LOGON_COD) {
						blockedCommands.add(mov.getCodMovimento());
					}
					
					long init = System.currentTimeMillis();
					
					if (template != null) {
						retorno = executeComTxTemplate(template, mov, blockedCommands);
					} else {
						retorno = executeSemTxTemplate(mov, blockedCommands);
					}

					MovimentoPerformance.getInstance().registerMovimento(mov.getCodMovimento().getId(), System.currentTimeMillis() - init);
				} else {
					throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA, "Solicitação já processada");
				}
				
				// Caso for o Logon, o log é feito dentro do processador
				if (mov.getCodMovimento().getId() != ArqListaComando.LOGON_COD) {
					LogProcessorDelegate.getInstance().writeMovimentoLog(mov);
				}
				
				mov.setComandosBloqueados(blockedCommands);
				return retorno;
			}
		} catch (RuntimeException e) {
			blockedCommands.remove(mov.getCodMovimento());
			sc.setRollbackOnly();
			e.printStackTrace();
			throw new ArqException("Erro de execução", e);
		}
	}

	private void prepare(UsuarioGeral usuarioLogado, List<Comando> comandosBloqueados, int codMovimento) throws RemoteException, ArqException {
		if ( usuarioLogado != null )
			usuarioLogado.getComandosBloqueados().remove(new Comando(codMovimento, ""));
		else if (comandosBloqueados != null)
			comandosBloqueados.remove(new Comando(codMovimento, ""));
	}

	public void ejbActivate() throws EJBException, RemoteException {

	}

	public void ejbPassivate() throws EJBException, RemoteException {

	}

	public void ejbRemove() throws EJBException, RemoteException {

	}
	
	/*
	 * 
	 */
	private Object executeSemTxTemplate(final Movimento mov, final List<Comando> blockedCommands) throws RemoteException, NegocioException, ArqException {
		try {
			ProcessadorComando processador = MovimentoLocator.getInstance().getProcessador(mov.getCodMovimento());
			return processador.execute(mov);
		} catch (NegocioException e) {
			sc.setRollbackOnly();
			blockedCommands.remove(mov.getCodMovimento());
			throw e;
		} catch (ArqException e) {
			blockedCommands.remove(mov.getCodMovimento());
			sc.setRollbackOnly();
			throw e;
		} finally {
			if (!isEmpty(mov.getClosableResources())) {
				for (ClosableResource resource : mov.getClosableResources()) {
					resource.close();
				}
			}
		}
	}
	
	/*
	 * 
	 */
	private Object executeComTxTemplate(final TransactionTemplate template, final Movimento mov, final List<Comando> blockedCommands) throws ArqException, NegocioException, RemoteException {
		final ProcessadorComando processador = MovimentoLocator.getInstance().getProcessador(mov.getCodMovimento());

		Object result = template.execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus status) {
				try {
					return processador.execute(mov);
				} catch (NegocioException e) {
					status.setRollbackOnly();
					sc.setRollbackOnly();
					blockedCommands.remove(mov.getCodMovimento());
					return e;
				} catch (ArqException e) {
					status.setRollbackOnly();
					sc.setRollbackOnly();
					blockedCommands.remove(mov.getCodMovimento());
					return e;
				} catch (RemoteException e) {
					status.setRollbackOnly();
					sc.setRollbackOnly();
					blockedCommands.remove(mov.getCodMovimento());
					return e;
				} catch(Exception e) {
					status.setRollbackOnly();
					sc.setRollbackOnly();
					blockedCommands.remove(mov.getCodMovimento());
					return e;
				} finally {
					if (!isEmpty(mov.getClosableResources())) {
						for (ClosableResource resource : mov.getClosableResources()) {
							resource.close();
						}
					}
					
					ThreadScopedResourceCache.closeResources();
				}
			}
		});
		
		if (result instanceof ArqException) {
			throw (ArqException) result;
		} else if (result instanceof NegocioException) {
			throw (NegocioException) result;
		} else if (result instanceof RemoteException) {
			throw (RemoteException) result;
		} else if (result instanceof RuntimeException) {
			throw (RuntimeException) result;
		} else {
			return result;
		}
	}
	
	/*
	 * 
	 */
	private TransactionTemplate getTransactionTemplate(Movimento mov) {
		TransactionTemplate template = null;
		ApplicationContext ac = mov.getApplicationContext();

		if (ac != null) {
			try {
				template = (TransactionTemplate) ac.getBean("transactionTemplate");
				template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
			} catch(NoSuchBeanDefinitionException e) {
				
			}
		}

		return template;
	}
	
	/*
	 * 
	 */
	private List<Comando> initBlockedCommands(Movimento mov) {
		List<Comando> blockedCommands = null;
		
		if (mov.getUsuarioLogado() != null) {
			blockedCommands = mov.getUsuarioLogado().getComandosBloqueados();
		} else {
			blockedCommands = mov.getComandosBloqueados();
		}
		
		if (blockedCommands == null) {
			blockedCommands = new ArrayList<Comando>();
		}
		
		return blockedCommands;
	}


}