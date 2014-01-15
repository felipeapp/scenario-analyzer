<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="etiquetasDiscentesBean"></a4j:keepAlive>
 	<h2><ufrn:subSistema /> > Geração de Etiquetas de Identificação de Discentes - Confirmação</h2>

	<h:form id="confirmar">
		<table align="center" class="formulario" width="95%">
			<caption>Critérios de busca dos discentes</caption>

			<tr><td> </td></tr>
			<tr>
				<td>
				<p style="margin:3px 0;text-indent:3em; padding: 1em;"/>Existe(m) aluno(s) que não possuem cadastro de forma de ingresso no sistema.
				<p style="margin:3px 0;text-indent:3em; padding: 1em;"/>A etiqueta vai ser gerada, mas a forma de ingresso não será impressa e, em seu lugar, o texto "SEM FORMA DE INGRESSO CADASTRADA" será mostrado.
				</td>
			</tr>
			<tr><td> </td></tr>
			<tfoot>
			<tr>
				<td colspan="3" align="center">
					<h:commandButton value="Gerar Relatório" id="gerarRelatorio" action="#{etiquetasDiscentesBean.gerar}" />
					<h:commandButton value="<< Voltar" id="voltar" action="#{etiquetasDiscentesBean.iniciar}" />
					<h:commandButton value="Cancelar" id="cancelar" onclick="#{confirm}" action="#{etiquetasDiscentesBean.cancelar}" />
				</td>
			</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
