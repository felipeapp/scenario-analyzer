/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Processador respons�vel pela autentica��o do usu�rio e envio dos dados das turmas de CT para o Servlet respons�vel
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
			
			// Verifica se os dados de acesso est�o corretos
			Usuario usuarioBD = (Usuario) usuarioDao.findByLogin(pMov.getLogin());
			
			if (usuarioBD == null || !UserAutenticacao.autenticaUsuario(pMov.getRequest(), usuarioBD.getLogin().toLowerCase(), pMov.getSenha(), true))
				throw new NegocioException("Usu�rio e/ou senha inv�lidos");
			
			// Verifica se o usu�rio possui papel necess�rio para realizar esta opera��o
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
				throw new NegocioException ("Para esta opera��o, � necess�rio que o usu�rio possua o papel SigaaPapeis.CONSULTOR_OFERTAS_TURMAS");
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
			throw new NegocioException ("Esta opera��o requer que sejam enviados, via POST, os campos 'login' e 'senha' do usu�rio que deseja realiz�-la e o ano e per�odo para a consulta das turmas");
	}
}
