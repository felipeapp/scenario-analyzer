/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 01/12/2011
 */
package br.ufrn.sigaa.ensino.infantil.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.infantil.dominio.FormularioEvolucaoCrianca;
import br.ufrn.sigaa.ensino.infantil.dominio.FormularioTurma;
import br.ufrn.sigaa.ensino.infantil.dominio.ItemInfantilFormulario;
import br.ufrn.sigaa.ensino.infantil.dominio.ItemInfantilPeriodo;

/**
 * Dao com consultas para o Formulário de Evolução da Criança do Ensino Infantil.
 * 
 * @author Leonardo Campos
 *
 */
public class FormularioEvolucaoCriancaDao extends GenericSigaaDAO {

	/**
	 * Retornao formulário de Evolucção da turma, de acordo com a turma inforamada. 
	 * 
	 * @param idTurma
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public FormularioEvolucaoCrianca findByTurma( int idTurma, int idMatriculaComponente ) throws HibernateException, DAOException {

		String sql = "select fec.id_formulario_evolucao_crianca, fec.data_cadastro, fec.ativo," +
				" iif.id_item_infantil_formulario, iif.profundidade, iif.ordem, iif.periodo," +
				" ii.id_item_infantil, ii.descricao, ii.tem_observacao, ii.ativo as ii_ativo, ta.id_tipo_forma_avaliacao," +
				" ta.legenda, fec.id_formulario_evolucao_crianca as fec_id_formulario_evolucao_crianca," +
				" iip.id_item_infantil_periodo as iip_id_item_infantil_periodo, iip.periodo as iip_periodo," +
				" iip.observacoes, iip.resultado, iip.id_matricula_componente," +
				" iip.id_item_infantil_formulario as iip_id_item_infantil_formulario" +
				" from infantil.formulario_turma ft" +
				" join infantil.formulario_evolucao_crianca fec using ( id_formulario_evolucao_crianca )" +
				" join infantil.item_infantil_formulario iif using ( id_formulario_evolucao_crianca )" +
				" join infantil.item_infantil ii using ( id_item_infantil )" +
				" left join infantil.tipo_forma_avaliacao ta on ( ii.forma_avaliacao = ta.id_tipo_forma_avaliacao ) " +
				" left join infantil.item_infantil_periodo iip on " +
				"	( iip.id_item_infantil_formulario = iif.id_item_infantil_formulario  and id_matricula_componente = :idMatriculaComponente )" +
				" where id_turma = :idTurma and ii.ativo and fec.ativo" +
				" order by iif.periodo, ordem";
		
		Query q = getSession().createSQLQuery(sql);
		q.setLong("idTurma", idTurma);
		q.setLong("idMatriculaComponente", idMatriculaComponente);
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		FormularioEvolucaoCrianca formEvol = null;
		if ( !list.isEmpty() ) {
			formEvol = new FormularioEvolucaoCrianca();
				for (Object[] obj : list){
					int i = 0;
					if ( formEvol.getId() == 0 ) {
						formEvol.setId((Integer) obj[i++]);
						formEvol.setDataCadastro((Date) obj[i++]);
						formEvol.setAtivo((Boolean) obj[i++]);
					} else {
						i = 3;
					}
					
					ItemInfantilFormulario itemInfForm = new ItemInfantilFormulario();
					itemInfForm.setId((Integer) obj[i++]);
					itemInfForm.setProfundidade((Integer) obj[i++]);
					itemInfForm.setOrdem((Integer) obj[i++]);
					itemInfForm.setPeriodo((Integer) obj[i++]);
					
					itemInfForm.getItem().setId((Integer) obj[i++]);
					itemInfForm.getItem().setDescricao((String) obj[i++]);
					itemInfForm.getItem().setTemObservacao((Boolean) obj[i++]);
					itemInfForm.getItem().setAtivo((Boolean) obj[i++]);

					Integer idFormaAva = (Integer) obj[i++];
					if ( idFormaAva != null  ) {
						itemInfForm.getItem().getFormaAvaliacao().setId( idFormaAva );
						itemInfForm.getItem().getFormaAvaliacao().setLegenda((String) obj[i++]);
					} else {
						i++;
					}

					itemInfForm.setEditavel(Boolean.FALSE);
					
					itemInfForm.setFormulario(new FormularioEvolucaoCrianca());
					itemInfForm.getFormulario().setId( (Integer) obj[i++] );
					
					Integer idItemPeriodo = (Integer) obj[i++];
					itemInfForm.setItemPeriodo( new ItemInfantilPeriodo() );
					if (  idItemPeriodo != null ) {
						itemInfForm.getItemPeriodo().setId( idItemPeriodo );
						itemInfForm.getItemPeriodo().setPeriodo( (Integer) obj[i++] );
						itemInfForm.getItemPeriodo().setObservacoes( (String) obj[i++] );
						itemInfForm.getItemPeriodo().setResultado( (Integer) obj[i++] );
						itemInfForm.getItemPeriodo().setMatricula(new MatriculaComponente((Integer) obj[i++]));
						itemInfForm.getItemPeriodo().setItemFormulario( new ItemInfantilFormulario() );
						itemInfForm.getItemPeriodo().getItemFormulario().setId( (Integer) obj[i++] );
					}

					if ( isEmpty( formEvol.getItens() )  ) {
						formEvol.setItens( new ArrayList<ItemInfantilFormulario>() );
						formEvol.getItens().add(itemInfForm);
					} else {
						formEvol.getItens().add(itemInfForm);
					}
					
			}
		}
		return formEvol;
	}
	
	/**
	 * Retorna os formulários da Turma, cujo componente foi informado.
	 * 
	 * @param idComponente
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<FormularioTurma> findByComponenteTurma(int idComponente) throws HibernateException, DAOException {
		String hql = "from FormularioTurma ft where ft.turma.disciplina.id = :idComponente and ft.formulario.ativo = trueValue()";
		@SuppressWarnings("unchecked")
		List<FormularioTurma> list = getSession().createQuery(hql).setInteger("idComponente", idComponente).list();
		return list;
	}
}
