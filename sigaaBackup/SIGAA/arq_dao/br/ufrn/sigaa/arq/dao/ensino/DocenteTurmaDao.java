/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 29/01/2009
 *
 */
package br.ufrn.sigaa.arq.dao.ensino;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dao.PerfilPessoaDAO;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/** 
 * Dao responsável por realizar consultas sobre a entidade DocenteTurma  
 * @author Victor Hugo
 */
public class DocenteTurmaDao extends GenericSigaaDAO {

	
	/**
	 * Busca os docentes das turmas de uma unidade,ano, período informados
	 * @param idUnidade
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<DocenteTurma> findByAnoPeriodoUnidade(int idUnidade, Integer ano, Integer periodo) throws DAOException{
		return findGeral(idUnidade, null, ano, periodo);
	}
	
	/**
	 * Busca os docentes das turmas conforme os parâmetros setados.
	 * @param idUnidade
	 * @param nivel
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<DocenteTurma> findGeral(int idUnidade, Character nivel, Integer ano, Integer periodo, Integer... situacao)
		throws DAOException{
		
		try {
			
			StringBuilder hql = new StringBuilder( " SELECT dt.id AS ID, dt.chDedicadaPeriodo AS CH_DEDICADA, " );
			hql.append( " doc.id AS ID_SERVIDOR, doc.siape AS SIAPE, p1.nome AS NOME_SERVIDOR, " );
			hql.append( " docExt.id AS ID_DOC_EXTERNO, p2.cpf_cnpj AS CPF,  p2.nome AS NOME_DOCENTE_EXTERNO, " );
			hql.append( " dt.turma.id AS ID_TURMA, dt.turma.codigo AS COD_TURMA, dt.turma.local AS LOCAL, " );
			hql.append( " dt.turma.disciplina.id AS ID_DISCIPLINA, dt.turma.disciplina.codigo AS COD_DISCIPLINA, dt.turma.disciplina.detalhes.nome AS NOME_DISCIPLINA, " );
			hql.append( " dt.turma.disciplina.detalhes.chTotal AS CH_TOTAL, dt.turma.ano AS ANO, dt.turma.periodo AS PERIODO " );
			hql.append( " FROM DocenteTurma dt" );
			hql.append( " LEFT JOIN dt.docente doc LEFT JOIN doc.pessoa p1 " );
			hql.append( " LEFT JOIN dt.docenteExterno docExt LEFT JOIN docExt.pessoa p2 " );
			hql.append( " WHERE 1 = 1  " );
			
			if( idUnidade > 0 )
				hql.append( " AND dt.turma.disciplina.unidade.id = :idUnidade " );
			if( !isEmpty( ano ) )
				hql.append( " AND dt.turma.ano = :ano " );
			if( !isEmpty( nivel ) )
				hql.append(" AND dt.turma.disciplina.nivel = :nivel ");
			if( !isEmpty( periodo ) )
				hql.append( " AND dt.turma.periodo = :periodo " );
			if( !isEmpty(situacao) )
				hql.append(" AND dt.turma.situacaoTurma.id IN " + gerarStringIn(situacao) + "");
			
			hql.append( " ORDER BY dt.turma.disciplina.codigo, dt.turma.ano, dt.turma.periodo, dt.turma.codigo " );
	
			Query q = getSession().createQuery( hql.toString() )  ;
			
			if( idUnidade > 0 )
				q.setInteger( "idUnidade", idUnidade);
			if( !isEmpty( ano ) )
				q.setInteger( "ano", ano);
			if( !isEmpty( periodo ) )
				q.setInteger( "periodo", periodo);
			if( !isEmpty( nivel ) )
				q.setCharacter( "nivel", nivel);
			
		
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> lista = q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
			
			
			List<DocenteTurma> resultado = new ArrayList<DocenteTurma>();
			for( Map<String, Object> map : lista ){
				DocenteTurma dt = new DocenteTurma();
				
				dt.setId( (Integer) map.get( "ID" ) );
				dt.setChDedicadaPeriodo( (Integer) map.get( "CH_DEDICADA" ) );
				if( map.get( "SIAPE" ) != null ){
					dt.setDocente( new Servidor() );
					dt.getDocente().setId( (Integer) map.get( "ID_SERVIDOR" ) );
					dt.getDocente().setSiape( (Integer) map.get( "SIAPE" ) );
					dt.getDocente().setPessoa(new Pessoa());
					dt.getDocente().getPessoa().setNome( (String) map.get( "NOME_SERVIDOR" ) );
				}else if( map.get( "CPF" ) != null ){
					dt.setDocenteExterno(new DocenteExterno());
					dt.getDocenteExterno().setPessoa(new Pessoa());
					dt.getDocenteExterno().setId( (Integer) map.get( "ID_DOC_EXTERNO" ) );
					dt.getDocenteExterno().getPessoa().setCpf_cnpj( (Long) map.get( "CPF" ) );
					dt.getDocenteExterno().getPessoa().setNome( (String) map.get( "NOME_DOCENTE_EXTERNO" ) );
				}
				
				dt.setTurma( new Turma() );
				dt.getTurma().setAno( (Integer) map.get("ANO") );
				dt.getTurma().setPeriodo( (Integer) map.get("PERIODO") );
				dt.getTurma().setId( (Integer) map.get( "ID_TURMA" ) );
				dt.getTurma().setCodigo( (String) map.get( "COD_TURMA" ) );
				dt.getTurma().setLocal( (String) map.get( "LOCAL" ) );
				dt.getTurma().setDisciplina( new ComponenteCurricular((Integer) map.get( "ID_DISCIPLINA" )) );
				dt.getTurma().getDisciplina().setCodigo( (String) map.get( "COD_DISCIPLINA" ) );
				dt.getTurma().getDisciplina().getDetalhes().setCodigo( (String) map.get( "COD_DISCIPLINA" ) );
				dt.getTurma().getDisciplina().setDetalhes( new ComponenteDetalhes() );
				dt.getTurma().getDisciplina().getDetalhes().setNome( (String) map.get( "NOME_DISCIPLINA" ) );
				dt.getTurma().getDisciplina().getDetalhes().setChTotal( (Integer) map.get( "CH_TOTAL" ) );
//				dt.setChDedicadaPeriodo( (Integer) map.get( "CH_TOTAL" ) );
				
				resultado.add(dt);
			}
			
			return resultado;
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Localiza DocenteTurma dado a turma e o docente
	 * 
	 * @param idDocente
	 * @param idTurma
	 * @return
	 * @throws DAOException
	 */
	public DocenteTurma findByDocenteTurma(Integer idDocente, Integer idDocenteExterno, int idTurma) throws DAOException {
		
		StringBuffer hql = new StringBuffer();
		
		hql.append("select dt from DocenteTurma dt where dt.");
		
		if (idDocente != null)
			hql.append("docente.id = " + idDocente);
		else if (idDocenteExterno != null)
			hql.append("docenteExterno.id = " + idDocenteExterno);
		else
			return null;
		
		hql.append(" and dt.turma.id = " + idTurma);
		
		return (DocenteTurma) getSession().createQuery(hql.toString()).uniqueResult();
	}
	
	/**
	 * Localiza todos os DocenteTurma cadastrados para o docente informado na turma.
	 * 
	 * @param idDocente
	 * @param idTurma
	 * @return
	 * @throws DAOException
	 */
	public List<DocenteTurma> findAllByDocenteTurma(Integer idDocente, Integer idDocenteExterno, int idTurma) throws DAOException {
		
		StringBuffer hql = new StringBuffer();
		
		hql.append("select dt from DocenteTurma dt where dt.");
		
		if (idDocente != null)
			hql.append("docente.id = " + idDocente);
		else if (idDocenteExterno != null)
			hql.append("docenteExterno.id = " + idDocenteExterno);
		else
			return null;
		
		hql.append(" and dt.turma.id = " + idTurma);
		
		Query q = getSession().createQuery(hql.toString());
		
		@SuppressWarnings("unchecked")
		List<DocenteTurma> list = q.list();
		
		return list;
	}
	
	/**
	 * Localiza DocenteTurma dado a turma e o docente
	 * 
	 * @param idDocente
	 * @param idTurma
	 * @return
	 * @throws DAOException
	 */
	public ArrayList<DocenteTurma> findByDocenteTurma(int idDocente, int ano, int periodo) throws DAOException {
		
		List<Integer> statusValidos = new ArrayList<Integer>();
		statusValidos.addAll(Arrays.asList(SituacaoTurma.ABERTA, SituacaoTurma.CONSOLIDADA));
		
		String hql =  " select dt from DocenteTurma dt " +
					  " INNER JOIN FETCH dt.turma t " +
					  " INNER JOIN t.disciplina disciplina " +
					  " where dt.docente.id = ? and dt.turma.ano = ? and dt.turma.periodo = ? " +
					  " and dt.turma.situacaoTurma in " + UFRNUtils.gerarStringIn( statusValidos ) +
					  " and disciplina.nivel <> ? " +
					  " order by dt.turma.ano, dt.turma.periodo, disciplina.nivel, disciplina.detalhes.nome";
		
		@SuppressWarnings("unchecked")
		List<DocenteTurma> lista = getSession().createQuery(hql).setInteger(0, idDocente).
			setInteger(1, ano).setInteger(2, periodo).
			setCharacter(3, NivelEnsino.FORMACAO_COMPLEMENTAR).list();
		
		// remove duplicações de turmas que ocorre quando o docente é de EAD.  
		Set<Integer> listasCodigoTurmas = new LinkedHashSet<Integer>();
		ArrayList<DocenteTurma> listaSemDup = new ArrayList<DocenteTurma>();
		
		for (DocenteTurma docenteTurma : lista) {
			if (docenteTurma.getTurma().isEad())
				listasCodigoTurmas.add(docenteTurma.getDisciplina().getId());
			else
				listaSemDup.add(docenteTurma);
		}
		
		for (DocenteTurma docenteTurma : lista) {
			if ( listasCodigoTurmas.contains( docenteTurma.getTurma().getDisciplina().getId() ) ) {
				listaSemDup.add(docenteTurma);
				listasCodigoTurmas.remove( docenteTurma.getTurma().getDisciplina().getId() );
			}
		}
		
		return listaSemDup;
	}

	/**
	 * Retorna DocenteTurma pelo ID da turma 
	 * 
	 * @param idTurma
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<DocenteTurma> findByTurma(int idTurma) throws HibernateException, DAOException {
		
		List <Integer> idsTurmas = getSession().createQuery("select t.id from Turma t where t.id = " + idTurma + " or t.turmaAgrupadora.id = " + idTurma).list();
		
		List <DocenteTurma> dts = getSession().createQuery("select dt from DocenteTurma dt where dt.turma.id in "+UFRNUtils.gerarStringIn(idsTurmas)).list();
		List <DocenteTurma> remover = new ArrayList <DocenteTurma> ();
		
		// Remove os docenteTurma com pessoa repetida.
		for (int i = 0; i < dts.size(); i++){
			
			int idPessoa1 = dts.get(i).getDocente() != null ? dts.get(i).getDocente().getPessoa().getId() : dts.get(i).getDocenteExterno().getPessoa().getId();
			
			for (int j = i + 1; j < dts.size(); j++){
				int idPessoa2 = dts.get(j).getDocente() != null ? dts.get(j).getDocente().getPessoa().getId() : dts.get(j).getDocenteExterno().getPessoa().getId();
				if (idPessoa1 == idPessoa2)
					remover.add(dts.get(j));
			}
		}
		
		for (DocenteTurma dt : remover)
			dts.remove(dt);
			
		Collections.sort(dts);
		
		return dts;
	}
	
	
	/**
	 * Retorna a lista dos docentes com carga horária de aula dedicada a turmas com vagas reservadas para 
	 * um curso de graduação, em um determinado ano.
	 * 
	 * @param curso
	 * @param ano
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> findRelatorioDocentesByCurso(Curso curso, Integer ano) {
		String sql = "select p.nome, " + 
			" (case when s.regime_trabalho = 99 then 'DE' else s.regime_trabalho || 'h' end) as regime_trabalho, " +
			" f.denominacao as titulacao, u.id_unidade, u.nome as lotacao, " + 
			" coalesce(sum(case when t.periodo = 1 then dt.ch_dedicada_periodo/15 end),0) as ch1, " +
			" coalesce(sum(case when t.periodo = 2 then dt.ch_dedicada_periodo/15 end),0) as ch2 " +
			" from rh.servidor s " +
			" join rh.formacao f using(id_formacao) " +
			" join comum.pessoa p using(id_pessoa) " +
			" join ensino.docente_turma dt on (dt.id_docente =  s.id_servidor) " +
			" join ensino.turma t using(id_turma) " +
			" join comum.unidade u on (s.id_unidade = u.id_unidade) " +
			" where t.ano = ?  " +
			" and exists ( select mc.id_curso " +
			" from graduacao.reserva_curso rc " +
			"	join graduacao.matriz_curricular mc using(id_matriz_curricular) " +
			"	where rc.id_turma = t.id_turma " +
			"	and mc.id_curso = ? ) " +
			" group by p.nome, s.regime_trabalho, f.denominacao, u.id_unidade, u.nome " +
			" order by u.nome, p.nome";
		
		return getJdbcTemplate().queryForList(sql, new Object[] {ano,curso.getId()});
	}
	
	/**
	 * Busca todos os docentes da turma informada
	 *
	 * @param turma
	 * @return
	 */
	public List<DocenteTurma> findDocentesByTurma(Turma turma) {
		String sql = "(select t.id_turma, dt.id_docente, p.id_pessoa, p.nome, f.denominacao, "
			+ "(select u.login from comum.usuario u where id_pessoa = s.id_pessoa and inativo=falseValue() " + BDUtils.limit(1) + ") as login, "
			+ "(select u.email from comum.usuario u where id_pessoa = s.id_pessoa and inativo=falseValue() " + BDUtils.limit(1) + ") as email, "
			+ "(select u.nome from comum.unidade u where u.id_unidade = s.id_unidade " + BDUtils.limit(1) + ") as unidade, "
			+ "(select u.id_foto from comum.usuario u where id_pessoa = s.id_pessoa and inativo = falseValue() " + BDUtils.limit(1) + ") as id_foto, s.id_perfil, s.siape "
			+ "from ensino.docente_turma dt, rh.servidor s, ensino.turma t, comum.pessoa p, rh.formacao f "
			+ "where dt.id_turma="+turma.getId()+" and dt.id_docente = s.id_servidor and s.id_formacao = f.id_formacao "
			+ "and dt.id_turma = t.id_turma and s.id_pessoa = p.id_pessoa order by p.nome asc) union ( "
			+ "select t.id_turma, dt.id_docente_externo, p.id_pessoa, p.nome, f.denominacao, "
			+ "(select u.login from comum.usuario u where id_pessoa = de.id_pessoa and inativo=falseValue() " + BDUtils.limit(1) + ") as login, "
			+ "(select u.email from comum.usuario u where id_pessoa = de.id_pessoa and inativo=falseValue() " + BDUtils.limit(1) + ") as email, "
			+ "(select u.nome from comum.unidade u where u.id_unidade = de.id_unidade " + BDUtils.limit(1) + ") as unidade, "
			+ "(select u.id_foto from comum.usuario u where id_pessoa = de.id_pessoa and inativo = falseValue() " + BDUtils.limit(1) + ") as id_foto, de.id_perfil, cast(de.matricula as integer) "
			+ "from ensino.docente_turma dt, ensino.docente_externo de, ensino.turma t, comum.pessoa p, rh.formacao f "
			+ "where dt.id_turma=" + turma.getId() +" and dt.id_docente_externo = de.id_docente_externo and de.id_formacao = f.id_formacao "
			+ "and dt.id_turma = t.id_turma and de.id_pessoa = p.id_pessoa order by p.nome asc)";
		
		List <Turma> turmas = new ArrayList <Turma> ();
		turmas.add(turma);
		List<DocenteTurma> lista = populaDocenteTurmas (sql);
		return lista;
	}

	/**
	 * Executa a consulta passada por parâmetro e retorna uma lista de DocenteTurma.
	 * 
	 * @param sql
	 * @return
	 */
	private List <DocenteTurma> populaDocenteTurmas (String sql){
		@SuppressWarnings("unchecked")
		List <DocenteTurma> rs =  getJdbcTemplate().query(sql, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Pessoa p = new Pessoa(rs.getInt(3), rs.getString(4));

				Unidade u = new Unidade();
				u.setNome(rs.getString(8));

				Usuario us = new Usuario();
				us.setLogin(rs.getString(6));
				us.setEmail(rs.getString(7));
				us.setUnidade(u);

				Servidor s = new Servidor(rs.getInt(2));
				s.setFormacao(new Formacao());
				s.getFormacao().setDenominacao(rs.getString(5));
				s.setIdFoto(rs.getInt(9));
				s.setSiape(rs.getInt(11));
				s.setPrimeiroUsuario(us);
				s.setUnidade(u);
				s.setPessoa(p);

				DocenteTurma dt = new DocenteTurma();
				dt.setTurma(new Turma(rs.getInt(1)));
				dt.setDocente(s);

				Integer idPerfil = rs.getInt(10);
				if (idPerfil != null && idPerfil != 0) {
					PerfilPessoa perfil = PerfilPessoaDAO.getDao().get(idPerfil);
					s.setPerfil(perfil);
				}

				return dt;
			}
		});
		
		return rs;
	}
	
	/**
	 * Retorna uma coleção de docentes de turmas que ocorreram entre os períodos passados na assinatura.
     *
	 * @param tarefa 
	 * 	@return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public List<DocenteTurma> findDocentesByPeriodoComponente(Integer anoInicio, Integer periodoInicio, Integer anoFim, Integer periodoFim, Integer unid, ComponenteCurricular disciplina) throws HibernateException, DAOException {
		
		String hql =
				"select s.id , de.id , pi.id , pi.nome , " +
				" pe.id , pe.nome , t.ano, t.periodo, " +
				" d.id , d.codigo , d.detalhes.nome " +
				"from DocenteTurma dt " +
				"left join dt.docente s " +
				"left join dt.docenteExterno de " +
				"left join s.pessoa pi "+
				"left join de.pessoa pe " +
				"join dt.turma t " +
				"join t.disciplina d " +
				"where 1 = 1 ";
		
		if (disciplina!=null && disciplina.getId()!=0)
			hql += " and d.id = :idDisciplina ";
		
		hql +=	"and d.unidade.id = :idUnidade " +
				"and t.ano || '' || t.periodo >= :periodoInicial " +
				"and t.ano || '' || t.periodo <= :periodoFinal " +
				"group by s.id , de.id , pi.id , pi.nome , pe.id , pe.nome , t.ano, t.periodo, s.id , d.id , d.codigo , d.detalhes.nome "+
				"order by d.detalhes.nome , d.codigo , d.id , t.ano , t.periodo , pi.nome , pe.nome ";

		Query q = getSession().createQuery(hql.toString());
		
		if (disciplina!=null && disciplina.getId()!=0){
			q.setParameter("idDisciplina", disciplina.getId());
		}		
		
		q.setParameter("idUnidade",unid);

		
		String periodoInicial = anoInicio + "" + periodoInicio;
		q.setParameter("periodoInicial", periodoInicial);
		
		String periodoFinal = anoFim + "" + periodoFim;
		q.setParameter("periodoFinal", periodoFinal);
		
		Collection<Object[]> res = q.list();
		List<DocenteTurma> result = new ArrayList<DocenteTurma>();
        if (res != null ) {
        	for (Object[] linha : res) {
        		int i = 0;

        		DocenteTurma dt = new DocenteTurma();
        		
        		Integer idServidor = (Integer)linha[i++];
        		Servidor s = new Servidor();
        		s.setId(idServidor!=null?idServidor:0);
        		
        		Integer idDocExterno = (Integer)linha[i++];
        		DocenteExterno de = new DocenteExterno();
        		de.setId(idDocExterno!=null?idDocExterno:0);
        		
        		Integer idPessoaInterna = (Integer)linha[i++];
        		Pessoa pInterna = new Pessoa();
        		pInterna.setId(idPessoaInterna!=null?idPessoaInterna:0);
        		pInterna.setNome((String)linha[i++]);
        		
        		Integer idPessoaExterna = (Integer)linha[i++];
        		Pessoa pExterna = new Pessoa();
        		pExterna.setId(idPessoaExterna!=null?idPessoaExterna:0);
        		pExterna.setNome((String)linha[i++]);
        		
        		Turma t = new Turma();
        		t.setAno((Integer)linha[i++]);
        		t.setPeriodo((Integer)linha[i++]);
        		
        		Integer idDisciplina = (Integer)linha[i++];
        		ComponenteCurricular d = new ComponenteCurricular();
        		d.setId(idDisciplina!=null?idDisciplina:0);
        		d.setCodigo((String)linha[i++]);
        		
        		ComponenteDetalhes cd = new ComponenteDetalhes();
        		cd.setNome((String)linha[i++]);
        		
        		if (s.getId()!=0){
        			s.setPessoa(pInterna);
        			dt.setDocente(s);
        		}
        		
        		if (de.getId()!=0){
        			de.setPessoa(pExterna);
        			dt.setDocenteExterno(de);
        		}
        		
        		d.setDetalhes(cd);
        		t.setDisciplina(d);
        		dt.setTurma(t);
        		result.add(dt);
        	}
        }
		return result;
	}
	
}
