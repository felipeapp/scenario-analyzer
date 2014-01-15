<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Situação de Matrícula</h2>
	<h:outputText value="#{situacaoMatricula.create}" />
	
	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{situacaoMatricula.preCadastrar}" value="Cadastrar"/>
				</h:form>
			</div>
	</center>
		
	<table class=formulario width="50%" >
		<tr>
			<caption class="listagem">Lista de Situações de Matrícula</caption>
			<thead>
				<td><b>Descrição</b></td>
				<td><b>Ativo</b></td>
				<td><b>Matricula Valida no Semestre</b></td>
			</thead>
		</tr>
		<c:forEach items="${situacaoMatricula.allPaginado}" var="item" varStatus="loop">
			<tr class="${loop.index % 2 == 0 ? 'linhaPar':'linhaImpar' }">
				<td>${item.descricao}</td>
				<td><ufrn:format type="SimNao" valor="${item.ativo}" /></td>
				<td><ufrn:format type="SimNao" valor="${item.matriculaValidaNoSemestre}" /></td>
			</tr>
		</c:forEach>
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