<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h:messages showDetail="true"></h:messages>
	<h2 class="title"><ufrn:subSistema /> > Trabalho Final de Stricto</h2>

	<h:form id="form">
		<c:set value="#{trabalhoFinalStricto.obj.discente}" var="discente"></c:set>
		<%@include file="/graduacao/info_discente.jsp"%>
		<table class="formulario" width="85%">
			<caption class="formulario">Dados do Trabalho Final</caption>
			<tr>
				<th>Título<span class="required">&nbsp;</span></th>
				<td>
				<h:inputText value="#{trabalhoFinalStricto.obj.titulo}" size="60" id="trabFinalStrictoCurso"></h:inputText>
				</td>
			</tr>
			<tr>
				<th>Páginas<span class="required">&nbsp;</span></th>
				<td>
				<h:inputText value="#{trabalhoFinalStricto.obj.paginas}" size="4" id="numPaginas"></h:inputText>
				</td>
			</tr>
			<tr>
				<th class="required">Área:</th>
				<td><h:selectOneMenu value="#{trabalhoFinalStricto.obj.area.id}"
					id="area" valueChangeListener="#{trabalhoFinalStricto.carregaSubAreas}" style="width: 300px"
					onchange="submit()" immediate="true">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{area.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Sub-Área:</th>

				<td><h:selectOneMenu value="#{trabalhoFinalStricto.obj.subArea.id}" style="width: 300px" id="subArea">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{trabalhoFinalStricto.subAreas}"/>
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th valign="top">Resumo<span class="required">&nbsp;</span></th>
				<td>
				<h:inputTextarea rows="5" cols="65" value="#{trabalhoFinalStricto.obj.resumo}" id="textoResumo"/>
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton id="confir" value="#{trabalhoFinalStricto.confirmButton}"
						action="#{trabalhoFinalStricto.confirmar}" />
					<h:commandButton value="Cancelar" id="cancelamento"
						action="#{trabalhoFinalStricto.cancelar}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

	<c:if test="${not empty trabalhoFinalStricto.trabalhosDiscente}">

		<table class="subFormulario" style="width: 100%" >
			<caption>Outros Trabalhos Finais desse Discente</caption>
			<tbody>
			<c:forEach items="${trabalhoFinalStricto.trabalhosDiscente}" var="trabalho" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
				<td>${trabalho.descricao}</td>
			</tr>
			</c:forEach>
			</tbody>
		</table>

	</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
