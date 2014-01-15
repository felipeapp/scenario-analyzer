/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 04/04/2012
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dao.PermissaoDAO;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.Permissao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.graduacao.dao.TurmaCTDao;

/**
 * Processador responsável pela autenticação do usuário e envio dos dados das turmas de CT para o Servlet responsável
 * 
 * @author Fred_Castro
 */
public class ProcessadorDadosTurmasCT extends AbstractProcessador {

	@Override
	public Object execute (Movimento mov) throws NegocioException, ArqException {
		validate (mov);
		
		TurmaCTDao dao = null;
		UsuarioDao usuarioDao = null;
		PermissaoDAO permissaoDao = null;
		
		MovimentoDadosTurmasCT pMov = (MovimentoDadosTurmasCT) mov;

		try {
			dao = getDAO(TurmaCTDao.class, pMov);
			usuarioDao = getDAO(UsuarioDao.class, pMov);
			
			// Verifica se os dados de acesso estão corretos
			Usuario usuarioBD = (Usuario) usuarioDao.findByLogin(pMov.getLogin());
			
			if (usuarioBD == null || !UserAutenticacao.autenticaUsuario(pMov.getRequest(), usuarioBD.getLogin().toLowerCase(), pMov.getSenha(), true))
				throw new NegocioException("Usuário e/ou senha inválidos");
			
			// Verifica se o usuário possui papel necessário para realizar esta operação
			try {

				permissaoDao = getDAO(PermissaoDAO.class, pMov);
				
				List<Permissao> permissoes = permissaoDao.findPermissaosAtivosByUsuario(usuarioBD.getId(), new Date());
				usuarioBD.setPermissoes(permissoes);

				HashSet<Papel> papeis = new HashSet<Papel>();
				for (Permissao p : usuarioBD.getPermissoes())
					papeis.add(p.getPapel());
				usuarioBD.setPapeis(papeis);

				if (usuarioBD.getTutor() != null)
					usuarioBD.getTutor().getPoloCurso().getPolo().getDescricao();

				pMov.setUsuarioLogado(usuarioBD);
				checkRole(SigaaPapeis.CONSULTOR_OFERTAS_TURMAS, pMov);
			} catch (SegurancaException e){
				throw new NegocioException ("Para esta operação, é necessário que o usuário possua o papel SigaaPapeis.CONSULTOR_OFERTAS_TURMAS");
			}
			
			return dao.findTurmasCTAbertasPorAnoEPeriodoEmCSV(pMov.getAno(), pMov.getPeriodo());
		} finally {
			if (dao != null)
				dao.close();
			
			if (usuarioDao != null)
				usuarioDao.close();
			
			if (permissaoDao != null)
				permissaoDao.close();
		}
	}

	@Override
	public void validate (Movimento mov) throws NegocioException, ArqException {
		MovimentoDadosTurmasCT pMov = (MovimentoDadosTurmasCT) mov;
		
		// Todos os dados devem estar preenchidos
		if (StringUtils.isEmpty(pMov.getLogin()) || StringUtils.isEmpty(pMov.getSenha()) || pMov.getAno() <= 0 || pMov.getPeriodo () <= 0)
			throw new NegocioException ("Esta operação requer que sejam enviados, via POST, os campos 'login' e 'senha' do usuário que deseja realizá-la e o ano e período para a consulta das turmas");
	}
}
