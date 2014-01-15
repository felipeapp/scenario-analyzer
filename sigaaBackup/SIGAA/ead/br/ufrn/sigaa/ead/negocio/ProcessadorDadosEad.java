/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created 04/05/2012
 *
 */
package br.ufrn.sigaa.ead.negocio;

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
import br.ufrn.sigaa.ead.dao.DadosEadDao;
import br.ufrn.sigaa.ensino.graduacao.dao.TurmaCTDao;

/**
 * Processador utilizado exportar os dados referentes a EAD para a Sedis
 * 
 * @author Fred_Castro
 *
 */
public class ProcessadorDadosEad extends AbstractProcessador {
	
	private final String GET_TUTORES = "getTutores";
	private final String GET_ALUNOS_TUTOR = "getAlunosTutor";
	private final String GET_DADOS_TUTOR = "getDadosTutor";
	private final String GET_ALUNOS = "getAlunos";
	private final String GET_DADOS_ALUNO = "getDadosAluno";
	private final String GET_COORDENADORES_CURSO = "getCoordenadoresCurso";
	private final String GET_COORDENADORES_POLO = "getCoordenadoresPolo";
	private final String GET_PROFESSORES = "getProfessores";
	private final String GET_PROFESSOR_DISCIPLINA = "getProfessorDisciplina";
	private final String GET_DISCIPLINAS_SEMESTRE = "getDisciplinasSemestre";
	private final String GET_MATRICULAS_POR_DISCIPLINA = "getMatriculasPorDisciplina";
	private final String GET_REMATRICULAS_POR_DISCIPLINA = "getReMatriculasPorDisciplina";
	private final String GET_PARTICIPANTES_ACAO_EXTENSAO = "getParticipantesAcaoExtensao";
	

	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {

		MovimentoDadosEad mov = (MovimentoDadosEad) movimento;

		login(mov);

		String [] parametrosArray = mov.getParametros().split(",");
		
		DadosEadDao dao = getDAO(DadosEadDao.class, mov);

		try {
			
			if (GET_TUTORES.equals(mov.getOperacao())){
				char nivelEnsino = parametrosArray[0].charAt(0);
				long inicio = Long.parseLong(parametrosArray[1]);
				long fim = Long.parseLong(parametrosArray[2]);
				int idCurso = Integer.parseInt(parametrosArray[3]);
				int idPolo = Integer.parseInt(parametrosArray[4]);
	
				return dao.findTutoresByNivelPeriodoCursoPolo(nivelEnsino, inicio, fim, idCurso, idPolo, "0");
			} else if (GET_ALUNOS_TUTOR.equals(mov.getOperacao())){
				char nivelEnsino = parametrosArray[0].charAt(0);
				long inicio = Long.parseLong(parametrosArray[1]);
				long fim = Long.parseLong(parametrosArray[2]);
				int idTutor = Integer.parseInt(parametrosArray[3]);
	
				return dao.findAlunosTutorByNivelPeriodoTutor(nivelEnsino, inicio, fim, idTutor);
			} else if (GET_DADOS_TUTOR.equals(mov.getOperacao())){
				char nivelEnsino = parametrosArray[0].charAt(0);
				long inicio = Long.parseLong(parametrosArray[1]);
				long fim = Long.parseLong(parametrosArray[2]);
				String login = parametrosArray[3];
	
				return dao.findTutoresByNivelPeriodoCursoPolo(nivelEnsino, inicio, fim, 0, 0, login);
			} else if (GET_ALUNOS.equals(mov.getOperacao())){
				char nivelEnsino = parametrosArray[0].charAt(0);
				long ano = Long.parseLong(parametrosArray[1]);
				long periodo = Long.parseLong(parametrosArray[2]);
				int idCurso = Integer.parseInt(parametrosArray[3]);
				int idPolo = Integer.parseInt(parametrosArray[4]);
	
				return dao.findDiscentesByNivelPeriodoCursoPolo(nivelEnsino, ano, periodo, idCurso, idPolo, 0);
			} else if (GET_DADOS_ALUNO.equals(mov.getOperacao())){
				char nivelEnsino = parametrosArray[0].charAt(0);
				int ano = Integer.parseInt(parametrosArray[1]);
				int periodo = Integer.parseInt(parametrosArray[2]);
				long matricula = Integer.parseInt(parametrosArray[3]);
	
				return dao.findDiscentesByNivelPeriodoCursoPolo(nivelEnsino, ano, periodo, 0, 0, matricula);
			} else if (GET_COORDENADORES_CURSO.equals(mov.getOperacao())){
				long inicio = Long.parseLong(parametrosArray[0]);
				long fim = Long.parseLong(parametrosArray[1]);
				int idCurso = Integer.parseInt(parametrosArray[2]);
	
				return dao.findCoordenadoresCursoByNivelNivelPeriodoCurso(inicio, fim, idCurso);
			} else if (GET_COORDENADORES_POLO.equals(mov.getOperacao())){
				long inicio = Long.parseLong(parametrosArray[0]);
				long fim = Long.parseLong(parametrosArray[1]);
				int idPolo = Integer.parseInt(parametrosArray[2]);
	
				return dao.findCoordenadoresPoloByNivelNivelPeriodoPolo(inicio, fim, idPolo);
			} else if (GET_PROFESSORES.equals(mov.getOperacao())){
				char nivelEnsino = parametrosArray[0].charAt(0);
				int ano = Integer.parseInt(parametrosArray[1]);
				int periodo = Integer.parseInt(parametrosArray[2]);
				long inicio = Long.parseLong(parametrosArray[3]);
	
				return dao.findDocentesByNivelPeriodoDisciplina (nivelEnsino, ano, periodo, "0", inicio);
			} else if (GET_PROFESSOR_DISCIPLINA.equals(mov.getOperacao())){
				char nivelEnsino = parametrosArray[0].charAt(0);
				int ano = Integer.parseInt(parametrosArray[1]);
				int periodo = Integer.parseInt(parametrosArray[2]);
				String codigoDisciplina = parametrosArray[3];
				long inicio = Long.parseLong(parametrosArray[4]);
	
				return dao.findDocentesByNivelPeriodoDisciplina (nivelEnsino, ano, periodo, codigoDisciplina, inicio);
			} else if (GET_DISCIPLINAS_SEMESTRE.equals(mov.getOperacao())){
				char nivelEnsino = parametrosArray[0].charAt(0);
				int ano = Integer.parseInt(parametrosArray[1]);
				int periodo = Integer.parseInt(parametrosArray[2]);
				int idCurso = Integer.parseInt(parametrosArray[3]);
				int idComponenteCurricular = Integer.parseInt(parametrosArray[4]);
				long inicio = Integer.parseInt(parametrosArray[5]);
	
				return dao.findTurmasByNivelPeriodoCurso (nivelEnsino, ano, periodo, idCurso, idComponenteCurricular, inicio);
			} else if (GET_MATRICULAS_POR_DISCIPLINA.equals(mov.getOperacao())){
				char nivelEnsino = parametrosArray[0].charAt(0);
				int ano = Integer.parseInt(parametrosArray[1]);
				int periodo = Integer.parseInt(parametrosArray[2]);
				String codigoDisciplina = parametrosArray[3];
				int idCurso = Integer.parseInt(parametrosArray[4]);
				long inicio = Integer.parseInt(parametrosArray[5]);
	
				return dao.findMatriculasByNivelPeriodoCodigoCurso (nivelEnsino, ano, periodo, codigoDisciplina, idCurso, inicio, null);
			} else if (GET_REMATRICULAS_POR_DISCIPLINA.equals(mov.getOperacao())){
				char nivelEnsino = parametrosArray[0].charAt(0);
				int ano = Integer.parseInt(parametrosArray[1]);
				int periodo = Integer.parseInt(parametrosArray[2]);
				String codigoDisciplina = parametrosArray[3];
				int idCurso = Integer.parseInt(parametrosArray[4]);
				long inicio = Integer.parseInt(parametrosArray[5]);
	
				return dao.findMatriculasByNivelPeriodoCodigoCurso (nivelEnsino, ano, periodo, codigoDisciplina, idCurso, inicio, Boolean.TRUE);
			} else if (GET_PARTICIPANTES_ACAO_EXTENSAO.equals(mov.getOperacao())){
				int acaoExtensao = Integer.parseInt(parametrosArray[0]);
				
				return dao.findParticipantesAcaoExtensao(acaoExtensao);
			}
		} finally {
			dao.close();
		}			
		
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

	private void login (MovimentoDadosEad mov) throws NegocioException, ArqException{
		TurmaCTDao dao = null;
		UsuarioDao usuarioDao = null;
		PermissaoDAO permissaoDao = null;

		try {
			dao = getDAO(TurmaCTDao.class, mov);
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
			if (dao != null)
				dao.close();

			if (usuarioDao != null)
				usuarioDao.close();

			if (permissaoDao != null)
				permissaoDao.close();
		}
	}
}