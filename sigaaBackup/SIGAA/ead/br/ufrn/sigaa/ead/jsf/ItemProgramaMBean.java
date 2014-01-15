package br.ufrn.sigaa.ead.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dominio.ItemPrograma;
import br.ufrn.sigaa.ead.negocio.MovimentoItemPrograma;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;

/** Controller respons�vel pelo cadastro de ItemPrograma.
 * 
 * @author �dipo Elder F. Melo
 *
 */
@Component("itemProgramaMBean") 
@Scope("session")
public class ItemProgramaMBean extends SigaaAbstractController<ItemPrograma> {

	/** ItemPrograma a cadastrar.*/
	private ItemPrograma itemPrograma;
	/** Lista de componente curriculares para cadastrar os �tens de programas. */
	private List<ComponenteCurricular> listaDisciplinas = new ArrayList<ComponenteCurricular>();
	/** Curso a cadastrar o item de programa. */
	private Integer idCurso;
	/** Componente curricular a cadastrar o item de programa. */
	private ComponenteCurricular componenteCurricular = new ComponenteCurricular();
	/** Lista de itens cadastrados para um componente curricular. */
	private List<ItemPrograma> listagemItensProgramaPorDisciplina = new ArrayList<ItemPrograma>();
	/** Data Model com os itens de programas cadastrados.*/
	private DataModel dataModel;
	/** ID do componente curricular a cadastrar.*/
	private Integer idComponenteCurricular;

	/** Retorna a lista de itens cadastrados para um componente curricular. 
	 * @return Lista de �tens cadastrados para um componente curricular. 
	 */
	public List<ItemPrograma> getListagemItensProgramaPorDisciplina() {
		return listagemItensProgramaPorDisciplina;
	}

	/** Seta a lista de itens cadastrados para um componente curricular. 
	 * @param listagemItensProgramaPorDisciplina Lista de �tens cadastrados para um componente curricular. 
	 */
	public void setListagemItensProgramaPorDisciplina(
			List<ItemPrograma> listagemItensProgramaPorDisciplina) {
		this.listagemItensProgramaPorDisciplina = listagemItensProgramaPorDisciplina;
	}

	/** Construtor padr�o. */
	public ItemProgramaMBean() {
		itemPrograma = new ItemPrograma();
		obj = new ItemPrograma();
	}
	
	/** Retorna o ItemPrograma a cadastrar.
	 * @return ItemPrograma a cadastrar.
	 */
	public ItemPrograma getItemPrograma() {
		return itemPrograma;
	}

	/** Seta o ItemPrograma a cadastrar.
	 * @param itemPrograma ItemPrograma a cadastrar.
	 */
	public void setItemPrograma(ItemPrograma itemPrograma) {
		this.itemPrograma = itemPrograma;
	}
	
	/** Retorna a lista de componente curriculares para cadastrar os itens de programas.
	 * M�todo n�o invocado por JSP.  
	 * @return Lista de componente curriculares para cadastrar os �tens de programas. 
	 * @throws DAOException
	 */
	public List<Curso> listagemCursosDistancia() throws DAOException {
		CursoDao cursoDao = getDAO(CursoDao.class);
		List<Curso> listaCursosDistancia = cursoDao.findAllCursosADistancia();		
		
		if (idCurso == null)
			listaDisciplinas = listagemDisciplinasPorCursoDistancia(listaCursosDistancia.get(0).getId());
		else
			listaDisciplinas = listagemDisciplinasPorCursoDistancia(idCurso);
		
		return listaCursosDistancia;
	}
	
	/** Retorna uma lista de componente curriculares para cadastrar os itens de programas por curso.
	 * M�todo n�o invocado por JSP. 
	 * @param idCurso
	 * @return Lista de componente curriculares para cadastrar os �tens de programas. 
	 * @throws DAOException
	 */
	public List<ComponenteCurricular> listagemDisciplinasPorCursoDistancia(int idCurso) throws DAOException {
		CursoDao cursoDao = getDAO(CursoDao.class);
		listaDisciplinas = cursoDao.findDisciplinasCursoDistanciaPorCurso(idCurso);
		return listaDisciplinas;
	}
	
	/** Retorna uma lista de todos cursos a dist�ncia.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllCursosDistancia() throws DAOException {
		return toSelectItems(listagemCursosDistancia(), "id", "nome");
	}
	
	/** Retorna a lista de componente curriculares para cadastrar os itens de programas.  
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllDisciplinasPorCurso() throws DAOException {

		if (idCurso != null) {
			listaDisciplinas = listagemDisciplinasPorCursoDistancia(idCurso); 
		}
		
		return toSelectItems(listaDisciplinas, "id", "nome");
	}
	
	/** Atualiza a Lista de componente curriculares para cadastrar os itens de programas de acordo com o curso selecionado.
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/ead/ItemPrograma/form.jsp
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/ead/ItemPrograma/lista.jsp  
	 * @param e
	 * @throws ArqException
	 */
	public void changeCursoDistancia(ValueChangeEvent e) throws ArqException {
		idCurso = Integer.valueOf( e.getNewValue().toString() );	
		listaDisciplinas = listagemDisciplinasPorCursoDistancia(idCurso);
		this.idComponenteCurricular = 0;
	}
	
	/** Muda a lista de Lista de itens cadastrados para um componente curricular de acordo com o componente selecionado.
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/ead/ItemPrograma/form.jsp
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/ead/ItemPrograma/lista.jsp 
	 * @param e
	 * @throws ArqException
	 */
	public void changeDisciplinaPorCurso(ValueChangeEvent e) throws ArqException {
		idComponenteCurricular = Integer.valueOf( e.getNewValue().toString() );
		
		buscarComponenteCurricular();
		buscarItensProgramaPorDisciplina(idComponenteCurricular);
	}
	
	/** Busca os componente curriculares.
	 * M�todo n�o invocado por JSP.
	 * @throws DAOException
	 */
	public void buscarComponenteCurricular() throws DAOException {
		ComponenteCurricularDao componenteCurricularDao = getDAO(ComponenteCurricularDao.class);
		if (idComponenteCurricular != null) {
			componenteCurricular = (ComponenteCurricular) componenteCurricularDao.findByPrimaryKeyLock(idComponenteCurricular, ComponenteCurricular.class);
			buscarItensProgramaPorDisciplina(idComponenteCurricular);
			dataModel = new ListDataModel(listagemItensProgramaPorDisciplina);
		}
		else {
			addMensagemErro("Voc� precisa selecionar uma disciplina!");
		}

	}
	
	
	/** Busca os �tens de programa por disciplina. 
	 * M�todo n�o invocado por JSP
	 * @param idComponenteCurricular
	 * @throws DAOException
	 */
	public void buscarItensProgramaPorDisciplina(int idComponenteCurricular) throws DAOException {
		CursoDao cursoDao = getDAO(CursoDao.class);
		listagemItensProgramaPorDisciplina = cursoDao.findListaItemProgramaByDisciplina(idComponenteCurricular);
	}
	
	/** Cancela a opera��o corrente.
	 * Chamado por /ead/ItemPrograma/form.jsp
	 * @see br.ufrn.arq.web.jsf.AbstractController#cancelar()
	 */
	public String cancelar() {
		return forward("/ead/menu.jsf");
	}
	
	/** Inicia a opera��o de cadastro.
	 * Chamado por /ead/menu.jsp
	 * @return
	 */
	public String cadastro() {
		this.dataModel = null;
		this.listaDisciplinas = new ArrayList<ComponenteCurricular>();
		this.idCurso = 0;
		return forward("/ead/ItemPrograma/form.jsf");
	}
	
	/** Inicia a opera��o de consulta.
	 * Chamado por /ead/menu.jsp
	 * @return
	 */
	public String consultar() {
		this.idCurso = 0;
		this.idComponenteCurricular = 0;
		this.dataModel = null;
		return forward("/ead/ItemPrograma/lista.jsf");
	}

	/** Cadastrar o item de programa.
     * Chamado por /ead/ItemPrograma/form.jsp
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	public String cadastrar() throws NegocioException, ArqException {
	
			componenteCurricular.setItemPrograma(new ArrayList<ItemPrograma>() );			
			componenteCurricular.getItemPrograma().add( itemPrograma );
			itemPrograma.setDisciplina(componenteCurricular);
			
			if ( ValidatorUtil.isEmpty( itemPrograma.getConteudo()) ) {
				addMensagemErro("Conte�do � obrigat�rio!");
			}
			if (itemPrograma.getAula() == null) {
				addMensagemErro("Aula � obrigat�rio!");
			}
			if ( listagemItensProgramaPorDisciplina != null ){
				for ( ItemPrograma item : listagemItensProgramaPorDisciplina )
					if (item.getAula() == itemPrograma.getAula()){
						addMensagemErro("J� foi cadastrado um item com este n�mero da aula, por favor modifique o n�mero da aula do item.");
						break;
					}
			}
			
			if (hasErrors()) {
				return null;
			} else {
				
				prepareMovimento( SigaaListaComando.CADASTRAR_ITEM_PROGRAMA );
				
				MovimentoItemPrograma mov = new MovimentoItemPrograma();
				mov.setCodMovimento(SigaaListaComando.CADASTRAR_ITEM_PROGRAMA);
				
				mov.setItemPrograma(itemPrograma);
				
				executeWithoutClosingSession(mov, getCurrentRequest());

				addMessage("Item do Programa cadastrado com sucesso!", TipoMensagemUFRN.INFORMATION);
				
				int numProximaAula = itemPrograma.getAula() >= 999 ? 999 : itemPrograma.getAula() + 1;
				itemPrograma = new ItemPrograma();	
				itemPrograma.setAula(numProximaAula);
				
				buscarComponenteCurricular();
			}
			
			return null;
	}
	
	/** Remove o item de programa.
     * Chamado por /ead/ItemPrograma/lista.jsp
     * Chamado por /ead/ItemPrograma/form.jsp
	 * @return
	 * @throws ArqException
	 */
	public String removerItemPrograma() throws ArqException {

			prepareMovimento(ArqListaComando.REMOVER);
		
			itemPrograma = (ItemPrograma) dataModel.getRowData();
			obj = itemPrograma;
			
			MovimentoCadastro mov = new MovimentoCadastro();
			PersistDB obj2 = obj;
			mov.setObjMovimentado(obj2);

			if (obj2.getId() == 0) {
				addMensagemErro("N�o h� objeto informado para remo��o");
				return null;
			} else {

				mov.setCodMovimento(ArqListaComando.REMOVER);
				try {
					executeWithoutClosingSession(mov,
							(HttpServletRequest) FacesContext.getCurrentInstance()
									.getExternalContext().getRequest());
					addMessage("Opera��o realizada com sucesso!",
							TipoMensagemUFRN.INFORMATION);
				} catch (NegocioException e) {
					addMensagemErro(e.getMessage());
					return forward(getFormPage());
				} catch (Exception e) {
					addMensagemErroPadrao();
					e.printStackTrace();
					return forward(getFormPage());
				}
				
				itemPrograma = new ItemPrograma();
			}
			
			buscarComponenteCurricular();
			
			return null;
	}

	/** Retorna o Data Model com os itens de programas cadastrados.
	 * @return Data Model com os �tens de programas cadastrados.
	 */
	public DataModel getDataModel() {
		return dataModel;
	}

	/** Seta o Data Model com os itens de programas cadastrados.
	 * @param dataModel Data Model com os �tens de programas cadastrados.
	 */
	public void setDataModel(DataModel dataModel) {
		this.dataModel = dataModel;
	}

	/** Retorna o ID do componente curricular a cadastrar.
	 * @return ID do componente curricular a cadastrar.
	 */
	public Integer getIdComponenteCurricular() {
		return idComponenteCurricular;
	}

	/** Seta o ID do componente curricular a cadastrar.
	 * @param idComponenteCurricular ID do componente curricular a cadastrar.
	 */
	public void setIdComponenteCurricular(Integer idComponenteCurricular) {
		this.idComponenteCurricular = idComponenteCurricular;
	}	

}
