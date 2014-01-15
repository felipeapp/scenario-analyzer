package br.ufrn.sigaa.monitoria.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.CalendarioMonitoria;


/**
 * Processador responsável por ações relacionadas ao calendário de monitoria.
 * @author ilueny santos
 *
 */
public class ProcessadorCalendarioMonitoria extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);		
		if( ((MovimentoCadastro)mov).getCodMovimento().equals(SigaaListaComando.CADASTRAR_CALENDARIO_MONITORIA)){
				criarNovoCalendarioMonitoria((MovimentoCadastro)mov);
		}
		return null;
	}

	
	
	
	/**
	 * Criar ou atualizar Calendário de Monitoria
	 * 
	 * @param mov
	 * @return
	 * @throws ArqException
	 */
	private void criarNovoCalendarioMonitoria(MovimentoCadastro mov) throws ArqException{
		
		GenericDAO dao = getGenericDAO(mov);

		try {
			
			CalendarioMonitoria novo = (CalendarioMonitoria) mov.getObjMovimentado();			
			dao.detach(novo);	
			Collection<CalendarioMonitoria>ativos = dao.findAllAtivos(CalendarioMonitoria.class, "id" );
			if ((ativos != null) && (! ativos.isEmpty())){
				CalendarioMonitoria antigo = ativos.iterator().next(); //pegando o primeiro e único ativo
				antigo.setAtivo(false);
				dao.update(antigo);
			}

			
			//criando novo calendário
			novo.setId(0);
			novo.setAtivo(true);
			novo.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
			dao.create(novo);
				
		}catch (DAOException e) {
			e.printStackTrace();
			throw new DAOException(e);					
		}finally {
			dao.close();
		}
		
	}
							
	public void validate(Movimento mov) throws NegocioException, ArqException {
	}
	
}