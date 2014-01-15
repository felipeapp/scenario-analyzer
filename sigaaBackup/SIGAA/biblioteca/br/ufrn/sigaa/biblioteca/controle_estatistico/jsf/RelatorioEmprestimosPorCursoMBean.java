/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p>MBean que gera o relat�rio de empr�stimos agrupados por curso.</p>
 * <p>Essse relat�rio � equivalente do relat�rio de emprestimos por unidade para servidores. </p>
 *
 * @author Br�ulio
 * @author Jadson
 * 
 * @see RelatorioEmprestimosPorUnidade
 */
@Component("relatorioEmprestimosPorCursoMBean")
@Scope("request")
public class RelatorioEmprestimosPorCursoMBean extends AbstractRelatorioBibliotecaMBean {

	/** P�gina do relat�rio. */
	private static final String PAGINA = "/biblioteca/controle_estatistico/relatorioEmprestimosPorCurso.jsp";
	
	/** Resultados do relat�rio. */
	private List<Object[]> resultados;
	
	public RelatorioEmprestimosPorCursoMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	@Override
	public void configurar() {

		titulo = "Relat�rio de Empr�stimos por Curso";
		
		descricao = "<p> A partir dos dados desse relat�rio � poss�vel saber a quantidade de empr�stimos realizados pelos alunos de cada cursos da institui��o." +
				" Neste relat�rio exclusivamente, s�o mostrados duas listagem separadas, um com os empr�stimos outra para renova��es. </p> "
		        +" <p> <strong>Observa��o</strong>: Como o relat�rio � por curso, o filtro \"Categoria de Usu�rio\" s� retorna resultado para categorias de discente. </p>";
		
		filtradoPorVariasBibliotecas = true;
		filtradoPorTipoDeEmprestimo = true;
		filtradoPorTipoDeMaterial = true;
		filtradoPorCategoriaDeUsuario = true;
		setCampoBibliotecaObrigatorio(false); // se n�o selecionar a biblioteca traz todas por padr�o
		filtradoPorPeriodo = true;
		
		inicioPeriodo = CalendarUtils.adicionaMeses(inicioPeriodo, -6); // por padr�o os �ltimos 6 meses

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
				
				// Adiciona a quantidade de renova��es ao empr�timos recuperados //
				if(resultados.size() > 0)
				for (Object[] resultadoE : resultados) {
					if(resultadosRenovacoes.size() > 0)
					for (Object[] resultadoR : resultadosRenovacoes) {
						if( ((Integer) resultadoE[2]).equals( resultadoR[2]) ){ // O mesmo curso
							resultadoE[3] = resultadoR[1];                      // Recebe a quantidade de renova��es para mostrar em una �nica listagem
							break;                                              // se j� achou o curso, n�o precisa mais percurrer o restante da listagem.
						}
					} 
					
				}
				
				// Adiciona a quantidade de renova��es para cursos que n�o tiveram empr�stimos //
				for (Object[] resultadoR : resultadosRenovacoes) {
					
					boolean cursoPossuiEmprestimos = false;
					
					if(resultados.size() > 0)
					for (Object[] resultadoE : resultados) {
						if( ((Integer) resultadoE[2]).equals( resultadoR[2]) ){ // A mesma unidade
							cursoPossuiEmprestimos = true;
							break;
						}
					}
					
					if(!cursoPossuiEmprestimos){ // Se no per�odo seleciona houve renova��es para alguma unidade mas n�o houve empr�stimos
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
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos1ComboBox()
	 */
	@Override
	public Collection<SelectItem> getAgrupamentos1ComboBox() {
		return null;
	}


	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos2ComboBox()
	 */
	@Override
	public Collection<SelectItem> getAgrupamentos2ComboBox() {
		return null;
	}

}
