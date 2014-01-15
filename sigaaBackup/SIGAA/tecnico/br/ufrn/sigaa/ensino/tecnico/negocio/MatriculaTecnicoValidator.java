/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 04/08/2008
 *
 */
package br.ufrn.sigaa.ensino.tecnico.negocio;

import java.util.Collection;
import java.util.List;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.EspecializacaoTurmaEntrada;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Contém as validações das restrições da matrícula no ensino técnico
 * 
 * @author Leonardo Campos
 *
 */
public class MatriculaTecnicoValidator {

	private static final int MAXIMO_REPROVACOES_COMPONENTE = 3;
	private static final int MAXIMO_REPROVACOES_DISCENTE = 4;

	/**
	 * Verifica se o aluno passado ultrapassou o limite de 4 reprovações em disciplinas distintas.
	 * Verifica se o aluno informado ultrapassou o limite de 2 reprovações na disciplina informada.
	 * 
	 * @param discente
	 * @param lista
	 * @throws DAOException
	 */
	public static void validarReprovacoes(Discente discente, ListaMensagens lista) throws DAOException{
		if (discente.isTecnico() && discente.getGestoraAcademica().getId() == Unidade.ESCOLA_MUSICA) {
			MatriculaComponenteDao dao = new MatriculaComponenteDao();
			
			try {
				// Validar limite máximo de reprovações em disciplinas diferentes.
				int totalReprovacoes = dao.countMatriculasByDiscente(discente, SituacaoMatricula.REPROVADO, SituacaoMatricula.REPROVADO_FALTA, SituacaoMatricula.REPROVADO_MEDIA_FALTA);
				if( totalReprovacoes >= MAXIMO_REPROVACOES_DISCENTE){
				    lista.addErro("O aluno " + discente.getNome() + 
							" não pode efetuar sua matrícula pois possui " + MAXIMO_REPROVACOES_DISCENTE +
							" ou mais reprovações em disciplinas distintas");
				}
				
				// Validar limite máximo de reprovações em uma mesma disciplina.
				Collection<ComponenteCurricular> reprovacoes =  dao.findComponentesReprovadosByDiscente(discente, MAXIMO_REPROVACOES_COMPONENTE);
				if( !ValidatorUtil.isEmpty(reprovacoes) ){
					
					String codigosReprovacoes = StringUtils.transformaEmLista((List<?>) reprovacoes);
					lista.addErro("O aluno "+ discente.getNome() + 
							" não pode efetuar sua matrícula" +
							" pois possui " + MAXIMO_REPROVACOES_COMPONENTE +
							" ou mais reprovações nas disciplinas " + codigosReprovacoes);
				}
			} finally {
				dao.close();
			}
		}
	}
	
	
	/**
	 * Verifica se a turma corresponde à especialidade da turma de entrada do discente do ensino técnico.
	 * @param discente
	 * @param turma
	 * @param erros
	 * @throws DAOException
	 */
	public static void validarEspecializacaoTurma(DiscenteAdapter discente, Turma turma, ListaMensagens erros) throws DAOException{
		if (discente.getNivel() == NivelEnsino.TECNICO && turma.getEspecializacao() != null) {
			GenericDAO dao = DAOFactory.getGeneric(Sistema.SIGAA);
			try {
				
				DiscenteTecnico dt = dao.findByPrimaryKey(discente.getId(), DiscenteTecnico.class);
				
				if (dt.getTurmaEntradaTecnico() == null) {
					erros.addErro("O Aluno " + discente.getPessoa().getNome() + " está sem turma de entrada definida. Não é possível realizar sua matrícula.<br/>");
				} else if (dt.getTurmaEntradaTecnico().getEspecializacao() == null) {
					
					erros.addErro("O discente não pode se matricular nessa turma da disciplina " + turma.getDisciplina().getNome() +
							" pois ela é reservada somente para alunos da(s) " +
							"especialidade(s) " +turma.getEspecializacao().getDescricao()+  ".<br/>");
					
				} else if(dt.getTurmaEntradaTecnico().getEspecializacao() != null) {
					if(turma.getEspecializacao() != null && turma.getEspecializacao().getEspecializacoes() != null 
							&& turma.getEspecializacao().getEspecializacoes().size() > 0){
						
						for(EspecializacaoTurmaEntrada e: turma.getEspecializacao().getEspecializacoes()){
							if(e.getId() == dt.getTurmaEntradaTecnico().getEspecializacao().getId())
								return;
						}
						erros.addErro("O discente não pode se matricular nessa turma da disciplina " + turma.getDisciplina().getNome() +
								" pois ela é reservada somente para alunos da(s) " +
								"especialidade(s) " +turma.getEspecializacao().getDescricao()+  ".<br/>");
						
					}else if(dt.getTurmaEntradaTecnico().getEspecializacao().getId() != turma.getEspecializacao().getId()){
						
						erros.addErro("O discente não pode se matricular nessa turma da disciplina " + turma.getDisciplina().getNome() +
								" pois ela é reservada somente para alunos da " +
								"especialidade " +turma.getEspecializacao().getDescricao()+  ".<br/>");
					}
				}
			} catch (Exception e) {
				throw new DAOException(e);
			} finally {
				dao.close();
			}
			
		}
	}
	
	
	
	
}
