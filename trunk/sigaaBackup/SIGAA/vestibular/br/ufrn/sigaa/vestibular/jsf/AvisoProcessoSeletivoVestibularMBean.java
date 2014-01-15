/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/04/2009
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import java.util.Calendar;
import java.util.Date;

import javax.faces.event.ValueChangeEvent;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.vestibular.dominio.AvisoProcessoSeletivoVestibular;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;
import br.ufrn.sigaa.vestibular.negocio.MovimentoAvisoProcessoSeletivoVestibular;

/** Controller responsável pelo cadastro de notícias de processos seletivos.
 * 
 * @author Édipo Elder F. Melo
 *
 */
@Component("avisoProcessoSeletivoVestibular")
@Scope("session")
public class AvisoProcessoSeletivoVestibularMBean extends SigaaAbstractController<AvisoProcessoSeletivoVestibular> {
	
	/** Data de divulgação do aviso. */
	private Date dataPublicacao;
	/** Hora de divulgação do aviso. */
	private Date horaPublicacao;
	/** Arquivo anexo do aviso. */
	private UploadedFile arquivo;
	/**  */
	private boolean voltarLista;
	
	/** Cadastra/altera o aviso.
	 * 
	 * <ul>
	 * 	<li>/vestibular/AvisoProcessoSeletivoVestibular/form.jsp</li>
	 * </ul>
	 *  
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		beforeCadastrarAfterValidate();
		erros.addAll(obj.validate());
		if (hasErrors()) {
			carregaListaAvisos();
			return null;
		}
		MovimentoAvisoProcessoSeletivoVestibular movimento = new MovimentoAvisoProcessoSeletivoVestibular();
		movimento.setAviso(obj);
		movimento.setArquivo(arquivo);
		movimento.setCodMovimento(SigaaListaComando.CADASTRAR_AVISO_PROCESSO_SELETIVO_VESTIBULAR);
		try {
			execute(movimento);
		} catch (NegocioException e) {
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
		if (getConfirmButton().equalsIgnoreCase("alterar")){
			addMensagemInformation("Aviso alterado com sucesso!");
			carregaListaAvisos();
			return forward(getListPage());
		} else {
			addMensagemInformation("Aviso cadastrado com sucesso!");
			return cancelar();
		}
	}
	
	/** Remove um aviso. 
	 * 
	 * <ul>
	 * 	<li>/vestibular/AvisoProcessoSeletivoVestibular/form.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#remover()
	 */
	@Override
	public String remover() throws ArqException {
		prepareMovimento(ArqListaComando.ALTERAR);
		GenericDAO dao = getGenericDAO();
		int id = getParameterInt("id", 0);
		this.obj = dao.findByPrimaryKey(id, obj.getClass());
		if (obj == null || obj.getId() == 0) {
			addMensagemErro("Não há objeto informado para remoção");
		} else {
			// o usuário utilizou o botão voltar
			if (!obj.isAtivo())
				throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA, "");
			obj.setAtivo(false);
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			try {
				mov.setCodMovimento(ArqListaComando.ALTERAR);
				executeWithoutClosingSession(mov, getCurrentRequest());
				addMessage("Aviso removido com sucesso!", TipoMensagemUFRN.INFORMATION);
			} catch (NegocioException e) {
				throw new ArqException(e);
			}
		}
		carregaListaAvisos();
		return forward("/vestibular/AvisoProcessoSeletivoVestibular/lista.jsp");
	}
	
	/** Seta a data e hora de publicação do aviso.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#beforeCadastrarAfterValidate()
	 */
	@Override
	public void beforeCadastrarAfterValidate() throws NegocioException,
			SegurancaException, DAOException {
		if (dataPublicacao != null && horaPublicacao != null){
			Calendar cDataPublicacao = Calendar.getInstance();
			cDataPublicacao.setTime(dataPublicacao);
			Calendar cHoraPublicacao = Calendar.getInstance();
			cHoraPublicacao.setTime(horaPublicacao);
			cDataPublicacao.set(Calendar.HOUR, cHoraPublicacao.get(Calendar.HOUR));
			cDataPublicacao.set(Calendar.MINUTE, cHoraPublicacao.get(Calendar.MINUTE));
			obj.setInicioPublicacao(cDataPublicacao.getTime());
		}
		int id = obj.getProcessoSeletivo().getId();
		obj.setProcessoSeletivo(new ProcessoSeletivoVestibular(id));
	}	
	
	/** Prepara para a operação de cadastro.
	 * 
	 * <ul>
	 * 	<li>/vestibular/menus/cadastros.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		init();
		prepareMovimento(SigaaListaComando.CADASTRAR_AVISO_PROCESSO_SELETIVO_VESTIBULAR);
		voltarLista = false;
		return super.preCadastrar();
	}
	
	/** Inicializa os atributos do controller e chama a página de listagem dos avisos.
	 * 
	 * <ul>
	 * 	<li>/vestibular/menus/cadastros.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#listar()
	 */
	@Override
	public String listar() throws ArqException {
		if (!voltarLista)
			init();
		return forward("/vestibular/AvisoProcessoSeletivoVestibular/lista.jsp");
	}

	/** Atualiza um aviso.
	 * 
	 * <ul>
	 * 	<li>/vestibular/AvisoProcessoSeletivoVestibular/form.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	@Override
	public String atualizar() throws ArqException {
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_AVISO_PROCESSO_SELETIVO_VESTIBULAR.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_AVISO_PROCESSO_SELETIVO_VESTIBULAR);
		setId();
		PersistDB obj = this.obj;
		setReadOnly(false);
		this.obj = getGenericDAO().findByPrimaryKey(obj.getId(), AvisoProcessoSeletivoVestibular.class);
		setConfirmButton("Alterar");
		this.dataPublicacao = this.obj.getInicioPublicacao();
		this.horaPublicacao = this.obj.getInicioPublicacao();
		this.voltarLista = true;
		return forward(getFormPage());
	}
	
	/** Inicializa os atributos do controller.
	 * 
	 */
	private void init(){
		this.obj = new AvisoProcessoSeletivoVestibular();
		this.arquivo = null;
		this.dataPublicacao = null;
		this.horaPublicacao = null;
	}
	
	/** Listener responsável por carregar a lista de avisos cadastrados para o processo seletivo.
	 * @param evt
	 * @throws DAOException
	 */
	public void carregaListaAvisos(ValueChangeEvent evt) throws DAOException {
		this.obj.getProcessoSeletivo().setId((Integer) evt.getNewValue());
		carregaListaAvisos();
	}
	
	/** Carrega a lista de avisos cadastrados para o processo seletivo. 
	 * @throws DAOException
	 */
	private void carregaListaAvisos() throws DAOException {
		GenericDAOImpl dao = getDAO(GenericDAOImpl.class);
		obj.setProcessoSeletivo(dao.findByPrimaryKey(obj.getProcessoSeletivo().getId(), ProcessoSeletivoVestibular.class));
		// evitar NullPointException
		if (obj.getProcessoSeletivo() == null){
			obj.setProcessoSeletivo(new ProcessoSeletivoVestibular());
		}
	}
	
	/** Retorna a data de divulgação do aviso. 
	 * @return
	 */
	public Date getDataPublicacao() {
		return dataPublicacao;
	}
	
	/** Seta a data de divulgação do aviso.
	 * @param dataPublicacao
	 */
	public void setDataPublicacao(Date dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
	}
	
	/** Retorna a hora de divulgação do aviso.
	 * @return
	 */
	public Date getHoraPublicacao() {
		return horaPublicacao;
	}
	
	/** Seta a hora de divulgação do aviso.
	 * @param horaPublicacao
	 */
	public void setHoraPublicacao(Date horaPublicacao) {
		this.horaPublicacao = horaPublicacao;
	}

	/** Retorna o arquivo anexo do aviso. 
	 * @return
	 */
	public UploadedFile getArquivo() {
		return arquivo;
	}

	/** Seta o arquivo anexo do aviso.
	 * @param arquivo
	 */
	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	/** Indica se deve mostrar o botão de voltar para a lista de avisos.
	 * @return
	 */
	public boolean isVoltarLista() {
		return voltarLista;
	}

	/** Seta se deve mostrar o botão de voltar para a lista de avisos.
	 * @param voltarLista
	 */
	public void setVoltarLista(boolean voltarLista) {
		this.voltarLista = voltarLista;
	}
}
