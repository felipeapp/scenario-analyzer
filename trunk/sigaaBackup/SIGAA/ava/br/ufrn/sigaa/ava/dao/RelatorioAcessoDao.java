/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 *
 * Criado em 18/01/2008
 * Autor: Agostinho
 * 
 */
package br.ufrn.sigaa.ava.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Arquivo;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.ArquivoUsuario;
import br.ufrn.sigaa.ava.dominio.ConteudoTurma;
import br.ufrn.sigaa.ava.dominio.LogLeituraSigaaTurmaVirtual;
import br.ufrn.sigaa.ava.dominio.LogLeituraSigaaTurmaVirtualDetalhes;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * DAO responsável pelas consultas relacionadas ao Relatório de Acesso da Turma Virtual
 *
 * @author Agostinho
 *
 */
public class RelatorioAcessoDao extends GenericSigaaDAO {

	public static final int ENTROU_TURMA_VIRTUAL = 1;
	public static final int ACESSO_ARQUIVOS = 2;
	public static final int CONTEUDO_TURMA = 3;

	/**
	 * Exibe todos os alunos que acessaram determinado arquivo de
	 * determinada Turma Virtual
	 * 
	 * @param idTurmaVirtual
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<LogLeituraSigaaTurmaVirtual> findArquivosAcessadosByDiscentes(int idTurmaVirtual, int idArquivo) {
      String sql = "select * from log_db_leitura where id_turma_virtual = ? and id_elemento = ? order by id_usuario";
        
      DataSource ds = Database.getInstance().getLogDs();
      return (List<LogLeituraSigaaTurmaVirtual>) getJdbcTemplate( ds ).query(sql, new Object[] {idTurmaVirtual, idArquivo}, new ResultSetExtractor() {
	
    	  public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
    		  
    		  List<LogLeituraSigaaTurmaVirtual> resultado = null;
    		  LogLeituraSigaaTurmaVirtual log = new LogLeituraSigaaTurmaVirtual();
	            
    		  log.setIdUsuario(0);
    		  while (rs.next()) {
	
    			  int idAtual = rs.getInt("id_usuario");

    			  if (log.getIdUsuario() != idAtual) {
    				  log = new LogLeituraSigaaTurmaVirtual();
    				  log.setIdUsuario(rs.getInt("id_usuario"));

    				  if (resultado == null)
    					  resultado = new ArrayList();


    				  resultado.add(log);
    			  }

    			  LogLeituraSigaaTurmaVirtualDetalhes detalhe = new LogLeituraSigaaTurmaVirtualDetalhes();
    			  detalhe.setArquivoBaixado( rs.getInt("id_elemento") );
    			  detalhe.setData(rs.getTimestamp("data"));
    			  log.getDetalhes().add(detalhe);
    		  }
    		  return resultado;
    	  }
      });
    }

	/**
	 * Localiza todos os acessos realizados a Turma Virtual pelos discentes
	 * e agrupa os acessos para exibir em relatório.
	 * 
	 * @param idTurmaVirtual
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<LogLeituraSigaaTurmaVirtual> relatorioAcessoDiscentesTurmaVirtual(int idUsuario, int idTurma) {
      String sql = "select * from log_db_leitura where id_usuario = ? and id_turma_virtual = ? order by data desc";
        
      DataSource ds = Database.getInstance().getLogDs();

      return (List<LogLeituraSigaaTurmaVirtual>) getJdbcTemplate( ds ).query(sql, new Object[] {idUsuario, idTurma}, new ResultSetExtractor() {
    	  public Object extractData(ResultSet rs) throws SQLException, DataAccessException {

    		  List<LogLeituraSigaaTurmaVirtual> resultado = null;
    		  LogLeituraSigaaTurmaVirtual log = new LogLeituraSigaaTurmaVirtual();

    		  log.setIdUsuario(0);
    		  while (rs.next()) {

    			  int idAtual = rs.getInt("id_usuario");

    			  if (log.getIdUsuario() != idAtual) {
    				  log = new LogLeituraSigaaTurmaVirtual();
    				  log.setIdUsuario(rs.getInt("id_usuario"));

    				  if (resultado == null)
    					  resultado = new ArrayList();

    				  if (!resultado.contains(log))
    					  resultado.add(log);
    			  }

    			  LogLeituraSigaaTurmaVirtualDetalhes detalhe = new LogLeituraSigaaTurmaVirtualDetalhes();
    			  detalhe.setArquivoBaixado( rs.getInt("id_elemento") );
    			  detalhe.setData(rs.getTimestamp("data"));
    			  detalhe.setTipoDeAcesso(rs.getString("tabela"));

    			  log.getDetalhes().add(detalhe);
    		  }
    		  return resultado;
    	  }
      });
    }

    /**
     * Retorna uma lista exibindo a quantidade de acessos realizados para cada elemento da 
     * Turma Virtual (como: quantas vezes um discente acessou a turma ou determinado arquivo). 
     *  
     * @param idTurmaVirtual
     * @param sql
     * @param tipoVisualizacao
     * @return
     */
	@SuppressWarnings("unchecked")
	public List<LogLeituraSigaaTurmaVirtual> relatorioSinteticoAcessoTurmaVirtual(int idTurmaVirtual, int tipoRelatorio, final String tipoVisualizacao) {
		
		String sql = "";
		if (tipoRelatorio == ENTROU_TURMA_VIRTUAL)
			sql = "select count(id_elemento) as total, id_usuario from log_db_leitura where (tabela = 'Entrou Turma Virtual' or tabela = '" +Turma.class.getName()+ "') and id_turma_virtual = ? group by id_usuario";
		if (tipoRelatorio == ACESSO_ARQUIVOS)
			sql = "select count(id_elemento) as total, id_usuario from log_db_leitura where (tabela = 'Arquivo' or tabela = '" +Arquivo.class.getName()+ "') and id_turma_virtual = ? group by id_usuario";
		if (tipoRelatorio == CONTEUDO_TURMA)
			sql = "select count(id_elemento) as total, id_usuario from log_db_leitura where (tabela = 'ConteudoTurma' or tabela = '" +ConteudoTurma.class.getName()+ "') and id_turma_virtual = ? group by id_usuario";
		
		DataSource ds = Database.getInstance().getLogDs();

		return (List<LogLeituraSigaaTurmaVirtual>) getJdbcTemplate( ds ).query(sql, new Object[] {idTurmaVirtual}, new ResultSetExtractor() {
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {

				List<LogLeituraSigaaTurmaVirtual> resultado = null;
				LogLeituraSigaaTurmaVirtual log = new LogLeituraSigaaTurmaVirtual();

				log.setIdUsuario(0);
				while (rs.next()) {

					int idAtual = rs.getInt("id_usuario");

					if (log.getIdUsuario() != idAtual) {
						log = new LogLeituraSigaaTurmaVirtual();
						log.setIdUsuario(rs.getInt("id_usuario"));

						if (tipoVisualizacao.equals("sqlEntrouTurmaVirtual"))
							log.setQntEntrouTurmaVirtual( rs.getInt("total") );
						if (tipoVisualizacao.equals("sqlArquivo"))
							log.setQntArquivo( rs.getInt("total") );
						if (tipoVisualizacao.equals("sqlConteudoTurma"))
							log.setQntConteudoTurma(( rs.getInt("total")));

						if (resultado == null)
							resultado = new ArrayList();

						if (!resultado.contains(log))
							resultado.add(log);
					}
				}
				return resultado;
			}
		});
	}

	/**
	 * Esse método retorna os usuários de acordo com os IDs dos usuários que foi registrado
	 * quando os discentes acessaram a Turma Virtual.
	 * @param idsUsuarios
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Usuario> findUsuariosAcessaramTurmaVirtual(int[] idsUsuarios) throws HibernateException, DAOException {
		String hql = "select u from br.ufrn.sigaa.dominio.Usuario u where u.id in " + gerarStringIn(idsUsuarios);
		
		@SuppressWarnings("unchecked")
		List<Usuario> lista = getSession().createQuery(hql).list();
		return lista;
	}

	/**
	 * Localiza um arquivo de acordo com o ID do mesmo. Os IDs serão dos arquivos baixados/visualizados na Turma Virtual são
	 * registrados e depois podem ser usados por esse método.   
	 * 
	 * @param idsArquivosBaixados
	 * @param usersAcessaramTurmaVirtual
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<LogLeituraSigaaTurmaVirtual> findArquivoByDiscente(int[] idsArquivosBaixados, List<LogLeituraSigaaTurmaVirtual> usersAcessaramTurmaVirtual) throws HibernateException, DAOException {
		if (idsArquivosBaixados.length > 0) {
				String hql = "select arq from br.ufrn.sigaa.ava.dominio.ArquivoUsuario arq where arq.idArquivo in " + gerarStringIn(idsArquivosBaixados);
				List<ArquivoUsuario> arquivos = getSession().createQuery(hql).list();
				
				for (ArquivoUsuario file : arquivos)
					for (LogLeituraSigaaTurmaVirtual a : usersAcessaramTurmaVirtual)
						for (LogLeituraSigaaTurmaVirtualDetalhes b : a.getDetalhes())
							if (file.getIdArquivo() == b.getArquivoBaixado())
								b.setDescricaoArquivoBaixado(file.getNome());
		}
		return usersAcessaramTurmaVirtual;
	}
	
	/**
	 * Responsável pela consulta de todos os acessos a turma virtual no período e ano passado como parâmetro.
	 * 
	 * @param idTurma
	 * @param periodo
	 * @param ano
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LogLeituraSigaaTurmaVirtual> graficoRelatorioAcessoTurmaVirtual(int idTurma, int periodo, int ano, int[] dias, int mes) {
		
		
		StringBuilder sql = new StringBuilder(" select date_part('day', data) as dia, date_part('Month', data) as mes, " +
				" count(id_elemento) as total, date_part('year', data) as ano "+
		" from log_db_leitura where (tabela = 'Entrou Turma Virtual' or tabela = '" +Turma.class.getName()+ "') ");
		if ((periodo == 1 || periodo == 3) && mes == 0) 
			sql.append(" and date_part('Month', data) >= 1 and date_part('Month', data) <= 6 ");
		if ((periodo == 2 || periodo == 4) && mes == 0)
			sql.append(" and date_part('Month', data) >= 7 and date_part('Month', data) <= 12 ");
		if (mes != 0) {
			sql.append(" and date_part('Month', data) = "+ mes);
		}
		if (dias != null) {
			sql.append(" and date_part('day', data) in " + UFRNUtils.gerarStringIn(dias));
		}

		sql.append(" and id_turma_virtual  = ? and date_part('year', data) = ? group by date_part('Month', data), " +
				"date_part('day', data), id_turma_virtual, date_part('year', data) " +
		" order by date_part('Month', data), date_part('day', data)");

		DataSource ds = Database.getInstance().getLogDs();

		return (List<LogLeituraSigaaTurmaVirtual>) getJdbcTemplate( ds ).query(sql.toString(), new Object[] {idTurma, ano}, new ResultSetExtractor() {
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {

				List<LogLeituraSigaaTurmaVirtual> resultado = null;
				LogLeituraSigaaTurmaVirtual log = new LogLeituraSigaaTurmaVirtual();

				while (rs.next()) {

					log = new LogLeituraSigaaTurmaVirtual();
					log.setDia(rs.getInt("dia"));
					log.setQntEntrouTurmaVirtual( rs.getInt("total") );
					log.setMes(rs.getInt("mes"));

					Calendar c = Calendar.getInstance();
					c.set(rs.getInt("ano"), rs.getInt("mes"), rs.getInt("dia"));
					log.setSemana(c.get(Calendar.DAY_OF_WEEK_IN_MONTH));
					log.setNomeMes(CalendarUtils.getMesAbreviado(rs.getInt("mes")));
					log.setNomeSemana(log.getSemana() + "° Sem. " + log.getNomeMes());

					if (resultado == null)
						resultado = new ArrayList();

					resultado.add(log);

				}

				return resultado;
			}
		});
	}
	
}