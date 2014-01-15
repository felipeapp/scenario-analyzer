/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 21/02/2008
 */
package br.ufrn.sigaa.arq.jsf;

import java.util.Iterator;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpSession;

import br.ufrn.arq.negocio.validacao.ListaMensagens;

/**
 * Phase Listener para chamar managed beans do menu por GET
 * 
 * @author David Pereira
 *
 */
public class SigaaPhaseListener implements PhaseListener {

	/**
	 * Trata o processamento de uma fase está pra começar.
	 * @see PhaseListener.#beforePhase(PhaseEvent)
	 */
	public void beforePhase(PhaseEvent event) {
		FacesContext context = event.getFacesContext();
		String view = context.getViewRoot().getViewId();

		if (view == null) 
			return;

		/*
		
		if (view.startsWith("/rh")) {
			putJsfErrorInSessionAttribute(context);
		}		
		
		if (view.startsWith("/ensino/trancamento_matricula/resumo_solicitacao")) {
			putJsfErrorInSessionAttribute(context);
		}
		
		if (view.startsWith("/graduacao/solicitacao_reposicao_prova/")) {
			putJsfErrorInSessionAttribute(context);
		}		
		
		if (view.startsWith("/graduacao/portal_noticias/form")) {
			putJsfErrorInSessionAttribute(context);
		}
		
		if (view.startsWith("/pesquisa/GrupoPesquisa")) {
			putJsfErrorInSessionAttribute(context);
		}		
		
		if (view.startsWith("/geral")) {
			putJsfErrorInSessionAttribute(context);
		}
		
		if (view.startsWith("/biblioteca")) {
			putJsfErrorInSessionAttribute(context);
		}
		
		if (view.startsWith("/ava"))
			putJsfErrorInSessionAttribute(context);
		
		if (view.startsWith("/estagio"))
			putJsfErrorInSessionAttribute(context);		
		
		if (view.startsWith("/ava/entrarTurma")) {
			FacesContext fc = FacesContext.getCurrentInstance();
			TurmaVirtualMBean menu = (TurmaVirtualMBean) context.getApplication().getVariableResolver().resolveVariable(fc, "turmaVirtual");

			
			try {
				Method metodo = menu.getClass().getDeclaredMethod("entrar", null);
				String result = (String) metodo.invoke(menu, null);
				
				Application app = context.getApplication();
				app.getNavigationHandler().handleNavigation(context, "metodoStr", result);
				context.renderResponse();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else if (view.startsWith("/avaliacaoInstitucionalDocente")) {
			FacesContext fc = FacesContext.getCurrentInstance();
			AvaliacaoInstitucionalMBean avalMBean = (AvaliacaoInstitucionalMBean) context.getApplication().getVariableResolver().resolveVariable(fc, "avaliacaoInstitucional");

			
			try {
				String result = avalMBean.iniciarDocente();
				
				Application app = context.getApplication();
				app.getNavigationHandler().handleNavigation(context, "metodoStr", result);
				context.renderResponse();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} else if (view.startsWith("/avaliacaoInstitucionalDiscente")) {
			FacesContext fc = FacesContext.getCurrentInstance();
			AvaliacaoInstitucionalMBean avalMBean = (AvaliacaoInstitucionalMBean) context.getApplication().getVariableResolver().resolveVariable(fc, "avaliacaoInstitucional");

			
			try {
				String result = avalMBean.iniciarDiscente();
				
				Application app = context.getApplication();
				app.getNavigationHandler().handleNavigation(context, "metodoStr", result);
				context.renderResponse();
			} catch(SegurancaException e) {
				HttpServletResponse res = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

				try {
					res.sendRedirect("/sigaa/verPortalDiscente.do");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				FacesContext.getCurrentInstance().responseComplete();
				return;
			} catch (NegocioException e) {
				HttpServletResponse res = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

				try {
					res.sendRedirect("/sigaa/verPortalDiscente.do");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				FacesContext.getCurrentInstance().responseComplete();
				return;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else if (view.startsWith("/vestibular/")) {
			
			// Caso o usuário utilize o botão volta do navegador e tente operar numa lista que já foi processada,
			// "converte" o erro de "resolved to null" para "Solicitação já processada"
			Iterator<FacesMessage> erros = context.getMessages();
			HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
			ListaMensagens listaErros = (ListaMensagens) session.getAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION);
			while (erros.hasNext()) {
				FacesMessage fm = erros.next();
				String summary = fm.getSummary();
				if (summary.contains("Target Unreachable,") &&summary.contains("resolved to null")) {
					if (listaErros == null)
						listaErros = new ListaMensagens();
					listaErros.addErro("Solitação já processada");
					session.setAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION, listaErros);
					forward("/vestibular/menu.jsf");
					break;
				}				 
			}
		} else if (view.startsWith("/formularioPlanoAula")) {
			FacesContext fc = FacesContext.getCurrentInstance();
			AvisoFaltaDocenteHomologadaMBean faltaHomologadaMBean = (AvisoFaltaDocenteHomologadaMBean) context.getApplication().getVariableResolver().resolveVariable(fc, "avisoFaltaHomologada");

			
			try {
				String result = faltaHomologadaMBean.listarHomologacoesPendentes();
				
				Application app = context.getApplication();
				app.getNavigationHandler().handleNavigation(context, "metodoStr", result);
				context.renderResponse();
			} catch(SegurancaException e) {
				HttpServletResponse res = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

				try {
					res.sendRedirect("/sigaa/verPortalDocente.do");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				FacesContext.getCurrentInstance().responseComplete();
				return;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else if (view.startsWith("/busca_discente_cadastro_unico")) {
			FacesContext fc = FacesContext.getCurrentInstance();
			BuscaDiscenteCadastroUnicoMBean buscaMBean = (BuscaDiscenteCadastroUnicoMBean) context.getApplication().getVariableResolver().resolveVariable(fc, "buscaDiscenteCadastroUnico");
			
			try {
				String result = buscaMBean.iniciarBusca();
				
				Application app = context.getApplication();
				app.getNavigationHandler().handleNavigation(context, "metodoStr", result);
				context.renderResponse();
			} catch(SegurancaException e) {
				HttpServletResponse res = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

				try {
					res.sendRedirect("/sipac/telaInicial.do");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				FacesContext.getCurrentInstance().responseComplete();
				return;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}			
			
		} else if (view.startsWith("/ver_cadastro_unico")) {
			
			String replace = view.replace(".jsp", "");
			String[] split = replace.split("/");

			String id = split[2];
			
			
			FacesContext fc = FacesContext.getCurrentInstance();
			AdesaoCadastroUnicoBolsaMBean adesaoMBean = (AdesaoCadastroUnicoBolsaMBean) context.getApplication().getVariableResolver().resolveVariable(fc, "adesaoCadastroUnico");
			
			try {
				String result = adesaoMBean.visualizarQuestionario(new Integer(id));
				
				Application app = context.getApplication();
				app.getNavigationHandler().handleNavigation(context, "metodoStr", result);
				context.renderResponse();
			} catch(SegurancaException e) {
				HttpServletResponse res = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

				try {
					res.sendRedirect("/sipac/telaInicial.do");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				FacesContext.getCurrentInstance().responseComplete();
				return;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}			
			
		} else if (view.startsWith("/public/vestibular")) {
			HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
			session.setAttribute("escondeEntrarSistema", true);			
		} else {
			HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
			session.setAttribute("escondeEntrarSistema", false);	
		}
		
		*/
	}
	
	/**
	  Adiciona os erros de JSF na lista de mensagens de erros
	 */
	private void putJsfErrorInSessionAttribute(FacesContext context) {
	
		Iterator erros = context.getMessages();
		while (erros.hasNext()) {
			FacesMessage fm = (FacesMessage) erros.next();

			String summary = fm.getSummary();
			summary = summary.replaceAll("_", " ");
			summary = summary.replaceAll("'", "");

			HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
			ListaMensagens listaErros = (ListaMensagens) session.getAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION);

			if (listaErros == null)
				listaErros = new ListaMensagens();

			listaErros.addErro(summary);
			session.setAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION, listaErros);
		}
	}

	/**
	 * Trata o processamento de uma fase está pra ser completada.
	 * @see PhaseListener.#afterPhase(PhaseEvent)
	 */
	public void afterPhase(PhaseEvent event) {
	}

	// execute the logic before rendering the view
	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}

	/**
	 * Da um forward para url passada
	 * 
	 * @param url
	 * @return
	 */
	private String forward(String url) {
		FacesContext context = FacesContext.getCurrentInstance();
		Application app = context.getApplication();
		UIViewRoot view = app.getViewHandler().createView(context, url);
		context.setViewRoot(view);
		context.renderResponse();

		// Retorna null para evitar o return null no action do Managed Bean
		return null;
	}
	
}
