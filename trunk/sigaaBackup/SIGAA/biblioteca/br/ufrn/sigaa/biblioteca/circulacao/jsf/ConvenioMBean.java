package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Convenio;

/**
 * MBean que gerencia os Convênios da biblioteca.
 * @see Convenio
 * @author Fred_Castro
 */
@Component("convenioBiblioteca")
@Scope("request")
public class ConvenioMBean extends SigaaAbstractController <Convenio>{

	
	public ConvenioMBean(){
		obj = new Convenio();
	}
	
	@Override
	public String getLabelCombo(){
		return "nome";
	}

	/**
	 * Indica qual página deve ser exibida após o cadastro. No caso, exibe a listagem.
	 * Método chamado pelo método cadastrar, da arquitetura.
	 * 
	 */
	@Override
	public String forwardCadastrar(){
		return getListPage();
	}

	/**
	 * Prepara o movimento antes de remover.
	 * Método chamado pelo método remover, da arquitetura.
	 */
	@Override
	public void beforeRemover() throws DAOException{
		setId();
		try {
			prepareMovimento(ArqListaComando.REMOVER);
		} catch (ArqException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retorna todos os convênios.
	 * Usado na página /biblioteca/Convenio/lista.jsp
	 */
	@Override
	public Collection <Convenio> getAll() throws ArqException {
		GenericDAO dao = null;
		try {
			dao = getGenericDAO();
			return dao.findAll(Convenio.class, "nome", "asc");
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * 
	 * Volta para a página que lista os convenios.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/Convenio/form.jsp
	 *
	 * @return
	 */
	public String voltar(){
		return forward(getListPage());
	}
	
	
	/**
	 * Retorna a quantidade de convêncios
	 * Usado na página /biblioteca/Convenio/lista.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public int getSize () throws ArqException{
		return getAll().size();
	}
	
}