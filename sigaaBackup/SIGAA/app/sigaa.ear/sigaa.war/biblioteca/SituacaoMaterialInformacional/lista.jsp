<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


<f:view>
	
	<h2><ufrn:subSistema /> > Situa��es dos Materiais Informacionais </h2>
	
	<div class="descricaoOperacao"> 
    	<p> Abaixo est�o listadas as situa��es que os materiais informacionais do acervo podem possuir. </p>
    	<br/>
    	<p> <strong> Uma situa��o indica o estado moment�neo do material, se ele est� emprestado a um usu�rio ou dispon�vel no acervo, entre outros. </strong> </p>
    	<p>Existem algumas situa��es que s�o fixas no sistema e n�o podem ser alteradas, as demais podem ser criadas e removidas livremente.</p>
    	<br/>
    	<p> Materiais que est�o em uma situa��o que n�o � "vis�vel", n�o s�o visualizados nas buscas do acervo utilizadas pelos usu�rios finais das bibliotecas.</p>
	</div>
	
	<h:form>

		<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
		
			<div class="infoAltRem" style="width:90%;">
				<h:graphicImage value="/img/adicionar.gif" />
				<h:commandLink action="#{situacaoMaterialInformacionalMBean.preCadastrar}" value="Nova Situa��o" />
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: 
				Alterar Situa��o
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
				Remover Situa��o
			</div>
			
		</ufrn:checkRole>

		<table class="listagem" style="width:90%">
			<caption>Lista de Situa��es (${situacaoMaterialInformacionalMBean.size})</caption>
			<thead>
			<tr>
				<th style="width: 70%;">Descri��o</th>
				<th style="width: 20%; text-align: center;">Vis�vel</th>
				<th style="width: 5%;"></th>
				<th style="width: 5%;"></th>
			</tr>
			</thead>
			
			<c:forEach items="#{situacaoMaterialInformacionalMBean.all}" var="situacao" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					
					<c:if test="${situacao.situacaoDisponivel}">
						<td  style="color: green; ">${situacao.descricao} 
					</c:if> 
					
					<c:if test="${situacao.situacaoEmprestado}">
						<td style="color: red;">${situacao.descricao}
					</c:if>
					
					<c:if test="${situacao.situacaoDeBaixa}">
						<td>${situacao.descricao}<span style="font-style: italic;"> ( material n�o existe mais no acervo ) </span> </td> 
					</c:if>
					
					<c:if test="${! situacao.situacaoDeBaixa && ! situacao.situacaoEmprestado && ! situacao.situacaoDisponivel}">
						<td >${situacao.descricao}</td>
					</c:if>
					
					<td style="text-align: center; ${situacao.visivelPeloUsuario ? 'color:green;' : 'color:red;'}"> <ufrn:format type="simNao" valor="${situacao.visivelPeloUsuario}" /> </td>
					
						<td style="text-align: center;">
							<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
								<h:commandLink title="Alterar Situa��o" action="#{situacaoMaterialInformacionalMBean.preAtualizar}" rendered="#{situacao.editavel}">
									<f:param name="id" value="#{situacao.id}" />
									<h:graphicImage url="/img/alterar.gif" alt="Alterar Situa��o" />
								</h:commandLink>
							</ufrn:checkRole>
						</td>
						<td style="text-align: center;">
							<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
								<h:commandLink title="Remover Situa��o" action="#{situacaoMaterialInformacionalMBean.preRemover}" rendered="#{situacao.editavel}">
									<f:param name="id" value="#{situacao.id}" />
									<h:graphicImage url="/img/delete.gif" alt="Remover Situa��o" />
								</h:commandLink>
							</ufrn:checkRole>
						</td>
				</tr>
			</c:forEach>
			
			
			<tfoot>
				<tr>
					<td style="text-align: center;" colspan="4">
						<h:commandButton value="Cancelar" action="#{situacaoMaterialInformacionalMBean.cancelar}"  immediate="true"  />
					</td>
				</tr>
			</tfoot>
			
		</table>

	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>