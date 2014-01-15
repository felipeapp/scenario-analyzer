package br.ufrn.sigaa.projetos.jsf;

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
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.projetos.InscricaoSelecaoProjetoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.graduacao.dominio.DadosAluno;
import br.ufrn.sigaa.extensao.dominio.AreaTematica;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.portal.jsf.PortalDiscenteMBean;
import br.ufrn.sigaa.projetos.dominio.InscricaoSelecaoProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoDiscenteProjeto;
import br.ufrn.sigaa.projetos.negocio.DiscenteProjetoValidator;

/**
 * MBean responsável por realizar as operações sobre as inscrições para seleção
 * de Discentes de Ações associadas
 * 
 * @author geyson
 * 
 */
@Component("selecaoDiscenteProjeto") @Scope("session")
public class SelecaoDiscenteProjetoMBean extends SigaaAbstractController<InscricaoSelecaoProjeto> {

	/** Utilizado para armazenar informações de uma consulta ao banco */
	private Collection<Projeto> projetos = new ArrayList<Projeto>();
	
	/** Projeto selecionado. */
	private Projeto projeto = new Projeto();

	/** Utilizado para armazenar informações de uma consulta ao banco */
	private Collection<InscricaoSelecaoProjeto> inscricoes = new ArrayList<InscricaoSelecaoProjeto>();

	/** Utilizado para armazenar informação inserida em tela de busca */
	private AreaTematica areaTematica = new AreaTematica();

	/** Utilizado para informar se o usuário deseja efetuar uma busca utilizando como opção determinado critério */
	private boolean checkBuscaPalavraChave;

	/** Utilizado para armazenar informação inserida em tela de busca */
	private String buscaPalavraChave;

	/** Utilizado para informar se o usuário deseja efetuar uma busca utilizando como opção determinado critério */
	private boolean checkBuscaUnidadeProponente;

	/** Utilizado para armazenar informação inserida em tela de busca */
	private Integer buscaUnidade;

	/** Utilizado para informar se o usuário deseja efetuar uma busca utilizando como opção determinado critério */
	private boolean checkBuscaAreaTematicaPrincipal;

	/** Utilizado para armazenar informação inserida em tela de busca */
	private Integer buscaAreaTematicaPrincipal;

	/** Utilizado para informar se o usuário deseja efetuar uma busca utilizando como opção determinado critério */
	private boolean checkBuscaCentro;

	/** Utilizado para armazenar informação inserida em tela de busca */
	private Integer buscaCentro;
	
	/** Utilizado para armazenar informação sobre um aluno */
	private DadosAluno dados; 
	
	public SelecaoDiscenteProjetoMBean() {
		obj = new InscricaoSelecaoProjeto();
	}
	
	/**
	 * Seleciona projeto e direciona discente para o formulário de inscrição
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *  Não invocado por jsp.
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String selecionarProjeto(int id) throws ArqException {
		
		projeto = getGenericDAO().findByPrimaryKey(id,
				Projeto.class);
		setDados(new DadosAluno());
		if (isEmpty(projeto)) {
			addMensagemErro("Ação Associada não encontrada");
			return null;
		}

		ListaMensagens lista = new ListaMensagens();
		DiscenteProjetoValidator.validaInscricaoSelecao(getDiscenteUsuario(), projeto, lista);

		if (lista.isEmpty()) {
			return forward(ConstantesNavegacaoProjetos.DISCENTE_PROJETO_INSCRICAO);
		} else
			addMensagens(lista);

		return null;
	}
	
	/**
	 * Realiza a inscrição do discente para a seleção do projeto solicitado. Um
	 * e-mail é enviado para o discente quando a inscrição é realizada com
	 * sucesso.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/extensao/DiscenteProjeto/inscricao_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public String realizarInscricaoDiscente() throws ArqException, NegocioException {
		
		if ( !checkOperacaoAtiva(SigaaListaComando.RECADASTRAR_INTERESSE_BOLSA_ASSOCIADA.getId()) ) {
			return cancelar();
		}
		
		InscricaoSelecaoProjetoDao dao = getDAO(InscricaoSelecaoProjetoDao.class);
		InscricaoSelecaoProjeto inscricaoSelecao = dao.findByDiscenteAtividade( getDiscenteUsuario().getDiscente().getId(), projeto.getId());
		if (inscricaoSelecao != null && inscricaoSelecao.getId() != 0){
			recadastrarInteresse(inscricaoSelecao);
			return forward(PortalDiscenteMBean.PORTAL_DISCENTE);
		}

			InscricaoSelecaoProjeto inscricao = new InscricaoSelecaoProjeto();
			inscricao.setDiscente(getDiscenteUsuario().getDiscente());
			inscricao.setProjeto(projeto);
			inscricao.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada().getId());
			inscricao.setDataCadastro(new Date());
			inscricao.setAtivo(true);
			
			//valida dados do aluno
			ValidatorUtil.validateEmailRequired(getDados().getEmail(), "Email", erros);
			ValidatorUtil.validateRequired(getDados().getTelefone(), "Telefone", erros);
			if(!getDados().getTelefone().isEmpty()){
				try{
					Integer.parseInt(getDados().getTelefone());
				}catch (Exception e) {
					erros.addErro("Telefone: Formato inválido.");
				}
			}
			ValidatorUtil.validateRequired(getDados().getQualificacoes(), "Qualificações", erros);
			
			if(!erros.isEmpty()){
				addMensagens(erros);
				return null;
			}
			
			inscricao.setDados(getDados());
			inscricao.setSituacaoDiscenteProjeto(new TipoSituacaoDiscenteProjeto(
					TipoSituacaoDiscenteProjeto.INSCRITO_PROCESSO_SELETIVO));
	
			prepareMovimento(SigaaListaComando.CADASTRAR_INTERESSE_BOLSA_ACAO_ASSOCIADA);
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(inscricao);
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_INTERESSE_BOLSA_ACAO_ASSOCIADA);
		
		try {

			execute(mov);

			//Enviando e-mail para o coordenador.
			if (projeto.getCoordenador() != null) {
				
				String cabecalho = new String();
				String mensagem = new String();
				String assunto = new String();
				String texto = new String();
				
				Pessoa coordenador = projeto.getCoordenador().getServidor().getPessoa();
				
				assunto = "Interesse de aluno em Ação Associada";
				cabecalho = "Caro(a) "+ coordenador.getNome().toUpperCase() + ",\n\n";
				texto = "O discente "+ getUsuarioLogado().getNome() + " registrou-se como interessado(a) na ação integrada " + projeto.getAnoTitulo() +". \n";
				texto += "\nPara visualizar todos os discentes interessados, acesse: ";
				texto += RepositorioDadosInstitucionais.get("siglaSigaa") + " -> Portal do Docente -> Ações Integradas -> Planos de Trabalho -> Indicar/Substituir Discente";
				
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
				email.setAssunto("Inscrição para seleção em Ação Associada");
				email.setContentType(MailBody.TEXT_PLAN);
				email.setNome(getUsuarioLogado().getNome());
				email.setEmail(getUsuarioLogado().getEmail());

				StringBuffer msg = new StringBuffer();
				msg.append("Caro(a) " + getUsuarioLogado().getNome() +", \n\n");
				msg.append("Sua inscrição no processo seletivo da Ação Associada '"
								+ projeto.getAnoTitulo()
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

		addMessage("Operação realizada com sucesso.", TipoMensagemUFRN.INFORMATION);
		addMessage("E-mail de confirmação enviado para: "
				+ getUsuarioLogado().getEmail(), TipoMensagemUFRN.INFORMATION);

		removeOperacaoAtiva();
		return forward(PortalDiscenteMBean.PORTAL_DISCENTE);
	}
	
	/**
	 * Recadastra interesse do discente em uma bolsa de ação associada.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Não invocado por JSP's</li>
	 * </ul>
	 * @param inscricaoSelecao
	 * @return
	 */
	public void recadastrarInteresse(InscricaoSelecaoProjeto inscricaoSelecao){
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(inscricaoSelecao);
		mov.setCodMovimento(SigaaListaComando.RECADASTRAR_INTERESSE_BOLSA_ASSOCIADA);
		
		try {
			
			prepareMovimento(SigaaListaComando.RECADASTRAR_INTERESSE_BOLSA_ASSOCIADA);
			execute(mov);

			if (getUsuarioLogado().getEmail() != null) {

				// enviando e-mail para o candidato
				MailBody email = new MailBody();
				email.setAssunto("Inscrição para seleção em Ação Associada");
				email.setContentType(MailBody.TEXT_PLAN);
				email.setNome(getUsuarioLogado().getNome());
				email.setEmail(getUsuarioLogado().getEmail());

				StringBuffer msg = new StringBuffer();
				msg
						.append("Sua inscrição no processo seletivo da ação de extensão'"
								+ projeto.getAnoTitulo()
								+ "' foi confirmada com sucesso!");
				msg
						.append("\n\nAguarde o envio de informações mais detalhadas pelo(a) coordenador(a) da ação sobre o processo seletivo.\n");

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

		addMessage("E-mail de confirmação enviado para: "
				+ getUsuarioLogado().getEmail(), TipoMensagemUFRN.INFORMATION);
		addMessage("Operação realizada com sucesso.", TipoMensagemUFRN.INFORMATION);
		
	}
	
	/**
	 * Realiza as validações necessárias e inicia o caso de uso onde o aluno se
	 * inscreve para a seleção de uma ação associada.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/projetos/DiscenteProjeto/inscricao_discente.jsp</li>
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
		setOperacaoAtiva(SigaaListaComando.RECADASTRAR_INTERESSE_BOLSA_ASSOCIADA.getId());
		
		// Verificando permissões
		if (getDiscenteUsuario() == null) {
			addMensagemErro("Você não tem acesso a esta operação.");
			return null;
		}

		return forward("/graduacao/agregador_bolsa/busca.jsp");

	}

	public Collection<Projeto> getProjetos() {
		return projetos;
	}

	public void setProjetos(Collection<Projeto> projetos) {
		this.projetos = projetos;
	}

	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	public Collection<InscricaoSelecaoProjeto> getInscricoes() {
		return inscricoes;
	}

	public void setInscricoes(Collection<InscricaoSelecaoProjeto> inscricoes) {
		this.inscricoes = inscricoes;
	}

	public AreaTematica getAreaTematica() {
		return areaTematica;
	}

	public void setAreaTematica(AreaTematica areaTematica) {
		this.areaTematica = areaTematica;
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
