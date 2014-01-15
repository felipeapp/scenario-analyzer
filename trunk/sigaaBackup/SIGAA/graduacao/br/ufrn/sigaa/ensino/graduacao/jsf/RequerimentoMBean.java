/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 27/05/2008
 * 
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.RequerimentoDao;
import br.ufrn.sigaa.arq.dao.graduacao.TipoRequerimentoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.Requerimento;
import br.ufrn.sigaa.ensino.graduacao.dominio.StatusRequerimento;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoRequerimento;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoRequerimento;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.negocio.ProcessadorMovimentacaoAluno;
import br.ufrn.sigaa.pessoa.dominio.Discente;

@Component("requerimento")
@Scope("session")

/**
 * Classe para cadastramento de requerimentos.
 * 
 * @author henrique
 * 
 */
public class RequerimentoMBean extends
		SigaaAbstractController<Requerimento> {

	/*
	 * Atributos
	 */

	private Collection<Requerimento> listaRequerimentos;
//	private Requerimento requerimento = new Requerimento();

	/*
	 * Busca
	 */
	private boolean checkBuscaDiscente;
	private boolean checkBuscaCodigo;

	private Integer buscaCodigo;
	private Discente discente = new Discente();

	private Collection<Requerimento> resultado;

	/*
	 * Telas
	 */
	public String telaFormRequerimento() {
		return forward("/graduacao/requerimento/form.jsp");
	}

	public String telaListarRequerimento() {
		return forward("/graduacao/requerimento/listar_requerimentos.jsp");
	}

	public String telaApresentacaoRequerimento() {
		return forward("/graduacao/requerimento/apresentacao_requerimento.jsp");
	}

	public String telaBusca() {
		return forward("/graduacao/requerimento/busca.jsp");
	}

	public String telaResultado() {
		return forward("/graduacao/requerimento/resultado.jsp");
	}

	/**
	 * Abre um requerimento Padrão.
	 * <br>JSP: /SIGAA/app/sigaa.ear/sigaa.war/portais/discente/menu_discente.jsp
	 * 
	 * @throws DAOException
	 * @throws SegurancaException
	 *
	 */
	public void requerimentoPadrao() throws DAOException, SegurancaException {
		obj = new Requerimento();
		TipoRequerimento tipo = new TipoRequerimento();
		obj.setTipo(tipo);
		obj.setGerarCodigo(true);
		abrirRequerimento();
	}

	/**
	 * Abre um requerimento de trancamento
	 * <br> JSP: /SIGAA/app/sigaa.ear/sigaa.war/portais/discente/menu_discente.jsp
	 * 
	 * @throws DAOException
	 * @throws SegurancaException
	 * 
	 */
	public void requerimentoTrancamento() throws DAOException,
			SegurancaException {
		obj = new Requerimento();
		TipoRequerimento tipo = new TipoRequerimento(TipoRequerimento.TRANCAMENTO_PROGRAMA);
		obj.setTipo(tipo);
		obj.setGerarCodigo(false);
		abrirRequerimento();
	}

	/**
	 * Abre um novo requerimento ou um existente.
	 * <br>JSP: /SIGAA/app/sigaa.ear/sigaa.war/portais/discente/listar_requerimentos.jsp
	 * @return
	 * @throws SegurancaException
	 * 
	
	 */
	public String abrirRequerimento() throws SegurancaException {

		if (getNivelEnsino() != NivelEnsino.GRADUACAO)
			throw new SegurancaException("Usuário não autorizado a realizar esta operação");

		try {
			String id = getParameter("idRequerimento");

			if (id == null)
				criarRequerimento();
			else
				carregarExistente(id);

		} catch (DAOException e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro("Um problema ocorreu durante a consulta. Contacte os administradores do sistema");
			return null;
		} catch (NumberFormatException e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro("Passagem de Parâmetro incorreto. Contacte os administradores do sistema");
			return null;
		}
		return telaFormRequerimento();
	}

	/**
	 * Inicia o processo de criar um novo requerimento, chamado em
	 * abrirRequerimento
	 * 
	 * @throws DAOException
	 */
	private void criarRequerimento() throws DAOException {

		DiscenteDao dao = getDAO(DiscenteDao.class);
		DiscenteAdapter discente = getUsuarioLogado().getDiscenteAtivo();
		discente = dao.findByPK(discente.getId());

		obj.setDiscente(discente.getDiscente());
		obj.setStatus(new StatusRequerimento());
	}

	/**
	 * Carrega o requerimento desejado, chamado em exibirRequerimento e
	 * abrirRequerimento
	 * 
	 * @param id
	 * @throws DAOException
	 * @throws NumberFormatException
	 * 
	 * 
	 */
	private void carregarExistente(String id) throws DAOException,
			NumberFormatException {
		RequerimentoDao dao = getDAO(RequerimentoDao.class);
		obj = dao.findByPrimaryKey(new Integer(id), Requerimento.class);
	}

	/**
	 * Apenas grava o requerimento, o aluno ainda pode alterar.
	 * <br> JSP: /SIGAA/app/sigaa.ear/sigaa.war/graduacao/requerimento/form.jsp
	 * @return
	 * @throws ArqException
	 * 
	 */
	public String gravarRequerimento() throws ArqException {

		if (getNivelEnsino() != NivelEnsino.GRADUACAO)
			throw new SegurancaException(
					"Usuário não autorizado a realizar esta operação");

		prepareMovimento(SigaaListaComando.GRAVAR_REQUERIMENTO);

		try {

			obj.setStatus(new StatusRequerimento(StatusRequerimento.ABERTO_PELO_ALUNO));

			chamarProcessador(obj, SigaaListaComando.GRAVAR_REQUERIMENTO);

			addMessage("Requerimento Gravado com sucesso.", TipoMensagemUFRN.INFORMATION);

		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagemErroPadrao();
			notifyError(e);
		}
		
		// marcar para o gc
		obj = null;
		return cancelar();
	}

	/**
	 * Grava e Envia o requerimento, o aluno não pode mais editar
	 * <br> JSP: /SIGAA/app/sigaa.ear/sigaa.war/graduacao/requerimento/form.jsp 
	 * @return
	 * @throws ArqException
	 * 
	 */
	public String enviarRequerimento() throws ArqException {

		if (getNivelEnsino() != NivelEnsino.GRADUACAO)
			throw new SegurancaException("Usuário não autorizado a realizar esta operação");

		prepareMovimento(SigaaListaComando.ENVIAR_REQUERIMENTO);

		try {
			validarRequerimento();
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}

		try {
			StatusRequerimento status = new StatusRequerimento(StatusRequerimento.SUBMETIDO_PELO_ALUNO);
			obj.setStatus(status);
			chamarProcessador(obj,	SigaaListaComando.ENVIAR_REQUERIMENTO);
			addMessage("Requerimento Gravado com sucesso.",	TipoMensagemUFRN.INFORMATION);
			carregarTodos();
		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagemErroPadrao();
			notifyError(e);
		}

		return telaListarRequerimento();
	}

	/**
	 * Valida o requerimento a partir da justificativa e/ou quantidade de semestres 
	 * e/ou ano e período.
	 * 
	 * @throws NegocioException
	 * @throws ArqException
	 * 
	 * 
	 */
	private void validarRequerimento() throws NegocioException, ArqException {
		
		// comum a todos
		if (obj.getSolicitacao().equals(""))
			throw new NegocioException("É necessario justificar a solicitação");

		// trancamento
		if (obj.getTipo().getId() == TipoRequerimento.TRANCAMENTO_PROGRAMA) {
			if (obj.getTrancarQtdSemestres() == null || obj.getTrancarQtdSemestres().intValue() <= 0)
				throw new NegocioException("Quantidade de semestres é obrigatorio. Especifique um valor acima de 0.");

			if (obj.getAnoBase() == null || obj.getPeriodoBase() == null) 
				throw new NegocioException("É necessário especificar a partir de qual Ano e Periodo deseja iniciar o trancamento.");
		
			ProcessadorMovimentacaoAluno proc = new ProcessadorMovimentacaoAluno();
			
			Collection<MovimentacaoAluno> solicitacoes = gerarSolicitacao(obj);
			ParametrosGestoraAcademica params = ParametrosGestoraAcademicaHelper.getParametros(obj.getDiscente());
			
			MovimentacaoAluno alunoMovimentadoAnterior = null;
			for (MovimentacaoAluno alunoMovimentado : solicitacoes) {
				
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setCodMovimento(SigaaListaComando.AFASTAR_ALUNO);
				mov.setObjMovimentado(alunoMovimentado);
				
				proc.validate(mov);
				proc.validarAfastamento(mov);
				proc.validarPeriodoDiscente(mov);
				proc.validarTrancamentoPrograma(mov, params, alunoMovimentadoAnterior, alunoMovimentado.getValorMovimentacao());
				
				alunoMovimentadoAnterior = alunoMovimentado;
				
			}
		}
		
	}	
	
	/**
	 * Exibe todos os requerimentos que o discente logado criou, chamado em
	 * exibirRequerimento
	 * 
	 * @throws DAOException
	 */
	public void carregarTodos() throws DAOException {
		DiscenteAdapter discente = getUsuarioLogado().getDiscenteAtivo();
		RequerimentoDao dao = getDAO(RequerimentoDao.class);
		listaRequerimentos = dao.findAllByDiscente(discente);
	}

	/**
	 * Exibe todos os requerimentos ou somente o desejado.
	 * <br>Chamado pelas JSPs:
	 * <ul><li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/requerimento/busca.jsp
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/requerimento/listar_requerimentos.jsp
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/portais/discente/menu_discente.jsp</ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * 
	 * 
	 */
	public String exibirRequerimento() throws SegurancaException {

		if (getNivelEnsino() != NivelEnsino.GRADUACAO)
			throw new SegurancaException("Usuário não autorizado a realizar esta operação");

		try {
			String id = getParameter("idRequerimento");

			if (id == null) {
				carregarTodos();
				return telaListarRequerimento();
			} else {
				carregarExistente(id);
				return telaApresentacaoRequerimento();
			}

		} catch (DAOException e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro("Um problema ocorreu durante a consulta. Contacte os administradores do sistema");
			return null;
		} catch (NumberFormatException e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro("Passagem de Parâmetro incorreto. Contacte os administradores do sistema");
			return null;
		}

	}

	/**
	 * Monta um combo com todos os tipos de requerimentos existentes
	 */
	public Collection<SelectItem> getAllCombo() {
		TipoRequerimentoDao dao = getDAO(TipoRequerimentoDao.class);
		try {
			return toSelectItems(dao.findByExactField(TipoRequerimento.class, "especializacao", "P", "asc", "descricao"),
					"id", "descricao");

		} catch (DAOException e) {
			notifyError(e);
		}
		return null;
	}

	/**
	 * Chama o processador
	 * 
	 * @param requerimento
	 * @param comando
	 * @throws ArqException
	 * @throws NegocioException
	 * 
	 * 
	 */
	private void chamarProcessador(Requerimento requerimento, Comando comando)
			throws ArqException, NegocioException {
		MovimentoRequerimento mov = new MovimentoRequerimento();
		mov.setRequerimento(requerimento);
		mov.setCodMovimento(comando);
		mov.setUsuarioLogado(getUsuarioLogado());

		executeWithoutClosingSession(mov, getCurrentRequest());
	}

	/**
	 * Localiza um dado requerimento.
	 * <br>JSP: /SIGAA/app/sigaa.ear/sigaa.war/graduacao/requerimento/busca.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String localizar() throws DAOException {

		Discente disc = null;
		Integer codigo = null;

		if (checkBuscaCodigo == false && checkBuscaDiscente == false) {
			addMensagemErro("Refine a consulta");
			return null;
		}

		if (checkBuscaCodigo)
			codigo = buscaCodigo;

		if (checkBuscaDiscente)
			disc = discente;

		RequerimentoDao dao = getDAO(RequerimentoDao.class);
		resultado = dao.FindAllByNomeMatriculaRequerimento(disc, codigo);

		return telaBusca();
	}

	/**
	 * Efetiva o requerimento de trancamento.
	 * <br>  JSP: /SIGAA/app/sigaa.ear/sigaa.war/graduacao/requerimento/busca.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String efetivarTrancamento() throws ArqException {
		/*
		 * pega o requerimento
		 */
		Integer idRequerimento = getParameterInt("idRequerimento");

		RequerimentoDao dao = getDAO(RequerimentoDao.class);
		Requerimento requerimento = dao.findByPrimaryKey(idRequerimento,
				Requerimento.class);

		
		/*
		 * gerar a movimentação para cada semestre requerido
		 */
		Collection<MovimentacaoAluno> solicitacoes = gerarSolicitacao(requerimento);
		Discente discente = requerimento.getDiscente();
		int quantidade = requerimento.getTrancarQtdSemestres();

		/*
		 * executa cada movimentação
		 */
		for (MovimentacaoAluno alunoMovimentado : solicitacoes) {
			prepareMovimento(SigaaListaComando.AFASTAR_ALUNO);
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.AFASTAR_ALUNO);
			mov.setObjMovimentado(alunoMovimentado);
			try {
				executeWithoutClosingSession(mov, getCurrentRequest());
			} catch (NegocioException e) {
				notifyError(e);
				e.printStackTrace();
				addMensagemErro(e.getMessage());
				return null;
			}
		}

		/*
		 * Muda o status do requerimento
		 */
		try {
			StatusRequerimento status = new StatusRequerimento(StatusRequerimento.ATENDIDO);
			requerimento.setStatus(status);
			chamarProcessador(requerimento, SigaaListaComando.GRAVAR_REQUERIMENTO);
		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagemErroPadrao();
			notifyError(e);
		}
		
		addMessage("Trancamento de " + quantidade
				+ " períodos realizado com sucesso para o aluno "
				+ discente.getMatriculaNome(), TipoMensagemUFRN.INFORMATION);
		
		
		return cancelar();
	}

	/**
	 * Gera a movimentação para cada semestre solicitado
	 * 
	 * @param requerimento
	 * @return
	 * @throws DAOException
	 */
	private Collection<MovimentacaoAluno> gerarSolicitacao(Requerimento requerimento) throws DAOException {
		
		Collection<MovimentacaoAluno> resultado = new ArrayList<MovimentacaoAluno>();
		
		int quantidade = requerimento.getTrancarQtdSemestres();
		int anoBase = requerimento.getAnoBase();
		int periodoBase = requerimento.getPeriodoBase();
		Discente discente = requerimento.getDiscente();

		int[] anoPeriodo = new int[quantidade];

		for (int i = 0; i < quantidade; i++) {
			anoPeriodo[i] = DiscenteHelper.somaSemestres(anoBase, periodoBase,
					i);
		}

		for (int i = 0; i < anoPeriodo.length; i++) {

			int ano = anoPeriodo[i] / 10;
			int periodo = anoPeriodo[i] - (ano * 10);

			MovimentacaoAluno alunoMovimentado = new MovimentacaoAluno();
			alunoMovimentado.setDiscente(new Discente());
			alunoMovimentado.setTipoMovimentacaoAluno(new TipoMovimentacaoAluno(TipoMovimentacaoAluno.TRANCAMENTO));
			alunoMovimentado.setAnoReferencia(ano);
			alunoMovimentado.setPeriodoReferencia(periodo);
			alunoMovimentado.setDiscente(discente);
			alunoMovimentado.setLimiteTrancamentos(true);
			alunoMovimentado.setValorMovimentacao(quantidade);

			alunoMovimentado.setTipoMovimentacaoAluno(getGenericDAO().findByPrimaryKey(
					alunoMovimentado.getTipoMovimentacaoAluno().getId(), TipoMovimentacaoAluno.class));
			
			resultado.add(alunoMovimentado);
		}
		
		return resultado;
	}
	
	public Collection<Requerimento> getListaRequerimentos() {
		return listaRequerimentos;
	}

	public void setListaRequerimentos(
			Collection<Requerimento> listaRequerimentos) {
		this.listaRequerimentos = listaRequerimentos;
	}

	public boolean isCheckBuscaDiscente() {
		return checkBuscaDiscente;
	}

	public void setCheckBuscaDiscente(boolean checkBuscaDiscente) {
		this.checkBuscaDiscente = checkBuscaDiscente;
	}

	public boolean isCheckBuscaCodigo() {
		return checkBuscaCodigo;
	}

	public void setCheckBuscaCodigo(boolean checkBuscaCodigo) {
		this.checkBuscaCodigo = checkBuscaCodigo;
	}

	public Integer getBuscaCodigo() {
		return buscaCodigo;
	}

	public void setBuscaCodigo(Integer buscaCodigo) {
		this.buscaCodigo = buscaCodigo;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public Collection<Requerimento> getResultado() {
		return resultado;
	}

	public void setResultado(Collection<Requerimento> resultado) {
		this.resultado = resultado;
	}

}
