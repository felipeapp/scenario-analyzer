<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%> 

<f:view>
<a4j:keepAlive beanName="designacaoFuncaoProjetoMBean" />
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> > Designa��es Dadas ao Membro do Projeto</h2>
	
		<h:form id="form">
		
		<div class="infoAltRem">
			<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>: 
       		<h:commandLink title="Cadastrar Designa��o" value="Cadastrar Designa��o" 
       			action="#{designacaoFuncaoProjetoMBean.preCadastrar}"/>
			<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Designa��o
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Designa��o
		</div>
		
			<table class="listagem">
				<caption>Designa��es Encontradas (${ fn:length(designacaoFuncaoProjetoMBean.allAtivos) })</caption>
				
				<tr>
					<td>
						<thead>
							<th>Tipo Designa��o</th>
							<th>Membro Projeto</th>
							<th>Data Cadastro</th>
							<th></th>
							<th></th>
						</thead>
						
						<c:choose>
							<c:when test="${ not empty designacaoFuncaoProjetoMBean.allAtivos }">
								<c:forEach var="designacao" items="#{ designacaoFuncaoProjetoMBean.allAtivos }" varStatus="status">
									<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
										<td> ${ designacao.tipoDesignacao.denominacao } </td>	
										<td> ${ designacao.membroProjeto.pessoa.nome } </td>			
										<td> 
											<h:outputText value="#{ designacao.dataCadastro }">  
		  										<f:convertDateTime pattern="dd/MM/yyyy"/>  
											</h:outputText>  
										</td>
										<td width="20">
											<h:commandLink action="#{designacaoFuncaoProjetoMBean.atualizar}" >
												<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Alterar Designa��o"/>
												<f:param name="id" value="#{designacao.id}"/>
											</h:commandLink>
										</td>
										<td width="20">
											<h:commandLink action="#{designacaoFuncaoProjetoMBean.inativar}" onclick="#{confirmDelete}" >
												<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover Designa��o"/>
												<f:param name="id" value="#{designacao.id}"/>
											</h:commandLink>
										</td>
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<td colspan="5" style="color: red; text-align: center;">Nenhuma Designa��o foi encontrada.</td>
								</tr>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</table>
		</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>