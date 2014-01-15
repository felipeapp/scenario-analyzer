/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Data de Cria��o: 30/09/2009
 */
package br.ufrn.comum.jsf;

import java.util.List;

import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.web.jsf.AbstractController;
import br.ufrn.comum.dao.UsuarioDAO;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Managed Bean para possibilitar a cria��o de autocompletes
 * de usu�rios.
 * 
 * @author David Pereira
 *
 */
@Component
public class UsuarioAutoCompleteMBean extends AbstractController {

	public List<UsuarioGeral> autocompleteNomeUsuario(Object event) throws DAOException {
		String nome = event.toString(); //Nome do usu�rio digitado no autocomplete
		return getDAO(UsuarioDAO.class, Sistema.COMUM).findByNomeLoginOuCpf(nome);
	}
	
}
