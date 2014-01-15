package br.ufrn.sigaa.extensao.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.extensao.dominio.AndamentoAtividade;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.Objetivo;
import br.ufrn.sigaa.extensao.dominio.ObjetivoAtividades;
import br.ufrn.sigaa.extensao.relatorio.dominio.TipoRelatorioExtensao;

/**
 * Dao exclusivo para as consultas sobre o andamento dos objetivos 
 * das atividades de extensão
 *  
 * @author jean
 */
public class AndamentoObjetivoDao extends GenericSigaaDAO{

	/**
	 * Carregar os Objetivos da Atividade de Extensão
	 * @param idAtividade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<AndamentoAtividade> findAndamentoAtividades( Integer idAtividadeExtensao, Integer tipoRelatorio, boolean importar ) throws DAOException {
		String projecao = " o.id_objetivo, o.objetivo, o.id_atividade, oa.id_objetivo_atividades, oa.descricao, oa.data_inicio," +
					 	  " oa.data_fim, oa.carga_horaria, aa.id_andamento_atividade, " +
					 	  " aa.andamento_atividade, aa.status_atividade, o.observacao_execucao ";
		
		String sql = " select " + projecao +
					 " from extensao.objetivo o" +
					 " left join extensao.objetivo_atividades oa using ( id_objetivo ) " +
					 " left join extensao.andamento_atividade aa on ( aa.id_atividade = oa.id_objetivo_atividades and id_tipo_relatorio_andamento = "+ tipoRelatorio +" and aa.ativo)" +
					 " where o.id_atividade = " + idAtividadeExtensao +
				   	 " and o.ativo = trueValue()" +
				   	 " order by o.id_objetivo asc, oa.data_inicio";
		
		Query q = getSession().createSQLQuery(sql);
			
		List<Object[]> lista = q.list();
		List<AndamentoAtividade> result = new ArrayList<AndamentoAtividade>();
		for (Object[] objects : lista) {
			AndamentoAtividade linha = null;
			int count = 0;
			linha = new AndamentoAtividade();
			linha.setAtividade(new ObjetivoAtividades());
			linha.getAtividade().setObjetivo(new Objetivo( (Integer) objects[count++] ));
			linha.getAtividade().getObjetivo().setObjetivo( (String) objects[count++] );
			linha.getAtividade().getObjetivo().setAtividadeExtensao(new AtividadeExtensao((Integer) objects[count++]));
			linha.getAtividade().setId( objects[count] != null ? (Integer) objects[count] : 0 );
			linha.getAtividade().setDescricao( (String) objects[++count] );
			linha.getAtividade().setDataInicio( (Date) objects[++count] );
			linha.getAtividade().setDataFim( (Date) objects[++count] );
			linha.getAtividade().setCargaHoraria( (Integer) objects[++count] );
			linha.setId( (Integer) objects[++count] != null && !importar ? (Integer) objects[count] : 0);
			linha.setAndamentoAtividade( (Integer) objects[++count] != null ? (Integer) objects[count] : 0 );
			linha.setStatusAtividade( (Integer) objects[++count] != null ? (Integer) objects[count] : 0 );
			linha.getAtividade().getObjetivo().setObservacaoExecucao( (String) objects[++count] );
			if ( importar )
				linha.setTipoRelatorio(new TipoRelatorioExtensao(TipoRelatorioExtensao.RELATORIO_FINAL));
			else
				linha.setTipoRelatorio(new TipoRelatorioExtensao(tipoRelatorio));
			
			result.add(linha);
		}

		return result;
	}

}