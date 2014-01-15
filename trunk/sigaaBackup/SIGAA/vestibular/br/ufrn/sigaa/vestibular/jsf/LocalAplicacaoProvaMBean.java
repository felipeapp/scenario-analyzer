/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 07/05/2008
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.dao.vestibular.LocalAplicacaoProvaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.vestibular.dominio.LocalAplicacaoProva;
import br.ufrn.sigaa.vestibular.dominio.Sala;

/**
 * Controller do cadastro de Local de Aplica��o de Prova
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
@Component("localAplicacaoProva")
@Scope("session")
public class LocalAplicacaoProvaMBean extends
		SigaaAbstractController<LocalAplicacaoProva> {

	/** Lista de munic�pios do endere�o usado para auxiliar a busca por CEP. */
	private Collection<SelectItem> municipiosEndereco = new ArrayList<SelectItem>(0);
	
	/** Sala a ser adicionada no local de aplica��o. */
	private Sala salaAdicionada = new Sala();
	
	/** Auxilia no controle de opera��o cancelada. */
	private boolean operacaoCancelada = false;

	/** Construtor padr�o. */
	public LocalAplicacaoProvaMBean() {
		obj = new LocalAplicacaoProva();
	}

	/**
	 * Adiciona a sala � lista de salas do local de aplica��o. Se existe alguma
	 * sala com o mesmo n�mero, a mesma n�o � adicionada. A lista � ordenada
	 * pelo n�mero da sala.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/vestibular/LocalAplicacaoProva/form.jsp</li>
	 * </ul>
	 * 
	 */
	public void adicionaSala() {
		// verifica se j� existe sala como mesmo n�mero
		boolean tem = false;
		for (Sala sala : obj.getSalas()) {
			if (sala.getNumero().equals(getSalaAdicionada().getNumero())) {
				tem = true;
			}
		}
		if (!tem) {
			Sala sala = new Sala();
			sala.setArea(getSalaAdicionada().getArea());
			sala.setCapacidadeIdeal(getSalaAdicionada().getCapacidadeIdeal());
			sala.setCapacidadeMaxima(getSalaAdicionada().getCapacidadeMaxima());
			sala.setNumero(getSalaAdicionada().getNumero());
			sala.setLocalAplicacaoProva(obj);
			erros.addAll(sala.validate());
			if (hasErrors()) {
				return;
			}
			obj.getSalas().add(sala);
			// calcula a capacidade ideal total
			obj.setCapacidadeIdealTotal(obj.getCapacidadeIdealTotal()
					+ sala.getCapacidadeIdeal());
			Collections.sort(obj.getSalas(), new Comparator<Sala>() {
				public int compare(Sala s1, Sala s2) {
					try {
						return Integer.parseInt(s1.getNumero())
								- Integer.parseInt(s2.getNumero());
					} catch (Exception e) {
						return s1.getNumero().compareTo(s2.getNumero());
					}
				}
			});
			getSalaAdicionada().setNumero(null);
		} else {
			addMensagemWarning("J� existe na lista uma sala como mesmo n�mero");
		}
	}

	/**
	 * Listener para auxiliar na busca do logradouro do endere�o. Sua
	 * funcionalidade � carregar a lista de munic�pios quando o usu�rio
	 * seleciona uma Unidade da Federa��o diferente.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/vestibular/LocalAplicacaoProva/form.jsp</li>
	 * <ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void carregarMunicipios(ValueChangeEvent e) throws DAOException {
		String selectId = e.getComponent().getId();
		if (selectId != null && e.getNewValue() != null) {
			Integer ufId = (Integer) e.getNewValue();
			carregarMunicipiosEndereco(ufId);
		}
	}

	/**
	 * Carrega a lista de munic�pios dado uma Unidade da Federa��o.
	 * 
	 * @param idUf
	 * @throws DAOException
	 */
	private void carregarMunicipiosEndereco(Integer idUf) throws DAOException {
		if (idUf == null) {
			idUf = obj.getEndereco().getUnidadeFederativa().getId();
		}

		MunicipioDao dao = getDAO(MunicipioDao.class);
		UnidadeFederativa uf = dao.findByPrimaryKey(idUf,
				UnidadeFederativa.class);
		Collection<Municipio> municipios = dao.findByUF(idUf);
		municipiosEndereco = new ArrayList<SelectItem>(0);
		municipiosEndereco.add(new SelectItem(uf.getCapital().getId(), uf
				.getCapital().getNome()));
		municipiosEndereco.addAll(toSelectItems(municipios, "id", "nome"));
	}

	/** Verifica os pap�is: VESTIBULAR.
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.VESTIBULAR);
	}

	/** Retorna uma Cole��o de locais de aplica��o de prova.
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAll()
	 */
	@Override
	public Collection<LocalAplicacaoProva> getAll() throws ArqException {
		LocalAplicacaoProvaDao localDao = getDAO(LocalAplicacaoProvaDao.class);
		return localDao.findAll(LocalAplicacaoProva.class, "nome", "asc");
	}

	/** Retorna uma cole��o de SelectItem de locais de aplica��o de prova.
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllCombo()
	 */
	@Override
	public Collection<SelectItem> getAllCombo() {
		return getAll(LocalAplicacaoProva.class, "id", "nome");
	}

	/**
	 * Retorna uma cole��o de SelectItem de munic�pios
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getMunicipiosEndereco() throws DAOException {
		if (municipiosEndereco == null || municipiosEndereco.size() == 0) {
			if (obj.getEndereco().getUnidadeFederativa() != null
					&& obj.getEndereco().getUnidadeFederativa().getId() != 0)
				carregarMunicipiosEndereco(obj.getEndereco()
						.getUnidadeFederativa().getId());
		}
		return municipiosEndereco;
	}

	/** Retorna a sala a ser adicionada ao local de aplica��o de prova.
	 * @return
	 */
	public Sala getSalaAdicionada() {
		return salaAdicionada;
	}

	/**
	 * Retorna a lista de salas cadastradas na forma de uma cole��o de
	 * SelectItem. Atualmente, o formul�rio para cadastro de locais de aplica��o
	 * n�o utiliza esta forma de listagem das salas.
	 * 
	 * @return
	 */
	public Collection<SelectItem> getSalas() {
		return toSelectItems(obj.getSalas(), "numero", "descricao");
	}

	/**
	 * Remove uma sala cadastrada da lista de salas.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/vestibular/LocalAplicacaoProva/form.jsp</li>
	 * </ul>
	 * 
	 */
	public void removeSala() {
		String salaRemover = getParameter("id");
		// cria um novo ArrayList excluindo o elemento selecionado
		for (Iterator<Sala> iterator = obj.getSalas().iterator(); iterator
				.hasNext();) {
			Sala sala = iterator.next();
			if (sala.getNumero().equals(salaRemover)) {
				iterator.remove();
			}
		}
	}

	/** Seta a lista de munic�pios do endere�o usado para auxiliar a busca por CEP. 
	 * @param municipiosEndereco
	 */
	public void setMunicipiosEndereco(Collection<SelectItem> municipiosEndereco) {
		this.municipiosEndereco = municipiosEndereco;
	}

	/** Seta a sala a ser adicionada no local de aplica��o. 
	 * @param salaAdicionada
	 */
	public void setSalaAdicionada(Sala salaAdicionada) {
		this.salaAdicionada = salaAdicionada;
	}

	/**
	 * Reseta o bean para iniciar a opera��o de cadastro de um local de
	 * aplica��o de prova.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/vestibular/menus/cadastros.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		this.obj = new LocalAplicacaoProva();
		resetBean();
		this.operacaoCancelada = false;
		setReadOnly(true);
		return super.preCadastrar();
	}

	/**
	 * Cancela a opera��o.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/vestibular/LocalAplicacaoProva/form.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractController#cancelar()
	 */
	@Override
	public String cancelar() {
		this.operacaoCancelada = true;
		return redirectJSF(getSubSistema().getLink());
	}

	/**
	 * Cadastra/atualiza o local de aplica��o de prova.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/vestibular/LocalAplicacaoProva/form.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		if (operacaoCancelada)
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA, "");
		return super.cadastrar();
	}
	
	
	/**
	 * Atualiza um local de aplica��o de prova.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/vestibular/LocalAplicacaoProva/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	@Override
	public String atualizar() throws ArqException {
		if (this.obj == null)
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA,"");
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		prepareMovimento(ArqListaComando.ALTERAR);
		GenericDAO dao = getGenericDAO();
		setId();
		PersistDB obj = this.obj;
		setReadOnly(false);
		this.obj = dao.findByPrimaryKey(obj.getId(), LocalAplicacaoProva.class);
		if (this.obj == null)
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA,"");
		setConfirmButton("Alterar");
		operacaoCancelada = false;
		return forward(getFormPage());
	}

	/**
	 * Remove um local de aplica��o de prova.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/vestibular/LocalAplicacaoProva/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preRemover()
	 */
	@Override
	public String preRemover() {
		try {
			obj = new LocalAplicacaoProva();
			populateObj(true);
			if (obj == null) {
				obj = new LocalAplicacaoProva();
				addMensagem(MensagensArquitetura.ACAO_JA_EXECUTADA,"Local de Aplica��o de Prova", "removido");
				return null;
			}
			prepareMovimento(ArqListaComando.REMOVER);
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
		}
		setReadOnly(true);
		setConfirmButton("Remover");
		return forward(getFormPage());
	}
}
