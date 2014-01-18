package br.ufrn.sigaa.ensino_rede.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino_rede.dominio.ConvocacaoDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.StatusDiscenteAssociado;

public class ConvocacaoDiscenteAssociadoDao extends GenericSigaaDAO {

	@SuppressWarnings("unchecked")
	public Collection<ConvocacaoDiscenteAssociado> findConvocacao(int idCampus, int idProgramaRede) throws HibernateException, DAOException {
		
		String sql = "select distinct cda.id_convocacao_discente_associado, cda.descricao, cda.data" +
				" from ensino_rede.discente_associado da" +
				" join ensino_rede.dados_curso_rede dcr using ( id_dados_curso_rede )" +
				" join ensino_rede.convocacao_discente_associado cda on ( da.id_convocacao_discente_associado = cda.id_convocacao_discente_associado )" +
				" where dcr.id_campus = " + idCampus +
				" and dcr.id_programa_rede = " + idProgramaRede +
			    " and da.id_status = " + StatusDiscenteAssociado.PRE_CADASTRADO; 

		List <Object[]> ls = getSession().createSQLQuery(sql).list();
		
		List<ConvocacaoDiscenteAssociado> rs = new ArrayList<ConvocacaoDiscenteAssociado>();
		
		ConvocacaoDiscenteAssociado convocacao = null;
		
		for (Object [] l : ls){
			int i = 0;
			
			convocacao = new ConvocacaoDiscenteAssociado();
			convocacao.setId( (Integer) l[i ++] );
			convocacao.setDescricao((String) l[i ++]);
			convocacao.setData( (Date) l[i ++] );

			rs.add(convocacao);
		}
		
		return rs;
	}
	
};
