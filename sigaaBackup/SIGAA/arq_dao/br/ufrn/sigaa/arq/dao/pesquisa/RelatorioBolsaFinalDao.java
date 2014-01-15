/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '22/06/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.pesquisa.dominio.CodigoProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.RelatorioBolsaFinal;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * DAO para consultas de relatórios finais de discentes
 * 
 * @author ricardo
 * 
 */
public class RelatorioBolsaFinalDao extends GenericSigaaDAO {

    /**
     * Popula os dados retornados na consulta para uma coleção de relatórios finais
     * 
     * @param lista
     * @return
     */
	@SuppressWarnings("cast")
	private Collection<RelatorioBolsaFinal> montarColecao(List<Object[]> lista) {

		ArrayList<RelatorioBolsaFinal> result = new ArrayList<RelatorioBolsaFinal>();
		for (int a = 0; a < lista.size(); a++) {
			RelatorioBolsaFinal relatorio = new RelatorioBolsaFinal();
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			relatorio.setId((Integer) colunas[col++]);
			relatorio.setDataEnvio((Date) colunas[col++]);
			relatorio.setDataParecer((Date) colunas[col++]);
			relatorio.setEnviado((Boolean) colunas[col++]);

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
	 * Buscar os relatórios finais de planos de trabalho orientados por um
	 * determinado docente
	 * 
	 * @param idOrientador
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<RelatorioBolsaFinal> findByOrientadorAndStatus(
			int idOrientador, Integer statusPlanoTrabalho) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(RelatorioBolsaFinal.class);
			Criteria plano = c.createCriteria("planoTrabalho");
			c.add(Expression.eq("enviado", true));
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
	 * Busca o relatório final mais recente de um discente
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public RelatorioBolsaFinal findRelatorioAtualByDiscente(Discente discente)
			throws DAOException {
		try {
			String hql = "from RelatorioBolsaFinal r"
					+ " where r.membroDiscente.discente.id = "
					+ discente.getId()
					+ " order by r.planoTrabalho.cota.descricao desc";
			return (RelatorioBolsaFinal) getSession().createQuery(hql)
					.setMaxResults(1).uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca os relatórios finais submetidos por um discente ordenados pelo período de cota
	 * do mais recente para o mais antigo.
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<RelatorioBolsaFinal> findByDiscente(Discente discente)
			throws DAOException {
		try {
			String hql = "from RelatorioBolsaFinal r"
					+ " where r.membroDiscente.discente.id = "
					+ discente.getId()
					+ " order by r.planoTrabalho.cota.descricao desc ";
			return getSession().createQuery(hql).list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca todos os relatórios finais submetidos por um discente.
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<RelatorioBolsaFinal> findAllByDiscente(DiscenteAdapter discente)
			throws DAOException {
		try {
			String hql = " SELECT id, dataEnvio, dataParecer, resumo, palavrasChave, introducao, objetivos, metodologia, resultados, " +
					"discussao, conclusoes, perspectivas, bibliografia, outrasAtividades, parecerOrientador, enviado, " +
					"membroDiscente.discente.matricula, membroDiscente.discente.pessoa.nome, planoTrabalho.orientador.pessoa.nome, " +
					"planoTrabalho.projetoPesquisa.codigo.prefixo, planoTrabalho.projetoPesquisa.codigo.numero, planoTrabalho.projetoPesquisa.codigo.ano, " +
					"planoTrabalho.projetoPesquisa.projeto.titulo"
			+" FROM RelatorioBolsaFinal r"
			+" WHERE r.membroDiscente.discente.id = "+ discente.getId();
			
			List<Object[]> colunas = getSession().createQuery(hql.toString()).list();
			
			Collection<RelatorioBolsaFinal> result = new ArrayList<RelatorioBolsaFinal>();
			
			for (int i = 0; i < colunas.size(); i++) {
				int col = 0;
				Object[] obj = colunas.get(i);
				RelatorioBolsaFinal relatorio = new RelatorioBolsaFinal();
				relatorio.setId((Integer) obj[col++]);
				relatorio.setDataEnvio((Date) obj[col++]);
				relatorio.setDataParecer((Date) obj[col++]);
				relatorio.setResumo((String) obj[col++]);
				relatorio.setPalavrasChave((String) obj[col++]);
				relatorio.setIntroducao((String) obj[col++]);
				relatorio.setObjetivos((String) obj[col++]);
				relatorio.setMetodologia((String) obj[col++]);
				relatorio.setResultados((String) obj[col++]);
				relatorio.setDiscussao((String) obj[col++]);
				relatorio.setConclusoes((String) obj[col++]);
				relatorio.setPerspectivas((String) obj[col++]);
				relatorio.setBibliografia((String) obj[col++]);
				relatorio.setOutrasAtividades((String) obj[col++]);
				relatorio.setParecerOrientador((String) obj[col++]);
				relatorio.setEnviado((Boolean) obj[col++]);
				
				MembroProjetoDiscente mpd = new MembroProjetoDiscente();
				Discente d = new Discente();
				d.setMatricula((Long) obj[col++]);
				Pessoa p = new Pessoa();
				p.setNome((String) obj[col++]);
				d.setPessoa(p);
				mpd.setDiscente(d);
								
				PlanoTrabalho pt = new PlanoTrabalho();
				pt.setOrientador(new Servidor());
				pt.getOrientador().getPessoa().setNome((String) obj[col++]);
				
				ProjetoPesquisa pp =  new ProjetoPesquisa();
				pp.setCodigo(new CodigoProjetoPesquisa());
				pp.getCodigo().setPrefixo((String) obj[col++]);
				pp.getCodigo().setNumero((Integer) obj[col++]);
				pp.getCodigo().setAno((Integer) obj[col++]);
				pp.getProjeto().setTitulo((String) obj[col++]);
				
				pt.setProjetoPesquisa(pp);
				relatorio.setMembroDiscente(mpd);
				relatorio.setPlanoTrabalho(pt);

				result.add(relatorio);
			}
			
			return result;
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	/**
	 * Busca relatórios finais utilizando diversos filtros opcionais.
	 * 
	 * @param centro
	 * @param departamento
	 * @param discente
	 * @param orientador
	 * @param modalidade
	 * @param cota
	 * @param parecer
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<RelatorioBolsaFinal> filter(Unidade centro,
			Unidade departamento, Discente discente, Servidor orientador,
			Integer modalidade, CotaBolsas cota, Boolean parecer, Boolean submetido)
			throws DAOException {
		try {

			StringBuilder hql = new StringBuilder();
			hql.append(" SELECT id, dataEnvio, dataParecer, enviado, membroDiscente.discente.matricula, ");
			hql.append("  membroDiscente.discente.pessoa.nome, planoTrabalho.orientador.pessoa.nome");
			hql.append(" FROM RelatorioBolsaFinal r");
			hql.append(" WHERE 1 = 1 ");

			if (cota != null) {
				hql.append(" AND r.planoTrabalho.cota.id = "
								+ cota.getId());
			}
			
			if (centro != null) {
				hql.append(" AND r.planoTrabalho.projetoPesquisa.centro.id = "
								+ centro.getId());
			}
			if (departamento != null) {
				hql.append(" AND r.planoTrabalho.projetoPesquisa.projeto.unidade.id = "
						+ departamento.getId());
			}
			if (discente != null) {
				hql.append(" AND r.membroDiscente.discente.id = "
						+ discente.getId());
			}
			if (orientador != null) {
				hql.append(" AND r.planoTrabalho.orientador.id = "
						+ orientador.getId());
			}
			if (modalidade != null) {
				hql.append(" AND r.planoTrabalho.tipoBolsa = " + modalidade);
			}
			if (parecer != null) {
				hql.append(" AND r.dataParecer is " + (parecer ? "not " : "")
						+ " null");
			}
			if (submetido != null) {
				hql.append(" AND r.enviado = " + submetido);
			}
			 
			hql.append(" AND r.ativo = " + Boolean.TRUE);

			hql.append(" ORDER BY membroDiscente.discente.pessoa.nome asc");

			Query query = getSession().createQuery(hql.toString());
			return montarColecao(query.list());

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca os relatórios finais para serem exibidos no portal do consultor.
	 * 
	 * @param idArea
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<RelatorioBolsaFinal> findPortalConsultor( Integer idArea ) throws DAOException {
        try {
        	Calendar c = Calendar.getInstance();
        	c.setTime(new Date());
        	Query q  = getSession().createQuery("  select r.id, r.dataEnvio, r.enviado, r.parecerOrientador, r.planoTrabalho.titulo, " +
        			" r.planoTrabalho.tipoBolsa.descricao " +
        			" from RelatorioBolsaFinal r " +
        			" where r.planoTrabalho.projetoPesquisa.areaConhecimentoCnpq.grandeArea.id = :idArea" +
        			" and year(r.planoTrabalho.edital.cota.fim) = :ano " +
        			" and r.enviado = :consideraEnviado " +
        			" order by r.planoTrabalho.tipoBolsa.vinculadoCota desc, planoTrabalho.tipoBolsa.descricao, r.planoTrabalho.titulo ");
        	q.setInteger("idArea", idArea);
        	q.setInteger("ano", c.get(Calendar.YEAR));
        	q.setBoolean("consideraEnviado", Boolean.TRUE);
        	
        	List lista = q.list();
            Collection<RelatorioBolsaFinal> result = new ArrayList<RelatorioBolsaFinal>();
        	for (int a = 0; a < lista.size(); a++) {
        		RelatorioBolsaFinal relatorio = new RelatorioBolsaFinal();
				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);

				relatorio.setId( (Integer )colunas[col++]);
				relatorio.setDataEnvio((Date) colunas[col++]);
				relatorio.setEnviado((Boolean) colunas[col++]);
				relatorio.setParecerOrientador((String) colunas[col++]);

				PlanoTrabalho plano = new PlanoTrabalho();
				plano.setTitulo((String) colunas[col++]);
				
				plano.setTipoBolsa(new TipoBolsaPesquisa());
				plano.getTipoBolsa().setDescricao((String) colunas[col++]);
				
				relatorio.setPlanoTrabalho(plano);
				result.add(relatorio);
        	}

        	return result;
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}
	
	/**
	 * Busca apenas as informações do relatório necessárias para serem exibidas na view de resumo.
	 * @param idRelatorio
	 * @return
	 * @throws DAOException
	 */
	public RelatorioBolsaFinal findOtimizadoParaView(int idRelatorio) throws DAOException {
		try {
			String hql = " SELECT id, dataEnvio, dataParecer, resumo, palavrasChave, introducao, objetivos, metodologia, resultados, " +
					"discussao, conclusoes, perspectivas, bibliografia, outrasAtividades, parecerOrientador, " +
					"membroDiscente.discente.matricula, membroDiscente.discente.pessoa.nome, planoTrabalho.orientador.pessoa.nome, " +
					"planoTrabalho.projetoPesquisa.codigo.prefixo, planoTrabalho.projetoPesquisa.codigo.numero, planoTrabalho.projetoPesquisa.codigo.ano, " +
					"planoTrabalho.projetoPesquisa.projeto.titulo"
			+" FROM RelatorioBolsaFinal r"
			+" WHERE r.id = "+ idRelatorio;
			
			Object[] colunas = (Object[]) getSession().createQuery(hql.toString()).uniqueResult();
			
			RelatorioBolsaFinal relatorio = null;
			
			if(colunas != null){
				int col = 0;
				relatorio = new RelatorioBolsaFinal();
				relatorio.setId((Integer) colunas[col++]);
				relatorio.setDataEnvio((Date) colunas[col++]);
				relatorio.setDataParecer((Date) colunas[col++]);
				relatorio.setResumo((String) colunas[col++]);
				relatorio.setPalavrasChave((String) colunas[col++]);
				relatorio.setIntroducao((String) colunas[col++]);
				relatorio.setObjetivos((String) colunas[col++]);
				relatorio.setMetodologia((String) colunas[col++]);
				relatorio.setResultados((String) colunas[col++]);
				relatorio.setDiscussao((String) colunas[col++]);
				relatorio.setConclusoes((String) colunas[col++]);
				relatorio.setPerspectivas((String) colunas[col++]);
				relatorio.setBibliografia((String) colunas[col++]);
				relatorio.setOutrasAtividades((String) colunas[col++]);
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
	
	/** Recupera o id do plano do relatorio final de plano de trabalho  */
	public RelatorioBolsaFinal findByPlanoTrabalho(int idPlanoTrabalho) throws DAOException {
		Criteria c = getSession().createCriteria(RelatorioBolsaFinal.class);
		c.add(Restrictions.eq("planoTrabalho.id", idPlanoTrabalho));
		c.setMaxResults(1);
		return (RelatorioBolsaFinal) c.uniqueResult();
	}
	
}
