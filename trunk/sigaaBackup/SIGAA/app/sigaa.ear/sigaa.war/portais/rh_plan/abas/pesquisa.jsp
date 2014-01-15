
<ul>
	<li> Projetos de Pesquisa
   		<ul>
        <li> <a href="${ctx}/pesquisa/projetoPesquisa/buscarProjetos.do?dispatch=consulta&popular=true&aba=pesquisa">Consultar</a> </li>
		<li> <a href="${ctx}/pesquisa/projetoPesquisa/buscarProjetos.do?dispatch=financiamentos&popular=true&aba=pesquisa"> Projetos Financiados </a> </li>
		<li>
			<a href="${ctx}/pesquisa/relatoriosPesquisa.do?dispatch=popularFinanciamentosSintetico&aba=pesquisa">
				Relat�rio Sint�tico de Financiamentos
			</a>
		</li>
		<li>
			<a href="${ctx}/pesquisa/editalSintetico.do?dispatch=popularEditais&aba=pesquisa">
				Relat�rio de Submiss�o de Projetos
			</a>
		</li>

		<li>
			<a href="${ctx}/pesquisa/distribuirProjetoPesquisa.do?dispatch=consultaResultadoDistribuicao&aba=pesquisa">
				Projetos com avalia��es pendentes
			</a>
		</li>
		<li> <a href="${ctx}/pesquisa/avaliarRelatorioProjeto.do?dispatch=listRelatoriosFinais&popular=true&aba=pesquisa"> Relat�rios Finais </a></li>
		<li> <h:commandLink value="Projetos de Infra-Estrutura" action="#{ projetoInfraPesq.listar }" onclick="setAba('pesquisa')"/> </li>
		</ul>
	</li>
	<li> Inicia��o Cient�fica
		<ul>
		<li>
			<a href="${ctx}/pesquisa/editalSintetico.do?dispatch=popularCotas&aba=pesquisa">
				Relat�rio de Cotas Solicitadas
			</a>
		</li>
		<li>
			<a href="${ctx}/pesquisa/relatorios/acompanhamentoCotas.do?dispatch=iniciar&aba=pesquisa">
				Acompanhamento de Distribui��o de Cotas de Bolsas
			</a>
		</li>
		<li><a href="${ctx }/pesquisa/relatorios/quantitativoSolicitacoesCotas.do?dispatch=iniciar&aba=pesquisa">Relat�rio Quantitativo de Solicita��es de Bolsas</a></li>
		<li> <h:commandLink action="#{relatorioRenovacaoBolsaMBean.selecionaQuantitativoBolsaPibic}" value="Relat�rio Quantitativo de Renova��o de Bolsas" onclick="setAba('pesquisa');"/> </li>
		<li> <a href="${ctx}/pesquisa/relatorios/form_bolsas_pesquisa.jsf?aba=pesquisa"> Relat�rio Quantitativo de Bolsas de Pesquisa Ativas </a> </li>
		<li> <a href="${ctx}/pesquisa/relatorios/form_resumo_cotas.jsf?aba=pesquisa"> Relat�rio Quantitativo de Cotas de Bolsas</a> </li>
		<li> <h:commandLink action="#{relatoriosPesquisaMBean.gerarRelatorioQuantBolsasCentroDepartamento}" value="Relat�rio Quantitativo de Bolsas por Centro/Departamento" onclick="setAba('pesquisa');"/> </li>
		<li> <h:commandLink action="#{relatoriosCNPQMBean.iniciarPesquisadoresCNPQ}" value="Relat�rio Pesquisadores Produtividade CNPQ" onclick="setAba('pesquisa');"/> </li>
		</ul>
	</li>
	<li> Docentes
		<ul>
		<li> <a href="${ctx}/pesquisa/relatorioParticipacaoDocentes.do?dispatch=iniciar&aba=pesquisa"> Relat�rio de Participa��o em Projetos de Pesquisa </a></li>
		</ul>
	</li>
	<li> Censo
		<ul>
		<li> <h:commandLink action="#{relatoriosCenso.relatorioProjetosDocente}" value="Relat�rio de Tempo de Dedica��o � Pesquisa por Grau de Forma��o" onclick="setAba('pesquisa')"/> </li>
		<li> Relat�rio de Tempo de Dedica��o � Pesquisa por �rea de Conhecimento
			<ul>
			<li> <h:commandLink action="#{relatoriosCenso.relatorioProjetosArea}" value="Relat�rio Sint�tico" onclick="setAba('pesquisa')"/> </li>
			<li> <h:commandLink action="#{relatoriosCenso.relatorioProjetosAreaAnalitico}" value="Relat�rio Anal�tico" onclick="setAba('pesquisa')"/> </li>
			</ul>
		</li>
		</ul>
	</li>
</ul>