/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/06/2009
 *
 */
package br.ufrn.sigaa.diploma.jsf;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.diploma.dao.RegistroDiplomaDao;
import br.ufrn.sigaa.diploma.dominio.AlteracaoRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.FolhaRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.LivroRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.ObservacaoRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.RegistroDiploma;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/** Controller responsável pelas operações de busca e visualização de registros de diplomas.
 * @author Édipo Elder F. Melo
 *
 */
@Component("buscaRegistroDiploma")
@Scope("session")
public class BuscaRegistroDiplomaMBean extends SigaaAbstractController<RegistroDiploma> {
	
	/** Indica se filtra a busca por nome do discente. */
	private boolean filtroNome;
	
	/** Indica se filtra a busca pela matrícula do discente. */
	private boolean filtroMatricula;
	
	/** Indica se filtra a busca pelo número do registro do diploma. */
	private boolean filtroNumero;
	
	/** Coleção de registros encontrados pela busca. */
	private Collection<RegistroDiploma> registrosEncontrados;

	/** Livro onde o diploma será registrado. */
	private LivroRegistroDiploma livro;
	
	/** Folha onde o diploma será registrado. */
	private FolhaRegistroDiploma folha;
	
	/** Observação a adicionar ao registro de diploma. */
	private ObservacaoRegistroDiploma observacao;

	/** Indica se a operação atual do controller é alterar. */
	private int operacao;
	
	/** Discente de Graduação do Diploma Registrado. */
	private DiscenteGraduacao discenteGraduacao;
	
	private Collection<AlteracaoRegistroDiploma> alteracoesRegistroDiploma;
	
	// constantes
	/** Constante que define a operação do controler como a de visualização de registro de diplomas. */
	private static final int VISUALIZAR_REGISTRO_DIPLOMA = 1;
	/** Constante que define a operação do controler como a de alteração de registro de diplomas. */
	private static final int ADICIONAR_OBSERVACAO_REGISTRO_DIPLOMA = 2;
	/** Constante que define a operação do controler como a de remoção de registro de diplomas. */
	private static final int REMOVER_REGISTRO_DIPLOMA = 3;
	/** Constante que define a operação do controler como a de alteração de registro de diplomas. */
	private static final int ALTERAR_REGISTRO_DIPLOMA = 4;
	
	/** Construtor padrão. */
	public BuscaRegistroDiplomaMBean() {
	}

	/**
	 * Inicia a busca de registros.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/menus/registro.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarBusca() throws SegurancaException {
		initController();
		checkChangeRole();
		operacao = VISUALIZAR_REGISTRO_DIPLOMA;
		return formBusca();
	}
	
	/**
	 * Inicia a remoção de registros de diplomas.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/menus/registro.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public String iniciarRemover() throws SegurancaException {
		initController();
		checkChangeRole();
		operacao = REMOVER_REGISTRO_DIPLOMA;
		return formBusca();
	}

	/** 
	 * Retorna o link para o formulário de busca de registros.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Método não invocado por JSP´s.</li>
	 * </ul>
	 * @return
	 */
	public String formBusca() {
		return forward("/diplomas/registro_diplomas/busca_registro.jsp");
	}
	
	/** Inicializa os atributos do controller.
	 * 
	 */
	private void initController() {
		removeOperacaoAtiva();
		obj = new RegistroDiploma();
		obj.setFolha(new FolhaRegistroDiploma());
		obj.getFolha().setLivro(new LivroRegistroDiploma());
		obj.setDiscente(new Discente());
		this.filtroNumero = false;
		this.filtroNome = false;
		this.filtroMatricula = false;
		this.registrosEncontrados = null;
		this.observacao = new ObservacaoRegistroDiploma();
	}
	
	/**
	 * Inicia a operação de atualização da observação de um registro de diploma.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/menus/registro.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarEditarObservacao() throws ArqException {
		initController();
		checkChangeRole();
		setConfirmButton("Incluir Observação");
		this.operacao = ADICIONAR_OBSERVACAO_REGISTRO_DIPLOMA;
		return formBusca();
	}
	
	/**
	 * Inicia a operação de atualização da observação de um registro de diploma.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/menus/registro.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarAlterarRegistroDiploma() throws ArqException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DIPLOMAS);
		initController();
		this.operacao = ALTERAR_REGISTRO_DIPLOMA;
		return formBusca();
	}

	/**
	 * Realiza a busca de acordo com os parâmetros informados.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/registro_diplomas/busca_registro.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#buscar()
	 */
	@Override
	public String buscar() throws DAOException {
		registrosEncontrados = null;
		// limpa os atributos que não serão utilizados na busca por exemplo
		if (!filtroNome) {
			obj.getDiscente().getPessoa().setNome(null);
		} else {
			validateRequired(obj.getDiscente().getPessoa().getNome(), "Nome", erros);
		}
		if(!filtroNumero) {
			obj.setNumeroRegistro(null);
		} else {
			validateRequired(obj.getNumeroRegistro(), "Número de Registro", erros);
		}
		if(!filtroMatricula) {
			obj.getDiscente().setMatricula(null);
		} else {
			validateRequired(obj.getDiscente().getMatricula(), "Matrícula", erros);
		}
		// restringe a busca por nível de ensino
		char nivelEnsino = ' ';
		if (getNiveisHabilitados().length > 1)
			nivelEnsino = obj.getLivroRegistroDiploma().getNivel();
		else 
			nivelEnsino = getNiveisHabilitados()[0];
		if (hasErrors())
			return null;
		if ( (obj.getDiscente().getPessoa().getNome() == null
				||obj.getDiscente().getPessoa().getNome().isEmpty())
			&& obj.getNumeroRegistro() == null
			&& (obj.getDiscente().getMatricula() == null || obj.getDiscente().getMatricula() == 0) ) {
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			return null;
		}
		RegistroDiplomaDao dao = getDAO(RegistroDiplomaDao.class);
		registrosEncontrados = dao.findByDiscenteNumeroRegistro(obj.getDiscente(), obj.getNumeroRegistro(), nivelEnsino);
		if (registrosEncontrados == null || registrosEncontrados.isEmpty())
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		return formBusca();
	}

	/**
	 * Exibe os dados do registro de diploma.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/registro_diplomas/busca_registro.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public String selecionaRegistro() throws ArqException, NegocioException {
		populateObj(true);
		if (obj.getDiscente().isGraduacao())
			discenteGraduacao = getGenericDAO().findByPrimaryKey(obj.getDiscente().getId(), DiscenteGraduacao.class);
		if (obj != null && isOperacaoAdicionarObservacao()) {
		// verifica se há registro de diploma, caso a operação ativa seja editar a observação.
			this.livro = obj.getLivroRegistroDiploma();
			this.folha = obj.getFolha();
			return forward("/diplomas/registro_diplomas/edita_observacao.jsp");
		} else if (isOperacaoVisualizar()) {
			RegistroDiplomaDao dao = getDAO(RegistroDiplomaDao.class);
			alteracoesRegistroDiploma = dao.findAlteracoesRegistroDiploma(obj);
			return forward("/diplomas/registro_diplomas/view.jsp");
		} else if (isOperacaoRemover()) {
			return forward("/diplomas/registro_diplomas/remove_registro.jsp");
		} else if (isOperacaoAlterarRegistro()) {
			AlteracaoRegistroDiplomaMBean mBean = getMBean("alteracaoRegistroDiplomaMBean");
			return mBean.inciarAlteracaoRegistroDiploma(obj);
		} else {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
	}

	/**
	 * Atualiza a observação de um registro de diploma.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/registro_diplomas/edita_observacao.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	public String atualizaObservacao() throws SegurancaException, ArqException,
	NegocioException {
		ValidatorUtil.validateRequired(this.observacao.getObservacao(), "Observação", erros);
		if(hasErrors())
			return null;
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		prepareMovimento(ArqListaComando.CADASTRAR);
		observacao.setRegistroDiploma(obj);
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(ArqListaComando.CADASTRAR);
		mov.setObjMovimentado(observacao);
		mov.setUsuarioLogado(getUsuarioLogado());
		execute(mov);
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO, "Observação");
		obj = getGenericDAO().refresh(obj);
		this.observacao = new ObservacaoRegistroDiploma();
		return null;
	}

	/**
	 * Remove (inativa) uma observação sobre o registro de diploma.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/registro_diplomas/edita_observacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public String removerObservacao() throws NegocioException, ArqException {
		Integer id = getParameterInt("id");
		if (id != null) {
			ObservacaoRegistroDiploma obs = getGenericDAO().findByPrimaryKey(id, ObservacaoRegistroDiploma.class);
			if (obs == null || !obs.isAtivo()) {
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
				return null;
			}
			obs.setAtivo(false);
			prepareMovimento(ArqListaComando.ALTERAR);
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(ArqListaComando.ALTERAR);
			mov.setUsuarioLogado(getUsuarioLogado());
			mov.setObjMovimentado(obs);
			execute(mov);
			addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Mensagem");
		}
		obj = getGenericDAO().refresh(obj);
		return forward("/diplomas/registro_diplomas/edita_observacao.jsp");
	}
	
	/**
	 * Remove (inativa) um registro de diploma registro de diploma.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/registro_diplomas/remove_registro.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	@Override
	public String remover() throws ArqException {
		try {
			if (obj == null || !obj.isAtivo()) {
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
				return null;
			}
			// inativa e adiciona a observação que o registro é inválido
			obj.setAtivo(false);
			obj.addObservacao(new ObservacaoRegistroDiploma("REGISTRO EXCLUÍDO POR: " + getRegistroEntrada().getUsuario().getNome()));
			prepareMovimento(ArqListaComando.ALTERAR);
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(ArqListaComando.ALTERAR);
			mov.setUsuarioLogado(getUsuarioLogado());
			mov.setObjMovimentado(obj);
			execute(mov);
			addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Registro de Diploma");
			obj = new RegistroDiploma();
		} catch (Exception e) {
			tratamentoErroPadrao(e);
			e.printStackTrace();
		}
		return cancelar();
	}

	/** 
	 * Indica se filtra a busca por nome do discente.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/diplomas/registro_diplomas/busca_registro.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isFiltroNome() {
		return filtroNome;
	}

	/** 
	 * Seta se filtra a busca por nome do discente.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>não invocado por JSP.</li>
	 * </ul>
	 * @param filtroNome
	 */
	public void setFiltroNome(boolean filtroNome) {
		this.filtroNome = filtroNome;
	}

	/** 
	 * Indica se filtra a busca pelo número do registro do diploma.
	 *
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/diplomas/registro_diplomas/busca_registro.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isFiltroNumero() {
		return filtroNumero;
	}

	/** 
	 * Seta se filtra a busca pelo número do registro do diploma. 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>não invocado por JSP.</li>
	 * </ul>
	 * @param filtroNumero
	 */
	public void setFiltroNumero(boolean filtroNumero) {
		this.filtroNumero = filtroNumero;
	}

	/** 
	 * Retorna a coleção de registros encontrados pela busca.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/diplomas/registro_diplomas/busca_registro.jsp</li>
	 * </ul>
	 * @return
	 */
	public Collection<RegistroDiploma> getRegistrosEncontrados() {
		return registrosEncontrados;
	}

	/** 
	 * Seta a coleção de registros encontrados pela busca.
     *
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>não invocado por JSP.</li>
	 * </ul>
	 * @param registrosEncontrados
	 */
	public void setRegistrosEncontrados(
			Collection<RegistroDiploma> registrosEncontrados) {
		this.registrosEncontrados = registrosEncontrados;
	}

	/** 
	 * Indica se filtra a busca pela matrícula do discente.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/diplomas/registro_diplomas/busca_registro.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isFiltroMatricula() {
		return filtroMatricula;
	}

	/** 
	 * Seta se filtra a busca pela matrícula do discente.
	 *  
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>não invocado por JSP.</li>
	 * </ul>
	 * @param filtroMatricula 
	 */
	public void setFiltroMatricula(boolean filtroMatricula) {
		this.filtroMatricula = filtroMatricula;
	}

	/** 
	 * Retorna o livro onde o diploma será registrado.
	 *
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>não invocado por JSP.</li>
	 * </ul>
	 * @return
	 */
	public LivroRegistroDiploma getLivro() {
		return livro;
	}

	/** 
	 * Seta o livro onde o diploma será registrado.
 	 *
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>não invocado por JSP.</li>
	 * </ul>
	 * @param livro
	 */
	public void setLivro(LivroRegistroDiploma livro) {
		this.livro = livro;
	}

	/** 
	 * Retorna a folha onde o diploma será registrado.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>não invocado por JSP.</li>
	 * </ul>
	 * @return
	 */
	public FolhaRegistroDiploma getFolha() {
		return folha;
	}

	/** 
	 * Seta a folha onde o diploma será registrado.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>não invocado por JSP.</li>
	 * </ul>
	 * @param folha
	 */
	public void setFolha(FolhaRegistroDiploma folha) {
		this.folha = folha;
	}

	/** 
	 * Retorna a observação a adicionar ao registro de diploma.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/diplomas/registro_diplomas/edita_observacao.jsp</li>
	 * </ul>
	 * @return
	 */
	public ObservacaoRegistroDiploma getObservacao() {
		return observacao;
	}

	/** 
	 * Seta a observação a adicionar ao registro de diploma.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>Não invocado por JSP.</li>
	 * </ul>
	 * @param observacao
	 */
	public void setObservacao(ObservacaoRegistroDiploma observacao) {
		this.observacao = observacao;
	}
	
	/** 
	 * Verifica as permissões do usuário.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>Não invocado por JSP.</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_DIPLOMAS_GRADUACAO, SigaaPapeis.GESTOR_DIPLOMAS_LATO, SigaaPapeis.GESTOR_DIPLOMAS_STRICTO);
	}

	/** 
	 * Indica se a operação atual do controler é a de alteração de registro de diplomas.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>Não invocado por JSP.</li>
	 * </ul>
	 * @return
	 */
	public boolean isOperacaoAdicionarObservacao(){
		return operacao == ADICIONAR_OBSERVACAO_REGISTRO_DIPLOMA;
	}
	
	/** 
	 * Indica se a operação atual do controler é a de alteração de registro de diplomas.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>Não invocado por JSP.</li>
	 * </ul>
	 * @return
	 */
	public boolean isOperacaoAlterarRegistro(){
		return operacao == ALTERAR_REGISTRO_DIPLOMA;
	}
	
	/** 
	 * Indica se a operação atual do controler é a de visualização de registro de diplomas.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>Não invocado por JSP.</li>
	 * </ul>
	 * @return
	 */
	public boolean isOperacaoVisualizar() {
		return operacao == VISUALIZAR_REGISTRO_DIPLOMA;
	}
	
	/** 
	 * Indica se a operação atual do controler é a de remoção de registro de diplomas.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>Não invocado por JSP.</li>
	 * </ul>
	 * @return
	 */
	public boolean isOperacaoRemover() {
		return operacao == REMOVER_REGISTRO_DIPLOMA;
	}

	public DiscenteGraduacao getDiscenteGraduacao() {
		return discenteGraduacao;
	}

	public void setDiscenteGraduacao(DiscenteGraduacao discenteGraduacao) {
		this.discenteGraduacao = discenteGraduacao;
	}

	public Collection<AlteracaoRegistroDiploma> getAlteracoesRegistroDiploma() {
		return alteracoesRegistroDiploma;
	}

	public void setAlteracoesRegistroDiploma(
			Collection<AlteracaoRegistroDiploma> alteracoesRegistroDiploma) {
		this.alteracoesRegistroDiploma = alteracoesRegistroDiploma;
	}
}