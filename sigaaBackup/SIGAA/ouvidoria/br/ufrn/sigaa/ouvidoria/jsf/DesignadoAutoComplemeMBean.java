package br.ufrn.sigaa.ouvidoria.jsf;

import java.util.Collection;

import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.web.jsf.AbstractController;
import br.ufrn.comum.dominio.Unidade;
import br.ufrn.sigaa.ouvidoria.dao.DelegacaoUsuarioRespostaDao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;


/**
 * Managed Bean para autocompletar as pessoas que podem ser designadas a responder uma manifestação pela unidade.
 * 
 * @author Bernardo
 *
 */
@Component
public class DesignadoAutoComplemeMBean extends AbstractController {

	/**
	 * Método responsável para trazer opções de nomes para o autocomplete de nomes para encaminar uma manifestação.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * ouvidoria/Manifestacao/responsavel/detalhes_manifestacao.jsp
	 * 
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public Collection<Pessoa> autocompleteDesignado(Object event) throws DAOException {
		Boolean apenasAtivos = false;
		
		if (getParameterBoolean("apenasAtivos")){
			apenasAtivos = getParameterBoolean("apenasAtivos");
		}
		
		Integer idUnidade = getParameterInt("idUnidade");
		
		Unidade unidade = idUnidade != null ? new Unidade(idUnidade) : new Unidade();
		
		String nome = event.toString(); //Nome do usuário digitado no autocomplete
		
		return getDAO(DelegacaoUsuarioRespostaDao.class).findByNome(nome, unidade, apenasAtivos);
	}
		
}
