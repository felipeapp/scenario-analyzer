package br.ufrn.sigaa.arq.dao.ensino;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Bolsas;
import br.ufrn.sigaa.ensino.dominio.Bolsistas;
import br.ufrn.sigaa.ensino.dominio.TipoBolsas;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * DAO responsável por consultas e gerenciamento de informações de bolsas diretamente no sistema administrativo.
 * 
 * @author wendell
 *
 */
public class BolsistaSigaaDao extends GenericSigaaDAO {

	public BolsistaSigaaDao() {
		setSistema(Sistema.SIGAA);
	}
	
	public Collection<Bolsistas> findByUnidade(Unidade unidade, Integer idTipoBolsa) throws DAOException {
		
		
		String hql = new String();
		
		hql = " select distinct bolsa.id, bolsa.inicio, bolsa.fim, " +
				"      bolsista.id, discente.matricula, pessoa.nome " +
			  " from  Bolsas as bolsa " +
			  " inner join bolsa.tipoBolsa as tipoBolsa " +
			  " inner join bolsa.unidade as unidade " +			  
			  " inner join bolsa.bolsista as bolsista " +
			  " inner join bolsista.discente as discente " +
			  " inner join discente.pessoa as pessoa " +
			  " where tipoBolsa.id =  " + TipoBolsas.BOLSA_CNPQ + " and unidade.id = " + unidade.getId();
		
		Query query = getSession().createQuery(hql);
		List<Object> lista = query.list();
		
		ArrayList<Bolsistas> bolsistas = new ArrayList<Bolsistas>();
		
		for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			Bolsistas bolsista = new Bolsistas();
			bolsista.setBolsa(new Bolsas());
			bolsista.setDiscente(new Discente());
			
			bolsista.getBolsa().setId((Integer) colunas[col++]);
			bolsista.getBolsa().setInicio((Date) colunas[col++]);
			bolsista.getBolsa().setFim((Date) colunas[col++]);
			
			bolsista.setId((Integer) colunas[col++]);
			bolsista.getDiscente().setMatricula((Long) colunas[col++]);
			bolsista.getDiscente().getPessoa().setNome((String) colunas[col++]);
			
			bolsistas.add(bolsista);
		}
		
		return bolsistas;
	}
	
	
	/**
	 * Registra a finalização de uma bolsa
	 * 
	 * @param bolsista
	 */
	public void finalizar(Bolsistas bolsista,Usuario usuario) {
		update("update ensino.bolsa " +
				" set data_finalizacao = ?, fim = ?, id_usuario_finalizacao = ? where id_bolsa = ?", 
				new Object[] { bolsista.getBolsa().getFim(), bolsista.getBolsa().getFim(), usuario.getId(), bolsista.getBolsa().getId()});
	}
	
	
	
	
	public boolean hasAtivoByDiscente(Discente discente) throws DAOException {
		
		String hql = new String();
		
		hql = "select bolsa.id " +
			  "from Bolsas as bolsa " +
			  "inner join bolsa.bolsista as bolsista " +
			  "inner join bolsista.discente as discente " +
			  "where discente.id =  " + discente.getId() + " and bolsa.fim > now() ";
		
		Query query = getSession().createQuery(hql);
		List<Object> lista = query.list();
		return ! ValidatorUtil.isEmpty(lista);
		
		
	}
	
	
	public Collection<Integer> tiposBolsaSigaa() throws DAOException {		
		String hql = new String();
		hql = 	"select id " +
				"from TipoBolsas as tipoBolsa " +
				"where tipoBolsa.nivel = 'S' and tipoBolsa.idBolsaSipac is null ";		
		Query query = getSession().createQuery(hql);
		Collection<Integer> tipos = (Collection<Integer>) query.list();		
		return tipos;
	}
	
	
	
	/**
	 * Realiza a busca de bolsistas ativos em um determinado período.
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 * @throws ArqException 
	 */
	public List<HashMap<String, Object>> findBolsistasAtivosSigaa(java.sql.Date dataInicial, java.sql.Date dataFinal, Character nivelEnsino, Collection<Integer> tiposBolsasCadastroSigaa ) throws ArqException {
		Connection con = null;
		SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			
			//lista de hashmap com resultado
			List<HashMap<String, Object>> resultado =  new ArrayList<HashMap<String,Object>>();
			
			con = Database.getInstance().getSigaaConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com SIGAA!");

			
			ResultSet rs = null;
			StringBuilder sql = new StringBuilder("select aluno.id_discente as id_discente, " +
					"tipo_bolsa.id_tipo_bolsa as id_tipo_bolsa, tipo_bolsa.descricao as denominacao, " +
					"bolsa.inicio, bolsa.fim, bolsa.data_finalizacao " +
					"from ensino.bolsista bolsista, ensino.bolsa  bolsa, public.discente aluno, ensino.tipo_bolsa tipo_bolsa " +
 				    "where bolsa.id_bolsista = bolsista.id_bolsista and bolsista.id_discente = aluno.id_discente " +
					"and bolsa.id_tipo_bolsa = tipo_bolsa.id_tipo_bolsa " +
					"and bolsa.fim > " + "'" + formatDate.format(new Date()) + "'" +
					"and ");
			sql.append(HibernateUtils.generateDateIntersection("?", "?", "bolsa.inicio", "bolsa.fim"));
			
			if( ! ValidatorUtil.isEmpty(nivelEnsino)) {
				if(NivelEnsino.isAlgumNivelStricto(nivelEnsino)) {
					sql.append(" and aluno.nivel in ('E','D','S') ");
				} else {				
					sql.append(" and aluno.nivel = " + nivelEnsino + " ");
				}
			}
			
			if( ! ValidatorUtil.isEmpty(tiposBolsasCadastroSigaa)) {
				sql.append(" and tipo_bolsa.id_tipo_bolsa in " + gerarStringIn(tiposBolsasCadastroSigaa));
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
	
}