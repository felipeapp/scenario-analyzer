<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipos de Cursos e Eventos de Extensão</h2>

	<h:outputText value="#{tipoCursoEventoExtensao.create}" />

	<center><h:messages />
	<div class="infoAltRem"><h:graphicImage value="/img/alterar.gif"
		style="overflow: visible;" />: Alterar Tipo de Curso e Evento <h:graphicImage
		value="/img/delete.gif" style="overflow: visible;" />: Remover Tipo
	de Curso e Evento<br />
	</div>
	</center>

	<h:form>
		<table class=listagem>
			<caption class="listagem">Lista de Tipo de Evento de
			Extensão</caption>
			<thead>
				<tr>
					<th>Descrição</th>
					<th>Escopo</th>
					<th>CH Mínima</th>
					<th></th>
					<th></th>
				</tr>
			</thead>

			<c:set value="#{tipoCursoEventoExtensao.allAtivos}" var="cursosEventos" />
			
			<c:forEach items="#{cursosEventos}" var="item"
				varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${item.descricao}</td>
					<td>${item.escopo == 'C' ? 'CURSO' : (item.escopo == 'E' ?
					'EVENTO' : 'NÃO DEFINIDO')}</td>
					<td>${item.chMinima} hora(s)</td>
					<td width="2%"><h:commandLink title="Alterar"
						action="#{tipoCursoEventoExtensao.atualizar}" style="border: 0;">
						<f:param name="id" value="#{item.id}" />
						<h:graphicImage url="/img/alterar.gif" />
					</h:commandLink></td>
					<td width="2%"><h:commandLink title="Remover"
						action="#{tipoCursoEventoExtensao.preRemover}" style="border: 0;">
						<f:param name="id" value="#{item.id}" />
						<h:graphicImage url="/img/delete.gif" />
					</h:commandLink></td>
				</tr>
			</c:forEach>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>