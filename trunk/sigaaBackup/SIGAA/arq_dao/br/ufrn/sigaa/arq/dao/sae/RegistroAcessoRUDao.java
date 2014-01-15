/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '13/02/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.sae;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.restaurante.dominio.RegistroAcessoRU;
import br.ufrn.sigaa.assistencia.restaurante.dominio.TipoLiberacaoAcessoRU;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO usado para se registrar as informações que são 
 * informadas ao se liberar a catraca do RU.
 * 
 * @author agostinho campos
 *
 */
public class RegistroAcessoRUDao extends GenericSigaaDAO {

	public RegistroAcessoRUDao() {
		super();
	}


	/**
	 * Registra as informações passadas pelo client desktop, 
	 * para se ter controle de quem passa pela catraca.
	 */
	public void registrarAcessoRU(int tipoLiberacao, Date dataHora, int idUsuario, Long matricula, int tipoBolsa, String outraJustificativa, char turno) {
		
		String sql = "INSERT INTO sae.registro_acesso_ru (id_registro_acesso_ru, id_tipo_liberacao, data_hora, " +
				"id_usuario, matricula_discente, id_tipo_bolsa, outra_justificativa, turno, data_acesso_ru) VALUES ((select nextval('hibernate_sequence')), ?, ?, ?, ?, ?, ?, ?, ?)";
		
		update(sql, new Object[] { tipoLiberacao, dataHora, idUsuario, matricula, tipoBolsa, outraJustificativa, String.valueOf(turno), new Date() } );
	}

	/**
	 * Retorna todos os tipos de liberação que estão disponíveis que a catraca pode fazer
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<TipoLiberacaoAcessoRU> getAllTipoLiberacaoCatraca() {
		String sql = "select * from sae.tipo_liberacao_catraca";
	
		return getJdbcTemplate().query(sql, new RowMapper() {

			public TipoLiberacaoAcessoRU mapRow(ResultSet rs, int arg1) throws SQLException {
				TipoLiberacaoAcessoRU tipoLiberacao = new TipoLiberacaoAcessoRU();
					tipoLiberacao.setId( rs.getInt("id_tipo_liberacao") );
					tipoLiberacao.setDescricao( rs.getString("tipo_liberacao"));
					return tipoLiberacao;
			}
		});
	}
	
	/**
	 * Faz uma busca para localizar os acessos que foram feitos no RU através da catraca. 
	 * @param tipoBolsa
	 * @param tipoLiberacao
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RegistroAcessoRU> relatorioAcessoRUCatraca(TipoBolsaAuxilio tipoBolsa, TipoLiberacaoAcessoRU tipoLiberacao, Date dataInicial, Date dataFinal) {
		StringBuilder sql = new StringBuilder();
		
			sql.append(
					"select acesso_ru.data_hora, acesso_ru.matricula_discente, acesso_ru.outra_justificativa, pessoa.nome, " +
					 "tipo_liberacao.id_tipo_liberacao, tipo_liberacao.tipo_liberacao, usuario.login, tipoBolsa.denominacao " + 
					 "from sae.registro_acesso_ru acesso_ru " +
					 "left join sae.tipo_liberacao_catraca tipo_liberacao on tipo_liberacao.id_tipo_liberacao = acesso_ru.id_tipo_liberacao " +
				 	 "left join comum.usuario usuario on usuario.id_usuario = acesso_ru.id_usuario " +
					 "left join discente discente on discente.matricula = acesso_ru.matricula_discente " +
					 "left join comum.pessoa pessoa on pessoa.id_pessoa = discente.id_pessoa " +
					 "left join sae.tipo_bolsa_auxilio tipoBolsa on tipoBolsa.id_tipo_bolsa_auxilio = acesso_ru.id_tipo_bolsa ");
			
		if (tipoBolsa.getId() != 0 ) {
			sql.append("where tipo_liberacao.id_tipo_liberacao = " + tipoLiberacao.getId() + " and acesso_ru.id_tipo_bolsa = " + tipoBolsa.getId() 
							+ " and acesso_ru.data_acesso_ru  between " + "'" + CalendarUtils.format(dataInicial, "yyyy-MM-dd") + "'" + " and " + "'" + CalendarUtils.format(dataFinal, "yyyy-MM-dd") + "'");		
			
			sql.append(" order by acesso_ru.data_hora, pessoa.nome, acesso_ru.matricula_discente, tipo_liberacao.tipo_liberacao, " +
					"tipoBolsa.denominacao, acesso_ru.outra_justificativa, usuario.login DESC");
		}
		
		if (tipoBolsa.getId() == 0) {
			sql.append("where tipo_liberacao.id_tipo_liberacao = " + tipoLiberacao.getId() + " and acesso_ru.data_acesso_ru  between " + "'" + CalendarUtils.format(dataInicial, "yyyy-MM-dd") + "'" + " and  " + "'" + CalendarUtils.format(dataFinal, "yyyy-MM-dd") + "'");
			sql.append(" order by acesso_ru.data_hora, pessoa.nome, acesso_ru.matricula_discente, tipo_liberacao.tipo_liberacao,  tipoBolsa.denominacao, acesso_ru.outra_justificativa, usuario.login DESC");
		}
		
		return getJdbcTemplate().query(sql.toString(), new RowMapper() {
			public Object mapRow(ResultSet rs, int row) throws SQLException {
				
				Pessoa pessoa = new Pessoa();
					pessoa.setNome( rs.getString("nome") );
					
				Discente discente = new Discente();
					discente.setMatricula( rs.getLong("matricula_discente") );
					discente.setPessoa(pessoa);
				
				Usuario usuario = new Usuario();
					usuario.setLogin( rs.getString("login") );
					usuario.setDiscente(discente);
				
				RegistroAcessoRU registroAcessoRU = new RegistroAcessoRU();
					registroAcessoRU.setDiscente(discente);
					registroAcessoRU.setDataHora( rs.getTimestamp("data_hora"));
					registroAcessoRU.setOutraJustificativa(rs.getString("outra_justificativa"));
					
				TipoBolsaAuxilio tipoBolsaAuxilio = new TipoBolsaAuxilio();
					tipoBolsaAuxilio.setDenominacao(rs.getString("denominacao"));
					registroAcessoRU.setTipoBolsa(tipoBolsaAuxilio);

				TipoLiberacaoAcessoRU tipoLiberacao = new TipoLiberacaoAcessoRU();
					tipoLiberacao.setDescricao( rs.getString("tipo_liberacao"));
					tipoLiberacao.setId( rs.getInt("id_tipo_liberacao"));
					
					registroAcessoRU.setTipoLiberacao(tipoLiberacao);
				
					registroAcessoRU.setUsuario(usuario);
					
				return registroAcessoRU;
			}
		});
	}

	/**
	 * Verifica através da matrícula do discente se o mesmo já acessou o RU para aquele horário/refeição.
	 * @param matricula
	 * @param horarioRefeicao
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Object> verificarPermissaoAcessoDiarioRU(Long matricula, char horarioRefeicao) {
		String sql = "select turno from sae.registro_acesso_ru where matricula_discente = ? and turno = ? and data_acesso_ru = ?";
		 
		return getJdbcTemplate().query(sql, new Object[]{ matricula, (String.valueOf(horarioRefeicao)), new Date()}, new RowMapper() {
			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				return rs.getString("turno");
			}
		});
	}	
}
