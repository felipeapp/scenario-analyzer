<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:outputText value="#{ relatoriosProjetoPesquisa.create }" />

<h2> <ufrn:subSistema /> &gt; Relatório Quantitativo de Projetos </h2>

	<h:form>
		<table class="formulario" width="45%">
			<caption>Selecione o mês e o ano de cadastro dos projetos</caption>
			<tr>
				<th width="40%" class="required">Mês:</th>
				<td>
					<h:selectOneMenu id="mes" value="#{relatoriosProjetoPesquisa.mesesAno}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{relatoriosProjetoPesquisa.meses}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required" width="40%">Ano:</th>
				<td> 
					<h:selectOneMenu id="ano" value="#{relatoriosProjetoPesquisa.ano}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{relatoriosProjetoPesquisa.anos}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Gerar relatório" action="#{relatoriosProjetoPesquisa.findQuantitativoProjeto}"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{relatoriosProjetoPesquisa.cancelar}"/>
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