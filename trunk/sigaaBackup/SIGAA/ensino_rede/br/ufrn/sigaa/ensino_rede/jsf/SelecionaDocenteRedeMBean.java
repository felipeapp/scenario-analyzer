/*
* Universidade Federal do Rio Grande do Norte
* Superintend�ncia de Inform�tica
* Diretoria de Sistemas
*
 * Created on 19/08/2013
*/
package br.ufrn.sigaa.ensino_rede.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;

import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.ensino_rede.dao.DocenteRedeDao;
import br.ufrn.sigaa.ensino_rede.dominio.DocenteRede;

/**
 * Managed bean respons�vel pelas sele��o de docentes de ensino em rede.
 *
 * @author Henrique Andr�
 *
 */
@SuppressWarnings("serial")
@Component("selecionaDocenteRedeMBean") @Scope("session")
public class SelecionaDocenteRedeMBean extends EnsinoRedeAbstractController<DocenteRede> {

	/** Atalho para a view de busca e listagem docente. */
	private static final String LISTA_DOCENTES = "/ensino_rede/docente_rede/lista.jsp";

	
	/** Par�metros utilizados na busca dos docentes */
	private ParametrosSelecionaDocente parametros;
	/** Valores dos par�metros utilizados na busca dos docentes */
	private ValoresSelecionaDocente valores;
	/** MBean que invoca os recursos  */
	private SelecionaDocente requisitor;
	/** Campus padr�o da sele��o */
	private CampusIes campus;
	/** Se � pra exibir o combo de sele��o de campus, n�o exibivel para coordenador da unidade */
	private boolean exibirComboCampus = true;
	/** Se � pra exibir a lupa */
	private boolean consultar = false;
	
	/** Tipo de busca e de formato de p�gina */
	public enum ModoBusca {
		/**tipo busca simples.*/
		SIMPLES,
		/**tipo busca detalhada.*/
		DETALHADA
	}
	/** Modo de busca padr�o */
	private ModoBusca modoBusca = ModoBusca.SIMPLES;

	/** Contrutor padr�o */
	public SelecionaDocenteRedeMBean() {
		clear();
	}
	
	/**
	 * Limpa os dados de sess�o.
	 * @return 
	 */
	private void clear() {
		obj = new DocenteRede();
		parametros = new ParametrosSelecionaDocente();
		valores = new ValoresSelecionaDocente();
		setResultadosBusca(new ArrayList<DocenteRede>());
	}

	/**
	 * Redireciona para tela de busca de docentes.
	 * M�todo n�o invocado por JSPs
	 * @return 
	 * @throws DAOException 
	 * @throws NegocioException 
	 */
	public String executar() throws DAOException, NegocioException {
		clear();
		
		if (modoBusca == ModoBusca.SIMPLES) {
			if (isEmpty(campus))
				throw new NegocioException("Campus n�o informado.");
			
			parametros.setCheckCampus(true);
			valores.setValorIdCampus(campus.getId());
			
			return buscarDocentes();
		} else
			return forward(LISTA_DOCENTES);
	}

	/**
	 * Busca os docentes de acordo com os par�metros informados
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/docente/lista.jsp</li>
	 * </ul>
	 * @return 
	 * @throws DAOException 
	 */
	public String buscarDocentes() throws DAOException {
	
		if (!exibirComboCampus) {
			parametros.setCheckCampus(true);
			valores.setValorIdCampus(campus.getId());
		}
		
		if (parametros.isCheckCpf() && isEmpty(valores.getValorCpf()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "CPF");	
		
		if (parametros.isCheckNome() && isEmpty(valores.getValorNome()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome");
		
		if (!parametros.isCheckCampus() && !parametros.isCheckCpf() && !parametros.isCheckNome())
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
		
		if (hasErrors())
			return null;		
		
		DocenteRedeDao dao = getDAO(DocenteRedeDao.class);
		ArrayList<DocenteRede> docentes = dao.findDocentesByParametros(parametros, valores);

		setResultadosBusca(docentes);

		if (isEmpty(getResultadosBusca())) {
			addMensagemErro("Nenhum docente foi encontrado.");
			return null;
		}

		return forward(LISTA_DOCENTES);
	}
	
	/**
	 * Escolhe um docente da listagem para enviar ao requesitor
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/docente/lista.jsp</li>
	 * </ul>
	 * @return 
	 * @throws ArqException 
	 */
	public void escolheDocente(ActionEvent evt) throws ArqException {

		Integer id = (Integer) evt.getComponent().getAttributes().get("idDocente");
		escolheDocente(id);

	}

	/**
	 * Redireciona para a listagem de docentes.
	 * M�todo Chamado pelas seguintes JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/docente_turma/view.jsp</li>
	 * </ul>
	 */
	public String redirectTelaLista () {
		return forward(LISTA_DOCENTES);
	}
	
	/**
	 * Escolhe um docente da listagem para enviar ao requesitor
	 * M�todo n�o invocado por JSPs:
	 * @return 
	 * @throws ArqException 
	 */
	private void escolheDocente(Integer id) throws ArqException {
		DocenteRedeDao dao = getDAO(DocenteRedeDao.class);
		obj = dao.findByPrimaryKey(id, DocenteRede.class);

		requisitor.setDocenteRede(obj);
		requisitor.selecionaDocenteRede();

	}

	public CampusIes getCampus() {
		return campus;
	}

	public void setCampus(CampusIes campus) {
		this.campus = campus;
	}

	public SelecionaDocente getRequisitor() {
		return requisitor;
	}

	public void setRequisitor(SelecionaDocente requisitor) {
		this.requisitor = requisitor;
	}

	public ModoBusca getModoBusca() {
		return modoBusca;
	}

	public void setModoBusca(ModoBusca modoBusca) {
		this.modoBusca = modoBusca;
	}

	public static String getListaDocentes() {
		return LISTA_DOCENTES;
	}

	public boolean isExibirForm() {
		return modoBusca == ModoBusca.DETALHADA;
	}

	public ParametrosSelecionaDocente getParametros() {
		return parametros;
	}

	public void setParametros(ParametrosSelecionaDocente parametros) {
		this.parametros = parametros;
	}

	public ValoresSelecionaDocente getValores() {
		return valores;
	}

	public void setValores(ValoresSelecionaDocente valores) {
		this.valores = valores;
	}

	public void setExibirComboCampus(boolean exibirComboCampus) {
		this.exibirComboCampus = exibirComboCampus;
	}

	public boolean isExibirComboCampus() {
		return exibirComboCampus;
	}

	public void setConsultar(boolean consultar) {
		this.consultar = consultar;
	}

	public boolean isConsultar() {
		return consultar;
	}

}
