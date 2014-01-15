/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 02/02/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
*
* <p> MBean que gerencia o caso de uso que vai permitir ao administrador geral do sistema de biblioteca alterar o valor de alguns 
* parâmetro utilizados na parte de circulação, como por exemplo, a quantidade de dias que o usuário vai ficar suspenso se atrasar um livro. </p>
* <p> Os parâmetros alterados aqui são válidos para todas as bibliotecas do sistema. </p>
* 
* @author jadson
*
*/
@Component("configuraParametrosCirculacaoMBean")
@Scope("request")
public class ConfiguraParametrosCirculacaoMBean extends AbstractConfiguraParametrosBiblioteca{

	/**
	 * OBSERVAÇÃO:  Caso adicione um novo parêmetro no sistema e queira que o usuário possa alterá-lo, adicione-o a essa lista.
	 * 
	 * <br/>
	 * <br/> 
	 * IMPORTANTE, MUITA ATENÇÃO: Caso altere essa ordem, lembrem-se de alterar a ordem na página, pois cada  posição na página implica em um tipo de campo e máscara diferente.
	 *
	 * @see br.ufrn.sigaa.biblioteca.jsf.AbstractConfiguraParametrosBiblioteca#configuraParametros()
	 */
	@Override
	protected void configuraParametros() {
		// Adicione nessa lista o parâmetro e a posição que ele deve parecer na tela para o usuário
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.SISTEMA_TRABALHA_COM_SUSPENSAO, 0);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.SISTEMA_TRABALHA_COM_MULTA, 1);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.SISTEMA_TRABALHA_COM_RESERVAS, 2);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.SISTEMA_PERMITE_CONFIGURAR_POLITICAS_DIFERENTES_POR_BIBLIOTECA, 3);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.QUANTIDADE_DIAS_SUSPENSAO_USUARIO_ATRASO_POR_DIA, 4);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.QUANTIDADE_DIAS_SUSPENSAO_USUARIO_ATRASO_POR_HORA, 5);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.VALOR_MULTA_USUARIO_ATRASO_POR_DIA, 6);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.VALOR_MULTA_USUARIO_ATRASO_POR_HORA, 7);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.PRAZO_MINIMO_ENTRE_EMPRESTIMOS, 8);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.PRAZO_USUARIO_INCORRE_IRREGULARIDADE_ADMINISTRATIVA, 9);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.MENSAGEM_ALERTA_EMAIL_EMPRESTIMOS_EM_ATRASO, 10);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.QUANTIDADE_MAXIMA_RESERVAS_ONLINE, 11);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.PRAZO_EM_DIAS_USUARIO_TEM_PARA_EFETIVAR_RESERVA, 12);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.QUANTIDADE_MINIMA_MATERIAIS_SOLICITAR_RESERVA, 13);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.PRAZO_ENVIO_EMAIL_EMPRESTIMO_VENCENDO, 14);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.LINK_MANUAL_EMPRESTIMOS_DESKTOP, 15);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.LINK_MANUAL_CHECKOUT_DESKTOP, 16);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.MENSAGEM_JANELA_SOBRE_DESKTOP, 17);
		codigosParametrosAlteraveis.put(ParametrosBiblioteca.TEXTO_DE_ADESAO_AO_SISTEMA_DE_BIBLIOTECAS, 18);
		
		// Ajustando parâmetros não obrigatórios
		codigosParametrosAlteraveisOpcionais.add(ParametrosBiblioteca.TEXTO_DE_ADESAO_AO_SISTEMA_DE_BIBLIOTECAS);
		
	}

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.jsf.AbstractConfiguraParametrosBiblioteca#configuraPaginaListaParametros()
	 */
	@Override
	protected void configuraPaginaListaParametros() {
		this.paginaListaParametrosBiblioteca = "/biblioteca/listaParametrosCirculacaoSistemaBiblioteca.jsp";
		
	}
	
	
}
