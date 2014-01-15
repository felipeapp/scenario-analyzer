/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 29/12/2009
 */

package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioTituloComMaisEmprestimoDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 * MBean que controla a geração do relatório de títulos com mais empréstimos.
 * 
 * Observação: anteriormente este era o "Relatório de empréstimos para fotocópia".
 *
 * @author Bráulio
 */
@Component("relatorioTitulosComMaisEmprestimos")
@Scope("request")
public class RelatorioTitulosComMaisEmprestimos extends AbstractRelatorioBibliotecaMBean {
	
	/**
	 * A página do relatório.
	 */
	private static final String PAGINA_DO_RELATORIO = "/biblioteca/controle_estatistico/relatorioTitulosComMaisEmprestimos.jsp";

	/** Lista dos títulos e a quantidade de empréstimos feitos. */
	private SortedSet<Linha> resultados;
	
	/** Total de empréstimos. */
	private int totalEmprestimos;
	
	/** Total de títulos emprestados. */
	private int totalTitulos;
	
	/** Total de materiais emprestados. */
	private int totalMateriais;
	
	/** Total de empréstimos por material emprestado. */
	private double totalMediaEmprestimos;
	
	/** O número máximo de materiais retornados. */
	private static final int LIMITE = 250;

	/**
	 * Representa uma linha do relatório.
	 */
	public static class Linha implements Comparable<Linha> {

		/**
		 * O Título mostrado ao usuário
		 */
		private String titulo;
		
		/**
		 * A quantidade de empréstimos desse título
		 */
		private int qtdEmprestimos;
		
		/**
		 * A quantidade de materiais desse título
		 */
		private int qtdMateriais;
		
		/**
		 * A média de empréstimos desse título
		 */
		private double mediaEmprestimos;
		
		/**
		 * O id do Título no cache.
		 */
		private int id;

		public Linha(String titulo, Integer qtdEmprestimos, Integer qtdMateriais, Double mediaEmprestimos, int id) {
			this.titulo = titulo;
			this.qtdEmprestimos = qtdEmprestimos;
			this.qtdMateriais = qtdMateriais;
			this.mediaEmprestimos = mediaEmprestimos;
			this.id = id;
		}

		/**
		 * Ordena primeiro por média de empréstimos (decrescente), em seguida por número de empréstimos (decrescente) e depois pelo
		 * nome do título (crescente).
		 */
		@Override
		public int compareTo( Linha that ) {
			if ( (int) (this.getMediaEmprestimos() * 100) != (int) (that.getMediaEmprestimos() * 100) )
				return ((int) (that.getMediaEmprestimos() * 100)) - ((int) (this.getMediaEmprestimos() * 100));
			else if ( this.getQtdEmprestimos() != that.getQtdEmprestimos() )
				return that.getQtdEmprestimos() - this.getQtdEmprestimos();
			else if ( this.getTitulo().compareTo( that.getTitulo() ) != 0 )
				return this.getTitulo().compareTo( that.getTitulo() );
			else
				return this.id - that.id;
		}
		
		// GETs
		public String getTitulo() { return titulo; }
		public int getQtdEmprestimos() { return qtdEmprestimos; }
		public int getQtdMateriais() { return qtdMateriais; }
		public double getMediaEmprestimos() { return mediaEmprestimos; }
		public int getId() { return id; }
	}
	
	public RelatorioTitulosComMaisEmprestimos(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * Configura o relatório.
	 * <p>Chamado pela seguinte JSP, indiretamente:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	@Override
	public void configurar() {
		setTitulo("Relatório de Títulos com mais Empréstimos");
		setDescricao(
				"Neste relatório, o usuário pode visualizar que títulos foram mais emprestados " +
				"em um certo período.");
		
		setFiltradoPorVariasBibliotecas(true);
		campoBibliotecaObrigatorio = false;
		setFiltradoPorPeriodo(true);
		inicioPeriodo = CalendarUtils.adicionaMeses(new Date(), -1);
		setFiltradoPorTipoDeEmprestimo(true);
	}

	/**
	 * Chamado por /biblioteca/controle_estatistico/formGeral.jsp
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#gerarRelatorio()
	 */
	@Override
	public String gerarRelatorio() throws DAOException {

		RelatorioTituloComMaisEmprestimoDao dao = getDAO( RelatorioTituloComMaisEmprestimoDao.class );
		configuraDaoRelatorio(dao);
		
		SortedMap<Integer, Number[]> resultadosUsandoIds = dao.findTitulosComMaisEmprestimos(
				UFRNUtils.toInteger(variasBibliotecas), this.getInicioPeriodo(), this.getFimPeriodo(), 
				this.getTipoDeEmprestimo(), LIMITE );

		resultados = new TreeSet<Linha>();

		totalEmprestimos 		= 0;
		totalTitulos     		= 0;
		totalMateriais     		= 0;
		totalMediaEmprestimos   = 0;

		for ( Map.Entry<Integer, Number[]> par : resultadosUsandoIds.entrySet() ) {
			CacheEntidadesMarc c = BibliotecaUtil.obtemDadosTituloCache(par.getKey());
			Number[] value = par.getValue();
			
			String tituloAtual = " Título Removido ";
			
			if(c != null)
				tituloAtual = c.getTitulo() + ( c.getAutor() != null ? " - " + c.getAutor() : "");
			
			resultados.add( new Linha( tituloAtual, (Integer) value[0], (Integer) value[1], (Double) value[2], c.getId() ) );

			totalEmprestimos += (Integer) value[0];
			totalTitulos     += 1;
			totalMateriais += (Integer) value[1];
		}
		
		if ( totalTitulos == 0 ) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		totalMediaEmprestimos = ((double) totalEmprestimos) / ((double) totalMateriais);

		return forward(PAGINA_DO_RELATORIO);
	}

	//// GETs e SETs ////

	public SortedSet<Linha> getResultados() { return resultados; }
	public int getTotalEmprestimos() { return totalEmprestimos; }
	public int getTotalTitulos() { return totalTitulos; }
	public int getLIMITE() {return LIMITE; }

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

	public int getTotalMateriais() {
		return totalMateriais;
	}

	public double getTotalMediaEmprestimos() {
		return totalMediaEmprestimos;
	}

}
