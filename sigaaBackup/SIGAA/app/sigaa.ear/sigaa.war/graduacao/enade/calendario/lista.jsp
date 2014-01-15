<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:form>
	<h2><ufrn:subSistema /> &gt; Calendário de Aplicação do ENADE</h2>
	<div class="infoAltRem" style="width: 100%">
		<f:verbatim><img src="/sigaa/img/adicionar.gif" style="overflow: visible;" />:</f:verbatim> Cadastrar
		<f:verbatim><img src="/sigaa/img/alterar.gif" style="overflow: visible;" />:</f:verbatim> Alterar
 	</div>
		<a4j:keepAlive beanName="calendarioEnadeMBean"></a4j:keepAlive>
		<table class="formulario" width="75%">
			<caption>Informe os Parâmetros para Filtrar a Lista de Calendários do ENADE</caption>
			<tr>
				<th>Ano-Período:</th>
				<td>
					<h:selectOneMenu value="#{ calendarioEnadeMBean.ano}" id="filtroAno">
						<f:selectItem itemValue="0" itemLabel="-- TODOS --"/>
						<f:selectItems value="#{ calendarioEnadeMBean.anoCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th>Tipo:</th>
				<td>
					<h:selectOneMenu value="#{ calendarioEnadeMBean.tipoEnade}" id="filtrotipoEnade">
						<f:selectItem itemValue="" itemLabel="-- TODOS --"/>
						<f:selectItems value="#{ participacaoEnade.tipoEnadeCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Buscar" action="#{calendarioEnadeMBean.buscar}" id="buscar" />
						<h:commandButton value="Cancelar" action="#{calendarioEnadeMBean.cancelar}" id="cancelarOperacao" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		<c:if test="${ not empty calendarioEnadeMBean.all }">
			<br/>
			<table class="listagem">
				<caption>Lista de Calendários</caption>
				<thead>
					<tr>
						<th style="text-align: center;" width="6%">Ano</th>
						<th width="15%">Tipo</th>
						<th >Cursos</th>
						<th width="3%"></th>
					</tr>
				</thead>
				<c:forEach items="#{calendarioEnadeMBean.all}" var="item" varStatus="loop" >
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td style="text-align: center;">${item.ano}</td>
						<td>${item.tipoEnade}</td>
						<td>
							<c:forEach items="#{item.cursosGrauAcademico}" var="itemCurso" end="3" varStatus="loopCurso" >
								<c:if test="${ loopCurso.index > 0 }">
									<h:outputText value=", "/>
								</c:if>
								<h:outputText value="#{itemCurso.descricao }"/>
							</c:forEach>
							<h:outputText value=" (e mais outros #{fn:length(item.cursosGrauAcademico) - 3} cursos)." rendered="#{fn:length(item.cursosGrauAcademico) > 3}"/>
						</td>
						<td>
							<h:commandLink action="#{ calendarioEnadeMBean.atualizar }" id="atualizarLink">
								<h:graphicImage value="/img/alterar.gif" alt="Alterar" title="Alterar" rendered="#{ item.id > 0 }"/>
								<h:graphicImage value="/img/adicionar.gif" alt="Cadastrar" title="Cadastrar" rendered="#{ item.id == 0 }"/>
								<f:param name="ano" value="#{ item.ano }" />
								<f:param name="tipo" value="#{ item.tipoEnade.ordinal }" />
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
				<tfoot>
					<tr>
						<td colspan="4" style="text-align: center;">
						&nbsp;
						</td>
					</tr>
				</tfoot>
			</table>
			<br/>
			<div style="text-align: center;"> 
			    <h:commandButton image="/img/voltar.gif" actionListener="#{paginacao.previousPage}" rendered="#{paginacao.paginaAtual > 0 }"/>
			    <h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" onchange="submit()" immediate="true">
				<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
			    </h:selectOneMenu>
			    <h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}"  rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}"/>
			    <br/><br/>
			    <em>
		    	${fn:length(calendarioEnadeMBean.all)}
			     Registro(s) Encontrado(s)</em>
			</div>
		</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
