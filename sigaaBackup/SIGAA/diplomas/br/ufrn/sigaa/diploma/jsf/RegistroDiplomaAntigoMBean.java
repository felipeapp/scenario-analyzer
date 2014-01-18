/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/05/2009
 *
 */
package br.ufrn.sigaa.diploma.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.diploma.dao.FolhaRegistroDiplomaDao;
import br.ufrn.sigaa.diploma.dao.LivroRegistroDiplomaDao;
import br.ufrn.sigaa.diploma.dao.RegistroDiplomaDao;
import br.ufrn.sigaa.diploma.dominio.FolhaRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.LivroRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.ObservacaoRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.RegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.ResponsavelAssinaturaDiplomas;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.mensagens.MensagensGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Controller respons�vel pelas opera��es de registro de diplomas antigos (anteriores ao registro eletr�nico no SIGAA)
 * @author �dipo Elder F. Melo
 *
 */
@Component("registroDiplomaAntigoMBean")
@Scope("session")
public class RegistroDiplomaAntigoMBean extends	SigaaAbstractController<RegistroDiploma> implements OperadorDiscente{
	
	/** Cole��o de registros de diplomas da folha do livro. */
	private Collection<RegistroDiploma> registrosDiplomas;
	
	/** Livro onde o diploma ser� registrado. */
	private LivroRegistroDiploma livro;
	
	/** Folha onde o diploma ser� registrado. */
	private FolhaRegistroDiploma folha;
	
	/** Observa��o acerca do registro de diploma.*/
	private ObservacaoRegistroDiploma observacao;
	
	/** Arquivo digitalizado do diploma do aluno (diplomas antigos). */
	private UploadedFile diplomaDigitalizado;
	
	/** Respons�veis pela assinatura no diploma antigo no momento do Registro. */
	private ResponsavelAssinaturaDiplomas assinaturaDiploma;

	/**
	 * Cadastra/altera um registro de diploma.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/registro_diplomas_antigo/registro_antigo.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	public String submeterDadosGerais() throws SegurancaException, ArqException, NegocioException {
		if (!checkOperacaoAtiva(SigaaListaComando.REGISTRO_DIPLOMA_INDIVIDUAL.getId()))
			return cancelar();
		if (!livro.isLivroAntigo()) {
			addMensagemErro("O livro utilizado para registro � para novos registros. Selecione um livro para registro de diplomas antigos.");
			return null;
		} else {
			// registro de diploma anterior ao registro no SIGAA
			obj.setFolha(this.folha);
			// evita erro de lazyInitialization
			if (folha.getLivro() != null)
				obj.getLivroRegistroDiploma().isLivroAntigo();
		}
		validacaoDados(erros);
		if(hasErrors())
			return null;
		return formAssinaturas();
	}
	
	/**
	 * Cadastra/altera um registro de diploma.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/registro_diplomas_antigo/registro_antigo.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	public String escolherAssinatura() throws SegurancaException, ArqException, NegocioException {
		if (!checkOperacaoAtiva(SigaaListaComando.REGISTRO_DIPLOMA_INDIVIDUAL.getId()))
			return cancelar();
		int id = getParameterInt("id", 0);
		obj.setAssinaturaDiploma(getGenericDAO().findByPrimaryKey(id, ResponsavelAssinaturaDiplomas.class));
		return forward("/diplomas/registro_diplomas_antigo/confirma.jsp");
	}
	
	/**
	 * Cadastra/altera um registro de diploma.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/registro_diplomas_antigo/registro_antigo.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	public String submeterAssinaturas() throws SegurancaException, ArqException, NegocioException {
		if (!checkOperacaoAtiva(SigaaListaComando.REGISTRO_DIPLOMA_INDIVIDUAL.getId()))
			return cancelar();
		erros.addAll(assinaturaDiploma.validate());
		if(hasErrors())
			return null;
		// verifica se n�o h� registro de assinaturas cadastrado para os valores informados
		String[] atributos = {"nomeReitor", "descricaoFuncaoReitor", "nomeDiretorUnidadeDiplomas", "descricaoFuncaoDiretorUnidadeDiplomas",
				"nomeDiretorGraduacao", "descricaoFuncaoDiretorGraduacao"};
		for (ResponsavelAssinaturaDiplomas ass : getGenericDAO().findAll(ResponsavelAssinaturaDiplomas.class)) {
			if (EqualsUtil.testTransientEquals(assinaturaDiploma, ass, atributos)) {
				addMensagemErro("H� uma configura��o de assinaturas na lista com os mesmos valores informados. Por favor, selecione o item correspondente na lista.");
				return null;
			}
		}
		assinaturaDiploma.setNivel(obj.getDiscente().getNivel());
		obj.setAssinaturaDiploma(assinaturaDiploma);
		return forward("/diplomas/registro_diplomas_antigo/confirma.jsp");
	}
	
	/**
	 * Cadastra/altera um registro de diploma.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/registro_diplomas_antigo/confirma.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		if (!checkOperacaoAtiva(SigaaListaComando.REGISTRO_DIPLOMA_INDIVIDUAL.getId()))
			return cancelar();
		if (observacao != null && !observacao.getObservacao().isEmpty()) {
			observacao.setRegistroDiploma(obj);
			obj.addObservacao(observacao);
		}
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			if (this.diplomaDigitalizado != null) {
				mov.setObjAuxiliar(diplomaDigitalizado);
			}
			mov.setCodMovimento(SigaaListaComando.REGISTRO_DIPLOMA_INDIVIDUAL);
			mov.setObjMovimentado(obj);
			mov.setUsuarioLogado(getUsuarioLogado());
			execute(mov);
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Diploma N� " + obj.getNumeroRegistro());
			obj = getGenericDAO().refresh(obj);
			getCurrentRequest().setAttribute("registro", obj);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			if (this.diplomaDigitalizado != null)
				addMensagemWarning("Aten��o: selecione o arquivo do diploma para enviar novamente.");
			return null;
		}
		return cancelar();
	}
	
	/**
	 * Inicializa os atributos do controller.
	 * @throws DAOException 
	 * @throws NumberFormatException 
	 */
	private void initObj() throws NumberFormatException, DAOException {
		this.obj = new RegistroDiploma();
		this.obj.setDiscente(new Discente());
		this.obj.setFolha(new FolhaRegistroDiploma());
		this.obj.getFolha().setLivro(new LivroRegistroDiploma());
		this.livro = new LivroRegistroDiploma();
		this.folha = new FolhaRegistroDiploma();
		this.observacao = new ObservacaoRegistroDiploma();
		this.assinaturaDiploma = new ResponsavelAssinaturaDiplomas();
		obj.setAssinaturaDiploma(assinaturaDiploma);
	}
	
	/** Link para o fomul�rio de registro de diploma antigo.
	 * <br/>M�todo n�o invocado por JSP�s.
	 * @return
	 */
	public String formRegistroAntigo() {
		if (!checkOperacaoAtiva(SigaaListaComando.REGISTRO_DIPLOMA_INDIVIDUAL.getId()))
			return cancelar();
		return forward("/diplomas/registro_diplomas_antigo/registro_antigo.jsp");
	}
	
	/** Link para o fomul�rio de registro de diploma antigo.
	 * <br/>M�todo n�o invocado por JSP�s.
	 * @return
	 */
	public String formAssinaturas() {
		if (!checkOperacaoAtiva(SigaaListaComando.REGISTRO_DIPLOMA_INDIVIDUAL.getId()))
			return cancelar();
		if (obj.getAssinaturaDiploma().getId() == 0)
			assinaturaDiploma = obj.getAssinaturaDiploma();
		else
			assinaturaDiploma = new ResponsavelAssinaturaDiplomas(); 
		return forward("/diplomas/registro_diplomas_antigo/assinaturas.jsp");
	}
	
	/** Valida os dados da opera��o.
	 * <br/>M�todo n�o invocado por JSP�s.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(java.util.Collection)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens mensagens) {
		// data de cola��o
		ValidatorUtil.validateRequired(obj.getDataColacao(), "Data de Cola��o", mensagens);
		// data de expedi��o
		ValidatorUtil.validateRequired(obj.getDataExpedicao(), "Data de Expedi��o", mensagens);
		// data de registro
		ValidatorUtil.validateRequired(obj.getDataRegistro(), "Data de Registro", mensagens);
		ValidatorUtil.validateRequiredId(this.livro.getId(), "Livro", mensagens);
		ValidatorUtil.validateRequiredId(this.folha.getId(), "Folha", mensagens);
		ValidatorUtil.validateRequiredId(obj.getId(), "Ordem na Folha", mensagens);
		ValidatorUtil.validateMinValue(obj.getNumeroRegistro(), 1, "N�mero de Registro", mensagens);
		return hasErrors();
	}
	
	/** Retorna a cole��o de registros de diplomas da folha do livro. 
	 * @return
	 */
	public Collection<RegistroDiploma> getRegistrosDiplomas() {
		return registrosDiplomas;
	}

	/** Seta a cole��o de registros de diplomas da folha do livro.
	 * @param registrosDiplomas
	 */
	public void setRegistrosDiplomas(Collection<RegistroDiploma> registrosDiplomas) {
		this.registrosDiplomas = registrosDiplomas;
	}

	/** M�todo invocado pela interface OperadorDiscente.
	 * <br/>M�todo n�o invocado por JSP�s.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */
	public String selecionaDiscente() throws ArqException {
		if (!checkOperacaoAtiva(SigaaListaComando.REGISTRO_DIPLOMA_INDIVIDUAL.getId()))
			return cancelar();
		ValidatorUtil.validateRequired(obj.getDiscente(), "Discente", erros);
		if (hasErrors()) return null;
		Integer operacaoAtiva = (Integer) getCurrentSession().getAttribute("operacaoAtiva");
		boolean operacaoAlterar = operacaoAtiva != null && operacaoAtiva.equals(ArqListaComando.ALTERAR.getId());
		// verifica se h� registro de diploma para o discente selecionado.
		RegistroDiplomaDao dao = getDAO(RegistroDiplomaDao.class);
		LivroRegistroDiplomaDao livroDao = getDAO(LivroRegistroDiplomaDao.class);
		RegistroDiploma registroAntigo = dao.findByDiscente(this.obj.getDiscente().getId());
		if (registroAntigo != null && !operacaoAlterar) {
			addMensagem(MensagensGerais.DISCENTE_COM_DIPLOMA_REGISTRADO,
					registroAntigo.getDiscente().getMatriculaNome(), 
					registroAntigo.getLivroRegistroDiploma().getTitulo(),
					registroAntigo.getFolha().getNumeroFolha(), registroAntigo.getNumeroRegistro());
			return null;
		} else {
			// verifica se o discente possui data de cola��o de grau
			obj.setDataColacao(this.obj.getDiscente().getDataColacaoGrau());
			// verifica se h� livro aberto para o registro do diploma
			LivroRegistroDiploma livro = livroDao.findByCurso(obj.getDiscente().getCurso().getId(), true, this.livro.isLivroAntigo());
			if (livro == null) {
				addMensagem(MensagensGraduacao.NAO_HA_LIVRO_ABERTO_PARA_REGISTRO_CURSO, obj.getDiscente().getCurso().getDescricao() + (this.livro.isLivroAntigo() ? " para registros antigos." : ""));
				return null;
			} 
			this.livro = livro;
			// verifica se a data de cola��o � anterior ao in�cio do registro de diploma no SIGAA
			if (this.obj.getDataColacao() != null) {
				int anoInicioRegistro = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.ANO_INICIO_REGISTRO_DIPLOMA);
				int semestreInicioRegistro = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.SEMESTRE_INICIO_REGISTRO_DIPLOMA);
				Date dataColacao = this.obj.getDataColacao();
				int ano = CalendarUtils.getAno(dataColacao);
				int semestre = CalendarUtils.getMesByData(dataColacao) / 7 + 1;
				if (ano * 10 + semestre >= anoInicioRegistro * 10 + semestreInicioRegistro) {
					addMensagemErro("Este dicente n�o pode ter o diploma registrado como antigo pois sua data de cola��o � anterior a "
							+ (anoInicioRegistro / 10) + "." + (anoInicioRegistro % 10));
					return null;
				}
			}
		}
		if (hasErrors())
			return null;
		setConfirmButton("Cadastrar");
		return formRegistroAntigo();
	}
	
	/** Seta o discente que ter� o diploma registrado.
	 * <br/>M�todo n�o invocado por JSP�s.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 */
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		this.obj.setDiscente(discente.getDiscente());
	}

	/**
	 * Inicia o registro de diploma antigo (anterior ao registro autom�tico no
	 * SIGAA).<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/menus/registro.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		checkChangeRole();
		initObj();
		livro.setLivroAntigo(true);
		prepareMovimento(SigaaListaComando.REGISTRO_DIPLOMA_INDIVIDUAL);
		setOperacaoAtiva(SigaaListaComando.REGISTRO_DIPLOMA_INDIVIDUAL.getId());
		setConfirmButton("Cadastrar");
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.REGISTRO_DIPLOMA_ANTIGO);
		return buscaDiscenteMBean.popular();
	}
	
	/** Retorna uma cole��o de SelecItem de livros de acordo com o caso de uso.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getLivrosCombo() throws DAOException {
		Collection<SelectItem> itens = new ArrayList<SelectItem>();
		LivroRegistroDiplomaDao dao = getDAO(LivroRegistroDiplomaDao.class);
		Collection<LivroRegistroDiploma> livros = new ArrayList<LivroRegistroDiploma>();
		if (obj != null && obj.getDiscente() != null && obj.getDiscente().getCurso() != null) {
			// retorna apenas os livros do curso do discente
			LivroRegistroDiploma livroDoCurso = dao.findByCurso(obj.getDiscente().getCurso().getId(), true, livro.isLivroAntigo());
			if (livroDoCurso != null) {
				livros.add(livroDoCurso);
			} 
		} 
		for (LivroRegistroDiploma livro : livros) {
			if (livro.isLivroAntigo() == this.livro.isLivroAntigo() && livro.isRegistroExterno() == this.livro.isRegistroExterno()) {
				SelectItem item = new SelectItem(livro.getId(), livro.getTitulo() + " - " +(livro.isRegistroExterno()?livro.getInstituicao():obj.getDiscente().getCurso().getDescricao()));
				itens.add(item);
			}
		}
		return itens;
	}
	
	/** Retorna uma cole��o de SelectItem de folhas com registro livre no livro.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getFolhasCombo() throws DAOException{
		Collection<SelectItem> itens = new ArrayList<SelectItem>();
		FolhaRegistroDiplomaDao dao = getDAO(FolhaRegistroDiplomaDao.class);
		for (FolhaRegistroDiploma folha : dao.findByLivro(livro)) {
			if (folha.hasRegistroLivre()) {
				SelectItem item = new SelectItem(folha.getId(),String.valueOf(folha.getNumeroFolha()));
				itens.add(item);
			}
		}
		return itens;
	}
	
	/** Retorna uma cole��o de SelectItem de registros da folha.
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getRegistroCombo() throws DAOException{
		FolhaRegistroDiplomaDao dao = getDAO(FolhaRegistroDiplomaDao.class);
		folha = dao.refresh(folha);
		Collection<SelectItem> itens = new ArrayList<SelectItem>();
		if (folha != null && folha.getRegistros() != null) {
			int i = 0;
			for (RegistroDiploma registro: folha.getRegistros()) {
				i++;
				// exclui as posi��es onde h� registro de diploma, exceto para lato sensu, que possui livro com v�rios registros por folha
				if (registro.isLivre() || registro.getLivroRegistroDiploma().isLatoSensu()) {
					SelectItem item = new SelectItem(registro.getId(), String.valueOf(i));
					itens.add(item);
				}
			}
		}
		return itens;
	}

	/** Retorna o livro onde o diploma ser� registrado.  
	 * @return
	 */
	public LivroRegistroDiploma getLivro() {
		return livro;
	}

	/** Seta o livro onde o diploma ser� registrado.
	 * @param livro
	 */
	public void setLivro(LivroRegistroDiploma livro) {
		this.livro = livro;
	}

	/** Retorna a folha onde o diploma ser� registrado. 
	 * @return
	 */
	public FolhaRegistroDiploma getFolha() {
		return folha;
	}

	/** Seta a folha onde o diploma ser� registrado.
	 * @param folha
	 */
	public void setFolha(FolhaRegistroDiploma folha) {
		this.folha = folha;
	}

	/** Retorna a observa��o acerca do registro de diploma.
	 * @return Observa��o acerca do registro de diploma.
	 */
	public ObservacaoRegistroDiploma getObservacao() {
		return observacao;
	}

	/** Seta a observa��o acerca do registro de diploma.
	 * @param observacao Observa��o acerca do registro de diploma.
	 */
	public void setObservacao(ObservacaoRegistroDiploma observacao) {
		this.observacao = observacao;
	}

	/** Verifica as permiss�es do usu�rio.
	 * <br/>M�todo n�o invocado por JSP�s.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_DIPLOMAS_GRADUACAO, SigaaPapeis.GESTOR_DIPLOMAS_LATO, SigaaPapeis.GESTOR_DIPLOMAS_STRICTO);
	}

	/** Retorna o arquivo digitalizado do diploma do aluno (diplomas antigos). 
	 * @return
	 */
	public UploadedFile getDiplomaDigitalizado() {
		return diplomaDigitalizado;
	}

	/** Seta o arquivo digitalizado do diploma do aluno (diplomas antigos).
	 * @param diplomaDigitalizado
	 */
	public void setDiplomaDigitalizado(UploadedFile diplomaDigitalizado) {
		this.diplomaDigitalizado = diplomaDigitalizado;
	}

	public ResponsavelAssinaturaDiplomas getAssinaturaDiploma() {
		return assinaturaDiploma;
	}

	public void setAssinaturaDiploma(ResponsavelAssinaturaDiplomas assinaturaDiploma) {
		this.assinaturaDiploma = assinaturaDiploma;
	}
}
