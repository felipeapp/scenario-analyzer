/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 *  Created on 19/02/2009
 */
package br.ufrn.sigaa.arq.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.email.Mail;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.web.LogonProgress;
import br.ufrn.sigaa.arq.acesso.AcessoMenu;
import br.ufrn.sigaa.arq.acesso.DadosAcesso;
import br.ufrn.sigaa.dominio.Usuario;

/** Action responsável pela página inicial do usuário.
 * @author David Pereira
 *
 */
public class PaginaInicialUsuarioAction extends SigaaAbstractAction {

	/** Representa uma página quando uma operação foi concluída com sucesso */
	public static final String SUCESSO = "sucesso";

	/** Representa uma página quando uma operação foi concluída com falha */
	public static final String FALHA = "falha";
	
	/** Redireciona para a página inicial do usuário.
	 * <br />
	 * Método não chamado por JSP.
	 * 
	 * @throws Exception
	 * @see org.apache.struts.actions.DispatchAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		Usuario usuario = (Usuario) getUsuarioLogado(req);
		
		// Bean para registrar o progresso do Logon
		LogonProgress progress = getBean(req, "logonProgress");
		progress.reset();
		progress.setTotal(8); // 5 da Caixa Postal + Docs Eletrônicos + Calendário Acadêmico + Acesso Menu, que serão adicionados no método createChain
		
		// Acesso Menu
		AcessoMenu acesso = new AcessoMenu(usuario, progress);
	
		// Processa Permissões
		acesso.executar(req);
		req.getSession().setAttribute("acesso", acesso.getDados());

		// Carrega o calendário acadêmico atual
		carregarParametrosCalendarioAtual(req);
		progress.increment();
		
		// Caso seja docente da UFRN, coloca a unidade de entrada a sua
		// lotação e também não tiver sido identificada uma unidade 
		// especializada (musica e museu)
		if (acesso.getDados().isDocenteUFRN() && !acesso.getDados().isUnidadeEspecializada() && !acesso.getDados().isChefeUnidade()) {
			if (usuario.getServidor().getUnidade() == null) {
				String sigla = RepositorioDadosInstitucionais.get("siglaSigaa");
				addMensagemErro("Caro Usuário, não foi possível determinar o mapeamento entre a sua "
								+ "lotação oficial de Recursos Humanos e a unidade do "+ sigla +", "
								+ "envie e-mail para sigaa@info.ufrn.br para reportar sua situação informando o seu login",
								req);
				
				Mail.sendAlerta("Não foi possível determinar lotação para servidor de login " + usuario.getLogin());
				return mapping.findForward(FALHA);
			}
			
		}
		
		return mapping.findForward(SUCESSO);
	}
	
}
