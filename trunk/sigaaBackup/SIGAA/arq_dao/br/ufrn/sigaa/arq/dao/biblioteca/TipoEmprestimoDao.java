/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 *  Dao para os tipo de empréstimos da biblioteca
 *
 * @author jadson
 * @since 03/06/2009
 * @version 1.0 criacao da classe
 *
 */
public class TipoEmprestimoDao extends GenericSigaaDAO{
	
	
	/**
	 *       Encontra os tipos de empréstimo ativos
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
	 * <p>Encontra os tipos de empréstimo ativos que possuem Políticas de empréstimos para ele.</p> 
	 * <p>Existem um tipo de empréstimo que não seque nenhuma política o usuário é quem define, esse empréstimos é fixo no sistema e não pode ser alterado.</p>
	 *
	 * @param apenasInstitucional recupera apenas o tipo de empréstimo institucional
	 * @param todosMenosInstitucional  recupera todos com política menos o o empréstimos institucional
	 * @param apenasInstitucional == false && todosMenosInstitucional == false recupera todos com política de empréstimos.
	 * @return
	 * @throws DAOException
	 */
	public List<TipoEmprestimo> findTipoEmprestimosAtivosComPoliticaDeEmprestimo(boolean todosMenosInstitucional, boolean apenasInstitucional) throws DAOException{
		
		if( (apenasInstitucional && todosMenosInstitucional) )
			throw new DAOException("Contradição na passagem dos parâmetros para o método");
			
		Criteria c = getSession().createCriteria( TipoEmprestimo.class );
		c.setProjection( Projections.projectionList().add(Projections.property("id"))
				.add(Projections.property("descricao"))
				.add(Projections.property("alteravel"))
				.add(Projections.property("institucional"))
				.add(Projections.property("semPoliticaEmprestimo"))
				.add(Projections.property("ativo"))  );
		
		c.add( Restrictions.eq( "semPoliticaEmprestimo" , false ) ); // Esse método só recupera os tipos de empréstimo que tem política de empréstimos
		
		/* Se ambos forem falso recupera todos os tipos que estão associados as políticas. */
		
		// Se é para recuperar apenas o institucional 
		if(apenasInstitucional && ! todosMenosInstitucional)
			c.add( Restrictions.eq( "institucional" , true ) );
	
		// Se é para recuperar todos menos o institucional 
		if(! apenasInstitucional && todosMenosInstitucional)
			c.add( Restrictions.eq( "institucional" , false ) );
		
		c.add( Restrictions.eq( "ativo" , true ) );
		
		@SuppressWarnings("unchecked")
		List<TipoEmprestimo> lista = new ArrayList<TipoEmprestimo>(HibernateUtils.parseTo(c.list(), "id, descricao, alteravel, institucional, semPoliticaEmprestimo, ativo", TipoEmprestimo.class));
		return lista;
	}
	
	
	/**
	 * <p>Retorna os tipos de empréstimo possíveis para uma biblioteca </p>
	 * 
	 * @param personalizavel  retorna o tipo de empréstimo personalizado para a biblioteca pode realizá-lo (Deve ser passado verdadeiro somente se o usuário tiver permissão para tal)
	 * @return
	 * @throws DAOException
	 */
	public List<TipoEmprestimo> findTiposEmprestimoParaBiblioteca(boolean personalizavel) throws DAOException {

		String projecao = " tipoEmprestimo.id, tipoEmprestimo.descricao, tipoEmprestimo.semPoliticaEmprestimo, tipoEmprestimo.institucional ";
		
		String hql = 
		" SELECT "+projecao+
		" FROM TipoEmprestimo tipoEmprestimo "+
		" WHERE tipoEmprestimo.ativo = trueValue() AND tipoEmprestimo.institucional = trueValue() ";
		
		// Tras além dos resultados o tipo de empréstimo personalizavel
		if(personalizavel)
			hql+= " OR tipoEmprestimo.semPoliticaEmprestimo = :personalizavel ";
		
		Query q = getSession().createQuery(hql);
		q.setBoolean("personalizavel", personalizavel);
		
		@SuppressWarnings("unchecked")
		List<Object[]> linhas = q.list();
		
		return new ArrayList<TipoEmprestimo>( HibernateUtils.parseTo(linhas, projecao, TipoEmprestimo.class, "tipoEmprestimo"));
		
	}
	
	

	/**
	 * <p>Retorna os tipos de empréstimo possíveis dados o tipo de usuário o status do material.</p>
	 * <p><i> Basicamente encontra os tipos de emprestimos que possuem políticas de empréstimo cadastradas para a biblioteca, o vínculo do usuário o status do material e o tipo de material.</i></p>
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
		
		
		// Tras além dos resultados o tipo de empréstimo personalizavel
		if(buscarTambemPersonalizavel)
			hql+= " OR ( politica.tipoEmprestimo.semPoliticaEmprestimo = :true )"; // não tem biblioteca, nem vínculo, nem nada
		
		// Tras o tipo de empréstimos institucional para os parâmetros passados.
		if(buscarInstitucional){
			hql+= " AND politica.tipoEmprestimo.institucional = :true ";
		}
		
		/* Se o sistema está configurado par usar políticas diferentes por biblioteca, 
		 * recupera os tipos de empréstimos da biblioteca passada 
		 * 
		 * Senão sempre recupera na biblioteca central
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
