<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:outputText value="#{ relatoriosProjetoPesquisa.create }" />

<h2> <ufrn:subSistema /> &gt; Relatório com estatísticas de cadastro de projetos de pesquisa </h2>

	<h:form>
		<table class="formulario" width="50%">
			<caption>Selecione o ano de cadastro dos projetos</caption>
			<tr>
				<th width="40%">Ano:</th>
				<td> <h:inputText value="#{relatoriosProjetoPesquisa.ano}" size="4" maxlength="4" id="ano"/> </td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Gerar relatório" action="#{relatoriosProjetoPesquisa.estatisticasCadastro}"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{relatoriosProjetoPesquisa.cancelar}"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
