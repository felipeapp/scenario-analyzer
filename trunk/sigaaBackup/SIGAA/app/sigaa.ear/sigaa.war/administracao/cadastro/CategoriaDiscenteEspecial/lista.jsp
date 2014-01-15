<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2> <ufrn:subSistema /> > Categoria de Discente Especial</h2>

	<h:outputText value="#{categoriaDiscenteEspecial.create}" />
	
	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>
					<h:commandLink action="#{categoriaDiscenteEspecial.preCadastrar}" value="Cadastrar"/>
					<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/> :Alterar
					<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/> :Remover
				</h:form>
			</div>
	</center>
	
	<table class=listagem>
		<caption>Lista de Categorias de Discente Especial</caption>
		<thead>
			<tr>
			 	<td width="30%" style="text-align: center;">Descrição</td>
				<td align="center" >Número Máximo de Disciplinas</td>
				<td align="center" >Número Máximo de Períodos</td>
				<td align="center">Permite Re-matrícula</td>
				<td align="center">Processa Matrícula</td>
				<td align="center"  width="10%" >Processa Re-matrícula</td>
				<td align="center"  width="10%">Chefe analisa a solicitação de matrícula</td>
				<td colspan="2"></td>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${categoriaDiscenteEspecial.allPaginado}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td width="8%" >${item.descricao}</td>
					<td align="center">${empty item.maxDisciplinas?'0':item.maxDisciplinas}</td>
	 				<td align="center">${empty item.maxPeriodos?'0':item.maxPeriodos}</td>
	 				<td align="center"><ufrn:format type="SimNao" valor="${item.permiteRematricula}" /></td>
					<td align="center"><ufrn:format type="SimNao" valor="${item.processaMatricula}" /></td>
					<td align="center"><ufrn:format type="SimNao" valor="${item.processaRematricula}" /></td>
					<td align="center"><ufrn:format type="SimNao" valor="${item.chefeAnalisaSolicitacao}" /></td>	
					<td width=20>
						<h:form><input type="hidden" value="${item.id}" name="id" /> <h:commandButton
							image="/img/alterar.gif" value="Alterar" alt="Alterar" title="Alterar"
							action="#{categoriaDiscenteEspecial.atualizar}" /></h:form>
					</td>
					<td width=25>
						<h:form><input type="hidden" value="${item.id}" name="id" /> <h:commandButton
							image="/img/delete.gif"  alt="Remover" title="Remover"
							action="#{categoriaDiscenteEspecial.remover}" onclick="#{confirmDelete}"/></h:form>
					</td>
				 </tr>
			</c:forEach>
		</tbody>
	</table>

<center>
	<h:form>
		<h:commandButton image="/img/voltar.gif" actionListener="#{paginacao.previousPage}" rendered="#{paginacao.paginaAtual > 0 }"/>
 		<h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" onchange="submit()" immediate="true">
			<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
		</h:selectOneMenu>
 		<h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}"  rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}"/>
		<br/><br/>
 		<em><h:outputText value="#{paginacao.totalRegistros }"/> Registro(s) Encontrado(s)</em>
	</h:form>
</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>