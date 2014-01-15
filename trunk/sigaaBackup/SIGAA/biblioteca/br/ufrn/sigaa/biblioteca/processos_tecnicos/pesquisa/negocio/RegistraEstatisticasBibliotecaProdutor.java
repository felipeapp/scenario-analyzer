package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.negocio;

import java.util.Queue;

import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.RegistroEstatisticasConsultaAcervo;


/**
 * <p>Produtor que registra na fila a operação feita, o consumidor vai ler dessa fila e salvar no banco os registros. </p> 
 * 
 * 
 * @author jadson
 *
 */
public class RegistraEstatisticasBibliotecaProdutor implements Runnable{
	
	/** Fila dos registros das estatisticas do sistema, vai existir 1 fila por servidor da aplicação */
	private  Queue<RegistroEstatisticasConsultaAcervo> filaRegistros;
	
	/** Guarda o registro para manipular em uma nova thread */
	private RegistroEstatisticasConsultaAcervo registro;
	
	public RegistraEstatisticasBibliotecaProdutor (Queue<RegistroEstatisticasConsultaAcervo> filaRegistros, RegistroEstatisticasConsultaAcervo registro){
		this.registro = registro;
		this.filaRegistros = filaRegistros;
	}
	
	
	public void run() {
		
		long initialTime = System.currentTimeMillis();
		
		synchronized (filaRegistros) { // Adiciona um registro na fila e acorda o consumidor para realizar o registros
			
			filaRegistros.add(registro);
			filaRegistros.notifyAll();    // notifica os produtos e cosumidores que estavam esperando
		}
		
		System.out.println(">>>>>>>>  Execução do Produtor das Estatística da Biblioteca Demorou: "+(System.currentTimeMillis()-initialTime)+" ms");
	}
	
}
