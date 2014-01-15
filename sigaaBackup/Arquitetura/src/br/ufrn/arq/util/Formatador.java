/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 30/09/2004
 */
package br.ufrn.arq.util;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Classe responsável por fazer a formatação de valores para apresentaçaão na
 * visualização.
 *
 * @author Gleydson Lima
 * @author Johnny Marçal
 *
 */
public class Formatador {

	private static Formatador singleton;

	/**
	 * Mantem formatos para datas e horas.
	 */
	private SimpleDateFormat df, dfH, dfS, dfE, dfR;

	/**
	 * Formato para números.
	 */
	private DecimalFormat dc;
	private DecimalFormat dc1;
	private DecimalFormat dc3;
	private DecimalFormat dc4;
	private DecimalFormat dc5;
	private DecimalFormat dc6;
	private DecimalFormat dc7;
	private DecimalFormat dc8;
	
	/**
	 * Formato para tempo (HH:mm).
	 */
	private SimpleDateFormat horaF;

	public String[] MESES = { "Janeiro", "Fevereiro", "Março", "Abril", "Maio",
			"Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro",
			"Dezembro" };

	public String[] MESES_ABREVIADO = { "Jan.", "Fev.", "Mar.", "Abr.", "Mai.",
			"Jun.", "Jul.", "Ago.", "Set.", "Out.", "Nov.",
			"Dez." };
	
	public String[] INICIAIS_DIAS_DA_SEMANA = {"D", "S", "T", "Q", "Q", "S", "S"};

	/**
	 * Contrutor Padrão
	 */
	private Formatador() {
		df = new SimpleDateFormat("dd/MM/yyyy");
		df.setLenient(false);
		dfR = new SimpleDateFormat("dd/MM/yy");
		dfH = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		dfE = new SimpleDateFormat("dd 'de' MMMMM 'de' yyyy");
		dfS = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		dc = new DecimalFormat("#,##0.00");
		dc1 = new DecimalFormat("#,##0.0");
		dc3 = new DecimalFormat("#,##0.000");
		dc4 = new DecimalFormat("#,##0.0000");
		dc5 = new DecimalFormat("####.##");
		dc6 = new DecimalFormat("000,000");
		dc7 = new DecimalFormat("#,##0.00000");
		dc8 = new DecimalFormat("#,##0.000000");

		horaF = new SimpleDateFormat("HH:mm");
		
	}

	/**
	 * Retorna uma instância do Formatador. Singleton.
	 *
	 * @return
	 */
	public static Formatador getInstance() {
		if (singleton == null) {
			singleton = new Formatador();
		}
		return singleton;
	}

	/**
	 * Formata data no formato dd/MM/yyyy
	 */
	public String formatarData(Date data) {
		return (data == null ? "" : df.format(data));
	}

	/**
	 * Formata data por extenso
	 */
	public String formatarDataExtenso(Date data) {
		return (data == null ? "" : dfE.format(data));
	}

	/**
	 * Recebe a quantidade de dias e transforma em anos, mes e dia
	 *
	 * @param qtdDias
	 * @return
	 */
	public String descreverTempoAnoMesesDias(int qtdDias){
		String descricao = "";

		int quantidadeDias = qtdDias;

		int anos = quantidadeDias / 365;
		quantidadeDias = quantidadeDias - (anos * 365);

		if(quantidadeDias >= 360)
			quantidadeDias = 359;

		int meses = quantidadeDias / 30;
		quantidadeDias = quantidadeDias - (meses * 30);
		
		if (anos > 0) {
			if(meses == 12){
				anos++;
				meses = 0;
			}
			descricao = anos + " ano(s)";
		}

		if (anos > 0 && meses > 0 && quantidadeDias > 0) {
			descricao = descricao + ", ";

		}else if (anos > 0 && meses > 0) {
			descricao = descricao + " e ";

		}else{
			descricao += " ";
		}

		if (meses > 0) {
			descricao = descricao + meses + " mes(es) ";
		}

		if ((anos > 0 || meses > 0) && quantidadeDias > 0) {
			descricao = descricao + "e ";
		}

			if (quantidadeDias > 0) {
				descricao = descricao + quantidadeDias + " dia(s)";

			}else if(quantidadeDias == 0){
				if(!(meses > 0 || anos > 0)){
					descricao = "0 dias";
				}
			}

		return descricao;
	}

	/**
	 * Retorna a data contida na String. Caso a data seja inválida, é retornado
	 * null
	 *
	 * @param data
	 * @return
	 */
	public Date parseDate(String data) {
		try {
			Date d1 = null;
			if( data.length() ==10 )
				d1 = df.parse(data);
			else
				d1 = dfR.parse(data);
			Date d2 = df.parse("01/01/1900");
			if (d1.before(d2)) {
				return null;
			} else {
				return d1;
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Validar hora
	 *
	 * @param hora
	 * @return
	 */
	public Date parseHora(String hora) {
		try {
			horaF.setLenient(false);
			return horaF.parse(hora);
		} catch (Exception e) {
			return null;
		} finally{
			horaF.setLenient(true);
		}
	}

	/**
	 * Retorna String com representação do mês informado como argumento. <BR>
	 * 0 - janeiro <br>
	 * 11 - Dezembro
	 *
	 * @param mes
	 *            inteiro representado mês.
	 * @return representação do mês.
	 */
	public String formatarMes(int mes) {
		if (mes < 0 || mes > 11)
			throw new IllegalArgumentException("O mês deve estar entre 0 e 11");

		return MESES[mes];
	}
	
	/**
	 * Retorna String com representação (apenas a letra inicial) do dia da semana informado como argumento.
	 *
	 * @param data.
	 * @return letra que representa o dia da semana.
	 */
	public String getLetraInicialDiaSemana(Date data) {
		int diaSemana = CalendarUtils.getDiaSemanaByData(data);
		if (diaSemana < 1)
			throw new IllegalArgumentException("Data deve ser informada.");

		return INICIAIS_DIAS_DA_SEMANA[diaSemana - 1];
	}

	/**
	 * Retorna String com representação do mês informado como argumento de forma abreviada. <BR>
	 * 0 - janeiro <br>
	 * 11 - Dezembro
	 *
	 * @param mes
	 *            inteiro representado mês.
	 * @return representação do mês abreviada.
	 */
	public String formatarMesAbreviado(int mes) {
		if (mes < 0 || mes > 11)
			throw new IllegalArgumentException("O mês deve estar entre 0 e 11");

		return MESES_ABREVIADO[mes];
	}

	/**
	 * Sobrecarga de {@see #formatarMes(int)}.
	 *
	 * @param mes
	 *            String com número representando mês
	 * @return representação do mês.
	 *
	 * @see #formatarMes(int)
	 */
	public String formatarMes(String mes) {
		return formatarMes(Integer.parseInt(mes));
	}

	/**
	 * Formata moeda com máscara #,##0.00
	 *
	 * @param valor
	 * @return
	 */
	public String formatarMoeda(double valor) {
		return dc.format(valor);
	}

	/**
	 * Formata moeda com máscara #,##0.00
	 *
	 * @param valor
	 * @return
	 */
	public String formatarMoeda(Double valor) {
		return dc.format(valor.doubleValue());
	}

	/**
	 * Formata moeda com máscara #,##0.00 e símbolo monetário brasileiro.
	 *
	 * @param valor
	 * @return
	 */
	public String formatarMoedaComSimboloMonetarioBrasil(Double valor) {
		return formatarMoedaComSimboloMonetario(valor, "pt", "BR");
	}
	
	/**
	 * Formata moeda com máscara #,##0.00 e símbolo monetário de acordo 
	 * com os parâmetros.
	 * Para mais informações consultar: http://docs.oracle.com/javase/1.5.0/docs/guide/intl/locale.doc.html
     *
	 * @param valor
	 * @param idioma
	 * @param siglaPais
	 * @return
	 */
	public String formatarMoedaComSimboloMonetario(Double valor, String idioma, String siglaPais) {
		return Currency.getInstance(new Locale(idioma,siglaPais)).getSymbol()+" "+dc.format(valor.doubleValue());
	}
	
	/**
	 * Formata número decimal com uma casa
	 *
	 * @param valor
	 * @return número formatado com uma casa decimal
	 */
	public String formatarDecimal1(double valor) {
		return dc1.format(valor);
	}

	/** Formata Decimal com ,00 opcional, caso o número seja integral */
	public String formatarDecimalInt(double valor) {
		return dc5.format(valor);
	}

	/** Formata números naturais */
	public String formatarNatural(int valor) {
		return dc6.format(valor);
	}

	/** Formata números naturais */
	public String formatarNatural(long valor) {
		return dc6.format(valor);
	}

	/**
	 * Formata moeda com 3 casas decimais
	 *
	 * @param valor
	 * @return
	 */
	public String formatarMoeda3(double valor) {
		return dc3.format(valor);
	}

	/**
	 * Formata moeda com 3 casas decimais
	 *
	 * @param valor
	 * @return
	 */
	public String formatarMoeda3(Double valor) {
		return dc3.format(valor.doubleValue());
	}

	/**
	 * Formata moeda com 4 casas decimais
	 *
	 * @param valor
	 * @return
	 */
	public String formatarMoeda4(double valor) {
		return dc4.format(valor);
	}

	/**
	 * Formata moeda com 4 casas decimais
	 *
	 * @param valor
	 * @return
	 */
	public String formatarMoeda4(Double valor) {
		return dc4.format(valor.doubleValue());
	}

	/**
	 * Formata moeda com máscara #,##0.00000
	 */
	public String formatarMoeda5(double valor) {
		return dc7.format(valor);
	}

	/**
	 * Formata moeda com máscara #,##0.00000
	 */
	public String formatarMoeda5(Double valor) {
		return dc7.format(valor.doubleValue());
	}

	/**
	 * Formata moeda com máscara #,##0.000000
	 *
	 * @param valor
	 * @return
	 */
	public String formatarMoeda6(double valor) {
		return dc8.format(valor);
	}

	/**
	 * Formata moeda com máscara #,##0.000000
	 *
	 * @param valor
	 * @return
	 */
	public String formatarMoeda6(Double valor) {
		return dc8.format(valor.doubleValue());
	}

	/**
	 * Formatar hora com HH:mm
	 * @param data
	 * @return
	 */
	public String formatarHora(Date data) {
		try {
			return horaF.format(data);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Formata data e hora. Máscara: dd/MM/yyyy HH:mm
	 *
	 * @param data
	 * @return
	 */
	public String formatarDataHora(Date data) {
		return dfH.format(data);
	}

	/**
	 * Formata data, hora e segundo. Máscara: dd/MM/yyyy HH:mm:ss
	 *
	 * @param data
	 * @return
	 */
	public String formatarDataHoraSec(Date data) {
		return dfS.format(data);
	}

	/**
	 * Formata data no Padrão: dd de MM de yyyy
	 * @param data
	 * @return
	 */
	public String formatarDataDiaMesAno(Date data) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		int dia = calendar.get(Calendar.DATE);
		int mes = calendar.get(Calendar.MONTH);
		int ano = calendar.get(Calendar.YEAR);

		return dia + " de " + Formatador.getInstance().formatarMes(mes)
				+ " de " + ano;
	}

	/**
	 * Retorna o dia de uma data
	 *
	 * @param data
	 * @return
	 */
	public String formatarDataDia(Date data) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		int dia = calendar.get(Calendar.DATE);

		return String.valueOf(dia);
	}

	/**
	 * Formatar cpf
	 *
	 * @param cpf_cnpj
	 * @return
	 */
	public String formatarCPF_CNPJ(long cpf_cnpj) {
		try {

			int size = String.valueOf(cpf_cnpj).length();
			if (size <= 11) {

				// Formata CPF

				String cpfFormatado = formatarCPF(cpf_cnpj);

				if (ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(cpfFormatado)) {
					return cpfFormatado;
				} else if (ValidadorCPFCNPJ.getInstance().validaCNPJ(cpfFormatado)) { 
					return formatarCNPJ(cpf_cnpj); 
				} else { 
					String cnpjFormatado = formatarCNPJ(cpf_cnpj);
					if(ValidadorCPFCNPJ.getInstance().validaCNPJ(cnpjFormatado))
						return cnpjFormatado;
					else
						return String.valueOf(cpf_cnpj); 
				}


			} else {

				// Formata CNPJ

				return formatarCNPJ(cpf_cnpj);
			}

		} catch (Exception e) {
			return String.valueOf(cpf_cnpj);
		}
	}

	/**
	 * Formatar CPF
	 *
	 * @param cpf
	 * @return
	 */
	public String formatarCPF(long cpf) {

		try {

			StringBuffer id = new StringBuffer(String.valueOf(cpf));

			if (id.length() < 11) {

				for (int a = id.length(); a < 11; a++) {
					id.insert(0, "0");
				}
			}

			int size = id.length();

			id.insert(size - 2, "-");
			id.insert(size - 5, ".");
			id.insert(size - 8, ".");

			return id.toString();

		} catch (Exception e) {
			return String.valueOf(cpf);
		}

	}

	/**
	 * Formatar cnpj
	 *
	 * @param cnpj
	 * @return
	 */
	public String formatarCNPJ(long cnpj) {

		try {

			StringBuffer id = new StringBuffer(String.valueOf(cnpj));
			int size = id.length();

			// Formata CNPJ

			if (size < 18) {
				for (int a = size; a < 14; a++) {
					id.insert(0, "0");
				}
			}

			/*
			id.insert(sizeInicial - 2, "-");
			id.insert(sizeInicial - 6, "/");
			id.insert(sizeInicial - 9, ".");
			id.insert(sizeInicial - 12, ".");
			*/
			id.insert(12, "-");
			id.insert(8, "/");
			id.insert(5, ".");
			id.insert(2, ".");

			return id.toString();

		} catch (Exception e) {
			return String.valueOf(cnpj);
		}

	}

	/**
	 * Formata o código da unidade
	 *
	 * @param codigo
	 * @return
	 */
	public String formatarCodigoUnidade(long codigo) {

		try {
			String value = String.valueOf(codigo);
			String result = "";
			if (value.length() % 2 == 0) {
				for (int a = 0; a <= value.length() - 2; a += 2) {
					result += value.substring(a, a + 2);
					if (a != value.length() - 2) {
						result += ".";
					}
				}
			}
			return result;
		} catch (Exception e) {
			return String.valueOf(codigo);
		}

	}

	/**
	 * Retorna a representação em String no formato #####-### do cep passado
	 * como argumento.
	 *
	 * @param cep
	 * @return cep no formato #####-###.
	 */
	public String formatarCep(int cep) {
		try {
			StringBuffer cepSB = new StringBuffer( UFRNUtils.completaZeros(cep, 8 ));
			cepSB.insert(cepSB.length() - 3, '-');

			return cepSB.toString();
		} catch (Exception e) {
			return String.valueOf(cep);
		}
	}

	/**
	 * Checa se o cep contém só inteiros
	 *
	 * @param cep
	 * @return
	 */
	public int parseCep(String cep) {
		try {
			return new Integer(cep.replaceAll("-", ""));
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Este método retira pontos, traço e barras de um CPF ou CNPJ e o
	 * transforma em uma String
	 *
	 * @param req
	 */
	public String parseStringCPFCNPJ(String cpfString) {

		if (cpfString == null)
			return "";

		int tamString = cpfString.length();
		String cpfLimpo = "";

		for (int i = 0; i < tamString; i++) {

			if ((cpfString.charAt(i) != '-') && (cpfString.charAt(i) != '.')
					&& (cpfString.charAt(i) != '/')
					&& (cpfString.charAt(i) != ' ')) {

				cpfLimpo += cpfString.charAt(i);
			}
		}

		return cpfLimpo;
	}

	/**
	 * Este método retira pontos, traço e barras de um CPF ou CNPJ e o
	 * transforma em uma String
	 *
	 * @param req
	 */
	public long parseCPFCNPJ(String cpfString) {

		if (isEmpty(cpfString))
			return 0;

		int tamString = cpfString.length();
		String cpfLimpo = "";

		for (int i = 0; i < tamString; i++) {

			if ((cpfString.charAt(i) != '-') && (cpfString.charAt(i) != '.')
					&& (cpfString.charAt(i) != '/')
					&& (cpfString.charAt(i) != ' ')) {

				cpfLimpo += cpfString.charAt(i);
			}
		}

		return Long.parseLong(cpfLimpo);
	}

	/**
	 * Este método retira pontos, traço e barras de um RG ou CNPJ
	 * e transforma em uma String
	 *
	 * @param rgString
	 * @return
	 */
	public String parseRG(String rgString) {
		return parseStringCPFCNPJ(rgString);
	}

	/**
	 * retira "-" de um numero de telefone
	 * @param numero
	 * @return
	 */
	public String parseTelefone(String numero) {
		return parseStringCPFCNPJ(numero);
	}

	/**
	 * insere "-" em um numero de telefone no formato "####-####"
	 * @param numero
	 * @return
	 */
	public String formatarTelefone(int numero) {
		try {
			StringBuffer cepSB = new StringBuffer(String.valueOf(numero));
			cepSB.insert(cepSB.length() - 4, '-');

			return cepSB.toString();
		} catch (Exception e) {
			return String.valueOf(numero);
		}
	}

	/**
	 * Formata o número de um protocolo
	 *
	 * @deprecated Usar: formataNumeroProtocolo(int radical, int numero, int ano, int dv)
	 *
	 * @param numProcesso
	 * @param anoProcesso
	 * @return
	 */
	public String formataNumProtocolo(int numProcesso, int anoProcesso) {
		StringBuffer processoFormatado = new StringBuffer(ProtocoloUtil.numFixo);
		processoFormatado.append("."
				+ ProtocoloUtil.formataNumero(numProcesso) + "/");
		processoFormatado.append(anoProcesso
				+ "-"
				+ ProtocoloUtil.formataDV(ProtocoloUtil.calculaDV(Integer.parseInt(ProtocoloUtil.numFixo), numProcesso, anoProcesso)));

		return processoFormatado.toString();
	}
	
	/**
	 * Formata número de protocolo de acordo com a Portaria Nº 3 16-05-2003 SLTI-MP Nº de Processos.
	 * Considera radical, número, ano e dígito verificador.
	 * 
	 * @param radical
	 * @param numero
	 * @param ano
	 * @param dv
	 * @return
	 */
	public String formataNumeroProtocolo(int radical, int numero, int ano, int dv) {
		
		return ProtocoloUtil.formataRadical(radical) + "." +
			   ProtocoloUtil.formataNumero(numero) + "/" + 
			   ano + "-" +
			   ProtocoloUtil.formataDV(dv);
		
	}

	/**
	 * Valida o número passado como string e retorna um Double
	 *
	 * @param valor
	 * @return
	 * @throws NumberFormatException
	 */
	public double parseValor(String valor) throws NumberFormatException {
		try {

			if (valor == null)
				return 0.0;

			valor = valor.trim().replaceAll("\\.", "");
			valor = valor.trim().replaceAll(",", ".");

			if ("".equals(valor.trim()))
				return 0.0;

			return Double.parseDouble(valor);
		} catch (Exception e) {
			throw new NumberFormatException();
		}
	}

	/**
	 * Preenche um número com a quantidade de zeros informados
	 *
	 * @param qtd
	 * @param numero
	 * @return
	 */
	public String preencheZeros(int qtd, int numero) {

		StringBuffer buf = new StringBuffer(qtd);
		for (int a = 0; a < qtd; a++) {
			buf.append("0");
		}

		DecimalFormat df = new DecimalFormat(buf.toString());
		return df.format(numero);
	}

	/**
	 * Retorna o nome completo do significado da sigla do
	 * sexo.
	 *
	 * @param sexo
	 * @return
	 */
	public String formatarSexo(String sexo) {
		if ("m".equalsIgnoreCase(sexo)) return "Masculino";
		else if ("f".equalsIgnoreCase(sexo)) return "Feminino";
		return "";
	}

	/**
	 * Formata um valor booleano
	 *
	 * @param bool
	 * @param tipo
	 * @return
	 */
	public String formatarBoolean(boolean bool, String tipo) {
		if (bool) {
			if ("V/F".equalsIgnoreCase(tipo)) return "Verdadeiro";
			else if ("S/N".equalsIgnoreCase(tipo)) return "Sim";
		} else {
			if ("V/F".equalsIgnoreCase(tipo)) return "Falso";
			else if ("S/N".equalsIgnoreCase(tipo)) return "Não";
		}
		return null;
	}

	/**
	 * Coloca o número passado no padrão ano.periodo
	 *
	 * @param inteiro
	 * @return
	 */
	public String formatarAnoSemestre(Integer inteiro) {
		if (inteiro != null && inteiro > 10000) {
			int ano = inteiro / 10;
			int sem = inteiro - (ano*10);
			return ano + "." + sem;
		}
		return null;
	}

	/**
	 * Valida a hora
	 *
	 * @param dataHora
	 * @return
	 */
	public Date parseDataHoraSec(String dataHora) {

		try {
			return dfS.parse(dataHora);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	/**
	 * Converte um número arábico para algarismos romanos
	 *
	 * @param numero
	 * @return
	 */
	public String converteParaRomano(int numero){

    	if (numero < 1)
            throw new NumberFormatException("O número a ser convertido para romano deve ser maior que 1.");
         if (numero > 3999)
            throw new NumberFormatException("O número a ser convertido para romano deve ser menor que 3999.");

         int[]  numeros = { 1000,  900,  500,  400,  100,   90,
                 50,   40,   10,    9,    5,    4,    1 };

         String[] letras = { "M",  "CM",  "D",  "CD", "C",  "XC",
                 "L",  "XL",  "X",  "IX", "V",  "IV", "I" };

         String romano = "";

         for (int i = 0; i < numeros.length; i++) {
             while (numero >= numeros[i]) {
                romano += letras[i];
                numero -= numeros[i];
             }
          }

    	return romano;
    }

	/**
	 * Retorna o tempo no formato MM:SS a partir do tempo em segundos
	 * passado como parâmetro
	 *
	 * @param tempo
	 * @return
	 */
	public String formatarTempo(long tempo){
		return formatarTempo(tempo, true);
	}
	
	/**
	 * Retorna o tempo no formato HH:MM:SS a partir do tempo em segundos passado como parâmetro.
	 * Caso "considerarSegundos = false", o formato exibido será HH:MM.
	 * 
	 * @param tempo
	 * @param considerarSegundos
	 * @return
	 */
	public String formatarTempo(long tempo, boolean considerarSegundos){

		boolean horaNegativa = tempo < 0;
		
		if(horaNegativa)
			tempo = Math.abs(tempo);
		
		long horas = tempo / 3600;

		tempo = tempo -(horas * 3600);

		long minutos = tempo / 60;

		tempo = tempo - (minutos * 60);

		if(minutos > 60){
			long horasAux = (minutos/60);

			horas = horas + horasAux;

			minutos = minutos - (horasAux * 60);

		}

		long segundos = tempo ;

		StringBuffer duracao = new StringBuffer();

		if(horas < 10){
			duracao.append("0"+horas);
		}else{
			duracao.append(horas);
		}

		duracao.append(":");

		if(minutos < 10){
			duracao.append("0"+minutos);
		}else{
			duracao.append(minutos);
		}

		if(considerarSegundos){
			duracao.append(":");
	
			if(segundos < 10){
				duracao.append("0"+segundos);
			}else{
				duracao.append(segundos);
			}
		}

		if(horaNegativa)
			duracao.insert(0, "-");
		
		return duracao.toString();
	}
	
	/** Formata o nome de uma pessoa para um nome com iniciais em maiúscula. Ex.: JOÃO DA SILVA -> João da Silva. 
	 * @param nome
	 * @return
	 */
	public String formataNomePessoa(String nome) {
		if (nome == null) return null;
		nome = nome.replaceAll("`", "'");
		nome = nome.replaceAll("´", "'");
		StringBuilder novoNome = new StringBuilder();
		// capitaliza as iniciais
		StringTokenizer tokenizer = new StringTokenizer(nome.toLowerCase());
		while (tokenizer.hasMoreTokens()) {
			novoNome.append(" ");
			String token = tokenizer.nextToken();
			// D'Arc, D'Aguiar, etc...
			if (token.startsWith("d'")) {
				novoNome.append("D'").append(StringUtils.capitalize(token.substring(2)));
			} else if ("de".equals(token) ||
				"da".equals(token) ||
				"do".equals(token) ||
				"das".equals(token) ||
				"dos".equals(token) ||
				"e".equals(token) ||
				"em".equals(token)) {
				novoNome.append(token);
			} else {
				novoNome.append(StringUtils.capitalize(token));
			}
		}
		return novoNome.toString().trim().replaceAll("  ", " ");
	}
	
	/**
	 * Formata uma string com uma mascara passada por parâmetro. 
	 * Ex.: #####-####, ###.###.###-##, ######.#######/####-##
	 * 
	 * @param formatoMascara formato da mascara
	 * @param valorString string a ser "mascarado"
	 * @return
	 */
	public String formataMascara(String formatoMascara, String valorString){
		/*
         * Não retorna a mascara se a string for nulo ou vazia
         */
        if (valorString == null || valorString.trim().equals(""))
            return "";

        /*
         * Formata valor com a mascara passada
         */
        for(int i = 0; i < valorString.length(); i++){
            formatoMascara = formatoMascara.replaceFirst("#", valorString.substring(i, i + 1));
        }

        /*
         * Subistitui por string vazia os digitos restantes da mascara
         * quando o valor passado é menor que a mascara
         */
        return formatoMascara.replaceAll("#", "");
	}
	
	/**
	 * Valida o NIT ou o número inscrição do PIS/PASEP de um trabalhador.
	 * Todos seguem a mesma numeração, com 11 dígitos sendo o último o dígito verificador.
	 * <br/><br/>
	 * Formato: 999.99999.99-9
	 * <br/><br/>
	 * - NIT: Número de Identificação do Trabalhador<br/>
	 * - PIS: Programa de Integração Social <br/> 
	 * - PASEP: Programa de Formação do Patrimônio do Servidor Público<br/>
	 * @param numero
	 * @return
	 */
	public String formatarNITPISPASEP(long numero) {

		try {

			StringBuffer numeroString = new StringBuffer(String.valueOf(numero));

			int size = numeroString.length();
			
			if (numeroString.length() < 11) {

				for (int a = size; a < 11; a++) {
					numeroString.insert(0, "0");
				}
			}
			
			numeroString.insert(10, "-");
			numeroString.insert(8, ".");
			numeroString.insert(3, ".");
			
			return numeroString.toString();

		} catch (Exception e) {
			return String.valueOf(numero);
		}
	}
}