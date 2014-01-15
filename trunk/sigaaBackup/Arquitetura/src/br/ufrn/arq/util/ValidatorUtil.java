/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 05/09/2009
 */
package br.ufrn.arq.util;

import static br.ufrn.arq.mensagens.MensagensArquitetura.AUTOCOMPLETE_OBRIGATORIO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.CAMPO_SEM_ABREVIACOES;
import static br.ufrn.arq.mensagens.MensagensArquitetura.CONTEUDO_INVALIDO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.DATA_ANTERIOR_A;
import static br.ufrn.arq.mensagens.MensagensArquitetura.DATA_ANTERIOR_IGUAL;
import static br.ufrn.arq.mensagens.MensagensArquitetura.DATA_INICIO_MENOR_FIM;
import static br.ufrn.arq.mensagens.MensagensArquitetura.DATA_NASCIMENTO_DIFERENTE_ANO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.DATA_POSTERIOR_A;
import static br.ufrn.arq.mensagens.MensagensArquitetura.DATA_POSTERIOR_ANTERIOR_A;
import static br.ufrn.arq.mensagens.MensagensArquitetura.FORMATO_INVALIDO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.MAXIMO_CARACTERES;
import static br.ufrn.arq.mensagens.MensagensArquitetura.MINIMO_CARACTERES;
import static br.ufrn.arq.mensagens.MensagensArquitetura.PAGINA_INICIAL_MAIOR_FINAL;
import static br.ufrn.arq.mensagens.MensagensArquitetura.URL_INVALIDA;
import static br.ufrn.arq.mensagens.MensagensArquitetura.VALOR_MAIOR_IGUAL_A;
import static br.ufrn.arq.mensagens.MensagensArquitetura.VALOR_MAIOR_IGUAL_ZERO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.VALOR_MAIOR_QUE_MENOR_QUE;
import static br.ufrn.arq.mensagens.MensagensArquitetura.VALOR_MAIOR_ZERO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.VALOR_MENOR_IGUAL_A;

import static br.ufrn.arq.util.CalendarUtils.descartarHoras;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.commons.validator.UrlValidator;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.AnnotationValidation;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;

/**
 * Classe utilitária para realização de validações.
 *
 * @author Édipo Elder F. Melo
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class ValidatorUtil {

	/** Adiciona uma mensagem de erro à lista de mensagens.
	 * @param msgConteudo
	 * @param lista
	 */
	@Deprecated
	public static void addMensagemErro(String msgConteudo, Collection<MensagemAviso> lista) {
		MensagemAviso msg = new MensagemAviso(msgConteudo, TipoMensagemUFRN.ERROR);
		lista.add(msg);
	}

	/**
	 * Valida se string  é diferente de null e não é vazia.
	 *
	 * @return
	 */
	public static final boolean isEmpty(String s) {
		return (s == null || s.trim().length() == 0);
	}

	/**
	 * Retorna TRUE se TODOS os objetos informados estão vazios. Utiliza a implementação de {@link ValidatorUtil#isEmpty(Object)}
	 * 
	 * @param objs
	 * @see ValidatorUtil#isEmpty(Object)
	 * @return
	 */
	public static final boolean isAllEmpty(Object... objs) {
		
		if (objs == null)
			return true;
		
		for (Object o : objs) {
			if (isEmpty(o) == false)
				return false;
		}
		
		return true;
	}
	
	/**
	 * Retorna TRUE se todos os objetos estão populados. Utiliza a implementação de {@link ValidatorUtil#isEmpty(Object)}
	 *  
	 * @param objs
	 * @see ValidatorUtil#isEmpty(Object)
	 * @return
	 */
	public static final boolean isAllNotEmpty(Object... objs) {
		
		if (objs == null)
			return false;
		
		for (Object o : objs) {
			if (isEmpty(o) == true)
				return false;
		}
		
		return true;
	}	
	
	/**
	 * Valida se um objeto é vazio. O seu funcionamento vai depender do tipo de objeto
	 * passado como parâmetro. Se o objeto for nulo, é vazio. Se for uma tring, verifica
	 * se não é string vazia ou não é formada apenas por espaços. Se for uma coleção,
	 * verifica se a coleção está vazia, etc.
	 *
	 * @return
	 */
	public static final boolean isEmpty(Object o) {
		if (o == null)
			return true;
		if (o instanceof String)
			return isEmpty( (String) o);
		if (o instanceof Number) {
			Number i = (Number) o;
			return (i.intValue() == 0);
		}
		if (o instanceof PersistDB)
			return ((PersistDB) o).getId() == 0;
		if (o instanceof Object[])
			return ((Object[]) o).length == 0;
		if (o instanceof int[])
			return ((int[]) o).length == 0;
		if (o instanceof Collection<?>)
			return ((Collection<?>) o).size() == 0;
		if (o instanceof Map<?, ?>)
			return ((Map<?, ?>) o).size() == 0;
		if (o instanceof ListaMensagens)
			return ((ListaMensagens) o).isEmpty();
		return false;
	}
	
	/**
	 * Verifica se um objeto não está vazio. É a negação
	 * do método isEmpty.
	 * @param o
	 * @return
	 */
	public static final boolean isNotEmpty(Object o) {
		return !isEmpty(o);
	}

	/** Valida se a data não é nula e se é válida.
	 * @param data
	 * @param campo
	 * @param lista
	 * @return
	 */
	public static Date validaData(String data, String campo, ListaMensagens lista) {
		validateRequired(data, campo, lista);

		Date dataObj = Formatador.getInstance().parseDate(data);
		if (dataObj != null) {
			return dataObj;
		} else {
			lista.addMensagem(FORMATO_INVALIDO, campo);
			return null;
		}
	}

	/** Valida se a data não é nula
	 * @param data
	 * @param campo
	 * @param lista
	 * @return
	 * @deprecated Usar validateRequired
	 */
	@Deprecated
	public static Date validaData(Date data, String campo, ListaMensagens lista) {
		if (data != null) {
			return data;
		} else {
			lista.addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, campo);
			return null;
		}
	}

	/**
	 * Valida se a data final é maior que data final.
	 * @throws ParseException
	 */
	public static void validaInicioFim(Date inicio, Date fim, String campo, ListaMensagens lista) {
		if (inicio != null && fim != null && inicio.getTime() > fim.getTime()) {
			lista.addMensagem(DATA_INICIO_MENOR_FIM, campo);
		}
	}

	/**
	 * Valida se data fim é maior ou igual a data inicio. Se não for, mostra
	 * mensagem passada pelo usuário
	 */
	@Deprecated
	public static void validaDataInicioFim(Date inicio, Date fim, String mensagem, Collection<MensagemAviso> lista) {
		if (inicio != null && fim != null && inicio.getTime() > fim.getTime()) {
			addMensagemErro(mensagem, lista);
		}
	}

	/**
	 * Valida se uma data inicial é anterior a uma data final.
	 *
	 * @param inicio
	 * @param fim
	 * @param considerarIguais. Se true -> bloqueia se início >= fim.. Se false -> bloqueia apenas se início > fim.
	 * @param campo
	 * @param lista
	 */
	public static void validaOrdemTemporalDatas(Date inicio, Date fim, boolean considerarIguais, String campo, ListaMensagens lista){
		if (inicio != null && fim != null) {

			if ( considerarIguais && inicio.getTime() > fim.getTime())
				lista.addMensagem(DATA_INICIO_MENOR_FIM, campo);
			if ( !considerarIguais && inicio.getTime() >= fim.getTime())
				lista.addMensagem(DATA_INICIO_MENOR_FIM, campo);
		}
	}

	/**
	 * Valida se o valor é maior que zero.
	 * @param valor
	 * @param campo
	 * @param lista
	 */
	public static void validaInt(Integer valor, String campo, ListaMensagens lista) {
		validaInt(valor, campo, false, lista);
	}

	/**
	 * Valida se o valor é maior que zero.
	 * @param valor
	 * @param campo
	 * @param lista
	 */
	public static void validaInt(Integer valor, String campo, boolean msgCampoObrigatorio, ListaMensagens lista) {
		if (valor == null || valor <= 0) {
			if (msgCampoObrigatorio) {
				lista.addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, campo);
			} else {
				lista.addMensagem(VALOR_MAIOR_ZERO, campo);
			}
		}
	}

	@Deprecated
	public static void validaInt(Integer valor, String campo, Collection<MensagemAviso> lista) {
		validaInt(valor, campo, new ListaMensagens(lista));
	}

	/** Valida se o valor é maior que zero.
	 * @param valor
	 * @param campo
	 * @param lista
	 */
	public static void validaInt(int valor, String campo, ListaMensagens lista) {
		validaInt(new Integer(valor), campo, lista);
	}

	@Deprecated
	public static void validaInt(int valor, String campo, Collection<MensagemAviso> lista) {
		validaInt(valor, campo, new ListaMensagens(lista));
	}

	/** Valida se o valor é maior que zero.
	 * @param valor
	 * @param campo
	 * @param lista
	 */
	public static void validaLong(long valor, String campo, ListaMensagens lista) {
		if (valor <= 0) {
			lista.addMensagem(VALOR_MAIOR_ZERO, campo);
		}
	}

	/**
	 * Valida se o valor é maior ou igual a zero
	 * @param valor
	 * @param campo
	 * @param lista
	 */
	public static void validaIntGt(Integer valor, String campo, ListaMensagens lista) {
		if (valor == null) valor = 0;
		if ( valor < 0) {
			lista.addMensagem(VALOR_MAIOR_IGUAL_ZERO, campo);
		}
	}

	@Deprecated
	public static void validaIntGt(Integer valor, String campo, Collection<MensagemAviso> lista) {
		validaIntGt(valor, campo, new ListaMensagens(lista));
	}

	/** Valida se o valor é maior ou igual a zero.
	 * @param valor
	 * @param campo
	 * @param lista
	 */
	public static void validaDouble(Double valor, String campo, ListaMensagens lista) {
		if (valor == null || valor < 0) {
			lista.addMensagem(VALOR_MAIOR_IGUAL_ZERO, campo);
		}
	}

	/** Valida se o valor é maior que zero.
	 * @param valor
	 * @param campo
	 * @param lista
	 */
	public static void validaDoublePositivo(Double valor, String campo, ListaMensagens lista) {
		if (valor == null || valor <= 0) {
			lista.addMensagem(VALOR_MAIOR_ZERO, campo);
		}
	}

	/** Valida se o valor é maior ou igual a zero.
	 * @param valor
	 * @param campo
	 * @param lista
	 */
	public static void validaFloatPositivo(Float valor, String campo, ListaMensagens lista) {
		if (valor == null || valor <= 0) {
			lista.addMensagem(VALOR_MAIOR_IGUAL_ZERO, campo);
		}
	}

	/** Valida o CPF/CNPJ.
	 * @param numero
	 * @param campo
	 * @param lista
	 * @return
	 */
	public static long validateCPF_CNPJ(String numero, String campo, ListaMensagens lista) {

		if (isEmpty(numero)) {
			lista.addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, campo);
			return 0;
		}

		String soNumeros = Formatador.getInstance().parseStringCPFCNPJ(numero);
		boolean deuCerto = ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(soNumeros);

		if (deuCerto) {
			return Long.parseLong(soNumeros);
		}

		lista.addMensagem(FORMATO_INVALIDO, campo);
		return 0;
	}

	/** Valida o CPF/CNPJ.
	 * @param numero
	 * @param campo
	 * @param lista
	 * @return
	 */
	public static boolean validateCPF_CNPJ(long numero, String campo, ListaMensagens lista) {
		boolean deuCerto = false;

		if (numero > 0){
			deuCerto = ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(numero);
		}

		if (!deuCerto) {
			lista.addMensagem(FORMATO_INVALIDO, campo);
		}

		return deuCerto;
	}

	/** Valida o RG.
	 * @param numero
	 * @param campo
	 * @param lista
	 * @return
	 */
	public static long validateRG(String numero, String campo, ListaMensagens lista) {

		if (isEmpty(numero))
			return 0;
		String soNumeros = Formatador.getInstance().parseRG(numero);
		try {
			return Long.parseLong(soNumeros);
		} catch (NumberFormatException e) {
			lista.addMensagem(FORMATO_INVALIDO, campo);
			return 0;
		}

	}

	/** Valida o telefone.
	 * @param numero
	 * @param campo
	 * @param lista
	 * @return
	 */
	public static int validateTelefone(String numero, String campo, ListaMensagens lista) {
		if (isEmpty(numero))
			return 0;
		String soNumeros = Formatador.getInstance().parseTelefone(numero);
		try {
			return Integer.parseInt(soNumeros);
		} catch (NumberFormatException e) {
			lista.addMensagem(FORMATO_INVALIDO, campo);
			return 0;
		}
	}

	/** Valida uma faixa de páginas.
	 * @param paginaInicial
	 * @param paginaFinal
	 * @param lista
	 */
	public static void validatePagina(int paginaInicial,int paginaFinal, ListaMensagens lista){
		if(paginaFinal<paginaInicial){
			lista.addMensagem(PAGINA_INICIAL_MAIOR_FINAL);
		}
	}

	/** Valida se o valor foi informado.
	 * @param valor
	 * @param campo
	 * @param lista
	 */
	public static void validateRequired(Object valor, String campo, ListaMensagens lista) {
		if (valor == null) {
			lista.addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, campo);
			return;
		} else if (valor instanceof String) {
			if (isEmpty((String) valor)) {
				lista.addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, campo);
			}
		} else if (valor instanceof PersistDB) {
			if (((PersistDB) valor).getId() <= 0) {
				lista.addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, campo);
			}
		} else if (valor instanceof Collection<?>) {
			if (((Collection<?>) valor).isEmpty()) {
				lista.addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, campo);
			}
		} else {
			if (isEmpty(valor.toString())) {
				lista.addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, campo);
			}
		}
	}

	/** Valida se o valor foi informado.
	 * @param valor
	 * @param campo
	 * @param lista
	 */
	@Deprecated
	public static void validateRequired(Object valor, String campo, Collection<MensagemAviso> lista) {
		validateRequired(valor, campo, new ListaMensagens(lista));

	}

	/** Valida se o valor foi informado.
	 * @param id
	 * @param msg
	 * @param lista
	 */
	public static void validateRequiredId(int id, String campo, ListaMensagens lista) {
		if (id <= 0) {
			lista.addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, campo);
		}
	}

	@Deprecated
	public static void validateRequiredId(int id, String campo, Collection<MensagemAviso> lista) {
		validateRequiredId(id, campo, new ListaMensagens(lista));
	}

	/** Valida se o valor foi informado.
	 * @param valor
	 * @param campo
	 * @param lista
	 */
	public static void validateRequiredId(char valor, String campo, ListaMensagens lista) {
		if (valor == '0') {
			lista.addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, campo);
		}
	}

	/** Valida se o valor foi informado.
	 * @param id
	 * @param campo
	 * @param lista
	 */
	public static void validateRequiredAjaxId(int id, String campo, ListaMensagens lista) {
		if (id <= 0) {
			lista.addMensagem(AUTOCOMPLETE_OBRIGATORIO, campo);
		}
	}

	/** Valida se a coleção é vazia.
	 * @param erro
	 * @param col
	 * @param lista
	 */
	public static void validateEmptyCollection(String erro, Collection<?> col, ListaMensagens lista) {
		if (col == null || col.size() == 0) {
			MensagemAviso msg = new MensagemAviso(erro, TipoMensagemUFRN.ERROR);
			lista.addMensagem(msg);
		}
	}

	/** Valida o e-mail.
	 *
	 * @param email
	 * @param campo
	 * @param lista
	 */
	public static void validateEmail(String email, String campo, ListaMensagens lista) {
		if (isEmpty(email))
			return;

		if (EmailValidator.getInstance().isValid(email))
			return;

		lista.addMensagem(FORMATO_INVALIDO, campo);
	}

	/**
	 * Valida o e-mail e se foi informado corretamente.
	 * @param email
	 * @param campo
	 * @param lista
	 */
	public static void validateEmailRequired(String email, String campo, ListaMensagens lista) {
		validateRequired(email, campo, lista);
		validateEmail(email, campo, lista);
	}

	/**
	 * Valida data de nascimento, evitando que o ano seja o atual.
	 *
	 * @author Andre M Dantas
	 */
	public static void validateBirthday(Date data, String campo, ListaMensagens lista) {
		if (data == null)
			return;
		Calendar now = Calendar.getInstance();
		Calendar birthday = Calendar.getInstance();
		birthday.setTime(data);
		if (now.get(Calendar.YEAR) <= birthday.get(Calendar.YEAR)) {
			lista.addMensagem(DATA_NASCIMENTO_DIFERENTE_ANO, campo, birthday.get(Calendar.YEAR));
		}
	}
	
	/** Valida se um objeto encontra-se no intervalo de min e max. Atualmente só
	 * valida Inteiro e Data.
	 * @param obj
	 * @param min
	 * @param max
	 * @param campo
	 * @param lista
	 */
	public static void validateRange(Object obj, Object min, Object max, String campo, ListaMensagens lista) {
		if (obj instanceof Date) {
			Formatador fmt = Formatador.getInstance();
			Calendar data = Calendar.getInstance();
			Calendar dataMin = Calendar.getInstance();
			Calendar dataMax = Calendar.getInstance();
			data.setTime((Date) obj);
			data = descartarHoras(data);
			
			if (min != null & max != null) {
				if (min instanceof Date & max instanceof Date) {
					dataMin.setTime((Date) min);
					dataMin = descartarHoras(dataMin);
					dataMax.setTime((Date) max);
					dataMax = descartarHoras(dataMax);
					
					if (data.before(dataMin) || data.after(dataMax)) {
						lista.addMensagem(DATA_POSTERIOR_ANTERIOR_A, campo, fmt.formatarData(dataMin.getTime()), fmt.formatarData(dataMax.getTime()));
					}
				}
			} else if (min != null & max == null) {
				dataMin.setTime((Date) min);
				dataMin = descartarHoras(dataMin);
				
				if (data.before(dataMin)) {
					lista.addMensagem(DATA_POSTERIOR_A, campo, fmt.formatarData(dataMin.getTime()));
				}
			} else if (min == null & max != null) {
				dataMax.setTime((Date) max);
				dataMax = descartarHoras(dataMax);
				
				if (data.after(dataMax)) {
					lista.addMensagem(DATA_ANTERIOR_A, campo, fmt.formatarData(dataMax.getTime()));
				}
			}
		} else if (obj instanceof Integer) {
			int inteiro = (Integer) obj;
			if (min != null & max != null) {
				if (min instanceof Integer & max instanceof Integer) {
					int intMax = (Integer) max;
					int intMin = (Integer) min;
					if (inteiro < intMin || inteiro > intMax) {
						lista.addMensagem(VALOR_MAIOR_QUE_MENOR_QUE, campo, intMin, intMax);
					}
				}
			} else if (min != null & max == null) {
				int intMin = (Integer) min;
				if (inteiro < intMin) {
					lista.addMensagem(VALOR_MAIOR_IGUAL_A, campo, intMin);
				}
			} else if (min == null & max != null) {
				int intMax = (Integer) max;
				if (inteiro > intMax) {
					lista.addMensagem(VALOR_MENOR_IGUAL_A, campo, intMax);
				}
			}
		} else if (obj instanceof Double) {
			double valor = (Double) obj;
			if (min != null & max != null) {
				if (min instanceof Double & max instanceof Double) {
					double valorMax = (Double) max;
					double valorMin = (Double) min;
					if (valor < valorMin || valor > valorMax) {
						lista.addMensagem(VALOR_MAIOR_QUE_MENOR_QUE, campo, valorMin, valorMax);
					}
				}
			} else if (min != null & max == null) {
				double intMin = (Double) min;
				if (valor < intMin) {
					lista.addMensagem(VALOR_MAIOR_IGUAL_A, campo, intMin);
				}
			} else if (min == null & max != null) {
				double valorMax = (Double) max;
				if (valor > valorMax) {
					lista.addMensagem(VALOR_MENOR_IGUAL_A, campo, valorMax);
				}
			}
		}
	}

	/** Valida se o obj é igual ou maior que val.
	 * @param obj
	 * @param val
	 * @param campo
	 * @param lista
	 */
	public static void validateMinValue(Object obj, Object val, String campo, ListaMensagens lista) {
		validateRange(obj, val, null, campo, lista);
	}

	@Deprecated
	public static void validateMinValue(Object obj, Object val, String campo, Collection<MensagemAviso> lista) {
		validateRange(obj, val, null, campo, new ListaMensagens(lista));
	}

	/** Valida se o obj é menor ou igual que val.
	 * @param obj
	 * @param val
	 * @param campo
	 * @param lista
	 */
	public static void validateMaxValue(Object obj, Object val, String campo, ListaMensagens lista) {
		validateRange(obj, null, val, campo, lista);
	}

	/**
	 * Valida a hora.
	 *
	 * @param hora
	 * @param campo
	 * @param lista
	 * @return
	 */
	public static Date validaHora(String hora, String campo, ListaMensagens lista) {
		Date data = Formatador.getInstance().parseHora(hora);
		if (data == null) {
			lista.addMensagem(FORMATO_INVALIDO, campo);
		}
		return data;
	}

	public static Date validaHora(String hora, String campo, Collection<MensagemAviso> lista) {
		return validaHora(hora, campo, new ListaMensagens(lista));
	}

	/** Valida o tamanho máximo da String.
	 * @param s
	 * @param valor
	 * @param campo
	 * @param lista
	 */
	public static void validateMaxLength(String s, int valor, String campo, ListaMensagens lista) {
		if (s != null && s.length() > valor) {
			lista.addMensagem(MAXIMO_CARACTERES, campo, valor);
		}
	}

	/** Valida o tamanho mínimo da String.
	 * @param s
	 * @param valor
	 * @param campo
	 * @param lista
	 */
	public static void validateMinLength(String s, int valor, String campo, ListaMensagens lista) {
		if (s != null && s.length() < valor) {
			lista.addMensagem(MINIMO_CARACTERES, campo, valor);
		}
	}

	@Deprecated
	public static void validateMaxLength(String s, int valor, String campo, Collection<MensagemAviso> lista) {
		validateMaxLength(s, valor, campo, new ListaMensagens(lista));
	}

	/** Lista de abreviações válidas.
	 *
	 */
	private static ArrayList<String> abreviacoesValidas = new ArrayList<String>();
	static {
		abreviacoesValidas.add("");
	}

	/**
	 * Valida se o nome tem abreviação.
	 * @param nome
	 * @param campo
	 * @param lista
	 */
	public static void validateAbreviacao(String nome, String campo, ListaMensagens lista){
		boolean abreviacao = false;

		if( nome != null && !nome.trim().equals("")){

			// antes o método era mais elaborado, simplifiquei só para testar se há pontos.
			if ( nome.contains(".") ) {
				abreviacao = true;
			}

			if(abreviacao)
				lista.addMensagem(CAMPO_SEM_ABREVIACOES, campo);
		}
	}

	/**
	 * Valida se o campo de texto contem lero-lero.
	 * @param texto string a ser validada
	 * @param campo nome do campo que aparecerá na mensagem de erro
	 * @param tamanhoMinimo tamanho mínimo que o texto deve conter
	 * @param minimoCaracteres mínimo de caracteres DIFERENTES que o texto deve conter
	 * @param minimoPalavras mínimo de palavras que o texto deve conter
	 * @param lista
	 */
	public static void validateLeroLero( String texto, String campo, int tamanhoMinimo, int minimoCaracteres, int minimoPalavras, ListaMensagens lista){
		String msg = StringUtils.containsLeroLero(texto, tamanhoMinimo, minimoCaracteres, minimoPalavras);
		if( msg != null ){
			lista.addMensagem(CONTEUDO_INVALIDO, campo);
		}
	}

	/** Valida o CEP informado.
	 * @param numero
	 * @param campo
	 * @param lista
	 * @return
	 */
	public static int validateCEP(String numero, String campo, ListaMensagens lista) {
		if (isEmpty(numero)) return 0;
		String soNumeros = Formatador.getInstance().parseRG(numero);
		try {
			int numeros = Integer.parseInt(soNumeros);
			if (numeros == 0){
				lista.addMensagem(FORMATO_INVALIDO, campo);
				return 0;
			}
			return numeros;
		} catch (NumberFormatException e) {
			lista.addMensagem(FORMATO_INVALIDO, campo);
			return 0;
		}
	}

	/**
	 * Valida um objeto buscando por anotações de validação nele e coloca
	 * as mensagens de erro na lista de mensagens passada como parâmetro.
	 *
	 * @param obj
	 * @param lista
	 */
	public static void validateEntity(Object obj, ListaMensagens lista) {
		AnnotationValidation.validate(obj, lista);
	}

	/**
	 * Valida um objeto buscando por anotações de validação nele e
	 * retorna uma lista de mensagens com as mensagens de erro
	 * encontradas.
	 *
	 * @param obj
	 * @return
	 */
	public static ListaMensagens validateEntity(Object obj) {
		ListaMensagens lista = new ListaMensagens();
		validateEntity(obj, lista);
		return lista;
	}

	/**
	 * Valida se a data passada como parâmetro está no passado, ou seja, se é menor
	 * que a data atual.
	 * @param date
	 * @param fieldName
	 * @param lista
	 */
	public static void validaDataAnteriorIgual(Date date, Date comparacao, String fieldName, ListaMensagens lista) {
		if (!isEmpty(date)) {
			if(date.after(comparacao) && !DateUtils.isSameDay(date, comparacao))
				lista.addMensagem(DATA_ANTERIOR_IGUAL, fieldName, new SimpleDateFormat("dd/MM/yyyy").format(comparacao));
		}
	}

	/**
	 * Valida se a data passada como parâmetro está no passado, ou seja, se é menor
	 * que a data atual.
	 * @param date
	 * @param fieldName
	 * @param lista
	 */
	public static void validatePast(Date date, String fieldName, ListaMensagens lista) {
		if (!isEmpty(date)) {
			if(date.after(new Date()))
				lista.addMensagem(DATA_ANTERIOR_A, fieldName, new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
		}
	}

	/**
	 * Valida se a data passada como parâmetro está no futuro, ou seja, se é maior
	 * que a data atual.
	 * @param date
	 * @param fieldName
	 * @param lista
	 */
	public static void validateFuture(Date date, String fieldName, ListaMensagens lista) {
		if (!isEmpty(date)) {
			if(date.before(new Date()))
				lista.addMensagem(DATA_POSTERIOR_A, fieldName, new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
		}
	}

	/**
	 * Valida se a URL passada como parâmetro está em um formato válido.
	 * @param fieldValue
	 * @param fieldName
	 * @param lista
	 */
	public static void validateUrl(String url, String fieldName, ListaMensagens lista) {
		if (!new UrlValidator().isValid(url))
			lista.addMensagem(URL_INVALIDA, fieldName);
	}

	/**
	 * Verifica se uma String passada como parâmetro casa com a expressão regular passada
	 * como parâmetro.
	 * @param value
	 * @param pattern
	 * @param fieldName
	 * @param lista
	 */
	public static void validatePattern(String value, String pattern, String fieldName, ListaMensagens lista) {
		if (!Pattern.matches(pattern, value)) {
			lista.addMensagem(FORMATO_INVALIDO, fieldName);
		}
	}
	
	/**
	 * Verifica se o arquivo passado como parâmetro possui uma extensão permitida. Se não possuir, adiciona um erro
	 * na lista de mensagens passada como parâmetro.
	 * @param file
	 * @param extensoesPermitidas
	 */
	public static void validaExtensaoArquivo(ListaMensagens lista, UploadedFile file, String... extensoesPermitidas) {
		String extensao = FilenameUtils.getExtension(file.getName());
		boolean valido = false;
		for (String permitida : extensoesPermitidas) {
			if (permitida.equalsIgnoreCase(extensao)) {
				valido = true;
				break;
			}
		}
		
		if (!valido) {
			lista.addErro("O arquivo enviado não possui uma extensão válida. As extensões permitidas são: " + StringUtils.transformaEmLista(Arrays.asList(extensoesPermitidas)) + ".");
		}
	}
	
	/**
	 * Verifica se o arquivo passado como parâmetro possui uma extensão permitida. Se não possuir, dispara 
	 * uma exceção.
	 * 
	 * @param file
	 * @param extensoesPermitidas
	 * @throws NegocioException
	 */
	public static void validaExtensaoArquivo(UploadedFile file, String... extensoesPermitidas) throws NegocioException {
		ListaMensagens lista = new ListaMensagens();
		validaExtensaoArquivo(lista, file, extensoesPermitidas);
		if (!lista.isEmpty())
			throw new NegocioException(lista);
	}
	
	/**
	 * Verifica se o arquivo passado como parâmetro possui um content type permitido. Se não possuir, adiciona um erro
	 * na lista de mensagens passada como parâmetro.
	 * @param file
	 * @param contentTypesPermitidos
	 */
	public static void validaFormatoArquivo(ListaMensagens lista, UploadedFile file, String... contentTypesPermitidos) {
		boolean valido = false;
		for (String permitido : contentTypesPermitidos) {
			if (permitido.equalsIgnoreCase(file.getContentType())) {
				valido = true;
				break;
			}
		}
		
		if (!valido) {
			lista.addErro("O arquivo enviado não possui um conteúdo válido. Os tipos de conteúdo permitidos são: " + StringUtils.transformaEmLista(Arrays.asList(contentTypesPermitidos)) + ".");
		}
	}
	
	/**
	 * Verifica se o arquivo passado como parâmetro possui um content type permitido. Se não possuir, dispara
	 * uma exceção.
	 * 
	 * @param file
	 * @param contentTypesPermitidos
	 * @throws NegocioException
	 */
	public static void validaFormatoArquivo(UploadedFile file, String... contentTypesPermitidos) throws NegocioException {
		ListaMensagens lista = new ListaMensagens();
		validaFormatoArquivo(lista, file, contentTypesPermitidos);
		if (!lista.isEmpty())
			throw new NegocioException(lista);
	}
	
	/** 
	 * Valida o NIT ou o número inscrição do PIS/PASEP de um trabalhador.
	 * Todos seguem a mesma numeração, com 11 dígitos sendo o último o dígito verificador.
	 * <br/><br/>
	 * - NIT: Número de Identificação do Trabalhador<br/>
	 * - PIS: Programa de Integração Social <br/> 
	 * - PASEP: Programa de Formação do Patrimônio do Servidor Público<br/>
	 * @param numero
	 * @param campo
	 * @param lista
	 * @return
	 */
	public static boolean validateNITPISPASEP(Long numero, String campo, ListaMensagens lista) {
		
		return validateNITPISPASEP( String.valueOf(numero), campo, lista);
	}
	
	/** 
	 * Valida o NIT ou o número inscrição do PIS/PASEP de um trabalhador.
	 * Todos seguem a mesma numeração, com 11 dígitos sendo o último o dígito verificador.
	 * <br/><br/>
	 * - NIT: Número de Identificação do Trabalhador<br/>
	 * - PIS: Programa de Integração Social <br/> 
	 * - PASEP: Programa de Formação do Patrimônio do Servidor Público<br/>
	 * @param numero
	 * @param campo
	 * @param lista
	 * @return
	 */
	public static boolean validateNITPISPASEP(String numero, String campo, ListaMensagens lista) {
		
		if (isEmpty(numero)) {
			lista.addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, campo);
			return false;
		}
				
		if (!validaNITPISPASEP(numero)) {
			lista.addMensagem(FORMATO_INVALIDO, campo);
			return false;
		}

		return true;
	}
	
	/** 
	 * Valida o NIT ou o número inscrição do PIS/PASEP de um trabalhador.
	 * Todos seguem a mesma numeração, com 11 dígitos sendo o último o dígito verificador.
	 * <br/><br/>
	 * - NIT: Número de Identificação do Trabalhador<br/>
	 * - PIS: Programa de Integração Social <br/> 
	 * - PASEP: Programa de Formação do Patrimônio do Servidor Público<br/>
	 * @param numero
	 * @return
	 */
	public static boolean validaNITPISPASEP(String numero) {
		
		String numeroLimpo = numero.replaceAll("[\\D]+", "");
		
		if (numeroLimpo.length() == 11){
			
			int[] multiplicador = new int[] { 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };
			
			int soma = 0;
			int resto = 0;
			int dv;
			
			//0~9
			for (int i = 0; i < 10; i++){
				soma += Integer.parseInt(numeroLimpo.charAt(i) + "") * multiplicador[i]; 
			}
			
			resto = soma % 11;

			dv = 11 - resto;
			
			if ( dv > 9 )
				dv = 0;
			
			//Caso o resultado seja igual ao ultimo dígito (o dígito verificador do numero informado)
			if(dv == Integer.parseInt(numeroLimpo.charAt(10) + ""))
				return true;
		}
		
		return false;
	}
	
	
	/** 
	 * Valida o campo ISBN para, já que este não possui exatamente um formato.
	 * @param numero
	 * @return
	 */
	public static boolean validaISBN(String numero){
		
		//Verifica se numero possuem digitos até seu penultimo valor.
	    boolean isInteger = true;  
	    int size = numero.length();  
	    for(int i = 0; (i < (size - 1)) && isInteger; i ++) {  
	       isInteger = Character.isDigit(numero.charAt(i));
	       if(isInteger == false){
	    	   return false;
	       }
	    }
	    
	    //numero limpo deve ter 9 (em caso de "X"), 10 (caso sem "X"), 12 digitos(caso de "X") ou 13 digitos(caso sem "X").
	    String numeroLimpo = numero.replaceAll("[\\D]+", "");
	  	int totalLimpo = numeroLimpo.length();
	  	if(!(totalLimpo == 9 || totalLimpo == 10 || totalLimpo == 12 || totalLimpo == 13)){
	  		return false;
	  	}
	    
	    if(numero.length() == 10 || numero.length() == 13){
	    	
	    	String ultimoValor = String.valueOf(numero.charAt(numero.length() - 1));
	    	ultimoValor.toUpperCase();
	    	try{
	         //Tenta converter o ultimo valor para int.
	    	 Integer.parseInt(ultimoValor);	
	    	}catch (Exception e) {
	    		//Se o ultimo valor não for inteiro, verifique se ele é diferente de X.
	    		if(!ultimoValor.equals("X")){
		    		return false;
		    	}	
			}
	    	
	    }else{
	    	return false;
	    }
		
		return true;
	}
}