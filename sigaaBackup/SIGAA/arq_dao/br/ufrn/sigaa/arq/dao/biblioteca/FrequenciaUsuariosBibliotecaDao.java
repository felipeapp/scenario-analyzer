/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 11/11/2008
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.RegistroFrequenciaUsuariosBiblioteca;
import br.ufrn.sigaa.biblioteca.jsf.RegistroFrequenciaUsuariosBibliotecaMBean;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 * DAO responsável por consultar as frequências dos usuários
 * da biblioteca. Essa frequência é registrada por um funcionário
 * da biblioteca diariamente.
 * 
 * @author agostinho
 *
 */
public class FrequenciaUsuariosBibliotecaDao extends GenericSigaaDAO {

	
	
	/**
	 * Retorna uma lista usando como parâmetro mês, ano e biblioteca
	 */
	public List<RegistroFrequenciaUsuariosBiblioteca> findFrequenciasMovimentacaoUsuarioAtivasByAnoMesEBiblioteca (int mesInicio, int ano, Integer idBiblioteca) throws DAOException, ParseException {
		
		String hql = "select freqUsers from RegistroFrequenciaUsuariosBiblioteca freqUsers " +
					 "WHERE freqUsers.ano = :ano AND freqUsers.mes = :mesInicio AND ativo = trueValue() ";
		
		if(idBiblioteca != null && idBiblioteca > 0){
			hql += " AND freqUsers.biblioteca.id = :idBiblioteca";
		}
		
		hql += " order by freqUsers.dataCadastro, freqUsers.turno ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("ano", ano);
		q.setInteger("mesInicio", mesInicio);
		
		if(idBiblioteca != null && idBiblioteca > 0){
			q.setInteger("idBiblioteca", idBiblioteca);
		}
			
		@SuppressWarnings("unchecked")
		List<RegistroFrequenciaUsuariosBiblioteca> lista = q.list();
		return lista;
	}
	
	/**
	 * Retorna uma lista usando como parâmetro turno, data e biblioteca
	 */
	public List<RegistroFrequenciaUsuariosBiblioteca> findFrequenciasMovimentacaoUsuarioAtivasByTurnoDataEBiblioteca (int turnoCadastro, Date data, Integer idBiblioteca) throws DAOException, ParseException {
		
		String hql = "SELECT freqUsers from RegistroFrequenciaUsuariosBiblioteca freqUsers " +
					 "WHERE turno = :idTurno AND freqUsers.dataCadastro = :dataCadastro AND ativo = trueValue() ";
		
		if(idBiblioteca != null && idBiblioteca > 0){
			hql += " AND freqUsers.biblioteca.id = :idBiblioteca";
		}
		
		hql += " ORDER BY freqUsers.dataCadastro, freqUsers.turno ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idTurno", turnoCadastro);
		q.setDate("dataCadastro", data);
		
		if(idBiblioteca != null && idBiblioteca > 0){
			q.setInteger("idBiblioteca", idBiblioteca);
		}
			
		@SuppressWarnings("unchecked")
		List<RegistroFrequenciaUsuariosBiblioteca> lista = q.list();
		return lista;
	}

	
	
	
	/**
	 * Retorna uma lista com o total de frequência por mês/turno.
	 */
	public ArrayList<RegistroFrequenciaUsuariosBiblioteca> findAllFrequenciaAnual(int ano, final int turno, int idBiblioteca) throws HibernateException, DAOException {
		String sql = "";
		
		sql +=
				"SELECT " +
				"	sum(quant_acesso) AS quant, " +
				"	freqUsers.mes AS mes, " +
				"	freqUsers.turno " +
				"FROM biblioteca.registro_frequencia_usuarios_bib freqUsers " +
				"WHERE " +
				"	ano = " + ano + " " +
				"	AND freqUsers.turno = " + turno + " " +
				"	AND freqUsers.ativo = trueValue() "; 
		
		if (
				idBiblioteca != RegistroFrequenciaUsuariosBibliotecaMBean.TODAS_BIBLIOTECAS
				&& idBiblioteca != RegistroFrequenciaUsuariosBibliotecaMBean.TODAS_BIBLIOTECAS_SETORIAIS)
			sql += " AND id_biblioteca = " + idBiblioteca + " ";
		
		if (idBiblioteca == RegistroFrequenciaUsuariosBibliotecaMBean.TODAS_BIBLIOTECAS_SETORIAIS)
			sql += " AND id_biblioteca <> " + BibliotecaUtil.getIdBibliotecaCentral() + " ";
		
		sql += "GROUP BY date_part('month', data_cadastro), mes, freqUsers.turno ";

		@SuppressWarnings("unchecked")
		ArrayList<RegistroFrequenciaUsuariosBiblioteca> lista = (ArrayList<RegistroFrequenciaUsuariosBiblioteca>) getJdbcTemplate().query(sql.toString(), new RowMapper() {
			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				RegistroFrequenciaUsuariosBiblioteca reg = new RegistroFrequenciaUsuariosBiblioteca();
				
				if (rs.getInt("mes") == 1) { 
					reg.setTotalAcessoJaneiro(rs.getInt("quant"));
					reg.setMes(1);
					reg.setTurno(turno);
				}
				if (rs.getInt("mes") == 2) { 
					reg.setTotalAcessoFevereiro(rs.getInt("quant"));
					reg.setMes(2);
					reg.setTurno(turno);
				}
				if (rs.getInt("mes") == 3) { 
					reg.setTotalAcessoMarco(rs.getInt("quant"));
					reg.setMes(3);
					reg.setTurno(turno);
				}
				if (rs.getInt("mes") == 4) { 
					reg.setTotalAcessoAbril(rs.getInt("quant"));
					reg.setMes(4);
					reg.setTurno(turno);
				}
				if (rs.getInt("mes") == 5) { 
					reg.setTotalAcessoMaio(rs.getInt("quant"));
					reg.setMes(5);
					reg.setTurno(turno);
				}
				if (rs.getInt("mes") == 6) { 
					reg.setTotalAcessoJunho(rs.getInt("quant"));
					reg.setMes(6);
					reg.setTurno(turno);
				}
				if (rs.getInt("mes") == 7) { 
					reg.setTotalAcessoJulho(rs.getInt("quant"));
					reg.setMes(7);
					reg.setTurno(turno);
				}
				if (rs.getInt("mes") == 8) { 
					reg.setTotalAcessoAgosto(rs.getInt("quant"));
					reg.setMes(8);
					reg.setTurno(turno);
				}
				if (rs.getInt("mes") == 9) { 
					reg.setTotalAcessoSetembro(rs.getInt("quant"));
					reg.setMes(9);
					reg.setTurno(turno);
				}
				if (rs.getInt("mes") == 10) { 
					reg.setTotalAcessoOutubro(rs.getInt("quant"));
					reg.setMes(10);
					reg.setTurno(turno);
				}
				if (rs.getInt("mes") == 11) { 
					reg.setTotalAcessoNovembro(rs.getInt("quant"));
					reg.setMes(11);
					reg.setTurno(turno);
				}
				if (rs.getInt("mes") == 12) { 
					reg.setTotalAcessoDezembro(rs.getInt("quant"));
					reg.setMes(12);
					reg.setTurno(turno);
				}
				
				return reg;
			}
		});
		
		return lista;
		
	}
	
	/**
	 * Retorna a quantidade de usuários que utilizaram as bibliotecas passadas em um
	 * dado período.
	 */
	public int[] findFrequenciaPorPeriodo( Collection<Integer> bibliotecas, Date inicio, Date fim )
			throws DAOException {
		
		String periodo = "'" +
				CalendarUtils.format(inicio, "yyyy-MM-dd 00:00:00") +
				"' AND '" +
				CalendarUtils.format(fim, "yyyy-MM-dd 23:59:59") + "'";
		
		String sql =
			"SELECT " +
			"	COALESCE(sum(quant_acesso), 0) AS quant " + // sum retornar null se não houver nada para somar
			"FROM biblioteca.registro_frequencia_usuarios_bib " + 
			"WHERE " +
			"	turno = :turno " +
			"	AND data_cadastro BETWEEN "+ periodo + " " +
			( bibliotecas != null && ! bibliotecas.isEmpty()  ? "	AND id_biblioteca IN ("+ StringUtils.join(bibliotecas, ',') +") " : "" );
		
		Query query = getSession().createSQLQuery(sql);
		
		int[] res = new int[3];
		
		query.setInteger("turno", RegistroFrequenciaUsuariosBiblioteca.TURNO_MATUTINO);
		res[0] = ((BigInteger) query.uniqueResult()).intValue();
		
		query.setInteger("turno", RegistroFrequenciaUsuariosBiblioteca.TURNO_VESPERTINO);
		res[1] = ((BigInteger) query.uniqueResult()).intValue();
		
		query.setInteger("turno", RegistroFrequenciaUsuariosBiblioteca.TURNO_NOTURNO);
		res[2] = ((BigInteger) query.uniqueResult()).intValue();
		
		return res;
	}
}
