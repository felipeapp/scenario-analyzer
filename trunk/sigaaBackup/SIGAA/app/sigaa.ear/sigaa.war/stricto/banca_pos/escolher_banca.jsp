<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp"%>
	<a4j:keepAlive beanName="certificadoBancaPos"></a4j:keepAlive>
	
	<h2 class="title"><ufrn:subSistema /> > Bancas do discente</h2>

	<c:set value="#{declaracaoDefesaMBean.discente}" var="discente" />
	<%@ include file="/graduacao/info_discente.jsp"%>
	
	<table class="listagem">
		<caption>${fn:length(declaracaoDefesaMBean.bancasDoDiscente)}
		banca(s) encontrada(s)</caption>

		<tbody>
			<h:form id="lista">
				<c:forEach items="#{declaracaoDefesaMBean.bancasDoDiscente}" var="b"
					varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
						<td width="10%">
							${b.tipoDescricao}
						</td>

						<td>
							${b.dadosDefesa.descricao}
						</td>
						
						<td align="right" width="2%">
							<h:commandLink action="#{declaracaoDefesaMBean.selecionaBanca}" title="Selecionar Banca" id="botaoSelecionarBanca">
								<h:graphicImage url="/img/seta.gif" />
								<f:setPropertyActionListener target="#{declaracaoDefesaMBean.idBanca}" value="#{b.id}" />
							</h:commandLink>
						</td>

					</tr>
				</c:forEach>
			</h:form>
		</tbody>
	</table>

	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
