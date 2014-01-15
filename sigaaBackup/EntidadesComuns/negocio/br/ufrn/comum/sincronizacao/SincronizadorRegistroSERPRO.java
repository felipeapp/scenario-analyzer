/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 05/12/2012
 */
package br.ufrn.comum.sincronizacao;

import java.util.Date;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.dominio.RegistroSERPROConexao;
import br.ufrn.arq.dominio.RegistroSERPROSincronizacao;

/**
 * Classe para sincronizar o cadastro de registros de conexão para os
 * bancos dos sistemas.
 *
 * @author Tiago Hiroshi
 *
 */
public class SincronizadorRegistroSERPRO {

	/** Conexão do banco com o qual se irá sincronizar os registros de conexao. */
	private static JdbcTemplate jtLog;

	public SincronizadorRegistroSERPRO() {
		jtLog = new JdbcTemplate(Database.getInstance().getLogDs());
	}
	
	/**
	 * Método para buscar o próximo id disponível para um registro de conexao.
	 * @return
	 */
	public static int getNextIdConexao() {
		return jtLog.queryForInt("select nextval('registro_serpro_conexao_seq')");
	}

	/**
	 * Método para buscar o próximo id disponível para um registro de sincronizacao.
	 * @return
	 */
	public static int getNextIdSincronizacao() {
		return jtLog.queryForInt("select nextval('registro_serpro_sincronizacao_seq')");
	}

	/**
	 * Cadastra as informações do registro de conexao.
	 * 
	 * @param usuarioSERPRO
	 * @param registroEntrada
	 */
	public RegistroSERPROConexao cadastrarRegistroSERPROConexao(String usuarioSERPRO, RegistroEntrada registroEntrada) {
		int id = getNextIdConexao();

		String sql = "insert into public.registro_serpro_conexao (id_registro_serpro_conexao, data_acesso, usuario, id_registro_entrada) values (?,?,?,?)";

		jtLog.update(sql, new Object[] { id, new Date(), usuarioSERPRO, registroEntrada.getId()});
		
		return new RegistroSERPROConexao(id);
	}
	
	/**
	 * Cadastra as informações do registro de sincronização.
	 * 
	 * @param usuarioSERPRO
	 * @param registroEntrada
	 */
	public RegistroSERPROSincronizacao cadastrarRegistroSERPROSincronizacao(RegistroSERPROSincronizacao registroSERPROSincronizacao) {
		int id = getNextIdSincronizacao();
		Date data = new Date();

		String sql = "insert into public.registro_serpro_sincronizacao (id_registro_serpro_sincronizacao, classe_entidade_sincronizada, " +
				"		id_entidade_sincronizada, excecao_falha_sincronizacao, id_tipo_registro_serpro_sincronizacao, sucesso, mensagem_erro, " +
				"		data_cadastro, id_registro_serpro_conexao) values (?, ?, ?, ?, ?, ?, ?, ?, ?)"; 

		jtLog.update(sql, new Object[] { id, registroSERPROSincronizacao.getClasseEntidadeSincronizada(), registroSERPROSincronizacao.getIdEntidadeSincronizada(),
						registroSERPROSincronizacao.getExcecaoFalhaSincronizacao(), registroSERPROSincronizacao.getTipoRegistroSERPROSincronizacao().getId(), 
						registroSERPROSincronizacao.isSucesso(), registroSERPROSincronizacao.getMensagemErro(), data, registroSERPROSincronizacao.getRegistroSERPROConexao().getId()});
		
		registroSERPROSincronizacao.setId(id);
		registroSERPROSincronizacao.setDataCadastro(data);
		
		return registroSERPROSincronizacao;
	}
	
}
