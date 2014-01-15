/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
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
 * Managed bean responsável pelo cadastro, atualização, das Areas SESU (Secretaria de Ensino Superior).
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
	 * Verifica se o objeto já foi removido, para evitar o nullPointer
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
	 * Responsável pela paginação do resultado gerado pela consulta, 
	 * são exibidos 20 registros em cada página ao usuário.
	 * 
	 * /sigaa.war/administracao/cadastro/AreaSesu/lista.jsp
	 */
	@Override
	public Collection<AreaSesu> getAllPaginado() throws ArqException {
		setTamanhoPagina(20);
		return super.getAllPaginado();
	}
	
	/**
	 * Serve para informar qual o atributo deve ser realizada a ordenação na realização da busca.
	 * 
	 * JSP: Não invocado.
	 */
	@Override
	public String getAtributoOrdenacao() {
		return "codigo";
	}
	
	/**
	 * Serve para verificar se Área SESU informada já não existe uma igual cadastrada.
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