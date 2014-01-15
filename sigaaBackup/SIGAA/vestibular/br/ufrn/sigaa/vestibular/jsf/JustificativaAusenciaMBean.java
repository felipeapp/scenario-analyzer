/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/08/2009
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.vestibular.FiscalDao;
import br.ufrn.sigaa.arq.dao.vestibular.ProcessoSeletivoVestibularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.vestibular.dominio.Fiscal;
import br.ufrn.sigaa.vestibular.dominio.JustificativaAusencia;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;
import br.ufrn.sigaa.vestibular.dominio.StatusJustificativaAusencia;
import br.ufrn.sigaa.vestibular.negocio.MovimentoJustificativaFiscal;

/**
 * Controller responsável pelas operações sobre Justificativa de Ausências.
 * @author Édipo Elder F. Melo
 */
@Component("justificativaAusencia")
@Scope("request")
public class JustificativaAusenciaMBean extends
		SigaaAbstractController<JustificativaAusencia> {

	/** Arquivo de justificativa da ausência. */
	private UploadedFile arquivo;
	
	/** ID do Processo Seletivo do qual serão analisadas as justificativas de ausência. */  
	private int idProcessoSeletivo;

	/** Lista de fiscais ausentes. */
	private Collection<Fiscal> listaFiscais;
	
	/** Construtor padrão. */
	public JustificativaAusenciaMBean() {
		obj = new JustificativaAusencia();
	}

	/** Cadastra/Altera justificativa de ausência
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>/vestibular/JustificativaAusencia/analise.jsp</li>
	 * 	<li>/vestibular/JustificativaAusencia/form.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		// se alterando o status da justificativa
		boolean retornaLista = false;
		if (obj.getId() > 0){
			// regra de negócio: não pode voltar ao status 'não analisado'.
			if (obj.getStatus() == StatusJustificativaAusencia.NAO_ANALISADO) { 
				addMensagemErro("Não é permitido voltar a justificativa para o status \"NÃO ANALISADO\"");
				return null;
			}
			// seta os valores antigos.
			FiscalDao dao = getDAO(FiscalDao.class);
			JustificativaAusencia justificativa = dao.refresh(obj);
			obj.setFiscal(justificativa.getFiscal());
			obj.setIdArquivo(justificativa.getIdArquivo());
			obj.setJustificativa(justificativa.getJustificativa());
			if (!obj.isIndeferido()) obj.setMotivoIndeferimento(null);
			retornaLista = true;
		} 
		ListaMensagens msgs = obj.validate();
		if (msgs != null) erros.addAll(msgs);
		if (hasErrors())
			return null;
		prepareMovimento(SigaaListaComando.CADASTRAR_JUSTIFICATIVA_AUSENCIA_FISCAL);
		MovimentoJustificativaFiscal movimento = new MovimentoJustificativaFiscal();
		movimento.setUsuarioLogado(getUsuarioLogado());
		movimento.setJustificativaAusencia(obj);
		movimento.setArquivo(arquivo);
		movimento.setCodMovimento(SigaaListaComando.CADASTRAR_JUSTIFICATIVA_AUSENCIA_FISCAL);
		execute(movimento);
		if (retornaLista){
			addMensagemInformation("Justificativa atualizada com sucesso!");
			try {
				buscar();
			} catch (Exception e) {
				throw new NegocioException(e);
			}
			return forward(getListPage());
		} else {
			addMensagemInformation("Justificativa cadastrada com sucesso!");
			return cancelar();
		}
	}
	
	/** Recupera o atributo fiscal para associar à justificativa de ausência.
	 * @return
	 * @throws DAOException
	 */
	private Fiscal recuperaFiscal() throws DAOException {
		FiscalDao dao = getDAO(FiscalDao.class);
		ProcessoSeletivoVestibularDao psDao = getDAO(ProcessoSeletivoVestibularDao.class);
		ProcessoSeletivoVestibular ps = psDao.findUltimoPeriodoInscricaoFiscal();
		if (ps == null) {
			addMensagemErro("Não há Processo Seletivo com período de inscrição de Fiscais definido.");
			return null;
		}
		Fiscal fiscal = dao.findByPessoaProcessoSeletivo(getUsuarioLogado().getPessoa(), ps);
		if (fiscal == null) {
			addMensagemErro("Não há registro de trabalho como fiscal no " + ps.getNome());
			return null;
		} else  if (fiscal.getPresenteAplicacao() != null
				&& fiscal.getPresenteAplicacao() != false 
				&& fiscal.getPresenteReuniao() != null
				&& fiscal.getPresenteReuniao() != false) {
			addMensagemWarning("Não há registro de ausência no " + ps.getNome());
			return null;
		}
		return fiscal;
	}
	
	/** Retorna uma coleção de fiscais ausentes no Processo Seletivo.
	 * @return
	 * @throws DAOException
	 */
	public Collection<Fiscal> getAllFiscaisAusentes() throws DAOException {
		if (listaFiscais == null) {
			FiscalDao dao = getDAO(FiscalDao.class);
			listaFiscais = dao.findAllAusentesByProcessoSeletivo(idProcessoSeletivo);
		}
		return listaFiscais;
	}
	
	/** Redireciona para o formulário de busca de fiscais ausentes.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>/vestibular/menus/fiscal.jsp</li>
	 * 	<li>/vestibular/JustificativaAusencia/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String listarFiscaisAusentes() {
		listaFiscais = null;
		return forward("/vestibular/JustificativaAusencia/ausentes.jsp");
	}

	/** Prepara para cadastrar uma justificativa de fiscal.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		Fiscal fiscal = recuperaFiscal();
		if (fiscal == null) return null;
		// recupera a justificativa anterior
		FiscalDao dao = getDAO(FiscalDao.class);
		obj = dao.findJustificativaByFiscal(fiscal);
		if (obj == null) {
			obj = new JustificativaAusencia();
			obj.setFiscal(fiscal);
		}
		return forward(getFormPage());
	}
	
	/** Redireciona para o formulário de cadastro de justificativa avulsa de ausência.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>/vestibular/JustificativaAusencia/ausentes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String cadastrarJustificativaAvulsa() throws DAOException{
		Integer id = getParameterInt("id");
		if (id == null) {
			addMensagemErro("Selecione um Fiscal válido");
			return null;
		}
		FiscalDao dao = getDAO(FiscalDao.class);
		Fiscal fiscal = dao.findByPrimaryKey(id, Fiscal.class);
		if (fiscal == null) {
			addMensagemErro("Selecione um Fiscal válido");
			return null;
		}
		JustificativaAusencia justificativa = dao.findJustificativaByFiscal(fiscal);
		if (justificativa != null) {
			addMensagemErro("Existe justificativa cadastrada para este Fiscal");
			return null;
		}
		this.obj = new JustificativaAusencia();
		this.obj.setFiscal(fiscal);
		this.obj.setStatus(StatusJustificativaAusencia.NAO_ANALISADO);
		return forward(getFormPage());
	}
	
	/** Retorna uma lista de SelectItem de possíveis status de justificativas.
	 * @return
	 */
	public Collection<SelectItem> getAllStatusCombo() {
		Collection<SelectItem> status = new ArrayList<SelectItem>();
		status.add(new SelectItem(StatusJustificativaAusencia.NAO_ANALISADO, "NÃO ANALISADO"));
		status.add(new SelectItem(StatusJustificativaAusencia.EM_ANALISE, "EM ANÁLISE"));
		status.add(new SelectItem(StatusJustificativaAusencia.DEFERIDO, "DEFERIDO"));
		status.add(new SelectItem(StatusJustificativaAusencia.INDEFERIDO, "INDEFERIDO"));
		return status;
	}
	
	/** Busca as justificativas de acordo com o processo seletivo selecionado.
	 * 
	 * <br />
	 * Chamado por  
	 * <ul>
	 * 	<li>/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#buscar()
	 */
	@Override
	public String buscar() throws Exception {
		FiscalDao dao = getDAO(FiscalDao.class);
		resultadosBusca = dao.findJustificativaByProcessoSeletivo(idProcessoSeletivo);
		return null;
	}
	
	/** Atualiza o status da justificativa (defere/indefere).
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>/vestibular/JustificativaAusencia/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	@Override
	public String atualizar() throws ArqException {
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_JUSTIFICATIVA_AUSENCIA_FISCAL.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_JUSTIFICATIVA_AUSENCIA_FISCAL);
		populateObj(true);
		setConfirmButton("Alterar");
		return forward(getFormAnalise());
	}
	
	/** Link para a página de formulário de cadastro da justificativa.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return "/vestibular/JustificativaAusencia/form.jsp";
	}

	/** Link para a página de listagem de justificativa.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/vestibular/JustificativaAusencia/lista.jsp";
	}
	
	/** Link para a página de formulário de análise da justificativa.
	 * @return
	 */
	public String getFormAnalise(){
		return "/vestibular/JustificativaAusencia/analise.jsp";
	}

	/** Retorna o arquivo de justificativa da ausência.
	 * @return Arquivo de justificativa da ausência. 
	 */
	public UploadedFile getArquivo() {
		return arquivo;
	}

	/** Seta o arquivo de justificativa da ausência. 
	 * @param arquivo Arquivo de justificativa da ausência. 
	 */
	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	/** Retorna o ID do Processo Seletivo do qual serão analisadas as justificativas de ausência. 
	 * @return ID do Processo Seletivo do qual serão analisadas as justificativas de ausência. 
	 */
	public int getIdProcessoSeletivo() {
		return idProcessoSeletivo;
	}

	/** Seta o ID do Processo Seletivo do qual serão analisadas as justificativas de ausência. 
	 * @param idProcessoSeletivo ID do Processo Seletivo do qual serão analisadas as justificativas de ausência. 
	 */
	public void setIdProcessoSeletivo(int idProcessoSeletivo) {
		this.idProcessoSeletivo = idProcessoSeletivo;
	}

	/** Retorna a lista de fiscais ausentes.
	 * @return
	 * @throws DAOException
	 */
	public Collection<Fiscal> getListaFiscais() throws DAOException {
		return getAllFiscaisAusentes();
	}

}
