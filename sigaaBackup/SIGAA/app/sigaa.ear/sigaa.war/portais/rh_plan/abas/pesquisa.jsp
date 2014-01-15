
<ul>
	<li> Projetos de Pesquisa
   		<ul>
        <li> <a href="${ctx}/pesquisa/projetoPesquisa/buscarProjetos.do?dispatch=consulta&popular=true&aba=pesquisa">Consultar</a> </li>
		<li> <a href="${ctx}/pesquisa/projetoPesquisa/buscarProjetos.do?dispatch=financiamentos&popular=true&aba=pesquisa"> Projetos Financiados </a> </li>
		<li>
			<a href="${ctx}/pesquisa/relatoriosPesquisa.do?dispatch=popularFinanciamentosSintetico&aba=pesquisa">
				Relatório Sintético de Financiamentos
			</a>
		</li>
		<li>
			<a href="${ctx}/pesquisa/editalSintetico.do?dispatch=popularEditais&aba=pesquisa">
				Relatório de Submissão de Projetos
			</a>
		</li>

		<li>
			<a href="${ctx}/pesquisa/distribuirProjetoPesquisa.do?dispatch=consultaResultadoDistribuicao&aba=pesquisa">
				Projetos com avaliações pendentes
			</a>
		</li>
		<li> <a href="${ctx}/pesquisa/avaliarRelatorioProjeto.do?dispatch=listRelatoriosFinais&popular=true&aba=pesquisa"> Relatórios Finais </a></li>
		<li> <h:commandLink value="Projetos de Infra-Estrutura" action="#{ projetoInfraPesq.listar }" onclick="setAba('pesquisa')"/> </li>
		</ul>
	</li>
	<li> Iniciação Científica
		<ul>
		<li>
			<a href="${ctx}/pesquisa/editalSintetico.do?dispatch=popularCotas&aba=pesquisa">
				Relatório de Cotas Solicitadas
			</a>
		</li>
		<li>
			<a href="${ctx}/pesquisa/relatorios/acompanhamentoCotas.do?dispatch=iniciar&aba=pesquisa">
				Acompanhamento de Distribuição de Cotas de Bolsas
			</a>
		</li>
		<li><a href="${ctx }/pesquisa/relatorios/quantitativoSolicitacoesCotas.do?dispatch=iniciar&aba=pesquisa">Relatório Quantitativo de Solicitações de Bolsas</a></li>
		<li> <h:commandLink action="#{relatorioRenovacaoBolsaMBean.selecionaQuantitativoBolsaPibic}" value="Relatório Quantitativo de Renovação de Bolsas" onclick="setAba('pesquisa');"/> </li>
		<li> <a href="${ctx}/pesquisa/relatorios/form_bolsas_pesquisa.jsf?aba=pesquisa"> Relatório Quantitativo de Bolsas de Pesquisa Ativas </a> </li>
		<li> <a href="${ctx}/pesquisa/relatorios/form_resumo_cotas.jsf?aba=pesquisa"> Relatório Quantitativo de Cotas de Bolsas</a> </li>
		<li> <h:commandLink action="#{relatoriosPesquisaMBean.gerarRelatorioQuantBolsasCentroDepartamento}" value="Relatório Quantitativo de Bolsas por Centro/Departamento" onclick="setAba('pesquisa');"/> </li>
		<li> <h:commandLink action="#{relatoriosCNPQMBean.iniciarPesquisadoresCNPQ}" value="Relatório Pesquisadores Produtividade CNPQ" onclick="setAba('pesquisa');"/> </li>
		</ul>
	</li>
	<li> Docentes
		<ul>
		<li> <a href="${ctx}/pesquisa/relatorioParticipacaoDocentes.do?dispatch=iniciar&aba=pesquisa"> Relatório de Participação em Projetos de Pesquisa </a></li>
		</ul>
	</li>
	<li> Censo
		<ul>
		<li> <h:commandLink action="#{relatoriosCenso.relatorioProjetosDocente}" value="Relatório de Tempo de Dedicação à Pesquisa por Grau de Formação" onclick="setAba('pesquisa')"/> </li>
		<li> Relatório de Tempo de Dedicação à Pesquisa por Área de Conhecimento
			<ul>
			<li> <h:commandLink action="#{relatoriosCenso.relatorioProjetosArea}" value="Relatório Sintético" onclick="setAba('pesquisa')"/> </li>
			<li> <h:commandLink action="#{relatoriosCenso.relatorioProjetosAreaAnalitico}" value="Relatório Analítico" onclick="setAba('pesquisa')"/> </li>
			</ul>
		</li>
		</ul>
	</li>
</ul>