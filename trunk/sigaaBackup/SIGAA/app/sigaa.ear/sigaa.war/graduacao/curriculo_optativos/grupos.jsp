<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Grupo de Componentes Optativos &gt; Componentes Optativos </h2>
	
	<h:form id="form">
		
		<c:if test="${not empty grupoOptativasMBean.grupos}">
			
			<div class="infoAltRem"><h:graphicImage value="/img/buscar.gif" style="overflow: visible;" />:
			Visualizar Grupo <h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />:
			Alterar Grupo <h:graphicImage value="/img/delete.gif" style="overflow: visible;" />:
			Remover Grupo
			</div>
		
			<table width="90%" class="listagem">
				<caption>Grupo de Componentes Optativos do Currículo Escolhido</caption>
				<thead>
					<tr>
					<td>Grupo</td><th style="text-align: right" width="90">CH Mínima</th><th style="text-align: right" width="90">CH Total</th><td></td><td></td><td></td>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="#{grupoOptativasMBean.grupos}" var="grupo" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
						<td>${grupo.descricao}</td>
						<td align="right">${grupo.chMinima}</td>
						<td align="right">${grupo.chTotal}</td>
						<td width="30">
						<h:commandLink action="#{ grupoOptativasMBean.visualizar }" id="visualizarGrupo">
						<f:param name="id" value="#{ grupo.id }"/>
						<h:graphicImage url="/img/buscar.gif" alt="Visualizar Grupo" title="Visualizar Grupo"/>
						</h:commandLink>
						</td>
						<td width="30">
						<h:commandLink action="#{ grupoOptativasMBean.atualizar }" id="update">
						<f:param name="id" value="#{ grupo.id }"/>
						<h:graphicImage url="/img/alterar.gif" alt="Alterar Grupo" title="Alterar Grupo"/>
						</h:commandLink>
						</td>
						<td width="30">
						<h:commandLink id="remocao" action="#{ grupoOptativasMBean.remover }" onclick="if (!confirm('Deseja realmente excluir esse registro?')) { return false; }">
						<f:param name="id" value="#{ grupo.id }"/>
						<h:graphicImage url="/img/delete.gif" alt="Remover Grupo" title="Remover Grupo"/>
						</h:commandLink>
						</td>
					</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>
		<c:if test="${ empty grupoOptativasMBean.grupos }">
			<p align="center"><strong>Nenhum grupo de optativas encontrado para esse currículo.</strong></p>
		</c:if>
			
		<p align="center">
		<br/>
		<h:commandButton value="Criar Novo Grupo" action="#{grupoOptativasMBean.novoGrupo}" id="novo"/>
		<h:commandButton value="<< Voltar" action="#{grupoOptativasMBean.telaSelecaoCurriculo}" id="voltar" />
		<h:commandButton value="Cancelar" action="#{grupoOptativasMBean.cancelar}" onclick="#{confirm}" id="cancelar"/>
		</p>
			
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
