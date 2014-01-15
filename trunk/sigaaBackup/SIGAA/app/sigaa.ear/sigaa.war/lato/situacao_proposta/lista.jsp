<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> > Lista de Situações de Proposta </h2>
<f:view>
<h:form id="form">

	<div class="infoAltRem">
		
		<h:commandLink 	action="#{situacaoPropostaMBean.preCadastrar}">
				<h:graphicImage url="/img/adicionar.gif" /> Cadastrar Nova Situação Proposta
		</h:commandLink><h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Situação de Proposta 
		<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Situação de Proposta 	
	</div>

	<table class="listagem">
	  <caption class="listagem">Listagem das Situações de Proposta </caption>
			<thead>
					<tr>
						<td>Descrição</td>
						<td>Válida</td>
						<td colspan="2"></td>
					</tr>
			</thead>
			<c:forEach items="#{situacaoPropostaMBean.allAtivos}" var="lista" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td>${lista.descricao}</td>
						<td> <ufrn:format type="SimNao" valor="${lista.valida}"/></td>
						<td width="2%">
							<h:commandLink title="Alterar" 
								action="#{situacaoPropostaMBean.atualizar}">
								<h:graphicImage url="/img/alterar.gif" />
								<f:param name="id" value="#{lista.id}"/>
							</h:commandLink>
						</td>
						<td width="2%">						
							<h:commandLink title="Remover" action="#{situacaoPropostaMBean.inativar}" onclick="#{confirmDelete}">
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