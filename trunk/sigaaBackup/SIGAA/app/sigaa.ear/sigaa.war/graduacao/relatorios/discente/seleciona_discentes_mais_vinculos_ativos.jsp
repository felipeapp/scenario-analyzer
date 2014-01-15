<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h2> <ufrn:subSistema/> > Relatório de Alunos com mais de um Vinculo Ativo </h2>

<h:form id="form">
<table align="center" class="formulario" width="40%">
	<caption class="listagem">Dados do Relatório</caption>
	
	<tr>
		<th width="50%"  class="required">Ano Ingresso: </th>
		<td><h:inputText id="ano" value="#{relatorioDiscente.ano}" onkeyup="return formatarInteiro(this);" size="4" maxlength="4"/></td>
	</tr>
	
	<tfoot>
		<tr>
			<td colspan="2" align="center">
				<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
				action="#{relatorioDiscente.gerarRelatorioDiscentesMaisVinculosAtivos}"/> <h:commandButton
				value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" onclick="#{confirm}" />
			</td>
		</tr>
	</tfoot>

</table>
<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>