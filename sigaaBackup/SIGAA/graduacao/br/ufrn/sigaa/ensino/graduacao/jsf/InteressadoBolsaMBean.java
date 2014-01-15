/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 01/04/2009
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.caixa_postal.Mensagem;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.comum.negocio.MovimentoPerfilPessoa;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.graduacao.AgregadorBolsasDao;
import br.ufrn.sigaa.arq.dao.graduacao.InteressadoBolsaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.jsf.AdesaoCadastroUnicoBolsaFluxoNavegacao;
import br.ufrn.sigaa.assistencia.jsf.AdesaoCadastroUnicoBolsaMBean;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.graduacao.dominio.AgregadorBolsas;
import br.ufrn.sigaa.ensino.graduacao.dominio.DadosAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.InteressadoBolsa;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoInteressadoBolsa;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoAgregadorBolsas;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.jsf.SelecaoDiscenteExtensaoMBean;
import br.ufrn.sigaa.monitoria.dominio.ProvaSelecao;
import br.ufrn.sigaa.parametros.dominio.ParametrosSAE;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.portal.jsf.PerfilDiscenteMBean;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.jsf.SelecaoDiscenteProjetoMBean;

/**
 * MBean respons�vel por gerenciar a inscri��o de um aluno nos diversos tipos de bolsa.
 * 
 * @author Henrique Andr�
 *
 */
@Component("interessadoBolsa") @Scope("session")
public class InteressadoBolsaMBean extends SigaaAbstractController<InteressadoBolsa> implements AdesaoCadastroUnicoBolsaFluxoNavegacao {
	
	/** Guada dados sobre o interresado pela bolsa. */
	private InteressadoBolsa interessadoBolsa;
	
	/** Constante que representa Extensao. */
	private static final int EXTENSAO = 2;
	
	/** Constante que representa Pesquisa. */
	private static final int PESQUISA = 3;
	
	/** Constante que representa Apoio Tecnico. */
	private static final int APOIO_TECNICO = 4;
	
	/** Constante que representa Acao associada. */
	private static final int ACAO_ASSOCIADA = 5;

	/** Destinat�rio da mensagem. */
	private Usuario destinarioMensagem;
	
	/** Lista de inscricoes. */
	private List<AgregadorBolsas> inscricoes = new ArrayList<AgregadorBolsas>();
	
	/** Link para a tela de inscricao. */
	private static final String TELA_INSCRICAO = "/graduacao/agregador_bolsa/inscricao_oportunidade.jsp";
	
	/** Agregadorbolsas que guarda a bolsa selecionada. */
	private AgregadorBolsas agregadorBolsa;
	
	/** Perfil do interessado pela bolsa. */
	private PerfilPessoa perfil;
	
	/** O id da bolsa. */
	private Integer idOportunidade;
	
	/** O id do Usuario responsavel pela bolsa. */
	private Integer idUsuario;
	
	/** Tipo de bolsa. */
	private Integer tipo;
	
	/**
	 * N�o pega os par�metros vindos da JSP. Apenas valida e inicia, supondo que eles j� foram setados em outra etapa.
	 * 
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	private String reIniciarCadastrarInteresse() throws DAOException, ArqException {
		return validarAndIniciar();
	}	
	
	/**
	 * Redirecionado para o formul�rio de Registro de Interesse.
	 * No formul�rio o aluno digita os dados e submete ao respons�vel do projeto
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>JSP: /sigaa.war/graduacao/agregador_bolsa/inscricao_oportunidade.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastrarInteresse() throws ArqException {
		capturarParametros();
		return validarAndIniciar();
	}

	/**
	 * Realiza valida��es e iniciar o registro de interesse.
	 * 
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	private String validarAndIniciar() throws DAOException, ArqException {
		
		if (!getDiscenteUsuario().isAtivo()) {
			addMensagemErro("Somente alunos ativos podem registrar interesse em bolsa.");
			return null;
		}
		
		if (getDiscenteUsuario().isGraduacao() && getDiscenteUsuario().getStatus() == StatusDiscente.GRADUANDO) {
			addMensagemErro("N�o � poss�vel registrar interesse em bolsa, para alunos com status GRADUANDO.");
			return null;
		}

		if (!isCadastroUnico()) {
			addMensagemWarning("Caro aluno, voc� foi redirecionado para a tela de Ades�o ao Cadastro �nico. Somente depois da ades�o voc� poder� registrar interesse nas bolsas.");
			AdesaoCadastroUnicoBolsaMBean mBean = getMBean("adesaoCadastroUnico");
			mBean.setMBeanFluxo(this);
			return mBean.apresentacaoCadastroUnico();
		}
		
		if (ValidatorUtil.isEmpty(tipo)) {
			addMensagemErro("Ocorreu um erro ao tentar detectar este tipo de bolsa, entre em contato com o suporte.");
			return null;
		}
		
		if (tipo.equals(TipoInteressadoBolsa.APOIO_TECNICO.getId())){
			int diaHoje = CalendarUtils.getDiaByData(new Date());
			int diaInicialPeriodoPermitido = Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosSAE.DIA_INICIAL_REGISTRO_INTERESSE_BOLSA_APOIO_TECNICO));
			int diaFinalPeriodoPermitido = Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosSAE.DIA_FINAL_REGISTRO_INTERESSE_BOLSA_APOIO_TECNICO)); 
			if ( (diaHoje < diaInicialPeriodoPermitido) || (diaHoje > diaFinalPeriodoPermitido )){			
					addMensagemErro("O per�odo de demonstra��o de interesse em bolsas de apoio t�cnico, por parte do discente," +
							" � do dia "+ diaInicialPeriodoPermitido+" ao dia "+diaFinalPeriodoPermitido+" de cada m�s." +
							" Hoje � dia "+diaHoje+". Tente novamente no pr�ximo m�s, no per�odo permitido.");
					return null;
			}
		}
		
		clear();
		
		switch (tipo) {
		case EXTENSAO:
			iniciarInscricaoExtensao();
			break;
		case PESQUISA:
			iniciarInscricaoGenerico(TipoInteressadoBolsa.PESQUISA);
			break;
		case APOIO_TECNICO:
			iniciarInscricaoGenerico(TipoInteressadoBolsa.APOIO_TECNICO);
			break;
		case ACAO_ASSOCIADA:
			iniciarInscricaoAssociada();
			break;
		default:
			addMensagemErro("Selecione um tipo");
			break;
		}
		
		return null;		
		
	}

	/**
	 * Pega os par�metros que foram passados da jsp
	 */
	private void capturarParametros() {
		idOportunidade = getParameterInt("idOportunidade");
		idUsuario = getParameterInt("idUsuario");
		tipo = getParameterInt("idTipoBolsa");
	}

	/**
	 * Limpa o objeto
	 */
	private void clear() {
		obj = new InteressadoBolsa();
		obj.setDados(new DadosAluno());
		agregadorBolsa = new AgregadorBolsas();
	}

	/**
	 * Transfere a responsabilidade da inscri��o de extens�o para o MBean SelecaoDiscenteExtensaoMBean
	 * 
	 * @see SelecaoDiscenteExtensaoMBean
	 * @throws ArqException
	 */
	private void iniciarInscricaoExtensao() throws ArqException {
		SelecaoDiscenteExtensaoMBean selecaoExtensao = getMBean("selecaoDiscenteExtensao");
		selecaoExtensao.selecionarAtividade(idOportunidade);
	}
	
	/**
	 * Transfere a responsabilidade da inscri��o de extens�o para o MBean SelecaoDiscenteExtensaoMBean
	 * 
	 * @see SelecaoDiscenteExtensaoMBean
	 * @throws ArqException
	 */
	private void iniciarInscricaoAssociada() throws ArqException {
		//TODO: selecaoProjeto em associadas 
		SelecaoDiscenteProjetoMBean selecaoProjeto = getMBean("selecaoDiscenteProjeto");
		selecaoProjeto.selecionarProjeto(idOportunidade);
		setOperacaoAtiva(SigaaListaComando.RECADASTRAR_INTERESSE_BOLSA_ASSOCIADA.getId());
	}

	/**
	 * Caso seja uma bolsa de pesquisa ou apoio t�cnico, � utilizado o mesmo mecanismo de inscri��o
	 * 
	 * @param tipoBolsa
	 * @return
	 * @throws ArqException
	 */
	private String iniciarInscricaoGenerico(TipoInteressadoBolsa tipoBolsa)
			throws ArqException {

		prepareMovimento(ArqListaComando.ATUALIZAR_PERFIL);
		
		PerfilDiscenteMBean mBeanPerfil = getMBean("perfilDiscente");
		perfil = mBeanPerfil.getPerfilUsuario();
		
		if (perfil == null)
			perfil = new PerfilPessoa();
		
		InteressadoBolsaDao dao = getDAO(InteressadoBolsaDao.class);
		
		prepareMovimento(SigaaListaComando.CADASTRAR_INTERESSE_BOLSA);

		InteressadoBolsa interesse = dao.findByDiscente(idOportunidade, tipoBolsa.getId(), getDiscenteUsuario().getId());
		
		if (!isEmpty(interesse)) {
			addMensagemErro("Voc� n�o pode realizar a inscri��o mais de uma vez na mesma bolsa.");
			return null;
		}
		
		AgregadorBolsasMBean mBean = getMBean("agregadorBolsas");
		
		if (isEmpty(mBean) || isEmpty(mBean.getResultado())) {
			addMensagemErroPadrao();
			return cancelar();
		}
		
		for (AgregadorBolsas ag : mBean.getResultado()) {
			if (ag.getId().equals(idOportunidade)) {
				agregadorBolsa = ag;
				break;
			}
		}
		
		
		destinarioMensagem = getGenericDAO().findByPrimaryKey(idUsuario, Usuario.class);

		obj.setIdEstagio(idOportunidade);
		obj.setIdUsuario(getUsuarioLogado().getId());
		obj.setTipoBolsa(tipoBolsa);
		obj.setDiscente(getDiscenteUsuario().getDiscente());
		return forward(TELA_INSCRICAO);
	}
	
	/**
	 * Persiste a inscri��o do aluno
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>JSP: /sigaa.war/graduacao/agregador_bolsa/inscricao_oportunidade.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String finalizarInscricaoInteresse() throws ArqException {
		
		validarInteresseAluno();
		
		if (hasErrors()) {
			return null;
		}
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_INTERESSE_BOLSA);
		
		try {
			gravarPerfil();
			execute(mov);
			enviarInteresseResponsavel();
			enviarEmailResponsavel();
			addMensagemInformation("Sua inscri��o foi realizada com sucesso.");
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		return cancelar();
	}

	/**
	 * Grava o perfil do aluno
	 * 
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	private void gravarPerfil() throws ArqException, NegocioException {
		
		PerfilDiscenteMBean mBean = getMBean("perfilDiscente");
		
		mBean.setTipoPerfil(perfil);

		MovimentoPerfilPessoa perfilMov = new MovimentoPerfilPessoa();
		perfilMov.setCodMovimento(ArqListaComando.ATUALIZAR_PERFIL);
		perfilMov.setPerfil(perfil);
		
		execute(perfilMov, getCurrentRequest());

		mBean.setPerfilPortal(perfil);
	}

	/**
	 * Valida os dados que vieram do formul�rio de registro de interesse
	 *  
	 * @param erros
	 */
	private void validarInteresseAluno() {
		ValidatorUtil.validateRequired(perfil.getDescricao(), "Descri��o", erros);
		ValidatorUtil.validateRequired(perfil.getAreas(), "�reas de Interesse", erros);
		
		ValidatorUtil.validateRequired(obj.getDados().getQualificacoes(), "Qualifica��es", erros);
	}	
	
	/**
	 * Envia uma mensagem ao respons�vel do projeto
	 */
	private void enviarInteresseResponsavel() throws ArqException {
		prepareMovimento(SigaaListaComando.ENVIAR_MENSAGEM_COORDENAOR);
		Mensagem mensagem = criarMensagem();
		
		PessoaDao pessoaDao = getDAO(PessoaDao.class);
		Pessoa pessoa = pessoaDao.findCompleto(getUsuarioLogado().getPessoa().getId());
		
		StringBuilder sb = new StringBuilder();
		sb.append("\nDados do Aluno");
		sb.append("\n-------------------------");
		sb.append("\nEmail: " + getUsuarioLogado().getEmail());
		sb.append("\nTelefone: " + pessoa.getTelefone());
		sb.append("\nQualifica��o:" + obj.getDados().getQualificacoes());
		sb.append("\nLattes:" + perfil.getEnderecoLattes());

		if (obj.isPesquisa()) {
			sb.append("\n");
			sb.append("\nDados do Plano de Trabalho");
			sb.append("\n-------------------------");
			PlanoTrabalho pt = getGenericDAO().findByPrimaryKey(obj.getIdEstagio(), PlanoTrabalho.class);
			sb.append("\nProjeto Pesquisa: " + pt.getProjetoPesquisa().getCodigoTitulo() );
			sb.append("\nPlano Trabalho: " + pt.getTitulo() );
		}
		
		sb.append("\n\nEste e-mail foi enviado automaticamente pelo sistema e n�o deve ser respondido.\n");
		
		mensagem.setTitulo("Interesse de aluno em projeto");
		mensagem.setMensagem(sb.toString());
		
		enviarMensagem(mensagem);
	}
	
	/**
	 * Cria a mensagem a ser enviada
	 */
	private Mensagem criarMensagem() {
		Mensagem mensagem = new Mensagem();
		mensagem.setAutomatica(false);
		mensagem.setUsuarioDestino(destinarioMensagem);
		mensagem.setRemetente(getUsuarioLogado());
		mensagem.setLeituraObrigatoria(true);
		mensagem.setUsuario(getUsuarioLogado());
		
		return mensagem;
	}
	
	/**
	 * Envia mensagem ao servidor
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>JSP: N�o invocado por JSP</li>
	 * </ul>
	 * @param mensagem 
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String enviarMensagem(Mensagem mensagem) throws ArqException {
		
		MovimentoAgregadorBolsas mov = new MovimentoAgregadorBolsas();
		mov.setCodMovimento(SigaaListaComando.ENVIAR_MENSAGEM_COORDENAOR);
		mov.setMensagem(mensagem);
		try {
			execute(mov, getCurrentRequest());
			addMensagemInformation("Mensagem enviada ao respons�vel.");
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
		}
		
		return null;
	}
	
	
	/**
	 * Envia um email para o reponsavel do projeto.
	 */
	private void enviarEmailResponsavel() {
		
		String cabecalho = new String();
		String mensagem = new String();
		String assunto = new String();
		String texto = new String();
		
		
			if ( destinarioMensagem != null ){
				assunto = "Interesse de aluno em projeto";
				cabecalho = "Caro(a) "+ destinarioMensagem.getNome().toUpperCase() + ",\n\n";
				texto = "O discente "+ getUsuarioLogado().getNome() + " registrou-se como interessado(a) no projeto " + getAgregadorBolsa().getDescricao() +".\n\n";
				
				texto += "\nPara visualizar todos os discentes interessados, acesse: "; 
				
				if ( obj.isPesquisa() )
					texto += RepositorioDadosInstitucionais.get("siglaSigaa") + " -> Portal do Docente -> Pesquisa -> Planos de Trabalho -> Indicar/Substituir Bolsista";
				
				if ( obj.isExtensao() )
					texto += RepositorioDadosInstitucionais.get("siglaSigaa") + " -> Portal do Docente -> Extens�o -> Planos de Trabalho -> Indicar/Substituir Discente";

				if ( obj.isMonitoria() )
					texto += RepositorioDadosInstitucionais.get("siglaSigaa") + " -> Portal do Docente -> Ensino -> Projetos -> Projeto de Monitoria -> Coordena��o de Projeto -> Processo Seletivo";
				
				texto += "\n\nEste e-mail foi enviado automaticamente pelo sistema e n�o deve ser respondido.\n";
				
				mensagem = cabecalho+texto;
				
				MailBody body = new MailBody();
					
				body.setAssunto(assunto);
				body.setMensagem(mensagem);
				body.setEmail(destinarioMensagem.getEmail());
				body.setContentType(MailBody.TEXT_PLAN);
				Mail.send(body);
			}	
		
		
	}

	/**
	 * P�gina que mostra o andamento das bolsas que o aluno registrou interesse
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>JSP: /sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String acompanharInscricoes() throws DAOException {
		
		inscricoes = new ArrayList<AgregadorBolsas>();
		
		InteressadoBolsaDao dao = getDAO(InteressadoBolsaDao.class);
		List<InteressadoBolsa> todosInteresses = dao.findAllByDiscente(getDiscenteUsuario().getId());
		
		if (ValidatorUtil.isEmpty(todosInteresses)) {
			addMensagemErro("Caro aluno, nenhum registro de Interesse foi localizado.");
			return null;
		}
		
		AgregadorBolsasDao agregadorBolsasDao = getDAO(AgregadorBolsasDao.class);
		
		for (InteressadoBolsa interessadoBolsa : todosInteresses) {
			AgregadorBolsas agregadorBolsas = new AgregadorBolsas();
			
			if (interessadoBolsa.getTipoBolsa().equals(TipoInteressadoBolsa.PESQUISA)) {
				PlanoTrabalho pt = dao.findByPrimaryKey(interessadoBolsa.getIdEstagio(), PlanoTrabalho.class);
				if (isEmpty(pt))
					continue;
				agregadorBolsas = new AgregadorBolsas(pt);
			} else if(interessadoBolsa.getTipoBolsa().equals(TipoInteressadoBolsa.APOIO_TECNICO)) {
				agregadorBolsas = agregadorBolsasDao.findApoioTecnicoById(interessadoBolsa.getIdEstagio());
				if (agregadorBolsas == null) {
					continue;
				}
				agregadorBolsas.setIdTipoBolsa(AgregadorBolsasMBean.APOIO_TECNICO);
			} else if (interessadoBolsa.getTipoBolsa().equals(TipoInteressadoBolsa.EXTENSAO)){
				AtividadeExtensao at = dao.findByPrimaryKey(interessadoBolsa.getIdEstagio(), AtividadeExtensao.class);
				if (isEmpty(at))
					continue;
				agregadorBolsas.setIdTipoBolsa(AgregadorBolsasMBean.EXTENSAO);
				agregadorBolsas = new AgregadorBolsas(at);
			} else if(interessadoBolsa.getTipoBolsa().equals(TipoInteressadoBolsa.MONITORIA)){
				ProvaSelecao ps = dao.findByPrimaryKey(interessadoBolsa.getIdEstagio(), ProvaSelecao.class);
				if (isEmpty(ps))
					continue;
				agregadorBolsas.setIdTipoBolsa(AgregadorBolsasMBean.MONITORIA);
				agregadorBolsas = new AgregadorBolsas(ps);
			} else if (interessadoBolsa.getTipoBolsa().equals(TipoInteressadoBolsa.ACAO_ASSOCIADA)){
				Projeto pro = dao.findByPrimaryKey(interessadoBolsa.getIdEstagio(), Projeto.class);
				if (isEmpty(pro))
					continue;
				agregadorBolsas.setIdTipoBolsa(AgregadorBolsasMBean.ACOES_ASSOCIADAS);
				agregadorBolsas = new AgregadorBolsas(pro);
			}
			
			inscricoes.add(agregadorBolsas);
		}
		
		popularUsuario(inscricoes);
		
		return forward("/graduacao/agregador_bolsa/acompanhamento_interesse/lista.jsp");
	}

	/**
	 * Popula o objeto com o usu�rio do respons�vel pelo projeto
	 * 
	 * @param res
	 * @throws DAOException
	 */
	private void popularUsuario(List<AgregadorBolsas> res) throws DAOException {
		UsuarioDao dao = getDAO(UsuarioDao.class);
		for (AgregadorBolsas ab : res) {
			if (ab.getIdUsuario() != null) 
				ab.setUsuario(dao.findByPrimaryKey(ab.getIdUsuario()));
			else if (ab.getResponsavelProjeto() != null) {
				ab.setUsuario(dao.findByServidorLeve(ab.getResponsavelProjeto()));
			}
		}
	}	

	/**
	 * Carrega as qualifica��es que o aluno preencheu quando se candidatou a bolsa no plano de trabalho
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/graduacao/agregador_bolsa/resumo_qualificacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String getVerQualificacao() throws DAOException {
		interessadoBolsa = new InteressadoBolsa();
		
		Integer idDiscente = getParameterInt("idAluno");
		Integer idPlanoTrabalho = getParameterInt("idPlanoTrabalho");
		
		InteressadoBolsaDao dao = getDAO(InteressadoBolsaDao.class);
		
		interessadoBolsa = dao.findByDiscente(idPlanoTrabalho, TipoInteressadoBolsa.PESQUISA.getId(), idDiscente);
		
		return null;
	}
	
	public List<AgregadorBolsas> getInscricoes() {
		return inscricoes;
	}

	public void setInscricoes(List<AgregadorBolsas> inscricoes) {
		this.inscricoes = inscricoes;
	}

	public AgregadorBolsas getAgregadorBolsa() {
		return agregadorBolsa;
	}

	public void setAgregadorBolsa(AgregadorBolsas agregadorBolsa) {
		this.agregadorBolsa = agregadorBolsa;
	}

	public PerfilPessoa getPerfil() {
		return perfil;
	}

	public void setPerfil(PerfilPessoa perfil) {
		this.perfil = perfil;
	}

	public InteressadoBolsa getInteressadoBolsa() {
		return interessadoBolsa;
	}

	public void setInteressadoBolsa(InteressadoBolsa interessadoBolsa) {
		this.interessadoBolsa = interessadoBolsa;
	}

	/**
	 *  Valida, Inicia o registo de interesse e pega a Url de destino.
	 */
	public String getUrlDestino() throws ArqException {
		return reIniciarCadastrarInteresse();
	}

}
