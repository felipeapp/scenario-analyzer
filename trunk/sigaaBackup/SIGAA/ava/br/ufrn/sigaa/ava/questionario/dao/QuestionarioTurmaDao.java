/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/01/2011
 * 
 */
package br.ufrn.sigaa.ava.questionario.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.questionarios.dominio.ConjuntoRespostasQuestionarioAluno;
import br.ufrn.sigaa.ava.questionarios.dominio.EnvioRespostasQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.PerguntaQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.QuestionarioTurma;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO que realiza as consultas referentes aos questionários da turma virtual. 
 * 
 * @author Fred_Castro
 *
 */
public class QuestionarioTurmaDao extends GenericSigaaDAO {

	/**
	 * Retorna true se o usuário pode tentar responder o questionário.
	 * 
	 * @param u
	 * @param questionario
	 * @return
	 * @throws DAOException
	 */
	public boolean permiteNovaTentativa (Usuario u, QuestionarioTurma questionario) throws DAOException {
		if (questionario == null) return false;

		Calendar cal = Calendar.getInstance();
		cal.setTime(questionario.getFim());
		cal.add(Calendar.HOUR, questionario.getHoraFim());
		cal.add(Calendar.MINUTE, questionario.getMinutoFim());
		
		// Se o prazo para envio expirou,
		if (cal.getTime().before(new Date()))
				return false;
		
		List <EnvioRespostasQuestionarioTurma> respostas = findRespostasByQuestionarioAluno(questionario, u, null, null);
		
		
		
		int enviosNaoFinalizados = 0;
		for (EnvioRespostasQuestionarioTurma e : respostas)
			if (!e.isFinalizado() && e.getDataCadastro().getTime() + questionario.getDuracao() * 60 * 1000 > new Date().getTime() && new Date().before(cal.getTime()))
				enviosNaoFinalizados ++;

		return respostas == null || respostas.size() - enviosNaoFinalizados < questionario.getTentativas();
	}
	
	/**
	 * Retorna todas as respostas ativas do questionário passado. Pode ser filtrada por usuário ou situação (envio finalizado ou não)
	 * 
	 * @param q
	 * @param u
	 * @param finalizados
	 * @param todasRespostas
	 * @return
	 * @throws DAOException
	 */
	public List <EnvioRespostasQuestionarioTurma> findRespostasByQuestionarioAluno (QuestionarioTurma q, Usuario u, Boolean finalizados, Boolean todasRespostas) throws DAOException{
		
		String hqlFinalizados = "";
		if (finalizados != null)
			hqlFinalizados = " and finalizado = " + (finalizados == false ? "falseValue()" : "trueValue()");
		
		String hqlUsuario = "";
		if (u != null)
			hqlUsuario = " and usuarioEnvio.id = " + u.getId();
			
		@SuppressWarnings("unchecked")
		List <EnvioRespostasQuestionarioTurma> es = getSession().createQuery("select r from EnvioRespostasQuestionarioTurma r where ativo = trueValue() " + hqlFinalizados + " and questionario.id = " + q.getId() + hqlUsuario + " order by usuarioEnvio.pessoa.nome, dataCadastro desc").list();
		
		List <EnvioRespostasQuestionarioTurma> rs = es;
		
		// Deve buscar todas as repostas finalizadas pelo discente ou apenas a última.
		boolean todas = (todasRespostas == null || !todasRespostas) ? false : true;
		
		// Deixa somente uma resposta por discente, sendo a última finalizada.
		if (finalizados != null && finalizados.equals(new Boolean(true)) && !todas){
			rs = new ArrayList<EnvioRespostasQuestionarioTurma>();
			for (EnvioRespostasQuestionarioTurma e : es){
				EnvioRespostasQuestionarioTurma remover = null;
				
				boolean adicionar = true;
				
				// Busca por todas as respostas já listadas para garantir que somente o último envio de cada usuário seja considerado.
				for (EnvioRespostasQuestionarioTurma r : rs){
					// Se for do mesmo usuário,
					if (r.getUsuarioEnvio().getId() == e.getUsuarioEnvio().getId()){
						// e o que está sendo verificado for mais recente que o que já está listado, remove o anterior.
						if (r.getDataFinalizacao().before(e.getDataFinalizacao()))
							remover = r;
						// Caso o já listado seja mais recente, não adiciona o que está sendo verificado.
						else
							adicionar = false;
						break;
					}
				}
	
				// Se for para remover o atual, remove e adiciona o novo no seu lugar.
				if (remover != null){
					int indice = rs.indexOf(remover);
					rs.remove(remover);
					rs.add(indice, e);
				// Caso contrário, só adiciona o novo caso não tenha outro para o usuário
				} else if (adicionar)
					rs.add(e);
			}
		}
		
		return rs;
	}
	
	/**
	 * Retorna todas as perguntas ativas da categoria passada por parâmetro.
	 * 
	 * @param idCategoria
	 * @return
	 * @throws DAOException
	 */
	public List <PerguntaQuestionarioTurma> findPerguntasByCategoria (int idCategoria) throws DAOException{
		
		Query q = getSession().createQuery("select p from PerguntaQuestionarioTurma p where p.ativo = trueValue() and p.categoria.id = " + idCategoria + " order by p.nome");
		
		@SuppressWarnings("unchecked")
		List <PerguntaQuestionarioTurma> ps = q.list();
		
		return ps;
	}
	
	/**
	 * Retorna os questionários ativos para serem exibidos na listagem. Feito com projeção para a consulta ficar mais leve.
	 * 
	 * @param idTurma
	 * @return
	 */
	public List <QuestionarioTurma> findQuestionariosAtivosParaListagem(int idTurma,boolean isDocente) {
		try {
			String projecao = "q.id_questionario_turma, a.titulo, q.inicio, q.fim, q.minuto_inicio, q.hora_inicio, q.minuto_fim, q.hora_fim, a.nota_maxima, q.notas_publicadas, q.finalizado, a.possui_nota, q.media_notas";
			String sql = "select " + projecao + ", count(distinct e.id_usuario_envio) from ava.questionario_turma q " +
					"left join ava.envio_respostas_questionario_turma e on e.id_questionario_turma = q.id_questionario_turma and e.ativo = trueValue() " + 
					"left join ava.atividade_avaliavel a on a.id_atividade_avaliavel = q.id_questionario_turma " +
					"inner join ava.topico_aula ta on ta.id_topico_aula = a.id_topico_aula " +
					"where a.ativo = trueValue() and q.id_turma = " + idTurma; 
					
					if (!isDocente) {
						sql += " and ta.visivel = trueValue() ";
					}
					
					sql += " group by " + projecao + " order by q.inicio";
			
			@SuppressWarnings("unchecked")
			List <Object []> rs = getSession().createSQLQuery(sql).list();
			List <QuestionarioTurma> qs = new ArrayList<QuestionarioTurma>();
			
			for (Object [] r : rs){
				int c = 0;
				QuestionarioTurma q = new QuestionarioTurma();
				q.setId(((Number) r[c++]).intValue());
				q.setTitulo((String) r[c++]);
				q.setInicio((Date) r[c++]);
				q.setFim((Date) r[c++]);
				q.setMinutoInicio(((Number) r[c++]).intValue());
				q.setHoraInicio(((Number) r[c++]).intValue());
				q.setMinutoFim(((Number) r[c++]).intValue());
				q.setHoraFim(((Number) r[c++]).intValue());
				
				Object notaMaxima = r[c++];
				if (notaMaxima != null)
					q.setNotaMaxima(((Number) notaMaxima).doubleValue());
				
				q.setNotasPublicadas((Boolean) r[c++]);
				q.setFinalizado((Boolean) r[c++]);
				q.setPossuiNota((Boolean) r[c++]);
				
				Object mediaNotas = r[c++];
				if (mediaNotas != null)
					q.setMediaNotas((Character) mediaNotas);

				q.setQtdRespostas(((Number) r[c++]).intValue());
				
				qs.add(q);
			}
			
			return qs;
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
	}
	
	/**
	 * Retorna as avaliações criadas para os discentes da turma para o questionário passado por parâmetro.
	 * 
	 * @param questionario
	 * @return
	 */
	public List <Avaliacao> findAvaliacoesByQuestionario (QuestionarioTurma questionario) {
		try {
			
			String sql = "select a.id_avaliacao, a.nota, a.nota_maxima, p.id_pessoa from ensino.avaliacao_unidade a join ensino.nota_unidade n on n.id_nota_unidade = a.id_unidade join ensino.matricula_componente m using (id_matricula_componente) join public.discente d using (id_discente) join comum.pessoa p using (id_pessoa) where n.ativo = trueValue() and a.id_atividade_que_gerou = " + questionario.getId();
			
			@SuppressWarnings("unchecked")
			List <Object []> as = getSession().createSQLQuery(sql).list();
			
			List <Avaliacao> rs = new ArrayList<Avaliacao>();
			
			for (Object [] a : as){
				int c = 0;
				Avaliacao avaliacao = new Avaliacao();
				avaliacao.setId(((Number) a[c++]).intValue());
				
				
				Object nota = a[c++];
				if (nota != null)
					avaliacao.setNota(((Number) nota).doubleValue());
				
				Object notaMaxima = a[c++];
				if(notaMaxima != null)
					avaliacao.setNotaMaxima(((Number) notaMaxima).doubleValue());
				
				Pessoa p = new Pessoa();
				p.setId(((Number) a[c++]).intValue());
				
				Discente d = new Discente (); d.setPessoa(p);
				MatriculaComponente m = new MatriculaComponente(); m.setDiscente(d);
				NotaUnidade n = new NotaUnidade(); n.setMatricula(m);
				avaliacao.setUnidade(n);
				
				rs.add(avaliacao);
			}
				
			
			return rs;
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
	}
	
	/**
	 * Retorna o conjunto de respostas de um usuário.
	 * 
	 * @param questionario
	 * @param questionario
	 * @return
	 */
	public ConjuntoRespostasQuestionarioAluno findConjuntoRespostasByQuestionarioUsuario ( QuestionarioTurma questionario , Usuario usuario ) throws HibernateException, DAOException{

		Query q = getSession().createQuery("select c from ConjuntoRespostasQuestionarioAluno c join c.respostas e join e.respostas " +
				" where c.ativo = trueValue() and c.questionario.id = " + questionario.getId() + " and c.usuarioEnvio.id = " +usuario.getId());
		
		q.setMaxResults(1);
		
		ConjuntoRespostasQuestionarioAluno result = (ConjuntoRespostasQuestionarioAluno) q.uniqueResult();
		
		return result;
	}

	/**
	 * Retorna o conjunto de respostas com todas as suas respostas povoadas.
	 * 
	 * @param questionario
	 * @return
	 */
	public ConjuntoRespostasQuestionarioAluno findConjuntoRespostas ( int id ) throws HibernateException, DAOException{

		Query q = getSession().createQuery("select distinct c from ConjuntoRespostasQuestionarioAluno c join fetch  c.respostas e join e.respostas r" +
				" where c.ativo = trueValue() and e.ativo = trueValue() and c.id = " + id);
		
		q.setMaxResults(1);
		
		ConjuntoRespostasQuestionarioAluno result = (ConjuntoRespostasQuestionarioAluno) q.uniqueResult();
		
		return result;
	}
	
	/**
	 * Retorna uma lista com todos os conjuntos de respostas de um questionário
	 * 
	 * @param questionario
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ConjuntoRespostasQuestionarioAluno> findConjuntoRespostasByQuestionario ( QuestionarioTurma questionario ) throws HibernateException, DAOException{

		Query q = getSession().createQuery("select distinct c from ConjuntoRespostasQuestionarioAluno c " +
				" join fetch c.respostas r " +
				" where c.ativo = trueValue() and r.ativo = trueValue() and c.questionario.id = " + questionario.getId() +
				" order by r.dataCadastro asc ");

		List<ConjuntoRespostasQuestionarioAluno> result = q.list();
		
		return result;
	}
	
	/**
	 * Retorna o número de tentativas enviadas pelo usuário
	 * 
	 * @param questionario
	 * @param usuario
	 * @return
	 */
	public Integer findNumeroTentativasUsuario ( QuestionarioTurma questionario , Usuario usuario) throws HibernateException, DAOException{

		Integer result = Integer.valueOf(getSession().createQuery("select count(r) from EnvioRespostasQuestionarioTurma r where ativo = trueValue()  and questionario.id = " +questionario.getId()+ " and usuarioEnvio.id = "+usuario.getId()).uniqueResult().toString());
		
		return result;
	}
	
	/**
	 * Retorna uma coleção de datas de questionarios cadastradas dentro de um número de dias especificados.
     *
	 * @param dias
	 * @param discente
	 * @param ano
	 * @param semestre
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List <QuestionarioTurma> findQuestionariosData (int diasInicio, int diasFim, Discente discente, Usuario usuario, int ano, int semestre) throws HibernateException, DAOException{
		
		Query  query = getSession().createSQLQuery(
				" select  q.id_questionario_turma , a.titulo , q.descricao , q.inicio , " +
				" e.id_envio_respostas_questionario_turma , t.id_turma , cd.nome , q.fim , q.hora_fim , q.minuto_fim " +
				" from ava.questionario_turma q " +
				" left join ava.envio_respostas_questionario_turma e on e.id_questionario_turma = q.id_questionario_turma and id_usuario_envio = "+usuario.getId()+" and e.finalizado = trueValue() and e.ativo = trueValue() " +
				" inner join ava.atividade_avaliavel a on a.id_atividade_avaliavel = q.id_questionario_turma " +
				" inner join ava.topico_aula ta on ta.id_topico_aula = a.id_topico_aula " +
				" inner join ensino.turma t on ta.id_turma = t.id_turma " +
				" inner join ensino.componente_curricular c on t.id_disciplina = c.id_disciplina " +
				" inner join ensino.componente_curricular_detalhes cd  on c.id_detalhe = cd.id_componente_detalhes " +
				" where t.id_turma in " +
				" ( " +
				" 	select m.id_turma from ensino.matricula_componente m " +	
				"  	join ensino.turma t2 on t2.id_turma = m.id_turma " + 	
				"  	where id_situacao_matricula = "+SituacaoMatricula.MATRICULADO.getId()+" and t2.ano = "+ano+" and t2.periodo = "+semestre+" and m.id_discente = "+discente.getId()+" " +
				" ) " +
				" and q.fim >= :dataInicio " +
				" and q.fim <= :dataFim " +
				" and a.ativo = trueValue() " +
				" order by q.fim asc "
				);
		
		Calendar calendario = Calendar.getInstance();
		calendario.add(Calendar.DAY_OF_YEAR, diasFim);
		
		query.setDate("dataFim", calendario.getTime());
		query.setDate("dataInicio", CalendarUtils.subtraiDias(new Date(),diasInicio));
		
		@SuppressWarnings("unchecked")
		List<Object[]> result = query.list();
		List<QuestionarioTurma> questionarios = new ArrayList<QuestionarioTurma>();
		
		if ( result != null )
		{	
			for (Object[] linha : result) {
				Integer i = 0;
				
				QuestionarioTurma q = new QuestionarioTurma();
				q.setId((Integer) linha[i++]);
				q.setTitulo((String) linha[i++]);
				q.setDescricao((String) linha[i++]);
				q.setInicio((Date) linha[i++]);
				q.setRespostas(new ArrayList<EnvioRespostasQuestionarioTurma>());
				Integer idResposta = (Integer) linha[i++];
				if ( idResposta != null ){
					EnvioRespostasQuestionarioTurma e = new EnvioRespostasQuestionarioTurma();
					e.setId(idResposta);
					q.getRespostas().add(e);
				}
				q.setAula( new TopicoAula() );
				q.getAula().setTurma( new Turma () );
				q.getAula().getTurma().setId((Integer) linha[i++]);
				q.getAula().getTurma().setDisciplina(new ComponenteCurricular());
				q.getAula().getTurma().getDisciplina().setDetalhes(new ComponenteDetalhes());
				q.getAula().getTurma().getDisciplina().getDetalhes().setNome((String) linha[i++]);
				q.setFim((Date) linha[i++]);
				q.setHoraFim((Integer) linha[i++]);
				q.setMinutoFim((Integer) linha[i++]);
				
				questionarios.add(q);
			}
		}
		
		return questionarios;

	}

}