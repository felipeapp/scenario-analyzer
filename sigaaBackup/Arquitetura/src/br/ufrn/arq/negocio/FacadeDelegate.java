/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 09/09/2004
 */
package br.ufrn.arq.negocio;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;

import javax.ejb.CreateException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.PrepareMov;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Delegate para a Fachada do Sistema
 *
 * @author Gleydson Lima
 *
 */
@SuppressWarnings("serial")
public class FacadeDelegate implements Serializable {

	private ProcessadorHome home;

	private Processador remote;

	public FacadeDelegate(String jndiName) {
		try {
			InitialContext ic = new InitialContext();
			home = (ProcessadorHome) ic.lookup(jndiName);
			remote = home.create();
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (CreateException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public Object execute(Movimento mov, HttpServletRequest req) throws NegocioException, ArqException {

		UsuarioGeral requisitor = (UsuarioGeral) req.getSession(true).getAttribute("usuario");
		List<Comando> comandosBloqueados = (List<Comando>) req.getSession(true).getAttribute("comandosBloqueados");
		
		mov.setUsuarioLogado(requisitor);
		mov.setComandosBloqueados(comandosBloqueados);
		mov.setSistema((Integer)req.getAttribute("sistema"));
		
		try {
			Object object = remote.execute(mov);
			req.getSession(true).setAttribute("comandosBloqueados", mov.getComandosBloqueados());
			return object;
		} catch (RemoteException e) {
			throw new ArqException(e);
		} 
	}

	/**
	 * Execute assíncrono
	 *
	 * @param mov
	 * @param usuario
	 * @param sistema
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public Object execute(Movimento mov, UsuarioGeral usuario, Integer sistema)
	throws NegocioException, ArqException  {
		mov.setUsuarioLogado( usuario );
		mov.setSistema( sistema );
		try {
			return remote.execute(mov);
		} catch (RemoteException e) {
			throw new ArqException(e);
		}
	}

	/**
	 * Usado para processo de LogOff
	 *
	 * @param mov
	 * @param session
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public Object execute(Movimento mov, HttpSession session)
			throws NegocioException, ArqException {

		UsuarioGeral requisitor = (UsuarioGeral) session.getAttribute("usuario");
		mov.setUsuarioLogado( requisitor );
		
		try {
			return remote.execute(mov);
		} catch (RemoteException e) {
			throw new ArqException(e);
		}
	}

	public void prepare(int codMovimento, HttpServletRequest req)
			throws NegocioException, ArqException {

		PrepareMov prepare = new PrepareMov();
		prepare.setId(codMovimento);
		execute(prepare, req);

	}

	/**
	 * Prepare assíncrono
	 *
	 * @param codMovimento
	 * @param usuario
	 * @param sistema
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public void prepare(int codMovimento, UsuarioGeral usuario, Integer sistema)
			throws NegocioException, ArqException{

		PrepareMov prepare = new PrepareMov();
		prepare.setId(codMovimento);
		execute(prepare, usuario, sistema);

	}


}