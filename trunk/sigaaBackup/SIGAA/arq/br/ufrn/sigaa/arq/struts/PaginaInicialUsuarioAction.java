/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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

/** Action respons�vel pela p�gina inicial do usu�rio.
 * @author David Pereira
 *
 */
public class PaginaInicialUsuarioAction extends SigaaAbstractAction {

	/** Representa uma p�gina quando uma opera��o foi conclu�da com sucesso */
	public static final String SUCESSO = "sucesso";

	/** Representa uma p�gina quando uma opera��o foi conclu�da com falha */
	public static final String FALHA = "falha";
	
	/** Redireciona para a p�gina inicial do usu�rio.
	 * <br />
	 * M�todo n�o chamado por JSP.
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
		progress.setTotal(8); // 5 da Caixa Postal + Docs Eletr�nicos + Calend�rio Acad�mico + Acesso Menu, que ser�o adicionados no m�todo createChain
		
		// Acesso Menu
		AcessoMenu acesso = new AcessoMenu(usuario, progress);
	
		// Processa Permiss�es
		acesso.executar(req);
		req.getSession().setAttribute("acesso", acesso.getDados());

		// Carrega o calend�rio acad�mico atual
		carregarParametrosCalendarioAtual(req);
		progress.increment();
		
		// Caso seja docente da UFRN, coloca a unidade de entrada a sua
		// lota��o e tamb�m n�o tiver sido identificada uma unidade 
		// especializada (musica e museu)
		if (acesso.getDados().isDocenteUFRN() && !acesso.getDados().isUnidadeEspecializada() && !acesso.getDados().isChefeUnidade()) {
			if (usuario.getServidor().getUnidade() == null) {
				String sigla = RepositorioDadosInstitucionais.get("siglaSigaa");
				addMensagemErro("Caro Usu�rio, n�o foi poss�vel determinar o mapeamento entre a sua "
								+ "lota��o oficial de Recursos Humanos e a unidade do "+ sigla +", "
								+ "envie e-mail para sigaa@info.ufrn.br para reportar sua situa��o informando o seu login",
								req);
				
				Mail.sendAlerta("N�o foi poss�vel determinar lota��o para servidor de login " + usuario.getLogin());
				return mapping.findForward(FALHA);
			}
			
		}
		
		return mapping.findForward(SUCESSO);
	}
	
}
