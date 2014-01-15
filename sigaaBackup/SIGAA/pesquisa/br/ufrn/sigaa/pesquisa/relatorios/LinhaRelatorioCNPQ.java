package br.ufrn.sigaa.pesquisa.relatorios;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Linha auxiliar responsável pela formação das linhas e do cabeçalho dos relatórios CNPq
 * 
 * @author Jean Guerethes
 */
public class LinhaRelatorioCNPQ {

	private Map<ColunaItemRelatorioCNPQ, Boolean> cabecalho = new TreeMap<ColunaItemRelatorioCNPQ, Boolean>();
	private String descricaoLinha;
	private List<String> valoresRelatorio = new ArrayList<String>();

	public String getDescricaoLinha() {
		return descricaoLinha;
	}
	public void setDescricaoLinha(String descricaoLinha) {
		this.descricaoLinha = descricaoLinha;
	}
	public List<String> getValoresRelatorio() {
		return valoresRelatorio;
	}
	public void setValoresRelatorio(List<String> valoresRelatorio) {
		this.valoresRelatorio = valoresRelatorio;
	}
	public Map<ColunaItemRelatorioCNPQ, Boolean> getCabecalho() {
		return cabecalho;
	}
	public void setCabecalho(Map<ColunaItemRelatorioCNPQ, Boolean> cabecalho) {
		this.cabecalho = cabecalho;
	}
	
	public static Map<ColunaItemRelatorioCNPQ, Boolean> getCabecalhoCorpoDiscente(boolean pibit){
		Map<ColunaItemRelatorioCNPQ, Boolean> cabecalho = new TreeMap<ColunaItemRelatorioCNPQ, Boolean>();
		cabecalho.put(new ColunaItemRelatorioCNPQ(0, ""), false);		
		cabecalho.put(new ColunaItemRelatorioCNPQ(1, "Graduação"), true);
		cabecalho.put(new ColunaItemRelatorioCNPQ(2, "Mestrado"), true);
		if ( pibit )
			cabecalho.put(new ColunaItemRelatorioCNPQ(3, "Mestrado Profissionalizante"), true);
		
		cabecalho.put(new ColunaItemRelatorioCNPQ(4, "Doutorado"), true);
		return cabecalho;
	}

	public static Map<ColunaItemRelatorioCNPQ, Boolean> getCabecalhoCorpoDocente(){
		Map<ColunaItemRelatorioCNPQ, Boolean> cabecalho = new TreeMap<ColunaItemRelatorioCNPQ, Boolean>();
		cabecalho.put(new ColunaItemRelatorioCNPQ(0, ""), false);
		cabecalho.put(new ColunaItemRelatorioCNPQ(1, "40 horas"), true);
		cabecalho.put(new ColunaItemRelatorioCNPQ(2, "20 horas"), true);
		cabecalho.put(new ColunaItemRelatorioCNPQ(3, "Menos de 20 horas"), true);
		cabecalho.put(new ColunaItemRelatorioCNPQ(4, "Total"), true);
		return cabecalho;
	}

	public static Map<ColunaItemRelatorioCNPQ, Boolean> getCabecalhoPesquisaDesenvolvidaNaInstituicao(){
		Map<ColunaItemRelatorioCNPQ, Boolean> cabecalho = new TreeMap<ColunaItemRelatorioCNPQ, Boolean>();
		cabecalho.put(new ColunaItemRelatorioCNPQ(0, "Atividade"), false);
		cabecalho.put(new ColunaItemRelatorioCNPQ(1, "Total"), true);
		return cabecalho;
	}

	public static Map<ColunaItemRelatorioCNPQ, Boolean> getCabecalhoIniciacaoAoDesenvolvimento( boolean pibit ){
		String tipoBolsa = pibit ? "IT" : "IC";
		Map<ColunaItemRelatorioCNPQ, Boolean> cabecalho = new TreeMap<ColunaItemRelatorioCNPQ, Boolean>();
		cabecalho.put(new ColunaItemRelatorioCNPQ(0, "Ano"), false);
		cabecalho.put(new ColunaItemRelatorioCNPQ(1, tipoBolsa + " da Instituição"), false);
		cabecalho.put(new ColunaItemRelatorioCNPQ(2, tipoBolsa + " voluntário"), true);
		cabecalho.put(new ColunaItemRelatorioCNPQ(3, tipoBolsa + " da Fundação de Amparo à Pesquisa do Estado/Secretária de C&T"), true);
		cabecalho.put(new ColunaItemRelatorioCNPQ(4, tipoBolsa + " de Outras Instituições"), true);
		if ( pibit )
			cabecalho.put(new ColunaItemRelatorioCNPQ(5, "Bolsas de PIBIC"), true);
		return cabecalho;
	}

	public static Map<ColunaItemRelatorioCNPQ, Boolean> getCabecalhoPesquisadoresCNPQ(){
		Map<ColunaItemRelatorioCNPQ, Boolean> cabecalho = new TreeMap<ColunaItemRelatorioCNPQ, Boolean>();
		cabecalho.put(new ColunaItemRelatorioCNPQ(0, "Pesquisador"), false);
		cabecalho.put(new ColunaItemRelatorioCNPQ(1, "Quantidade de Bolsa Obtida"), true);
		return cabecalho;
	}

	public static Map<ColunaItemRelatorioCNPQ, Boolean> getCabecalhoGrandeAreaConhecimento(){
		Map<ColunaItemRelatorioCNPQ, Boolean> cabecalho = new TreeMap<ColunaItemRelatorioCNPQ, Boolean>();
		cabecalho.put(new ColunaItemRelatorioCNPQ(0, "Grande Área"), false);
		cabecalho.put(new ColunaItemRelatorioCNPQ(1, "Número de bolsistas"), true);
		return cabecalho;
	}
	
	public static Map<ColunaItemRelatorioCNPQ, Boolean> getCabecalhoComiteExterno(){
		Map<ColunaItemRelatorioCNPQ, Boolean> cabecalho = new TreeMap<ColunaItemRelatorioCNPQ, Boolean>();
		cabecalho.put(new ColunaItemRelatorioCNPQ(0, "Nome"), false);
		cabecalho.put(new ColunaItemRelatorioCNPQ(1, "Instituição"), false);
		cabecalho.put(new ColunaItemRelatorioCNPQ(2, "Área de Atuação"), false);
		cabecalho.put(new ColunaItemRelatorioCNPQ(3, "Nível bolsa PQ do CNPq"), true);
		return cabecalho;
	}

	public static Map<ColunaItemRelatorioCNPQ, Boolean> getIndicadoresGerais(){
		Map<ColunaItemRelatorioCNPQ, Boolean> cabecalho = new TreeMap<ColunaItemRelatorioCNPQ, Boolean>();
		cabecalho.put(new ColunaItemRelatorioCNPQ(0, "Status Grupo"), false);
		cabecalho.put(new ColunaItemRelatorioCNPQ(1, "Grupos(G)"), true);
		cabecalho.put(new ColunaItemRelatorioCNPQ(2, "Pesquisadores(P)"), true);
		cabecalho.put(new ColunaItemRelatorioCNPQ(3, "Doutores(D)"), true);
		cabecalho.put(new ColunaItemRelatorioCNPQ(4, "Estudantes(E)"), true);
		cabecalho.put(new ColunaItemRelatorioCNPQ(5, "Técnicos(T)"), true);
		cabecalho.put(new ColunaItemRelatorioCNPQ(6, "Linhas de Pesquisa(L)"), true);
		cabecalho.put(new ColunaItemRelatorioCNPQ(7, "P/G"), true);
		cabecalho.put(new ColunaItemRelatorioCNPQ(8, "D/G"), true);
		cabecalho.put(new ColunaItemRelatorioCNPQ(9, "E/G"), true);
		cabecalho.put(new ColunaItemRelatorioCNPQ(10, "T/G"), true);
		cabecalho.put(new ColunaItemRelatorioCNPQ(11, "L/G"), true);
		return cabecalho;
	}
	
	public static String descricaoLinhaDesenvolvimentoInstituicao(int linha, boolean pibit ){
		String tecn =  pibit ? " Tecnológica " : "";
		switch (linha) {
		case 0:
			if (pibit)
				return "Relacionar os Grupos de Pesquisa na área tecnológica, cadastrados no Diretório de Grupo de Pesquisa do CNPq";
			else
				return "Número de Grupos de Pesquisa Cadastrados no Diretório de Grupos de Pesquisa do CNPq";
		case 1:
			return "Número de Linhas de Pesquisa " + tecn + "  desenvolvidas";
		case 2:
			return "Número de doutores envolvidos com a Pesquisa" + tecn;
		}
		return null;
	}

}