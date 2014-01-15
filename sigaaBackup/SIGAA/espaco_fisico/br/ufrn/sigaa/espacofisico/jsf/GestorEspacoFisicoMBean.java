/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * MBean responsável por controlar as permissões dos usuários que irão gerenciar os espaços físicos
 * 
 * @author Henrique André
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
	 * Inicializa o objeto que o mbean gerência
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
	 * Inicia o processo de alterar os dados de gestão do espaço físico selecionado
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
			addMensagemErro("Este registro não está disponível.");
			return null;
		}
		
		evitarNull();
		
		setConfirmButton("Alterar");
		return forward(getFormPage());
	}

	/**
	 * Remove a gestão deste espaço físico selecionado
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
			addMensagemErro("Este registro não está disponível.");
			return null;
		}
		
		obj.setAtivo(false);
		
		evitarNull();
		
		setConfirmButton("Remover");
		return forward(getFormPage());
	}		
	
	/**
	 * Método invocado na jsp para finalizar o cadastro ou alteração da gestão do espaço físico
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
	 * Valida os dados do formulário
	 */
	private void validar() {
		ValidatorUtil.validateRequired(obj.getUsuario(), "Nome do Gestor", erros);
		ValidatorUtil.validateRequired(obj.getTipo(), "Permissão", erros);
		if (isUnidade())
			ValidatorUtil.validateRequired(obj.getUnidade(), "Unidade", erros);
		else
			ValidatorUtil.validateRequired(obj.getEspacoFisico(), "Espaço Físico", erros);
	}

	/**
	 * Insere um gestor para um espaço físico
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
			addMensagemInformation("Permissão adicionada com sucesso.");
		} catch (NegocioException e) {
			return tratamentoErroPadrao(e);
		}
		return cancelar();
	}

	/**
	 * Quando a permissão do gestor for sobre um espaço físico, unidade deve ser nula.
	 * Quando a permissão do gestor for sobre uma unidade, o espaço físico deve ser nulo.
	 * Isso é necessário para no momento que for persistido, não possuir ID = 0.
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
	 * Alterar um gestor para um espaço físico
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
			addMensagemInformation("Permissão alterada com sucesso.");
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
			addMensagemWarning("Não foi encontrado nenhum resultado com estes critérios");
			return;
		}
		
		buscaModel = new ListDataModel(resultado);
	}

	/**
	 * Valida os parâmetros da busca
	 */
	private void validarBusca() {

		if (filtros.isNaoSelecionou()) {
			erros.addErro("Selecione um critério para busca.");
			return;
		}
	
		if (filtros.isUsuario()) 
			ValidatorUtil.validateRequiredId(valores.getUsuario().getId(), "Usuário", erros);

		if (filtros.isEspacoFisico())
			ValidatorUtil.validateRequiredId(valores.getEspacoFisico().getId(), "Espaço Físico", erros);
		
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
	 * Monta o select para determinar se a permissão vai ser aplicada a uma uniade ou espaço físico.
	 * 
	 * @return
	 */
	public List<SelectItem> getTipos() {
		List<SelectItem> t = new ArrayList<SelectItem>();
		t.add(new SelectItem(new Integer(UNIDADE), "Unidade"));
		t.add(new SelectItem(new Integer(ESPACO_FISICO), "Espaço Físico"));
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
	 * Classe para mapear os filtros do formulário de busca
	 * 
	 * @author Henrique André
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
	 * Classe que irá receber os valores da formulário de busca
	 * 
	 * @author Henrique André
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
