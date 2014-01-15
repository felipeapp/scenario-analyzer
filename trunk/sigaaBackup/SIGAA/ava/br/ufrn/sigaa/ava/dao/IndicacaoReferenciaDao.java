package br.ufrn.sigaa.ava.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.IndicacaoReferencia;
import br.ufrn.sigaa.ava.dominio.MaterialTurma;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * DAO para consultas de referencias.
 * @author Suelton Miguel
 *
 */
public class IndicacaoReferenciaDao extends  GenericSigaaDAO {
	
	/**
	 * Retorna as referências de determinada turma de acrodo com o acesso.
	 * @param turma, isDocente
	 * @return
	 */
	public List<IndicacaoReferencia> findReferenciasTurma(Turma turma, boolean isDocente) {
		try {
			String projecao = " r.id, r.descricao, r.url, r.tipoIndicacao, r.tipo, r.aula.id, r.material.id, r.material.ordem, r.material.nivel ";
			
			String hql = "SELECT " + projecao + " FROM IndicacaoReferencia r " +
						 "LEFT JOIN r.aula a " +
						 "WHERE r.ativo = trueValue() AND r.turma.id = :idTurma";
						 
			if (!isDocente) {
				hql += " AND (r.aula is NULL OR a.visivel = trueValue())";  
			}
			
			hql += " ORDER BY r.material.ordem";
			
			Query q = getSession().createQuery(hql);
			
			
			q.setInteger("idTurma", turma.getId());
			
			@SuppressWarnings("unchecked")
			List<Object[]> results = q.list();
			
			ArrayList<IndicacaoReferencia> referencias = null;
			if ( results != null ) {
				referencias = new ArrayList<IndicacaoReferencia>();
				for(Object[] linha:results) {
					if(linha != null) {
						Integer i = 0;
						IndicacaoReferencia r = new IndicacaoReferencia();
						r.setId((Integer) linha[i++]);
						r.setDescricao((String) linha[i++]);
						r.setUrl((String) linha[i++]);
						r.setTipoIndicacao((Integer)linha[i++]);
						r.setTipo((Character)linha[i++]);
						
						if (linha[i] != null) {
							TopicoAula a = new TopicoAula();
							a.setId((Integer)linha[i++]);
							r.setAula(a);
						}
						else {
							i++;
						}
						
						MaterialTurma m = new MaterialTurma();
						m.setId((Integer)linha[i++]);
						m.setOrdem((Integer) linha[i++]);
						m.setNivel((Integer) linha[i++]);
						
						r.setMaterial(m);
						referencias.add(r);
					}
										
				}
			}
			return referencias;
			
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
		
	}


}
