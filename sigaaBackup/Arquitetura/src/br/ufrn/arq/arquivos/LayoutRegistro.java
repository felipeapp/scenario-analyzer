/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 12/12/2009
 */
package br.ufrn.arq.arquivos;

import java.util.ArrayList;

import br.ufrn.arq.util.UFRNUtils;

/**
 * Define o layout de um registro com posições. Um layout é formado por um
 * conjunto de campos. Cada campo tem um nome, posição de início e de fim.
 *
 * @author Gleydson Lima
 *
 */
public class LayoutRegistro {

	private Integer tipo;
	
	private String linha;

	private ArrayList<Campo> campos = new ArrayList<Campo>();

	private int idProcessamento;

	// chave primária da tabela arquivo_siape
	private int idLinhaArquivo;

	// caso alguma crítica foi inserida
	boolean criticado = false;

	private int campoAtual = 0;
	
	private String linhaConstruida;

	public void extrairCampos() {

		for (Campo c : campos) {
			c.extractValue(linha);
		}
	}

	public String getValorCampo(String campo) {
		Campo c = new Campo(campo);
		c = campos.get(campos.indexOf(c));
		return c.getValue();
	}
	
	public int getValorCampoInt(String campo) {
		return Integer.parseInt(getValorCampo(campo)); 
	}

	public Campo getCampo(String campo) {
		for(Campo c : campos){
			if(c.getNome().equalsIgnoreCase(campo)){
				return c;
			}
		}
		return null;
	}

	public void addCampo(String nome, int inicio, int fim, char tipo) {
		campos.add(new Campo(nome, inicio, fim, tipo));
	}
	
	public void addCampo(String nome, int tamanho, char tipo) {
		campos.add(new Campo(nome, campoAtual, campoAtual + tamanho, 0, tipo));
		campoAtual += tamanho;
	}

	public void addCampo(String nome, int inicio, int fim, int qtdCasasDecimais, char tipo) {
		campos.add(new Campo(nome, inicio, fim, qtdCasasDecimais, tipo));
	}
	
	/**
	 * Adiciona o campo setando o nome, tipo, posição inicial e final a partir do tamanho e quantidade de casas decimais.
	 * @param nome
	 * @param tipo
	 * @param tamanho
	 * @param qtdCasasDecimais
	 */
	public void addCampo(String nome, char tipo, int tamanho, int qtdCasasDecimais) {
		campos.add(new Campo(nome, campoAtual, (campoAtual+tamanho), qtdCasasDecimais, tipo));
		campoAtual+=tamanho;
	}
	
	public ArrayList<Campo> getCampos() {
		return campos;
	}

	public void setCampos(ArrayList<Campo> campos) {
		this.campos = campos;
	}

	public String getLinha() {
		return linha;
	}

	public void setDados(String linha, int idLinhaSiape, int idProcessamento) {
		this.linha = linha;
		this.idLinhaArquivo = idLinhaSiape;
		this.idProcessamento = idProcessamento;
	}
	
	public void setLinha(String linha) {
		this.linha = linha;
	}

	public Integer getTipo() {
		return tipo;
	}

	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}

	public int getIdLinhaArquivo() {
		return idLinhaArquivo;
	}

	public void setIdLinhaArquivo(int idLinhaArquivo) {
		this.idLinhaArquivo = idLinhaArquivo;
	}

	public int getIdProcessamento() {
		return idProcessamento;
	}

	public void setIdProcessamento(int idProcessamento) {
		this.idProcessamento = idProcessamento;
	}

	public boolean isCriticado() {
		return criticado;
	}

	public void setCriticado(boolean criticado) {
		this.criticado = criticado;
	}

	public void gerarLinha() {

		StringBuffer linhaBuffer = new StringBuffer();
		if (tipo != null) {
			linhaBuffer.append(tipo);
		}
		for ( Campo c : campos ) {
			int size = c.getFim() - c.getInicio();
			
				if ( c.getTipo() == 'X'){
					linhaBuffer.append(completaEspacos(c.getValue(), size));					
				}else{
					
					if(c.getValue().length() != size)
						linhaBuffer.append(UFRNUtils.completaZeros(new Long(c.getValue()), size));
					else
						linhaBuffer.append(c.getValue());
					
				}
		}

		linhaConstruida = linhaBuffer.toString();
	}

	public String getLinhaConstruida() {
		return linhaConstruida;
	}

	public static String completaEspacos(String informacao, int totalEspacos) {
		int size = informacao.length();
		String branco = "";
		for (int a = 0; a < totalEspacos - size; a++) {
			branco += " ";
		}
		return informacao + branco;
	}

	public void setCampo(String nome, Object valor){
		for(Campo c : campos){
			if(c.getNome().equalsIgnoreCase(nome)){
				if (valor instanceof String)
					c.setValue((String)valor);
				else
					c.setValue((Integer)valor);
			}
		}
	}
}