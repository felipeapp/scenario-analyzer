/**
 * 
 */
package br.ufrn.comum.jsf;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dao.SistemaDao;
import br.ufrn.comum.dominio.Sistema;

/**
 * @author Ricardo Wendell
 *
 */
@Component("sistemaBean") @Scope("request")
public class SistemaMBean extends ComumAbstractController<Sistema> {

	public Collection<SelectItem> getAllCombo() {
		return toSelectItems(getAll(), "id", "nome");
	}

	public Collection<Sistema> getAll() {
		return getDAO(SistemaDao.class).findSistemasAtivos();
	}
	
	public Collection<SelectItem> getAllControleUsuariosCombo() {
		return toSelectItems(getDAO(SistemaDao.class).findSistemasControleUsuarios(), "id", "nome");
	}
	
	/**
	 * Método que verifica se um determinado sistema está ativo e acessível.
	 * 
	 * Método não invocado por JSPs.
	 * 
	 * @author Weinberg Souza 
	 * @param idSistema
	 * @return
	 * @throws DAOException 
	 */
	public boolean verificaAtivoAcessivel(int idSistema, String urlSistema) {
		boolean ativoAcessivel = false;
		
		HttpURLConnection con = null;
		
		try {
			
			// Verificando se o sistema está ativo.
			if(new SistemaDao().isSistemaAtivo(idSistema)) {

				// Verificando se o sistema responde as requisições.
				con = (HttpURLConnection) new URL(urlSistema).openConnection();
				con.connect();
				
				if(con.getResponseCode() == HttpURLConnection.HTTP_OK) {
					ativoAcessivel = true;
				}
				
			}
			
		} catch(Exception e) { 
			ativoAcessivel = false;
		} finally {
			if(con != null) {
				con.disconnect();
			}
		}
		
		return ativoAcessivel;
	}

	public boolean isAdministrativoAtivo() {
		return Sistema.isAdministrativoAtivo();
	}
	
	public boolean isAcademicoAtivo() {
		return Sistema.isSigaaAtivo();
	}
	
	/**
	 * Identifica se o IProject está ativo ou não
	 * @return
	 * @throws DAOException
	 */
	public boolean isIProjectAtivo() {
		return Sistema.isSistemaAtivo(Sistema.IPROJECT);
	}
	
}
