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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
import br.ufrn.sigaa.extensao.dominio.EditalExtensaoLinhaAtuacao;
import br.ufrn.sigaa.extensao.dominio.EditalExtensaoRegra;
import br.ufrn.sigaa.projetos.dominio.Edital;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;

/*******************************************************************************
 * Gerencia o cadastro de editais de extensão.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@SuppressWarnings("serial")
@Component("editalExtensao") 
@Scope("session")
public class EditalExtensaoMBean extends SigaaAbstractController<EditalExtensao> {

	/** Atributo utilizado para representar o arquivo a ser upado */
	private UploadedFile file;
	/** Atributo utilizado para representar a regra do Edital de Extensão */
	private EditalExtensaoRegra novaRegra = new EditalExtensaoRegra();
	/** Atributo utilizado para representar uma linha de atuação do Edital de Extensão */
	private EditalExtensaoLinhaAtuacao novaLinha = new EditalExtensaoLinhaAtuacao();
	/**membro da comissão de avaliação dos projetos, usado no cadastro de linha de atuação. */
	private MembroComissao membroComissao = new MembroComissao();
	/**DataTable representa as linhas de atuação em HTML */
	private HtmlDataTable dataTableLinhas = new HtmlDataTable();

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
	 * Lista todas os membros da comissão de avaliação dos projetos.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s).</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<SelectItem> getComissoesCombo() {
		Collection<SelectItem> membroscomissao = new ArrayList<SelectItem>();
		membroscomissao.add(new SelectItem(MembroComissao.MEMBRO_COMISSAO_MONITORIA, "COMISSÃO DE MONITORIA"));
		membroscomissao.add(new SelectItem(MembroComissao.MEMBRO_COMISSAO_CIENTIFICA, "COMISSÃO CIENTÍFICA"));
		membroscomissao.add(new SelectItem(MembroComissao.MEMBRO_COMISSAO_PESQUISA,"COMISSÃO DE PESQUISA"));
		membroscomissao.add(new SelectItem(MembroComissao.MEMBRO_COMISSAO_EXTENSAO, "COMITÊ DE EXTENSÃO"));
		membroscomissao.add(new SelectItem(MembroComissao.MEMBRO_COMISSAO_INTEGRADA, "COMISSÃO INTEGRADA DE ENSINO, PESQUISA E EXTENSÃO"));
		membroscomissao.add(new SelectItem(MembroComissao.MEMBRO_COMISSAO_POS_GRADUACAO, "COMISSÃO DE PÓS-GRADUAÇÃO"));
		return membroscomissao;
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
	    membroComissao.setPapel(0);
	    novaLinha = new EditalExtensaoLinhaAtuacao();
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
	    membroComissao.setPapel(0);
	    novaLinha = new EditalExtensaoLinhaAtuacao();
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
	 * Adiciona uma linha a lista de linhas de atuação do edital.
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/EditalExtensao/form.jsp
	 * 
	 * @return
	 */
	public String adicionarLinha() {
		ListaMensagens mensagens = new ListaMensagens();
		novaLinha.setEditalExtensao(obj);
		novaLinha.addPapeisMembrosComissao(novaLinha.getMembrosComissao());
		mensagens = novaLinha.validate();
		
		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
		}
		if(!obj.getLinhasAtuacao().contains(novaLinha))
			obj.getLinhasAtuacao().add(novaLinha);
		else
			addMensagemErro("Linha de atuação \""+novaLinha.getDescricao()+"\" já está cadastrada no edital.");

		novaLinha = new EditalExtensaoLinhaAtuacao();
		membroComissao = new MembroComissao();
		return null;
	}
	
	/**
	 * Adiciona um novo membro a comissão de avaliação da linha de atuação.
	 * 
	  * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/EditalExtensao/form.jsp</li>
	 * </ul>
	 * 
	 */
	public void adicionarMembroComissao(){
		if(membroComissao.getPapel() == 0){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Membro da comissão");
		}else{
			boolean contem = false;
			for(MembroComissao m:novaLinha.getMembrosComissao())
				if(m.getPapel() == membroComissao.getPapel()){
					contem = true;
					break;
				}
			if(!contem)
				novaLinha.getMembrosComissao().add(membroComissao);
			else
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Membro da comissão");
			
			membroComissao = new MembroComissao();
			membroComissao.setPapel(0);
		}
	}
	
	/**
	 * Remove um membro da comissão de avaliação da linha de atuação.
	 * 
	  * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/EditalExtensao/form.jsp</li>
	 * </ul>
	 * 
	 */
	public void removerMembroComissao(){
		boolean removeu = false;
		int papel = getParameterInt("papelMembroComissao",-1);
		for(Iterator<MembroComissao> it = novaLinha.getMembrosComissao().iterator();it.hasNext();){
			MembroComissao membro = it.next();
			if (membro.getPapel() == papel){
				it.remove();
				removeu = true;
				break;
			}
		}
		if(!removeu){
			addMensagemErro("Atenção! Esta operação foi concluída anteriormente. Por favor, reinicie o processo.");
			cancelar();
		}
	}
	
	/**
	 * Método utilizado para remover uma linha de atuação do edital
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/EditalExtensao/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String removerLinha() {	    
	    EditalExtensaoLinhaAtuacao linhaRemovida = (EditalExtensaoLinhaAtuacao) getDataTableLinhas().getRowData();
	    if (linhaRemovida.getId() != 0) {
	    	linhaRemovida.setAtivo(false);
	    }else {
	    	obj.getLinhasAtuacao().remove(linhaRemovida);
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
	public void removerRegra() {	    
	    boolean removeu = false;
		int tipoAcao = getParameterInt("tipoAcao",-1);
		for(Iterator<EditalExtensaoRegra> it = obj.getRegras().iterator();it.hasNext();){
			EditalExtensaoRegra regra = it.next();
			if (regra.getTipoAtividadeExtensao().getId() == tipoAcao){
				if(regra.getId() > 0 && regra.isAtivo()){
					regra.setAtivo(false);
					removeu = true;
					break;
				}else if(regra.getId() == 0){
					it.remove();
					removeu = true;
					break;					
				}
					
			}
		}
		if(!removeu){
			addMensagemErro("Atenção! Esta operação foi concluída anteriormente. Por favor, reinicie o processo.");
			cancelar();
		}
	}
	
	public EditalExtensaoRegra getNovaRegra() {
	    return novaRegra;
	}

	public void setNovaRegra(EditalExtensaoRegra novaRegra) {
	    this.novaRegra = novaRegra;
	}

	public EditalExtensaoLinhaAtuacao getNovaLinha() {
		return novaLinha;
	}

	public void setNovaLinha(EditalExtensaoLinhaAtuacao novaLinha) {
		this.novaLinha = novaLinha;
	}

	public HtmlDataTable getDataTableLinhas() {
		return dataTableLinhas;
	}

	public void setDataTableLinhas(HtmlDataTable dataTableLinhas) {
		this.dataTableLinhas = dataTableLinhas;
	}

	public MembroComissao getMembroComissao() {
		return membroComissao;
	}

	public void setMembroComissao(MembroComissao membroComissao) {
		this.membroComissao = membroComissao;
	}

}
