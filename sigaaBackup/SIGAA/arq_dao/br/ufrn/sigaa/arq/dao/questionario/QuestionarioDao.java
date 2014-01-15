/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '04/12/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.questionario;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.questionario.dominio.Alternativa;
import br.ufrn.sigaa.questionario.dominio.PerguntaQuestionario;
import br.ufrn.sigaa.questionario.dominio.Questionario;
import br.ufrn.sigaa.questionario.dominio.QuestionarioRespostas;
import br.ufrn.sigaa.questionario.dominio.TipoQuestionario;

/**
 * DAO respons�vel por gerenciar o question�rio
 *
 * @author Victor Hugo
 *
 */
public class QuestionarioDao extends GenericSigaaDAO {

	/** 
	 * Busca question�rios de acordo com o tipo especificado
	 * 
	 * @param tipo @see TipoQuestionario
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public Collection<Questionario> findByTipo(int tipo) throws HibernateException, DAOException{
		return getSession().createCriteria(Questionario.class)
		.add(Expression.eq("tipo.id", tipo))
		.add( Expression.eq("ativo", Boolean.TRUE))
		.addOrder(Order.desc("dataCadastro"))
		.list();		
	}
	
	/** 
	 * Busca question�rios de acordo com os tipos especificados
	 * 
	 * @param tipo @see TipoQuestionario
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public Collection<Questionario> findByTipos(Integer... tipos) throws HibernateException, DAOException{
		return getSession().createCriteria(Questionario.class)
		.add(Expression.in("tipo.id", tipos))
		.add( Expression.eq("ativo", Boolean.TRUE))
		.addOrder(Order.desc("dataCadastro"))
		.list();		
	}
	
	/** 
	 * Busca question�rios de acordo com o tipo especificado
	 * 
	 * @param tipo @see TipoQuestionario
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public Collection<TipoQuestionario> findTiposById(Integer... ids) throws HibernateException, DAOException{
		return getSession().createCriteria(TipoQuestionario.class)
		.add(Expression.in("id", ids))
		.addOrder(Order.desc("descricao"))
		.list();		
	}

	/**
	 * Buscar os question�rios referentes a processos seletivos pertencentes a uma determinada unidade
	 * 
	 * @param unidade
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Questionario> findQuestionariosProcessosSeletivos( Unidade unidade ) throws HibernateException, DAOException {
		return getSession().createCriteria(Questionario.class)
			.add( Expression.eq("tipo.id", TipoQuestionario.PROCESSO_SELETIVO ) )
			.add( Expression.eq("unidade.id", unidade.getId()) )
			.add( Expression.eq("ativo", Boolean.TRUE))
			.addOrder(Order.desc("dataCadastro") )
			.list();		
	}
	
	/**
	 * Buscar os question�rios referentes a processos seletivos di tipo unidade acad�mica passado no par�metro.
	 * 
	 * @param unidade
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Questionario> findByTipoUnidadeAcademica( int tipoAcademica) throws HibernateException, DAOException {
		
			Criteria c = getSession().createCriteria(Questionario.class);
			c.add(Expression.eq("tipo.id", TipoQuestionario.PROCESSO_SELETIVO ));
			c.add( Expression.eq("ativo", Boolean.TRUE));
			c.addOrder(Order.desc("dataCadastro") );
			Criteria cUnidade = c.createCriteria("unidade");
			cUnidade.add( Expression.eq("tipoAcademica", tipoAcademica) );
		 
		 return c.list();
	}

	/** 
	 * Busca question�rios de acordo com o tipo especificado
	 * 
	 * @param tipo @see TipoQuestionario
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public List<Questionario> findQuestionarioPorTipoData(int tipo, Date dataInicio, Date dataFim) throws HibernateException, DAOException{
		return getSession().createCriteria(Questionario.class)
		.add(Expression.eq("tipo.id", tipo))
		.add( Expression.eq("ativo", Boolean.TRUE))
		.add( Expression.ge("inicio", dataInicio != null ? dataInicio : new Date()) )
		.add( Expression.le("fim", dataFim != null ? dataFim : new Date()) )
		.list();		
	}

	/** 
	 * Busca question�rios de acordo com o tipo especificado e se est� aberto.
	 * 
	 * @param tipo @see TipoQuestionario
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public List<Questionario> findQuestionarioAberto(int tipo) throws HibernateException, DAOException{
		Date dataAtual = new Date();
		dataAtual = CalendarUtils.descartarHoras(dataAtual);

		return getSession().createCriteria(Questionario.class)
		.add(Expression.eq("tipo.id", tipo))
		.add( Expression.eq("ativo", Boolean.TRUE))
		.add( Expression.le("inicio", dataAtual) )
		.add( Expression.ge("fim", dataAtual) )
		.list();		
	}
	
	
	/**
	 * <p>Buscar os question�rios referentes a inscri��es em Atividade de acordo com a unidade.</p>
	 * 
	 * <p>Pode ser passado uma proje��o para n�o precisar retornar todos os dados do question�rio. 
	 * Por exemplo, nos comboxes s� interesse o id e nome. </p>
	 * 
	 * @param unidade
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Questionario> findQuestionariosInscricaoAtividadeByUnidade( Unidade unidade, String... projections) throws DAOException {
		Criteria c = getSession().createCriteria(Questionario.class);
		
		StringBuilder projecao = new StringBuilder();
		
		if(projections != null && projections.length > 0){
			
			ProjectionList projectionList = Projections.projectionList();
			
			for (String projection : projections) {
				projectionList.add(Projections.property(projection) );
				
				projecao.append(projection+", ");
			}
			
			c.setProjection( projectionList );
			
		}
				
		c.add( Expression.eq("tipo.id", TipoQuestionario.QUESTIONARIO_INSCRICAO_ATIVIDADE ) )
		.add( Expression.eq("unidade.id", unidade.getId()) )
		.add( Expression.eq("ativo", Boolean.TRUE))
		.addOrder(Order.desc("dataCadastro") );	
		
		if(projections == null || projections.length == 0 )
			return c.list();
		else
			return  HibernateUtils.parseTo(c.list(), projecao.toString().substring(0, projecao.toString().length()-2), Questionario.class);
	}


	
	
	/**
	 * Retorna a quantidade de inscritos de um processo seletivo ativos e que estejam aberto.
	 * 
	 * @param adesao
	 * @return
	 * @throws DAOException
	 */
	public int findQtdInscritosByQuestionario(Integer idQuestionario) throws DAOException {
		
		Date dataAtual = new Date(System.currentTimeMillis());
		return getJdbcTemplate().queryForInt(
				" SELECT COUNT(i.id_inscricao_atividade_participante) " +
				" FROM extensao.inscricao_atividade_participante i " +
				" INNER JOIN extensao.inscricao_atividade ia USING(id_inscricao_atividade) " +				
				" WHERE ia.periodo_inicio <= '" + dataAtual + "' AND ia.periodo_fim >= '" + dataAtual + "'" +
				" AND ia.ativo = trueValue() AND ia.id_questionario = " + idQuestionario);

	}
	
	
	/**
	 * <p>Retorna uma lista das respostas aos question�rios ( apenas com os ids ) do question�rio da inscri��o de atividade de extens�o passada.</p>
	 * 
	 * <p>Esse m�todo � utilizando para recuperar o id das respotas, quando o usu�rio desejar visualizar � que mostrar o question�rio completo. </p>
	 * 
	 * @param unidade
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<QuestionarioRespostas> findIdsRespostasInscricaoAtividadeExtensaoByQuestionario( int idQuestionario) throws DAOException {
		
		String projecao = " respostasUsuario.id, respostasUsuario.inscricaoAtividadeParticipante.id  ";
		
		String hql = " SELECT  "+projecao 
					+" FROM QuestionarioRespostas respostasUsuario "
			        +" WHERE respostasUsuario.questionario.tipo.id = "+TipoQuestionario.QUESTIONARIO_INSCRICAO_ATIVIDADE
			        +" AND respostasUsuario.questionario.id = :idQuestionario ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idQuestionario", idQuestionario);
		
		return new ArrayList<QuestionarioRespostas>(HibernateUtils.parseTo(q.list(), projecao, QuestionarioRespostas.class, "respostasUsuario"));
		
	}
	
	
	/**
	 * <p>Retorna os dado de um question�rio, usado quando se precisa carregar os dados de um question�rio para o usu�rio responder..</p>
	 * 
	 * <p> Para recuperar as respostas dadas pelo usu�rio a um question�rio utilize do m�todo 
	 * {@link QuestionarioRespostasDao#findInformacaoRespostasQuestionario(int, int)}.</p>
	 * 
	 * @param unidade
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Questionario findInformacaoQuestionario( int idQuestionario) throws DAOException {
		
		String projecao = " questionario.id as idQuestionario, questionario.titulo, tipo.id as idTipoQuestionario, "+
						  " perguntas.id, perguntas.tipo as tipoPergunta, perguntas.pergunta, perguntas.ordem, perguntas.obrigatoria, perguntas.maxCaracteres, "+
						  " alternativas.id as idAlternativa, alternativas.alternativa, alternativas.peso, alternativas.pergunta.id as idPerguntaAlternativa";
		
		String hql = " SELECT  "+projecao 
					+" FROM Questionario questionario "
					+" INNER JOIN questionario.tipo tipo "
					+" INNER JOIN questionario.perguntas perguntas "
					+" LEFT JOIN perguntas.alternativas alternativas "
			        +" WHERE questionario.id = :idQuestionario "
			        +" AND perguntas.ativo = :true AND ( alternativas IS NULL OR alternativas.ativo = :true ) "
			        +" ORDER BY perguntas.ordem";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idQuestionario", idQuestionario);
		q.setBoolean("true", true);
		
		List<Object[]> objects = q.list(); 
		
		Questionario retorno = new Questionario();
		
		for (Object[] item : objects) {
			
			if(retorno.getId() == 0){
				retorno.setId((Integer)item[0]);
				retorno.setTitulo((String)item[1]);
				retorno.setTipo(new TipoQuestionario((Integer)item[2]));
			}
			
			PerguntaQuestionario pergunta = new PerguntaQuestionario();
			pergunta.setId( (Integer)item[3] );
			pergunta.setTipo( (Integer)item[4] );
			pergunta.setPergunta( (String)item[5] );
			pergunta.setOrdem( (Integer)item[6] );
			pergunta.setObrigatoria( (Boolean)item[7] );
			pergunta.setMaxCaracteres( (Integer)item[8] );
			
			if(! retorno.containsPergunta(pergunta)){
				retorno.adicionaPergunta(pergunta);
			}
			
			if(item[9] != null ){ // se a partunta tem alternativa 
				Alternativa alternativa = new Alternativa();
				alternativa.setId( (Integer) item[9]);
				alternativa.setAlternativa((String) item[10]);
				alternativa.setPeso((Integer) item[11]);
				
				PerguntaQuestionario perguntaDaAlternativo = retorno.getPerguntas().get(retorno.getPerguntas().indexOf(new PerguntaQuestionario((Integer) item[12])));
				
				perguntaDaAlternativo.adicionaAlternativa(alternativa);
			}
		}
		
		return retorno;
		
	}
	
	/**
	 * Verifica se o question�rios est� respondido, contando as respostas.
	 */
	public boolean questionarioJaRespondido( int idQuestionario) throws DAOException {
		String sql = " select count(*) from questionario.questionario_respostas where id_questionario = " + idQuestionario;
		Query q = getSession().createSQLQuery(sql);
		return ((BigInteger) q.uniqueResult()).intValue() > 0;
	}
	
}