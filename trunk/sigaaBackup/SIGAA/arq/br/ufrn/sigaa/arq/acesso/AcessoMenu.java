/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 06/10/2008 
 */
package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.arq.web.LogonProgress;
import br.ufrn.comum.dao.PermissaoDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.dominio.VinculoUsuario;

/**
 * Classe de controle utilizada para decidir os acessos ao usuário no sistema
 * 
 * @author Gleydson
 * 
 */
public class AcessoMenu extends AcessoMenuExecutor {

	/** Informações sobre acesso do usuário aos subsistemas do SIGAA. */
	private final DadosAcesso dados;
	
	/** Barra de indicação de progresso de logon. */
	private final LogonProgress progress;
	
	/** Construtor parametrizado.
	 * @param usuario
	 * @param progress
	 * @throws ArqException
	 */
	public AcessoMenu(Usuario usuario, LogonProgress progress) throws ArqException {
		this.dados = new DadosAcesso(usuario);
		this.progress = progress;
		
		createChain(progress);
	}

	/** Construtor parametrizado.
	 * @param usuario
	 * @param progress
	 * @param multiplosVinculos
	 * @throws ArqException
	 */
	public AcessoMenu(Usuario usuario, LogonProgress progress, boolean multiplosVinculos) throws ArqException {
		this.dados = new DadosAcesso(usuario, multiplosVinculos);
		this.progress = progress;

		createChain(progress);
	}
	
	/** Executa a requisição.
	 * @param req
	 * @throws ArqException
	 */
	public void executar(HttpServletRequest req) throws ArqException {
		executar(dados, req, progress);
	}
	
	/** Cria a corrente de dados de acesso à cada subsistema.
	 * @param progress
	 */
	private void createChain(LogonProgress progress) {
		
		// ATENÇÃO NO ENCADEAMENTO DOS MÉTODOS ABAIXO... 
		setNext(new AcessoGeral()).
		setNext(new AcessoBiblioteca()).
		setNext(new AcessoDiscente()).
		setNext(new AcessoServidor()).
		setNext(new AcessoDocente()).
		setNext(new AcessoDocenteExterno()).		
		setNext(new AcessoEad()).
		setNext(new AcessoExtensao()).
		setNext(new AcessoProjeto()).
		setNext(new AcessoGraduacao()).
		setNext(new AcessoInfantil()).
		setNext(new AcessoLato()).
		setNext(new AcessoMedio()).
		setNext(new AcessoMonitoria()).
		setNext(new AcessoMembroComissao()).
		setNext(new AcessoPesquisa()).
		setNext(new AcessoSae()).
		setNext(new AcessoCoordenacao()).
		setNext(new AcessoStricto()).
		setNext(new AcessoSecretarias()).
		setNext(new AcessoTecnico()).
		setNext(new AcessoFormacaoComplementar()).
		setNext(new AcessoVestibular()).
//		setNext(new AcessoInfraEstruturaFisica()).
		setNext(new AcessoComplexoHospitalar()).
		setNext(new AcessoAmbientesVirtuais()). 
		setNext(new AcessoRelatorios()).
		setNext(new AcessoRegistroDiploma()).
		setNext(new AcessoConveniosEstagio()).
		setNext(new AcessoPortalConcedenteEstagio()).
		setNext(new AcessoNee()).
		setNext(new AcessoAcoesAssociadas()).
		setNext(new AcessoProgramaAtualizacaoPedagogica()).
		setNext(new AcessoAbrirChamado()).
		setNext(new AcessoOuvidoria()).
		setNext(new AcessoFamiliar()).
		setNext(new AcessoRelacoesInternacionais());

		// QUANDO ACRESCENTAR UM ACESSO MENU, INCREMENTAR O TOTAL DA LINHA ABAIXO
		progress.incrementaTotal(34);
	}

	/**
	 * Processa as permissões
	 * 
	 * @param usuario
	 * @throws ArqException
	 */
	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
				
		PermissaoDAO permDAO = getDAO(PermissaoDAO.class, req);
		try {
		UserAutenticacao.carregaPermissoes(req, usuario);

		// Verificar acesso ao SIPAC, por papel ou se for servidor
		dados.setSipac(permDAO.isUserInSipac(usuario.getId()) || usuario.getVinculoServidor() != null);
		
		// Verificar se o usuário é administrador do sistema
		dados.setAdministradorSistema(isUserInRole(SigaaPapeis.ADMINISTRADOR_SIGAA, req));
		
		//Verifica se o modo acessibilidade está ativo
		dados.setAcessibilidade((Boolean) req.getSession().getAttribute("acessibilidade"));
		
		// Usuário possui múltiplos vínculos
		if (usuario.getVinculos().size() > 1)
			dados.setMultiplosVinculos(true);

		// Se o usuário possuir mais de um vínculo prioritário, deve
		// escolher um dos vínculos
		if (usuario.getVinculosPrioritarios().size() > 1) {
			dados.setMultiplosVinculos(true);
			dados.setEscolhidoVinculoPrincipal(false);
		} else if (usuario.getVinculosPrioritarios().size() == 1) {
			/* Se  possuir  apenas um  vínculo  prioritário,  usá-lo */
			if (usuario.getVinculoAtivo() == null || usuario.getVinculoAtivo().getNumero() == 0)
				usuario.setVinculoAtivo(usuario.getVinculosPrioritarios().get(0));
		} else {

			if (usuario.getVinculos().isEmpty()) { // Se não possuir vínculos
				usuario.setVinculoAtivo(VinculoUsuario.nenhumVinculo((Unidade)getUsuarioLogado(req).getUnidade()));
			} else {
				// Pega qualquer vínculo não prioritário
				if (usuario.getVinculoAtivo() == null || usuario.getVinculoAtivo().getNumero() == 0)
					usuario.setVinculoAtivo(usuario.getVinculos().get(0));
			}

		}
		
		boolean chefe = false;
		if (usuario.getVinculoAtivo() != null) {
			if (usuario.getVinculoAtivo().isVinculoServidor()) {
				usuario.setServidor(usuario.getVinculoAtivo().getServidor());
				
				// O processamento de vínculos deve considerar também o vínculo de funções administrativas.
				// Devemos pegar se o servidor é responsável da unidade e mostrar como um novo vínculo.
				//  Ativar o boolean chefeUnidade e mudar a unidade do usuário para a unidade da responsabilidade.
				if (usuario.getVinculoAtivo().getResponsavel() != null) {
					dados.setChefeUnidade(true);
					dados.getUsuario().addPapelTemporario(SigaaPapeis.CHEFE_UNIDADE);
					chefe = true;
				}
				
			} else if (usuario.getVinculoAtivo().isVinculoDiscente()) {
				usuario.setDiscente(usuario.getVinculoAtivo().getDiscente().getDiscente());
			} else if (usuario.getVinculoAtivo().isVinculoDocenteExterno()) {
				usuario.setDocenteExterno(usuario.getVinculoAtivo().getDocenteExterno());
			} else if (usuario.getVinculoAtivo().isVinculoTutorOrientador()) {
				usuario.setTutor(usuario.getVinculoAtivo().getTutor());
			}
		} 

		if (!chefe) {
			dados.setChefeUnidade(false);
			usuario.getPapeis().remove(SigaaPapeis.CHEFE_UNIDADE);
		}
		} finally {
			if (permDAO != null)
				permDAO.close();
		}
		
	}

	/** Retorna as informações sobre acesso do usuário aos subsistemas do SIGAA. 
	 * @return
	 */
	public DadosAcesso getDados() {
		return dados;
	}
	
}
