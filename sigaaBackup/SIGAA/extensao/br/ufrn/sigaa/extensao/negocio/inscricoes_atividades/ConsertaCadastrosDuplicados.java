/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 20/03/2013
 * 
 */
package br.ufrn.sigaa.extensao.negocio.inscricoes_atividades;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.ufrn.arq.util.StringUtils;

/**
 * Conserta os cadastros duplicados em cursos e eventos de extensão, estão 
 * duplicados porque a chave unica do email foi tirada para poder rodar a migração.
 *
 * @author jadson - jadson@info.ufrn.br
 * @vesion 1.0 - criação da classe.
 * @since 20/03/2013
 *
 */
public class ConsertaCadastrosDuplicados {

	/**
	 * @param args
	 *  
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/biblioteca/</li>
	 * </ul>
	 * @throws SQLException 
	 *
	 */
	public static void main(String[] args) throws SQLException {
		Connection connection = null;
		
		boolean executar = true; // Indica se vai só testar as consultas ou executar as mudanças no banco.
		
		long tempo = System.currentTimeMillis();
		
		try{
			// TODO colocar o endereço e senha do banco de produção aqui //
			
			//connection = getConnection("jdbc:postgresql://bddesenv1.info.ufrn.br:5432/sigaa_20130218", "sigaa", "sigaa");
			//connection = getConnection("jdbc:postgresql://bdgeral.info.ufrn.br:5432/sigaa_3_9_20130218", "sigaa", "sigaa");
			connection = getConnection("jdbc:postgresql://bdproducao1.info.ufrn.br:5432/sigaa", "sigaa", "");
			connection.setAutoCommit(false);
			
			consertaCadastrosEmailDuplicadoMesmaPessoa(connection, executar);
			
			
			// Se tudo ocorrer sem erro, comiga a transação
			connection.commit();
			
		}catch(Exception ex){
			ex.printStackTrace();
			if (connection != null) connection.rollback();
		}finally{
			if (connection != null) connection.close();
		}
		
		System.out.println(" ************** FIM MIGRAÇÃO "+(System.currentTimeMillis()-tempo)/1000+" segundos ************** ");
		System.exit(0);

	}

	/**
	 * <p>Conserta os cadastros que tem email duplicados no sistema, mas são a mesma pessoa.</p>
	 * 
	 * <p>Nesse caso, desativa um cadastro e migra os participantes e inscrição para o cadastro que continua ativo. </p>
	 * 
	 * @param connection
	 * @param executar
	 * 
	 * @throws SQLException 
	 *
	 */
	private static void consertaCadastrosEmailDuplicadoMesmaPessoa( Connection connection, boolean executar) throws SQLException {
		
		
		System.out.println(" *** Consertando os cadastro que tem email duplicado no sistema. Só pode ter 1 cadastro por e-mail 397 segundos ****");
		
		
		String sql = " select email, count(id_cadastro_participante_atividade_extensao) "+ 
				" from extensao.cadastro_participante_atividade_extensao "+
				" where ativo = true "+
				" group by email "+
				" having count(id_cadastro_participante_atividade_extensao) = 2 "+
				" order by 2 desc ";
		
		String sqlUpdateInscricaoParticipante = " update extensao.inscricao_atividade_participante set id_cadastro_participante_atividade_extensao = ? WHERE id_cadastro_participante_atividade_extensao = ? ";
		
		String sqlUpdateParticipante = " update extensao.participante_acao_extensao set id_cadastro_participante_atividade_extensao = ? WHERE id_cadastro_participante_atividade_extensao = ? ";
	
		String sqlDesativaCadastro = " update extensao.cadastro_participante_atividade_extensao set ativo = false  WHERE id_cadastro_participante_atividade_extensao = ? ";
	
		
		PreparedStatement psUpdateInscricao = connection.prepareStatement(sqlUpdateInscricaoParticipante);
		
		PreparedStatement psInsertaParticipante = connection.prepareStatement(sqlUpdateParticipante);
		
		PreparedStatement psInativaCadastro = connection.prepareStatement(sqlDesativaCadastro);
		
		
		PreparedStatement ps = connection.prepareStatement( sql );
		ps.execute();
		ResultSet rs = ps.getResultSet();
		
		
		int contador = 0;
		
		while( rs.next() ){
			
			String email = rs.getString(1);
			
			
			// Para cada email retorna verifica se todos os cadastros são para a mesma pessoa //
			
			
			String sqlRecuperaCadatros = " select id_cadastro_participante_atividade_extensao, cpf, nome "
										+" from extensao.cadastro_participante_atividade_extensao  where email = ? ";
			
			
			PreparedStatement psRecuperaCadatros = connection.prepareStatement( sqlRecuperaCadatros );
			psRecuperaCadatros.setString(1, email);
			psRecuperaCadatros.execute();
			
			ResultSet rsRecuperaCadatros = psRecuperaCadatros.getResultSet();
			
			CadastroParticipanteTemp cadastro1 = null;
			CadastroParticipanteTemp cadastro2 = null;
			
			while( rsRecuperaCadatros.next() ){
				
				int idCadastro = rsRecuperaCadatros.getInt(1);
				Long cpf = rs.getLong(2);
				String nome = rsRecuperaCadatros.getString(3);
				
				if(cadastro1 == null)
					cadastro1 = new CadastroParticipanteTemp(idCadastro, cpf, nome);
				else
					cadastro2 = new CadastroParticipanteTemp(idCadastro, cpf, nome);
			}
			
			
			if( saoMesmoCadatro (cadastro1, cadastro2, contador) ){
				
				setCadastroAserDesativado(cadastro1, cadastro2, contador);
			

				int idCadastroDesativado = cadastro1.isVaiSerDesativado() ? cadastro1.getIdCadastro() : cadastro2.getIdCadastro();
				int idCadastroCorreto = ! cadastro1.isVaiSerDesativado() ? cadastro1.getIdCadastro() : cadastro2.getIdCadastro();
				
				
				psUpdateInscricao.setInt( 1, idCadastroCorreto );
				psUpdateInscricao.setInt( 2, idCadastroDesativado );
				psUpdateInscricao.addBatch();
				
				
				psInsertaParticipante.setInt( 1, idCadastroCorreto );
				psInsertaParticipante.setInt( 2, idCadastroDesativado );
				psInsertaParticipante.addBatch();
				
		
				psInativaCadastro.setInt( 1, idCadastroDesativado );
				psInativaCadastro.addBatch();
				
			}
		
			
			
			contador++;
		}
		
		
		if(executar){
			psUpdateInscricao.executeBatch();
			psInsertaParticipante.executeBatch();
			psInativaCadastro.executeBatch();
		}
		
	}

	
	
	
	
	private static void setCadastroAserDesativado(CadastroParticipanteTemp cadatro1, CadastroParticipanteTemp cadatro2, int contador) {
		
			if(cadatro1.getNome().equalsIgnoreCase("USUÁRIO MIGRADO SEM NOME") ||  cadatro1.getCpf() == null ){
				cadatro1.setVaiSerDesativado(true);
				if(contador % 100 == 0)
					System.out.println(contador+" desativando: "+cadatro1.getNome());
			}else{
				cadatro2.setVaiSerDesativado(true);
				if(contador % 100 == 0)
					System.out.println(contador+" desativando: "+cadatro2.getNome());
			}
		
	}

	
	private static boolean saoMesmoCadatro(CadastroParticipanteTemp cadatro1, CadastroParticipanteTemp cadatro2, int contador) {
		
			// se o nome tá igual, ou um dos cadastros foi migrado sem informações, então eses são iguais
			if( StringUtils.toAsciiAndUpperCase(cadatro1.getNome()).equalsIgnoreCase( StringUtils.toAsciiAndUpperCase(cadatro2.getNome()) )   
					||  (cadatro1.getNome().equalsIgnoreCase("USUÁRIO MIGRADO SEM NOME")) 
					||  (cadatro2.getNome().equalsIgnoreCase("USUÁRIO MIGRADO SEM NOME"))){
				
				if(contador % 100 == 0)
					System.out.println(contador+" sao mesmo cadatro: "+cadatro1.getNome()+" - "+cadatro2.getNome());
				
				return true;
			}
			
			if(contador % 100 == 0)
				System.out.println(contador+" Nao sao mesmo cadatro: "+cadatro1.getNome()+" - "+cadatro2.getNome());
			
			return false;
	}
	

	/**
	 *   Cria uma conexão com o banco do Sigaa
	 *
	 * @param urlFonte
	 * @param usuarioFonte
	 * @param senhaFonte
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection(String urlDestino, String usuarioDestino, String senhaDestino) throws SQLException{
		try{
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {}
		return DriverManager.getConnection(urlDestino, usuarioDestino, senhaDestino);
	}
	
}


class CadastroParticipanteTemp{
	
	private int idCadastro;	
	private Long cpf;
	private String nome;
	

	private boolean vaiSerDesativado;
	
	public CadastroParticipanteTemp(int idCadastro, Long cpf, String nome) {
		this.idCadastro = idCadastro;
		this.cpf = cpf;
		this.nome = nome;
	}
	
	
	public int getIdCadastro() {
		return idCadastro;
	}
	public void setIdCadastro(int idCadastro) {
		this.idCadastro = idCadastro;
	}
	public Long getCpf() {
		return cpf;
	}
	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}


	public boolean isVaiSerDesativado() {
		return vaiSerDesativado;
	}


	public void setVaiSerDesativado(boolean vaiSerDesativado) {
		this.vaiSerDesativado = vaiSerDesativado;
	}
	  
	  
	  
}

