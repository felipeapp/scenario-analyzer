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
 * MBean que gerencia os Conv�nios da biblioteca.
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
	 * Indica qual p�gina deve ser exibida ap�s o cadastro. No caso, exibe a listagem.
	 * M�todo chamado pelo m�todo cadastrar, da arquitetura.
	 * 
	 */
	@Override
	public String forwardCadastrar(){
		return getListPage();
	}

	/**
	 * Prepara o movimento antes de remover.
	 * M�todo chamado pelo m�todo remover, da arquitetura.
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
	 * Retorna todos os conv�nios.
	 * Usado na p�gina /biblioteca/Convenio/lista.jsp
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
	 * Volta para a p�gina que lista os convenios.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/Convenio/form.jsp
	 *
	 * @return
	 */
	public String voltar(){
		return forward(getListPage());
	}
	
	
	/**
	 * Retorna a quantidade de conv�ncios
	 * Usado na p�gina /biblioteca/Convenio/lista.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public int getSize () throws ArqException{
		return getAll().size();
	}
	
}