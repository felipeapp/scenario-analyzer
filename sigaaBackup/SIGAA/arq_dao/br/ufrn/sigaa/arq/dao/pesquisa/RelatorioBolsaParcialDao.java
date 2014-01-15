/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '08/02/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.pesquisa.dominio.CodigoProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.RelatorioBolsaParcial;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * DAO para consultas de relatórios parciais de discentes
 * 
 * @author ricardo
 * 
 */
public class RelatorioBolsaParcialDao extends GenericSigaaDAO {

	/**
	 * Busca apenas as informações do relatório necessárias para serem exibidas na view de resumo.
	 * @param idRelatorio
	 * @return
	 * @throws DAOException
	 */
	public RelatorioBolsaParcial findOtimizadoParaView(int idRelatorio) throws DAOException {
		try {
			String hql = " SELECT id, dataEnvio, dataParecer, atividadesRealizadas, comparacaoOriginalExecutado, outrasAtividades, resultadosPreliminares, parecerOrientador, " +
					"membroDiscente.discente.matricula, membroDiscente.discente.pessoa.nome, planoTrabalho.orientador.pessoa.nome, " +
					"planoTrabalho.projetoPesquisa.codigo.prefixo, planoTrabalho.projetoPesquisa.codigo.numero, planoTrabalho.projetoPesquisa.codigo.ano, " +
					"planoTrabalho.projetoPesquisa.projeto.titulo"
			+" FROM RelatorioBolsaParcial r"
			+" WHERE r.id = "+ idRelatorio;
			
			Object[] colunas = (Object[]) getSession().createQuery(hql.toString()).uniqueResult();
			
			RelatorioBolsaParcial relatorio = null;
			
			if(colunas != null){
				int col = 0;
				relatorio = new RelatorioBolsaParcial();
				relatorio.setId((Integer) colunas[col++]);
				relatorio.setDataEnvio((Date) colunas[col++]);
				relatorio.setDataParecer((Date) colunas[col++]);
				relatorio.setAtividadesRealizadas((String) colunas[col++]);
				relatorio.setComparacaoOriginalExecutado((String) colunas[col++]);
				relatorio.setOutrasAtividades((String) colunas[col++]);
				relatorio.setResultadosPreliminares((String) colunas[col++]);
				relatorio.setParecerOrientador((String) colunas[col++]);
				
				MembroProjetoDiscente mpd = new MembroProjetoDiscente();
				Discente d = new Discente();
				d.setMatricula((Long) colunas[col++]);
				Pessoa p = new Pessoa();
				p.setNome((String) colunas[col++]);
				d.setPessoa(p);
				mpd.setDiscente(d);
								
				PlanoTrabalho pt = new PlanoTrabalho();
				pt.setOrientador(new Servidor());
				pt.getOrientador().getPessoa().setNome((String) colunas[col++]);
				
				ProjetoPesquisa pp =  new ProjetoPesquisa();
				pp.setCodigo(new CodigoProjetoPesquisa());
				pp.getCodigo().setPrefixo((String) colunas[col++]);
				pp.getCodigo().setNumero((Integer) colunas[col++]);
				pp.getCodigo().setAno((Integer) colunas[col++]);
				pp.getProjeto().setTitulo((String) colunas[col++]);
				
				pt.setProjetoPesquisa(pp);
				relatorio.setMembroDiscente(mpd);
				relatorio.setPlanoTrabalho(pt);
			}
			
			return relatorio;
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna todos os relatórios de bolsa parcial, que possua ou não parecer. 
	 * 
	 * @param parecer
	 * @return
	 * @throws DAOException
	 */
	public Collection<RelatorioBolsaParcial> findAllOtimizado(boolean parecer)
			throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql
					.append(" SELECT id, dataEnvio, dataParecer, membroDiscente.discente.matricula, ");
			hql
					.append("  membroDiscente.discente.pessoa.nome, planoTrabalho.orientador.pessoa.nome");
			hql.append(" FROM RelatorioBolsaParcial r");

			if (parecer)
				hql.append(" where r.parecerOrientador is not null ");

			hql.append(" ORDER BY membroDiscente.discente.pessoa.nome asc");

			Query query = getSession().createQuery(hql.toString());
			return montarColecao(query.list());

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retornar os relatórios de Bolsa Parcial enviados pelos usuários.
	 * 
	 * @param centro
	 * @param departamento
	 * @param modalidade
	 * @param orientador
	 * @param cota
	 * @param aluno
	 * @param parecer
	 * @return
	 * @throws DAOException
	 */
	public Collection<RelatorioBolsaParcial> findByRelatoriosBolsaParcial(Integer centro, Integer departamento, 
			Integer modalidade, Integer orientador, Integer cota, Integer aluno, boolean parecer) throws DAOException {
		StringBuilder hql = new StringBuilder(); 
		hql.append("select id, dataEnvio, dataParecer, membroDiscente.discente.matricula, membroDiscente.discente.pessoa.nome, " +
				"planoTrabalho.orientador.pessoa.nome from RelatorioBolsaParcial r where 1=1 ");

		if (centro != null && centro != 0) 
			hql.append("and r.planoTrabalho.projetoPesquisa.projeto.unidade.gestora.id = :centro ");

		if (departamento != null && departamento != 0) 
			hql.append("and r.planoTrabalho.projetoPesquisa.projeto.unidade.id = :departamento ");
		
		if (modalidade != null && modalidade != 0) 
			hql.append("and r.planoTrabalho.tipoBolsa = :tipoBolsa ");
		
		if (orientador != null && orientador != 0) 
			hql.append("and r.planoTrabalho.orientador.id = :orientador ");

		if (cota != null && cota != 0) 
			hql.append("and r.planoTrabalho.cota.id = :cota ");
		
		if (aluno != null && aluno != 0) 
			hql.append("and r.enviado = trueValue() and r.membroDiscente.discente.id = :aluno ");
		
		if (parecer)
			hql.append(" and r.parecerOrientador is not null ");
		
		hql.append(" and r.ativo = " + Boolean.TRUE);
		
		hql.append(" order by membroDiscente.discente.pessoa.nome asc");
		
		Query q = getSession().createQuery(hql.toString());
		
		if (centro != null && centro != 0) q.setInteger("centro", centro);
		if (departamento != null && departamento != 0) q.setInteger("departamento", departamento);
		if (modalidade != null && modalidade != 0) q.setInteger("tipoBolsa", modalidade);
		if (orientador != null && orientador != 0) q.setInteger("orientador", orientador);
		if (cota != null && cota != 0) q.setInteger("cota", cota);
		if (aluno != null && aluno != 0) q.setInteger("aluno", aluno);

		return montarColecao(q.list());
	}

	/**
	 * Método responsável por montar uma coleção com os relatórios de bolsa parcial.
	 * 
	 * @param lista
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Collection<RelatorioBolsaParcial> montarColecao(List lista) {

		ArrayList<RelatorioBolsaParcial> result = new ArrayList<RelatorioBolsaParcial>();
		for (int a = 0; a < lista.size(); a++) {
			RelatorioBolsaParcial relatorio = new RelatorioBolsaParcial();
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			relatorio.setId((Integer) colunas[col++]);
			relatorio.setDataEnvio((Date) colunas[col++]);
			relatorio.setDataParecer((Date) colunas[col++]);

			// Plano de Trabalho
			PlanoTrabalho plano = new PlanoTrabalho();

			MembroProjetoDiscente membroDiscente = new MembroProjetoDiscente();
			Discente discente = new Discente();
			discente.setMatricula((Long) colunas[col++]);
			discente.getPessoa().setNome((String) colunas[col++]);
			membroDiscente.setDiscente(discente);

			relatorio.setMembroDiscente(membroDiscente);

			// Orientador
			Servidor orientador = new Servidor();
			orientador.getPessoa().setNome((String) colunas[col++]);
			plano.setOrientador(orientador);

			relatorio.setPlanoTrabalho(plano);
			result.add(relatorio);
		}

		return result;
	}

	/**
	 * Buscar os relatórios parciais de planos de trabalho orientados por um
	 * determinado docente
	 * 
	 * @param idOrientador
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<RelatorioBolsaParcial> findByOrientador(int idOrientador,
			Integer statusPlanoTrabalho) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(
					RelatorioBolsaParcial.class);
			c.add(Expression.eq("enviado", Boolean.TRUE));
			Criteria plano = c.createCriteria("planoTrabalho");
			plano.add(Expression.eq("orientador", new Servidor(idOrientador)));
			if (statusPlanoTrabalho != null) {
				plano.add(Expression.eq("status", statusPlanoTrabalho));
			}

			return c.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna todos os relatórios de Bolsa parcial do discente informando.
	 * 
	 * @param discente
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<RelatorioBolsaParcial> findByDiscente(Discente discente)
			throws DAOException {
		try {
			String hql = "from RelatorioBolsaParcial r"
					+ " where r.membroDiscente.discente.id = "
					+ discente.getId()
					+ " order by r.planoTrabalho.cota.descricao desc ";
			return getSession().createQuery(hql).list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna todos os relatórios de Bolsa parcial do discente informando.
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<RelatorioBolsaParcial> findAllByDiscente(DiscenteAdapter discente)
			throws DAOException {
		try {

			Criteria c = getSession().createCriteria(
					RelatorioBolsaParcial.class)
					.createCriteria("planoTrabalho").createCriteria(
							"membrosDiscentes").add(
							Expression.eq("discente.id", discente.getId()));

			return c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Responsável pela verificação da existência ou não do relatório de Bolsa Parcial para o membro discente informado.
	 * 
	 * @param idPlanoTrabalho
	 * @param idMembroDiscente
	 * @return
	 * @throws DAOException
	 */
	public boolean existsRelatorio(int idPlanoTrabalho, int idMembroDiscente) throws DAOException {
		try {
			String hql = "select r.id from RelatorioBolsaParcial r" +
					" where r.planoTrabalho.id = " + idPlanoTrabalho +
					" and r.membroDiscente.id = "+ idMembroDiscente;
			return getSession().createQuery(hql).uniqueResult() != null;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retornar o id relatório de bolsa parcial do plano de trabalho e do membro informandos. 
	 * 
	 * @param idPlanoTrabalho
	 * @param idMembroDiscente
	 * @return
	 * @throws DAOException
	 */
	public Integer findIdByPlanoAndMembro(int idPlanoTrabalho, int idMembroDiscente) throws DAOException {
		try {
			String hql = "select r.id from RelatorioBolsaParcial r" +
					" where r.planoTrabalho.id = " + idPlanoTrabalho +
					" and r.membroDiscente.id = "+ idMembroDiscente;
			return (Integer) getSession().createQuery(hql).uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
}
