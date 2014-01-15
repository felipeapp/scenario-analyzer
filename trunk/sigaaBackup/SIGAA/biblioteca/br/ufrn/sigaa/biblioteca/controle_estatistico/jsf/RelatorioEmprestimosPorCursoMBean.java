/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 11/11/2010
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioEmprestimosPorCursoDao;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;

/**
 * <p>MBean que gera o relatório de empréstimos agrupados por curso.</p>
 * <p>Essse relatório é equivalente do relatório de emprestimos por unidade para servidores. </p>
 *
 * @author Bráulio
 * @author Jadson
 * 
 * @see RelatorioEmprestimosPorUnidade
 */
@Component("relatorioEmprestimosPorCursoMBean")
@Scope("request")
public class RelatorioEmprestimosPorCursoMBean extends AbstractRelatorioBibliotecaMBean {

	/** Página do relatório. */
	private static final String PAGINA = "/biblioteca/controle_estatistico/relatorioEmprestimosPorCurso.jsp";
	
	/** Resultados do relatório. */
	private List<Object[]> resultados;
	
	public RelatorioEmprestimosPorCursoMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	@Override
	public void configurar() {

		titulo = "Relatório de Empréstimos por Curso";
		
		descricao = "<p> A partir dos dados desse relatório é possível saber a quantidade de empréstimos realizados pelos alunos de cada cursos da instituição." +
				" Neste relatório exclusivamente, são mostrados duas listagem separadas, um com os empréstimos outra para renovações. </p> "
		        +" <p> <strong>Observação</strong>: Como o relatório é por curso, o filtro \"Categoria de Usuário\" só retorna resultado para categorias de discente. </p>";
		
		filtradoPorVariasBibliotecas = true;
		filtradoPorTipoDeEmprestimo = true;
		filtradoPorTipoDeMaterial = true;
		filtradoPorCategoriaDeUsuario = true;
		setCampoBibliotecaObrigatorio(false); // se não selecionar a biblioteca traz todas por padrão
		filtradoPorPeriodo = true;
		
		inicioPeriodo = CalendarUtils.adicionaMeses(inicioPeriodo, -6); // por padrão os últimos 6 meses

	}

	@Override
	public String gerarRelatorio() throws DAOException, SegurancaException {

		RelatorioEmprestimosPorCursoDao dao = null;
		
		try{
			
		
			dao  = getDAO(RelatorioEmprestimosPorCursoDao.class);
		
			VinculoUsuarioBiblioteca vinculo =  VinculoUsuarioBiblioteca.getVinculo(valorVinculoDoUsuarioSelecionado);
			
			if(vinculo == VinculoUsuarioBiblioteca.INATIVO)
				vinculo = null;
			
			resultados = dao.findQtdEmprestimosByCurso( UFRNUtils.toInteger(variasBibliotecas),
					inicioPeriodo, fimPeriodo, tipoDeEmprestimo, tipoDeMaterial,  vinculo);
			
			List<Object[]> resultadosRenovacoes = dao.findQtdRenovacoesByCurso( UFRNUtils.toInteger(variasBibliotecas),
					inicioPeriodo, fimPeriodo, tipoDeEmprestimo, tipoDeMaterial,  vinculo);
			
			
			
			if ( resultados.isEmpty() && resultadosRenovacoes.isEmpty() ) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}else{
				
				// Adiciona a quantidade de renovações ao emprétimos recuperados //
				if(resultados.size() > 0)
				for (Object[] resultadoE : resultados) {
					if(resultadosRenovacoes.size() > 0)
					for (Object[] resultadoR : resultadosRenovacoes) {
						if( ((Integer) resultadoE[2]).equals( resultadoR[2]) ){ // O mesmo curso
							resultadoE[3] = resultadoR[1];                      // Recebe a quantidade de renovações para mostrar em una única listagem
							break;                                              // se já achou o curso, não precisa mais percurrer o restante da listagem.
						}
					} 
					
				}
				
				// Adiciona a quantidade de renovações para cursos que não tiveram empréstimos //
				for (Object[] resultadoR : resultadosRenovacoes) {
					
					boolean cursoPossuiEmprestimos = false;
					
					if(resultados.size() > 0)
					for (Object[] resultadoE : resultados) {
						if( ((Integer) resultadoE[2]).equals( resultadoR[2]) ){ // A mesma unidade
							cursoPossuiEmprestimos = true;
							break;
						}
					}
					
					if(!cursoPossuiEmprestimos){ // Se no período seleciona houve renovações para alguma unidade mas não houve empréstimos
						resultados.add( new Object[]{resultadoR[0], 0, resultadoR[2], resultadoR[1]});
					}
					
				}
				
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
		return forward(PAGINA);
	}
	
	
	//// GETs e SETs
	
	

	public List<Object[]> getResultados() { return resultados; }
	
	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos1ComboBox()
	 */
	@Override
	public Collection<SelectItem> getAgrupamentos1ComboBox() {
		return null;
	}


	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos2ComboBox()
	 */
	@Override
	public Collection<SelectItem> getAgrupamentos2ComboBox() {
		return null;
	}

}
