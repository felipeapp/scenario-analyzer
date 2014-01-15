/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 13/10/2004
 */
package br.ufrn.arq.web.tags;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Extenso;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.StringUtils;

/**
 *
 * Tag Library para chamar formatador de números e datas
 *
 * @author Gleydson Lima
 *
 */
public class FormatTag extends TagSupport {

	/** Nome do bean que terá uma propriedade formatada */
	private String name;

	/** Propriedade que será formatada */
	private String property;

	/** Tipo de formatação a ser realizada (ex: valor, data, cpf, etc.) */
	private String type;

	/** Escopo do bean */
	private String scope;

	/** Utilizar cores para diferenciar números positivos de negativos no caso de formatação por valor. */
	private String cor;

	/** Valor a ser formatado */
	private Object valor;
	
	/** Dias a serem somados a uma data que será formatada */
	private String dias;
	
	/** Indica se o que está sendo formatado é uma movimentação financeira ou não. */
	private boolean movimento;

	/** Especifica a quantidade de caracteres que deve gerar um <BR> */
	private int lineWrap = 0;

	/** Especifica a quantidade máxima de caracteres a ser exibida. */
	private int length = -1;
	
	/** Palavra a ser destacada no texto */
	private String palavraChave;
	
	/**
	 * Metodo manipulador da taglibrary
	 */
	@Override
	public int doStartTag() throws JspException {

		Object obj = null;

		try {
			Object atual;
			if (valor == null) {
				if (scope != null) {
					if (scope.compareTo("session") == 0) {
						obj = pageContext.getSession().getAttribute(name);
					}

					if (scope.compareTo("request") == 0) {
						obj = pageContext.findAttribute(name);
					}

				} else if (name != null){
					obj = pageContext.findAttribute(name);
				}

				if ( obj == null ) {

					return super.doStartTag();
				}
				atual = obj;

				if (property != null) {

					atual = ReflectionUtils.evalPropertyObj(obj, property);
				}
			} else
				atual = valor;

			Boolean credito = Boolean.FALSE;

			if (type.toLowerCase().equals("valor")) {

				if (cor != null) {
					if (cor.equals("true")) {
						String color = "blue";
						color = ((Number) atual).doubleValue() < 0 ? "red"
								: color;
						pageContext
								.getOut()
								.println(
										"<font color="
												+ color
												+ ">"
												+ Formatador
														.getInstance()
														.formatarMoeda(
																Double
																		.parseDouble(String
																				.valueOf(atual)))
												+ "</font>");
					}
				}else{
					if (movimento) {
						credito = (Boolean) ReflectionUtils.getMethodReturnValue(obj, "isCredito");
                        if (credito.booleanValue()) {
                            pageContext.getOut().print("<font color=blue>");
                        } else {
                            pageContext.getOut().print("<font color=red>");
                        }
                    }
				}
				if ((cor == null) || (cor.equals("false"))) {
					pageContext.getOut().print(
							Formatador.getInstance().formatarMoeda(
									Double.parseDouble(String.valueOf(atual))));
				}

			}

			// Formata valor com apenas 1 casa decimal
			if (type.toLowerCase().equals("valor1")) {
				pageContext.getOut().print(
						Formatador.getInstance().formatarDecimal1(
								Double.parseDouble(String.valueOf(atual))));
			}

			/* Formata valor com 3 casas decimais */
			if (type.toLowerCase().equals("valor3")) {
				pageContext.getOut().print(
						Formatador.getInstance().formatarMoeda3(
								Double.parseDouble(String.valueOf(atual))));
			}

			if (type.toLowerCase().equals("valor4")) {
				pageContext.getOut().print(
						Formatador.getInstance().formatarMoeda4(
								Double.parseDouble(String.valueOf(atual))));
			}

			if (type.toLowerCase().equals("valor5")) {
				pageContext.getOut().print(
						Formatador.getInstance().formatarMoeda5(
								Double.parseDouble(String.valueOf(atual))));
			}
			
			if (type.toLowerCase().equals("valor6")) {
				pageContext.getOut().print(
						Formatador.getInstance().formatarMoeda6(
								Double.parseDouble(String.valueOf(atual))));
			}
			
			if (type.toLowerCase().equals("valorint")) {
				pageContext.getOut().print(
						Formatador.getInstance().formatarDecimalInt(
								Double.parseDouble(String.valueOf(atual))));
			}

			if (type.toLowerCase().equals("simnao")) {
				if (Boolean.valueOf(String.valueOf(atual)))
					pageContext.getOut().print("Sim");
				else
					pageContext.getOut().print("Não");
			}

			if (type.toLowerCase().equals("moeda")) {
				if (cor != null) {
					if (cor.equals("true")) {
						String color = "blue";

						if ( atual instanceof String ) {
							atual = new Double((String)atual);
						}

						color = ((Number) atual).doubleValue() <= 0 ? "red"
								: color;
						pageContext
								.getOut()
								.print(
										"<font color="
												+ color
												+ ">"
												+ "R$ "
												+ Formatador
														.getInstance()
														.formatarMoeda(
																Double
																		.parseDouble(String
																				.valueOf(atual)))
												+ "</font>");
					}
				}
				if ((cor == null) || (cor.equals("false"))) {
					if (atual != null && !atual.equals("")) {
						pageContext.getOut().print(
								"R$ " + Formatador.getInstance().formatarMoeda(
									Double.parseDouble(String.valueOf(atual))));
					} else {
						pageContext.getOut().print("R$ 0,00");
					}
				}
			}

			if (type.toLowerCase().equals("moeda3")) {
				if (cor != null && atual != null) {
					if (cor.equals("true")) {
						String color = "blue";
						color = ((Number) atual).doubleValue() <= 0 ? "red"
								: color;
						pageContext
								.getOut()
								.print(
										"<font color="
												+ color
												+ ">"
												+ "R$ "
												+ Formatador
														.getInstance()
														.formatarMoeda3(
																Double
																		.parseDouble(String
																				.valueOf(atual)))
												+ "</font>");
					}
				}
				if ((cor == null) || (cor.equals("false"))) {
					pageContext.getOut().println(
							"R$ "
									+ Formatador.getInstance().formatarMoeda3(
											Double.parseDouble(String
													.valueOf(atual))));
				}
			}

			if (type.toLowerCase().equals("moeda4")) {
				if (cor != null && atual != null) {
					if (cor.equals("true")) {
						String color = "blue";
						color = ((Number) atual).doubleValue() <= 0 ? "red"
								: color;
						pageContext
								.getOut()
								.println(
										"<font color="
												+ color
												+ ">"
												+ "R$ "
												+ Formatador
														.getInstance()
														.formatarMoeda4(
																Double
																		.parseDouble(String
																				.valueOf(atual)))
												+ "</font>");
					}
				}
				if ((cor == null) || (cor.equals("false"))) {
					pageContext.getOut().println(
							"R$ "
									+ Formatador.getInstance().formatarMoeda4(
											Double.parseDouble(String
													.valueOf(atual))));
				}
			}

			if (type.toLowerCase().equals("data")) {
				if (atual != null && atual instanceof Date) {
					pageContext.getOut().print(
							Formatador.getInstance().formatarData((Date) atual)
									.trim());
				}
			}
			
			if(type.toLowerCase().equals("calcula_data")){
				if (atual != null && atual instanceof Date) {
					try{
						int diasContados = Integer.parseInt(dias);
						Calendar c = Calendar.getInstance();
						
						c.setTime((Date) atual);
						c.add(Calendar.DAY_OF_MONTH, diasContados);
						
						atual = c.getTime(); 
						
						pageContext.getOut().print(
								Formatador.getInstance().formatarData((Date) atual)
										.trim());
					}
					catch (Exception e) {
						throw new Exception(e);
					}
				}
			}
			if (type.toLowerCase().equals("mes")) {
				if (atual != null) {
					// Extrai mês de um objeto data
					if (atual instanceof Date) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime((Date) atual);
						atual = new Integer(calendar.get(Calendar.MONTH));
					}
					pageContext.getOut().println(
							Formatador.getInstance().formatarMes(
									String.valueOf(atual)));
				}
			}
			
			if (type.toLowerCase().equals("mes_informado")) {
				if (atual != null && (atual instanceof Integer || atual instanceof Long) ) {
					int mes = 0;
					if (atual instanceof Long){
						mes = ((Long)atual).intValue();						
					}else if (atual instanceof Integer)
						mes = (Integer) atual;
					
					pageContext.getOut().println(CalendarUtils.getMesAbreviado(mes));
				}
			}
			
			if (type.toLowerCase().equals("mes_abreviado")) {
				if (atual != null) {
					// Extrai mês de um objeto data
					if (atual instanceof Date) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime((Date) atual);
						atual = new Integer(calendar.get(Calendar.MONTH));
					}
					pageContext.getOut().println(
							Formatador.getInstance().formatarMesAbreviado(Integer.parseInt(""+atual)));
				}
			}
			
			if (type.toLowerCase().equals("mes_upper")) {
				if (atual != null) {
					// Extrai mês de um objeto data
					if (atual instanceof Date) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime((Date) atual);
						atual = new Integer(calendar.get(Calendar.MONTH));
					}
					pageContext.getOut().println(
							Formatador.getInstance().formatarMes(
									String.valueOf(atual)).toUpperCase());
				}
			}

			if (type.toLowerCase().equals("mes_ano")) {
				if (atual != null && atual instanceof Date) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime((Date) atual);
					int mes = calendar.get(Calendar.MONTH);
					int ano = calendar.get(Calendar.YEAR);

					pageContext.getOut().println(
							Formatador.getInstance().formatarMes(mes) + " de "
									+ ano);
				} else
					throw new JspException("Bean " + name + "." + property
							+ " não é um objeto java.util.Date");
			}

			if (type.toLowerCase().equals("mes_ano_numero")) {
				if (atual != null && atual instanceof Date) {
					SimpleDateFormat df = new SimpleDateFormat("MM/yyyy");
					pageContext.getOut().println(df.format(atual));
				}
			}

			/* ANO */
			if (type.toLowerCase().equals("ano")) {
				if (atual != null && atual instanceof Date) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime((Date) atual);
					int ano = calendar.get(Calendar.YEAR);

					pageContext.getOut().print(ano);
				}
			}

			if (type.toLowerCase().equals("dia_mes_ano")) {
				if (atual != null && atual instanceof Date) {
					pageContext.getOut().print(
							Formatador.getInstance().formatarDataDiaMesAno(
									(Date) atual));
				} else
					throw new JspException("Bean " + name + "." + property
							+ " não é um objeto java.util.Date");
			}


			if (type.toLowerCase().equals("dia")) {
				if (atual != null && atual instanceof Date) {
					pageContext.getOut().print(
							Formatador.getInstance().formatarDataDia(
									(Date) atual));
				} else
					throw new JspException("Bean " + name + "." + property
							+ " não é um objeto java.util.Date");
			}
			
			if (type.toLowerCase().equals("inicial_dia_semana")) {
				if (atual != null && atual instanceof Date) {
					pageContext.getOut().print(
							Formatador.getInstance().getLetraInicialDiaSemana((Date) atual));
				} else
					throw new JspException("Bean " + name + "." + property
							+ " não é um objeto java.util.Date");
			}


			if (type.toLowerCase().equals("hora")) {
				if (atual != null) {
					pageContext.getOut()
							.println(
									Formatador.getInstance().formatarHora(
											(Date) atual));
				}
			}

			if (type.toLowerCase().equals("datahora")) {
				if (atual != null) {
					pageContext.getOut().print(
							Formatador.getInstance().formatarDataHora(
									(Date) atual));
				}
			}

			if (type.toLowerCase().equals("datahorasec")) {
				if (atual != null) {
					pageContext.getOut().print(
							Formatador.getInstance().formatarDataHoraSec(
									(Date) atual));
				}
			}
			
			if(type.toLowerCase().equals("formatatempo")){
				if (atual != null) {
					pageContext.getOut().print(
							Formatador.getInstance().formatarTempo(Long.parseLong(""+atual)));
				}
			}
			
			if (type.toLowerCase().equals("cpf")) {
				if (atual != null) {
					String cpfFormatado = null;
					
					if (atual instanceof Long) {
						cpfFormatado = Formatador.getInstance().formatarCPF(((Long) atual).longValue());
					} else if (atual instanceof String) {
						cpfFormatado = Formatador.getInstance().formatarCPF(Long.parseLong(atual.toString()));
					} else {
						cpfFormatado = Formatador.getInstance().formatarCPF(((BigInteger) atual).longValue());
					}
					
					pageContext.getOut().print(cpfFormatado);
					
				}
			}
			
			if (type.toLowerCase().equals("cpf_cnpj")) {
				if (atual != null) {
					String cpfCnpjFormatado = null;
					
					if (atual instanceof Long) {
						cpfCnpjFormatado = Formatador.getInstance().formatarCPF_CNPJ(((Long) atual).longValue());
					} else if (atual instanceof String) {
						cpfCnpjFormatado = Formatador.getInstance().formatarCPF_CNPJ(Long.parseLong(atual.toString()));
					} else {
						cpfCnpjFormatado = Formatador.getInstance().formatarCPF_CNPJ(((BigInteger) atual).longValue());
					}
					
					pageContext.getOut().print(cpfCnpjFormatado);
					
				}
			}
			
			if (type.toLowerCase().equals("cnpj")) {
				if (atual != null) {
					String cnpj = null;
					
					if (atual instanceof Long) {
						cnpj = Formatador.getInstance().formatarCNPJ(((Long) atual).longValue());
					} else if (atual instanceof String) {
						cnpj = Formatador.getInstance().formatarCNPJ(Long.parseLong(atual.toString()));
					} else {
						cnpj = Formatador.getInstance().formatarCNPJ(((BigInteger) atual).longValue());
					}
					
					pageContext.getOut().print(cnpj);
				}
			}

			if (type.equalsIgnoreCase("decimal")) {
				if (atual != null) {
					pageContext.getOut().print(
							Formatador.getInstance().formatarDecimal1(
									Double.parseDouble(String.valueOf(atual))));
				}
			}

			if (type.equalsIgnoreCase("natural")) {
				if (atual != null) {
					pageContext.getOut().print(
							Formatador.getInstance().formatarNatural(
									Long.parseLong(String.valueOf(atual))));
				}
			}

			if (type.equalsIgnoreCase("unidade")) {
				if (atual != null) {
					pageContext.getOut().print(
							Formatador.getInstance().formatarCodigoUnidade(
									Long.parseLong(String.valueOf(atual))));
				}
			}

			if (type.equalsIgnoreCase("sexo")) {
				if (atual != null) {
					pageContext.getOut().print(
							Formatador.getInstance().formatarSexo(
									String.valueOf(atual)));
				}
			}

			if (type.equalsIgnoreCase("telefone")) {
				if (atual != null) {
					pageContext.getOut().print(
							Formatador.getInstance().formatarTelefone(
									Integer.parseInt(String.valueOf(atual))));
				}
			}

			if (type.equalsIgnoreCase("cep")) {
				if (atual != null && !atual.equals("")) {
					pageContext.getOut().print(
							Formatador.getInstance().formatarCep(
									Integer.parseInt(String.valueOf(atual))));
				}
			}

			if (type.equalsIgnoreCase("bool_vf") || type.equalsIgnoreCase("bool_sn")) {
				String tipo = null;
				if (type.toLowerCase().endsWith("vf")) {
					tipo = "V/F";
				} else {
					tipo = "S/N";
				}
				if (atual != null) {
					pageContext.getOut().print(
							Formatador.getInstance().formatarBoolean(
									Boolean.parseBoolean(String.valueOf(atual)), tipo));
				}
			}

			if (type.equalsIgnoreCase("extenso")) {
				if (atual != null) {
					Extenso extenso = new Extenso();
					extenso.setMoeda(true);
					extenso.setNumber(BigDecimal.valueOf(Double.parseDouble(String.valueOf(atual))));
					pageContext.getOut().print(extenso.toString().toUpperCase());
				}
			}

			if (type.equalsIgnoreCase("extensonumero")) {
				if (atual != null) {
					Extenso extenso = new Extenso();
					extenso.setMoeda(false);
					extenso.setNumber(BigDecimal.valueOf(Double.parseDouble(String.valueOf(atual))));
					pageContext.getOut().print(extenso.toString().toUpperCase());
				}
			}

			if (type.equalsIgnoreCase("anosemestre")) {
				if (atual != null) {

					pageContext.getOut().print(
							Formatador.getInstance().formatarAnoSemestre(
									Integer.parseInt(String.valueOf(atual))));

				}
			}

			if (type.equalsIgnoreCase("anos_meses_dias")) {
				if (atual != null) {

					pageContext.getOut().print(
							Formatador.getInstance().descreverTempoAnoMesesDias(
									Integer.parseInt(String.valueOf(atual))));

				}
			}
			
			if (type.equalsIgnoreCase("marcacao_palavra")) {
				
				String texto = (String) atual;
				
				if(texto == null) texto = "";
				
				StringBuilder textoFmt = new StringBuilder(texto);
				
				int indice = 0;
				
				if(palavraChave != null && !palavraChave.trim().equals("")){
					while(indice != -1){
						palavraChave = StringUtils.toAscii(palavraChave);
						
						indice = textoFmt.indexOf(palavraChave.toUpperCase(), indice);
						if(indice != -1){
							textoFmt.insert(indice, "<b>");
							
							indice += (palavraChave.length() + 3);
							
							textoFmt.insert(indice, "</b>");
						}
							
					}
				}
				
				pageContext.getOut().print(textoFmt.toString());
			}
			if (type.equalsIgnoreCase("texto")) {
				try {
					if (length == 0)
						length = 30;
					else if (length == -1)
						length = Integer.MAX_VALUE;

					String texto = (String) atual;
					if(texto == null) texto = "";
					StringBuilder textoFmt = new StringBuilder();

					for (int i = 0, wrapCount = 0; i <= length && i < texto.length(); i++, wrapCount++) {
						switch (texto.charAt(i)) {
						case '\n':
							textoFmt.append("<br />");
							wrapCount = 0;
							break;
						/*
                        case ' ':
							textoFmt.append("&nbsp;");
							break;
                        */
						case '\r':
							break;
						case '\"':
							textoFmt.append("&quot;");
							break;
						case '<':
							textoFmt.append("&lt;");
							break;
						case '>':
							textoFmt.append("&gt;");
							break;
						default:
							textoFmt.append(texto.charAt(i));
							break;
						}


						if ((lineWrap > 0 && wrapCount >= lineWrap)
								&& (texto.charAt(i) == ' ' || wrapCount > lineWrap + 10)) {
							textoFmt.append(" <br />");
							wrapCount = 0;
						}
					}
					if (texto.length() > length)
						textoFmt.append("...");

					pageContext.getOut().print(textoFmt.toString());

				} catch (Exception e) {
					pageContext.getOut().print(atual);
				}

				release();
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e);
		}

		return super.doStartTag();
	}

	@Override
	public void release() {
		this.cor = null;
		this.name = null;
		this.property = null;
		this.scope = null;
		this.type = null;
		this.valor = null;
		this.length = -1;
		this.lineWrap = 0;

		super.release();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getParam() {
		return null;
	}

	public void setParam(String param) {
		
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getCor() {
		return cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}

	public Object getValor() {
		return valor;
	}

	public void setValor(Object valor) {
		this.valor = valor;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getLineWrap() {
		return lineWrap;
	}

	public void setLineWrap(int lineWrap) {
		this.lineWrap = lineWrap;
	}

    public boolean isMovimento() {
        return movimento;
    }

    public void setMovimento(boolean movimento) {
        this.movimento = movimento;
    }

	public String getDias() {
		return dias;
	}

	public void setDias(String dias) {
		this.dias = dias;
	}

	public String getPalavraChave() {
		return palavraChave;
	}

	public void setPalavraChave(String palavraChave) {
		this.palavraChave = palavraChave;
	}
}