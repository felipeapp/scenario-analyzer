package br.ufrn.bolsas.negocio;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;


/**
 * Métodos de integração dos dados das bolsas entre o SIPAC e o SIGAA
 *
 * @author ricardo
 * @author ilueny santos
 *
 */
public class IntegracaoBolsas {

	/** Manter valor sincronizado com o status correspondente no sistema acadêmico */
	public static final int STATUS_INDICACAO_PESQUISA = 6;
	public static final int STATUS_REGULARIZACAO_PESQUISA = 7;
	
	/** Manter valores sincronizados com os tipos de bolsa correspondentes no sistema acadêmico */
	public static final int TIPO_BOLSA_PROPESQ = 1;
	public static final int TIPO_BOLSA_EXTENSAO = 4;
	public static final int TIPO_BOLSA_PROPESQ_INDUCAO = 6;
	
	public static final String SIGLA_SIGAA = RepositorioDadosInstitucionais.get("siglaSigaa");
	public static final String SIGLA_SIPAC = RepositorioDadosInstitucionais.get("siglaSipac");

	/**
	 * Verificar se foi cadastrada, no sistema acadêmico, uma indicação de bolsa para
	 * o aluno informado.
	 *
	 * @param idDiscente
	 * @return
	 * @throws ArqException
	 */
	public static boolean verificarIndicacaoPesquisa(long matricula) throws ArqException{
		Connection con = null;
		try {
			con = Database.getInstance().getSigaaConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com " + SIGLA_SIGAA + "!");


			Statement st = con.createStatement();

			String sql = "SELECT pt.id_plano_trabalho " +
					" FROM pesquisa.plano_trabalho pt, pesquisa.membro_projeto_discente m, " +
					" 		pesquisa.tipo_bolsa_pesquisa t, discente d" +
					" WHERE pt.id_plano_trabalho = m.id_plano_trabalho" +
					" AND m.id_discente = d.id_discente" +
					" AND d.matricula = " + matricula +
					" AND pt.status = " + STATUS_REGULARIZACAO_PESQUISA +
					" AND pt.tipo_bolsa = t.id_tipo_bolsa " +
					" AND t.id_tipo_bolsa IN" + UFRNUtils.gerarStringIn(new int[]{TIPO_BOLSA_PROPESQ, TIPO_BOLSA_PROPESQ_INDUCAO});

			ResultSet rs = st.executeQuery(sql);

			return (rs.next());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		} finally {
			Database.getInstance().close(con);
		}
	}

	/**
	 * Verificar se foi cadastrada, no sistema acadêmico, uma indicação de bolsa para
	 * o aluno informado
	 *
	 * @param matricula
	 * @return
	 * @throws ArqException
	 */
	public static boolean verificarIndicacaoExtensao(long matricula) throws ArqException{
		Connection con = null;
		try {
			con = Database.getInstance().getSigaaConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com " + SIGLA_SIGAA + "!");

			String sql = "SELECT de.id_discente_extensao " +
					" FROM extensao.discente_extensao de, discente d" +
					" WHERE de.id_discente = d.id_discente" +
					" AND de.ativo = trueValue() " +
					" AND de.id_situacao_discente_extensao = 4 " + //4 = ativo
					" AND de.id_tipo_vinculo_discente in (2,4) " + //2 = bolsista faex, 4 = bolsista externo
					" AND (de.data_inicio <= ?  AND (de.data_fim >= ? OR de.data_fim is null ))"+ 
					" AND d.matricula = " + matricula;
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setDate(1, new java.sql.Date(new Date().getTime()));
			pst.setDate(2,new java.sql.Date(new Date().getTime()));
			
			ResultSet rs = pst.executeQuery();
			return (rs.next());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		} finally {
			Database.getInstance().close(con);
		}
	}

	/**
	 * Verificar se foi cadastrada, no sistema acadêmico, uma indicação de bolsa para
	 * o aluno informado
	 *
	 * @param matricula
	 * @return
	 * @throws ArqException
	 */
	public static boolean verificarIndicacaoMonitoria(long matricula) throws ArqException {
		Connection con = null;
		try {
			con = Database.getInstance().getSigaaConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com " + SIGLA_SIGAA + "!");

			String sql = "SELECT dm.id_discente_monitoria " +
					" FROM monitoria.discente_monitoria dm, discente d" +
					" WHERE dm.id_discente = d.id_discente" +
					" AND d.matricula = " + matricula +
					" AND dm.ativo = trueValue() " +
					" AND (dm.data_inicio <= ? AND (dm.data_fim >= ? OR dm.data_fim is null ))"+
					" AND dm.id_tipo_vinculo_discente = 2"; //1 - voluntário, 2-bolsista

			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setDate(1, new java.sql.Date(new Date().getTime()));
			pst.setDate(2,new java.sql.Date(new Date().getTime()));

			ResultSet rs = pst.executeQuery();
			
			return (rs.next());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		} finally {
			Database.getInstance().close(con);
		}
	}

	/**
	 * Cria a bolsa no sistema acadêmico e atualiza as tabelas necessárias
	 *
	 * @param idDiscente
	 * @param tipo
	 * @param inicio
	 * @param fim
	 * @throws ArqException
	 */
	public static void regularizarBolsa(long matricula, char tipo, Date inicio, Date fim) throws ArqException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		int idProjeto = -1;
		int idBolsa;
		int idDiscente = -1;
		try {
			con = Database.getInstance().getSigaaConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com " + SIGLA_SIGAA + "!");


			/* Buscar id da bolsa */
			st = con.prepareStatement("SELECT NEXTVAL('hibernate_sequence') as SEQ");
			rs = st.executeQuery();
			rs.next();
			idBolsa = rs.getInt("SEQ");

			/* Buscar o projeto referente à indicação do bolsista no sistema acadêmico*/
			/* Efetuar as atualizações específicas de cada tipo de projeto */
			int[] dadosIndicacao = {};
			String coluna = "";
			switch (tipo) {
				case 'P': // Bolsas de Pesquisa
					dadosIndicacao = getDadosPesquisa(matricula, idBolsa, con);
					coluna = "id_plano_trabalho";
					break;
				case 'M': // Bolsas de Monitoria
					dadosIndicacao = getDadosMonitoria(matricula, idBolsa, con);
					coluna = "id_monitor";
					break;
				case 'E': // Bolsas de Extensão
					dadosIndicacao = getDadosExtensao(matricula, idBolsa, con);
					coluna = "id_membro_equipe";
					break;
			}

			idProjeto = dadosIndicacao[0];
			idDiscente = dadosIndicacao[1];


			/* Cadastrar bolsa */
			st = con.prepareStatement("INSERT INTO bolsa" +
					"(id_bolsa, id_projeto, id_discente, data_inicio, data_fim, " + coluna + ")" +
					"VALUES (?,?,?,?,?,?)");
			int a = 1;
			st.setInt(a++, idBolsa);
			st.setInt(a++, idProjeto);
			st.setInt(a++, idDiscente);
			st.setDate(a++, new java.sql.Date(inicio.getTime()));
			st.setDate(a++, new java.sql.Date(fim.getTime()));
			st.setInt(a++, dadosIndicacao[2]);

			st.executeUpdate();

			// Regularizar as tabelas específicas do tipo de bolsa no sistema acadêmico
			switch (tipo) {
				case 'P': regularizarBolsaPesquisa(idBolsa, dadosIndicacao[2], con);
				case 'M': regularizarBolsaMonitoria(idBolsa, dadosIndicacao[2], con);
				case 'E': regularizarBolsaExtensao(idBolsa, dadosIndicacao[2], con);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		} finally {
			Database.getInstance().close(con);
		}

	}

	/**
	 * Buscar os dados referentes à bolsa de monitoria
	 *
	 * @param matricula
	 * @param idBolsa
	 * @param con
	 * @return
	 * @throws ArqException
	 */
	private static int[] getDadosMonitoria(long matricula, int idBolsa, Connection con) throws ArqException {
		PreparedStatement st = null;
		ResultSet rs = null;

		int idMonitor;
		int idProjeto;
		int idDiscente;
		try {
			con = Database.getInstance().getSigaaConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com " + SIGLA_SIGAA + "!");


			// Buscar o projeto de pesquisa e o plano de trabalho
			st = con.prepareStatement("SELECT m.id_monitor, cc.id_projeto_monitoria, d.id_discente " +
					" FROM monitoria.monitor m, monitoria.orientacao o, monitoria.componente_curricular cc, discente d" +
					" WHERE m.id_discente = d.id_discente" +
					" AND d.matricula = " + matricula +
					" AND m.id_monitor = o.id_monitor" +
					" AND o.id_componente_curricular = cc.id_componente_curricular " +
					" AND m.ativo = ativo" +
					" AND m.bolsista = trueValue()");
			rs = st.executeQuery();
			if (rs.next()) {
				idMonitor = rs.getInt("ID_MONITOR");
				idProjeto = rs.getInt("ID_PROJETO_MONITORIA");
				idDiscente = rs.getInt("ID_DISCENTE");
			} else {
				throw new NegocioException("Indicação de bolsista não encontrada no sistema acadêmico");
			}

			int[] result = {idProjeto, idDiscente, idMonitor};
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		}
	}

	/**
	 * Buscar os dados referentes à bolsa de extensão
	 *
	 * @param matricula
	 * @param idBolsa
	 * @param con
	 * @return
	 * @throws ArqException
	 */
	private static int[] getDadosExtensao(long matricula, int idBolsa, Connection con) throws ArqException {
		PreparedStatement st = null;
		ResultSet rs = null;

		int idMembro;
		int idProjeto;
		int idDiscente;
		try {
			con = Database.getInstance().getSigaaConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com " + SIGLA_SIGAA + "!");


			// Buscar o projeto de pesquisa e o plano de trabalho
			st = con.prepareStatement("SELECT m.id_membro_equipe, m.id_atividade, m.id_discente " +
					" FROM extensao.membro_equipe m, discente d" +
					" WHERE m.id_discente = d.id_discente" +
					" AND m.ativo = trueValue() " +
					" AND d.matricula = " + matricula);
			rs = st.executeQuery();
			if (rs.next()) {
				idMembro = rs.getInt("ID_MEMBRO_EQUIPE");
				idProjeto = rs.getInt("ID_ATIVIDADE");
				idDiscente = rs.getInt("ID_DISCENTE");
			} else {
				throw new NegocioException("Indicação de bolsista não encontrada no sistema acadêmico");
			}

			int[] result = {idProjeto, idDiscente, idMembro};
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		}
	}

	/**
	 * Buscar os dados referentes à bolsa de pesquisa
	 *
	 * @param idDiscente
	 * @param idBolsa
	 * @param con
	 * @return
	 * @throws ArqException
	 */
	private static int[] getDadosPesquisa(long matricula, int idBolsa, Connection con) throws ArqException {
		PreparedStatement st = null;
		ResultSet rs = null;

		int idPlano;
		int idProjeto;
		int idDiscente;
		try {
			con = Database.getInstance().getSigaaConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com " + SIGLA_SIGAA + "!");


			// Buscar o projeto de pesquisa e o plano de trabalho
			st = con.prepareStatement("SELECT pt.id_plano_trabalho, pt.id_projeto_pesquisa, d.id_discente " +
					" FROM pesquisa.plano_trabalho pt, pesquisa.membro_projeto_discente m, discente d" +
					" WHERE pt.id_plano_trabalho = m.id_plano_trabalho" +
					" AND m.id_discente = d.id_discente" +
					" AND d.matricula = " + matricula +
					" AND pt.status = " + STATUS_INDICACAO_PESQUISA);
			rs = st.executeQuery();
			if (rs.next()) {
				idPlano = rs.getInt("ID_PLANO_TRABALHO");
				idProjeto = rs.getInt("ID_PROJETO_PESQUISA");
				idDiscente = rs.getInt("ID_DISCENTE");
			} else {
				throw new NegocioException("Indicação de bolsista não encontrada no sistema acadêmico");
			}

			int[] result = {idProjeto, idDiscente, idPlano};
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		}
	}

	/**
	 * Atualizar a tabela de planos de trabalho informando, para o plano
	 * passado como parâmetro, qual a sua bolsa.
	 *
	 * @param idBolsa
	 * @param idPlano
	 * @throws ArqException
	 */
	private static void regularizarBolsaPesquisa(int idBolsa, int idPlano, Connection con) throws ArqException {
		PreparedStatement st = null;

		try {
			con = Database.getInstance().getSigaaConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com " + SIGLA_SIGAA + "!");


			// Atualizar o plano de trabalho
			st = con.prepareStatement("UPDATE pesquisa.plano_trabalho " +
					" SET status = ?, id_bolsa = ?" +
					" WHERE id_plano_trabalho = ?");
			st.setInt(1, STATUS_REGULARIZACAO_PESQUISA);
			st.setInt(2, idBolsa);
			st.setInt(3, idPlano);

			st.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		}
	}

	/**
	 * Atualizar a tabela de monitores informando, para o monitor
	 * passado como parâmetro, qual a sua bolsa.
	 *
	 * @param idBolsa
	 * @param idMonitor
	 * @throws ArqException
	 */
	private static void regularizarBolsaMonitoria(int idBolsa, int idMonitor, Connection con) throws ArqException {
		PreparedStatement st = null;

		try {
			con = Database.getInstance().getSigaaConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com " + SIGLA_SIGAA + "!");

			// Atualizar o plano de trabalho
			st = con.prepareStatement("UPDATE monitoria.monitor " +
					" SET id_bolsa = ?" +
					" WHERE id_monitor = ?");
			st.setInt(1, idBolsa);
			st.setInt(2, idMonitor);

			st.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		}
	}

	/**
	 * Atualiza a tabela de membros de equipe de extensão informando, para o membro
	 * passado como parâmetro, qual a sua bolsa.
	 *
	 * @param idBolsa
	 * @param idMonitor
	 * @throws ArqException
	 */
	private static void regularizarBolsaExtensao(int idBolsa, int idMembro, Connection con) throws ArqException {
		PreparedStatement st = null;

		try {
			con = Database.getInstance().getSigaaConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com " + SIGLA_SIGAA + "!");

			// Atualizar o plano de trabalho
			st = con.prepareStatement("UPDATE extensao.membro_equipe " +
					" SET id_bolsa = ?" +
					" WHERE id_membro_equipe = ?");
			st.setInt(1, idBolsa);
			st.setInt(2, idMembro);

			st.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		}
	}






	/**
	 * Verificar se o aluno foi cadastrado no SIPAC e tem uma bolsa ativa.
	 *
	 * @param matricula
	 * @return id da bolsa ativa encontrada no SIPAC. 0 = não encontrou bolsa ativa para a matricula informada
	 * @throws ArqException
	 */
	public static int verificarCadastroBolsaSIPAC(long matricula, int[] idTipoBolsa) throws ArqException{

		Connection con = null;
		try {

			con = Database.getInstance().getSipacConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com " + SIGLA_SIPAC + "!");

					ResultSet rs = null;

					StringBuffer sql = new StringBuffer( "SELECT bsa.id as idBolsa " +
							" FROM bolsas.bolsista bta INNER JOIN bolsas.bolsa bsa ON bta.id = bsa.id_bolsista " +
							" INNER JOIN bolsas.tipo_bolsa tb ON tb.id = bsa.id_tipo_bolsa " +
							" WHERE (bta.matricula = ?) AND (bsa.fim > ?) " +
							" AND tb.permite_acumular = falseValue()");
					
					 if (idTipoBolsa != null) {
					     sql.append(" AND (id_tipo_bolsa IN ( ");
					     for (int i = 0; i < idTipoBolsa.length; i++) {
						 if (i < idTipoBolsa.length - 1)
						     sql.append(idTipoBolsa[i] + ", ");
						 else
						     sql.append(idTipoBolsa[i] + " ))");
					    }
					 }

					PreparedStatement pst = con.prepareStatement(sql.toString());

					pst.setLong(1, matricula);
					pst.setDate(2, new java.sql.Date( DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH).getTime() ) );					

					rs = pst.executeQuery();

					if (rs.next())
						return (rs.getInt("idBolsa"));
					else
						return 0;

		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		} finally {
			Database.getInstance().close(con);
		}
	}


	/**
	 * Verificar se os alunos passados possuem qualquer bolsa ativa de um tipo especificado. Se não for informado o tipo de bolsa,
	 * verifica se tem bolsas ativas de qualquer tipo.
	 *
	 * @param matricula
	 * @return Retorna somente os alunos com bolsa ativa.
	 * @throws ArqException
	 */
	public static Collection<Long> verificarCadastroBolsaSIPAC(List<Long> matricula, Integer[] idTipoBolsa) throws ArqException{

		Collection<Long> resultado = null;
		
		Connection con = null;
		try {

			con = Database.getInstance().getSipacConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com " + SIGLA_SIPAC + "!");

					ResultSet rs = null;

					StringBuffer sql = new StringBuffer( "SELECT bta.matricula as matricula " +
							" FROM bolsas.bolsista bta INNER JOIN bolsas.bolsa bsa ON bta.id = bsa.id_bolsista " +
							" WHERE (bta.matricula in " + gerarStringIn(matricula) + ") AND (bsa.fim > ?) ");
					
					if( idTipoBolsa!= null && idTipoBolsa.length > 0 )
						sql.append(" AND bsa.id_tipo_bolsa IN" + UFRNUtils.gerarStringIn(idTipoBolsa));
					
					
					PreparedStatement pst = con.prepareStatement(sql.toString());

					pst.setDate(1, new java.sql.Date( DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH).getTime() ) );					

					
					
					rs = pst.executeQuery();

					while (rs.next()) {
						if (resultado == null)
							resultado = new ArrayList<Long>();
						
						resultado.add(rs.getLong("matricula"));
					}

		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		} finally {
			Database.getInstance().close(con);
		}
		
		return resultado;
	}
	
	/**
	 * Verificar se os alunos passados possuem qualquer bolsa ativa.
	 * 
	 * 
	 * @param matricula
	 * @return
	 * @throws ArqException
	 */
	public static Collection<Long> verificarCadastroBolsaSIPAC(List<Long> matricula) throws ArqException{
		return verificarCadastroBolsaSIPAC(matricula, null);
	}
	
	
	
	/**
	 * Retorna a lista de solicitações de cadastro de bolsas realizadas no SIPAC.
	 *
	 * @param matricula matricula do discentes, se não informada retorna todas as solicitações
	 * @param idTipoBolsa tipo de bolsas para consulta das solicitações
	 * @return Coleção de ids dos discentes com solicitações cadastradas no SIPAC
	 * @throws ArqException
	 */
	public static Collection<Long> verificarSolicitacaoBolsaSIPAC(Long matricula, Integer idTipoBolsa) throws ArqException{

		Connection con = null;
		try {

			con = Database.getInstance().getSipacConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com " + SIGLA_SIPAC + "!");

					ResultSet rs = null;

					StringBuffer sql = new StringBuffer( "SELECT sol.matricula as matricula " +
							" FROM bolsas.solicitacao_bolsa sol  " +
							" WHERE (sol.tipo_solicitacao = ?) AND (sol.excluida = ?) " +
							" AND ((sol.status = ?) OR (sol.status = ? AND sol.data_finalizacao is null))");

					 if (idTipoBolsa != null)
						 sql.append(" AND (id_tipo_bolsa = ?) ");
					 
					 if (matricula != null)
						 sql.append(" AND (sol.matricula = ?) "); 

					PreparedStatement pst = con.prepareStatement(sql.toString());

					pst.setInt(1, 1);  //sipac.bolas.dominio.SolicitacaoBolsa.INCLUSAO
					pst.setBoolean(2, false);
					pst.setInt(3, 101);  //sipac.requisicoes.comuns.StatusRequisicao.ENVIADA
					pst.setInt(4, 104);  //sipac.requisicoes.comuns.StatusRequisicao.ATENDIDA
					if (idTipoBolsa != null)
						pst.setInt(5, idTipoBolsa);
					
					if (matricula != null)
						pst.setLong(6, matricula);
						
					rs = pst.executeQuery();

					Collection<Long> idsDiscentes = new ArrayList<Long>();
					while (rs.next())
						idsDiscentes.add(rs.getLong("matricula"));
					
					return idsDiscentes;

		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		} finally {
			Database.getInstance().close(con);
		}
	}

	
	
	
	

	/**
	 * Cadastra/Atualiza Frequência no SIPAC
	 *
	 * 	ATENÇÃO - As frequências no SIPAC utilizam o seguinte esquema para representar os meses:
	 * 	0 = JANEIRO,	1 = FEVEREIRO, 2 = MARÇO, ..., 11 = DEZEMBRO
	 *
	 * @param matricula
	 * @param inicio
	 * @param fim
	 * @throws ArqException
	 */
	public static void cadastrarFrequenciaSIPAC(int idBolsa, int mes, int ano, double frequencia, int idUsuario, int idAtividadeMonitor) throws ArqException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		int idFrequencia;
		try {

			con = Database.getInstance().getSipacConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com " + SIGLA_SIPAC + "!");


						//verifica se essa frequência ja foi cadastrada...
						idFrequencia = verificarEnvioFrequenciaSIPAC(idAtividadeMonitor);


						if (idFrequencia == 0){

									/* Buscar id da nova frequência */
									st = con.prepareStatement("SELECT NEXTVAL('FREQUENCIA_SEQ') as SEQ");
									rs = st.executeQuery();
									rs.next();
									idFrequencia = rs.getInt("SEQ");

									/* Cadastrar frequência */
									st = con.prepareStatement("INSERT INTO bolsas.frequencia " +
											"(id, mes, ano, frequencia, id_bolsa, id_usuario, id_atividade_monitor) " +
											"VALUES (?,?,?,?,?,?,?)");

									int a = 1;
									st.setInt(a++, idFrequencia);
									st.setInt(a++, mes);
									st.setInt(a++, ano);
									st.setDouble(a++, frequencia);
									st.setInt(a++, idBolsa);
									st.setInt(a++, idUsuario);
									st.setInt(a++, idAtividadeMonitor);

						}else{

									/* Atualizar frequência */
									st = con.prepareStatement("UPDATE bolsas.frequencia " +
											"SET mes = ?,  ano = ?, frequencia = ? WHERE id = ?");

									int a = 1;
									st.setInt(a++, mes);
									st.setInt(a++, ano);
									st.setDouble(a++, frequencia);
									st.setDouble(a++, idFrequencia);

						}


						st.executeUpdate();


		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		} finally {
			Database.getInstance().close(con);
		}

	}



	/**
	 * Verificar se a frequência da atividade informada já está no SIPAC. Evita enviar a frequência da mesma atividade
	 * duas vezes pro SIPAC.
	 *
	 * @param matricula
	 * @return id da frequência encontrada no SIPAC. 0 = não encotrou frequência relacionada a atividade no SIPAC
	 * @throws ArqException
	 */
	public static int verificarEnvioFrequenciaSIPAC(int idAtividadeMonitor) throws ArqException{
		try {
			Connection con = Database.getInstance().getSipacConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com " + SIGLA_SIPAC + "!");

			ResultSet rs = null;
			String sql = "SELECT f.id as idFrequencia FROM bolsas.frequencia f " +
					" WHERE (f.id_atividade_monitor = ?)";

			PreparedStatement pst = con.prepareStatement(sql);
			pst.setLong(1, idAtividadeMonitor);

			rs = pst.executeQuery();

			if (rs.next())
				return (rs.getInt("idFrequencia"));
			else
				return 0;

		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		} 
	}

	/**
	 * Atualizar a tabela de atividade do monitor no SIGAA com a data/hora, registro entrada do envio da frequência
	 *
	 * @param idBolsa
	 * @param idMonitor
	 * @throws ArqException
	 */
	public static void validarAtividadeMonitorPROGRAD(int idAtividadeMonitor, boolean validado, java.sql.Date dataValidacaoPrograd, int idRegistroEntradaPrograd ) throws ArqException {
		PreparedStatement st = null;

		try {
			Connection con = Database.getInstance().getSigaaConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com " + SIGLA_SIGAA + "!");


			// Atualizar a atividade do monitor
			st = con.prepareStatement("UPDATE monitoria.atividade_monitor " +
					" SET validado_prograd = ?, data_validacao_prograd = ?, id_registro_entrada_prograd = ? " +
					" WHERE id = ?");

			int a = 1;
			st.setBoolean(a++, validado);
			st.setDate(a++, dataValidacaoPrograd);
			st.setInt(a++, idRegistroEntradaPrograd);
			st.setInt(a++, idAtividadeMonitor);

			st.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		}
	}
	
	/**
	 * Retorna os discentes carentes do cadastro único de bolsas ativo
	 * 
	 * @param pontuacaoCarente
	 * @return
	 * @throws ArqException
	 * @deprecated Esse metódo vai trazer resultados errados. Usar {@link #isPrioritario(int, int, int)}
	 */
	@Deprecated
	public static List<Integer> findDiscentesPioritarios() throws ArqException{

		Connection con = null;
		try {
			con = Database.getInstance().getSigaaConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com " + SIGLA_SIGAA + "!");

			ResultSet rs = null;
			String sql = "	select d.id_discente from discente d " +
					"			inner join sae.adesao_cadastro_unico adesao using(id_discente) " +
					"			inner join sae.cadastro_unico_bolsa cub using(id_cadastro_unico) " +
					"		where adesao.pontuacao < ? and cub.status = 1";

			PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, ParametroHelper.getInstance().getParametroInt( ConstantesParametroGeral.ALUNO_PRIORITARIO));

			rs = pst.executeQuery();

			List<Integer> resultado = null;
			while (rs.next()) {
				if (resultado == null) 
					resultado = new ArrayList<Integer>();
				resultado.add(rs.getInt("id_discente"));
			}

			return resultado;

		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		} finally {
			Database.getInstance().close(con);
		}
	}
	
	
	/**
	 * Realiza a busca de bolsistas ativos em um determinado período.
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 * @throws ArqException 
	 */
	public static List<HashMap<String, Object>> findBolsistasAtivos(java.sql.Date dataInicial, java.sql.Date dataFinal, Character nivelEnsino, Collection<Integer> tiposBolsasCadastroSigaa ) throws ArqException {
		Connection con = null;
		SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			
			//lista de hashmap com resultado
			List<HashMap<String, Object>> resultado =  new ArrayList<HashMap<String,Object>>();
			
			con = Database.getInstance().getSipacConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com " + SIGLA_SIPAC + "!");

			
			ResultSet rs = null;
			StringBuilder sql = new StringBuilder("select aluno.id_discente as id_discente, " +
					"tipo_bolsa.id as id_tipo_bolsa, tipo_bolsa.denominacao as denominacao, " +
					"bolsa.inicio, bolsa.fim, bolsa.data_finalizacao " +
					"from bolsas.bolsista bolsista, bolsas.bolsa  bolsa, academico.aluno aluno, bolsas.tipo_bolsa tipo_bolsa " +
 				    "where bolsa.id_bolsista = bolsista.id and bolsista.id_aluno = aluno.id_aluno " +
					"and bolsa.id_tipo_bolsa = tipo_bolsa.id " +
					"and bolsa.fim > " + "'" + formatDate.format(new Date()) + "'" +
					"and ");
			sql.append(HibernateUtils.generateDateIntersection("?", "?", "bolsa.inicio", "bolsa.fim"));
			
			if( ! ValidatorUtil.isEmpty(nivelEnsino)) {
				if(NivelEnsino.isAlgumNivelStricto(nivelEnsino)) {
					sql.append(" and aluno.nivel_sigaa in ('E','D','S') ");
				} else {				
					sql.append(" and aluno.nivel_sigaa = " + nivelEnsino + " ");
				}
			}
			
			if( ! ValidatorUtil.isEmpty(tiposBolsasCadastroSigaa)) {
				sql.append(" and tipo_bolsa.id not in " + gerarStringIn(tiposBolsasCadastroSigaa));
			}
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setDate(1, dataInicial);
			pst.setDate(2, dataInicial);
			pst.setDate(3, dataInicial);
			pst.setDate(4, dataInicial);
			pst.setDate(5, dataFinal);
			
			rs = pst.executeQuery();
			
			//construindo o mapa com valores
			while (rs.next()) {
				 HashMap<String, Object> mapa = new HashMap<String, Object>();
				 mapa.put("id_discente", rs.getInt("id_discente"));
				 mapa.put("id_tipo_bolsa", rs.getInt("id_tipo_bolsa"));
				 mapa.put("denominacao", rs.getString("denominacao"));
				 mapa.put("inicio", rs.getDate("inicio"));
				 mapa.put("fim", rs.getDate("fim"));
				 resultado.add(mapa);
			}
			
			return resultado;

		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		} finally {
			Database.getInstance().close(con);
		}
	}
	
	
	
	
	/**
	 * Realiza a busca de bolsistas ativos em um determinado período.
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 * @throws ArqException 
	 */								   
	public static Map<Integer, String> findBolsistasAtivosDiscente(java.sql.Date dataInicial, java.sql.Date dataFinal) throws ArqException {
		Connection con = null;
		try {
			con = Database.getInstance().getSipacConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com " + SIGLA_SIPAC + "!");

			ResultSet rs = null;
			StringBuilder sql = new StringBuilder("select aluno.id_discente as id_discente, tipo_bolsa.id as id_tipo_bolsa, tipo_bolsa.denominacao as denominacao from  " +
						 "bolsas.bolsista bolsista, bolsas.bolsa  bolsa, academico.aluno aluno, bolsas.tipo_bolsa tipo_bolsa " +
						 "where bolsa.id_bolsista = bolsista.id and bolsista.id_aluno = aluno.id_aluno " +
						 "and bolsa.id_tipo_bolsa = tipo_bolsa.id " +
						 "and ");
			sql.append(HibernateUtils.generateDateIntersection("bolsa.inicio", "bolsa.fim", "?", "?"));
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setDate(1, dataInicial);
			pst.setDate(2, dataInicial);
			pst.setDate(3, dataInicial);
			pst.setDate(4, dataInicial);
			pst.setDate(5, dataFinal);
			
			rs = pst.executeQuery();
			
			//lista de hashmap com resultado		
			Map<Integer, String> resultado = new HashMap<Integer, String>();
			
			//construindo o mapa com valores
			while (rs.next()) 
 				 resultado.put(rs.getInt("id_discente"), rs.getString("denominacao"));
						
			return resultado;

		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		} finally {
			Database.getInstance().close(con);
		}
	}	
	
	/**
	 * Retorna se o discente é prioritário no ano e período informado
	 * 
	 * @param idDiscente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws ArqException
	 */
	public static boolean isPrioritario(int idDiscente, int ano, int periodo) throws ArqException {
		Connection con = null;
		try {
			con = Database.getInstance().getSigaaConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com " + SIGLA_SIGAA + "!");

			ResultSet rs = null;
			String sql = " " +
					" select count(id_adesao) as qtd from sae.adesao_cadastro_unico " +
					" where id_discente = ? and " +
					"	ano = ? " +
					"	and periodo = ? " +
					"	and pontuacao < ?";

			PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, idDiscente);
			pst.setInt(2, ano);
			pst.setInt(3, periodo);
			pst.setInt(4, ParametroHelper.getInstance().getParametroInt( ConstantesParametroGeral.ALUNO_PRIORITARIO));

			rs = pst.executeQuery();

			if (rs.next()) { 
				int resultado = rs.getInt("qtd");
				if (resultado > 0)
					return true;
			}
			
			return false;

		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		} finally {
			Database.getInstance().close(con);
		}
	}
	
	/**
	 * Método responsável por retornar os tipos de bolsa do discente no SIPAC.
	 *
	 * @param matricula
	 * @return Lista com os identificadores dos tipos de bolsa referentes às bolsas ativas encontrada no SIPAC. 
	 * @throws ArqException
	 */
	public static List<Integer> tipoBolsaSipacAtivoByMatricula(long matricula) throws ArqException{

		Connection con = null;
		try {

			con = Database.getInstance().getSipacConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com " + SIGLA_SIPAC + "!");

			ResultSet rs = null;

			StringBuffer sql = new StringBuffer( "SELECT bsa.id_tipo_bolsa as idTipoBolsa " +
					" FROM bolsas.bolsista bta INNER JOIN bolsas.bolsa bsa ON bta.id = bsa.id_bolsista " +
					" INNER JOIN bolsas.tipo_bolsa tb ON tb.id = bsa.id_tipo_bolsa " +
					" WHERE (bta.matricula = ?) AND (bsa.fim > ?) " +
					" AND bsa.data_finalizacao is null ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());

			pst.setLong(1, matricula);
			pst.setDate(2, new java.sql.Date( DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH).getTime() ) );					

			rs = pst.executeQuery();
			List<Integer> listTipoBolsas = new ArrayList<Integer>();
			while (rs.next()) {
				listTipoBolsas.add(rs.getInt("idTipoBolsa"));
			}
			return(listTipoBolsas);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		} finally {
			Database.getInstance().close(con);
		}
	}
}
