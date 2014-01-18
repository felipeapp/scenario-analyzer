<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
  <h:form>
	<h2 class="tituloPagina"> <ufrn:subSistema/> > Critérios de Renovação de Bolsa </h2>

	<div class="infoAltRem">
	    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
	</div>

	<table class="listagem" width="100">
	
		<caption> Lista dos Critérios Cadastrados </caption>
		<thead>
			<tr>
				<th>Bolsa Auxilio</th>
				<th>Situação Bolsa Auxilio</th>
				<th>Tipo Renovação Bolsa Auxilio</th>
				<th width="25"></th>
				<th width="25"></th>
			</tr>
		</thead>

		<c:forEach items="#{criterioSolicitacaoRenovacaoMBean.all}" var="criterio" varStatus="loop">
			<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
			
				<td>${criterio.tipoBolsaAuxilio.denominacao}</td>
				<td>${criterio.situacaoBolsa.denominacao}</td>
				<td>${criterio.tipoRenovacao.descricao}</td>

				<td>
					<h:commandLink action="#{ criterioSolicitacaoRenovacaoMBean.atualizar }">
							<f:param name="id" value="#{ criterio.id }"/>
							<h:graphicImage value="/img/alterar.gif" title="Alterar"/>
					</h:commandLink> 
				</td>
				
				<td>
					<h:commandLink action="#{ criterioSolicitacaoRenovacaoMBean.inativar }" 
							onclick="return(confirm('Deseja realmente REMOVER este item?'));">
							<f:param name="id" value="#{ criterio.id }"/>
							<h:graphicImage value="/img/delete.gif" title="Remover" />
					</h:commandLink>  
				</td> 
				
			</tr>
		</c:forEach>
		
	</table>

  </h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>