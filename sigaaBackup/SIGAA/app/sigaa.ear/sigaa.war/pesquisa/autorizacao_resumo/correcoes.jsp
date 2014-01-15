<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Corre��es do Resumo</h2>
	<h:form>
		<table class="formulario" width="80%">
			<caption>Corre��es do Resumo</caption>
			<tbody>
				<tr>
					<th width="10%"><b>Corre��o:</b></th>
					<td><h:inputTextarea value="#{autorizacaoResumo.obj.correcao}" style="width: 95%" rows="10" id="correcaoResumo"/></td>
				</tr>
			</tbody>
			<tfoot>
				<tr align="center">
					<td colspan="2">
						<h:commandButton value="<< Voltar" action="#{autorizacaoResumo.listarResumos}" id="voltar" />
						<h:commandButton value="Devolver para Corre��o" action="#{autorizacaoResumo.retornarResumoRecusado}" id="correcao" />
					</td>
				</tr>
			</tfoot>
		</table>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>