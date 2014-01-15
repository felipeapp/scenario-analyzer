/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 05/10/2004
 */
package br.ufrn.arq.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.comum.dominio.CasoUso;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.SubSistema;


/**
 * Esta classe representa a classe de paramâtros do SIPAC. Esses parâmetros tem
 * uma relação com algumas regras de negócio do sistema.
 * 
 * Os parâmetros são armazenados na base dados sistemas_comum.
 * 
 * @author Ricardo Alexsandro de Medeiros Valentim
 * @author David Pereira
 * @author Gleydson Lima
 */
public class Parametro {

    /** Código que representa o parâmetro. */
    private String codigo;

    /** Identifica o sistema ao qual o parâmetro pertence */
    private Sistema sistema;
    
    /** Identifica o subsistema ao qual o parâmetro pertence */
    private SubSistema subSistema;
    
    /** Casos de uso que utilizam este parâmetro */
    private List<CasoUso> casosUso;

    /** Nome do parâmetro. Nome da constante no sistema. */
    private String nome;

    /** Valor atribuído ao parâmetro */
    private String valor;

    /** Descrição da utilidade do parâmetro no sistema.*/
    private String descricao;
    
    /** Tempo máximo no qual o parâmetro fica em cache antes de ser recarregado. */
    private Integer tempoMaximo;
    
    /** Tipo de dados do parâmetro. Utilizado para validar o valor digitado. */
    private Integer tipo;
    
    /** Se o tipo de dados for uma String, ou uma data, define o padrão de formato desse dado. */
    private String padrao;
    
    /** Valor mínimo permitido para o parâmetro, caso ele seja do tipo numérico. */
    private BigDecimal valorMinimo;
    
    /** Valor máximo permitido para o parâmetro, caso ele seja do tipo numérico. */
    private BigDecimal valorMaximo;

    public ListaMensagens validaValor() {
    	ListaMensagens erros = new ListaMensagens();
    	
    	if (tipo != null && !isEmpty(valor)) {
    		switch(tipo) {
    		case Types.BOOLEAN:
    			if (!"true".equalsIgnoreCase(valor.trim()) && !"false".equalsIgnoreCase(valor.trim())) {
    				erros.addErro("Valor inválido para parâmetro do tipo Boolean. Deve ser \"true\" ou \"false\".");
    			}
    			break;
    		case Types.VARCHAR:
    			if (padrao != null) {
    				try {
    					Pattern pattern = Pattern.compile(padrao);
    					Matcher matcher = pattern.matcher(valor);
    					if (!matcher.find()) {
    						erros.addErro("Valor inválido para parâmetro do tipo String. Deve estar de acordo com a expressão regular " + padrao + ".");
    					}
    				} catch(PatternSyntaxException e) {
    					erros.addErro("Expressão regular de validação inválida.");
    				}
    			}

    			break;
    		case Types.INTEGER:
    			if (valorMinimo != null) {
    				long valorLong = 0;
    				
    				try {
    					valorLong = Long.parseLong(valor);
    				} catch(NumberFormatException e) {
    					erros.addErro("Valor inválido para parâmetro. Deve ser um valor numérico inteiro.");
    					break;
    				}
    				
    				if (valorMinimo.compareTo(new BigDecimal(valorLong)) > 0) {
    					erros.addErro("Valor inválido para parâmetro. O menor valor possível é " + valorMinimo + ".");
    				}
    			}
    			if (valorMaximo != null) {
    				if (valorMaximo.compareTo(new BigDecimal(Long.parseLong(valor))) < 0) {
    					erros.addErro("Valor inválido para parâmetro. O maior valor possível é " + valorMaximo + ".");
    				}
    			}
    			break;
    		case Types.DECIMAL:
    			double valorDouble = 0;
				
				try {
					valorDouble = Double.parseDouble(valor);
				} catch(NumberFormatException e) {
					erros.addErro("Valor inválido para parâmetro. Deve ser um valor numérico de ponto flutuante.");
					break;
				}
				
    			if (valorMinimo != null) {
    				if (valorMinimo.compareTo(new BigDecimal(valorDouble)) > 0) {
    					erros.addErro("Valor inválido para parâmetro. O menor valor possível é " + valorMinimo + ".");
    				}
    			}
    			if (valorMaximo != null) {
    				if (valorMaximo.compareTo(new BigDecimal(valorDouble)) < 0) {
    					erros.addErro("Valor inválido para parâmetro. O maior valor possível é " + valorMaximo + ".");
    				}
    			}
    			break;
    		case Types.DATE:
    			if (padrao != null) {
    				try {
    					SimpleDateFormat sdf = new SimpleDateFormat(padrao);
    					sdf.parse(valor);
    				} catch(IllegalArgumentException e) {
    					erros.addErro("Padrão de data inválido.");
    				} catch (ParseException e) {
    					erros.addErro("Valor inválido para parâmetro. A data deve seguir o padrão " + padrao + ".");
					}
    			}
    			break;
    		}
		}
    	
    	return erros;
    }
    
	public SubSistema getSubSistema() {
		return subSistema;
	}

	public void setSubSistema(SubSistema subSistema) {
		this.subSistema = subSistema;
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Sistema getSistema() {
		return sistema;
	}

	public void setSistema(Sistema sistema) {
		this.sistema = sistema;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Integer getTempoMaximo() {
		return tempoMaximo;
	}

	public void setTempoMaximo(Integer tempoMaximo) {
		this.tempoMaximo = tempoMaximo;
	}

	public List<CasoUso> getCasosUso() {
		return casosUso;
	}

	public void setCasosUso(List<CasoUso> casosUso) {
		this.casosUso = casosUso;
	}

	public Integer getTipo() {
		return tipo;
	}

	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}

	public String getPadrao() {
		return padrao;
	}

	public void setPadrao(String padrao) {
		this.padrao = padrao;
	}

	public BigDecimal getValorMinimo() {
		return valorMinimo;
	}

	public void setValorMinimo(BigDecimal valorMinimo) {
		this.valorMinimo = valorMinimo;
	}

	public BigDecimal getValorMaximo() {
		return valorMaximo;
	}

	public void setValorMaximo(BigDecimal valorMaximo) {
		this.valorMaximo = valorMaximo;
	}
	
}