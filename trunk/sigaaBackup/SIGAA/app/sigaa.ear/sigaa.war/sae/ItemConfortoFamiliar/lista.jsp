<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages/>
	<h2 class="tituloPagina"> <ufrn:subSistema/> > Lista dos Itens de conforto familiar</h2>


	<div class="infoAltRem">
	    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Visualizar
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
	</div>

	<table class="listagem" width="100">
	
	<caption> Itens conforto familiar Cadastrados </caption>
	<thead>
		<tr>
			<th>Descrição</th>
			<th width="25"></th>
			<th width="25"></th>
		</tr>
	</thead>

		<tbody>
			<c:forEach items="#{itensConfortoFamiliarMBean.listaItensConfortoFamiliar}" var="itemConforto" varStatus="loop">
			
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				
					<td>${itemConforto.item}</td>
					
					<td>
						<h:form>
								<h:commandLink action="#{ itensConfortoFamiliarMBean.alterar }">
										<f:param name="id" value="#{ itemConforto.id }"/>
										<h:graphicImage value="/img/alterar.gif" title="Alterar" />
								</h:commandLink> 
						</h:form>
					</td>
					
					<td>
							<h:form>
									<h:commandLink action="#{ itensConfortoFamiliarMBean.remover }" 
											onclick="return(confirm('Deseja realmente REMOVER este item?'));">
											<f:param name="id" value="#{ itemConforto.id }"/>
											<h:graphicImage value="/img/delete.gif" title="Remover" />
									</h:commandLink>  
							</h:form>
						
					</td> 
				</tr>
				
			</c:forEach>
		</tbody>
		
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>