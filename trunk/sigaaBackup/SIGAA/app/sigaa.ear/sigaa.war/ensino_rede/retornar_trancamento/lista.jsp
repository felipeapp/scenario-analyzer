<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName = "selecionaDiscenteMBean" />
<h2> <ufrn:subSistema /> &gt; Movimentações do discente</h2>

	<c:set var="discente_" value="#{retornarTrancamentoProgramaRedeMBean.obj.discente}"/>
	<c:set var="_largura" value="70%" />
	<%@include file="/ensino_rede/discente/resumo_dados_discente.jsp"%>
	
	<br/>
	<br/>

	<c:if test="${not empty retornarTrancamentoProgramaRedeMBean.resultadosBusca}">	
		<h:form>
			
			<div class="infoAltRem" style="width:70%">
				<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar  Movimentação<br />
			</div>
			
			<table class="listagem" style="width:70%">
			  <caption>Movimentações Encontradas (${fn:length(retornarTrancamentoProgramaRedeMBean.resultadosBusca)})</caption>
				<thead>
					<tr>
						<th>Tipo</th>
						<th>Ano-Período</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:set var="cies" value="0"/>
					<c:forEach var="linha" items="#{retornarTrancamentoProgramaRedeMBean.resultadosBusca}" varStatus="status">
						<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td> 
								${linha.tipo.descricao }
							</td>
							<td> 
								${linha.anoReferencia } - ${linha.periodoReferencia }
							</td>
							<td align="right" width="2%">
								<h:commandLink action="#{retornarTrancamentoProgramaRedeMBean.selecionarMovimentacao}" id="selecionarMovimentacao" title="Selecionar Movimentação">
									<f:param name="idMovimentacao" value="#{linha.id}" />
									<h:graphicImage url="/img/seta.gif" alt="Selecionar Movimentação" />
								</h:commandLink>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</h:form>
	</c:if>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>