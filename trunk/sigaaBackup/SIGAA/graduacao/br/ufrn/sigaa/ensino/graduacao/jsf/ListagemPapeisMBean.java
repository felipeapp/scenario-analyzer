/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/09/11 - 20:39:11
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.util.List;

import javax.faces.model.SelectItem;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Managed bean para listar os usuários que possuem um determinado
 * papel.
 * 
 * @author David Pereira
 *
 */
public class ListagemPapeisMBean extends SigaaAbstractController {

	private int papel;
	
	private boolean buscou;
	
	private List<Usuario> usuarios;
	
	public String listar() throws DAOException {
		UsuarioDao dao = getDAO(UsuarioDao.class);
		usuarios = dao.findUsuariosByPapel(new Papel(papel));
		buscou = true;
		
		return null;
	}
	
	public List<Usuario> getUsuarios() {
		return usuarios;
	}
	
	public List<SelectItem> getPapeis() throws DAOException {
		GenericDAO dao = getGenericDAO();
		return toSelectItems(dao.findByExactField(Papel.class, "subSistema.id", SigaaSubsistemas.GRADUACAO.getId()), "id", "descricao");
	}

	/**
	 * @return the papel
	 */
	public int getPapel() {
		return papel;
	}

	/**
	 * @param papel the papel to set
	 */
	public void setPapel(int papel) {
		this.papel = papel;
	}

	/**
	 * @return the buscou
	 */
	public boolean isBuscou() {
		return buscou;
	}

	/**
	 * @param buscou the buscou to set
	 */
	public void setBuscou(boolean buscou) {
		this.buscou = buscou;
	}
}
