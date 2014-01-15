<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%-- @page import="br.ufrn.sigaa.arq.seguranca.SigaaPapeis" --%>
<%@page import=" br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta" %>
<h2>
	<ufrn:subSistema/> &gt;
	<fmt:message key="titulo.listar">
		<fmt:param value="Propostas"/>
	</fmt:message>
</h2>

<html:form action="/ensino/latosensu/criarCurso?dispatch=list" method="post" focus="obj.nome" styleId="form">

		<table class="formulario" align="center" width="50%">
			<caption class="listagem">Busca de Proposta </caption>

			<tr>
				<td> <input type="radio" name="tipoBusca" value="1" class="noborder" id="curso"/>
					 <label for="curso">Curso:</label>
				</td>
				<td>
					<html:text property="obj.nome" size="45" onkeyup="CAPS(this)" 
						onfocus="javascript:forms[0].tipoBusca[0].checked = true;"></html:text>
				</td>
			</tr>
			<tr>
				<td> <input type="radio" name="tipoBusca" value="todos" class="noborder" id="todos"/> 
					 <label for="todos">Todas</label>
				</td>
				<td> </td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						   <html:submit value="Buscar" />
						   <html:hidden property="buscar" value="true"/>
					</td>
				</tr>
			</tfoot>
		</table>
</html:form>
<br>

<c:if test="${ param.renovar == null }">
	<c:if test="${not empty lista}">
		<br/>
		<div class="infoAltRem">
			<c:if test="${ not usuario.coordenadorLato }">
		    <html:img page="/img/alterar.gif" style="overflow: visible;"/>
		    : Alterar dados da Proposta
		    <html:img page="/img/delete.gif" style="overflow: visible;"/>
		    : Remover Proposta
		    </c:if>
		    <html:img page="/img/view.gif" style="overflow: visible;"/>
		    : Visualizar Proposta
		</div>
		<br>
	
		<table class="listagem">
		<caption>Propostas Cadastradas</caption>
			<thead>
				<tr>
		        <th>Curso</th>
		        <th>Coordenador</th>
		        <th>Situação da Proposta</th>
		        <th colspan="3">&nbsp;</th>
				</tr>
			</thead>
	
	        <tbody>
	        <c:forEach items="${lista}" var="curso" varStatus="status">
	        <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td> ${curso.descricao} </td>
				<td> ${curso.propostaCurso.coordenador.nome} </td>
		        <td> ${curso.propostaCurso.situacaoProposta.descricao} </td>
				<td width="15">
					
		        	<html:link action="/ensino/latosensu/criarCurso?dispatch=edit&id=${ curso.id }">
	                       <img src="<%= request.getContextPath() %>/img/alterar.gif" alt="Alterar dados do Curso" title="Alterar dados do Curso" border="0"/>
	                </html:link>
	                
		        </td>
		        <td width="15">
		        	<html:link action="/ensino/latosensu/criarCurso?dispatch=remove&id=${ curso.id }">
	                       <img src="<%= request.getContextPath() %>/img/delete.gif" alt="Remover Curso" title="Remover Curso" border="0"/>
	                </html:link>
		        </td>
		        <td width="15">
		        	<html:link action="/ensino/latosensu/criarCurso?dispatch=view&id=${ curso.id }">
	                       <img src="<%= request.getContextPath() %>/img/view.gif" alt="Visualizar Curso" title="Visualizar Curso" border="0"/>
	                </html:link>
		        </td>
			</tr>
		    </c:forEach>
		</table>
	
	</c:if>

</c:if>

<c:if test="${ param.renovar != null }">
<c:if test="${ not empty lista }">
	<br>
	<div class="infoAltRem">
		<html:img page="/img/avancar.gif"/> : Renovar Proposta de Curso
	</div>
</c:if>

<ufrn:table collection="${lista}" properties="nome"
headers="Nome"
title="Cursos" crud="false" links="src='${ctx}/img/avancar.gif',?id={id}&renovar=true&dispatch=edit"/>
</c:if>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
