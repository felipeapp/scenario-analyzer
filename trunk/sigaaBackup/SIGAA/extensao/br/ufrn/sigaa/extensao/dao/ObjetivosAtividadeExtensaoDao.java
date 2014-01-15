/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 05/09/2012
 * 
 */
package br.ufrn.sigaa.extensao.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.extensao.dominio.Objetivo;
import br.ufrn.sigaa.extensao.dominio.ObjetivoAtividades;
import br.ufrn.sigaa.extensao.dominio.ProgramaExtensao;

/**
 * <p> Dao exclusivo para as consultas sobre objetivos de atividade de extensão </p>
 *
 * <p> <i> <strong>IMPORTANTE</strong> Use projeção nas consultas. Obrigado.</i> </p>
 * 
 * @author jadson
 *
 */
public class ObjetivosAtividadeExtensaoDao extends GenericSigaaDAO{

	
	/**
	 * Retorna os objetivos ativos do programa de extensão passado
	 * 
	 * @param idProgramaExtensao
	 * @return
	 * @throws DAOException
	 * @Deprecated Método usado enquanto os novos formulário não ficam prontos.  Por objetivos não 
	 * vai está associado ao programa, e sim diretamente à atividade de extensão.
	 */
	@Deprecated
	public List<Objetivo> findObjetivosAtivosProgramaExtensao(ProgramaExtensao programa)throws DAOException {
		
		String projecao = " objetivo.id, objetivo.objetivo, objetivo.quantitativos, objetivo.qualitativos, objetivo.ativo,"
				+" atividadesPrincipais.id, atividadesPrincipais.descricao, atividadesPrincipais.dataInicio, atividadesPrincipais.dataFim ";

		String hql = " SELECT DISTINCT " + projecao + "" +
				" FROM  Objetivo objetivo " +
				" LEFT JOIN  objetivo.atividadesPrincipais atividadesPrincipais " + // Pode ser que os objetivos não tenham atividades
				" WHERE objetivo.programaExtensao.id = :idProgramaExtensao AND objetivo.ativo = :true ";
			
		Query query = getSession().createQuery(hql);
		query.setInteger("idProgramaExtensao", programa.getId());
		query.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List<Object[]> dados = query.list();
		
		List<Objetivo> lista = new ArrayList<Objetivo>();
		
		for (Object[] dado : dados) {
//			Objetivo objetivo = new Objetivo((Integer) dado[0], (String) dado[1],  (String) dado[2], (String) dado[3], (Boolean) dado[4] );
//			objetivo.setProgramaExtensao(programa);
			
//			if(! lista.contains(objetivo))
//				lista.add(objetivo);
//			else
//				objetivo = lista.get(lista.indexOf(objetivo));
//			
//			if( (Integer) dado[5] != null){
//				ObjetivoAtividades atividadesDoObjetivo = new ObjetivoAtividades((Integer) dado[5], (String) dado[6],  (Date) dado[7], (Date) dado[8], objetivo );
//				objetivo.addAtividadesPrincipais(atividadesDoObjetivo);
//			}
		}
		
		
		return lista;
		
	}
	
	
	
}
