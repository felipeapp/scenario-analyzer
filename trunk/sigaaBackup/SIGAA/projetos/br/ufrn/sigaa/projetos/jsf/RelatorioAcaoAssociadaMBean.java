/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/01/2011
 *
 */
package br.ufrn.sigaa.projetos.jsf;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.faces.context.FacesContext;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dominio.OrcamentoConsolidado;
import br.ufrn.sigaa.extensao.dominio.TipoCursoEventoExtensao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;
import br.ufrn.sigaa.projetos.dominio.ArquivoProjeto;
import br.ufrn.sigaa.projetos.dominio.DetalhamentoRecursosProjeto;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.RelatorioAcaoAssociada;
import br.ufrn.sigaa.projetos.dominio.TipoRelatorioProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
import br.ufrn.sigaa.projetos.negocio.RelatorioProjetoValidator;

/** 
 * MBean responsável por operações envolvendo relatório de ações associadas.
 * 
 * @author geyson
 */
@Scope("session")
@Component("relatorioAcaoAssociada")
public class RelatorioAcaoAssociadaMBean extends SigaaAbstractController<RelatorioAcaoAssociada> {

	/** Armazena todas as atividades coordenadas pelo coordenador atual.  **/
	private Collection<Projeto> projetosCoordenador;
	
	/** Utilizado para armazenar informações de uma consulta ao banco */	
	private Collection<TipoCursoEventoExtensao> outrasAcoes = new HashSet<TipoCursoEventoExtensao>();

	/** Utilizado para armazenar informações de uma consulta ao banco */
	private Collection<TipoCursoEventoExtensao> apresentacoes = new HashSet<TipoCursoEventoExtensao>();

	/** Utilizado para armazenar informações de uma consulta ao banco */
	private Collection<TipoProducao> producoesGeradas = new HashSet<TipoProducao>();
	
	/** Descrição do arquivo */
	private String descricaoArquivo;
	
	/**  Utilizado para o envio de arquivos (documentos importantes do relatório) */
	private UploadedFile file;
	
	/** Registro de entrada  */
	private RegistroEntrada registroEntradaDepartamento;

	/** Construtor */
	public RelatorioAcaoAssociadaMBean(){
		obj = new RelatorioAcaoAssociada();
	}
	
	/**
	 * Redireciona para tela de lista de relatórios
	 * cadastrados pelo usuário.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * <ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarCadastroRelatorio() throws SegurancaException {
		checkListRole();
		try {
			projetosCoordenador = carregarProjetosCoordenador();
			removeOperacaoAtiva();
			return forward(ConstantesNavegacaoProjetos.RELATORIO_PROJETO_LISTA);
		} catch (DAOException e) {
			notifyError(e);
			return null;
		}
	}
	
	/**
	 * Retorna todas os projetos coordenados pelo usuário atual que podem
	 * receber cadastros de relatórios
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Método não invocado por JSP´s</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Projeto> carregarProjetosCoordenador() throws DAOException {
		ProjetoDao dao = getDAO(ProjetoDao.class);		
		Integer[] idSituacoesValidas = new Integer[] {TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO, 
				TipoSituacaoProjeto.PROJETO_BASE_CONCLUIDO, TipoSituacaoProjeto.PROJETO_BASE_ENCERRADO_COM_PENDENCIAS,
				TipoSituacaoProjeto.PENDENTE_DE_RELATORIO_FINAL, TipoSituacaoProjeto.EXTENSAO_PENDENTE_DE_RELATORIO};
		return dao.findProjetosComRelatorioByCoordenador(getUsuarioLogado().getServidor(), idSituacoesValidas);
	}
	
	/**
	 * Prepara para adicionar relatório
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/projetos/RelatorioAcaoAssociada/lista.jsp</li>
	 * </ul>
	 * @throws ArqException
	 */ 
	public String preAdicionarRelatorio() throws ArqException {
		Integer id = getParameterInt("id", 0);
		Projeto projeto = getGenericDAO().findByPrimaryKey(id,	Projeto.class);
		
		Boolean relatorioFinal = getParameterBoolean("relatorioFinal");
		TipoRelatorioProjeto tipoRelatorio;
		if (relatorioFinal)
			tipoRelatorio = getGenericDAO().findByPrimaryKey(
					TipoRelatorioProjeto.RELATORIO_FINAL,
					TipoRelatorioProjeto.class);
		else
			tipoRelatorio = getGenericDAO().findByPrimaryKey(
					TipoRelatorioProjeto.RELATORIO_PARCIAL,
					TipoRelatorioProjeto.class);

		ListaMensagens lista = new ListaMensagens();
		if ( projeto.getId() > 0 ) {
			RelatorioProjetoValidator.validaNovoRelatorio(projeto.getId(), tipoRelatorio.getId(), lista);
		}

		if (!lista.isEmpty()) {
			addMensagens(lista);
			return null;
		}
		prepareMovimento(SigaaListaComando.ENVIAR_RELATORIO_ACAO_ASSOCIADA);
		obj = new RelatorioAcaoAssociada();
		obj.setProjeto(projeto);
		obj.getProjeto().setEquipe(getGenericDAO().findByExactField(MembroProjeto.class, "projeto.id", obj.getProjeto().getId()));
		obj.getProjeto().setOrcamento(getGenericDAO().findByExactField(OrcamentoDetalhado.class, "projeto.id", obj.getProjeto().getId()));		
		obj.setDataCadastro(new Date());
		obj.setRegistroEntradaCadastro(getUsuarioLogado().getRegistroEntrada().getId());
		obj.setRegistroEntradaDepartamento(null);
		obj.setTipoRelatorio(tipoRelatorio);
		obj.setPublicoRealAtingido(0); 
		obj.setAtivo(true);

		// Criando detalhamento de recursos do projeto (recursos utilizados)
		Collection<DetalhamentoRecursosProjeto> recursos = new ArrayList<DetalhamentoRecursosProjeto>();
		for (OrcamentoConsolidado oc : projeto.getOrcamentoConsolidado()) {
			DetalhamentoRecursosProjeto recurso = new DetalhamentoRecursosProjeto();
			recurso.setElemento(oc.getElementoDespesa());
			recurso.setInterno(0.0);
			recurso.setExterno(0.0);
			recurso.setOutros(0.0);
			recurso.setRelatorioProjeto(obj);
			recursos.add(recurso);
		}
		obj.setDetalhamentoRecursosProjeto(recursos);
		setReadOnly(false);
		setConfirmButton("Enviar Relatório");
		return forward(ConstantesNavegacaoProjetos.RELATORIO_PROJETO_FORM);
		

	}
	
	/**
	 * Salva rascunho de relatório de projetos
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/projetos/RelatorioAcaoAssociada/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException, NegocioException
	 * 
	 */
	public String salvar() throws ArqException, NegocioException {

		prepareMovimento(SigaaListaComando.SALVAR_RELATORIO_ACAO_ASSOCIADA);
		setOperacaoAtiva(SigaaListaComando.SALVAR_RELATORIO_ACAO_ASSOCIADA.getId());
		// Salvando o rascunho do relatório
		try {
			obj.setDataEnvio(null);
			submeter(SigaaListaComando.SALVAR_RELATORIO_ACAO_ASSOCIADA);
		} catch (NegocioException e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}
		return iniciarCadastroRelatorio();
	}
	
	/**
	 * Enviar o relatório do projeto.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/projetos/RelatorioAcaoAssociada/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException, ArqException, NegocioException
	 * 
	 */
	public String enviar() throws SegurancaException, ArqException,
	NegocioException {

		// VALIDAÇAO
		ListaMensagens lista = new ListaMensagens();
		RelatorioProjetoValidator.validaDadosGeraisRelatorioProjeto(obj, lista);		

		if (!lista.isEmpty()) {
			addMensagens(lista);
			return null;
		}
		try {
			obj.setDataEnvio(new Date());
			setOperacaoAtiva(SigaaListaComando.ENVIAR_RELATORIO_ACAO_ASSOCIADA.getId());
			submeter(SigaaListaComando.ENVIAR_RELATORIO_ACAO_ASSOCIADA);
		} catch (NegocioException e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}
		return iniciarCadastroRelatorio();
	}
	
	/**
	 * Método para salvar o relatório utilizado no cadastro
	 * do relatório 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Método não invocado por JSP´s.</li>
	 * </ul>
	 * 
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public void submeter(Comando operacaoSalvarEnviar)
	throws ArqException, NegocioException {
		Integer operacoes[] = {SigaaListaComando.ENVIAR_RELATORIO_ACAO_ASSOCIADA.getId(), SigaaListaComando.SALVAR_RELATORIO_ACAO_ASSOCIADA.getId(), };
		if(isOperacaoAtiva(operacoes)){
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
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			prepareMovimento(operacaoSalvarEnviar);
			mov.setCodMovimento(operacaoSalvarEnviar);
			execute(mov, getCurrentRequest());
			addMensagemInformation("Operação concluída com sucesso");
			removeOperacaoAtiva();
		}
		else{
			addMensagemErro("Ação já executada");
		}
	}
	
	/** Carrega relatório e prepara MBeans para visualização.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/RelatorioAcaoAssociada/lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public String view() {
		Integer id = getParameterInt("id",0);
		try {
			obj = getGenericDAO().findByPrimaryKey(id, RelatorioAcaoAssociada.class);
			obj.getProjeto().setEquipe(getGenericDAO().findByExactField(MembroProjeto.class, "projeto.id", obj.getProjeto().getId()));

			//Evitar erro de Lazy.
			Collection<MembroProjeto> membros =  getGenericDAO().findByExactField(MembroProjeto.class, "projeto.id", obj.getProjeto().getId());
			obj.getProjeto().setEquipe(membros);
						    	
			return forward(ConstantesNavegacaoProjetos.RELATORIO_PROJETO_VIEW);

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Prepara para remover relatório
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/projetos/RelatorioAcaoAssociada/lista.jsp</li> 
	 * </ul>
	 * @throws ArqException
	 */
	public String preRemoverRelatorio() throws ArqException {

		Integer id = getParameterInt("idRelatorio", 0);
		GenericDAO dao = getGenericDAO();
		try {
			obj = dao.findByPrimaryKey(id,
					RelatorioAcaoAssociada.class);
			obj.setRegistroEntradaCadastro(getUsuarioLogado()
					.getRegistroEntrada().getId());
			obj.setAtivo(false); 
			// Removendo o relatório
			prepareMovimento(SigaaListaComando.REMOVER_RELATORIO_ACAO_ASSOCIADA);
			setOperacaoAtiva(SigaaListaComando.REMOVER_RELATORIO_ACAO_ASSOCIADA.getId());
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}
		setReadOnly(true);
		setConfirmButton("Remover Relatório");
		return forward(ConstantesNavegacaoProjetos.RELATORIO_PROJETO_FORM);
	}
	
	/**
	 * Remove o relatório.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/projetos/RelatorioAcaoAssociada/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String removerRelatorio() throws NegocioException, ArqException {
		if(isOperacaoAtiva(SigaaListaComando.REMOVER_RELATORIO_ACAO_ASSOCIADA.getId())){
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(SigaaListaComando.REMOVER_RELATORIO_ACAO_ASSOCIADA);
			execute(mov, getCurrentRequest());
			addMensagemInformation("Operação concluída com sucesso");
			removeOperacaoAtiva();
			return iniciarCadastroRelatorio();
		}
		else{
			addMensagemErro("Relatório já foi removido.");
			return iniciarCadastroRelatorio();
		}
	}
	
	/**
	 * Adiciona um arquivo ao relatório.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/projetos/RelatorioAcaoAssociada/form.jsp</li>
	 * <ul>
	 * @return
	 */
	public String anexarArquivo() {

		try {
			if ((descricaoArquivo == null) || ("".equals(descricaoArquivo.trim()))) {
				addMensagemErro("Descrição do arquivo é obrigatória!");
				return null;
			}

			if ((file == null)) {
				addMensagemErro("Informe o nome completo do arquivo.");
				return null;
			}
			int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
			EnvioArquivoHelper.inserirArquivo(idArquivo, file.getBytes(), file
					.getContentType(), file.getName());
			ArquivoProjeto arquivo = new ArquivoProjeto();
			arquivo.setRelatorioProjeto(obj);
			arquivo.setDescricao(descricaoArquivo);
			arquivo.setIdArquivo(idArquivo);
			arquivo.setAtivo(true);
			obj.getArquivos().add(arquivo);

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
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/projetos/RelatorioAcaoProjeto/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String removeAnexo() throws ArqException {
		ArquivoProjeto arquivo = new ArquivoProjeto();

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
				addMessage("Remoção realizada com sucesso!", TipoMensagemUFRN.INFORMATION);
				prepareMovimento(ultimoComando);

			} catch (Exception e) {
				notifyError(e);
				addMensagemErroPadrao();
			}
		}

		// Remove do banco de arquivos
		EnvioArquivoHelper.removeArquivo(arquivo.getIdArquivo());

		// Remove da view
		if (obj.getArquivos() != null)
			obj.getArquivos().remove(arquivo);

		return null;
	}
	
	/**
	 * Visualizar o arquivo anexo ao relatório.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/projetos/RelatorioAcaoAssociada/form.jsp</li>
	 * </ul>
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
	 * Coordenador do projeto escolhe um relatório para alteração.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/projetos/RelatorioAcaoAssociada/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * 
	 */
	public String preAlterarRelatorio() throws SegurancaException {

		GenericDAO dao = getGenericDAO();
		try {
			obj = dao.findByPrimaryKey(getParameterInt("idRelatorio",0),
					RelatorioAcaoAssociada.class);
			obj.setRegistroEntradaCadastro(getUsuarioLogado()
					.getRegistroEntrada().getId());
			obj.getDetalhamentoRecursosProjeto().iterator();
			prepareMovimento(SigaaListaComando.ENVIAR_RELATORIO_PROJETO_EXTENSAO);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}finally{
			dao.close();
		}
		setReadOnly(false);
		setConfirmButton("Enviar Relatório");
		return forward(ConstantesNavegacaoProjetos.RELATORIO_PROJETO_FORM);
	}

	public Collection<Projeto> getProjetosCoordenador() {
		return projetosCoordenador;
	}

	public void setProjetosCoordenador(Collection<Projeto> projetosCoordenador) {
		this.projetosCoordenador = projetosCoordenador;
	}

	/**
	 * Lista todos os tipos de curso e eventos possíveis.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * /projetos/RelatorioProjeto/form.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<TipoCursoEventoExtensao> getOutrasAcoes() throws DAOException {
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
	 * Lista todos os tipos de curso e eventos possíveis.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/projetos/RelatorioAcaoAssociada/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<TipoCursoEventoExtensao> getApresentacoes() throws DAOException {
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
	 * 
	 * Lista todas as produções possíveis de TipoProducao.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/projetos/RelatorioAcaoAssociada/form.jsp</li>
	 * </ul>
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

	public String getDescricaoArquivo() {
		return descricaoArquivo;
	}

	public void setDescricaoArquivo(String descricaoArquivo) {
		this.descricaoArquivo = descricaoArquivo;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}
	
	/**
	 * Retorna a lista de todos os arquivos (documentos) anexados ao relatório.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/projetos/RelatorioAcaoAssociada/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<ArquivoProjeto> getArquivosRelatorio()
	throws DAOException {
		return obj.getArquivos();
	}

	public RegistroEntrada getRegistroEntradaDepartamento() {
		return registroEntradaDepartamento;
	}

	public void setRegistroEntradaDepartamento(
			RegistroEntrada registroEntradaDepartamento) {
		this.registroEntradaDepartamento = registroEntradaDepartamento;
	}

}
