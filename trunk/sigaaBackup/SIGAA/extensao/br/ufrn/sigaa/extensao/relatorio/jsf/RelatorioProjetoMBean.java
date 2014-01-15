/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/11/2007
 *
 */
package br.ufrn.sigaa.extensao.relatorio.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
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
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.extensao.ParticipanteAcaoExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.RelatorioAcaoExtensaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dao.AndamentoObjetivoDao;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoCursoEventoExtensao;
import br.ufrn.sigaa.extensao.jsf.ConstantesNavegacao;
import br.ufrn.sigaa.extensao.negocio.RelatorioExtensaoValidator;
import br.ufrn.sigaa.extensao.relatorio.dominio.ArquivoRelatorioAcaoExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.DetalhamentoRecursos;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioAcaoExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioProjetoExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.TipoRelatorioExtensao;
import br.ufrn.sigaa.parametros.dominio.ParametrosExtensao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * MBean para a geração de relatórios relacionados a projetos de extensão.
 * 
 * @author Victor Hugo
 * @author ilueny santos
 * 
 */
@Scope("session")
@Component("relatorioProjeto")
public class RelatorioProjetoMBean extends SigaaAbstractController<RelatorioProjetoExtensao> {

	/** Utilizado para armazenar informações de uma consulta ao banco */	
	private Collection<TipoCursoEventoExtensao> outrasAcoes = new HashSet<TipoCursoEventoExtensao>();

	/** Utilizado para armazenar informações de uma consulta ao banco */
	private Collection<TipoCursoEventoExtensao> apresentacoes = new HashSet<TipoCursoEventoExtensao>();

	/** Utilizado para armazenar informações de uma consulta ao banco */
	private Collection<TipoProducao> producoesGeradas = new HashSet<TipoProducao>();

	/**  Utilizado para o envio de arquivos (documentos importantes do relatório) */ 
	private UploadedFile file;

	/** Descrição de arquivo anexado ao relatório */
	private String descricaoArquivo;	

	/** Data limite para a exibição do formulário antigo */
	private static Date dateLimite;

	public RelatorioProjetoMBean() {
		obj = new RelatorioProjetoExtensao();
		dateLimite = ParametroHelper.getInstance()
			.getParametroDate(ParametrosExtensao.DATA_LIMITE_ALTERACAO_CH_EQUIPE_EXECUTORA);
	}

	/**
	 * Inicia o caso de uso de submeter relatório, popula os dados necessários
	 * em sessão.
	 *
	 * Método não invocado por JSP´s.
	 * 
	 * @return
	 * @throws ArqException
	 */
	public void preAdicionarRelatorio(AtividadeExtensao atividade, Date dataLimite) throws ArqException {

		if (!getAcessoMenu().isCoordenadorExtensao())
			throw new SegurancaException("Usuário não autorizado a realizar esta operação");

		Integer idAtividade = getParameterInt("id", 0);
		RelatorioAcaoExtensaoMBean mBean = getMBean("relatorioAcaoExtensao");
		TipoRelatorioExtensao tipoRelatorio = mBean.carregarTipoRelatorio();

		obj = new RelatorioProjetoExtensao();
		mBean.carregarObjetivos(atividade, dataLimite, obj);
		
		ListaMensagens lista = new ListaMensagens();
		if ((idAtividade != null) && (idAtividade > 0)) {
			RelatorioExtensaoValidator.validaNovoRelatorio(idAtividade, tipoRelatorio.getId(), lista);
		}

		if (!lista.isEmpty()) {
			addMensagens(lista);
			return;
		}
		
	}

	/**
	 * Coordenador do projeto escolhe um relatório para alteração.
	 * 
	 * Método não invocado por JSP´s.
	 * 
	 * @return
	 * @throws ArqException 
	 * 
	 */
	public void preAlterarRelatorio(AtividadeExtensao atividade, Date dataLimite) throws ArqException {

		if (!getAcessoMenu().isCoordenadorExtensao())
			throw new SegurancaException("Usuário não autorizado a realizar esta operação");

		AndamentoObjetivoDao dao = getDAO(AndamentoObjetivoDao.class);
		ParticipanteAcaoExtensaoDao participanteDao = getDAO(ParticipanteAcaoExtensaoDao.class);
		try {
			obj = dao.findByPrimaryKey(getParameterInt("idRelatorio"),
					RelatorioProjetoExtensao.class);
			obj.setRegistroEntradaCadastro(getUsuarioLogado()
					.getRegistroEntrada());
			obj.getDetalhamentoRecursos().iterator();
			
			RelatorioAcaoExtensaoMBean mBean = getMBean("relatorioAcaoExtensao");
			mBean.carregarObjetivos(atividade, dataLimite, obj);
			
			prepareMovimento(SigaaListaComando.ENVIAR_RELATORIO_PROJETO_EXTENSAO);
		} finally {
			dao.close();
			participanteDao.close();
		}
		setReadOnly(false);
		setConfirmButton("Enviar Relatório");
	}

	/**
	 * Coordenador do projeto escolhe um relatório para alteração.
	 * 
	 * Método não invocado por JSP´s.
	 * 
	 * @return
	 * @throws SegurancaException
	 * 
	 */
	public String preRemoverRelatorio() throws SegurancaException {

		if (!getAcessoMenu().isCoordenadorExtensao())
			throw new SegurancaException("Usuário não autorizado a realizar esta operação");

		GenericDAO dao = getGenericDAO();
		try {
			obj = dao.findByPrimaryKey(getParameterInt("idRelatorio"),
					RelatorioProjetoExtensao.class);
			obj.setRegistroEntradaCadastro(getUsuarioLogado()
					.getRegistroEntrada());
			obj.setAtivo(false); // Removendo o relatório
			prepareMovimento(SigaaListaComando.REMOVER_RELATORIO_ACAO_EXTENSAO);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}
		setReadOnly(true);
		setConfirmButton("Remover Relatório");
		return forward(ConstantesNavegacao.RELATORIOPROJETO_DELETE);
	}

	/**
	 * Lista todos os tipos de curso e eventos possíveis.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioProjeto/form.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<TipoCursoEventoExtensao> getApresentacoes()
	throws DAOException {

		GenericDAO dao = getGenericDAO();
		apresentacoes = dao.findAll(TipoCursoEventoExtensao.class);
		if (obj.getApresentacaoProjetos().size() > 0) {
			ArrayList<Integer> tipos = new ArrayList<Integer>();
			for (TipoCursoEventoExtensao tipo : obj.getApresentacaoProjetos()) {
				tipos.add(tipo.getId());
			}
			for (TipoCursoEventoExtensao tipoEvento : apresentacoes) {
				if (tipos.contains(tipoEvento.getId())) {
					tipoEvento.setSelecionado(true);
				}
			}
		}
		return apresentacoes;
	}

	public void setApresentacoes(Collection<TipoCursoEventoExtensao> apresentacoes) {
		this.apresentacoes = apresentacoes;
	}

	/**
	 * Lista todos os tipos de curso e eventos possíveis.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioProjeto/form.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<TipoCursoEventoExtensao> getOutrasAcoes()
	throws DAOException {

		GenericDAO dao = getGenericDAO();
		try {
			outrasAcoes = dao.findAll(TipoCursoEventoExtensao.class);
			if (obj.getOutrasAcoesProjeto().size() > 0) {
				ArrayList<Integer> tipos = new ArrayList<Integer>();
				for (TipoCursoEventoExtensao tipo : obj.getOutrasAcoesProjeto()) {
					tipos.add(tipo.getId());
				}
				for (TipoCursoEventoExtensao tipoEvento : outrasAcoes) {
					if (tipos.contains(tipoEvento.getId())) {
						tipoEvento.setSelecionado(true);
					}
				}
			}
		} finally {
			dao.close();
		}
		return outrasAcoes;
	}

	public void setOutrasAcoes(Collection<TipoCursoEventoExtensao> outrasAcoes) {
		this.outrasAcoes = outrasAcoes;
	}

	/**
	 * 
	 * Lista todas as produções possíveis de TipoProducao.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioProjeto/form.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<TipoProducao> getProducoesGeradas() throws DAOException {
		GenericDAO dao = getGenericDAO();
		producoesGeradas = dao.findAll(TipoProducao.class);

		if (obj.getOutrasAcoesProjeto().size() > 0) {

			ArrayList<Integer> tipos = new ArrayList<Integer>();
			for (TipoProducao tipo : obj.getProducoesGeradas()) {
				tipos.add(tipo.getId());
			}

			for (TipoProducao tipoProducao : producoesGeradas) {
				if (tipos.contains(tipoProducao.getId())) {
					tipoProducao.setSelecionado(true);
				}
			}
		}
		return producoesGeradas;
	}

	public void setProducoesGeradas(Collection<TipoProducao> producoesGeradas) {
		this.producoesGeradas = producoesGeradas;
	}

	/**
	 * Método para salvar o relatório de ação de extensão utilizado no cadastro
	 * do relatório e quando o coordenador, a partir do relatório, decide
	 * alterar a lista de participantes do projeto. Neste ponto é realizado 
	 * um salvamento do relatório antes de ir para tela de cadastro de
	 * participantes.
	 * 
	 * Método não invocado por JSP´s.
	 * 
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public RelatorioAcaoExtensao submeter(Comando operacaoSalvarEnviar)
	throws ArqException, NegocioException {

		if (!getAcessoMenu().isCoordenadorExtensao())
			throw new SegurancaException(
			"Usuário não autorizado a realizar esta operação");
		
		Date dateLimite = ParametroHelper.getInstance()
				.getParametroDate(ParametrosExtensao.DATA_LIMITE_ALTERACAO_CH_EQUIPE_EXECUTORA);
				
		if ( obj.getAtividade().getProjeto().getDataCadastro().before(dateLimite) ) {
			
			// pegando as outras ações selecionadas
			Collection<TipoCursoEventoExtensao> acoesTemp = new HashSet<TipoCursoEventoExtensao>(); // utilizado
			// apenas para validação
			for (TipoCursoEventoExtensao acao : outrasAcoes) {
				if (acao.isSelecionado())
					acoesTemp.add(acao);
			}

			// pegando as outras ações selecionadas
			Collection<TipoCursoEventoExtensao> apresentacoesTemp = new HashSet<TipoCursoEventoExtensao>(); // utilizado
			// apenas para validação
			for (TipoCursoEventoExtensao apresentacao : apresentacoes) {
				if (apresentacao.isSelecionado())
					apresentacoesTemp.add(apresentacao);
			}

			// pegando as outras ações selecionadas
			Collection<TipoProducao> producoesTemp = new HashSet<TipoProducao>(); // utilizado
			// apenas para validação
			for (TipoProducao producao : producoesGeradas) {
				if (producao.isSelecionado())
					producoesTemp.add(producao);
			}

			obj.setOutrasAcoesProjeto(acoesTemp);
			obj.setApresentacaoProjetos(apresentacoesTemp);
			obj.setProducoesGeradas(producoesTemp);
			
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
	 * /extensao/RelatorioProjeto/form.jsp
	 * 
	 * @return
	 * @throws ArqException, NegocioException
	 * 
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
	 * Enviar o relatório do projeto de extensão.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioProjeto/form.jsp
	 * 
	 * @return
	 * @throws SegurancaException, ArqException, NegocioException
	 * 
	 */
	public String enviar() throws SegurancaException, ArqException,
	NegocioException {

		// VALIDAÇAO
		ListaMensagens lista = new ListaMensagens();
		
		RelatorioExtensaoValidator.validaDadosGeraisRelatorioAcaoExtensao(obj, lista);		

		if (!lista.isEmpty()) {
			addMensagens(lista);
			return null;
		}
		try {
			obj.setDataEnvio(new Date());
			submeter(SigaaListaComando.ENVIAR_RELATORIO_PROJETO_EXTENSAO);
			addMessage("Operação realizada com sucesso!", TipoMensagemUFRN.INFORMATION);
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
	 * Remove o relatório do projeto de extensão.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioProjeto/form.jsp
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
		
		return redirectJSF(getSubSistema().getLink());
	}

	/**
	 * Retorna a lista de todos os arquivos (documentos) anexados ao relatório.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /extensao/RelatorioProjeto/form.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<ArquivoRelatorioAcaoExtensao> getArquivosRelatorio()
	throws DAOException {
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
	 * Visualizar o arquivo anexo ao relatório.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *  /extensao/AutorizacaoDepartamento/form_relatorio_curso_evento.jsp
	 *  /extensao/AutorizacaoDepartamento/form_relatorio_projeto.jsp
	 *  /extensao/RelatorioProjeto/form.jsp
	 *  /extensao/RelatorioProjeto/view.jsp
	 *  /extensao/ValidacaoRelatorioProex/form_projeto.jsp
	 *  
	 */
	public String viewArquivo() {
		try {
			int idArquivo = getParameterInt("idArquivo");
			EnvioArquivoHelper.recuperaArquivo(getCurrentResponse(), idArquivo, false);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(" Arquivo não encontrado!");
		}
		FacesContext.getCurrentInstance().responseComplete();
		return null;
	}
	
	/**
	 * Importa dados do relatório parcial.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/extensao/RelatorioProjeto/form.jsp</li>
	 * </ul>
	 * @throws DAOException
	 */
	public String importarDadosRelatorioParcial() throws DAOException{
		RelatorioAcaoExtensaoDao dao = getDAO(RelatorioAcaoExtensaoDao.class);
		AndamentoObjetivoDao andamentoDao = getDAO(AndamentoObjetivoDao.class);
		try {
			Collection<RelatorioAcaoExtensao> relatorios = dao.findByAtividadeTipoRelatorio(obj.getAtividade().getId(), TipoRelatorioExtensao.RELATORIO_PARCIAL);
			if (ValidatorUtil.isNotEmpty(relatorios)) {
				
				RelatorioProjetoExtensao relatorioParcial = dao.findByPrimaryKey(relatorios.iterator().next().getId(), RelatorioProjetoExtensao.class);
				
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
				obj.setRelacaoPropostaCurso(relatorioParcial.getRelacaoPropostaCurso());
				obj.setAtividadesRealizadas(relatorioParcial.getAtividadesRealizadas());
				obj.setResultadosQualitativos(relatorioParcial.getResultadosQualitativos());
				obj.setResultadosQuantitativos(relatorioParcial.getResultadosQuantitativos());
				obj.setDificuldadesEncontradas(relatorioParcial.getDificuldadesEncontradas());
				obj.setAjustesDuranteExecucao(relatorioParcial.getAjustesDuranteExecucao());
				obj.setPublicoRealAtingido(relatorioParcial.getPublicoRealAtingido());
				obj.setAnoContrato(relatorioParcial.getAnoContrato());
				obj.setAnoConvenio(relatorioParcial.getAnoConvenio());
				obj.setNumeroContrato(relatorioParcial.getNumeroContrato());
				obj.setNumeroConvenio(relatorioParcial.getNumeroConvenio());
				
				obj.setObservacoesGerais(relatorioParcial.getObservacoesGerais());
				obj.setApresentacaoEventoCientifico(relatorioParcial.getApresentacaoEventoCientifico());
				obj.setObservacaoApresentacao(relatorioParcial.getObservacaoApresentacao());
				obj.setArtigosEventoCientifico( relatorioParcial.getArtigosEventoCientifico() );
				obj.setObservacaoArtigo( relatorioParcial.getObservacaoArtigo() );
				obj.setProducoesCientifico( relatorioParcial.getProducoesCientifico() );
				obj.setObservacaoProducao( relatorioParcial.getObservacaoProducao() );
				obj.setAcaoRealizada( relatorioParcial.getAcaoRealizada() );
				
				obj.setAndamento(andamentoDao.findAndamentoAtividades(obj.getAtividade().getId(), TipoRelatorioExtensao.RELATORIO_PARCIAL, true));
			
				addMensagemInformation("O relatório Parcial foi importado com sucesso.");
			}
			
		} finally {
			dao.close();
			andamentoDao.close();
		}
			
		return redirectMesmaPagina();
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
		finally
		{
			dao.close();
		}
	}

	/** Indica se deve ser exibido o botão de importar no relatório final */
	public boolean isExibeBotaoImportar() throws DAOException {
		return isExisteRelatorioParcial() && isRelatorioFinal() && obj.getId() == 0;
	}
	
	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
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
	 * 	<li>sigaa.war/extensao/RelatorioProjeto/form.jsp</li>
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
