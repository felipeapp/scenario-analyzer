<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Seleção de Monitores</h2>
			
			<div class="descricaoOperacao">
				<b>Atenção: </b>
				Selecione um projeto de monitoria para listar as provas de seleção cadastradas
			</div>

			
		<h:form id="form">
			<br/>
			
				<center>
			  	<table class="formulario" width="70%">
					<caption>Busca por Projetos com Provas Seletivas Cadastradas</caption>
					
					<tbody>
						<tr>
							<th width="20%" class="rotulo"><b>Ano do Projeto:</b></th>
							<td align="left"><h:inputText value="#{validaSelecaoMonitor.ano}" title="Ano" size="5" id="ano" maxlength="4" onkeyup="return formatarInteiro(this)" /></td>
                        </tr>
                        <tr> 
                            <th width="20%" class="rotulo"><b>Título do Projeto:</b></th>
                            <td align="left"><h:inputText value="#{validaSelecaoMonitor.titulo}" style="width:90%" title="Título" id="titulo" maxlength="250"/></td>
                        </tr>
                        <tr> 
							<th width="20%" class="rotulo"><b>Departamento:</b></th>
							<td align="left">
								<h:selectOneMenu id="centro" value="#{validaSelecaoMonitor.idCentro}" style="width:90%">
									<f:selectItem itemValue="" itemLabel="-- SELECIONE --"  />
									<f:selectItems value="#{unidade.centrosEspecificasEscolas}"/>
								</h:selectOneMenu>
							</td>
						</tr>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="2">
								<h:commandButton value="Buscar" action="#{ validaSelecaoMonitor.filtrar }" title="Buscar" id="cmdFiltrar" />
								<h:commandButton value="Cancelar" action="#{validaSelecaoMonitor.cancelar}" id="cmdCancelar" onclick="#{confirm}"/>
							</td>
						</tr>
					</tfoot>					
				 </table>
				</center>
		
			<br/>
			
			<div class="infoAltRem">
			    <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>: Listar Provas Seletivas			        
			</div>
			
			<br/>
			
			<c:set value="#{validaSelecaoMonitor.projetos}" var="projetos" />
			
			<table class="listagem">
			    <caption>Lista de Projetos com Provas não validadas (${fn:length(projetos)})</caption>
			
			      <thead>
			      	<tr>
			      		<th>Ano</th>
			        	<th>Título</th>
			        	<th>&nbsp;</th>
			        </tr>
			 	</thead>
			        
				<c:if test="${empty projetos}">
			        <tbody>
	                    <tr> <td colspan="5" align="center"> <font color="red">Não há provas de seleção pendentes de validação.</font> </td></tr>
					</tbody>		
				</c:if>

				<c:if test="${not empty projetos}">
			
			        <tbody>
			        	<c:set var="unidadeProjeto" value=""/>
				        <c:forEach items="#{projetos}" var="projeto" varStatus="status">
										<c:if test="${ unidadeProjeto != projeto.unidade.id }">
											<tr>
												<td colspan="4" style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
													<c:set var="unidadeProjeto" value="${ projeto.unidade.id }"/>
													${ projeto.unidade.nome } / ${ projeto.unidade.sigla }
												</td>
											</tr>
										</c:if>
				
						               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">				
											<td> ${projeto.ano}</td>
					                    	<td> ${projeto.titulo}
						                     	<br/><i><h:outputText value="Coordenador(a): #{projeto.coordenacao.pessoa.nome}" rendered="#{not empty projeto.coordenacao}"/></i>
					                    	</td>
											<td>
												<h:commandLink  title="Listar Provas Seletivas" action="#{validaSelecaoMonitor.selecionarProjeto}" style="border: 0;" id="cmdListarProvas">
													<f:param name="id" value="#{projeto.id}"/>				    	
													<h:graphicImage url="/img/listar.gif"    />
												</h:commandLink>
											</td>
					              	  </tr>
				        </c:forEach>
					</tbody>
				</c:if>
			</table>
	</h:form>


</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>