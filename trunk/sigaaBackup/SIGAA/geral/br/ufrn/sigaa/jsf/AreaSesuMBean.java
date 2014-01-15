/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '24/10/2006'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.AreaSesu;

/**
 * Managed bean respons�vel pelo cadastro, atualiza��o, das Areas SESU (Secretaria de Ensino Superior).
 * 
 * @author 
 */
public class AreaSesuMBean extends SigaaAbstractController<AreaSesu> {
	
	public AreaSesuMBean() {
		clear();	
	}

	/** Serve para inicializar o Managed Bean */
	public void clear() {
		obj = new AreaSesu();
	}
	
	@Override
	public Collection<SelectItem> getAllCombo() throws DAOException {
		Collection<AreaSesu> areas = getGenericDAO().findAll(AreaSesu.class, "codigo","asc");
		return toSelectItems(areas, "id", "descricao");
	}
	
	/**
	 * Verifica se o objeto j� foi removido, para evitar o nullPointer
	 * 
	 * JSP: /sigaa.war/administracao/cadastro/AreaSesu/form.jsp
	 */
	@Override
	public String remover() throws ArqException {

		Integer id = getParameterInt("id", 0);
		GenericDAO dao = getDAO(GenericDAOImpl.class);		
		obj = dao.findByPrimaryKey(id, AreaSesu.class);

		if (obj == null) {
			clear();
		}
		prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}
	
	/**
	 * Para gerar uma listagem de forma ordenada, tomando como base a sigla informada.
	 * 
	 * JSP: /sigaa.war/administracao/cadastro/AreaSesu/lista.jsp
	 * @return
	 * @throws DAOException
	 */
	public Collection<AreaSesu> getAllOrdenado() throws DAOException {
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		return dao.findAll(AreaSesu.class, "codigo", "asc");
	}

	/**
	 * Respons�vel pela pagina��o do resultado gerado pela consulta, 
	 * s�o exibidos 20 registros em cada p�gina ao usu�rio.
	 * 
	 * /sigaa.war/administracao/cadastro/AreaSesu/lista.jsp
	 */
	@Override
	public Collection<AreaSesu> getAllPaginado() throws ArqException {
		setTamanhoPagina(20);
		return super.getAllPaginado();
	}
	
	/**
	 * Serve para informar qual o atributo deve ser realizada a ordena��o na realiza��o da busca.
	 * 
	 * JSP: N�o invocado.
	 */
	@Override
	public String getAtributoOrdenacao() {
		return "codigo";
	}
	
	/**
	 * Serve para verificar se �rea SESU informada j� n�o existe uma igual cadastrada.
	 * 
	 * JSP: /sigaa.war/administracao/cadastro/AreaSesu/form.jsp
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		Collection<AreaSesu> mesmaArea = dao.findByExactField(AreaSesu.class, "codigo", obj.getCodigo());
		for (AreaSesu as : mesmaArea) {
			if (as.getId() == obj.getId()) {
				return super.cadastrar();
			} if((as.getNome().equals(obj.getNome())) && (as.getCodigo().equals(obj.getCodigo()))){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Area SESU");
				return null;
			}
		}
		return super.cadastrar();
	} 														
}