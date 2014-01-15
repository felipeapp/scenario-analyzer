/*
 * TestaGeracaoFormatoPCS.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.testes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 *   Classe para testar o método que gera o formato PCS de exportação para a FGV. 
 *
 * @author jadson
 * @since 01/12/2009
 * @version 1.0 criacao da classe
 *
 */
public class TestaGeracaoFormatoPCS extends TestCase {

	/**
	 *   Testa o restorno do método no caso mais simples, quando o arquivo só tem um resgistro e esse 
	 * registro tem um tamanho menor que o do bloco (2048)
	 * 
	 * 
	 * Test method for {@link br.ufrn.sigaa.biblioteca.util.BibliotecaUtil#formataArquivoBibliotegraficoExportacaoParaPCS(byte[])}.
	 * @throws IOException 
	 */
	public void testFormataArquivoBibliotegraficoExportacaoParaPCSRegistroMenorQueBloco() throws IOException {
		
		List<ByteArrayOutputStream> outputSteams = new ArrayList<ByteArrayOutputStream>();
		
		String bytesTeste = "00978nam  2200289 a 4500001001200000005001700012008004100029001001300070020001500083040001800098041001300116043001200129080001500141100002900156245013600185250001300321260004300334300002000377440003500397504002500432651005300457651003000510651003800540997000700578998001700585999008600602BU00004846920091007141818.2091007s2005    rjb    fr     000 0 por dRN0000685221  a8521903979  aBlRjFGVBbpor1 aporheng  as-bl-mg  a94 (81).341 aMaxwell, Kenneth.d1941-12aA devassa da devassa : ba Inconfidencia Mineira : Brasil-Portugal - 1750-1808 /cKenneth Robert Maxwell ; traducao de Joao Maia. -  a6. ed. -  aRio de Janeiro :bPaz e Terra,c 2005.  a317 p. :bil. - 1a(Estudos brasileiros ;vv. 22)  aInclui bibliografia. 4aBrasil -xHistoria -xConjuracao mineira,y1789. 4aMinas Gerais -xHistoria. 4aPortugal -xColonias -zAmericas.  aRN  aRN  BB BU UR  aCatbib 2.0qb05/11/2009c05/11/2009 16:36:02dRN00002.CCeANTONIA ANGELA DA SILVA";
		
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		byteArray.write(bytesTeste.getBytes());
		
		outputSteams.add(byteArray);
		
		byte[] b = BibliotecaUtil.formataArquivoBibliotegraficoExportacaoParaPCS(outputSteams);
		
		String s = new String(b);
		
		System.out.println("Dados passados ----->"+bytesTeste+"<---------");
		System.out.println("Dados recebido ----->"+s+"<---------");
		
		assertEquals(2048, b.length);
		assertEquals(2048, s.length());
	}

	
	/**
	 *   Testa o restorno do método no caso com 1 único registro mas com o tamanho maior que 2048.
	 * 
	 * 
	 * Test method for {@link br.ufrn.sigaa.biblioteca.util.BibliotecaUtil#formataArquivoBibliotegraficoExportacaoParaPCS(byte[])}.
	 * @throws IOException 
	 */
	public void testFormataArquivoBibliotegraficoExportacaoParaPCSRegistroMaiorQueBloco() throws IOException {
		
		List<ByteArrayOutputStream> outputSteams = new ArrayList<ByteArrayOutputStream>();
		
		// +- 6000 caracteres - > deve gerar 3 blocos.
		String bytesTeste = "05937ntm  2200361 a 4500001001200000008004100012040001800053080001400071100003600085245020700121260002200328300001800350500004900368502014900417504004300566520169800609520058402307520148102891520064804372650004205020650002405062650006005086650005405146650005205200653002405252653003905276653003005315653003305345654000905378700003705387740004805424856010305472RN000387560080711s2007    rnba   fr     000 0 por d  aBlRjFGVBbpor  a65.011.561 aOliveira, Idelmarcia Dantas de.14aUma perspectiva de extensao do modelo de aceitacao de tecnologia para explicar o uso de linguagens de desenvolvimento WEB :bpesquisa com desenvolvedores Python e Java/cIdelmarcia Dantas de Oliveira. -  aNatal, RN,c2007.  a93 f. :bi. -  aOrientadora: Anatalia Saraiva Martins Ramos.  aDissertacao (Mestrado) - Universidade Federal do Rio Grande do Norte. Centro de Tecnologia. Programa de Pos-Graduacao em Engenharia de Producao.  aInclui referencias, anexos, glossario.  aResumo: A difusao da Web impulsionou a disseminacao de Sistemas de Informacao (SI) baseados na Web. Para apoiar a implementacao desses sistemas, diversas tecnologias surgiram ou evoluiram com este proposito, dentre elas as linguagens de programacao. O Modelo de Aceitacao de Tecnologia - TAM (Davis, 1986) foi concebido com o intuito de avaliar a aceitacao/uso de tecnologias da informacao por seus usuarios. Varios estudos e aplicacoes diversas tem utilizado o TAM, no entanto, nao foi encontrada na literatura mencao a utilizacao de tal modelo com relacao ao uso de linguagens de programacao. Este estudo objetiva investigar que fatores influenciam o uso de linguagens de programacao utilizadas no desenvolvimento de sistemas Web por parte de seus desenvolvedores, utilizando uma extensao do TAM, proposta neste estudo. Para tanto, foi realizada uma pesquisa com desenvolvedores Web pertencentes a dois grupos do Yahoo: java-br e python-brasil, no qual foram respondidos, na integra, 26 questionarios Java e 39 Python. O questionario tinha questoes de carater geral e questoes que mediam os fatores intrinsecos e extrinsecos das linguagens de programacao, a utilidade percebida, a facilidade de uso percebida, a atitude sobre o uso e o uso da linguagem de programacao. A maioria dos respondentes eram homens, com nivel superior, idade entre 20 e 30 anos, atuando nas regioes sudeste e sul. Do ponto de vista de seus objetivos, a pesquisa foi descritiva. Em relacao a forma de abordagem, quantitativa. Para a analise de dados foram utilizadas ferramentas estatisticas, estatistica descritiva, componentes principais e analise de regressao linear multipla. Os principais resultados da pesquisa  aforam: Java e Python possuem independencia de maquina, extensibilidade, generalidade e confianca; Java e Python sao mais utilizadas por corporacoes e organizacoes internacionais do que apoiadas pelo governo ou instituicoes de ensino; ha mais programadores Java do que Python; a utilidade percebida e influenciada pela facilidade de uso percebida; a generalidade e a extensibilidade sao fatores intrinsecos as linguagens de programacao que influenciam a facilidade de uso percebida; a facilidade de uso percebida influencia a atitude em relacao ao uso da linguagem de programacao.  aAbstract: The spread of the Web boosted the dissemination of Information Systems (IS) based on the Web. In order to support the implementation of these systems, several technologies came up or evolved with this purpose, namely the programming languages. The Technology Acceptance Model ? TAM (Davis, 1986) was conceived aiming to evaluate the acceptance/use of information technologies by their users. A lot of studies and many applications have used the TAM, however, in the literature it was not found a mention of the use of such model related to the use of programming languages. This study aims to investigate which factors influence the use of programming languages on the development of Web systems by their developers, applying an extension of the TAM, proposed in this work. To do so, a research was done with Web developers in two Yahoo groups: java-br and python-brasil, where 26 Java questionnaires and 39 Python questionnaires were fully answered. The questionnaire had general questions and questions which measured intrinsic and extrinsic factors of the programming languages, the perceived usefulness, the perceived ease of use, the attitude toward the using and the programming language use. Most of the respondents were men, graduate, between 20 and 30 years old, working in the southeast and south regions. The research was descriptive in the sense of its objectives. Statistical tools, descriptive statistics, main components and linear regression analysis  awere used for the data analysis. The foremost research results were: Java and Python have machine independence, extensibility, generality and reliability; Java and Python are more used by corporations and international organizations than supported by the government or educational institutions; there are more Java programmers than Python programmers; the perceived usefulness is influenced by the perceived ease of use; the generality and the extensibility are intrinsic factors of programming languages which influence the perceived ease of use; the perceived ease of use influences the attitude toward the using of the programming language. 4aSistema de informacao -xDissertacao. 4aWeb -xDissertacao. 4aModelo de Aceitacao de Tecnologia (TAM) -xDissertacao. 4aDesenvolvedores de sistema Python -xDissertacao. 4aDesenvolvedores de sistema Java -xDissertacao. 4aInformation system. 4aTechnology Acceptance Model (TAM). 4aDevelopers of Java system 4aDevelopers of Python system. 44Web.1 aRamos, Anatalia Saraiva Martins.0 aPesquisa com desenvolvedores Pytons e Java.11uftp://ftp.ufrn.br/pub/biblioteca/ext/bdtd/IdelmarciaDO.pdfzVisualizacao do texto completo.4BDTD.";
		
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		byteArray.write(bytesTeste.getBytes());
		
		outputSteams.add(byteArray);
		
		byte[] b = BibliotecaUtil.formataArquivoBibliotegraficoExportacaoParaPCS(outputSteams);
		
		String s = new String(b);
		
		System.out.println("Dados passados ----->"+bytesTeste+"<---------");
		System.out.println("Dados recebido ----->"+s+"<---------");
		
		assertEquals(0, s.length()%2048);
	}
	
	
	/**
	 *   Prova de fogo, teste o um regitro com mais de 2048 caracteres e mais 3 registros pesquenos
	 *   num mesmo aquivo
	 * 
	 * 
	 * Test method for {@link br.ufrn.sigaa.biblioteca.util.BibliotecaUtil#formataArquivoBibliotegraficoExportacaoParaPCS(byte[])}.
	 * @throws IOException 
	 */
	public void testFormataArquivoBibliotegraficoExportacaoParaPCSVariasRegistros() throws IOException {
		
		List<ByteArrayOutputStream> outputSteams = new ArrayList<ByteArrayOutputStream>();
		
		// +- 6000 caracteres - > deve gerar 3 blocos.
		// + um de +- 1000
		// + um de +- 900
		// + um de +- 700
		String bytesTeste1 = "05937ntm  2200361 a 4500001001200000008004100012040001800053080001400071100003600085245020700121260002200328300001800350500004900368502014900417504004300566520169800609520058402307520148102891520064804372650004205020650002405062650006005086650005405146650005205200653002405252653003905276653003005315653003305345654000905378700003705387740004805424856010305472RN000387560080711s2007    rnba   fr     000 0 por d  aBlRjFGVBbpor  a65.011.561 aOliveira, Idelmarcia Dantas de.14aUma perspectiva de extensao do modelo de aceitacao de tecnologia para explicar o uso de linguagens de desenvolvimento WEB :bpesquisa com desenvolvedores Python e Java/cIdelmarcia Dantas de Oliveira. -  aNatal, RN,c2007.  a93 f. :bi. -  aOrientadora: Anatalia Saraiva Martins Ramos.  aDissertacao (Mestrado) - Universidade Federal do Rio Grande do Norte. Centro de Tecnologia. Programa de Pos-Graduacao em Engenharia de Producao.  aInclui referencias, anexos, glossario.  aResumo: A difusao da Web impulsionou a disseminacao de Sistemas de Informacao (SI) baseados na Web. Para apoiar a implementacao desses sistemas, diversas tecnologias surgiram ou evoluiram com este proposito, dentre elas as linguagens de programacao. O Modelo de Aceitacao de Tecnologia - TAM (Davis, 1986) foi concebido com o intuito de avaliar a aceitacao/uso de tecnologias da informacao por seus usuarios. Varios estudos e aplicacoes diversas tem utilizado o TAM, no entanto, nao foi encontrada na literatura mencao a utilizacao de tal modelo com relacao ao uso de linguagens de programacao. Este estudo objetiva investigar que fatores influenciam o uso de linguagens de programacao utilizadas no desenvolvimento de sistemas Web por parte de seus desenvolvedores, utilizando uma extensao do TAM, proposta neste estudo. Para tanto, foi realizada uma pesquisa com desenvolvedores Web pertencentes a dois grupos do Yahoo: java-br e python-brasil, no qual foram respondidos, na integra, 26 questionarios Java e 39 Python. O questionario tinha questoes de carater geral e questoes que mediam os fatores intrinsecos e extrinsecos das linguagens de programacao, a utilidade percebida, a facilidade de uso percebida, a atitude sobre o uso e o uso da linguagem de programacao. A maioria dos respondentes eram homens, com nivel superior, idade entre 20 e 30 anos, atuando nas regioes sudeste e sul. Do ponto de vista de seus objetivos, a pesquisa foi descritiva. Em relacao a forma de abordagem, quantitativa. Para a analise de dados foram utilizadas ferramentas estatisticas, estatistica descritiva, componentes principais e analise de regressao linear multipla. Os principais resultados da pesquisa  aforam: Java e Python possuem independencia de maquina, extensibilidade, generalidade e confianca; Java e Python sao mais utilizadas por corporacoes e organizacoes internacionais do que apoiadas pelo governo ou instituicoes de ensino; ha mais programadores Java do que Python; a utilidade percebida e influenciada pela facilidade de uso percebida; a generalidade e a extensibilidade sao fatores intrinsecos as linguagens de programacao que influenciam a facilidade de uso percebida; a facilidade de uso percebida influencia a atitude em relacao ao uso da linguagem de programacao.  aAbstract: The spread of the Web boosted the dissemination of Information Systems (IS) based on the Web. In order to support the implementation of these systems, several technologies came up or evolved with this purpose, namely the programming languages. The Technology Acceptance Model ? TAM (Davis, 1986) was conceived aiming to evaluate the acceptance/use of information technologies by their users. A lot of studies and many applications have used the TAM, however, in the literature it was not found a mention of the use of such model related to the use of programming languages. This study aims to investigate which factors influence the use of programming languages on the development of Web systems by their developers, applying an extension of the TAM, proposed in this work. To do so, a research was done with Web developers in two Yahoo groups: java-br and python-brasil, where 26 Java questionnaires and 39 Python questionnaires were fully answered. The questionnaire had general questions and questions which measured intrinsic and extrinsic factors of the programming languages, the perceived usefulness, the perceived ease of use, the attitude toward the using and the programming language use. Most of the respondents were men, graduate, between 20 and 30 years old, working in the southeast and south regions. The research was descriptive in the sense of its objectives. Statistical tools, descriptive statistics, main components and linear regression analysis  awere used for the data analysis. The foremost research results were: Java and Python have machine independence, extensibility, generality and reliability; Java and Python are more used by corporations and international organizations than supported by the government or educational institutions; there are more Java programmers than Python programmers; the perceived usefulness is influenced by the perceived ease of use; the generality and the extensibility are intrinsic factors of programming languages which influence the perceived ease of use; the perceived ease of use influences the attitude toward the using of the programming language. 4aSistema de informacao -xDissertacao. 4aWeb -xDissertacao. 4aModelo de Aceitacao de Tecnologia (TAM) -xDissertacao. 4aDesenvolvedores de sistema Python -xDissertacao. 4aDesenvolvedores de sistema Java -xDissertacao. 4aInformation system. 4aTechnology Acceptance Model (TAM). 4aDevelopers of Java system 4aDevelopers of Python system. 44Web.1 aRamos, Anatalia Saraiva Martins.0 aPesquisa com desenvolvedores Pytons e Java.11uftp://ftp.ufrn.br/pub/biblioteca/ext/bdtd/IdelmarciaDO.pdfzVisualizacao do texto completo.4BDTD.";
		String bytesTeste2 = "01004nam  2200253 a 4500001001200000005001700012008004100029001001300070020002400083040001800107080001000125100003800135245022900173250002700402260004100429300003300470440001700503500008100520650001300601700003300614997000700647998001100654999008500665UB00075200820091028183907.9091028s2009    spba          001 0dpor dRN0000685211  a8531512353 (broch.)  aBlRjFGVBbpor  a299.61 aCabloco Sete Espadasd(Espirito).10aUmbanda :ba proto-sintese cosmica : epistemologia, etica e metodo da escola da sintese /c[Cabloco Sete Espadas; Psicografado por] Yamunisiddha Arhapiagha (Mestre Espiritual da Umbanda); Ilustracao de William Jose Fuspini -  a4. ed. rev. e atual. -  aRio de Janeiro :bPensamento,c2003.  a392 p. :bil. +e1 Cartaz. - 1a(Metafisica)  aAcompanha 1 cartaz contendo a sintese magistica e cabalistica do Ombhandhum. 4aUmbanda.1 aRivas Neto, Franciscod1950-  aRN  aRN  UB  aCatbib 2.0qb05/11/2009c05/11/2009 16:24:08dRN00001.CCeJADSON JOSE DOS SANTOS";
		String bytesTeste3 = "00978nam  2200289 a 4500001001200000005001700012008004100029001001300070020001500083040001800098041001300116043001200129080001500141100002900156245013600185250001300321260004300334300002000377440003500397504002500432651005300457651003000510651003800540997000700578998001700585999008600602BU00004846920091007141818.2091007s2005    rjb    fr     000 0 por dRN0000685221  a8521903979  aBlRjFGVBbpor1 aporheng  as-bl-mg  a94 (81).341 aMaxwell, Kenneth.d1941-12aA devassa da devassa : ba Inconfidencia Mineira : Brasil-Portugal - 1750-1808 /cKenneth Robert Maxwell ; traducao de Joao Maia. -  a6. ed. -  aRio de Janeiro :bPaz e Terra,c 2005.  a317 p. :bil. - 1a(Estudos brasileiros ;vv. 22)  aInclui bibliografia. 4aBrasil -xHistoria -xConjuracao mineira,y1789. 4aMinas Gerais -xHistoria. 4aPortugal -xColonias -zAmericas.  aRN  aRN  BB BU UR  aCatbib 2.0qb05/11/2009c05/11/2009 16:36:02dRN00002.CCeANTONIA ANGELA DA SILVA";
		String bytesTeste4 = "00708nam a2200217 u 4500008004100000005001700041001001300058020002700071040001800098080000700116100002900123245010500152260003200257300001100289650001400300650003600314650003900350997000700389998000800396999008600404090922s2008    rnb    fr     000 0 por d20090922154209.6RN0000685231  a9788572733960 (broch.)  aBlRjFGVBbpor  a371 aNaschold, Angela Chuvas.10aRedes vinculares comunicativas :ba FICAI e o caminho da volta a escola /cAngela Chuvas Naschold. -  aNatal, RN :bEDUFRN,c2008.  a247 p. 4aEducacao. 4aRedes vinculares cumunicativas. 4aPanoramas politicos e pedagogicos.  aRN  aRN   aCatbib 2.0qb05/11/2009c05/11/2009 16:36:03dRN00002.CCeANTONIA ANGELA DA SILVA";
		
		ByteArrayOutputStream byteArray1 = new ByteArrayOutputStream();
		byteArray1.write(bytesTeste1.getBytes());
		
		outputSteams.add(byteArray1);
		
		ByteArrayOutputStream byteArray2 = new ByteArrayOutputStream();
		byteArray2.write(bytesTeste2.getBytes());
		
		outputSteams.add(byteArray2);
		
		ByteArrayOutputStream byteArray3 = new ByteArrayOutputStream();
		byteArray3.write(bytesTeste3.getBytes());
		
		outputSteams.add(byteArray3);
		
		ByteArrayOutputStream byteArray4 = new ByteArrayOutputStream();
		byteArray4.write(bytesTeste4.getBytes());
		
		outputSteams.add(byteArray4);
		
		byte[] b = BibliotecaUtil.formataArquivoBibliotegraficoExportacaoParaPCS(outputSteams);
		
		String s = new String(b);
		
		System.out.println("Dados passados ----->"+bytesTeste1+bytesTeste2+bytesTeste3+bytesTeste4+"<---------");
		System.out.println("Dados recebido ----->"+s+"<---------");
		
		assertEquals(0, s.length()%2048);
	}
	
}
