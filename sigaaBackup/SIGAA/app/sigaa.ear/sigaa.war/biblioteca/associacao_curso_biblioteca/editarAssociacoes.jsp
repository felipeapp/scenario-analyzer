<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<a4j:keepAlive beanName="associacaoCursoBibliotecaMBean" />
	
	<c:set var="mbean" value="#{ associacaoCursoBibliotecaMBean }" />
	<c:set var="podeAlterar" value="${ mbean.usuarioTemPermissaoDeAlteracao }" />
	<c:set var="width" value="70%" />

	<h2><ufrn:subSistema /> &gt; Cursos Associados </h2>
	
	<h:form id="form" >
	
		<c:if test="${ podeAlterar }">
			<div class="descricaoOperacao">
				<p>Abaixo estão listados os curso associados à biblioteca <strong> ${associacaoCursoBibliotecaMBean.biblioteca.descricaoCompleta} </strong> </p>
				<br/>
				<p>Esses cursos são os cursos cujos alunos vão ser atendidos pela biblioteca selecionada para os serviços prestados pelo sistema.</p>
			</div>
		</c:if>
		
		
		<c:if test="${ not empty mbean.cursosAssociados and podeAlterar }">
			<div class="infoAltRem" style="width: ${width};">
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
				Remover curso
			</div>
		</c:if>
		
		<table class="formulario" width="${width}" >
		
			<caption><h:outputText value="#{associacaoCursoBibliotecaMBean.biblioteca.descricaoCompleta}"/></caption>
			
			<tbody>
				<c:if test="${ podeAlterar }">
				<tr>
					<td>
						<table class="subFormulario" width="100%">
							<tbody>
								<tr>
									<th style="width: 100px; text-align: right;" class="obrigatorio">Curso:</th>
									<td>
										<h:inputHidden id="idCurso"  value="#{ associacaoCursoBibliotecaMBean.curso.id }" />
										<h:inputText   id="nomeCurso" value="#{ associacaoCursoBibliotecaMBean.curso.nome }" size="70" style="width: 500px;" />
										<rich:suggestionbox
												id="suggestionNomeCurso"
												for="form:nomeCurso"
												var="_curso"
												suggestionAction="#{ curso.autocompleteNomeGeralCursos }"
												nothingLabel="Nenhum curso encontrado"
												width="600" height="400" minChars="5" >
											
											<h:column>
												<h:outputText value="#{ _curso.descricaoCompleta }"/>
												&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp ( <h:outputText value="#{ _curso.nivelDescricao }"/> )
											</h:column>
											
											<a4j:support event="onselect" reRender="form:idCurso" >
												<f:setPropertyActionListener
													value="#{ _curso.id }" target="#{ associacaoCursoBibliotecaMBean.curso.id }"/>
											</a4j:support>
											
										</rich:suggestionbox>
									</td>
								</tr>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="2">
										<h:commandButton value="Adicionar Curso" action="#{ mbean.cadastrar }" id="adicionar" />
									</td>
								</tr>
							</tfoot>
						</table>
					</td>
				</tr>
				</c:if>
				<tr>
					<td>
						<table id="tableDadosPesquisa" class="subFormulario" style="border: 1px solid #cacaca" width="100%">
							<caption>Cursos associados</caption>
							<%-- Lista de cursos associados --%>
							<c:choose>
								<c:when test="${ empty mbean.cursosAssociados }">
									<td style="text-align: center;" colspan="2">Nenhum curso está associado a essa biblioteca</td>
								</c:when>
								<c:otherwise>
									<c:forEach var="c" items="#{ mbean.cursosAssociados }" varStatus="loop" >
										<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }" >
											<td colspan="${ podeAlterar? '1' : '2'}"> ${ c.descricaoCompleta } &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp ( ${c.nivelDescricao} ) </td>
											<c:if test="${ podeAlterar }">
												<td align="right">
													<h:commandLink
															action="#{ mbean.removerPorId }"
															title="Remover curso"
															onclick="return confirm('Tem certeza de que deseja remover esse curso?');"
															id="remover" >
														<f:param name="idAssociacaoCursoBiblioteca" value="#{c.id}" />
														<h:graphicImage url="/img/garbage.png" alt="Remover" />
													</h:commandLink>
												</td>
											</c:if>
										</tr>
									</c:forEach>
								</c:otherwise>
							</c:choose>
						</table>
					</td>
				</tr>
			</tbody>
			
			<tfoot>
				<tr>
					<td style="text-align: center;" colspan="2">
						<h:commandButton value="Cancelar"  action="#{ mbean.voltar }" id="cancelar" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		
		</table>
		
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
		
	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>