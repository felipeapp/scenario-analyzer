/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '14/03/2011'
 *
 */
package br.ufrn.sigaa.arq.dao.monitoria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.ProvaSelecao;
import br.ufrn.sigaa.monitoria.dominio.TipoProjetoEnsino;

/**
 * DAO que provê consultas ao banco de dados do sistema monitoria,
 * mais precisamente buscas relacionadas ao processo seletivo de monitores
 * dos projetos. 
 * 
 */
public class ProvaSelecaoDao  extends GenericSigaaDAO {

	
	/**
	 * Busca os projeto de monitoria nos quais um servidor informado
	 * é coordenador. 
	 * Busca específica para gerenciamento de provas seletivas. 
	 * 
	 * 
	 * @param docente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoEnsino> findProjetosCoordenadosByServidor(int idServidor) throws DAOException {

		try {
			String projecao = " pm.id, p.id, p.ano, p.titulo, ps.id, ps.titulo, " +
					"ps.vagasRemuneradas, ps.vagasNaoRemuneradas, ps.dataLimiteIncricao, ps.dataProva, sp.id, "+
					"(SELECT count(dm.id) FROM DiscenteMonitoria dm JOIN dm.provaSelecao ps1 WHERE dm.ativo = trueValue() AND ps1.id = ps.id) ";
			
			StringBuffer hql = new StringBuffer("SELECT ");
			hql.append(projecao);
			hql.append(" FROM ProjetoEnsino pm JOIN pm.projeto p JOIN p.coordenador c " +
					" LEFT JOIN pm.provasSelecao ps WITH (ps.ativo = trueValue()) " +
					" LEFT JOIN ps.situacaoProva sp ");
			hql.append(" WHERE pm.tipoProjetoEnsino.id != :tipoMonitoria " +
					"AND p.ativo = trueValue() " +
					"AND pm.ativo = trueValue() " +
					"AND c.ativo = trueValue() " +
					"AND c.servidor.id = :idServidor ");			
			hql.append(" order by pm.id, ps.id");

			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idServidor", idServidor);
			q.setInteger("tipoMonitoria", TipoProjetoEnsino.PROJETO_PAMQEG);

			List<Object> lista = new ArrayList<Object>();
			lista = q.list();
			
			ArrayList<ProjetoEnsino> result = new ArrayList<ProjetoEnsino>();
			
			int idOldPj = 0;
			int idOldPs = 0;
			for (int a = 0; a < lista.size(); a++) {
				int col = 0;
				
				Object[] colunas = (Object[]) lista.get(a);
				
				//Preenche os dados do projeto
				ProjetoEnsino pm = new ProjetoEnsino();				
				Integer idNewPj = (Integer) colunas[col++];
				pm.setId(idNewPj);
				if (idOldPj != idNewPj) {
					idOldPj = idNewPj;
					pm.getProjeto().setId((Integer) colunas[col++]);
					pm.getProjeto().setAno((Integer) colunas[col++]);
					pm.getProjeto().setTitulo((String) colunas[col++]);
					result.add(pm);
				}										
				
				col = 4;
				ProvaSelecao ps = new ProvaSelecao();
				Integer idNewPs = (Integer) colunas[col++];
				if (idNewPs != null) {
					ps.setId(idNewPs);		

					if (idOldPs != idNewPs) {
						idOldPs = idNewPs;
						ps.setTitulo((String) colunas[col++]);
						ps.setVagasRemuneradas((Integer) colunas[col++]);
						ps.setVagasNaoRemuneradas((Integer) colunas[col++]);
						ps.setDataLimiteIncricao((Date) colunas[col++]);
						ps.setDataProva((Date) colunas[col++]);
						ps.getSituacaoProva().setId((Integer) colunas[col++]);
						result.get(result.indexOf(pm)).getProvasSelecao().add(ps);
					}

					//Criando lista com a quantidade de resultados cadastrados.
					Long totalResultados = (Long)colunas[11];
					if (totalResultados != null) {
						List<ProvaSelecao> provas = result.get(result.indexOf(pm)).getProvasSelecao();
						//Discentes criados com ids zerados. Utilizado apenas para quantificar os cadastros de resultados da prova.
						for(int i=0; i<totalResultados.intValue();i++){
							provas.get(provas.indexOf(ps)).getResultadoSelecao().add(new DiscenteMonitoria(0));
						}						
					}
				}
			}

			return result;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

}
