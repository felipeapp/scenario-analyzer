/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 16/04/2010
 */
package br.ufrn.comum.jsf;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.web.jsf.AbstractController;
import br.ufrn.comum.dao.PessoaDAO;
import br.ufrn.comum.dominio.PessoaGeral;

/**
 * @author David Pereira
 *
 */
@Component
public class PessoaAutoCompleteMBean extends AbstractController {

	/**
	 * Método que responde às requisições de autocomplete com o componente
	 * rich:suggestionBox do RichFaces retornando uma lista de pessoas jurídicas
	 *
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public List<PessoaGeral> autocompleteNomePessoaJuridica(Object event) throws DAOException {
		String nome = event.toString();
		return getDAO(PessoaDAO.class).findByRazaoSocial(nome, 'J');
	}

	/**
	 * Método que responde às requisições de autocomplete com o componente
	 * rich:suggestionBox do RichFaces retornando uma lista de pessoas físicas
	 *
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public List<PessoaGeral> autocompleteNomePessoaFisica(Object event) throws DAOException {
		String nome = event.toString();
		return getDAO(PessoaDAO.class).findByRazaoSocial(nome, 'F');
	}

	/**
	 * Método que responde às requisições de autocomplete com o componente rich:suggestionBox
	 * do RichFaces retornando uma lista de pessoas físicas que são servidores da instituição.
	 *
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public List<PessoaGeral> autocompleteNomePessoaFisicaServidor(Object event) throws DAOException {
		String nome = event.toString();
		return getDAO(PessoaDAO.class).findByRazaoSocial(nome, 'F');
	}

	/**
	 * Método que responde às requisições de autocomplete com o componente
	 * rich:suggestionBox do RichFaces retornando uma lista de pessoas físicas e jurídicas
	 * MÉTODO INVOCADO PELA JSPS mapeadas pelas contantes:
	 * ParticipanteCompraExternoMBean.FORM_PARTICIPANTE_COMPRA_EXTERNO
	 * RelatorioParticipanteCompraExternoMBean.FORM_RELATORIO_PARTICIPANTE_EXTERNO
	 *
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public List<PessoaGeral> autocompleteNomePessoa(Object event) throws DAOException {
		String nome = event.toString(); //Nome do item digitado no autocomplete
		Long cpfCnpj = StringUtils.extractLong(nome);
		if (cpfCnpj != null){
			nome = null; //consulta pelo cpf/cnpj apenas
		}

		List<PessoaGeral> lista = new ArrayList<PessoaGeral>();
		GenericDAO genDAO = getGenericDAO();
		if (nome == null){
			//Consulta pelo código
			PessoaGeral item = genDAO.findByExactField(PessoaGeral.class, "cpf_cnpj", cpfCnpj, true);
			if (item != null)
				lista.add(item);
		}else{
			lista = getDAO(PessoaDAO.class).findPessoaByNome(nome);
		}

		return lista;
	}

	/**
	 * Método que responde às requisições de autocomplete com o componente rich:suggestionBox do
	 * RichFaces retornando uma lista de pessoas físicas que também são servidores da instituição.
	 *
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public List<PessoaGeral> autocompleteNomePessoaServidor(Object event) throws DAOException {
		String nome = event.toString(); //Nome do item digitado no autocomplete
		Long cpfCnpj = StringUtils.extractLong(nome);
		if (cpfCnpj != null){
			nome = null; //consulta pelo cpf/cnpj apenas
		}

		List<PessoaGeral> lista = getDAO(PessoaDAO.class).findPessoaServidorByNomeOrCpf(nome, cpfCnpj);

		return lista;
	}

}