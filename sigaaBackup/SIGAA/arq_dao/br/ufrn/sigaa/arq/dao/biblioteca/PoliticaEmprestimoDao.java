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
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PoliticaEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.StatusMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 *
 * DAO para as políticas de empréstimo da biblioteca.
 *
 * @author jadson
 * @since 03/06/2009
 * @version 1.0 criacao da classe
 * @version 1.5 - jadson em 20/02/2012 - novos métodos de consulta par suportar a nova modelagem da política.
 * 
 */
public class PoliticaEmprestimoDao extends GenericSigaaDAO{

	
	
	/** Projeção padrão para recuperanção dos dados de uma política */
	final static String PROJECAO_PADRAO = " p.id, p.biblioteca.id, p.biblioteca.descricao, p.vinculo, " +
			" p.tipoEmprestimo.id, p.tipoEmprestimo.descricao, p.tipoEmprestimo.institucional, p.tipoEmprestimo.semPoliticaEmprestimo," + // todas as informações para validar o tipo de empréstimo correto
			" p.quantidadeMateriais, p.prazoEmprestimo, p.tipoPrazo, p.quantidadeRenovacoes, p.alteravel, p.personalizavel, p.ativo, "+
			" status.id, status.descricao, status.permiteEmprestimo," +
			" tiposMateriais.id, tiposMateriais.descricao ";
	
	
	
	
	
	
	
	
	/**
	 *    <p>Encontra a política de empréstimo específica para todos os parâmetros passados. 
	 *    Biblioteca, Tipo do Usuário, Tipo de Empréstimo, Status do Material e Tipo de Material</p>
	 *
	 *    <p> Era para existir somente um política por vez ativa com esses parâmetros. </p>
	 *
	 *    <p>
	 *    <strong>
	 *    ESSE AQUI É O MÉTODO A SER USADO NO MOMENTO DE REALIZAR UM NOVO EMPRÉSTIMO, ELE RETORNA A POLÍTICA ATIVIA 
	 *    A SER USADA DEPENDENDO DAS CARACTERÍSTICAS DO MATERIAL A SER EMPRESTADO !!!! <br/>
	 *    NÃO O ALTERE E MUITO MENOS UTILIZE PARA OUTRA COISA, A NÃO SER QUE VOCÊ REALMENTE SAIBA O QUE ESTÁ FAZENDO ! <br/>
	 *    
	 *    </strong> 
	 *    </p>
	 *
	 * @param biblioteca
	 * @param vinculo
	 * @param tipoEmprestimo
	 * @param status
	 * @param tipoMaterial
	 * 
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public PoliticaEmprestimo findPoliticaEmpretimoAtivaASerUsuadaNoEmprestimo(Biblioteca bibliotecaPassada, 
			VinculoUsuarioBiblioteca vinculo, TipoEmprestimo tipoEmprestimo, StatusMaterialInformacional  status, TipoMaterial tipoMaterial) throws DAOException, NegocioException{
		
		// Condições de entrada //
		if(bibliotecaPassada == null)
			throw new NegocioException("Para ser emprestado o material precisa pertencer a uma biblioteca.");
		if(vinculo == null)
			throw new NegocioException("Para realizar o empréstimos é preciso que o usuário possua um vínculo no sistema.");
		if(tipoEmprestimo == null)
			throw new NegocioException("Para realizar o empréstimos é preciso escolher um tipo de empréstimo.");
		if(status == null)
			throw new NegocioException("Para ser emprestado o material precisa possuir um status.");
		if(tipoMaterial == null)
			throw new NegocioException("Para ser emprestado o material precisa possuir um tipo de materail.");
		
		
		boolean permitePoliticasDiferentesPorBiblioteca = ParametroHelper.getInstance().getParametroBoolean(ParametrosBiblioteca.SISTEMA_PERMITE_CONFIGURAR_POLITICAS_DIFERENTES_POR_BIBLIOTECA);
		
		Integer idBibliotecaPolitica = -1;
		
		/*
		 *   Apenas se o sistema está configurado para trabalhar com o várias políticas, usa o id da biblioteca passada. <br/>
		 *   Se não todas vão usar sempre a política da central, a biblioteca passada não é usada. <br/><br/>
		 *   
		 *   Aqui houve uma alteração na regra de negócio. Antes para todo todo material existia obrigatoriamente um política no banco.<br/>
		 *   Agora pode ser que não exista, porque exemplo: na UFRN não existe uma política para o tipo de empréstimos regular para material especial.<br/><br/>
		 *   
		 *   Nesse caso, se tentar empréstar uma material especial como empréstimos normal, o sistema não vai deixar.<br/><br/>
		 *   
		 *   Então quando se ativa o empréstimo para todos as bibliotecas, cada biblioteca tem que definir suas política não vai usar mais a da central.<br/>
		 *   Por que se não o administrador poderia não definir alguma política e o usuário consegui emprestar pela política da central.<br/>
		 *   
		 */
		if( permitePoliticasDiferentesPorBiblioteca ){ //
			idBibliotecaPolitica  = bibliotecaPassada.getId(); 
		}else{
			idBibliotecaPolitica = ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.BIBLIOTECA_CENTRAL); // 
		}
		
		
		String hql = " SELECT "+PROJECAO_PADRAO
				+" FROM PoliticaEmprestimo p "
				+" LEFT JOIN p.statusMateriais status "
				+" LEFT JOIN p.tiposMateriais tiposMateriais "
				+" WHERE p.ativo = :true ";                                                     // MUITO IMPORTANTE, RECUPERA SEMPRE A ATIVA NO MOMENTO
		
		
		
		if (!tipoEmprestimo.isTipoEmprestimoPersonalizavel()){
			hql += 
				 " AND p.biblioteca.id = :idBibliotecaPolitica "
				+" AND p.tipoEmprestimo.id = :idTipoEmprestimo "
				+" AND p.vinculo = :vinculo ";
				//+" AND ( status.id = :idStatus  OR status.id IS NULL )"                         // PARA A POLÍTICA QUE CONTENHA O STATUS PASSADO, OU QUE SEJA VÁLIDAS PARA TODOS OS STATUS
				//+" AND ( tiposMateriais.id = :idTipoMaterial OR tiposMateriais.id IS NULL )"; // PARA A POLÍTICA QUE CONTENHA O TIPO DE MATERIAL PASSADO, OU QUE SEJA VÁLIDAS PARA TODOS OS TIPOS DE MATERIAIS
		} else{
			hql += " AND p.personalizavel = :true ";                                            // SÓ TEM UMA NO BANCO, ENTÃO NÃO PRECISA BUSCAR PELOS OUTROS DADOS
		}
	
		
		
		Query q = getSession().createQuery( hql );
		if (! tipoEmprestimo.isTipoEmprestimoPersonalizavel() ){
			q.setInteger("idBibliotecaPolitica",  idBibliotecaPolitica);
			q.setInteger("vinculo",  vinculo.getValor());
			q.setInteger("idTipoEmprestimo",  tipoEmprestimo.getId());
			
			//q.setInteger("idStatus",  status.getId());
			//q.setInteger("idTipoMaterial",  tipoMaterial.getId());
		}
		q.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List<Object[]> dados = q.list();
		
		List<PoliticaEmprestimo> politicas = montaDadosPolitica(dados);
		
		
		/*
		 *  Não dá para filtar o status e tipo de material diretamente na consultas sql, senão a consulta 
		 *  só retorna aquele status e tipo de material buscado. Tem que retornar todos da politica de depois
		 *  selecionar aquelas politicas que contém o status.
		 */
		List<PoliticaEmprestimo> politicasRetornadas = new ArrayList<PoliticaEmprestimo>();
		
		
		for (PoliticaEmprestimo politicaEmprestimo : politicas) {
			if(politicaEmprestimo.getStatusMateriais() == null || politicaEmprestimo.getStatusMateriais().contains(status)
					&&  ( politicaEmprestimo.getTiposMateriais() == null || politicaEmprestimo.getTiposMateriais().contains(tipoMaterial) ) )
				politicasRetornadas.add(politicaEmprestimo);
		}
		
		
		// Condições de Saída //
		if(politicasRetornadas.size() <= 0){
			throw new NegocioException("Não existe nenhuma política de empréstimos cadastrada que permita realizar o empréstimos desse material.");
		}else{
			if(politicasRetornadas.size() > 1){
				throw new NegocioException("Erro de inconsistência no cadastro das políticas de empréstimo. Existe mais de uma política cadastrada que pode ser usada com esse material.");
			}else{
				return politicasRetornadas.get(0);
			}
		}
		
	}
	
	
	/**
	 *  <p>Encontra todas as políticas de empréstimos ativas para a biblioteca e vinculo do usuários passados que podem ser alteradas.</p>
	 *
	 *  <p>Geralmente usadas no cadastro da política.</p>
	 *
	 * @param biblioteca
	 * @param tipoUsuario
	 * @return
	 * @throws DAOException
	 */
	public List<PoliticaEmprestimo> findPoliticasEmpretimoAtivasAlteraveisByBibliotecaEVinculo(Biblioteca biblioteca, VinculoUsuarioBiblioteca vinculo) throws DAOException{
		
		
		
		String hql = " SELECT "+PROJECAO_PADRAO+" FROM PoliticaEmprestimo p "
				+" LEFT JOIN p.statusMateriais status "
				+" LEFT JOIN p.tiposMateriais tiposMateriais "
				+" WHERE p.ativo = :true AND p.alteravel = :true "
				+" AND p.biblioteca.id = :idBiblioteca AND p.vinculo = "+vinculo;
		
		Query q = getSession().createQuery( hql );
		q.setInteger("idBiblioteca",  biblioteca.getId());
		q.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List<Object[]> dados = q.list();
		
		List<PoliticaEmprestimo> lista = montaDadosPolitica(dados);
		
		return lista;
	}


	
	
	
	
	/**
	 *  <p>Encontra todas as políticas de empréstimos ativas para a biblioteca e vinculo do usuários passados que podem ser alteradas.</p>
	 *
	 *  <p>Geralmente usadas no cadastro da política.</p>
	 *
	 * @param biblioteca
	 * @param tipoUsuario
	 * @return
	 * @throws DAOException
	 */
	public PoliticaEmprestimo findDadosAlteracaoPoliticaById(int idPoliticaEmprestimo) throws DAOException{
		
		String hql = " SELECT "+PROJECAO_PADRAO+" FROM PoliticaEmprestimo p "
				+" LEFT JOIN p.statusMateriais status "
				+" LEFT JOIN p.tiposMateriais tiposMateriais "
				+" WHERE p.id = :idPoliticaEmprestimo ";
		
		Query q = getSession().createQuery( hql );
		q.setInteger("idPoliticaEmprestimo",  idPoliticaEmprestimo);
		
		
		@SuppressWarnings("unchecked")
		List<Object[]> dados = q.list();
		
		List<PoliticaEmprestimo> lista = montaDadosPolitica(dados);
		
		if(lista.size() > 0)
			return lista.get(0);
		else
			return null;
	}
	
	
	/** Monta as informações das políticas para serem retornadas, geralmente usado no cadastro */
	private List<PoliticaEmprestimo> montaDadosPolitica(List<Object[]> dados) {
		
		List<PoliticaEmprestimo> lista = new ArrayList<PoliticaEmprestimo>();
		
		for (Object[] objects : dados) {
			
			PoliticaEmprestimo p = new PoliticaEmprestimo();
			p.setId( (Integer) objects[0]);
			
			p.setBiblioteca( new Biblioteca((Integer) objects[1], (String) objects[2]));
			p.setVinculo((VinculoUsuarioBiblioteca) objects[3] );
			p.setTipoEmprestimo( new TipoEmprestimo((Integer) objects[4],  (String) objects[5]));
			p.getTipoEmprestimo().setInstitucional( (Boolean) objects[6] );
			p.getTipoEmprestimo().setSemPoliticaEmprestimo( (Boolean) objects[7] );
			
			p.setQuantidadeMateriais( (Integer) objects[8]);
			p.setPrazoEmprestimo( (Integer) objects[9]);
			p.setTipoPrazo( (Short) objects[10]);
			p.setQuantidadeRenovacoes( (Integer) objects[11]);
			p.setAlteravel( (Boolean) objects[12]);
			p.setPersonalizavel( (Boolean) objects[13]);
			p.setAtivo( (Boolean) objects[14]);
			
			if(lista.contains(p))
				p = lista.get(lista.indexOf(p));
			else
				lista.add(p);
			
			if(objects[15] != null)
				p.adicionaStatusMaterial( new StatusMaterialInformacional(  (Integer) objects[15],  (String) objects[16], (Boolean) objects[17])  ) ;
				
			if(objects[18] != null)
				p.adicionaTipoMaterial( new TipoMaterial(  (Integer) objects[18],  (String) objects[19])  ) ;
			
		}
		return lista;
	}
	
	
	
	/**
	 *    <p>Encontra todas as políticas de empréstimos ativas para um determinado tipo de empréstimos.</p>
	 *
	 *    <p>Esse método é usado geralmente no momento de remover um tipo de empréstimo do sistema, todos as políticas associadas a ele devem ser removidas.</p>
	 *
	 * @param biblioteca
	 * @param tipoUsuario
	 * @return
	 * @throws DAOException
	 */
	public List<PoliticaEmprestimo> findPoliticasEmpretimoAtivasByTipoEmprestimo(int idTipoEmprestimo) throws DAOException{
		
		Criteria c = getSession().createCriteria( PoliticaEmprestimo.class );
		c.add( Restrictions.eq( "tipoEmprestimo.id" , idTipoEmprestimo) );
		c.add( Restrictions.eq( "ativo" , true ) );
		
		@SuppressWarnings("unchecked")
		List<PoliticaEmprestimo> lista = c.list();
		return lista;
	}
	
	
	
	/**
	 *    Encontra todas as políticas de empréstimos ativas associadas a um determinado status
	 *    
	 *    <p>Esse método é usado geralmente no momento de remover um status do sistema, a associação com política de empréstimo deve ser desfeita, já que o status não existe mais.</p>
	 *
	 * @param biblioteca
	 * @param tipoUsuario
	 * @return
	 * @throws DAOException
	 */
	public List<PoliticaEmprestimo> findPoliticasEmpretimoAtivasByStatusMaterial(int idStatusMaterial) throws DAOException{
		
		String hql = " SELECT "+PROJECAO_PADRAO+" FROM PoliticaEmprestimo p "
				+" LEFT JOIN p.statusMateriais status "
				+" LEFT JOIN p.tiposMateriais tiposMateriais "
				+" WHERE p.ativo = :true "
				+" AND status.id = :idStatusMaterial";
		
		Query q = getSession().createQuery( hql );
		q.setInteger("idStatusMaterial",  idStatusMaterial);
		q.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List<Object[]> dados = q.list();
		
		return  montaDadosPolitica(dados);

	}
	
	
	/**
	 *    Encontra todas as políticas de empréstimos ativas associadas a um determinado tipo de Material
	 *    
	 *    <p>Esse método é usado geralmente no momento de remover um tipo de material do sistema, a associação com política de empréstimo deve ser desfeita, já que o tipo de Material não existe mais.</p>
	 *
	 * @param biblioteca
	 * @param tipoUsuario
	 * @return
	 * @throws DAOException
	 */
	public List<PoliticaEmprestimo> findPoliticasEmpretimoAtivasByTipoMaterial(int idTipoMaterial) throws DAOException{
		String hql = " SELECT "+PROJECAO_PADRAO+" FROM PoliticaEmprestimo p "
				+" LEFT JOIN p.statusMateriais status "
				+" LEFT JOIN p.tiposMateriais tiposMateriais "
				+" WHERE p.ativo = :true "
				+" AND tiposMateriais.id = :idTipoMaterial";
		
		Query q = getSession().createQuery( hql );
		q.setInteger("idTipoMaterial",  idTipoMaterial);
		q.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List<Object[]> dados = q.list();
		
		return  montaDadosPolitica(dados);
	}
	
	
	
	
	
	
	/**
	 *    <p>Retorna o prazo e quantidade da política de empréstimos ativa mais utiliza. Normalmente 
	 *       será a política para alunos de gradução, materiais regulares e empréstimos normais </p>
	 *
	 *    <p> Esse prazo e quantiade renovações são utilizados para gerar a previsão de efetivação das 
	 *    reservas de materiais da biblioteca</p>
	 *
	 * @param biblioteca
	 * @param tipoUsuario
	 * @return
	 * @throws DAOException
	 */
	public Integer[] findPrazoEQuantiadeRenovacoesPoliticaMaisUsada() throws DAOException{
		
		String sql = " SELECT p.id_politica_emprestimo, p.prazo_emprestimo, p.quantidade_renovacoes, count(*) as quantidade_emprestimos "
		+" FROM biblioteca.emprestimo  e "
		+" INNER JOIN biblioteca.politica_emprestimo p ON p.id_politica_emprestimo = e.id_politica_emprestimo "
		+" WHERE e.situacao = :situacao AND e.ativo = :true  AND p.ativo = :true "
		+" GROUP BY p.id_politica_emprestimo, p.prazo_emprestimo, p.quantidade_renovacoes " 
		+" ORDER BY quantidade_emprestimos desc  ";
		
		Query q = getSession().createSQLQuery(sql);
		q.setBoolean("true", true);
		q.setInteger("situacao", Emprestimo.EMPRESTADO);
		q.setMaxResults(1);
		Object[] temp = (Object[]) q.uniqueResult();
		
		Integer[] retorno = new Integer[2];
		retorno[0] = (Integer) temp[1];
		retorno[1] = ((Short) temp[2]).intValue();
		
		return retorno;
	}
	
	
}
