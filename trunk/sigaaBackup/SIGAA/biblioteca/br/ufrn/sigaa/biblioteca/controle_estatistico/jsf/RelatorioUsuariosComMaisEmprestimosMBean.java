/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * MBean respons�vel pela gera��o do relat�rio dos usu�rios da biblioteca
 * que mais fizeram empr�stimos.
 *
 * @author Br�ulio Bezerra
 * @since 13/11/2009
 * @version 1.0 Cria��o e implementa��o
 */
@Component("relatorioDeUsuariosComMaisEmprestimos")
@Scope("request")
public class RelatorioUsuariosComMaisEmprestimosMBean extends AbstractRelatorioBibliotecaMBean {
	
	/** M�ximo de usu�rios retornados pelo relat�rio. */
	private static final int MAX_USUARIOS = 50;
	
	/** A p�gina JSF do relat�rio. */
	private static final String PAGINA = "/biblioteca/controle_estatistico/relatorioUsuariosComMaisEmprestimos.jsp";
	
	/** Os resultados. Para cada item, [0] = nome do usu�rio, [1] = total de empr�stimos,
	 * [2] = matr�cula ou siape, [3] curso ou unidade */
	public List< Object[] > resultados;

	public RelatorioUsuariosComMaisEmprestimosMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	@Override
	public void configurar() {
		titulo = "Relat�rio de Usu�rios com mais Empr�stimos";
		descricao = "Neste Relat�rio s�o mostrados os <strong>"+MAX_USUARIOS+"</strong>  usu�rios que realizaram mais empr�stimos no sistema dentro do per�odo escolhido.";

		filtradoPorVariasBibliotecas = true;
		filtradoPorCategoriaDeUsuario = true;
		filtradoPorCurso = true;
		filtradoPorPeriodo = true;
		filtradoPorTipoDeEmprestimo = true;
		filtradoPorTipoDeMaterial = true;
		
		setCampoBibliotecaObrigatorio(false); // se o usu�rio n�o selcionar, recupera todas
		
		inicioPeriodo = CalendarUtils.adicionarAnos(inicioPeriodo, -1); // Por padr�o recupera de 1 anos para c�.
		
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
