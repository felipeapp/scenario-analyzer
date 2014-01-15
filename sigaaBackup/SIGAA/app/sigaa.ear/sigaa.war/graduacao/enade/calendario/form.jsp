<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Calendário de Aplicação do ENADE</h2>
	<h:form id="form">
	<a4j:keepAlive beanName="calendarioEnadeMBean"></a4j:keepAlive>
	
		<div class="descricaoOperacao">
			<p>Caro Usuário,</p>
			<p> 
				Digite o nome de um curso a ser adicionado e o selecione na lista de sugestões. 
				Pelo menos um curso que será avaliado no ENADE deverá ser adicionado.
			</p>
		</div>
		<table class="formulario" width="80%">
			<caption>Defina os Cursos para o ENADE</caption>
			<tr>
				<th class="rotulo" width="15%">Ano:</th>
				<td>
					<h:outputText value="#{ calendarioEnadeMBean.obj.ano }"/>
				</td>
			</tr>
			<tr>
				<th class="rotulo">Tipo:</th>
				<td>
					<h:outputText value="#{ calendarioEnadeMBean.obj.tipoEnade }"/>
				</td>
			</tr>
			<tr>
				<th>Data da Prova:</th>
				<td>
					<t:inputCalendar value="#{calendarioEnadeMBean.obj.dataProva}" id="dataProva" size="10" maxlength="10" 
					    onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
					    renderAsPopup="true" renderPopupButtonAsImage="true" >
					      <f:converter converterId="convertData"/>
					</t:inputCalendar> 
				</td>
			</tr>
			<tr>
				<th class="required">Curso:</th>
				<td>
					<h:inputHidden id="idCurso"  value="#{ calendarioEnadeMBean.idMatriz }" />
					<h:inputText   id="nomeCurso" value="#{ calendarioEnadeMBean.curso.nome }" size="70" maxlength="120" style="width: 500px;" />
					<rich:suggestionbox
							id="suggestionNomeCurso"
							for="form:nomeCurso"
							var="_matriz"
							suggestionAction="#{ calendarioEnadeMBean.autocompleteNomeCurso }"
							nothingLabel="Nenhum curso encontrado"
							width="500" height="250" minChars="5" >
						<h:column>
							<h:outputText value="#{ _matriz.curso.descricao }"/> - 
							<h:outputText value="#{ _matriz.grauAcademico.descricao }"/> 
							( <h:outputText value="#{ _matriz.curso.municipio }" /> )
						</h:column>
						<a4j:support event="onselect" reRender="form:idCurso" >
							<f:setPropertyActionListener
								value="#{ _matriz.id }" target="#{ calendarioEnadeMBean.idMatriz }"/>
						</a4j:support>
					</rich:suggestionbox>
					<h:commandButton action="#{calendarioEnadeMBean.adicionarCurso }"  value="Adicionar Curso" id="adicionarCurso"/>
				</td>
			</tr>
			<c:if test="${ not empty calendarioEnadeMBean.obj.cursosGrauAcademico }">
				<tr>
					<td colspan="2" class="subFormulario"> Cursos Adicionados
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<div class="infoAltRem" style="width: 100%">
						 	<img src="/sigaa/img/delete.gif" style="overflow: visible;" />: Remover Curso
					 	</div>
						<table class="listagem">
							<thead>
								<tr>
									<th>Nome</th>
									<th width="5%"></th>
								</tr>
							</thead>
							<c:forEach items="#{calendarioEnadeMBean.obj.cursosGrauAcademico}" var="item" varStatus="loop" >
								<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
									<td>
										<h:outputText value="#{ item.curso.descricao }"/> - 
										<h:outputText value="#{ item.grauAcademico.descricao }"/> 
										( <h:outputText value="#{ item.curso.municipio }" /> )
									</td>
									<td>
										<h:commandLink action="#{ calendarioEnadeMBean.removerCurso }" onclick="#{confirm}" id="deleteLink">
											<f:verbatim>
												<img src="/sigaa/img/delete.gif" alt="Remover Curso" title="Remover Curso" />
											</f:verbatim>
											<f:param name="id" value="#{ item.id }" />
										</h:commandLink>
									</td>
								</tr>
							</c:forEach>
						</table>
					</td>
				</tr>
			</c:if>
				<tr>
				
			<tfoot>
				<tr>
					<td colspan="2">
						 <h:commandButton value="#{calendarioEnadeMBean.confirmButton}" action="#{calendarioEnadeMBean.cadastrar}" id="btnCadastrar"  /> 
						 <h:commandButton value="<< Voltar" action="#{calendarioEnadeMBean.voltar}" id="btnVoltar"  />
						 <h:commandButton value="Cancelar" action="#{calendarioEnadeMBean.cancelar}" onclick="#{confirm}" immediate="true" id="btnCancelar" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br/>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>