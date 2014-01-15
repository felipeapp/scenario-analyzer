/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 18/02/2011
 * 
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ReservaMaterialBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ReservaMaterialBiblioteca.StatusReserva;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *
 * <p> Dao exclusivo para as buscas utilizados na solicitação de reservas de materiais do 
 *   sistema. </p>
 * 
 * <p> <strong>IMPORTANTE: </strong> Todas as busca nessa classe devem ser ordenadas pela data da solicitação da reserva. 
 * Porque o sistema guarda uma fila de reservas, então a primeira a ser solicitada DEVE ser a primeira a ser atendida, sempre ! </p>
 * 
 * @author jadson
 *
 */
public class ReservaMaterialBibliotecaDao extends GenericSigaaDAO{

	/**
	 * <p>Projeção padrão utilizados nas consultas de reservas para mostrar a listagem das reservas de um título específico. </p>
	 */
	private final String projecaoReservas= " r.id, r.tituloReservado.id,  r.usuarioReserva.id, r.usuarioReserva.pessoa.nome, r.status, r.dataSolicitacao, r.dataEmEspera, r.prazoRetiradaMaterial, r.dataPrevisaoRetiradaMaterial ";
	
	
	
	/**
	 * <p>Retorna as reservas EM ESPERA para o título do material passado.</p>
	 *
	 * @param idReserva
	 * @param idTitulo
	 * @return
	 * @throws DAOException
	 */
	public List<ReservaMaterialBiblioteca> findReservasEmEsperaDoTituloDoMaterialEmOrdem(int idTituloDoMaterial) throws DAOException{
		
		MaterialInformacionalDao daoMaterial = null;
		
		try{
			daoMaterial = new MaterialInformacionalDao();
			
			StringBuilder hql = new StringBuilder(" SELECT "+projecaoReservas);
			
			hql.append(" FROM ReservaMaterialBiblioteca r ");
			hql.append(" WHERE r.tituloReservado.id = :idTitulo ");
			hql.append(" AND r.status = "+StatusReserva.EM_ESPERA);
			hql.append(" ORDER BY r.dataSolicitacao, r.id ");
			
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idTitulo", idTituloDoMaterial);
			
			@SuppressWarnings("unchecked")
			List<Object[]> dadosReservas = q.list();
			
			return new ArrayList<ReservaMaterialBiblioteca>( HibernateUtils.parseTo(dadosReservas, projecaoReservas, ReservaMaterialBiblioteca.class, "r"));
		
		}finally{
			if( daoMaterial != null ) daoMaterial.close();
		}
	}
	
	
	
	
	/**
	 * <p>Retorna a próxima reserva solicitada para o título do mateiral passado.</p>
	 *
	 * @param idReserva
	 * @param idTitulo
	 * @return
	 * @throws DAOException
	 */
	public ReservaMaterialBiblioteca findProximaReservaSolicitadaDoTituloDoMaterial(int idMaterial) throws DAOException{
		
		return findProximaReservaByStatusDoTituloDoMaterial(idMaterial, StatusReserva.SOLICITADA);
	}
	
	/**
	 * <p>Retorna a próxima reserva solicitada para o título do mateiral passado.</p>
	 *
	 * @param idReserva
	 * @param idTitulo
	 * @return
	 * @throws DAOException
	 */
	private ReservaMaterialBiblioteca findProximaReservaByStatusDoTituloDoMaterial(int idMaterial, StatusReserva status) throws DAOException{
		
		MaterialInformacionalDao daoMaterial = null;
		
		try{
			daoMaterial = new MaterialInformacionalDao();
		
			int idTitulo = daoMaterial.findIdTituloMaterial(idMaterial);
			
			StringBuilder hql = new StringBuilder(" SELECT "+projecaoReservas);
			
			hql.append(" FROM ReservaMaterialBiblioteca r ");
			hql.append(" WHERE r.tituloReservado.id = :idTitulo ");
			hql.append(" AND r.status = "+status);
			hql.append(" ORDER BY r.dataSolicitacao, r.id ");
			
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idTitulo", idTitulo);
			q.setMaxResults(1);                       // Retorna apenas a próxima
			
			
			Object[] dadosReserva = (Object[]) q.uniqueResult(); 
			
			if(dadosReserva != null){
				ReservaMaterialBiblioteca r = new ReservaMaterialBiblioteca();
				r.setId( (Integer) dadosReserva[0]);
				r.setTituloReservado( new TituloCatalografico( (Integer) dadosReserva[1]) );
				
				UsuarioBiblioteca u = new UsuarioBiblioteca((Integer) dadosReserva[2]);
				u.setPessoa( new Pessoa(0, (String) dadosReserva[3]));
				r.setUsuarioReserva( u );
				
				r.setStatus( (StatusReserva) dadosReserva[4]);
				r.setDataSolicitacao( (Date) dadosReserva[5]);
				r.setDataEmEspera( (Date) dadosReserva[6]);
				r.setPrazoRetiradaMaterial( (Date) dadosReserva[7]);
				r.setDataPrevisaoRetiradaMaterial( (Date) dadosReserva[8]);
				
				return r;
			}else
				return null;
		
		}finally{
			if( daoMaterial != null ) daoMaterial.close();
		}
	}


	/**
	 * <p>Retorna a listagem de todas as reservas ativas do Título passada que foram solicitadas depois da reserva passada.</p>
	 *
	 * @param idReserva
	 * @param idTitulo
	 * @return
	 * @throws DAOException
	 */
	public List<ReservaMaterialBiblioteca> findAllReservasAtivasDoMesmoTituloPosterioresAReserva(int idTitulo, Date dataSolicitacao) throws DAOException{
		
		final String projecao= " r.id, r.tituloReservado.id, r.prazoRetiradaMaterial, r.status, r.usuarioReserva.id, r.dataPrevisaoRetiradaMaterial ";
		
		StringBuilder hql = new StringBuilder(" SELECT "+projecao);
		
		hql.append(" FROM ReservaMaterialBiblioteca r ");
		hql.append(" WHERE r.tituloReservado.id = :idTitulo ");
		hql.append(" AND r.status in "+UFRNUtils.gerarStringIn( ReservaMaterialBiblioteca.getReservasAtivas() ) );
		hql.append(" AND r.dataSolicitacao > :dataSolicitacao");
		hql.append(" ORDER BY r.dataSolicitacao, r.id ");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idTitulo", idTitulo);
		q.setTimestamp("dataSolicitacao", dataSolicitacao);
		
		@SuppressWarnings("unchecked")
		List<Object[]> linhas = q.list();
		
		return new ArrayList<ReservaMaterialBiblioteca>( HibernateUtils.parseTo(linhas, projecao, ReservaMaterialBiblioteca.class, "r"));
	}
	
	
	
	
	/**
	 * <p>Verifica se a reserva passada está com status SOLICITADA no banco.</p>
	 *
	 * @param idReserva
	 * @return
	 * @throws DAOException
	 */
	public boolean isReservaSolicitadaNoBanco(int idReserva) throws DAOException{

		StringBuilder hql = new StringBuilder(" SELECT r.status ");
		hql.append(" FROM ReservaMaterialBiblioteca r ");
		hql.append(" WHERE r.id = :idReserva");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idReserva", idReserva);
		
		StatusReserva statusBanco = (StatusReserva) q.uniqueResult();
		
		if(statusBanco == StatusReserva.SOLICITADA ){
			return true;
		}
		
		return false;
	}
	
	

	/**
	 * <p>Verifica se a reserva passada está ativa no banco.</p>
	 * 
	 * @param idReserva
	 * @return
	 * @throws DAOException
	 */
	public boolean isReservaAtivaNoBanco(int idReserva) throws DAOException{

		StringBuilder hql = new StringBuilder(" SELECT r.status ");
		hql.append(" FROM ReservaMaterialBiblioteca r ");
		hql.append(" WHERE r.id = :idReserva");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idReserva", idReserva);
		
		StatusReserva statusBanco = (StatusReserva) q.uniqueResult();
		
		List<StatusReserva> statusReservaAtiva = Arrays.asList(ReservaMaterialBiblioteca.getReservasAtivas());
		
		Arrays.asList(ReservaMaterialBiblioteca.getReservasAtivas());
		
		if (statusReservaAtiva.contains(statusBanco)) {
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * <p>Encontra as informações sobre o funcionamento da biblioteca do material que estiver disponíveldo 
	 * para Título passado.</p>
	 *  
	 * <p>Essas informações são usadas para saber quais dias a biblioteca irá funcionar e poder calcular 
	 * com mais precissão a data que o prazo para pegar o material reservado deve cair.</p> 
	 *  
	 * <p>Geralmente títulos reservados só possuiem 1 material disponível por vez. Então vai pegar as informações 
	 * da biblioteca desse material. Pode ocorrer de possuir mais de uma material ao mesmo tempo disponível caso sejam
	 * devolvidos 2 ou mais materiais do Título do mesmo dia.  Ai vai pegar as informações do primeiro que encontrar.</p>
	 *  
	 * <p>Não há como saber com exatidão se a biblioteca vai está fechada ou não, porque um título reservado pode 
	 * contér materiais em várias biblioteca, no pior caso, o usuário vai ganhar 1 ou 2 dias a mais para concretizar 
	 * a reserva.</p> 
	 *
	 * @param idTitulo
	 * @return
	 * @throws DAOException 
	 */
	public Biblioteca findInfomacoesFuncionamentoBibliotecaMaterialDisponivelDoTitulo(int idTitulo) throws DAOException{
	
		List<Integer> idsMateriais = findIdsMateriaisParaReservasByTitulo(idTitulo, true);
		
		int idMaterialDisponivel = 0;
		
		if(idsMateriais.size() > 0){
			idMaterialDisponivel = idsMateriais.get(0);  // Se tiver mais de um disponível, considerar apenas o primeiro encontrado
		
			StringBuilder hql = new StringBuilder(" SELECT b.id, b.funcionaSabado, b.funcionaDomingo ");
			hql.append("FROM MaterialInformacional m ");
			hql.append("INNER JOIN m.biblioteca b ");
			hql.append("WHERE m.id = :idMaterial ");
			
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idMaterial", idMaterialDisponivel);
			Object[] retorno = (Object[]) q.uniqueResult();
			
			Biblioteca biblioteca = new Biblioteca((Integer) retorno[0]);
			biblioteca.setFuncionaSabado((Boolean ) retorno[1]);
			biblioteca.setFuncionaDomingo((Boolean ) retorno[2]);
			return biblioteca;
			
		}
		return null;
	}
	
	
	/**
	 *  Método que encontra o primeiro prazo de devolução de um material do Título passado. Serve 
	 *  para ter uma estivametiva de quando a reserva vai ser efetuada. 
	 *  
	 *
	 * @param idTitulo
	 * @return
	 * @throws DAOException
	 */
	
	public List<Object[]> findPrazosDevolucaoMaterialDoTitulo(int idTitulo) throws DAOException {
		
		List<Integer> idsMateriais = findIdsMateriaisParaReservasByTitulo(idTitulo, false);

		StringBuilder hql = new StringBuilder(" SELECT e.id, e.prazo, p.prazoEmprestimo, p.quantidadeRenovacoes, p.tipoPrazo ");
		
		hql.append(" FROM Emprestimo e ");
		hql.append(" INNER JOIN e.material m ");
		hql.append(" INNER JOIN e.politicaEmprestimo p ");
		hql.append(" WHERE e.dataDevolucao IS NULL AND e.dataEstorno IS NULL");  // emprestimos ativos
		hql.append(" AND m.id in ( :idMateriais ) ");                            // dos materiais do título passado
		
		Query q = getSession().createQuery(hql.toString());
		q.setParameterList("idMateriais", idsMateriais);
		 
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		
		return list;
		
	}
	
	/**
	 *  Método que encontra o primeiro prazo de devolução de um material do Título passado. Serve 
	 *  para ter uma estivametiva de quando a reserva vai ser efetuada. 
	 *  
	 *
	 * @param idTitulo
	 * @return
	 * @throws DAOException
	 */
	
	public Date findPrimeiroPrazoDevolucaoMaterialDoTitulo(int idTitulo) throws DAOException {
		
		List<Integer> idsMateriais = findIdsMateriaisParaReservasByTitulo(idTitulo, false);

		StringBuilder hql = new StringBuilder(" SELECT e.prazo ");
		
		hql.append(" FROM Emprestimo e ");
		hql.append(" INNER JOIN e.material m ");
		hql.append(" INNER JOIN e.politicaEmprestimo p ");
		hql.append(" WHERE e.situacao = :situacao AND e.dataDevolucao IS NULL AND e.dataEstorno IS NULL ");  // emprestimos ativos
		hql.append(" AND m.id in ( :idMateriais ) ");                            // dos materiais do título passado
		hql.append(" ORDER BY e.prazo ASC "); 
		
		Query q = getSession().createQuery(hql.toString());
		q.setParameterList("idMateriais", idsMateriais);
		q.setInteger("situacao", Emprestimo.EMPRESTADO);
		q.setMaxResults(1);
		
		
		return (Date) q.uniqueResult();
		
	}
	
	
	/**
	 * <p>Retorna os ids dos materiais ativos do título passado que são considerados para a realização das reservas.</p>
	 * 
	 * <p>Os materiais que são considerados para a realização das reservas são os não anexos e que não possuem os status 
	 * que não podem ser reservados.</p>
	 * 	
	 * @param idTitulo
	 * @return
	 * @throws DAOException
	 */
	
	private List<Integer> findIdsMateriaisParaReservasByTitulo (int idTitulo, boolean disponiveis) throws DAOException{
		
		List<Integer> retorno = new ArrayList<Integer>();
		
		String hql = " SELECT e.id" 
			+" FROM Exemplar e "
			+" WHERE e.tituloCatalografico.id = " + idTitulo+" AND e.ativo = trueValue() AND e.situacao.situacaoDeBaixa = falseValue()"
			+" AND ( e.exemplarDeQuemSouAnexo IS NULL AND e.anexo = falseValue()  )"
			+" AND ( e.status.aceitaReserva = trueValue()  )  ";	
			if(disponiveis)
				hql += " AND e.situacao.situacaoDisponivel = trueValue() ";
		
		Query q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		List<Object> list = q.list();
		
		for (Object object : list) {
			retorno.add((Integer)  object);
		}
		
		
		if(retorno.size() == 0 ){ // Se não tem exemplares, verifica se os materiais são fascículos
			
			hql = " SELECT f.id" 
				+" FROM Fasciculo f "
				+" WHERE f.assinatura.tituloCatalografico.id = " + idTitulo+" AND f.incluidoAcervo = trueValue() AND f.ativo = trueValue() AND f.situacao.situacaoDeBaixa = falseValue() "
				+" AND ( f.fasciculoDeQuemSouSuplemento IS NULL AND f.suplemento = falseValue() )  "
				+" AND ( f.status.aceitaReserva = trueValue()  )  ";
			
				if(disponiveis)
					hql += " AND f.situacao.situacaoDisponivel = trueValue() ";
			q = getSession().createQuery(hql);
			
			@SuppressWarnings("unchecked")
			List<Object> lista = q.list();
			
			for (Object object : lista) {
				retorno.add((Integer)  object);
			}
		}
		
		return retorno;
	}
	
	
	/**
	 *  <p>Recupera a lista de reservas ativas do usuário biblioteca passado ordenadas pela data da solicitação. </p>
	 *
	 *  
	 * @param idTituloCatalografico
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public List<ReservaMaterialBiblioteca> buscasReservasAtivasUsuarioEmOrdem( int idUsuarioBiblioteca) throws DAOException {
		
		
		StringBuilder hql = new StringBuilder(" SELECT "+projecaoReservas);
		
		hql.append(" FROM ReservaMaterialBiblioteca r ");
		hql.append(" WHERE r.usuarioReserva.id = :idUsuarioBiblioteca ");
		hql.append(" AND r.status in "+UFRNUtils.gerarStringIn( ReservaMaterialBiblioteca.getReservasAtivas() ) );
		hql.append(" ORDER BY r.dataSolicitacao, r.id ");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idUsuarioBiblioteca", idUsuarioBiblioteca);
		
		@SuppressWarnings("unchecked")
		List<Object[]> linhas = q.list();
		
		return new ArrayList<ReservaMaterialBiblioteca>( HibernateUtils.parseTo(linhas, projecaoReservas, ReservaMaterialBiblioteca.class, "r"));
		
	}
	
	
	/**
	 * <p>Retorna as reservas ativos para o Título do Material passado.</p>
	 *
	 * @param idReserva
	 * @param idTitulo
	 * @return
	 * @throws DAOException
	 */
	public List<ReservaMaterialBiblioteca> buscasReservasAtivasDoTituloDoMaterialEmOrdem(int idMaterial) throws DAOException{
		
		MaterialInformacionalDao daoMaterial = null;
		
		try{
			daoMaterial = new MaterialInformacionalDao();
			
			int idTitulo = daoMaterial.findIdTituloMaterial(idMaterial);
			
			return buscasReservasAtivasTituloEmOrdem(idTitulo);
		
		}finally{
			if( daoMaterial != null ) daoMaterial.close();
		}
	}
	
	
	
	/**
	 *  <p>Recupera a lista de reservas ativas do título passado ordenadas pela data da solicitação. </p>
	 *
	 *  
	 * @param idTituloCatalografico
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public List<ReservaMaterialBiblioteca> buscasReservasAtivasTituloEmOrdem( int idTituloCatalografico) throws DAOException {
		
		
		StringBuilder hql = new StringBuilder(" SELECT "+projecaoReservas);
		
		hql.append(" FROM ReservaMaterialBiblioteca r ");
		hql.append(" WHERE r.tituloReservado.id = :idTitulo ");
		hql.append(" AND r.status in "+UFRNUtils.gerarStringIn( ReservaMaterialBiblioteca.getReservasAtivas() ) );
		hql.append(" ORDER BY r.dataSolicitacao, r.id ");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idTitulo", idTituloCatalografico);
		
		@SuppressWarnings("unchecked")
		List<Object[]> linhas = q.list();
		
		return new ArrayList<ReservaMaterialBiblioteca>( HibernateUtils.parseTo(linhas, projecaoReservas, ReservaMaterialBiblioteca.class, "r"));
		
	}

	
	/**
	 *  <p>Recupera o prazo para retirada do material reservado, para mostrar aos usuários que o material está disponível mas tem reserva para ele.</p>
	 *
	 *  
	 * @param idTituloCatalografico
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public Date findPrazoConcluirReservasEmEsperaTitulo( int idTituloCatalografico) throws DAOException {
		
		
		StringBuilder hql = new StringBuilder(" SELECT  r.prazoRetiradaMaterial ");
		hql.append(" FROM ReservaMaterialBiblioteca r ");
		hql.append(" WHERE r.tituloReservado.id = :idTitulo ");
		hql.append(" AND r.status = "+ReservaMaterialBiblioteca.StatusReserva.EM_ESPERA);
		hql.append(" ORDER BY r.prazoRetiradaMaterial DESC ");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idTitulo", idTituloCatalografico);
		q.setMaxResults(1);
		
		return( Date ) q.uniqueResult();
	}
	
	
	
	/**
	 * Utilizando para contar a quantidade de materiais do título ativos para verificar as regras da reserva.
	 *
	 * @param idTituloCatalografico
	 * @return
	 * @throws DAOException
	 */
	public int countQuantidadeMateriaisAtivosDoTitulo(int idTituloCatalografico) throws DAOException {
		
		Long quantidade = 0l;
		
		StringBuilder hqlCountExemplar = new StringBuilder("SELECT COUNT( DISTINCT e.id ) "
			+" FROM Exemplar e "
			+" INNER JOIN e.tituloCatalografico t "
			+" INNER JOIN e.situacao s "
			+" WHERE t.id = :idTitulo ");
		
		hqlCountExemplar.append(" AND s.situacaoDeBaixa = falseValue() ");
			
			hqlCountExemplar.append(" AND e.ativo = trueValue() "  
			+" AND ( e.exemplarDeQuemSouAnexo IS NULL AND e.anexo = falseValue()  )  "
			+" AND ( e.status.aceitaReserva = trueValue()  )  ");
		
		Query q = getSession().createQuery(hqlCountExemplar.toString());
		q.setInteger("idTitulo", idTituloCatalografico);
		quantidade = (Long) q.uniqueResult();
		
		
		StringBuilder hqlCountFasciculo = new StringBuilder("SELECT COUNT( DISTINCT f.id ) "
			+" FROM Fasciculo f "
			+" INNER JOIN f.situacao s "
			+" INNER JOIN f.assinatura a "
			+" INNER JOIN a.tituloCatalografico t "
			+" WHERE t.id = :idTitulo ");
		
		
		hqlCountExemplar.append(" AND s.situacaoDeBaixa = falseValue() ");
		
		hqlCountFasciculo.append(" AND f.ativo = trueValue() "  
			+" AND (f.fasciculoDeQuemSouSuplemento IS NULL AND f.suplemento = falseValue() )  "
			+" AND (f.status.aceitaReserva = trueValue()  )  ");
		
		Query q2 = getSession().createQuery(hqlCountFasciculo.toString());
		q2.setInteger("idTitulo", idTituloCatalografico);
		quantidade += (Long) q2.uniqueResult();
		
		return quantidade.intValue();
		
		
	}
	
	
	
	/**
	 * <p> Conta os materiais que estão numa situação diferente de empréstado, se tiver pelo mes </p>
	 * 
	 * @param idTituloCatalografico
	 * @return a quantidade de materiais disponíveis do título 
	 * @throws DAOException 
	 */
	public int countMateriaisNaoAnexoDisponiveisDoTitulo(int idTituloCatalografico) throws DAOException {
		
		Long quantidade = 0l;
		
		StringBuilder hqlCountExemplar = new StringBuilder("SELECT COUNT( DISTINCT e.id ) "
			+" FROM Exemplar e "
			+" INNER JOIN e.tituloCatalografico t "
			+" INNER JOIN e.situacao s "
			+" WHERE t.id = :idTitulo ");
			
		hqlCountExemplar.append(" AND s.situacaoDisponivel = trueValue() ");
		hqlCountExemplar.append(" AND s.situacaoDeBaixa = falseValue() ");
			
		hqlCountExemplar.append(" AND e.ativo = trueValue() "  
			+" AND ( e.exemplarDeQuemSouAnexo IS NULL AND e.anexo = falseValue()  )  "
			+" AND ( e.status.aceitaReserva = trueValue()  )  ");
		
		Query q = getSession().createQuery(hqlCountExemplar.toString());
		q.setInteger("idTitulo", idTituloCatalografico);
		quantidade = (Long) q.uniqueResult();
		
		
		StringBuilder hqlCountFasciculo = new StringBuilder("SELECT COUNT( DISTINCT f.id ) "
			+" FROM Fasciculo f "
			+" INNER JOIN f.situacao s "
			+" INNER JOIN f.assinatura a "
			+" INNER JOIN a.tituloCatalografico t "
			+" WHERE t.id = :idTitulo ");
		
		
		hqlCountExemplar.append(" AND s.situacaoDisponivel = trueValue() ");
		hqlCountExemplar.append(" AND s.situacaoDeBaixa = falseValue() ");
		
		hqlCountFasciculo.append(" AND f.ativo = trueValue() "  
			+" AND (f.fasciculoDeQuemSouSuplemento IS NULL AND f.suplemento = falseValue() )  "
			+" AND (f.status.aceitaReserva = trueValue()  )  ");
		
		Query q2 = getSession().createQuery(hqlCountFasciculo.toString());
		q2.setInteger("idTitulo", idTituloCatalografico);
		quantidade += (Long) q2.uniqueResult();
		
		return quantidade.intValue();
	}
	
	
	/**
	 * <p> Conta os materiais que estão na situação de empréstado para o Título passado. </p>
	 * 
	 * @param idTituloCatalografico
	 * @return a quantidade de materiais disponíveis do título 
	 * @throws DAOException 
	 */
	public int countMateriaisEmprestadosDoTitulo(int idTituloCatalografico) throws DAOException {
		
		Long quantidade = 0l;
		
		StringBuilder hqlCountExemplar = new StringBuilder("SELECT COUNT( DISTINCT e.id ) "
			+" FROM Exemplar e "
			+" INNER JOIN e.tituloCatalografico t  "
			+" INNER JOIN e.situacao s "
			+" WHERE t.id = :idTitulo ");
			
		hqlCountExemplar.append(" AND s.situacaoEmprestado = trueValue() ");
		hqlCountExemplar.append(" AND s.situacaoDeBaixa = falseValue() ");
		
		hqlCountExemplar.append(" AND e.ativo = trueValue() "  
		+" AND ( e.exemplarDeQuemSouAnexo IS NULL AND e.anexo = falseValue()  )  "
		+" AND ( e.status.aceitaReserva = trueValue()  )  ");
		
		Query q = getSession().createQuery(hqlCountExemplar.toString());
		q.setInteger("idTitulo", idTituloCatalografico);
		quantidade = (Long) q.uniqueResult();
		
		
		StringBuilder hqlCountFasciculo = new StringBuilder("SELECT COUNT( DISTINCT f.id ) "
			+" FROM Fasciculo f "
			+" INNER JOIN f.situacao s "
			+" INNER JOIN f.assinatura a "
			+" INNER JOIN a.tituloCatalografico t "
			+" WHERE t.id = :idTitulo ");
		
		
		hqlCountFasciculo.append(" AND s.situacaoEmprestado = trueValue() ");
		hqlCountFasciculo.append(" AND s.situacaoDeBaixa = falseValue() ");
		
		hqlCountFasciculo.append(" AND f.ativo = trueValue() "  
			+" AND (f.fasciculoDeQuemSouSuplemento IS NULL AND f.suplemento = falseValue() )  "
			+" AND (f.status.aceitaReserva = trueValue()  )  ");
		
		Query q2 = getSession().createQuery(hqlCountFasciculo.toString());
		q2.setInteger("idTitulo", idTituloCatalografico);
		quantidade += (Long) q2.uniqueResult();
		
		return quantidade.intValue();
	}
	
	/**
	 * <p> Conta os materiais que estão na situação de empréstado e que tem comunicação de perda ativa. </p>
	 * 
	 * <p> Por exemplo: se o Título possui todos os materiais emprestado e todos eles estão com perdas comunicadas no sistema,
	 * o usuário não vai poder mais solicitar novas reserva, porque talvéz o material nunca seja reposto, ou o prazo para 
	 * reposição seja tam grande que não compense o usuário ficar esperando na reserva</p>
	 * 
	 * @param idTituloCatalografico
	 * @return a quantidade de materiais disponíveis do título 
	 * @throws DAOException 
	 */
	public int  countMateriaisEmprestadosComComunicacaoPerdaDoTitulo (int idTituloCatalografico) throws DAOException {

		BigInteger quantidade = BigInteger.ZERO;
		
		StringBuilder sqlCountExemplar = new StringBuilder("SELECT COUNT( DISTINCT e.id_exemplar ) "
			+" FROM biblioteca.exemplar e "
			+" INNER JOIN biblioteca.material_informacional m ON m.id_material_informacional = e.id_exemplar "
			+" INNER JOIN biblioteca.titulo_catalografico t ON e.id_titulo_catalografico = t.id_titulo_catalografico "
			+" INNER JOIN biblioteca.situacao_material_informacional s ON m.id_situacao_material_informacional = s.id_situacao_material_informacional "
			+" INNER JOIN biblioteca.status_material_informacional status ON m.id_status_material_informacional = status.id_status_material_informacional "
			
			+" INNER JOIN biblioteca.emprestimo emp ON m.id_material_informacional = emp.id_material "
			+" LEFT JOIN biblioteca.prorrogacao_emprestimo pro ON emp.id_emprestimo = pro.id_emprestimo "
			
			+" WHERE t.id_titulo_catalografico = :idTitulo " 
			+" AND emp.situacao = "+Emprestimo.EMPRESTADO                                              // Possuir um emprestimo ativo
			+" AND pro.tipo = "+TipoProrrogacaoEmprestimo.PERDA_DE_MATERIAL    // e esse empréstimo não pode ter uma prorrogação por perda
			);
			
		sqlCountExemplar.append(" AND s.situacao_emprestado = trueValue() ");
		sqlCountExemplar.append(" AND s.situacao_de_baixa = falseValue() ");
		
		sqlCountExemplar.append(" AND m.ativo = trueValue() "  
		+" AND ( e.id_exemplar_de_quem_sou_anexo IS NULL AND e.anexo = falseValue()  )  "
		+" AND ( status.aceita_reserva = trueValue()  )  ");
		
		Query q = getSession().createSQLQuery(sqlCountExemplar.toString());
		q.setInteger("idTitulo", idTituloCatalografico);
		quantidade = (BigInteger) q.uniqueResult();
		
		
		StringBuilder hqlCountFasciculo = new StringBuilder("SELECT COUNT( DISTINCT f.id_fasciculo ) "
			+" FROM biblioteca.fasciculo f "
			+" INNER JOIN biblioteca.material_informacional m ON m.id_material_informacional = f.id_fasciculo "
			+" INNER JOIN biblioteca.situacao_material_informacional s ON m.id_situacao_material_informacional = s.id_situacao_material_informacional "
			+" INNER JOIN biblioteca.assinatura a ON a.id_assinatura = f.id_assinatura "
			+" INNER JOIN biblioteca.titulo_catalografico t ON a.id_titulo_catalografico = t.id_titulo_catalografico "
			+" INNER JOIN biblioteca.status_material_informacional status ON m.id_status_material_informacional = status.id_status_material_informacional "
			
			+" INNER JOIN biblioteca.emprestimo emp ON m.id_material_informacional = emp.id_material "
			+" LEFT JOIN biblioteca.prorrogacao_emprestimo pro ON emp.id_emprestimo = pro.id_emprestimo "
			
			+" WHERE t.id_titulo_catalografico = :idTitulo "
			+" AND emp.situacao = "+Emprestimo.EMPRESTADO                      // Possuir um emprestimo ativo
			+" AND pro.tipo <> "+TipoProrrogacaoEmprestimo.PERDA_DE_MATERIAL   // e esse empréstimo não pode ter uma prorrogação por perda
			);
		
		
		sqlCountExemplar.append(" AND s.situacao_emprestado = trueValue() ");
		sqlCountExemplar.append(" AND s.situacao_de_baixa = falseValue() ");
		
		hqlCountFasciculo.append(" AND m.ativo = trueValue() "  
			+" AND (f.id_fasciculo_de_quem_sou_suplemento IS NULL AND f.suplemento = falseValue() )  "
			+" AND (status.aceita_reserva = trueValue()  )  ");
		
		Query q2 = getSession().createSQLQuery(hqlCountFasciculo.toString());
		q2.setInteger("idTitulo", idTituloCatalografico);
		quantidade.add((BigInteger) q2.uniqueResult());
		
		return quantidade.intValue();
	}
	
	
	
	/**
	 * Conta a quantidade de reservas ativas do usuário
	 * 
	 * @param idUsuarioBiblioteca
	 * @return a quantidade de reservas ativas do usuário
	 * @throws DAOException 
	 */
	
	public int countReservasMaterialAtivasDoUsuario(int idUsuarioBiblioteca) throws DAOException {
		
		String hql = "SELECT count(r.id) "
				+" FROM ReservaMaterialBiblioteca r "
				+" WHERE "
				+" r.usuarioReserva.id = " + idUsuarioBiblioteca  
				+" AND r.status in "+UFRNUtils.gerarStringIn( ReservaMaterialBiblioteca.getReservasAtivas() );
		
		Query q = getSession().createQuery(hql);
		Long temp = (Long) q.uniqueResult();
		
		return temp.intValue();
	}
	
	
	
	/**
	 * Conta a quantidade de reservas EM ESPERA do usuário
	 * 
	 * @param idTitulo
	 * @return a quantidade de reservas em espera do título
	 * @throws DAOException
	 */
	
	public int countReservasEmEsperaDoTitulo(int idTitulo) throws DAOException {
		
		String hql = "SELECT count(r.id) "
				+" FROM ReservaMaterialBiblioteca r "
				+" WHERE "
				+" r.tituloReservado.id = " + idTitulo  
				+" AND r.status = "+StatusReserva.EM_ESPERA;
		
		Query q = getSession().createQuery(hql);
		Long temp = (Long) q.uniqueResult();
		
		return temp.intValue();
	}
	
	

	/**
	 * Conta a quantidade de reservas com status SOLICITADA do título
	 * 
	 * @param idTitulo
	 * @return a quantidade de reservas solicitadas do título
	 * @throws DAOException
	 */
	
	public int countReservasAtivasDoTitulo(int idTitulo) throws DAOException {
		
		String hql = "SELECT count(r.id) "
				+" FROM ReservaMaterialBiblioteca r "
				+" WHERE "
				+" r.tituloReservado.id = " + idTitulo  
				+" AND r.status IN " + UFRNUtils.gerarStringIn(ReservaMaterialBiblioteca.getReservasAtivas());
		
		Query q = getSession().createQuery(hql);
		Long temp = (Long) q.uniqueResult();
		
		return temp.intValue();
	}
	
	
	
	/**
	 * Conta a quantidade de empréstimos que o usuário possui para materias do título passado
	 * 
	 * @param idUsuarioBiblioteca
	 * @return a quantidade de reservas ativas do usuário
	 * @throws DAOException 
	 */
	
	public int countEmprestimoAtivosParaUsuarioDoTitulo(int idUsuarioBiblioteca, int idTitulo) throws DAOException {
		
		List<Integer> idsMateriais = findIdsMateriaisParaReservasByTitulo(idTitulo, false);
		
		String hql = "SELECT count(e.id) "
				+" FROM Emprestimo e "
				+" INNER JOIN e.material m "
				+" WHERE "
				+" e.usuarioBiblioteca.id = :idUsuarioBiblioteca "  
				+" AND e.dataDevolucao IS NULL and e.dataEstorno IS NULL "  
				+" AND m.id in ( :idMateriais ) ";    
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idUsuarioBiblioteca", idUsuarioBiblioteca);
		q.setParameterList("idMateriais", idsMateriais);
		Long temp = (Long) q.uniqueResult();
		
		return temp.intValue();
	}

	
}
