<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Especialização de Turmas de Entrada</h2>
	<h:outputText value="#{especializacaoTurma.create}" />

	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>:
					<h:commandLink action="#{especializacaoTurma.preCadastrar}" value="Cadastrar"/>
					<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
			        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
				</h:form>
			</div>
	</center>

	<table class=listagem>
		<caption class="listagem">Lista de Especializações (${ fn:length(especializacaoTurma.allPaginado) })</caption>
		<thead>
			<td>Descrição</td>
			<td></td>
			<td></td>
		</thead>
		<c:forEach items="${especializacaoTurma.allPaginado}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>
				<td width=20>
					<h:form>
					<input type="hidden" value="${item.id}" name="id" /> <h:commandButton
							image="/img/alterar.gif" value="Alterar"
							action="#{especializacaoTurma.atualizar}" />
					</h:form>
				</td>
				<td width=25>
					<h:form>
						<input type="hidden" value="${item.id}" name="id" /> <h:commandButton
								image="/img/delete.gif" alt="Remover"
							action="#{especializacaoTurma.preRemover}" />
					</h:form>
				</td>
			</tr>
		</c:forEach>
	</table>

	<center>
	<h:form>
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
	</h:form>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
