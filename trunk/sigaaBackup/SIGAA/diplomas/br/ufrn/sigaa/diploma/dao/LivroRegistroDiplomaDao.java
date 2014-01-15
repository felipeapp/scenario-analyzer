/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 03/06/2009
 *
 */
package br.ufrn.sigaa.diploma.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.diploma.dominio.FolhaRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.LivroRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.RegistroDiploma;
import br.ufrn.sigaa.dominio.Curso;

/** Classe responsável por consultas especificas aos Livros de Registros de Diplomas
 * @author Édipo Elder F. Melo
 *
 */
public class LivroRegistroDiplomaDao extends GenericSigaaDAO {

	/** Encontra um livro de registro de diploma dado o curso.  
	 * @param idCurso
	 * @param somenteLivrosAbertos Caso true, retorna apenas os livros abertos (ativos).
	 * @param livroAntigo Caso seja diferente de null, retorna os livros conforme o valor.
	 * @return
	 * @throws DAOException
	 */
	public LivroRegistroDiploma findByCurso(int idCurso, boolean somenteLivrosAbertos, Boolean livroAntigo) throws DAOException {
		String projecao = "livro.id," +
				" livro.titulo, " +
				" livro.ativo, " +
				" livro.instituicao, " +
				" livro.livroAntigo, " +
				" livro.nivel, " +
				" livro.numeroRegistroPorFolha, " +
				" livro.numeroSugeridoFolhas, " +
				" livro.registroCertificado, " +
				" livro.registroExterno";
		// o distinct se faz necessário porque o hibernate repete uma linha por curso
		StringBuilder hql = new StringBuilder("select distinct ")
			.append(projecao)
			.append(" from LivroRegistroDiploma livro" +
					" inner join livro.cursosRegistrados curso " +
					" where curso.id = :idCurso");
		if (somenteLivrosAbertos)
			hql.append(" and livro.ativo = true");
		if (livroAntigo != null)
			hql.append(" and livro.livroAntigo = :livroAntigo");

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idCurso", idCurso);
		if (livroAntigo != null)
			q.setBoolean("livroAntigo", livroAntigo);
		
		@SuppressWarnings("unchecked")
		Collection<LivroRegistroDiploma> livros = HibernateUtils.parseTo(q.list(), projecao, LivroRegistroDiploma.class, "livro");
		if (isEmpty(livros)) 
			return null;
		// cursos registrados
		carregaCursosRegistrados(livros);
		// número de páginas do livro.
		atualizaNumeroPaginas(livros);
		
		LivroRegistroDiploma livro = livros.iterator().next();
		// carrega as folhas do livro
		carregaFolhas(livro);
		return livro;
	}
	
	
	/** Pre-carrega as folhas de registro do diploma
	 * @param livro
	 * @throws DAOException 
	 */
	private void carregaFolhas(LivroRegistroDiploma livro) throws DAOException {
		if (livro != null) {
			String hql = "select id, numeroFolha " +
					" from FolhaRegistroDiploma folha" +
					" where folha.livro.id = :idLivro" +
					" order by numeroFolha";
			Query q = getSession().createQuery(hql);
			q.setInteger("idLivro", livro.getId());
			@SuppressWarnings("unchecked")
			Collection<FolhaRegistroDiploma> folhas = HibernateUtils.parseTo(q.list(), "id, numeroFolha", FolhaRegistroDiploma.class);
			if (folhas != null) {
				for (FolhaRegistroDiploma folha : folhas)
					folha.setLivro(livro);
				// carrega os registros de diploma
				carregaRegistrosDiplomas(folhas);
				livro.setFolhas(folhas);
			}
		}		
	}

	/** Carrega os registros de diplomas de uma coleção de folhas do livro.
	 * @param folhas
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	private void carregaRegistrosDiplomas( Collection<FolhaRegistroDiploma> folhas) throws HibernateException, DAOException {
		if (!isEmpty(folhas)) {
			Collection<Integer> ids = new ArrayList<Integer>();
			for (FolhaRegistroDiploma folha : folhas) 
				ids.add(folha.getId());
			String projecao = "registro.id, registro.folha.id," +
					" assinaturaDiploma.id as registro.assinaturaDiploma.id, registro.ativo, coordenadorCurso as registro.coordenadorCurso," +
					" registro.criadoEm, registro.dataColacao, registro.dataExpedicao, registro.dataRegistro," +
					" discente.id as registro.discente.id, discente.matricula as registro.discente.matricula, " +
					" registro.impresso, registro.livre, registro.numeroRegistro," +
					" registro.processo, registroDiplomaColetivo.id as registro.registroDiplomaColetivo.id, " +
					" registro.registroEntrada.id, curso.id as registro.discente.curso.id, curso.nome as registro.discente.curso.nome";
			String hql = "select " + HibernateUtils.removeAliasFromProjecao(projecao) +
					" from RegistroDiploma registro" +
					" left join registro.discente discente" +
					" left join discente.curso curso" +
					" left join registro.coordenadorCurso coordenadorCurso" +
					" left join registro.assinaturaDiploma assinaturaDiploma" +
					" left join registro.registroDiplomaColetivo registroDiplomaColetivo" +
					" where registro.folha.id in " +
					UFRNUtils.gerarStringIn(ids) +
					" order by registro.folha.numeroFolha, registro.numeroRegistro";
			Query q = getSession().createQuery(hql);
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			Collection<RegistroDiploma>registros = HibernateUtils.parseTo(lista, projecao, RegistroDiploma.class, "registro");
			for (RegistroDiploma registro : registros) {
				for (FolhaRegistroDiploma folha : folhas) {
					if (registro.getFolha().getId() == folha.getId()) {
						registro.setFolha(folha);
						folha.addRegistro(registro);
					}
				}
			}
		}
	}


	/** Carrega a lista de cursos registrados em uma coleção de livros.
	 * @param livros
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private void carregaCursosRegistrados(Collection<LivroRegistroDiploma> livros) throws HibernateException, DAOException {
		// cursos registrados
		String hqlCursos = "select livro.id as idLivro, curso" +
				" from LivroRegistroDiploma livro" +
				" inner join livro.cursosRegistrados curso" +
				" inner join curso.unidade unidade" +
				" inner join curso.municipio municipio" +
				" where livro.id in " +
				UFRNUtils.gerarStringIn(livros);
		Query qCursos = getSession().createQuery(hqlCursos);
		@SuppressWarnings("unchecked")
		List<Object[]> cursos = qCursos.list();
		for (Object[] obj : cursos) {
			for (LivroRegistroDiploma livro : livros) {
				if (livro.getId() == ((Integer)obj[0])) {
					livro.addCurso((Curso) obj[1]);
				}
			}
		}
	}

	/** Atualiza o número de páginas utilizadas em uma coleção de livros.
	 * @param livros
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private void atualizaNumeroPaginas(Collection<LivroRegistroDiploma> livros) throws HibernateException, DAOException {
		String sql = "select id_livro_registro_diploma, count(id_folha_registro_diploma)" +
				" from diploma.folha_registro_diploma" +
				" where id_livro_registro_diploma in " +
				UFRNUtils.gerarStringIn(livros) +
				" group by id_livro_registro_diploma";
		Query qc = getSession().createSQLQuery(sql);
		@SuppressWarnings("unchecked")
		List<Object[]> paginasLivro = qc.list();
		for (LivroRegistroDiploma livro : livros) {
			for (Object[] obj : paginasLivro) {
				int idLivro = (Integer) obj[0];
				if (livro.getId() == idLivro) {
					BigInteger numeroFolhas = (BigInteger) obj[1];
					livro.setNumeroFolhas(numeroFolhas.intValue());
				}
			}
		}
	}
	
	/** Retorna o livro de acordo com o título.
	 * @param titulo
	 * @param nivelEnsino
	 * @return
	 * @throws DAOException
	 */
	public LivroRegistroDiploma findByTitulo(String titulo, char nivelEnsino) throws DAOException{
		Criteria c = getSession().createCriteria(LivroRegistroDiploma.class);
		c.add(Restrictions.eq("titulo", titulo.trim().toUpperCase()));
		c.add(Restrictions.eq("nivel", nivelEnsino));
		LivroRegistroDiploma livro = (LivroRegistroDiploma) c.uniqueResult();
		// carrega as folhas do livro
		carregaFolhas(livro);
		return livro;
	}
	
	/** Busca livros de registro de diplomas conforme os parâmetros passados.
	 * @param titulo título, ou parte do título, do livro
	 * @param idCurso ID do curso registrado no livro. Caso ID seja zero, não restringirá a busca.
	 * @param ativo Indica se busca por livros ativos. Caso null, não restringirá a busca.
	 * @param registroExterno Indica se busca por livros utilizados em registro de diplomas externos. Caso null, não restringirá a busca.
	 * @param livroAntigo Indica se busca por livros anteriores ao registro no SIGAA. Caso null, não restringirá a busca.
	 * @nivelEnsino nível de ensino dos diplomas registrados no livro. Caso null, não restringirá a busca.
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Collection<LivroRegistroDiploma> findByTituloCurso(String titulo, int idCurso, Boolean ativo, Boolean registroExterno, Boolean livroAntigo, Character nivelEnsino) throws HibernateException, DAOException {
		String projecao = "livro.id," +
				" livro.titulo, " +
				" livro.ativo, " +
				" livro.instituicao, " +
				" livro.livroAntigo, " +
				" livro.nivel, " +
				" livro.numeroRegistroPorFolha, " +
				" livro.numeroSugeridoFolhas, " +
				" livro.registroCertificado, " +
				" livro.registroExterno";
		// o distinct se faz necessário porque o hibernate repete uma linha por curso
		StringBuilder hql = new StringBuilder("select distinct ")
			.append(projecao)
			.append(" from LivroRegistroDiploma livro" +
					" left join livro.cursosRegistrados curso " +
					" where 1 = 1");
		if (!isEmpty(titulo))
			hql.append(" and livro.titulo = :titulo");
		if (idCurso != 0)
			hql.append(" and curso.id = :idCurso");
		if (!isEmpty(ativo))
			hql.append(" and livro.ativo = :ativo");
		if (!isEmpty(registroExterno))
			hql.append(" and livro.registroExterno = :registroExterno");
		if (!isEmpty(livroAntigo))
			hql.append(" and livro.livroAntigo = :livroAntigo");
		if (!isEmpty(nivelEnsino))
			hql.append(" and livro.nivel = :nivel");
		hql.append(" order by livro.nivel, livro.titulo");
		
		Query q = getSession().createQuery(hql.toString());
		if (!isEmpty(titulo))
			q.setString("titulo", titulo);
		if (idCurso != 0)
			q.setInteger("idCurso", idCurso);
		if (!isEmpty(ativo))
			q.setBoolean("ativo", ativo);
		if (!isEmpty(registroExterno))
			q.setBoolean("registroExterno", registroExterno);
		if (!isEmpty(livroAntigo))
			q.setBoolean("livroAntigo", livroAntigo);
		if (!isEmpty(nivelEnsino))
			q.setCharacter("nivel", nivelEnsino);
		@SuppressWarnings("unchecked")
		Collection<LivroRegistroDiploma> livros = HibernateUtils.parseTo(q.list(), projecao, LivroRegistroDiploma.class, "livro");
		if (isEmpty(livros)) 
			return null;
		// cursos registrados
		carregaCursosRegistrados(livros);
		// número de páginas do livro.
		atualizaNumeroPaginas(livros);
		return livros;
	}
	
	/** Retorna um livro de registro de diplomas especificado pelo ID. Esta consulta usa projeção e subconsultas para 
	 * otimizar o tempo de resposta.
	 * @param id
	 * @param classe
	 * @return
	 * @throws DAOException
	 */
	public LivroRegistroDiploma findByPrimaryKey(int id) throws DAOException {
		String projecao = "livro.id, livro.titulo, " +
				" livro.ativo, " +
				" livro.instituicao, " +
				" livro.livroAntigo, " +
				" livro.nivel, " +
				" livro.numeroRegistroPorFolha, " +
				" livro.numeroSugeridoFolhas, " +
				" livro.registroCertificado, " +
				" livro.registroExterno";
		StringBuilder hql = new StringBuilder("select ")
			.append(projecao)
			.append(" from LivroRegistroDiploma livro " +
					" where livro.id = :idLivro");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idLivro", id);
		@SuppressWarnings("unchecked")
		Collection<LivroRegistroDiploma> livros = HibernateUtils.parseTo(q.list(), projecao, LivroRegistroDiploma.class, "livro");
		if (isEmpty(livros)) 
			return null;
		// cursos registrados
		carregaCursosRegistrados(livros);
		// número de páginas do livro.
		atualizaNumeroPaginas(livros);
		// carrega as folhas do livro
		LivroRegistroDiploma livro = livros.iterator().next();
		carregaFolhas(livro);
		return livro;
	}
	
	/** Retorna os últimos livros criados, em ordem reversa de criação. 
	 * @param quantidade número de livros a retornar. Caso este valor seja menor que 1, será retornado pelo menos 1 livro.
	 * @return 
	 * @throws DAOException
	 */
	public Collection<LivroRegistroDiploma> findUltimosLivros(int quantidade, char nivelEnsino) throws DAOException {
		if (quantidade < 1)
			quantidade = 1;
		Criteria c = getSession().createCriteria(LivroRegistroDiploma.class)
		.add(Restrictions.eq("nivel", nivelEnsino))
		.addOrder(Order.desc("id"))
		.setMaxResults(quantidade);
		
		@SuppressWarnings("unchecked") 
		Collection<LivroRegistroDiploma> lista = c.list();
		return lista;
	}

	/** Retorna o livro atual (aberto) utilizado nos registros de diplomas de cursos de stricto sensu ou lato sensu. 
	 * @return
	 * @throws DAOException 
	 */
	public LivroRegistroDiploma findLivroAtivoPosByNivelEnsino(boolean livroAntigo, char nivelEnsino) throws DAOException {
		if (NivelEnsino.isAlgumNivelStricto(nivelEnsino))
			nivelEnsino = NivelEnsino.STRICTO;
		Criteria c = getSession().createCriteria(LivroRegistroDiploma.class)
		.add(Restrictions.eq("nivel", nivelEnsino))
		.add(Restrictions.eq("ativo", true))
		.add(Restrictions.eq("livroAntigo", livroAntigo));
		if (livroAntigo) {
			c.setFetchMode("folhas", FetchMode.JOIN)
			.createCriteria("folhas")
			.setFetchMode("registros", FetchMode.JOIN);
		}
		LivroRegistroDiploma livro = (LivroRegistroDiploma) c.uniqueResult();
		carregaFolhas(livro);
		return livro;
	}

	/** Indica se existe livro aberto para registro de curso de stricto sensu.
	 * @param livroAntigo
	 * @return
	 * @throws DAOException
	 */
	public boolean hasLivroStrictoAberto(boolean livroAntigo, char nivel) throws DAOException {
		return getLivroStrictoAberto(livroAntigo, nivel) != null;
	}

	/** Retorna o livro atualmente aberto para o registro de diploma de cursos de stricto sensu.
	 * @param livroAntigo
	 * @return
	 * @throws DAOException
	 */
	public LivroRegistroDiploma getLivroStrictoAberto(boolean livroAntigo, char nivel) throws DAOException {
		Criteria c = getSession().createCriteria(LivroRegistroDiploma.class);
		c.add(Restrictions.eq("nivel", nivel));
		c.add(Restrictions.eq("ativo", true));
		c.add(Restrictions.eq("livroAntigo", livroAntigo));
		LivroRegistroDiploma livro = (LivroRegistroDiploma) c.uniqueResult();
		// carrega as folhas do livro
		carregaFolhas(livro);
		return livro;
	}
}
