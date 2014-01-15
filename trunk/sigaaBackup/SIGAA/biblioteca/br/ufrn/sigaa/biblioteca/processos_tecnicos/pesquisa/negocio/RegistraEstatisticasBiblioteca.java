/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 27/03/2013
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.negocio;

import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.RegistroEstatisticasConsultaAcervo;

/**
 * <p>Singleton que inicia o registro de de estatisticas da biblioteca.</p>
 * 
 * <p>Nenhuma tarefa pesada pode ficar aqui, porque executa sincronizado com o código do sistema e pode comprometer o desempenho. 
 * Tudo que é pesado é iniciado em thread separadas.</p>
 *
 * @author jadson - jadson@info.ufrn.br
 * @vesion 1.0 - criação da classe.
 * @since 27/03/2013
 *
 */
public class RegistraEstatisticasBiblioteca {

	/** Implementa o padrão singleton para essa classe. Evita ficar instancioando várias thread no sistema. Vai ter 1 única thread adicional por servidor. */
	private static RegistraEstatisticasBiblioteca singleton;
	
	/** Fila dos registros das estatisticas do sistema, vai existir 1 fila por servidor da aplicação */
	private static Queue<RegistroEstatisticasConsultaAcervo> filaRegistros;
	
	/** Cria um pool de thread para o produtor  */
	private static ExecutorService executorService;
	
	
	/** Construtor para iniciar a fila e o consumidor apenas 1 vez.*/
	private RegistraEstatisticasBiblioteca() {
		// Só inicia 1 vez essa fila
		filaRegistros = new ConcurrentLinkedQueue<RegistroEstatisticasConsultaAcervo>();
		
		// cria um pool limitado de thread uma única vez, para impedir a poliferação desordenada de threads. //
		executorService = Executors.newFixedThreadPool(10);
		
	}
	
	/**
	 * Retorna a mesma instância do produtor
	 * @return
	 */
	public static synchronized RegistraEstatisticasBiblioteca getInstance() {
		if (singleton == null) 
			singleton = new RegistraEstatisticasBiblioteca();
		
		return singleton;
	}
	
	
	/**
	 * Adiciona um registro de consulta à fila de registros
	 * 
	 * @param mov
	 */
	public void registrarTitulosConsultados(List<CacheEntidadesMarc> titulosConsultados) {

		long initialTime = System.currentTimeMillis();
		
		try {

			RegistroEstatisticasConsultaAcervo registro = new RegistroEstatisticasConsultaAcervo(titulosConsultados,  new Date());
			
			// executa o produtor e consumidor em uma nova thread para registrar a consulta e não impactar na performance do sistema //
			executorService.execute(new RegistraEstatisticasBibliotecaProdutor(filaRegistros, registro));
			executorService.execute(new RegistraEstatisticasBibliotecaConsumidor(filaRegistros));

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(">>>>>>>>  Registrar Títulos Consultados Demorou: "+(System.currentTimeMillis()-initialTime)+" ms");
	}
	
	
	/**
	 * Adiciona um registro de visualização à fila de registros
	 * 
	 * @param mov
	 */
	public void registrarTituloVisualizado(int idTituloVisualizado) {
		
		long initialTime = System.currentTimeMillis();
	
		try {

			RegistroEstatisticasConsultaAcervo registro = new RegistroEstatisticasConsultaAcervo(idTituloVisualizado, new Date());
		
			// executa o produtor e consumidor em uma nova thread para registrar a consulta e não impactar na performance do sistema //
			executorService.execute(new RegistraEstatisticasBibliotecaProdutor(filaRegistros, registro));
			executorService.execute(new RegistraEstatisticasBibliotecaConsumidor(filaRegistros));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(">>>>>>>>  Registrar Títulos Visualizados Demorou: "+(System.currentTimeMillis()-initialTime)+" ms");
	}
	
	
	
	/**
	 * Adiciona um registro de empréstimos à fila de registros
	 * 
	 * @param mov
	 */
	public void registrarTitulosEmprestados(List<Integer> idsMateriaisEmprestados) {
		
		long initialTime = System.currentTimeMillis();
		
		try {
			
			RegistroEstatisticasConsultaAcervo registro = new RegistroEstatisticasConsultaAcervo( new Date(), idsMateriaisEmprestados);
		
			// executa o produtor e consumidor em uma nova thread para registrar a consulta e não impactar na performance do sistema //
			executorService.execute(new RegistraEstatisticasBibliotecaProdutor(filaRegistros, registro));
			executorService.execute(new RegistraEstatisticasBibliotecaConsumidor(filaRegistros));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(">>>>>>>>  Registrar Títulos Emprestados Demorou: "+(System.currentTimeMillis()-initialTime)+" ms");
	}
}
