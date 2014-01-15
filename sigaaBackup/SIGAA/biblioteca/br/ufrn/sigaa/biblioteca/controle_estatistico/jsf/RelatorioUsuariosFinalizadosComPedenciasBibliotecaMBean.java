/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 16/04/2013
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioUsuariosFinalizadosComPedenciasBibliotecaDao;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.OrdenacaoRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;

/**
 * <p>Cria um relatório para a biblioteca poder visualizar os usuários que foram finalizados com pedência de material informacional.</p>
 *
 * <p>Esse usuários são proprícios a cobrança judicial caso não devolvam o material. </p>
 *
 * <p>Isso é necessário porque o cancelamento de vínculos não será mais bloqueado, haja visto que isso seria uma vantagem para o usuário.
 * Pegar livros na biblioteca para não ter seu vínculo cancelado na universidade.
 * </p>
 *
 * @author jadson - jadson@info.ufrn.br
 * @vesion 1.0 - criação da classe.
 * @since 16/04/2013
 *
 */
@Component("relatorioUsuariosFinalizadosComPedenciasBibliotecaMBean")
@Scope("request")
public class RelatorioUsuariosFinalizadosComPedenciasBibliotecaMBean  extends AbstractRelatorioBibliotecaMBean{

	/**
	 * A página do relatório propriamente dito, onde os dados são mostrados
	 */
	public static final String PAGINA_RELATORIO = "/biblioteca/controle_estatistico/relatorioUsuariosFinalizadosComPendenciasBiblioteca.jsp";
	
	
	/** Resultado da consulta de discentes */
	private List<Object[]> resultadosDiscente;
	
	/** Resultado da consulta de servidores */
	private List<Object[]> resultadosServidor;
	
	/** Resultado da consulta de biblioteca */
	private List<Object[]> resultadosBiblioteca;
	
	/**
	 * Construtor obrigatório para que usa o abstract relatório da biblioteca.
	 */
	public RelatorioUsuariosFinalizadosComPedenciasBibliotecaMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	
	/**
	 * Configura os filtros do relatório.
	 */
	@Override
	public void configurar() {
		setTitulo("Relatório de Usuários Finalizados com Pendências na Biblioteca");
		
		String instituicao = RepositorioDadosInstitucionais.getSiglaInstituicao();
		
		setDescricao("<p> Nesta página é possível emitir um relatório analítico de usuários que foram finalizados com pendências na biblioteca. </p>"+
				" <br/>"+
				" <p> O sistema bloqueia a conclusão de alunos com pendências na biblioteca porém, o cancelamento do vínculo não pode ser bloqueado, pois isso geraria uma vantagem " +
				"para o aluno inadimplente. Bastaria o discente tomar emprestado um material da biblioteca e não devolver para ficar eternamente com um vínculo ativo na "+instituicao+".</p>"+
				"<br/>"+
				"<p>Nesses casos a responsabilidade ficar a cargo da biblioteca de tomar as providências legais cabíveis.</p>"+
				"<br/>"
				);
		
		setFiltradoPorVariasBibliotecas(true);
		setFiltradoPorVariasCategoriasDeUsuario(true);
		setCampoBibliotecaObrigatorio(false);
		setFiltradoPorOrdenacao(true);
		setOrdenacao( OrdenacaoRelatoriosBiblioteca.ORDENADO_POR_NOME.getValor() );
	}
	
	

	/**
	 * Realiza a consulta do relatório.
	 */
	@Override
	public String gerarRelatorio() throws DAOException, SegurancaException {
		
		RelatorioUsuariosFinalizadosComPedenciasBibliotecaDao dao = getDAO(RelatorioUsuariosFinalizadosComPedenciasBibliotecaDao.class);
		configuraDaoRelatorio(dao);
		
		resultadosDiscente = new ArrayList<Object[]>();
		resultadosServidor = new ArrayList<Object[]>();;
		resultadosBiblioteca = new ArrayList<Object[]>();;
		
		resultadosDiscente = dao.findDiscenteFinalizadosComPendenciaNaBiblioteca( UFRNUtils.toInteger(variasBibliotecas), UFRNUtils.toInteger(variasCategoriasDeUsuario), OrdenacaoRelatoriosBiblioteca.getOrdenacao(ordenacao) );
		resultadosServidor = dao.findServidoresFinalizadosComPendenciaNaBiblioteca( UFRNUtils.toInteger(variasBibliotecas), UFRNUtils.toInteger(variasCategoriasDeUsuario), OrdenacaoRelatoriosBiblioteca.getOrdenacao(ordenacao));
		resultadosBiblioteca = dao.findBibliotecasFinalizadosComPendenciaNaBiblioteca( UFRNUtils.toInteger(variasBibliotecas), UFRNUtils.toInteger(variasCategoriasDeUsuario), OrdenacaoRelatoriosBiblioteca.getOrdenacao(ordenacao));
		
		if (getTotalResultados() == 0) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		
		// troca os valores dos vínculos dos usuários pela descrição teta de "n"
		for (Object[] resultado : resultadosDiscente) {
			resultado[1] = VinculoUsuarioBiblioteca.getVinculo( (Integer)resultado[1] ).getDescricao();
		}
		for (Object[] resultado : resultadosServidor) {
			resultado[1] = VinculoUsuarioBiblioteca.getVinculo( (Integer)resultado[1] ).getDescricao();
		}
		for (Object[] resultado : resultadosBiblioteca) {
			resultado[1] = VinculoUsuarioBiblioteca.getVinculo( (Integer)resultado[1] ).getDescricao();
		}
		
		return forward(PAGINA_RELATORIO);
	}
	
	
	public int getTotalResultados() {
		return resultadosDiscente.size()+resultadosServidor.size()+resultadosBiblioteca.size();
	}
	

	@Override
	public Collection<SelectItem> getOrdenacaoComboBox() {
		ordenacaoItens = new ArrayList<SelectItem>();
		ordenacaoItens.add( new SelectItem(OrdenacaoRelatoriosBiblioteca.ORDENADO_POR_NOME.getValor(), OrdenacaoRelatoriosBiblioteca.ORDENADO_POR_NOME.getAlias()));
		ordenacaoItens.add( new SelectItem(OrdenacaoRelatoriosBiblioteca.ORDENADO_POR_PRAZO.getValor(), OrdenacaoRelatoriosBiblioteca.ORDENADO_POR_PRAZO.getAlias()));
		return ordenacaoItens;
	}


	
	public List<Object[]> getResultadosDiscente() {	return resultadosDiscente;}
	public void setResultadosDiscente(List<Object[]> resultadosDiscente) { this.resultadosDiscente = resultadosDiscente;}
	public List<Object[]> getResultadosServidor() {return resultadosServidor;}
	public void setResultadosServidor(List<Object[]> resultadosServidor) {this.resultadosServidor = resultadosServidor;}
	public List<Object[]> getResultadosBiblioteca() {return resultadosBiblioteca;}
	public void setResultadosBiblioteca(List<Object[]> resultadosBiblioteca) {this.resultadosBiblioteca = resultadosBiblioteca;}



}
