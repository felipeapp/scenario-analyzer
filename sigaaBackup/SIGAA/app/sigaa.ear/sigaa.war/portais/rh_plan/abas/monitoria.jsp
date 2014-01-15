
<ul>
	<li> Monitores
		<ul>
			<li>  
				<h:commandLink action="#{comissaoMonitoria.relatorioMonitoresPorProjeto}" value="Relat�rio de Monitores por Projeto" onclick="setAba('monitoria')">
					<f:param value="true" name="menu"/>
				</h:commandLink>
			</li>
			<li>  
				<h:commandLink action="#{comissaoMonitoria.relatorioQuantitativoMonitores}" value="Relat�rio Quantitativo de Monitores por Projeto" onclick="setAba('monitoria')">
					<f:param value="true" name="menu"/>
				</h:commandLink> 
			</li>

			<li><a href="${ctx}/monitoria/Relatorios/dados_bancarios_monitores_form.jsf?aba=monitoria">Dados Banc�rios dos Monitores</a></li>
			<li>  
				<h:commandLink action="#{comissaoMonitoria.iniciarRelatorioMonitoresPorCentro}" value="Relat�rio de Monitores por Centro" onclick="setAba('monitoria')">
					<f:param value="true" name="menu"/>
				</h:commandLink>
			</li>
			<li>  
				<h:commandLink action="#{relatorioRendimentoComponente.iniciarRelatorioRendimentoComponente}" value="Relat�rio de An�lise de Rendimentos por Departamento" onclick="setAba('monitoria')">
				</h:commandLink>
			</li>								
		</ul>
		</li>	
	<li> Projetos
		<ul>
			<li><h:commandLink action="#{projetoMonitoria.iniciarLocalizacaoRelatorioGeral}" value="Quadro Geral de Projetos" onclick="setAba('monitoria')"/></li>
			<li><a href="${ctx}/monitoria/Relatorios/informativo_sintetico.jsf?aba=monitoria">Informativo Sint�tico</a></li>
			<li>  
				<h:commandLink action="#{projetoMonitoria.iniciarRelatorioProjetosAtivosComMonitoresAtivosInativos}" value="Relatorio de projetos que est�o ativos e com monitores ativos ou inativos" onclick="setAba('monitoria')"/>
			</li>
		</ul>
	</li>	
</ul>