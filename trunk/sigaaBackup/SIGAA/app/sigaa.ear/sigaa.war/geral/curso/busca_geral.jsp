<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<jsp:useBean id="sigaaSubSistemas" class="br.ufrn.arq.seguranca.SigaaSubsistemas" scope="page"/>
<c:set var="_portalDocente"   value="<%=sigaaSubSistemas.PORTAL_DOCENTE%>"/>
<c:set var="_portalDiscente"  value="<%=sigaaSubSistemas.PORTAL_DISCENTE%>"/>
<c:set var="_portalTutor"     value="<%=sigaaSubSistemas.PORTAL_TUTOR%>"/>
<c:set var="_portalCoordPolo" value="<%=sigaaSubSistemas.PORTAL_COORDENADOR_POLO%>"/>

<ufrn:checkSubSistema subsistema="${_portalDiscente.id}">
	<c:if test="${acesso.discente}">
		<%@include file="/portais/discente/menu_discente.jsp" %>
	</c:if>
</ufrn:checkSubSistema>
<ufrn:checkSubSistema subsistema="${_portalDocente.id}">
	<c:if test="${acesso.docente}">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</c:if>
</ufrn:checkSubSistema>
<ufrn:checkSubSistema subsistema="${_portalTutor.id}">
	<c:if test="${acesso.tutorEad}">
		<%@include file="/portais/tutor/menu_tutor.jsp" %>
	</c:if>
</ufrn:checkSubSistema>
<ufrn:checkSubSistema subsistema="${_portalCoordPolo.id}">
	<c:if test="${acesso.coordenadorPolo}">
		<%@include file="/portais/cpolo/menu_cpolo.jsp" %>
	</c:if>
</ufrn:checkSubSistema>

	<h2><ufrn:subSistema/> > Consulta Geral de Cursos</h2>
	<h:outputText value="#{curso.create}" />
	<h:form id="busca">
		<table class="formulario" width="70%">
			<caption>Informe os critérios de consulta</caption>
			<tbody>
				<tr>
					<td width="3%"></td>
					<td width="20%"><label>Nível:</label></td>
					<td>
						<h:selectOneMenu id="niveis" value="#{curso.nivel}">
							<f:selectItems value="#{componenteCurricular.niveis}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td><input type="radio" id="checkNome" name="paramBusca" value="nome" class="noborder" ${(curso.tipoBusca == "nome")?'checked':'' }  /></td>
					<td><label for="checkNome">Nome do curso:</label></td>
					<td><h:inputText value="#{curso.obj.nome}" id="param1" onkeydown="marcaCheckBox('checkNome')" style="width: 95%;"/></td>
				</tr>
				<tr>
					<td><input type="radio" name="paramBusca" value="todos" id="checkTodos" class="noborder" ${(curso.tipoBusca == "todos")?'checked':'' } /></td>
					<td><label for="checkTodos">Todos</label></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{curso.buscar}" />
						<h:commandButton value="Cancelar" action="#{curso.cancelar}" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<br>

	<c:if test="${not empty curso.resultadosBusca}">
		<br>
		
		<%--
		<center>
		<div class="infoAltRem">
	    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Detalhar Curso<br/>
		</div>
		</center>
		--%>
		
		<table class="listagem">
			<caption class="listagem">Lista de Cursos Encontrados</caption>
			<thead>
				<tr>
					<td>Curso</td>
					<td>Nível</td>
					<td>Coordenador</td>
					<td width="3%"></td>
				</tr>
			</thead>
			<c:forEach items="${curso.resultadosBusca}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td>${item.nomeCompleto}</td>
					<td style="font-variant: small-caps;">${item.nivelDescricao}</td>
					<td>
						<c:forEach var="coordenador" items="${item.coordenacoesCursos}">
							<c:if test="${coordenador.ativo && coordenador.coordenador && coordenador.virgente}">
								${coordenador.servidor.pessoa.nome} <br />
							</c:if>
						</c:forEach>
					</td>
					<td>
					<%--
					<h:form>
						<input type="hidden" value="${item.id}" name="id"/>
						<h:commandButton image="/img/view.gif" value="Detalhes" action="#{curso.detalhar}" style="border: 0;"/>
					</h:form>
					--%>
					</td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
</f:view>
<script type="text/javascript">
<!--
$('busca:param1').focus()
//-->
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
