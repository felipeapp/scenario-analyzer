/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 06/10/2008
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormatoMaterialEtiqueta;

/**
 *
 *    DAO para buscas por etiquetas.
 *
 * @author Fred
 * @since 26/09/2008
 * @version 1.0 Criação da classe
 *
 */
public class EtiquetaDao extends GenericSigaaDAO {

	/**
	 *     Método que deve ser usado para buscar as etiquetas dos campos. Qualquer outro método 
	 * que busca as etiquetas apenas pelas tags não vai trazer os resultados corretos.
	 *
	 * @param tag
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public Etiqueta findEtiquetaPorTagETipoAtiva(String tag, int tipo) throws DAOException{

		String hql = " SELECT e FROM Etiqueta e WHERE e.tag = :tag  and  e.tipo = :tipo and e.ativa = trueValue() ";
		Query q = getSession().createQuery(hql);
		q.setString("tag", tag);
		q.setInteger("tipo", tipo);
		return (Etiqueta) q.uniqueResult();

	}

	/**
	 *     <p>Método que deve ser usado para buscar as etiquetas dos campos. </p>
	 *     
	 *     <p>Já traz a etiqueta com os descritores dos subcampos, indicadores e valores dos subcampos
	 *     inicializados para não realizar várias consultas no banco</p> 
	 *
	 * @param tag
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public Etiqueta findEtiquetaPorTagETipoAtivaInicializandoDados(String tag, int tipo) throws DAOException{

		long tempo = System.currentTimeMillis();
		
		String hql = " SELECT e FROM Etiqueta e "
			+" LEFT JOIN FETCH e.valoresIndicador as valorIndicador "
			+" LEFT JOIN FETCH e.descritorSubCampo as descritores "
			+" LEFT JOIN FETCH descritores.valoresDescritorSubCampo as valoresDescritores "
			+" WHERE e.tag = :tag  and  e.tipo = :tipo and e.ativa = trueValue() ";
		Query q = getSession().createQuery(hql);
		q.setString("tag", tag);
		q.setInteger("tipo", tipo);
		Etiqueta temp = (Etiqueta) q.uniqueResult();

		System.out.println("Buscar Informações Etiqueta demorou:  "+(System.currentTimeMillis()-tempo)+" ms");
		return temp;
	}
	
	
	/**
	 * Retorna uma etiqueta com os descritores, indicadores e valores inicializados. Normalmente
	 * é passada como parâmetro uma das constantes da classe <tt>Etiqueta</tt>, como
	 * <tt>Etiqueta.TITULO</tt>. Os campos <tt>tag</tt> e <tt>tipo</tt> devem estar inicializados
	 * para esse método funcionar. 
	 *
	 * @param e
	 * @return
	 * @throws DAOException
	 */
	public Etiqueta findEtiquetaInicializandoDados( Etiqueta e ) throws DAOException {
		return findEtiquetaPorTagETipoAtivaInicializandoDados(
				StringUtils.leftPad( e.getTag(), 3, '0'), e.getTipo() );
	}


	/**
	 *     <p>Método que recupera todas as etiquetas locais ativas no sistema.</p>
	 *     
	 *    <p> Encontra todas as etiquetas locais definidas no padrão MARC (09X, 59X, 69X) + as etiquetas 9XX</p>
	 *
	 *     <p>'490' por exemplo não é uma etiqueta local.</p>
	 *
	 * @param tag
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */

	public List<Etiqueta> findAllEtiquetasLocaisAtivas() throws DAOException{

		String hql = "SELECT e FROM Etiqueta e WHERE ( e.tag like '09%' or e.tag like '59%' or e.tag like '69%' or e.tag like '9%' ) and e.ativa = trueValue() ";
		Query q = getSession().createQuery(hql);

		@SuppressWarnings("unchecked")
		List<Etiqueta> lista = q.list();
		return lista;
	}

	
	/**
	 *     <p>Método que recupera todas as etiquetas do MARC ativas no sistema.</p>
	 *    
	 *
	 * @param tag
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */

	public List<Etiqueta> findAllEtiquetasAtivas() throws DAOException{

		String hql = "SELECT e FROM Etiqueta e WHERE  e.ativa = trueValue() ";
		Query q = getSession().createQuery(hql);

		@SuppressWarnings("unchecked")
		List<Etiqueta> lista = q.list();
		return lista;
	}
	

	/**
	 * Método que procura por todas as etiquetas do tipo passado e que contenham a tag contendo a substring passada.
	 * 
	 * @param tipoCampo
	 * @param tag
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */	
	public List <Etiqueta> findEtiquetasByTipoCampoAndTipoAndTagLikeAtivas(char tipoCampo, String tag, int tipoEtiqueta) throws DAOException{
		String hql = "SELECT e FROM Etiqueta e WHERE e.tipoCampo = :tipoCampo and e.tag like '%" + tag + "%' and tipo = :tipo e.ativa = trueValue()";
		Query q = getSession().createQuery(hql);
		q.setCharacter("tipoCampo", tipoCampo);
		q.setInteger("tipo", tipoEtiqueta);

		@SuppressWarnings("unchecked")
		List<Etiqueta> lista = q.list();
		return lista;
	}

	/**
	 * 
	 *   Método que busca no banco uma etiqueta trazendo os valores dos indicadores, descritores do campo
	 * e os valores dos descritores. Usado para otimizar as consultas quando se deseja trazer todas
	 * as informações de uma etiqueta para validação.
	 *
	 * Chamado a partir da página: /biblioteca/
	 * Método não chamado por nenhuma página jsp.
	 *
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Etiqueta findDadosCompletosEtiquetaParaValidacao(Etiqueta e) throws DAOException{

		String hql = new String( " SELECT e FROM Etiqueta e "
				+" LEFT JOIN FETCH e.valoresIndicador as valorIndicador "
				+" LEFT JOIN FETCH e.descritorSubCampo as descritores "
				+" LEFT JOIN FETCH descritores.valoresDescritorSubCampo as valoresDescritores "
				+" WHERE e.id  = :idEtiqueta ");

		Query q = getSession().createQuery(hql);
		q.setInteger("idEtiqueta", e.getId());
		return  (Etiqueta) q.uniqueResult();
	}

	/**
	 *   @Deprecated Foi usado apenas na migração. Não utilizar mais.
	 */
	@Deprecated
	public FormatoMaterialEtiqueta findFormatoMaterialEtiquetaByEtiquetaFormato( String etiqueta, String siglaFormato ) throws DAOException{
		Query q = getSession().createQuery( "SELECT fme FROM FormatoMaterialEtiqueta fme WHERE fme.etiqueta.tag = :etiqueta AND fme.formatoMaterial.sigla = :sigla " );
		q.setString("etiqueta", etiqueta);
		q.setString("sigla", siglaFormato);
		return (FormatoMaterialEtiqueta) q.uniqueResult();
	}

}
