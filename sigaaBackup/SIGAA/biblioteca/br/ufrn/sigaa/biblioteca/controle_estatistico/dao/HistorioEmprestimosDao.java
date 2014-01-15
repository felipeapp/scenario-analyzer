/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 20/04/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PoliticaEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 *
 * <p>Dao exclusivo para a emiss�o do hist�rico de empr�stimos </p>
 
 * 
 * @author jadson
 *
 */
public class HistorioEmprestimosDao extends GenericSigaaDAO{

	/**
	 * <p>Retorna todos os empr�stimos das contasd dos usu�rios bibliotecas passados, normalmente essas contas pertencem a uma mesma pessoa ou biblioteca</p>
	 * 
	 * <p>Esse m�todo � utilizado para gerar o hist�rio de empr�stimos de um usu�rio, retorna empr�stimos ativos porque no hist�rico n�o � mostrados os empr�stimos estornados.
	 * Empr�stimos devolvidos s�o considerados ativos no sistema, s� n�o est�o mais ativos quando s�o estornados. </p>
	 * 
	 * @param usuarioBiblioteca
	 * @param materialInformacional
	 * @param biblioteca
	 * @param devolvidos
	 * @param podeRenovar
	 * @param atrasados
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 * @throws DAOException
	 * 
	 */
	public List <Emprestimo> findEmprestimosAtivosByUsuarios(List<UsuarioBiblioteca> contasUsuarioBiblioteca, Date dataInicio, Date dataFim) throws DAOException{

		String projecao = " emp.id, emp.dataEmprestimo, emp.prazo, emp.dataDevolucao, emp.material.id, emp.material.numeroChamada, emp.material.segundaLocalizacao, emp.politicaEmprestimo.tipoPrazo, emp.politicaEmprestimo.tipoEmprestimo.id, emp.politicaEmprestimo.tipoEmprestimo.descricao ";
		
		int idPessoa = -1;
		int idBiblioteca = -1;
		
		List<Integer> idsUsuariosBibliotecas = new ArrayList<Integer>();
		
		if( contasUsuarioBiblioteca == null || contasUsuarioBiblioteca.size() == 0)
			return new ArrayList<Emprestimo>();
		
		
		for (UsuarioBiblioteca ub : contasUsuarioBiblioteca) {
			
			idsUsuariosBibliotecas.add(ub.getId());
			
			if( ub.getPessoa()  != null){
				if( idPessoa  == -1)
					idPessoa  = ub.getPessoa().getId();
				else{
					if(idPessoa  != ub.getPessoa().getId()){ // contas de diferentes pessoas 
						throw new java.lang.IllegalArgumentException(" O hist�rio s� pode ser emitido para uma mesma pessoa.");
					}
				}
				
			}
				
			if( ub.getBiblioteca() != null){
				if( idBiblioteca  == -1)
					idBiblioteca  = ub.getBiblioteca().getId();
				else{
					if(idBiblioteca  != ub.getBiblioteca().getId()){ // contas de diferentes bibliotecas 
						throw new java.lang.IllegalArgumentException(" O hist�rio s� pode ser emitido para uma mesma bibliotecas.");
					}
				}
			}	
			
		}
		
		StringBuilder hql = new StringBuilder(" SELECT "+projecao
			+ " FROM Emprestimo emp "
			+ " WHERE emp.usuarioBiblioteca.id in "+UFRNUtils.gerarStringIn(idsUsuariosBibliotecas) );
			
		if (dataInicio != null)
			hql.append(" AND emp.dataEmprestimo >= :dataInicio");
			
		if (dataFim != null)
			hql.append(" AND emp.dataEmprestimo <= :dataFim");
		
		hql.append(" AND emp.ativo = :true " );  // Emprestimos n�o estornados
		hql.append(" ORDER BY emp.dataEmprestimo DESC ");
		
		Query q = getSession().createQuery(hql.toString());
		if (dataInicio != null)
			q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(dataInicio, 0, 0, 0, 0) );
		if (dataFim != null)
			q.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(dataFim, 23, 59, 59, 999));
		
		
		q.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List<Object[]> linhas = q.list();
		
		List<Emprestimo> emprestimos = new ArrayList<Emprestimo>();
		
		// N�o � poss�vel utilizar HibernateUtils.parseTo porque ele n�o sabe como instanciar a heran�a de material informacional
		for (Object[] objects : linhas) {
			
			Emprestimo emp = new Emprestimo();
			emp.setId( (Integer) objects[0]);
			emp.setDataEmprestimo((Date) objects[1]);
			emp.setPrazo((Date) objects[2]);
			emp.setDataDevolucao( (Date) objects[3] );
			emp.setMaterial( new Exemplar((Integer) objects[4]));
			
			emp.setDataRenovacao(BibliotecaUtil.getDataUltimaRenovacao(emp));
			emp.setQuantidadeRenovacoes(BibliotecaUtil.getQuantidadeRenovacoesEmprestimo(emp));
			emp.getMaterial().setInformacao( BibliotecaUtil.obtemDadosMaterialInformacional( emp.getMaterial().getId() ));
			
			emp.getMaterial().setInformacao(emp.getMaterial().getInformacao()+ ( StringUtils.notEmpty((String) objects[5] ) ? " .  Localiza��o: "+(String) objects[5] :  " " )+ ( StringUtils.notEmpty((String) objects[6]) ? " . Segunda Localiza��o: "+(String) objects[6] : " "));
			
			PoliticaEmprestimo p = new PoliticaEmprestimo();
			p.setTipoPrazo( (Short) objects[7] );
			p.setTipoEmprestimo( new TipoEmprestimo((Integer) objects[8], (String) objects[9]));
			emp.setPoliticaEmprestimo( p );
			
			
			
			emprestimos.add(emp);
		}
		
		
		return emprestimos;
	
	}
	
}
