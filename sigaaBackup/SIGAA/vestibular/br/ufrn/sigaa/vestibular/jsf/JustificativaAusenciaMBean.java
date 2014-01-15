/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Controller respons�vel pelas opera��es sobre Justificativa de Aus�ncias.
 * @author �dipo Elder F. Melo
 */
@Component("justificativaAusencia")
@Scope("request")
public class JustificativaAusenciaMBean extends
		SigaaAbstractController<JustificativaAusencia> {

	/** Arquivo de justificativa da aus�ncia. */
	private UploadedFile arquivo;
	
	/** ID do Processo Seletivo do qual ser�o analisadas as justificativas de aus�ncia. */  
	private int idProcessoSeletivo;

	/** Lista de fiscais ausentes. */
	private Collection<Fiscal> listaFiscais;
	
	/** Construtor padr�o. */
	public JustificativaAusenciaMBean() {
		obj = new JustificativaAusencia();
	}

	/** Cadastra/Altera justificativa de aus�ncia
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
			// regra de neg�cio: n�o pode voltar ao status 'n�o analisado'.
			if (obj.getStatus() == StatusJustificativaAusencia.NAO_ANALISADO) { 
				addMensagemErro("N�o � permitido voltar a justificativa para o status \"N�O ANALISADO\"");
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
	
	/** Recupera o atributo fiscal para associar � justificativa de aus�ncia.
	 * @return
	 * @throws DAOException
	 */
	private Fiscal recuperaFiscal() throws DAOException {
		FiscalDao dao = getDAO(FiscalDao.class);
		ProcessoSeletivoVestibularDao psDao = getDAO(ProcessoSeletivoVestibularDao.class);
		ProcessoSeletivoVestibular ps = psDao.findUltimoPeriodoInscricaoFiscal();
		if (ps == null) {
			addMensagemErro("N�o h� Processo Seletivo com per�odo de inscri��o de Fiscais definido.");
			return null;
		}
		Fiscal fiscal = dao.findByPessoaProcessoSeletivo(getUsuarioLogado().getPessoa(), ps);
		if (fiscal == null) {
			addMensagemErro("N�o h� registro de trabalho como fiscal no " + ps.getNome());
			return null;
		} else  if (fiscal.getPresenteAplicacao() != null
				&& fiscal.getPresenteAplicacao() != false 
				&& fiscal.getPresenteReuniao() != null
				&& fiscal.getPresenteReuniao() != false) {
			addMensagemWarning("N�o h� registro de aus�ncia no " + ps.getNome());
			return null;
		}
		return fiscal;
	}
	
	/** Retorna uma cole��o de fiscais ausentes no Processo Seletivo.
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
	
	/** Redireciona para o formul�rio de busca de fiscais ausentes.
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
	
	/** Redireciona para o formul�rio de cadastro de justificativa avulsa de aus�ncia.
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
			addMensagemErro("Selecione um Fiscal v�lido");
			return null;
		}
		FiscalDao dao = getDAO(FiscalDao.class);
		Fiscal fiscal = dao.findByPrimaryKey(id, Fiscal.class);
		if (fiscal == null) {
			addMensagemErro("Selecione um Fiscal v�lido");
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
	
	/** Retorna uma lista de SelectItem de poss�veis status de justificativas.
	 * @return
	 */
	public Collection<SelectItem> getAllStatusCombo() {
		Collection<SelectItem> status = new ArrayList<SelectItem>();
		status.add(new SelectItem(StatusJustificativaAusencia.NAO_ANALISADO, "N�O ANALISADO"));
		status.add(new SelectItem(StatusJustificativaAusencia.EM_ANALISE, "EM AN�LISE"));
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
	
	/** Link para a p�gina de formul�rio de cadastro da justificativa.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return "/vestibular/JustificativaAusencia/form.jsp";
	}

	/** Link para a p�gina de listagem de justificativa.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/vestibular/JustificativaAusencia/lista.jsp";
	}
	
	/** Link para a p�gina de formul�rio de an�lise da justificativa.
	 * @return
	 */
	public String getFormAnalise(){
		return "/vestibular/JustificativaAusencia/analise.jsp";
	}

	/** Retorna o arquivo de justificativa da aus�ncia.
	 * @return Arquivo de justificativa da aus�ncia. 
	 */
	public UploadedFile getArquivo() {
		return arquivo;
	}

	/** Seta o arquivo de justificativa da aus�ncia. 
	 * @param arquivo Arquivo de justificativa da aus�ncia. 
	 */
	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	/** Retorna o ID do Processo Seletivo do qual ser�o analisadas as justificativas de aus�ncia. 
	 * @return ID do Processo Seletivo do qual ser�o analisadas as justificativas de aus�ncia. 
	 */
	public int getIdProcessoSeletivo() {
		return idProcessoSeletivo;
	}

	/** Seta o ID do Processo Seletivo do qual ser�o analisadas as justificativas de aus�ncia. 
	 * @param idProcessoSeletivo ID do Processo Seletivo do qual ser�o analisadas as justificativas de aus�ncia. 
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
