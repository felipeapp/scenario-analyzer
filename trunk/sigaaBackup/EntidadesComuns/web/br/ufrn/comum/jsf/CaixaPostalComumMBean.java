/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Criado em: 22/02/2010
*/
package br.ufrn.comum.jsf;

import java.io.IOException;

import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.caixa_postal.Mensagem;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.autenticacao.TokenGenerator;
import br.ufrn.arq.seguranca.dominio.TokenAutenticacao;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Managed bean para acesso à caixa postal do SIGAdmin a partir dos demais sistemas
 * 
 * @author Johnny Marçal
 * 
 */
@Component
@Scope("request")
public class CaixaPostalComumMBean extends ComumAbstractController<Mensagem> {

	@Autowired
	private TokenGenerator generator;
	
	/**
	 * Cria um token de autenticação e redireciona para a caixa de entrada. JSP: Não é chamado por JSPs. Chamado
	 * diretamente através de URL através do pretty-faces.
	 * 
	 * @return
	 */
	public String acessarCaixaPostal() {

		//verificando sistema de origem
		int sistema = 0;
		if (!ValidatorUtil.isEmpty(getParameter("sistema")))
			sistema = getParameterInt("sistema");
		else
			sistema = getSistema();

		TokenAutenticacao key = generator.generateToken(getUsuarioLogado(), sistema);

		if (ValidatorUtil.isEmpty(getCurrentSession().getAttribute("sistema"))
				|| ValidatorUtil.isEmpty(getCurrentSession().getAttribute("tipo")))
			return redirectSemContexto(RepositorioDadosInstitucionais.getLinkCaixaPostal()
					+ "/cxpostal/verCaixadeEntrada.jsf?" + "idUsuario=" + getUsuarioLogado().getId() + "&sistema="
					+ sistema + "&id=" + key.getId() +  "&key=" + key.getKey());

		else
			return redirectSemContexto(RepositorioDadosInstitucionais.getLinkCaixaPostal()
					+ "/cxpostal/verCaixadeEntrada.jsf?" + "idUsuario=" + getUsuarioLogado().getId() + "&sistema="
					+ getCurrentSession().getAttribute("sistema") + "&id=" + key.getId() +  "&key=" + key.getKey());

	}

	/**
	 * Consome o token gerado no método acessarCaixaPostal() e direciona para a jsp da caixa postal JSP: Não é chamado
	 * por JSPs. Chamado diretamente através de URL através do pretty-faces.
	 * 
	 * @return
	 * @throws IOException
	 * @throws ArqException
	 */
	public String exibirTela() throws IOException, ArqException {
		String key = getParameter("key");
		int sistema = getParameterInt("sistema");
		int idToken = getParameterInt("id");

		if (generator.isTokenValid(idToken, key)) {
			generator.invalidateToken(idToken);

			int idUsuario = getParameterInt("idUsuario");
			getCurrentSession()
					.setAttribute("usuario", getGenericDAO().findByPrimaryKey(idUsuario, UsuarioGeral.class));
			
			getCurrentSession().setAttribute("sistemaOrigem", sistema);
			getCurrentSession().setAttribute("tipoChamado", Mensagem.getTipoChamado(sistema));

			return redirectSemContexto(RepositorioDadosInstitucionais.getLinkCaixaPostal() + "/cxpostal/caixa_postal.jsf");

		} else {
			getCurrentResponse().getWriter().write("Acesso negado.");
			FacesContext.getCurrentInstance().responseComplete();
			return null;
		}
	}

	/**
	 * Retorna o link do sistema de acordo com a seleção do usuário 
	 * @return
	 */
	public String entrarSistema() {
		int sistema = 0;

		if (!ValidatorUtil.isEmpty(getCurrentSession().getAttribute("sistemaOrigem")))
			sistema = Integer.parseInt(getCurrentSession().getAttribute("sistemaOrigem").toString());
		else
			sistema = -1;

		getCurrentSession().invalidate();

		switch (sistema) {
		case Sistema.SIPAC:
			return redirectSemContexto(RepositorioDadosInstitucionais.getLinkSipac() + "/sipac/menuPrincipal.do");

		case Sistema.SIGRH:
			return redirectSemContexto(RepositorioDadosInstitucionais.getLinkSigrh() + "/sigrh/voltarSIGRH.jsf");

		case Sistema.SIGAA:
			return redirectSemContexto(RepositorioDadosInstitucionais.getLinkSigaa() + "/sigaa/verMenuPrincipal.do");

		case Sistema.SIGADMIN:
			return redirectSemContexto(RepositorioDadosInstitucionais.getLinkSigadmin() + "/admin/voltarSIGADMIN.jsf");

		default:
			return redirectSemContexto(RepositorioDadosInstitucionais.getLinkSipac() + "/public");

		}

	}

	/**
	 * Retorna o link do sistema de acordo com a seleção do usuário 
	 * @return
	 */
	public String sairCaixaPostal() {
		int sistema = 0;

		if (!ValidatorUtil.isEmpty(getCurrentSession().getAttribute("sistemaOrigem")))
			sistema = Integer.parseInt(getCurrentSession().getAttribute("sistemaOrigem").toString());
		else
			sistema = -1;

		getCurrentSession().invalidate();

		switch (sistema) {
		case Sistema.SIPAC:
			return redirectSemContexto(RepositorioDadosInstitucionais.getLinkSipac() + "/sipac/logoff.do");

		case Sistema.SIGRH:
			return redirectSemContexto(RepositorioDadosInstitucionais.getLinkSigrh() + "/sigrh/LogOff");

		case Sistema.SIGAA:
			return redirectSemContexto(RepositorioDadosInstitucionais.getLinkSigaa()
					+ "/sigaa/logar.do?dispatch=logOff");

		case Sistema.SIGADMIN:
			return redirectSemContexto(RepositorioDadosInstitucionais.getLinkSigadmin() + "/admin/logoff.jsf");

		default:
			return redirectSemContexto(RepositorioDadosInstitucionais.getLinkSipac() + "/public");

		}

	}

}
