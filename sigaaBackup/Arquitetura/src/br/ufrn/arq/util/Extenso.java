/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Cria��o em: 11/02/2005
 */
package br.ufrn.arq.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Classe que converte um valor num�rico em uma String contendo
 * o seu valor por extenso.
 * 
 * @author Ricardo Wendell
 * @author Gleydson Lima
 *
 */
public final class Extenso {

	private ArrayList<Integer> nro;
	private BigInteger num;
	private boolean moeda = true;
	
	private String qualificadores[][] = {
			{"centavo", "centavos"},
			{"", ""},
			{"mil", "mil"},
			{"milh�o", "milh�es"},
			{"bilh�o", "bilh�es"},
			{"trilh�o", "trilh�es"},
			{"quatrilh�o", "quatrilh�es"},
			{"quintilh�o", "quintilh�es"},
			{"sextilh�o", "sextilh�es"},
			{"septilh�o", "septilh�es"}
			};
	private String numeros[][] = {
			{"zero", "um", "dois", "tr�s", "quatro", "cinco", "seis", "sete", "oito", "nove", "dez",
			"onze", "doze", "treze", "quatorze", "quinze", "dezesseis", "dezessete", "dezoito", "dezenove"},
			{"vinte", "trinta", "quarenta", "cinquenta", "sessenta", "setenta", "oitenta", "noventa"},
			{"cem", "cento", "duzentos", "trezentos", "quatrocentos", "quinhentos", "seiscentos",
			"setecentos", "oitocentos", "novecentos"}
			};


	/**
	 *  Construtor
	 */
	public Extenso() {
		nro = new ArrayList<Integer>();
	}


	/**
	 *  Construtor
	 *
	 *@param  dec  valor para colocar por extenso
	 */
	public Extenso(BigDecimal dec) {
		this();
		setNumber(dec);
	}


	/**
	 *  Constructor for the Extenso object
	 *
	 *@param  dec  valor para colocar por extenso
	 */
	public Extenso(double dec) {
		this();
		setNumber(dec);
	}


	/**
	 *  Sets the Number attribute of the Extenso object
	 *
	 *@param  dec  The new Number value
	 */
	public void setNumber(BigDecimal dec) {
		// Converte para inteiro arredondando os centavos
		num = dec
			.setScale(2, BigDecimal.ROUND_HALF_UP)
			.multiply(BigDecimal.valueOf(100))
			.toBigInteger();

		// Adiciona valores
		nro.clear();
		if (num.equals(BigInteger.ZERO)) {
			// Centavos
			nro.add(0);
			// Valor
			nro.add(0);
		}
		else {
			// Adiciona centavos
			addRemainder(100);
			
			// Adiciona grupos de 1000
			while (!num.equals(BigInteger.ZERO)) {
				addRemainder(1000);
			}
		}
	}

	public void setNumber(double dec) {
		setNumber(new BigDecimal(dec));
	}

	/**
	 *  Description of the Method
	 */
	public void show() {
		for (Iterator<Integer> valores = nro.iterator(); valores.hasNext(); ) {
			System.out.println(valores.next().intValue());
		}
		System.out.println(toString());
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();

		//int numero = ((Integer) nro.get(0)).intValue();
		int ct;

		for (ct = nro.size() - 1; ct > 0; ct--) {
			// Se ja existe texto e o atual n�o � zero
			if (buf.length() > 0 && ! ehGrupoZero(ct)) {
				buf.append(" e ");
			}
			buf.append(numToString(nro.get(ct).intValue(), ct));
		}
		if (buf.length() > 0) {
			if (ehUnicoGrupo())
				buf.append(" de ");
			while (buf.toString().endsWith(" "))
				buf.setLength(buf.length()-1);
			
			if (isMoeda()) {
			    if (nro.size() == 2 && nro.get(1).intValue() == 1) {
			        buf.append(" real");
			    } else {
			        buf.append(" reais");
			    }
			}
			
			if (nro.get(0).intValue() != 0) {
				buf.append(" e ");
			}
		}
		if (nro.get(0).intValue() != 0) {
			buf.append(numToString(nro.get(0).intValue(), 0));
		}
		return buf.toString();
	}

	/**
	 *  Adds a feature to the Remainder attribute of the Extenso object
	 *
	 *@param  divisor  The feature to be added to the Remainder attribute
	 */
	private void addRemainder(int divisor) {
		// Encontra newNum[0] = num modulo divisor, newNum[1] = num dividido divisor
		BigInteger[] newNum = num.divideAndRemainder(BigInteger.valueOf(divisor));

		// Adiciona modulo
		nro.add(new Integer(newNum[1].intValue()));

		// Altera numero
		num = newNum[0];
	}


	/**
	 *  Description of the Method
	 *
	 *@param  ps  Description of Parameter
	 *@return     Description of the Returned Value
	 
	private boolean temMaisGrupos(int ps) {
		for (; ps > 0; ps--) {
			if (((Integer) nro.get(ps)).intValue() != 0) {
				return true;
			}
		}

		return false;
	}
*/

	/**
	 *  Description of the Method
	 *
	 *@param  ps  Description of Parameter
	 *@return     Description of the Returned Value

	private boolean ehUltimoGrupo(int ps) {
		return (ps > 0) && ((Integer)nro.get(ps)).intValue() != 0 && !temMaisGrupos(ps - 1);
	}
	 */

	/**
	 *  Description of the Method
	 *
	 *@return     Description of the Returned Value
	 */
	private boolean ehUnicoGrupo() {
		if (nro.size() <= 3)
			return false;
		if (!ehGrupoZero(1) && !ehGrupoZero(2))
			return false;
		boolean hasOne = false;
		for(int i=3; i < nro.size(); i++) {
			if (nro.get(i).intValue() != 0) {
				if (hasOne)
					return false;
				hasOne = true;
			}
		}
		return true;
	}

	boolean ehGrupoZero(int ps) {
		if (ps <= 0 || ps >= nro.size())
			return true;
		return nro.get(ps).intValue() == 0;
	}
	
	/**
	 *  Description of the Method
	 *
	 *@param  numero  Description of Parameter
	 *@param  escala  Description of Parameter
	 *@return         Description of the Returned Value
	 */
	private String numToString(int numero, int escala) {
		int unidade = (numero % 10);
		int dezena = (numero % 100); //* nao pode dividir por 10 pois verifica de 0..19
		int centena = (numero / 100);
		StringBuffer buf = new StringBuffer();

		if (numero != 0) {
			if (centena != 0) {
				if (dezena == 0 && centena == 1) {
					buf.append(numeros[2][0]);
				}
				else {
					buf.append(numeros[2][centena]);
				}
			}

			if ((buf.length() > 0) && (dezena != 0)) {
				buf.append(" e ");
			}
			if (dezena > 19) {
				dezena /= 10;
				buf.append(numeros[1][dezena - 2]);
				if (unidade != 0) {
					buf.append(" e ");
					buf.append(numeros[0][unidade]);
				}
			}
			else if (centena == 0 || dezena != 0) {
				buf.append(numeros[0][dezena]);
			}

			buf.append(" ");
			if (numero == 1) {
				buf.append(qualificadores[escala][0]);
			}
			else {
				buf.append(qualificadores[escala][1]);
			}
		}

		return buf.toString();
	}
    /**
     * @return Retorna moeda.
     */
    public boolean isMoeda() {
        return moeda;
    }
    /**
     * @param moeda The moeda to set.
     */
    public void setMoeda(boolean moeda) {
        this.moeda = moeda;
    }
    /**
     * @return Retorna nro.
     */
    public ArrayList<Integer> getNro() {
        return nro;
    }
    /**
     * @param nro The nro to set.
     */
    public void setNro(ArrayList<Integer> nro) {
        this.nro = nro;
    }
    /**
     * @return Retorna num.
     */
    public BigInteger getNum() {
        return num;
    }
    /**
     * @param num The num to set.
     */
    public void setNum(BigInteger num) {
        this.num = num;
    }
    /**
     * @return Retorna numeros.
     */
    public String[][] getNumeros() {
        return numeros;
    }
    /**
     * @param numeros The numeros to set.
     */
    public void setNumeros(String[][] numeros) {
        this.numeros = numeros;
    }
    /**
     * @return Retorna qualificadores.
     */
    public String[][] getQualificadores() {
        return qualificadores;
    }
    /**
     * @param qualificadores The qualificadores to set.
     */
    public void setQualificadores(String[][] qualificadores) {
        this.qualificadores = qualificadores;
    }
}