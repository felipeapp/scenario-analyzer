/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '05/09/2006'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.notificacoes.Destinatario;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.TipoBolsa;
import br.ufrn.sigaa.pesquisa.dominio.CodigoProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoStatusPlanoTrabalho;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * DAO responsável pelas consultas aos discentes de graduação 
 * que são membros de projetos de pesquisa (alunos de iniciação à pesquisa)
 *  
 * @author Victor Hugo
 * @author Leonardo Campos
 */
public class MembroProjetoDiscenteDao extends GenericSigaaDAO {

	/**
	 * Busca todas as associações de um discente com planos de trabalho
	 *
	 * @param discente
	 * @param ativo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<MembroProjetoDiscente> findByDiscente(Discente discente,
			boolean ativo) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append(" from MembroProjetoDiscente mem");
			hql.append(" where mem.inativo = falseValue() and mem.discente.id = :idDiscente");

			if (ativo) {
				hql
						.append(" and (mem.dataFim is null OR mem.dataFim >= :hoje) ");
			}

			Query query = getSession().createQuery(hql.toString());
			if (ativo) {
				query.setDate("hoje", new Date());
			}

			query.setInteger("idDiscente", discente.getId());
			return query.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Busca todas as associações de um discente com planos de trabalho
	 *
	 * @param discente
	 * @param ativo
	 * @return
	 * @throws DAOException
	 */
	public boolean discentePossuiProjetoAtivo(int idDiscente) throws DAOException {
		try {
			String sql = "select count (m.id_membro_projeto_discente) from pesquisa.membro_projeto_discente m where m.inativo = falseValue() and m.id_discente = :idDiscente and (m.data_fim is null or m.data_fim >= :hoje)";

			Query query = getSession().createSQLQuery(sql);
			
			query.setDate("hoje", new Date());
			query.setInteger("idDiscente", idDiscente);
			
			@SuppressWarnings("unchecked")
			List <BigInteger> rs = query.list();
			
			return rs.get(0).intValue() > 0;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca a associação ativa entre um discente e um plano de trabalho
	 *
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public MembroProjetoDiscente findByDiscenteAtivo(Discente discente)
			throws DAOException {
		Collection<MembroProjetoDiscente> membros = findByDiscente(discente,
				true);
		if (membros == null || membros.isEmpty()) {
			return null;
		} else {
			return membros.iterator().next();
		}
	}

	/**
	 * Busca as associações de discentes a planos de trabalho pela matrícula do
	 * discente
	 *
	 * @param matricula
	 * @param ativos
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<MembroProjetoDiscente> findByMatricula(long matricula,
			boolean ativos) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append(" from MembroProjetoDiscente mem");
			hql.append(" where mem.inativo = falseValue() and mem.discente.matricula = :matricula");
			Query query = getSession().createQuery(hql.toString());

			if (ativos) {
				hql.append(" and mem.dataFim is null");
			}

			query.setLong("matricula", matricula);
			return query.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca todos os discentes sob a orientação de um determinado docente
	 *
	 * @param idDocente
	 * @param ativos
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<MembroProjetoDiscente> findByOrientador(int idDocente,
			boolean ativos) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append(" from MembroProjetoDiscente mem");
			hql.append(" where mem.inativo = falseValue() and mem.planoTrabalho.orientador.id = :idDocente");

			if (ativos) {
				hql
						.append(" and (mem.dataFim is null OR mem.dataFim >= :hoje)");
			}

			hql.append(" order by mem.discente.pessoa.nome");

			Query query = getSession().createQuery(hql.toString());
			if (ativos) {
				query.setDate("hoje", new Date());
			}

			query.setLong("idDocente", idDocente);
			return query.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca os alunos de iniciação à pesquisa de um orientador num determinado período
	 * 
	 * @param idDocente
	 * @param anoInicial
	 * @param anoFinal
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<MembroProjetoDiscente> findByOrientador(int idDocente, int anoInicial, int anoFinal) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append(" from MembroProjetoDiscente mem");
			hql.append(" where mem.inativo = falseValue() and mem.planoTrabalho.orientador.id = :idDocente");
			hql.append(" and mem.planoTrabalho.projetoPesquisa.codigo.ano >= " + anoInicial);
			hql.append(" and mem.planoTrabalho.projetoPesquisa.codigo.ano <= " + anoFinal);
			hql.append(" order by mem.planoTrabalho.projetoPesquisa.codigo.ano, mem.discente.pessoa.nome");

			Query query = getSession().createQuery(hql.toString());

			query.setLong("idDocente", idDocente);
			return query.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca todos os docentes ativamente ligados a um plano de trabalho
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection<MembroProjetoDiscente> findAtivos() throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql
					.append(" SELECT id, discente.matricula, discente.pessoa.nome, "
							+ " planoTrabalho.id, planoTrabalho.orientador.pessoa.nome,"
							+ " planoTrabalho.projetoPesquisa.codigo, tipoBolsa.id, tipoBolsa.descricao");
			hql.append(" from MembroProjetoDiscente mem");
			hql.append(" where mem.inativo = falseValue() and (mem.dataFim is null OR mem.dataFim >= :hoje)");
			hql.append(" order by mem.discente.pessoa.nome");

			Query query = getSession().createQuery(hql.toString());
			query.setDate("hoje", new Date());

			@SuppressWarnings("unchecked")
			List<Object[]> lista = query.list();

			ArrayList<MembroProjetoDiscente> result = new ArrayList<MembroProjetoDiscente>();
			for (int a = 0; a < lista.size(); a++) {
				MembroProjetoDiscente membro = new MembroProjetoDiscente();
				int col = 0;
				Object[] colunas = lista.get(a);
				membro.setId((Integer) colunas[col++]);

				Discente discente = new Discente();
				discente.setMatricula((Long) colunas[col++]);
				discente.getPessoa().setNome((String) colunas[col++]);
				membro.setDiscente(discente);

				PlanoTrabalho plano = new PlanoTrabalho();

				plano.setId((Integer) colunas[col++]);
				plano.setOrientador(new Servidor());
				plano.getOrientador().getPessoa().setNome(
						(String) colunas[col++]);

				ProjetoPesquisa projeto = new ProjetoPesquisa();
				projeto.setCodigo((CodigoProjetoPesquisa) colunas[col++]);
				plano.setProjetoPesquisa(projeto);
				membro.setPlanoTrabalho(plano);
				membro.setTipoBolsa( new TipoBolsaPesquisa((Integer) colunas[col++]));
				membro.getTipoBolsa().setDescricao((String) colunas[col++]);
				result.add(membro);
			}
			return result;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca alunos de iniciação à pesquisa com diversos filtros opcionais
	 * 
	 * @param idGrupoPesquisa
	 * @param idCentro
	 * @param idUnidade
	 * @param idAluno
	 * @param idOrientador
	 * @param modalidade
	 * @param idCurso
	 * @param sexo
	 * @param status
	 * @param idCota
	 * @param somenteAtivos
	 * @param nomeUnidade
	 * @param nomeAluno
	 * @param nomeOrientador
	 * @return
	 * @throws DAOException
	 */
	public Collection<MembroProjetoDiscente> filter(Integer idGrupoPesquisa,
			Integer idCentro, Integer idUnidade, Integer idAluno,
			Integer idOrientador, Integer modalidade, Integer idCurso,
			Character sexo, Integer status, Integer idCota,
			Boolean somenteAtivos, String nomeUnidade, String nomeAluno,
			String nomeOrientador) throws DAOException {

		try {
			StringBuilder hql = new StringBuilder();
		//	String projecao = " SELECT mpd.id, mpd.dataInicio, mpd.dataFim, mpd.discente.matricula, mpd.discente.pessoa.nome, "
		//			+ " mpd.planoTrabalho.id, mpd.planoTrabalho.titulo, mpd.planoTrabalho.orientador.pessoa.nome,"
		//			+ " mpd.planoTrabalho.projetoPesquisa.codigo, mpd.tipoBolsa.id, "
		//			+ " mpd.planoTrabalho.status, mpd.planoTrabalho.cota.descricao ";
			String projecao = " SELECT id, mpd.dataInicio, mpd.dataFim, discente.matricula, discente.pessoa.nome, "
					+ " planoTrabalho.id, planoTrabalho.titulo, planoTrabalho.orientador.pessoa.nome,"
					+ " planoTrabalho.projetoPesquisa.codigo, mpd.tipoBolsa.id, mpd.tipoBolsa.descricao, mpd.tipoBolsa.categoria, "
					+ " planoTrabalho.status, planoTrabalho.cota.descricao ";
			hql.append(" FROM MembroProjetoDiscente mpd");
			hql.append(" WHERE mpd.inativo = falseValue() ");

			if (idGrupoPesquisa != null) {
				hql
						.append(" AND mpd.planoTrabalho.projetoPesquisa.linhaPesquisa.grupoPesquisa.id = :idGrupoPesquisa ");
			}
			if (idCentro != null) {
				hql
						.append(" AND mpd.planoTrabalho.projetoPesquisa.projeto.unidade.gestora.id = :idCentro ");
			}
			if (idUnidade != null) {
				hql
						.append(" AND mpd.planoTrabalho.projetoPesquisa.projeto.unidade.id = :idUnidade ");
			}
			if (idAluno != null) {
				hql.append(" AND mpd.discente.id = :idAluno ");
			}
			if (idOrientador != null) {
				hql
						.append(" AND mpd.planoTrabalho.orientador.id = :idOrientador ");
			}
			if (nomeUnidade != null) {
				hql
						.append(" AND mpd.planoTrabalho.projetoPesquisa.projeto.unidade.nome like :nomeUnidade ");
			}
			if (nomeAluno != null) {
				hql.append(" AND mpd.discente.pessoa.nome like :nomeAluno ");
			}
			if (nomeOrientador != null) {
				hql
						.append(" AND mpd.planoTrabalho.orientador.pessoa.nome like :nomeOrientador ");
			}
			if (modalidade != null) {
				if (somenteAtivos) {
					hql
							.append(" AND mpd.planoTrabalho.tipoBolsa.id = :modalidade ");
					hql.append(" AND mpd.tipoBolsa.id = :modalidade ");
				} else {
					hql.append(" AND mpd.tipoBolsa.id = :modalidade ");
				}
			}
			if (idCurso != null) {
				hql.append(" AND mpd.discente.curso.id = :idCurso ");
			}
			if (sexo != null) {
				hql.append(" AND mpd.discente.pessoa.sexo = :sexo ");
			}
			if (status != null) {
				hql.append(" AND mpd.planoTrabalho.status = :status ");
			}
			if (idCota != null) {
				hql.append(" AND mpd.planoTrabalho.cota.id = :idCota ");
			}
			if (somenteAtivos) {
				hql
						.append(" AND mpd.id = mpd.planoTrabalho.membroProjetoDiscente.id ");
				hql.append(" AND mpd.dataFim is null ");
			}

			String orderBy = " ORDER BY planoTrabalho.cota desc, discente.pessoa.nome";
			//String orderBy = " ORDER BY mpd.planoTrabalho.cota desc, mpd.discente.pessoa.nome";

			// Criando a consulta
			Query query = getSession().createQuery(projecao + hql + orderBy);

			if (idGrupoPesquisa != null) {
				query.setInteger("idGrupoPesquisa", idGrupoPesquisa);
			}
			if (idCentro != null) {
				query.setInteger("idCentro", idCentro);
			}
			if (idUnidade != null) {
				query.setInteger("idUnidade", idUnidade);
			}
			if (idAluno != null) {
				query.setInteger("idAluno", idAluno);
			}
			if (idOrientador != null) {
				query.setInteger("idOrientador", idOrientador);
			}
			if (nomeOrientador != null) {
				query.setString("nomeOrientador", "%"
						+ nomeOrientador.trim().toUpperCase() + "%");
			}
			if (nomeUnidade != null) {
				query.setString("nomeUnidade", "%"
						+ nomeUnidade.trim().toUpperCase() + "%");
			}
			if (nomeAluno != null) {
				query.setString("nomeAluno", nomeAluno.trim().toUpperCase()
						+ "%");
			}
			if (idCurso != null) {
				query.setInteger("idCurso", idCurso);
			}
			if (modalidade != null) {
				query.setInteger("modalidade", modalidade);
			}
			if (sexo != null) {
				query.setCharacter("sexo", sexo);
			}
			if (status != null) {
				query.setInteger("status", status);
			}
			if (idCota != null) {
				query.setInteger("idCota", idCota);
			}

			@SuppressWarnings("unchecked")
			List<Object[]> lista = query.list();

			ArrayList<MembroProjetoDiscente> result = new ArrayList<MembroProjetoDiscente>();
			for (int a = 0; a < lista.size(); a++) {
				MembroProjetoDiscente membro = new MembroProjetoDiscente();
				int col = 0;
				Object[] colunas = lista.get(a);
				membro.setId((Integer) colunas[col++]);
				membro.setDataInicio((Date) colunas[col++]);
				membro.setDataFim((Date) colunas[col++]);

				Discente discente = new Discente();
				discente.setMatricula((Long) colunas[col++]);
				discente.getPessoa().setNome((String) colunas[col++]);
				membro.setDiscente(discente);

				PlanoTrabalho plano = new PlanoTrabalho();

				plano.setId((Integer) colunas[col++]);
				plano.setTitulo((String) colunas[col++]);
				plano.setOrientador(new Servidor());
				plano.getOrientador().getPessoa().setNome(
						(String) colunas[col++]);

				ProjetoPesquisa projeto = new ProjetoPesquisa();
				projeto.setCodigo((CodigoProjetoPesquisa) colunas[col++]);
				plano.setProjetoPesquisa(projeto);
				membro.setTipoBolsa( new TipoBolsaPesquisa((Integer) colunas[col++]));
				membro.getTipoBolsa().setDescricao((String) colunas[col++]);
				membro.getTipoBolsa().setCategoria((Integer) colunas[col++]);
				plano.setStatus((Integer) colunas[col++]);

				CotaBolsas cota = new CotaBolsas();
				cota.setDescricao((String) colunas[col++]);
				plano.setCota(cota);

				membro.setPlanoTrabalho(plano);

				result.add(membro);
			}

			return result;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Sobrecarga do método de busca de alunos de iniciação à pesquisa com algumas mudanças nos parâmetros
	 * 
	 * @param idGrupoPesquisa
	 * @param idCentro
	 * @param idUnidade
	 * @param idAluno
	 * @param idOrientador
	 * @param modalidade
	 * @param idCurso
	 * @param sexo
	 * @param status
	 * @param idCota
	 * @param somenteAtivos
	 * @return
	 * @throws DAOException
	 */
	public Collection<MembroProjetoDiscente> filter(Integer idGrupoPesquisa,
			Integer idCentro, Integer idUnidade, Integer idAluno,
			Integer idOrientador, Integer modalidade, Integer idCurso,
			Character sexo, Integer status, Integer idCota,
			Boolean somenteAtivos) throws DAOException {
		return filter(idGrupoPesquisa, idCentro, idUnidade, idAluno,
				idOrientador, modalidade, idCurso, sexo, status, idCota,
				somenteAtivos, null, null, null);
	}

	/**
	 * Sobrecarga do método de busca de alunos de iniciação à pesquisa com algumas mudanças nos parâmetros
	 * 
	 * @param idGrupoPesquisa
	 * @param idCentro
	 * @param nomeUnidade
	 * @param nomeAluno
	 * @param nomeOrientador
	 * @param modalidade
	 * @param idCurso
	 * @param sexo
	 * @param status
	 * @param idCota
	 * @param somenteAtivos
	 * @return
	 * @throws DAOException
	 */
	public Collection<MembroProjetoDiscente> filter(Integer idGrupoPesquisa,
			Integer idCentro, String nomeUnidade, String nomeAluno,
			String nomeOrientador, Integer modalidade, Integer idCurso,
			Character sexo, Integer status, Integer idCota,
			Boolean somenteAtivos) throws DAOException {
		return filter(idGrupoPesquisa, idCentro, null, null, null, modalidade,
				idCurso, sexo, status, idCota, somenteAtivos, nomeUnidade,
				nomeAluno, nomeOrientador);
	}

	/**
	 * Busca todas as indicações/substituições/finalizações de bolsistas
	 * ocorridas num determinado período.
	 *
	 * @param tipoBolsa
	 * @param inicio
	 * @param fim
	 * @param ordenacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<MembroProjetoDiscente> findSubstituicoes(Date inicio,
			Date fim, CotaBolsas cota, Integer tipoBolsa, Servidor orientador,
			Boolean ativos, String ordenacao) throws DAOException {

		StringBuilder hql = new StringBuilder();

		hql
				.append(" select m.id, m.dataIndicacao, m.dataFinalizacao, m.planoTrabalho.id, m.tipoBolsa.id, m.tipoBolsa.descricao, "
						+ " m.tipoBolsa.categoria, m.planoTrabalho.orientador.pessoa.nome, "
						+ " m.discente.pessoa.cpf_cnpj, m.discente.pessoa.nome ");
		hql.append(" from MembroProjetoDiscente m ");
		hql.append(" where m.inativo = falseValue() ");

		if (cota != null) {
			hql.append(" and m.planoTrabalho.cota.id = " + cota.getId());
		}

		if (ativos != null) {
			if (ativos) {
				hql.append(" and m.dataFinalizacao is null");
			} else {
				hql.append(" and m.dataFinalizacao is not null");
			}
		}

		if (inicio != null && fim != null) {
			hql
					.append(" and( (cast(m.dataIndicacao as date) >= :inicio AND cast(m.dataIndicacao as date) <= :fim AND m.planoTrabalho.membroProjetoDiscente.id = m.id) "
							+ " OR (cast(m.dataFinalizacao as date) >= :inicio AND cast(m.dataFinalizacao as date) <= :fim) )");
		}

		if (tipoBolsa != null) {
			hql.append(" and m.tipoBolsa.id = " + tipoBolsa);
		}

		if (orientador != null) {
			hql.append(" and m.planoTrabalho.orientador.id = "
					+ orientador.getId());
		}

		// Ordenação dos resultados
		hql.append(" order by m.tipoBolsa, ");
		if (ordenacao.equals("dataIndicacao")) {
			hql.append(" m.dataIndicacao, m.discente.pessoa.nome ");
		} else if (ordenacao.equals("discente")) {
			hql.append(" m.discente.pessoa.nome, m.dataIndicacao ");
		} else if (ordenacao.equals("orientador")) {
			hql.append(" m.planoTrabalho.orientador.pessoa.nome, m.discente.pessoa.nome, m.dataIndicacao ");
		} else if (ordenacao.equals("dataOrientador")){
			hql.append(" m.dataIndicacao, m.planoTrabalho.orientador.pessoa.nome, m.discente.pessoa.nome ");
		}

		Query query = getSession().createQuery(hql.toString());

		if (inicio != null) {
			query.setTimestamp("inicio", inicio);
		}
		if (fim != null) {
			query.setTimestamp("fim", fim);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.list();
		ArrayList<MembroProjetoDiscente> result = new ArrayList<MembroProjetoDiscente>();
		for (int a = 0; a < lista.size(); a++) {
			MembroProjetoDiscente membro = new MembroProjetoDiscente();
			int col = 0;
			Object[] colunas = lista.get(a);
			membro.setId((Integer) colunas[col++]);
			membro.setDataIndicacao((Date) colunas[col++]);
			membro.setDataFinalizacao((Date) colunas[col++]);

			PlanoTrabalho plano = new PlanoTrabalho();
			plano.setId((Integer) colunas[col++]);
			membro.setTipoBolsa( new TipoBolsaPesquisa((Integer) colunas[col++]));
			membro.getTipoBolsa().setDescricao((String) colunas[col++]);
			membro.getTipoBolsa().setCategoria((Integer) colunas[col++]);
			plano.setOrientador(new Servidor());
			plano.getOrientador().getPessoa().setNome((String) colunas[col++]);

			Discente discente = new Discente();
			discente.getPessoa().setCpf_cnpj((Long) colunas[col++]);
			discente.getPessoa().setNome((String) colunas[col++]);
			membro.setDiscente(discente);

			membro.setPlanoTrabalho(plano);

			result.add(membro);
		}

		return result;
	}

	/**
	 * Buscar os alunos de IC de um projeto de pesquisa
	 *
	 * @param projeto
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<MembroProjetoDiscente> findByProjeto(
			ProjetoPesquisa projeto) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql
					.append(" select pt.membroProjetoDiscente from PlanoTrabalho pt");
			hql.append(" where pt.membroProjetoDiscente.inativo = falseValue() and pt.projetoPesquisa.id = " + projeto.getId());
			hql
					.append(" order by pt.membroProjetoDiscente.discente.pessoa.nome ");
			Query query = getSession().createQuery(hql.toString());
			return query.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Buscar aluno em um plano de trabalho pela matrícula
	 *
	 * @param planoTrabalho
	 * @param matricula
	 * @return
	 * @throws DAOException
	 */
	public MembroProjetoDiscente findByPlanoTrabalho(
			PlanoTrabalho planoTrabalho, Long matricula) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();

			hql.append(" select mpd from MembroProjetoDiscente mpd");
			hql.append(" where mpd.inativo = falseValue() and mpd.planoTrabalho.id = :planoTrabalho and mpd.discente.matricula = :matricula");
			hql.append(" order by mpd.dataIndicacao desc");

			Query query = getSession().createQuery(hql.toString());
			query.setInteger("planoTrabalho", planoTrabalho.getId());
			query.setLong("matricula", matricula);

			query.setMaxResults(1);
			MembroProjetoDiscente mpd = (MembroProjetoDiscente) query.uniqueResult();
			return mpd;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca o último discente finalizado em um plano de trabalho, a partir do plano de trabalho
	 *
	 * @param planoTrabalho
	 * @return
	 * @throws DAOException
	 */
	public MembroProjetoDiscente findUltimoDiscenteFinalizado(PlanoTrabalho planoTrabalho) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("select mpd from MembroProjetoDiscente mpd where mpd.inativo = falseValue() and mpd.planoTrabalho.id = :planoTrabalho order by dataFim desc");
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("planoTrabalho", planoTrabalho.getId());
			q.setMaxResults(1);
			return (MembroProjetoDiscente) q.uniqueResult();
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}

	}
	
	/**
	 * Busca o último discente finalizado em um plano de trabalho, a partir de um membro qualquer que já fez parte daquele plano
	 * 
	 * @param membroDiscente
	 * @return
	 * @throws DAOException
	 */
	public MembroProjetoDiscente findUltimoDiscenteFinalizado(MembroProjetoDiscente membroDiscente) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("select mpd from MembroProjetoDiscente mpd where mpd.inativo = falseValue() and mpd.planoTrabalho.id = :planoTrabalho and dataFinalizacao is not null order by dataFim desc");
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("planoTrabalho", membroDiscente.getPlanoTrabalho().getId());
			q.setMaxResults(1);
			return (MembroProjetoDiscente) q.uniqueResult();
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}

	}

	/**
	 * Monta uma lista de destinatários de mensagens a partir dos tipos de bolsa selecionados
	 *
	 * @param tiposBolsaSelecionados
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Destinatario> findDestinatariosByTipoBolsa(List<Integer> tiposBolsaSelecionados) throws DAOException {
		try {
			return getSession().createSQLQuery("select distinct nome as nome, u.email as email, u.id_usuario as idusuario" +
					" from pesquisa.plano_trabalho pt, pesquisa.membro_projeto_discente mpd, discente d, comum.pessoa p, comum.usuario u" +
					" where mpd.inativo = falseValue() and pt.id_membro_projeto_discente = mpd.id_membro_projeto_discente" +
					" and mpd.id_discente = d.id_discente" +
					" and d.id_pessoa = p.id_pessoa " +
					" and p.id_pessoa = u.id_pessoa " +
					" and pt.status = " + TipoStatusPlanoTrabalho.EM_ANDAMENTO +
					" and pt.tipo_bolsa in " + UFRNUtils.gerarStringIn(tiposBolsaSelecionados) +
					" order by nome, email")
				.setResultTransformer(Transformers.aliasToBean(Destinatario.class))
				.list();
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Busca todos os bolsistas ativos do tipo de bolsa informado.
	 * Se o tipo informado for <= 0, retorna os bolsistas de todas as modalidades.
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public Collection<MembroProjetoDiscente> findByTipoBolsa(int tipo) throws DAOException {
		return findByTipoBolsa(tipo, true);
	}

	/**
	 * Busca todos os bolsistas ativos do tipo de bolsa informado ou todos os inativos. 
	 * Se o tipo informado for <= 0, retorna os bolsistas de todas as modalidades.
	 * 
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public Collection<MembroProjetoDiscente> findByTipoBolsa(int tipo, boolean ativos) throws DAOException {
		try {
			String projecao = "mpd.id, mpd.dataInicio, mpd.dataIndicacao, mpd.dataFinalizacao, mpd.planoTrabalho.id," +
					" mpd.planoTrabalho.dataInicio, mpd.planoTrabalho.dataFim, mpd.planoTrabalho.projetoPesquisa.codigo.prefixo," +
					" mpd.planoTrabalho.projetoPesquisa.codigo.numero, mpd.planoTrabalho.projetoPesquisa.codigo.ano," +
					" mpd.planoTrabalho.projetoPesquisa.projeto.unidade.id, mpd.tipoBolsa.id, mpd.tipoBolsa.descricao," +
					" mpd.tipoBolsa.categoria, mpd.tipoBolsa.tipoBolsaSipac, mpd.discente.id, mpd.discente.matricula, mpd.discente.nivel," +
					" mpd.discente.curso.id, mpd.discente.curso.nome, mpd.discente.curso.modalidadeEducacao.id," +
					" mpd.discente.pessoa.id, mpd.discente.pessoa.nome, mpd.discente.pessoa.contaBancaria.agencia," +
					" mpd.discente.pessoa.contaBancaria.numero, mpd.discente.pessoa.contaBancaria.banco.codigo," +
					" mpd.discente.curso.municipio.nome, mpd.discente.curso.unidade.sigla ";
			
			StringBuilder hql = new StringBuilder();
			hql.append(" select " + projecao);
			hql.append(" from MembroProjetoDiscente mpd");
			hql.append(" where mpd.inativo = falseValue() and mpd.tipoBolsa.id in (select id from TipoBolsaPesquisa where tipoBolsaSipac is not null) ");
			hql.append(" and mpd.ignorar = falseValue() ");
			if(ativos)
				hql.append(" and mpd.dataFinalizacao is null ");
			else
				hql.append(" and mpd.dataFinalizacao is not null ");
			if(tipo > 0)
				hql.append(" and mpd.tipoBolsa.id = "+ tipo);
			if(ativos)
				hql.append(" order by mpd.dataIndicacao desc");
			else
				hql.append(" order by mpd.discente.matricula, mpd.dataFinalizacao asc");
				
			@SuppressWarnings("unchecked")
			List<Object[]> lista = getSession().createQuery(hql.toString()).list();
			
			Collection<MembroProjetoDiscente> result = HibernateUtils.parseTo(lista, projecao, MembroProjetoDiscente.class, "mpd");
			return result; 

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna uma coleção com todos os ids de discentes de pesquisa 
	 * que já solicitaram cadastro de bolsas no SIPAC via SIGAA.
	 * 
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Integer> findDiscentesBolsasSolicitadasSipac() throws DAOException {
		String consulta = "select id_discente_projeto from projetos.sincronizacao_bolsa_sipac " +
				"where id_tipo_bolsa in " + UFRNUtils.gerarStringIn(new int[]{TipoBolsa.BOLSA_PESQUISA, TipoBolsa.BOLSA_PPQ, TipoBolsa.BOLSA_PESQUISA_REUNI}) ;
		return getJdbcTemplate().query(consulta, new RowMapper() {
			public Object mapRow(ResultSet rs, int pos) throws SQLException {
				return new Integer(rs.getInt(1));
			}
		});
	}
	
	/**
	 * Retorna todos os MembroProjetoDiscente do plano de trabalho passado.
	 * 
	 * @param plano
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<MembroProjetoDiscente> findAllByPlanoTrabalho(PlanoTrabalho plano) throws DAOException {
		
		try {
			StringBuilder hql = new StringBuilder();
			hql.append(" select mpd from MembroProjetoDiscente mpd");
			hql.append(" where mpd.inativo = falseValue() and mpd.planoTrabalho.id = :planoTrabalho");
			hql.append(" order by mpd.dataIndicacao asc");

			Query query = getSession().createQuery(hql.toString());
			query.setInteger("planoTrabalho", plano.getId());
			return query.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Método que retorna uma lista do mesmo membroProjetoDiscente com seus vários planos de trabalho
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public Collection<MembroProjetoDiscente> findMesmoMembroComCadaPlanoTrabalho(int idDiscente) throws DAOException{
		try{
			StringBuilder hql = new StringBuilder();
			hql.append(" SELECT m.id, m.dataInicio, m.dataFim, m.tipoBolsa, disc.id, disc.matricula, p.id, p.nome, p.nomeAscii, pt.id, pt.titulo, pt.orientador, " +
					" pro.id ");
			hql.append(" FROM MembroProjetoDiscente m ");
			hql.append(" INNER JOIN m.planoTrabalho pt");
			hql.append(" INNER JOIN pt.projetoPesquisa pro");
			hql.append(" INNER JOIN m.discente disc ");
			hql.append(" INNER JOIN disc.pessoa p ");
			hql.append(" WHERE m.inativo = falseValue() and disc.id = :idDiscente ");
			hql.append(" order by pt.titulo ");
			
			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idDiscente", idDiscente);
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = query.list();
			Collection<MembroProjetoDiscente> result = new ArrayList<MembroProjetoDiscente>();
			
			for(int i=0; i<lista.size(); i++){
				int col = 0;
				Object[] colunas = lista.get(i);
				MembroProjetoDiscente m = new MembroProjetoDiscente();
				m.setId((Integer) colunas[col++]);
				m.setDataInicio((Date) colunas[col++]);
				m.setDataFim((Date) colunas[col++]);
				m.setTipoBolsa((TipoBolsaPesquisa) colunas[col++]);
				
				Discente d = new Discente();
				d.setId((Integer) colunas[col++]);
				d.setMatricula((Long) colunas[col++]);
				
				Pessoa p = new Pessoa();
				p.setId((Integer) colunas[col++]);
				p.setNome((String) colunas[col++]);
				p.setNomeAscii((String) colunas[col++]);
				
				d.setPessoa(p);
				m.setDiscente(d);
				
				PlanoTrabalho pt = new PlanoTrabalho();
				pt.setId((Integer) colunas[col++]);
				pt.setTitulo((String) colunas[col++]);
				pt.setOrientador((Servidor) colunas[col++]);
				pt.setTipoBolsa( m.getTipoBolsa() );
				
				ProjetoPesquisa pro = new ProjetoPesquisa();
				pro.setId((Integer) colunas[col++]);
				
				pt.setProjetoPesquisa(pro);
				m.setPlanoTrabalho(pt);
				result.add(m);
			}
			
			return result;
			
		}catch(Exception e){
			throw new DAOException(e.getMessage(), e);
		}
	}
	
}