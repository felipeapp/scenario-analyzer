<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp" %>

<h:form>
	<h2><ufrn:subSistema /> > Lista de Editais</h2>
		<h:outputText value="#{editalExtensao.create}"/>
		<center>
				<h:messages/>
				<div class="infoAltRem">
				    <c:if test="${acesso.extensao}">
           				 <h:graphicImage url="/img/adicionar.gif" />
                   		 <h:commandLink title="Cadastrar Novo Edital" value="Cadastrar Novo Edital" action="#{editalExtensao.preCadastrar}" style="border: 0;"/>
					</c:if>
				    <h:graphicImage value="/img/view.gif" 	style="overflow: visible;"/>: Visualizar Arquivo
				    <c:if test="${acesso.extensao}">
					    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
					    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
					    <h:graphicImage value="/img/seta.gif" 	style="overflow: visible;"/>: Enviar Arquivo
					</c:if>
				</div>
		</center>

	<table class="listagem">
			<caption class="listagem">Editais de Extensão</caption>
			<thead>
			<tr>
				<th> Descrição</th>
				<th> Início Submissão</th>
				<th> Fim Submissão</th>								
				<th> Valor Financiamento </th>
				<th> Nº Bolsas </th>
				<th></th>
				<c:if test="${acesso.extensao}">
					<th></th>										
					<th></th>
					<th></th>
				</c:if>
				
			</tr>
			</thead>

			<tbody>
				<c:forEach items="#{editalExtensao.allAtivos}" var="edital" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td> ${edital.descricao} </td>
						<td><fmt:formatDate value="${edital.inicioSubmissao}" 	pattern="dd/MM/yyyy"/> </td>			
						<td><fmt:formatDate value="${edital.fimSubmissao}" 		pattern="dd/MM/yyyy"/> </td>						
						<td><fmt:formatNumber currencySymbol="R$ " value="${edital.valorFinanciamento}" type="currency"/>  </td>
						<td> ${edital.numeroBolsas} </td>
			
							<td width="2%">
									<h:commandLink title="Ver Arquivo" action="#{editalMBean.viewArquivo}" style="border: 0;">
									         <f:param name="id" value="#{edital.edital.id}"/>
				                   			<h:graphicImage url="/img/view.gif" />
									</h:commandLink>
							</td>	
							<c:if test="${acesso.extensao}">
								<td width="2%">										
									<h:commandLink title="Alterar" action="#{editalExtensao.atualizar}" style="border: 0;">
									         <f:param name="id" value="#{edital.id}"/>
				                   			<h:graphicImage url="/img/alterar.gif" />
									</h:commandLink>
								</td>									
								<td width="2%">											
									<h:commandLink title="Remover" action="#{editalExtensao.preRemover}" style="border: 0;">
									         <f:param name="id" value="#{edital.id}"/>
				                   			<h:graphicImage url="/img/delete.gif" />
									</h:commandLink>
								</td>	
								<td width="2%">
									<c:if test="${not edital.associado}">
										<h:commandLink title="Enviar Arquivo" action="#{editalExtensao.populaEnviaArquivo}" style="border: 0;">
										         <f:param name="id" value="#{edital.id}"/>
					                   			<h:graphicImage url="/img/seta.gif" />
										</h:commandLink>
									</c:if>
								</td>	
							</c:if>
				</c:forEach>
			</tbody>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>