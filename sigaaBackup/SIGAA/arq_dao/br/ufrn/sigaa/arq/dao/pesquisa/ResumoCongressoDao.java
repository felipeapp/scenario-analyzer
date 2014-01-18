/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '03/07/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.pesquisa.dominio.AutorResumoCongresso;
import br.ufrn.sigaa.pesquisa.dominio.CongressoIniciacaoCientifica;
import br.ufrn.sigaa.pesquisa.dominio.ResumoCongresso;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * DAO para consultas relacionadas a resumos de congressos de iniciação científica
 *
 * @author Ricardo Wendell
 *
 */
public class ResumoCongressoDao extends GenericSigaaDAO {

	/**
	 * Busca um resumo pelo seu identificador.
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public ResumoCongresso find(int id) throws DAOException {
		return findByPrimaryKey(id, ResumoCongresso.class);
	}

	/**
	 * Buscar resumo a partir do autor principal e do congresso em que o mesmo
	 * foi publicado
	 *
	 * @param autor
	 * @param congresso
	 * @return
	 * @throws DAOException
	 */
	public ResumoCongresso findByAutorPrincipal( AutorResumoCongresso autor, CongressoIniciacaoCientifica congresso ) throws DAOException {

		try {
    		String hql = "select r from ResumoCongresso r " +
    				" join r.autores as autor " +
    				" where autor.cpf = " + autor.getCpf() +
    				" and autor.tipoParticipacao = " + AutorResumoCongresso.AUTOR +
    				" and r.ativo = trueValue() "+
    				" and r.congresso.id = " + congresso.getId();
			return (ResumoCongresso) getSession().createQuery(hql).uniqueResult();

    	} catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	/**
	 * Busca resumos a partir do autor
	 * @param autor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ResumoCongresso> findByAutor( AutorResumoCongresso autor )  throws DAOException {
		try {
    		String hql = "select r from ResumoCongresso r " +
    				" join r.autores as autor " +
    				" where autor.cpf = " + autor.getCpf() +
    				" and r.ativo = trueValue() "+
    				" order by r.congresso.ano desc, r.titulo asc";
			return getSession().createQuery(hql).list();

    	} catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	/**
	 * Busca resumos a partir do autor com participação.
	 * @param autor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Map<String, Object>> findResumoAndParticipacaoByAutor( AutorResumoCongresso autor )  throws DAOException {
		try {
    		String hql = "select r as resumo, a as autor from ResumoCongresso r " +
    				" join r.autores as a " +
    				" where a.cpf = " + autor.getCpf() +
    				" and r.ativo = trueValue() "+
    				" order by r.congresso.ano desc, r.titulo asc";
			return getSession().createQuery(hql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

    	} catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

    /**
     * Busca resumos a partir do autor do tipo discente
     * @param discente
     * @return
     * @throws DAOException
     */
	@SuppressWarnings("unchecked")
	public Collection<Map<String, Object>> findResumoAndParticipacaoByAutor( DiscenteAdapter discente )  throws DAOException {

		try {
    		String hql = "select r as resumo, a as autor from ResumoCongresso r " +
    				" join r.autores as a " +
    				" where a.discente.id = " + discente.getId() +
    				" and r.ativo = trueValue() "+
    				" order by r.congresso.ano desc, r.titulo asc";
			return getSession().createQuery(hql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

    	} catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	/**
	 * Busca resumos a partir do autor do tipo docente
	 * @param docente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Map<String, Object>> findResumoAndParticipacaoByAutor( Servidor docente )  throws DAOException {

		try {
    		String hql = "select r as resumo, a as autor from ResumoCongresso r " +
    				" join r.autores as a " +
    				" where a.docente.id = " + docente.getId() +
    				" and r.ativo = trueValue() "+
    				" order by r.congresso.ano desc, r.titulo asc";
			return getSession().createQuery(hql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

    	} catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	/**
	 * Busca resumos a partir de vários filtros.
	 * @param idCongresso
	 * @param idAreaConhecimento
	 * @param centro
	 * @param nomeAutor
	 * @param cpfAutor
	 * @param status
	 * @param codigo
	 * @param nomeOrientador
	 * @param pendenteDistribuicao
	 * @param pendenteDistribuicaoApresentacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<ResumoCongresso> filter(Integer idCongresso, Integer idAreaConhecimento, 
			Unidade centro, String nomeAutor, Long cpfAutor, Integer status, String codigo, 
			String nomeOrientador, Boolean pendenteDistribuicao, Boolean pendenteDistribuicaoApresentacao) throws DAOException {
		return filter(idCongresso, idAreaConhecimento, centro, nomeAutor, cpfAutor, status, codigo, nomeOrientador, pendenteDistribuicao, pendenteDistribuicaoApresentacao, null);
	}
	
	/**
	 * Busca resumos a partir de uma lista de identificadores de unidade de cursos e vários outros filtros.
	 * @param idCongresso
	 * @param idAreaConhecimento
	 * @param centro
	 * @param nomeAutor
	 * @param cpfAutor
	 * @param status
	 * @param codigo
	 * @param nomeOrientador
	 * @param pendenteDistribuicao
	 * @param pendenteDistribuicaoApresentacao
	 * @param idsUnidadesCurso
	 * @return
	 * @throws DAOException
	 */
	public Collection<ResumoCongresso> filter(Integer idCongresso, Integer idAreaConhecimento, 
			Unidade centro, String nomeAutor, Long cpfAutor, Integer status, String codigo, 
			String nomeOrientador, Boolean pendenteDistribuicao, Boolean pendenteDistribuicaoApresentacao, List<Integer> idsUnidadesCurso) throws DAOException {

		StringBuilder hql = new StringBuilder();
		hql.append(" select r.id, r.codigo, r.titulo, r.status, r.numeroPainel, " +
				" autor.cpf, autor.nome, " +
				" orientador.cpf, orientador.nome, orientador.docente.id, " +
				" r.congresso.edicao, r.congresso.ano, " +
				" r.areaConhecimentoCnpq.nome," +
				" curso.unidade.gestora.sigla, curso.unidade.gestora.id, "+
				" curso.unidade.id, curso.unidade.nome, curso.unidade.sigla, "+
				" case " +
				"   when orientador.docente.unidade.gestora.id = :idGestoraGlobal " +
				"   then orientador.docente.unidade.id" +
				"   else orientador.docente.unidade.gestora.id" +
				" end");
		hql.append(" from ResumoCongresso r ");
		hql.append(" join r.autores as autor ");
		hql.append(" left join autor.discente.curso as curso, ");
		hql.append(" ResumoCongresso r2 ");
		hql.append(" join r2.autores as orientador ");
		hql.append(" where r.congresso.id = :idCongresso ");
		hql.append(" and r.ativo = trueValue() ");
		hql.append(" and r.id = r2.id ");
		hql.append(" and autor.tipoParticipacao = :autorPrincipal ");
		hql.append(" and orientador.tipoParticipacao = :orientador ");
		if(idAreaConhecimento != null)
			hql.append(" and r.areaConhecimentoCnpq.id = :idAreaConhecimento ");
		if(centro != null)
			hql.append(" and ( orientador.docente.unidade.gestora.id = " + centro.getId() + " or orientador.docente.unidade.id = " + centro.getId() + " )");
		if(cpfAutor != null)
			hql.append(" and autor.cpf = :cpf ");
		if(nomeAutor != null)
			hql.append(" and upper(autor.nome) like upper(:nomeAutor)");
		if(status != null)
			hql.append(" and r.status = :status ");
		if(codigo != null)
			hql.append(" and r.codigo like :codigo");
		if(nomeOrientador != null)
			hql.append(" and upper(orientador.nome) like upper(:nomeOrientador)");
		if(pendenteDistribuicao != null && pendenteDistribuicao){
			hql.append(" and r.id not in (select a.resumo.id from AvaliacaoResumo a) ");
		}
		if(pendenteDistribuicaoApresentacao != null && pendenteDistribuicaoApresentacao){
			hql.append(" and (select count(*) from AvaliacaoApresentacaoResumo ap where ap.resumo.id = r.id) < 2 ");
		}
		if(idsUnidadesCurso != null){
			hql.append(" and ( orientador.docente.unidade.gestora.id in " + UFRNUtils.gerarStringIn(idsUnidadesCurso) + 
					" or orientador.docente.unidade.id in " + UFRNUtils.gerarStringIn(idsUnidadesCurso) + " )");
		}
		hql.append(" order by autor.discente.curso.unidade.gestora.nome, autor.discente.curso.unidade.nome, autor.nome  ");

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idCongresso", idCongresso);
		q.setInteger("autorPrincipal", AutorResumoCongresso.AUTOR);
		q.setInteger("orientador", AutorResumoCongresso.ORIENTADOR);
		q.setInteger("idGestoraGlobal", Unidade.UNIDADE_DIREITO_GLOBAL);
		if(idAreaConhecimento != null)
			q.setInteger("idAreaConhecimento", idAreaConhecimento);
		if(cpfAutor != null)
			q.setLong("cpf", cpfAutor);
		if(nomeAutor != null)
			q.setString("nomeAutor", "%"+nomeAutor+"%");
		if(status != null)
			q.setInteger("status", status);
		if(codigo != null)
			q.setString("codigo", codigo+"%");
		if(nomeOrientador != null)
			q.setString("nomeOrientador", "%"+nomeOrientador+"%");
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();

    	ArrayList<ResumoCongresso> result = new ArrayList<ResumoCongresso>();
    	for (int a = 0; a < lista.size(); a++) {
    		ResumoCongresso resumo = new ResumoCongresso();
    		int col = 0;
			Object[] colunas = lista.get(a);
			resumo.setId( (Integer) colunas[col++] );
			resumo.setCodigo( (String) colunas[col++] );
			resumo.setTitulo( (String) colunas[col++] );
			resumo.setStatus( (Integer) colunas[col++] );
			resumo.setNumeroPainel( (Integer) colunas[col++] );

			AutorResumoCongresso autor = new AutorResumoCongresso();
			autor.setCpf( (Long) colunas[col++] );
			autor.setNome( (String) colunas[col++] );
			autor.setTipoParticipacao(AutorResumoCongresso.AUTOR);
			resumo.setAutores( new ArrayList<AutorResumoCongresso>() );
			resumo.getAutores().add(autor);

			AutorResumoCongresso orientador = new AutorResumoCongresso();
			orientador.setCpf( (Long) colunas[col++] );
			orientador.setNome( (String) colunas[col++] );
			orientador.setTipoParticipacao(AutorResumoCongresso.ORIENTADOR);
			orientador.setDocente(new Servidor((Integer) colunas[col++]));
			resumo.getAutores().add(orientador);

			CongressoIniciacaoCientifica congresso = new CongressoIniciacaoCientifica();
			congresso.setEdicao( (String) colunas[col++] );
			congresso.setAno( (Integer) colunas[col++] );
			resumo.setCongresso(congresso);

			AreaConhecimentoCnpq area = new AreaConhecimentoCnpq();
			area.setNome( (String) colunas[col++] );
			resumo.setAreaConhecimentoCnpq(area);


			Unidade	centroAutor = new Unidade();
			centroAutor.setSigla( (String) colunas[col++] );
			centroAutor.setId( (Integer) colunas[col++] );
			
			Unidade unidadeCurso = new Unidade();
			unidadeCurso.setId((Integer) colunas[col++] );
			unidadeCurso.setNome((String) colunas[col++] );
			unidadeCurso.setSigla( (String) colunas[col++] );
			
			autor.setDiscente( new Discente() );
			autor.getDiscente().setCurso( new Curso() );
			autor.getDiscente().getCurso().setUnidade( unidadeCurso );
			autor.getDiscente().getCurso().getUnidade().setGestora(centroAutor);
			
			resumo.setIdUnidadeAgrupadora((Integer) colunas[col++]);

			result.add(resumo);
    	}

    	return result;
	}
	
	/**
	 * Busca de trabalhos aprovados a partir de congresso e centro.  
	 * @param idCongresso
	 * @param idCentro
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> findTrabalhosAprovadosCentro(int idCongresso, int idCentro) throws DAOException {
		return  getSession().createSQLQuery("select a.id_resumo_congresso" +
				" from pesquisa.autor_resumo_congresso a" +
				" inner join pesquisa.resumo_congresso r on (a.id_resumo_congresso=r.id_resumo_congresso)" +
				" inner join rh.servidor s on (a.id_docente=s.id_servidor)" +
				" inner join comum.unidade u on (s.id_unidade=u.id_unidade)" +
				" where a.tipo_participacao=" + AutorResumoCongresso.ORIENTADOR +
				" and r.ativo=trueValue()" +
				" and r.id_congresso=" + idCongresso +
				" and r.status=" + ResumoCongresso.APROVADO +
				" and (u.id_unidade=" + idCentro +" or u.id_gestora="+ idCentro +")").list();
	}
	
	/**
	 * Busca todos os resumos pendentes de autorização pelo orientador.
	 * @param idOrientador
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ResumoCongresso> findAllPendentesByOrientador(int idOrientador) throws DAOException {
		
		List<Object> lista = getSession().createSQLQuery("select rc.id_resumo_congresso, rc.codigo, rc.titulo, rc.status," +
				" rc.data_envio, arc.id_autor_resumo_congresso, arc.nome, arc.cpf from pesquisa.resumo_congresso rc," +
				" pesquisa.autor_resumo_congresso arc where rc.id_resumo_congresso = arc.id_resumo_congresso and" +
				" arc.id_resumo_congresso in(select id_resumo_congresso from pesquisa.autor_resumo_congresso where" +
				" id_docente = " + idOrientador + " and tipo_participacao = " + AutorResumoCongresso.ORIENTADOR + 
				") and arc.tipo_participacao = " +	AutorResumoCongresso.AUTOR + " and rc.status = " + 
				ResumoCongresso.AGUARDANDO_AUTORIZACAO).list();
		
		Collection<ResumoCongresso> resumos = new ArrayList<ResumoCongresso>();
		for (int i = 0; i < lista.size(); i++) {
			
			ResumoCongresso resumo = new ResumoCongresso();
    		int col = 0;
			Object[] colunas = (Object[]) lista.get(i);
			resumo.setId((Integer) colunas[col++]);
			resumo.setCodigo((String) colunas[col++]);
			resumo.setTitulo((String) colunas[col++]);
			resumo.setStatus((Integer) colunas[col++]);
			resumo.setDataEnvio((Date) colunas[col++]);
			
			AutorResumoCongresso autor = new AutorResumoCongresso();
			autor.setId((Integer) colunas[col++]);
			autor.setNome((String) colunas[col++]);
			autor.setCpf(((BigInteger) colunas[col++]).longValue());
			autor.setTipoParticipacao(AutorResumoCongresso.AUTOR);
			resumo.setAutores(new ArrayList<AutorResumoCongresso>());
			resumo.getAutores().add(autor);
			resumos.add(resumo);
		}
		return resumos;
	}
	
}
