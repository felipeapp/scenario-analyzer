/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '11/11/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.erros.DAOException;
/**
 * Dao responsável por emitir relatório de bancas por orientador. Informa as bancas que eles participaram seja, como orientador, co-orientador ou membro.
 * @author geyson 
 *
 */
public class RelatorioBancasOrientadorDao extends AbstractRelatorioSqlDao {

/**
 * Relatório de Bancas por Orientador	
 * @param idOrientador
 * @return
 * @throws DAOException
 */
public List<Map<String,Object>> relatorioBancasOrientador(Integer idOrientador, Date dataInicial, Date dataFinal) throws DAOException {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	StringBuilder sqlconsulta = new StringBuilder(" select " +  
	"(select p.nome from rh.servidor s " +  
	"inner join comum.pessoa p on s.id_pessoa = p.id_pessoa " +  
	"where (s.id_servidor = mb.id_servidor or s.id_servidor = mb.id_docente_externo )) as docente," +  
	  
	" b.titulo as titulo, " +  
	  
	"(case when mb.id_tipo_membro_banca = 1 then 'EXAMINADOR INTERNO' " +  
	"when mb.id_tipo_membro_banca = 2 then 'EXAMINADOR EXTERNO' " +  
	"when mb.id_tipo_membro_banca = 3 then 'EXAMINADOR EXTERNO A INSTITUICAO' end) as funcao, " +  
	  
	"b.data_defesa as data " +  
	  
	"from rh.servidor s1 " +  
	"inner join ensino.membro_banca mb on (s1.id_servidor = mb.id_servidor or s1.id_servidor = mb.id_docente_externo) " +  
	"inner join ensino.banca b on (mb.id_banca = b.id_banca) ");  

	sqlconsulta.append(" where (mb.id_servidor = "+ idOrientador +" or mb.id_docente_externo = "+ idOrientador +") ");  
	  
	if(dataFinal != null){  
		Calendar calendar = Calendar.getInstance();  
		calendar.setTime(dataFinal);  
		calendar.add(Calendar.DATE, 1);  
		  
		  
		sqlconsulta.append(" and data_defesa between '" + sdf.format(dataInicial) + "' and '"+ sdf.format(calendar.getTime()) +"' ");  
	}  
	else  
		sqlconsulta.append(" and data_defesa >= '" + sdf.format(dataInicial) + "'");
	
	sqlconsulta.append(" order by data_defesa asc ");  
	  
	List result;  
	  
	try {  
		result = executeSql(sqlconsulta.toString());  
	
	} catch (Exception e) {
		e.printStackTrace();
		throw new DAOException(e);
	}  
	
	
	return result;  
	

}
	
}
