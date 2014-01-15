/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/06/2009
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoEmprestimo;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 *
 *  Dao para os tipo de empr�stimos da biblioteca
 *
 * @author jadson
 * @since 03/06/2009
 * @version 1.0 criacao da classe
 *
 */
public class TipoEmprestimoDao extends GenericSigaaDAO{
	
	
	/**
	 *       Encontra os tipos de empr�stimo ativos
	 *
	 * @param biblioteca
	 * @param tipoUsuario
	 * @return
	 * @throws DAOException
	 */
	public List<TipoEmprestimo> findTipoEmprestimosAtivos() throws DAOException{
		
		Criteria c = getSession().createCriteria( TipoEmprestimo.class );
		c.add( Restrictions.eq( "ativo" , true ) );
		
		@SuppressWarnings("unchecked")
		List<TipoEmprestimo> lista = c.list();
		return lista;
	}
	
	
	
	/**
	 * <p>Encontra os tipos de empr�stimo ativos que possuem Pol�ticas de empr�stimos para ele.</p> 
	 * <p>Existem um tipo de empr�stimo que n�o seque nenhuma pol�tica o usu�rio � quem define, esse empr�stimos � fixo no sistema e n�o pode ser alterado.</p>
	 *
	 * @param apenasInstitucional recupera apenas o tipo de empr�stimo institucional
	 * @param todosMenosInstitucional  recupera todos com pol�tica menos o o empr�stimos institucional
	 * @param apenasInstitucional == false && todosMenosInstitucional == false recupera todos com pol�tica de empr�stimos.
	 * @return
	 * @throws DAOException
	 */
	public List<TipoEmprestimo> findTipoEmprestimosAtivosComPoliticaDeEmprestimo(boolean todosMenosInstitucional, boolean apenasInstitucional) throws DAOException{
		
		if( (apenasInstitucional && todosMenosInstitucional) )
			throw new DAOException("Contradi��o na passagem dos par�metros para o m�todo");
			
		Criteria c = getSession().createCriteria( TipoEmprestimo.class );
		c.setProjection( Projections.projectionList().add(Projections.property("id"))
				.add(Projections.property("descricao"))
				.add(Projections.property("alteravel"))
				.add(Projections.property("institucional"))
				.add(Projections.property("semPoliticaEmprestimo"))
				.add(Projections.property("ativo"))  );
		
		c.add( Restrictions.eq( "semPoliticaEmprestimo" , false ) ); // Esse m�todo s� recupera os tipos de empr�stimo que tem pol�tica de empr�stimos
		
		/* Se ambos forem falso recupera todos os tipos que est�o associados as pol�ticas. */
		
		// Se � para recuperar apenas o institucional 
		if(apenasInstitucional && ! todosMenosInstitucional)
			c.add( Restrictions.eq( "institucional" , true ) );
	
		// Se � para recuperar todos menos o institucional 
		if(! apenasInstitucional && todosMenosInstitucional)
			c.add( Restrictions.eq( "institucional" , false ) );
		
		c.add( Restrictions.eq( "ativo" , true ) );
		
		@SuppressWarnings("unchecked")
		List<TipoEmprestimo> lista = new ArrayList<TipoEmprestimo>(HibernateUtils.parseTo(c.list(), "id, descricao, alteravel, institucional, semPoliticaEmprestimo, ativo", TipoEmprestimo.class));
		return lista;
	}
	
	
	/**
	 * <p>Retorna os tipos de empr�stimo poss�veis para uma biblioteca </p>
	 * 
	 * @param personalizavel  retorna o tipo de empr�stimo personalizado para a biblioteca pode realiz�-lo (Deve ser passado verdadeiro somente se o usu�rio tiver permiss�o para tal)
	 * @return
	 * @throws DAOException
	 */
	public List<TipoEmprestimo> findTiposEmprestimoParaBiblioteca(boolean personalizavel) throws DAOException {

		String projecao = " tipoEmprestimo.id, tipoEmprestimo.descricao, tipoEmprestimo.semPoliticaEmprestimo, tipoEmprestimo.institucional ";
		
		String hql = 
		" SELECT "+projecao+
		" FROM TipoEmprestimo tipoEmprestimo "+
		" WHERE tipoEmprestimo.ativo = trueValue() AND tipoEmprestimo.institucional = trueValue() ";
		
		// Tras al�m dos resultados o tipo de empr�stimo personalizavel
		if(personalizavel)
			hql+= " OR tipoEmprestimo.semPoliticaEmprestimo = :personalizavel ";
		
		Query q = getSession().createQuery(hql);
		q.setBoolean("personalizavel", personalizavel);
		
		@SuppressWarnings("unchecked")
		List<Object[]> linhas = q.list();
		
		return new ArrayList<TipoEmprestimo>( HibernateUtils.parseTo(linhas, projecao, TipoEmprestimo.class, "tipoEmprestimo"));
		
	}
	
	

	/**
	 * <p>Retorna os tipos de empr�stimo poss�veis dados o tipo de usu�rio o status do material.</p>
	 * <p><i> Basicamente encontra os tipos de emprestimos que possuem pol�ticas de empr�stimo cadastradas para a biblioteca, o v�nculo do usu�rio o status do material e o tipo de material.</i></p>
	 * 
	 * @param tipoUsuario
	 * @param idStatusMaterial
	 * @param idBiblioteca
	 * @param personalizaveis
	 * @return
	 * @throws DAOException
	 */
	public Collection<TipoEmprestimo> findTiposEmprestimoByInformacoesPoliticas(int idBiblioteca, VinculoUsuarioBiblioteca vinculo, int idStatusMaterial, int idTipoMaterial, boolean buscarTambemPersonalizavel, boolean buscarInstitucional) throws DAOException {

		String projecao = " politica.tipoEmprestimo.id, politica.tipoEmprestimo.descricao, politica.tipoEmprestimo.semPoliticaEmprestimo, politica.tipoEmprestimo.institucional ";
		
		String restricaoPolitica = 
				" AND politica.biblioteca.id = :idBiblioteca "+
				" AND politica.vinculo = "+vinculo+" "+
				" AND politica.quantidadeMateriais > 0  " +
				" AND politica.tipoEmprestimo.ativo = :true "+
				" AND ( ( status.ativo = :true AND status.id  IN ( :idStatusMaterial ) ) OR status.id IS NULL ) "+
				" AND ( ( tiposMateriais.ativo = :true AND tiposMateriais.id  IN ( :idTipoMaterial ) ) OR  tiposMateriais.id IS NULL )  ";
		
		String hql =  " SELECT DISTINCT "+projecao+
					" FROM PoliticaEmprestimo politica "+
					" LEFT JOIN politica.statusMateriais status "+
					" LEFT JOIN politica.tiposMateriais tiposMateriais "+
					" WHERE politica.ativo = :true "+
					restricaoPolitica;
		
		
		// Tras al�m dos resultados o tipo de empr�stimo personalizavel
		if(buscarTambemPersonalizavel)
			hql+= " OR ( politica.tipoEmprestimo.semPoliticaEmprestimo = :true )"; // n�o tem biblioteca, nem v�nculo, nem nada
		
		// Tras o tipo de empr�stimos institucional para os par�metros passados.
		if(buscarInstitucional){
			hql+= " AND politica.tipoEmprestimo.institucional = :true ";
		}
		
		/* Se o sistema est� configurado par usar pol�ticas diferentes por biblioteca, 
		 * recupera os tipos de empr�stimos da biblioteca passada 
		 * 
		 * Sen�o sempre recupera na biblioteca central
		 */
		boolean permitePoliticasDiferentesPorBiblioteca = ParametroHelper.getInstance()
				.getParametroBoolean(ParametrosBiblioteca.SISTEMA_PERMITE_CONFIGURAR_POLITICAS_DIFERENTES_POR_BIBLIOTECA);
		
		Integer idBibliotecaUsada = -1;
		
		if( permitePoliticasDiferentesPorBiblioteca ){
			idBibliotecaUsada  = idBiblioteca; 
		}else{
			idBibliotecaUsada = ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.BIBLIOTECA_CENTRAL); 
		}
		
		Query q = getSession().createQuery(hql);
		
		q.setInteger("idBiblioteca", idBibliotecaUsada);
		q.setInteger("idStatusMaterial", idStatusMaterial);
		q.setInteger("idTipoMaterial", idTipoMaterial);
		q.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List <Object[]> linhas = q.list();
		
		List <TipoEmprestimo> tiposEmprestimo = new ArrayList <TipoEmprestimo> ();
		
		for (Object [] o : linhas){
			TipoEmprestimo t = new TipoEmprestimo();
			t.setId((Integer)o[0]);
			t.setDescricao((String)o[1]);
			t.setSemPoliticaEmprestimo((Boolean)o[2]);
			t.setInstitucional((Boolean)o[3]);
			tiposEmprestimo.add(t);
		}
			
		return tiposEmprestimo;
	}
}
