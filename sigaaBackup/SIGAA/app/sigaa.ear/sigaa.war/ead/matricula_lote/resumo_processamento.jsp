<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema /> > Escolha de Componentes</h2>

	<div id="ajuda" class="descricaoOperacao">
		<p>Caro Usuário,</p>
		<p>O processamento em lote das matrículas foi concluído com as seguintes mensagens:</p>	
	</div>
	<h:form>
	<br/>
	<c:if test="${not empty loteMatriculas.resultadoProcessamentoLote }">
		<table class="formulario">
			<caption>Resumo do Processamento em Lote</caption>
			<tbody>
				<c:forEach items="#{ loteMatriculas.resultadoProcessamentoLote }" var="discente" >
					<tr>
						<td class="subFormulario">${ discente.key }</td>
					</tr>
					<c:if test="${not empty discente.value }">
						<c:forEach items="#{ discente.value }" var="mensagem">
							<tr>
								<td>${ mensagem }</td>
							</tr>
						</c:forEach>
					</c:if>
					<c:if test="${empty discente.value }">
						Discente matriculado com sucesso!
					</c:if>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td>
						<h:commandButton value="Concluído" action="#{ loteMatriculas.cancelar }" immediate="true"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</c:if>
	<c:if test="${empty loteMatriculas.resultadoProcessamentoLote }">
		<div style="text-align:center">
			Não há mensagens resultantes do processamento em lote.<br/><br/>
			<h:commandLink value="<< Voltar ao Menu Principal" action="#{ loteMatriculas.cancelar }" immediate="true"/>
		</div>
	</c:if>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>