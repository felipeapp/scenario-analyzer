/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 */

package br.ufrn.sigaa.biblioteca.processos_tecnicos.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
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
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormaDocumento;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;

/**
 * DAO para realizar consultas do caso-de-uso 'Transferir Materiais entre Setores'.
 * 
 * @author Felipe Rivas
 */
public class TransferirMateriaisEntreSetoresDAO extends GenericSigaaDAO {

	
	/**
	 * A projeção padrão para consulta para impressão de etiquetas
	 */
	private static final String PROJECAO_PADRAO = " m.id, m.codigoBarras, m.numeroChamada, m.segundaLocalizacao, m.biblioteca.identificador, m.biblioteca.descricao, m.colecao.descricao, m.situacao.descricao, m.status.descricao, m.tipoMaterial.descricao, forma.denominacao ";
	
	/**
	 * A consulta padrão para a impressão de etiquetas, geralmente só muda a cláusula "WHERE"
	 */
	private static final String CONSULTA_PADRAO = "SELECT "+PROJECAO_PADRAO+" FROM MaterialInformacional m LEFT JOIN m.formasDocumento forma ";
	
	
	/**
	 * Retorna o material correspondente ao código de barras, se aquele existir. Preenche o material com os atributos necessários
	 * ao caso-de-uso.
	 * 
	 * @param codigoBarras Código de barras cujo material se desejar recuperar.
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public MaterialInformacional getMaterialByCodigoBarras(String codigoBarras) throws HibernateException, DAOException {
		String hql = 
			"SELECT \n" +
			"\t e.id, \n" +
			"\t e.codigoBarras, \n" +
			"\t e.biblioteca, \n" +
			"\t s.id, \n" +
			"\t s.descricao, \n" +
			"\t s.situacaoDisponivel, \n" +
			"\t s.situacaoEmprestado, \n" +
			"\t s.situacaoDeBaixa \n" +
			"FROM \n" +
			"\t Exemplar e \n" +
			"\t INNER JOIN e.situacao s \n" +
			"WHERE e.codigoBarras = :codigoBarras ";
		
		Query q = getSession().createQuery(hql);
		
		q.setString("codigoBarras", codigoBarras);
		
		Object[] result = (Object[]) q.uniqueResult();
		
		MaterialInformacional material = null;
		
		if (result == null) {
			hql = 
				"SELECT \n" +
				"\t f.id, \n" +
				"\t f.codigoBarras, \n" +
				"\t f.biblioteca, \n" +
				"\t s.id, \n" +
				"\t s.descricao, \n" +
				"\t s.situacaoDisponivel, \n" +
				"\t s.situacaoEmprestado, \n" +
				"\t s.situacaoDeBaixa \n" +
				"FROM \n" +
				"\t Fasciculo f \n" +
				"\t INNER JOIN f.situacao s \n" +
				"WHERE f.codigoBarras = :codigoBarras ";
			
			q = getSession().createQuery(hql);
			
			q.setString("codigoBarras", codigoBarras);
			
			result = (Object[]) q.uniqueResult();

			if (result != null) {
				material = montarMaterial(result, false);
			}
		}
		else {
			material = montarMaterial(result, true);
		}
		
		return material;
	}
	
	/**
	 * A partir de um array de objetos contendo os atributos de um material, monta o objeto material.
	 * 
	 * @param result Array de objetos com os atributos relevantes para o caso-de-uso
	 * @param isExemplar Indica se é exemplar ou fascículo
	 * @return
	 */
	private MaterialInformacional montarMaterial(Object[] result, boolean isExemplar) {
		MaterialInformacional material = isExemplar ? new Exemplar() : new Fasciculo();
		SituacaoMaterialInformacional situacao = new SituacaoMaterialInformacional();
		
		int cont = 0;
		
		material.setId((Integer) result[cont++]);
		material.setCodigoBarras((String) result[cont++]);
		material.setBiblioteca((Biblioteca) result[cont++]);

		situacao.setId((Integer) result[cont++]);
		situacao.setDescricao((String) result[cont++]);
		situacao.setSituacaoDisponivel((Boolean) result[cont++]);
		situacao.setSituacaoEmprestado((Boolean) result[cont++]);
		situacao.setSituacaoDeBaixa((Boolean) result[cont++]);
		
		material.setSituacao(situacao);
		
		return material;
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

}
