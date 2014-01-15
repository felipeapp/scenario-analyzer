/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '19/09/2008'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.dominio;

import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

/**
 * Classe utilizada na view do relatório RID
 * 
 * @author leonardo
 *
 */
public class ItemRID {

	/** Constantes de visualização do RID */
	public final int ENSINO_GRADUACAO = 1;
	public final int ORIENTACAO_ACADEMICA = 2;
	public final int ENSINO_POS_GRADUACAO = 3;
	public final int ENSINO_EM_NIVEL_DE_EXTENSAO = 4;
	public final int ORIENTACAO_E_CO_ORIENTACAO_DE_TRAB_PESQUISA = 5;
	public final int PROJETO_DE_PESQUISA = 6;
	public final int PRODUCAO_INTELECTUAL = 7;
	public final int ATIVIDADES_DE_EXTENSAO = 8;
	public final int PART_EXP_CONG_SIMPO_ENC_SEM_MESA_REDONDA_OUTRSO = 9;
	public final int PART_BANCA_COMISSAO_EXAMINADORA_DE_CONCURSO_OUTROS = 10;
	public final int PART_COLEGIADO_COMISSAO_OFICIAL_UFRN_REPRESENTACAO = 11;
	public final int FUNCOES_ADMINISTRATIVAS = 12;
	public final int DOCENTE_EM_QUALIFICACAO = 13;
	public final int ATIVIDADES_TECNICAS = 14;
	public final int RESUMO = 15;
	public final int ENSINO_TECNICO = 16;
	public final int ATIVIDADE_ATUALIZACAO_PEDAGOGICA = 17;
	public final int DISCENTE_DE_EXTENSAO = 18;
	public final int PROJETOS_ASSOCIADOS = 19;
	public final int PLANOS_ASSOCIADOS = 20;
	public final int ESTAGIO = 21;
	
	/** Atributo que define o tipo do item do relatório */
	private int tipoItem;

	/** Atributo que define o gruop que pertence o item do relatório */
	private String grupo;
	
	/** Atributo que define o conteúdo do item */
	private String item;
	
	/** Atributo que define o cabeçalho da tabela no item do relatório  */
	private String cabecalho;
	
	/** Atributo que define as linhas da tabela no item do relatório */
	private String body;

	/** Atributo que define o rodapé da tabela no item do relatório */
	private String rodape = "<tfoot>" +
								"<tr>" +
									"<td colspan='6'> Total </td>" +
									"<td class='pontos'> &nbsp; </td>" +
								"</tr>" +
							"</tfoot>" +
							"</table>";
	
	/** Atributo que define o objeto a ser inserido na linha da tabela  */
	private Object o;
	
	/** Atributo que define os pontos a serem exibidos na linha da tabela */
	private Double pontos;
	
	/** Atributo que define o total de pontos a ser exibido na linha da tabela */
	private Double total;
	
	/** Atributo que define as colunas que forma a linha da tabela. */
	private String[] colunas;
	
	/** Atributo que define a pontuacao máxima */
	private Double pontuacaoMax;
	
	/** Atributo que define o membro de um projeto */
	private MembroProjeto membroDocente;
	
	/** Atributo que possui as partes que compõem o cabeçalho */
	private StringBuilder cabecalhoBuilder = new StringBuilder();
	
	public ItemRID() {
		pontuacaoMax = Double.MAX_VALUE;
	}
	
	public ItemRID(Double pontuacaoMax) {
		this.pontuacaoMax = pontuacaoMax;
	}
	
	public ItemRID(int num_colunas) {
		this.colunas = new String[num_colunas];
		this.pontuacaoMax = Double.MAX_VALUE;
	}
	
	public ItemRID(int num_colunas, Double pontuacaoMax) {
		this.colunas = new String[num_colunas];
		this.pontuacaoMax = pontuacaoMax;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "o");
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(o);
	}

	public Object getO() {
		return o;
	}

	public void setO(Object o) {
		this.o = o;
	}

	public Double getPontos() {
		return pontos;
	}

	public void setPontos(Double pontos) {
		this.pontos = pontos;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = Math.min(total, pontuacaoMax);
	}

	public String[] getColunas() {
		return colunas;
	}

	public void setColunas(String[] colunas) {
		this.colunas = colunas;
	}

	public void setMembroDocente(MembroProjeto membroDocente) {
		this.membroDocente = membroDocente;
	}

	public MembroProjeto getMembroDocente() {
		return membroDocente;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public String getCabecalho() {
		return cabecalho;
	}

	public void setCabecalho(String cabecalho) {
		this.cabecalho = cabecalho;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public int getTipoItem() {
		return tipoItem;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public void setTipoItem(int tipoItem) {
		this.tipoItem = tipoItem;
	}
	
	public StringBuilder getCabecalhoBuilder() {
		return cabecalhoBuilder;
	}

	public void setCabecalhoBuilder(StringBuilder cabecalhoBuilder) {
		this.cabecalhoBuilder = cabecalhoBuilder;
	}

	public String getRodape() {
		return rodape;
	}

	public void setRodape(String rodape) {
		this.rodape = rodape;
	}

	/**
	 * Método que monta o corpo da tabela de um item do relatório.
	 * Método não invocado por JSP's.
	 * @param colunas
	 * @return
	 */
	private String montarBody(int colunas){
		StringBuilder body = new StringBuilder();
		body.append("<tbody> <tr>");
			for (int i = 0; i < colunas; i++) {
				if (getColunas()[i] == null) 
					getColunas()[i] = "";
				if (StringUtils.hasLetter(getColunas()[i])) 
					body.append("<td align='left'> "+ getColunas()[i] +" </td>");
				else
					body.append("<td align='center'> "+ getColunas()[i] +" </td>");
			}
		body.append("<td colspan='2'> &nbsp; </td> <td> &nbsp; </td>");
		body.append("</tr> </tbody>");
		return body.toString();
	}
	
	/**
	 * Método que monta o grupo juntamente com a tabela.
	 * Método não invocado por JSP's.
	 * @param tipoItem
	 * @param temElemento
	 */
	public void motarView(int tipoItem, boolean temElemento) {
		switch (tipoItem) {
		case ENSINO_GRADUACAO:
			setGrupo("<h4>GRUPO I: ATIVIDADES DE ENSINO </h4>");
			setItem("<h5>1. Ensino de graduação </h5>");
			cabecalhoBuilder = new StringBuilder();
			cabecalhoBuilder.append("<table class='producoes'>"+
								"<thead>"+
									"<tr>"+
										"<td colspan='7' align='center'>Disciplinas lecionadas</td>"+
										"<td colspan='3' align='center'>Uso da comissão</td>"+
									"</tr>"+
									"<tr>"+
										"<td align='center'>Semestre</td>"+
										"<td align='center'>Código</td>"+
										"<td>Natureza</td>"+
										"<td align='center'>Nº de Créditos</td>"+
										"<td align='center'>Cod. Turma</td>"+
										"<td align='center'>Nº de Prof.</td>"+
										"<td align='center'>Créditos</td>"+
										"<td>Pontos p/ crédito</td>"+
										"<td colspan='2'>Total de pontos</td>"+
									"</tr>"+
								"</thead>");
			if (!temElemento) 
				cabecalhoBuilder.append("");
			setCabecalho(cabecalhoBuilder.toString());	
			if (temElemento) 
				setBody(montarBody(getColunas().length));
			break;
		
		case ORIENTACAO_ACADEMICA:
			setGrupo("<h4>GRUPO I: ATIVIDADES DE ENSINO </h4>");
			setItem("<h5>2. Orientação acadêmica </h5>");
			cabecalhoBuilder = new StringBuilder();
			cabecalhoBuilder.append("<table class='producoes'>"+
								"<thead>"+
									"<tr>"+
										"<td colspan='4' align='center'>Descrição</td>"+
										"<td colspan='3' align='center'>Uso da comissão</td>"+
									"</tr>"+
									"<tr>"+
										"<td align='center'>Período</td>"+
										"<td align='right'>Meses</td>"+
										"<td>Curso</td>"+
										"<td>Aluno</td>"+
										"<td>Nº de pontos</td>"+
										"<td colspan='2'>Total</td>"+
									"</tr>"+
								"</thead>");
			if (!temElemento) 
				cabecalhoBuilder.append("");
			setCabecalho(cabecalhoBuilder.toString());	
			if (temElemento) 
				setBody(montarBody(getColunas().length));
			break;
			
		case ENSINO_POS_GRADUACAO:
			setGrupo("<h4>GRUPO I: ATIVIDADES DE ENSINO </h4>");
			setItem("<h5>3. Ensino de pós-graduação </h5>");
			cabecalhoBuilder = new StringBuilder();
			cabecalhoBuilder.append("<table class='producoes'>"+
										"<thead>"+
											"<tr>"+
												"<td colspan='6' align='center'>Disciplinas lecionadas</td>"+
												"<td colspan='3' align='center'>Uso da comissão</td>"+
											"</tr>"+
											"<tr>"+
												"<td align='center'>Semestre</td>"+
												"<td align='center'>Código</td>"+
												"<td>Curso</td>"+
												"<td align='center'>Nº de Créditos</td>"+
												"<td align='center'>Nº de Prof.</td>"+
												"<td align='center'>Créditos</td>"+
												"<td>Pontos p/ crédito</td>"+
												"<td colspan='2'>Total de pontos</td>"+
											"</tr>"+
										"</thead>");
			if (!temElemento) 
				cabecalhoBuilder.append("");
			setCabecalho(cabecalhoBuilder.toString());	
			if (temElemento) 
				setBody(montarBody(getColunas().length));
			break;

		case ENSINO_EM_NIVEL_DE_EXTENSAO:
			setGrupo("<h4>GRUPO I: ATIVIDADES DE ENSINO </h4>");
			setItem("<h5>4. Ensino em nível de extensão </h5>");
			cabecalhoBuilder = new StringBuilder();
			cabecalhoBuilder.append("<table class='producoes'>"+
										"<thead>"+
											"<tr>"+
												"<td colspan='4' align='center'>Descrição</td>"+
												"<td colspan='3' align='center'>Uso da comissão</td>"+
											"</tr>"+
											"<tr>"+
												"<td align='center'>Semestre</td>"+
												"<td align='left'>Curso</td>"+
												"<td align='right'>Carga horária</td>"+
												"<td align='center'>Nº prof.</td>"+
												"<td>Nº de pontos</td>"+
												"<td colspan='2'>Total</td>"+
											"</tr>"+
									"</thead>");
			if (!temElemento) 
				cabecalhoBuilder.append("");
			setCabecalho(cabecalhoBuilder.toString());	
			if (temElemento) 
				setBody(montarBody(getColunas().length));
			break;
		
		case ORIENTACAO_E_CO_ORIENTACAO_DE_TRAB_PESQUISA:
			setGrupo("<h4>GRUPO II: ATIVIDADES DE PESQUISA </h4>");
			setItem("<h5>1. Orientação e co-orientação de trabalho de pesquisa</h5>");
			cabecalhoBuilder = new StringBuilder();
			cabecalhoBuilder.append("<table class='producoes'>"+
					"<thead>"+
					"<tr>"+
					"<td colspan='5' align='center'>Descrição</td>"+
					"<td colspan='3' align='center'>Uso da comissão</td>"+
					"</tr>"+
					"<tr>"+
					"<td align='center'>Período</td>"+
					"<td align='right'>Meses</td>"+
					"<td>Natureza</td>"+
					"<td>Quantidade Orientadores</td>"+
					"<td>Nome do orientando ou título do trabalho</td>"+
					"<td>Nº de pontos</td>"+
					"<td colspan='2'>Total</td>"+
					"</tr>"+
			"</thead>");
			if (!temElemento) 
				cabecalhoBuilder.append("");
			setCabecalho(cabecalhoBuilder.toString());	
			if (temElemento) 
				setBody(montarBody(getColunas().length));
			break;
			
		case PROJETO_DE_PESQUISA:
			setGrupo("<h4>GRUPO II: ATIVIDADES DE PESQUISA </h4>");
			setItem("<h5>2. Projetos de pesquisa</h5>");
			cabecalhoBuilder = new StringBuilder();
			cabecalhoBuilder.append("<table class='producoes'>"+
								"<thead>"+
									"<tr>"+
										"<td colspan='4' align='center'>Descrição</td>"+
										"<td colspan='3' align='center'>Uso da comissão</td>"+
									"</tr>"+
									"<tr>"+
										"<td align='center'>Período</td>"+
										"<td align='right'>Meses</td>"+
										"<td>Título do projeto</td>"+
										"<td>Tipo de participação</td>"+
										"<td>Nº de pontos</td>"+
										"<td colspan='2'>Total</td>"+
									"</tr>"+
								"</thead>");
			if (!temElemento) 
				cabecalhoBuilder.append("");
			setCabecalho(cabecalhoBuilder.toString());	
			if (temElemento) 
				setBody(montarBody(getColunas().length));
			break;
		
		case PRODUCAO_INTELECTUAL:
			setGrupo("<h4>GRUPO II: ATIVIDADES DE PESQUISA </h4>");
			setItem("<h5>3. Produção científica</h5>");
			cabecalhoBuilder = new StringBuilder();
			cabecalhoBuilder.append("<table class='producoes'>"+
											"<thead>"+
												"<tr>"+
													"<td colspan='5' align='center'>Descrição</td>"+
													"<td colspan='3' align='center'>Uso da comissão</td>"+
												"</tr>"+
												"<tr>"+
													"<td align='center'>Semestre</td>"+
													"<td align='center'>Data</td>"+
													"<td>Tipo</td>"+
													"<td>Título e publicação</td>"+
													"<td>Indexação/circulação</td>"+
													"<td>Nº de pontos</td>"+
													"<td colspan='2'>Total</td>"+
												"</tr>"+
											"</thead>");
			if (!temElemento) 
				cabecalhoBuilder.append("");
			setCabecalho(cabecalhoBuilder.toString());	
			if (temElemento) 
				setBody(montarBody(getColunas().length));
			break;
		
		case ATIVIDADES_DE_EXTENSAO:
			setGrupo("<h4>GRUPO III: ATIVIDADES DE EXTENSÃO </h4>");
			setItem("<h5>1. Projetos de Extensão</h5>");
			cabecalhoBuilder = new StringBuilder();
			cabecalhoBuilder.append("<table class='producoes'>"+
										"<thead>"+
											"<tr>"+
												"<td colspan='4' align='center'>Descrição</td>"+
												"<td colspan='3' align='center'>Uso da comissão</td>"+
											"</tr>"+
											"<tr>"+
												"<td align='center'>Semestre</td>"+
												"<td>Tipo de atividade</td>"+
												"<td>Título</td>"+
												"<td>Função</td>"+
												"<td>Nº de pontos</td>"+
												"<td colspan='2'>Total</td>"+
											"</tr>"+
										 "</thead>");
			if (!temElemento) 
				cabecalhoBuilder.append("");
			setCabecalho(cabecalhoBuilder.toString());	
			if (temElemento) 
				setBody(montarBody(getColunas().length));
			break;

		case DISCENTE_DE_EXTENSAO:
			setGrupo("<h4>GRUPO III: ATIVIDADES DE EXTENSÃO </h4>");
			setItem("<h5>2. Orientação de Bolsistas de Extensão</h5>");
			cabecalhoBuilder = new StringBuilder();
			cabecalhoBuilder.append("<table class='producoes'>"+
										"<thead>"+
											"<tr>"+
												"<td colspan='4' align='center'>Discente</td>"+
												"<td colspan='3' align='center'>Uso da comissão</td>"+
											"</tr>"+
											"<tr>"+
												"<td align='center'>Discente</td>"+
												"<td>Atividade Extensão</td>"+
												"<td>Data Início</td>"+
												"<td>Data Fim</td>"+
												"<td>Nº de pontos</td>"+
												"<td colspan='2'>Total</td>"+
											"</tr>"+
										 "</thead>");
			if (!temElemento) 
				cabecalhoBuilder.append("");
			setCabecalho(cabecalhoBuilder.toString());	
			if (temElemento) 
				setBody(montarBody(getColunas().length));
			break;

			
		case PART_EXP_CONG_SIMPO_ENC_SEM_MESA_REDONDA_OUTRSO:
			setGrupo("<h4>GRUPO V: PARTICIPAÇÃO EM EVENTOS CIENTÍFICOS, CULTURAIS E COLEGIADOS </h4>");
			setItem("<h5>1. Participação em exposição, congresso, simpósio, encontro, seminário, mesa-redonda e outros eventos</h5>");
			cabecalhoBuilder = new StringBuilder();
			cabecalhoBuilder.append("<table class='producoes'>"+
										"<thead>"+
											"<tr>"+
												"<td colspan='5' align='center'>Descrição</td>"+
												"<td colspan='3' align='center'>Uso da comissão</td>"+
											"</tr>"+
											"<tr>"+
												"<td align='center'>Semestre</td>"+
												"<td>Evento</td>"+
												"<td>Tipo de participação</td>"+
												"<td>Título/Local</td>"+
												"<td>Nível</td>"+
												"<td>Nº de pontos</td>"+
												"<td colspan='2'>Total</td>"+
											"</tr>"+
										"</thead>");
			if (!temElemento) 
				cabecalhoBuilder.append("");
			setCabecalho(cabecalhoBuilder.toString());
			if (temElemento) 
				setBody(montarBody(getColunas().length));
			break;

		case PART_BANCA_COMISSAO_EXAMINADORA_DE_CONCURSO_OUTROS:
			setGrupo("<h4>GRUPO V: PARTICIPAÇÃO EM EVENTOS CIENTÍFICOS, CULTURAIS E COLEGIADOS </h4>");
			setItem("<h5>2. Participação em banca ou comissão examinadora de concurso e outros</h5>");
			cabecalhoBuilder = new StringBuilder();
			cabecalhoBuilder.append("<table class='producoes'>"+
										"<thead>"+
											"<tr>"+
												"<td colspan='5' align='center'>Descrição</td>"+
												"<td colspan='3' align='center'>Uso da comissão</td>"+
											"</tr>"+
											"<tr>"+
												"<td align='center'>Semestre</td>"+
												"<td width='30%'>Banca/comissão</td>"+
												"<td align='center'>Data</td>"+
												"<td align='center'>Nível</td>"+
												"<td>Categoria funcional/Natureza do exame</td>"+
												"<td>Nº de pontos</td>"+
												"<td colspan='2'>Total</td>"+
											"</tr>"+
										"</thead>");
			if (!temElemento) 
				cabecalhoBuilder.append("");
			setCabecalho(cabecalhoBuilder.toString());
			if (temElemento) 
				setBody(montarBody(getColunas().length));
			break;

		case PART_COLEGIADO_COMISSAO_OFICIAL_UFRN_REPRESENTACAO:
			setGrupo("<h4>GRUPO V: PARTICIPAÇÃO EM EVENTOS CIENTÍFICOS, CULTURAIS E COLEGIADOS </h4>");
			setItem("<h5>3. Participação em colegiado ou comissão oficial da " + RepositorioDadosInstitucionais.getSiglaInstituicao() + " e representação junto outros órgãos</h5>");
			cabecalhoBuilder = new StringBuilder();
			cabecalhoBuilder.append("<table class='producoes'>"+
									 "<thead>"+
										"<tr>"+
											"<td colspan='4' align='center'>Descrição</td>"+
											"<td colspan='3' align='center'>Uso da comissão</td>"+
										"</tr>"+
										"<tr>"+
											"<td align='center'>Período</td>"+
											"<td align='right'>Meses</td>"+
											"<td>Conselho/comissão/câmara etc.</td>"+
											"<td>Nº reuniões</td>"+
											"<td>Nº de pontos</td>"+
											"<td colspan='2'>Total</td>"+
										"</tr>"+
									"</thead>");
			if (!temElemento) 
				cabecalhoBuilder.append("");
			setCabecalho(cabecalhoBuilder.toString());
			if (temElemento) 
				setBody(montarBody(getColunas().length));
			break;

		case FUNCOES_ADMINISTRATIVAS:
			setGrupo("<h4>GRUPO VI: FUNÇÕES ADMINISTRATIVAS </h4>");
			setItem("<h5>&nbsp;</h5>");
			cabecalhoBuilder = new StringBuilder();
			cabecalhoBuilder.append("<table class='producoes'>"+
										"<thead>"+
											"<tr>"+
												"<td colspan='3' align='center'>Descrição</td>"+
												"<td colspan='5' align='center'>Uso da comissão</td>"+
											"</tr>"+
											"<tr>"+
												"<td align='center'>Período</td>"+
												"<td align='right'>Meses</td>"+
												"<td>Função</td>"+
												"<td colspan='2'>Nº de pontos</td>"+
												"<td colspan='3'>Total</td>"+
											"</tr>"+
										"</thead>");
			if (!temElemento) 
				cabecalhoBuilder.append("");
			setCabecalho(cabecalhoBuilder.toString());
			if (temElemento) 
				setBody(montarBody(getColunas().length));
			break;

		case DOCENTE_EM_QUALIFICACAO:
			setGrupo("<h4>GRUPO VII: DOCENTE EM QUALIFICAÇÃO (REALIZANDO CURSO) </h4>");
			setItem("<h5>1. Cursos do Docente</h5>");
			cabecalhoBuilder = new StringBuilder();
			cabecalhoBuilder.append("<table class='producoes'>"+
										"<thead>"+
											"<tr>"+
												"<td colspan='4' align='center'>Descrição</td>"+
												"<td colspan='3' align='center'>Uso da comissão</td>"+
											"</tr>"+
											"<tr>"+
												"<td align='center'>Período</td>"+
												"<td align='right'>Meses</td>"+
												"<td>Curso/nível</td>"+
												"<td>Fase/situação</td>"+
												"<td>Nº de pontos</td>"+
												"<td colspan='2'>Total</td>"+
											"</tr>"+
										"</thead>");
			if (!temElemento) 
				cabecalhoBuilder.append("");
			setCabecalho(cabecalhoBuilder.toString());
			if (temElemento) 
				setBody(montarBody(getColunas().length));
			break;

		case ATIVIDADES_TECNICAS:
			setGrupo("<h4>GRUPO VIII: ATIVIDADES TÉCNICAS </h4>");
			setItem("<h5>&nbsp;</h5>");
			cabecalhoBuilder.append("<table class='producoes'>"+
										"<thead>"+
											"<tr>"+
												"<td colspan='3' align='center'>Descrição</td>"+
												"<td colspan='4' align='center'>Uso da comissão</td>"+
											"</tr>"+
											"<tr>"+
												"<td align='center'>Semestre</td>"+
												"<td>Atividade</td>"+
												"<td align='right'>Nº de horas semanais</td>"+
												"<td colspan='2'>Nº de pontos</td>"+
												"<td colspan='2'>Total</td>"+
											"</tr>"+
										"</thead>");
			if (!temElemento) 
				cabecalhoBuilder.append("");
			setCabecalho(cabecalhoBuilder.toString());
			if (temElemento) 
				setBody(montarBody(getColunas().length));
			break;
		
		case RESUMO:
			setGrupo("");
			setItem("");
			cabecalhoBuilder.append("<div id='quadro-resumo'>"+
									"<h4> Para uso da comissão de avaliação </h4>"+
										"<h5>1. Pontuação obtida pelo docente em cada grupo de atividade</h5>"+
											"<table>"+
												"<thead>"+
													"<tr>"+
														"<td>GRUPO</td>"+
														"<td>TOTAL DE PONTOS</td>"+
													"</tr>"+
												"</thead>"+
												"<tbody>"+
												  	"<tr class='grupo'>"+
												  		"<td> I. Atividades de Ensino </td>"+
												  		"<td class='pontos'> </td>"+
												  	"</tr>"+
												  	"<tr class='grupo'>"+
												  		"<td> II. Atividades de Pesquisa </td>"+
												  		"<td class='pontos'>  </td>"+
												  	"</tr>"+
												  	"<tr class='grupo'>"+
												  		"<td> III. Atividades de Extensão </td>"+
												  		"<td class='pontos'> </td>"+
												  	"</tr>"+
												  	"<tr class='grupo'>"+
												  		"<td> IV. Participação em eventos científicos, culturais e colegiados </td>"+
												  		"<td class='pontos'> </td>"+
												  	"</tr>"+
												  	"<tr class='grupo'>"+
												  		"<td> V. Funções Administrativas </td>"+
												  		"<td class='pontos'> </td>"+
												  	"</tr>"+
												  	"<tr class='grupo'>"+
												  		"<td> VI. Docente em qualificação </td>"+
												  		"<td class='pontos'> </td>"+
												  	"</tr>"+
												  	"<tr class='grupo'>"+
												  		"<td> VII. Atividades Técnicas </td>"+
												  		"<td class='pontos'> </td>"+
												  	"</tr>"+
											  "</tbody>"+
											  "<tfoot>"+
											  		"<tr>"+
											  			"<td> Total Geral de Pontos </td>"+
											  			"<td class='pontos'> </td>"+
											  		"</tr>"+
											  "</tfoot>"+
											  "</table>"+
											  "<br />"+
											  "<h5>2. Resumo</h5>"+
											  	"<table>"+
											  		"<tbody>"+
											  			"<tr class='grupo'>"+
											  				"<td> Número total de semestres avaliados </td>"+
											  				"<td class='pontos'>  </td>"+
											  			"</tr>"+
											  			"<tr class='grupo'>"+
											  				"<td> Número de semestres em regime de 40 horas ou Dedicação Exclusiva </td>"+
											  				"<td class='pontos'>  </td>"+
											  			"</tr>"+
											  			"<tr class='grupo'>"+
											  				"<td> Número de semestres em regime de 20 horas </td>"+
											  				"<td class='pontos'>  </td>"+
											  			"</tr>"+
											  			"<tr class='grupo'>"+
											  				"<td> Total geral de pontos nos semestres avaliados  </td>"+
											  				"<td class='pontos'>  </td>"+
											  			"</tr>"+
											  			"<tr class='grupo'>"+
											  				"<td> Média de pontos por semestre </td>"+
											  				"<td class='pontos'>  </td>"+
											  			"</tr>"+
											  		"</tbody>"+
											  "</table>"+
									"</div>");
			setBody(cabecalhoBuilder.toString());
			break;
			
		case ENSINO_TECNICO:
			setGrupo("<h4>GRUPO I: ATIVIDADES DE ENSINO </h4>");
			setItem("<h5>5. Ensino de técnico </h5>");
			cabecalhoBuilder = new StringBuilder();
			cabecalhoBuilder.append("<table class='producoes'>"+
								"<thead>"+
									"<tr>"+
										"<td colspan='7' align='center'>Disciplinas lecionadas</td>"+
										"<td colspan='3' align='center'>Uso da comissão</td>"+
									"</tr>"+
									"<tr>"+
										"<td align='center'>Semestre</td>"+
										"<td align='center'>Código</td>"+
										"<td>Natureza</td>"+
										"<td align='center'>Nº de Créditos</td>"+
										"<td align='center'>Cod. Turma</td>"+
										"<td align='center'>Nº de Prof.</td>"+
										"<td align='center'>Créditos</td>"+
										"<td>Pontos p/ crédito</td>"+
										"<td colspan='2'>Total de pontos</td>"+
									"</tr>"+
								"</thead>");
			if (!temElemento) 
				cabecalhoBuilder.append("");
			setCabecalho(cabecalhoBuilder.toString());	
			if (temElemento) 
				setBody(montarBody(getColunas().length));
			break;
			
		case ATIVIDADE_ATUALIZACAO_PEDAGOGICA:
			setGrupo("<h4>GRUPO VI: DOCENTE EM QUALIFICAÇÃO (REALIZANDO CURSO) </h4>");	
			setItem("<h5>2. Atividades de Atualização Pedagógica </h5>");
			cabecalhoBuilder = new StringBuilder();
			cabecalhoBuilder.append("<table class='producoes'>"+
								"<thead>"+
									"<tr>"+
										"<td class=\"titulo\">Título</td>"+
										"<td class=\"data\">Período</td>"+
										"<td class=\"ch\">CH</td>"+
										"<td >Nº de pontos</td>"+
										"<td colspan='4'>Total</td>"+
									"</tr>"+
								"</thead>");
			if (!temElemento) 
				cabecalhoBuilder.append("");
			setCabecalho(cabecalhoBuilder.toString());	
			if (temElemento) 
				setBody(montarBody(getColunas().length));
			break;
		
		case PROJETOS_ASSOCIADOS:
			setGrupo("<h4>GRUPO IV: PROJETOS INTEGRADOS </h4>");
			setItem("<h5>1. Projeto Integrados.</h5>");
			cabecalhoBuilder = new StringBuilder();
			cabecalhoBuilder.append("<table class='producoes'>"+
								"<thead>"+
									"<tr>"+
										"<td colspan='4' align='center'>Descrição</td>"+
										"<td colspan='3' align='center'>Uso da comissão</td>"+
									"</tr>"+
									"<tr>"+
										"<td align='center'>Período</td>"+
										"<td align='right'>Meses</td>"+
										"<td>Título do projeto</td>"+
										"<td>Tipo de participação</td>"+
										"<td>Nº de pontos</td>"+
										"<td colspan='2'>Total</td>"+
									"</tr>"+
								"</thead>");
			if (!temElemento) 
				cabecalhoBuilder.append("");
			setCabecalho(cabecalhoBuilder.toString());	
			if (temElemento) 
				setBody(montarBody(getColunas().length));
			break;
		
		case PLANOS_ASSOCIADOS:
			setGrupo("<h4>GRUPO IV: PROJETOS INTEGRADOS </h4>");
			setItem("<h5>2. Planos Integrados.</h5>");
			cabecalhoBuilder = new StringBuilder();
			cabecalhoBuilder.append("<table class='producoes'>"+
					"<thead>"+
					"<tr>"+
					"<td colspan='4' align='center'>Descrição</td>"+
					"<td colspan='3' align='center'>Uso da comissão</td>"+
					"</tr>"+
					"<tr>"+
					"<td align='center'>Período</td>"+
					"<td align='right'>Meses</td>"+
					"<td>Dimensões</td>"+
					"<td>Nome do orientando ou título do trabalho</td>"+
					"<td>Nº de pontos</td>"+
					"<td colspan='2'>Total</td>"+
					"</tr>"+
			"</thead>");
			if (!temElemento) 
				cabecalhoBuilder.append("");
			setCabecalho(cabecalhoBuilder.toString());	
			if (temElemento) 
				setBody(montarBody(getColunas().length));
			break;

		case ESTAGIO:
			setGrupo("<h4>GRUPO I: ATIVIDADES DE ENSINO </h4>");
			setItem("<h5>6. Estágio</h5>");
			cabecalhoBuilder = new StringBuilder();
			cabecalhoBuilder.append("<table class='producoes'>"+
					"<thead>"+
					"<tr>"+
					"<td colspan='4' align='center'>Descrição</td>"+
					"<td colspan='3' align='center'>Uso da comissão</td>"+
					"</tr>"+
					"<tr>"+
					"<td align='center'>Período</td>"+
					"<td align='right'>Meses</td>"+
					"<td>Estágio</td>"+
					"<td>Discente</td>"+
					"<td>Nº de pontos</td>"+
					"<td colspan='2'>Total</td>"+
					"</tr>"+
			"</thead>");
			if (!temElemento) 
				cabecalhoBuilder.append("");
			setCabecalho(cabecalhoBuilder.toString());	
			if (temElemento) 
				setBody(montarBody(getColunas().length));
			break;
		}
		
	}
}