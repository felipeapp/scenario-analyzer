/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 05/11/2012
 * 
 */
package br.ufrn.sigaa.extensao.negocio.inscricoes_atividades;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.extensao.dominio.StatusInscricaoParticipante;

/**
 *
 * <p>MIgra os dados antigos para o nome modelo de inscrições de extensão. </p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
public class MigraDadosAntigosInscricaoAtividadeExtensao {

	
	public static final int ID_MODALIDADE_UNICA = 1;
	
	/**
	 * realizar a migração.  Executar os métodos na sequência.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *   </ul>
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		Connection connection = null;
		
		boolean executar = true; // Indica se vai só testar as consultas ou executar as mudanças no banco.
		
		long tempo = System.currentTimeMillis();
		
		try{
			// TODO colocar o endereço e senha do banco de produção aqui //
			
			//connection = getConnection("jdbc:postgresql://bddesenv1.info.ufrn.br:5432/sigaa_20130218", "sigaa", "sigaa");
			//connection = getConnection("jdbc:postgresql://bdgeral.info.ufrn.br:5432/sigaa_3_9_20130218", "sigaa", "sigaa");
			connection = getConnection("jdbc:postgresql://bdproducao1.info.ufrn.br:5432/sigaa", "sigaa", "???");
			connection.setAutoCommit(false);
			
			//migraInformacoesCursoEventoExtensao(connection, executar);
			
			//migraValoresPagosUsuario(connection, executar);
			
			//migraInformacoesCadastroParticipantesDasInscricoes(connection, executar);
			
			//associaInscricaParticipantesComCadastroParticipante(connection, executar);
			
			//migraInformacoesCadastroDosParticipantes(connection, executar);
			
			//desativaParticipantesDuplicados(connection, executar);
			
			//desativaParticipantesDuplicadosMiniAtividadeExtensao(connection, executar);
			
			//desativaParticipantesDuplicadosSemCPF(connection, executar);
			
			//associaParticipantesComCadastroParticipanteOtimizado(connection, executar);
			
			//associaParticipantesComCadastroParticipante(connection, executar);
			
			//migraStatusInscricaoParticipante(connection, executar);
			
			//migraInscricaoParaParticipante(connection, executar);
			
			
			// RODAR ESSES AQUI AGORA //
			//migraInformacoesCadastroDosParticipantesQueContinuaremSemCadastroAposAMigracao(connection, executar);
			
			//associaParticipantesAindaSemCadastroAposMigracao(connection, executar);
			
			
			
			
			// MAIS 4 NOVOS MÉTODOS DE MIGRAÇÃO 
			
			//migraInformacoesCadastroDosParticipantesDiscente(connection, executar);
			
			//associaParticipantesSemInformacaoMasComDiscente(connection, executar);
			
			//migraInformacoesCadastroDosParticipantesServidor(connection, executar);
			
			//associaParticipantesSemInformacaoMasComServidor(connection, executar);
			
			
			
			migraInformacoesCadastroDosParticipantesSemInformacao(connection, executar);
			
			// ACHO QUE PARA EXECUTAR ISSO AQUI SÓ DEPOIS QUE PASSAR UNS 2 MESES NO AR SEM DÁ PROBLEMAS //
			//apagaColunasNaoMaisUsadas(connection, executar);
			
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
	 *  Migra as informações dos valores pagos para as inscrições dos participantes
	 *
	 * @param connection
	 * @param executar
	 * @throws SQLException 
	 */
	public static void migraValoresPagosUsuario(Connection connection, boolean executar) throws SQLException {
		
		String sql = " select m.id_modalidade_participante_periodo_inscricao_atividade as idModalide, m.taxa_matricula, m.id_inscricao_atividade as idInscricao "+
				 " FROM extensao.modalidade_participante_periodo_inscricao_atividade m  "+
				 " WHERE m.ativo = true AND m.id_modalidade_participante =  "+ID_MODALIDADE_UNICA; // Só existe modalidade única até o momento.
		
		
		String sqlUpdateInscricaoParticipante = " update extensao.inscricao_atividade_participante set valor_taxa_matricula = ?, id_modalidade_participante_periodo_inscricao_atividade = ? WHERE id_inscricao_atividade = ? ";
	
		// Ler os dados da entidade CursoEvento //
		PreparedStatement ps = connection.prepareStatement( sql );
		ps.execute();
		ResultSet rs = ps.getResultSet();

		PreparedStatement psUpdateInscricaoParticipante = connection.prepareStatement(sqlUpdateInscricaoParticipante);
		
		while( rs.next() ){
			
			// Dados da inscrição atividade, não para para instanciar o objeto aqui.
			int idModalidadeInscricao = rs.getInt(1);
			Double taxaMatricula =  rs.getDouble(2);
			int idPeriodiInscriaoInscricao = rs.getInt(3);
			
			
			psUpdateInscricaoParticipante.setDouble( 1, taxaMatricula );
			psUpdateInscricaoParticipante.setInt( 2,  idModalidadeInscricao);
			psUpdateInscricaoParticipante.setInt( 3, idPeriodiInscriaoInscricao );
			
			psUpdateInscricaoParticipante.addBatch();
			
		}
		
		if(executar){
			System.out.println(" Executando migração dos valores cobrados dos participante  InscricaoAtividadeParticipante !!!! ");
			psUpdateInscricaoParticipante.executeBatch();
		}
		
	}














	/**
	 * Atualiza o status das inscrições, o status confirmado deixou de exitir.
	 *
	 * Agora vai de inscrito para aprovado. o usuário não vai precisar confirmar sua inscrição.
	 *
	 * então quem está confirmado, volta para inscrito.
	 *
	 * @param connection
	 * @param executar
	 * @throws SQLException 
	 */
	@SuppressWarnings("deprecation")
	public static void migraStatusInscricaoParticipante(Connection connection,boolean executar) throws SQLException {
		
	
		String sqlUpdateInscricao = " update extensao.inscricao_atividade_participante set id_status_inscricao_participante = ? WHERE id_status_inscricao_participante = ? ";
		
		PreparedStatement psUpdateInscricaoParticipante = connection.prepareStatement(sqlUpdateInscricao);
		
		psUpdateInscricaoParticipante.setInt( 1, StatusInscricaoParticipante.INSCRITO );
		psUpdateInscricaoParticipante.setInt( 2, StatusInscricaoParticipante.CONFIRMADO );
		
		psUpdateInscricaoParticipante.addBatch();
		
		if(executar){
			System.out.println(" Executando migração do status das  InscricaoAtividadeParticipante !!!! ");
			psUpdateInscricaoParticipante.executeBatch();
		}
		
		
	}







	/**
	 * Adiciona aos participantes a informação da sua inscrição, é a partir da inscrição que os dados dele são recuperados.
	 *
	 * @param connection
	 * @param executar
	 * @throws SQLException 
	 */
	public static void migraInscricaoParaParticipante(Connection connection, boolean executar) throws SQLException {
		
		System.out.println("Adicionando aos participantes a informação daqueles que tem inscrição....");
		
		String sql = " select inscricao.id_inscricao_atividade_participante, inscricao.id_participante_acao_extensao "+
				 " from extensao.inscricao_atividade_participante inscricao WHERE inscricao.id_participante_acao_extensao IS NOT NULL ";
		
		String sqlUpdateParticipante = " update extensao.participante_acao_extensao set id_inscricao_atividade_participante = ? WHERE id_participante_acao_extensao = ? ";
		
		PreparedStatement psUpdateParticipante = connection.prepareStatement(sqlUpdateParticipante);
		
		PreparedStatement ps = connection.prepareStatement( sql );
		ps.execute();
		ResultSet rs = ps.getResultSet();
		
		while( rs.next() ){
					
			// Dados da inscrição atividade, não para para instanciar o objeto aqui.
			int idInscricaoAtividadeParticipante = rs.getInt(1);
			int idParticipanteAcaoExtensao =  rs.getInt(2);
			
			psUpdateParticipante.setInt( 1, idInscricaoAtividadeParticipante );
			psUpdateParticipante.setInt( 2, idParticipanteAcaoExtensao );
			
			psUpdateParticipante.addBatch();
		}
		
		if(executar){
			System.out.println(" Executando migração da informação da inscrição para o participante de extensão ");
			psUpdateParticipante.executeBatch();
		}
		
		
	}



	
	
	/**
	 * Cria os cadastros para os participantes que estão sem inscrição.
	 * 
	 * A partir de agora todos participante deve possuir um único cadastro no sistema
	 *
	 * @param connection
	 * @param executar
	 * @throws SQLException 
	 */
	public static void migraInformacoesCadastroDosParticipantes(Connection connection, boolean executar) throws SQLException {
		
		System.out.println("Migrando participantes extensao sem inscrição (cadastrado diretamente pelo coordenador ) ... essa demora um pouco +- 10 mim.");
		
		String sql = " select cpf, passaporte, nome, endereco, email, cep, data_nascimento, " +
					"  id_unidade_federativa, id_municipio, data_cadastro, instituicao, id_acao_extensao, id_sub_atividade_extensao "+
					 " FROM extensao.participante_acao_extensao " +
					 " WHERE ativo = true  ";
		
		
		
		PreparedStatement ps = connection.prepareStatement( sql );
		ps.execute();
		ResultSet rs = ps.getResultSet();
		
		List<CadastroParticipanteAtividadeExtensaoMigracao> cadastros = new ArrayList<CadastroParticipanteAtividadeExtensaoMigracao>();
		
		int contador = 0;
		
		while( rs.next() ){   // Para cada participante criado sem inscrição 
					
			// Ler os dados do particapante //
			Long cpf =  rs.getLong(1);
			
	
			String passaporte =  rs.getString(2);
			String nome =  rs.getString(3);
			String endereco =  rs.getString(4);
			String email =  rs.getString(5);
			String cep =  rs.getString(6);
			Date dataNascimento =  rs.getDate(7);
			Integer idUnidadeFederativa =  rs.getInt(8);
			Integer idMunicipio =  rs.getInt(9);
			Date dataCadastro =  rs.getDate(10);
			//String instituicao =  rs.getString(12);
			
			//Integer idAcaoExtensao = rs.getInt(13);
			//Integer idSubAtividadeExtensao = rs.getInt(14);
			
			
			CadastroParticipanteAtividadeExtensaoMigracao cadastro = new CadastroParticipanteAtividadeExtensaoMigracao
					(0, cpf, passaporte, cpf == null, nome, dataNascimento, endereco, "0000", "DESCONHECIDO",
							idMunicipio, idUnidadeFederativa, cep, email, UFRNUtils.geraSenhaAleatoria(), "", "",
							"", dataCadastro);
			
			boolean cadastroJaNaLista = false;
			
			for (CadastroParticipanteAtividadeExtensaoMigracao cadastroTemp : cadastros) {
				if( isMesmoCadastros(cadastroTemp, cadastro )  ){ // pelo cpf, email, passaporte ou data nascimento
					cadastro = cadastroTemp;
					cadastroJaNaLista = true;
					break;
				}
			}
			
			// Adiciona o participante desse cadastro
			//cadastro.adicionaParticipanteAssociadosAoCadastro(idParticipanteAcaoExtensao);
			
			if(! cadastroJaNaLista){ // é um novo, verifica se ele está persistido
				int idCadatroPersistido = retornaIdCadastrosPersisitdo(connection, executar, cadastro.getCpf(), cadastro.getEmail(), cadastro.getPassaporte(), cadastro.getDataNascimento());
			
				if(idCadatroPersistido > 0){ // se está persistido
					cadastro.setId(idCadatroPersistido);
					cadastro.setCriarNovoCadastro(false);
					if(contador % 1000 == 0) System.out.println(contador+" "+cadastro.getNome()+" já está persistido.");
				}else{
					cadastro.setCriarNovoCadastro(true);
					if(contador % 1000 == 0) System.out.println(contador+" Criar cadastro para usuário: "+cadastro.getNome()+".");
				}
				
				cadastros.add(cadastro);
			}
			
			contador++;
			
		} // while( rs)
		
		
		
		List<CadastroParticipanteAtividadeExtensaoMigracao> cadastrosInsercao = new ArrayList<CadastroParticipanteAtividadeExtensaoMigracao>();
		
		for (CadastroParticipanteAtividadeExtensaoMigracao cadatro : cadastros) {
		
			if(cadatro.isCriarNovoCadastro()){
				cadatro.setId( getNextCadastroParticipanteSequence(connection, executar));
				cadastrosInsercao.add(cadatro);
			}
		}
		
		try{
			insereNovoCadastroParticipanteExtensao(connection, executar, cadastrosInsercao);
		}catch(SQLException sqlEx){
			System.err.println("Se deu erro aqui provavelmente participantes com CPFs diferentes possuem o mesmo email. Verificar se não são a mesma pessoa !!! ");
			throw sqlEx;
		}
		
	}
	
	
	



	private static int retornaIdCadastrosPersisitdo(Connection connection, boolean executar, Long cpf, String email, String passaporte, Date dataNascimento) throws SQLException{
		
		String sqlBuscaCadastro = "  select id_cadastro_participante_atividade_extensao from extensao.cadastro_participante_atividade_extensao  "+
				" where  cpf = ? OR ( passaporte = ? and data_nascimento = ? AND cpf IS NULL ) " +
				" OR ( email = ? AND cpf IS NULL AND passaporte IS NULL AND data_nascimento IS NULL ) "+BDUtils.limit(1);
				
				
		
		PreparedStatement psBuscaCadastro = connection.prepareStatement( sqlBuscaCadastro );
		
		if(cpf != null)
			psBuscaCadastro.setLong( 1, cpf );
		else
			psBuscaCadastro.setNull(1, java.sql.Types.BIGINT);
		
		if( passaporte != null)
			psBuscaCadastro.setString( 2, passaporte );
		else
			psBuscaCadastro.setNull( 2, java.sql.Types.VARCHAR );
		
		if(dataNascimento != null)
			psBuscaCadastro.setDate( 3, new java.sql.Date(  dataNascimento.getTime() ) );
		else
			psBuscaCadastro.setNull(3, Types.DATE);  
		
		if(email != null)
			psBuscaCadastro.setString( 4, email );
		else
			psBuscaCadastro.setNull(4, java.sql.Types.VARCHAR);
		
		
		
		psBuscaCadastro.execute();
		
		ResultSet rsCadastro = psBuscaCadastro.getResultSet();
		
		int idCadastroParticipante  = -1;
		
		if ( rsCadastro.next() ){ // Menos mal, tem cadastro
			idCadastroParticipante  = rsCadastro.getInt(1);
			
			return idCadastroParticipante;
			
		}else{  // Não tem cadastro no sistema, tenta criar 1 se tiver informaçõse para isso
		
			return -1;	
		}
		
	}


	public static void desativaParticipantesDuplicadosAcaoExtensao(Connection connection, boolean executar) throws SQLException{
		System.out.println("Desativando os participantes duplicados nas ações de extensã ... " +
				" ( Não faz sentido ter o mesmo participante 2 vezes na mesma ação. tem mais de 6.000 nessa situação vai ficar impossível migrar) ");
	
	
		String sqlDuplicados = " select id_participante_acao_extensao, cpf, id_acao_extensao "+
							   " from extensao.participante_acao_extensao  where cpf in ( "+
								
							   " select cpf "+
							   " from  extensao.participante_acao_extensao "+ 
							   " WHERE ativo = true  and cpf > 0 "+
							   " GROUP BY id_acao_extensao, cpf "+
							   " HAVING count(id_participante_acao_extensao)  > 1"+
								
							   " ) and id_acao_extensao in ( "+
								
								
								" select id_acao_extensao "+
								" from  extensao.participante_acao_extensao "+ 
								" WHERE ativo = true and cpf > 0 "+
								" GROUP BY id_acao_extensao, cpf "+
								" HAVING count(id_participante_acao_extensao)  > 1"+
								
								" ) order by id_acao_extensao, cpf  ";
		

		String sqlAtualizaParticipanteExtensaoDuplicados = " update extensao.participante_acao_extensao  set ativo = false where id_participante_acao_extensao = ? ";
		
		PreparedStatement ps = connection.prepareStatement( sqlDuplicados );
		
		PreparedStatement psAtualizaParticipanteExtensaoDuplicados = connection.prepareStatement(sqlAtualizaParticipanteExtensaoDuplicados);
		
		
		ps.execute();
		ResultSet rs = ps.getResultSet();
		
		
		long CPFAnterior = -1l;
		int idAcaoExtensaoAnterior = -1;
		
		while( rs.next() ){ // Para cadas inscrição existente 
			int idParticipanteExtensao = rs.getInt(1);
			Long CPF = rs.getLong(2);
			int idAcaoExtensao = rs.getInt(3);
			
			if(CPF != null && CPF.equals(CPFAnterior) && idAcaoExtensao == idAcaoExtensaoAnterior ){
				
				psAtualizaParticipanteExtensaoDuplicados.setInt(1, idParticipanteExtensao);
				psAtualizaParticipanteExtensaoDuplicados.addBatch();
			}
			
			CPFAnterior = CPF;
			idAcaoExtensaoAnterior = idAcaoExtensao;
		}
		
		if(executar){
			psAtualizaParticipanteExtensaoDuplicados.executeBatch();
		}
		
		
	}
	
	
	
	public static void desativaParticipantesDuplicadosMiniAtividadeExtensao(Connection connection, boolean executar) throws SQLException{
		System.out.println("Desativando os participantes duplicados nas mini atividades de extensão ... " +
				" ( Não faz sentido ter o mesmo participante 2 vezes na mesma mini atividade. ) ");
	
	
		String sqlDuplicados = " select id_participante_acao_extensao, cpf, id_sub_atividade_extensao "+
								" from extensao.participante_acao_extensao  where cpf in ( "+
								
								" select cpf  "+
								" from  extensao.participante_acao_extensao  "+ 
								" WHERE  ativo = true AND  cpf > 0 and id_sub_atividade_extensao is not null  "+
								" GROUP BY id_sub_atividade_extensao, cpf  "+
								" HAVING count(id_participante_acao_extensao)  > 1  "+
								
								" ) and id_sub_atividade_extensao in (  "+
								
								
								" select id_sub_atividade_extensao  "+
								" from  extensao.participante_acao_extensao  "+ 
								" WHERE  ativo = true AND  cpf > 0 and id_sub_atividade_extensao is not null  "+
								" GROUP BY id_sub_atividade_extensao, cpf  "+
								" HAVING count(id_participante_acao_extensao)  > 1  "+
								
								" ) order by id_sub_atividade_extensao, cpf  ";
		

		String sqlAtualizaParticipanteExtensaoDuplicados = " update extensao.participante_acao_extensao  set ativo = false where id_participante_acao_extensao = ? ";
		
		PreparedStatement ps = connection.prepareStatement( sqlDuplicados );
		
		PreparedStatement psAtualizaParticipanteExtensaoDuplicados = connection.prepareStatement(sqlAtualizaParticipanteExtensaoDuplicados);
		
		
		ps.execute();
		ResultSet rs = ps.getResultSet();
		
		
		long CPFAnterior = -1l;
		int idSubAtividadeExtensaoAnterior = -1;
		
		while( rs.next() ){ // Para cadas inscrição existente 
			int idParticipanteExtensao = rs.getInt(1);
			Long CPF = rs.getLong(2);
			int idSubAtividadeExtensao = rs.getInt(3);
			
			if(CPF != null && CPF.equals(CPFAnterior) && idSubAtividadeExtensao == idSubAtividadeExtensaoAnterior ){
				
				psAtualizaParticipanteExtensaoDuplicados.setInt(1, idParticipanteExtensao);
				psAtualizaParticipanteExtensaoDuplicados.addBatch();
			}
			
			CPFAnterior = CPF;
			idSubAtividadeExtensaoAnterior = idSubAtividadeExtensao;
		}
		
		if(executar){
			psAtualizaParticipanteExtensaoDuplicados.executeBatch();
		}
		
		
	}
	
	public static void desativaParticipantesDuplicadosSemCPF(Connection connection, boolean executar) throws SQLException{
		System.out.println("Desativando os participantes duplicados nas  atividades de extensão que não possuem CPF ... ");
	
	
		String sqlAtualizaParticipanteExtensaoDuplicados = " update  extensao.participante_acao_extensao  set ativo = false " +
				" WHERE id_participante_acao_extensao IN ( 91750836 ) ";		

		PreparedStatement psAtualizaParticipanteExtensaoDuplicados = connection.prepareStatement(sqlAtualizaParticipanteExtensaoDuplicados);
	
		
		if(executar){
			psAtualizaParticipanteExtensaoDuplicados.execute();
		}	
		
	}
	
	
	
	/** Associa os Participantes ao Cadastro otimizando as consulta ao banco
	 * @throws SQLException */
	public static void associaParticipantesComCadastroParticipanteOtimizado(Connection connection, boolean executar) throws SQLException{
	
		System.out.println("Lendo dados do participante novamente  para associar ao cadastro ... ");
		
		
		String sqlAtualizacaoOtimizacao = 
				 " UPDATE extensao.participante_acao_extensao participante set id_cadastro_participante_atividade_extensao = interna.id_cadastro_participante_atividade_extensao " 
				 +" from( "
				 +" select cadastro.id_cadastro_participante_atividade_extensao, cadastro.cpf " 
				 +" from extensao.cadastro_participante_atividade_extensao cadastro "
				 +" WHERE cadastro.ativo = true and cadastro.cpf <> 0 and cadastro.cpf is not null "
				 +" )interna "
				 +" WHERE interna.cpf = participante.cpf; ";
		
		
		
		PreparedStatement psAtualizaParticipanteExtensao = connection.prepareStatement(sqlAtualizacaoOtimizacao);
		
		if(executar){
			System.out.println(" Atualizando os participantes para apontar para o cadastro do participante. ");
			try{
				psAtualizaParticipanteExtensao.execute();
			}catch(SQLException sqlE){
				System.err.println("Se deu erro aqui provavelmente um participante está associado a uma atividade mais de uma vez. Desative 1 deles.");
				throw sqlE;
			}
		}
		
	}
	
	
	
	/** Associa os Participantes ao Cadastro criado 
	 * @throws SQLException */
	public static void associaParticipantesComCadastroParticipante(Connection connection, boolean executar) throws SQLException{
	
		System.out.println("Lendo dados do participante novamente  para associar ao cadastro ... ");
		
		
		String sql = " select id_participante_acao_extensao, cpf, email, passaporte, data_nascimento "+
					" FROM extensao.participante_acao_extensao WHERE ativo = true AND ( cpf = 0 OR CPF IS NULL ) ";
		
		String sqlAtualizaParticipanteExtensao = " UPDATE extensao.participante_acao_extensao "+
				" set id_cadastro_participante_atividade_extensao = ? WHERE id_participante_acao_extensao = ? ";
		
		PreparedStatement ps = connection.prepareStatement( sql );
		
		ps.execute();
		ResultSet rs = ps.getResultSet();
		
		PreparedStatement psAtualizaParticipanteExtensao = connection.prepareStatement(sqlAtualizaParticipanteExtensao);
		
		
		while( rs.next() ){ // Para cadas inscrição existente 
			
			int idParticipanteExtensao = rs.getInt(1);
			Long CPF = rs.getLong(2);
			String email = rs.getString(3);
			String passaporte = rs.getString(4);
			Date dataNascimento = rs.getDate(5);
			
			String sqlBuscaCadastroByCPF = " select id_cadastro_participante_atividade_extensao "+
					" FROM extensao.cadastro_participante_atividade_extensao where ativo = true AND cpf = ? ";
			
			
			PreparedStatement psBuscaCadastroByCPF = connection.prepareStatement(sqlBuscaCadastroByCPF);
			psBuscaCadastroByCPF.setLong(1, CPF);
			
			ResultSet rs2 = psBuscaCadastroByCPF.executeQuery();
			
			if( CPF > 0 && rs2.next() ){  // encontrou o cadastro pelo CPF
				
				int idCadastroParticipante = rs2.getInt(1);
				
				psAtualizaParticipanteExtensao.setInt(1, idCadastroParticipante);
				psAtualizaParticipanteExtensao.setInt(2, idParticipanteExtensao);
				psAtualizaParticipanteExtensao.addBatch();
				
				//System.out.println("UPDATE extensao.participante_acao_extensao set id_cadastro_participante_atividade_extensao = "+idCadastroParticipante+" WHERE id_participante_acao_extensao = "+idParticipanteExtensao);
				
			}else{ // não tem CPF
			
				if(dataNascimento != null && passaporte != null ){
					String sqlBuscaCadastroByPassaporteDataNascimento = " select id_cadastro_participante_atividade_extensao "+
							" FROM extensao.cadastro_participante_atividade_extensao where ativo = true AND passaporte = ? AND data_nascimento = ? ";
					
					
					PreparedStatement psBuscaCadastroByPassaporteDataNascimento = connection.prepareStatement(sqlBuscaCadastroByPassaporteDataNascimento);
					psBuscaCadastroByPassaporteDataNascimento.setString(1, passaporte);
					psBuscaCadastroByPassaporteDataNascimento.setDate(2, new java.sql.Date(dataNascimento.getTime()));
					ResultSet rs3 = psBuscaCadastroByPassaporteDataNascimento.executeQuery();
					
					if( rs3.next() ){  // encontrou o cadastro pelo CPF
						
						int idCadastroParticipante = rs3.getInt(1);
						
						psAtualizaParticipanteExtensao.setInt(1, idCadastroParticipante);
						psAtualizaParticipanteExtensao.setInt(2, idParticipanteExtensao);
						psAtualizaParticipanteExtensao.addBatch();
						
						//System.out.println("UPDATE extensao.participante_acao_extensao set id_cadastro_participante_atividade_extensao = "+idCadastroParticipante+" WHERE id_participante_acao_extensao = "+idParticipanteExtensao);
						
					}else{
						System.out.println(" [ERRO] Participante sem cadastro: id_participante_acao_extensao = "+idParticipanteExtensao+" CPF= "+CPF+" Pass= "+passaporte+" nasc = "+dataNascimento);
					}
				}else{
					
					String sqlBuscaCadastroByEmail = " select id_cadastro_participante_atividade_extensao "+
							" FROM extensao.cadastro_participante_atividade_extensao where ativo = true AND email = ? ";
					
					PreparedStatement psBuscaCadastroByEmail = connection.prepareStatement(sqlBuscaCadastroByEmail);
					psBuscaCadastroByEmail.setString(1, email);
					
					ResultSet rs3 = psBuscaCadastroByEmail.executeQuery();
					
					if( rs3.next() ){  // encontrou o cadastro pelo Email
						
						int idCadastroParticipante = rs3.getInt(1);
						
						psAtualizaParticipanteExtensao.setInt(1, idCadastroParticipante);
						psAtualizaParticipanteExtensao.setInt(2, idParticipanteExtensao);
						psAtualizaParticipanteExtensao.addBatch();
						
						//System.out.println("UPDATE extensao.participante_acao_extensao set id_cadastro_participante_atividade_extensao = "+idCadastroParticipante+" WHERE id_participante_acao_extensao = "+idParticipanteExtensao);
						
					}else{
						System.out.println(" [ERRO] Participante sem cadastro: id_participante_acao_extensao = "+idParticipanteExtensao+" CPF= "+CPF+" Pass= "+passaporte+" nasc = "+dataNascimento+" email "+email);
					}
				}
				
			}
		}
		
		
		if(executar){
			System.out.println(" Atualizando os participantes para apontar para o cadastro do participante. ");
			try{
				psAtualizaParticipanteExtensao.executeBatch();
			}catch(SQLException sqlE){
				System.err.println("Se deu erro aqui provavelmente um participante está associado a uma atividade mais de uma vez. Desative 1 deles.");
				throw sqlE;
			}
		}
		
	}









	/**
	 * Migra os dados que estavam na entidade "CursosEventoExtensao" e vão passar para "InscricaoAtividade"
	 * @throws SQLException 
	 *
	 */
	public static void migraInformacoesCursoEventoExtensao(Connection connection, boolean executar) throws SQLException {
		System.out.println(" *** Migrando os dados de curso e eventos de extensao para inscrição atividade ****");
		
		
		
		String sql = " select i.id_inscricao_atividade, c.cobranca_taxa_matricula, c.data_vencimento_gru, c.taxa_matricula "+
					 " from extensao.curso_evento c "+
					 " inner join extensao.atividade a ON a.id_curso_evento = c.id_curso_evento "+
					 " inner join extensao.inscricao_atividade i ON i.id_atividade = a.id_atividade ";
		
		String sqlUpdateInscricao = " update extensao.inscricao_atividade set cobranca_taxa_matricula = ?, data_vencimento_gru =? WHERE id_inscricao_atividade = ? ";
		
		String sqlInsertModalidade = " insert into extensao.modalidade_participante_periodo_inscricao_atividade " +
				" (id_modalidade_participante_periodo_inscricao_atividade, taxa_matricula, id_modalidade_participante, id_inscricao_atividade ) " +
				"VALUES ( nextval('extensao.modalidade_participante_sequence'), ?, ?, ? )";
		
		
		
		// Ler os dados da entidade CursoEvento //
		PreparedStatement ps = connection.prepareStatement( sql );
		ps.execute();
		ResultSet rs = ps.getResultSet();
		
		PreparedStatement psUpdateInscricao = connection.prepareStatement(sqlUpdateInscricao);
		
		PreparedStatement psInsertaModalidade = connection.prepareStatement(sqlInsertModalidade);
		
		int contador = 0;
		
		while( rs.next() ){
			
			// Dados da inscrição atividade, não para para instanciar o objeto aqui.
			int idPeriodoInscricao = rs.getInt(1);
			boolean cobrancaTaxaMatricula =  rs.getBoolean(2);
			Date dataVencimentoGRU = rs.getDate(3);
			
			// se tem cobrança de taxa de matrícula, set o valor 
			// por padrão os migrados vão todos ser da modalidade única, porque não havia valores diferentes antes.
			if(cobrancaTaxaMatricula){
				Double taxaMatricula = rs.getDouble(4);	
				
				psInsertaModalidade.setDouble( 1, taxaMatricula );
				psInsertaModalidade.setInt( 2,  ID_MODALIDADE_UNICA);
				psInsertaModalidade.setInt( 3, idPeriodoInscricao );
				
				psInsertaModalidade.addBatch();
			}
			
			psUpdateInscricao.setBoolean( 1, cobrancaTaxaMatricula );
			
			if(dataVencimentoGRU != null)
				psUpdateInscricao.setDate( 2, new java.sql.Date( dataVencimentoGRU.getTime() ) );
			else
				psUpdateInscricao.setNull(2, java.sql.Types.DATE);
			
			psUpdateInscricao.setInt(3, idPeriodoInscricao);
			
			psUpdateInscricao.addBatch();
			
			if(contador % 100 == 0 && contador > 0 )
				System.out.println(" Atualizando inscrição atividade número: "+contador);
			
			contador++;
		}
		
		
		if(executar){
			
			System.out.println(" Executando migração de cursosEventos para  InscricaoAtividade !!!! ");
			
			psUpdateInscricao.executeBatch();
		
			psInsertaModalidade.executeBatch(); // para aqueles que tem taxa de inscrição insere como sendo modalidade única com o valor existente hoje.
			
		}
	
	}

	
	
	/**
	 * migra os dados pessoais do participante de InscricaoAtividadeParticipante e ParticipanteAcaoExtensao 
	 * para CadastroParticipanteAtividadeExtensao.
	 *
	 * AQUI MIGRA AS INFORMAÇÔES DOS CADASTROS DOS PARTICIPANTES (cpf, passaporte, nome, etc...) QUE TEM INSCRIÇÃO EM ATIVIDADE
	 *
	 *
	 * Vai ficar tudo concentrado nesse classe agora, para não ficar duplicando dados e gerando problemas !!!
	 *
	 * @param connection
	 * @param executar
	 * @throws SQLException 
	 */
	public static void migraInformacoesCadastroParticipantesDasInscricoes(Connection connection, boolean executar) throws SQLException {

		System.out.println("Lendo dados da inscrição do participante... ");
		
		String sql = " select cpf, passaporte, nome, data_nascimento, logradouro, numero, bairro, id_municipio, id_unidade_federativa, "+ 
					" cep, email, senha, telefone, celular, codigo_acesso, data_cadastro "+
					" FROM extensao.inscricao_atividade_participante where ativo = true ORDER BY data_cadastro desc ";
		
		PreparedStatement ps = connection.prepareStatement( sql );
		
		ps.execute();
		ResultSet rs = ps.getResultSet();
		
		// cadastro a serem migrados //
		List<CadastroParticipanteAtividadeExtensaoMigracao> cadastros = new ArrayList<CadastroParticipanteAtividadeExtensaoMigracao>();
		
		while( rs.next() ){
			
			Long CPF = rs.getLong(1);
			String passaporte = rs.getString(2);
			Date dataNascimento = rs.getDate(4);
			
			if( ( CPF == null || CPF == 0 ) && ( passaporte == null || dataNascimento == null ) ){
				System.out.println("Opa problema !!! ");
				continue; // passa para o próximo, não tem como migrar se CPF passaporte e dataNascimento são nulas.
			}
			
			if( new Long(rs.getLong(1)) != null && rs.getLong(1) > 0){  //tem CPF
				CadastroParticipanteAtividadeExtensaoMigracao cadastro = new CadastroParticipanteAtividadeExtensaoMigracao
						( getNextCadastroParticipanteSequence(connection, executar),
								rs.getLong(1), rs.getString(2), false, rs.getString(3), rs.getDate(4), rs.getString(5), rs.getString(6), rs.getString(7),
								rs.getInt(8), rs.getInt(9), rs.getString(10), rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14),
								rs.getString(15), rs.getDate(16));
				
				if(! cadastros.contains(cadastro)) // não aceita CPF duplicados
					cadastros.add(cadastro);
			
			}else{ // se não tem cpf é considerado extrageiro
				CadastroParticipanteAtividadeExtensaoMigracao cadastro = new CadastroParticipanteAtividadeExtensaoMigracao
						(getNextCadastroParticipanteSequence(connection, executar),
								rs.getLong(1), rs.getString(2), true, rs.getString(3), rs.getDate(4), rs.getString(5), rs.getString(6), rs.getString(7),
								rs.getInt(8), rs.getInt(9), rs.getString(10), rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14),
								rs.getString(15), rs.getDate(16));
				
				if(! containsCadastroByPassaporteDataNascimento(cadastros, cadastro)) // não aceita CPF duplicados
					cadastros.add(cadastro);
			}
			
		}
		
		insereNovoCadastroParticipanteExtensao(connection, executar, cadastros);
		
	}

	
	
	
	private static void insereNovoCadastroParticipanteExtensao(Connection connection, boolean executar, List<CadastroParticipanteAtividadeExtensaoMigracao> cadastros) throws SQLException{
		
		
		String sqlInsertCadastro = " insert into extensao.cadastro_participante_atividade_extensao  " +
				" (id_cadastro_participante_atividade_extensao, cpf, passaporte, nome, nome_ascii, data_nascimento, logradouro, numero, complemento, bairro, id_municipio, id_unidade_federativa, "+ 
					" cep, email, telefone, celular, senha, senha_gerada, estrangeiro, data_cadastro, codigo_acesso_confirmacao, ativo, confirmado ) " +
				" VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
		
		
		PreparedStatement psInsertaCadastro= connection.prepareStatement(sqlInsertCadastro);
		
		System.out.println("Migrando: "+cadastros.size()+" cadastros ");
		
		for (CadastroParticipanteAtividadeExtensaoMigracao cadastro : cadastros) {
			
			psInsertaCadastro.setInt(1, cadastro.getId());
			
			if(cadastro.getCpf() != null)
				psInsertaCadastro.setLong(2, cadastro.getCpf());
			else
				psInsertaCadastro.setNull(2, java.sql.Types.BIGINT);
			
			if(StringUtils.notEmpty(cadastro.getPassaporte()))
				psInsertaCadastro.setString(3, cadastro.getPassaporte());
			else
				psInsertaCadastro.setNull(3, java.sql.Types.VARCHAR);
			
			
			psInsertaCadastro.setString(4, cadastro.getNome());
			psInsertaCadastro.setString(5, cadastro.getNomeAscii());
			
			if(cadastro.getDataNascimento() != null)
				psInsertaCadastro.setDate(6, new java.sql.Date( cadastro.getDataNascimento().getTime() ));
			else
				psInsertaCadastro.setDate(6, new java.sql.Date( new Date().getTime() ));
			
			psInsertaCadastro.setString(7, cadastro.getLogradouro());
			psInsertaCadastro.setString(8, cadastro.getNumero());
			psInsertaCadastro.setString(9, cadastro.getComplemento());
			psInsertaCadastro.setString(10, cadastro.getBairro());
			psInsertaCadastro.setInt(11, cadastro.getMunicipio());
			psInsertaCadastro.setInt(12, cadastro.getUnidadeFederativa());
			psInsertaCadastro.setString(13, cadastro.getCep());
			psInsertaCadastro.setString(14, cadastro.getEmail());
			psInsertaCadastro.setString(15, cadastro.getTelefone());
			psInsertaCadastro.setString(16, cadastro.getCelular());
			
			cadastro.geraHashSenha();
			psInsertaCadastro.setString(17, cadastro.getSenha());
			psInsertaCadastro.setString(18, cadastro.getSenhaGerada());
			psInsertaCadastro.setBoolean(19, cadastro.isEstrangeiro());
			
			if( cadastro.getDataCadastro() != null)
				psInsertaCadastro.setDate(20,  new java.sql.Date( cadastro.getDataCadastro().getTime() ) );
			else
				psInsertaCadastro.setDate(20,  new java.sql.Date( new Date().getTime() ) );
			
			psInsertaCadastro.setString(21, cadastro.getCodigoAcessoConfirmacao());
			psInsertaCadastro.setBoolean(22, true);
			psInsertaCadastro.setBoolean(23, true);
			
	
			psInsertaCadastro.addBatch();
			
		}
		
		if(executar){
			System.out.println(" Executando migração cadatros para a base e associando aos particpantes ! ");
			psInsertaCadastro.executeBatch();
		}
	}
	
	
	private static int getNextCadastroParticipanteSequence(Connection connection, boolean executar) throws SQLException{
		
		String sql = " select * FROM nextval('extensao.cadastro_participante_atividade_extensao_sequence') ";
		
		if(executar){
			PreparedStatement ps = connection.prepareStatement( sql );
			ps.execute();
			ResultSet rs = ps.getResultSet();
			
			if( rs.next() )
				return rs.getInt(1);
			
			return 0;
			
		}else
			return 0;
	}
	
//	private static int getNextInscriacoParticipanteSequence(Connection connection, boolean executar) throws SQLException{
//		
//		String sql = " select * FROM nextval('extensao.inscricao_atividade_participante_sequence') ";
//		
//		if(executar){
//			PreparedStatement ps = connection.prepareStatement( sql );
//			ps.execute();
//			ResultSet rs = ps.getResultSet();
//			
//			if( rs.next() )
//				return rs.getInt(1);
//			
//			return 0;
//			
//		}else
//			return 0;
//	}
	
	
	/**
	 *  Verifica se já não existe o cadastro by passaporte e data de nascimento para não deixar duplicar. 
	 *  
	 *
	 * @param cadastros
	 * @param cadastro
	 * @return
	 */
	private static boolean containsCadastroByPassaporteDataNascimento(List<CadastroParticipanteAtividadeExtensaoMigracao> cadastros, CadastroParticipanteAtividadeExtensaoMigracao cadastro) {

		for (CadastroParticipanteAtividadeExtensaoMigracao cadastroTemp : cadastros) {
			if(cadastroTemp.getPassaporte() != null && cadastroTemp.getPassaporte().equalsIgnoreCase(cadastro.getPassaporte()) 
					&& cadastroTemp.getDataNascimento() != null && cadastroTemp.getDataNascimento().equals( cadastro.getDataNascimento()))
				return true;
		}
		
		return false;
	}





	/**
	 *  Realiza a assolicação entre a inscrição do participante e o cadatro criado para eles.
	 *  
	 *   Antes a cada nova inscrição era criado um cadastro, agora o participante só tem um cadastro e a inscrição "aponta" para esse cadastro.
	 *
	 * @param connection
	 * @param executar
	 * @throws SQLException 
	 */
	public static void associaInscricaParticipantesComCadastroParticipante(Connection connection, boolean executar) throws SQLException {
		
		System.out.println("Lendo dados das inscrição do participante novamente ... ");
		
		String sql = " select id_inscricao_atividade_participante, cpf, passaporte, data_nascimento "+
					" FROM extensao.inscricao_atividade_participante ";
		
		String sqlAtualizaInscricaoParticipante = " UPDATE extensao.inscricao_atividade_participante "+
				" set id_cadastro_participante_atividade_extensao = ? WHERE id_inscricao_atividade_participante = ? ";
		
		PreparedStatement ps = connection.prepareStatement( sql );
		
		ps.execute();
		ResultSet rs = ps.getResultSet();
		
		PreparedStatement psAtualizaInscricaoParticipante = connection.prepareStatement(sqlAtualizaInscricaoParticipante);
		
		
		while( rs.next() ){ // Para cadas inscrição existente 
			
			int idInscricaoParticipante = rs.getInt(1);
			Long CPF = rs.getLong(2);
			String passaporte = rs.getString(3);
			Date dataNascimento = rs.getDate(4);
			
			String sqlBuscaCadastroByCPF = " select id_cadastro_participante_atividade_extensao "+
					" FROM extensao.cadastro_participante_atividade_extensao where ativo = true AND cpf = ? ";
			
			
			PreparedStatement psBuscaCadastroByCPF = connection.prepareStatement(sqlBuscaCadastroByCPF);
			psBuscaCadastroByCPF.setLong(1, CPF);
			
			ResultSet rs2 = psBuscaCadastroByCPF.executeQuery();
			
			if( rs2.next() ){  // encontrou o cadastro pelo CPF
				
				int idCadastroParticipante = rs2.getInt(1);
				
				psAtualizaInscricaoParticipante.setInt(1, idCadastroParticipante);
				psAtualizaInscricaoParticipante.setInt(2, idInscricaoParticipante);
				psAtualizaInscricaoParticipante.addBatch();
			}else{ // não tem CPF
			
				String sqlBuscaCadastroByPassaporteDataNascimento = " select id_cadastro_participante_atividade_extensao "+
						" FROM extensao.cadastro_participante_atividade_extensao where ativo = true AND passaporte = ? AND data_nascimento = ? ";
				
				
				PreparedStatement psBuscaCadastroByPassaporteDataNascimento = connection.prepareStatement(sqlBuscaCadastroByPassaporteDataNascimento);
				psBuscaCadastroByPassaporteDataNascimento.setString(1, passaporte);
				psBuscaCadastroByPassaporteDataNascimento.setDate(2, new java.sql.Date(dataNascimento.getTime()));
				ResultSet rs3 = psBuscaCadastroByPassaporteDataNascimento.executeQuery();
				
				if( rs3.next() ){  // encontrou o cadastro pelo CPF
					
					int idCadastroParticipante = rs3.getInt(1);
					
					psAtualizaInscricaoParticipante.setInt(1, idCadastroParticipante);
					psAtualizaInscricaoParticipante.setInt(2, idInscricaoParticipante);
					psAtualizaInscricaoParticipante.addBatch();
				}else{
					System.out.println(" [ERRO] Inscrição sem cadastro: "+idInscricaoParticipante+" CPF= "+CPF+" Pass= "+passaporte+" nasc = "+dataNascimento);
				}
				
			}
		}
		
		
		if(executar){
			System.out.println(" Atualizando as incrições para apontar para o cadastro do participante. ");
			psAtualizaInscricaoParticipante.executeBatch();
		}
		
	}



	
	/**
	 * Cria os cadastros para os participantes que estão sem inscrição.
	 * 
	 * A partir de agora todos participante deve possuir um único cadastro no sistema
	 *
	 * @param connection
	 * @param executar
	 * @throws SQLException 
	 */
	public static void migraInformacoesCadastroDosParticipantesQueContinuaremSemCadastroAposAMigracao(Connection connection, boolean executar) throws SQLException {
		
		System.out.println("Migrando participantes que continuarem sem cadastro depois da migração.  +- 900 .");
		
		String sql = " select cpf, passaporte, nome, endereco, email, cep, data_nascimento, " +
					"  id_unidade_federativa, id_municipio, data_cadastro "+
					 " FROM extensao.participante_acao_extensao " +
					 " WHERE ativo = true  and id_cadastro_participante_atividade_extensao is null "+
					 " and ( cpf > 0  OR (passaporte is not null and passaporte <> '' and data_nascimento is not null ) ) and nome is not null and nome <> '' ";
		
		
		PreparedStatement ps = connection.prepareStatement( sql );
		ps.execute();
		ResultSet rs = ps.getResultSet();
		
		List<CadastroParticipanteAtividadeExtensaoMigracao> cadastros = new ArrayList<CadastroParticipanteAtividadeExtensaoMigracao>();
		
		int contador = 0;
		
		while( rs.next() ){   // Para cada participante criado sem inscrição 
					
			// Ler os dados do particapante //
			Long cpf =  rs.getLong(1);
			
	
			String passaporte =  rs.getString(2);
			String nome =  rs.getString(3);
			String endereco =  rs.getString(4);
			String email =  rs.getString(5);
			String cep =  rs.getString(6);
			Date dataNascimento =  rs.getDate(7);
			Integer idUnidadeFederativa =  rs.getInt(8);
			Integer idMunicipio =  rs.getInt(9);
			Date dataCadastro =  rs.getDate(10);
			//String instituicao =  rs.getString(12);
			
			//Integer idAcaoExtensao = rs.getInt(13);
			//Integer idSubAtividadeExtensao = rs.getInt(14);
			
			
			CadastroParticipanteAtividadeExtensaoMigracao cadastro = new CadastroParticipanteAtividadeExtensaoMigracao
					(0, cpf, passaporte, cpf == null, nome, dataNascimento, endereco, "0000", "DESCONHECIDO",
							idMunicipio, idUnidadeFederativa, cep, email, UFRNUtils.geraSenhaAleatoria(), "", "",
							"", dataCadastro);
			
			boolean cadastroJaNaLista = false;
			
			for (CadastroParticipanteAtividadeExtensaoMigracao cadastroTemp : cadastros) {
				if( isMesmoCadastros(cadastroTemp, cadastro )  ){ // pelo cpf, email, passaporte ou data nascimento
					cadastro = cadastroTemp;
					cadastroJaNaLista = true;
					break;
				}
			}
			
			// Adiciona o participante desse cadastro
			//cadastro.adicionaParticipanteAssociadosAoCadastro(idParticipanteAcaoExtensao);
			
			if(! cadastroJaNaLista){ // é um novo, verifica se ele está persistido
				int idCadatroPersistido = retornaIdCadastrosPersisitdo(connection, executar, cadastro.getCpf(), cadastro.getEmail(), cadastro.getPassaporte(), cadastro.getDataNascimento());
			
				if(idCadatroPersistido > 0){ // se está persistido
					cadastro.setId(idCadatroPersistido);
					cadastro.setCriarNovoCadastro(false);
					if(contador % 100 == 0) System.out.println(contador+" "+cadastro.getNome()+" já está persistido.");
				}else{
					cadastro.setCriarNovoCadastro(true);
					if(contador % 100 == 0) System.out.println(contador+" Criar cadastro para usuário: "+cadastro.getNome()+".");
				}
				
				cadastros.add(cadastro);
			}
			
			contador++;
			
		} // while( rs)
		
		
		
		List<CadastroParticipanteAtividadeExtensaoMigracao> cadastrosInsercao = new ArrayList<CadastroParticipanteAtividadeExtensaoMigracao>();
		
		for (CadastroParticipanteAtividadeExtensaoMigracao cadatro : cadastros) {
		
			if(cadatro.isCriarNovoCadastro()){
				if(executar)
					cadatro.setId( getNextCadastroParticipanteSequence(connection, executar));
				
				cadastrosInsercao.add(cadatro);
			}
		}
		
		try{
			
			insereNovoCadastroParticipanteExtensao(connection, executar, cadastrosInsercao);
		}catch(SQLException sqlEx){
			System.err.println("Se deu erro aqui provavelmente participantes com CPFs diferentes possuem o mesmo email. Verificar se não são a mesma pessoa !!! ");
			throw sqlEx;
		}
		
	}
	
	
	
	/** Associa os Participantes ao Cadastro criado 
	 * @throws SQLException */
	public static void associaParticipantesAindaSemCadastroAposMigracao(Connection connection, boolean executar) throws SQLException{
	
		System.out.println("Associando participantes que ainda continuam sem cadastro após a migração +- 800 ... ");
		
		
		String sql = " select id_participante_acao_extensao, cpf, email, passaporte, data_nascimento "+
					 " FROM extensao.participante_acao_extensao " +
					 " WHERE ativo = true  and id_cadastro_participante_atividade_extensao is null "+
					 " and ( cpf > 0  OR (passaporte is not null and passaporte <> '' and data_nascimento is not null ) ) and nome is not null and nome <> '' ";
		
		String sqlAtualizaParticipanteExtensao = " UPDATE extensao.participante_acao_extensao "+
				" set id_cadastro_participante_atividade_extensao = ? WHERE id_participante_acao_extensao = ? ";
		
		PreparedStatement ps = connection.prepareStatement( sql );
		
		ps.execute();
		ResultSet rs = ps.getResultSet();
		
		PreparedStatement psAtualizaParticipanteExtensao = connection.prepareStatement(sqlAtualizaParticipanteExtensao);
		
		
		while( rs.next() ){ // Para cadas inscrição existente 
			
			int idParticipanteExtensao = rs.getInt(1);
			Long CPF = rs.getLong(2);
			String email = rs.getString(3);
			String passaporte = rs.getString(4);
			Date dataNascimento = rs.getDate(5);
			
			String sqlBuscaCadastroByCPF = " select id_cadastro_participante_atividade_extensao "+
					" FROM extensao.cadastro_participante_atividade_extensao where ativo = true AND cpf = ? ";
			
			
			PreparedStatement psBuscaCadastroByCPF = connection.prepareStatement(sqlBuscaCadastroByCPF);
			psBuscaCadastroByCPF.setLong(1, CPF);
			
			ResultSet rs2 = psBuscaCadastroByCPF.executeQuery();
			
			if( CPF > 0 && rs2.next() ){  // encontrou o cadastro pelo CPF
				
				int idCadastroParticipante = rs2.getInt(1);
				
				System.out.println("Achou cadatro CPF "+CPF);
				
				psAtualizaParticipanteExtensao.setInt(1, idCadastroParticipante);
				psAtualizaParticipanteExtensao.setInt(2, idParticipanteExtensao);
				psAtualizaParticipanteExtensao.addBatch();
				
				//System.out.println("UPDATE extensao.participante_acao_extensao set id_cadastro_participante_atividade_extensao = "+idCadastroParticipante+" WHERE id_participante_acao_extensao = "+idParticipanteExtensao);
				
			}else{ // não tem CPF
			
				if(dataNascimento != null && passaporte != null ){
					String sqlBuscaCadastroByPassaporteDataNascimento = " select id_cadastro_participante_atividade_extensao "+
							" FROM extensao.cadastro_participante_atividade_extensao where ativo = true AND passaporte = ? AND data_nascimento = ? ";
					
					
					PreparedStatement psBuscaCadastroByPassaporteDataNascimento = connection.prepareStatement(sqlBuscaCadastroByPassaporteDataNascimento);
					psBuscaCadastroByPassaporteDataNascimento.setString(1, passaporte);
					psBuscaCadastroByPassaporteDataNascimento.setDate(2, new java.sql.Date(dataNascimento.getTime()));
					ResultSet rs3 = psBuscaCadastroByPassaporteDataNascimento.executeQuery();
					
					if( rs3.next() ){  // encontrou o cadastro pelo passapor e data de nascimento
						
						int idCadastroParticipante = rs3.getInt(1);
						
						psAtualizaParticipanteExtensao.setInt(1, idCadastroParticipante);
						psAtualizaParticipanteExtensao.setInt(2, idParticipanteExtensao);
						psAtualizaParticipanteExtensao.addBatch();
						
						System.out.println("Achou cadatro passaporte "+passaporte+" e data de nascimento "+dataNascimento);
						
						//System.out.println("UPDATE extensao.participante_acao_extensao set id_cadastro_participante_atividade_extensao = "+idCadastroParticipante+" WHERE id_participante_acao_extensao = "+idParticipanteExtensao);
						
					}else{
						System.out.println(" [ERRO] Participante sem cadastro: id_participante_acao_extensao = "+idParticipanteExtensao+" CPF= "+CPF+" Pass= "+passaporte+" nasc = "+dataNascimento);
					}
				}else{
					
					String sqlBuscaCadastroByEmail = " select id_cadastro_participante_atividade_extensao "+
							" FROM extensao.cadastro_participante_atividade_extensao where ativo = true AND email = ? ";
					
					PreparedStatement psBuscaCadastroByEmail = connection.prepareStatement(sqlBuscaCadastroByEmail);
					psBuscaCadastroByEmail.setString(1, email);
					
					ResultSet rs3 = psBuscaCadastroByEmail.executeQuery();
					
					if( rs3.next() ){  // encontrou o cadastro pelo Email
						
						int idCadastroParticipante = rs3.getInt(1);
						
						psAtualizaParticipanteExtensao.setInt(1, idCadastroParticipante);
						psAtualizaParticipanteExtensao.setInt(2, idParticipanteExtensao);
						psAtualizaParticipanteExtensao.addBatch();
						
						System.out.println("Achou cadatro pelo email "+email+" ");
						
						//System.out.println("UPDATE extensao.participante_acao_extensao set id_cadastro_participante_atividade_extensao = "+idCadastroParticipante+" WHERE id_participante_acao_extensao = "+idParticipanteExtensao);
						
					}else{
						System.out.println(" [ERRO] Participante sem cadastro: id_participante_acao_extensao = "+idParticipanteExtensao+" CPF= "+CPF+" Pass= "+passaporte+" nasc = "+dataNascimento+" email "+email);
					}
				}
				
			}
		}
		
		
		if(executar){
			System.out.println(" Atualizando os participantes para apontar para o cadastro do participante. ");
			try{
				psAtualizaParticipanteExtensao.executeBatch();
			}catch(SQLException sqlE){
				System.err.println("Se deu erro aqui provavelmente um participante está associado a uma atividade mais de uma vez. Desative 1 deles.");
				throw sqlE;
			}
		}
		
	}

	
	
	
	/**
	 * <p> Migra os participantes que não tinham nenhuma informação, mas tinha um discente associado.</p>
	 *
	 * @param connection
	 * @param executar
	 * @throws SQLException 
	 */
	public static void migraInformacoesCadastroDosParticipantesDiscente(Connection connection, boolean executar) throws SQLException {
		
		System.out.println("Migra os participantes que não tinham nenhuma informação, mas tinha um discente associado..  +- 38.000 participantes  607 segundos sem realizar os inserts.");
		
		String sql = " select pe.cpf_cnpj, pe.passaporte, pe.nome, e.logradouro, pe.email, e.cep, pe.data_nascimento, e.id_unidade_federativa, e.id_municipio, p.data_cadastro "
				+" from extensao.participante_acao_extensao p "
				+" inner join discente d on d.id_discente = p.id_discente "
				+" inner join comum.pessoa pe on pe.id_pessoa = d.id_pessoa "
				+" left join comum.endereco e on e.id_endereco = pe.id_endereco_contato " 
				+" where ativo = true and p.id_discente is not null  and id_cadastro_participante_atividade_extensao is null ";
		
		
		PreparedStatement ps = connection.prepareStatement( sql );
		ps.execute();
		ResultSet rs = ps.getResultSet();
		
		List<CadastroParticipanteAtividadeExtensaoMigracao> cadastros = new ArrayList<CadastroParticipanteAtividadeExtensaoMigracao>();
		
		int contador = 0;
		
		while( rs.next() ){   // Para cada participante criado sem inscrição 
					
			// Ler os dados do particapante //
			Long cpf =  rs.getLong(1);
			
	
			String passaporte =  rs.getString(2);
			String nome =  rs.getString(3);
			String endereco =  rs.getString(4);
			String email =  rs.getString(5);
			String cep =  rs.getString(6);
			Date dataNascimento =  rs.getDate(7);
			Integer idUnidadeFederativa =  rs.getInt(8);
			Integer idMunicipio =  rs.getInt(9);
			Date dataCadastro =  rs.getDate(10);
		
			
			
			CadastroParticipanteAtividadeExtensaoMigracao cadastro = new CadastroParticipanteAtividadeExtensaoMigracao
					(0, cpf, passaporte, cpf == null, nome, dataNascimento, endereco, "0000", "DESCONHECIDO",
							idMunicipio, idUnidadeFederativa, cep, email, UFRNUtils.geraSenhaAleatoria(), "", "",
							"", dataCadastro);
			
			boolean cadastroJaNaLista = false;
			
			for (CadastroParticipanteAtividadeExtensaoMigracao cadastroTemp : cadastros) {
				if( isMesmoCadastros(cadastroTemp, cadastro )  ){ // pelo cpf, email, passaporte ou data nascimento
					cadastro = cadastroTemp;
					cadastroJaNaLista = true;
					break;
				}
			}
			
			
			if(! cadastroJaNaLista){ // é um novo, verifica se ele está persistido
				int idCadatroPersistido = retornaIdCadastrosPersisitdo(connection, executar, cadastro.getCpf(), cadastro.getEmail(), cadastro.getPassaporte(), cadastro.getDataNascimento());
			
				if(idCadatroPersistido > 0){ // se está persistido
					cadastro.setId(idCadatroPersistido);
					cadastro.setCriarNovoCadastro(false);
					if(contador % 100 == 0) System.out.println(contador+" "+cadastro.getNome()+" já está persistido.");
				}else{
					cadastro.setCriarNovoCadastro(true);
					if(contador % 100 == 0) System.out.println(contador+" Criar cadastro para usuário: "+cadastro.getNome()+".");
				}
				
				cadastros.add(cadastro);
			}
			
			contador++;
			
		} // while( rs)
		
		
		
		List<CadastroParticipanteAtividadeExtensaoMigracao> cadastrosInsercao = new ArrayList<CadastroParticipanteAtividadeExtensaoMigracao>();
		
		for (CadastroParticipanteAtividadeExtensaoMigracao cadatro : cadastros) {
		
			if(cadatro.isCriarNovoCadastro()){
				if(executar)
					cadatro.setId( getNextCadastroParticipanteSequence(connection, executar));
				
				cadastrosInsercao.add(cadatro);
			}
		}
		
		try{
			
			System.out.println("Teste inserindo novos "+cadastrosInsercao.size()+" ....... ");
			for (CadastroParticipanteAtividadeExtensaoMigracao cadastro : cadastrosInsercao) {
				System.out.println("Inserindo cadastro: "+cadastro.getNome()+" "+cadastro.getCpf()+" "+cadastro.getPassaporte()+" "+cadastro.getDataNascimento()); 
			}
			
			insereNovoCadastroParticipanteExtensao(connection, executar, cadastrosInsercao);
		}catch(SQLException sqlEx){
			System.err.println("Se deu erro aqui provavelmente participantes com CPFs diferentes possuem o mesmo email. Verificar se não são a mesma pessoa !!! ");
			throw sqlEx;
		}
		
	}

	
	
	/** Associa os Participantes ao Cadastro criado 
	 * @throws SQLException */
	public static void associaParticipantesSemInformacaoMasComDiscente(Connection connection, boolean executar) throws SQLException{
	
		System.out.println("Associando participantes que não tinham nenhuma informação mas tinham discente +- 38.000 ... 2.941 segundos, 50 mim ");
		
		
		String sql = " select  id_participante_acao_extensao, pe.cpf_cnpj, pe.email, pe.passaporte, pe.data_nascimento "+
		" from extensao.participante_acao_extensao p "+
		" inner join discente d on d.id_discente = p.id_discente "+
		" inner join comum.pessoa pe on pe.id_pessoa = d.id_pessoa "+
		" left join comum.endereco e on e.id_endereco = pe.id_endereco_contato "+
		" where ativo = true and p.id_discente is not null  and id_cadastro_participante_atividade_extensao is null ";
		
		String sqlAtualizaParticipanteExtensao = " UPDATE extensao.participante_acao_extensao "+
				" set id_cadastro_participante_atividade_extensao = ? WHERE id_participante_acao_extensao = ? ";
		
		PreparedStatement ps = connection.prepareStatement( sql );
		
		ps.execute();
		ResultSet rs = ps.getResultSet();
		
		PreparedStatement psAtualizaParticipanteExtensao = connection.prepareStatement(sqlAtualizaParticipanteExtensao);
		
		int count = 0;
		
		while( rs.next() ){ // Para cadas inscrição existente 
			
			int idParticipanteExtensao = rs.getInt(1);
			Long CPF = rs.getLong(2);
			String email = rs.getString(3);
			String passaporte = rs.getString(4);
			Date dataNascimento = rs.getDate(5);
			
			String sqlBuscaCadastroByCPF = " select id_cadastro_participante_atividade_extensao "+
					" FROM extensao.cadastro_participante_atividade_extensao where ativo = true AND cpf = ? ";
			
			
			PreparedStatement psBuscaCadastroByCPF = connection.prepareStatement(sqlBuscaCadastroByCPF);
			psBuscaCadastroByCPF.setLong(1, CPF);
			
			ResultSet rs2 = psBuscaCadastroByCPF.executeQuery();
			
			if( CPF > 0 && rs2.next() ){  // encontrou o cadastro pelo CPF
				
				int idCadastroParticipante = rs2.getInt(1);
				
				if(count % 100 == 0)
					System.out.println(count+" Achou cadatro CPF "+CPF);
				
				psAtualizaParticipanteExtensao.setInt(1, idCadastroParticipante);
				psAtualizaParticipanteExtensao.setInt(2, idParticipanteExtensao);
				psAtualizaParticipanteExtensao.addBatch();
				
				//System.out.println("UPDATE extensao.participante_acao_extensao set id_cadastro_participante_atividade_extensao = "+idCadastroParticipante+" WHERE id_participante_acao_extensao = "+idParticipanteExtensao);
				
			}else{ // não tem CPF
			
				if(dataNascimento != null && passaporte != null ){
					String sqlBuscaCadastroByPassaporteDataNascimento = " select id_cadastro_participante_atividade_extensao "+
							" FROM extensao.cadastro_participante_atividade_extensao where ativo = true AND passaporte = ? AND data_nascimento = ? ";
					
					
					PreparedStatement psBuscaCadastroByPassaporteDataNascimento = connection.prepareStatement(sqlBuscaCadastroByPassaporteDataNascimento);
					psBuscaCadastroByPassaporteDataNascimento.setString(1, passaporte);
					psBuscaCadastroByPassaporteDataNascimento.setDate(2, new java.sql.Date(dataNascimento.getTime()));
					ResultSet rs3 = psBuscaCadastroByPassaporteDataNascimento.executeQuery();
					
					if( rs3.next() ){  // encontrou o cadastro pelo passapor e data de nascimento
						
						int idCadastroParticipante = rs3.getInt(1);
						
						psAtualizaParticipanteExtensao.setInt(1, idCadastroParticipante);
						psAtualizaParticipanteExtensao.setInt(2, idParticipanteExtensao);
						psAtualizaParticipanteExtensao.addBatch();
						
						if(count % 100 == 0)
							System.out.println(count+" Achou cadatro passaporte "+passaporte+" e data de nascimento "+dataNascimento);
						
						//System.out.println("UPDATE extensao.participante_acao_extensao set id_cadastro_participante_atividade_extensao = "+idCadastroParticipante+" WHERE id_participante_acao_extensao = "+idParticipanteExtensao);
						
					}else{
						System.out.println(" [ERRO] Participante sem cadastro: id_participante_acao_extensao = "+idParticipanteExtensao+" CPF= "+CPF+" Pass= "+passaporte+" nasc = "+dataNascimento);
					}
				}else{
					
					String sqlBuscaCadastroByEmail = " select id_cadastro_participante_atividade_extensao "+
							" FROM extensao.cadastro_participante_atividade_extensao where ativo = true AND email = ? ";
					
					PreparedStatement psBuscaCadastroByEmail = connection.prepareStatement(sqlBuscaCadastroByEmail);
					psBuscaCadastroByEmail.setString(1, email);
					
					ResultSet rs3 = psBuscaCadastroByEmail.executeQuery();
					
					if( rs3.next() ){  // encontrou o cadastro pelo Email
						
						int idCadastroParticipante = rs3.getInt(1);
						
						psAtualizaParticipanteExtensao.setInt(1, idCadastroParticipante);
						psAtualizaParticipanteExtensao.setInt(2, idParticipanteExtensao);
						psAtualizaParticipanteExtensao.addBatch();
						
						if(count % 100 == 0)
							System.out.println(count+" Achou cadatro pelo email "+email+" ");
						
						//System.out.println("UPDATE extensao.participante_acao_extensao set id_cadastro_participante_atividade_extensao = "+idCadastroParticipante+" WHERE id_participante_acao_extensao = "+idParticipanteExtensao);
						
					}else{
						System.out.println(" [ERRO] Participante sem cadastro: id_participante_acao_extensao = "+idParticipanteExtensao+" CPF= "+CPF+" Pass= "+passaporte+" nasc = "+dataNascimento+" email "+email);
					}
				}
				
			}
			
			count++;
		}
		
		
		if(executar){
			System.out.println(" Atualizando os participantes para apontar para o cadastro do participante. ");
			try{
				psAtualizaParticipanteExtensao.executeBatch();
			}catch(SQLException sqlE){
				System.err.println("Se deu erro aqui provavelmente um participante está associado a uma atividade mais de uma vez. Desative 1 deles.");
				throw sqlE;
			}
		}
		
	}
	
	
	
	
	/**
	 * <p> Migra os participantes que não tinham nenhuma informação, mas tinha um discente associado.</p>
	 *
	 * @param connection
	 * @param executar
	 * @throws SQLException 
	 */
	public static void migraInformacoesCadastroDosParticipantesServidor(Connection connection, boolean executar) throws SQLException {
		
		System.out.println("Migra os participantes que não tinham nenhuma informação, mas tinha um discente associado..  +- 300 participantes  15 segundos sem realizar os inserts.");
		
		String sql = " select pe.cpf_cnpj, pe.passaporte, pe.nome, e.logradouro, pe.email, e.cep, pe.data_nascimento, e.id_unidade_federativa, e.id_municipio, p.data_cadastro "
				+" from extensao.participante_acao_extensao p "
				+" inner join rh.servidor s on s.id_servidor = p.id_servidor "
				+" inner join comum.pessoa pe on pe.id_pessoa = s.id_pessoa "
				+" left join comum.endereco e on e.id_endereco = pe.id_endereco_contato " 
				+" where ativo = true and p.id_servidor is not null  and id_cadastro_participante_atividade_extensao is null ";
		
		
		PreparedStatement ps = connection.prepareStatement( sql );
		ps.execute();
		ResultSet rs = ps.getResultSet();
		
		List<CadastroParticipanteAtividadeExtensaoMigracao> cadastros = new ArrayList<CadastroParticipanteAtividadeExtensaoMigracao>();
		
		int contador = 0;
		
		while( rs.next() ){   // Para cada participante criado sem inscrição 
					
			// Ler os dados do particapante //
			Long cpf =  rs.getLong(1);
			
	
			String passaporte =  rs.getString(2);
			String nome =  rs.getString(3);
			String endereco =  rs.getString(4);
			String email =  rs.getString(5);
			String cep =  rs.getString(6);
			Date dataNascimento =  rs.getDate(7);
			Integer idUnidadeFederativa =  rs.getInt(8);
			Integer idMunicipio =  rs.getInt(9);
			Date dataCadastro =  rs.getDate(10);
		
			
			
			CadastroParticipanteAtividadeExtensaoMigracao cadastro = new CadastroParticipanteAtividadeExtensaoMigracao
					(0, cpf, passaporte, cpf == null, nome, dataNascimento, endereco, "0000", "DESCONHECIDO",
							idMunicipio, idUnidadeFederativa, cep, email, UFRNUtils.geraSenhaAleatoria(), "", "",
							"", dataCadastro);
			
			boolean cadastroJaNaLista = false;
			
			for (CadastroParticipanteAtividadeExtensaoMigracao cadastroTemp : cadastros) {
				if( isMesmoCadastros(cadastroTemp, cadastro )  ){ // pelo cpf, email, passaporte ou data nascimento
					cadastro = cadastroTemp;
					cadastroJaNaLista = true;
					break;
				}
			}
			
			
			if(! cadastroJaNaLista){ // é um novo, verifica se ele está persistido
				int idCadatroPersistido = retornaIdCadastrosPersisitdo(connection, executar, cadastro.getCpf(), cadastro.getEmail(), cadastro.getPassaporte(), cadastro.getDataNascimento());
			
				if(idCadatroPersistido > 0){ // se está persistido
					cadastro.setId(idCadatroPersistido);
					cadastro.setCriarNovoCadastro(false);
					if(contador % 100 == 0) System.out.println(contador+" "+cadastro.getNome()+" já está persistido.");
				}else{
					cadastro.setCriarNovoCadastro(true);
					if(contador % 100 == 0) System.out.println(contador+" Criar cadastro para usuário: "+cadastro.getNome()+".");
				}
				
				cadastros.add(cadastro);
			}
			
			contador++;
			
		} // while( rs)
		
		
		
		List<CadastroParticipanteAtividadeExtensaoMigracao> cadastrosInsercao = new ArrayList<CadastroParticipanteAtividadeExtensaoMigracao>();
		
		for (CadastroParticipanteAtividadeExtensaoMigracao cadatro : cadastros) {
		
			if(cadatro.isCriarNovoCadastro()){
				if(executar)
					cadatro.setId( getNextCadastroParticipanteSequence(connection, executar));
				
				cadastrosInsercao.add(cadatro);
			}
		}
		
		try{
			
			insereNovoCadastroParticipanteExtensao(connection, executar, cadastrosInsercao);
		}catch(SQLException sqlEx){
			System.err.println("Se deu erro aqui provavelmente participantes com CPFs diferentes possuem o mesmo email. Verificar se não são a mesma pessoa !!! ");
			throw sqlEx;
		}
		
	}

	
	
	
	/** Associa os Participantes ao Cadastro criado 
	 * @throws SQLException */
	public static void associaParticipantesSemInformacaoMasComServidor(Connection connection, boolean executar) throws SQLException{
	
		System.out.println("Associando participantes que não tinham nenhuma informação mas tinham discente +- 300 ... 38 segundos se realizar os inserts ");
		
		
		String sql = " select  id_participante_acao_extensao, pe.cpf_cnpj, pe.email, pe.passaporte, pe.data_nascimento "+
		" from extensao.participante_acao_extensao p "+
		" inner join rh.servidor s on s.id_servidor = p.id_servidor "+
		" inner join comum.pessoa pe on pe.id_pessoa = s.id_pessoa "+
		" left join comum.endereco e on e.id_endereco = pe.id_endereco_contato "+
		" where ativo = true and p.id_servidor is not null  and id_cadastro_participante_atividade_extensao is null ";
		
		String sqlAtualizaParticipanteExtensao = " UPDATE extensao.participante_acao_extensao "+
				" set id_cadastro_participante_atividade_extensao = ? WHERE id_participante_acao_extensao = ? ";
		
		PreparedStatement ps = connection.prepareStatement( sql );
		
		ps.execute();
		ResultSet rs = ps.getResultSet();
		
		PreparedStatement psAtualizaParticipanteExtensao = connection.prepareStatement(sqlAtualizaParticipanteExtensao);
		
		int count = 0;
		
		while( rs.next() ){ // Para cadas inscrição existente 
			
			int idParticipanteExtensao = rs.getInt(1);
			Long CPF = rs.getLong(2);
			String email = rs.getString(3);
			String passaporte = rs.getString(4);
			Date dataNascimento = rs.getDate(5);
			
			String sqlBuscaCadastroByCPF = " select id_cadastro_participante_atividade_extensao "+
					" FROM extensao.cadastro_participante_atividade_extensao where ativo = true AND cpf = ? ";
			
			
			PreparedStatement psBuscaCadastroByCPF = connection.prepareStatement(sqlBuscaCadastroByCPF);
			psBuscaCadastroByCPF.setLong(1, CPF);
			
			ResultSet rs2 = psBuscaCadastroByCPF.executeQuery();
			
			if( CPF > 0 && rs2.next() ){  // encontrou o cadastro pelo CPF
				
				int idCadastroParticipante = rs2.getInt(1);
				
				if(count % 100 == 0)
					System.out.println(count+" Achou cadatro CPF "+CPF);
				
				psAtualizaParticipanteExtensao.setInt(1, idCadastroParticipante);
				psAtualizaParticipanteExtensao.setInt(2, idParticipanteExtensao);
				psAtualizaParticipanteExtensao.addBatch();
				
				//System.out.println("UPDATE extensao.participante_acao_extensao set id_cadastro_participante_atividade_extensao = "+idCadastroParticipante+" WHERE id_participante_acao_extensao = "+idParticipanteExtensao);
				
			}else{ // não tem CPF
			
				if(dataNascimento != null && passaporte != null ){
					String sqlBuscaCadastroByPassaporteDataNascimento = " select id_cadastro_participante_atividade_extensao "+
							" FROM extensao.cadastro_participante_atividade_extensao where ativo = true AND passaporte = ? AND data_nascimento = ? ";
					
					
					PreparedStatement psBuscaCadastroByPassaporteDataNascimento = connection.prepareStatement(sqlBuscaCadastroByPassaporteDataNascimento);
					psBuscaCadastroByPassaporteDataNascimento.setString(1, passaporte);
					psBuscaCadastroByPassaporteDataNascimento.setDate(2, new java.sql.Date(dataNascimento.getTime()));
					ResultSet rs3 = psBuscaCadastroByPassaporteDataNascimento.executeQuery();
					
					if( rs3.next() ){  // encontrou o cadastro pelo passapor e data de nascimento
						
						int idCadastroParticipante = rs3.getInt(1);
						
						psAtualizaParticipanteExtensao.setInt(1, idCadastroParticipante);
						psAtualizaParticipanteExtensao.setInt(2, idParticipanteExtensao);
						psAtualizaParticipanteExtensao.addBatch();
						
						if(count % 100 == 0)
							System.out.println(count+" Achou cadatro passaporte "+passaporte+" e data de nascimento "+dataNascimento);
						
						//System.out.println("UPDATE extensao.participante_acao_extensao set id_cadastro_participante_atividade_extensao = "+idCadastroParticipante+" WHERE id_participante_acao_extensao = "+idParticipanteExtensao);
						
					}else{
						System.out.println(" [ERRO] Participante sem cadastro: id_participante_acao_extensao = "+idParticipanteExtensao+" CPF= "+CPF+" Pass= "+passaporte+" nasc = "+dataNascimento);
					}
				}else{
					
					String sqlBuscaCadastroByEmail = " select id_cadastro_participante_atividade_extensao "+
							" FROM extensao.cadastro_participante_atividade_extensao where ativo = true AND email = ? ";
					
					PreparedStatement psBuscaCadastroByEmail = connection.prepareStatement(sqlBuscaCadastroByEmail);
					psBuscaCadastroByEmail.setString(1, email);
					
					ResultSet rs3 = psBuscaCadastroByEmail.executeQuery();
					
					if( rs3.next() ){  // encontrou o cadastro pelo Email
						
						int idCadastroParticipante = rs3.getInt(1);
						
						psAtualizaParticipanteExtensao.setInt(1, idCadastroParticipante);
						psAtualizaParticipanteExtensao.setInt(2, idParticipanteExtensao);
						psAtualizaParticipanteExtensao.addBatch();
						
						if(count % 100 == 0)
							System.out.println(count+" Achou cadatro pelo email "+email+" ");
						
						//System.out.println("UPDATE extensao.participante_acao_extensao set id_cadastro_participante_atividade_extensao = "+idCadastroParticipante+" WHERE id_participante_acao_extensao = "+idParticipanteExtensao);
						
					}else{
						System.out.println(" [ERRO] Participante sem cadastro: id_participante_acao_extensao = "+idParticipanteExtensao+" CPF= "+CPF+" Pass= "+passaporte+" nasc = "+dataNascimento+" email "+email);
					}
				}
				
			}
			
			count++;
		}
		
		
		if(executar){
			System.out.println(" Atualizando os participantes para apontar para o cadastro do participante. ");
			try{
				psAtualizaParticipanteExtensao.executeBatch();
			}catch(SQLException sqlE){
				System.err.println("Se deu erro aqui provavelmente um participante está associado a uma atividade mais de uma vez. Desative 1 deles.");
				throw sqlE;
			}
		}
		
	}
	
	
	
	/** <p>Associa os Participantes que ficaram sem informção ao cadastro, isso ocorreu porque ele não tem nem CPF nem passaporte.
	 * E alguns parecem ser estrageiros, então vão ser migrados como participantes extrageiros, criando-se cpf aleatórios para eles.
	 * para não perder a informação.
	 * </p>
	 * 
	 * @throws SQLException */
	public static void migraInformacoesCadastroDosParticipantesSemInformacao(Connection connection, boolean executar) throws SQLException{
	
		System.out.println("Associando participantes que não tinham nenhuma informação +- 1751 ... 1 segundos se realizar os inserts ");
		
		
		String sql = " select id_participante_acao_extensao, nome, endereco, email, data_nascimento, id_discente "
				+" from extensao.participante_acao_extensao  where id_cadastro_participante_atividade_extensao is null and ativo = true; ";
		
		
		PreparedStatement ps = connection.prepareStatement( sql );
		ps.execute();
		ResultSet rs = ps.getResultSet();
		
		List<CadastroParticipanteAtividadeExtensaoMigracao> cadastros = new ArrayList<CadastroParticipanteAtividadeExtensaoMigracao>();
		
		final int idMunicipioPadrao = 1171;
		final int idUnidadeFederativaPadrao = 24;
		
		
		int contador = 0;
		 
		while( rs.next() ){   // Para cada participante criado sem inscrição 
					
			int idParticipanteExtensao = rs.getInt(1);
			String nome = rs.getString(2);
			String endereco = rs.getString(3);
			String email = rs.getString(4);
			Date dataNascimento = rs.getDate(5);
			int idDiscente = rs.getInt(6);
		
			if(idDiscente > 0 && ( dataNascimento == null || StringUtils.isEmpty(nome) )){ // a única informação válida que tem nesses discente é a data de nascimento e o nome
				
				String sqlBuscaDiscente = " select p.data_nascimento, p.nome "+
				" from discente d "+
				" inner join comum.pessoa p on p.id_pessoa = d.id_pessoa "+
				" left join comum.usuario u on u.id_pessoa = p.id_pessoa "+
				" where id_discente = ? ";
				
				PreparedStatement psDiscente = connection.prepareStatement( sqlBuscaDiscente );
				psDiscente.setInt(1, idDiscente);
				psDiscente.execute();
				ResultSet rsDiscente = psDiscente.getResultSet();
				
				while( rsDiscente.next() ){  
					if( dataNascimento == null ){
						dataNascimento = rsDiscente.getDate(1);
					}
					if(StringUtils.isEmpty(nome) )
						nome = rsDiscente.getString(2);
				}
				
			}
			
			CadastroParticipanteAtividadeExtensaoMigracao cadastro = new CadastroParticipanteAtividadeExtensaoMigracao
					(0, null, UFRNUtils.geraSenhaAleatoria(), true, nome, dataNascimento, endereco, "0000", "DESCONHECIDO",
							idMunicipioPadrao, idUnidadeFederativaPadrao, "59000000", email, UFRNUtils.geraSenhaAleatoria(), "", "",
							"", new Date() );
			
			
			
			boolean cadastroJaNaLista = false;
			
			for (CadastroParticipanteAtividadeExtensaoMigracao cadastroTemp : cadastros) {
				if( isMesmoCadastrosSoPeloNome(cadastroTemp, cadastro )  ){ // apenas pelo nome, já que não tem cpf nem passaporte
					cadastro = cadastroTemp;
					cadastroJaNaLista = true;
					break;
				}
			}
			
			
			if(! cadastroJaNaLista){ // é um novo, verifica se ele está persistido
				cadastro.setCriarNovoCadastro(true);
				cadastros.add(cadastro);
			}
			
			cadastro.getIdsParticipantesExtensaoAssociadosAoCadastro().add(idParticipanteExtensao);
			
			contador++;
			
		} // while( rs)
		
		
		System.out.println(" Qtd participantes sem cadastro "+contador);
		
		
		List<CadastroParticipanteAtividadeExtensaoMigracao> cadastrosInsercao = new ArrayList<CadastroParticipanteAtividadeExtensaoMigracao>();
		
		for (CadastroParticipanteAtividadeExtensaoMigracao cadatro : cadastros) {
		
			if(cadatro.isCriarNovoCadastro()){
				if(executar)
					cadatro.setId( getNextCadastroParticipanteSequence(connection, executar));
				
				cadastrosInsercao.add(cadatro);
			}
		}
		
		try{
			
			insereNovoCadastroParticipanteExtensao(connection, executar, cadastrosInsercao);
		}catch(SQLException sqlEx){
			System.err.println("Se deu erro aqui provavelmente participantes com CPFs diferentes possuem o mesmo email. Verificar se não são a mesma pessoa !!! ");
			throw sqlEx;
		}
		
		
		
		String sqlAtualizaParticipanteExtensao = " UPDATE extensao.participante_acao_extensao "+
				" set id_cadastro_participante_atividade_extensao = ? WHERE id_participante_acao_extensao = ? ";
		
		PreparedStatement psAtualizaParticipanteExtensao = connection.prepareStatement(sqlAtualizaParticipanteExtensao);
		
		
		int contadorParticipantes = 0;
		int contadorCadastrosInseridos = 0;
		
		for (CadastroParticipanteAtividadeExtensaoMigracao cadastroParticipante : cadastrosInsercao) {
			
			if(cadastroParticipante.isCriarNovoCadastro()){
				
				// Para casa participante pertencente ao cadastro
				for (Integer idParticipante : cadastroParticipante.getIdsParticipantesExtensaoAssociadosAoCadastro()) {
					psAtualizaParticipanteExtensao.setInt(1, cadastroParticipante.getId());
					psAtualizaParticipanteExtensao.setInt(2, idParticipante );
					psAtualizaParticipanteExtensao.addBatch();
				}
				
				contadorParticipantes += cadastroParticipante.getIdsParticipantesExtensaoAssociadosAoCadastro().size();
				contadorCadastrosInseridos++;
			}
			
		}
		
		System.out.println(" Qtd de cadastros inseridos "+contadorCadastrosInseridos+"\n");
		
		System.out.println(" Qtd de participantes associados "+contadorParticipantes);
		
		if(executar){
			System.out.println(" Atualizando os participantes para apontar para o cadastro do participante. ");
			try{
				psAtualizaParticipanteExtensao.executeBatch();
			}catch(SQLException sqlE){
				System.err.println("Se deu erro aqui provavelmente um participante está associado a uma atividade mais de uma vez. Desative 1 deles.");
				throw sqlE;
			}
		}
		
	}
	
	
	
	/**
	 * TOMAR COIDADO AO USAR ESSA FUNÇÃO
	 * 
	 * Só chamar depois que a migração estiver toda feita e sem erro.
	 *  
	 * @param connection
	 * @param executar
	 * @throws SQLException 
	 */
	public static void apagaColunasNaoMaisUsadas(Connection connection, boolean executar) throws SQLException {
		
		System.out.println("Apagando as colunas não usadas, essa operação não poderá ser desfeita. ");
		
		String sql1 = " ALTER TABLE extensao.curso_evento DROP COLUMN cobranca_taxa_matricula ";
		String sql2 = " ALTER TABLE extensao.curso_evento DROP COLUMN taxa_matricula ";
		String sql3 = " ALTER TABLE extensao.curso_evento DROP COLUMN data_vencimento_gru ";
		
		String sql4 = " ALTER TABLE extensao.inscricao_atividade_participante DROP COLUMN ativo ";
		String sql5 = " ALTER TABLE extensao.inscricao_atividade_participante DROP COLUMN cpf ";
		String sql6 = " ALTER TABLE extensao.inscricao_atividade_participante DROP COLUMN passaporte ";
		String sql7 = " ALTER TABLE extensao.inscricao_atividade_participante DROP COLUMN nome ";
		String sql8 = " ALTER TABLE extensao.inscricao_atividade_participante DROP COLUMN data_nascimento ";
		String sql9 = " ALTER TABLE extensao.inscricao_atividade_participante DROP COLUMN logradouro ";
		String sql10 = " ALTER TABLE extensao.inscricao_atividade_participante DROP COLUMN numero ";
		String sql11 = " ALTER TABLE extensao.inscricao_atividade_participante DROP COLUMN id_municipio ";
		String sql12 = " ALTER TABLE extensao.inscricao_atividade_participante DROP COLUMN id_unidade_federativa ";
		String sql13 = " ALTER TABLE extensao.inscricao_atividade_participante DROP COLUMN cep ";
		String sql14 = " ALTER TABLE extensao.inscricao_atividade_participante DROP COLUMN email ";
		String sql15 = " ALTER TABLE extensao.inscricao_atividade_participante DROP COLUMN telefone ";
		String sql16 = " ALTER TABLE extensao.inscricao_atividade_participante DROP COLUMN celular ";
		String sql17 = " ALTER TABLE extensao.inscricao_atividade_participante DROP COLUMN senha ";
		String sql18 = " ALTER TABLE extensao.inscricao_atividade_participante DROP COLUMN codigo_acesso ";
		
		String sql19 = " ALTER TABLE extensao.participante_acao_extensao DROP COLUMN id_discente ";
		String sql20 = " ALTER TABLE extensao.participante_acao_extensao DROP COLUMN id_servidor ";
		String sql21 = " ALTER TABLE extensao.participante_acao_extensao DROP COLUMN cpf ";
		String sql22 = " ALTER TABLE extensao.participante_acao_extensao DROP COLUMN passaporte ";
		String sql23 = " ALTER TABLE extensao.participante_acao_extensao DROP COLUMN nome ";
		String sql24 = " ALTER TABLE extensao.participante_acao_extensao DROP COLUMN data_nascimento ";
		String sql25 = " ALTER TABLE extensao.participante_acao_extensao DROP COLUMN instituicao ";
		String sql26 = " ALTER TABLE extensao.participante_acao_extensao DROP COLUMN endereco ";
		String sql27 = " ALTER TABLE extensao.participante_acao_extensao DROP COLUMN id_municipio ";
		String sql28 = " ALTER TABLE extensao.participante_acao_extensao DROP COLUMN id_unidade_federativa ";
		String sql29 = " ALTER TABLE extensao.participante_acao_extensao DROP COLUMN cep ";
		String sql30 = " ALTER TABLE extensao.participante_acao_extensao DROP COLUMN email ";
		
		

		String sql31 = " ALTER TABLE extensao.inscricao_atividade_participante DROP COLUMN id_discente ";
		String sql32 = " ALTER TABLE extensao.inscricao_atividade_participante DROP COLUMN id_servidor ";
		
		String sql33 = " ALTER TABLE extensao.inscricao_atividade_participante DROP COLUMN bairro ";
		
		
		PreparedStatement ps1 = connection.prepareStatement( sql1 );
		PreparedStatement ps2 = connection.prepareStatement( sql2 );
		PreparedStatement ps3 = connection.prepareStatement( sql3 );
		
		PreparedStatement ps4 = connection.prepareStatement( sql4 );
		PreparedStatement ps5 = connection.prepareStatement( sql5 );
		PreparedStatement ps6 = connection.prepareStatement( sql6 );
		PreparedStatement ps7 = connection.prepareStatement( sql7 );
		PreparedStatement ps8 = connection.prepareStatement( sql8 );
		PreparedStatement ps9 = connection.prepareStatement( sql9 );
		PreparedStatement ps10 = connection.prepareStatement( sql10 );
		PreparedStatement ps11 = connection.prepareStatement( sql11 );
		PreparedStatement ps12 = connection.prepareStatement( sql12 );
		PreparedStatement ps13 = connection.prepareStatement( sql13 );
		PreparedStatement ps14 = connection.prepareStatement( sql14 );
		PreparedStatement ps15 = connection.prepareStatement( sql15 );
		PreparedStatement ps16 = connection.prepareStatement( sql16 );
		PreparedStatement ps17 = connection.prepareStatement( sql17 );
		PreparedStatement ps18 = connection.prepareStatement( sql18 );
		
		
		PreparedStatement ps19 = connection.prepareStatement( sql19 );
		PreparedStatement ps20 = connection.prepareStatement( sql20 );
		PreparedStatement ps21 = connection.prepareStatement( sql21 );
		PreparedStatement ps22 = connection.prepareStatement( sql22 );
		PreparedStatement ps23 = connection.prepareStatement( sql23 );
		PreparedStatement ps24 = connection.prepareStatement( sql24 );
		PreparedStatement ps25 = connection.prepareStatement( sql25 );
		PreparedStatement ps26 = connection.prepareStatement( sql26 );
		PreparedStatement ps27 = connection.prepareStatement( sql27 );
		PreparedStatement ps28 = connection.prepareStatement( sql28 );
		PreparedStatement ps29 = connection.prepareStatement( sql29 );
		PreparedStatement ps30 = connection.prepareStatement( sql30 );
		
		PreparedStatement ps31 = connection.prepareStatement( sql31 );
		PreparedStatement ps32 = connection.prepareStatement( sql32 );
		PreparedStatement ps33 = connection.prepareStatement( sql33 );
		
		if(executar){
			ps1.executeUpdate();
			ps2.executeUpdate();
			ps3.executeUpdate();
			ps4.executeUpdate();
			ps5.executeUpdate();
			ps6.executeUpdate();
			ps7.executeUpdate();
			ps8.executeUpdate();
			ps9.executeUpdate();
			ps10.executeUpdate();
			ps11.executeUpdate();
			ps12.executeUpdate();
			ps13.executeUpdate();
			ps14.executeUpdate();
			ps15.executeUpdate();
			ps16.executeUpdate();
			ps17.executeUpdate();
			ps18.executeUpdate();
			ps19.executeUpdate();
			ps20.executeUpdate();
			ps21.executeUpdate();
			ps22.executeUpdate();
			ps23.executeUpdate();
			ps24.executeUpdate();
			ps25.executeUpdate();
			ps26.executeUpdate();
			ps27.executeUpdate();
			ps28.executeUpdate();
			ps29.executeUpdate();
			ps30.executeUpdate();
			
			ps31.executeUpdate();
			ps32.executeUpdate();
			ps33.executeUpdate();
		}
		
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
	
	
	
	public static boolean isMesmoCadastros(CadastroParticipanteAtividadeExtensaoMigracao c1, CadastroParticipanteAtividadeExtensaoMigracao c2){
		// se tiver o mesmo cpf são iguais
		if(c1.getCpf() != null && c2.getCpf() != null)
			return c1.getCpf().equals(c2.getCpf());
		
		// se não se tiver o mesmo emails são iguais
		if(c1.getEmail() != null && c2.getEmail() != null)
			return c1.getEmail().equals(c2.getEmail());
		
		// se não, se tiver o mesmo passaporte e data de nascimento são iguais //
		if(c1.getPassaporte() != null && c2.getPassaporte() != null && c1.getDataNascimento() != null && c2.getDataNascimento() != null)
			return c1.getPassaporte().equals(c2.getPassaporte()) && c1.getDataNascimento().equals(c2.getDataNascimento());
		
		return false;
	}
	
	
	public static boolean isMesmoCadastrosSoPeloNome(CadastroParticipanteAtividadeExtensaoMigracao c1, CadastroParticipanteAtividadeExtensaoMigracao c2){
		// se tiver o mesmo cpf são iguais
		if(c1.getNome() != null && c2.getNome() != null)
			return c1.getNome().equals(c2.getNome());
		return false;
	}
	
}


///** Usado na migração de participantse que não possuem cadastro nem estão associados a uma incrição no sistema */
//class ParticipantaAtividadeExtensaoMigracao{
//	
//	/// As informaçõse do participante //
//	private int idParticipanteAcaoExtensao;
////	private Long cpf;
////	private String passaporte;
////	private String nome;
////	private String endereco;
////	private String email;
////	private String cep;
////	private Date dataNascimento;
////	private Integer idUnidadeFederativa;
////	private Integer idMunicipio;
////	private Date dataCadastro;
//	
//	private String instituicao;
//	private Integer idAcaoExtensao;
//	private Integer idSubAtividadeExtensao;
//
//	// As inscrições que o participante relalizou 
//	private Integer idInscricaoAtividade;
//	
//	public ParticipantaAtividadeExtensaoMigracao(int idParticipanteAcaoExtensao, int idInscricaoAtividade, String instituicao,Integer idAcaoExtensao, Integer idSubAtividadeExtensao) {
//		this.idParticipanteAcaoExtensao=  idParticipanteAcaoExtensao;
//		this.instituicao = instituicao;
//		this.idAcaoExtensao = idAcaoExtensao;
//		this.idSubAtividadeExtensao = idSubAtividadeExtensao;
//		this.idInscricaoAtividade = idInscricaoAtividade;
//	}
//
//	
//
//
////	public boolean isCriarNovoCadastro() {
////		return criarNovoCadastro;
////	}
////
////	public void setCriarNovoCadastro(boolean criarNovoCadastro) {
////		this.criarNovoCadastro = criarNovoCadastro;
////	}
//
//
//	public int getIdParticipanteAcaoExtensao() {
//		return idParticipanteAcaoExtensao;
//	}
//
//
//
//
//	public void setIdParticipanteAcaoExtensao(int idParticipanteAcaoExtensao) {
//		this.idParticipanteAcaoExtensao = idParticipanteAcaoExtensao;
//	}
//
//
//
//
//	public Integer getIdInscricaoAtividade() {
//		return idInscricaoAtividade;
//	}
//
//
//
//
//	public void setIdInscricaoAtividade(Integer idInscricaoAtividade) {
//		this.idInscricaoAtividade = idInscricaoAtividade;
//	}
//
//
//
//
//	public void setIdAcaoExtensao(Integer idAcaoExtensao) {
//		this.idAcaoExtensao = idAcaoExtensao;
//	}
//
//
//
//
//	public void setIdSubAtividadeExtensao(Integer idSubAtividadeExtensao) {
//		this.idSubAtividadeExtensao = idSubAtividadeExtensao;
//	}
//
//
//
//
//	public String getInstituicao() {
//		return instituicao;
//	}
//
//	public Integer getIdAcaoExtensao() {
//		return idAcaoExtensao;
//	}
//
//	public Integer getIdSubAtividadeExtensao() {
//		return idSubAtividadeExtensao;
//	}
//
//	public void setInstituicao(String instituicao) {
//		this.instituicao = instituicao;
//	}
//	
//	
//}






/**
 * <p>Classe usada apenas na migração, já que não dá para instanciar  a classe nomal por causa do parâmetro em unidade federativa</p>
 *
 * 
 * @author jadson
 *
 */
class CadastroParticipanteAtividadeExtensaoMigracao{
	
  private int id;	
	
   private Long cpf;
   
   private String passaporte;

   private String nome;

   private String nomeAscii;
   
   private Date dataNascimento;

   private String logradouro;
   
   private String complemento;
   
   private String numero;

   private String bairro;
   
   private Integer municipio;
   
   private Integer unidadeFederativa;

   private String cep;
   
   private String email;
   
   private String telefone;
	
   private String celular;

   private String senha;

   private String senhaGerada;
   
   private boolean estrangeiro = false;
   
   
   private Date dataCadastro;

   
   private String codigoAcessoConfirmacao;
	
   /** O participante associado ao cadatro, utilizado na migração de participantes sem inscrição */
   private Integer idParticipanteAcaoExtensao;
   
   private boolean criarNovoCadastro = false;
   
   /** Usado na migração das informações dos participantes de extensao, para o respecitivo cadastro.*/
   private List<Integer> idsParticipantesExtensaoAssociadosAoCadastro = new ArrayList<Integer>();
   
   
   public void geraHashSenha(){
   		this.senha = UFRNUtils.toMD5(senha);
   }

	public String geraSenhaAutomatica(){
		String senhaTemp = UFRNUtils.toMD5(UFRNUtils.geraSenhaAleatoria() );
		if(senhaTemp.length() > 10)
			return senhaTemp.substring(0, 10);
		else
			return senhaTemp;
	}

	public CadastroParticipanteAtividadeExtensaoMigracao(int id) {
		this.id = id;
	}
	
	public CadastroParticipanteAtividadeExtensaoMigracao(int id, Long cpf,
		String passaporte, boolean estrangeiro, String nome, Date dataNascimento, String logradouro,
		String numero, String bairro, Integer municipio,
		Integer unidadeFederativa, String cep, String email, String senha, String telefone,
		String celular,  String codigoAcessoConfirmacao, Date dataCadastro) {
	super();
	
	this.id = id;
	
	this.estrangeiro = estrangeiro;
	
	if(cpf != null && cpf > 0)
		this.cpf = cpf;
	
	this.passaporte = passaporte;
	this.nome = nome;
	
	if(StringUtils.isEmpty(this.nome)){
		this.nome = "USUÁRIO MIGRADO SEM NOME";
	}
	
	this.nomeAscii = StringUtils.toAsciiAndUpperCase(this.nome); 
	
	this.dataNascimento = dataNascimento;
	
	if(this.dataNascimento == null)
		this.dataNascimento = CalendarUtils.createDate(1, 1, 1900);
		
	this.logradouro = logradouro;
	if(StringUtils.isEmpty(this.logradouro)){
		this.logradouro = "ENDEREÇO DESCONHECIDO";
	}
	
	// alguns endereços então muito grandes, acredito que seja o mascaramento.
	if( this.logradouro.length() > 60 )
		this.logradouro = this.logradouro.substring(0, 60);
		
	this.numero = numero;
	if(StringUtils.isEmpty(this.numero)){
		this.numero = "S?N";
	}
	
	if(this.numero.length() > 6 ){ // provavelmente tem complemento aqui também, além do número !!!
		
		
		// Tenta extrair os 6 primeiros dígitos
		String numeroTemp = "";
		int i = 0;
		for (; i < 6; i++) {
			if( Character.isDigit(this.numero.charAt(i)) )
				numeroTemp += this.numero.charAt(i);
			else{
				break;
			}
		}
		
		complemento = numero.substring(i, numero.length());
		this.numero  = numeroTemp;
	}
	
	this.bairro = bairro;
	
	if(StringUtils.isEmpty(this.bairro)){
		this.bairro = "DESCONHECIDO";
	}
	
	this.municipio = municipio;
	
	if(this.municipio == null)
		this.municipio = 1171;  // NATAL
	
	this.unidadeFederativa = unidadeFederativa;
	
	if(this.unidadeFederativa == null)
		this.unidadeFederativa = 24; // RN
	
	this.cep = cep;
	
	// retira pontos do cep, que por acaso existam //
	if(this.cep != null && this.cep.contains("."))
		this.cep = this.cep.replace(".", "");
	
	// cep inválido //
	if(this.cep != null && this.cep.length() > 9){
		this.cep = this.cep.substring(0, 9);
	}
	
	
	if(StringUtils.isEmpty(this.cep)){
		this.cep = "59000-000";
	}
	
	this.email = email;
	
	// cria um email fictício para não quebrar a restrição, se esse cadastro realmente existir, vai ter que consertar no banco.
	if(StringUtils.isEmpty(this.email)){
		this.email = this.nome.replaceAll("( )+", "_").toLowerCase().trim()+"@ufrn.br";
	}
	
	this.telefone = telefone;
	this.celular = celular;
	this.senha = senha;
	
	if(StringUtils.isEmpty(this.senha)){
		this.senha = geraSenhaAutomatica();
	}
	
	this.senhaGerada = senha;
	this.dataCadastro = dataCadastro;
	this.codigoAcessoConfirmacao = codigoAcessoConfirmacao;
}

	
	
	
//  public void adicionaParticipanteAssociadosAoCadastro(Integer idParticipante){
//	if(idsParticipantesExtensaoAssociadosAoCadastro == null)
//		idsParticipantesExtensaoAssociadosAoCadastro = new ArrayList<Integer>();
//	idsParticipantesExtensaoAssociadosAoCadastro.add(idParticipante);
//  }
// 
// 
//
//
//  public List<Integer> getIdsParticipantesExtensaoAssociadosAoCadastro() {
//	return idsParticipantesExtensaoAssociadosAoCadastro;
//  }

//	public Integer getIdParticipanteAcaoExtensao() {
//		return idParticipanteAcaoExtensao;
//	}
//
//	public void setIdParticipanteAcaoExtensao(Integer idParticipanteAcaoExtensao) {
//		this.idParticipanteAcaoExtensao = idParticipanteAcaoExtensao;
//	}

@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((cpf == null) ? 0 : cpf.hashCode());
	return result;
}



@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	CadastroParticipanteAtividadeExtensaoMigracao other = (CadastroParticipanteAtividadeExtensaoMigracao) obj;
	if (cpf == null) {
		if (other.cpf != null)
			return false;
	} else if (!cpf.equals(other.cpf))
		return false;
	return true;
}







@Override
public String toString() {
	return "CadastroParticipanteAtividadeExtensaoMigracao [id=" + id + ", cpf="
			+ cpf + ", passaporte=" + passaporte + ", nome=" + nome
			+ ", nomeAscii=" + nomeAscii + ", dataNascimento=" + dataNascimento
			+ ", logradouro=" + logradouro + ", complemento=" + complemento
			+ ", numero=" + numero + ", bairro=" + bairro + ", municipio="
			+ municipio + ", unidadeFederativa=" + unidadeFederativa + ", cep="
			+ cep + ", email=" + email + ", telefone=" + telefone
			+ ", celular=" + celular + ", senha=" + senha + ", senhaGerada="
			+ senhaGerada + ", estrangeiro=" + estrangeiro + ", dataCadastro="
			+ dataCadastro + ", codigoAcessoConfirmacao="
			+ codigoAcessoConfirmacao + ", idParticipanteAcaoExtensao="
			+ idParticipanteAcaoExtensao + ", criarNovoCadastro="
			+ criarNovoCadastro
			+ ", idsParticipantesExtensaoAssociadosAoCadastro="
			+ idsParticipantesExtensaoAssociadosAoCadastro + "]";
}


public Long getCpf() {
	return cpf;
}


public void setCpf(Long cpf) {
	this.cpf = cpf;
}


public String getPassaporte() {
	return passaporte;
}


public void setPassaporte(String passaporte) {
	this.passaporte = passaporte;
}

public boolean isCriarNovoCadastro() {
	return criarNovoCadastro;
}

public void setCriarNovoCadastro(boolean criarNovoCadastro) {
	this.criarNovoCadastro = criarNovoCadastro;
}

public String getNome() {
	return nome;
}

public String getNomeAscii() {
	return nomeAscii;
}

public void setNome(String nome) {
	this.nome = nome;
}


public Date getDataNascimento() {
	return dataNascimento;
}


public void setDataNascimento(Date dataNascimento) {
	this.dataNascimento = dataNascimento;
}


public String getLogradouro() {
	return logradouro;
}


public void setLogradouro(String logradouro) {
	this.logradouro = logradouro;
}


public String getNumero() {
	return numero;
}


public void setNumero(String numero) {
	this.numero = numero;
}


public String getBairro() {
	return bairro;
}


public void setBairro(String bairro) {
	this.bairro = bairro;
}


public Integer getMunicipio() {
	return municipio;
}


public void setMunicipio(Integer municipio) {
	this.municipio = municipio;
}


public Integer getUnidadeFederativa() {
	return unidadeFederativa;
}


public void setUnidadeFederativa(Integer unidadeFederativa) {
	this.unidadeFederativa = unidadeFederativa;
}


public String getCep() {
	return cep;
}


public void setCep(String cep) {
	this.cep = cep;
}


public String getEmail() {
	return email;
}


public void setEmail(String email) {
	this.email = email;
}


public String getTelefone() {
	return telefone;
}


public void setTelefone(String telefone) {
	this.telefone = telefone;
}


public String getCelular() {
	return celular;
}


public void setCelular(String celular) {
	this.celular = celular;
}


public String getSenha() {
	return senha;
}


public void setSenha(String senha) {
	this.senha = senha;
}


public String getSenhaGerada() {
	return senhaGerada;
}


public void setSenhaGerada(String senhaGerada) {
	this.senhaGerada = senhaGerada;
}


public boolean isEstrangeiro() {
	return estrangeiro;
}


public void setEstrangeiro(boolean estrangeiro) {
	this.estrangeiro = estrangeiro;
}


public Date getDataCadastro() {
	return dataCadastro;
}


public void setDataCadastro(Date dataCadastro) {
	this.dataCadastro = dataCadastro;
}


public String getCodigoAcessoConfirmacao() {
	return codigoAcessoConfirmacao;
}


public void setCodigoAcessoConfirmacao(String codigoAcessoConfirmacao) {
	this.codigoAcessoConfirmacao = codigoAcessoConfirmacao;
}

public int getId() {
	return id;
}

public void setId(int id) {
	this.id = id;
}

public String getComplemento() {
	return complemento;
}

public Integer getIdParticipanteAcaoExtensao() {
	return idParticipanteAcaoExtensao;
}

public void setIdParticipanteAcaoExtensao(Integer idParticipanteAcaoExtensao) {
	this.idParticipanteAcaoExtensao = idParticipanteAcaoExtensao;
}

public List<Integer> getIdsParticipantesExtensaoAssociadosAoCadastro() {
	return idsParticipantesExtensaoAssociadosAoCadastro;
}

public void setIdsParticipantesExtensaoAssociadosAoCadastro(List<Integer> idsParticipantesExtensaoAssociadosAoCadastro) {
	this.idsParticipantesExtensaoAssociadosAoCadastro = idsParticipantesExtensaoAssociadosAoCadastro;
}
   

   
   
}
