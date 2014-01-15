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
 * DAO para as pol�ticas de empr�stimo da biblioteca.
 *
 * @author jadson
 * @since 03/06/2009
 * @version 1.0 criacao da classe
 * @version 1.5 - jadson em 20/02/2012 - novos m�todos de consulta par suportar a nova modelagem da pol�tica.
 * 
 */
public class PoliticaEmprestimoDao extends GenericSigaaDAO{

	
	
	/** Proje��o padr�o para recuperan��o dos dados de uma pol�tica */
	final static String PROJECAO_PADRAO = " p.id, p.biblioteca.id, p.biblioteca.descricao, p.vinculo, " +
			" p.tipoEmprestimo.id, p.tipoEmprestimo.descricao, p.tipoEmprestimo.institucional, p.tipoEmprestimo.semPoliticaEmprestimo," + // todas as informa��es para validar o tipo de empr�stimo correto
			" p.quantidadeMateriais, p.prazoEmprestimo, p.tipoPrazo, p.quantidadeRenovacoes, p.alteravel, p.personalizavel, p.ativo, "+
			" status.id, status.descricao, status.permiteEmprestimo," +
			" tiposMateriais.id, tiposMateriais.descricao ";
	
	
	
	
	
	
	
	
	/**
	 *    <p>Encontra a pol�tica de empr�stimo espec�fica para todos os par�metros passados. 
	 *    Biblioteca, Tipo do Usu�rio, Tipo de Empr�stimo, Status do Material e Tipo de Material</p>
	 *
	 *    <p> Era para existir somente um pol�tica por vez ativa com esses par�metros. </p>
	 *
	 *    <p>
	 *    <strong>
	 *    ESSE AQUI � O M�TODO A SER USADO NO MOMENTO DE REALIZAR UM NOVO EMPR�STIMO, ELE RETORNA A POL�TICA ATIVIA 
	 *    A SER USADA DEPENDENDO DAS CARACTER�STICAS DO MATERIAL A SER EMPRESTADO !!!! <br/>
	 *    N�O O ALTERE E MUITO MENOS UTILIZE PARA OUTRA COISA, A N�O SER QUE VOC� REALMENTE SAIBA O QUE EST� FAZENDO ! <br/>
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
		
		// Condi��es de entrada //
		if(bibliotecaPassada == null)
			throw new NegocioException("Para ser emprestado o material precisa pertencer a uma biblioteca.");
		if(vinculo == null)
			throw new NegocioException("Para realizar o empr�stimos � preciso que o usu�rio possua um v�nculo no sistema.");
		if(tipoEmprestimo == null)
			throw new NegocioException("Para realizar o empr�stimos � preciso escolher um tipo de empr�stimo.");
		if(status == null)
			throw new NegocioException("Para ser emprestado o material precisa possuir um status.");
		if(tipoMaterial == null)
			throw new NegocioException("Para ser emprestado o material precisa possuir um tipo de materail.");
		
		
		boolean permitePoliticasDiferentesPorBiblioteca = ParametroHelper.getInstance().getParametroBoolean(ParametrosBiblioteca.SISTEMA_PERMITE_CONFIGURAR_POLITICAS_DIFERENTES_POR_BIBLIOTECA);
		
		Integer idBibliotecaPolitica = -1;
		
		/*
		 *   Apenas se o sistema est� configurado para trabalhar com o v�rias pol�ticas, usa o id da biblioteca passada. <br/>
		 *   Se n�o todas v�o usar sempre a pol�tica da central, a biblioteca passada n�o � usada. <br/><br/>
		 *   
		 *   Aqui houve uma altera��o na regra de neg�cio. Antes para todo todo material existia obrigatoriamente um pol�tica no banco.<br/>
		 *   Agora pode ser que n�o exista, porque exemplo: na UFRN n�o existe uma pol�tica para o tipo de empr�stimos regular para material especial.<br/><br/>
		 *   
		 *   Nesse caso, se tentar empr�star uma material especial como empr�stimos normal, o sistema n�o vai deixar.<br/><br/>
		 *   
		 *   Ent�o quando se ativa o empr�stimo para todos as bibliotecas, cada biblioteca tem que definir suas pol�tica n�o vai usar mais a da central.<br/>
		 *   Por que se n�o o administrador poderia n�o definir alguma pol�tica e o usu�rio consegui emprestar pela pol�tica da central.<br/>
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
				//+" AND ( status.id = :idStatus  OR status.id IS NULL )"                         // PARA A POL�TICA QUE CONTENHA O STATUS PASSADO, OU QUE SEJA V�LIDAS PARA TODOS OS STATUS
				//+" AND ( tiposMateriais.id = :idTipoMaterial OR tiposMateriais.id IS NULL )"; // PARA A POL�TICA QUE CONTENHA O TIPO DE MATERIAL PASSADO, OU QUE SEJA V�LIDAS PARA TODOS OS TIPOS DE MATERIAIS
		} else{
			hql += " AND p.personalizavel = :true ";                                            // S� TEM UMA NO BANCO, ENT�O N�O PRECISA BUSCAR PELOS OUTROS DADOS
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
		 *  N�o d� para filtar o status e tipo de material diretamente na consultas sql, sen�o a consulta 
		 *  s� retorna aquele status e tipo de material buscado. Tem que retornar todos da politica de depois
		 *  selecionar aquelas politicas que cont�m o status.
		 */
		List<PoliticaEmprestimo> politicasRetornadas = new ArrayList<PoliticaEmprestimo>();
		
		
		for (PoliticaEmprestimo politicaEmprestimo : politicas) {
			if(politicaEmprestimo.getStatusMateriais() == null || politicaEmprestimo.getStatusMateriais().contains(status)
					&&  ( politicaEmprestimo.getTiposMateriais() == null || politicaEmprestimo.getTiposMateriais().contains(tipoMaterial) ) )
				politicasRetornadas.add(politicaEmprestimo);
		}
		
		
		// Condi��es de Sa�da //
		if(politicasRetornadas.size() <= 0){
			throw new NegocioException("N�o existe nenhuma pol�tica de empr�stimos cadastrada que permita realizar o empr�stimos desse material.");
		}else{
			if(politicasRetornadas.size() > 1){
				throw new NegocioException("Erro de inconsist�ncia no cadastro das pol�ticas de empr�stimo. Existe mais de uma pol�tica cadastrada que pode ser usada com esse material.");
			}else{
				return politicasRetornadas.get(0);
			}
		}
		
	}
	
	
	/**
	 *  <p>Encontra todas as pol�ticas de empr�stimos ativas para a biblioteca e vinculo do usu�rios passados que podem ser alteradas.</p>
	 *
	 *  <p>Geralmente usadas no cadastro da pol�tica.</p>
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
	 *  <p>Encontra todas as pol�ticas de empr�stimos ativas para a biblioteca e vinculo do usu�rios passados que podem ser alteradas.</p>
	 *
	 *  <p>Geralmente usadas no cadastro da pol�tica.</p>
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
	
	
	/** Monta as informa��es das pol�ticas para serem retornadas, geralmente usado no cadastro */
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
	 *    <p>Encontra todas as pol�ticas de empr�stimos ativas para um determinado tipo de empr�stimos.</p>
	 *
	 *    <p>Esse m�todo � usado geralmente no momento de remover um tipo de empr�stimo do sistema, todos as pol�ticas associadas a ele devem ser removidas.</p>
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
	 *    Encontra todas as pol�ticas de empr�stimos ativas associadas a um determinado status
	 *    
	 *    <p>Esse m�todo � usado geralmente no momento de remover um status do sistema, a associa��o com pol�tica de empr�stimo deve ser desfeita, j� que o status n�o existe mais.</p>
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
	 *    Encontra todas as pol�ticas de empr�stimos ativas associadas a um determinado tipo de Material
	 *    
	 *    <p>Esse m�todo � usado geralmente no momento de remover um tipo de material do sistema, a associa��o com pol�tica de empr�stimo deve ser desfeita, j� que o tipo de Material n�o existe mais.</p>
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
	 *    <p>Retorna o prazo e quantidade da pol�tica de empr�stimos ativa mais utiliza. Normalmente 
	 *       ser� a pol�tica para alunos de gradu��o, materiais regulares e empr�stimos normais </p>
	 *
	 *    <p> Esse prazo e quantiade renova��es s�o utilizados para gerar a previs�o de efetiva��o das 
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
