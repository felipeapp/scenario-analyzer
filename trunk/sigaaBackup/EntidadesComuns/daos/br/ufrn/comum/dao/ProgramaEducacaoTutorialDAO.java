package br.ufrn.comum.dao;

import br.ufrn.academico.dominio.ProgramaEducacaoTutorial;
import br.ufrn.arq.dao.GenericJdbcDAO;

/**
 * DAO responsável pela persistência de entidades PET.
 * Utiliza consultas com JDBCTemplate para sincronizar 
 * operações entre diferentes bancos
 * 
 * @author wendell
 *
 */
@Deprecated
public class ProgramaEducacaoTutorialDAO extends GenericJdbcDAO {

	private static ProgramaEducacaoTutorialDAO instance = new ProgramaEducacaoTutorialDAO();
	
	public static ProgramaEducacaoTutorialDAO getInstance() {
		return instance;
	}
	
	/**
	 * Persiste uma entidade PET nos bancos academico e administrativo
	 * 
	 * @param pet
	 */
	public void create(ProgramaEducacaoTutorial pet) {
		int id = getComumTemplate().queryForInt("(select nextval('hibernate_sequence'))");
		pet.setId(id);
		
		Object[] array = new Object[] { pet.getId(), pet.getDescricao(), pet.getIdCurso(), pet.getIdAreaConhecimentoCnpq(), 
				pet.getDataInicio(), pet.getDataFim(), pet.getLimiteBolsas(), pet.isAtivo() };
		
		String corpoInsert = "(id_pet, descricao, id_curso, id_area_conhecimento_cnpq, data_inicio, data_fim, limite_bolsas, ativo) values (?,?,?,?,?,?,?,?)";
		
		getSigaaTemplate().update("insert into prodocente.pet " + corpoInsert, array);
	}
	
	/**
	 * Atualiza os dados de uma entidade PET
	 * 
	 * @param pet
	 */
	public void update(ProgramaEducacaoTutorial pet) {
		String corpoUpdate = "set descricao=?, id_curso=?, id_area_conhecimento_cnpq = ?, data_inicio = ?, data_fim = ?, limite_bolsas = ?, ativo = ?  where id_pet=?";
		Object[] array = new Object[] {pet.getDescricao(), pet.getIdCurso(), pet.getIdAreaConhecimentoCnpq(),
				pet.getDataInicio(), pet.getDataFim(), pet.getLimiteBolsas() ,pet.isAtivo(),  pet.getId() };
		
		getSigaaTemplate().update("update prodocente.pet " + corpoUpdate, array);
	}
	
	public void createOrUpdate(ProgramaEducacaoTutorial pet) {
		if (pet.getId() == 0) {
			create(pet);
		} else {
			update(pet);
		}
	}
	
}
