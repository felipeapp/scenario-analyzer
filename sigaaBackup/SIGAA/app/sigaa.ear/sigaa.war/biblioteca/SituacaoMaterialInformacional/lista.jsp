<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


<f:view>
	
	<h2><ufrn:subSistema /> > Situações dos Materiais Informacionais </h2>
	
	<div class="descricaoOperacao"> 
    	<p> Abaixo estão listadas as situações que os materiais informacionais do acervo podem possuir. </p>
    	<br/>
    	<p> <strong> Uma situação indica o estado momentâneo do material, se ele está emprestado a um usuário ou disponível no acervo, entre outros. </strong> </p>
    	<p>Existem algumas situações que são fixas no sistema e não podem ser alteradas, as demais podem ser criadas e removidas livremente.</p>
    	<br/>
    	<p> Materiais que estão em uma situação que não é "visível", não são visualizados nas buscas do acervo utilizadas pelos usuários finais das bibliotecas.</p>
	</div>
	
	<h:form>

		<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
		
			<div class="infoAltRem" style="width:90%;">
				<h:graphicImage value="/img/adicionar.gif" />
				<h:commandLink action="#{situacaoMaterialInformacionalMBean.preCadastrar}" value="Nova Situação" />
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: 
				Alterar Situação
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
				Remover Situação
			</div>
			
		</ufrn:checkRole>

		<table class="listagem" style="width:90%">
			<caption>Lista de Situações (${situacaoMaterialInformacionalMBean.size})</caption>
			<thead>
			<tr>
				<th style="width: 70%;">Descrição</th>
				<th style="width: 20%; text-align: center;">Visível</th>
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
						<td>${situacao.descricao}<span style="font-style: italic;"> ( material não existe mais no acervo ) </span> </td> 
					</c:if>
					
					<c:if test="${! situacao.situacaoDeBaixa && ! situacao.situacaoEmprestado && ! situacao.situacaoDisponivel}">
						<td >${situacao.descricao}</td>
					</c:if>
					
					<td style="text-align: center; ${situacao.visivelPeloUsuario ? 'color:green;' : 'color:red;'}"> <ufrn:format type="simNao" valor="${situacao.visivelPeloUsuario}" /> </td>
					
						<td style="text-align: center;">
							<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
								<h:commandLink title="Alterar Situação" action="#{situacaoMaterialInformacionalMBean.preAtualizar}" rendered="#{situacao.editavel}">
									<f:param name="id" value="#{situacao.id}" />
									<h:graphicImage url="/img/alterar.gif" alt="Alterar Situação" />
								</h:commandLink>
							</ufrn:checkRole>
						</td>
						<td style="text-align: center;">
							<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
								<h:commandLink title="Remover Situação" action="#{situacaoMaterialInformacionalMBean.preRemover}" rendered="#{situacao.editavel}">
									<f:param name="id" value="#{situacao.id}" />
									<h:graphicImage url="/img/delete.gif" alt="Remover Situação" />
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