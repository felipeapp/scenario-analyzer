/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 11/08/2009
 */
package br.ufrn.arq.util;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;

import br.ufrn.ambientes.dominio.ConstantesParametrosAmbientes;
import br.ufrn.ambientes.dominio.TipoAmbiente;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.erros.ConfiguracaoAmbienteException;
import br.ufrn.arq.erros.RuntimeNegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;

/**
 * Métodos utilitários para funções relacionadas
 * aos ambientes de execução dos sistemas.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 */
public class AmbienteUtils {

	/**
	 * Deixa um sistema em modo de manutenção.
	 * 
	 * @param sistema
	 */
	public static void deixarSistemaEmManutencao(int sistema) {
		try {
			FileUtils.touch(new File(getDirShared() + "manutencao/" + sistema));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Tira um sistema do modo de manutenção.
	 * 
	 * @param sistema
	 */
	public static void tirarSistemaDeManutencao(int sistema) {
		new File(getDirShared() + "manutencao/" + sistema).delete();
	}
	
	/**
	 * Verifica se um sistema está em modo de manutenção.
	 * 
	 * @param sistema
	 */
	public static boolean isSistemaEmManutencao(int sistema) {
		return new File(getDirShared() + "manutencao/" + sistema).exists();
	}
	
	private static String getDirShared() {
		String dirShared = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.CAMINHO_RECURSOS);
		if (!dirShared.endsWith("/")) {
			dirShared += "/";
		}
		return dirShared;
	}

	/**
	 * Verifica se o servidor atual é o de testes
	 *
	 * @return
	 */
	public static boolean isServidorTestes() {
	
		List<String> servidores = Arrays
				.asList(new String[] { "desenvolvimento", "testes" });
		return servidores.contains(AmbienteUtils.getLocalName());
	
	}

	/**
	 * Verifica se o servidor atual é localhost.
	 * @return
	 */
	public static boolean isServidorLocalhost() {
		return !isServidorTestes() && !isServidorProducao();
	}
	
	/**
	 * Verifica se o servidor atual é o de produção
	 *
	 * @return
	 */
	public static boolean isServidorProducao() {
		int tipoAmbiente = ParametroHelper.getInstance().getParametroInt(ConstantesParametrosAmbientes.TIPO_AMBIENTE_ATUAL);
		return tipoAmbiente == TipoAmbiente.PRODUCAO;
	}

	/**
	 * Retorna o nome da máquina em que o método é executado
	 * @deprecated Usar métodos de {@link NetworkUtils}
	 * @return
	 */
	@Deprecated
	public static String getLocalName() {
		return NetworkUtils.getLocalName();
	}

	/**
	 * Retorna o endereço da máquina onde o método foi executado
	 * @deprecated Usar métodos de {@link NetworkUtils}
	 * @return
	 */
	@Deprecated
	public static String getLocalAddress() {
		return NetworkUtils.getLocalAddress();
	}

	/**
	 * Verifica se o protocolo de acesso a uma URL não está seguro
	 *
	 * @param request
	 * @return
	 */
	public static boolean isNotSecure(HttpServletRequest request) {
		return !request.isSecure() && isServidorProducao()
				&& request.getServerName().contains("www");
	}

	/**
	 * Retorna uma String com o html da tela de manutenção de um determinado
	 * sistema passado como parâmetro.
	 * @param sistema
	 * @return
	 */
	public static String getTelaManutencao(Integer sistema) {
		File tela = new File(getDirShared() + "manutencao/" + sistema + ".htm");
		try {
			return FileUtils.readFileToString(tela);
		} catch (IOException e) {
			e.printStackTrace();
			return "Sistema em manutenção.";
		}
	}
	
	/**
	 * Escreve uma String contendo o html da tela de manutenção do
	 * sistema passado como parâmetro em um arquivo.
	 * @param sistema
	 * @param conteudo
	 */
	public static void escreverTelaManutencao(Integer sistema, String conteudo) {
		if (getDirShared() == null) 
			throw new ConfiguracaoAmbienteException("Não foi possível encontrar o diretório para armazenar as informações solicitadas porque o parâmetro CAMINHO_RECURSOS (código 3_1_4) não está definido.");
		
		if (!isEmpty(conteudo)) {
			File tela = new File(getDirShared() + "manutencao/" + sistema + ".htm");
			try {
				FileUtils.writeStringToFile(tela, conteudo);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new RuntimeNegocioException("Nenhum conteúdo foi informado para a tela de manutenção dos sistemas. Por favor, informe esse conteúdo para continuar.");
		}
	}
	
	public static String obterVersaoProducao(HttpServletRequest request){
		return request.getParameter("sistema");
	}
	
	/**
	 * Método que retorna os dados da versão dos sistemas.
	 * 
	 * Recebe como parâmetro o arquivo bundle associado, conforme o projeto.
	 * 
	 * @param arquivo
	 * @return
	 */
	public static HashMap<String, String> dadosVersao(String arquivo){
		HashMap<String, String> retorno = null;
		try {
			ResourceBundle p = ResourceBundle.getBundle(arquivo);
			retorno = new HashMap<String, String>();
			Enumeration<String> em = p.getKeys();
			
			while(em.hasMoreElements()){
				String chave = (String) em.nextElement();
				retorno.put(chave, p.getString(chave));
			}
		} catch (MissingResourceException e) {
			e.printStackTrace();
		}
		
		return retorno;
	}
	
	/**
	 * Retorna a instância atual do JBoss em que o sistema está rodando.
	 * @return
	 */
	public static String getInstanceName() {
		return System.getProperty("br.ufrn.jboss.instanceName");
	}

	/**
	 * Retorna o nome do servidor do JBoss com a instância em que o sistema
	 * está sendo executado.
	 * @return
	 */
	public static String getNomeServidorComInstancia() {
		String nomeServidor = NetworkUtils.getLocalName();
		String nomeInstancia = getInstanceName();
		if (nomeInstancia != null)
			nomeServidor += "." + nomeInstancia;
		return nomeServidor;
	}
	
	/**
	 * Método que que verifica se um agent passado como parâmetro é um mobile agent.
	 * @param agent
	 * @return
	 */
	public static boolean isMobileUserAgent(String agent) {
		String mobileAgents = ParametroHelper.getInstance().getParametro(ConstantesParametrosAmbientes.TIPOS_MOBILE_AGENTS);
		return agent.toLowerCase().matches(mobileAgents);
	}
	
}
