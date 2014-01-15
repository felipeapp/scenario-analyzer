<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> > Lista de Situações de Proposta </h2>
<f:view>
<h:form id="form">

	<div class="infoAltRem">
		
		<h:commandLink 	action="#{financiamentoCursoLatoSensuMBean.preCadastrar}">
				<h:graphicImage url="/img/adicionar.gif" /> Cadastrar Novo Tipo de Financiamento
		</h:commandLink><h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Financiamento 
		<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Financiamento 	
	</div>

	<table class="listagem">
	  <caption class="listagem">Listagem dos Financiamentos </caption>
			<thead>
					<tr>
						<td>Descrição</td>
						<td colspan="2"></td>
					</tr>
			</thead>
			<c:forEach items="#{financiamentoCursoLatoSensuMBean.allAtivos}" var="lista" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td>${lista.descricao}</td>
						<td width="2%">
							<h:commandLink title="Alterar" 
								action="#{financiamentoCursoLatoSensuMBean.atualizar}">
								<h:graphicImage url="/img/alterar.gif" />
								<f:param name="id" value="#{lista.id}"/>
							</h:commandLink>
						</td>
						<td width="2%">						
							<h:commandLink title="Remover" action="#{financiamentoCursoLatoSensuMBean.inativar}" onclick="#{confirmDelete}">
								<h:graphicImage url="/img/delete.gif"/>
								<f:param name="id" value="#{lista.id}"/>
							</h:commandLink>
						</td>
					</tr>
		    </c:forEach>							
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>