/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 01/08/2008
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
import br.ufrn.sigaa.arq.dao.extensao.ParticipanteAcaoExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.RelatorioAcaoExtensaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.OrcamentoConsolidado;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;
import br.ufrn.sigaa.extensao.jsf.ConstantesNavegacao;
import br.ufrn.sigaa.extensao.negocio.RelatorioExtensaoValidator;
import br.ufrn.sigaa.extensao.relatorio.dominio.ArquivoRelatorioAcaoExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.DetalhamentoRecursos;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioAcaoExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioCursoEvento;
import br.ufrn.sigaa.extensao.relatorio.dominio.TipoRelatorioExtensao;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;

/**
 * Mbean respons�vel por controlar o cadastro de relat�rios de cursos e eventos
 * de extens�o
 * 
 * @author Ilueny Santos
 * 
 */
@Scope("session")
@Component("relatorioCursoEvento")
public class RelatorioCursoEventoMBean extends
SigaaAbstractController<RelatorioCursoEvento> {

	/** Utilizado para o envio de arquivos (documentos importantes do relat�rio) */
	private UploadedFile file;

	/** Descri��o do arquivo anexado ao relat�rio */
	private String descricaoArquivo;


	public RelatorioCursoEventoMBean() {
		obj = new RelatorioCursoEvento();
	}

	/**
	 * Inicia o caso de uso de submeter relat�rio, popula os dados necess�rios
	 * em sess�o.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioCursoEvento/lista.jsp
	 *  
	 * @return
	 * @throws ArqException
	 */
	public void preAdicionarRelatorio(AtividadeExtensao atividade, Date dataLimite) throws ArqException {

		if (!getAcessoMenu().isCoordenadorExtensao())
			throw new SegurancaException("Usu�rio n�o autorizado a realizar esta opera��o");

		Integer idAtividade = getParameterInt("id", 0);
		RelatorioAcaoExtensaoMBean mBean = getMBean("relatorioAcaoExtensao");
		TipoRelatorioExtensao tipoRelatorio = mBean.carregarTipoRelatorio();

		ListaMensagens lista = new ListaMensagens();
		if ((idAtividade != null) && (idAtividade > 0)) {
			RelatorioExtensaoValidator.validaNovoRelatorio(idAtividade, tipoRelatorio.getId(), lista);
		}

		if (!lista.isEmpty()) {
			addMensagens(lista);
			return;
		}

		obj = new RelatorioCursoEvento();
		mBean.carregarObjetivos(atividade, dataLimite, obj);
		obj.setTaxaMatricula(0);
		obj.setNumeroConcluintes(0);
		obj.setTotalArrecadado(0);

		prepareMovimento(SigaaListaComando.ENVIAR_RELATORIO_CURSO_EVENTO_EXTENSAO);

		setReadOnly(false);
		setConfirmButton("Enviar Relat�rio");
	}

	/**
	 * Coordenador do projeto escolhe um relat�rio para altera��o.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioCursoEvento/lista.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 * 
	 */
	public void preAlterarRelatorio(AtividadeExtensao atividade, Date dataLimite) throws SegurancaException {
		if (!getAcessoMenu().isCoordenadorExtensao())
			throw new SegurancaException(
			"Usu�rio n�o autorizado a realizar esta opera��o");

		GenericDAO dao = getGenericDAO();
		try {

			obj = dao.findByPrimaryKey(getParameterInt("idRelatorio"),
					RelatorioCursoEvento.class);
			obj.setRegistroEntradaCadastro(getUsuarioLogado()
					.getRegistroEntrada());

			obj.getDetalhamentoRecursos().iterator();
			obj.getAtividade().setParticipantes(getDAO(ParticipanteAcaoExtensaoDao.class).findByAcao(obj.getAtividade().getId()));
			RelatorioAcaoExtensaoMBean mBean = getMBean("relatorioAcaoExtensao");
			mBean.carregarObjetivos(atividade, dataLimite, obj);
			
			prepareMovimento(SigaaListaComando.ENVIAR_RELATORIO_CURSO_EVENTO_EXTENSAO);

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return;
		}

		setReadOnly(false);
		setConfirmButton("Enviar Relat�rio");

	}

	/**
	 * Coordenador do projeto escolhe um relat�rio para cancelar.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioCursoEvento/lista.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 * 
	 */
	public String preRemoverRelatorio() throws SegurancaException {
		if (!getAcessoMenu().isCoordenadorExtensao())
			throw new SegurancaException(
			"Usu�rio n�o autorizado a realizar esta opera��o");

		GenericDAO dao = getGenericDAO();
		try {

			obj = dao.findByPrimaryKey(getParameterInt("idRelatorio"),RelatorioCursoEvento.class);
			obj.setRegistroEntradaCadastro(getUsuarioLogado().getRegistroEntrada());
			obj.setAtivo(false); // cancelando o relat�rio

			prepareMovimento(SigaaListaComando.REMOVER_RELATORIO_ACAO_EXTENSAO);

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}

		setReadOnly(true);
		setConfirmButton("Remover Relat�rio");

		return forward(ConstantesNavegacao.RELATORIOCURSOSEVENTOS_DELETE);

	}

	/**
	 * M�todo usado para realizar submiss�o de relat�rios de Cursos e Eventos.
	 *
	 * M�todo n�o invocado por JSP�s.
	 * 
	 * @return
	 * @throws ArqException, NegocioException
	 */
	public RelatorioAcaoExtensao submeter(Comando operacaoSalvarEnviar)
	throws ArqException, NegocioException {

		if (!getAcessoMenu().isCoordenadorExtensao())
			throw new SegurancaException(
			"Usu�rio n�o autorizado a realizar esta opera��o");

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(operacaoSalvarEnviar);

		return (RelatorioAcaoExtensao) execute(mov, getCurrentRequest());
	}

	/**
	 * Prepara o MBean (participanteAcaoExtensao) e o processador que ir�
	 * realizar o cadastro do participante. Valida para somente coordenadores de
	 * a��es realizarem esta opera��o. Redireciona o usu�rio para o form de
	 * cadastro.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioCursoEvento/form.jsp
	 * 
	 * @return
	 * @throws ArqException, NegocioException
	 */
	public String salvar() throws ArqException, NegocioException {

		prepareMovimento(SigaaListaComando.SALVAR_RELATORIO_ACAO_EXTENSAO);

		// VALIDA��O
		ListaMensagens lista = new ListaMensagens();
		RelatorioExtensaoValidator.validaDadosGeraisRelatorioAcaoExtensao(obj, lista);			

		if (!lista.isEmpty()) {
			addMensagens(lista);
			return null;
		}

		// Salvando o rascunho do relat�rio
		try {

			obj.setDataEnvio(null);
			submeter(SigaaListaComando.SALVAR_RELATORIO_ACAO_EXTENSAO);

		} catch (NegocioException e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
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
	 * M�todo usado para fazer o envio de um relat�rio (Curso e Evento).
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioCursoEvento/form.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String enviar() throws ArqException {

		try {

			// VALIDA��O
			ListaMensagens lista = new ListaMensagens();
			RelatorioExtensaoValidator.validaDadosGeraisRelatorioAcaoExtensao(obj, lista);			
			
			if (!lista.isEmpty()) {
				addMensagens(lista);
				return null;
			}

			obj.setDataEnvio(new Date());
			submeter(SigaaListaComando.ENVIAR_RELATORIO_CURSO_EVENTO_EXTENSAO);
			addMessage("Opera��o realizada com sucesso!", TipoMensagemUFRN.INFORMATION);

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
	 * Remover o relat�rio de projeto de extens�o
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioCursoEvento/form.jsp
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
//		return mbean.iniciarCadastroRelatorio();
	}


	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	/**
	 * Retorna a lista de todos os arquivos (documentos) anexados ao relat�rio.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<ArquivoRelatorioAcaoExtensao> getArquivosRelatorio()
	throws DAOException {
		return obj.getArquivos();
	}

	/**
	 * Adiciona um arquivo ao relat�rio.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioProjeto/form.jsp
	 * 
	 * @return
	 */
	public String anexarArquivo() {

		try {
			if ((descricaoArquivo == null) || ("".equals(descricaoArquivo.trim())))
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Descri��o");
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
	 * Remove o arquivo da lista de anexos do relat�rio.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioProjeto/form.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String removeAnexo() throws ArqException {
		ArquivoRelatorioAcaoExtensao arquivo = new ArquivoRelatorioAcaoExtensao();

		arquivo.setIdArquivo(Integer.parseInt(getParameter("idArquivo")));
		arquivo.setId(Integer.parseInt(getParameter("idArquivoRelatorio")));

		//Remove do banco de extens�o
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
		addMessage("Remo��o realizada com sucesso!", TipoMensagemUFRN.INFORMATION);
		
		// Remove da view
		if (obj.getArquivos() != null)
			obj.getArquivos().remove(arquivo);

		return null;
	}

	/**
	 * Visualizar o arquivo anexo ao relat�rio
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioCursoEvento/form.jsp
	 * /extensao/RelatorioCursoEvento/view.jsp
	 * 
	 * @param event
	 */
	public void viewArquivo() {
		try {
			int idArquivo = getParameterInt("idArquivo");
			EnvioArquivoHelper.recuperaArquivo(getCurrentResponse(), idArquivo, false);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(" Arquivo n�o encontrado!");
		}
		FacesContext.getCurrentInstance().responseComplete();
	}
	
	/**
	 * Importa dados do relat�rio parcial.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/extensao/RelatorioProjeto/form.jsp</li>
	 * </ul>
	 * @throws DAOException
	 */
	public String importarDadosRelatorioParcial() throws DAOException{
		RelatorioAcaoExtensaoDao dao = getDAO(RelatorioAcaoExtensaoDao.class);
		Collection<RelatorioAcaoExtensao> relatorios = dao.findByAtividadeTipoRelatorio(obj.getAtividade().getId(), TipoRelatorioExtensao.RELATORIO_PARCIAL);
		if ((relatorios != null) && (!relatorios.isEmpty())) {
			
			RelatorioCursoEvento relatorioParcial = dao.findByPrimaryKey(relatorios.iterator().next().getId(), RelatorioCursoEvento.class);
			
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

			//OBS: Arquivos e outras cole��o n�o foram importados para evitar problemas de 
			//atualiza��o e e altera��es acidentais no relat�rio parcial j� enviado.
			//principalmente no caso dos arquivos.
			
			//importando dados..
			obj.setAtividadesRealizadas(relatorioParcial.getAtividadesRealizadas());
			obj.setResultadosQualitativos(relatorioParcial.getResultadosQualitativos());
			obj.setResultadosQuantitativos(relatorioParcial.getResultadosQuantitativos());
			obj.setDificuldadesEncontradas(relatorioParcial.getDificuldadesEncontradas());
			obj.setAjustesDuranteExecucao(relatorioParcial.getAjustesDuranteExecucao());
			obj.setPublicoRealAtingido(relatorioParcial.getPublicoRealAtingido());
			obj.setNumeroConcluintes(relatorioParcial.getNumeroConcluintes());
			obj.setTotalArrecadado(relatorioParcial.getTotalArrecadado());
			obj.setTaxaMatricula(relatorioParcial.getTaxaMatricula());
			
		}
		
		return redirectMesmaPagina();
	}
	
	/**
	 * Verifica se o relat�rio para cadastro � um relat�rio final
	 * <br>
	 * Met�do chamado pelas seguintes JSP:
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
	 * Verifica se h� algum relat�rio parcial para a��o selecionada
	 * <br>
	 * Met�do chamado pelas seguintes JSP:
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
		finally
		{
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
	 * Cancela o cadastro ou altera��o do relat�rio e retorna para
	 * a tela de listas de relat�rios.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/extensao/RelatorioCursoEvento/form.jsp</li>
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
