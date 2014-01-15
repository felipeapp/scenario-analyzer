<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:outputText value="#{ relatoriosProjetoPesquisa.create }" />

<h2> <ufrn:subSistema /> &gt; Relatório Quantitativo de Renovação de Bolsas </h2>

	<h:form>
		<table class="formulario" width="45%">
			<caption>Selecione o intervalo das Cotas de Bolsa</caption>
			<tr>
				<th class="required" width="40%">Período de Cotas Inicial:</th>
				<td> 
					<h:selectOneMenu id="cotaAnterior" value="#{relatorioRenovacaoBolsaMBean.cotaBolsaAnterior}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{relatorioRenovacaoBolsaMBean.allCotas}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th width="40%" class="required">Período de Cotas Final:</th>
				<td>
					<h:selectOneMenu id="cotaAtual" value="#{relatorioRenovacaoBolsaMBean.cotaBolsaAtual}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{relatorioRenovacaoBolsaMBean.allCotas}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Gerar Relatório" action="#{relatorioRenovacaoBolsaMBean.findQuantitativoBolsaPibic}"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{relatorioRenovacaoBolsaMBean.cancelar}"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>