/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 23/04/2009
 *
 */
package br.ufrn.sigaa.espacofisico.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.espacofisico.GestorEspacoFisicoDao;
import br.ufrn.sigaa.arq.dao.espacofisico.TipoGestorEspacoFisicoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.espacofisico.dominio.EspacoFisico;
import br.ufrn.sigaa.espacofisico.dominio.GestorEspacoFisico;
import br.ufrn.sigaa.espacofisico.dominio.TipoGestorEspacoFisico;
import br.ufrn.sigaa.espacofisico.negocio.GestorEspacoFisicoValidator;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * MBean respons�vel por controlar as permiss�es dos usu�rios que ir�o gerenciar os espa�os f�sicos
 * 
 * @author Henrique Andr�
 *
 */

@Component("gestorEspacoBean") @Scope("request")
public class GestorEspacoFisicoMBean extends SigaaAbstractController<GestorEspacoFisico> {

	private static final int UNIDADE = 0;
	private static final int ESPACO_FISICO = 1;
	
	private DataModel buscaModel;
	private Filtros filtros = new Filtros();
	private ValoresBusca valores = new ValoresBusca();
	
	private int tipo = ESPACO_FISICO;
	
	/**
	 * Inicializa o objeto que o mbean ger�ncia
	 */
	private void init() {
		obj = new GestorEspacoFisico();
		obj.setUnidade(new Unidade());
		obj.setEspacoFisico(new EspacoFisico());
		obj.setUsuario(new Usuario());
		obj.getUsuario().setPessoa(new Pessoa());
		obj.setTipo(new TipoGestorEspacoFisico());
	}
	
	/**
	 * Inicia o cadastro de um gestor
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/infra_fisica/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarCadastrar() throws ArqException {
		prepareMovimento(ArqListaComando.CADASTRAR);
		setReadOnly(false);
		init();
		return forward(getFormPage());
	}
	
	/**
	 * Inicia a busca de gestores
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * <li>sigaa.war/infra_fisica/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarBusca() {
		init();
		setConfirmButton("Cadastrar");
		return forward(getDirBase() + "/busca.jsp");
	}

	/**
	 * Inicia o processo de alterar os dados de gest�o do espa�o f�sico selecionado
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * <li>sigaa.war/infra_fisica/gestor/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarAlterar() throws ArqException {
		prepareMovimento(ArqListaComando.ALTERAR);
		setReadOnly(true);
		
		obj = (GestorEspacoFisico) buscaModel.getRowData();
		
		if (obj.isGestorEspacoFisico())
			tipo = ESPACO_FISICO;
		else
			tipo = UNIDADE;
		
		if (isEmpty(obj) || !obj.isAtivo()) {
			addMensagemErro("Este registro n�o est� dispon�vel.");
			return null;
		}
		
		evitarNull();
		
		setConfirmButton("Alterar");
		return forward(getFormPage());
	}

	/**
	 * Remove a gest�o deste espa�o f�sico selecionado
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * <li>sigaa.war/infra_fisica/gestor/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarRemover() throws ArqException {
		prepareMovimento(ArqListaComando.ALTERAR);
		setReadOnly(true);
		
		obj = (GestorEspacoFisico) buscaModel.getRowData();
		
		if (obj.isGestorEspacoFisico())
			tipo = ESPACO_FISICO;
		else
			tipo = UNIDADE;		
		
		if (isEmpty(obj) || !obj.isAtivo()) {
			addMensagemErro("Este registro n�o est� dispon�vel.");
			return null;
		}
		
		obj.setAtivo(false);
		
		evitarNull();
		
		setConfirmButton("Remover");
		return forward(getFormPage());
	}		
	
	/**
	 * M�todo invocado na jsp para finalizar o cadastro ou altera��o da gest�o do espa�o f�sico
	 * 
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * <li>sigaa.war/infra_fisica/gestor/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String persistir() throws ArqException {

		validar();
		
		if (hasErrors())
			return null;
		
		try {
			if (isUnidade())
				GestorEspacoFisicoValidator.evitarDuplicidadeGestaoUnidade(obj);
			else
				GestorEspacoFisicoValidator.evitarDuplicidadeGestaoEspacoFisico(obj);
			
			if (getUltimoComando().equals(ArqListaComando.CADASTRAR))
				return inserir();
			else if (getUltimoComando().equals(ArqListaComando.ALTERAR)) 
				return alterar();
			
		} catch (NegocioException e) {
			return tratamentoErroPadrao(e, e.getMessage());
		}
		
		return null;
		
	}

	/**
	 * Valida os dados do formul�rio
	 */
	private void validar() {
		ValidatorUtil.validateRequired(obj.getUsuario(), "Nome do Gestor", erros);
		ValidatorUtil.validateRequired(obj.getTipo(), "Permiss�o", erros);
		if (isUnidade())
			ValidatorUtil.validateRequired(obj.getUnidade(), "Unidade", erros);
		else
			ValidatorUtil.validateRequired(obj.getEspacoFisico(), "Espa�o F�sico", erros);
	}

	/**
	 * Insere um gestor para um espa�o f�sico
	 * 
	 * @return
	 * @throws ArqException
	 */
	private String inserir() throws ArqException {
		
		setarNull();
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(ArqListaComando.CADASTRAR);
		
		try {
			execute(mov);
			addMensagemInformation("Permiss�o adicionada com sucesso.");
		} catch (NegocioException e) {
			return tratamentoErroPadrao(e);
		}
		return cancelar();
	}

	/**
	 * Quando a permiss�o do gestor for sobre um espa�o f�sico, unidade deve ser nula.
	 * Quando a permiss�o do gestor for sobre uma unidade, o espa�o f�sico deve ser nulo.
	 * Isso � necess�rio para no momento que for persistido, n�o possuir ID = 0.
	 */
	private void setarNull() {
		if (isEspacoFisico()) 
			obj.setUnidade(null);
		else 
			obj.setEspacoFisico(null);
	}
	
	/**
	 * Evitar null em objetos
	 */
	private void evitarNull() {
		if (isEspacoFisico()) 
			obj.setUnidade(new Unidade());
		else 
			obj.setEspacoFisico(new EspacoFisico());
	}	
	
	/**
	 * Alterar um gestor para um espa�o f�sico
	 * 
	 * @return
	 * @throws ArqException
	 */
	private String alterar() throws ArqException {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(ArqListaComando.ALTERAR);
		
		setarNull();
		
		try {
			execute(mov);
			addMensagemInformation("Permiss�o alterada com sucesso.");
		} catch (NegocioException e) {
			return tratamentoErroPadrao(e);
		}
		return cancelar();
	}	

	/**
	 * Realizar a busca de um gestor
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * <li>sigaa.war/infra_fisica/gestor/busca.jsp</li>
	 * </ul>
	 * 
	 * @param event
	 * @throws DAOException 
	 */
	public void buscar(ActionEvent event) throws DAOException  {
		
		validarBusca();

		if (hasErrors())
			return;
		
		GestorEspacoFisicoDao dao = getDAO(GestorEspacoFisicoDao.class);
		
		List<GestorEspacoFisico> resultado = dao.findAtivosByUsuarioEspacoFisico(filtros, valores);
			
		if (isEmpty(resultado)) {
			addMensagemWarning("N�o foi encontrado nenhum resultado com estes crit�rios");
			return;
		}
		
		buscaModel = new ListDataModel(resultado);
	}

	/**
	 * Valida os par�metros da busca
	 */
	private void validarBusca() {

		if (filtros.isNaoSelecionou()) {
			erros.addErro("Selecione um crit�rio para busca.");
			return;
		}
	
		if (filtros.isUsuario()) 
			ValidatorUtil.validateRequiredId(valores.getUsuario().getId(), "Usu�rio", erros);

		if (filtros.isEspacoFisico())
			ValidatorUtil.validateRequiredId(valores.getEspacoFisico().getId(), "Espa�o F�sico", erros);
		
		if (filtros.isUnidade())
			ValidatorUtil.validateRequiredId(valores.getUnidade().getId(), "Undidade", erros);

	}
	
	/**
	 * Monta combo com os tipos de gestores
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * <li>sigaa.war/infra_fisica/gestor/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getTipoGestorCombo() throws DAOException {
		TipoGestorEspacoFisicoDao dao = getDAO(TipoGestorEspacoFisicoDao.class);
		Collection<TipoGestorEspacoFisico> col = dao.findAll(TipoGestorEspacoFisico.class);
		
		return toSelectItems(col,"id", "denominacao");
	}
	
	
	public boolean isUnidade() {
		return tipo == UNIDADE;			
	}
	
	public boolean isEspacoFisico() {
		return tipo == ESPACO_FISICO;			
	}	
	
	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	/**
	 * Monta o select para determinar se a permiss�o vai ser aplicada a uma uniade ou espa�o f�sico.
	 * 
	 * @return
	 */
	public List<SelectItem> getTipos() {
		List<SelectItem> t = new ArrayList<SelectItem>();
		t.add(new SelectItem(new Integer(UNIDADE), "Unidade"));
		t.add(new SelectItem(new Integer(ESPACO_FISICO), "Espa�o F�sico"));
		return t;
	}
	
	@Override
	public String getDirBase() {
		return "/infra_fisica/gestor";
	}
	
	@Override
	public String forwardCadastrar() {
		return cancelar();
	}

	public DataModel getBuscaModel() {
		return buscaModel;
	}

	public void setBuscaModel(DataModel buscaModel) {
		this.buscaModel = buscaModel;
	}
	
	public Filtros getFiltros() {
		return filtros;
	}

	public void setFiltros(Filtros filtros) {
		this.filtros = filtros;
	}	
	
	public ValoresBusca getValores() {
		return valores;
	}

	public void setValores(ValoresBusca valores) {
		this.valores = valores;
	}

	/**
	 * Classe para mapear os filtros do formul�rio de busca
	 * 
	 * @author Henrique Andr�
	 */
	public class Filtros {
		private boolean usuario;
		private boolean espacoFisico;
		private boolean unidade;
		
		public boolean isUsuario() {
			return usuario;
		}
		public void setUsuario(boolean usuario) {
			this.usuario = usuario;
		}
		public boolean isEspacoFisico() {
			return espacoFisico;
		}
		public void setEspacoFisico(boolean espacoFisico) {
			this.espacoFisico = espacoFisico;
		}
		public boolean isUnidade() {
			return unidade;
		}
		public void setUnidade(boolean unidade) {
			this.unidade = unidade;
		}
		public boolean isNaoSelecionou() {
			return !(espacoFisico || usuario || unidade);
		}
	}

	/**
	 * Classe que ir� receber os valores da formul�rio de busca
	 * 
	 * @author Henrique Andr�
	 *
	 */
	public class ValoresBusca {
		
		private Usuario usuario = new Usuario();
		private EspacoFisico espacoFisico = new EspacoFisico();
		private Unidade unidade = new Unidade();
		
		public Usuario getUsuario() {
			return usuario;
		}
		public void setUsuario(Usuario usuario) {
			this.usuario = usuario;
		}
		public EspacoFisico getEspacoFisico() {
			return espacoFisico;
		}
		public void setEspacoFisico(EspacoFisico espacoFisico) {
			this.espacoFisico = espacoFisico;
		}
		public Unidade getUnidade() {
			return unidade;
		}
		public void setUnidade(Unidade unidade) {
			this.unidade = unidade;
		}
	}
	
}
