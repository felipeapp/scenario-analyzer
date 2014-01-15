<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="solicitacaoReposicaoProva"/>
<style>
	table.listagem tr td.periodo {
		background: #C4D2EB;
		padding: 3px;
		font-weight: bold;
	}
</style>

<f:view>
	<a4j:keepAlive beanName="relatorioAtividadeAP"></a4j:keepAlive>

	<h2> <ufrn:subSistema /> &gt; Relatório de Atividades </h2>
	
	<div class="descricaoOperacao">
		<p>
			<b> Caro Usuário, </b>
		</p> 	
		<p>Nesta tela é possível buscar as atividades por ano e período. Após buscar as atividades
		 desejadas basta seleciona-las clicando na seta para visualizar seus detalhes.</p>		
		<br/>
	</div>	
	
<h:form prependId="false">
	
	<table class="formulario" width="50%">
		<caption>FILTROS</caption>
		<tbody>
			<tr>
				<th width=35%" style="text-align:right;">Ano-Período:</th>
				<td>
					<h:inputText id="Ano" value="#{ relatorioAtividadeAP.ano }" onkeyup="return formatarInteiro(this);" size="4" maxlength="4"/>	- <h:inputText id="Período" value="#{ relatorioAtividadeAP.periodo }" size="1" maxlength="1" onkeyup="return formatarInteiro(this);"/>
				</td>
			</tr>
		</tbody>
		<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="Filtrar" action="#{relatorioAtividadeAP.filtrar}"/>
			</td>
		</tr>	
		</tfoot>	
	</table>
	<br/>
	
	<div class="infoAltRem" style="width:80%">
		<h:graphicImage value="/img/buscar.gif"style="overflow: visible;"/> : Visualizar Atividade
	</div>

		<c:if test="${not empty relatorioAtividadeAP.atividades}">
			<table class="listagem" style="width:80%">
			<caption class="listagem">Atividades de Atualização Pedagógica</caption>
			
			<thead>
			<tr>
				<th style="text-align:left;">Atividade</th>
				<th style="text-align:center;">Início</th>
				<th style="text-align:center;">Fim</th>
				<th></th>
			</tr>
			</thead>
					<c:forEach items="#{relatorioAtividadeAP.atividades}" var="item" varStatus="status">
						<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">

							<td><h:outputText value="#{item.nome}"/></td>
							<td><fmt:formatDate value="${item.inicio}" pattern="dd/MM/yyyy"/></td>
							<td><fmt:formatDate value="${item.fim}" pattern="dd/MM/yyyy"/></td> 
							<td align="right"> 
								<h:commandLink action="#{relatorioAtividadeAP.selecionar}" title="Visualizar Atividade"> 
									<h:graphicImage url="/img/buscar.gif" />
									<f:param name="id" value="#{item.id}"/>
								</h:commandLink>
							</td>
							
						</tr>
					</c:forEach>
			</table>
		</c:if>
		<br/>
						
		<c:if test="${empty relatorioAtividadeAP.atividades}">
			<table class="listagem" style="width:80%">
			<caption class="listagem">Atividades de Atualização Pedagógica</caption>
				<tr>
					<td>
						<div align="center" style="color:red">Não existe nenhuma atividade para os critérios de busca indicados.</div>
					</td>
				</tr>
			</table>
		</c:if>
		<br/>
	
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>