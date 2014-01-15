/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 05/12/2012
 */
package br.ufrn.arq.seguranca.log;

import java.util.Date;

import org.springframework.dao.EmptyResultDataAccessException;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.dominio.RegistroSERPROConexao;
import br.ufrn.arq.dominio.RegistroSERPROSincronizacao;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Classe utilizada para log de telas da conexão entre os sistemas 
 * integrados e os sistemas do governo.
 *
 * @author Tiago Hiroshi
 *
 */
public class LoggerSERPROConexao {
	/** Conexão do banco comum, onde serão registrados os logs da conexão. */
	private static JdbcTemplate jtLog = new JdbcTemplate(Database.getInstance().getLogDs());
	
	/**
	 * Método para buscar o último id para um log de conexão.
	 * @return
	 */
	public static int getNextId() {
		return jtLog.queryForInt("select nextval('public.registro_serpro_tela_conexao_seq')");
	}

	/**
	 * Método para buscar o último id para um log de conexão.
	 * @return
	 */
	public static int getUltimoId() {
		return jtLog.queryForInt("select last_value from public.registro_serpro_tela_conexao_seq");
	}

	/**
	 * Cadastra as informações do log de conexão.
	 * 
	 * @param usuarioSERPRO
	 * @param registroEntrada
	 */
	public static void cadastrarLogConexao(RegistroSERPROConexao registroSERPROConexao, RegistroSERPROSincronizacao registroSERPROSincronizacao, RegistroEntrada registroEntrada, String tela) {
		if(!isEqualTelaConexaoAnterior(getUltimoId(), tela)){
			String sqlSistema = "insert into public.registro_serpro_tela_conexao (id_registro_serpro_tela_conexao, data_acesso, id_registro_serpro_conexao, id_registro_serpro_sincronizacao, id_registro_entrada, tela) values (?,?,?,?,?,?)";
			try {
				Integer idConexao = (ValidatorUtil.isEmpty(registroSERPROConexao) ? null : registroSERPROConexao.getId());
				Integer idSincronizacao = (ValidatorUtil.isEmpty(registroSERPROSincronizacao) ? null : registroSERPROSincronizacao.getId());
				Integer idRegistroEntrada = (ValidatorUtil.isEmpty(registroEntrada) ? null : registroEntrada.getId());
				jtLog.update(sqlSistema, new Object[] { getNextId(), new Date(), idConexao, idSincronizacao, idRegistroEntrada, tela });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * Verifica se a tela anterior é igual.
	 * @return
	 */
	public static boolean isEqualTelaConexaoAnterior(int id, String tela) {
		String sqlBusca = "select t.tela from public.registro_serpro_tela_conexao t where id_registro_serpro_tela_conexao = " + id;

		try {
			String telaAnterior = jtLog.queryForObject(sqlBusca, String.class);
			return (ValidatorUtil.isEmpty(telaAnterior) ? false : telaAnterior.equals(tela));
		} catch(EmptyResultDataAccessException e) {
			return false;
		}
		
	}

}