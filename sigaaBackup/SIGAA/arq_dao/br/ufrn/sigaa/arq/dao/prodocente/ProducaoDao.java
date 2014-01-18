/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 08/11/2006
 *
 */
package br.ufrn.sigaa.arq.dao.prodocente;

import static org.hibernate.criterion.Restrictions.eq;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.BancaDefesa;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoMembroGrupoPesquisa;
import br.ufrn.sigaa.pesquisa.relatorios.MesAno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.producao.dominio.Artigo;
import br.ufrn.sigaa.prodocente.producao.dominio.Banca;
import br.ufrn.sigaa.prodocente.producao.dominio.BloqueioCadastroProducao;
import br.ufrn.sigaa.prodocente.producao.dominio.BolsaObtida;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.SubTipoArtistico;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoBanca;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoPeriodico;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;
import br.ufrn.sigaa.prodocente.relatorios.dominio.LinhaRelatorioGrupoPesquisa;
import br.ufrn.sigaa.prodocente.relatorios.dominio.RelatorioAvaliacaoGrupoPesquisa;

/**
 * DAO responsável por consultas específicas à entidade Estágio.
 * 
 * @author Gleydson
 * 
 */
public class ProducaoDao extends GenericSigaaDAO {

	/** HashTable responsável pela construção das projeções utilizadas. */
	static Hashtable<String,String> construtoresProjecao = new Hashtable<String,String>();

	static {
		construtoresProjecao.put("Artigo",
				"Artigo(id,titulo,tipoParticipacao.descricao,anoReferencia,validado,idArquivo,sequenciaProducao,issn,exibir)");
	}

	/**
	 * Retorna a quantidade de produções ainda pendentes de validação pelos docentes de um departamento.
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public Long getTotalPendentes(Unidade unidade) throws DAOException {
		Query q = getSession().createQuery(
				"select count(p.id) from Producao p where p.servidor.unidade = "
						+ unidade.getId() + " and p.validado is null");

		return (Long) q.uniqueResult();
	}

	/**
	 * Busca todas as produções intelectuais de um certo tipo do docente informado. 
	 * @param servidor
	 * @param tipo
	 * @param paginacao
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Producao> findByProducaoServidor(Servidor servidor,
			Class tipo, PagingInformation paginacao) throws DAOException {

		Query qTotal = getSession().createQuery("select count(p.id) from " 
				+ tipo.getSimpleName() + " p where p.servidor = " + servidor.getId()
				+ " and (p.ativo is null or p.ativo = trueValue())");

		Long total = (Long) qTotal.uniqueResult();

		Criteria c = getSession().createCriteria(tipo);
		c.add(Expression.eq("servidor", servidor));

		// somente as produções que não foram "deletadas" (ativo!=falseValue())
		c.add(Expression.or(Expression.eq("ativo", true), Expression.isNull("ativo")));
		c.addOrder(Order.desc("anoReferencia"));
		c.addOrder(Order.desc("dataProducao"));
		c.addOrder(Order.desc("dataCadastro"));

		if (paginacao != null) {
			paginacao.setTotalRegistros(total.intValue());
			c.setFirstResult(paginacao.getPaginaAtual() * paginacao.getTamanhoPagina());
			c.setMaxResults(paginacao.getTamanhoPagina());
		}
		return c.list();
	}


	/**
	 * Procura as produções do Servidor filtrando por Válidas, Inválidas e
	 * Pendentes.
	 *
	 * @param validacao :
	 *            1 = valida, 0 = Invalida, -1 = pendente de validação,
	 *            !{1|0|-1} = todas
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Producao> findByProducaoServidor(Servidor servidor,
			Class tipo, Integer validacao, int anoInicial, int anoFinal, 
			Integer periodoInicial, Integer periodoFinal)
			throws DAOException {

		Criteria c = getSession().createCriteria(tipo);
		c.add(Expression.eq("servidor", servidor));
		// somente as produções que não foram "deletadas" (ativo!=falseValue())
		c.add(Expression.or(Expression.eq("ativo", true), Expression
				.isNull("ativo")));

		if ( periodoInicial != null && periodoFinal != null  ) {
			c.add(Expression.ge("dataProducao", CalendarUtils.createDate(01, periodoInicial == 1 ? 0 : 5, anoInicial)));
			c.add(Expression.le("dataProducao", CalendarUtils.createDate(periodoFinal == 1 ? 30 : 31, periodoFinal == 1 ? 5 : 11, anoFinal)));
		} else {
			c.add(Expression.ge("anoReferencia", anoInicial));
			c.add(Expression.le("anoReferencia", anoFinal));
		}
		
		if (validacao != null) {
			// Produções Válidas
			if (validacao == 1)
				c.add(Expression.eq("validado", true));
			// Produções Inválidas
			else if (validacao == 0)
				c.add(Expression.eq("validado", false));
			// Produções Pendentes
			else if (validacao == -1)
				c.add(Expression.isNull("validado"));
		}
		c.addOrder(Order.desc("anoReferencia"));
		c.addOrder(Order.desc("dataProducao"));

		return c.list();

	}

	/**
	 * Busca as participações em bancas de um docente de acordo com o tipo da banca.
	 * @param servidor
	 * @param tipoBanca
	 * @param paginacao
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Banca> findByBanca(Servidor servidor, int tipoBanca,
			PagingInformation paginacao) throws DAOException {

		if(servidor == null){
			return new ArrayList<Banca>();
		}
		
		Query qTotal = getSession().createQuery(
				"select count(p.id) from Banca p where p.servidor = "
						+ servidor.getId() + " and p.tipoBanca.id = "
						+ tipoBanca + " and (p.ativo is null or p.ativo = trueValue())") ;

		Long total = (Long) qTotal.uniqueResult();

		Criteria c = getSession().createCriteria(Banca.class);
		c.add(Expression.eq("servidor", servidor));
		c.add(Expression.eq("tipoBanca", new TipoBanca(tipoBanca)));
		c.add(Expression.or(Expression.eq("ativo", true), Expression.isNull("ativo")));
		c.addOrder(Order.desc("anoReferencia")).addOrder(Order.desc("dataCadastro"));

		if (paginacao != null) {
			paginacao.setTotalRegistros(total.intValue());
			c.setFirstResult(paginacao.getPaginaAtual()
					* paginacao.getTamanhoPagina());
			c.setMaxResults(paginacao.getTamanhoPagina());
		}

		return c.list();

	}

	/**
	 * Busca as produções pendentes para validação pela chefia.
	 * @param chefe
	 * @param titulo
	 * @param servidor
	 * @param anoReferencia
	 * @param validados
	 * @return
	 * @throws DAOException
	 */
	public ArrayList<Producao> findToValidacaoChefe(Usuario chefe,
			String titulo, Servidor servidor, int anoReferencia,
			Boolean validados) throws DAOException {
		return findToValidacao(chefe.getServidor(), true, titulo, chefe
				.getUnidade(), servidor, anoReferencia, validados, null);
	}

	/**
	 * Busca as produções pendentes para validação pela direção.
	 * @param diretor
	 * @param titulo
	 * @param servidor
	 * @param anoReferencia
	 * @param validados
	 * @param docentesCentro
	 * @return
	 * @throws DAOException
	 */
	public ArrayList<Producao> findToValidacaoDiretor(Usuario diretor,
			String titulo, Servidor servidor, int anoReferencia,
			Boolean validados, Integer[] docentesCentro) throws DAOException {
		return findToValidacao(diretor.getServidor(), false, titulo, null,
				servidor, anoReferencia, validados, docentesCentro);
	}

	/**
	 * Busca as produções de um docente a partir de alguns critérios informados, como
	 * o título da produção, o ano de referência e se estão validadas ou não.
	 * @param titulo
	 * @param servidor
	 * @param anoReferencia
	 * @param validados
	 * @return
	 * @throws DAOException
	 */
	public ArrayList<Producao> findByDocente(String titulo, Servidor servidor,
			int anoReferencia, Boolean validados) throws DAOException {
		return findToValidacao(null, null, titulo, null, servidor,
				anoReferencia, validados, null);
	}

	/**
	 * Busca todas as produções pendentes de validação de um docente. 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	public ArrayList<Producao> findPendentesValidacao(Servidor servidor) throws DAOException {
		return findToValidacao(null, null, null, null, servidor, null, false, null);
	}
	
	/**
	 * Busca produções por título
	 *
	 * @param titulo
	 * @param unidade
	 * @param servidor
	 * @param anoReferencia
	 * @param validados
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<Producao> findToValidacao(Servidor avaliador,
			Boolean avaliadorChefe, String titulo, Unidade unidade,
			Servidor servidor, Integer anoReferencia, Boolean validados,
			Integer[] docentesCentro) throws DAOException {

		try {
			// String select = "SELECT new Producao(id, titulo,
			// tipoProducao.descricao, tipoProducao.id, servidor.pessoa.nome,
			// servidor.id, validado, dataValidacao)";
			StringBuffer restoHql = new StringBuffer();
			restoHql.append(" FROM Producao");
			restoHql.append(" WHERE (ativo is null or ativo = trueValue()) ");
			if  (anoReferencia != null )
				restoHql.append(" and anoReferencia = :anoReferencia ");
			if (titulo != null && !"".equals(titulo.trim()))
				restoHql.append(" and titulo like '" + titulo + "%' ");
			if (validados != null)
				if (validados) {
					restoHql.append(" and validado = trueValue()");
				} else {
					restoHql.append(" and (validado is null or validado = falseValue())");
				}
			if (unidade != null && unidade.getId() != 0)
				restoHql.append(" and servidor.unidade.id = :unidade ");
			if (avaliador != null)
				restoHql.append(" and servidor.id<>:chefe ");
			if (servidor != null && servidor.getId() != 0) {
				restoHql.append(" and servidor.id=:servidor ");
			} else if (!avaliadorChefe) {
				// para validação feita por diretor de centro
				restoHql.append(" and servidor.id in (");
				for (int id : docentesCentro) {
					restoHql.append(id + ", ");
				}
				restoHql.deleteCharAt(restoHql.lastIndexOf(", "));
				restoHql.append(")");
			}

			Query q = getSession().createQuery(
					"SELECT count(id)" + restoHql.toString());
			if ( anoReferencia != null )
				q.setInteger("anoReferencia", anoReferencia);
			if (unidade != null && unidade.getId() != 0)
				q.setInteger("unidade", unidade.getId());
			if (servidor != null && servidor.getId() != 0)
				q.setInteger("servidor", servidor.getId());
			if (avaliador != null)
				q.setInteger("chefe", avaliador.getId());

			Long total = (Long) q.uniqueResult();
			if (total < 1000) {
				restoHql.append(" order by servidor.id asc");
				q = getSession().createQuery(restoHql.toString());
				if ( anoReferencia != null )
					q.setInteger("anoReferencia", anoReferencia);
				if (unidade != null && unidade.getId() != 0)
					q.setInteger("unidade", unidade.getId());
				if (servidor != null && servidor.getId() != 0)
					q.setInteger("servidor", servidor.getId());
				if (avaliador != null)
					q.setInteger("chefe", avaliador.getId());
				return (ArrayList<Producao>) q.list();
			}

			return new ArrayList<Producao>();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca todas as bolsas de produtividade cadastradas por docentes que ainda estão pendentes
	 * de validação. 
	 * @param anoReferencia
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<BolsaObtida> findBolsasProdutividadePendentesValidacao(Integer anoReferencia) throws DAOException {

		try {
			
			StringBuffer restoHql = new StringBuffer();
			restoHql.append(" FROM BolsaObtida");
			restoHql.append(" WHERE (ativo is null or ativo = trueValue()) ");
			if  (anoReferencia != null )
				restoHql.append(" and anoReferencia = :anoReferencia ");
			restoHql.append(" and (validado is null or validado = falseValue()) ");
			restoHql.append(" and tipoBolsaProdocente.produtividade = trueValue() ");
			
			restoHql.append(" order by servidor.id asc");
			
			Query q = getSession().createQuery(restoHql.toString());
			if ( anoReferencia != null )
				q.setInteger("anoReferencia", anoReferencia);
			
			return (ArrayList<BolsaObtida>) q.list();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Busca os diferentes anos de referência das produções cadastradas na base de dados.
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Integer> getAnosCadastrados() throws DAOException {

		Query q = getSession()
				.createQuery(
						"select distinct p.anoReferencia from Producao p order by p.anoReferencia desc");
		return q.list();

	}

	/**
	 * Busca os diferentes anos de referência das bolsas de produtividade cadastradas na base de dados.
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Integer> getAnosCadastradosBolsaProdutividadePendente() throws DAOException {

		Query q = getSession()
				.createQuery(
						"select distinct b.anoReferencia from BolsaObtida b " +
						"where b.tipoBolsaProdocente.produtividade = trueValue() " +
						"order by b.anoReferencia desc");
		return q.list();

	}
	
	/**
	 * Retorna o relatório quantitativo de
	 * produções de uma determinada unidade por um período
	 *
	 * @param anoInicial
	 * @param anoFinal
	 * @param unidade
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Map<MesAno, Long>> generateQuantitativos(
			Date dataInicial, Date dataFinal, int unidade) throws DAOException {
		try {
			String hql = "SELECT tipoProducao.descricao, month(dataProducao), year(dataProducao), count(dataProducao) "
					+ "FROM Producao WHERE "
					+ " (ativo is null or ativo = trueValue()) "
					+ " and dataProducao >= '"	+ dataInicial  + "'"
					+ " and dataProducao <= '" + dataFinal + "'"
					+ (unidade != -1 ? " and servidor.unidade.id = " + unidade : "")
					+ " GROUP BY tipoProducao.descricao, month(dataProducao), year(dataProducao) "
					+ " ORDER BY tipoProducao.descricao, month(dataProducao), year(dataProducao) ";
			
			List<Object[]> list = getSession().createQuery(hql).list();
			Map<String, Map<MesAno, Long>> resultado = new TreeMap<String, Map<MesAno, Long>>();

			long diferenca = dataFinal.getTime() - dataInicial.getTime();  
			long tam = (diferenca/1000) / 60 / 60 / 24 / 30;
			
			Iterator<Object[]> it = list.iterator();
			
			while(it.hasNext()){
				Object[] linha = it.next();
				String descricao = (String) linha[0];
				Integer mes = (Integer) linha[1];
				Integer ano = (Integer) linha[2];
				Long count = (Long) linha[3];
				
				MesAno mesAno = new MesAno(mes, ano);
				Map<MesAno, Long> mesesAnos = gerarMapa(tam, dataInicial);
				
				if(resultado.get(descricao) != null){
					mesesAnos = resultado.get(descricao);
				}
				mesesAnos.put(mesAno, count);
				resultado.put(descricao, mesesAnos);
			}
			
			
			return resultado;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}
	
	
	
	/**
	 * Idem ao anterior, só que detalhando as produções dos docentes
	 *
	 * @param anoInicial
	 * @param anoFinal
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Map<String, Map<MesAno, Long>>> generateQuantitativosDocentes(Date dataInicial, Date dataFinal, int unidade) throws DAOException {
		try {
			String hql = "SELECT servidor.pessoa.nome, tipoProducao.descricao, month(dataProducao), year(dataProducao), count(dataProducao) "
				+ "FROM Producao WHERE "
				+ " (ativo is null or ativo = trueValue()) "
				+ " and dataProducao >= '"	+ dataInicial + "'"
				+ " and dataProducao <= '" + dataFinal + "'"
				+ (unidade != -1 ? " and servidor.unidade.id = " + unidade : "")
				+ " GROUP BY servidor.pessoa.nome, tipoProducao.descricao, tipoProducao.descricao, month(dataProducao), year(dataProducao) "
				+ " ORDER BY servidor.pessoa.nome asc,tipoProducao.descricao asc";

			List<Object[]> list = getSession().createQuery(hql).list();
			Map<String, Map<MesAno, Long>> resultadoParcial = new TreeMap<String, Map<MesAno, Long>>();
			Map<String, Map<String, Map<MesAno, Long>>> resultado = new TreeMap<String, Map<String,Map<MesAno,Long>>>();
			Iterator<Object[]> it = list.iterator();
			
			long diferenca = dataFinal.getTime() - dataInicial.getTime();  
			long tam = (diferenca/1000) / 60 / 60 / 24 / 30;			
			
			String docente = "";
			while(it.hasNext()){
				
				Object[] linha = it.next();
				
				// quando mudar de docente, limpe o resultado parcial (titulo, map de mesAno)
				if (!((String) linha[0]).equals(docente)) {
					resultadoParcial = new TreeMap<String, Map<MesAno, Long>>();
				}
				
				docente = (String) linha[0];
				String descricao = (String) linha[1];
				Integer mes = (Integer) linha[2];
				Integer ano = (Integer) linha[3];
				Long count = (Long) linha[4];
				
				MesAno mesAno = new MesAno(mes, ano);
				Map<MesAno, Long> mesesAnos = gerarMapa(tam, dataInicial);
				
				if(resultadoParcial.get(descricao) != null){
					mesesAnos = resultadoParcial.get(descricao);
				}
				mesesAnos.put(mesAno, count);
				resultadoParcial.put(descricao, mesesAnos);

				if (resultado.get(docente) != null) {
					
				}
				
				resultado.put(docente, resultadoParcial);
				
			}
			
			
			
			return resultado;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}	
	
	/**
	 * Retorna o relatório de quantitativos de alguns itens da pesquisa,
	 * como algumas coisas da produção intelectual, projetos de pequisa, e indicações de bolsistas e voluntários.
	 * @param dataInicial
	 * @param dataFinal
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Map<MesAno, Long>> generateQuantitativosPesquisa(
			Date dataInicial, Date dataFinal, int unidade) throws DAOException {
		try {
			String hql = "SELECT tipoProducao.descricao, month(dataProducao), year(dataProducao), count(dataProducao) "
					+ "FROM Producao WHERE "
					+ " (ativo is null or ativo = trueValue()) "
					+ " and dataProducao >= '"	+ dataInicial  + "'"
					+ " and dataProducao <= '" + dataFinal + "'"
					+ (unidade != -1 ? " and servidor.unidade.id = " + unidade : "")
					+ " and tipoProducao.id in "+ 
						UFRNUtils.gerarStringIn(new int[]{TipoProducao.ARTIGO_PERIODICO_JORNAIS_SIMILARES.getId(), 
								TipoProducao.LIVRO.getId(), TipoProducao.CAPITULO_LIVROS.getId(), TipoProducao.PATENTE.getId()})
					+ " GROUP BY tipoProducao.descricao, month(dataProducao), year(dataProducao) "
					+ " ORDER BY tipoProducao.descricao, month(dataProducao), year(dataProducao) ";
			
			List<Object[]> list = getSession().createQuery(hql).list();
			Map<String, Map<MesAno, Long>> resultado = new TreeMap<String, Map<MesAno, Long>>();

			long diferenca = dataFinal.getTime() - dataInicial.getTime();  
			long tam = (diferenca/1000) / 60 / 60 / 24 / 30;
			
			Iterator<Object[]> it = list.iterator();
			
			int cont = 0;
			
			while(it.hasNext()){
				Object[] linha = it.next();
				String tipoProducao = (String) linha[0];
				Integer mes = (Integer) linha[1];
				Integer ano = (Integer) linha[2];
				Long count = (Long) linha[3];
			
				String descricao = "1."+cont+". ";
				descricao += tipoProducao;
				
				MesAno mesAno = new MesAno(mes, ano);
				Map<MesAno, Long> mesesAnos = gerarMapa(tam, dataInicial);
				
				if(resultado.get(descricao) != null){
					mesesAnos = resultado.get(descricao);
				}else{
					descricao = "1."+ ++cont +". "+tipoProducao;
				}
				mesesAnos.put(mesAno, count);
				resultado.put(descricao, mesesAnos);
			}
			
			hql = " SELECT p.interno, month(p.dataCadastro), year(p.dataCadastro), count(p.dataCadastro) "
				+ " FROM ProjetoPesquisa pp join pp.projeto p WHERE ativo = trueValue()"
				+ " AND p.dataCadastro >= '"	+ dataInicial  + "'"
				+ " AND p.dataCadastro <= '" + dataFinal + "'"
				+ (unidade != -1 ? " AND p.unidade.id = " + unidade : "")
				+ " GROUP BY p.interno, month(p.dataCadastro), year(p.dataCadastro) "
				+ " ORDER BY p.interno, month(p.dataCadastro), year(p.dataCadastro) ";
			
			list = getSession().createQuery(hql).list();
			it = list.iterator();
			
			while(it.hasNext()){
				Object[] linha = it.next();
				Boolean interno = (Boolean) linha[0];
				Integer mes = (Integer) linha[1];
				Integer ano = (Integer) linha[2];
				Long count = (Long) linha[3];
				
				String descricao = interno ? "2.1 Projetos Internos" : "2.2 Projetos Externos";
				
				MesAno mesAno = new MesAno(mes, ano);
				Map<MesAno, Long> mesesAnos = gerarMapa(tam, dataInicial);
				
				if(resultado.get(descricao) != null){
					mesesAnos = resultado.get(descricao);
				}
				mesesAnos.put(mesAno, count);
				resultado.put(descricao, mesesAnos);
			}
			
			hql = " SELECT tipoBolsa.id, month(dataIndicacao), year(dataIndicacao), count(dataIndicacao) "
				+ " FROM MembroProjetoDiscente WHERE tipoBolsa.id <> "+ TipoBolsaPesquisa.A_DEFINIR
				+ " AND dataIndicacao >= '"	+ dataInicial  + "'"
				+ " AND dataIndicacao <= '" + dataFinal + "'"
				+ " GROUP BY tipoBolsa.id, month(dataIndicacao), year(dataIndicacao) "
				+ " ORDER BY tipoBolsa.id, month(dataIndicacao), year(dataIndicacao) ";
			
			list = getSession().createQuery(hql).list();
			it = list.iterator();
			
			while(it.hasNext()){
				Object[] linha = it.next();
				Integer tipoBolsa = (Integer) linha[0];
				Integer mes = (Integer) linha[1];
				Integer ano = (Integer) linha[2];
				Long count = (Long) linha[3];
				
				String descricao = tipoBolsa == TipoBolsaPesquisa.VOLUNTARIO ? "4. Voluntários Cadastrados" : "3. Bolsistas Cadastrados";
				
				MesAno mesAno = new MesAno(mes, ano);
				Map<MesAno, Long> mesesAnos = gerarMapa(tam, dataInicial);
				
				if(resultado.get(descricao) != null){
					mesesAnos = resultado.get(descricao);
				}
				mesesAnos.put(mesAno, count);
				resultado.put(descricao, mesesAnos);
			}
			
			return resultado;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}
	
	/**
	 * Busca os docentes permanentes de um grupo de pesquisa.
	 * 
	 * @param idGrupoPesquisa
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Integer> findByDocentesPermanentes(int idGrupoPesquisa) throws DAOException {
		StringBuilder sql = new StringBuilder();
			sql.append("select s.id_servidor, p.nome");
	
			sql.append(" from pesquisa.membro_grupo_pesquisa m " +
					   " inner join pesquisa.grupo_pesquisa gp using (id_grupo_pesquisa)"+
					   " inner join rh.servidor s on (m.id_servidor=s.id_servidor)" +
					   " inner join comum.pessoa p on (s.id_pessoa=p.id_pessoa)");
			if (idGrupoPesquisa == 0) {
				sql.append(" inner join comum.unidade u on (s.id_unidade = u.id_unidade)" +
					   " inner join comum.unidade u2 on (u.unidade_responsavel = u2.id_unidade)");
			}
			
			if (idGrupoPesquisa == 0) 
				sql.append(" where gp.ativo = true and s.id_ativo not in " + UFRNUtils.gerarStringIn(new int[] { Ativo.APOSENTADO, Ativo.EXCLUIDO }) + " and ");
			else
				sql.append(" where m.id_grupo_pesquisa="+idGrupoPesquisa + " and ");
			
			sql.append(" m.id_tipo_membro_grupo_pesquisa="+TipoMembroGrupoPesquisa.PERMANENTE+
			" and gp.nome is not null order by");
			
			sql.append(" gp.nome, p.nome");
			
			if (idGrupoPesquisa == 0) 
				sql.append(", u2.nome, u.nome");
		
		List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql.toString());
		
		Map<String, Integer> docentes = new TreeMap<String, Integer>();
		for(Map<String, Object> linha: list){
			docentes.put((String) linha.get("nome"), (Integer) linha.get("id_servidor"));
		}
		return docentes;
	}
	
	/**
	 * Efetua a geração do relatório para avaliação dos grupos de pesquisa,
	 * realizando as consultas dos itens e montando o relatório conforme o modelo solicitado pela PROPESQ.
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @param docentes
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public RelatorioAvaliacaoGrupoPesquisa generateAvaliacaoGrupoPesquisa(
			Date dataInicial, Date dataFinal, Map<String,Integer> docentes, boolean todos) throws DAOException {
		try {
			RelatorioAvaliacaoGrupoPesquisa relatorio = new RelatorioAvaliacaoGrupoPesquisa();
			relatorio.setDocentes(docentes.keySet());
			
			relatorio.getResultado().put(RelatorioAvaliacaoGrupoPesquisa.ART_COMP_PER_IND_NAC, new LinhaRelatorioGrupoPesquisa(30, docentes.keySet()));
			relatorio.getResultado().put(RelatorioAvaliacaoGrupoPesquisa.ART_COMP_PER_IND_INT, new LinhaRelatorioGrupoPesquisa(50, docentes.keySet()));
			
			String sql= " select pe.nome  as nome, tr.id_tipo_regiao, tr.descricao, count(a.id_artigo)" +
						" from prodocente.producao p" +
						" inner join prodocente.artigo a on (p.id_producao=a.id_artigo)" +
						" left outer join prodocente.tipo_regiao tr on (a.id_tipo_regiao=tr.id_tipo_regiao)" +
						" inner join rh.servidor s on (p.id_servidor=s.id_servidor)" +
						" inner join comum.pessoa pe on (s.id_pessoa=pe.id_pessoa)" +
						" inner join pesquisa.membro_grupo_pesquisa m on (s.id_servidor=m.id_servidor)"+ 
						" inner join pesquisa.grupo_pesquisa gp using (id_grupo_pesquisa) "+
						" where m.id_servidor in " + UFRNUtils.gerarStringIn(docentes.values()) +
						" and p.data_producao > ?" +
						" and p.data_producao <= ?" +
						" and a.id_tipo_regiao in " + UFRNUtils.gerarStringIn(new int[]{TipoRegiao.NACIONAL, TipoRegiao.INTERNACIONAL})+
						" and (a.id_tipo_periodico is null or a.id_tipo_periodico not in " 
						+ UFRNUtils.gerarStringIn(new int[]{TipoPeriodico.JORNAL_NAO_CIENTIFICO, TipoPeriodico.REVISTA_NAO_CIENTIFICA})+")"+
						" and gp.nome is not null"+
						" group by gp.nome, pe.nome, tr.id_tipo_regiao, tr.descricao, s.id_servidor"+
						" order by gp.nome, pe.nome, tr.descricao";
			
			JdbcTemplate template = getJdbcTemplate();
			List<Map<String, Object>> list = template.queryForList(sql.toString(), new Object[]{dataInicial, dataFinal});
			
			for(Map<String, Object> linha: list){
				int idTipoRegiao = (Integer) linha.get("id_tipo_regiao");
				if(idTipoRegiao == TipoRegiao.NACIONAL)
					relatorio.getResultado().get(RelatorioAvaliacaoGrupoPesquisa.ART_COMP_PER_IND_NAC).getDocentes()
						.put((String)linha.get("nome"), (Long)linha.get("count"));
				else
					relatorio.getResultado().get(RelatorioAvaliacaoGrupoPesquisa.ART_COMP_PER_IND_INT).getDocentes()
						.put((String)linha.get("nome"), (Long)linha.get("count"));
			}
			
			relatorio.getResultado().put(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_LOC, new LinhaRelatorioGrupoPesquisa(1, docentes.keySet()));
			relatorio.getResultado().put(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_REG, new LinhaRelatorioGrupoPesquisa(2, docentes.keySet()));
			relatorio.getResultado().put(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_NAC, new LinhaRelatorioGrupoPesquisa(3, docentes.keySet()));
			relatorio.getResultado().put(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_INT, new LinhaRelatorioGrupoPesquisa(4, docentes.keySet()));
			
			relatorio.getResultado().put(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_EX_LOC, new LinhaRelatorioGrupoPesquisa(2, docentes.keySet()));
			relatorio.getResultado().put(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_EX_REG, new LinhaRelatorioGrupoPesquisa(4, docentes.keySet()));
			relatorio.getResultado().put(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_EX_NAC, new LinhaRelatorioGrupoPesquisa(6, docentes.keySet()));
			relatorio.getResultado().put(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_EX_INT, new LinhaRelatorioGrupoPesquisa(8, docentes.keySet()));
			
			relatorio.getResultado().put(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_TC_LOC, new LinhaRelatorioGrupoPesquisa(4, docentes.keySet()));
			relatorio.getResultado().put(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_TC_REG, new LinhaRelatorioGrupoPesquisa(8, docentes.keySet()));
			relatorio.getResultado().put(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_TC_NAC, new LinhaRelatorioGrupoPesquisa(12, docentes.keySet()));
			relatorio.getResultado().put(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_TC_INT, new LinhaRelatorioGrupoPesquisa(16, docentes.keySet()));
			
			sql = new String();
				sql = "select pe.nome as nome, pl.natureza, tr.id_tipo_regiao, tr.descricao, count(pl.id_publicacao_evento)"+
					  " from prodocente.producao p" +
					  "	inner join prodocente.publicacao_evento pl on (p.id_producao=pl.id_publicacao_evento)" +
					  "	left outer join prodocente.tipo_regiao tr on (pl.id_tipo_regiao=tr.id_tipo_regiao)" +
  					  "	inner join rh.servidor s on (p.id_servidor=s.id_servidor)" +
					  "	inner join comum.pessoa pe on (s.id_pessoa=pe.id_pessoa)" +
					  "	inner join pesquisa.membro_grupo_pesquisa m on (s.id_servidor=m.id_servidor)" +
					  "	inner join pesquisa.grupo_pesquisa gp using (id_grupo_pesquisa) "+
				      " where m.id_servidor in " + UFRNUtils.gerarStringIn(docentes.values()) +
					  " and p.data_producao > ?" +
					  " and p.data_producao <= ?" +
					  " and pl.natureza is not null" +
					  " and gp.nome is not null"+
					  " group by gp.nome, pe.nome, pl.natureza, tr.id_tipo_regiao, tr.descricao, s.id_servidor"+
					  " order by gp.nome, pe.nome, tr.descricao";
			
			list.clear();
			list = template.queryForList(sql.toString(), new Object[]{dataInicial, dataFinal});
	
			for(Map<String, Object> linha: list){
				int idTipoRegiao = (Integer) linha.get("id_tipo_regiao");
				String natureza = (String) linha.get("natureza");
				if(natureza.equals("R")){
					if(idTipoRegiao == TipoRegiao.LOCAL)
						relatorio.getResultado().get(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_LOC).getDocentes()
							.put((String)linha.get("nome"), (Long)linha.get("count"));
					else if(idTipoRegiao == TipoRegiao.REGIONAL)
						relatorio.getResultado().get(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_REG).getDocentes()
							.put((String)linha.get("nome"), (Long)linha.get("count"));
					else if(idTipoRegiao == TipoRegiao.NACIONAL)
						relatorio.getResultado().get(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_NAC).getDocentes()
							.put((String)linha.get("nome"), (Long)linha.get("count"));
					else
						relatorio.getResultado().get(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_INT).getDocentes()
							.put((String)linha.get("nome"), (Long)linha.get("count"));
				}else if(natureza.equals("E")){
					if(idTipoRegiao == TipoRegiao.LOCAL)
						relatorio.getResultado().get(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_EX_LOC).getDocentes()
							.put((String)linha.get("nome"), (Long)linha.get("count"));
					else if(idTipoRegiao == TipoRegiao.REGIONAL)
						relatorio.getResultado().get(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_EX_REG).getDocentes()
							.put((String)linha.get("nome"), (Long)linha.get("count"));
					else if(idTipoRegiao == TipoRegiao.NACIONAL)
						relatorio.getResultado().get(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_EX_NAC).getDocentes()
							.put((String)linha.get("nome"), (Long)linha.get("count"));
					else
						relatorio.getResultado().get(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_EX_INT).getDocentes()
							.put((String)linha.get("nome"), (Long)linha.get("count"));
				}else if(natureza.equals("T")){
					if(idTipoRegiao == TipoRegiao.LOCAL)
						relatorio.getResultado().get(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_TC_LOC).getDocentes()
							.put((String)linha.get("nome"), (Long)linha.get("count"));
					else if(idTipoRegiao == TipoRegiao.REGIONAL)
						relatorio.getResultado().get(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_TC_REG).getDocentes()
							.put((String)linha.get("nome"), (Long)linha.get("count"));
					else if(idTipoRegiao == TipoRegiao.NACIONAL)
						relatorio.getResultado().get(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_TC_NAC).getDocentes()
							.put((String)linha.get("nome"), (Long)linha.get("count"));
					else
						relatorio.getResultado().get(RelatorioAvaliacaoGrupoPesquisa.AN_EVT_RES_TC_INT).getDocentes()
							.put((String)linha.get("nome"), (Long)linha.get("count"));
				}
			}
			
			relatorio.getResultado().put(RelatorioAvaliacaoGrupoPesquisa.LIVRO_ISBN_NAC, new LinhaRelatorioGrupoPesquisa(50, docentes.keySet()));
			relatorio.getResultado().put(RelatorioAvaliacaoGrupoPesquisa.LIVRO_ISBN_INT, new LinhaRelatorioGrupoPesquisa(60, docentes.keySet()));
			
			sql = new String();
				sql = " select pe.nome as nome, tr.id_tipo_regiao, tr.descricao, count(l.id_livro)"+
					  " from prodocente.producao p" +
					  "	inner join prodocente.livro l on (p.id_producao=l.id_livro)" +
					  "	left outer join prodocente.tipo_regiao tr on (l.id_tipo_regiao=tr.id_tipo_regiao)" +
					  "	inner join rh.servidor s on (p.id_servidor=s.id_servidor)" +
					  "	inner join comum.pessoa pe on (s.id_pessoa=pe.id_pessoa)" +
					  "	inner join pesquisa.membro_grupo_pesquisa m on (s.id_servidor=m.id_servidor)" +
					  " inner join pesquisa.grupo_pesquisa gp using (id_grupo_pesquisa)"+
					  " where m.id_servidor in " + UFRNUtils.gerarStringIn(docentes.values()) +
					  " and p.data_producao > ?" +
					  " and p.data_producao <= ?" +
					  " and gp.nome is not null "+
					  " and l.id_tipo_regiao in " + UFRNUtils.gerarStringIn(new int[]{TipoRegiao.NACIONAL, TipoRegiao.INTERNACIONAL})+
					  " group by gp.nome, pe.nome, tr.id_tipo_regiao, tr.descricao, s.id_servidor"+
					  " order by gp.nome, tr.descricao, pe.nome";
			
			list.clear();
			list = template.queryForList(sql.toString(), new Object[]{dataInicial, dataFinal});
			
			for(Map<String, Object> linha: list){
				int idTipoRegiao = (Integer) linha.get("id_tipo_regiao");
				if(idTipoRegiao == TipoRegiao.NACIONAL)
					relatorio.getResultado().get(RelatorioAvaliacaoGrupoPesquisa.LIVRO_ISBN_NAC).getDocentes()
						.put((String)linha.get("nome"), (Long)linha.get("count"));
				else
					relatorio.getResultado().get(RelatorioAvaliacaoGrupoPesquisa.LIVRO_ISBN_INT).getDocentes()
						.put((String)linha.get("nome"), (Long)linha.get("count"));
			}
			
			relatorio.getResultado().put(RelatorioAvaliacaoGrupoPesquisa.CAP_LIVRO_ISBN_NAC, new LinhaRelatorioGrupoPesquisa(30, docentes.keySet()));
			relatorio.getResultado().put(RelatorioAvaliacaoGrupoPesquisa.CAP_LIVRO_ISBN_INT, new LinhaRelatorioGrupoPesquisa(40, docentes.keySet()));
			
			sql = new String();
				sql = "select pe.nome as nome, tr.id_tipo_regiao, tr.descricao, count(c.id_capitulo)"+
					  " from prodocente.producao p" +
					  "	inner join prodocente.capitulo c on (p.id_producao=c.id_capitulo)" +
					  "	left outer join prodocente.tipo_regiao tr on (c.id_tipo_regiao=tr.id_tipo_regiao)" +
					  "	inner join rh.servidor s on (p.id_servidor=s.id_servidor)" +
					  "	inner join comum.pessoa pe on (s.id_pessoa=pe.id_pessoa)" +
					  "	inner join pesquisa.membro_grupo_pesquisa m on (s.id_servidor=m.id_servidor)" +
					  "	inner join pesquisa.grupo_pesquisa gp using (id_grupo_pesquisa)"+
					  " where m.id_servidor in " + UFRNUtils.gerarStringIn(docentes.values()) +
					  " and p.data_producao > ?" +
					  " and p.data_producao <= ?" +
					  " and gp.nome is not null "+
					  " and c.id_tipo_regiao in " +UFRNUtils.gerarStringIn(new int[]{TipoRegiao.NACIONAL, TipoRegiao.INTERNACIONAL})+
					  " group by gp.nome, pe.nome, tr.id_tipo_regiao, tr.descricao, s.id_servidor"+
					  " order by gp.nome, tr.descricao, pe.nome";
				
			list.clear();
			list = template.queryForList(sql.toString(), new Object[]{dataInicial, dataFinal});

			for(Map<String, Object> linha: list){
				int idTipoRegiao = (Integer) linha.get("id_tipo_regiao");
				if(idTipoRegiao == TipoRegiao.NACIONAL)
					relatorio.getResultado().get(RelatorioAvaliacaoGrupoPesquisa.CAP_LIVRO_ISBN_NAC).getDocentes()
						.put((String)linha.get("nome"), (Long)linha.get("count"));
				else
					relatorio.getResultado().get(RelatorioAvaliacaoGrupoPesquisa.CAP_LIVRO_ISBN_INT).getDocentes()
						.put((String)linha.get("nome"), (Long)linha.get("count"));
			}
			
			return relatorio;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}
	
	/**
	 * Método auxiliar para gerar um mapa de meses e quantidades a partir de uma data
	 * e um tamanho especificado. Utilizado na geração dos relatórios quantitativos.
	 * @param tam
	 * @param dataInicial
	 * @return
	 */
	private Map<MesAno, Long> gerarMapa(long tam, Date dataInicial) {
		
		Map<MesAno, Long> mesesAnos = new TreeMap<MesAno, Long>();
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataInicial);
		int mes = cal.get(Calendar.MONTH) + 1;
		int ano = cal.get(Calendar.YEAR);
//		cal.setTime(dataFinal);
//		int mes2 = cal.get(Calendar.MONTH);
//		int ano2 = cal.get(Calendar.YEAR);
		
		for (int i = 0; i < tam; i++) {
			mesesAnos.put(new MesAno(mes++, ano), 0L);
			if(mes > 12){
				mes = 1;
				ano++;
			}
		}
		
		return mesesAnos;
	}

	/**
	 * Busca os subtipos artísticos de uma determinada produção intelectual.
	 * @param producao
	 * @return
	 * @throws DAOException 
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<SubTipoArtistico> findSubTipos(Producao producao) throws DAOException {
		Criteria criteria = getCriteria(SubTipoArtistico.class);
		criteria.add(Expression.eq("tipoProducao", producao
				.getTipoProducao()));
		criteria.addOrder(Order.asc("descricao"));

		return criteria.list();
	}

	/**
	 * Busca produções de um determinado docente por tipo, com a possibilidade de utilizar paginação
	 * @param classe
	 * @param servidor
	 * @param tipoProducao
	 * @param paginacao
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Object> findByTipoProducaoServidor(Class classe, Servidor servidor, TipoProducao tipoProducao, PagingInformation paginacao) throws DAOException {

		Criteria criteria = getCriteria(classe);
		criteria.add(Expression.eq("tipoProducao", tipoProducao));
		criteria.add(Expression.eq("servidor", servidor));
		criteria.add(Expression.or(Expression.eq("ativo", true), Expression.isNull("ativo")));

		Long total = (Long) getSession().createQuery(
				"select count(*) from " + classe.getName()
						+ " where tipoProducao.id = "
						+ tipoProducao.getId() + " and servidor.id  = "
						+ servidor.getId()).uniqueResult();

		if (paginacao != null) {
			paginacao.setTotalRegistros((int) total.longValue());
		}

		if (paginacao != null) {
			criteria.setFirstResult(paginacao.getPaginaAtual()
					* paginacao.getTamanhoPagina());
			criteria.setMaxResults(paginacao.getTamanhoPagina());
		}
		return criteria.list();
	}

	/**
	 * Busca produções para o acervo por título e departamento.
	 * @param titulo
	 * @param departamento
	 * @param paginacao
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Producao> findByAcervo(String titulo, int departamento,
			PagingInformation paginacao) throws DAOException {

		String hql = "select new Producao(p.id, p.titulo, p.servidor.id, p.servidor.pessoa.nome, "
				+ "p.tipoProducao.id, p.tipoProducao.descricao, p.anoReferencia, p.servidor.unidade.sigla, p.idArquivo )  "
				+ " from Producao p where p.idArquivo is not null ";

		String where = "";

		if (titulo != null && !titulo.equals("")) {
			
			titulo = "%"+ titulo;
			String[] strings = titulo.split(" ");
			StringBuilder resultado = new StringBuilder();
			for (String string : strings) {
				resultado.append(string + "%");
			}
			
			
			where += " and " + UFRNUtils.toAsciiUpperUTF8("p.titulo")
					+ " like "
					+ UFRNUtils.toAsciiUpperUTF8("'" + resultado.toString() + "'");

		}
		if (departamento != -1) {
			where += " and p.servidor.unidade.id = " + departamento;
		}

		Query qTotal = getSession().createQuery(
				"select count(*) from Producao p where p.idArquivo is not null "
						+ where);
		Long total = (Long) qTotal.uniqueResult();

		Query q = getSession().createQuery(
				hql + where + " order by p.titulo ");

		if (paginacao != null) {
			paginacao.setTotalRegistros((int) total.longValue());
			q.setFirstResult(paginacao.getPaginaAtual()
					* paginacao.getTamanhoPagina());
			q.setMaxResults(paginacao.getTamanhoPagina());
		}

		return q.list();
	}


	/**
	 * Busca as produções para o acervo utilizando diversos critérios opcionais.
	 * @param titulo
	 * @param tipoProducao
	 * @param areaConhecimento
	 * @param anoPublicacao
	 * @param departamento
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Producao> findByAcervo(String titulo,	Integer tipoProducao, Integer areaConhecimento,	Integer anoPublicacao,
			Integer departamento) throws DAOException {
		
		
		String hql = "select new Producao(p.id, p.titulo, p.servidor.id, p.servidor.pessoa.nome, "
				+ "p.tipoProducao.id, p.tipoProducao.descricao, p.anoReferencia, p.servidor.unidade.sigla, p.idArquivo, u.login )  "
				+ " from Producao p, Usuario u where p.idArquivo is not null and p.servidor.pessoa.id = u.pessoa.id";

		String where = "";

		if (titulo != null && !titulo.equals("")) {
			
			titulo = "%"+ titulo;
			String[] strings = titulo.split(" ");
			StringBuilder resultado = new StringBuilder();
			for (String string : strings) {
				resultado.append(string + "%");
			}
			
			
			where += " and " + UFRNUtils.toAsciiUpperUTF8("p.titulo")
					+ " like "
					+ UFRNUtils.toAsciiUpperUTF8("'" + resultado.toString() + "'");

		}
		
		if (tipoProducao != null) {
			where += " and p.tipoProducao.id = " + tipoProducao;
		}
		
		if (areaConhecimento != null) {
			where += " and p.area.id = " + areaConhecimento;
		}
		
		if (anoPublicacao != null) {
			where += " and p.anoReferencia = " + anoPublicacao;
		}
		
		if (departamento != null) {
			where += " and p.servidor.unidade.id = " + departamento;
		}

		
		Query q = getSession().createQuery(
				hql + where + " order by  p.tipoProducao.id, p.servidor.id, p.anoReferencia desc, p.titulo");


		return q.list();

	}	
	
	/**
	 * Retorna o bloqueio de cadastro de produções ativo
	 * @return
	 * @throws DAOException
	 */
	public BloqueioCadastroProducao findByBloqueio() throws DAOException {

		Criteria c = getSession()
				.createCriteria(BloqueioCadastroProducao.class);
		c.add(Expression.eq("ativo", Boolean.TRUE));
		c.setMaxResults(1);

		return (BloqueioCadastroProducao) c.uniqueResult();

	}

	/**
	 * Método usado para buscar as produções intelectuais com projeção para a listagem
	 * 
	 * Obs.: Foi acrescentado os parâmetros anoInicial e anoFinal e filtro, para que seja feito uma busca pelos parâmetros, 
	 * informados.   
	 *  
	 * @param s
	 * @param paginacao
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Artigo> findProducaoProjection(String producao, Servidor s, PagingInformation paginacao, int anoInicial, int anoFinal, int filtro) throws  DAOException {
		
		StringBuilder corpoSql = new StringBuilder();
		
		if (anoFinal != 0 && anoInicial != 0) {
			corpoSql.append(" from " + producao + " p where p.servidor.id = " + s.getId()+
				" AND p.anoReferencia >="+anoInicial+" AND p.anoReferencia <= "+anoFinal+"");
				// 1 = valida, 0 = invalida , -1 = pendente, 2 = Todas	
				 if (filtro==1) {
					corpoSql.append(" AND p.validado = trueValue()");
				}if (filtro==0) {
					corpoSql.append(" AND p.validado = falseValue()");
				}if (filtro == -1) {
					corpoSql.append(" AND p.validado is null");
				}
		}else{
			corpoSql.append(" from " + producao + " p where p.servidor.id = " + s.getId());
		}

		corpoSql.append(" AND (p.ativo = trueValue() OR p.ativo is null) ");
		
		Query qTotal = getSession().createQuery("select count(p) " + corpoSql);

		//int id, String titulo, String tipoParticipacao, int ano, Boolean validado, Boolean ativo
		Query q = getSession().createQuery("select new " + construtoresProjecao.get(producao) + corpoSql +
					" order by p.anoReferencia desc, p.dataProducao desc");

		if (paginacao != null) {
			paginacao.setTotalRegistros(((Long) qTotal.uniqueResult()).intValue());
			q.setFirstResult(paginacao.getPaginaAtual()	* paginacao.getTamanhoPagina());
			q.setMaxResults(paginacao.getTamanhoPagina());
		}

		return q.list();

	}

	/**
	 * Retorna os docentes que ainda não consolidaram suas produções
	 * @param docente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Servidor> findDocentesPendentesConsolidacao(Servidor consolidador, Unidade unidade, boolean chefe) throws DAOException {
		ServidorDao servidorDao = new ServidorDao();
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" SELECT p.servidor FROM Producao p");
			hql.append(" WHERE (p.ativo is null or p.ativo = trueValue()) ");
			hql.append(" and (validado != null and validado = trueValue())  "); // Somente validados
			hql.append(" and (consolidado = null or consolidado = falseValue())"); // Somente pendentes consolidação
			hql.append(" and p.servidor.id != :consolidador ");
			if (chefe) {
				hql.append(" and p.servidor.unidade.id = :unidade ");
			} else {
				// Diretor de centro
				Collection<Servidor> chefes = servidorDao.findChefesDepartamentoByCentro(unidade.getId());

				Collection ids = CollectionUtils.collect(chefes, new Transformer() {
					public Object transform(Object o) {
						return ((Servidor) o).getId() ;
					}
				});
				hql.append(" and p.servidor.id in " + UFRNUtils.gerarStringIn(ids));
			}
			hql.append(" order by p.servidor.unidade.nome, p.servidor.pessoa.nome");

			Query q = getSession().createQuery(hql.toString());
			q.setInteger("consolidador", consolidador.getId());
			if (chefe) {
				q.setInteger("unidade", unidade.getId());
			}

			return q.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e.getMessage(), e);
		} finally {
			servidorDao.close();
		}
	}

	/**
	 * Busca as produções de um docente que ainda não foram consolidadas
	 * @param docente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Producao> findPendentesConsolidacao(Servidor servidor) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" FROM Producao");
			hql.append(" WHERE (ativo is null or ativo = trueValue()) ");
			hql.append(" and (validado != null and validado = trueValue())"); // Somente validados
			hql.append(" and (consolidado = null or consolidado = falseValue())"); // Somente pendentes consolidação
			hql.append(" and servidor.id = :servidor ");

			Query q = getSession().createQuery(hql.toString());
			q.setInteger("servidor", servidor.getId());
			return q.list();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna a banca de um docente com um determinado título e data
	 * @param servidor
	 * @param titulo
	 * @param data
	 * @return
	 * @throws DAOException
	 */
	public Banca findBancaByServidorDiscenteData(Servidor servidor, String titulo, Date data) throws DAOException {
		Criteria c = getSession().createCriteria(Banca.class);
		c.add(eq("servidor", servidor)).add(eq("titulo", titulo)).add(eq("data", data));
		c.setMaxResults(1);

		return (Banca) c.uniqueResult();
	}

	/**
	 * Retorna a banca associada com uma banca de pós e o servidor
	 * 
	 * @param servidor
	 * @param bancaPos
	 * @return
	 * @throws DAOException
	 */
	public Banca findBancaByServidorAndBancaPos(Servidor servidor, BancaPos bancaPos) throws DAOException {
		
		String hql = "select b from Banca b where bancaPos.id = :bancaPos and servidor.id = :servidor";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("bancaPos", bancaPos.getId());
		q.setInteger("servidor", servidor.getId());
		
		return (Banca) q.setMaxResults(1).uniqueResult();
	}	
	
	/**
	 * Retorna a banca associada com uma banca de defesa (Graduação) e o servidor
	 * 
	 * @param servidor
	 * @param bancaDefesa
	 * @return
	 * @throws DAOException
	 */
	public Banca findBancaByServidorAndBancaDefesa(Servidor servidor, BancaDefesa bancaDefesa) throws DAOException {
		
		String hql = "select b from Banca b where bancaDefesa.id = :bancaDefesa and servidor.id = :servidor";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("bancaDefesa", bancaDefesa.getId());
		q.setInteger("servidor", servidor.getId());
		
		return (Banca) q.setMaxResults(1).uniqueResult();
	}		
	
	/**
	 * Retorna as produções intelectuais de um determinado SERVIDOR que possuem um determinado TÍTULO, são de um determinado TIPO de produção e foram publicadas em um determinado Ano.
	 * Atenção: o método não busca produções com título parecidos, ou que contenham partes do título. O título deve ser exato (desconsiderando apenas maiúsculas/minúsculas e espaçamentos de início e fim do título)
	 * Usado no método: boolean isProducaoMesmoAnoTituloTipo(...)
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Producao> findProducaoByServidorAnoTituloTipo(Producao producao) throws DAOException {
		Criteria c = getSession().createCriteria(Producao.class);
		c.add(Expression.eq("tipoProducao.id", producao.getTipoProducao().getId()));
		c.add(Expression.eq("servidor.id", producao.getServidor().getId()));
		c.add(Expression.or(Expression.eq("ativo", true), Expression.isNull("ativo"))); //<-- Só busca produções ATIVAS (ou seja que ativo é != falseValue() [pois ativo pode ser null])
		c.add(Expression.eq("anoReferencia", producao.getAnoReferencia())); //<-- Deve considerar o ano em que a produção foi publicada.

		//Busca também por strings com um espaço no início e fim do título. (A busca é case insensitive)
		c.add( Expression.or( Expression.ilike("titulo", producao.getTitulo()), Expression.or(Expression.ilike("titulo", " "+producao.getTitulo()), Expression.ilike("titulo", producao.getTitulo()+" ")) ) );

		return c.list();
	}

	/**
	 * Retorna TRUE caso já exista uma produção de um determinado tipo, de um determinado servidor, com o mesmo TÍTULO, e publicada no mesmo Ano.
	 * A busca desconsidera maiúsculas/minúsculas e se há espaço no início e fim do título
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public boolean isProducaoMesmoAnoTituloTipo(Producao producao) throws DAOException	{

		if (findProducaoByServidorAnoTituloTipo(producao).size() >0)
			return true;
		return false;
	}

}
