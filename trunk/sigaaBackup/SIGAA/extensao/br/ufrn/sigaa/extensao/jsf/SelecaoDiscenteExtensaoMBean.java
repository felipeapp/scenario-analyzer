/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/02/2009
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.extensao.InscricaoSelecaoExtensaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.DadosAluno;
import br.ufrn.sigaa.extensao.dominio.AreaTematica;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoSelecaoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoSituacaoDiscenteExtensao;
import br.ufrn.sigaa.extensao.negocio.DiscenteExtensaoValidator;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.portal.jsf.PortalDiscenteMBean;

/**
 * MBean responsável por realizar as operações sobre as inscrições para seleção
 * de Discentes de Extensão
 * 
 * @author Ilueny Santos
 * 
 */
@Component("selecaoDiscenteExtensao") @Scope("session")
public class SelecaoDiscenteExtensaoMBean extends SigaaAbstractController<InscricaoSelecaoExtensao> {

	/** Utilizado para armazenar informações de uma consulta ao banco. */
	private Collection<AtividadeExtensao> atividades = new ArrayList<AtividadeExtensao>();

	/** Utilizado para armazenar uma atividade de extesao. */
	private AtividadeExtensao atividade = new AtividadeExtensao();

	/** Utilizado para armazenar informações de uma consulta ao banco. */
	private Collection<InscricaoSelecaoExtensao> inscricoes = new ArrayList<InscricaoSelecaoExtensao>();

	/** Utilizado para armazenar informação inserida em tela de busca. */
	private AreaTematica areaTematica = new AreaTematica();

	/** Utilizado para informar se o usuário deseja efetuar uma busca utilizando como opção determinado critério. */
	private boolean checkBuscaPalavraChave;
	
	/** Utilizado para armazenar informacao inserida em tela de busca.  */
	private String buscaPalavraChave;

	/** Utilizado para informar se o usuário deseja efetuar uma busca utilizando como opção determinado critério. */
	private boolean checkBuscaUnidadeProponente;

	/** Utilizado para armazenar informacao inserida em tela de busca.  */
	private Integer buscaUnidade;

	/** Utilizado para informar se o usuário deseja efetuar uma busca utilizando como opção determinado critério. */
	private boolean checkBuscaAreaTematicaPrincipal;

	/** Utilizado para armazenar informação inserida em tela de busca. */
	private Integer buscaAreaTematicaPrincipal;

	/** Utilizado para informar se o usuário deseja efetuar uma busca utilizando como opção determinado critério. */
	private boolean checkBuscaCentro;

	/** Utilizado para armazenar informação inserida em tela de busca. */
	private Integer buscaCentro;
	
	/** Utilizado para armazenar informação sobre um aluno. */ 
	private DadosAluno dados; 

	
	public SelecaoDiscenteExtensaoMBean() {
		obj = new InscricaoSelecaoExtensao();
		
	}

	/**
	 * Realiza as validações necessárias e inicia o caso de uso onde o aluno se
	 * inscreve para a seleção de uma atividade de extensão
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/extensao/DiscenteExtensao/inscricao_discente.jsp</li>
	 *  <li>/sigaa.war/graduacao/agregador_bolsa/busca.jsp</li>
	 *  <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws RemoteException
	 * @throws ArqException
	 */
	public String iniciarInscricaoDiscente() throws ArqException {
		resetBean();
		
		// Verificando permissões
		if (getDiscenteUsuario() == null) {
			addMensagemErro("Você não tem acesso a esta operação.");
			return null;
		}

		return forward("/graduacao/agregador_bolsa/busca.jsp");

	}

	/**
	 * Seleciona a ação de extensão e realiza a validação verificando se o
	 * discente já esta inscrito na seleção desta ação. Se o discente passar
	 * na validação ele é redirecionado para o formulário de inscrição
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/extensao/DiscenteExtensao/lista_atividades.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String selecionarAtividade() throws ArqException {

		int id = getParameterInt("id");
		atividade = getGenericDAO().findByPrimaryKey(id,
				AtividadeExtensao.class);

		// evitar erro de lazy na tela de inscrição da seleção do discente
		if (atividade.getUnidadesProponentes() != null)
			atividade.getUnidadesProponentes().iterator();
		
		ListaMensagens lista = new ListaMensagens();
		DiscenteExtensaoValidator.validaInscricaoSelecao(getDiscenteUsuario(),	atividade, lista);

		if (lista.isEmpty()) {
			return forward(ConstantesNavegacao.DISCENTEEXTENSAO_INSCRICAO_DISCENTE);
		} else {
			addMensagens(lista);
		}

		return null;
	}

	/**
	 * Seleciona a ação de extensão e realiza a validação verificando se o
	 * discente já está inscrito na seleção desta ação. Se o discente passar
	 * na validação ele é redirecionado para o formulário de inscrição
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *  Não invocado por jsp.
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String selecionarAtividade(int id) throws ArqException {
		
		atividade = getGenericDAO().findByPrimaryKey(id,
				AtividadeExtensao.class);
		setDados(new DadosAluno());
		if (isEmpty(atividade)) {
			addMensagemErro("Atividade de extensão não encontrada");
			return null;
		}

		// evitar erro de lazy na tela de inscrição da seleção do discente
		if (atividade.getUnidadesProponentes() != null)
			atividade.getUnidadesProponentes().iterator();
		
		ListaMensagens lista = new ListaMensagens();
		DiscenteExtensaoValidator.validaInscricaoSelecao(getDiscenteUsuario(),	atividade, lista);

		if (lista.isEmpty()) {
			return forward(ConstantesNavegacao.DISCENTEEXTENSAO_INSCRICAO_DISCENTE);
		} else
			addMensagens(lista);

		return null;
	}
	
	/**
	 * Recadastra interesse do discente em uma bolsa de extensão.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Não invocado por JSP's</li>
	 * </ul>
	 * @param inscricaoSelecao
	 * @return
	 */
	public void recadastrarInteresse(InscricaoSelecaoExtensao inscricaoSelecao){
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(inscricaoSelecao);
		mov.setCodMovimento(SigaaListaComando.RECADASTRAR_INTERESSE_BOLSA_EXTENSAO);
		
		try {

			execute(mov);

			if (getUsuarioLogado().getEmail() != null) {

				// enviando e-mail para o candidato
				MailBody email = new MailBody();
				email.setAssunto("Inscrição para seleção em Ação de Extensão");
				email.setContentType(MailBody.TEXT_PLAN);
				email.setNome(getUsuarioLogado().getNome());
				email.setEmail(getUsuarioLogado().getEmail());

				StringBuffer msg = new StringBuffer();
				msg.append("Sua inscrição no processo seletivo da ação de extensão'"
						+ atividade.getCodigoTitulo() + "' foi confirmada com sucesso!");
				msg.append("\n\nAguarde o envio de informações mais detalhadas pelo(a) coordenador(a) da ação sobre o processo seletivo.\n");

				email.setMensagem(msg.toString());
				Mail.send(email);

			}

		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
		} catch (ArqException e) {
			addMensagemErro(e.getMessage());
			e.printStackTrace();
		}

		addMessage("Operação realizada com sucesso.\n"
				+ "E-mail de confirmação enviado para: "
				+ getUsuarioLogado().getEmail(), TipoMensagemUFRN.INFORMATION);

	}
	
	/**
	 * Realiza a inscrição do discente para a seleção do projeto solicitado. Um
	 * e-mail é enviado para o discente quando a inscrição é realizada com
	 * sucesso.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/extensao/DiscenteExtensao/inscricao_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public String realizarInscricaoDiscente() throws ArqException, NegocioException {
		
		InscricaoSelecaoExtensaoDao dao = DAOFactory.getInstance().getDAO(InscricaoSelecaoExtensaoDao.class);
		InscricaoSelecaoExtensao inscricaoSelecao = dao.findByDiscenteAtividade( getDiscenteUsuario().getDiscente().getId(), atividade.getId());
		if (inscricaoSelecao != null && inscricaoSelecao.getId() != 0){
			recadastrarInteresse(inscricaoSelecao);
			return forward(PortalDiscenteMBean.PORTAL_DISCENTE);
		}

			InscricaoSelecaoExtensao inscricao = new InscricaoSelecaoExtensao();
			inscricao.setDiscente(getDiscenteUsuario().getDiscente());
			inscricao.setAtividade(atividade);
			inscricao.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
			inscricao.setDataCadastro(new Date());
			inscricao.setAtivo(true);
			
			//valida dados do aluno
			ValidatorUtil.validateEmailRequired(getDados().getEmail(), "Email", erros);
			ValidatorUtil.validateRequired(getDados().getTelefone(), "Telefone", erros);
			ValidatorUtil.validateRequired(getDados().getQualificacoes(), "Qualificações", erros);
			
			if(!erros.isEmpty()){
				addMensagens(erros);
				return null;
			}
			
			inscricao.setDados(getDados());
			inscricao.setSituacaoDiscenteExtensao(new TipoSituacaoDiscenteExtensao(
					TipoSituacaoDiscenteExtensao.INSCRITO_PROCESSO_SELETIVO));
	
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(inscricao);
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_INTERESSE_BOLSA_EXTENSAO);
			prepareMovimento(SigaaListaComando.CADASTRAR_INTERESSE_BOLSA_EXTENSAO);
		
		try {

			execute(mov);
			
			//enviando email para o coordenador.
			if (atividade.getCoordenacao() != null) {

					String cabecalho = new String();
					String mensagem = new String();
					String assunto = new String();
					String texto = new String();
					
					Pessoa coordenador = atividade.getCoordenacao().getPessoa();
					
					assunto = "Interesse de aluno em Ação de Extensão";
					cabecalho = "Caro(a) "+ coordenador.getNome().toUpperCase() + ",\n\n";
					texto = "O discente "+ getUsuarioLogado().getNome() + " registrou-se como interessado(a) na ação de extensão " + atividade.getTitulo() + ".\n";
					texto += "\nPara visualizar todos os discentes interessados, acesse: ";
					texto += RepositorioDadosInstitucionais.get("siglaSigaa") + " -> Portal do Docente -> Extensão -> Planos de Trabalho -> Indicar/Substituir Discente";
					
					texto += "\n\nEste e-mail foi enviado automaticamente pelo sistema e não deve ser respondido.\n";
					mensagem = cabecalho+texto;
					
					MailBody body = new MailBody();
						
					body.setAssunto(assunto);
					body.setMensagem(mensagem);
					body.setEmail(coordenador.getEmail());
					body.setContentType(MailBody.TEXT_PLAN);
					Mail.send(body);		
			}

			if (getUsuarioLogado().getEmail() != null) {

				// enviando e-mail para o candidato
				MailBody email = new MailBody();
				email.setAssunto("Inscrição para seleção em Ação de Extensão");
				email.setContentType(MailBody.TEXT_PLAN);
				email.setNome(getUsuarioLogado().getNome());
				email.setEmail(getUsuarioLogado().getEmail());

				StringBuffer msg = new StringBuffer();
				msg.append("Caro(a) " + getUsuarioLogado().getNome() +", \n\n");
				msg.append("Sua inscrição no processo seletivo da ação de extensão '"
								+ atividade.getCodigoTitulo()
								+ "' foi confirmada com sucesso!");
				msg.append("\n\nAguarde o envio de informações mais detalhadas pelo(a) coordenador(a) da ação sobre o processo seletivo.\n");
				msg.append("\n\nEste e-mail foi enviado automaticamente pelo sistema e não deve ser respondido.\n");

				email.setMensagem(msg.toString());
				Mail.send(email);

			}

		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
		} catch (ArqException e) {
			addMensagemErro(e.getMessage());
			e.printStackTrace();
		}

		addMessage("Operação realizada com sucesso.\n"
				+ "E-mail de confirmação enviado para: "
				+ getUsuarioLogado().getEmail(), TipoMensagemUFRN.INFORMATION);

		return forward(PortalDiscenteMBean.PORTAL_DISCENTE);
	}

	/**
	 * Carrega a lista de ações nas quais o discente fez a inscrição na seleção
	 * para que ele possa escolher um para visualizar os resultados.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>JSP: /sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarVisualizarResultados() throws ArqException {

		if (getDiscenteUsuario() == null) {
			addMensagemErro("Você não tem acesso a esta operação.");
		}

		DiscenteAdapter d = getDiscenteUsuario();

		inscricoes = getGenericDAO().findByExactField(
				InscricaoSelecaoExtensao.class, "discente.id", d.getId(),
				"asc", "atividade.projeto.titulo");

		if (inscricoes == null || inscricoes.size() == 0) {
			addMensagemErro("Você não participou de nenhum processo de seletivo de extensão, portanto, não pode visualizar os resultados das seleções.");
			return null;
		}

		return forward(ConstantesNavegacao.DISCENTEEXTENSAO_VISUALIZAR_RESULTADO_SELECAO_LISTA_PROJETOS);

	}

	public AtividadeExtensao getAtividade() {
		return atividade;
	}

	public void setAtividade(AtividadeExtensao atividade) {
		this.atividade = atividade;
	}

	public AreaTematica getAreaTematica() {
		return areaTematica;
	}

	public void setAreaTematica(AreaTematica areaTematica) {
		this.areaTematica = areaTematica;
	}

	public Collection<InscricaoSelecaoExtensao> getInscricoes() {
		return inscricoes;
	}

	public void setInscricoes(Collection<InscricaoSelecaoExtensao> inscricoes) {
		this.inscricoes = inscricoes;
	}

	public Collection<AtividadeExtensao> getAtividades() {
		return atividades;
	}

	public void setAtividades(Collection<AtividadeExtensao> atividades) {
		this.atividades = atividades;
	}

	/**
	 * Localiza projeto na tela de situação do projeto
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/extensao/DiscenteExtensao/lista_atividades.jsp</li>
	 * </ul>
	 */
	public String localizar() {

		erros.getMensagens().clear();

		if (atividades != null) {
			atividades.clear();
		}

		/* Analisando filtros selecionados */
		String palavraChave = null;
		Integer idUnidadeProponente = null;
		Integer idCentro = null;
		Integer idAreaTematicaPrincipal = null;

		ListaMensagens lista = new ListaMensagens();

		// Definição dos filtros e validações
		if (checkBuscaPalavraChave) {
			palavraChave = buscaPalavraChave;
			ValidatorUtil.validateRequired(palavraChave, "Palavra chave", lista);
		}
		if (checkBuscaUnidadeProponente) {
			idUnidadeProponente = buscaUnidade;
			ValidatorUtil.validateRequiredId(idUnidadeProponente, "Unidade Proponente", lista);
		}
		if (checkBuscaAreaTematicaPrincipal) {
			idAreaTematicaPrincipal = buscaAreaTematicaPrincipal;
			ValidatorUtil.validateRequiredId(idAreaTematicaPrincipal, "Área Temática", lista);
		}

		if (checkBuscaCentro) {
			idCentro = buscaCentro;
			ValidatorUtil.validateRequiredId(idCentro, "Centro", lista);
		}

		if (!checkBuscaPalavraChave && !checkBuscaUnidadeProponente
				&& !checkBuscaAreaTematicaPrincipal && !checkBuscaCentro) {

			addMensagemErro("Selecione uma opção para efetuar a busca por ações de extensão.");

		} else {

			try {
				if (lista.isEmpty()) {
					InscricaoSelecaoExtensaoDao dao = getDAO(InscricaoSelecaoExtensaoDao.class);
					atividades = dao.localizarAcoesDisponiveis(palavraChave,
							idUnidadeProponente, idCentro,
							idAreaTematicaPrincipal);

				} else {
					addMensagens(lista);
				}

			} catch (DAOException e) {
				notifyError(e);
				addMensagemErro("Erro ao Buscar atividades!");
			}

		}
		return null;

	}

	public boolean isCheckBuscaPalavraChave() {
		return checkBuscaPalavraChave;
	}

	public void setCheckBuscaPalavraChave(boolean checkBuscaPalavraChave) {
		this.checkBuscaPalavraChave = checkBuscaPalavraChave;
	}

	public String getBuscaPalavraChave() {
		return buscaPalavraChave;
	}

	public void setBuscaPalavraChave(String buscaPalavraChave) {
		this.buscaPalavraChave = buscaPalavraChave;
	}

	public boolean isCheckBuscaUnidadeProponente() {
		return checkBuscaUnidadeProponente;
	}

	public void setCheckBuscaUnidadeProponente(boolean checkBuscaUnidadeProponente) {
		this.checkBuscaUnidadeProponente = checkBuscaUnidadeProponente;
	}

	public Integer getBuscaUnidade() {
		return buscaUnidade;
	}

	public void setBuscaUnidade(Integer buscaUnidade) {
		this.buscaUnidade = buscaUnidade;
	}

	public boolean isCheckBuscaAreaTematicaPrincipal() {
		return checkBuscaAreaTematicaPrincipal;
	}

	public void setCheckBuscaAreaTematicaPrincipal(
			boolean checkBuscaAreaTematicaPrincipal) {
		this.checkBuscaAreaTematicaPrincipal = checkBuscaAreaTematicaPrincipal;
	}

	public Integer getBuscaAreaTematicaPrincipal() {
		return buscaAreaTematicaPrincipal;
	}

	public void setBuscaAreaTematicaPrincipal(Integer buscaAreaTematicaPrincipal) {
		this.buscaAreaTematicaPrincipal = buscaAreaTematicaPrincipal;
	}

	public boolean isCheckBuscaCentro() {
		return checkBuscaCentro;
	}

	public void setCheckBuscaCentro(boolean checkBuscaCentro) {
		this.checkBuscaCentro = checkBuscaCentro;
	}

	public Integer getBuscaCentro() {
		return buscaCentro;
	}

	public void setBuscaCentro(Integer buscaCentro) {
		this.buscaCentro = buscaCentro;
	}

	public DadosAluno getDados() {
		return dados;
	}

	public void setDados(DadosAluno dados) {
		this.dados = dados;
	}

}
