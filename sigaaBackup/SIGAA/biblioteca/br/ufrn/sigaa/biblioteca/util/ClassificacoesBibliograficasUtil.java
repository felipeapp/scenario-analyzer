/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 16/02/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.ClassificacaoBibliograficaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoDados;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CamposMarcClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica.OrdemClassificacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.RelacionaClassificacaoBibliograficaAreaCNPq;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.SubCampo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TipoCatalogacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

/**
 *
 * <p>Classe auxiliar com m�todos utilit�rios para trabalhar com as classifica��es bibliogr�ficas 
 * utilizadas no sistema. </p>
 *
 * <p>Essa classe tamb�m mant�m em cache na mem�ria quais classifica��es o sistema est� utilizando 
 * e a descri��o dessas classifica��es para exibi��o do usu�rio. � preciso manter em cache para n�o realizar
 *  novas buscas sempre que essa informa��es necessitar ser acessada.</p>
 *
 * <p> <i> Essa classe substitui a classe ConfiguraClassificacoesBibliograficasImpl que continha 
 * regras fixas para as classifica��es e classes desses classifica��es utilizadas na UFRN.
 * </i> </p>
 * 
 * @author jadson
 * @see ClassificacaoBibliografica
 */
public class ClassificacoesBibliograficasUtil {

	/**
	 * A tempo em que o cache das informa��es classifica��o vai ser atualizado.
	 */
	private static Date lastRefreshTime;
	
	/**
	 * Tempo EM SEGUNDOS que o sistema vai atualizar as informa��es das classifica��es no banco ( A cada 30 mim )
	 */
	private static final int REFRESH_TIME_CACHE_DIGITAIS = 1800; 
	
	/** Vari�vel que informa que o sistema est� utilizando a primeira classifica��o */
	private static boolean isSistemaUtilizandoClassificacao1;
	/** Vari�vel que informa que o sistema est� utilizando a segunda classifica��o */
	private static boolean isSistemaUtilizandoClassificacao2;
	/** Vari�vel que informa que o sistema est� utilizando a terceira classifica��o */
	private static boolean isSistemaUtilizandoClassificacao3;
	
	/** Retorna a descri��o da primeira  classifica��o utilizada no sistema */
	private static String descricaoClassificacao1 = "";
	/** Retorna a descri��o da segunda  classifica��o utilizada no sistema */
	private static String descricaoClassificacao2 = "";
	/** Retorna a descri��o da terceira  classifica��o utilizada no sistema */
	private static String descricaoClassificacao3 = "";
	
	/** As classes principais da classifica��o 1 */
	private  static List<String> classesPincipaisClassificacao1 = new ArrayList<String>();
	
	/** As classes principais da classifica��o 2 */
	private  static List<String> classesPincipaisClassificacao2 = new ArrayList<String>();
	
	/** As classes principais da classifica��o 3 */
	private  static List<String> classesPincipaisClassificacao3 = new ArrayList<String>();
	
	/**
	 * <p>M�todo que verifica no banco se o sistema est� trabalhando com a primeira classifica��o.</p>
	 * @return
	 * @throws DAOException 
	 */
	public static boolean isSistemaUtilizandoClassificacao1(){
		if( expirouTempoAtualizaCache()){
			try {
				atualizaInformacoesClassificacaoUtilizadaSistema();
			} catch (DAOException e) {
				// retorna false se der erro ao buscar no banco
			}
		}
		return isSistemaUtilizandoClassificacao1;
	}
	
	
	/**
	 * <p>M�todo que verifica no banco se o sistema est� trabalhando com a primeira classifica��o.</p>
	 * @return
	 * @throws DAOException 
	 */
	public static boolean isSistemaUtilizandoClassificacao2(){
		if( expirouTempoAtualizaCache()){
			try {
				atualizaInformacoesClassificacaoUtilizadaSistema();
			} catch (DAOException e) {
				// retorna false se der erro ao buscar no banco
			}
		}
		return isSistemaUtilizandoClassificacao2;
	}
	
	
	/**
	 * <p>M�todo que verifica no banco se o sistema est� trabalhando com a primeira classifica��o.</p>
	 * @return
	 * @throws DAOException 
	 */
	public static boolean isSistemaUtilizandoClassificacao3(){
		if( expirouTempoAtualizaCache()){
			try {
				atualizaInformacoesClassificacaoUtilizadaSistema();
			} catch (DAOException e) {
				// retorna false se der erro ao buscar no banco
			}
		}
		return isSistemaUtilizandoClassificacao3;
	}
	
	
	
	/**
	 * <p>M�todo que retorna .</p>
	 * @return
	 * @throws DAOException 
	 */
	public static String getDescricaoClassificacao1(){
		if( expirouTempoAtualizaCache()){
			try {
				atualizaInformacoesClassificacaoUtilizadaSistema();
			} catch (DAOException e) {
				// retorna vazio se der erro ao buscar no banco
			}
		}
		return descricaoClassificacao1;
	}
	
	
	/**
	 * <p>M�todo que verifica no banco se o sistema est� trabalhando com a segunda classifica��o.</p>
	 * @return
	 * @throws DAOException 
	 */
	public static String getDescricaoClassificacao2(){
		if( expirouTempoAtualizaCache()){
			try {
				atualizaInformacoesClassificacaoUtilizadaSistema();
			} catch (DAOException e) {
				// retorna vazio se der erro ao buscar no banco
			}
		}
		return descricaoClassificacao2;
	}
	
	
	/**
	 * <p>M�todo que verifica no banco se o sistema est� trabalhando com a terceira classifica��o.</p>
	 * @return
	 * @throws DAOException 
	 */
	public static String getDescricaoClassificacao3(){
		if( expirouTempoAtualizaCache()){
			try {
				atualizaInformacoesClassificacaoUtilizadaSistema();
			} catch (DAOException e) {
				// retorna vazio se der erro ao buscar no banco
			}
		}
		return descricaoClassificacao3;
	}
	
	
	/**
	 * <p>M�todo que retorna .</p>
	 * @return
	 * @throws DAOException 
	 */
	public static List<String> getClassesPrincipaisClassificacao1(){
		if( expirouTempoAtualizaCache()){
			try {
				atualizaInformacoesClassificacaoUtilizadaSistema();
				Collections.sort(classesPincipaisClassificacao1);
			} catch (DAOException e) {
				// retorna cole��o vazia caso der erro ao buscar no banco
			}
		}
		
		return classesPincipaisClassificacao1;
	}
	
	
	/**
	 * <p>M�todo que verifica no banco se o sistema est� trabalhando com a segunda classifica��o.</p>
	 * @return
	 * @throws DAOException 
	 */
	public static List<String> getClassesPrincipaisClassificacao2(){
		if( expirouTempoAtualizaCache()){
			try {
				atualizaInformacoesClassificacaoUtilizadaSistema();
				Collections.sort(classesPincipaisClassificacao2);
			} catch (DAOException e) {
				// retorna cole��o vazia caso der erro ao buscar no banco
			}
		}
		return classesPincipaisClassificacao2;
	}
	
	
	/**
	 * <p>M�todo que verifica no banco se o sistema est� trabalhando com a terceira classifica��o.</p>
	 * @return
	 * @throws DAOException 
	 */
	public static List<String> getClassesPrincipaisClassificacao3(){
		if( expirouTempoAtualizaCache()){
			try {
				atualizaInformacoesClassificacaoUtilizadaSistema();
				Collections.sort(classesPincipaisClassificacao3);
			} catch (DAOException e) {
				// retorna cole��o vazia caso der erro ao buscar no banco
			}
		}
		
		return classesPincipaisClassificacao3;
	}
	
	
	
	/**
	 * Verifia se alguma das tag passadas correspondes a campos de classifica��o bibliogr�ficas.
	 *
	 * @param tag
	 * @return
	 */
	public static boolean isCampoUtilizadoClassificacao(String... tags){
		
		for (String tagCampos : tags) {
			if( Arrays.asList(CamposMarcClassificacaoBibliografica.getCamposClassificacao()).contains(tagCampos)
					|| Arrays.asList(CamposMarcClassificacaoBibliografica.getCamposClassificacao()).contains(tagCampos) ){
				return true;
			}
		}

		return false;
	}
	
	
	/**
	 * Retorna a classifica��o correspondente ao campo MARC passsado.
	 *
	 * @param classificacoes
	 * @param campoPassado
	 * @return
	 */
	public static ClassificacaoBibliografica encontraClassificacaoByCampo(List<ClassificacaoBibliografica> classificacoes, String... camposPassado){
		
		if(classificacoes != null)
		for (ClassificacaoBibliografica classificacaoBibliografica : classificacoes) {
			for (String campo : camposPassado) {
				if(classificacaoBibliografica.getCampoMARC().getCampo().equalsIgnoreCase(campo))
					return classificacaoBibliografica;
			}
			
		}
		
		return null;
	}
	
	/**
	 * Retorna a classifica��o correspondente a ordem passada
	 *
	 * @param classificacoes
	 * @param campoPassado
	 * @return
	 */
	public static ClassificacaoBibliografica encontraClassificacaoByCampo(List<ClassificacaoBibliografica> classificacoes, OrdemClassificacao ordem){
		
		if(classificacoes != null)
		for (ClassificacaoBibliografica classificacaoBibliografica : classificacoes) {
			if(classificacaoBibliografica.getOrdem() == ordem)
				return classificacaoBibliografica;
		}
		
		return null;
	}
	
	
	/**
	 * <p> M�todo que configura as informa��es das classes principais e �reas CNPQ das classifica��es passada para o T�tulo passado. </p>
	 * 
	 * <p> M�todo � chamadado porque o sistema mant�m em cache essas informa��es duplicadas para facilitar e melhar a performance das consultas dos relat�rios. </p>
	 * 
	 * @throws DAOException 
	 */
	public static void configuraClassificacoesEAreasCNPQTitulo(TituloCatalografico titulo, ClassificacaoBibliografica... classificacoes) throws DAOException {
		
			if(classificacoes != null)
			for (ClassificacaoBibliografica classificacao : classificacoes) {
				
				if(classificacao == null) return;
				
				if(classificacao.isPrimeiraClassificacao()){
					// Apaga o que tinha antes ///
					titulo.zeraDadosClassificacao1();
				}
				
				if(classificacao.isSegundaClassificacao()){
					// Apaga o que tinha antes ///
					titulo.zeraDadosClassificacao2();
				}
				
				
				if(classificacao.isTerceiraClassificacao()){
					// Apaga o que tinha antes ///
					titulo.zeraDadosClassificacao3();
				}
				
				
				String classificacaoInformada = null;
				
				// percorre o T�tulo at� achar o campo e sub campo correspondente a classifica��o passada //
				if(titulo.getCamposDados() != null){
					forExterno:
					for (CampoDados dado : titulo.getCamposDados()) {
			
						if((dado != null && dado.getEtiqueta() != null) && classificacao.getCampoMARC() != null 
								&& dado.getEtiqueta().equals(new Etiqueta(classificacao.getCampoMARC().getCampo(), TipoCatalogacao.BIBLIOGRAFICA))){
			
							
							for (SubCampo sub : dado.getSubCampos()) {
								if(sub.getCodigo() != null && sub.getCodigo().equals( classificacao.getCampoMARC().getSubCampo()  ) ){
									classificacaoInformada = sub.getDado();
									break forExterno;
								}
							}
						}
					}
				}
				
				
				if ( StringUtils.notEmpty( classificacaoInformada ) ) {
					
					if(classificacao.isPrimeiraClassificacao()){
						titulo.setClassificacao1(classificacaoInformada); 
						titulo.setClassePrincipalClassificacao1(encontraClassePrincipal(classificacaoInformada, classificacao.getClassesPrincipaisClassificacaoBibliografica()));
						titulo.setAreaConhecimentoCNPQClassificacao1(encontraAreaConhecimentoCNPQClassificacao(classificacaoInformada, classificacao));
					
					}
					if(classificacao.isSegundaClassificacao()){
						titulo.setClassificacao2(classificacaoInformada); 
						titulo.setClassePrincipalClassificacao2(encontraClassePrincipal(classificacaoInformada, classificacao.getClassesPrincipaisClassificacaoBibliografica()));
						titulo.setAreaConhecimentoCNPQClassificacao2(encontraAreaConhecimentoCNPQClassificacao(classificacaoInformada, classificacao));
					}
					
					if(classificacao.isTerceiraClassificacao()){
						titulo.setClassificacao3(classificacaoInformada); 
						titulo.setClassePrincipalClassificacao3(encontraClassePrincipal(classificacaoInformada, classificacao.getClassesPrincipaisClassificacaoBibliografica()));
						titulo.setAreaConhecimentoCNPQClassificacao3(encontraAreaConhecimentoCNPQClassificacao(classificacaoInformada, classificacao) );
					}
					
				}
				
			} // for classifica��es
		
	}

	

	/**
	 * Enconta a classe principal da classificao bibliogr�fica passada.
	 *
	 * @param dadosClassificacaoDigitadaUsuario
	 * @param classesPrincipaisClassificacaoBibliografica
	 * @return
	 */
	public static String encontraClassePrincipal(String classificacaoBibliografica, List<String> classesPrincipaisClassificacaoBibliografica) {
		
		if(StringUtils.isEmpty(classificacaoBibliografica))
			return null;
		
		// Ordena as classes principais pelo tamnho e texto para poder usar corretamente o m�todo startsWith
		Collections.sort(classesPrincipaisClassificacaoBibliografica, new Comparator<String>() {

			@Override
			public int compare(String s1, String s2) {
				
				if(s2.length() == s1.length()){
					return s1.compareTo(s2); // se for mesmo tamanho, compara pelo texto
				}else{
					return s2.length()- s1.length(); // se n�o, compara pelo tamanho da ordem inversa
				}
			}

		});
		
		
		// Algumas classifica��es podem conter par�nteses (34) 5334.444,  ent�o a classifica��o come�a a contar depois do par�nteses = 5334.444 
		
		final char abreParenteses = '(';
		final char fechaParenteses = ')';
		
		if(classificacaoBibliografica.startsWith(""+abreParenteses)){ // se contiver '(' e ')' a classe principal � o n�mero depois do par�nteses
			
			int ptr = classificacaoBibliografica.indexOf(""+abreParenteses);
			
			while(ptr < classificacaoBibliografica.length() && classificacaoBibliografica.charAt(ptr) != fechaParenteses ){
				ptr++;
			}
			
			if(ptr < classificacaoBibliografica.length()-1 ) // se n�o terminou porque chegou no final, anda mais 1 para retirar o ')'
				ptr++;
			
			String classificacaoDepoisParenteses = classificacaoBibliografica.substring(ptr, classificacaoBibliografica.length());
			
			return encontraClassePrincipal(classificacaoDepoisParenteses, classesPrincipaisClassificacaoBibliografica); // chama novamente o m�todo para achar a classe principal com a informa��o depois do par�ntestes
		}
		
		
		
		
		/*
		 * Para cada classe principal, verifica se o valor num�rico dos X primeiros d�gitos da 
		 * classifica��o est� entre a classe principal atual e a pr�xima e se o primeiro d�gito � igual a classe atual,
		 *  se positivo, ent�o encontramos a classe principal.
		 * 
		 * X = n�mero de d�gitos da classe principal.
		 * 
		 * Importente para suportar as regras das classifica��es CDU e CDD. Haja visto as classes principais CDU precisamos apanes 
		 * verificar se o n�mero come�a com a classe principal, j� na CDD , "500" � a classe principal para "513", apezar de "513" n�o come�ar com "500".
		 * 
		 * Para a classe BLACK, tentamos tratrar do mesmo jeito a classifica��o e classes principais, retirando os "D" iniciais nos n�meros das classifica��es.
		 * 
		 * Esse algortimo serve para encontrar as classes principais das principais classifica��es utilizadas nas bibliotecas: CDU, CDD e BLACK.
		 * 
		 */
		StringBuilder classificacaoTemp = new StringBuilder(classificacaoBibliografica);
		
		while( classificacaoTemp.length() > 0  &&  ! Character.isDigit(classificacaoTemp.charAt(0))){
			classificacaoTemp = classificacaoTemp.deleteCharAt(0);     // Retira os "D" ou caracteres iniciais das classifica��es bibliogr�ficas
		}
		
		
		for (int index = 0 ; index < classesPrincipaisClassificacaoBibliografica.size(); index++ ) {
			
			boolean possuiProximaClassePrincipal = index+1 < classesPrincipaisClassificacaoBibliografica.size() ? true : false ;
			
			String classePrincipal = classesPrincipaisClassificacaoBibliografica.get(index);
			String proximaClassePrincipal =  possuiProximaClassePrincipal ? classesPrincipaisClassificacaoBibliografica.get(index+1) : null;
			
			StringBuilder classePrincipalTemp = new StringBuilder(classePrincipal);
			StringBuilder proximaClassePrincipalTemp = possuiProximaClassePrincipal ? new StringBuilder(proximaClassePrincipal) : null;
			
			while( classePrincipalTemp.length() > 0 && ! Character.isDigit(classePrincipalTemp.charAt(0))){
				classePrincipalTemp = classePrincipalTemp.deleteCharAt(0); // Retira os "D" ou caracteres iniciais das classifica��es bibliogr�ficas
			}
			
			if(possuiProximaClassePrincipal){
				while( proximaClassePrincipalTemp.length() > 0 &&  ! Character.isDigit(proximaClassePrincipalTemp.charAt(0))){
					proximaClassePrincipalTemp = proximaClassePrincipalTemp.deleteCharAt(0);     // Retira os "D" ou caracteres iniciais das classifica��es bibliogr�ficas
				}
			}
			
			
			int qtdDigitos = classePrincipalTemp.length(); // quantidade de d�gitos compar�veis
			
			if(qtdDigitos > classificacaoTemp.length())
				qtdDigitos =  classificacaoTemp.length();
			
			int valorClassificacacao = 0;
			int valorClassePrincipal = 0;
			int valorProximaClassePrincipal = 0; 
			
			try{
				
				valorClassificacacao = Integer.parseInt(classificacaoTemp.substring(0, qtdDigitos)); // O valor num�rico dos X primeiros d�gitos da classifica��o
				
				valorClassePrincipal = Integer.parseInt(classePrincipalTemp.toString());
				valorProximaClassePrincipal = possuiProximaClassePrincipal ? Integer.parseInt(proximaClassePrincipalTemp.toString()) : 0;
				
		
				if(possuiProximaClassePrincipal){
					
					// Se o valor est� entre o interfava e o primeiro caracter e igual ao primeiro caracter da classe principal //
					if(valorClassificacacao >= valorClassePrincipal && valorClassificacacao < valorProximaClassePrincipal
							&& isPrimeiroCaracterIgual( classificacaoTemp.toString(), classePrincipalTemp.toString()) ){
						return classePrincipal;
					}
				}else{
					if(valorClassificacacao >= valorClassePrincipal
							&& isPrimeiroCaracterIgual( classificacaoTemp.toString(), classePrincipalTemp.toString()) ){
						return classePrincipal;
					}
				}
			
			}catch(NumberFormatException nfe){
				
				// N�o � um n�mero, ent�o compara com startsWith
				if(classificacaoBibliografica.toUpperCase().trim().startsWith(classePrincipal) ){
					return classePrincipal;
				}
			}
			
		}
		
		
		return null;
		
	}
	
	
	/** Verifica se o primeiro caracter das string passadas s�o iguais */
	private static boolean isPrimeiroCaracterIgual(String classificao, String classePrincial){
		return classificao.startsWith(""+classePrincial.toString().charAt(0));
	}
	
	
	/**
	 * Encontra a �rea de conhecimento CNPQ a partir da classe da classifica��o passada e do relacionamento entre �reas CNPq e classifica��es bibliograficas existente no sistema.
	 *
	 * @see RelacionaClassificacaoBibliograficaAreaCNPq
	 *
	 * @param grandesAreasCNPq
	 * @param dadosClassificacaoDigitadaUsuario
	 * @param classificacao
	 * @return
	 * @throws DAOException 
	 */
	private static AreaConhecimentoCnpq encontraAreaConhecimentoCNPQClassificacao( String dadosClassificacaoDigitadaUsuario, ClassificacaoBibliografica classificacao) throws DAOException {
		
		ClassificacaoBibliograficaDao dao = null;
		
		try{
			
			dao = DAOFactory.getInstance().getDAO(ClassificacaoBibliograficaDao.class);
			
			List<RelacionaClassificacaoBibliograficaAreaCNPq> relacionamentos =  dao.findInformacoesRelacionamentosAreasCNPqByClassificacaoParaValidacao(classificacao.getId());
			
			if(dadosClassificacaoDigitadaUsuario == null)  return null;
			
			boolean encontrouClasse = false;
			
			
			
			for (RelacionaClassificacaoBibliograficaAreaCNPq relacionamento : relacionamentos) {
				
				encontrouClasse = false;
				
				String[] classesInclusao = new String[0]; // est�o separadas por espa�o.
				String[] classesExclusao = new String[0];
				
				if(StringUtils.notEmpty(relacionamento.getClassesInclusao()))
					classesInclusao = relacionamento.getClassesInclusao().split("\\s");
				
				if(StringUtils.notEmpty(relacionamento.getClassesExclusao()))
					classesExclusao = relacionamento.getClassesExclusao().split("\\s");
			
				ordenaClassesPorTamanho(classesInclusao);
				ordenaClassesPorTamanho(classesExclusao);
				
				forClasses:
				for (String prefixo : classesInclusao) {
					
					if (StringUtils.isEmpty(prefixo))
							continue;
					
					boolean comecaPrexicoCadastrado = false;
					
					if(prefixo.contains("-")){  // � um intervalo de classes CPF
						comecaPrexicoCadastrado = estaDentroIntervaloClassificacao(prefixo, dadosClassificacaoDigitadaUsuario);
					}else{
						comecaPrexicoCadastrado =  dadosClassificacaoDigitadaUsuario.startsWith(prefixo);
					}
					
					if (comecaPrexicoCadastrado ){  // se come�a com o prefixo cadastrado
						
						encontrouClasse = true;
						
						for (String prefixoExclusao : classesExclusao) { // e n�o come�a com os prefixo de exclus�o
							
							if (StringUtils.isEmpty(prefixoExclusao))
								continue;
							
							boolean naoComecaPrexicoCadastrado = false;
							
							if(prefixoExclusao.contains("-")){  // � um intervalo de classes CPF
								naoComecaPrexicoCadastrado = estaDentroIntervaloClassificacao(prefixoExclusao, dadosClassificacaoDigitadaUsuario);
							}else{
								naoComecaPrexicoCadastrado =  dadosClassificacaoDigitadaUsuario.startsWith(prefixoExclusao);
							}
							
							if (  naoComecaPrexicoCadastrado ){
								encontrouClasse = false;
							}
						}
						
						if(encontrouClasse){
							break forClasses;
						}
					}
					
				}
				
				if(encontrouClasse){
					return relacionamento.getArea();
				}
				
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
		return null;
	}

	
	
	/**
	 *    Ordena as classes por tamanho. Os maiores devem vir primeiro, j� que a compara��o tem que ser com <tt>startsWith</tt>.
	 */
	private static void ordenaClassesPorTamanho(String[] classes){
	
		for (int i = 0; i < classes.length-1; i++) {
			for (int j = i+1; j < classes.length; j++) {
				
				if(classes[i].length() < classes[j].length()){
					String temp = classes[j];
					classes[j] = classes[i];
					classes[i] = temp;
				}
			}
		}
		
	}
	
	
	/**
	 *   <p>M�todo que verifica se a classifica��o cdu est� dentro do intervalo passado.</p>
	 *   <p>Por exemplo, verifica se a classifica��o cdu 681.271 est� dentro do intervalo 681.1-681.270 </p>
	 *
	 * @param intervaloClassificacao
	 * @param classificacaoCDUdoTitulo
	 * @return
	 */
	private static boolean estaDentroIntervaloClassificacao(String intervaloClassificacao, String classificacaoTitulo){
		
		if(StringUtils.isEmpty(classificacaoTitulo))
			return false;
		else{
			try{
			
				String[] temp =  intervaloClassificacao.split("-");
				
				
					
				if( StringUtils.notEmpty(temp[0])){
					temp[0] = retiraCaractaresInicialNaoDigitos(temp[0]);
					
				}
				
				if( StringUtils.notEmpty(temp[1])){
					temp[1] = retiraCaractaresInicialNaoDigitos(temp[1]);	
				}
				
				if( StringUtils.notEmpty(classificacaoTitulo)){
					classificacaoTitulo = retiraCaractaresInicialNaoDigitos(classificacaoTitulo);
					
				}
					
				float f1 = new Float(temp[0]);
				float f2 = new Float(temp[1]);
				
				int tamanhoIntervalo = temp[0].length();
				
				if(tamanhoIntervalo < temp[1].length()){
					tamanhoIntervalo = temp[1].length();
				}
				
				// tentar converter os dados da classifica��o do T�tulo em um n�mero para pode chegar o intervalo
				if(classificacaoTitulo == null)
					return false;
				
				// Para comprarar intervalos, tem que recuperar os primeiros caracteres num�rico
				// por exemplo CDU = "1(44)",   recuperar somente o '1'
				String classificacaoTituloNumera = "";
				
				for (int posicao = 0; posicao < classificacaoTitulo.length(); posicao++) {
					
					if( Character.isDigit(classificacaoTitulo.charAt(posicao)))
						classificacaoTituloNumera += classificacaoTitulo.charAt(posicao);
					else{
						break;  // no primeiro caracter n�o d�gito, sai do laco
					}
				}
				
				if(classificacaoTituloNumera.length() > tamanhoIntervalo)
					classificacaoTituloNumera = classificacaoTituloNumera.substring(0, tamanhoIntervalo);
				else{
					if(classificacaoTituloNumera.length() < tamanhoIntervalo){
						
						// Por exemplo:  Intervalo definido de 100 a 179 e classifica��o = 1,
						// tem que comparar 100 <= 100 <= 179  se comparar 100 <= 1 <= 179 vai ser false
						// quando na verdade est� entre o intervalo
						
						for (int index = classificacaoTituloNumera.length(); index < tamanhoIntervalo; index++) {
							classificacaoTituloNumera = classificacaoTituloNumera+"0";
						}
					}else{
						// se for do mesmo tamanho n�o precisa fazer nada
					}
				}
				
				float f3 = new Float( classificacaoTituloNumera );
				
				if(f1 <= f3 && f3 <= f2){
					return true;
				}else{
					return false;
				}
			
			}catch(NumberFormatException nfe){
				/* se os dados do intervalo ou classifica��o n�o podem ser convertidos para
				 * n�meros n�o d� para testar se a classifica��o est� dentro do intervalo
				 */
				return false;
			}
		}
	}


	/**
	 * Retira os que n�o for d�gito do intervalo informado pelo usu�rio. Algumas classifica��es 
	 * possuiem letras que n�o d� para calcular o intervalo.
	 * 
	 * Exemplo  A classifica��o black come�a as classes com "D" 
	 *  
	 * @param temp
	 */
	private static String retiraCaractaresInicialNaoDigitos(String temp) {
			
		if( ! Character.isDigit(temp.charAt(0)) ){
			return temp.subSequence(1, temp.length()).toString(); 
			
		}
		return temp;
	}
	
	
	
	
	
	
	/**
	 * Remove as classifica��es bibliogr�ficas e suas respectivas �reas de conhecimento CNPq de campos remotivos do Titulo pelo usu�rio.<br/>
	 * 
	 */
	public static void removeClassificacoesNaoInformadas(TituloCatalografico titulo, List<ClassificacaoBibliografica> classificacoesUtilizada){
		
		boolean classifcacao1Presente = false;
		boolean classifcacao2Presente = false;
		boolean classifcacao3Presente = false;

		// Percorre os campos de dados
		if(titulo != null && titulo.getCamposDados() != null){
			
			
			// Verifica para cada classifica��o configurada no sistema se os dados dela foram preenchidos //
			
			for (ClassificacaoBibliografica classificacaoUtilizada : classificacoesUtilizada) {
				if(classificacaoUtilizada.isPrimeiraClassificacao() ){
					classifcacao1Presente = verificaSeCampoCorrepondendeClassificacaoPreenchido(titulo, classificacaoUtilizada);
				}	
				else if(classificacaoUtilizada.isSegundaClassificacao() ){
					classifcacao2Presente = verificaSeCampoCorrepondendeClassificacaoPreenchido(titulo, classificacaoUtilizada);
				}	
				else if(classificacaoUtilizada.isTerceiraClassificacao() ){
					classifcacao3Presente = verificaSeCampoCorrepondendeClassificacaoPreenchido(titulo, classificacaoUtilizada);
				}
			}
			
			if(! classifcacao1Presente)
				titulo.zeraDadosClassificacao1(); 
			
			if(! classifcacao2Presente)
				titulo.zeraDadosClassificacao2(); 
			
			if(! classifcacao3Presente)
				titulo.zeraDadosClassificacao3(); 
				
			// S� chamar depois que atribuio o valor "null"  ///
			titulo.anularAreasCNPqNaoInformadas();
			
		}
		
	}


	
	
	
	/**
	 * Verifica se o T�tulo passado possui o campo correpondende � classifica��o passada preenchido pelo usu�rio
	 */
	public static boolean verificaSeCampoCorrepondendeClassificacaoPreenchido(TituloCatalografico titulo, ClassificacaoBibliografica classificacaoUtilizada){
		
		// Percorre os campos de dados
		if(titulo != null && titulo.getCamposDados() != null){
			
			for (CampoDados dados : titulo.getCamposDados()) {
				
				if(dados.getEtiqueta().equals(new Etiqueta(classificacaoUtilizada.getCampoMARC().getCampo(), TipoCatalogacao.BIBLIOGRAFICA))){
					
					if(dados.getSubCampos() != null)
						for (SubCampo sub : dados.getSubCampos()) { // se tem o sub campo 'a' com dados ok
							if(StringUtils.notEmpty( sub.getDado()) && sub.getCodigo() != null && sub.getCodigo().equals(   classificacaoUtilizada.getCampoMARC().getSubCampo()   )   ){
								return true;
							} 
						}
				}
				
			}
		}
		
		return false;
	}
	
	
	
	/**
	 * Retorna a informa��o contida no campo correspondente a informa��o passada.
	 */
	public static String retornaInformacaoCampoDaClassificacao(TituloCatalografico titulo, ClassificacaoBibliografica classificacaoUtilizada){
		
		// Percorre os campos de dados
		if(titulo != null && titulo.getCamposDados() != null){
			
			for (CampoDados dados : titulo.getCamposDados()) {
				
				if(dados.getEtiqueta().equals(new Etiqueta(classificacaoUtilizada.getCampoMARC().getCampo(), TipoCatalogacao.BIBLIOGRAFICA))){
					
					if(dados.getSubCampos() != null)
						for (SubCampo sub : dados.getSubCampos()) { // se tem o sub campo 'a' com dados ok
							if(StringUtils.notEmpty( sub.getDado()) && sub.getCodigo() != null && sub.getCodigo().equals(   classificacaoUtilizada.getCampoMARC().getSubCampo()   )   ){
								return sub.getDado();
							} 
						}
				}
				
			}
		}
		
		return null; // a informa��o do campo n�o est� preenchida
	}
	
	
	
	/**
	 * M�todo que verifica se j� est� no tempo de atualizar o cache
	 *
	 * @return
	 */
	private static boolean expirouTempoAtualizaCache(){
		if(lastRefreshTime == null) 
			return true;
		else 
			return  ( System.currentTimeMillis() - lastRefreshTime.getTime() ) > ( REFRESH_TIME_CACHE_DIGITAIS * 1000 );
	}
	
	
	/**
	 * Atualiza as inforam��es das classifica��es utilizadas pelo sistema quando o tempo de atualiza��o expirou.
	 *  
	 * @throws DAOException
	 */
	private synchronized static void atualizaInformacoesClassificacaoUtilizadaSistema() throws DAOException {
		ClassificacaoBibliograficaDao dao = null;
		try{
			dao =  DAOFactory.getInstance().getDAO(ClassificacaoBibliograficaDao.class);
			
			isSistemaUtilizandoClassificacao1 =  dao.isSistemaTrabalhaClassificacao(OrdemClassificacao.PRIMERA_CLASSIFICACAO);
			isSistemaUtilizandoClassificacao2 =  dao.isSistemaTrabalhaClassificacao(OrdemClassificacao.SEGUNDA_CLASSIFICACAO);
			isSistemaUtilizandoClassificacao3 =  dao.isSistemaTrabalhaClassificacao(OrdemClassificacao.TERCEIRA_CLASSIFICACAO);
			
			if(isSistemaUtilizandoClassificacao1){
				descricaoClassificacao1  =  dao.getDescricaoClassificacao(OrdemClassificacao.PRIMERA_CLASSIFICACAO);
				classesPincipaisClassificacao1 = dao.getClassesPrincipaisClassificacao(OrdemClassificacao.PRIMERA_CLASSIFICACAO);
			}
			
			if(isSistemaUtilizandoClassificacao2){
				descricaoClassificacao2  =  dao.getDescricaoClassificacao(OrdemClassificacao.SEGUNDA_CLASSIFICACAO);
				classesPincipaisClassificacao2 = dao.getClassesPrincipaisClassificacao(OrdemClassificacao.SEGUNDA_CLASSIFICACAO);
			}
			
			if(isSistemaUtilizandoClassificacao3){
				descricaoClassificacao3  =  dao.getDescricaoClassificacao(OrdemClassificacao.TERCEIRA_CLASSIFICACAO);
				classesPincipaisClassificacao3 = dao.getClassesPrincipaisClassificacao(OrdemClassificacao.TERCEIRA_CLASSIFICACAO);
			}
			
			lastRefreshTime = new Date();
			
			ClassificacoesBibliograficasUtil.class.notifyAll(); // libera para outros thread entrarem na regi�o cr�tica	
			
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	
}
