/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/11/2006
 */
package br.ufrn.comum.sincronizacao;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dominio.RegistroEntrada;

/**
 * Classe para sincronizar o cadastro de registros de entrada para os
 * bancos dos sistemas e o banco de log.
 *
 * @author David Ricardo
 * @author Gleydson Lima
 *
 */
public class SincronizadorRegistroEntrada {

	/** Conexão do banco com o qual se irá sincronizar as unidades */
	private JdbcTemplate jtSistema;
	private JdbcTemplate jtLog = new JdbcTemplate(Database.getInstance().getLogDs());
	private int sistema;
	
	private SincronizadorRegistroEntrada(DataSource ds) {
		if (ds != null)
			this.jtSistema = new JdbcTemplate(ds);
	}
	
	/**
	 * Método para buscar o próximo id disponível para um registro de entrada.
	 * @return
	 */
	public static int getNextId() {
		return new JdbcTemplate(Database.getInstance().getComumDs()).queryForInt("select nextval('entrada_seq')");
	}

	/**
	 * Sincroniza as informações de cadastro de registro de entrada
	 * 
	 * @param u
	 * @param 
	 * @throws Exception
	 */
	public void cadastrarRegistroEntrada(RegistroEntrada r) {
		int id = getNextId();

		String sqlSistema = "insert into comum.registro_entrada (id_entrada, id_usuario, data, ip, id_sistema, host, user_agent, data_saida, resolucao, passaporte, canal, ip_interno_nat) values (?,?,?,?,?,?,?,?,?,?,?,?)";
		jtSistema.update(sqlSistema, new Object[] { id, r.getUsuario().getId(), r.getData(), r.getIP(), sistema, r.getServer(), r.getUserAgent(), r.getDataSaida(), r.getResolucao(), r.getPassaporte(), r.getCanal(), r.getIpInternoNat() });
		
		String sqlLog = "insert into registro_entrada (id_entrada, id_usuario, data, ip, id_sistema, host, user_agent, data_saida, resolucao, passaporte, canal, ip_interno_nat) values (?,?,?,?,?,?,?,?,?,?,?,?)";
		jtLog.update(sqlLog, new Object[] { id, r.getUsuario().getId(), r.getData(), r.getIP(), sistema, r.getServer(), r.getUserAgent(), r.getDataSaida(), r.getResolucao(), r.getPassaporte(), r.getCanal(), r.getIpInternoNat() });
		
		r.setId(id);
	}
	
	 /**
	  * Método Fábrica da classe. Instancia um novo objeto <code>SincronizadorRegistroEntrada</code> 
	  * associado a um data source específico.
	  *  
	  * @param ds
	  * @return
	  */
	 public static SincronizadorRegistroEntrada usandoSistema(int sistema) {
		 DataSource ds = Database.getInstance().getDataSource(sistema);
		 
		 SincronizadorRegistroEntrada sincronizador = MOCK;
		 if (ds != null) {
			 sincronizador = new SincronizadorRegistroEntrada(ds);
			 sincronizador.sistema = sistema;
		 }
		 
		 return sincronizador;
	 }
	 
	 private static final SincronizadorRegistroEntrada MOCK = new SincronizadorRegistroEntrada((DataSource) null) {
		public void cadastrarRegistroEntrada(RegistroEntrada r) { }
	 };
	 
}
