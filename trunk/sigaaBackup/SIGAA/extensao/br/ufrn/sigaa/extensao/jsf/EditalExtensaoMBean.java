/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/11/2006
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.io.IOException;
import java.util.Collection;

import javax.faces.component.html.HtmlDataTable;
import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.projetos.EditalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dominio.EditalExtensao;
import br.ufrn.sigaa.extensao.dominio.EditalExtensaoRegra;
import br.ufrn.sigaa.projetos.dominio.Edital;

/*******************************************************************************
 * Gerencia o cadastro de editais de extensão.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Component("editalExtensao") 
@Scope("session")
public class EditalExtensaoMBean extends SigaaAbstractController<EditalExtensao> {

	/** Atributo utilizado para representar o arquivo a ser upado */
	private UploadedFile file;
	/** Atributo utilizado para representar a regra do Edital de Extensão */
	private EditalExtensaoRegra novaRegra = new EditalExtensaoRegra();
	/** Atributo utilizado para representar o dataTable do HTML */
	private HtmlDataTable dataTableRegras = new HtmlDataTable();
	

	public EditalExtensaoMBean() {
		inicializarEditalExtensao();
	}

	/**
	 * Inicializa objeto para cadastro de um novo edital.
	 */
	private void inicializarEditalExtensao() {
		obj = new EditalExtensao();
		obj.setTipo(Edital.EXTENSAO);
	    obj.setAno(getCalendarioVigente().getAno());
	    obj.setSemestre(getCalendarioVigente().getPeriodo());
	}

	
	/**
	 * Após o cadastro redireciona para pagina com a lista de editais.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Método não é chamado por JSP(s).</li>
	 * </ul>
	 * 
	 */
	@Override
	public String forwardCadastrar() {
	    return forward(getListPage());
	}

	/**
	 * Método utilizado para informar todas as descrições ativas dos Editais.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Método não é chamado por JSP(s).</li>
	 * </ul>
	 * 
	 */
	public Collection<SelectItem> getAllCombo() throws ArqException {
	    return toSelectItems(getAllAtivos(), "id", "descricao");
	}

	/**
	 * Método utilizado para checar o papel do usuário.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Método não é chamado por JSP(s).</li>
	 * </ul>
	 * 
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
	    checkRole(SigaaPapeis.GESTOR_EXTENSAO);
	}
	
	/**
	 * Lista todos so editais de extensão em aberto.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s).</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<SelectItem> getAbertosCombo() {
	    try {
		EditalDao dao = getDAO(EditalDao.class);
		Collection<Edital> editais = dao.findAbertos(Edital.EXTENSAO);
		return toSelectItems(editais, "id", "descricao");
	    } catch (DAOException e) {
		notifyError(e);
		return null;
	    }
	}

	/**
	 * Faz o upload do arquivo com os dados completos do edital.
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/ArquivoEdital/form.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws SegurancaException 
	 */
	public String enviarArquivo() throws SegurancaException, ArqException {
	    checkChangeRole();
	    GenericDAO dao = null;
	    try {
		int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
		EnvioArquivoHelper.inserirArquivo(idArquivo, file.getBytes(), file.getContentType(), file.getName());
		dao = getGenericDAO();
		obj = dao.findByPrimaryKey(obj.getId(), EditalExtensao.class);
		obj.setIdArquivo(idArquivo);
		cadastrar();
		if (!hasErrors()) {
		    addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		}
	    } catch (IOException e) {
		tratamentoErroPadrao(e);
	    }

	    setReadOnly(true);
	    return null;
	}

	/**
	 * Carrega o edital selecionado e prepara o form para o envio do arquivo do
	 * edital.
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/EditalExtensao/lista.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String populaEnviaArquivo() throws ArqException {
		atualizar();
		return forward("/extensao/ArquivoEdital/form.jsp");
	}

	@Override
	public String getDirBase() {
	    return "/extensao/EditalExtensao";
	}
	
	/**
	 * Prepara pra remover o edital. a remocao eh feita setando a propriedade
	 * ativo = false do edital.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s).</li>
	 * </ul>
	 * 
	 */
	@Override
	public void afterPreRemover() {
	    try {
		prepareMovimento(ArqListaComando.ALTERAR);
	    } catch (ArqException e) {
		e.printStackTrace();
	    }
	}
	
	/**
	 * Inicia o caso de uso de cadastro
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/menu.jsp</li>
	 * 		<li>/sigaa.war/extensao/EditalExtensao/lista.jsp</li>
	 * </ul>
	 */
	public String preCadastrar() throws ArqException, NegocioException {
	    checkChangeRole();
	    resetBean();
	    setOperacaoAtiva(SigaaListaComando.PUBLICAR_EDITAL_EXTENSAO.getId());
	    prepareMovimento(SigaaListaComando.PUBLICAR_EDITAL_EXTENSAO);	    
	    setConfirmButton("Cadastrar");	    
		inicializarEditalExtensao();
	    return forward("/extensao/EditalExtensao/form.jsp");
	}

	
	/**
	 * Persiste as informações do edital
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/extensao/EditalExtensao/form.jsp</li>
	 * </ul>
	 * 
	 * @throws NegocioException 
	 */
	public String cadastrar() throws SegurancaException, ArqException {
	    checkChangeRole();
	    if (getConfirmButton().equalsIgnoreCase("remover")) {
		return remover();
	    } else {

		erros = new ListaMensagens();
		ListaMensagens lista = obj.validate();

		if (lista != null && !lista.isEmpty()) {
		    erros.addAll(lista.getMensagens());
		}

		if (hasErrors()) {
		    return null;
		}else {
		    try {
			beforeCadastrarAfterValidate();
			if( !checkOperacaoAtiva(SigaaListaComando.PUBLICAR_EDITAL_EXTENSAO.getId()) ) {
			    return cancelar();
			}
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(SigaaListaComando.PUBLICAR_EDITAL_EXTENSAO);
			execute(mov);
			addMensagem(OPERACAO_SUCESSO);
		    } catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			hasErrors();
			return forward(getFormPage());
		    }
		    afterCadastrar();
		    return redirectJSF(getListPage());
		} 
	    }
	}
	
	/**
	 * Carrega as informações do edital para serem alteradas
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li> sigaa.war/extensao/EditalExtensao/lista.jsp </li>
	 * </ul>
	 */
	public String atualizar() throws ArqException {
	    try {
		setOperacaoAtiva(SigaaListaComando.PUBLICAR_EDITAL_EXTENSAO.getId());
		prepareMovimento(SigaaListaComando.PUBLICAR_EDITAL_EXTENSAO);
		setId();
		setReadOnly(false);
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), EditalExtensao.class);
		setConfirmButton("Alterar");
		afterAtualizar();
	    } catch (Exception e) {
		tratamentoErroPadrao(e);
	    }
	    return forward(getFormPage());
	}
	
	/**
	 * Método chamado para remover uma entidade.
	 * 
	 * Chamado por:
	 * sigaa.ear/sigaa.war/extensao/EditalExtensao/form.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String remover() throws ArqException {
	    obj.setAtivo(false);
	    MovimentoCadastro mov = new MovimentoCadastro();
	    mov.setObjMovimentado(obj);

	    if (obj.getId() == 0) {
		addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
		return null;
	    } else {
		mov.setCodMovimento(SigaaListaComando.REMOVER_EDITAL_EXTENSAO);
		try {
		    execute(mov);
		    addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		    redirectJSF(getListPage());
		    return null;
		} catch (NegocioException e) {
		    addMensagemErro(e.getMessage());
		} catch (Exception e) {
		    addMensagemErroPadrao();
		}
		return forward(getFormPage());
	    }
	}
	
	
	/**
	 * Retorna todos os itens de uma determinada entidade considerando o
	 * atributo ativo.
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/EditalExtensao/lista.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	@Override
	public Collection<EditalExtensao> getAllAtivos() throws ArqException {
	    return getDAO(EditalDao.class).findAllAtivosExtensao();
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

	/**
	 * Adiciona uma regra a lista de regras do edital.
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/EditalExtensao/form.jsp
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String adicionarRegra() {
		try {
			novaRegra.setAtivo(true);
			novaRegra.setEditalExtensao(obj);
			if (!novaRegra.getTipoAtividadeExtensao().isPrograma()) {
				novaRegra.setMinAcoesVinculadas(0);
			}
			
			// validacao
			ListaMensagens mensagens = new ListaMensagens();
			mensagens = novaRegra.validate();
			if (!mensagens.isEmpty()) {
				addMensagens(mensagens);
				return null;
			}

			if (!obj.getRegras().contains(novaRegra)) {
				getGenericDAO().initialize(novaRegra.getTipoAtividadeExtensao());
				obj.addRegra(novaRegra);
			} else {
				addMensagemErro("Regra já está inserida no edital.");
			}

			// limpa os dados do objetivo atual
			novaRegra = new EditalExtensaoRegra();
		}catch (DAOException e) {
			tratamentoErroPadrao(e);
		}
		return null;
	}
	
	/**
	 * Método utilizado para remover uma regra do edital
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/EditalExtensao/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String removerRegra() {	    
	    EditalExtensaoRegra regraRemovida = (EditalExtensaoRegra) getDataTableRegras().getRowData();
	    if (regraRemovida.getId() != 0) {
		regraRemovida.setAtivo(false);
	    }else {
		obj.getRegras().remove(regraRemovida);
	    }
	    return null;
	}
	
	public void permitidoCoordenadorTecnico(){
		redirectMesmaPagina();
	}

	public EditalExtensaoRegra getNovaRegra() {
	    return novaRegra;
	}

	public void setNovaRegra(EditalExtensaoRegra novaRegra) {
	    this.novaRegra = novaRegra;
	}

	public HtmlDataTable getDataTableRegras() {
	    return dataTableRegras;
	}

	public void setDataTableRegras(HtmlDataTable dataTableRegras) {
	    this.dataTableRegras = dataTableRegras;
	}

}
