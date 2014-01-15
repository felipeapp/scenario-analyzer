/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Criado em: 20/11/2007
 */
package br.ufrn.sigaa.cv.jsf;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.FacadeDelegate;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.cv.dao.ComunidadeVirtualDao;
import br.ufrn.sigaa.ava.cv.dao.MembroComunidadeDao;
import br.ufrn.sigaa.cv.dominio.MembroComunidade;
import br.ufrn.sigaa.cv.negocio.MovimentoCadastroCv;

/**
 * Servlet para autenticar o link clicado pelo usuário quando recebe
 * um convite para participar em alguma Comunidade Virtual.
 * 
 * @author Agostinho
 *
 */
public class ValidarConviteCVServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		MembroComunidadeDao membroDAO = new MembroComunidadeDao();
		ComunidadeVirtualDao comunidadeDAO = new ComunidadeVirtualDao();
		
		boolean membroExistente = false;		
		Set<MembroComunidade> listaParticipantesAtual = new LinkedHashSet<MembroComunidade>();
		
		try {
				Integer idUser = Integer.valueOf( req.getParameter("user") );
				String hash = req.getParameter("autenticacao");
				MembroComunidade membroLocalizado = membroDAO.findConviteByHashAutenticacao(hash);
			
				if (membroLocalizado != null) {
					listaParticipantesAtual = comunidadeDAO.findParticipantes(membroLocalizado.getComunidade());
						if (listaParticipantesAtual.contains(membroLocalizado)) 
							membroExistente = true;
				}
				else {
					req.setAttribute("mensagem", "CONVITE NÃO LOCALIZADO OU INVÁLIDO");
					req.getRequestDispatcher("/public/autenticidade/convite_cv.jsf").forward(req, res);
				}		
			
			if (!membroExistente && membroLocalizado != null) {
				try {
					req.setAttribute("sistema", new Integer(Sistema.SIGAA));
					
					FacadeDelegate facade = new FacadeDelegate("ejb/SigaaFacade");
					facade.prepare(SigaaListaComando.CADASTRAR_MEMBRO_CONVITE.getId(), new UsuarioGeral(idUser), Sistema.SIGAA);
					
					MovimentoCadastroCv mov = new MovimentoCadastroCv();
					mov.setCodMovimento(SigaaListaComando.CADASTRAR_MEMBRO_CONVITE);
					mov.setMembroComunidade(membroLocalizado);

					facade.execute(mov, new UsuarioGeral(idUser), Sistema.SIGAA);

					FacadeDelegate facadeDesativarConvite = new FacadeDelegate("ejb/SigaaFacade");
					facadeDesativarConvite.prepare(SigaaListaComando.DESATIVAR_CONVITE_ACEITO_CV.getId(), new UsuarioGeral(idUser), Sistema.SIGAA);
					
					MovimentoCadastroCv mov2 = new MovimentoCadastroCv();
					mov2.setCodMovimento(SigaaListaComando.DESATIVAR_CONVITE_ACEITO_CV);
					mov2.setMembroComunidade(membroLocalizado);
					mov2.setHash(hash);

					facadeDesativarConvite.execute(mov2, new UsuarioGeral(idUser), Sistema.SIGAA);
					
					req.setAttribute("mensagem", "VOCÊ ACEITOU O CONVITE! <br><br>A PARTIR DE AGORA VOCÊ PODE SE LOGAR NO SIGAA E PARTICIPAR DA COMUNIDADE!");
					req.getRequestDispatcher("/public/autenticidade/convite_cv.jsf").forward(req, res);
				} catch (NegocioException e) {
					e.printStackTrace();
				} catch (ArqException e) {
					e.printStackTrace();
				} finally {
					membroDAO.close();
					comunidadeDAO.close();
				}
			}
			else {
				req.setAttribute("mensagem", "VOCÊ JÁ É PARTICIPANTE DESSA COMUNIDADE!");
				req.getRequestDispatcher("/public/autenticidade/convite_cv.jsf").forward(req, res);
			}
		} catch (DAOException e) {
			e.printStackTrace();
		} finally {
			membroDAO.close();
			comunidadeDAO.close();
		}
	}
}
