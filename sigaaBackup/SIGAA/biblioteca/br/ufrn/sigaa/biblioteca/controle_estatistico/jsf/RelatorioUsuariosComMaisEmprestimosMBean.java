/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 13/11/2009
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioUsuariosComMaisEmprestimosDao;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;

/**
 * MBean responsável pela geração do relatório dos usuários da biblioteca
 * que mais fizeram empréstimos.
 *
 * @author Bráulio Bezerra
 * @since 13/11/2009
 * @version 1.0 Criação e implementação
 */
@Component("relatorioDeUsuariosComMaisEmprestimos")
@Scope("request")
public class RelatorioUsuariosComMaisEmprestimosMBean extends AbstractRelatorioBibliotecaMBean {
	
	/** Máximo de usuários retornados pelo relatório. */
	private static final int MAX_USUARIOS = 50;
	
	/** A página JSF do relatório. */
	private static final String PAGINA = "/biblioteca/controle_estatistico/relatorioUsuariosComMaisEmprestimos.jsp";
	
	/** Os resultados. Para cada item, [0] = nome do usuário, [1] = total de empréstimos,
	 * [2] = matrícula ou siape, [3] curso ou unidade */
	public List< Object[] > resultados;

	public RelatorioUsuariosComMaisEmprestimosMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	@Override
	public void configurar() {
		titulo = "Relatório de Usuários com mais Empréstimos";
		descricao = "Neste Relatório são mostrados os <strong>"+MAX_USUARIOS+"</strong>  usuários que realizaram mais empréstimos no sistema dentro do período escolhido.";

		filtradoPorVariasBibliotecas = true;
		filtradoPorCategoriaDeUsuario = true;
		filtradoPorCurso = true;
		filtradoPorPeriodo = true;
		filtradoPorTipoDeEmprestimo = true;
		filtradoPorTipoDeMaterial = true;
		
		setCampoBibliotecaObrigatorio(false); // se o usuário não selcionar, recupera todas
		
		inicioPeriodo = CalendarUtils.adicionarAnos(inicioPeriodo, -1); // Por padrão recupera de 1 anos para cá.
		
	}
	
	@Override
	public String gerarRelatorio() throws DAOException {
		
		RelatorioUsuariosComMaisEmprestimosDao dao = null;
		
		try{
			
			dao = getDAO(RelatorioUsuariosComMaisEmprestimosDao.class);
			
			VinculoUsuarioBiblioteca vinculo =  VinculoUsuarioBiblioteca.getVinculo(valorVinculoDoUsuarioSelecionado);
			
			if(vinculo == VinculoUsuarioBiblioteca.INATIVO)
				vinculo = null;
			
			resultados = dao.findUsuariosComMaisEmprestimosERenovacoes(
					MAX_USUARIOS, vinculo, UFRNUtils.toInteger(variasBibliotecas), curso,
					inicioPeriodo, fimPeriodo, tipoDeEmprestimo, tipoDeMaterial );
	
			if ( resultados.isEmpty() ) {
				addMensagem( MensagensArquitetura.BUSCA_SEM_RESULTADOS );
				return null;
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		return forward( PAGINA );
	}
	
	//// GETs e SETs

	public List<Object[]> getResultados() {
		return resultados;
	}

	public void setResultados(List<Object[]> resultados) {
		this.resultados = resultados;
	}

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
