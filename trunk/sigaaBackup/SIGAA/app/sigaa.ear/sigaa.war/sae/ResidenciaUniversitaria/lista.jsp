<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages/>
	<h2 class="tituloPagina"> <ufrn:subSistema/> > Lista de Residências</h2>

	<h:outputText value="#{itensConfortoFamiliarMBean.create}"/>

	<div class="infoAltRem">
	    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
	</div>

	<table class="listagem" width="100">
	
	<caption> Lista de Residências </caption>
	<thead>
		<tr>
			<th>Descrição</th>
			<th>Sexo</th>
			<th width="25"></th>
			<th width="25"></th>
		</tr>
	</thead>

		<tbody>
			<c:forEach items="#{residenciaUniversitariaMBean.listaResidencias}" var="residencia" varStatus="loop">
			
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				
					<td>${residencia.localizacao}</td>
					<td>${residencia.sexo}</td>

					<td>
						<h:form>
								<h:commandLink action="#{ residenciaUniversitariaMBean.alterar }">
										<f:param name="id" value="#{ residencia.id }"/>
										<h:graphicImage value="/img/alterar.gif" title="Alterar"/>
								</h:commandLink> 
						</h:form>
					</td>
					
					<td>
							<h:form>
									<h:commandLink action="#{ residenciaUniversitariaMBean.remover }" 
											onclick="return(confirm('Deseja realmente REMOVER este item?'));">
											<f:param name="id" value="#{ residencia.id }"/>
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