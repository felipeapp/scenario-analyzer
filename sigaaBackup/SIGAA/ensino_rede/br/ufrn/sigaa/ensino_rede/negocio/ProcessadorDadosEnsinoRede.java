/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 10/08/2013 
 */
package br.ufrn.sigaa.ensino_rede.negocio;

import java.rmi.RemoteException;
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
import br.ufrn.comum.dao.PermissaoDAO;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.Permissao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino_rede.dao.DadosEnsinoRedeDao;

/**
 * Processador responsável por exportar informações de ensino em rede.
 * 
 * @author Leonardo Campos
 *
 */
public class ProcessadorDadosEnsinoRede extends AbstractProcessador {

	private final String GET_CURSOS_POR_PROGRAMA_CAMPUS_IES = "getCursosPorProgramaCampusIes";
	private final String GET_MATRICULAS_POR_CAMPUS_IES = "getMatriculasPorCampusIes";
	private final String GET_DADOS_PESSOAIS_ALUNOS_POR_CAMPUS_IES = "getDadosPessoaisAlunosPorCampusIes";
	private final String GET_DADOS_PESSOAIS_DOCENTES_POR_CAMPUS_IES = "getDadosPessoaisDocentesPorCampusIes";
	
	public Object execute(Movimento movimento) throws NegocioException, ArqException,
			RemoteException {

		MovimentoDadosEnsinoRede mov = (MovimentoDadosEnsinoRede) movimento;
		
		login(mov);
		
		String [] parametrosArray = mov.getParametros().split(",");
		
		DadosEnsinoRedeDao dao = getDAO(DadosEnsinoRedeDao.class, mov);
		
		try {
			
			if(GET_CURSOS_POR_PROGRAMA_CAMPUS_IES.equals(mov.getOperacao())){
				String programa = parametrosArray[0];
				char nivelEnsino = parametrosArray[1].charAt(0);
				String siglaIes = parametrosArray[2];
				int idCampusIes = Integer.parseInt(parametrosArray[3]);
				int idCurso = Integer.parseInt(parametrosArray[4]);
			
				return dao.findCursosByProgramaCampusIes(programa, nivelEnsino, siglaIes, idCampusIes, idCurso);
			} else if(GET_MATRICULAS_POR_CAMPUS_IES.equals(mov.getOperacao())){
				char nivelEnsino = parametrosArray[0].charAt(0);
				int ano = Integer.parseInt(parametrosArray[1]);
				int periodo = Integer.parseInt(parametrosArray[2]);
				String siglaIes = parametrosArray[3];
				int idCampusIes = Integer.parseInt(parametrosArray[4]);
				int idCurso = Integer.parseInt(parametrosArray[5]);
				String codigoDisciplina = parametrosArray[6];
				long inicio = Long.parseLong(parametrosArray[7]);
				
				return dao.findMatriculasByCampusIes(nivelEnsino, ano, periodo, siglaIes, idCampusIes, codigoDisciplina, idCurso, inicio);
			} else if(GET_DADOS_PESSOAIS_ALUNOS_POR_CAMPUS_IES.equals(mov.getOperacao())){
				char nivelEnsino = parametrosArray[0].charAt(0);
				String siglaIes = parametrosArray[1];
				int idCampusIes = Integer.parseInt(parametrosArray[2]);
				int idCurso = Integer.parseInt(parametrosArray[3]);
				
				return dao.findDadosPessoaisAlunosByCampusIes(nivelEnsino, siglaIes, idCampusIes, idCurso);
			} else if(GET_DADOS_PESSOAIS_DOCENTES_POR_CAMPUS_IES.equals(mov.getOperacao())){
				char nivelEnsino = parametrosArray[0].charAt(0);
				String siglaIes = parametrosArray[1];
				int idCampusIes = Integer.parseInt(parametrosArray[2]);
				int idCurso = Integer.parseInt(parametrosArray[3]);
				
				return dao.findDadosPessoaisDocentesByCampusIes(nivelEnsino, siglaIes, idCampusIes, idCurso);
			}
		
		} finally {
			dao.close();
		}		
		return null;
	}

	
	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

	private void login (MovimentoDadosEnsinoRede mov) throws NegocioException, ArqException{
		UsuarioDao usuarioDao = null;
		PermissaoDAO permissaoDao = null;

		try {
			usuarioDao = getDAO(UsuarioDao.class, mov);

			// Verifica se os dados de acesso estão corretos
			Usuario usuarioBD = usuarioDao.findByLogin(mov.getLogin());

			if (usuarioBD == null || !UserAutenticacao.autenticaUsuario(mov.getRequest(), usuarioBD.getLogin().toLowerCase(), mov.getSenha(), true))
				throw new NegocioException("Usuário e/ou senha inválidos");

			try {
				permissaoDao = getDAO(PermissaoDAO.class, mov);

				List<Permissao> permissoes = permissaoDao.findPermissaosAtivosByUsuario(usuarioBD.getId(), new Date());
				usuarioBD.setPermissoes(permissoes);

				HashSet<Papel> papeis = new HashSet<Papel>();
				for (Permissao p : usuarioBD.getPermissoes())
					papeis.add(p.getPapel());
						
				usuarioBD.setPapeis(papeis);

				if (usuarioBD.getTutor() != null)
					usuarioBD.getTutor().getPoloCurso().getPolo().getDescricao();

				mov.setUsuarioLogado(usuarioBD);

				checkRole(SigaaPapeis.CONSULTOR_DADOS_EAD, mov);
			} catch (SegurancaException e){
				throw new NegocioException ("Para esta operação, é necessário que o usuário possua o papel SigaaPapeis.CONSULTOR_DADOS_EAD");
			}
		} finally {
			if (usuarioDao != null)
				usuarioDao.close();

			if (permissaoDao != null)
				permissaoDao.close();
		}
	}
}
