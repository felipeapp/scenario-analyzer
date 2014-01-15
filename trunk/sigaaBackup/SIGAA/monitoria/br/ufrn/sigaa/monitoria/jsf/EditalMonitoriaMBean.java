/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/10/2006
 *
 */
package br.ufrn.sigaa.monitoria.jsf;


import static br.ufrn.arq.mensagens.MensagensArquitetura.NAO_HA_OBJETO_REMOCAO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.io.IOException;
import java.util.Collection;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.projetos.EditalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.EditalMonitoria;

/**
 * Controlador para gerenciar a publicação/atualização/remoção de editais de monitoria
 * 
 * @author Ilueny
 * @author Leonardo Campos
 *
 */
@Component("editalMonitoria") @Scope("request")
public class EditalMonitoriaMBean extends SigaaAbstractController<EditalMonitoria> {

	/**
	 * Atributo utilizado para representar o arquivo
	 */
	private UploadedFile file;
	

	/**
	 * Construtor padrão
	 */
	public EditalMonitoriaMBean() {
		obj = new EditalMonitoria();
	}

	/**
	 * Submete o arquivo do edital gravando-o na base de arquivos <br /><br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <br />
	 * <ul>
	 * 		<li> sigaa/monitoria/ArquivoEdital/form.jsp </li>
	 * </ul>
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws DAOException 
	 */
	public String enviarArquivo() throws ArqException, NegocioException {
	    checkRole(SigaaPapeis.GESTOR_MONITORIA);		
	    try {
		int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
		EnvioArquivoHelper.inserirArquivo(idArquivo, file.getBytes(), file.getContentType(), file.getName());
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), EditalMonitoria.class);
		obj.setIdArquivo(idArquivo);
		cadastrar();
		setReadOnly(true);
	    } catch (DAOException e) {
		tratamentoErroPadrao(e);
	    } catch (IOException e) {
		tratamentoErroPadrao(e);
	    }
	    return null;
	}

	/**
	 * Popula os dados e encaminha para a tela de submissão do arquivo do edital <br /><br />
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li> sigaa/monitoria/EditalMonitoria/lista.jsp </li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String populaEnviaArquivo() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		atualizar();
		return forward("/monitoria/ArquivoEdital/form.jsp");
	}

	@Override
	public String getDirBase() {
		return "/monitoria/EditalMonitoria";
	}

	/**
	 * Método responsável por checar as permissões do usuário
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
	}

	/**
	 * Método responsável por checar as permissões do usuário
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 */
	@Override
	public void checkListRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
	}
	
	/**
	 * Permite listar todos os editais cadastrados no Select da View
	 * <br /><br />
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa/monitoria/Relatorios/informativo_sintetico.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	@Override	
	public Collection<SelectItem> getAllCombo() throws ArqException {
		return toSelectItems(getAllAtivos(), "id", "descricao");
	}

	/**
	 * Muda o tipo de edital<br /><br />
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa/monitoria/ProjetoMonitoria/form.jsp</li>
	 * </ul>
	 */
	public void changeTipoEdital(ValueChangeEvent evt) throws DAOException, SegurancaException {
		Character tipo = (Character)evt.getNewValue();
		obj.setTipo(tipo);
	}
	
	
	/**
	 * Permite listar os editais ativos no Select da View Lista somente os
	 * editais de monitoria cuja data final de recebimento de projetos não
	 * tenha expirado.
	 * <br /><br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa/monitoria/ProjetoMonitoria/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<SelectItem> getEditaisAbertosByTipoCombo(char tipo) {	    
	    try {
		EditalDao dao = getDAO(EditalDao.class);
		Collection<EditalMonitoria> editais = dao.findAbertosMonitoria(tipo);
		return toSelectItems(editais, "id", "descricao");
	    } catch (DAOException e) {
		notifyError(e);
		return null;
	    }
	}
	
	/**
	 * Permite listar os "editais" de configuração de projetos externos de monitoria
	 * cuja data final de recebimento de projetos não tenha expirado.
	 * <br /><br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa/monitoria/ProjetoMonitoria/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<SelectItem> getEditalConfiguracaoProjetoExternoAbertos() {	    
	    try {
			EditalDao dao = getDAO(EditalDao.class);
			Collection<EditalMonitoria> editais = dao.findExternosAbertosMonitoria();
			return toSelectItems(editais, "id", "descricao");
	    } catch (DAOException e) {
			notifyError(e);
			return null;
	    }
	}

	/**
	 * Inicia o caso de uso de cadastro<br /><br />
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s)
	 * <ul>
	 * 		<li> sigaa/monitoria/index.jsp </li>
	 * </ul>
	 */
	public String preCadastrar() throws ArqException, NegocioException {
		checkChangeRole();
		obj = new EditalMonitoria();
		obj.getEdital().setAno(CalendarUtils.getAnoAtual());
		setOperacaoAtiva(SigaaListaComando.PUBLICAR_EDITAL_MONITORIA.getId());
		prepareMovimento(SigaaListaComando.PUBLICAR_EDITAL_MONITORIA);
		setConfirmButton("Cadastrar");
		obj.setFatorCargaHoraria(0);
		return forward(getFormPage());
	}
	
	/**
	 * Persiste as informações do edital
	 * <br /><br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa/monitoria/ProjetoMonitoria/form.jsp</li>
	 * </ul>
	 */
	public String cadastrar() throws SegurancaException, ArqException {
	    checkChangeRole();

	    if (getConfirmButton().equalsIgnoreCase("remover")) {
		return remover();
	    } else {
	    	
	    // Setando parâmetros de coordenador de monitoria. Apenas Docentes podem ser coordenadores
	    obj.getEdital().getRestricaoCoordenacao().setPermitirCoordenadorDocente(true);
	    obj.getEdital().getRestricaoCoordenacao().setApenasServidorAtivoCoordena(false);
	    obj.getEdital().getRestricaoCoordenacao().setApenasTecnicoSuperiorCoordena(false);
	    obj.getEdital().getRestricaoCoordenacao().setPermitirCoordenadorTecnico(false);
	    	
		ListaMensagens lista = obj.validate();

		if (!lista.isEmpty()) {
			addMensagens(lista);
		    return null;
		}else {

		    if( !checkOperacaoAtiva(SigaaListaComando.PUBLICAR_EDITAL_MONITORIA.getId()) ) {
			return cancelar();
		    }

		    try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.PUBLICAR_EDITAL_MONITORIA);
			obj.setUsuario(getUsuarioLogado());
			mov.setObjMovimentado(obj);
			execute(mov);
			addMensagem(OPERACAO_SUCESSO);
		    } catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		    }
		    return redirectJSF(getSubSistema().getLink());
		}
	    }
	}
	
	/**
	 * Carrega as informações do edital para serem alteradas <br /><br />
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li> sigaa/monitoria/EditalMonitoria/lista.jsp </li>
	 * </ul>
	 * 
	 */
	public String atualizar() throws ArqException {
	    try {
		setOperacaoAtiva(SigaaListaComando.PUBLICAR_EDITAL_MONITORIA.getId());
		prepareMovimento(SigaaListaComando.PUBLICAR_EDITAL_MONITORIA);
		setId();
		setReadOnly(false);
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), EditalMonitoria.class);
		setConfirmButton("Alterar");
		afterAtualizar();
	    } catch (DAOException e) {
		tratamentoErroPadrao(e);
	    }
	    return forward(getFormPage());
	}
	
	
	/**
	 * Método utilizado para realizar ações antes de remover um Edital de Monitoria
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/EditalMonitoria/lista.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String preRemover() {
	    try {
		prepareMovimento(SigaaListaComando.REMOVER_EDITAL_MONITORIA);
		setId();
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), EditalMonitoria.class);
		setConfirmButton("Remover");
		return forward(getFormPage());
	    } catch (ArqException e) {
		tratamentoErroPadrao(e);
		return null;
	    }
	}
	
	/**
	 * Remove o edital do sistema <br /><br />
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 */
	public String remover() throws ArqException {
		MovimentoCadastro mov = new MovimentoCadastro(obj, SigaaListaComando.REMOVER_EDITAL_MONITORIA);

		if (obj == null || obj.getId() == 0) {
			addMensagem(NAO_HA_OBJETO_REMOCAO);
			return null;
		} else {

			try {
				execute(mov);
				addMensagem(OPERACAO_SUCESSO);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return forward(getFormPage());
			} 

			setResultadosBusca(null);
			afterRemover();

			return redirectJSF(getListPage());

		}
	}
	
	/**
	 * Método utilizado para cancelar uma ação
	 * <br />
	 * Método Chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/ArquivoEdital/form.jsp</li>
	 * 		<li>/sigaa.war/monitoria/EditalMonitoria/form.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String cancelar() {
	    resetBean();
	    return redirectJSF(getListPage());
	}
	
	/**
	 * Retorna todos os editais ativos de monitoria
	 * <br />
	 * Método Chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 */
	public Collection<SelectItem> getAllAtivosCombo() throws ArqException {
	    return toSelectItems(getAllAtivos(), "id", "descricao");
	}
	
	/**
	 * Retorna todos os editais ativos de monitoria
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/EditalMonitoria/lista.jsp</li>
	 * </ul>
	 */
	public Collection<EditalMonitoria> getAllAtivos() throws ArqException {
	    return getDAO(EditalDao.class).findAllAtivosMonitoria();
	}

	public boolean isEditalAssociado(){
		return obj.isAssociado();
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

}
