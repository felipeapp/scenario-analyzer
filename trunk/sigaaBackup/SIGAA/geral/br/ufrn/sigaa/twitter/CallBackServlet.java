/**
 * 
 */
package br.ufrn.sigaa.twitter;

import java.io.IOException;
import java.util.Date;

import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.util.FacesContextUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.TurmaTwitter;
import br.ufrn.sigaa.ava.jsf.TurmaVirtualMBean;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe implementada para recuperar os atributos de callback do twitter
 * @author Eric Moura
 *
 */
public class CallBackServlet extends HttpServlet {
	
	/** ID de serialização da classe */
	private static final long serialVersionUID = -1141588968620914900L;

	/**
	 * Recebe o acessToken do Twitter.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Twitter twitter = (Twitter) request.getSession().getAttribute("twitter");
        try {
        	String pin = request.getParameter("oauth_verifier");
        	RequestToken requestToken = (RequestToken) request.getSession().getAttribute("requestToken");
            AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, pin);
            Usuario usuario = (Usuario)request.getSession().getAttribute("usuario");
            Turma turma = (Turma)request.getSession().getAttribute("turmaTwitter");
            request.getSession().removeAttribute("turmaTwitter");
            request.getSession().removeAttribute("requestToken");
            gravarAccessToken(usuario,accessToken,turma, request, response);            
        } catch (TwitterException e) {
        	// Twitter fora do ar, envia mensagem para o usuário
        	request.getSession().setAttribute("erro", true);
        }
        response.sendRedirect(request.getContextPath() + "/ava/NoticiaTurma/novo_twitter.jsf");
    }
	
	/**
	 * Cria um twitter da turma virtual com seu acessToken
	 */
	private void gravarAccessToken(Usuario usuario, AccessToken accessToken, Turma turma,HttpServletRequest req, HttpServletResponse res) {
		TurmaTwitter turmaTwitter = new TurmaTwitter();
		turmaTwitter.setRegistroEntrada((RegistroEntrada) req.getSession().getAttribute("registroEntrada"));
		turmaTwitter.setTurma(turma);
		turmaTwitter.setAccessToken(accessToken.getToken());
		turmaTwitter.setAccessSecret(accessToken.getTokenSecret());
		turmaTwitter.setDataCadastro(new Date());
		turmaTwitter.setCredentialsTwitter(null);
		turmaTwitter.setUsuario(usuario);
		GenericDAOImpl genericDAOImpl = null;
		try{
			TurmaVirtualMBean tBean = getMBean("turmaVirtual", req, res);
			tBean.registrarAcao(null, EntidadeRegistroAva.TWITTER, AcaoAva.INICIAR_INSERCAO, true);
			genericDAOImpl = DAOFactory.getInstance().getDAO(GenericDAOImpl.class,Sistema.SIGAA, req);
			genericDAOImpl.create(turmaTwitter);
			tBean.registrarAcao(null, EntidadeRegistroAva.TWITTER, AcaoAva.INSERIR, turmaTwitter.getId());

		} catch (Exception e) {
			e.printStackTrace();
		} 	finally{
			if (genericDAOImpl != null)
				genericDAOImpl.close();
		}
	}
	
	/**
	 * Retorna um managed-bean existente no container do JavaServer Faces.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getMBean(String mbeanName, HttpServletRequest req, HttpServletResponse res) {
		FacesContext fc = FacesContextUtils.getFacesContext(req, res);
		return (T) fc.getELContext().getELResolver().getValue(fc.getELContext(), null, mbeanName);
	}
	
}
