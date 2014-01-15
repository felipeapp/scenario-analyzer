/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 01/09/2004
 */
package br.ufrn.arq.negocio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;

/**
 * Localizador de Movimentos no Sistema. Esta classe recebe o código do comando
 * e retorna o processador responsável pelo seu processamento.
 *
 * @author Gleydson Lima
 */
public class MovimentoLocator {

    private static MovimentoLocator singleton = new MovimentoLocator();

    //private Hashtable<Integer,String> tableMov;
    private Hashtable<Integer,String> descricaoMov;

    DataSource ds;

    InitialContext ic;

    private MovimentoLocator() {

        try {
            //tableMov = new Hashtable<Integer,String>();
            descricaoMov = new Hashtable<Integer,String>();

            ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:/jdbc/SIPACDB");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MovimentoLocator getInstance() {
        return singleton;
    }

    public ProcessadorComando getProcessador(Comando comando) throws ArqException {

    	/*
        try {
            // Busca da tabela de Cache
            String jndiName = (String) tableMov.get(codMovimento);
            if (jndiName == null) {
                jndiName = getJNDIName(codMovimento);
                if (jndiName != null) {
                    tableMov.put(codMovimento, jndiName);
                } else {
                    throw new ArqException("Processador não encontrado");
                }
            }

            ProcessadorHome home = (ProcessadorHome) ic.lookup(jndiName);
            return home.create();

        } catch (Exception e) {
            throw new ArqException(e);
        }
        */
    	try {
			return (ProcessadorComando) Class.forName(comando.getClasse()).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException("Erro ao criar processador: " + e.getMessage());
		}

    }

    public String getJNDIName(int codMovimento) throws SQLException {

        Connection con = null;
        PreparedStatement pSt = null;
        ResultSet rs = null;

        try {
            con = ds.getConnection();
            pSt = con
                    .prepareStatement("SELECT PROCESSADOR_JNDI, DESCRICAO FROM PROCESSADORES WHERE COD_MOVIMENTO = ?");
            pSt.setInt(1, codMovimento);
            rs = pSt.executeQuery();
            if (rs.next()) {
            	descricaoMov.put(codMovimento, rs.getString("descricao"));
                return rs.getString("processador_jndi");
            } else {
                return null;
            }
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pSt != null) {
                try {
                    pSt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    public String getDescricao(int codMovimento) {
    	return descricaoMov.get(codMovimento);
    }

}