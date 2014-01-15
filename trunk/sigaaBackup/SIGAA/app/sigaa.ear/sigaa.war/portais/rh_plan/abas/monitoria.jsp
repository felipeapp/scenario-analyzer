
<ul>
	<li> Monitores
		<ul>
			<li>  
				<h:commandLink action="#{comissaoMonitoria.relatorioMonitoresPorProjeto}" value="Relatório de Monitores por Projeto" onclick="setAba('monitoria')">
					<f:param value="true" name="menu"/>
				</h:commandLink>
			</li>
			<li>  
				<h:commandLink action="#{comissaoMonitoria.relatorioQuantitativoMonitores}" value="Relatório Quantitativo de Monitores por Projeto" onclick="setAba('monitoria')">
					<f:param value="true" name="menu"/>
				</h:commandLink> 
			</li>

			<li><a href="${ctx}/monitoria/Relatorios/dados_bancarios_monitores_form.jsf?aba=monitoria">Dados Bancários dos Monitores</a></li>
			<li>  
				<h:commandLink action="#{comissaoMonitoria.iniciarRelatorioMonitoresPorCentro}" value="Relatório de Monitores por Centro" onclick="setAba('monitoria')">
					<f:param value="true" name="menu"/>
				</h:commandLink>
			</li>
			<li>  
				<h:commandLink action="#{relatorioRendimentoComponente.iniciarRelatorioRendimentoComponente}" value="Relatório de Análise de Rendimentos por Departamento" onclick="setAba('monitoria')">
				</h:commandLink>
			</li>								
		</ul>
		</li>	
	<li> Projetos
		<ul>
			<li><h:commandLink action="#{projetoMonitoria.iniciarLocalizacaoRelatorioGeral}" value="Quadro Geral de Projetos" onclick="setAba('monitoria')"/></li>
			<li><a href="${ctx}/monitoria/Relatorios/informativo_sintetico.jsf?aba=monitoria">Informativo Sintético</a></li>
			<li>  
				<h:commandLink action="#{projetoMonitoria.iniciarRelatorioProjetosAtivosComMonitoresAtivosInativos}" value="Relatorio de projetos que estão ativos e com monitores ativos ou inativos" onclick="setAba('monitoria')"/>
			</li>
		</ul>
	</li>	
</ul>