/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 07/01/2008
 */
package br.ufrn.sigaa.processamento.tests;

import junit.framework.TestCase;
import br.ufrn.sigaa.processamento.dominio.MatriculaEmProcessamento;

/**
 * Testes unitários para a classe MatriculaEmProcessamento 
 * 
 * @author David Pereira
 *
 */
public class MatriculaEmProcessamentoTest extends TestCase {

	private MatriculaEmProcessamento matriculaAdiantado8 = new MatriculaEmProcessamento(MatriculaEmProcessamento.ADIANTADO, 8.0);
	private MatriculaEmProcessamento matriculaAdiantado9 = new MatriculaEmProcessamento(MatriculaEmProcessamento.ADIANTADO, 9.0);
	private MatriculaEmProcessamento matriculaEletivo8 = new MatriculaEmProcessamento(MatriculaEmProcessamento.ELETIVO, 8.0);
	private MatriculaEmProcessamento matriculaEletivo9 = new MatriculaEmProcessamento(MatriculaEmProcessamento.ELETIVO, 9.0);
	private MatriculaEmProcessamento matriculaFormando8 = new MatriculaEmProcessamento(MatriculaEmProcessamento.FORMANDO, 8.0);
	private MatriculaEmProcessamento matriculaFormando9 = new MatriculaEmProcessamento(MatriculaEmProcessamento.FORMANDO, 9.0);
	private MatriculaEmProcessamento matriculaNivelado8 = new MatriculaEmProcessamento(MatriculaEmProcessamento.NIVELADO, 8.0);
	private MatriculaEmProcessamento matriculaNivelado9 = new MatriculaEmProcessamento(MatriculaEmProcessamento.NIVELADO, 9.0);
	private MatriculaEmProcessamento matriculaOutros8 = new MatriculaEmProcessamento(MatriculaEmProcessamento.OUTROS, 8.0);
	private MatriculaEmProcessamento matriculaOutros9 = new MatriculaEmProcessamento(MatriculaEmProcessamento.OUTROS, 9.0);
	private MatriculaEmProcessamento matriculaRecuperacao8 = new MatriculaEmProcessamento(MatriculaEmProcessamento.RECUPERACAO, 8.0);
	private MatriculaEmProcessamento matriculaRecuperacao9 = new MatriculaEmProcessamento(MatriculaEmProcessamento.RECUPERACAO, 9.0);
	private MatriculaEmProcessamento matriculaVestibular8 = new MatriculaEmProcessamento(MatriculaEmProcessamento.VESTIBULAR, 8.0);
	private MatriculaEmProcessamento matriculaVestibular9 = new MatriculaEmProcessamento(MatriculaEmProcessamento.VESTIBULAR, 9.0);
	
    public void testCompareToAdiantadosIguais() throws Throwable {
    	assertEquals(-1, matriculaAdiantado8.compareTo(matriculaAdiantado9));
    	assertEquals(1, matriculaAdiantado9.compareTo(matriculaAdiantado8));
    }
    
    public void testCompareToEletivosIguais() throws Throwable {
    	assertEquals(-1, matriculaEletivo8.compareTo(matriculaEletivo9));
    	assertEquals(1, matriculaEletivo9.compareTo(matriculaEletivo8));
    }
    
    public void testCompareToFormandosIguais() throws Throwable {
    	assertEquals(-1, matriculaFormando8.compareTo(matriculaFormando9));
    	assertEquals(1, matriculaFormando9.compareTo(matriculaFormando8));
    }
    
    public void testCompareToNiveladoIguais() throws Throwable {
    	assertEquals(-1, matriculaNivelado8.compareTo(matriculaNivelado9));
    	assertEquals(1, matriculaNivelado9.compareTo(matriculaNivelado8));
    }
    
    public void testCompareToOutrosIguais() throws Throwable {
    	assertEquals(-1, matriculaOutros8.compareTo(matriculaOutros9));
    	assertEquals(1, matriculaOutros9.compareTo(matriculaOutros8));
    }
    
    public void testCompareToRecuperacoesIguais() throws Throwable {
    	assertEquals(-1, matriculaRecuperacao8.compareTo(matriculaRecuperacao9));
    	assertEquals(1, matriculaRecuperacao9.compareTo(matriculaRecuperacao8));
    }
    
    public void testCompareToVestibularesIguais() throws Throwable {
    	assertEquals(-1, matriculaVestibular8.compareTo(matriculaVestibular9));
    	assertEquals(1, matriculaVestibular9.compareTo(matriculaVestibular8));
    }
    
    public void testCompareToPrioridadeVestibular() {
    	assertEquals(1, matriculaVestibular8.compareTo(matriculaAdiantado8));
    	assertEquals(1, matriculaVestibular8.compareTo(matriculaEletivo8));
    	assertEquals(1, matriculaVestibular8.compareTo(matriculaFormando8));
    	assertEquals(1, matriculaVestibular8.compareTo(matriculaNivelado8));
    	assertEquals(1, matriculaVestibular8.compareTo(matriculaOutros8));
    	assertEquals(1, matriculaVestibular8.compareTo(matriculaRecuperacao8));
    	
    	assertEquals(1, matriculaVestibular8.compareTo(matriculaAdiantado9));
    	assertEquals(1, matriculaVestibular8.compareTo(matriculaEletivo9));
    	assertEquals(1, matriculaVestibular8.compareTo(matriculaFormando9));
    	assertEquals(1, matriculaVestibular8.compareTo(matriculaNivelado9));
    	assertEquals(1, matriculaVestibular8.compareTo(matriculaOutros9));
    	assertEquals(1, matriculaVestibular8.compareTo(matriculaRecuperacao9));
    	
    	assertEquals(1, matriculaVestibular9.compareTo(matriculaAdiantado8));
    	assertEquals(1, matriculaVestibular9.compareTo(matriculaEletivo8));
    	assertEquals(1, matriculaVestibular9.compareTo(matriculaFormando8));
    	assertEquals(1, matriculaVestibular9.compareTo(matriculaNivelado8));
    	assertEquals(1, matriculaVestibular9.compareTo(matriculaOutros8));
    	assertEquals(1, matriculaVestibular9.compareTo(matriculaRecuperacao8));
    	
    	assertEquals(1, matriculaVestibular9.compareTo(matriculaAdiantado9));
    	assertEquals(1, matriculaVestibular9.compareTo(matriculaEletivo9));
    	assertEquals(1, matriculaVestibular9.compareTo(matriculaFormando9));
    	assertEquals(1, matriculaVestibular9.compareTo(matriculaNivelado9));
    	assertEquals(1, matriculaVestibular9.compareTo(matriculaOutros9));
    	assertEquals(1, matriculaVestibular9.compareTo(matriculaRecuperacao9));
    }
    
    public void testCompareToPrioridadeNivelado() {
    	assertEquals(1, matriculaNivelado8.compareTo(matriculaAdiantado8));
    	assertEquals(1, matriculaNivelado8.compareTo(matriculaEletivo8));
    	assertEquals(1, matriculaNivelado8.compareTo(matriculaFormando8));
    	assertEquals(-1, matriculaNivelado8.compareTo(matriculaVestibular8));
    	assertEquals(1, matriculaNivelado8.compareTo(matriculaOutros8));
    	assertEquals(1, matriculaNivelado8.compareTo(matriculaRecuperacao8));
    	
    	assertEquals(1, matriculaNivelado8.compareTo(matriculaAdiantado9));
    	assertEquals(1, matriculaNivelado8.compareTo(matriculaEletivo9));
    	assertEquals(1, matriculaNivelado8.compareTo(matriculaFormando9));
    	assertEquals(-1, matriculaNivelado8.compareTo(matriculaVestibular9));
    	assertEquals(1, matriculaNivelado8.compareTo(matriculaOutros9));
    	assertEquals(1, matriculaNivelado8.compareTo(matriculaRecuperacao9));
    	
    	assertEquals(1, matriculaNivelado9.compareTo(matriculaAdiantado8));
    	assertEquals(1, matriculaNivelado9.compareTo(matriculaEletivo8));
    	assertEquals(1, matriculaNivelado9.compareTo(matriculaFormando8));
    	assertEquals(-1, matriculaNivelado9.compareTo(matriculaVestibular8));
    	assertEquals(1, matriculaNivelado9.compareTo(matriculaOutros8));
    	assertEquals(1, matriculaNivelado9.compareTo(matriculaRecuperacao8));
    	
    	assertEquals(1, matriculaNivelado9.compareTo(matriculaAdiantado9));
    	assertEquals(1, matriculaNivelado9.compareTo(matriculaEletivo9));
    	assertEquals(1, matriculaNivelado9.compareTo(matriculaFormando9));
    	assertEquals(-1, matriculaNivelado9.compareTo(matriculaVestibular9));
    	assertEquals(1, matriculaNivelado9.compareTo(matriculaOutros9));
    	assertEquals(1, matriculaNivelado9.compareTo(matriculaRecuperacao9));
    }
    
    public void testCompareToPrioridadeFormando() {
    	assertEquals(1, matriculaFormando8.compareTo(matriculaAdiantado8));
    	assertEquals(1, matriculaFormando8.compareTo(matriculaEletivo8));
    	assertEquals(-1, matriculaFormando8.compareTo(matriculaNivelado8));
    	assertEquals(-1, matriculaFormando8.compareTo(matriculaVestibular8));
    	assertEquals(1, matriculaFormando8.compareTo(matriculaOutros8));
    	assertEquals(1, matriculaFormando8.compareTo(matriculaRecuperacao8));
    	
    	assertEquals(1, matriculaFormando8.compareTo(matriculaAdiantado9));
    	assertEquals(1, matriculaFormando8.compareTo(matriculaEletivo9));
    	assertEquals(-1, matriculaFormando8.compareTo(matriculaNivelado9));
    	assertEquals(-1, matriculaFormando8.compareTo(matriculaVestibular9));
    	assertEquals(1, matriculaFormando8.compareTo(matriculaOutros9));
    	assertEquals(1, matriculaFormando8.compareTo(matriculaRecuperacao9));
    	
    	assertEquals(1, matriculaFormando9.compareTo(matriculaAdiantado8));
    	assertEquals(1, matriculaFormando9.compareTo(matriculaEletivo8));
    	assertEquals(-1, matriculaFormando9.compareTo(matriculaNivelado8));
    	assertEquals(-1, matriculaFormando9.compareTo(matriculaVestibular8));
    	assertEquals(1, matriculaFormando9.compareTo(matriculaOutros8));
    	assertEquals(1, matriculaFormando9.compareTo(matriculaRecuperacao8));
    	
    	assertEquals(1, matriculaFormando9.compareTo(matriculaAdiantado9));
    	assertEquals(1, matriculaFormando9.compareTo(matriculaEletivo9));
    	assertEquals(-1, matriculaFormando9.compareTo(matriculaNivelado9));
    	assertEquals(-1, matriculaFormando9.compareTo(matriculaVestibular9));
    	assertEquals(1, matriculaFormando9.compareTo(matriculaOutros9));
    	assertEquals(1, matriculaFormando9.compareTo(matriculaRecuperacao9));
    }
    
    public void testCompareToPrioridadeRecuperacao() {
    	assertEquals(1, matriculaRecuperacao8.compareTo(matriculaAdiantado8));
    	assertEquals(1, matriculaRecuperacao8.compareTo(matriculaEletivo8));
    	assertEquals(-1, matriculaRecuperacao8.compareTo(matriculaFormando8));
    	assertEquals(-1, matriculaRecuperacao8.compareTo(matriculaVestibular8));
    	assertEquals(1, matriculaRecuperacao8.compareTo(matriculaOutros8));
    	assertEquals(-1, matriculaRecuperacao8.compareTo(matriculaNivelado8));
    	
    	assertEquals(1, matriculaRecuperacao8.compareTo(matriculaAdiantado9));
    	assertEquals(1, matriculaRecuperacao8.compareTo(matriculaEletivo9));
    	assertEquals(-1, matriculaRecuperacao8.compareTo(matriculaFormando9));
    	assertEquals(-1, matriculaRecuperacao8.compareTo(matriculaVestibular9));
    	assertEquals(1, matriculaRecuperacao8.compareTo(matriculaOutros9));
    	assertEquals(-1, matriculaRecuperacao8.compareTo(matriculaNivelado9));
    	
    	assertEquals(1, matriculaRecuperacao9.compareTo(matriculaAdiantado8));
    	assertEquals(1, matriculaRecuperacao9.compareTo(matriculaEletivo8));
    	assertEquals(-1, matriculaRecuperacao9.compareTo(matriculaFormando8));
    	assertEquals(-1, matriculaRecuperacao9.compareTo(matriculaVestibular8));
    	assertEquals(1, matriculaRecuperacao9.compareTo(matriculaOutros8));
    	assertEquals(-1, matriculaRecuperacao9.compareTo(matriculaNivelado8));
    	
    	assertEquals(1, matriculaRecuperacao9.compareTo(matriculaAdiantado9));
    	assertEquals(1, matriculaRecuperacao9.compareTo(matriculaEletivo9));
    	assertEquals(-1, matriculaRecuperacao9.compareTo(matriculaFormando9));
    	assertEquals(-1, matriculaRecuperacao9.compareTo(matriculaVestibular9));
    	assertEquals(1, matriculaRecuperacao9.compareTo(matriculaOutros9));
    	assertEquals(-1, matriculaRecuperacao9.compareTo(matriculaNivelado9));
    }
    
    public void testCompareToPrioridadeAdiantado() {
    	assertEquals(-1, matriculaAdiantado8.compareTo(matriculaNivelado8));
    	assertEquals(1, matriculaAdiantado8.compareTo(matriculaEletivo8));
    	assertEquals(-1, matriculaAdiantado8.compareTo(matriculaFormando8));
    	assertEquals(-1, matriculaAdiantado8.compareTo(matriculaVestibular8));
    	assertEquals(1, matriculaAdiantado8.compareTo(matriculaOutros8));
    	assertEquals(-1, matriculaAdiantado8.compareTo(matriculaRecuperacao8));
    	
    	assertEquals(-1, matriculaAdiantado8.compareTo(matriculaNivelado9));
    	assertEquals(1, matriculaAdiantado8.compareTo(matriculaEletivo9));
    	assertEquals(-1, matriculaAdiantado8.compareTo(matriculaFormando9));
    	assertEquals(-1, matriculaAdiantado8.compareTo(matriculaVestibular9));
    	assertEquals(1, matriculaAdiantado8.compareTo(matriculaOutros9));
    	assertEquals(-1, matriculaAdiantado8.compareTo(matriculaRecuperacao9));
    	
    	assertEquals(-1, matriculaAdiantado9.compareTo(matriculaNivelado8));
    	assertEquals(1, matriculaAdiantado9.compareTo(matriculaEletivo8));
    	assertEquals(-1, matriculaAdiantado9.compareTo(matriculaFormando8));
    	assertEquals(-1, matriculaAdiantado9.compareTo(matriculaVestibular8));
    	assertEquals(1, matriculaAdiantado9.compareTo(matriculaOutros8));
    	assertEquals(-1, matriculaAdiantado9.compareTo(matriculaRecuperacao8));
    	
    	assertEquals(-1, matriculaAdiantado9.compareTo(matriculaNivelado9));
    	assertEquals(1, matriculaAdiantado9.compareTo(matriculaEletivo9));
    	assertEquals(-1, matriculaAdiantado9.compareTo(matriculaFormando9));
    	assertEquals(-1, matriculaAdiantado9.compareTo(matriculaVestibular9));
    	assertEquals(1, matriculaAdiantado9.compareTo(matriculaOutros9));
    	assertEquals(-1, matriculaAdiantado9.compareTo(matriculaRecuperacao9));
    }
    
    public void testCompareToPrioridadeEletivo() {
    	assertEquals(-1, matriculaEletivo8.compareTo(matriculaAdiantado8));
    	assertEquals(-1, matriculaEletivo8.compareTo(matriculaNivelado8));
    	assertEquals(-1, matriculaEletivo8.compareTo(matriculaFormando8));
    	assertEquals(-1, matriculaEletivo8.compareTo(matriculaVestibular8));
    	assertEquals(1, matriculaEletivo8.compareTo(matriculaOutros8));
    	assertEquals(-1, matriculaEletivo8.compareTo(matriculaRecuperacao8));
    	
    	assertEquals(-1, matriculaEletivo8.compareTo(matriculaAdiantado9));
    	assertEquals(-1, matriculaEletivo8.compareTo(matriculaNivelado9));
    	assertEquals(-1, matriculaEletivo8.compareTo(matriculaFormando9));
    	assertEquals(-1, matriculaEletivo8.compareTo(matriculaVestibular9));
    	assertEquals(1, matriculaEletivo8.compareTo(matriculaOutros9));
    	assertEquals(-1, matriculaEletivo8.compareTo(matriculaRecuperacao9));
    	
    	assertEquals(-1, matriculaEletivo9.compareTo(matriculaAdiantado8));
    	assertEquals(-1, matriculaEletivo9.compareTo(matriculaNivelado8));
    	assertEquals(-1, matriculaEletivo9.compareTo(matriculaFormando8));
    	assertEquals(-1, matriculaEletivo9.compareTo(matriculaVestibular8));
    	assertEquals(1, matriculaEletivo9.compareTo(matriculaOutros8));
    	assertEquals(-1, matriculaEletivo9.compareTo(matriculaRecuperacao8));
    	
    	assertEquals(-1, matriculaEletivo9.compareTo(matriculaAdiantado9));
    	assertEquals(-1, matriculaEletivo9.compareTo(matriculaNivelado9));
    	assertEquals(-1, matriculaEletivo9.compareTo(matriculaFormando9));
    	assertEquals(-1, matriculaEletivo9.compareTo(matriculaVestibular9));
    	assertEquals(1, matriculaEletivo9.compareTo(matriculaOutros9));
    	assertEquals(-1, matriculaEletivo9.compareTo(matriculaRecuperacao9));
    }
    
    public void testCompareToPrioridadeOutros() {
    	assertEquals(-1, matriculaOutros8.compareTo(matriculaAdiantado8));
    	assertEquals(-1, matriculaOutros8.compareTo(matriculaEletivo8));
    	assertEquals(-1, matriculaOutros8.compareTo(matriculaFormando8));
    	assertEquals(-1, matriculaOutros8.compareTo(matriculaVestibular8));
    	assertEquals(-1, matriculaOutros8.compareTo(matriculaNivelado8));
    	assertEquals(-1, matriculaOutros8.compareTo(matriculaRecuperacao8));
    	
    	assertEquals(-1, matriculaOutros8.compareTo(matriculaAdiantado9));
    	assertEquals(-1, matriculaOutros8.compareTo(matriculaEletivo9));
    	assertEquals(-1, matriculaOutros8.compareTo(matriculaFormando9));
    	assertEquals(-1, matriculaOutros8.compareTo(matriculaVestibular9));
    	assertEquals(-1, matriculaOutros8.compareTo(matriculaNivelado9));
    	assertEquals(-1, matriculaOutros8.compareTo(matriculaRecuperacao9));
    	
    	assertEquals(-1, matriculaOutros9.compareTo(matriculaAdiantado8));
    	assertEquals(-1, matriculaOutros9.compareTo(matriculaEletivo8));
    	assertEquals(-1, matriculaOutros9.compareTo(matriculaFormando8));
    	assertEquals(-1, matriculaOutros9.compareTo(matriculaVestibular8));
    	assertEquals(-1, matriculaOutros9.compareTo(matriculaNivelado8));
    	assertEquals(-1, matriculaOutros9.compareTo(matriculaRecuperacao8));
    	
    	assertEquals(-1, matriculaOutros9.compareTo(matriculaAdiantado9));
    	assertEquals(-1, matriculaOutros9.compareTo(matriculaEletivo9));
    	assertEquals(-1, matriculaOutros9.compareTo(matriculaFormando9));
    	assertEquals(-1, matriculaOutros9.compareTo(matriculaVestibular9));
    	assertEquals(-1, matriculaOutros9.compareTo(matriculaNivelado9));
    	assertEquals(-1, matriculaOutros9.compareTo(matriculaRecuperacao9));
    }
    
        
}
