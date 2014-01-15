/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 26/10/2007
 */
package br.ufrn.arq.usuarios;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.AmbienteUtils;
import br.ufrn.arq.util.FileLogger;
import br.ufrn.arq.util.LdapShaPasswordEncoder;
import br.ufrn.arq.util.NetworkUtils;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPModification;

/**
 * Classe que inclui o usuário no LDAP
 *
 * @author Gleydson Lima
 *
 */
public class UsuarioLDAP extends Thread {

	private static int ldapPort = LDAPConnection.DEFAULT_PORT;

	private static int ldapVersion = LDAPConnection.LDAP_V3;

	private static String ldapHost = "200.17.143.35";

	private static String loginDN = "cn=admin,dc=ufrn,dc=br";

	private static String password = "muhehfoda";

	private static String containerName = "dc=sistemas,dc=ufrn,dc=br";
	
	// usuários e senha usados na sincronização assincroniza com o LDAP
	private String login, senha;
	
	private static FileLogger log;
	
	/** Registra autenticação no Log */
	public UsuarioLDAP(String login, String senha) {
		this.login = login;
		this.senha = senha;
		
		if ( log == null ) {
			try {
				log = new FileLogger(System.getProperty("java.io.tmpdir") + "/ldap.log",true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void log(String linha) {
		if ( log != null ) {
			log.log(linha);
		}
	}
	
	@Override
	public void run() {
		
		log("Iniciando sincronizador LDAP: " + login);
		if ( ! AmbienteUtils.isServidorProducao() ) {
			log("Não é servidor de produção: " + NetworkUtils.getLocalName());
			return;
		}
		
		log("Servidor de producao detectado.");
		
		try {
			resyncronize();
		} catch (ArqException e) {
			e.printStackTrace();
		}
	}

	/** Ressincronizando o login no LDAP */
	public void resyncronize() throws ArqException {
		if (ParametroHelper.getInstance().getParametroBoolean(ConstantesParametroGeral.LDAP_ATIVO)) {
			LDAPConnection lc = new LDAPConnection();
	
			try {
	
				// connect to the server
				lc.connect(ldapHost, ldapPort);
	
				// authenticate to the server
				lc.bind(ldapVersion, loginDN, password.getBytes("UTF8"));
				
				try {
					log("Verificando " + login + " no LDAP.");
					lc.read("uid=" + login + "," + containerName);
					log("Usuário " + login + " encontrado.");
					// se passou por aqui é por que achou a entrada
					// comentado a criação a todo momento por que Bruno alegou problemas de excesso de escrita
					//UsuarioLDAP.modifyEntry(login, senha);
				} catch (Exception e) {
					log("Adicionando " + login + " no LDAP");
					addEntry(login, senha);
					log("Adicionado com sucesso");
				}
			}  catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/** Remove entrada de log */
	public static void removeEntry(String login) throws DAOException {
		if (ParametroHelper.getInstance().getParametroBoolean(ConstantesParametroGeral.LDAP_ATIVO)) {
			LDAPConnection lc = new LDAPConnection();
	
			try {
	
				// connect to the server
				lc.connect(ldapHost, ldapPort);
	
				// authenticate to the server
				lc.bind(ldapVersion, loginDN, password.getBytes("UTF8"));
	
				// Deletes the entry from the directory
				lc.delete("uid=" + login + "," + containerName);
	
				// disconnect with the server
				lc.disconnect();
	
			} catch (LDAPException e) {
				if (e.getResultCode() == LDAPException.NO_SUCH_OBJECT)
					System.err.println("Error: No such object");
				else if (e.getResultCode() == LDAPException.INSUFFICIENT_ACCESS_RIGHTS)
					System.err.println("Error: Insufficient rights");
				else
					System.err.println("Error: " + e.toString());
			} catch (UnsupportedEncodingException e) {
				System.out.println("Error: " + e.toString());
			}
		}
	}
	
	/** retorna próximo número da sequência */
	public static int getNextUidNumber() throws DAOException {
		GenericSharedDBDao dao = new GenericSharedDBDao();
		return dao.getNextSeq("guid_seq");
	}

	
	/** Adicionando nova entrada*/
	public static void addEntry(String login, String senha) throws ArqException {
		if (ParametroHelper.getInstance().getParametroBoolean(ConstantesParametroGeral.LDAP_ATIVO)) {
			
			log("Verificando se producao");
			
			if ( ! AmbienteUtils.isServidorProducao() ) {
				log("Não é servidor de produção: " + NetworkUtils.getLocalName());
				return;
			}
			
			log("Servidor de Producao: " + NetworkUtils.getLocalName());
	
			LDAPConnection lc = new LDAPConnection();
	
			LDAPAttributeSet attributeSet = new LDAPAttributeSet();
	
			/*
			 * To Add an entry to the directory, - Create the attributes of the
			 * entry and add them to an attribute set - Specify the DN of the entry
			 * to be created - Create an LDAPEntry object with the DN and the
			 * attribute set - Call the LDAPConnection add method to add it to the
			 * directory
			 *
			 */
			attributeSet.add(new LDAPAttribute("uid", login));
			attributeSet.add(new LDAPAttribute( "objectClass", new String [] { "top", "account", "posixAccount", "shadowAccount" } ));
			attributeSet.add(new LDAPAttribute("cn", login));
			attributeSet.add(new LDAPAttribute("shadowMax", "99999"));
			attributeSet.add(new LDAPAttribute("shadowWarning", "7"));
			attributeSet.add(new LDAPAttribute("loginShell", "/bin/bash" ));
			
			int uidNumber = getNextUidNumber();
			attributeSet.add(new LDAPAttribute("uidNumber",  String.valueOf(uidNumber)));
			log("uidNumber(" + login + "): "  + uidNumber);
			attributeSet.add(new LDAPAttribute("gidNumber", "50001"));
			attributeSet.add(new LDAPAttribute("homeDirectory", "/home/" + login));
			attributeSet.add(new LDAPAttribute("shadowLastChange", "13559"));
	
			String slapPasswd = getSlapPasswd(senha);
			attributeSet
					.add(new LDAPAttribute("userpassword",  slapPasswd )); 
			log("slappaswd(" + login + "):" + slapPasswd);
			String dn = "uid=" + login + "," + containerName;
	
			LDAPEntry newEntry = new LDAPEntry(dn, attributeSet);
	
			try {
	
				// connect to the server
				lc.connect(ldapHost, ldapPort);
	
				// authenticate to the server
				lc.bind(ldapVersion, loginDN, password.getBytes("UTF8"));
	
				lc.add(newEntry);
				log(login + " adicionado");
				
				// disconnect with the server
				lc.disconnect();
	
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error:  " + e.toString());
			}
		}
	}

	/** modificando entrada através do login e senha */
	public static  void modifyEntry(String login, String senha) throws DAOException {
		if (ParametroHelper.getInstance().getParametroBoolean(ConstantesParametroGeral.LDAP_ATIVO)) {
//			if ( ! UFRNUtils.isServidorProducao() ) {
//				return;
//			}
//	
			// tenta adicionar primeiro pra o caso de não ter ainda.
			try {
				addEntry(login, senha);
			} catch (Exception e ) {
				// silencia
			}
	
			LDAPConnection lc = new LDAPConnection();
	
			String desc = "This LDAP object was modified at " + new Date();
	
			try {
	
				// connect to the server
				lc.connect(ldapHost, ldapPort);
	
				// bind to the server
				lc.bind(ldapVersion, loginDN, password.getBytes("UTF8"));
	
				// Add a known phone number value so we have something to change
				lc.modify("uid=" + login + "," + containerName,
						new LDAPModification(LDAPModification.REPLACE,
								new LDAPAttribute("userpassword",
										getSlapPasswd(senha))));
	
				System.out.println(desc);
	
			} catch (Exception e) {
	
				System.out.println("Error: " + e.toString());
	
			} finally {
				try {
					lc.disconnect();
				} catch (LDAPException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String getSlapPasswd(String senha) throws ArqException {
		LdapShaPasswordEncoder encoder = new LdapShaPasswordEncoder();
		return encoder.encodePassword(senha, "salt".getBytes());
	}

	
	public static void main(String[] args) throws LDAPException, ArqException {

		//System.out.println(System.getProperty("java.io.tmpdir"));
		//UsuarioLDAP ldap = new UsuarioLDAP("gleydson", "gleydson");
		//ldap.start();
		
		LdapShaPasswordEncoder encoder = new LdapShaPasswordEncoder();
		System.out.println(encoder.isPasswordValid(getSlapPasswd("1234"), "1234", null));

	}
	
	/** Verificando entrada */
	public static boolean verifyEntry(String login) throws DAOException, NegocioException{
		if (!ParametroHelper.getInstance().getParametroBoolean(ConstantesParametroGeral.LDAP_ATIVO))
			throw new NegocioException("A sincronização com LDAP não está ativa.");

		LDAPConnection lc = new LDAPConnection();
		
		try {
			
			// connect to the server
			lc.connect(ldapHost, ldapPort);
			
			// authenticate to the server
			lc.bind(ldapVersion, loginDN, password.getBytes("UTF8"));
			
			try {
				
				lc.read("uid=" + login + "," + containerName);
				//Se chegar aqui é porque encontrou
				return true;

			} catch (Exception e) {
				//Se nao encontrar
				return false;
			}
		}  catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;

	}
	
}


