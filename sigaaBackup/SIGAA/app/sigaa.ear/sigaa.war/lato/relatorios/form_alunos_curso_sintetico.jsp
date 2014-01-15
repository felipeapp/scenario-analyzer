<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> > Relatório de Alunos por Curso </h2>
<f:view>

  <h:form id="form">
	<table class="formulario" style="width: 30%" align="center">
		<caption> Informe os critérios de busca </caption>
		<tbody>
		<tr>
			<th>Ano Inicial:</th>
			<td>
				<h:inputText value="#{relatoriosLato.anoInicial}" id="ano" size="6" maxlength="4"  onkeyup="return formatarInteiro(this);"/>
			</td>
		</tr>
	
		<tr>
			<th>Ano Final:</th>
			<td>
				<h:inputText value="#{relatoriosLato.ano}" id="anoInicial" size="6" maxlength="4" onkeyup="return formatarInteiro(this);"/>
			</td>
		</tr>
	
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatoriosLato.alunosCursoSintetico}" /> 
					<h:commandButton id="cancelar" value="Cancelar" action="#{relatoriosLato.cancelar}" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
	</table>
  </h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>