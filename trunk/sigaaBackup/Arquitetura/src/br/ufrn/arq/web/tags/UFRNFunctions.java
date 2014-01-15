/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 13/10/2006
 */
package br.ufrn.arq.web.tags;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.springframework.web.util.HtmlUtils;

import br.ufrn.arq.email.Mail;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;

/**
 * Classe que implementa as funções via Expression Language
 *
 * @author Gleydson Lima
 *
 */
public class UFRNFunctions {

	private static final List<String> PACOTES = new ArrayList<String>();
	private static final List<String> PACOTES_PARAMETROS = new ArrayList<String>();
	
	private static final Map<String, String> CACHE_CONSTANTES = new HashMap<String, String>();
	private static final Map<String, String> CACHE_CONSTANTES_PARAMETROS = new HashMap<String, String>();
	
	public static boolean contains(Object[] opcoes, Object valor) {
		if(opcoes != null)
			for (Object o : opcoes) {
				if (o.equals(valor)) {
					return true;
				}
			}
		return false;
	}

	public static String mensagem(String constante) {
		
		MensagemAviso msg = UFRNUtils.getMensagem(constante);
		
		if (msg.getMensagem().contains("Mensagem não encontrada")) {
			String codigo = CACHE_CONSTANTES.get(constante);
			if (codigo == null) {
				if (isEmpty(PACOTES)) {
					try {
						ResourceBundle pacotesBundle = ResourceBundle.getBundle("pacotes_mensagens");
						Enumeration<String> keys = pacotesBundle.getKeys();
						while (keys.hasMoreElements()) {
							String key = keys.nextElement();
							PACOTES.add(pacotesBundle.getObject(key).toString());
						}
						
					} catch (Exception e ) {
						System.err.println("Erro ao encontrar informações dos pacotes de mensagens.");
					}
				}
				
				for (String pacote : PACOTES) {
					String classe = pacote + "." + constante.substring(0, constante.lastIndexOf("."));
					String atributo = constante.substring(constante.lastIndexOf(".") + 1);
					
					codigo = ReflectionUtils.getValorConstante(classe, atributo);
					if (codigo != null) {
						CACHE_CONSTANTES.put(constante, codigo);
						break;
					}
				}
			}
			
			msg = UFRNUtils.getMensagem(codigo);
			//verificando de mensagem não encontrada.
			if(msg.getMensagem().contains("Mensagem não encontrada")) {
				StackTraceElement[] st = Thread.currentThread().getStackTrace();
				String texto = "Mensagem: " + constante + " não definida. <br/><br/>";
				String trace = Arrays.toString(st);
				trace = trace.replace(",", "<br/>");
				Mail.sendAlerta(texto + trace);
			}
		}
		
		
		return msg.getMensagem();
	}
	
	public static String parametro(String constante) {
		String codigo = CACHE_CONSTANTES_PARAMETROS.get(constante);
		if (codigo == null) {
			if (isEmpty(PACOTES_PARAMETROS)) {
				try {
					ResourceBundle pacotesBundle = ResourceBundle.getBundle("pacotes_parametros");
					Enumeration<String> keys = pacotesBundle.getKeys();
					while (keys.hasMoreElements()) {
						String key = keys.nextElement();
						PACOTES_PARAMETROS.add(pacotesBundle.getObject(key).toString());
					}
					
				} catch (Exception e ) {
					System.err.println("Erro ao encontrar informações dos pacotes de mensagens.");
				}
			}
			
			for (String pacote : PACOTES_PARAMETROS) {
				String classe = pacote + "." + constante.substring(0, constante.lastIndexOf("."));
				String atributo = constante.substring(constante.lastIndexOf(".") + 1);
				
				codigo = ReflectionUtils.getValorConstante(classe, atributo);
				if (codigo != null) {
					CACHE_CONSTANTES_PARAMETROS.put(constante, codigo);
					break;
				}
			}
		}
		try {
			String parametro = ParametroHelper.getInstance().getParametro( codigo );
			return parametro.toString();
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String escapeHtml(String html) {
		return HtmlUtils.htmlEscape(html).replaceAll("\r", "").replaceAll("\n", "<br/>").replaceAll("\"", "&quot;");
	}

	public static String nomeResumido(String nome) {
		if (nome == null || StringUtils.isEmpty(nome))
			return "";

		String[] partes = nome.split(" ");
		if (partes.length > 1)
			return partes[0] + " " + partes[partes.length - 1];
		return partes[0];
	}

	public static String descDiaSemana(int dia) {
		switch(dia) {
		case Calendar.SUNDAY: return "Domingo";
		case Calendar.MONDAY: return "Segunda-feira";
		case Calendar.TUESDAY: return "Terça-feira";
		case Calendar.WEDNESDAY: return "Quarta-feira";
		case Calendar.THURSDAY: return "Quinta-feira";
		case Calendar.FRIDAY: return "Sexta-feira";
		case Calendar.SATURDAY: return "Sábado";
		default: return "";
		}
	}
	
	public static String descMes(int mes) {
		switch(mes) {
		case 0: return "Janeiro";
		case 1: return "Fevereiro";
		case 2: return "Março";
		case 3: return "Abril";
		case 4: return "Maio";
		case 5: return "Junho";
		case 6: return "Julho";
		case 7: return "Agosto";
		case 8: return "Setembro";
		case 9: return "Outubro";
		case 10: return "Novembro";
		case 11: return "Dezembro";
		default: return "";
		}
	}

	public static String descricaoConceito(int conceito) {
		if (conceito == 5) return "A";
		else if (conceito == 4) return "B";
		else if (conceito == 3) return "C";
		else if (conceito == 2) return "D";
		else return "E";
	}

	public static int piso(double numero) {
		return (int) Math.floor(numero);
	}

	public static boolean interseccao(Date data, Date inicio, Date fim) {
		return data.equals(inicio) || data.equals(fim) || (data.after(inicio) && data.before(fim));
	}

	public static int qtdDias(Set<Date> datas, int mes) {
		int qtd = 0;
		
		for (Date date : datas) {
			if (CalendarUtils.getMesByData(date) == mes) qtd++;
		}
		
		return qtd;
	}
	
	public static String generateArquivoKey(int idArquivo) {
		return UFRNUtils.generateArquivoKey(idArquivo);
	}
	
	public static void main(String[] args) {
		
		System.out.println(generateArquivoKey(38682));
		
	}
	
	public static String converteMaiusculo(String texto) {
		texto = texto.toUpperCase();
		return texto;
	}
	
	/**
	 * Método marca em negrito em texto uma ou mais palavras.
	 * e as destaca em negrito.
	 * @param frase - String a ser procurada.
	 * @param texto - Texto onde deve ser procurado a palavra.
	 * @return
	 */
	public static String marcaTexto(String frase, String texto){

		StringBuilder sb = new StringBuilder(StringUtils.stripHtmlTags(texto));
		String[] palavras = frase.trim().split(" ");
		Integer posicao;
		
		for (int j = 0; j < palavras.length; j++) {
	
			for( int i = 0; i < texto.length(); i++ ){
				
				posicao =  sb.toString().toLowerCase().indexOf(palavras[j].toLowerCase(), i);
				if(posicao>=0 && palavras[j].length()>=2){
					sb.insert(posicao, "<b>");
					sb.insert(posicao+palavras[j].length()+3, "</b>");
					i = posicao + palavras[j].length()+3;
				}
				
			}
			
		}
		
		return sb.toString();
				
	}
}