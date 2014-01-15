<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:form>
	<h2><ufrn:subSistema /> &gt; Ênfase</h2>
	<div class="infoAltRem" style="width: 100%">
		<img src="/sigaa/img/alterar.gif" style="overflow: visible;" />: Alterar
	 	<img src="/sigaa/img/cronograma/remover.gif" style="overflow: visible;" />: Inativar
 	</div>
		<a4j:keepAlive beanName="enfase"></a4j:keepAlive>
		<c:if test="${ not empty enfase.all }">
			<table class="listagem">
				<caption>Lista de Ênfases</caption>
				<thead>
					<tr>
						<th>Curso</th>
						<th>Nome</th>
						<th style="text-align: center;">Ativo</th>
						<th width="3%"></th>
						<th width="3%"></th>
					</tr>
				</thead>
				<c:forEach items="#{enfase.all}" var="item" varStatus="loop" >
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${item.curso.nome}</td>
						<td>${item.nome}</td>
						<td style="text-align: center;"><ufrn:format type="SimNao" valor="${item.ativo}" /></td>
						<td align="center">
							<h:commandLink action="#{ enfase.atualizar }" id="atualizarLink">
								<f:verbatim>
									<img src="/sigaa/img/alterar.gif" alt="Alterar" title="Alterar" />
								</f:verbatim>
								<f:param name="id" value="#{ item.id }" />
							</h:commandLink>
						</td>
						<td align="center">
							<c:if test="${item.ativo}">
								<h:commandLink action="#{ enfase.inativar }" onclick="#{confirmDelete}" id="deleteLink">
									<f:verbatim>
										<img src="/sigaa/img/cronograma/remover.gif" alt="Inativar" title="Inativar" />
									</f:verbatim>
									<f:param name="id" value="#{ item.id }" />
								</h:commandLink>
							</c:if>
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
