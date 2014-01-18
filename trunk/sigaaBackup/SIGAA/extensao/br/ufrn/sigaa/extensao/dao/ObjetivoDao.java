package br.ufrn.sigaa.extensao.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.MembroAtividade;
import br.ufrn.sigaa.extensao.dominio.Objetivo;
import br.ufrn.sigaa.extensao.dominio.ObjetivoAtividades;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

public class ObjetivoDao extends GenericSigaaDAO{

	/**
	 * Carregar os Objetivos da Atividade de Extensão
	 * @param idAtividade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Objetivo> findObjetivos( Integer idAtividade, Integer idObjetivoSelecionado ) throws DAOException {
		String sql = " select o.id_objetivo, o.objetivo, o.id_atividade, oa.id_objetivo_atividades, oa.descricao, oa.data_inicio, " +
				     " oa.data_fim, oa.carga_horaria, ma.ativo, ma.id_membro_atividade, ma.id_membro_projeto, p.nome, ma.carga_horaria as chmembro" +
				     " from extensao.objetivo o" +
				     " left join extensao.objetivo_atividades oa using ( id_objetivo )" +
				     " left join extensao.membro_atividade ma on " +
				     "      ( oa.id_objetivo_atividades = ma.id_objetivo_atividade and oa.ativo = trueValue())" +
				     " left join projetos.membro_projeto mo on " +
				     "      ( ma.id_membro_projeto = mo.id_membro_projeto and ma.ativo = trueValue() )" +
				     " left join comum.pessoa p using ( id_pessoa )";
				
				if ( idAtividade != null)
					sql += " where o.id_atividade = :idAtividade";
			   else
				   sql += " where o.id_objetivo = :idObjetivo";
					
				   sql += " and o.ativo = trueValue()" +
				   		  " order by id_objetivo_atividade asc";
		
		Query q = getSession().createSQLQuery(sql);
		if ( idAtividade != null)
			q.setInteger("idAtividade", idAtividade);
		else
			q.setInteger("idObjetivo", idObjetivoSelecionado);
			
		List<Object[]> lista = q.list();
		List<Objetivo> result = new ArrayList<Objetivo>();
		Objetivo objetivo = null;
		ObjetivoAtividades objetivoAtividades = null;
		Integer idObjetivo = 0;
		Integer idObjetivoAtividade = 0;
		Integer idMembroAtividade = 0;
		
		for (Object[] objects : lista) {
			
			int count = 0;
			int idObjetivoAtual = (Integer) objects[0];
			if ( !idObjetivo.equals(idObjetivoAtual) ) {
				if ( idObjetivo > 0 ) {
					if ( !isEmpty(objetivoAtividades) )
						objetivo.getAtividadesPrincipais().add(objetivoAtividades);
					else 
						objetivo.setAtividadesPrincipais(null);
					result.add(objetivo);
					objetivoAtividades = new ObjetivoAtividades();
					objetivoAtividades.setMembrosAtividade(null);
					idObjetivoAtividade = 0;
				}
				objetivo = new Objetivo();
				objetivo.setId( (Integer) objects[count++] );
				objetivo.setObjetivo( (String) objects[count++] );
				objetivo.setAtividadeExtensao(new AtividadeExtensao((Integer) objects[count++]));
				objetivo.setAtivo(Boolean.TRUE);
				objetivo.setAtividadesPrincipais(new ArrayList<ObjetivoAtividades>());
			} 
			
			count = 3;
			Integer idObjetivoAtividadeAtual = (Integer) objects[count++];
			if ( idObjetivoAtividadeAtual != null && !idObjetivoAtividade.equals( idObjetivoAtividadeAtual ) ) {
				if ( idObjetivoAtividade != 0 )
					objetivo.getAtividadesPrincipais().add(objetivoAtividades);
				objetivoAtividades = new ObjetivoAtividades(idObjetivoAtividadeAtual);
				objetivoAtividades.setDescricao( (String) objects[count++] );
				objetivoAtividades.setDataInicio( (Date) objects[count++] );
				objetivoAtividades.setDataFim( (Date) objects[count++] );
				objetivoAtividades.setCargaHoraria( (Integer) objects[count++] );
				objetivoAtividades.setObjetivo(objetivo);
				objetivoAtividades.setAtivo(Boolean.TRUE);
			} 
			
			count = 8;
			Boolean ativo = (Boolean) objects[count++];
			Integer idMembroAtividadeAtual = (Integer) objects[count++];
			if ( ativo != null && ativo ) {
				if ( idMembroAtividadeAtual != null && !idMembroAtividade.equals( idMembroAtividadeAtual ) ) {
					MembroAtividade ma = new MembroAtividade();
					ma.setId( idMembroAtividadeAtual );
					ma.setMembroProjeto(new MembroProjeto( (Integer) objects[count++] ));
					ma.getMembroProjeto().setPessoa(new Pessoa());
					ma.getMembroProjeto().getPessoa().setNome( (String) objects[count++] );
					ma.setCargaHoraria( (Integer) objects[count++] );
					ma.setAtivo(Boolean.TRUE);
					if ( isEmpty(objetivoAtividades.getMembrosAtividade()) )
						objetivoAtividades.setMembrosAtividade(new ArrayList<MembroAtividade>());	
					objetivoAtividades.getMembrosAtividade().add(ma);
				} 
			}
			
			idObjetivoAtividade = idObjetivoAtividadeAtual;
			idMembroAtividade = idMembroAtividadeAtual;
			idObjetivo = idObjetivoAtual;
		}
		
		if ( !isEmpty(objetivo) ) {
			if ( !isEmpty(objetivo.getAtividadesPrincipais()) || !isEmpty(objetivoAtividades) )
				objetivo.getAtividadesPrincipais().add(objetivoAtividades);
			result.add(objetivo);
		}
		
		return result;
	}
	
	/**
	 * Server para inativar todos os registros do Objetivo
	 * @param objetivo
	 * @throws DAOException
	 */
	public void inativarObjetivo( Objetivo objetivo ) throws DAOException {

		update("UPDATE extensao.objetivo SET ativo = falseValue() WHERE id_objetivo = ? ", new Object[]{ objetivo.getId()}); 
		update("UPDATE extensao.objetivo_atividades SET ativo = falseValue() WHERE id_objetivo = ? ", new Object[]{ objetivo.getId()});
		Collection<Integer> idObjetivosAtividade = new ArrayList<Integer>();
		for (ObjetivoAtividades  linha : objetivo.getAtividadesPrincipais())
				idObjetivosAtividade.add( linha.getId() );
		
		if ( !idObjetivosAtividade.isEmpty() )
			update("UPDATE extensao.membro_atividade SET ativo = falseValue() WHERE id_objetivo_atividade in " + UFRNUtils.gerarStringIn(idObjetivosAtividade) ); 
	}

	/**
	 * Carregar os Membro de uma ação de extensão para atualização da carga horária do membro
	 * 
	 * @param objetivo
	 * @throws DAOException
	 */
	public Map<Integer, Integer> carregarMembrosObjetivo( AtividadeExtensao atividade ) throws DAOException {
		String sql = "select ma.id_membro_projeto, ma.carga_horaria" +
				" from extensao.objetivo o" +
				" join extensao.objetivo_atividades oa using ( id_objetivo )" +
				" join extensao.membro_atividade ma on ( oa.id_objetivo_atividades = ma.id_objetivo_atividade and oa.ativo = trueValue())" +
				" where o.id_atividade = :idAtividade and o.ativo = trueValue() and oa.ativo = trueValue() and ma.ativo = trueValue()" +
				" order by ma.id_membro_projeto";

		Query q = getSession().createSQLQuery(sql);
		q.setInteger("idAtividade", atividade.getId());
		List<Object[]> lista = q.list();
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		
		for (Object[] o : lista) {
			if ( result.get(o[0]) == null )
				result.put((Integer) o[0], (Integer) o[1]);
			else 
				result.put((Integer) o[0], ( result.get(o[0]) + (Integer) o[1]));
		}
		
		return result;
	}
	
	public void alterarCHMembroProjeto(Map<Integer, Integer> membrosAtividade) throws DAOException, SQLException {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			con = Database.getInstance().getSigaaConnection();
			st = con.createStatement();
			for (Integer membro : membrosAtividade.keySet()) {
				String update = "UPDATE projetos.membro_projeto" +
					" SET ch_dedicada = " + membrosAtividade.get(membro) +
					" WHERE id_membro_projeto = " + membro + ";";
				st.addBatch(update);
			}
			st.executeBatch();
        } finally {
    		closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);

		}
	}
	
}