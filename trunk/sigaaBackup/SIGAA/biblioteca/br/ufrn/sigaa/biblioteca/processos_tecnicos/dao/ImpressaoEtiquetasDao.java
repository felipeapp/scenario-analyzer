/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 21/03/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.StatusMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormaDocumento;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MateriaisMarcadosParaGerarEtiquetas;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;

/**
 *
 * <p>Dao específico para as consultas utilizadas na parte de geração de etiquetas </p>
 * 
 * @author jadson
 *
 */
public class ImpressaoEtiquetasDao  extends GenericSigaaDAO {

	
	/**
	 * A projeção padrão para consulta para impressão de etiquetas
	 */
	private static final String PROJECAO_PADRAO = " m.id, m.codigoBarras, m.numeroChamada, m.segundaLocalizacao, m.biblioteca.identificador, m.biblioteca.descricao, m.colecao.descricao, m.situacao.descricao, m.status.descricao, m.tipoMaterial.descricao, forma.denominacao ";
	
	/**
	 * A consulta padrão para a impressão de etiquetas, geralmente só muda a cláusula "WHERE"
	 */
	private static final String CONSULTA_PADRAO = "SELECT "+PROJECAO_PADRAO+" FROM MaterialInformacional m LEFT JOIN m.formasDocumento forma ";
	
	/**
	 *    Método que encontra um material ativo, seja ele exemplar ou fascículos ativo pelo código 
	 *  de barras.
	 *
	 * @param codigoBarras
	 * @return
	 * @throws DAOException
	 */

	public MaterialInformacional findMaterialAtivoByCodigoBarras(String codigoBarras) throws DAOException {
		
		String hql = CONSULTA_PADRAO
				+" WHERE m.codigoBarras = :codigoBarras AND m.situacao.situacaoDeBaixa = falseValue() AND m.ativo = trueValue() ";
		Query q = getSession().createQuery(hql);
		q.setString("codigoBarras", codigoBarras);
		
		@SuppressWarnings("unchecked")
		List<Object[]> infoMaterial = q.list();  // Traz 1 objeto para cadas formato de material que o material tenha
		
		if( infoMaterial == null)
			return null;
		
		MaterialInformacional m = new Exemplar(); // não importa se é exemplar os fascículos as regras são as mesmas
		
		m.setId( (Integer) infoMaterial.get(0)[0]);
		m.setCodigoBarras( (String) infoMaterial.get(0)[1]);
		m.setNumeroChamada( (String) infoMaterial.get(0)[2]);
		m.setSegundaLocalizacao( (String) infoMaterial.get(0)[3]);
		m.setBiblioteca( (new Biblioteca(   (String) infoMaterial.get(0)[4],  (String) infoMaterial.get(0)[5]) )   );
		m.setColecao( (new Colecao(   (String) infoMaterial.get(0)[6]) )   );
		m.setSituacao( (new SituacaoMaterialInformacional(   (String) infoMaterial.get(0)[7]) )   );
		m.setStatus( (new StatusMaterialInformacional(   (String) infoMaterial.get(0)[8]) )   );
		m.setTipoMaterial( (new TipoMaterial(   (String) infoMaterial.get(0)[9]) )   );
		
		Collection<FormaDocumento> formasDocumento = new ArrayList<FormaDocumento>();
		
		for (Object[] objects : infoMaterial) {
			if( StringUtils.notEmpty((String) objects[10]) )
				formasDocumento.add( new FormaDocumento( (String) objects[10] ) );
		}
		
		m.setFormasDocumento( formasDocumento );
		
		return m;
	}
	
	
	
	/**
	 * 
	 *  Busca os materiais ativos por faixa de código de barras. 
	 *
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public List <MaterialInformacional> findMateriaisAtivosByFaixaCodigosBarras(String codigosBarrasInicial, String codigoBarrasFinal) throws DAOException {
		
		String hql = CONSULTA_PADRAO
		+" WHERE ( m.codigoBarras BETWEEN :codigoBarrasInicial AND :codigoBarrasFinal ) AND m.situacao.situacaoDeBaixa = falseValue() AND m.ativo = trueValue() ";
		Query q = getSession().createQuery(hql);
		q.setString("codigoBarrasInicial", codigosBarrasInicial);
		q.setString("codigoBarrasFinal", codigoBarrasFinal);
		@SuppressWarnings("unchecked")
		
		List<Object[]> infoMaterial = q.list();  // Traz 1 objeto para cadas formato de material que o material tenha
		
		return montaInformacoesMaterialParaImpressaoEtiqueta(infoMaterial);
	}
	
	
	/**
	 *   Conta os materiais ativos por faixa de código de barras. 
	 *
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public Integer countMateriaisAtivosByFaixaCodigosBarras(String codigosBarrasInicial, String CodigoBarrasFinal) throws DAOException {
		
		Integer quantidade = 0;
		
		Criteria c = getSession().createCriteria(MaterialInformacional.class);
		c.setProjection(Projections.distinct(Projections.countDistinct("id")));
		c.add(Restrictions.between("codigoBarras", codigosBarrasInicial, CodigoBarrasFinal));
		c.add(Restrictions.eq("ativo", true));
		c.createCriteria("situacao").add(Restrictions.eq("situacaoDeBaixa", false));
		quantidade = (Integer) c.list().get(0);
		
		return quantidade;
		
	}
	
	
	
	
	
	/**
	 * <p>   Encontra todos os materiais marcados para a geração de etiqueta do usuário passado </p>
	 * 
	 * @param idBiblioteca a biblioteca do material, se for nulo trás de todas das bibliotecas 
	 * @param idUsuario id do usuário que incluiu o material no acervo mas ainda não imprimiu a etiqueta.
	 * @return
	 * @throws DAOException
	 */
	public List<MaterialInformacional> findMateriaisPendentesGeracaoEtiquetaDoUsuario(int idUsuario, List<Integer> idsBiblioteca ) throws DAOException {
		
		String hql = " SELECT m FROM MateriaisMarcadosParaGerarEtiquetas m "
			+" WHERE m.idUsuarioMarcouGeracaoEtiqueta = :idUsuario ";
		
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idUsuario", idUsuario);
		
		@SuppressWarnings("unchecked")
		List<MateriaisMarcadosParaGerarEtiquetas> listaMateriaisPendentes = q.list();
		
		List<Integer> idMateriaisPendentes = new ArrayList<Integer>();
		
		for (MateriaisMarcadosParaGerarEtiquetas m: listaMateriaisPendentes) {
			if(m.getIdExemplar() != null)
				idMateriaisPendentes.add(m.getIdExemplar());
			
			if(m.getIdFasciculo() != null)
				idMateriaisPendentes.add(m.getIdFasciculo());
		}
		
		if(idMateriaisPendentes.size() == 0)
			return new ArrayList<MaterialInformacional>();
		
		String hql2 = CONSULTA_PADRAO
		+" WHERE  m.id in ( :idMateriaisPendentes )  ";
		
		if(idsBiblioteca != null && idsBiblioteca.size() > 0)
			hql2 += " AND m.biblioteca.id in (:idsBiblioteca) "; 
		
		Query q2 = getSession().createQuery(hql2);
		q2.setParameterList("idMateriaisPendentes", idMateriaisPendentes);
		
		if(idsBiblioteca != null && idsBiblioteca.size() > 0)
			q2.setParameterList("idsBiblioteca", idsBiblioteca);
		
		@SuppressWarnings("unchecked")
		List<Object[]> infoMaterial = q2.list();
		
		return montaInformacoesMaterialParaImpressaoEtiqueta(infoMaterial);
		
	}
	
	
	/**
	 * <p>   Encontra todos os materiais marcados para a geração de etiqueta  </p>
	 * 
	 * @param idBiblioteca a biblioteca do material, se for nulo trás de todas das bibliotecas 
	 * @return
	 * @throws DAOException
	 */
	public List<MaterialInformacional> findMateriaisPendentesGeracaoEtiqueta(List<Integer> idsBiblioteca) throws DAOException {
		
		String hql = " SELECT m FROM MateriaisMarcadosParaGerarEtiquetas m ";
		
		Query q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		List<MateriaisMarcadosParaGerarEtiquetas> listaMateriaisPendentes = q.list();
		
		List<Integer> idMateriaisPendentes = new ArrayList<Integer>();
		
		for (MateriaisMarcadosParaGerarEtiquetas m: listaMateriaisPendentes) {
			if(m.getIdExemplar() != null)
				if(m.getIdExemplar() != null)
					idMateriaisPendentes.add(m.getIdExemplar());
				
				if(m.getIdFasciculo() != null)
					idMateriaisPendentes.add(m.getIdFasciculo());
		}
		
		
		if(idMateriaisPendentes.size() == 0)
			return new ArrayList<MaterialInformacional>();
		
		String hql2 = CONSULTA_PADRAO
		+" WHERE ( m.id in ( :idMateriaisPendentes ) ";
		
		if(idsBiblioteca != null && idsBiblioteca.size() > 0)
			hql2 += " AND m.biblioteca.id in (:idsBiblioteca) "; 
		
		Query q2 = getSession().createQuery(hql2);
		q2.setParameterList("idMateriaisPendentes", idMateriaisPendentes);
		
		if(idsBiblioteca != null && idsBiblioteca.size() > 0)
			q2.setParameterList("idsBiblioteca", idsBiblioteca);
		
		@SuppressWarnings("unchecked")
		List<Object[]> infoMaterial = q2.list();
		
		return montaInformacoesMaterialParaImpressaoEtiqueta(infoMaterial);
		
	}
	
	
	/**
	 *  <p>Monta os dados dos materiais informacionais para a impressão de etiquetas </p>
	 *
	 *  <p>O hibernateUtils.parteTo não funciona nesse caso </p>
	 *
	 * @param infoMaterial
	 * @return
	 */
	private List<MaterialInformacional> montaInformacoesMaterialParaImpressaoEtiqueta(List<Object[]> infoMaterial ){

		List <MaterialInformacional> materiais = new ArrayList <MaterialInformacional> ();
		
		if( infoMaterial == null)
			return materiais;
		
		for (Object[] objects : infoMaterial) {  
			
			MaterialInformacional mTemp = new Exemplar( (Integer) objects[0]  );
			
			if( materiais.contains(mTemp)  ){
				MaterialInformacional m = materiais.get(materiais.indexOf( mTemp));
				m.adicionaFormasDocumento(    new FormaDocumento( (String) objects[9] )    );
			}else{
				
				mTemp.setCodigoBarras( (String) objects[1]);
				mTemp.setNumeroChamada( (String) objects[2]);
				mTemp.setSegundaLocalizacao( (String) objects[3]);
				mTemp.setBiblioteca( (new Biblioteca((String) objects[4],    (String) objects[5]) )   );
				mTemp.setColecao( (new Colecao(   (String) objects[6]) )   );
				mTemp.setSituacao( (new SituacaoMaterialInformacional(   (String) objects[7]) )   );
				mTemp.setStatus( (new StatusMaterialInformacional(   (String) objects[8]) )   );
				mTemp.setTipoMaterial( (new TipoMaterial(   (String) objects[9]) )   );
				
				if( StringUtils.notEmpty((String) objects[10]) )
					mTemp.adicionaFormasDocumento(    new FormaDocumento( (String) objects[10] )    );
				
				materiais.add(mTemp);
			}
			
		}
		
		return materiais;
	}
	
	
	
	/**
	 * <p>   Encontra os materiais que estão marcados para a geração cuja etiqueta já foi impressa </p>
	 * 
	 * @param idsExemplares os exemplares que estão
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<MateriaisMarcadosParaGerarEtiquetas> findMateriaisPendentesGerouEtiqueta(List<Integer> idsMateriais ) throws DAOException {
		
		String hql = " SELECT m FROM MateriaisMarcadosParaGerarEtiquetas m "
			+" WHERE m.idExemplar in ( :idExemplares ) OR m.idFasciculo in ( :idFasciculos )";
		
		
		Query q = getSession().createQuery(hql);
		q.setParameterList("idExemplares", idsMateriais);
		q.setParameterList("idFasciculos", idsMateriais);
		
		@SuppressWarnings("unchecked")
		List<MateriaisMarcadosParaGerarEtiquetas> lista = q.list();
		return lista;		
	}
	
}
