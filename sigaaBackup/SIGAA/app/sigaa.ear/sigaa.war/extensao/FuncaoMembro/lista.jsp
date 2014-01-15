<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema /> > Funções do Membros da Equipe</h2>

<h:outputText value="#{funcaoMembroEquipe.create}"/>

<center>
		<h:messages/>
		<div class="infoAltRem">
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar dados da Função de Membro da Equipe
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Função de Membro da Equipe<br/>
		</div>
</center>


<h:form>
<table class=listagem>
		<caption class="listagem"> Lista de Funções dos Membros das Equipes</caption>
		<thead>
		<tr>
			<th> Descrição </th>
			<th> Escopo </th>
			<th style="text-align: center;"> Pesquisa </th>
			<th style="text-align: center;"> Extensão </th>
			<th style="text-align: center;"> Ações Integradas </th>
			<th style="text-align: center;"> Monitoria </th>
			<th></th>
		</tr>
		</thead>
		
		<c:forEach items="#{funcaoMembroEquipe.all}" var="item" varStatus="status">
		<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${item.descricao}</td>
					<td>${item.scopoString}</td>
					<td style="text-align: center;"> <ufrn:format type="SimNao" valor="${item.pesquisa}" /> </td>
					<td style="text-align: center;"> <ufrn:format type="SimNao" valor="${item.extensao}" /> </td>
					<td style="text-align: center;"> <ufrn:format type="SimNao" valor="${item.integrados}" /> </td>
					<td style="text-align: center;"> <ufrn:format type="SimNao" valor="${item.ensino}" /> </td>
					<td  width="5%">
							<h:commandLink title="Alterar" action="#{funcaoMembroEquipe.atualizar}" style="border: 0;">
							         <f:param name="id" value="#{item.id}"/>
		                   			<h:graphicImage url="/img/alterar.gif" />
							</h:commandLink>
							
							<h:commandLink title="Remover" action="#{funcaoMembroEquipe.preRemover}" style="border: 0;">
							         <f:param name="id" value="#{item.id}"/>
		                   			<h:graphicImage url="/img/delete.gif" />
							</h:commandLink>
					</td>
		</tr>
		</c:forEach>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>