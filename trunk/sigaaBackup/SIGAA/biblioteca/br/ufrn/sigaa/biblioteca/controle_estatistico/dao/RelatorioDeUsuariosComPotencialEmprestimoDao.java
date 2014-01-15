/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 27/04/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dao.biblioteca.ConsultasEmprestimoDao;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaFactory;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaStrategy;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;

/**
 * <p>Dao exclusivo para a consulta para o relatório de usuários com potencial de empréstimo </p>
 * 
 * @author jadson
 *
 */
public class RelatorioDeUsuariosComPotencialEmprestimoDao extends GenericSigaaDAO implements RelatorioDeUsuariosComPotencialEmprestimo{

	/**
	 * Retorna, para cada tipo de usuário definido em <tt>ConstantesBiblioteca</tt>,
	 * a quantidade de usuários que têm a possibilidade de fazer empréstimos
	 * nas bibliotecas do sistema. Isso inclui os usuários que ainda não fizerem nenhum
	 * empréstimo ou mesmo que ainda não se cadastraram para utilizar os
	 * serviços das bibliotecas.
	 * 
	 * @param vinculoUsuario Conta os usuários de apenas um tipo
	 * @return um <em>map</em> com os tipos de usuário e sua quantidades
	 */
	public Map<String, Integer> findQtdUsuariosComPotencialDeEmprestimo( VinculoUsuarioBiblioteca vinculoUsuario ) throws DAOException {
		
		Map<String, Integer> resultados = new HashMap<String, Integer>();
		
		ObtemVinculoUsuarioBibliotecaStrategy estrategiaUtilizada = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo();
		
		String hqlAlunoInfantil =
			" SELECT count(discente.id) " +
			" FROM Discente AS discente " +
			" LEFT JOIN discente.formaIngresso AS forma " +
			" WHERE discente.nivel IN "+UFRNUtils.gerarStringIn( estrategiaUtilizada.getNiveisAlunosInfantilBiblioteca() )
			+ ConsultasEmprestimoDao.RESTRICOES_CONSULTA_DISCENTE_BIBLIOTECA;
		
		String hqlAlunoTecnicoMedio =
			" SELECT count(discente.id) " +
			" FROM Discente AS discente " +
			" LEFT JOIN discente.formaIngresso AS forma " +
			" WHERE discente.nivel IN "+UFRNUtils.gerarStringIn( estrategiaUtilizada.getNiveisAlunosMedioTecnicoBiblioteca() )
			+ ConsultasEmprestimoDao.RESTRICOES_CONSULTA_DISCENTE_BIBLIOTECA;
		
		String hqlAlunoGraduacao =
				" SELECT count(discente.id) " +
				" FROM Discente AS discente " +
				" LEFT JOIN discente.formaIngresso AS forma " +
				" WHERE discente.nivel IN "+UFRNUtils.gerarStringIn( estrategiaUtilizada.getNiveisAlunosGraduacaoBiblioteca())
				+ ConsultasEmprestimoDao.RESTRICOES_CONSULTA_DISCENTE_BIBLIOTECA;
		
		String hqlAlunoPosGraduacao =
				" SELECT count(discente.id) " +
				" FROM Discente AS discente " +
				" LEFT JOIN discente.formaIngresso AS forma " +
				" WHERE discente.nivel IN "
				+ UFRNUtils.gerarStringIn( estrategiaUtilizada.getNiveisAlunosPosGraduacaoBiblioteca() ) // Residentes são considerados alunos de pós agora 13/06/2011
				+ ConsultasEmprestimoDao.RESTRICOES_CONSULTA_DISCENTE_BIBLIOTECA;  
		
		String hqlServidor =
				" SELECT count(servidor.id) " +
				" FROM Servidor AS servidor " +
				" WHERE servidor.categoria.id IN  "+ UFRNUtils.gerarStringIn( estrategiaUtilizada.getCategoriaServidorBiblioteca() )
				+ ConsultasEmprestimoDao.RESTRICOES_CONSULTA_SERVIDOR_BIBLIOTECA; 

		String hqlDocente =
			" SELECT count(servidor.id) " +
			" FROM Servidor AS servidor " +
			" WHERE servidor.categoria.id IN  "+ UFRNUtils.gerarStringIn( estrategiaUtilizada.getCategoriaDocenteBiblioteca() )
			+ ConsultasEmprestimoDao.RESTRICOES_CONSULTA_SERVIDOR_BIBLIOTECA; 
		
		String hqlDocenteExterno =
			" SELECT count(docente.id) " +
			" FROM DocenteExterno AS docente " +
			" WHERE 1=1 "+ConsultasEmprestimoDao.RESTRICOES_CONSULTA_DOCENTE_EXTERNO_BIBLIOTECA;
			
		String hqlUsuarioExterno =
				" SELECT count(ube.id) " +
				" FROM UsuarioExternoBiblioteca ube " +
				" WHERE 1=1 " +ConsultasEmprestimoDao.RESTRICOES_CONSULTA_USUARIOEXTERNO_BIBLIOTECA;
		
		String hqlBiblioteca =
			"SELECT count(biblioteca.id) " +
			"FROM Biblioteca AS biblioteca " +
			"WHERE biblioteca.ativo = trueValue()	AND biblioteca.unidade IS NOT NULL ";
	
		String hqlBibliotecaExterna =
			"SELECT count(biblioteca.id) " +
			"FROM Biblioteca AS biblioteca " +
			"WHERE biblioteca.ativo = trueValue() AND biblioteca.unidade IS NULL ";
		
		
		//// Execução das consultas, se não por passado o vínculo, executa todas as consultas //
		
		if ( vinculoUsuario == VinculoUsuarioBiblioteca.ALUNO_INFANTIL || vinculoUsuario == null )
			resultados.put(VinculoUsuarioBiblioteca.ALUNO_INFANTIL.getDescricao(), ((Long) getSession().createQuery(hqlAlunoInfantil).uniqueResult()).intValue() );
		
		if ( vinculoUsuario == VinculoUsuarioBiblioteca.ALUNO_TECNICO_MEDIO || vinculoUsuario == null )
			resultados.put(VinculoUsuarioBiblioteca.ALUNO_TECNICO_MEDIO.getDescricao(), ((Long) getSession().createQuery(hqlAlunoTecnicoMedio).uniqueResult()).intValue() );
		
		if ( vinculoUsuario == VinculoUsuarioBiblioteca.ALUNO_GRADUACAO || vinculoUsuario == null )
			resultados.put(VinculoUsuarioBiblioteca.ALUNO_GRADUACAO.getDescricao(), ((Long) getSession().createQuery(hqlAlunoGraduacao).uniqueResult()).intValue() );
		
		if ( vinculoUsuario == VinculoUsuarioBiblioteca.ALUNO_POS_GRADUCACAO || vinculoUsuario == null  )
			resultados.put(VinculoUsuarioBiblioteca.ALUNO_POS_GRADUCACAO.getDescricao(),((Long) getSession().createQuery(hqlAlunoPosGraduacao).uniqueResult()).intValue() );
		
		if ( vinculoUsuario == VinculoUsuarioBiblioteca.SERVIDOR_TECNO_ADMINISTRATIVO || vinculoUsuario == null  )
			resultados.put(VinculoUsuarioBiblioteca.SERVIDOR_TECNO_ADMINISTRATIVO.getDescricao(),((Long) getSession().createQuery(hqlServidor).uniqueResult()).intValue() );
		
		if ( vinculoUsuario == VinculoUsuarioBiblioteca.DOCENTE || vinculoUsuario == null  )
			resultados.put(VinculoUsuarioBiblioteca.DOCENTE.getDescricao(), ((Long) getSession().createQuery(hqlDocente).uniqueResult()).intValue() );
		
		if ( vinculoUsuario == VinculoUsuarioBiblioteca.BIBLIOTECA || vinculoUsuario == null  )
			resultados.put(VinculoUsuarioBiblioteca.BIBLIOTECA.getDescricao(), ((Long) getSession().createQuery(hqlBiblioteca).uniqueResult()).intValue() );
		
		if ( vinculoUsuario == VinculoUsuarioBiblioteca.BIBLIOTECA_EXTERNA || vinculoUsuario == null  )
			resultados.put(VinculoUsuarioBiblioteca.BIBLIOTECA_EXTERNA.getDescricao(), ((Long) getSession().createQuery(hqlBibliotecaExterna).uniqueResult()).intValue() );
		
		if ( vinculoUsuario == VinculoUsuarioBiblioteca.USUARIO_EXTERNO || vinculoUsuario == null  ){
			Query q  = getSession().createQuery(hqlUsuarioExterno);
			q.setDate("hoje", new Date());
			resultados.put(VinculoUsuarioBiblioteca.USUARIO_EXTERNO.getDescricao(), ((Long) q.uniqueResult()).intValue() );
		
		}
			
		if ( vinculoUsuario == VinculoUsuarioBiblioteca.DOCENTE_EXTERNO || vinculoUsuario == null  ){
			// Verificar se o prazo adicional de acesso também foi excedido
			Calendar cal = Calendar.getInstance();
			cal.setTime(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
			cal.add(Calendar.DAY_OF_MONTH, -ParametroHelper.getInstance().getParametroInt(ParametrosGerais.PERIODO_ACESSO_ADICIONAL_DOCENTE_EXTERNO)-1);
			Date dataAtual = cal.getTime();
			
			Query q = getSession().createQuery(hqlDocenteExterno);
			q.setTimestamp("dataAtual", dataAtual);
			
			resultados.put(VinculoUsuarioBiblioteca.DOCENTE_EXTERNO.getDescricao(), ((Long) q.uniqueResult()).intValue() );
		
		}	
			
		return resultados;
	}
	
}
