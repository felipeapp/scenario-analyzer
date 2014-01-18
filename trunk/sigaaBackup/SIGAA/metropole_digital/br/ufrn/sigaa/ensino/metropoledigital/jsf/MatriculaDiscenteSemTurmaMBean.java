package br.ufrn.sigaa.ensino.metropoledigital.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TurmaEntradaTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.OpcaoPoloGrupo;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;




/**
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Entidade respons�vel pelo gerenciamento da funcionalidade matricular discentes do IMD n�o enturmados.
 * 
 * 
 * @author Rafael Barros
 *
 */

@Scope("request")
@Component("matriculaDiscenteSemTurma")
public class MatriculaDiscenteSemTurmaMBean extends SigaaAbstractController {
	
	/**Cole��o de itens do combobox de polo**/
	private Collection<SelectItem> opcaoPolosCombo = new ArrayList<SelectItem>();
	
	/**Vari�vel que corresponde ao ID do polo selecionado**/
	private int idOpcaoPoloSelecionado = 0;
	
	/**Vari�vel que corresponde ao ID do polo selecionado Antigo**/
	private int idOpcaoPoloSelecionadoAntigo = 0;
	
	/**Cole��o de turmas que est�o associadas ao polo selecionado**/
	private Collection<TurmaEntradaTecnico> listaTurmas = new ArrayList<TurmaEntradaTecnico> ();
	
	/**Vari�vel que corresponde ao ID da turma selecionada**/
	private int idTurmaEntradaSelecionada = 0;
	
	/**Entidade que corresponde a turma de entrada selecionada na qual os alunos ser�o matriculados**/
	private TurmaEntradaTecnico turmaEntradaSelecionada = new TurmaEntradaTecnico();
	
	/**Entidade que corresponde a op��o polo grupo selecionada na qual os alunos ser�o listados**/
	private OpcaoPoloGrupo opcaoPoloSelecionada = new OpcaoPoloGrupo();
	
	/**Cole��o de discentes n�o enturmados que est�o associadas ao polo selecionado**/
	private Collection<DiscenteTecnico> listaDiscentes = new ArrayList<DiscenteTecnico> ();
	
	
	public MatriculaDiscenteSemTurmaMBean(){
		opcaoPolosCombo = Collections.emptyList();
		turmaEntradaSelecionada = new TurmaEntradaTecnico();
		opcaoPoloSelecionada = new OpcaoPoloGrupo();
		listaTurmas = new ArrayList<TurmaEntradaTecnico>();
		listaDiscentes = new ArrayList<DiscenteTecnico>();
	}
	
	
	/** M�todo respons�vel por retornar o atributo polosCombo, caso o atributo esteja vazio, o m�todo efetua o preenchimento da lista.
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/matricula/form.jsp</li>
	 *  <li>/sigaa.war/metropole_digital/matricula/pre_form.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return polosCombo
	 * @throws DAOException
	 */
	public Collection<SelectItem> getOpcaoPolosCombo() throws DAOException {
		if(opcaoPolosCombo.isEmpty()){
			opcaoPolosCombo = toSelectItems(getGenericDAO().findAll(OpcaoPoloGrupo.class), "id", "descricao");
		}
		return opcaoPolosCombo;
	}

	
	public void setOpcaoPolosCombo(Collection<SelectItem> opcaoPolosCombo) {
		this.opcaoPolosCombo = opcaoPolosCombo;
	}
	
	
	/** M�todo respons�vel por retornar o atributo listaTurmas, caso o atributo esteja vazio, o m�todo efetua o preenchimento da lista.
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/matricula/form.jsp</li>
	 *  <li>/sigaa.war/metropole_digital/matricula/pre_form.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return listaTurmas
	 * @throws DAOException
	 */
	public Collection<TurmaEntradaTecnico> getListaTurmas() throws DAOException {
		TurmaEntradaTecnicoDao tDao = new TurmaEntradaTecnicoDao();
		try {
			
			if(idOpcaoPoloSelecionado > 0 && idOpcaoPoloSelecionado != idOpcaoPoloSelecionadoAntigo){
				listaTurmas = (Collection<TurmaEntradaTecnico>) tDao.findTurmaByOpcaoPoloGrupo(idOpcaoPoloSelecionado);
				idOpcaoPoloSelecionadoAntigo = idOpcaoPoloSelecionado;
			}
			return listaTurmas;
			
		} finally {
			tDao.close();
		}
	}

	
	public void setListaTurmas(Collection<TurmaEntradaTecnico> listaTurmas) {
		this.listaTurmas = listaTurmas;
	}


	public int getIdOpcaoPoloSelecionado() {
		return idOpcaoPoloSelecionado;
	}


	public void setIdOpcaoPoloSelecionado(int idOpcaoPoloSelecionado) {
		this.idOpcaoPoloSelecionado = idOpcaoPoloSelecionado;
	}	
	
	
	public int getIdOpcaoPoloSelecionadoAntigo() {
		return idOpcaoPoloSelecionadoAntigo;
	}


	public void setIdOpcaoPoloSelecionadoAntigo(int idOpcaoPoloSelecionadoAntigo) {
		this.idOpcaoPoloSelecionadoAntigo = idOpcaoPoloSelecionadoAntigo;
	}
	

	public TurmaEntradaTecnico getTurmaEntradaSelecionada() {
		return turmaEntradaSelecionada;
	}


	public void setTurmaEntradaSelecionada(
			TurmaEntradaTecnico turmaEntradaSelecionada) {
		this.turmaEntradaSelecionada = turmaEntradaSelecionada;
	}


	public int getIdTurmaEntradaSelecionada() {
		return idTurmaEntradaSelecionada;
	}


	public void setIdTurmaEntradaSelecionada(int idTurmaEntradaSelecionada) {
		this.idTurmaEntradaSelecionada = idTurmaEntradaSelecionada;
	}


	public OpcaoPoloGrupo getOpcaoPoloSelecionada() {
		return opcaoPoloSelecionada;
	}


	public void setOpcaoPoloSelecionada(OpcaoPoloGrupo opcaoPoloSelecionada) {
		this.opcaoPoloSelecionada = opcaoPoloSelecionada;
	}


	public Collection<DiscenteTecnico> getListaDiscentes() {
		return listaDiscentes;
	}


	public void setListaDiscentes(Collection<DiscenteTecnico> listaDiscentes) {
		this.listaDiscentes = listaDiscentes;
	}


	/** M�todo respons�vel por preencher a lista de turmas quando a op��o polo grupo for alterada.
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/matricula/form.jsp</li>
	 *  <li>/sigaa.war/metropole_digital/matricula/pre_form.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return 
	 * @throws DAOException
	 */
	public void preencherTurmas() throws DAOException{
		TurmaEntradaTecnicoDao tDao = new TurmaEntradaTecnicoDao();
		try {
			this.listaTurmas = Collections.emptyList();
			if(idOpcaoPoloSelecionado > 0 && listaTurmas.isEmpty()){
				listaTurmas = (Collection<TurmaEntradaTecnico>) tDao.findTurmaByOpcaoPoloGrupo(idOpcaoPoloSelecionado);
			}
		} finally {
			tDao.close();
		}
	}
	
	
	/** M�todo respons�vel por armazenar a turma e a op��o de polo selecionadas no pre_form.jsp.
	 * 
	 * M�todo � chamado nas seguintes JSP's:
	 * <ul>
	 *  <li>/sigaa.war/metropole_digital/matricula/pre_form.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return JSP na qual a p�gina ser� redirecionada
	 * @throws DAOException
	 */
	public String salvarTurmaOpcaoPolo() throws DAOException{
		TurmaEntradaTecnicoDao turmaDao = new TurmaEntradaTecnicoDao();
		try {
			if(getParameter("idTurma").length() > 0){
				idTurmaEntradaSelecionada = getParameterInt("idTurma");
			}
			
			if(idOpcaoPoloSelecionado > 0 && idTurmaEntradaSelecionada > 0){
				turmaEntradaSelecionada = getGenericDAO().findByPrimaryKey(idTurmaEntradaSelecionada, TurmaEntradaTecnico.class);
				opcaoPoloSelecionada = getGenericDAO().findByPrimaryKey(idOpcaoPoloSelecionado, OpcaoPoloGrupo.class);
				
				listaDiscentes = turmaDao.findDiscentesSemTurmaByOpcaoPoloGrupo(idOpcaoPoloSelecionado);
				
				return forward("/metropole_digital/matricula/form.jsp");
			} else {
				addMessage("Selecione a turma para efetuar a matr�cula.",TipoMensagemUFRN.INFORMATION);
				return null;
			}
		} finally {
			turmaDao.close();
			getGenericDAO().close();
		}
	}
	
	
}
