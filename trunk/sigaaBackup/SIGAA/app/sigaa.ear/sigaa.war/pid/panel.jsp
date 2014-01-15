<style>

	.listagemPopup {
		width: 100%;
		margin: 0 auto;
		text-align: left;
		font-size: 12px;
		padding-left: 10px;
	}
	
	.colunas td {
		font-weight: bold;
		border-bottom: 1px solid;
	}
	
</style>

<%-- GRADUAÇÃO --%>
<rich:modalPanel id="panelGraduacao" autosized="true" minWidth="900">
	<f:facet name="header">
		<h:panelGroup>
			<h:outputText value="Orientações de Graduação"></h:outputText>
		</h:panelGroup>
	</f:facet>
	<f:facet name="controls">
        <h:panelGroup>
                <a4j:commandLink onclick="Richfaces.hideModalPanel('panelGraduacao');">
                    <h:graphicImage value="/img/close.png" styleClass="hidelink" id="hidelink1"/>
            </a4j:commandLink>
        </h:panelGroup>
    </f:facet>

	<div style="height: 180px; overflow: auto;">
		<table class="listagemPopup">
			<tr class="colunas">
				<td>Matrícula</td>
				<td>Nome</td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
			<c:forEach var="item" items="#{cargaHorariaPIDMBean.listaCHOrientacaoGraduacaoSemAtividades}">
				<tr>
					<td>${item.discente.matricula}</td>
					<td>${item.discente.nome}</td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
			</c:forEach>
		</table>
	</div>
	
	<br/>
	<c:if test="${empty cargaHorariaPIDMBean.listaCHOrientacaoGraduacao}">
		<center>Nenhum orientação encontrada.</center>
	</c:if>
	
</rich:modalPanel>


<%-- PÓS-GRADUAÇÃO --%>
<rich:modalPanel id="panelPosGraduacao" autosized="true" minWidth="900">
	<f:facet name="header">
		<h:panelGroup>
			<h:outputText value="Orientações de Pós-Graduação"></h:outputText>
		</h:panelGroup>
	</f:facet>
	<f:facet name="controls">
        <h:panelGroup>
                <a4j:commandLink onclick="Richfaces.hideModalPanel('panelPosGraduacao');">
                    <h:graphicImage value="/img/close.png" styleClass="hidelink" id="hidelink2"/>
            </a4j:commandLink>
        </h:panelGroup>
    </f:facet>

	<div style="height: 180px; overflow: auto;">
		<table class="listagemPopup">
			<tr class="colunas">
				<td>Matrícula</td>
				<td>Nome</td>
			</tr>
			<c:forEach var="item"
				items="#{cargaHorariaPIDMBean.listaCHOrientacaoPosGraduacao}">
				<tr>
					<td>${item.discente.matricula}</td>
					<td>${item.discente.nome}</td>
				</tr>
			</c:forEach>
		</table>
		
		<br/>
		<c:if test="${empty cargaHorariaPIDMBean.listaCHOrientacaoPosGraduacao}">
			<center>Nenhum orientação encontrada.</center>
		</c:if>
		
	</div>
</rich:modalPanel>

<%-- Ensino --%>
<rich:modalPanel id="panellinkEnsinoProducaoAcademica" autosized="true" minWidth="950">
	<f:facet name="header">
		<h:panelGroup>
			<h:outputText value="Projetos de Ensino"></h:outputText>
		</h:panelGroup>
	</f:facet>
	<f:facet name="controls">
        <h:panelGroup>
                <a4j:commandLink onclick="Richfaces.hideModalPanel('panellinkEnsinoProducaoAcademica');">
                    <h:graphicImage value="/img/close.png" styleClass="hidelink" id="hidelink5"/>
            </a4j:commandLink>
        </h:panelGroup>
    </f:facet>

	<div style="height: 180px; overflow: auto;">
		<table class="listagemPopup">
			<tr class="colunas">
				<td>Ano</td>
				<td>Situação</td>
				<td>Título</td>
				<td>Início</td>
				<td>Fim</td>
			</tr>
			<c:forEach var="item"
				items="#{cargaHorariaPIDMBean.listaCargaHorariaProjetoEnsino}">
				<tr>
					<td>${item.membroProjeto.projeto.ano}</td>
					<td nowrap="nowrap">${item.membroProjeto.projeto.situacaoProjeto.descricao}</td>
					<td>${item.membroProjeto.projeto.titulo}</td>
					<td><fmt:formatDate pattern="dd/MM/yyyy" value="${ item.membroProjeto.dataInicio }"/></td>
					<td><fmt:formatDate pattern="dd/MM/yyyy" value="${ item.membroProjeto.dataFim }"/></td>
				</tr>
			</c:forEach>
		</table>
		
		<br/>
		<c:if test="${empty cargaHorariaPIDMBean.listaCargaHorariaProjetoEnsino}">
			<center>Nenhum projeto encontrado.</center>
		</c:if>
		
	</div>
	
</rich:modalPanel>

<%-- Pesquisa --%>
<rich:modalPanel id="panellinkPesquisaProducaoAcademica" autosized="true" minWidth="950">
	<f:facet name="header">
		<h:panelGroup>
			<h:outputText value="Pesquisa e Produção Acadêmica"></h:outputText>
		</h:panelGroup>
	</f:facet>
	<f:facet name="controls">
        <h:panelGroup>
                <a4j:commandLink onclick="Richfaces.hideModalPanel('panellinkPesquisaProducaoAcademica');">
                    <h:graphicImage value="/img/close.png" styleClass="hidelink" id="hidelink3"/>
            </a4j:commandLink>
        </h:panelGroup>
    </f:facet>

	<div style="height: 180px; overflow: auto;">
		<table class="listagemPopup">
			<tr class="colunas">
				<td>Ano</td>
				<td>Situação</td>
				<td>Título</td>
				<td>Início</td>
				<td>Fim</td>
			</tr>
			<c:forEach var="item"
				items="#{cargaHorariaPIDMBean.listaCargaHorariaProjetoPesquisa}">
				<tr>
					<td>${item.membroProjeto.projeto.ano}</td>
					<td nowrap="nowrap">${item.membroProjeto.projeto.situacaoProjeto.descricao}</td>
					<td>${item.membroProjeto.projeto.titulo}</td>
					<td><fmt:formatDate pattern="dd/MM/yyyy" value="${ item.membroProjeto.dataInicio }"/></td>
					<td><fmt:formatDate pattern="dd/MM/yyyy" value="${ item.membroProjeto.dataFim }"/></td>
				</tr>
			</c:forEach>
		</table>
		
		<br/>
		<c:if test="${empty cargaHorariaPIDMBean.listaCargaHorariaProjetoPesquisa}">
			<center>Nenhum projeto encontrado.</center>
		</c:if>
		
	</div>
	
</rich:modalPanel>

<%-- Extensão --%>
<rich:modalPanel id="panelExtensao" autosized="true" minWidth="950">
	<f:facet name="header">
		<h:panelGroup>
			<h:outputText value="Extensão e Outras Atividades"></h:outputText>
		</h:panelGroup>
	</f:facet>
	<f:facet name="controls">
        <h:panelGroup>
                <a4j:commandLink onclick="Richfaces.hideModalPanel('panelExtensao');">
                    <h:graphicImage value="/img/close.png" styleClass="hidelink" id="hidelink4"/>
            </a4j:commandLink>
        </h:panelGroup>
    </f:facet>

	<div style="height: 180px; overflow: auto;">
		<table class="listagemPopup">
			<tr class="colunas">
				<td>Ano</td>
				<td>Situação</td>
				<td style="padding-left: 50px;">Título</td>
				<td>Início</td>
				<td>Fim</td>
			</tr>
			
			<c:forEach var="item" items="#{cargaHorariaPIDMBean.listaCargaHorariaProjetoExtensao}">
				<tr>
					<td>${item.membroProjeto.projeto.ano}</td>
					<td nowrap="nowrap">${item.membroProjeto.projeto.situacaoProjeto.descricao}</td>
					<td style="padding-left: 50px;">${item.membroProjeto.projeto.titulo}</td>
					<td><fmt:formatDate pattern="dd/MM/yyyy" value="${ item.membroProjeto.dataInicio }"/></td>
					<td><fmt:formatDate pattern="dd/MM/yyyy" value="${ item.membroProjeto.dataFim }"/></td>
				</tr>
			</c:forEach>
		</table>
		
		<br/>
		<c:if test="${empty cargaHorariaPIDMBean.listaCargaHorariaProjetoExtensao}">
			<center>Nenhum projeto encontrado.</center>
		</c:if>
		
	</div>
</rich:modalPanel>
