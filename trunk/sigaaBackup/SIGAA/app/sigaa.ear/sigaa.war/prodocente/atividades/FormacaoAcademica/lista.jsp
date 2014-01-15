<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	<h2><ufrn:subSistema /> > Forma��o Acad�mica</h2>
<%-- 
 O Cadastro foi tranferido para o SIGRH
	<h:form>
		<div class="infoAltRem" style="width: 100%">
			<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>: <h:commandLink action="#{formacaoAcademica.preCadastrar}" value="Cadastrar Nova Forma��o Acad�mica"></h:commandLink>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Forma��o
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Forma��o
		</div>
	</h:form>
--%>

<div class="descricaoOperacao">
	<h:form>
		<b>Caro Usu�rio, </b><br/><br/>	
		
		Para atualizar a forma��o voc� deve ir no Menu Servi�os -> Atualizar Forma��o Acad�mica. 
		<h:commandLink action="#{formacaoAcademica.iniciarCadastro}" value="Clique aqui para ir ao SIGRH"/>
	
	</h:form>
</div>

<c:set var="lista" value="${formacaoAcademica.allServidor}" />
<c:if test="${empty lista}">
	<br />
	<center>
	<span style="color:red;">Nenhuma Forma��o Acad�mica Encontrada.</span>
	</center>
</c:if>
<c:if test="${not empty lista}">

	<table class=listagem>
		<caption class="listagem">Forma��o Acad�mica</caption>

		<thead>
			<tr>
				<td> Titulo </td>
				<td> Forma��o </td>
				<td> Grau </td>
				<td> Orientador </td>
				<td> Institui��o </td>
			</tr>
		</thead>

		<c:forEach items="${formacaoAcademica.allServidor}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td width="30%">${item.titulo}</td>
				<td>${item.formacao} </td>
				<td>${item.grau}</td>
				<td width="20%">${item.orientador}</td>
				<td>${item.instituicao}</td>
				<%-- O Cadastro foi tranferido para o SIGRH
				<h:form>
					<td width="20"><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/alterar.gif" value="Alterar"
						action="#{formacaoAcademica.atualizar}" /></td>
				</h:form>
				<h:form>
					<td width=25><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/delete.gif" alt="Remover"
						action="#{formacaoAcademica.remover}" onclick="#{confirmDelete}"/></td>
				</h:form>
				 --%>
			</tr>
		</c:forEach>
	</table>
</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
