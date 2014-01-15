/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 16/09/2011
 *
 */

package br.ufrn.sigaa.arq.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.net.SocketTimeoutException;

import javax.xml.ws.WebServiceException;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.integracao.interfaces.MemorandoEletronicoRemoteService;
import br.ufrn.sigaa.arq.acesso.DadosSubsistemaPrincipal;
import br.ufrn.sigaa.caixa_postal.dominio.MensagensHelper;

/**
 * Classe para gerenciar o fluxo após a seleção de vínculos
 * 
 * @author Henrique André
 */
@SuppressWarnings("serial")
@Component("telasPosSelecaoVinculos") @Scope("session")
public class TelasPosSelecaoVinculos extends SigaaAbstractController<Object> {
	
	/** Indica se houve acesso aos memorandos */
	private boolean acessouMemorandos = false;
	
	/**
	 * Inicia o fluxo
	 * 
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws IOException
	 */
	public String iniciar() throws NegocioException, ArqException, IOException {
		
		//Existe um bug (talvez no pretty faces) que o acessoMenu chega null neste ponto. Nesses casos, o melhor a se fazer é invalidar a sessão.
		if (isEmpty(getAcessoMenu()))
			return redirect("/logar.do?dispatch=logOff");
		
		// Redirecionamento: se há passaporte, utilizá-lo, caso contrário utilizar o atributo de sessão, se este existir.
		if (getUsuarioLogado().getPassaporteLogon() != null && getUsuarioLogado().getPassaporteLogon().getUrl() != null) {
			String urlParam = getUsuarioLogado().getPassaporteLogon().getUrl();
			
			return redirect(urlParam);
		} else if (getCurrentSession().getAttribute("urlRedirect") != null) {
			String urlSession = (String) getCurrentSession().getAttribute("urlRedirect");

			getCurrentSession().removeAttribute("urlRedirect");
			
			// Subsistema: Se há um subsistema especificado, configurar o subsistema.
			if (getCurrentSession().getAttribute("subsistemaRedirect") != null) {
				int subsistema = Integer.parseInt((String)getCurrentSession().getAttribute("subsistemaRedirect"));

				getCurrentSession().removeAttribute("subsistemaRedirect");
				
				setSubSistemaAtual(SigaaSubsistemas.getSubsistema(subsistema));
			}
			
			return redirect(urlSession);
		}		
		
		if(Sistema.isMemorandosAtivos(Sistema.SIGAA) && getUsuarioLogado().getVinculoAtivo().isVinculoServidor() && !acessouMemorandos){
			try {
				MemorandoEletronicoRemoteService memorandoEletronicoService = getMBean("memorandoEletronicoInvoker");
				
				if(!getParameterBoolean("passaporte") && Sistema.isSipacAtivo() && 
						memorandoEletronicoService.possuiMemorandoEletronico(getUsuarioLogado().getId())) {
					
					String url = RepositorioDadosInstitucionais.getLinkSipac() + "/sipac/logon.do?login="+ getUsuarioLogado().getLogin() + "&passaporte=true&memorandos=true";
					acessouMemorandos = true;
					logarSistema(url, Sistema.SIGAA, Sistema.SIPAC, getUsuarioLogado(), getCurrentRequest(), getCurrentResponse());
					return null;
				}
			} catch (SocketTimeoutException e) {
				addMensagemWarning("Não foi possível verificar os memorandos porque excedeu o tempo limite de resposta do servidor. Tente novamente em instantes.");
			} catch (WebServiceException e) {
				addMensagemWarning("Memorandos indisponível. Não foi possível estabelecer um contato com o serviço de memorandos.");
			}
		}
		
		if (getAcessoMenu().isPendentePlanoResposicao()) {
			return redirect("/formularioPlanoAula.jsf");
		}
		
		if (getAcessoMenu().isPendenteNotificacaoAcademica()) {
			return redirect("/formularioNotificacaoAcademica.jsf");
		}
		
		if (getAcessoMenu().isPendenteQuestionarioExtensao()) {
			return redirect("/pendenciaQuestionarioExtensao.jsf");
		}

		if (getUsuarioLogado().getVinculoAtivo().isVinculoServidor() && getAcessoMenu().isPendenteRelatoriosExtensao()) {
			return redirect("/pendenciaRelatoriosExtensao.jsf");
		}

		if (Sistema.isCaixaPostalAtiva(Sistema.SIGAA)) {
			boolean acessoCxPostal = MensagensHelper.acessoCaixaPostal(getUsuarioLogado().getId()) ? false : true;
			
			// Adicionando atributo em sessão para determinar se o usuário verá ou não o link para caixa postal
			getCurrentSession().setAttribute("acessarCaixaPostal", acessoCxPostal);
	
			if(acessoCxPostal) {
				
				String modoCxPostal = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.MODO_OPERACAO_CAIXA_POSTAL);
				int qtdNaoLidas = MensagensHelper.qtdNaoLidaCaixaEntrada(getUsuarioLogado().getId());
				
				if ("MOSTRAR_NAO_LIDAS".equals(modoCxPostal)) {
					getCurrentSession().setAttribute("qtdMsgsNaoLidasCxPostal", qtdNaoLidas);
				} else {
					// Verifica se tem mensagens para serem lidas na caixa postal
					if (qtdNaoLidas > 0) {
						// Se tem mensagens para serem lidas, vai para a caixa postal
						getCurrentSession().setAttribute("sistema", Sistema.SIGAA);	
						return redirect("/abrirCaixaPostal.jsf");
					}
				}
			}
		}
		
		if (getAcessoMenu().isDiscenteAptoAvaliacaoInstitucional()) {
			getCurrentSession().setAttribute("avisadoAvaliacaoInstitucional", true);
			return redirect("/avaliacaoInstitucionalDiscente.jsf");
		}
		
		// Se só for associado a um sistema, envia usuário para o único menu
		if (isAcessarDiretamenteModulo()) {
			setSubSistemaAtual(getModuloEntrarAutomaticamente());
			return redirect(getModuloEntrarAutomaticamente().getLink());
		}		
		
		return redirect("/verMenuPrincipal.do");
	}

	/**
	 * Se possui apenas um modulo principal com entrada automatica então retorna true.
	 * 
	 * @return
	 */
	private boolean isAcessarDiretamenteModulo() {
		
		if (ValidatorUtil.isEmpty(getAcessoMenu().getDadosSubsistemaPrincipal()))
			return false;
		
		int count = 0;
		
		for (DadosSubsistemaPrincipal dsp : getAcessoMenu().getDadosSubsistemaPrincipal()) {
			if (dsp.isEntrarAutomaticamente())
				count++;
		}
		
		if (count == 1)
			return true;
		
		return false;
	}

	/**
	 * Se possui apenas um modulo principal com entrada automatica então retorna, então retorna este subsistema
	 * 
	 * @return
	 */
	private SubSistema getModuloEntrarAutomaticamente() {
		
		if (isAcessarDiretamenteModulo()) {
			for (DadosSubsistemaPrincipal dsp : getAcessoMenu().getDadosSubsistemaPrincipal()) {
				if (dsp.isEntrarAutomaticamente())
				return dsp.getSubSistemaPrincipal();
			}			
		} 

		return null;
	}
}
