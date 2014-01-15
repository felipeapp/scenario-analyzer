/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '28/05/2007'
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
import org.hibernate.criterion.Order;
import org.hibernate.transform.Transformers;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.pesquisa.dominio.CodigoProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.RelatorioProjeto;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * DAO para consultas relacionadas a relatórios
 *
 * @author Ricardo Wendell
 *
 */
public class RelatorioProjetoDao extends GenericSigaaDAO {

	public Collection<RelatorioProjeto> findAll() throws DAOException {
		return findAll(RelatorioProjeto.class,
				new String[] {"projetoPesquisa.codigo.ano", "projetoPesquisa.codigo.numero"},
				new String[] {"desc", "desc"});
	}

	@SuppressWarnings("unchecked")
	public Collection<RelatorioProjeto> findByCoordenador( Servidor servidor ) throws DAOException {
        try {
        	Criteria c = getSession().createCriteria(RelatorioProjeto.class);
        	Criteria cProjeto =  c.createCriteria("projetoPesquisa").add( Expression.eq("coordenador.id", servidor.getId() ));
        	cProjeto.addOrder( Order.desc("codigo.ano") ).addOrder( Order.asc("codigo.numero") );
        	return c.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	@SuppressWarnings("unchecked")
	public Collection<RelatorioProjeto> findByMembroProjeto( Servidor servidor ) throws DAOException {
        try {
        	Query q  = getSession().createQuery("select r.id as id, " +
        			" r.projetoPesquisa as projetoPesquisa, " +
        			" r.dataEnvio as dataEnvio " +
        			" from RelatorioProjeto r " +
        			" join r.projetoPesquisa.projeto as projeto" +
        			" join projeto.equipe as membro " +
        			" where membro.servidor.id = " + servidor.getId() +
        			" order by r.projetoPesquisa.codigo.ano desc, r.projetoPesquisa.codigo.numero ");
        	return q.setResultTransformer( Transformers.aliasToBean(RelatorioProjeto.class) ).list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}
	
	/**
	 * Busca os relatórios de projeto pendentes para avaliação dos consultores
	 * 
	 * @param idArea
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<RelatorioProjeto> findPendentesAvaliacao( Integer idArea ) throws DAOException {
        try {
        	Calendar c = Calendar.getInstance();
        	c.setTime(new Date());
        	Query q  = getSession().createQuery("  select r.id, r.dataEnvio, r.avaliacao, r.projetoPesquisa.projeto.titulo, r.projetoPesquisa.codigo " +
        			" from RelatorioProjeto r " +
        			" where r.projetoPesquisa.areaConhecimentoCnpq.grandeArea.id = :idArea" +
        			" and year(r.projetoPesquisa.edital.cota.fim) = :ano " +
        			" and r.avaliacao = :status " +
        			" order by r.projetoPesquisa.codigo.ano desc, r.projetoPesquisa.codigo.numero ");
        	q.setInteger("idArea", idArea);
        	q.setInteger("ano", c.get(Calendar.YEAR));
        	q.setInteger("status", RelatorioProjeto.PENDENTE);
        	
        	List lista = q.list();
            Collection<RelatorioProjeto> result = new ArrayList<RelatorioProjeto>();
        	for (int a = 0; a < lista.size(); a++) {
        		RelatorioProjeto relatorio = new RelatorioProjeto();
				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);

				relatorio.setId( (Integer )colunas[col++]);
				relatorio.setDataEnvio((Date) colunas[col++]);
				relatorio.setAvaliacao((Integer) colunas[col++]);

				ProjetoPesquisa projeto = new ProjetoPesquisa();
				projeto.setTitulo((String) colunas[col++]);
				projeto.setCodigo((CodigoProjetoPesquisa) colunas[col++]);

				relatorio.setProjetoPesquisa(projeto);
				result.add(relatorio);
        	}

        	return result;
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	@SuppressWarnings("unchecked")
	public Collection<RelatorioProjeto> filter(Integer ano, Integer idCoordenador, Integer idArea) throws DAOException {
        try {
        	StringBuilder hql = new StringBuilder();
        	hql.append(" select r.id, r.dataEnvio, r.avaliacao, r.projetoPesquisa.projeto.titulo, r.projetoPesquisa.codigo, " +
        			" r.projetoPesquisa.coordenador.siape, r.projetoPesquisa.coordenador.pessoa.nome ");
        	hql.append(" from RelatorioProjeto r");
        	hql.append(" where 1 = 1 " );

        	if (ano != null)
        		hql.append(" and r.projetoPesquisa.codigo.ano = :ano");
        	if (idCoordenador != null)
        		hql.append(" and r.projetoPesquisa.coordenador.id = :idCoordenador");
        	if (idArea != null) {
        		hql.append(" and r.projetoPesquisa.areaConhecimentoCnpq.grandeArea.id = :idArea");
        	}

        	Query query = getSession().createQuery(hql.toString());

        	if (ano != null)
        		query.setInteger("ano", ano);
        	if (idCoordenador != null)
        		query.setInteger("idCoordenador",idCoordenador);
        	if (idArea != null) {
        		query.setInteger("idArea",idArea);
        	}

            List lista = query.list();
            Collection<RelatorioProjeto> result = new ArrayList<RelatorioProjeto>();
        	for (int a = 0; a < lista.size(); a++) {
        		RelatorioProjeto relatorio = new RelatorioProjeto();
				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);

				relatorio.setId((Integer) colunas[col++]);
				relatorio.setDataEnvio((Date) colunas[col++]);
				relatorio.setAvaliacao((Integer) colunas[col++]);

				ProjetoPesquisa projeto = new ProjetoPesquisa();
				projeto.setTitulo((String) colunas[col++]);
				projeto.setCodigo((CodigoProjetoPesquisa) colunas[col++]);

				Servidor servidor = new Servidor();
				servidor.setSiape( (Integer) colunas[col++]);
				servidor.setPessoa(new Pessoa(0, (String) colunas[col++]));

				projeto.setCoordenador(servidor);
				relatorio.setProjetoPesquisa(projeto);
				result.add(relatorio);
        	}

        	return result;


        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

}

