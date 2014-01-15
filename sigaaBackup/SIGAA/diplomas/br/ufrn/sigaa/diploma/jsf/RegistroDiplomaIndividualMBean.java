/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/05/2009
 *
 */
package br.ufrn.sigaa.diploma.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

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
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.HomologacaoTrabalhoFinalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.diploma.dao.FolhaRegistroDiplomaDao;
import br.ufrn.sigaa.diploma.dao.LivroRegistroDiplomaDao;
import br.ufrn.sigaa.diploma.dao.RegistroDiplomaDao;
import br.ufrn.sigaa.diploma.dominio.FolhaRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.LivroRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.ObservacaoRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.RegistroDiploma;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.stricto.dominio.HomologacaoTrabalhoFinal;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.mensagens.MensagensGraduacao;
import br.ufrn.sigaa.parametros.dominio.MensagensStrictoSensu;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Controller responsável pelas operações de cadastro / alteração de registros de diplomas.
 * @author Édipo Elder F. Melo
 *
 */
@Component("registroDiplomaIndividual")
@Scope("session")
public class RegistroDiplomaIndividualMBean extends	SigaaAbstractController<RegistroDiploma> implements OperadorDiscente{
	
	/** Coleção de registros de diplomas da folha do livro. */
	private Collection<RegistroDiploma> registrosDiplomas;
	
	/** Livro onde o diploma será registrado. */
	private LivroRegistroDiploma livro;
	
	/** Folha onde o diploma será registrado. */
	private FolhaRegistroDiploma folha;
	
	/** Observação acerca do registro de diploma.*/
	private ObservacaoRegistroDiploma observacao;
	
	/** Arquivo digitalizado do diploma do aluno (diplomas antigos). */
	private UploadedFile diplomaDigitalizado;

	/**
	 * Inicia a operação de registro de diplomas.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/diplomas/menus/registro.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		initObj();
		checkChangeRole();
		prepareMovimento(SigaaListaComando.REGISTRO_DIPLOMA_INDIVIDUAL);
		setOperacaoAtiva(SigaaListaComando.REGISTRO_DIPLOMA_INDIVIDUAL.getId());
		setConfirmButton("Cadastrar");
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.REGISTRO_DIPLOMA_INDIVIDUAL);
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Cadastra/altera um registro de diploma.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/registro_diplomas/registro_unico.jsp</li>
	 * <li>/sigaa.war/diplomas/registro_diplomas/registro_antigo.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
	NegocioException {
		LivroRegistroDiplomaDao dao = getDAO(LivroRegistroDiplomaDao.class);
		Integer operacaoAtiva = (Integer) getCurrentSession().getAttribute("operacaoAtiva");
		boolean operacaoAlterar = operacaoAtiva != null && operacaoAtiva.equals(ArqListaComando.ALTERAR.getId());
		if (livro.isLivroAntigo()) {
			// registro de diploma anterior ao registro no SIGAA
			obj.setFolha(this.folha);
			// evita erro de lazyInitialization
			obj.getLivroRegistroDiploma().isLivroAntigo();
		} else {
			// registro automático no SIGAA
			livro = dao.findByPrimaryKey(livro.getId());
			RegistroDiploma registroLivre = livro.getRegistroLivre();
			registroLivre.setDiscente(obj.getDiscente());
			registroLivre.setProcesso(obj.getProcesso());
			registroLivre.setDataColacao(obj.getDataColacao());
			registroLivre.setDataExpedicao(obj.getDataExpedicao());
			registroLivre.setDataRegistro(obj.getDataRegistro());
			obj = registroLivre;
		}
		validacaoDados(erros);
		if(hasErrors())
			return null;
		if (observacao != null && !observacao.getObservacao().isEmpty()) {
			observacao.setRegistroDiploma(obj);
			obj.addObservacao(observacao);
		}
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			if (this.diplomaDigitalizado != null) {
				mov.setObjAuxiliar(diplomaDigitalizado);
			}
			if (operacaoAlterar) {
				mov.setCodMovimento(ArqListaComando.ALTERAR);
				mov.setObjMovimentado(obj);
				mov.setUsuarioLogado(getUsuarioLogado());
				execute(mov);
				addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Mensagem");
			} else {
				mov.setCodMovimento(SigaaListaComando.REGISTRO_DIPLOMA_INDIVIDUAL);
				mov.setObjMovimentado(obj);
				mov.setUsuarioLogado(getUsuarioLogado());
				execute(mov);
				if (obj.getLivroRegistroDiploma().isRegistroCertificado())
					addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Certificado Nº " + obj.getNumeroRegistro());
				else
					addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Diploma Nº " + obj.getNumeroRegistro());
			}
			obj = getGenericDAO().refresh(obj);
			getCurrentRequest().setAttribute("registro", obj);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			if (livro.isLivroAntigo())
				addMensagemWarning("Atenção: selecione o arquivo do diploma para enviar novamente.");
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
	}
	
	/** Link para o formulário de registro de diploma individual.
	 * <br/>Método não invocado por JSP´s.
	 * @return
	 */
	public String formRegistroDiploma() {
		return forward("/diplomas/registro_diplomas/registro_unico.jsp");
	}
	
	/** Link para o fomulário de registro de diploma antigo.
	 * <br/>Método não invocado por JSP´s.
	 * @return
	 */
	public String formRegistroAntigo() {
		return forward("/diplomas/registro_diplomas/registro_antigo.jsp");
	}
	
	/** Valida os dados da operação.
	 * <br/>Método não invocado por JSP´s.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(java.util.Collection)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens mensagens) {
		if (!livro.isLivroAntigo()) {
			if (Sistema.isSipacAtivo() && Sistema.isProtocoloAtivo() && isEmpty(obj.getProcesso())) {
				mensagens.addErro("Número do Processo: Campo obrigatório não informado");
			} else if(!confirmaProcesso()) {
				mensagens.addErro("Número do Processo não encontrado. Verifique se o número foi digitado corretamente ou se está cadastrado no Sistema de Protocolo");
			}
		}
		// data de colação
		ValidatorUtil.validateRequired(obj.getDataColacao(), "Data de Colação", mensagens);
		// data de expedição
		ValidatorUtil.validateRequired(obj.getDataExpedicao(), "Data de Expedição", mensagens);
		// data de registro
		ValidatorUtil.validateRequired(obj.getDataRegistro(), "Data de Registro", mensagens);
		ValidatorUtil.validateRequiredId(this.livro.getId(), "Livro", mensagens);
		if (this.livro.isLivroAntigo()){
			ValidatorUtil.validateRequiredId(this.folha.getId(), "Folha", mensagens);
			ValidatorUtil.validateRequiredId(obj.getId(), "Ordem na Folha", mensagens);
			ValidatorUtil.validateMinValue(obj.getNumeroRegistro(), 1, "Número de Registro", mensagens);
		}
		return hasErrors();
	}
	
	/** Verifica se existe algum processo com o número informado.
	 * @return
	 */
	private boolean confirmaProcesso() {
		if (this.livro.isLivroAntigo()) {
			return true;
		} 
		ImpressaoDiplomaMBean mBean = getMBean("impressaoDiploma");
		return mBean.confirmaProcesso(obj.getProcesso());
	}

	/** Retorna a coleção de registros de diplomas da folha do livro. 
	 * @return
	 */
	public Collection<RegistroDiploma> getRegistrosDiplomas() {
		return registrosDiplomas;
	}

	/** Seta a coleção de registros de diplomas da folha do livro.
	 * @param registrosDiplomas
	 */
	public void setRegistrosDiplomas(Collection<RegistroDiploma> registrosDiplomas) {
		this.registrosDiplomas = registrosDiplomas;
	}

	/** Método invocado pela interface OperadorDiscente.
	 * <br/>Método não invocado por JSP´s.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */
	public String selecionaDiscente() throws ArqException {
		ValidatorUtil.validateRequired(obj.getDiscente(), "Discente", erros);
		if (hasErrors()) return null;
		Integer operacaoAtiva = (Integer) getCurrentSession().getAttribute("operacaoAtiva");
		boolean operacaoAlterar = operacaoAtiva != null && operacaoAtiva.equals(ArqListaComando.ALTERAR.getId());
		// verifica se há registro de diploma para o discente selecionado.
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
			if (obj.getDiscente().isGraduacao()) {
				// verifica se o discente possui data de colação de grau
				if (this.obj.getDiscente().getDataColacaoGrau() == null) {
					addMensagem(MensagensGraduacao.DISCENTE_SEM_DATA_DE_COLACAO);
					return null;
				}
				obj.setDataColacao(this.obj.getDiscente().getDataColacaoGrau());
				// verifica se há livro aberto para o registro do diploma
				LivroRegistroDiploma livro = livroDao.findByCurso(obj.getDiscente().getCurso().getId(), true, this.livro.isLivroAntigo());
				if (livro == null) {
					addMensagem(MensagensGraduacao.NAO_HA_LIVRO_ABERTO_PARA_REGISTRO_CURSO, obj.getDiscente().getCurso().getDescricao() + (this.livro.isLivroAntigo() ? " para registros antigos." : ""));
					return null;
				} 
				this.livro = livro;
			} else if (obj.getDiscente().isStricto() || obj.getDiscente().isLato()) {
					HomologacaoTrabalhoFinal homologacao = getDAO(HomologacaoTrabalhoFinalDao.class).findUltimoByDiscente(obj.getDiscente().getId());
					if (homologacao != null) {
						obj.setDataColacao(homologacao.getCriadoEm());
					} else { 
						// movimentação de saída
						MovimentacaoAluno movimentacaoSaida = getDAO(MovimentacaoAlunoDao.class).findUltimoAfastamentoByDiscente(obj.getDiscente().getId(), true, false);
						if (movimentacaoSaida != null) {
							obj.setDataColacao(movimentacaoSaida .getDataOcorrencia());
						} else {
							addMensagem(MensagensStrictoSensu.DISCENTE_SEM_DATA_HOMOLOGACAO_DIPLOMA, obj.getDiscente().getMatriculaNome());
							return null;
						}
					}
					// verifica se há livro aberto para o registro do diploma
					LivroRegistroDiploma livro = livroDao.findLivroAtivoPosByNivelEnsino(this.livro.isLivroAntigo(), obj.getDiscente().getNivel());
					if (livro == null) {
						addMensagem(MensagensStrictoSensu.NAO_HA_LIVRO_REGISTRO_DIPLOMA_ABERTO, (isLatoSensu() ? "Certificado" : "Diploma"));
						return null;
					} 
					this.livro = livro;
				}
			if (!this.livro.isLivroAntigo()) {
				// verifica se a data de colação é anterior ao início do registro de diploma no SIGAA
				int anoInicioRegistro = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.ANO_INICIO_REGISTRO_DIPLOMA);
				int semestreInicioRegistro = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.SEMESTRE_INICIO_REGISTRO_DIPLOMA);
				Date dataColacao = this.obj.getDataColacao();
				int ano = CalendarUtils.getAno(dataColacao);
				int semestre = CalendarUtils.getMesByData(dataColacao) / 7 + 1;
				if (ano * 10 + semestre < anoInicioRegistro * 10 + semestreInicioRegistro) {
					addMensagem(MensagensGraduacao.REGISTRO_DIPLOMA_ANTERIOR_AO_PERIODO_INICIO, anoInicioRegistro, semestreInicioRegistro);
					return null;
				}
			}
		}
		if (hasErrors())
			return null;
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		prepareMovimento(ArqListaComando.CADASTRAR);
		setConfirmButton("Cadastrar");
		if (this.livro.isLivroAntigo())
			return formRegistroAntigo();
		else
			return formRegistroDiploma();
	}
	
	/** Seta o discente que terá o diploma registrado.
	 * <br/>Método não invocado por JSP´s.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 */
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		this.obj.setDiscente(discente.getDiscente());
	}

	/**
	 * Inicia o registro de diploma antigo (anterior ao registro automático no
	 * SIGAA).<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/menus/registro.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String preCadastrarRegistroAntigo() throws ArqException, NegocioException {
		String retorno = preCadastrar();
		livro.setLivroAntigo(true);
		return retorno;
	}
	
	/** Retorna uma coleção de SelecItem de livros de acordo com o caso de uso.
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
	
	/** Retorna uma coleção de SelectItem de folhas com registro livre no livro.
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
	
	/** Retorna uma coleção de SelectItem de registros da folha.
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
				// exclui as posições onde há registro de diploma, exceto para lato sensu, que possui livro com vários registros por folha
				if (registro.isLivre() || registro.getLivroRegistroDiploma().isLatoSensu()) {
					SelectItem item = new SelectItem(registro.getId(), String.valueOf(i));
					itens.add(item);
				}
			}
		}
		return itens;
	}

	/** Retorna o livro onde o diploma será registrado.  
	 * @return
	 */
	public LivroRegistroDiploma getLivro() {
		return livro;
	}

	/** Seta o livro onde o diploma será registrado.
	 * @param livro
	 */
	public void setLivro(LivroRegistroDiploma livro) {
		this.livro = livro;
	}

	/** Retorna a folha onde o diploma será registrado. 
	 * @return
	 */
	public FolhaRegistroDiploma getFolha() {
		return folha;
	}

	/** Seta a folha onde o diploma será registrado.
	 * @param folha
	 */
	public void setFolha(FolhaRegistroDiploma folha) {
		this.folha = folha;
	}

	/** Retorna a observação acerca do registro de diploma.
	 * @return Observação acerca do registro de diploma.
	 */
	public ObservacaoRegistroDiploma getObservacao() {
		return observacao;
	}

	/** Seta a observação acerca do registro de diploma.
	 * @param observacao Observação acerca do registro de diploma.
	 */
	public void setObservacao(ObservacaoRegistroDiploma observacao) {
		this.observacao = observacao;
	}

	/** Verifica as permissões do usuário.
	 * <br/>Método não invocado por JSP´s.
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
}
