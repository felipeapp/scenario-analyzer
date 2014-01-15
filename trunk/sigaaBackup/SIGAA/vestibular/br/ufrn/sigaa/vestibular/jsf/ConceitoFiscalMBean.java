/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 23/06/2008
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.dao.vestibular.FiscalDao;
import br.ufrn.sigaa.arq.dao.vestibular.LocalAplicacaoProvaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.vestibular.dominio.ConceitoFiscal;
import br.ufrn.sigaa.vestibular.dominio.Fiscal;

/** 
 * Controller responsável pelas operações com conceitos de fiscais.
 * @author Édipo Elder F. Melo
 *
 */
@Component("conceitoFiscal")
@Scope("request")
public class ConceitoFiscalMBean extends SigaaAbstractController<Fiscal> {

	/** ID do Processo Seletivo ao qual a busca de fiscais se restringe. */
	private int idProcessoSeletivo;
	/** ID do Local de Aplicação de Provas ao qual a busca de fiscais se restringe. */
	private int idLocalAplicacao;
	/** Nome (ou parte do nome) do fiscal ao qual a busca de fiscais se restringe. */
	private String nomeFiscal;
	
	/** Indica que a busca deve considerar o nome do fiscal. */
	private boolean buscaNomeFiscal;
	/** Indica que a busca deve considerar o Local de Aplicação de Prova do fiscal. */
	private boolean buscaLocalAplicacao;
	/** Indica que a busca deve considerar o Processo Seletivo do fiscal. */
	private boolean buscaProcessoSeletivo;
	
	/** Lista de locais de aplicação de provas do processo seletivo*/
	private List<SelectItem> locaisAplicacao;
	
	/** Atualiza o conceito do fiscal.
	 * <br />
	 * Chamado por:
	 * <ul>
	 * 	<li>/vestibular/ConceitoFiscal/conceito.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		if (obj.getConceito() == ConceitoFiscal.INSUFICIENTE 
				&& (obj.getObservacao() == null || obj.getObservacao().isEmpty())) {
			addMensagemErro("Informe no campo observação, o motivo do conceito INSUFICIENTE do fiscal.");
		}
		checkOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		if (hasErrors())
			return null;
		MovimentoCadastro movimento = new MovimentoCadastro();
		movimento.setUsuarioLogado(getUsuarioLogado());
		movimento.setObjMovimentado(obj);
		movimento.setCodMovimento(ArqListaComando.ALTERAR);
		execute(movimento);
		addMensagemInformation("Conceito atualizado com sucesso!");
		try {
			buscar();
		} catch (Exception e) {
			throw new ArqException(e);
		}
		return forward(getListPage());
	}
	
	/** Retorna uma coleção de SelecItem de conceitos de fiscais (SUFICIENTE, INSUFICIENTE).
	 * @return
	 */
	public Collection<SelectItem> getAllConceitoCombo() {
		ArrayList<SelectItem> aList = new ArrayList<SelectItem>();
		// Transforma os conceitos definidos na classe ConceitoFiscal em SelectItens
		for (Character key : ConceitoFiscal.descricaoConceitos.keySet()) {
			SelectItem si = new SelectItem();
			si.setLabel(ConceitoFiscal.descricaoConceitos.get(key));
			si.setValue(key.toString());
			aList.add(si);
		}
		return aList;
	}
	
	/** Inicia a operação de registro de conceito de fiscal.
	 * 
	 * <br />
	 * Chamado por:
	 * <ul>
	 * 	<li>/vestibular/menu/fiscal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarRegistroConceito() throws ArqException {
		obj = new Fiscal();
		resultadosBusca = new ArrayList<Fiscal>();
		locaisAplicacao = new ArrayList<SelectItem>();
		this.buscaLocalAplicacao = false;
		this.buscaNomeFiscal = false;
		prepareMovimento(ArqListaComando.ALTERAR);
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		return forward(getListPage());
	}
	
	/** Seleciona o fiscal para registrar o conceito.
	 * 
	 * <br />
	 * Chamado por:
	 * <ul>
	 * 	<li>/vestibular/ConceitoFiscal/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String selecionarFiscal() throws ArqException {
		obj = new Fiscal();
		populateObj(true);
		prepareMovimento(ArqListaComando.ALTERAR);
		return forward(getFormPage());
	}
	
	/** Busca por fiscais de acordo com os parâmetros informados pelo usuário.
	 * 
	 * <br />
	 * Chamado por:
	 * <ul>
	 * 	<li>/vestibular/ConceitoFiscal/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#buscar()
	 */
	@Override
	public String buscar() throws Exception {
		String nomeFiscal = null;
		int idLocalAplicacao = 0;
		int idProcessoSeletivo = 0;
		if (buscaNomeFiscal){
			if (this.nomeFiscal == null || this.nomeFiscal.isEmpty()) {
			addMensagemErro("Informe o nome (ou parte do nome) do fiscal");
			} else {
				nomeFiscal = this.nomeFiscal;
			}
		}
		if (buscaLocalAplicacao && this.idLocalAplicacao == 0) {
			addMensagemErro("Selecione um Local de Aplicação de Prova válido.");
		} else {
			idLocalAplicacao = this.idLocalAplicacao;
		}
		if (buscaProcessoSeletivo && this.idProcessoSeletivo == 0) {
			addMensagemErro("Selecione um Processo Seletivo Válido");
		} else {
			idProcessoSeletivo = this.idProcessoSeletivo;
		}
		if (! (buscaNomeFiscal || buscaLocalAplicacao || buscaProcessoSeletivo) )
			addMensagemErro("Informe um parâmetro para filtrar a busca");
		if (hasOnlyErrors()) {
			resultadosBusca = new ArrayList<Fiscal>();
			return null;
		}
		FiscalDao dao = getDAO(FiscalDao.class);
		try {
			resultadosBusca = dao.findByNomeMatricula(idProcessoSeletivo, idLocalAplicacao, nomeFiscal, null, null);
		} catch (LimiteResultadosException e) {
			addMensagemErro(e.getMessage());
		}
		return forward(getListPage()); 
	}
	
	/**
	 * Listener responsável por atualizar a lista de locais de aplicação de
	 * prova quando o usuário altera o ID do processo seletivo.
     * 
     * <br />
	 * Chamado por:
     * <ul>
	 * 	<li>/vestibular/ConceitoFiscal/lista.jsp</li>
	 * </ul>
     * 
	 * @param evt
	 * @return
	 * @throws DAOException
	 */
	public String carregaLocalAplicacao(ValueChangeEvent evt)
			throws DAOException {
		idProcessoSeletivo = (Integer) evt.getNewValue();
		return carregaLocalAplicacao();
	}
	
	/** Carrega a lista de locais de aplicação de prova associados ao processo seletivo.
	 * @return
	 * @throws DAOException
	 */
	private String carregaLocalAplicacao() throws DAOException {
		LocalAplicacaoProvaDao dao = getDAO(LocalAplicacaoProvaDao.class);
		locaisAplicacao = toSelectItems(dao.findByProcessoSeletivo(idProcessoSeletivo), "id", "nome");
		idLocalAplicacao = 0;
		return null;
	}
	
	/** Retorna o link para a página de lista de fiscais.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/vestibular/ConceitoFiscal/lista.jsp";
	}
	
	/** Retorna o link para formulário de conceitos de fiscais.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return "/vestibular/ConceitoFiscal/conceito.jsp";
	}

	/** Retorna o ID do Processo Seletivo ao qual a busca de fiscais se restringe. 
	 * @return ID do Processo Seletivo ao qual a busca de fiscais se restringe. 
	 */
	public int getIdProcessoSeletivo() {
		return idProcessoSeletivo;
	}

	/** Seta o ID do Processo Seletivo ao qual a busca de fiscais se restringe. 
	 * @param idProcessoSeletivo ID do Processo Seletivo ao qual a busca de fiscais se restringe. 
	 */
	public void setIdProcessoSeletivo(int idProcessoSeletivo) {
		this.idProcessoSeletivo = idProcessoSeletivo;
	}

	/** Retorna o nome (ou parte do nome) do fiscal ao qual a busca de fiscais se restringe.
	 * @return Nome (ou parte do nome) do fiscal ao qual a busca de fiscais se restringe. 
	 */
	public String getNomeFiscal() {
		return nomeFiscal;
	}

	/** Seta o nome (ou parte do nome) do fiscal ao qual a busca de fiscais se restringe. 
	 * @param nomeFiscal Nome (ou parte do nome) do fiscal ao qual a busca de fiscais se restringe. 
	 */
	public void setNomeFiscal(String nomeFiscal) {
		this.nomeFiscal = nomeFiscal;
	}

	/** Retorna o ID do Local de Aplicação de Provas ao qual a busca de fiscais se restringe. 
	 * @return ID do Local de Aplicação de Provas ao qual a busca de fiscais se restringe. 
	 */
	public int getIdLocalAplicacao() {
		return idLocalAplicacao;
	}

	/** seta o ID do Local de Aplicação de Provas ao qual a busca de fiscais se restringe. 
	 * @param idLocalAplicacao ID do Local de Aplicação de Provas ao qual a busca de fiscais se restringe. 
	 */
	public void setIdLocalAplicacao(int idLocalAplicacao) {
		this.idLocalAplicacao = idLocalAplicacao;
	}

	/** Retorna uma coleção de SelectItem de Locais de Aplicação de acordo com o Processo Seletivo.
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getLocaisAplicacao() throws DAOException {
		if (locaisAplicacao == null)
			carregaLocalAplicacao();
		return locaisAplicacao;
	}

	/** Seta a coleção de SelectItem de Locais de Aplicação.
	 * @param locaisAplicacao
	 */
	public void setLocaisAplicacao(List<SelectItem> locaisAplicacao) {
		this.locaisAplicacao = locaisAplicacao;
	}

	/** Indica que a busca deve considerar o nome do fiscal. 
	 * @return
	 */
	public boolean isBuscaNomeFiscal() {
		return buscaNomeFiscal;
	}

	/** Seta que a busca deve considerar o nome do fiscal. 
	 * @param buscaNomeFiscal
	 */
	public void setBuscaNomeFiscal(boolean buscaNomeFiscal) {
		this.buscaNomeFiscal = buscaNomeFiscal;
	}

	/** Indica que a busca deve considerar o Local de Aplicação de Prova do fiscal. 
	 * @return
	 */
	public boolean isBuscaLocalAplicacao() {
		return buscaLocalAplicacao;
	}

	/** Seta que a busca deve considerar o Local de Aplicação de Prova do fiscal. 
	 * @param buscaLocalAplicacao
	 */
	public void setBuscaLocalAplicacao(boolean buscaLocalAplicacao) {
		this.buscaLocalAplicacao = buscaLocalAplicacao;
	}

	/** Indica que a busca deve considerar o Processo Seletivo do fiscal. 
	 * @return
	 */
	public boolean isBuscaProcessoSeletivo() {
		return buscaProcessoSeletivo;
	}

	/** Seta que a busca deve considerar o Processo Seletivo do fiscal. 
	 * @param buscaProcessoSeletivo
	 */
	public void setBuscaProcessoSeletivo(boolean buscaProcessoSeletivo) {
		this.buscaProcessoSeletivo = buscaProcessoSeletivo;
	}

}
