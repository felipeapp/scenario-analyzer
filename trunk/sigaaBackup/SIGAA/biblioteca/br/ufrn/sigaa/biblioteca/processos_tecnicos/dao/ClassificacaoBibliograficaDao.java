/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 14/02/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CamposMarcClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica.OrdemClassificacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.RelacionaClassificacaoBibliograficaAreaCNPq;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.RelacionaClassificacaoBibliograficaBiblioteca;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

/**
*
* DAO responsável pelas consultas relacionadas às classificações bibliográficas do sistema.
*
* @author Felipe Rivas
* @since 09/02/2012
* @version 1.0 criacao da classe
*
*/
public class ClassificacaoBibliograficaDao extends GenericSigaaDAO {

	/** Constante utilizada para indicar que a consulta sobre classificaçaõ é ascendente*/
	public static final String ASC = "ASC";
	
	/** Constante utilizada para indicar que a consulta sobre classificaçaõ é descendente */
	private static final String DESC = "DESC";
	
	
	/**
	 * Busca as classificações bibliográficas ativas do sistema.
	 * 
	 * @param ordenacao
	 * @param sentidoOrdenacao
	 * @param projecao
	 * @return
	 * @throws DAOException
	 */
	public List<ClassificacaoBibliografica> findAllAtivos(String ordenacao, String sentidoOrdenacao, String... projecao) throws DAOException {
		
		Criteria c = getSession().createCriteria(ClassificacaoBibliografica.class);
		ProjectionList projecoes = Projections.projectionList();
		
		StringBuilder projecaoTexto = new StringBuilder();
		
		if (projecao ==  null || projecao.length == 0) {
			projecao = new String[]{"id", "descricao"};
		}
		
		for (String string : projecao) {
			projecoes.add(Projections.property(string));
			projecaoTexto.append(string+", ");
		}
		
		projecaoTexto.delete(projecaoTexto.length()-2, projecaoTexto.length()-1); // apaga a virgula e espaço no final
		
		c.add(Restrictions.eq("ativa", true));
		c.setProjection(projecoes);
		
		if (!StringUtils.isNotEmpty(sentidoOrdenacao) && DESC.equals(sentidoOrdenacao.toUpperCase())) {
			c.addOrder(Order.desc(ordenacao));
		} else {
			c.addOrder(Order.asc(ordenacao));
		}
		
		@SuppressWarnings("unchecked")
		List<ClassificacaoBibliografica> lista = new ArrayList<ClassificacaoBibliografica>(HibernateUtils.parseTo( c.list(), projecaoTexto.toString(), ClassificacaoBibliografica.class));
		
		return lista;
	}

	
	/**
	 * Retorna todas as classificações ativas sem fazer nenum tipo de projeção. 
	 * <strong>Deve ser utilizado apenas no CRUD das classificações bibliográficas !!! </strong>
	 *
	 * @return
	 * @throws DAOException
	 */
	public List<ClassificacaoBibliografica> findAllAtivosSemProjecao() throws DAOException {
		return findAllAtivosSemProjecao("ordem", ASC);
	}

	
	/**
	 * Retorna todas as classificações ativas sem fazer nenum tipo de projeção, especificando a ordem de recuperação da informação. 
	 *  <strong>Deve ser utilizado apenas no CRUD das classificações bibliográficas !!! </strong>
	 *
	 * @return
	 * @throws DAOException
	 */
	public List<ClassificacaoBibliografica> findAllAtivosSemProjecao(String ordenacao, String sentidoOrdenacao) throws DAOException {
		StringBuilder hql = new StringBuilder("" +
				"SELECT " +
					"cb.id, " +
					"cb.descricao, " +
					"cb.ordem, " +
					"cb.campoMARC, " +
					"cb.ativa, " +
					"cp " +
				"FROM ClassificacaoBibliografica cb " +
				"INNER JOIN cb.classesPrincipaisClassificacaoBibliografica cp " + 
				"WHERE cb.ativa = trueValue() ");
		
		if (!StringUtils.isNotEmpty(sentidoOrdenacao) && DESC.equals(sentidoOrdenacao.toUpperCase())) {
			hql.append("ORDER BY " + ordenacao + " DESC ");
		} else {
			hql.append("ORDER BY " + ordenacao + " ASC ");
		}
		
		Query q = getSession().createQuery(hql.toString());
		
		@SuppressWarnings("unchecked")
		List<Object[]> listaObjArray = q.list();
		
		List<ClassificacaoBibliografica> lista = new ArrayList<ClassificacaoBibliografica>();
		
		for (Object[] obj : listaObjArray) {
			ClassificacaoBibliografica c = new ClassificacaoBibliografica((Integer) obj[0]);
			
			if (lista.contains(c)) {
				c = lista.get(lista.indexOf(c));
				
				c.getClassesPrincipaisClassificacaoBibliografica().add((String) obj[5]);
			} else {
				c.setDescricao((String) obj[1]);
				c.setOrdem((ClassificacaoBibliografica.OrdemClassificacao) obj[2]);
				c.setCampoMARC((CamposMarcClassificacaoBibliografica) obj[3]);
				c.setAtiva((Boolean) obj[4]);
				c.getClassesPrincipaisClassificacaoBibliografica().add((String) obj[5]);
				
				lista.add(c);
			}
		}
		
		return lista;
	}
	
	
	
	
	/**
	 * Encontra a classificação bibliográfica correspondente ao campo passado
	 * 
	 * @return
	 * @throws DAOException
	 */
	public ClassificacaoBibliografica findClassificacaoByCampoMARC(CamposMarcClassificacaoBibliografica campoMARC) throws DAOException {
		
		String projecao = " classificacao.id_classificacao_bibliografica, classificacao.descricao, classificacao.campo, classificacao.ordem, classesPrincipais.classe_principal ";
		
		String sql  = " SELECT "+projecao+" FROM biblioteca.classificacao_bibliografica classificacao " 
		+" LEFT JOIN biblioteca.classe_principal_classificacao_bibliografica classesPrincipais ON classesPrincipais.id_classificacao_bibliografica = classificacao.id_classificacao_bibliografica "	
		+" WHERE classificacao.ativa = :true AND classificacao.campo =  "+campoMARC;
		
		Query q = getSession().createSQLQuery(sql); // Sql pro causa da collection os elements, não descobir como fazer projeção com HQL
		q.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List<Object[]> objetos = q.list();
		
		
		
		ClassificacaoBibliografica classificacao = new ClassificacaoBibliografica();
		
		int contador  = 1;
		
		for (Object[] dados : objetos) { // retorna um objeto por cada classe principal cadastrada
			
			if(contador == 1){
				classificacao.setId((Integer) dados[0] );
				classificacao.setDescricao( (String) dados[1] );
				classificacao.setCampoMARC( CamposMarcClassificacaoBibliografica.getCampoMarcClassificaoByValor( ((Short) dados[2]).intValue() ) );
				classificacao.setOrdem( OrdemClassificacao.getOrdem(  ((Short) dados[3]).intValue()  )  );
			}
			
			classificacao.adicionaClassesPrincipais((String) dados[4]);
			
			contador++;
		}
		
		
		return classificacao;
	}
	
	
	/**
	 * Retorna a lista de classificações utilizada no momento da validação.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<ClassificacaoBibliografica>  findAllClassificacoesParaValidacao() throws DAOException {
		
		String projecao = " classificacao.id_classificacao_bibliografica, classificacao.descricao, classificacao.campo, classificacao.ordem, classesPrincipais.classe_principal ";
		
		String sql  = " SELECT "+projecao+" FROM biblioteca.classificacao_bibliografica classificacao " 
		+" LEFT JOIN biblioteca.classe_principal_classificacao_bibliografica classesPrincipais ON classesPrincipais.id_classificacao_bibliografica = classificacao.id_classificacao_bibliografica "	
		+" WHERE classificacao.ativa = :true  ";
		
		Query q = getSession().createSQLQuery(sql); // Sql pro causa da collection os elements, não descobir como fazer projeção com HQL
		q.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List<Object[]> objetos = q.list();
		
		List<ClassificacaoBibliografica> classificacoes = new ArrayList<ClassificacaoBibliografica>();
		
		for (Object[] dados : objetos) { // retorna um objeto por cada classe principal cadastrada
			ClassificacaoBibliografica classificacao = new ClassificacaoBibliografica();
			classificacao.setId((Integer) dados[0] );
			classificacao.setDescricao( (String) dados[1] );
			classificacao.setCampoMARC( CamposMarcClassificacaoBibliografica.getCampoMarcClassificaoByValor(  ( (Short) dados[2]).intValue() ) );
			classificacao.setOrdem( OrdemClassificacao.getOrdem(   ( (Short) dados[3]).intValue() )  );
			
			if(classificacoes.contains(classificacao)){
				classificacao = classificacoes.get(classificacoes.indexOf(classificacao));
			}else{
				classificacoes.add(classificacao);
			}
			
			classificacao.adicionaClassesPrincipais((String) dados[4]);
		}
		
		
		return classificacoes;
	}
	
	
	/**
	 * Busca as informações de todos os relacionamento entre as classificações bibliográficas e as bibliotecas do sistema
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<RelacionaClassificacaoBibliograficaBiblioteca> findAllRelacionamentosClassificacaoBiblioteca() throws DAOException {
		
		String projecao = " relacionamento.id, relacionamento.biblioteca.id, relacionamento.biblioteca.descricao, relacionamento.classificacao.id, relacionamento.classificacao.descricao, relacionamento.classificacao.ordem ";
		
		String hql  = " SELECT "+projecao+" FROM RelacionaClassificacaoBibliograficaBiblioteca relacionamento ORDER BY relacionamento.biblioteca.descricao ";
		
		Query q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		List<RelacionaClassificacaoBibliograficaBiblioteca> lista = new ArrayList<RelacionaClassificacaoBibliograficaBiblioteca>(
			HibernateUtils.parseTo(q.list(), projecao, RelacionaClassificacaoBibliograficaBiblioteca.class, "relacionamento") );
		
		return lista;
	}
	
	/**
	 * Busca as informações de todos os relacionamento entre as classificações bibliográficas e as áreas CNPq configuradas no sistema.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<RelacionaClassificacaoBibliograficaAreaCNPq> findAllRelacionamentosClassificacaoAreasCNPqByClassificacao(int idClassificacao) throws DAOException {
		
		String projecao = " relacionamento.id_relaciona_classificacao_bibliografica_area_cnpq, relacionamento.classes_inclusao, relacionamento.classes_exclusao, " 
			+" area.id_area_conhecimento_cnpq, COALESCE( informacoes.sigla, area.sigla ) as sigla, COALESCE( informacoes.nome, area.nome ) as nome, " 
			+" classificacao.id_classificacao_bibliografica, classificacao.descricao ";
		
		String sql  = 
			 " SELECT DISTINCT "+projecao
			+" FROM biblioteca.relaciona_classificacao_bibliografica_area_cnpq relacionamento "
			+" INNER JOIN comum.area_conhecimento_cnpq area ON area.id_area_conhecimento_cnpq = relacionamento.id_area_conhecimento_cnpq "
			+" INNER JOIN biblioteca.classificacao_bibliografica classificacao ON classificacao.id_classificacao_bibliografica = relacionamento.id_classificacao_bibliografica "
			+" LEFT JOIN biblioteca.informacoes_area_cnpq_biblioteca informacoes ON informacoes.id_area_conhecimento_cnpq = area.id_area_conhecimento_cnpq "
			+" WHERE classificacao.id_classificacao_bibliografica = :idClassificacao " 
			+" ORDER BY nome ";
		
		Query q = getSession().createSQLQuery(sql);   // Sql para fazer o inner join com "InformacoesAreaCNPQBiblioteca"
		q.setInteger("idClassificacao", idClassificacao);
		
		@SuppressWarnings("unchecked")
		List<Object[]> objects = q.list(); // não é possível utilizar o HibernateUtils.parseTo   porque não relacionamento direto com a tabela "informacoes_area_cnpq_biblioteca"
		
		List<RelacionaClassificacaoBibliograficaAreaCNPq> lista = new ArrayList<RelacionaClassificacaoBibliograficaAreaCNPq>();
		
		for (Object[] objeto : objects) {
			RelacionaClassificacaoBibliograficaAreaCNPq temp = new RelacionaClassificacaoBibliograficaAreaCNPq();
			temp.setId( (Integer) objeto[0]);
			temp.setClassesInclusao( (String) objeto[1]);
			temp.setClassesExclusao( (String) objeto[2]);
			temp.setArea( new AreaConhecimentoCnpq((Integer) objeto[3], (String) objeto[4], (String) objeto[5]));
			temp.setClassificacao( new ClassificacaoBibliografica((Integer) objeto[6], (String) objeto[7]) );
			lista.add(temp);
		}
		
		return lista;
	}
	
	
	
	/**
	 * Retorna apenas as informações necessárias encontrar a área cnpq a partir da classificação.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<RelacionaClassificacaoBibliograficaAreaCNPq> findInformacoesRelacionamentosAreasCNPqByClassificacaoParaValidacao(int idClassificacao) throws DAOException {
		
		String projecao = " area.id_area_conhecimento_cnpq , relacionamento.classes_inclusao, relacionamento.classes_exclusao ";
		
		String sql  = 
			 " SELECT DISTINCT "+projecao
			+" FROM biblioteca.relaciona_classificacao_bibliografica_area_cnpq relacionamento "
			+" INNER JOIN comum.area_conhecimento_cnpq area ON area.id_area_conhecimento_cnpq = relacionamento.id_area_conhecimento_cnpq "
			+" INNER JOIN biblioteca.classificacao_bibliografica classificacao ON classificacao.id_classificacao_bibliografica = relacionamento.id_classificacao_bibliografica "
			+" WHERE classificacao.id_classificacao_bibliografica = :idClassificacao " ;
		
		Query q = getSession().createSQLQuery(sql);   // Sql para fazer o inner join com "InformacoesAreaCNPQBiblioteca"
		q.setInteger("idClassificacao", idClassificacao);
		
		@SuppressWarnings("unchecked")
		List<Object[]> objects = q.list(); // não é possível utilizar o HibernateUtils.parseTo   porque não relacionamento direto com a tabela "informacoes_area_cnpq_biblioteca"
		
		List<RelacionaClassificacaoBibliograficaAreaCNPq> lista = new ArrayList<RelacionaClassificacaoBibliograficaAreaCNPq>();
		
		for (Object[] objeto : objects) {
			RelacionaClassificacaoBibliograficaAreaCNPq temp = new RelacionaClassificacaoBibliograficaAreaCNPq();
			//temp.setId( (Integer) objeto[0]);
			temp.setArea( new AreaConhecimentoCnpq((Integer) objeto[0] ) );
			temp.setClassesInclusao( (String) objeto[1]);
			temp.setClassesExclusao( (String) objeto[2]);
			//temp.setClassificacao( new ClassificacaoBibliografica((Integer) objeto[6], (String) objeto[7]) );
			lista.add(temp);
		}
		
		return lista;
	}
	
	
	/**
	 * <p>
	 * Verifica no banco se existe cadastrado no sistema a classificação 1 e se ela está ativa
	 * , significa que o sistema está hatilitado para utilizar.
	 * </p>
	 * 
	 * @param ordenacao
	 * @param sentidoOrdenacao
	 * @param projecao
	 * @return
	 * @throws DAOException
	 */
	public boolean isSistemaTrabalhaClassificacao(OrdemClassificacao ordem) throws DAOException {
		
		String hql  = 
			 " SELECT COUNT(DISTINCT classificacao.id )"
			+" FROM ClassificacaoBibliografica classificacao " 
			+" WHERE classificacao.ativa = :true AND classificacao.ordem = "+ordem; // Só pode existir 1 ativa por ordem
		
		Query q = getSession().createQuery(hql);
		q.setBoolean("true", true);
		
		Long quantidade = (Long) q.uniqueResult();
		
		if(quantidade > 0)
			return true;
		else
			return false;
	}
	
	
	/**
	 * <p>
	 * Verifica no banco se existe cadastrado no sistema a classificação 1 e se ela está ativa
	 * , significa que o sistema está hatilitado para utilizar.
	 * </p>
	 * 
	 * @param ordenacao
	 * @param sentidoOrdenacao
	 * @param projecao
	 * @return
	 * @throws DAOException
	 */
	public String getDescricaoClassificacao(OrdemClassificacao ordem) throws DAOException {
		
		String hql  = 
			 " SELECT descricao "
			+" FROM ClassificacaoBibliografica classificacao " 
			+" WHERE classificacao.ativa = :true AND classificacao.ordem = "+ordem; // Só pode existir 1 ativa por ordem
		
		Query q = getSession().createQuery(hql);
		q.setBoolean("true", true);
		
		return (String) q.uniqueResult();
	}

	
	/**
	 * Retorna as classes principais de uma determinada classificação bibliográfica.
	 *
	 * @param primeraClassificacao
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public List<String> getClassesPrincipaisClassificacao(OrdemClassificacao ordem) throws DAOException {
		String sql  = 
			 " SELECT classesPrincipais.classe_principal "
			+" FROM biblioteca.classificacao_bibliografica classificacao " 
			+" LEFT JOIN biblioteca.classe_principal_classificacao_bibliografica classesPrincipais ON classesPrincipais.id_classificacao_bibliografica = classificacao.id_classificacao_bibliografica "
			+" WHERE classificacao.ativa = :true AND classificacao.ordem = "+ordem; // Só pode existir 1 ativa por ordem
		
		Query q = getSession().createSQLQuery(sql);
		q.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List<String> classeesPrincipais = q.list();
		
		return classeesPrincipais;
	}
	
	
	
	/**
	 * Encontra a classificação bibliográfica utilizada na biblioteca passada
	 * 
	 * @return
	 * @throws DAOException
	 */
	public ClassificacaoBibliografica findClassificacaoUtilizadaPelaBiblioteca(int  idBiblioteca) throws DAOException {
		
		String projecao = " classificacao.id_classificacao_bibliografica, classificacao.descricao, classificacao.campo, classificacao.ordem, classesPrincipais.classe_principal ";
		
		String sql  = " SELECT "+projecao+" FROM biblioteca.classificacao_bibliografica classificacao " 
		+" LEFT JOIN biblioteca.classe_principal_classificacao_bibliografica classesPrincipais ON classesPrincipais.id_classificacao_bibliografica = classificacao.id_classificacao_bibliografica "	
		+" LEFT JOIN biblioteca.relaciona_classificacao_bibliografica_biblioteca relacionamento ON relacionamento.id_classificacao_bibliografica = classificacao.id_classificacao_bibliografica "
		+" WHERE classificacao.ativa = :true AND relacionamento.id_biblioteca = :idBiblioteca";
		
		Query q = getSession().createSQLQuery(sql); // Sql por causa da "collection os elements", não descobir como fazer projeção com HQL
		q.setInteger("idBiblioteca", idBiblioteca);
		q.setBoolean("true", true);
		
		
		@SuppressWarnings("unchecked")
		List<Object[]> objetos = q.list();
		
		if(objetos == null || objetos.size() == 0 ) // não existe classificação configurada para a biblioteca.
			return null;
		
		ClassificacaoBibliografica classificacao = new ClassificacaoBibliografica();
		
		int contador  = 1;
		
		for (Object[] dados : objetos) { // retorna um objeto por cada classe principal cadastrada
			
			if(contador == 1){
				classificacao.setId((Integer) dados[0] );
				classificacao.setDescricao( (String) dados[1] );
				classificacao.setCampoMARC( CamposMarcClassificacaoBibliografica.getCampoMarcClassificaoByValor( ((Short) dados[2]).intValue() ) );
				classificacao.setOrdem( OrdemClassificacao.getOrdem(  ((Short) dados[3]).intValue()  )  );
			}
			
			classificacao.adicionaClassesPrincipais((String) dados[4]);
			
			contador++;
		}
		
		
		return classificacao;
	}

	
	
	/**
	 *   Verifica se os dados classificação bibliográfica do Título estão preenchidos
	 */
	public boolean isDadosClassificacaoPreenchidos(Integer idTitulo, OrdemClassificacao ordem) throws DAOException {
		
		String projection = "";
		
		if(ordem == OrdemClassificacao.PRIMERA_CLASSIFICACAO){
			projection = "t.classificacao1, t.classePrincipalClassificacao1";
		}
		
		if(ordem == OrdemClassificacao.SEGUNDA_CLASSIFICACAO){
			projection = "t.classificacao2, t.classePrincipalClassificacao2";
		}
		
		if(ordem == OrdemClassificacao.TERCEIRA_CLASSIFICACAO){
			projection = "t.classificacao3, t.classePrincipalClassificacao3";
		}
		
		String hql = " SELECT " +projection+
				" FROM TituloCatalografico t"+
				" WHERE t.id  = :idTitulo ";
		
		Query q = getSession().createQuery(hql);
		
		q.setInteger("idTitulo", idTitulo);
		
		Object[] dados = (Object[]) q.uniqueResult();
		
		if(StringUtils.isEmpty( (String) dados[0]) || StringUtils.isEmpty( (String) dados[1]) )
			return false;
		else
			return true;
		
	}	
	
	
	
}
