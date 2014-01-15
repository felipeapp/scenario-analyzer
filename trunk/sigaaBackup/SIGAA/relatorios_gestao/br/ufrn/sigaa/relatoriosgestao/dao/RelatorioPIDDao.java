/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/03/2011
 *
 */
package br.ufrn.sigaa.relatoriosgestao.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.rh.dominio.Cargo;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.rh.dominio.Situacao;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.pid.dominio.PlanoIndividualDocente;
import br.ufrn.sigaa.relatoriosgestao.dominio.RelatorioPIDPorSemestre;

/**
 * Classe responsável por realizar as buscas para ser utilizadas nos relatórios de PID. 
 * 
 * @author Arlindo
 *
 */
public class RelatorioPIDDao extends GenericSigaaDAO  {
	
	/**
	 * Retorna os totais de PID por Departamento no ano e período informado
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<RelatorioPIDPorSemestre> findTotaisPIDByAnoPeriodo(Integer ano, Integer periodo) throws HibernateException, DAOException{
		
		StringBuffer hql = new StringBuffer();
		
		hql.append(" select u.id_gestora, g.sigla, u.id_unidade, u.nome as unidade, ");
		hql.append(" 	   cast(count(s.id_servidor) as integer), ");
		hql.append("       cast(sum(case when coalesce(pid.status,0) = "+PlanoIndividualDocente.CADASTRADO+" then 1 " +
				                      "  when coalesce(pid.status,0) = "+PlanoIndividualDocente.HOMOLOGADO+" then 1 else 0 end) as integer) as cadastrados,");
		hql.append("       cast(sum(case when coalesce(pid.status,0) = "+PlanoIndividualDocente.HOMOLOGADO+" then 1 else 0 end) as integer) as homologados");
		hql.append(" from rh.servidor s");
		hql.append(" inner join comum.unidade u on (u.id_unidade = s.id_unidade)");
		hql.append(" inner join comum.unidade g on (g.id_unidade = u.id_gestora)");
		hql.append(" left join ( select * from pid.plano_individual_docente pid ) pid"); 
		hql.append("	on (pid.id_servidor = s.id_servidor) and pid.ano = :ano and pid.periodo = :periodo");
		hql.append(" where s.id_ativo = "+Ativo.SERVIDOR_ATIVO);
		hql.append(" and s.id_categoria = "+Categoria.DOCENTE);
		hql.append(" and s.id_cargo in "+ UFRNUtils.gerarStringIn(new int[]{Cargo.PROFESSOR_DO_MAGISTERIO_SUPERIOR, Cargo.DOCENTE_SUPERIOR_EFETIVO}) );
		hql.append(" and s.id_situacao = "+Situacao.ATIVO_PERMANENTE);
		hql.append(" group by 1,2,3,4");
		hql.append(" order by g.sigla, u.nome");		

    	Query q = getSession().createSQLQuery(hql.toString());
    	
   		q.setInteger("ano", ano);    
   		q.setInteger("periodo", periodo);

    	@SuppressWarnings("unchecked")
		List<Object> lista = q.list();

    	List<RelatorioPIDPorSemestre> resultado = new ArrayList<RelatorioPIDPorSemestre>();
    	for (int a = 0; a < lista.size(); a++) {
    		RelatorioPIDPorSemestre relatorio = new RelatorioPIDPorSemestre();
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			
			Unidade gestora = new Unidade();
			gestora.setId( (Integer) colunas[col++] );
			gestora.setSigla( (String) colunas[col++] );			

			Unidade unidade = new Unidade();
			unidade.setId( (Integer) colunas[col++] );
			unidade.setNome( (String) colunas[col++] );
			unidade.setGestora(gestora);
			
			relatorio.setUnidade(unidade);
			
			relatorio.setTotalDocentesAtivos( (Integer) colunas[col++] );
			relatorio.setTotalCadastrados( (Integer) colunas[col++]  );
			relatorio.setTotalHomologados( (Integer) colunas[col++] );

			resultado.add( relatorio );
    	}

        return resultado;
	}
	
	/**
	 * Retorna os PIDs cadastrados conforme os parâmetros informados
	 * Utilizado no relatório de detalhamento 
	 * @param unidade
	 * @param ano
	 * @param periodo
	 * @param status
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<PlanoIndividualDocente> findDocentesAtivosByUnidade(Unidade unidade, Unidade gestora, Integer ano, Integer periodo) throws HibernateException, DAOException{

		StringBuilder projecao =  new StringBuilder(" servidor.id, servidor.pessoa.nome, servidor.unidade.id, " +
				" servidor.unidade.nome, servidor.unidade.sigla, servidor.unidade.gestora.id, servidor.unidade.gestora.nome, ");
		
		projecao.append(" (SELECT pi.status  ");
		projecao.append("  FROM PlanoIndividualDocente pi ");
		projecao.append("  INNER JOIN pi.servidor ");
		projecao.append("  WHERE ano =  "+ano);
		projecao.append("   AND periodo = "+periodo);
		projecao.append("   AND pi.servidor.id = servidor.id ");
		projecao.append(" ) AS status_pid  ");
		
		StringBuilder hql = new StringBuilder("select " + projecao.toString() +" from Servidor servidor ");
		
		hql.append(" inner join servidor.pessoa pessoa ");
		hql.append(" inner join servidor.unidade unidade ");
		
		hql.append("      where servidor.ativo = "+Ativo.SERVIDOR_ATIVO);
		hql.append("      and servidor.categoria = "+Categoria.DOCENTE);
		hql.append("      and servidor.cargo.id in "+ UFRNUtils.gerarStringIn(new int[]{Cargo.PROFESSOR_DO_MAGISTERIO_SUPERIOR, Cargo.DOCENTE_SUPERIOR_EFETIVO}) );
		hql.append("      and servidor.situacaoServidor.id = "+Situacao.ATIVO_PERMANENTE);		
		
		if (unidade != null) { hql.append(" and servidor.unidade.id = " + unidade.getId()); }
		if (gestora != null) { hql.append(" and servidor.unidade.gestora.id = " + gestora.getId()); }
	
		hql.append(" order by servidor.unidade.nome, servidor.pessoa.nome ");

    	Query q = getSession().createQuery(hql.toString());
    	
    	@SuppressWarnings("unchecked")
		List<Object> lista = q.list();

    	List<PlanoIndividualDocente> resultado = new ArrayList<PlanoIndividualDocente>();
    	for (int a = 0; a < lista.size(); a++) {
    		PlanoIndividualDocente relatorio = new PlanoIndividualDocente();
    		relatorio.setId(1);
    		
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			
			Servidor s = new Servidor();
			s.setId( (Integer) colunas[col++] );
			s.setUnidade(new Unidade());
			
			Pessoa p = new Pessoa();
			p.setNome((String) colunas[col++]);
			
			s.getUnidade().setId((Integer) colunas[col++]);
			s.getUnidade().setNome((String) colunas[col++]);
			s.getUnidade().setSigla((String) colunas[col++]);
			
			s.getUnidade().setGestora(new Unidade((Integer) colunas[col++]));
			s.getUnidade().getGestora().setNome((String) colunas[col++]);						
			
			s.setPessoa(p);
			
			relatorio.setServidor(s);
			
			Integer status = (Integer) colunas[col++];
			if (status != null && status > 0)
				relatorio.setStatus(status);
			
			resultado.add(relatorio);
    	}

		return resultado;
		
	}
	
	
	/**
	 * Retorna os PIDs cadastrados conforme os parâmetros informados
	 * Utilizado no relatório de detalhamento 
	 * @param unidade
	 * @param ano
	 * @param periodo
	 * @param status
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<PlanoIndividualDocente> findPIDBySemestreAndDepartamento(Unidade unidade, Unidade gestora, Integer ano, Integer periodo, Integer... status) throws HibernateException, DAOException{

		String projecao = " pid.id, pid.ano, pid.periodo, pid.status, pid.servidor.pessoa.nome, pid.servidor.unidade.id, " +
				" pid.servidor.unidade.nome, pid.servidor.unidade.sigla, pid.servidor.unidade.gestora.id ";
		
		StringBuilder hql = new StringBuilder("select " + projecao +" from PlanoIndividualDocente pid");
		
		hql.append(" inner join pid.servidor servidor ");
		hql.append(" inner join pid.servidor.pessoa pessoa ");
		hql.append(" inner join servidor.unidade unidade ");
		hql.append(" inner join pid.servidor.unidade.gestora gestora ");
		
		hql.append(" where servidor.ativo = "+Ativo.SERVIDOR_ATIVO);
		hql.append(" and servidor.categoria = "+Categoria.DOCENTE);
		hql.append(" and servidor.cargo.id in "+ UFRNUtils.gerarStringIn(new int[]{Cargo.PROFESSOR_DO_MAGISTERIO_SUPERIOR, Cargo.DOCENTE_SUPERIOR_EFETIVO}) );
		hql.append(" and servidor.situacaoServidor.id = "+Situacao.ATIVO_PERMANENTE);		
		
		if (status != null){ hql.append(" and status in " + UFRNUtils.gerarStringIn( status )); }		
		if (unidade != null) { hql.append(" and pid.servidor.unidade.id = " + unidade.getId()); }		
		if (gestora != null) { hql.append(" and gestora.id = " + gestora.getId()); }
		if (ano != null) { hql.append(" and pid.ano = " + ano); }
		if (periodo != null) { hql.append(" and pid.periodo = " + periodo); }
	
		hql.append(" order by ano, periodo, pid.servidor.unidade.sigla, pid.servidor.pessoa.nome ");
		
  	Query q = getSession().createQuery(hql.toString());
    	
    	@SuppressWarnings("unchecked")
		List<Object> lista = q.list();

    	List<PlanoIndividualDocente> resultado = new ArrayList<PlanoIndividualDocente>();
    	for (int a = 0; a < lista.size(); a++) {
    		PlanoIndividualDocente relatorio = new PlanoIndividualDocente();
    		
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			
			relatorio.setId((Integer) colunas[col++]);
			relatorio.setAno((Integer) colunas[col++]);
			relatorio.setPeriodo((Integer) colunas[col++]);
			relatorio.setStatus((Integer) colunas[col++]);
			
			Pessoa p = new Pessoa();
			p.setNome((String) colunas[col++]);			
			
			Servidor s = new Servidor();
			s.setPessoa(p);
			s.setUnidade(new Unidade((Integer) colunas[col++]));
			s.getUnidade().setNome((String) colunas[col++]);
			s.getUnidade().setSigla((String) colunas[col++]);
		
			s.getUnidade().setGestora(new Unidade((Integer) colunas[col++]));				
			
			relatorio.setServidor(s);
			
			resultado.add(relatorio);
    	}				
		return resultado;
		
	}	

}
