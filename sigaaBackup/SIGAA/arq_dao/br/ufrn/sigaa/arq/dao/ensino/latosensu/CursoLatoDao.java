/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 07/11/2006
 *
 */
package br.ufrn.sigaa.arq.dao.ensino.latosensu;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.ensino.dominio.CargoAcademico;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.PropostaCursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta;
import br.ufrn.sigaa.ensino.latosensu.relatorios.LinhaConsultaCursoGeral;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Consultas específicas à CursoLato.
 *
 * @author Leonardo
 *
 */
public class CursoLatoDao extends CursoDao {
	
	/** Sequência utilizada para gerar o código do curso lato */
	private static final String SEQUENCIA_CODIGO_CURSO_LATO = "lato_sensu.seq_codigo_curso_lato_";

	public CursoLatoDao(){
	}

	/**
	 * Busca todos os cursos de lato sensu coordenados pelo servidor passado como parâmetro.
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curso> findAllCoordenadoPor( int idServidor ) throws DAOException{
		try {
			Collection<Curso> resultado = new HashSet<Curso>();

			Query c = getSession().createQuery("select cc from CursoLato cl, CoordenacaoCurso cc" +
					" where cl.id = cc.curso.id" +
					" and cc.servidor.id = :idServidor" +
					" and cc.ativo = :ativo " +
					" and cc.dataInicioMandato <= :hoje " +
					" and cc.dataFimMandato >= :hoje " +
					" and cc.cargoAcademico.id in "+ UFRNUtils.gerarStringIn(new Integer[]{CargoAcademico.COORDENACAO, CargoAcademico.VICE_COORDENACAO}) +
					" and cl.propostaCurso.situacaoProposta.id = :idAceita" +
					" and cl.ativo = :ativo");
			
			Date hoje = CalendarUtils.configuraTempoDaData(new Date(), 0, 0, 0, 0);
			
			c.setInteger("idServidor", idServidor);
			c.setBoolean("ativo", Boolean.TRUE);
			c.setDate("hoje", hoje);
			c.setInteger("idAceita", SituacaoProposta.ACEITA);
			
			@SuppressWarnings("unchecked")
			List<CoordenacaoCurso> result = c.list();

			if( result != null && !result.isEmpty() )
				for(CoordenacaoCurso o: result){
					Curso curso = o.getCurso();
					resultado.add(curso);
				}
			Collection<Curso> lista = resultado;
			return lista;
		} catch( Exception e){
			throw new DAOException(e.getMessage(), e);
		}
	}

	/** Retorna o número de discentes matriculados em um curso.
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	public int getNumeroDiscentesMatriculados(int idCurso) throws DAOException{
		try {
			Query q = getSession().createQuery(
					"select  cast( count(*) as integer) from Discente d where d.curso.id = :idCurso and d.status = :idAtivo");
			q.setInteger("idCurso", idCurso);
			q.setInteger("idAtivo", StatusDiscente.ATIVO);
			return (Integer) q.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}


	/** Retorna o último código cadastrado de uma disciplina de lato sensu (Ex.: LAT0654).
	 * @return
	 * @throws DAOException
	 */
	public Integer getMaxCodigoDisciplina() throws DAOException{
		try {
			Query q = getSession().createSQLQuery(
					"select  cast(  max(substring(codigo from 4)) as integer) from ensino.componente_curricular where nivel = 'L'");

			Integer max = (Integer) q.uniqueResult();
			
			if(max != null)
				return max;
			else
				return 0;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}


	/**
	 * Retorna todos os cursos por nível de ensino.
	 *
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<CursoLato> findAllOtimizado() throws DAOException {
		return findAllByFilter(null);
	}

	/**
	 * Retorna todos os cursos por nível de ensino e situação.
	 *
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<CursoLato> findAllByFilter(Integer idSituacao) throws DAOException {
		try {
			String projecao =  "distinct cl.id_curso, c.nome, c.nivel, pc.id_situacao_proposta, p.nome, c.id_modalidade_educacao ";
			StringBuilder hql = new StringBuilder();
			hql.append(" select " + projecao +
					" from lato_sensu.curso_lato cl" +
					" inner join curso c on cl.id_curso=c.id_curso" +
					" inner join lato_sensu.proposta_curso_lato pc on cl.id_proposta=pc.id_proposta" +
					" inner join ensino.coordenacao_curso cc on ( cc.id_curso = cl.id_curso )" +
					" join rh.servidor s on ( s.id_servidor = cc.id_servidor )" +
					" join comum.pessoa p on ( p.id_pessoa = s.id_pessoa )" +
					" where c.nivel= :nivel" +
					" and cc.data_inicio_mandato <= :dataInicio" +
					" and cc.data_fim_mandato >= :dataFim" +
					" and cc.id_cargo_academico = :cargo");
			
			if (idSituacao != null)
				hql.append(" and pc.id_situacao_proposta = :situacao");
			
			hql.append(" group by cl.id_curso, c.nome, c.nivel, pc.id_situacao_proposta, p.nome, c.id_modalidade_educacao");
			hql.append(" order by c.nome");

			Query q = getSession().createSQLQuery(hql.toString());
			q.setCharacter("nivel", NivelEnsino.LATO);
			q.setDate("dataInicio", new Date());
			q.setDate("dataFim", new Date());
			q.setInteger("cargo", CargoAcademico.COORDENACAO);
			if (idSituacao != null)
				q.setInteger("situacao", idSituacao);

			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			
			Collection<CursoLato> result = new ArrayList<CursoLato>();
			for (Object[] linha : lista) {
				int atrib = 0;

				CursoLato cl = new CursoLato();
				
				cl.setId((Integer) linha[atrib++]);
				cl.setNome((String) linha[atrib++]);
				cl.setNivel((Character) linha[atrib++]);
				cl.getPropostaCurso().getSituacaoProposta().setId((Integer) linha[atrib++]);
				
				CoordenacaoCurso cc = new CoordenacaoCurso();
				cc.getServidor().getPessoa().setNome((String) linha[atrib++]);
				cl.setCoordenacaoAtual(cc);
				cl.setModalidadeEducacao(new ModalidadeEducacao((Integer) linha[atrib++]));
				result.add(cl);
			}
			return result;
		} catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}


	/**
	 * Retorna o curso lato associado à proposta de curso lato especificada como parâmetro.
	 * @param idPropostaCursoLato
	 * @return
	 * @throws DAOException
	 */
	public CursoLato findByPropostaCursoLato(int idPropostaCursoLato) throws DAOException {

		String hql = " SELECT cl FROM CursoLato cl INNER JOIN cl.propostaCurso pc WHERE pc.id = :idProposta ";

		Query q = getSession().createQuery(hql);
		q.setInteger("idProposta", idPropostaCursoLato);
		q.setMaxResults(1);

		return (CursoLato) q.uniqueResult();
	}
	
	/**
	 * Retorna o curso lato correspondente ao ID informado. 
	 * Obs. Tentei usar findbByPrimaryKey mas trazia o dado errado. Falei para David. 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public CursoLato findById(int id) throws DAOException {

		String hql = " From CursoLato c " +
					 " Where c.id = :id " ;

		Query q = getSession().createQuery(hql);
		q.setInteger("id", id);
		q.setMaxResults(1);

		return (CursoLato) q.uniqueResult();
	}

	/**
	 * Retorna a proposta do curso lato.
	 * 
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	public PropostaCursoLato findPropostaByCurso(int idCurso) throws DAOException {
		try {
			Query q = getSession().createQuery(
					"SELECT pc FROM CursoLato cl INNER JOIN cl.propostaCurso pc " +
					"WHERE cl.id = :idCurso AND pc.situacaoProposta.id = :idSituacao");
			q.setInteger("idCurso", idCurso);
			q.setInteger("idSituacao", SituacaoProposta.SUBMETIDA);
			return (PropostaCursoLato) q.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/** Retorna os cursos de lato sensu que possuem em sua proposta a disciplina informada no parâmetro.
	 * @param idComponenteCurricular ID do componente curricular.
	 * @return
	 * @throws DAOException 
	 */
	public Collection<CursoLato> findCursosByDisciplina(int idComponenteCurricular) throws DAOException {
		Criteria c = getSession().createCriteria(CursoLato.class);
		c.setProjection(Projections.projectionList()
				.add(Projections.distinct( Projections.property( "id" )))
				.add(Projections.property("nome")) );
		c.add(Restrictions.le("dataInicio", new Date()));
		c.add(Restrictions.ge("dataFim", new Date()));
		c.createCriteria("componentesCursoLato").createCriteria("disciplina")
		.add(Restrictions.eq("id", idComponenteCurricular));
		@SuppressWarnings("unchecked")
        List <Object[]> rs = c.list();
        Collection<CursoLato> lista = HibernateUtils.parseTo(rs, "id, nome", CursoLato.class, "this_");
		return lista;
	}
	
	/**
	 * Verifica se um dado docente lecionará pelo menos uma disciplina 
	 * oferecida dentro da Proposta de um novo Curso de Lato Sensu.
	 * 
	 * @param idServidor ID do servidor.
	 * @param idProposta ID da proposta de curso.
	 * @return
	 * @throws DAOException 
	 * @throws DAOException 
	 */
	public boolean lecionaDisciplina (int idServidor, int idProposta) throws DAOException{
	
		StringBuffer hql = new StringBuffer();
		hql.append(" SELECT COUNT(*) ");
		hql.append(" FROM  CorpoDocenteDisciplinaLato");
		hql.append(" WHERE id_servidor = :servidor ");
		hql.append(" AND id_proposta = :proposta");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("servidor", idServidor);
		q.setInteger("proposta", idProposta);
		
		Long qntDisciplinas = (Long) q.uniqueResult();

		return qntDisciplinas != 0;
	}
	
	/**
	 * Monta uma coleção de {@link LinhaConsultaCursoGeral} de acordo com os dados passados.
	 * 
	 * @param ano
	 * @param coordenador
	 * @param curso
	 * @param situacao
	 * @param area
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<LinhaConsultaCursoGeral> filter(Integer ano,Date dataInicio,Date dataFim, Servidor coordenador, Curso curso, SituacaoProposta situacao, 
			AreaConhecimentoCnpq area, Integer sequenciaCodigo, Integer anoCodigo , boolean apenasCoordenador ) throws DAOException {
		
		StringBuilder sql = new StringBuilder("select distinct c.id_curso, c.nome, sp.id_situacao_proposta, sp.descricao, cl.data_inicio," +
				    "( case when (cc.data_fim_mandato < :hoje) then 'INATIVO' else p.nome end) as coordenador, " +
					" acc2.nome as area, mod.id_modalidade_educacao, cl.cod_prefixo, cl.cod_numero, cl.cod_ano " +
					" from lato_sensu.curso_lato cl inner join curso c on (cl.id_curso = c.id_curso)" +
					" inner join lato_sensu.proposta_curso_lato pcl on ( cl.id_proposta = pcl.id_proposta )" +
					" inner join lato_sensu.situacao_proposta sp on (sp.id_situacao_proposta = pcl.id_situacao_proposta)" +
					" inner join ensino.coordenacao_curso cc on (cl.id_curso = cc.id_curso)" +
					" inner join rh.servidor s on (cc.id_servidor = s.id_servidor)" +
					" inner join comum.pessoa p on (s.id_pessoa = p.id_pessoa)" +
					" inner join comum.area_conhecimento_cnpq acc on (acc.id_area_conhecimento_cnpq = cl.id_area_conhecimento_cnpq)" +
					" inner join comum.area_conhecimento_cnpq acc2 on (acc.id_grande_area = acc2.id_area_conhecimento_cnpq)" +
					" left join comum.modalidade_educacao mod on (c.id_modalidade_educacao = mod.id_modalidade_educacao)" +
					" where c.ativo = trueValue()" +
					" and cc.data_fim_mandato = (select max(data_fim_mandato) from ensino.coordenacao_curso where id_curso = c.id_curso)" +
					" and id_cargo_academico = " + CargoAcademico.COORDENACAO +
					" and cc.ativo = trueValue()");
					
				if ( apenasCoordenador) {
					if (coordenador != null )
						sql.append(" and cc.id_servidor = :idCoordenador " );
				}
				
				if ( !apenasCoordenador && coordenador != null ) {
					sql.append(" and cc.id_curso in ( select distinct id_curso from ensino.coordenacao_curso " +
						" where id_servidor = :idCoordenador and ativo = trueValue() " +
						" and id_cargo_academico in " + UFRNUtils.gerarStringIn(new int[]{ CargoAcademico.COORDENACAO, CargoAcademico.VICE_COORDENACAO }) + " ) " );
				}
			
		if (ano != null) 
			sql.append(" and DATE_PART('year', cl.data_inicio) = :ano");
		if(dataInicio != null)
			sql.append(" and cl.data_inicio >= :dataInicio");
		if(dataFim != null)
			sql.append(" and cl.data_fim <= :dataFim");
		if (curso != null) {
			if (!isEmpty(curso.getNome())) 
				sql.append(" and c.nome_ascii ilike :nomeCurso");
			else if (curso.getId() != 0)
				sql.append(" and cl.id_curso = :idCurso");
		}
		if( anoCodigo != null ){
			sql.append(" and cl.cod_ano = :codigoAno ");
		}
		if( sequenciaCodigo != null ){
			sql.append(" and cl.cod_numero = :codigoNumero ");
		}
		
		if (situacao != null && situacao.getId() != 0) 
			sql.append(" and pcl.id_situacao_proposta = :idSituacaoProposta");
        if (area != null && area.getId() != 0)
        	sql.append(" and acc2.id_area_conhecimento_cnpq = :idArea");
        
		sql.append(" order by c.nome");
        Collection<LinhaConsultaCursoGeral> cursoLato = new ArrayList<LinhaConsultaCursoGeral>();
        SQLQuery q = getSession().createSQLQuery(sql.toString());
        q.setDate("hoje", new Date());
        if ( coordenador != null )
        	q.setInteger("idCoordenador", coordenador.getId());
        if (ano != null) 
			q.setInteger("ano", ano);
        if(dataInicio != null)
        	q.setDate("dataInicio", dataInicio);
        if(dataFim != null)
        	q.setDate("dataFim", dataFim);
		if (curso != null) {
			if (!isEmpty(curso.getNome())) 
				q.setString("nomeCurso", "%" + curso.getNomeAscii()+"%");
			else if (curso.getId() != 0)
				q.setInteger("idCurso", curso.getId());
		}
		if( anoCodigo != null ){
			q.setInteger("codigoAno",anoCodigo);
		}
		if( sequenciaCodigo != null ){
			q.setInteger("codigoNumero",sequenciaCodigo);
		}
		
		if (situacao != null && situacao.getId() != 0) 
			q.setInteger("idSituacaoProposta", situacao.getId());
        if (area != null && area.getId() != 0)
        	q.setInteger("idArea", area.getId());

        List<Object[]> list = q.list();
        
		for (Object[] item : list) {
			LinhaConsultaCursoGeral linha = new LinhaConsultaCursoGeral();
			linha.setIdCurso((Integer) item[0]);
			if (item[7] != null && ((Integer) item[7]) == ModalidadeEducacao.A_DISTANCIA)
				linha.setNomeCurso((String) item[1] + " - EAD");
			else
				linha.setNomeCurso((String) item[1]);
			
			linha.setSituacao(new SituacaoProposta());
			linha.getSituacao().setId((Integer) item[2]);
			linha.getSituacao().setDescricao((String) item[3]);
			linha.setDataInicio((Date) item[4]);
			linha.setCoordenador((String) item[5]);
			linha.setAreaConhecimento((String) item[6]);
			linha.setPrefixoCodigo( (String) item[8]);
			linha.setNumeroCodigo( (Integer) item[9]);
			linha.setAno( (Short) item[10]);
				
			cursoLato.add(linha);
		}
		return cursoLato;
	}


	/**
	 * Lista todas as proposta de Curso Lato Sensu, sobre a coordenação de um determinado Docente
	 * 
	 * @param servidor
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<CursoLato> findAllByPropostaServidor(Servidor servidor) throws HibernateException, DAOException {

		StringBuilder sql = 
				new StringBuilder("SELECT distinct(cl.id_curso), c.nome, sp.descricao, cl.ch, cl.data_inicio, cl.data_fim, " +
						" sp.valida, tcl.descricao as tipo_curso, sp.id_situacao_proposta" +
						" FROM ensino.coordenacao_curso cc" +
						" JOIN rh.servidor s using (id_servidor)" +
						" JOIN lato_sensu.curso_lato cl using (id_curso)" +
						" JOIN curso c using (id_curso)" +
						" JOIN lato_sensu.proposta_curso_lato pcl using (id_proposta)" +
						" JOIN lato_sensu.situacao_proposta sp using (id_situacao_proposta)" +
						" JOIN lato_sensu.tipo_curso_lato tcl using (id_tipo_curso_lato)" +
						" WHERE cc.id_servidor = " + servidor.getId() + " AND cc.ativo = trueValue()" +
						" AND s.id_ativo = " + Ativo.SERVIDOR_ATIVO +	
						" AND sp.id_situacao_proposta <> " + SituacaoProposta.EXCLUIDA + 
						" AND cc.id_cargo_academico in " + UFRNUtils.gerarStringIn(new int[]{CargoAcademico.COORDENACAO, CargoAcademico.VICE_COORDENACAO}) +
						" order by sp.id_situacao_proposta ");

		Collection<CursoLato> cursoLato = new ArrayList<CursoLato>();
	    @SuppressWarnings("unchecked")
		List<Object[]> list = getSession().createSQLQuery(sql.toString()).list();
        
		for (Object[] item : list) {
			int k = 0;
			CursoLato linha = new CursoLato();
			linha.setId((Integer) item[k]);
			linha.setNome((String) item[++k]);
			linha.getPropostaCurso().getSituacaoProposta().setDescricao((String) item[++k]);
			linha.setCargaHoraria((Integer) item[++k]);
			linha.setDataInicio((Date) item[++k]);
			linha.setDataFim((Date) item[++k]);
			linha.getPropostaCurso().getSituacaoProposta().setValida((Boolean) item[++k]);
			linha.getTipoCursoLato().setDescricao((String) item[++k]);
			cursoLato.add(linha);
		}
		return cursoLato;
	}
	
	/**
	 * Retorna todos os cursos lato cujas Situações esteja dentro da situação informada.
	 * 
	 * @param cursos
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Curso> findAllBySituacoes(Integer... situacoes) throws HibernateException, DAOException {

		StringBuilder hql = new StringBuilder(" FROM CursoLato c");
		hql.append(" WHERE c.propostaCurso.situacaoProposta.id IN " + gerarStringIn(situacoes) );
		hql.append(" ORDER BY c.propostaCurso.situacaoProposta.id");
		
		Query q = getSession().createQuery(hql.toString());
		
		@SuppressWarnings("unchecked")
		Collection<Curso> lista = q.list();
		return lista;

	}

	/**
	 * Retorna todos os cursos lato cujas Situações esteja dentro da situação informada.
	 * 
	 * @param cursos
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<HashMap<String,Object>> findAndamentoCursoLato(int anoInicial, int anoFinal) throws HibernateException, DAOException {
		StringBuilder hql = new StringBuilder(
				"SELECT cl.data_inicio, cl.data_fim, c.nome,"
						+ " count(distinct ccl.id_componente_curricular) as total_componentes,"
						+ " count(distinct t.id_disciplina) as total_turmas,"
						+ " round(100.0*count(distinct t.id_disciplina)/count(distinct ccl.id_componente_curricular), 2) as andamento,"
						+ " count(distinct d.id_discente) as discentes_ativos"
						+ " from lato_sensu.curso_lato cl"
						+ " inner join curso c using (id_curso)"
						+ " left join discente d on (c.id_curso = d.id_curso and d.status = :ativo)"
						+ " inner join lato_sensu.proposta_curso_lato pc using (id_proposta)"
						+ " inner join lato_sensu.componente_curso_lato ccl on (id_curso_lato = c.id_curso)"
						+ " left join ensino.turma t on (ccl.id_componente_curricular = t.id_disciplina AND t.id_situacao_turma = :consolidada)"
						+ " WHERE cl.data_inicio >= :inicio"
						+ " AND cl.data_fim <= :fim"
						+ " AND pc.id_situacao_proposta = ")
		.append(SituacaoProposta.ACEITA)
		.append(" GROUP BY cl.data_inicio, cl.data_fim, c.nome" +
				" ORDER BY c.nome, cl.data_inicio");
		
		Query q = getSession().createSQLQuery(hql.toString());
		q.setDate("inicio", CalendarUtils.createDate(01, 0, anoInicial));
		q.setDate("fim", CalendarUtils.createDate(31, 11, anoFinal));
		q.setInteger("consolidada", SituacaoTurma.CONSOLIDADA);
		q.setInteger("ativo", StatusDiscente.ATIVO);
		@SuppressWarnings("unchecked")
		List<HashMap<String, Object>> lista = q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		return lista;
	}
	

	/**
	 * Busca o próximo número na sequência para gerar o código do curso lato.
	 * 
	 * @param ano
	 * @return
	 * @throws DAOException 
	 */
	public Integer findNextSequenciaCursoLato(int ano) throws DAOException {
		String nomeSequencia = SEQUENCIA_CODIGO_CURSO_LATO + ano;
		return getNextSeq(nomeSequencia);
	}

}