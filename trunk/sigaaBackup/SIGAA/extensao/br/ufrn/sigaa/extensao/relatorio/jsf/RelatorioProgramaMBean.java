/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '08/06/2011'
 *
 */
package br.ufrn.sigaa.extensao.relatorio.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.faces.context.FacesContext;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.extensao.RelatorioAcaoExtensaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.OrcamentoConsolidado;
import br.ufrn.sigaa.extensao.jsf.ConstantesNavegacao;
import br.ufrn.sigaa.extensao.negocio.RelatorioExtensaoValidator;
import br.ufrn.sigaa.extensao.relatorio.dominio.ArquivoRelatorioAcaoExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.DetalhamentoRecursos;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioAcaoExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioProgramaExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioProjetoExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.TipoRelatorioExtensao;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;

/**
 * Mbean responsável por controlar o cadastro de relatórios de programas de extensão
 * 
 * @author Ilueny Santos
 * 
 */
@Scope("session")
@Component("relatorioProgramaMBean")
public class RelatorioProgramaMBean  extends SigaaAbstractController<RelatorioProgramaExtensao> {

	/** Utilizado para o envio de arquivos (documentos importantes do relatório) */
	private UploadedFile file;

	/** Descrição do arquivo anexado ao relatório */
	private String descricaoArquivo;


	/** Construtor padrão. */
	public RelatorioProgramaMBean() {
		obj = new RelatorioProgramaExtensao();
	}

	/**
	 * Inicia o caso de uso de submeter relatório, popula objeto com os dados necessários. 
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioPrograma/lista.jsp
	 *  
	 * @return
	 * @throws ArqException
	 */
	public void preAdicionarRelatorio(AtividadeExtensao atividade, Date dataLimite) throws ArqException {
		if (!getAcessoMenu().isCoordenadorExtensao()) {
			throw new SegurancaException("Usuário não autorizado a realizar esta operação");
		}

		Integer idAtividade = getParameterInt("id", 0);
		RelatorioAcaoExtensaoMBean mBean = getMBean("relatorioAcaoExtensao");
		TipoRelatorioExtensao tipoRelatorio = mBean.carregarTipoRelatorio();

		obj = new RelatorioProgramaExtensao();
		mBean.carregarObjetivos(atividade, dataLimite, obj);

		ListaMensagens lista = new ListaMensagens();
		if ((idAtividade != null) && (idAtividade > 0)) {
			RelatorioExtensaoValidator.validaNovoRelatorio(idAtividade, tipoRelatorio.getId(), lista);
		}

		if (!lista.isEmpty()) {
			addMensagens(lista);
			return;
		}

		prepareMovimento(SigaaListaComando.ENVIAR_RELATORIO_PROGRAMA_EXTENSAO);
	}

	/**
	 * Coordenador do projeto escolhe um relatório para alteração.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioCursoEvento/lista.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 * 
	 */
	public void preAlterarRelatorio(AtividadeExtensao atividade, Date dataLimite) throws SegurancaException {
		if (!getAcessoMenu().isCoordenadorExtensao()) {
			throw new SegurancaException("Usuário não autorizado a realizar esta operação.");
		}

		GenericDAO dao = getGenericDAO();
		try {
			obj = dao.findByPrimaryKey(getParameterInt("idRelatorio"),	RelatorioProgramaExtensao.class);
			obj.setRegistroEntradaCadastro(getUsuarioLogado().getRegistroEntrada());
			obj.getDetalhamentoRecursos().iterator();
			RelatorioAcaoExtensaoMBean mBean = getMBean("relatorioAcaoExtensao");
			mBean.carregarObjetivos(atividade, dataLimite, obj);
			prepareMovimento(SigaaListaComando.ENVIAR_RELATORIO_PROGRAMA_EXTENSAO);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return;
		}

		setReadOnly(false);
		setConfirmButton("Enviar Relatório");
	}

	/**
	 * Coordenador do projeto escolhe um relatório para cancelar.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioPrograma/lista.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 * 
	 */
	public String preRemoverRelatorio() throws SegurancaException {
		if (!getAcessoMenu().isCoordenadorExtensao()) {
			throw new SegurancaException("Usuário não autorizado a realizar esta operação.");
		}

		GenericDAO dao = getGenericDAO();
		try {

			obj = dao.findByPrimaryKey(getParameterInt("idRelatorio"),RelatorioProgramaExtensao.class);
			obj.setRegistroEntradaCadastro(getUsuarioLogado().getRegistroEntrada());
			obj.setAtivo(false); // cancelando o relatório
			prepareMovimento(SigaaListaComando.REMOVER_RELATORIO_ACAO_EXTENSAO);

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}

		setReadOnly(true);
		setConfirmButton("Remover Relatório");

		return forward(ConstantesNavegacao.RELATORIO_PROGRAMA_DELETE);

	}

	/**
	 * Método usado para realizar submissão de relatórios de Programas.
	 *
	 * Método não invocado por JSP´s.
	 * 
	 * @return
	 * @throws ArqException, NegocioException
	 */
	public RelatorioAcaoExtensao submeter(Comando operacaoSalvarEnviar) throws ArqException, NegocioException {
		if (!getAcessoMenu().isCoordenadorExtensao()) {
			throw new SegurancaException("Usuário não autorizado a realizar esta operação");
		}

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(operacaoSalvarEnviar);

		return (RelatorioAcaoExtensao) execute(mov, getCurrentRequest());
	}

	/**
	 * Prepara o MBean (participanteAcaoExtensao) e o processador que irá
	 * realizar o cadastro do participante. Valida para somente coordenadores de
	 * ações realizarem esta operação. Redireciona o usuário para o form de
	 * cadastro.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioPrograma/form.jsp
	 * 
	 * @return
	 * @throws ArqException, NegocioException
	 */
	public String salvar() throws ArqException, NegocioException {

		prepareMovimento(SigaaListaComando.SALVAR_RELATORIO_ACAO_EXTENSAO);

		// VALIDAÇAO
		ListaMensagens lista = new ListaMensagens();
		
		RelatorioExtensaoValidator.validaDadosGeraisRelatorioAcaoExtensao(obj, lista);		

		if (!lista.isEmpty()) {
			addMensagens(lista);
			return null;
		}
		
		// Salvando o rascunho do relatório
		try {

			obj.setDataEnvio(null);
			submeter(SigaaListaComando.SALVAR_RELATORIO_ACAO_EXTENSAO);

		} catch (NegocioException e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}
		
		RelatorioAcaoExtensaoMBean mbean = ((RelatorioAcaoExtensaoMBean)getMBean("relatorioAcaoExtensao"));
		if(mbean.isTelaNotificacoes()) {
			mbean.setAtividadesPendentesRelatoriosCoordenador(getDAO(RelatorioAcaoExtensaoDao.class).findAcosPendentesRelatorio(getUsuarioLogado().getPessoa()));
			if( ValidatorUtil.isEmpty(mbean.getAtividadesPendentesRelatoriosCoordenador())){				
				return redirect("/sigaa/verPortalDocente.do");
			} else {
				return redirect("/pendenciaRelatoriosExtensao.jsf");
			}
		}
		return mbean.iniciarCadastroRelatorio();

	}

	/**
	 * Método usado para fazer o envio de um relatório (Programa).
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioPrograma/form.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String enviar() throws ArqException {

		try {

			// VALIDAÇÃO
			ListaMensagens lista = new ListaMensagens();
			RelatorioExtensaoValidator.validaDadosGeraisRelatorioAcaoExtensao(obj, lista);			

			if (!lista.isEmpty()) {
				addMensagens(lista);
				return null;
			}

			obj.setDataEnvio(new Date());
			submeter(SigaaListaComando.ENVIAR_RELATORIO_PROGRAMA_EXTENSAO);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);

		} catch (NegocioException e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}

		RelatorioAcaoExtensaoMBean mbean = ((RelatorioAcaoExtensaoMBean)getMBean("relatorioAcaoExtensao"));
		if(mbean.isTelaNotificacoes()) {
			mbean.setAtividadesPendentesRelatoriosCoordenador(getDAO(RelatorioAcaoExtensaoDao.class).findAcosPendentesRelatorio(getUsuarioLogado().getPessoa()));
			if( ValidatorUtil.isEmpty(mbean.getAtividadesPendentesRelatoriosCoordenador())){				
				return redirect("/sigaa/verPortalDocente.do");
			} else {
				return redirect("/pendenciaRelatoriosExtensao.jsf");
			}
		}
		return mbean.iniciarCadastroRelatorio();

	}

	/**
	 * Remover o relatório de Programa de extensão
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioPrograma/form.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String removerRelatorio() throws NegocioException, ArqException {
		if (getParameterInt("idRelatorio") != null){
			obj.setId(getParameterInt("idRelatorio"));
		}
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(SigaaListaComando.REMOVER_RELATORIO_ACAO_EXTENSAO);
		prepareMovimento(SigaaListaComando.REMOVER_RELATORIO_ACAO_EXTENSAO);
		execute(mov, getCurrentRequest());
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		
		RelatorioAcaoExtensaoMBean mbean = ((RelatorioAcaoExtensaoMBean)getMBean("relatorioAcaoExtensao"));
		if(mbean.isTelaNotificacoes()) {
			mbean.setAtividadesPendentesRelatoriosCoordenador(getDAO(RelatorioAcaoExtensaoDao.class).findAcosPendentesRelatorio(getUsuarioLogado().getPessoa()));
			return redirect("/pendenciaRelatoriosExtensao.jsf");			
		}	
		
		// Atualiza a view
		Collection<RelatorioAcaoExtensao> c = mbean.getRelatoriosLocalizados();
		for (Iterator<RelatorioAcaoExtensao> it = c.iterator(); it.hasNext();) {
			if (it.next().getId() == obj.getId()) {
				it.remove();
				mbean.setRelatoriosLocalizados(c);
				break;
			}
		}

		return redirect("/extensao/RelatorioAcaoExtensao/busca.jsf");
		// return mbean.iniciarCadastroRelatorio();
	}


	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	/**
	 * Retorna a lista de todos os arquivos (documentos) anexados ao relatório.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<ArquivoRelatorioAcaoExtensao> getArquivosRelatorio() throws DAOException {
		return obj.getArquivos();
	}

	/**
	 * Adiciona um arquivo ao relatório.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioProjeto/form.jsp
	 * 
	 * @return
	 */
	public String anexarArquivo() {

		try {
			if ((descricaoArquivo == null) || ("".equals(descricaoArquivo.trim())))
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Descrição");
			if ((file == null))
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Arquivo");
			
			if ( hasErrors() )
				return null;
			
			int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
			EnvioArquivoHelper.inserirArquivo(idArquivo, file.getBytes(), file
					.getContentType(), file.getName());
			ArquivoRelatorioAcaoExtensao arquivo = new ArquivoRelatorioAcaoExtensao();
			arquivo.setDescricao(descricaoArquivo);
			arquivo.setIdArquivo(idArquivo);
			arquivo.setAtivo(true);
			obj.addArquivo(arquivo);

			addMessage("Arquivo Anexado com Sucesso!", TipoMensagemUFRN.INFORMATION);
			descricaoArquivo = "";
			
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}
		return null;
	}

	/**
	 * Remove o arquivo da lista de anexos do relatório.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioProjeto/form.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String removeAnexo() throws ArqException {
		ArquivoRelatorioAcaoExtensao arquivo = new ArquivoRelatorioAcaoExtensao();

		arquivo.setIdArquivo(Integer.parseInt(getParameter("idArquivo")));
		arquivo.setId(Integer.parseInt(getParameter("idArquivoRelatorio")));

		//Remove do banco de extensão
		if (arquivo.getId() != 0){

			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(arquivo);
			Comando ultimoComando = getUltimoComando();

			try {
				prepareMovimento(ArqListaComando.REMOVER);
				mov.setCodMovimento(ArqListaComando.REMOVER);

				execute(mov, getCurrentRequest());
				prepareMovimento(ultimoComando);

			} catch (Exception e) {
				notifyError(e);
				addMensagemErroPadrao();
			}
		}

		// Remove do banco de arquivos
		EnvioArquivoHelper.removeArquivo(arquivo.getIdArquivo());
		addMessage("Remoção realizada com sucesso!", TipoMensagemUFRN.INFORMATION);
		
		// Remove da view
		if (obj.getArquivos() != null)
			obj.getArquivos().remove(arquivo);

		return null;
	}

	/**
	 * Visualizar o arquivo anexo ao relatório
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioPrograma/form.jsp
	 * /extensao/RelatorioPrograma/view.jsp
	 * 
	 * @param event
	 */
	public void viewArquivo() {
		try {
			int idArquivo = getParameterInt("idArquivo");
			EnvioArquivoHelper.recuperaArquivo(getCurrentResponse(), idArquivo, false);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(" Arquivo não encontrado!");
		}
		FacesContext.getCurrentInstance().responseComplete();
	}
	
	/**
	 * Importa dados do relatório parcial.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/extensao/RelatorioPrograma/form.jsp</li>
	 * </ul>
	 * @throws DAOException
	 */
	public String importarDadosRelatorioParcial() throws DAOException{
		RelatorioAcaoExtensaoDao dao = getDAO(RelatorioAcaoExtensaoDao.class);
		Collection<RelatorioAcaoExtensao> relatorios = dao.findByAtividadeTipoRelatorio(obj.getAtividade().getId(), TipoRelatorioExtensao.RELATORIO_PARCIAL);
		if (ValidatorUtil.isAllNotEmpty(relatorios)) {
			
			RelatorioProgramaExtensao relatorioParcial = dao.findByPrimaryKey(relatorios.iterator().next().getId(), RelatorioProgramaExtensao.class);
			
			if (ValidatorUtil.isNotEmpty(relatorioParcial.getDetalhamentoRecursos())) {				
				obj.setDetalhamentoRecursos(new ArrayList<DetalhamentoRecursos>());
				for (DetalhamentoRecursos detalheTemp : relatorioParcial.getDetalhamentoRecursos()) {
					DetalhamentoRecursos detalheImportadoParcial = new DetalhamentoRecursos();
					detalheImportadoParcial.setId(0);
					detalheImportadoParcial.setElemento(detalheTemp.getElemento());
					detalheImportadoParcial.setFaex(detalheTemp.getFaex());
					detalheImportadoParcial.setFunpec(detalheTemp.getFunpec());
					detalheImportadoParcial.setOutros(detalheTemp.getOutros());
					detalheImportadoParcial.setValor(detalheTemp.getValor());
					detalheImportadoParcial.setRelatorioAcaoExtensao(obj);
					obj.getDetalhamentoRecursos().add(detalheImportadoParcial);
				} 				
			}

			//OBS: Arquivos e outras coleção não foram importados para evitar problemas de 
			//atualização e e alterações acidentais no relatório parcial já enviado.
			//principalmente no caso dos arquivos.
			
			//importando dados simples..
			obj.setAtividadesRealizadas(relatorioParcial.getAtividadesRealizadas());
			obj.setResultadosQualitativos(relatorioParcial.getResultadosQualitativos());
			obj.setResultadosQuantitativos(relatorioParcial.getResultadosQuantitativos());
			obj.setDificuldadesEncontradas(relatorioParcial.getDificuldadesEncontradas());
			obj.setAjustesDuranteExecucao(relatorioParcial.getAjustesDuranteExecucao());
			obj.setPublicoRealAtingido(relatorioParcial.getPublicoRealAtingido());
			
		}
		
		return forward(ConstantesNavegacao.RELATORIO_PROGRAMA_FORM);
	}
	
	/**
	 * Verifica se o relatório para cadastro é um relatório final
	 * <br>
	 * Metódo chamado pelas seguintes JSP:
	 * <ul>
	 * 	<li>sigaa.war/extensao/RelatorioProjeto/form.jsp </li>
	 * </ul>
	 *  */
	public boolean isRelatorioFinal(){
		Boolean relatorioFinal = getParameterBoolean("relatorioFinal");
		if(relatorioFinal || obj.getTipoRelatorio().getId() == TipoRelatorioExtensao.RELATORIO_FINAL){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Verifica se há algum relatório parcial para ação selecionada
	 * <br>
	 * Metódo chamado pelas seguintes JSP:
	 * <ul>
	 * 	<li>sigaa.war/extensao/RelatorioProjeto/form.jsp </li>
	 * </ul>
	 * @throws DAOException 
	 *  */
	public boolean isExisteRelatorioParcial() throws DAOException{
		RelatorioAcaoExtensaoDao dao = getDAO(RelatorioAcaoExtensaoDao.class);
		try{
			Integer idAtividade = obj.getAtividade().getId();
			Collection<RelatorioAcaoExtensao> relatorios = dao.findByAtividadeTipoRelatorio(idAtividade, TipoRelatorioExtensao.RELATORIO_PARCIAL);

			if ((relatorios != null) && (!relatorios.isEmpty())) {
				return true;
			}else{
				return false;
			}
		}
		finally	{
			dao.close();
		}
	}

	public String getDescricaoArquivo() {
		return descricaoArquivo;
	}

	public void setDescricaoArquivo(String descricaoArquivo) {
		this.descricaoArquivo = descricaoArquivo;
	}
	
	/**
	 * Cancela o cadastro ou alteração do relatório e retorna para
	 * a tela de listas de relatórios.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/extensao/RelatorioPrograma/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	@Override
	public String cancelar() {
		try {
			resetBean();
			RelatorioAcaoExtensaoMBean mbean = ((RelatorioAcaoExtensaoMBean)getMBean("relatorioAcaoExtensao"));			
			if(mbean.isTelaNotificacoes()) {				
					return redirect("/pendenciaRelatoriosExtensao.jsf");				
			}			
			return mbean.iniciarCadastroRelatorio();
		} catch (Exception e) {
			tratamentoErroPadrao(e);
			return null;
		}
	}

	
}
