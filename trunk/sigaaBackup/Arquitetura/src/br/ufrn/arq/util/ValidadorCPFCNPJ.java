/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 27/12/2004
 */
package br.ufrn.arq.util;

import java.text.DecimalFormat;

/**
 * Classe que Valida CPFs e CPNJs para verificar sua validade
 * 
 * @author Gleydson Lima
 */
public class ValidadorCPFCNPJ {

	private static ValidadorCPFCNPJ singleton = new ValidadorCPFCNPJ();

	private static DecimalFormat dfCPF = new DecimalFormat("00000000000");

	private static DecimalFormat dfCNPJ = new DecimalFormat("00000000000000");

	private ValidadorCPFCNPJ() {

	}

	public static ValidadorCPFCNPJ getInstance() {
		return singleton;
	}

	public boolean validaCpfCNPJ(Long cpfCnpj) {
		if (cpfCnpj == null)
			return false;
		else {
			if (String.valueOf(cpfCnpj).length() <= 11) {
				return validaCpfCNPJ(dfCPF.format(cpfCnpj));
			} else {
				return validaCpfCNPJ(dfCNPJ.format(cpfCnpj));
			}
		}
	}

	public boolean validaCpfCNPJ(String cpf_cnpj) {
		if (cpf_cnpj == null || cpf_cnpj.trim().length() == 0)
			return false;
		
		String cpfCnpjLimpo = cpf_cnpj.replaceAll("[\\D]+", "");

		if (cpfCnpjLimpo.length() <= 11) {
			return validaCPF(cpfCnpjLimpo);
		} else if (cpfCnpjLimpo.length() > 11) {
			return validaCNPJ(cpfCnpjLimpo);
		}

		return false;

	}

	public boolean validaCPF(String cpf_cnpj) throws IllegalArgumentException {

		try {
			if ((cpf_cnpj == null) || (cpf_cnpj.length() == 0))
				return false;

			Long.parseLong( cpf_cnpj.replaceAll("[\\D]+", "") );

		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Informe somente números para validar o CPF");
		}

		int soma = 0;

		if (cpf_cnpj.length() == 11) {
			for (int i = 0; i < 9; i++)
				soma += (10 - i) * (cpf_cnpj.charAt(i) - '0');
			soma = 11 - (soma % 11);
			if (soma > 9)
				soma = 0;
			if (soma == (cpf_cnpj.charAt(9) - '0')) {
				soma = 0;
				for (int i = 0; i < 10; i++)
					soma += (11 - i) * (cpf_cnpj.charAt(i) - '0');
				soma = 11 - (soma % 11);
				if (soma > 9)
					soma = 0;
				if (soma == (cpf_cnpj.charAt(10) - '0')) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean validaCNPJ(String cpf_cnpj) throws IllegalArgumentException {

		try {
			cpf_cnpj = cpf_cnpj.replaceAll("[\\D]+", "");
			Long.parseLong(cpf_cnpj);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Informe somente números para validar o CNPJ");
		}

		int soma = 0;

		if (cpf_cnpj.length() == 14) {
			for (int i = 0, j = 5; i < 12; i++) {
				soma += j-- * (cpf_cnpj.charAt(i) - '0');
				if (j < 2)
					j = 9;
			}
			soma = 11 - (soma % 11);
			if (soma > 9)
				soma = 0;
			if (soma == (cpf_cnpj.charAt(12) - '0')) {
				soma = 0;
				for (int i = 0, j = 6; i < 13; i++) {
					soma += j-- * (cpf_cnpj.charAt(i) - '0');
					if (j < 2)
						j = 9;
				}
				soma = 11 - (soma % 11);
				if (soma > 9)
					soma = 0;
				if (soma == (cpf_cnpj.charAt(13) - '0')) {
					return true;
				}
			}
		}

		return false;
	}

}