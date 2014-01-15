<%@include file="/ava/cabecalho.jsp"%>

<f:view>
	<%@include file="/ava/menu.jsp"%>
	
	<h:form id="form">
		<c:set var="rotulos" value="#{ rotuloTurmaBean.listagem }" />
	
		<fieldset>		
			<legend>R�tulos</legend>
			
			<div class="menu-botoes" style="text-align:center;width:210px;margin: 0 auto;">
				<ul class="menu-interno">
						<li class="botao-medio novoRotulo;">
							<h:commandLink action="#{ rotuloTurmaBean.novoRotuloTurma }" rendered="#{ rotuloTurmaBean.permiteCadastrarRotulo }">
								<h:outputText value="<p style='margin-left:55px;font-variant:small-caps;font-size:1.3em;font-weight:bold;'>Novo R�tulo</p>" escape="false" /> 
							</h:commandLink>
						</li>
				</ul>	
				<div style="clear:both;"></div>	
			</div>
			
			 
			<c:if test="${ empty rotulos }">
				<p class="empty-listing">Nenhum item foi encontrado.</p>
			</c:if>
			 
			<c:if test="${ not empty rotulos }">
				<table class="listing">
					<thead>
						<tr>
							<th>Descri��o</th>
							<th>Aula</th>
							<th>Vis�vel</th>
							<c:if test="${ turmaVirtual.docente  }">
								<th></th>
								<th></th>
							</c:if>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="r" items="#{ rotulos }" varStatus="loop">
							<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">	
								<td class="first">
									${ r.descricao }
								</td>
								
								<td>
									${ r.aula.descricao }
								</td>

								<td class="width150">
									<h:outputText value="#{ r.visivel ? 'Sim' : 'N�o' }" />
								</td>
	
								<%-- A��es para os r�tulos --%>
								<td class="icon">
									<h:commandLink id="idEditarMaterialRotulo" action="#{ rotuloTurmaBean.editar }" title="Editar R�tulo" styleClass="naoImprimir"
										rendered="#{ turmaVirtual.docente }">
											<h:graphicImage value="/ava/img/page_edit.png"/>
											<f:param name="id" value="#{ r.id }"/>
									</h:commandLink>
								</td>	
								<td class="icon">
									<h:commandLink id="idRemoverMaterialRotulo" action="#{ rotuloTurmaBean.inativar }" title="Remover R�tulo" 
										onclick="return(confirm('Deseja realmente excluir este r�tulo?'));" styleClass="naoImprimir"
										rendered="#{ turmaVirtual.docente }">
										<h:graphicImage value="/ava/img/bin.png"/>
										<f:param name="id" value="#{ r.id }"/>
									</h:commandLink>
								</td>
								
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>
			
		</fieldset>
	</h:form>
</f:view>
<%@include file="/ava/rodape.jsp"%>
