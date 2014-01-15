package br.ufrn.sigaa.ensino.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.ensino.dominio.InscricaoSelecao;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.StatusInscricaoSelecao;

/**
 * DAO que disponibiliza todas consultas utilizadas na validação da inscrição
 * em processo seletivo.
 * @author Mário Rizzi
 *
 */
public class ValidacaoInscricaoSelecaoDao extends GenericSigaaDAO {

	
	/**
	 * Verifica se existe pessoa com vínculo discente ou servidor
	 * @param cpf
	 * @param passaporte
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public boolean possuiVinculoDiscenteServidor(Long cpf, String passaporte)
			throws DAOException{
		
		String hqlCondicao = "";
		if( !isEmpty(cpf) ){
			hqlCondicao = " AND p.cpf_cnpj = :cpf";
		}else{
			hqlCondicao = " AND p.passaporte = :passaporte";
		}	
		
		Collection<Integer> status = StatusDiscente.getStatusComVinculo();
		status.remove( StatusDiscente.GRADUANDO );
		
		String sqlDiscente = "SELECT p.id_pessoa FROM discente d "
				+ " INNER JOIN comum.pessoa p USING(id_pessoa) "
				+ " WHERE d.status IN " 
				+ gerarStringIn( status )  
				+ hqlCondicao;
	    String sqlServidor = "SELECT p.id_pessoa FROM rh.servidor s "
	    		+ " INNER JOIN comum.pessoa p USING(id_pessoa) "
	    		+ " WHERE s.id_ativo = "
	    		+ Ativo.SERVIDOR_ATIVO
	    		+ hqlCondicao;
	    
	    String sql = " SELECT CAST(COUNT(id_pessoa) AS INTEGER) FROM ( ("+ sqlDiscente + ") UNION (" + sqlServidor + ") ) AS sub";
	    Query q = getSession().createSQLQuery(sql);
	    if( !isEmpty(cpf) )
	    	q.setLong("cpf", cpf);
	    else
	    	q.setString("passaporte", passaporte);
	    Integer qtd = (Integer) q.uniqueResult();
	    return qtd>0;
		
	}
	
	/**
	 * Verifica se existe pessoa possui bolsa do SAE.
	 * @param cpf
	 * @param passaporte
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public boolean possuiBolsaSAE(Long cpf, String passaporte, Integer ano, Integer periodo)
			throws DAOException{
		
		String hqlCondicao = "";
		if( !isEmpty(cpf) ){
			hqlCondicao = " AND p.cpf_cnpj = :cpf";
		}else{
			hqlCondicao = " AND p.passaporte = :passaporte";
		}	
		
		String sql = " SELECT CAST(COUNT(DISTINCT p.id_pessoa) AS INTEGER) FROM discente d "
					+ " INNER JOIN comum.pessoa p ON(d.id_pessoa = p.id_pessoa) "
					+ " INNER JOIN sae.bolsa_auxilio ba ON(ba.id_discente = d.id_discente) "
					+ " LEFT JOIN sae.bolsa_auxilio_periodo bap ON(bap.id_bolsa_auxilio = ba.id_bolsa_auxilio) "
					+ " WHERE d.status IN " + gerarStringIn( StatusDiscente.getAtivos() )  
					+ " AND ( "
					+ " 		( "
					+ "				( "
					+ "					( ba.id_tipo_bolsa_auxilio IN " + gerarStringIn( TipoBolsaAuxilio.getTiposResidencia() ) 
										+ " AND ba.id_situacao_bolsa = " + SituacaoBolsaAuxilio.BOLSA_AUXILIO_MORADIA + " ) "
					+ "					OR "
					+ "					( ba.id_tipo_bolsa_auxilio = " + TipoBolsaAuxilio.ALIMENTACAO 
										+ " AND ba.id_situacao_bolsa = " + SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA +" ) "
					+ "				)"
					+ "				AND bap.ano = " + ano + " AND bap.periodo = " + periodo
					+ "			)"
					+ "			OR ba.id_situacao_bolsa = " + SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA 
					+ " 	) "	+ hqlCondicao;
	  
		
		
	    Query q = getSession().createSQLQuery(sql);
	    if( !isEmpty(cpf) )
	    	q.setLong("cpf", cpf);
	    else
	    	q.setString("passaporte", passaporte);
	    Integer qtd = (Integer) q.uniqueResult();
	    return qtd > 0;
		
	}
	public static List<Integer> getStatusAtivos(){
		
		List<Integer> statusAtivos = new ArrayList<Integer>();
		
		statusAtivos.add(StatusInscricaoSelecao.DEFERIDA);
		statusAtivos.add(StatusInscricaoSelecao.APROVADO_SELECAO);
		statusAtivos.add(StatusInscricaoSelecao.SUBMETIDA);
		statusAtivos.add(StatusInscricaoSelecao.SUPLENTE);
		
		return statusAtivos;
	}
	/**
	 * Verifica se existe vaga disponível para o processo seletivo.
	 * @param cpf
	 * @param passaporte
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public boolean existeVaga(ProcessoSeletivo processo)
			throws DAOException{
		
		String sql = "SELECT CAST(COUNT(DISTINCT id) AS INTEGER) FROM ensino.inscricao_selecao "
				+ " WHERE id_processo_seletivo = " + processo.getId()  
				+ " AND status IN " + gerarStringIn( getStatusAtivos() );
	  
	    Query q = getSession().createSQLQuery(sql);
	    Integer qtdInscritos = (Integer) q.uniqueResult();
	    return isEmpty( processo.getVaga() ) || ( processo.getVaga() - qtdInscritos ) > 0;
		
	}
	
	/**
	 * Verifica se já existe inscrição para o mesmo cpf ou passaporte
	 * @param inscricao
	 * @return
	 * @throws DAOException
	 */
	public boolean existeInscricao(InscricaoSelecao inscricao) throws DAOException {
				
		String hqlCondicao = "";
		if( !isEmpty(inscricao.getPessoaInscricao().getCpf()) ){
			hqlCondicao = " pi.cpf = :cpf";
		}else{
			hqlCondicao = " pi.passaporte = :passaporte";
		}	
		
		String sql = " SELECT CAST( COUNT(i.id) AS INTEGER) "
				+ " FROM ensino.inscricao_selecao i "
				+ " INNER JOIN ensino.pessoa_inscricao pi on(i.id_pessoa_inscricao=pi.id) "
				+ " INNER JOIN ensino.processo_seletivo ps on(ps.id_processo_seletivo = i.id_processo_seletivo) "
				+ " INNER JOIN ensino.edital_processo_seletivo eps on(eps.id_edital_processo_seletivo = ps.id_edital_processo_seletivo) "
				+ " INNER JOIN curso c on(ps.id_curso = c.id_curso) "
				+ " WHERE " + hqlCondicao
				+ " AND c.id_unidade = " + inscricao.getProcessoSeletivo().getCurso().getUnidade().getId()
				+ " AND i.status IN " + gerarStringIn( getStatusAtivos() )
				+ " AND eps.inicio_inscricoes <= NOW() AND eps.fim_inscricoes >= NOW() "
				+ " AND ps.ativo "
				+ " AND c.ativo ";
		 
		Query q = getSession().createSQLQuery(sql);
		  if( !isEmpty(inscricao.getPessoaInscricao().getCpf()) )
		    	q.setLong("cpf", inscricao.getPessoaInscricao().getCpf());
		    else
		    	q.setString("passaporte", inscricao.getPessoaInscricao().getPassaporte());
		Integer qtd = (Integer) q.uniqueResult();
		
		return qtd > 0;
		
	}
	
}
