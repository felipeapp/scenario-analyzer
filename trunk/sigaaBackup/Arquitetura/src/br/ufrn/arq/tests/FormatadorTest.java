/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 23/05/2008
 */
package br.ufrn.arq.tests;

import junit.framework.TestCase;
import br.ufrn.arq.util.Formatador;

/** 
 * JUnit para testes da classe {@link Formatador}
 * 
 * @author Édipo Elder
 * @author Gleydson Lima
 *
 */
public class FormatadorTest extends TestCase {

	public void testFormataNomePessoa() {
		assertEquals("Aane Daniele da Silva Pessoa", Formatador.getInstance().formataNomePessoa("AANE DANIELE DA SILVA PESSOA"));
		assertEquals("Abel Bezerra Júnior", Formatador.getInstance().formataNomePessoa("ABEL BEZERRA JÚNIOR"));
		assertEquals("Abel Cícero Belarmino Amorim de Freitas Neto", Formatador.getInstance().formataNomePessoa("ABEL CÍCERO BELARMINO AMORIM DE FREITAS NETO"));
		assertEquals("Abel Lameque Silva Damasceno", Formatador.getInstance().formataNomePessoa("ABEL LAMEQUE SILVA DAMASCENO"));
		assertEquals("Abel Lima Bisneto", Formatador.getInstance().formataNomePessoa("ABEL LIMA BISNETO"));
		assertEquals("Ábia Oliveira de Souza", Formatador.getInstance().formataNomePessoa("ÁBIA OLIVEIRA DE SOUZA"));
		assertEquals("Abigail Noádia Barbalho da Silva", Formatador.getInstance().formataNomePessoa("ABIGAIL NOÁDIA BARBALHO DA SILVA"));
		assertEquals("Abílio Junior de Medeiros", Formatador.getInstance().formataNomePessoa("ABÍLIO JUNIOR DE MEDEIROS"));
		assertEquals("Abimael Esdras Carvalho de Moura Lira", Formatador.getInstance().formataNomePessoa("ABIMAEL ESDRAS CARVALHO DE MOURA LIRA"));
		assertEquals("Abimaelle Silva Chibério", Formatador.getInstance().formataNomePessoa("ABIMAELLE SILVA CHIBÉRIO"));
		assertEquals("Abimax Marcelino da Silva", Formatador.getInstance().formataNomePessoa("ABIMAX MARCELINO DA SILVA"));
		assertEquals("Abinoan de Carvalho", Formatador.getInstance().formataNomePessoa("ABINOAN DE CARVALHO"));
		assertEquals("Abnele de Queiroz Ramalho", Formatador.getInstance().formataNomePessoa("ABNELE DE QUEIROZ RAMALHO"));
		assertEquals("Ábner Alves Moreira", Formatador.getInstance().formataNomePessoa("ÁBNER ALVES MOREIRA"));
		assertEquals("Abner Carlos Costa de Melo", Formatador.getInstance().formataNomePessoa("ABNER CARLOS COSTA DE MELO"));
		assertEquals("Abner Paulo de Araujo", Formatador.getInstance().formataNomePessoa("ABNER PAULO DE ARAUJO"));
		assertEquals("Abner Pinto de Aguiar Junior", Formatador.getInstance().formataNomePessoa("ABNER PINTO DE AGUIAR JUNIOR"));
		assertEquals("Abraão Azevedo Lopes Segundo", Formatador.getInstance().formataNomePessoa("ABRAÃO AZEVEDO LOPES SEGUNDO"));
		assertEquals("Abraão Bernardo Coelho da Silva", Formatador.getInstance().formataNomePessoa("ABRAÃO BERNARDO COELHO DA SILVA"));
		assertEquals("Abraão Bruno Lima de Moura", Formatador.getInstance().formataNomePessoa("ABRAÃO BRUNO LIMA DE MOURA"));
		assertEquals("Abraão Ferreira Soares dos Santos", Formatador.getInstance().formataNomePessoa("ABRAÃO FERREIRA SOARES DOS SANTOS"));
		assertEquals("Abraão José Azevedo dos Santos", Formatador.getInstance().formataNomePessoa("ABRAÃO JOSÉ AZEVEDO DOS SANTOS"));
		assertEquals("Abraão Lima de Sousa", Formatador.getInstance().formataNomePessoa("ABRAÃO LIMA DE SOUSA"));
		assertEquals("Abraão Lucas Ferreira Guimarães", Formatador.getInstance().formataNomePessoa("ABRAÃO LUCAS FERREIRA GUIMARÃES"));
		assertEquals("Abraão Lucas Ferreira Guinarães", Formatador.getInstance().formataNomePessoa("ABRAÃO LUCAS FERREIRA GUINARÃES"));
		assertEquals("Abrahão Henrique Fernandes", Formatador.getInstance().formataNomePessoa("ABRAHÃO HENRIQUE FERNANDES"));
		assertEquals("Absalão de Paiva Fonseca", Formatador.getInstance().formataNomePessoa("ABSALÃO DE PAIVA FONSECA"));
		assertEquals("Abson Isaque Santos de Albuquerque", Formatador.getInstance().formataNomePessoa("ABSON ISAQUE SANTOS DE ALBUQUERQUE"));
		assertEquals("Abylene Melo da Silva", Formatador.getInstance().formataNomePessoa("ABYLENE MELO DA SILVA"));
		assertEquals("Acácia Mikaela Santos de Farias", Formatador.getInstance().formataNomePessoa("ACÁCIA MIKAELA SANTOS DE FARIAS"));
		assertEquals("Acácia Pierre dos Santos Medeiros", Formatador.getInstance().formataNomePessoa("ACÁCIA PIERRE DOS SANTOS MEDEIROS"));
		assertEquals("Acácio Emanuel de Oliveira Barbosa", Formatador.getInstance().formataNomePessoa("ACÁCIO EMANUEL DE OLIVEIRA BARBOSA"));
		assertEquals("Acácio Lopes Borges de Araújo", Formatador.getInstance().formataNomePessoa("ACÁCIO LOPES BORGES DE ARAÚJO"));
		assertEquals("Acácio Soares Júnior", Formatador.getInstance().formataNomePessoa("ACÁCIO SOARES JÚNIOR"));
		assertEquals("Acácio Trindade da Silva", Formatador.getInstance().formataNomePessoa("ACÁCIO TRINDADE DA SILVA"));
		assertEquals("Açaí Marques do Nascimento", Formatador.getInstance().formataNomePessoa("AÇAÍ MARQUES DO NASCIMENTO"));
		assertEquals("Acássio dos Anjos Araújo", Formatador.getInstance().formataNomePessoa("ACÁSSIO DOS ANJOS ARAÚJO"));
		assertEquals("Acherlin Serafim da Silva", Formatador.getInstance().formataNomePessoa("ACHERLIN SERAFIM DA SILVA"));
		assertEquals("Aciene Leite de Souza", Formatador.getInstance().formataNomePessoa("ACIENE LEITE DE SOUZA"));
		assertEquals("Acsa Sunamita Bertulino da Silva", Formatador.getInstance().formataNomePessoa("ACSA SUNAMITA BERTULINO DA SILVA"));
		assertEquals("Acynelly Dafne da Silva Nunes", Formatador.getInstance().formataNomePessoa("ACYNELLY DAFNE DA SILVA NUNES"));
		assertEquals("Adaiany Vieira da Costa", Formatador.getInstance().formataNomePessoa("ADAIANY VIEIRA DA COSTA"));
		assertEquals("Adailton Barbosa da Costa Junior", Formatador.getInstance().formataNomePessoa("ADAILTON BARBOSA DA COSTA JUNIOR"));
		assertEquals("Adailton de Souza Pereira", Formatador.getInstance().formataNomePessoa("ADAILTON DE SOUZA PEREIRA"));
		assertEquals("Adailton José Mendes de Azevêdo", Formatador.getInstance().formataNomePessoa("ADAILTON JOSÉ MENDES DE AZEVÊDO"));
		assertEquals("Adailton Lopes Monteiro", Formatador.getInstance().formataNomePessoa("ADAILTON LOPES MONTEIRO"));
		assertEquals("Adailton Ramos de Abreu", Formatador.getInstance().formataNomePessoa("ADAILTON RAMOS DE ABREU"));
		assertEquals("Adailton Rodrigues Bezerra", Formatador.getInstance().formataNomePessoa("ADAILTON RODRIGUES BEZERRA"));
		assertEquals("Adailza Denize da Silva", Formatador.getInstance().formataNomePessoa("ADAILZA DENIZE DA SILVA"));
		assertEquals("Adaína Maria da Silva", Formatador.getInstance().formataNomePessoa("ADAÍNA MARIA DA SILVA"));
		assertEquals("Adaise Leite Maia Brasil", Formatador.getInstance().formataNomePessoa("ADAISE LEITE MAIA BRASIL"));
	}
}
