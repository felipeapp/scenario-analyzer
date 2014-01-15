<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
<%@page import="br.ufrn.sigaa.pesquisa.form.MembroProjetoDiscenteForm"%>

    <ul>
    	<li> Projetos de Pesquisa
    		<ul>
	        <li> <html:link action="/pesquisa/projetoPesquisa/buscarProjetos?dispatch=consulta&popular=true&aba=relatorios">Consultar</html:link> </li>
			<li> <html:link action="/pesquisa/projetoPesquisa/buscarProjetos?dispatch=financiamentos&popular=true&aba=relatorios"> Projetos Financiados </html:link> </li>
			<li>
				<html:link action="/pesquisa/relatoriosPesquisa?dispatch=popularFinanciamentosSintetico&aba=relatorios">
					Relatório Sintético de Financiamentos
				</html:link>
			</li>
			<li>
				<html:link action="/pesquisa/editalSintetico?dispatch=popularEditais&aba=relatorios">
					Relatório de Submissão de Projetos
				</html:link>
			</li>

			<li>
				<html:link action="/pesquisa/distribuirProjetoPesquisa?dispatch=consultaResultadoDistribuicao&aba=relatorios">
					Projetos com avaliações pendentes
				</html:link>
			</li>
			<li> <html:link action="/pesquisa/avaliarRelatorioProjeto?dispatch=listRelatoriosFinais&popular=true&aba=relatorios"> Relatórios Finais </html:link></li>
			<li> <a href="${ctx}/pesquisa/relatorios/form_estatisticas_projetos.jsf"> Estatísticas de Cadastros de Projetos </a></li>
			<li> <a href="${ctx}/pesquisa/relatorios/form_quant_projetos.jsf"> Relatório Quantitativo de Projetos </a></li>
			</ul>
		</li>

		<li> Iniciação Científica
			<ul>
			<li>
				<html:link action="/pesquisa/editalSintetico?dispatch=popularCotas&aba=relatorios">
					Relatório de Cotas Solicitadas
				</html:link>
			</li>
			<li>
				<html:link action="/pesquisa/relatorios/quantitativoSolicitacoesCotas?dispatch=iniciar&aba=relatorios">
					Relatório Quantitativo de Solicitações de Bolsas
				</html:link>
			</li>
			<li>
				<html:link action="/pesquisa/relatorios/acompanhamentoCotas?dispatch=iniciar&aba=relatorios">
					Acompanhamento de Distribuição de Cotas de Bolsas
				</html:link>
			</li>
			<li> <h:commandLink action="#{relatorioPendenciasIndicacao.iniciarRelatorioPendenciasIndicacao}" value="Relatório de Pendências de Indicação" onclick="setAba('relatorios');"/> </li>
			<li> <h:commandLink action="#{relatorioResumosCic.iniciarRelatorioResumosCic}" value="Relatório Quantitativo de Resumos CIC"/> </li>
			<li> <h:commandLink action="#{avaliacaoApresentacaoResumoBean.popularRelatorioAvaliadores}" value="Relatório de Avaliadores de Trabalhos do CIC" onclick="setAba('relatorios');"/> </li>
			<li> <h:commandLink action="#{avaliacaoApresentacaoResumoBean.popularRelatorioPontuacao}" value="Relatório de Premiação de Trabalhos do CIC" onclick="setAba('relatorios');"/> </li>
			<li> <h:commandLink action="#{relatorioRenovacaoBolsaMBean.selecionaQuantitativoBolsaPibic}" value="Relatório Quantitativo de Renovação de Bolsas" onclick="setAba('relatorios');"/> </li>
			<li> <a href="${ctx}/pesquisa/relatorios/form_bolsas_pesquisa.jsf?aba=relatorios"> Relatório Quantitativo de Bolsas de Pesquisa Ativas </a> </li>
			<li> <a href="${ctx}/pesquisa/relatorios/form_resumo_cotas.jsf"> Relatório Quantitativo de Cotas de Bolsas</a> </li>
			<li> <h:commandLink action="#{relatoriosPesquisaMBean.gerarRelatorioQuantBolsasCentroDepartamento}" value="Relatório Quantitativo de Bolsas por Centro/Departamento" onclick="setAba('relatorios');"/> </li>
			</ul>
		</li>
		
		<li> Docentes
			<ul>
			<li> <a href="${ctx}/administracao/cadastro/Servidor/lista.jsf?aba=relatorios">Consulta de Docentes</a></li>
			<li> <html:link action="/pesquisa/relatorioParticipacaoDocentes?dispatch=iniciar&aba=relatorios"> Participação em Projetos de Pesquisa </html:link></li>
			<li> <h:commandLink action="#{relatorioCargaHorariaPesquisaBean.gerar}" value="Carga Horária Dedicada a Projetos de Pesquisa"/> </li>
			
			</ul>
		</li>
		
		<li> Censo
			<ul>
			<li> <h:commandLink action="#{relatoriosCenso.relatorioProjetosDocente}" value="Relatório de Tempo de Dedicação à Pesquisa por Grau de Formação"/> </li>
			<li> Relatório de Tempo de Dedicação à Pesquisa por Área de Conhecimento
				<ul>
					<li> <h:commandLink action="#{relatoriosCenso.relatorioProjetosArea}" value="Relatório Sintético"/> </li>
					<li> <h:commandLink action="#{relatoriosCenso.relatorioProjetosAreaAnalitico}" value="Relatório Analítico"/> </li>
				</ul>
			</li>
			<li> <h:commandLink action="#{relatoriosCNPQMBean.gerarIndicadoresGerais}" value="Indicadores Gerais" onclick="setAba('relatorios');"/> </li>
			</ul>
		</li>

		<li> Relatórios CNPq
			<ul>
				<li> <h:commandLink action="#{relatoriosCNPQMBean.iniciarCorpoDiscente}" value="Corpo Discente" onclick="setAba('relatorios');"/> </li>
				<li> <h:commandLink action="#{relatoriosCNPQMBean.gerarCorpoDocente}" value="Corpo Docente" onclick="setAba('relatorios');"/> </li>
				<li> <h:commandLink action="#{relatoriosCNPQMBean.iniciarEnvolvidosComPesquisa}" value="Pesquisa Científica desenvolvida na Instituição" onclick="setAba('relatorios');"/> </li>
				<li> <h:commandLink action="#{relatoriosCNPQMBean.iniciarBolsasInstituicao}" value="Número de Bolsas de Iniciação ao Densevolvimento" onclick="setAba('relatorios');"/> </li>
				<li> <h:commandLink action="#{relatoriosCNPQMBean.iniciarPesquisadoresCNPQ}" value="Relatório Pesquisadores Produtividade CNPQ" onclick="setAba('relatorios');"/> </li>
				<li> <h:commandLink action="#{relatoriosCNPQMBean.iniciarRelatorioGrandeArea}" value="Relatório Bolsistas Grande Área" onclick="setAba('relatorios');"/> </li>
				<li> <h:commandLink action="#{relatoriosCNPQMBean.gerarComiteExterno}" value="Relatório Comitê Externo" onclick="setAba('relatorios');"/> </li>
				<li> <h:commandLink action="#{relatoriosCNPQMBean.gerarComiteInstitucional}" value="Relatório Comitê Institucional" onclick="setAba('relatorios');"/> </li>
			</ul>
		</li>

    </ul>